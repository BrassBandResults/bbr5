variable "region" {
  description = "Region to use for environment"
  type        = string
  default     = "ukwest"
}

variable "environment" {
  description = "Name of the environment to configure"
  type        = string
  default     = "prod"
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

variable "smtp_hostname" {
  description = "SMTP server to use to send emails"
  type        = string
  default     = "smtp.sendgrid.net"
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

variable "docker_image_tag" {
  description = "Docker image tag to deploy"
  type        = string
  default     = "new-docker-tag"
}
