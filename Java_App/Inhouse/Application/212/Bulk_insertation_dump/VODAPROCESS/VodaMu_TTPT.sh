#!/bin/sh
#set -x      
     
WHICH="/usr/bin/which"
SQL="`$WHICH mysql`"
AWK="`$WHICH awk`"
ARITH="`$WHICH expr`"
DATE="`$WHICH date`"

mysql_db="vodafone_radio"
mysql_user="ivr"
mysql_password="ivr"
mysql_host="10.43.248.137"
mysql_local_file="/home/java/AirmRBTrNGMnger/Script/Vodamurng.txt"

count=0
CURDATE="`$DATE +%Y-%m-%d`"

$SQL -h$mysql_host -u$mysql_user $mysql_db -p$mysql_password -e "select ani,rngid,req_type,crbt_mode,status,songid from tbl_crbtrng_reqs where status=0 and req_type not in('mt','crbt') order by date_time asc;"> $mysql_local_file

#echo $sql
processLine()
{
	ani=`echo $line | $AWK '{ print $1 }'`
	rngid=`echo $line | $AWK '{ print $2 }'`
	req_type=`echo $line | $AWK '{ print $3 }'`
	crbt_mode=`echo $line | $AWK '{ print $4 }'`
	status=`echo $line | $AWK '{ print $5 }'`
	songid=`echo $line | $AWK '{ print $6 }'`

	#echo "ani ==" $ani "|rngid  ==" $rngid "|req_type  ==" $req_type "|crbt_mode  ==" $crbt_mode "|status  ==" $status "|songid  ==" $songid  >> malav.txt
	#resp=`elinks -dump "http://202.87.41.147/waphung/contentServe/PIN_generate.php" | tr -d ' \t'`
	resp=`elinks -dump "http://202.87.41.147/waphung/voiceContentServe/PIN_generate.php" | tr -d ' \t'`

	#echo "Response===" $resp
	rndPIN=$resp
	#echo "rndPIN iss" $rndPIN
	
	txtMSG="Thanks for downloading from Radio Unlimited. To download(Data Charges apply),click, http://202.87.41.147/waphung/voiceContentServe/"$rngid"/"$rndPIN

	#echo "textmess iss" $txtMSG	
	#$SQL -h$mysql_host -u$mysql_user $mysql_db -p$mysql_password -e "call master_db.SENDSMS_NEW($ani,'$txtMSG','HMMUSC','3','54646','TTPT');"
	$SQL -h$mysql_host -u$mysql_user $mysql_db -p$mysql_password -e "call master_db.SENDSMS_NEW($ani,'$txtMSG','55665',1);"	
	
	#$SQL -h$mysql_host -u$mysql_user $mysql_db -p$mysql_password -e "call radio_crbtrng_ok($ani,'$req_type','OK');"											
	$SQL -h$mysql_host -u$mysql_user $mysql_db -p$mysql_password -e "update tbl_crbtrng_reqs set status=2 where ani=$ani and req_type='$req_type' and songid='$songid';"
#	sleep 1 ;
	$SQL -h$mysql_host -u$mysql_user $mysql_db -p$mysql_password -e "call radio_crbtrng_ok($ani,'$req_type','$crbt_mode','OK');"
	
	echo $ani $rngid $req_type $crbt_mode $status $songid $resp $txtMSG >> /home/java/AirmRBTrNGMnger/Script/VODAMU_$CURDATE.txt
}

exec 0<$mysql_local_file

while read line
do
	if [ $count -gt 0 ]
	then
		processLine $line
	else
		count=`$ARITH $count + 1`
	fi	
done

cd /home/java/AirmRBTrNGMnger/Script/
rm -rf $mysql_local_file
