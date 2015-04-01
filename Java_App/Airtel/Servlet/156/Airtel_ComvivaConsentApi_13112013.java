

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Servlet implementation class Airtel_ComvivaConsentApi
 */
public class Airtel_ComvivaConsentApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Airtel_ComvivaConsentApi() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void processRequest(HttpServletRequest request,
			HttpServletResponse res) throws ServletException, IOException {
    	
    	  if (request.getProtocol().equals("HTTP/1.1")) {
    	       res.setHeader("Cache-Control", "no-cache");
    	     }
    	     res.setContentType("application/ecmascript");
    	  
    	     res.setContentType("text/html;charset=UTF-8");
    	     
    	  String ulink=request.getParameter("ulink");
    	  String msisdn=request.getParameter("msisdn");
    	  String uid=request.getParameter("uid");
    	  String pass=request.getParameter("pass");
    	  String myTune=request.getParameter("myTune");
    	  String stype=request.getParameter("stype");
    	  String rentType=request.getParameter("rentType");
    	  String vcode=request.getParameter("vcode");
    	  String ucode=request.getParameter("ucode");
    	  String cbsmsisdn=request.getParameter("cbsmsisdn");
    	  String downChg=request.getParameter("downChg");
    	  String reserve=request.getParameter("reserve");
    	  String downPrice=request.getParameter("downPrice");
    	  String myAlbum=request.getParameter("myAlbum");
    	  String lang=request.getParameter("lang");
    	  String cLogs=request.getParameter("cLogs");
    	  String ConsentParam=request.getParameter("ConsentParam");
    	  String channelName=request.getParameter("channelName");
    	  String cpTransId=request.getParameter("cpTransId");
    	  String zone=request.getParameter("zone");
    	  String circle=request.getParameter("circle");
    	  
    	  String urllink=null;
    	
    	  try{
    		
    		  if(ConsentParam.equalsIgnoreCase("C"))
    		  {	
	    		urllink=ulink+"?msisdn="+msisdn+"&uid="+uid+"&pass="+pass+"&stype="+stype+"&vcode="+vcode+"&ucode="+ucode+"&cbsmsisdn=&downChg="+downChg+"&reserve="+reserve+"&myTune="+myTune+"&downPrice="+downPrice+"&myAlbum="+myAlbum+"&rentType="+rentType+"&lang="+lang+"&ConsentParam="+ConsentParam+"&channelName="+channelName;
    		  }
    		  else
    		  {
    			  urllink=ulink+"?msisdn="+msisdn+"&uid="+uid+"&pass="+pass+"&stype="+stype+"&vcode="+vcode+"&ucode="+ucode+"&cbsmsisdn=&downChg="+downChg+"&reserve="+reserve+"&myTune="+myTune+"&downPrice="+downPrice+"&myAlbum="+myAlbum+"&rentType="+rentType+"&lang="+lang+"&ConsentParam="+ConsentParam+"&channelName="+channelName+"&cpTransId="+cpTransId;
    		  }
	    		URL url=new URL(urllink);
	    		HttpURLConnection connection =(HttpURLConnection)url.openConnection();
	    		
	    		connection.connect();
	    		
	    		InputStream instream=connection.getInputStream();
	    		BufferedReader br=new BufferedReader(new InputStreamReader(instream));
	    		
	    		
	    		PrintWriter pw = res.getWriter();
	    		
		    	String line,cline="";
		    	
		    	while((line=br.readLine())!=null)
		    	{
		    		cline=cline+line+"\n";
		    	}
		    	
	    	
	    		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    
		    	InputSource is = new InputSource();
		        is.setCharacterStream(new StringReader(cline));
		        
		    	Document doc=dBuilder.parse(is);
		    	doc.getDocumentElement().normalize();
		    	 
		    	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		    	NodeList eList = doc.getElementsByTagName("ErrorCode");
		    	Node eNode = eList.item(0);
		    	Element resElement = (Element) eNode;
		    	
		    	String responsecode=resElement.getAttribute("value");
		    	
		    	System.out.println("responsecode is :" +responsecode );
		    	System.out.println("Final url is :" +url +" and xml is :- "+cline);
		    	
		    	String cptId=null,Sbid=null,sopid=null,sovc=null;
	    		NodeList nList = doc.getElementsByTagName("parameters");
		    	for (int temp = 0; temp < nList.getLength(); temp++) {
		    		Node nNode = nList.item(temp);
		     		System.out.println("\nCurrent Element :" + nNode.getNodeName());
		     		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		     			Element eElement = (Element) nNode;
		     			cptId=eElement.getAttribute("cpt");
		     			Sbid=eElement.getAttribute("Sbid");
		     			sopid=eElement.getAttribute("sopid");
		     			sovc=eElement.getAttribute("sovc");
		    		}
		    	}
		    	
		    	String out_string="out_string.length=5;out_string[0]='"+responsecode+"';out_string[1]='"+cptId+"';out_string[2]='"+Sbid+"';out_string[3]='"+sopid+"';out_string[4]='"+sovc+"'";
		    	pw.print(out_string);
			
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	  }
    	
       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}

