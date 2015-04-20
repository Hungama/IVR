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


	public class UninorDataProcessCRI extends Thread
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

			dIP="119.82.69.218";
			dDSN="misdata";
			dUSR="kunalk.arora";
			dPWD="google";

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

	public UninorDataProcessCRI()
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
				String Sport_Succ = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE',if(chrg_amount is null,'0',chrg_amount) 'CHARGING AMOUNT',circle,if(status=1,'SUCCESS','FAILURE') 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(date_time),time(date_time) from master_db.tbl_billing_success_backup nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1408";
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
						DATE1 = Rs22dnis.getString("date(date_time)");
						TIME1 = Rs22dnis.getString("time(date_time)");
			//			System.out.println(MSISDN+","+MODE+","+CHARGINGAMOUNT+","+CIRCLE+","+CHARGINGREASON+","+DATE+","+TYPE+","+USER_TYPE+","+SERVICE+","+DATE1+","+TIME1);
						stmt_destination.executeUpdate("insert into tbl_billing_cri_Uninor values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
	            }
	            String Sport_fail = "select concat('91',msisdn) 'MSISDN',if(mode is null,'IVR',mode) 'MODE','0' as 'CHARGING AMOUNT',circle,status as 'CHARGING REASON',response_time 'DATE',case event_type when 'sub' then 'Activation' when 'TOPUP' then 'TOP-UP' when 'EVENT' then 'EVENT' else 'Renewal' end 'TYPE',pre_post 'USER_TYPE',plan_id 'SERVICE',date(date_time),time(date_time) from master_db.tbl_billing_failure nolock where date(response_time)=date(subdate(now(),"+day+")) and service_id=1408";
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
						DATE1 = Rs23dnis.getString("date(date_time)");
						TIME1 = Rs23dnis.getString("time(date_time)");
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
	            System.out.println("****Updations is Begining********");


				stmt_destination.executeUpdate("update tbl_billing_cri_Uninor set mode='IVR' where mode REGEXP '^-?[0-9]+$' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_cri_Uninor set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA' and date(Date) = date(subdate(now()," + day + "))");
				stmt_destination.executeUpdate("update tbl_billing_cri_Uninor set `USER TYPE`='NA' where  (`USER TYPE` in ('0','/N','NULL','') or `USER TYPE` is NULL) and date(Date) = date(subdate(now()," + day + "))");

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
		UninorDataProcessCRI c = new UninorDataProcessCRI();
		c.start();

	}

}
