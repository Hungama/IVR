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

public class AirtleDataValidation_duggal extends Thread
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

			sIP="10.2.73.160";
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

	public AirtleDataValidation_duggal()
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
		AirtleDataValidation_duggal c = new AirtleDataValidation_duggal();
		c.start();
	}

	public void run()
	{
		try
		{
			day=3;
			Vh1Validation();
			GudlifeValidation();
			Air54646Validation();
			RiyaValidation();
			MTVValidation();
			PDUValidation();
			MNDValidation();
			ComedyValidation();
			DevValidation();
			ESValidation();
			EUValidation();
			PKValidation();
			MANValidation();
			CrazyValidation();
		}
		catch(Exception ex){PrintAndLog("Exception in run method "+ex.toString());}
		finally
		{
			// The finally clause is always executed - even in error
			// conditions PreparedStatements and Connections will always be closed
			try
			{
				stmt_source.close();
				stmt_destination.close();
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
	public int Vh1Validation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_vh1 nolock where date(Date) = date(subdate(now(),"+day+")) and service='VH1Airtel'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelVH1 Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1507";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelVH1 Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1507";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelVH1 Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_vh1.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelVH1 Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for Airtel VH1****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelVH1'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelVH1',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelVH1 count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelVH1 count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelVH1 count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelVH1 Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception Vh1Validation)
		{
				PrintAndLog("Exception in Vh1Validation "+Vh1Validation.toString()+".");
		}
		return 0;
	}
	public int GudlifeValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_gl_airtel nolock where date(Date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelGL Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1511 and plan_id in(29,46)";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelGL Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1511 and plan_id in(29,46)";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelGL Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_rasoi.tbl_rasoi_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelGL Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for Airtel GUD LIFE****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelGL'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelGL',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelGL count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelGL count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelGL count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelGL Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception GudlifeValidation)
		{
				PrintAndLog("Exception in GudlifeValidation "+GudlifeValidation.toString()+".");
		}
		return 0;
	}
	public int Air54646Validation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_54646_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("Airtel54646 Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1502 and plan_id!=50 and sc not like ('5464634P%')";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("Airtel54646 Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1502 and plan_id!=50 and sc not like ('5464634P%')";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("Airtel54646 Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_hungama.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+")) and plan_id!=50 ";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("Airtel54646 Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for Airtel54646****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='Airtel54646'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('Airtel54646',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************Airtel54646 count at source DB**** "+sum1+"******************************************************");
				System.out.println("************Airtel54646 count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****Airtel54646 count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****Airtel54646 Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception Air54646Validation)
		{
				PrintAndLog("Exception in Air54646Validation "+Air54646Validation.toString()+".");
		}
		return 0;
	}
	public int RiyaValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_ria_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelRiya Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1511 and plan_id in(30,48)";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelRiya Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1511 and plan_id in(30,48)";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelRiya Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_manchala.tbl_riya_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelRiya Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelRiya****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelRiya'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelRiya',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelRiya count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelRiya count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelRiya count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelRiya Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception RiyaValidation)
		{
				PrintAndLog("Exception in RiyaValidation "+RiyaValidation.toString()+".");
		}
		return 0;
	}
	public int MTVValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVAirtel'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelMTV Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1503";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelMTV Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1503";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelMTV Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_hungama.tbl_mtv_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelMTV Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelMTV****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelMTV'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelMTV',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelMTV count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelMTV count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelMTV count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelMTV Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MTVValidation)
		{
				PrintAndLog("Exception in MTVValidation "+MTVValidation.toString()+".");
		}
		return 0;
	}
	public int PDUValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_pd_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelPDU Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1514";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelPDU Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1514";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelPDU Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_EDU.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelPDU Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelPDU****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelPDU'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelPDU',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelPDU count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelPDU count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelPDU count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelPDU Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception PDUValidation)
		{
				PrintAndLog("Exception in PDUValidation "+PDUValidation.toString()+".");
		}
		return 0;
	}
	public int MNDValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_mnd_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelMND Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1513";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelMND Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1513";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelMND Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_mnd.tbl_character_unsub1 nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelMND Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelMND****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelMND'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelMND',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelMND count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelMND count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelMND count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelMND Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MNDValidation)
		{
				PrintAndLog("Exception in MNDValidation "+MNDValidation.toString()+".");
		}
		return 0;
	}
	public int ComedyValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_comedy where date(Date) = date(subdate(now()," + day + ")) and service='AirtelComedy'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelComedy Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1518 and plan_id=50";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelComedy Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1518 and plan_id=50";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelComedy Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_hungama.tbl_comedyportal_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+")) and plan_id=50";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelComedy Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelComedy****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelComedy'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelComedy',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelComedy count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelComedy count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelComedy count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelComedy Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception ComedyValidation)
		{
				PrintAndLog("Exception in ComedyValidation "+ComedyValidation.toString()+".");
		}
		return 0;
	}
	public int DevValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_devo_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelDev Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1515";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelDev Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1515";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelDev Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_devo.tbl_devo_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelDev Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelDev****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelDev'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelDev',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelDev count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelDev count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelDev count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelDev Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception DevValidation)
		{
				PrintAndLog("Exception in DevValidation "+DevValidation.toString()+".");
		}
		return 0;
	}
	public int ESValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_se_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelES Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1517";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelES Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1517";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelES Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_SPKENG.tbl_spkeng_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelES Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelES****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelES'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelES',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelES count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelES count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelES count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelES Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception ESValidation)
		{
				PrintAndLog("Exception in ESValidation "+ESValidation.toString()+".");
		}
		return 0;
	}
	public int EUValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_mu_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelEU Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1501";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelEU Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1501";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelEU Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_radio.tbl_radio_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelEU Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelEU****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelEU'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelEU',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelEU count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelEU count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelEU count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelEU Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception EUValidation)
		{
				PrintAndLog("Exception in EUValidation "+EUValidation.toString()+".");
		}
		return 0;
	}
	public int PKValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_pk where date(Date) = date(subdate(now(),"+day+")) and service='AirtelPK'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelPK Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1520";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelPK Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1520";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelPK Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_hungama.tbl_pk_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelPK Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelPK****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelPK'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelPK',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelPK count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelPK count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelPK count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelPK Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception PKValidation)
		{
				PrintAndLog("Exception in PKValidation "+PKValidation.toString()+".");
		}
		return 0;
	}
	public int MANValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_comedy where date(Date) = date(subdate(now(),"+day+")) and service='AirtelRegKK'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelManaPata Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1522 and plan_id=63";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelManaPata Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1522 and plan_id=63";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelManaPata Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_hungama.tbl_arm_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+")) and plan_id=63";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelManaPata Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelManaPata****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelManoranjan'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelManoranjan',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelManaPata count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelManaPata count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelManaPata count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelManaPata Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MANValidation)
		{
				PrintAndLog("Exception in MANValidation "+MANValidation.toString()+".");
		}
		return 0;
	}
	public int CrazyValidation()
	{
		try
		{
				String str1 = "select count(1) as CNT1 from tbl_billing_comedy where date(Date) = date(subdate(now(),"+day+")) and service='AirtelRegTN'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("AirtelCrazy Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success_backup nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1522 and plan_id=64";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("AirtelCrazy Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1522 and plan_id=64";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("AirtelCrazy Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from airtel_hungama.tbl_arm_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+")) and plan_id=64";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("AirtelCrazy Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for AirtelCrazy****");
				stmt_source.executeUpdate("delete from airtel_radio.tbl_AirtelData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='AirtelCrazy'");

				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into airtel_radio.tbl_AirtelData_Validation values('AirtelCrazy',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************AirtelCrazy count at source DB**** "+sum1+"******************************************************");
				System.out.println("************AirtelCrazy count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****AirtelCrazy count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****AirtelCrazy Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception CrazyValidation)
		{
				PrintAndLog("Exception in CrazyValidation "+CrazyValidation.toString()+".");
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
