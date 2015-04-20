#!/bin/bash

#First Copy tar file from remote server and then untar the files on 212
#1.Copy Tar file For Voda server

cd /home/MISDATA_TEST/BILLINGDATA/DIGI/
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
sshpass -p'Hun$$gam&&42$R$' /usr/bin/scp -r root@172.16.56.42:/home/BILLINGDATA/*_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/DIGI/


#2.Untar the copied file on 212 copied from remote server
cd /home/MISDATA_TEST/BILLINGDATA/DIGI/

basepath="/home/MISDATA_TEST/BILLINGDATA/DIGI/"
logfilename='DIGI_ALL_'
targetfilepath="$logfilename$DATE_PREV.tar.gz"
orgfilename="$logfilename$DATE_PREV.txt"

sleep 2
untargetfilepath="$basepath$targetfilepath"
untarfile=`tar -zxvf $untargetfilepath`