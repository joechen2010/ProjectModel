 package com.joe.jsf.web.view;

/**
 * This class TODO <enter description of class here>
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: May 10, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class HeaderDisplayView {
	
	/*
	 * Constructor
	 */
	public HeaderDisplayView() {
		component1 = new DisplayComponent();
		component2 = new DisplayComponent();
		component3 = new DisplayComponent();
		component4 = new DisplayComponent();
		component5 = new DisplayComponent();
		component6 = new DisplayComponent();
		component7 = new DisplayComponent();
		component8 = new DisplayComponent();
	}
	private DisplayComponent component1;
	private DisplayComponent component2;
	private DisplayComponent component3;
	private DisplayComponent component4;
	private DisplayComponent component5;
	private DisplayComponent component6;
	private DisplayComponent component7;
	private DisplayComponent component8;
	
	public class DisplayComponent {
		
		private String displayLabel;
		private String displayValue;
		
		/*
		 * @constructor
		 */
		public DisplayComponent(String label, String value) {
			this.displayLabel = label;
			this.displayValue = value;
		}
		public DisplayComponent() {
			this.displayLabel = "";
			this.displayValue = "";
		}
		
		/**
		 * @return the displayLabel
		 */
		public String getDisplayLabel() {
			if (displayLabel!=null && !displayLabel.equals("")){
				return displayLabel+":";
			}
			return "";
		}
		/**
		 * @param displayLabel the displayLabel to set
		 */
		public void setDisplayLabel(String displayLabel) {
			this.displayLabel = displayLabel;
		}
		/**
		 * @return the displayValue
		 */
		public String getDisplayValue() {
			return displayValue;
		}
		
		/**
		 * @return the concatenated displayValue
		 */
		public String getDisplayConcatenatedValue() {
			if (displayValue.length() > 10)
				return displayValue.substring(0, 10)+ "...";
			return displayValue;
		}
		
		/**
		 * @param displayValue the displayValue to set
		 */
		public void setDisplayValue(String displayValue) {
			this.displayValue = displayValue;
		}
		
	}

	/**
	 * @return the component1
	 */
	public DisplayComponent getComponent1() {
		if (component1!=null) {
			return component1;
		}
		else {
			return new DisplayComponent();
		}

	}

	/**
	 * @param component1 the component1 to set
	 */
	public void setComponent1(DisplayComponent component1) {
		this.component1 = component1;
	}

	/**
	 * @return the component2
	 */
	public DisplayComponent getComponent2() {
		if (component2!=null) {
			return component2;
		}
		else {
			return new DisplayComponent();
		}
	}

	/**
	 * @param component2 the component2 to set
	 */
	public void setComponent2(DisplayComponent component2) {
		this.component2 = component2;
	}

	/**
	 * @return the component3
	 */
	public DisplayComponent getComponent3() {
		if (component3!=null) {
			return component3;
		}
		else {
			return new DisplayComponent();
		}
	}

	/**
	 * @param component3 the component3 to set
	 */
	public void setComponent3(DisplayComponent component3) {
		this.component3 = component3;
	}

	/**
	 * @return the component4
	 */
	public DisplayComponent getComponent4() {
		if (component4!=null) {
			return component4;
		}
		else {
			return new DisplayComponent();
		}
	}

	/**
	 * @param component4 the component4 to set
	 */
	public void setComponent4(DisplayComponent component4) {
		this.component4 = component4;
	}

	/**
	 * @return the component5
	 */
	public DisplayComponent getComponent5() {
		if (component5!=null) {
			return component5;
		}
		else {
			return new DisplayComponent();
		}
	}

	/**
	 * @param component5 the component5 to set
	 */
	public void setComponent5(DisplayComponent component5) {
		this.component5 = component5;
	}

	/**
	 * @return the component6
	 */
	public DisplayComponent getComponent6() {
		if (component6!=null) {
			return component6;
		}
		else {
			return new DisplayComponent();
		}
	}

	/**
	 * @param component6 the component6 to set
	 */
	public void setComponent6(DisplayComponent component6) {
		this.component6 = component6;
	}

	/**
	 * @return the component7
	 */
	public DisplayComponent getComponent7() {
		if (component7!=null) {
			return component7;
		}
		else {
			return new DisplayComponent();
		}
	}

	/**
	 * @param component7 the component7 to set
	 */
	public void setComponent7(DisplayComponent component7) {
		this.component7 = component7;
	}

	/**
	 * @return the component8
	 */
	public DisplayComponent getComponent8() {
		if (component8!=null) {
			return component8;
		}
		else {
			return new DisplayComponent();
		}
	}

	/**
	 * @param component8 the component8 to set
	 */
	public void setComponent8(DisplayComponent component8) {
		this.component8 = component8;
	}
}
