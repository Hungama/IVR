import java.io.*;
import java.io.File.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Record_cricket extends HttpServlet
{

    public Record_cricket()
    {
    }

    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
        String strDirectory = "2012";
        uploadPath1 = new File((new StringBuilder("/tomcat2/webapps/hungama/promptFiles/cricket/record/recordshare")).toString());
        System.out.println((new StringBuilder("uploadPath :")).append(uploadPath1).toString());
        if(!uploadPath1.exists())
            uploadPath1.mkdir();
        else
            System.out.println("folder already exits");
    }



    public void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        PrintWriter out;
        out = response.getWriter();
        if(request.getProtocol().equals("HTTP/1.1"))
            response.setHeader("Cache-Control", "no-cache");
        response.setContentType("application/ecmascript");
        System.out.println("Rename Recorded file on Servlet");
        String recFilename,month,day;
        File recFile,recFile1;
        String rq = request.getParameter("rq");
        String ANI = request.getParameter("ANI");
        String dni = request.getParameter("dni");
         String lang = request.getParameter("lang");
		String  datetime = request.getParameter("datetime");
        recFilename = request.getParameter("recfilename");


        recFile1 = new File((new StringBuilder()).append(uploadPath1).toString());
       /* if(!recFile.exists())
        {
            System.out.println("File does not exits ");
            return;
        } */
        try
        {
			if(recFile1.exists())
			{
				System.out.println("folder found :");

					 String newFileName = (new StringBuilder()).append(uploadPath1).append("/").append(recFilename).toString();
					 System.out.println("my new file name  is :" + newFileName) ;
				 	 File newFile = new File(newFileName);
				      if(!newFile.exists())
				        newFile.delete();
				 	boolean rc = recFile1.renameTo(newFile);
				     out.flush();
                     out.close();


			}
			else
			{
				System.out.println("folder notfound :");
            	recFile1.mkdir();
            	String newFileName = (new StringBuilder()).append(uploadPath1).append("/").append(recFilename).toString();
					 System.out.println("my new file name  is :" + newFileName) ;
				 	 File newFile = new File(newFileName);
				      if(!newFile.exists())
				        newFile.delete();
				 	boolean rc = recFile1.renameTo(newFile);
				     out.flush();
                     out.close();
            }
        }
        catch(Exception lEx)
        {
            getServletContext().log(lEx, "error reading or saving file");
        }
        return;
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

    private static final long serialVersionUID = 1L;
    private File uploadPath1;
}
