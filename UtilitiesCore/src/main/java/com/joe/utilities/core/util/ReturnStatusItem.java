package com.joe.utilities.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A ReturnStatusItem reports on the status of one or more status items contained by a ResultStatus object
 * 
 * @author Dave Ousey Creation date: 1/5/2007 9 AM Copyright (c) 2007 MEDecision, Inc. All rights reserved.
 */
public class ReturnStatusItem implements Comparable<ReturnStatusItem> {

    /** Enum value of status: ERROR, WARNING, OK */
    private ReturnStatus.Status status;

    /** Default message description */
    private String defaultMessage;

    /** Unique code identifier for status item. This is the unique business code ID for the message */
    private String code;

    /** Parameter map. For messages that contain parameterized values, this map will hold the values keyed by parameter ID. */
    private Map<String, String> parameterMap = new HashMap<String, String>();
    

    /**
     * @param code
     * @param status
     */
    public ReturnStatusItem(String code, ReturnStatus.Status status) {
        this(code, status, null, null);
    }

    /**
     * @param code
     * @param status
     * @param defaultMessage
     */
    public ReturnStatusItem(String code, ReturnStatus.Status status, String defaultMessage) {
        this(code, status, defaultMessage, null);
    }

    /**
     * @param code
     * @param status
     * @param defaultMessage
     * @param parameterMap
     */
    public ReturnStatusItem(String code, ReturnStatus.Status status, String defaultMessage, Map<String, String> parameterMap) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
        if (parameterMap != null)
            this.parameterMap.putAll(parameterMap);
    }

    /**
     * Method getCode.
     * 
     * @return String
     */
    public String getCode() {
        return code;
    }

    /**
     * Method getDefaultMessage.
     * 
     * @return String
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Method getParameterMap.
     * 
     * @return Map<String,String>
     */
    public Map<String, String> getParameterMap() {
        return parameterMap;
    }

    /**
     * Method getStatus.
     * 
     * @return ReturnStatus.Status
     */
    public ReturnStatus.Status getStatus() {
        return status;
    }

    /**
     * Method isError.
     * 
     * @return boolean
     */
    public boolean isError() {
        return status == ReturnStatus.Status.ERROR;
    }

    /**
     * Method isSuccess.
     * 
     * @return boolean
     */
    public boolean isSuccess() {
        return status != ReturnStatus.Status.ERROR;
    }

    /**
     * Method createParameterMap. Utility method to create a parameter map with one initial entry. Typically this is common for invalid
     * value evaluations in which the invalid value will be the parameter value.
     * 
     * @param parameterID
     * @param parameterValue
     * @return Map<String,String>
     */
    public static Map<String, String> createParameterMap(String parameterID, String parameterValue) {
        Map<String, String> parameterMap = new HashMap<String, String>(4);
        parameterMap.put(parameterID, parameterValue);
        return parameterMap;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("code", code).append("status", status).append("message", defaultMessage).toString();
    }

    /** @see java.lang.Object#equals(Object) */
    @Override
    public boolean equals(Object o) {
        if (this == null) {
            return true;
        }
        if (o == null || !(o instanceof ReturnStatusItem)) {
            return false;
        }

        ReturnStatusItem test = (ReturnStatusItem) o;
        java.util.Set<String> thisParamKeys = null;
        java.util.Set<String> testParamKeys = null;
        String thisValue = null;
        String testValue = null;


        // Not equal: Either test or this object has null values and the other object does not
        if ((this.code == null && test.code != null) || (this.code != null && test.code == null)
                || (this.defaultMessage == null && test.defaultMessage != null)
                || (this.defaultMessage != null && test.defaultMessage == null) || (this.status == null && test.status != null)
                || (this.status != null && test.status == null) || (this.parameterMap == null && test.parameterMap != null)
                || (this.parameterMap != null && test.parameterMap == null)) {
            return false;
        }

        // Not equal: Code, Default message or status are not the save value for either test or this object.
        if ((this.code != null && !this.code.equals(test.code))
                || (this.defaultMessage != null && !this.defaultMessage.equals(test.defaultMessage))
                || (this.status != null && !this.status.equals(test.status))) {
            return false;
        }

        if ((this.code == null && test.code == null) && (this.defaultMessage == null && test.defaultMessage == null)
                && (this.status == null && test.status == null) && (this.parameterMap == null && test.parameterMap == null)) {
            return true;
        }

        if (this.parameterMap == null && test.parameterMap == null) {
            return true;
        }

        thisParamKeys = this.parameterMap.keySet();
        testParamKeys = test.parameterMap.keySet();

        if (!testParamKeys.containsAll(thisParamKeys) || !thisParamKeys.containsAll(testParamKeys)) {
            return false;
        }

        for (String key : thisParamKeys) {
            thisValue = this.parameterMap.get(key);
            testValue = test.parameterMap.get(key);

            if (thisValue == null && testValue != null || thisValue != null && testValue == null) {
                return false;
            }

            if (thisValue != null && !thisValue.equals(testValue)) {
                return false;
            }

        }

        return true;
    }

    /** @see java.lang.Comparable#compareTo(Object) */
    public int compareTo(ReturnStatusItem item) {
        if (this.equals(item)) {
            return 0;
        }
        return this.toString().compareTo(item.toString());
    }

}
