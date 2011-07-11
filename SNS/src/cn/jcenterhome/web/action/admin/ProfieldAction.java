
package cn.jcenterhome.web.action.admin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class ProfieldAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageprofield")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		Map<String, Object> profield = null;
		int fieldId = Common.intval(request.getParameter("fieldid"));
		if (fieldId > 0) {
			String sql = "SELECT * FROM " + JavaCenterHome.getTableName("profield") + " WHERE fieldid = "
					+ fieldId;
			List<Map<String, Object>> query = dataBaseService.executeQuery(sql);
			if (!query.isEmpty()) {
				profield = query.get(0);
			}
		}
		String op = request.getParameter("op");
		if (op != null && !"add".equals(op) && profield == null) {
			return cpMessage(request, mapping, "cp_there_is_no_designated_users_columns");
		}
		try {
			if (submitCheck(request, "fieldsubmit")) {
				Map<String, Object> setData = new HashMap<String, Object>();
				String title=Common.trim(request.getParameter("title"));
				if(Common.empty(title)){
					return cpMessage(request, mapping, "cp_mtag_title_can_not_be_empty");
				}
				setData.put("title", Common.cutstr((String) Common.sHtmlSpecialChars(title), 80, ""));
				setData.put("note", Common.cutstr((String) Common.sHtmlSpecialChars(Common.trim(request
						.getParameter("note"))), 255, ""));
				setData.put("formtype", (String) Common.sHtmlSpecialChars(Common.trim(request
						.getParameter("formtype"))));
				setData.put("inputnum", Common.range(request.getParameter("inputnum"), 65535, 0));
				setData.put("choice", Common.sHtmlSpecialChars(Common.trim(request.getParameter("choice"))));
				setData.put("mtagminnum", Common.range(request.getParameter("mtagminnum"), 65535, 0));
				setData.put("manualmoderator", Common.intval(request.getParameter("manualmoderator")));
				setData.put("manualmember", Common.intval(request.getParameter("manualmember")));
				setData.put("displayorder", Common.range(request.getParameter("displayorder"), 255, 0));

				if (profield == null || Common.empty(profield.get("fieldid"))) {
					dataBaseService.insertTable("profield", setData, false, false);
				} else {
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("fieldid", profield.get("fieldid"));
					dataBaseService.updateTable("profield", setData, whereData);
				}
				cacheService.profield_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=profield");
			} else if (submitCheck(request, "ordersubmit")) {
				String[] fieldids = request.getParameterValues("fieldid[]");
				if (fieldids != null) {
					for (String id : fieldids) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("profield")
								+ " SET displayorder = "
								+ Common.range(request.getParameter("displayorder_" + id), 255, 0)
								+ " WHERE fieldid = " + id);
					}
					cacheService.profield_cache();
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=profield");
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		if (Common.empty(op)) {
			String sql = "SELECT * FROM " + JavaCenterHome.getTableName("profield")
					+ " ORDER BY displayorder";
			List<Map<String, Object>> profields = dataBaseService.executeQuery(sql);
			request.setAttribute("profields", profields);
			request.setAttribute("actives_view", " class='active'");
		} else if ("add".equals(op)) {
			profield = new HashMap<String, Object>();
			profield.put("fieldid", 0);
			profield.put("formtype", "text");
		} else if ("delete".equals(op)) {
			Map globalProfield = Common.getCacheDate(request, response, "/data/cache/cache_profield.jsp",
					"globalProfield");
			if (globalProfield.size() < 2) {
				return cpMessage(request, mapping, "cp_have_one_mtag");
			}

			try {
				if (submitCheck(request, "deletesubmit")) {
					int newFieldId = Common.intval(request.getParameter("newfieldid"));
					if (Common.empty(globalProfield.get(newFieldId))) {
						return cpMessage(request, mapping, "cp_there_is_no_designated_users_columns");
					}

					if (fieldId > 0) {
						String sql = "DELETE FROM " + JavaCenterHome.getTableName("profield")
								+ " WHERE fieldid = " + fieldId;
						dataBaseService.executeUpdate(sql);
						sql = "UPDATE " + JavaCenterHome.getTableName("mtag") + " SET fieldid = "
								+ newFieldId + " WHERE fieldid = " + fieldId;
						dataBaseService.executeUpdate(sql);
						cacheService.profield_cache();
						return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=profield");
					} else {
						return cpMessage(request, mapping, "cp_choose_to_delete_the_columns",
								"admincp.jsp?ac=profield");
					}
				}
			} catch (Exception e1) {
				return showMessage(request, response, e1.getMessage());
			}
			globalProfield.remove(fieldId);
		}
		request.setAttribute("profield", profield);
		Map<String, String> formTypes = new LinkedHashMap<String, String>();
		formTypes.put("text", "文本输入");
		formTypes.put("select", "单选列表");
		formTypes.put("multi", "多选列表");
		request.setAttribute("formTypes", formTypes);
		return mapping.findForward("profield");
	}
}