variable "region" {
  description = "Region to use for environment"
  type        = string
  default     = "ukwest"
}

variable "environments" {
  description = "List of environments to configure"
  type        = list(string)
  default     = ["prod"]
}

variable "database_admin_username" {
  description = "Username to use for database admin user"
  type        = string
  default     = "bbradmin"
}

variable "database_admin_password" {
  description = "Password to use for database admin user"
  type        = string
  default     = "DefaultPa$$word8337612"
  sensitive   = true
}

variable "smtp_username" {
  description = "Username to use for smtp server"
  type        = string
  default     = "username"
}

variable "smtp_password" {
  description = "Password to use for smtp server"
  type        = string
  default     = "DefaultPa$$word9841285"
  sensitive   = true
}
