import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.net.*;
import java.io.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;



/**
 *
 * @author Administrator
 */
public class radio_cRBT extends HttpServlet {

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

	static DataSource ds;

	public void init() {
		try {
			Context initCtx, envCtx;
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");

			// Look up our data source

			ds = (DataSource) envCtx.lookup("jdbc/astrodb");

		} catch (Exception e) {
			System.out.println("error is" + e.toString());
		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse res) throws ServletException, IOException {
		String ANI = (String) request.getParameter("ANI");
		String Token = (String) request.getParameter("TOKEN");
		String CLIPID = (String) request.getParameter("CLIPID");

		res.setContentType("text/html;charset=UTF-8");
		Connection conat = null;
		CallableStatement cs1 = null;
		PrintWriter out = res.getWriter();
		String value = null;
		String UserStatus1=null;
		String renewdate1=null;
		String Query = null;
		String sts_flag=null;
		String ulink="";
		String rtrnSmsResp=null;
		String result;
		String UserStatus="";
		Calendar today = Calendar.getInstance();

		String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4) + formatN("" + (today.get(Calendar.MONTH) + 1), 2)	+ formatN("" + today.get(Calendar.DATE), 2);


		try
		{
				System.out.println("Setting URLS");
				System.out.println("TOKEN  "+Token);
				if(Token.equalsIgnoreCase("USERSTATUS"))
					ulink ="http://59.161.254.4:8085/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=STATUS&XML_REQUIRED=TRUE";
				else if(Token.equalsIgnoreCase("ACTIVATE"))
					ulink ="http://59.161.254.4:8085/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=PREPAID&TONE_ID="+CLIPID+"&SELECTED_BY=MUSICIVR&CATEGORY_ID=23&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=EAUC1DAY";
				else if(Token.equalsIgnoreCase("DOWNLOAD"))
					ulink ="http://59.161.254.4:8085/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=PREPAID&TONE_ID="+CLIPID+"&SELECTED_BY=MUSICIVR&CATEGORY_ID=23&ISACTIVATE=TRUE";
				else if(Token.equalsIgnoreCase("MIGRATE"))
					 ulink ="http://59.161.254.4:8085/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=upgrade&MODE=MUSICIVR&COSID=21&SUBSCRIPTION_CLASS=EAUC";
				URL url = new URL(ulink);
            	System.out.println("Going to open conn");
            	HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
				String response ="";
				urlconn.setReadTimeout(5*10000);
				if(urlconn.getResponseCode()== HttpURLConnection.HTTP_OK){

					//RESPONSE IS GOOD
					BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
					String line="";
					System.out.println("*******************START*************************");
					while ((line= in.readLine()) != null)  {
						System.out.println(line);
						response = response + line;
					}
					in.close();
					urlconn.disconnect();
					System.out.println("*******************END***************************");
				}
				else
				{
					response = "Its Not HTTP_OK"+urlconn.getResponseCode();
				}
         }
		 catch(Exception e)
		 {
			 UserStatus="UserStatus1.value='INVALID'";
			 out.println(UserStatus);
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
