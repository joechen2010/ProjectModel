package cn.jcenterhome.web.action.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.web.action.BaseAction;


public class UserGroupAction extends BaseAction {
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageusergroup")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		Map<String, Object> theValue = null;
		int gid = Common.intval(request.getParameter("gid"));
		if (gid > 0) {
			String sql = "SELECT * FROM " + JavaCenterHome.getTableName("usergroup") + " WHERE gid=" + gid;
			List<Map<String, Object>> values = dataBaseService.executeQuery(sql);
			if (values.isEmpty()) {
				return cpMessage(request, mapping, "cp_user_group_does_not_exist");
			}
			theValue = values.get(0);
			theValue.put("magicaward", Serializer.unserialize((String) theValue.get("magicaward"), false));
		} else {
			theValue = new HashMap<String, Object>();
		}

		try {
			if (submitCheck(request, "thevaluesubmit")) {
				String groupTitle = (String) Common.sHtmlSpecialChars(request.getParameter("set[grouptitle]")
						.trim());
				if (Common.empty(groupTitle)) {
					return cpMessage(request, mapping, "cp_user_group_were_not_empty");
				}

				Map<String, Object> setArr = new HashMap<String, Object>();
				setArr.put("grouptitle", Common.cutstr(groupTitle, 20, ""));
				Integer system = (Integer) theValue.get("system");
				if (system == null) {
					system = Common.intval(request.getParameter("set[system]"));
				}
				setArr.put("system", system);
				if (system == 0) {
					int explower = Common.intval(request.getParameter("set[explower]"));
					if (explower > 999999999 || explower < -999999999) {
						return cpMessage(request, mapping, "cp_integral_limit_error");
					}

					String sql = "SELECT gid FROM " + JavaCenterHome.getTableName("usergroup")
							+ " WHERE explower = " + explower + " AND system = 0";
					List<String> lowGidList = dataBaseService.executeQuery(sql, 1);
					if (!lowGidList.isEmpty()) {
						int lowGid = Common.intval(lowGidList.get(0));
						if (lowGid != gid) {
							return cpMessage(request, mapping,
									"cp_integral_limit_duplication_with_other_user_group");
						}
					}
					setArr.put("explower", explower);
				}
				Map<String, Object> magicAwards = new HashMap<String, Object>();
				String[] magicValues = request.getParameterValues("magicaward[]");
				if (magicValues != null) {
					Map<String, Object> tmp = null;
					String magic[] = null;
					int magicNum = 0;
					for (String value : magicValues) {
						tmp = new HashMap<String, Object>();
						magic = value.split(",");
						if ((magicNum = Common.intval(magic[1])) > 0) {
							tmp.put("mid", magic[0]);
							tmp.put("num", magicNum);
							magicAwards.put(magic[0], tmp);
						}
					}
				}
				setArr.put("magicaward", Serializer.serialize(magicAwards));

				String[] ignoreNames = {"set[gid]", "set[grouptitle]", "set[system]", "set[explower]"};
				Map<String, String[]> elements = request.getParameterMap();
				Set<String> elementNames = elements.keySet();

				String key = null;
				String value = null;
				for (String elementName : elementNames) {
					if (!elementName.startsWith("set[") || Common.in_array(ignoreNames, elementName)) {
						continue;
					}
					key = elementName.replaceAll("(set\\[)|(\\])", "");
					value = elements.get(elementName)[0].trim();
					if (!value.equals(theValue.get(key))) {
						if ("maxfriendnum".equals(key) || "postinterval".equals(key)
								|| "searchinterval".equals(key) || "domainlength".equals(key)) {
							setArr.put(key, Common.range(value, 65535, 0));
						} else if ("maxattachsize".equals(key)) {
							setArr.put(key, Common.range(value, 2147483647, 0));
						} else if ("color".equals(key)) {
							setArr.put(key, value.matches("^#?\\w{0,10}") ? value : "");
						} else if ("icon".equals(key)) {
							setArr.put(key, Common.cutstr((String) Common.sHtmlSpecialChars(value), 100, ""));
						} else {
							setArr.put(key, value);
						}
					}

				}
				if (Common.empty(theValue.get("gid"))) {
					Object[] keys = setArr.keySet().toArray();
					Object[] values = setArr.values().toArray();
					String sql = "INSERT INTO " + JavaCenterHome.getTableName("usergroup") + " ("
							+ Common.implode(keys, ",") + ") VALUES ('" + Common.implode(values, "','")
							+ "')";
					dataBaseService.insert(sql);
				} else {
					Object[] keys = setArr.keySet().toArray();
					Object[] values = setArr.values().toArray();
					StringBuffer setSql = new StringBuffer();
					String comma = "";
					for (int i = 0; i < keys.length; i++) {
						setSql.append(comma + keys[i] + "='" + values[i] + "'");
						comma = ",";
					}
					String sql = "UPDATE " + JavaCenterHome.getTableName("usergroup") + " SET " + setSql
							+ " WHERE gid='" + theValue.get("gid") + "'";
					dataBaseService.executeUpdate(sql);
				}
				updateGroupExplower();
				cacheService.usergroup_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=usergroup");
			} else if (submitCheck(request, "updatesubmit")) {
				String[] gids = request.getParameterValues("gid[]");
				if (gids != null) {
					Map<Integer, Integer> explowers = new TreeMap<Integer, Integer>();
					for (String id : gids) {
						int explower = Common.intval(request.getParameter("explower_" + id));
						if (explower > 999999999 || explower < -999999999) {
							return cpMessage(request, mapping, "cp_integral_limit_error");
						} else if (explowers.containsValue(explower)) {
							return cpMessage(request, mapping,
									"cp_integral_limit_duplication_with_other_user_group");
						}
						explowers.put(Common.intval(id), explower);
					}
					String sql = "SELECT gid,explower FROM " + JavaCenterHome.getTableName("usergroup")
							+ " WHERE system = 0";
					List<Map<String, Object>> values = dataBaseService.executeQuery(sql);
					for (Map<String, Object> value : values) {
						int id = (Integer) value.get("gid");
						if (value.get("explower").equals(explowers.get(id))) {
							explowers.remove(id);
						}
					}
					if (!explowers.isEmpty()) {
						Set<Integer> ids = explowers.keySet();
						for (Integer id : ids) {
							sql = "UPDATE " + JavaCenterHome.getTableName("usergroup") + " SET explower='"
									+ explowers.get(id) + "' WHERE gid=" + id;
							dataBaseService.executeUpdate(sql);
						}
						cacheService.usergroup_cache();
					}
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=usergroup");
			} else if (submitCheck(request, "copysubmit")) {
				theValue.remove("grouptitle");
				theValue.remove("gid");
				theValue.remove("explower");
				theValue.put("magicaward", Serializer.serialize(theValue.get("magicaward")));
				Map<String, Object> copyValue = (Map) Common.sAddSlashes(theValue);
				String[] aimGroups = request.getParameterValues("aimgroup");
				if (aimGroups != null) {
					for (int i = 0; i < aimGroups.length; i++) {
						Set<String> keys = copyValue.keySet();
						StringBuffer sql = new StringBuffer();
						sql.append("UPDATE " + JavaCenterHome.getTableName("usergroup") + " SET ");
						String comma = "";
						for (String key : keys) {
							sql.append(comma + key + "='" + copyValue.get(key) + "'");
							comma = ",";
						}
						sql.append(" WHERE gid = " + aimGroups[i]);
						dataBaseService.executeUpdate(sql.toString());
					}
					cacheService.usergroup_cache();
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=usergroup");
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		String op = request.getParameter("op");
		if (Common.empty(op)) {
			String sql = "SELECT * FROM " + JavaCenterHome.getTableName("usergroup") + " ORDER BY explower";
			List<Map<String, Object>> values = dataBaseService.executeQuery(sql);
			Map<String, List<Object>> list = new HashMap<String, List<Object>>();
			String system = null;
			List<Object> temp = null;
			for (Map<String, Object> value : values) {
				value.put("color", Common.getColor(value));
				value.put("icon", Common.getIcon(value));
				system = String.valueOf(value.get("system"));
				temp = list.get(system);
				if (temp == null) {
					temp = new ArrayList<Object>();
					list.put(system, temp);
				}
				temp.add(value);
			}

			request.setAttribute("actives_view", " class='active'");
			request.setAttribute("list", list);
		} else if ("copy".equals(op)) {
			String sql = "SELECT gid, grouptitle FROM " + JavaCenterHome.getTableName("usergroup")
					+ " WHERE gid <> " + theValue.get("gid") + " AND system = " + theValue.get("system")
					+ " ORDER BY explower";
			List<Map<String, Object>> userGroups = dataBaseService.executeQuery(sql);
			request.setAttribute("userGroups", userGroups);
		} else if ("delete".equals(op) && !theValue.isEmpty()) {
			int system = (Integer) theValue.get("system");
			if (system == 0) {
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("usergroup")
						+ " WHERE gid = " + gid);
				updateGroupExplower();
			} else if (system == 1) {
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("usergroup")
						+ " WHERE gid = " + gid);
			} else {
				return cpMessage(request, mapping, "cp_system_user_group_could_not_be_deleted");
			}
			String sql = "UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET groupid = 0 WHERE groupid = " + gid;
			dataBaseService.executeUpdate(sql);

			try {
				cacheService.usergroup_cache();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=usergroup");
		} else {
			if ("add".equals(op)) {
				theValue = new HashMap<String, Object>();
				theValue.put("gid", 0);
				theValue.put("explower", 0);
				theValue.put("maxattachsize", 10);
				theValue.put("maxfriendnum", 50);
				theValue.put("postinterval", 60);
				theValue.put("searchinterval", 60);
				theValue.put("domainlength", 0);
			}
			Common.getCacheDate(request, response, "/data/cache/cache_magic.jsp", "globalMagic");
			request.setAttribute("discount", getDiscount());
		}
		request.setAttribute("thevalue", theValue);
		return mapping.findForward("usergroup");
	}

	
	private Map<Integer, String> getDiscount() {
		Map<Integer, String> discount = new LinkedHashMap<Integer, String>();
		discount.put(0, "╡╩╢Руш");
		discount.put(1, "1уш");
		discount.put(2, "2уш");
		discount.put(3, "3уш");
		discount.put(4, "4уш");
		discount.put(5, "5уш");
		discount.put(6, "6уш");
		discount.put(7, "7уш");
		discount.put(8, "8уш");
		discount.put(9, "9уш");
		discount.put(-1, "цБ╥я");
		return discount;
	}

	
	private void updateGroupExplower() {
		String sql = "SELECT gid FROM " + JavaCenterHome.getTableName("usergroup")
				+ " WHERE system = 0 ORDER BY explower LIMIT 1";
		List<String> gids = dataBaseService.executeQuery(sql, 1);
		if (!gids.isEmpty()) {
			sql = "UPDATE " + JavaCenterHome.getTableName("usergroup")
					+ " SET explower = -999999999 WHERE gid = " + gids.get(0);
			dataBaseService.executeUpdate(sql);
		}
	}

}