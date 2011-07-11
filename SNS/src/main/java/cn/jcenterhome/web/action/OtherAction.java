
package cn.jcenterhome.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
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
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.util.XmlRpc;
import cn.jcenterhome.vo.MessageVO;

import com.huangzhimin.contacts.Contact;
import com.huangzhimin.contacts.ContactsImporter;
import com.huangzhimin.contacts.ContactsImporterFactory;


public class OtherAction extends BaseAction {
	
	@SuppressWarnings("unchecked")
	public ActionForward index(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String requestURI = (String) request.getAttribute("requestURI");
		String queryString = null;
		if (requestURI != null) {
			queryString = requestURI.substring(requestURI.indexOf("?") + 1);
		}
		if (Common.isNumeric(queryString)) {
			return showMessage(request, response, "enter_the_space", "space.jsp?uid=" + queryString, 0);
		}

		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String paramDo = request.getParameter("do");
		if (paramDo == null && !Common.empty(sConfig.get("allowdomain"))) {
			String host = request.getServerName();
			String domainRoot = (String) sConfig.get("domainroot");
			String[] hostArr = host.split("\\.");
			String[] domainRootArr = domainRoot.split("\\.");
			int hostArrLen = hostArr.length;
			int domainRootArrLen = domainRootArr.length;
			if (hostArrLen > 2 && hostArrLen > domainRootArrLen && !hostArr[0].equals("www")
					&& !Common.isHoldDomain(sConfig, hostArr[0])) {
				return showMessage(request, response, "enter_the_space", sConfig.get("siteallurl")
						+ "space.jsp?domain=" + hostArr[0], 0);
			}
		}

		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if (supe_uid > 0) {
			return showMessage(request, response, "enter_the_space", "space.jsp?do=home", 0);
		}
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (Common.empty(sConfig.get("networkpublic"))) {
			File cacheFile = new File(JavaCenterHome.jchRoot + "data/cache/cache_index.txt");
			long cacheTime = (int) (cacheFile.lastModified() / 1000);
			List<Map<String, Object>> spaces = null;
			if ((Integer) sGlobal.get("timestamp") - cacheTime > 900) {
				spaces = dataBaseService
						.executeQuery("SELECT s.uid, s.username, s.name, s.namestatus, s.friendnum, sf.resideprovince, sf.residecity FROM "
								+ JavaCenterHome.getTableName("space")
								+ " s LEFT JOIN "
								+ JavaCenterHome.getTableName("spacefield")
								+ " sf ON sf.uid=s.uid ORDER BY s.friendnum DESC LIMIT 0,20");
				FileHelper.writeFile(cacheFile, Serializer.serialize(spaces));
			} else {
				spaces = Serializer.unserialize(FileHelper.readFile(cacheFile));
			}
			request.setAttribute("spaces", spaces);
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			for (Map<String, Object> value : spaces) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), (String) value.get("name"), (Integer) value.get("namestatus"));
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			int myAppCount = 0;
			if ((Integer) sConfig.get("my_status") == 1) {
				myAppCount = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("myapp") + " WHERE flag>='0'");
				if (myAppCount > 0) {
					List<Map<String, Object>> myApps = dataBaseService
							.executeQuery("SELECT appid,appname FROM " + JavaCenterHome.getTableName("myapp")
									+ " WHERE flag>=0 ORDER BY flag DESC, displayorder LIMIT 0,7");
					request.setAttribute("myApps", myApps);
				}
			}
			request.setAttribute("myAppCount", myAppCount);

			request.setAttribute("tpl_css", "network");
			return include(request, response, sConfig, sGlobal, "index.jsp");
		} else {
			return network(request, response, sGlobal, sConfig, space);
		}
	}

	public ActionForward app(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		return include(request, response, sConfig, sGlobal, "iframe.jsp");
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward help(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String ac = request.getParameter("ac");
		if (Common.empty(ac)) {
			ac = "register";
		}
		int supe_uid=(Integer)sGlobal.get("supe_uid");
		if(supe_uid!=0){
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			Map<String, Object> member=Common.getMember(request);
			Common.realname_set(sGlobal, sConfig, sNames, supe_uid, (String)member.get("username"), (String)member.get("name"), (Integer)member.get("namestatus"));
			Common.realname_get(sGlobal, sConfig, sNames, null);
		}
		request.setAttribute("ac", ac);
		request.setAttribute("active_" + ac, " style=\"font-weight:bold;\"");
		request.setAttribute("navtitle", "帮助 - ");
		return include(request, response, sConfig, sGlobal, "help.jsp");
	}

	public ActionForward invite(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String message = Common.checkClose(request, response, (Integer) sGlobal.get("supe_uid"));
		if (message != null) {
			return showMessage(request, response, message);
		}
		Map<String, Integer> reward = Common.getReward("invitecode", false, 0, "", true, request, response);
		int uid = Common.intval(request.getParameter("u"));
		String code = Common.trim(request.getParameter("c"));
		int app = Common.intval(request.getParameter("app"));
		String requestURI = (String) request.getAttribute("requestURI");
		String queryString = "";
		if (requestURI != null) {
			if (requestURI.indexOf("?") >= 0) {
				queryString = requestURI.substring(requestURI.indexOf("?") + 1);
			}
		}
		if (app != 0) {
			reward.put("credit", 0);
		}
		Map<String, Object> invite = new HashMap<String, Object>();
		String theURL = null;
		String urlPlus = null;
		if (uid != 0 && reward.get("credit") == 0) {
			invite.put("uid", uid);
			invite.put("id", 0);
			theURL = "invite.jsp?u=" + uid + "&app=" + app + "&c=" + code;
			urlPlus = "uid=" + uid + "&app=" + app + "&code=" + code;
		} else {
			int id = 0;
			String c = "";
			int inviteLength = Common.strlen(queryString);
			if (inviteLength > 6) {
				c = Common.addSlashes(queryString.substring(queryString.length() - 6, queryString.length()));
				id = Common.intval(queryString.replace(c, ""));
			}
			if (id == 0) {
				return showMessage(request, response, "invite_code_error");
			}
			List<Map<String, Object>> inviteList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("invite") + " WHERE id='" + id + "' AND code='" + c + "'");
			if (inviteList.isEmpty()) {
				return showMessage(request, response, "invite_code_error");
			}
			invite = inviteList.get(0);
			if (!Common.empty(invite.get("fuid"))) {
				return showMessage(request, response, "invite_code_fuid");
			}
			if (sGlobal.get("supe_uid").equals(invite.get("uid"))) {
				return showMessage(request, response, "should_not_invite_your_own");
			}
			theURL = "invite.jsp?" + queryString;
			urlPlus = "uid=" + invite.get("uid") + "&invite=" + invite.get("code");
		}
		Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, invite.get("uid"));
		if (Common.empty(space)) {
			return showMessage(request, response, "space_does_not_exist");
		}
		if (uid != 0 && reward.get("credit") == 0) {
			if (!code.equals(Common.spaceKey(space, sConfig, app))) {
				return showMessage(request, response, "invite_code_error");
			}
		}
		if ((Boolean) space.get("self")) {
			return showMessage(request, response, "should_not_invite_your_own");
		}
		if ((Integer) sGlobal.get("supe_uid") != 0
				&& Common.in_array(space.get("friends"), sGlobal.get("supe_uid"))) {
			space.put("isfriend", true);
		} else {
			space.put("isfriend", false);
		}
		String jumpURL = app != 0 ? "userapp.jsp?id=" + app + "&my_extra=invitedby_bi_" + uid + "_" + code
				+ "&my_suffix=Lw%3D%3D" : "space.jsp?uid=" + space.get("uid");
		if ((Boolean) space.get("isfriend")) {
			return showMessage(request, response, "you_have_friends", jumpURL, 1);
		}
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		try {
			if (submitCheck(request, "invitesubmit")) {
				if ((Integer) sGlobal.get("supe_uid") == 0) {
					return showMessage(request, response, "invite_code_error");
				}
				cpService.updateInvite(request, response, sGlobal, sConfig, sNames, (Integer) invite
						.get("id"), (Integer) sGlobal.get("supe_uid"), (String) sGlobal.get("supe_username"),
						(Integer) space.get("uid"), (String) space.get("username"), app);
				return showMessage(request, response, "friends_add", jumpURL, 1, sNames.get(space.get("uid")));
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		List<Map<String, Object>> fList = dataBaseService
				.executeQuery("SELECT fuid AS uid, fusername AS username FROM "
						+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + invite.get("uid")
						+ "' AND status='1' ORDER BY num DESC, dateline DESC LIMIT 0,12");
		for (Map<String, Object> value : fList) {
			Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
					.get("username"), null, 0);
		}
		Common.realname_get(sGlobal, sConfig, sNames, space);
		request.setAttribute("flist", fList);
		Map<String, Integer> numMap = new HashMap<String, Integer>();
		for (String var : new String[] {"album", "doing", "blog", "thread", "tagspace"}) {
			numMap.put(var, dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName(var) + " WHERE uid='" + invite.get("uid") + "'"));
		}
		request.setAttribute("numMap", numMap);
		if (app != 0) {
			List<Map<String, Object>> appList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("myapp") + " WHERE appid='" + app + "'");
			if (!appList.isEmpty()) {
				request.setAttribute("userapp", appList.get(0));
			}
		}
		request.setAttribute("theURL", theURL);
		request.setAttribute("urlPlus", urlPlus);
		request.setAttribute("space", space);
		return include(request, response, sConfig, sGlobal, "invite.jsp");
	}

	public ActionForward js(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int id = Common.intval(request.getParameter("id"));
		int adId = Common.intval(request.getParameter("adid"));
		if (id > 0) {
			Map<Integer, Integer> globalBlock = Common.getCacheDate(request, response,
					"/data/cache/cache_block.jsp", "globalBlock");
			if (globalBlock.get(id) == null) {
				PrintWriter out = response.getWriter();
				out.write("document.write(\'No data.\')");
				out.flush();
				out.close();
				return null;
			}
			int updatetime = globalBlock.get(id);
			String cachefilepath = JavaCenterHome.jchRoot + "./data/block_cache/block_" + id + ".js";
			File cachefile = new File(cachefilepath);
			if (updatetime > 0 && cachefile.exists()
					&& (Integer) sGlobal.get("timestamp") - cachefile.lastModified() / 1000 < updatetime) {
				String content = "document.writeln(\'No data.\')";
				if (cachefile.exists()) {
					content = FileHelper.readFile(cachefile);
				}
				PrintWriter out = response.getWriter();
				out.write(content);
				out.flush();
				out.close();
				return null;
			}
			sConfig.put("linkguide", 0);
			sConfig.put("allowcache", 0);
			include(request, response, sConfig, sGlobal, "/data/blocktpl/" + id + ".jsp");
			request.setAttribute("updatetime", updatetime);
			request.setAttribute("isWriteJsFile", true);
			request.setAttribute("id", id);
		} else if (adId > 0) {
			String filePath = JavaCenterHome.jchRoot + "data/adtpl/" + adId + ".htm";
			List<String> lines = FileHelper.readFileToList(new File(filePath));
			PrintWriter out = response.getWriter();
			if (lines.size() > 0) {
				for (String line : lines) {
					out.write("document.writeln('" + Common.addCSlashes(line.trim(), new char[] {'\'', '\\'})
							+ "');\n");
				}
			} else {
				out.write("document.writeln('NO AD.')");
			}
			out.flush();
			out.close();
		}
		return null;
	}

	
	public ActionForward link(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String url = request.getParameter("url");
		if (Common.empty(url)) {
			return showMessage(request, response, "do_success", (String) request.getAttribute("refer"), 0);
		} else {
			if ((Integer) sConfig.get("linkguide") == 0) {
				return showMessage(request, response, "do_success", url, 0);
			}
		}
		Map<String, Object> space = null;
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if (supe_uid > 0) {
			space = Common.getSpace(request, sGlobal, sConfig, supe_uid);
		}
		if (Common.empty(space)) {
			return showMessage(request, response, "do_success", url, 0);
		}
		request.setAttribute("space", space);
		url = (String) Common.sHtmlSpecialChars(url);
		if (!Common.matches(url, "(?i)^http\\:\\/\\/")) {
			url = "http://" + url;
		}
		request.setAttribute("url", url);
		request.setAttribute("timestamp", Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) sGlobal
				.get("timestamp"), true));
		return include(request, response, sConfig, sGlobal, "iframe.jsp");
	}

	
	public ActionForward network(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		String message = Common.checkClose(request, response, supe_uid);
		if (message != null) {
			return showMessage(request, response, message);
		}

		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (supe_uid > 0) {
			space = Common.getSpace(request, sGlobal, sConfig, supe_uid);
			if ((Integer) space.get("flag") == -1) {
				return showMessage(request, response, "space_has_been_locked");
			}
			if (Common.checkPerm(request, response, "banvisit")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				} else {
					return showMessage(request, response, "you_do_not_have_permission_to_visit");
				}
			}
		}
		return network(request, response, sGlobal, sConfig, space);
	}
	
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		if ("true".equals(jchConfig.get("updatetime"))) {
			int timestamp = (int) (System.currentTimeMillis() / 1000);
			dataBaseService.executeUpdate("REPLACE INTO "+JavaCenterHome.getTableName("config")+" VALUES ('jchid','" + jchConfig.get("jchid")
					+ "'),('lastupdate','" + timestamp
					+ "'),('status','" + jchConfig.get("status") + "')");
			jchConfig.remove("updatetime");
		}
		return null;
	}
	
	public ActionForward rss(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		response.setContentType("application/xml");
		int uid = Common.intval(request.getParameter("uid"));
		String rssDateFormat = "E, d MMM  yyyy HH:mm:ss Z";
		Map<String, Object> space = null;
		if (uid > 0) {
			space = Common.getSpace(request, sGlobal, sConfig, uid);
		}
		String siteURL = Common.getSiteUrl(request);
		if (Common.empty(space)) {
			space = new HashMap<String, Object>();
			space.put("username", sConfig.get("sitename"));
			space.put("name", sConfig.get("sitename"));
			space.put("email", sConfig.get("adminemail"));
			space.put("space_url", siteURL);
			space.put("lastupdate", Common.sgmdate(request, rssDateFormat, 0));
			Map privacy = new HashMap();
			privacy.put("blog", 1);
			space.put("privacy", privacy);
		} else {
			space.put("username", space.get("username") + "@" + sConfig.get("sitename"));
			space.put("space_url", siteURL + "space.jsp?uid=" + space.get("uid"));
			space.put("lastupdate", Common.sgmdate(request, rssDateFormat,
					(space.get("lastupdate") != null ? (Integer) space.get("lastupdate") : 0)));
		}
		String uidSQL = Common.empty(space.get("uid")) ? "" : " AND b.uid='" + space.get("uid") + "'";
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT bf.message, b.* FROM "
				+ JavaCenterHome.getTableName("blog") + " b LEFT JOIN "
				+ JavaCenterHome.getTableName("blogfield") + " bf ON bf.blogid=b.blogid WHERE b.friend='0' "
				+ uidSQL + " ORDER BY dateline DESC LIMIT 0, 10");
		for (Map<String, Object> value : list) {
			if (space.get("privacy") != null && !Common.empty(((Map) space.get("privacy")).get("blog"))) {
				value.remove("message");
			} else {
				value.put("message", Common.getStr((String) value.get("message"), 300, false, false, false,
						0, -1, request, response));
				if (!Common.empty(value.get("pic"))) {
					value.put("pic", Common.pic_cover_get(sConfig, (String) value.get("pic"), (Integer) value
							.get("picflag")));
					value.put("message", value.get("message") + "<br /><img src=\"" + value.get("pic")
							+ "\">");
				}
			}
			Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
					.get("username"), "", 0);
			value.put("dateline", Common.sgmdate(request, rssDateFormat, (Integer) value.get("dateline")));
		}
		Common.realname_get(sGlobal, sConfig, sNames, space);
		request.setAttribute("space", space);
		request.setAttribute("siteurl", siteURL);
		request.setAttribute("list", list);
		return include(request, response, sConfig, sGlobal, "space_rss.jsp");
	}

	public ActionForward userapp(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		return include(request, response, sConfig, sGlobal, "userapp.jsp");
	}

	
	public ActionForward xmlrpc(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		if (Common.empty(sConfig.get("openxmlrpc"))) {
			return showMessage(request, response, "no_privilege");
		}
		String siteURL = Common.getSiteUrl(request);
		if (request.getParameter("rsd") != null) {
			response.setHeader("Content-type", "text/xml, charset=utf-8");
			String result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<rsd version=\"1.0\" xmlns=\"http://archipelago.phrasewise.com/rsd\">" + "<service>"
					+ "<engineName>JavaCenter Home</engineName>"
					+ "<engineLink>http://j.jsprun.net/</engineLink>" + "<homePageLink>" + siteURL
					+ "</homePageLink>" + "<apis>"
					+ "<api name=\"MetaWeblog\" blogID=\"1\" preferred=\"false\" apiLink=\"" + siteURL
					+ "xmlrpc.jsp\" />"
					+ "<api name=\"WordPress\" blogID=\"1\" preferred=\"true\" apiLink=\"" + siteURL
					+ "xmlrpc.jsp\" />" + "<api name=\"Blogger\" blogID=\"1\" preferred=\"false\" apiLink=\""
					+ siteURL + "xmlrpc.jsp\" />" + "</apis>" + "</service>" + "</rsd>";
			PrintWriter out = response.getWriter();
			out.write(result);
			out.flush();
		} else {
			try {
				XmlRpc xmlRpc = new XmlRpc(request, response);
				xmlRpc.xmlrpcServer();
			} catch (Exception e) {
				String message = e.getMessage();
				if (message == null) {
					message = e.getCause().getMessage();
				}
				PrintWriter out = response.getWriter();
				out.write(message);
				out.flush();
			}
		}
		return null;
	}

	
	public ActionForward avatar(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-store, private, post-check=0, pre-check=0, max-age=0");
		response.setHeader("Pragma", "no-cache");
		String a = request.getParameter("a");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		sGlobal.put("inajax", 0);
		if ("uploadavatar".equals(a)) {
			uploadAvatar(sGlobal, sConfig, request, response);
		} else if ("rectavatar".equals(a)) {
			response.setContentType("application/xml; charset=utf-8");
			rectAvatar(sGlobal, sConfig, request, response);
		}
		return null;
	}

	
	public ActionForward contact(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String[] emails = new String[] {"Sohu", "Sina", "139", "163", "126", "Yeah", "Yahoo", "Gmail", "MSN",
				"Tom"};
		String ab = request.getParameter("ab");
		request.setAttribute("ta", request.getParameter("ta"));
		if (Common.in_array(emails, ab)) {
			request.setAttribute("abid", ab);
			String errorMessage = null;
			try {
				if (submitCheck(request, "loginsubmit")) {
					String email = Common.trim(request.getParameter("username"));
					String password = Common.trim(request.getParameter("password"));
					if (Common.empty(email) || Common.empty(password)) {
						errorMessage = "帐户或密码是不是为空啦!!!";
					} else if ("139".equals(ab) && !Common.matches(email, "^[1]\\d{10}$")) {
						errorMessage = "请输入正确的手机号码";
					} else if (!"139".equals(ab) && !Common.isEmail(email)) {
						errorMessage = "请输入正确的邮箱名和密码";
					} else {
						try {
							ContactsImporter importer = null;
							if ("Sohu".equals(ab)) {
								importer = ContactsImporterFactory.getSohuContacts(email, password);
							} else if ("Sina".equals(ab)) {
								importer = ContactsImporterFactory.getSinaContacts(email, password);
							} else if ("163".equals(ab)) {
								importer = ContactsImporterFactory.getOneSixThreeContacts(email, password);
							} else if ("126".equals(ab)) {
								importer = ContactsImporterFactory.getOneTwoSixContacts(email, password);
							} else if ("Yeah".equals(ab)) {
								importer = ContactsImporterFactory.getYeahContacts(email, password);
							} else if ("Yahoo".equals(ab)) {
								importer = ContactsImporterFactory.getYahooContacts(email, password);
							} else if ("Gmail".equals(ab)) {
								importer = ContactsImporterFactory.getGmailContacts(email, password);
							} else if ("MSN".equals(ab)) {
								if (email.toLowerCase().indexOf("@hotmail") > 0) {
									importer = ContactsImporterFactory.getHotmailContacts(email, password);
								} else {
									importer = ContactsImporterFactory.getMSNContacts(email, password);
								}
							} else if ("139".equals(ab)) {
								importer = ContactsImporterFactory.getOneThreeNineContacts(email, password);
							} else if ("Tom".equals(ab)) {
								importer = ContactsImporterFactory.getTomContacts(email, password);
							}
							if (importer == null) {
								errorMessage = "请指定有效的信箱";
							} else {
								List<Contact> contacts = importer.getContacts();
								List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
								Object[] existEmails = Common.uniqueArray(Common.trim(
										request.getParameter("emails")).split(","));
								for (Contact contact : contacts) {
									Map<String, Object> map = new HashMap<String, Object>();
									String name = contact.getUsername();
									map.put("name", name);
									String addr = contact.getEmail();
									map.put("addr", addr);
									boolean exist = false;
									for (Object existEmail : existEmails) {
										if (Common.trim(existEmail.toString()).equalsIgnoreCase(addr.trim())) {
											exist = true;
											break;
										}
									}
									map.put("isexist", exist);
									list.add(map);
								}
								request.setAttribute("list", list);
							}
						} catch (Exception e) {
							errorMessage = "登录失败，请检查帐户和密码是否有误!";
						}
					}
				}
			} catch (Exception e) {
				errorMessage = e.getMessage();
			}
			request.setAttribute("errorMessage", errorMessage);
		} else {
			request.setAttribute("emails", emails);
		}
		return include(request, response, sConfig, sGlobal, "/api/contactlist.jsp");
	}

	
	private void uploadAvatar(Map<String, Object> sGlobal, Map<String, Object> sConfig,
			HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		FileItem item = null;
		try {
			out = response.getWriter();
			int uid = Common.intval(request.getParameter("uid"));
			if (uid > 0) {
				sGlobal.put("supe_uid", uid);
				String hash = request.getParameter("hash");
				String JC_KEY = JavaCenterHome.jchConfig.get("JC_KEY");
				if (!Common.md5(sGlobal.get("supe_uid") + JC_KEY).equals(hash)) {
					out.write("-1");
					return;
				}
			} else if (Common.empty(uid = (Integer) sGlobal.get("supe_uid"))) {
				showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
				return;
			}
			String jchDataTemp = JavaCenterHome.jchRoot + "data/temp/";
			File tempAvatarDir = new File(jchDataTemp + uid);
			if (tempAvatarDir.exists()) {
				if (deleteUploadFile(tempAvatarDir, false) == false) {
					out.write("-4");
					return;
				}
			} else {
				tempAvatarDir.mkdir();
			}
			String fileType = ".jpg";
			File tempAvatar = new File(jchDataTemp + uid + "/" + sGlobal.get("timestamp") + fileType);
			FileUploadUtil upload = new FileUploadUtil(new File(jchDataTemp + uid), 4096);
			upload.parse(request, JavaCenterHome.JCH_CHARSET);
			item = upload.getFileItem("Filedata");
			if (!item.isFormField()) {
				item.write(tempAvatar);
			}
			String imgType = Common.getImageType(tempAvatar);
			if (Common.empty(imgType)) {
				tempAvatar.delete();
				out.write("-2");
				return;
			}
			out.write(Common.getSiteUrl(request) + "data/temp/" + uid + "/" + tempAvatar.getName());
		} catch (IOException ioe) {
			out.write("-5");
		} catch (NullPointerException ne) {
			out.write("-3");
		} catch (Exception e) {
			if (item != null) {
				item.delete();
			}
			out.write("-4");
		} finally {
			out.flush();
			out.close();
			out = null;
			item = null;
		}
	}

	
	private void rectAvatar(Map<String, Object> sGlobal, Map<String, Object> sConfig,
			HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			int uid = Common.intval(request.getParameter("uid"));
			if (uid > 0) {
				sGlobal.put("supe_uid", uid);
				String hash = request.getParameter("hash");
				String JC_KEY = JavaCenterHome.jchConfig.get("JC_KEY");
				if (!Common.md5(sGlobal.get("supe_uid") + JC_KEY).equals(hash)) {
					out.write("<root><message type=\"error\" value=\"-1\" /></root>");
					return;
				}
			} else if (Common.empty(uid = (Integer) sGlobal.get("supe_uid"))) {
				showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
				return;
			}
			String home = Common.get_home(uid);
			String jchDataDir = JavaCenterHome.jchRoot + "data";
			File avatarHomeFile = new File(jchDataDir + "/avatar/" + home);
			if (!avatarHomeFile.isDirectory()) {
				Common.set_home(uid, jchDataDir + "/avatar/");
			}
			String avatarType = "real".equals(request.getParameter("avatartype")) ? "real" : "virtual";
			String avatarRoot = jchDataDir + "/avatar/";
			File bigAvatarFile = new File(avatarRoot + Common.avatar_file(sGlobal, uid, "big", avatarType));
			File middleAvatarfile = new File(avatarRoot
					+ Common.avatar_file(sGlobal, uid, "middle", avatarType));
			File smallAvatarfile = new File(avatarRoot
					+ Common.avatar_file(sGlobal, uid, "small", avatarType));
			boolean bigOK = writeAvatar(bigAvatarFile, request.getParameter("avatar1"));
			boolean middleOK = writeAvatar(middleAvatarfile, request.getParameter("avatar2"));
			boolean smallOK = writeAvatar(smallAvatarfile, request.getParameter("avatar3"));
			if (!bigOK || !middleOK || !smallOK) {
				out.write("<root><message type=\"error\" value=\"-2\" /></root>");
				return;
			}
			boolean success = true;
			String bigAvatarType = Common.getImageType(bigAvatarFile);
			String middleAvatarType = Common.getImageType(middleAvatarfile);
			String smallAvatarType = Common.getImageType(smallAvatarfile);
			if (Common.empty(bigAvatarType) || Common.empty(middleAvatarType)
					|| Common.empty(smallAvatarType)) {
				if (bigAvatarFile.exists())
					bigAvatarFile.delete();
				if (middleAvatarfile.exists())
					middleAvatarfile.delete();
				if (smallAvatarfile.exists())
					smallAvatarfile.delete();
				success = false;
			}
			File tempAvatarFile = new File(jchDataDir + "/temp/" + uid);
			if (tempAvatarFile.exists()) {
				deleteUploadFile(tempAvatarFile, true);
			}
			if (success) {
				out.write("<?xml version=\"1.0\" ?><root><face success=\"1\"/></root>");
			} else {
				out.write("<?xml version=\"1.0\" ?><root><face success=\"0\"/></root>");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write("<?xml version=\"1.0\" ?><root><face success=\"0\"/></root>");
		} finally {
			out.flush();
			out.close();
			out = null;
		}
	}
	private boolean deleteUploadFile(File file, boolean deleteDir) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File tempFile : files) {
				if (tempFile.isFile()) {
					if (!tempFile.delete()) {
						return false;
					}
				}
			}
			if (deleteDir) {
				file.delete();
			}
		}
		return true;
	}
	private byte[] flashDataDecode(String dataStr) {
		if ((dataStr.length() & 0x01) == 0x01) {
			dataStr = new String(dataStr + "0");
		}
		BigInteger cI = new BigInteger(dataStr, 16);
		byte[] data = cI.toByteArray();
		return data;
	}
	private boolean writeAvatar(File avatarFile, String avatar) {
		if (Common.empty(avatar)) {
			return false;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(avatarFile);
			byte[] bs = flashDataDecode(avatar);
			fos.write(bs, 1, bs.length - 1);
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
					fos = null;
				}
			} catch (Exception e) {
			}
		}
		return true;
	}

	
	@SuppressWarnings("unchecked")
	private ActionForward network(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> sConfig, Map<String, Object> space)
			throws Exception {
		if (Common.empty(sConfig.get("networkpublic"))) {
			if (Common.empty(sGlobal.get("supe_uid"))) {
				CookieHelper.setCookie(request, response, "_refer", Common.urlEncode((String) request
						.getAttribute("requestURI")));
				return showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
			}
		}
		int timestamp = (Integer) sGlobal.get("timestamp");
		Map<String, Map<String, Object>> globalNetWork = Common.getCacheDate(request, response,
				"/data/cache/cache_network.jsp", "globalNetWork");
		String jchRoot = JavaCenterHome.jchRoot;
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		List<Map<String, Object>> blogs = null;
		String cachePath = jchRoot + "data/cache/cache_network_blog.txt";
		Map<String, Object> netWork = getNetWork(globalNetWork, "blog");
		if (check_network_cache(netWork, cachePath, timestamp)) {
			blogs = Serializer.unserialize(FileHelper.readFile(cachePath));
		} else {
			Map<String, String> result = mk_network_sql(netWork, timestamp, new String[] {"blogid", "uid"},
					new String[] {"hot", "viewnum", "replynum"}, new String[] {"dateline"}, new String[] {
							"dateline", "viewnum", "replynum", "hot"});
			String sql = "SELECT main.blogid, main.uid, main.username, main.subject, main.hot, main.dateline, field.message FROM "
					+ JavaCenterHome.getTableName("blog")
					+ " main LEFT JOIN "
					+ JavaCenterHome.getTableName("blogfield")
					+ " field ON field.blogid=main.blogid WHERE "
					+ result.get("wheres")
					+ " AND main.friend='0' ORDER BY main."
					+ result.get("order")
					+ " " + result.get("sc") + " LIMIT 0,6";
			blogs = dataBaseService.executeQuery(sql);
			for (Map<String, Object> blog : blogs) {
				blog.put("subject", Common.getStr((String) blog.get("subject"), 50, false, false, false, 0,
						-1, request, response));
				blog.put("message", Common.getStr((String) blog.get("message"), 86, false, false, false, 0,
						-1, request, response));
				blog.put("dateline", Common.sgmdate(request, "MM-dd HH:mm", (Integer) blog.get("dateline"),
						true));
			}
			if ((Integer) netWork.get("cache") > 0) {
				FileHelper.writeFile(cachePath, Serializer.serialize(blogs));
			}
		}
		if (!Common.empty(blogs)) {
			for (Map<String, Object> blog : blogs) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) blog.get("uid"), (String) blog
						.get("username"), null, 0);
			}
			request.setAttribute("blogs", blogs);
		}
		List<Map<String, Object>> pics = null;
		cachePath = jchRoot + "data/cache/cache_network_pic.txt";
		netWork = getNetWork(globalNetWork, "pic");
		if (check_network_cache(netWork, cachePath, timestamp)) {
			pics = Serializer.unserialize(FileHelper.readFile(cachePath));
		} else {
			pics = new ArrayList<Map<String, Object>>();
			Map<String, String> result = mk_network_sql(netWork, timestamp, new String[] {"picid", "uid"},
					new String[] {"hot"}, new String[] {"dateline"}, new String[] {"dateline", "hot"});
			String sql = "SELECT album.albumname, album.friend, space.username, space.name, space.namestatus, main.picid, main.uid, main.dateline, main.filepath, main.thumb, main.remote, main.hot FROM "
					+ JavaCenterHome.getTableName("pic")
					+ " main LEFT JOIN "
					+ JavaCenterHome.getTableName("album")
					+ " album ON album.albumid=main.albumid LEFT JOIN "
					+ JavaCenterHome.getTableName("space")
					+ " space ON space.uid=main.uid WHERE "
					+ result.get("wheres")
					+ " ORDER BY main."
					+ result.get("order")
					+ " "
					+ result.get("sc")
					+ " LIMIT 0,28";
			List<Map<String, Object>> picList = dataBaseService.executeQuery(sql);
			for (Map<String, Object> pic : picList) {
				if (Common.empty(pic.get("friend"))) {
					pic.put("filepath", Common.pic_get(sConfig, (String) pic.get("filepath"), (Integer) pic
							.get("thumb"), (Integer) pic.get("remote"), true));
					pic.put("dateline", Common.sgmdate(request, "MM-dd HH:mm", (Integer) pic.get("dateline"),
							true));
					pic.remove("friend");
					pic.remove("thumb");
					pic.remove("remote");
					pics.add(pic);
				}
			}
			if ((Integer) netWork.get("cache") > 0) {
				FileHelper.writeFile(cachePath, Serializer.serialize(pics));
			}
		}
		if (!Common.empty(pics)) {
			for (Map<String, Object> pic : pics) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) pic.get("uid"), (String) pic
						.get("username"), (String) pic.get("name"), (Integer) pic.get("namestatus"));
			}
			request.setAttribute("pics", pics);
		}
		List<Map<String, Object>> threads = null;
		cachePath = jchRoot + "data/cache/cache_network_thread.txt";
		netWork = getNetWork(globalNetWork, "thread");
		if (check_network_cache(netWork, cachePath, timestamp)) {
			threads = Serializer.unserialize(FileHelper.readFile(cachePath));
		} else {
			Map<String, String> result = mk_network_sql(netWork, timestamp, new String[] {"tid", "uid"},
					new String[] {"hot", "viewnum", "replynum"}, new String[] {"dateline", "lastpost"},
					new String[] {"dateline", "viewnum", "replynum", "hot"});
			String sql = "SELECT main.tid, main.tagid, main.subject, main.uid, main.username, main.hot, m.tagname FROM "
					+ JavaCenterHome.getTableName("thread")
					+ " main LEFT JOIN "
					+ JavaCenterHome.getTableName("mtag")
					+ " m ON m.tagid=main.tagid WHERE "
					+ result.get("wheres")
					+ " ORDER BY main."
					+ result.get("order")
					+ " "
					+ result.get("sc")
					+ " LIMIT 0,10";
			threads = dataBaseService.executeQuery(sql);
			for (Map<String, Object> thread : threads) {
				thread.put("tagname", Common.getStr((String) thread.get("tagname"), 20, false, false, false,
						0, 0, request, response));
				thread.put("subject", Common.getStr((String) thread.get("subject"), 50, false, false, false,
						0, 0, request, response));
			}
			if ((Integer) netWork.get("cache") > 0) {
				FileHelper.writeFile(cachePath, Serializer.serialize(threads));
			}
		}
		if (!Common.empty(threads)) {
			for (Map<String, Object> thread : threads) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) thread.get("uid"), (String) thread
						.get("username"), null, 0);
			}
			request.setAttribute("threads", threads);
		}
		String dateformat = "MM-dd HH:mm";
		Map<Object, Map<String, Object>> globalEventClass = Common.getCacheDate(request, response,
				"/data/cache/cache_eventclass.jsp", "globalEventClass");
		List<Map<String, Object>> events = null;
		cachePath = jchRoot + "data/cache/cache_network_event.txt";
		netWork = getNetWork(globalNetWork, "event");
		if (check_network_cache(netWork, cachePath, timestamp)) {
			events = Serializer.unserialize(FileHelper.readFile(cachePath));
		} else {
			Map<String, String> result = mk_network_sql(netWork, timestamp, new String[] {"eventid", "uid"},
					new String[] {"hot", "membernum", "follownum"}, new String[] {"dateline"}, new String[] {
							"dateline", "membernum", "follownum", "hot"});
			String sql = "SELECT main.eventid, main.uid, main.username, main.title, main.classid, main.province, main.city, main.location, main.poster, main.thumb, main.remote, main.starttime, main.endtime, main.membernum, main.follownum FROM "
					+ JavaCenterHome.getTableName("event")
					+ " main WHERE "
					+ result.get("wheres")
					+ " ORDER BY main." + result.get("order") + " " + result.get("sc") + " LIMIT 0,6";
			events = dataBaseService.executeQuery(sql);
			for (Map<String, Object> event : events) {
				event.put("title", Common.getStr((String) event.get("title"), 45, false, false, false, 0, 0,
						request, response));
				String poster = (String) event.get("poster");
				if (Common.empty(poster)) {
					event.put("poster", globalEventClass.get(event.get("classid")).get("poster"));
				} else {
					event.put("poster", Common.pic_get(sConfig, poster, (Integer) event.get("thumb"),
							(Integer) event.get("remote"), true));
				}
				event.put("starttime", Common.sgmdate(request, dateformat, (Integer) event.get("starttime"),
						false));
				event.put("endtime", Common.sgmdate(request, dateformat, (Integer) event.get("endtime"),
						false));
				event.remove("classid");
				event.remove("thumb");
				event.remove("remote");
			}
			if ((Integer) netWork.get("cache") > 0) {
				FileHelper.writeFile(cachePath, Serializer.serialize(events));
			}
		}
		if (!Common.empty(events)) {
			for (Map<String, Object> event : events) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) event.get("uid"), (String) event
						.get("username"), null, 0);
			}
			request.setAttribute("events", events);
		}
		List<Map<String, Object>> polls = null;
		cachePath = jchRoot + "data/cache/cache_network_poll.txt";
		netWork = getNetWork(globalNetWork, "poll");
		if (check_network_cache(netWork, cachePath, timestamp)) {
			polls = Serializer.unserialize(FileHelper.readFile(cachePath));
		} else {
			Map<String, String> result = mk_network_sql(netWork, timestamp, new String[] {"pid", "uid"},
					new String[] {"hot", "voternum", "replynum"}, new String[] {"dateline"}, new String[] {
							"dateline", "voternum", "replynum", "hot"});
			String sql = "SELECT main.pid, main.uid, main.username, main.subject, main.voternum FROM "
					+ JavaCenterHome.getTableName("poll") + " main WHERE " + result.get("wheres")
					+ " ORDER BY main." + result.get("order") + " " + result.get("sc") + " LIMIT 0,9";
			polls = dataBaseService.executeQuery(sql);
			if ((Integer) netWork.get("cache") > 0) {
				FileHelper.writeFile(cachePath, Serializer.serialize(polls));
			}
		}
		if (!Common.empty(polls)) {
			for (Map<String, Object> poll : polls) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) poll.get("uid"), (String) poll
						.get("username"), null, 0);
			}
			request.setAttribute("polls", polls);
		}
		List<Map<String, Object>> doings = dataBaseService
				.executeQuery("SELECT doid, uid, username, dateline, message  FROM "
						+ JavaCenterHome.getTableName("doing") + " ORDER BY dateline DESC LIMIT 0,5");
		if (doings.size() > 0) {
			for (Map<String, Object> doing : doings) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) doing.get("uid"), (String) doing
						.get("username"), null, 0);
				doing.put("title", Common.getStr((String) doing.get("message"), 0, false, false, false, 0,
						-1, request, response));
				doing.put("dateline", Common.sgmdate(request, "HH:mm", (Integer) (Integer) doing
						.get("dateline"), true));
			}
			request.setAttribute("doings", doings);
		}
		List<Map<String, Object>> stars = null;
		Object spaceBarUserName = sConfig.get("spacebarusername");
		if (!Common.empty(spaceBarUserName)) {
			stars = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("space")
					+ " WHERE username IN (" + Common.sImplode(spaceBarUserName.toString().split(","))
					+ ") ORDER BY rand() limit 1");
		}
		List<Map<String, Object>> shows = dataBaseService.executeQuery("SELECT sh.note, s.* FROM "
				+ JavaCenterHome.getTableName("show") + " sh LEFT JOIN "
				+ JavaCenterHome.getTableName("space")
				+ " s ON s.uid=sh.uid ORDER BY sh.credit DESC LIMIT 0,23");
		if (shows.size() > 0) {
			for (Map<String, Object> show : shows) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) show.get("uid"), (String) show
						.get("username"), (String) show.get("name"), (Integer) show.get("namestatus"));
				show.put("note", Common.addSlashes(Common.getStr((String) show.get("note"), 80, false, false,
						false, 0, -1, request, response)));
			}
			if (Common.empty(stars)) {
				stars = Common.getRandList(shows, 1);
			}
			request.setAttribute("shows", shows);
		}
		List<Map<String, Object>> onlines = dataBaseService.executeQuery("SELECT s.*, sf.note FROM "
				+ JavaCenterHome.getTableName("session") + " s LEFT JOIN "
				+ JavaCenterHome.getTableName("spacefield")
				+ " sf ON sf.uid=s.uid WHERE s.magichidden = 0 ORDER BY s.lastactivity DESC LIMIT 0,12");
		if (onlines.size() > 0) {
			for (Map<String, Object> online : onlines) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) online.get("uid"), (String) online
						.get("username"), null, 0);
				online.put("note", Common.sHtmlSpecialChars(Common.stripTags((String) online.get("note"))));
			}
			if (Common.empty(stars)) {
				stars = Common.getRandList(onlines, 1);
				for (Map<String, Object> value : stars) {
					List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("space") + " WHERE uid='" + value.get("uid") + "'");
					if (query.size() > 0) {
						value.putAll(query.get(0));
					}
				}
			}
			request.setAttribute("onlines", onlines);
		}

		if (!Common.empty(stars)) {
			Map<String, Object> star = stars.get(0);
			Common.realname_set(sGlobal, sConfig, sNames, (Integer) star.get("uid"), (String) star
					.get("username"), (String) star.get("name"), star.get("namestatus") == null ? 0
					: (Integer) star.get("namestatus"));
			star.put("updatetime", Common.sgmdate(request, "HH:mm", star.get("updatetime") == null ? 0
					: (Integer) star.get("updatetime"), true));
			request.setAttribute("star", star);
		}
		request.setAttribute("onlineCount", dataBaseService.findRows("SELECT COUNT(*) FROM "
				+ JavaCenterHome.getTableName("session")));
		int myAppCount = 0;
		if ((Integer) sConfig.get("my_status") == 1) {
			myAppCount = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("myapp") + " WHERE flag>='0'");
			if (myAppCount > 0) {
				List<Map<String, Object>> myApps = dataBaseService.executeQuery("SELECT appid,appname FROM "
						+ JavaCenterHome.getTableName("myapp")
						+ " WHERE flag>=0 ORDER BY flag DESC, displayorder LIMIT 0,7");
				request.setAttribute("myApps", myApps);
			}
		}
		request.setAttribute("myAppCount", myAppCount);
		List<Map<String, Object>> shares = null;
		cachePath = jchRoot + "data/cache/cache_network_share.txt";
		netWork = getNetWork(globalNetWork, "share");
		if (check_network_cache(netWork, cachePath, timestamp)) {
			shares = Serializer.unserialize(FileHelper.readFile(cachePath));
		} else {
			Map<String, String> result = mk_network_sql(netWork, timestamp, new String[] {"sid", "uid"},
					new String[] {"hot"}, new String[] {"dateline"}, new String[] {"dateline", "hot"});
			String sql = "SELECT * FROM " + JavaCenterHome.getTableName("share") + " main LEFT JOIN "
					+ JavaCenterHome.getTableName("space") + " space ON space.uid=main.uid WHERE "
					+ result.get("wheres") + " ORDER BY main." + result.get("order") + " " + result.get("sc")
					+ " LIMIT 0,6";
			shares = dataBaseService.executeQuery(sql);
			for (Map<String, Object> share : shares) {
				Map<String, String> bodyData = Serializer.unserialize((String) share.get("body_data"), false);
				String type = (String) share.get("type");
				boolean iscut = false;
				if (!Common.empty(bodyData)) {
					Set<String> keys = bodyData.keySet();
					for (String key : keys) {
						if (!iscut && ("blog".equals(type) || "thread".equals(type))) {
							bodyData.put("message", Common.cutstr(bodyData.get("message"), 40));
							iscut = true;
						}
						if (!iscut && "poll".equals(type)) {
							String subject = bodyData.get("subject");
							List<String> strList = Common.pregMatch(subject,
									"(?is)<a href=\"(.+?)\">(.+?)</a>");
							if (strList.size() > 0) {
								subject = "<a href=\"" + strList.get(1) + "\">"
										+ Common.cutstr(strList.get(2), 40) + "</a>";
							}
							bodyData.put("subject", subject);
							iscut = true;
						}
						String body_template = ((String) share.get("body_template")).replace("{" + key + "}",
								bodyData.get(key));
						share.put("body_template", body_template);
					}
				}
				share.put("body_data", bodyData);
				share.put("dateline", Common.sgmdate(request, dateformat, (Integer) share.get("dateline"),
						true));
				share.put("body_general", Common.cutstr((String) share.get("body_general"), 40));
			}
			if ((Integer) netWork.get("cache") > 0) {
				FileHelper.writeFile(cachePath, Serializer.serialize(shares));
			}
		}
		if (!Common.empty(shares)) {
			for (Map<String, Object> share : shares) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) share.get("uid"), (String) share
						.get("username"), null, 0);
			}
			request.setAttribute("shares", shares);
		}
		Map<String, String> shareClasses = new LinkedHashMap<String, String>();
		shareClasses.put("link", "网址");
		shareClasses.put("video", "视频");
		shareClasses.put("music", "音乐");
		shareClasses.put("flash", "Flash");
		shareClasses.put("blog", "日志");
		shareClasses.put("album", "相册");
		shareClasses.put("pic", "图片");
		shareClasses.put("mtag", "群组");
		shareClasses.put("thread", "话题");
		shareClasses.put("poll", "投票");
		shareClasses.put("event", "活动");
		shareClasses.put("space", "用户");
		shareClasses.put("tag", "TAG");
		request.setAttribute("shareClasses", shareClasses);

		Common.realname_get(sGlobal, sConfig, sNames, space);
		Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
		String loginUser = sCookie.get("loginuser");
		request.setAttribute("memberName", Common.empty(loginUser) ? "" : Common.stripSlashes(loginUser));
		request.setAttribute("tpl_css", "network");
		return include(request, response, sConfig, sGlobal, "network.jsp");
	}

	private Map<String, Object> getNetWork(Map<String, Map<String, Object>> globalNetWork, String type) {
		Map<String, Object> netWork = globalNetWork.get(type);
		if (Common.empty(netWork)) {
			netWork = new HashMap<String, Object>();
		}
		Object cache = netWork.get("cache");
		if (Common.empty(cache)) {
			netWork.put("cache", 0);
		}
		return netWork;
	}
	private boolean check_network_cache(Map<String, Object> netWork, String filePath, int timestamp) {
		File cacheFile = new File(filePath);
		int cacheTime = (int) (cacheFile.lastModified() / 1000);
		if (timestamp - cacheTime < (Integer) netWork.get("cache")) {
			return true;
		}
		return false;
	}
	private Map<String, String> mk_network_sql(Map<String, Object> netWork, int timestamp, String[] ids,
			String[] crops, String[] days, String[] orders) {
		StringBuffer wheres = new StringBuffer();
		wheres.append("1");
		for (String id : ids) {
			Object value = netWork.get(id);
			if (!Common.empty(value)) {
				wheres.append(" AND main." + id + " IN (" + value + ")");
			}
		}
		for (String crop : crops) {
			String crop1 = crop + "1";
			String crop2 = crop + "2";
			Object value1 = netWork.get(crop1);
			Object value2 = netWork.get(crop2);
			if (!Common.empty(value1)) {
				wheres.append(" AND main." + crop + " >= '" + value1 + "'");
			}
			if (!Common.empty(value2)) {
				wheres.append(" AND main." + crop + " <= '" + value2 + "'");
			}
		}
		for (String day : days) {
			Object value = netWork.get(day);
			if (!Common.empty(value)) {
				int daytime = timestamp - (Integer) value * 3600 * 24;
				wheres.append(" AND main." + day + " >= '" + daytime + "'");
			}
		}
		String order = Common.in_array(orders, netWork.get("order")) ? netWork.get("order").toString()
				: orders[1];
		String sc = Common.in_array(new String[] {"desc", "asc"}, netWork.get("sc")) ? netWork.get("sc")
				.toString() : "desc";
		Map<String, String> result = new HashMap<String, String>();
		result.put("wheres", wheres.toString());
		result.put("order", order);
		result.put("sc", sc);
		return result;
	}
}