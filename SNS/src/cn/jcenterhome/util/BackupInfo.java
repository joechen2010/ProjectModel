package cn.jcenterhome.util;


public class BackupInfo {
	private int offset;
	private String structure;
	private String data;
	private boolean complete;
	private boolean runBackupData;

	public boolean isRunBackupData() {
		return runBackupData;
	}

	public void setRunBackupData(boolean runBackupData) {
		this.runBackupData = runBackupData;
	}

	public BackupInfo() {
		offset = 0;
		structure = "";
		data = "";
		complete = true;
	}

	
	public void setOffset(int offset) {
		this.offset = offset;
	}

	
	public void setStructure(String structure) {
		this.structure = structure;
	}

	
	public void setData(String data) {
		this.data = data;
	}

	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	
	public int getOffset() {
		return offset;
	}

	
	public String getStructure() {
		return structure;
	}

	
	public String getData() {
		return data;
	}

	
	public boolean isComplete() {
		return complete;
	}
}
