import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
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
public class hungama_check extends HttpServlet {

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


	static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);


	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
				    response.setHeader ("Cache-Control", "no-cache");}

		PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");
		String favSongs=null;

		String ANI = request.getParameter("ANI");
		int length = ANI.length();
		if(length > 10)
		{
			int diff = length - 10;
			ANI = ANI.substring(diff,length);
		}
		String ANI4 = ANI.substring(0,4);
		String ANI5 = ANI.substring(0,5);
		String TOKEN = request.getParameter("TOKEN");
		if(TOKEN!=null && TOKEN.equalsIgnoreCase("FREE"))
			hashMap.clear();
		String key =ANI4;

	    if(hashMap.get(key)!=null)
		{
			System.out.println("key:"+key+"return value:"+hashMap.get(key));
			out.println(hashMap.get(key));
			return;
		}
		key =ANI5;

		if(hashMap.get(key)!=null)
		{
			System.out.println("key:"+key+"return value:"+hashMap.get(key));
			out.println(hashMap.get(key));
			return;
		}

		try {
			    File file_txt1 = new File(getServletContext().getRealPath("config/series.txt"));
				if (file_txt1.exists()) {
					ArrayList data = new ArrayList();
					BufferedReader in = new BufferedReader(new FileReader(file_txt1));
					String temp = null;
					while ((temp = in.readLine()) != null) {
						data.add(temp.trim().toLowerCase());
						temp = null;
						//Thread.sleep(10);
					}
                    ListIterator ite = data.listIterator();
					int counter = 0;
					while (ite.hasNext()) {
						favSongs = (String)ite.next();
						String temp1[] = favSongs.split("#");
						key = temp1[0];
						favSongs = "ciropr1.value='" + favSongs + "'";
						hashMap.put(key,favSongs);
					}
					data.clear();
					in.close();
			}
			key = ANI4;
			if(hashMap.get(key)!=null)
			{
				System.out.println("key:"+key+"return fetch:"+hashMap.get(key));
				out.println(hashMap.get(key));
				return;
			}
			key =ANI5;

			if(hashMap.get(key)!=null)
			{
				System.out.println("key:"+key+"return fetch:"+hashMap.get(key));
				out.println(hashMap.get(key));
				return;
			}

			if(hashMap.get(key)==null)
			{
				System.out.println("key:"+key+"return value:"+hashMap.get(key));
				out.println("ciropr1.value='"+ANI5+"#other#oth#mobile'");
				return;
			}
			/*Calendar today = Calendar.getInstance();

			String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
					+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
					+ formatN("" + today.get(Calendar.DATE), 2);
			FileOutputStream outp = null; // declare a file output object
			PrintStream p = null;
			File ServiceFolder = new File("/home/Hungama_call_logs/missing_series/");
			if (!ServiceFolder.exists())
			{
				ServiceFolder.mkdir();
				outp = new FileOutputStream("/home/Hungama_call_logs/missing_series/missing_series_"+strlogfile+".txt", true);
			}
			else
			{
				outp = new FileOutputStream("/home/Hungama_call_logs/missing_series/missing_series_"+strlogfile+".txt", true);
			}
			synchronized(this)
			{
				try
				{
					PrintWriter p1=new PrintWriter(outp);
					p1.println(ANI);
					p1.flush();
					p1.close();
					//temp1=null;
					outp.flush();
					outp.close();
				}catch(Exception E)
				{
					System.out.println("outp is not opened");
				}
			}*/


		} catch (Exception e) {
			out.println("error is" + e.toString());
		}finally{
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
