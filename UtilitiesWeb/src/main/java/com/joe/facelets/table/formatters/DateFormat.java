package com.joe.facelets.table.formatters;

import java.util.TimeZone;

public class DateFormat extends java.text.SimpleDateFormat implements Format{

	public DateFormat(String string) {
		super(string);
		setTimeZone(TimeZone.getDefault());
	}

	private static final long serialVersionUID = 1L;
	
}
