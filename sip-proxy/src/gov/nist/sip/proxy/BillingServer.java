package gov.nist.sip.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BillingServer {

	protected Connection conn;
	
	public BillingServer(Database db) {
		conn = db.Connect();
	}
	
	
	public void charge(String caller, float elapsedTime) {
		
		String existingUser = "SELECT * FROM users WHERE username=?;";
		
		try {
			
			PreparedStatement prep = conn.prepareStatement(existingUser);
			prep.setString(1, caller);
			ResultSet rs = prep.executeQuery();
			
			boolean exists = rs.next();
			
			if(exists){	
				
				String policy = rs.getString("policy");
				float charged = rs.getFloat("charge");
				System.out.println(policy);
				System.out.println(charged);
				float newCharge;
				
				if(policy.equals("premium")){							// charing policy for premium users
					newCharge = (float) 0.05*elapsedTime + charged;
				}
				else if(policy.equals("student")){						// charging policy for students
					newCharge = (float) 0.08*elapsedTime + charged;
				}
				else{													// default charging policy
					newCharge = (float) 0.20*elapsedTime + charged;
				}
				
				String chargeQ = "UPDATE users SET charge=? WHERE username=?";
				prep = conn.prepareStatement(chargeQ);
				prep.setFloat(1, newCharge);							//charge in minutes
				prep.setString(2, caller);
				System.out.println(prep);
				prep.executeUpdate();
				System.out.println(newCharge);
			}
			else
				return;
			
		}
		catch (SQLException e) {
        	e.printStackTrace();
		}
	
	}
	
	public void begin(String caller) {
		
		String existingUser = "SELECT * FROM users WHERE username=?;";
		
		try {
			PreparedStatement prep = conn.prepareStatement(existingUser);
			prep.setString(1, caller);
			ResultSet rs = prep.executeQuery();
			
			boolean exists = rs.next();
			
			if(exists){	
				
				String billingQ = "INSERT INTO billing(caller, start_time) VALUES(?, ?);";
				prep = conn.prepareStatement(billingQ);
				prep.setString(1, caller);
				prep.setLong(2, System.currentTimeMillis());			//starting time in mseconds
				System.out.println(prep);
				prep.executeUpdate();
				
			}
			else 
				return;
		}
		catch (SQLException e) {
        	e.printStackTrace();
		}
		
	}
	
	public void end(String caller) {
		
		String existingUser = "SELECT * FROM billing WHERE caller=?;";
		
		
		try{
			PreparedStatement prep = conn.prepareStatement(existingUser);
			prep.setString(1, caller);
			ResultSet rs = prep.executeQuery();
			
			boolean exists = rs.next();
			
			if(exists){	
				
				long startTime = rs.getLong("start_time");
				float elapsedTime = (float) (System.currentTimeMillis() - startTime) / 1000;     //elapsed time in seconds
				
				String billingQ = "DELETE FROM billing WHERE caller=?";
				prep = conn.prepareStatement(billingQ);
				prep.setString(1, caller);			//starting time in mseconds
				System.out.println(prep);
				prep.executeUpdate();
				System.out.println(elapsedTime);
				charge(caller, elapsedTime);
			}
			else
				return;
		}
		catch (SQLException e) {
        	e.printStackTrace();
		}
	}
	
}
