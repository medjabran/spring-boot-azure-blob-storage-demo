terraform {
  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
    }
  }
}

variable "group_name" {
  type = string
  default = "1-38d6a4c1-playground-sandbox"
}

variable "group_location" {
  type = string
  default = "eastus"
}

provider "azurerm" {
  features {}
  skip_provider_registration = "true"
}

resource "azurerm_storage_account" "account" {
  name                          = "demostorageaccount"
  resource_group_name           = var.group_name
  location                      = var.group_location
  account_tier                  = "Standard"
  account_replication_type      = "LRS"
  public_network_access_enabled = true
}

resource "azurerm_storage_container" "blob" {
  name                  = "demo-blob"
  storage_account_name  = azurerm_storage_account.account.name
  container_access_type = "private"
}