resource "azurerm_resource_group" "this" {
  name     = "bbr5-${terraform.workspace}"
  location = var.region
}
