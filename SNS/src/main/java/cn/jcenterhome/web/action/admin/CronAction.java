package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class CronAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managecron")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		int cronid = Common.intval(request.getParameter("cronid"));
		try {
			if (submitCheck(request, "cronsubmit")) {
				String name = Common.htmlSpecialChars(request.getParameter("name"));

				String fileName = Common.trim(request.getParameter("filename"));
				fileName = fileName.replace("..", "").replace("/", "").replace("\\", "").trim();
				File file = new File(JavaCenterHome.jchRoot + "./source/cron/" + fileName);
				if (Common.empty(fileName) || !file.isFile() || !file.canRead()) {
					return cpMessage(request, mapping, "cp_designated_script_file_incorrect");
				}

				String weekday = request.getParameter("weekday");
				String day = request.getParameter("day");
				if (!"-1".equals(weekday)) {
					day = "-1";
				}

				String[] minute = request.getParameterValues("minute");
				String postminute = "";
				if (minute != null) {
					Map<String, String> tempMap = new HashMap<String, String>();
					int tempI = 0;
					for (String subM : minute) {
						tempI = Integer.parseInt(subM);
						if (tempI > -1 && tempI < 60) {
							tempMap.put(subM, "");
						}
					}
					tempI = tempMap.size();
					if (tempI > 0) {
						minute = new String[tempI];
						Set<String> keySet = tempMap.keySet();
						Iterator<String> iterator = keySet.iterator();
						tempI = 0;
						while (iterator.hasNext()) {
							minute[tempI++] = iterator.next();
						}
						Arrays.sort(minute);
						StringBuilder builder = new StringBuilder();
						for (String tempS : minute) {
							builder.append(tempS);
							builder.append("\t");
						}
						postminute = builder.substring(0, builder.length() - 1);
					}
				}

				String hour = request.getParameter("hour");
				if ("-1".equals(weekday) && "-1".equals(day) && "-1".equals(hour) && "".equals(postminute)) {
					return cpMessage(request, mapping, "cp_implementation_cycle_incorrect_script");
				}

				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("name", name);
				setData.put("filename", fileName);
				setData.put("available", request.getParameter("available"));
				setData.put("weekday", weekday);
				setData.put("day", day);
				setData.put("hour", hour);
				setData.put("minute", postminute);

				if (Common.empty(cronid)) {
					setData.put("type", "user");
					Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
					setData.put("nextrun", String.valueOf(sGlobal.get("timestamp")));
					setData.put("cronid", dataBaseService.insertTable("cron", setData, true, false));
				} else {
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("cronid", cronid);
					dataBaseService.updateTable("cron", setData, whereData);
					setData.put("cronid", cronid);
				}
				cronService.cronNextRun(request, setData);
				cronService.cron_config(request);
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=cron");
			}
		} catch (Exception exception) {
			return showMessage(request, response, exception.getMessage());
		}
		Map<String, Object> thevalue = null;
		DecimalFormat format = new DecimalFormat("00");
		String op = request.getParameter("op");
		if ("edit".equals(op)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("cron") + " WHERE cronid='" + cronid + "'");
			if (query.size() > 0) {
				thevalue = query.get(0);
			}
		} else if ("add".equals(op)) {
			thevalue = new HashMap<String, Object>();
			thevalue.put("week", -1);
			thevalue.put("hour", -1);
			thevalue.put("day", -1);
			thevalue.put("minute", "0");
			thevalue.put("available", 1);

		} else if ("delete".equals(op)) {
			dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("cron") + " WHERE cronid='"
					+ cronid + "' AND type='user'");
			try {
				cronService.cron_config(request);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=cron");
		} else if ("run".equals(op)) {
			try {
				cronService.runCron(request, response, cronid);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=cron");
		} else {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("cron") + " ORDER BY type DESC");
			String[] tempArray = {"weekday", "day", "hour", "minute"};
			String[] minuteArray = null;
			String tempS;
			for (Map<String, Object> cron : query) {
				for (String key : tempArray) {
					tempS = String.valueOf(cron.get(key)).trim();
					if (tempS.equals("") || tempS.equals("-1")) {
						cron.put(key, "*");
					} else if (key.equals("weekday")) {
						cron.put(key, 1 + (Integer) cron.get(key));
					} else if (key.equals("minute")) {
						minuteArray = ((String) cron.get(key)).split("\t");
						for (int i = 0; i < minuteArray.length; i++) {
							minuteArray[i] = format.format(Long.parseLong(minuteArray[i]));
						}
						StringBuilder buf = new StringBuilder();
						for (int i = 0; i < minuteArray.length; i++) {
							if (i != 0)
								buf.append(", ");
							buf.append(minuteArray[i]);
						}
						cron.put(key, buf.toString());
					}
				}
				int lastrun = (Integer) cron.get("lastrun");
				if (lastrun == 0) {
					cron.put("lastrun", "N/A");
				} else {
					cron.put("lastrun", Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss", lastrun));
				}

				int nextrun = (Integer) cron.get("nextrun");
				if (nextrun == 0 || (Integer) cron.get("available") == 0) {
					cron.put("nextrun", "N/A");
				} else {
					cron.put("nextrun", Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss", nextrun));
				}
				list.add(cron);
			}
			Map<String, String> actives = new HashMap<String, String>();
			actives.put("view", " class=\"active\"");
			request.setAttribute("actives", actives);
			request.setAttribute("list", list);
		}

		if (thevalue != null) {
			StringBuilder daystr = new StringBuilder();
			StringBuilder hourstr = new StringBuilder();
			StringBuilder minuteselect = new StringBuilder();
			String selstr;

			Object tempO = thevalue.get("weekday");
			if (tempO != null) {
				Map<String, String> weekdays = new HashMap<String, String>();
				weekdays.put(String.valueOf(tempO), " selected");
				request.setAttribute("weekdays", weekdays);
			}
			for (int i = 1; i < 32; i++) {
				selstr = (Integer) thevalue.get("day") == i ? " selected" : "";
				daystr.append("<option value=\"");
				daystr.append(i);
				daystr.append("\"");
				daystr.append(selstr);
				daystr.append(">");
				daystr.append(i);
				daystr.append("</option>");
			}
			for (int i = 1; i < 24; i++) {
				selstr = (Integer) thevalue.get("hour") == i ? " selected" : "";
				hourstr.append("<option value=\"");
				hourstr.append(i);
				hourstr.append("\"");
				hourstr.append(selstr);
				hourstr.append(">");
				hourstr.append(i);
				hourstr.append("</option>");
			}
			String[] cronminutearr = ((String) thevalue.get("minute")).trim().split("\t");
			String selected;
			for (int i = 0; i < 12; i++) {
				minuteselect.append("<select name=\"minute\"><option value=\"-1\">*</option>");
				for (int j = 0; j < 60; j++) {
					selected = "";
					if (cronminutearr.length - 1 >= i && Integer.parseInt(cronminutearr[i]) == j) {
						selected = " selected";
					}
					minuteselect.append("<option value=\"");
					minuteselect.append(j);
					minuteselect.append("\"");
					minuteselect.append(selected);
					minuteselect.append(">");
					minuteselect.append(format.format(j));
					minuteselect.append("</option>");
				}
				minuteselect.append("</select>");
				if (i == 5) {
					minuteselect.append("<br>");
				} else {
					minuteselect.append(" ");
				}
			}

			Map<String, String> availables = new HashMap<String, String>();
			availables.put(String.valueOf(thevalue.get("available")), " checked");

			request.setAttribute("thevalue", thevalue);
			request.setAttribute("daystr", daystr.toString());
			request.setAttribute("hourstr", hourstr.toString());
			request.setAttribute("minuteselect", minuteselect.toString());
			request.setAttribute("availables", availables);
		}

		request.setAttribute("op", op);
		request.setAttribute("cronid", cronid);
		return mapping.findForward("cron");
	}
}