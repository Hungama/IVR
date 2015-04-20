import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.sql.*;


	public class Voda_Bulkload_ProcessRU extends Thread
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

			sIP="10.43.248.137";
			sDSN="master_db";
			sUSR="ivr";
			sPWD="ivr";

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

	public Voda_Bulkload_ProcessRU()
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
	public void VH1BulkLoad(String filepath)
	{
		try
		{
				String str1 = "select count(1) as CNT from tbl_billing_vh1 nolock where DATE1 = date(subdate(now()," + day + ")) and service='VH1Vodafone'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				  if (localResultSet1.next())
				  {
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records for Vh1****");
					  stmt_destination.executeUpdate("delete from tbl_billing_vh1 where DATE1 = date(subdate(now()," + day + ")) and service='VH1Vodafone'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********VODA VH1 DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_vh1 " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********VODA VH1 DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date1 ));
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
	            String str2 = "select count(1) as CNT from tbl_billing_54646_vodafone nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				  if (localResultSet2.next())
				  {
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_vodafone where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********VODA 54646 DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_54646_vodafone " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********VODA 54646 DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date1 ));
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
	            String str3 = "select count(1) as CNT from tbl_billing_redfm nolock where DATE1 = date(subdate(now()," + day + ")) and service='RedFMVodafone'";
				ResultSet localResultSet3 = stmt_destination.executeQuery(str3);
				if (localResultSet3.next())
				{
					i = localResultSet3.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet3.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_redfm where DATE1 = date(subdate(now()," + day + ")) and service='RedFMVodafone'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********VODA REDFM DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_redfm " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********VODA REDFM DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
public void MUBulkLoad(String filepath)
	{
		try
		{
	            String str2 = "select count(1) as CNT from tbl_billing_mu_vodafone nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				  if (localResultSet2.next())
				  {
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mu_vodafone where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********VODA MU DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mu_vodafone " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********VODA MU DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date1 ));
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

				ResultSet Rsdate = stmt_source.executeQuery(query_date);
				while(Rsdate.next())
				{
						FILEDATE = Rsdate.getString("FILEDATE");
						System.out.println("Current date is "+FILEDATE);
				}
				//VH1BulkLoad("/home/MISDATA_TEST/BILLINGDATA/VODA/VODA_VH1_"+ FILEDATE + ".txt");
				//BulkLoad54646("/home/MISDATA_TEST/BILLINGDATA/VODA/VODA_54646_"+ FILEDATE + ".txt");
				//REDFMBulkLoad("/home/MISDATA_TEST/BILLINGDATA/VODA/VODA_REDFM_"+ FILEDATE + ".txt");
				MUBulkLoad("/home/MISDATA_TEST/BILLINGDATA/VODA/VODA_MU_"+ FILEDATE + ".txt");

				System.out.println("************************UPDATING THE DB LOGS**********************************");

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
							stmt_source.close();
						if (con_source != null)
							con_source.close();
					}
					catch(Exception e) {}

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
				Voda_Bulkload_ProcessRU c = new Voda_Bulkload_ProcessRU();
				c.start();

			}

		}
