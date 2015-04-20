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

public class MTSDataValidation extends Thread
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
	public int counter = 0;
	public static int sum1 = 0;

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

	public MTSDataValidation()
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
		MTSDataValidation validation = new MTSDataValidation();
		validation.start();
	}

	public void run()
	{
		try
		{
			MtsMuValidation();
			MTS54646Validation();
			MtvValidation();
			CelebValidation();
			RedfmValidation();
			VAValidation();
			MNDValidation();
			DevoValidation();
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
	public int MtsMuValidation()
	{
		try
		{
				ResultSet localResultSet1	=	null;
				String str1 = "select count(1) as CNT1 from tbl_billing_mu_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				try{localResultSet1 = stmt_destination.executeQuery(str1);}catch(Exception e){PrintAndLog("Exception in Getting Count "+e.toString()+".");HandleXception(e);}
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("MtsMu Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1101";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("MtsMu Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1101";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("MtsMu Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from mts_mu.tbl_HB_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("MtsMu Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for MtsMu****");
				stmt_source.executeUpdate("delete from mts_radio.tbl_MTSData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='MtsMu'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into mts_radio.tbl_MTSData_Validation values('MtsMu',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************MtsMu count at source DB**** "+sum1+"******************************************************");
				System.out.println("************MtsMu count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****MtsMu count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****MtsMu Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MtsMuValidation)
		{
				PrintAndLog("Exception in MtsMuValidation "+MtsMuValidation.toString()+".");
				HandleXception(MtsMuValidation);
		}
		return 0;
	}
	public int MTS54646Validation()
	{
		try
		{
				ResultSet localResultSet1	=	null;
				String str1 = "select count(1) as CNT1 from tbl_billing_54646_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				try{localResultSet1 = stmt_destination.executeQuery(str1);}catch(Exception e){PrintAndLog("Exception in Getting Count "+e.toString()+".");HandleXception(e);}
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("MTS54646 Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1102";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("MTS54646 Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1102";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("MTS54646 Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from mts_hungama.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+")) and Plan_id not in('95')";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("MTS54646 Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for MTS54646****");
				stmt_source.executeUpdate("delete from mts_radio.tbl_MTSData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='Hun54646'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into mts_radio.tbl_MTSData_Validation values('Hun54646',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************MTS54646 count at source DB**** "+sum1+"******************************************************");
				System.out.println("************MTS54646 count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****MTS54646 count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****MTS54646 Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MTS54646Validation)
		{
				PrintAndLog("Exception in MTS54646Validation "+MTS54646Validation.toString()+".");
				HandleXception(MTS54646Validation);
		}
		return 0;
	}
	public int RedfmValidation()
	{
		try
		{
				ResultSet localResultSet1	=	null;
				String str1 = "select count(1) as CNT1 from Tbl_billing_RedFM nolock where DATE1 = date(subdate(now()," + day + ")) and service='RedFMMTS'";
				try{localResultSet1 = stmt_destination.executeQuery(str1);}catch(Exception e){PrintAndLog("Exception in Getting Count "+e.toString()+".");HandleXception(e);}
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("MTSRedfm Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1110";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("MTSRedfm Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1110";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("MTSRedfm Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from mts_redfm.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("MTSRedfm Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for Docomo Redfm****");
				stmt_source.executeUpdate("delete from mts_radio.tbl_MTSData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='MTSRedfm'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into mts_radio.tbl_MTSData_Validation values('MTSRedfm',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************MTSRedfm count at source DB**** "+sum1+"******************************************************");
				System.out.println("************MTSRedfm count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****MTSRedfm count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****MTSRedfm Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception RedfmValidation)
		{
				PrintAndLog("Exception in RedfmValidation "+RedfmValidation.toString()+".");
				HandleXception(RedfmValidation);
		}
		return 0;
	}
	public int MtvValidation()
	{
		try
		{
				ResultSet localResultSet1	=	null;
				String str1 = "select count(1) as CNT1 from Tbl_billing_MTV nolock where DATE1 = date(subdate(now()," + day + ")) and service='MTVMTS'";
				try{localResultSet1 = stmt_destination.executeQuery(str1);}catch(Exception e){PrintAndLog("Exception in Getting Count "+e.toString()+".");HandleXception(e);}
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("MtsMTV Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1103";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("MtsMTV Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1103";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("MtsMTV Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from mts_mtv.tbl_mtv_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("MtsMTV Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for MtsMTV****");
				stmt_source.executeUpdate("delete from mts_radio.tbl_MTSData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='MtsMTV'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into mts_radio.tbl_MTSData_Validation values('MtsMTV',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************MtsMTV count at source DB**** "+sum1+"******************************************************");
				System.out.println("************MtsMTV count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****MtsMTV count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****MtsMTV Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MtvValidation)
		{
				PrintAndLog("Exception in MtvValidation "+MtvValidation.toString()+".");
				HandleXception(MtvValidation);
		}
		return 0;
	}
	public int DevoValidation()
	{
		try
		{
				ResultSet localResultSet1	=	null;
				String str1 = "select count(1) as CNT1 from tbl_billing_devo_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				try{localResultSet1 = stmt_destination.executeQuery(str1);}catch(Exception e){PrintAndLog("Exception in Getting Count "+e.toString()+".");HandleXception(e);}
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("mtsDevo Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1111";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("mtsDevo Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1111";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("mtsDevo Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from dm_radio.tbl_digi_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("mtsDevo Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for mtsDevo****");
				stmt_source.executeUpdate("delete from mts_radio.tbl_MTSData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='mtsDevo'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into mts_radio.tbl_MTSData_Validation values('mtsDevo',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************mtsDevo count at source DB**** "+sum1+"******************************************************");
				System.out.println("************mtsDevo count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****mtsDevo count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****mtsDevo Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception DevoValidation)
		{
				PrintAndLog("Exception in DevoValidation "+DevoValidation.toString()+".");
				HandleXception(DevoValidation);
		}
		return 0;
	}
	public int CelebValidation()
	{
		try
		{
				ResultSet localResultSet1	=	null;
				String str1 = "select count(1) as CNT1 from tbl_billing_fmj_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				try{localResultSet1 = stmt_destination.executeQuery(str1);}catch(Exception e){PrintAndLog("Exception in Getting Count "+e.toString()+".");HandleXception(e);}
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("mtsCeleb Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1106";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("mtsCeleb Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1106";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("mtsCeleb Failure table count is= " + cnt7);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from mts_starclub.tbl_jbox_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("mtsCeleb Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for mtsCeleb****");
				stmt_source.executeUpdate("delete from mts_radio.tbl_MTSData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='mtsCeleb'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into mts_radio.tbl_MTSData_Validation values('mtsCeleb',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************mtsCeleb count at source DB**** "+sum1+"******************************************************");
				System.out.println("************mtsCeleb count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****mtsCeleb count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****mtsCeleb Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception CelebValidation)
		{
				PrintAndLog("Exception in CelebValidation "+CelebValidation.toString()+".");
				HandleXception(CelebValidation);
		}
		return 0;
	}
	public int VAValidation()
	{
		try
		{
				ResultSet localResultSet1	=	null;
				String str1 = "select count(1) as CNT1 from tbl_billing_vs_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				try{localResultSet1 = stmt_destination.executeQuery(str1);}
				catch(Exception e){PrintAndLog("Exception in Getting Count "+e.toString()+".");HandleXception(e);}
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("mtsVA Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1116";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("mtsVA Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1116";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("mtsVA Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from mts_voicealert.tbl_voice_unsub nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("mtsVA Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for mtsVA****");
				stmt_source.executeUpdate("delete from mts_radio.tbl_MTSData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='mtsVA'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into mts_radio.tbl_MTSData_Validation values('mtsVA',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************mtsVA count at source DB**** "+sum1+"******************************************************");
				System.out.println("************mtsVA count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****mtsVA count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****mtsVA Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception VAValidation)
		{
				PrintAndLog("Exception in VAValidation "+VAValidation.toString()+".");
				HandleXception(VAValidation);
		}
		return 0;
	}
	public int MNDValidation()
	{
		try
		{
				ResultSet localResultSet1	=	null;
				String str1 = "select count(1) as CNT1 from tbl_billing_mnd_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				try{localResultSet1 = stmt_destination.executeQuery(str1);}catch(Exception e){PrintAndLog("Exception in Getting Count "+e.toString()+".");HandleXception(e);}
				if(localResultSet1.next())
				{
					cnt1 = localResultSet1.getInt("CNT1");
					System.out.println("mtsMND Destination table count is= " + cnt1);
					localResultSet1.close();
				}
	            String str2 = "select count(1) as CNT2 from master_db.tbl_billing_success nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1113";
				ResultSet localResultSet2 = stmt_source.executeQuery(str2);
				if (localResultSet2.next())
				{
					cnt2 = localResultSet2.getInt("CNT2");
					System.out.println("mtsMND Success table count is= " + cnt2);
					localResultSet2.close();
				}
	            String str3 = "select count(1) as CNT3 from master_db.tbl_billing_failure nolock where date(response_time) = date(subdate(now(),"+day+")) and service_id=1113";
				ResultSet localResultSet3 = stmt_source.executeQuery(str3);
				if (localResultSet3.next())
				{
					cnt3 = localResultSet3.getInt("CNT3");
					System.out.println("mtsMND Failure table count is= " + cnt3);
					localResultSet3.close();
				}
	            String str4 = "select count(1) as CNT4 from mts_mnd.tbl_character_unsub1 nolock where date(unsub_date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet4 = stmt_source.executeQuery(str4);
				if (localResultSet4.next())
				{
					cnt4 = localResultSet4.getInt("CNT4");
					System.out.println("mtsMND Unsub table count is= " + cnt4);
					localResultSet4.close();
				}
				sum1= cnt2 + cnt3 + cnt4;
				cnt5 = 	sum1 - cnt1;

				System.out.println("****Deleting old entry from validation table for mtsMND****");
				stmt_source.executeUpdate("delete from mts_radio.tbl_MTSData_Validation where date(date_time) = date(subdate(now(),"+day+")) and service_name='mtsMND'");
				System.out.println("****Inserting into validation table****");
				stmt_source.executeUpdate("insert into mts_radio.tbl_MTSData_Validation values('mtsMND',subdate(now(),"+day+"),'"+sum1+"','"+cnt1+"','"+cnt5+"')");
				System.out.println("********************************************************************************************************");
				System.out.println("************mtsMND count at source DB**** "+sum1+"******************************************************");
				System.out.println("************mtsMND count at Destination DB**** "+cnt1+"*************************************************");

				if(cnt5 > 100)
					System.out.println("****mtsMND count Difference at Both DB**** "+cnt5+"**********************************************");
				else
					System.out.println("****mtsMND Validation is fine,difference is**** "+cnt5+"*****************************************");
				System.out.println("********************************************************************************************************");
				cnt1 = 0;cnt2 = 0;cnt3 = 0;cnt4 = 0;cnt5 = 0;sum1 = 0;
		}
		catch(Exception MNDValidation)
		{
				PrintAndLog("Exception in MNDValidation "+MNDValidation.toString()+".");
				HandleXception(MNDValidation);
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
	/*********************************************************************************/
  	public void HandleXception(Exception paramException)
  	{
		counter += 1;
	    if (counter > 5)
    	{
			System.out.println("************ Thread is going to close**************");
			System.exit(0);
    	}
		else if ((paramException.toString().trim().equalsIgnoreCase("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure")) || (paramException.toString().trim().startsWith("com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException:")) || (paramException.toString().trim().equalsIgnoreCase("java.net.SocketException: Connection reset")) || (paramException.toString().trim().startsWith("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure")))
		{
		  try
		  {
			con_destination = null;
			System.out.println("***********Reconnecting to Datbases***********");

			this.dIP = "192.168.100.218";
			this.dDSN = "misdata";
			this.dUSR = "billing";
			this.dPWD = "billing";

			System.out.println("**SOURCE IP is  [" + this.sIP + "] **  DSN is [" + this.sDSN + "] Usr is [" + this.sUSR + "] Pwd is [" + this.sPWD + "]\t**");
			System.out.println("**DESTINATION IP is  [" + this.dIP + "] **  DSN is [" + this.dDSN + "] Usr is [" + this.dUSR + "] Pwd is [" + this.dPWD + "]\t**");

			Class.forName("com.mysql.jdbc.Driver");

			con_source = DriverManager.getConnection("jdbc:mysql://"+sIP+"/"+sDSN, sUSR, sPWD);
			stmt_source = con_source.createStatement();
			System.out.println("###RECONNECTION TO DB UP FOR SOURCE DB###");
			con_destination = DriverManager.getConnection("jdbc:mysql://" + this.dIP + "/" + this.dDSN, this.dUSR, this.dPWD);
			System.out.println("***Database Connection established!***");

			stmt_destination = con_destination.createStatement();
			System.out.println("###RECONNECTION TO DB UP FOR DESTINATION DB###");
		  }
		  catch (Exception localException)
		  {
			PrintAndLog("Exception in Connecting to Database " + localException.toString() + ".");
		  }
		}
	}

}
