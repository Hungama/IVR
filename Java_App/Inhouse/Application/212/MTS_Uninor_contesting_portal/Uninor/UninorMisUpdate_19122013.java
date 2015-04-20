import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;


public class UninorMisUpdate extends Thread
{
	public static Connection con=null;
	public static String dsn="uninor_summer_contest";
	public static String IP="192.168.100.224:3306";
	public static String username="ivr";
	public static String pwd="ivr";
	public static String cnt=null;
	public static Statement stmtupdate=null,stmtselect=null;
	private Connection dbcon()
	{
		while(true)
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				con=DriverManager.getConnection("jdbc:mysql://"+IP+"/"+dsn, username,pwd);
				System.out.println("Database Connection established!");
				return con;
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void run()
	{
		try
		{
			dbcon();
			stmtselect=con.createStatement();
			stmtupdate=con.createStatement();
			while(true)
			{	
				//String uquery="update question_played set circle=master_db.getCircle(ANI) where date(date_time)=subdate(date(now()),1)";
				//stmtupdate.executeUpdate(uquery);

				String str=null;
				str="select distinct(circle) from question_played where date(date_time)=subdate(date(now()),1)";
				ResultSet rs=stmtselect.executeQuery(str);
				ArrayList<String> list=new ArrayList<String>();
				while(rs.next())
				{
					list.add(rs.getString("circle"));
				}
				
				Iterator itr=list.iterator();
				
				 ArrayList finallist=new ArrayList();
				 ArrayList<String> Anilist=new ArrayList<String>();
				while(itr.hasNext())
				{
					//String str1="select ANI from tbl_contest_dailymis where circle='"+itr.next()+"' and date(date_time)=subdate(date(now()),1) order by score desc limit 10";
					
					String str1="select distinct(ANI) from question_played where circle='"+itr.next()+"' and date(date_time)=subdate(date(now()),1) order by score desc";
					
					ResultSet rs1=stmtselect.executeQuery(str1);
				
					while(rs1.next())
					{
						Anilist.add(rs1.getString("ANI"));
					}
				}
				
				Iterator aniIterator=Anilist.iterator();
				
				
				
				
				while(aniIterator.hasNext())
				{
					String ANI=(String)aniIterator.next();
					
					//String str2="select ANI,score,circle,(select chrg_amount from tbl_contest_subscription where ANI='"+ANI+"') as chrg_amount, question_played,SOU from tbl_contest_dailymis where ANI='"+ANI+"' and date(date_time)=subdate(date(now()),1)";
					
					String str2="select ANI,circle, max(score) as cscore,count(*) as cnt, (select ifnull(max(score),0) from question_played where ani='"+ANI+"' and date(date_time)<subdate(date(now()),1) order by score desc limit 1) as pscore,(select ifnull(SOU,0) from tbl_contest_sou where date(date_time)=subdate(date(now()),1) and ANI='"+ANI+"') as sou, (select ifnull(chrg_amount,0) from master_db.tbl_billing_success where msisdn='"+ANI+"' and service_id=1423 and  date(response_time)=subdate(date(now()),1) and mode!='CallBack' order by response_time desc limit 1) as chrg_amount from question_played where ani='"+ANI+"' and date(date_time)=subdate(date(now()),1) order by score desc limit 1";
					
					ResultSet rs2=stmtselect.executeQuery(str2);
					while(rs2.next())
					{
						ANI=rs2.getString("ANI");
						String circle=rs2.getString("circle");
						int cscore=Integer.parseInt(rs2.getString("cscore"));
						int pscore=Integer.parseInt(rs2.getString("pscore"));
						int finalscore=cscore-pscore;
						
						cnt=rs2.getString("cnt");
						
						String sou=rs2.getString("sou");
						if(sou==null)
							sou="0";
						String chrg_amount=rs2.getString("chrg_amount");
						if(chrg_amount==null)
                                                        chrg_amount="0";

						double pulse=Math.ceil((Double.parseDouble(sou)/60));
						
						finallist.add(ANI);
						finallist.add(circle);
						finallist.add(cnt);
						finallist.add(finalscore);
						finallist.add(sou);
						finallist.add(chrg_amount);
						finallist.add(pulse);
						
						String query="insert into tbl_contest_misdaily(ANI,total_question_play,score,date_time,circle,SOU,lastChargeAmount,pulses)values('"+ANI+"','"+cnt+"','"+finalscore+"',subdate(date(now()),1),'"+circle+"','"+sou+"','"+chrg_amount+"','"+pulse+"')";
						
						stmtupdate.executeUpdate(query);
						
					}
		
				}
			//	System.out.println(finallist);
			
				System.exit(0);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) 
	{
		UninorMisUpdate tps=new UninorMisUpdate();
		tps.start();
	}
}

