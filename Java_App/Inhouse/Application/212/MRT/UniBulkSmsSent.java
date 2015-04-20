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

public class UniBulkSmsSent extends Thread{
	public static Connection con=null;
	public static Statement stmt,stmt1,stmtUpdate;
	public static CallableStatement cstmt=null;
	public String ani=null,rngid=null,req_type=null,crbt_mode=null,lang=null,circle=null;
	public String ip=null,dsn=null,username=null,pwd=null;
	public UniBulkSmsSent()
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
				STR="select ani from tnl_bulk_sms where status=0";
				ResultSet rs = stmt.executeQuery(STR);
				while(rs .next())
				{
					ani    = rs .getString("ani");
					ani    = ani.trim();

					System.out.println("Picked Mobile :"+ani);

					stmtUpdate.executeUpdate("update tnl_bulk_sms set status=2 where ani='"+ani+"'");
					txtMSG = "FREE..FREE Sirf aap ke liye FREE Dial kare 59090 aur sune Satyamev Jayate ya Pyaar Ki Pungi jaise apne manpasand UNLIMITED gane bilkul FREE";
					System.out.println("txtMSG-->"+txtMSG);
					sendSMS(ani,txtMSG,"HUNVOC");
					System.out.println("SMS SENT ON DOCOMO");
				}

				Thread.sleep(2000);
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
		UniBulkSmsSent crm = new UniBulkSmsSent();
		crm.start();
	}

}
