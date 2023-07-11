resource "azurerm_static_site" "docs" {
  name                = "static-docs"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
  sku_tier            = "Free"
  sku_size            = "Free"
}

resource "azurerm_static_site" "apps" {
  name                = "static-apps"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
  sku_tier            = "Free"
  sku_size            = "Free"
}
