/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */
/*     */ public class Record extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  48 */     if (request.getProtocol().equals("HTTP/1.1")) {
/*  49 */       response.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  51 */     response.setContentType("application/ecmascript");
/*     */
/*  53 */     PrintWriter out = response.getWriter();
/*  54 */     String MSG = null;
/*  55 */     String TOKEN = null;
/*  56 */     String ANI = null;
/*  57 */     response.setContentType("text/html;charset=UTF-8");
/*  58 */     String ConfigPath = request.getParameter("ConfigPath");
/*  59 */     ANI = request.getParameter("ANI");
/*  60 */     MSG = request.getParameter("MSG");
/*  61 */     TOKEN = request.getParameter("TOKEN");
/*     */
/*  63 */     label302:
/*     */     try { if (ConfigPath != null) {
/*  64 */         File file_txt1 = new File("/tomcat/webapps/hungama/Content/" + ConfigPath);
/*  65 */         if (file_txt1.exists())
/*     */         {
/*  67 */           String dtFile = getServletContext().getRealPath("Content/" + ConfigPath + "/" + TOKEN + "_RECORD_" + ANI + ".wav");
/*  68 */           System.out.println("sourse File ." + MSG);
/*  69 */           System.out.println("destination File ." + dtFile);
/*  70 */           copyfile(MSG, dtFile); break label302;
/*     */         }
/*     */
/*  74 */         System.out.println("destination File missing");
/*     */       }
/*     */
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  80 */       out.println("error is" + e.toString());
/*     */     } finally {
/*  82 */       ConfigPath = null;
/*  83 */       MSG = null;
/*  84 */       TOKEN = null;
/*  85 */       ANI = null;
/*     */     }
/*     */   }
/*     */
/*     */   public void copyfile(String srFile, String dtFile)
/*     */   {
/*     */     try {
/*  92 */       File f1 = new File(srFile);
/*  93 */       File f2 = new File(dtFile);
/*  94 */       InputStream srin = new FileInputStream(f1);
/*     */
/*  96 */       OutputStream dstout = new FileOutputStream(f2);
/*     */
/*  98 */       byte[] buf = new byte[1024];
/*     */       int len;
/* 100 */       while ((len = srin.read(buf)) > 0)
/*     */       {
/*     */         //int len;
/* 101 */         dstout.write(buf, 0, len);
/*     */       }
/* 103 */       srin.close();
/* 104 */       dstout.close();
/* 105 */       System.out.println("File copied.");
/*     */     }
/*     */     catch (FileNotFoundException ex) {
/* 108 */       System.out.println(ex.getMessage() + " in the specified directory.");
/* 109 */       System.exit(0);
/*     */     }
/*     */     catch (IOException e) {
/* 112 */       System.out.println(e.getMessage());
/*     */     }
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 126 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 139 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 146 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\Vinay\Desktop\
 * Qualified Name:     Record
 * JD-Core Version:    0.6.0
 */