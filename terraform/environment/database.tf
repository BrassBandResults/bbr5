resource "azurerm_mssql_server" "bbr5" {
  name                         = "${azurerm_resource_group.bbr5-resource-group.name}-sqlserver"
  resource_group_name          = azurerm_resource_group.bbr5-resource-group.name
  location                     = azurerm_resource_group.bbr5-resource-group.location
  version                      = "12.0"
  administrator_login          = var.database_admin_username
  administrator_login_password = var.database_admin_password
}

resource "azurerm_mssql_database" "bbr5" {
  name           = "bbr"
  server_id      = azurerm_mssql_server.bbr5.id
  collation      = "Norwegian_100_CI_AS" # We run in Norwegian as the sorting order works correctly for accented characters
  license_type   = "LicenseIncluded"
  max_size_gb    = 1
  sku_name       = "Basic"
  zone_redundant = false
}