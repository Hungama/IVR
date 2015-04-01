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

			ds = (DataSource) envCtx.lookup("jdbc/mts_radio");

		} catch (Exception e) {
			System.out.println("error is" + e.toString());
		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse res) throws ServletException, IOException {
		String ANI = (String) request.getParameter("ANI");
		String Token = (String) request.getParameter("TOKEN");
		String CLIPID = (String) request.getParameter("CLIPID");
		String OPERATOR = (String) request.getParameter("OPERATOR");
		String CCGID = (String) request.getParameter("CCGID");
		if (request.getProtocol().equals ("HTTP/1.1")){
		    res.setHeader ("Cache-Control", "no-cache");}
		res.setContentType("application/ecmascript");
		res.setContentType("text/html;charset=UTF-8");
		if(OPERATOR==null)
			OPERATOR = "MTSM";
		Connection conat = null;
		CallableStatement ccstmt = null;
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
		String service="";
		String svcid="";
		String svcdesc="";
		String status="";
		Calendar today = Calendar.getInstance();

		String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4) + formatN("" + (today.get(Calendar.MONTH) + 1), 2)	+ formatN("" + today.get(Calendar.DATE), 2);
		try
		{
				System.out.println("Setting URLS");
				System.out.println("MSISDN  "+ANI);
				System.out.println("TOKEN  "+Token);
				System.out.println("CLIPID  "+CLIPID);
				System.out.println("CCGID  "+CCGID);
				/*if(Token.equalsIgnoreCase("ACTIVATE") || Token.equalsIgnoreCase("DOWNLOAD"))
				{
					conat = ds.getConnection();
					if (conat != null)
					{
						Query="{call RADIO_RNGMENU(?,?,?)}";
						ccstmt = conat.prepareCall(Query);
						ccstmt.setString(1, CLIPID);
						ccstmt.setString(2, "CRBT");
						ccstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
						ccstmt.execute();
						CLIPID = ccstmt.getString(3);
						ccstmt.close();
						conat.close();
						System.out.println("CLIPID  "+CLIPID);
					}
				}*/
				if(Token.equalsIgnoreCase("USERSTATUS"))
					ulink ="http://10.130.7.35:80/interfaces/isuctuser.do?operator=18&account=hungama&password=hungama&phonenumber="+ANI;
				else if(Token.equalsIgnoreCase("ACTIVATE"))
					ulink ="http://10.130.7.35:80/interfaces/ordertone.do?operatoraccount=hungama&operatorpwd=hungama&phonenumber="+ANI+"&operator=18&userbrand=688&isuct=1&cg_id="+CCGID;
				else if(Token.equalsIgnoreCase("DOWNLOAD"))
					ulink ="http://10.130.7.35:80/interfaces/ordertone.do?operatoraccount=hungama&operatorpwd=hungama&phonenumber="+ANI+"&resourcecode="+CLIPID+"&resourcetype=1&operator=18&isuct=1&cg_id="+ CCGID;
				else if(Token.equalsIgnoreCase("MIGRATE"))
					 ulink ="http://10.130.7.35:80/interfaces/ordertone.do?operatoraccount=hungama&operatorpwd=hungama&phonenumber="+ANI+"&operator=18&userbrand=688&isuct=1&cg_id="+CCGID;
				else if(Token.equalsIgnoreCase("DEACTIVATE"))
				ulink = "http://10.130.7.35:8080/interfaces/batchdeleteuser.do?operatoraccount=hungama&operatorpwd=hungama&phonenumber="+ANI+"&operator=18";
				URL url = new URL(ulink);
            	System.out.println("Going to open conn");
            	HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
				String response ="";
				//urlconn.setReadTimeout(5*10000);
				System.out.println("Url response code by Manish" + urlconn.getResponseCode());
				if(urlconn.getResponseCode()== HttpURLConnection.HTTP_OK){

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
					if(Token.equalsIgnoreCase("DEACTIVATE"))
					{
						String temp[] = response.split("|");
						if(temp.length==2)
						{
							if(temp[1].equalsIgnoreCase("0"))
								UserStatus="UserStatus1.value='SUCCESS'";
							else
								UserStatus="UserStatus1.value='"+temp[1]+"'";
						}
						out.println(UserStatus);
					}
					else if(Token.equalsIgnoreCase("USERSTATUS"))
					{
						UserStatus=response.trim();
						//out.println("response==>"+UserStatus);
						if(UserStatus.equalsIgnoreCase("1"))
						{
							UserStatus="UserStatus1.value='EAUC'";
						}
						else if(UserStatus.equalsIgnoreCase("-1"))
						{
							UserStatus="UserStatus1.value='NEW'";
						}
						else if(UserStatus.equalsIgnoreCase("0"))
						{
							UserStatus="UserStatus1.value='RBT_ACT'";
						}
						else
						{
							UserStatus="UserStatus1.value='INVALID_STATE'";
						}
						out.println(UserStatus);
					}
					else
						out.println(response);
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
