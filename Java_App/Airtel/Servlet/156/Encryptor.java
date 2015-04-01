 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.net.URLDecoder;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import javax.naming.Context;
 import javax.naming.InitialContext;
 import javax.servlet.ServletConfig;
 import javax.servlet.ServletContext;
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.sql.DataSource;
 import org.apache.log4j.Logger;
 import org.apache.log4j.PropertyConfigurator;
 
 public class Encryptor extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   static DataSource ds;
   static Logger log = Logger.getLogger(Encryptor.class.getName());
 
   public void init(ServletConfig config)
     throws ServletException
   {
     try
     {
       Context initCtx = new InitialContext();
       Context envCtx = (Context)initCtx.lookup("java:comp/env");
       ds = (DataSource)envCtx.lookup("jdbc/airtel_radio");
       String prefix = getServletContext().getRealPath("/");
       String file = getInitParameter("log4j-init-file");
       if (file != null) {
         PropertyConfigurator.configure(prefix + file);
         System.out.println("Log4J Logging started: " + prefix + file);
       } else {
         System.out
           .println("Log4J Is not configured for your Application: " + 
           prefix + file);
       }
     } catch (Exception e) {
       System.out.println("error is" + e.toString());
     }
   }
 
   protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
     throws ServletException, IOException
   {
     log.info("----got request unimriya_dbinteraction--------");
     if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1")) {
       paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
     }
     paramHttpServletResponse.setContentType("application/ecmascript");
     String str1 = paramHttpServletRequest.getParameter("msisdn");
     String str2 = paramHttpServletRequest.getParameter("srvkey");
     String str3 = paramHttpServletRequest.getParameter("ChannelName");
     String str4 = paramHttpServletRequest.getParameter("SourceChannel");
     log.info("Recieved MSISDN : " + str1);
     log.info("Recieved srvkey : " + str2);
     log.info("Recieved ChannelName : " + str3);
     log.info("Recieved SourceChannel : " + str4);
     log.info("STRING 1 is : " + str1);
     str1 = str1.replaceAll("/", "%2F");
     log.info("Replaced slash" + str1);
     str1 = str1.replaceAll("=", "%3D");
     log.info("Replaced equal" + str1);
     str1 = str1.replaceAll("\\+", "%2B");
     log.info("Replaced plus" + str1);
 
     log.info("NOW STRING 1 is : " + str1);
     String key = "vj5as2dL0Vubt4QW1sroZPE3XrhN9mD";
 
     EncryptionUtil encryption = new EncryptionUtil();
     String encValue = str1;
     log.info("Encrypted msisdn = " + encValue);
 
     TripleDESCrypt des = new TripleDESCrypt(key);
     String decparams = null;
     try {
       decparams = des.decrypt(URLDecoder.decode(encValue));
     }
     catch (Exception localException1) {
     }
     HashMap map = encryption.decrypt(URLDecoder.decode(decparams));
     String msisdn = (String)(String)map.get("msisdn");
     log.info("Decrypted msisdn = " + msisdn);
     paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
     PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
     String amount = "";
     String pid = "";
     int chk = 0;
		     if (str2.equalsIgnoreCase("1508730")) {
		       amount = "10";
		       pid = "6";
		     } else if (str2.equalsIgnoreCase("1508709")) {
		    	 	amount = "2";
		       		pid = "20";
		     }else if (str2.equalsIgnoreCase("1508786")) {
		       amount = "30";
		       pid = "24";
		     } else {
		       log.info("Invalid srvkey");
		       chk = 1;
		     }
			     log.info("Amount : " + amount);
			     log.info("Plan ID : " + pid);
     
			     if (chk == 1) 
			     {
			    	 chk = 0;
			    	 	localPrintWriter.println("INVALID REQUEST");
			     }
			     else 
			     {
					       String[] arrayOfString1 = new String[7];
						
					       String str5 = "airtel_radio.RADIO_SUB(";
					       arrayOfString1[0] = msisdn;
					       arrayOfString1[1] = "01";
					       arrayOfString1[2] = "WEB_AL_HUNGAMA";
					       arrayOfString1[3] = "546469";
					       arrayOfString1[4] = amount;
					       arrayOfString1[5] = "1501";
					       arrayOfString1[6] = pid;
					       for (int k = 0; k < 7; k++) {
					         str5 = str5 + "?";
					         if (k < 6)
					           str5 = str5 + ",";
					       }
					       str5 = str5 + ")";
 
					       String[] arrayOfString2 = new String[2];
					       String str9 = "airtel_radio.RADIO_QUERY_Encr(";
					       arrayOfString2[0] = msisdn;
					       arrayOfString2[1] = "1";
					       for (int k = 0; k < 2; k++) {
					         str9 = str9 + "?";
					         if (k < 1)
					           str9 = str9 + ",";
					       }
					       str9 = str9 + ",?";
					       str9 = str9 + ")";
 
					       Connection localConnection = null;
					       CallableStatement localCallableStatement = null;
					 
					       String str6 = null;
					       Object localObject1 = null;
					       Object localObject2 = null;
 
					       label1396: 
					    try 
					     	{ 
					    		localConnection = ds.getConnection();
					    	   		if (localConnection != null) 
					    	   		{
					    	   				localCallableStatement = localConnection.prepareCall("{call " + str9 + "}");
					    	   					for (int m = 0; m < 2; m++) 
					    	   					   {
					    	   							int n = m + 1;
					    	   								localCallableStatement.setString(n, arrayOfString2[m]);
					    	   					   }
									           localCallableStatement.execute();
									           localCallableStatement.registerOutParameter(3, 12);
									           String str10 = localCallableStatement.getString(3);
									           log.info("get string isss:" + str10);
									           String[] arrayOfString3 = str10.split("#");
           
								           if (arrayOfString3[0].equalsIgnoreCase("-1"))
								             {
										        	   localCallableStatement = localConnection.prepareCall("{call " + str5 + "}");
										        	   for (int m = 0; m < 7; m++) 
										        	   {
										        		   int n = m + 1;
										        		   localCallableStatement.setString(n, arrayOfString1[m]);
										        	   }
										               localCallableStatement.execute();
								             
											             try
											             {
											            	 Thread.sleep(8000);
											             }
											             catch(Exception eee)
											             {
											            	 eee.printStackTrace();
											             }
								             
											             localCallableStatement = localConnection.prepareCall("{call " + str9 + "}");
											             for (int m = 0; m < 2; m++) 
											             {
											            	 int n = m + 1;
											            	 localCallableStatement.setString(n, arrayOfString2[m]);
											             }
											             localCallableStatement.execute();
											             localCallableStatement.registerOutParameter(3, 12);
											             str10 = localCallableStatement.getString(3);
											             log.info("get string isss:" + str10);
											             String[] arrayOfString4 = str10.split("#");
										            if (arrayOfString4[0].equalsIgnoreCase("-1"))
										            	{
										            		log.info("Response Sent :  FAILED");
										            			localPrintWriter.println(arrayOfString4[1]); break label1396;
										            	}
										            else if (arrayOfString4[0].equalsIgnoreCase("1"))
									            	{
									            		log.info("Response Sent :  SUCCESS");
									            		localPrintWriter.println("SUCCESS"); break label1396;
									            	}
 
										            log.info("Response Sent : RIP");
										            localPrintWriter.println("RIP"); break label1396;
								             }
 
								           log.info("Response Sent : ALS");
								           localPrintWriter.println("ALS");
					    	   		}
					     	}
					       catch (Exception localSQLException4)
					       	{
					    	   	localSQLException4.printStackTrace();
					       	}
					       finally 
					       {
						         localPrintWriter.close();
						         localPrintWriter = null;
						         localObject2 = null;
						         if (localCallableStatement != null) 
						         {
							           try 
							           {
							             localCallableStatement.close();
							           }
							           catch (SQLException localSQLException) 
							           {
							           }
							           		localCallableStatement = null;
						         	}
						         if (localConnection != null) 
						         {
						        	 try 
						        	 {
						        		 localConnection.close();
						        	 } catch (SQLException localSQLException1)
						        	 {
						        	 }
						        	 	localConnection = null;
						         }
						         log.info("Requested Completed in finally");
					       }
			     }
			     	log.info("Requested Completed");
   		}
 
			   protected void doGet(HttpServletRequest request, HttpServletResponse response)
			     throws ServletException, IOException
			   {
			     processRequest(request, response);
			   }
			 
			   protected void doPost(HttpServletRequest request, HttpServletResponse response)
			     throws ServletException, IOException
			   {
			     processRequest(request, response);
			   }
 }




