#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/tomcat/lib/mysql.jar
export CLASSPATH
cd /home/java/BillingDataProcess/RELIANCE/

/usr/java/jdk1.6.0_30/bin/javac RelianceDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java RelianceDataProcess 1 &

#sleep 600

#sh /home/java/BillingDataProcess/RELIANCE/Reliance_Data_files.sh