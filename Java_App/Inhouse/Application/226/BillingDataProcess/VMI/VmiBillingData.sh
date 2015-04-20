#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/tomcat/lib/mysql.jar
export CLASSPATH
cd /home/java/BillingDataProcess/VMI/

/usr/java/jdk1.6.0_30/bin/javac VmiDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java VmiDataProcess 1 &

sleep 600

sh /home/java/BillingDataProcess/VMI/Vmi_Data_files.sh