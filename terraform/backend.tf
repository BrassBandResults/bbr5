terraform {
  backend "azurerm" {
    resource_group_name  = "bbr5-terraform"
    storage_account_name = "bbr5tfstate"
    container_name       = "terraform"
    key                  = "bbr5.tfstate"
  }
}
