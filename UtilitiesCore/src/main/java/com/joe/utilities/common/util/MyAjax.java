package com.joe.utilities.common.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MyAjax {

	public String getXML(ResultSet rs, int size, int len, int page,
			String[] columnArray) {

		StringBuffer XML = new StringBuffer();
		XML.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		XML.append("<response>\n");
		XML.append("\n");
		XML.append("<page size=\"");
		XML.append(size);
		XML.append("\" len=\"");
		XML.append(len);
		XML.append("\" page=\"");
		XML.append(page);
		XML.append("\" />");
		XML.append("\n");
		try {
			// org.json.JSONObject json = null;

			int i = 0;
			while (rs.next()) {
				XML.append("<result>");
				String s = "";
				for (int j = 0; j < columnArray.length; j++) {
					s = columnArray[j] == null ? "" : columnArray[j].toString();
					XML.append("<" + s + ">");
					XML.append(rs.getObject(s).toString().replaceAll("<",
							"&lt;").replaceAll(">", "&gt;"));
					XML.append("</" + s + ">");
				}
				XML.append("</result>");
				XML.append("\n");
				i++;
				XML.append("<total_record>" + i + "</total_record>");
			}

			XML.append("</response>");

		} catch (Exception e) {
			System.out.println("getXML ERROR:" + e);
		} finally {
			MyConnection.closeResultSet(rs);
		}
		return XML.toString();
	}

	public static void createAjaxResponseContent(Object object,
			javax.servlet.http.HttpServletResponse response, String type) {
		response.setContentType("text/" + type + "; charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");
		java.io.PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (java.io.IOException e) {
		}
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<HigherUp>");
		if (object instanceof String)
			out.println("<![CDATA[" + object + "]]>");
		else if (object instanceof java.util.List) {
			java.util.List list = (java.util.List) object;
			for (int i = 0; i < list.size(); i++) {
				Object obj[] = (Object[]) list.get(i);
				out.println("<Child>");
				for (int j = 0; i < obj.length; j++) {
					String value = obj[j] == null ? "" : obj[j].toString();
					out.println("<Value" + (j + 1) + "><![CDATA[" + value
							+ "]]></Value" + (j + i) + ">");
				}
				out.println("</Child>");
			}
		}
		out.println("</HigherUp>");
	}

	 
	
	
}
