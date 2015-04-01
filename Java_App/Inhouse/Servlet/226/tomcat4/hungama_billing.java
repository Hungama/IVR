
import java.io.IOException;
import java.util.logging.*;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.datatype.Duration;
/**
 *
 * @author Administrator
 */
public class hungama_billing extends HttpServlet {

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
	public static Logger logger;
	public void init() {
		try {
			
            /*********** logger create ****************/	
			
			//int limit = 1000000; // 1 Mb			
		    //int numLogFiles = 3; 
			//boolean append = true;
			//FileHandler fh = new FileHandler("TestLog.log",append);
			FileHandler fh = new FileHandler(getServletContext().getRealPath("/log/hungama-billing-exception.log"));
			//fh.setFormatter(new XMLFormatter());
			fh.setFormatter(new SimpleFormatter());
			logger = Logger.getLogger("hungama-billing-exception");
			logger.addHandler(fh);
			
			/******************* end here *************/
			
			Context initCtx, envCtx;
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");

			// Look up our data source
			ds = (DataSource) envCtx.lookup("jdbc/hungama");
		} catch (Exception e) {
			System.out.println("error is" + e.toString());
			logger.warning(e.toString());

		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
        //http://localhost:8080/aajkahungama/hungama_billing?TOKEN=16&ANI=9312928418  // chk language
        //http://localhost:8080/aajkahungama/hungama_billing?TOKEN=17&ANI=9312928418&DNIS=570801&LANG=HIN  // update and insert language		
		//http://localhost:8080/aajkahungama/hungama_billing?TOKEN=1&ANI=111111&DNIS=1212 // check user subscribe or not
        //http://localhost:8080/aajkahungama/hungama_billing?TOKEN=2&ANI=111111&DNIS=1212&LANG=HINDI&MOS=IVR&AMT=1 // request for subscription
        //http://localhost:8080/aajkahungama/hungama_billing?TOKEN=3&ANI=111111&DNIS=1212 // request for unsubscription
        //http://localhost:8080/aajkahungama/hungama_billing?TOKEN=18&ANI=9312928418&BAL=12&DNIS=570801  // update user balence
        //http://localhost:8080/aajkahungama/hungama_billing?TOKEN=19   // update user balence
		
		
		if (request.getProtocol().equals ("HTTP/1.1")){
			response.setHeader ("Cache-Control", "no-cache");}
		
		response.setContentType("application/ecmascript");
		
		String value = null,usrmoviename=null;
		String usr_status=null,balance=null,tarrif=null,mode_sub=null,FreeSub_Flag=null,top_dmin=null,lang=null,lang_status=null;
		String wl_status=null,PlayListCount=null,top_id=null,operatorid=null,circleid=null,servicesid=null;
		int totaltop;
		
		String TOKEN = (String) request.getParameter("TOKEN");
		String ANI = (String) request.getParameter("ANI");		
		String DNIS = (String) request.getParameter("DNIS");
		lang = (String) request.getParameter("LANG");
		mode_sub=(String) request.getParameter("MOS");
		tarrif=(String) request.getParameter("AMT");
		balance=(String) request.getParameter("BAL");
		operatorid=(String) request.getParameter("operatorid");
		circleid=(String) request.getParameter("circleid");
		servicesid=(String) request.getParameter("servicesid");
		String planid = (String) request.getParameter("planid");
		String movie = (String) request.getParameter("movie");
		String moviename = (String) request.getParameter("moviename");
		String Flag = (String) request.getParameter("Flag");
		top_id=(String) request.getParameter("TOPUPAMTID");
		
		if(TOKEN == null || TOKEN=="")
			TOKEN="1";
		
		Connection conat = null;
		CallableStatement ccstmt = null;
		PrintWriter out = response.getWriter();		
		
		try {

			conat = ds.getConnection();

			if (conat != null) {

				// out.println("connection up");
				

				if(TOKEN.equals("1")) // check user new or old 
				{
					ccstmt = conat.prepareCall("{call JBOX_QUERY(?,?,?,?)}");						
					ccstmt.setString(1, TOKEN);				
					ccstmt.setString(2, ANI);		
					ccstmt.setString(3, DNIS);				
					ccstmt.registerOutParameter(4,java.sql.Types.VARCHAR);				
					ccstmt.execute();				
					value = ccstmt.getString(4);				
					String temp[]=value.split("#");
					
					if(temp[0].equals("-1"))  //NEW USER
					{
						usr_status = "sub_status.value='" + temp[0] + "'";
						out.println(usr_status);
					}
					else // Existing User
					{
						usr_status = "sub_status.value='" + temp[0] + "'";
						lang = "usrlang.value='" + temp[1].trim().toLowerCase() + "'";
						balance = "usrbalance.value='" + temp[2] + "'";	
						movie = "usrmovie.value='" + temp[3] + "'";

						out.println(usr_status);
						out.println(lang);
						out.println(balance);
						out.println(movie);
						
					}
				}
				else if(TOKEN.equals("2")) // subscribe user 
				{
					ccstmt = conat.prepareCall("{call JBOX_SUB(?,?,?,?,?,?,?)}");
					ccstmt.setString(1, ANI);		
					ccstmt.setString(2, lang);	
					ccstmt.setString(3, mode_sub);		
					ccstmt.setString(4, DNIS);									
					ccstmt.setString(5, tarrif);
					ccstmt.setString(6, servicesid);	
					ccstmt.setString(7, planid);
					ccstmt.execute();		
					System.out.println("user subscribe successfully ::");
					
				}
				else if(TOKEN.equals("3")) // un-subscribe user 
				{
					ccstmt = conat.prepareCall("{call JBOX_UNSUB(?,?)}");
					ccstmt.setString(1, ANI);						
					ccstmt.setString(2, DNIS);
					ccstmt.execute();		
					System.out.println("user un-subscribe successfully ::");
					
				}
				else if(TOKEN.equals("4")) // top up
				{
					ccstmt = conat.prepareCall("{call JBOX_TOP(?,?,?)}");
					ccstmt.setString(1, ANI);		
					ccstmt.setString(2, top_id);							
					ccstmt.setString(3, DNIS);	
					ccstmt.execute();		
					System.out.println("user subscribe successfully ::");
					
				}
				else if(TOKEN.equals("16")) // check lang  
				{
					ccstmt = conat.prepareCall("{call JBOX_CHK_LANG(?,?)}");						
					ccstmt.setString(1, ANI);					
					ccstmt.registerOutParameter(2,java.sql.Types.VARCHAR);				
					ccstmt.execute();					
					value = ccstmt.getString(2);
					System.out.println("languade value ::"+value);
					
					String temp[]=value.split("#");
					
					if(temp[0].equals("-1"))  //Language is not set by the user
					{
						lang_status = "lang_status.value='" + temp[0] + "'";
						out.println(lang_status);
					}
					else // language already set 
					{
						lang_status = "lang_status.value='" + temp[0] + "'";
						lang = "usrlang.value='" + temp[1].trim()+ "'";
						out.println(lang_status);
						out.println(lang);						
					}
				}
				else if(TOKEN.equals("17")) // update lang  
				{
					ccstmt = conat.prepareCall("{call JBOX_LANG_CHANGE(?,?,?)}");						
					ccstmt.setString(1, ANI);
					ccstmt.setString(2, DNIS);
					ccstmt.setString(3, lang);								
					ccstmt.execute();	
					System.out.println("Language update successfully ::");
				}
				else if(TOKEN.equals("18")) // update balance
				{
					ccstmt = conat.prepareCall("{call JBOX_UPDATETIME(?,?,?)}");						
					ccstmt.setString(1, ANI);					
					ccstmt.setString(2, balance);
					ccstmt.setString(3, DNIS);
					ccstmt.execute();	
					System.out.println("Balance update successfully ::");
				}
				else if(TOKEN.equals("19")) // activate topup
				{
					ccstmt = conat.prepareCall("{call JBOX_CHK_TOPUP(?)}");						
					ccstmt.registerOutParameter(1,java.sql.Types.VARCHAR);				
					ccstmt.execute();					
					value = ccstmt.getString(1);
					System.out.println("top up alue ::"+value);
					
					String temp[]=value.split("#");				 
					totaltop=Integer.parseInt(temp[0]);
					if(totaltop==3)
					{
						out.println("topup1.value='" + temp[1] + "'");
					    out.println("topup2.value='" + temp[2] + "'");
					    out.println("topup3.value='" + temp[3] + "'");
					    out.println("totaltopup.value='" + temp[0] + "'");
					}
					else if(totaltop==2)
					{
						out.println("topup1.value='" + temp[1] + "'");
				        out.println("topup2.value='" + temp[2] + "'");
				        out.println("totaltopup.value='" + temp[0] + "'");
					}
					else if(totaltop==1)
					{
						out.println("topup1.value='" + temp[1] + "'");				        
				        out.println("totaltopup.value='" + temp[0] + "'");
					}										
					
				}
				else if(TOKEN.equals("21")) // check user move & update movie 
				{
					String temp_split[] = moviename.split("#");
					ccstmt = conat.prepareCall("{call JBOX_UPDATE_MOVIE(?,?,?,?,?)}");						
					ccstmt.setString(1, ANI);								
					ccstmt.setString(2, Flag);		
					ccstmt.setString(3, temp_split[0]);
					ccstmt.setString(4, temp_split[1]);
					ccstmt.registerOutParameter(5,java.sql.Types.VARCHAR);				
					ccstmt.execute();				
					value = ccstmt.getString(5);				
					String temp1[]=value.split("#");																	
					usrmoviename = "usrmoviename.value='" + temp1[0] + "'";
					System.out.println("usrmoviename:"+usrmoviename);
					out.println(usrmoviename);
													
					
				}




			}
		} catch (Exception e) {

			//ExceptionLog obj=new ExceptionLog();
			String pageName=getServletConfig().getServletName();
			//obj.log(pageName+" "+e);
			pageName=null;
			e.printStackTrace();
			out.println("exception is" + e.toString());
			logger.warning(e.toString());
		} finally {
			out.close();
			ANI=null;
			out=null;
			value=null;
			wl_status=null;
			balance=null;
			lang=null;
			usr_status=null;
			balance=null;
			tarrif=null;
			mode_sub=null;
			FreeSub_Flag=null;
			top_dmin=null;
			lang=null;
			lang_status=null;			
			PlayListCount=null;
			top_id=null;
			totaltop=0;
			movie=null;
			moviename=null;
			if (ccstmt != null) {
				try {
					ccstmt.close();
				} catch (SQLException e) {
					;
				}
				ccstmt = null;
			}
			if (conat != null) {
				try {
					conat.close();
				} catch (SQLException e) {
					;
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
