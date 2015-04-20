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
	public static void main(String arg[])
	{
		if(arg.length>0)
			day = Integer.parseInt(arg[0]);
		else
			day = 1;
		DocomoDataValidation c = new DocomoDataValidation();
		c.start();
	}

	public void run()
	{
		try
		{
			//day=1;
			DocomoValidation();
			Hun4646Validation();
			fmjValidation();
			RedfmValidation();
			RiyaValidation();
			MtvValidation();
			MNDValidation();
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
	public int DocomoValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_mu_tatadocomo nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("DocomoMU Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1001";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("DocomoMU Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1001";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("DocomoMU Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_radio.tbl_radio_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("DocomoMU Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;
				System.out.println("****Deleting old entry from validation table for Docomo MU****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='DocomoMU'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_DocomoData_Validation values('DocomoMU',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************Docomo count at source DB**** "+sum1+"******************************************************");
				System.out.println("************Docomo count at Destination DB**** "+cnt1+"*************************************************");
				if(cnt5 > 100)
					System.out.println("****Docomo MU count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****Docomo MU Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception DocomoValidation)
		{
				PrintAndLog("Exception in DocomoValidation "+DocomoValidation.toString()+".");
		}
		return 0;
	}
	public int Hun4646Validation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_54646_tatadocomo nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("Docomo54646 Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1002 and sc not like ('%P%')";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("Docomo54646 Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1002 and sc not like ('%P%')";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("Docomo54646 Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_hungama.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("Docomo54646 Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;
				System.out.println("****Deleting old entry from validation table for 54646****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='Hun54646'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_DocomoData_Validation values('Hun54646',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************Docomo54646 count at source DB**** "+sum1+"******************************************************");
				System.out.println("************Docomo54646 count at Destination DB**** "+cnt1+"*************************************************");
				if(cnt5 > 100)
					System.out.println("****Docomo54646 count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****Docomo54646 Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception Hun4646Validation)
		{
				PrintAndLog("Exception in DocomoValidation "+Hun4646Validation.toString()+".");
		}
		return 0;
	}
	public int RedfmValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from Tbl_billing_RedFM nolock where DATE1 = date(subdate(now()," + day + ")) and service='RedFMTataDocomo'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("DocomoRedfm Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1010";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("DocomoRedfm Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1010'";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("DocomoRedfm Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_redfm.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("DocomoRedfm Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;
				System.out.println("****Deleting old entry from validation table for Docomo Redfm****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='DocomoRedfm'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_DocomoData_Validation values('DocomoRedfm',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************DocomoRedfm count at source DB**** "+sum1+"******************************************************");
				System.out.println("************DocomoRedfm count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****DocomoRedfm count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****DocomoRedfm Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception RedfmValidation)
		{
				PrintAndLog("Exception in DocomoValidation "+RedfmValidation.toString()+".");
		}
		return 0;
	}
	public int MtvValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from Tbl_billing_MTV nolock where DATE1 = date(subdate(now()," + day + ")) and service='MTVTatadocomo'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("DocomoMTV Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1003'";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("DocomoMTV Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1003'";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("DocomoMTV Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_hungama.tbl_mtv_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("DocomoMTV Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for DocomoMTV****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='DocomoMTV'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_DocomoData_Validation values('DocomoMTV',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************DocomoMTV count at source DB**** "+sum1+"******************************************************");
				System.out.println("************DocomoMTV count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****DocomoMTV count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****DocomoMTV Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MtvValidation)
		{
				PrintAndLog("Exception in DocomoValidation "+MtvValidation.toString()+".");
		}
		return 0;
	}
	public int RiyaValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_ria nolock where DATE1 = date(subdate(now()," + day + ")) and service='RiaTatadocomo'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("DocomoRiya Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1009'";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("DocomoRiya Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1009'";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("DocomoRiya Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_manchala.tbl_riya_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("DocomoRiya Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;
				System.out.println("****Deleting old entry from validation table for DocomoRiya****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='DocomoRiya'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_DocomoData_Validation values('DocomoRiya',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************DocomoRiya count at source DB**** "+sum1+"******************************************************");
				System.out.println("************DocomoRiya count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****DocomoRiya count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****DocomoRiya Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception RiyaValidation)
		{
				PrintAndLog("Exception in DocomoValidation "+RiyaValidation.toString()+".");
		}
		return 0;
	}
	public int fmjValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_fmj_tatadocomo nolock  where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("DocomoFmj Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1005' and  Plan_id in('18','20','33','34')";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("DocomoFmj Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str22 = "select count(1) as CNT22 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1005' and  Plan_id in('19')";
				ResultSet localResultSet22 = stmt_source.executeQuery(str22);
				if (localResultSet22.next())
				{
					cnt6 = localResultSet22.getInt("CNT22");
					System.out.println("DocomoFmj Success table(19 plan id) count is= " + cnt6);
					localResultSet22.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1005' and  Plan_id in('18','20','33','34')";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt7 = localResultSet3.getInt("CNT3");
					System.out.println("DocomoFmj Failure table count is= " + cnt7);
					localResultSet3.close();
				}
	            String str33 = "select count(1) as CNT33 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1005' and  Plan_id in('19')";
				ResultSet localResultSet33 = stmt_source.executeQuery(str33);
				if (localResultSet33.next())
				{
					cnt3 = localResultSet33.getInt("CNT33");
					System.out.println("DocomoFmj Failure table(19 plan id) count is= " + cnt3);
					localResultSet33.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_starclub.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("DocomoFmj Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4 + cnt6 + cnt7;
				cnt5 = 	sum1 - cnt1;
				System.out.println("****Deleting old entry from validation table for DocomoFmj****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='DocomoFmj'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_DocomoData_Validation values('DocomoFmj',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************DocomoFmj count at source DB**** "+sum1+"******************************************************");
				System.out.println("************DocomoFmj count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****DocomoFmj count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****DocomoFmj Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;cnt6 = 0;cnt7 = 0;
		}
		catch(Exception fmjValidation)
		{
				PrintAndLog("Exception in fmj DocomoValidation "+fmjValidation.toString()+".");
		}
		return 0;
	}
	public int MNDValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_mnd_tatadocomo nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("DocomoMND Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1013'";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("DocomoMND Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id='1013'";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("DocomoMND Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from docomo_mnd.tbl_character_unsub1 nolock where date(unsub_date) = date(subdate(now(),"+day+")) and plan_id=106";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("DocomoMND Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for DocomoMND****");
				stmt_source.executeUpdate("delete from docomo_radio.tbl_DocomoData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='DocomoMND'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into docomo_radio.tbl_DocomoData_Validation values('DocomoMND',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************DocomoMND count at source DB**** "+sum1+"******************************************************");
				System.out.println("************DocomoMND count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****DocomoMND count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****DocomoMND Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MNDValidation)
		{
				PrintAndLog("Exception in DocomoValidation "+MNDValidation.toString()+".");
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
