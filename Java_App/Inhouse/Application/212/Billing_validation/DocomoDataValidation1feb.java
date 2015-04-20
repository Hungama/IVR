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


	public class DocomoDataValidation extends Thread
	{
	public static Connection con_source=null,con_destination=null;
	public static Statement stmt_source,stmt_destination;
	public String sIP=null,sDSN=null,sUSR=null,sPWD=null;
	public String dIP=null,dDSN=null,dUSR=null,dPWD=null;
	public static CallableStatement cstmt=null;
	public static int day=1;
	public static int cnt1 = 0;
	public static int cnt2 = 0;
	public static int cnt3 = 0;
	public static int cnt4 = 0;
	public static int cnt5 = 0;
	public static int cnt6 = 0;
	public static int cnt7 = 0;
	public static int cnt8 = 0;
	public static int cnt9 = 0;
	public static int sum1 = 0;

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
			dDSN="misdata2";
			dUSR="noc";
			dPWD="noc123";

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

	public DocomoDataValidation()
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

				String str1 = "select count(1) as CNT1 from tbl_billing_mu_tatadocomo where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				  if (localResultSet1.next())
				  {
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("ROW COUNT IS= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1001'";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("ROW COUNT IS= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1001'";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("ROW COUNT IS= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_radio.tbl_radio_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("ROW COUNT IS= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				System.out.println("****Deleting old entry from validation table****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+"))");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_DocomoData_Validation values('DocomoMU',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("****Docomo count at source DB**** "+sum1);
				System.out.println("****Docomo count at Destination DB**** "+cnt1);
				cnt5 = 	sum1 - cnt1;
				if(cnt5 > 100)
					System.out.println("****Docomo MU count Difference at Both DB**** "+cnt5);
				else
					System.out.println("****Docomo MU Validation is fine**** "+cnt5);
				System.out.println("********************************************************************************************************");
	            /*String str5 = "select count(1) as CNT from tbl_billing_ria where date(Date) = date(subdate(now()," + day + ")) and service='RiaTatadocomo'";
	            stmt_destination.executeUpdate("insert into tbl_billing_mu_mts values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
				ResultSet localResultSet5 = stmt_destination.executeQuery(str5);
				if (localResultSet5.next())
				{
					i = localResultSet5.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet5.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_ria where date(Date) = date(subdate(now()," + day + ")) and service='RiaTatadocomo'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
	            String str6 = "select count(1) as CNT from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVTatadocomo'";
				ResultSet localResultSet6 = stmt_destination.executeQuery(str6);
				if (localResultSet6.next())
				{
					i = localResultSet6.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet6.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVTatadocomo'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				stmt_destination.executeUpdate("update tbl_billing_mu_tatadocomo set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
				stmt_destination.executeUpdate("update tbl_billing_mu_tatadocomo set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA';");
				stmt_destination.executeUpdate("update tbl_billing_mu_tatadocomo set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL;");

				stmt_destination.executeUpdate("update tbl_billing_mtv set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
				stmt_destination.executeUpdate("update tbl_billing_mtv set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA';");
				stmt_destination.executeUpdate("update tbl_billing_mtv set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL;");

				stmt_destination.executeUpdate("update tbl_billing_ria set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
				stmt_destination.executeUpdate("update tbl_billing_ria set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA';");
				stmt_destination.executeUpdate("update tbl_billing_ria set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL;");

				stmt_destination.executeUpdate("update tbl_billing_redfm set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
				stmt_destination.executeUpdate("update tbl_billing_redfm set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA';");
				stmt_destination.executeUpdate("update tbl_billing_redfm set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL;");

				stmt_destination.executeUpdate("update tbl_billing_fmj_tatadocomo set type='Activation_Ticket' where service='20' and type='Activation'");
				stmt_destination.executeUpdate("update tbl_billing_fmj_tatadocomo set type='Renewal_Ticket' where service='20' and type='Renewal'");
				stmt_destination.executeUpdate("update tbl_billing_fmj_tatadocomo set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
				stmt_destination.executeUpdate("update tbl_billing_fmj_tatadocomo set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA';");
				stmt_destination.executeUpdate("update tbl_billing_fmj_tatadocomo set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL;");

				stmt_destination.executeUpdate("update tbl_billing_54646_tatadocomo set mode='IVR' where mode REGEXP '^-?[0-9]+$';");
				stmt_destination.executeUpdate("update tbl_billing_54646_tatadocomo set `CHARGING REASON`='33' where  `CHARGING REASON` is NULL or`CHARGING REASON`='NA';");
				stmt_destination.executeUpdate("update tbl_billing_54646_tatadocomo set `USER TYPE`='NA' where  `USER TYPE` in ('0','/N','NULL') or `USER TYPE` is NULL;");
				*/

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
		else if("HAY".equalsIgnoreCase(CIR))
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
		else if("PUB".equalsIgnoreCase(CIR))
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
	public static void main(String arg[])
	{
		if(arg.length>0)
			day = Integer.parseInt(arg[0]);
		else
			day = 1;
		DocomoDataValidation c = new DocomoDataValidation();
		c.start();

	}

}
