package com.joe.facelets.table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joe.facelets.table.formatters.DateFormat;

public abstract class DataMapper<RowType> {
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
	public abstract Row convertRow(RowType obj);

	public List<Row> convertRows(List<RowType> objs) {
		List<Row> result = new ArrayList<Row>();
		if (objs != null) {
			for (RowType row : objs) {
				result.add(convertRow(row));
			}
		}
		return result;
	}

	Date parseDate(Object date, String pattern) {
		Date result = null;
		if (date != null) {
			try {
				DateFormat formatter = new DateFormat(pattern);
				result = formatter.parse(formatter.format(date));
			} catch (ParseException e) {
				// e.printStackTrace();
			}
		}

		return result;
	}
	
	Date parseDate(Object date){
		return parseDate(date, DEFAULT_DATE_FORMAT);
	}
}
