#!/bin/sh
#set -x

CLASSPATH=$CLASSPATH:.:/home/ivr/lib/activemq-all-5.5.0.jar:/home/ivr/lib/saaj-api.jar:/home/ivr/lib/log4j-1.2.14.jar:/home/ivr/lib/slf4j-api-1.5.11.jar:/home/ivr/lib/slf4j-log4j12-1.5.11.jar:/home/ivr/lib/mysql.jar;
export CLASSPATH
cd /home/ivr/JAVA/BillingDataProcess/
javac DigiDataProcess.java
nohup java DigiDataProcess 1 &
