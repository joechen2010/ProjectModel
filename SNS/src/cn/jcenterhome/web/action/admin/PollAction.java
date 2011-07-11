package cn.jcenterhome.web.action.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class PollAction extends BaseAction {
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		boolean allowmanage = Common.checkPerm(request, response, "managepoll");
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!allowmanage) {
			paramMap.put("uid", new String[] { String.valueOf(supe_uid) });
			paramMap.put("username", null);
		}

		try {
			if (submitCheck(request, "deletesubmit")) {
				String[] ids = request.getParameterValues("ids");
				if (ids != null && adminDeleteService.deletePolls(request, response, supe_uid, ids)) {
					return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
				} else {
					return cpMessage(request, mapping, "cp_the_correct_choice_to_delete_the_poll");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=poll");
		String op = Common.trim(request.getParameter("op"));

		if ("delete".equals(op)) {
			String pid = request.getParameter("pid");
			if (!Common.empty(pid)) {
				adminDeleteService.deletePolls(request, response, supe_uid, pid);
			}
			return cpMessage(request, mapping, "do_success", mpurl.toString());
		} else {
			String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
			int expiration = Common.intval(request.getParameter("expiration"));
			int timestamp = (Integer) sGlobal.get("timestamp");
			if (expiration > 0) {
				paramMap.put("expiration" + (expiration == 1 ? 1 : 2), new String[] { String
						.valueOf(timestamp) });
			}
			String[] intKeys = new String[] { "uid", "noreply", "pid", "sex" };
			String[] strKeys = new String[] { "username" };
			List<String[]> randKeys = new ArrayList<String[]>();
			randKeys.add(new String[] { "sstrtotime", "dateline" });
			randKeys.add(new String[] { "intval", "voternum" });
			randKeys.add(new String[] { "intval", "replynum" });
			randKeys.add(new String[] { "intval", "percredit" });
			randKeys.add(new String[] { "intval", "expiration" });
			randKeys.add(new String[] { "intval", "hot" });
			String[] likeKeys = new String[] { "subject" };
			Map<String, String> wheres = getWheres(intKeys, strKeys, randKeys, likeKeys, "", paramMap,
					timeoffset);
			String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
			mpurl.append(wheres.get("url"));
			Map<String, String> orders = getOrders(
					new String[] { "dateline", "viewnum", "replynum", "percredit", "hot" }, "pid", null,
					paramMap);
			String ordersql = orders.get("sql").toString();
			mpurl.append(orders.get("url"));
			request.setAttribute("orderby_" + request.getParameter("orderby"), " selected");
			request.setAttribute("ordersc_" + request.getParameter("ordersc"), " selected");
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
				selectsql = "pid";
			} else {
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("poll") + " WHERE " + whereSQL);
				selectsql = "*";
			}
			mpurl.append("&perpage=" + perpage);
			request.setAttribute("perpage_" + perpage, " selected");
			boolean managebatch = Common.checkPerm(request, response, "managebatch");
			boolean allowbatch = true;
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT " + selectsql
						+ " FROM " + JavaCenterHome.getTableName("poll") + " WHERE " + whereSQL + " "
						+ ordersql + " LIMIT " + start + "," + perpage);
				if (perpage > 100) {
					count = list.size();
				} else {
					SimpleDateFormat doingSDF = Common.getSimpleDateFormat("yyyy-MM-dd", timeoffset);
					for (Map<String, Object> value : list) {
						expiration = (Integer) value.get("expiration");
						value.put("isexpired", expiration > 0 && expiration < timestamp ? true : false);
						if (!managebatch && (Integer) value.get("uid") != supe_uid) {
							allowbatch = false;
						}
						value.put("dateline", Common.gmdate(doingSDF, (Integer) value.get("dateline")));
					}
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
			request.setAttribute("allowmanage", allowmanage);
			request.setAttribute("allowbatch", allowbatch);
			request.setAttribute("perpage", perpage);
			return mapping.findForward("poll");
		}
	}
}