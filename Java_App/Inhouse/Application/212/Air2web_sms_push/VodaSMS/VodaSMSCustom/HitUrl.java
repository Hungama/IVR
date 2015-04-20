//package org.vodafone.wap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.*;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class HitUrl 
{
	private String ani;
	private String message;
	private String date_time;
	private String dnis;
	private String circle;
	
	public String getAni() {
		return ani;
	}
	public void setAni(String ani) {
		this.ani = ani;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDate_time() {
		return date_time;
	}
	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}
	public String getDnis() {
		return dnis;
	}
	public void setDnis(String dnis) {
		this.dnis = dnis;
	}
	 public void setCircle(String circle) {
                this.circle = circle;
        }
	public String getCircle() {
                return circle;
        }

	

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	private int flag,status;
	
	public String hitUrl()
	{
		String response="NOK";
		String urlString="";
		
		try{
			if(getCircle().equalsIgnoreCase("KER"))
			{
				 urlString="http://121.241.247.190:7501/failsafe/HttpLink?aid=428262&pin=HVOU1&signature=HNGAMA&mnumber="+getAni()+"&message="+URLEncoder.encode(getMessage(),"UTF-8");
			}
			else
			{
				 urlString="http://202.87.41.147/admin/ivr/ivr_upe.php?msisdn=91"+getAni()+"&code="+getDnis();
			}
		System.out.println("URL to Hit -" +urlString);
				
			URL url=new URL(urlString);
			HttpURLConnection urlcon= (HttpURLConnection)url.openConnection();
				if(urlcon.getResponseCode()==HttpsURLConnection.HTTP_OK)
				{
					String line="";
					BufferedReader bf= new BufferedReader(new InputStreamReader(urlcon.getInputStream()));  
					while((line=bf.readLine())!=null)
					{
						response=line;
					}
					bf.close();
					urlcon.disconnect();				
						
				}
			
		}
		catch(Exception e)
		{
			System.out.print("Exception under hit Url "+e);
			response="NOK";
		}
		log(response);
		return response;
	}

	public void log(String response)
	{
		try
		{
			Calendar mytoday = Calendar.getInstance();
			String mystrdate = formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2) + formatN(""+mytoday.get(Calendar.DATE),2);
			String mystrtime = formatN(""+mytoday.get(Calendar.HOUR_OF_DAY),2)+formatN(""+mytoday.get(Calendar.MINUTE),2)+formatN(""+mytoday.get(Calendar.SECOND),2);
			
			File f =new File("./log/"+formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2));
			if(!f.exists())
				f.mkdirs();
			
			FileOutputStream fos =new FileOutputStream(f+"/"+mystrdate+".txt", true);
			PrintStream ps=new PrintStream(fos);
			ps.println(mystrdate+"#"+mystrtime+"#"+response+"#"+getAni()+"#"+getDate_time()+"#"+getDnis()+"#"+getFlag()+"#"+getMessage()+"#"+getStatus()+"#");
			ps.close();
			fos.close();			
			System.out.println(mystrdate+"#"+mystrtime+"#"+response+"#"+getAni()+"#"+getDate_time()+"#"+getDnis()+"#"+getFlag()+"#"+getMessage()+"#"+getStatus()+"#");
		}
		catch(Exception e)
		{
			System.out.print("Exception under log "+e);

		}
	}

	private  String formatN(String str, int x)
	{
		int len;
		String ret_str="";
		len = str.length();
		if (len >= x)
			ret_str = str;
		else
		{
			for(int i=0; i<x-len; i++)
				ret_str = ret_str + "0";
			ret_str = ret_str + str;
		}
		return ret_str;
	}	

}
