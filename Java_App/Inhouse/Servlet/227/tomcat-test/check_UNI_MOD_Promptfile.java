import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
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
public class check_UNI_MOD_Promptfile extends HttpServlet {

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

	static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);


	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
				    response.setHeader ("Cache-Control", "no-cache");}

		response.setContentType("application/ecmascript");

		PrintWriter out = response.getWriter();
		String favSongs=null;
		String TOKEN=null;
		response.setContentType("text/html;charset=UTF-8");
		String lang = request.getParameter("lang");
		String pname = request.getParameter("pname");
		TOKEN = request.getParameter("TOKEN");
		if(TOKEN!=null && TOKEN.equalsIgnoreCase("FREE"))
			hashMap.clear();
		String key =pname;

        if(TOKEN == null)
        	TOKEN="T20";

	    if(hashMap.get(key)!=null)
		{
			out.println(hashMap.get(key));
			return;
		}

		int testCounter=0;
		try {
			if (pname!= null) {

				File file_txt1 = new File("/tomcat-test/webapps/hungama/promptFiles/uni_mod/spzone/"+lang+"/"+pname);
				//out.println("file path : "+file_txt1);
				if (file_txt1.exists()) {

					ArrayList data = new ArrayList();
					//BufferedReader in = new BufferedReader(new FileReader(file_txt1));
					String temp = pname;

						data.add(temp.trim());
						temp = null;
						testCounter++;

					Calendar today = Calendar.getInstance();

							String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
									+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
								   + formatN("" + today.get(Calendar.DATE), 2);

					ListIterator ite = data.listIterator();
					favSongs = "favSongs.length=" + data.size();
					int counter = 0;
					/*while (ite.hasNext()) {
						favSongs = favSongs + ";" + "favSongs" + "[" + counter
								+ "]" + "=" + "'" + ite.next() + "'";
						counter++;
					}*/

					data.clear();
					//in.close();
				} else {
						favSongs = "favSongs.length=0;";
				}

			}
			hashMap.put(key,favSongs);
			out.println(favSongs);

		} catch (Exception e) {
			out.println("error is" + e.toString());
		}finally{
			pname=null;
			lang=null;
			favSongs=null;
			key=null;
			TOKEN=null;

		}
	}


	private String formatN(String str, int x) {
			int len;
			String ret_str = "";
			len = str.length();
			if (len >= x)
				ret_str = str;
			else {
				for (int i = 0; i < x - len; i++)
					ret_str = ret_str + "0";
				ret_str = ret_str + str;
			}
			return ret_str;
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