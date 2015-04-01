

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.activation.DataSource;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class TataSecondConsent
 */
public class TataSecondConsent extends HttpServlet {
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TataSecondConsent() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private static final long serialVersionUID = 1L;
    private static Connection con=null;
	private static String dsn="Inhouse_IVR";
	private static String IP="192.168.100.224:3306";
	private static String username="ivr",pwd="ivr";

	
	private static Statement stmtselect=null;
     
    private Connection dbcon()
	{
		while(true)
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				con=DriverManager.getConnection("jdbc:mysql://"+IP+"/"+dsn, username,pwd);
				System.out.println("Database Connection established!");
				return con;
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String MSISDN=request.getParameter("MSISDN");
		String productID=request.getParameter("productID");
		String pName=request.getParameter("pName");
		
		String NetworkId=request.getParameter("NetworkId");
		String circleId=request.getParameter("circleId");
		String Lang=request.getParameter("Lang");
		String Lang_A=request.getParameter("Lang_A");
		String reqType=request.getParameter("reqType");
		String transID=request.getParameter("transID");
		String pPrice=request.getParameter("pPrice");
		String pVal=request.getParameter("pVal");
		
		String CpId=request.getParameter("CpId");
		String CpName=request.getParameter("CpName");
		String CpPwd=request.getParameter("CpPwd");
		
		
		
		
		
			String ulink="http://182.156.191.80:8091/API/CCG?MSISDN=91"+MSISDN+"&productID="+productID+"&pName="+pName+"&reqMode=IVR&Ivr_approach=VXML&NetworkId="+NetworkId+"&circleId="+circleId+"&Lang="+Lang+"&Lang_A="+Lang_A+"&reqType="+reqType+"&ismID=16&transID="+transID+"&pPrice="+pPrice+"&pVal="+pVal+"&CpId="+CpId+"&CpName="+CpName+"&CpPwd="+CpPwd+"&Songname=abc";
			try{
			
				PrintWriter out = response.getWriter();
				URL url = new URL(ulink);
        		System.out.println("Going to open conn");
        		System.out.println(ulink);
        		HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
        		String getResponse="";
		if(urlconn.getResponseCode()== HttpURLConnection.HTTP_OK)
			{

				BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
				String line="";
				
				System.out.println("*******************START*************************");
				while ((line= in.readLine()) != null)
				{
					getResponse = getResponse + line;
					System.out.println(line);
				}
					in.close();
				urlconn.disconnect();
				System.out.println("*******************END***************************");
				getResponse = getResponse.trim();
				
				/* for getting the file size */
				byte[] b= new byte[100000];
				
				System.out.println("Prompt getting from tata : "+transID);				
				AudioInputStream ais = AudioSystem.getAudioInputStream(new URL("http://182.156.191.80:8092/mcarbon/mcarbon_ARP/media/F/Hin/"+transID+"hug.wav"));
				
				AudioFormat format = ais.getFormat();
						
			//	length=ais.getFrameLength();
				
				System.out.println(format.getFrameSize());
				
				int filesize=0;
				
				int tempq=0;
				
				while((tempq=ais.read(b))!=-1)
				{
					filesize=filesize+tempq;
				}
				System.out.println("File size iss : "+filesize);
				
				dbcon();
				stmtselect=con.createStatement();
				String userDtmf="";
				String str=null;
				str="select dtmf from Inhouse_IVR.tbl_tata_bypass where filesize="+filesize;
				ResultSet rs=stmtselect.executeQuery(str);
				
				while(rs.next())
				{
					userDtmf=rs.getString("dtmf");
				}
				
				String secondLink="http://182.156.191.80:8092/mcarbon/mcarbon_ARP/ValidateSecond.jsp?userinput="+userDtmf+"tranId="+transID+"hug";
				
				URL secondurl = new URL(secondLink);
        		System.out.println("Going to open conn");
        		System.out.println(secondLink);
        		HttpURLConnection secondurlconn = (HttpURLConnection)secondurl.openConnection();
        		
        		if(secondurlconn.getResponseCode()== HttpURLConnection.HTTP_OK)
        		{
        			System.out.println("Second url success");
        			
        			String thirdLink="http://182.156.191.80:8092/mcarbon/mcarbon_ARP/ValidateSecond_2.jsp?userinput="+userDtmf+"tranId="+transID+"hug";
        			URL thirdurl = new URL(thirdLink);
            		System.out.println("Going to open conn");
            		System.out.println(thirdLink);
            		HttpURLConnection thirdurlconn = (HttpURLConnection)thirdurl.openConnection();
            		if(thirdurlconn.getResponseCode()== HttpURLConnection.HTTP_OK)
            		{
            			System.out.println("Third url success");
            		}

        		}
				
			}
			else
			{
				getResponse = "Error!Its Not HTTP_OK"+urlconn.getResponseCode();
				out.println(getResponse);
			}
		
		

			}catch(Exception eee)
			{
				eee.printStackTrace();
			}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

}

