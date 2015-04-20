#!/bin/bash
#First Copy tar file from remote server and then untar the files on 212
#1.Copy Tar file For Voda server

cd /home/MISDATA_TEST/BILLINGDATA/Airtel/
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
sshpass -p'Hun$$gam&&160$R$' scp -r root@10.2.73.160:/home/BILLINGDATA/*_Airtel_$DATE_PREV.tar.gz /home/MISDATA_TEST/BILLINGDATA/Airtel/


#2.Untar the copied file on 212 copied from remote server
cd /home/MISDATA_TEST/BILLINGDATA/Airtel/

basepath="/home/MISDATA_TEST/BILLINGDATA/Airtel/"
logfilename='*_Airtel_'
targetfilepath="$logfilename$DATE_PREV.tar.gz"
orgfilename="$logfilename$DATE_PREV.txt"

sleep 2
untargetfilepath="$basepath$targetfilepath"
untarfile=`tar -zxvf $untargetfilepath`



