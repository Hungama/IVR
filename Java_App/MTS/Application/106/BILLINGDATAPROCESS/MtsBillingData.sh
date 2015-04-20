#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/usr/local/apache-tomcat-6.0.20/lib/mysql.jar
export CLASSPATH
cd /home/java/BILLINGDATAPROCESS/

/usr/src/jdk1.6.0_26/bin/javac MTSDataProcess.java
nohup /usr/src/jdk1.6.0_26/bin/java MTSDataProcess 1 &

#sleep 2000
#sh /home/java/BILLINGDATAPROCESS/MTS_Data_files.sh

