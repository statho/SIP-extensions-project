package gov.nist.sip.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.UnsupportedOperationException;


class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }
}

public class User {
	protected String username = null;
	protected String password = null;
	protected String policy = null;
	protected String creditCardNo = null;
	
	public User(String source, String password, Database db) throws RegistrationException{
		username = source;
		this.password = password;
		Connection conn = db.Connect();
		String isregisteredQ = "SELECT password FROM users WHERE username=?;";

		if(username.isEmpty()){
			throw new RegistrationException("Username cannot be empty");
		}
		else if(password.isEmpty()){
			throw new RegistrationException("Password cannot be empty");
		}
		
		try{
			PreparedStatement prep = conn.prepareStatement(isregisteredQ);
			prep.setString(1, username);
			ResultSet rs = prep.executeQuery();
			
			boolean isinserted = rs.next();
			
			if(isinserted){
				if(password.equals(rs.getString("password")))
					System.out.println("User:" + username + " succesfully logged in with pass:" + password);
				else
					throw new RegistrationException("User: " + username + " used wrong password.");
			}
			else
				throw new RegistrationException("Username: "+username + " not found.");
			
		
		}
		catch (SQLException e) {
        	e.printStackTrace();
        	
		}
		db.Disconnect();
	}
	
	public User(String username, String password, String policy, String creditCardNo, Database db) throws RegistrationException {
		this.username = username;
		this.password = password;
		this.policy = policy;
		this.creditCardNo = creditCardNo;
		String isregisteredQ = "SELECT password FROM users WHERE username=?;";
		
		if(username.isEmpty()){
			throw new RegistrationException("Username cannot be empty");
		}
		else if(password.isEmpty()){
			throw new RegistrationException("Password cannot be empty");
		}
		else if(policy.isEmpty()){
			throw new RegistrationException("Policy cannot be empty");
		}
		else if(creditCardNo.isEmpty()){
			throw new RegistrationException("Credit card number cannot be empty");
		}
		else {
			Connection conn = db.Connect();
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
				else{
					throw new RegistrationException("Username: " + username + " is taken.");
				}
			}		
			catch (SQLException e) {
	        	e.printStackTrace();
			}
		}
		db.Disconnect();
	}

}
