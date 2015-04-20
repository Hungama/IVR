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

public class RelianceMM_CrbtProcess extends Thread{
	public static Connection con=null;
	public static Statement stmt,stmtUpdate;
	public static CallableStatement cstmt=null;
	public String ani=null,rngid=null,req_type=null,crbt_mode=null;
	public String ip=null,dsn=null,username=null,pwd=null;
	public RelianceMM_CrbtProcess()
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/crbtmgr");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
//  #########################################################################################################################################
	private Connection dbConn()
	{
		while(true)
		{
			try
			{
			    Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				System.out.println("Database Connection established!");
				return con;
			}
			catch(Exception e)
			{
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
//	#################################################### check_multiple METHOD  ############################################################

	 public void check_multiple()
	 {
		 try
		 {
			 File mfile = new File("relmmcrbt.lck");
			 if(mfile.exists())
			 {
				 System.out.println(" WARNING !!! ANOTHER PROGRAM IS RUNNING !!!!!");
				 System.exit(0);
			 }
			 else
			 {
				 mfile.createNewFile();
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }

/************ SEND SMS ************/
		public void sendSMS(String ani, String mesg, String dnis)
		{

				try
				{
					CallableStatement cstmtfm1 = null;
					cstmtfm1 = con.prepareCall("{call master_db.SENDSMS_NEW(?,?,?,?,?,?)}");
					cstmtfm1.setString(1,ani);
					cstmtfm1.setString(2,mesg);
					cstmtfm1.setString(3,dnis);
					cstmtfm1.setString(4,"RELM");
					cstmtfm1.setString(5,"RNG");
					cstmtfm1.setInt(6,5);
					cstmtfm1.execute();
					cstmtfm1.close();
					CallableStatement cstmtfm = null;

					cstmtfm = con.prepareCall("{call music_crbtrng_ok(?,?,?,?)}");
					cstmtfm.setString(1,ani);
					cstmtfm.setString(2,req_type);
					cstmtfm.setString(3,crbt_mode);
					cstmtfm.setString(4,"OK");
					cstmtfm.execute();
					cstmtfm.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}


		}

	public void run()
	{
		try
		{
			dbConn();
			stmt = con.createStatement();
			stmtUpdate = con.createStatement();
			while(true)
			{
				try
				{
					String STR;
					String rndPIN="",txtMSG="";
					STR = "select ani,rngid,req_type,crbt_mode from tbl_crbtrng_reqs where status=0 order by date_time asc";
					ResultSet rs = stmt.executeQuery(STR);
		            while(rs .next())
					{
						ani = rs .getString("ani");
						ani=ani.trim();
						rngid=rs.getString("rngid");
						rngid=rngid.trim();
						req_type=rs.getString("req_type");
						req_type=req_type.trim();
						crbt_mode=rs.getString("crbt_mode");
						crbt_mode=crbt_mode.trim();
						System.out.println("Picked Mobile :"+ani+" for Request "+req_type);
						stmtUpdate.executeUpdate("update tbl_crbtrng_reqs set status=2 where ani='"+ani+"' and req_type='"+req_type+"'");

						if(req_type.equalsIgnoreCase("mt") || req_type.equalsIgnoreCase("fsd") || req_type.equalsIgnoreCase("tt") || req_type.equalsIgnoreCase("pt"))
						{
							String pingenURL = "http://202.87.41.147/waphung/voiceContentServe/PIN_generate.php";
							URL pingen = new URL(pingenURL);
							HttpURLConnection pingenconn = (HttpURLConnection)pingen.openConnection();
							String response ="";
							if(pingenconn.getResponseCode()== HttpURLConnection.HTTP_OK)
							{
								BufferedReader in = new BufferedReader(new InputStreamReader(pingenconn.getInputStream()));
								String line="";
								System.out.println("*******************START*************************");
								while ((line= in.readLine()) != null)  {
									response = response + line;
									System.out.println("responce-->"+response);
								}
								in.close();
								pingenconn.disconnect();
								System.out.println("*******************END***************************");
								rndPIN=response;
								txtMSG = "Thanks for downloading from RelianceMusicMania.To download,click link(Data Charges apply on download), http://202.87.41.147/waphung/voiceContentServe/"+rngid+"/"+rndPIN+"";
								System.out.println("txtMSG-->"+txtMSG);
								sendSMS(ani,txtMSG,"RM-HNGAMA");
							}
							else
							{
								response = "Its Not HTTP_OK"+pingenconn.getResponseCode();
							}
						}
						else if(req_type.equalsIgnoreCase("crbt"))
						{
							//String smsURL1 = "http://119.82.69.211:8088/hungama/radio_cRBT?ANI="+ani+"&CLIPID="+rngid+"&TOKEN="+crbt_mode+"&OPERATOR=RELC";
							String smsURL1 = "http://220.226.104.11/rcomvas?type=3&MDN="+ani+"&tuneCode="+rngid+"&chID=IVR&opId=RCOM&servId=0-0-HNGMA-IVRMM&resType=1";

							URL sms = new URL(smsURL1);
							HttpURLConnection smsconn = (HttpURLConnection)sms.openConnection();
							String response ="";
							if(smsconn.getResponseCode()== HttpURLConnection.HTTP_OK)
							{
								BufferedReader in = new BufferedReader(new InputStreamReader(smsconn.getInputStream()));
								String line="";
								System.out.println("*******************START*************************");
								while ((line= in.readLine()) != null)
								{
									response = response + line;
									System.out.println("responce-->"+response);
								}
								in.close();
								smsconn.disconnect();
								System.out.println("*******************END***************************");
								CallableStatement cstmtfm = null;
								cstmtfm = con.prepareCall("{call music_crbtrng_ok(?,?,?,?)}");
								cstmtfm.setString(1,ani);
								cstmtfm.setString(2,req_type);
								cstmtfm.setString(3,crbt_mode);
								cstmtfm.setString(4,response);
								cstmtfm.execute();
								cstmtfm.close();
							}
							else
							{
								response = "Its Not HTTP_OK"+smsconn.getResponseCode();
							}
						}
					}
		            Thread.sleep(1000);
				}catch(Exception e)
				{
					e.printStackTrace();
					try
					{
						if(e.toString().startsWith("com.mysql.jdbc.CommunicationsException:"))
						{
							System.out.println("DB Connectivity Failure!!! Retries to connect DB");
							Thread.sleep(10000);
							dbConn();
							stmt = con.createStatement();
							stmtUpdate = con.createStatement();
						}

					}catch(Exception e1)
					{
						e1.printStackTrace();
						System.exit(0);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String args[])
	{
		RelianceMM_CrbtProcess crm = new RelianceMM_CrbtProcess();
		crm.start();
	}
}
