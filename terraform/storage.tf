resource "random_string" "pr-storage-account-name" {
  length  = 10
  special = false
  upper   = false
}

resource "azurerm_storage_account" "bbr5" {
  name                     = terraform.workspace == "prod" ? "bbr5storage" : "bbr5storage${random_string.pr-storage-account-name.result}"
  resource_group_name      = azurerm_resource_group.this.name
  location                 = azurerm_resource_group.this.location
  account_tier             = "Standard"
  account_replication_type = "GRS"
}

resource "azurerm_storage_queue" "point-awards" {
  name                 = "point-awards"
  storage_account_name = azurerm_storage_account.bbr5.name
}
