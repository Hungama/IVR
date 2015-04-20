#!/bin/sh
#set -x
cd /home/ivr/BillingDataProcess/MTS/
javac MTSDataProcessTwo.java
nohup java MTSDataProcessTwo 1 &
