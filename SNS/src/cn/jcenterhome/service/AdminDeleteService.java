
package cn.jcenterhome.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.FtpUtil;
import cn.jcenterhome.util.JavaCenterHome;


public class AdminDeleteService {
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");

	
	public boolean deleteBlogs(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object blogIds) {
		boolean allowmanage = Common.checkPerm(request, response, "manageblog");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		List<Map<String, Object>> blogList = dataBaseService
				.executeQuery("SELECT blogid, uid FROM " + JavaCenterHome.getTableName("blog")
						+ " WHERE blogid IN (" + Common.sImplode(blogIds) + ")");
		List<Map<String, Object>> blogs = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> value : blogList) {
			int uid = (Integer) value.get("uid");
			if (allowmanage || supe_uid == uid) {
				blogs.add(value);
				if (!managebatch && supe_uid != uid) {
					delnum++;
				}
			}
		}

		if (blogs.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}
		Map<String, Integer> reward = Common.getReward("delblog", false, 0, "", true, request, response);
		int credit = reward.get("credit");
		int experience = reward.get("experience");
		List<Integer> newBlogIds = new ArrayList<Integer>();
		for (Map<String, Object> value : blogs) {
			int blogid = (Integer) value.get("blogid");
			int uid = (Integer) value.get("uid");
			newBlogIds.add(blogid);
			if (allowmanage && supe_uid != uid) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + credit + ", experience=experience-" + experience
						+ " WHERE uid=" + uid);
			}
			List<String> tags = dataBaseService.executeQuery("SELECT tagid FROM "
					+ JavaCenterHome.getTableName("tagblog") + " WHERE blogid=" + blogid, 1);
			if (tags.size() > 0) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag")
						+ " SET blognum=blognum-1 WHERE tagid IN (" + Common.sImplode(tags) + ")");
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagblog")
						+ " WHERE blogid=" + blogid);
			}
		}
		String ids = Common.sImplode(newBlogIds);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("blog")
				+ " WHERE blogid IN (" + ids + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("blogfield")
				+ " WHERE blogid IN (" + ids + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("comment")
				+ " WHERE id IN (" + ids + ") AND idtype='blogid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
				+ " WHERE id IN (" + ids + ") AND idtype='blog'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE id IN ("
				+ ids + ") AND idtype='blogid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("clickuser")
				+ " WHERE id IN (" + ids + ") AND idtype='blogid'");
		return true;
	}

	
	public boolean deleteAlbums(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object albumIds) {
		boolean allowmanage = Common.checkPerm(request, response, "managealbum");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		List<Object> newids = new ArrayList<Object>();
		List<Map<String, Object>> albums = dataBaseService.executeQuery("SELECT albumid, uid FROM "
				+ JavaCenterHome.getTableName("album") + " WHERE albumid IN (" + Common.sImplode(albumIds)
				+ ")");
		for (Map<String, Object> album : albums) {
			int uid = (Integer) album.get("uid");
			if (allowmanage || supe_uid == uid) {
				newids.add(album.get("albumid"));
				if (!managebatch && supe_uid != uid) {
					delnum++;
				}
			}
		}

		if (newids.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}
		String ids = Common.sImplode(newids);

		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("album")
				+ " WHERE albumid IN (" + ids + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE id IN ("
				+ ids + ")  AND idtype='albumid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
				+ " WHERE id IN (" + ids + ")  AND idtype='albumid'");

		List<Map<String, Object>> pics = dataBaseService
				.executeQuery("SELECT uid, size, filepath, thumb, remote FROM "
						+ JavaCenterHome.getTableName("pic") + " WHERE albumid IN (" + ids + ")");
		if (pics.size() > 0) {
			Map<String, Integer> reward = Common.getReward("delimage", false, 0, "", true, request, response);
			int credit = reward.get("credit");
			int experience = reward.get("experience");
			for (Map<String, Object> pic : pics) {
				int uid = (Integer) pic.get("uid");
				StringBuffer setsql = new StringBuffer();
				if (allowmanage && uid != supe_uid) {
					setsql.append(credit > 0 ? (",credit=credit-" + credit) : "");
					setsql.append(experience > 0 ? (",experience=experience-" + experience) : "");
				}
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET attachsize=attachsize-" + pic.get("size") + " " + setsql + " WHERE uid="
						+ uid);
			}
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("pic")
					+ " WHERE albumid IN (" + ids + ")");
			deletePicFiles(request, response, pics);
		}
		return true;
	}

	
	public boolean deletePics(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object picIds) {
		boolean allowmanage = Common.checkPerm(request, response, "managealbum");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		List<Object> newIds = new ArrayList<Object>();
		List<Map<String, Object>> pics = new ArrayList<Map<String, Object>>();
		Map<Integer, Integer[]> albums = new HashMap<Integer, Integer[]>();
		Map<Integer, Integer[]> spaces = new HashMap<Integer, Integer[]>();
		List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("pic") + " WHERE picid IN (" + Common.sImplode(picIds) + ")");
		for (Map<String, Object> pic : picList) {
			int uid = (Integer) pic.get("uid");
			if (allowmanage || uid == supe_uid) {
				pics.add(pic);
				newIds.add(pic.get("picid"));
				int albumId = (Integer) pic.get("albumid");
				if (albumId > 0) {
					Integer[] album = albums.get(albumId);
					if (album == null) {
						album = new Integer[] {uid, 1};
					} else {
						album[0] = uid;
						album[1]++;
					}
					albums.put(albumId, album);
				}
				if (uid != supe_uid) {
					if (!managebatch) {
						delnum++;
					}
				}
				int size = (Integer) pic.get("size");
				Integer[] space = spaces.get(uid);
				if (space == null) {
					space = new Integer[] {size, 1};
				} else {
					space[0] += size;
					space[1]++;
				}
				spaces.put(uid, space);
			}
		}
		if (pics.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}

		if (spaces.size() > 0) {
			Map<String, Integer> reward = Common.getReward("delimage", false, 0, "", true, request, response);
			int credit = reward.get("credit");
			int experience = reward.get("experience");
			Set<Integer> uids = spaces.keySet();
			for (Integer uid : uids) {
				Integer[] space = spaces.get(uid);
				StringBuffer setsql = new StringBuffer();
				if (allowmanage) {
					if (credit != 0) {
						setsql.append(" ,credit=credit-" + (space[1] * credit));
					}
					if (experience != 0) {
						setsql.append(" ,experience=experience-" + (space[1] * experience));
					}
				}
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET attachsize=attachsize-" + space[0] + setsql + " WHERE uid=" + uid);
			}
		}
		if (newIds.size() > 0) {
			String ids = Common.sImplode(newIds);
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("pic")
					+ " WHERE picid IN (" + ids + ")");
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("comment")
					+ " WHERE id IN (" + ids + ") AND idtype='picid'");
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed")
					+ " WHERE id IN (" + ids + ") AND idtype='picid'");
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
					+ " WHERE id IN (" + ids + ") AND idtype='picid'");
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("clickuser")
					+ " WHERE id IN (" + ids + ") AND idtype='picid'");
		}
		if (albums.size() > 0) {
			Set<Integer> albumIds = albums.keySet();
			for (Integer albumId : albumIds) {
				Integer[] album = albums.get(albumId);
				String thePic = getAlbumPic(album[0], albumId);
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("album") + " SET pic='"
						+ thePic + "', picnum=picnum-" + album[1] + " WHERE albumid=" + albumId);
			}
		}
		if (pics.size() > 0) {
			deletePicFiles(request, response, pics);
		}
		return true;
	}

	
	public boolean deleteProfilefield(HttpServletRequest request, HttpServletResponse response,
			Object fieldids) {
		if (!Common.checkPerm(request, response, "manageprofilefield")) {
		}
		Object[] fieldidArray;
		if (fieldids instanceof Set) {
			fieldidArray = ((Set) fieldids).toArray();
		} else if (fieldids instanceof List) {
			fieldidArray = ((List) fieldids).toArray();
		} else if (fieldids instanceof Object[]) {
			fieldidArray = (Object[]) fieldids;
		} else {
			return false;
		}
		StringBuilder sql = new StringBuilder("DELETE FROM " + JavaCenterHome.getTableName("profilefield")
				+ " WHERE fieldid IN (" + Common.sImplode(fieldidArray) + ")");
		dataBaseService.execute(sql.toString());
		sql.delete(0, sql.length());
		sql.append("ALTER TABLE " + JavaCenterHome.getTableName("spacefield") + " DROP `field_");
		int tempLen = sql.length();
		sql.append("`");
		for (Object ob : fieldidArray) {
			sql.insert(tempLen, String.valueOf(ob));
			dataBaseService.execute(sql.toString());
			sql.delete(tempLen, sql.length() - 1);
		}
		return true;
	}

	
	public boolean deleteComments(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object cids) {
		boolean allowmanage = Common.checkPerm(request, response, "managecomment");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		List<Map<String, Object>> dels = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> comments = dataBaseService
				.executeQuery("SELECT cid, uid, id, idtype, authorid FROM "
						+ JavaCenterHome.getTableName("comment") + " WHERE cid IN (" + Common.sImplode(cids)
						+ ")");
		for (Map<String, Object> comment : comments) {
			int authorId = (Integer) comment.get("authorid");
			if (allowmanage || authorId == supe_uid || (Integer) comment.get("uid") == supe_uid) {
				dels.add(comment);
				if (!managebatch && authorId != supe_uid) {
					delnum++;
				}
			}
		}
		if (dels.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}
		Map<String, Integer> reward = Common.getReward("delcomment", false, 0, "", true, request, response);
		int credit = reward.get("credit");
		int experience = reward.get("experience");
		List<Object> newcids = new ArrayList<Object>();
		Map<Integer, Integer> blognums = new HashMap<Integer, Integer>();
		for (Map<String, Object> value : dels) {
			newcids.add(value.get("cid"));
			if ("blogid".equals(value.get("idtype"))) {
				int id = (Integer) value.get("id");
				Integer blogNum = blognums.get(id);
				if (blogNum == null) {
					blogNum = 0;
				}
				blognums.put(id, blogNum + 1);
			}
			int authorId = (Integer) value.get("authorid");
			if (allowmanage && authorId != supe_uid) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + credit + ", experience=" + experience + " WHERE uid="
						+ authorId);
			}
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("comment")
				+ " WHERE cid IN (" + Common.sImplode(newcids) + ")");
		if (blognums.size() > 0) {
			Object[] nums = Common.reNum(blognums);
			List<Integer> num0 = (List<Integer>) nums[0];
			Map<Integer, List<Integer>> num1 = (Map<Integer, List<Integer>>) nums[1];
			for (Integer num : num0) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("blog")
						+ " SET replynum=replynum-" + num + " WHERE blogid IN ("
						+ Common.sImplode(num1.get(num)) + ")");
			}
		}
		return true;
	}

	
	public List<Map<String, Object>> deleteThreads(HttpServletRequest request, HttpServletResponse response,
			int supe_uid, int tagId, Object tids) throws Exception {
		boolean allowmanage = Common.checkPerm(request, response, "managethread");
		boolean ismanager = allowmanage;
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		StringBuffer wheresql = new StringBuffer();
		if (!allowmanage && tagId > 0) {
			Map<String, Object> mtag = Common.getMtag(request, response, supe_uid, tagId);
			if ((Integer) mtag.get("grade") >= 8) {
				allowmanage = true;
				managebatch = true;
				wheresql.append(" AND tagid=" + tagId);
			}
		}
		List<Map<String, Object>> delthreads = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT tid, uid FROM "
				+ JavaCenterHome.getTableName("thread") + " WHERE tid IN(" + Common.sImplode(tids) + ") "
				+ wheresql);
		for (Map<String, Object> value : threadList) {
			int uid = (Integer) value.get("uid");
			if (allowmanage || uid == supe_uid) {
				delthreads.add(value);
				if (!managebatch && uid != supe_uid) {
					delnum++;
				}
			}
		}
		if (delthreads.isEmpty() || (!managebatch && delnum > 1)) {
			return null;
		}
		Map<String, Integer> reward = Common.getReward("delthread", false, 0, "", true, request, response);
		int credit = reward.get("credit");
		int experience = reward.get("experience");
		List<Object> newids = new ArrayList<Object>();
		for (Map<String, Object> value : delthreads) {
			int uid = (Integer) value.get("uid");
			newids.add(value.get("tid"));
			value.put("isthread", 1);
			if (ismanager && uid != supe_uid) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + credit + ", experience=experience-" + experience
						+ " WHERE uid=" + uid);
			}
		}
		String ids = Common.sImplode(newids);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("thread")
				+ " WHERE tid IN(" + ids + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("post") + " WHERE tid IN("
				+ ids + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE id IN("
				+ ids + ") AND idtype='tid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
				+ " WHERE id IN(" + ids + ") AND idtype='tid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("clickuser")
				+ " WHERE id IN(" + ids + ") AND idtype='tid'");
		return delthreads;
	}

	
	public List<Map<String, Object>> deletePosts(HttpServletRequest request, HttpServletResponse response,
			int supe_uid, int tagId, Object pids) throws Exception {
		boolean allowmanage = Common.checkPerm(request, response, "managethread");
		boolean ismanager = allowmanage;
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		StringBuffer wheresql = new StringBuffer();
		if (!allowmanage && tagId > 0) {
			Map<String, Object> mtag = Common.getMtag(request, response, supe_uid, tagId);
			if ((Integer) mtag.get("grade") >= 8) {
				allowmanage = true;
				managebatch = true;
				wheresql.append(" AND tagid=" + tagId);
			}
		}

		List<Map<String, Object>> posts = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> postList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("post") + " WHERE pid IN (" + Common.sImplode(pids) + ") "
				+ wheresql + " ORDER BY isthread DESC");
		for (Map<String, Object> value : postList) {
			int uid = (Integer) value.get("uid");
			if (allowmanage || uid == supe_uid) {
				posts.add(value);
				if (!managebatch && uid != supe_uid) {
					delnum++;
				}
			}
		}
		if (posts.isEmpty() || !managebatch && delnum > 1) {
			return null;
		}
		Map<String, Integer> reward = Common.getReward("delcomment", false, 0, "", true, request, response);
		int credit = reward.get("credit");
		int experience = reward.get("experience");
		List<Integer> tids = new ArrayList<Integer>();
		List<Object> newids = new ArrayList<Object>();
		List<Map<String, Object>> delPosts = new ArrayList<Map<String, Object>>();
		Map<Integer, Integer> postNums = new HashMap<Integer, Integer>();
		for (Map<String, Object> value : posts) {
			int tid = (Integer) value.get("tid");
			if ((Integer) value.get("isthread") > 0) {
				tids.add(tid);
			} else {
				if (!tids.contains(tid)) {
					int uid = (Integer) value.get("uid");
					newids.add(value.get("pid"));
					delPosts.add(value);
					Integer postNum = postNums.get(tid);
					if (postNum == null) {
						postNum = 0;
					}
					postNums.put(tid, postNum + 1);
					if (ismanager && uid != supe_uid) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET credit=credit-" + credit + ", experience=experience-" + experience
								+ " WHERE uid=" + uid);
					}
				}
			}
		}
		List<Map<String, Object>> delThreads = null;
		if (tids.size() > 0) {
			delThreads = deleteThreads(request, response, supe_uid, tagId, tids);
		}
		if (delPosts.isEmpty()) {
			return delThreads;
		}
		Object[] nums = Common.reNum(postNums);
		List<Integer> pnum0 = (List<Integer>) nums[0];
		Map<Integer, List<Integer>> pnum1 = (Map<Integer, List<Integer>>) nums[1];
		for (Integer pnum : pnum0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread")
					+ " SET replynum=replynum-" + pnum + " WHERE tid IN (" + Common.sImplode(pnum1.get(pnum))
					+ ")");
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("post")
				+ " WHERE pid IN (" + Common.sImplode(newids) + ")");

		return delPosts;
	}

	
	public boolean deleteDoings(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object ids) {
		boolean allowmanage = Common.checkPerm(request, response, "managedoing");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		List<Map<String, Object>> doings = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> doingList = dataBaseService.executeQuery("SELECT doid, uid FROM "
				+ JavaCenterHome.getTableName("doing") + " WHERE doid IN (" + Common.sImplode(ids) + ")");
		for (Map<String, Object> value : doingList) {
			int uid = (Integer) value.get("uid");
			if (allowmanage || uid == supe_uid) {
				doings.add(value);
				if (!managebatch && uid != supe_uid) {
					delnum++;
				}
			}
		}
		if (doings.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}
		Map<String, Integer> reward = Common.getReward("deldoing", false, 0, "", true, request, response);
		int credit = reward.get("credit");
		int experience = reward.get("experience");
		List<Object> newdoids = new ArrayList<Object>();
		for (Map<String, Object> value : doings) {
			int uid = (Integer) value.get("uid");
			newdoids.add(value.get("doid"));
			if (allowmanage && uid != supe_uid) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + credit + ", experience=experience-" + experience
						+ " WHERE uid=" + uid);
			}
		}
		String newIds = Common.sImplode(newdoids);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("doing")
				+ " WHERE doid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("docomment")
				+ " WHERE doid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE id IN ("
				+ newIds + ") AND idtype='doid'");
		return true;
	}

	
	public boolean deleteShares(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object sids) {
		boolean allowmanage = Common.checkPerm(request, response, "manageshare");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		List<Map<String, Object>> shares = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> shareList = dataBaseService.executeQuery("SELECT sid, uid FROM "
				+ JavaCenterHome.getTableName("share") + " WHERE sid IN (" + Common.sImplode(sids) + ")");
		for (Map<String, Object> value : shareList) {
			int uid = (Integer) value.get("uid");
			if (allowmanage || uid == supe_uid) {
				shares.add(value);
				if (!managebatch && uid != supe_uid) {
					delnum++;
				}
			}
		}
		if (shares.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}
		Map<String, Integer> reward = Common.getReward("delshare", false, 0, "", true, request, response);
		int credit = reward.get("credit");
		int experience = reward.get("experience");
		List<Object> newSids = new ArrayList<Object>();
		for (Map<String, Object> value : shares) {
			int uid = (Integer) value.get("uid");
			newSids.add(value.get("sid"));
			if (allowmanage && uid != supe_uid) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + credit + ", experience=experience-" + experience
						+ " WHERE uid=" + uid);
			}
		}
		String newIds = Common.sImplode(newSids);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("share")
				+ " WHERE sid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("comment")
				+ " WHERE id IN (" + newIds + ") AND idtype='sid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE id IN ("
				+ newIds + ") AND idtype='sid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
				+ " WHERE id IN (" + newIds + ") AND idtype='sid'");
		return true;
	}

	
	public boolean deletePolls(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object pids) {
		boolean allowmanage = Common.checkPerm(request, response, "managepoll");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		List<Map<String, Object>> polls = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> pollList = dataBaseService.executeQuery("SELECT pid, uid, credit FROM "
				+ JavaCenterHome.getTableName("poll") + " WHERE pid IN (" + Common.sImplode(pids) + ")");
		for (Map<String, Object> poll : pollList) {
			int uid = (Integer) poll.get("uid");
			if (allowmanage || uid == supe_uid) {
				polls.add(poll);
				if (!managebatch && uid != supe_uid) {
					delnum++;
				}
			}
		}
		if (polls.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}
		Map<String, Integer> reward = Common.getReward("delpoll", false, 0, "", true, request, response);
		int credit = reward.get("credit");
		int experience = reward.get("experience");
		List<Object> newPids = new ArrayList<Object>();
		for (Map<String, Object> poll : polls) {
			int uid = (Integer) poll.get("uid");
			newPids.add(poll.get("pid"));
			if (allowmanage && uid != supe_uid) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + credit + ", experience=experience-" + experience
						+ " WHERE uid=" + uid);
			}
			int pollCredit = (Integer) poll.get("credit");
			if (pollCredit > 0) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit+" + pollCredit + " WHERE uid=" + uid);
			}
		}
		String newIds = Common.sImplode(newPids);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("poll")
				+ " WHERE pid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("pollfield")
				+ " WHERE pid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("polloption")
				+ " WHERE pid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("polluser")
				+ " WHERE pid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("comment")
				+ " WHERE id IN (" + newIds + ") AND idtype='pid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE id IN ("
				+ newIds + ") AND idtype='pid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
				+ " WHERE id IN (" + newIds + ") AND idtype='pid'");
		return true;
	}

	
	public boolean deleteTags(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object tagIds) {
		if (!Common.checkPerm(request, response, "managetag")) {
			return false;
		}
		String newIds = Common.sImplode(tagIds);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagblog")
				+ " WHERE tagid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tag")
				+ " WHERE tagid IN (" + newIds + ")");
		return true;
	}

	
	public boolean deleteMtag(HttpServletRequest request, HttpServletResponse response, Object tagIds) {
		if (!Common.checkPerm(request, response, "manageprofield")
				&& !Common.checkPerm(request, response, "managemtag")) {
			return false;
		}
		String sql = "SELECT tagid FROM " + JavaCenterHome.getTableName("mtag") + " WHERE tagid IN ("
				+ Common.sImplode(tagIds) + ")";
		List<String> newTagIds = dataBaseService.executeQuery(sql, 1);
		if (newTagIds.isEmpty()) {
			return false;
		}
		String newIds = Common.sImplode(newTagIds);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagspace")
				+ " WHERE tagid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mtag")
				+ " WHERE tagid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("thread")
				+ " WHERE tagid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("post")
				+ " WHERE tagid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mtaginvite")
				+ " WHERE tagid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
				+ " WHERE id IN (" + newIds + ") AND idtype='tagid'");
		return true;
	}

	
	public boolean deleteEvents(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Object eventIds) {
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		boolean allowmanage = Common.checkPerm(request, response, "manageevent");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");
		int delnum = 0;
		List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT eventid, uid, title FROM "
				+ JavaCenterHome.getTableName("event") + " WHERE eventid IN (" + Common.sImplode(eventIds)
				+ ")");
		for (Map<String, Object> value : eventList) {
			int uid = (Integer) value.get("uid");
			if (allowmanage || uid == supe_uid) {
				events.add(value);
				if (!managebatch && uid != supe_uid) {
					delnum++;
				}
			}
		}

		if (events.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}
		Map<String, Integer> reward = Common.getReward("delpoll", false, 0, "", true, request, response);
		int credit = reward.get("credit");
		int experience = reward.get("experience");
		String supe_username = (String) sGlobal.get("supe_username");
		int timestamp = (Integer) sGlobal.get("timestamp");
		List<Object> newEvenTids = new ArrayList<Object>();
		List<Integer> note_ids = new ArrayList<Integer>();
		List<String> note_inserts = new ArrayList<String>();
		for (Map<String, Object> value : events) {
			int uid = (Integer) value.get("uid");
			newEvenTids.add(value.get("eventid"));
			if (uid != supe_uid) {
				if (allowmanage) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET credit=credit-" + credit + ", experience=experience-" + experience
							+ " WHERE uid=" + uid);
				}
				note_ids.add(uid);
				String note_msg = Common.getMessage(request, "cp_event_set_delete", (String) value
						.get("title"));
				note_inserts.add("('" + uid + "', 'event', '1', '" + supe_uid + "', '" + supe_username
						+ "', '" + Common.addSlashes(note_msg) + "', '" + timestamp + "')");
			}
		}
		String newIds = Common.sImplode(newEvenTids);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("event")
				+ " WHERE eventid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("eventpic")
				+ " WHERE eventid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("eventinvite")
				+ " WHERE eventid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("userevent")
				+ " WHERE eventid IN (" + newIds + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("comment")
				+ " WHERE id IN (" + newIds + ") AND idtype='eventid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE id IN ("
				+ newIds + ") AND idtype='eventid'");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
				+ " WHERE id IN (" + newIds + ") AND idtype='eventid'");
		if (note_inserts.size() > 0) {
			dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("notification")
					+ " (uid, type, new, authorid, author,note, dateline) VALUES "
					+ Common.implode(note_inserts, ","));
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET notenum=notenum+1 WHERE uid IN (" + Common.sImplode(note_ids) + ")");
		}
		return true;
	}

	
	public boolean deleteSpace(HttpServletRequest request, HttpServletResponse response, int uid,
			boolean force) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		String supe_username = (String) sGlobal.get("supe_username");
		int timestamp = (Integer) sGlobal.get("timestamp");
		boolean allowmanage = Common.checkPerm(request, response, "managedelspace");
		Map<String, Object> delspace = null;
		List<Map<String, Object>> spaceList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("space") + " WHERE uid=" + uid);
		for (Map<String, Object> value : spaceList) {
			if (force || allowmanage && uid != supe_uid) {
				delspace = value;
				if (!force) {
					dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("spacelog")
							+ " (uid,username,opuid,opusername,flag,dateline) VALUES (" + uid + ",'"
							+ Common.sAddSlashes(value.get("username")) + "'," + supe_uid + ",'"
							+ supe_username + "',-1," + timestamp + ")");
				}
			}
		}

		if (delspace == null) {
			return false;
		}

		Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
		if(member!=null){
			Map<String, Object> usergroup = Common.getCacheDate(request, response, "/data/cache/usergroup_"
					+ member.get("groupid") + ".jsp", "usergroup" + member.get("groupid"));
			usergroup.put("managebatch", 1);	
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("space") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("spacefield")
				+ " WHERE uid=" + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed") + " WHERE uid="
				+ uid + " OR (id=" + uid + " AND idtype='uid')");
		List<String> doids = dataBaseService.executeQuery("SELECT doid FROM "
				+ JavaCenterHome.getTableName("doing") + " WHERE uid=" + uid, 1);

		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("doing") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("docomment")
				+ " WHERE doid IN (" + Common.sImplode(doids) + ") OR uid=" + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("share") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("album") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("creditlog")
				+ " WHERE uid=" + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("notification")
				+ " WHERE (uid=" + uid + " OR authorid=" + uid + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("poke") + " WHERE (uid="
				+ uid + " OR fromuid=" + uid + ")");
		List<String> pollIds = dataBaseService.executeQuery("SELECT pid FROM "
				+ JavaCenterHome.getTableName("poll") + " WHERE uid=" + uid, 1);
		deletePolls(request, response, supe_uid, pollIds);
		pollIds = dataBaseService.executeQuery("SELECT pid FROM " + JavaCenterHome.getTableName("polluser")
				+ " WHERE uid=" + uid, 1);
		if (pollIds.size() > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("poll")
					+ " SET voternum=voternum-1 WHERE pid IN (" + Common.sImplode(pollIds) + ")");
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("polluser")
				+ " WHERE uid=" + uid);
		List<String> eventIds = dataBaseService.executeQuery("SELECT eventid FROM "
				+ JavaCenterHome.getTableName("event") + " WHERE uid = " + uid, 1);
		deleteEvents(request, response, sGlobal, eventIds);
		List<Integer> ids = new ArrayList<Integer>();
		List<Integer> ids1 = new ArrayList<Integer>();
		List<Integer> ids2 = new ArrayList<Integer>();
		List<Map<String, Object>> userEvent = dataBaseService.executeQuery("SELECT eventid,status FROM "
				+ JavaCenterHome.getTableName("userevent") + " WHERE uid = " + uid);
		for (Map<String, Object> value : userEvent) {
			int status = (Integer) value.get("status");
			int eventId = (Integer) value.get("eventid");
			if (status == 1) {
				ids1.add(eventId);
			} else if (status > 1) {
				ids2.add(eventId);
			}
			ids.add(eventId);
		}
		if (ids1.size() > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
					+ " SET follownum = follownum - 1 WHERE eventid IN (" + Common.sImplode(ids1) + ")");
		}
		if (ids2.size() > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
					+ " SET membernum = membernum - 1 WHERE eventid IN (" + Common.sImplode(ids2) + ")");
		}
		if (ids.size() > 0) {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("userevent")
					+ " WHERE eventid IN (" + Common.sImplode(ids) + ") AND uid = " + uid);
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("eventinvite")
				+ " WHERE uid = " + uid + " OR touid = " + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("eventpic")
				+ " WHERE uid = " + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("usermagic")
				+ " WHERE uid = " + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("magicinlog")
				+ " WHERE uid = " + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("magicuselog")
				+ " WHERE uid = " + uid);
		List<Map<String, Object>> pics = dataBaseService.executeQuery("SELECT filepath,thumb,remote FROM "
				+ JavaCenterHome.getTableName("pic") + " WHERE uid=" + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("pic") + " WHERE uid = "
				+ uid);
		List<String> blogIds = dataBaseService.executeQuery("SELECT blogid FROM "
				+ JavaCenterHome.getTableName("blog") + " WHERE uid=" + uid, 1);
		for (String blogId : blogIds) {
			List<String> tagIds = dataBaseService.executeQuery("SELECT DISTINCT tagid FROM "
					+ JavaCenterHome.getTableName("tagblog") + " WHERE blogid=" + blogId, 1);
			if (tagIds.size() > 0) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tag")
						+ " SET blognum=blognum-1 WHERE tagid IN (" + Common.sImplode(tagIds) + ")");
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagblog")
						+ " WHERE blogid = " + blogId);
			}
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("blog") + " WHERE uid = "
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("blogfield")
				+ " WHERE uid = " + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("comment")
				+ " WHERE (uid=" + uid + " OR authorid=" + uid + " OR (id=" + uid + " AND idtype='uid'))");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("visitor")
				+ " WHERE (uid=" + uid + " OR vuid=" + uid + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("usertask")
				+ " WHERE uid=" + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("class") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("friend") + " WHERE (uid="
				+ uid + " OR fuid=" + uid + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("member") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("clickuser")
				+ " WHERE uid=" + uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("blacklist")
				+ " WHERE (uid=" + uid + " OR buid=" + uid + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("invite") + " WHERE (uid="
				+ uid + " OR fuid=" + uid + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mailcron") + ", "
				+ JavaCenterHome.getTableName("mailqueue") + " USING "
				+ JavaCenterHome.getTableName("mailcron") + ", " + JavaCenterHome.getTableName("mailqueue")
				+ " WHERE " + JavaCenterHome.getTableName("mailcron") + ".touid=" + uid + " AND "
				+ JavaCenterHome.getTableName("mailcron") + ".cid="
				+ JavaCenterHome.getTableName("mailqueue") + ".cid");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("myinvite")
				+ " WHERE (touid=" + uid + " OR fromuid=" + uid + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("userapp") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("userappfield")
				+ " WHERE uid=" + uid);
		List<Integer> temp = null;
		Map<Integer, List<Integer>> tids = new HashMap<Integer, List<Integer>>();
		List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT tid, tagid FROM "
				+ JavaCenterHome.getTableName("thread") + " WHERE uid=" + uid);
		for (Map<String, Object> value : threadList) {
			int tid = (Integer) value.get("tid");
			int tagid = (Integer) value.get("tagid");
			temp = tids.get(tagid);
			if (temp == null) {
				temp = new ArrayList<Integer>();
				tids.put(tagid, temp);
			}
			temp.add(tid);
		}
		Set<Integer> tagIds = tids.keySet();
		for (Integer tagId : tagIds) {
			deleteThreads(request, response, supe_uid, tagId, tids.get(tagId));
		}
		Map<Integer, List<Integer>> pids = new HashMap<Integer, List<Integer>>();
		List<Map<String, Object>> postList = dataBaseService.executeQuery("SELECT pid, tagid FROM "
				+ JavaCenterHome.getTableName("post") + " WHERE uid=" + uid);
		for (Map<String, Object> value : postList) {
			int pid = (Integer) value.get("pid");
			int tagid = (Integer) value.get("tagid");
			temp = pids.get(tagid);
			if (temp == null) {
				temp = new ArrayList<Integer>();
				pids.put(tagid, temp);
			}
			temp.add(pid);
		}
		tagIds = pids.keySet();
		for (Integer tagId : tagIds) {
			deleteThreads(request, response, supe_uid, tagId, pids.get(tagId));
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("thread") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("post") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("session") + " WHERE uid="
				+ uid);
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("show") + " WHERE uid="
				+ uid);
		List<String> mTagIds = dataBaseService.executeQuery("SELECT DISTINCT tagid FROM "
				+ JavaCenterHome.getTableName("tagspace") + " WHERE uid=" + uid, 1);
		if (mTagIds.size() > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag")
					+ " SET membernum=membernum-1 WHERE tagid IN (" + Common.sImplode(mTagIds) + ")");
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagspace")
					+ " WHERE uid=" + uid);
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mtaginvite")
				+ " WHERE (uid=" + uid + " OR fromuid=" + uid + ")");
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report") + " WHERE id="
				+ uid + " AND idtype='uid'");
		if (!Common.empty(sConfig.get("my_status"))) {
			dataBaseService
					.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("userlog")
							+ " (uid,action,dateline) VALUES (" + uid + ",'delete',"
							+ sGlobal.get("timestamp") + ")");
		}
		deletePicFiles(request, response, pics);
		return true;
	}

	
	public boolean deleteFeeds(HttpServletRequest request, HttpServletResponse response, int supe_uid,
			Object feedIds) {
		boolean allowmanage = Common.checkPerm(request, response, "managefeed");
		boolean managebatch = Common.checkPerm(request, response, "managebatch");

		int delnum = 0;
		List<Map<String, Object>> feedList = dataBaseService
				.executeQuery("SELECT feedid, uid FROM " + JavaCenterHome.getTableName("feed")
						+ " WHERE feedid IN (" + Common.sImplode(feedIds) + ")");
		List<Object> newFeedIds = new ArrayList<Object>();
		for (Map<String, Object> value : feedList) {
			int uid = (Integer) value.get("uid");
			if (allowmanage || uid == supe_uid) {
				newFeedIds.add(value.get("feedid"));
				if (!managebatch && uid != supe_uid) {
					delnum++;
				}
			}
		}
		if (newFeedIds.isEmpty() || (!managebatch && delnum > 1)) {
			return false;
		}
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("feed")
				+ " WHERE feedid IN (" + Common.sImplode(newFeedIds) + ")");
		return true;
	}

	
	private void deletePicFiles(HttpServletRequest request, HttpServletResponse response,
			List<Map<String, Object>> pics) {
		String jchRoot = JavaCenterHome.jchRoot;
		String attachDir = JavaCenterHome.jchConfig.get("attachDir");
		List<Map<String, Object>> remotes = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> pic : pics) {
			if ((Integer) pic.get("remote") > 0) {
				remotes.add(pic);
			} else {
				String filePath = jchRoot + attachDir + "/" + pic.get("filepath");
				File file = new File(filePath);
				if (file.exists() && !file.delete()) {
					FileHelper.writeLog(request, "PIC", "Delete pic file '" + filePath + "' error.");
				}
				if ((Integer) pic.get("thumb") > 0) {
					filePath = filePath + ".thumb.jpg";
					file = new File(filePath);
					if (file.exists() && !file.delete()) {
						FileHelper.writeLog(request, "PIC", "Delete pic file '" + filePath + "' error.");
					}
				}
			}
		}
		if (remotes.size() > 0) {
			Common.getCacheDate(request, response, "/data/cache/cache_setting.jsp", "globalSetting");
			FtpUtil ftpUtil = new FtpUtil();
			int ftpconn = ftpUtil.sftp_connect(request);
			for (Map<String, Object> pic : remotes) {
				String filePath = (String) pic.get("filepath");
				if (ftpconn > 0) {
					if (!ftpUtil.sftp_delete(request, filePath)) {
						FileHelper.writeLog(request, "FTP", "Delete pic file '" + filePath + "' error.");
					}
					if ((Integer) pic.get("thumb") > 0
							&& !ftpUtil.sftp_delete(request, filePath + ".thumb.jpg")) {
						FileHelper.writeLog(request, "FTP", "Delete pic file '" + filePath
								+ ".thumb.jpg' error.");
					}
				} else {
					FileHelper.writeLog(request, "FTP", "Delete pic file '" + filePath + "' error.");
					if ((Integer) pic.get("thumb") > 0) {
						FileHelper.writeLog(request, "FTP", "Delete pic file '" + filePath
								+ ".thumb.jpg' error.");
					}
				}
			}
			ftpUtil.sftp_close();
		}

	}

	
	private String getAlbumPic(int uid, int albumId) {
		List<Map<String, Object>> albumPics = dataBaseService.executeQuery("SELECT filepath, thumb FROM "
				+ JavaCenterHome.getTableName("pic") + " WHERE albumid=" + albumId + " AND uid=" + uid
				+ " ORDER BY thumb DESC, dateline DESC LIMIT 0,1");
		if (albumPics.size() > 0) {
			Map<String, Object> albumPic = albumPics.get(0);
			return albumPic.get("filepath") + ((Integer) albumPic.get("thumb") > 0 ? ".thumb.jpg" : "");
		} else {
			return "";
		}
	}
	public boolean deletetopics(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Object ids) {
		List<Integer> newids = null;
		boolean managetopic = Common.checkPerm(request, response, "managetopic");
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("topic") + " WHERE topicid IN (" + Common.sImplode(ids) + ")");
		for (Map<String, Object> value : query) {
			if (managetopic || value.get("uid").equals(sGlobal.get("supe_uid"))) {
				if (newids == null) {
					newids = new ArrayList<Integer>();
				}
				newids.add((Integer) value.get("topicid"));
			}
		}
		if (newids != null) {
			dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("topic")
					+ " WHERE topicid IN (" + Common.sImplode(newids) + ")");
			return true;
		} else {
			return false;
		}
	}
}