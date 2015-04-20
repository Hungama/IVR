#!/bin/sh
#shell Script for compressing the billing files createTarFile.sh
WHICH="/usr/bin/which"
DATE="`$WHICH date`"
file_date="$(date --date='1 day ago' "+%Y-%m-%d")"

#DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
#file_date=2015-02-03

echo $file_date
#/home/BILLINGDATA/PK_Airtel_2015-02-16.txt

cd /home/BILLINGDATA/
logfilename='*_Airtel_'
sourcefilepath="$logfilename$file_date.txt"
#targetfilepath="tarfiles/$logfilename$file_date.tar.gz"
targetfilepath="$logfilename$file_date.tar.gz"
createtarfile=`tar -zcvf $targetfilepath $sourcefilepath`
