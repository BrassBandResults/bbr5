provider "azurerm" {
  environment = "public"

  features {}
}

provider "cloudflare" {
  # set CLOUDFLARE_API_TOKEN environment variable
}
