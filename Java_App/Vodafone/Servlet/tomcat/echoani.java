import java.io.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class echoani extends HttpServlet
{
public void doPost(HttpServletRequest request, HttpServletResponse response)
{
    try
    {
        System.out.println("Echo ANI Demo");
        response.setContentType("text/event-stream");

        String ani = request.getParameter("ani");

        PrintWriter pw = response.getWriter();
        //int i=0;
        //while(true)
       // {

            //i++;
            pw.write("event: echoani\n\n");  //take note of the 2 \n 's, also on the next line.
            pw.write("data: "+ ani + "\n\n");
            System.out.println("Data Sent!!!");
       //     if(i>10)
       //     break;
       // }
        pw.close();

    }catch(Exception e){
        e.printStackTrace();
    }
}

public void doGet(HttpServletRequest request,HttpServletResponse response)
{
    doPost(request,response);
}

}