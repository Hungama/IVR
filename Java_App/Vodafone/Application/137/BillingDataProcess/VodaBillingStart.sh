#!/bin/bash
#set -x
CLASSPATH=$CLASSPATH:.:/var/lib/apache-tomcat-6.0.32/lib/commons-httpclient-3.0.1.jar:/var/lib/apache-tomcat-6.0.32/lib/commons-logging-1.0.4.jar:/var/lib/apache-tomcat-6.0.32/lib/commons-codec.jar:/var/lib/apache-tomcat-6.0.32/lib/servlet-2.3.jar:/home/ivr/lib/activemq-all-5.4.2.jar:/home/ivr/lib/log4j-1.2.14.jar:/home/ivr/lib/mysql-connector-java-3.1.11a-bin.jar
export CLASSPATH
cd /home/java/BillingDataProcess/
javac VodaDataProcess.java
nohup java VodaDataProcess 1 &
