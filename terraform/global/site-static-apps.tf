resource "azurerm_static_site" "static-apps" {
  name                = "static-apps"
  resource_group_name = azurerm_resource_group.bbr5-static-resource-group.name
  location            = azurerm_resource_group.bbr5-static-resource-group.location
  sku_tier            = "Free"
  sku_size            = "Free"
}