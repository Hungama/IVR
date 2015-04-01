import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.*;
import javax.sql.DataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Inhouse_Concentlog extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static HashMap hashMap = new HashMap();

	    public static Connection con = null;
		public static Statement stmtupdate=null;
		public static String dsn="master_db";
		public static String username="ivr";
		public static String pwd="ivr";
		static DataSource ds;

	/*	public void init()
		{
			try
			{
				System.out.println("Connection to be establised");
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://192.168.100.224:3306/"+ dsn, username, pwd);
				if(con!=null)
						System.out.println("Connection prepare");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}*/


		 public void init()
		  {
		    try
		    {
		      InitialContext localInitialContext = new InitialContext();
		      Context localContext = (Context)localInitialContext.lookup("java:comp/env");

		      ds = (DataSource)localContext.lookup("jdbc/uninor_hungama");

		    }
		    catch (Exception localException) {
		      System.out.println("error is" + localException.toString());
		    }
		  }




	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
				    response.setHeader ("Cache-Control", "no-cache");}

		PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");


		String ANI = request.getParameter("ANI");
		String circle = request.getParameter("circle");
		String service = request.getParameter("service");
		String SC = request.getParameter("SC");
		String firstconsent = request.getParameter("firstconsent");
        String firstconsenttime = request.getParameter("firstconsenttime");
        String secondconsent = request.getParameter("secondconsent");
        String secondconsenttime = request.getParameter("secondconsenttime");
        String ccgresult = request.getParameter("ccgresult");
        String IVRtransid = request.getParameter("IVRtransid");
        String operator = request.getParameter("operator");
        String dtmf = request.getParameter("dtmf");



		String temp1 = "";
		try {

			Calendar today = Calendar.getInstance();

			String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
					+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
					+ formatN("" + today.get(Calendar.DATE), 2);

			temp1 = "#"+service+"#" +ANI + "#" + circle + "#" + service + "#" + SC + "#" + firstconsent + "#"+firstconsenttime+"#"+secondconsent+"#"+secondconsenttime+"#"+ccgresult+"#"+IVRtransid;

			FileOutputStream outp = null; // declare a file output object
			PrintStream p = null;

			if(operator.equalsIgnoreCase("unim"))
			{
				File ServiceFolder = new File("/home/Hungama_call_logs/ConsentLog/UNIM/"+service+"/");
				if (!ServiceFolder.exists())
				{
					ServiceFolder.mkdir();
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/UNIM/"+service+"/" +strlogfile+".txt", true);
				}
				else
				{
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/UNIM/"+service+"/" +strlogfile+".txt", true);
				}
			}
			else if(operator.equalsIgnoreCase("tatc") || operator.equalsIgnoreCase("tatm") || operator.equalsIgnoreCase("tata"))
			{
				File ServiceFolder = new File("/home/Hungama_call_logs/ConsentLog/TATA/"+service+"/");
				if (!ServiceFolder.exists())
				{
					ServiceFolder.mkdir();
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/TATA/"+service+"/" +strlogfile+".txt", true);
				}
				else
				{
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/TATA/"+service+"/" +strlogfile+".txt", true);
				}
		    }
			else if(operator.equalsIgnoreCase("relc") || operator.equalsIgnoreCase("relm"))
			{
				File ServiceFolder = new File("/home/Hungama_call_logs/ConsentLog/REL/"+service+"/");
				if (!ServiceFolder.exists())
				{
					ServiceFolder.mkdir();
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/REL/"+service+"/" +strlogfile+".txt", true);
				}
				else
				{
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/REL/"+service+"/" +strlogfile+".txt", true);
				}
			}
			else
				{
				File ServiceFolder = new File("/home/Hungama_call_logs/ConsentLog/OTHER/"+service+"/");
				if (!ServiceFolder.exists())
				{
					ServiceFolder.mkdir();
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/OTHER/"+service+"/" +strlogfile+".txt", true);
				}
				else
				{
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/OTHER/"+service+"/" +strlogfile+".txt", true);
				}
			}
			synchronized(this)
			{
				try
				{
					PrintWriter p1=new PrintWriter(outp);
					p1.println(temp1);
					p1.flush();
					p1.close();
					System.out.println("data insert in table ANI:- "+ANI);
					con=ds.getConnection();
					stmtupdate=con.createStatement();
					if(operator.equalsIgnoreCase("unim"))
						stmtupdate.executeUpdate("insert into Inhouse_IVR.tbl_consent_log_unim(ANI,circle,service,SC,firstconsent,firstconsenttime,secondconsent,secondconsenttime,ccgresult,IVRtransid,date_time,dtmf)values('"+ANI+"','"+circle+"','"+service+"','"+SC+"','"+firstconsent+"','"+firstconsenttime+"','"+secondconsent+"','"+secondconsenttime+"','"+ccgresult+"','"+IVRtransid+"',now(),'"+dtmf+"')");
					else if(operator.equalsIgnoreCase("tatc") || operator.equalsIgnoreCase("tatm") || operator.equalsIgnoreCase("tata"))
						stmtupdate.executeUpdate("insert into Inhouse_IVR.tbl_consent_log_tata(ANI,circle,service,SC,firstconsent,firstconsenttime,secondconsent,secondconsenttime,ccgresult,IVRtransid,date_time)values('"+ANI+"','"+circle+"','"+service+"','"+SC+"','"+firstconsent+"','"+firstconsenttime+"','"+secondconsent+"','"+secondconsenttime+"','"+ccgresult+"','"+IVRtransid+"',now())");
					else if(operator.equalsIgnoreCase("relc") || operator.equalsIgnoreCase("relm") || operator.equalsIgnoreCase("rel"))
						stmtupdate.executeUpdate("insert into Inhouse_IVR.tbl_consent_log_rel(ANI,circle,service,SC,firstconsent,firstconsenttime,date_time)values('"+ANI+"','"+circle+"','"+service+"','"+SC+"','"+firstconsent+"','"+firstconsenttime+"',now())");
					else
						stmtupdate.executeUpdate("insert into Inhouse_IVR.tbl_consent_log_other(ANI,circle,service,SC,firstconsent,firstconsenttime,date_time)values('"+ANI+"','"+circle+"','"+service+"','"+SC+"','"+firstconsent+"','"+firstconsenttime+"',now())");
					outp.flush();
					outp.close();
				}catch(Exception E)
				{
					System.out.println("outp is not opened");
				}
			}


		} catch (Exception e) {
			out.println("error is" + e.toString());
		}finally{


		}
	}


	private String formatN(String str, int x) {
		int len;
		String ret_str = "";
		len = str.length();
		if (len >= x)
			ret_str = str;
		else {
			for (int i = 0; i < x - len; i++)
				ret_str = ret_str + "0";
			ret_str = ret_str + str;
		}
		return ret_str;
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 */
	public String getServletInfo() {
		return "Short description";
	}
	// </editor-fold>
}
