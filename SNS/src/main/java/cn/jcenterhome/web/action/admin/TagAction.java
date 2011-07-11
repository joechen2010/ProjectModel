
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
import cn.jcenterhome.web.action.BaseAction;


public class TagAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		boolean allowmanage = Common.checkPerm(request, response, "managetag");
		if (!allowmanage) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		try {
			if (submitCheck(request, "opsubmit")) {
				String mpurl = request.getParameter("mpurl");
				int opnum = 0;
				String[] ids = request.getParameterValues("ids");
				List<Object> newIds = new ArrayList<Object>();
				List<Map<String, Object>> tagList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("tag") + " WHERE tagid IN (" + Common.sImplode(ids)
						+ ")");
				for (Map<String, Object> value : tagList) {
					int uid = (Integer) value.get("uid");
					if (allowmanage || uid == supe_uid) {
						newIds.add(value.get("tagid"));
						if (!managebatch && uid != supe_uid) {
							opnum++;
						}
					}
				}
				if (!managebatch && opnum > 1) {
					return cpMessage(request, mapping, "cp_choose_to_delete_the_tag");
				}
				String opType = request.getParameter("optype");
				if ("delete".equals(opType)) {
					if (newIds.size() > 0
							&& adminDeleteService.deleteTags(request, response, supe_uid, newIds)) {
						return cpMessage(request, mapping, "do_success", mpurl);
					} else {
						return cpMessage(request, mapping, "cp_choose_to_delete_the_tag");
					}
				} else if ("merge".equals(opType)) {
					String newTagName = (String) Common.sHtmlSpecialChars(Common.trim(request
							.getParameter("newtagname")));
					int length = Common.strlen(newTagName);
					if (length < 1 || length > 30) {
						return cpMessage(request, mapping,
								"cp_to_merge_the_tag_name_of_the_length_discrepancies");
					}
					Map<String, Object> whereArr = new HashMap<String, Object>();
					whereArr.put("tagname", newTagName);
					int newTagId = Common.intval(Common.getCount("tag", whereArr, "tagid"));
					if (newTagId == 0) {
						newTagId = dataBaseService.insert("INSERT INTO " + JavaCenterHome.getTableName("tag")
								+ " (tagname,uid,dateline) VALUES ('" + newTagName + "','" + supe_uid + "','"
								+ sGlobal.get("timestamp") + "')");
					}
					if (newIds.size() > 0 && opService.mergeTag(request, response, newTagId, newIds)) {
						return cpMessage(request, mapping, "do_success", mpurl);
					} else {
						return cpMessage(request, mapping, "cp_the_tag_choose_to_merge", mpurl);
					}
				} else if ("close".equals(opType) || "open".equals(opType)) {
					if (newIds.size() > 0 && opService.closeTag(request, response, opType, newIds)) {
						return cpMessage(request, mapping, "do_success", mpurl);
					} else {
						return cpMessage(request, mapping, "cp_choose_to_operate_tag");
					}
				} else {
					return cpMessage(request, mapping, "cp_choice_batch_action");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=tag");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);

		String[] intkeys = new String[] { "close" };
		List<String[]> randkeys = new ArrayList<String[]>();
		randkeys.add(new String[] { "sstrtotime", "dateline" });
		randkeys.add(new String[] { "intval", "blognum" });
		String[] likekeys = new String[] { "tagname" };
		Map<String, String[]> paramMap = request.getParameterMap();
		Map<String, String> wheres = getWheres(intkeys, null, randkeys, likekeys, "", paramMap, timeoffset);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpurl.append(wheres.get("url"));
		Map<String, String> orders = getOrders(new String[] { "dateline", "blognum" }, "tagid", null,
				paramMap);
		String ordersql = orders.get("sql");
		mpurl.append(orders.get("url"));
		request.setAttribute("orderby_" + request.getParameter("orderby"), " selected");
		request.setAttribute("ordersc_" + request.getParameter("ordersc"), " selected");
		int perPage = Common.intval(request.getParameter("perpage"));
		if (!Common.in_array(new Integer[] { 20, 50, 100 }, perPage)) {
			perPage = 20;
		}
		mpurl.append("&perpage=" + perPage);
		request.setAttribute("perpage_" + perPage, " selected");
		int page = Math.max(Common.intval(request.getParameter("page")), 1);
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		boolean allowbatch = true;
		int count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("tag")
				+ " WHERE " + whereSQL + "");
		if (count > 0) {
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("tag") + " WHERE " + whereSQL + " " + ordersql + " LIMIT "
					+ start + "," + perPage);
			SimpleDateFormat tagSDF = Common.getSimpleDateFormat("yyyy-MM-dd", timeoffset);
			for (Map<String, Object> value : list) {
				if (!managebatch && (Integer) value.get("uid") != supe_uid) {
					allowbatch = false;
				}
				value.put("dateline", Common.gmdate(tagSDF, (Integer) value.get("dateline")));
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, mpurl
					.toString(), null, null));
			request.setAttribute("list", list);
			if(list.size()%perPage==1){
				mpurl.append("&page="+(page-1));
			}else{
				mpurl.append("&page="+page);
			}
		}
		request.setAttribute("FORMHASH", formHash(request));
		request.setAttribute("mpurl", mpurl);
		request.setAttribute("allowbatch", allowbatch);
		return mapping.findForward("tag");
	}
}