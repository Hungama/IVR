import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Calendar;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class exit_log extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  long cal_duration = 0L;
  static DataSource ds;
   FileOutputStream outq = null;
  		    PrintStream q = null;
  		       FileOutputStream outd = null;
  public void init()
  {
    try
    {
      Context initCtx = new InitialContext();
      Context envCtx = (Context)initCtx.lookup("java:comp/env");

      ds = (DataSource)envCtx.lookup("jdbc/airtel_hungama");
    } catch (Exception e) {
      System.out.println("error is" + e.toString());
    }
 }

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    if (request.getProtocol().equals("HTTP/1.1"))
      response.setHeader("Cache-Control", "no-cache");
    response.setContentType("application/ecmascript");

    Connection conat = null;
    CallableStatement ccstmt = null;
    String starttime = request.getParameter("starttime");
    String endtime = request.getParameter("endtime");
    String ani = request.getParameter("ani");
    String contenttime = request.getParameter("contenttime");
    String circle = request.getParameter("circle");
    String operator = request.getParameter("operator");
    String dnis = request.getParameter("dnis");
    String realdnis = request.getParameter("realdnis");
    String sub_flag = request.getParameter("subflag");
    String service = request.getParameter("service");
    String contentlog = request.getParameter("contentlog");
contentlog = operator + "#" + circle + "#" + contentlog;

    if (contenttime == null)
      contenttime = "0";
    if (sub_flag == null) {
      sub_flag = "0";
    }
    Calendar today = Calendar.getInstance();

    String strlogfileNEW = formatN(new StringBuilder().append(today.get(1)).toString(), 4) +"-"+ formatN(new StringBuilder().append(today.get(2) + 1).toString(), 2) +"-"+formatN(new StringBuilder().append(today.get(5)).toString(), 2);
    String strlogfile = formatN(new StringBuilder().append(today.get(1)).toString(), 4) +
          formatN(new StringBuilder().append(today.get(2) + 1).toString(), 2) +
   formatN(new StringBuilder().append(today.get(5)).toString(), 2);
     String strlogfile_new = formatN(new StringBuilder().append(today.get(1)).toString(), 4) +
             formatN(new StringBuilder().append(today.get(2) + 1).toString(), 2) +
   formatN(new StringBuilder().append(today.get(5)).toString(), 2);

    String strlogfile1 = null;
     String strlogfile2 = null;
    try
    {
      long duration;

      if (starttime.equals("0")) {
        duration = 100L;
      } else {
        int diffseconds = 0; int diffminutes = 0; int diffhours = 0;

        int h1 = Integer.parseInt(starttime.substring(0, 2));
        int h2 = Integer.parseInt(endtime.substring(0, 2));
        int m1 = Integer.parseInt(starttime.substring(2, 4));
        int m2 = Integer.parseInt(endtime.substring(2, 4));
        int s1 = Integer.parseInt(starttime.substring(4, 6));
        int s2 = Integer.parseInt(endtime.substring(4, 6));

        if (h1 > h2) {
          long d2 = s2 + 60 * m2 + 3600 * h2;
          long d1 = 59 - s1 + (59 - m1) * 60 + (23 - h1) * 60 * 60;
          duration = d1 + d2;
        }
        else {
          if (s2 < s1) {
            s2 += 60;
            m2--;
            diffseconds = s2 - s1;
          } else {
            diffseconds = s2 - s1;
          }
          if (m2 < m1) {
            m2 += 60;
            h2--;
            diffminutes = m2 - m1;
          } else {
            diffminutes = m2 - m1;
          }
          diffhours = h2 - h1;
          duration = diffseconds + 60 * diffminutes +
            3600 * diffhours;
        }

      }
		if(duration==0)
					duration=1;

      this.cal_duration = duration;
      String temp1 = "";
      String temp2 = "";
      long Content_Time;

      if (Integer.parseInt(contenttime) > this.cal_duration)
      {
        Content_Time = this.cal_duration;
      }
      else
      {
        Content_Time = Integer.parseInt(contenttime);
      }

	 temp1 = "#" + operator + "_" + service + "_" + circle + "#" + sub_flag + "#" + strlogfile + "#" +
         starttime + "#" + this.cal_duration + "#" + ani + "#" + Content_Time + "#" +
         dnis + "#" + realdnis + "#";

       FileOutputStream outp = null;
       PrintStream p = null;
       FileOutputStream outc = null;


       /*File ServiceFolder = new File("/home/Hungama_call_logs/" + service + "/");
       if (!ServiceFolder.exists()) {
         ServiceFolder.mkdir();
         strlogfile1 = service + "_contentlog_" + strlogfile;
         strlogfile = service + "_calllog_" + strlogfile;
         outp = new FileOutputStream("/home/Hungama_call_logs/" + service + "/" + strlogfile + ".txt", true);
         if (!contenttime.equalsIgnoreCase("0"))
           outc = new FileOutputStream("/home/Hungama_call_logs/" + service + "/" + strlogfile1 + ".txt", true);
       }
       else
       {
         strlogfile1 = service + "_contentlog_" + strlogfile;
         strlogfile = service + "_calllog_" + strlogfile;
         outp = new FileOutputStream("/home/Hungama_call_logs/" + service + "/" + strlogfile + ".txt", true);
         if (!contenttime.equalsIgnoreCase("0")) {
           outc = new FileOutputStream("/home/Hungama_call_logs/" + service + "/" + strlogfile1 + ".txt", true);
         }*/



		String H1 =starttime.substring(0,2);
					String H2 =starttime.substring(2,4);
					String H3 =starttime.substring(4,6);
					String H4= H1+":"+H2+":"+H3;

		//System.out.println("h4 value "+H4);
 		temp2 = "#" + operator + "#" + service + "#" + circle + "#" + sub_flag + "#" + strlogfileNEW + "#" +
		         H4 + "#" + this.cal_duration + "#" + ani + "#" + Content_Time + "#" +
		         dnis + "#" + realdnis + "#";


			 File ServiceFolder1 = new File("/home/Hungama_call_logs/NEW/" + service + "/");
		       if (!ServiceFolder1.exists()) {
		         ServiceFolder1.mkdir();
		         strlogfile2 = service + "_contentlog_" + strlogfile_new;
		         strlogfile_new = service + "_calllog_" + strlogfile_new;

		         outq = new FileOutputStream("/home/Hungama_call_logs/NEW/" + service + "/" + strlogfile_new + ".txt", true);
		         if (!contenttime.equalsIgnoreCase("0"))
		           outd = new FileOutputStream("/home/Hungama_call_logs/NEW/" + service + "/" + strlogfile2 + ".txt", true);
		       }
		       else
		       {
		         strlogfile2 = service + "_contentlog_" + strlogfile_new;
		         strlogfile_new = service + "_calllog_" + strlogfile_new;
		         outq = new FileOutputStream("/home/Hungama_call_logs/NEW/" + service + "/" + strlogfile_new + ".txt", true);
		         if (!contenttime.equalsIgnoreCase("0")) {
		           outd = new FileOutputStream("/home/Hungama_call_logs/NEW/" + service + "/" + strlogfile2 + ".txt", true);
        			}
				}
      //}

      synchronized (this)
      {
		try
			{
				PrintWriter q1 = new PrintWriter(outq);
				q1.println(temp2);
				q1.flush();
				q1.close();
				temp2 = null;
				outq.flush();
				outq.close();
			}
		catch (Exception E) {
				String pageName = getServletConfig().getServletName();
				System.out.println("Error in outq : "+pageName+ " -- " +E.toString());
				pageName = null;
				E.printStackTrace();
			}
        try
			{
				if(outd!=null)
				{
					PrintWriter q2 = new PrintWriter(outd);
					q2.println(contentlog);
					q2.flush();
					q2.close();
					outd.flush();
					outd.close();
				}
				else
				{
					System.out.println("Not Listen any content");
				}
			}
			catch (Exception E) {
				String pageName = getServletConfig().getServletName();
				System.out.println("Error in outd : "+pageName+ " -- " +E.toString());
				pageName = null;
				E.printStackTrace();
         	}

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

  private String formatN(String str, int x)
  {
    String ret_str = "";
    int len = str.length();
    if (len >= x) {
      ret_str = str;
    } else {
      for (int i = 0; i < x - len; i++)
        ret_str = ret_str + "0";
      ret_str = ret_str + str;
    }
    return ret_str;
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

/* Location:           C:\Users\Vinay\Desktop\
 * Qualified Name:     exit_log
 * JD-Core Version:    0.6.0
 */