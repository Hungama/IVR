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

 public class CelebChatStatus extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   static DataSource ds;
   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
   String IN_TYPE;
   String SONGID;
   String CIRCLE;
   String pageName;

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
					System.out.println("Error in outd: "+pageName+ " -- " +localException.toString());
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
     			String str1 = "1";
     			String str2 = paramHttpServletRequest.getParameter("ANI");
     			String str3 = paramHttpServletRequest.getParameter("status");
     			String str4 = paramHttpServletRequest.getParameter("flag");

     			String str5 = paramHttpServletRequest.getParameter("TOKEN");
     			String str6 = paramHttpServletRequest.getParameter("OPERATOR");
     			if (str6 == null)
     				  str6 = "TATM";
     			System.out.println("CIRCLE-->" + this.CIRCLE);
     			Connection localConnection1 = null;
     			CallableStatement localCallableStatement1 = null;
     			try
     			{
     				  localConnection1 = ds.getConnection();
       				  if (localConnection1 != null)
       					{
       						  	localCallableStatement1 = localConnection1.prepareCall("{call CELEB_STATUS(?,?,?,?,?)}");
								localCallableStatement1.setString(1, str2);
								localCallableStatement1.setString(2, "1");
								localCallableStatement1.setString(3, str3);
								localCallableStatement1.setString(4, str4);
								localCallableStatement1.registerOutParameter(5, java.sql.Types.VARCHAR);

         						localCallableStatement1.execute();
         						localConnection1.close();
         						localCallableStatement1.close();
       					}
     			}
     			catch (Exception localException1)
     			{
						pageName = getServletConfig().getServletName();
						System.out.println("Error in outd: "+pageName+ " -- " +localException1.toString());
						pageName = null;
     				  try
     					  {
							 if (localConnection1 != null)
       							    localConnection1.close();
     					    localCallableStatement1.close();
     					  }
     				  catch (Exception localException2) {
     					    localException2.printStackTrace();
     					  }
     			}
     			if ((str5 != null) && (str5.equalsIgnoreCase("FREE")))
     				  hashMap.clear();

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




