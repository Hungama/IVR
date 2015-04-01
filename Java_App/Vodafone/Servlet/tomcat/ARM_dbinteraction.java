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
/*     */ public class ARM_dbinteraction extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static DataSource ds;
 			static Logger log = Logger.getLogger(ARM_dbinteraction.class.getName());
/*     */
/*     */   public void init()
/*     */   {
/*     */     try
/*     */     {
/*  44 */       Context initCtx = new InitialContext();
/*  45 */       Context envCtx = (Context)initCtx.lookup("java:comp/env");
					ds = (DataSource)envCtx.lookup("jdbc/Vodafone_ARM");
/*  48 */       String prefix = getServletContext().getRealPath("/");
		 		String file = getInitParameter("log4j-init-file");
				 if (file != null) {
						PropertyConfigurator.configure(prefix + file);
						System.out.println("Log4J Logging started: " + prefix + file);
				 }
				 else {
						System.out.println("Log4J Is not configured for your Application: " + prefix + file);
					  }
				   } catch (Exception e) {
					// System.out.println("error is" + e.toString());
					 log.error("error is" +e.toString());
				   }
			  }
/*     */
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse res)
/*     */     throws ServletException, IOException
/*     */   {
/*  59 */     if (request.getProtocol().equals("HTTP/1.1")) {
/*  60 */       res.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  62 */     res.setContentType("application/ecmascript");
/*     */
/*  65 */     String PROCEDURE = request.getParameter("PROCEDURE");
/*  66 */     String Inparameter_string = request.getParameter("INTOKEN");
/*  67 */     String Outparameter_string = request.getParameter("OUTTOKEN");
/*  68 */     int Inparameter = Integer.parseInt(Inparameter_string);
/*  69 */     int Outparameter = Integer.parseInt(Outparameter_string);

				log.info("---- PARAMETERS ARE ------- ");
				log.info("PROCEDURE:" + PROCEDURE);
				log.info("Inparameter:" + Inparameter);
				log.info("Outparameter:" + Outparameter);

/*     */
/*  74 */     String[] IN = new String[Inparameter];
/*     */
/*  76 */     String[] OUT = new String[Outparameter];
/*  77 */     String CALL = PROCEDURE + "(";
/*  78 */     for (int i = 0; i < Inparameter; i++)
/*     */     {
/*  80 */       IN[i] = request.getParameter("INPARAM[" + i + "]");
/*  81 */       System.out.println("IN[" + i + "]:" + IN[i]);
/*  82 */       CALL = CALL + "?";
/*  83 */       if (i < Inparameter - 1)
/*  84 */         CALL = CALL + ",";
/*     */     }
/*  86 */     if (Outparameter != 0)
/*     */     {
/*  88 */       CALL = CALL + ",?";
/*     */     }
/*  90 */     CALL = CALL + ")";
/*  91 */
log.info("CALL procedure:" + CALL);
/*  92 */     res.setContentType("text/html;charset=UTF-8");
/*  93 */     Connection conat = null;
/*  94 */     CallableStatement ccstmt = null;
/*  95 */     PrintWriter out = res.getWriter();
/*  96 */     String value = null;
/*  97 */     String usr_status1 = null; String lang1 = null;
/*     */     try
/*     */     {
/* 100 */       conat = ds.getConnection();
/*     */
/* 102 */       if (conat != null)
/*     */       {
/* 104 */         ccstmt = conat.prepareCall("{call " + CALL + "}");
/* 105 */         for (int i = 0; i < Inparameter; i++)
/*     */         {
/* 107 */           int j = i + 1;
/* 108 */           ccstmt.setString(j, IN[i]);
/* 109 */          // System.out.println("ccstmt.setString(" + j + "," + IN[i] + ");");
log.info("ccstmt.setString(" + j + "," + IN[i] + ");");
/*     */         }
/*     */
/* 112 */         if (Outparameter != 0)
/*     */         {
/* 114 */           Outparameter = Inparameter + 1;
/* 115 */           ccstmt.registerOutParameter(Outparameter, 12);
 log.info("ccstmt.setString(" + Outparameter + ", java.sql.Types.VARCHAR);");
/* 116 */          // System.out.println("ccstmt.setString(" + Outparameter + ", java.sql.Types.VARCHAR);");
/* 117 */           Outparameter = 1;
/*     */         }
/* 119 */         ccstmt.execute();
/* 120 */         if (Outparameter != 0)
/*     */         {
/* 122 */           value = ccstmt.getString(Inparameter + 1);
/* 123 */           String[] temp = value.split("#");
/* 124 */           String out_string = "out_string.length=" + temp.length;
/* 125 */           int counter = 0;
/* 126 */           while (counter < temp.length) {
/* 127 */             out_string = out_string + ";" + "out_string" + "[" + counter +
/* 128 */               "]" + "=" + "'" + temp[counter].trim() + "'";
/* 129 */             counter++;
/*     */           }
/* 131 */           out.println(out_string);
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 135 */       e.printStackTrace();
/*     */     } finally {
/* 137 */       out.close();
/* 138 */       out = null;
/* 139 */       value = null;
/* 140 */       lang1 = null;
/* 141 */       if (ccstmt != null) {
/*     */         try {
/* 143 */           ccstmt.close();
/*     */         }
/*     */         catch (SQLException localSQLException) {
/*     */         }
/* 147 */         ccstmt = null;
/*     */       }
/* 149 */       if (conat != null) {
/*     */         try {
/* 151 */           conat.close();
/*     */         }
/*     */         catch (SQLException localSQLException1) {
/*     */         }
/* 155 */         conat = null;
/*     */       }
/*     */     }
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 173 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 187 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 194 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\chandra.mer\Desktop\vodalog4j\
 * Qualified Name:     ARM_dbinteraction
 * JD-Core Version:    0.6.0
 */