#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/tomcat/lib/mysql.jar
export CLASSPATH
cd /home/java/BillingDataProcess/UNINOR/


/usr/java/jdk1.6.0_30/bin/javac UninorDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java UninorDataProcess 1 &

sleep 600

sh /home/java/BillingDataProcess/UNINOR/Uninor_Data_files.sh

