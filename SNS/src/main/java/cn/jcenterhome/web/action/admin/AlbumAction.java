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


public class AlbumAction extends BaseAction {

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
			paramMap.put("username", null);
		}
		try {
			if (submitCheck(request, "deletesubmit")) {
				String[] ids = request.getParameterValues("ids");
				if (ids != null && adminDeleteService.deleteAlbums(request, response, supe_uid, ids)) {
					return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
				} else {
					return cpMessage(request, mapping, "cp_at_least_one_option_to_delete_albums");
				}
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		StringBuffer mpURL = new StringBuffer("admincp.jsp?ac=album");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		String[] intKeys = new String[] { "uid", "friend", "albumid" };
		String[] strKeys = new String[] { "username" };
		List<String[]> randKeys = new ArrayList<String[]>();
		randKeys.add(new String[] { "sstrtotime", "dateline" });
		String[] likeKeys = new String[] { "albumname" };

		Map<String, String> wheres = getWheres(intKeys, strKeys, randKeys, likeKeys, null, paramMap,
				timeoffset);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpURL.append(wheres.get("url"));
		Map<String, String> orders = getOrders(new String[] { "dateline", "updatetime", "picnum" },
				"albumid", null, paramMap);
		String orderSQL = orders.get("sql");
		mpURL.append(orders.get("url"));
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
			selectsql = "albumid";
		} else {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("album")
					+ " WHERE " + whereSQL);
			selectsql = "*";
		}
		mpURL.append("&perpage=" + perPage);
		request.setAttribute("perpage_" + perPage, " selected");

		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		boolean allowbatch = true;
		int picNum=0;
		boolean isOnlyDefault=false;
		int uid=Common.intval(request.getParameter("uid"));
		String defaultAlbumSQL=null;
		String username=Common.trim(request.getParameter("username"));
		if(!Common.empty(username)){
			uid=Common.getUid(sConfig, username);
		}
		if(uid>0){
			defaultAlbumSQL="`albumid`='0' AND `uid`='" + uid + "'";
		}
		if(defaultAlbumSQL!=null){
			picNum= dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pic")
					+ " WHERE "+defaultAlbumSQL+" LIMIT 1 ");
			if (picNum > 0) {
				if(count==0){
					isOnlyDefault=true;
				}
				count+=1;
			}
		}
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		SimpleDateFormat albumSDF = Common.getSimpleDateFormat("yyyy-MM-dd", timeoffset);
		if (count > 0 && !isOnlyDefault) {
			list = dataBaseService.executeQuery("SELECT " + selectsql + " FROM "
					+ JavaCenterHome.getTableName("album") + " WHERE " + whereSQL + " " + orderSQL
					+ " LIMIT " + start + "," + perPage);
			if (perPage > 100) {
				count = list.size();
				if (picNum > 0) {
					count+=picNum;
				}
			} else {
				Map<Integer,String> friends = new TreeMap<Integer, String>();
				friends.put(0, "全站用户可见");
				friends.put(1, "全好友可见");
				friends.put(2, "仅指定的好友可见");
				friends.put(3, "仅自己可见");
				friends.put(4, "凭密码查看");
				for (Map<String, Object> value : list) {
					value.put("pic", Common.pic_cover_get(sConfig, (String) value.get("pic"), (Integer) value
							.get("picflag")));
					if (!managebatch && (Integer) value.get("uid") != supe_uid) {
						allowbatch = false;
					}
					value.put("friendTitle", friends.get(value.get("friend")));
					value.put("dateline", Common.gmdate(albumSDF, (Integer) value.get("dateline")));
				}
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, mpURL
					.toString(), null, null));
			if(count%perPage==1){
				mpURL.append("&page="+(page-1));
			}else{
				mpURL.append("&page="+page);
			}
		}
		if (perPage<= 100) {
			if (picNum > 0) {
				List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT "+selectsql+ " FROM "
						+ JavaCenterHome.getTableName("pic")+" WHERE "+defaultAlbumSQL+" ORDER BY dateline DESC LIMIT 1");
				Map<String, Object> defaultAlbum=picList.get(0);
				defaultAlbum.put("pic", Common.pic_get(sConfig, (String) defaultAlbum.get("filepath"), (Integer) defaultAlbum
						.get("thumb"), (Integer) defaultAlbum.get("remote"), true));
				defaultAlbum.put("uid", uid);
				defaultAlbum.put("albumid", 0);
				defaultAlbum.put("albumname", Common.getMessage(request, "default_albumname"));
				defaultAlbum.put("picnum", picNum);
				defaultAlbum.put("dateline", Common.gmdate(albumSDF, (Integer) defaultAlbum.get("dateline")));
				list.add(0,defaultAlbum);
			}
		}
		request.setAttribute("list", list);
		request.setAttribute("FORMHASH", formHash(request));
		request.setAttribute("count", count);
		request.setAttribute("mpurl", mpURL);
		request.setAttribute("allowmanage", allowmanage);
		request.setAttribute("allowbatch", allowbatch);
		request.setAttribute("perpage", perPage);
		return mapping.findForward("album");
	}
}