
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
		menuNames.put("index", "管理首页");
		menuNames.put("config", "站点设置");
		menuNames.put("privacy", "隐私设置");
		menuNames.put("usergroup", "用户组");
		menuNames.put("credit", "积分规则");
		menuNames.put("profilefield", "用户栏目");
		menuNames.put("profield", "群组栏目");
		menuNames.put("eventclass", "活动分类");
		menuNames.put("gift", "礼物设置");
		menuNames.put("magic", "道具设置");
		menuNames.put("task", "有奖任务");
		menuNames.put("spam", "防灌水设置");
		menuNames.put("censor", "词语屏蔽");
		menuNames.put("ad", "广告设置");
		menuNames.put("network", "随便看看");
		menuNames.put("cache", "缓存更新");
		menuNames.put("log", "系统log记录");
		menuNames.put("space", "用户管理");
		menuNames.put("feed", "动态(feed)");
		menuNames.put("share", "分享");
		menuNames.put("blog", "日志");
		menuNames.put("album", "相册");
		menuNames.put("pic", "图片");
		menuNames.put("comment", "评论/留言");
		menuNames.put("thread", "话题");
		menuNames.put("post", "回帖");
		menuNames.put("doing", "记录");
		menuNames.put("tag", "标签");
		menuNames.put("mtag", "群组");
		menuNames.put("poll", "投票");
		menuNames.put("event", "活动");
		menuNames.put("magiclog", "道具记录");
		menuNames.put("report", "举报");
		menuNames.put("block", "数据调用");
		menuNames.put("template", "模板编辑");
		menuNames.put("backup", "数据备份");
		menuNames.put("stat", "统计更新");
		menuNames.put("cron", "系统计划任务");
		menuNames.put("click", "表态动作");
		menuNames.put("ip", "访问IP设置");
		menuNames.put("hotuser", "推荐成员设置");
		menuNames.put("defaultuser", "默认好友设置");
		return menuNames;
	}
}