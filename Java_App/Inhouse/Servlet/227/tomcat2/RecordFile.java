/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.sql.DataSource;
/*     */
/*     */ public class RecordFile extends HttpServlet
/*     */ {
/*     */   static DataSource ds;
/*     */
/*     */   public void destroy()
/*     */   {
/*  36 */     super.destroy();
/*     */   }
/*     */
/*     */   public void init()
/*     */   {
/*     */     try
/*     */     {
/*  45 */       Context initCtx = new InitialContext();
/*  46 */       Context envCtx = (Context)initCtx.lookup("java:comp/env");
/*     */
/*  50 */       ds = (DataSource)envCtx.lookup("jdbc/reliance_cricket");
/*     */     }
/*     */     catch (Exception e) {
/*  53 */       System.out.println("error is" + e.toString());
/*     */     }
/*     */   }
/*     */
/*     */   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  62 */     if (request.getProtocol().equals("HTTP/1.1")) {
/*  63 */       response.setHeader("Cache-Control", "no-cache");
/*     */     }
/*  65 */     response.setContentType("application/ecmascript");
/*  66 */     Connection conat = null;
/*  67 */     CallableStatement cs1 = null;
/*  68 */     PrintWriter out = response.getWriter();
/*  69 */     String rq = null;
/*  70 */     String ani = null;
/*  71 */     String dni = null;
/*  72 */     String lang = null;
/*  73 */     String datetime = null;
/*  74 */     String lgrecmsg = null;
/*     */
/*  79 */     int counter = 0;
/*     */     try
/*     */     {
/*  89 */       rq = request.getParameter("rq");
/*  90 */       ani = request.getParameter("ani");
/*  91 */       dni = request.getParameter("dni");
/*  92 */       lang = request.getParameter("lang");
/*  93 */       datetime = request.getParameter("datetime");
/*  94 */       lgrecmsg = request.getParameter("lgrecmsg");
/*     */
/* 100 */       if (rq.equalsIgnoreCase("fed"))
/*     */       {
/*     */         try
/*     */         {
/* 104 */           String temp = ""; String recDir = "";
/*     */
/* 106 */           System.out.println("record message dir name is ::" + recDir);
/* 107 */           System.out.println("record message file name is ::" + lgrecmsg);
/*     */
/* 109 */           String dtFile = getServletContext().getRealPath("promptFiles/cricket/record/feedback/FEDBCK_" + lang + "_" + ani + "_" + datetime + ".wav");
/*     */
/* 113 */           System.out.println("sourse File ." + lgrecmsg);
/* 114 */           System.out.println("destination File ." + dtFile);
/* 115 */           copyfile(lgrecmsg, dtFile);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 120 */           e.printStackTrace();
/*     */         }
/*     */         finally
/*     */         {
/* 124 */           out.close();
/*     */
/* 126 */           counter = 0;
/*     */         }
/*     */
/*     */       }
/* 131 */       else if (rq.equalsIgnoreCase("rec_share"))
/*     */       {
/*     */         try
/*     */         {
/* 135 */           String recDir = "";
/*     */
/* 137 */           System.out.println("record message dir name is ::" + recDir);
/* 138 */           System.out.println("record message file name is ::" + lgrecmsg);
/*     */
/* 140 */           String dtFile = getServletContext().getRealPath("promptFiles/cricket/record/recordshare/RECSHARE_" + lang + "_" + ani + "_" + datetime + ".wav");
/*     */
/* 143 */           System.out.println("sourse File ." + lgrecmsg);
/* 144 */           System.out.println("destination File ." + dtFile);
/* 145 */           copyfile(lgrecmsg, dtFile);
/*     */
/* 147 */           String recFile = "RECSHARE_" + lang + "_" + ani + "_" + datetime + ".wav";
/* 148 */           System.out.println("REcord File Name ." + recFile);
/* 149 */           conat = ds.getConnection();
/* 150 */           if (conat != null)
/*     */           {
/* 152 */             cs1 = conat.prepareCall("{call CRICKET_RECORDSHARE(?,?)}");
/* 153 */             cs1.setString(1, ani);
/* 154 */             cs1.setString(2, recFile);
/* 155 */             cs1.execute();
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 160 */           e.printStackTrace();
/*     */         }
/*     */         finally
/*     */         {
/* 164 */           out.close();
/*     */
/* 166 */           counter = 0;
/*     */         }
/*     */
/*     */       }
/*     */
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 177 */       e.printStackTrace();
/*     */     }
/*     */     finally
/*     */     {
/* 182 */       out.close();
/* 183 */       rq = null;
/* 184 */       ani = null;
/* 185 */       dni = null;
/*     */     }
/*     */   }
/*     */
/*     */   public void copyfile(String srFile, String dtFile)
/*     */   {
/*     */     try
/*     */     {
/* 193 */       File f1 = new File(srFile);
/* 194 */       File f2 = new File(dtFile);
/* 195 */       InputStream srin = new FileInputStream(f1);
/*     */
/* 197 */       OutputStream dstout = new FileOutputStream(f2);
/*     */
/* 199 */       byte[] buf = new byte[1024];
/*     */       int len;
/* 201 */       while ((len = srin.read(buf)) > 0)
/*     */       {
/*     */
/* 202 */         dstout.write(buf, 0, len);
/*     */       }
/* 204 */       srin.close();
/* 205 */       dstout.close();
/* 206 */       System.out.println("File copied.");
/*     */     }
/*     */     catch (FileNotFoundException ex) {
/* 209 */       System.out.println(ex.getMessage() + " in the specified directory.");
/* 210 */       System.exit(0);
/*     */     }
/*     */     catch (IOException e) {
/* 213 */       System.out.println(e.getMessage());
/*     */     }
/*     */   }
/*     */
/*     */   public void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 231 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 247 */     processRequest(request, response);
/*     */   }
/*     */ }

/* Location:           C:\Users\chandra.mer\Desktop\
 * Qualified Name:     RecordFile
 * JD-Core Version:    0.6.0
 */