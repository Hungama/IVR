import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class ar_ReadConfigSong extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	//AMIT


	//static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);
	static HashMap hashMap= new HashMap();


	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
				    response.setHeader ("Cache-Control", "no-cache");}

		response.setContentType("application/ecmascript");

		PrintWriter out = response.getWriter();
		//String tomcat_path="/usr/local/apache-tomcat-6.0.20/webapps/";
		//String tomcat_path="/tomcat/webapps/";
		String tomcat_path="/db/tomcat-test/webapps/";
		String favSongs=null;
		String TOKEN=null;
		response.setContentType("text/html;charset=UTF-8");
		String ConfigPath = request.getParameter("ConfigPath");
		TOKEN = request.getParameter("TOKEN");
		if(TOKEN!=null && TOKEN.equalsIgnoreCase("FREE"))
			hashMap.clear();
		String key =ConfigPath;

        if(TOKEN == null)
        	TOKEN="T20";

	    if(hashMap.get(key)!=null)
		{
			out.println(hashMap.get(key));
			return;
		}
		//String PreviewToPlay1 = "PreviewToPlay1.value='HINDI_TOP20'";
		 if(TOKEN == null)
        	TOKEN="T20";

		int testCounter=0;
		try {
			if (ConfigPath != null) {
				File file_txt1 = new File(tomcat_path+"hungama/config/" + ConfigPath );
				if (file_txt1.exists()) {
					ArrayList data = new ArrayList();
					ArrayList data_suff = new ArrayList();
					BufferedReader in = new BufferedReader(new FileReader(
							file_txt1));
					String temp = null;
					while ((temp = in.readLine()) != null) {

						//data.add(temp.trim().toLowerCase());
						if((ConfigPath.startsWith("config/tatm/songconfig/") || ConfigPath.startsWith("config/tatc/songconfig/")) && ConfigPath.endsWith("01.cfg"))
						{
							 if(testCounter<10)
								 data.add(temp.trim());
							 else
								 data_suff.add(temp.trim());
						}
						else
							data.add(temp.trim());
						temp = null;
						testCounter++;
						//Thread.sleep(10);
					}
					if((ConfigPath.startsWith("config/tatm/songconfig/") || ConfigPath.startsWith("config/tatc/songconfig/")) && ConfigPath.endsWith("01.cfg"))
					{
						Collections.shuffle(data);
						data.addAll(data_suff);
					}
					if(TOKEN!=null && TOKEN.equalsIgnoreCase("SHUFFLE"))
					{
						Collections.shuffle(data);
					}

					ListIterator ite = data.listIterator();
					if(data.size()<100)
						favSongs = "favSongs.length=" + data.size();
					else
						favSongs = "favSongs.length=100";
					int counter = 0;
					int flag = 0;
					while (ite.hasNext() && flag==0) {
						if(counter==99)
							flag=1;
						favSongs = favSongs + ";" + "favSongs" + "[" + counter
								+ "]" + "=" + "'" + ite.next() + "'";
						counter++;

					}

					data.clear();
					in.close();
				} else {
					favSongs = "favSongs.length=0;favSongs[0]='0'";
				}

			}
			if(!((ConfigPath.startsWith("config/tatm/songconfig/") || ConfigPath.startsWith("config/tatc/songconfig/")) && (ConfigPath.endsWith("01.cfg") || ConfigPath.endsWith("luv.cfg"))))
				hashMap.put(key,favSongs);
			out.println(favSongs);

		} catch (Exception e) {
			out.println("error is" + e.toString());
		}finally{
			ConfigPath=null;
			favSongs=null;
			key=null;
			TOKEN=null;

		}
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 */
	public String getServletInfo() {
		return "Short description";
	}
	// </editor-fold>
}