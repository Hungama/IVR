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
public class Eu_ChechApi extends HttpServlet {

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

			ds = (DataSource) envCtx.lookup("jdbc/mod");

		} catch (Exception e) {
			System.out.println("error is" + e.toString());
		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse res) throws ServletException, IOException {

			String ulink = (String) request.getParameter("ulink");
			String API=(String) request.getParameter("API");

			String MSISDN="";
			String REQUEST="";
			String MODE="";
			String UID="";
			String PASS="";
			String FLAG="";

		if (request.getProtocol().equals ("HTTP/1.1")){
		    res.setHeader ("Cache-Control", "no-cache");}

		res.setContentType("application/ecmascript");
		res.setContentType("text/html;charset=UTF-8");

		Connection conat = null;
		CallableStatement ccstmt = null;
		PrintWriter out = res.getWriter();
		String value = null;
		String UserStatus1=null;
		String renewdate1=null;
		String Query = null;
		String sts_flag=null;
		String rtrnSmsResp=null;
		String result;
		String UserStatus="";
		String service="";
		String svcid="";
		String svcdesc="";
		String status="";

		try
		{
			//http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN=9900014500&REQUEST=STATUS&MODE=STS
			if(API.equalsIgnoreCase("OnMobile"))
			{
				 MSISDN=(String) request.getParameter("MSISDN");
				 REQUEST=(String) request.getParameter("REQUEST");
				 MODE=(String) request.getParameter("MODE");


				ulink=ulink+"?MSISDN="+MSISDN+"&REQUEST="+REQUEST+"&MODE="+MODE;
			}
			else if(API.equalsIgnoreCase("RealNetwork"))
			{
				MSISDN=(String) request.getParameter("MSISDN");
				UID=(String) request.getParameter("uid");
				PASS=(String) request.getParameter("pass");
				//http://10.2.89.203/URLIntegration/profile_check.jsp?API=RealNetwork&msisdn=8527000779&uid=HUN_IVR&pass=hun_ivr
				FLAG=(String) request.getParameter("FLAG");

				ulink=ulink+"?msisdn="+MSISDN+"&uid="+UID+"&pass="+PASS+"&flag="+FLAG;
			}


			URL url = new URL(ulink);
            		System.out.println("Going to open conn");
            		System.out.println(ulink);
            		HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
			String response ="";
			if(urlconn.getResponseCode()== HttpURLConnection.HTTP_OK)
				{

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

						UserStatus="UserStatus1.value='"+response+"'";
						out.println(UserStatus);


					}

				else
				{
					response = "Error!Its Not HTTP_OK"+urlconn.getResponseCode();
					out.println(response);
				}
         }
		 catch(Exception e)
		 {
			 out.println("Error!Exception in Hitting URL");
			 e.printStackTrace();
		 }
	}





	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}


	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		processRequest(request, response);
	}


}
