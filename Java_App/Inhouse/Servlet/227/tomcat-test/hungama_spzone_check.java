/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.ListIterator;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class hungama_spzone_check extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  35 */   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
/*     */ 
/*     */   protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/*  41 */     if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1")) {
/*  42 */       paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  44 */     PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
/*  45 */     paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
/*  46 */     String str1 = null;
/*  47 */     String str2 = null;
/*  48 */     String str3 = null;
/*  49 */     String str4 = paramHttpServletRequest.getParameter("events");
/*  50 */     int i = new Date().getDate();
/*  51 */     int j = new Date().getMonth();
/*     */ 
/*  53 */     String str5 = paramHttpServletRequest.getParameter("TOKEN");
/*  54 */     if ((str5 != null) && (str5.equalsIgnoreCase("FREE")))
/*  55 */       hashMap.clear();
/*  56 */     Object localObject1 = str4;
/*     */ 
/*  58 */     if (hashMap.get(localObject1) != null)
/*     */     {
/*  60 */       System.out.println("key:" + (String)localObject1 + "return value:" + (String)hashMap.get(localObject1));
/*  61 */       localPrintWriter.println((String)hashMap.get(localObject1));
/*  62 */       return;
/*     */     }
/*     */     try
/*     */     {
/*  66 */       j += 1;
/*  67 */       String str6 = new Integer(i).toString();
/*  68 */       String str7 = new Integer(j).toString();
/*  69 */       int k = str7.length();
/*     */ 
/*  71 */       if (k == 1)
/*     */       {
/*  73 */         str7 = "0" + str7;
/*     */       }
/*  75 */       String str8 = str6 + str7;
/*     */ 
/*  79 */       if (str4.equals("sub"))
/*  80 */         str3 = "subintro";
/*  81 */       else if (str4.equals("spzone_mpd"))
/*  82 */         str3 = "sp_mpd";
/*     */       else {
/*  84 */         str3 = str4 + "_spzone";
/*     */       }
/*  86 */       File localFile = new File(getServletContext().getRealPath("config/mod/" + str3 + ".cfg"));
/*     */       Object localObject2;
/*  89 */       if (localFile.exists())
/*     */       {
/*  92 */         localObject2 = new ArrayList();
/*  93 */         BufferedReader localBufferedReader = new BufferedReader(new FileReader(localFile));
/*  94 */         String str9 = null;
/*  95 */         while ((str9 = localBufferedReader.readLine()) != null)
/*     */         {
/*  97 */           ((ArrayList)localObject2).add(str9.trim().toLowerCase());
/*  98 */           str9 = null;
/*     */         }
/*     */ 
/* 101 */         ListIterator localListIterator = ((ArrayList)localObject2).listIterator();
/* 102 */         int m = 0;
/* 103 */         while (localListIterator.hasNext())
/*     */         {
/* 105 */           str1 = (String)localListIterator.next();
/* 106 */           String[] arrayOfString = str1.split(":");
/* 107 */           localObject1 = arrayOfString[0];
/* 108 */           hashMap.put(localObject1, str1);
/*     */         }
/* 110 */         ((ArrayList)localObject2).clear();
/* 111 */         localBufferedReader.close();
/*     */       }
/*     */ 
/* 114 */       localObject1 = str8;
/*     */ 
/* 116 */       if (hashMap.get(localObject1) != null)
/*     */       {
/* 118 */         str2 = (String)hashMap.get(localObject1);
/* 119 */         localObject2 = str2.split(":");
/* 120 */         str2 = localObject2[1];
/* 121 */         if (str4.equals("sub"))
/* 122 */           str2 = "sub_val.value='" + str2 + "'";
/*     */         else
/* 124 */           str2 = "spzone.value='" + str2 + "'"; localPrintWriter.println(str2);
/*     */         return;
/*     */       }
/*     */     } catch (Exception localException) {
/* 130 */       localPrintWriter.println("error is" + localException.toString());
/*     */     } finally {
/* 132 */       str1 = null;
/* 133 */       localObject1 = null;
/* 134 */       str5 = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private String formatN(String paramString, int paramInt)
/*     */   {
/* 142 */     String str = "";
/* 143 */     int i = paramString.length();
/* 144 */     if (i >= paramInt) {
/* 145 */       str = paramString;
/*     */     } else {
/* 147 */       for (int j = 0; j < paramInt - i; j++)
/* 148 */         str = str + "0";
/* 149 */       str = str + paramString;
/*     */     }
/* 151 */     return str;
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 164 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
/*     */     throws ServletException, IOException
/*     */   {
/* 177 */     processRequest(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */ 
/*     */   public String getServletInfo()
/*     */   {
/* 184 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\admin\Desktop\In-House\java code\
 * Qualified Name:     hungama_spzone_check
 * JD-Core Version:    0.6.0
 */