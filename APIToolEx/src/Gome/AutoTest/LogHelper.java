package Gome.AutoTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.http.util.ExceptionUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogHelper{

	static{

		try{
//			String projectPath = URLDecoder.decode(System.getProperty("user.dir"),"utf-8");
			String projectPath = System.getProperty("user.dir");
			
			InputStream in = LogHelper.class.getResourceAsStream("log4j.properties"); 
//			Properties prop = new Properties();
//			prop.load(in);
			
			StringBuffer   out   =   new   StringBuffer(); 
	        byte[]   b   =   new   byte[4096]; 
	        for   (int   n;   (n   =   in.read(b))   !=   -1;)   { 
	                out.append(new   String(b,   0,   n)); 
	        } 
	        String str = out.toString(); 
			
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			
			projectPath = projectPath.replace("\\", "//");
			
			System.out.println(projectPath);
			str=str.replace("projectPath",projectPath);
			str=str.replace("exeDate",sdf.format(d));
			
			InputStream   in_nocode = new ByteArrayInputStream(str.getBytes());  
			PropertyConfigurator.configure(in_nocode);
		}catch (Exception e){
			System.out.println(e.getMessage());
		}

//		PropertyConfigurator.configure(in_nocode);
//		PropertyConfigurator.configure(configFileEx);
	}


	public static void info(Object message){
		StackTraceElement stack[] = (new Throwable()).getStackTrace();  
		Logger logger = Logger.getLogger(stack[1].getClassName());  
		
		logger.log(LogHelper.class.getName(), Level.INFO, message, null);  
	}

	public static void error(Object message){
		StackTraceElement stack[] = (new Throwable()).getStackTrace();  
		Logger logger = Logger.getLogger(stack[1].getClassName());  
		logger.log(LogHelper.class.getName(), Level.ERROR, message, null);  
	}
	public static void warn(Object message){
		StackTraceElement stack[] = (new Throwable()).getStackTrace();  
		Logger logger = Logger.getLogger(stack[1].getClassName());  
		logger.log(LogHelper.class.getName(), Level.WARN, message, null);  
	}

	public static void exception(Exception e) {  
		StackTraceElement stack[] = (new Throwable()).getStackTrace();  
		Logger logger = Logger.getLogger(stack[1].getClassName());  
		logger.log(LogHelper.class.getName(), Level.ERROR, e.getMessage(), null);  
	} 

}
