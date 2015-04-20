//package org.vodafone.wap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;



public class GetSmsMCDBack implements Runnable
{
	public Connection con=null;
	public Statement stmt=null;
	public Statement stmt_del=null;
	public Statement stmt_update=null,stmt_update1=null;
	HitUrl hu;
	public void createConnect() 
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://192.168.100.224/Hungama_ENT_MCDOWELL", "ivr", "ivr");
			stmt=con.createStatement();
			stmt_del=con.createStatement();
			stmt_update=con.createStatement();
			stmt_update1=con.createStatement();
			
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
		String strAni=null;
		String strMsg=null;
		try
		{
			while(true)
			{
				ResultSet rs2=stmt.executeQuery("select count(*)cnt from tbl_sendsms_tp nolock where status=1 ");
				if(rs2.next())
					if(rs2.getInt("cnt")>0)
					{
						ResultSet rs=stmt_update.executeQuery("select msgid,ani,message,date_time,status,dnis,circle from tbl_sendsms_tp nolock where status=1 limit 500");
						while(rs.next())
						{
							String msgid=rs.getString("msgid");
							strAni=rs.getString("ani");
						    hu.setAni(strAni);
							strMsg=rs.getString("message");
						    hu.setMessage(strMsg);
						    hu.setDate_time(rs.getString("date_time"));
						    hu.setStatus(rs.getInt("status"));
						    hu.setDnis(rs.getString("dnis"));
						  // hu.setCircle(rs.getString("circle"));
						    
						    hu.hitUrl();
							stmt_update1.executeUpdate("insert into tbl_sendsms_tp_log values('"+strAni+"','"+strMsg+"',now())");	
							
							stmt_del.executeUpdate("delete from tbl_sendsms_tp where msgid="+msgid);
						}
					}
					else
					{
						Thread.sleep(5000);
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
		GetSmsMCDBack ob=new GetSmsMCDBack();
		ob.run();
	

	}
	

}
