#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/tomcat/lib/mysql.jar
export CLASSPATH
cd /home/ivr/BillingDataProcess/BSNL/

#sh /home/ivr/BillingDataProcess/BSNL/Billinglogcopier_Bsnl.sh
javac Bsnl_Bulkload_Process.java
nohup /usr/jdk1.6.0_26/bin/java Bsnl_Bulkload_Process 1 &




