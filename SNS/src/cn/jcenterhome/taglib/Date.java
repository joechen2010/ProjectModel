package cn.jcenterhome.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import cn.jcenterhome.util.Common;

public class Date extends TagSupport {

	
	private static final long serialVersionUID = -3281689141982775524L;
	private String dateformat;
	private Integer timestamp;
	private String format;

	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().write(
					Common.sgmdate((HttpServletRequest) pageContext.getRequest(), dateformat, timestamp,
							!Common.empty(format)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.doStartTag();
	}
}
