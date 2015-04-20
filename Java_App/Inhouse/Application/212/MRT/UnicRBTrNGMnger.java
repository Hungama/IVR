import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class UnicRBTrNGMnger extends Thread {
	public static Connection con = null;

	public static Statement stmt, stmtR, stmtUpdate;

	public static CallableStatement cstmt = null;

	public String ani = null, rngid = null, req_type = null, crbt_mode = null,
			operator = null, songid = null, cname = null, aname = null,
			serialid = null, sstatus = null, circle=null;

	public String ip = null, dsn = null, username = null, pwd = null;

	public int state = 1;

	public UnicRBTrNGMnger() {
		try {

			ResourceBundle resource = ResourceBundle
					.getBundle("config/chargingmgr");
			ip = resource.getString("IP");
			dsn = resource.getString("DSN");
			username = resource.getString("USERNAME");
			pwd = resource.getString("PWD");
			System.out.println("IP: " + ip + " DATABASE :" + dsn + " USER :"
					+ username + " PWD:" + pwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// #########################################################################################################################################
	private Connection dbConn() {
		while (true) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://" + ip + "/"
						+ dsn, username, pwd);
				System.out.println("Database Connection established!");
				return con;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	// #################################################### check_multiple
	// METHOD ############################################################

	public void check_multiple() {
		try {
			File mfile = new File("UnicRBTrNGMnger.lck");
			if (mfile.exists()) {
				System.out
						.println(" WARNING !!! ANOTHER PROGRAM IS RUNNING !!!!!");
				System.exit(0);
			} else {
				mfile.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** ********** SEND SMS *********** */
	public void sendSMS(String ani, String mesg, String dnis)
		{

			try
			{
				CallableStatement cstmtfm1 = null;
				cstmtfm1 = con.prepareCall("{call master_db.SENDSMS_NEW(?,?,?,?,?,?)}");
				cstmtfm1.setString(1, ani);
				cstmtfm1.setString(2, mesg);
				cstmtfm1.setString(3, dnis);
				cstmtfm1.setString(4, "UNIM");
				cstmtfm1.setString(5, "RNG");
				cstmtfm1.setInt(6, 2);
				cstmtfm1.execute();
				cstmtfm1.close();
				CallableStatement cstmtfm = null;
				cstmtfm = con.prepareCall("{call RADIO_CRBTRNG_OK(?,?,?)}");
				cstmtfm.setString(1, ani);
				cstmtfm.setString(2, req_type);
				cstmtfm.setString(3, "OK");
				cstmtfm.execute();
				cstmtfm.close();

				CallableStatement cstmtfm2 = null;
	                        cstmtfm2 = con.prepareCall("{call RADIO_RT_TOTALREQ(?,?)}");
        	                cstmtfm2.setString(1, ani);
                	        cstmtfm2.setString(2, serialid);
                        	cstmtfm2.execute();
	                        cstmtfm2.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
		    }
       }

	public void mtlog(String ani, String status) {

		try {
			System.out.println("IN MT log method");

			CallableStatement cstmtfm = null;
			cstmtfm = con.prepareCall("{call RADIO_CRBTRNG_OK(?,?,?)}");
			cstmtfm.setString(1, ani);
			cstmtfm.setString(2, "mt");
			cstmtfm.setString(3, status);
			cstmtfm.execute();
			cstmtfm.close();

			CallableStatement cstmtfm4 = null;
                        cstmtfm4 = con.prepareCall("{call RADIO_RT_TOTALREQ(?,?)}");
                        cstmtfm4.setString(1, ani);
                        cstmtfm4.setString(2, serialid);
                        cstmtfm4.execute();
                        cstmtfm4.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run()
	{
		try
			{
				dbConn();
				stmt = con.createStatement();
				stmtR = con.createStatement();
				stmtUpdate = con.createStatement();
				while (true)
				{
					try
					{
						String STR;
						String rndPIN = "", txtMSG = "";

						STR = "select ani,rngid,req_type,crbt_mode,operator,status,songid,serialid,circle from tbl_crbtrng_reqs where status=0 or status=5 order by date_time desc";
						ResultSet rs = stmt.executeQuery(STR);

						while (rs.next())
							{
								ani = rs.getString("ani");
								ani = ani.trim();
								rngid = rs.getString("rngid");
								rngid = rngid.trim();
								req_type = rs.getString("req_type");
								req_type = req_type.trim();
								crbt_mode = rs.getString("crbt_mode");
								crbt_mode = crbt_mode.trim();
								operator = rs.getString("operator");
								operator = operator.trim();
								state = rs.getInt("status");
								songid = rs.getString("songid");
								serialid = rs.getString("serialid");
								if (songid.contains("_"))
									songid = songid.substring(4);
								songid = songid.trim();
								circle = rs.getString("circle");
								circle = circle.trim();
								sstatus  = "99999";

								String strsubquery = "Select status from tbl_radio_ringtonesubscription where serialid="+ serialid;
								ResultSet rsstatus = stmtR.executeQuery(strsubquery);
								//System.out.println(strsubquery);
								while (rsstatus.next())
									{
										sstatus = rsstatus.getString("status");
									}
									//System.out.println("my status : " + sstatus );
									if (sstatus.equals("0"))
										{

										}
									else if (sstatus.equals("1"))
										{
												System.out.println("Picked Mobile :" + ani + " for Request " + req_type + " status is " + state + " album name " + songid);
												stmtUpdate.executeUpdate("update tbl_crbtrng_reqs set status=2 where ani='"+ ani+ "' and req_type='"+ req_type + "' and operator='" + operator + "' and songid like '%"	+ songid + "%'");
											//	System.out.println("update tbl_crbtrng_reqs set status=2 where ani='"+ ani+ "' and req_type='"+ req_type+ "' and operator='"+ operator+ "' and songid='"+ songid + "'");

												if (req_type.equalsIgnoreCase("fsd") || req_type.equalsIgnoreCase("tt") || req_type.equalsIgnoreCase("pt"))
													{
														String pingenURL = "http://202.87.41.147/waphung/voiceContentServe/PIN_generate.php";
														URL pingen = new URL(pingenURL);
														HttpURLConnection pingenconn = (HttpURLConnection) pingen.openConnection();
														String response = "";
														if (pingenconn.getResponseCode() == HttpURLConnection.HTTP_OK)
														{
															BufferedReader in = new BufferedReader(
																	new InputStreamReader(pingenconn
																			.getInputStream()));
															String line = "";
															System.out.println("*******************START*************************");
															while ((line = in.readLine()) != null)
																{
																	response = response + line;
																	System.out.println("responce-->"
																			+ response);
																}
																in.close();
																pingenconn.disconnect();
															System.out.println("*******************END***************************");
															rndPIN = response;

															txtMSG = "Thanks for downloading from Uninor My Ringtones.To download,click link(Data Charges apply on download), http://202.87.41.147/waphung/voiceContentServe/"+ rngid + "/" + rndPIN + "";

															System.out.println("txtMSG-->" + txtMSG);
															sendSMS(ani, txtMSG, "52888");

														}
														else
														{
															response = "Its Not HTTP_OK"+ pingenconn.getResponseCode();
														}

													}
												else if (req_type.equalsIgnoreCase("mt"))
													{
															System.out.println("i am in MT----->");
															String STRmt;
															String strhexdump = "";
															String strcir = "";
															String status = "OK";

															System.out.println("Song ID----->" + songid);
															try
																{
																	STRmt = "select HEX_DUMP from uninor_myringtone.tbl_song_details where SongUniqueCode='" + songid + "'";
																	//System.out.println(STRmt);
																	ResultSet rshex = stmt.executeQuery(STRmt);
																	while (rshex.next())
																	{
																		strhexdump = rshex.getString("HEX_DUMP");
																	}
																}
															catch(Exception e1)
																{
																	System.out.println("Error-> "+e1);
																}

																System.out.println("Hex dump : " + strhexdump);
																if (strhexdump.length() > 400)
																	{
																		status = "NOK";
																		stmtUpdate.executeUpdate("update tbl_crbtrng_reqs set status=3 where ani='"+ ani+ "' and req_type='"+ req_type+ "' and operator='"+ operator+ "' and songid like '%"+ songid + "%'");
																	}
																else
																	{
																		String Strsms = "insert into master_db.tbl_new_sms1(ani,message,date_time,status,dnis,type,operator,circle,priority) values("+ ani+ ",'"+ strhexdump+ "',now(),5,52888,'RNG','UNIM','"+ circle + "',5)";
																		stmt.executeUpdate(Strsms);
																		//System.out.println(Strsms);
																		System.out.println("MT complete" + Strsms);
																	}

																		System.out.println("Before MT log method");
																		mtlog(ani, status);
																		System.out.println("After MT log method");

													}
												else if (req_type.equalsIgnoreCase("crbt"))
													{
														String smsURL1 = "http://119.82.69.211:8088/hungama/radio_cRBT?ANI="+ ani+ "&CLIPID="+ rngid+ "&TOKEN="+ crbt_mode+ "&OPERATOR="+ operator+ "";
														URL sms = new URL(smsURL1);
														HttpURLConnection smsconn = (HttpURLConnection) sms.openConnection();
														String response = "";
														if (smsconn.getResponseCode() == HttpURLConnection.HTTP_OK)
															{
																BufferedReader in = new BufferedReader(new InputStreamReader(smsconn.getInputStream()));
																String line = "";
																System.out.println("*******************START*************************");
																while ((line = in.readLine()) != null)
																	{
																		response = response + line;
																		response = response.replace("UserStatus1.value='", "");
																		response = response.replace("\'", "");
																		System.out.println("responce-->"+ response);
																	}
																	in.close();
																	smsconn.disconnect();
															   System.out.println("*******************END***************************");
															   CallableStatement cstmtfm = null;
																cstmtfm = con.prepareCall("{call RADIO_CRBTRNG_OK(?,?,?)}");
																cstmtfm.setString(1, ani);
																cstmtfm.setString(2, req_type);
																cstmtfm.setString(3, response);
																cstmtfm.execute();
																cstmtfm.close();
															}
													  else
															{
																response = "Its Not HTTP_OK"+ smsconn.getResponseCode();
															}
													}
										}
									else if (sstatus.equals("99999"))
										{
											stmtUpdate.executeUpdate("update tbl_crbtrng_reqs set status=2 where ani='"+ ani+ "' and req_type='"+ req_type+ "' and operator='"+ operator+ "' and songid like '%"+ songid + "%'");
											System.out.println("status is 99999 : " + sstatus);

											mtlog(ani, "NOK");

										}
								   else
								   		{
											System.out.println("status is : " + sstatus);

											mtlog(ani, "NOK");
										}
							}
							Thread.sleep(1000);
					}
					catch (Exception e)
						{
								System.out.println("In catch block");
								//e.printStackTrace();
								try
									{
										if (e.toString().startsWith("com.mysql.jdbc.CommunicationsException:"))
											{
													System.out.println("DB Connectivity Failure!!! Retries to connect DB");
													Thread.sleep(10000);
													dbConn();
													stmt = con.createStatement();
													stmtUpdate = con.createStatement();
											}

									}
							  catch (Exception e1)
							  		{
										e1.printStackTrace();
										System.exit(0);
									}
						}
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		UnicRBTrNGMnger crm = new UnicRBTrNGMnger();
		crm.start();
	}

}
