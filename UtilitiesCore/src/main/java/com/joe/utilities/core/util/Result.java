package com.joe.utilities.core.util;


/**
* A Result object can be commonly used to return the status of a function.  
* The result is composed of a ReturnStatus and an object that is expected to be returned function. A null 
* object would correspond to a function that would otherwise return void.
* 
* The ReturnStatus will determine a general status based on the presence and status of its component items.  
* 
* @author Lev Shalevich
* 
* Creation date: //2007
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/

public class Result
{
    /** Return status of an operation. */
	private ReturnStatus returnStatus;

	/** Function return */
	private Object result;
	
	/**
	 * Simple Constructor
	 */
	public Result()
	{
		this(null, null);
	}

	/**
	 * Constructs Result object for a void function
	 * @param returnStatus
	 */
	public Result(ReturnStatus returnStatus)
	{
		this(returnStatus, null);
	}

	/**
	 * Constructs Result object from ReturnStatus and return object 
	 */
	public Result(ReturnStatus returnStatus, Object result)
	{
		this.returnStatus = returnStatus;
		this.result = result;
	}


    // ***********************************************************************
    // ACCESSORS
    // ***********************************************************************
	/**
	 * result the method return
	 * @return
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * returnStatus the status of method execution
	 * @return
	 */
	public ReturnStatus getReturnStatus() {
		return returnStatus;
	}

	
	// ***********************************************************************
    // MUTATORS
    // ***********************************************************************
    /**
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * @param returnStatus the returnStatus to set
	 */
	public void setReturnStatus(ReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}


    // ***********************************************************************
    // HELPERS
    // ***********************************************************************
    /**
     * Method isSuccess()
     * A convenience method that returns false if ReturnStatus is something other than OK.
     * 
     * @return boolean return status
     */
    public boolean isSuccess()
    {
        return this.returnStatus.isSuccess();   
    }
    
    /**
     * Method isEmpty()
     * A convenience method that returns false if result object is other than NUll. 
     * 
     * @return boolean
     */
    public boolean isEmpty()
    {
        return this.result == null;
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
        returnValue.append(returnStatus);
        returnValue.append(result);
        returnValue.append(")");
        return returnValue.toString();
    }
    
    
    
    public static void main(String [] args)
    {
    	Result result = new Result(ReturnStatus.createErrorReturnStatus("TEST", "Test message."));
    	System.out.println(result.toString());
    }
}
