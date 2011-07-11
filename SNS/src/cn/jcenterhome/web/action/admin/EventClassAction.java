
package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.ImageUtil;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class EventClassAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageeventclass")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		Map<String, Object> thevalue = new HashMap<String, Object>();
		int classId = Common.intval(request.getParameter("classid"));
		if (classId > 0) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("eventclass") + " WHERE classid='" + classId + "'");
			if (query.size() > 0) {
				thevalue = query.get(0);
				int poster = (Integer) thevalue.get("poster");
				if (poster != 0) {
					thevalue.put("poster", "data/event/" + thevalue.get("classid") + ".jpg");
				} else {
					thevalue.put("poster", "image/event/default.jpg");
				}
			}
		}

		String op = request.getParameter("op");
		if (!Common.empty(op) && !op.equals("add") && Common.empty(thevalue)) {
			return cpMessage(request, mapping, "cp_there_is_no_designated_users_columns");
		}

		try {
			if (submitCheck(request, "eventclasssubmit")) {
				int classid = Common.intval(request.getParameter("classid"));
				String classname=Common.trim(request.getParameter("classname"));
				if(Common.empty(classname)){
					return cpMessage(request, mapping, "cp_event_classname_can_not_be_empty");
				}
				classname = Common.getStr(classname, 80, true, true, true, 0,
						0, request, response);
				String template = Common.getStr(request.getParameter("template"), 0, true, true, true, 0, 0,
						request, response);

				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("classname", classname);
				setData.put("template", template);
				setData.put("displayorder", Common.range(request.getParameter("displayorder"), 16777215, 0));

				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("eventclass") + " WHERE classname = '" + classname
						+ "'");
				Map<String, Object> value = query.size() > 0 ? query.get(0) : new HashMap<String, Object>();
				if (value.size() > 0 && classid != (Integer) value.get("classid")) {
					return cpMessage(request, mapping, "cp_classname_duplicated");
				}
				if (classid > 0) {
					int delposter = Common.intval(request.getParameter("delposter"));
					if (delposter != 0) {
						setData.put("poster", 0);
						value.put("poster", 0);
					}
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("classid", classid);
					dataBaseService.updateTable("eventclass", setData, whereData);
				} else {
					setData.put("poster", 0);
					classid = dataBaseService.insertTable("eventclass", setData, true, false);
				}
				DynaActionForm actionForm = (DynaActionForm) form;
				FormFile formFile = (FormFile) actionForm.get("poster");
				if (formFile != null && formFile.getFileSize() > 0) {
					String tmp_name = JavaCenterHome.jchRoot + "data/temp/eventposter.tmp";
					if (uploadFile(formFile, tmp_name)) {
						String thumbpath = ImageUtil.makeThumb(request, response, tmp_name, 200, 200, 300,
								300);
						File tmpFile = new File(tmp_name);
						if (Common.empty(thumbpath)) {
							if (!"jpg".equals(Common.getImageType(tmpFile))) {
								return cpMessage(request, mapping, "cp_poster_only_jpg_allowed");
							}
							thumbpath = tmp_name;
						} else {
							tmpFile.delete();
						}
						File eventDir = new File(JavaCenterHome.jchRoot + "data/event");
						if (!eventDir.isDirectory()) {
							eventDir.mkdirs();
						}
						File eventFile = new File(JavaCenterHome.jchRoot + "data/event/" + classid + ".jpg");
						if (eventFile.isFile()) {
							eventFile.delete();
						}
						new File(thumbpath).renameTo(eventFile);

						Integer temI = (Integer) value.get("poster");
						if ((temI == null || temI == 0) && eventFile.isFile()) {
							Map<String, Object> updateData = new HashMap<String, Object>();
							updateData.put("poster", 1);
							Map<String, Object> whereData = new HashMap<String, Object>();
							whereData.put("classid", classid);
							dataBaseService.updateTable("eventclass", updateData, whereData);
						}
					}
				}
				cacheService.eventclass_cache();
				request.setAttribute("thevalue", thevalue);
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=eventclass", 2);
			} else if (submitCheck(request, "ordersubmit")) {
				Object obj = getParameters(request, "displayorder");

				if (Common.isArray(obj)) {
					Map<String, String> displayorder = (Map<String, String>) obj;
					Map<Integer, Map<String, Object>> globalEventClass = Common.getCacheDate(request,
							response, "/data/cache/cache_eventclass.jsp", "globalEventClass");
					int classid;
					int neworder;
					Map<String, Object> updateData = new HashMap<String, Object>();
					Map<String, Object> whereData = new HashMap<String, Object>();
					for (Entry<String, String> entry : displayorder.entrySet()) {
						classid = Common.intval(entry.getKey());
						neworder = Common.range(entry.getValue(), 16777215, 0);
						if (((Integer) globalEventClass.get(classid).get("displayorder")).intValue() != neworder) {
							updateData.put("displayorder", neworder);
							whereData.put("classid", classid);
							dataBaseService.updateTable("eventclass", updateData, whereData);
						}
					}
					cacheService.eventclass_cache();
					return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=eventclass", 2);
				}
			} else if (submitCheck(request, "deletesubmit")) {
				int oldClassId = Common.intval(request.getParameter("classid"));
				if (oldClassId == 0) {
					return cpMessage(request, mapping, "at_least_one_option_to_delete_eventclass",
							"admincp.jsp?ac=eventclass", 2);
				}
				int newClassId = Common.intval(request.getParameter("newclassid"));
				if (newClassId == 0) {
					return cpMessage(request, mapping, "columns_option_to_merge_the_eventclass",
							"admincp.jsp?ac=eventclass&classid=" + oldClassId, 2);
				}
				List<Map<String, Object>> query = dataBaseService
						.executeQuery("SELECT classid FROM " + JavaCenterHome.getTableName("eventclass")
								+ " WHERE classid = '" + oldClassId + "'");
				if (query.size() == 0) {
					return cpMessage(request, mapping, "columns_option_to_merge_the_eventclass",
							"admincp.jsp?ac=eventclass&classid=" + oldClassId, 2);
				}
				Map<String, Object> updateData = new HashMap<String, Object>();
				updateData.put("classid", newClassId);
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("classid", oldClassId);
				dataBaseService.updateTable("event", updateData, whereData);
				dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("eventclass")
						+ " WHERE classid = '" + oldClassId + "'");
				cacheService.eventclass_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=eventclass", 2);
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		if ("delete".equals(op)) {
			if (Common.empty(thevalue)) {
				return cpMessage(request, mapping, "cp_there_is_no_designated_users_columns",
						"admincp.jsp?ac=eventclass", 2);
			}

			Map<Integer, Map<String, Object>> globalEventClass = Common.getCacheDate(request, response,
					"/data/cache/cache_eventclass.jsp", "globalEventClass");
			if (globalEventClass.size() < 2) {
				return cpMessage(request, mapping, "cp_have_no_eventclass", "admincp.jsp?ac=eventclass", 2);
			}
			globalEventClass.remove(thevalue.get("classid"));
			request.setAttribute("list", globalEventClass);
		} else if ("add".equals(op)) {
			thevalue.put("poster", "image/event/default.jpg");
		} else {
			Map<Integer, Map<String, Object>> globalEventClass = Common.getCacheDate(request, response,
					"/data/cache/cache_eventclass.jsp", "globalEventClass");
			request.setAttribute("actives_view", " class=\"active\"");
			request.setAttribute("list", globalEventClass);
		}
		request.setAttribute("thevalue", thevalue);
		return mapping.findForward("eventclass");
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

	private boolean uploadFile(FormFile formFile, String tempPath) throws FileNotFoundException, IOException {
		boolean success = false;
		if (formFile != null) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				inputStream = formFile.getInputStream();
				outputStream = new FileOutputStream(tempPath);
				int bufferSize = 2048;
				byte[] bufferByte = new byte[bufferSize];
				int read = 0;
				while ((read = inputStream.read(bufferByte, 0, bufferSize)) >= 0) {
					outputStream.write(bufferByte, 0, read);
				}
				success = true;
			} finally {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (outputStream != null) {
					outputStream.close();
					outputStream = null;
				}
			}
		}
		return success;
	}
}