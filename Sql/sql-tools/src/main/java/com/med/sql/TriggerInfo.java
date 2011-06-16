package com.med.sql;

/**
 * Bean containing common information about database triggers.
 * 
 * @author Jane Eisenstein
 */
public class TriggerInfo implements Comparable {
	
	String triggerId = null;
	String tableId = null;
	String schemaId = null;
	String type = null;
	String event = null;
	
	public TriggerInfo() {}
	
	public TriggerInfo(String triggerId, String tableId, String schemaId) {
		this.triggerId = triggerId;
		this.tableId = tableId;
		this.schemaId = schemaId;
	}
	
	public String getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(String triggerId) {
		this.triggerId = triggerId;
	}
	
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	
	public String getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
	public String toString() {
		return schemaId + "." + triggerId;
	}
	
	public String getFullName() {
		return schemaId + "." + tableId + "." + triggerId;
	}
	
	/**
	 * Note: this class has a natural ordering that is inconsistent with equals.
	 */
	public int compareTo(Object o) {
		if (o != null && o instanceof TriggerInfo) {
			TriggerInfo that = (TriggerInfo) o;
			return this.getFullName().compareTo(that.getFullName());
		} else
			return -1; // definitely not equal

	}
}
