resource "azurerm_storage_account" "state" {
  name                     = "bbr5state"
  resource_group_name      = azurerm_resource_group.bbr5-global-resource-group.name
  location                 = azurerm_resource_group.bbr5-global-resource-group.location
  account_tier             = "Standard"
  account_replication_type = "GRS"
}

resource "azurerm_storage_container" "terraform" {
  name                  = "terraform"
  storage_account_name  = azurerm_storage_account.state.name
  container_access_type = "blob"
}