//package org.vodafone.wap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;



public class GetSms implements Runnable
{
	public Connection con=null;
	public Statement stmt=null;
	public Statement stmt_del=null;
	public Statement stmt_update=null;
	HitUrl hu;
	public void createConnect() 
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://10.43.248.137/master_db", "ivr", "ivr");
//			con=DriverManager.getConnection("jdbc:mysql://203.199.126.129/master_db", "billing", "billing");
			stmt=con.createStatement();
			stmt_del=con.createStatement();
			stmt_update=con.createStatement();
			
		}
		catch(Exception e)
		{
			System.out.println("Error at DB Connection "+e);
			System.exit(0);
		}
		
	}

	public void run()
	{
		createConnect();
		hu=new HitUrl();
		try
		{
			while(true)
			{

				try
				{
				ResultSet rs2=stmt.executeQuery("select count(*)cnt from tbl_wap_sms nolock ");
				if(rs2.next())
					if(rs2.getInt("cnt")>0)
					{
						ResultSet rs=stmt_update.executeQuery("select msgid,ani,message,date_time,status,dnis from tbl_wap_sms nolock group by date_time order by date_time desc limit 100");
						while(rs.next())
						{
							String msgid=rs.getString("msgid");
						    hu.setAni(rs.getString("ani"));
						    hu.setMessage(rs.getString("message"));
						    hu.setDate_time(rs.getString("date_time"));
						    hu.setStatus(rs.getInt("status"));
						    hu.setDnis(rs.getString("dnis"));
						    
						    hu.hitUrl();
							
							
							stmt_del.executeUpdate("delete from tbl_wap_sms where msgid="+msgid);
						}
					}
					else
					{
						Thread.sleep(120000);
						System.out.println("No sms to send");
					}
				}
				catch(Exception e)
				{
					System.out.println("Error at run inside loop - "+e);
					createConnect();
				}
			
			}
			
		} 
		catch (Exception e)
		{
			System.out.println("Error at run"+e);
		}
	}
	

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		GetSms ob=new GetSms();
		ob.run();
	

	}
	

}
