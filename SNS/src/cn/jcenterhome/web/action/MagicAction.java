package cn.jcenterhome.web.action;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.CookieHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.vo.MessageVO;

public class MagicAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		String mid = request.getParameter("mid");
		mid = Common.empty(mid) ? "" : mid.trim();

		request.setAttribute("mid", mid);

		String op = request.getParameter("op");
		op = Common.empty(op) ? "use" : op;
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if (Common.empty(supe_uid)) {
			return showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
		}
		String message = Common.checkClose(request, response, supe_uid);
		if (message != null) {
			return showMessage(request, response, message);
		}
		if (Common.empty(mid)) {
			return showMessage(request, response, "unknown_magic");
		}
		Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, supe_uid);
		if (Common.empty(space)) {
			return showMessage(request, response, "space_does_not_exist");
		}
		Object result = magicService.magic_get(mid);
		if (result instanceof MessageVO) {
			return showMessage(request, response, (MessageVO) result);
		}
		Map<String, Object> magic = (Map<String, Object>) result;
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("usermagic") + " WHERE uid='" + supe_uid + "' AND mid='" + mid
				+ "'");
		Map<String, Object> usermagic = query.size() > 0 ? query.get(0) : new HashMap<String, Object>();
		int magicCount;
		if (Common.empty(usermagic) || Common.empty(usermagic.get("count"))) {
			op = "buy";
			magicCount = 0;
		} else {
			magicCount = (Integer) usermagic.get("count");
			request.setAttribute("usermagic", usermagic);
		}
		boolean frombuy = false;
		boolean buysubmitB = false;
		try {
			buysubmitB = submitCheck(request, "buysubmit");
		} catch (Exception e1) {
			e1.printStackTrace();
			return showMessage(request, response, e1.getMessage());
		}
		if (buysubmitB) {
			result = magicService.magic_buy_get(request, response, magic, sGlobal, space);
			if (result instanceof MessageVO) {
				return showMessage(request, response, (MessageVO) result);
			}
			Map<String, Object> datas = (Map<String, Object>) result;
			Map<String, Object> magicstore = (Map<String, Object>) datas.get("magicstore");
			Map<String, Object> coupon = (Map<String, Object>) datas.get("coupon");
			int discount = (Integer) datas.get("discount");
			int charge = (Integer) datas.get("charge");

			result = magicService
					.magic_buy_post(request, response, sGlobal, space, magic, magicstore, coupon);
			if (result instanceof MessageVO) {
				return showMessage(request, response, (MessageVO) result);
			}

			op = "use";
			frombuy = true;
			String buynum = request.getParameter("buynum");
			if (buynum != null) {
				usermagic.put("count", magicCount + Common.intval(buynum));
			}
			request.setAttribute("usermagic", usermagic);
		}

		request.setAttribute("frombuy", frombuy);
		request.setAttribute("op", op);
		request.setAttribute("magic", magic);
		request.setAttribute("space", space);
		String idtype = request.getParameter("idtype");
		idtype = Common.empty(idtype) ? "" : idtype.trim();
		String idS = request.getParameter("id");
		int id = Common.empty(idS) ? 0 : Common.intval(idS);
		request.setAttribute("id", id);
		request.setAttribute("idtype", idtype);
		if ("buy".equals(op)) {
			result = magicService.magic_buy_get(request, response, magic, sGlobal, space);
			if (result instanceof MessageVO) {
				return showMessage(request, response, (MessageVO) result);
			}
			Map<String, Object> datas = (Map<String, Object>) result;
			Map<String, Object> magicstore = (Map<String, Object>) datas.get("magicstore");
			Map<String, Object> coupon = (Map<String, Object>) datas.get("coupon");
			int discount = (Integer) datas.get("discount");
			int charge = (Integer) datas.get("charge");

			request.setAttribute("magicstore", magicstore);
			request.setAttribute("coupon", coupon);
			request.setAttribute("discount", discount);
			request.setAttribute("charge", charge);
			String extra = "";
			if ("doodle".equals(mid)) {
				StringBuilder builder = new StringBuilder();
				builder.append("&showid=");
				String tempS = request.getParameter("showid");
				if (tempS != null) {
					builder.append(tempS);
				}
				builder.append("&target=");
				tempS = request.getParameter("target");
				if (tempS != null) {
					builder.append(tempS);
				}
				builder.append("&from=");
				tempS = request.getParameter("from");
				if (tempS != null) {
					builder.append(tempS);
				}
				extra = builder.toString();
			}
			request.setAttribute("extra", extra);
			return include(request, response, sConfig, sGlobal, "cp_magic.jsp");
		}
		int useperoid = (Integer) magic.get("useperoid");
		if (useperoid > 0) {
			int time = (Integer) sGlobal.get("timestamp") - useperoid;
			List<Map<String, Object>> tempML = dataBaseService.executeQuery("SELECT COUNT(*) AS cout FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid='" + supe_uid + "' AND mid='"
					+ mid + "' AND dateline > '" + time + "'");
			int count = 0;
			if (tempML.size() > 0) {
				count = (Integer) tempML.get(0).get("cout");
			}
			if (count >= (Integer) magic.get("usecount")) {
				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid='" + supe_uid
						+ "' AND mid='" + mid + "' AND dateline > '" + time + "' ORDER BY dateline LIMIT 1");
				Map<String, Object> value = null;
				if (query != null && query.size() > 0) {
					value = query.get(0);
				}
				String nexttime = "";
				if (value != null) {
					nexttime = Common.sgmdate(request, "MM-dd HH:mm:ss", (Integer) value.get("dateline")
							+ (Integer) magic.get("useperoid"));
				}
				return showMessage(request, response, "magic_usecount_limit", null, 1, nexttime);
			}
		}
		String magicName = "magic_" + mid;
		boolean exit = false;
		try {
			exit = !accessMagic(magicName, request, response);
		} catch (Exception e) {
			e.printStackTrace();
			return showMessage(request, response, e.getMessage());
		}
		if (exit) {
			return null;
		}
		return include(request, response, sConfig, sGlobal, magicName + ".jsp");
	}

	
	private boolean accessMagic(String methodName, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Method method = this.getClass().getDeclaredMethod(methodName, HttpServletRequest.class,
				HttpServletResponse.class);
		return (Boolean) method.invoke(this, request, response);
	}

	
	private boolean magic_anonymous(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String idtype = (String) request.getAttribute("idtype");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if ("uid".equals(idtype)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("visitor") + " WHERE uid = '" + id + "' AND vuid = '"
					+ supe_uid + "'");
			Map<String, Object> value = null;
			if (query.size() > 0) {
				value = query.get(0);
			}
			if (Common.empty(value)) {
				showMessage(request, response, "magicuse_bad_object");
				return false;
			} else if ("".equals(value.get("vusername"))) {
				showMessage(request, response, "magicuse_object_once_limit");
				return false;
			}
		} else if ("cid".equals(idtype)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("comment") + " WHERE cid = '" + id + "' AND authorid = '"
					+ supe_uid + "'");
			Map<String, Object> value = null;
			if (query.size() > 0) {
				value = query.get(0);
			}
			if (Common.empty(value)) {
				showMessage(request, response, "magicuse_bad_object");
				return false;
			} else if ("".equals(value.get("author"))) {
				showMessage(request, response, "magicuse_object_once_limit");
				return false;
			}
		} else {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("clickuser") + " WHERE id = '" + id + "' AND idtype = '"
					+ idtype + "' AND uid = '" + supe_uid + "'");
			Map<String, Object> value = null;
			if (query.size() > 0) {
				value = query.get(0);
			}
			if (Common.empty(value)) {
				showMessage(request, response, "magicuse_bad_object");
				return false;
			} else if ("".equals(value.get("username"))) {
				showMessage(request, response, "magicuse_object_once_limit");
				return false;
			}
		}
		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int second = 1;
			if ("uid".equals(idtype)) {
				CookieHelper.setCookie(request, response, "anonymous_visit_" + supe_uid + "_" + id, "1");
				Map<String, Object> setSQLArr = new HashMap<String, Object>();
				setSQLArr.put("vusername", "");
				Map<String, Object> whereSQLArr = new HashMap<String, Object>();
				whereSQLArr.put("uid", id);
				whereSQLArr.put("vuid", supe_uid);
				dataBaseService.updateTable("visitor", setSQLArr, whereSQLArr);
				second = 0;
			} else if ("cid".equals(idtype)) {
				Map<String, Object> setSQLArr = new HashMap<String, Object>();
				setSQLArr.put("author", "");
				Map<String, Object> whereSQLArr = new HashMap<String, Object>();
				whereSQLArr.put("cid", id);
				whereSQLArr.put("authorid", supe_uid);
				dataBaseService.updateTable("comment", setSQLArr, whereSQLArr);
			} else {
				Map<String, Object> setSQLArr = new HashMap<String, Object>();
				setSQLArr.put("username", "");
				Map<String, Object> whereSQLArr = new HashMap<String, Object>();
				whereSQLArr.put("id", id);
				whereSQLArr.put("idtype", idtype);
				whereSQLArr.put("uid", supe_uid);
				dataBaseService.updateTable("clickuser", setSQLArr, whereSQLArr);
			}
			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), second);
			return false;
		}

		return true;
	}

	
	private boolean magic_attachsize(HttpServletRequest request, HttpServletResponse response) {
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int rate = operateMagicCustomInfo(request, "addsize", 10);
			int addsize = 1048576 * rate;
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET addsize = addsize + " + addsize + " WHERE uid='" + sGlobal.get("supe_uid") + "'");

			magicService.magic_use(sGlobal, mid, new HashMap<String, Object>(), true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}
		return true;
	}

	
	private boolean magic_bgimage(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		String idtype = "blogid";
		Object result = magicService.magic_check_idtype(sGlobal, id, idtype);
		if (result instanceof MessageVO) {
			showMessage(request, response, (MessageVO) result);
			return false;
		}

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int paper = Common.intval(request.getParameter("paper"));
			Map<String, Object> setSQLArr = new HashMap<String, Object>();
			setSQLArr.put("magicpaper", paper);
			Map<String, Object> whereSQLArr = new HashMap<String, Object>();
			whereSQLArr.put("blogid", id);
			dataBaseService.updateTable("blogfield", setSQLArr, whereSQLArr);

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}
		return true;
	}

	
	private boolean magic_call(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String idtype = (String) request.getAttribute("idtype");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("blogid", "blog");
		mapping.put("tid", "thread");
		mapping.put("eventid", "event");
		if (mapping.get(idtype) == null) {
			showMessage(request, response, "magicuse_bad_object");
			return false;
		}
		Object result = magicService.magic_check_idtype(sGlobal, id, idtype);
		if (result instanceof MessageVO) {
			showMessage(request, response, (MessageVO) result);
			return false;
		}

		int custom_maxcall = operateMagicCustomInfo(request, "maxcall", 10);

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}

		if (sc) {
			int supe_uid = (Integer) sGlobal.get("supe_uid");
			String name = mapping.get(idtype);
			List<Integer> ids = new ArrayList<Integer>();
			List<String> note_inserts = new ArrayList<String>();
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + supe_uid
					+ "' AND fusername IN (" + Common.sImplode(request.getParameterValues("fusername[]"))
					+ ") LIMIT " + custom_maxcall);

			String note = Common.getMessage(request, "cp_magic_call", Common.getMessage(request, name),
					"space.jsp?uid=" + supe_uid + "&do=" + name + "&id=" + id);
			Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			String supe_username = (String) sGlobal.get("supe_username");
			int timestamp = (Integer) sGlobal.get("timestamp");
			StringBuilder builder = new StringBuilder();
			for (Map<String, Object> value : query) {
				int fuid = (Integer) value.get("fuid");
				Common.realname_set(sGlobal, sConfig, sNames, fuid, (String) value.get("fusername"), "", 0);
				ids.add(fuid);
				builder.append("('");
				builder.append(fuid);
				builder.append("', '");
				builder.append(name);
				builder.append("', '1', '");
				builder.append(supe_uid);
				builder.append("', '");
				builder.append(supe_username);
				builder.append("', '");
				builder.append(note);
				builder.append("', '");
				builder.append(timestamp);
				builder.append("')");
				note_inserts.add(builder.toString());
				builder.delete(0, builder.length());
			}
			if (Common.empty(ids)) {
				showMessage(request, response, "magicuse_has_no_valid_friend");
				return false;
			}
			request.setAttribute("list", query);
			dataBaseService.execute("INSERT INTO " + JavaCenterHome.getTableName("notification")
					+ "(uid, type, new, authorid, author, note, dateline) VALUES "
					+ Common.implode(note_inserts, ","));
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET notenum = notenum + 1 WHERE uid IN (" + Common.sImplode(ids) + ")");

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);

			Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
			Common.realname_get(sGlobal, sConfig, sNames, space);

			request.setAttribute("op", "show");
		}
		return true;
	}

	
	private boolean magic_color(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String idtype = (String) request.getAttribute("idtype");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("blogid", "blogfield");
		mapping.put("tid", "thread");
		if (mapping.get(idtype) == null) {
			showMessage(request, response, "magicuse_bad_object");
			return false;
		}
		Object result = magicService.magic_check_idtype(sGlobal, id, idtype);
		if (result instanceof MessageVO) {
			showMessage(request, response, (MessageVO) result);
			return false;
		}

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int supe_uid = (Integer) sGlobal.get("supe_uid");
			String tablename = mapping.get(idtype);
			int color = Common.intval(request.getParameter("color"));
			Map<String, Object> setSQLArr = new HashMap<String, Object>();
			Map<String, Object> whereSQLArr = new HashMap<String, Object>();
			setSQLArr.put("magiccolor", color);
			whereSQLArr.put(idtype, id);
			whereSQLArr.put("uid", supe_uid);
			dataBaseService.updateTable(tablename, setSQLArr, whereSQLArr);
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("feed") + " WHERE id='" + id + "' AND idtype='" + idtype
					+ "' AND uid='" + supe_uid + "'");
			Map<String, Object> feed = query.size() > 0 ? query.get(0) : null;

			if (feed != null) {
				Map temp = Serializer.unserialize((String) feed.get("body_data"), false);
				if (!Common.isArray(temp)) {
					temp = new HashMap();
				}
				temp.put("magic_color", color);
				String body_data = Serializer.serialize(temp);
				feed.put("body_data", body_data);
				setSQLArr.clear();
				setSQLArr.put("body_data", body_data);
				whereSQLArr.clear();
				whereSQLArr.put("feedid", feed.get("feedid"));
				dataBaseService.updateTable("feed", setSQLArr, whereSQLArr);
			}

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}
		return true;
	}

	
	private boolean magic_detector(HttpServletRequest request, HttpServletResponse response) {
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		int custom_maxdetect = operateMagicCustomInfo(request, "maxdetect", 10);
		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			magicService.magic_use(sGlobal, mid, new HashMap<String, Object>(), true);

			request.setAttribute("op", "show");

			int limit = custom_maxdetect + 20;
			int supe_uid = (Integer) sGlobal.get("supe_uid");
			Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");

			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid != '" + supe_uid
					+ "' AND mid = 'gift' LIMIT " + limit);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			int max = 1;
			Map<String, Object> dataValue = null;
			int left = 0;
			Object tempO = null;
			Map<Integer, Integer> receiver = null;
			for (Map<String, Object> value : query) {
				left = 0;
				tempO = null;
				dataValue = Serializer.unserialize((String) value.get("data"), false);
				value.put("data", dataValue);

				tempO = dataValue.get("left");
				if (tempO != null) {
					left = (Integer) (tempO);
				}
				receiver = (Map<Integer, Integer>) dataValue.get("receiver");
				if (left != 0 && (Common.empty(receiver) || !mapWithValue(receiver, supe_uid))) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					list.add(value);
					if (++max > custom_maxdetect) {
						break;
					}
				}
			}
			request.setAttribute("list", list);
			Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
			Common.realname_get(sGlobal, sConfig, sNames, space);
		}

		return true;
	}

	
	private boolean magic_doodle(HttpServletRequest request, HttpServletResponse response) {

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {

			request.setAttribute("op", "show");
		}
		return true;
	}

	
	private boolean magic_downdateline(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String idtype = (String) request.getAttribute("idtype");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Object result = magicService.magic_check_idtype(sGlobal, id, idtype);
		if (result instanceof MessageVO) {
			showMessage(request, response, (MessageVO) result);
			return false;
		}
		Map<String, Object> blog = (Map<String, Object>) result;
		request.setAttribute("blog", blog);

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			String newdatelineString = request.getParameter("newdateline");
			int newdateline = 0;
			Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
			String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
			if (newdatelineString == null
					|| (newdateline = Common.strToTime(newdatelineString, timeoffset, "yyyy-MM-dd HH:mm")) < Common
							.strToTime("1970-1-1", timeoffset)
					|| newdateline > (Integer) blog.get("dateline")) {
				showMessage(request, response, "magicuse_bad_dateline");
				return false;
			}
			String tablename = cpService.getTablebyIdType(idtype);
			int supe_uid = (Integer) sGlobal.get("supe_uid");
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName(tablename) + " SET dateline='"
					+ newdateline + "' WHERE " + idtype + "='" + id + "' AND uid='" + supe_uid + "'");
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("feed") + " SET dateline='"
					+ newdateline + "' WHERE id='" + id + "' AND idtype='" + idtype + "' AND uid='"
					+ supe_uid + "'");

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}

		return true;
	}

	
	private boolean magic_flicker(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		int supe_uid = (Integer) sGlobal.get("supe_uid");
		String idtype = "cid";
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("comment") + " WHERE cid = '" + id + "' AND authorid = '"
				+ supe_uid + "'");
		Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
		if (Common.empty(value)) {
			showMessage(request, response, "magicuse_bad_object");
			return false;
		} else if ((Integer) value.get("magicflicker") == 1) {
			showMessage(request, response, "magicuse_object_once_limit");
			return false;
		}

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			Map<String, Object> setSQLArr = new HashMap<String, Object>();
			setSQLArr.put("magicflicker", 1);
			Map<String, Object> whereSQLArr = new HashMap<String, Object>();
			whereSQLArr.put("cid", id);
			whereSQLArr.put("authorid", supe_uid);
			dataBaseService.updateTable("comment", setSQLArr, whereSQLArr);
			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"));
			return false;
		}
		return true;
	}

	
	private boolean magic_frame(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		String idtype = "picid";
		Object result = magicService.magic_check_idtype(sGlobal, id, idtype);
		if (result instanceof MessageVO) {
			showMessage(request, response, (MessageVO) result);
			return false;
		}

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int frame = Common.intval(request.getParameter("frame"));

			Map<String, Object> setSQLArr = new HashMap<String, Object>();
			setSQLArr.put("magicframe", frame);
			Map<String, Object> whereSQLArr = new HashMap<String, Object>();
			whereSQLArr.put("picid", id);
			whereSQLArr.put("uid", (Integer) sGlobal.get("supe_uid"));
			dataBaseService.updateTable("pic", setSQLArr, whereSQLArr);

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);

			magicService.magic_use(sGlobal, mid, magicuselog, true);

			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}

		return true;
	}

	
	private boolean magic_friendnum(HttpServletRequest request, HttpServletResponse response) {
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int addnum = operateMagicCustomInfo(request, "addnum", 10);
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET addfriend = addfriend + " + addnum + " WHERE uid = '"
					+ (Integer) sGlobal.get("supe_uid") + "'");

			magicService.magic_use(sGlobal, mid, new HashMap<String, Object>(), true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}

		return true;
	}

	
	private boolean magic_gift(HttpServletRequest request, HttpServletResponse response) {
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int leftcredit = 0;
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid='" + supe_uid + "' AND mid='"
				+ mid + "'");
		Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
		Map<String, Object> data = null;
		if (value != null) {
			String dataTemp = (String) value.get("data");
			if (dataTemp != null && !dataTemp.equals("")) {
				data = Serializer.unserialize(dataTemp, false);
				leftcredit = (Integer) data.get("left");
			}
		}
		request.setAttribute("leftcredit", leftcredit);
		int custom_maxchunk = operateMagicCustomInfo(request, "maxchunk", 20);
		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int creditPost = Common.intval(request.getParameter("credit"));
			int chunkPost = Common.intval(request.getParameter("chunk"));

			if (chunkPost < 1 || chunkPost > creditPost || chunkPost > custom_maxchunk) {
				showMessage(request, response, "magicuse_bad_chunk_given");
				return false;
			}
			Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
			if (creditPost < 1 || creditPost > (Integer) space.get("credit")) {
				showMessage(request, response, "magicuse_bad_credit_given");
				return false;
			}

			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET credit = credit - " + creditPost + " + " + leftcredit + " WHERE uid = '"
					+ supe_uid + "'");
			data = new HashMap<String, Object>();
			data.put("credit", creditPost);
			data.put("chunk", chunkPost);
			data.put("left", creditPost);

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("data", Serializer.serialize(data));
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"));
			return false;
		}

		return true;
	}

	
	private boolean magic_hot(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String idtype = (String) request.getAttribute("idtype");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		Object result = magicService.magic_check_idtype(sGlobal, id, idtype);
		if (result instanceof MessageVO) {
			showMessage(request, response, (MessageVO) result);
			return false;
		}
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
				+ JavaCenterHome.getTableName("magicuselog") + " WHERE id = '" + id + "' AND idtype = '"
				+ idtype + "' AND uid = '" + supe_uid + "' AND mid = '" + mid + "'");
		if (query.size() > 0 && (Integer) query.get(0).get("cont") != 0) {
			showMessage(request, response, "magicuse_object_once_limit");
			return false;
		}

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			Object feedhotminOB = sConfig.get("feedhotmin");
			int hot = feedhotminOB != null ? Common.intval(String.valueOf(feedhotminOB)) : 0;
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("feed") + " SET hot = hot + "
					+ hot + " WHERE id = '" + id + "' AND idtype = '" + idtype + "' AND uid = '" + supe_uid
					+ "'");
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("blog") + " SET hot = hot + "
					+ hot + " WHERE blogid = '" + id + "' AND uid = '" + supe_uid + "'");

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"));
			return false;
		}
		return true;
	}

	
	private boolean magic_icon(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		String idtype = "tid";
		Object result = magicService.magic_check_idtype(sGlobal, id, idtype);
		if (result instanceof MessageVO) {
			showMessage(request, response, (MessageVO) result);
			return false;
		}
		Map<String, Object> thread = (Map<String, Object>) result;
		if ((Integer) thread.get("magicegg") >= 8) {
			showMessage(request, response, "magicuse_object_count_limit", null, 1, "8");
			return false;
		}

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("thread")
					+ " SET magicegg = magicegg + 1 WHERE tid = '" + id + "' AND uid = '" + supe_uid + "'");
			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}
		return true;
	}

	
	private boolean magic_invisible(HttpServletRequest request, HttpServletResponse response) {
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int custom_effectivetime = operateMagicCustomInfo(request, "effectivetime", 86400);
			int expire = (Integer) sGlobal.get("timestamp") + custom_effectivetime;

			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("session")
					+ " SET magichidden = 1 WHERE uid='" + supe_uid + "'");

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("expire", expire);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}
		return true;
	}

	
	private boolean magic_reveal(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String idtype = (String) request.getAttribute("idtype");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
				+ JavaCenterHome.getTableName("magicuselog") + " WHERE id = '" + id + "' AND idtype = '"
				+ idtype + "' AND mid = 'anonymous'");
		if (query.size() == 0 && (Integer) query.get(0).get("cont") == 0) {
			showMessage(request, response, "magicuse_bad_object");
			return false;
		}

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);

			request.setAttribute("op", "show");
			query = dataBaseService.executeQuery("SELECT uid, username FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE id = '" + id + "' AND idtype = '"
					+ idtype + "' AND mid = 'anonymous'");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			for (Map<String, Object> value : query) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), "", 0);
			}
			request.setAttribute("list", query);
		}
		return true;
	}

	
	private boolean magic_superstar(HttpServletRequest request, HttpServletResponse response) {
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			int starPost = Common.intval(request.getParameter("star"));
			int custom_effectivetime = operateMagicCustomInfo(request, "effectivetime", 604800);
			int expire = (Integer) sGlobal.get("timestamp") + custom_effectivetime;

			Map<String, Object> setSQLArr = new HashMap<String, Object>();
			Map<String, Object> whereSQLArr = new HashMap<String, Object>();
			setSQLArr.put("magicstar", starPost);
			setSQLArr.put("magicexpire", expire);
			whereSQLArr.put("uid", supe_uid);
			dataBaseService.updateTable("spacefield", setSQLArr, whereSQLArr);

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("expire", expire);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}
		return true;
	}

	
	private boolean magic_thunder(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String idtype = (String) request.getAttribute("idtype");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			magicService.magic_use(sGlobal, mid, new HashMap<String, Object>(), true);
			int uid = supe_uid;
			String supe_username = (String) sGlobal.get("supe_username");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			Common.realname_set(sGlobal, sConfig, sNames, supe_uid, supe_username, "", 0);
			Common.realname_get(sGlobal, sConfig, sNames, space);

			sGlobal.put("supe_uid", 0);
			String avatar = cpService.ckavatar(sGlobal, sConfig, uid) ? Common.avatar(uid, "middle", true,
					sGlobal, sConfig) : "data/avatar/noavatar_middle.gif";
			Map<String, Object> title_data = new HashMap<String, Object>();
			title_data.put("uid", uid);
			title_data.put("username", "<a href=\"space.jsp?uid=" + uid + "\">" + sNames.get(uid) + "</a>");
			Map<String, Object> body_data = new HashMap<String, Object>();
			body_data.put("uid", uid);
			body_data.put("magic_thunder", 1);
			cpService.addFeed(sGlobal, "thunder", Common.getMessage(request,
					"cp_magicuse_thunder_announce_title"), title_data, Common.getMessage(request,
					"cp_magicuse_thunder_announce_body"), body_data, "", new String[] {avatar},
					new String[] {"space.jsp?uid=" + uid}, "", 0, 0, id, idtype, false);

			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}
		return true;
	}

	
	private boolean magic_updateline(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String idtype = (String) request.getAttribute("idtype");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		Object result = magicService.magic_check_idtype(sGlobal, id, idtype);
		if (result instanceof MessageVO) {
			showMessage(request, response, (MessageVO) result);
			return false;
		}

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			String tablename = cpService.getTablebyIdType(idtype);
			int timestamp = (Integer) sGlobal.get("timestamp");
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName(tablename) + " SET dateline = '"
					+ timestamp + "' WHERE " + idtype + " = '" + id + "' AND uid = '" + supe_uid + "'");
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("feed") + " SET dateline = '"
					+ timestamp + "' WHERE id = '" + id + "' AND idtype = '" + idtype + "' AND uid = '"
					+ supe_uid + "'");

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			showMessage(request, response, "magicuse_success", request.getHeader("referer"), 0);
			return false;
		}
		return true;
	}

	
	private boolean magic_viewmagic(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		int custom_maxview = operateMagicCustomInfo(request, "maxview", 10);

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			String idtype = "uid";

			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);

			request.setAttribute("op", "show");
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("usermagic") + " WHERE uid='" + id
					+ "' AND count > 0 LIMIT " + custom_maxview);
			request.setAttribute("list", list);
		}
		return true;
	}

	
	private boolean magic_viewmagiclog(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			String idtype = "uid";
			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);

			request.setAttribute("op", "show");
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid='" + id
					+ "' ORDER BY dateline DESC LIMIT 10");
			request.setAttribute("list", list);
		}
		return true;
	}

	
	private boolean magic_viewvisitor(HttpServletRequest request, HttpServletResponse response) {
		int id = (Integer) request.getAttribute("id");
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		int custom_maxview = operateMagicCustomInfo(request, "maxview", 10);
		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			String idtype = "uid";
			Map<String, Object> magicuselog = new HashMap<String, Object>();
			magicuselog.put("id", id);
			magicuselog.put("idtype", idtype);
			magicService.magic_use(sGlobal, mid, magicuselog, true);
			request.setAttribute("op", "show");
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT m.uid, m.username FROM "
					+ JavaCenterHome.getTableName("visitor") + " v LEFT JOIN "
					+ JavaCenterHome.getTableName("member") + " m ON v.uid = m.uid WHERE v.vuid = '" + id
					+ "' AND v.vusername != '' ORDER BY v.dateline DESC LIMIT " + custom_maxview);
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			for (Map<String, Object> value : list) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), "", 0);
			}
			request.setAttribute("list", list);
			Common.realname_get(sGlobal, sConfig, sNames, space);
		}
		return true;
	}

	
	private boolean magic_visit(HttpServletRequest request, HttpServletResponse response) {
		String mid = (String) request.getAttribute("mid");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		boolean sc = false;
		try {
			sc = submitCheck(request, "usesubmit");
		} catch (Exception e) {
			showMessage(request, response, e.getMessage());
			return false;
		}
		if (sc) {
			String[] friends = (String[]) space.get("friends");
			String[] fids = null;
			int count = friends.length;
			if (count == 0) {
				showMessage(request, response, "magicuse_has_no_valid_friend");
				return false;
			} else if (count == 1) {
				fids = new String[] {friends[0]};
			} else {
				int custom_maxvisit = operateMagicCustomInfo(request, "maxvisit", 10);
				int getCount = Math.min(custom_maxvisit, count);
				fids = new String[getCount];
				List<Integer> tempList = new ArrayList<Integer>(getCount);
				int tempI = 0;
				Random random = new Random();
				for (int i = 0; i < getCount; i++) {
					tempI = random.nextInt(count);
					if (tempList.contains(tempI)) {
						i--;
						continue;
					}
					fids[i] = friends[tempI];
					tempList.add(tempI);
				}
			}
			StringBuilder inserts = new StringBuilder();
			String visitwayPost = request.getParameter("visitway");
			if ("poke".equals(visitwayPost)) {
				String note = "";
				int icon = Common.intval(request.getParameter("visitpoke"));
				String supe_username = (String) sGlobal.get("supe_username");
				int timestamp = (Integer) sGlobal.get("timestamp");
				String tempS = "', '";
				for (String fid : fids) {
					inserts.append("('");
					inserts.append(fid);
					inserts.append(tempS);
					inserts.append(supe_uid);
					inserts.append(tempS);
					inserts.append(supe_username);
					inserts.append(tempS);
					inserts.append(note);
					inserts.append(tempS);
					inserts.append(timestamp);
					inserts.append(tempS);
					inserts.append(icon);
					inserts.append("'),");
				}
				int tempI = 0;
				if ((tempI = inserts.length()) > 0) {
					inserts.delete(tempI - 1, tempI);
				}
				List<String> repokeids = new ArrayList<String>();
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("poke") + " WHERE fromuid = '" + supe_uid
						+ "' AND uid IN (" + Common.sImplode(fids) + ")");
				for (Map<String, Object> value : query) {
					repokeids.add(String.valueOf(value.get("uid")));
				}
				dataBaseService
						.execute("REPLACE INTO " + JavaCenterHome.getTableName("poke")
								+ "(uid, fromuid, fromusername, note, dateline, iconid) VALUES "
								+ inserts.toString());
				String[] ids = array_diff(fids, repokeids);
				dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET pokenum = pokenum + 1 WHERE uid IN (" + Common.sImplode(ids) + ")");

			} else if ("comment".equals(visitwayPost)) {
				String message = null;
				try {
					message = Common.getStr(request.getParameter("visitmsg"), 255, true, true, true, 0, 0,
							request, response);
				} catch (Exception e) {
					showMessage(request, response, e.getMessage());
					return false;
				}
				String ip = Common.getOnlineIP(request);
				StringBuilder note_inserts = new StringBuilder();

				String supe_username = (String) sGlobal.get("supe_username");
				int timestamp = (Integer) sGlobal.get("timestamp");
				String tempS = "', '";
				String note = null;

				StringBuilder arg = new StringBuilder();
				for (String fid : fids) {
					inserts.append("('");
					inserts.append(fid);
					inserts.append(tempS);
					inserts.append(fid);
					inserts.append("', 'uid', '");
					inserts.append(supe_uid);
					inserts.append(tempS);
					inserts.append(supe_username);
					inserts.append(tempS);
					inserts.append(ip);
					inserts.append(tempS);
					inserts.append(timestamp);
					inserts.append(tempS);
					inserts.append(message);
					inserts.append("'),");

					arg.append("space.jsp?uid=");
					arg.append(fid);
					arg.append("&do=wall");
					note = Common.getMessage(request, "cp_magic_note_wall", arg.toString());
					;
					arg.delete(0, arg.length());

					note_inserts.append("('");
					note_inserts.append(fid);
					note_inserts.append("', 'comment', '1', '");
					note_inserts.append(supe_uid);
					note_inserts.append(tempS);
					note_inserts.append(supe_username);
					note_inserts.append(tempS);
					note_inserts.append(note);
					note_inserts.append(tempS);
					note_inserts.append(timestamp);
					note_inserts.append("'),");
				}
				int tempI = 0;
				if ((tempI = inserts.length()) > 0) {
					inserts.delete(tempI - 1, tempI);
				}
				if ((tempI = note_inserts.length()) > 0) {
					note_inserts.delete(tempI - 1, tempI);
				}
				dataBaseService.execute("INSERT INTO " + JavaCenterHome.getTableName("comment")
						+ "(uid, id, idtype, authorid, author, ip, dateline, message) VALUES "
						+ inserts.toString());
				dataBaseService.execute("INSERT INTO " + JavaCenterHome.getTableName("notification")
						+ "(uid, type, new, authorid, author, note, dateline) VALUES "
						+ note_inserts.toString());
				dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET notenum = notenum + 1 WHERE uid IN (" + Common.sImplode(fids) + ")");

			} else {
				String supe_username = (String) sGlobal.get("supe_username");
				int timestamp = (Integer) sGlobal.get("timestamp");
				String tempS = "', '";
				for (String fid : fids) {
					inserts.append("('");
					inserts.append(fid);
					inserts.append(tempS);
					inserts.append(supe_uid);
					inserts.append(tempS);
					inserts.append(supe_username);
					inserts.append(tempS);
					inserts.append(timestamp);
					inserts.append("'),");
				}
				int tempI = 0;
				if ((tempI = inserts.length()) > 0) {
					inserts.delete(tempI - 1, tempI);
				}
				dataBaseService.execute("REPLACE INTO " + JavaCenterHome.getTableName("visitor")
						+ "(uid, vuid, vusername, dateline) VALUES " + inserts.toString());
			}

			magicService.magic_use(sGlobal, mid, new HashMap<String, Object>(), true);
			List<Map<String, Object>> query = dataBaseService
					.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("member")
							+ " WHERE uid IN (" + Common.sImplode(fids) + ")");
			int uid = 0;
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			for (Map<String, Object> value : query) {
				uid = (Integer) value.get("uid");
				Common.realname_set(sGlobal, sConfig, sNames, uid, (String) value.get("username"), "", 0);
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("users", query);
			request.setAttribute("op", "show");
		} else {
			List<String> icons = new ArrayList<String>(13);
			icons.add("<img src=\"image/poke/cyx.gif\" /> ²ÈÒ»ÏÂ");
			icons.add("<img src=\"image/poke/wgs.gif\" /> ÎÕ¸öÊÖ");
			icons.add("<img src=\"image/poke/wx.gif\" /> Î¢Ð¦");
			icons.add("<img src=\"image/poke/jy.gif\" /> ¼ÓÓÍ");
			icons.add("<img src=\"image/poke/pmy.gif\" /> Å×ÃÄÑÛ");
			icons.add("<img src=\"image/poke/yb.gif\" /> Óµ±§");
			icons.add("<img src=\"image/poke/fw.gif\" /> ·ÉÎÇ");
			icons.add("<img src=\"image/poke/nyy.gif\" /> ÄÓÑ÷Ñ÷");
			icons.add("<img src=\"image/poke/gyq.gif\" /> ¸øÒ»È­");
			icons.add("<img src=\"image/poke/dyx.gif\" /> µçÒ»ÏÂ");
			icons.add("<img src=\"image/poke/yw.gif\" /> ÒÀÙË");
			icons.add("<img src=\"image/poke/ppjb.gif\" /> ÅÄÅÄ¼ç°ò");
			icons.add("<img src=\"image/poke/yyk.gif\" /> Ò§Ò»¿Ú");
			request.setAttribute("icons", icons);
		}
		return true;
	}

	private int operateMagicCustomInfo(HttpServletRequest request, String key, int defaultValue) {
		int customKeyInf = 0;
		Map<String, Object> magic = (Map<String, Object>) request.getAttribute("magic");
		if (magic != null) {
			Map<String, String> custom = (Map<String, String>) magic.get("custom");
			if (custom == null) {
				custom = new HashMap<String, String>();
				magic.put("custom", custom);
			}
			String keyValue = custom.get(key);
			if (keyValue != null && !keyValue.equals("") && !keyValue.equals("0")) {
				customKeyInf = Common.intval(keyValue);
			} else {
				custom.put(key, defaultValue + "");
			}
		}
		return customKeyInf == 0 ? defaultValue : customKeyInf;
	}

	private boolean mapWithValue(Map<Integer, Integer> sourceMap, int target) {
		Collection<Integer> collection = sourceMap.values();
		return collection.contains(target);
	}

	
	private String[] array_diff(String[] searchArray, List<String> sourceList) {
		if (searchArray == null || sourceList == null) {
			return null;
		}
		String[] sourceArray = sourceList.toArray(new String[sourceList.size()]);
		List<String> resultList = new ArrayList<String>();
		for (String tempS : searchArray) {
			if (!Common.in_array(sourceArray, tempS)) {
				resultList.add(tempS);
			}
		}
		return resultList.toArray(new String[0]);
	}
}