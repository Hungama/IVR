/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Surtarang_QA extends HttpServlet {

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
    	String path = (String) request.getParameter("path");
    	PrintWriter out = response.getWriter();
        response.setContentType("application/ecmascript");
        String temp = "";
       ArrayList Question = new ArrayList();
       ArrayList Answer = new ArrayList();

        try {
             File file_txt = new File("/var/lib/apache-tomcat-6.0.32/webapps/hungama/config/54646/common/"+path+".txt");
               if (file_txt.exists())
                {


                    BufferedReader in = new BufferedReader(new FileReader(file_txt));
                     while ((temp = in.readLine()) != null) {

                           String ques[]=temp.split("#");
                           Question.add(ques[0]);
                           Answer.add(ques[1]);
                           }



                     String  QuestionArray ="QuestionArray.length=" +  Question.size();
                     String  AnswerArray="AnswerArray.length=" +  Question.size();

                     for(int i=0;i<Answer.size();i++)
                    {
                    	 QuestionArray=QuestionArray + ";" + "QuestionArray" + "[" + i + "]" + "=" + "'" + Question.get(i) + "'";
                    	 AnswerArray=AnswerArray + ";" + "AnswerArray" + "[" + i + "]" + "=" + "'" + Answer.get(i) + "'";

                    }
                      in.close();
                     out.println(QuestionArray);
                     out.println(AnswerArray);
                         //    out.println(str[2]);

                       }
                else
                {
                	String  QuestionArray ="QuestionArray.length=0";
                    String  AnswerArray="AnswerArray.length=0";
                    out.println(QuestionArray);
                    out.println(AnswerArray);

                }



             }



        catch (Exception e) {
        	String  QuestionArray ="QuestionArray.length=0";
            String  AnswerArray="AnswerArray.length=0";
            out.println(QuestionArray);
            out.println(AnswerArray);
        }
       finally
       {
    	   Question.clear();
           Answer.clear();

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
