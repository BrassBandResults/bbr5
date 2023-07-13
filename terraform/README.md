# Terraform
Terraform is used to deploy resources onto the Microsoft Cloud, Azure.

This provides
* Resource Group for resources
* A static site that hosts the static resources used by the site such as flags and icons
* A SQL Database server + database to host the data, this is generated 1 per environment and managed via the Terraform variable __`environment`__

The following environment variables need to be set for this terraform run to work:
* __`ARM_TENANT_ID`__ - This is your Azure tenant ID
* __`ARM_SUBSCRIPTION_ID`__ - This is your Azure subscription ID
* __`ARM_CLIENT_ID`__ - This is your Azure client ID
* __`ARM_CLIENT_SECRET`__ - This is your Azure client secret
* __`ARM_ACCESS_KEY`__ - This is your Terraform state storage account access key
