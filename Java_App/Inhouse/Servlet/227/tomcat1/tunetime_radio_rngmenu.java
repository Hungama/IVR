import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
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
public class tunetime_radio_rngmenu extends HttpServlet {

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
	static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);

	public void init() {
		try {
			Context initCtx, envCtx;
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");

			// Look up our data source
			ds = (DataSource) envCtx.lookup("jdbc/tunetalk_radio");
		} catch (Exception e) {
			System.out.println("error is" + e.toString());

		}

	}


	protected void processRequest(HttpServletRequest request,
			HttpServletResponse res) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
						    res.setHeader ("Cache-Control", "no-cache");}

		res.setContentType("application/ecmascript");
		res.setContentType("text/html;charset=UTF-8");
		PrintWriter out = res.getWriter();
		String SONGID = (String) request.getParameter("SONGID");
		String IN_TYPE = (String) request.getParameter("IN_TYPE");
		String CIRCLE = (String) request.getParameter("CIRCLE");
		String TOKEN = (String) request.getParameter("TOKEN");
		String OPERATOR = (String) request.getParameter("OPERATOR");
		if(OPERATOR==null)
			OPERATOR = "DIGM";
		System.out.println("CIRCLE-->"+CIRCLE);
		Connection conat1 = null;
		CallableStatement ccstmt1 = null;
		try {
			conat1 = ds.getConnection();
			if (conat1 != null)
			{
				if("DIGM".equalsIgnoreCase(OPERATOR))
					ccstmt1 = conat1.prepareCall("{call RADIO_CRBTRNG_TOTALREQS(?,?,?)}");
				else if("RELM".equalsIgnoreCase(OPERATOR) || "RELC".equalsIgnoreCase(OPERATOR))
					ccstmt1 = conat1.prepareCall("{call reliance_hungama.RADIO_CRBTRNG_TOTALREQS(?,?,?)}");
				else
					ccstmt1 = conat1.prepareCall("{call indicom_radio.RADIO_CRBTRNG_TOTALREQS(?,?,?)}");
				ccstmt1.setString(1, SONGID);
				ccstmt1.setString(2, IN_TYPE);
				ccstmt1.setString(3, CIRCLE);
				ccstmt1.execute();
				conat1.close();
				ccstmt1.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				conat1.close();
				ccstmt1.close();
			}catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
		if(TOKEN!=null && TOKEN.equalsIgnoreCase("FREE"))
			hashMap.clear();
		String key =OPERATOR+"_"+SONGID+"_"+IN_TYPE;

		if(hashMap.get(key)!=null)
		{
			System.out.println("Hesh key-->"+key);
			out.println(hashMap.get(key));
			return;
		}

		String PROCEDURE = "RADIO_RNGMENU";
		if("DIGM".equalsIgnoreCase(OPERATOR))
			PROCEDURE = "RADIO_RNGMENU";
		else if("RELM".equalsIgnoreCase(OPERATOR) || "RELC".equalsIgnoreCase(OPERATOR))
			PROCEDURE = "reliance_hungama.RADIO_RNGMENU";
		else
			PROCEDURE = "indicom_radio.RADIO_RNGMENU";
		String Inparameter_string = "2";
		String Outparameter_string = "1";
		int Inparameter = Integer.parseInt(Inparameter_string);
		int Outparameter = Integer.parseInt(Outparameter_string);
		String[] IN;
		IN = new String[Inparameter];
		String[] OUT;
		OUT = new String[Outparameter];
		String CALL = PROCEDURE+"(";
		for(int i=0;i<Inparameter;i++)
		{
			if(i==0)
				IN[i] = SONGID;
			else
				IN[i] = IN_TYPE;
			//System.out.println("IN["+i+"]:"+IN[i]);
			CALL = CALL+"?";
			if(i<Inparameter-1)
				CALL = CALL+",";
		}
		if(Outparameter!=0)
		{
			CALL = CALL+",?";
		}
		CALL = CALL+")";
		//System.out.println("CALL procedure:"+CALL);
		res.setContentType("text/html;charset=UTF-8");
		Connection conat = null;
		CallableStatement ccstmt = null;
		String value = null;
		try {

			conat = ds.getConnection();

			if (conat != null) {

				ccstmt = conat.prepareCall("{call "+CALL+"}");
				for(int i=0;i<Inparameter;i++)
				{
					int j=i+1;
					ccstmt.setString(j, IN[i]);
					//System.out.println("ccstmt.setString("+j+","+IN[i]+");");
				}

				if(Outparameter!=0)
				{
					Outparameter = Inparameter+1;
					ccstmt.registerOutParameter(Outparameter, java.sql.Types.VARCHAR);
					//System.out.println("ccstmt.setString("+Outparameter+", java.sql.Types.VARCHAR);");
					Outparameter = 1;
				}
				ccstmt.execute();
				if(Outparameter!=0)
				{
					value = ccstmt.getString(Inparameter+1);
					String temp[]=value.split("#");
					String out_string = "out_string.length=" + temp.length;
					int counter = 0;
					while (counter<temp.length) {
						out_string = out_string + ";" + "out_string" + "[" + counter
								+ "]" + "=" + "'" + temp[counter].trim() + "'";
						counter++;
					}
					hashMap.put(key,out_string);
					out.println(out_string);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			out.close();
			out=null;
			value=null;
			key=null;
			TOKEN=null;
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