
package cn.jcenterhome.web.action.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class ProfilefieldAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageprofilefield")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		Map<String, Object> thevalue = null;
		List<Map<String, Object>> list = null;
		String fieldidString = request.getParameter("fieldid");
		int fieldid = Common.empty(fieldidString) ? 0 : Common.intval(fieldidString);
		if (fieldid != 0) {
			List<Map<String, Object>> tempML = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("profilefield") + " WHERE fieldid='" + fieldid + "'");
			if (tempML.size() > 0) {
				thevalue = tempML.get(0);
			}
		}
		String op = request.getParameter("op");
		if (!Common.empty(op) && !op.equals("add") && Common.empty(thevalue)) {
			return cpMessage(request, mapping, "cp_there_is_no_designated_users_columns");
		}
		boolean isFieldSubmit = false;
		boolean isOrderSubmit = false;
		try {
			isFieldSubmit = submitCheck(request, "fieldsubmit");
			isOrderSubmit = submitCheck(request, "ordersubmit");
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}

		if (isFieldSubmit) {
			Map<String, Object> setarr = new HashMap<String, Object>();
			String tempS = request.getParameter("title");
			if(Common.empty(tempS)){
				return cpMessage(request, mapping, "cp_profile_title_can_not_be_empty");
			}
			setarr.put("title", Common.sHtmlSpecialChars(tempS.trim()));
			tempS = request.getParameter("note");
			setarr.put("note", tempS == null ? "" : Common.sHtmlSpecialChars(tempS.trim()));
			tempS = request.getParameter("formtype");
			setarr.put("formtype", tempS == null ? "" : Common.sHtmlSpecialChars(tempS.trim()));
			int maxsize = Common.intval(request.getParameter("maxsize"));
			if (maxsize < 1 || maxsize > 255) {
				maxsize = 50;
			}
			setarr.put("maxsize", maxsize);
			setarr.put("required", Common.intval(request.getParameter("required")));
			setarr.put("invisible", Common.intval(request.getParameter("invisible")));
			setarr.put("allowsearch", Common.intval(request.getParameter("allowsearch")));
			tempS = request.getParameter("choice");
			setarr.put("choice", tempS == null ? "" : Common.sHtmlSpecialChars(tempS.trim()));
			setarr.put("displayorder", Common.intval(request.getParameter("displayorder")));

			if (thevalue == null || Common.empty(thevalue.get("fieldid"))) {
				int fieldid_ = dataBaseService.insertTable("profilefield", setarr, true, false);
				String sql = "ALTER TABLE " + JavaCenterHome.getTableName("spacefield") + " ADD `field_"
						+ fieldid_ + "` varchar(" + maxsize + ") NOT NULL default ''";
				Map<String, Object> tempM = dataBaseService.execute(sql);
				Object success = tempM.get("sucess");
				if (success == null && ((Integer) success) == 0) {
					sql = "DELETE FROM " + JavaCenterHome.getTableName("profilefield") + " WHERE fieldid='"
							+ fieldid_ + "'";
					dataBaseService.execute(sql);
				}
			} else {
				String sql = "ALTER TABLE " + JavaCenterHome.getTableName("spacefield") + " CHANGE `field_"
						+ thevalue.get("fieldid") + "` `field_" + thevalue.get("fieldid") + "` varchar("
						+ maxsize + ") NOT NULL default ''";
				Map<String, Object> tempM = dataBaseService.execute(sql);
				Object success = tempM.get("sucess");
				if (success == null && ((Integer) success) == 0) {
					return cpMessage(request, mapping, "cp_failed_to_change_the_length_of_columns",
							"admincp.jsp?ac=profilefield", 1);
				}
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("fieldid", thevalue.get("fieldid"));
				dataBaseService.updateTable("profilefield", setarr, whereData);
			}
			try {
				cacheService.profilefield_cache();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=profilefield", 1);
		} else if (isOrderSubmit) {
			Map<String, String> displayorder = (Map<String, String>) getParameters(request, "displayorder");
			if (displayorder != null) {
				String key, value;
				for (Entry<String, String> entry : displayorder.entrySet()) {
					key = entry.getKey();
					value = entry.getValue();
					Map<String, Object> setData = new HashMap<String, Object>(1);
					setData.put("displayorder", Common.intval(value));
					Map<String, Object> whereData = new HashMap<String, Object>(1);
					whereData.put("fieldid", Common.intval(key));
					dataBaseService.updateTable("profilefield", setData, whereData);
				}
				try {
					cacheService.profilefield_cache();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=profilefield", 1);
		}

		if (Common.empty(op)) {
			list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("profilefield") + " ORDER BY displayorder");
			Map<String, String> actives = new HashMap<String, String>();
			actives.put("view", " class=\"active\"");
			request.setAttribute("actives", actives);
			request.setAttribute("list", list);
		} else if (op.equals("add")) {
			thevalue = new HashMap<String, Object>();
			thevalue.put("filedid", 0);
			thevalue.put("formtype", "text");
			thevalue.put("maxsize", 50);
			request.setAttribute("thevalue", thevalue);

			Map<String, String> formtypearr = new HashMap<String, String>();
			request.setAttribute("formtypearr", formtypearr);
		} else if (op.equals("edit")) {
			Map<String, String> formtypearr = new HashMap<String, String>();
			formtypearr.put(String.valueOf(thevalue.get("formtype")), " selected");
			request.setAttribute("formtypearr", formtypearr);
			request.setAttribute("thevalue", thevalue);
		} else if (op.equals("delete")) {
			if (fieldid != 0
					&& adminDeleteService.deleteProfilefield(request, response, new String[] {String
							.valueOf(fieldid)})) {
				try {
					cacheService.profilefield_cache();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=profilefield", 1);
			} else {
				return cpMessage(request, mapping, "cp_choose_to_delete_the_columns",
						"admincp.jsp?ac=profilefield", 1);
			}
		}

		Map<String, Map<String, String>> _TPL = new HashMap<String, Map<String, String>>();
		Map<String, String> formtypes = new HashMap<String, String>();
		_TPL.put("formtypes", formtypes);
		formtypes.put("text", "文本输入");
		formtypes.put("select", "单选列表");
		formtypes.put("multi", "多选列表");

		request.setAttribute("_TPL", _TPL);
		request.setAttribute("FORMHASH", formHash(request));
		return mapping.findForward("profilefield");
	}

	
	private Object getParameters(HttpServletRequest request, String prefix) {
		return getParameters(request, prefix, false);
	}

	
	private Object getParameters(HttpServletRequest request, String prefix, boolean isCheckBox) {
		Map<String, String[]> primalParameters = request.getParameterMap();
		if (primalParameters == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		String key;
		String[] value;
		String prefix_ = null;
		if (prefix != null) {
			prefix_ = prefix + "[";
		}
		for (Entry<String, String[]> primalPE : primalParameters.entrySet()) {
			key = primalPE.getKey();
			if (prefix == null || key.startsWith(prefix_)) {
				value = primalPE.getValue();
				if (!getParametersSetResultMap(result, key, value, isCheckBox)) {
					return null;
				}
			}
		}
		if (prefix != null) {
			return result.get(prefix);
		}
		return result;
	}

	private String disposeParameter(String parameterName) {
		if (parameterName.endsWith("[]")) {
			return parameterName.substring(0, parameterName.length() - 2);
		} else {
			return parameterName;
		}
	}

	private boolean getParametersSetResultMap(Map<String, Object> result, String key, String[] value,
			boolean isCheckBox) {
		key = disposeParameter(key);
		return getParametersParseKey(new StringBuilder(key), result, value, isCheckBox);
	}

	private boolean getParametersParseKey(StringBuilder operatingKey, Map<String, Object> supMap,
			String[] value, boolean isCheckBox) {
		int tempI = operatingKey.indexOf("[");
		int tempII = operatingKey.indexOf("]");
		if (tempI < 0) {
			putValue(supMap, operatingKey.toString(), value, isCheckBox);
			return true;
		} else if (tempII < tempI) {
			return false;
		}
		String subKey = operatingKey.substring(0, tempI);
		Map<String, Object> subMap = (Map<String, Object>) supMap.get(subKey);
		if (subMap == null) {
			subMap = new HashMap<String, Object>();
			supMap.put(subKey, subMap);
		}
		operatingKey.deleteCharAt(tempII);
		operatingKey.delete(0, tempI + 1);
		return getParametersParseKey(operatingKey, subMap, value, isCheckBox);
	}

	private void putValue(Map<String, Object> targetMap, String key, String[] value, boolean isCheckBox) {
		if (isCheckBox || value == null || value.length == 0) {
			targetMap.put(key, value);
		} else {
			targetMap.put(key, value[0]);
		}
	}

}