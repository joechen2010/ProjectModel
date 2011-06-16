package com.joe.jsf.component;

import java.util.TimeZone;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

public abstract class Block {

	private ProcessNavigation process;

	private boolean blockVisible = true;

	private UIComponent contents;

	private String hotkey;

	public String instanceId;

	public abstract void init();

	public ProcessNavigation getProcess() {
		return process;
	}

	public void setProcess(ProcessNavigation process) {
		this.process = process;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getHotkey() {
		return hotkey;
	}

	public void setHotkey(String hotkey) {
		this.hotkey = hotkey;
	}

	public boolean isBlockVisible() {
		return blockVisible;
	}

	public void setBlockVisible(boolean blockVisible) {
		this.blockVisible = blockVisible;
	}

	public abstract String getBlockName();

	public Object getEventValue(ActionEvent event, String name) {
		UIComponent c = (UIComponent) event.getSource();
		return c.getAttributes().get(name);
	}

	public UIComponent getContents() {
		return contents;
	}

	public void setContents(UIComponent contents) {
		this.contents = contents;
	}
	
	/**
	 * Returns the default time zone from the JVM so that it can be used by the date/time converter to convert to the
	 * same time zone.
	 * 
	 * @return
	 */
	public TimeZone getTimeZone()
	{
		return TimeZone.getDefault();
	}

}
