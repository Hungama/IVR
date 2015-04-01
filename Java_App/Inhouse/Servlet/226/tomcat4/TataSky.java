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

 public class TataSky extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   static DataSource ds;
   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);
	 String IN_TYPE,CIRCLE;

   public void init()
   {
     try {
       Context initCtx = new InitialContext();
       Context envCtx = (Context)initCtx.lookup("java:comp/env");

       ds = (DataSource)envCtx.lookup("jdbc/Hungama_Tatasky");
     } catch (Exception e) {
       System.out.println("error is" + e.toString());
     }
   }

   protected void processRequest(HttpServletRequest request, HttpServletResponse res)
     throws ServletException, IOException
   {
     if (request.getProtocol().equals("HTTP/1.1")) {
       res.setHeader("Cache-Control", "no-cache");
     }
     res.setContentType("application/ecmascript");
     res.setContentType("text/html;charset=UTF-8");
     PrintWriter out = res.getWriter();
     String ANI = request.getParameter("ANI");

     String realDNIS = request.getParameter("realDNIS");

     Connection conat1 = null;
     CallableStatement ccstmt1 = null;
		 try
		 {
		   conat1 = ds.getConnection();
		   if (conat1 != null)
		   {
			 ccstmt1 = conat1.prepareCall("{call TATASKY_PUTDETAILS(?,?)}");
			 ccstmt1.setString(1, ANI);
			 ccstmt1.setString(2, realDNIS);
			 ccstmt1.execute();
			 conat1.close();
			 ccstmt1.close();
		   }
		 }
		 catch (Exception e)
		 {
		   e.printStackTrace();
		   try
		   {
			 conat1.close();
			 ccstmt1.close();
		   }
		   catch (Exception e1)
		   {
			 e1.printStackTrace();
		   }
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




