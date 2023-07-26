resource "azurerm_service_plan" "bbr5plan" {
  name                = terraform.workspace == "prod" ? "bbr5-plan" : "bbr5-plan-${terraform.workspace}"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
  os_type             = "Linux"
  sku_name            = "B1"
}
resource "azurerm_linux_web_app" "bbr5" {
  name                = terraform.workspace == "prod" ? "bbr5" : "bbr5-${terraform.workspace}"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_service_plan.bbr5plan.location
  service_plan_id     = azurerm_service_plan.bbr5plan.id

  site_config {
    application_stack {
      docker_image_name   = "brassbandresults/bbr5:${var.docker_image_tag}"
      docker_registry_url = "http://ghcr.io"
    }
  }

  logs {
    application_logs {
      file_system_level = "Verbose"
    }
  }

  app_settings = {
    WEBSITES_PORT                     = "8080"
    BBR_SMTP_SERVER_USERNAME          = var.smtp_username
    BBR_SMTP_SERVER_PASSWORD          = var.smtp_password
    BBR_SMTP_SERVER_HOST              = var.smtp_hostname
    BBR_DATABASE_URL                  = "jdbc:sqlserver://${azurerm_mssql_server.this.fully_qualified_domain_name};database=${azurerm_mssql_database.bbr.name}"
    BBR_DATABASE_USERNAME             = random_password.mssql_username.result
    BBR_DATABASE_PASSWORD             = random_password.mssql_password.result
    BBR_STATIC_FILES_HOST             = azurerm_static_site_custom_domain.static.domain_name
    BBR_WEB_SITE_PREFIX               = terraform.workspace == "prod" ? "bbr5" : "bbr5-${terraform.workspace}"
    BBR_STRIPE_PUBLIC_BUY_BUTTON      = var.stripe_public_buy_button
    BBR_STRIPE_PUBLIC_PUBLISHABLE_KEY = var.stripe_public_publishable_key
    BBR_STRIPE_PRIVATE_API_KEY        = var.stripe_private_api_key
  }
}

resource "azurerm_app_service_custom_hostname_binding" "bbr5" {
  hostname            = terraform.workspace == "prod" ? "bbr5.brassbandresults.co.uk" : "bbr5-${terraform.workspace}.brassbandresults.co.uk"
  app_service_name    = azurerm_linux_web_app.bbr5.name
  resource_group_name = azurerm_resource_group.this.name
}

resource "azurerm_app_service_managed_certificate" "bbr5cert" {
  depends_on                 = [cloudflare_record.app_service]
  custom_hostname_binding_id = azurerm_app_service_custom_hostname_binding.bbr5.id
}

resource "azurerm_app_service_certificate_binding" "bbr5cert" {
  hostname_binding_id = azurerm_app_service_custom_hostname_binding.bbr5.id
  certificate_id      = azurerm_app_service_managed_certificate.bbr5cert.id
  ssl_state           = "SniEnabled"
}
