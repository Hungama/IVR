import java.io.*;
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
public class removetmpfile extends HttpServlet {

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
		int cnt=0;

		String ani = request.getParameter("ani");

		//String contentlog = request.getParameter("contentlog");
		//contentlog = operator+"#"+circle+"#"+contentlog;
		String inputfilepath="/home/Hungama_call_logs/tmpcontentlog/"+ani+".tmp";

		try
		{

			File inputFD= new File(inputfilepath);
			if (inputFD.exists())
			{
				inputFD.delete();
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
