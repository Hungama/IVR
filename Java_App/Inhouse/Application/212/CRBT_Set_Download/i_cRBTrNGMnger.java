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

public class i_cRBTrNGMnger extends Thread{
	public static Connection con=null;
	public static Statement stmt,stmtR,stmtUpdate;
	public static CallableStatement cstmt=null;
	public String ani=null,rngid=null,req_type=null,crbt_mode=null,circle=null,operator=null,songid=null,cname=null,aname=null; public String DCccg_ID = null;
	public String ip=null,dsn=null,username=null,pwd=null;
	public int state=1;
	public i_cRBTrNGMnger()
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr");
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
			 File mfile = new File("cRBTrNGMnger.lck");
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
					cstmtfm1.setString(4,"TATM");
					cstmtfm1.setString(5,"RNG");
					cstmtfm1.setInt(6,2);
					cstmtfm1.execute();
					cstmtfm1.close();
					CallableStatement cstmtfm = null;
					cstmtfm = con.prepareCall("{call RADIO_CRBTRNG_OK(?,?,?)}");
					cstmtfm.setString(1,ani);
					cstmtfm.setString(2,req_type);
					cstmtfm.setString(3,"OK");
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
			stmtR = con.createStatement();
			stmtUpdate = con.createStatement();
			while(true)
			{
				try
				{
					String STR;
					String STRR;
					String rndPIN="",txtMSG="";
					STR = "select ani,rngid,req_type,crbt_mode,operator,status,songid,circle,DCccg_ID from tbl_crbtrng_reqs where status=7 and DCccg_ID is not null order by date_time asc";

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
						circle=rs.getString("circle");
						circle=circle.trim();
						state=rs.getInt("status");
						songid=rs.getString("songid");
						DCccg_ID = rs.getString("DCccg_ID");
						DCccg_ID = DCccg_ID.trim();

						if(songid.contains("_"))
							songid = songid.substring(4);
						songid=songid.trim();


						STRR = "select ContentName,AlbumName from tbl_song_details as a,master_db.master_content as b where a.SongUniqueCode=b.SongUniqueCode and a.SongUniqueCode='"+songid+"'";
						ResultSet rs1 = stmtR.executeQuery(STRR);
						while(rs1 .next())
						{
							aname = rs1.getString("AlbumName");
							if(aname.length()>12)
								aname = aname.substring(0,11);
							aname = aname.trim();

							cname = rs1.getString("ContentName");
							if(cname.length()>12)
								cname = cname.substring(0,11);
							cname=cname.trim();
						}

						System.out.println("Picked Mobile :"+ani+" for Request "+req_type+" status is "+state+" album name "+songid);

						stmtUpdate.executeUpdate("update tbl_crbtrng_reqs set status=2 where ani='"+ani+"' and req_type='"+req_type+"' and DCccg_ID='"+DCccg_ID+"'");
						if(req_type.equalsIgnoreCase("mt") || req_type.equalsIgnoreCase("fsd") || req_type.equalsIgnoreCase("tt") || req_type.equalsIgnoreCase("pt"))
						{
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
								if(state==5)
								{
									txtMSG = "Now enjoy FREE ringtone of "+cname+" from "+aname+".To download,click link http://1li.in/ceprt/"+rngid+"/"+rndPIN+"";
								}
								else
								{
									txtMSG = "Thnks 4 downloading from Endless Music.To download,click link(Data Charges apply on download), http://202.87.41.147/waphung/docomocontentServe/"+rngid+"/"+rndPIN+"";
								}
								System.out.println("txtMSG-->"+txtMSG);
								sendSMS(ani,txtMSG,"590906");

							}else{
								response = "Its Not HTTP_OK"+pingenconn.getResponseCode();
							}
						}
						else if(req_type.equalsIgnoreCase("crbt"))
						{
							String smsURL1;
							if("MUM".equalsIgnoreCase(circle) || "MAH".equalsIgnoreCase(circle))
							{
							smsURL1 = "http://192.168.100.211:8088/hungama/radio_cRBT?ANI="+ani+"&CLIPID="+rngid+"&TOKEN="+crbt_mode+"&OPERATOR=TATCM&CCGID="+DCccg_ID+"";
							}
							else
							{
							smsURL1 = "http://192.168.100.211:8088/hungama/radio_cRBT?ANI="+ani+"&CLIPID="+rngid+"&TOKEN="+crbt_mode+"&OPERATOR=TATC&CCGID="+DCccg_ID+"";
							}
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
									response = response.replace("UserStatus1.value='","");
									response = response.replace("\'","");
									System.out.println("responce-->"+response);
								}
								in.close();
								smsconn.disconnect();
								System.out.println("*******************END***************************");
								CallableStatement cstmtfm = null;
								cstmtfm = con.prepareCall("{call RADIO_CRBTRNG_OK(?,?,?)}");
								cstmtfm.setString(1,ani);
								cstmtfm.setString(2,req_type);
								cstmtfm.setString(3,response);
								cstmtfm.execute();
								cstmtfm.close();
							}else
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
		i_cRBTrNGMnger crm = new i_cRBTrNGMnger();
		crm.start();
	}

}
