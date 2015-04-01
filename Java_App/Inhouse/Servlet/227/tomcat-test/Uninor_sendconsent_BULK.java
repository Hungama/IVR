import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;
import java.util.*;


import java.util.StringTokenizer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.NamedNodeMap;


public class Uninor_sendconsent_BULK extends HttpServlet
{
	String strUrl;
	String strCGID;

public String getDTTM(Date d1, String strformat)
 	{
		String strDT;

		strDT = new SimpleDateFormat(strformat).format(d1);

		return strDT;

}

public void myparsexml(String strCG)
{
	try{

	strCGID = "";
    String xmlRecords = "<data>"+strCG+"</data>";
        String[] temp;
		    String[] temp2;
		    String[] temp3;
		    String[] temp4;
		    String[] temp5;
    String[] temp6;

    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(xmlRecords));

    Document doc = db.parse(is);
    NodeList nodes = doc.getElementsByTagName("var");

    for (int i = 0; i < nodes.getLength(); i++) {
      Element element = (Element) nodes.item(i);

      Node staff = doc.getElementsByTagName("var").item(0);

	  NamedNodeMap attr = staff.getAttributes();
      Node nodeAttr = attr.getNamedItem("expr");
      String mynode = nodeAttr.toString();
	      temp = mynode.split("=");
	      temp2 = temp[1].split("\"");
    	temp3 = temp2[1].split("'");
      System.out.println("CG ID is - " + temp3[1]);
      strCGID = temp3[1].toString();

/*      NodeList name = element.getElementsByTagName("name");
      Element line = (Element) name.item(0);
      System.out.println("Name: " + getCharacterDataFromElement(line));

      NodeList title = element.getElementsByTagName("title");
      line = (Element) title.item(0);
      System.out.println("Title: " + getCharacterDataFromElement(line));*/
    }
}catch(Exception e)
{

}

}



public void hitsecondconsent()
{

try{
	String strConURL = strUrl+"&CGID="+strCGID+"&SNDCONSENT=9";
	System.out.println("consent URL is - "+strConURL);

	URL url1 = new URL(strConURL);
	HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
	connection.setDoOutput(true);
	connection.setInstanceFollowRedirects(false);
	connection.setRequestMethod("GET");
	connection.setRequestProperty("Content-Type", "text/plain");
	connection.setRequestProperty("charset", "utf-8");
	connection.connect();


	BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	StringBuffer sbr = new StringBuffer();
	String line=null;
	while((line=br.readLine())!=null)
	{
		System.out.println(line.toString());
	}
}catch(Exception e)
{

}

}

public void doPost(HttpServletRequest request, HttpServletResponse response)
{
    try
    {
		sendconsent s1=new sendconsent();
        System.out.println("Echo ANI Demo");
        //response.setContentType("text/event-stream");

        String path = request.getParameter("PATH");
        String lang = request.getParameter("LANG");
        String schn = request.getParameter("SCHN");
        String cp = request.getParameter("CP");
        String msisdn = request.getParameter("MSISDN");
        String cppid = request.getParameter("CPPID");
        String prodtype = request.getParameter("PRODTYPE");
        String pmarkname = request.getParameter("PMARKNAME");
        String price = request.getParameter("PRICE");
        String se = request.getParameter("SE");
        Date date1 = new Date();
        String cptid = s1.getDTTM(date1, "yyyyMMddhhmmss");
        String dt = s1.getDTTM(date1, "yyyy-MM-dd%20hh:mm:ss");
        String pd = request.getParameter("PD");
        String scode = request.getParameter("SCODE");
        String rsv = request.getParameter("RSV");
        String rsv2 = request.getParameter("RSV2");

        String strCGUrl = "http://180.178.28.62:7001/IVR/SecondConsentIVRRequestSyncPS?";
        String strCGUrlCon = "http://180.178.28.62:7001/CG/Request?";
        String strParam1 = "PATH="+path+"&LANG="+lang+"&";
        String strParam2 = "SCHN="+schn+"&CP="+cp+"&MSISDN="+msisdn+"&CPPID="+cppid+"&PRODTYPE="+prodtype+"&PMARKNAME="+pmarkname+"&PRICE="+price+"&SE="+se+"&CPTID="+cptid+"&DT="+dt+"&PD="+pd+"&SCODE="+scode+"&RSV="+rsv+"&RSV2="+rsv2;

		String reqstr=strCGUrl+strParam1+strParam2;
		//System.out.println("Request URL is - "+reqstr);
		s1.setUrlStr(strCGUrlCon+strParam2);

		URL url1 = new URL(reqstr);
		HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "text/plain");
		connection.setRequestProperty("charset", "utf-8");
		connection.connect();


		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer sbr = new StringBuffer();
		String line=null;
		while((line=br.readLine())!=null)
		{
			String checkStr = line.toString();
			if(checkStr.contains("<var name=\"CGID\""))
			{
				System.out.println("got the line from out stream - "+ line.toString());
				s1.myparsexml(line.toString());
			}

		}
		int	randomNum = 1 + (int)(Math.random()*5);
		randomNum=randomNum*1000;
		Thread.sleep(randomNum);
		s1.hitsecondconsent();

        //PrintWriter pw = response.getWriter();

        //int i=0;
        //while(true)
       // {

            //i++;
            //pw.write("Done\n\n");  //take note of the 2 \n 's, also on the next line.

       //     break;
       // }
       // pw.close();

    }catch(Exception e){
        e.printStackTrace();
    }
}

public void setUrlStr(String strurl)
	{
		strUrl = strurl.toString();
	}

public void doGet(HttpServletRequest request,HttpServletResponse response)
{
    doPost(request,response);
}

}