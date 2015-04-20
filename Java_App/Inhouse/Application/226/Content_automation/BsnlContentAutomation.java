//package airtelContentlog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BsnlContentAutomation extends Thread {
	public static Connection con_destination = null;
	public static Statement stmt_destination, stmt_del;
	public String sIP = null, sDSN = null, sUSR = null, sPWD = null;
	public String dIP = null, dDSN = null, dUSR = null, dPWD = null;
	public static CallableStatement cstmt = null;
	public static int day = 0;
	public String FILEDATE = "";

	public void readDBCONFIG() {
		try {
			System.out
					.println("**********************************************************");
			System.out
					.println("**     Thread Started With The Following Configuration  **");
			System.out
					.println("**              File to be Read is DB.CFG          **");

			dIP = "192.168.100.218";
			dDSN = "misdata";
			dUSR = "billing";
			dPWD = "billing";

			System.out.println("**DESTINATION IP is  [" + dIP
					+ "] **  DSN is [" + dDSN + "] Usr is [" + dUSR
					+ "] Pwd is [" + dPWD + "]\t**");
			System.out
					.println("**********************************************************");

		} catch (Exception e) {
			System.out.println("Exception while reading DB.cfg");
			e.printStackTrace();

		}
	}

	public BsnlContentAutomation() {
		try {
			readDBCONFIG();
			System.out.println("Initiallizing DB");
			Class.forName("com.mysql.jdbc.Driver");
			con_destination = DriverManager.getConnection("jdbc:mysql://" + dIP
					+ "/" + dDSN, dUSR, dPWD);
			System.out.println("Database Connection established!");
			stmt_destination = con_destination.createStatement();
			System.out.println("DB UP");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getCIRCLE(String CIR) {
		String c;
		if ("APD".equalsIgnoreCase(CIR))
			c = "Andhra Pradesh";
		else if ("ASM".equalsIgnoreCase(CIR))
			c = "Assam";
		else if ("BIH".equalsIgnoreCase(CIR))
			c = "Bihar";
		else if ("CHN".equalsIgnoreCase(CIR))
			c = "Chennai";
		else if ("DEL".equalsIgnoreCase(CIR))
			c = "Delhi";
		else if ("GUJ".equalsIgnoreCase(CIR))
			c = "Gujarat";
		else if ("HAY".equalsIgnoreCase(CIR))
			c = "Haryana";
		else if ("HPD".equalsIgnoreCase(CIR))
			c = "Himachal Pradesh";
		else if ("JNK".equalsIgnoreCase(CIR))
			c = "Jammu-Kashmir";
		else if ("KAR".equalsIgnoreCase(CIR))
			c = "Karnataka";
		else if ("KER".equalsIgnoreCase(CIR))
			c = "Kerala";
		else if ("KOL".equalsIgnoreCase(CIR))
			c = "Kolkata";
		else if ("MPD".equalsIgnoreCase(CIR))
			c = "Madhya Pradesh";
		else if ("MAH".equalsIgnoreCase(CIR))
			c = "Maharashtra";
		else if ("MUM".equalsIgnoreCase(CIR))
			c = "Mumbai";
		else if ("NES".equalsIgnoreCase(CIR))
			c = "NE";
		else if ("ORI".equalsIgnoreCase(CIR))
			c = "Orissa";
		else if ("PUB".equalsIgnoreCase(CIR))
			c = "Punjab";
		else if ("RAJ".equalsIgnoreCase(CIR))
			c = "Rajasthan";
		else if ("TNU".equalsIgnoreCase(CIR))
			c = "Tamil Nadu";
		else if ("UPE".equalsIgnoreCase(CIR))
			c = "UP EAST";
		else if ("UPW".equalsIgnoreCase(CIR))
			c = "UP WEST";
		else if ("WBL".equalsIgnoreCase(CIR))
			c = "WestBengal";
		else
			c = "others";
		return c;

	}
	public static String getCAT(String cat)
	{
		String l="";		
		String c="";
		int length=0;
		String lang ="";
		String category="";
		String FinalCat="";
		   		if(cat.length()>4 && (cat.startsWith("0") || cat.startsWith("1") || cat.startsWith("2")))
		   			cat = cat.substring(0,4);
		   		else
		   			lang = cat;	

				length = cat.length();
				if(length==4)
				{
					lang = cat.substring(0,2);
					category = cat.substring(2,4);
				}
				if(length==3)
				{
					lang = cat.substring(0,2);
					category = "00";
				}				
				if(length==2)
				{
					lang = cat.substring(0,2);
					category = "00";
				}

			if("01".equalsIgnoreCase(lang))
					l = "Hindi";
			else if ("02".equalsIgnoreCase(lang))
				l = "English";
			else if ("03".equalsIgnoreCase(lang))
				l = "Punjabi";
			else if ("04".equalsIgnoreCase(lang))
				l = "Bhojpuri";
			else if ("05".equalsIgnoreCase(lang))
				l = "Haryanavi";
			else if ("06".equalsIgnoreCase(lang))
				l = "Bengali";
			else if ("07".equalsIgnoreCase(lang))
				l = "Tamil";
			else if ("08".equalsIgnoreCase(lang))
				l = "Telugu";
			else if ("09".equalsIgnoreCase(lang))
				l = "Malayalam";
			else if ("10".equalsIgnoreCase(lang))
				l = "Kannada";
			else if ("11".equalsIgnoreCase(lang))
				l = "Marathi";
			else if ("12".equalsIgnoreCase(lang))
				l = "Gujarati";
			else if ("13".equalsIgnoreCase(lang))
				l = "Oriya";
			else if ("14".equalsIgnoreCase(lang))
				l = "Kashmiri";
			else if ("15".equalsIgnoreCase(lang))
				l = "Himachali";
			else if ("16".equalsIgnoreCase(lang))
				l = "Chhattisgarhi";
			else if ("17".equalsIgnoreCase(lang))
				l = "Assamese";
			else if ("18".equalsIgnoreCase(lang))
				l = "Rajasthani";
			else if ("00".equalsIgnoreCase(lang))
				l = "SplZone";		
			else
				l = lang;

			if ("01".equalsIgnoreCase(category))
				c = "Top20";
			else if ("02".equalsIgnoreCase(category))
				c = "NewArrivals";
			else if ("03".equalsIgnoreCase(category))
				c = "Popular";
			else if ("04".equalsIgnoreCase(category))
				c = "Evergreen";
			else if ("05".equalsIgnoreCase(category))
				c = "Sufiana";
			else if ("06".equalsIgnoreCase(category))
				c = "Ghazal";
			else if ("07".equalsIgnoreCase(category))
				c = "Indipop";
			else if ("08".equalsIgnoreCase(category))
				c = "Rock";
			else if ("09".equalsIgnoreCase(category))
				c = "Hiphop";
			else if ("10".equalsIgnoreCase(category))
				c = "Metal";
			else if ("11".equalsIgnoreCase(category))
				c = "Jazz";
			else if ("13".equalsIgnoreCase(category))
				c = "Devotional";
			else if ("14".equalsIgnoreCase(category))
				c = "Fusion";
			else if ("15".equalsIgnoreCase(category))
				c = "lovesong";
			else if ("16".equalsIgnoreCase(category))
				c = "hitpehit";
			else if ("32".equalsIgnoreCase(category))
				c = "RbtCut";
			else if ("00".equalsIgnoreCase(category))
				c = "";
		    else if (category.startsWith("WMF"))
                   c = "";
            else if (category.startsWith("Contest"))
                   c = "";				
			else
				c = category;
			FinalCat = l.concat(c);
			return FinalCat;
		}	
	public void Generate54646MIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f6 = new File(filepath);
		if (f6.exists()) {
			String line6 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_54646 where Date = date(subdate(now(),"
								+ day + ")) and Operator='BSNL'");
				BufferedReader br6 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line6 = br6.readLine()) != null) {
					line6 = line6.trim();
					String[] arrtemp = line6.split("#");
					String Operator = arrtemp[0].trim();
					String Circle = arrtemp[1].trim();
					Circle = getCIRCLE(Circle);
					MSISDN = arrtemp[2].trim();
					String DNIS = arrtemp[3].trim();
					DATE = arrtemp[4].trim();
					String TIME = arrtemp[5].trim();
					int i = 6;
					while (i < arrtemp.length) {
						String SongUniqueCode = arrtemp[i].trim();
						if (SongUniqueCode.contains("__")) {
							SongUniqueCode = SongUniqueCode.replace("__", "_");
						}
						SongUniqueCode = SongUniqueCode
								.substring(SongUniqueCode.indexOf("_") + 1);
						String Cat = arrtemp[i + 1].trim();
						Cat= getCAT(Cat);						
						String SubCat = arrtemp[i + 2].trim();
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = "30";
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line6);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line " + line6);
							} else {
								stmt_destination
										.executeUpdate("insert into tbl_contentusage_54646(Operator,MSISDN,Circle,Date,Duration,DNIS,Section,SubSection,Filename,Language,Mode,businesscategory)values('BSNL','"
												+ MSISDN
												+ "','"
												+ Circle
												+ "','"
												+ DATE
												+ "','"
												+ Duration
												+ "','"
												+ DNIS
												+ "','"
												+ Cat
												+ "','"
												+ SubCat
												+ "','"
												+ SongUniqueCode
												+ "','01','','"+Cat+"')");
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line6);
				e.printStackTrace();
			}
		}
	}
	public void Generate54646MISREST(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f6 = new File(filepath);
		if (f6.exists()) {
			String line6 = "";
			try {
				BufferedReader br6 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line6 = br6.readLine()) != null) {
					line6 = line6.trim();
					String[] arrtemp = line6.split("#");
					String Operator = arrtemp[0].trim();
					String Circle = arrtemp[1].trim();
					Circle = getCIRCLE(Circle);
					MSISDN = arrtemp[2].trim();
					String DNIS = arrtemp[3].trim();
					DATE = arrtemp[4].trim();
					String TIME = arrtemp[5].trim();
					int i = 6;
					while (i < arrtemp.length) {
						String SongUniqueCode = arrtemp[i].trim();
						if (SongUniqueCode.contains("__")) {
							SongUniqueCode = SongUniqueCode.replace("__", "_");
						}
						SongUniqueCode = SongUniqueCode
								.substring(SongUniqueCode.indexOf("_") + 1);
						String Cat = arrtemp[i + 1].trim();
						 Cat= getCAT(Cat);
						/*if (Cat.equalsIgnoreCase("01"))
							Cat = "Hindi";
						else if (Cat.equalsIgnoreCase("02"))
							Cat = "English";
						else if (Cat.equalsIgnoreCase("07"))
							Cat = "Tamil";
						else if (Cat.equalsIgnoreCase("08"))
							Cat = "Telugu";*/
						String SubCat = arrtemp[i + 2].trim();
						/*if (SubCat.equalsIgnoreCase("01"))
							SubCat = "Top20";
						else if (SubCat.equalsIgnoreCase("02"))
							SubCat = "NewRelease";
						else if (SubCat.equalsIgnoreCase("15"))
							SubCat = "romantics";
						else if (SubCat.equalsIgnoreCase("04"))
							SubCat = "classic";*/
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = "30";
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line6);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line " + line6);
							} else {
								stmt_destination
										.executeUpdate("insert into tbl_contentusage_54646(Operator,MSISDN,Circle,Date,Duration,DNIS,Section,SubSection,Filename,Language,Mode,businesscategory)values('BSNL','"
												+ MSISDN
												+ "','"
												+ Circle
												+ "','"
												+ DATE
												+ "','"
												+ Duration
												+ "','"
												+ DNIS
												+ "','"
												+ Cat
												+ "','"
												+ SubCat
												+ "','"
												+ SongUniqueCode
												+ "','01','','"+Cat+"')");
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line6);
				e.printStackTrace();
			}
		}
	}	
	public void run() {
		try {
			System.out.println("Inside Run");
			// String FILEDATE = "";
			String query_date = "select date_format(adddate(now(),-" + day
					+ "),'%Y%m%d') as 'FILEDATE'";
			ResultSet Rsdate = stmt_destination.executeQuery(query_date);
			while (Rsdate.next()) {
				FILEDATE = Rsdate.getString("FILEDATE");
			}
			//Generate54646MIS("/home/MISDATA_TEST/BSNL/NORTH/BSNL_54646_contentlog_"+ FILEDATE + ".txt"); 
			Generate54646MIS("/home/Hungama_call_logs_227/BSNL/CHA/BSNL_54646_contentlog_"+ FILEDATE + ".txt");
			Generate54646MISREST("/home/Hungama_call_logs_227/BSNL/PUN/BSNL_54646_contentlog_"+ FILEDATE + ".txt");
			Generate54646MISREST("/home/Hungama_call_logs_227/BSNL/MADU/BSNL_54646_contentlog_"+ FILEDATE + ".txt");


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt_destination.close();
				con_destination.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length > 0)
			day = Integer.parseInt(args[0]);
		else
			day = 1;
		BsnlContentAutomation c = new BsnlContentAutomation();
		c.start();

	}

}
