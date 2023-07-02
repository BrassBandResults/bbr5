#!/bin/bash
./mvnw clean install -Dmaven.test.skip=true
export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
export BBR_SMTP_SERVER_PASSWORD=password
export BBR_SMTP_SERVER_USERNAME=username
export BBR_SMTP_SERVER_HOST=smtp.sendgrid.net
export BBR_DATABASE_URL="jdbc:sqlserver://timpi;database=bbr;trustServerCertificate=true"
export BBR_DATABASE_USERNAME=sa
export BBR_DATABASE_PASSWORD=admin9552Password
env | grep BBR_
./mvnw spring-boot:run
