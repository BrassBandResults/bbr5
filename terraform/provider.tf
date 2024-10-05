provider "azurerm" {
  skip_provider_registration = true
  environment                = "public"

  features {}
}

provider "cloudflare" {
  # set CLOUDFLARE_API_TOKEN environment variable
}
