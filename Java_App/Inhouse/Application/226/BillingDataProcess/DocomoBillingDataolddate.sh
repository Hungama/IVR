#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/tomcat/lib/mysql.jar
export CLASSPATH
cd /home/java/BillingDataProcess/

/usr/java/jdk1.6.0_30/bin/javac DocomoDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java DocomoDataProcess 1 &

sleep 120

sh /home/java/BillingDataProcess/Docomo_Data_files.sh 

sleep 60

cd /home/java/BillingDataProcess/INDICOM/

/usr/java/jdk1.6.0_30/bin/javac IndicomDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java IndicomDataProcess 1 &

sleep 600

sh /home/java/BillingDataProcess/INDICOM/Indicom_Data_files.sh

sleep 60

cd /home/java/BillingDataProcess/VMI/
/usr/java/jdk1.6.0_30/bin/javac VmiDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java VmiDataProcess 1 &

sleep 600

sh /home/java/BillingDataProcess/VMI/Vmi_Data_files.sh

sleep 60

cd /home/java/BillingDataProcess/RELIANCE/

/usr/java/jdk1.6.0_30/bin/javac RelianceDataProcess.java
nohup /usr/java/jdk1.6.0_30/bin/java RelianceDataProcess 1 &

sleep 600

sh /home/java/BillingDataProcess/RELIANCE/Reliance_Data_files.sh
