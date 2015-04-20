import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.csvreader.CsvWriter;


public class SendEmail {
	ArrayList<String> al=new ArrayList<String>();
	
	public SendEmail(ArrayList<String> al) {
		this.al=al;
	}
	
	
		public  void send() throws IOException {
			//String to="rajneesh.srivastava@hungama.com";
			String to="mukesh.malav@hungama.com";
			String from="mukesh.malav@hungama.com";
			//String[] cc={"gagandeep.singh@hungama.com","monika.patel@hungama.com","kunalk.arora@hungama.com","mukesh.malav@hungama.com"};

			String host="smtpout.hungama.com";
			Properties property=System.getProperties();
			property.setProperty("mail.smtp.host", host);
			
			Session session = Session.getDefaultInstance(property);
			
			try{
		         // Create a default MimeMessage object.
		         MimeMessage message = new MimeMessage(session);

		         // Set From: header field of the header.
		         message.setFrom(new InternetAddress(from));

		         // Set To: header field of the header.
		         message.addRecipient(Message.RecipientType.TO,
		                                  new InternetAddress(to));
		/*	for(int i=0;i<cc.length;i++)
		         {
		        	 message.addRecipient(Message.RecipientType.CC,
                             new InternetAddress(cc[i]));
		         } */
		         // Set Subject: header field
		         message.setSubject("Mts contest last day MIS");
			
		         
		         
		         Iterator<String> itrscore=al.iterator();
              
		         String text="Hi , \n\n Please find the attached csv file for the previous day. \n\n";
		         
		         text=text +"\n\n Regards \n Mukesh Malav";
		        
		         BodyPart messageBodyPart1=new MimeBodyPart();
		         messageBodyPart1.setText(text);
		         
		         MimeBodyPart messageBodyPart2=new MimeBodyPart();
		         
		        
		         
		         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		         Date date = new Date();
		         String todaydate= dateFormat.format(date);
           
		         String filename="MTS_MIS_"+todaydate+".csv";
		         
		         CsvWriter writer =new CsvWriter ("logs/"+filename);

			//CsvWriter writer =new CsvWriter (filename);		         

		         String[] values={};
		         
		         String [] fieldname = "ANI#Today's score#circle#last charge amount#Question's played Today#SOU#pulses".split("#");
		         
		         writer.writeRecord(fieldname);
		         
                 while(itrscore.hasNext())
                 {
                  
                	 String ANI=(String)itrscore.next();
   	          	  String score=(String)itrscore.next();
   	          	  String circle=(String)itrscore.next();
   	          	  String chrg_amount =(String)itrscore.next();
   	          	  String total_qusplay=(String)itrscore.next();
   	              String sou=(String)itrscore.next();
   	              
			double pulse=Math.ceil((Double.parseDouble(sou)/60));

   	              String text1=ANI +"#"+score+"#"+circle+"#"+chrg_amount+"#"+total_qusplay+"#" + sou+"#"+pulse;
   	              
   	              	values=text1.split("#");
   	              
   	              	writer.writeRecord(values);
                 }
                 
                 writer.close();
		      
                 DataSource source =new FileDataSource("logs/"+filename);
    	         messageBodyPart2.setDataHandler(new DataHandler(source));
    	         messageBodyPart2.setFileName("logs/"+filename);
    	         
    	         
    	         Multipart multipart=new MimeMultipart();
    	         multipart.addBodyPart(messageBodyPart1);
    	         multipart.addBodyPart(messageBodyPart2);
    	         
    	         message.setContent(multipart);
		         // Send message
		         Transport.send(message);
		         System.out.println("Sent message successfully....");
		      }catch (MessagingException mex) {
		         mex.printStackTrace();
		      }
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
}

