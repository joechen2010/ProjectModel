package com.joe.facelets.table;

import java.util.Map;

public class Row {
	Map<String, Object> data;
	boolean selected = false;
	
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		
		//System.out.println("Row selected");
		this.selected = selected;
	}
	public Object get(Object arg) {
		return data.get(arg);
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public Row(Map<String, Object> data) {
		this.data = data;
	}
}
