import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;


public class TopScore extends Thread
{
	public static Connection con=null;
	public static String dsn="Mts_summer_contest";
	public static String IP="10.130.14.160:3306";
	public static String username="billing";
	public static String pwd="billing";
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
				String str=null;
				str="select distinct(circle) from tbl_contest_dailymis";
				ResultSet rs=stmtselect.executeQuery(str);
				ArrayList<String> list=new ArrayList<String>();
				while(rs.next())
				{
					list.add(rs.getString("circle"));
				}

				Iterator itr=list.iterator();
				 ArrayList<String> finallist=new ArrayList<String>();
				 ArrayList<String> Anilist=new ArrayList<String>();
				while(itr.hasNext())
				{
					String str1="select distinct(ANI) from tbl_contest_dailymis where circle='"+itr.next()+"' and date(date_time)=subdate(date(now()),1) group by ANI order by sum(score) desc limit 20";
					//System.out.println("FiredQuery:"+str1);
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

					String str2="select ANI,sum(score) as score,circle,(select ifnull(chrg_amount,0) from master_db.tbl_billing_success where msisdn='"+ANI+"' and service_id=1123 and MODE!='CallBack' and date(response_time)=subdate(date(now()),1) order by response_time desc limit 1) as chrg_amount, sum(question_played) as question_played,sum(SOU) as SOU from tbl_contest_dailymis where ANI='"+ANI+"' and date(date_time)=subdate(date(now()),1)";
					ResultSet rs2=stmtselect.executeQuery(str2);
					while(rs2.next())
					{
						/*finallist.add(rs2.getString("ANI"));
						finallist.add(rs2.getString("Score"));
						finallist.add(rs2.getString("circle"));
						String chrg_amount =rs2.getString("chrg_amount");
						if(chrg_amount==null)
							chrg_amount="0";
						finallist.add(chrg_amount);
						finallist.add(rs2.getString("question_played"));
						finallist.add(rs2.getString("SOU"));*/
						ANI=rs2.getString("ANI");
						String score=rs2.getString("Score");
						String circle = rs2.getString("circle");
						String chrg_amount =rs2.getString("chrg_amount");
						if(chrg_amount==null)
							chrg_amount="0";

						String question_played = rs2.getString("question_played");
						String SOU=rs2.getString("SOU");
						double pulse=Math.ceil((Double.parseDouble(SOU)/60));
						finallist.add(ANI);
						finallist.add(score);
						finallist.add(circle);
						finallist.add(chrg_amount);
						finallist.add(question_played);
						finallist.add(SOU);

						String query="insert into tbl_contest_topscore(ANI,total_question_play,score,date_time,circle,SOU,lastChargeAmount,pulses)values('"+ANI+"','"+question_played+"','"+score+"',subdate(date(now()),1),'"+circle+"','"+SOU+"','"+chrg_amount+"','"+pulse+"')";
						System.out.println("FiredQuery:"+query);
						stmtupdate.executeUpdate(query);

					}

				}



			//SendEmail sm=new SendEmail(finallist);
			//sm.send();
				try
				{
					if(con!=null)
						con.close();
						System.exit(0);
				}catch(Exception ee)
				{
					System.out.println("Excdeption:"+ee.getMessage());

					ee.printStackTrace();
					System.exit(0);
				}

			}
		}
		catch(Exception e)
		{
			try
			{
				if(con!=null)
					con.close();
			}catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}
	public static void main(String[] args)
	{
		TopScore tps=new TopScore();
		tps.start();
	}
}

