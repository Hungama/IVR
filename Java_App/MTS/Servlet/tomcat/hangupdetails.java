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
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class hangupdetails extends HttpServlet {

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
	//AMIT


	static HashMap hashMap = new HashMap();


	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
				    response.setHeader ("Cache-Control", "no-cache");}

		PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");
		

		//String SONG = request.getParameter("SONG");
		String DATA="";
		String ANI=request.getParameter("ANI");
		String DNIS=request.getParameter("DNIS");
		String CODE=request.getParameter("status");
		String DCEND=request.getParameter("DCEND");
		
		try {

			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
    		String timestamp= sdf.format(today.getTime());
			

			String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
					+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
					+ formatN("" + today.get(Calendar.DATE), 2);
			FileOutputStream outp = null; // declare a file output object
			PrintStream p = null;
			File ServiceFolder = new File("/home/Hungama_call_logs/OBDVOICE/VA_hangup_logs/");
			if (!ServiceFolder.exists())
			{
				ServiceFolder.mkdir();
				outp = new FileOutputStream("/home/Hungama_call_logs/OBDVOICE/VA_hangup_logs/hangup_logs_"+strlogfile+".txt", true);
			}
			else
			{
				outp = new FileOutputStream("/home/Hungama_call_logs/OBDVOICE/VA_hangup_logs/hangup_logs_"+strlogfile+".txt", true);
			}
			DATA = "#"+timestamp+"#"+ANI+"#"+DNIS+"#"+CODE+"#"+DCEND+"#";
			synchronized(this)
			{
				try
				{
					PrintWriter p1=new PrintWriter(outp);
					p1.println(DATA);
					p1.flush();
					p1.close();
					//temp1=null;
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
