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


public class ShareAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		boolean allowmanage = Common.checkPerm(request, response, "manageshare");
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!allowmanage) {
			paramMap.put("uid", new String[] { String.valueOf(supe_uid) });
			paramMap.put("username", null);
		}
		try {
			if (submitCheck(request, "batchsubmit")) {
				String[] ids = request.getParameterValues("ids");
				if (ids != null && adminDeleteService.deleteShares(request, response, supe_uid, ids)) {
					return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
				} else {
					return cpMessage(request, mapping, "cp_please_delete_the_correct_choice_to_share");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=share");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);

		String[] intkeys = new String[] { "uid", "sid" };
		String[] strkeys = new String[] { "username", "type" };
		List<String[]> randkeys = new ArrayList<String[]>();
		randkeys.add(new String[] { "sstrtotime", "dateline" });
		randkeys.add(new String[] { "intval", "hot" });
		Map<String, String> wheres = getWheres(intkeys, strkeys, randkeys, null, "", paramMap, timeoffset);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpurl.append(wheres.get("url"));
		Map<String, String> orders = getOrders(new String[] { "dateline", "hot" }, "sid", null, paramMap);
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
			selectsql = "sid";
		} else {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("share")
					+ " WHERE " + whereSQL);
			selectsql = "*";
		}
		mpurl.append("&perpage=" + perpage);
		request.setAttribute("perpage_" + perpage, " selected");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		boolean allowbatch = true;
		if (count > 0) {
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT " + selectsql + " FROM "
					+ JavaCenterHome.getTableName("share") + " WHERE " + whereSQL + " " + ordersql
					+ " LIMIT " + start + "," + perpage);
			
			if (perpage > 100) {
				count = list.size();
			} else {
				SimpleDateFormat doingSDF = Common.getSimpleDateFormat("yyyy-MM-dd HH:mm", timeoffset);
				for (Map<String, Object> value : list) {
					Common.mkShare(value);
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
		return mapping.findForward("share");
	}
}