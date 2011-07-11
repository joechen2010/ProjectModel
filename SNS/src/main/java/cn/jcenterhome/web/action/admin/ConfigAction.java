package cn.jcenterhome.web.action.admin;

import java.io.File;
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


public class ConfigAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageconfig")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		try {
			if (submitCheck(request, "thevaluesubmit")) {

				List<String> configs = new ArrayList<String>();
				List<String> dataSets = new ArrayList<String>();
				Map<String, String> datas = new HashMap<String, String>();
				Map<String, String> mails = new HashMap<String, String>();
				Map<String, String[]> values = request.getParameterMap();
				Set<String> keys = values.keySet();
				String var = null, value = null;
				for (String key : keys) {
					var = key.replaceAll("(.*\\[)|(\\])", "");
					value = values.get(key)[0].trim();
					if (key.startsWith("config[")) {
						if ("newspaceavatar".equals(var) || "newspacerealname".equals(var)
								|| "newspacevideophoto".equals(var) || "videophoto".equals(var)) {
							configs.add("('" + var + "','" + Common.intval(value) + "')");
						} else if ("regipdate".equals(var) || "feedday".equals(var)
								|| "feedhotday".equals(var) || "topcachetime".equals(var)
								|| "sendmailday".equals(var)) {
							if (!Common.isNumeric(value) || value.startsWith("-")) {
								value = "0";
							}
							configs.add("('" + var + "','" + value + "')");
						} else if ("onlinehold".equals(var) || "maxpage".equals(var)
								|| "showallfriendnum".equals(var) || "newspacenum".equals(var)
								|| "feedhotmin".equals(var) || "groupnum".equals(var)
								|| "importnum".equals(var) || "maxreward".equals(var)
								|| "jc_tagrelatedtime".equals(var)) {
							configs.add("('" + var + "','" + Common.range(value, 2147483647, 0) + "')");
						} else if ("feedmaxnum".equals(var)) {
							configs.add("('" + var + "','" + Common.range(value, 2147483647, 50) + "')");
						} else if ("starcredit".equals(var) || "starlevelnum".equals(var)) {
							configs.add("('" + var + "','" + Common.range(value, 2147483647, 2) + "')");
						} else if ("feedhotnum".equals(var)) {
							configs.add("('" + var + "','" + Common.range(value, 10, 0) + "')");
						} else {
							configs.add("('" + var + "','" + value + "')");
						}
					} else if (key.startsWith("dataset[")) {
						dataSets.add("('" + var + "','" + value + "')");
					} else if (key.startsWith("data[")) {
						if ("thumbwidth".equals(var) || "thumbheight".equals(var)
								|| "maxthumbwidth".equals(var) || "maxthumbheight".equals(var)
								|| "ftpport".equals(var) || "ftptimeout".equals(var)) {
							datas.put(var, String.valueOf(Common.range(value, 2147483647, 0)));
						} else {
							datas.put(var, Common.stripSlashes(value).trim());
						}
					} else if (key.startsWith("mail[")) {
						if ("port".equals(var)) {
							mails.put(var, String.valueOf(Common.range(value, 214783647, 0)));
						} else {
							mails.put(var, Common.stripSlashes(value).trim());
						}
					}
				}
				if (!configs.isEmpty()) {
					dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("config")
							+ " (var, datavalue) VALUES " + Common.implode(configs, ","));
				}
				if (!dataSets.isEmpty()) {
					dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("data")
							+ " (var, datavalue) VALUES " + Common.implode(dataSets, ","));
				}
				Common.setData("setting", datas, false);
				Common.setData("mail", mails, false);
				cacheService.config_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=config");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		Map<String, Object> configs = new HashMap<String, Object>();
		List<Map<String, Object>> values = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("config"));
		for (Map<String, Object> value : values) {
			configs.put((String) value.get("var"), Common.sHtmlSpecialChars(value.get("datavalue")));
		}

		if (Common.intval((String) configs.get("feedfilternum")) < 1) {
			configs.put("feedfilternum", 1);
		}
		Map<String, Object> dataSets = new HashMap<String, Object>();
		values = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("data"));
		for (Map<String, Object> value : values) {
			String var = (String) value.get("var");
			if ("setting".equals(var) || "mail".equals(var)) {
				dataSets.put(var, Serializer.unserialize((String) value.get("datavalue"), false));
			} else {
				dataSets.put(var, Common.sHtmlSpecialChars(value.get("datavalue")));
			}
		}

		Map<String, String> datas = (Map<String, String>) dataSets.get("setting");
		Map<String, String> mails = (Map<String, String>) dataSets.get("mail");
		List<String> templates = new ArrayList<String>();
		templates.add("default");
		File tplDir = new File(JavaCenterHome.jchRoot + "template");
		if (tplDir.exists()) {
			File[] dirs = tplDir.listFiles();
			for (File dir : dirs) {
				if (!"default".equals(dir.getName()) && new File(dir.getPath() + "/style.css").exists()) {
					templates.add(dir.getName());
				}
			}
		}

		request.setAttribute("templates", templates);
		request.setAttribute("configs", configs);
		request.setAttribute("datasets", dataSets);
		request.setAttribute("datas", datas);
		request.setAttribute("mails", mails);
		request.setAttribute("timeZoneIDs", Common.getTimeZoneIDs());
		return mapping.findForward("config");
	}
}