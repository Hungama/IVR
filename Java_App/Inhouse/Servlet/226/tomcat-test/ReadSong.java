import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


/**
 *
 * @author Administrator
 */
public class ReadSong extends HttpServlet {
    	static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);

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

	static DataSource ds;
        Statement  stmt =null;
        Connection conat = null;
	public void init() {
		try {
			Context initCtx, envCtx;
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");

			// Look up our data source
			ds = (DataSource) envCtx.lookup("jdbc/mod");
		} catch (Exception e) {
			System.out.println("error is" + e.toString());

		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, SQLException {
            PrintWriter out = response.getWriter();
           try
               {
		if (request.getProtocol().equals ("HTTP/1.1")){
						    response.setHeader ("Cache-Control", "no-cache");}

		response.setContentType("application/ecmascript");
                
                
                conat = ds.getConnection();
                 stmt = conat.createStatement();
                String strANI = (String) request.getParameter("ANI");

                
		String favSongs=null;
		String TOKEN=null;
		response.setContentType("text/html;charset=UTF-8");
		
		TOKEN = request.getParameter("TOKEN");
		if(TOKEN!=null && TOKEN.equalsIgnoreCase("FREE"))
			hashMap.clear();

                
               String strQuery = "select recom.foldername,recom.business_category,recom.recom_songcode,recom.rankorder \n" +
"from Song_recomendation_op recom,master_db.tbl_CatSequence cat\n" +
" where recom.ani='"+strANI+"' and recom.business_category=cat.business_category\n" +
"order by cat.rankid,recom.rankorder asc";
					System.out.println("query:"+strQuery);

					ResultSet rs = stmt.executeQuery(strQuery);
                                        ArrayList data = new ArrayList();
					
		            while(rs .next())
					{
                                            System.out.println("recom_songcode:"+rs .getString("recom_songcode"));
                                            data.add(rs .getString("foldername")+"_"+rs .getString("recom_songcode"));

                                        }
                            
                            ListIterator ite = data.listIterator();
					favSongs = "favSongs.length=" + data.size();
					int counter = 0;
					while (ite.hasNext()) {
						favSongs = favSongs + ";" + "favSongs" + "[" + counter
								+ "]" + "=" + "'" + ite.next() + "'";
						counter++;
					}
                                        System.out.println("favSongs:"+favSongs);
					data.clear();
                                        out.print(favSongs);

                
			
		} catch (Exception e) {

			            System.out.println("exception:"+e.getMessage());
		} finally {
			out.close();
			out=null;
			
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					   System.out.println("exception1:"+e.getMessage());
				}
				stmt = null;
			}
			if (conat != null) {
				try {
					conat.close();
				} catch (SQLException e) {
					 System.out.println("exception5:"+e.getMessage());
				}
				conat = null;
			}
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on
	// the + sign on the left to edit the code.">
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
                try {
                    processRequest(request, response);
                } catch (SQLException ex) {
                       System.out.println("exception3:"+ex.getMessage());
                }
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
                try {
                    processRequest(request, response);
                } catch (SQLException ex) {
                    
                       System.out.println("exception4:"+ex.getMessage());
                }
	}

	/**
	 * Returns a short description of the servlet.
	 */
	public String getServletInfo() {
		return "Short description";
	}
	// </editor-fold>
}