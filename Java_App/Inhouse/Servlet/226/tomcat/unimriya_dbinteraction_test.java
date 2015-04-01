/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.sql.DataSource;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ 
/*     */ public class unimriya_dbinteraction_test extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static DataSource ds;
/*     */ 	static Logger log = Logger.getLogger(unimriya_dbinteraction_test.class.getName());
/*     */   public void init()
/*     */   {
/*     */     try
/*     */     {
/*  44 */       InitialContext localInitialContext = new InitialContext();
/*  45 */       Context localContext = (Context)localInitialContext.lookup("java:comp/env");
/*     */ 
/*  48 */       ds = (DataSource)localContext.lookup("jdbc/uninor_manchala");
				String prefix = getServletContext().getRealPath("/");
/*  54 */       String file = getInitParameter("log4j-init-file");
					if (file != null) {
/*  57 */        		 PropertyConfigurator.configure(prefix + file);
/*  58 */        		 System.out.println("Log4J Logging started: " + prefix + file);
/*     */       		}
/*     */       	else {
/*  61 */        		 System.out.println("Log4J Is not configured for your Application: " + prefix + file);
/*     */      		 }
/*     */     } catch (Exception localException) {
/*  50 */       System.out.println("error is" + localException.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
			  log.info("----got request unimriya_dbinteraction--------");
			  
/*  59 */     if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1")) {
/*  60 */       paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  62 */     paramHttpServletResponse.setContentType("application/ecmascript");
/*     */ 
/*  65 */     String str1 = paramHttpServletRequest.getParameter("PROCEDURE");
/*  66 */     String str2 = paramHttpServletRequest.getParameter("INTOKEN");
/*  67 */     String str3 = paramHttpServletRequest.getParameter("OUTTOKEN");
				log.info("PROCEDURE:" + str1);
/*  86 */     	log.info("Inparameter:" + str2);
/*  87 */     	log.info("Outparameter:" + str3);
/*  68 */     int i = Integer.parseInt(str2);
/*  69 */     int j = Integer.parseInt(str3);
/*     */ 
/*  74 */     String[] arrayOfString1 = new String[i];
/*     */ 
/*  76 */     String[] arrayOfString2 = new String[j];
/*  77 */     String str4 = str1 + "(";
/*  78 */     for (int k = 0; k < i; k++)
/*     */     {
/*  80 */       arrayOfString1[k] = paramHttpServletRequest.getParameter("INPARAM[" + k + "]");
/*     */ 		log.info("Parameters Values:-"+arrayOfString1[k]);
/*  82 */       str4 = str4 + "?";
/*  83 */       if (k < i - 1)
/*  84 */         str4 = str4 + ",";
/*     */     }
/*  86 */     if (j != 0)
/*     */     {
/*  88 */       str4 = str4 + ",?";
/*     */     }
/*  90 */     str4 = str4 + ")";
/*     */ 
/*  92 */     paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
/*  93 */     Connection localConnection = null;
/*  94 */     CallableStatement localCallableStatement = null;
/*  95 */     PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
/*  96 */     String str5 = null;
/*  97 */     Object localObject1 = null; Object localObject2 = null;
/*     */     try
/*     */     {
/* 100 */       localConnection = ds.getConnection();
/*     */ 
/* 102 */       if (localConnection != null)
/*     */       {
					log.info("Call procedure isss:="+str4);
/* 104 */         localCallableStatement = localConnection.prepareCall("{call " + str4 + "}");
					
/* 105 */         for (int m = 0; m < i; m++)
/*     */         {
/* 107 */           int n = m + 1;
					log.info("value of n is:"+n);
/* 108 */           localCallableStatement.setString(n, arrayOfString1[m]);
/*     */         }
/*     */ 
/* 112 */         if (j != 0)
/*     */         {
/* 114 */           j = i + 1;
					log.info("value of j is:"+j);
/* 115 */           localCallableStatement.registerOutParameter(j, 12);
/*     */ 
/* 117 */           j = 1;
/*     */         }
/* 119 */         localCallableStatement.execute();
/* 120 */         if (j != 0)
/*     */         {
/* 122 */           str5 = localCallableStatement.getString(i + 1);
					log.info("get string isss:"+str5);
/* 123 */           String[] arrayOfString3 = str5.split("#");
/* 124 */           String str6 = "out_string.length=" + arrayOfString3.length;
/* 125 */           int i1 = 0;
/* 126 */           while (i1 < arrayOfString3.length) {
/* 127 */             str6 = str6 + ";" + "out_string" + "[" + i1 + "]" + "=" + "'" + arrayOfString3[i1].trim() + "'";
/*     */ 
/* 129 */             i1++;
/*     */           }
					log.info("OUTSTRING is : " + str6);
/* 131 */           localPrintWriter.println(str6);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception localSQLException4) {
				log.error("Error occured" + localSQLException4.getMessage());
/* 136 */       localSQLException4.printStackTrace();
/*     */     } finally {
/* 138 */       localPrintWriter.close();
/* 139 */       localPrintWriter = null;
/* 140 */       str5 = null;
/* 141 */       localObject2 = null;
/* 142 */       if (localCallableStatement != null) {
/*     */         try {
/* 144 */           localCallableStatement.close();
/*     */         }
/*     */         catch (SQLException localSQLException5) {
/*     */         }
/* 148 */         localCallableStatement = null;
/*     */       }
/* 150 */       if (localConnection != null) {
/*     */         try {
/* 152 */           localConnection.close();
/*     */         }
/*     */         catch (SQLException localSQLException6) {
/*     */         }
/* 156 */         localConnection = null;
/*     */       }
				log.info("request completed");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 174 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 188 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */ 
/*     */   public String getServletInfo()
/*     */   {
/* 195 */     return "Short description";
/*     */   }
/*     */ }
