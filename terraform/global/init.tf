provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "bbr5-global-resource-group" {
  name     = "bbr5-global"
  location = var.region_global
}

resource "azurerm_resource_group" "bbr5-static-resource-group" {
  name     = "bbr5-global-static"
  location = var.region_static
}