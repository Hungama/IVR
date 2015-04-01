/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Calendar;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ public class content_score_log extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  39 */   long cal_duration = 0L;
/*     */   static DataSource ds;
/*     */ 
/*     */   protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/*  61 */     if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1"))
/*  62 */       paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
/*  63 */     paramHttpServletResponse.setContentType("application/ecmascript");
/*     */ 
/*  75 */     String str1 = paramHttpServletRequest.getParameter("contenttime");
/*  76 */     String str2 = paramHttpServletRequest.getParameter("contentfile");
/*     */ 
/*  81 */     Calendar localCalendar = Calendar.getInstance();
/*     */ 
/*  84 */     String str3 = "" + formatN(new StringBuilder().append("").append(localCalendar.get(1)).toString(), 4) + formatN(new StringBuilder().append("").append(localCalendar.get(2) + 1).toString(), 2) + formatN(new StringBuilder().append("").append(localCalendar.get(5)).toString(), 2);
/*     */ 
/*  87 */     String str4 = formatN("" + localCalendar.get(11), 2);
/*     */ 
/*  89 */     str3 = str3 + "_" + str4;
/*     */     try
/*     */     {
/*  94 */       FileOutputStream localFileOutputStream = null;
/*     */ 
/*  96 */       Object localObject1 = new File("/home/Hungama_content_score/");
/*  97 */       if (!((File)localObject1).exists())
/*     */       {
/*  99 */         ((File)localObject1).mkdir();
/*     */ 
/* 101 */         str3 = "content_score_" + str3;
/* 102 */         if (!str1.equalsIgnoreCase("0")) {
/* 103 */           localFileOutputStream = new FileOutputStream("/home/Hungama_content_score/" + str3 + ".txt", true);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 108 */         str3 = "content_score_" + str3;
/* 109 */         if (!str1.equalsIgnoreCase("0")) {
/* 110 */           localFileOutputStream = new FileOutputStream("/home/Hungama_content_score/" + str3 + ".txt", true);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 117 */       synchronized (this)
/*     */       {
/*     */         try
/*     */         {
/* 121 */           PrintWriter localPrintWriter = new PrintWriter(localFileOutputStream);
/* 122 */           localPrintWriter.println(str2);
/* 123 */           localPrintWriter.flush();
/* 124 */           localPrintWriter.close();
/*     */ 
/* 126 */           localFileOutputStream.flush();
/* 127 */           localFileOutputStream.close();
/*     */         }
/*     */         catch (Exception localException2) {
/* 130 */           System.out.println("outp is not opened");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception localException1)
/*     */     {
/* 139 */       Object localObject1 = getServletConfig().getServletName();
/*     */ 
/* 141 */       localObject1 = null;
/* 142 */       localException1.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private String formatN(String paramString, int paramInt)
/*     */   {
/* 149 */     String str = "";
/* 150 */     int i = paramString.length();
/* 151 */     if (i >= paramInt) {
/* 152 */       str = paramString;
/*     */     } else {
/* 154 */       for (int j = 0; j < paramInt - i; j++)
/* 155 */         str = str + "0";
/* 156 */       str = str + paramString;
/*     */     }
/* 158 */     return str;
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 173 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 186 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */ 
/*     */   public String getServletInfo()
/*     */   {
/* 193 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\admin\Desktop\Airtel entertainment Unlimited - 546469\java code\
 * Qualified Name:     content_score_log
 * JD-Core Version:    0.6.0
 */