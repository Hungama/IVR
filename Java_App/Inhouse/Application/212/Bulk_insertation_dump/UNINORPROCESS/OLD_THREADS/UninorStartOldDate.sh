#!/bin/sh
#set -x
CLASSPATH=$CLASSPATH:.:/usr/java/jdk1.6.0_16/lib/mysql.jar;
export CLASSPATH
cd /home/ivr/BillingDataProcess/UNINORPROCESS/
javac UninorDataProcessOldDate.java
nohup java UninorDataProcessOldDate 71 &
