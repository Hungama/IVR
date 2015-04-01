import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
public class HMP_dbinteraction extends HttpServlet {
  private static final long serialVersionUID = 1L;
	static DataSource ds;
	String pageName=null;
	public void init() {
		try {
			Context initCtx, envCtx;
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/Hungama_54646");
		} catch (Exception e) {
			System.out.println("error is" + e.toString());
		}
	}

		protected void processRequest(HttpServletRequest request,
				HttpServletResponse res) throws ServletException, IOException {

			if (request.getProtocol().equals ("HTTP/1.1")){
							    res.setHeader ("Cache-Control", "no-cache");}

			res.setContentType("application/ecmascript");


			String PROCEDURE = (String) request.getParameter("PROCEDURE");
			String Inparameter_string = (String) request.getParameter("INTOKEN");
			String Outparameter_string = (String) request.getParameter("OUTTOKEN");
			int Inparameter = Integer.parseInt(Inparameter_string);
			int Outparameter = Integer.parseInt(Outparameter_string);
			System.out.println("PROCEDURE:"+PROCEDURE);
			System.out.println("Inparameter:"+Inparameter);
			System.out.println("Outparameter:"+Outparameter);
			String[] IN;
			IN = new String[Inparameter];
			String[] OUT;
			OUT = new String[Outparameter];
			String CALL = PROCEDURE+"(";
			for(int i=0;i<Inparameter;i++)
			{
				IN[i] = (String) request.getParameter("INPARAM["+i+"]");
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
			PrintWriter out = res.getWriter();
			String value = null;
			String usr_status1=null,lang1=null;
			try {

				conat = ds.getConnection();

				if (conat != null) {

					ccstmt = conat.prepareCall("{call "+CALL+"}");
					for(int i=0;i<Inparameter;i++)
					{
						int j=i+1;
						ccstmt.setString(j, IN[i]);
						System.out.println("ccstmt.setString("+j+","+IN[i]+");");
					}

					if(Outparameter!=0)
					{
						Outparameter = Inparameter+1;
						ccstmt.registerOutParameter(Outparameter, java.sql.Types.VARCHAR);

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
						out.println(out_string);
					}
				}
			} catch (Exception e) {

				e.printStackTrace();
			} finally {
				out.close();
				out=null;
				value=null;
				lang1=null;
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



		protected void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			processRequest(request, response);
		}


		protected void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {

			processRequest(request, response);
		}


		public String getServletInfo() {
			return "Short description";
		}

}