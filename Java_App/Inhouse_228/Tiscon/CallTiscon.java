
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.CallableStatement;



public class CallTiscon extends Thread {

    public static Connection con = null;
    public static String ip = null, dsn = null, username = null, pwd = null, drivermgr = null, Query = null, Channel = null, Context = null, Exten = null, CallerID = null, strGender = null;
    public static Statement stmt, stmtR, stmtUpdate, stmtUpdate1,stmtcnt;
    public static String LogPath;
    ResultSet rsFemale;
    ResultSet rsMale;
    ResultSet rsData;
    ResultSet rscnt;
    int MaleBase = 0, Priority = 0, FemaleBase = 0, SleepTime = 0;
    private Properties props;
    String[] strFemaleChatID;
    String[] strMaleChatID;
    int ASTERISK_PORT;
    String ASTERISK_HOST = "";
    String ASTERISK_LOGINNAME = "";
    String ASTERISK_LOGINPWD = "";
    String strANI="";
   static public RequestInfo objRequest = new RequestInfo();
    int second, minute, hour, StartHour, EndHour;
    GregorianCalendar date = new GregorianCalendar();
	 String strCircle="";
	 String strOperator="";
 String strRealDnis="";

 SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
   Date localDate = new Date();
    public CallTiscon  () {
        try {
		//Load properties from property file in confog folder
            loadProps();
            System.out.println("log path become:"+objRequest.getLogPath());
            System.out.println("log page called");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int getRandom(int MinVal, int MaxVal) {
        int random = (int) (Math.random() * MaxVal + MinVal);
        return random;
    }


    public void loadProps() throws IOException {
        try {
            props = new Properties();
            props.load(new FileInputStream((new StringBuilder()).append(".")
                    .append(File.separator).append("config").append(
                    File.separator).append("OBDMgr.properties")
                    .toString()));

            ip = props.getProperty("IP");
            dsn = props.getProperty("DSN");
            username = props.getProperty("USERNAME");
            pwd = props.getProperty("PWD");
            drivermgr = props.getProperty("DRIVERMGR");
            Query = props.getProperty("Query");
            Channel = props.getProperty("CHANNEL");
            Context = props.getProperty("CONTEXT");
            Exten = props.getProperty("EXTEN");
            StartHour = Integer.parseInt(props.getProperty("StartHour"));
            EndHour = Integer.parseInt(props.getProperty("EndHour"));
	     LogPath=props.getProperty("LogPath");

            Exten = props.getProperty("EXTEN");

            Priority = Integer.parseInt(props.getProperty("PRIORITY"));
            SleepTime = Integer.parseInt(props.getProperty("SLEEPTIME"));
            CallerID = props.getProperty("CALLERID");

            ASTERISK_PORT = Integer.parseInt(props.getProperty("PORT"));
            ASTERISK_HOST = props.getProperty("HOST");
            ASTERISK_LOGINNAME = props.getProperty("LOGINNAME");
            ASTERISK_LOGINPWD = props.getProperty("LOGINPWD");

            objRequest.setChannel(Channel);
            objRequest.SetCallerId(CallerID);
            objRequest.setContext(Context);
            objRequest.setExten(Exten);
            objRequest.setPriority(Priority);
          objRequest.setLogPath(LogPath);
	     objRequest.setAsteriskHost(ASTERISK_HOST);
            objRequest.SetAsteriskPort(ASTERISK_PORT);
            objRequest.setAsteriskLogin(ASTERISK_LOGINNAME);
            objRequest.SetAsteriskPassword(ASTERISK_LOGINPWD);
            objRequest.setDriverMgr(drivermgr);
             objRequest.setUserName(username);
             objRequest.setPassword(pwd);
		 System.out.println("log path is equal to"+objRequest.getLogPath());

        } catch (Exception exception) {
           Log.writeLog("loadprop exception:" + exception.getMessage());
            return;
        }
    }

    private Connection  dbConn() throws IOException
	{
		while(true)
		{
			try
			{

                         System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd+" DRIVERMGR:"+drivermgr+" Query:"+Query+"Channel:"+Channel+"context:"+Context+"exten:"+Exten+"priority:"+Priority+"CallerId:"+CallerID+"asteriskPort:"+ASTERISK_PORT+"asteriskloginnname:"+ASTERISK_LOGINNAME+"asteriskLoginPwd:"+ASTERISK_LOGINPWD+"Starthour:"+StartHour+"endhour:"+EndHour);
  			 System.out.println("Calling Db");
                         Class.forName("com.mysql.jdbc.Driver");
 			 System.out.println("com.mysql.jdbc.Driver called");
			 System.out.println("password:"+pwd);
			 System.out.println("username:"+username);
                         System.out.println("DriveMgr:"+drivermgr);
                         
			 if( con==null || con.isClosed() )
                         {
				 con = DriverManager.getConnection(drivermgr, username, pwd);
                                 stmt = con.createStatement();
                                 stmtUpdate = con.createStatement();
					stmtcnt= con.createStatement();

	 			 System.out.println("cnnection established");
		         	 System.out.println("Database Connection established!");
			  }	
			else
			{
				System.out.println("Connection already established");
                                Thread.sleep(SleepTime);
			}
			objRequest.setConn(con);
			}
			catch(Exception e)
			{
				System.out.println("Exception:"+e.getMessage());
                                Log.writeLog("dbconn exception:" + e.getMessage());
				try {
					Thread.sleep(SleepTime);
				} catch (InterruptedException e1) {
					System.out.println(e1.getMessage());
				}
			}
                        return con;
		}
	}

    public void run() {
        while (true) {

            try {

                dbConn();
                String ID = "";
               
                System.out.println("query:" + Query);
                rsData = stmt.executeQuery(Query);
                
                SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date localDate = new Date();
                System.out.println("Datetime: " + localSimpleDateFormat.format(localDate));
                Log.writeLog("SendOBD CallDatetime" + localSimpleDateFormat.format(localDate));

                
                
                
                while (rsData.next()) {
                    try {

                        System.out.println("Process starting");
                        Log.writeLog("Process starting");
                        
                        hour = date.get(Calendar.HOUR_OF_DAY);
                        System.out.println("CurrentHour: " + hour + " and Starthour:" + StartHour + " and end hour" + EndHour);

                        objRequest.setAni(rsData.getString("msisdn"));
			objRequest.SetDialNumber(rsData.getString("ANI"));

                        objRequest.setDND(rsData.getString("in_dnd"));


                        
                        objRequest.setId(rsData.getString("id"));
                        
                        System.out.println("Sendingcall msisdn" + rsData.getString("msisdn"));
                        Log.writeLog("Sendingcall msisdn" + rsData.getString("msisdn"));
                        
                        ID = rsData.getString("id");
			  strANI=rsData.getString("ANI");
  			strCircle=rsData.getString("circle");
			strOperator=rsData.getString("operator");
			strRealDnis=rsData.getString("realdnis");


                     
                        objRequest.setLogPath(LogPath);
                        System.out.println("Sendingcall id" + rsData.getString("id"));
			System.out.println("log path is equal to:" + objRequest.getLogPath());
                        Log.writeLog("Sendingcall msisdn" + rsData.getString("id"));

                         if(objRequest.getDND().equals("0"))
                        {
                             System.out.println("calling 54 not dnd ip");
                            Log.writeLog("calling 54 not dnd ip");
                            objRequest.setSipHeader("<sip:9303706@10.50.129.54>");
                             
                        }
                        else
                        {
                            System.out.println(" calling 50 dnd ip");
                            Log.writeLog(" calling 50 dnd ip");
                            objRequest.setSipHeader("<sip:66785800@10.50.129.50>");

                        }

			if(strRealDnis.equals("1409580672"))
			  {
                            objRequest.setAni("5"+rsData.getString("msisdn"));
				objRequest.setChannel("SIP/TG/" + objRequest.getAni());
			  }
                      else if(strRealDnis.equals("1409580673"))
			  {
                            objRequest.setAni("6"+rsData.getString("msisdn"));
				objRequest.setChannel("SIP/TG/" + objRequest.getAni());
			  }
			 else
			 {
                         objRequest.setChannel(Channel + objRequest.getAni());
			 }

                        objRequest.setContext(Context);
                        objRequest.setExten(Exten);
                        objRequest.setPriority(Priority);
                        System.out.println("Sendingcall channel" + objRequest.getChannel());
                        Log.writeLog("Sendingcall msisdn channel" +  objRequest.getChannel());

			 rscnt = stmtcnt.executeQuery("select * from tbl_tata_pushobd where id='"+ID+"' and status=1");
			  if(rscnt.next())
			  {
				//System.out.println("MSISDN already in process");
				System.out.println("Call already Sent");
			  }	
			else
			{		
                        
			 int i= stmtUpdate.executeUpdate("update tbl_tata_pushobd set status=1,obd_retry_date_time=now(),obd_sent_date_time=now(),obd_diff=TIMESTAMPDIFF(second, date_time, now()) where id=" + ID);
                        System.out.println("update tbl_tata_pushobd set status=1,obd_sent_date_time=now(),obd_retry_date_time=now(),obd_diff=TIMESTAMPDIFF(second, date_time, now()) where id=" + ID);
                        Log.writeLog("Firing update query:update tbl_tata_pushobd set status=1,obd_retry_date_time=now(),obd_sent_date_time=now(),obd_diff=TIMESTAMPDIFF(second, date_time, now()) where id=" + ID);
                        
                        
                            Log.writeLog("Firing update query status=" + i); 
                            System.out.println("update query:result=" + i);
                        
			   //Initiate Outbond Call
			   creatFile();
                           System.out.println("Call Sent"); 
                         }
                    
                        Thread.sleep(SleepTime);

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }

                }
                
                
               // managerConnection.logoff();
               Thread.sleep(SleepTime);

            } catch (Exception ex) {
                try {
                    Thread.sleep(SleepTime);
                } catch (InterruptedException ex1) {
                    System.out.println("Male base exception: " + ex.getMessage());

                }
                System.out.println("Male base exception1: " + ex.getMessage());

            }
        }
    }
 public String creatFile() throws IOException {
                FileWriter fw = null;
		String retrn = "";
		String path = "/var/spool/asterisk/tmp/";
		String move_path="/var/spool/asterisk/outgoing/";
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy.MM.dd.HH.mm.ss");

		String filename = format.format(new java.util.Date()) +"TISCON"+ "_"
				+ objRequest.getDialNumber();
                System.out.println("Filepath:"+filename);
                
		try {

			File dynFile = new File(path, filename + ".call");

			boolean fileCreated = dynFile.createNewFile();
			if (fileCreated) {
				fw = new FileWriter(dynFile);

				fw.write("Channel: " + objRequest.getChannel());
				fw.write("\r\n");
				fw.write("MaxRetries: 2");
				fw.write("\r\n");
				fw.write("RetryTime: 300");
				fw.write("\r\n");
				fw.write("WaitTime: 35");
				fw.write("\r\n");
				fw.write("Context: "+objRequest.getContext());
				fw.write("\r\n");
				fw.write("Extension: "+objRequest.getExten());
				fw.write("\r\n");
                                fw.write("Priority: 1");
				fw.write("\r\n");
				fw.write("SetVar: dst="+objRequest.getDialNumber());
				fw.write("\r\n");
				fw.write("SetVar: realdnis=" + strRealDnis);
                            fw.write("\r\n");
				fw.write("SetVar: ID=" + objRequest.getId());
                                
                            fw.write("\r\n");
				fw.write("SetVar: CallFrom=TISCON");

				fw.write("\r\n");
				fw.write("SetVar: operator="+strOperator);

				fw.write("\r\n");
				fw.write("SetVar: circle="+strCircle);
                          
                            if(objRequest.getChannel().toString().contains("TATAHUL"))
				{
	                            fw.write("\r\n");
					fw.write("SetVar: __SIPADDHEADER01=P-Preferred-Identity:" + objRequest.getSipHeader());
				}

				fw.flush();
				System.out.println("send call file data:"+fw);
				retrn = "success";

			}

		} catch (Exception e) {
			e.printStackTrace();
			retrn = "notsuccess";

		} finally {

			if (fw != null) {
				 
					 fw.close();
	                   // Runtime.getRuntime().exec("chown asterisk:asterisk "+path + filename + ".call");
	                    Runtime.getRuntime().exec("mv "+path + filename + ".call "+move_path + filename + ".call");

	                }
				
			
		}
		return retrn;
	}

     public static void main(String args[]) {
        CallTiscon crm = new CallTiscon();
        crm.start();
    }
    
    
}

    

