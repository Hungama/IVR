#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/tomcat/lib/mysql.jar
export CLASSPATH
cd /home/java/BillingDataProcess/INDICOM/

/usr/java/jdk1.6.0_30/bin/javac IndicomDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java IndicomDataProcess 1 &

sleep 600

sh /home/java/BillingDataProcess/INDICOM/Indicom_Data_files.sh