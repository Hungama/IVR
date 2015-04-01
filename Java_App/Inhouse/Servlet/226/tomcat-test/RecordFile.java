import java.io.BufferedReader;
import java.io.File;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RecordFile extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public RecordFile() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		        
		if (request.getProtocol().equals ("HTTP/1.1")){
		    response.setHeader ("Cache-Control", "no-cache");}
		
		response.setContentType("application/ecmascript");
		PrintWriter out = response.getWriter();
		String rq = null;
		String ani = null;	
		String dni = null;	
		String lang=null;
		String lgrecmsg=null;
		
		 HashMap MySongs;
		 ArrayList MZ = new ArrayList();
		 int[] Randam_number=new int[1000];
		 ListIterator ite = null;
		 String bollywood_details = null;
		 String LGQA_details = null;
		 String LGPRQA_details = null;
		 
		 
		 int counter = 0;
		 
		 MySongs = new HashMap();
	
		
	   try
	   {	
		
		
		
		rq = request.getParameter("rq");		
		ani = request.getParameter("ani");
		dni = request.getParameter("dni");		
		lang=request.getParameter("lang");	
		lgrecmsg=request.getParameter("lgrecmsg");	
		
					
			
		
		
		if ((rq.equalsIgnoreCase("fed")))// feedback
		{
			
			try {
				   String temp = "",recDir= "";
	                
		                 System.out.println("record message dir name is ::"+ recDir);
		                 System.out.println("record message file name is ::"+ lgrecmsg);
		   			     //String dtFile = getServletContext().getRealPath("Content/"+recDir+"/LG_"+lang+"_UR_"+ani+"_"+dni+".wav");		           
		   			     String dtFile = getServletContext().getRealPath("promptFiles/cricket/record/FEDBCK_"+lang+"_"+ani+"_"+dni+".wav");
		   			  
		   	             System.out.println("sourse File ."+lgrecmsg);
		   	             System.out.println("destination File ."+ dtFile);
		   	             copyfile(lgrecmsg,dtFile);
		          
		            
		         }
		         catch (Exception e) {
		         	e.printStackTrace();             
		         }
		         finally
		         {		       	  
		       	  out.close();
		       	  ite = null;
		       	  counter = 0;
		         }     
		       	   
		       
			
		}else if ((rq.equalsIgnoreCase("rec_share")))// feedback
		{
			
			try {
				   String temp = "",recDir= "";
	                
		                 System.out.println("record message dir name is ::"+ recDir);
		                 System.out.println("record message file name is ::"+ lgrecmsg);
		   			     //String dtFile = getServletContext().getRealPath("Content/"+recDir+"/LG_"+lang+"_UR_"+ani+"_"+dni+".wav");		           
		   			     String dtFile = getServletContext().getRealPath("promptFiles/cricket/record/RECSHARE_"+lang+"_"+ani+"_"+dni+".wav");
		   			  
		   	             System.out.println("sourse File ."+lgrecmsg);
		   	             System.out.println("destination File ."+ dtFile);
		   	             copyfile(lgrecmsg,dtFile);
		          
		            
		         }
		         catch (Exception e) {
		         	e.printStackTrace();             
		         }
		         finally
		         {		       	  
		       	  out.close();
		       	  ite = null;
		       	  counter = 0;
		         }     
		       	   
		       
			
		}
		
		
	   }
	   catch (Exception e)
	   {
	 	 e.printStackTrace();
	 	 //logging.filelogger("Exception in Processing Game Portal Request. "+e.toString());
	   }
	   finally
	   {
		    out.close();
		    rq = null;
			ani = null;
			dni = null;
			
	   }
		
	}
	
	public void copyfile(String srFile, String dtFile){
	    try{
	      File f1 = new File(srFile);
	      File f2 = new File(dtFile);
	      InputStream srin = new FileInputStream(f1);
	   
	      OutputStream dstout = new FileOutputStream(f2);

	      byte[] buf = new byte[1024];
	      int len;
	      while ((len = srin.read(buf)) > 0){
	        dstout.write(buf, 0, len);
	      }
	      srin.close();
	      dstout.close();
	      System.out.println("File copied.");
	    }
	    catch(FileNotFoundException ex){
	      System.out.println(ex.getMessage() + " in the specified directory.");
	      System.exit(0);
	    }
	    catch(IOException e){
	      System.out.println(e.getMessage());      
	    }
	  }

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processRequest(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processRequest(request, response);
	}

	/***
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
