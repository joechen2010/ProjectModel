
package cn.jcenterhome.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.struts.upload.FormFile;

import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.FtpUtil;
import cn.jcenterhome.util.ImageUtil;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.vo.MessageVO;

public class CpService {
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");

	
	public int addFeed(Map<String, Object> sGlobal, String icon, String title_template, Map title_data,
			String body_template, Map body_data, String body_general, String[] images, String[] image_links,
			String target_ids, int friend, int appid, int id, String idType, boolean returnId) {
		title_data = title_data == null ? new HashMap() : title_data;
		body_data = body_data == null ? new HashMap() : body_data;
		if (Common.empty(appid)) {
			if (!Common.isNumeric(icon)) {
				appid = Common.intval(JavaCenterHome.jchConfig.get("JC_APPID"));
			}
		}
		Map<String, Object> feedarr = new HashMap<String, Object>();
		feedarr.put("appid", appid);
		feedarr.put("icon", icon);
		feedarr.put("uid", sGlobal.get("supe_uid"));
		feedarr.put("username", sGlobal.get("supe_username"));
		feedarr.put("dateline", sGlobal.get("timestamp"));
		feedarr.put("title_template", title_template);
		feedarr.put("body_template", body_template);
		feedarr.put("body_general", body_general);
		if (images != null) {
			int imagesLength = images.length;
			for (int i = 0; i < imagesLength; i++) {
				feedarr.put("image_" + (i + 1), images[i]);
			}
		}
		if (image_links != null) {
			int imageLinksLength = image_links.length;
			for (int i = 0; i < imageLinksLength; i++) {
				feedarr.put("image_" + (i + 1) + "_link", image_links[i]);
			}
		}
		feedarr.put("target_ids", target_ids);
		feedarr.put("friend", friend);
		feedarr.put("id", id);
		feedarr.put("idtype", idType);
		feedarr = (Map) Common.sStripSlashes(feedarr);
		feedarr.put("title_data", Serializer.serialize(Common.sStripSlashes(title_data)));
		feedarr.put("body_data", Serializer.serialize(Common.sStripSlashes(body_data)));
		feedarr.put("hash_template", Common.md5(feedarr.get("title_template") + "\t"
				+ feedarr.get("body_template")));
		feedarr.put("hash_data", Common.md5(feedarr.get("title_template") + "\t" + feedarr.get("title_data")
				+ "\t" + feedarr.get("body_template") + "\t" + feedarr.get("body_data")));
		feedarr = (Map) Common.sAddSlashes(feedarr);
		List<Map<String, Object>> feedList = dataBaseService.executeQuery("SELECT feedid FROM "
				+ JavaCenterHome.getTableName("feed") + " WHERE uid='" + feedarr.get("uid")
				+ "' AND hash_data='" + feedarr.get("hash_data") + "' LIMIT 0,1");
		if (feedList.size() > 0) {
			Set<String> keys = feedarr.keySet();
			StringBuffer updateStr = new StringBuffer();
			for (String key : keys) {
				updateStr.append(key + "='" + feedarr.get(key) + "',");
			}
			String sql = "UPDATE " + JavaCenterHome.getTableName("feed") + " SET "
					+ updateStr.substring(0, updateStr.length() - 1) + " WHERE feedid='"
					+ feedList.get(0).get("feedid") + "'";
			dataBaseService.executeUpdate(sql);
			return 0;
		}
		StringBuffer insertKey = new StringBuffer();
		StringBuffer insertValue = new StringBuffer();
		Set<String> keys = feedarr.keySet();
		for (String key : keys) {
			insertKey.append(key + ",");
			insertValue.append("'" + feedarr.get(key) + "',");
		}
		String sql = "INSERT INTO " + JavaCenterHome.getTableName("feed") + " ("
				+ insertKey.substring(0, insertKey.length() - 1) + ") VALUES ("
				+ insertValue.substring(0, insertValue.length() - 1) + ")";
		if (returnId) {
			return dataBaseService.insert(sql);
		} else {
			dataBaseService.executeUpdate(sql);
			return 1;
		}
	}

	
	public String getTablebyIdType(String idtype) {
		String tableName = null;
		if ("blogid".equals(idtype)) {
			tableName = "blog";
		} else if ("tid".equals(idtype)) {
			tableName = "thread";
		} else if ("picid".equals(idtype)) {
			tableName = "pic";
		} else if ("eventid".equals(idtype)) {
			tableName = "event";
		} else if ("sid".equals(idtype)) {
			tableName = "share";
		} else if ("pid".equals(idtype)) {
			tableName = "poll";
		}
		return tableName;
	}

	
	public String getVideoPic(String fileName) {
		String path = "data/video/";
		if (fileName == null) {
			path += "novideo.gif";
		} else {
			String dir1 = fileName.substring(0, 1);
			String dir2 = fileName.substring(1, 2);
			path += dir1 + "/" + dir2 + "/" + fileName + ".jpg";
		}
		return path;
	}

	
	public void sendEmailCheck(HttpServletRequest request, HttpServletResponse response, int uid, String email)
			throws Exception {
		if (uid > 0 && !Common.empty(email)) {
			String hash = Common.authCode(uid + "\t" + email, "ENCODE", null, 0);
			String url = Common.getSiteUrl(request) + "do.jsp?ac=emailcheck&amp;hash="
					+ Common.urlEncode(hash);
			String mailSubject = Common.getMessage(request, "cp_active_email_subject");
			String mailMessage = Common.getMessage(request, "cp_active_email_msg", url);
			sendMail(request, response, 0, email, mailSubject, mailMessage, null);
		}
	}

	
	public boolean sendMail(HttpServletRequest request, HttpServletResponse response, int touid,
			String email, String subject, String message, String mailtype) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		int timestamp = (Integer) sGlobal.get("timestamp");
		int cid = 0;
		if (touid > 0 && !Common.empty(sConfig.get("sendmailday"))) {
			Map<String, Object> tospace = Common.getSpace(request, sGlobal, sConfig, touid);
			if (Common.empty(tospace)) {
				return false;
			}
			Map sendmail = Serializer.unserialize((String) tospace.get("sendmail"), false);

			if (!Common.empty(tospace.get("emailcheck"))
					&& !Common.empty(tospace.get("email"))
					&& timestamp - (Integer) tospace.get("lastlogin") > Double.parseDouble(sConfig.get(
							"sendmailday").toString()) * 86400
					&& (Common.empty(sendmail) || !Common.empty(sendmail.get(mailtype)))) {
				if (Common.empty(tospace.get("lastsend"))) {
					tospace.put("lastsend", timestamp);
				}
				if ((Integer) sendmail.get("frequency") == null) {
					sendmail.put("frequency", 604800);
				}
				int sendtime = (Integer) tospace.get("lastsend") + (Integer) sendmail.get("frequency");
				List<Map<String, Object>> mailcronList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("mailcron") + " WHERE touid=" + touid + " LIMIT 1");
				if (mailcronList.size() > 0) {
					Map<String, Object> value = mailcronList.get(0);
					cid = (Integer) value.get("cid");
					sendtime = (Integer) value.get("sendtime") < sendtime ? (Integer) value.get("sendtime")
							: sendtime;
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mailcron")
							+ " SET email='" + Common.addSlashes((String) tospace.get("email")) + "',sendtime="
							+ sendtime + " WHERE cid=" + cid);
				} else {
					cid = dataBaseService.insert("INSERT INTO " + JavaCenterHome.getTableName("mailcron")
							+ " (touid,email,sendtime) VALUES (" + touid + ",'"
							+ Common.addSlashes((String) tospace.get("email")) + "'," + sendtime + ")");
				}
			}
		} else if (!Common.empty(email)) {
			email = Common.getStr(email, 80, true, true, false, 0, 0, request, response);
			List<String> mailcronList = dataBaseService.executeQuery("SELECT cid FROM "
					+ JavaCenterHome.getTableName("mailcron") + " WHERE email='" + email + "' LIMIT 1", 1);
			if (mailcronList.size() > 0) {
				cid = Integer.valueOf(mailcronList.get(0));
			} else {
				cid = dataBaseService.insert("INSERT INTO " + JavaCenterHome.getTableName("mailcron")
						+ " (email) VALUES ('" + email + "')");
			}
		}

		if (cid > 0) {
			message = message == null ? "" : message;
			mailtype = mailtype == null ? "" : mailtype;
			subject = Common.addSlashes(Common.stripSlashes(subject));
			message = Common.addSlashes(Common.stripSlashes(message));
			dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("mailqueue")
					+ " (cid,subject,message,dateline) VALUES (" + cid + ",'" + subject + "','" + message
					+ "'," + timestamp + ")");
		}
		return true;
	}

	
	public String videoPicUpload(FormFile formFile, int uid, int timestamp) {
		if (formFile == null || uid <= 0) {
			return null;
		}
		if (formFile.getFileSize() > 0) {
			String newfilename = Common.md5(String.valueOf(timestamp).substring(0, 7) + uid);
			String dir1 = newfilename.substring(0, 1);
			String dir2 = newfilename.substring(1, 2);
			File file = new File(JavaCenterHome.jchRoot + "/data/video/" + dir1 + "/" + dir2);
			if (!file.isDirectory() && !file.mkdirs()) {
				return null;
			}
			String newName = JavaCenterHome.jchRoot + "/" + getVideoPic(newfilename);
			file = new File(newName);
			if (file.exists()) {
				file.delete();
			}
			uploadFile(formFile, newName);
			return newfilename;
		} else {
			return null;
		}
	}

	
	private void uploadFile(FormFile formfile, String targetpath) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new BufferedInputStream(formfile.getInputStream(), 4096);
			os = new BufferedOutputStream(new FileOutputStream(targetpath), 4096);
			int count = 0;
			byte[] buffer = new byte[4096];
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
			buffer = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		is = null;
		formfile = null;
	}

	
	public boolean checkRealName(HttpServletRequest request, String type) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
		boolean realNameDisable = Common.empty(sConfig.get("realname"));
		boolean nameStatusEmpty = member == null || Common.empty(member.get("namestatus"));
		boolean useAuthorized = Common.empty(sConfig.get("name_allow" + type));
		if (realNameDisable || !nameStatusEmpty || !useAuthorized) {
			return true;
		}
		return false;
	}

	
	public boolean checkVideoPhoto(HttpServletRequest request, HttpServletResponse response, String type,
			int privacyStatus) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
		if (Common.empty(sConfig.get("videophoto")) || !Common.empty(member.get("videostatus"))) {
			return true;
		}
		if (privacyStatus == 0) {
			boolean videoPhotoIgnore = Common.checkPerm(request, response, "videophotoignore");
			boolean useAuthorized = Common.empty(sConfig.get("video_allow" + type));
			if (!videoPhotoIgnore && useAuthorized) {
				boolean allowViewVideoPic = Common.checkPerm(request, response, "allowviewvideopic");
				if (!type.equals("viewphoto") || type.equals("viewphoto") && !allowViewVideoPic) {
					return false;
				}
			}
		} else if (privacyStatus == 2) {
			return false;
		}
		return true;
	}

	
	public boolean checkVideoPhoto(HttpServletRequest request, HttpServletResponse response, String type,
			Map toSpace) {
		Map privacy = (Map) toSpace.get("privacy");
		Map view = (Map) privacy.get("view");
		int status = view.get("video" + type) == null ? 0 : (Integer) view.get("video" + type);
		return checkVideoPhoto(request, response, type, status);
	}

	
	public boolean checkVideoPhoto(HttpServletRequest request, HttpServletResponse response, String type) {
		return checkVideoPhoto(request, response, type, 0);
	}

	
	public int checkNewUser(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (Common.checkPerm(request, response, "spamignore")) {
			return 1;
		}
		int timestamp = (Integer) sGlobal.get("timestamp");
		int userDateline = (Integer) space.get("dateline");
		Integer newUserTime = (Integer) sConfig.get("newusertime");
		if (newUserTime != null && timestamp - userDateline < newUserTime * 3600) {
			return 2;
		}
		if (!Common.empty(sConfig.get("need_avatar")) && Common.empty(space.get("avatar"))) {
			return 3;
		}
		int userFriendNum = (Integer) space.get("friendnum");
		Integer needFriendNum = (Integer) sConfig.get("need_friendnum");
		if (needFriendNum != null && userFriendNum < needFriendNum) {
			return 4;
		}
		if (!Common.empty(sConfig.get("need_email")) && Common.empty(space.get("emailcheck"))) {
			return 5;
		}
		return 1;
	}

	
	public List<Map<String, Object>> getAlbums(int uid) {
		String sql = "SELECT * FROM " + JavaCenterHome.getTableName("album") + " WHERE uid='" + uid
				+ "' ORDER BY albumid DESC";
		List<Map<String, Object>> albumList = dataBaseService.executeQuery(sql);
		return albumList;
	}

	
	public int checkTopic(HttpServletRequest request, int topicID, String type) {
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<String, Object> topic = Common.getTopic(request, topicID);
		int newTopicID = topicID;
		if (topic.size() != 0) {
			if (!Common.empty(topic.get("joingid"))) {
				if (!Common.in_array((String[]) topic.get("joingid"), space.get("groupid"))) {
					newTopicID = 0;
				}
			}
			if (!Common.empty(topic.get("jointype"))) {
				if (!Common.in_array((String[]) topic.get("jointype"), type)) {
					newTopicID = 0;
				}
			}
		} else {
			newTopicID = 0;
		}
		return newTopicID;
	}

	
	public Object savePic(HttpServletRequest request, HttpServletResponse response, FileItem item,
			String albumID, String title, int topicID) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		if (Common.intval(albumID) < 0) {
			albumID = "0";
		}
		String[] allowPicType = {"jpg", "jpeg", "gif", "png"};
		long size = item.getSize();
		String maxFileSize = jchConfig.get("upload_max_filesize");
		long maxSize = Common.getByteSizeByBKMG(maxFileSize);
		if (maxSize <= 0) {
			maxSize = 1024 * 1024 * 1;
		}
		if (size == 0 || item.isFormField() || size > maxSize) {
			return Common.getMessage(request, "cp_lack_of_access_to_upload_file_size");
		}
		String fileExt = Common.fileext(item.getName()).toLowerCase();
		if (!Common.in_array(allowPicType, fileExt)) {
			return Common.getMessage(request, "cp_only_allows_upload_file_types");
		}
		String filePath = getFilePath(request, fileExt, true);
		if (filePath == null) {
			return Common.getMessage(request, "cp_unable_to_create_upload_directory_server");
		}
		if (Common.empty(space)) {
			space = Common.getSpace(request, sGlobal, sConfig, sGlobal.get("supe_uid"));
			request.setAttribute("space", space);
		}
		if (!Common.checkPerm(request, response, "allowupload")) {
			MessageVO msgVO = Common.ckSpaceLog(request);
			if (msgVO != null) {
				return Common.getMessage(request, msgVO.getMsgKey(), msgVO.getArgs());
			}
			return Common.getMessage(request, "cp_inadequate_capacity_space");
		}
		if (!checkRealName(request, "album")) {
			return Common.getMessage(request, "cp_inadequate_capacity_space");
		}
		if (!checkVideoPhoto(request, response, "album")) {
			return Common.getMessage(request, "cp_inadequate_capacity_space");
		}
		if (checkNewUser(request, response) != 1) {
			return Common.getMessage(request, "cp_inadequate_capacity_space");
		}
		int maxAttachSize = (Integer) Common.checkPerm(request, response, sGlobal, "maxattachsize");
		if (maxAttachSize != 0) {
			int attachSize = (Integer) space.get("attachsize");
			int addSize = (Integer) space.get("addsize");
			if (attachSize + size > maxAttachSize + addSize) {
				return Common.getMessage(request, "cp_inadequate_capacity_space");
			}
		}
		boolean showTip = true;
		int albumFriend = 0;
		int albumID_int = 0;
		if (!Common.empty(albumID)) {
			Pattern p = Pattern.compile("(?i)^new\\:(.+)$");
			Matcher m = p.matcher(albumID);
			String albumName = null;
			if (m.find()) {
				albumName = (String) Common.sHtmlSpecialChars(m.group(1).trim());
				if (albumName == null || albumName.length() == 0) {
					albumName = Common.sgmdate(request, "yyyyMMdd", (Integer) sGlobal.get("timestamp"));
				}
				Map<String, Object> arr = new HashMap<String, Object>();
				arr.put("albumname", albumName);
				arr.put("target_ids", "");
				albumID_int = createAlbum(request, arr);
			} else {
				albumID_int = Common.intval(albumID);
				if (albumID_int != 0) {
					List<Map<String, Object>> valueList = dataBaseService
							.executeQuery("SELECT albumname,friend FROM "
									+ JavaCenterHome.getTableName("album") + " WHERE albumid='" + albumID_int
									+ "' AND uid='" + sGlobal.get("supe_uid") + "'");
					if (valueList.size() > 0) {
						Map<String, Object> value = valueList.get(0);
						albumName = Common.addSlashes((String) value.get("albumname"));
						albumFriend = (Integer) value.get("friend");
					} else {
						albumName = Common.sgmdate(request, "yyyyMMdd", (Integer) sGlobal.get("timestamp"));
						Map<String, Object> arr = new HashMap<String, Object>();
						arr.put("albumname", albumName);
						arr.put("target_ids", "");
						albumID_int = createAlbum(request, arr);
					}
				}
			}
		} else {
			showTip = false;
		}
		String newName = JavaCenterHome.jchRoot + jchConfig.get("attachDir") + "./" + filePath;
		File uploadFile = new File(newName);
		try {
			item.write(uploadFile);
		} catch (Exception e) {
			return Common.getMessage(request, "cp_mobile_picture_temporary_failure");
		}
		String imgType = Common.getImageType(uploadFile);
		if (Common.empty(imgType)) {
			uploadFile.delete();
			return Common.getMessage(request, "cp_only_allows_upload_file_types");
		}
		String thumbPath = ImageUtil.makeThumb(request, response, newName);
		int thumb = thumbPath != null ? 1 : 0;
		if (!Common.empty(sConfig.get("allowwatermark"))) {
			ImageUtil.makeWaterMark(request, response, newName);
		}
		long fileSize = uploadFile.length();
		int picRemote = 0;
		int albumPicFlag = 0;
		if (!Common.empty(sConfig.get("allowftp"))) {
			FtpUtil ftpUtil = new FtpUtil();
			if (ftpUtil.ftpUpload(request, newName, filePath)) {
				picRemote = 1;
				albumPicFlag = 2;
			} else {
				uploadFile.delete();
				new File(newName + ".thumb.jpg").delete();
				FileHelper.writeLog(request, "ftp", "Ftp Upload '" + newName + "' failed.");
				return Common.getMessage(request, "cp_ftp_upload_file_size");
			}
		} else {
			picRemote = 0;
			albumPicFlag = 1;
		}
		try {
			title = Common.getStr(title, 200, true, true, true, 0, 0, request, response);
		} catch (Exception e) {
			return e.getMessage();
		}
		Map<String, Object> setArr = new HashMap<String, Object>();
		setArr.put("albumid", albumID_int);
		setArr.put("uid", sGlobal.get("supe_uid"));
		setArr.put("username", sGlobal.get("supe_username"));
		setArr.put("dateline", sGlobal.get("timestamp"));
		setArr.put("filename", Common.addSlashes(item.getName()));
		setArr.put("postip", Common.getOnlineIP(request));
		setArr.put("title", title);
		setArr.put("type", Common.addSlashes(imgType));
		setArr.put("size", fileSize);
		setArr.put("filepath", filePath);
		setArr.put("thumb", thumb);
		setArr.put("remote", picRemote);
		setArr.put("topicid", topicID);
		setArr.put("picid", dataBaseService.insertTable("pic", setArr, true, false));
		String setSql = "";
		if (showTip) {
			Map<String, Integer> reward = Common.getReward("uploadimage", false, 0, "", true, request,
					response);
			if (reward.get("credit") != 0) {
				setSql = ",credit=credit+" + reward.get("credit");
			}
			if (reward.get("experience") != 0) {
				setSql += ",experience=experience+" + reward.get("experience");
			}
		}
		dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
				+ " SET attachsize=attachsize+'" + fileSize + "', updatetime='"
				+ sGlobal.get("timestamp") + "' " + setSql + " WHERE uid='" + sGlobal.get("supe_uid") + "'");
		if (albumID_int != 0) {
			String file = filePath + (thumb != 0 ? ".thumb.jpg" : "");
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("album")
					+ " SET picnum=picnum+1, updatetime='" + sGlobal.get("timestamp") + "', pic='" + file
					+ "', picflag='" + albumPicFlag + "' WHERE albumid='" + albumID_int + "'");
		}
		updateStat(request, "pic", false);
		return setArr;
	}

	
	public boolean updateStat(HttpServletRequest request, String type, boolean primary) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		if (Common.empty(sGlobal.get("supe_uid")) || Common.empty(sConfig.get("updatestat"))) {
			return false;
		}
		String nowDayTime = Common.sgmdate(request, "yyyyMMdd", (Integer) sGlobal.get("timestamp"));
		Map<String, Object> setArr = null;
		if (primary) {
			setArr = new HashMap<String, Object>();
			setArr.put("uid", sGlobal.get("supe_uid"));
			setArr.put("daytime", nowDayTime);
			setArr.put("type", type);
			if (Common.intval(Common.getCount("statuser", setArr, null)) != 0) {
				return false;
			} else {
				dataBaseService.insertTable("statuser", setArr, false, false);
			}
		}
		setArr = new HashMap<String, Object>();
		setArr.put("daytime", nowDayTime);
		if (Common.intval(Common.getCount("stat", setArr, null)) != 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("stat") + " SET `" + type
					+ "`=`" + type + "`+1 WHERE daytime='" + nowDayTime + "'");
		} else {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("statuser")
					+ " WHERE daytime != '" + nowDayTime + "'");
			setArr = new HashMap<String, Object>();
			setArr.put("daytime", nowDayTime);
			setArr.put(type, "1");
			dataBaseService.insertTable("stat", setArr, false, false);
		}
		return true;
	}

	
	public int createAlbum(HttpServletRequest request, Map<String, Object> arr) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> whereArr = new HashMap<String, Object>();
		whereArr.put("albumname", arr.get("albumname"));
		whereArr.put("uid", sGlobal.get("supe_uid"));
		int albumID = Common.intval(Common.getCount("album", whereArr, "albumid"));
		if (albumID != 0) {
			return albumID;
		} else {
			arr.put("uid", sGlobal.get("supe_uid"));
			arr.put("username", sGlobal.get("supe_username"));
			arr.put("dateline", sGlobal.get("timestamp"));
			arr.put("updatetime", sGlobal.get("timestamp"));
			albumID = dataBaseService.insertTable("album", arr, true, false);
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET albumnum=albumnum+1 WHERE uid='" + sGlobal.get("supe_uid") + "'");
			return albumID;
		}
	}

	
	public String getFilePath(HttpServletRequest request, String fileExt, boolean mkDir) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		String filePath = sGlobal.get("supe_uid") + "_" + sGlobal.get("timestamp")
				+ Common.getRandStr(4, false) + "." + fileExt;
		String name1 = Common.gmdate("yyyyMM", (Integer) sGlobal.get("timestamp"), String.valueOf(sConfig
				.get("timeoffset")));
		String name2 = Common.gmdate("d", (Integer) sGlobal.get("timestamp"), String.valueOf(sConfig
				.get("timeoffset")));

		if (mkDir) {
			Map<String, String> jchConf = JavaCenterHome.jchConfig;
			File newFileName = new File(JavaCenterHome.jchRoot + jchConf.get("attachDir") + "./" + name1);
			if (!newFileName.isDirectory()) {
				if (!newFileName.mkdirs()) {
					FileHelper.writeLog(request, "error", "DIR: " + newFileName + " can not make");
					return filePath;
				}
			}
			newFileName = new File(JavaCenterHome.jchRoot + jchConf.get("attachDir") + "./" + name1 + "/"
					+ name2);
			if (!newFileName.isDirectory()) {
				if (!newFileName.mkdirs()) {
					FileHelper.writeLog(request, "error", "DIR: " + newFileName + " can not make");
					return name1 + "/" + filePath;
				}
			}
		}
		return name1 + "/" + name2 + "/" + filePath;
	}

	
	public Map<String, Object> getInvite(Map<String, Object> sGlobal, Map<String, Object> sConfig,
			Map<Integer, String> sNames, int uid, String code) {
		Map<String, Object> invits = null;
		if (uid > 0 && !Common.empty(code)) {
			List<Map<String, Object>> inviteList = dataBaseService
					.executeQuery("SELECT i.*, s.username, s.name, s.namestatus FROM "
							+ JavaCenterHome.getTableName("invite") + " i LEFT JOIN "
							+ JavaCenterHome.getTableName("space") + " s ON s.uid=i.uid WHERE i.uid=" + uid
							+ " AND i.code='" + code + "' AND i.fuid='0'");
			if (inviteList.size() > 0) {
				invits = inviteList.get(0);
				Common.realname_set(sGlobal, sConfig, sNames, uid, (String) invits.get("username"),
						(String) invits.get("name"), (Integer) invits.get("namestatus"));
				invits = (Map<String, Object>) Common.sAddSlashes(invits);
			}
		}
		return invits;
	}

	
	public boolean checkSeccode(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> sConfig, String seccode) {
		if (Common.empty(sGlobal.get("mobile"))) {
			Object old_seccode = request.getSession().getAttribute("seccode");
			if (old_seccode == null) {
				return false;
			}
			seccode = Common.trim(seccode);
			if ((Integer) sConfig.get("questionmode") == 1) {
				Map<String, Map<Integer, Object>> globalSpam = Common.getCacheDate(request, response,
						"/data/cache/cache_spam.jsp", "globalSpam");
				Object answer = globalSpam.get("answer").get(old_seccode);
				if (answer == null || !seccode.equals(answer.toString())) {
					return false;
				}
			} else if (!old_seccode.toString().toLowerCase().equals(seccode.toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	
	public void updateFriend(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> sConfig, int uid, String userName, int fuid, String fuserName, String op,
			int gid) {
		if (uid == 0 || fuid == 0 || uid == fuid) {
			return;
		}
		Map<String, Object> flogData = new HashMap<String, Object>();
		if ("add".equals(op) || "invite".equals(op)) {
			Map<String, Object> insertData = new HashMap<String, Object>();
			insertData.put("uid", uid);
			insertData.put("fuid", fuid);
			insertData.put("fusername", fuserName);
			insertData.put("status", 1);
			insertData.put("gid", gid);
			insertData.put("dateline", sGlobal.get("timestamp"));
			dataBaseService.insertTable("friend", insertData, false, true);
			if ("invite".equals(op)) {
				insertData.put("uid", fuid);
				insertData.put("fuid", uid);
				insertData.put("fusername", userName);
				insertData.remove("gid");
				dataBaseService.insertTable("friend", insertData, false, true);
			} else {
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("status", 1);
				setData.put("dateline", sGlobal.get("timestamp"));
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("uid", fuid);
				whereData.put("fuid", uid);
				dataBaseService.updateTable("friend", setData, whereData);
			}
			flogData.put("action", "add");
		} else {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("friend")
					+ " WHERE (uid=" + uid + " AND fuid=" + fuid + ") OR (uid=" + fuid + " AND fuid=" + uid
					+ ")");
			flogData.put("action", "delete");
		}
		if (!Common.empty(sConfig.get("my_status"))) {
			flogData.put("uid", uid > fuid ? uid : fuid);
			flogData.put("fuid", uid > fuid ? fuid : uid);
			flogData.put("dateline", sGlobal.get("timestamp"));
			dataBaseService.insertTable("friendlog", flogData, false, true);
		}
		friendCache(request, sGlobal, sConfig, uid);
		friendCache(request, sGlobal, sConfig, fuid);
	}

	
	public void friendCache(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> sConfig, int uid) {
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (Common.empty(space) || (Integer) space.get("uid") != uid) {
			space = Common.getSpace(request, sGlobal, sConfig, uid);
		}
		if (Common.empty(space)) {
			return;
		}
		Map<String, Object> privacy = (Map<String, Object>) space.get("privacy");
		Set<Integer> groupIds = Common.empty(privacy.get("filter_gid")) ? null
				: ((Map<Integer, Integer>) privacy.get("filter_gid")).keySet();
		int maxFriendNum = 200;
		StringBuffer friendList = new StringBuffer();
		StringBuffer feedFriendList = new StringBuffer();
		String fmod = "", ffmod = "";
		int i = 0, count = 0;
		List<Map<String, Object>> friends = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("friend") + " WHERE uid=" + uid
				+ " AND status=1 ORDER BY num DESC, dateline DESC");
		for (Map<String, Object> friend : friends) {
			int fuidTemp = (Integer) friend.get("fuid");
			if (fuidTemp > 0) {
				friendList.append(fmod + fuidTemp);
				fmod = ",";
				if (i < maxFriendNum && (Common.empty(groupIds) || !groupIds.contains(friend.get("gid")))) {
					feedFriendList.append(ffmod + fuidTemp);
					ffmod = ",";
					i++;
				}
				count++;
			}
		}
		if (count > 50000) {
			friendList = new StringBuffer();
		}
		Map<String, Object> setData = new HashMap<String, Object>();
		setData.put("friend", friendList.toString());
		setData.put("feedfriend", feedFriendList.toString());
		Map<String, Object> whereData = new HashMap<String, Object>();
		whereData.put("uid", uid);
		dataBaseService.updateTable("spacefield", setData, whereData);
		if ((Integer) space.get("friendnum") != count) {
			setData = new HashMap<String, Object>();
			setData.put("friendnum", count);
			dataBaseService.updateTable("space", setData, whereData);
		}
		if (!Common.empty(sConfig.get("my_status"))) {
			Map<String, Object> insertData = new HashMap<String, Object>();
			insertData.put("uid", uid);
			insertData.put("action", "update");
			insertData.put("dateline", sGlobal.get("timestamp"));
			dataBaseService.insertTable("userlog", insertData, false, true);
		}
	}

	
	public void addFriendNum(Map<String, Object> sGlobal, int uid, String userName) {
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if (supe_uid == 0 || uid == supe_uid) {
			return;
		}
		Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
		if (member != null && Common.in_array((String[]) member.get("friends"), uid)) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("friend")
					+ " SET num=num+1 WHERE uid=" + supe_uid + " AND fuid=" + uid);
		}
	}

	
	public boolean checkNoteUid(Map<String, Object> note, Set<String> filter) {
		if (filter != null) {
			String key = note.get("type") + "|0";
			if (filter.contains(key)) {
				return false;
			} else {
				key = note.get("type") + "|" + note.get("authorid");
				if (filter.contains(key)) {
					return false;
				}
			}
		}
		return true;
	}

	
	public int addGiftNotification(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> sConfig, int uid, String type, String note, boolean returnId,
			boolean isAnonymous) {
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int timestamp = (Integer) sGlobal.get("timestamp");
		String supe_username = (String) sGlobal.get("supe_username");
		Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, uid);
		if (supe_uid > 0 && !isAnonymous) {
			addFriendNum(sGlobal, uid, (String) space.get("username"));
		}
		Map<String, Object> insertData = new HashMap<String, Object>();
		insertData.put("uid", uid);
		insertData.put("type", type);
		insertData.put("new", 1);
		if (!isAnonymous) {
			insertData.put("authorid", supe_uid);
			insertData.put("author", supe_username);
		}
		insertData.put("note", Common.addSlashes((String) Common.sStripSlashes(note)));
		insertData.put("dateline", timestamp);

		Map<String, Map<String, Object>> privacy = (Map<String, Map<String, Object>>) space.get("privacy");
		Set<String> filterNote = Common.empty(privacy.get("filter_note")) ? null : privacy.get("filter_note")
				.keySet();
		if (checkNoteUid(insertData, filterNote)) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET notenum=notenum+1 WHERE uid=" + uid);
			return dataBaseService.insertTable("notification", insertData, returnId, false);
		} else {
			return 0;
		}
	}

	
	public int addNotification(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> sConfig, int uid, String type, String note, boolean returnId) {
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int timestamp = (Integer) sGlobal.get("timestamp");
		String supe_username = (String) sGlobal.get("supe_username");
		Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, uid);
		if (supe_uid > 0) {
			addFriendNum(sGlobal, uid, (String) space.get("username"));
		}
		Map<String, Object> insertData = new HashMap<String, Object>();
		insertData.put("uid", uid);
		insertData.put("type", type);
		insertData.put("new", 1);
		insertData.put("authorid", supe_uid);
		insertData.put("author", supe_username);
		insertData.put("note", Common.addSlashes((String) Common.sStripSlashes(note)));
		insertData.put("dateline", timestamp);

		Map<String, Map<String, Object>> privacy = (Map<String, Map<String, Object>>) space.get("privacy");
		Set<String> filterNote = Common.empty(privacy.get("filter_note")) ? null : privacy.get("filter_note")
				.keySet();
		if (checkNoteUid(insertData, filterNote)) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET notenum=notenum+1 WHERE uid=" + uid);
			return dataBaseService.insertTable("notification", insertData, returnId, false);
		} else {
			return 0;
		}
	}

	public void updateInvite(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> sConfig, Map<Integer, String> sNames,
			int inviteId, int uid, String userName, int m_uid, String m_userName, int appId) {
		if (uid > 0 && uid != m_uid) {
			int friendStatus = Common.getFriendStatus(uid, m_uid);
			if (friendStatus < 1) {
				updateFriend(request, sGlobal, sConfig, uid, userName, m_uid, m_userName, "invite", 0);
				int count = dataBaseService.findRows("SELECT * FROM " + JavaCenterHome.getTableName("invite")
						+ " WHERE uid=" + m_uid + " AND fuid=" + uid);
				if (count > 0) {
					return;
				}
				Common.getReward("invitefriend", true, m_uid, "", false, request, response);
				sGlobal.put("supe_uid", m_uid);
				sGlobal.put("supe_username", m_userName);
				Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
				Common.realname_set(sGlobal, sConfig, sNames, uid, userName, null, 0);
				Common.realname_get(sGlobal, sConfig, sNames, space);
				if (Common.ckPrivacy(sGlobal, sConfig, space, "invite", 1)) {
					String title_template = Common.getMessage(request, "cp_feed_invite");
					Map<String, Object> title_data = new HashMap<String, Object>();
					title_data.put("username", "<a href=\"space.jsp?uid=" + uid + "\">"
							+ Common.stripSlashes(sNames.get(uid)) + "</a>");
					addFeed(sGlobal, "friend", title_template, title_data, "", null, "", null, null, "", 0,
							0, 0, "", false);
				}
				sGlobal.put("supe_uid", uid);
				sGlobal.put("supe_username", userName);
				addNotification(request, sGlobal, sConfig, m_uid, "friend", Common.getMessage(request,
						"cp_note_invite"), false);
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("fuid", uid);
				setData.put("fusername", userName);
				setData.put("appid", appId);
				if (inviteId > 0) {
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("id", inviteId);
					dataBaseService.updateTable("invite", setData, whereData);
				} else {
					setData.put("uid", m_uid);
					dataBaseService.insertTable("invite", setData, false, true);
				}
			}
		}
	}

	
	public boolean ckavatar(Map<String, Object> sGlobal, Map<String, Object> sConfig, int uid) {
		String type = Common.empty(sConfig.get("avatarreal")) ? "virtual" : "real";
		File file = new File(JavaCenterHome.jchRoot + "./data/avatar/"
				+ Common.avatar_file(sGlobal, uid, "middle", type));
		return file.exists();
	}

	
	public boolean updateStat(Map<String, Object> sGlobal, Map<String, Object> sConfig, String type,
			boolean primary) {
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int updateStat = (Integer) sConfig.get("updatestat");
		if (supe_uid == 0 || updateStat == 0) {
			return false;
		}
		int timestamp = (Integer) sGlobal.get("timestamp");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		int nowDayTime = Integer.parseInt(Common.gmdate("yyyyMMdd", timestamp, timeoffset));
		if (primary) {
			Map<String, Object> setMap = new HashMap<String, Object>();
			setMap.put("daytime", nowDayTime);
			setMap.put("uid", supe_uid);
			setMap.put("type", type);
			if (Common.intval(Common.getCount("statuser", setMap, null)) > 0) {
				return false;
			} else {
				dataBaseService.insertTable("statuser", setMap, false, false);
			}
		}
		Map<String, Object> setMap = new HashMap<String, Object>();
		setMap.put("daytime", nowDayTime);
		if (Common.intval(Common.getCount("stat", setMap, null)) > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("stat") + " SET `" + type
					+ "`=`" + type + "`+1 WHERE daytime=" + nowDayTime);
		} else {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("statuser")
					+ " WHERE daytime != " + nowDayTime);
			setMap.put(type, 1);
			dataBaseService.insertTable("stat", setMap, false, true);
		}
		return true;
	}

	public void topicJoin(HttpServletRequest request, int topicID, int uid, String username) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		List<Map<String, Object>> valueList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("topicuser") + " WHERE uid='" + uid + "' AND topicid='"
				+ topicID + "'");
		if (valueList.size() > 0) {
			Map<String, Object> setarr = new HashMap<String, Object>();
			setarr.put("dateline", sGlobal.get("timestamp"));
			Map<String, Object> wherearr = new HashMap<String, Object>();
			wherearr.put("id", valueList.get(0).get("id"));
			dataBaseService.updateTable("topicuser", setarr, wherearr);
		} else {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("topic")
					+ " SET joinnum=joinnum+1,lastpost='" + sGlobal.get("timestamp") + "' WHERE topicid='"
					+ topicID + "'");
			Map<String, Object> setarr = new HashMap<String, Object>();
			setarr.put("uid", uid);
			setarr.put("topicid", topicID);
			setarr.put("username", username);
			setarr.put("dateline", sGlobal.get("timestamp"));
			dataBaseService.insertTable("topicuser", setarr, false, false);
		}
	}

	
	public Map<Integer, Object> getClassArr(int uid) {
		Map<Integer, Object> classArr = new HashMap<Integer, Object>();
		List<Map<String, Object>> values = dataBaseService.executeQuery("SELECT classid, classname FROM "
				+ JavaCenterHome.getTableName("class") + " WHERE uid='" + uid + "'");
		for (Map<String, Object> value : values) {
			classArr.put((Integer) value.get("classid"), value);
		}
		return classArr;
	}

	
	public void privacyUpdate(Map privacy, int uid) {
		Map setmap = new HashMap();
		Map wheremap = new HashMap();
		setmap.put("privacy", Common.addSlashes(Serializer.serialize(privacy)));
		wheremap.put("uid", uid);
		dataBaseService.updateTable("spacefield", setmap, wheremap);
	}

	
	public boolean friendCache(HttpServletRequest request) {
		Map sGlobal = (Map) request.getAttribute("sGlobal");
		Map sConfig = (Map) request.getAttribute("sConfig");
		Map space = (Map) request.getAttribute("space");
		Map theSpace;
		if (!Common.empty(space) && (Integer) space.get("uid") == (Integer) sGlobal.get("supe_uid")) {
			theSpace = space;
		} else {
			theSpace = Common.getSpace(request, sGlobal, sConfig, sGlobal.get("supe_uid"));
		}
		if (Common.empty(theSpace)) {
			return false;
		}
		Map privacy = (Map) theSpace.get("privacy");
		Map groupIds = Common.empty(privacy.get("filter_gid")) ? new HashMap() : (Map) privacy
				.get("filter_gid");
		int maxFriendNum = 200;
		int i = 0, count = 0;
		StringBuffer friendList = new StringBuffer();
		StringBuffer feedFriendList = new StringBuffer();
		String fmod = "", ffmod = "";

		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + sGlobal.get("supe_uid")
				+ "' AND status='1' ORDER BY num DESC, dateline DESC");
		for (Map<String, Object> value : query) {
			if (!Common.empty(value.get("fuid"))) {
				friendList.append(fmod + value.get("fuid"));
				fmod = ",";
				if (i < maxFriendNum
						&& (Common.empty(groupIds) || groupIds.get(String.valueOf(value.get("gid"))) == null)) {
					feedFriendList.append(ffmod + value.get("fuid"));
					ffmod = ",";
					i++;
				}
				count++;
			}
		}
		if (count > 50000) {
			friendList = new StringBuffer("");
		}
		Map setmap = new HashMap();
		Map wheremap = new HashMap();
		setmap.put("friend", friendList.toString());
		setmap.put("feedfriend", feedFriendList.toString());
		wheremap.put("uid", sGlobal.get("supe_uid"));
		dataBaseService.updateTable("spacefield", setmap, wheremap);
		if ((Integer) theSpace.get("friendnum") != count) {
			setmap = new HashMap();
			wheremap = new HashMap();
			setmap.put("friendnum", count);
			wheremap.put("uid", sGlobal.get("supe_uid"));
			dataBaseService.updateTable("space", setmap, wheremap);
		}
		if (!Common.empty(sConfig.get("my_status"))) {
			setmap = new HashMap();
			setmap.put("uid", sGlobal.get("uid"));
			setmap.put("action", "update");
			setmap.put("dateline", sGlobal.get("timestamp"));
			dataBaseService.insertTable("userlog", setmap, false, true);
		}
		return true;
	}

	
	public int isBlackList(int uid, int currUid) {
		Map where = new HashMap();
		where.put("uid", uid);
		where.put("buid", currUid);
		int result = Common.intval(Common.getCount("blacklist", where, null));
		return result;
	}

	
	public boolean updateHot(HttpServletRequest request, HttpServletResponse response, String idType, int id,
			String hotUser) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		ArrayList hotUsers;
		if (!Common.empty(hotUser)) {
			int index = 0;
			hotUsers = new ArrayList();
			Matcher m = Pattern.compile(",").matcher(hotUser);
			while (m.find()) {
				String match = hotUser.subSequence(index, m.start()).toString();
				hotUsers.add(match);
				index = m.end();
			}
			String match = hotUser.subSequence(index, hotUser.length()).toString();
			hotUsers.add(match);
		} else {
			hotUsers = new ArrayList();
		}
		if (!hotUsers.isEmpty() && Common.in_array(hotUsers.toArray(), sGlobal.get("supe_uid"))) {
			return false;
		} else {
			hotUsers.add(sGlobal.get("supe_uid"));
			hotUser = Common.implode(hotUsers, ",");
		}
		int newHot = hotUsers.size() + 1;
		if (newHot == (Integer) sConfig.get("feedhotmin")) {
			String tableName = getTablebyIdType(idType);
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT uid FROM "
					+ JavaCenterHome.getTableName(tableName) + " WHERE " + idType + "='" + id + "'");
			Map item = query.size() == 0 ? new HashMap() : query.get(0);
			Common.getReward("hotinfo", true, (Integer) item.get("uid"), "", false, request, response);
		}
		if ("blogid".equals(idType)) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("blogfield")
					+ " SET hotuser='" + hotUser + "' WHERE blogid='" + id + "'");
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("blog")
					+ " SET hot=hot+1 WHERE blogid='" + id + "'");
		} else if ("tid".equals(idType)) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("post") + " SET hotuser='"
					+ hotUser + "' WHERE tid='" + id + "' AND isthread='1'");
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread")
					+ " SET hot=hot+1 WHERE tid='" + id + "'");
		} else if ("picid".equals(idType)) {
			dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("picfield")
					+ " (picid, hotuser) VALUES ('" + id + "','" + hotUser + "')");
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("pic")
					+ " SET hot=hot+1 WHERE picid='" + id + "'");
		} else if ("eventid".equals(idType)) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("eventfield")
					+ " SET hotuser='" + hotUser + "' WHERE eventid='" + id + "'");
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
					+ " SET hot=hot+1 WHERE eventid='" + id + "'");
		} else if ("sid".equals(idType)) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("share")
					+ " SET hot=hot+1, hotuser='" + hotUser + "' WHERE sid='" + id + "'");
		} else if ("pid".equals(idType)) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("pollfield")
					+ " SET hotuser='" + hotUser + "' WHERE pid='" + id + "'");
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("poll")
					+ " SET hot=hot+1 WHERE pid='" + id + "'");
		} else {
			return false;
		}
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT feedid, friend FROM "
				+ JavaCenterHome.getTableName("feed") + " WHERE id='" + id + "' AND idtype='" + idType + "'");
		if (query.size() != 0) {
			Map feed = query.get(0);
			if (Common.empty(feed.get("friend"))) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("feed")
						+ " SET hot=hot+1 WHERE feedid='" + feed.get("feedid") + "'");
			}
		} else {
			FeedService feedService = new FeedService();
			feedService.feedPublish(request, response, id, idType, false);
		}

		return true;
	}

	
	public Object stream_save(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> space, Map<String, Object> sConfig,
			InputStream inputStream, String albumid, String fileext, String name, String title,
			Integer delsize, String from) throws Exception {
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		if (albumid == null)
			albumid = "0";
		if (fileext == null || fileext.equals(""))
			fileext = "jpg";
		if (name == null)
			name = "";
		if (title == null)
			title = "";
		if (delsize == null)
			delsize = 0;
		if (from == null)
			from = "";

		String creatAlbumid = null;
		if (!albumid.equals("0")) {
			Pattern pattern = Pattern.compile("^(?i)new:(.+)$");
			Matcher matcher = pattern.matcher(albumid);
			if (matcher.find()) {
				creatAlbumid = matcher.group(1);
			} else if (Integer.parseInt(albumid) < 0) {
				albumid = "0";
			}
		}

		Map<String, Object> setarr = new HashMap<String, Object>();
		String filepath = getFilePath(request, fileext, true);
		String newfilename = request.getSession().getServletContext().getRealPath(
				jchConfig.get("attachDir") + "./" + filepath);

		File newFile = new File(newfilename);
		FileOutputStream fileOutputStream = null;
		boolean writeSuccess = false;
		try {
			fileOutputStream = new FileOutputStream(newFile);
			int bufferSize = 1024 * 5;
			byte[] bufferArray = new byte[bufferSize];
			int readCount;
			while ((readCount = inputStream.read(bufferArray)) != -1) {
				fileOutputStream.write(bufferArray, 0, readCount);
			}
			fileOutputStream.close();
			fileOutputStream = null;
			inputStream.close();
			inputStream = null;
			writeSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {

			}
		}
		if (writeSuccess) {
			int supe_uid = (Integer) sGlobal.get("supe_uid");
			long size = newFile.length();
			if (Common.empty(space)) {
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("space") + " WHERE uid='" + supe_uid + "'");
				if (query.size() > 0) {
					space = query.get(0);
					sGlobal.put("supe_username", Common.addSlashes((String) space.get("username")));
				} else {
					return -4;
				}
			}
			sGlobal.put("member", space);

			Integer maxattachsize = (Integer) Common.checkPerm(request, response, sGlobal, "maxattachsize");
			if (maxattachsize != null && maxattachsize != 0) {
				if ((Integer) space.get("attachsize") + size - delsize > maxattachsize
						+ (Integer) space.get("addsize")) {
					newFile.delete();
					return -1;
				}
			}
			if (!validateImage(newFile)) {
				newFile.delete();
				return -2;
			}
			String thumbPath = ImageUtil.makeThumb(request, response, newfilename);
			int thumb = thumbPath != null ? 1 : 0;
			if ((Integer) sConfig.get("allowwatermark") == 1) {
				ImageUtil.makeWaterMark(request, response, newfilename);
			}
			String filename = Common.addSlashes((name != null && !name.equals("") ? name : filepath
					.substring(filepath.lastIndexOf("/") + 1)));
			title = Common.getStr(title, 200, true, true, true, 0, 0, request, response);
			int albumId;
			if (!albumid.equals("0")) {
				if (!Common.empty(creatAlbumid)) {
					String albumname = (String) Common.sHtmlSpecialChars(creatAlbumid.trim());
					if (Common.empty(albumname))
						albumname = Common.sgmdate(request, "yyyyMMdd", 0);
					Map<String, Object> arr = new HashMap<String, Object>();
					arr.put("albumname", albumname);
					arr.put("target_ids", "");
					albumId = createAlbum(request, arr);
				} else {
					albumId = Common.intval(albumid);
					if (albumId != 0) {
						List<Map<String, Object>> query = dataBaseService
								.executeQuery("SELECT albumname,friend FROM "
										+ JavaCenterHome.getTableName("album") + " WHERE albumid='" + albumId
										+ "' AND uid='" + supe_uid + "'");
						Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
						if (!Common.empty(value)) {
							String albumname = Common.addSlashes((String) value.get("albumname"));
							int albumfriend = (Integer) value.get("friend");
						} else {
							String albumname = Common.sgmdate(request, "yyyyMMdd", 0);
							Map<String, Object> arr = new HashMap<String, Object>();
							arr.put("albumname", albumname);
							arr.put("target_ids", "");
							albumId = createAlbum(request, arr);
						}
					}
				}
			} else {
				albumId = 0;
			}
			setarr.put("albumid", albumId);
			setarr.put("uid", supe_uid);
			setarr.put("username", sGlobal.get("supe_username"));
			setarr.put("dateline", sGlobal.get("timestamp"));
			setarr.put("filename", filename);
			setarr.put("postip", Common.getOnlineIP(request));
			setarr.put("title", title);
			setarr.put("type", fileext);
			setarr.put("size", size);
			setarr.put("filepath", filepath);
			setarr.put("thumb", thumb);
			int tempI = dataBaseService.insertTable("pic", setarr, true, false);
			setarr.put("picid", tempI);
			StringBuilder setsql = new StringBuilder();
			if (!from.equals("")) {
				Map<String, Integer> reward = Common.getReward(from, false, 0, "", true, request, response);
				if (!Common.empty(reward)) {
					if (reward.get("credit") != 0) {
						setsql.append(",credit=credit+");
						setsql.append(reward.get("credit"));
					}
					if (reward.get("experience") != 0) {
						setsql.append(",experience=experience+");
						setsql.append(reward.get("experience"));
					}
				}
			}
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET attachsize=attachsize+'" + size + "', updatetime='" + sGlobal.get("timestamp")
					+ "' " + setsql.toString() + " WHERE uid='" + supe_uid + "'");
			if (albumId != 0) {
				String file = filepath + (thumb == 1 ? ".thumb.jpg" : "");
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("album") + " "
						+ "SET picnum=picnum+1, updatetime='" + sGlobal.get("timestamp") + "', pic='" + file
						+ "', picflag='1' " + "WHERE albumid='" + albumId + "'");
			}
			if ((Integer) sConfig.get("allowftp") != 0) {
				FtpUtil ftpUtil = new FtpUtil();
				if (ftpUtil.ftpUpload(request, newfilename, filepath)) {
					setarr.put("remote", 1);
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("remote", 1);
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("picid", setarr.get("picid"));
					dataBaseService.updateTable("pic", setData, whereData);
				} else {
					return -4;
				}
			}
			updateStat(request, "pic", false);
			return setarr;
		}
		return -3;
	}

	private boolean validateImage(File imageFile) {
		ImageInputStream iis = null;
		try {
			iis = ImageIO.createImageInputStream(imageFile);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				return false;
			}
			ImageReader reader = iter.next();
			String result = reader.getFormatName();
			if (!"jpg".equalsIgnoreCase(result) && !"jpeg".equalsIgnoreCase(result)
					&& !"gif".equalsIgnoreCase(result) && !"png".equalsIgnoreCase(result)) {
				return false;
			}
			reader.setInput(iis);
			int tmp_width = reader.getWidth(0);
			int tmp_height = reader.getHeight(0);
			int tmp_size = tmp_width * tmp_height;
			if (tmp_size > 16777216 || tmp_size < 4) {
				return false;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (iis != null) {
					iis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public Map<String, String> parseUrl(String url) {
		Map urlMap = new HashMap();
		try {
			URL u = new URL(url);

			String scheme = u.getProtocol();
			String host = u.getHost();
			int port = u.getPort() == -1 ? u.getDefaultPort() : u.getPort();
			String user = null;
			String pass = null;
			String path = u.getPath();
			String query = u.getQuery();
			String fragment = u.getRef();

			String user_password = u.getUserInfo();
			if (user_password != null && user_password.length() != 0) {
				String[] up = user_password.split(":");
				switch (up.length) {
					case 1:
						user = up[0];
						break;
					case 2:
						user = up[0];
						pass = up[1];
						break;
				}
			}
			if (host != null && host.length() != 0) {
				urlMap.put("host", host);
			}
			if (port != -1) {
				urlMap.put("port", port);
			}
			if (user != null) {
				urlMap.put("user", user);
			}
			if (pass != null) {
				urlMap.put("pass", pass);
			}
			if (path.length() != 0) {
				urlMap.put("path", path);
			}
			if (query != null) {
				urlMap.put("query", query);
			}
			if (fragment != null) {
				urlMap.put("fragment", fragment);
			}
		} catch (MalformedURLException e) {
		}
		return urlMap;
	}

	
	public void ignoreRequest(Map<String, Object> space, Map<String, Object> sConfig, int uid) {
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("friend") + " WHERE uid='"
				+ uid + "' AND fuid='" + space.get("uid") + "'");
		dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
				+ " SET addfriendnum=addfriendnum-1 WHERE uid='" + space.get("uid") + "' AND addfriendnum>0");
	}

	
	public String getPicUrlt(String picUrl) {
		return getPicUrlt(picUrl, 200);
	}

	
	public String getPicUrlt(String picUrl, int maxLength) {
		picUrl = (String) Common.sHtmlSpecialChars(Common.trim(picUrl));
		if (!Common.empty(picUrl)) {
			if (Common.matches(picUrl, "(?i)^http\\:\\/\\/.{5," + maxLength + "}\\.(jpg|gif|png)$")) {
				return picUrl;
			}
		}
		return "";
	}
}