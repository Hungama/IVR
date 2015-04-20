import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.sql.*;


	public class MTS_Bulkload_Process extends Thread
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

	public MTS_Bulkload_Process()
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
	public void MUBulkLoad(String filepath)
	{
		try
		{
				String str1 = "select count(1) as CNT from tbl_billing_mu_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records of the same date****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mu_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS MU DATA INSERTION START***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mu_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS MU DATA INSERTED AT DESTINATION***********");
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
	            String str2 = "select count(1) as CNT from tbl_billing_54646_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				if (localResultSet2.next())
				{
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS 54646 DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_54646_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS 54646 DATA INSERTED AT DESTINATION***********");
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
	            String str3 = "select count(1) as CNT from tbl_billing_mnd_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet3 = stmt_destination.executeQuery(str3);
				if (localResultSet3.next())
				{
					i = localResultSet3.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet3.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mnd_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS MND INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mnd_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS MND DATA INSERTED AT DESTINATION***********");
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
	            String str6 = "select count(1) as CNT from tbl_billing_fmj_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet6 = stmt_destination.executeQuery(str6);
				if (localResultSet6.next())
				{
					i = localResultSet6.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet6.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_fmj_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS FMJ INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_fmj_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS FMJ DATA INSERTED AT DESTINATION***********");
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
	            String str5 = "select count(1) as CNT from tbl_billing_redfm nolock where DATE1 = date(subdate(now()," + day + ")) and service='RedFMMTS'";
				ResultSet localResultSet5 = stmt_destination.executeQuery(str5);
				if (localResultSet5.next())
				{
					i = localResultSet5.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet5.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_redfm where DATE1 = date(subdate(now()," + day + ")) and service='RedFMMTS'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS REDFM DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_redfm " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS REDFM DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void VABulkLoad(String filepath)
	{
		try
		{
	            String str8 = "select count(1) as CNT from tbl_billing_vs_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet8 = stmt_destination.executeQuery(str8);
				if (localResultSet8.next())
				{
					i = localResultSet8.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet8.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_vs_mts where DATE1 = date(subdate(now()," + day + ")) ");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS VA DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_vs_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS VA DATA INSERTED AT DESTINATION***********");
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
	            String str7 = "select count(1) as CNT from tbl_billing_mtv nolock where DATE1 = date(subdate(now()," + day + ")) and service='MTVMTS'";
				ResultSet localResultSet7 = stmt_destination.executeQuery(str7);
				if (localResultSet7.next())
				{
					i = localResultSet7.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet7.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mtv where DATE1 = date(subdate(now()," + day + ")) and service='MTVMTS'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS MTV DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mtv " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS MTV DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void DEVOBulkLoad(String filepath)
	{
		try
		{
	            String str4 = "select count(1) as CNT from tbl_billing_devo_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet4 = stmt_destination.executeQuery(str4);
				if (localResultSet4.next())
				{
					i = localResultSet4.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet4.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_devo_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS DEVO DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_devo_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS DEVO DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void ACBulkLoad(String filepath)
	{
		try
		{
				String str15 = "select count(1) as CNT from tbl_billing_ac_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet15 = stmt_destination.executeQuery(str15);
				if (localResultSet15.next())
				{
					i = localResultSet15.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet15.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records of the same date****");
					  stmt_destination.executeUpdate("delete from tbl_billing_ac_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS AC DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_ac_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS AC DATA INSERTED AT DESTINATION***********");
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
	           	String str22 = "select count(1) as CNT from tbl_billing_contest_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet22 = stmt_destination.executeQuery(str22);
				if (localResultSet22.next())
				{
					i = localResultSet22.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet22.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_contest_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS CONTEST DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_contest_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS CONTEST DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void JokesBulkLoad(String filepath)
	{
		try
		{
	           	String str23 = "select count(1) as CNT from tbl_billing_comedy   nolock where DATE1 = date(subdate(now()," + day + ")) and service='MTSJokes'";
				ResultSet localResultSet23 = stmt_destination.executeQuery(str23);
				if (localResultSet23.next())
				{
					i = localResultSet23.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet23.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_comedy where DATE1 = date(subdate(now()," + day + ")) and service='MTSJokes'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS JOKES DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_comedy " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS JOKES DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void RegBulkLoad(String filepath)
	{
		try
		{
	           	String str24 = "select count(1) as CNT from tbl_billing_reg_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet24 = stmt_destination.executeQuery(str24);
				if (localResultSet24.next())
				{
					i = localResultSet24.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet24.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_reg_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS REGIONAL DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_reg_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS REGIONAL DATA INSERTED AT DESTINATION***********");
				Date Current_Date1 = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date1 ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void CriBulkLoad(String filepath)
	{
		try
		{
	           	String str24 = "select count(1) as CNT from tbl_billing_cri_mts nolock where DATE1 = date(subdate(now()," + day + "))";
				ResultSet localResultSet24 = stmt_destination.executeQuery(str24);
				if (localResultSet24.next())
				{
					i = localResultSet24.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet24.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_cri_mts where DATE1 = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********MTS CRICKET DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.println(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_cri_mts " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********MTS CRICKET DATA INSERTED AT DESTINATION***********");
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
				MUBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_MU_"+ FILEDATE + ".txt");
				BulkLoad54646("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_54646_"+ FILEDATE + ".txt");
				MNDBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_MND_"+ FILEDATE + ".txt");
				REDFMBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_REDFM_"+ FILEDATE + ".txt");
				FMJBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_FMJ_"+ FILEDATE + ".txt");
				VABulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_VA_"+ FILEDATE + ".txt");
				MTVBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_MTV_"+ FILEDATE + ".txt");
				DEVOBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_DEVO_"+ FILEDATE + ".txt");
				ACBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_AC_"+ FILEDATE + ".txt");
				ContestBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_Contest_"+ FILEDATE + ".txt");
				JokesBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_Jokes_"+ FILEDATE + ".txt");
				RegBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_Regional_"+ FILEDATE + ".txt");
				CriBulkLoad("/home/MISDATA_TEST/BILLINGDATA/MTS/MTS_Cricket_"+ FILEDATE + ".txt");

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
				MTS_Bulkload_Process c = new MTS_Bulkload_Process();
				c.start();

			}

		}
