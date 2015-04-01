 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import javax.naming.Context;
 import javax.naming.InitialContext;
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.sql.DataSource;

 public class celebradio_rngmenu extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   static DataSource ds;
   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
   String IN_TYPE;
   String SONGID;
   String CIRCLE;
   String pageName=null;
   public void init()
   {
		 try
		 	{
				 InitialContext localInitialContext = new InitialContext();
				 Context localContext = (Context)localInitialContext.lookup("java:comp/env");
				 ds = (DataSource)localContext.lookup("jdbc/newseleb_hungama");
		 	}
		 catch (Exception localException)
		 	{
					pageName = getServletConfig().getServletName();
					System.out.println("Error : "+pageName+ " -- " +localException.toString());
					pageName = null;
		 	}
   }

   protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
     throws ServletException, IOException
   {
     	if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1"))
     		{
     			  paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
     		}
			 paramHttpServletResponse.setContentType("application/ecmascript");
			 paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
			 PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
			 String str1 = paramHttpServletRequest.getParameter("ANI");
			 String str2 = paramHttpServletRequest.getParameter("in_connectionid");
			 String str3 = paramHttpServletRequest.getParameter("in_sessionid_call");

			 String str4 = paramHttpServletRequest.getParameter("TOKEN");
			 String str5 = paramHttpServletRequest.getParameter("OPERATOR");

			  if ((str4 != null) && (str4.equalsIgnoreCase("FREE")))
       				hashMap.clear();

    		  if (str5 == null)
    		   		str5 = "TATM";

     		Connection localConnection1 = null;
     		CallableStatement localCallableStatement1 = null;
     		try
     			{
       				localConnection1 = ds.getConnection();
					if (localConnection1 != null)
					   {
								 localCallableStatement1 = localConnection1.prepareCall("{call CELEB_CHAT(?,?,?)}");

								 localCallableStatement1.setString(1, str1);
								 localCallableStatement1.setString(2, str2);
								 localCallableStatement1.setString(3, str3);

								 localCallableStatement1.execute();
								 localConnection1.close();
								 localCallableStatement1.close();
					   }
				}
    		catch (Exception localException1)
    			 {
						pageName = getServletConfig().getServletName();
						System.out.println("Error : "+pageName+ " -- " +localException1.toString());
						pageName = null;
    				    localException1.printStackTrace();
    				   try
    					   {
    						        localConnection1.close();
    						        localCallableStatement1.close();
    					   }
    				   catch (Exception localException2)
    				   	   {
									pageName = getServletConfig().getServletName();
									System.out.println("Error : "+pageName+ " -- " +localException2.toString());
									pageName = null;
    				   			    localException2.printStackTrace();
       					   }
     			  }

       }

   protected void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
     throws ServletException, IOException
   {
     processRequest(paramHttpServletRequest, paramHttpServletResponse);
   }

   protected void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
     throws ServletException, IOException
   {
     processRequest(paramHttpServletRequest, paramHttpServletResponse);
   }

   public String getServletInfo()
   {
     return "Short description";
   }
 }

