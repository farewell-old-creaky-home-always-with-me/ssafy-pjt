variable "aws_region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

variable "project_name" {
  description = "리소스 이름 접두사"
  type        = string
  default     = "ssafy-home"
}

variable "admin_cidr" {
  description = "SSH 접근 허용 CIDR (예: 1.2.3.4/32)"
  type        = string
}

variable "db_password" {
  description = "RDS MySQL 비밀번호"
  type        = string
  sensitive   = true
}

variable "ssh_public_key" {
  description = "EC2 Key Pair에 등록할 SSH 퍼블릭 키"
  type        = string
  sensitive   = true
}
