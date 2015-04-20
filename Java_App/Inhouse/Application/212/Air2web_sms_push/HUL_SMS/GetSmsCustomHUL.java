//package org.vodafone.wap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;


public class GetSmsCustomHUL implements Runnable
{
	public Connection con=null;
	public Statement stmt=null;
	public Statement stmt_del=null;
	public Statement stmt_update=null;
	HitUrlHul hu;
	public void createConnect()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://192.168.100.224/hul_hungama", "ivr", "ivr");
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
		hu=new HitUrlHul();
		try
		{
			while(true)
			{
				Date d=new Date();
				int currenthour=d.getHours();
				if(currenthour>=9 && currenthour<20)
					{
						ResultSet rs2=stmt.executeQuery("select count(*)cnt from tbl_sendsms_tpurl nolock ");
						if(rs2.next())
							if(rs2.getInt("cnt")>0)
							{
								ResultSet rs=stmt_update.executeQuery("select msgid,ani,message,date_time,status,dnis,circle from tbl_sendsms_tpurl nolock limit 100");
								while(rs.next())
								{
									String msgid=rs.getString("msgid");
									hu.setAni(rs.getString("ani"));
									hu.setMessage(rs.getString("message"));
									hu.setDate_time(rs.getString("date_time"));
									hu.setStatus(rs.getInt("status"));
									hu.setDnis(rs.getString("dnis"));
									hu.setCircle(rs.getString("circle"));

									hu.hitUrl();

									stmt_del.executeUpdate("delete from tbl_sendsms_tpurl where msgid="+msgid);
								}
							}
							else
							{
								Thread.sleep(120000);
								System.out.println("No sms to send");
							}
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
		GetSmsCustomHUL ob=new GetSmsCustomHUL();
		ob.run();
	}
}
