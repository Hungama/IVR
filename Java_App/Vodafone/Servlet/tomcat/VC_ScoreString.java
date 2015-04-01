import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VC_ScoreString extends HttpServlet
{
   //private static final long serialVersionUID = 1L;

public void init()
{

}
public String getThirdString(int h_element, int ext_element)
	{
		String strfname="";

		if(ext_element == 0)
		{
			strfname="NA";
		}else if(ext_element == 10)
		{
			strfname="score10";
		}else if(ext_element == 20)
		{
			strfname="score20";
		}else if(ext_element == 30)
		{
			strfname="score30";
		}else if(ext_element == 40)
		{
			strfname="score40";
		}else if(ext_element == 50)
		{
			strfname="score50";
		}else if(ext_element == 60)
		{
			strfname="score60";
		}else if(ext_element == 70)
		{
			strfname="score70";
		}else if(ext_element == 80)
		{
			strfname="score80";
		}else if(ext_element == 90)
		{
			strfname="score90";
		}

		return strfname;

	}
	public String getSecondString(int h_element, int ext_element)
	{
		return "NA";
	}

	public String getFirstFileName(int h_element, int ext_element)
	{
		String strfname;
		String strplus="";
		if(ext_element > 0)
		{
			strplus = "_plus";
		}

		if(h_element==1)
		{
			strfname = "one_hundred";
		}else if(h_element==2)
		{
			strfname = "two_hundred";
		}else if(h_element==3)
		{
			strfname = "three_hundred";
		}else if(h_element==4)
		{
			strfname = "four_hundred";
		}else if(h_element==5)
		{
			strfname = "five_hundred";
		}else if(h_element==6)
		{
			strfname = "six_hundred";
		}else if(h_element==7)
		{
			strfname = "seven_hundred";
		}else if(h_element==8)
		{
			strfname = "eight_hundred";
		}else if(h_element==9)
		{
			strfname = "nine_hundred";
		}else if(h_element==10)
		{
			strfname = "one_thousand";
		}else
		{
			strfname = "NA";
			strplus="";
		}

		strfname = strfname+strplus;

		return strfname;

	}


 protected void processRequest(HttpServletRequest request, HttpServletResponse res)
   throws ServletException, IOException
 {
   if (request.getProtocol().equals("HTTP/1.1")) {
     res.setHeader("Cache-Control", "no-cache");
   }
   res.setContentType("application/ecmascript");

   String SCORE = request.getParameter("score");
   String Outparameter_string = request.getParameter("OUTTOKEN");
   int Outparameter = Integer.parseInt(Outparameter_string);
   String[] OUT = new String[Outparameter];
   int iScore = Integer.parseInt(SCORE);
   int hun_element = iScore / 100;
   int extra_element = iScore % 100;
   String first_file, sec_file,third_file, strRet;

   first_file = getFirstFileName(hun_element, extra_element);
   sec_file = getSecondString(hun_element, extra_element);
   third_file = getThirdString(hun_element, extra_element);
   strRet = "out_string.length=3;out_string[0]='"+first_file +"';out_string[1]='" +sec_file +"';out_string[2]='"+ third_file+"'";

 	res.setContentType("text/html;charset=UTF-8");
 	PrintWriter out = res.getWriter();
 	out.println(strRet);
 	out.close();

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




