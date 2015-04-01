

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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.NamedNodeMap;

/**
 * Servlet implementation class Airtel_SouthActivationApi
 */
public class Airtel_SouthActivationApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Airtel_SouthActivationApi() {
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
	 	  String vcode=request.getParameter("vcode");
	 	  String mode=request.getParameter("mode");
	 	  String dnis=request.getParameter("dnis");
	 	  String subscriptionClass=request.getParameter("subscriptionClass");
	 	  String chargeClass=request.getParameter("chargeClass");
	 	  String zone=request.getParameter("zone");
	 	  String circle=request.getParameter("circle");
	 	  
	 	 String urllink=null;
	    	
    	 try{
	    		PrintWriter pw = res.getWriter();
	    		
	    		urllink=ulink+"?action=SET&subscriberID="+msisdn+"&callerID=ALL&categoryID=6&clipID=rbt_"+vcode+"_rbt&isPrepaid=y&mode="+mode+"&calledNo="+dnis+"&subscriptionClass="+subscriptionClass+"&chargeClass="+chargeClass;
	    		
	    		URL url=new URL(urllink);
	    		HttpURLConnection connection =(HttpURLConnection)url.openConnection();
	    		
	    		connection.setDoOutput(true);
	    		connection.setInstanceFollowRedirects(false);
	    		connection.setRequestMethod("GET");
	    		connection.setRequestProperty("Content-Type", "text/plain");
	    		connection.setRequestProperty("charset", "utf-8");
	    		connection.connect();
	    		
	    		InputStream instream=connection.getInputStream();
	    		BufferedReader br=new BufferedReader(new InputStreamReader(instream));
	    		
	    				
		    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    	
		    	String line,cline="";
		    	
		    	while((line=br.readLine())!=null)
		    	{
		    		cline=cline+line+"\n";
		    	}
		    	
		    	InputSource is = new InputSource();
		        is.setCharacterStream(new StringReader(cline));
		        
		    	Document doc=dBuilder.parse(is);
		    	doc.getDocumentElement().normalize();
		    	 
		    	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		    	
		    	
		    	String responce="0",transID="0";
		    	
		    	/*BufferedReader br1=new BufferedReader(new InputStreamReader(instream));
		    	line="";
		    	while((line=br1.readLine())!=null)
		    	{
		    		if(line.contains("<rbt><response>success</response>"))
					{
		    			System.out.println("if part");
		    			responce="success";
		    			break;
					}
		    		else{
		    			System.out.println("else part");
		    			responce="failure";
		    		}
		    	}*/
		    	
		    	if(cline.contains("<rbt><response>success</response>"))
				{
	    			System.out.println("if part");
	    			responce="success";
	    		}
	    		else{
	    			System.out.println("else part");
	    			responce="failure";
	    		}
		    	
		    	
		    	System.out.println("url iss :- "+url+"   responce="+responce+"  cline:="+cline);
		    	
		    	if(responce=="success"){
		    		NodeList nList = doc.getElementsByTagName("consent");
			    	for (int temp = 0; temp < nList.getLength(); temp++) {
			    		Node nNode = nList.item(temp);
			     		System.out.println("\nCurrent Element :" + nNode.getNodeName());
			     		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			     			Element eElement = (Element) nNode;
			     			//System.out.println("transID id : " + eElement.getAttribute("transID"));
			     			transID=eElement.getAttribute("transID");
			     			//pw.print("transID id :" + eElement.getAttribute("transID"));
			    		}
			    	}
	    		}
		    	String out_string="out_string.length=2;out_string[0]='"+responce+"';out_string[1]='"+transID+"'";
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
