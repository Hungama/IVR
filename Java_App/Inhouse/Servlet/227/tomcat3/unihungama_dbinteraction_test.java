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
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

/*     */
/*     */ public class unihungama_dbinteraction_test extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static DataSource ds;
  static Logger log = Logger.getLogger(uniradio_dbinteraction.class.getName());
/*     */
/*     */   public void init()
/*     */   {
/*     */     try
/*     */     {
/*  24 */       InitialContext localInitialContext = new InitialContext();
/*  25 */       Context localContext = (Context)localInitialContext.lookup("java:comp/env");
/*     */
/*  27 */       ds = (DataSource)localContext.lookup("jdbc/uninor_hungama");
 String prefix =  getServletContext().getRealPath("/");
        String file = getInitParameter("log4j-init-file");
         if(file != null){
		         PropertyConfigurator.configure(prefix+file);
		         System.out.println("Log4J Logging started: " + prefix+file);
		        }
		        else{
		         System.out.println("Log4J Is not configured for your Application: " + prefix + file);
        }

/*     */     } catch (Exception localException) {
/*  29 */       System.out.println("error is" + localException.toString());
/*     */     }
/*     */   }
/*     */
/*     */   protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
	 log.info("-----Got Request uniradio_dbinteraction------");
/*  36 */     if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1")) {
/*  37 */       paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  39 */     paramHttpServletResponse.setContentType("application/ecmascript");
/*     */
/*  41 */     String str1 = paramHttpServletRequest.getParameter("PROCEDURE");
/*  42 */     String str2 = paramHttpServletRequest.getParameter("INTOKEN");
/*  43 */     String str3 = paramHttpServletRequest.getParameter("OUTTOKEN");
/*  44 */     int i = Integer.parseInt(str2);
/*  45 */     int j = Integer.parseInt(str3);
 log.info("---- PARAMETERS ARE ------- ");
     log.info("PROCEDURE IS : " + str1);
     log.info("INTOKEN IS : " + str2);
     log.info("OUTTOKEN IS : " + str3);
/*     */
/*  47 */     String[] arrayOfString1 = new String[i];
/*     */
/*  49 */     String[] arrayOfString2 = new String[j];
/*  50 */     String str4 = str1 + "(";
/*  51 */     for (int k = 0; k < i; k++)
/*     */     {
/*  53 */       arrayOfString1[k] = paramHttpServletRequest.getParameter("INPARAM[" + k + "]");
/*     */
/*  55 */       str4 = str4 + "?";
/*  56 */       if (k < i - 1)
/*  57 */         str4 = str4 + ",";
/*     */     }
/*  59 */     if (j != 0)
/*     */     {
/*  61 */       str4 = str4 + ",?";
/*     */     }
/*  63 */     str4 = str4 + ")";
/*     */
/*  65 */     paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
/*  66 */     Connection localConnection = null;
/*  67 */     CallableStatement localCallableStatement = null;
/*  68 */     PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
/*  69 */     String str5 = null;
/*  70 */     Object localObject1 = null; Object localObject2 = null;
/*     */     try
/*     */     {
/*  73 */       localConnection = ds.getConnection();
/*     */
/*  75 */       if (localConnection != null)
/*     */       {
/*  77 */         localCallableStatement = localConnection.prepareCall("{call " + str4 + "}");
					 log.info("call[" + str4 + "] ");
/*  78 */         for (int m = 0; m < i; m++)
/*     */         {
/*  80 */           int n = m + 1;
/*  81 */           localCallableStatement.setString(n, arrayOfString1[m]);
/*     */         }
/*     */
/*  84 */         if (j != 0)
/*     */         {
/*  86 */           j = i + 1;
/*  87 */           localCallableStatement.registerOutParameter(j, 12);
/*     */
/*  89 */           j = 1;
/*     */         }
/*  91 */         localCallableStatement.execute();
/*  92 */         if (j != 0)
/*     */         {
/*  94 */           str5 = localCallableStatement.getString(i + 1);
log.info("str5 value[" + str5 + "] ");
/*  95 */           String[] arrayOfString3 = str5.split("#");
/*  96 */           String str6 = "out_string.length=" + arrayOfString3.length;
/*  97 */           int i1 = 0;
/*  98 */           while (i1 < arrayOfString3.length) {
/*  99 */             str6 = str6 + ";" + "out_string" + "[" + i1 + "]" + "=" + "'" + arrayOfString3[i1].trim() + "'";
/*     */				log.info("str6 value[" + str6 + "] ");
/* 101 */             i1++;
/*     */           }
/* 103 */           localPrintWriter.println(str6);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception localSQLException4) {
/* 108 */      // localException.printStackTrace();
/*     */     } finally {
/* 110 */       localPrintWriter.close();
/* 111 */       localPrintWriter = null;
/* 112 */       str5 = null;
/* 113 */       localObject2 = null;
/* 114 */       if (localCallableStatement != null) {
/*     */         try {
/* 116 */           localCallableStatement.close();
/*     */         }
/*     */         catch (SQLException localSQLException5) {
/* 119 */           System.out.println("PROCEDURE Name is " + str1);
/*     */         }
/* 121 */         localCallableStatement = null;
/*     */       }
/* 123 */       if (localConnection != null) {
/*     */         try {
/* 125 */           localConnection.close();
/*     */         }
/*     */         catch (SQLException localSQLException6) {
/*     */         }
/* 129 */         localConnection = null;
/*     */       }
/*     */     }
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 137 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 143 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 148 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\chandra.mer\Desktop\LOG4J\
 * Qualified Name:     unihungama_dbinteraction
 * JD-Core Version:    0.6.0
 */