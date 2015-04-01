/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.util.Calendar;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.sql.DataSource;
/*     */
/*     */ public class mobisur_VG extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static DataSource ds;
/*     */
/*     */   public void init()
/*     */   {
/*     */     try
/*     */     {
/*  45 */       Context initCtx = new InitialContext();
/*  46 */       Context envCtx = (Context)initCtx.lookup("java:comp/env");
/*     */
/*  50 */       ds = (DataSource)envCtx.lookup("jdbc/mod");
/*     */     }
/*     */     catch (Exception e) {
/*  53 */       System.out.println("error is" + e.toString());
/*     */     }
/*     */   }
/*     */
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse res)
/*     */     throws ServletException, IOException
/*     */   {
/*  60 */     String ANI = (String) request.getParameter("ANI");
			  String operator_Mob = (String) request.getParameter("operator_Mob");
		      String circle_Mob = (String) request.getParameter("circle_Mob");
/*  63 */     if (request.getProtocol().equals("HTTP/1.1"))
/*  64 */       res.setHeader("Cache-Control", "no-cache");
/*  65 */     res.setContentType("application/ecmascript");
/*  66 */     res.setContentType("text/html;charset=UTF-8");
/*  67 */     Connection conat = null;
/*  68 */     CallableStatement ccstmt = null;
/*  69 */     PrintWriter out = res.getWriter();
/*  70 */     String value = null;
/*  71 */     String UserStatus1 = null;
/*  72 */     String renewdate1 = null;
/*  73 */     String Query = null;
/*  74 */     String sts_flag = null;
/*  75 */     String ulink = "";
/*  76 */     String rtrnSmsResp = null;
/*     */
/*  78 */     String UserStatus = "";
/*  79 */     String service = "";
/*  80 */     String svcid = "";
/*  81 */     String svcdesc = "";
/*  82 */     String status = "";
/*  83 */     Calendar today = Calendar.getInstance();
/*     */
/*  85 */     String strlogfile = formatN(new StringBuilder().append(today.get(1)).toString(), 4) + formatN(new StringBuilder().append(today.get(2) + 1).toString(), 2) + formatN(new StringBuilder().append(today.get(5)).toString(), 2);
/*     */     try
/*     */     {
/*  90 */       System.out.println("Setting URLS");
/*  91 */
				ulink ="http://183.82.99.137/Hungama_Sub/index.php?mobno="+ANI+"&operator="+operator_Mob+"&circle="+circle_Mob+"&uname=vgtApp&pwd=Vot_Mob";
/* 113 */
				URL url = new URL(ulink);
/* 120 */       System.out.println("Going to open conn");
/* 121 */       HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
/* 122 */       String response = "";
/*     */
/* 124 */       if (urlconn.getResponseCode() == 200)
/*     */       {
/* 127 */         BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
/* 128 */         String line = "";
/* 129 */         System.out.println("*******************START*************************");
/* 130 */         while ((line = in.readLine()) != null)
/*     */         {
/* 132 */           response = response + line;
/* 133 */           System.out.println(line);
/*     */         }
/* 135 */         in.close();
/* 136 */         urlconn.disconnect();
/* 137 */         System.out.println("*******************END***************************");
/* 138 */         UserStatus = "UserStatus1.value=OK";

/* 139 */         //out.println(UserStatus);
/*     */       }
/*     */       else
/*     */       {
/* 143 */         response = "Its Not HTTP_OK" + urlconn.getResponseCode();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 148 */       UserStatus = "UserStatus1.value='INVALID'";
/* 149 */      // out.println(UserStatus);
/* 150 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */
/*     */   private String formatN(String str, int x)
/*     */   {
/* 157 */     String ret_str = "";
/* 158 */     int len = str.length();
/* 159 */     if (len >= x) {
/* 160 */       ret_str = str;
/*     */     } else {
/* 162 */       for (int i = 0; i < x - len; i++)
/* 163 */         ret_str = ret_str + "0";
/* 164 */       ret_str = ret_str + str;
/*     */     }
/* 166 */     return ret_str;
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 183 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 197 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 204 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\Shaili.verma\Desktop\
 * Qualified Name:     mobisur_VG
 * JD-Core Version:    0.6.0
 */