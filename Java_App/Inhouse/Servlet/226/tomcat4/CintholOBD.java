/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.sql.DataSource;
/*     */
/*     */ public class CintholOBD extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static DataSource ds;
/*  40 */   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
			 String IN_TYPE,SONGID,CIRCLE;
/*     */
/*     */   public void init()
/*     */   {
/*     */     try {
/*  45 */       Context initCtx = new InitialContext();
/*  46 */       Context envCtx = (Context)initCtx.lookup("java:comp/env");
/*     */
/*  49 */       ds = (DataSource)envCtx.lookup("jdbc/Hungama_ENT_Cinthol");
/*     */     } catch (Exception e) {
/*  51 */       System.out.println("error is" + e.toString());
/*     */     }
/*     */   }
/*     */
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse res)
/*     */     throws ServletException, IOException
/*     */   {
/*  61 */     if (request.getProtocol().equals("HTTP/1.1")) {
/*  62 */       res.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  64 */     res.setContentType("application/ecmascript");
/*  65 */     res.setContentType("text/html;charset=UTF-8");
/*  66 */     PrintWriter out = res.getWriter();
/*  67 */     String ANI = request.getParameter("ANI");
/*  68 */     String IN_CALLID = request.getParameter("in_connectionid");
/*  68 */     String IN_SESSIONID = request.getParameter("in_sessionid_call");
/*  69 */
/*  70 */     String TOKEN = request.getParameter("TOKEN");
/*  71 */     String OPERATOR = request.getParameter("OPERATOR");
/*  72 */     if (OPERATOR == null)
/*  73 */       OPERATOR = "MTSM";
/*  74 */     System.out.println("CIRCLE-->" + CIRCLE);
/*  75 */     Connection conat1 = null;
/*  76 */     CallableStatement ccstmt1 = null;
/*     */     try {
/*  78 */       conat1 = ds.getConnection();
/*  79 */       if (conat1 != null)
/*     */       {
/*  81 */         ccstmt1 = conat1.prepareCall("{call CINTHOL_UPDATE_FAILEDCALL(?,?,?)}");
/*  83 */
/*  87 */         ccstmt1.setString(1, ANI);
/*  88 */         ccstmt1.setString(2, IN_CALLID);
				  ccstmt1.setString(3, IN_SESSIONID);
/*  89 */
/*  90 */         ccstmt1.execute();
/*  91 */         conat1.close();
/*  92 */         ccstmt1.close();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  97 */       e.printStackTrace();
/*     */
/*     */     }

/* 201 */
/* 209 */
/*     */     }
/*     */
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 233 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 247 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 254 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\Vinay\Desktop\
 * Qualified Name:     radio_rngmenu
 * JD-Core Version:    0.6.0
 */