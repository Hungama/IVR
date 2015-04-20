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

public class UniRingToneSent extends Thread{
	public static Connection con=null;
	public static Statement stmt,stmt1,stmtUpdate;
	public static CallableStatement cstmt=null;
	public String ani=null,rngid=null,req_type=null,crbt_mode=null,lang=null,circle=null;
	public String ip=null,dsn=null,username=null,pwd=null;
	public UniRingToneSent()
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/dbconf");
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
			 File mfile = new File("RingToneSent.lck");
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
					cstmtfm1.setString(3,"59090");
					cstmtfm1.setString(4,"TATM");
					cstmtfm1.setString(5,"RNG");
					cstmtfm1.setInt(6,5);
					cstmtfm1.execute();
					cstmtfm1.close();
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
			stmt1 = con.createStatement();
			stmtUpdate = con.createStatement();
			try
			{
				String STR,STR1;
				String rndPIN="",txtMSG="";
				STR="select ani,circle from tbl_radio_promo where status=1";
				ResultSet rs = stmt.executeQuery(STR);
				while(rs .next())
				{
					ani    = rs .getString("ani");
					ani    = ani.trim();
					circle = rs .getString("circle");
					circle = circle.trim();

					System.out.println("Picked Mobile :"+ani);
					System.out.println("Picked circle  :"+circle);
					stmtUpdate.executeUpdate("update tbl_radio_promo set status=2 where ani='"+ani+"'");
					STR1="select ringID from tbl_ring_data where circle='"+circle+"'";
					ResultSet rs1 = stmt1.executeQuery(STR1);
					while(rs1 .next())
					{
						rngid=rs1.getString("ringID");
						rngid=rngid.trim();
						System.out.println("Ring id is:"+rngid);
						String pingenURL = "http://202.87.41.147/waphung/docomocontentServe/PIN_generate.php";
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
							txtMSG = "Now enjoy FREE Ringtone from Endless Music.To download,click link(Data Charge apply on download), http://202.87.41.147/waphung/docomocontentServe/"+rngid+"/"+rndPIN+"";

							System.out.println("txtMSG-->"+txtMSG);
							sendSMS(ani,txtMSG,"HUNVOC");
							System.out.println("SMS SENT ON DOCOMO");
						}
						else
						{
							response = "Its Not HTTP_OK"+pingenconn.getResponseCode();
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
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String args[])
	{
		UniRingToneSent crm = new UniRingToneSent();
		crm.start();
	}

}
