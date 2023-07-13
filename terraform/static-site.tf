resource "azurerm_static_site" "apps" {
  name                = "static-apps"
  resource_group_name = azurerm_resource_group.this.name
  location            = "westeurope"
  sku_tier            = "Free"
  sku_size            = "Free"
}

resource "azurerm_static_site_custom_domain" "static" {
  depends_on      = [cloudflare_record.static_site]
  static_site_id  = azurerm_static_site.apps.id
  domain_name     = terraform.workspace == "prod" ? "static.brassbandresults.co.uk" : "static-${terraform.workspace}.brassbandresults.co.uk"
  validation_type = "cname-delegation"
}
