import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class air_recording_mobisur extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private File uploadPath1;

  public void init(ServletConfig config)
    throws ServletException
  {
    super.init(config);
    String strDirectory = "Mobisur";

    this.uploadPath1 = new File("/var/lib/LIVERECORDING/" + strDirectory);
    System.out.println("uploadPath :" + this.uploadPath1);
    if (!this.uploadPath1.exists())
      this.uploadPath1.mkdir();
    else
      System.out.println("folder already exits");
  }

  public void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    PrintWriter out = response.getWriter();
    if (request.getProtocol().equals("HTTP/1.1"))
      response.setHeader("Cache-Control", "no-cache");
    response.setContentType("application/ecmascript");

    System.out.println("Rename Recorded file on Servlet");
    try
    {
      String ANI = request.getParameter("ANI");
      String recFilename = request.getParameter("recfilename");
      File recFile = new File(this.uploadPath1 + "/" + recFilename + ".wav");
      if (!recFile.exists())
      {
        System.out.println("File does not exits ");
        return;
      }
      String newFileName = this.uploadPath1 + "/" + recFilename + ".wav";
      File newFile = new File(newFileName);

      if (!newFile.exists()) {
        newFile.delete();
      }
      boolean rc = recFile.renameTo(newFile);

      out.flush();
      out.close();
    }
    catch (Exception lEx) {
      getServletContext().log(lEx, "error reading or saving file");
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