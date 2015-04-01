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
/*     */ public class tatmbpl_dbinteraction extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static DataSource ds;
/*  24 */   static Logger log = Logger.getLogger(tatmbpl_dbinteraction.class.getName());
/*     */
/*     */   public void init()
/*     */   {
/*     */     try
/*     */     {
/*  30 */       Context initCtx = new InitialContext();
/*  31 */       Context envCtx = (Context)initCtx.lookup("java:comp/env");
/*     */
/*  33 */       ds = (DataSource)envCtx.lookup("jdbc/docomo_bpl");
/*  34 */       String prefix = getServletContext().getRealPath("/");
/*  35 */       String file = getInitParameter("log4j-init-file");
/*  36 */       if (file != null) {
/*  37 */         PropertyConfigurator.configure(prefix + file);
/*  38 */         System.out.println("Log4J Logging started: " + prefix + file);
/*     */       }
/*     */       else {
/*  41 */         System.out.println("Log4J Is not configured for your Application: " + prefix + file);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  45 */       System.out.println("error is" + e.toString());
/*     */     }
/*     */   }
/*     */
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse res)
/*     */     throws ServletException, IOException
/*     */   {
/*  52 */     if (request.getProtocol().equals("HTTP/1.1")) {
/*  53 */       res.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  55 */     res.setContentType("application/ecmascript");
/*     */
/*  57 */     String PROCEDURE = request.getParameter("PROCEDURE");
/*  58 */     String Inparameter_string = request.getParameter("INTOKEN");
/*  59 */     String Outparameter_string = request.getParameter("OUTTOKEN");
/*  60 */     int Inparameter = Integer.parseInt(Inparameter_string);
/*  61 */     int Outparameter = Integer.parseInt(Outparameter_string);
/*     */
/*  63 */     log.info("---- PARAMETERS ARE ------- ");
/*  64 */     log.info("PROCEDURE IS : " + PROCEDURE);
/*  65 */     log.info("INTOKEN IS : " + Inparameter_string);
/*  66 */     log.info("OUTTOKEN IS : " + Outparameter_string);
/*  67 */     String[] IN = new String[Inparameter];
/*     */
/*  69 */     String[] OUT = new String[Outparameter];
/*  70 */     String CALL = PROCEDURE + "(";
/*  71 */     for (int i = 0; i < Inparameter; i++)
/*     */     {
/*  73 */       IN[i] = request.getParameter("INPARAM[" + i + "]");
/*  74 */       log.info("IN[i] parametersss IS :" + IN[i]);
/*     */
/*  76 */       CALL = CALL + "?";
/*  77 */       if (i < Inparameter - 1)
/*  78 */         CALL = CALL + ",";
/*     */     }
/*  80 */     if (Outparameter != 0)
/*     */     {
/*  82 */       CALL = CALL + ",?";
/*     */     }
/*  84 */     CALL = CALL + ")";
/*     */
/*  86 */     res.setContentType("text/html;charset=UTF-8");
/*  87 */     Connection conat = null;
/*  88 */     CallableStatement ccstmt = null;
/*  89 */     PrintWriter out = res.getWriter();
/*  90 */     String value = null;
/*  91 */     String usr_status1 = null; String lang1 = null;
/*     */     try
/*     */     {
/*  94 */       conat = ds.getConnection();
/*     */
/*  96 */       if (conat != null)
/*     */       {
/*  98 */         ccstmt = conat.prepareCall("{call " + CALL + "}");
/*  99 */         log.info("{call " + CALL + "}");
/* 100 */         for (int i = 0; i < Inparameter; i++)
/*     */         {
/* 102 */           int j = i + 1;
/* 103 */           ccstmt.setString(j, IN[i]);
/*     */         }
/*     */
/* 106 */         if (Outparameter != 0)
/*     */         {
/* 108 */           Outparameter = Inparameter + 1;
/* 109 */           ccstmt.registerOutParameter(Outparameter, 12);
/*     */
/* 111 */           Outparameter = 1;
/*     */         }
/* 113 */         ccstmt.execute();
/* 114 */         if (Outparameter != 0)
/*     */         {
/* 116 */           value = ccstmt.getString(Inparameter + 1);
/* 117 */           String[] temp = value.split("#");
/* 118 */           String out_string = "out_string.length=" + temp.length;
/* 119 */           int counter = 0;
/* 120 */           while (counter < temp.length) {
/* 121 */             out_string = out_string + ";" + "out_string" + "[" + counter +
/* 122 */               "]" + "=" + "'" + temp[counter].trim() + "'";
/* 123 */             counter++;
/*     */           }
/* 125 */           log.info("{out_string " + out_string + "}");
/* 126 */           out.println(out_string);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 131 */       e.printStackTrace();
/*     */     } finally {
/* 133 */       out.close();
/* 134 */       out = null;
/* 135 */       value = null;
/* 136 */       lang1 = null;
/* 137 */       if (ccstmt != null) {
/*     */         try {
/* 139 */           ccstmt.close();
/*     */         }
/*     */         catch (SQLException localSQLException) {
/*     */         }
/* 143 */         ccstmt = null;
/*     */       }
/* 145 */       if (conat != null) {
/*     */         try {
/* 147 */           conat.close();
/*     */         }
/*     */         catch (SQLException localSQLException1) {
/*     */         }
/* 151 */         conat = null;
/*     */       }
/*     */     }
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 159 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 165 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 170 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\admin\Desktop\BPL\
 * Qualified Name:     tatmbpl_dbinteraction
 * JD-Core Version:    0.6.0
 */