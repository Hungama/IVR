/*     */ import com.oreilly.servlet.multipart.FilePart;
/*     */ import com.oreilly.servlet.multipart.MultipartParser;
/*     */ import com.oreilly.servlet.multipart.ParamPart;
/*     */ import com.oreilly.servlet.multipart.Part;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */
/*     */ public class air_recording_mobisur extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private File uploadPath1;
/*     */
/*     */   public void init(ServletConfig config)
/*     */     throws ServletException
/*     */   {
/*  40 */     super.init(config);
/*  41 */     String strDirectory = "Mobisur";
/*     */
/*  43 */     this.uploadPath1 = new File("/var/lib/LIVERECORDING/" + strDirectory);
/*  44 */     System.out.println("uploadPath :" + this.uploadPath1);
/*  45 */     if (!this.uploadPath1.exists())
/*  46 */       this.uploadPath1.mkdir();
/*     */     else
/*  48 */       System.out.println("folder already exits");
/*     */   }
/*     */
/*     */   public void processRequest(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  54 */     PrintWriter out = response.getWriter();
/*  55 */     if (request.getProtocol().equals("HTTP/1.1"))
/*  56 */       response.setHeader("Cache-Control", "no-cache");
/*  57 */     response.setContentType("application/voicexml+xml");
/*     */
/*  59 */     System.out.println("Upload Recorded file on Servlet");
/*     */     try
/*     */     {
/*  63 */       String ANI = ""; String value = ""; String fileName = ""; String UploadFileName = ""; String content = "";
/*  64 */       MultipartParser mp = new MultipartParser(request, 52428800);
/*     */
/*  66 */       int cntr = 0;
/*     */       Part part;
/*  67 */       while ((part = mp.readNextPart()) != null)
/*     */       {
/*     */         //Part part;
/*  68 */         cntr++;
/*  69 */         String name = part.getName();
/*  70 */         if (part.isParam())
/*     */         {
/*  72 */           ParamPart paramPart = (ParamPart)part;
/*  73 */           value = paramPart.getStringValue();
/*     */
/*  76 */           System.out.println("param; name=" + name + ", value=" + value);
/*  77 */           if (name.equalsIgnoreCase("ANI"))
/*  78 */             ANI = value;
/*  79 */           if (name.equalsIgnoreCase("UploadFileName"))
/*  80 */             UploadFileName = value;
/*     */         }
/*     */         else {
/*  83 */           if (!part.isFile())
/*     */             continue;
/*  85 */           FilePart filePart = (FilePart)part;
/*  86 */           fileName = filePart.getFileName();
/*  87 */           if (fileName != null)
/*     */           {
/*  90 */             File recFile = new File(this.uploadPath1 + "/" + fileName);
/*  91 */             if (!recFile.exists()) {
/*  92 */               recFile.delete();
/*     */             }
/*  94 */             long size = filePart.writeTo(this.uploadPath1);
/*     */
/* 100 */             String newFileName = this.uploadPath1 + "/" + UploadFileName + ".wav";
/* 101 */             System.out.println("newFileName :" + newFileName);
/* 102 */             File newFile = new File(newFileName);
/*     */
/* 104 */             if (!newFile.exists()) {
/* 105 */               newFile.delete();
/*     */             }
/* 107 */             boolean rc = recFile.renameTo(newFile);
/* 108 */             System.out.println("file; name=" + name + "; filename=" + fileName +
/* 109 */               ", filePath=" + filePart.getFilePath() +
/* 110 */               ", content type=" + filePart.getContentType() +
/* 111 */               ", size=" + size);
/*     */
/* 113 */             content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\"><form> <block> <var name=\"status\" expr=\"200\"/> <return namelist=\"status\"/> </block> </form> </vxml>";
/*     */           }
/*     */           else
/*     */           {
/* 124 */             content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\"><form> <block> <var name=\"status\" expr=\"201\"/> <return namelist=\"status\"/> </block> </form> </vxml>";
/*     */
/* 130 */             System.out.println("file; name=" + name + "; EMPTY");
/*     */           }
/*     */
/* 137 */           response.setContentLength(content.length());
/* 138 */           out.println(content);
/* 139 */           response.setStatus(200);
/* 140 */           out.flush();
/* 141 */           out.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException lEx) {
/* 146 */       getServletContext().log(lEx, "error reading or saving file");
/*     */     }
/*     */   }
/*     */
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 339 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 348 */     processRequest(request, response);
/*     */   }
/*     */
/*     */   public String getServletInfo()
/*     */   {
/* 354 */     return "Short description";
/*     */   }
/*     */ }

/* Location:           C:\Users\Mukesh Manav\Desktop\Mobisure Proj\
 * Qualified Name:     air_recording_mobisur
 * JD-Core Version:    0.6.0
 */