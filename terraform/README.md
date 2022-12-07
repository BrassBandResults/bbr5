# Terraform
Terraform is used to deploy resources onto the Microsoft Cloud, Azure.

There are two folders of terraform available

## Global
This terraform is the basic structures that span all deployed bbr environments.  This provides
* Resource Group for static resources
* A static site that hosts the static resources used by the site such as flags and icons
* a static site that hosts some documentation about how to work with the source and make app changes
* A storage account used for the Environment terraform state
This terraform needs to be run manually.  It's likely already there in the correct state.

## Evironment
This terraform is the structure of a deployed bbr application.  Multiple versions of this can (in theory at this stage!) be deployed in isolation, though they do share static resources.
The environment name is controlled using the value of the environment terraform variable.
This deploys for each environment:
* A resource group for the environment resources.
* A SQL Database to host the data

The following environment variables need to be set for this terraform run to work:
ARM_ACCESS_KEY - This needs to be set to an Access Key for the bbr5state Storage Account that is created by the Global terraform