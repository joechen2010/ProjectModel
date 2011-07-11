package cn.jcenterhome.web.action.admin;

import java.io.File;
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


public class TaskAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managetask")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		int taskid = Common.intval(request.getParameter("taskid"));
		try {
			if (submitCheck(request, "tasksubmit")) {
				String fileName = Common.htmlSpecialChars(Common.trim(request.getParameter("filename")));
				fileName = fileName.replace("..", "").replace("/", "").replace("\\", "");
				File file = new File(JavaCenterHome.jchRoot + "./source/task/" + fileName);
				if (Common.empty(fileName) || !file.exists() || !file.canRead()) {
					return cpMessage(request, mapping, "cp_designated_script_file_incorrect");
				}
				Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
				Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

				String starttimeTemp = request.getParameter("starttime");
				String endtimeTemp = request.getParameter("endtime");
				String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
				int starttime = Common.empty(starttimeTemp) ? 0 : Common.strToTime(starttimeTemp, timeoffset,
						"yyyy-MM-dd HH:mm:ss");
				int endtime = Common.empty(endtimeTemp) ? 0 : Common.strToTime(endtimeTemp, timeoffset,
						"yyyy-MM-dd HH:mm:ss");

				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("name", Common.htmlSpecialChars(Common.trim(request.getParameter("name"))));
				setData.put("note", Common.trim(request.getParameter("note")));
				setData.put("filename", fileName);
				setData.put("image", Common.trim(request.getParameter("image")));
				setData.put("available", Common.intval(request.getParameter("available")));
				setData.put("starttime", starttime);
				setData.put("endtime", endtime);
				String nexttype = Common.trim(request.getParameter("nexttype"));
				setData.put("nexttype", nexttype);
				setData.put("nexttime", "time".equals(nexttype) ? Common.intval(request
						.getParameter("nexttime")) : 0);
				setData.put("credit", Common.intval(request.getParameter("credit")));
				setData.put("maxnum", Common.intval(request.getParameter("maxnum")));
				setData.put("displayorder", Common.range(request.getParameter("displayorder"), 65535, 0));
				if (taskid == 0) {
					dataBaseService.insertTable("task", setData, false, false);
				} else {
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("taskid", taskid);
					dataBaseService.updateTable("task", setData, whereData);
				}
				cacheService.task_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=task");
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		Map<String, Object> thevalue = null;
		String op = request.getParameter("op");
		if ("edit".equals(op)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("task") + " WHERE taskid='" + taskid + "'");
			if (query.size() > 0) {
				thevalue = query.get(0);
				int startime = (Integer) thevalue.get("starttime");
				thevalue.put("starttime", startime > 0 ? Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss",
						startime) : "");
				int endtime = (Integer) thevalue.get("endtime");
				thevalue.put("endtime", endtime > 0 ? Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss", endtime)
						: "");
			}
		} else if ("add".equals(op)) {
			thevalue = new HashMap<String, Object>();
			thevalue.put("taskid", 0);
			thevalue.put("available", 1);
			thevalue.put("nexttime", 0);
			thevalue.put("credit", 0);

		} else if ("delete".equals(op)) {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("task")
					+ " WHERE taskid='" + taskid + "'");
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("usertask")
					+ " WHERE taskid='" + taskid + "'");
			try {
				cacheService.task_cache();
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=task");
		} else {
			List<Map<String, Object>> tasks = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("task") + " ORDER BY displayorder");
			for (Map<String, Object> task : tasks) {
				int starttime = (Integer) task.get("starttime");
				task.put("starttime", starttime > 0 ? Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss",
						starttime) : "N/A");
				int endtime = (Integer) task.get("endtime");
				task.put("endtime", endtime > 0 ? Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss", endtime)
						: "N/A");
				String image = (String) task.get("image");
				if (Common.empty(image)) {
					task.put("image", "image/task.gif");
				}
			}
			request.setAttribute("actives_view", " class=\"active\"");
			request.setAttribute("tasks", tasks);
		}

		if (thevalue != null) {
			request.setAttribute("nexttypes_" + thevalue.get("nexttype"), " selected");
			request.setAttribute("nextimestyle", "time".equals(thevalue.get("nexttype")) ? "" : "none");
			request.setAttribute("availables_" + thevalue.get("available"), " checked");
			request.setAttribute("thevalue", thevalue);
		}

		request.setAttribute("taskid", taskid);
		request.setAttribute("op", op);
		return mapping.findForward("task");
	}
}