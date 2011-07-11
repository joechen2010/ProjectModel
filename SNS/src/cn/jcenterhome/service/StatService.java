package cn.jcenterhome.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jcenterhome.dao.DataBaseDao;
import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;

public class StatService {
	private DataBaseDao dataBaseDao = (DataBaseDao) BeanFactory.getBean("dataBaseDao");

	
	public boolean blogReplyNumStat(int start, int perpage) {
		boolean next = false;
		Map<Integer, Integer> updates = new HashMap<Integer, Integer>();
		List<Map<String, Object>> blogList = dataBaseDao.executeQuery("SELECT blogid, replynum FROM "
				+ JavaCenterHome.getTableName("blog") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : blogList) {
			next = true;
			int count = dataBaseDao.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("comment")
					+ " WHERE id='" + value.get("blogid") + "' AND idtype='blogid'");
			if (count != (Integer) value.get("replynum")) {
				updates.put((Integer) value.get("blogid"), count);
			}
		}
		if (updates.size() == 0) {
			return next;
		}
		Object[] nums = Common.reNum(updates);
		List<Integer> num0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer num : num0) {
			dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("blog") + " SET replynum="
					+ num + " WHERE blogid IN (" + Common.sImplode(num1.get(num)) + ")");
		}
		return next;
	}

	
	public boolean spaceFriendNumStat(int start, int perpage) {
		boolean next = false;
		Map<Integer, Integer> updates = new HashMap<Integer, Integer>();
		List<Map<String, Object>> spaceList = dataBaseDao.executeQuery("SELECT uid, friendnum FROM "
				+ JavaCenterHome.getTableName("space") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : spaceList) {
			next = true;
			int count = dataBaseDao.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("friend")
					+ " WHERE uid='" + value.get("uid") + "' AND status='1'");
			if (count != (Integer) value.get("friendnum")) {
				updates.put((Integer) value.get("uid"), count);
			}
		}
		if (updates.size() == 0) {
			return next;
		}
		Object[] nums = Common.reNum(updates);
		List<Integer> num0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer num : num0) {
			dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET friendnum="
					+ num + " WHERE uid IN(" + Common.sImplode(num1.get(num)) + ")");
		}
		return next;
	}

	
	public boolean spaceFriendStat(int start, int perpage) {
		boolean next = false;
		List<Map<String, Object>> spacefieldList = dataBaseDao.executeQuery("SELECT uid, friend FROM "
				+ JavaCenterHome.getTableName("spacefield") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : spacefieldList) {
			next = true;
			List<String> fuids = dataBaseDao.executeQuery("SELECT fuid FROM "
					+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + value.get("uid")
					+ "' AND status='1'", 1);
			String friend = Common.implode(fuids, ",");
			if (!friend.equals(value.get("friend"))) {
				dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("spacefield")
						+ " SET friend='" + friend + "' WHERE uid='" + value.get("uid") + "'");
			}
		}
		return next;
	}

	

	public boolean mtagMemberNumStat(int start, int perpage) {
		boolean next = false;
		Map<Integer, Integer> updates = new HashMap<Integer, Integer>();
		List<Map<String, Object>> mtagList = dataBaseDao.executeQuery("SELECT tagid, membernum FROM "
				+ JavaCenterHome.getTableName("mtag") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : mtagList) {
			next = true;
			int count = dataBaseDao.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='" + value.get("tagid") + "'");
			if (count != (Integer) value.get("membernum")) {
				updates.put((Integer) value.get("tagid"), count);
			}
		}
		if (updates.size() == 0) {
			return next;
		}
		Object[] nums = Common.reNum(updates);
		List<Integer> num0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer num : num0) {
			dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag") + " SET membernum="
					+ num + " WHERE tagid IN (" + Common.sImplode(num1.get(num)) + ")");
		}
		return next;
	}

	
	public boolean mtagThreadNumStat(int start, int perpage) {
		boolean next = false;
		Map<Integer, Integer> updates = new HashMap<Integer, Integer>();
		List<Map<String, Object>> mtagList = dataBaseDao.executeQuery("SELECT tagid, threadnum FROM "
				+ JavaCenterHome.getTableName("mtag") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : mtagList) {
			next = true;
			int count = dataBaseDao.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("thread")
					+ " WHERE tagid='" + value.get("tagid") + "'");
			if (count != (Integer) value.get("threadnum")) {
				updates.put((Integer) value.get("tagid"), count);
			}
		}
		if (updates.size() == 0) {
			return next;
		}
		Object[] nums = Common.reNum(updates);
		List<Integer> num0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer num : num0) {
			dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag") + " SET threadnum="
					+ num + " WHERE tagid IN (" + Common.sImplode(num1.get(num)) + ")");
		}
		return next;
	}

	
	public boolean mtagPostNumStat(int start, int perpage) {
		boolean next = false;
		Map<Integer, Integer> updates = new HashMap<Integer, Integer>();
		List<Map<String, Object>> mtagList = dataBaseDao.executeQuery("SELECT tagid, postnum FROM "
				+ JavaCenterHome.getTableName("mtag") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : mtagList) {
			next = true;
			int count = dataBaseDao.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("post")
					+ " WHERE tagid='" + value.get("tagid") + "' AND isthread='0'");
			if (count != (Integer) value.get("postnum")) {
				updates.put((Integer) value.get("tagid"), count);
			}
		}
		if (updates.size() == 0) {
			return next;
		}
		Object[] nums = Common.reNum(updates);
		List<Integer> num0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer num : num0) {
			dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag") + " SET postnum=" + num
					+ " WHERE tagid IN (" + Common.sImplode(num1.get(num)) + ")");
		}
		return next;
	}

	
	public boolean threadReplyNumStat(int start, int perpage) {
		boolean next = false;
		Map<Integer, Integer> updates = new HashMap<Integer, Integer>();
		List<Map<String, Object>> threadList = dataBaseDao.executeQuery("SELECT tid, replynum FROM "
				+ JavaCenterHome.getTableName("thread") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : threadList) {
			next = true;
			int count = dataBaseDao.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("post")
					+ " WHERE tid='" + value.get("tid") + "' AND isthread='0'");
			if (count != (Integer) value.get("replynum")) {
				updates.put((Integer) value.get("tid"), count);
			}
		}
		if (updates.size() == 0) {
			return next;
		}
		Object[] nums = Common.reNum(updates);
		List<Integer> num0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer num : num0) {
			dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread") + " SET replynum="
					+ num + " WHERE tid IN (" + Common.sImplode(num1.get(num)) + ")");
		}
		return next;
	}

	
	public boolean albumPicNumStat(int start, int perpage) {
		boolean next = false;
		Map<Integer, Integer> updates = new HashMap<Integer, Integer>();
		List<Map<String, Object>> albumList = dataBaseDao.executeQuery("SELECT albumid, picnum FROM "
				+ JavaCenterHome.getTableName("album") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : albumList) {
			next = true;
			int count = dataBaseDao.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pic")
					+ " WHERE albumid='" + value.get("albumid") + "'");
			if (count != (Integer) value.get("picnum")) {
				updates.put((Integer) value.get("albumid"), count);
			}
		}
		if (updates.size() == 0) {
			return next;
		}
		Object[] nums = Common.reNum(updates);
		List<Integer> num0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer num : num0) {
			dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("album") + " SET picnum=" + num
					+ " WHERE albumid IN (" + Common.sImplode(num1.get(num)) + ")");
		}
		return next;
	}

	
	public boolean tagBlogNumStat(int start, int perpage) {
		boolean next = false;
		Map<Integer, Integer> updates = new HashMap<Integer, Integer>();
		List<Map<String, Object>> tagList = dataBaseDao.executeQuery("SELECT tagid, blognum FROM "
				+ JavaCenterHome.getTableName("tag") + " LIMIT " + start + "," + perpage);
		for (Map<String, Object> value : tagList) {
			next = true;
			int count = dataBaseDao.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("tagblog")
					+ " WHERE tagid='" + value.get("tagid") + "'");
			if (count != (Integer) value.get("blognum")) {
				updates.put((Integer) value.get("tagid"), count);
			}
		}
		if (updates.size() == 0) {
			return next;
		}
		Object[] nums = Common.reNum(updates);
		List<Integer> num0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer num : num0) {
			dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag") + " SET blognum=" + num
					+ " WHERE tagid IN (" + Common.sImplode(num1.get(num)) + ")");
		}
		return next;
	}
}
