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
import cn.jcenterhome.web.action.BaseAction;


public class FeedAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		boolean allowManage = Common.checkPerm(request, response, "managefeed");
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!allowManage) {
			paramMap.put("uid", new String[] { String.valueOf(supe_uid) });
			paramMap.put("username", null);
		}

		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		try {
			if (submitCheck(request, "feedsubmit")) {
				if (!allowManage) {
					return cpMessage(request, mapping, "cp_no_authority_management_operation");
				}
				int feedId = Common.intval(request.getParameter("feedid"));
				int feedUid = Common.intval(request.getParameter("feeduid"));
				Map<String, Object> setMap = new HashMap<String, Object>();
				String titleTemplate = null;
				String bodyTemplate = null;
				if (feedUid == 0 || feedId == 0) {
					titleTemplate = Common.trim(request.getParameter("title_template"));
					bodyTemplate = Common.trim(request.getParameter("body_template"));
					if (Common.empty(titleTemplate) && Common.empty(bodyTemplate)) {
						return cpMessage(request, mapping, "cp_sitefeed_error");
					}
					setMap.put("title_template", titleTemplate);
					setMap.put("body_template", bodyTemplate);
				}
				String dateline = Common.trim(request.getParameter("dateline"));
				int newTimesTamp = 0;
				if (!Common.empty(dateline)) {
					newTimesTamp = Common.strToTime(dateline, timeoffset, "yyyy-MM-dd HH:mm");
					if (newTimesTamp > (Integer) sGlobal.get("timestamp")) {
						sGlobal.put("timestamp", newTimesTamp);
					}
				}
				String image_1 = Common.trim(request.getParameter("image_1"));
				String image_2 = Common.trim(request.getParameter("image_2"));
				String image_3 = Common.trim(request.getParameter("image_3"));
				String image_4 = Common.trim(request.getParameter("image_4"));
				String image_1_link = Common.trim(request.getParameter("image_1_link"));
				String image_2_link = Common.trim(request.getParameter("image_2_link"));
				String image_3_link = Common.trim(request.getParameter("image_3_link"));
				String image_4_link = Common.trim(request.getParameter("image_4_link"));
				String body_general = Common.trim(request.getParameter("body_general"));
				if (feedId == 0) {
					sGlobal.put("supe_uid", 0);
					String[] images = new String[] { image_1, image_2, image_3, image_4 };
					String[] image_links = new String[] { image_1_link, image_2_link, image_3_link, image_4_link };
					feedId = cpService.addFeed(sGlobal, "sitefeed", titleTemplate, null, bodyTemplate, null,
							body_general, images, image_links, "", 0, 0, 0, "", true);
				} else {
					if (feedUid == 0) {
						setMap.put("body_general", body_general);
					}
					int hot = Common.intval(request.getParameter("hot"));
					setMap.put("image_1", image_1);
					setMap.put("image_2", image_2);
					setMap.put("image_3", image_3);
					setMap.put("image_4", image_4);
					setMap.put("image_1_link", image_1_link);
					setMap.put("image_2_link", image_2_link);
					setMap.put("image_3_link", image_3_link);
					setMap.put("image_4_link", image_4_link);
					setMap.put("dateline", newTimesTamp);
					setMap.put("hot", hot);

					Set<String> keys = setMap.keySet();
					StringBuffer updateStr = new StringBuffer();
					for (String key : keys) {
						updateStr.append(key + "='" + setMap.get(key) + "',");
					}
					String sql = "UPDATE " + JavaCenterHome.getTableName("feed") + " SET "
							+ updateStr.substring(0, updateStr.length() - 1) + " WHERE feedid=" + feedId;
					dataBaseService.executeUpdate(sql);
					int id = Common.intval(request.getParameter("id"));
					String idType = request.getParameter("idtype");
					if (hot > 0 && id > 0 && !Common.empty(idType)) {
						String tablename = cpService.getTablebyIdType(idType);
						if (tablename != null) {
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName(tablename)
									+ " SET hot=" + hot + " WHERE " + idType + "=" + id);
						}
					}
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=feed&feedid=" + feedId);
			} else if (submitCheck(request, "deletesubmit")) {
				Object[] ids = request.getParameterValues("ids");
				if (ids != null && adminDeleteService.deleteFeeds(request, response, supe_uid, ids)) {
					return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
				} else {
					return cpMessage(request, mapping, "cp_choose_to_delete_events");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}

		String op = request.getParameter("op");
		if ("add".equals(op)) {
			if (!allowManage) {
				return cpMessage(request, mapping, "cp_no_authority_management_operation");
			}
			Map<String, Object> feed = new HashMap<String, Object>();
			String dateline = Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) sGlobal.get("timestamp"));
			feed.put("dateline", dateline);
			feed.put("timestamp", dateline);
			request.setAttribute("feed", feed);
		} else if ("edit".equals(op)) {
			if (!allowManage) {
				return cpMessage(request, mapping, "cp_no_authority_management_operation");
			}
			int feedid = Common.intval(request.getParameter("feedid"));
			List<Map<String, Object>> feedList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("feed") + " WHERE feedid='" + feedid + "'");
			if (feedList.isEmpty()) {
				return cpMessage(request, mapping, "feed_does_not_exist");
			}
			Map<String, Object> feed = feedList.get(0);
			int uid = (Integer) feed.get("uid");
			if (uid > 0) {
				Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
				Common.realname_set(sGlobal, sConfig, sNames, uid, (String) feed.get("username"), "", 0);
				Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
				Common.realname_get(sGlobal, sConfig, sNames, space);
				feed = Common.mkFeed(sNames, sConfig, request, feed, null);
			}
			feed.put("title_template", Common.sHtmlSpecialChars(feed.get("title_template")));
			feed.put("body_general", Common.sHtmlSpecialChars(feed.get("body_general")));
			feed.put("dateline", Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) feed.get("dateline")));
			feed.put("timestamp", Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) sGlobal
					.get("timestamp")));
			request.setAttribute("feed", feed);
		} else if ("delete".equals(op)) {
			int feedid = Common.intval(request.getParameter("feedid"));
			if (adminDeleteService.deleteFeeds(request, response, supe_uid, feedid)) {
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=feed");
			} else {
				return cpMessage(request, mapping, "cp_choose_to_delete_events", "admincp.jsp?ac=feed");
			}
		} else {
			StringBuffer mpURL = new StringBuffer("admincp.jsp?ac=feed");
			String[] intkeys = new String[] { "uid", "feedid" };
			String[] strkeys = new String[] { "username", "icon" };
			List<String[]> randkeys = new ArrayList<String[]>();
			randkeys.add(new String[] { "sstrtotime", "dateline" });
			randkeys.add(new String[] { "intval", "hot" });
			Map<String, String> wheres = getWheres(intkeys, strkeys, randkeys, null, "", paramMap, timeoffset);
			String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
			mpURL.append(wheres.get("url"));
			Map<String, String> orders = getOrders(new String[] { "dateline", "hot" }, "feedid", null,
					paramMap);
			String ordersql = orders.get("sql");
			mpURL.append(orders.get("url"));
			request.setAttribute("orderby_" + request.getParameter("orderby"), " selected");
			request.setAttribute("ordersc_" + request.getParameter("ordersc"), " selected");
			String uid = request.getParameter("uid");
			if (uid != null && Common.strlen(uid) > 0) {
				request.setAttribute("active_site", " class=\"active\"");
			} else if ("hot".equals(request.getParameter("orderby"))) {
				request.setAttribute("active_hot", " class=\"active\"");
			} else {
				request.setAttribute("active_all", " class=\"active\"");
			}
			int perpage = Common.intval(request.getParameter("perpage"));
			if (!Common.in_array(new Integer[] { 20, 50, 100, 1000 }, perpage)) {
				perpage = 20;
			}
			int page = Math.max(Common.intval(request.getParameter("page")), 1);
			int start = (page - 1) * perpage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perpage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			int count = 1;
			String selectsql = null;
			if (perpage > 100) {
				selectsql = "feedid";
			} else {
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("feed") + " WHERE " + whereSQL);
				selectsql = "*";
			}
			mpURL.append("&perpage=" + perpage);
			request.setAttribute("perpage_" + perpage, " selected");
			boolean managebatch = Common.checkPerm(request, response, "managebatch");
			boolean allowbatch = true;
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT " + selectsql
						+ " FROM " + JavaCenterHome.getTableName("feed") + " WHERE " + whereSQL + " "
						+ ordersql + " LIMIT " + start + "," + perpage);
				if (perpage > 100) {
					count = list.size();
				} else {
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), (String) value.get("name"), 1);
						if (!managebatch && (Integer) value.get("uid") != supe_uid) {
							allowbatch = false;
						}
						value = Common.mkFeed(sNames, sConfig, request, value, null);
						value.put("dateline", Common.sgmdate(request, "MM-dd HH:mm", (Integer) value
								.get("dateline"), true));
					}
				}
				request.setAttribute("list", list);
				request.setAttribute("multi", Common.multi(request, count, perpage, page, maxPage, mpURL
						.toString(), null, null));
				if(list.size()%perpage==1){
					mpURL.append("&page="+(page-1));
				}else{
					mpURL.append("&page="+page);
				}
			}

			Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("count", count);
			request.setAttribute("allowbatch", allowbatch);
			request.setAttribute("perpage", perpage);
			request.setAttribute("mpurl", mpURL);
		}
		request.setAttribute("FORMHASH", formHash(request));
		request.setAttribute("allowmanage", allowManage);
		request.setAttribute("op", op);
		return mapping.findForward("feed");
	}
}