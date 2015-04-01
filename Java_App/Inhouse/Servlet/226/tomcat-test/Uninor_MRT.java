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

 public class Uninor_MRT extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   static DataSource ds;
   static HashMap<String, String> hashMap = new HashMap(16, 0.5F);

   public void init()
   {
     try {
       Context initCtx = new InitialContext();
       Context envCtx = (Context)initCtx.lookup("java:comp/env");

       ds = (DataSource)envCtx.lookup("jdbc/uninor_myringtone");
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
     String SONGID = request.getParameter("SONGID");
     String IN_TYPE = request.getParameter("IN_TYPE");
     String IN_DNIS = request.getParameter("IN_DNIS");
     String CIRCLE = request.getParameter("CIRCLE");
      String OPER = request.getParameter("OPR");
     String TOKEN = request.getParameter("TOKEN");
     String OPERATOR = request.getParameter("OPERATOR");
     if (OPERATOR == null)
       OPERATOR = "UNIM";
     System.out.println("CIRCLE-->" + CIRCLE);
     Connection conat1 = null;
     CallableStatement ccstmt1 = null;
     try {
       conat1 = ds.getConnection();
       if (conat1 != null)
       {
         if ("UNIM".equalsIgnoreCase(OPERATOR))
           ccstmt1 = conat1.prepareCall("{call MRT_TOTALREQS(?,?,?,?)}");

         ccstmt1.setString(1, SONGID);
         ccstmt1.setString(2, IN_TYPE);
         ccstmt1.setString(3, CIRCLE);
          ccstmt1.setString(4, OPER);
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
       catch (Exception e1) {
         e1.printStackTrace();
       }
     }
     if ((TOKEN != null) && (TOKEN.equalsIgnoreCase("FREE")))
       hashMap.clear();
     String key = OPERATOR + "_" + SONGID + "_" + IN_TYPE;

     if (hashMap.get(key) != null)
     {
       System.out.println("Hesh key-->" + key);
       out.println((String)hashMap.get(key));
       return;
     }

     String PROCEDURE = "RADIO_RNGMENU";
     if ("UNIM".equalsIgnoreCase(OPERATOR))
       PROCEDURE = "RADIO_RNGMENU";
     else if (("RELM".equalsIgnoreCase(OPERATOR)) || ("RELC".equalsIgnoreCase(OPERATOR)))
       PROCEDURE = "reliance_hungama.RADIO_RNGMENU";
     else
       PROCEDURE = "indicom_radio.RADIO_RNGMENU";
     String Inparameter_string = "3";
     String Outparameter_string = "1";
     int Inparameter = Integer.parseInt(Inparameter_string);
     int Outparameter = Integer.parseInt(Outparameter_string);

     String[] IN = new String[Inparameter];

     String[] OUT = new String[Outparameter];
     String CALL = PROCEDURE + "(";
     for (int i = 0; i < Inparameter; i++)
     {
       if (i == 0)
         IN[i] = SONGID;
       else if (i == 1)
         IN[i] = IN_TYPE;
       else {
         IN[i] = IN_DNIS;
       }
       CALL = CALL + "?";
       if (i < Inparameter - 1)
         CALL = CALL + ",";
     }
     if (Outparameter != 0)
     {
       CALL = CALL + ",?";
     }
     CALL = CALL + ")";

     res.setContentType("text/html;charset=UTF-8");
     Connection conat = null;
     CallableStatement ccstmt = null;
     String value = null;
     try
     {
       conat = ds.getConnection();

       if (conat != null)
       {
         ccstmt = conat.prepareCall("{call " + CALL + "}");
         for (int i = 0; i < Inparameter; i++)
         {
           int j = i + 1;
           ccstmt.setString(j, IN[i]);
         }

         if (Outparameter != 0)
         {
           Outparameter = Inparameter + 1;
           ccstmt.registerOutParameter(Outparameter, 12);

           Outparameter = 1;
         }
         ccstmt.execute();
         if (Outparameter != 0)
         {
           value = ccstmt.getString(Inparameter + 1);
           String[] temp = value.split("#");
           String out_string = "out_string.length=" + temp.length;
           int counter = 0;
           while (counter < temp.length) {
             out_string = out_string + ";" + "out_string" + "[" + counter +
               "]" + "=" + "'" + temp[counter].trim() + "'";
             counter++;
           }
           hashMap.put(key, out_string);
           out.println(out_string);
         }
       }
     }
     catch (Exception e) {
       e.printStackTrace();
       out.close();
       out = null;
       value = null;
       key = null;
       TOKEN = null;
       if (ccstmt != null) {
         try {
           ccstmt.close();
         }
         catch (SQLException localSQLException) {
         }
         ccstmt = null;
       }
       if (conat != null) {
         try {
           conat.close();
         }
         catch (SQLException localSQLException1) {
         }
         conat = null;
       }
     } finally {
       out.close();
       out = null;
       value = null;
       key = null;
       TOKEN = null;
       if (ccstmt != null) {
         try {
           ccstmt.close();
         }
         catch (SQLException localSQLException2) {
         }
         ccstmt = null;
       }
       if (conat != null) {
         try {
           conat.close();
         }
         catch (SQLException localSQLException3) {
         }
         conat = null;
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




