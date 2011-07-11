package cn.jcenterhome.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.vo.MessageVO;

public class MagicService {
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");

	
	public Object magic_get(String mid) {
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("magic") + " WHERE mid = '" + mid + "'");
		if (query.size() < 1) {
			return new MessageVO("unknown_magic");
		}
		Map<String, Object> magic = query.get(0);
		if (((Integer) magic.get("close")) == 1) {
			return new MessageVO("magic_is_closed");
		}
		String forbiddengid = (String) magic.get("forbiddengid");
		if (Common.empty(forbiddengid)) {
			magic.put("forbiddengid", null);
		} else {
			magic.put("forbiddengid", forbiddengid.split(","));
		}
		String custom = (String) magic.get("custom");
		if (Common.empty(custom)) {
			magic.put("custom", new HashMap<String, String>());
		} else {
			magic.put("custom", Serializer.unserialize(custom, false));
		}
		return magic;
	}

	
	public Object magic_buy_get(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> magic, Map<String, Object> sGlobal, Map<String, Object> space) {
		if (Common.empty(magic)) {
			return new MessageVO("unknown_magic");
		}
		String mid = (String) magic.get("mid");

		String[] blacklist = {"coupon"};
		if (Common.in_array(blacklist, mid)) {
			return new MessageVO("magic_not_for_sale");
		}

		if (!Common.checkPerm(request, response, "allowmagic")) {
			MessageVO msgVO = Common.ckSpaceLog(request);
			if (msgVO != null) {
				return msgVO;
			}
			return new MessageVO("magic_groupid_not_allowed");
		}
		String[] tempA = (String[]) magic.get("forbiddengid");
		if (tempA != null && tempA.length > 0 && Common.in_array(tempA, space.get("groupid"))) {
			return new MessageVO("magic_groupid_limit");
		}

		Map<String, Object> setData = new HashMap<String, Object>();
		setData.put("mid", mid);
		setData.put("storage", magic.get("providecount"));
		setData.put("lastprovide", sGlobal.get("timestamp"));

		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("magicstore") + " WHERE mid = '" + mid + "'");
		Map<String, Object> magicstore = null;
		if (query.size() > 0) {
			magicstore = query.get(0);
		}

		if (magicstore == null) {
			magicstore = new HashMap<String, Object>();
			dataBaseService.insertTable("magicstore", setData, false, false);
			magicstore.put("storage", magic.get("providecount"));
		} else if ((Integer) magicstore.get("storage") < (Integer) magic.get("providecount")
				&& (Integer) magicstore.get("lastprovide") + (Integer) magic.get("provideperoid") < (Integer) sGlobal
						.get("timestamp")) {

			setData.remove("mid");
			Map<String, Object> whereData = new HashMap<String, Object>(1);
			whereData.put("mid", mid);
			dataBaseService.updateTable("magicstore", setData, whereData);
			magicstore.put("storage", magic.get("providecount"));
		}

		if ((Integer) magicstore.get("storage") < 1) {
			String nexttime = Common.sgmdate(request, "MM-dd HH:mm", (Integer) magicstore.get("lastprovide")
					+ (Integer) magic.get("provideperoid"));
			return new MessageVO("not_enough_storage", null, 0, nexttime);
		}
		int discount = (Integer) Common.checkPerm(request, response, sGlobal, "magicdiscount");
		int charge = (Integer) magic.get("charge");
		if (discount > 0) {
			charge = charge * discount / 10;
			if (charge < 1) {
				charge = 1;
			}
		} else if (discount < 0) {
			charge = 0;
		}
		magicstore.put("maxbuy", charge == 0 ? magicstore.get("storage") : Math.min((Integer) magicstore
				.get("storage"), (int) Math.floor((Integer) space.get("credit") / (float) charge)));
		query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("usermagic")
				+ " WHERE uid='" + sGlobal.get("supe_uid") + "' AND mid = 'coupon'");
		Map<String, Object> coupon = null;
		if (query.size() > 0) {
			coupon = query.get(0);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("magicstore", magicstore);
		data.put("coupon", coupon);
		data.put("discount", discount);
		data.put("charge", charge);
		return data;
	}

	
	public Object magic_buy_post(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> space, Map<String, Object> magic,
			Map<String, Object> magicstore, Map<String, Object> coupon) {
		if (Common.empty(magic)) {
			return new MessageVO("unknown_magic");
		}
		String mid = (String) magic.get("mid");

		int buynum = Common.intval(request.getParameter("buynum"));
		if (buynum < 1) {
			return new MessageVO("bad_buynum");
		}
		if ((Integer) magicstore.get("storage") < buynum) {
			String nexttime = Common.sgmdate(request, "MM-dd HH:mm", (Integer) magicstore.get("lastprovide")
					+ (Integer) magic.get("provideperoid"));
			return new MessageVO("not_enough_storage", null, 0, nexttime);
		}

		int coupon_post = Common.intval(request.getParameter("coupon"));

		int discard = 0;
		if (coupon_post != 0) {
			if ((Integer) coupon.get("count") < coupon_post) {
				return new MessageVO("not_enough_coupon");
			}
			discard = 100 * coupon_post;
		}

		int discount = (Integer) Common.checkPerm(request, response, sGlobal, "magicdiscount");
		if (discount > 0) {
			int charge = (Integer) magic.get("charge") * discount / 10;
			if (charge < 1) {
				charge = 1;
			}
			magic.put("charge", charge);
		} else if (discount < 0) {
			magic.put("charge", 0);
		}
		int charge = buynum * (Integer) magic.get("charge") - discard;
		charge = charge > 0 ? charge : 0;
		if (charge > (Integer) space.get("credit")) {
			return new MessageVO("credit_is_not_enough");
		}

		int supe_uid = (Integer) sGlobal.get("supe_uid");
		dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("magicstore")
				+ " SET storage = storage - " + buynum + ", sellcount = sellcount + " + buynum
				+ ", sellcredit = sellcredit + " + charge + " WHERE mid = '" + mid + "'");
		int experience = buynum * (Integer) magic.get("experience");
		dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space") + " SET credit = credit - "
				+ charge + ", experience = experience + '" + experience + "' WHERE uid = '" + supe_uid + "'");
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("usermagic") + " WHERE uid='" + supe_uid + "' AND mid='" + mid
				+ "'");
		int count = buynum;
		if (query.size() > 0) {
			count += (Integer) query.get(0).get("count");
		}
		String username = (String) sGlobal.get("username");
		dataBaseService.execute("REPLACE " + JavaCenterHome.getTableName("usermagic")
				+ "(uid, username, mid, count) VALUES ('" + supe_uid + "', '" + username + "', '" + mid
				+ "', '" + count + "')");
		Map<String, Object> insertData = new HashMap<String, Object>();
		insertData.put("uid", supe_uid);
		insertData.put("username", sGlobal.get("supe_username"));
		insertData.put("mid", mid);
		insertData.put("count", buynum);
		insertData.put("type", 1);
		insertData.put("credit", charge);
		insertData.put("dateline", sGlobal.get("timestamp"));
		dataBaseService.insertTable("magicinlog", insertData, false, false);
		if (coupon_post != 0) {
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("usermagic")
					+ " SET count = count - " + coupon_post + " WHERE uid='" + supe_uid
					+ "' AND mid = 'coupon'");
		}
		return charge;
	}

	
	public Object magic_check_idtype(Map<String, Object> sGlobal, Object id, String idtype) {
		Map<String, Object> value = null;
		CpService cpService = (CpService) BeanFactory.getBean("cpService");
		String tablename = cpService.getTablebyIdType(idtype);
		if (tablename != null) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName(tablename) + " WHERE " + idtype + "='" + id
					+ "' AND uid = '" + sGlobal.get("supe_uid") + "'");
			value = query.size() > 0 ? query.get(0) : null;
		}
		if (Common.empty(value)) {
			return new MessageVO("magicuse_bad_object");
		}
		return value;
	}

	
	public void magic_use(Map<String, Object> sGlobal, String mid, Map<String, Object> magicuselog,
			boolean replace) {
		if (magicuselog == null) {
			magicuselog = new HashMap<String, Object>();
		}
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("usermagic")
				+ " SET count = count - 1 WHERE uid = '" + supe_uid + "' AND mid = '" + mid
				+ "' AND count > 0");
		Map<String, Object> value = null;
		if (replace) {
			String whereSQL = "";
			Integer tempId = (Integer) magicuselog.get("id");
			if (tempId != null && tempId != 0) {
				whereSQL = " AND id='" + tempId + "' AND idtype='" + magicuselog.get("idtype") + "'";
			}
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid = '" + supe_uid
					+ "' AND mid = '" + mid + "' " + whereSQL);
			value = query.size() > 0 ? query.get(0) : null;
		}
		magicuselog.put("mid", mid);
		magicuselog.put("uid", supe_uid);
		magicuselog.put("username", sGlobal.get("supe_username"));
		magicuselog.put("dateline", sGlobal.get("timestamp"));

		int count = 0;
		int logid = 0;
		if (value != null) {
			count = (Integer) value.get("count");
			logid = (Integer) value.get("logid");
		}
		if (count != 0) {
			magicuselog.put("count", count + 1);
		} else {
			magicuselog.put("count", 1);
		}

		if (logid != 0) {
			Map<String, Object> whereData = new HashMap<String, Object>();
			whereData.put("logid", logid);
			dataBaseService.updateTable("magicuselog", magicuselog, whereData);
		} else {
			if (magicuselog.get("data") == null) {
				magicuselog.put("data", "");
			}
			dataBaseService.insertTable("magicuselog", magicuselog, false, false);
		}

	}
}