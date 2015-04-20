#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/tomcat/lib/mysql.jar
export CLASSPATH
cd /home/java/BillingDataProcess/ETISALAT/

/usr/java/jdk1.6.0_30/bin/javac EtisalatDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java EtisalatDataProcess 1 &

sleep 1200

sh /home/java/BillingDataProcess/ETISALAT/Etisalat_Data_files.sh
