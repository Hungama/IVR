#!/bin/bash

cd /home/MISDATA_TEST
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
sshpass -p'DDTitNp$%&<gazab##@#!*224FHG34DF' /usr/bin/scp -r gazab@192.168.100.224:/tmp/BILLINGDATA/ETISALAT/*.txt /home/MISDATA_TEST/BILLINGDATA/ETISALAT/


