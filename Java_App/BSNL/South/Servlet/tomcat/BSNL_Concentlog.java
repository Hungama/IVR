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
import java.sql.*;
import javax.sql.DataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BSNL_Concentlog extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static HashMap hashMap = new HashMap();

	    public static Connection con = null;
		public static Statement stmtupdate=null;
		public static String dsn="BSNL_hungama";
		public static String username="ivr";
		public static String pwd="ivr123";


		public void init()
		{
			try
			{
				System.out.println("Connection to be establised");
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://192.168.1.5:3306/"+ dsn, username, pwd);
				if(con!=null)
						System.out.println("Connection prepare");
			}catch(Exception e)
			{
				e.printStackTrace();
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


	String temp1 = "";
		try {

			Calendar today = Calendar.getInstance();

			String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
					+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
					+ formatN("" + today.get(Calendar.DATE), 2);

			temp1 = "#"+service+"#" +ANI + "#" + circle + "#" + service + "#" + SC + "#" + firstconsent + "#"+firstconsenttime+"#"+secondconsent+"#"+secondconsenttime;

			FileOutputStream outp = null; // declare a file output object
			PrintStream p = null;


				File ServiceFolder = new File("/home/Hungama_call_logs/ConsentLog/BSNL/"+service+"/");
				if (!ServiceFolder.exists())
				{
					ServiceFolder.mkdir();
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/BSNL/"+service+"/" +strlogfile+".txt", true);
				}
				else
				{
					strlogfile = service+"_consentlog_"+strlogfile;
					outp = new FileOutputStream("/home/Hungama_call_logs/ConsentLog/BSNL/"+service+"/" +strlogfile+".txt", true);
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
					stmtupdate=con.createStatement();
					stmtupdate.executeUpdate("insert into BSNL_IVR.tbl_consent_log_bsnl(ANI,circle,service,SC,firstconsent,firstconsenttime,secondconsent,secondconsenttime,date_time)values('"+ANI+"','"+circle+"','"+service+"','"+SC+"','"+firstconsent+"','"+firstconsenttime+"','"+secondconsent+"','"+secondconsenttime+"',now())");

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
