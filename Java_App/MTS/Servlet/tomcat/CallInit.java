import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.util.Calendar;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
/*
* Callinit prepares the xml request that needs to be posted to the OCMP CallInit interface using
* http. The xml data contains the various parameters like:
* 1. Connection : The destination number to be dialed
* 2. The service-id which needs to be invoked upon accepting the call
* 3. Report : Report URL
* 4. Field: If user defined ccxml script needs to be loaded, it can be specified in the field tag
* <name="ccxmlscript" value="URI">
*/
public class CallInit extends HttpServlet
{
    /* Remote OCMP machine    */
    private String host;
    /*  Port on which CallInit interface is listening :    * 5000 for OCMP   */
    private int port;
    /*  Vxml Service id    */
    private String tdServiceID;
    String Ani=null;
    String Bni=null;
    String circle=null;
    String OCMP_BREIP=null;
    File flu=null;

	Calendar today = Calendar.getInstance();

		String strlogfile = "" + formatN("" + today.get(Calendar.YEAR), 4)
				+ formatN("" + (today.get(Calendar.MONTH) + 1), 2)
				+ formatN("" + today.get(Calendar.DATE), 2);


    private static final String CIR_NAME = "cireq.xml";

    private static final String KEY_SID = "@SID@";

    private static final String KEY_CONNECTION = "@CONNECTION@";

    private static final String KEY_REPORT = "@REPORT@";

    private static final String KEY_FIELD = "@KEY_FIELD@";

    private static final String KEY_LOCALURI = "@KE_LOCALURI@";

    private static final String CIR_TEMPLATE = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"

        + "<ci-request xmlns=\"http://www.pipebeach.com/2001/10/ci-request\"\n"

        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"

        + "xsi:schemaLocation=\"http://www.pipebeach.com/2001/10/ci-request cirequest.xsd\"\n"

        + "version=\"1.0\">\n"

        + "<service-id>"

        + KEY_SID

        + "</service-id>\n"

                                + "<connection connecttimeout=\"300s\">"

        + KEY_CONNECTION + "</connection>\n"

        + "<fields>\n"

        + "<field name=\"localuri\" value=\"" + KEY_LOCALURI + "\"/>\n" + KEY_FIELD
	  + "<field name=\"ccxmlscript\" value=\"http://10.130.14.106:8088/hungama/obd_HUNGAMA/voicexml20_hungama.ccxml\"/>\n"

        + "</fields>\n"

        + "</ci-request>";


    /* javax.servlet.http.HttpServletResponse, This method creates a

     multipartform data of the xml data and uses http post method to send

     the data to the OCMP CallInit interface listening on port 5000.

    */

