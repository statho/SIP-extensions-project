package gov.nist.sip.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.UnsupportedOperationException;

class WrongUserException2 extends Exception {
    public WrongUserException2(String message) {
        super(message);
    }
}

public class ForwardingServer {
	
	protected Connection conn;
	
	public ForwardingServer(Database db) {
		conn = db.Connect();
	}
	
	public void ForwardUser(String source, String target) throws WrongUserException2{
		String existingUser = "SELECT * FROM users WHERE username=?;";
		
		try {
			PreparedStatement prep = conn.prepareStatement(existingUser);
			prep.setString(1, source);
			ResultSet rs = prep.executeQuery();
			
			boolean exists = rs.next();
			
			if(exists){	
				String sourceExists = "SELECT * FROM forwarding WHERE source=?;";
				PreparedStatement prep2 = conn.prepareStatement(sourceExists);
				prep2.setString(1, source);
				ResultSet rs2 = prep2.executeQuery();
				boolean exists2 = rs2.next();
				if (!exists2){
					String forwardQ = "INSERT INTO forwarding(source, target) VALUES(?, ?);";
					prep = conn.prepareStatement(forwardQ);
					prep.setString(1, source);
					prep.setString(2, target);
					System.out.println(prep);
					prep.executeUpdate();
				}
				else{
					String updateQ = "UPDATE forwarding SET target=? WHERE source=?;";
					prep = conn.prepareStatement(updateQ);
					prep.setString(2, source);
					prep.setString(1, target);
					System.out.println(prep);
					prep.executeUpdate();
				}
				}
			else {
				throw new WrongUserException2("User: " + source + " does not exist.");
			}
		}
		catch (SQLException e) {
        	e.printStackTrace();
        	
		}
	}
	
	public void UnforwardUser(String source, String target) throws WrongUserException2{
		String existingUser = "SELECT * FROM users WHERE username=?;";
		
		try {
			PreparedStatement prep = conn.prepareStatement(existingUser);
			prep.setString(1, source);
			ResultSet rs = prep.executeQuery();
			
			boolean exists = rs.next();
			
			if(exists){	
					String deleteQ = "DELETE FROM forwarding WHERE source=? AND target=?;";
					prep = conn.prepareStatement(deleteQ);
					prep.setString(1, source);
					prep.setString(2, target);
					System.out.println(prep);
					prep.executeUpdate();
					}
			else {
				throw new WrongUserException2("User: " + source + " does not exist.");
			}
		}
		catch (SQLException e) {
        	e.printStackTrace();
        	
		}
	}
	
	public String getForwardingTarget(String source){
		String forwardCheck = "SELECT target FROM forwarding where source = ?";
		
		try {
			PreparedStatement prep = conn.prepareStatement(forwardCheck);
			prep.setString(1, source);
			ResultSet rs = prep.executeQuery();
			String target = null;
			if (rs.next()) {
				target = rs.getString("target");
				}
			return target;
		}
		catch (SQLException e) {
        	e.printStackTrace();	
		}
		return null;
	}
}