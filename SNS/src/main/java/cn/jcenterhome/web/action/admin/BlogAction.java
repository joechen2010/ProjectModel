package cn.jcenterhome.web.action.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class BlogAction extends BaseAction {
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		boolean allowmanage = Common.checkPerm(request, response, "manageblog");
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!allowmanage) {
			paramMap.put("uid", new String[] { String.valueOf(supe_uid) });
			paramMap.put("username", null);
		}
		try {
			if (submitCheck(request, "batchsubmit")) {
				Object[] ids = request.getParameterValues("ids");
				if (ids != null && adminDeleteService.deleteBlogs(request, response, supe_uid, ids)) {
					return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
				} else {
					return cpMessage(request, mapping, "cp_the_correct_choice_to_delete_the_log");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=blog");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		
		String[] intkeys = new String[] { "uid", "friend", "blogid" };
		String[] strKeys = new String[] { "username" };
		List<String[]> randkeys = new ArrayList<String[]>();
		randkeys.add(new String[] { "sstrtotime", "dateline" });
		randkeys.add(new String[] { "intval", "viewnum" });
		randkeys.add(new String[] { "intval", "replynum" });
		randkeys.add(new String[] { "intval", "hot" });
		String[] likeKeys = new String[] { "subject" };
		Map<String, String> wheres1 = getWheres(intkeys, strKeys, randkeys, likeKeys, "b.", paramMap,
				timeoffset);
		mpurl.append(wheres1.get("url"));
		strKeys = new String[] { "postip" };
		likeKeys = new String[] { "message" };
		Map<String, String> wheres2 = getWheres(null, strKeys, null, likeKeys, "bf.", paramMap, null);
		mpurl.append(wheres2.get("url"));

		String whereSQL1 = wheres1.get("sql") == null ? "1" : wheres1.get("sql");
		String whereSQL2 = wheres2.get("sql");
		Map<String, String> orders = getOrders(new String[] { "dateline", "viewnum", "replynum", "hot" },
				"blogid", "b.", paramMap);
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
			selectsql = "b.blogid";
		} else {
			String csql = null;
			if (whereSQL2 != null) {
				csql = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("blog") + " b, "
						+ JavaCenterHome.getTableName("blogfield") + " bf WHERE " + whereSQL1
						+ " AND bf.blogid=b.blogid AND " + whereSQL2;
			} else {
				csql = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("blog") + " b WHERE "
						+ whereSQL1;
			}
			count = dataBaseService.findRows(csql);
			selectsql = "*";
		}
		mpurl.append("&perpage=" + perpage);
		request.setAttribute("perpage_" + perpage, " selected");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		boolean allowbatch = true;
		if (count > 0) {
			String qsql = null;
			if (whereSQL2 != null) {
				qsql = "SELECT " + selectsql + " FROM " + JavaCenterHome.getTableName("blog") + " b, "
						+ JavaCenterHome.getTableName("blogfield") + " bf WHERE " + whereSQL1
						+ " AND bf.blogid=b.blogid AND " + whereSQL2 + " ORDER BY b.blogid DESC LIMIT "
						+ start + "," + perpage + "";
			} else {
				qsql = "SELECT " + selectsql + " FROM " + JavaCenterHome.getTableName("blog") + " b WHERE "
						+ whereSQL1 + " " + ordersql + " LIMIT " + start + "," + perpage + "";
			}
			List<Map<String, Object>> list = dataBaseService.executeQuery(qsql);
			if (perpage > 100) {
				count = list.size();
			} else {
				Map<Integer,String> friends = new TreeMap<Integer, String>();
				friends.put(0, "全站用户可见");
				friends.put(1, "全好友可见");
				friends.put(2, "仅指定的好友可见");
				friends.put(3, "仅自己可见");
				friends.put(4, "凭密码查看");
				SimpleDateFormat blogSDF = Common.getSimpleDateFormat("yyyy-MM-dd HH:mm", timeoffset);
				for (Map<String, Object> blog : list) {
					if (!managebatch && ((Integer) blog.get("uid")) != supe_uid) {
						allowbatch = false;
					}
					blog.put("friendTitle", friends.get(blog.get("friend")));
					blog.put("dateline", Common.gmdate(blogSDF, (Integer) blog.get("dateline")));
				}
			}
			request.setAttribute("list", list);
			request.setAttribute("multi", Common.multi(request, count, perpage, page, maxPage, mpurl
					.toString(), null, null));
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
		return mapping.findForward("blog");
	}
}