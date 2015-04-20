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


	public class AudioCinemaMis extends Thread
	{
	public static Connection con_source=null,con_destination=null;
	public static Statement stmt_source,stmt_destination;
	public String sIP=null,sDSN=null,sUSR=null,sPWD=null;
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
			System.out.println("**     Thread Started With The Following Configuration  **");
			System.out.println("**              File to be Read is DB.CFG          **");

			sIP="10.130.14.160";
			sDSN="master_db";
			sUSR="ivr";
			sPWD="ivr";

			/*dIP="119.82.69.218";
			dDSN="misdata";
			dUSR="kunalk.arora";
			dPWD="google";*/
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

	public AudioCinemaMis()
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
	public static void main(String arg[])
	{
		if(arg.length>0)
			day = Integer.parseInt(arg[0]);
		else
			day = 1;
		AudioCinemaMis c = new AudioCinemaMis();
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
					System.out.println("****Running AudioCinemamis for date :****"+FileDate);
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
				String REVENUE = "";
				String CIRCLE = "";
				String TYPE = "";
				String USER_TYPE = "";
				String SERVICE = "";
				String DATE1 = "";
				String TIME1 = "";
				String ModeRevenue="";

				String str1 = "select count(1) as CNT from misdata.dailymis where Date = date(subdate(now(),"+day+")) and Service='MTSAC'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from misdata.dailymis where Date = date(subdate(now(),"+day+")) and Service='MTSAC'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
	            String Billing_Success = "select count(1) as cnt,circle,chrg_amount,sum(chrg_amount) as Revenue,service_id,event_type,plan_id from "+ BillingTable +" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id in(1124) and event_type in('SUB','RESUB','TOPUP') and SC not like '%P%' group by circle,service_id,chrg_amount,event_type,plan_id";
				ResultSet Rs2dnis = stmt_source.executeQuery(Billing_Success);
				System.out.println("***********AUDIO CINEMA ACTIVATION DATA INSERTING AT DESTINATION***********");
				while(Rs2dnis.next())
				{
					try
					{
	            	    CNT = Rs2dnis.getString("cnt");
	            	    CIRCLE = Rs2dnis.getString("circle");
	            	    if(CIRCLE.equals("")) CIRCLE="UND";
						else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
						else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
						CIRCLE = getCIRCLE(CIRCLE);
	            	    CHARGAMT = Rs2dnis.getString("chrg_amount");
	            	    REVENUE = Rs2dnis.getString("revenue");
	            	    SERVICEID = Rs2dnis.getInt("service_id");
	            	    EVENT_TYPE = Rs2dnis.getString("event_type");
	            	    PLANID = Rs2dnis.getInt("plan_id");

						if(EVENT_TYPE.equals("SUB"))
							EVENT_TYPE="Activation_"+CHARGAMT;
						else if(EVENT_TYPE.equals("RESUB"))
							EVENT_TYPE="Renewal_"+CHARGAMT;
						else if(EVENT_TYPE.equals("TOPUP"))
							EVENT_TYPE="TOP-UP_"+CHARGAMT;

						stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','"+EVENT_TYPE+"','"+CNT+"','"+REVENUE+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
				}// end the code to insert the data of activation Airtel

				//Start the code to activation Record mode wise
				String Billing_Modewise = "select count(1) as cnt,circle,service_id,event_type,mode,plan_id,sum(chrg_amount) as Revenue from "+ BillingTable +" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id in(1124) and event_type in('SUB','TOPUP','RESUB') and SC not like '%P%' group by circle,service_id,event_type,mode order by circle,event_type,mode"; //and plan_id!=29
				ResultSet Rs3dnis = stmt_source.executeQuery(Billing_Modewise);
				System.out.println("***********AUDIO CINEMA MODE WISE DATA INSERTING AT DESTINATION***********");
				while(Rs3dnis.next())
				{
					CNT = Rs3dnis.getString("cnt");
					CIRCLE = Rs3dnis.getString("circle");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);
					SERVICEID = Rs3dnis.getInt("service_id");
					EVENT_TYPE = Rs3dnis.getString("event_type");
					MODE = Rs3dnis.getString("mode");
					PLANID = Rs3dnis.getInt("plan_id");
					ModeRevenue = Rs3dnis.getString("revenue");

					if(EVENT_TYPE.equals("SUB"))
						MODE_TYPE="Mode_Activation_"+MODE.toUpperCase();
					if(EVENT_TYPE.equals("RESUB"))
						MODE_TYPE="Mode_Renewal_"+MODE.toUpperCase();
					else if(EVENT_TYPE.equals("TOPUP"))
						MODE_TYPE="Mode_TOP-UP_IVR";
				   //System.out.println("insert into misdata.dailymis(report_date,type,circle,service_id,charging_rate,total_count,mous,pulse,total_sec) values('"+FileDate+"','"+MODE_TYPE+"','"+CIRCLE+"','"+SERVICEID+"','"+CHARGAMT+"','"+CNT+"','NA','NA','NA')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','"+MODE_TYPE+"','"+CNT+"','"+ModeRevenue+"')");
				}
				/////////////////////////////////// pending Base ////////////////////////////////////////////////////////////
				// start code to insert the Pending Base date into the database airtel
				String Pending_Base = "select count(1) as cnt from misdata.tbl_base_active where Date = date(subdate(now(),"+day+")) and Service='MTSAC' and status='Pending'";
				ResultSet Rs4dnis = stmt_destination.executeQuery(Pending_Base);
				System.out.println("***********AUDIO CINEMA PENDING BASE DATA INSERTING AT DESTINATION***********");
				if(Rs4dnis.next())
				{
					CNT = Rs4dnis.getString("cnt");
					Rs4dnis.close();
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mous,pulse,total_sec,service_id) values('"+FileDate+"','Pending_Base','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','Tamil Nadu','Pending_Base','"+CNT+"','0.00')");
				}
				////////////////////////////////////////////////////// Active Base ///////////////////////////////////////////////////////////////
				String Active_Base = "select count(1) as cnt from misdata.tbl_base_active where Date = date(subdate(now(),"+day+")) and Service='MTSAC' and status='Active'";
				ResultSet Rs5dnis = stmt_destination.executeQuery(Active_Base);
				System.out.println("***********AUDIO CINEMA Active BASE DATA INSERTING AT DESTINATION***********");
				if(Rs5dnis.next())
				{
					CNT = Rs5dnis.getString("cnt");
					Rs5dnis.close();
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','Tamil Nadu','Active_Base','"+CNT+"','0.00')");
				}
				//////////////////////////////////////////////////// Deactivation //////////////////////////////////////////////////
				String Deactivation_Base = "select count(1) as cnt,circle from mts_radio.tbl_AudioCinema_unsub where status=1 and date(unsub_date)=date(subdate(now(),"+day+")) group by circle";
				ResultSet Rs6dnis = stmt_source.executeQuery(Deactivation_Base);
				System.out.println("***********AUDIO CINEMA Deactivation BASE DATA INSERTING AT DESTINATION***********");
				while(Rs6dnis.next())
				{
					CNT = Rs6dnis.getString("cnt");
					CIRCLE = Rs6dnis.getString("circle");

					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mous,pulse,total_sec,service_id) values('"+FileDate+"','Deactivation_30','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value) values('"+FileDate+"','MTSAC','"+CIRCLE+"','Deactivation_30','"+CNT+"')");
				}
				////////////////////////////////////////////// Mode_Deactivation /////////////////////////////////////////////////////////////////////////////
				// start code to insert the Deactivation Base count and unsub reason into the MIS database Audio Cinema
				String Deactivation_Base1 = "select count(1) as cnt,circle,unsub_reason from mts_radio.tbl_AudioCinema_unsub where status=1 and date(unsub_date)=date(subdate(now(),"+day+")) group by circle,unsub_reason";
				ResultSet Rs7dnis = stmt_source.executeQuery(Deactivation_Base1);
				System.out.println("***********AUDIO CINEMA Deactivation BASE DATA INSERTING AT DESTINATION***********");
				while(Rs7dnis.next())
				{
					CNT = Rs7dnis.getString("cnt");
					CIRCLE = Rs7dnis.getString("circle");
					UNSUB_REASON = Rs7dnis.getString("unsub_reason");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					if(UNSUB_REASON.equals("Involuntary")) UNSUB_REASON = "in";
					if(UNSUB_REASON.equals("SELF_REQS") || UNSUB_REASON.equals("SELF_REQ")) UNSUB_REASON ="IVR";
					deactivation_str1="Mode_Deactivation_"+UNSUB_REASON;

					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','"+deactivation_str1+"','"+CNT+"','0.00')");
				}
				///////////////////////////////////////////////////////////// CALL_TF //////////////////////////////////////////////////////////////////////
				//start code to insert the data for call_tf Audio Cinema
				String Call_TF_Query = "select count(1) as cnt,circle from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+")) and circle='tnu' group by circle";
				System.out.println("***********AUDIO CINEMA CALL_TF DATA INSERTING AT DESTINATION***********");
				ResultSet Rs8dnis = stmt_source.executeQuery(Call_TF_Query);
				while(Rs8dnis.next())
				{
					CNT = Rs8dnis.getString("cnt");
					CIRCLE = Rs8dnis.getString("circle");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','CALLS_TF','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','CALLS_TF','"+CNT+"','0.00')");
				}
				String Call_TF_Query1 = "select count(1) as cnt,circle,status from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+")) and circle='tnu' group by circle,status";
				ResultSet Rs9dnis = stmt_source.executeQuery(Call_TF_Query1);
				System.out.println("***********AUDIO CINEMA CALL_TF LIVE NON LIVE DATA INSERTING AT DESTINATION***********");
				while(Rs9dnis.next())
				{
					CNT = Rs9dnis.getString("cnt");
					CIRCLE = Rs9dnis.getString("circle");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					STATUS = Rs9dnis.getString("status");
					if(STATUS.equals("1"))
						call_TF="L_CALLS_TF";
					else
						call_TF="N_CALLS_TF";

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+call_TF+"','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','"+call_TF+"','"+CNT+"','0.00')");
				}
				/////////////////////////////////////////////////////////////// MOU_TF //////////////////////////////////////////////////////////////////////////
				//start code to insert the data for mous_tf for Audio Cinema
				String mous_tf_query="select count(id) as cnt,circle,sum(duration_in_sec)/60 as mous from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+")) and circle='tnu' group by circle";
				ResultSet Rs10dnis = stmt_source.executeQuery(mous_tf_query);
				while(Rs10dnis.next())
				{
					CNT = Rs10dnis.getString("cnt");
					CIRCLE = Rs10dnis.getString("circle");
					MOUS = Rs10dnis.getString("mous");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','MOU_TF','"+CIRCLE+"',0,'"+MOUS+"','NA','"+MOUS+"','NA','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','MOU_TF','"+MOUS+"','0.00')");
				}
				////////////////////////////////////////////////////////////////MOU LIVE NON LIVE
				String mous_tf_query1="select count(id) as cnt,circle,sum(duration_in_sec)/60 as mous,status from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+"))  and circle='tnu' group by circle,status";
				ResultSet Rs11dnis = stmt_source.executeQuery(mous_tf_query1);
				while(Rs11dnis.next())
				{
					CNT = Rs11dnis.getString("cnt");
					CIRCLE = Rs11dnis.getString("circle");
					MOUS = Rs11dnis.getString("mous");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					STATUS = Rs11dnis.getString("status");
					if(STATUS.equals("1"))
						Mous_TF="L_MOU_TF";
					else
						Mous_TF="N_MOU_TF";

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+Mous_TF+"','"+CIRCLE+"',0,'"+MOUS+"','NA','"+MOUS+"','NA','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','"+Mous_TF+"','"+MOUS+"','0.00')");
				}
				////////////////////////////////////////////////////////////////PULSE LIVE NON LIVE
				//start code to insert the data for PULSE_TF Audio Cinema
				String Pulse_TF_Query="select count(id) as cnt,circle,sum(ceiling(duration_in_sec/60)) as pulse from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+")) and circle='tnu' group by circle";
				ResultSet Rs12dnis = stmt_source.executeQuery(Pulse_TF_Query);
				while(Rs12dnis.next())
				{
					CNT = Rs12dnis.getString("cnt");
					CIRCLE = Rs12dnis.getString("circle");
					PULSE = Rs12dnis.getString("pulse");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','PULSE_TF','"+CIRCLE+"',0,'"+PULSE+"','NA','NA','"+PULSE+"','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','PULSE_TF','"+PULSE+"','0.00')");
				}//END

				//start code to insert the data for PULSE_TF LIVE ABND NON LIVE USERS Audio Cinema
				String Pulse_TF_Query1="select count(id) as cnt,circle,sum(ceiling(duration_in_sec/60)) as pulse,status from mis_db.tbl_AC_calllog where date(call_date)=date(subdate(now(),"+day+")) and circle='tnu' group by circle,status";
				ResultSet Rs13dnis = stmt_source.executeQuery(Pulse_TF_Query1);
				while(Rs13dnis.next())
				{
					CNT = Rs13dnis.getString("cnt");
					CIRCLE = Rs13dnis.getString("circle");
					PULSE = Rs13dnis.getString("pulse");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					STATUS = Rs13dnis.getString("status");
					if(STATUS.equals("1"))
						Pulse_TF="L_PULSE_TF";
					else
						Pulse_TF="N_PULSE_TF";

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+Pulse_TF+"','"+CIRCLE+"',0,'"+PULSE+"','NA','NA','"+PULSE+"','NA','1002')");
					  stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','"+Pulse_TF+"','"+PULSE+"','0.00')");
				}//END
				//////////////////////////////////////////////////////// UU_TF
				//start code to insert the data for Unique Users  for airtel mnd KK
				String UU_TF_Query="select count(distinct msisdn) as cnt,circle from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+"))  and circle='tnu' group by circle";
				System.out.println("***********AUDIO CINEMA UU BASE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs14dnis = stmt_source.executeQuery(UU_TF_Query);
				while(Rs14dnis.next())
				{
					CNT = Rs14dnis.getString("cnt");
					CIRCLE = Rs14dnis.getString("circle");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','UU_TF','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','UU_TF','"+CNT+"','0.00')");
				}//END

				//start code to insert the data for live and non live Unique Users  for airtel mnd KK
				//String UU_TF_Query1="select count(distinct msisdn) as cnt,circle,if(status=1,'Active','c') 'user_status' from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+"))  group by circle";
				String UU_TF_Query1="(select circle, count(distinct msisdn) as cnt,'Non Active' as user_status from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+"))  and circle='tnu' and status in(-1,11,0) AND MSISDN  NOT IN( select DISTINCT MSISDN from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+"))   and circle='tnu' and status IN (1)) group by circle) UNION (select circle, count(distinct msisdn),'Active' as user_status from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+"))  and circle='tnu' and status=1 group by circle)";
				System.out.println("***********AUDIO CINEMA UU LIVE AND NON LIVE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs15dnis = stmt_source.executeQuery(UU_TF_Query1);
				while(Rs15dnis.next())
				{
					CIRCLE = Rs15dnis.getString("circle");
					CNT = Rs15dnis.getString("cnt");
					USER_STATUS = Rs15dnis.getString("user_status");
					USER_STATUS=USER_STATUS.trim();

					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					if(USER_STATUS.equals("Active"))
							UU_TF_TYPE="L_UU_TF";
					else if(USER_STATUS.equals("Non Active"))
							UU_TF_TYPE="N_UU_TF";
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+UU_TF_TYPE+"','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','"+UU_TF_TYPE+"','"+CNT+"','0.00')");
				}//END
				//////////////////////////////////////////////////////// SEC_TF
				//start code to insert the data for SEC TF  for airtel mnd KK
				String SEC_TF_Query="select count(id) as cnt,circle,sum(duration_in_sec) as duration from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+"))  and circle='tnu' group by circle";
				ResultSet Rs16dnis = stmt_source.executeQuery(SEC_TF_Query);
				while(Rs16dnis.next())
				{
					CNT = Rs16dnis.getString("cnt");
					CIRCLE = Rs16dnis.getString("circle");
					DURATION = Rs16dnis.getString("duration");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','SEC_TF','"+CIRCLE+"',0,'"+DURATION+"','NA','NA','NA','"+DURATION+"','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','SEC_TF','"+DURATION+"','0.00')");
				}//END

				//start code to insert the data for SEC_TF LIVE AND NON LIVE USERS Audio Cinema
				String Sec_TF_Query1="select count(id) as cnt,circle,sum(duration_in_sec) as duration,status from mis_db.tbl_AC_calllog  where date(call_date)=date(subdate(now(),"+day+"))  and circle='tnu' group by circle,status";
				System.out.println("***********AUDIO CINEMA SEC LIVE AND NON LIVE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs17dnis = stmt_source.executeQuery(Sec_TF_Query1);
				while(Rs17dnis.next())
				{
					CNT = Rs17dnis.getString("cnt");
					CIRCLE = Rs17dnis.getString("circle");
					DURATION = Rs17dnis.getString("duration");
					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";
					CIRCLE = getCIRCLE(CIRCLE);

					STATUS = Rs17dnis.getString("status");
					if(STATUS =="1")
						SEC_TF_TYPE="L_SEC_TF";
					else
						SEC_TF_TYPE="N_SEC_TF";

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+SEC_TF_TYPE+"','"+CIRCLE+"',0,'"+DURATION+"','NA','NA','NA','"+DURATION+"','1002')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','MTSAC','"+CIRCLE+"','"+SEC_TF_TYPE+"','"+DURATION+"','0.00')");
				}//END
				/*//FAILURE TABLE DATA INSERTING INTO TABLE
				String charging_fail="select count(1) as cnt,circle,event_type,service_id from master_db.tbl_billing_failure where date(date_time)=date(subdate(now(),"+day+")) group by circle,event_type,service_id order by service_id";
				System.out.println("***********AUDIO CINEMA FAILURE SUB and RESUB DATA INSERTING AT DESTINATION***********");
				ResultSet Rs18dnis = stmt_source.executeQuery(charging_fail);
				while(Rs18dnis.next())
				{
					CNT = Rs18dnis.getString("cnt");
					CIRCLE = Rs18dnis.getString("circle");
					EVENTTYPE= Rs18dnis.getString("event_type");
					SERVICE_ID= Rs18dnis.getString("service_id");

					if(CIRCLE.equals("")) CIRCLE="UND";
					else if(CIRCLE.equals("HAR")) CIRCLE="HAY";
					else if(CIRCLE.equals("PUN")) CIRCLE="PUB";

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

					stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+FAIL_EVENT_TYPE+"','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA','1002')");
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
			else if("HAY".equalsIgnoreCase(CIR) || "HAR".equalsIgnoreCase(CIR))
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
			else if("PUB".equalsIgnoreCase(CIR) || "PUN".equalsIgnoreCase(CIR))
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
				c = "others";
			return c;
		}

}
