#!/bin/bash
#First Copy tar file from remote server and then untar the files on 212

#1.Copy Tar file For Voda server

cd /home/MISDATA_TEST
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
sshpass -p'DDTitNp$%&<gazab##@#!*137FHG34DF' /usr/bin/scp -r gazab@10.43.248.137:/home/BILLINGDATA/VODA_*_$DATE_PREV.tar.gz /home/MISDATA_TEST/BILLINGDATA/VODA/


#2.Untar the copied file on 212 copied from remote server

cd /home/MISDATA_TEST/BILLINGDATA/VODA/

basepath="/home/MISDATA_TEST/BILLINGDATA/VODA/"
logfilename='VODA_*_'
targetfilepath="$logfilename$DATE_PREV.tar.gz"
orgfilename="$logfilename$DATE_PREV.txt"

sleep 2
untargetfilepath="$basepath$targetfilepath"
untarfile=`tar -zxvf $untargetfilepath`



