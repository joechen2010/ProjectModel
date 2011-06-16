package com.joe.jsf.validator;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator( value="CustomDateValidator" )
public class CustomDateValidator implements Validator {

	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		GregorianCalendar myDate =  new GregorianCalendar();
		myDate.setTime((Date)value);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(1841, 1, 1);
		
		if (myDate.before(cal)) {
            FacesMessage message = new FacesMessage();
            
            message.setDetail("Invalid date entered. Please enter a date in the format MM/DD/CCYY.");
            message.setSummary("Invalid date entered. Please enter a date in the format MM/DD/CCYY.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
		}
	}

}
