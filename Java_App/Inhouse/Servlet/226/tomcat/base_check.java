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
public class base_check extends HttpServlet
{

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


	protected int processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
			{

				if (request.getProtocol().equals ("HTTP/1.1"))
				{
				    response.setHeader ("Cache-Control", "no-cache");
				}
				PrintWriter out = response.getWriter();
				response.setContentType("text/html;charset=UTF-8");
				String favSongs=null;
				String ANI = request.getParameter("ANI");
				/* added by Rajesh Jakhar at 18 may */
				if(ANI.startsWith("91"))
				{
					ANI = ANI.substring(2,ANI.length());
        		}
				if(ANI.startsWith("0"))
				{
					ANI = ANI.substring(1,ANI.length());
        		}
				int flag=0;
				String ANI4 = ANI.substring(0,10);
				String TOKEN = request.getParameter("TOKEN");
				if(TOKEN!=null && TOKEN.equalsIgnoreCase("FREE"))
					hashMap.clear();
					String key =ANI4;
			    if(hashMap.get(key)!=null)
				{
					System.out.println("key:"+key+"return value:"+hashMap.get(key));
					out.println(hashMap.get(key));
					flag=1;
				}
				else
				{
					flag=0;
				}

				try
				{
				    File file_txt1 = new File(getServletContext().getRealPath("series.txt"));
			    	//out.println(key+"File---->"+file_txt1);
					if (file_txt1.exists())
					{
						//out.println(key+"ANI---->"+ANI);
						ArrayList data = new ArrayList();
						BufferedReader in = new BufferedReader(new FileReader(file_txt1));
						String temp = null;
						while ((temp = in.readLine()) != null)
						{
							data.add(temp.trim().toLowerCase());
							temp = null;
							//Thread.sleep(10);
						}
                	    ListIterator ite = data.listIterator();
						int counter = 0;
						while (ite.hasNext())
						{
							favSongs = (String)ite.next();
							String temp1[] = favSongs.split("#");
							key = temp1[0];
							//out.println("Key values---->"+key);
							favSongs = "ciropr1.value='" + favSongs + "'";
							//out.println("value is "+favSongs);
							hashMap.put(key,favSongs);
						}
						data.clear();
						in.close();
				}
				key = ANI4;
				if(hashMap.get(key)!=null)
				{

					out.println("key:"+key+"return fetch:"+hashMap.get(key));
					out.println(hashMap.get(key));
					flag=1;
					out.println("values of flag is ---->"+flag);
				}
				else
				{
					flag=0;
					out.println("values of flag is ---->"+flag);
				}

			}
			catch (Exception e)
			{
				out.println("error is" + e.toString());
			}
			finally
			{
				favSongs=null;
				key=null;
				TOKEN=null;
			}
			return flag;

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
