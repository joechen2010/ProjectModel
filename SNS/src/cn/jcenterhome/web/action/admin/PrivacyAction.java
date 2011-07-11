package cn.jcenterhome.web.action.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.web.action.BaseAction;


public class PrivacyAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageconfig")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		try {
			if (submitCheck(request, "thevaluesubmit")) {
				Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

				List<String> configs = new ArrayList<String>();
				Map<String, Map<String, Integer>> privacys = new HashMap<String, Map<String, Integer>>();
				Map<String, Integer> view = new HashMap<String, Integer>();
				Map<String, Integer> feed = new HashMap<String, Integer>();
				privacys.put("view", view);
				privacys.put("feed", feed);
				Map<String, String[]> values = request.getParameterMap();
				Set<String> keys = values.keySet();

				String var = null;
				String value = null;
				for (String key : keys) {
					var = key.replaceAll("(.*\\[)|(\\])", "");
					value = values.get(key)[0].trim();
					if (key.startsWith("config[")) {
						if (!sConfig.containsKey(var) || !value.equals(sConfig.get(var))) {
							configs.add("('" + var + "','" + value + "')");
						}
					} else if (key.startsWith("view[")) {
						view.put(var, Common.intval(value));
					} else if (key.startsWith("feed[")) {
						feed.put(var, 1);
					}
				}

				configs.add("('privacy','" + Common.addSlashes(Serializer.serialize(privacys)) + "')");
				dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("config")
						+ " (var,datavalue) VALUES " + Common.implode(configs, ","));
				cacheService.config_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=privacy");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		List<Map<String, Object>> configs = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("config") + " where var IN ('networkpublic','privacy')");
		for (Map<String, Object> config : configs) {
			if ("privacy".equals(config.get("var"))) {
				Map<String, Map<String, Integer>> privacy = Serializer.unserialize((String) config
						.get("datavalue"), true);
				Map<String, Integer> view = privacy.get("view");
				Map<String, Integer> feed = privacy.get("feed");

				Set<String> keys = view.keySet();
				for (String key : keys) {
					request.setAttribute("view_" + key + view.get(key), " selected");
				}
				keys = feed.keySet();
				for (String key : keys) {
					request.setAttribute("feed_" + key, " checked");
				}
			} else {
				request.setAttribute("networkpublic" + config.get("datavalue"), " checked");
			}
		}
		return mapping.findForward("privacy");
	}
}