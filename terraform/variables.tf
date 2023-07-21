variable "region" {
  description = "Region to use for environment"
  type        = string
  default     = "ukwest"
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

variable "cloudflare_zone_id" {
  description = "cloudflare zone id"
  type        = string
  default     = ""
}

variable "home_ip" {
  description = "home ip address for database access"
  type        = string
  default     = "82.69.23.82"
}
