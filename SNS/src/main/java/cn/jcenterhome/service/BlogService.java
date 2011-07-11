
package cn.jcenterhome.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;


public class BlogService {
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
	public String blogBBCode(String message) {
		if (Common.empty(message)) {
			return message;
		}
		Matcher matcher = Pattern.compile("(?i)\\[flash\\=?(media|real)*\\](.+?)\\[\\/flash\\]").matcher(
				message);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, blogFlash(matcher.group(2), matcher.group(1)));
		}
		matcher.appendTail(sb);
		message= sb.toString();
		return message;
	}
	private String blogFlash(String swfURL, String type) {
		String html = null;
		String width = "520";
		String height = "390";
		if ("media".equals(type)) {
			html = "<object classid=\"clsid:6bf52a52-394a-11d3-b153-00c04f79faa6\" width=\"" + width
					+ "\" height=\"" + height + "\">" + "<param name=\"autostart\" value=\"0\">"
					+ "<param name=\"url\" value=\"" + swfURL + "\">" + "<embed autostart=\"false\" src=\""
					+ swfURL + "\" type=\"video/x-ms-wmv\" width=\"" + width + "\" height=\"" + height
					+ "\" controls=\"imagewindow\" console=\"cons\"></embed>" + "</object>";
		} else if ("real".equals(type)) {
			html = "<object classid=\"clsid:cfcdaa03-8be4-11cf-b84b-0020afbbccfa\" width=\"" + width
					+ "\" height=\"" + height + "\">" + "<param name=\"autostart\" value=\"0\">"
					+ "<param name=\"src\" value=\"" + swfURL + "\">"
					+ "<param name=\"controls\" value=\"Imagewindow,controlpanel\">"
					+ "<param name=\"console\" value=\"cons\">" + "<embed autostart=\"false\" src=\""
					+ swfURL + "\" type=\"audio/x-pn-realaudio-plugin\" width=\"" + width + "\" height=\""
					+ height + "\" controls=\"controlpanel\" console=\"cons\"></embed>" + "</object>";
		} else {
			html = "<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" width=\"" + width
					+ "\" height=\"" + height + "\">" + "<param name=\"movie\" value=\"" + swfURL + "\">"
					+ "<param name=\"allowscriptaccess\" value=\"always\">" + "<embed src=\"" + swfURL
					+ "\" type=\"application/x-shockwave-flash\" width=\"" + width + "\" height=\"" + height
					+ "\" allowfullscreen=\"true\" allowscriptaccess=\"always\"></embed>" + "</object>";
		}
		return html;
	}

	
	public Map<String, Object> blogPost(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> olds) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal_bak = null;

		boolean isSelf = true;

		if (olds.get("uid") != null && olds.get("uid") != sGlobal.get("supe_uid")) {
			
			isSelf = false;
			sGlobal_bak = new HashMap<String, Object>(sGlobal);
			sGlobal.put("supe_uid", olds.get("uid"));
			sGlobal.put("supe_username", Common.addSlashes((String) olds.get("username")));
		}
		String subject = request.getParameter("subject").trim();
		String message = request.getParameter("message").trim();
		String tag = request.getParameter("tag").trim();
		int friend = Common.intval(request.getParameter("friend"));
		String targetIds = "";
		String password = "";
		int classId = 0;

		if (subject.length() != 0) {
			subject = Common.getStr(subject, 80, true, true, true, 0, 0, request, response);
		} else {
			subject = Common.sgmdate(request, "yyyy-MM-dd", (Integer) sGlobal.get("timestamp"));
		}
		if (sGlobal.get("mobile") != null) {
			message = Common.getStr(message, 0, true, false, true, 1, 0, request, response);
		} else {
			message = Common.getStr(checkHtml(request, response, message), 0, true, false, true, 0, 1,
					request, response);
			message = message.replaceAll("(?i)<div></div>", "").replace("(?i)<a\\s+href=\"([^>]+?)\">",
					"<a href=\"$1\" target=\"_blank\">");
		}
		if (tag.length() != 0) {
			tag = Common.getStr((String) Common.sHtmlSpecialChars(tag), 500, true, true, true, 0, 0, request,
					response);
		}
		if (friend == 2) {
			String[] names = request.getParameter("target_names") == null ? null : request.getParameter(
					"target_names").replaceAll(Common.getMessage(request, "cp_tab_space"), " ").split(" ");
			if (!Common.empty(names)) {
				List<String> uids = dataBaseService.executeQuery("SELECT uid FROM "
						+ JavaCenterHome.getTableName("space") + " WHERE username IN ("
						+ Common.sImplode(names) + ")", 1);
				if (uids.size() == 0) {
					friend = 3;
				} else {
					targetIds = Common.implode(uids, ",");
				}
			}
		} else if (friend == 4) {
			password = request.getParameter("password").trim();
			if (password.equals("")) {
				friend = 0;
			}
		}
		String className = null;
		String classIdStr = request.getParameter("classid");
		if (!Common.empty(classIdStr)) {
			if (classIdStr.startsWith("new:")) {
				
				className = (String) Common.sHtmlSpecialChars(classIdStr.substring(4).trim());
				if (className.length() != 0) {
					className = Common.getStr(className, 0, true, true, true, 0, 0, request, response);
					Map<String, Object> whereArr = new HashMap<String, Object>();
					whereArr.put("classname", className);
					whereArr.put("uid", sGlobal.get("supe_uid"));
					classId = Common.intval(Common.getCount("class", whereArr, "classid"));
					if (classId == 0) {
						Map<String, Object> setArr = new HashMap<String, Object>();
						setArr.put("classname", className);
						setArr.put("uid", sGlobal.get("supe_uid"));
						setArr.put("dateline", sGlobal.get("timestamp"));
						classId = dataBaseService.insertTable("class", setArr, true, false);
					}
				}
			} else if (!classIdStr.equals(String.valueOf(olds.get("classid")))) {
				
				classId = Common.intval(classIdStr);
			} else {
				
				classId = (Integer) olds.get("classid");
			}
		}
		if (classId != 0 && Common.empty(className)) {
			Map<String, Object> whereArr = new HashMap<String, Object>();
			whereArr.put("classid", classId);
			whereArr.put("uid", sGlobal.get("supe_uid"));
			if (Common.empty(Common.getCount("class", whereArr, "classname"))) {
				classId = 0;
			}
		}
		Map<String, Object> blogArr = new HashMap<String, Object>();
		blogArr.put("subject", subject);
		blogArr.put("classid", classId);
		blogArr.put("friend", friend);
		blogArr.put("password", password);
		blogArr.put("noreply", request.getParameter("noreply") == null ? 0 : 1);
		Map<Integer, String> picIdForm = new HashMap<Integer, String>();
		List<Integer> picIds = new ArrayList<Integer>();
		for (Enumeration paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
			String key = (String) paramNames.nextElement();
			if (key.startsWith("picids[")) {
				int picId = Integer.parseInt(key.replaceAll("picids\\[(\\d+)\\]", "$1"));
				picIdForm.put(picId, request.getParameter(key));
				picIds.add(picId);
			}
		}
		Map uploads = new HashMap();
		String titlePic = null;
		if (picIdForm.size() != 0) {
			List<Map<String, Object>> pictures = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("pic") + " WHERE picid IN (" + Common.sImplode(picIds)
					+ ") AND uid='" + sGlobal.get("supe_uid") + "'");
			Map<String, Object> pic = null;
			int picturesSize = pictures.size();
			for (int i = 0; i < picturesSize; i++) {
				pic = pictures.get(i);
				if (titlePic == null && !Common.empty(pic.get("thumb"))) {
					titlePic = pic.get("filepath") + ".thumb.jpg";
					blogArr.put("picflag", Common.empty(pic.get("remote")) ? 1 : 2);
				}
				int picId = (Integer) pic.get("picid");
				uploads.put(picIdForm.get(picId), pic);
			}
			if (titlePic == null && pic != null) {
				titlePic = (String) pic.get("filepath");
				blogArr.put("picflag", Common.empty(pic.get("remote")) ? 1 : 2);
			}
		}
		if (uploads.size() > 0) {
			String regex="(?i)<img.*src=\'(.+?)\'.*?_jchome_localimg_([0-9]+).+?src=\"(.+?)\"";
			if(Common.matches(message, regex)){
				message=message.replaceAll(regex, "<IMG id=_jchome_localimg_$2 src=\"$1\"");
			}else if(Common.matches(message, regex="(?i)<img\\s.*?_jchome_localimg_([0-9]+).+?src=\'(.+?)\'.+?src=\"(.+?)\"")){
				message=message.replaceAll(regex, "<IMG id=_jchome_localimg_$1 src=\"$2\"");
			}else{
				Matcher m = Pattern.compile("(?i)\\[local\\](\\d+)\\[\\/local\\]").matcher(message);
				while (m.find()) {
					String id=m.group(1);
					if(uploads.get(id)!=null){
						message=message.replace("[local]"+id+"[/local]", "<IMG id=_jchome_localimg_"+id+" src=\"img_"+id+"\">");
					}
				}
			}
			Matcher m = Pattern.compile("(?i)<img\\s.*?_jchome_localimg_([0-9]+).+?src=\"(.+?)\"").matcher(
					message);
			List<String> matches1 = new ArrayList<String>();
			List<String> matches2 = new ArrayList<String>();
			while (m.find()) {
				matches1.add(m.group(1));
				matches2.add(m.group(2));
			}

			int matchesLen = matches1.size();
			if (matchesLen != 0) {
				
				for (int i = 0; i < matchesLen; i++) {
					String index = matches1.get(i);
					Map value = (Map) uploads.get(index);
					if (!Common.empty(value)) {
						String search = matches2.get(i);
						String idSearch = "_jchome_localimg_" + index;
						String replace = Common.pic_get(sConfig, (String) value.get("filepath"),
								(Integer) value.get("thumb"), (Integer) value.get("remote"), false);
						message = message.replace(matches2.get(i), replace);
						message = message.replace(idSearch, "jchomelocalimg[]");
						uploads.remove(index);
					}
				}
			}
			for (Iterator it = uploads.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Map value = (Map) uploads.get(key);
				String picUrl = Common.pic_get(sConfig, (String) value.get("filepath"), (Integer) value
						.get("thumb"), (Integer) value.get("remote"), false);
				message += "<div class=\"jchome-message-pic\"><img src=\"" + picUrl + "\"><p>"
						+ value.get("title") + "</p></div>";
			}
		}
		String checkMessage = message.replaceAll("(?is)(<div>|</div>|\\s|&nbsp;|<br>|<p>|</p>)+", "");
		if (checkMessage.length() == 0)
			return null;
		message = Common.addSlashes(message);
		if (titlePic == null) {
			titlePic = getMessagePic(message);
			blogArr.put("picflag", 0);
		}
		blogArr.put("pic", titlePic);
		if (Common.checkPerm(request, response, "manageblog")) {
			blogArr.put("hot", Common.intval(request.getParameter("hot")));
		}
		CpService cpService = (CpService) BeanFactory.getBean("cpService");
		int blogId = 0;
		if (olds.get("blogid") != null) {
			
			blogId = (Integer) olds.get("blogid");
			Map<String, Object> whereData = new HashMap<String, Object>();
			whereData.put("blogid", blogId);
			dataBaseService.updateTable("blog", blogArr, whereData);

			blogArr.put("uid", olds.get("uid"));
			blogArr.put("username", olds.get("username"));
		} else {
			
			blogArr.put("topicid", cpService.checkTopic(request, Common.intval(request
					.getParameter("topicid")), "blog"));
			blogArr.put("uid", sGlobal.get("supe_uid"));
			blogArr.put("username", sGlobal.get("supe_username"));
			blogArr.put("dateline", Common.empty(request.getParameter("dateline")) ? sGlobal.get("timestamp")
					: request.getParameter("dateline"));
			blogId = dataBaseService.insertTable("blog", blogArr, true, false);
		}
		blogArr.put("blogid", blogId);
		Map<String, Object> fieldArr = new HashMap<String, Object>();
		fieldArr.put("message", message);
		fieldArr.put("postip", Common.getOnlineIP(request));
		fieldArr.put("target_ids", targetIds);
		String oldTagStr = Common.addSlashes(Common.empty(olds.get("tag")) ? "" : Common.implode(Serializer
				.unserialize((String) olds.get("tag"), false), " "));
		if (!oldTagStr.equals(tag)) {
			if (!Common.empty(olds.get("tag"))) {
				List<String> oldTags = dataBaseService.executeQuery("SELECT tagid FROM "
						+ JavaCenterHome.getTableName("tagblog") + " WHERE blogid='" + blogId + "'", 1);
				if (oldTags.size() > 0) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag")
							+ " SET blognum=blognum-1 WHERE tagid IN (" + Common.sImplode(oldTags) + ")");
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagblog")
							+ " WHERE blogid='" + blogId + "'");
				}
			}
			Map<Integer, String> tagArr = tagBatch(sGlobal, blogId, tag);
			fieldArr.put("tag", Common.empty(tagArr) ? "" : Common.addSlashes(Serializer.serialize(tagArr)));
		}

		if (!Common.empty(olds)) {
			
			Map<String, Object> whereData = new HashMap<String, Object>();
			whereData.put("blogid", blogId);
			dataBaseService.updateTable("blogfield", fieldArr, whereData);
		} else {
			
			fieldArr.put("blogid", blogId);
			fieldArr.put("uid", blogArr.get("uid"));
			fieldArr.put("related", "");
			fieldArr.put("hotuser", "");
			dataBaseService.insertTable("blogfield", fieldArr, false, false);
		}
		if (isSelf) {
			if (!Common.empty(olds)) {
				
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET updatetime='" + sGlobal.get("timestamp") + "' WHERE uid='"
						+ sGlobal.get("supe_uid") + "'");
			} else {
				
				String blogNumSql = null;
				Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
				if (Common.empty(space.get("blognum"))) {
					Map<String, Object> whereArr = new HashMap<String, Object>();
					whereArr.put("uid", space.get("uid"));
					space.put("blognum", Common.getCount("blog", whereArr, null));
					blogNumSql = "blognum=" + space.get("blognum");
				} else {
					blogNumSql = "blognum=blognum+1";
				}
				Map<String, Integer> reward = Common.getReward("publishblog", false, 0, "", true, request,
						response);
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
						+ blogNumSql + ", lastpost='" + sGlobal.get("timestamp") + "', updatetime='"
						+ sGlobal.get("timestamp") + "', credit=credit+" + reward.get("credit")
						+ ", experience=experience+" + reward.get("experience") + " WHERE uid='"
						+ sGlobal.get("supe_uid") + "'");
				cpService.updateStat(request, "blog", false);
			}
		}
		if (!Common.empty(request.getParameter("makefeed"))) {
			FeedService feedService = (FeedService) BeanFactory.getBean("feedService");
			feedService.feedPublish(request, response, blogId, "blogid", Common.empty(olds) ? true : false);
		}
		if (Common.empty(olds) && !Common.empty(blogArr.get("topicid"))) {
			cpService.topicJoin(request, (Integer) blogArr.get("topicid"), (Integer) sGlobal.get("supe_uid"),
					(String) sGlobal.get("supe_username"));
		}
		if (sGlobal_bak != null) {
			sGlobal = new HashMap<String, Object>(sGlobal_bak);
		}

		return blogArr;
	}

	
	private Map<Integer, String> tagBatch(Map<String, Object> sGlobal, int blogId, String tags) {
		Map<Integer, String> tagArr = new HashMap<Integer, String>();
		Set<String> tagNames = new HashSet<String>();
		if (!Common.empty(tags)) {
			String[] tmp = tags.split(" ");
			for (String tagName : tmp) {
				tagNames.add(tagName);
			}
		} else {
			return tagArr;
		}

		Map<String, Object> vtags = new HashMap<String, Object>();
		List<Map<String, Object>> tagList = dataBaseService.executeQuery("SELECT tagid, tagname, close FROM "
				+ JavaCenterHome.getTableName("tag") + " WHERE tagname IN (" + Common.sImplode(tagNames)
				+ ")");
		for (Map<String, Object> value : tagList) {
			String vkey = Common.md5(Common.addSlashes((String) value.get("tagname")));
			vtags.put(vkey, value);
		}
		List<Integer> updateTagIds = new ArrayList<Integer>();
		for (String tagName : tagNames) {//"^([\\x7f-\\xff_-]|\\w){3,20}$"
			if (!Common.matches(tagName, "^([\u2E80-\u9FFF]+|\\w){3,20}$")) {
				continue;
			}
			String vkey = Common.md5(tagName);
			if (Common.empty(vtags.get(vkey))) {
				Map<String, Object> setArr = new HashMap<String, Object>();
				setArr.put("tagname", tagName);
				setArr.put("uid", sGlobal.get("supe_uid"));
				setArr.put("dateline", sGlobal.get("timestamp"));
				setArr.put("blognum", 1);
				int tagId = dataBaseService.insertTable("tag", setArr, true, false);
				tagArr.put(tagId, tagName);
			} else {
				Map<String, Object> t = (Map) vtags.get(vkey);
				if (Common.empty(t.get("close"))) {
					int tagId = (Integer) t.get("tagid");
					updateTagIds.add(tagId);
					tagArr.put(tagId, tagName);
				}
			}
		}
		if (updateTagIds.size() > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag")
					+ " SET blognum=blognum+1 WHERE tagid IN (" + Common.sImplode(updateTagIds) + ")");
		}
		Set<Integer> tagIds = tagArr.keySet();
		List<String> inserts = new ArrayList<String>();
		for (int tagId : tagIds) {
			inserts.add("('" + tagId + "','" + blogId + "')");
		}
		if (inserts.size() > 0) {
			dataBaseService.execute("REPLACE INTO " + JavaCenterHome.getTableName("tagblog")
					+ " (tagid,blogid) VALUES " + Common.implode(inserts, ","));
		}
		return tagArr;
	}

	
	public String getMessagePic(String message) {
		String pic = "";
		message = Common.stripSlashes(message);
		message = message.replaceAll("(?is)<img src=\".*?image/face/(.+?).gif\".*?>\\s*", "");
		Matcher m = Pattern.compile("(?i)src=[\"\']*([^>\\s]{25,105})\\.(jpg|gif|png)").matcher(message);
		if (m.find()) {
			pic = m.group(1) + "." + m.group(2);
		}
		return Common.addSlashes(pic);
	}

	
	public String checkHtml(HttpServletRequest request, HttpServletResponse response, String html) {
		html = Common.stripSlashes(html);
		if (!Common.checkPerm(request, response, "allowhtml")) {
			
			Pattern p = Pattern.compile("(?is)<([^<]+)>");
			Matcher m = p.matcher(html);
			Set<String> values = new HashSet<String>();
			while (m.find()) {
				values.add(m.group(1));
			}
			String allowTags = "img|a|font|div|table|tbody|caption|tr|td|th|br|p|b|strong|i|u|em|span|ol|ul|li|blockquote|object|param|embed";
			html = html.replace("<", "&lt;");
			html = html.replace(">", "&gt;");
			for (String replace : values) {
				String search = "&lt;" + replace + "&gt;";
				replace = (String) Common.sHtmlSpecialChars(replace);
				replace = replace.replace("\\\\", ".");
				replace = replace.replace("/*", "/.");
				replace = replace.replaceAll("(?i)(javascript|script|eval|behaviour|expression)", ".");
				replace = replace.replaceAll("(?i)(\\s+|&quot;|')on", " .");
				if (!Common.matches(replace, "(?is)^[/|\\s]?(" + allowTags + ")(\\s+|$)")) {
					replace = "";
				}
				replace=Common.empty(replace)? "" :"<"+replace.replace("&quot;", "\"")+">";
				html = html.replace(search, replace);
			}
		}
		html = Common.addSlashes(html);
		return html;
	}

	
	public List<Map<String, Object>> deleteBlogs(HttpServletRequest request, HttpServletResponse response,
			Integer... blogIds) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		boolean allowManage = Common.checkPerm(request, response, "manageblog");
		boolean manageBatch = Common.checkPerm(request, response, "managebatch");

		List<Map<String, Object>> blogs = dataBaseService
				.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("blog") + " WHERE blogid IN ("
						+ Common.sImplode(blogIds) + ")");
		int delnum = 0;
		boolean isBlogEmpty = true;
		for(Map<String, Object> value : blogs) {
			if(allowManage || value.get("uid").equals(sGlobal.get("supe_uid"))) {
				isBlogEmpty = false;
				if(!manageBatch && !value.get("uid").equals(sGlobal.get("supe_uid"))) {
					delnum++;
				}
			}
		}
		
		if(isBlogEmpty || (!manageBatch && delnum > 1)) {
			return null;
		}
		Map<String, Integer> reward = Common.getReward("delblog", false, 0, "", true, request, response);
		List<Object> delBlogIds = new ArrayList<Object>();
		for (Map<String, Object> value : blogs) {
			delBlogIds.add(value.get("blogid"));
			if (allowManage && value.get("uid") != sGlobal.get("supe_uid")) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + reward.get("credit") + ",experience=experience-"
						+ reward.get("experience") + " WHERE uid='" + value.get("uid") + "'");
			}
			List<String> tags = dataBaseService.executeQuery("SELECT tagid FROM "
					+ JavaCenterHome.getTableName("tagblog") + " WHERE blogid='" + value.get("blogid") + "'",
					1);
			if (tags.size() > 0) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag")
						+ " SET blognum=blognum-1 WHERE tagid IN (" + Common.sImplode(tags) + ")");
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagblog")
						+ " WHERE blogid='" + value.get("blogid") + "'");
			}
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("comment")
				+ " WHERE id IN (" + Common.sImplode(delBlogIds) + ") AND idtype='blogid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
				+ " WHERE id IN (" + Common.sImplode(delBlogIds) + ") AND idtype='blogid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE id IN ("
				+ Common.sImplode(delBlogIds) + ") AND idtype='blogid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("clickuser")
				+ " WHERE id IN (" + Common.sImplode(delBlogIds) + ") AND idtype='blogid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("blog")
				+ " WHERE blogid IN(" + Common.sImplode(delBlogIds) + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("blogfield")
				+ " WHERE blogid IN (" + Common.sImplode(delBlogIds) + ")");

		return blogs;
	}
}
