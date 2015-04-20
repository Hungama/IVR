#!/bin/bash

cd /home/MISDATA_TEST
DATE_PREV=$(date --date="5 day ago" +"%Y-%m-%d")
sshpass -p'redhat' /usr/bin/scp -r root@10.130.14.160:/opt/BILLINGDATA/*_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/MTS/
