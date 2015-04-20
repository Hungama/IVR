#!/bin/bash
#First Copy tar file from remote server and then untar the files on 212
#1.Copy Tar file For Voda server

cd /home/MISDATA_TEST/BILLINGDATA/MTS/
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
sshpass -p'redhat' /usr/bin/scp -r root@10.130.14.160:/opt/BILLINGDATA/MTS_*_$DATE_PREV.tar.gz /home/MISDATA_TEST/BILLINGDATA/MTS/


#2.Untar the copied file on 212 copied from remote server
cd /home/MISDATA_TEST/BILLINGDATA/MTS/

basepath="/home/MISDATA_TEST/BILLINGDATA/MTS/"
logfilename='MTS_*_'
targetfilepath="$logfilename$DATE_PREV.tar.gz"
orgfilename="$logfilename$DATE_PREV.txt"

sleep 2
untargetfilepath="$basepath$targetfilepath"
untarfile=`tar -zxvf $untargetfilepath`