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
import java.util.*;
import java.io.*;

public class VmiDataValidation extends Thread
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

	public VmiDataValidation()
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
		VmiDataValidation c = new VmiDataValidation();
		c.start();
	}

	public void run()
	{
		try
		{
			day=1;
			VmiValidation();
			RedfmValidation();
			RiyaValidation();
			stmt_source.close();
			stmt_destination.close();
			con_source.close();
			con_destination.close();
		}
		catch(Exception ex){PrintAndLog("Exception in run method "+ex.toString());}
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
	public int VmiValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_mu_tatadocomovmi nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("VmiMU Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1801";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("VmiMU Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1801";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("VmiMU Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_radio.tbl_radio_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+")) and plan_id in(40,49,50)";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("VmiMU Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for VMI MU****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_VmiData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='VmiMU'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_VmiData_Validation values('VmiMU',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************VmiMU count at source DB**** "+sum1+"******************************************************");
				System.out.println("************VmiMU count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****VmiMU count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****VmiMU Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception VmiValidation)
		{
				PrintAndLog("Exception in VmiValidation "+VmiValidation.toString()+".");
		}
		return 0;
	}

	public int RedfmValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from Tbl_billing_RedFM nolock  where DATE1 = date(subdate(now()," + day + ")) and service='RedFMTataDoCoMovmi'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("VmiRedfm Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1810";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("VmiRedfm Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1810";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("VmiRedfm Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from virgin_redfm.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("VmiRedfm Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for Docomo Redfm****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='VmiRedfm'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_VmiData_Validation values('VmiRedfm',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************VmiRedfm count at source DB**** "+sum1+"******************************************************");
				System.out.println("************VmiRedfm count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****VmiRedfm count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****VmiRedfm Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception RedfmValidation)
		{
				PrintAndLog("Exception in VmiValidation "+RedfmValidation.toString()+".");
		}
		return 0;
	}
	public int RiyaValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_ria nolock where DATE1 = date(subdate(now()," + day + ")) and service='RIATataDoCoMovmi'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("VmiRiya Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1809";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("VmiRiya Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1809";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("VmiRiya Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_manchala.tbl_riya_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+")) and plan_id=73";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("VmiRiya Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for VmiRiya****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_VmiData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='VmiRiya'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_VmiData_Validation values('VmiRiya',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************VmiRiya count at source DB**** "+sum1+"******************************************************");
				System.out.println("************VmiRiya count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****VmiRiya count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****VmiRiya Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception RiyaValidation)
		{
				PrintAndLog("Exception in DocomoValidation "+RiyaValidation.toString()+".");
		}
		return 0;
	}
	/*************************************************************************/
	public int PrintAndLog(String Buff)
	{
		try
		{
			Calendar today 	= Calendar.getInstance();
			String FileName	= "DATA_VALIDATION";
			String strlogfile = ""+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
			String strdate = formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
			String strtime = formatN(""+today.get(Calendar.HOUR_OF_DAY),2)+formatN(""+today.get(Calendar.MINUTE),2)+formatN(""+today.get(Calendar.SECOND),2);
			Buff = "["+FileName+" "+strdate+" "+strtime +"]--> "+Buff;
			System.out.println(Buff);
			FileOutputStream outfile = new FileOutputStream("./log/" +FileName+"_"+ strlogfile + ".log",true);
			PrintStream outprint = new PrintStream(outfile);
			outprint.println(Buff);
			outprint.close();
			outfile.close();
			return 1;
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			return 0;
		}
	}
	/*********************************************************************************/
	public String formatN(String str, int x)
	{
		int len;
		String ret_str="";
		len = str.length();
		if (len >= x)
			ret_str = str;
		else
		{
			for(int i=0; i<x-len; i++)
				ret_str = ret_str + "0";
			ret_str = ret_str + str;
		}
		return ret_str;
	}


}