                    public void doPost(HttpServletRequest req, HttpServletResponse res)  throws  IOException {

                       Ani=req.getParameter("ANI");
           Bni=req.getParameter("BNI");
           circle=req.getParameter("circle");
	    PrintWriter out = res.getWriter();
if (circle.equals("del") || circle.equals("DEL"))
{
                OCMP_BREIP="10.130.199.52";
}
else if (circle.equals("tnu") || circle.equals("TNU"))
{
                OCMP_BREIP="10.130.14.3";
}
else if (circle.equals("chn") || circle.equals("CHN"))
{
                OCMP_BREIP="10.130.14.4";
}
else if (circle.equals("kar") || circle.equals("KAR"))
{
                OCMP_BREIP="10.130.41.35";
}
else if (circle.equals("ban") || circle.equals("BAN"))
{
                OCMP_BREIP="10.130.41.36";
}
else if (circle.equals("ker") || circle.equals("KER"))
{
                OCMP_BREIP="10.130.25.21";
}
else if (circle.equals("wbl") || circle.equals("WBL"))
{
                OCMP_BREIP="10.130.83.101";
}
else if (circle.equals("kol") || circle.equals("KOL"))
{
                OCMP_BREIP="10.130.65.101";
}
else if (circle.equals("mah") || circle.equals("MAH"))
{
                OCMP_BREIP="10.130.129.2";
}
else if (circle.equals("mum") || circle.equals("MUM"))
{
                OCMP_BREIP="10.130.129.3";
}
else if (circle.equals("hry") || circle.equals("HRY"))
{
                OCMP_BREIP="10.130.232.16";
}
else if (circle.equals("hay") || circle.equals("HAY"))
{
                OCMP_BREIP="10.130.232.16";
}
else if (circle.equals("raj") || circle.equals("RAJ"))
{
                OCMP_BREIP="10.130.173.45";
}
else if (circle.equals("apd") || circle.equals("APD"))
{
                OCMP_BREIP="10.130.33.21";
}
else if (circle.equals("hpd") || circle.equals("HPD"))
{
                OCMP_BREIP="10.130.33.22";
}
else if (circle.equals("bih") || circle.equals("BIH"))
{
                OCMP_BREIP="10.130.89.52";
}
else if (circle.equals("upe") || circle.equals("UPE"))
{
                OCMP_BREIP="10.130.199.53";
		  Bni="0"+Bni;
}
else if (circle.equals("upw") || circle.equals("UPW"))
{
                OCMP_BREIP="10.130.199.52";
		  Bni="0"+Bni;
}
else if (circle.equals("guj") || circle.equals("GUJ"))
{
	//OCMP_BREIP="10.130.199.52";
               OCMP_BREIP="10.130.14.4";
		 // Bni="0"+Bni;
//OCMP_BREIP="10.130.14.3";
}


    //this.host = "10.0.0.1"; // Need to Update with the user OCMP host IP - Address
                //this.host = "10.130.199.52";
    //this.port = 5000;
    this.port = 5000;
    //this.tdServiceID = "test_Lab"; // Need to update with the configured application for the user
    this.tdServiceID = "obd_HUNGAMA";
     //this.tdServiceID="Mivasout";
    PostMethod post = new PostMethod("http://" + OCMP_BREIP + ":" + port + "/router/callinit.do");

    try {

                    // Create a multipart form data of the input xml

                    Part[] parts = new Part[] { createCIRequest() };

                    post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));

                    int status = new HttpClient().executeMethod(post);

                    BufferedReader rd = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()));

//                   while ((line = rd.readLine()) != null) {
//                        System.out.println(line);
//                    }

           String line;
	    FileOutputStream flu = null; // declare a file output object

	  //  flu = new FileOutputStream("/home/Hungama_call_logs/OBDVOICE/LOGS/" + strlogfile + ".txt",true);
	  //  PrintWriter pw=new PrintWriter(flu);
	  //  synchronized(this)
	  //  {
	  //  	try
	//	{
	//	    pw.println(Bni);
	//	    pw.flush();
	//	    pw.close();
	//	    line=null;
	//	    flu.flush();
	//	    flu.close();
	//	}
	//	catch(Exception E)
	//	{
	//		out.println("flu is not opened");
	//	}
	//}	


    } catch (FileNotFoundException e) {

        e.printStackTrace();

    } catch (HttpException e) {

        e.printStackTrace();

    } catch (IOException e) {

        e.printStackTrace();

    } finally {

        post.releaseConnection();

        }

    }
private String formatN(String str, int x) {
		int len;
		String ret_str = "";
		len = str.length();
		if (len >= x)
			ret_str = str;
		else {
			for (int i = 0; i < x - len; i++)
				ret_str = ret_str + "0";
			ret_str = ret_str + str;
		}
		return ret_str;
}

    public void doGet(HttpServletRequest req, HttpServletResponse res)  throws  IOException  {
        doPost(req, res);
    }

    /*  Populates the necessary fields of the input xml which needs to be posted to the OCMP CallInit interface.   */
    private PartBase createCIRequest() throws FileNotFoundException {

        StringBuffer cir = new StringBuffer(CIR_TEMPLATE);

        replace(cir, KEY_SID, tdServiceID);

        //replace(cir, KEY_CONNECTION, "sip:16.181.73.36"); // Need to update with the user SIP phone IP adress
                                replace(cir, KEY_CONNECTION, "isup:"+Bni);
        replace(cir, KEY_LOCALURI, Ani); // Need to update with the number that user want to show to the caller number

        return new StringPart(CIR_NAME, cir.toString());
                }

    private void replace(StringBuffer buf, String key, String value) {
        replace(buf, key, value, true);
    }

    private void replace(StringBuffer buf, String key, String value,boolean trim) {
        int pos = buf.indexOf(key);
        buf.replace(pos, pos + key.length(), trim ? value.trim() : value);
    }
}

