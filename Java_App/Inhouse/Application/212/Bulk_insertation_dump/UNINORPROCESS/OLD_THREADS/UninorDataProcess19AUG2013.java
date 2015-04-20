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


	public class UninorDataProcess extends Thread
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

			sIP="192.168.100.224";
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

	public UninorDataProcess()
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
				//day=1;
				String FILEDATE = "";
				String query_date = "select date_format(adddate(now(),-"+day+"),'%Y%m%d') as 'FILEDATE'";

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

				ResultSet Rsdate = stmt_source.executeQuery(query_date);
				while(Rsdate.next())
				{
						FILEDATE = Rsdate.getString("FILEDATE");
						System.out.println("****Running for date :****"+FILEDATE);
				}
				String str1 = "select count(1) as CNT from tbl_billing_astro_uninor where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_astro_uninor where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String Jyotish_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1416";
				ResultSet Rsdnis = stmt_source.executeQuery(Jyotish_Success);
				System.out.println("***********UNINOR JYOTISH SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
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
	                    DATE1 = Rsdnis.getString("date(response_time)");
	                    TIME1 = Rsdnis.getString("time(response_time)");

	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_astro_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Jyotish_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1416";
				ResultSet Rs2dnis = stmt_source.executeQuery(Jyotish_failure);
				System.out.println("***********UNINOR JYOTISH FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs2dnis.next())
				{
					try
					{
	            	    MSISDN = Rs2dnis.getString("MSISDN");
	                    MODE = Rs2dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs2dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs2dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs2dnis.getString("CHARGING REASON");
	                    DATE = Rs2dnis.getString("DATE");
	                    TYPE = Rs2dnis.getString("TYPE");
	                    USER_TYPE = Rs2dnis.getString("USER_TYPE");
	                    SERVICE = Rs2dnis.getString("SERVICE");
	                    DATE1 = Rs2dnis.getString("date(response_time)");
	                    TIME1 = Rs2dnis.getString("time(response_time)");
	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_astro_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Jyotish_unsub  = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from uninor_jyotish.tbl_Jyotish_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs3dnis = stmt_source.executeQuery(Jyotish_unsub);
				System.out.println("***********UNINOR JYOTISH UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs3dnis.next())
				{
					try
					{

	            	    MSISDN = Rs3dnis.getString("MSISDN");
	                    MODE = Rs3dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs3dnis.getString("CHARGING AMOUNT");
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
						stmt_destination.executeUpdate("insert into tbl_billing_astro_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str2 = "select count(1) as CNT from tbl_billing_54646_uninor where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				if (localResultSet2.next())
				{
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_uninor where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String Hun54646_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1402 and Plan_id not in(95) and sc not like ('5464634P%')";
	            ResultSet Rs4dnis = stmt_source.executeQuery(Hun54646_Success);
				System.out.println("***********UNINOR 54646 SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
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
						DATE1 = Rs4dnis.getString("date(response_time)");
						TIME1 = Rs4dnis.getString("time(response_time)");

			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Hun54646_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1402 and Plan_id not in(95) and sc not like ('5464634P%')";
	            ResultSet Rs5dnis = stmt_source.executeQuery(Hun54646_failure);
				System.out.println("***********UNINOR 54646 FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs5dnis.next())
				{
					try
					{
						MSISDN = Rs5dnis.getString("MSISDN");
						MODE = Rs5dnis.getString("MODE");
						CHARGINGAMOUNT = Rs5dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs5dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs5dnis.getString("CHARGING REASON");
						DATE = Rs5dnis.getString("DATE");
						TYPE = Rs5dnis.getString("TYPE");
						USER_TYPE = Rs5dnis.getString("USER_TYPE");
						SERVICE = Rs5dnis.getString("SERVICE");
						DATE1 = Rs5dnis.getString("date(response_time)");
						TIME1 = Rs5dnis.getString("time(response_time)");

			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Hun54646_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from uninor_hungama.tbl_jbox_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id not in(95)";
				ResultSet Rs6dnis = stmt_source.executeQuery(Hun54646_unsub);
				System.out.println("***********UNINOR 54646 UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs6dnis.next())
				{
					try
					{
						MSISDN = Rs6dnis.getString("MSISDN");
						MODE = Rs6dnis.getString("MODE");
						CHARGINGAMOUNT = Rs6dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs6dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs6dnis.getString("CHARGING REASON");
						DATE = Rs6dnis.getString("DATE");
						TYPE = Rs6dnis.getString("TYPE");
						USER_TYPE = Rs6dnis.getString("USER_TYPE");
						SERVICE = Rs6dnis.getString("SERVICE");
						DATE1 = Rs6dnis.getString("date(unsub_date)");
						TIME1 = Rs6dnis.getString("time(unsub_date)");

			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str3 = "select count(1) as CNT from tbl_billing_rt_uninor where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet3 = stmt_destination.executeQuery(str3);
				if (localResultSet3.next())
				{
					i = localResultSet3.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet3.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_rt_uninor where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String Ring_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case plan_id when '69' then 'Polytunes' when '70' then 'Monotones' when '71' then 'TrueTones' else 'others' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1412";
				ResultSet Rs7dnis = stmt_source.executeQuery(Ring_Success);
				System.out.println("***********UNINOR RING Success TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs7dnis.next())
				{
					try
					{
						MSISDN = Rs7dnis.getString("MSISDN");
						MODE = Rs7dnis.getString("MODE");
						CHARGINGAMOUNT = Rs7dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs7dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs7dnis.getString("CHARGING REASON");
						DATE = Rs7dnis.getString("DATE");
						TYPE = Rs7dnis.getString("TYPE");
						USER_TYPE = Rs7dnis.getString("USER_TYPE");
						SERVICE = Rs7dnis.getString("SERVICE");
						DATE1 = Rs7dnis.getString("date(response_time)");
						TIME1 = Rs7dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_rt_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Ring_Failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case plan_id when '69' then 'Polytunes' when '70' then 'Monotones' when '71' then 'TrueTones' else 'others' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1412";
				ResultSet Rs8dnis = stmt_source.executeQuery(Ring_Failure);
				System.out.println("***********UNINOR RING FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs8dnis.next())
				{
					try
					{
						MSISDN = Rs8dnis.getString("MSISDN");
						MODE = Rs8dnis.getString("MODE");
						CHARGINGAMOUNT = Rs8dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs8dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs8dnis.getString("CHARGING REASON");
						DATE = Rs8dnis.getString("DATE");
						TYPE = Rs8dnis.getString("TYPE");
						USER_TYPE = Rs8dnis.getString("USER_TYPE");
						SERVICE = Rs8dnis.getString("SERVICE");
						DATE1 = Rs8dnis.getString("date(response_time)");
						TIME1 = Rs8dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_rt_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str4 = "select count(1) as CNT from tbl_billing_aa_uninor where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet4 = stmt_destination.executeQuery(str4);
				if (localResultSet4.next())
				{
					i = localResultSet4.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet4.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_aa_uninor where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String AA_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1402 and Plan_id in(95)";
				ResultSet Rs9dnis = stmt_source.executeQuery(AA_Succ);
				System.out.println("***********UNINOR ARTIST ALOUD SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs9dnis.next())
				{
					try
					{
						MSISDN = Rs9dnis.getString("MSISDN");
						MODE = Rs9dnis.getString("MODE");
						CHARGINGAMOUNT = Rs9dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs9dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs9dnis.getString("CHARGING REASON");
						DATE = Rs9dnis.getString("DATE");
						TYPE = Rs9dnis.getString("TYPE");
						USER_TYPE = Rs9dnis.getString("USER_TYPE");
						SERVICE = Rs9dnis.getString("SERVICE");
						DATE1 = Rs9dnis.getString("date(response_time)");
						TIME1 = Rs9dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_aa_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String AA_fail = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1402 and Plan_id in(95)";
				ResultSet Rs10dnis = stmt_source.executeQuery(AA_fail);
				System.out.println("***********UNINOR ARTIST ALOUD FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs10dnis.next())
				{
					try
					{
						MSISDN = Rs10dnis.getString("MSISDN");
						MODE = Rs10dnis.getString("MODE");
						CHARGINGAMOUNT = Rs10dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs10dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs10dnis.getString("CHARGING REASON");
						DATE = Rs10dnis.getString("DATE");
						TYPE = Rs10dnis.getString("TYPE");
						USER_TYPE = Rs10dnis.getString("USER_TYPE");
						SERVICE = Rs10dnis.getString("SERVICE");
						DATE1 = Rs10dnis.getString("date(response_time)");
						TIME1 = Rs10dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_aa_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String AA_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from uninor_hungama.tbl_jbox_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id in(95)";
				ResultSet Rs11dnis = stmt_source.executeQuery(AA_unsub);
				System.out.println("***********UNINOR ARTIST ALOUD UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs11dnis.next())
				{
					try
					{
						MSISDN = Rs11dnis.getString("MSISDN");
						MODE = Rs11dnis.getString("MODE");
						CHARGINGAMOUNT = Rs11dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs11dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs11dnis.getString("CHARGING REASON");
						DATE = Rs11dnis.getString("DATE");
						TYPE = Rs11dnis.getString("TYPE");
						USER_TYPE = Rs11dnis.getString("USER_TYPE");
						SERVICE = Rs11dnis.getString("SERVICE");
						DATE1 = Rs11dnis.getString("date(unsub_date)");
						TIME1 = Rs11dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_aa_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str5 = "select count(1) as CNT from tbl_billing_redfm where date(Date) = date(subdate(now()," + day + ")) and service in('RedFMUninor','WAPREDFMUninor')";
				ResultSet localResultSet5 = stmt_destination.executeQuery(str5);
				if (localResultSet5.next())
				{
					i = localResultSet5.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet5.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_redfm where date(Date) = date(subdate(now()," + day + ")) and service in('RedFMUninor','WAPREDFMUninor')");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String RFM_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','RedFMUninor' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1410";
				ResultSet Rs13dnis = stmt_source.executeQuery(RFM_Success);
				System.out.println("***********UNINOR RED FM SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs13dnis.next())
				{
					try
					{
						MSISDN = Rs13dnis.getString("MSISDN");
						MODE = Rs13dnis.getString("MODE");
						CHARGINGAMOUNT = Rs13dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs13dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs13dnis.getString("CHARGING REASON");
						DATE = Rs13dnis.getString("DATE");
						TYPE = Rs13dnis.getString("TYPE");
						USER_TYPE = Rs13dnis.getString("USER_TYPE");
						SERVICE = Rs13dnis.getString("SERVICE");
						DATE1 = Rs13dnis.getString("date(response_time)");
						TIME1 = Rs13dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String RFM_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','RedFMUninor' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1410";
				ResultSet Rs14dnis = stmt_source.executeQuery(RFM_failure);
				System.out.println("***********UNINOR RED FM FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs14dnis.next())
				{
					try
					{
						MSISDN = Rs14dnis.getString("MSISDN");
						MODE = Rs14dnis.getString("MODE");
						CHARGINGAMOUNT = Rs14dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs14dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs14dnis.getString("CHARGING REASON");
						DATE = Rs14dnis.getString("DATE");
						TYPE = Rs14dnis.getString("TYPE");
						USER_TYPE = Rs14dnis.getString("USER_TYPE");
						SERVICE = Rs14dnis.getString("SERVICE");
						DATE1 = Rs14dnis.getString("date(response_time)");
						TIME1 = Rs14dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String RFM_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','RedFMUninor' SERVICE,date(unsub_date),time(unsub_date) from uninor_redfm.tbl_jbox_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs15dnis = stmt_source.executeQuery(RFM_unsub);
				System.out.println("***********UNINOR RED FM UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs15dnis.next())
				{
					try
					{
						MSISDN = Rs15dnis.getString("MSISDN");
						MODE = Rs15dnis.getString("MODE");
						CHARGINGAMOUNT = Rs15dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs15dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs15dnis.getString("CHARGING REASON");
						DATE = Rs15dnis.getString("DATE");
						TYPE = Rs15dnis.getString("TYPE");
						USER_TYPE = Rs15dnis.getString("USER_TYPE");
						SERVICE = Rs15dnis.getString("SERVICE");
						DATE1 = Rs15dnis.getString("date(unsub_date)");
						TIME1 = Rs15dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str6 = "select count(1) as CNT from tbl_billing_ria where date(Date) = date(subdate(now()," + day + ")) and service='RiaUninor'";
				ResultSet localResultSet6 = stmt_destination.executeQuery(str6);
				if (localResultSet6.next())
				{
					i = localResultSet6.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet6.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_ria where date(Date) = date(subdate(now()," + day + ")) and service='RiaUninor'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String Riya_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','RIAUninor' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1409";
				ResultSet Rs16dnis = stmt_source.executeQuery(Riya_Success);
				System.out.println("***********UNINOR RIYA SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs16dnis.next())
				{
					try
					{
						MSISDN = Rs16dnis.getString("MSISDN");
						MODE = Rs16dnis.getString("MODE");
						CHARGINGAMOUNT = Rs16dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs16dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs16dnis.getString("CHARGING REASON");
						DATE = Rs16dnis.getString("DATE");
						TYPE = Rs16dnis.getString("TYPE");
						USER_TYPE = Rs16dnis.getString("USER_TYPE");
						SERVICE = Rs16dnis.getString("SERVICE");
						DATE1 = Rs16dnis.getString("date(response_time)");
						TIME1 = Rs16dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ria values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Riya_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','RIAUninor' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1409";
				ResultSet Rs17dnis = stmt_source.executeQuery(Riya_failure);
				System.out.println("***********UNINOR RIYA FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs17dnis.next())
				{
					try
					{
						MSISDN = Rs17dnis.getString("MSISDN");
						MODE = Rs17dnis.getString("MODE");
						CHARGINGAMOUNT = Rs17dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs17dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs17dnis.getString("CHARGING REASON");
						DATE = Rs17dnis.getString("DATE");
						TYPE = Rs17dnis.getString("TYPE");
						USER_TYPE = Rs17dnis.getString("USER_TYPE");
						SERVICE = Rs17dnis.getString("SERVICE");
						DATE1 = Rs17dnis.getString("date(response_time)");
						TIME1 = Rs17dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ria values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Riya_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','RIAUninor' SERVICE,date(unsub_date),time(unsub_date) from uninor_manchala.tbl_riya_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs18dnis = stmt_source.executeQuery(Riya_unsub);
				System.out.println("***********UNINOR RIYA UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs18dnis.next())
				{
					try
					{
						MSISDN = Rs18dnis.getString("MSISDN");
						MODE = Rs18dnis.getString("MODE");
						CHARGINGAMOUNT = Rs18dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs18dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs18dnis.getString("CHARGING REASON");
						DATE = Rs18dnis.getString("DATE");
						TYPE = Rs18dnis.getString("TYPE");
						USER_TYPE = Rs18dnis.getString("USER_TYPE");
						SERVICE = Rs18dnis.getString("SERVICE");
						DATE1 = Rs18dnis.getString("date(unsub_date)");
						TIME1 = Rs18dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ria values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str7 = "select count(1) as CNT from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVUninor'";
				ResultSet localResultSet7 = stmt_destination.executeQuery(str7);
				if (localResultSet7.next())
				{
					i = localResultSet7.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet7.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVUninor'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String MTV_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','MTVUninor' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1403";
				ResultSet Rs19dnis = stmt_source.executeQuery(MTV_Success);
				System.out.println("***********UNINOR MTV SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs19dnis.next())
				{
					try
					{
						MSISDN = Rs19dnis.getString("MSISDN");
						MODE = Rs19dnis.getString("MODE");
						CHARGINGAMOUNT = Rs19dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs19dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs19dnis.getString("CHARGING REASON");
						DATE = Rs19dnis.getString("DATE");
						TYPE = Rs19dnis.getString("TYPE");
						USER_TYPE = Rs19dnis.getString("USER_TYPE");
						SERVICE = Rs19dnis.getString("SERVICE");
						DATE1 = Rs19dnis.getString("date(response_time)");
						TIME1 = Rs19dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mtv values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String MTV_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','MTVUninor' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1403";
				ResultSet Rs20dnis = stmt_source.executeQuery(MTV_failure);
				System.out.println("***********UNINOR MTV FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs20dnis.next())
				{
					try
					{
						MSISDN = Rs20dnis.getString("MSISDN");
						MODE = Rs20dnis.getString("MODE");
						CHARGINGAMOUNT = Rs20dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs20dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs20dnis.getString("CHARGING REASON");
						DATE = Rs20dnis.getString("DATE");
						TYPE = Rs20dnis.getString("TYPE");
						USER_TYPE = Rs20dnis.getString("USER_TYPE");
						SERVICE = Rs20dnis.getString("SERVICE");
						DATE1 = Rs20dnis.getString("date(response_time)");
						TIME1 = Rs20dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mtv values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String MTV_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','MTVUninor' SERVICE,date(unsub_date),time(unsub_date) from uninor_hungama.tbl_mtv_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs21dnis = stmt_source.executeQuery(MTV_unsub);
				System.out.println("***********UNINOR MTV UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs21dnis.next())
				{
					try
					{
						MSISDN = Rs21dnis.getString("MSISDN");
						MODE = Rs21dnis.getString("MODE");
						CHARGINGAMOUNT = Rs21dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs21dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs21dnis.getString("CHARGING REASON");
						DATE = Rs21dnis.getString("DATE");
						TYPE = Rs21dnis.getString("TYPE");
						USER_TYPE = Rs21dnis.getString("USER_TYPE");
						SERVICE = Rs21dnis.getString("SERVICE");
						DATE1 = Rs21dnis.getString("date(unsub_date)");
						TIME1 = Rs21dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mtv values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str8 = "select count(1) as CNT from tbl_billing_cri_Uninor where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet8 = stmt_destination.executeQuery(str8);
				if (localResultSet8.next())
				{
					i = localResultSet8.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet8.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_cri_Uninor where date(Date) = date(subdate(now()," + day + ")) ");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String Sport_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1408";
	            ResultSet Rs22dnis = stmt_source.executeQuery(Sport_Succ);
				System.out.println("***********UNINOR SPORTS SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs22dnis.next())
				{
					try
					{
						MSISDN = Rs22dnis.getString("MSISDN");
						MODE = Rs22dnis.getString("MODE");
						CHARGINGAMOUNT = Rs22dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs22dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs22dnis.getString("CHARGING REASON");
						DATE = Rs22dnis.getString("DATE");
						TYPE = Rs22dnis.getString("TYPE");
						USER_TYPE = Rs22dnis.getString("USER_TYPE");
						SERVICE = Rs22dnis.getString("SERVICE");
						DATE1 = Rs22dnis.getString("date(response_time)");
						TIME1 = Rs22dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_cri_Uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Sport_fail = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1408";
	            ResultSet Rs23dnis = stmt_source.executeQuery(Sport_fail);
				System.out.println("***********UNINOR SPORTS FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs23dnis.next())
				{
					try
					{
						MSISDN = Rs23dnis.getString("MSISDN");
						MODE = Rs23dnis.getString("MODE");
						CHARGINGAMOUNT = Rs23dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs23dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs23dnis.getString("CHARGING REASON");
						DATE = Rs23dnis.getString("DATE");
						TYPE = Rs23dnis.getString("TYPE");
						USER_TYPE = Rs23dnis.getString("USER_TYPE");
						SERVICE = Rs23dnis.getString("SERVICE");
						DATE1 = Rs23dnis.getString("date(response_time)");
						TIME1 = Rs23dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_cri_Uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Sport_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from uninor_cricket.tbl_cricket_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs24dnis = stmt_source.executeQuery(Sport_unsub);
				System.out.println("***********UNINOR SPORTS UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs24dnis.next())
				{
					try
					{
						MSISDN = Rs24dnis.getString("MSISDN");
						MODE = Rs24dnis.getString("MODE");
						CHARGINGAMOUNT = Rs24dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs24dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs24dnis.getString("CHARGING REASON");
						DATE = Rs24dnis.getString("DATE");
						TYPE = Rs24dnis.getString("TYPE");
						USER_TYPE = Rs24dnis.getString("USER_TYPE");
						SERVICE = Rs24dnis.getString("SERVICE");
						DATE1 = Rs24dnis.getString("date(unsub_date)");
						TIME1 = Rs24dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_cri_Uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String str9 = "select count(1) as CNT from tbl_billing_comedy where date(Date) = date(subdate(now()," + day + ")) and service='UninorComedy'";
				ResultSet localResultSet9 = stmt_destination.executeQuery(str9);
				if (localResultSet9.next())
				{
					i = localResultSet9.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet9.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_comedy where date(Date) = date(subdate(now()," + day + ")) and service='UninorComedy' ");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String Comedy_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','UninorComedy' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1418";
	            ResultSet Rs25dnis = stmt_source.executeQuery(Comedy_Succ);
				System.out.println("***********UNINOR COMEDY SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs25dnis.next())
				{
					try
					{
						MSISDN = Rs25dnis.getString("MSISDN");
						MODE = Rs25dnis.getString("MODE");
						CHARGINGAMOUNT = Rs25dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs25dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs25dnis.getString("CHARGING REASON");
						DATE = Rs25dnis.getString("DATE");
						TYPE = Rs25dnis.getString("TYPE");
						USER_TYPE = Rs25dnis.getString("USER_TYPE");
						SERVICE = Rs25dnis.getString("SERVICE");
						DATE1 = Rs25dnis.getString("date(response_time)");
						TIME1 = Rs25dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Comedy_fail = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','UninorComedy' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1418";
	            ResultSet Rs26dnis = stmt_source.executeQuery(Comedy_fail);
				System.out.println("***********UNINOR COMEDY FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs26dnis.next())
				{
					try
					{
						MSISDN = Rs26dnis.getString("MSISDN");
						MODE = Rs26dnis.getString("MODE");
						CHARGINGAMOUNT = Rs26dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs26dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs26dnis.getString("CHARGING REASON");
						DATE = Rs26dnis.getString("DATE");
						TYPE = Rs26dnis.getString("TYPE");
						USER_TYPE = Rs26dnis.getString("USER_TYPE");
						SERVICE = Rs26dnis.getString("SERVICE");
						DATE1 = Rs26dnis.getString("date(response_time)");
						TIME1 = Rs26dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Comedy_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','UninorComedy' SERVICE,date(unsub_date),time(unsub_date) from uninor_hungama.tbl_comedy_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs27dnis = stmt_source.executeQuery(Comedy_unsub);
				System.out.println("***********UNINOR COMEDY UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs27dnis.next())
				{
					try
					{
						MSISDN = Rs27dnis.getString("MSISDN");
						MODE = Rs27dnis.getString("MODE");
						CHARGINGAMOUNT = Rs27dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs27dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs27dnis.getString("CHARGING REASON");
						DATE = Rs27dnis.getString("DATE");
						TYPE = Rs27dnis.getString("TYPE");
						USER_TYPE = Rs27dnis.getString("USER_TYPE");
						SERVICE = Rs27dnis.getString("SERVICE");
						DATE1 = Rs27dnis.getString("date(unsub_date)");
						TIME1 = Rs27dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }

	            System.out.println("****Updations is Begining********");

	            System.out.println("update tbl_billing_redfm set service='WAPREDFMUninor' where  TYPE='EVENT'  and service='RedFMUninor' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_redfm set service='WAPREDFMUninor' where  TYPE='EVENT'  and service='RedFMUninor' and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_rt_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_rt_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_rt_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_mtv set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_mtv set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_mtv set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_ria set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_ria set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_ria set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_redfm set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_redfm set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_redfm set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL)  and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_astro_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_astro_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_astro_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_54646_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_54646_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_54646_uninor set `USER TYPE`='NA' where ( `USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_cri_Uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_cri_Uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_cri_Uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_aa_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_aa_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_aa_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_comedy set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_comedy set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_comedy set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

				System.out.println("****Updations done closing connections**** Good bye See u again****");

				stmt_source.close();
				stmt_destination.close();

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
		else if("PUB".equalsIgnoreCase(CIR)||"PUN".equalsIgnoreCase(CIR))
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
		UninorDataProcess c = new UninorDataProcess();
		c.start();

	}

}
