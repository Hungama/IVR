#!/bin/bash

cd /home/MISDATA_TEST
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
sshpass -p'DDTitNp$%&<gazab##@#!*137FHG34DF' /usr/bin/scp -r gazab@10.43.248.137:/home/BILLINGDATA/*_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/VODA/
