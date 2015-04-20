import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;


public class Uninor_monthly extends Thread
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

				String str=null;
				str="select distinct(ANI) from question_played where date(date_time)>='2013-10-06' and date(date_time)<='2013-11-07'";

				ResultSet rs=stmtselect.executeQuery(str);
				ArrayList<String> list=new ArrayList<String>();
				while(rs.next())
				{
					list.add(rs.getString("ANI"));
				}

				Iterator itr=list.iterator();

				 ArrayList finallist=new ArrayList();
				 ArrayList<String> Anilist=new ArrayList<String>();
				while(itr.hasNext())
				{
					String ANI=(String)itr.next();
					String str1="select ANI,circle,min(score) as minscore,max(score) as maxscore,count(*) as total_ques from question_played where date(date_time)>='2013-10-06' and date(date_time)<='2013-11-07' and ANI='"+ANI+"' and prompt_name is not null";

					ResultSet rs1=stmtselect.executeQuery(str1);

					while(rs1.next())
					{
							ANI=rs1.getString("ANI");
							String circle=rs1.getString("circle");
							String mscore=rs1.getString("minscore");
							String mxscore=rs1.getString("maxscore");
							String t_ques=rs1.getString("total_ques");
							if(mscore==null)
								mscore="0";
							if(mxscore==null)
								mxscore="0";
							if(t_ques==null)
								t_ques="0";

							int minscore=Integer.parseInt(mscore);
							int maxscore=Integer.parseInt(mxscore);
							int finalscore=maxscore-minscore;
							int total_ques=Integer.parseInt(t_ques);
							if(total_ques!=0)
							{
								String query="insert into Inhouse_tmp.tbl_contest_monthly(ANI,circle,score,total_ques)values('"+ANI+"','"+circle+"','"+finalscore+"','"+total_ques+"')";
								stmtupdate.executeUpdate(query);
						}
					}
				}




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
		Uninor_monthly tps=new Uninor_monthly();
		tps.start();
	}
}

