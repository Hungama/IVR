/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.ListIterator;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class ar_ReadConfigSong extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  40 */   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
/*     */ 
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  46 */     if (request.getProtocol().equals("HTTP/1.1")) {
/*  47 */       response.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  49 */     response.setContentType("application/ecmascript");
/*     */ 
/*  51 */     PrintWriter out = response.getWriter();
/*  52 */     String favSongs = null;
/*  53 */     String TOKEN = null;
/*  54 */     response.setContentType("text/html;charset=UTF-8");
/*  55 */     String ConfigPath = request.getParameter("ConfigPath");
/*  56 */     TOKEN = request.getParameter("TOKEN");
/*  57 */     if ((TOKEN != null) && (TOKEN.equalsIgnoreCase("FREE")))
/*  58 */       hashMap.clear();
/*  59 */     String key = ConfigPath;
/*     */ 
/*  61 */     if (TOKEN == null) {
/*  62 */       TOKEN = "T20";
/*     */     }
/*  64 */     if (hashMap.get(key) != null)
/*     */     {
/*  66 */       out.println((String)hashMap.get(key));
/*  67 */       return;
/*     */     }
/*     */ 
/*  70 */     if (TOKEN == null) {
/*  71 */       TOKEN = "T20";
/*     */     }
/*  73 */     int testCounter = 0;
/*     */     try {
/*  75 */       if (ConfigPath != null) {
			
/*  76 */         File file_txt1 = new File("/var/lib/apache-tomcat-6.0.29/webapps/hungama/config/" + ConfigPath);
		 System.out.println(file_txt1);
/*  77 */         if (file_txt1.exists()) {
/*  78 */           ArrayList data = new ArrayList();
/*  79 */           BufferedReader in = new BufferedReader(
/*  80 */             new FileReader(file_txt1));
/*  81 */           String temp = null;
/*  82 */           while ((temp = in.readLine()) != null)
/*     */           {
/*  85 */             data.add(temp.trim());
/*  86 */             temp = null;
/*  87 */             testCounter++;
/*     */           }
/*     */ 
/*  90 */           if (TOKEN.equalsIgnoreCase("superhit")) {
/*  91 */             Collections.shuffle(data);
/*     */           }
/*  93 */           ListIterator ite = data.listIterator();
/*  94 */           if (data.size() < 100)
/*  95 */             favSongs = "favSongs.length=" + data.size();
/*     */           else
/*  97 */             favSongs = "favSongs.length=100";
/*  98 */           int counter = 0;
/*  99 */           int flag = 0;
/* 100 */           while ((ite.hasNext()) && (flag == 0)) {
/* 101 */             if (counter == 99)
/* 102 */               flag = 1;
/* 103 */             favSongs = favSongs + ";" + "favSongs" + "[" + counter + 
/* 104 */               "]" + "=" + "'" + ite.next() + "'";
/* 105 */             counter++;
/*     */           }
/*     */ 
/* 109 */           data.clear();
/* 110 */           in.close();
/*     */         } else {
/* 112 */           favSongs = "favSongs.length=0;favSongs[0]='0'";
/*     */         }
/*     */       }
/*     */ 
/* 116 */       hashMap.put(key, favSongs);
/* 117 */       out.println(favSongs);
/*     */     }
/*     */     catch (Exception e) {
/* 120 */       out.println("error is" + e.toString());
/*     */     } finally {
/* 122 */       ConfigPath = null;
/* 123 */       favSongs = null;
/* 124 */       key = null;
/* 125 */       TOKEN = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 140 */     processRequest(request, response);
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 153 */     processRequest(request, response);
/*     */   }
/*     */ 
/*     */   public String getServletInfo()
/*     */   {
/* 160 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\chandra.mer\Desktop\BSNLLL\
 * Qualified Name:     ar_ReadConfigSong
 * JD-Core Version:    0.6.0
 */
