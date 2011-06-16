package com.joe.utilities.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A ReturnStatus can be commonly used to return the status of a function. The status is composed of zero or more status items that
 * contribute to the general Return Status. Zero elements would indicate that the return status is completely OK. The one or more
 * ReturnStatusItem instance may each point to a error, warning condition, or informational message Errors will block the current flow of
 * business logic. Warnings will usually lead to an interactive decision by the presentation layer to treat the condition as an error or not
 * or possibly identify a condition that will turn into an error later in a workflow. Informational status items refer to successful
 * execution but with an advisory message included in the return. The ReturnStatus will determine a general status based on the presence and
 * status of its component items.
 * 
 * @author Dave Ousey Creation date: 1/16/2007 9 AM Copyright (c) 2007 MEDecision, Inc. All rights reserved.
 */
public class ReturnStatus {

    public enum Status {
        OK, WARNING, ERROR, INFORMATIONAL, UNKNOWN
    };

    /**
     * Contains map from unique status item code to the status item. This allows status items to be added and removed more easily based on
     * their unique code.
     */
    private TreeMap<String, ReturnStatusItem[]> statusItems = new TreeMap<String, ReturnStatusItem[]>();

    /**
     * Simple constructor
     */
    public ReturnStatus() {}

    /**
     * Constructs this ReturnStatus using another as a starting point
     * 
     * @param existingStatus
     */
    public ReturnStatus(ReturnStatus existingStatus) {
        appendStatusItems(existingStatus);
    }

    /**
     * Method getStatus. Return status derived from the state of the contained status items. If any errors exist then general status is
     * Error. Else if any warnings exist then general status is Warning Else if any informational items exist then general status is
     * Informational Else Status is OK
     * 
     * @return Status
     */
    public Status getStatus() {
        boolean warningsExist = false;
        boolean informationalMessagesExist = false;
        for (ReturnStatusItem[] items : statusItems.values()) {
            for (ReturnStatusItem item : items) {
                // If one error exists, then general status is an error
                if (!item.isSuccess()) {
                    return Status.ERROR;
                }
                if (item.getStatus() == Status.WARNING) {
                    warningsExist = true;
                } else if (item.getStatus() == Status.INFORMATIONAL)
                    informationalMessagesExist = true;
            }
        }

        if (warningsExist) {
            return Status.WARNING;
        } else if (informationalMessagesExist) {
            return Status.INFORMATIONAL;
        } else
            return Status.OK;
    }

    /**
     * Method addError. Create and add an error return status item
     * 
     * @param code
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addError(String code) {
        return addReturnStatusItem(code, Status.ERROR, null, null);
    }

    /**
     * Method addError.
     * 
     * @param code
     * @param description
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addError(String code, String description) {
        return addReturnStatusItem(code, Status.ERROR, description, null);
    }

    /**
     * Method addError.
     * 
     * @param code
     * @param description
     * @param parameterMap
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addError(String code, String description, Map<String, String> parameterMap) {
        return addReturnStatusItem(code, Status.ERROR, description, parameterMap);
    }

    /**
     * Method addWarning. Create and add a warning return status item
     * 
     * @param code
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addWarning(String code) {
        return addReturnStatusItem(code, Status.WARNING, null, null);
    }

    /**
     * Method addWarning.
     * 
     * @param code
     * @param description
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addWarning(String code, String description) {
        return addReturnStatusItem(code, Status.WARNING, description, null);
    }

    /**
     * Adds / Replaces the current warning based on code. This assumes that this code/description is the most accurate. If an
     * error/information message was added with the same code then this method will replace that message with the new warning.
     * 
     * @param code Unique Code to identify the warning
     * @param description The description associated with the code.
     * @return ReturnStatusItem The latest return status item.
     */
    public ReturnStatusItem addReplaceUniqueWarning(String code, String description) {
        if (hasItemWithCode(code)) {
            clearItemWithCode(code);
        }
        return addWarning(code, description);
    }

    /**
     * Method addWarning.
     * 
     * @param code
     * @param description
     * @param parameterMap
     * @return ReturnStatusItem
     */
    public ReturnStatusItem addWarning(String code, String description, Map<String, String> parameterMap) {
        return addReturnStatusItem(code, Status.WARNING, description, parameterMap);
    }

