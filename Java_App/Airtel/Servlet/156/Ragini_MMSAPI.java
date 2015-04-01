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
public class Ragini_MMSAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static DataSource ds;
	public void init() {
		try {
			Context initCtx, envCtx;
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/Hungama_RaginiMMS");

		} catch (Exception e) {
			System.out.println("error is" + e.toString());
		}

	}

	protected void processRequest(HttpServletRequest request,HttpServletResponse res) throws ServletException, IOException {
			String ANI="";
			String MSISDN="";
			String operator="";
			String circle="";
			String continous_counter="";
			String UID="";
			String PASS="";
			String FLAG="";
			String RES = null;
			String pageName=null;
			 Connection localConnection1 = null;
     		CallableStatement localCallableStatement1 = null;

		if (request.getProtocol().equals ("HTTP/1.1")){
		    res.setHeader ("Cache-Control", "no-cache");}

		res.setContentType("application/ecmascript");
		res.setContentType("text/html;charset=UTF-8");

		PrintWriter out = res.getWriter();
		String value = null;
		String ulink = null;
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

				 MSISDN=(String) request.getParameter("ANI");
				 operator=(String) request.getParameter("operator");
				 circle=(String) request.getParameter("circle");
				  continous_counter=(String) request.getParameter("continous_counter");
				 if(operator.equalsIgnoreCase("MTS"))
				 	ulink="http://10.130.14.107/hungamacare/getWallpaperlink.php?ani="+MSISDN+"&op="+operator+"&circle="+circle;
				 else if(operator.equalsIgnoreCase("VODA"))
					ulink="http://10.43.248.137/hungamacare/getWallpaperlink.php?ani="+MSISDN+"&op="+operator+"&circle="+circle;
				 else if(operator.equalsIgnoreCase("AIR"))
					ulink="http://10.2.73.156/kmis/services/hungamacare/getWallpaperlink.php?ani="+MSISDN+"&op="+operator+"&circle="+circle;
				else if(operator.equalsIgnoreCase("INHOUSE"))
					ulink="http://192.168.100.212/kmis/services/hungamacare/getWallpaperlink.php?ani="+MSISDN+"&op="+operator+"&circle="+circle;

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
							RES=response;
							//out.println("Ragini MMS link "+RES);
					}
			else
				{
					response = "Error!Its Not HTTP_OK"+urlconn.getResponseCode();
					//out.println(response);
				}

					try
					{
						localConnection1 = ds.getConnection();
						if (localConnection1 != null)
						   {
									 localCallableStatement1 = localConnection1.prepareCall("{call Hungama_RaginiMMS.MMS_LINK(?,?,?)}");
									 localCallableStatement1.setString(1, MSISDN);
									 localCallableStatement1.setString(2, RES);
									  localCallableStatement1.setString(3,continous_counter);
									 localCallableStatement1.execute();
									 localConnection1.close();
									 localCallableStatement1.close();
						   }
					}
					catch (Exception localException1)
					 {
							pageName = getServletConfig().getServletName();
							System.out.println("Error : "+pageName+ " -- " +localException1.toString());
							pageName = null;
							localException1.printStackTrace();
						   try
							   {
										localConnection1.close();
										localCallableStatement1.close();
							   }
						   catch (Exception localException2)
							   {
										pageName = getServletConfig().getServletName();
										System.out.println("Error : "+pageName+ " -- " +localException2.toString());
										pageName = null;
										localException2.printStackTrace();
							   }
					}
         }
		 catch(Exception e)
		 {
			// out.println("Error!Exception in Hitting URL");
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
