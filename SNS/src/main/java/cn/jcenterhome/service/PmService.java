
package cn.jcenterhome.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;


public class PmService {
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
	private final int PMLIMIT1DAY_ERROR = -1;
	private final int PMFLOODCTRL_ERROR = -2;
	private final int PMMSGTONOTFRIEND = -3;
	private final int PMSENDREGDAYS = -4;
	private Map<String, Object> user = null;

	
	public List<Map<String, Object>> getPmByToUid(int uid, int toUid, int startTime, int endTime) {
		List<Map<String, Object>> arr1 = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("pms") + " WHERE msgfromid='" + uid + "' AND msgtoid='" + toUid
				+ "' AND dateline>='" + startTime + "' AND dateline<'" + endTime
				+ "' AND related>'0' AND delstatus IN (0,2) ORDER BY dateline");
		List<Map<String, Object>> arr2 = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("pms") + " WHERE msgfromid='" + toUid + "' AND msgtoid='" + uid
				+ "' AND dateline>='" + startTime + "' AND dateline<'" + endTime
				+ "' AND related>'0' AND delstatus IN (0,1) ORDER BY dateline");
		Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
		set.addAll(arr1);
		set.addAll(arr2);
		List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>(set);
		Collections.sort(arr, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> c1, Map<String, Object> c2) {
				if (c1.get("dateline") == c2.get("dateline")) {
					return 0;
				}
				return ((Integer) c1.get("dateline") < (Integer) c2.get("dateline")) ? -1 : 1;
			}
		});
		return arr;
	}

	
	public void setPmStatus(int uid, int toUid, int pmid, int status) {
		int oldStatus = 0;
		int newStatus = 0;
		if (status == 0) {
			oldStatus = 1;
			newStatus = 0;
		} else {
			oldStatus = 0;
			newStatus = 1;
		}
		if (toUid > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("pms") + " SET new='"
					+ newStatus + "' WHERE msgfromid IN (" + toUid + ") AND msgtoid='" + uid + "' AND new='"
					+ oldStatus + "'");
		}
		if (pmid > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("pms") + " SET new='"
					+ newStatus + "' WHERE pmid IN (" + pmid + ") AND msgtoid='" + uid + "' AND new='"
					+ oldStatus + "'");
		}
	}

	
	public List<Map<String, Object>> getPmByPmid(int uid, int pmid) {
		List<Map<String, Object>> arr = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("pms") + " WHERE pmid='" + pmid + "' AND (msgtoid IN ('" + uid
				+ "','0') OR msgfromid='" + uid + "')");
		return arr;
	}

	
	public Map getBlackls(int uid, Object uids) {
		Map blackLs = null;
		if (Common.empty(uids)) {
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT blacklist FROM "
					+ JavaCenterHome.getTableName("member") + " WHERE uid='" + uid + "'");
			if (list.size() > 0) {
				blackLs = list.get(0);
			}
		} else {
			blackLs = new HashMap();
			List<Map<String, Object>> list = dataBaseService
					.executeQuery("SELECT uid, blacklist FROM " + JavaCenterHome.getTableName("member")
							+ " WHERE uid IN (" + Common.sImplode(uids) + ")");
			for (Map<String, Object> data : list) {
				blackLs.put(data.get("uid"), ((String) data.get("blacklist")).split(","));
			}
		}
		return blackLs;
	}

	
	public List<Map<String, Object>> getPmList(int uid, int pmNum, String folder, String filter, int start,
			int ppp) {
		ppp = ppp > 0 ? ppp : 10;
		String sql = null;
		if ("newbox".equals(folder)) {
			folder = "inbox";
			filter = "newpm";
		}
		if ("inbox".equals(folder) || "outbox".equals(folder)) {
			String filterAdd = null;
			if (filter.equals("newpm")) {
				filterAdd = "pm.msgtoid='"
						+ uid
						+ "' AND (pm.related='0' AND pm.msgfromid>'0' OR pm.msgfromid='0') AND pm.folder='inbox' AND pm.new='1'";
			} else if (filter.equals("systempm")) {
				filterAdd = "pm.msgtoid='" + uid + "' AND pm.msgfromid='0' AND pm.folder='inbox'";
			} else if (filter.equals("privatepm")) {
				filterAdd = "pm.msgtoid='" + uid
						+ "' AND pm.related='0' AND pm.msgfromid>'0' AND pm.folder='inbox'";
			} else if (filter.equals("announcepm")) {
				filterAdd = "pm.msgtoid='0' AND pm.folder='inbox'";
			} else {
				filterAdd = "pm.msgtoid='" + uid + "' AND pm.related='0' AND pm.folder='inbox'";
			}
			sql = "SELECT m.username as msgfrom,pm.* FROM " + JavaCenterHome.getTableName("pms")
					+ " pm LEFT JOIN " + JavaCenterHome.getTableName("space")
					+ " m ON pm.msgfromid = m.uid WHERE " + filterAdd + " ORDER BY pm.dateline DESC LIMIT "
					+ start + ", " + ppp;
		} else if ("searchbox".equals(folder)) {
			String filterAdd = "msgtoid='" + uid + "' AND folder='inbox' AND message LIKE '%"
					+ (Common.addCSlashes(filter, new char[] {'%', '_'}).replace("_", "\\_")) + "%'";
			sql = "SELECT * FROM " + JavaCenterHome.getTableName("pms") + " WHERE " + filterAdd
					+ " ORDER BY dateline DESC LIMIT " + start + ", " + ppp;
		}
		List<Map<String, Object>> list = null;
		if (sql != null) {
			int time = Common.time();
			int today = time - time % 86400;
			list = dataBaseService.executeQuery(sql);
			int dateRange = 0;
			for (Map<String, Object> data : list) {
				dateRange = 5;
				if ((Integer) data.get("dateline") >= today) {
					dateRange = 1;
				} else if ((Integer) data.get("dateline") >= today - 86400) {
					dateRange = 2;
				} else if ((Integer) data.get("dateline") >= today - 172800) {
					dateRange = 3;
				} else if ((Integer) data.get("dateline") >= today - 604800) {
					dateRange = 4;
				}
				data.put("daterange", dateRange);
				data.put("subject", Common.htmlSpecialChars((String) data.get("subject")));
				if ("announcepm".equals(filter)) {
					data.remove("msgfromid");
					data.remove("msgfrom");
				}
				data.put("touid",
						uid == (data.get("msgfromid") == null ? 0 : (Integer) data.get("msgfromid")) ? data
								.get("msgtoid") : data.get("msgfromid"));
			}
		}
		if (folder == "inbox") {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("newpm")
					+ " WHERE uid='" + uid + "'");
		}
		return list == null ? new ArrayList<Map<String, Object>>() : list;
	}

	
	public String removeCode(String str, int length) {
		return Common.trim(Common.cutstr(str.replaceAll(
				"\\[(email|code|quote|img)=?.*\\].*?\\[\\/(email|code|quote|img)\\](?is)", "").replaceAll(
				"(?is)\\[/?(b|i|url|u|color|size|font|align|list|indent|float)=?[^]]*\\]", "").replaceAll(
				"\r\n", ""), length));
	}

	
	public int getNum(int uid, String folder, String filter) {
		int num = 0;
		String sql = null;
		if ("newbox".equals(folder)) {
			sql = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pms") + " WHERE msgtoid='" + uid
					+ "' AND (related='0' AND msgfromid>'0' OR msgfromid='0') AND folder='inbox' AND new='1'";
		} else if ("outbox".equals(folder) || "inbox".equals(folder)) {
			String filterAdd = null;
			if (filter.equals("newpm")) {
				filterAdd = "msgtoid='"
						+ uid
						+ "' AND (related='0' AND msgfromid>'0' OR msgfromid='0') AND folder='inbox' AND new='1'";
			} else if (filter.equals("systempm")) {
				filterAdd = "msgtoid='" + uid + "' AND msgfromid='0' AND folder='inbox'";
			} else if (filter.equals("privatepm")) {
				filterAdd = "msgtoid='" + uid + "' AND related='0' AND msgfromid>'0' AND folder='inbox'";
			} else if (filter.equals("announcepm")) {
				filterAdd = "msgtoid='0' AND folder='inbox'";
			} else {
				filterAdd = "msgtoid='" + uid + "' AND related='0' AND folder='inbox'";
			}
			sql = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pms") + " WHERE " + filterAdd;
		}
		if (sql != null) {
			num = dataBaseService.findRows(sql);
		}
		return num;
	}

	public int getPageStart(int page, int ppp, int totalnum) {
		double totalpage = Math.ceil((double) totalnum / (double) ppp);
		page = (int) Math.max(1, Math.min(totalpage, page));
		return (page - 1) * ppp;
	}

	
	public int jcSendPm(HttpServletRequest request, HttpServletResponse response, int fromUid, String msgto,
			String subject, String message, int replyPmId, boolean isUserName, boolean isAddrbook) throws Exception {
		Map<String, Object> user = null;
		this.user = new HashMap<String, Object>();
		if (fromUid > 0) {
			List<Map<String, Object>> userList = dataBaseService
					.executeQuery("SELECT uid,username,dateline FROM " + JavaCenterHome.getTableName("space")
							+ " WHERE uid='" + fromUid + "'");
			if (userList.size() == 0) {
				return 0;
			}
			user = userList.get(0);
			this.user.put("uid", user.get("uid"));
			this.user.put("username", Common.addSlashes((String) user.get("username")));
		} else {
			user = new HashMap<String, Object>();
			user.put("dateline", 0);
			this.user.put("uid", 0);
			this.user.put("username", "");
		}
		if (replyPmId > 0) {
			isUserName = true;
			List<Map<String, Object>> pms = getPmByPmid((Integer) this.user.get("uid"), replyPmId);
			if (pms.size() > 0) {
				if (pms.get(0).get("msgfromid") == this.user.get("uid")) {
					List<Map<String, Object>> userList = dataBaseService
							.executeQuery("SELECT uid,username,dateline FROM "
									+ JavaCenterHome.getTableName("space") + " WHERE uid='"
									+ pms.get(0).get("msgtoid") + "'");
					if (userList.size() > 0) {
						user = userList.get(0);
					}
					msgto = (String) user.get("username");
				} else {
					msgto = (String) pms.get(0).get("msgfrom");
				}
			}
		}
		Object[] msgtoArr = Common.uniqueArray(msgto.split(","));
		if (isUserName) {
			msgtoArr = name2id(msgtoArr).toArray();
		}
		Map blackLs = new HashMap();
		if (Common.empty(msgtoArr)) {
			List<Map<String, Object>> blackList = dataBaseService.executeQuery("SELECT blacklist FROM "
					+ JavaCenterHome.getTableName("member") + " WHERE uid='" + this.user.get("uid") + "'");
			if (blackList.size() > 0) {
				blackLs = blackList.get(0);
				blackLs.put("blacklist", blackLs.get("blacklist").toString().split(","));
			}
		} else {
			List<Map<String, Object>> blackList = dataBaseService.executeQuery("SELECT uid, blacklist FROM "
					+ JavaCenterHome.getTableName("member") + " WHERE uid IN (" + Common.sImplode(msgtoArr)
					+ ")");
			for (Map<String, Object> data : blackList) {
				blackLs.put(data.get("uid"), data.get("blacklist").toString().split(","));
			}
		}
		if (fromUid > 0) {
			Map sConfig = (Map) request.getAttribute("sConfig");
			if (!Common.empty(sConfig.get("pmsendregdays"))) {
				if ((Integer) user.get("dateline") > Common.time()
						- ((Integer) sConfig.get("pmsendregdays") * 86400)) {
					return PMSENDREGDAYS;
				}
			}
			if (!isAddrbook && msgtoArr.length > 1 && !isFriend(fromUid, msgtoArr)) {
				return PMMSGTONOTFRIEND;
			}
			boolean pmlimit1day = false;
			if (!Common.empty(sConfig.get("pmlimit1day"))) {
				pmlimit1day = getPmCountByFromUid((Integer) this.user.get("uid"), 86400) > (Integer) sConfig
						.get("pmlimit1day");
			}
			if (pmlimit1day
					|| (!Common.empty(sConfig.get("pmfloodctrl")) && getPmCountByFromUid((Integer) this.user
							.get("uid"), (Integer) sConfig.get("pmfloodctrl")) > 0)) {
				if (!isFriend(fromUid, msgtoArr)) {
					if (!isReplyPm(fromUid, msgtoArr)) {
						if (pmlimit1day) {
							return PMLIMIT1DAY_ERROR;
						} else {
							return PMFLOODCTRL_ERROR;
						}
					}
				}
			}
		}
		int lastPmId = 0;
		for (Object uid : msgtoArr) {
			uid = Common.intval(uid.toString());
			if (fromUid == 0 || !Common.in_array((String[]) blackLs.get(uid), "{ALL}")) {
				blackLs.put(uid, name2id((String[]) blackLs.get(uid)));
				if (fromUid == 0 || blackLs.get(uid) != null
						&& !((List) blackLs.get(uid)).contains(this.user.get("uid"))) {
					lastPmId = sendPm(request, response, subject, message, this.user, (Integer) uid,
							replyPmId);
				}
			}
		}
		return lastPmId;
	}

	private int getPmCountByFromUid(int uid, int timeOffSet) {
		int dateline = Common.time() - timeOffSet;
		return dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pms")
				+ " WHERE msgfromid='" + uid + "' AND dateline>'" + dateline + "'");
	}

	private boolean isFriend(int uid, Object[] friendIds) {
		if (dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("friend")
				+ " WHERE uid=" + uid + " AND fuid IN (" + Common.sImplode(friendIds) + ") AND status='1'") == friendIds.length) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isReplyPm(int uid, Object[] touIds) {
		List<Map<String, Object>> pmList = dataBaseService.executeQuery("SELECT msgfromid, msgtoid FROM "
				+ JavaCenterHome.getTableName("pms") + " WHERE msgfromid IN ('"
				+ Common.implode(touIds, "', '") + "') AND msgtoid='" + uid + "' AND related=1");
		Map pmReply = new HashMap();
		for (Map<String, Object> data : pmList) {
			pmReply.put(data.get("msgfromid"), data);
		}
		for (Object val : touIds) {
			if (pmReply.get(val) == null) {
				return false;
			}
		}
		return true;
	}

	private List<Integer> name2id(Object[] userNamesArr) {
		for (int i = 0; i < userNamesArr.length; i++) {
			userNamesArr[i] = Common.addSlashes(Common.stripSlashes(userNamesArr[i].toString()));
		}
		List<Map<String, Object>> uids = dataBaseService.executeQuery("SELECT uid FROM "
				+ JavaCenterHome.getTableName("space") + " WHERE username IN("
				+ Common.sImplode(userNamesArr) + ")");
		List<Integer> arr = new ArrayList<Integer>(uids.size());
		for (Map<String, Object> value : uids) {
			arr.add((Integer) value.get("uid"));
		}
		return arr;
	}

	private int sendPm(HttpServletRequest request, HttpServletResponse response, String subject,
			String message, Map<String, Object> msgFrom, int msgTo, int related) throws Exception {
		if (!Common.empty(msgFrom.get("uid")) && (Integer) msgFrom.get("uid") == msgTo) {
			return 0;
		}
		message = Common.getStr(message, -1, false, false, true, 0, 0, request, response);
		subject = Common.getStr(subject, -1, false, false, true, 0, 0, request, response);
		String box = "inbox";
		if ("".equals(subject) && related == 0) {
			subject = removeCode(message, 75);
		} else {
			subject = Common.cutstr(subject, 75, "");
		}
		int lastPmId = 0;
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int timeStamp = (Integer) sGlobal.get("timestamp");
		if (!Common.empty(msgFrom.get("uid"))) {
			int sessionExist = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("pms") + " WHERE msgfromid='" + msgFrom.get("uid")
					+ "' AND msgtoid='" + msgTo + "' AND folder='inbox' AND related='0'");
			if (sessionExist == 0 || sessionExist > 1) {
				if (sessionExist > 1) {
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("pms")
							+ " WHERE msgfromid='" + msgFrom.get("uid") + "' AND msgtoid='" + msgTo
							+ "' AND folder='inbox' AND related='0'");
				}
				lastPmId = dataBaseService
						.insert("INSERT INTO "
								+ JavaCenterHome.getTableName("pms")
								+ " (msgfrom,msgfromid,msgtoid,folder,new,subject,dateline,related,message,fromappid) VALUES ('"
								+ msgFrom.get("username") + "','" + msgFrom.get("uid") + "','" + msgTo
								+ "','" + box + "','1','" + subject + "','" + timeStamp + "','0','" + message
								+ "','" + JavaCenterHome.jchConfig.get("JC_APPID") + "')");
			} else {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("pms")
						+ " SET subject='" + subject + "', message='" + message + "', dateline='" + timeStamp
						+ "', new='1', fromappid='" + JavaCenterHome.jchConfig.get("JC_APPID")
						+ "' WHERE msgfromid='" + msgFrom.get("uid") + "' AND msgtoid='" + msgTo
						+ "' AND folder='inbox' AND related='0'");
			}
			sessionExist = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("pms") + " WHERE msgfromid='" + msgTo + "' AND msgtoid='"
					+ msgFrom.get("uid") + "' AND folder='inbox' AND related='0'");
			if (sessionExist == 0) {
				dataBaseService
						.executeUpdate("INSERT INTO "
								+ JavaCenterHome.getTableName("pms")
								+ " (msgfrom,msgfromid,msgtoid,folder,new,subject,dateline,related,message,fromappid) VALUES ('"
								+ msgFrom.get("username") + "','" + msgTo + "','" + msgFrom.get("uid")
								+ "','" + box + "','0','" + subject + "','" + timeStamp + "','0','" + message
								+ "','0')");
			}
			lastPmId = dataBaseService
					.insert("INSERT INTO "
							+ JavaCenterHome.getTableName("pms")
							+ " (msgfrom,msgfromid,msgtoid,folder,new,subject,dateline,related,message,fromappid) VALUES ('"
							+ msgFrom.get("username") + "','" + msgFrom.get("uid") + "','" + msgTo + "','"
							+ box + "','1','" + subject + "','" + timeStamp + "','1','" + message + "','"
							+ JavaCenterHome.jchConfig.get("JC_APPID") + "')");
		} else {
			lastPmId = dataBaseService
					.insert("INSERT INTO "
							+ JavaCenterHome.getTableName("pms")
							+ " (msgfrom,msgfromid,msgtoid,folder,new,subject,dateline,related,message,fromappid) VALUES ('"
							+ msgFrom.get("username") + "','" + msgFrom.get("uid") + "','" + msgTo + "','"
							+ box + "','1','" + subject + "','" + timeStamp + "','0','" + message + "','"
							+ JavaCenterHome.jchConfig.get("JC_APPID") + "')");
		}
		dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("newpm")
				+ " (uid) VALUES ('" + msgTo + "')");
		return lastPmId;
	}
}