    /**
     * Adds the <code>ReturnStatusItem</code> given by the parameters to the map of statusItems. If the status code already exists in the
     * map, it adds the newly created <code>ReturnStatusItem</code> to the <code>ReturnStatusItem[]</code>. This allows the addition of
     * multiple status messages for one error code.
     * 
     * @param code <code>String</code> error code.
     * @param description <code>String</code> error description.
     * @param parameterMap <code>Map</string> (<code>String, String</code>) containing the parameters to inject into the status message.
     * @return ReturnStatusItem the <code>ReturnStatusItem</code> given by the parameters.
     */
    public ReturnStatusItem addReturnStatusItem(String code, Status status, String description, Map<String, String> parameterMap) {
        ReturnStatusItem item = new ReturnStatusItem(code, status, description, parameterMap);

        addReturnStatusItemInternal(code, item);

        return item;
    }

	/**
     * Adds the <code>ReturnStatusItem</code> given by the parameters to the map of statusItems. If the status code already exists in the
     * map, it adds the newly created <code>ReturnStatusItem</code> to the <code>ReturnStatusItem[]</code>. This allows the addition of
     * multiple status messages for one error code.
     * 
     * @param code <code>String</code> error code.
     * @param description <code>String</code> error description.
     * @param parameterMap <code>Map</string> (<code>String, String</code>) containing the parameters to inject into the status message.
     * @return ReturnStatusItem the <code>ReturnStatusItem</code> given by the parameters.
     */
    public ReturnStatusItem addReturnStatusItem(String code, Status status, String description, Map<String, String> parameterMap, String ruleResult) {
        ReturnStatusItem item = new ReturnStatusItem(code, status, description, parameterMap);

        addReturnStatusItemInternal(code, item);

        return item;
    }

    /**
     * Internal little method that is used by {@link #addReturnStatusItem(String, Status, String, Map)} and {@link #addReturnStatusItem(String, Status, String, Map, String)}
	 * @param code
	 * @param item
	 */
	private void addReturnStatusItemInternal(String code, ReturnStatusItem item) {
		if (statusItems.get(code) != null) {
	        ReturnStatusItem[] items = statusItems.get(code);
	        ReturnStatusItem[] newItems = new ReturnStatusItem[items.length + 1];
	
	        System.arraycopy(items, 0, newItems, 0, items.length);
	        newItems[newItems.length - 1] = item;
	        statusItems.put(code, newItems);
	    } else {
	        statusItems.put(code, new ReturnStatusItem[] { item });
	    }
	}

	/**
     * Takes the list of errors/warnings/messages/information associated with the return status object and adds those to the current status
     * item. The values in the input list take priority and will overwrite the values in the internal class list.
     * 
     * @param returnStatus the list of errors/warnings/messages/information to be placed in the current list.
     */
    public void appendStatusItems(ReturnStatus returnStatus) {
    	
    	if ( returnStatus == null || 
       		 returnStatus.statusItems == null 
       	){
       		return;
       	}
           statusItems.putAll(returnStatus.statusItems);

    }

    /**
     * Takes the list of errors/warnings/messages/information associated with the return status object and adds those to the current status
     * item. If an key for a code is in both lists then the return status items will be merged. The final list will contain return status
     * items from the original and the input list.
     * 
     * @param returnStatus the list of errors/warnings/messages/information to be placed in the current list.
     */
    public void mergeStatusItems(ReturnStatus returnStatus) {
        if (returnStatus == null) {
            return;
        }
        for (String key : returnStatus.getStatusItemCodes()) {
            if (!(this.statusItems.containsKey(key))) {
                this.statusItems.put(key, returnStatus.getReturnStatusItemsForCode(key));
            } else {
                Set<ReturnStatusItem> thisStatusItems = setFromArray(this.getReturnStatusItemsForCode(key));
                Set<ReturnStatusItem> paramStatusItems = setFromArray(returnStatus.getReturnStatusItemsForCode(key));

                thisStatusItems.addAll(paramStatusItems);
                this.statusItems.put(key, thisStatusItems.toArray(new ReturnStatusItem[thisStatusItems.size()]));
            }
        }
    }

    private Set<ReturnStatusItem> setFromArray(ReturnStatusItem[] items) {
        Set<ReturnStatusItem> set = new TreeSet<ReturnStatusItem>();
        for (ReturnStatusItem item : items) {
            set.add(item);
        }
        return set;
    }

