
import java.sql.Connection;


public class RequestInfo {
        private String Channel="";
	private String Ani="";
	private String Context="";
        private String Exten="";
        private int PRIORITY;
	private String Status="";
	private String Id="";
        private String CALLERID="";
        
         int ASTERISK_PORT;
         String ASTERISK_HOST = "";
         String ASTERISK_LOGINNAME = "";
         String ASTERISK_LOGINPWD = "";
         String ASTERISK_Channel = "";
         String ASTERISK_HangUpReason = "";
	String ASTERISK_DialNumber="";
        String ASTERISK_SipHeader="";
        String IsDND="";
         String LogPath="";
          String DriverMgr="";
           String Username="";
           String Password="";
           boolean CallOnProgress;
          public  Connection con = null;
         
         public String getLogPath() {
		return LogPath;
	}
	public void setLogPath(String LogPath)
	{
		LogPath= LogPath;
	}
        public String getDriverMgr() {
		return DriverMgr;
	}
        
        public boolean getISCallOnProgres()
	{
		return CallOnProgress;
	}
        public void SetISCallOnProgres(boolean callProgress) {
		CallOnProgress=callProgress;
	}
        
	public void setDriverMgr(String strDrivermgr)
	{
		DriverMgr= strDrivermgr;
	}
        public String getUserName() {
		return Username;
	}
	public void setUserName(String strUsername)
	{
		Username= strUsername;
	}
         public String getPassword() {
		return Password;
	}
	public void setPassword(String strPassword)
	{
		Password= strPassword;
	}
        
        public Connection getConn() {
		return con;
	}
	public void setConn(Connection Connection)
	{
		con= Connection;
	}
         
	public String getChannel() {
		return ASTERISK_Channel;
	}
	public void setChannel(String channel)
	{
		ASTERISK_Channel= channel;
	}
        public String getSipHeader() {
		return ASTERISK_SipHeader;
	}
	public void setSipHeader(String SipHeader)
	{
		ASTERISK_SipHeader= SipHeader;
	}
        public String getDND() {
		return IsDND;
	}
	public void setDND(String DND)
	{
		IsDND= DND;
	}
        public String getExten() {
		return Exten;
	}
	public void setExten(String exten)
	{
		Exten = exten;
	}
         public int getPriority() {
		return PRIORITY;
	}
	public void setPriority(int Priority)
	{
		PRIORITY = Priority;
	}
	public String getAni()
	{
		return Ani;
	}
	public void setAni(String ani)
	{
		Ani = ani;
	}
	public String getContext()
	{
		return Context;
	}
	public void setContext(String context) {
		Context = context;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
        public void SetCallerId(String CallerId) {
		CALLERID = CallerId;
	}
	public String getCallerId() {
		return CALLERID;
	}
        
        public String getAsteriskHost() {
		return ASTERISK_HOST;
	}
	public void setAsteriskHost(String AsteriskHost) {
		ASTERISK_HOST = AsteriskHost;
	}
        public void SetAsteriskPort(int AsteriskPort) {
		ASTERISK_PORT = AsteriskPort;
	}
	public int getAsteriskPort() {
		return ASTERISK_PORT;
	}
        
          public String getAsteriskLogin(String AsteriskLogin) {
		return ASTERISK_LOGINNAME;
	}
	public void setAsteriskLogin(String AsteriskLogin) {
		ASTERISK_LOGINNAME = AsteriskLogin;
	}
        public void SetAsteriskPassword(String AsteriskPwd) {
		ASTERISK_LOGINPWD = AsteriskPwd;
	}
	public String getAsteriskPassword() {
		return ASTERISK_LOGINPWD;
	}
       public void SetCallChannel(String CallChannel) {
		ASTERISK_Channel = CallChannel;
	}
	public String getCallChannel() {
		return ASTERISK_Channel;
	}
        public void SetCallHangUpReason(String HangReason) {
		ASTERISK_HangUpReason = HangReason;
	}
	public String getCallHangUpReason() {
		return ASTERISK_HangUpReason;
	}
 	public void SetDialNumber(String DialNumber) {
		ASTERISK_DialNumber= DialNumber;
	}
	public String getDialNumber() {
		return ASTERISK_DialNumber;
	}

}
