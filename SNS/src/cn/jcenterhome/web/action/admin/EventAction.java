package cn.jcenterhome.web.action.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.web.action.BaseAction;


public class EventAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		boolean allowmanage = Common.checkPerm(request, response, "manageevent");
		if (!allowmanage) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		try {
			if (submitCheck(request, "opsubmit")) {
				String[] ids = request.getParameterValues("ids");
				String opType = request.getParameter("optype");
				if ("delete".equals(opType)) {
					if (ids != null && adminDeleteService.deleteEvents(request, response, sGlobal, ids)) {
						return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
					} else {
						return cpMessage(request, mapping, "cp_choose_to_delete_the_columns_event");
					}
				}
				Map<String, Integer> grademap = new HashMap<String, Integer>();
				grademap.put("verify", 1);
				grademap.put("delayverify", -1);
				grademap.put("close", -2);
				grademap.put("open", 1);
				grademap.put("recommend", 2);
				grademap.put("unrecommend", 1);
				if (grademap.containsKey(opType)) {
					int grade = grademap.get(opType);
					if (ids != null && verifyEvents(request, response, sGlobal, grade, ids)) {
						return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
					} else {
						return cpMessage(request, mapping, "cp_choose_to_grade_the_columns_event");
					}
				} else {
					return cpMessage(request, mapping, "cp_choice_batch_action");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		if (Common.empty(request.getParameter("op"))) {
			Common.getCacheDate(request, response, "/data/cache/cache_eventclass.jsp", "globalEventClass");
			StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=event");

			String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
			Map<String, String[]> paramMap = request.getParameterMap();
			String[] intkeys = new String[] { "eventid", "uid", "public", "grade", "classid" };
			String[] strkeys = new String[] { "province", "city" };
			List<String[]> randkeys = new ArrayList<String[]>();
			randkeys.add(new String[] { "intval", "hot" });
			String[] likekeys = new String[] { "title" };
			Map<String, String> wheres = getWheres(intkeys, strkeys, randkeys, likekeys, "", paramMap,
					timeoffset);
			StringBuffer tempSQL = new StringBuffer();
			String starttime = request.getParameter("starttime");
			if (!Common.empty(starttime)) {
				tempSQL.append(" AND starttime >= "
						+ Common.strToTime(starttime, timeoffset, "yyyy-MM-dd HH:mm"));
				mpurl.append("&starttime=" + starttime);
			}
			String endtime = request.getParameter("endtime");
			if (!Common.empty(endtime)) {
				tempSQL.append(" AND starttime <= "
						+ Common.strToTime(endtime, timeoffset, "yyyy-MM-dd HH:mm"));
				mpurl.append("&endtime=" + endtime);
			}
			String over = request.getParameter("over");
			if ("1".equals(over)) {
				tempSQL.append(" AND endtime < " + sGlobal.get("timestamp"));
				mpurl.append("&over=1");
			} else if ("0".equals(over)) {
				tempSQL.append(" AND endtime >= " + sGlobal.get("timestamp"));
				mpurl.append("&over=0");
			}
			String whereSQL = wheres.get("sql");
			if (whereSQL != null) {
				whereSQL += tempSQL;
			} else if (tempSQL.length() > 0) {
				whereSQL = tempSQL.substring(5);
			} else {
				whereSQL = "1";
			}
			mpurl.append(wheres.get("url"));
			request.setAttribute("active_" + request.getParameter("grade"), " class='active'");
			String activeGrade = request.getParameter("grade");
			int grade = Common.intval(activeGrade);
			if (activeGrade != null && grade == 0) {
				activeGrade = "grade0";
			} else if (grade == -1) {
				activeGrade = "grade_1";
			} else if (grade == 1) {
				activeGrade = "grade1";
			} else if (grade == -2) {
				activeGrade = "grade_2";
			} else if (grade == 2) {
				activeGrade = "grade2";
			} else {
				activeGrade = "all";
			}
			request.setAttribute("active_" + activeGrade, " class=\"active\"");
			Map<String, String> orders = getOrders(
					new String[] { "dateline", "starttime", "membernum", "hot" }, "eventid", null, paramMap);
			String ordersql = orders.get("sql");
			mpurl.append(orders.get("url"));
			request.setAttribute("orderby_" + request.getParameter("orderby"), " selected");
			request.setAttribute("ordersc_" + request.getParameter("ordersc"), " selected");
			int perpage = Common.intval(request.getParameter("perpage"));
			if (!Common.in_array(new Integer[] { 20, 50, 100, 1000 }, perpage)) {
				perpage = 20;
			}
			mpurl.append("&perpage=" + perpage);
			request.setAttribute("perpage_" + perpage, " selected");
			int page = Math.max(Common.intval(request.getParameter("page")), 1);
			int start = (page - 1) * perpage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perpage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			boolean managebatch = Common.checkPerm(request, response, "managebatch");
			boolean allowbatch = true;
			int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("event") + " WHERE " + whereSQL);
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("event") + " WHERE " + whereSQL + " " + ordersql
						+ " LIMIT " + start + "," + perpage);
				SimpleDateFormat eventSDF = Common.getSimpleDateFormat("MM-dd", timeoffset);
				for (Map<String, Object> value : list) {
					if (!managebatch && (Integer) value.get("uid") != supe_uid) {
						allowbatch = false;
					}
					value.put("starttime", Common.gmdate(eventSDF, (Integer) value.get("starttime")));
					value.put("endtime", Common.gmdate(eventSDF, (Integer) value.get("endtime")));
				}
				request.setAttribute("multi", Common.multi(request, count, perpage, page, maxPage, mpurl
						.toString(), null, null));
				request.setAttribute("list", list);
				if(list.size()%perpage==1){
					mpurl.append("&page="+(page-1));
				}else{
					mpurl.append("&page="+page);
				}
			}
			request.setAttribute("FORMHASH", formHash(request));
			request.setAttribute("count", count);
			request.setAttribute("mpurl", mpurl);
			request.setAttribute("allowbatch", allowbatch);
			request.setAttribute("perpage", perpage);
		}
		return mapping.findForward("event");
	}

	
	private boolean verifyEvents(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, int grade, String[] eventIds) throws Exception {
		boolean allowmanage = Common.checkPerm(request, response, "manageevent");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int opnum = 0;
		List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("event") + " WHERE eventid IN (" + Common.sImplode(eventIds)
				+ ")");
		for (int i = 0; i < eventList.size(); i++) {
			if (allowmanage && !managebatch) {
				opnum++;
			}
		}
		if (!allowmanage || (!managebatch && opnum > 1)) {
			return false;
		}
		if (!Common.in_array(new Integer[] { -2, -1, 1, 2 }, grade)) {
			throw new Exception("bad_event_grade");
		}
		List<Integer> newIds = new ArrayList<Integer>();
		Map<Integer, Map<String, Object>> events = new HashMap<Integer, Map<String, Object>>();
		Map<Integer, String> actions = new HashMap<Integer, String>();
		for (Map<String, Object> event : eventList) {
			int eventGrade = (Integer) event.get("grade");
			if (grade == eventGrade) {
				continue;
			}
			int eventId = (Integer) event.get("eventid");
			newIds.add(eventId);
			events.put(eventId, event);
			switch (grade) {
			case -2:
				actions.put(eventId, "close");
				break;
			case -1:
				actions.put(eventId, "unverify");
				break;
			case 1:
				if (eventGrade == -2) {
					actions.put(eventId, "open");
				} else if (eventGrade < 1) {
					actions.put(eventId, "verify");
				} else if (eventGrade == 2) {
					actions.put(eventId, "unrecommend");
				}
				break;
			case 2:
				actions.put(eventId, "recommend");
				break;
			}
		}

		if (newIds.isEmpty()) {
			return false;
		}
		Map<Integer, Map<String, Object>> globalEventClass = Common.getCacheDate(request, response,
				"/data/cache/cache_eventclass.jsp", "globalEventClass");
		List<Integer> noteids = new ArrayList<Integer>();
		List<String> note_inserts = new ArrayList<String>();
		List<String> feed_inserts = new ArrayList<String>();
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int timestamp = (Integer) sGlobal.get("timestamp");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		for (Integer id : newIds) {
			Map<String, Object> event = events.get(id);
			int eventId = (Integer) event.get("eventid");
			int uid = (Integer) event.get("uid");
			int eventGrade = (Integer) event.get("grade");
			String title = (String) event.get("title");
			if (grade >= 1 && eventGrade < 1 && eventGrade >= -1) {
				String poster = null;
				if (Common.empty(event.get("poster"))) {
					poster = (String) (globalEventClass.get(event.get("classid"))).get("poster");
				} else {
					poster = Common.pic_get(sConfig, (String) event.get("poster"), (Integer) event
							.get("thumb"), (Integer) event.get("remote"), true);
				}
				Map<String, Object> title_data = new HashMap<String, Object>();
				title_data.put("eventid", id);
				title_data.put("title", title);

				Map<String, Object> body_data = new HashMap<String, Object>();
				body_data.put("eventid", id);
				body_data.put("title", event.get("title"));
				body_data.put("username", event.get("username"));
				body_data.put("starttime", Common.sgmdate(request, "MM-dd HH:mm", (Integer) event
						.get("starttime")));
				body_data.put("endtime", Common.sgmdate(request, "MM-dd HH:mm", (Integer) event
						.get("endtime")));
				body_data.put("province", event.get("province"));
				body_data.put("city", event.get("city"));
				body_data.put("location", event.get("location"));

				Map<String, Object> feedarr = new HashMap<String, Object>();
				feedarr.put("appid", Common.intval(jchConfig.get("JC_APPID")));
				feedarr.put("icon", "event");
				feedarr.put("username", event.get("username"));
				feedarr.put("title_template", Common.getMessage(request, "cp_event_add"));
				feedarr.put("title_data", title_data);
				feedarr.put("body_template", Common.getMessage(request, "cp_event_feed_info"));
				feedarr.put("body_data", body_data);
				feedarr.put("image_1", poster);

				feedarr = (Map<String, Object>) Common.sStripSlashes(feedarr);
				feedarr.put("title_data", Serializer.serialize(Common
						.sStripSlashes(feedarr.get("title_data"))));
				feedarr
						.put("body_data", Serializer
								.serialize(Common.sStripSlashes(feedarr.get("body_data"))));
				feedarr.put("hash_template", Common.md5(feedarr.get("title_template") + "\t"
						+ feedarr.get("body_template")));
				feedarr.put("hash_data", Common.md5(feedarr.get("title_template") + "\t"
						+ feedarr.get("title_data") + "\t" + feedarr.get("body_template") + "\t"
						+ feedarr.get("body_data")));
				feedarr = (Map<String, Object>) Common.sAddSlashes(feedarr);
				feed_inserts.add("('" + feedarr.get("appid") + "', 'event', " + uid + ", '"
						+ feedarr.get("username") + "', " + timestamp + ", 0, '"
						+ feedarr.get("hash_template") + "', '" + feedarr.get("hash_data") + "', '"
						+ feedarr.get("title_template") + "', '" + feedarr.get("title_data") + "', '"
						+ feedarr.get("body_template") + "', '" + feedarr.get("body_data") + "', '', '"
						+ feedarr.get("image_1") + "', 'space.jsp?do=event&id=" + id
						+ "', '', '', '', '', '', '', '', " + id + ", 'eventid')");
			}
			if (uid != supe_uid) {
				noteids.add(uid);
				String note_msg = Common.getMessage(request, "cp_event_set_" + actions.get(id),
						"space.jsp?do=event&id=" + eventId, title);
				note_inserts.add("('" + event.get("uid") + "', 'system', '1', '0', '', '"
						+ Common.addSlashes(note_msg) + "', '" + timestamp + "')");
			}
		}
		String idStr = Common.sImplode(newIds);
		if (grade == 2) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event") + " SET grade="
					+ grade + ", recommendtime=" + timestamp + " WHERE eventid IN (" + idStr + ")");
		} else {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event") + " SET grade="
					+ grade + " WHERE eventid IN (" + idStr + ")");
		}
		if (note_inserts.size() > 0) {
			dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("notification")
					+ " (uid, type, new, authorid, author,note, dateline) VALUES "
					+ Common.implode(note_inserts, ","));
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET notenum=notenum+1 WHERE uid IN (" + Common.sImplode(noteids) + ")");
		}
		if (feed_inserts.size() > 0) {
			String sql = "INSERT INTO "
					+ JavaCenterHome.getTableName("feed")
					+ " (appid ,icon ,uid ,username ,dateline ,friend ,hash_template ,hash_data ,title_template ,title_data ,body_template ,body_data ,body_general ,image_1 ,image_1_link ,image_2 ,image_2_link ,image_3 ,image_3_link ,image_4 ,image_4_link ,target_ids ,id ,idtype) VALUES "
					+ Common.implode(feed_inserts, ",");
			dataBaseService.executeUpdate(sql);
		}
		return true;
	}
}