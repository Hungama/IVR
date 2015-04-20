#!/bin/bash
cd /home/MISDATA_TEST/BILLINGDATA/Airtel/
DATE_PREV=$(date --date="1 day ago" +"%Y-%m-%d")
sshpass -p'Hun$$gam&&160$R$' scp -r root@10.2.73.160:/home/BILLINGDATA/*_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/Airtel/
#sshpass -p'P#PO#MA#DI!&TOPO!H%' /usr/bin/scp -r root@10.2.73.160:/home/BILLINGDATA/*_$DATE_PREV.txt /home/MISDATA_TEST/BILLINGDATA/Airtel/

