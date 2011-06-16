package com.joe.utilities.core.util;

public interface IStandardFieldLookupProfile extends ILookupProfile {

	public static final String ERROR_CODE_MISSING_CODE = "SF_MISSING_CODE";
	public static final String ERROR_CODE_MISSING_DESCRIPTION = "SF_MISSING_DESC";
	public static final String ERROR_CODE_CODE_NOT_UNIQUE = "SF_CODE_NOT_UNIQUE";
	public static final String ERROR_CODE_DESC_NOT_UNIQUE = "SF_DESC_NOT_UNIQUE";
	
	/**
	 * Returns if item is active or inactive
	 * True = active; False = inactive
	 * @return boolean
	 */
	public boolean isActive();
	
	/**
	 * Validates the item for save
	 * @return ReturnStatus
	 */
	public ReturnStatus validate();
	
	/**
	 * Returns if item is new or existing
	 * True = new; False = existing
	 * @return boolean
	 */
	public boolean isNew();
	
	/**
	 * Sets if item is new or existing
	 * True = new; False = existing
	 * @param newFlag
	 */
	public void setNew(boolean newFlag);
	
}
