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
public class time_Digi extends HttpServlet {

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


	//static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);


	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getProtocol().equals ("HTTP/1.1")){
				    response.setHeader ("Cache-Control", "no-cache");}

		response.setContentType("application/ecmascript");

		PrintWriter out = response.getWriter();
		String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");
		 try {

			long duration, d1, d2,Content_Time;
			if (starttime.equals("0")) {
				duration = 100;
			} else {
				int s1, s2, m1, m2, h1, h2, diffseconds = 0, diffminutes = 0, diffhours = 0;
				// long duration,d1,d2;

				h1 = Integer.parseInt(starttime.substring(0, 2));
				h2 = Integer.parseInt(endtime.substring(0, 2));
				m1 = Integer.parseInt(starttime.substring(2, 4));
				m2 = Integer.parseInt(endtime.substring(2, 4));
				s1 = Integer.parseInt(starttime.substring(4, 6));
				s2 = Integer.parseInt(endtime.substring(4, 6));
				if (h1 > h2) {
					d2 = s2 + 60 * m2 + 60 * 60 * h2;
					d1 = (59 - s1) + (59 - m1) * 60 + (23 - h1) * 60 * 60;
					duration = d1 + d2;
					// System.out.println("Duration in Seconds \t"+duration);
				} else {
					if (s2 < s1) {
						s2 += 60;
						--m2;
						diffseconds = s2 - s1;
					} else {
						diffseconds = s2 - s1;
					}
					if (m2 < m1) {
						m2 += 60;
						--h2;
						diffminutes = m2 - m1;
					} else {
						diffminutes = m2 - m1;
					}
					diffhours = h2 - h1;
					duration = diffseconds + (60 * diffminutes)
							+ (60 * 60 * diffhours);
					out.println("duration is" + duration);

					// System.out.println("Duration in Seconds \t"+duration);
				}
			}
			
			//cal_duration = duration;   //new changes done
			





		} catch (Exception e) {
			out.println("error is" + e.toString());
		}
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