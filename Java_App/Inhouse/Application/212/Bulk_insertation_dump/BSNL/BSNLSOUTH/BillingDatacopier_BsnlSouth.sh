#!/bin/bash
cd /home/MISDATA_TEST
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")

#sshpass -p'Hun$$gam&&BSNL_2$R$' /usr/bin/scp -r root@59.90.196.159:2222/home/BillingData/Bsnl_Chd_54646_$DATE_PREV.txt /home/MISDATA_TEST/BSNL/NORTH/
sshpass -p'redhat' /usr/bin/scp -P 2222 -r root@117.218.244.209:/home/BillingData/Bsnl_Chd_54646_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/BSNL/SOUTH/


#sleep 120

#CLASSPATH=.:/home/ivr/lib/mysql-connector-java-5.1.15-bin.jar:/usr/jdk1.6.0_26/lib:
#export CLASSPATH

#cd /home/ivr/BsnlContentMis
#javac BsnlContentAutomation.java
#/usr/jdk1.6.0_26/bin/java BsnlContentAutomation 1 > /home/ivr/BsnlContentMis/logs/Bsnl`date '+%m-%d-%Y'`.txt
