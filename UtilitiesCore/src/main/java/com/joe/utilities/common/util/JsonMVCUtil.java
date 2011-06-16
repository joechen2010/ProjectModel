/**
 * Copyright (c) http://www.hao-se.cn Ltd.,2007 All  rights  reserved.
 */
package com.joe.utilities.common.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

/**
 * Json工具类
 * 
 *
 */
public class JsonMVCUtil {

	/**
	 * 更新时
	 * 
	 * @param jsonData
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public static void jsonResponse(JSONObject jsonData,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// check whether it is script Tag...
		// which is called by JSON
		boolean scriptTag = false;
		String cb = request.getParameter("callback");

		if (cb != null) {
			scriptTag = true;
			response.setContentType("text/javascript;charset=UTF-8");
		} else {
			response.setContentType("application/x-json;charset=UTF-8");
		}

		PrintWriter out = response.getWriter();
		if (scriptTag) {
			out.write(cb + "(");
		}
		response.getWriter().print(jsonData);
		if (scriptTag) {
			out.write(");");
		}
	}

	/**
	 * @param jsonData
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public static void jsonErrorsResponse(JSONObject jsonData,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html;charset=UTF-8");

		response.getWriter().print(jsonData);
	}

	/**
	 * 成功创建，给出成功信息
	 * 
	 * @param okStr
	 * @param request
	 * @param response
	 */
	public static void jsonOkResponse(String okStr, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		JSONObject jsonResult = new JSONObject();
		jsonResult.put("success", true);
		jsonResult.put("info", okStr);

		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().print(jsonResult);
	}

	/**
	 * 创建失败，给出失败信息
	 * 
	 * @param failStr
	 * @param request
	 * @param response
	 */
	public static void jsonFailResponse(String failStr,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("failure", true);
		jsonResult.put("errorInfo", failStr);

		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().print(jsonResult);
	}

}
