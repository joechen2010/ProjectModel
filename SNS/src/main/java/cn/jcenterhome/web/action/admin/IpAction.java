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

public class IpAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageip")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		String onlineip = Common.getOnlineIP(request);
		try {
			if (submitCheck(request, "thevaluesubmit")) {
				String ipAccess = Common.addSlashes(request.getParameter("ipaccess").replaceAll(
						"(\\s*(\r\n|\n\r|\n|\r)\\s*)", "\r\n").trim());
				if (!Common.ipAccess(onlineip, ipAccess)) {
					return cpMessage(request, mapping, "cp_ip_is_not_allowed_to_visit_the_area", null, 1,
							onlineip);
				}
				String ipBanned = Common.addSlashes(request.getParameter("ipbanned").replaceAll(
						"(\\s*(\r\n|\n\r|\n|\r)\\s*)", "\r\n").trim());
				if (Common.ipBanned(onlineip, ipBanned)) {
					return cpMessage(request, mapping,
							"cp_the_prohibition_of_the_visit_within_the_framework_of_ip", null, 1, onlineip);
				}

				Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
				List<String> configs = new ArrayList<String>();
				if (!ipAccess.equals(sConfig.get("ipaccess"))) {
					configs.add("('ipaccess','" + ipAccess + "')");
				}
				if (!ipBanned.equals(sConfig.get("ipbanned"))) {
					configs.add("('ipbanned','" + ipBanned + "')");
				}
				if (!configs.isEmpty()) {
					dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("config")
							+ " (var,datavalue) VALUES " + Common.implode(configs, ","));
				}
				cacheService.config_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=ip");
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		Map<String, Object> configs = new HashMap<String, Object>();
		List<Map<String, Object>> values = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("config") + " WHERE var IN ('ipbanned','ipaccess')");
		for (Map<String, Object> value : values) {
			configs.put((String) value.get("var"), Common.sHtmlSpecialChars(value.get("datavalue")));
		}

		request.setAttribute("configs", configs);
		request.setAttribute("onlineip", onlineip);
		return mapping.findForward("ip");
	}
}