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


public class SpamAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageconfig")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		try {
			if (submitCheck(request, "spamsubmit")) {
				String[] questionArr = request.getParameterValues("data[question]");
				String[] answerArr = request.getParameterValues("data[answer]");

				List<String> questions = new ArrayList<String>();
				List<String> answers = new ArrayList<String>();
				for (int i = 0; i < questionArr.length; i++) {
					if (!Common.empty(questionArr[i]) && !Common.empty(answerArr[i])) {
						questions.add(questionArr[i]);
						answers.add(answerArr[i]);
					}
				}

				Map<String, Object> spams = new HashMap<String, Object>();
				spams.put("question", questions);
				spams.put("answer", answers);
				Common.setData("spam", spams, false);
				List<String> configs = new ArrayList<String>();
				Map<String, String[]> elements = request.getParameterMap();
				Set<String> elementNames = elements.keySet();
				String var = null;
				String value = null;
				boolean registerFlag = true;
				boolean loginFlag = true;
				for (String elementName : elementNames) {
					var = elementName.replaceAll("(.*\\[)|(\\])", "");
					value = elements.get(elementName)[0].trim();
					if (elementName.startsWith("config[")) {
						if ("seccode_register".equals(var)) {
							registerFlag = false;
							if (Common.empty(value)) {
								value = "0";
							}
						} else if ("seccode_login".equals(var)) {
							loginFlag = false;
							if (Common.empty(value)) {
								value = "0";
							}
						} else if ("questionmode".equals(var) && questions.isEmpty()) {
							value = "0";
						} else if ("login_action".equals(var) || "register_action".equals(var)) {
							if (!value.matches("[a-zA-z]*")) {
								value = "";
							}
						} else if ("newusertime".equals(var) || "need_friendnum".equals(var)
								|| "pmsendregdays".equals(var) || "pmfloodctrl".equals(var)
								|| "pmlimit1day".equals(var)) {
							value = String.valueOf(Common.range(value, 2147483647, 0));
						}
						configs.add("('" + var + "','" + value + "')");
					}
				}

				if (registerFlag) {
					configs.add("('seccode_register','" + 0 + "')");
				}
				if (loginFlag) {
					configs.add("('seccode_login','" + 0 + "')");
				}

				if (!configs.isEmpty()) {
					dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("config")
							+ " (var, datavalue) VALUES " + Common.implode(configs, ","));
				}
				cacheService.config_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=spam", 1);
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		Map<String, Object> configs = new HashMap<String, Object>();
		String sql = "SELECT * FROM "
				+ JavaCenterHome.getTableName("config")
				+ " WHERE var IN ('seccode_login','need_email','uniqueemail','seccode_register','questionmode','newusertime','register_action','need_avatar','need_friendnum','login_action','spam','pmsendregdays','pmfloodctrl','pmlimit1day')";
		List<Map<String, Object>> values = dataBaseService.executeQuery(sql);

		for (Map<String, Object> value : values) {
			configs.put((String) value.get("var"), Common.sHtmlSpecialChars(value.get("datavalue")));
		}

		Map<String, Object> spams = Serializer.unserialize(Common.getData("spam"), false);

		request.setAttribute("configs", configs);
		request.setAttribute("questions", spams.get("question"));
		request.setAttribute("answers", spams.get("answer"));
		return mapping.findForward("spam");
	}
}