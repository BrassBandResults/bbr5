resource "azurerm_storage_account" "bbr5" {
  name                     = terraform.workspace == "prod" ? "bbr5storage" : "bbr5storage${terraform.workspace}"
  resource_group_name      = azurerm_resource_group.this.name
  location                 = azurerm_resource_group.this.location
  account_tier             = "Standard"
  account_replication_type = "GRS"
}

resource "azurerm_storage_queue" "location-updates" {
  name                 = "location-updates"
  storage_account_name = azurerm_storage_account.bbr5.name
}
