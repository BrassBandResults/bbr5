resource "azurerm_static_site" "apps" {
  name                = "static-apps"
  resource_group_name = azurerm_resource_group.this.name
  location            = "westeurope"
  sku_tier            = "Free"
  sku_size            = "Free"
}

resource "azurerm_static_site_custom_domain" "static" {
  static_site_id  = azurerm_static_site.apps.id
  domain_name     = "static.brassbandresults.co.uk"
  validation_type = "cname-delegation"
}