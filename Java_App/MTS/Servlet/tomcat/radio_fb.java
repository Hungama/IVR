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
public class radio_fb extends HttpServlet {

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

			ds = (DataSource) envCtx.lookup("jdbc/mts_radio");

		} catch (Exception e) {
			System.out.println("error is" + e.toString());
		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse res) throws ServletException, IOException {
		String ANI = (String) request.getParameter("ANI");
		String songname = (String) request.getParameter("songname");
		String albumname = (String) request.getParameter("albumname");
		String option = (String) request.getParameter("option");

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

					songname=songname.replaceAll(" ", "%20");
					System.out.println("SONG NAME  "+songname);
					albumname=albumname.replaceAll(" ", "%20");
					System.out.println("ALBUM NAME  "+albumname);
					ulink ="http://192.168.100.217/MTS/fb_api.php?msisdn=91"+ANI+"&songname="+songname+"&albumname="+albumname+"&cid="+option;
					/* ulink ="http://124.153.73.2/MTS/fb_api.php?mode=post&msisdn=91"+ANI+"&message="+MSG+"&cid="+option; */
					/* ulink ="http://124.153.73.2/Reliance/SnsPost.php?msisdn=91"+ANI+"&siteid=1&msg="+MSG+"&option="+option; */
					/*  Save Ulink into DB with status 0 */
					/* Reply with Ok */

				/*URL url = new URL(ulink);
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
					if (response.equalsIgnoreCase("OK")){
						out.println("OK");
					}else
						out.println("NOK");
					System.out.println("*******************END***************************");
				}
				else
				{
					response = "Its Not HTTP_OK"+urlconn.getResponseCode();
				}*/

					conat = ds.getConnection();
					if (conat != null) {

						cs1 = conat.prepareCall("{call RADIO_FB_LINK(?,?)}");
						cs1.setString(1,ANI);
						cs1.setString(2,ulink);
						cs1.execute();
					}
         }
		catch (Exception e) {

			e.printStackTrace();
		} finally {
			out.close();
			out=null;

			if (cs1 != null) {
				try {
					cs1.close();
				} catch (SQLException e) {
					;
				}
				cs1 = null;
			}
			if (conat != null) {
				try {
					conat.close();
				} catch (SQLException e) {
					;
				}
				conat = null;
			}
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
