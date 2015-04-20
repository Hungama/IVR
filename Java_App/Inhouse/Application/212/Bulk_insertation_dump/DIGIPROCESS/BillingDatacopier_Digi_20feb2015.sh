#!/bin/bash

cd /home/MISDATA_TEST
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
sshpass -p'Hun$$gam&&42$R$' /usr/bin/scp -r root@172.16.56.42:/home/BILLINGDATA/*_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/DIGI/


