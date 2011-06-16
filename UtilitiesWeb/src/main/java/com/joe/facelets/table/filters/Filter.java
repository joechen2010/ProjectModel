package com.joe.facelets.table.filters;

import javax.faces.component.UIInput;

public abstract class Filter {
	protected UIInput filterComponent;
	protected String oldValue;
	
	public abstract boolean isMatch(Object obj);
	
	public UIInput getFilterComponent() {
		return filterComponent;
	}

	public void setFilterComponent(UIInput filterComponent) {
		this.filterComponent = filterComponent;
	}
	
	//For performance reasons, this needs to be very accurate. 
	//It will be run at least 10 times per page view, but if it says 
	//that the filter has changed when it has not really, 
	//the entire table will be refiltered 10 times
	public boolean hasChanged()
	{
		return !getValue().equals(this.oldValue);
	}
	
	public void clear() {
		filterComponent.setValue(null);
	}
	
	public String getValue()
	{
		String value = (String) filterComponent.getValue();
		if(value == null) return "";
		return value;
	}
	
	public void setValue(String value)
	{
		filterComponent.setValue(value);
	}

	public void applyLabelLimit(int maxChars) {
		// do nothing by default, this only applies to list filters 
	}
	
}

