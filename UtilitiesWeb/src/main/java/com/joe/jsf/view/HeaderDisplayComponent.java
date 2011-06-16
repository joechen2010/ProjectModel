package com.joe.jsf.view;

/**
 * UI display class for placing information in the header.
 * Mostly used for CMV display of member data but can be used
 * for any page that needs data to display on the header.
 * 
 * Meant to replace the HeaderDisplayView implementation.  This should
 * take place when the CMV is refactored to be icefaces enabled.
 * Talk to Steve Wilcher or John Jones when this happens.
 * 
 * @author swilcher
 * Creation Date: 7/7/2009
 */
public class HeaderDisplayComponent
{		
	private String displayLabel;
	private String displayValue;
	
	public HeaderDisplayComponent(String label, String value)
	{
		this.displayLabel = label;
		this.displayValue = value;
	}
	
	public HeaderDisplayComponent()
	{
		this.displayLabel = "";
		this.displayValue = "";
	}
	
	/**
	 * @return the displayLabel
	 */
	public String getDisplayLabel()
	{
		if (displayLabel!=null && !displayLabel.equals(""))
			return displayLabel+": ";
		else
			return "";
	}
	/**
	 * @param displayLabel the displayLabel to set
	 */
	public void setDisplayLabel(String displayLabel)
	{
		this.displayLabel = displayLabel;
	}
	
	/**
	 * @return the displayValue
	 */
	public String getDisplayValue()
	{
		return displayValue;
	}
	
	/**
	 * @param displayValue the displayValue to set
	 */
	public void setDisplayValue(String displayValue)
	{
		this.displayValue = displayValue;
	}
}
