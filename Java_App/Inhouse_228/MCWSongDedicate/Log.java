// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Log.java



import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author Reeju
 */
public  class Log
{
    static public RequestInfo objRequest = new RequestInfo();
	
    public static void writeLog(String strContent) throws IOException
    {
        String LogDir="";
       
         LogDir=objRequest.getLogPath();
       
             
        System.out.println("LogDir path is:" + LogDir);
        Date date = new Date();
        SimpleDateFormat fName = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = fName.format(date);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:");
        String ndate = df.format(date);
        String lineSep = "\n==============================" + ndate + "==================================\n";
        String contents = lineSep + strContent;
        File dirObj = new File(LogDir);
        try
        {
            if(!dirObj.isDirectory())
                dirObj.mkdirs();
            File file = new File(dirObj, fileName + ".log");
            if(!file.exists())
            {
                FileWriter fw = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(fw);
                out.write(contents);
                out.flush();
                out.close();
                fw.close();
            } else
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
                out.newLine();
                out.write(contents);
                out.flush();
                out.close();
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception from ErrorLog class!!" + e.getMessage());
        }
    }
}
