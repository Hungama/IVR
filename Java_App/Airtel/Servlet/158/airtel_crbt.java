// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 3/13/2013 10:22:53 AM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   airtel_crbt.java

import java.io.*;
import java.sql.*;
import java.util.Calendar;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.sql.DataSource;

public class airtel_crbt extends HttpServlet
{

    public airtel_crbt()
    {
    }

    public void init()
    {
        try
        {
            Context initCtx = new InitialContext();
            Context envCtx = (Context)initCtx.lookup("java:comp/env");
            ds = (DataSource)envCtx.lookup("jdbc/master_db");
        }
        catch(Exception e)
        {
            System.out.println("error is" + e.toString());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse res)
        throws ServletException, IOException
    {
        String ANI = request.getParameter("ANI");
        String vcode = request.getParameter("vcode");
        String zone = request.getParameter("zone");
        String circle = request.getParameter("cirid");
        String TOKEN = request.getParameter("TOKEN");
        String ACTION = request.getParameter("ACTION");
        String SNGID = request.getParameter("SNGID");
        String DNIS = request.getParameter("DNIS");
        String cirname = request.getParameter("cirname");
        res.setContentType("text/html;charset=UTF-8");
        Connection conat = null;
        CallableStatement cs1 = null;
        PrintWriter out = res.getWriter();
        String value = null;
        String UserStatus1 = null;
        String renewdate1 = null;
        String Query = null;
        String sts_flag = null;
        String ulink = "";
        String rtrnSmsResp = null;
        String UserStatus = "";
        Calendar today = Calendar.getInstance();
       // String strlogfile = formatN(today.get(1), 4) + formatN(today.get(2) + 1, 2) + formatN(today.get(5), 2);
	   String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4) + formatN("" + (today.get(Calendar.MONTH) + 1), 2)	+ formatN("" + today.get(Calendar.DATE), 2);
        try
        {
            if(TOKEN.equalsIgnoreCase("CRBT_SET"))
            {
                System.out.println("Setting URLS");
                System.out.println("zone===" + zone);
                System.out.println("ANI===" + ANI);
                System.out.println("vcode===" + vcode);
                if(zone.equalsIgnoreCase("south"))
                {
                    if(circle.equalsIgnoreCase("19"))
                       // ulink = "http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&CATEGORY_ID=27&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT35&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                       //ulink = "http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&CATEGORY_ID=27&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT35&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                       //ulink =  "http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=500761&CHARGE_CLASS=500998&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
						ulink =  "http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT35&CHARGE_CLASS=DEFAULT_R15&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                    else
                    if(circle.equalsIgnoreCase("20"))
                      //  ulink = "http://10.105.55.36:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&CATEGORY_ID=27&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                      ulink = "http://10.105.55.36:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&CHARGE_CLASS=DEFAULT_R15&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                      //ulink	=  "http://10.105.55.36:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_"+vcode+"_rbt&SELECTED_BY=HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=500649&CHARGE_CLASS=500998&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                    else
                    if(circle.equalsIgnoreCase("21"))
                      //  ulink = "http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&CATEGORY_ID=27&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                     ulink = "http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&CHARGE_CLASS=DEFAULT_R15&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                     //  ulink = "http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN="+ANI+"&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=500649&CHARGE_CLASS=500998&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                    else
                    if(circle.equalsIgnoreCase("23"))
                        //ulink = "http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&CATEGORY_ID=27&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                      ulink = "http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&CHARGE_CLASS=DEFAULT_R15&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                      //ulink = "http://10.111.15.46:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=500649&CHARGE_CLASS=500998&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";

                    else
                    if(circle.equalsIgnoreCase("22"))
                      	//  ulink = "http://10.127.7.4:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&CATEGORY_ID=27&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                      	ulink = "http://10.127.7.4:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&CHARGE_CLASS=DEFAULT_R15&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                      	//  ulink = "http://10.127.7.4:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=500649&CHARGE_CLASS=500998&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";

                    else
                       // ulink = "http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&CATEGORY_ID=6&PROMO_ID=" + vcode + "&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT&REDIRECT_NATIONAL=TRUE";
                       ulink =  "http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=IVR_HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=DEFAULT35&CHARGE_CLASS=DEFAULT_R15&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";
                       //ulink =  "http://10.89.8.73:8080/rbt/rbt_promotion.jsp?MSISDN=" + ANI + "&REQUEST=SELECTION&SUB_TYPE=Prepaid&WAV_FILE=rbt_" + vcode + "_rbt&SELECTED_BY=HUNGAMA&ISACTIVATE=TRUE&SUBSCRIPTION_CLASS=500761&CHARGE_CLASS=500998&REDIRECT_NATIONAL=TRUE&ISACTIVATE=True";

                } else
                if(zone.equalsIgnoreCase("north"))
                    //ulink = "http://10.2.89.202/URLIntegration/helloTune.jsp?msisdn="+ANI+"&vcode=" + vcode + "&uid=HUN_IVR&pass=hun_ivr&flag=0&sFree=1&dFree=1";
                    ulink = "http://10.2.96.114:9080/CWM/helloTune.jsp?msisdn="+ANI+"&vcode="+vcode+"&uid=Hun_IVR&pass=SHVuX0lWUg==&flag=0&sFree=1&dFree=1";
                else
                if(zone.equalsIgnoreCase("east"))
                    //ulink = "http://10.133.23.190/URLIntegration/helloTune.jsp?msisdn=" + ANI + "&vcode=" + vcode + "&uid=HUN_IVR&pass=hun_ivr&flag=0&sFree=1&dFree=1";
                    ulink = "http://10.133.27.101:9080/CWM/helloTune.jsp?msisdn="+ANI+"&vcode="+vcode+"&uid=Hun_IVR&pass=SHVuX0lWUg==&flag=0&sFree=1&dFree=1";
                else
                if(zone.equalsIgnoreCase("west"))
			 if(circle.equalsIgnoreCase("9") || circle.equalsIgnoreCase("11"))
				 //ulink = "http://10.49.5.90/URLIntegration/helloTune.jsp?msisdn=" + ANI + "&vcode=" + vcode + "&uid=HUN_IVR&pass=hun_ivr&flag=" + request.getParameter("flag")+ "&sFree=1&dFree=1";
				 ulink ="http://10.49.7.86:9080/CWM/helloTune.jsp?msisdn="+ANI+"&vcode="+vcode+"&uid=Hun_IVR&pass=SHVuX0lWUg==&flag=" + request.getParameter("flag")+ "&sFree=1&dFree=1";
			else
				 //ulink = "http://10.49.5.90/URLIntegration/helloTune.jsp?msisdn=" + ANI + "&vcode=" + vcode + "&uid=HUN_IVR&pass=hun_ivr&flag=0&sFree=1&dFree=1";
				 ulink ="http://10.49.7.86:9080/CWM/helloTune.jsp?msisdn="+ANI+"&vcode="+vcode+"&uid=Hun_IVR&pass=SHVuX0lWUg==&flag=0&sFree=1&dFree=1";

                conat = ds.getConnection();
                if(conat != null)
                {
                    cs1 = conat.prepareCall("{call CRBT_RNGT_RECORDS(?,?,?,?,?,?)}");
                    cs1.setString(1, ANI);
                    cs1.setString(2, SNGID);
                    cs1.setString(3, ACTION);
                    cs1.setString(4, ulink);
                    cs1.setString(5, cirname);
                    cs1.setString(6, DNIS);
                    cs1.execute();
                }
            } else
            if(TOKEN.equalsIgnoreCase("FB"))
                if(ACTION.equalsIgnoreCase("CRBT"))
                    ulink = "http://124.153.73.2/uninor/fb_api.php?mode=post&msisdn=" + ANI + "&message=changed%20his%20MyTune%20to%20SongName%20(AlbumName).%20You%20can%20also%20change%20your%20MyTunes%20for%20FREE%20with%20Uninor%20MusicUnlimited%20and%20also%20enjoy%20Unlimited%20Music%20and%20Download%20Unlimited%20Ringtones%20-%20Dial%20now%2052222%20from%20your%20Uninor%20Mobile&cid=" + vcode;
                else
                if(ACTION.equalsIgnoreCase("RTONE"))
                    ulink = "http://124.153.73.2/airtel/fb_api.php?mode=post&msisdn=" + ANI + "&message=just%20download%20the%20Ringtone%20of%20SongName%20(AlbumName)%20on%20Uninor%20MusicUnlimited.%20Dial%2052222%20from%20your%20Uninor%20Mobile%20and%20download%20Unlimited%20Ringtones,%20Unlimited%20MyTunes%20and%20listen%20to%20Unlimited%20Music%20on%20the%20move,%20anytime%20anywhere!&cid=" + vcode;
                else
                if(ACTION.equalsIgnoreCase("LIKED"))
                    ulink = "http://124.153.73.2/airtel/fb_api.php?mode=post&msisdn=" + ANI + "&message=loves%20the%20song%20SongName%20from%20AlbumName%20on%20Uninor%20MyMusic.%20Dial%2052555%20to%20listen%20to%20ur%20favorite%20Music%20on%20your%20Uninor%20Mobile%20and%20share%20with%20your%20Facebook%20Friends%20on%20the%20Move!&cid=" + vcode;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            out.close();
            out = null;
            if(cs1 != null)
            {
                try
                {
                    cs1.close();
                }
                catch(SQLException sqlexception) { }
                cs1 = null;
            }
            if(conat != null)
            {
                try
                {
                    conat.close();
                }
                catch(SQLException sqlexception1) { }
                conat = null;
            }
        }
        return;
    }

    private String formatN(String str, int x)
    {
        String ret_str = "";
        int len = str.length();
        if(len >= x)
        {
            ret_str = str;
        } else
        {
            for(int i = 0; i < x - len; i++)
                ret_str = ret_str + "0";

            ret_str = ret_str + str;
        }
        return ret_str;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    public String getServletInfo()
    {
        return "Short description";
    }

    private static final long serialVersionUID = 1L;
    static DataSource ds;
}
