import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReadConfigSongShuffle10 extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  static HashMap hashMap = new HashMap();

  protected void processRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    if (paramHttpServletRequest.getProtocol().equals("HTTP/1.1")) {
      paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
    }
    paramHttpServletResponse.setContentType("application/ecmascript");

    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();

    String str1 =  "/tomcat4/webapps/";
    String str2 = null;
    String str3 = null;
    paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
    String str4 = paramHttpServletRequest.getParameter("ConfigPath");
    str3 = paramHttpServletRequest.getParameter("TOKEN");
    if ((str3 != null) && (str3.equalsIgnoreCase("FREE")))
      hashMap.clear();
    String str5 = str4;

    if (str3 == null) {
      str3 = "T20";
    }
    if (hashMap.get(str5) != null)
    {
      localPrintWriter.println(hashMap.get(str5));
      return;
    }

    if (str3 == null) {
      str3 = "T20";
    }
    int i = 0;
    try {
      if (str4 != null) {
        File localFile = new File(str1 + "hungama/config/" + str4);
        if (localFile.exists()) {
          ArrayList localArrayList1 = new ArrayList();
          ArrayList localArrayList2 = new ArrayList();
          ArrayList localArrayList3 = new ArrayList();
          ArrayList localArrayList4 = new ArrayList();
          ArrayList localArrayList5 = new ArrayList();

          BufferedReader localBufferedReader = new BufferedReader(new FileReader(localFile));

          String str6 = null;
          while ((str6 = localBufferedReader.readLine()) != null)
          {
				  if (i < 10)
						localArrayList1.add(str6.trim());
				  else if(i>=10 && i < 20)
						localArrayList2.add(str6.trim());
				  else
						localArrayList3.add(str6.trim());
            str6 = null;
            i++;
          }

			Collections.shuffle(localArrayList1);
			Collections.shuffle(localArrayList2);
			Collections.shuffle(localArrayList3);
	

			localArrayList1.addAll(localArrayList2);
			localArrayList1.addAll(localArrayList3);


          ListIterator localListIterator = localArrayList1.listIterator();
          if (localArrayList1.size() < 100)
            str2 = "favSongs.length=" + localArrayList1.size();
          else
            str2 = "favSongs.length=100";
          int j = 0;
          int k = 0;
          while ((localListIterator.hasNext()) && (k == 0)) {
            if (j == 99)
              k = 1;
            str2 = str2 + ";" + "favSongs" + "[" + j + "]" + "=" + "'" + localListIterator.next() + "'";

            j++;
          }

          localArrayList1.clear();
          localBufferedReader.close();
        } else {
          str2 = "favSongs.length=0;favSongs[0]='0'";
        }
      }

      if (((!str4.startsWith("config/tatm/songconfig/")) && (!str4.startsWith("config/tatc/songconfig/"))) || ((!str4.endsWith("01.cfg")) && (!str4.endsWith("luv.cfg"))))
        hashMap.put(str5, str2);
      localPrintWriter.println(str2);
    }
    catch (Exception localException) {
      localPrintWriter.println("error is" + localException.toString());
    } finally {
      str4 = null;
      str2 = null;
      str5 = null;
      str3 = null;
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