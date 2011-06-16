package com.joe.jsf.component;

import java.util.Map;

import javax.faces.component.UINamingContainer;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;



public class SubformComponent extends UINamingContainer {
	public SubformComponent() {
	}
	
	@Override
	public void processDecodes(FacesContext arg0) {
		if(!isSubmitted()) return;
		super.processDecodes(arg0);
	}

	@Override
	public void processUpdates(FacesContext arg0) {
		if(!isSubmitted()) return;
		super.processUpdates(arg0);
	}

	@Override
	public void processValidators(FacesContext arg0) {
		if(!isSubmitted()) return;
		super.processValidators(arg0);
		//MethodBinding onUpdate = (MethodBinding) this.getAttributes().get("update");
		//onUpdate.invoke(FacesContext.getCurrentInstance(), new Object[0]);
	}
	public boolean isSubmitted() {
		return (isSubmittedForm() || (isInSubmittedForm() && submitOnParentSubmit()));
	}

	public boolean submitOnParentSubmit() {
		Map<String, Object> attributes = this.getAttributes();
		Object required_value = attributes.get("requiredForSaveAll");
		boolean required = true;
		if(required_value instanceof Boolean) {
			required = (Boolean) required_value;
		}
		if(required_value instanceof String) {
			required = (required_value.equals("true"));
		}
		return required;
	}
	public boolean isInSubmittedForm() {
		String client_id = this.getClientId(FacesContext.getCurrentInstance());
		return client_id.startsWith(findSpecificFormSubmitted());
	}
	public boolean isSubmittedForm() {
		String client_id = this.getClientId(FacesContext.getCurrentInstance());
		return client_id.equals(findSpecificFormSubmitted());
	}
	public String findSpecificFormSubmitted() {
		ExternalContext ctx = FacesContext.getCurrentInstance()
				.getExternalContext();
		Map<String, String> param = ctx.getRequestParameterMap();
		String form_id;
		//String submit_id = param.get("ice.event.captured");
		//System.out.println("Form: " + submit_id);
		
		String target_id = param.get("ice.event.target");
		//System.out.println("Target: " + target_id);
		
		String[] parts = target_id.split(":");

		form_id = parts[0];

		// System.out.println("Event triggered by: " + submit_id);

		// Find deepest form
		int depth = -1;
		for (int i = parts.length - 1; i >= 0; i--) {
			if (parts[i].endsWith("_form")) {
				depth = i;
				break;
			}
		}
		// Put all but the last part back together
		for (int i = 1; i <= depth; i++) {
			form_id += ":" + parts[i];
		}
		return form_id;
	}
}
