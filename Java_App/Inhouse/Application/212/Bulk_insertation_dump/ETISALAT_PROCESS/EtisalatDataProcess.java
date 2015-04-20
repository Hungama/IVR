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


	public class EtisalatDataProcess extends Thread
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

	public EtisalatDataProcess()
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
				ResultSet Rsdate = stmt_source.executeQuery(query_date);
				while(Rsdate.next())
				{
						FILEDATE = Rsdate.getString("FILEDATE");
						System.out.println("****Running for date :****"+FILEDATE);
				}

				String Jokes_Success = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Jokes' circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(116,121,168)";
				String Jokes_failure = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Jokes' circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(116,121,168)";
				String Jokes_unsub   = "select ani 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Jokes' circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from etislat_hsep.tbl_jokes_subscription_log nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id in(116,121,168)";

				String Holly_Success = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Hollywood' circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(117,122,171)";
				String Holly_failure = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Hollywood' circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(117,122,171)";
				String Holly_unsub   = "select ani 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Hollywood' circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from etislat_hsep.tbl_hollywood_subscription_log nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id in(117,122,171)";

				String Funnews_Success = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Fun' circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(118,123,170)";
				String Funnews_failure = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Fun' circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(118,123,170)";
				String Funnews_unsub   = "select ani 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','Fun' circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from etislat_hsep.tbl_funnews_subscription_log nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id in(118,123,170)";

				String Sfp_Success = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','SPL' circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(119,124,172)";
				String Sfp_failure = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','SPL' circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(119,124,172)";
				String Sfp_unsub   = "select ani 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','SPL' circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from etislat_hsep.tbl_sfp_subscription_log nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id in(119,124,172)";

				String Epl_Success = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','EPL' circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(125,126,166)";
				String Epl_failure = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','EPL' circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(125,126,166)";
				String Epl_unsub   = "select ani 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','EPL' circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from etislat_hsep.tbl_epl_subscription_log nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id in(125,126,166)";

				String LSP_Success = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','LSP' circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(174,175,176)";
				String LSP_failure = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','LSP' circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(174,175,176)";
				String LSP_unsub   = "select ani 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','LSP' circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from etislat_hsep.tbl_lsp_subscription_log nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id in(174,175,176)";

				String MOT_Success = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','MOT' circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_success nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(177,178,179)";
				String MOT_failure = "select msisdn 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','MOT' circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOPUP' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=2121 and Plan_id in(177,178,179)";
				String MOT_unsub   = "select ani 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT','MOT' circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from etislat_hsep.tbl_mot_subscription_log nolock where date(unsub_date)=date(subdate(now(),"+day+")) and Plan_id in(177,178,179)";

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


				String str1 = "select count(1) as CNT from tbl_billing_sms_etisalat where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_sms_etisalat where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				ResultSet Rsdnis = stmt_source.executeQuery(Jokes_Success);
				System.out.println("***********ETISLAT JOKES SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis.next())
				{
					try
					{

	            	    MSISDN = Rsdnis.getString("MSISDN");
	                    MODE = Rsdnis.getString("MODE");
	                    CHARGINGAMOUNT = Rsdnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rsdnis.getString("circle");
	                    CHARGINGREASON = Rsdnis.getString("CHARGING REASON");
	                    DATE = Rsdnis.getString("DATE");
	                    TYPE = Rsdnis.getString("TYPE");
	                    USER_TYPE = Rsdnis.getString("USER_TYPE");
	                    SERVICE = Rsdnis.getString("SERVICE");
	                    DATE1 = Rsdnis.getString("date(response_time)");
	                    TIME1 = Rsdnis.getString("time(response_time)");

	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs2dnis = stmt_source.executeQuery(Jokes_failure);
				System.out.println("***********ETISLAT JOKES FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs2dnis.next())
				{
					try
					{
	            	    MSISDN = Rs2dnis.getString("MSISDN");
	                    MODE = Rs2dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs2dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs2dnis.getString("circle");
	                    CHARGINGREASON = Rs2dnis.getString("CHARGING REASON");
	                    DATE = Rs2dnis.getString("DATE");
	                    TYPE = Rs2dnis.getString("TYPE");
	                    USER_TYPE = Rs2dnis.getString("USER_TYPE");
	                    SERVICE = Rs2dnis.getString("SERVICE");
	                    DATE1 = Rs2dnis.getString("date(response_time)");
	                    TIME1 = Rs2dnis.getString("time(response_time)");
	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs3dnis = stmt_source.executeQuery(Jokes_unsub);
				System.out.println("***********ETISLAT JOKES UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs3dnis.next())
				{
					try
					{

	            	    MSISDN = Rs3dnis.getString("MSISDN");
	                    MODE = Rs3dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs3dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs3dnis.getString("circle");
	                    CHARGINGREASON = Rs3dnis.getString("CHARGING REASON");
	                    DATE = Rs3dnis.getString("DATE");
	                    TYPE = Rs3dnis.getString("TYPE");
	                    USER_TYPE = Rs3dnis.getString("USER_TYPE");
	                    SERVICE = Rs3dnis.getString("SERVICE");
	                    DATE1 = Rs3dnis.getString("date(unsub_date)");
	                    TIME1 = Rs3dnis.getString("time(unsub_date)");
	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            ResultSet Rs4dnis = stmt_source.executeQuery(Holly_Success);
				System.out.println("***********ETISALAT HOLLYEOOD SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs4dnis.next())
				{
					try
					{

						MSISDN = Rs4dnis.getString("MSISDN");
						MODE = Rs4dnis.getString("MODE");
						CHARGINGAMOUNT = Rs4dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs4dnis.getString("circle");
						CHARGINGREASON = Rs4dnis.getString("CHARGING REASON");
						DATE = Rs4dnis.getString("DATE");
						TYPE = Rs4dnis.getString("TYPE");
						USER_TYPE = Rs4dnis.getString("USER_TYPE");
						SERVICE = Rs4dnis.getString("SERVICE");
						DATE1 = Rs4dnis.getString("date(response_time)");
						TIME1 = Rs4dnis.getString("time(response_time)");

			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            ResultSet Rs5dnis = stmt_source.executeQuery(Holly_failure);
				System.out.println("***********ETISALAT HOLLYEOOD FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs5dnis.next())
				{
					try
					{
						MSISDN = Rs5dnis.getString("MSISDN");
						MODE = Rs5dnis.getString("MODE");
						CHARGINGAMOUNT = Rs5dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs5dnis.getString("circle");
						CHARGINGREASON = Rs5dnis.getString("CHARGING REASON");
						DATE = Rs5dnis.getString("DATE");
						TYPE = Rs5dnis.getString("TYPE");
						USER_TYPE = Rs5dnis.getString("USER_TYPE");
						SERVICE = Rs5dnis.getString("SERVICE");
						DATE1 = Rs5dnis.getString("date(response_time)");
						TIME1 = Rs5dnis.getString("time(response_time)");

			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs6dnis = stmt_source.executeQuery(Holly_unsub);
				System.out.println("***********ETISALAT HOLLYEOOD UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs6dnis.next())
				{
					try
					{
						MSISDN = Rs6dnis.getString("MSISDN");
						MODE = Rs6dnis.getString("MODE");
						CHARGINGAMOUNT = Rs6dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs6dnis.getString("circle");
						CHARGINGREASON = Rs6dnis.getString("CHARGING REASON");
						DATE = Rs6dnis.getString("DATE");
						TYPE = Rs6dnis.getString("TYPE");
						USER_TYPE = Rs6dnis.getString("USER_TYPE");
						SERVICE = Rs6dnis.getString("SERVICE");
						DATE1 = Rs6dnis.getString("date(unsub_date)");
						TIME1 = Rs6dnis.getString("time(unsub_date)");

			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs9dnis = stmt_source.executeQuery(Funnews_Success);
				System.out.println("***********ETISALAT FUNNEWS SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs9dnis.next())
				{
					try
					{
						MSISDN = Rs9dnis.getString("MSISDN");
						MODE = Rs9dnis.getString("MODE");
						CHARGINGAMOUNT = Rs9dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs9dnis.getString("circle");
						CHARGINGREASON = Rs9dnis.getString("CHARGING REASON");
						DATE = Rs9dnis.getString("DATE");
						TYPE = Rs9dnis.getString("TYPE");
						USER_TYPE = Rs9dnis.getString("USER_TYPE");
						SERVICE = Rs9dnis.getString("SERVICE");
						DATE1 = Rs9dnis.getString("date(response_time)");
						TIME1 = Rs9dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs10dnis = stmt_source.executeQuery(Funnews_failure);
				System.out.println("***********ETISALAT FUNNEWS FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs10dnis.next())
				{
					try
					{
						MSISDN = Rs10dnis.getString("MSISDN");
						MODE = Rs10dnis.getString("MODE");
						CHARGINGAMOUNT = Rs10dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs10dnis.getString("circle");
						CHARGINGREASON = Rs10dnis.getString("CHARGING REASON");
						DATE = Rs10dnis.getString("DATE");
						TYPE = Rs10dnis.getString("TYPE");
						USER_TYPE = Rs10dnis.getString("USER_TYPE");
						SERVICE = Rs10dnis.getString("SERVICE");
						DATE1 = Rs10dnis.getString("date(response_time)");
						TIME1 = Rs10dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs11dnis = stmt_source.executeQuery(Funnews_unsub);
				System.out.println("***********ETISALAT FUNNEWS UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs11dnis.next())
				{
					try
					{
						MSISDN = Rs11dnis.getString("MSISDN");
						MODE = Rs11dnis.getString("MODE");
						CHARGINGAMOUNT = Rs11dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs11dnis.getString("circle");
						CHARGINGREASON = Rs11dnis.getString("CHARGING REASON");
						DATE = Rs11dnis.getString("DATE");
						TYPE = Rs11dnis.getString("TYPE");
						USER_TYPE = Rs11dnis.getString("USER_TYPE");
						SERVICE = Rs11dnis.getString("SERVICE");
						DATE1 = Rs11dnis.getString("date(unsub_date)");
						TIME1 = Rs11dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }

				ResultSet Rs13dnis = stmt_source.executeQuery(Sfp_Success);
				System.out.println("***********ETISALAT SFP SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs13dnis.next())
				{
					try
					{
						MSISDN = Rs13dnis.getString("MSISDN");
						MODE = Rs13dnis.getString("MODE");
						CHARGINGAMOUNT = Rs13dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs13dnis.getString("circle");
						CHARGINGREASON = Rs13dnis.getString("CHARGING REASON");
						DATE = Rs13dnis.getString("DATE");
						TYPE = Rs13dnis.getString("TYPE");
						USER_TYPE = Rs13dnis.getString("USER_TYPE");
						SERVICE = Rs13dnis.getString("SERVICE");
						DATE1 = Rs13dnis.getString("date(response_time)");
						TIME1 = Rs13dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs14dnis = stmt_source.executeQuery(Sfp_failure);
				System.out.println("***********ETISALAT SFP FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs14dnis.next())
				{
					try
					{
						MSISDN = Rs14dnis.getString("MSISDN");
						MODE = Rs14dnis.getString("MODE");
						CHARGINGAMOUNT = Rs14dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs14dnis.getString("circle");
						CHARGINGREASON = Rs14dnis.getString("CHARGING REASON");
						DATE = Rs14dnis.getString("DATE");
						TYPE = Rs14dnis.getString("TYPE");
						USER_TYPE = Rs14dnis.getString("USER_TYPE");
						SERVICE = Rs14dnis.getString("SERVICE");
						DATE1 = Rs14dnis.getString("date(response_time)");
						TIME1 = Rs14dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs15dnis = stmt_source.executeQuery(Sfp_unsub);
				System.out.println("***********ETISALAT SFP UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs15dnis.next())
				{
					try
					{
						MSISDN = Rs15dnis.getString("MSISDN");
						MODE = Rs15dnis.getString("MODE");
						CHARGINGAMOUNT = Rs15dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs15dnis.getString("circle");
						CHARGINGREASON = Rs15dnis.getString("CHARGING REASON");
						DATE = Rs15dnis.getString("DATE");
						TYPE = Rs15dnis.getString("TYPE");
						USER_TYPE = Rs15dnis.getString("USER_TYPE");
						SERVICE = Rs15dnis.getString("SERVICE");
						DATE1 = Rs15dnis.getString("date(unsub_date)");
						TIME1 = Rs15dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }

				ResultSet Rs16dnis = stmt_source.executeQuery(Epl_Success);
				System.out.println("***********ETISALAT EPL SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs16dnis.next())
				{
					try
					{
						MSISDN = Rs16dnis.getString("MSISDN");
						MODE = Rs16dnis.getString("MODE");
						CHARGINGAMOUNT = Rs16dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs16dnis.getString("circle");
						CHARGINGREASON = Rs16dnis.getString("CHARGING REASON");
						DATE = Rs16dnis.getString("DATE");
						TYPE = Rs16dnis.getString("TYPE");
						USER_TYPE = Rs16dnis.getString("USER_TYPE");
						SERVICE = Rs16dnis.getString("SERVICE");
						DATE1 = Rs16dnis.getString("date(response_time)");
						TIME1 = Rs16dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs17dnis = stmt_source.executeQuery(Epl_failure);
				System.out.println("***********ETISALAT EPL FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs17dnis.next())
				{
					try
					{
						MSISDN = Rs17dnis.getString("MSISDN");
						MODE = Rs17dnis.getString("MODE");
						CHARGINGAMOUNT = Rs17dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs17dnis.getString("circle");
						CHARGINGREASON = Rs17dnis.getString("CHARGING REASON");
						DATE = Rs17dnis.getString("DATE");
						TYPE = Rs17dnis.getString("TYPE");
						USER_TYPE = Rs17dnis.getString("USER_TYPE");
						SERVICE = Rs17dnis.getString("SERVICE");
						DATE1 = Rs17dnis.getString("date(response_time)");
						TIME1 = Rs17dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs18dnis = stmt_source.executeQuery(Epl_unsub);
				System.out.println("***********ETISALAT EPL UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs18dnis.next())
				{
					try
					{
						MSISDN = Rs18dnis.getString("MSISDN");
						MODE = Rs18dnis.getString("MODE");
						CHARGINGAMOUNT = Rs18dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs18dnis.getString("circle");
						CHARGINGREASON = Rs18dnis.getString("CHARGING REASON");
						DATE = Rs18dnis.getString("DATE");
						TYPE = Rs18dnis.getString("TYPE");
						USER_TYPE = Rs18dnis.getString("USER_TYPE");
						SERVICE = Rs18dnis.getString("SERVICE");
						DATE1 = Rs18dnis.getString("date(unsub_date)");
						TIME1 = Rs18dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs19dnis = stmt_source.executeQuery(LSP_Success);
				System.out.println("***********ETISALAT LSP SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs19dnis.next())
				{
					try
					{
						MSISDN = Rs19dnis.getString("MSISDN");
						MODE = Rs19dnis.getString("MODE");
						CHARGINGAMOUNT = Rs19dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs19dnis.getString("circle");
						CHARGINGREASON = Rs19dnis.getString("CHARGING REASON");
						DATE = Rs19dnis.getString("DATE");
						TYPE = Rs19dnis.getString("TYPE");
						USER_TYPE = Rs19dnis.getString("USER_TYPE");
						SERVICE = Rs19dnis.getString("SERVICE");
						DATE1 = Rs19dnis.getString("date(response_time)");
						TIME1 = Rs19dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs20dnis = stmt_source.executeQuery(LSP_failure);
				System.out.println("***********ETISALAT LSP FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs20dnis.next())
				{
					try
					{
						MSISDN = Rs20dnis.getString("MSISDN");
						MODE = Rs20dnis.getString("MODE");
						CHARGINGAMOUNT = Rs20dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs20dnis.getString("circle");
						CHARGINGREASON = Rs20dnis.getString("CHARGING REASON");
						DATE = Rs20dnis.getString("DATE");
						TYPE = Rs20dnis.getString("TYPE");
						USER_TYPE = Rs20dnis.getString("USER_TYPE");
						SERVICE = Rs20dnis.getString("SERVICE");
						DATE1 = Rs20dnis.getString("date(response_time)");
						TIME1 = Rs20dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs21dnis = stmt_source.executeQuery(LSP_unsub);
				System.out.println("***********ETISALAT LSP UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs21dnis.next())
				{
					try
					{
						MSISDN = Rs21dnis.getString("MSISDN");
						MODE = Rs21dnis.getString("MODE");
						CHARGINGAMOUNT = Rs21dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs21dnis.getString("circle");
						CHARGINGREASON = Rs21dnis.getString("CHARGING REASON");
						DATE = Rs21dnis.getString("DATE");
						TYPE = Rs21dnis.getString("TYPE");
						USER_TYPE = Rs21dnis.getString("USER_TYPE");
						SERVICE = Rs21dnis.getString("SERVICE");
						DATE1 = Rs21dnis.getString("date(unsub_date)");
						TIME1 = Rs21dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs22dnis = stmt_source.executeQuery(MOT_Success);
				System.out.println("***********ETISALAT MOT SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs22dnis.next())
				{
					try
					{
						MSISDN = Rs22dnis.getString("MSISDN");
						MODE = Rs22dnis.getString("MODE");
						CHARGINGAMOUNT = Rs22dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs22dnis.getString("circle");
						CHARGINGREASON = Rs22dnis.getString("CHARGING REASON");
						DATE = Rs22dnis.getString("DATE");
						TYPE = Rs22dnis.getString("TYPE");
						USER_TYPE = Rs22dnis.getString("USER_TYPE");
						SERVICE = Rs22dnis.getString("SERVICE");
						DATE1 = Rs22dnis.getString("date(response_time)");
						TIME1 = Rs22dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs23dnis = stmt_source.executeQuery(MOT_failure);
				System.out.println("***********ETISALAT MOT FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs23dnis.next())
				{
					try
					{
						MSISDN = Rs23dnis.getString("MSISDN");
						MODE = Rs23dnis.getString("MODE");
						CHARGINGAMOUNT = Rs23dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs23dnis.getString("circle");
						CHARGINGREASON = Rs23dnis.getString("CHARGING REASON");
						DATE = Rs23dnis.getString("DATE");
						TYPE = Rs23dnis.getString("TYPE");
						USER_TYPE = Rs23dnis.getString("USER_TYPE");
						SERVICE = Rs23dnis.getString("SERVICE");
						DATE1 = Rs23dnis.getString("date(response_time)");
						TIME1 = Rs23dnis.getString("time(response_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				ResultSet Rs24dnis = stmt_source.executeQuery(MOT_unsub);
				System.out.println("***********ETISALAT MOT UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs24dnis.next())
				{
					try
					{
						MSISDN = Rs24dnis.getString("MSISDN");
						MODE = Rs24dnis.getString("MODE");
						CHARGINGAMOUNT = Rs24dnis.getString("CHARGING AMOUNT");
						CIRCLE = Rs24dnis.getString("circle");
						CHARGINGREASON = Rs24dnis.getString("CHARGING REASON");
						DATE = Rs24dnis.getString("DATE");
						TYPE = Rs24dnis.getString("TYPE");
						USER_TYPE = Rs24dnis.getString("USER_TYPE");
						SERVICE = Rs24dnis.getString("SERVICE");
						DATE1 = Rs24dnis.getString("date(unsub_date)");
						TIME1 = Rs24dnis.getString("time(unsub_date)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_sms_etisalat values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
/*				stmt_destination.executeUpdate("update tbl_billing_sms_etisalat set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_sms_etisalat set `CHARGING REASON`='33' where  (`CHARGING REASON` is NULL or`CHARGING REASON`='NA') and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_sms_etisalat set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL) and DATE1 = date(subdate(now(),"+day+"))");*/
				System.out.println("****Updations done closing connections**** Good bye See u again****");

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
	public static void main(String arg[])
	{
		if(arg.length>0)
			day = Integer.parseInt(arg[0]);
		else
			day = 1;
		EtisalatDataProcess c = new EtisalatDataProcess();
		c.start();

	}

}
