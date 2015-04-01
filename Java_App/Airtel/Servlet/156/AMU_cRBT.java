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
public class AMU_cRBT extends HttpServlet {

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

			ds = (DataSource) envCtx.lookup("jdbc/master_db");

		} catch (Exception e) {
			System.out.println("error is" + e.toString());
		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse res) throws ServletException, IOException {
		String ANI = (String) request.getParameter("ANI");
		String vcode = (String) request.getParameter("vcode");
		String zone = (String) request.getParameter("zone");
		String circle = (String) request.getParameter("cirid");
		String TOKEN = (String) request.getParameter("TOKEN");
		String ACTION = (String) request.getParameter("ACTION");

		String SNGID = (String) request.getParameter("SNGID");
		String DNIS = (String) request.getParameter("DNIS");
		String cirname = (String) request.getParameter("cirname");
		String HTflag = (String) request.getParameter("HTflag");

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
			if(TOKEN.equalsIgnoreCase("CRBT_SET"))
			{
				System.out.println("Setting URLS");
				System.out.println("zone===" +zone);
				System.out.println("ANI===" +ANI);
				System.out.println("vcode===" +vcode);
				if(zone.equalsIgnoreCase("south"))
					if(circle.equalsIgnoreCase("19")) //KK
							ulink="http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=IVR_HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=PSO&CHARGE_CLASS=FREE&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE" ;
							// ulink="http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=500690&CHARGE_CLASS=501002&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";
 					else if(circle.equalsIgnoreCase("20")) //AP
						 	ulink="http://10.105.55.36:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=IVR_HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=PSO&CHARGE_CLASS=FREE&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";
						 	//  ulink="http://10.105.55.36:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=500690&CHARGE_CLASS=501002&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";
					else if(circle.equalsIgnoreCase("23"))
							ulink="http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=IVR_HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=PSO&CHARGE_CLASS=FREE&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";

							//  ulink="http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=500690&CHARGE_CLASS=501002&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";
 					else if(circle.equalsIgnoreCase("22"))
							ulink="http://10.127.7.4:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=IVR_HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=PSO&CHARGE_CLASS=FREE&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";

							// ulink="http://10.127.7.4:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=500690&CHARGE_CLASS=501002&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";
 					else
						 	ulink="http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=IVR_HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=PSO&CHARGE_CLASS=FREE&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";
						 	//ulink="http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&SELECTED_BY=HUNGAMA&WAV_FILE=rbt_"+vcode+"_rbt&SUBSCRIPTION_CLASS=500690&CHARGE_CLASS=501002&ISACTIVATE=TRUE&REDIRECT_NATIONAL=TRUE";
 				else if(zone.equalsIgnoreCase("north"))
						ulink ="http://10.2.96.114:9080/CWM/helloTune.jsp?msisdn="+ANI+"&vcode="+vcode+"&uid=Hun_IVR&pass=SHVuX0lWUg==&flag="+HTflag+"&sFree=1&dFree=0";
				else if(zone.equalsIgnoreCase("east"))
						ulink = "http://10.133.27.101:9080/CWM/helloTune.jsp?msisdn="+ANI+"&vcode="+vcode+"&uid=Hun_IVR&pass=SHVuX0lWUg==&flag="+HTflag+"&sFree=1&dFree=0";
				else if(zone.equalsIgnoreCase("west"))
						ulink ="http://10.49.7.86:9080/CWM/helloTune.jsp?msisdn="+ANI+"&vcode="+vcode+"&uid=Hun_IVR&pass=SHVuX0lWUg==&flag="+HTflag+"&sFree=1&dFree=0";

					/*  Save Ulink into DB with status 0 */
					/* Reply with Ok */



					conat = ds.getConnection();
					if (conat != null) {

						cs1 = conat.prepareCall("{call CRBT_RNGT_RECORDS(?,?,?,?,?,?)}");
						cs1.setString(1,ANI);
						cs1.setString(2,SNGID);
						cs1.setString(3,ACTION);
						cs1.setString(4,ulink);
						cs1.setString(5,cirname);
						cs1.setString(6,DNIS);
						cs1.execute();
					}
			}else if(TOKEN.equalsIgnoreCase("FB"))
			{
				if(ACTION.equalsIgnoreCase("CRBT"))
					ulink="http://124.153.73.2/uninor/fb_api.php?mode=post&msisdn="+ANI+"&message=changed%20his%20MyTune%20to%20SongName%20(AlbumName).%20You%20can%20also%20change%20your%20MyTunes%20for%20FREE%20with%20Uninor%20MusicUnlimited%20and%20also%20enjoy%20Unlimited%20Music%20and%20Download%20Unlimited%20Ringtones%20-%20Dial%20now%2052222%20from%20your%20Uninor%20Mobile&cid="+vcode;
				else if(ACTION.equalsIgnoreCase("RTONE"))
					ulink="http://124.153.73.2/airtel/fb_api.php?mode=post&msisdn="+ANI+"&message=just%20download%20the%20Ringtone%20of%20SongName%20(AlbumName)%20on%20Uninor%20MusicUnlimited.%20Dial%2052222%20from%20your%20Uninor%20Mobile%20and%20download%20Unlimited%20Ringtones,%20Unlimited%20MyTunes%20and%20listen%20to%20Unlimited%20Music%20on%20the%20move,%20anytime%20anywhere!&cid="+vcode;
				else if(ACTION.equalsIgnoreCase("LIKED"))
					ulink="http://124.153.73.2/airtel/fb_api.php?mode=post&msisdn="+ANI+"&message=loves%20the%20song%20SongName%20from%20AlbumName%20on%20Uninor%20MyMusic.%20Dial%2052555%20to%20listen%20to%20ur%20favorite%20Music%20on%20your%20Uninor%20Mobile%20and%20share%20with%20your%20Facebook%20Friends%20on%20the%20Move!&cid="+vcode;
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
