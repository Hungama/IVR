import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
//import org.jsmpp.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;





//import net.freeutils.charset.*;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsmpp.bean.*;
import org.jsmpp.session.*;
import org.jsmpp.extra.*;
import org.jsmpp.*;
import org.jsmpp.bean.OptionalParameter.Tag;

import java.util.ArrayList;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.codec.binary.Base64;

/*************************************************************************************
 * \ This are implementation of {@link Gateway}. This gateway will reconnect for
 * a specified interval if the session are closed.
 *
 *************************************************************************************/
public class AutoReconnectGateway_Uninor implements Gateway
{
	private static final Logger logger = LoggerFactory.getLogger(AutoReconnectGateway_Uninor.class);
	private SMPPSession session = null;
	private final String remoteIpAddress;
	private final int remotePort;
	private final BindParameter bindParam;
	private final long reconnectInterval = 5000L; // 5 seconds
	public static String ip=null,dsn=null,username=null,pwd=null,msgqueue=null;
	public static ArrayList<String> S_Type = new ArrayList<String>();
	public static ArrayList<String> S_Addr = new ArrayList<String>();
	public static ArrayList<String> D_Addr = new ArrayList<String>();
	public static ArrayList<String> message = new ArrayList<String>();
	public static ArrayList<String> messagetype = new ArrayList<String>();

		//public static HashMap<String, String> ANI_TABLE_NAME = new HashMap<String, String>();
	//public static String[] UNIM_CIRCLES = new String[] {"BIH","GUJ","MAH","UPW","UPE"};

	public static String[] UNIM_CIRCLES = new String[] {"UND","APD","ASM","BIH","PUB","KAR","MAH","TNU","WBL","DEL","MPD","CHN","GUJ","HPD","HAY","JNK","KER","KOL","NES","ORI","RAJ","UPW","UPE","HAR","HP"};




    public static Connection conn=null;


	/************************************************* SLEEP ****************************************************************/
	public static void SLEEP(final int n) {
		try {
			Thread.sleep(n);
		} catch (final Exception e) {
			System.out.println("Exception while sleep : " + e);
		}
	}

	/*********************************************** DATE FORMAT FOR LOGGING DATE *****************************************/
	public static String DATE() {
		final Date todaysDate = new java.util.Date();
		final SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		final String formattedDate = formatter.format(todaysDate);
		return formattedDate;
	}

	public static String RANDOM()
	{

				final Date todaysDate = new java.util.Date();
				final SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyyMMddHHmmssSS");
				final String formattedDate = formatter.format(todaysDate);
				return formattedDate;

	}

