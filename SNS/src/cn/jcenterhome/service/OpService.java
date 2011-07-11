package cn.jcenterhome.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;


public class OpService {
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");

	
	public boolean digestThreads(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			int tagId, int v, Object tids) throws Exception {
		Map<String, Object> mtag = Common.getMtag(request, response, supe_uid, tagId);
		if ((Integer) mtag.get("grade") < 8) {
			return false;
		}
		StringBuffer wheresql = new StringBuffer();
		if (v == 0) {
			wheresql.append(" AND digest=1");
		} else {
			wheresql.append(" AND digest=0");
			v = 1;
		}
		List<String> newTids = dataBaseService.executeQuery("SELECT tid FROM "
				+ JavaCenterHome.getTableName("thread") + " WHERE tagid=" + tagId + " AND tid IN ("
				+ Common.sImplode(tids) + ") " + wheresql.toString(), 1);
		if (newTids.size() > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread") + " SET digest="
					+ v + " WHERE tid IN (" + Common.sImplode(newTids) + ")");
			return true;
		} else {
			return false;
		}
	}

	
	public boolean topThreads(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			int tagId, int v, Object tids) throws Exception {
		Map<String, Object> mtag = Common.getMtag(request, response, supe_uid, tagId);
		if ((Integer) mtag.get("grade") < 8) {
			return false;
		}
		StringBuffer wheresql = new StringBuffer();
		if (v == 0) {
			wheresql.append(" AND displayorder=1");
		} else {
			wheresql.append(" AND displayorder=0");
			v = 1;
		}
		List<String> newTids = dataBaseService.executeQuery("SELECT tid FROM "
				+ JavaCenterHome.getTableName("thread") + " WHERE tagid=" + tagId + " AND tid IN ("
				+ Common.sImplode(tids) + ") " + wheresql.toString(), 1);
		if (newTids.size() > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread")
					+ " SET displayorder=" + v + " WHERE tid IN (" + Common.sImplode(newTids) + ")");
			return true;
		} else {
			return false;
		}
	}

	
	public boolean mergeTag(HttpServletRequest request, HttpServletResponse response, int newTagId,
			List<Object> tagIds) {
		if (!Common.checkPerm(request, response, "managetag")) {
			return false;
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tag")
				+ " WHERE tagid IN (" + Common.sImplode(tagIds) + ") AND tagid <> " + newTagId);
		tagIds.add(newTagId);
		String newIds = Common.sImplode(Common.uniqueArray(tagIds.toArray()));
		List<String> blogIds = dataBaseService.executeQuery("SELECT DISTINCT blogid FROM "
				+ JavaCenterHome.getTableName("tagblog") + " WHERE tagid IN (" + newIds + ")", 1);
		int size = blogIds.size();
		if (size > 0) {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagblog")
					+ " WHERE tagid IN (" + newIds + ")");
			List<String> inserts = new ArrayList<String>();
			for (String blogid : blogIds) {
				inserts.add("(" + newTagId + "," + blogid + ")");
			}
			dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("tagblog")
					+ " (tagid, blogid) VALUES " + Common.implode(inserts, ","));
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag") + " SET blognum="
					+ size + "  WHERE tagid=" + newTagId);
		}
		return true;
	}

	
	public boolean closeTag(HttpServletRequest request, HttpServletResponse response, String opType,
			Object tagIds) {
		if (!Common.checkPerm(request, response, "managetag")) {
			return false;
		}
		int close = 0;
		if (!"close".equals(opType)) {
			close = 1;
		}
		List<String> newTagIds = dataBaseService.executeQuery("SELECT tagid FROM "
				+ JavaCenterHome.getTableName("tag") + " WHERE tagid IN (" + Common.sImplode(tagIds)
				+ ")  AND close=" + close, 1);
		if (newTagIds.isEmpty()) {
			return false;
		}
		String newIds = Common.sImplode(newTagIds);
		if ("close".equals(opType)) {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagblog")
					+ " WHERE tagid IN (" + newIds + ")");
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag")
					+ " SET blognum=0, close=1 WHERE tagid IN (" + newIds + ")");
		} else {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag")
					+ " SET close=0 WHERE tagid IN (" + newIds + ")");
		}
		return true;
	}

	
	public boolean mergeMtag(HttpServletRequest request, HttpServletResponse response, int newTagId,
			String[] tagIds) {
		if (!Common.checkPerm(request, response, "managemtag")) {
			return false;
		}
		List<Integer> ckTagIds = new ArrayList<Integer>();
		for (String tagId : tagIds) {
			int id = Common.intval(tagId);
			if (id > 0 && id != newTagId) {
				ckTagIds.add(id);
			}
		}
		if (ckTagIds.isEmpty()) {
			return false;
		}
		String newIds = Common.sImplode(ckTagIds);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mtag")
				+ " WHERE tagid IN (" + newIds + ")");
		dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread") + " SET tagid="
				+ newTagId + " WHERE tagid IN (" + newIds + ")");
		dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("post") + " SET tagid="
				+ newTagId + " WHERE tagid IN (" + newIds + ")");
		List<String> oldUids = dataBaseService.executeQuery("SELECT uid FROM "
				+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid=" + newTagId, 1);

		Map<String, String> newUids = new TreeMap<String, String>();
		List<Map<String, Object>> tagspaceList2 = dataBaseService.executeQuery("SELECT uid, username FROM "
				+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid IN (" + newIds + ")");
		for (Map<String, Object> value : tagspaceList2) {
			String uid = value.get("uid").toString();
			if (!oldUids.contains(uid)) {
				newUids.put(uid, (String) value.get("username"));
			}
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagspace")
				+ " WHERE tagid IN (" + newIds + ")");
		List<String> inserts = new ArrayList<String>();
		Set<String> keys = newUids.keySet();
		for (String uid : keys) {
			inserts.add("(" + newTagId + ", " + uid + ", '" + Common.addSlashes((String) newUids.get(uid))
					+ "')");
		}
		if (inserts.size() > 0) {
			dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("tagspace")
					+ " (tagid,uid,username) VALUES " + Common.implode(inserts, ","));
		}
		int memberNum = dataBaseService.findRows("SELECT COUNT(*) FROM "
				+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid=" + newTagId);
		int threadNum = dataBaseService.findRows("SELECT COUNT(*) FROM "
				+ JavaCenterHome.getTableName("thread") + " WHERE tagid=" + newTagId);
		int postNum = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("post")
				+ " WHERE tagid=" + newTagId + " AND isthread=0");
		String sql = "UPDATE " + JavaCenterHome.getTableName("mtag") + " SET membernum=" + memberNum
				+ ",threadnum=" + threadNum + ",postnum=" + postNum + "  WHERE tagid=" + newTagId;
		dataBaseService.executeUpdate(sql);
		return true;
	}

	
	public boolean closeMtag(HttpServletRequest request, HttpServletResponse response, String opType,
			Object tagIds) {
		if (!Common.checkPerm(request, response, "managemtag")) {
			return false;
		}
		int close = 0;
		if (!"close".equals(opType)) {
			close = 1;
		}
		List<String> newTagIds = dataBaseService.executeQuery("SELECT tagid FROM "
				+ JavaCenterHome.getTableName("mtag") + " WHERE tagid IN (" + Common.sImplode(tagIds)
				+ ")  AND close=" + close, 1);
		if (newTagIds.isEmpty()) {
			return false;
		}
		dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag") + " SET close="
				+ ("close".equals(opType) ? 1 : 0) + " WHERE tagid IN (" + Common.sImplode(newTagIds) + ")");
		return true;
	}

	
	public boolean recommendMtag(HttpServletRequest request, HttpServletResponse response, String opType,
			Object tagIds) {
		if (!Common.checkPerm(request, response, "managemtag")) {
			return false;
		}
		int recommend = 0;
		if (!"recommend".equals(opType)) {
			recommend = 1;
		}
		List<String> newTagIds = dataBaseService.executeQuery("SELECT tagid FROM "
				+ JavaCenterHome.getTableName("mtag") + " WHERE tagid IN (" + Common.sImplode(tagIds)
				+ ")  AND recommend=" + recommend, 1);
		if (newTagIds.isEmpty()) {
			return false;
		}
		dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag") + " SET recommend="
				+ ("recommend".equals(opType) ? 1 : 0) + " WHERE tagid IN (" + Common.sImplode(newTagIds)
				+ ")");
		return true;
	}
}
