data "azurerm_cosmosdb_account" "bbr5" {
  name                = terraform.workspace == "prod" ? "bbr5" : "bbr5-${terraform.workspace}"
  resource_group_name = azurerm_resource_group.this.name
}

resource "azurerm_cosmosdb_sql_database" "example" {
  name                = "locations"
  resource_group_name = data.azurerm_cosmosdb_account.bbr5.resource_group_name
  account_name        = data.azurerm_cosmosdb_account.bbr5.name
}