	/************************************************* CREATING FILE IN APPEND MODE *******************************************/
	public static void FILE(final String Filename, final String Content) {
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(
					Filename, true));
			out.write(DATE()+"#"+Content+"\n");
			out.close();
			logger.info("Wrirting to File  : "+DATE()+"#"+Content);
		} catch (final IOException e) {
			System.out.println("File I/O Error :" + e);
		} catch (final Exception E) {
			System.out.println("Error :" + E);
		}
	}

	/************************************************* DELETING FILE *******************************************************/
	public static void DELETE(final String FL) {
		final boolean success = (new File(FL)).delete();
		if (!success)
			System.out.println("Deletion failed.." + FL);
	}


	/************************************ Returns number of tokens *************************************************/
	public static int COUNT_TOKEN(final String MSG) {
		try {
			final StringTokenizer st = new StringTokenizer(MSG, "#");
			int count = 0;

			while (st.hasMoreTokens()) {
				count++;
				st.nextToken();
			}
			return count;
		} catch (final Exception e) {
			System.out.println(e);
		}
		return 0;
	}

	/********************************************** DATE String for log_file Creation *******************************************************/
	public static String DATE_FORMAT() {
		final Date todaysDate = new java.util.Date();
		final SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
		final String formattedDate = formatter.format(todaysDate);
		return formattedDate;
	}


      public static void PUSH_PROCESS()
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/dbConfig");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);

		}
		catch(Exception e)
		{

			e.printStackTrace();
			//System.exit(0);
		}
	}

       public static Connection dbConn()
	{
		while(true)
		{
			try
			{
				if(!conn.isClosed())
				return conn;
			}
			catch(Exception e){}
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn+"?autoReconnect=true", username, pwd);
				System.out.println("Database Connection established!");
				return conn;
			}catch(Exception e)
			{
				e.printStackTrace();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block

					e1.printStackTrace();
				}
			}
		}
	}
	/**************************************************************************************************************
	 * \ Construct auto reconnect gateway with specified ip address, port and
	 * SMPP Bind parameters.
	 *
	 * @param remoteIpAddress
	 *            is the SMSC IP address.
	 * @param remotePort
	 *            is the SMSC port.
	 * @param bindParam
	 *            is the SMPP Bind parameters.
	 * @throws IOException
	 *************************************************************************************************************/
	public AutoReconnectGateway_Uninor(final String remoteIpAddress,
			final int remotePort, final BindParameter bindParam,
			final long timeout) throws IOException {
		this.remoteIpAddress = remoteIpAddress;
		this.remotePort = remotePort;
		this.bindParam = bindParam;
		//this.Timeout = timeout;
		session = newSession();
	}

	public String submitShortMessage(final String serviceType,
			final TypeOfNumber sourceAddrTon,
			final NumberingPlanIndicator sourceAddrNpi,
			final String sourceAddr, final TypeOfNumber destAddrTon,
			final NumberingPlanIndicator destAddrNpi,
			final String destinationAddr, final ESMClass esmClass,
			final byte protocolId, final byte priorityFlag,
			final String scheduleDeliveryTime, final String validityPeriod,
			final RegisteredDelivery registeredDelivery,
			final byte replaceIfPresentFlag, final DataCoding dataCoding,
			final byte smDefaultMsgId, final byte[] shortMessage,
			final OptionalParameter... optionalParameters) throws PDUException,
			ResponseTimeoutException, InvalidResponseException,
			NegativeResponseException, IOException {
		return getSession().submitShortMessage(serviceType, sourceAddrTon,
				sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi,
				destinationAddr, esmClass, protocolId, priorityFlag,
				scheduleDeliveryTime, validityPeriod, registeredDelivery,
				replaceIfPresentFlag, dataCoding, smDefaultMsgId, shortMessage,
				optionalParameters);
	}

	String toBinary(final byte[] bytes) {
		final StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for (int i = 0; i < Byte.SIZE * bytes.length; i++)
			sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
					: '1');
		return sb.toString();
	}

	/*************************************************************************************
	 * Create new {@link SMPPSession} complete with the
	 * {@link SessionStateListenerImpl}.
	 *
	 * @return the {@link SMPPSession}.
	 * @throws IOException
	 *             if the creation of new session failed.
	 *************************************************************************************/
	private SMPPSession newSession() throws IOException {
		final SMPPSession tmpSession = new SMPPSession(remoteIpAddress,
				remotePort, bindParam);
		tmpSession.setTransactionTimer(10000);//60000);
		tmpSession.addSessionStateListener(new SessionStateListenerImpl());

		return tmpSession;
	}

	/*************************************************************************************
	 * \ Get the session. If the session still null or not in bound state, then
	 * IO exception will be thrown.
	 *
	 * @return the valid session.
	 * @throws IOException
	 *             if there is no valid session or session creation is invalid.
	 *************************************************************************************/
	private SMPPSession getSession() throws IOException {
		if (session == null) {
			logger.info("Initiate session for the first time to "
					+ remoteIpAddress + ":" + remotePort);
			session = newSession();
		} else if (!session.getSessionState().isBound()) {
			throw new IOException("We have no valid session yet");
		}
		return session;
	}

	/***************************************************************************************
	 * \ Reconnect session after specified interval.
	 *
	 * @param timeInMillis
	 *            is the interval.
	 ****************************************************************************************/
	private void reconnectAfter(final long timeInMillis) {
		new Thread() {
			@Override
			public void run() {
				logger.info("Schedule reconnect after " + timeInMillis
						+ " millis");
				try {
					Thread.sleep(timeInMillis);
				} catch (final InterruptedException e) {
				}
				int attempt = 0;
				while (session == null
						|| session.getSessionState()
								.equals(SessionState.CLOSED)) {
					try {
						logger.info("Reconnecting attempt #" + (++attempt)
								+ "...");
						session = newSession();
					} catch (final IOException e) {
						logger.error("Failed opening connection and bind to "
								+ remoteIpAddress + ":" + remotePort, e);
						// wait for a second
						try {
							Thread.sleep(1000);
						} catch (final InterruptedException ee) {
						}
					}
				}
			}
		}.start();
	}

	/************************************************************************************************
	 * \ This class will receive the notification from {@link SMPPSession} for
	 * the state changes. It will schedule to re-initialize session.
	 *
	 *
	 ************************************************************************************************/
	private class SessionStateListenerImpl implements SessionStateListener {
		public void onStateChange(final SessionState newState,
				final SessionState oldState, final Object source) {
			if (newState.equals(SessionState.CLOSED)) {
				logger.info("Session closed");
				reconnectAfter(reconnectInterval);
			}
		}
	}

	/*********************************************** Void Main ************************************************/
	public static void main(final String[] args) throws IOException {
		//final Date Curr = new Date();
		//final int count = Curr.getHours() - 1;
		//final AtomicInteger counter = new AtomicInteger();
		String IP="",UserName="",Password="";
		int Port=0;
		try{IP=args[0];}catch(Exception e){IP="180.178.28.39";}
		try{Port=Integer.parseInt(args[1]);}catch(Exception e){Port=9081;}
		try{UserName=args[2];}catch(Exception e){UserName="hungama13";}
		try{Password=args[3];}catch(Exception e){Password="hungam13";}

              PUSH_PROCESS();  // Read DB config files  and make single connection
		/*if(IP.equals("0") || Port==0 || UserName.equals("0") || Password.equals("0") )
		{
			System.out.println("Missing Parameters in Command Line : IP - "+IP+" Port - "+Port+"  UserName - "+UserName+" Password - "+Password);
			System.exit(1);
		}
                */

		BasicConfigurator.configure();
                final Gateway gateway = new AutoReconnectGateway_Uninor(IP,Port, new BindParameter(BindType.BIND_TX, UserName, Password,"CMT", TypeOfNumber.ALPHANUMERIC,NumberingPlanIndicator.ISDN, null), 3322);

              // BIND_TRX for Transeciever
  int TPS=5;
		while (true) {

			try {Thread.sleep(1000);	} catch (Exception e) {		}

                        // if(S_Type.size()<=40)
                       //{
			try
			{


			for(int I=0;I<10;I++)
			{
                           if(S_Type.size()>=10)
                           break;
						  // System.out.println(" ******************************** Sending Message to Circle "+UNIM_CIRCLES[I]+"*****************************************");

					PUSH_APP();
			}


                         //   }


			for (int i = 0; i < S_Type.size(); i++) {
				String SType = S_Type.get(0);
				String DAddr = D_Addr.get(0);  // ANI
				String SAddr = S_Addr.get(0);  // Shortcode
				String messg = message.get(0);
				String MSGTYPE=messagetype.get(0);
                try {Thread.sleep(1000/TPS);} catch (Exception e) {	}
                // TPS Controlled
                //if(i==5)
               // break;

                				S_Type.remove(0);
								S_Addr.remove(0);
								D_Addr.remove(0);
								message.remove(0);
								messagetype.remove(0);
System.out.println(" ******************************** Sending Message to ANI "+DAddr+"*****************************************");
			try {


					if(MSGTYPE.equals("TXT"))
					{
							String messageId = gateway
									.submitShortMessage(SType,TypeOfNumber.NETWORK_SPECIFIC,
											NumberingPlanIndicator.PRIVATE, SAddr,
											TypeOfNumber.UNKNOWN,
											NumberingPlanIndicator.UNKNOWN, DAddr,
											new ESMClass(0), (byte) 0, (byte) 0,
											null, null, new RegisteredDelivery(
											SMSCDeliveryReceipt.DEFAULT),
											(byte) 0, DataCoding.newInstance(0),
											(byte) 0, messg.replaceAll("newline", "\n").getBytes());
							System.out.println("Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == "+messageId);

							FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+MSGTYPE+"#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+messageId);
					}
					else if(MSGTYPE.equalsIgnoreCase("silent"))
					 {
										 System.out.println("Slient Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == ");

										 String messageId = gateway.submitShortMessage(SType,TypeOfNumber.NETWORK_SPECIFIC,NumberingPlanIndicator.PRIVATE, SAddr,TypeOfNumber.UNKNOWN,NumberingPlanIndicator.UNKNOWN, DAddr, new ESMClass(0), (byte) 0, (byte) 0, null, null, new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte) 0, DataCoding.newInstance((short)0x00),(byte) 0, hexStringToByteArray(""));
										 System.out.println("Slient Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == "+messageId);
										 Thread.sleep(5000);


				 	}
				 	else if(MSGTYPE.equalsIgnoreCase("hindi"))
				 	{
				 		String MESSAGE_TEXT_8BIT=toHexHind(messg);
				 		OptionalParameter opr1[]= new OptionalParameter[1];

                         opr1[0] = new OptionalParameter.COctetString((short)0x0424,hexStringToByteArray(MESSAGE_TEXT_8BIT));
                        // messageId = gateway.submitShortMessage("CMT", TypeOfNumber.ALPHANUMERIC , NumberingPlanIndicator.UNKNOWN,SEND_MSISDN , TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "91"+RECIEVE_MSISDN, new ESMClass(0), (byte)0, (byte)0,null, null, new RegisteredDelivery(0), (byte)0, new GeneralDataCoding(8), (byte)0, "".getBytes(),opr1);

						
						String messageId = gateway.submitShortMessage("CMT", TypeOfNumber.NETWORK_SPECIFIC , NumberingPlanIndicator.PRIVATE,SAddr,TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN,DAddr, new ESMClass(0), (byte)0, (byte)0,null, null, new RegisteredDelivery(0), (byte)0, new GeneralDataCoding(8), (byte)0,"".getBytes(),opr1);
						System.out.println("Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == "+messageId+" <------>"+MESSAGE_TEXT_8BIT);
						FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+MSGTYPE+"#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+messageId);
					}
					else if(MSGTYPE.equals("RNG"))
					{
											String messageId = gateway
													.submitShortMessage(SType,TypeOfNumber.NETWORK_SPECIFIC,
															NumberingPlanIndicator.PRIVATE, SAddr,
															TypeOfNumber.UNKNOWN,
															NumberingPlanIndicator.UNKNOWN, DAddr,
															new ESMClass((short) 0x40), (byte) 0, (byte) 0,
															null, null, new RegisteredDelivery(
															SMSCDeliveryReceipt.DEFAULT),
															(byte) 0, DataCoding.newInstance((short) 0xf5),
															(byte) 0, hexStringToByteArray("06050415811581"+messg.replaceAll(" ","")));
											System.out.println("Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == "+messageId);

											FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+MSGTYPE+"#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+messageId);
					}
					else if(MSGTYPE.equals("SIM"))
					{
											String messageId = gateway
													.submitShortMessage(SType,TypeOfNumber.NETWORK_SPECIFIC,
															NumberingPlanIndicator.PRIVATE, SAddr,
															TypeOfNumber.UNKNOWN,
															NumberingPlanIndicator.UNKNOWN, DAddr,
															new ESMClass((short) 0x40), (byte) 127, (byte) 0,
															null, null,
 													new RegisteredDelivery(SMEOriginatedAcknowledgement.DEFAULT),
															(byte) 0, new GeneralDataCoding((short) 0xf6),
															(byte) 0, hexStringToByteArray(messg));
											System.out.println("Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == "+messageId);

											FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+MSGTYPE+"#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+messageId);
					}
					else if(MSGTYPE.equals("WAP"))
					{
										String url="www.google.com";
										byte hrefTagToken=(byte) 0;

										if (url.startsWith("http://"))
										{
											if (url.startsWith("www.", 7))
											{
												hrefTagToken = 0xD;
												url = url.substring(11);
											}
											else
											{
												hrefTagToken = 0xC;
												url = url.substring(7);
											}
										}
										else if (url.startsWith("https://"))
										{
											if (url.startsWith("www.", 8))
											{
												hrefTagToken = 0xF;
												url = url.substring(12);
											}
											else
											{
												hrefTagToken = 0xE;
												url = url.substring(8);
											}
										}

									String messageId = gateway.submitShortMessage(SType,TypeOfNumber.NETWORK_SPECIFIC,NumberingPlanIndicator.PRIVATE, SAddr,TypeOfNumber.UNKNOWN,NumberingPlanIndicator.UNKNOWN, DAddr,new ESMClass((short) 0x40), (byte) 0, (byte) 0,null, null, new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),(byte) 0, DataCoding.newInstance((short) 0xf5),(byte) 0, hexStringToByteArray("0605040B8423F0DC0601AE02056A0045C60C037777772E736d70707365727665722E6f7267000103736d7070736572766572000101"));
									System.out.println("Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == "+messageId);
									FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+MSGTYPE+"#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+messageId);
					}
					else if(MSGTYPE.equals("LAVA"))
					{
						//String message1="301:52:11:0:,00,Unlimited Fun!,:1,00,Love Guru,1042,2,,,,,5464634p201,:2,00,Cricket Alerts,1043,1,30,30,0,MMTSCRI,58558,:#$A103$";
						  String msg=stringToHex(messg);
						  int totalSize=254;
						  int splitsize=230;
						  int splitpos=0,index=0;
						 if (msg != null && msg.length() > splitsize)
							 {
								  int ftotalsegment=  countsegments(msg);
								   String array[]= new String [ftotalsegment];
										for(int z=0;z<=ftotalsegment-1;z++)
											 {
													 splitpos=index+splitsize;
													if(msg.length()-index<=splitsize)
																	   {
															 splitpos=msg.length();

																	   }

												   array[z]=msg.substring(index, splitpos);
																int y=z+1;
													String finalmsg="0B0504F1C00000000301030"+y+array[z];
													 index=splitpos;

														String messageId = gateway.submitShortMessage(SType,TypeOfNumber.NETWORK_SPECIFIC,NumberingPlanIndicator.PRIVATE, SAddr,TypeOfNumber.UNKNOWN,NumberingPlanIndicator.UNKNOWN, DAddr, new ESMClass((short) 0x40), (byte) 0, (byte) 0,
														null, null, new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),(byte) 0, DataCoding.newInstance((short) 0xf5),
																			(byte) 0, hexStringToByteArray(finalmsg));

											 System.out.println("Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == "+messageId);

											  FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+MSGTYPE+"#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+messageId);

							}

		  		 }

				else
						{
                               String finalmsg="060504F1C00000"+msg;
												String messageId = gateway.submitShortMessage(SType,TypeOfNumber.NETWORK_SPECIFIC,
																NumberingPlanIndicator.PRIVATE, SAddr,
																TypeOfNumber.UNKNOWN,
																NumberingPlanIndicator.UNKNOWN, DAddr,
																new ESMClass((short) 0x40), (byte) 0, (byte) 0,
															null, null, new RegisteredDelivery(
																SMSCDeliveryReceipt.DEFAULT),
																(byte) 0, DataCoding.newInstance((short) 0xf5),
																(byte) 0, hexStringToByteArray(finalmsg));
																System.out.println("Message sent "+SType+":"+DAddr+"---->"+SAddr+"("+messg+") == "+messageId);

								FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+MSGTYPE+"#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+messageId);
						}
					}

				}
				catch (final PDUException e) {
					// Invalid PDU parameter
					System.err.println("Invalid PDU parameter"+e);
					e.printStackTrace();
					FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
					FILE("log/SMPP_ERROR"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
				} catch (final ResponseTimeoutException e) {
					// Response timeout
					System.err.println("Response timeout"+e);
					e.printStackTrace();
					FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
					FILE("log/SMPP_ERROR"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
				} catch (final InvalidResponseException e) {
					// Invalid response
					System.err.println("Receive invalid respose"+e);
					e.printStackTrace();
					FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
					FILE("log/SMPP_ERROR"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
				} catch (final NegativeResponseException e) {
					// Receiving negative response (non-zero command_status)
					System.err.println("Receive negative response"+e);
					e.printStackTrace();
					FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
					FILE("log/SMPP_ERROR"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
				} catch (final IOException e) {
					System.err.println("IO error occur"+e);
					e.printStackTrace();
					FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
					FILE("log/Error"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
				} catch (final Exception e) {
					System.err.println("IO error occur"+e);
					e.printStackTrace();
					FILE("log/SMPP_"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
					FILE("log/SMPP_ERROR"+DATE_FORMAT()+".txt","SEND#"+SAddr+"#"+DAddr+"#"+messg+"#"+SType+"#"+e.getMessage());
				}


				//SLEEP(50000);
			}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static String toHexHind(String str)
	{
		StringBuffer ostr = new StringBuffer();
	    try {

			for(int i=0; i<str.length(); i++)
			{
				String ch = ""+str.charAt(i);

				ostr.append(String.format("%04x", new BigInteger(ch.getBytes("UTF-8"))));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ostr.toString();
	}

private static byte hexStringToByte(final String encoded) {
    if ((encoded.length() % 2) != 0)
        throw new IllegalArgumentException("Input string must contain an even number of characters");

    final byte result[] = new byte[encoded.length()/2];
    final char enc[] = encoded.toCharArray();
    for (int i = 0; i < enc.length; i += 2) {
        StringBuilder curr = new StringBuilder(2);
        curr.append(enc[i]).append(enc[i + 1]);
        result[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
    }
    return result[0];
}


private static byte[] hexStringToByteArray(final String encoded)
{

		String ENCODED=encoded;
		if ((encoded.length() % 2) != 0)
			ENCODED=encoded+"0";
		final byte result[] = new byte[ENCODED.length()/2];
		try
		{
				
				final char enc[] = ENCODED.toCharArray();
				for (int i = 0; i < enc.length; i += 2) 
				{
				StringBuilder curr = new StringBuilder(2);
				curr.append(enc[i]).append(enc[i + 1]);
				result[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    
    return result;
}

private static String  stringToHex(String str) {
		  char[] chars = str.toCharArray();
		  StringBuffer strBuffer = new StringBuffer();
		  for (int i = 0; i < chars.length; i++) {
		    strBuffer.append(Integer.toHexString((int) chars[i]));
		  }
		  return strBuffer.toString();
		  }

 private static int  countsegments(String str) {
	 String msg=str;
	int totalSize=254,splitsize=230,index=0,ftotalsegment=0,splitpos=0;
    double totalsegments=0.0;
	int len=  msg.length();
		 double length = (double) len;
		  totalsegments = length / splitsize ;
		  double totalsegment=Math.ceil(totalsegments);
		  ftotalsegment = (int)totalsegment;
		return ftotalsegment;
	}



public static void PUSH_APP()
{
	String qquery ="select msgid,ani,message,date_time,status,dnis,type,operator,circle,priority from master_db.tbl_new_sms1  where (status=0 or status=5) and  operator='UNIM' and type in('LAVA','TXT','RNG','WAP','SIM','silent','hindi')  order by priority limit 10";
	ResultSet rs = null;
	try {
		rs = dbConn().createStatement().executeQuery(qquery);
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	if(rs!=null)
	{
		try {
			while(rs.next())
			{
				try{

						// Get data from Table
						String ANI=rs.getString("ani");
						String DNIS=rs.getString("dnis");
						String MESSAGE=rs.getString("message");
						String MESSAGETYPE=rs.getString("type");
						String ID=rs.getString("msgid");
						String CIRCLE=rs.getString("circle");
						if(ANI.length()==10)
						ANI="91"+ANI;




			        	System.out.println("Ready to Send messages");
									                S_Type.add("CMT");
									                S_Addr.add(DNIS);//8341425904"); //8546048758");//8532076165"); //8341425904");//8532076165");//8546048758");// 9175154905
									                D_Addr.add(ANI);
									                message.add(MESSAGE);
									                messagetype.add(MESSAGETYPE);

						//dbConn().createStatement().executeUpdate("insert into master_db.tbl_new_sms1_log select *,now() from master_db.tbl_new_sms1 where msgid="+ID );
						dbConn().createStatement().executeUpdate("delete from master_db.tbl_new_sms1 where msgid="+ID );

					}
					catch(Exception e)
					{
			             e.printStackTrace();
					}
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	} // function ends



public static  boolean isCurrentTimeBetween(String starthhmmss, String endhhmmss) throws ParseException{
    	  DateFormat hhmmssFormat = new SimpleDateFormat("yyyyMMddhh:mm:ss");
    	  java.util.Date now = new Date();
    	  String yyyMMdd = hhmmssFormat.format(now).substring(0, 8);

    	  return(hhmmssFormat.parse(yyyMMdd+starthhmmss).after(now) &&
    	    hhmmssFormat.parse(yyyMMdd+endhhmmss).before(now));
    	 }



    	 
public static String toHex(String arg) 
    	 {
		   return String.format("%x", new java.math.BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    	 }

public static byte[] createContent (String text, String url, byte hrefTagToken) {
    ByteArrayOutputStream msg = new ByteArrayOutputStream();

    //build WDP header (UDH)
    msg.write(0x06);
    msg.write(0x05);
    msg.write(0x04);
    msg.write(0x0b);
    msg.write(0x84);
    msg.write(0x23);
    msg.write(0xf0);

    //build PDU
    byte[] pduBody = getPduBody(text, url, hrefTagToken);

    //Write WSP header
    msg.write(0x25);
    msg.write(0x06);
    msg.write(0x08); //the length of the next 8 bytes
    msg.write(0x03);
    msg.write(0xAE);
    msg.write(0x81);
    msg.write(0xEA);
    msg.write(0xaf);
    msg.write(0x82);
    msg.write(0xB4);
    msg.write(0x84);
    msg.write(pduBody, 0, pduBody.length);

    return msg.toByteArray();
}

private static byte[] getPduBody (String text, String url, byte hrefTagToken) {
    try {
    ByteArrayOutputStream pdu = new ByteArrayOutputStream();
    OutputStreamWriter pduWriter = new OutputStreamWriter(pdu, "UTF-8");
    pdu.write(0x01); // Version 1.1
    pdu.write(0x05); // ServiceIndication 1.0
    pdu.write(0x6A); // UTF-8
    pdu.write(0x00);
    pdu.write(SetTagTokenIndications((byte)0x5, false, true)); // &lt;si>
    pdu.write(SetTagTokenIndications((byte)0x6, true, true)); // &lt;indication href=... action=...>
    pdu.write(hrefTagToken); // href=
    pdu.write(0x03); // Inline string follows
    pduWriter.write(url);
    pduWriter.flush();
    pdu.write(0x00);
    pdu.write(0x07); // Action="signal-medium"
    pdu.write(0x01); // >
    pdu.write(0x03); // Inline string follows
    int maxTextLen = 119 - pdu.size();
    int charsToWrite = charsInTruncatedString(text, maxTextLen);
    pduWriter.write(text, 0, charsToWrite);
    pduWriter.flush();
    pdu.write(0x00);
    pdu.write(0x01); // &lt;/indication>
    pdu.write(0x01); // &lt;/si>

    return pdu.toByteArray();
    } catch (IOException e) {
        // There is no sane reason for us to get here.
        return null;
    }
}

// Returns the number of characters from the string that
// fit into byteLength bytes, when encoded in UTF-8.
private static int charsInTruncatedString(String string, int byteLimit) throws UnsupportedEncodingException {
    byte[] byteArray = string.getBytes("UTF-8");
    if (byteLimit >= byteArray.length)
        return string.length();
    int charCounter = 0, curr = 0;
    while (curr <= byteLimit) {
        charCounter ++;
        if ((byteArray[curr] & 0x80) == 0x0)
            curr ++;
        else if ((byteArray[curr] & 0xE0) == 0xC0)
            curr += 2;
        else if ((byteArray[curr] & 0xF0) == 0xE0)
            curr += 3;
        else
            curr += 4;
    }
    return (charCounter - 1);
}

private static byte SetTagTokenIndications(byte token, boolean hasAttributes, boolean hasContent)
{
    if (hasAttributes)
        token |= 0xC0;
    if (hasContent)
        token |= 0x40;

    return token;
}
}

/*SELECT COUNT(*)  FROM  master_db.tbl_new_sms1 LIMIT 10;
SELECT * FROM  master_db.tbl_new_sms1 LIMIT 10;
INSERT INTO master_db.tbl_new_sms1(ani,message,date_time,STATUS,dnis,TYPE,operator,circle)
  VALUES('8586968482','परिणाम स्वरूप', NOW(),0,'54646','hindi','UNIM','DEL');*/
