package com.joe.tools;

import java.util.ArrayList;
import java.util.List;

public class Forecast {
	
	private String name;
	private String red;
	private String blue;
	private String and500;
	private List<String> and500AndList = new ArrayList<String>();
	private List<String> and500AndDataList = new ArrayList<String>();
	private List<String> andOtherList = new ArrayList<String>();
	private List<String> andOtherDataList = new ArrayList<String>();
	public Forecast(String name, String red, String blue) {
		super();
		this.name = name;
		this.red = red;
		this.blue = blue;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRed() {
		return red;
	}
	public void setRed(String red) {
		this.red = red;
	}
	public String getBlue() {
		return blue;
	}
	public void setBlue(String blue) {
		this.blue = blue;
	}
	public String getAnd500() {
		return and500;
	}
	public void setAnd500(String and500) {
		this.and500 = and500;
	}
	public List<String> getAnd500AndList() {
		return and500AndList;
	}
	public void setAnd500AndList(List<String> and500AndList) {
		this.and500AndList = and500AndList;
	}
	public List<String> getAnd500AndDataList() {
		return and500AndDataList;
	}
	public void setAnd500AndDataList(List<String> and500AndDataList) {
		this.and500AndDataList = and500AndDataList;
	}
	public List<String> getAndOtherList() {
		return andOtherList;
	}
	public void setAndOtherList(List<String> andOtherList) {
		this.andOtherList = andOtherList;
	}
	public List<String> getAndOtherDataList() {
		return andOtherDataList;
	}
	public void setAndOtherDataList(List<String> andOtherDataList) {
		this.andOtherDataList = andOtherDataList;
	}
	
	
	/*public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Forecast other = (Forecast) obj;
        
        return new EqualsBuilder().append(getName(), other.getName())
        						  .isEquals();
	}*/
	
}