    /**
     * /** Method hasErrors.
     * 
     * @return boolean
     */
    public boolean hasErrors() {
        for (ReturnStatusItem item : this.getResultStatusItems()) {
            if (item.getStatus() == Status.ERROR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method hasErrorsOrWarnings.
     * 
     * @return boolean
     */
    public boolean hasErrorsOrWarnings() {
        for (ReturnStatusItem item : this.getResultStatusItems()) {
            if (item.getStatus() == Status.ERROR || item.getStatus() == Status.WARNING) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method hasWarnings.
     * 
     * @return boolean
     */
    public boolean hasWarnings() {
        for (ReturnStatusItem item : this.getResultStatusItems()) {
            if (item.getStatus() == Status.WARNING) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method isSuccess. If any status items are not successful, then return false; else true
     * 
     * @return boolean
     */
    public boolean isSuccess() {
        for (ReturnStatusItem item : this.getResultStatusItems()) {
            if (!item.isSuccess()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method getResultStatusItems. Return the items
     * 
     * @return ReturnStatusItem[]
     */
    public ReturnStatusItem[] getResultStatusItems() {
        List<ReturnStatusItem> itemList = new ArrayList<ReturnStatusItem>();

        for (ReturnStatusItem[] items : statusItems.values()) {
            for (ReturnStatusItem item : items) {
                itemList.add(item);
            }
        }
        return itemList.toArray(new ReturnStatusItem[itemList.size()]);
    }

    /**
     * Method getErrorResultStatusItems. Get errors
     * 
     * @return ReturnStatusItem[]
     */
    public ReturnStatusItem[] getErrorResultStatusItems() {
        return getResultStatusItemsWithStatus(Status.ERROR);
    }

    /**
     * Method getWarningResultStatusItems. Get errors
     * 
     * @return ReturnStatusItem[]
     */
    public ReturnStatusItem[] getWarningResultStatusItems() {
        return getResultStatusItemsWithStatus(Status.WARNING);
    }

    /**
     * Method getInformationalResultStatusItems. Get errors
     * 
     * @return ReturnStatusItem[]
     */
    public ReturnStatusItem[] getInformationalResultStatusItems() {
        return getResultStatusItemsWithStatus(Status.INFORMATIONAL);
    }

    /**
     * Method getStatusItemCode.
     * 
     * @return Set<String>
     */
    public Set<String> getStatusItemCodes() {
        return Collections.unmodifiableSet(statusItems.keySet());
    }

    /**
     * Method hasItemWithCode. Determines if given code exists in the set.
     * 
     * @param code
     * @return boolean
     */
    public boolean hasItemWithCode(String code) {
        return statusItems.containsKey(code);
    }

    /**
     * Method hasAnyItemsInCodeSet.
     * 
     * @param codes
     * @return boolean
     */
    public boolean hasAnyItemsInCodeSet(Set<String> codes) {
        if (codes == null) {
            return false;
        }

        for (String code : codes) {
            if (statusItems.keySet().contains(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the array of <code>ReturnStatusItem</code>'s associated with the given status code.
     * 
     * @param code status code used to retrieve the status items with.
     * @return <code>ReturnStatusItem[]</code> that corresponds to the given status code.
     */
    public ReturnStatusItem[] getReturnStatusItemsForCode(String code) {
        return this.statusItems.get(code);
    }

    /**
     * Method clearItemWithCode. Deletes the item with the given code
     * 
     * @param code
     * @return void
     */
    public void clearItemWithCode(String code) {
        statusItems.remove(code);
    }

    /**
     * Method getResultStatusItemsWithStatus. Return the items
     * 
     * @return ReturnStatusItem[]
     */
    private ReturnStatusItem[] getResultStatusItemsWithStatus(Status status) {
        List<ReturnStatusItem> itemList = new ArrayList<ReturnStatusItem>();

        for (ReturnStatusItem item : this.getResultStatusItems()) {
            if (item.getStatus() == status) {
                itemList.add(item);
            }
        }
        return itemList.toArray(new ReturnStatusItem[itemList.size()]);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer returnValue = new StringBuffer(this.getClass().getName());
        returnValue.append("(");
        for (ReturnStatusItem[] items : statusItems.values()) {
            for (ReturnStatusItem item : items) {
                returnValue.append(item);
                returnValue.append(",");
            }
            returnValue.append(";");
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
    public static ReturnStatus createErrorReturnStatus(String errorCode, String defaultMessage) {
        ReturnStatus returnStatus = new ReturnStatus();
        returnStatus.addError(errorCode, defaultMessage);
        return returnStatus;
    }
    public void appendStatusItems(Collection<ReturnStatusItem> items) {
    	
    	if ( items == null || items.size() == 0){
       		return;
       	}
        for(ReturnStatusItem item: items){
        	addReturnStatusItem(item.getCode(), item.getStatus(), item.getDefaultMessage(), item.getParameterMap());
        }
    }
}
