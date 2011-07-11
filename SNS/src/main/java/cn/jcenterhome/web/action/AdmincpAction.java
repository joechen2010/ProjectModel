
package cn.jcenterhome.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.CookieHelper;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.vo.MessageVO;


public class AdmincpAction extends BaseAction {
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		request.setAttribute("in_admincp", true);
		request.setAttribute("menuNames", getMenuNames());
		Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
		String collapse = sCookie.get("collapse");
		if (!Common.empty(collapse)) {
			String[] collapses = collapse.split("_");
			for (String val : collapses) {
				if (val.length() > 0) {
					request.setAttribute("menu_style_" + val, " style=\"display: none\"");
					request.setAttribute("menu_img_" + val, "image/plus.gif");
				}
			}
		}
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String ac = request.getParameter("ac");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		String message = Common.checkClose(request, response, supe_uid);
		if (message != null) {
			return showMessage(request, response, message);
		}
		if (supe_uid == 0) {
			String refer = "GET".equals(request.getMethod()) ? (String) request.getAttribute("requestURI")
					: "admincp.jsp?ac=" + ac;
			CookieHelper.setCookie(request, response, "_refer", Common.urlEncode(refer));
			return showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
		}
		Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, supe_uid);
		if (Common.empty(space)) {
			return showMessage(request, response, "space_does_not_exist");
		}
		request.setAttribute("space", space);
		if (Common.checkPerm(request, response, "banvisit")) {
			MessageVO msgVO = Common.ckSpaceLog(request);
			if (msgVO != null) {
				return showMessage(request, response, msgVO);
			} else {
				return showMessage(request, response, "you_do_not_have_permission_to_visit");
			}
		}
		boolean isFounder = Common.ckFounder(supe_uid);
		List<String[]> acs = new ArrayList<String[]>();
		acs.add(new String[] {"index", "config", "privacy", "ip", "spam", "hotuser", "defaultuser",
				"usergroup", "credit", "magic", "magiclog", "profield", "ad"});
		acs.add(new String[] {"tag", "mtag", "event", "report", "space"});
		StringBuffer acs2 = new StringBuffer(
				"cache,network,profilefield,eventclass,gift,click,task,censor,stat,block,cron,log");
		if (isFounder) {
			Map<String, String> jchConfig = JavaCenterHome.jchConfig;
			if (Common.intval(jchConfig.get("allowedittpl")) > 0) {
				acs2.append(",template");
			}
			acs2.append(",backup");
		}
		acs.add(acs2.toString().split(","));
		acs.add(new String[] {"feed", "blog", "album", "pic", "comment", "thread", "post", "doing", "share",
				"poll"});
		request.setAttribute("acs", acs);

		if (Common.empty(ac) || !Common.in_array(acs.get(0), ac) && !Common.in_array(acs.get(1), ac)
				&& !Common.in_array(acs.get(2), ac) && !Common.in_array(acs.get(3), ac)) {
			ac = "index";
		}
		request.setAttribute("ac", ac);
		String refer = (String) sGlobal.get("refer");
		if (!refer.matches(".*admincp\\.jsp.*")) {
			sGlobal.put("refer", "admincp.jsp?ac=" + ac);
		}
		Map<String, Map<String, Integer>> menus = new TreeMap<String, Map<String, Integer>>();
		menus.put("menu0", new HashMap<String, Integer>());
		menus.put("menu1", new HashMap<String, Integer>());
		menus.put("menu2", new HashMap<String, Integer>());
		boolean needLogin = false;
		int groupid = (Integer) ((Map<String, Object>) sGlobal.get("member")).get("groupid");
		Map<String, Object> usergroup = Common.getCacheDate(request, response, "/data/cache/usergroup_"
				+ groupid + ".jsp", "usergroup" + groupid);
		usergroup.put("manageuserapp", usergroup.get("manageapp"));
		for (int i = 0; i < 3; i++) {
			for (String value : acs.get(i)) {
				if (isFounder || (Integer) usergroup.get("manageconfig") > 0
						|| !Common.empty(usergroup.get("manage" + value))) {
					needLogin = true;
					Map<String, Integer> menu = menus.get("menu" + i);
					menu.put(value, 1);
					usergroup.put("manage" + value, 1);
				}
			}
		}
		if (isFounder || (Integer) usergroup.get("managename") > 0
				|| (Integer) usergroup.get("managespacegroup") > 0
				|| (Integer) usergroup.get("managespaceinfo") > 0
				|| (Integer) usergroup.get("managespacecredit") > 0
				|| (Integer) usergroup.get("managespacenote") > 0
				|| (Integer) usergroup.get("managedelspace") > 0) {
			needLogin = true;
			Map<String, Integer> menu = menus.get("menu1");
			menu.put("space", 1);
		}
		request.setAttribute("menus", menus);
		int timestamp = (Integer) sGlobal.get("timestamp");
		int cpAccess = 0;
		if (needLogin) {
			String tableName = JavaCenterHome.getTableName("adminsession");
			List<String> sessions = dataBaseService.executeQuery("SELECT errorcount FROM " + tableName
					+ " WHERE uid=" + supe_uid + " AND dateline+1800>=" + timestamp, 1);
			if (sessions.size() > 0) {
				int errorCount = Integer.valueOf(sessions.get(0));
				if (errorCount == -1) {
					dataBaseService.executeUpdate("UPDATE " + tableName + " SET dateline=" + timestamp
							+ " WHERE uid=" + supe_uid);
					cpAccess = 2;
				} else if (errorCount <= 3) {
					cpAccess = 1;
				}
			} else {
				dataBaseService.executeUpdate("DELETE FROM " + tableName + " WHERE uid=" + supe_uid
						+ " OR dateline+1800<" + timestamp);
				dataBaseService.executeUpdate("INSERT INTO " + tableName
						+ " (uid, ip, dateline, errorcount) VALUES ('" + supe_uid + "', '"
						+ Common.getOnlineIP(request) + "', '" + timestamp + "', '0')");
				cpAccess = 1;
			}
		} else {
			cpAccess = 2;
		}
		switch (cpAccess) {
			case 1:
				try {
					if (submitCheck(request, "loginsubmit")) {
						String tableName = JavaCenterHome.getTableName("adminsession");
						List<Map<String, Object>> members = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("member") + " WHERE username = '"
								+ sGlobal.get("supe_username") + "'");
						if (members.isEmpty()) {
							return showMessage(request, response, "login_failure_please_re_login",
									"do.jsp?ac=" + sConfig.get("login_action"));
						}
						Map<String, Object> member = members.get(0);
						String password = Common.trim(request.getParameter("password"));
						password = Common.md5(Common.md5(password) + member.get("salt"));
						if (!password.equals(member.get("password"))) {
							dataBaseService.executeUpdate("UPDATE " + tableName
									+ " SET errorcount=errorcount+1 WHERE uid=" + supe_uid);
							return cpMessage(request, mapping, "cp_enter_the_password_is_incorrect",
									"admincp.jsp");
						} else {
							dataBaseService.executeUpdate("UPDATE " + tableName
									+ " SET errorcount=-1 WHERE uid=" + supe_uid);
							refer = sCookie.get("_refer");
							refer = Common.empty(refer) ? (String) sGlobal.get("refer") : Common
									.urlDecode(refer);
							if (Common.empty(refer) || Common.matches(refer, "(?i)(login)")) {
								refer = "admincp.jsp";
							}
							CookieHelper.removeCookie(request, response, "_refer");
							return showMessage(request, response, "login_success", refer, 0);
						}
					} else {
						refer = "GET".equals(request.getMethod()) ? (String) request
								.getAttribute("requestURI") : "admincp.jsp?ac=" + ac;
						CookieHelper.setCookie(request, response, "_refer", Common.urlEncode(refer));
						request.setAttribute("active_advance", " class=\"active\"");
						return include(request, response, sConfig, sGlobal, "cp_advance.jsp");
					}
				} catch (Exception e) {
					return showMessage(request, response, e.getMessage());
				}
			case 2:

				break;
			default:
				return cpMessage(request, mapping, "cp_excessive_number_of_attempts_to_sign");
		}
		if (needLogin) {
			admincpLog(request);
		}

		String acfile = null;
		if (ac.equals("defaultuser")) {
			acfile = "hotuser";
		} else {
			acfile = ac;
		}
		sGlobal.put("maxpage", 0);
		request.removeAttribute("globalAd");
		try {
			request.getRequestDispatcher("/admin/" + acfile + ".do").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void admincpLog(HttpServletRequest request) {
		StringBuffer logContent = new StringBuffer();
		Map<String, String[]> datas = request.getParameterMap();
		if (datas.size() > 0) {
			logContent.append(request.getMethod());
			logContent.append('{');
			Set<String> keys = datas.keySet();
			for (String key : keys) {
				String[] values = datas.get(key);
				int length = values.length;
				if (length > 1) {
					Map temp = new TreeMap();
					for (int i = 0; i < length; i++) {
						temp.put(i, values[i]);
					}
					logContent.append(key.replace("[]", "") + "=" + Serializer.serialize(temp) + ";");
				} else {
					logContent.append(key + "=" + values[0] + ";");
				}
			}
			logContent.append('}');
		}
		FileHelper.writeLog(request, "admincp", logContent.toString());
	}

	
	private Map<String, String> getMenuNames() {
		Map<String, String> menuNames = new HashMap<String, String>();
		menuNames.put("index", "������ҳ");
		menuNames.put("config", "վ������");
		menuNames.put("privacy", "��˽����");
		menuNames.put("usergroup", "�û���");
		menuNames.put("credit", "���ֹ���");
		menuNames.put("profilefield", "�û���Ŀ");
		menuNames.put("profield", "Ⱥ����Ŀ");
		menuNames.put("eventclass", "�����");
		menuNames.put("gift", "��������");
		menuNames.put("magic", "��������");
		menuNames.put("task", "�н�����");
		menuNames.put("spam", "����ˮ����");
		menuNames.put("censor", "��������");
		menuNames.put("ad", "�������");
		menuNames.put("network", "��㿴��");
		menuNames.put("cache", "�������");
		menuNames.put("log", "ϵͳlog��¼");
		menuNames.put("space", "�û�����");
		menuNames.put("feed", "��̬(feed)");
		menuNames.put("share", "����");
		menuNames.put("blog", "��־");
		menuNames.put("album", "���");
		menuNames.put("pic", "ͼƬ");
		menuNames.put("comment", "����/����");
		menuNames.put("thread", "����");
		menuNames.put("post", "����");
		menuNames.put("doing", "��¼");
		menuNames.put("tag", "��ǩ");
		menuNames.put("mtag", "Ⱥ��");
		menuNames.put("poll", "ͶƱ");
		menuNames.put("event", "�");
		menuNames.put("magiclog", "���߼�¼");
		menuNames.put("report", "�ٱ�");
		menuNames.put("block", "���ݵ���");
		menuNames.put("template", "ģ��༭");
		menuNames.put("backup", "���ݱ���");
		menuNames.put("stat", "ͳ�Ƹ���");
		menuNames.put("cron", "ϵͳ�ƻ�����");
		menuNames.put("click", "��̬����");
		menuNames.put("ip", "����IP����");
		menuNames.put("hotuser", "�Ƽ���Ա����");
		menuNames.put("defaultuser", "Ĭ�Ϻ�������");
		return menuNames;
	}
}