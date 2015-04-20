import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;



class MTSThread extends Thread{
	public static Connection con=null;
	public static String dsn="Mts_summer_contest";
	public static String IP="database.master_mts";
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
	
	public void run(){
		try{
			dbcon();
			stmtupdate=con.createStatement();
			stmtselect=con.createStatement();
			ArrayList<String> scorelist=new ArrayList<String>();
			while(true){
				String squery="select distinct(score) from Mts_summer_contest.tbl_contest_subscription order by score desc;"; 
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
					String updatequery="update Mts_summer_contest.tbl_contest_subscription set Rank='"+ ++rankcount+"' where score='"+itr.next()+"';";
					int updaterow=stmtupdate.executeUpdate(updatequery);
				//	System.out.println(updaterow);
				}
				con.close();
				System.out.println("MTS process complete");
				System.exit(0);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}



public class MtsContestRank {
	public static void main(String[] args) {
		MTSThread mth= new MTSThread();
		mth.start();
	}

}

