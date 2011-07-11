package cn.jcenterhome.web.action.admin;

import java.util.ArrayList;
import java.util.HashMap;
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


public class HotUserAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		String ac = request.getParameter("ac");
		String vars = null;
		if ("hotuser".equals(ac)) {
			if (!Common.checkPerm(request, response, "managehotuser")) {
				return cpMessage(request, mapping, "cp_no_authority_management_operation");
			}
			vars = "'spacebarusername'";
		} else {
			if (!Common.checkPerm(request, response, "managedefaultuser")) {
				return cpMessage(request, mapping, "cp_no_authority_management_operation");
			}
			vars = "'defaultfusername','defaultpoke'";
		}
		try {
			if (submitCheck(request, "thevaluesubmit")) {
				Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
				List<String> configs = new ArrayList<String>();
				List<Object> userNames = new ArrayList<Object>();
				String elementName = null;
				if ("hotuser".equals(ac)) {
					elementName = "spacebarusername";
				} else {
					elementName = "defaultfusername";
					String defaultPoke = Common.cutstr(request.getParameter("defaultpoke"), 50, "");
					if (!defaultPoke.equals(sConfig.get("defaultpoke"))) {
						configs.add("('defaultpoke','" + defaultPoke + "')");
					}
				}

				String elementValue = request.getParameter(elementName);
				if (!Common.empty(elementValue)) {
					elementValue = elementValue.replaceAll("(\\s*(\r\n|\n\r|\n|\r)\\s*)", "\r\n").trim();
					List<Map<String, Object>> spaces = dataBaseService.executeQuery("SELECT username FROM "
							+ JavaCenterHome.getTableName("space") + " WHERE username IN ("
							+ Common.sImplode(elementValue.split("\r\n")) + ")");
					for (Map<String, Object> space : spaces) {
						userNames.add(Common.sAddSlashes(space.get("username")));
					}
				}

				elementValue = userNames.isEmpty() ? "" : Common.implode(userNames, ",");
				if (!elementValue.equals(sConfig.get(elementName))) {
					configs.add("('" + elementName + "','" + elementValue + "')");
				}
				if (configs.size() > 0) {
					dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("config")
							+ " (var,datavalue) VALUES " + Common.implode(configs, ","));
				}
				cacheService.config_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=" + ac);
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		List<Map<String, Object>> values = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("config") + " WHERE var IN (" + vars + ")");
		Map<String, Object> configs = new HashMap<String, Object>();
		String var = null;
		Object dataValue = null;
		for (Map<String, Object> value : values) {
			var = (String) value.get("var");
			dataValue = Common.sHtmlSpecialChars(value.get("datavalue"));
			if ("defaultfusername".equals(var) || "spacebarusername".equals(var)) {
				dataValue = Common.implode(((String) dataValue).split(","), "\r\n");
			}
			configs.put(var, dataValue);
		}

		request.setAttribute("configs", configs);
		return mapping.findForward("hotuser");
	}
}