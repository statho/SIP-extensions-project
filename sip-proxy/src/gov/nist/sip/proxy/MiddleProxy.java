package gov.nist.sip.proxy;

import java.sql.Connection;

import java.text.ParseException;
import javax.sip.*;
import javax.sip.message.*;
import javax.sip.header.*;
import javax.sip.address.*;
import gov.nist.javax.sip.parser.URLParser;

public class MiddleProxy {
	protected ForwardingServer forwardingServer;
	//protected BillingServer billingServer;
	protected BlockingServer blockingServer;
	protected User user;
	protected Database db;
	
	public MiddleProxy(){
		db=new Database();
		forwardingServer=new ForwardingServer(db);
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
		catch(NotUserException a){
			System.out.println(a.getMessage());
			throw a;
		}
	}
	public void forward(Request request) throws WrongUserException2{
		String sourceUri = getSourceUri(request).toString();
		String source = sourceUri.substring(4, sourceUri.lastIndexOf("@"));
		byte[] temp = request.getRawContent();
		String target = new String(temp);		
		//String getForwardingTarget = forwardingServer.getForwardingTarget(source);
		try {
			forwardingServer.ForwardUser(source, target);
		}
		catch(WrongUserException2 a){
			System.out.println(a.getMessage());
			throw a;
		}
	}
	
	
	public void block(Request request) throws WrongUserException{
		String sourceUri = getSourceUri(request).toString();
		String blocker = sourceUri.substring(4, sourceUri.lastIndexOf("@"));
		byte[] temp = request.getRawContent();
		String temp2 = new String(temp);	
		String blocked = temp2.substring(0, temp2.indexOf("|"));
		String whatDo = temp2.substring(temp2.indexOf("|")+1);

		if (whatDo.equals("1")) {
			try {
				blockingServer.BlockUser(blocker, blocked);
			}
			catch(WrongUserException a){
				System.out.println(a.getMessage());
				throw a;
			}
		}
		else {
			try {
				blockingServer.UnblockUser(blocker, blocked);
			}
			catch(WrongUserException a){
				System.out.println(a.getMessage());
				throw a;
			}
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
	
	public String findWhereIsForwarded(Request request) {
		String sourceUri = getSourceUri(request).toString();
		String target = sourceUri.substring(4, sourceUri.lastIndexOf("@"));
		String targetUri = getTargetUri(request).toString();
		String source = targetUri.substring(4, targetUri.lastIndexOf("@"));
		
		String forwardTarget = forwardingServer.getForwardingTarget(source);
		return forwardTarget;
	}
	
	

			private String getUsernameFromHeader(ToHeader header) {
				URI uri = header.getAddress().getURI();
				String uriString = uri.toString();
				return uriString.substring(uriString.indexOf("sip:") + 4,
						uriString.indexOf("@"));
			}
		 
				
			public Request checkAndSetForwarding(Request request, Proxy proxy) {

				ToHeader header = (ToHeader) request.getHeader(ToHeader.NAME);
//				String oldToUser = getUsernameFromHeader(header);
//				String toUser = dbManager.getForward(oldToUser);
				String toUser = this.findWhereIsForwarded(request);
				if (toUser != null) {
					String originalUri = header.getAddress().toString();
					String newUri = "sip:" + toUser
							+ originalUri.substring(originalUri.indexOf("@"));
					URI newURI;
					try {
						newURI = proxy.getAddressFactory().createURI(newUri);
						ToHeader newTo = proxy.getHeaderFactory().createToHeader(
								proxy.getAddressFactory().createAddress(newURI), null);
						Request newreq = (Request) request.clone();
						newreq.setHeader(newTo);
						return newreq;

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else
					return request;
				
				return null;
			}
}
