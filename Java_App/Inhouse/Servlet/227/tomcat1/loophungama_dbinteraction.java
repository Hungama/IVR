/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.sql.DataSource;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */
/*     */ public class loophungama_dbinteraction extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  24 */   private static final String INPARAM = null;
/*     */   static DataSource ds;
/*  26 */   static Logger log = Logger.getLogger(loophungama_dbinteraction.class.getName());
/*     */
/*     */   public void init()
/*     */   {
/*     */     try
/*     */     {
/*  32 */       InitialContext localInitialContext = new InitialContext();
/*  33 */       Context localContext = (Context)localInitialContext.lookup("java:comp/env");
/*     */
/*  35 */       ds = (DataSource)localContext.lookup("jdbc/loop_hungama");
/*  36 */       String prefix = getServletContext().getRealPath("/");
/*  37 */       String file = getInitParameter("log4j-init-file");
/*  38 */       if (file != null) {
/*  39 */         PropertyConfigurator.configure(prefix + file);
/*  40 */         System.out.println("Log4J Logging started: " + prefix + file);
/*     */       }
/*     */       else {
/*  43 */         System.out.println("Log4J Is not configured for your Application: " + prefix + file);
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {
/*  47 */       System.out.println("error is" + localException.toString());
/*     */     }
/*     */   }
/*     */
/*     */   protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/*  54 */     log.info("-----Got Request uniradio_dbinteraction------");
/*  55 */     if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1")) {
/*  56 */       paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  58 */     paramHttpServletResponse.setContentType("application/ecmascript");
/*     */
/*  60 */     String str1 = paramHttpServletRequest.getParameter("PROCEDURE");
/*  61 */     String str2 = paramHttpServletRequest.getParameter("INTOKEN");
/*  62 */     String str3 = paramHttpServletRequest.getParameter("OUTTOKEN");
/*  63 */     int i = Integer.parseInt(str2);
/*  64 */     int j = Integer.parseInt(str3);
/*  65 */     log.info("---- PARAMETERS ARE ------- ");
/*  66 */     log.info("PROCEDURE IS : " + str1);
/*  67 */     log.info("INTOKEN IS : " + str2);
/*  68 */     log.info("OUTTOKEN IS : " + str3);
/*     */
/*  70 */     String[] arrayOfString1 = new String[i];
/*     */
/*  72 */     String[] arrayOfString2 = new String[j];
/*  73 */     String str4 = str1 + "(";
/*  74 */     for (int k = 0; k < i; k++)
/*     */     {
/*  76 */       arrayOfString1[k] = paramHttpServletRequest.getParameter("INPARAM[" + k + "]");
/*     */
/*  78 */       log.info("arrayOfString1 parametersss IS :" + arrayOfString1[k]);
/*  79 */       str4 = str4 + "?";
/*  80 */       if (k < i - 1)
/*  81 */         str4 = str4 + ",";
/*  82 */       log.info("str4 parametersss IS :" + str4);
/*     */     }
/*  84 */     if (j != 0)
/*     */     {
/*  86 */       str4 = str4 + ",?";
/*     */     }
/*  88 */     str4 = str4 + ")";
/*     */
/*  90 */     log.info("str4 IS : " + str4);
/*  91 */     paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
/*  92 */     Connection localConnection = null;
/*  93 */     CallableStatement localCallableStatement = null;
/*  94 */     PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
/*  95 */     String str5 = null;
/*  96 */     Object localObject1 = null; Object localObject2 = null;
/*     */     try
/*     */     {
/*  99 */       localConnection = ds.getConnection();
/*     */
/* 101 */       if (localConnection != null)
/*     */       {
/* 103 */         localCallableStatement = localConnection.prepareCall("{call " + str4 + "}");
/* 104 */         log.info("call[" + str4 + "] ");
/* 105 */         for (int m = 0; m < i; m++)
/*     */         {
/* 107 */           int n = m + 1;
/* 108 */           localCallableStatement.setString(n, arrayOfString1[m]);
/*     */         }
/*     */
/* 111 */         if (j != 0)
/*     */         {
/* 113 */           j = i + 1;
/* 114 */           localCallableStatement.registerOutParameter(j, 12);
/*     */
/* 116 */           j = 1;
/*     */         }
/* 118 */         localCallableStatement.execute();
/* 119 */         if (j != 0)
/*     */         {
/* 121 */           str5 = localCallableStatement.getString(i + 1);
/* 122 */           log.info("str5 value[" + str5 + "] ");
/* 123 */           String[] arrayOfString3 = str5.split("#");
/* 124 */           String str6 = "out_string.length=" + arrayOfString3.length;
/* 125 */           int i1 = 0;
/* 126 */           while (i1 < arrayOfString3.length) {
/* 127 */             str6 = str6 + ";" + "out_string" + "[" + i1 + "]" + "=" + "'" + arrayOfString3[i1].trim() + "'";
/* 128 */             log.info("str6 value[" + str6 + "] ");
/* 129 */             i1++;
/*     */           }
/* 131 */           localPrintWriter.println(str6);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/*     */     finally {
/* 138 */       localPrintWriter.close();
/* 139 */       localPrintWriter = null;
/* 140 */       str5 = null;
/* 141 */       localObject2 = null;
/* 142 */       if (localCallableStatement != null) {
/*     */         try {
/* 144 */           localCallableStatement.close();
/*     */         }
/*     */         catch (SQLException localSQLException5) {
/* 147 */           System.out.println("PROCEDURE Name is " + str1);
/*     */         }
/* 149 */         localCallableStatement = null;
/*     */       }
/* 151 */       if (localConnection != null) {
/*     */         try {
/* 153 */           localConnection.close();
/*     */         }
/*     */         catch (SQLException localSQLException1) {
/*     */         }
/* 157 */         localConnection = null;
/*     */       }
/*     */     }
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 165 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 171 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 176 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\chandra.mer\Desktop\loop\
 * Qualified Name:     unihungama_dbinteraction
 * JD-Core Version:    0.6.0
 */