resource "azurerm_service_plan" "bbr5plan" {
  name                = "bbr5-plan"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
  os_type             = "Linux"
  sku_name            = "B1"
}
resource "azurerm_linux_web_app" "bbr5" {
  name                = "bbr5"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_service_plan.bbr5plan.location
  service_plan_id     = azurerm_service_plan.bbr5plan.id

  site_config {}

  application_stack {
    docker_image_name   = "brassbandresults/bbr5:pr-5"
    docker_registry_url = "http://ghcr.io"
  }

  app_settings = {
    BBR_SMTP_SERVER_USERNAME = "${var.smtp_username}"
    BBR_SMTP_SERVER_PASSWORD = "${var.smtp_password}"
    BBR_SMTP_SERVER_HOST     = "${var.smtp_hostname}"
    BBR_DATABASE_URL         = "jdbc:sqlserver://${azurerm_mssql_database.bbr[each.key].fully_qualified_domain_name};database=${azurerm_mssql_database.bbr[each.key].name}"
    BBR_DATABASE_USERNAME    = "${var.database_admin_username}"
    BBR_DATABASE_PASSWORD    = "${var.database_admin_password}"
  }
}