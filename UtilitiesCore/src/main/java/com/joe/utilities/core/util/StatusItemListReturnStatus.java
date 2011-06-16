package com.joe.utilities.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This ReturnStatus has a list of ReturnStatusItems rather than a map so that the
 * same error code may be returned multiple times.
* A ReturnStatus can be commonly used to return the status of a function.  
* The status is composed of zero or more status items
* that contribute to the general Return Status.  Zero elements would indicate that the return status is completely OK.
* The one or more ReturnStatusItem instance may each point to a error, warning condition, or informational message 
* Errors will block the current flow of business logic.  
* Warnings will usually lead to an interactive decision by the presentation layer to treat the condition as an error or not or possibly identifiy a condition that will turn into an error later in a workflow.
* Informational status items refer to successful execution but with an advisory message included in the return. 
* 
* The ReturnStatus will determine a general status based on the presence and status of its component items.
* 
* @author Dave Ousey
* @author Scott Beebe
* 
* Creation date: 1/16/2007 9 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public class StatusItemListReturnStatus extends ReturnStatus
{
    private Collection<ReturnStatusItem> statusItems = new ArrayList<ReturnStatusItem>();

    /**
     * Simple constructor
     */
    public StatusItemListReturnStatus()
    {
    }
    
    /**
     * Constructs this ReturnStatus using another as a starting point
     * @param existingStatus
     */
    public StatusItemListReturnStatus(StatusItemListReturnStatus existingStatus)
    {
        appendStatusItems(existingStatus);
    }

    /**
     * Method getStatus. Return status derived from the state of the contained status items.
     * If any errors exist then general status is Error.  
     * Else if any warnings exist then general status is Warning
     * Else if any informational items exist then general status is Informational
     * Else Status is OK
     * @return Status
     */
    public Status getStatus()
    {
        boolean warningsExist = false;
        boolean informationalMessagesExist = false;
        for (ReturnStatusItem item : statusItems)
        {
            // If one error exists, then general status is an error
            if (!item.isSuccess())
                return Status.ERROR;

            if (item.getStatus() == Status.WARNING)
                warningsExist = true;
            else if (item.getStatus() == Status.INFORMATIONAL)
                informationalMessagesExist = true;
        }
        
        if (warningsExist)
            return Status.WARNING;
        else if (informationalMessagesExist)
            return Status.INFORMATIONAL;
        else
            return Status.OK;
    }
    
    /**
     * Method addError. Create and add an error return status item
     * 
     * @param code
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addError(String code)
    {
        return addReturnStatusItem(code, Status.ERROR, null, null);
    }

    /**
     * Method addError. 
     * @param code
     * @param description
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addError(String code, String description)
    {
        return addReturnStatusItem(code, Status.ERROR, description, null);
    }

    /**
     * Method addError. 
     * @param code
     * @param description
     * @param parameterMap
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addError(String code, String description, Map<String, String> parameterMap)
    {
        return addReturnStatusItem(code, Status.ERROR, description, parameterMap);
    }
    
    
    /**
     * Method addWarning. Create and add a warning return status item
     * 
     * @param code
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addWarning(String code)
    {
        return addReturnStatusItem(code, Status.WARNING, null, null);
    }

    /**
     * Method addWarning. 
     * @param code
     * @param description
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addWarning(String code, String description)
    {
        return addReturnStatusItem(code, Status.WARNING, description, null);
    }

    /**
     * Adds / Replaces the current warning based on code.
     * This assumes that this code/description is the most accurate.  If an error/information message was
     * added with the same code then this method will replace that message with the new warning.
     * @param code Unique Code to identify the warning
     * @param description The description associated with the code.
     * @return ReturnStatusItem The latest return status item.
     */
    public ReturnStatusItem addReplaceUniqueWarning(String code, String description)
    {
    	if (hasItemWithCode(code)) {
			clearItemWithCode(code);
		}
    	return addWarning(code,description);
    }

    /**
     * Method addWarning. 
     * @param code
     * @param description
     * @param parameterMap
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addWarning(String code, String description, Map<String, String> parameterMap)
    {
        return addReturnStatusItem(code, Status.WARNING, description, parameterMap);
    }
    
    /**
     * Method addReturnStatusItem. 
     * @param code
     * @param description
     * @param parameterMap
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addReturnStatusItem(String code, Status status, String description, Map<String, String> parameterMap)
    {
        ReturnStatusItem item = new ReturnStatusItem(code, status, description, parameterMap);
        this.statusItems.add(item);
        return item;
    }

    /**
     * Method appendStatusItems.
     * 
     * @param returnStatus
     * @return void
     */
    public void appendStatusItems(StatusItemListReturnStatus returnStatus)
    {
        statusItems.addAll(returnStatus.statusItems);
    }

    /**
     * Method appendStatusItems.
     * 
     * @param returnStatus
     * @return void
     */
    public void appendStatusItems(ReturnStatus returnStatus)
    {
    	ReturnStatusItem[] statusItemArray = returnStatus.getResultStatusItems();
    	for (int i=0; i<returnStatus.getResultStatusItems().length; i++)
    	{
            statusItems.add(statusItemArray[i]);
    	}
    }

    public void appendStatusItems(Collection<ReturnStatusItem> items) {

    	if ( items == null || items.size() == 0){
       		return;
       	}
        for(ReturnStatusItem item: items){
        	addReturnStatusItem(item.getCode(), item.getStatus(), item.getDefaultMessage(), item.getParameterMap());
        }
    }

    /**
     * Method hasErrors. 
     * @return boolean
     */
    public boolean hasErrors()
    {
        for (ReturnStatusItem item : statusItems)
        {
            if (item.getStatus() == Status.ERROR)
                return true;
        }

        return false;
    }
    
    /**
     * Method hasErrorsOrWarnings. 
     * @return boolean
     */
    public boolean hasErrorsOrWarnings()
    {
        for (ReturnStatusItem item : statusItems)
        {
            if (item.getStatus() == Status.ERROR || item.getStatus() == Status.WARNING)
                return true;
        }

        return false;        
    }
    
    /**
     * Method hasWarnings. 
     * @return boolean
     */
    public boolean hasWarnings()
    {
        for (ReturnStatusItem item : statusItems)
        {
            if (item.getStatus() == Status.WARNING)
                return true;
        }

        return false;        
    }
    
    /**
     * Method isSuccess. If any status items are not successful, then return false; else true
     * @return boolean
     */
    public boolean isSuccess()
    {

        for (ReturnStatusItem item : statusItems)
        {
            if (!item.isSuccess())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Method getResultStatusItems. Return the items 
     * @return ReturnStatusItem[]
     */
    public ReturnStatusItem[] getResultStatusItems()
    {
        ReturnStatusItem[] returnValue = new ReturnStatusItem[statusItems.size()];
        returnValue = this.statusItems.toArray(returnValue);
        return returnValue;
    }
    
    /**
     * Method getErrorResultStatusItems. Get errors 
     * @return ReturnStatusItem[]
     */
    public ReturnStatusItem[] getErrorResultStatusItems()
    {
        return getResultStatusItemsWithStatus(Status.ERROR);
    }

    /**
     * Method getWarningResultStatusItems. Get errors 
     * @return ReturnStatusItem[]
     */
    public ReturnStatusItem[] getWarningResultStatusItems()
    {
        return getResultStatusItemsWithStatus(Status.WARNING);
    }

    /**
     * Method getInformationalResultStatusItems. Get errors 
     * @return ReturnStatusItem[]
     */
    public ReturnStatusItem[] getInformationalResultStatusItems()
    {
        return getResultStatusItemsWithStatus(Status.INFORMATIONAL);
    }
    
    /**
     * Method getStatusItemCode. 
     * @return Set<String>
     */
    public Set<String> getStatusItemCodes()
    {
    	Set<String> statusItemCodes = new HashSet<String>();
    	for (ReturnStatusItem statusItem : statusItems)
    	{
    		statusItemCodes.add(statusItem.getCode());
    	}
    	return statusItemCodes;
    }
    
    /**
     * Method hasItemWithCode. Determines if given code exists in the set 
     * @param code
     * @return boolean
     */
    public boolean hasItemWithCode(String code)
    {
        return getStatusItemCodes().contains(code);
    }

    /**
     * Method hasAnyItemsInCodeSet. 
     * @param codes
     * @return boolean
     */
    public boolean hasAnyItemsInCodeSet(Set<String> codes)
    {
    	if (codes == null)
            return false;
    	Set<String> statusItemCodes = getStatusItemCodes();
        for (String code: getStatusItemCodes())
        {
            if (statusItemCodes.contains(code))
                return true;
        }
        
        return false;
    }
    
    /**
     * Method clearItemWithCode. Deletes the item with the given code 
     * @param code 
     * @return void
     */
    public void clearItemWithCode(String code)
    {
        List<ReturnStatusItem> targetItems = new ArrayList<ReturnStatusItem>(statusItems.size());
    	for (ReturnStatusItem statusItem : statusItems)
    	{
    		if (statusItem.getCode().equals(code))
    			targetItems.add(statusItem);
    	}
   		statusItems.removeAll(targetItems);
    }
    
    /**
     * Method getResultStatusItemsWithStatus. Return the items 
     * @return ReturnStatusItem[]
     */
    private ReturnStatusItem[] getResultStatusItemsWithStatus(Status status)
    {
        ReturnStatusItem[] returnValue = new ReturnStatusItem[statusItems.size()];
        int index = 0;
        for (ReturnStatusItem item : statusItems)
        {
            if (item.getStatus() == status)
                returnValue[index++] = item;
        }
        return returnValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer returnValue = new StringBuffer(this.getClass().getName());
        returnValue.append("(");
        for (ReturnStatusItem item : statusItems)
        {
            returnValue.append(item);
            returnValue.append(",");
        }
        returnValue.append(")");
        return returnValue.toString();
    }

    /**
     * Method createErrorReturnStatus. Convenience method to create
     * 
     * @param errorCode
     * @param defaultMessage
     * @return ReturnStatus
     */
    public static StatusItemListReturnStatus createErrorReturnStatus(String errorCode, String defaultMessage)
    {
        StatusItemListReturnStatus returnStatus = new StatusItemListReturnStatus();
        returnStatus.addError(errorCode, defaultMessage);
        return returnStatus;
    }
    
    public Collection<ReturnStatusItem> getStatusItems(){
    	return statusItems;
    }
}
