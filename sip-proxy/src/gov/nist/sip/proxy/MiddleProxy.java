package gov.nist.sip.proxy;

import java.sql.Connection;

import javax.sip.*;
import javax.sip.message.*;
import javax.sip.header.*;
import javax.sip.address.*;
import gov.nist.javax.sip.parser.URLParser;

public class MiddleProxy {
	//protected ForwardingServer forwardingServer;
	//protected BillingServer billingServer;
	//protected BlockingServer blockingServer;
	protected User user;
	protected Database db;
	
	public MiddleProxy(){
		db=new Database();
		//forwardingServer=new ForwardingServer();
		//billingServer=new BillingServer();
		//blockingServer=new BlockingServer();
	}
	
	public static URI getSourceUri(Request request) {
		FromHeader header = (FromHeader) request.getHeader(FromHeader.NAME);
		return header.getAddress().getURI();
	}

	public static URI getTargetUri(Request request) {
		ToHeader header = (ToHeader) request.getHeader(ToHeader.NAME);
		return header.getAddress().getURI();
	}
	
	public void register(Request request) throws WrongPasswordException{
		String sourceUri = getSourceUri(request).toString();
		String source = sourceUri.substring(4, sourceUri.lastIndexOf("@"));
		byte[] temp = request.getRawContent();
		String password = new String(temp);
		try {
			User user = new User(source, password, db);
		}
		catch(WrongPasswordException a){
			System.out.println(a.getMessage());
			throw a;
		}
	}
	
}
