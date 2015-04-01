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

public class air_recording_bolbabybol extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private File uploadPath1,uploadPath2,uploadPath3;

  public void init(ServletConfig paramServletConfig)
    throws ServletException
  {
    super.init(paramServletConfig);
    String str = "Mobisur";
  }

  public void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1"))
      paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
    paramHttpServletResponse.setContentType("application/voicexml+xml");

    System.out.println("Upload Recorded file on Servlet");
    try
    {
      Object localObject1 = ""; Object localObject2 = ""; Object localObject3 = ""; String str1 = ""; String str2 = ""; String str3 = ""; String str4 = "";
      MultipartParser localMultipartParser = new MultipartParser(paramHttpServletRequest, 52428800);

      int i = 0;
       Part localPart;
       while ((localPart = localMultipartParser.readNextPart()) != null)
       {
         i++;
         String str5 = localPart.getName();
         Object localObject4;
         if (localPart.isParam())
         {
           localObject4 = (ParamPart)localPart;
           str1 = ((ParamPart)localObject4).getStringValue();

           System.out.println("param; name=" + str5 + ", value=" + str1);
           if (str5.equalsIgnoreCase("ANI"))
             localObject1 = str1;
           if (str5.equalsIgnoreCase("UploadFileName"))
             str3 = str1;
           if (str5.equalsIgnoreCase("month"))
             localObject2 = str1;
           if (str5.equalsIgnoreCase("day")) {
             localObject3 = str1;
           }

           this.uploadPath2= new File("/db/tomcat3/webapps/hungama/RecordFiles/BOLBABYBOL/2012/"+(String)localObject2);

           if (!this.uploadPath2.exists())
           		{
				    this.uploadPath1.mkdir();
				    this.uploadPath1 = new File("/db/tomcat3/webapps/hungama/RecordFiles/BOLBABYBOL/2012/" +(String)localObject2+"/"+ (String)localObject3);
				    System.out.println("uploadPath :" + this.uploadPath1);
           			if (!this.uploadPath1.exists())
					     this.uploadPath1.mkdir();
					else
            			 System.out.println("folder already exits");
				}
		   else
		   		{
					this.uploadPath1 = new File("/db/tomcat3/webapps/hungama/RecordFiles/BOLBABYBOL/2012/" +(String)localObject2+"/"+ (String)localObject3);
					System.out.println("uploadPath :" + this.uploadPath1);
					if (!this.uploadPath1.exists())
						 this.uploadPath1.mkdir();
					else
            			 System.out.println("folder already exits");
				}
         }
         else {
           if (!localPart.isFile())
             continue;
           localObject4 = (FilePart)localPart;
           str2 = ((FilePart)localObject4).getFileName();
           if (str2 != null)
           {
             File localFile1 = new File(this.uploadPath1 + "/" + str2);
             if (!localFile1.exists()) {
               localFile1.delete();
             }
             long l = ((FilePart)localObject4).writeTo(this.uploadPath1);

             String str6 = this.uploadPath1 + "/" + str3;
             System.out.println("newFileName :" + str6);
             File localFile2 = new File(str6);

             if (!localFile2.exists()) {
               localFile2.delete();
             }
             boolean bool = localFile1.renameTo(localFile2);
             System.out.println("file; name=" + str5 + "; filename=" + str2 + ", filePath=" + ((FilePart)localObject4).getFilePath() + ", content type=" + ((FilePart)localObject4).getContentType() + ", size=" + l);

             str4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\"><form> <block> <var name=\"status\" expr=\"200\"/> <return namelist=\"status\"/> </block> </form> </vxml>";
           }
           else
           {
             str4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\"><form> <block> <var name=\"status\" expr=\"201\"/> <return namelist=\"status\"/> </block> </form> </vxml>";

             System.out.println("file; name=" + str5 + "; EMPTY");
           }

           paramHttpServletResponse.setContentLength(str4.length());
           localPrintWriter.println(str4);
           paramHttpServletResponse.setStatus(200);
           localPrintWriter.flush();
           localPrintWriter.close();
         }
       }
     }
     catch (IOException localIOException) {
       getServletContext().log(localIOException, "error reading or saving file");
     }
   }

   protected void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
     throws ServletException, IOException
   {
     processRequest(paramHttpServletRequest, paramHttpServletResponse);
   }

   protected void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
     throws ServletException, IOException
   {
     processRequest(paramHttpServletRequest, paramHttpServletResponse);
   }

   public String getServletInfo()
   {
     return "Short description";
   }
 }




