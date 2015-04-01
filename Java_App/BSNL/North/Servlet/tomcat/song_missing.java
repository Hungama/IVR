/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Calendar;
/*     */ import java.util.HashMap;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class song_missing extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  43 */   static HashMap hashMap = new HashMap();
/*     */ 
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  49 */     if (request.getProtocol().equals("HTTP/1.1")) {
/*  50 */       response.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  52 */     PrintWriter out = response.getWriter();
/*  53 */     response.setContentType("text/html;charset=UTF-8");
/*     */ 
/*  56 */     String SONG = request.getParameter("SONG");
/*     */     try
/*     */     {
/*  60 */       Calendar today = Calendar.getInstance();
/*     */ 
/*  62 */       String strlogfile = formatN(new StringBuffer().append(today.get(1)).toString(), 4) + 
/*  63 */         formatN(new StringBuffer().append(today.get(2) + 1).toString(), 2) + 
/*  64 */         formatN(new StringBuffer().append(today.get(5)).toString(), 2);
/*  65 */       FileOutputStream outp = null;
/*  66 */       PrintStream p = null;
/*  67 */       File ServiceFolder = new File("/home/Hungama_call_logs/missing_songs/");
/*  68 */       if (!ServiceFolder.exists())
/*     */       {
/*  70 */         ServiceFolder.mkdir();
/*  71 */         outp = new FileOutputStream("/home/Hungama_call_logs/missing_songs/missing_songs_" + strlogfile + ".txt", true);
/*     */       }
/*     */       else
/*     */       {
/*  75 */         outp = new FileOutputStream("/home/Hungama_call_logs/missing_songs/missing_songs_" + strlogfile + ".txt", true);
/*     */       }
/*  77 */       synchronized (this)
/*     */       {
/*     */         try
/*     */         {
/*  81 */           PrintWriter p1 = new PrintWriter(outp);
/*  82 */           p1.println(SONG);
/*  83 */           p1.flush();
/*  84 */           p1.close();
/*     */ 
/*  86 */           outp.flush();
/*  87 */           outp.close();
/*     */         }
/*     */         catch (Exception E) {
/*  90 */           System.out.println("outp is not opened");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  96 */       out.println("error is" + e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private String formatN(String str, int x)
/*     */   {
/* 106 */     String ret_str = "";
/* 107 */     int len = str.length();
/* 108 */     if (len >= x) {
/* 109 */       ret_str = str;
/*     */     } else {
/* 111 */       for (int i = 0; i < x - len; i++)
/* 112 */         ret_str = ret_str + "0";
/* 113 */       ret_str = ret_str + str;
/*     */     }
/* 115 */     return ret_str;
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 128 */     processRequest(request, response);
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 141 */     processRequest(request, response);
/*     */   }
/*     */ 
/*     */   public String getServletInfo()
/*     */   {
/* 148 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\chandra.mer\Desktop\BSNLLL\WEB-INF\classes\
 * Qualified Name:     song_missing
 * JD-Core Version:    0.6.0
 */