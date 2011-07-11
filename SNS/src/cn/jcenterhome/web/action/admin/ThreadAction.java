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


public class ThreadAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int tagid = Common.intval(request.getParameter("tagid"));
		try {
			if (submitCheck(request, "opsubmit")) {
				String optype = request.getParameter("optype");
				Object[] ids = request.getParameterValues("ids");
				if ("delete".equals(optype)) {
					if (ids != null
							&& adminDeleteService.deleteThreads(request, response, supe_uid, tagid, ids) != null) {
						return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
					} else {
						return cpMessage(request, mapping, "cp_choose_to_delete_the_topic");
					}
				} else if ("digest".equals(optype)) {
					int v = Common.intval(request.getParameter("digestv"));
					if (ids != null && opService.digestThreads(request, response, supe_uid, tagid, v, ids)) {
						return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
					} else {
						return cpMessage(request, mapping, "cp_choosing_to_operate_the_topic");
					}
				} else if ("top".equals(optype)) {
					int v = Common.intval(request.getParameter("topv"));
					if (ids != null && opService.topThreads(request, response, supe_uid, tagid, v, ids)) {
						return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
					} else {
						return cpMessage(request, mapping, "cp_choosing_to_operate_the_topic");
					}
				} else {
					return cpMessage(request, mapping, "cp_choice_batch_action");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		boolean allowbatch = true;
		boolean allowmanage = false;

		if (Common.checkPerm(request, response, "managethread")) {
			allowmanage = true;
		} else if (tagid > 0) {
			Map<String, Object> whereArr = new HashMap<String, Object>();
			whereArr.put("tagid", tagid);
			whereArr.put("uid", supe_uid);
			int grade = Common.intval(Common.getCount("tagspace", whereArr, "grade"));
			if (grade >= 8) {
				managebatch = true;
				allowmanage = true;
			}
		}
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!allowmanage) {
			paramMap.put("uid", new String[] { String.valueOf(supe_uid) });
			paramMap.put("username", null);
		}
		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=thread");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);

		String[] intKeys = new String[] { "uid", "tagid", "digest", "tid" };
		String[] strKeys = new String[] { "username" };
		List<String[]> randKeys = new ArrayList<String[]>();
		randKeys.add(new String[] { "sstrtotime", "dateline" });
		randKeys.add(new String[] { "intval", "viewnum" });
		randKeys.add(new String[] { "intval", "replynum" });
		randKeys.add(new String[] { "intval", "hot" });
		String[] likeKeys = new String[] { "subject" };
		Map<String, String> wheres = getWheres(intKeys, strKeys, randKeys, likeKeys, "", paramMap, timeoffset);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpurl.append(wheres.get("url"));
		Map<String, String> orders = getOrders(
				new String[] { "dateline", "lastpost", "viewnum", "replynum", "hot" }, "tid", null, paramMap);
		String ordersql = orders.get("sql");
		mpurl.append(orders.get("url"));
		request.setAttribute("orderby_" + request.getParameter("orderby"), " selected");
		request.setAttribute("ordersc_" + request.getParameter("ordersc"), " selected");
		int perPage = Common.intval(request.getParameter("perpage"));
		if (!Common.in_array(new Integer[] { 20, 50, 100, 1000 }, perPage)) {
			perPage = 20;
		}
		int page = Math.max(Common.intval(request.getParameter("page")), 1);
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		int count = 1;
		String selectsql = null;
		if (perPage > 100) {
			selectsql = "tid";
		} else {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("thread")
					+ " WHERE " + whereSQL);
			selectsql = "*";
		}
		mpurl.append("&perpage=" + perPage);
		request.setAttribute("perpage_" + perPage, " selected");
		if (count > 0) {
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT " + selectsql + " FROM "
					+ JavaCenterHome.getTableName("thread") + " WHERE " + whereSQL + " " + ordersql
					+ " LIMIT " + start + "," + perPage);
			if (perPage > 100) {
				count = list.size();
			} else {
				SimpleDateFormat threadSDF = Common.getSimpleDateFormat("yyyy-MM-dd", timeoffset);
				List<Integer> tagids = new ArrayList<Integer>();
				for (Map<String, Object> value : list) {
					int tagId=(Integer)value.get("tagid");
					if (tagId>0) {
						tagids.add(tagId);
					}
					if (!managebatch && (Integer) value.get("uid") != supe_uid) {
						allowbatch = false;
					}
					value.put("dateline", Common.gmdate(threadSDF, (Integer) value.get("dateline")));
				}
				if (tagids.size() > 0) {
					Map<Object,Object> tags = new HashMap<Object,Object>();
					List<Map<String, Object>> mtagList = dataBaseService
							.executeQuery("SELECT tagid, tagname FROM " + JavaCenterHome.getTableName("mtag")
									+ " WHERE tagid IN (" + Common.sImplode(tagids) + ")");
					for (Map<String, Object> value : mtagList) {
						tags.put(value.get("tagid"), value.get("tagname"));
					}
					request.setAttribute("tags", tags);
				}
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
		request.setAttribute("count", count);
		request.setAttribute("mpurl", mpurl);
		request.setAttribute("allowmanage", allowmanage);
		request.setAttribute("allowbatch", allowbatch);
		request.setAttribute("perpage", perPage);
		request.setAttribute("tagid", tagid);
		return mapping.findForward("thread");
	}
}