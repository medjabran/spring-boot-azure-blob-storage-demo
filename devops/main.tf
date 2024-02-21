terraform {
  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
    }
  }
}

provider "azurerm" {
  features {}
  skip_provider_registration = "true"
}

resource "azurerm_resource_group" "group" {
  name     = "demoressourcegroup"
  location = "eastus"
}

resource "azurerm_storage_account" "account" {
  name                          = "demostorageaccount"
  resource_group_name           = azurerm_resource_group.group.name
  location                      = azurerm_resource_group.group.location
  # account_kind             = "StorageV2"
  account_tier                  = "Standard"
  account_replication_type      = "LRS"
  public_network_access_enabled = true
}

resource "azurerm_storage_container" "blob" {
  name                  = "demo-blob"
  storage_account_name  = azurerm_storage_account.account.name
  container_access_type = "private"
}

resource "azurerm_storage_queue" "queue" {
  name                 = "demo-queue"
  storage_account_name = azurerm_storage_account.account.name
}