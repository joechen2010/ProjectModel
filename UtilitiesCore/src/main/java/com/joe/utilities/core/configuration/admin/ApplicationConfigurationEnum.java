package com.joe.utilities.core.configuration.admin;

public enum ApplicationConfigurationEnum {

	ENABLE_PRIOR_AUTHORIZATION("EnablePriorAuthorization"),
	ENABLE_BEHAVIORAL_HEALTH("EnableBehavioralHealth"),
	DEFAULT_TO_BEHAVIORAL_HEALTH("DefaultToBehavioralHealth"),
	PRIOR_AUTHRIOTION_DEFAULT_TREATMENT_SETTING("PriorAuthorizationDefaultTreamentSetting");
	
	private String configCode;
	
	private ApplicationConfigurationEnum(String configCode) {
		this.configCode = configCode;
	}
	
	public String getConfigCode() {
		return configCode;
	}
	
}
