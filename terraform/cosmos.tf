resource "azurerm_cosmosdb_account" "bbr5" {
  name                = terraform.workspace == "prod" ? "bbr5" : "bbr5-${terraform.workspace}"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
  offer_type          = "Standard"


  capabilities {
    name = "EnableServerless"
  }

  consistency_policy {
    consistency_level       = "BoundedStaleness"
    max_interval_in_seconds = 300
    max_staleness_prefix    = 100000
  }

  geo_location {
    location          = azurerm_resource_group.this.location
    failover_priority = 0
  }
}

resource "azurerm_cosmosdb_sql_database" "locations" {
  name                = "locations"
  resource_group_name = azurerm_cosmosdb_account.bbr5.resource_group_name
  account_name        = azurerm_cosmosdb_account.bbr5.name
}

resource "azurerm_cosmosdb_sql_container" "band-locations" {
  name                  = "band-locations"
  resource_group_name   = data.azurerm_cosmosdb_account.bbr5.resource_group_name
  account_name          = data.azurerm_cosmosdb_account.bbr5.name
  database_name         = azurerm_cosmosdb_sql_database.locations.name
  partition_key_path    = "/band/slug"
  partition_key_version = 1
  throughput            = 400

  indexing_policy {
    indexing_mode = "consistent"

    included_path {
      path = "/*"
    }

    included_path {
      path = "/included/?"
    }

    excluded_path {
      path = "/excluded/?"
    }
  }

  unique_key {
    paths = ["/band/slug"]
  }
}
