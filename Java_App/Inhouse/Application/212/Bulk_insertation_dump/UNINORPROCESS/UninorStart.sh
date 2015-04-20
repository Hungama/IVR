#!/bin/bash
#set -x
CLASSPATH=.:/home/ivr/lib/activemq-all-5.5.0.jar:/home/ivr/lib/commons-lang-2.4.jar:/home/ivr/lib/commons-logging-1.1.jar:/home/ivr/lib/hungamalogging.jar:/home/ivr/lib/jcharset.jar:/home/ivr/lib/jsmpp-2.1.0.zip:/home/ivr/lib/log4j-1.2.14.jar:/home/ivr/lib/mysql-connector-java-5.1.15-bin.jar:/home/ivr/lib/slf4j-simple-1.5.8.jar:
export CLASSPATH
echo -e "## Script started `date` ##\n"
cd /home/ivr/BillingDataProcess/UNINORPROCESS/
javac Uninor_Bulkload_Process.java
nohup /usr/jdk1.6.0_26/bin/java Uninor_Bulkload_Process 1 &
