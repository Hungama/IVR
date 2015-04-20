import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class VodaMuCrbtMnger extends Thread {
	public static Connection con = null;

	public static Statement stmt, stmtR, stmtUpdate;

	public static CallableStatement cstmt = null;

	public String ani = null, rngid = null, req_type = null, crbt_mode = null,
			operator = null, songid = null, cname = null, aname = null,
			 sstatus = null;

	public String ip = null, dsn = null, username = null, pwd = null;

	public int state = 1;

	public VodaMuCrbtMnger() {
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
			System.out.println("I have not found config");
			e.printStackTrace();
		}
	}

	// #########################################################################################################################################
	private Connection dbConn() {
		if(con==null){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://" + ip + "/"
						+ dsn, username, pwd);
				System.out.println("Database Connection established!");
				return con;
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return con;
	}

	// #################################################### check_multiple
	// METHOD ############################################################

	public void check_multiple() {
		try {
			File mfile = new File("VodaMuCrbtMnger.lck");
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
	public void sendSMS(String ani, String mesg, String dnis) {

		try {
			CallableStatement cstmtfm1 = null;
			cstmtfm1 = con
					.prepareCall("{call master_db.SENDSMS_NEW(?,?,?,?)}");
			cstmtfm1.setString(1, ani);
			cstmtfm1.setString(2, mesg);
			cstmtfm1.setString(3, dnis);
			cstmtfm1.setString(4, "1");
			cstmtfm1.execute();
			cstmtfm1.close();
			CallableStatement cstmtfm = null;
			cstmtfm = con.prepareCall("{call radio_crbtrng_ok(?,?,?,?)}");
			cstmtfm.setString(1, ani);
			cstmtfm.setString(2, req_type);
			cstmtfm.setString(3, crbt_mode);
			cstmtfm.setString(4, "OK");
			cstmtfm.execute();
			cstmtfm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void mtlog(String ani, String status) {

		try {
			CallableStatement cstmtfm = null;
			cstmtfm = con.prepareCall("{call radio_crbtrng_ok(?,?,?,?)}");
			cstmtfm.setString(1, ani);
			cstmtfm.setString(2, req_type);
			cstmtfm.setString(3, crbt_mode);
			cstmtfm.setString(4, "OK");
			cstmtfm.execute();
			cstmtfm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			dbConn();
			stmt = con.createStatement();
			stmtR = con.createStatement();
			stmtUpdate = con.createStatement();
			while (true) {
				try {
					String STR;
					STR = "select ani,rngid,req_type,crbt_mode,operator,status,songid from tbl_crbtrng_reqs where status=0 and req_type like '%crbt%' order by date_time asc";
					ResultSet rs = stmt.executeQuery(STR);
					while (rs.next()) {
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

						if (songid.contains("_"))
							songid = songid.substring(4);
						songid = songid.trim();
						System.out.println("Picked Mobile :" + ani
								+ " for Request " + req_type + " status is "
								+ state + " album name " + songid);
						stmtUpdate
								.executeUpdate("update tbl_crbtrng_reqs set status=2 where ani='"
										+ ani
										+ "' and req_type='"
										+ req_type
										+ "' and operator='"
										+ operator
										+ "' and songid like '%"
										+ songid
										+ "%'");
						if (req_type.equalsIgnoreCase("crbt")) {
							String smsURL1 = "http://10.43.248.137/VodafoneBilling/VodaCrbtRu.php?msisdn="
									+ ani + "&PROMO_ID=" + rngid;
							System.out.println("SMS URL 1 :: " + smsURL1);
							URL sms = new URL(smsURL1);
							HttpURLConnection smsconn = (HttpURLConnection) sms
									.openConnection();
							String response = "";
							if (smsconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
								BufferedReader in = new BufferedReader(
										new InputStreamReader(smsconn
												.getInputStream()));
								String line = "";
								System.out
										.println("*******************START*************************");
								while ((line = in.readLine()) != null) {
									response = response + line;
									// response = response.replace(
									// "UserStatus1.value='", "");
									// response = response.replace("\'",
									// "");
									System.out
											.println("responce-->" + response);
								}
								in.close();
								smsconn.disconnect();
								System.out
										.println("*******************END***************************");
								CallableStatement cstmtfm = null;
								cstmtfm = con.prepareCall("{call radio_crbtrng_ok(?,?,?,?)}");
								cstmtfm.setString(1, ani);
								cstmtfm.setString(2, req_type);
								cstmtfm.setString(3, crbt_mode);
								cstmtfm.setString(4, response);
								cstmtfm.execute();
								cstmtfm.close();
							} else {
								response = "Its Not HTTP_OK"
										+ smsconn.getResponseCode();
							}
						}
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						if (e.toString().startsWith(
								"com.mysql.jdbc.CommunicationsException:")) {
							System.out
									.println("DB Connectivity Failure!!! Retries to connect DB");
							Thread.sleep(10000);
							dbConn();
							stmt = con.createStatement();
							stmtUpdate = con.createStatement();
						}

					} catch (Exception e1) {
						e1.printStackTrace();
						System.exit(0);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		VodaMuCrbtMnger crm = new VodaMuCrbtMnger();
		crm.start();
	}

}
