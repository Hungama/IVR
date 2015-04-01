import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;


/**
 *
 * @author Administrator
 */
public class exit_log_lastcat extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	long cal_duration = 0;
	static DataSource ds;

	public void init() {
			try {
				Context initCtx, envCtx;
				initCtx = new InitialContext();
				envCtx = (Context) initCtx.lookup("java:comp/env");

				// Look up our data source

				ds = (DataSource) envCtx.lookup("jdbc/mod");
			} catch (Exception e) {
				System.out.println("error is" + e.toString());

			}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
						    response.setHeader ("Cache-Control", "no-cache");}
		response.setContentType("application/ecmascript");

		// response.setContentType("text/html;charset=UTF-8");
		Connection conat = null;
		CallableStatement ccstmt = null;
		String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");
		String ani = request.getParameter("ani");
		String circle = request.getParameter("circle");
		String dnis = request.getParameter("dnis");
        String service = request.getParameter("service");
		String songid = request.getParameter("songid");
		String catid = request.getParameter("catagory_id");
		Calendar today = Calendar.getInstance();

		String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
				+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
				+ formatN("" + today.get(Calendar.DATE), 2);

		String strlogfile1=null;


		try {

			long duration, d1, d2,Content_Time;
			if (starttime.equals("0")) {
				duration = 100;
			} else {
				int s1, s2, m1, m2, h1, h2, diffseconds = 0, diffminutes = 0, diffhours = 0;
				// long duration,d1,d2;

				h1 = Integer.parseInt(starttime.substring(0, 2));
				h2 = Integer.parseInt(endtime.substring(0, 2));
				m1 = Integer.parseInt(starttime.substring(2, 4));
				m2 = Integer.parseInt(endtime.substring(2, 4));
				s1 = Integer.parseInt(starttime.substring(4, 6));
				s2 = Integer.parseInt(endtime.substring(4, 6));
				if (h1 > h2) {
					d2 = s2 + 60 * m2 + 60 * 60 * h2;
					d1 = (59 - s1) + (59 - m1) * 60 + (23 - h1) * 60 * 60;
					duration = d1 + d2;
					// System.out.println("Duration in Seconds \t"+duration);
				} else {
					if (s2 < s1) {
						s2 += 60;
						--m2;
						diffseconds = s2 - s1;
					} else {
						diffseconds = s2 - s1;
					}
					if (m2 < m1) {
						m2 += 60;
						--h2;
						diffminutes = m2 - m1;
					} else {
						diffminutes = m2 - m1;
					}
					diffhours = h2 - h1;
					duration = diffseconds + (60 * diffminutes)
							+ (60 * 60 * diffhours);


					// System.out.println("Duration in Seconds \t"+duration);
				}
			}

			cal_duration = duration;   //new changes done
			String temp1 = "";




			/*temp1 = "#"+operator+"_"+service+"_" +circle + "#" + sub_flag + "#" + strlogfile + "#"
						+ starttime + "#" + cal_duration + "#" + ani + "#" + Content_Time + "#"
						+ dnis + "#" + realdnis + "#";
			*/

			temp1 = "#"+ani+ "#" +circle+ "#" +starttime+ "#" +endtime+ "#" +dnis+ "#" + cal_duration + "#" +catid+ "#" +songid+ "#";

			FileOutputStream outp = null; // declare a file output object
			PrintStream p = null;

			File ServiceFolder = new File("/home/Hungama_call_logs/MRT_LASTCAT/"+service+"/");
			if (!ServiceFolder.exists()) {
				ServiceFolder.mkdir();
				strlogfile1 = service+"_contentlog_"+strlogfile;
				strlogfile = service+"_calllog_"+strlogfile;
				outp = new FileOutputStream("/home/Hungama_call_logs/MRT_LASTCAT/"+service+"/" + strlogfile + ".txt", true);
			}
			else
			{
				strlogfile1 = service+"_contentlog_"+strlogfile;
				strlogfile = service+"_calllog_" +strlogfile;
				outp = new FileOutputStream("/home/Hungama_call_logs/MRT_LASTCAT/"+service+"/" + strlogfile + ".txt", true);
			}


			// Connect print stream to the output stream

			synchronized(this)
			{
				try
				{
					PrintWriter p1=new PrintWriter(outp);
					p1.println(temp1);
					p1.flush();
					p1.close();
					temp1=null;
					outp.flush();
					outp.close();
				}catch(Exception E)
				{
					System.out.println("outp is not opened");
				}
			}




		} catch (Exception e) {
			//ExceptionLog obj=new ExceptionLog();
			String pageName=getServletConfig().getServletName();
			//obj.log(pageName+" "+e);
			pageName=null;
			e.printStackTrace();
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

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on
	// the + sign on the left to edit the code.">
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
