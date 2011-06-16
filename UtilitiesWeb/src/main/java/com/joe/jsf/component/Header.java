package com.joe.jsf.component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.icesoft.faces.context.effects.JavascriptContext;

public class Header {

	boolean memberDetailVisible = false;
	boolean footerPanelVisible = false;
	boolean roleChangeEnabled = true;

	public boolean isRoleChangeEnabled() {
		return roleChangeEnabled;
	}

	public void setRoleChangeEnabled(boolean roleChangeEnabled) {
		this.roleChangeEnabled = roleChangeEnabled;
	}

	public boolean isFooterPanelVisible() {
		return footerPanelVisible;
	}

	public void setFooterPanelVisible(boolean footerPanelVisible) {
		this.footerPanelVisible = footerPanelVisible;
	}

	public boolean isMemberDetailVisible() {
		return memberDetailVisible;
	}

	public void setMemberDetailVisible(boolean memberDetailVisible) {
		this.memberDetailVisible = memberDetailVisible;
	}

	public void openInteractionManagement(ActionEvent event) {
		JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(),
				"window.open('calls.iface', '_blank');");
	}
}
