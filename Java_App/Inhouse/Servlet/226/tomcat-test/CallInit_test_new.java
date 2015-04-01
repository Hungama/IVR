import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class CallInit_test_new extends HttpServlet
{
    /* Remote OCMP machine    */
    private String host;
    /*  Port on which CallInit interface is listening :    * 5000 for OCMP   */
    private int port;
    /*  Vxml Service id    */
    private String tdServiceID;
    String Ani=null;
    String Bni=null;

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
        //+ "<field name=\"ccxmlscript\" value=\"http://10.130.14.106:8088/hungama/obd_HUNGAMA/voicexml20_hungama.ccxml\"/>\n"

        + "</fields>\n"

        + "</ci-request>";


    /* javax.servlet.http.HttpServletResponse, This method creates a

     multipartform data of the xml data and uses http post method to send

     the data to the OCMP CallInit interface listening on port 5000.

    */

	    public void doPost(HttpServletRequest req, HttpServletResponse res) {
	       Ani=req.getParameter("ANI");
           Bni=req.getParameter("BNI");

    //this.host = "10.0.0.1"; // Need to Update with the user OCMP host IP - Address
	this.host = "192.168.4.191";
    //this.port = 5000;
    this.port = 5000;
    //this.tdServiceID = "test_Lab"; // Need to update with the configured application for the user
    this.tdServiceID = "OCMPTEST";

    PostMethod post = new PostMethod("http://" + host + ":" + port + "/router/callinit.do");

    try {

	    // Create a multipart form data of the input xml

	    Part[] parts = new Part[] { createCIRequest() };

	    post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));

	    int status = new HttpClient().executeMethod(post);

	    BufferedReader rd = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()));

	    String line;

	    while ((line = rd.readLine()) != null) {

	        System.out.println(line);

	    }

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

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
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

