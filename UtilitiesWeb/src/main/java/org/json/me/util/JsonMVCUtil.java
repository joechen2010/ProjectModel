/**
 * Copyright (c) http://www.hao-se.cn Ltd.,2007 All  rights  reserved.
 */
package org.json.me.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.me.JSONObject;


public class JsonMVCUtil {


	public static void jsonResponse(JSONObject jsonData,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// check whether it is script Tag...
		// which is called by JSON
		boolean scriptTag = false;
		String cb = request.getParameter("callback");
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
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
		try {
			response.setContentType("text/html;charset=UTF-8");

			response.getWriter().print(jsonData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	public static void jsonOkResponse(String okStr, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("success", true);
			jsonResult.put("info", okStr);

			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().print(jsonResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	public static void jsonFailResponse(String failStr,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("failure", true);
			jsonResult.put("errorInfo", failStr);

			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().print(jsonResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
