import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;


/**
 *
 * @author Administrator
 */
public class content_score_New extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static DataSource ds;

	public void init() {
			try {
				Context initCtx, envCtx;
				initCtx = new InitialContext();
				envCtx = (Context) initCtx.lookup("java:comp/env");

				// Look up our data source

				ds = (DataSource) envCtx.lookup("jdbc/contentfromdb");
			} catch (Exception e) {
				System.out.println("error is" + e.toString());

			}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getProtocol().equals ("HTTP/1.1")){
		response.setHeader ("Cache-Control", "no-cache");
		}
		response.setContentType("application/ecmascript");

		//String contenttime = request.getParameter("contenttime");
		String contentfile = request.getParameter("contentfile");
		//System.out.println("contenttime Is " +  contenttime);
		System.out.println("contentfile Is " +  contentfile);



		String InputStr = contentfile;
		String Songid = "";
		String Cat = "";
		String Duration = "";
		String value = "";
		String Dbvalue = "";
		int TempScoreDur = 0;
		float TempScore = 0.0f;
		float updatedScore = 0.0f;
		response.setContentType("text/html;charset=UTF-8");
		Connection conat = null;
		CallableStatement ccstmt = null;
		CallableStatement ccstmt1 = null;

		try
		{
			//if(!contenttime.equalsIgnoreCase("0"))
			//{
				InputStr = InputStr.trim();
				String TempData[] = InputStr.split("#");
				Songid = TempData[0].trim();
				Cat = TempData[1].trim();
				Duration = TempData[2].trim();
				if(!Duration.equalsIgnoreCase("0"))
				{
					System.out.println("songid Is " +  Songid );
					System.out.println("Cat Is " +  Cat );
					System.out.println("Duration Is " +  Duration );
					if(Integer.parseInt(Duration) < 30)
						TempScoreDur = 0;
					else if(Integer.parseInt(Duration) <= 60)
						TempScoreDur = 1;
					else if(Integer.parseInt(Duration) > 60 && Integer.parseInt(Duration) <= 90)
						TempScoreDur = 2;
					else if(Integer.parseInt(Duration) > 90 && Integer.parseInt(Duration) <= 120)
						TempScoreDur = 3;
					else if(Integer.parseInt(Duration) > 120 && Integer.parseInt(Duration) <= 150)
						TempScoreDur = 4;
					else if(Integer.parseInt(Duration) > 150)
						TempScoreDur = 5;
					System.out.println("Temp score for this song code Is " +  TempScore);
					TempScore = (float) TempScoreDur/40;
					System.out.println("Temp score for this song code Is " +  TempScore);
					conat = ds.getConnection();
					if (conat != null)
					{
						System.out.println("Procedure Call for score");
						ccstmt = conat.prepareCall("{call getscore_tatm_mtv(?,?,?)}");  //Getting the score through this procedure from DB
						ccstmt.setString(1,Songid);
						ccstmt.setString(2,Cat);
						ccstmt.registerOutParameter(3, 12);					//Outparameter
						ccstmt.execute();
						value = ccstmt.getString(3);				// Getting output
						System.out.println("Procedure Call for value  IS " + value );
						String[] temp = value.split("#");
						Dbvalue = temp[0].trim();					// score from DB is
						float Dbscore = Float.parseFloat(Dbvalue);
						updatedScore= Dbscore + TempScore;
						System.out.println("Procedure Call for score updation in DB");
						ccstmt1 = conat.prepareCall("{call updatescore_tatm_mtv(?,?,?)}");
						ccstmt1.setString(1,Songid);
						ccstmt1.setString(2,Cat);
						ccstmt1.setFloat(3,updatedScore);
						ccstmt1.execute();
						System.out.println("Score updated in DB");
					}
				}
				else
				{
					System.out.println("Input String is of Zero duration");
				}
		}
		catch (Exception e)
		{
			String pageName=getServletConfig().getServletName();
			pageName=null;
			e.printStackTrace();
		}
		finally
		{
			value = null;
			if (ccstmt != null)
			{
				try{ccstmt.close();} catch (SQLException localSQLException) {}
				ccstmt = null;
			}
			if (ccstmt1 != null)
			{
				try {ccstmt1.close();} catch (SQLException localSQLException) {}
				ccstmt1 = null;
			}
			if (conat != null)
			{
				try {conat.close();} catch (SQLException localSQLException1) {}
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
