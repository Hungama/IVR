#!/bin/bash
#set -x
CLASSPATH=.:/home/ivr/lib/activemq-all-5.5.0.jar:/home/ivr/lib/commons-lang-2.4.jar:/home/ivr/lib/commons-logging-1.1.jar:/home/ivr/lib/hungamalogging.jar:/home/ivr/lib/jcharset.jar:/home/ivr/lib/jsmpp-2.1.0.zip:/home/ivr/lib/log4j-1.2.14.jar:/home/ivr/lib/mysql-connector-java-5.1.15-bin.jar:/home/ivr/lib/slf4j-simple-1.5.8.jar:/usr/jdk1.6.0_26/lib:
export CLASSPATH

cd /home/ivr/BillingDataProcess/RELIANCE/
javac RelianceDataValidation.java
nohup /usr/jdk1.6.0_26/bin/java RelianceDataValidation &
