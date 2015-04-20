import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;



class UninorThread extends Thread{
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
	
	public void run(){
		try{
			dbcon();
			stmtupdate=con.createStatement();
			stmtselect=con.createStatement();
			ArrayList<String> scorelist=new ArrayList<String>();
			while(true){
				String squery="select distinct(score) from uninor_summer_contest.tbl_contest_subscription order by score desc;"; 
				ResultSet rs=stmtselect.executeQuery(squery);
				
				while(rs.next())
				{
					scorelist.add(rs.getString("score"));
				}
				//System.out.println(scorelist);
				Iterator<String> itr=scorelist.iterator();
				int rankcount=0;
				while(itr.hasNext())
				{
					String updatequery="update uninor_summer_contest.tbl_contest_subscription set Rank='"+ ++rankcount+"' where score='"+itr.next()+"';";
					int updaterow=stmtupdate.executeUpdate(updatequery);
				//	System.out.println(updaterow);
				}
				con.close();
				System.out.println("uninor process complete");
				System.exit(0);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}


public class UninorContestRank {
	public static void main(String[] args) {
		UninorThread uth= new UninorThread();
		uth.start();
	}

}

