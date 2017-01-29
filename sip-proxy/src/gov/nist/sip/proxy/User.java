package gov.nist.sip.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.UnsupportedOperationException;

class WrongPasswordException extends Exception {
    public WrongPasswordException(String message) {
        super(message);
    }
}

public class User {
	protected String username;
	protected String password;

	public User(String source, String password, Database db) throws WrongPasswordException  {
		username = source;
		password = password;
		Connection conn = db.Connect();
		String isregisteredQ = "SELECT password FROM users WHERE username=?;";

		try{
			PreparedStatement prep = conn.prepareStatement(isregisteredQ);
			prep.setString(1, username);
			ResultSet rs = prep.executeQuery();
			
			boolean isinserted = rs.next();
			
			if(isinserted){
				if(!password.equals(rs.getString("password")))
					throw new WrongPasswordException("User: " + username + " used wrong password: " + password + ".");
				else
					System.out.println("User:" + username + " succesfully logged in with pass:" + password);
			}
			else{
				String registerQ = "INSERT INTO users(username, password) VALUES(?, ?);";
				prep = conn.prepareStatement(registerQ);
				prep.setString(1, username);
				prep.setString(2, password);
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
