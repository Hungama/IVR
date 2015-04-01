 import com.oreilly.servlet.multipart.FilePart;
 import com.oreilly.servlet.multipart.MultipartParser;
 import com.oreilly.servlet.multipart.ParamPart;
 import com.oreilly.servlet.multipart.Part;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import javax.servlet.ServletConfig;
 import javax.servlet.ServletContext;
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 public class air_recording_mobisur extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   private File uploadPath1;

   public void init(ServletConfig config)
     throws ServletException
   {
     super.init(config);
     String strDirectory = "Mobisur";

   /*  this.uploadPath1 = new File("/var/lib/LIVERECORDING/Mobisur/2012/08/");
     System.out.println("uploadPath :" + this.uploadPath1);
     if (!this.uploadPath1.exists())
       this.uploadPath1.mkdir();
     else
       System.out.println("folder already exits"); */
   }

   public void processRequest(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
   {
     PrintWriter out = response.getWriter();
     if (request.getProtocol().equals("HTTP/1.1"))
       response.setHeader("Cache-Control", "no-cache");
     response.setContentType("application/voicexml+xml");

     System.out.println("Upload Recorded file on Servlet");
     try
     {
       String ANI = "",month="",day=""; String value = ""; String fileName = ""; String UploadFileName = ""; String content = "";
       MultipartParser mp = new MultipartParser(request, 52428800);

       int cntr = 0;
       Part part;
       while ((part = mp.readNextPart()) != null)
       {
         //Part part;
         cntr++;
         String name = part.getName();
         if (part.isParam())
         {
           ParamPart paramPart = (ParamPart)part;
           value = paramPart.getStringValue();

           System.out.println("param; name=" + name + ", value=" + value);
           if (name.equalsIgnoreCase("ANI"))
             ANI = value;
           if (name.equalsIgnoreCase("UploadFileName"))
             UploadFileName = value;
           if (name.equalsIgnoreCase("month"))
           	   month = value;
           if (name.equalsIgnoreCase("day"))
               day = value;

               this.uploadPath1 = new File("/var/lib/LIVERECORDING/Mobisur/2012/08/" + day);
			   System.out.println("uploadPath :" + this.uploadPath1);
			      if (!this.uploadPath1.exists())
			          this.uploadPath1.mkdir();
			      else
      				   System.out.println("folder already exits");
          }
         else {
           if (!part.isFile())
             continue;
           FilePart filePart = (FilePart)part;
           fileName = filePart.getFileName();
           if (fileName != null)
           {
             File recFile = new File(this.uploadPath1 + "/" + fileName);
             if (!recFile.exists()) {
               recFile.delete();
             }
             long size = filePart.writeTo(this.uploadPath1);

             String newFileName = this.uploadPath1 + "/" + UploadFileName ;
             System.out.println("newFileName :" + newFileName);
             File newFile = new File(newFileName);

             if (!newFile.exists()) {
               newFile.delete();
             }
             boolean rc = recFile.renameTo(newFile);
             System.out.println("file; name=" + name + "; filename=" + fileName +
               ", filePath=" + filePart.getFilePath() +
               ", content type=" + filePart.getContentType() +
               ", size=" + size);

             content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\"><form> <block> <var name=\"status\" expr=\"200\"/> <return namelist=\"status\"/> </block> </form> </vxml>";
           }
           else
           {
             content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\"><form> <block> <var name=\"status\" expr=\"201\"/> <return namelist=\"status\"/> </block> </form> </vxml>";

             System.out.println("file; name=" + name + "; EMPTY");
           }

           response.setContentLength(content.length());
           out.println(content);
           response.setStatus(200);
           out.flush();
           out.close();
         }
       }
     }
     catch (IOException lEx) {
       getServletContext().log(lEx, "error reading or saving file");
     }
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
   {
     processRequest(request, response);
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
   {
     processRequest(request, response);
   }

   public String getServletInfo()
   {
     return "Short description";
   }
 }




