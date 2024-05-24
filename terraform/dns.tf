resource "cloudflare_record" "static_site" {
  zone_id = var.cloudflare_zone_id
  name    = terraform.workspace == "prod" ? "static" : "static-${terraform.workspace}"
  value   = azurerm_static_site.apps.default_host_name
  type    = "CNAME"
  ttl     = terraform.workspace == "prod" ? 1 : 60
  proxied = terraform.workspace == "prod" ? true : false
}

resource "cloudflare_record" "app_service" {
  zone_id = var.cloudflare_zone_id
  name    = terraform.workspace == "prod" ? "bbr5" : "bbr5-${terraform.workspace}"
  value   = azurerm_linux_web_app.bbr5.default_hostname
  type    = "CNAME"
  ttl     = terraform.workspace == "prod" ? 1 : 60
  proxied = terraform.workspace == "prod" ? true : false
}

resource "cloudflare_record" "app_service-asuid" {
  count   = terraform.workspace == "prod" ? 0 : 1
  zone_id = var.cloudflare_zone_id
  name    = terraform.workspace == "prod" ? "bbr5" : "asuid.bbr5-${terraform.workspace}"
  value   = azurerm_linux_web_app.bbr5.custom_domain_verification_id
  type    = "CNAME"
  ttl     = terraform.workspace == "prod" ? 1 : 60
  proxied = terraform.workspace == "prod" ? true : false
}
