package gov.nist.sip.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.UnsupportedOperationException;


class NotUserException extends Exception {
    public NotUserException(String message) {
        super(message);
    }
}

class WrongPasswordException extends Exception {
    public WrongPasswordException(String message) {
        super(message);
    }
}


public class User {
	protected String username = null;
	protected String password = null;
	protected String policy = null;
	protected String creditCardNo = null;
	
	public User(String source, String password, Database db) throws WrongPasswordException, NotUserException  {
		username = source;
		this.password = password;
		Connection conn = db.Connect();
		String isregisteredQ = "SELECT password FROM users WHERE username=?;";

		try{
			PreparedStatement prep = conn.prepareStatement(isregisteredQ);
			prep.setString(1, username);
			ResultSet rs = prep.executeQuery();
			
			boolean isinserted = rs.next();
			
			if(isinserted){
				if(password.equals(rs.getString("password")))
					System.out.println("User:" + username + " succesfully logged in with pass:" + password);
				else
					throw new WrongPasswordException("User: " + username + " used wrong password: " + password + ".");
			}
			else
				throw new NotUserException("Username: "+username + " does not correspond to a user.");
			
		
		}
		catch (SQLException e) {
        	e.printStackTrace();
        	
		}
		db.Disconnect();
	}
	
	public User(String username, String password, String policy, String creditCardNo, Database db) {
		
		this.username = username;
		this.password = password;
		this.policy = policy;
		this.creditCardNo = creditCardNo;
		Connection conn = db.Connect();
		String isregisteredQ = "SELECT password FROM users WHERE username=?;";

		try{
			PreparedStatement prep = conn.prepareStatement(isregisteredQ);
			prep.setString(1, username);
			ResultSet rs = prep.executeQuery();
			
			boolean isinserted = rs.next();
			
			if(!isinserted){
				String registerQ = "INSERT INTO users(username, password, policy, creditCardNo, charge) VALUES(?, ?, ?, ?, 0.0);";
				prep = conn.prepareStatement(registerQ);
				prep.setString(1, username);
				prep.setString(2, password);
				prep.setString(3, policy);
				prep.setString(4, creditCardNo);
				System.out.println(prep);
				prep.executeUpdate();
			}
			
		}		
		catch (SQLException e) {
        	e.printStackTrace();
		}
		db.Disconnect();
	}

}
