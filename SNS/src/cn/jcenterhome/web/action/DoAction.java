package cn.jcenterhome.web.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.CookieHelper;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.FileUploadUtil;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Mail;


public class DoAction extends BaseAction {
	private String[] acs = {"login", "register", "lostpasswd", "swfupload", "inputpwd", "ajax", "seccode",
			"sendmail", "stat", "emailcheck"};

	@SuppressWarnings("unchecked")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String ac = request.getParameter("ac");
		if (sConfig.get("login_action").equals(ac)) {
			ac = "login";
		} else if (sConfig.get("register_action").equals(ac)) {
			ac = "register";
		} else if ("login".equals(ac) || "register".equals(ac)) {
			ac = null;
		}

		if (Common.empty(ac) || !Common.in_array(acs, ac)) {
			return showMessage(request, response, "enter_the_space", "index.jsp", 0);
		}
		request.setAttribute("theUrl", "do.jsp?ac=" + ac);
		return invokeMethod(this, "do_" + ac, request, response);
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward do_ajax(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String op = request.getParameter("op");
		if ("comment".equals(op)) {
			int cid = Common.empty(request.getParameter("cid")) ? 0 : Common.intval(request
					.getParameter("cid"));
			int ajaxEdit = 0;
			String cidSql = null;
			if (cid != 0) {
				cidSql = "cid='" + cid + "' AND";
				ajaxEdit = 1;
			} else {
				cidSql = "";
				ajaxEdit = 0;
			}
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("comment") + " WHERE " + cidSql + " authorid='"
					+ sGlobal.get("supe_uid") + "' ORDER BY dateline DESC LIMIT 0,1");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			for (Map<String, Object> value : query) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"), (String) value
						.get("author"), "", 0);
			}
			Common
					.realname_get(sGlobal, sConfig, sNames, (Map<String, Object>) request
							.getAttribute("space"));

			request.setAttribute("list", query);
			request.setAttribute("ajax_edit", ajaxEdit);
		} else if ("getfriendgroup".equals(op)) {
			int uid = Common.intval(request.getParameter("uid"));
			Map<String, Object> friend = null;
			if (!Common.empty(sGlobal.get("supe_uid")) && uid != 0) {
				Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, sGlobal
						.get("supe_uid"));
				List<Map<String, Object>> friendList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + sGlobal.get("supe_uid")
						+ "' AND fuid='" + uid + "'");
				if (!friendList.isEmpty()) {
					friend = friendList.get(0);
				}
				request.setAttribute("space", space);
			}
			Map<Integer, String> groups = Common.getFriendGroup(request);
			if (friend == null || Common.empty(friend.get("gid"))) {
				friend = friend == null ? new HashMap<String, Object>() : friend;
				friend.put("gid", 0);
			}
			request.setAttribute("group", groups.get(friend.get("gid")));
		} else if ("getfriendname".equals(op)) {
			int group = Common.intval(request.getParameter("group"));
			if (!Common.empty(sGlobal.get("supe_uid")) && group != 0) {
				Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, sGlobal
						.get("supe_uid"));
				request.setAttribute("space", space);
				Map<Integer, String> groups = Common.getFriendGroup(request);
				request.setAttribute("groupname", groups.get(group));
			}
		} else if ("getmtagmember".equals(op)) {

		} else if ("share".equals(op)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("share") + " WHERE uid='" + sGlobal.get("supe_uid")
					+ "' ORDER BY dateline DESC LIMIT 0,1");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			Map<String, Object> share = query.size() > 0 ? query.get(0) : null;
			if (!Common.empty(share)) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) share.get("uid"), (String) share
						.get("username"), "", 0);
				Common.mkShare(share);
			}
			Common
					.realname_get(sGlobal, sConfig, sNames, (Map<String, Object>) request
							.getAttribute("space"));
			request.setAttribute("share", share);

		} else if ("post".equals(op)) {
			int pid = Common.intval(request.getParameter("pid"));
			String pidSQL = null;
			if (pid > 0) {
				pidSQL = " WHERE pid='" + pid + "'";
			} else {
				pidSQL = "";
				request.setAttribute("ajax_edit", 0);
			}
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("post") + " " + pidSQL
					+ " ORDER BY dateline DESC LIMIT 0,1");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (!list.isEmpty()) {
				Map<String, Object> postValue = list.get(0);
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) postValue.get("uid"),
						(String) postValue.get("username"), null, 0);
				request.setAttribute("postValue", postValue);
			}
			Common
					.realname_get(sGlobal, sConfig, sNames, (Map<String, Object>) request
							.getAttribute("space"));

		} else if ("album".equals(op)) {
			int id = Common.empty(request.getParameter("id")) ? 0 : Common.intval(request.getParameter("id"));
			int start = Common.empty(request.getParameter("start")) ? 0 : Common.intval(request
					.getParameter("start"));

			if (Common.empty(sGlobal.get("supe_uid"))) {
				return showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
			}

			int count = 0;
			int perPage = 10;
			String ret = Common.ckStart(start, perPage, (Integer) sConfig.get("maxpage"));
			if (ret != null) {
				return showMessage(request, response, ret);
			}

			List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("pic") + " WHERE albumid='" + id + "' AND uid='"
					+ sGlobal.get("supe_uid") + "' ORDER BY dateline DESC LIMIT " + start + "," + perPage);
			for (Map<String, Object> value : picList) {
				value.put("bigpic", Common.pic_get(sConfig, (String) value.get("filepath"), (Integer) value
						.get("thumb"), (Integer) value.get("remote"), false));
				value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"), (Integer) value
						.get("thumb"), (Integer) value.get("remote"), true));
				count++;
			}

			try {
				String multi = Common.smulti(sGlobal, start, perPage, count, "do.jsp?ac=ajax&op=album&id="
						+ id, request.getParameter("ajaxdiv"));
				request.setAttribute("multi", multi);
				request.setAttribute("piclist", picList);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}

		} else if ("docomment".equals(op)) {
		} else if ("deluserapp".equals(op)) {
		} else if ("getreward".equals(op)) {
			Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
			String reward_log = sCookie.get("reward_log");
			if (reward_log != null) {
				String[] logs = reward_log.split(",");
				if (logs.length == 2) {
					int clid = Common.intval(logs[1]);
					if (clid > 0) {
						Map<String, Map<String, Object>> globalCreditrule = Common.getCacheDate(request,
								response, "/data/cache/cache_creditrule.jsp", "globalCreditrule");
						List<String> cyclenums = dataBaseService.executeQuery("SELECT cyclenum FROM "
								+ JavaCenterHome.getTableName("creditlog") + " WHERE clid=" + clid, 1);
						int cyclenum = cyclenums.size() > 0 ? Integer.parseInt(cyclenums.get(0)) : 0;
						Map<String, Object> rule = globalCreditrule.get(logs[0]);
						int rewardnum = (Integer) rule.get("rewardnum");
						rule.put("cyclenum", rewardnum > 0 ? rewardnum - cyclenum : 0);
						request.setAttribute("rule", rule);
					}
				}
				CookieHelper.removeCookie(request, response, "reward_log");
			}
		}
		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "do_ajax.jsp");
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward do_emailcheck(HttpServletRequest request, HttpServletResponse response) {
		int uid = 0;
		String email = null;
		String hash = Common.trim(request.getParameter("hash"));
		if (!Common.empty(hash)) {
			String[] list = Common.authCode(hash, "DECODE", null, 0).split("\t");
			if (list.length == 2) {
				uid = Common.intval(list[0]);
				email = list[1];
			}
		}
		if (uid > 0 && Common.isEmail(email)) {
			Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
			if ((Integer) sConfig.get("uniqueemail") == 1) {
				int count = dataBaseService.findRows("SELECT * FROM "
						+ JavaCenterHome.getTableName("spacefield") + " WHERE email = '" + email
						+ "' AND emailcheck = 1");
				if (count > 0) {
					return showMessage(request, response, "uniqueemail_recheck");
				}
			}
			Common.getReward("realemail", true, uid, "", true, request, response);
			Map<String, Object> setData = new HashMap<String, Object>();
			setData.put("email", Common.addSlashes(email));
			setData.put("emailcheck", 1);
			setData.put("newemail", "");
			Map<String, Object> whereData = new HashMap<String, Object>();
			whereData.put("uid", uid);
			dataBaseService.updateTable("spacefield", setData, whereData);
			return showMessage(request, response, "email_check_sucess", "", 1, email);
		} else {
			return showMessage(request, response, "email_check_error");
		}
	}

	
	public ActionForward do_inputpwd(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (submitCheck(request, "pwdsubmit")) {
				int blogId = Common.intval(request.getParameter("blogid"));
				int albumId = Common.intval(request.getParameter("albumid"));
				Map<String, Object> item = null;
				String itemUrl = null;
				String cookieName = null;
				if (blogId > 0) {
					List<Map<String, Object>> items = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("blog") + " WHERE blogid=" + blogId);
					if (items.size() > 0) {
						item = items.get(0);
						itemUrl = "space.jsp?uid=" + item.get("uid") + "&do=blog&id=" + blogId;
						cookieName = "view_pwd_blog_" + blogId;
					}
				} else if (albumId > 0) {
					List<Map<String, Object>> items = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("album") + " WHERE albumid=" + albumId);
					if (items.size() > 0) {
						item = items.get(0);
						itemUrl = "space.jsp?uid=" + item.get("uid") + "&do=album&id=" + albumId;
						cookieName = "view_pwd_album_" + albumId;
					}
				}
				if (Common.empty(item)) {
					return showMessage(request, response, "news_does_not_exist");
				}
				String password = (String) item.get("password");
				if (!Common.empty(password) && password.equals(request.getParameter("viewpwd"))) {
					CookieHelper.setCookie(request, response, cookieName, Common.md5(Common.md5(password)));
					return showMessage(request, response, "proved_to_be_successful", itemUrl);
				} else {
					return showMessage(request, response, "password_is_not_passed", itemUrl);
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward do_login(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if (supe_uid > 0) {
			return showMessage(request, response, "do_success", "space.jsp", 0);
		}
		Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
		String refer = request.getParameter("refer");
		if (Common.empty(refer)) {
			refer = sCookie.get("_refer");
			refer = Common.empty(refer) ? "" : Common.urlDecode(refer);
		}
		List<String> ms = Common.pregMatch(refer, "(?i)(admincp|do|cp)\\.jsp\\?ac\\=([a-z]+)");
		if (ms.size() == 3) {
			if (!"cp".equals(ms.get(1)) || !"sendmail".equals(ms.get(2))) {
				refer = null;
			}
		}
		if (Common.empty(refer)) {
			refer = "space.jsp?do=home";
		}
		request.setAttribute("refer", refer);
		int uid = Common.intval(request.getParameter("uid"));
		String code = Common.trim(request.getParameter("code"));
		int app = Common.intval(request.getParameter("app"));
		String invite = Common.trim(request.getParameter("invite"));
		Map<String, Object> invits = null;
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		if (uid > 0 && code.length() > 0) {
			Map<String, Integer> reward = Common.getReward("invitecode", false, 0, "", true, request,
					response);
			if (reward.get("credit") == 0) {
				Map<String, Object> mSpace = Common.getSpace(request, sGlobal, sConfig, uid);
				if (code.equals(Common.spaceKey(mSpace, sConfig, app))) {
					invits = new HashMap<String, Object>();
					invits.put("id", 0);
					invits.put("uid", uid);
					invits.put("username", mSpace.get("username"));
				}
				request.setAttribute("url_plus", "uid=" + uid + "&app=" + app + "&code=" + code);
			}
		} else if (uid > 0 && invite.length() > 0) {
			invits = cpService.getInvite(sGlobal, sConfig, sNames, uid, invite);
			request.setAttribute("url_plus", "uid=" + uid + "&invite=" + invite);
		}
		sGlobal.put("nologinform", 1);
		int seccode_login = (Integer) sConfig.get("seccode_login");
		try {
			if (submitCheck(request, "loginsubmit")) {
				String userName = request.getParameter("username");
				if (Common.empty(userName)) {
					return showMessage(request, response, "users_were_not_empty_please_re_login",
							"do.jsp?ac=" + sConfig.get("login_action"));
				}
				String password = request.getParameter("password");
				int cookieTime = Common.intval(request.getParameter("cookietime"));
				if (seccode_login == 1
						&& !cpService.checkSeccode(request, response, sGlobal, sConfig, request
								.getParameter("seccode"))) {
					sGlobal.put("input_seccode", 1);
					request.setAttribute("invits", invits);
					request.setAttribute("memberName", userName);
					request.setAttribute("password", password);
					if (cookieTime > 0) {
						request.setAttribute("cookieCheck", " checked");
					}
					return include(request, response, sConfig, sGlobal, "do_login.jsp");
				}

				List<Map<String, Object>> members = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("member") + " WHERE username = '" + userName + "'");
				if (members.isEmpty()) {
					return showMessage(request, response, "login_failure_please_re_login", "do.jsp?ac="
							+ sConfig.get("login_action"));
				}
				Map<String, Object> member = members.get(0);
				password = Common.md5(Common.md5(password) + member.get("salt"));
				if (!password.equals(member.get("password"))) {
					return showMessage(request, response, "login_failure_please_re_login", "do.jsp?ac="
							+ sConfig.get("login_action"));
				}

				List<Map<String, Object>> spaces = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("space") + " WHERE uid=" + member.get("uid"));
				Map<String, Object> space = null;
				if (spaces.isEmpty()) {
					space = spaceService.openSpace(request, response, sGlobal, sConfig, (Integer) member
							.get("uid"), (String) member.get("username"), 0, "");
				} else {
					space = spaces.get(0);
				}
				sGlobal.put("member", space);
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) space.get("uid"), (String) space
						.get("username"), (String) space.get("name"), (Integer) space.get("namestatus"));

				spaceService.insertSession(request, response, sGlobal, sConfig, (Integer) member.get("uid"),
						(String) member.get("username"), (String) member.get("password"));

				CookieHelper.setCookie(request, response, "auth", Common.authCode(member.get("password")
						+ "\t" + member.get("uid"), "ENCODE", null, 0), cookieTime == 0 ? -1 : cookieTime);
				CookieHelper.setCookie(request, response, "loginuser", (String) member.get("username"),
						31536000);
				CookieHelper.removeCookie(request, response, "_refer");
				if (invits != null) {
					cpService.updateInvite(request, response, sGlobal, sConfig, sNames, (Integer) invits
							.get("id"), (Integer) member.get("uid"), (String) member.get("username"),
							(Integer) invits.get("uid"), (String) invits.get("username"), app);
				}
				sGlobal.put("supe_uid", space.get("uid"));

				Map<String, Object> setData = new HashMap<String, Object>();
				boolean avatarExists = cpService.ckavatar(sGlobal, sConfig, (Integer) space.get("uid"));
				int avatar = (Integer) space.get("avatar");
				if (avatarExists) {
					if (avatar == 0) {
						Map<String, Integer> reward = Common.getReward("setavatar", false, 0, "", true,
								request, response);
						int credit = reward.get("credit");
						int experience = reward.get("experience");
						if (credit > 0) {
							setData.put("credit", "credit=credit+" + credit);
						}
						if (experience > 0) {
							setData.put("experience", "experience=experience+" + experience);
						}
						setData.put("avatar", "avatar=1");
						setData.put("updatetime", "updatetime=" + sGlobal.get("timestamp"));
					}
				} else if (avatar > 0) {
					setData.put("avatar", "avatar=0");
				}
				if (setData.size() > 0) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
							+ Common.implode(setData, ",") + " WHERE uid='" + space.get("uid") + "'");
				}
				Common.realname_get(sGlobal, sConfig, sNames, space);
				return showMessage(request, response, "login_success", app > 0 ? "userapp.jsp?id=" + app
						: refer, 1, "");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		String loginUser = sCookie.get("loginuser");
		if (!Common.empty(loginUser)) {
			request.setAttribute("memberName", Common.stripSlashes(loginUser));
		}
		request.setAttribute("cookieCheck", " checked");
		request.setAttribute("invits", invits);
		request.setAttribute("formhash", formHash(request));
		return include(request, response, sConfig, sGlobal, "do_login.jsp");
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward do_lostpasswd(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String op = Common.trim(request.getParameter("op"));
		try {
			if (submitCheck(request, "lostpwsubmit")) {
				List<Map<String, Object>> spaceInfos = dataBaseService
						.executeQuery("SELECT s.uid, s.groupid, s.username, s.flag, sf.email, sf.emailcheck FROM "
								+ JavaCenterHome.getTableName("space")
								+ " s LEFT JOIN "
								+ JavaCenterHome.getTableName("spacefield")
								+ " sf ON sf.uid=s.uid WHERE s.username='"
								+ request.getParameter("username")
								+ "'");
				if (spaceInfos.isEmpty()) {
					return showMessage(request, response, "getpasswd_account_notmatch");
				}
				Map<String, Object> spaceInfo = spaceInfos.get(0);
				String email = (String) spaceInfo.get("email");
				if (Common.empty(email) || !Common.isEmail(email)) {
					return showMessage(request, response, "getpasswd_account_notmatch");
				}
				String[] founder = JavaCenterHome.jchConfig.get("founder").split(",");
				int flag = (Integer) spaceInfo.get("flag");
				if (flag > 0 || Common.in_array(founder, spaceInfo.get("uid"))
						|| Common.checkPerm(request, response, "admin")) {
					return showMessage(request, response, "getpasswd_account_invalid");
				}
				op = "email";
				request.setAttribute("username", spaceInfo.get("username"));
				request.setAttribute("email", email.substring(email.indexOf("@")));
			} else if (submitCheck(request, "emailsubmit")) {
				List<Map<String, Object>> spaceInfos = dataBaseService
						.executeQuery("SELECT s.uid, s.groupid, s.username, s.flag, sf.email, sf.emailcheck FROM "
								+ JavaCenterHome.getTableName("space")
								+ " s LEFT JOIN "
								+ JavaCenterHome.getTableName("spacefield")
								+ " sf ON sf.uid=s.uid WHERE s.username='"
								+ request.getParameter("username")
								+ "'");
				if (spaceInfos.isEmpty()) {
					return showMessage(request, response, "getpasswd_email_notmatch");
				}
				Map<String, Object> spaceInfo = spaceInfos.get(0);
				String email = (String) spaceInfo.get("email");
				if (Common.empty(email) || !email.equals(request.getParameter("email"))) {
					return showMessage(request, response, "getpasswd_email_notmatch");
				}
				String[] founder = JavaCenterHome.jchConfig.get("founder").split(",");
				int flag = (Integer) spaceInfo.get("flag");
				if (flag > 0 || Common.in_array(founder, spaceInfo.get("uid"))
						|| Common.checkPerm(request, response, "admin")) {
					return showMessage(request, response, "getpasswd_account_invalid");
				}

				String idString = Common.getRandStr(6, false);
				String reSetURL = Common.getSiteUrl(request) + "do.jsp?ac=lostpasswd&amp;op=reset&amp;uid="
						+ spaceInfo.get("uid") + "&amp;id=" + idString;
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("authstr", sGlobal.get("timestamp") + "\t1\t" + idString);
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("uid", spaceInfo.get("uid"));
				dataBaseService.updateTable("spacefield", setData, whereData);
				String mailSubject = Common.getMessage(request, "cp_get_passwd_subject");
				String mailMessage = Common.getMessage(request, "cp_get_passwd_message", reSetURL);
				cpService.sendMail(request, response, 0, email, mailSubject, mailMessage, null);
				return showMessage(request, response, "getpasswd_send_succeed", "do.jsp?ac="
						+ sConfig.get("login_action"), 5);
			} else if (submitCheck(request, "resetsubmit")) {
				int uid = Common.intval(request.getParameter("uid"));
				String id = Common.trim(request.getParameter("id"));
				String newPassword1 = Common.trim(request.getParameter("newpasswd1"));
				String newPassword2 = Common.trim(request.getParameter("newpasswd2"));
				if (!newPassword1.equals(newPassword2)) {
					return showMessage(request, response, "password_inconsistency");
				}
				if (!newPassword1.equals(Common.addSlashes(newPassword2))) {
					return showMessage(request, response, "profile_passwd_illegal");
				}
				List<Map<String, Object>> spaceInfos = dataBaseService
						.executeQuery("SELECT s.uid, s.username, s.groupid, s.flag, sf.email, sf.authstr FROM "
								+ JavaCenterHome.getTableName("space")
								+ " s, "
								+ JavaCenterHome.getTableName("spacefield")
								+ " sf WHERE s.uid="
								+ uid
								+ " AND sf.uid=s.uid");
				Map<String, Object> space = null;
				if (!spaceInfos.isEmpty()) {
					space = spaceInfos.get(0);
				}
				String result = checkUser(sGlobal, id, space);
				if (result != null) {
					return showMessage(request, response, result);
				}
				String[] founder = JavaCenterHome.jchConfig.get("founder").split(",");
				int flag = (Integer) space.get("flag");
				if (flag > 0 || Common.in_array(founder, space.get("uid"))
						|| Common.checkPerm(request, response, "admin")) {
					return showMessage(request, response, "reset_passwd_account_invalid");
				}
				String salt = Common.getRandStr(6, false);
				newPassword1 = Common.md5(Common.md5(newPassword1) + salt);
				Map<String, Object> memberData = new HashMap<String, Object>();
				memberData.put("password", newPassword1);
				memberData.put("salt", salt);
				Map<String, Object> spaceFieldData = new HashMap<String, Object>();
				spaceFieldData.put("authstr", "");
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("uid", uid);
				dataBaseService.updateTable("member", memberData, whereData);
				dataBaseService.updateTable("spacefield", spaceFieldData, whereData);
				return showMessage(request, response, "getpasswd_succeed");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}

		if ("reset".equals(op)) {
			List<Map<String, Object>> spaceInfos = dataBaseService
					.executeQuery("SELECT s.username, sf.email, sf.authstr FROM "
							+ JavaCenterHome.getTableName("space") + " s, "
							+ JavaCenterHome.getTableName("spacefield") + " sf WHERE s.uid='"
							+ request.getParameter("uid") + "' AND sf.uid=s.uid");
			Map<String, Object> space = null;
			if (!spaceInfos.isEmpty()) {
				space = spaceInfos.get(0);
			}
			String result = checkUser(sGlobal, request.getParameter("id"), space);
			if (result != null) {
				return showMessage(request, response, result);
			}
			request.setAttribute("space", space);
		}
		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "do_lostpasswd.jsp");
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward do_register(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if (supe_uid > 0) {
			return showMessage(request, response, "do_success", "space.jsp?do=home", 0);
		}
		sGlobal.put("nologinform", 1);
		int uid = Common.intval(request.getParameter("uid"));
		String code = Common.trim(request.getParameter("code"));
		int app = Common.intval(request.getParameter("app"));
		String invite = Common.trim(request.getParameter("invite"));
		Map<String, Object> invits = null;
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		if (uid > 0 && code.length() > 0) {
			Map<String, Integer> reward = Common.getReward("invitecode", false, 0, "", true, request,
					response);
			if (reward.get("credit") == 0) {
				Map<String, Object> mSpace = Common.getSpace(request, sGlobal, sConfig, uid);
				if (code.equals(Common.spaceKey(mSpace, sConfig, app))) {
					invits = new HashMap<String, Object>();
					invits.put("id", 0);
					invits.put("uid", uid);
					invits.put("username", mSpace.get("username"));
				}
				request.setAttribute("url_plus", "uid=" + uid + "&app=" + app + "&code=" + code);
			}
		} else if (uid > 0 && invite.length() > 0) {
			invits = cpService.getInvite(sGlobal, sConfig, sNames, uid, invite);
			request.setAttribute("url_plus", "uid=" + uid + "&invite=" + invite);
		}
		String op = Common.trim(request.getParameter("op"));
		if ("checkusername".equals(op)) {
			String userName = Common.trim(request.getParameter("username"));
			if (Common.empty(userName)) {
				return showMessage(request, response, "user_name_is_not_legitimate");
			}
			int result = checkName(userName,request,response);
			if (result == -1) {
				return showMessage(request, response, "user_name_is_not_legitimate");
			} else if (result == -2) {
				return showMessage(request, response, "include_not_registered_words");
			} else if (result == -3) {
				return showMessage(request, response, "user_name_already_exists");
			} else {
				return showMessage(request, response, "succeed");
			}
		} else if ("checkseccode".equals(op)) {
			if (cpService.checkSeccode(request, response, sGlobal, sConfig, Common.trim(request
					.getParameter("seccode")))) {
				return showMessage(request, response, "succeed");
			} else {
				return showMessage(request, response, "incorrect_code");
			}
		} else {
			if ((Integer) sConfig.get("closeregister") == 1) {
				if ((Integer) sConfig.get("closeinvite") == 1) {
					return showMessage(request, response, "not_open_registration");
				} else if (Common.empty(invits)) {
					return showMessage(request, response, "not_open_registration_invite");
				}
			}
			String message = Common.checkClose(request, response, supe_uid);
			if (message != null) {
				return showMessage(request, response, message);
			}
			String jumpURL = app > 0 ? "userapp.jsp?id=" + app + "&my_extra=invitedby_bi_" + uid + "_" + code
					+ "&my_suffix=Lw%3D%3D" : "space.jsp?do=home";
			try {
				if (submitCheck(request, "registersubmit")) {
					if ((Integer) sConfig.get("seccode_register") == 1
							&& !cpService.checkSeccode(request, response, sGlobal, sConfig, request
									.getParameter("seccode"))) {
						return showMessage(request, response, "incorrect_code");
					}
					String password = request.getParameter("password");
					String password2 = request.getParameter("password2");
					if (Common.empty(password) || !password.equals(Common.addSlashes(password))) {
						return showMessage(request, response, "profile_passwd_illegal");
					}
					if (!password.equals(password2)) {
						return showMessage(request, response, "password_inconsistency");
					}
					String userName = Common.trim(request.getParameter("username"));
					int result = checkName(userName,request,response);
					if (result == -1) {
						return showMessage(request, response, "user_name_is_not_legitimate");
					} else if (result == -2) {
						return showMessage(request, response, "include_not_registered_words");
					} else if (result == -3) {
						return showMessage(request, response, "user_name_already_exists");
					}
					String email = Common.trim(request.getParameter("email"));
					if (!Common.isEmail(email)) {
						return showMessage(request, response, "email_format_is_wrong");
					}
					if ((Integer) sConfig.get("checkemail") == 1) {
						int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
								+ JavaCenterHome.getTableName("spacefield") + " WHERE email='" + email + "'");
						if (count > 0) {
							return showMessage(request, response, "email_has_been_registered");
						}
					}
					int timestamp = (Integer) sGlobal.get("timestamp");
					String onlineIp = Common.getOnlineIP(request);
					String regipDateStr = sConfig.get("regipdate").toString();
					double regipDate = Double.parseDouble(regipDateStr);
					if (regipDate > 0) {
						List<String> datelines = dataBaseService.executeQuery("SELECT dateline FROM "
								+ JavaCenterHome.getTableName("space") + " WHERE regip='" + onlineIp
								+ "' ORDER BY dateline DESC LIMIT 1", 1);
						if (datelines.size() > 0) {
							if (timestamp - Integer.parseInt(datelines.get(0)) < regipDate * 3600) {
								return showMessage(request, response, "regip_has_been_registered", null, 1,
										regipDateStr);
							}
						}
					}
					String salt = Common.getRandStr(6, false);
					password = Common.md5(Common.md5(password) + salt);
					Map<String, Object> insertData = new HashMap<String, Object>();
					insertData.put("username", userName);
					insertData.put("password", password);
					insertData.put("blacklist", "");
					insertData.put("salt", salt);
					int newUid = dataBaseService.insertTable("member", insertData, true, false);
					if (newUid <= 0) {
						return showMessage(request, response, "register_error");
					}
					spaceService.openSpace(request, response, sGlobal, sConfig, newUid, userName, 0, email);
					String defaultFUserName = (String) sConfig.get("defaultfusername");
					if (!Common.empty(defaultFUserName)) {
						List<Map<String, Object>> spaces = dataBaseService
								.executeQuery("SELECT uid,username FROM "
										+ JavaCenterHome.getTableName("space") + " WHERE username IN ("
										+ Common.sImplode(defaultFUserName.split(",")) + ")");
						if (spaces.size() > 0) {
							String defaultPoke = Common.addSlashes(Common.trim(sConfig.get("defaultpoke")
									.toString()));
							List<Integer> fuids = new ArrayList<Integer>();
							List<String> inserts = new ArrayList<String>();
							List<String> pokes = new ArrayList<String>();
							List<String> flogs = new ArrayList<String>();
							for (Map<String, Object> space : spaces) {
								space = (Map<String, Object>) Common.sAddSlashes(space);
								int fuid = (Integer) space.get("uid");
								fuids.add(fuid);
								inserts.add("(" + newUid + "," + fuid + ",'" + space.get("username") + "',1,"
										+ timestamp + ")");
								inserts.add("(" + fuid + "," + newUid + ",'" + userName + "',1," + timestamp
										+ ")");
								pokes.add("(" + newUid + "," + fuid + ",'" + space.get("username") + "','"
										+ defaultPoke + "'," + timestamp + ")");
								flogs.add("(" + fuid + "," + newUid + ",'add'," + timestamp + ")");
							}
							dataBaseService.executeUpdate("REPLACE INTO "
									+ JavaCenterHome.getTableName("friend")
									+ " (uid,fuid,fusername,status,dateline) VALUES "
									+ Common.implode(inserts, ","));
							dataBaseService.executeUpdate("REPLACE INTO "
									+ JavaCenterHome.getTableName("poke")
									+ " (uid,fromuid,fromusername,note,dateline) VALUES "
									+ Common.implode(pokes, ","));
							dataBaseService.executeUpdate("REPLACE INTO "
									+ JavaCenterHome.getTableName("friendlog")
									+ " (uid,fuid,action,dateline) VALUES " + Common.implode(flogs, ","));
							String friendStr = Common.implode(fuids, ",");
							Map<String, Object> whereData = new HashMap<String, Object>();
							whereData.put("uid", newUid);
							Map<String, Object> setSpaceData = new HashMap<String, Object>();
							setSpaceData.put("friendnum", fuids.size());
							setSpaceData.put("pokenum", pokes.size());
							Map<String, Object> setSpaceFieldData = new HashMap<String, Object>();
							setSpaceFieldData.put("friend", friendStr);
							setSpaceFieldData.put("feedfriend", friendStr);

							dataBaseService.updateTable("space", setSpaceData, whereData);
							dataBaseService.updateTable("spacefield", setSpaceFieldData, whereData);
							for (Integer fuid : fuids) {
								cpService.friendCache(request, sGlobal, sConfig, fuid);
							}
						}
					}
					spaceService.insertSession(request, response, sGlobal, sConfig, newUid, userName,
							password);
					CookieHelper.setCookie(request, response, "auth", Common.authCode(password + "\t"
							+ newUid, "ENCODE", null, 0));
					CookieHelper.setCookie(request, response, "loginuser", userName, 31536000);
					CookieHelper.removeCookie(request, response, "_refer");
					if (invits != null) {
						cpService.updateInvite(request, response, sGlobal, sConfig, sNames, (Integer) invits
								.get("id"), newUid, userName, (Integer) invits.get("uid"), (String) invits
								.get("username"), app);
						dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("space")+" SET advgiftcount=advgiftcount+3 WHERE uid='"+invits.get("uid")+"'");
						if (email.equals(invits.get("email"))) {
							Map<String, Object> whereData = new HashMap<String, Object>();
							whereData.put("uid", newUid);
							Map<String, Object> setSpaceFieldData = new HashMap<String, Object>();
							setSpaceFieldData.put("emailcheck", 1);
							dataBaseService.updateTable("spacefield", setSpaceFieldData, whereData);
						}
						if (app > 0) {
							cpService.updateStat(sGlobal, sConfig, "appinvite", false);
						} else {
							cpService.updateStat(sGlobal, sConfig, "invite", false);
						}
					}
					if ((Integer) sConfig.get("my_status") == 1) {
						Map<String, Object> insertUserLogData = new HashMap<String, Object>();
						insertUserLogData.put("uid", newUid);
						insertUserLogData.put("action", "add");
						insertUserLogData.put("dateline", timestamp);
						dataBaseService.insertTable("userlog", insertUserLogData, false, true);
					}
					return showMessage(request, response, "registered", jumpURL);
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			String registerRule = Common.getData("registerrule");
			request.setAttribute("registerRule", registerRule);
			request.setAttribute("jumpURL", jumpURL);
			request.setAttribute("invits", invits);
			return include(request, response, sConfig, sGlobal, "do_register.jsp");
		}
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward do_sendmail(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int perNum = 1;
		CookieHelper.setCookie(request, response, "sendmail", "1", 300);
		File lockFile = new File(JavaCenterHome.jchRoot + "data/sendmail.lock");
		long lastModified = lockFile.lastModified();
		long starttime = (Long) sGlobal.get("starttime");
		if (starttime - lastModified < 5) {
			return null;
		}
		if (lastModified == 0) {
			try {
				lockFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		lockFile.setLastModified(starttime);
		int timestamp = (Integer) sGlobal.get("timestamp");
		List<Map<String, Object>> mailCrons = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("mailcron") + " WHERE sendtime<=" + timestamp
				+ " ORDER BY sendtime LIMIT 0," + perNum);
		if (mailCrons.size() > 0) {
			List<Integer> cids = new ArrayList<Integer>();
			Map<Integer, Integer> toUids = new HashMap<Integer, Integer>();
			Map<Integer, Map<String, Object>> list = new LinkedHashMap<Integer, Map<String, Object>>();
			for (Map<String, Object> mailCron : mailCrons) {
				int toUid = (Integer) mailCron.get("touid");
				int cid = (Integer) mailCron.get("cid");
				if (toUid > 0) {
					toUids.put(toUid, toUid);
				}
				cids.add(cid);
				list.put(cid, mailCron);
			}
			String newCids = Common.sImplode(cids);
			Map<Integer, List<Map<String, Object>>> subList = new LinkedHashMap<Integer, List<Map<String, Object>>>();
			List<Map<String, Object>> mailQueues = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("mailqueue") + " WHERE cid IN (" + newCids + ")");
			for (Map<String, Object> mailQueue : mailQueues) {
				int cid = (Integer) mailQueue.get("cid");
				List<Map<String, Object>> temp = subList.get(cid);
				if (temp == null) {
					temp = new ArrayList<Map<String, Object>>();
					subList.put(cid, temp);
				}
				temp.add(mailQueue);
			}
			if (toUids.size() > 0) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET lastsend=" + timestamp + " WHERE uid IN (" + Common.sImplode(toUids) + ")");
			}
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mailcron")
					+ " WHERE cid IN (" + newCids + ")");
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mailqueue")
					+ " WHERE cid IN (" + newCids + ")");
			try {
				Mail mail = new Mail(request, response);
				Set<Integer> keys = list.keySet();
				for (Integer cid : keys) {
					Map<String, Object> mailCron = list.get(cid);
					List<Map<String, Object>> mList = subList.get(cid);
					String email = (String) mailCron.get("email");
					if (!Common.empty(email) && mList != null) {
						String subject = Common.getStr((String) mList.get(0).get("subject"), 80, false,
								false, false, 0, -1, request, response);
						StringBuffer messageSB = new StringBuffer();
						for (Map<String, Object> subValue : mList) {
							subject = (String) subValue.get("subject");
							String message = (String) subValue.get("message");
							if (Common.empty(message)) {
								messageSB.append(subject + "<br>");
							} else {
								messageSB.append("<br><strong>" + subject + "</strong><br>" + message
										+ "<br>");
							}
						}
						if (!mail.sendMessage(null, email, subject, messageSB.toString())) {
							FileHelper.writeLog(request, "sendmail", email + " sendmail failed.");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward do_stat(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int updateStat = (Integer) sConfig.get("updatestat");
		if (updateStat == 0) {
			return showMessage(request, response, "not_open_updatestat");
		}
		String hash = request.getParameter("hash");
		if (!Common.empty(hash)) {
			CookieHelper.setCookie(request, response, "stat_hash", hash);
			return showMessage(request, response, "do_success", "do.jsp?ac=stat", 0);
		}
		Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
		String statHash = Common.md5(sConfig.get("sitekey") + "\t"
				+ sGlobal.get("timestamp").toString().substring(0, 6));
		if (!Common.checkPerm(request, response, "allowstat") && !statHash.equals(sCookie.get("stat_hash"))) {
			return showMessage(request, response, "no_privilege");
		}
		Map<String, String[]> cols = new LinkedHashMap<String, String[]>();
		cols.put("login", new String[] {"login", "register", "invite", "appinvite"});
		cols.put("add", new String[] {"doing", "blog", "pic", "poll", "event", "share", "thread"});
		cols.put("comment", new String[] {"docomment", "blogcomment", "piccomment", "pollcomment",
				"pollvote", "eventcomment", "eventjoin", "sharecomment", "post", "click"});
		cols.put("space", new String[] {"wall", "poke"});
		request.setAttribute("cols", cols);

		String type = request.getParameter("type");
		if (Common.empty(type)) {
			type = "all";
		}
		String xml = request.getParameter("xml");
		if (!Common.empty(xml)) {
			StringBuffer xaxis = new StringBuffer();
			Map<String, StringBuffer> graph = new LinkedHashMap<String, StringBuffer>();
			int count = 1;
			List<Map<String, Object>> stats = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("stat") + " ORDER BY daytime");
			for (Map<String, Object> stat : stats) {
				xaxis.append("<value xid='" + count + "'>" + stat.get("daytime").toString().substring(4, 8)
						+ "</value>");
				if ("all".equals(type)) {
					Set<String> cks = cols.keySet();
					for (String ck : cks) {
						if ("login".equals(ck)) {
							StringBuffer login = graph.get("login");
							if (login == null) {
								login = new StringBuffer();
								graph.put("login", login);
							}
							login.append("<value xid='" + count + "'>" + stat.get("login") + "</value>");
							StringBuffer register = graph.get("register");
							if (register == null) {
								register = new StringBuffer();
								graph.put("register", register);
							}
							register
									.append("<value xid='" + count + "'>" + stat.get("register") + "</value>");
						} else {
							int num = 0;
							for (String cvk : cols.get(ck)) {
								num = (Integer) stat.get(cvk) + num;
							}
							StringBuffer temp = graph.get(ck);
							if (temp == null) {
								temp = new StringBuffer();
								graph.put(ck, temp);
							}
							temp.append("<value xid='" + count + "'>" + num + "</value>");
						}
					}
				} else {
					StringBuffer temp = graph.get(type);
					if (temp == null) {
						temp = new StringBuffer();
						graph.put(type, temp);
					}
					temp.append("<value xid='" + count + "'>" + stat.get(type) + "</value>");
				}
				count++;
			}
			StringBuffer xmlSB = new StringBuffer();
			xmlSB.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			xmlSB.append("<chart><xaxis>");
			xmlSB.append(xaxis);
			xmlSB.append("</xaxis><graphs>");
			count = 0;
			Set<String> keys = graph.keySet();
			for (String key : keys) {
				StringBuffer value = graph.get(key);
				xmlSB.append("<graph gid='" + count + "' title='" + Common.getMessage(request, "cp_do_stat_" + key) + "'>");
				xmlSB.append(value);
				xmlSB.append("</graph>");
				count++;
			}
			xmlSB.append("</graphs></chart>");
			try {
				PrintWriter out = response.getWriter();
				out.write(xmlSB.toString());
			} catch (IOException e) {
			}
			return null;
		}
		String siteURL = Common.getSiteUrl(request);
		String statuspara = "path=&settings_file=data/stat_setting.xml&data_file="
				+ Common.urlEncode("do.jsp?ac=stat&xml=1&type=" + type);
		Map<String, String> actives = new HashMap<String, String>();
		actives.put(type, " style=\"font-weight:bold;\"");
		request.setAttribute("actives", actives);
		request.setAttribute("type", type);
		request.setAttribute("siteURL", siteURL);
		request.setAttribute("statuspara", statuspara);
		return include(request, response, sConfig, sGlobal, "do_stat.jsp");
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward do_swfupload(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		String op = request.getParameter("op");
		boolean isupload = Common.empty(request.getParameter("cam"))
				&& Common.empty(request.getParameter("doodle")) ? true : false;
		boolean iscamera = request.getParameter("cam") != null ? true : false;
		boolean isdoodle = request.getParameter("doodle") != null ? true : false;
		String fileurl = "";
		String JC_KEY = JavaCenterHome.jchConfig.get("JC_KEY");
		FileUploadUtil upload = new FileUploadUtil(new File(JavaCenterHome.jchRoot + "./data/temp"), 4096);
		try {
			upload.parse(request, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String uid = upload.getParameter("uid");
		if (!Common.empty(uid)) {
			sGlobal.put("supe_uid", Common.intval(uid));
			String hash = upload.getParameter("hash");
			if (Common.empty(sGlobal.get("supe_uid"))
					|| !hash.equals(Common.md5(sGlobal.get("supe_uid") + JC_KEY))) {
				return null;
			}
		} else if (Common.empty(sGlobal.get("supe_uid"))) {
			return showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
		}

		if ("finish".equals(op)) {
			int albumId = Common.intval(request.getParameter("albumid"));
			Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, sGlobal.get("supe_uid"));
			if (Common.ckPrivacy(sGlobal, sConfig, space, "upload", 1)) {
				feedService.feedPublish(request, response, albumId, "albumid", false);
			}
			return null;

		} else if ("config".equals(op)) {
			String hash = Common.md5(sGlobal.get("supe_uid") + JC_KEY);
			if (isupload && !Common.checkPerm(request, response, "allowupload")) {
				hash = "";
			} else {
				File[] imageFiles = null;
				String[] filearr = null;
				if (iscamera) {
					File[] directory = Common.readDir(JavaCenterHome.jchRoot + "./image/foreground");
					File f;
					Object[] tempOA;
					Map<Integer, Object[]> dirarr = new HashMap<Integer, Object[]>();
					for (int i = 0; i < directory.length; i++) {
						f = directory[i];
						if (f.isDirectory()) {
							imageFiles = Common.readDir(f.toString(), new String[] {"jpg", "jpeg", "gif",
									"png"});
							if (imageFiles != null) {
								filearr = new String[imageFiles.length];
								for (int j = 0; j < imageFiles.length; j++) {
									filearr[j] = imageFiles[j].getName();
								}
								File categoryFile = new File(f.toString() + "/categories.txt");
								tempOA = new Object[3];
								if (categoryFile.isFile()) {
									List<String> catfile = FileHelper.readFileToList(categoryFile);
									if (catfile.size() > 0) {
										tempOA[0] = catfile.get(0).trim();
									} else {
										tempOA[0] = "";
									}
								} else {
									tempOA[0] = f.getName().trim();
								}
								tempOA[1] = "image/foreground/" + f.getName() + "/";
								tempOA[2] = filearr;
								dirarr.put(i, tempOA);
							}
						}
					}
					request.setAttribute("dirarr", dirarr);
				} else if (isdoodle) {
					imageFiles = Common.readDir(JavaCenterHome.jchRoot + "./image/doodle/big", new String[] {
							"jpg", "jpeg", "gif", "png"});
					if (imageFiles != null) {
						filearr = new String[imageFiles.length];
						for (int j = 0; j < imageFiles.length; j++) {
							filearr[j] = imageFiles[j].getName();
						}
					}
				}
				request.setAttribute("filearr", filearr);
			}
			int max = 0;
			String upload_max_filesize = JavaCenterHome.jchConfig.get("upload_max_filesize");
			if (upload_max_filesize != null
					&& (upload_max_filesize = upload_max_filesize.trim()).length() > 0) {
				String unit = upload_max_filesize.substring(upload_max_filesize.length() - 1);
				if (unit.equalsIgnoreCase("k")) {
					max = Integer
							.parseInt(upload_max_filesize.substring(0, upload_max_filesize.length() - 1)) * 1024;
				} else if (unit.equalsIgnoreCase("m")) {
					max = Integer
							.parseInt(upload_max_filesize.substring(0, upload_max_filesize.length() - 1)) * 1024 * 1024;
				} else if (unit.equalsIgnoreCase("g")) {
					max = Integer
							.parseInt(upload_max_filesize.substring(0, upload_max_filesize.length() - 1)) * 1024 * 1024 * 1024;
				} else {
					max = Integer.parseInt(upload_max_filesize);
				}
			}
			List<Map<String, Object>> albums = cpService.getAlbums((Integer) sGlobal.get("supe_uid"));

			request.setAttribute("max", max);
			request.setAttribute("albums", albums);
			request.setAttribute("hash", hash);

		} else if ("screen".equals(op) || "doodle".equals(op)) {
			
			InputStream stream = null;
			try {
				stream = request.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			String status = "failure";
			Object uploadfiles = null;
			boolean dosave = true;

			int supe_uid = (Integer) sGlobal.get("supe_uid");
			if (op.equals("doodle")) {
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("usermagic") + " WHERE uid = '" + supe_uid
						+ "' AND mid = 'doodle'");
				if (query.size() > 0) {
					Map<String, Object> value = query.get(0);
					if (Common.empty(value) || (Integer) value.get("count") < 1) {
						uploadfiles = "-8";
						dosave = false;
					}
				}
			}
			if (dosave && stream != null) {
				String albumId = request.getHeader("ALBUMID");
				String jcChar = JavaCenterHome.jchConfig.get("charset");
				try {

					albumId = Common.addSlashes((String) Common.siconv(URLDecoder.decode(albumId, "UTF-8"),
							jcChar, "UTF-8", jcChar));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
				String from = null;
				if (op.equals("screen")) {
					from = "camera";
				} else if ("album".equals(request.getParameter("from"))) {
					from = "uploadimage";
				}
				sConfig.put("allowwatermark", 0);
				Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
				try {
					uploadfiles = cpService.stream_save(request, response, sGlobal, space, sConfig, stream,
							albumId, "jpg", "", "", 0, from);
				} catch (Exception e) {
					e.printStackTrace();
					return showMessage(request, response, e.getMessage());
				}
			}

			boolean uploadResponse = true;
			int picid = 0, proid = 0, albumid = 0;
			if (uploadfiles != null && Common.isArray(uploadfiles)) {
				Map<String, Object> tempM = (Map<String, Object>) uploadfiles;
				status = "success";
				albumid = (Integer) tempM.get("albumid");
				picid = (Integer) tempM.get("picid");
				if ("doodle".equals(op)) {
					Integer thumb = (Integer) tempM.get("thumb");
					Integer remote = (Integer) tempM.get("remote");
					fileurl = Common.pic_get(sConfig, (String) tempM.get("filepath"), (thumb != null ? thumb
							: 0), (remote != null ? remote : 0), false);

					magicService.magic_use(sGlobal, "doodle", null, true);
				}
			} else {
				Integer tempI = (Integer) uploadfiles;
				switch (tempI) {
					case -1:
						uploadfiles = Common.getMessage(request, "cp_inadequate_capacity_space");
						break;
					case -2:
						uploadfiles = Common.getMessage(request, "cp_only_allows_upload_file_types");
						break;
					case -4:
						uploadfiles = Common.getMessage(request, "cp_ftp_upload_file_size");
						break;
					case -8:
						uploadfiles = Common.getMessage(request, "cp_has_not_more_doodle");
						break;
					default:
						uploadfiles = Common.getMessage(request, "cp_mobile_picture_temporary_failure");
						break;
				}
			}
			request.setAttribute("uploadResponse", uploadResponse);
			request.setAttribute("status", status);
			request.setAttribute("uploadfiles", uploadfiles);
			request.setAttribute("proid", proid);
			request.setAttribute("albumid", albumid);
			request.setAttribute("picid", picid);

		} else if (upload.isMultipart()) {
			

			FileItem item = upload.getFileItem("Filedata");
			Object uploadFiles = null;

			if (item != null) {
				long size = item.getSize();
				String maxsize = JavaCenterHome.jchConfig.get("upload_max_filesize");

				if (size > Common.getByteSizeByBKMG(maxsize)) {
					uploadFiles = Common.getMessage(request, "cp_file_is_too_big");
				} else {
					String albumId ="";
					String title ="";
					try {
						albumId = Common.addSlashes(URLDecoder.decode(upload.getParameter("albumid"),"UTF-8"));
						title =URLDecoder.decode(upload.getParameter("title"),"UTF-8");
					} catch (Exception e) {
					}
					uploadFiles = cpService.savePic(request, response, item, albumId, title, 0);
				}
			}
			String proId = upload.getParameter("proid");
			boolean uploadResponse = true;
			Object albumId = null;
			String status = null;
			if (!Common.empty(uploadFiles) && Common.isArray(uploadFiles)) {
				status = "success";
				albumId = ((Map) uploadFiles).get("albumid");
			} else {
				status = "failure";
			}

			request.setAttribute("proid", proId);
			request.setAttribute("uploadResponse", uploadResponse);
			request.setAttribute("status", status);
			request.setAttribute("uploadfiles", uploadFiles);
			request.setAttribute("albumid", albumId);
		}

		String newalbumname = Common.sgmdate(request, "yyyyMMdd", 0);
		request.setAttribute("newalbumname", newalbumname);
		request.setAttribute("iscamera", iscamera);
		request.setAttribute("isdoodle", isdoodle);
		request.setAttribute("isupload", isupload);
		request.setAttribute("fileurl", fileurl);

		response.setHeader("Expires", "-1");
		response.addHeader("Cache-Control", "no-store, private, post-check=0, pre-check=0, max-age=0");
		response.setHeader("Pragma", "no-cache");
		return include(request, response, sConfig, sGlobal, "do_swfupload.jsp");
	}

	
	private int checkName(String userName,HttpServletRequest request,HttpServletResponse response) {
		userName = Common.addSlashes(Common.stripSlashes(Common.trim(userName)));
		String guestexp = "\\xA1\\xA1|\\xAC\\xA3|^Guest|^\\xD3\\xCE\\xBF\\xCD|\\xB9\\x43\\xAB\\xC8";
		int len = Common.strlen(userName);
		if (len > 15 || len < 3
				|| Common.matches(userName, "(?is)\\s+|^c:\\con\\con|[%,\\*\"\\s\\<\\>\\&]|" + guestexp)) {
			return -1;
		}
		try {
			String temp=Common.getStr(userName, 0, false, false, true, 0, 0, request, response);
			if(!temp.equals(userName)){
				return -2;
			}
		} catch (Exception e) {
			return -2;
		}
		int count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("member")
				+ " WHERE username='" + userName + "'");
		if (count > 0) {
			return -3;
		} else {
			return 1;
		}
	}

	
	private String checkUser(Map<String, Object> sGlobal, String id, Map<String, Object> space) {
		if (Common.empty(space)) {
			return "user_does_not_exist";
		}
		String[] auths = ((String) space.get("authstr")).split("\t");
		if (auths.length != 3 || Common.intval(auths[0]) < (Integer) sGlobal.get("timestamp") - 86400 * 3
				|| !auths[1].equals("1") || !auths[2].equals(id)) {
			return "getpasswd_illegal";
		} else {
			return null;
		}
	}
}