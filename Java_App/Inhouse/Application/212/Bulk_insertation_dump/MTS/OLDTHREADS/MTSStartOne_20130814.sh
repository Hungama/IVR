#!/bin/sh
#set -x
cd /home/ivr/BillingDataProcess/MTS/
javac MTSDataProcessOne.java
nohup java MTSDataProcessOne &
