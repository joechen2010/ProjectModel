package cn.jcenterhome.vo;


public class MessageVO {

	
	private String msgKey = null;
	
	private String forwardURL = null;
	
	private int second = 1;
	
	private Object[] args = null;

	
	public MessageVO() {
	}

	
	public MessageVO(String msgKey) {
		this.msgKey = msgKey;
	}

	
	public MessageVO(String msgKey, String forwardURL) {
		this.msgKey = msgKey;
		this.forwardURL = forwardURL;
	}

	
	public MessageVO(String msgKey, String forwardURL, int second, Object... args) {
		this.msgKey = msgKey;
		this.forwardURL = forwardURL;
		this.second = second;
		this.args = args;
	}

	
	public String getMsgKey() {
		return msgKey;
	}

	
	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}

	
	public String getForwardURL() {
		return forwardURL;
	}

	
	public void setForwardURL(String forwardURL) {
		this.forwardURL = forwardURL;
	}

	
	public int getSecond() {
		return second;
	}

	
	public void setSecond(int second) {
		this.second = second;
	}

	
	public Object[] getArgs() {
		return args;
	}

	
	public void setArgs(Object... args) {
		this.args = args;
	}
}
