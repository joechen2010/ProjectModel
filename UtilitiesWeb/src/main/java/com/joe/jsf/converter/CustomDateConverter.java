package com.joe.jsf.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang.StringUtils;

@FacesConverter( value="CustomDateConverter" )
public class CustomDateConverter extends DateTimeConverter{
    private static final String DATE_PATTERN = "MM/dd/yyyy";
    private static final String REGULAR_EXPRESSION = "\\d{1,2}/\\d{1,2}/\\d{2,4}";
    
	public CustomDateConverter(){
		super();
		setTimeZone(TimeZone.getDefault());
		setPattern(DATE_PATTERN);
	}

	/**
	 * Override getAsObject to check for 2 digit year field
	 */
    public Object getAsObject(FacesContext context, UIComponent component,
            String value){
    	// if user specified letter T, return today's date
    	if ("T".equalsIgnoreCase(value)){
    		return new Date();
    	}

    	if (value != null && !value.equals("") && !validateDateTime(value)) {
            FacesMessage message = new FacesMessage();
            
            message.setDetail("Invalid date entered. Please enter a date in the format MM/DD/CCYY.");
            message.setSummary("Invalid date entered. Please enter a date in the format MM/DD/CCYY.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);                             
            throw new ConverterException(message);
    	}

    	
    	Date dateValue =  (Date)super.getAsObject(context, component, value);
    	
    	if (dateValue != null){
	        String [] dateComps = StringUtils.split(value,"/");
	        String year = dateComps[2];
	        
	        // if 2 digit year is specified
	        if (year.length() == 2){
	        	// convert to 4 digits
	        	try{
	        		int yearValue = Integer.parseInt(year);
	        		// if 2-digit year value is less than 50, make it 21st century
	        		if (yearValue < 50){
	        			dateComps[2] = "20".concat(year);
	        		}
	        		else{
	        			// else make it 20th century
	        			dateComps[2] = "19".concat(year);
	        		}
	        	}
	        	catch (NumberFormatException nfe){
	        		//return null;
	                FacesMessage message = new FacesMessage();
	                
	                message.setDetail("Invalid date entered. Please enter a date in the format MM/DD/CCYY.");
	                message.setSummary("Invalid date entered. Please enter a date in the format MM/DD/CCYY.");
	                message.setSeverity(FacesMessage.SEVERITY_ERROR);                             
	                throw new ConverterException(message);
	        		
	        	}
	        	
		        SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN); 
		        try
		        { 
			        dateValue = df.parse(StringUtils.join(dateComps, '/'));             
		        } catch (ParseException e) 
		        { 
		        	//e.printStackTrace();
		            FacesMessage message = new FacesMessage();
		            
		            message.setDetail("Invalid date entered. Please enter a date in the format MM/DD/CCYY.");
		            message.setSummary("Invalid date entered. Please enter a date in the format MM/DD/CCYY.");
		            message.setSeverity(FacesMessage.SEVERITY_ERROR);                             
		            throw new ConverterException(message);
		        }
	        }
    	}
        return dateValue;
    }
    
    private boolean validateDateTime(String date) {
   		boolean isValid = false;
   		Pattern p = Pattern.compile(REGULAR_EXPRESSION);
   		Matcher m = p.matcher(date);
   		if(!m.matches()){
   			return isValid;
   		}
   		
   		String [] dateArray = StringUtils.split(date,"/");
   		int  day= Integer.valueOf(dateArray[1]).intValue();
   		int month = Integer.valueOf(dateArray[0]).intValue();
   		int year = Integer.valueOf(dateArray[2]).intValue();
   
   		if ((day > 0 && day <= 31) && (month > 0 && month <= 12)) {
   
   			isValid = true;
   			try {
   				GregorianCalendar cal = new GregorianCalendar();
   
   				
   				cal.setLenient(false);
   				cal.set(year, (month - 1), day);
   				cal.add(Calendar.SECOND, 1);
   			} catch (IllegalArgumentException iae) {
   				isValid = false;
   			}
   		}
   		return isValid;
   	}

}
