call mvnw clean install -Dmaven.test.skip=true
set JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
set BBR_SMTP_SERVER_PASSWORD=password
set BBR_SMTP_SERVER_USERNAME=username
set BBR_SMTP_SERVER_HOST=smtp.sendgrid.net
set BBR_DATABASE_URL=jdbc:sqlserver://timpi;database=bbr;trustServerCertificate=true
set BBR_DATABASE_USERNAME=sa
set BBR_DATABASE_PASSWORD=admin9552Password
set BBR_STATIC_FILES_HOST=static.brassbandresults.co.uk
call mvnw spring-boot:run
