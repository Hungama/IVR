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

public class TATA_SWITCHURL extends HttpServlet {


	private static final long serialVersionUID = 1L;


	static DataSource ds;

	public void init() {
		try {
			/*Context initCtx, envCtx;
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");



			ds = (DataSource) envCtx.lookup("jdbc/mod");*/
			System.out.println("TATA SWITCH is");

		} catch (Exception e) {
			System.out.println("error is" + e.toString());
		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse res) throws ServletException, IOException {
		String MSISDN = (String) request.getParameter("MSISDN");
		String circleId = (String) request.getParameter("circleId");
		String Lang = (String) request.getParameter("Lang");
		String Lang_A = (String) request.getParameter("Lang_A");
		String transID = (String) request.getParameter("transID");
		String pPrice = (String) request.getParameter("pPrice");
		String pVal = (String) request.getParameter("pVal");

		if (request.getProtocol().equals ("HTTP/1.1")){
		    res.setHeader ("Cache-Control", "no-cache");}
		res.setContentType("application/ecmascript");
		res.setContentType("text/html;charset=UTF-8");

		Connection conat = null;
		CallableStatement ccstmt = null;
		PrintWriter out = res.getWriter();
		String value = null;
		String CCGResponse=null;
		String renewdate1=null;
		String Query = null;
		String sts_flag=null;
		String ulink="";
		String ulink1="";
		String rtrnSmsResp=null;
		String result;
		String UserStatus="";
		String service="";
		String svcid="";
		String svcdesc="";
		String status="";
		Calendar today = Calendar.getInstance();

		String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4) + formatN("" + (today.get(Calendar.MONTH) + 1), 2)	+ formatN("" + today.get(Calendar.DATE), 2);


		try
		{


			ulink ="http://182.156.191.80:8091/API/CCG?MSISDN="+MSISDN+"&productID=GSMHMP30&pName=Entertainment%20Portal&reqMode=IVR&Ivr_approach=VXML&NetworkId=ISDNPRI&circleId="+circleId+"&Lang="+Lang+"&Lang_A="+Lang_A+"&reqType=Event&ismID=16&transID="+transID+"&pPrice="+pPrice+"&pVal="+pVal+"&CpId=hug&CpName=Hungama&CpPwd=hug@8910&Songname=abc";
			ulink1 ="http://182.156.191.80:8092/mcarbon/mcarbon_ARP/ValidateSecond.jsp?userinput=9&tranId="+transID+"hug";




				URL url = new URL(ulink);
				HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
				System.out.println("Going to open conn"+urlconn.getResponseCode());
				URL url1 = new URL(ulink1);
            	HttpURLConnection urlconn1 = (HttpURLConnection)url1.openConnection();
				String response ="";
				//urlconn.setReadTimeout(5*10000);
				System.out.println("Going to open conn"+urlconn1.getResponseCode());
				if(urlconn.getResponseCode()== HttpURLConnection.HTTP_OK)
				{

					//RESPONSE IS GOOD
					BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
					String line="";
					System.out.println("*******************START*************************");
					while ((line= in.readLine()) != null)
					{
						response = response + line;
						System.out.println(line);
					}
					in.close();
					urlconn.disconnect();
					System.out.println("*******************END***************************");
					response = response.trim();

						CCGResponse="CCGResponse.value='"+response+"'";
						//out.println(CCGResponse);


					}

				else
				{
					response = "Error!Its Not HTTP_OK"+urlconn.getResponseCode();
					//out.println(response);
				}
         }
		 catch(Exception e)
		 {
			 out.println("Error!Exception in Hitting URL");
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
