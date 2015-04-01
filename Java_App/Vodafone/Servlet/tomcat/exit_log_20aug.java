/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.util.Calendar;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.sql.DataSource;
/*     */
/*     */ public class exit_log extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  39 */   long cal_duration = 0L;
/*     */   static DataSource ds;
/*     */
/*     */   public void init()
/*     */   {
/*     */     try
/*     */     {
/*  45 */       Context initCtx = new InitialContext();
/*  46 */       Context envCtx = (Context)initCtx.lookup("java:comp/env");
/*     */
/*  50 */       ds = (DataSource)envCtx.lookup("jdbc/vodafone_hungama");
/*     */     } catch (Exception e) {
/*  52 */       System.out.println("error is" + e.toString());
/*     */     }
/*     */   }
/*     */
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  61 */     if (request.getProtocol().equals("HTTP/1.1"))
/*  62 */       response.setHeader("Cache-Control", "no-cache");
/*  63 */     response.setContentType("application/ecmascript");
/*     */
/*  66 */     Connection conat = null;
/*  67 */     CallableStatement ccstmt = null;
/*  68 */     String starttime = request.getParameter("starttime");
/*  69 */     String endtime = request.getParameter("endtime");
/*  70 */     String ani = request.getParameter("ani");
/*  71 */     String contenttime = request.getParameter("contenttime");
/*  72 */     String circle = request.getParameter("circle");
/*  73 */     String operator = request.getParameter("operator");
/*  74 */     String dnis = request.getParameter("dnis");
/*  75 */     String realdnis = request.getParameter("realdnis");
/*  76 */     String sub_flag = request.getParameter("subflag");
/*  77 */     String service = request.getParameter("service");
/*  78 */     String contentlog = request.getParameter("contentlog");
/*     */
/*  80 */     if (contenttime == null)
/*  81 */       contenttime = "0";
/*  82 */     if (sub_flag == null) {
/*  83 */       sub_flag = "0";
/*     */     }
/*  85 */     Calendar today = Calendar.getInstance();
/*     */
/*  87 */     String strlogfile = formatN(new StringBuilder().append(today.get(1)).toString(), 4) +
/*  88 */       formatN(new StringBuilder().append(today.get(2) + 1).toString(), 2) +
/*  89 */       formatN(new StringBuilder().append(today.get(5)).toString(), 2);
/*     */
/*  91 */     String strlogfile1 = null;
/*     */
/*  94 */     if ((contenttime == null) || (dnis == null) || (ani == null) || (endtime == null) || (starttime == null))
/*     */     {
/*  98 */       String temp2 = "#ERROR#" + sub_flag + "#" + strlogfile + "#" +
/*  99 */         starttime + "#" + contenttime + "#" + ani + "#" + contenttime + "#" +
/* 100 */         dnis + "#" + dnis + "#";
/*     */
/* 102 */       FileOutputStream outp1 = null;
/* 103 */       outp1 = new FileOutputStream("D:/MIS/wrong.txt", true);
/*     */
/* 105 */       PrintWriter p2 = new PrintWriter(outp1);
/*     */
/* 108 */       p2.println(temp2);
/* 109 */       p2.flush();
/* 110 */       p2.close();
/* 111 */       temp2 = null;
/* 112 */       outp1.flush();
/* 113 */       outp1.close();
/* 114 */       return;
/*     */     }
/*     */     try
/*     */     {
/*     */       long duration;
/*     */
/* 122 */       if (starttime.equals("0")) {
/* 123 */         duration = 100L;
/*     */       } else {
/* 125 */         int diffseconds = 0; int diffminutes = 0; int diffhours = 0;
/*     */
/* 128 */         int h1 = Integer.parseInt(starttime.substring(0, 2));
/* 129 */         int h2 = Integer.parseInt(endtime.substring(0, 2));
/* 130 */         int m1 = Integer.parseInt(starttime.substring(2, 4));
/* 131 */         int m2 = Integer.parseInt(endtime.substring(2, 4));
/* 132 */         int s1 = Integer.parseInt(starttime.substring(4, 6));
/* 133 */         int s2 = Integer.parseInt(endtime.substring(4, 6));
/*     */
/* 134 */         if (h1 > h2) {
/* 135 */           long d2 = s2 + 60 * m2 + 3600 * h2;
/* 136 */           long d1 = 59 - s1 + (59 - m1) * 60 + (23 - h1) * 60 * 60;
/* 137 */           duration = d1 + d2;
/*     */         }
/*     */         else {
/* 140 */           if (s2 < s1) {
/* 141 */             s2 += 60;
/* 142 */             m2--;
/* 143 */             diffseconds = s2 - s1;
/*     */           } else {
/* 145 */             diffseconds = s2 - s1;
/*     */           }
/* 147 */           if (m2 < m1) {
/* 148 */             m2 += 60;
/* 149 */             h2--;
/* 150 */             diffminutes = m2 - m1;
/*     */           } else {
/* 152 */             diffminutes = m2 - m1;
/*     */           }
/* 154 */           diffhours = h2 - h1;
/* 155 */           duration = diffseconds + 60 * diffminutes +
/* 156 */             3600 * diffhours;
/*     */         }
/*     */
/*     */       }
/*     */
/* 169 */       this.cal_duration = duration;
/* 170 */       String temp1 = "";
/*     */       long Content_Time;
/*     */
/* 221 */       if (Integer.parseInt(contenttime) > this.cal_duration)
/*     */       {
/* 223 */         Content_Time = this.cal_duration;
/*     */       }
/*     */       else
/*     */       {
/* 227 */         Content_Time = Integer.parseInt(contenttime);
/*     */       }
/*     */
/* 231 */       temp1 = "#" + operator + "_" + service + "_" + circle + "#" + sub_flag + "#" + strlogfile + "#" +
/* 232 */         starttime + "#" + this.cal_duration + "#" + ani + "#" + Content_Time + "#" +
/* 233 */         dnis + "#" + realdnis + "#";
/*     */	    contentlog = operator + "#" + circle + "#" + contentlog;
/* 238 */       FileOutputStream outp = null;
/* 239 */       PrintStream p = null;
/* 240 */       FileOutputStream outc = null;
/*     */
/* 242 */       File ServiceFolder = new File("/home/Hungama_call_logs/" + service + "/");
/* 243 */       if (!ServiceFolder.exists()) {
/* 244 */         ServiceFolder.mkdir();
/* 245 */         strlogfile1 = service + "_contentlog_" + strlogfile;
/* 246 */         strlogfile = service + "_calllog_" + strlogfile;
/* 247 */         outp = new FileOutputStream("/home/Hungama_call_logs/" + service + "/" + strlogfile + ".txt", true);
/* 248 */         if (!contenttime.equalsIgnoreCase("0"))
/* 249 */           outc = new FileOutputStream("/home/Hungama_call_logs/" + service + "/" + strlogfile1 + ".txt", true);
/*     */       }
/*     */       else
/*     */       {
/* 253 */         strlogfile1 = service + "_contentlog_" + strlogfile;
/* 254 */         strlogfile = service + "_calllog_" + strlogfile;
/* 255 */         outp = new FileOutputStream("/home/Hungama_call_logs/" + service + "/" + strlogfile + ".txt", true);
/* 256 */         if (!contenttime.equalsIgnoreCase("0")) {
/* 257 */           outc = new FileOutputStream("/home/Hungama_call_logs/" + service + "/" + strlogfile1 + ".txt", true);
/*     */         }
/*     */
/*     */       }
/*     */
/* 263 */       synchronized (this)
/*     */       {
/*     */         try
/*     */         {
/* 267 */           PrintWriter p1 = new PrintWriter(outp);
/* 268 */           p1.println(temp1);
/* 269 */           p1.flush();
/* 270 */           p1.close();
/* 271 */           temp1 = null;
/* 272 */           outp.flush();
/* 273 */           outp.close();
/*     */         }
/*     */         catch (Exception E) {
/* 276 */           System.out.println("outp is not opened");
/*     */         }
/*     */         try
/*     */         {
/* 280 */           PrintWriter p2 = new PrintWriter(outc);
/* 281 */           p2.println(contentlog);
/* 282 */           p2.flush();
/* 283 */           p2.close();
/* 284 */           outc.flush();
/* 285 */           outc.close();
/*     */         }
/*     */         catch (Exception E) {
/* 288 */           System.out.println("outc is not opened");
/*     */         }
/*     */
/*     */       }
/*     */
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 297 */       String pageName = getServletConfig().getServletName();
/*     */
/* 299 */       pageName = null;
/* 300 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */
/*     */   private String formatN(String str, int x)
/*     */   {
/* 307 */     String ret_str = "";
/* 308 */     int len = str.length();
/* 309 */     if (len >= x) {
/* 310 */       ret_str = str;
/*     */     } else {
/* 312 */       for (int i = 0; i < x - len; i++)
/* 313 */         ret_str = ret_str + "0";
/* 314 */       ret_str = ret_str + str;
/*     */     }
/* 316 */     return ret_str;
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 331 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 344 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 351 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\Vinay\Desktop\
 * Qualified Name:     exit_log
 * JD-Core Version:    0.6.0
 */