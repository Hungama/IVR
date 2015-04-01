
import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.lang.*;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import com.oreilly.servlet.multipart.Part;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.ParamPart;


public class Hungamarecord extends HttpServlet
{
	String record_filename="";
	String name="";
	String epath2= "D:\\logs\\commongexceptionlogs\\";
  		//------------------------------------- dopost method --------------------------------------------------------------------------
  	public void doPost(HttpServletRequest req, HttpServletResponse res)
	{

		try
		{
			MultipartRequest multipartRequest;

			PrintWriter out = (PrintWriter) res.getWriter();
			//multipartRequest = new MultipartRequest(req,"e:\\ivr\\prompts\\MKR\\message\\", 50*1024*1024);
			multipartRequest = new MultipartRequest(req,"/var/lib/recording/", 50*1024*1024);

			//multipartRequest = new MultipartRequest(req,"..\\",50*1024*1024);

			Enumeration files = multipartRequest.getFileNames();
			while (files.hasMoreElements())
			{
			  String name = (String)files.nextElement();
			  String filename = multipartRequest.getFilesystemName(name);
			  String type = multipartRequest.getContentType(name);
			  File f = multipartRequest.getFile(name);

			  if (f != null)
			  {
			   record_filename =f.getName();
			   name=f.getName();
			  }
			  else
			  {
				  	String writeafile ="";
				  	File f1=new File(writeafile);
				  	FileWriter bw= new FileWriter(f1);
					PrintWriter pw=new PrintWriter(bw);
				  	bw.write("hello");
				  	bw.close();
			  }
			}
			String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\">"
				+  "<form> <block> "
				+ "<var name=\"fname\" expr=\"'"+record_filename+"'\"/> "
				+ "<return namelist=\"fname\"/> "
				+ "</block> </form> </vxml>";
			res.setContentType("application/voicexml+xml");
			res.setContentLength(content.length());

			out.println(content);

			res.setStatus(200);
			out.close();
				FileOutputStream fs = new FileOutputStream("E:\\ivr\\prompts\\mkr\\outdial\\600\\115.txt");
				FileOutputStream fs1 = new FileOutputStream("E:\\ivr\\prompts\\mkr\\outdial\\600\\115.lck");
				PrintStream fileOut=new PrintStream(fs);
				PrintStream fileOut1=new PrintStream(fs1);
				fileOut.println("MKRDED");
				fileOut.println("9897782306");
				fileOut.println("9897782306");
				String songnme=req.getParameter("getsongname");
				fileOut.println(songnme);
				fileOut.println(record_filename.substring(0,record_filename.indexOf(".")));
				fileOut.println(name);

				fileOut1.println("9897782306");
				fileOut.close();
				fs.close();
				fileOut1.close();
				fs1.close();

        }
		catch (Exception e)
		{


		}
	}

//------------------------------------------------ doGet method -------------------------------------------------------------------------
public void doGet(HttpServletRequest req, HttpServletResponse res)
	{


		doPost(req, res);
	}
//---------------------------------------------------------------------------------------------------------------------------------------
}
//---------------------------------------------------------------------------------------------------------------------------------------
