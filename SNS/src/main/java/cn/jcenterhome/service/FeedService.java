
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

public class FeedService {

	
	public void feedPublish(HttpServletRequest request, HttpServletResponse response, int id, String idType,
			boolean add) {
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");

		Map<String, Object> setArr = new HashMap<String, Object>();

		if ("blogid".equals(idType)) {
			List<Map<String, Object>> blogs = dataBaseService.executeQuery("SELECT b.*,bf.* FROM "
					+ JavaCenterHome.getTableName("blog") + " b LEFT JOIN "
					+ JavaCenterHome.getTableName("blogfield") + " bf ON bf.blogid=b.blogid WHERE b.blogid='"
					+ id + "'");
			if (blogs.size() > 0) {
				Map<String, Object> blog = blogs.get(0);
				if ((Integer) blog.get("friend") != 3) {
					setArr.put("icon", "blog");
					setArr.put("id", blog.get("blogid"));
					setArr.put("idtype", idType);
					setArr.put("uid", blog.get("uid"));
					setArr.put("username", blog.get("username"));
					setArr.put("dateline", blog.get("dateline"));
					setArr.put("target_ids", blog.get("target_ids"));
					setArr.put("friend", blog.get("friend"));
					setArr.put("hot", blog.get("hot"));
					String url = "space.jsp?uid=" + blog.get("uid") + "&do=blog&id=" + blog.get("blogid");
					if ((Integer) blog.get("friend") == 4) {
						
						setArr.put("title_template", Common.getMessage(request, "cp_feed_blog_password"));
						Map<String, Object> td = new HashMap<String, Object>();
						td.put("subject", "<a href=\"" + url + "\">" + blog.get("subject") + "</a>");
						setArr.put("title_data", td);
						setArr.put("body_template", "");
					} else {
						
						if (!Common.empty(blog.get("pic"))) {
							setArr.put("image_1", Common.pic_cover_get(sConfig, (String) blog.get("pic"),
									(Integer) blog.get("picflag")));
							setArr.put("image_1_link", url);
						}
						setArr.put("title_template", Common.getMessage(request, "cp_feed_blog"));
						setArr.put("body_template", "<b>{subject}</b><br>{summary}");
						Map<String, Object> bd = new HashMap<String, Object>();
						bd.put("subject", "<a href=\"" + url + "\">" + blog.get("subject") + "</a>");
						try {
							bd.put("summary", Common.getStr((String) blog.get("message"), 150, true, true,
									false, 0, -1, request, response));
						} catch (Exception e) {
						}
						setArr.put("body_data", bd);
					}
				}
			}
		} else if ("albumid".equals(idType)) {
			int key = 1;
			if (id > 0) {
				List<Map<String, Object>> query = dataBaseService
						.executeQuery("SELECT p.*, a.username, a.albumname, a.picnum, a.friend, a.target_ids FROM "
								+ JavaCenterHome.getTableName("pic")
								+ " p LEFT JOIN "
								+ JavaCenterHome.getTableName("album")
								+ " a ON a.albumid=p.albumid WHERE p.albumid='"
								+ id
								+ "' ORDER BY dateline DESC LIMIT 0,4");
				for (Map<String, Object> album : query) {
					if ((Integer) album.get("friend") <= 2) {
						if (Common.empty(setArr.get("icon"))) {
							setArr.put("icon", "album");
							setArr.put("id", album.get("albumid"));
							setArr.put("idtype", idType);
							setArr.put("uid", album.get("uid"));
							setArr.put("username", album.get("username"));
							setArr.put("dateline", album.get("dateline"));
							setArr.put("target_ids", album.get("target_ids"));
							setArr.put("friend", album.get("friend"));
							setArr.put("title_template", "{actor} "
									+ Common.getMessage(request, "cp_upload_album"));
							setArr.put("body_template", "<b>{album}</b><br>"
									+ Common.getMessage(request, "cp_the_total_picture", "{picnum}"));
							Map<String, Object> bd = new HashMap<String, Object>();
							bd.put("album", "<a href=\"space.jsp?uid=" + album.get("uid") + "&do=album&id="
									+ album.get("albumid") + "\">" + album.get("albumname") + "</a>");
							bd.put("picnum", album.get("picnum"));
							setArr.put("body_data", bd);
						}
						setArr.put("image_" + key, Common.pic_get(sConfig, (String) album.get("filepath"),
								(Integer) album.get("thumb"), (Integer) album.get("remote"), true));
						setArr.put("image_" + key + "_link", "space.jsp?uid=" + album.get("uid")
								+ "&do=album&picid=" + album.get("picid"));
						key++;
					} else {
						break;
					}
				}
			} else {
				
				List<String> result = dataBaseService.executeQuery("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("pic") + " WHERE uid='"+sGlobal.get("supe_uid")+"' AND albumid='0'", 1);
				int picNum = Common.intval(result.get(0));
				if (picNum >= 1) {
					List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("pic") + " WHERE uid='" + sGlobal.get("supe_uid")
							+ "' AND albumid='0' ORDER BY dateline DESC LIMIT 0,4");
					for (Map<String, Object> album : query) {
						if (Common.empty(setArr.get("icon"))) {
							setArr.put("icon", "album");
							setArr.put("uid", album.get("uid"));
							setArr.put("username", album.get("username"));
							setArr.put("dateline", album.get("dateline"));
							setArr.put("title_template", "{actor} "
									+ Common.getMessage(request, "cp_upload_album"));
							setArr.put("body_template", "<b>{album}</b><br>"
									+ Common.getMessage(request, "cp_the_total_picture", "{picnum}"));
							Map<String, Object> bd = new HashMap<String, Object>();
							bd.put("album", "<a href=\"space.jsp?uid=" + album.get("uid")
									+ "&do=album&id=-1\">" + Common.getMessage(request, "default_albumname")
									+ "</a>");
							bd.put("picnum", picNum);
							setArr.put("body_data", bd);
						}
						setArr.put("image_" + key, Common.pic_get(sConfig, (String) album.get("filepath"),
								(Integer) album.get("thumb"), (Integer) album.get("remote"), true));
						setArr.put("image_" + key + "_link", "space.jsp?uid=" + album.get("uid")
								+ "&do=album&picid=" + album.get("picid"));
						key++;
					}
				}
			}
		} else if ("picid".equals(idType)) {
			String plusSql = id > 0 ? "p.picid='" + id + "'" : "p.uid='" + sGlobal.get("supe_uid")
					+ "' ORDER BY dateline DESC LIMIT 1";
			List<Map<String, Object>> query = dataBaseService
					.executeQuery("SELECT p.*, a.friend, a.target_ids, s.username FROM "
							+ JavaCenterHome.getTableName("pic") + " p LEFT JOIN "
							+ JavaCenterHome.getTableName("space") + " s ON s.uid=p.uid LEFT JOIN "
							+ JavaCenterHome.getTableName("album") + " a ON a.albumid=p.albumid WHERE "
							+ plusSql);
			if (query.size() != 0) {
				Map<String, Object> pic = query.get(0);
				if (Common.empty(pic.get("friend"))) {
					setArr.put("icon", "album");
					setArr.put("id", pic.get("picid"));
					setArr.put("idtype", idType);
					setArr.put("uid", pic.get("uid"));
					setArr.put("username", pic.get("username"));
					setArr.put("dateline", pic.get("dateline"));
					setArr.put("target_ids", pic.get("target_ids"));
					setArr.put("friend", pic.get("friend"));
					setArr.put("hot", pic.get("hot"));
					String url = "space.jsp?uid=" + pic.get("uid") + "&do=album&picid=" + pic.get("picid");
					setArr.put("image_1", Common.pic_get(sConfig, (String) pic.get("filepath"), (Integer) pic
							.get("thumb"), (Integer) pic.get("remote"), true));
					setArr.put("image_1_link", url);
					setArr.put("title_template", "{actor} "
							+ Common.getMessage(request, "cp_upload_a_new_picture"));
					setArr.put("body_template", "{title}");
					Map<String, Object> bd = new HashMap<String, Object>();
					bd.put("title", pic.get("title"));
					setArr.put("body_data", bd);
				}
			}
		} else if ("tid".equals(idType)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT t.*, p.* FROM "
					+ JavaCenterHome.getTableName("thread") + " t LEFT JOIN "
					+ JavaCenterHome.getTableName("post")
					+ " p ON p.tid=t.tid AND p.isthread='1' WHERE t.tid='" + id + "'");
			if (query.size() != 0) {
				Map<String, Object> thread = query.get(0);
				setArr.put("icon", "thread");
				setArr.put("id", thread.get("tid"));
				setArr.put("idtype", idType);
				setArr.put("uid", thread.get("uid"));
				setArr.put("username", thread.get("username"));
				setArr.put("dateline", thread.get("dateline"));
				setArr.put("hot", thread.get("hot"));
				String url = "space.jsp?uid=" + thread.get("uid") + "&do=thread&id=" + thread.get("tid");
				if (!Common.empty(thread.get("eventid"))) {
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("event") + " WHERE eventid='"
							+ thread.get("eventid") + "'");
					Map<String, Object> event = query.size() == 0 ? new HashMap<String, Object>() : query
							.get(0);
					setArr.put("title_template", Common.getMessage(request, "cp_feed_eventthread"));
					setArr.put("body_template", "<b>{subject}</b><br>" + Common.getMessage(request, "event")
							+ ": {event}<br>{summary}");
					Map<String, Object> bd = new HashMap<String, Object>();
					bd.put("subject", "<a href=\"" + url + "&eventid=" + thread.get("eventid") + "\">"
							+ thread.get("subject") + "</a>");
					bd.put("event", "<a href=\"space.jsp?do=event&id=" + thread.get("eventid") + "\">"
							+ event.get("title") + "</a>");
					try {
						bd.put("summary", Common.getStr((String) thread.get("message"), 150, true, true,
								false, 0, -1, request, response));
					} catch (Exception e) {
					}
					setArr.put("body_data", bd);
				} else {
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("mtag") + " WHERE tagid='" + thread.get("tagid")
							+ "'");
					Map<String, Object> mtag = query.size() == 0 ? new HashMap<String, Object>() : query
							.get(0);
					setArr.put("title_template", Common.getMessage(request, "cp_feed_thread"));
					setArr.put("body_template", "<b>{subject}</b><br>"
							+ Common.getMessage(request, "cp_mtag") + ": {mtag}<br>{summary}");
					Map<String, Object> bd = new HashMap<String, Object>();
					bd.put("subject", "<a href=\"" + url + "\">" + thread.get("subject") + "</a>");
					bd.put("mtag", "<a href=\"space.jsp?do=mtag&tagid=" + thread.get("tagid") + "\">"
							+ mtag.get("tagname") + "</a>");
					try {
						bd.put("summary", Common.getStr((String) thread.get("message"), 150, true, true,
								false, 0, -1, request, response));
					} catch (Exception e) {
					}
					setArr.put("body_data", bd);
				}
			}
		} else if ("pid".equals(idType)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("poll") + " WHERE pid='" + id + "'");
			if (query.size() != 0) {
				Map<String, Object> poll = query.get(0);
				setArr.put("icon", "poll");
				setArr.put("id", poll.get("pid"));
				setArr.put("idtype", idType);
				setArr.put("uid", poll.get("uid"));
				setArr.put("username", poll.get("username"));
				setArr.put("dateline", poll.get("dateline"));
				setArr.put("hot", poll.get("hot"));
				String url = "space.jsp?uid=" + poll.get("uid") + "&do=poll&pid=" + poll.get("pid");
				setArr.put("title_template", Common.getMessage(request, "cp_feed_poll"));
				setArr.put("body_template", "<a href=\"{url}\"><strong>{subject}</strong></a>{option}");
				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("polloption") + " WHERE pid='" + poll.get("pid")
						+ "' LIMIT 0,2");
				StringBuffer optionStr = new StringBuffer();
				for (Map<String, Object> option : query) {
					optionStr.append("<br><input type=\""
							+ ((Integer) poll.get("maxchoice") > 1 ? "checkbox" : "radio")
							+ "\" disabled name=\"poll_" + option.get("oid") + "\"/>" + option.get("option"));
				}
				Map<String, Object> bd = new HashMap<String, Object>();
				bd.put("url", url);
				bd.put("subject", poll.get("subject"));
				bd.put("option", optionStr);
				setArr.put("body_data", bd);
				setArr.put("body_general", !Common.empty(poll.get("percredit")) ? Common.getMessage(request,
						"cp_reward_info", poll.get("percredit")) : "");
			}
		} else if ("eventid".equals(idType)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("event") + " WHERE eventid='" + id + "'");
			if (query.size() != 0) {
				Map<String, Object> event = query.get(0);
				setArr.put("icon", "event");
				setArr.put("id", event.get("eventid"));
				setArr.put("idtype", idType);
				setArr.put("uid", event.get("uid"));
				setArr.put("username", event.get("username"));
				setArr.put("dateline", event.get("dateline"));
				setArr.put("hot", event.get("hot"));
				String url = "space.jsp?do=event&id=" + event.get("eventid");
				setArr.put("title_template", Common.getMessage(request, "cp_event_add"));
				setArr.put("body_template", Common.getMessage(request, "cp_event_feed_info"));
				Map<String, Object> bd = new HashMap<String, Object>();
				bd.put("title", "<a href=\"" + url + "\">" + event.get("title") + "</a>");
				bd.put("province", event.get("province"));
				bd.put("city", event.get("city"));
				bd.put("location", event.get("location"));
				bd.put("starttime", Common.sgmdate(request, "MM-dd HH:mm", (Integer) event.get("starttime")));
				bd.put("endtime", Common.sgmdate(request, "MM-dd HH:mm", (Integer) event.get("endtime")));
				setArr.put("body_data", bd);
				if (!Common.empty(event.get("poster"))) {
					setArr.put("image_1", Common.pic_get(sConfig, (String) event.get("poster"),
							(Integer) event.get("thumb"), (Integer) event.get("remote"), true));
					setArr.put("image_1_link", url);
				}
			}
		} else if ("sid".equals(idType)) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("share") + " WHERE sid='" + id + "'");
			if (query.size() != 0) {
				Map<String, Object> share = query.get(0);
				setArr.put("icon", "share");
				setArr.put("id", share.get("sid"));
				setArr.put("idtype", idType);
				setArr.put("uid", share.get("uid"));
				setArr.put("username", share.get("username"));
				setArr.put("dateline", share.get("dateline"));
				setArr.put("hot", share.get("hot"));
				String url = "space.jsp?uid=" + share.get("uid") + "&do=share&id=" + share.get("sid");
				Map<String, Object> title_data = new HashMap<String, Object>();
				title_data.put("url", url);
				setArr.put("title_data", title_data);
				setArr.put("title_template", "{actor} <a href=\"{url}\">"+share.get("title_template")+"</a>" );
				setArr.put("body_template", share.get("body_template"));
				setArr.put("body_data", share.get("body_data"));
				setArr.put("body_general", share.get("body_general"));
				setArr.put("image_1", share.get("image"));
				setArr.put("image_1_link", share.get("image_link"));
			}
		}

		if (setArr.get("icon") != null) {
			setArr.put("appid", Common.intval(JavaCenterHome.jchConfig.get("JC_APPID")));
			setArr.put("title_data", Serializer.serialize(setArr.get("title_data")));
			if (!"sid".equals(idType)) {
				setArr.put("body_data", Serializer.serialize(setArr.get("body_data")));
			}
			setArr.put("hash_template", Common.md5(setArr.get("title_template") + "\t"
					+ setArr.get("body_template")));
			setArr.put("hash_data", Common.md5(setArr.get("title_template") + "\t" + setArr.get("title_data")
					+ "\t" + setArr.get("body_template") + "\t" + setArr.get("body_data")));
			Common.sAddSlashes(setArr);
			int feedId = 0;
			if (!add && !Common.empty(setArr.get("id"))) {
				List<String> query = dataBaseService.executeQuery("SELECT feedid FROM "
						+ JavaCenterHome.getTableName("feed") + " WHERE id='" + id + "' AND idtype='"
						+ idType + "'", 1);
				if (query.size() != 0) {
					feedId = Common.intval(query.get(0));
				}
			}
			if (feedId != 0) {
				Map<String, Object> whereArr = new HashMap<String, Object>();
				whereArr.put("feedid", feedId);
				dataBaseService.updateTable("feed", setArr, whereArr);
			} else {
				if (setArr.get("body_general") == null) {
					setArr.put("body_general", "");
				}
				if (setArr.get("target_ids") == null) {
					setArr.put("target_ids", "");
				}
				dataBaseService.insertTable("feed", setArr, false, false);
			}
		}
	}

}
