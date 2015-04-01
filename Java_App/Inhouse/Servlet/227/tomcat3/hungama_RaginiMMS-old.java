import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

public class hungama_RaginiMMS extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private static final String INPARAM = null;
  static DataSource ds;
 // static Logger log = Logger.getLogger(loophungama_dbinteraction.class.getName());

  public void init()
  {
    try
    {
      InitialContext localInitialContext = new InitialContext();
      Context localContext = (Context)localInitialContext.lookup("java:comp/env");

      ds = (DataSource)localContext.lookup("jdbc/Hungama_RaginiMMS");

    }
    catch (Exception localException) {
      System.out.println("error is" + localException.toString());
    }
  }

  protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {

    if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1")) {
      paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
    }
    paramHttpServletResponse.setContentType("application/ecmascript");

    String str1 = paramHttpServletRequest.getParameter("PROCEDURE");
    String str2 = paramHttpServletRequest.getParameter("INTOKEN");
    String str3 = paramHttpServletRequest.getParameter("OUTTOKEN");
    int i = Integer.parseInt(str2);
    int j = Integer.parseInt(str3);


    String[] arrayOfString1 = new String[i];

    String[] arrayOfString2 = new String[j];
    String str4 = str1 + "(";
    for (int k = 0; k < i; k++)
    {
      arrayOfString1[k] = paramHttpServletRequest.getParameter("INPARAM[" + k + "]");


      str4 = str4 + "?";
      if (k < i - 1)
        str4 = str4 + ",";

    }
    if (j != 0)
    {
      str4 = str4 + ",?";
    }
    str4 = str4 + ")";


    paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
    Connection localConnection = null;
    CallableStatement localCallableStatement = null;
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    String str5 = null;
    Object localObject1 = null; Object localObject2 = null;
    try
    {
      localConnection = ds.getConnection();

      if (localConnection != null)
      {
        localCallableStatement = localConnection.prepareCall("{call " + str4 + "}");

        for (int m = 0; m < i; m++)
        {
          int n = m + 1;
          localCallableStatement.setString(n, arrayOfString1[m]);
        }

        if (j != 0)
        {
          j = i + 1;
          localCallableStatement.registerOutParameter(j, 12);

          j = 1;
        }
        localCallableStatement.execute();
        if (j != 0)
        {
          str5 = localCallableStatement.getString(i + 1);

          String[] arrayOfString3 = str5.split("#");
          String str6 = "out_string.length=" + arrayOfString3.length;
          int i1 = 0;
          while (i1 < arrayOfString3.length) {
            str6 = str6 + ";" + "out_string" + "[" + i1 + "]" + "=" + "'" + arrayOfString3[i1].trim() + "'";

            i1++;
          }
          localPrintWriter.println(str6);
        }
      }
    }
    catch (Exception localException) {
    }
    finally {
      localPrintWriter.close();
      localPrintWriter = null;
      str5 = null;
      localObject2 = null;
      if (localCallableStatement != null) {
        try {
          localCallableStatement.close();
        }
        catch (SQLException localSQLException5) {
          System.out.println("PROCEDURE Name is " + str1);
        }
        localCallableStatement = null;
      }
      if (localConnection != null) {
        try {
          localConnection.close();
        }
        catch (SQLException localSQLException1) {
        }
        localConnection = null;
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




