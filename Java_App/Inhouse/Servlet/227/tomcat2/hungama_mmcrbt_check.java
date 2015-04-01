import java.lang.*;
import java.text.*;
import java.util.*;
import java.io.*;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 *
 * @author Administrator
 */
public class hungama_mmcrbt_check extends HttpServlet {

	/**
	 *
	 */
	 static DataSource ds;
	 Connection conat = null;
 	 public static Statement stmt;

	 public void init()
	 {
	    try
	    {
	      Context initCtx = new InitialContext();
	      Context envCtx = (Context)initCtx.lookup("java:comp/env");

	      ds = (DataSource)envCtx.lookup("jdbc/mod");

	      conat = ds.getConnection();

	    } catch (Exception e) {
	      System.out.println("error is" + e.toString());
	    }
	 }

	private static final long serialVersionUID = 1L;

	static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);


	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
				    response.setHeader ("Cache-Control", "no-cache");}

		PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");
		String favSongs=null;
		String favSongs1=null;
		String sp_file=null;
		String fldr=null;
		String STR=null;
		String operator = request.getParameter("operator");
		String scode = request.getParameter("scode");



		String TOKEN = request.getParameter("TOKEN");
		if(TOKEN!=null && TOKEN.equalsIgnoreCase("FREE"))
			hashMap.clear();
		String key =operator;


	    if(hashMap.get(key)!=null)
		{
			out.println("key:"+key+"return value:"+hashMap.get(key));
			out.println(hashMap.get(key));
			return;
		}

		try {
				stmt = conat.createStatement();
			    File file_txt1 = new File(getServletContext().getRealPath("config/config/"+operator+"/songconfig/MM_MOD_CRBT.cfg"));
				if(file_txt1.exists())
				{
					//out.println(file_txt1);
					ArrayList data = new ArrayList();
					BufferedReader in = new BufferedReader(new FileReader(file_txt1));
					String temp = null;
					while ((temp = in.readLine()) != null)
					{
						data.add(temp.trim());
						temp = null;
						//Thread.sleep(10);
					}
                    ListIterator ite = data.listIterator();
					int counter = 0;
					while (ite.hasNext())
					{
						favSongs = (String)ite.next();
						String temp1[] = favSongs.split(":");
						key = temp1[0];
						hashMap.put(key,favSongs);
					}
					data.clear();
					in.close();
				}


				key = scode;
				//out.println("key is: "+key);
				if(hashMap.get(key)!=null)
				{
					favSongs1=hashMap.get(key);
					String temp1[] = favSongs1.split(":");
					favSongs1= temp1[1];
					//out.println("key is: "+favSongs1);

					STR = "select foldername from master_db.master_content where SongUniqueCode='"+favSongs1+"'";
					//out.println(stmt);

					ResultSet rs = stmt.executeQuery(STR);
					while(rs .next())
					{
						fldr= rs.getString("foldername");
					}
					favSongs1=fldr+"_"+favSongs1;

					favSongs1 = "song.value='" + favSongs1 + "'";
					out.println(favSongs1);
					stmt.close();
					rs.close();
					return;
				}

		} catch (Exception e) {
			out.println("error is" + e.toString());

		}finally{
			favSongs=null;
			favSongs1=null;
			key=null;
			scode=null;
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
