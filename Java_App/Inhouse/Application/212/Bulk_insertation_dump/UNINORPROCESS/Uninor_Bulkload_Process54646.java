import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.sql.*;


	public class Uninor_Bulkload_Process54646 extends Thread
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

			/*sIP="192.168.100.224";
			sDSN="master_db";
			sUSR="billing";
			sPWD="billing";*/

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

	public Uninor_Bulkload_Process54646()
	{
		try
		{
			readDBCONFIG();
			System.out.println("Initiallizing DB");
			Class.forName("com.mysql.jdbc.Driver");
			//con_source = DriverManager.getConnection("jdbc:mysql://"+sIP+"/"+sDSN, sUSR, sPWD);
			con_destination=DriverManager.getConnection("jdbc:mysql://"+dIP+"/"+dDSN, dUSR, dPWD);
			System.out.println("Database Connection established!");
			//stmt_source = con_source.createStatement();
			stmt_destination = con_destination.createStatement();
			System.out.println("DB UP");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void JADBulkLoad(String filepath)
	{
		try
		{
				String str1 = "select count(1) as CNT from tbl_billing_astro_uninor where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_astro_uninor where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR JAD DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_astro_uninor " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR JAD DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void BulkLoad54646(String filepath)
	{
		try
		{
	            String str2 = "select count(1) as CNT from tbl_billing_54646_uninor where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				  if (localResultSet2.next())
				  {
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_uninor where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR 54646 DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_54646_uninor " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR 54646 DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void RTBulkLoad(String filepath)
	{
		try
		{
	            String str3 = "select count(1) as CNT from tbl_billing_rt_uninor where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet3 = stmt_destination.executeQuery(str3);
				if (localResultSet3.next())
				{
					i = localResultSet3.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet3.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_rt_uninor where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR RT INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_rt_uninor " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR RT DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void AABulkLoad(String filepath)
	{
		try
		{
	            String str4 = "select count(1) as CNT from tbl_billing_aa_uninor where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet4 = stmt_destination.executeQuery(str4);
				if (localResultSet4.next())
				{
					i = localResultSet4.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet4.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_aa_uninor where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR AA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_aa_uninor " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR AA DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void REDFMBulkLoad(String filepath)
	{
		try
		{
	            String str5 = "select count(1) as CNT from tbl_billing_redfm where DATE1 = date(subdate(now()," + day + ")) and service in('RedFMUninor','WAPREDFMUninor')";
				ResultSet localResultSet5 = stmt_destination.executeQuery(str5);
				if (localResultSet5.next())
				{
					i = localResultSet5.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet5.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_redfm where DATE1 = date(subdate(now()," + day + ")) and service in('RedFMUninor','WAPREDFMUninor')");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR REDFM DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_redfm " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR REDFM DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void RIYABulkLoad(String filepath)
	{
		try
		{
	            String str6 = "select count(1) as CNT from tbl_billing_ria where DATE1 = date(subdate(now()," + day + ")) and service='RiaUninor'";
				ResultSet localResultSet6 = stmt_destination.executeQuery(str6);
				if (localResultSet6.next())
				{
					i = localResultSet6.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet6.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_ria where DATE1 = date(subdate(now()," + day + ")) and service='RiaUninor'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNIM RIYA DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_ria " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNIM RIYA DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void MTVBulkLoad(String filepath)
	{
		try
		{
	            String str7 = "select count(1) as CNT from tbl_billing_mtv where DATE1 = date(subdate(now()," + day + ")) and service='MTVUninor'";
				ResultSet localResultSet7 = stmt_destination.executeQuery(str7);
				if (localResultSet7.next())
				{
					i = localResultSet7.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet7.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mtv where DATE1 = date(subdate(now()," + day + ")) and service='MTVUninor'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR MTV DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mtv " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR MTV DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void CRIBulkLoad(String filepath)
	{
		try
		{
	            String str8 = "select count(1) as CNT from tbl_billing_cri_Uninor where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet8 = stmt_destination.executeQuery(str8);
				if (localResultSet8.next())
				{
					i = localResultSet8.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet8.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_cri_Uninor where DATE1 = date(subdate(now()," + day + ")) ");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR CRI DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_cri_Uninor " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR CRI DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void ComedyBulkLoad(String filepath)
	{
		try
		{
	            String str9 = "select count(1) as CNT from tbl_billing_comedy where DATE1 = date(subdate(now()," + day + ")) and service='UninorComedy'";
				ResultSet localResultSet9 = stmt_destination.executeQuery(str9);
				if (localResultSet9.next())
				{
					i = localResultSet9.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet9.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_comedy where DATE1 = date(subdate(now()," + day + ")) and service='UninorComedy' ");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR COMEDY DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_comedy " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR COMEDY DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void ContestBulkLoad(String filepath)
	{
		try
		{
	            String str10 = "select count(1) as CNT from tbl_billing_contest_uninor where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet10 = stmt_destination.executeQuery(str10);
				if (localResultSet10.next())
				{
					i = localResultSet10.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet10.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_contest_uninor where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********UNINOR CONTEST DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_contest_uninor " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********UNINOR CONTEST DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void run()
	{
		try{
				String FILEDATE = "";
				String query_date = "select date_format(adddate(now(),-"+day+"),'%Y-%m-%d') as 'FILEDATE'";

				ResultSet Rsdate = stmt_destination.executeQuery(query_date);
				while(Rsdate.next())
				{
						FILEDATE = Rsdate.getString("FILEDATE");
						System.out.println("Current date is "+FILEDATE);
				}
				//JADBulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_JAD_"+ FILEDATE + ".txt");
			//	BulkLoad54646("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_54646_"+ FILEDATE + ".txt");
				RTBulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_RT_"+ FILEDATE + ".txt");
				/*AABulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_AA_"+ FILEDATE + ".txt");
				REDFMBulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_RedFM_"+ FILEDATE + ".txt");
				RIYABulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_RIA_"+ FILEDATE + ".txt");
				MTVBulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_MTV_"+ FILEDATE + ".txt");
				CRIBulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_CRI_"+ FILEDATE + ".txt");
				ComedyBulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_Comedy_"+ FILEDATE + ".txt");
				ContestBulkLoad("/home/MISDATA_TEST/BILLINGDATA/UNIM/Uninor_Contest_"+ FILEDATE + ".txt");
				*/
				System.out.println("************************INSERTION DONE CLOSING CONNECTIONS**********************************");
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
							stmt_destination.close();
						if (con_destination != null)
							con_destination.close();
					}
					catch (Exception e){}
			  }

			}
			public static void main(String arg[])
			{
				if(arg.length>0)
					day = Integer.parseInt(arg[0]);
				else
					day = 1;
				Uninor_Bulkload_Process54646 c = new Uninor_Bulkload_Process54646();
				c.start();

			}

		}
