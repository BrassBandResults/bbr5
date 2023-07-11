resource "azurerm_static_site" "apps" {
  name                = "static-apps"
  resource_group_name = azurerm_resource_group.this.name
  location            = "westeurope"
  sku_tier            = "Free"
  sku_size            = "Free"
}
