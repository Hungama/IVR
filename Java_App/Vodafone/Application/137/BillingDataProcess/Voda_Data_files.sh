#!/bin/sh
#set -x
WHICH="/usr/bin/which"
SQL="`$WHICH mysql`"
AWK="`$WHICH awk`"
ARITH="`$WHICH expr`"
DATE="`$WHICH date`"

mysql_db="master_db"
mysql_user="team_user"
mysql_password="teamuser@voda#123"
mysql_host="10.43.248.137"
mysql_local_file="procedure.txt"


echo "VODA_BillingData";
$SQL -u$mysql_user $mysql_db -p$mysql_password -e "call MISDATA.Voda_Billing_Data_All(1);"

