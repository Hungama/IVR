import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.sql.*;


	public class VodaDataProcess extends Thread
	{
	public static Connection con_source=null,con_destination=null;
	public static Statement stmt_source,stmt_destination;
	public String sIP=null,sDSN=null,sUSR=null,sPWD=null;
	public String dIP=null,dDSN=null,dUSR=null,dPWD=null;
	public static CallableStatement cstmt=null;
	public static int day=1;
	public static int i=0;
	public void readDBCONFIG()
	{
		try
		{
			System.out.println("**********************************************************");
			System.out.println("**     Thread Started With The Following Configuration  **");
			System.out.println("**              File to be Read is DB.CFG          **");

			sIP="203.199.126.129";
			sDSN="master_db";
			sUSR="billing";
			sPWD="billing";

			dIP="192.168.100.218";
			dDSN="misdata";
			dUSR="billing";
			dPWD="billing";

			System.out.println("**SOURCE IP is  ["+sIP+"] **  DSN is ["+sDSN+"] Usr is ["+sUSR+"] Pwd is ["+sPWD+"]\t**");
			System.out.println("**DESTINATION IP is  ["+dIP+"] **  DSN is ["+dDSN+"] Usr is ["+dUSR+"] Pwd is ["+dPWD+"]\t**");
			System.out.println("**********************************************************");

		}
		catch(Exception e)
		{
			System.out.println("Exception while reading DB.cfg");
			e.printStackTrace();

		}
	}

	public VodaDataProcess()
	{
		try
		{
			readDBCONFIG();
			System.out.println("Initiallizing DB");
			Class.forName("com.mysql.jdbc.Driver");
			con_source = DriverManager.getConnection("jdbc:mysql://"+sIP+"/"+sDSN, sUSR, sPWD);
			con_destination=DriverManager.getConnection("jdbc:mysql://"+dIP+"/"+dDSN, dUSR, dPWD);
			System.out.println("***Database Connection established!***");
			stmt_source = con_source.createStatement();
			stmt_destination = con_destination.createStatement();
			System.out.println("###DB CONNECTION UP FOR BOTH DB###");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void run()
	{
		try{
				String FILEDATE = "";
				String query_date = "select date_format(subdate(now(),"+day+"),'%Y%m%d') as 'FILEDATE'";
				ResultSet Rsdate = stmt_source.executeQuery(query_date);
				while(Rsdate.next())
				{
					FILEDATE = Rsdate.getString("FILEDATE");
					System.out.println("****Running for date :****"+FILEDATE);
				//	Rsdate.close();
				}

				String MSISDN = "";
				String MODE = "";
				String CHARGINGAMOUNT = "";
				String CIRCLE = "";
				String CHARGINGREASON = "";
				String DATE = "";
				String TYPE = "";
				String USER_TYPE = "";
				String SERVICE = "";
				String DATE1 = "";
				String TIME1 = "";

				String str1 = "select count(1) as CNT from tbl_billing_VH1 nolock where date(Date) = date(subdate(now()," + day + ")) and service='VH1Vodafone'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				  if (localResultSet1.next())
				  {
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records for Vh1****");
					  stmt_destination.executeUpdate("delete from tbl_billing_VH1 where date(Date) = date(subdate(now()," + day + ")) and service='VH1Vodafone'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String Vh1_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','VH1Vodafone' SERVICE,date(date_time),time(date_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1307 and sc not like ('5464634P%')";
				ResultSet Rsdnis = stmt_source.executeQuery(Vh1_Success);
				System.out.println("***********Voda VH1 SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis.next())
				{
					try
					{

	            	    MSISDN = Rsdnis.getString("MSISDN");
	                    MODE = Rsdnis.getString("MODE");
	                    CHARGINGAMOUNT = Rsdnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rsdnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rsdnis.getString("CHARGING REASON");
	                    DATE = Rsdnis.getString("DATE");
	                    TYPE = Rsdnis.getString("TYPE");
	                    USER_TYPE = Rsdnis.getString("USER_TYPE");
	                    SERVICE = Rsdnis.getString("SERVICE");
	                    DATE1 = Rsdnis.getString("date(date_time)");
	                    TIME1 = Rsdnis.getString("time(date_time)");

	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_VH1 values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Vh1_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','VH1Vodafone' SERVICE,date(date_time),time(date_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1307 and sc not like ('5464634P%')";
				ResultSet Rs2dnis = stmt_source.executeQuery(Vh1_failure);
				System.out.println("***********Voda VH1 FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs2dnis.next())
				{
					try
					{
	            	    MSISDN = Rs2dnis.getString("MSISDN");
	                    MODE = Rs2dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs2dnis.getString("CHARGING AMOUNT");
	                    if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
	                    CIRCLE = Rs2dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs2dnis.getString("CHARGING REASON");
	                    DATE = Rs2dnis.getString("DATE");
	                    TYPE = Rs2dnis.getString("TYPE");
	                    USER_TYPE = Rs2dnis.getString("USER_TYPE");
	                    SERVICE = Rs2dnis.getString("SERVICE");
	                    DATE1 = Rs2dnis.getString("date(date_time)");
	                    TIME1 = Rs2dnis.getString("time(date_time)");
	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_VH1 values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Vh1_unsub  = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','VH1Vodafone' SERVICE,date(unsub_date),time(unsub_date) from vodafone_vh1.tbl_jbox_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs3dnis = stmt_source.executeQuery(Vh1_unsub);
				System.out.println("***********Voda VH1 UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs3dnis.next())
				{
					try
					{

	            	    MSISDN = Rs3dnis.getString("MSISDN");
	                    MODE = Rs3dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs3dnis.getString("CHARGING AMOUNT");
	                    if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
	                    CIRCLE = Rs3dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs3dnis.getString("CHARGING REASON");
	                    DATE = Rs3dnis.getString("DATE");
	                    TYPE = Rs3dnis.getString("TYPE");
	                    USER_TYPE = Rs3dnis.getString("USER_TYPE");
	                    SERVICE = Rs3dnis.getString("SERVICE");
	                    DATE1 = Rs3dnis.getString("date(unsub_date)");
	                    TIME1 = Rs3dnis.getString("time(unsub_date)");
	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_VH1 values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str2 = "select count(1) as CNT from tbl_billing_54646_vodafone nolock where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				  if (localResultSet2.next())
				  {
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_vodafone where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String Voda54646_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(date_time),time(date_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1302 and sc not like ('5464634P%')";
	            ResultSet Rs4dnis = stmt_source.executeQuery(Voda54646_Success);
				System.out.println("***********VODA 54646 SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs4dnis.next())
				{
					try
					{

						MSISDN = Rs4dnis.getString("MSISDN");
						MODE = Rs4dnis.getString("MODE");
						CHARGINGAMOUNT = Rs4dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs4dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs4dnis.getString("CHARGING REASON");
						DATE = Rs4dnis.getString("DATE");
						TYPE = Rs4dnis.getString("TYPE");
						USER_TYPE = Rs4dnis.getString("USER_TYPE");
						SERVICE = Rs4dnis.getString("SERVICE");
						DATE1 = Rs4dnis.getString("date(date_time)");
						TIME1 = Rs4dnis.getString("time(date_time)");

					//	System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_vodafone values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Voda54646_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(date_time),time(date_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1302 and sc not like ('5464634P%')";
	            ResultSet Rs5dnis = stmt_source.executeQuery(Voda54646_failure);
				System.out.println("***********VODA 54646 FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs5dnis.next())
				{
					try
					{
						MSISDN = Rs5dnis.getString("MSISDN");
						MODE = Rs5dnis.getString("MODE");
						CHARGINGAMOUNT = Rs5dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs5dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs5dnis.getString("CHARGING REASON");
						DATE = Rs5dnis.getString("DATE");
						TYPE = Rs5dnis.getString("TYPE");
						USER_TYPE = Rs5dnis.getString("USER_TYPE");
						SERVICE = Rs5dnis.getString("SERVICE");
						DATE1 = Rs5dnis.getString("date(date_time)");
						TIME1 = Rs5dnis.getString("time(date_time)");

					//	System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_vodafone values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Voda54646_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from vodafone_hungama.tbl_jbox_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs6dnis = stmt_source.executeQuery(Voda54646_unsub);
				System.out.println("***********VODA 54646 UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs6dnis.next())
				{
					try
					{
						MSISDN = Rs6dnis.getString("MSISDN");
						MODE = Rs6dnis.getString("MODE");
						CHARGINGAMOUNT = Rs6dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs6dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs6dnis.getString("CHARGING REASON");
						DATE = Rs6dnis.getString("DATE");
						TYPE = Rs6dnis.getString("TYPE");
						USER_TYPE = Rs6dnis.getString("USER_TYPE");
						SERVICE = Rs6dnis.getString("SERVICE");
						DATE1 = Rs6dnis.getString("date(unsub_date)");
						TIME1 = Rs6dnis.getString("time(unsub_date)");

					//	System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_vodafone values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str3 = "select count(1) as CNT from Tbl_billing_redfm nolock where date(Date) = date(subdate(now()," + day + ")) and service='RedFMVodafone'";
				ResultSet localResultSet3 = stmt_destination.executeQuery(str3);
				if (localResultSet3.next())
				{
					i = localResultSet3.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet3.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from Tbl_billing_redfm where date(Date) = date(subdate(now()," + day + ")) and service='RedFMVodafone'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String RFM_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','RedFMvodafone' SERVICE,date(date_time),time(date_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1310";
				ResultSet Rs13dnis = stmt_source.executeQuery(RFM_Success);
				System.out.println("***********VODA RED FM SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs13dnis.next())
				{
					try
					{
						MSISDN = Rs13dnis.getString("MSISDN");
						MODE = Rs13dnis.getString("MODE");
						CHARGINGAMOUNT = Rs13dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs13dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs13dnis.getString("CHARGING REASON");
						DATE = Rs13dnis.getString("DATE");
						TYPE = Rs13dnis.getString("TYPE");
						USER_TYPE = Rs13dnis.getString("USER_TYPE");
						SERVICE = Rs13dnis.getString("SERVICE");
						DATE1 = Rs13dnis.getString("date(date_time)");
						TIME1 = Rs13dnis.getString("time(date_time)");
					//	System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into Tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String RFM_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','RedFMvodafone' SERVICE,date(date_time),time(date_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1310";
				ResultSet Rs14dnis = stmt_source.executeQuery(RFM_failure);
				System.out.println("***********VODA RED FM FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs14dnis.next())
				{
					try
					{
						MSISDN = Rs14dnis.getString("MSISDN");
						MODE = Rs14dnis.getString("MODE");
						CHARGINGAMOUNT = Rs14dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs14dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs14dnis.getString("CHARGING REASON");
						DATE = Rs14dnis.getString("DATE");
						TYPE = Rs14dnis.getString("TYPE");
						USER_TYPE = Rs14dnis.getString("USER_TYPE");
						SERVICE = Rs14dnis.getString("SERVICE");
						DATE1 = Rs14dnis.getString("date(date_time)");
						TIME1 = Rs14dnis.getString("time(date_time)");
					//	System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into Tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String RFM_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','RedFMvodafone' SERVICE,date(unsub_date),time(unsub_date) from vodafone_redfm.tbl_jbox_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs15dnis = stmt_source.executeQuery(RFM_unsub);
				System.out.println("***********VODA RED FM UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs15dnis.next())
				{
					try
					{
						MSISDN = Rs15dnis.getString("MSISDN");
						MODE = Rs15dnis.getString("MODE");
						CHARGINGAMOUNT = Rs15dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs15dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs15dnis.getString("CHARGING REASON");
						DATE = Rs15dnis.getString("DATE");
						TYPE = Rs15dnis.getString("TYPE");
						USER_TYPE = Rs15dnis.getString("USER_TYPE");
						SERVICE = Rs15dnis.getString("SERVICE");
						DATE1 = Rs15dnis.getString("date(unsub_date)");
						TIME1 = Rs15dnis.getString("time(unsub_date)");
					//	System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into Tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }

				stmt_destination.executeUpdate("update Tbl_billing_redfm set mode='IVR' where service='RedFMVodafone' and date(Date) = date(subdate(now(),"+day+")) and mode REGEXP '^-?[0-9]+$'");
				stmt_destination.executeUpdate("update Tbl_billing_redfm set `CHARGING REASON`='33' where service='RedFMVodafone' and date(Date) = date(subdate(now(),"+day+")) and (`CHARGING REASON` is NULL or`CHARGING REASON`='NA')");
				stmt_destination.executeUpdate("update Tbl_billing_redfm set `USER TYPE`='NA' where service='RedFMVodafone' and date(Date) = date(subdate(now(),"+day+")) and (`USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL)");

				stmt_destination.executeUpdate("update Tbl_billing_54646_vodafone set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update Tbl_billing_54646_vodafone set `CHARGING REASON`='33' where  (`CHARGING REASON` is NULL or`CHARGING REASON`='NA') and date(Date) = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update Tbl_billing_54646_vodafone set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL) and date(Date) = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update Tbl_billing_VH1 set mode='IVR' where mode REGEXP '^-?[0-9]+$' and service='VH1Vodafone' and date(Date) = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update Tbl_billing_VH1 set `CHARGING REASON`='33' where  service='VH1Vodafone' and date(Date) = date(subdate(now(),"+day+")) and (`CHARGING REASON` is NULL or`CHARGING REASON`='NA')");
				stmt_destination.executeUpdate("update Tbl_billing_VH1 set `USER TYPE`='NA' where service='VH1Vodafone' and date(Date) = date(subdate(now(),"+day+")) and (`USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL)");

				System.out.println("****Updations done closing connections****");

				stmt_source.close();
				stmt_destination.close();
				con_source.close();
				con_destination.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
			        // The finally clause is always executed - even in error
			        // conditions PreparedStatements and Connections will always be closed
			        try
			        {
			        	if (con_source != null)
			            	con_source.close();
			        }
			        catch(Exception e) {}

			        try
			        {
			        	if (con_destination != null)
			            	con_destination.close();
			        }
			        catch (Exception e){}
			  }
	}
	public String getCIRCLE(String CIR)
	{
		String c;
		if("APD".equalsIgnoreCase(CIR))
			c = "Andhra Pradesh";
		else if("ASM".equalsIgnoreCase(CIR))
			c= "Assam";
		else if("BIH".equalsIgnoreCase(CIR))
			c= "Bihar";
		else if("CHN".equalsIgnoreCase(CIR))
			c = "Chennai";
		else if("DEL".equalsIgnoreCase(CIR))
			c = "Delhi";
		else if("GUJ".equalsIgnoreCase(CIR))
			c = "Gujarat";
		else if("HAY".equalsIgnoreCase(CIR)|| "HAR".equalsIgnoreCase(CIR))
			c = "Haryana";
		else if("HPD".equalsIgnoreCase(CIR))
			c = "Himachal Pradesh";
		else if("JNK".equalsIgnoreCase(CIR))
			c = "Jammu-Kashmir";
		else if("KAR".equalsIgnoreCase(CIR))
			c = "Karnataka";
		else if("KER".equalsIgnoreCase(CIR))
			c = "Kerala";
		else if("KOL".equalsIgnoreCase(CIR))
			c = "Kolkata";
		else if("MPD".equalsIgnoreCase(CIR))
			c = "Madhya Pradesh";
		else if("MAH".equalsIgnoreCase(CIR))
			c = "Maharashtra";
		else if("MUM".equalsIgnoreCase(CIR))
			c = "Mumbai";
		else if("NES".equalsIgnoreCase(CIR))
			c = "NE";
		else if("ORI".equalsIgnoreCase(CIR))
			c = "Orissa";
		else if("PUB".equalsIgnoreCase(CIR)|| "PUN".equalsIgnoreCase(CIR))
			c = "Punjab";
		else if("RAJ".equalsIgnoreCase(CIR))
			c = "Rajasthan";
		else if("TNU".equalsIgnoreCase(CIR))
			c = "Tamil Nadu";
		else if("UPE".equalsIgnoreCase(CIR))
			c = "UP EAST";
		else if("UPW".equalsIgnoreCase(CIR))
			c = "UP WEST";
		else if("WBL".equalsIgnoreCase(CIR))
			c = "WestBengal";
		else
			c = "Others";
		return c;
	}
	public static void main(String arg[])
	{
		if(arg.length>0)
			day = Integer.parseInt(arg[0]);
		else
			day = 1;
		VodaDataProcess c = new VodaDataProcess();
		c.start();

	}

}
