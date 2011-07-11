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


public class CommentAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		boolean allowmanage = Common.checkPerm(request, response, "managecomment");
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!allowmanage) {
			paramMap.put("uid", new String[] { String.valueOf(supe_uid) });
		}
		try {
			if (submitCheck(request, "deletesubmit")) {
				Object[] ids = request.getParameterValues("ids");
				if (ids != null && adminDeleteService.deleteComments(request, response, supe_uid, ids)) {
					return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
				} else {
					return cpMessage(request, mapping, "cp_the_correct_choice_to_delete_comments");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}

		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=comment");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);

		String[] intKeys = new String[] { "uid", "cid", "id", "authorid" };
		String[] strKeys = new String[] { "author", "ip", "idtype" };
		List<String[]> randKeys = new ArrayList<String[]>();
		randKeys.add(new String[] { "sstrtotime", "dateline" });
		String[] likeKeys = new String[] { "title", "message" };
		Map<String, String> wheres = getWheres(intKeys, strKeys, randKeys, likeKeys, null, paramMap,
				timeoffset);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpurl.append(wheres.get("url"));
		Map<String, String> orders = getOrders(new String[] { "dateline" }, "cid", null, paramMap);
		String ordersql = orders.get("sql");
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
			selectsql = "cid";
		} else {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("comment")
					+ " WHERE " + whereSQL + "");
			selectsql = "*";
		}
		mpurl.append("&perpage=" + perpage);
		request.setAttribute("perpage_" + perpage, " selected");
		request.setAttribute("idtype_" + request.getParameter("idtype"), " selected");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		boolean allowbatch = true;
		if (count > 0) {
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT " + selectsql + " FROM "
					+ JavaCenterHome.getTableName("comment") + " WHERE " + whereSQL + " " + ordersql
					+ " LIMIT " + start + "," + perpage);
			if (perpage > 100) {
				count = list.size();
			} else {
				boolean cid_isEmpty=Common.empty(request.getParameter("cid"));
				SimpleDateFormat commentSDF = Common.getSimpleDateFormat("yyyy-MM-dd HH:mm", timeoffset);
				Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
				for (Map<String, Object> value : list) {
					if (cid_isEmpty&&!Common.empty(value.get("message"))) {
						try {
							value.put("message", Common.getStr(Common
									.stripTags((String) value.get("message")), 200, false, false, false, 0,
									0, request, response));
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
					}
					if (!managebatch && (Integer) value.get("uid") != supe_uid) {
						allowbatch = false;
					}
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
							(String) value.get("author"), "", 0);
					value.put("dateline", Common.gmdate(commentSDF, (Integer) value.get("dateline")));
				}
				Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
				Common.realname_get(sGlobal, sConfig, sNames, space);
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
		request.setAttribute("wheresql", whereSQL);
		return mapping.findForward("comment");
	}
}