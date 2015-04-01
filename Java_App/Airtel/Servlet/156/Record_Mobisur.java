// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/24/2012 3:16:29 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   rec_confirm.java

import java.io.*;
import java.io.File.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Record_Mobisur extends HttpServlet
{

    public Record_Mobisur()
    {
    }

    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
        String strDirectory = "Mobisur";
        uploadPath1 = new File((new StringBuilder("/var/lib/LIVERECORDING/")).append(strDirectory).toString());
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
        String ANI = request.getParameter("ANI");
        recFilename = request.getParameter("recfilename");
        month = request.getParameter("month");
        day = request.getParameter("day");
        System.out.println("ANI is :" + ANI);
        System.out.println("recfilename is :" + recFilename);
        System.out.println("day is :" + day);
        System.out.println("my file name  is :" + (new StringBuilder()).append(uploadPath1).append("/2012/08/").append(day).toString());
        recFile = new File((new StringBuilder()).append(uploadPath1).append("/2012/08/").append(day).toString());
       /* if(!recFile.exists())
        {
            System.out.println("File does not exits ");
            return;
        } */
        try
        {
			if(recFile.exists())
			{
				System.out.println("folder found :");
				 String newFileName = (new StringBuilder()).append(uploadPath1).append("/2012/08/").append(day).append("/").append(recFilename).toString();
				 System.out.println("my new file name  is :" + newFileName) ;
				 File newFile = new File(newFileName);
				      if(!newFile.exists())
				        newFile.delete();
				 boolean rc = recFile.renameTo(newFile);
				     out.flush();
                     out.close();

			}
			else
			{
				System.out.println("folder notfound :");
            	recFile.mkdir();
            	String newFileName = (new StringBuilder()).append(uploadPath1).append("/2012/08/").append(day).append("/").append(recFilename).toString();
            	System.out.println("my new not found file name  is :" + newFileName) ;
				File newFile = new File(newFileName);
            	if(!newFile.exists())
                	newFile.delete();
            		boolean rc = recFile.renameTo(newFile);
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
