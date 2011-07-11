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


public class PicAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		boolean allowmanage = Common.checkPerm(request, response, "managealbum");
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!allowmanage) {
			paramMap.put("uid", new String[] { String.valueOf(supe_uid) });
		}
		try {
			if (submitCheck(request, "batchsubmit")) {
				Object[] ids = request.getParameterValues("ids");
				if (ids != null && adminDeleteService.deletePics(request, response, supe_uid, ids)) {
					return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
				} else {
					return cpMessage(request, mapping, "cp_choose_to_delete_pictures");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}

		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=pic");
		String username = Common.trim(request.getParameter("username"));
		if (username.length() > 0) {
			List<String> uids = dataBaseService.executeQuery("SELECT uid FROM "
					+ JavaCenterHome.getTableName("space") + " WHERE username='" + username + "'", 1);
			if (uids.size() > 0) {
				paramMap.put("uid", new String[] { uids.get(0) });
			}
		}
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);

		String[] intkeys = new String[] { "albumid", "uid", "picid" };
		String[] strkeys = new String[] { "postip" };
		List<String[]> randkeys = new ArrayList<String[]>();
		randkeys.add(new String[] { "sstrtotime", "dateline" });
		randkeys.add(new String[] { "intval", "hot" });
		String[] likekeys = new String[] { "filename", "title" };
		Map<String, String> wheres = getWheres(intkeys, strkeys, randkeys, likekeys, "", paramMap, timeoffset);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpurl.append(wheres.get("url"));
		Map<String, String> orders = getOrders(new String[] { "dateline", "size", "hot" }, "picid", null,
				paramMap);
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
			selectsql = "picid";
		} else {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pic")
					+ " WHERE " + whereSQL + "");
			selectsql = "*";
		}
		mpurl.append("&perpage=" + perpage);
		request.setAttribute("perpage_" + perpage, " selected");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		boolean allowbatch = true;
		if (count > 0) {
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT " + selectsql + " FROM "
					+ JavaCenterHome.getTableName("pic") + " WHERE " + whereSQL + " " + ordersql + " LIMIT "
					+ start + "," + perpage);
			if (perpage > 100) {
				count = list.size();
			} else {
				List<Integer> albumIds = new ArrayList<Integer>();
				List<Integer> uids = new ArrayList<Integer>();
				SimpleDateFormat picSDF = Common.getSimpleDateFormat("yyyy-MM-dd HH:mm", timeoffset);
				for (Map<String, Object> value : list) {
					String filePath = (String) value.get("filepath");
					int thumb = (Integer) value.get("thumb");
					int remote = (Integer) value.get("remote");
					value.put("pic", Common.pic_get(sConfig, filePath, thumb, remote, true));
					value.put("bigpic", Common.pic_get(sConfig, filePath, thumb, remote, false));
					int albumId = (Integer) value.get("albumid");
					if (albumId > 0) {
						albumIds.add(albumId);
					}
					if (!managebatch && ((Integer) value.get("uid")) != supe_uid) {
						allowbatch = false;
					}
					int uid = (Integer) value.get("uid");
					if (uid > 0) {
						uids.add(uid);
					}
					value.put("size", Common.formatSize((Integer) value.get("size")));
					value.put("dateline", Common.gmdate(picSDF, (Integer) value.get("dateline")));
				}
				Map<Object, Object> albums = new HashMap<Object, Object>();
				Map<Object, Object> users = new HashMap<Object, Object>();
				if (albumIds.size() > 0) {
					List<Map<String, Object>> albumList = dataBaseService
							.executeQuery("SELECT albumid, albumname FROM "
									+ JavaCenterHome.getTableName("album") + " WHERE albumid IN ("
									+ Common.sImplode(albumIds) + ")");
					for (Map<String, Object> value : albumList) {
						albums.put(value.get("albumid"), value.get("albumname"));
					}
				}
				if (uids.size() > 0) {
					String uidstr = Common.sImplode(uids);
					if (uidstr.equals("'" + supe_uid + "'")) {
						users.put(supe_uid, sGlobal.get("supe_username"));
					} else {
						List<Map<String, Object>> userList = dataBaseService
								.executeQuery("SELECT uid, username FROM "
										+ JavaCenterHome.getTableName("space") + " WHERE uid IN (" + uidstr
										+ ")");
						for (Map<String, Object> value : userList) {
							users.put(value.get("uid"), value.get("username"));
						}
					}
				}
				request.setAttribute("albums", albums);
				request.setAttribute("users", users);
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
		return mapping.findForward("pic");
	}
}