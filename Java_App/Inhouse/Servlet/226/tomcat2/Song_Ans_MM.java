import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Song_Ans_MM extends HttpServlet {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */


    @SuppressWarnings({"unchecked","unchecked", "unchecked"})
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       // response.setContentType("text/html;charset=UTF-8");
    	String ConfigPath = request.getParameter("ConfigPath");
    	PrintWriter out = response.getWriter();
        response.setContentType("application/ecmascript");
        String temp = "";
       ArrayList Song = new ArrayList();
       ArrayList Answer = new ArrayList();
       ArrayList Clip = new ArrayList();

        try {				
             File file_txt = new File("/tomcat2/webapps/hungama/config/"+ConfigPath);
               if (file_txt.exists())
                {


                    BufferedReader in = new BufferedReader(new FileReader(file_txt));
                     while ((temp = in.readLine()) != null) {

                           String ques[]=temp.split("#");
                           Song.add(ques[0]);
                           Answer.add(ques[1]);
                           Clip.add(ques[2]);
                           }



                     String  SongArray ="SongArray.length=" +  Song.size();
                     String  AnswerArray="AnswerArray.length=" +  Answer.size();
                     String  ClipArray="ClipArray.length=" +  Clip.size();

                     for(int i=0;i<Answer.size();i++)
                    {
                    	 SongArray=SongArray + ";" + "SongArray" + "[" + i + "]" + "=" + "'" + Song.get(i) + "'";
                    	 AnswerArray=AnswerArray + ";" + "AnswerArray" + "[" + i + "]" + "=" + "'" + Answer.get(i) + "'";
                    	 ClipArray=ClipArray + ";" + "ClipArray" + "[" + i + "]" + "=" + "'" + Clip.get(i) + "'";

                    }
                      in.close();
                     out.println(SongArray);
                     out.println(AnswerArray);
                     out.println(ClipArray);
                         //    out.println(str[2]);

                       }
                else
                {
                	String  SongArray ="SongArray.length=0";
                    String  AnswerArray="AnswerArray.length=0";
                    String  ClipArray="ClipArray.length=0";
                    out.println(SongArray);
                    out.println(AnswerArray);
                    out.println(ClipArray);

                }



             }



        catch (Exception e) {
        	String  SongArray ="SongArray.length=0";
            String  AnswerArray="AnswerArray.length=0";
            String  ClipArray="ClipArray.length=0";
            out.println(SongArray);
            out.println(AnswerArray);
            out.println(ClipArray);
        }
       finally
       {
    	   Song.clear();
           Answer.clear();
           Clip.clear();

       }




    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
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







