#!/bin/sh
#set -x
WHICH="/usr/bin/which"
SQL="`$WHICH mysql`"
AWK="`$WHICH awk`"
ARITH="`$WHICH expr`"

mysql_host="database.master"
mysql_db1="misdata"
mysql_user="billing"
mysql_passwd="billing"


$SQL -h$mysql_host -u$mysql_user -p$mysql_passwd $mysql_db1 -e "call misdata.VMI_Billing_Data_All(1);"
