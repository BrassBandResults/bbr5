resource "random_password" "mssql_username" {
  length      = 16
  special     = false
  min_lower   = 1
  min_upper   = 1
  min_numeric = 1
}

resource "random_password" "mssql_password" {
  length           = 16
  special          = true
  override_special = "!#"
  min_lower        = 1
  min_upper        = 1
  min_numeric      = 1
}

resource "azurerm_mssql_server" "this" {
  name                         = "${terraform.workspace}-bbr-sqlserver"
  resource_group_name          = azurerm_resource_group.this.name
  location                     = azurerm_resource_group.this.location
  version                      = "12.0"
  administrator_login          = random_password.mssql_username.result
  administrator_login_password = random_password.mssql_password.result
}

resource "azurerm_mssql_database" "bbr" {
  name           = "bbr"
  server_id      = azurerm_mssql_server.this.id
  collation      = "Norwegian_100_CI_AS" # We run in Norwegian as the sorting order works correctly for accented characters
  license_type   = "LicenseIncluded"
  max_size_gb    = 1
  sku_name       = "Basic"
  zone_redundant = false
}

resource "azurerm_mssql_firewall_rule" "prod" {
  for_each         = terraform.workspace == "prod" ? toset(azurerm_linux_web_app.bbr5.possible_outbound_ip_address_list) : []
  name             = "bbr-app-${each.value}"
  server_id        = azurerm_mssql_server.this.id
  start_ip_address = each.value
  end_ip_address   = each.value
}

resource "azurerm_mssql_firewall_rule" "nonprod" {
  count            = terraform.workspace == "nonprod" ? 1 : 0
  name             = "bbr-app-nonprod"
  server_id        = azurerm_mssql_server.this.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}
