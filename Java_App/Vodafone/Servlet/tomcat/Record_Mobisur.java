 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
import java.io.OutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import javax.servlet.ServletContext;
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 public class Record_Mobisur extends HttpServlet
 {
   private static final long serialVersionUID = 1L;

     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
       throws ServletException, IOException
          {
     if (request.getProtocol().equals("HTTP/1.1")) {
       response.setHeader("Cache-Control", "no-cache");
     }
     response.setContentType("application/ecmascript");

     PrintWriter out = response.getWriter();
     String MSG = null;
     String TOKEN = null;
     String ANI = null;
     String month = null;
	 String day = null;
     response.setContentType("text/html;charset=UTF-8");
     String ConfigPath = request.getParameter("ConfigPath");
     ANI = request.getParameter("ANI");
     MSG = request.getParameter("MSG");
     TOKEN = request.getParameter("TOKEN");
	 month = request.getParameter("month");
	 day = request.getParameter("day");

     label302:
     try { if (ConfigPath != null)
       {
    	 	File file_txt1 = new File("/var/lib/apache-tomcat-6.0.32/webapps/hungama/Content/" +ConfigPath + "/2012/" + month);

	         if (file_txt1.exists())
		         {
					File file_txt2 = new File("/var/lib/apache-tomcat-6.0.32/webapps/hungama/Content/" + ConfigPath + "/2012/" + month + "/" + day);
						if (file_txt2.exists())
						{
							String dtFile = getServletContext().getRealPath("Content/" + ConfigPath + "/2012/" + month + "/" + day + "/" + ANI);
							System.out.println("sourse File ." + MSG);
							System.out.println("destination File ." + dtFile);
		                    copyfile(MSG, dtFile); break label302;
						}
						else
						{
							file_txt2.mkdir();
							String dtFile = getServletContext().getRealPath("Content/" + ConfigPath + "/2012/" + month + "/" + day + "/" + ANI);
							System.out.println("sourse File ." + MSG);
							System.out.println("destination File ." + dtFile);
		                    copyfile(MSG, dtFile); break label302;
						}
		         }
		      else
		         {
					file_txt1.mkdir();
					File file_txt2 = new File("/var/lib/apache-tomcat-6.0.32/webapps/hungama/Content/" + ConfigPath + "/2012/" + month + "/" + day);
					file_txt2.mkdir();
					String dtFile = getServletContext().getRealPath("Content/" + ConfigPath + "/2012/" + month + "/" + day + "/" + ANI);
					System.out.println("sourse File ." + MSG);
					System.out.println("destination File ." + dtFile);
		            copyfile(MSG, dtFile); break label302;
				 }
       }

     }
     catch (Exception e)
	     {
	       out.println("error is" + e.toString());
	     } finally {
	       ConfigPath = null;
	       MSG = null;
	       TOKEN = null;
	       ANI = null;
	     }
	   }

   public void copyfile(String srFile, String dtFile)
   {
     try {
       File f1 = new File(srFile);
       File f2 = new File(dtFile);
       InputStream srin = new FileInputStream(f1);

       OutputStream dstout = new FileOutputStream(f2);

       byte[] buf = new byte[1024];
       int len;
       while ((len = srin.read(buf)) > 0)
       {
        // int len;
         dstout.write(buf, 0, len);
       }
      srin.close();
       dstout.close();
       System.out.println("File copied.");
     }
     catch (FileNotFoundException ex) {
       System.out.println(ex.getMessage() + " in the specified directory.");
       System.exit(0);
     }
     catch (IOException e) {
       System.out.println(e.getMessage());
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
