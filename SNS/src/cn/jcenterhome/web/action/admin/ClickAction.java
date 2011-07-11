
package cn.jcenterhome.web.action.admin;

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


public class ClickAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageclick")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		int clickid = Common.intval(request.getParameter("clickid"));
		Map<String, Object> click = new HashMap<String, Object>();
		if (clickid > 0) {
			List<Map<String, Object>> clickList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("click") + " WHERE clickid='" + clickid + "'");
			if (clickList.size() > 0) {
				click = clickList.get(0);
			}
		}
		try {
			if (submitCheck(request, "clicksubmit")) {
				String name = Common.trim(request.getParameter("name"));
				String icon = Common.trim(request.getParameter("icon"));
				int displayorder = Common.range(request.getParameter("displayorder"), 255, 0);
				if(Common.empty(name)){
					return cpMessage(request, mapping, "cp_click_name_can_not_be_empty");
				}
				if (clickid == 0) {
					String idtype = request.getParameter("idtype");
					if (!Common.in_array(new String[] {"blogid", "picid", "tid"}, idtype)) {
						idtype = "blogid";
					}
					clickid = dataBaseService.insert("INSERT INTO " + JavaCenterHome.getTableName("click")
							+ " (name,icon,displayorder,idtype) VALUES ('" + name + "','" + icon + "','"
							+ displayorder + "','" + idtype + "')");
					String tablename = null;
					if ("picid".equals(idtype)) {
						tablename = JavaCenterHome.getTableName("pic");
					} else if ("tid".equals(idtype)) {
						tablename = JavaCenterHome.getTableName("thread");
					} else {
						tablename = JavaCenterHome.getTableName("blog");
					}
					dataBaseService.executeUpdate("ALTER TABLE " + tablename + " ADD click_" + clickid
							+ " smallint(6) unsigned NOT NULL default '0'");
				} else {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("click")
							+ " SET name='" + name + "',icon='" + icon + "',displayorder='" + displayorder
							+ "' WHERE clickid='" + clickid + "'");
				}
				cacheService.click_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=click");
			} else if (submitCheck(request, "ordersubmit")) {
				String[] clickIds = request.getParameterValues("clickIds");
				if (clickIds != null && clickIds.length > 0) {
					for (String clickId : clickIds) {
						int displayorder = Common.range(request.getParameter("displayorder" + clickId), 255,
								0);
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("click")
								+ " SET displayorder='" + displayorder + "' WHERE clickid='" + clickId + "'");
					}
				}
				cacheService.click_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=click");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		String op = request.getParameter("op");
		if (Common.empty(op)) {
			String idtype = request.getParameter("idtype");
			String where = null;
			if (!Common.empty(idtype)) {
				where = " WHERE idtype='" + idtype + "'";
			} else {
				where = "";
				idtype = "view";
			}
			List<Map<String, Object>> clicks = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("click") + where + " ORDER BY displayorder");
			Map<String, String> idtypeName = new HashMap<String, String>();
			idtypeName.put("blogid", "日志");
			idtypeName.put("picid", "图片");
			idtypeName.put("tid", "话题");
			request.setAttribute("idtypeName", idtypeName);
			request.setAttribute("actives_" + idtype, " class=\"active\"");
			request.setAttribute("clicks", clicks);
		} else if ("delete".equals(op)) {
			if (!click.isEmpty()) {
				String idtype = (String) click.get("idtype");
				String tablename = null;
				if ("picid".equals(idtype)) {
					tablename = JavaCenterHome.getTableName("pic");
				} else if ("tid".equals(idtype)) {
					tablename = JavaCenterHome.getTableName("thread");
				} else {
					tablename = JavaCenterHome.getTableName("blog");
				}
				dataBaseService.executeUpdate("ALTER TABLE " + tablename + " DROP click_" + clickid);
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("click")
						+ " WHERE clickid='" + clickid + "'");
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("clickuser")
						+ " WHERE clickid='" + clickid + "'");
				try {
					cacheService.click_cache();
				} catch (Exception e) {
					return cpMessage(request, mapping, e.getMessage());
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=click");
			}
		}
		request.setAttribute("op", op);
		request.setAttribute("click", click);
		return mapping.findForward("click");
	}
}