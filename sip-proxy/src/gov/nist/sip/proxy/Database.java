package gov.nist.sip.proxy;

import java.sql.*;

public class Database {
	   // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost:3306/softengdb";
	   
	   //  Database credentials
	   static final String USER = "root";
	   static final String PASS = "";
	   
	   Connection conn = null;
	   
	   public Connection Connect() {
		   try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver");

			      //STEP 3: Open a connection
			      System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(DB_URL,USER,PASS);  
		   }catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
		   }
	      return conn;
	   }
	   
	   
	   public void Disconnect(){		  
		   try{
			   if(conn!=null){
				   conn.close();
				   System.out.println("Connection with database terminated");
			   }
		   }catch(SQLException se){
			   se.printStackTrace();
		   }
	   }
}