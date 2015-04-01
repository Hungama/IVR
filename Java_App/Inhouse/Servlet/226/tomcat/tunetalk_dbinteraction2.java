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

 public class tunetalk_dbinteraction2 extends HttpServlet
 {
   private static final long serialVersionUID = 1L;
   static DataSource ds;
   String PROCEDURE ="test";
   public void init()
   {
   try
     {
      Context initCtx = new InitialContext();
       Context envCtx = (Context)initCtx.lookup("java:comp/env");

       ds = (DataSource)envCtx.lookup("jdbc/tunetalk_radio");
     } catch (Exception e) {
       System.out.println("error is" + e.toString());
    }
   }

   protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
			
			try
    		       {
				String[] temp;
				
				response.reset();
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();

				 PROCEDURE = request.getParameter("PROCEDURE");

		/*  66 */     String Inparameter_string = request.getParameter("INTOKEN");
		/*  67 */     String Outparameter_string = request.getParameter("OUTTOKEN");
		/*  68 */     int Inparameter = Integer.parseInt(Inparameter_string);
		/*  69 */     int Outparameter = Integer.parseInt(Outparameter_string);
		/*     */
		/*  74 */     String[] IN = new String[Inparameter];
		/*     */
		/*  76 */     String[] OUT = new String[Outparameter];
		/*  77 */     String CALL = PROCEDURE + "(";
		/*  78 */     for (int i = 0; i < Inparameter; i++)
		/*     */     {
		/*  80 */       IN[i] = request.getParameter("INPARAM[" + i + "]");
		/*     */
		/*  82 */       CALL = CALL + "?";
		/*  83 */       if (i < Inparameter - 1)
		/*  84 */         CALL = CALL + ",";
		/*     */     }
		/*  86 */     if (Outparameter != 0)
		/*     */     {
		/*  88 */       CALL = CALL + ",?";
		/*     */     }
		/*  90 */     CALL = CALL + ")";
		/*     */

		/*  93 */     Connection conat = null;
		/*  94 */     CallableStatement ccstmt = null;
		
		/*  96 */     String value = null;
		/*  97 */     String usr_status1 = null; String lang1 = null;
		/*     */     try
		/*     */     {
		/* 100 */       conat = ds.getConnection();
		/*     */
		/* 102 */       if (conat != null)
		/*     */       {
		/* 104 */         ccstmt = conat.prepareCall("{call " + CALL + "}");
		/* 105 */         for (int i = 0; i < Inparameter; i++)
		/*     */         {
		/* 107 */           int j = i + 1;
		/* 108 */           ccstmt.setString(j, IN[i]);
		/*     */         }
		/*     */
		/* 112 */         if (Outparameter != 0)
		/*     */         {
		/* 114 */           Outparameter = Inparameter + 1;
		/* 115 */           ccstmt.registerOutParameter(Outparameter, 12);
		/*     */
		/* 117 */           Outparameter = 1;
		/*     */         }
		/* 119 */         ccstmt.execute();
		/* 120 */         if (Outparameter != 0)
		/*     */         {
		/* 122 */           value = ccstmt.getString(Inparameter + 1);
		/* 123 */            temp = value.split("#");
		/* 124 */           String out_string = "out_string.length=" + temp.length;
		/* 125 */           int counter = 0;
		
		/* 126 */           while (counter < temp.length) {
		/* 127 */             out_string = out_string + ";" + "out_string" + "[" + counter +
		/* 128 */               "]" + "=" + "'" + temp[counter].trim() + "'";

		/* 129 */             counter++;
		
						
		/*     */           }
		/* 131 */          // out.println(out_string);


				
								out.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\r\n" +
								          "<vxml version=\"2.0\">" +
								"<form id=\"get_ccinfo\"><block>"+
						 "<var name=\"status\" expr=\"'"+temp[0].trim() +"'\" />"+
						 "<var name=\"lang\" expr=\"'"+temp[1].trim() +"'\" />"+
						  "<var name=\"balance\" expr=\"'"+temp[2].trim() +"'\" />"+
						   "<var name=\"mode\" expr=\"'"+temp[3].trim() +"'\"/>"+
						  "<return namelist=\"status lang balance mode\" />"+
						"</block></form>\r\n" +
						       "</vxml>");
				

		/*     */         }
		/*     */       }
		/*     */     }
		/*     */     catch (Exception e) {
		/* 136 */       e.printStackTrace();
		/*     */     } finally {
		/* 138 */       out.close();
		/* 139 */       out = null;
		/* 140 */       value = null;
		/* 141 */       lang1 = null;
		/* 142 */       if (ccstmt != null) {
		/*     */         try {
		/* 144 */           ccstmt.close();
		/*     */         }
		/*     */         catch (SQLException localSQLException) {
		/*     */         }
		/* 148 */         ccstmt = null;
		/*     */       }
		/* 150 */       if (conat != null) {
		/*     */         try {
		/* 152 */           conat.close();
		/*     */         }
		/*     */         catch (SQLException localSQLException1) {
		/*     */         }
		/* 156 */         conat = null;
		/*     */       }
		/*     */     }





			}
			 catch (Exception e) 
				{
				       PrintWriter op = response.getWriter();
					response.setContentType("text/html;charset=UTF-8");
					
				op.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\r\n" +
						          "<vxml version=\"2.0\">" +
						"<form id=\"get_ccinfo\"><block>"+
				   "<var name=\"sNumber\" expr=\"'out_string.length=4;out_string[0]=1;out_string[1]=03;out_string[2]=180;out_string[3]=IVR'\" />"+
				  
				    "<var name=\"dExpDate\" expr=\"'exception:"+e.getMessage() +"'\" />"+
				    "<return namelist=\"sNumber dExpDate\" />"+
				 "</block></form>\r\n" +
				         "</vxml>");

				
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