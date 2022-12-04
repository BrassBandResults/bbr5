variable "region" {
    type = string
    default = "ukwest"
    description = "Region to use for environment"
}

variable "environment" {
    type = string
    default = "test"
    description = "Suffix to use for environment name, format is bbr5-suffix"
}

variable "database_admin_username" {
    type = string
    default = "bbradmin"
    description = "Username to use for database admin user"
}

variable "database_admin_password" {
    type = string
    default = "DefaultPa$$word8337612"
    sensitive = true
    description = "Password to use for database admin user"
}