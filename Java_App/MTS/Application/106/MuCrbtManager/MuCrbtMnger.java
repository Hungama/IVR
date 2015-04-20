import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.protocol.Protocol;


import com.sun.net.ssl.HttpsURLConnection;

public class MuCrbtMnger<AuthSSLProtocolSocketFactory> extends Thread{
	public static Connection con=null;
	public static Statement stmt,stmtUpdate;
	public static CallableStatement cstmt=null;
	public String ani=null,rngid=null,req_type=null,crbt_mode=null,circle=null,songid=null,DCccg_ID = null;
	public String ip=null,dsn=null,username=null,pwd=null;
	String cuki=new String();
	public MuCrbtMnger()
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
				sendSMS("8376903442","Hi There is some issue in crbt manager java code on mts server.Pls check","52222");
				System.exit(0);
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
			 File mfile = new File("MuCrbtMnger.lck");
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
					cstmtfm1 = con.prepareCall("{call master_db.SENDSMS(?,?,?)}");
					cstmtfm1.setString(1,ani);
					cstmtfm1.setString(2,mesg);
					cstmtfm1.setString(3,dnis);
					cstmtfm1.execute();
					cstmtfm1.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}


		}

/************ RINGTONE SEND SMS ************/
		public void ringsendSMS(String ani, String mesg, String dnis)
		{

				try
				{
					CallableStatement cstmtfm2 = null;
					cstmtfm2 = con.prepareCall("{call RINGTONE_SENDSMS(?,?,?)}");
					cstmtfm2.setString(1,ani);
					cstmtfm2.setString(2,mesg);
					cstmtfm2.setString(3,dnis);
					cstmtfm2.execute();
					cstmtfm2.close();
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
					STR = "select ani,songid,rngid,req_type,crbt_mode,circle,DCccg_ID from tbl_HB_CrbtRng_Reqs nolock where status=0 order by date_time asc";
					ResultSet rs = stmt.executeQuery(STR);
		            while(rs .next())
					{
						ani = rs .getString("ani");
						ani=ani.trim();
						songid=rs.getString("songid");
						songid=songid.trim();
						rngid=rs.getString("rngid");
						rngid=rngid.trim();
						req_type=rs.getString("req_type");
						req_type=req_type.trim();
						crbt_mode=rs.getString("crbt_mode");
						crbt_mode=crbt_mode.trim();
						circle=rs.getString("circle");
						circle=circle.trim();
						//if(DCccg_ID.equals(null))
						//	DCccg_ID ="0";
						DCccg_ID = rs.getString("DCccg_ID");
                                                if(DCccg_ID.equals(null))
                                                        DCccg_ID ="0";
						DCccg_ID = DCccg_ID.trim();

						System.out.println("Picked Mobile :"+ani+" for Request "+req_type+" CCG ID "+DCccg_ID);
						//stmtUpdate.executeUpdate("update tbl_HB_CrbtRng_Reqs set status=2 where ani='"+ani+"' and req_type='"+req_type+"'");
						stmtUpdate.executeUpdate("update tbl_HB_CrbtRng_Reqs set status=2 where ani='"+ani+"' and req_type='"+req_type+"' and DCccg_ID='"+DCccg_ID+"'");
						if(req_type.equalsIgnoreCase("rngtone"))
						{
							CallableStatement cstmtfm = null;
							if((circle.equalsIgnoreCase("tnu")) || (circle.equalsIgnoreCase("raj")) || (circle.equalsIgnoreCase("kar")) || (circle.equalsIgnoreCase("ker")) || (circle.equalsIgnoreCase("apd")) || (circle.equalsIgnoreCase("chn")))
							{
								System.out.println("TC API circle  "+circle);
								stmtUpdate.executeUpdate("insert into tbl_min_reqs_tc values('"+ani+"',0)");
								cstmtfm = con.prepareCall("{call RADIO_MIN_TC(?,?,?,?)}");
							}
							else
							{
								System.out.println("FORIS API circle  "+circle);
								stmtUpdate.executeUpdate("insert into tbl_min_reqs_foris values('"+ani+"',0)");
								cstmtfm = con.prepareCall("{call RADIO_MIN_FORIS(?,?,?,?)}");
							}
							//cstmtfm = con.prepareCall("{call RADIO_MIN_VINAY(?,?,?,?)}");
							cstmtfm.setString(1,ani);
							cstmtfm.setString(2,"GET");
							cstmtfm.setString(3,songid);
							cstmtfm.registerOutParameter(4,java.sql.Types.VARCHAR);
							cstmtfm.execute();
							String value = cstmtfm.getString(4);
							String temp[]=value.split("#");
							cstmtfm.close();
							if("-1".equalsIgnoreCase(temp[0]))
							{
								try
								{
									//stmtUpdate.executeUpdate("insert into tbl_min_reqs values('"+ani+"',0)");
									txtMSG = "Dear Customer ringtone requested cannot be complete as u dnt hav My MTS planet, Just select My MTS on ur ph >Catalog >download My MTS Planet";
									System.out.println("txtMSG-->"+txtMSG);
									//sendSMS(ani,txtMSG,"52222");
								}
								catch(Exception e)
								{
									System.out.println( "Something bad just happened." );
									System.out.println( e );
									e.printStackTrace();
								}



							}
							else
							{
								String pingenURL = "http://10.130.199.25/MTS/subscriberuser.php?min="+temp[0]+"&mdn="+ani+"&conid="+temp[2]+"&conname="+temp[3]+"&skey="+temp[1]+"&subdate="+temp[4]+"&tid="+temp[4]+"";
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
									cstmtfm = null;
									System.out.println("*******************MUKESH***************************");
									cstmtfm = con.prepareCall("{call RADIO_CRBTRNG_OK(?,?,?,?)}");
									cstmtfm.setString(1,ani);
									cstmtfm.setString(2,req_type);
									cstmtfm.setString(3,crbt_mode);
									cstmtfm.setString(4,response);
									cstmtfm.execute();
									System.out.println("*****procedure call*****"+cstmtfm);

									cstmtfm.close();
								}else{
									response = "Its Not HTTP_OK"+pingenconn.getResponseCode();
								}
								txtMSG = "U hv requested ("+temp[3]+") ringtone to download Dial "+temp[1]+" frm ur phone or visit My MTS Planet frm ur phone.";
								txtMSG = txtMSG.replaceAll(" ","%20");
								System.out.println("txtMSG-->"+txtMSG);
								ringsendSMS(ani,txtMSG,"52222");
							}
						}
						else if(req_type.equalsIgnoreCase("crbt"))
						{
							String smsURL1 = "http://localhost:8088/hungama/radio_cRBT?ANI="+ani+"&CLIPID="+rngid+"&TOKEN="+crbt_mode+"&CCGID="+DCccg_ID+"";
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
								cstmtfm = con.prepareCall("{call HB_CRBTRNG_OK(?,?,?,?)}");
								cstmtfm.setString(1,ani);
								cstmtfm.setString(2,req_type);
								cstmtfm.setString(3,crbt_mode);
								cstmtfm.setString(4,response);
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
					//e.printStackTrace();
					sendSMS("8376903442","Hi There is some issue in crbt manager java code on mts server.Pls check","52222");
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
						sendSMS("8376903442","Hi There is some issue in crbt manager java code on mts server.Pls check","52222");
						System.exit(0);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			sendSMS("8376903442","Hi There is some issue in crbt manager java code on mts server.Pls check","52222");
			System.exit(0);
		}
	}

	public static void main(String args[])
	{
		MuCrbtMnger crm = new MuCrbtMnger();
		crm.start();
	}

}
