package com.joe.utilities.core.jsf.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class FacesMessagesMultiPageSupport implements PhaseListener {

	private static final long serialVersionUID = 1250469273857785274L;
    private static final String sessionToken = "MULTI_PAGE_MESSAGES_SUPPORT";
 
    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;
    }
 
    /*
     * Check to see if we are "naturally" in the RENDER_RESPONSE phase. If we
     * have arrived here and the response is already complete, then the page is
     * not going to show up: don't display messages yet.
     */
    public void beforePhase(final PhaseEvent event)
    {
        FacesContext facesContext = event.getFacesContext();
        this.saveMessages(facesContext);
 
        if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
        {
            if (!facesContext.getResponseComplete())
            {
                this.restoreMessages(facesContext);
            }
        }
    }
 
    /*
     * Save messages into the session after every phase.
     */
    public void afterPhase(final PhaseEvent event)
    {
        if (!PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
        {
            FacesContext facesContext = event.getFacesContext();
            this.saveMessages(facesContext);
        }
    }
 
    @SuppressWarnings("unchecked")
    private int saveMessages(final FacesContext facesContext)
    {
        List<FacesMessage> newMessageList = new ArrayList<FacesMessage>();
        for (Iterator<FacesMessage> iter = facesContext.getMessages(null); iter.hasNext();)
        {
        	newMessageList.add(iter.next());
            iter.remove();
        }
 
        if (newMessageList.size() == 0)
        {
            return 0;
        }
 
        Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
        List<FacesMessage> existingMessages = (List<FacesMessage>) sessionMap.get(sessionToken);
        if (existingMessages != null)
        {
        	// only add messages that don't already exist
        	for (FacesMessage facesMessage : newMessageList) {
				if (!existingMessages.contains(facesMessage)) {
					existingMessages.add(facesMessage);
				}
			}
        }
        else
        {
            sessionMap.put(sessionToken, newMessageList);
        }
        return newMessageList.size();
    }
 
    @SuppressWarnings("unchecked")
    private int restoreMessages(final FacesContext facesContext)
    {
        Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
        List<FacesMessage> messagesFromSession = (List<FacesMessage>) sessionMap.remove(sessionToken);
 
        if (messagesFromSession == null)
        {
            return 0;
        }
 
        int restoredCount = messagesFromSession.size();
        // loop through messages on session and only add message back to context if not present
        for (FacesMessage facesMessage : messagesFromSession) {
        	boolean found = false;
			for(Iterator<FacesMessage> iter = facesContext.getMessages(null); iter.hasNext();) {
				if (iter.next().equals(facesMessage)) {
					found=true;
					break;
				}
			}
			if (!found) {
				facesContext.addMessage(null, facesMessage);
			}
		}
        
        // update the messages managed bean with all faces messages
        //TODO
        //MessagesSupportView msv = (MessagesSupportView)ManagedBeanUtility.getBindingObject("#{MessagesSupportBean}");
        //msv.updateFacesMessages(facesContext.getMessages());   
        
        return restoredCount;
    }

}
