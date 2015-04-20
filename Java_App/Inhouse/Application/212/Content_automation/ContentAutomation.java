//package airtelContentlog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.print.DocFlavor.STRING;

public class ContentAutomation extends Thread {
	public static Connection con_source = null, con_destination = null;
	public static Statement stmt_source, stmt_destination, stmt_del;
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

			// ResourceBundle resource =
			// ResourceBundle.getBundle("config/cdrmoverDBCONFIG");
			sIP = "192.168.100.224";
			sDSN = "master_db";
			sUSR = "ivr";
			sPWD = "ivr";

			dIP = "192.168.100.218";
			dDSN = "misdata";
			dUSR = "billing";
			dPWD = "billing";

			System.out.println("**SOURCE IP is  [" + sIP + "] **  DSN is ["
					+ sDSN + "] Usr is [" + sUSR + "] Pwd is [" + sPWD
					+ "]\t**");
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

	public ContentAutomation() {
		try {
			readDBCONFIG();
			System.out.println("Initiallizing DB");
			Class.forName("com.mysql.jdbc.Driver");
			con_source = DriverManager.getConnection("jdbc:mysql://" + sIP
					+ "/" + sDSN, sUSR, sPWD);
			con_destination = DriverManager.getConnection("jdbc:mysql://" + dIP
					+ "/" + dDSN, dUSR, dPWD);
			System.out.println("Database Connection established!");
			stmt_source = con_source.createStatement();
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
                    if(length==6)
                    {
                            lang = cat.substring(0,2);
                            category = cat.substring(2,4);
                    }
                    if(length==5)
                    {
                            lang = cat.substring(0,2);
                            category = cat.substring(2,4);
                    }
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
				if("00".equalsIgnoreCase(lang))
					category = "00";

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
			else if ("28".equalsIgnoreCase(category))
				c = "Mashup";
			else if ("32".equalsIgnoreCase(category))
				c = "RbtCut";
			else if ("00".equalsIgnoreCase(category))
				c = "";
			else if (category.startsWith("WMF"))
				c = "";
			else if ("Contest".startsWith(category))
				c = "";
			else
				c = category;

			FinalCat = l.concat(c);
			return FinalCat;
		}
	public static String getRegCat(String cat) {
		String c;
		if ("REGCAT24".equalsIgnoreCase(cat) || "REGCAT12".equalsIgnoreCase(cat) || "REGCAT15".equalsIgnoreCase(cat) || "REGCAT24".equalsIgnoreCase(cat))
			c = "Masala Cafe";
		else if ("REGCAT5".equalsIgnoreCase(cat))
			c = "Lok Geet";
		else if ("REGCAT1".equalsIgnoreCase(cat))
			c = "Bangla Folk";
		else if ("REGCAT21".equalsIgnoreCase(cat))
			c = "Sound of Deserts";
		else if ("REGCAT7".equalsIgnoreCase(cat))
			c = "Comedy Songs";
		else if ("REGCAT9".equalsIgnoreCase(cat))
			c = "Blockbuster hits";
		else if ("REGCAT32".equalsIgnoreCase(cat) ||"REGCAT36".equalsIgnoreCase(cat)||"REGCAT38".equalsIgnoreCase(cat)|| "REGCAT34".equalsIgnoreCase(cat)||"REGCAT266".equalsIgnoreCase(cat)|| "REGCAT26".equalsIgnoreCase(cat)|| "REGCAT28".equalsIgnoreCase(cat)|| "REGCAT30".equalsIgnoreCase(cat))
			c = "Occasion/Festival/Personalities";
		else if ("REGCAT4".equalsIgnoreCase(cat))
			c = "Garba Hits";
		else if ("REGCAT19".equalsIgnoreCase(cat))
			c = "Desert Melodies";
		else if ("REGCAT23".equalsIgnoreCase(cat) || "REGCAT11".equalsIgnoreCase(cat) ||"REGCAT14".equalsIgnoreCase(cat))
			c = "Legends&Musician Paradise";
		else if ("REGCAT2".equalsIgnoreCase(cat))
			c = "Nazrul Geeti";
		else if ("REGCAT20".equalsIgnoreCase(cat) || "REGCAT22".equalsIgnoreCase(cat) || "REGCAT13".equalsIgnoreCase(cat) || "REGCAT10".equalsIgnoreCase(cat))
			c = "HotSizzling&NewArrivals";
		else if ("REGCAT8".equalsIgnoreCase(cat))
			c = "Lokgeet";
		else if ("REGCAT25".equalsIgnoreCase(cat) || "REGCAT27".equalsIgnoreCase(cat) || "REGCAT35".equalsIgnoreCase(cat) || "REGCAT37".equalsIgnoreCase(cat) || "REGCAT29".equalsIgnoreCase(cat) || "REGCAT255".equalsIgnoreCase(cat) || "REGCAT31".equalsIgnoreCase(cat) || "REGCAT33".equalsIgnoreCase(cat))
			c = "Culture & Heritage";
		else if ("REGCAT6".equalsIgnoreCase(cat))
			c = "Mehfil-E-Ghazal(Gujarati)";
		else if ("REGCAT3".equalsIgnoreCase(cat))
			c = "Rabindra Sangeet";
		else if ("HIN_del".equalsIgnoreCase(cat) || "TAM_TAMILNADU".equalsIgnoreCase(cat) || "GUJ_GUJARAT".equalsIgnoreCase(cat) || "KAN_KARNATAKA".equalsIgnoreCase(cat) || "MAL_KERALA".equalsIgnoreCase(cat) || "BEN_WESTBEN".equalsIgnoreCase(cat) || "RAJ_RAJASTHAN".equalsIgnoreCase(cat) || "TAM_TAMILNADU".equalsIgnoreCase(cat) || "HIN_UPW".equalsIgnoreCase(cat))
			c = "Recipe";
		else if ("REGCAT16".equalsIgnoreCase(cat))
			c = "Punjabi popular";
		else
			c = "others";
		return c;
	}
	public static String getDevoCat(String maincat)
	{
		String D="";
		if(maincat.startsWith("cat"))
			maincat = maincat.substring(3);
		//if(maincat.length()>4)
		//	maincat = maincat.substring(0,4);
					if("0131".equalsIgnoreCase(maincat))
						D = "ChristianHindiTheHolyBible";
					else if ("0132".equalsIgnoreCase(maincat))
						D = "ChristianHindiGospelsongs";
					else if ("0133".equalsIgnoreCase(maincat))
						D = "HinduHindiDharmikGranth";
					else if ("0134".equalsIgnoreCase(maincat))
						D = "HinduHindiMaryadaPurshottamShriRam";
					else if ("0135".equalsIgnoreCase(maincat))
						D = "HinduHindiMurlidharshriKrishna";
					else if ("0136".equalsIgnoreCase(maincat))
						D = "HinduHindiBhajanDeviMaa";
					else if ("0137".equalsIgnoreCase(maincat))
						D = "HinduHindiSankatmochanShriHanuman";
					else if ("0138".equalsIgnoreCase(maincat))
						D = "HinduHindiBhagwanBholeShankar";
					else if ("0139".equalsIgnoreCase(maincat))
						D = "HinduHindiBhajanMangalkarishriGanesh";
					else if ("0140".equalsIgnoreCase(maincat))
						D = "HinduHindiBhajanShridikeSaiBaba";
					else if ("0141".equalsIgnoreCase(maincat))
						D = "HinduHindiBhajanAnyaDeviDevta";
					else if ("0142".equalsIgnoreCase(maincat))
						D = "MuslimHindiAayaten";
					else if ("0143".equalsIgnoreCase(maincat))
						D = "MuslimHindiNaatShareif";
					else if ("0144".equalsIgnoreCase(maincat))
						D = "MuslimHindiDaroodSharief";
					else if ("0145".equalsIgnoreCase(maincat))
						D = "MuslimHindiDua";
					else if ("0146".equalsIgnoreCase(maincat))
						D = "MuslimHindiQuwwalies";
					else if ("0148".equalsIgnoreCase(maincat))
						D = "JainHindiBhaktamberStotra";
					else if ("0149".equalsIgnoreCase(maincat))
						D = "HinduHindiChalisaAnyaDeviDevta";
					else if ("0150".equalsIgnoreCase(maincat))
						D = "JainHindiAarti";
					else if ("0151".equalsIgnoreCase(maincat))
						D = "JainHindiBhaktiGeet";
					else if ("0152".equalsIgnoreCase(maincat))
						D = "BudhHindiVandana";
					else if ("0153".equalsIgnoreCase(maincat))
						D = "BudhHindiChants";
					else if ("0154".equalsIgnoreCase(maincat))
						D = "BudhHindiBhajan";
					else if ("0231".equalsIgnoreCase(maincat))
						D = "ChristianEnglishTheHolyBible";
					else if ("0233".equalsIgnoreCase(maincat))
						D = "ChristianEnglishHymns";
					else if ("0234".equalsIgnoreCase(maincat))
						D = "ChristianEnglishChristmasSongs";
					else if ("0331".equalsIgnoreCase(maincat))
						D = "SikhPunjabiHukamnama";
					else if ("0332".equalsIgnoreCase(maincat))
						D = "SikhPunjabiPaath";
					else if ("0333".equalsIgnoreCase(maincat))
						D = "SikhPunjabiMukhwak";
					else if ("0335".equalsIgnoreCase(maincat))
						D = "SikhPunjabiArdaas";
					else if ("0336".equalsIgnoreCase(maincat))
						D = "SikhPunjabiDharmikGeet";
					else if ("0337".equalsIgnoreCase(maincat))
						D = "SikhPunjabiShabadkeertan";
					else if ("0338".equalsIgnoreCase(maincat))
						D = "SikhPunjabiGurbani";
					else if ("0431".equalsIgnoreCase(maincat))
						D = "HinduBhojpuriDeviGeet";
					else if ("0432".equalsIgnoreCase(maincat))
						D = "HinduBhojpuriBhagwanBholeShankarKeBhajan";
					else if ("0433".equalsIgnoreCase(maincat))
						D = "HinduBhojpuriChhathPoojakeGeet";
					else if ("0434".equalsIgnoreCase(maincat))
						D = "HinduBhojpuriBhajanSangam";
					else if ("0631".equalsIgnoreCase(maincat))
						D = "HinduBengaliDebiMaagaan";
					else if ("0632".equalsIgnoreCase(maincat))
						D = "HinduBengaliShriKrishnaBhajan";
					else if ("0633".equalsIgnoreCase(maincat))
						D = "HinduBengaliShibaBhajan";
					else if ("0634".equalsIgnoreCase(maincat))
						D = "HinduBhojpuriBhajanSangam";
					else if ("0731".equalsIgnoreCase(maincat))
						D = "HinduTamilBalajiBhajan";
					else if ("0732".equalsIgnoreCase(maincat))
						D = "HinduTamilAyyappa";
					else if ("0733".equalsIgnoreCase(maincat))
						D = "HinduTamilMurugan";
					else if ("0734".equalsIgnoreCase(maincat))
						D = "HinduTamilBhajanSangam";
					else if ("0739".equalsIgnoreCase(maincat))
						D = "MuslimTamilAayaten-TamilTranslation";
					else if ("0831".equalsIgnoreCase(maincat))
						D = "HinduTeluguSuprabhatam";
					else if ("0832".equalsIgnoreCase(maincat))
						D = "HinduTeluguAyyappa";
					else if ("0833".equalsIgnoreCase(maincat))
						D = "HinduTeluguSaiBaba";
					else if ("0834".equalsIgnoreCase(maincat))
						D = "HinduTeluguHanuman";
					else if ("0835".equalsIgnoreCase(maincat))
						D = "HinduTeluguGanesh";
					else if ("0836".equalsIgnoreCase(maincat))
						D = "HinduTeluguKrishna";
					else if ("0837".equalsIgnoreCase(maincat))
						D = "HinduTeluguBhajanSangam";
					else if ("0931".equalsIgnoreCase(maincat))
						D = "MuslimMalayalamAayaten-MalayamTranslation";
					else if ("0932".equalsIgnoreCase(maincat))
						D = "ChristianMalayalamChristianDevotional";
					else if ("0935".equalsIgnoreCase(maincat))
						D = "HinduMalayalamKrishna";
					else if ("0936".equalsIgnoreCase(maincat))
						D = "HinduMalayalamDeviMaa";
					else if ("0937".equalsIgnoreCase(maincat))
						D = "HinduMalayalamGanesha";
					else if ("0938".equalsIgnoreCase(maincat))
						D = "HinduMalayalamShiva";
					else if ("0939".equalsIgnoreCase(maincat))
						D = "HinduMalayalamBhajanSangam";
					else if ("0940".equalsIgnoreCase(maincat))
						D = "MuslimMalayalamMapillaDevotional";
					else if ("1031".equalsIgnoreCase(maincat))
						D = "HinduKannadaSuprabhatam";
					else if ("1032".equalsIgnoreCase(maincat))
						D = "HinduKannadaHanuman";
					else if ("1033".equalsIgnoreCase(maincat))
						D = "HinduKannadaBhajan";

					else if ("1131".equalsIgnoreCase(maincat))
						D = "HinduMarathiSaiBhajan";
					else if ("1132".equalsIgnoreCase(maincat))
						D = "HinduMarathiGanpatiBhajan";
					else if ("1133".equalsIgnoreCase(maincat))
						D = "HinduMarathiBhaktiGeete";

					else if ("1231".equalsIgnoreCase(maincat))
						D = "HinduGujaratiShivbhajan";
					else if ("1232".equalsIgnoreCase(maincat))
						D = "HinduGujaratiKrishnaBhajan";
					else if ("1233".equalsIgnoreCase(maincat))
						D = "HinduGujaratiSwaminarayanKirtan";
					else if ("1234".equalsIgnoreCase(maincat))
						D = "HinduGujaratiBhajanSangam";

					else if ("1431".equalsIgnoreCase(maincat))
						D = "MuslimKashmiriNaatShareif";
					else if ("1432".equalsIgnoreCase(maincat))
						D = "MuslimKashmiriHamud";
					else if ("1433".equalsIgnoreCase(maincat))
						D = "MuslimKashmiriMankabhat";
					else if ("1434".equalsIgnoreCase(maincat))
						D = "MuslimKashmiriManajhaat";

					else if ("1631".equalsIgnoreCase(maincat))
						D = "HinduChhatisgarhiDeviGeet";
					else if ("1632".equalsIgnoreCase(maincat))
						D = "HinduChhatisgarhiBhajanSangam";

					else if ("1731".equalsIgnoreCase(maincat))
						D = "HinduAssameseBhajanSangam";
					else if ("1732".equalsIgnoreCase(maincat))
						D = "HinduAssameseShankardebBhajans";

					else if ("1831".equalsIgnoreCase(maincat))
						D = "HinduRajasthaniKhatuShyam";
					else if ("1832".equalsIgnoreCase(maincat))
						D = "HinduRajasthaniSalasarKeBalaji";
					else if ("1833".equalsIgnoreCase(maincat))
						D = "HinduRajasthaniSrinathji";
					else if ("1834".equalsIgnoreCase(maincat))
						D = "HinduRajasthaniBabaRamdev";
					else if ("1835".equalsIgnoreCase(maincat))
						D = "HinduRajasthaniGogaPeer";
					else if ("1836".equalsIgnoreCase(maincat))
						D = "HinduRajasthaniBhajanSangam";
				    else if (maincat.startsWith("myth_stories"))
		                   D = "myth_stories";
					else
						D=maincat;
					return D;
		}

	public void GenerateCinemaMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f19 = new File(filepath);
		if (f19.exists()) {
			String line19 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_ac_mts where Date = date(subdate(now(),"
								+ day + "))");
				BufferedReader br19 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line19 = br19.readLine()) != null) {
					line19 = line19.trim();
					// StringTokenizer st = new StringTokenizer(line19,"#");
					String arrtemp[] = line19.split("#");
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
						if (SongUniqueCode == ""
								|| SongUniqueCode.equalsIgnoreCase("")
								|| SongUniqueCode.isEmpty()
								|| SongUniqueCode == null) {
							System.out
									.println("Blank SongUniquecode " + line19);
						} else {

							if (SongUniqueCode.contains("__")) {
								// System.out.println("HERE"+SongUniqueCode);
								SongUniqueCode = SongUniqueCode.replace("__",
										"_");
								// System.out.println("HERE"+SongUniqueCode);
							}
							SongUniqueCode = SongUniqueCode
									.substring(SongUniqueCode.indexOf("_") + 1);
							String Cat = arrtemp[i + 1].trim();
							String SubCat = arrtemp[i + 2].trim();
							Cat = Cat.concat(SubCat);
							if(Cat.length()>4)
								Cat=Cat.substring(0,4);
							Cat= getCAT(Cat);
							String Duration = arrtemp[i + 3].trim();
							if (Integer.parseInt(Duration) < 0) {
								Duration = "0";
								// System.out.println("Duration"+Duration);
							}
							if (Duration.toLowerCase().contains("undef")
									|| Integer.parseInt(Duration) > 9000) {
								System.out
										.println("Duration error : " + line19);
							} else {
								if (SongUniqueCode.equalsIgnoreCase("01")
										|| SongUniqueCode.equalsIgnoreCase("")
										|| SongUniqueCode
												.equalsIgnoreCase("02")
										|| SongUniqueCode
												.equalsIgnoreCase("03")
										|| SongUniqueCode.toLowerCase()
												.contains("undef")) {
									System.out.println("Error line " + line19);
								} else {
									// System.out.println(SongUniqueCode+","+Duration+","+Circle+","+DATE+","+MSISDN+","+DNIS);
									try {
										stmt_destination
												.executeUpdate("insert into tbl_contentusage_ac_mts(SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
														+ SongUniqueCode
														+ "','"
														+ Duration
														+ "','"
														+ Circle
														+ "','"
														+ DATE
														+ "','"
														+ MSISDN
														+ "','"
														+ DNIS
														+ "','','"+Cat+"')");
									} catch (Exception e) {
										System.out.println("SQL Error : "
												+ e.getMessage() + " :  "
												+ line19);
										e.printStackTrace();
									}
								}
							}
						}
						i = i + 4;
					}

				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line19);
				e.printStackTrace();
			}
		}
	}

	public void GenerateDevoMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f19 = new File(filepath);
		if (f19.exists()) {
			String line19 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_devo_mts where Date = date(subdate(now(),"
								+ day + "))");
				BufferedReader br19 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line19 = br19.readLine()) != null) {
					line19 = line19.trim();
					if (line19.contains("##")) {
						System.out.println("ERROR IS" + line19);
					} else {

						// StringTokenizer st = new StringTokenizer(line19,"#");
						String arrtemp[] = line19.split("#");
						String Operator = arrtemp[0].trim();
						String Circle = arrtemp[1].trim();
						Circle = getCIRCLE(Circle);
						MSISDN = arrtemp[2].trim();
						String DNIS = arrtemp[3].trim();
						DATE = arrtemp[4].trim();
						String TIME = arrtemp[5].trim();
						int i = 6;
						while (i < arrtemp.length) {
							String SongUniqueCode="";
							String SongUniqueCode1 = arrtemp[i].trim();
							if (SongUniqueCode1.contains("__")) {
								// System.out.println("HERE"+SongUniqueCode);
								SongUniqueCode1 = SongUniqueCode1.replace("__",
										"_");
								// System.out.println("HERE"+SongUniqueCode);
							}
							//SongUniqueCode = SongUniqueCode.substring(SongUniqueCode.indexOf("_") + 1);
							SongUniqueCode = SongUniqueCode1.substring(0,SongUniqueCode1.indexOf("_"));
					      	if(SongUniqueCode.equals("115"))
					      		SongUniqueCode = SongUniqueCode1.substring(SongUniqueCode1.indexOf("_")+1,SongUniqueCode1.lastIndexOf("_"));
							else
								SongUniqueCode = SongUniqueCode1.substring(SongUniqueCode1.indexOf("_")+1);

							String Cat = arrtemp[i + 1].trim();
							Cat = getDevoCat(Cat);
							String SubCat = arrtemp[i + 2].trim();
							String Duration = arrtemp[i + 3].trim();
							if (Integer.parseInt(Duration) < 0) {
								Duration = "0";
								// System.out.println("Duration"+Duration);
							}
							if (Duration.toLowerCase().contains("undef")
									|| Integer.parseInt(Duration) > 9000) {
								System.out
										.println("Duration error : " + line19);
							} else {
								if (SongUniqueCode.startsWith("_"))
									SongUniqueCode = SongUniqueCode
											.substring(SongUniqueCode
													.indexOf("_") + 1);
								if (SongUniqueCode.equalsIgnoreCase("01")
										|| SongUniqueCode.equalsIgnoreCase("")
										|| SongUniqueCode
												.equalsIgnoreCase("02")
										|| SongUniqueCode
												.equalsIgnoreCase("03")
										|| SongUniqueCode.toLowerCase()
												.contains("undef")) {
									System.out.println("Error line " + line19);
								} else {
									// System.out.println(SongUniqueCode+","+Duration+","+Circle+","+DATE+","+MSISDN+","+DNIS);

									stmt_destination
											.executeUpdate("insert into tbl_contentusage_devo_mts(SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
													+ SongUniqueCode
													+ "','"
													+ Duration
													+ "','"
													+ Circle
													+ "','"
													+ DATE
													+ "','"
													+ MSISDN
													+ "','"
													+ DNIS
													+ "','','"+Cat+"')");
								}
							}
							i = i + 4;
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line19);
				e.printStackTrace();
			}
		}
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
								+ day + ")) and Operator='MTS'");
				BufferedReader br6 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line6 = br6.readLine()) != null) {
					line6 = line6.trim();
					String[] arrtemp = line6.split("#");
					// StringTokenizer st = new StringTokenizer(line6,"#");
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
						String Cat1= getCAT(Cat);
						String SubCat = arrtemp[i + 2].trim();
						if (SubCat.equalsIgnoreCase("01"))
							SubCat = "Top20";
						else if (SubCat.equalsIgnoreCase("02"))
							SubCat = "NewRelease";
						else if (SubCat.equalsIgnoreCase("15"))
							SubCat = "romantics";
						else if (SubCat.equalsIgnoreCase("04"))
							SubCat = "classic";
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
										.executeUpdate("insert into tbl_contentusage_54646(Operator,MSISDN,Circle,Date,Duration,DNIS,Section,SubSection,Filename,Language,Mode,businesscategory)values('MTS','"
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
												+ "','01','','"+Cat1+"')");
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

	public void GenerateMndMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f13 = new File(filepath);
		if (f13.exists()) {
			String line13 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_mnd_mts where Date = date(subdate(now(),"
								+ day + "))");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line13 = br.readLine()) != null) {
					line13 = line13.trim();
					String arrtemp[] = line13.split("#");
					String operator = arrtemp[0].trim();
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
						String SubCat = arrtemp[i + 2].trim();
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line13);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line " + line13);
							} else {
								stmt_destination
										.executeUpdate("insert into tbl_contentusage_mnd_mts (SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
												+ SongUniqueCode
												+ "','"
												+ Duration
												+ "','"
												+ Circle
												+ "','"
												+ DATE
												+ "','"
												+ MSISDN
												+ "','" + DNIS + "','','"+Cat+"')");
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line13);
				e.printStackTrace();
			}
		}
	}

	public void GenerateMtvMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f4 = new File(filepath);
		if (f4.exists()) {
			String line4 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_mtv where Date = date(subdate(now(),"
								+ day + ")) and Service='MTVMTS'");
				BufferedReader br4 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line4 = br4.readLine()) != null) {
					line4 = line4.trim();
					String arrtemp[] = line4.split("#");
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
						String SubCat = arrtemp[i + 2].trim();
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line4);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| Duration.equalsIgnoreCase("NaN")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line " + line4);
							} else {
								stmt_destination
										.executeUpdate("insert into tbl_contentusage_mtv (SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Service,Mode,businesscategory)values('"
												+ SongUniqueCode
												+ "','"
												+ Duration
												+ "','"
												+ Circle
												+ "','"
												+ DATE
												+ "','"
												+ MSISDN
												+ "','"
												+ DNIS
												+ "','MTVMTS','','"+Cat+"')");
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line4);
				e.printStackTrace();
			}
		}
	}

	public void GenerateMODMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f5 = new File(filepath);
		if (f5.exists()) {
			String line5 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_mu_mts where Date = date(subdate(now(),"
								+ day + "))");
				BufferedReader br5 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line5 = br5.readLine()) != null) {
					line5 = line5.trim();
					String arrtemp[] = line5.split("#");
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
						if(SongUniqueCode.equalsIgnoreCase("") || SongUniqueCode.equalsIgnoreCase(" "))
						{
							System.out.println("Category error"+SongUniqueCode);
						}
						String Cat = arrtemp[i + 1].trim();
						if (Cat.equalsIgnoreCase("") || Cat.equalsIgnoreCase(" ")){
							System.out.println("Category error"+Cat);
						}
						String SubCat = arrtemp[i + 2].trim();
						if((Cat.startsWith("Contest")) || (Cat.startsWith("WMF")))
						{
							System.out.println("Category is "+Cat);
							Cat= getCAT(Cat);
						}
						else
						{
							Cat = Cat.concat(SubCat);
							Cat= getCAT(Cat);
						}

						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line5);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.equalsIgnoreCase("NA")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| Duration.equalsIgnoreCase("NaN")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line " + line5);
							} else {
								// System.out.println("Error line " + line5);
								stmt_destination
										.executeUpdate("insert into tbl_contentusage_mu_mts (SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
												+ SongUniqueCode
												+ "','"
												+ Duration
												+ "','"
												+ Circle
												+ "','"
												+ DATE
												+ "','"
												+ MSISDN
												+ "','" + DNIS + "','','"+Cat+"')");
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line5);
				e.printStackTrace();
			}
		}
	}

	public void GenerateFMJMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f5 = new File(filepath);
		if (f5.exists()) {
			String line5 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_fmj_mts where Date = date(subdate(now(),"
								+ day + "))");
				BufferedReader br5 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line5 = br5.readLine()) != null) {
					line5 = line5.trim();
					String arrtemp[] = line5.split("#");
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
						String SubCat = arrtemp[i + 2].trim();
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line5);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| Duration.equalsIgnoreCase("NaN")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line " + line5);
							} else {
								// System.out.println("Error line " + line5);
								stmt_destination
										.executeUpdate("insert into tbl_contentusage_fmj_mts (SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
												+ SongUniqueCode
												+ "','"
												+ Duration
												+ "','"
												+ Circle
												+ "','"
												+ DATE
												+ "','"
												+ MSISDN
												+ "','" + DNIS + "','','"+Cat+"')");
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line5);
				e.printStackTrace();
			}
		}
	}

	public void GenerateREDFMMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f10 = new File(filepath);
		if (f10.exists()) {
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_redfm where Date = date(subdate(now(),"
								+ day + ")) and Service='RedFMMTS'");
				String line10 = "";
				BufferedReader br10 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line10 = br10.readLine()) != null) {
					line10 = line10.trim();
					String[] arrtemp = line10.split("#");
					String Operator = arrtemp[0].trim();
					String Circle = arrtemp[1].trim();
					Circle = getCIRCLE(Circle);
					MSISDN = arrtemp[2].trim();
					String DNIS = arrtemp[3].trim();
					DATE = arrtemp[4].trim();
					String TIME = arrtemp[5].trim();
					int i = 6;
					while (i < arrtemp.length) {
						String Station = arrtemp[i].trim();
						String SongUniqueCode = arrtemp[i + 1].trim();
						SongUniqueCode = SongUniqueCode.substring(0,
								SongUniqueCode.indexOf("."));
						SongUniqueCode = SongUniqueCode
								.substring(SongUniqueCode.indexOf("_") + 1);
						String Duration = arrtemp[i + 2].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
						}
						if (Integer.parseInt(Duration) > 60) {
							Duration = "59";
						}
						if (SongUniqueCode.toLowerCase().equalsIgnoreCase("0")
								|| SongUniqueCode.toLowerCase()
										.equalsIgnoreCase("01")
								|| SongUniqueCode.toLowerCase()
										.equalsIgnoreCase("02")
								|| SongUniqueCode.toLowerCase()
										.equalsIgnoreCase("03"))
							System.out.println("error Line is :" + line10);
						if (SongUniqueCode.toLowerCase().contains("undef")) {
						} else {
							// System.out.println(SongUniqueCode + "," + Station
							// + "," + Duration + "," + Circle + "," + DATE +
							// "," + MSISDN + "," + DNIS);
							String servicename = "";
							servicename = "RedFMMTS";
							if (Station.equalsIgnoreCase("Kolka"))
								Station = "KolKata";
							stmt_destination
									.executeUpdate("insert into tbl_contentusage_redfm (SongUniqueCode,Station,Duration,Circle,Date,MSISDN,DNIS,Service,Mode,businesscategory)values('"
											+ SongUniqueCode
											+ "','"
											+ Station
											+ "','"
											+ Duration
											+ "','"
											+ Circle
											+ "','"
											+ DATE
											+ "','"
											+ MSISDN
											+ "','"
											+ DNIS
											+ "','"
											+ servicename + "','','"+Station+"')");
						}
						i = i + 3;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void GenerateVAMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f5 = new File(filepath);
		if (f5.exists()) {
			String line5 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_va_mts where Date = date(subdate(now(),"
								+ day + ")) and mode='OBD'");
				BufferedReader br5 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line5 = br5.readLine()) != null) {
					line5 = line5.trim();
					String arrtemp[] = line5.split("#");
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
						// SongUniqueCode =
						// SongUniqueCode.substring(SongUniqueCode.indexOf("_")
						// + 1);

						String Cat = arrtemp[i + 1].trim();
						String SubCat = arrtemp[i + 2].trim();
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")) {
							System.out.println("Duration error : " + line5);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| Duration.equalsIgnoreCase("NaN")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line " + line5);
							} else {
								if (SongUniqueCode.contains(".wav")) {
									SongUniqueCode = SongUniqueCode.substring(
											0, SongUniqueCode.indexOf("."));
									// System.out.println("Error line " +
									// line5);
									stmt_destination
											.executeUpdate("insert into tbl_contentusage_va_mts (SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
													+ SongUniqueCode
													+ "','"
													+ Duration
													+ "','"
													+ Circle
													+ "','"
													+ DATE
													+ "','"
													+ MSISDN
													+ "','"
													+ DNIS
													+ "','OBD','"+Cat+"')");
								} else {
									System.out.println("Error line " + line5);
								}
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line5);
				e.printStackTrace();
			}
		}
	}

	public void GenerateVA_IVRMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f5 = new File(filepath);
		if (f5.exists()) {
			String line5 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_va_mts where Date = date(subdate(now(),"
								+ day + ")) and mode='IVR'");
				BufferedReader br5 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line5 = br5.readLine()) != null) {
					line5 = line5.trim();
					if (line5.contains("##")) {
						line5 = line5.replace("##", "#");
					}
					String arrtemp[] = line5.split("#");
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

						// SongUniqueCode =
						// SongUniqueCode.substring(SongUniqueCode.indexOf("_")
						// + 1);

						String Cat = arrtemp[i + 1].trim();
						String SubCat = arrtemp[i + 2].trim();
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line5);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| Duration.equalsIgnoreCase("NaN")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line " + line5);
							} else {
								if (SongUniqueCode.contains(".wav")) {
									SongUniqueCode = SongUniqueCode.substring(
											0, SongUniqueCode.indexOf("."));
									// System.out.println("Error line " +
									// line5);
									stmt_destination
											.executeUpdate("insert into tbl_contentusage_va_mts (SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
													+ SongUniqueCode
													+ "','"
													+ Duration
													+ "','"
													+ Circle
													+ "','"
													+ DATE
													+ "','"
													+ MSISDN
													+ "','"
													+ DNIS
													+ "','IVR','"+Cat+"')");
								} else {
									System.out.println("Error line " + line5);
								}
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line5);
				e.printStackTrace();
			}
		}
	}
	public void GenerateContestMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f5 = new File(filepath);
		if (f5.exists()) {
			String line5 = "";
			try {
				BufferedReader br5 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line5 = br5.readLine()) != null) {
					line5 = line5.trim();
					if (line5.contains("##")) {
						line5 = line5.replace("##", "#");
					}
					String arrtemp[] = line5.split("#");
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
						/*if (SongUniqueCode.contains("__")) {
							SongUniqueCode = SongUniqueCode.replace("__", "_");
						}*/

						SongUniqueCode =
						 SongUniqueCode.substring(SongUniqueCode.indexOf("_") + 1);
					//	System.out.println(SongUniqueCode);
						String Cat = arrtemp[i + 1].trim();
						String SubCat = arrtemp[i + 2].trim();
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line5);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| Duration.equalsIgnoreCase("NaN")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line song code issue" + line5);
							} else {
								/*if (SongUniqueCode.contains(".wav")) {
									SongUniqueCode = SongUniqueCode.substring(
											0, SongUniqueCode.indexOf("."));
									// System.out.println("Error line " +
									// line5);*/
									stmt_destination
											.executeUpdate("insert into tbl_contentusage_contest_mts (SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
													+ SongUniqueCode
													+ "','"
													+ Duration
													+ "','"
													+ Circle
													+ "','"
													+ DATE
													+ "','"
													+ MSISDN
													+ "','"
													+ DNIS
													+ "','','"+Cat+"')");
							/*	} /*else {
									System.out.println("Error line " + line5);
								}*/
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line5);
				e.printStackTrace();
			}
		}
	}
	public void GenerateJokesMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f6 = new File(filepath);
		if (f6.exists()) {
			String line6 = "";
			try {
				stmt_destination
						.executeUpdate("delete from tbl_contentusage_jokes where Date = date(subdate(now(),"
								+ day + ")) and Operator='MTS'");
				BufferedReader br6 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line6 = br6.readLine()) != null) {
					line6 = line6.trim();
					String[] arrtemp = line6.split("#");
					// StringTokenizer st = new StringTokenizer(line6,"#");
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
										.executeUpdate("insert into tbl_contentusage_jokes(Operator,MSISDN,Circle,Date,Duration,DNIS,Section,SubSection,Filename,Language,Mode,businesscategory)values('MTS','"
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
	public void GenerateRegionalMIS(String filepath) {
		String MSISDN = "";
		String DATE = "";
		System.out.println(filepath);
		File f5 = new File(filepath);
		if (f5.exists()) {
			String line5 = "";
			try {
				BufferedReader br5 = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath)));
				while ((line5 = br5.readLine()) != null) {
					line5 = line5.trim();
					if (line5.contains("##")) {
						line5 = line5.replace("##", "#");
					}
					String arrtemp[] = line5.split("#");
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
						/*if (SongUniqueCode.contains("__")) {
							SongUniqueCode = SongUniqueCode.replace("__", "_");
						}*/

						SongUniqueCode =
						 SongUniqueCode.substring(SongUniqueCode.indexOf("_") + 1);
					//	System.out.println(SongUniqueCode);
						String Cat = arrtemp[i + 1].trim();
						Cat= getRegCat(Cat);
						String SubCat = arrtemp[i + 2].trim();
						String Duration = arrtemp[i + 3].trim();
						if (Integer.parseInt(Duration) < 0) {
							Duration = Duration.substring(1);
							// System.out.println("Duration"+Duration);
						}
						if (Duration.toLowerCase().contains("undef")
								|| Integer.parseInt(Duration) > 9000) {
							System.out.println("Duration error : " + line5);
						} else {
							if (SongUniqueCode.equalsIgnoreCase("01")
									|| SongUniqueCode.equalsIgnoreCase("02")
									|| SongUniqueCode.equalsIgnoreCase("03")
									|| SongUniqueCode.equalsIgnoreCase("")
									|| Duration.equalsIgnoreCase("NaN")
									|| SongUniqueCode.toLowerCase().contains(
											"undef")) {
								System.out.println("Error line song code issue" + line5);
							} else {
								/*if (SongUniqueCode.contains(".wav")) {
									SongUniqueCode = SongUniqueCode.substring(
											0, SongUniqueCode.indexOf("."));
									// System.out.println("Error line " +
									// line5);*/
									stmt_destination
											.executeUpdate("insert into tbl_contentusage_reg_mts (SongUniqueCode,Duration,Circle,Date,MSISDN,DNIS,Mode,businesscategory)values('"
													+ SongUniqueCode
													+ "','"
													+ Duration
													+ "','"
													+ Circle
													+ "','"
													+ DATE
													+ "','"
													+ MSISDN
													+ "','"
													+ DNIS
													+ "','','"+Cat+"')");
							/*	} /*else {
									System.out.println("Error line " + line5);
								}*/
							}
						}
						i = i + 4;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception Line is : " + line5);
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
			ResultSet Rsdate = stmt_source.executeQuery(query_date);
			while (Rsdate.next()) {
				FILEDATE = Rsdate.getString("FILEDATE");
			}
			GenerateDevoMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/Devotional/Devotional_contentlog_"+ FILEDATE + ".txt");
			Generate54646MIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/54646/54646_contentlog_"+ FILEDATE + ".txt");
			GenerateMndMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/MND/MND_contentlog_"+ FILEDATE + ".txt");
			GenerateMtvMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/mtv/mtv_contentlog_"
					+ FILEDATE + ".txt");
			GenerateMODMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/MOD/MOD_contentlog_"
					+ FILEDATE + ".txt");
			GenerateFMJMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/starclub/starclub_contentlog_"
					+ FILEDATE + ".txt");
			GenerateREDFMMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/REDFM/REDFM_contentlog_"
					+ FILEDATE + ".txt");
			GenerateVAMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/OBDVOICE/OBDVOICE_contentlog_"
					+ FILEDATE + ".txt");
			GenerateVA_IVRMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/Voicealert/Voicealert_contentlog_"
					+ FILEDATE + ".txt");
			GenerateCinemaMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/CINEMA/CINEMA_contentlog_"
					+ FILEDATE + ".txt");
			GenerateContestMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/JEETOCONTEST/Monsoon-Contest_contentlog_"
					+ FILEDATE + ".txt");
			GenerateContestMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/JEETOCONTEST/Monsoon-Contest-toll_contentlog_"
					+ FILEDATE + ".txt");
			GenerateJokesMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/JOKES/JOKEPORTAL_contentlog_"
					+ FILEDATE + ".txt");
			GenerateRegionalMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/Regional/Regional_Portal_contentlog_"
					+ FILEDATE + ".txt");
			GenerateRegionalMIS("/home/MISDATA_TEST/Hungama_call_logs_MTS/Regional/Reg_Browsing_contentlog_"
					+ FILEDATE + ".txt");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt_destination.close();
				stmt_source.close();
				con_destination.close();
				con_source.close();
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
		ContentAutomation c = new ContentAutomation();
		c.start();
	}
}
