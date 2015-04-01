/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.URLDecoder;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.sql.DataSource;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ 
/*     */ public class Encryptor extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static DataSource ds;
/*  27 */   static Logger log = Logger.getLogger(Encryptor.class.getName());
/*     */ 
/*     */   public void init(ServletConfig config)
/*     */     throws ServletException
/*     */   {
/*     */     try
/*     */     {
/*  34 */       Context initCtx = new InitialContext();
/*  35 */       Context envCtx = (Context)initCtx.lookup("java:comp/env");
/*  36 */       ds = (DataSource)envCtx.lookup("jdbc/airtel_radio");
/*  37 */       String prefix = getServletContext().getRealPath("/");
/*  38 */       String file = getInitParameter("log4j-init-file");
/*  39 */       if (file != null) {
/*  40 */         PropertyConfigurator.configure(prefix + file);
/*  41 */         System.out.println("Log4J Logging started: " + prefix + file);
/*     */       } else {
/*  43 */         System.out
/*  44 */           .println("Log4J Is not configured for your Application: " + 
/*  45 */           prefix + file);
/*     */       }
/*     */     } catch (Exception e) {
/*  48 */       System.out.println("error is" + e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/*  55 */     log.info("----got request unimriya_dbinteraction--------");
/*  56 */     if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1")) {
/*  57 */       paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  59 */     paramHttpServletResponse.setContentType("application/ecmascript");
/*  60 */     String str1 = paramHttpServletRequest.getParameter("msisdn");
/*  61 */     String str2 = paramHttpServletRequest.getParameter("srvkey");
/*  62 */     String str3 = paramHttpServletRequest.getParameter("ChannelName");
/*  63 */     String str4 = paramHttpServletRequest.getParameter("SourceChannel");
/*  64 */     log.info("Recieved MSISDN : " + str1);
/*  65 */     log.info("Recieved srvkey : " + str2);
/*  66 */     log.info("Recieved ChannelName : " + str3);
/*  67 */     log.info("Recieved SourceChannel : " + str4);
/*  68 */     log.info("STRING 1 is : " + str1);
/*  69 */     str1 = str1.replaceAll("/", "%2F");
/*  70 */     log.info("Replaced slash" + str1);
/*  71 */     str1 = str1.replaceAll("=", "%3D");
/*  72 */     log.info("Replaced equal" + str1);
/*  73 */     str1 = str1.replaceAll("\\+", "%2B");
/*  74 */     log.info("Replaced plus" + str1);
/*     */ 
/*  76 */     log.info("NOW STRING 1 is : " + str1);
/*  77 */     String key = "vj5as2dL0Vubt4QW1sroZPE3XrhN9mD";
/*     */ 
/*  79 */     EncryptionUtil encryption = new EncryptionUtil();
/*  80 */     String encValue = str1;
/*  81 */     log.info("Encrypted msisdn = " + encValue);
/*     */ 
/*  84 */     TripleDESCrypt des = new TripleDESCrypt(key);
/*  85 */     String decparams = null;
/*     */     try {
/*  87 */       decparams = des.decrypt(URLDecoder.decode(encValue));
/*     */     }
/*     */     catch (Exception localException1) {
/*     */     }
/*  91 */     HashMap map = encryption.decrypt(URLDecoder.decode(decparams));
/*  92 */     String msisdn = (String)(String)map.get("msisdn");
/*  93 */     log.info("Decrypted msisdn = " + msisdn);
/*  94 */     paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
/*  95 */     PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
/*  96 */     String amount = "";
/*  97 */     String pid = "";
/*  98 */     int chk = 0;
/*  99 */     if (str2.equalsIgnoreCase("1508730")) {
/* 100 */       amount = "10";
/* 101 */       pid = "6";
/* 102 */     } else if (str2.equalsIgnoreCase("1508709")) {
/* 103 */       amount = "2";
/* 104 */       pid = "20";
		} else if (str2.equalsIgnoreCase("1508786")) {
/* 103 */       amount = "30";
/* 104 */       pid = "24";
/*     */     } else {
/* 106 */       log.info("Invalid srvkey");
/* 107 */       chk = 1;
/*     */     }
/* 109 */     log.info("Amount : " + amount);
/* 110 */     log.info("Plan ID : " + pid);
/* 111 */     if (chk == 1) {
/* 112 */       chk = 0;
/* 113 */       localPrintWriter.println("INVALID REQUEST");
/*     */     } else {
/* 115 */       String[] arrayOfString1 = new String[7];
			
/* 116 */       String str5 = "airtel_radio.RADIO_SUB(";
/* 117 */       arrayOfString1[0] = msisdn;
/* 118 */       arrayOfString1[1] = "01";
/* 119 */       arrayOfString1[2] = "WEB_AL_HUNGAMA";
/* 120 */       arrayOfString1[3] = "546469";
/* 121 */       arrayOfString1[4] = amount;
/* 122 */       arrayOfString1[5] = "1501";
/* 123 */       arrayOfString1[6] = pid;
/* 124 */       for (int k = 0; k < 7; k++) {
/* 125 */         str5 = str5 + "?";
/* 126 */         if (k < 6)
/* 127 */           str5 = str5 + ",";
/*     */       }
/* 129 */       str5 = str5 + ")";
/*     */ 
/* 131 */       String[] arrayOfString2 = new String[2];
/* 132 */       String str9 = "airtel_radio.RADIO_QUERY_Encr(";
/* 133 */       arrayOfString2[0] = msisdn;
/* 134 */       arrayOfString2[1] = "1";
/* 135 */       for (int k = 0; k < 2; k++) {
/* 136 */         str9 = str9 + "?";
/* 137 */         if (k < 1)
/* 138 */           str9 = str9 + ",";
/*     */       }
/* 140 */       str9 = str9 + ",?";
/* 141 */       str9 = str9 + ")";
/*     */ 
/* 143 */       Connection localConnection = null;
/* 144 */       CallableStatement localCallableStatement = null;
/*     */ 
/* 146 */       String str6 = null;
/* 147 */       Object localObject1 = null;
/* 148 */       Object localObject2 = null;
/*     */ 
/* 150 */       label1396: 
/*     */       try { localConnection = ds.getConnection();
/* 151 */         if (localConnection != null) {
/* 152 */           localCallableStatement = localConnection.prepareCall("{call " + str9 + "}");
/* 153 */           for (int m = 0; m < 2; m++) {
/* 154 */             int n = m + 1;
/* 155 */             localCallableStatement.setString(n, arrayOfString2[m]);
/*     */           }
/* 157 */           localCallableStatement.execute();
/* 158 */           localCallableStatement.registerOutParameter(3, 12);
/* 159 */           String str10 = localCallableStatement.getString(3);
/* 160 */           log.info("get string isss:" + str10);
/* 161 */           String[] arrayOfString3 = str10.split("#");
/* 162 */           if (arrayOfString3[0].equalsIgnoreCase("-1"))
/*     */           {
/* 164 */             localCallableStatement = localConnection
/* 165 */               .prepareCall("{call " + str5 + "}");
/* 166 */             for (int m = 0; m < 7; m++) {
/* 167 */               int n = m + 1;
/* 168 */               localCallableStatement.setString(n, arrayOfString1[m]);
/*     */             }
/* 170 */             localCallableStatement.execute();
/* 171 */             localCallableStatement = localConnection.prepareCall("{call " + str9 + "}");
/* 172 */             for (int m = 0; m < 2; m++) {
/* 173 */               int n = m + 1;
/* 174 */               localCallableStatement.setString(n, arrayOfString2[m]);
/*     */             }
/* 176 */             localCallableStatement.execute();
/* 177 */             localCallableStatement.registerOutParameter(3, 12);
/* 178 */             str10 = localCallableStatement.getString(3);
/* 179 */             log.info("get string isss:" + str10);
/* 180 */             String[] arrayOfString4 = str10.split("#");
/* 181 */             if (arrayOfString4[0].equalsIgnoreCase("-1"))
/*     */             {
/* 183 */               log.info("Response Sent :  FAILED");
/* 184 */               localPrintWriter.println(arrayOfString4[1]); break label1396;
/*     */             }
/*     */ 
/* 188 */             log.info("Response Sent : SUCCESS");
/* 189 */             localPrintWriter.println("SUCCESS"); break label1396;
/*     */           }
/*     */ 
/* 194 */           log.info("Response Sent : ALS");
/* 195 */           localPrintWriter.println("ALS");
/*     */         }
/*     */       } catch (Exception localSQLException4)
/*     */       {
/* 199 */         localSQLException4.printStackTrace();
/*     */       } finally {
/* 201 */         localPrintWriter.close();
/* 202 */         localPrintWriter = null;
/* 203 */         localObject2 = null;
/* 204 */         if (localCallableStatement != null) {
/*     */           try {
/* 206 */             localCallableStatement.close();
/*     */           } catch (SQLException localSQLException) {
/*     */           }
/* 209 */           localCallableStatement = null;
/*     */         }
/* 211 */         if (localConnection != null) {
/*     */           try {
/* 213 */             localConnection.close();
/*     */           } catch (SQLException localSQLException1) {
/*     */           }
/* 216 */           localConnection = null;
/*     */         }
/* 218 */         log.info("Requested Completed in finally");
/*     */       }
/*     */     }
/* 221 */     log.info("Requested Completed");
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 227 */     processRequest(request, response);
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 233 */     processRequest(request, response);
/*     */   }
/*     */ }

/* Location:           C:\Users\admin\Desktop\
 * Qualified Name:     Encryptor
 * JD-Core Version:    0.6.0
 */