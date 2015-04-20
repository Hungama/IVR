#!/bin/bash

cd /home/MISDATA_TEST
DATE_PREV=$(date --date="2 day ago" +"%Y-%m-%d")
sshpass -p'redhat' /usr/bin/scp -r root@10.130.14.160:/opt/BILLINGDATA/MTS_Cricket_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/MTS/
