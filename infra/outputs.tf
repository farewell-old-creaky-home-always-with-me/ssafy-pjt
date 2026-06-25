output "elastic_ip" {
  description = "EC2 Elastic IP — Route53 A 레코드에 수동 등록"
  value       = aws_eip.main.public_ip
}

output "rds_endpoint" {
  description = "RDS 엔드포인트 host:port — datasource url에 사용"
  value       = aws_db_instance.main.endpoint
}

output "rds_host" {
  description = "RDS 호스트명 (포트 제외)"
  value       = aws_db_instance.main.address
}

output "ec2_instance_id" {
  description = "EC2 인스턴스 ID"
  value       = aws_instance.main.id
}
