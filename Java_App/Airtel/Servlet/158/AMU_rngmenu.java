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
/*     */ public class AMU_rngmenu extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static DataSource ds;
/*  40 */   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
/*     */
/*     */   public void init()
/*     */   {
/*     */     try {
/*  45 */       Context initCtx = new InitialContext();
/*  46 */       Context envCtx = (Context)initCtx.lookup("java:comp/env");
/*     */
/*  49 */       ds = (DataSource)envCtx.lookup("jdbc/airtel_radio");
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
/*  67 */     String SONGID = request.getParameter("SONGID");
/*  68 */     String IN_TYPE = request.getParameter("IN_TYPE");
/*  69 */     String IN_DNIS = request.getParameter("IN_DNIS");
/*  70 */     String CIRCLE = request.getParameter("CIRCLE");
/*  71 */     String TOKEN = request.getParameter("TOKEN");
/*  72 */     String OPERATOR = request.getParameter("OPERATOR");
/*  73 */     if (OPERATOR == null)
/*  74 */       OPERATOR = "AIRM";
/*  75 */     System.out.println("CIRCLE-->" + CIRCLE);
/*  76 */     Connection conat1 = null;
/*  77 */     CallableStatement ccstmt1 = null;
/*     */     try {
/*  79 */       conat1 = ds.getConnection();
/*  80 */       if (conat1 != null)
/*     */       {
/*  82 */         if ("AIRM".equalsIgnoreCase(OPERATOR))
/*  83 */           ccstmt1 = conat1.prepareCall("{call RADIO_CRBTRNG_TOTALREQS(?,?,?)}");
/*  84 */         else
/*  87 */           ccstmt1 = conat1.prepareCall("{call RADIO_CRBTRNG_TOTALREQS(?,?,?)}");
/*  88 */         ccstmt1.setString(1, SONGID);
/*  89 */         ccstmt1.setString(2, IN_TYPE);
/*  90 */         ccstmt1.setString(3, CIRCLE);
/*  91 */         ccstmt1.execute();
/*  92 */         conat1.close();
/*  93 */         ccstmt1.close();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  98 */       e.printStackTrace();
/*     */       try
/*     */       {
/* 101 */         conat1.close();
/* 102 */         ccstmt1.close();
/*     */       }
/*     */       catch (Exception e1) {
/* 105 */         e1.printStackTrace();
/*     */       }
/*     */     }
/* 108 */     if ((TOKEN != null) && (TOKEN.equalsIgnoreCase("FREE")))
/* 109 */       hashMap.clear();
/* 110 */     String key = OPERATOR + "_" + SONGID + "_" + IN_TYPE;
/*     */
/* 112 */     if (hashMap.get(key) != null)
/*     */     {
/* 114 */       System.out.println("Hesh key-->" + key);
/* 115 */       out.println((String)hashMap.get(key));
/* 116 */       return;
/*     */     }
/*     */
/* 119 */     String PROCEDURE = "RADIO_RNGMENU";
/* 120 */     if ("AIRM".equalsIgnoreCase(OPERATOR))
/* 121 */       PROCEDURE = "RADIO_RNGMENU";
/* 122 */     else
/* 125 */       PROCEDURE = "RADIO_RNGMENU";
/* 126 */     String Inparameter_string = "3";
/* 127 */     String Outparameter_string = "1";
/* 128 */     int Inparameter = Integer.parseInt(Inparameter_string);
/* 129 */     int Outparameter = Integer.parseInt(Outparameter_string);
/*     */
/* 131 */     String[] IN = new String[Inparameter];
/*     */
/* 133 */     String[] OUT = new String[Outparameter];
/* 134 */     String CALL = PROCEDURE + "(";
/* 135 */     for (int i = 0; i < Inparameter; i++)
/*     */     {
/* 137 */       if (i == 0)
/* 138 */         IN[i] = SONGID;
/* 139 */       else if (i == 1)
/* 140 */         IN[i] = IN_TYPE;
/*     */       else {
/* 142 */         IN[i] = IN_DNIS;
/*     */       }
/* 144 */       CALL = CALL + "?";
/* 145 */       if (i < Inparameter - 1)
/* 146 */         CALL = CALL + ",";
/*     */     }
/* 148 */     if (Outparameter != 0)
/*     */     {
/* 150 */       CALL = CALL + ",?";
/*     */     }
/* 152 */     CALL = CALL + ")";
/*     */
/* 154 */     res.setContentType("text/html;charset=UTF-8");
/* 155 */     Connection conat = null;
/* 156 */     CallableStatement ccstmt = null;
/* 157 */     String value = null;
/*     */     try
/*     */     {
/* 160 */       conat = ds.getConnection();
/*     */
/* 162 */       if (conat != null)
/*     */       {
/* 164 */         ccstmt = conat.prepareCall("{call " + CALL + "}");
/* 165 */         for (int i = 0; i < Inparameter; i++)
/*     */         {
/* 167 */           int j = i + 1;
/* 168 */           ccstmt.setString(j, IN[i]);
/*     */         }
/*     */
/* 172 */         if (Outparameter != 0)
/*     */         {
/* 174 */           Outparameter = Inparameter + 1;
/* 175 */           ccstmt.registerOutParameter(Outparameter, 12);
/*     */
/* 177 */           Outparameter = 1;
/*     */         }
/* 179 */         ccstmt.execute();
/* 180 */         if (Outparameter != 0)
/*     */         {
/* 182 */           value = ccstmt.getString(Inparameter + 1);
/* 183 */           String[] temp = value.split("#");
/* 184 */           String out_string = "out_string.length=" + temp.length;
/* 185 */           int counter = 0;
/* 186 */           while (counter < temp.length) {
/* 187 */             out_string = out_string + ";" + "out_string" + "[" + counter +
/* 188 */               "]" + "=" + "'" + temp[counter].trim() + "'";
/* 189 */             counter++;
/*     */           }
/* 191 */           hashMap.put(key, out_string);
/* 192 */           out.println(out_string);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 197 */       e.printStackTrace();
/* 198 */       out.close();
/* 199 */       out = null;
/* 200 */       value = null;
/* 201 */       key = null;
/* 202 */       TOKEN = null;
/* 203 */       if (ccstmt != null) {
/*     */         try {
/* 205 */           ccstmt.close();
/*     */         }
/*     */         catch (SQLException localSQLException) {
/*     */         }
/* 209 */         ccstmt = null;
/*     */       }
/* 211 */       if (conat != null) {
/*     */         try {
/* 213 */           conat.close();
/*     */         }
/*     */         catch (SQLException localSQLException1) {
/*     */         }
/* 217 */         conat = null;
/*     */       }
/*     */     } finally {
/* 220 */       out.close();
/* 221 */       out = null;
/* 222 */       value = null;
/* 223 */       key = null;
/* 224 */       TOKEN = null;
/* 225 */       if (ccstmt != null) {
/*     */         try {
/* 227 */           ccstmt.close();
/*     */         }
/*     */         catch (SQLException localSQLException2) {
/*     */         }
/* 231 */         ccstmt = null;
/*     */       }
/* 233 */       if (conat != null) {
/*     */         try {
/* 235 */           conat.close();
/*     */         }
/*     */         catch (SQLException localSQLException3) {
/*     */         }
/* 239 */         conat = null;
/*     */       }
/*     */     }
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 257 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 271 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 278 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\admin\Desktop\Airtel entertainment Unlimited - 546469\RINGTONE CODES\
 * Qualified Name:     AMU_rngmenu
 * JD-Core Version:    0.6.0
 */