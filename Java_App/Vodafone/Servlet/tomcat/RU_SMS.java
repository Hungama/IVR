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
public class RU_SMS extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   static DataSource ds;
   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
   String IN_TYPE,SONGID,CIRCLE;
 public void init()
   {
		 try {
		   Context initCtx = new InitialContext();
		   Context envCtx = (Context)initCtx.lookup("java:comp/env");
		   ds = (DataSource)envCtx.lookup("jdbc/vodafone_hungama");
		 } catch (Exception e)
		{
	String pageName = getServletConfig().getServletName();
	System.out.println("Error : "+pageName+ " -- " +e.toString());
	pageName = null;
	System.out.println("error is" + e.toString());
	}
   }
  protected void processRequest(HttpServletRequest request, HttpServletResponse res)throws ServletException, IOException
   {
       if (request.getProtocol().equals("HTTP/1.1"))
		{
		     res.setHeader("Cache-Control", "no-cache");
		}
     			res.setContentType("application/ecmascript");
     			res.setContentType("text/html;charset=UTF-8");
     			PrintWriter out = res.getWriter();
			String ANI = request.getParameter("ANI");

     			Connection conat1 = null;
				CallableStatement ccstmt1 = null;
			 try
				{
				conat1 = ds.getConnection();
					if (conat1 != null)
					{
						ccstmt1 = conat1.prepareCall("{call vodafone_radio.radio_miss_sms(?)}");
						ccstmt1.setString(1, ANI);
						ccstmt1.execute();
						conat1.close();
						ccstmt1.close();
					}
				}
			catch (Exception e)
				{
					String pageName = getServletConfig().getServletName();
					System.out.println("Error : "+pageName+ " -- " +e.toString());
					pageName = null;
					e.printStackTrace();

				}
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




