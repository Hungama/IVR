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

/**
 *
 * @author Administrator
 */
public class SwitchCall_log extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	static HashMap hashMap = new HashMap();

	public static Connection con = null;
	public static Statement stmtupdate=null;
	public static String dsn="master_db";
	public static String username="billing";
	public static String pwd="billing";


	public void init()
	{
		try
		{
			System.out.println("Connection to be establised");
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://10.130.14.106:3306/"+ dsn, username, pwd);
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
		 String starttime = request.getParameter("starttime");
		 String service = request.getParameter("service");
		 String dnis = request.getParameter("dnis");
         String realdnis = request.getParameter("realdnis");
         String ourtransid = request.getParameter("IVRtransid");
         String error_code = request.getParameter("error_code");
         String cg_id = request.getParameter("cg_id");
         String t_id = request.getParameter("t_id");
         String error_desc = request.getParameter("error_desc");
         String cons_time = request.getParameter("cons_time");
         String cons_stat = request.getParameter("cons_stat");

		String temp1 = "";
		try {

			Calendar today = Calendar.getInstance();

			String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
					+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
					+ formatN("" + today.get(Calendar.DATE), 2);

			temp1 = "#"+service+"#" +ANI + "#" + circle + "#" + starttime + "#" + dnis + "#" + realdnis + "#"+ourtransid+"#"+error_code+"#"+cg_id+"#"+t_id+"#"+ error_desc+"#"+cons_time+"#"+cons_stat;

			FileOutputStream outp = null; // declare a file output object
			PrintStream p = null;
			File ServiceFolder = new File("/home/Hungama_call_logs/DoubleConsentLog/"+service+"/");
			if (!ServiceFolder.exists())
			{
				ServiceFolder.mkdir();
				strlogfile = service+"_switchlog_"+strlogfile;
				outp = new FileOutputStream("/home/Hungama_call_logs/DoubleConsentLog/"+service+"/" +strlogfile+".txt", true);
			}
			else
			{
				strlogfile = service+"_switchlog_"+strlogfile;
				outp = new FileOutputStream("/home/Hungama_call_logs/DoubleConsentLog/"+service+"/" +strlogfile+".txt", true);
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
					stmtupdate.executeUpdate("insert into master_db.tbl_double_consent_log (ANI,circle,starttime,service,SC,ivrtransid,error_code,cg_id,t_id,error_desc,cons_time,cons_stat)values('"+ANI+"','"+circle+"','"+starttime+"','"+service+"','"+realdnis+"','"+ourtransid+"','"+error_code+"','"+cg_id+"','"+t_id+"','"+error_desc+"','"+cons_time+"','"+cons_stat+"')");
					System.out.println("insert query iss-insert into master_db.tbl_double_consent_log (ANI,circle,starttime,service,SC,ivrtransid,error_code,cg_id,t_id,error_desc,cons_time,cons_stat)values('"+ANI+"','"+circle+"','"+starttime+"','"+service+"','"+realdnis+"','"+ourtransid+"','"+error_code+"','"+cg_id+"','"+t_id+"','"+error_desc+"','"+cons_time+"','"+cons_stat+"')");

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
