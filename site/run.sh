#!/bin/bash
cd ~/web/bbr5/site
./mvnw clean install -Dmaven.test.skip=true
export JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
./mvnw spring-boot:run
