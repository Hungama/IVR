import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UssdMo
 */
public class UssdMo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UssdMo() {
        super();
        // TODO Auto-generated constructor stub
    }
       
    protected void processRequest(HttpServletRequest request,
    			HttpServletResponse response) throws ServletException, IOException {

    		if (request.getProtocol().equals ("HTTP/1.1")){
    				    response.setHeader ("Cache-Control", "no-cache");}

    		PrintWriter out = response.getWriter();
    		response.setContentType("text/html;charset=UTF-8");
    		
    		
    		String service_id = request.getParameter("service_id");
    		String keyword = request.getParameter("keyword");
    		String ANI = request.getParameter("ANI");
    		String subid = request.getParameter("subid");
    		String umb_consent=request.getParameter("umb_consent");
    		String language_id=request.getParameter("language_id");
    		String session_id=request.getParameter("session_id");
    		String transaction_id=request.getParameter("transaction_id");
    		String request_ind=request.getParameter("request_ind");
    		String extra_content=request.getParameter("extra_content");
    		String additional_info=request.getParameter("additional_info");
    	
    	//	System.out.println("Getting the values");
    		
    		
    		out.print(service_id+" "+keyword+"    "+ANI+"  "+subid+"  "+umb_consent+"  "+language_id+"  "+session_id+"   "+transaction_id+"   "+request_ind+"  "+extra_content+"  "+additional_info);
    		
    		String test=service_id+"#"+keyword+"#"+ANI+"#"+subid+"#"+umb_consent+"#"+language_id+"#"+session_id+"#"+transaction_id+"#"+request_ind+"#"+extra_content+"#"+additional_info;
    		
    	//	File f=new File("/USSD/Logs/");
    		
    	//	String filename="ussd.txt";
    	try{	
    		FileOutputStream fout=new FileOutputStream("/home/Hungama_call_logs/USSD/ussd.txt",true);
    		PrintWriter p1=new PrintWriter(fout);
			p1.println(test);
			System.out.println("test is:"+test);
			p1.flush();
			p1.close();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	           		
        }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

}
