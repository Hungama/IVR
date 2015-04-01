

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

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
		    	
		    	if(cline.contains("<rbt><response>success</response>"))
				{
	    			System.out.println("if part");
	    			responce="success";
	    		}
	    		else{
	    			System.out.println("else part");
	    			responce="failure";
	    		}
		    	
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
		    	
		    	/*  Only for logging   */
		    	
		    	Calendar today = Calendar.getInstance();

				String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
						+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
						+ formatN("" + today.get(Calendar.DATE), 2);
				FileOutputStream outp = null; // declare a file output object
				File ServiceFolder = new File("/home/Hungama_call_logs/HTLogs/");
				if (!ServiceFolder.exists())
				{
					ServiceFolder.mkdir();
					outp = new FileOutputStream("/home/Hungama_call_logs/HTLogs/HT_download_"+strlogfile+".txt", true);
				}
				else
				{
					outp = new FileOutputStream("/home/Hungama_call_logs/HTLogs/HT_download_"+strlogfile+".txt", true);
				}
				synchronized(this)
				{
					try
					{
						String finalurl=url +"/n" +cline;
						PrintWriter p1=new PrintWriter(outp);
						p1.println(finalurl);
						p1.flush();
						p1.close();
						outp.flush();
						outp.close();
					}catch(Exception E)
					{
						System.out.println("outp is not opened");
					}
				}

			
    	}catch(Exception e)
    	{
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
