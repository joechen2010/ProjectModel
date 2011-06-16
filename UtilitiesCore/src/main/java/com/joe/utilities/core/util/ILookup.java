package com.joe.utilities.core.util;

public interface ILookup extends ILookupProfile {

	/**
	 * Sets the code of the item
	 * @param code
	 */
	public void setCode(String code);
	
	/**
	 * Sets the description of the item
	 * @param description
	 */
	public void setDescription(String description);
	
	/**
	 * Sets if the item is active or inactive
	 * True = active; False = inactive
	 * @param active
	 */
	public void setActive(boolean active);
	
}
