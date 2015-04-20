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
import java.util.Date;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;


	public class AirtelBillingDataProcess extends Thread
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

			sIP="10.2.73.160";
			sDSN="master_db";
			sUSR="manish";
			sPWD="Manish";

			dIP="10.2.73.160";
			dDSN="misdata";
			dUSR="manish";
			dPWD="Manish";

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

	public AirtelBillingDataProcess()
	{
		try
		{
			readDBCONFIG();
			System.out.println("Initiallizing DB");
			Class.forName("com.mysql.jdbc.Driver");
			con_source = DriverManager.getConnection("jdbc:mysql://"+sIP+"/"+sDSN, sUSR, sPWD);
			con_destination=DriverManager.getConnection("jdbc:mysql://"+dIP+"/"+dDSN, dUSR, dPWD);
			System.out.println("Database Connection established!");
			stmt_source = con_source.createStatement();
			stmt_destination = con_destination.createStatement();
			System.out.println("DB UP");
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
					cstmtfm1 = con_destination.prepareCall("{call misdata.Airtel_Billing_Data_All(?)}");
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
			//	day=1;
				String FILEDATE = "";
				String BillingTable = "master_db.tbl_billing_success";
				String query_date = "select date_format(adddate(now(),-"+day+"),'%Y%m%d') as 'FILEDATE'";

				if (day > 2)
					BillingTable = "master_db.tbl_billing_success_backup";
				else
					BillingTable = "master_db.tbl_billing_success";

				System.out.println("****Billing table is :****"+BillingTable);

				//String Paus_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and sc like ('5464634P%')";
				//String Paus_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and sc like ('5464634P%')";

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
						System.out.println("Current date is "+FILEDATE);
				}
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
				/*String str1 = "select count(1) as CNT from tbl_billing_vh1 where date(Date) = date(subdate(now(),"+day+")) and service='VH1Airtel'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_vh1 where date(Date) = date(subdate(now(),"+day+")) and service='VH1Airtel'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_vh1");
				String Vh1_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','VH1Airtel' SERVICE,date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1507";
				System.out.println("***********AIRTEL VH1 SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rsdnis = stmt_source.executeQuery(Vh1_Success);
				while(Rsdnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_vh1 values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            String Vh1_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','VH1Airtel' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1507";
				System.out.println("***********AIRTEL VH1 FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs2dnis = stmt_source.executeQuery(Vh1_failure);
				while(Rs2dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_vh1 values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String Vh1_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','VH1Airtel' SERVICE,date(unsub_date),time(unsub_date) from airtel_vh1.tbl_jbox_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs3dnis = stmt_source.executeQuery(Vh1_unsub);
				while(Rs3dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_vh1 values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str2 = "select count(1) as CNT from tbl_billing_gl_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				if (localResultSet2.next())
				{
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_gl_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_gl_airtel");
				String GL_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1511 and plan_id in(29,46,96)";
				System.out.println("***********AIRTEL GUD LIFE SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs4dnis = stmt_source.executeQuery(GL_Success);
				while(Rs4dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_gl_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String GL_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1511 and plan_id in(29,46,96)";
				ResultSet Rs5dnis = stmt_source.executeQuery(GL_failure);
				while(Rs5dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_gl_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String GL_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_rasoi.tbl_rasoi_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs6dnis = stmt_source.executeQuery(GL_unsub);
				while(Rs6dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_gl_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str3 = "select count(1) as CNT from tbl_billing_54646_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet3 = stmt_destination.executeQuery(str3);
				if (localResultSet3.next())
				{
					i = localResultSet3.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet3.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_54646_airtel");
				String H_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1502 and plan_id!='50' and sc not like ('5464634P%')";
				System.out.println("***********AIRTEL 54646 SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs7dnis = stmt_source.executeQuery(H_Success);
				while(Rs7dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String H_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1502 and plan_id!='50' and sc not like ('5464634P%')";
				ResultSet Rs8dnis = stmt_source.executeQuery(H_failure);
				while(Rs8dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String H_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_hungama.tbl_jbox_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day)) and plan_id!='50'";
				ResultSet Rs9dnis = stmt_source.executeQuery(H_unsub);
				while(Rs9dnis.next())
				{
					try{

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
	                    DATE1 = Rs9dnis.getString("date(unsub_date)");
	                    TIME1 = Rs9dnis.getString("time(unsub_date)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_54646_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str4 = "select count(1) as CNT from tbl_billing_ria_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet4 = stmt_destination.executeQuery(str4);
				if (localResultSet4.next())
				{
					i = localResultSet4.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet4.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_ria_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_ria_airtel");
				String Riya_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1511 and plan_id in('30','48')";
				System.out.println("***********AIRTEL RIYA SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs10dnis = stmt_source.executeQuery(Riya_Success);
				while(Rs10dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ria_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String Riya_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1511 and plan_id in('30','48')";
				ResultSet Rs11dnis = stmt_source.executeQuery(Riya_failure);
				while(Rs11dnis.next())
				{
					try{

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
	                    DATE1 = Rs11dnis.getString("date(response_time)");
	                    TIME1 = Rs11dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ria_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String Riya_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_manchala.tbl_riya_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs12dnis = stmt_source.executeQuery(Riya_unsub);
				while(Rs12dnis.next())
				{
					try{

	            	    MSISDN = Rs12dnis.getString("MSISDN");
	                    MODE = Rs12dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs12dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs12dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs12dnis.getString("CHARGING REASON");
	                    DATE = Rs12dnis.getString("DATE");
	                    TYPE = Rs12dnis.getString("TYPE");
	                    USER_TYPE = Rs12dnis.getString("USER_TYPE");
	                    SERVICE = Rs12dnis.getString("SERVICE");
	                    DATE1 = Rs12dnis.getString("date(unsub_date)");
	                    TIME1 = Rs12dnis.getString("time(unsub_date)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_ria_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str5 = "select count(1) as CNT from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVAirtel'";
				ResultSet localResultSet5 = stmt_destination.executeQuery(str5);
				if (localResultSet5.next())
				{
					i = localResultSet5.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet5.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVAirtel'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_mtv");
				String MTV_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','MTVAirtel' SERVICE,date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1503";
				System.out.println("***********AIRTEL MTV SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs13dnis = stmt_source.executeQuery(MTV_Success);
				while(Rs13dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mtv values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String MTV_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','MTVAirtel' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1503";
				ResultSet Rs14dnis = stmt_source.executeQuery(MTV_failure);
				while(Rs14dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mtv values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String MTV_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','MTVAirtel' SERVICE,date(unsub_date),time(unsub_date) from airtel_hungama.tbl_mtv_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs15dnis = stmt_source.executeQuery(MTV_unsub);
				while(Rs15dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mtv values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str6 = "select count(1) as CNT from tbl_billing_pd_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet6 = stmt_destination.executeQuery(str6);
				if (localResultSet6.next())
				{
					i = localResultSet6.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet6.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_pd_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_pd_airtel");
				String PDU_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1514";
				System.out.println("***********AIRTEL PDU SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs16dnis = stmt_source.executeQuery(PDU_Success);
				while(Rs16dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_pd_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String PDU_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1514";
				ResultSet Rs17dnis = stmt_source.executeQuery(PDU_failure);
				while(Rs17dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_pd_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String PDU_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_EDU.tbl_jbox_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs18dnis = stmt_source.executeQuery(PDU_unsub);
				while(Rs18dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_pd_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str7 = "select count(1) as CNT from tbl_billing_mnd_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet7 = stmt_destination.executeQuery(str7);
				if (localResultSet7.next())
				{
					i = localResultSet7.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet7.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mnd_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_mnd_airtel");
				String MND_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1513";
				System.out.println("***********AIRTEL MND SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs19dnis = stmt_source.executeQuery(MND_Success);
				while(Rs19dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String MND_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1513";
				ResultSet Rs20dnis = stmt_source.executeQuery(MND_failure);
				while(Rs20dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String MND_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_mnd.tbl_character_unsub1 nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs21dnis = stmt_source.executeQuery(MND_unsub);
				while(Rs21dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str8 = "select count(1) as CNT from tbl_billing_comedy where date(Date) = date(subdate(now()," + day + ")) and service='AirtelComedy'";
				ResultSet localResultSet8 = stmt_destination.executeQuery(str8);
				if (localResultSet8.next())
				{
					i = localResultSet8.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet8.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_comedy where date(Date) = date(subdate(now()," + day + ")) and service='AirtelComedy'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_comedy");
				String Com_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelComedy' SERVICE,date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1518 and plan_id=50";
				System.out.println("***********AIRTEL COMEDY SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs22dnis = stmt_source.executeQuery(Com_Success);
				while(Rs22dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String Com_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelComedy' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1518 and plan_id=50";
				ResultSet Rs23dnis = stmt_source.executeQuery(Com_failure);
				while(Rs23dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String Com_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','AirtelComedy' SERVICE,date(unsub_date),time(unsub_date) from airtel_hungama.tbl_comedyportal_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day)) and plan_id=50";
				ResultSet Rs24dnis = stmt_source.executeQuery(Com_unsub);
				while(Rs24dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str9 = "select count(1) as CNT from tbl_billing_devo_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet9 = stmt_destination.executeQuery(str9);
				if (localResultSet9.next())
				{
					i = localResultSet9.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet9.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_devo_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_devo_airtel");
				String DEV_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1515";
				System.out.println("***********AIRTEL DEVO SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs25dnis = stmt_source.executeQuery(DEV_Success);
				while(Rs25dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_devo_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String DEV_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1515";
				ResultSet Rs26dnis = stmt_source.executeQuery(DEV_failure);
				while(Rs26dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_devo_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String DEV_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_devo.tbl_devo_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs27dnis = stmt_source.executeQuery(DEV_unsub);
				while(Rs27dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_devo_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str10 = "select count(1) as CNT from tbl_billing_se_airtel where date(Date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet10 = stmt_destination.executeQuery(str10);
				if(localResultSet10.next())
				{
					i = localResultSet10.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet10.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_se_airtel where date(Date) = date(subdate(now(),"+day+"))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_se_airtel");
				String Eng_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1517";
				System.out.println("***********AIRTEL ENG SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs28dnis = stmt_source.executeQuery(Eng_Success);
				while(Rs28dnis.next())
				{
					try{

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
	                    TIME1 = Rs28dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_se_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String Eng_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1517";
				ResultSet Rs29dnis = stmt_source.executeQuery(Eng_failure);
				while(Rs29dnis.next())
				{
					try{

	            	    MSISDN = Rs29dnis.getString("MSISDN");
	                    MODE = Rs29dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs29dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs29dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs29dnis.getString("CHARGING REASON");
	                    DATE = Rs29dnis.getString("DATE");
	                    TYPE = Rs29dnis.getString("TYPE");
	                    USER_TYPE = Rs29dnis.getString("USER_TYPE");
	                    SERVICE = Rs29dnis.getString("SERVICE");
	                    DATE1 = Rs29dnis.getString("date(response_time)");
	                    TIME1 = Rs29dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_se_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String Eng_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_SPKENG.tbl_spkeng_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs30dnis = stmt_source.executeQuery(Eng_unsub);
				while(Rs30dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_se_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str11 = "select count(1) as CNT from tbl_billing_mu_airtel where date(Date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet11 = stmt_destination.executeQuery(str11);
				if(localResultSet11.next())
				{
					i = localResultSet11.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet11.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mu_airtel where date(Date) = date(subdate(now(),"+day+"))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_mu_airtel");
				String EU_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1501";
				System.out.println("***********AIRTEL EU SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs31dnis = stmt_source.executeQuery(EU_Success);
				while(Rs31dnis.next())
				{
					try{

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
	                    TIME1 = Rs31dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mu_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String EU_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1501";
				ResultSet Rs32dnis = stmt_source.executeQuery(EU_failure);
				while(Rs32dnis.next())
				{
					try{

	            	    MSISDN = Rs32dnis.getString("MSISDN");
	                    MODE = Rs32dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs32dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs32dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs32dnis.getString("CHARGING REASON");
	                    DATE = Rs32dnis.getString("DATE");
	                    TYPE = Rs32dnis.getString("TYPE");
	                    USER_TYPE = Rs32dnis.getString("USER_TYPE");
	                    SERVICE = Rs32dnis.getString("SERVICE");
	                    DATE1 = Rs32dnis.getString("date(response_time)");
	                    TIME1 = Rs32dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mu_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String EU_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_radio.tbl_radio_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs33dnis = stmt_source.executeQuery(EU_unsub);
				while(Rs33dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mu_airtel values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str12 = "select count(1) as CNT from tbl_billing_pk where date(Date) = date(subdate(now(),"+day+")) and service='AirtelPK'";
				ResultSet localResultSet12 = stmt_destination.executeQuery(str12);
				if(localResultSet12.next())
				{
					i = localResultSet12.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet12.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_pk where date(Date) = date(subdate(now(),"+day+")) and service='AirtelPK'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				stmt_destination.executeUpdate("truncate table tbl_billing_pk");
				String PK_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelPK' SERVICE,date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1520";
				System.out.println("***********AIRTEL PK SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs34dnis = stmt_source.executeQuery(PK_Success);
				while(Rs34dnis.next())
				{
					try{

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
	                    TIME1 = Rs34dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_pk values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String PK_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelPK' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1520";
				ResultSet Rs35dnis = stmt_source.executeQuery(PK_failure);
				while(Rs35dnis.next())
				{
					try{

	            	    MSISDN = Rs35dnis.getString("MSISDN");
	                    MODE = Rs35dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs35dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs35dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs35dnis.getString("CHARGING REASON");
	                    DATE = Rs35dnis.getString("DATE");
	                    TYPE = Rs35dnis.getString("TYPE");
	                    USER_TYPE = Rs35dnis.getString("USER_TYPE");
	                    SERVICE = Rs35dnis.getString("SERVICE");
	                    DATE1 = Rs35dnis.getString("date(response_time)");
	                    TIME1 = Rs35dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_pk values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String PK_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','AirtelPK' SERVICE,date(unsub_date),time(unsub_date) from airtel_hungama.tbl_pk_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs36dnis = stmt_source.executeQuery(PK_unsub);
				while(Rs36dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_pk values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str13 = "select count(1) as CNT from tbl_billing_comedy where date(Date) = date(subdate(now(),"+day+")) and service='AirtelRegKK'";
				ResultSet localResultSet13 = stmt_destination.executeQuery(str13);
				if(localResultSet13.next())
				{
					i = localResultSet13.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet13.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_comedy where date(Date) = date(subdate(now(),"+day+")) and service='AirtelRegKK'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				String MAN_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelRegKK' SERVICE,date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1522 and plan_id=63";
				System.out.println("***********AIRTEL MANA SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs37dnis = stmt_source.executeQuery(MAN_Success);
				while(Rs37dnis.next())
				{
					try{

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
	                    TIME1 = Rs37dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String MAN_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelRegKK' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1522 and plan_id=63";
				ResultSet Rs38dnis = stmt_source.executeQuery(MAN_failure);
				while(Rs38dnis.next())
				{
					try{

	            	    MSISDN = Rs38dnis.getString("MSISDN");
	                    MODE = Rs38dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs38dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs38dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs38dnis.getString("CHARGING REASON");
	                    DATE = Rs38dnis.getString("DATE");
	                    TYPE = Rs38dnis.getString("TYPE");
	                    USER_TYPE = Rs38dnis.getString("USER_TYPE");
	                    SERVICE = Rs38dnis.getString("SERVICE");
	                    DATE1 = Rs38dnis.getString("date(response_time)");
	                    TIME1 = Rs38dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String MAN_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','AirtelRegKK' SERVICE,date(unsub_date),time(unsub_date) from airtel_hungama.tbl_arm_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day)) and plan_id='63'";
				ResultSet Rs39dnis = stmt_source.executeQuery(MAN_unsub);
				while(Rs39dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str14 = "select count(1) as CNT from tbl_billing_comedy where date(Date) = date(subdate(now(),"+day+")) and service='AirtelRegTN'";
				ResultSet localResultSet14 = stmt_destination.executeQuery(str14);
				if(localResultSet14.next())
				{
					i = localResultSet14.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet14.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_comedy where date(Date) = date(subdate(now(),"+day+")) and service='AirtelRegTN'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				String CRA_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelRegTN' SERVICE,date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1522 and plan_id=64";
				System.out.println("***********AIRTEL CRA SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs40dnis = stmt_source.executeQuery(CRA_Success);
				while(Rs40dnis.next())
				{
					try{

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
	                    TIME1 = Rs40dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String CRA_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelRegTN' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1522 and plan_id=64";
				ResultSet Rs41dnis = stmt_source.executeQuery(CRA_failure);
				while(Rs41dnis.next())
				{
					try{

	            	    MSISDN = Rs41dnis.getString("MSISDN");
	                    MODE = Rs41dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs41dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs41dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs41dnis.getString("CHARGING REASON");
	                    DATE = Rs41dnis.getString("DATE");
	                    TYPE = Rs41dnis.getString("TYPE");
	                    USER_TYPE = Rs41dnis.getString("USER_TYPE");
	                    SERVICE = Rs41dnis.getString("SERVICE");
	                    DATE1 = Rs41dnis.getString("date(response_time)");
	                    TIME1 = Rs41dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String CRA_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','AirtelRegTN' SERVICE,date(unsub_date),time(unsub_date) from airtel_hungama.tbl_arm_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day)) and plan_id=64";
				ResultSet Rs42dnis = stmt_source.executeQuery(CRA_unsub);
				while(Rs42dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str15 = "select count(1) as CNT from tbl_billing_comedy where date(Date) = date(subdate(now(),"+day+")) and service='AirtelRegKR'";
				ResultSet localResultSet15 = stmt_destination.executeQuery(str15);
				if(localResultSet15.next())
				{
					i = localResultSet15.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet15.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_comedy where date(Date) = date(subdate(now(),"+day+")) and service='AirtelRegKR'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}*/
				String TINTU_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelRegKR' SERVICE,date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1523";
				System.out.println("***********AIRTEL TINTU SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs43dnis = stmt_source.executeQuery(TINTU_Success);
				while(Rs43dnis.next())
				{
					try{

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
	                    TIME1 = Rs43dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String TINTU_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE','AirtelRegKR' SERVICE,date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1523";
				ResultSet Rs44dnis = stmt_source.executeQuery(TINTU_failure);
				while(Rs44dnis.next())
				{
					try{

	            	    MSISDN = Rs44dnis.getString("MSISDN");
	                    MODE = Rs44dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs44dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs44dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs44dnis.getString("CHARGING REASON");
	                    DATE = Rs44dnis.getString("DATE");
	                    TYPE = Rs44dnis.getString("TYPE");
	                    USER_TYPE = Rs44dnis.getString("USER_TYPE");
	                    SERVICE = Rs44dnis.getString("SERVICE");
	                    DATE1 = Rs44dnis.getString("date(response_time)");
	                    TIME1 = Rs44dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String TINTU_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE','AirtelRegKR' SERVICE,date(unsub_date),time(unsub_date) from airtel_TINTUMON.tbl_TINTUMON_unsub nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day))";
				ResultSet Rs45dnis = stmt_source.executeQuery(TINTU_unsub);
				while(Rs45dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_comedy values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            /*String str16 = "select count(1) as CNT from tbl_billing_mnd_airtelkk where date(Date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet16 = stmt_destination.executeQuery(str16);
				if(localResultSet16.next())
				{
					i = localResultSet16.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet16.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mnd_airtelkk where date(Date) = date(subdate(now(),"+day+"))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				String MNDKK_Success = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from "+BillingTable+" nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1513 and plan_id=81";
				System.out.println("***********AIRTEL MNDKK SUCCESS TABLE DATA INSERTING AT DESTINATION***********");
				ResultSet Rs46dnis = stmt_source.executeQuery(MNDKK_Success);
				while(Rs46dnis.next())
				{
					try{

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
	                    TIME1 = Rs46dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_airtelkk values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
						}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
	            String MNDKK_failure = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',status as 'CHARGING AMOUNT',circle,if(chrg_amount is null,'0',chrg_amount) 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(response_time),time(response_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(date_add(now(), interval -"+day+" day)) and service_id=1513 and plan_id=81";
				ResultSet Rs47dnis = stmt_source.executeQuery(MNDKK_failure);
				System.out.println("***********AIRTEL MNDKK FAILURE TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs47dnis.next())
				{
					try{

	            	    MSISDN = Rs47dnis.getString("MSISDN");
	                    MODE = Rs47dnis.getString("MODE");
	                    CHARGINGAMOUNT = Rs47dnis.getString("CHARGING AMOUNT");
	                    CIRCLE = Rs47dnis.getString("circle");
	                    CIRCLE = getCIRCLE(CIRCLE);
	                    CHARGINGREASON = Rs47dnis.getString("CHARGING REASON");
	                    DATE = Rs47dnis.getString("DATE");
	                    TYPE = Rs47dnis.getString("TYPE");
	                    USER_TYPE = Rs47dnis.getString("USER_TYPE");
	                    SERVICE = Rs47dnis.getString("SERVICE");
	                    DATE1 = Rs47dnis.getString("date(response_time)");
	                    TIME1 = Rs47dnis.getString("time(response_time)");
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_airtelkk values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }
				String MNDKK_unsub = "select concat('91',ani) 'MSISDN',if(unsub_reason is null,'IVR',unsub_reason) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,'SUCCESS' as 'CHARGING REASON',unsub_date 'DATE','Deactivation' as 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(unsub_date),time(unsub_date) from airtel_mnd.tbl_character_unsub1 nolock where date(unsub_date)=date(date_add(now(), interval -"+day+" day)) and plan_id=81";
				ResultSet Rs48dnis = stmt_source.executeQuery(MNDKK_unsub);
				System.out.println("***********AIRTEL MNDKK UNSUB TABLE DATA INSERTING AT DESTINATION***********");
				while(Rs48dnis.next())
				{
					try{

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
	                    //System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_mnd_airtelkk values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
						catch(Exception e)
						{
						System.out.println(e);
						}
	            }*/
						System.out.println("************************UPDATING THE DB LOGS**********************************");
						stmt_destination.executeUpdate("update tbl_billing_vh1 set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update tbl_billing_vh1 set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update tbl_billing_vh1 set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						stmt_destination.executeUpdate("update tbl_billing_gl_airtel set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_gl_airtel set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_gl_airtel set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						stmt_destination.executeUpdate("update misdata.tbl_billing_54646_airtel set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
						stmt_destination.executeUpdate("update misdata.tbl_billing_54646_airtel set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_54646_airtel set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL ");

						stmt_destination.executeUpdate("update misdata.tbl_billing_ria_airtel set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_ria_airtel set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_ria_airtel set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						stmt_destination.executeUpdate("update misdata.tbl_billing_mtv set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_mtv set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_mtv set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						stmt_destination.executeUpdate("update misdata.tbl_billing_pd_airtel set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
						stmt_destination.executeUpdate("update misdata.tbl_billing_pd_airtel set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_pd_airtel set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						stmt_destination.executeUpdate("update misdata.tbl_billing_mnd_airtel set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_mnd_airtel set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_mnd_airtel set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						/*stmt_destination.executeUpdate("update misdata.tbl_billing_mnd_airtelkk set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_mnd_airtelkk set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_mnd_airtelkk set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");
						*/
						stmt_destination.executeUpdate("update misdata.tbl_billing_comedy set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
						stmt_destination.executeUpdate("update misdata.tbl_billing_comedy set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_comedy set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						stmt_destination.executeUpdate("update misdata.tbl_billing_devo_airtel set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_devo_airtel set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_devo_airtel set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						stmt_destination.executeUpdate("update misdata.tbl_billing_se_airtel set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_se_airtel set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_se_airtel set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						stmt_destination.executeUpdate("update misdata.tbl_billing_mu_airtel set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_mu_airtel set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_mu_airtel set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL;");

						stmt_destination.executeUpdate("update misdata.tbl_billing_pk set mode='IVR' where mode REGEXP '^-?[0-9]+$'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_pk set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA'");
						stmt_destination.executeUpdate("update misdata.tbl_billing_pk set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL");

						//System.out.println("****Updations done closing connections**** Good bye See u again****");
						Date Current_Date1 = new Date();
						System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));

						Thread.sleep(1000);
						Fun_FileCreator(day);

						Thread.sleep(2000);
						RunScript("/home/Java/AirtelBillingDataProcess/createTarFiles.sh");
						System.out.println("***Billing files compressed**** Good bye See u again****");


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
					c = "Others";
				return c;

			}

			public static void main(String arg[])
			{
				if(arg.length>0)
					day = Integer.parseInt(arg[0]);
				else
					day = 1;
				AirtelBillingDataProcess c = new AirtelBillingDataProcess();
				c.start();

			}

		}
