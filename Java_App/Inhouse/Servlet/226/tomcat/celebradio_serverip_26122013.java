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
/*     */ public class celebradio_serverip extends HttpServlet
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
/*  49 */       ds = (DataSource)envCtx.lookup("jdbc/docomo_manchala");
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
/*  68 */
/*  68 */     String IN_SERVERIP = request.getParameter("IN_SERVERIP");
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
/*  81 */         ccstmt1 = conat1.prepareCall("{call CHAT_SERVERIP_STORE(?,?)}");
/*  83 */
/*  87 */         ccstmt1.setString(1, ANI);
/*  88 */         ccstmt1.setString(2, IN_SERVERIP);
/*  89 */
/*  90 */         ccstmt1.execute();
/*  91 */         conat1.close();
/*  92 */         ccstmt1.close();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  97 */       e.printStackTrace();
/*     */       try
/*     */       {
/* 100 */         conat1.close();
/* 101 */         ccstmt1.close();
/*     */       }
/*     */       catch (Exception e1) {
/* 104 */         e1.printStackTrace();
/*     */       }
/*     */     }
/* 107 */     if ((TOKEN != null) && (TOKEN.equalsIgnoreCase("FREE")))
/* 108 */       hashMap.clear();
/* 109 */     String key = OPERATOR + "_" + SONGID + "_" + IN_TYPE;
/*     */
/* 111 */     if (hashMap.get(key) != null)
/*     */     {
/* 113 */       System.out.println("Hesh key-->" + key);
/* 114 */       out.println((String)hashMap.get(key));
/* 115 */       return;
/*     */     }
/*     */
/* 118 */     String PROCEDURE = "CHAT_SERVERIP_STORE";
/* 119 */
/* 125 */     String Inparameter_string = "2";
/* 126 */     String Outparameter_string = "0";
/* 127 */     int Inparameter = Integer.parseInt(Inparameter_string);
/* 128 */     int Outparameter = Integer.parseInt(Outparameter_string);
/*     */
/* 130 */     String[] IN = new String[Inparameter];
/*     */
/* 132 */     String[] OUT = new String[Outparameter];
/* 133 */     String CALL = PROCEDURE + "(";
/* 134 */     for (int i = 0; i < Inparameter; i++)
/*     */     {
/* 136 */       if (i == 0)
/* 137 */         IN[i] = SONGID;
/*     */       else {
/* 139 */         IN[i] = IN_TYPE;
/*     */       }
/* 141 */       CALL = CALL + "?";
/* 142 */       if (i < Inparameter - 1)
/* 143 */         CALL = CALL + ",";
/*     */     }
/* 145 */     if (Outparameter != 0)
/*     */     {
/* 147 */       CALL = CALL + ",?";
/*     */     }
/* 149 */     CALL = CALL + ")";
/*     */
/* 151 */     res.setContentType("text/html;charset=UTF-8");
/* 152 */     Connection conat = null;
/* 153 */     CallableStatement ccstmt = null;
/* 154 */     String value = null;
/*     */     try
/*     */     {
/* 157 */       conat = ds.getConnection();
/*     */
/* 159 */       if (conat != null)
/*     */       {
/* 161 */         ccstmt = conat.prepareCall("{call " + CALL + "}");
/* 162 */         for (int i = 0; i < Inparameter; i++)
/*     */         {
/* 164 */           int j = i + 1;
/* 165 */           ccstmt.setString(j, IN[i]);
/*     */         }
/*     */
/* 169 */         if (Outparameter != 0)
/*     */         {
/* 171 */           Outparameter = Inparameter + 1;
/* 172 */           ccstmt.registerOutParameter(Outparameter, 12);
/*     */
/* 174 */           Outparameter = 1;
/*     */         }
/* 176 */         ccstmt.execute();
/* 177 */         if (Outparameter != 0)
/*     */         {
/* 179 */           value = ccstmt.getString(Inparameter + 1);
/* 180 */           String[] temp = value.split("#");
/* 181 */           String out_string = "out_string.length=" + temp.length;
/* 182 */           int counter = 0;
/* 183 */           while (counter < temp.length) {
/* 184 */             out_string = out_string + ";" + "out_string" + "[" + counter +
/* 185 */               "]" + "=" + "'" + temp[counter].trim() + "'";
/* 186 */             counter++;
/*     */           }
/* 188 */           hashMap.put(key, out_string);
/* 189 */           out.println(out_string);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 194 */       e.printStackTrace();
/*     */     } finally {
/* 196 */       out.close();
/* 197 */       out = null;
/* 198 */       value = null;
/* 199 */       key = null;
/* 200 */       TOKEN = null;
/* 201 */       if (ccstmt != null) {
/*     */         try {
/* 203 */           ccstmt.close();
/*     */         }
/*     */         catch (SQLException localSQLException) {
/*     */         }
/* 207 */         ccstmt = null;
/*     */       }
/* 209 */       if (conat != null) {
/*     */         try {
/* 211 */           conat.close();
/*     */         }
/*     */         catch (SQLException localSQLException1) {
/*     */         }
/* 215 */         conat = null;
/*     */       }
/*     */     }
/*     */   }
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