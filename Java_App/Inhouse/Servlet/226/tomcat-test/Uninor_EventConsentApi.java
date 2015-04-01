

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
public class Uninor_EventConsentApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Uninor_EventConsentApi() {
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
    	  String SCHN=request.getParameter("SCHN");
    	  String CP=request.getParameter("CP");
    	  String MSISDN=request.getParameter("MSISDN");
    	  String CPPID=request.getParameter("CPPID");
    	  String PRODTYPE=request.getParameter("PRODTYPE");
    	  String PMARKNAME=request.getParameter("PMARKNAME");
    	  String SE=request.getParameter("SE");
    	  String CPTID=request.getParameter("CPTID");
    	  String DT=request.getParameter("DT");
    	  String PD=request.getParameter("PD");
    	  
    	  String SCODE=request.getParameter("SCODE");
    	  String RSV=request.getParameter("RSV");
    	  String RSV2=request.getParameter("RSV2");
    	  
    	  String urllink=null;
    	
    	  try{
    		
    		  	urllink=ulink+"?SCHN="+SCHN+"&CP="+CP+"&MSISDN="+MSISDN+"&CPPID="+CPPID+"&PRODTYPE="+PRODTYPE+"&PMARKNAME="+PMARKNAME+"&SE="+SE+"&CPTID="+CPTID+"&DT="+DT+"&PD="+PD+"&SCODE="+SCODE+"&RSV="+RSV+"&RSV2="+RSV2;
    		  
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
		    //	NodeList eList = doc.getElementsByTagName("ErrorCode");
		    //	Node eNode = eList.item(0);
		    //	Element resElement = (Element) eNode;
		    	
		    //	String responsecode=resElement.getAttribute("value");
		    	
		    //	System.out.println("responsecode is :" +responsecode );
		    	System.out.println("Final url is :" +url +" and xml is :- "+cline);
		    	
		   /* 	<CP_Response>
		    	<SOURCE>IVR_EVT</SOURCE>
		    	<CGID>9711711335_bb94-786c4d0d7ffb</CGID>
		    	<MSISDN>9711711335</MSISDN>
		    	<RESULT>Failure</RESULT>
		    	<REASON>Error Message Format</REASON>
		    	<CODE>002</CODE>
		    	<CPTID>20131108134240</CPTID>
		    	</CP_Response>
		    	*/
		    	
		    	String SOURCE=null,CGID=null,RESULT=null,REASON=null,CODE=null;
	    		NodeList nList = doc.getElementsByTagName("CP_Response");
		    	for (int temp = 0; temp < nList.getLength(); temp++) {
		    	
		    		Node nNode = nList.item(temp);
		     		System.out.println("\nCurrent Element :" + nNode.getNodeName());
		     		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		     			Element eElement = (Element) nNode;
		     			
		     			NodeList SOURCEList = eElement.getElementsByTagName("SOURCE");
		     			NodeList CGIDList = eElement.getElementsByTagName("CGID");
		     			NodeList MSISDNList = eElement.getElementsByTagName("MSISDN");
		     			NodeList RESULTList = eElement.getElementsByTagName("RESULT");
		     			NodeList REASONList = eElement.getElementsByTagName("REASON");
		     			NodeList CODEList = eElement.getElementsByTagName("CODE");
		     			NodeList CPTIDList = eElement.getElementsByTagName("CPTID");
		     			
		     			SOURCE=SOURCEList.item(0).getChildNodes().item(0).getNodeValue();
		     			CGID=CGIDList.item(0).getChildNodes().item(0).getNodeValue();
		     			MSISDN=MSISDNList.item(0).getChildNodes().item(0).getNodeValue();
		     			RESULT=RESULTList.item(0).getChildNodes().item(0).getNodeValue();
		     			REASON=REASONList.item(0).getChildNodes().item(0).getNodeValue();
		     			CODE=CODEList.item(0).getChildNodes().item(0).getNodeValue();
		     			CPTID=CPTIDList.item(0).getChildNodes().item(0).getNodeValue();
                        
		    		}
		    	}
		    	
		    	String out_string="out_string.length=7;out_string[0]='"+SOURCE+"';out_string[1]='"+CGID+"';out_string[2]='"+MSISDN+"';out_string[3]='"+RESULT+"';out_string[4]='"+REASON+"';out_string[5]='"+CODE+"';out_string[6]='"+CPTID+"'";
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

