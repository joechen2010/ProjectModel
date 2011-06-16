package com.joe.utilites.core.session;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;




public class SessionListener implements HttpSessionBindingListener 
{
	public static final String NAME ="AlineoSessionListener";	
	private String sessionID;
	
	/** Constructor used to create Listener */
	public SessionListener() {
	}

	public void valueBound(HttpSessionBindingEvent arg0) {
		sessionID = SessionController.getSessionID();
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		/*// Delete all lock table entries for this session
		LockFacade lockFacade =(LockFacade) ServiceLocator.getInstance().getBean("lockFacade");
		lockFacade.deleteSessionLocks(sessionID);
		// Delete member edit status record for this session.
		MemberFacade memberFacade =(MemberFacade) ServiceLocator.getInstance().getBean("memberFacade");
		memberFacade.removeMemberEditStatusBySessionId(sessionID);*/
	}
}
