import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.sql.*;


	public class Airtel_Bulkload_ProcessMND extends Thread
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

			sIP="10.2.73.160";
			sDSN="master_db";
			sUSR="billing";
			sPWD="billing";

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

	public Airtel_Bulkload_ProcessMND()
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
	/*public void Vh1BulkLoad(String filepath)
	{
		try
		{
				String str1 = "select count(1) as CNT from tbl_billing_vh1 nolock where date(Date) = date(subdate(now(),"+day+")) and service='VH1Airtel'";
				ResultSet localResultSet1 = stmt_destination.executeQuery(str1);
				if (localResultSet1.next())
				{
					i = localResultSet1.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet1.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_vh1 where date(Date) = date(subdate(now(),"+day+")) and service='VH1Airtel'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL VH1 DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_vh1 " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL VH1 DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void GudLifeBulkLoad(String filepath)
	{
		try
		{
				String str2 = "select count(1) as CNT from tbl_billing_gl_airtel nolock where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet2 = stmt_destination.executeQuery(str2);
				if (localResultSet2.next())
				{
					i = localResultSet2.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet2.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_gl_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL GOOD LIFE DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_gl_airtel " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL GOOD LIFE DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
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
				String str3 = "select count(1) as CNT from tbl_billing_54646_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet3 = stmt_destination.executeQuery(str3);
				if (localResultSet3.next())
				{
					i = localResultSet3.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet3.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_54646_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL 54646 LIFE DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_54646_airtel " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL 5466 LIFE DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void RIABulkLoad(String filepath)
	{
		try
		{
	            String str4 = "select count(1) as CNT from tbl_billing_ria_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet4 = stmt_destination.executeQuery(str4);
				if (localResultSet4.next())
				{
					i = localResultSet4.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet4.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_ria_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL RIYA LIFE DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_ria_airtel " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL RIYA LIFE DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
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
				String str5 = "select count(1) as CNT from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVAirtel'";
				ResultSet localResultSet5 = stmt_destination.executeQuery(str5);
				if (localResultSet5.next())
				{
					i = localResultSet5.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet5.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mtv where date(Date) = date(subdate(now()," + day + ")) and service='MTVAirtel'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL MTV DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mtv " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL MTV DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void PDBulkLoad(String filepath)
	{
		try
		{
				String str6 = "select count(1) as CNT from tbl_billing_pd_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet6 = stmt_destination.executeQuery(str6);
				if (localResultSet6.next())
				{
					i = localResultSet6.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet6.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_pd_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL PD DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_pd_airtel " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL PD DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}*/
	public void MNDBulkLoad(String filepath)
	{
		try
		{
	            String str7 = "select count(1) as CNT from tbl_billing_mnd_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet7 = stmt_destination.executeQuery(str7);
				if (localResultSet7.next())
				{
					i = localResultSet7.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet7.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mnd_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL MND DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mnd_airtel " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL MND DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	/*public void ComedyBulkLoad(String filepath)
	{
		try
		{
	            String str8 = "select count(1) as CNT from tbl_billing_comedy where date(Date) = date(subdate(now()," + day + ")) and service in('AirtelComedy','AirtelRegKK','AirtelRegTN','AirtelRegKR')";
				ResultSet localResultSet8 = stmt_destination.executeQuery(str8);
				if (localResultSet8.next())
				{
					i = localResultSet8.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet8.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_comedy where date(Date) = date(subdate(now()," + day + ")) and service in('AirtelComedy','AirtelRegKK','AirtelRegTN','AirtelRegKR')");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL Comedy DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_comedy " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL Comedy DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void DevoBulkLoad(String filepath)
	{
		try
		{
				String str9 = "select count(1) as CNT from tbl_billing_devo_airtel where date(Date) = date(subdate(now()," + day + "))";
				ResultSet localResultSet9 = stmt_destination.executeQuery(str9);
				if (localResultSet9.next())
				{
					i = localResultSet9.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet9.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_devo_airtel where date(Date) = date(subdate(now()," + day + "))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL DEVO DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_devo_airtel " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL DEVO DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void EnglishBulkLoad(String filepath)
	{
		try
		{
	            String str10 = "select count(1) as CNT from tbl_billing_se_airtel where date(Date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet10 = stmt_destination.executeQuery(str10);
				if(localResultSet10.next())
				{
					i = localResultSet10.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet10.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_se_airtel where date(Date) = date(subdate(now(),"+day+"))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL ENGLISH DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_se_airtel " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL ENGLISH DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
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
	            String str11 = "select count(1) as CNT from tbl_billing_mu_airtel where date(Date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet11 = stmt_destination.executeQuery(str11);
				if(localResultSet11.next())
				{
					i = localResultSet11.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet11.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mu_airtel where date(Date) = date(subdate(now(),"+day+"))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL MU DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mu_airtel " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL MU DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void PKBulkLoad(String filepath)
	{
		try
		{
	            String str12 = "select count(1) as CNT from tbl_billing_pk where date(Date) = date(subdate(now(),"+day+")) and service='AirtelPK'";
				ResultSet localResultSet12 = stmt_destination.executeQuery(str12);
				if(localResultSet12.next())
				{
					i = localResultSet12.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet12.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_pk where date(Date) = date(subdate(now(),"+day+")) and service='AirtelPK'");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL PK DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_pk " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL PK DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}
	public void MNDKKBulkLoad(String filepath)
	{
		try
		{
	     		String str16 = "select count(1) as CNT from tbl_billing_mnd_airtelkk where date(Date) = date(subdate(now(),"+day+"))";
				ResultSet localResultSet16 = stmt_destination.executeQuery(str16);
				if(localResultSet16.next())
				{

					i = localResultSet16.getInt("CNT");
					System.out.println("ROW COUNT IS= " + i);
					localResultSet16.close();
					if (i >= 1)
					{
					  System.out.println("****Deleting the records****");
					  stmt_destination.executeUpdate("delete from tbl_billing_mnd_airtelkk where date(Date) = date(subdate(now(),"+day+"))");
					}
					else
					{
					   System.out.println("****No DB Records to Delete****");
					}
				}
				System.out.println("***********AIRTEL MNDKK DATA INSERTING AT DESTINATION***********");
				Date Current_Date = new Date();
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
						String esquel = "LOAD DATA LOCAL INFILE '" + filepath +
												"' INTO TABLE tbl_billing_mnd_airtelkk " +
												" FIELDS TERMINATED BY \'#\'" +
												" LINES TERMINATED BY \'\\n\'";
						stmt_destination.executeUpdate(esquel);
				System.out.println("***********AIRTEL MNDKK DATA INSERTED AT DESTINATION***********");
				System.out.printf(String.format("Current Date/Time : %tc", Current_Date ));
		}
		catch(Exception e)
			{
				System.out.println(e);
			}
	}*/
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
				/*Vh1BulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/VH1_Airtel_"+ FILEDATE + ".txt");
				GudLifeBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/GUDLIFE_Airtel_"+ FILEDATE + ".txt");
				BulkLoad54646("/home/MISDATA_TEST/BILLINGDATA/Airtel/54646_Airtel_"+ FILEDATE + ".txt");
				RIABulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/RIA_Airtel_"+ FILEDATE + ".txt");
				MTVBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/MTV_Airtel_"+ FILEDATE + ".txt");
				PDBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/PD_Airtel_"+ FILEDATE + ".txt");*/
				MNDBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/MND_Airtel_"+ FILEDATE + ".txt");
				/*ComedyBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/COMEDY_Airtel_"+ FILEDATE + ".txt");
				DevoBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/DEVO_Airtel_"+ FILEDATE + ".txt");
				EnglishBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/ENGLISH_Airtel_"+ FILEDATE + ".txt");
				MUBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/EU_Airtel_"+ FILEDATE + ".txt");
				PKBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/PK_Airtel_"+ FILEDATE + ".txt");
				MNDKKBulkLoad("/home/MISDATA_TEST/BILLINGDATA/Airtel/MNDKK_Airtel_"+ FILEDATE + ".txt");*/

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
			public static void main(String arg[])
			{
				if(arg.length>0)
					day = Integer.parseInt(arg[0]);
				else
					day = 1;
				Airtel_Bulkload_ProcessMND c = new Airtel_Bulkload_ProcessMND();
				c.start();

			}

		}
