#!/bin/sh
#set -x
CLASSPATH=.:/home/Java/mysql-connector-java-5.1.15-bin.jar
export CLASSPATH

echo -e "Dev Team: Manish /home/Java/AirtelBillingDataProcess/AirtelBilling156.sh Script Started `date`\n" >> /home/Java/AirtelBillingDataProcess/dev_team_AirtelStart_script.log
cd /home/Java/AirtelBillingDataProcess

/usr/java/jdk1.6.0_30/bin/javac AirtelBillingDataProcess.java
/usr/java/jdk1.6.0_30/bin/java AirtelBillingDataProcess 1 &

#sleep 600
echo -e "Dev Team: Manish /home/Java/AirtelBillingDataProcess/AirtelBilling156.sh Script Ended `date`\n" >> /home/Java/AirtelBillingDataProcess/dev_team_AirtelStart_script.log


#sh /home/Scripts/Airtel_Data_files.sh
