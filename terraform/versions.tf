terraform {
  required_providers {
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "4.10.0"
    }
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.90.0"
    }
  }
}
