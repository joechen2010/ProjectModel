package com.joe.utilities.core.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.joe.utilities.core.util.ReturnStatus;



/** Contains a list of composite Global Configuration errors */
public class CompositeGlobalConfigurationException extends
		GlobalConfigurationException {

	/**
	 * Used by the Serialization interface.
	 */
	private static final long serialVersionUID = 1L;
	
	/** The list of global configuration exceptions */
	List<GlobalConfigurationException> globalExceptionList = new ArrayList<GlobalConfigurationException>();

	/**
	 * Generate an exception that holds a list of exceptions
     * @param cause The initial exception in the list.
     */
    public CompositeGlobalConfigurationException(GlobalConfigurationException  cause) {
        super(cause.getMessage(), cause);
        
        if ( cause instanceof CompositeGlobalConfigurationException ){
        	this.addException((CompositeGlobalConfigurationException) cause);
        }else {
        	this.addException(cause);
        }
    }
    
    
	/** Adds the exception to the list 
	 * 
	 * @param exception The error 
	 */
	public void addException(GlobalConfigurationException exception){
        if ( exception instanceof CompositeGlobalConfigurationException ){
         	globalExceptionList.addAll(((CompositeGlobalConfigurationException )exception).getGlobalExceptionList());
        } else {
         	globalExceptionList.add(exception);
        }
	}
	
	/** Returns the entire list of global errors.
	 * 
	 * @return The list of global configuration exceptions.
	 */
	public List<GlobalConfigurationException> getGlobalExceptionList(){
		return  globalExceptionList;
	}
	
	public ReturnStatus getReturnStatus(){
		ReturnStatus returnStatus = new ReturnStatus();
		
		for ( GlobalConfigurationException error : getGlobalExceptionList()){
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("message", error.getMessage());
			returnStatus.addError("Exception", error.getMessage(),params);
		}
		
		return returnStatus;
	}
}
