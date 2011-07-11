
package cn.jcenterhome.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;

public class SpaceService {
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");

	
	public Map<String, Object> openSpace(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> sConfig, int uid, String userName, int gid,
			String email) throws Exception {
		if (uid == 0 || Common.empty(userName)) {
			return null;
		}
		int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
				+ JavaCenterHome.getTableName("spacelog") + " WHERE uid=" + uid + " AND flag=-1");
		if (count > 0) {
			throw new Exception("the_space_has_been_closed");
		}
		Map<String, Object> space = new HashMap<String, Object>();
		space.put("uid", uid);
		space.put("username", userName);
		space.put("namestatus", 0);
		space.put("avatar", 0);
		space.put("dateline", sGlobal.get("timestamp"));
		space.put("groupid", gid);
		space.put("regip", Common.getOnlineIP(request));
		Map<String, Integer> reward = Common.getReward("register", false, uid, "", true, request, response);
		int credit = reward.get("credit");
		if (credit != 0) {
			space.put("credit", credit);
		}
		int experience = reward.get("experience");
		if (experience != 0) {
			space.put("experience", experience);
		}
		Map<String, Object> spaceField = new HashMap<String, Object>();
		spaceField.put("uid", uid);
		spaceField.put("email", email);
		spaceField.put("note", "");
		spaceField.put("spacenote", "");
		spaceField.put("css", "");
		spaceField.put("privacy", "");
		spaceField.put("friend", "");
		spaceField.put("feedfriend", "");
		spaceField.put("sendmail", "");
		dataBaseService.insertTable("space", space, false, true);
		dataBaseService.insertTable("spacefield", spaceField, false, true);
		CpService cpService = (CpService) BeanFactory.getBean("cpService");
		cpService.sendEmailCheck(request, response, uid, email);
		int _uid = (Integer) sGlobal.get("supe_uid");
		String _userName = (String) sGlobal.get("supe_username");
		sGlobal.put("supe_uid", uid);
		sGlobal.put("supe_username", Common.addSlashes(userName));
		if (Common.ckPrivacy(sGlobal, sConfig, space, "spaceopen", 1)) {
			cpService.addFeed(sGlobal, "profile", Common.getMessage(request, "cp_feed_space_open"), null, "",
					null, "", null, null, "", 0, 0, 0, "", false);
		}
		if ((Integer) sConfig.get("newspacenum") > 0) {
			List<String> whereArr = new ArrayList<String>();
			whereArr.add("1");
			if (!Common.empty(sConfig.get("newspaceavatar"))) {
				whereArr.add("avatar='1'");
			}
			if (!Common.empty(sConfig.get("newspacerealname"))) {
				whereArr.add("namestatus='1'");
			}
			if (!Common.empty(sConfig.get("newspacevideophoto"))) {
				whereArr.add("videostatus='1'");
			}
			List<Map<String, Object>> newSpaceList = dataBaseService
					.executeQuery("SELECT uid,username,name,namestatus,videostatus,dateline FROM "
							+ JavaCenterHome.getTableName("space") + " WHERE "
							+ Common.implode(whereArr, " AND ") + " ORDER BY uid DESC LIMIT 0,"
							+ sConfig.get("newspacenum"));
			Common.setData("newspacelist", newSpaceList, false);
		}
		cpService.updateStat(sGlobal, sConfig, "register", false);
		sGlobal.put("supe_uid", _uid);
		sGlobal.put("supe_username", _userName);
		return space;
	}

	
	public void insertSession(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> sConfig, int uid, String userName,
			String password) {
		int onlinehold = (Integer) sConfig.get("onlinehold");
		if (onlinehold < 300) {
			onlinehold = 300;
			sConfig.put("onlinehold", onlinehold);
		}
		int timestamp = (Integer) sGlobal.get("timestamp");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("session") + " WHERE uid="
				+ uid + " OR lastactivity<" + (timestamp - onlinehold));
		String ip = Common.getOnlineIP(request, true);
		byte magichidden = 0;
		Map<String, String> magic = (Map<String, String>) request.getAttribute("globalMagic");
		if (magic != null && magic.get("invisible") != null) {
			List<String> expires = dataBaseService.executeQuery("SELECT expire FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid=" + uid
					+ " AND mid='invisible'", 1);
			if (expires.size() > 0) {
				if (Integer.parseInt(expires.get(0)) > timestamp) {
					magichidden = 1;
				}
			}
		}
		dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("session")
				+ " (uid,username,password,lastactivity,ip,magichidden) VALUES (" + uid + ",'" + userName
				+ "','" + password + "'," + timestamp + ",'" + ip + "'," + magichidden + ")");

		sGlobal.put("supe_uid", uid);
		List<String> spaceFields = new ArrayList<String>();
		spaceFields.add("lastlogin = " + timestamp);
		spaceFields.add("ip = '" + ip + "'");
		Map<String, Integer> reward = Common.getReward("daylogin", false, uid, "", true, request, response);
		int experience = reward.get("credit");
		int credit = reward.get("experience");
		if (credit > 0) {
			spaceFields.add("credit=credit+" + credit);
		}
		if (experience > 0) {
			spaceFields.add("experience=experience+" + experience);
		}
		dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
				+ Common.implode(spaceFields, ",") + " WHERE uid=" + uid);
		int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
				+ JavaCenterHome.getTableName("spacelog") + " WHERE uid=" + uid + " AND expiration <="
				+ timestamp);
		if (count > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET groupid=0 WHERE uid=" + uid);
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("spacelog") + " WHERE uid="
					+ uid);
		}
		CpService cpService = (CpService) BeanFactory.getBean("cpService");
		cpService.updateStat(sGlobal, sConfig, "login", true);
	}

	
	public Map getTask(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<Object, Map> globalTask = Common.getCacheDate(request, response, "/data/cache/cache_task.jsp",
				"globalTask");
		List<Map> tasks = new ArrayList<Map>();
		if (!globalTask.isEmpty()) {
			List<Map<String, Object>> userTaskList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("usertask") + " WHERE uid='" + sGlobal.get("supe_uid")
					+ "'");
			Map<Object, Map<String, Object>> userTasks = new HashMap<Object, Map<String, Object>>();
			for (Map<String, Object> value : userTaskList) {
				userTasks.put(value.get("taskid"), value);
			}
			Set keys = globalTask.keySet();
			Map value = null;
			int timestamp = (Integer) sGlobal.get("timestamp");
			for (Object key : keys) {
				boolean allowNext = false;
				value = globalTask.get(key);
				String nextType = value.get("nexttype").toString();
				int lastTime = userTasks.get(value.get("taskid")) == null ? 0 : (Integer) userTasks.get(
						value.get("taskid")).get("dateline");
				if (Common.empty(lastTime)) {
					allowNext = true;
				} else if ("day".equals(nextType)) {
					if (!Common.sgmdate(request, "yyyyMMdd", timestamp).equals(
							Common.sgmdate(request, "yyyyMMdd", lastTime))) {
						allowNext = true;
					}
				} else if ("hour".equals(nextType)) {
					if (!Common.sgmdate(request, "yyyyMMddHH", timestamp).equals(
							Common.sgmdate(request, "yyyyMMddHH", lastTime))) {
						allowNext = true;
					}
				} else if ((Integer) value.get("nexttime") > 0) {
					if (timestamp - lastTime >= (Integer) value.get("nexttime")) {
						allowNext = true;
					}
				}
				if ((Integer) value.get("starttime") <= timestamp && allowNext) {
					value.put("image", Common.empty(value.get("image")) ? "image/task.gif" : value
							.get("image"));
					tasks.add(value);
				}
			}
		}

		if (!tasks.isEmpty()) {
			int max = tasks.size();
			int randIndex = max <= 1 ? 0 : Common.rand(0, max - 1);
			return tasks.get(randIndex);
		} else {
			return new HashMap();
		}
	}
}