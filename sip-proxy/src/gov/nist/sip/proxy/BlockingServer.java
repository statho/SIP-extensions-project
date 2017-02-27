package gov.nist.sip.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.UnsupportedOperationException;

class WrongUserException extends Exception {
    public WrongUserException(String message) {
        super(message);
    }
}

public class BlockingServer {
	
	protected Connection conn;
	
	public BlockingServer(Database db) {
		conn = db.Connect();
	}
	
	public void BlockUser(String blocker, String blocked) throws WrongUserException{
		String existingUser = "SELECT * FROM users WHERE username=?;";
		
		try {
			PreparedStatement prep = conn.prepareStatement(existingUser);
			prep.setString(1, blocked);
			ResultSet rs = prep.executeQuery();
			
			boolean exists = rs.next();
			
			if(exists){	//! if already blocked check 
				String blockQ = "INSERT INTO blocking(blocker, blocked) VALUES(?, ?);";
				prep = conn.prepareStatement(blockQ);
				prep.setString(1, blocker);
				prep.setString(2, blocked);
				System.out.println(prep);
				prep.executeUpdate();
			}
			else {
				throw new WrongUserException("User: " + blocked + " does not exist.");
			}
		}
		catch (SQLException e) {
        	e.printStackTrace();
        	
		}
	}
	
	public void UnblockUser(String blocker, String blocked) throws WrongUserException{
		String existingUser = "SELECT * FROM blocking WHERE blocker=? AND blocked=?;";
		
		try {
			PreparedStatement prep = conn.prepareStatement(existingUser);
			prep.setString(1, blocker);
			prep.setString(2, blocked);
			ResultSet rs = prep.executeQuery();
			
			boolean exists = rs.next();
			
			if(exists){	//! if already unblocked check 
				String unblockQ = "DELETE FROM blocking WHERE blocker=? AND blocked=?";
				prep = conn.prepareStatement(unblockQ);
				prep.setString(1, blocker);
				prep.setString(2, blocked);
				System.out.println(prep);
				prep.executeUpdate();
			}
			else {
				throw new WrongUserException("User: " + blocked + " is not blocked.");
			}
		}
		catch (SQLException e) {
        	e.printStackTrace();	
		}
	}
	
	public boolean getBlock(String blocker, String blocked){
		String blockCheck = "SELECT blocked FROM blocking where blocker = ? AND blocked = ?";
		
		try {
			PreparedStatement prep = conn.prepareStatement(blockCheck);
			prep.setString(1, blocker);
			prep.setString(2, blocked);
			ResultSet rs = prep.executeQuery();
			
			if (rs.next()) {
				if (blocked.contentEquals(rs.getString("blocked"))){
					return true;
				}
			} else return false;
		}
		catch (SQLException e) {
        	e.printStackTrace();	
		}
		return false;
	}
}