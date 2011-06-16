package com.joe.facelets.table;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.model.DataModel;

public class TableHelper {
	public static String format(Object o, Format f) {
		if(o == null) return "";
		if(f == null) return o.toString();
		return f.format(o);
	}
	public static Integer length(Object o) {
		if(o instanceof DataModel) {
			return ((DataModel) o).getRowCount();
		}
		if(o instanceof List<?>) {
			return ((List<?>)o).size();
		}
		throw new IllegalArgumentException("Object type not supported: " + o.getClass().getCanonicalName());
	}
	
	public static String formatDate(Object o, String pattern){
		if(o == null){
			return "";
		}
		
		if(pattern == null || "".equals(pattern) || !(o instanceof Date)){
			return o.toString();
		}else{
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			return formatter.format(o);
		}
		
	}
}
