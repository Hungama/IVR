import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.sql.*;


	public class Indicom_Bulkload_Process extends Thread
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

	public Indicom_Bulkload_Process()
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
	public void EndlessBulkLoad(String filepath)
	{
		try
		{
				String str1 = "select count(1) as CNT from tbl_billing_mu_tatadocomocdma where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				  if (localResultSet1.next())
				  {
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mu_tatadocomocdma where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********INDICOM ENDLESS DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mu_tatadocomocdma " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********INDICOM ENDLESS DATA INSERTED AT DESTINATION***********");
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
	            String str2 = "select count(1) as CNT from tbl_billing_54646_tatadocomocdma where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				  if (localResultSet2.next())
				  {
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_tatadocomocdma where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********INDICOM 54646 DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_54646_tatadocomocdma " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********INDICOM 54646 DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void FMJBulkLoad(String filepath)
	{
		try
		{
	            String str3 = "select count(1) as CNT from tbl_billing_fmj_tatadocomocdma where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet3 = stmt_destination.executeQuery(str3);
				if (localResultSet3.next())
				{
					i = localResultSet3.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet3.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_fmj_tatadocomocdma where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********DOCOMO FMJ DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_fmj_tatadocomocdma " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********DOCOMO FMJ DATA INSERTED AT DESTINATION***********");
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
	            String str4 = "select count(1) as CNT from tbl_billing_redfm where DATE1 = date(subdate(now()," + day + ")) and service='RedFMTataDoCoMocdma'";
				ResultSet localResultSet4 = stmt_destination.executeQuery(str4);
				if (localResultSet4.next())
				{
					i = localResultSet4.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet4.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_redfm where DATE1 = date(subdate(now()," + day + ")) and service='RedFMTataDoCoMocdma'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********INDICOM REDFM DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_redfm " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********INDICOM REDFM DATA INSERTED AT DESTINATION***********");
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
	            String str5 = "select count(1) as CNT from tbl_billing_ria where DATE1 = date(subdate(now()," + day + ")) and service='RIATataDoCoMocdma'";
				ResultSet localResultSet5 = stmt_destination.executeQuery(str5);
				if (localResultSet5.next())
				{
					i = localResultSet5.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet5.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_ria where DATE1 = date(subdate(now()," + day + ")) and service='RIATataDoCoMocdma'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********INDICOM RIYA DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_ria " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********INDICOM RIYA DATA INSERTED AT DESTINATION***********");
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
	            String str6 = "select count(1) as CNT from tbl_billing_mtv where DATE1 = date(subdate(now()," + day + ")) and service='MTVTataDoCoMocdma'";
				ResultSet localResultSet6 = stmt_destination.executeQuery(str6);
				if (localResultSet6.next())
				{
					i = localResultSet6.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet6.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mtv where DATE1 = date(subdate(now()," + day + ")) and service='MTVTataDoCoMocdma'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********INDICOM RIYA DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mtv " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********INDICOM RIYA DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void MNDBulkLoad(String filepath)
	{
		try
		{
	            String str7 = "select count(1) as CNT from tbl_billing_mnd_tatadocomocdma where DATE1 = date(subdate(now(),"+day+"))";
				ResultSet localResultSet7 = stmt_destination.executeQuery(str7);
				if (localResultSet7.next())
				{
					i = localResultSet7.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet7.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mnd_tatadocomocdma where DATE1 = date(subdate(now(),"+day+")) ");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********DOCOMO MND DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mnd_tatadocomocdma " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********DOCOMO MND DATA INSERTED AT DESTINATION***********");
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
				EndlessBulkLoad("/home/MISDATA_TEST/BILLINGDATA/INDICOM/Indicom_Endless_"+ FILEDATE + ".txt");
				BulkLoad54646("/home/MISDATA_TEST/BILLINGDATA/INDICOM/Indicom_54646_"+ FILEDATE + ".txt");
				FMJBulkLoad("/home/MISDATA_TEST/BILLINGDATA/INDICOM/Indicom_Fmj_"+ FILEDATE + ".txt");
				REDFMBulkLoad("/home/MISDATA_TEST/BILLINGDATA/INDICOM/Indicom_RedFm_"+ FILEDATE + ".txt");
				RIYABulkLoad("/home/MISDATA_TEST/BILLINGDATA/INDICOM/Indicom_Riya_"+ FILEDATE + ".txt");
				MTVBulkLoad("/home/MISDATA_TEST/BILLINGDATA/INDICOM/Indicom_MTV_"+ FILEDATE + ".txt");
				MNDBulkLoad("/home/MISDATA_TEST/BILLINGDATA/INDICOM/Indicom_MND_"+ FILEDATE + ".txt");

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
				Indicom_Bulkload_Process c = new Indicom_Bulkload_Process();
				c.start();

			}

		}
