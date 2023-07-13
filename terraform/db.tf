resource "azurerm_mssql_server" "this" {
  name                         = "${var.environment}-bbr-sqlserver"
  resource_group_name          = azurerm_resource_group.this.name
  location                     = azurerm_resource_group.this.location
  version                      = "12.0"
  administrator_login          = var.database_admin_username
  administrator_login_password = var.database_admin_password
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

resource "azurerm_mssql_firewall_rule" "this" {
  for_each         = azurerm_linux_web_app.bbr5.possible_outbound_ip_address_list
  name             = "bbr-app-${each.value}"
  server_id        = azurerm_mssql_server.this.id
  start_ip_address = each.value
  end_ip_address   = each.value
}
