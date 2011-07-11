package cn.jcenterhome.web.action.admin;

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


public class MagicLogAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		int perPage = 50;
		boolean allowManage = Common.checkPerm(request, response, "managemagiclog");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!allowManage) {
			paramMap.put("uid", new String[] {sGlobal.get("supe_uid").toString()});
			paramMap.put("username", new String[] {sGlobal.get("supe_username").toString()});
			request.setAttribute("disabled", " disabled");
		}
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		String view = request.getParameter("view");
		if (Common.empty(view)) {
			view = "holdlog";
		}
		request.setAttribute("actives_" + view, " class='active'");

		if ("inlog".equals(view)) {
			StringBuffer mpUrl = new StringBuffer("admincp.jsp?ac=magiclog&view=inlog");

			String[] intKeys = {"type"};
			String[] strKeys = {"mid"};
			String[] likeKeys = {"username"};
			Map<String, String> wheres = getWheres(intKeys, strKeys, null, likeKeys, "", paramMap, null);

			StringBuffer tempSQL = new StringBuffer();
			String startTime = request.getParameter("starttime");
			if (!Common.empty(startTime)) {
				tempSQL.append(" AND dateline >= "
						+ Common.strToTime(startTime, timeoffset, "yyyy-MM-dd HH:mm"));
				mpUrl.append("&starttime=" + startTime);
			}
			String endTime = request.getParameter("endtime");
			if (!Common.empty(endTime)) {
				tempSQL.append(" AND dateline <= "
						+ Common.strToTime(endTime, timeoffset, "yyyy-MM-dd HH:mm"));
				mpUrl.append("&endtime=" + endTime);
			}
			String countStr = request.getParameter("count");
			if (!Common.empty(countStr)) {
				String[] temp = countStr.split("-");
				tempSQL.append(" AND count >= '" + temp[0] + "' AND count <= '" + temp[1] + "'");
				mpUrl.append("&count=" + countStr);
			}
			String whereSQL = wheres.get("sql");
			if (whereSQL != null) {
				whereSQL += tempSQL;
			} else if (tempSQL.length() > 0) {
				whereSQL = tempSQL.substring(4);
			} else {
				whereSQL = "1";
			}
			mpUrl.append(wheres.get("url"));

			mpUrl.append("&perPage=" + perPage);
			int page = Math.max(Common.intval(request.getParameter("page")), 1);
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}

			String sql = "SELECT COUNT(*) as thecount FROM " + JavaCenterHome.getTableName("magicinlog")
					+ " WHERE " + whereSQL;
			int count = dataBaseService.findRows(sql);
			if (count > 0) {
				sql = "SELECT * FROM " + JavaCenterHome.getTableName("magicinlog") + " WHERE " + whereSQL
						+ " ORDER BY dateline DESC LIMIT " + start + "," + perPage;
				List<Map<String, Object>> list = dataBaseService.executeQuery(sql);
				for (Map<String, Object> value : list) {
					value.put("dateline", Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) value
							.get("dateline")));
				}
				String multi = Common.multi(request, count, perPage, page, maxPage, mpUrl.toString(), null,
						null);
				request.setAttribute("list", list);
				request.setAttribute("multi", multi);
			}
		} else if ("uselog".equals(view)) {
			StringBuffer mpUrl = new StringBuffer("admincp.jsp?ac=magiclog&view=uselog");
			String[] intKeys = {"id"};
			String[] strKeys = {"mid", "idtype"};
			String[] likeKeys = {"username"};
			Map<String, String> wheres = getWheres(intKeys, strKeys, null, likeKeys, "", paramMap, null);
			StringBuffer tempSQL = new StringBuffer();
			String startTime = request.getParameter("starttime");
			if (!Common.empty(startTime)) {
				tempSQL.append(" AND dateline >= "
						+ Common.strToTime(startTime, timeoffset, "yyyy-MM-dd HH:mm"));
				mpUrl.append("&starttime=" + startTime);
			}
			String endTime = request.getParameter("endtime");
			if (!Common.empty(endTime)) {
				tempSQL.append(" AND dateline <= "
						+ Common.strToTime(endTime, timeoffset, "yyyy-MM-dd HH:mm"));
				mpUrl.append("&endtime=" + endTime);
			}

			String whereSQL = wheres.get("sql");
			if (whereSQL != null) {
				whereSQL += tempSQL;
			} else if (tempSQL.length() > 0) {
				whereSQL = tempSQL.substring(4);
			} else {
				whereSQL = "1";
			}
			mpUrl.append(wheres.get("url"));

			mpUrl.append("&perpage=" + perPage);
			int page = Math.max(Common.intval(request.getParameter("page")), 1);
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}

			String sql = "SELECT COUNT(*) as thecount FROM " + JavaCenterHome.getTableName("magicuselog")
					+ " WHERE " + whereSQL;
			int count = dataBaseService.findRows(sql);
			if (count > 0) {
				sql = "SELECT * FROM " + JavaCenterHome.getTableName("magicuselog") + " WHERE " + whereSQL
						+ " ORDER BY dateline DESC LIMIT " + start + "," + perPage;
				List<Map<String, Object>> list = dataBaseService.executeQuery(sql);
				for (Map<String, Object> value : list) {
					value.put("dateline", Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) value
							.get("dateline")));
				}
				String multi = Common.multi(request, count, perPage, page, maxPage, mpUrl.toString(), null,
						null);
				request.setAttribute("list", list);
				request.setAttribute("multi", multi);
			}
		} else if ("storelog".equals(view)) {
			if (!allowManage) {
				return cpMessage(request, mapping, "cp_no_authority_management_operation");
			}

			int totalCount = 0, totalCredit = 0;
			String sql = "SELECT * FROM " + JavaCenterHome.getTableName("magicstore")
					+ " ORDER BY sellcount DESC";
			List<Map<String, Object>> list = dataBaseService.executeQuery(sql);
			for (Map<String, Object> value : list) {
				totalCount += (Integer) value.get("sellcount");
				totalCredit += (Integer) value.get("sellcredit");
			}

			request.setAttribute("totalcount", totalCount);
			request.setAttribute("totalcredit", totalCredit);
			request.setAttribute("list", list);
		} else {
			StringBuffer mpUrl = new StringBuffer("admincp.jsp?ac=magiclog&view=holdlog");

			String[] intKeys = {"uid"};
			String[] strKeys = {"mid"};
			String[] likeKeys = {"username"};
			Map<String, String> wheres = getWheres(intKeys, strKeys, null, likeKeys, "", paramMap, null);

			String whereSQL = wheres.get("sql");
			if (whereSQL == null) {
				whereSQL = "count > 0";
			} else {
				whereSQL += " AND count > 0";
			}

			mpUrl.append(wheres.get("url"));
			mpUrl.append("&perpage=" + perPage);
			int page = Math.max(Common.intval(request.getParameter("page")), 1);
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}

			String sql = "SELECT COUNT(*) as thecount FROM " + JavaCenterHome.getTableName("usermagic")
					+ " WHERE " + whereSQL;
			int count = dataBaseService.findRows(sql);
			if (count > 0) {
				sql = "SELECT * FROM " + JavaCenterHome.getTableName("usermagic") + " WHERE " + whereSQL
						+ " LIMIT " + start + "," + perPage;
				List<Map<String, Object>> list = dataBaseService.executeQuery(sql);
				String multi = Common.multi(request, count, perPage, page, maxPage, mpUrl.toString(), null,
						null);
				request.setAttribute("list", list);
				request.setAttribute("multi", multi);
			}
		}

		request.setAttribute("allowmanage", allowManage);
		request.setAttribute("view", view);
		return mapping.findForward("magiclog");
	}
}