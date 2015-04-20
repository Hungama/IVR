#!/bin/sh
#set -x
WHICH="/usr/bin/which"
SQL="`$WHICH mysql`"
AWK="`$WHICH awk`"
ARITH="`$WHICH expr`"
DATE="`$WHICH date`"

mysql_db="master_db"
mysql_user="root"
mysql_password="D1g1r00t@!23"
mysql_host="172.16.56.42"
mysql_local_file="procedure.txt"


echo "DIGI_BillingData";
$SQL -u$mysql_user $mysql_db -p$mysql_password -e "call MISDATA.Digi_Billing_Data_All(1);"
