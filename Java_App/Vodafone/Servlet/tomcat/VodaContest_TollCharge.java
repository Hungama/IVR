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
public class VodaContest_TollCharge extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static DataSource ds;
	public void init() {
		try {
			Context initCtx, envCtx;
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/mod");
			} catch (Exception e) {
				System.out.println("error is" + e.toString());
			}
		}
	protected void processRequest(HttpServletRequest request,HttpServletResponse res) throws ServletException, IOException {
			String ANI = (String) request.getParameter("ANI");
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
		String ulink="";
		String rtrnSmsResp=null;
		String result;
		String UserStatus="";
		String service="";
		String svcid="";
		String svcdesc="";
		String status="";

		try
		{
				ulink ="http://10.43.248.137/VodafoneBilling/vodafoneOCGBilling.php?msisdn="+ANI+"&Tcharge=3&eventType=HNG_ENTRMNTPORTAL_T_3";
				URL url = new URL(ulink);
            	System.out.println("Going to open conn");
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
protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		processRequest(request, response);
	}
public String getServletInfo() {
		return "Short description";
	}

}
