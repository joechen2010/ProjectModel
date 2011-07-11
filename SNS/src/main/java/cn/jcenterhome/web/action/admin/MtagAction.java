
package cn.jcenterhome.web.action.admin;

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
import cn.jcenterhome.web.action.BaseAction;


public class MtagAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managemtag")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		try {
			if (submitCheck(request, "opsubmit")) {
				String[] ids = request.getParameterValues("ids");
				if (!managebatch && ids != null && ids.length > 1) {
					return cpMessage(request, mapping, "cp_no_authority_management_operation");
				}
				String mpurl = request.getParameter("mpurl");
				String opType = request.getParameter("optype");
				if ("delete".equals(opType)) {
					if (ids != null && adminDeleteService.deleteMtag(request, response, ids)) {
						return cpMessage(request, mapping, "do_success", mpurl);
					} else {
						return cpMessage(request, mapping, "cp_choose_to_delete_the_columns_tag");
					}
				} else if ("merge".equals(opType)) {
					int merge_newfieldid = Common.intval(request.getParameter("merge_newfieldid"));
					String newtagname = (String) Common.sHtmlSpecialChars(Common.trim(request
							.getParameter("newtagname")));
					Map<String, Object> whereArr = new HashMap<String, Object>();
					whereArr.put("tagname", newtagname);
					whereArr.put("fieldid", merge_newfieldid);
					int newtagid = Common.intval(Common.getCount("mtag", whereArr, "tagid"));
					if (newtagid == 0) {
						return cpMessage(request, mapping, "cp_designated_to_merge_the_columns_do_not_exist");
					}
					if (ids != null && opService.mergeMtag(request, response, newtagid, ids)) {
						return cpMessage(request, mapping, "cp_the_successful_merger_of_the_designated_columns",
								mpurl);
					} else {
						return cpMessage(request, mapping, "cp_columns_option_to_merge_the_tag");
					}
				} else if ("move".equals(opType)) {
					int move_newfieldid = Common.intval(request.getParameter("move_newfieldid"));
					if (ids != null && move_newfieldid > 0) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag")
								+ " SET fieldid=" + move_newfieldid + " WHERE tagid IN ("
								+ Common.sImplode(ids) + ")");
						return cpMessage(request, mapping, "do_success", mpurl);
					} else {
						return cpMessage(request, mapping, "cp_choose_to_operate_columns_tag");
					}
				} else if ("close".equals(opType) || "open".equals(opType)) {
					if (ids != null && opService.closeMtag(request, response, opType, ids)) {
						return cpMessage(request, mapping, "cp_lock_open_designated_columns_tag_success", mpurl);
					} else {
						return cpMessage(request, mapping, "cp_choose_to_operate_columns_tag");
					}
				} else if ("recommend".equals(opType) || "unrecommend".equals(opType)) {
					if (ids != null && opService.recommendMtag(request, response, opType, ids)) {
						return cpMessage(request, mapping, "cp_recommend_designated_columns_tag_success", mpurl);
					} else {
						return cpMessage(request, mapping, "cp_choose_to_operate_columns_tag");
					}
				} else {
					return cpMessage(request, mapping, "cp_choice_batch_action");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		Common.getCacheDate(request, response, "/data/cache/cache_profield.jsp", "globalProfield");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=mtag");
		String[] intkeys = new String[] { "close", "recommend", "fieldid", "joinperm", "viewperm", "threadperm", "postperm", "tagid" };
		List<String[]> randkeys = new ArrayList<String[]>();
		randkeys.add(new String[] { "intval", "membernum" });
		randkeys.add(new String[] { "intval", "threadnum" });
		randkeys.add(new String[] { "intval", "postnum" });
		String[] likekeys = new String[] { "tagname" };
		Map<String, String[]> paramMap = request.getParameterMap();
		Map<String, String> wheres = getWheres(intkeys, null, randkeys, likekeys, "", paramMap, null);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpurl.append(wheres.get("url"));
		Map<String, String> orders = getOrders(new String[] { "membernum", "threadnum", "postnum" },
				"tagid", null, paramMap);
		String ordersql = orders.get("sql");
		mpurl.append(orders.get("url"));
		request.setAttribute("orderby_" + request.getParameter("orderby"), " selected");
		request.setAttribute("ordersc_" + request.getParameter("ordersc"), " selected");

		request.setAttribute("joinperm_" + request.getParameter("joinperm"), " selected");
		request.setAttribute("viewperm_" + request.getParameter("viewperm"), " selected");
		request.setAttribute("threadperm_" + request.getParameter("threadperm"), " selected");
		request.setAttribute("postperm_" + request.getParameter("postperm"), " selected");
		int perpage = Common.intval(request.getParameter("perpage"));
		if (!Common.in_array(new Integer[] { 20, 50, 100 }, perpage)) {
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
		int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
				+ JavaCenterHome.getTableName("mtag") + " WHERE " + whereSQL);
		if (count > 0) {
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("mtag") + " WHERE " + whereSQL + " " + ordersql
					+ " LIMIT " + start + "," + perpage);
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
		request.setAttribute("mpurl", mpurl);
		request.setAttribute("managebatch", managebatch);
		return mapping.findForward("mtag");
	}
}