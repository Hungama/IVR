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

			dIP="192.168.100.224";
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
	/*******************MAKE TEST FILES********************/
		public void Fun_FileCreator(int Day)
		{
				try
				{
					CallableStatement cstmtfm1 = null;
					cstmtfm1 = con_destination.prepareCall("{call misdata.Uninor_Billing_Data_All(?)}");
					cstmtfm1.setInt(1,Day);
					cstmtfm1.execute();
					cstmtfm1.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	/*******************MAKE TEST FILES END********************/

	public void run()
	{
		try{
				//day=1;
				String FILEDATE = "";
				String BillingTable = "master_db.tbl_billing_success";
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
				String RETRY_TYPE = "";
				String AFFID = "";

				if (day > 2)
					BillingTable = "master_db.tbl_billing_success_backup";
				else
					BillingTable = "master_db.tbl_billing_success";
				System.out.println("****Billing table is :****"+BillingTable);

				ResultSet Rsdate = stmt_source.executeQuery(query_date);
				while(Rsdate.next())
				{
						FILEDATE = Rsdate.getString("FILEDATE");
						System.out.println("****Running for date :****"+FILEDATE);
				}
				stmt_destination.executeUpdate("truncate table tbl_billing_astro_uninor");
				String Jyotish_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from "+BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1416";
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
	                    TIME1 = Rsdnis.getString("ccg_id");

	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_astro_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Jyotish_failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1416";
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
	                    if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
	                    USER_TYPE = Rs2dnis.getString("USER_TYPE");
	                    SERVICE = Rs2dnis.getString("SERVICE");
	                    DATE1 = Rs2dnis.getString("date(response_time)");
        				TIME1 = Rs2dnis.getString("ccg_id");

						RETRY_TYPE=Rs2dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

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
	                    TIME1 = "X";
	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_astro_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_54646_uninor");
				String Hun54646_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1402 and Plan_id not in(95) and sc not like ('5464634P%') and event_type not in('Recharge Failed','Recharged')";
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
						TIME1 = Rs4dnis.getString("ccg_id");
						AFFID = Rs4dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Hun54646_failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1402 and Plan_id not in(95) and sc not like ('5464634P%')";
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
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs5dnis.getString("USER_TYPE");
						SERVICE = Rs5dnis.getString("SERVICE");
						DATE1 = Rs5dnis.getString("date(response_time)");
						TIME1 = Rs5dnis.getString("ccg_id");
						AFFID = Rs5dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;

						RETRY_TYPE=Rs5dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

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
						TIME1 = "X";

			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_rt_uninor");
				String Ring_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE','EVENT' as 'TYPE',case plan_id when '69' then 'Polytunes' when '70' then 'Monotones' when '71' then 'TrueTones' else 'others' end 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1412";
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
						TIME1 = Rs7dnis.getString("ccg_id");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_rt_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Ring_Failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE','EVENT' as 'TYPE',case plan_id when '69' then 'Polytunes' when '70' then 'Monotones' when '71' then 'TrueTones' else 'others' end 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1412";
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
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs8dnis.getString("USER_TYPE");
						SERVICE = Rs8dnis.getString("SERVICE");
						DATE1 = Rs8dnis.getString("date(response_time)");
						TIME1 = Rs8dnis.getString("ccg_id");

						RETRY_TYPE=Rs8dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_rt_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_aa_uninor");
				String AA_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1402 and Plan_id in(95)";
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
						TIME1 = Rs9dnis.getString("ccg_id");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_aa_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String AA_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1402 and Plan_id in(95)";
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
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs10dnis.getString("USER_TYPE");
						SERVICE = Rs10dnis.getString("SERVICE");
						DATE1 = Rs10dnis.getString("date(response_time)");
						TIME1 = Rs10dnis.getString("ccg_id");

						RETRY_TYPE=Rs10dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

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
						TIME1 = "X";
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_aa_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_redfm_uninor");
				String RFM_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','RedFMUninor' SERVICE,date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1410";
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
						TIME1 = Rs13dnis.getString("ccg_id");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_redfm_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String RFM_failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','RedFMUninor' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1410";
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
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs14dnis.getString("USER_TYPE");
						SERVICE = Rs14dnis.getString("SERVICE");
						DATE1 = Rs14dnis.getString("date(response_time)");
						TIME1 = Rs14dnis.getString("ccg_id");

						RETRY_TYPE=Rs14dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_redfm_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
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
						TIME1 = "X";
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_redfm_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_ria_uninor");
				String Riya_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','RIAUninor' SERVICE,date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1413";
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
						TIME1 = Rs16dnis.getString("ccg_id");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ria_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Riya_failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','RIAUninor' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1413";
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
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs17dnis.getString("USER_TYPE");
						SERVICE = Rs17dnis.getString("SERVICE");
						DATE1 = Rs17dnis.getString("date(response_time)");
						TIME1 = Rs17dnis.getString("ccg_id");

						RETRY_TYPE=Rs17dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_ria_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Riya_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','RIAUninor' SERVICE,date(unsub_date),time(unsub_date) from uninor_mnd.tbl_character_unsub1 nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
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
						TIME1 = "X";
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ria_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_mtv_uninor");
				String MTV_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','MTVUninor' SERVICE,date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1403";
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
						TIME1 = Rs19dnis.getString("ccg_id");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mtv_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String MTV_failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','MTVUninor' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1403";
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
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs20dnis.getString("USER_TYPE");
						SERVICE = Rs20dnis.getString("SERVICE");
						DATE1 = Rs20dnis.getString("date(response_time)");
						TIME1 = Rs20dnis.getString("ccg_id");

						RETRY_TYPE=Rs20dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_mtv_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
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
						TIME1 = "X";
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mtv_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_cri_uninor");
				String Sport_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1408  and event_type not in('Recharge Failed','Recharged')";
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
						TIME1 = Rs22dnis.getString("ccg_id");
						AFFID = Rs22dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_cri_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Sport_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1408";
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
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs23dnis.getString("USER_TYPE");
						SERVICE = Rs23dnis.getString("SERVICE");
						DATE1 = Rs23dnis.getString("date(response_time)");
						TIME1 = Rs23dnis.getString("ccg_id");
						AFFID = Rs23dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;

						RETRY_TYPE=Rs23dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_cri_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
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
						TIME1 = "X";
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_cri_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_comedy");
				String Comedy_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorComedy' SERVICE,date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1418";
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
						TIME1 = Rs25dnis.getString("ccg_id");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Comedy_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorComedy' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1418";
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
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs26dnis.getString("USER_TYPE");
						SERVICE = Rs26dnis.getString("SERVICE");
						DATE1 = Rs26dnis.getString("date(response_time)");
						TIME1 = Rs26dnis.getString("ccg_id");

						RETRY_TYPE=Rs26dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;
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
						TIME1 = "X";
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_contest_uninor");
				String Contest_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1423 and event_type not in('Recharge Failed','Recharged') and Plan_id not in(270)";
	            ResultSet Rs28dnis = stmt_source.executeQuery(Contest_Succ);
				System.out.println("***********UNINOR CONTEST SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs28dnis.next())
				{
					try
					{
						MSISDN = Rs28dnis.getString("MSISDN");
						MODE = Rs28dnis.getString("MODE");
						CHARGINGAMOUNT = Rs28dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs28dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs28dnis.getString("CHARGING REASON");
						DATE = Rs28dnis.getString("DATE");
						TYPE = Rs28dnis.getString("TYPE");
						USER_TYPE = Rs28dnis.getString("USER_TYPE");
						SERVICE = Rs28dnis.getString("SERVICE");
						DATE1 = Rs28dnis.getString("date(response_time)");
						TIME1 = Rs28dnis.getString("ccg_id");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_contest_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Contest_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1423 and Plan_id not in(270)";
	            ResultSet Rs29dnis = stmt_source.executeQuery(Contest_fail);
				System.out.println("***********UNINOR CONTEST FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs29dnis.next())
				{
					try
					{
						MSISDN = Rs29dnis.getString("MSISDN");
						MODE = Rs29dnis.getString("MODE");
						CHARGINGAMOUNT = Rs29dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs29dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs29dnis.getString("CHARGING REASON");
						DATE = Rs29dnis.getString("DATE");
						TYPE = Rs29dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs29dnis.getString("USER_TYPE");
						SERVICE = Rs29dnis.getString("SERVICE");
						DATE1 = Rs29dnis.getString("date(response_time)");
						TIME1 = Rs29dnis.getString("ccg_id");

						RETRY_TYPE=Rs29dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_contest_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Contest_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from uninor_summer_contest.tbl_contest_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs30dnis = stmt_source.executeQuery(Contest_unsub);
				System.out.println("***********UNINOR CONTEST UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs30dnis.next())
				{
					try
					{
						MSISDN = Rs30dnis.getString("MSISDN");
						MODE = Rs30dnis.getString("MODE");
						CHARGINGAMOUNT = Rs30dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs30dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs30dnis.getString("CHARGING REASON");
						DATE = Rs30dnis.getString("DATE");
						TYPE = Rs30dnis.getString("TYPE");
						USER_TYPE = Rs30dnis.getString("USER_TYPE");
						SERVICE = Rs30dnis.getString("SERVICE");
						DATE1 = Rs30dnis.getString("date(unsub_date)");
						TIME1 = Rs30dnis.getString("time(unsub_date)");
						TIME1 = "X";
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_contest_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_va_uninor");
				String BollyAlerts_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVABollyAlerts' SERVICE,date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1430";
	            ResultSet Rs31dnis = stmt_source.executeQuery(BollyAlerts_Succ);
				System.out.println("***********UNINOR BollyAlerts SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs31dnis.next())
				{
					try
					{
						MSISDN = Rs31dnis.getString("MSISDN");
						MODE = Rs31dnis.getString("MODE");
						CHARGINGAMOUNT = Rs31dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs31dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs31dnis.getString("CHARGING REASON");
						DATE = Rs31dnis.getString("DATE");
						TYPE = Rs31dnis.getString("TYPE");
						USER_TYPE = Rs31dnis.getString("USER_TYPE");
						SERVICE = Rs31dnis.getString("SERVICE");
						DATE1 = Rs31dnis.getString("date(response_time)");
						TIME1 = Rs31dnis.getString("ccg_id");
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String BollyAlerts_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVABollyAlerts' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1430";
	            ResultSet Rs32dnis = stmt_source.executeQuery(BollyAlerts_fail);
				System.out.println("***********UNINOR BollyAlerts FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs32dnis.next())
				{
					try
					{
						MSISDN = Rs32dnis.getString("MSISDN");
						MODE = Rs32dnis.getString("MODE");
						CHARGINGAMOUNT = Rs32dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs32dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs32dnis.getString("CHARGING REASON");
						DATE = Rs32dnis.getString("DATE");
						TYPE = Rs32dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs32dnis.getString("USER_TYPE");
						SERVICE = Rs32dnis.getString("SERVICE");
						DATE1 = Rs32dnis.getString("date(response_time)");
						TIME1 = Rs32dnis.getString("ccg_id");

						RETRY_TYPE=Rs32dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String BollyAlerts_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','UninorVABollyAlerts' SERVICE,date(unsub_date),time(unsub_date) from Uninor_BollyAlerts.tbl_BA_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs33dnis = stmt_source.executeQuery(BollyAlerts_unsub);
				System.out.println("***********UNINOR BollyAlerts UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs33dnis.next())
				{
					try
					{
						MSISDN = Rs33dnis.getString("MSISDN");
						MODE = Rs33dnis.getString("MODE");
						CHARGINGAMOUNT = Rs33dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs33dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs33dnis.getString("CHARGING REASON");
						DATE = Rs33dnis.getString("DATE");
						TYPE = Rs33dnis.getString("TYPE");
						USER_TYPE = Rs33dnis.getString("USER_TYPE");
						SERVICE = Rs33dnis.getString("SERVICE");
						DATE1 = Rs33dnis.getString("date(unsub_date)");
						TIME1 = Rs33dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				String BollyMasala_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVABollyMasala' SERVICE,date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1432";
	            ResultSet Rs34dnis = stmt_source.executeQuery(BollyMasala_Succ);
				System.out.println("***********UNINOR BollyMasala SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs34dnis.next())
				{
					try
					{
						MSISDN = Rs34dnis.getString("MSISDN");
						MODE = Rs34dnis.getString("MODE");
						CHARGINGAMOUNT = Rs34dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs34dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs34dnis.getString("CHARGING REASON");
						DATE = Rs34dnis.getString("DATE");
						TYPE = Rs34dnis.getString("TYPE");
						USER_TYPE = Rs34dnis.getString("USER_TYPE");
						SERVICE = Rs34dnis.getString("SERVICE");
						DATE1 = Rs34dnis.getString("date(response_time)");
						TIME1 = Rs34dnis.getString("ccg_id");
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String BollyMasala_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVABollyMasala' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1432";
	            ResultSet Rs35dnis = stmt_source.executeQuery(BollyMasala_fail);
				System.out.println("***********UNINOR BollyMasala FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs35dnis.next())
				{
					try
					{
						MSISDN = Rs35dnis.getString("MSISDN");
						MODE = Rs35dnis.getString("MODE");
						CHARGINGAMOUNT = Rs35dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs35dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs35dnis.getString("CHARGING REASON");
						DATE = Rs35dnis.getString("DATE");
						TYPE = Rs35dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs35dnis.getString("USER_TYPE");
						SERVICE = Rs35dnis.getString("SERVICE");
						DATE1 = Rs35dnis.getString("date(response_time)");
						TIME1 = Rs35dnis.getString("ccg_id");

						RETRY_TYPE=Rs35dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String BollyMasala_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','UninorVABollyMasala' SERVICE,date(unsub_date),time(unsub_date) from Uninor_BollywoodMasala.tbl_BM_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs36dnis = stmt_source.executeQuery(BollyMasala_unsub);
				System.out.println("***********UNINOR BollyMasala UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs36dnis.next())
				{
					try
					{
						MSISDN = Rs36dnis.getString("MSISDN");
						MODE = Rs36dnis.getString("MODE");
						CHARGINGAMOUNT = Rs36dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs36dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs36dnis.getString("CHARGING REASON");
						DATE = Rs36dnis.getString("DATE");
						TYPE = Rs36dnis.getString("TYPE");
						USER_TYPE = Rs36dnis.getString("USER_TYPE");
						SERVICE = Rs36dnis.getString("SERVICE");
						DATE1 = Rs36dnis.getString("date(unsub_date)");
						TIME1 = Rs36dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				String FilmiWords_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVAFilmy' SERVICE,date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1431";
	            ResultSet Rs37dnis = stmt_source.executeQuery(FilmiWords_Succ);
				System.out.println("***********UNINOR FILMI WORDS SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs37dnis.next())
				{
					try
					{
						MSISDN = Rs37dnis.getString("MSISDN");
						MODE = Rs37dnis.getString("MODE");
						CHARGINGAMOUNT = Rs37dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs37dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs37dnis.getString("CHARGING REASON");
						DATE = Rs37dnis.getString("DATE");
						TYPE = Rs37dnis.getString("TYPE");
						USER_TYPE = Rs37dnis.getString("USER_TYPE");
						SERVICE = Rs37dnis.getString("SERVICE");
						DATE1 = Rs37dnis.getString("date(response_time)");
						TIME1 = Rs37dnis.getString("ccg_id");
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String FilmiWords_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVAFilmy' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1431";
	            ResultSet Rs38dnis = stmt_source.executeQuery(FilmiWords_fail);
				System.out.println("***********UNINOR FILMI WORDS FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs38dnis.next())
				{
					try
					{
						MSISDN = Rs38dnis.getString("MSISDN");
						MODE = Rs38dnis.getString("MODE");
						CHARGINGAMOUNT = Rs38dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs38dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs38dnis.getString("CHARGING REASON");
						DATE = Rs38dnis.getString("DATE");
						TYPE = Rs38dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs38dnis.getString("USER_TYPE");
						SERVICE = Rs38dnis.getString("SERVICE");
						DATE1 = Rs38dnis.getString("date(response_time)");
						TIME1 = Rs38dnis.getString("ccg_id");

						RETRY_TYPE=Rs38dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String FilmiWords_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','UninorVAFilmy' SERVICE,date(unsub_date),time(unsub_date) from Uninor_FilmiWords.tbl_FW_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs39dnis = stmt_source.executeQuery(FilmiWords_unsub);
				System.out.println("***********UNINOR FILMI WORDS UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs39dnis.next())
				{
					try
					{
						MSISDN = Rs39dnis.getString("MSISDN");
						MODE = Rs39dnis.getString("MODE");
						CHARGINGAMOUNT = Rs39dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs39dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs39dnis.getString("CHARGING REASON");
						DATE = Rs39dnis.getString("DATE");
						TYPE = Rs39dnis.getString("TYPE");
						USER_TYPE = Rs39dnis.getString("USER_TYPE");
						SERVICE = Rs39dnis.getString("SERVICE");
						DATE1 = Rs39dnis.getString("date(unsub_date)");
						TIME1 = Rs39dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				String FilmiHealth_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVAHealth' SERVICE,date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1433";
	            ResultSet Rs40dnis = stmt_source.executeQuery(FilmiHealth_Succ);
				System.out.println("***********UNINOR FILMI HEALTH SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs40dnis.next())
				{
					try
					{
						MSISDN = Rs40dnis.getString("MSISDN");
						MODE = Rs40dnis.getString("MODE");
						CHARGINGAMOUNT = Rs40dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs40dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs40dnis.getString("CHARGING REASON");
						DATE = Rs40dnis.getString("DATE");
						TYPE = Rs40dnis.getString("TYPE");
						USER_TYPE = Rs40dnis.getString("USER_TYPE");
						SERVICE = Rs40dnis.getString("SERVICE");
						DATE1 = Rs40dnis.getString("date(response_time)");
						TIME1 = Rs40dnis.getString("ccg_id");
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String FilmiHealth_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVAHealth' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1433";
	            ResultSet Rs41dnis = stmt_source.executeQuery(FilmiHealth_fail);
				System.out.println("***********UNINOR FILMI HEALTH FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs41dnis.next())
				{
					try
					{
						MSISDN = Rs41dnis.getString("MSISDN");
						MODE = Rs41dnis.getString("MODE");
						CHARGINGAMOUNT = Rs41dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs41dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs41dnis.getString("CHARGING REASON");
						DATE = Rs41dnis.getString("DATE");
						TYPE = Rs41dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs41dnis.getString("USER_TYPE");
						SERVICE = Rs41dnis.getString("SERVICE");
						DATE1 = Rs41dnis.getString("date(response_time)");
						TIME1 = Rs41dnis.getString("ccg_id");

						RETRY_TYPE=Rs41dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String FilmiHealth_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','UninorVAHealth' SERVICE,date(unsub_date),time(unsub_date) from Uninor_FilmiHeath.tbl_FH_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs42dnis = stmt_source.executeQuery(FilmiHealth_unsub);
				System.out.println("***********UNINOR FILMI HEALTH UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs42dnis.next())
				{
					try
					{
						MSISDN = Rs42dnis.getString("MSISDN");
						MODE = Rs42dnis.getString("MODE");
						CHARGINGAMOUNT = Rs42dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs42dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs42dnis.getString("CHARGING REASON");
						DATE = Rs42dnis.getString("DATE");
						TYPE = Rs42dnis.getString("TYPE");
						USER_TYPE = Rs42dnis.getString("USER_TYPE");
						SERVICE = Rs42dnis.getString("SERVICE");
						DATE1 = Rs42dnis.getString("date(unsub_date)");
						TIME1 = Rs42dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				String CFashion_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVAFashion' SERVICE,date(response_time),ccg_id from "+BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1434";
	            ResultSet Rs43dnis = stmt_source.executeQuery(CFashion_Succ);
				System.out.println("***********UNINOR CELEBRITY FASHION SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs43dnis.next())
				{
					try
					{
						MSISDN = Rs43dnis.getString("MSISDN");
						MODE = Rs43dnis.getString("MODE");
						CHARGINGAMOUNT = Rs43dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs43dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs43dnis.getString("CHARGING REASON");
						DATE = Rs43dnis.getString("DATE");
						TYPE = Rs43dnis.getString("TYPE");
						USER_TYPE = Rs43dnis.getString("USER_TYPE");
						SERVICE = Rs43dnis.getString("SERVICE");
						DATE1 = Rs43dnis.getString("date(response_time)");
						TIME1 = Rs43dnis.getString("ccg_id");
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String CFashion_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','UninorVAFashion' SERVICE,date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1434";
	            ResultSet Rs44dnis = stmt_source.executeQuery(CFashion_fail);
				System.out.println("***********UNINOR CELEBRITY FASHION FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs44dnis.next())
				{
					try
					{
						MSISDN = Rs44dnis.getString("MSISDN");
						MODE = Rs44dnis.getString("MODE");
						CHARGINGAMOUNT = Rs44dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs44dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs44dnis.getString("CHARGING REASON");
						DATE = Rs44dnis.getString("DATE");
						TYPE = Rs44dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs44dnis.getString("USER_TYPE");
						SERVICE = Rs44dnis.getString("SERVICE");
						DATE1 = Rs44dnis.getString("date(response_time)");
						TIME1 = Rs44dnis.getString("ccg_id");

						RETRY_TYPE=Rs44dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String CFashion_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','UninorVAFashion' SERVICE,date(unsub_date),time(unsub_date) from Uninor_CelebrityFashion.tbl_CF_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs45dnis = stmt_source.executeQuery(CFashion_unsub);
				System.out.println("***********UNINOR CELEBRITY FASHION UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs45dnis.next())
				{
					try
					{
						MSISDN = Rs45dnis.getString("MSISDN");
						MODE = Rs45dnis.getString("MODE");
						CHARGINGAMOUNT = Rs45dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs45dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs45dnis.getString("CHARGING REASON");
						DATE = Rs45dnis.getString("DATE");
						TYPE = Rs45dnis.getString("TYPE");
						USER_TYPE = Rs45dnis.getString("USER_TYPE");
						SERVICE = Rs45dnis.getString("SERVICE");
						DATE1 = Rs45dnis.getString("date(unsub_date)");
						TIME1 = Rs45dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_va_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_sms_uninor");
				String SmsFashion_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','SMSUninorFashion' SERVICE,date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1438";
	            ResultSet Rs46dnis = stmt_source.executeQuery(SmsFashion_Succ);
				System.out.println("***********UNINOR SMS FASHION SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs46dnis.next())
				{
					try
					{
						MSISDN = Rs46dnis.getString("MSISDN");
						MODE = Rs46dnis.getString("MODE");
						CHARGINGAMOUNT = Rs46dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs46dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs46dnis.getString("CHARGING REASON");
						DATE = Rs46dnis.getString("DATE");
						TYPE = Rs46dnis.getString("TYPE");
						USER_TYPE = Rs46dnis.getString("USER_TYPE");
						SERVICE = Rs46dnis.getString("SERVICE");
						DATE1 = Rs46dnis.getString("date(response_time)");
						TIME1 = Rs46dnis.getString("ccg_id");
						AFFID = Rs46dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;

						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String SmsFashion_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','SMSUninorFashion' SERVICE,date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1438";
	            ResultSet Rs47dnis = stmt_source.executeQuery(SmsFashion_fail);
				System.out.println("***********UNINOR SmsFashion FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs47dnis.next())
				{
					try
					{
						MSISDN = Rs47dnis.getString("MSISDN");
						MODE = Rs47dnis.getString("MODE");
						CHARGINGAMOUNT = Rs47dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs47dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs47dnis.getString("CHARGING REASON");
						DATE = Rs47dnis.getString("DATE");
						TYPE = Rs47dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs47dnis.getString("USER_TYPE");
						SERVICE = Rs47dnis.getString("SERVICE");
						DATE1 = Rs47dnis.getString("date(response_time)");
						TIME1 = Rs47dnis.getString("ccg_id");
						AFFID = Rs47dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;

						RETRY_TYPE=Rs47dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String SmsFashion_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','SMSUninorFashion' SERVICE,date(unsub_date),time(unsub_date) from Uninor_smspack.tbl_fashion_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs48dnis = stmt_source.executeQuery(SmsFashion_unsub);
				System.out.println("***********UNINOR SmsFashion UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs48dnis.next())
				{
					try
					{
						MSISDN = Rs48dnis.getString("MSISDN");
						MODE = Rs48dnis.getString("MODE");
						CHARGINGAMOUNT = Rs48dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs48dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs48dnis.getString("CHARGING REASON");
						DATE = Rs48dnis.getString("DATE");
						TYPE = Rs48dnis.getString("TYPE");
						USER_TYPE = Rs48dnis.getString("USER_TYPE");
						SERVICE = Rs48dnis.getString("SERVICE");
						DATE1 = Rs48dnis.getString("date(unsub_date)");
						TIME1 = Rs48dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				String SmsLocalGuj_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','SMSUninorGujarati' SERVICE,date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1439";
	            ResultSet Rs49dnis = stmt_source.executeQuery(SmsLocalGuj_Succ);
				System.out.println("***********UNINOR SmsLocalGuj SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs49dnis.next())
				{
					try
					{
						MSISDN = Rs49dnis.getString("MSISDN");
						MODE = Rs49dnis.getString("MODE");
						CHARGINGAMOUNT = Rs49dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs49dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs49dnis.getString("CHARGING REASON");
						DATE = Rs49dnis.getString("DATE");
						TYPE = Rs49dnis.getString("TYPE");
						USER_TYPE = Rs49dnis.getString("USER_TYPE");
						SERVICE = Rs49dnis.getString("SERVICE");
						DATE1 = Rs49dnis.getString("date(response_time)");
						TIME1 = Rs49dnis.getString("ccg_id");
						AFFID = Rs49dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;
						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String SmsLocalGuj_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','SMSUninorGujarati' SERVICE,date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1439";
	            ResultSet Rs50dnis = stmt_source.executeQuery(SmsLocalGuj_fail);
				System.out.println("***********UNINOR SmsLocalGuj FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs50dnis.next())
				{
					try
					{
						MSISDN = Rs50dnis.getString("MSISDN");
						MODE = Rs50dnis.getString("MODE");
						CHARGINGAMOUNT = Rs50dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs50dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs50dnis.getString("CHARGING REASON");
						DATE = Rs50dnis.getString("DATE");
						TYPE = Rs50dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs50dnis.getString("USER_TYPE");
						SERVICE = Rs50dnis.getString("SERVICE");
						DATE1 = Rs50dnis.getString("date(response_time)");
						TIME1 = Rs50dnis.getString("ccg_id");
						AFFID = Rs50dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;

						RETRY_TYPE=Rs50dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String SmsLocalGuj_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','SMSUninorGujarati' SERVICE,date(unsub_date),time(unsub_date) from Uninor_smspack.tbl_local_gujarati_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs51dnis = stmt_source.executeQuery(SmsLocalGuj_unsub);
				System.out.println("***********UNINOR SmsLocalGuj UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs51dnis.next())
				{
					try
					{
						MSISDN = Rs51dnis.getString("MSISDN");
						MODE = Rs51dnis.getString("MODE");
						CHARGINGAMOUNT = Rs51dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs51dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs51dnis.getString("CHARGING REASON");
						DATE = Rs51dnis.getString("DATE");
						TYPE = Rs51dnis.getString("TYPE");
						USER_TYPE = Rs51dnis.getString("USER_TYPE");
						SERVICE = Rs51dnis.getString("SERVICE");
						DATE1 = Rs51dnis.getString("date(unsub_date)");
						TIME1 = Rs51dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				String SmsRichAlerts_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','SMSUninorAlert' SERVICE,date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1440";
	            ResultSet Rs52dnis = stmt_source.executeQuery(SmsRichAlerts_Succ);
				System.out.println("***********UNINOR SmsRichAlerts SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs52dnis.next())
				{
					try
					{
						MSISDN = Rs52dnis.getString("MSISDN");
						MODE = Rs52dnis.getString("MODE");
						CHARGINGAMOUNT = Rs52dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs52dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs52dnis.getString("CHARGING REASON");
						DATE = Rs52dnis.getString("DATE");
						TYPE = Rs52dnis.getString("TYPE");
						USER_TYPE = Rs52dnis.getString("USER_TYPE");
						SERVICE = Rs52dnis.getString("SERVICE");
						DATE1 = Rs52dnis.getString("date(response_time)");
						TIME1 = Rs52dnis.getString("ccg_id");
						AFFID = Rs52dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;
						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String SmsRichAlerts_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE','SMSUninorAlert' SERVICE,date(response_time),ccg_id,ifnull(aff_id,0) 'aff_id' from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1440";
	            ResultSet Rs53dnis = stmt_source.executeQuery(SmsRichAlerts_fail);
				System.out.println("***********UNINOR SmsLocalGuj FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs53dnis.next())
				{
					try
					{
						MSISDN = Rs53dnis.getString("MSISDN");
						MODE = Rs53dnis.getString("MODE");
						CHARGINGAMOUNT = Rs53dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs53dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs53dnis.getString("CHARGING REASON");
						DATE = Rs53dnis.getString("DATE");
						TYPE = Rs53dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs53dnis.getString("USER_TYPE");
						SERVICE = Rs53dnis.getString("SERVICE");
						DATE1 = Rs53dnis.getString("date(response_time)");
						TIME1 = Rs53dnis.getString("ccg_id");
						AFFID = Rs53dnis.getString("aff_id");
						if(MODE.startsWith("WAP"))
							MODE=MODE+"_"+AFFID;

						RETRY_TYPE=Rs53dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String SmsRichAlerts_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','SMSUninorAlert' SERVICE,date(unsub_date),time(unsub_date) from Uninor_smspack.tbl_rich_alerts_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs54dnis = stmt_source.executeQuery(SmsRichAlerts_unsub);
				System.out.println("***********UNINOR SmsLocalGuj UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs54dnis.next())
				{
					try
					{
						MSISDN = Rs54dnis.getString("MSISDN");
						MODE = Rs54dnis.getString("MODE");
						CHARGINGAMOUNT = Rs54dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs54dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs54dnis.getString("CHARGING REASON");
						DATE = Rs54dnis.getString("DATE");
						TYPE = Rs54dnis.getString("TYPE");
						USER_TYPE = Rs54dnis.getString("USER_TYPE");
						SERVICE = Rs54dnis.getString("SERVICE");
						DATE1 = Rs54dnis.getString("date(unsub_date)");
						TIME1 = Rs54dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_sms_uninor  values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_reg_uninor");
				String DesiBeat_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from "+ BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1441";
	            ResultSet Rs55dnis = stmt_source.executeQuery(DesiBeat_Succ);
				System.out.println("***********UNINOR DESI BEAT SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs55dnis.next())
				{
					try
					{
						MSISDN = Rs55dnis.getString("MSISDN");
						MODE = Rs55dnis.getString("MODE");
						CHARGINGAMOUNT = Rs55dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs55dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs55dnis.getString("CHARGING REASON");
						DATE = Rs55dnis.getString("DATE");
						TYPE = Rs55dnis.getString("TYPE");
						USER_TYPE = Rs55dnis.getString("USER_TYPE");
						SERVICE = Rs55dnis.getString("SERVICE");
						DATE1 = Rs55dnis.getString("date(response_time)");
						TIME1 = Rs55dnis.getString("ccg_id");
						stmt_destination.executeUpdate("insert into tbl_billing_reg_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String DesiBeat_fail = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' when 'DUPREQ' then 'Activation' when 'Grace' then 'Renewal' when 'park' then 'Renewal' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' when 'resub' then 'Renewal' else 'Renewal' end 'TYPE',cppp 'USER_TYPE',plan_id 'SERVICE',date(response_time),ccg_id from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1441";
	            ResultSet Rs56dnis = stmt_source.executeQuery(DesiBeat_fail);
				System.out.println("***********UNINOR DESI BEAT FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs56dnis.next())
				{
					try
					{
						MSISDN = Rs56dnis.getString("MSISDN");
						MODE = Rs56dnis.getString("MODE");
						CHARGINGAMOUNT = Rs56dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs56dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs56dnis.getString("CHARGING REASON");
						DATE = Rs56dnis.getString("DATE");
						TYPE = Rs56dnis.getString("TYPE");
						if(TYPE.equalsIgnoreCase("SUB_FAIL")) TYPE="Activation";
						USER_TYPE = Rs56dnis.getString("USER_TYPE");
						SERVICE = Rs56dnis.getString("SERVICE");
						DATE1 = Rs56dnis.getString("date(response_time)");
						TIME1 = Rs56dnis.getString("ccg_id");

						RETRY_TYPE=Rs56dnis.getString("event_type");

						if(RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="G_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("PARK"))
							CHARGINGREASON="P_"+CHARGINGREASON;
						else if(RETRY_TYPE.toUpperCase().contains("ALRSUB"))
							CHARGINGREASON="A_"+CHARGINGREASON;

						stmt_destination.executeUpdate("insert into tbl_billing_reg_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String DesiBeat_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from uninor_hungama.tbl_LG_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs57dnis = stmt_source.executeQuery(DesiBeat_unsub);
				System.out.println("***********UNINOR DESI BEAT UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs57dnis.next())
				{
					try
					{
						MSISDN = Rs57dnis.getString("MSISDN");
						MODE = Rs57dnis.getString("MODE");
						CHARGINGAMOUNT = Rs57dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs57dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs57dnis.getString("CHARGING REASON");
						DATE = Rs57dnis.getString("DATE");
						TYPE = Rs57dnis.getString("TYPE");
						USER_TYPE = Rs57dnis.getString("USER_TYPE");
						SERVICE = Rs57dnis.getString("SERVICE");
						DATE1 = Rs57dnis.getString("date(unsub_date)");
						TIME1 = Rs57dnis.getString("time(unsub_date)");
						TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_reg_uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            System.out.println("****Updations is Begining********");

	            		System.out.println("update tbl_billing_redfm_uninor set service='WAPREDFMUninor' where  TYPE='EVENT'  and service='RedFMUninor' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_redfm_uninor set service='WAPREDFMUninor' where  TYPE='EVENT'  and service='RedFMUninor' and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_rt_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_rt_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_rt_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_mtv_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_mtv_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_mtv_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_ria_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_ria_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_ria_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_redfm_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_redfm_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_redfm_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL)  and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_astro_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_astro_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_astro_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_54646_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_54646_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_54646_uninor set `USER TYPE`='NA' where ( `USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_cri_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_cri_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_cri_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_aa_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_aa_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_aa_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_comedy set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_comedy set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_comedy set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_contest_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_contest_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_contest_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_va_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_va_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_va_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				stmt_destination.executeUpdate("update tbl_billing_sms_uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_sms_uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_sms_uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and DATE1 = date(subdate(now()," + day + "))");

				System.out.println("****Updations done****");

				Thread.sleep(1000);

				System.out.println("************************file creation Process running**********************************");
				Fun_FileCreator(day);

				Thread.sleep(1000);
				System.out.println("****Closing connections**** Good bye See u again****");

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
