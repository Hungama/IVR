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
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

	public class MTSDataProcess extends Thread
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

			sIP="database.master_mts";
			sDSN="master_db";
			sUSR="billing";
			sPWD="billing";

			dIP="database.master_mts";
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
	public MTSDataProcess()
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
	/************ MAKE TEST FILES ************/
		public void Fun_FileCreator(int Day)
		{
				try
				{
					CallableStatement cstmtfm1 = null;
					cstmtfm1 = con_destination.prepareCall("{call misdata.MTS_Billing_Data_All(?)}");
					cstmtfm1.setInt(1,Day);
					cstmtfm1.execute();
					cstmtfm1.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	/************ MAKE TEST FILES END************/
	/************ TAR BILLING FILES START ************/
  		public boolean RunScript(String Script)
        {
                try
                {
                        String line;
                        String Output="";
                        Process p = Runtime.getRuntime().exec("sh "+Script);
                        BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
                        while ((line = input.readLine()) != null)
                        {
                                Output=Output+line;
                                System.out.println(line); //<-- Parse data here.
                        }
                        input.close();
                        try
                        {
                                InputStream is = p.getInputStream();
                                InputStream es = p.getErrorStream();
                                OutputStream os = p.getOutputStream();
                                is.close();
                                es.close();
                                os.close();
                        }
                        catch(Exception e1)
                        {
                                e1.printStackTrace();
                        }
                        return true;
                }
                catch(Exception e)
                {
                        e.printStackTrace();
                }
                return false;
        }
	/************ TAR BILLING FILES END************/
	public void run()
	{
		try{
				//day=1;
				String FILEDATE = "";
				String BillingTable = "master_db.tbl_billing_success";
				String query_date = "select date_format(subdate(now(),"+day+"),'%Y%m%d') as 'FILEDATE'";
				ResultSet Rsdate = stmt_source.executeQuery(query_date);
				while(Rsdate.next())
				{
					FILEDATE = Rsdate.getString("FILEDATE");
					System.out.println("****Running for date :****"+FILEDATE);
				}
				if (day > 2)
					BillingTable = "master_db.tbl_billing_success_backup";
				else
					BillingTable = "master_db.tbl_billing_success";

				System.out.println("****Table name is :****"+BillingTable);

				String MSISDN = "";
				String RETRY_TYPE = "";
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

				stmt_destination.executeUpdate("truncate table tbl_billing_mu_mts");
				String MU_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1101";
				ResultSet Rsdnis = stmt_source.executeQuery(MU_Success);
				System.out.println("***********MTS MU SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
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
	                    TIME1 = Rsdnis.getString("cgid");
	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	TIME1 =TIME1;
	                    else
	                    	TIME1 = "X";

	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mu_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String MU_failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid  from master_db.tbl_billing_failure nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1101";
				ResultSet Rs2dnis = stmt_source.executeQuery(MU_failure);
				System.out.println("***********MTS MU FAILURE TABLE DATA INSERTING AT DESTINATION***********");
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
	                    DATE1 = Rs2dnis.getString("date(response_time)");
	                    TIME1 = Rs2dnis.getString("cgid");

						RETRY_TYPE=Rs2dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mu_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String MU_unsub  = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from mts_mu.tbl_HB_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs3dnis = stmt_source.executeQuery(MU_unsub);
				System.out.println("***********MTS MU UNSUB TABLE DATA INSERTING AT DESTINATION***********");
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
	                    TIME1 = Rs3dnis.getString("DCccg_ID");
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mu_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_54646_mts ");
				String Hun54646_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1102";
	            ResultSet Rs4dnis = stmt_source.executeQuery(Hun54646_Success);
				System.out.println("***********MTS 54646 SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
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
						TIME1 = Rs4dnis.getString("cgid");
						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";

						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Hun54646_failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid  from master_db.tbl_billing_failure nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1102";
	            ResultSet Rs5dnis = stmt_source.executeQuery(Hun54646_failure);
				System.out.println("***********MTS 54646 FAILURE TABLE DATA INSERTING AT DESTINATION***********");
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
						DATE1 = Rs5dnis.getString("date(response_time)");
						TIME1 = Rs5dnis.getString("cgid");

						RETRY_TYPE=Rs5dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Hun54646_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from mts_hungama.tbl_jbox_unsub nolock where date(unsub_date) =date(subdate(now(),"+day+"))";
				ResultSet Rs6dnis = stmt_source.executeQuery(Hun54646_unsub);
				System.out.println("***********MTS 54646 UNSUB TABLE DATA INSERTING AT DESTINATION***********");
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
						TIME1 = Rs6dnis.getString("DCccg_ID");

						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_mnd_mts");
				String MND_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1113";
				ResultSet Rs7dnis = stmt_source.executeQuery(MND_Success);
				System.out.println("***********MTS MND Success TABLE DATA INSERTING AT DESTINATION***********");
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
						TIME1 = Rs7dnis.getString("cgid");
						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
//						System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String MND_Failure = "select event_type,concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from master_db.tbl_billing_failure nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1113";
				ResultSet Rs8dnis = stmt_source.executeQuery(MND_Failure);
				System.out.println("***********MTS MND FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs8dnis.next())
				{
					try
					{
						MSISDN = Rs8dnis.getString("MSISDN");
						MODE = Rs8dnis.getString("MODE");
						CHARGINGAMOUNT = Rs8dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs8dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs8dnis.getString("CHARGING REASON");
						DATE = Rs8dnis.getString("DATE");
						TYPE = Rs8dnis.getString("TYPE");
						USER_TYPE = Rs8dnis.getString("USER_TYPE");
						SERVICE = Rs8dnis.getString("SERVICE");
						DATE1 = Rs8dnis.getString("date(response_time)");
						TIME1 = Rs8dnis.getString("cgid");

						RETRY_TYPE=Rs8dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
//						System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String MND_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from mts_mnd.tbl_character_unsub1 nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
	            ResultSet Rs111dnis = stmt_source.executeQuery(MND_unsub);
				System.out.println("***********MTS MND UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs111dnis.next())
				{
					try
					{
						MSISDN = Rs111dnis.getString("MSISDN");
						MODE = Rs111dnis.getString("MODE");
						CHARGINGAMOUNT = Rs111dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs111dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs111dnis.getString("CHARGING REASON");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						DATE = Rs111dnis.getString("DATE");
						TYPE = Rs111dnis.getString("TYPE");
						USER_TYPE = Rs111dnis.getString("USER_TYPE");
						SERVICE = Rs111dnis.getString("SERVICE");
						DATE1 = Rs111dnis.getString("date(unsub_date)");
						TIME1 = Rs111dnis.getString("DCccg_ID");

//						System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_redfm");
				String RFM_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE','RedFMMTS' SERVICE,date(response_time),cgid from "+BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1110";
				ResultSet Rs13dnis = stmt_source.executeQuery(RFM_Success);
				System.out.println("***********MTS RED FM SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
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
						TIME1 = Rs13dnis.getString("cgid");
						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String RFM_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE','RedFMMTS' SERVICE,date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1110";
				ResultSet Rs14dnis = stmt_source.executeQuery(RFM_failure);
				System.out.println("***********MTS RED FM FAILURE TABLE DATA INSERTING AT DESTINATION***********");
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
						DATE1 = Rs14dnis.getString("date(response_time)");
						TIME1 = Rs14dnis.getString("cgid");

						RETRY_TYPE=Rs14dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;


						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String RFM_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','RedFMMTS' SERVICE,date(unsub_date),DCccg_ID from mts_redfm.tbl_jbox_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs15dnis = stmt_source.executeQuery(RFM_unsub);
				System.out.println("***********MTS RED FM UNSUB TABLE DATA INSERTING AT DESTINATION***********");
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
						TIME1 = Rs15dnis.getString("DCccg_ID");
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_redfm values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_fmj_mts");
				String Celeb_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1106";
				ResultSet Rs16dnis = stmt_source.executeQuery(Celeb_Succ);
				System.out.println("***********MTS CELEB CHAT SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
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
						TIME1 = Rs16dnis.getString("cgid");
						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_fmj_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Celeb_fail = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1106";
				ResultSet Rs17dnis = stmt_source.executeQuery(Celeb_fail);
				System.out.println("***********MTS CELEB CHAT FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs17dnis.next())
				{
					try
					{
						MSISDN = Rs17dnis.getString("MSISDN");
						MODE = Rs17dnis.getString("MODE");
						CHARGINGAMOUNT = Rs17dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs17dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs17dnis.getString("CHARGING REASON");
						DATE = Rs17dnis.getString("DATE");
						TYPE = Rs17dnis.getString("TYPE");
						USER_TYPE = Rs17dnis.getString("USER_TYPE");
						SERVICE = Rs17dnis.getString("SERVICE");
						DATE1 = Rs17dnis.getString("date(response_time)");
						TIME1 = Rs17dnis.getString("cgid");

						RETRY_TYPE=Rs17dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_fmj_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Celeb_unsub= "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from mts_starclub.tbl_jbox_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs18dnis = stmt_source.executeQuery(Celeb_unsub);
				System.out.println("***********MTS CELEB CHAT UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs18dnis.next())
				{
					try
					{
						MSISDN = Rs18dnis.getString("MSISDN");
						MODE = Rs18dnis.getString("MODE");
						CHARGINGAMOUNT = Rs18dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs18dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs18dnis.getString("CHARGING REASON");
						DATE = Rs18dnis.getString("DATE");
						TYPE = Rs18dnis.getString("TYPE");
						USER_TYPE = Rs18dnis.getString("USER_TYPE");
						SERVICE = Rs18dnis.getString("SERVICE");
						DATE1 = Rs18dnis.getString("date(unsub_date)");
						TIME1 = Rs18dnis.getString("DCccg_ID");
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_fmj_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_vs_mts");
				String VA_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1116";
	            ResultSet Rs22dnis = stmt_source.executeQuery(VA_Success);
				System.out.println("***********MTS VOICE ALERT SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
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
						TIME1 = Rs22dnis.getString("cgid");
						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_vs_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String VA_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1116";
	            ResultSet Rs23dnis = stmt_source.executeQuery(VA_failure);
				System.out.println("***********MTS VOICE ALERT FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs23dnis.next())
				{
					try
					{
						MSISDN = Rs23dnis.getString("MSISDN");
						MODE = Rs23dnis.getString("MODE");
						CHARGINGAMOUNT = Rs23dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs23dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs23dnis.getString("CHARGING REASON");
						DATE = Rs23dnis.getString("DATE");
						TYPE = Rs23dnis.getString("TYPE");
						USER_TYPE = Rs23dnis.getString("USER_TYPE");
						SERVICE = Rs23dnis.getString("SERVICE");
						DATE1 = Rs23dnis.getString("date(response_time)");
						TIME1 = Rs23dnis.getString("cgid");

						RETRY_TYPE=Rs23dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_vs_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String VA_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from mts_voicealert.tbl_voice_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs24dnis = stmt_source.executeQuery(VA_unsub);
				System.out.println("***********MTS VOICE ALERT UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs24dnis.next())
				{
					try
					{
						MSISDN = Rs24dnis.getString("MSISDN");
						MODE = Rs24dnis.getString("MODE");
						CHARGINGAMOUNT = Rs24dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs24dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs24dnis.getString("CHARGING REASON");
						DATE = Rs24dnis.getString("DATE");
						TYPE = Rs24dnis.getString("TYPE");
						USER_TYPE = Rs24dnis.getString("USER_TYPE");
						SERVICE = Rs24dnis.getString("SERVICE");
						DATE1 = Rs24dnis.getString("date(unsub_date)");
						TIME1 = Rs24dnis.getString("DCccg_ID");
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_vs_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_devo_mts");
				String Devo_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1111";
				ResultSet Rs9dnis = stmt_source.executeQuery(Devo_Succ);
				System.out.println("***********MTS DEVOTIONAL SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
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
						TIME1 = Rs9dnis.getString("cgid");
						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_devo_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Devo_fail = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1111";
				ResultSet Rs10dnis = stmt_source.executeQuery(Devo_fail);
				System.out.println("***********MTS DEVOTIONAL FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs10dnis.next())
				{
					try
					{
						MSISDN = Rs10dnis.getString("MSISDN");
						MODE = Rs10dnis.getString("MODE");
						CHARGINGAMOUNT = Rs10dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs10dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs10dnis.getString("CHARGING REASON");
						DATE = Rs10dnis.getString("DATE");
						TYPE = Rs10dnis.getString("TYPE");
						USER_TYPE = Rs10dnis.getString("USER_TYPE");
						SERVICE = Rs10dnis.getString("SERVICE");
						DATE1 = Rs10dnis.getString("date(response_time)");
						TIME1 = Rs10dnis.getString("cgid");
						RETRY_TYPE=Rs10dnis.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

						if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_devo_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Devo_unsub= "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from dm_radio.tbl_digi_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rs11dnis = stmt_source.executeQuery(Devo_unsub);
				System.out.println("***********MTS DEVOTIONAL UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs11dnis.next())
				{
					try
					{
						MSISDN = Rs11dnis.getString("MSISDN");
						MODE = Rs11dnis.getString("MODE");
						CHARGINGAMOUNT = Rs11dnis.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rs11dnis.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rs11dnis.getString("CHARGING REASON");
						DATE = Rs11dnis.getString("DATE");
						TYPE = Rs11dnis.getString("TYPE");
						USER_TYPE = Rs11dnis.getString("USER_TYPE");
						SERVICE = Rs11dnis.getString("SERVICE");
						DATE1 = Rs11dnis.getString("date(unsub_date)");
						TIME1 = Rs11dnis.getString("DCccg_ID");
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_devo_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_ac_mts");
				String Cinema_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1124";
				ResultSet Rsdnis15 = stmt_source.executeQuery(Cinema_Success);
				System.out.println("***********MTS AUDIO CINEMA SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis15.next())
				{
					try
					{
	            	    MSISDN = Rsdnis15.getString("MSISDN");
	                    MODE = Rsdnis15.getString("MODE");
	                    CHARGINGAMOUNT = Rsdnis15.getString("CHARGING AMOUNT");
	                    CIRCLE = Rsdnis15.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rsdnis15.getString("CHARGING REASON");
	                    DATE = Rsdnis15.getString("DATE");
	                    TYPE = Rsdnis15.getString("TYPE");
	                    USER_TYPE = Rsdnis15.getString("USER_TYPE");
	                    SERVICE = Rsdnis15.getString("SERVICE");
	                    DATE1 = Rsdnis15.getString("date(response_time)");
	                    TIME1 = Rsdnis15.getString("cgid");
	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";

	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ac_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				String Cinema_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1124";
				ResultSet Rsdnis16 = stmt_source.executeQuery(Cinema_failure);
				System.out.println("***********MTS AUDIO CINEMA FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis16.next())
				{
					try
					{
	            	    MSISDN = Rsdnis16.getString("MSISDN");
	                    MODE = Rsdnis16.getString("MODE");
	                    CHARGINGAMOUNT = Rsdnis16.getString("CHARGING AMOUNT");
	                    if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
	                    CIRCLE = Rsdnis16.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rsdnis16.getString("CHARGING REASON");
	                    DATE = Rsdnis16.getString("DATE");
	                    TYPE = Rsdnis16.getString("TYPE");
	                    USER_TYPE = Rsdnis16.getString("USER_TYPE");
	                    SERVICE = Rsdnis16.getString("SERVICE");
	                    DATE1 = Rsdnis16.getString("date(response_time)");
	                    TIME1 = Rsdnis16.getString("cgid");

						RETRY_TYPE=Rsdnis16.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ac_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				String Cinema_unsub  = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from mts_radio.tbl_AudioCinema_unsub nolock where date(unsub_date)=date(subdate(now(),"+day+"))";
				ResultSet Rsdnis17 = stmt_source.executeQuery(Cinema_unsub);
				System.out.println("***********MTS AUDIO CINEMA UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis17.next())
				{
					try
					{
	            	    MSISDN = Rsdnis17.getString("MSISDN");
	                    MODE = Rsdnis17.getString("MODE");
	                    CHARGINGAMOUNT = Rsdnis17.getString("CHARGING AMOUNT");
	                    if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
	                    CIRCLE = Rsdnis17.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rsdnis17.getString("CHARGING REASON");
	                    DATE = Rsdnis17.getString("DATE");
	                    TYPE = Rsdnis17.getString("TYPE");
	                    USER_TYPE = Rsdnis17.getString("USER_TYPE");
	                    SERVICE = Rsdnis17.getString("SERVICE");
	                    DATE1 = Rsdnis17.getString("date(unsub_date)");
	                    TIME1 = Rsdnis17.getString("DCccg_ID");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ac_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_contest_mts");
				String Contest_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1123";
	            ResultSet Rsdnis18 = stmt_source.executeQuery(Contest_Success);
				System.out.println("***********MTS CONTEST SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis18.next())
				{
					try
					{

						MSISDN = Rsdnis18.getString("MSISDN");
						MODE = Rsdnis18.getString("MODE");
						CHARGINGAMOUNT = Rsdnis18.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis18.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis18.getString("CHARGING REASON");
						DATE = Rsdnis18.getString("DATE");
						TYPE = Rsdnis18.getString("TYPE");
						USER_TYPE = Rsdnis18.getString("USER_TYPE");
						SERVICE = Rsdnis18.getString("SERVICE");
						DATE1 = Rsdnis18.getString("date(response_time)");
						TIME1 = Rsdnis18.getString("cgid");
	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_contest_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Contest_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1123";
	            ResultSet Rsdnis19 = stmt_source.executeQuery(Contest_failure);
				System.out.println("***********MTS CONTEST FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis19.next())
				{
					try
					{
						MSISDN = Rsdnis19.getString("MSISDN");
						MODE = Rsdnis19.getString("MODE");
						CHARGINGAMOUNT = Rsdnis19.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis19.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis19.getString("CHARGING REASON");
						DATE = Rsdnis19.getString("DATE");
						TYPE = Rsdnis19.getString("TYPE");
						USER_TYPE = Rsdnis19.getString("USER_TYPE");
						SERVICE = Rsdnis19.getString("SERVICE");
						DATE1 = Rsdnis19.getString("date(response_time)");
						TIME1 = Rsdnis19.getString("cgid");

						RETRY_TYPE=Rsdnis19.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;
	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_contest_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Contest_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from Mts_summer_contest.tbl_contest_unsub nolock where date(unsub_date) =date(subdate(now(),"+day+"))";
				ResultSet Rsdnis20 = stmt_source.executeQuery(Contest_unsub);
				System.out.println("***********MTS CONTEST UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis20.next())
				{
					try
					{
						MSISDN = Rsdnis20.getString("MSISDN");
						MODE = Rsdnis20.getString("MODE");
						CHARGINGAMOUNT = Rsdnis20.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis20.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis20.getString("CHARGING REASON");
						DATE = Rsdnis20.getString("DATE");
						TYPE = Rsdnis20.getString("TYPE");
						USER_TYPE = Rsdnis20.getString("USER_TYPE");
						SERVICE = Rsdnis20.getString("SERVICE");
						DATE1 = Rsdnis20.getString("date(unsub_date)");
						TIME1 = Rsdnis20.getString("DCccg_ID");

						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_contest_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_comedy");
				String Jokes_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE','MTSJokes' SERVICE,date(response_time),cgid from "+BillingTable+" nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1125";
	            ResultSet Rsdnis21 = stmt_source.executeQuery(Jokes_Success);
				System.out.println("***********MTS JOKES SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis21.next())
				{
					try
					{

						MSISDN = Rsdnis21.getString("MSISDN");
						MODE = Rsdnis21.getString("MODE");
						CHARGINGAMOUNT = Rsdnis21.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis21.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis21.getString("CHARGING REASON");
						DATE = Rsdnis21.getString("DATE");
						TYPE = Rsdnis21.getString("TYPE");
						USER_TYPE = Rsdnis21.getString("USER_TYPE");
						SERVICE = Rsdnis21.getString("SERVICE");
						DATE1 = Rsdnis21.getString("date(response_time)");
						TIME1 = Rsdnis21.getString("cgid");
	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Jokes_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE','MTSJokes' SERVICE,date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1125";
	            ResultSet Rsdnis22 = stmt_source.executeQuery(Jokes_failure);
				System.out.println("***********MTS JOKES FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis22.next())
				{
					try
					{
						MSISDN = Rsdnis22.getString("MSISDN");
						MODE = Rsdnis22.getString("MODE");
						CHARGINGAMOUNT = Rsdnis22.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis22.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis22.getString("CHARGING REASON");
						DATE = Rsdnis22.getString("DATE");
						TYPE = Rsdnis22.getString("TYPE");
						USER_TYPE = Rsdnis22.getString("USER_TYPE");
						SERVICE = Rsdnis22.getString("SERVICE");
						DATE1 = Rsdnis22.getString("date(response_time)");
						TIME1 = Rsdnis22.getString("cgid");
						RETRY_TYPE=Rsdnis22.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Jokes_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','MTSJokes' SERVICE,date(unsub_date),DCccg_ID from mts_JOKEPORTAL.tbl_jokeportal_unsub nolock where date(unsub_date) =date(subdate(now(),"+day+"))";
				ResultSet Rsdnis23 = stmt_source.executeQuery(Jokes_unsub);
				System.out.println("***********MTS JOKES UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis23.next())
				{
					try
					{
						MSISDN = Rsdnis23.getString("MSISDN");
						MODE = Rsdnis23.getString("MODE");
						CHARGINGAMOUNT = Rsdnis23.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis23.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis23.getString("CHARGING REASON");
						DATE = Rsdnis23.getString("DATE");
						TYPE = Rsdnis23.getString("TYPE");
						USER_TYPE = Rsdnis23.getString("USER_TYPE");
						SERVICE = Rsdnis23.getString("SERVICE");
						DATE1 = Rsdnis23.getString("date(unsub_date)");
						TIME1 = Rsdnis23.getString("DCccg_ID");

						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_reg_mts");
				String Reg_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1126";
	            ResultSet Rsdnis24 = stmt_source.executeQuery(Reg_Success);
				System.out.println("***********MTS REGIONAL SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis24.next())
				{
					try
					{

						MSISDN = Rsdnis24.getString("MSISDN");
						MODE = Rsdnis24.getString("MODE");
						CHARGINGAMOUNT = Rsdnis24.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis24.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis24.getString("CHARGING REASON");
						DATE = Rsdnis24.getString("DATE");
						TYPE = Rsdnis24.getString("TYPE");
						USER_TYPE = Rsdnis24.getString("USER_TYPE");
						SERVICE = Rsdnis24.getString("SERVICE");
						DATE1 = Rsdnis24.getString("date(response_time)");
						TIME1 = Rsdnis24.getString("cgid");
	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_reg_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Reg_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1126";
	            ResultSet Rsdnis25 = stmt_source.executeQuery(Reg_failure);
				System.out.println("***********MTS REGIONAL FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis25.next())
				{
					try
					{
						MSISDN = Rsdnis25.getString("MSISDN");
						MODE = Rsdnis25.getString("MODE");
						CHARGINGAMOUNT = Rsdnis25.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis25.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis25.getString("CHARGING REASON");
						DATE = Rsdnis25.getString("DATE");
						TYPE = Rsdnis25.getString("TYPE");
						USER_TYPE = Rsdnis25.getString("USER_TYPE");
						SERVICE = Rsdnis25.getString("SERVICE");
						DATE1 = Rsdnis25.getString("date(response_time)");
						TIME1 = Rsdnis25.getString("cgid");
						RETRY_TYPE=Rsdnis25.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_reg_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Reg_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from mts_Regional.tbl_regional_unsub nolock where date(unsub_date) =date(subdate(now(),"+day+"))";
				ResultSet Rsdnis26 = stmt_source.executeQuery(Reg_unsub);
				System.out.println("***********MTS REGIONAL UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis26.next())
				{
					try
					{
						MSISDN = Rsdnis26.getString("MSISDN");
						MODE = Rsdnis26.getString("MODE");
						CHARGINGAMOUNT = Rsdnis26.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis26.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis26.getString("CHARGING REASON");
						DATE = Rsdnis26.getString("DATE");
						TYPE = Rsdnis26.getString("TYPE");
						USER_TYPE = Rsdnis26.getString("USER_TYPE");
						SERVICE = Rsdnis26.getString("SERVICE");
						DATE1 = Rsdnis26.getString("date(unsub_date)");
						TIME1 = Rsdnis26.getString("DCccg_ID");

						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_reg_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
				stmt_destination.executeUpdate("truncate table tbl_billing_cri_mts");
				String Cri_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',RPID 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid from "+BillingTable+" nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1108";
	            ResultSet Rsdnis27 = stmt_source.executeQuery(Cri_Success);
				System.out.println("***********MTS CRICKET SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis27.next())
				{
					try
					{

						MSISDN = Rsdnis27.getString("MSISDN");
						MODE = Rsdnis27.getString("MODE");
						CHARGINGAMOUNT = Rsdnis27.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis27.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis27.getString("CHARGING REASON");
						DATE = Rsdnis27.getString("DATE");
						TYPE = Rsdnis27.getString("TYPE");
						USER_TYPE = Rsdnis27.getString("USER_TYPE");
						SERVICE = Rsdnis27.getString("SERVICE");
						DATE1 = Rsdnis27.getString("date(response_time)");
						TIME1 = Rsdnis27.getString("cgid");
	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						stmt_destination.executeUpdate("insert into tbl_billing_cri_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Cri_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,res_text as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'SUB_RETRY' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'Event' then 'Event' when 'resub' then 'Renewal' when 'Grace' then 'Renewal' when 'Resub_Fail' then 'Renewal' when 'Resub_Retry_Fail' then 'Renewal' when 'EVENT_RETRY' then 'Event' when 'park' then 'Renewal' when 'ALRSUB' then 'Activation' when 'sub_fail' then 'Activation' else event_type end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),cgid,event_type from master_db.tbl_billing_failure nolock where date(response_time) =date(subdate(now(),"+day+")) and service_id=1108";
	            ResultSet Rsdnis28 = stmt_source.executeQuery(Cri_failure);
				System.out.println("***********MTS CRICKET FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis28.next())
				{
					try
					{
						MSISDN = Rsdnis28.getString("MSISDN");
						MODE = Rsdnis28.getString("MODE");
						CHARGINGAMOUNT = Rsdnis28.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis28.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis28.getString("CHARGING REASON");
						DATE = Rsdnis28.getString("DATE");
						TYPE = Rsdnis28.getString("TYPE");
						USER_TYPE = Rsdnis28.getString("USER_TYPE");
						SERVICE = Rsdnis28.getString("SERVICE");
						DATE1 = Rsdnis28.getString("date(response_time)");
						TIME1 = Rsdnis28.getString("cgid");
						RETRY_TYPE=Rsdnis28.getString("event_type");
						if(RETRY_TYPE.toUpperCase().contains("RETRY")  || RETRY_TYPE.toUpperCase().contains("GRACE"))
							CHARGINGREASON="R_"+CHARGINGREASON;

	                    if(TYPE.equalsIgnoreCase("Activation"))
	                    	{}
	                    else
	                    	TIME1 = "X";
						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_cri_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Cri_unsub   = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),DCccg_ID from MTS_cricket.tbl_cricket_unsub nolock where date(unsub_date) =date(subdate(now(),"+day+"))";
				ResultSet Rsdnis29 = stmt_source.executeQuery(Cri_unsub);
				System.out.println("***********MTS CRICKET UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rsdnis29.next())
				{
					try
					{
						MSISDN = Rsdnis29.getString("MSISDN");
						MODE = Rsdnis29.getString("MODE");
						CHARGINGAMOUNT = Rsdnis29.getString("CHARGING AMOUNT");
						if(CHARGINGAMOUNT.equalsIgnoreCase("NOK"))	CHARGINGAMOUNT = "0";
						CIRCLE = Rsdnis29.getString("circle");
						CIRCLE = getCIRCLE(CIRCLE);
						CHARGINGREASON = Rsdnis29.getString("CHARGING REASON");
						DATE = Rsdnis29.getString("DATE");
						TYPE = Rsdnis29.getString("TYPE");
						USER_TYPE = Rsdnis29.getString("USER_TYPE");
						SERVICE = Rsdnis29.getString("SERVICE");
						DATE1 = Rsdnis29.getString("date(unsub_date)");
						TIME1 = Rsdnis29.getString("DCccg_ID");

						//System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_cri_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            System.out.println("***********MTS ALL SERVICES DATA INSERTING AT DESTINATION***********");
				stmt_destination.executeUpdate("update tbl_billing_vs_mts set mode='IVR' where mode REGEXP '^-?[0-9]+$' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_vs_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_vs_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL  and  DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_mtv set mode='IVR' where mode REGEXP '^-?[0-9]+$' and service='MTVMTS' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_mtv set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and service='MTVMTS' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_mtv set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and service='MTVMTS' and  DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_fmj_mts set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
				stmt_destination.executeUpdate("update tbl_billing_fmj_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_fmj_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and  DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_redfm set mode='IVR' where mode REGEXP '^-?[0-9]+$' and service='RedFMMTS' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_redfm set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and service='RedFMMTS' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_redfm set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and service='RedFMMTS' and  DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_devo_mts set mode='IVR' where mode REGEXP '^-?[0-9]+$' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_devo_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_devo_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and  DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_54646_mts set mode='IVR' where mode REGEXP '^-?[0-9]+$' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_54646_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and  DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_54646_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and  DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_ac_mts set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_ac_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_ac_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_mnd_mts set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_mnd_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_mnd_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_contest_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_contest_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_mu_mts set mode='IVR' where mode REGEXP '^-?[0-9]+$' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_mu_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_mu_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_comedy set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_comedy set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_reg_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_reg_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and DATE1 = date(subdate(now(),"+day+"))");

				stmt_destination.executeUpdate("update tbl_billing_cri_mts set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and DATE1 = date(subdate(now(),"+day+"))");
				stmt_destination.executeUpdate("update tbl_billing_cri_mts set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL and DATE1 = date(subdate(now(),"+day+"))");

				System.out.println("***********MTS ALL SERVICES DATA UPDATIONS DONE***********");

				Thread.sleep(1000);
				Fun_FileCreator(day);
				System.out.println("***********MTS ALL SERVICES BILLING FILES CREATION DONE***********");

				Thread.sleep(2000);
				RunScript("/home/java/BILLINGDATAPROCESS/createTarFiles.sh");
				System.out.println("***********BILLING FILES COPMPRES IN TAR DONE***********");

				stmt_source.close();
				stmt_destination.close();
				con_source.close();
				con_destination.close();
				System.out.println("***********MTS ALL SERVICES DATA DB CONNECTIONS CLOSING CONNECTIONS***********");



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
		MTSDataProcess c = new MTSDataProcess();
		c.start();

	}

}
