package gov.nist.sip.proxy;

import java.sql.Connection;

import java.text.ParseException;
import javax.sip.*;
import javax.sip.message.*;
import javax.sip.header.*;
import javax.sip.address.*;
import gov.nist.javax.sip.parser.URLParser;

public class MiddleProxy {
	//protected ForwardingServer forwardingServer;
	//protected BillingServer billingServer;
	protected BlockingServer blockingServer;
	protected User user;
	protected Database db;
	
	public MiddleProxy(){
		db=new Database();
		//forwardingServer=new ForwardingServer();
		//billingServer=new BillingServer();
		blockingServer= new BlockingServer(db);
	}
	
	public static URI getSourceUri(Request request) {
		FromHeader header = (FromHeader) request.getHeader(FromHeader.NAME);
		return header.getAddress().getURI();
	}

	public static URI getTargetUri(Request request) {
		ToHeader header = (ToHeader) request.getHeader(ToHeader.NAME);
		return header.getAddress().getURI();
	}
	
	public boolean containsChar(String s, char search) {
	    if (s.length() == 0)
	        return false;
	    else
	        return s.charAt(0) == search || containsChar(s.substring(1), search);
	}
	
	public void register(Request request) throws WrongPasswordException, NotUserException{
		String sourceUri = getSourceUri(request).toString();
		String source = sourceUri.substring(4, sourceUri.lastIndexOf("@"));
		byte[] temp = request.getRawContent();
		String password = new String(temp);		
		try {
			User user;
			if(containsChar(password,"|".charAt(0))){
				String passwd = password.substring(0, password.indexOf("|"));
				String policy = password.substring(password.indexOf("|")+1, password.lastIndexOf("|"));
				String creditCardNo = password.substring(password.lastIndexOf("|")+1);
				user = new User(source, passwd, policy, creditCardNo, db);
			}
			else
				user = new User(source, password, db);
		}
		catch(WrongPasswordException a){
			System.out.println(a.getMessage());
			throw a;
		}
	}
	
	public void block(Request request) throws WrongUserException{
		String sourceUri = getSourceUri(request).toString();
		String blocker = sourceUri.substring(4, sourceUri.lastIndexOf("@"));
		String targetUri = getTargetUri(request).toString();
		String blocked = targetUri.substring(4, targetUri.lastIndexOf("@"));
		boolean isBlocked = blockingServer.getBlock(blocker, blocked);
		try {
			blockingServer.BlockUser(blocker, blocked);
		}
		catch(WrongUserException a){
			System.out.println(a.getMessage());
			throw a;
		}
	}
	
	public void unblock(Request request) throws WrongUserException{
		String sourceUri = getSourceUri(request).toString();
		String blocker = sourceUri.substring(4, sourceUri.lastIndexOf("@"));
		String targetUri = getTargetUri(request).toString();
		String blocked = targetUri.substring(4, targetUri.lastIndexOf("@"));
		try {
			blockingServer.UnblockUser(blocker, blocked);
		}
		catch(WrongUserException a){
			System.out.println(a.getMessage());
			throw a;
		}
	}
	
	public boolean checkIfBlock(Request request) {
		String sourceUri = getSourceUri(request).toString();
		String blocked = sourceUri.substring(4, sourceUri.lastIndexOf("@"));
		String targetUri = getTargetUri(request).toString();
		String blocker = targetUri.substring(4, targetUri.lastIndexOf("@"));
		
		boolean isBlocked = blockingServer.getBlock(blocker, blocked);
		if (isBlocked) {
			return true;
		} 
		else return false;
	}
}
