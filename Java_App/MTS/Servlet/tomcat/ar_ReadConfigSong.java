/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.ListIterator;
/*     */ import javax.servlet.ServletContext;
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
/*  76 */         File file_txt1 = new File(getServletContext().getRealPath("config/" + ConfigPath));
/*     */
/*  78 */         if (file_txt1.exists()) {
/*  79 */           ArrayList data = new ArrayList();
/*  80 */           BufferedReader in = new BufferedReader(
/*  81 */             new FileReader(file_txt1));
/*  82 */           String temp = null;
/*  83 */           while ((temp = in.readLine()) != null)
/*     */           {
/*  86 */             data.add(temp.trim());
/*  87 */             temp = null;
/*  88 */             testCounter++;
/*     */           }
/*     */
/*  91 */           if (TOKEN.equalsIgnoreCase("superhit")) {
/*  92 */             Collections.shuffle(data);
/*     */           }
/*  94 */           ListIterator ite = data.listIterator();
			 		if (data.size() < 100)
						favSongs = "favSongs.length=" + data.size();
					  else
						favSongs = "favSongs.length=100";
/*  95 */
				//favSongs = "favSongs.length=" + data.size();
/*  96 */           int counter = 0;
					 int k = 0;


				while ((ite.hasNext()) && (k == 0)) {
					 if (counter == 99)
					   k = 1;
					 favSongs = favSongs + ";" + "favSongs" + "[" + counter + "]" + "=" + "'" + ite.next() + "'";

					 counter++;
				}


/* while (ite.hasNext()) {
            favSongs = favSongs + ";" + "favSongs" + "[" + counter +
              "]" + "=" + "'" + ite.next() + "'";
            counter++;
          }*/
/*     */
/* 103 */           data.clear();
/* 104 */           in.close();
/*     */         } else {
/* 106 */           favSongs = "favSongs.length=0;favSongs[0]='0'";
/*     */         }
/*     */       }
/*     */
/* 110 */       hashMap.put(key, favSongs);
/* 111 */       out.println(favSongs);
/*     */     }
/*     */     catch (Exception e) {
/* 114 */       out.println("error is" + e.toString());
/*     */     } finally {
/* 116 */       ConfigPath = null;
/* 117 */       favSongs = null;
/* 118 */       key = null;
/* 119 */       TOKEN = null;
/*     */     }
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 134 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 147 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 154 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\chandra.mer\Desktop\mts servlte\
 * Qualified Name:     ar_ReadConfigSong
 * JD-Core Version:    0.6.0
 */