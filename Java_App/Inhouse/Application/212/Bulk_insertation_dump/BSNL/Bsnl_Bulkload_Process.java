import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.sql.*;


	public class Bsnl_Bulkload_Process extends Thread
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

			System.out.println("**DESTINATION IP is  ["+dIP+"] **  DSN is ["+dDSN+"] Usr is ["+dUSR+"] Pwd is ["+dPWD+"]\t**");
			System.out.println("**********************************************************");

		}
		catch(Exception e)
		{
			System.out.println("Exception while reading DB.cfg");
			e.printStackTrace();

		}
	}

	public Bsnl_Bulkload_Process()
	{
		try
		{
			readDBCONFIG();
			System.out.println("Initiallizing DB");
			Class.forName("com.mysql.jdbc.Driver");
			con_destination=DriverManager.getConnection("jdbc:mysql://"+dIP+"/"+dDSN, dUSR, dPWD);
			System.out.println("Database Connection established!");
			stmt_destination = con_destination.createStatement();
			System.out.println("DB UP");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void Hun54646BulkLoad(String filepath)
	{
		try
		{
				String str1 = "select count(1) as CNT from tbl_billing_54646_bsnl nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				  if (localResultSet1.next())
				  {
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_bsnl where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********BSNL 54646 BILLING DATA INSERTION START***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_54646_bsnl " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********BSNL 54646 BILLING DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void Hun54646SouthBulkLoad(String filepath)
	{
		try
		{
	            System.out.println("***********BSNL SOUTH BILLING DATA INSERTION START***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_54646_bsnl " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********BSNL SOUTH BILLING DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void Hun54646WestBulkLoad(String filepath)
	{
		try
		{
	            System.out.println("***********BSNL WEST BILLING DATA INSERTION START***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_54646_bsnl " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********BSNL WEST BILLING DATA INSERTED AT DESTINATION***********");
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
				Hun54646BulkLoad("/home/MISDATA_TEST/BILLINGDATA/BSNL/Bsnl_Chd_54646_"+ FILEDATE + ".txt");
				Hun54646SouthBulkLoad("/home/MISDATA_TEST/BILLINGDATA/BSNL/SOUTH/Bsnl_south_54646_"+ FILEDATE + ".txt");
				Hun54646WestBulkLoad("/home/MISDATA_TEST/BILLINGDATA/BSNL/WEST/Bsnl_west_54646_"+ FILEDATE + ".txt");

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
				Bsnl_Bulkload_Process c = new Bsnl_Bulkload_Process();
				c.start();

			}

		}
