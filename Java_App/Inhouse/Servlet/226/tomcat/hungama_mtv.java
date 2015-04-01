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


public class hungama_mtv extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public hungama_mtv() {
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
		
		//http://localhost:8080/aajkahungama/hungama_mtv?rq=mtv1&lang=HIN // request send for Top20
        //http://localhost:8080/aajkahungama/hungama_mtv?rq=mtv2&lang=HIN // request send for Chartbusters
        //http://localhost:8080/aajkahungama/hungama_mtv?rq=mtv3&lang=HIN // request send for Shuffle
        //http://localhost:8080/aajkahungama/hungama_mtv?rq=mtv4&lang=HIN // request send for ItemSongs
        //http://localhost:8080/aajkahungama/hungama_mtv?rq=mtv5&lang=HIN // request send for RomanticSongs
        //http://localhost:8080/aajkahungama/hungama_mtv?rq=mtv6&lang=HIN // request send for OldClassics
        //http://localhost:8080/aajkahungama/hungama_mtv?rq=mtv7&lang=HIN // request send for edgysongs
        
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
		 ListIterator ite = null;
		 String songs_details = null;		
		 
		 
		 int counter = 0;
		 
		 MySongs = new HashMap();
	
		
	   try
	   {	
		
		
		
		rq = request.getParameter("rq");		
		ani = request.getParameter("ani");
		dni = request.getParameter("dni");		
		lang=request.getParameter("lang");	
		lgrecmsg=request.getParameter("lgrecmsg");	
		
		if ((rq.equalsIgnoreCase("mtv1")))  //Top20
		{
			try {
				   String temp = "";
		           File file_txt1 = new File(getServletContext().getRealPath("config/"+lang+"_mtv_Top20.txt"));
		           if (file_txt1.exists())
		             {

		                 BufferedReader in = new BufferedReader(new FileReader(file_txt1));
		                 temp=null;                   
		                  while ((temp = in.readLine()) != null) {
		                       MZ.add(temp);
		                       temp=null;
		                       
		                  }
		                 MySongs.put("MyZone", MZ);                   
		                 ArrayList obj = (ArrayList) MySongs.get("MyZone");
		                 ite = obj.listIterator();

		                 songs_details = "songs_details.length=" + obj.size();
		                 counter = 0;
		                 while (ite.hasNext()) {
		                	 songs_details = songs_details + ";" + "songs_details" + "[" + counter + "]" + "=" + "'" + ite.next() + "'";
		                     counter++;
		                 }
		                 MZ.clear();
		                 MySongs.clear();
		                 out.println(songs_details);      
		                 System.out.println("songs_details ----" + songs_details);
		             }
		             else
		             {	             	    	   
		         	   
		             	System.out.println(getServletContext().getRealPath("config/"+lang+"_mtv_Top20.txt")+ "bollywood file Could not found ----->");	

		             }
		            
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
		else if ((rq.equalsIgnoreCase("mtv2")))  //Chartbusters
		{
			try {
				   String temp = "";
		           File file_txt1 = new File(getServletContext().getRealPath("config/"+lang+"_mtv_Chartbusters.txt"));
		           if (file_txt1.exists())
		             {

		                 BufferedReader in = new BufferedReader(new FileReader(file_txt1));
		                 temp=null;                   
		                  while ((temp = in.readLine()) != null) {
		                       MZ.add(temp);
		                       temp=null;
		                       
		                  }
		                 MySongs.put("MyZone", MZ);                   
		                 ArrayList obj = (ArrayList) MySongs.get("MyZone");
		                 ite = obj.listIterator();

		                 songs_details = "songs_details.length=" + obj.size();
		                 counter = 0;
		                 while (ite.hasNext()) {
		                	 songs_details = songs_details + ";" + "songs_details" + "[" + counter + "]" + "=" + "'" + ite.next() + "'";
		                     counter++;
		                 }
		                 MZ.clear();
		                 MySongs.clear();
		                 out.println(songs_details);      
		                 System.out.println("songs_details ----" + songs_details);
		             }
		             else
		             {	             	    	   
		         	   
		             	System.out.println(getServletContext().getRealPath("config/"+lang+"_mtv_Chartbusters.txt")+ "bollywood file Could not found ----->");	

		             }
		            
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
		else if ((rq.equalsIgnoreCase("mtv3")))  //Shuffle
		{
			try {
				   String temp = "";
		           File file_txt1 = new File(getServletContext().getRealPath("config/"+lang+"_mtv_Shuffle.txt"));
		           if (file_txt1.exists())
		             {

		                 BufferedReader in = new BufferedReader(new FileReader(file_txt1));
		                 temp=null;                   
		                  while ((temp = in.readLine()) != null) {
		                       MZ.add(temp);
		                       temp=null;
		                       
		                  }
		                 MySongs.put("MyZone", MZ);                   
		                 ArrayList obj = (ArrayList) MySongs.get("MyZone");
		                 ite = obj.listIterator();

		                 songs_details = "songs_details.length=" + obj.size();
		                 counter = 0;
		                 while (ite.hasNext()) {
		                	 songs_details = songs_details + ";" + "songs_details" + "[" + counter + "]" + "=" + "'" + ite.next() + "'";
		                     counter++;
		                 }
		                 MZ.clear();
		                 MySongs.clear();
		                 out.println(songs_details);      
		                 System.out.println("songs_details ----" + songs_details);
		             }
		             else
		             {	             	    	   
		         	   
		             	System.out.println(getServletContext().getRealPath("config/"+lang+"_mtv_Shuffle.txt")+ "bollywood file Could not found ----->");	

		             }
		            
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
		else if ((rq.equalsIgnoreCase("mtv4")))  //ItemSongs
		{
			try {
				   String temp = "";
		           File file_txt1 = new File(getServletContext().getRealPath("config/"+lang+"_mtv_ItemSongs.txt"));
		           if (file_txt1.exists())
		             {

		                 BufferedReader in = new BufferedReader(new FileReader(file_txt1));
		                 temp=null;                   
		                  while ((temp = in.readLine()) != null) {
		                       MZ.add(temp);
		                       temp=null;
		                       
		                  }
		                 MySongs.put("MyZone", MZ);                   
		                 ArrayList obj = (ArrayList) MySongs.get("MyZone");
		                 ite = obj.listIterator();

		                 songs_details = "songs_details.length=" + obj.size();
		                 counter = 0;
		                 while (ite.hasNext()) {
		                	 songs_details = songs_details + ";" + "songs_details" + "[" + counter + "]" + "=" + "'" + ite.next() + "'";
		                     counter++;
		                 }
		                 MZ.clear();
		                 MySongs.clear();
		                 out.println(songs_details);      
		                 System.out.println("songs_details ----" + songs_details);
		             }
		             else
		             {	             	    	   
		         	   
		             	System.out.println(getServletContext().getRealPath("config/"+lang+"_mtv_ItemSongs.txt")+ "bollywood file Could not found ----->");	

		             }
		            
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
		else if ((rq.equalsIgnoreCase("mtv5")))  //RomanticSongs
		{
			try {
				   String temp = "";
		           File file_txt1 = new File(getServletContext().getRealPath("config/"+lang+"_mtv_RomanticSongs.txt"));
		           if (file_txt1.exists())
		             {

		                 BufferedReader in = new BufferedReader(new FileReader(file_txt1));
		                 temp=null;                   
		                  while ((temp = in.readLine()) != null) {
		                       MZ.add(temp);
		                       temp=null;
		                       
		                  }
		                 MySongs.put("MyZone", MZ);                   
		                 ArrayList obj = (ArrayList) MySongs.get("MyZone");
		                 ite = obj.listIterator();

		                 songs_details = "songs_details.length=" + obj.size();
		                 counter = 0;
		                 while (ite.hasNext()) {
		                	 songs_details = songs_details + ";" + "songs_details" + "[" + counter + "]" + "=" + "'" + ite.next() + "'";
		                     counter++;
		                 }
		                 MZ.clear();
		                 MySongs.clear();
		                 out.println(songs_details);      
		                 System.out.println("songs_details ----" + songs_details);
		             }
		             else
		             {	             	    	   
		         	   
		             	System.out.println(getServletContext().getRealPath("config/"+lang+"_mtv_RomanticSongs.txt")+ "bollywood file Could not found ----->");	

		             }
		            
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
		else if ((rq.equalsIgnoreCase("mtv6")))  //OldClassics
		{
			try {
				   String temp = "";
		           File file_txt1 = new File(getServletContext().getRealPath("config/"+lang+"_mtv_OldClassics.txt"));
		           if (file_txt1.exists())
		             {

		                 BufferedReader in = new BufferedReader(new FileReader(file_txt1));
		                 temp=null;                   
		                  while ((temp = in.readLine()) != null) {
		                       MZ.add(temp);
		                       temp=null;
		                       
		                  }
		                 MySongs.put("MyZone", MZ);                   
		                 ArrayList obj = (ArrayList) MySongs.get("MyZone");
		                 ite = obj.listIterator();

		                 songs_details = "songs_details.length=" + obj.size();
		                 counter = 0;
		                 while (ite.hasNext()) {
		                	 songs_details = songs_details + ";" + "songs_details" + "[" + counter + "]" + "=" + "'" + ite.next() + "'";
		                     counter++;
		                 }
		                 MZ.clear();
		                 MySongs.clear();
		                 out.println(songs_details);      
		                 System.out.println("songs_details ----" + songs_details);
		             }
		             else
		             {	             	    	   
		         	   
		             	System.out.println(getServletContext().getRealPath("config/"+lang+"_mtv_OldClassics.txt")+ "bollywood file Could not found ----->");	

		             }
		            
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
		else if ((rq.equalsIgnoreCase("mtv7")))  //Top20
		{
			try {
				   String temp = "";
		           File file_txt1 = new File(getServletContext().getRealPath("config/"+lang+"_mtv_edgysongs.txt"));
		           if (file_txt1.exists())
		             {

		                 BufferedReader in = new BufferedReader(new FileReader(file_txt1));
		                 temp=null;                   
		                  while ((temp = in.readLine()) != null) {
		                       MZ.add(temp);
		                       temp=null;
		                       
		                  }
		                 MySongs.put("MyZone", MZ);                   
		                 ArrayList obj = (ArrayList) MySongs.get("MyZone");
		                 ite = obj.listIterator();

		                 songs_details = "songs_details.length=" + obj.size();
		                 counter = 0;
		                 while (ite.hasNext()) {
		                	 songs_details = songs_details + ";" + "songs_details" + "[" + counter + "]" + "=" + "'" + ite.next() + "'";
		                     counter++;
		                 }
		                 MZ.clear();
		                 MySongs.clear();
		                 out.println(songs_details);      
		                 System.out.println("songs_details ----" + songs_details);
		             }
		             else
		             {	             	    	   
		         	   
		             	System.out.println(getServletContext().getRealPath("config/"+lang+"_mtv_edgysongs.txt")+ "bollywood file Could not found ----->");	

		             }
		            
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
			songs_details=null;
			
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

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
