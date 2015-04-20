#!/bin/bash
cd /home/MISDATA_TEST
DATE_PREV=$(date --date="3 day ago" +"%Y-%m-%d")

#sshpass -p'Hun$$gam&&BSNL_2$R$' /usr/bin/scp -r root@59.90.196.159:2222/home/BillingData/Bsnl_Chd_54646_$DATE_PREV.txt /home/MISDATA_TEST/BSNL/NORTH/
sshpass -p'Hun$$gam&&BSNL_1$R$' /usr/bin/scp -P 22 -r root@59.90.196.159:/home/BillingData/BillingFiles/Bsnl_Chd_54646_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/BSNL/
sshpass -p'redhat' /usr/bin/scp -P 22 -r root@117.218.244.209:/home/BillingData/BillingFiles/Bsnl_south_54646_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/BSNL/SOUTH/
sshpass -p'Hun$$gam&&BS$NlR$06' /usr/bin/scp -P 2931 -r root@117.218.164.149:/home/BillingData/BillingFiles/Bsnl_west_54646_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/BSNL/WEST/

#sleep 120

#CLASSPATH=.:/home/ivr/lib/mysql-connector-java-5.1.15-bin.jar:/usr/jdk1.6.0_26/lib:
#export CLASSPATH

#cd /home/ivr/BsnlContentMis
#javac BsnlContentAutomation.java
#/usr/jdk1.6.0_26/bin/java BsnlContentAutomation 1 > /home/ivr/BsnlContentMis/logs/Bsnl`date '+%m-%d-%Y'`.txt
