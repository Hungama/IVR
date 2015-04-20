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

	public class TuneTalkMisNew extends Thread
	{
	public static Connection con_source=null,con_destination=null;
	public static Statement stmt_source,stmt_destination;
	public String sIP=null,sDSN=null,sUSR=null,sPWD=null;
	public String dIP=null,dDSN=null,dUSR=null,dPWD=null;
	public static CallableStatement cstmt=null;
	public static int day=1;
	public static int BaseCnt1=0;
	public static int BaseCnt2=0;
	public static int BaseCnt3=0;
    public static int CHARGAMT1=0;
	public static int i=0;
	public void readDBCONFIG()
	{
		try
		{
			System.out.println("**********************************************************");
			System.out.println("**     Thread Started With The Following Configuration  **");
			System.out.println("**              File to be Read is DB.CFG          **");

			sIP="10.151.41.83";
			sDSN="master_db";
			sUSR="Manish_dev";
			sPWD="Man@123";

			/*dIP="119.82.69.218";
			dDSN="misdata";
			dUSR="kunalk.arora";
			dPWD="google";*/
			dIP="192.168.100.218";
			dDSN="misdata";
			dUSR="billing";
			dPWD="billing";

			System.out.println("**DB IP is  ["+sIP+"] **  DSN is ["+sDSN+"] Usr is ["+sUSR+"] Pwd is ["+sPWD+"]\t**");
			System.out.println("**********************************************************");

		}
		catch(Exception e)
		{
			System.out.println("Exception while reading DB.cfg");
			e.printStackTrace();

		}
	}

	public TuneTalkMisNew()
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
			System.out.println("###DB CONNECTION UP WITH DB###");
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
		TuneTalkMisNew c = new TuneTalkMisNew();
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
					System.out.println("****Running TuneTalkMisNew for date :****"+FileDate);
				}
				int SERVICEID = 0;
				int PLANID = 0;
				/*if(day>2)
					BillingTable="master_db.tbl_billing_success_backup";
				else
					BillingTable="master_db.tbl_billing_success";
				*/
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

				String str1 = "select count(1) as CNT from misdata.dailymis where Date = date(subdate(now(),"+day+")) and Service='TuneTalkIVR'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from misdata.dailymis where Date = date(subdate(now(),"+day+")) and Service='TuneTalkIVR'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				// Start the code to insert the data of activation and renewal
	            String Activation_Query = "select count(msisdn) as cnt,chrg_amount,sum(chrg_amount) as Revenue,service_id,event_type from "+ BillingTable +" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id in(1901) and event_type in('SUB','RESUB','TOPUP') group by chrg_amount,service_id,event_type";
				ResultSet Rs2dnis = stmt_source.executeQuery(Activation_Query);
				System.out.println("***********TUNETALK ACTIVATION DATA INSERTING AT DESTINATION***********");
				while(Rs2dnis.next())
				{
					try
					{
	            	    CNT = Rs2dnis.getString("cnt");
	            	    CHARGAMT = Rs2dnis.getString("chrg_amount");
	            	    REVENUE = Rs2dnis.getString("Revenue");
						SERVICEID = Rs2dnis.getInt("service_id");
	            	    EVENT_TYPE = Rs2dnis.getString("event_type");

						if(CHARGAMT.equals("1.00")) CHARGAMT="1";
						else if(CHARGAMT.equals("1.50")) CHARGAMT="1.5";
						else if(CHARGAMT.equals("2.00")) CHARGAMT="2";
						else if(CHARGAMT.equals("2.50")) CHARGAMT="2.5";
						else if(CHARGAMT.equals("3.00")) CHARGAMT="3";

						if(EVENT_TYPE.equals("SUB"))
							EVENT_TYPE="Activation_"+CHARGAMT;
						else if(EVENT_TYPE.equals("RESUB"))
							EVENT_TYPE="Renewal_"+CHARGAMT;
						else if(EVENT_TYPE.equals("TOPUP"))
							EVENT_TYPE="TOP-UP_"+CHARGAMT;

						stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','"+EVENT_TYPE+"','"+CNT+"','"+REVENUE+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
				}// end the code to insert the data of activation and renewal

				//Start the code to activation Record mode wise
				String Query_Modewise = "select count(msisdn) as cnt,chrg_amount,service_id,event_type,mode from "+ BillingTable +" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id in(1901) and event_type in('SUB','TOPUP') group by chrg_amount,service_id,event_type,mode";
				//$get_activation_query="select count(msisdn),circle,chrg_amount,service_id,event_type,mode from master_db.tbl_billing_success nolock where DATE(response_time)='$view_date1' and service_id in(1901) and event_type in('SUB') group by service_id,chrg_amount,event_type,mode";
				ResultSet Rs3dnis = stmt_source.executeQuery(Query_Modewise);
				System.out.println("***********TUNETALK MODE WISE DATA INSERTING AT DESTINATION***********");
				while(Rs3dnis.next())
				{
					CNT = Rs3dnis.getString("cnt");
					CHARGAMT = Rs3dnis.getString("chrg_amount");
					SERVICEID = Rs3dnis.getInt("service_id");
					EVENT_TYPE = Rs3dnis.getString("event_type");
					MODE = Rs3dnis.getString("mode");

					if(CHARGAMT.equals("1.00")) CHARGAMT="1";
					else if(CHARGAMT.equals("1.50")) CHARGAMT="1.5";
					else if(CHARGAMT.equals("2.00")) CHARGAMT="2";
					else if(CHARGAMT.equals("2.50")) CHARGAMT="2.5";
					else if(CHARGAMT.equals("3.00")) CHARGAMT="3";

					if(MODE.equals("SELF_REQ")) MODE="IVR";

					if(EVENT_TYPE.equals("SUB"))
						MODE_TYPE="Mode_Activation_"+MODE.toUpperCase();
					else if(EVENT_TYPE.equals("TOPUP"))
						MODE_TYPE="Mode_TOP-UP_IVR";
				   // System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
				   	//System.out.println("insert into misdata.dailymis(report_date,type,circle,service_id,charging_rate,total_count,mous,pulse,total_sec) values('"+FileDate+"','"+MODE_TYPE+"','"+CIRCLE+"','"+SERVICEID+"','"+CHARGAMT+"','"+CNT+"','NA','NA','NA')");
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,service_id,charging_rate,total_count,mous,pulse,total_sec) values('"+FileDate+"','"+MODE_TYPE+"','Indian','"+SERVICEID+"','"+CHARGAMT+"','"+CNT+"','NA','NA','NA')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','"+MODE_TYPE+"','"+CNT+"','"+CHARGAMT+"')");
				}
				/////////////////////////////////// pending Base ////////////////////////////////////////////////////////////
				// start code to insert the Pending Base date into the database airtel
				String Pending_Base = "select count(1) as cnt from tunetalk_radio.tbl_tunetalk_subscription where status=11 and date(sub_date)<=date(subdate(now(),"+day+"))";
				ResultSet Rs4dnis = stmt_source.executeQuery(Pending_Base);
				System.out.println("***********TUNETALK PENDING BASE DATA INSERTING AT DESTINATION***********");
				if(Rs4dnis.next())
				{
					CNT = Rs4dnis.getString("cnt");
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mous,pulse,total_sec,service_id) values('"+FileDate+"','Pending_Base','Indian',0,'"+CNT+"','NA','NA','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','Pending_Base','"+CNT+"','0.00')");
				}
				////////////////////////////////////////////////////// Active Base ///////////////////////////////////////////////////////////////
				String Active_Base = "select count(1) as cnt from tunetalk_radio.tbl_tunetalk_subscription where status=1 and date(sub_date)<=date(subdate(now(),"+day+")) ";
				ResultSet Rs5dnis = stmt_source.executeQuery(Active_Base);
				System.out.println("***********TUNETALK Active BASE DATA INSERTING AT DESTINATION***********");
				if(Rs5dnis.next())
				{
					BaseCnt1 = Rs5dnis.getInt("cnt");

				}
				String Active_Base1 = "select count(1) as cnt from tunetalk_radio.tbl_tunetalk_unsub where status in (1) and date(unsub_date)> date(subdate(now(),"+day+")) and date(sub_date) < date(now())";
				ResultSet Rs55dnis = stmt_source.executeQuery(Active_Base1);
				if(Rs55dnis.next())
				{
					BaseCnt2 = Rs55dnis.getInt("cnt");
				}
				BaseCnt3 = BaseCnt1 + BaseCnt2;
				//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mous,pulse,total_sec,service_id) values('"+FileDate+"','Active_Base','Indian',0,'"+BaseCnt3+"','NA','NA','NA','1901')");
				stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','Active_Base','"+BaseCnt3+"','0.00')");
				//////////////////////////////////////////////////// Deactivation //////////////////////////////////////////////////
				String Deactivation_Base = "select count(1) as cnt from tunetalk_radio.tbl_tunetalk_unsub where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs6dnis = stmt_source.executeQuery(Deactivation_Base);
				System.out.println("***********TUNETALK Deactivation BASE DATA INSERTING AT DESTINATION***********");
				if(Rs6dnis.next())
				{
					CNT = Rs6dnis.getString("cnt");
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mous,pulse,total_sec,service_id) values('"+FileDate+"','Deactivation_2','Indian',0,'"+CNT+"','NA','NA','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','Deactivation_2','"+CNT+"','0.00')");
				}
				////////////////////////////////////////////// Mode_Deactivation /////////////////////////////////////////////////////////////////////////////
				// start code to insert the Deactivation Base count and unsub reason into the MIS database Airtel MNDKK
				String Deactivation_Base1 = "select count(1) as cnt,unsub_reason from tunetalk_radio.tbl_tunetalk_unsub where date(unsub_date)=date(subdate(now(),"+day+")) group by unsub_reason";
				ResultSet Rs7dnis = stmt_source.executeQuery(Deactivation_Base1);
				System.out.println("***********TUNETALK Deactivation BASE DATA INSERTING AT DESTINATION***********");
				while(Rs7dnis.next())
				{
					CNT = Rs7dnis.getString("cnt");
					UNSUB_REASON = Rs7dnis.getString("unsub_reason");


					if(UNSUB_REASON.equals("Involuntary")) UNSUB_REASON = "in";
					if(UNSUB_REASON.equals("SELF_REQS") || UNSUB_REASON.equals("SELF_REQ")) UNSUB_REASON ="IVR";
					deactivation_str1="Mode_Deactivation_"+UNSUB_REASON;

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+deactivation_str1+"','Indian',0,'"+CNT+"','"+UNSUB_REASON+"','NA','NA','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','"+deactivation_str1+"','"+CNT+"','0.00')");
				}
				///////////////////////////////////////////////////////////// CALL_TF //////////////////////////////////////////////////////////////////////
				//start code to insert the data for call_tf TUNETALK
				String Call_TF_Query = "select count(1) as cnt from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%' ";
				System.out.println("***********TUNETALK CALL_TF DATA INSERTING AT DESTINATION***********");
				ResultSet Rs8dnis = stmt_source.executeQuery(Call_TF_Query);
				if(Rs8dnis.next())
				{
					CNT = Rs8dnis.getString("cnt");
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','CALLS_TF','Indian',0,'"+CNT+"','NA','NA','NA','NA','1901')");
					  stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','CALLS_TF','"+CNT+"','0.00')");
				}
				String Call_TF_Query1 = "select count(1) as cnt,status from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%' group by status";
				ResultSet Rs9dnis = stmt_source.executeQuery(Call_TF_Query1);
				System.out.println("***********TUNETALK CALL_TF LIVE NON LIVE DATA INSERTING AT DESTINATION***********");
				while(Rs9dnis.next())
				{
					CNT = Rs9dnis.getString("cnt");
					STATUS = Rs9dnis.getString("status");
					if(STATUS.equals("1"))
						call_TF="L_CALLS_T";
					else
						call_TF="N_CALLS_T";

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+call_TF+"','Indian',0,'"+CNT+"','NA','NA','NA','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','"+call_TF+"','"+CNT+"','0.00')");
				}
				/////////////////////////////////////////////////////////////// MOU_TF //////////////////////////////////////////////////////////////////////////
				//start code to insert the data for mous_tf for TUNETALK
				String mous_tf_query="select sum(duration_in_sec)/60 as mous from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%'";
				ResultSet Rs10dnis = stmt_source.executeQuery(mous_tf_query);
				if(Rs10dnis.next())
				{
					MOUS = Rs10dnis.getString("mous");
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','MOU_TF','Indian',0,'"+MOUS+"','NA','"+MOUS+"','NA','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','MOU_TF','"+MOUS+"','0.00')");
				}
				////////////////////////////////////////////////////////////////MOU LIVE NON LIVE
				String mous_tf_query1="select count(id) as cnt,sum(duration_in_sec)/60 as mous,status from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%' group by status";
				ResultSet Rs11dnis = stmt_source.executeQuery(mous_tf_query1);
				while(Rs11dnis.next())
				{
					CNT = Rs11dnis.getString("cnt");
					MOUS = Rs11dnis.getString("mous");
					STATUS = Rs11dnis.getString("status");
					if(STATUS.equals("1"))
						Mous_TF="L_MOU_TF";
					else
						Mous_TF="N_MOU_TF";

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+Mous_TF+"','Indian',0,'"+MOUS+"','NA','"+MOUS+"','NA','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','"+Mous_TF+"','"+MOUS+"','0.00')");
				}
				////////////////////////////////////////////////////////////////PULSE LIVE NON LIVE
				//start code to insert the data for PULSE_TF TUNETALK
				String Pulse_TF_Query="select sum(ceiling(duration_in_sec/60)) as pulse from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%'";
				ResultSet Rs12dnis = stmt_source.executeQuery(Pulse_TF_Query);
				if(Rs12dnis.next())
				{
					PULSE = Rs12dnis.getString("pulse");
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','PULSE_TF','Indian',0,'"+PULSE+"','NA','NA','"+PULSE+"','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','PULSE_TF','"+PULSE+"','0.00')");
				}//END

				//start code to insert the data for PULSE_TF LIVE ABND NON LIVE USERS TUNETALK
				String Pulse_TF_Query1="select count(id) as cnt,sum(ceiling(duration_in_sec/60)) as pulse,status from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%' group by status";
				ResultSet Rs13dnis = stmt_source.executeQuery(Pulse_TF_Query1);
				while(Rs13dnis.next())
				{
					CNT = Rs13dnis.getString("cnt");
					PULSE = Rs13dnis.getString("pulse");
					STATUS = Rs13dnis.getString("status");

					if(STATUS.equals("1"))
						Pulse_TF="L_PULSE_TF";
					else
						Pulse_TF="N_PULSE_TF";

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+Pulse_TF+"','Indian',0,'"+PULSE+"','NA','NA','"+PULSE+"','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','"+Pulse_TF+"','"+PULSE+"','0.00')");
				}//END
				//////////////////////////////////////////////////////// UU_TF
				//start code to insert the data for Unique Users  for TUNETALK
				String UU_TF_Query="select count(distinct msisdn) as cnt from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%'";
				System.out.println("***********TUNETALK UU BASE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs14dnis = stmt_source.executeQuery(UU_TF_Query);
				if(Rs14dnis.next())
				{
					CNT = Rs14dnis.getString("cnt");
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','UU_TF','Indian',0,'"+CNT+"','NA','NA','NA','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','UU_TF','"+CNT+"','0.00')");
				}//END

				//start code to insert the data for live and non live Unique Users  for TUNETALK
				//String UU_TF_Query1="select count(distinct msisdn) as cnt,circle,if(status=1,'Active','c') 'user_status' from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis in('54646196') and circle ='kar' and operator='airm' group by circle";
				String UU_TF_Query1="(select count(distinct msisdn) as cnt,'NonActive' as user_status from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%' and status in(-1,11,0) AND MSISDN  NOT IN( select DISTINCT MSISDN from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),1)) and dnis like '13131%' and status IN (1))) UNION (select count(distinct msisdn),'Active' as user_status from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%' and status=1)";
				System.out.println("***********TUNETALK UU LIVE AND NON LIVE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs15dnis = stmt_source.executeQuery(UU_TF_Query1);
				while(Rs15dnis.next())
				{
					CNT = Rs15dnis.getString("cnt");
					USER_STATUS=Rs15dnis.getString("user_status");
					USER_STATUS=USER_STATUS.trim();

					if(USER_STATUS.equals("Active"))
							UU_TF_TYPE="L_UU_TF";
					else if(USER_STATUS.equals("NonActive"))
							UU_TF_TYPE="N_UU_TF";
					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+UU_TF_TYPE+"','Indian',0,'"+CNT+"','NA','NA','NA','NA','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','"+UU_TF_TYPE+"','"+CNT+"','0.00')");
				}//END
				//////////////////////////////////////////////////////// SEC_TF
				//start code to insert the data for SEC TF  for TUNETALK
				String SEC_TF_Query="select sum(duration_in_sec) as duration from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%'";
				ResultSet Rs16dnis = stmt_source.executeQuery(SEC_TF_Query);
				if(Rs16dnis.next())
				{
					DURATION = Rs16dnis.getString("duration");

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','SEC_TF','Indian',0,'"+DURATION+"','NA','NA','NA','"+DURATION+"','1901')")
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','SEC_TF','"+DURATION+"','0.00')");
				}//END

				//start code to insert the data for SEC_TF LIVE AND NON LIVE USERS TUNETALK
				String Sec_TF_Query1="select count(id) as cnt,sum(duration_in_sec) as duration,status from mis_db.tbl_tune_calllog where date(call_date)=date(subdate(now(),"+day+")) and dnis like '13131%' group by status";
				System.out.println("***********TUNETALK SEC LIVE AND NON LIVE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs17dnis = stmt_source.executeQuery(Sec_TF_Query1);
				while(Rs17dnis.next())
				{
					CNT = Rs17dnis.getString("cnt");
					DURATION = Rs17dnis.getString("duration");
					STATUS = Rs17dnis.getString("status");

					if(STATUS.equals("1"))
						SEC_TF_TYPE="L_SEC_TF";
					else
						SEC_TF_TYPE="N_SEC_TF";

					//stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+SEC_TF_TYPE+"','Indian',0,'"+DURATION+"','NA','NA','NA','"+DURATION+"','1901')");
					stmt_destination.executeUpdate("insert into misdata.dailymis(Date,Service,Circle,Type,Value,Revenue) values('"+FileDate+"','TuneTalkIVR','Indian','"+SEC_TF_TYPE+"','"+DURATION+"','0.00')");
				}//END
				/*String charging_fail="select count(1) as cnt,circle,event_type,service_id from master_db.tbl_billing_failure where date(date_time)=date(subdate(now(),"+day+")) group by circle,event_type,service_id order by service_id";
				System.out.println("***********TUNETALK FAILURE SUB and RESUB DATA INSERTING AT DESTINATION***********");
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

					stmt_destination.executeUpdate("insert into misdata.dailymis(report_date,type,circle,charging_rate,total_count,mode_of_sub,mous,pulse,total_sec,service_id) values('"+FileDate+"','"+FAIL_EVENT_TYPE+"','"+CIRCLE+"',0,'"+CNT+"','NA','NA','NA','NA',1513)");
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
