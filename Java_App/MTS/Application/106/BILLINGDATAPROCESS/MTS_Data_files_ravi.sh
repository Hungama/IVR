#!/bin/sh
#set -x
WHICH="/usr/bin/which"
SQL="`$WHICH mysql`"

mysql_host="database.master_mts"
mysql_db1="mts_radio"
mysql_user="root"
mysql_password="MtsHungama"

#$SQL -h$mysql_host -u$mysql_user -p$mysql_password $mysql_db1 -e "call MU_ENGAGE_SMS();"
$SQL -h$mysql_host -u$mysql_user -p$mysql_password $mysql_db1 -e "call misdata.MTS_Billing_Data_All(7);"
