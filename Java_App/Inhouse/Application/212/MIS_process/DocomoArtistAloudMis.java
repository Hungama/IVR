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


	public class DocomoArtistAloudMis extends Thread
	{
	public static Connection con_source=null,con_destination=null;
	public static Statement stmt_source,stmt_destination;
	public String IP=null,DSN=null,USR=null,PWD=null;
	public String dIP=null,dDSN=null,dUSR=null,dPWD=null;
	public static CallableStatement cstmt=null;
	public static int day=1;
	public static int i=0;
	public static int BaseCnt1=0;
	public static int BaseCnt2=0;
	public static int BaseCnt3=0;
	public void readDBCONFIG()
	{
		try
		{
			System.out.println("**********************************************************");
			IP="192.168.100.224";
			DSN="master_db";
			USR="ivr";
			PWD="ivr";
			System.out.println("**SOURCE IP is  ["+IP+"] **  DSN is ["+DSN+"] Usr is ["+USR+"] Pwd is ["+PWD+"]\t**");
			System.out.println("**********************************************************");

		}
		catch(Exception e)
		{
			System.out.println("Exception while reading DB.cfg");
			e.printStackTrace();

		}
	}

	public DocomoArtistAloudMis()
	{
		try
		{
			readDBCONFIG();
			System.out.println("Initiallizing DB");
			Class.forName("com.mysql.jdbc.Driver");
			con_source = DriverManager.getConnection("jdbc:mysql://"+IP+"/"+DSN, USR, PWD);
			System.out.println("***Database Connection established!***");
			stmt_source = con_source.createStatement();
			stmt_destination = con_source.createStatement();
			System.out.println("###DB CONNECTION UP FOR DATABASE###");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String arg[])
	{
		if(arg.length>0)
			day = Integer.parseInt(arg[0]);
		else
			day = 1;
		DocomoArtistAloudMis c = new DocomoArtistAloudMis();
		c.start();
	}
	public void run()
	{
		try
		{
				//day=1;
				String BillingTable="master_db.tbl_billing_success";
				String BillingTableBackup="master_db.tbl_billing_success_backup";
				String FileDate = "";
				String TableDate = "";
				String query_date = "select date_format(subdate(now(),"+day+"),'%Y%m%d') as 'FILEDATE'";
				ResultSet Rsdate = stmt_source.executeQuery(query_date);
				while(Rsdate.next())
				{
					FileDate = Rsdate.getString("FILEDATE");
					System.out.println("****Running DocomoAAloud for date :****"+FileDate);
				}
				int SERVICEID = 0;
				int PLANID = 0;
				if(day>2)
					BillingTable="master_db.tbl_billing_success_backup";
				else
					BillingTable="master_db.tbl_billing_success";

				System.out.println("****Table name is****" +BillingTable);
				String CNT = "";
				String EVENT_TYPE = "";
				String MODE_TYPE = "";
				String UNSUB_REASON = "";
				String STATUS = "";
				String USER_STATUS = "";
				String FAIL_EVENT_TYPE = "";
				String SERVICE_ID = "";
				String EVENTTYPE = "";
				String DURATION = "";
				String SEC_TF_TYPE = "";
				String UU_TF_TYPE = "";
				String deactivation_str1 ="";
				String call_TF ="";
				String Mous_TF ="";
				String Pulse_TF ="";
				String MODE = "";
				String MOUS = "";
				String PULSE = "";
				String CHARGAMT = "";
				String CIRCLE = "";
				String TYPE = "";
				String USER_TYPE = "";
				String SERVICE = "";
				String DATE1 = "";
				String TIME1 = "";

				String str1 = "select count(1) as CNT from mis_db.tbl_AA_daily_report_test where date(report_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from mis_db.tbl_AA_daily_report_test where date(report_date) = date(subdate(now(),"+day+"))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
	            String Airtel_Success = "select count(1) as cnt,circle,chrg_amount,service_id,event_type,plan_id from "+ BillingTable +" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id in(1002) and plan_id=96 and event_type in('SUB','RESUB','TOPUP') and SC not like '%P%' group by circle,service_id,chrg_amount,event_type,plan_id";
				ResultSet Rs2dnis = stmt_source.executeQuery(Airtel_Success);
				System.out.println("***********AIRTEL ACTIVATION DATA INSERTING AT DESTINATION***********");
				while(Rs2dnis.next())
				{
					try
					{
	            	    CNT = Rs2dnis.getString("cnt");
	            	    CIRCLE = Rs2dnis.getString("circle");
	            	    if(CIRCLE == "") CIRCLE="UND";
						else if(CIRCLE == "HAR") CIRCLE="HAY";
						else if(CIRCLE == "PUN") CIRCLE="PUB";
	            	    CHARGAMT = Rs2dnis.getString("chrg_amount");
	            	    SERVICEID = Rs2dnis.getInt("service_id");
	            	    EVENT_TYPE = Rs2dnis.getString("event_type");
	            	    PLANID = Rs2dnis.getInt("plan_id");

						if(EVENT_TYPE.equals("SUB"))
							EVENT_TYPE="Activation_"+CHARGAMT;
						else if(EVENT_TYPE.equals("RESUB"))
							EVENT_TYPE="Renewal_"+CHARGAMT;
						else if(EVENT_TYPE.equals("TOPUP"))
							EVENT_TYPE="TOP-UP_"+CHARGAMT;

	                   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
	                    //System.out.println("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,service_id,charging_rate,total_count,mous,pulse,total_sec) values('"+FileDate+"','"+EVENT_TYPE+"','"+CIRCLE+"','"+SERVICEID+"','"+CHARGAMT+"','"+CNT+"','NA','NA','NA')");
						stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,service_id,charging_rate,total_count,mous,pulse,total_sec) values('"+FileDate+"','"+EVENT_TYPE+"','"+CIRCLE+"','"+SERVICEID+"','"+CHARGAMT+"','"+CNT+"','NA','NA','NA')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
				}// end the code to insert the data of activation Airtel

				//Start the code to activation Record mode wise
				String Airtel_Modewise = "select count(1) as cnt,circle,service_id,event_type,mode,plan_id from "+ BillingTable +" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id in(1002) and plan_id=96 and event_type in('SUB','TOPUP') and SC not like '%P%' group by circle,service_id,event_type,mode,plan_id order by event_type,plan_id"; //and plan_id!=29
				ResultSet Rs3dnis = stmt_source.executeQuery(Airtel_Modewise);
				System.out.println("***********AIRTEL MODE WISE DATA INSERTING AT DESTINATION***********");
				while(Rs3dnis.next())
				{
					CNT = Rs3dnis.getString("cnt");
					CIRCLE = Rs3dnis.getString("circle");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";
					SERVICEID = Rs3dnis.getInt("service_id");
					EVENT_TYPE = Rs3dnis.getString("event_type");
					MODE = Rs3dnis.getString("mode");
					PLANID = Rs3dnis.getInt("plan_id");

					if(EVENT_TYPE.equals("SUB"))
						MODE_TYPE="Mode_Activation_"+MODE.toUpperCase();
					else if(EVENT_TYPE.equals("TOPUP"))
						MODE_TYPE="Mode_TOP-UP_IVR";
				   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
				   	System.out.println("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,service_id,charging_rate,total_count,mous,pulse,total_sec) values('"+FileDate+"','"+MODE_TYPE+"','"+CIRCLE+"','"+SERVICEID+"','"+CHARGAMT+"','"+CNT+"','NA','NA','NA')");
					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,service_id,charging_rate,total_count,mous,pulse,total_sec) values('"+FileDate+"','"+MODE_TYPE+"','"+CIRCLE+"','"+SERVICEID+"','"+CHARGAMT+"','"+CNT+"','NA','NA','NA')");
				}
				/////////////////////////////////// pending Base ////////////////////////////////////////////////////////////
				// start code to insert the Pending Base date into the database airtel
				String Pending_Base = "select count(1) as cnt,circle from docomo_hungama.tbl_Artist_Aloud_subscription where status=11 and date(sub_date)=date(subdate(now(),"+day+")) group by circle";
				ResultSet Rs4dnis = stmt_source.executeQuery(Pending_Base);
				System.out.println("***********AIRTEL PENDING BASE DATA INSERTING AT DESTINATION***********");
				while(Rs4dnis.next())
				{
					CNT = Rs4dnis.getString("cnt");
					CIRCLE = Rs4dnis.getString("circle");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";
					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mous,pulse,total_sec,service_id) values('"+FileDate+"','Pending_Base','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','1002')");
				}
				////////////////////////////////////////////////////// Active Base ///////////////////////////////////////////////////////////////
				String Active_Base = "select count(1) as cnt,circle from docomo_hungama.tbl_Artist_Aloud_subscription where status=1 and date(sub_date)<=date(subdate(now(),"+day+")) group by circle";
				ResultSet Rs5dnis = stmt_source.executeQuery(Active_Base);
				System.out.println("***********AIRTEL Active BASE DATA INSERTING AT DESTINATION***********");
				while(Rs5dnis.next())
				{
					CNT = Rs5dnis.getString("cnt");
					CIRCLE = Rs5dnis.getString("circle");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";
					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mous,pulse,total_sec,service_id) values('"+FileDate+"','Active_Base','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','1002')");
				}
				//////////////////////////////////////////////////// Deactivation //////////////////////////////////////////////////
				String Deactivation_Base = "select count(1) as cnt,circle from docomo_hungama.tbl_Artist_Aloud_unsub where status=1 and date(unsub_date)=date(subdate(now(),"+day+")) group by circle";
				ResultSet Rs6dnis = stmt_source.executeQuery(Deactivation_Base);
				System.out.println("***********AIRTEL Deactivation BASE DATA INSERTING AT DESTINATION***********");
				while(Rs6dnis.next())
				{
					CNT = Rs6dnis.getString("cnt");
					CIRCLE = Rs6dnis.getString("circle");

					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mous,pulse,total_sec,service_id) values('"+FileDate+"','Deactivation_30','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','1002')");
				}
				////////////////////////////////////////////// Mode_Deactivation /////////////////////////////////////////////////////////////////////////////
				// start code to insert the Deactivation Base count and unsub reason into the MIS database DocomoAloudArtist
				String Deactivation_Base1 = "select count(1) as cnt,circle,unsub_reason from docomo_hungama.tbl_Artist_Aloud_unsub where status=1 and date(unsub_date)=date(subdate(now(),"+day+")) group by circle,unsub_reason";
				ResultSet Rs7dnis = stmt_source.executeQuery(Deactivation_Base1);
				System.out.println("***********AIRTEL Deactivation BASE DATA INSERTING AT DESTINATION***********");
				while(Rs7dnis.next())
				{
					CNT = Rs7dnis.getString("cnt");
					CIRCLE = Rs7dnis.getString("circle");
					UNSUB_REASON = Rs7dnis.getString("unsub_reason");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					if(UNSUB_REASON == "Involuntary") UNSUB_REASON = "in";
					if(UNSUB_REASON == "546461" || UNSUB_REASON == "BULK" || UNSUB_REASON == "SELF_REQS" || UNSUB_REASON == "SELF_REQ") UNSUB_REASON ="IVR";
					deactivation_str1="Mode_Deactivation_"+UNSUB_REASON;

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+deactivation_str1+"','"+CIRCLE+"',0,'"+CNT+"','"+UNSUB_REASON+"','NA','NA','NA','1002')");
				}
				///////////////////////////////////////////////////////////// CALL_TF //////////////////////////////////////////////////////////////////////
				//start code to insert the data for call_tf DocomoAloudArtist
				String Call_TF_Query = "select count(1) as cnt,circle from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle";
				System.out.println("***********AIRTEL CALL_TF DATA INSERTING AT DESTINATION***********");
				ResultSet Rs8dnis = stmt_source.executeQuery(Call_TF_Query);
				while(Rs8dnis.next())
				{
					CNT = Rs8dnis.getString("cnt");
					CIRCLE = Rs8dnis.getString("circle");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','CALLS_TF','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
				}
				String Call_TF_Query1 = "select count(1) as cnt,circle,status from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle,status";
				ResultSet Rs9dnis = stmt_source.executeQuery(Call_TF_Query1);
				System.out.println("***********AIRTEL CALL_TF LIVE NON LIVE DATA INSERTING AT DESTINATION***********");
				while(Rs9dnis.next())
				{
					CNT = Rs9dnis.getString("cnt");
					CIRCLE = Rs9dnis.getString("circle");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					STATUS = Rs9dnis.getString("status");
					if(STATUS.equals("1"))
						call_TF="L_CALLS_T";
					else
						call_TF="N_CALLS_T";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+call_TF+"','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
				}
				/////////////////////////////////////////////////////////////// MOU_TF //////////////////////////////////////////////////////////////////////////
				//start code to insert the data for mous_tf for DocomoAloudArtist
				String mous_tf_query="select count(id) as cnt,circle,sum(duration_in_sec)/60 as mous from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle";
				ResultSet Rs10dnis = stmt_source.executeQuery(mous_tf_query);
				while(Rs10dnis.next())
				{
					CNT = Rs10dnis.getString("cnt");
					CIRCLE = Rs10dnis.getString("circle");
					MOUS = Rs10dnis.getString("mous");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','MOU_TF','"+CIRCLE+"',0,'"+MOUS+"','NA','"+MOUS+"','NA','NA','1002')");
				}
				////////////////////////////////////////////////////////////////MOU LIVE NON LIVE
				String mous_tf_query1="select count(id) as cnt,circle,sum(duration_in_sec)/60 as mous,status from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle,status";
				ResultSet Rs11dnis = stmt_source.executeQuery(mous_tf_query1);
				while(Rs11dnis.next())
				{
					CNT = Rs11dnis.getString("cnt");
					CIRCLE = Rs11dnis.getString("circle");
					MOUS = Rs11dnis.getString("mous");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					STATUS = Rs11dnis.getString("status");
					if(STATUS.equals("1"))
						Mous_TF="L_MOU_TF";
					else
						Mous_TF="N_MOU_TF";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+Mous_TF+"','"+CIRCLE+"',0,'"+MOUS+"','NA','"+MOUS+"','NA','NA','1002')");
				}
				////////////////////////////////////////////////////////////////PULSE LIVE NON LIVE
				//start code to insert the data for PULSE_TF DocomoAloudArtist
				String Pulse_TF_Query="select count(id) as cnt,circle,sum(ceiling(duration_in_sec/60)) as pulse from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle";
				ResultSet Rs12dnis = stmt_source.executeQuery(Pulse_TF_Query);
				while(Rs12dnis.next())
				{
					CNT = Rs12dnis.getString("cnt");
					CIRCLE = Rs12dnis.getString("circle");
					PULSE = Rs12dnis.getString("pulse");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','PULSE_TF','"+CIRCLE+"',0,'"+PULSE+"','NA','NA','"+PULSE+"','NA','1002')");
				}//END

				//start code to insert the data for PULSE_TF LIVE ABND NON LIVE USERS DocomoAloudArtist
				String Pulse_TF_Query1="select count(id) as cnt,circle,sum(ceiling(duration_in_sec/60)) as pulse,status from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle,status";
				ResultSet Rs13dnis = stmt_source.executeQuery(Pulse_TF_Query1);
				while(Rs13dnis.next())
				{
					CNT = Rs13dnis.getString("cnt");
					CIRCLE = Rs13dnis.getString("circle");
					PULSE = Rs13dnis.getString("pulse");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					STATUS = Rs13dnis.getString("status");
					if(STATUS.equals("1"))
						Pulse_TF="L_PULSE_TF";
					else
						Pulse_TF="N_PULSE_TF";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+Pulse_TF+"','"+CIRCLE+"',0,'"+PULSE+"','NA','NA','"+PULSE+"','NA','1002')");
				}//END
				//////////////////////////////////////////////////////// UU_TF
				//start code to insert the data for Unique Users  for airtel mnd KK
				String UU_TF_Query="select count(distinct msisdn) as cnt,circle from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle";
				System.out.println("***********AIRTEL UU BASE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs14dnis = stmt_source.executeQuery(UU_TF_Query);
				while(Rs14dnis.next())
				{
					CNT = Rs14dnis.getString("cnt");
					CIRCLE = Rs14dnis.getString("circle");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','UU_TF','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
				}//END

				//start code to insert the data for live and non live Unique Users  for airtel mnd KK
				//String UU_TF_Query1="select count(distinct msisdn) as cnt,circle,if(status=1,'Active','c') 'user_status' from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle";
				String UU_TF_Query1="(select circle, count(distinct msisdn) as cnt,'Non Active' as user_status from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in(5464611) and operator='tatm' and status in(-1,11,0) AND MSISDN  NOT IN( select DISTINCT MSISDN from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in(5464611) and operator='tatm' and status IN (1)) group by circle) UNION (select circle, count(distinct msisdn),'Active' as user_status from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in(5464611) and operator='tatm' and status=1 group by circle)";
				System.out.println("***********AIRTEL UU LIVE AND NON LIVE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs15dnis = stmt_source.executeQuery(UU_TF_Query1);
				while(Rs15dnis.next())
				{
					CIRCLE = Rs15dnis.getString("circle");
					CNT = Rs15dnis.getString("cnt");
					USER_STATUS = Rs15dnis.getString("user_status");
					USER_STATUS=USER_STATUS.trim();

					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					if(USER_STATUS.equals("Active"))
							UU_TF_TYPE="L_UU_TF";
					else if(USER_STATUS.equals("Non Active"))
							UU_TF_TYPE="N_UU_TF";
					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+UU_TF_TYPE+"','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
				}//END
				//////////////////////////////////////////////////////// SEC_TF
				//start code to insert the data for SEC TF  for airtel mnd KK
				String SEC_TF_Query="select count(id) as cnt,circle,sum(duration_in_sec) as duration from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle";
				ResultSet Rs16dnis = stmt_source.executeQuery(SEC_TF_Query);
				while(Rs16dnis.next())
				{
					CNT = Rs16dnis.getString("cnt");
					CIRCLE = Rs16dnis.getString("circle");
					DURATION = Rs16dnis.getString("duration");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','SEC_TF','"+CIRCLE+"',0,'"+DURATION+"','NA','NA','NA','"+DURATION+"','1002')");
				}//END

				//start code to insert the data for SEC_TF LIVE AND NON LIVE USERS DocomoAloudArtist
				String Sec_TF_Query1="select count(id) as cnt,circle,sum(duration_in_sec) as duration,status from mis_db.tbl_54646_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('5464611') and operator='tatm' group by circle,status";
				System.out.println("***********AIRTEL SEC LIVE AND NON LIVE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs17dnis = stmt_source.executeQuery(Sec_TF_Query1);
				while(Rs17dnis.next())
				{
					CNT = Rs17dnis.getString("cnt");
					CIRCLE = Rs17dnis.getString("circle");
					DURATION = Rs17dnis.getString("duration");
					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					STATUS = Rs17dnis.getString("status");
					if(STATUS =="1")
						SEC_TF_TYPE="L_SEC_TF";
					else
						SEC_TF_TYPE="N_SEC_TF";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+SEC_TF_TYPE+"','"+CIRCLE+"',0,'"+DURATION+"','NA','NA','NA','"+DURATION+"','1002')");
				}//END
				/*//FAILURE TABLE DATA INSERTING INTO TABLE
				String charging_fail="select count(1) as cnt,circle,event_type,service_id from master_db.tbl_billing_failure where date(date_time)=date(subdate(now(),"+day+")) group by circle,event_type,service_id order by service_id";
				System.out.println("***********AIRTEL FAILURE SUB and RESUB DATA INSERTING AT DESTINATION***********");
				ResultSet Rs18dnis = stmt_source.executeQuery(charging_fail);
				while(Rs18dnis.next())
				{
					CNT = Rs18dnis.getString("cnt");
					CIRCLE = Rs18dnis.getString("circle");
					EVENTTYPE= Rs18dnis.getString("event_type");
					SERVICE_ID= Rs18dnis.getString("service_id");

					if(CIRCLE == "") CIRCLE="UND";
					else if(CIRCLE == "HAR") CIRCLE="HAY";
					else if(CIRCLE == "PUN") CIRCLE="PUB";

					if(SERVICEID == 1511 && (PLANID==30 || PLANID==48))
							SERVICEID = 1509;
					else if(SERVICEID == 1511 && (PLANID==29 || PLANID==46))
							SERVICEID = 1511;
					else if(SERVICEID == 1522)
					{
							if(PLANID==63) SERVICEID = 15222;
							else if(PLANID==64) SERVICEID = 15221;
					}

					if(EVENT_TYPE.equals("SUB"))
						FAIL_EVENT_TYPE="FAIL_ACT";
					else if(EVENT_TYPE.equals("RESUB"))
						FAIL_EVENT_TYPE="FAIL_REN";
					else if(EVENT_TYPE.equals("TOPUP"))
						FAIL_EVENT_TYPE="FAIL_TOP";

					stmt_destination.executeUpdate("insert into mis_db.tbl_AA_daily_report_test(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+FAIL_EVENT_TYPE+"','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
				}*/
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
					stmt_source.close();
					if (con_source != null)
						con_source.close();
				}
				catch(Exception e) {}

				try
				{
					stmt_destination.close();
					if (con_destination != null)
						con_destination.close();
				}
				catch (Exception e){}
		  }
	}


}
