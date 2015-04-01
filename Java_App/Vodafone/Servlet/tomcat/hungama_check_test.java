 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.ListIterator;
 import javax.servlet.ServletContext;
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

 public class hungama_check_test extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
   static Logger log = Logger.getLogger(hungama_check_test.class.getName());
   
   
   
   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
   {
	  
	   String prefix = getServletContext().getRealPath("/");
		 String file = getInitParameter("log4j-init-file");
		 if (file != null) {
				PropertyConfigurator.configure(prefix + file);
				System.out.println("Log4J Logging started: " + prefix + file);
		 }
		 else {
				System.out.println("Log4J Is not configured for your Application: " + prefix + file);
			  }
		 
     if (request.getProtocol().equals("HTTP/1.1")) {
       response.setHeader("Cache-Control", "no-cache");
     }
     PrintWriter out = response.getWriter();
     response.setContentType("text/html;charset=UTF-8");
     String favSongs = null;

     String ANI = request.getParameter("ANI");

     String ANI4 = ANI.substring(0, 4);
     String ANI5 = ANI.substring(0, 5);
     String TOKEN = request.getParameter("TOKEN");
     if ((TOKEN != null) && (TOKEN.equalsIgnoreCase("FREE")))
       hashMap.clear();
     String key = ANI4;

     if (hashMap.get(key) != null)
     {
    	 log.info("key:" + key + "return value:" + (String)hashMap.get(key));
     //  System.out.println("key:" + key + "return value:" + (String)hashMap.get(key));
       out.println((String)hashMap.get(key));
       return;
     }
     key = ANI5;

     if (hashMap.get(key) != null)
     {
       log.info("key:" + key + "return value:" + (String)hashMap.get(key));
    	// System.out.println("key:" + key + "return value:" + (String)hashMap.get(key));
       out.println((String)hashMap.get(key));
       return;
     }
     try {
       try {
         File file_txt1 = new File(getServletContext().getRealPath("config/series.txt"));
         if (file_txt1.exists()) {
           ArrayList data = new ArrayList();
           BufferedReader in = new BufferedReader(new FileReader(file_txt1));
           String temp = null;
           while ((temp = in.readLine()) != null) {
             data.add(temp.trim().toLowerCase());
             temp = null;
           }

           ListIterator ite = data.listIterator();
           int counter = 0;
           while (ite.hasNext()) {
             favSongs = (String)(String)ite.next();
             String[] temp1 = favSongs.split("#");
             key = temp1[0];
             favSongs = "ciropr1.value='" + favSongs + "'";
             hashMap.put(key, favSongs);
           }
           data.clear();
           in.close();
         }
         key = ANI4;
         if (hashMap.get(key) != null)
         {
        	 log.info("key:" + key + "return fetch:" + (String)hashMap.get(key));
          // System.out.println("key:" + key + "return fetch:" + (String)hashMap.get(key));
           out.println((String)hashMap.get(key));

           favSongs = null;
           key = null;
           TOKEN = null;

           return;
         }
         key = ANI5;

         if (hashMap.get(key) != null)
         {
        	 log.info("key:" + key + "return fetch:" + (String)hashMap.get(key));
          // System.out.println("key:" + key + "return fetch:" + (String)hashMap.get(key));
           out.println((String)hashMap.get(key));

           favSongs = null;
           key = null;
           TOKEN = null;

           return;
         }
       }
       catch (Exception e) {
    	   log.error("error is" + e.toString());
         //out.println("error is" + e.toString());
       }
     } finally {
       favSongs = null;
       key = null;
       TOKEN = null;
     }
     favSongs = null;
     key = null;
     TOKEN = null;
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
 }




