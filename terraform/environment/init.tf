provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "bbr5-resource-group" {
  name     = "bbr5-${var.environment}"
  location = var.region
}

terraform {
  backend "azurerm" {
    resource_group_name      = "bbr5-global"
    storage_account_name     = "bbr5state"
    container_name           = "terraform"
    key                      = "bbr-terraform.tfstate"
  }
}