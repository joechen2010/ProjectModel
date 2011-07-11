package cn.jcenterhome.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.service.BlogService;
import cn.jcenterhome.service.TreeService;
import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.CookieHelper;
import cn.jcenterhome.util.ExifUtil;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.JcHomeCode;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.vo.MessageVO;


public class SpaceAction extends BaseAction {
	private String[] dos = {"feed", "doing", "mood", "blog", "album", "thread", "mtag", "friend", "wall",
			"tag", "notice", "share", "topic", "home", "pm", "event", "poll", "top", "info", "videophoto",
			"addrbook", "gift"};

	private BlogService blogService = (BlogService) BeanFactory.getBean("blogService");

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Common.getCacheDate(request, response, "/data/cache/cache_magic.jsp", "globalMagic");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		String message = Common.checkClose(request, response, supe_uid);
		if (message != null) {
			return showMessage(request, response, message);
		}
		String reWrite = request.getParameter("rewrite");
		if (!Common.empty(sConfig.get("allowrewrite")) && reWrite != null) {
			Map<String, String[]> paramMap = request.getParameterMap();
			String[] rws = reWrite.split("-");
			int rw_uid = 0;
			if ((rw_uid = Common.intval(rws[0])) > 0) {
				paramMap.put("uid", new String[] {rw_uid + ""});
			} else {
				paramMap.put("do", new String[] {rws[0]});
			}
			int rw_count = rws.length;
			if (rw_count >= 2) {
				if (rws[1] != null) {
					for (int i = 1; i < rw_count; i = i + 2) {
						if (rw_count > i + 1) {
							paramMap.put(rws[i], Common.empty(rws[i + 1]) ? new String[] {""}
									: new String[] {rws[i + 1]});
						} else {
							paramMap.put(rws[i], new String[] {""});
						}
					}
				}
			}
			paramMap.remove("rewrite");
		}
		int isInvite = 0;
		int uid = Common.intval(request.getParameter("uid"));
		String userName = request.getParameter("username");
		userName = Common.empty(userName) ? "" : userName;
		String doMain = request.getParameter("domain");
		doMain = Common.empty(doMain) ? "" : doMain;
		String action = request.getParameter("do");
		if (Common.empty(action) || !Common.in_array(dos, action)) {
			action = "index";
		}
		String code = request.getParameter("code");
		code = Common.empty(code) ? "" : code;
		if ("index".equals(action)) {
			String inVite = request.getParameter("invite");
			inVite = Common.empty(inVite) ? null : inVite;
			Map<String, Integer> reWard = Common.getReward("invitecode", false, 0, "", true, request,
					response);
			int credit = reWard.get("credit");
			if (!"".equals(code) && credit == 0) {
				isInvite = -1;
			} else if (inVite != null) {
				List<Map<String, Object>> inviteList = dataBaseService.executeQuery("SELECT id FROM "
						+ JavaCenterHome.getTableName("invite") + " WHERE uid='" + uid + "' AND code='"
						+ inVite + "' AND fuid='0'");
				if (inviteList.size() > 0) {
					isInvite = (Integer) inviteList.get(0).get("id");
				}
			}
		}
		if (isInvite == 0 && Common.empty(sConfig.get("networkpublic"))) {
			if (supe_uid == 0) {
				CookieHelper.setCookie(request, response, "_refer", Common.urlEncode((String) request
						.getAttribute("requestURI")));
				return showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
			}
		}
		if("addrbook".equals(action) && supe_uid == 0) {
			return showMessage(request, response, "to_login", "do.jsp?ac="+sConfig.get("login_action"));
		}
		Map<String, Object> space = null;
		if (uid > 0) {
			space = Common.getSpace(request, sGlobal, sConfig, uid, "uid", false);
		} else if (!"".equals(userName)) {
			space = Common.getSpace(request, sGlobal, sConfig, userName, "username", false);
		} else if (!"".equals(doMain)) {
			space = Common.getSpace(request, sGlobal, sConfig, doMain, "domain", false);
		} else if (supe_uid > 0) {
			space = Common.getSpace(request, sGlobal, sConfig, supe_uid, "uid", false);
		}
		if (space != null) {
			if ((Integer) space.get("flag") == -1) {
				return showMessage(request, response, "space_has_been_locked");
			}
			if (isInvite == 0
					|| (isInvite < 0 && !code.equals(Common.spaceKey(space, sConfig, Common.intval(request
							.getParameter("app")))))) {
				if (Common.empty(sConfig.get("networkpublic"))) {
					if (supe_uid == 0) {
						CookieHelper.setCookie(request, response, "_refer", Common.urlEncode((String) request
								.getAttribute("requestURI")));
						return showMessage(request, response, "to_login", "do.jsp?ac="
								+ sConfig.get("login_action"));
					}
				}
				if (!Common.ckPrivacy(sGlobal, sConfig, space, action, 0)) {
					if(supe_uid!=0){
						Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
						Map<String, Object> member=Common.getMember(request);
						Common.realname_set(sGlobal, sConfig, sNames, supe_uid, (String)member.get("username"), (String)member.get("name"), (Integer)member.get("namestatus"));
						Common.realname_get(sGlobal, sConfig, sNames, null);
					}
					request.setAttribute("space", space);
					setPrivacy(sGlobal, space);
					return include(request, response, sConfig, sGlobal, "space_privacy.jsp");
				}
			}
			Map<String, String[]> paramMap = request.getParameterMap();
			if (!(Boolean) space.get("self")) {
				paramMap.put("view", new String[] {"me"});
			} else if (Common.empty(space.get("feedfriend")) && Common.empty(request.getParameter("view"))) {
				paramMap.put("view", new String[] {"all"});
			}
			if ("me".equals(request.getParameter("view"))) {
				space.put("feedfriend", "");
			}
		} else if (uid > 0) {
			if (dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("spacelog")
					+ " WHERE uid='" + uid + "' AND flag='-1'") > 0) {
				if(supe_uid!=0){
					Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
					Map<String, Object> member=Common.getMember(request);
					Common.realname_set(sGlobal, sConfig, sNames, supe_uid, (String)member.get("username"), (String)member.get("name"), (Integer)member.get("namestatus"));
					Common.realname_get(sGlobal, sConfig, sNames, null);
				}
				return showMessage(request, response, "the_space_has_been_closed");
			}
		}
		if (Common.empty(space)) {
			space = new HashMap<String, Object>();
			space.put("uid", 0);
			space.put("username", "guest");
			space.put("self", true);
			if ("index".equals(action)) {
				action = "feed";
			}
		}
		request.setAttribute("do", action);
		request.setAttribute("space", space);
		if (supe_uid > 0) {
			Common.getMember(request);
			if ((Integer) ((Map) sGlobal.get("member")).get("flag") == -1) {
				return showMessage(request, response, "space_has_been_locked");
			}
			if (Common.checkPerm(request, response, "banvisit")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				} else {
					return showMessage(request, response, "you_do_not_have_permission_to_visit");
				}
			}

			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("session")
					+ " SET lastactivity='" + sGlobal.get("timestamp") + "' WHERE uid='" + supe_uid + "'");
		}
		Integer cronnextrun = (Integer) sConfig.get("cronnextrun");
		if (!Common.empty(cronnextrun) && cronnextrun <= (Integer) sGlobal.get("timestamp")) {
			cronService.runCron(request, response);
		}

		return invokeMethod(this, "space_" + action, request, response);
	}

	
	public ActionForward space_album(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<Integer, String> friendsName = new TreeMap<Integer, String>();
		friendsName.put(1, "仅好友可见");
		friendsName.put(2, "指定好友可见");
		friendsName.put(3, "仅自己可见");
		friendsName.put(4, "凭密码可见");
		request.setAttribute("friendsName", friendsName);

		int id = Common.intval(request.getParameter("id"));
		Integer picId = Common.intval(request.getParameter("picid"));
		int page = Common.intval(request.getParameter("page"));
		page = page < 1 ? 1 : page;
		if (id != 0) {
			int perPage = 20;
			perPage = Common.mobPerpage(request, perPage);
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			String whereSql = null;
			int count = 0;
			Map<String, Object> album = null;
			if (id > 0) {
				List<Map<String, Object>> albumList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("album") + " WHERE albumid='" + id + "' AND uid='"
						+ space.get("uid") + "' LIMIT 1");
				if (albumList.size() > 0) {
					album = albumList.get(0);
				}
				if (album == null) {
					return showMessage(request, response, "to_view_the_photo_does_not_exist");
				}
				if (ckFriendAlbum(album, sGlobal, sConfig, space, request, response) == null) {
					return null;
				}
				whereSql = "albumid='" + id + "'";
				count = (Integer) album.get("picnum");
			} else {
				whereSql = "albumid='0' AND uid='" + space.get("uid") + "'";
				count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pic")
						+ " WHERE `albumid`='0' AND `uid`='" + space.get("uid") + "' LIMIT 1 ");
				album = new HashMap<String, Object>();
				album.put("uid", space.get("uid"));
				album.put("albumid", -1);
				album.put("albumname", Common.getMessage(request, "default_albumname"));
				album.put("picnum", count);
			}
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("pic") + " WHERE " + whereSql
						+ " ORDER BY dateline DESC LIMIT " + start + "," + perPage);
				for (Map<String, Object> value : list) {
					value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"), (Integer) value
							.get("thumb"), (Integer) value.get("remote"), true));
				}
				request.setAttribute("list", list);
			}

			if (!(Boolean) space.get("self")) {
				Map<String, Object> TPL = new HashMap<String, Object>();
				TPL.put("spacetitle", "相册 - " + album.get("albumname"));
				TPL.put("spacemenus", new String[] {
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=album&view=me\">TA的所有相册</a>",
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=album&id=" + id + "\">"
								+ album.get("albumname") + "</a>"});
				request.setAttribute("TPL", TPL);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
					"space.jsp?uid=" + album.get("uid") + "&do=" + request.getAttribute("do") + "&id=" + id,
					null, null));
			request.setAttribute("managealbum", Common.checkPerm(request, response, "managealbum"));
			request.setAttribute("tpl_css", "album");
			request.setAttribute("album", album);
			request.setAttribute("navtitle", album.get("albumname") + " - " + "相册 - ");
			return include(request, response, sConfig, sGlobal, "space_album_view.jsp");
		} else if (picId > 0) {
			String gotoStr = request.getParameter("goto");
			if (Common.empty(gotoStr)) {
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("goto", null);
				gotoStr = "";
			}
			int eventId = Common.intval(request.getAttribute("eventId") + "");
			Map<String, Object> pic = null;
			if (eventId == 0) {
				List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("pic") + " WHERE picid='" + picId + "' AND uid='"
						+ space.get("uid") + "' LIMIT 1");
				if (picList.size() > 0) {
					pic = picList.get(0);
				}
			} else {
				pic = (Map<String, Object>) request.getAttribute("eventPic");
			}
			if (pic == null) {
				return showMessage(request, response, "view_images_do_not_exist");
			}
			if ("up".equals(gotoStr)) {
				if (eventId > 0) {
					List<Map<String, Object>> eventPicList = dataBaseService
							.executeQuery("SELECT pic.*, ep.* FROM "
									+ JavaCenterHome.getTableName("eventpic") + " ep LEFT JOIN "
									+ JavaCenterHome.getTableName("pic")
									+ " pic ON ep.picid = pic.picid WHERE ep.eventid='" + eventId
									+ "' AND ep.picid > '" + pic.get("picid")
									+ "' ORDER BY ep.picid ASC LIMIT 0,1");
					if (eventPicList.size() == 0) {
						List<Map<String, Object>> eventPicList2 = dataBaseService
								.executeQuery("SELECT pic.*, ep.* FROM "
										+ JavaCenterHome.getTableName("eventpic") + " ep LEFT JOIN "
										+ JavaCenterHome.getTableName("pic")
										+ " pic ON ep.picid = pic.picid WHERE ep.eventid='" + eventId
										+ "' ORDER BY ep.picid ASC LIMIT 1");
						if (eventPicList2.size() > 0) {
							pic = eventPicList2.get(0);
						}
					} else {
						pic = eventPicList.get(0);
					}
				} else {
					List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("pic") + " WHERE albumid='" + pic.get("albumid")
							+ "' AND uid='" + space.get("uid") + "' AND picid>" + picId
							+ " ORDER BY picid LIMIT 1");
					if (picList.size() == 0) {
						List<Map<String, Object>> picList2 = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("pic") + " WHERE albumid='"
								+ pic.get("albumid") + "' AND uid='" + space.get("uid")
								+ "' ORDER BY picid LIMIT 1");
						if (picList2.size() > 0) {
							pic = picList2.get(0);
						}
					} else {
						pic = picList.get(0);
					}
				}
			} else if ("down".equals(gotoStr)) {
				if (eventId > 0) {
					List<Map<String, Object>> eventPicList = dataBaseService
							.executeQuery("SELECT pic.*, ep.* FROM "
									+ JavaCenterHome.getTableName("eventpic") + " ep LEFT JOIN "
									+ JavaCenterHome.getTableName("pic")
									+ " pic ON ep.picid = pic.picid WHERE ep.eventid='" + eventId
									+ "' AND ep.picid < '" + pic.get("picid")
									+ "' ORDER BY ep.picid DESC LIMIT 0,1");
					if (eventPicList.size() == 0) {
						List<Map<String, Object>> eventPicList2 = dataBaseService
								.executeQuery("SELECT pic.*, ep.* FROM "
										+ JavaCenterHome.getTableName("eventpic") + " ep LEFT JOIN "
										+ JavaCenterHome.getTableName("pic")
										+ " pic ON ep.picid = pic.picid WHERE ep.eventid='" + eventId
										+ "' ORDER BY ep.picid DESC LIMIT 1");
						if (eventPicList2.size() > 0) {
							pic = eventPicList2.get(0);
						}
					} else {
						pic = eventPicList.get(0);
					}
				} else {
					List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("pic") + " WHERE albumid='" + pic.get("albumid")
							+ "' AND uid='" + space.get("uid") + "' AND picid<" + picId
							+ " ORDER BY picid DESC LIMIT 1");
					if (picList.size() == 0) {
						List<Map<String, Object>> picList2 = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("pic") + " WHERE albumid='"
								+ pic.get("albumid") + "' AND uid='" + space.get("uid")
								+ "' ORDER BY picid DESC LIMIT 1");
						if (picList2.size() > 0) {
							pic = picList2.get(0);
						}
					} else {
						pic = picList.get(0);
					}
				}
			}
			picId = (Integer) pic.get("picid");
			if (Common.empty(picId)) {
				return showMessage(request, response, "view_images_do_not_exist");
			}
			String theURL = null;
			if (eventId > 0) {
				theURL = "space.jsp?do=event&id=" + eventId + "&view=pic&picid=" + picId;
			} else {
				theURL = "space.jsp?uid=" + pic.get("uid") + "&do=" + request.getParameter("do") + "&picid="
						+ picId;
			}
			Map<String, Object> album = null;
			if (!Common.empty(pic.get("albumid"))) {
				List<Map<String, Object>> albumList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("album") + " WHERE albumid='" + pic.get("albumid")
						+ "'");
				if (albumList.size() == 0) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("pic")
							+ " SET albumid='0' WHERE albumid='" + pic.get("albumid") + "'");
				} else {
					album = albumList.get(0);
				}
			}
			if (Common.empty(album)) {
				album = new HashMap<String, Object>();
				album.put("picnum", dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("pic") + " WHERE `albumid`='0' AND `uid`='"
						+ pic.get("uid") + "' LIMIT 1 "));
				album.put("albumid", -1);
				pic.put("albumid", -1);
			} else {
				if (eventId > 0) {
					List<Map<String, Object>> eventPicList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("eventpic") + " WHERE eventid='" + eventId
							+ "' AND picid='" + picId + "'");
					if (eventPicList.size() == 0) {
						return showMessage(request, response, "pic_not_share_to_event");
					}
					album.put("picnum", request.getAttribute("picCount"));
				} else {
					if (ckFriendAlbum(album, sGlobal, sConfig, space, request, response) == null) {
						return null;
					}
				}
			}

			if (!Common.empty(album.get("picnum"))) {
				int sequence = 0;
				if ("down".equals(gotoStr)) {
					Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
					String picSequence = sCookie.get("pic_sequence");
					Integer picNum = (Integer) album.get("picnum");
					sequence = Common.empty(picSequence) ? picNum : Common.intval(picSequence);
					sequence++;
					if (sequence > picNum) {
						sequence = 1;
					}
				} else if ("up".equals(gotoStr)) {
					Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
					String picSequence = sCookie.get("pic_sequence");
					Integer picNum = (Integer) album.get("picnum");
					sequence = Common.empty(picSequence) ? picNum : Common.intval(picSequence);
					sequence--;
					if (sequence < 1) {
						sequence = picNum;
					}
				} else {
					sequence = 1;
				}
				CookieHelper.setCookie(request, response, "pic_sequence", sequence + "");
				request.setAttribute("sequence", sequence);
			}
			pic.put("pic", Common.pic_get(sConfig, (String) pic.get("filepath"), (Integer) pic.get("thumb"),
					(Integer) pic.get("remote"), false));
			pic.put("size", Common.formatSize((Integer) pic.get("size")));
			if (request.getParameter("exif") != null) {
				request.setAttribute("exifs", ExifUtil.getExif(request, (String) pic.get("pic")));
			}
			int perPage = 50;
			perPage = Common.mobPerpage(request, perPage);
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			int cid = Common.intval(request.getParameter("cid"));
			request.setAttribute("cid", cid);
			String csql = cid > 0 ? "cid='" + cid + "'  AND" : "";
			int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("comment") + " WHERE " + csql + " id='" + pic.get("picid")
					+ "' AND idtype='picid'");
			Map<Integer, String> sNames = null;
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("comment") + " WHERE " + csql + " id='"
						+ pic.get("picid") + "' AND idtype='picid' ORDER BY dateline LIMIT " + start + ","
						+ perPage);
				sNames = (Map<Integer, String>) request.getAttribute("sNames");
				for (Map<String, Object> value : list) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
							(String) value.get("author"), "", 0);
				}
				request.setAttribute("list", list);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL, "",
					"pic_comment"));
			if (Common.empty(album.get("albumname"))) {
				album.put("albumname", Common.getMessage(request, "default_albumname"));
			}
			if (!(Boolean) space.get("self")) {
				dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("log")
						+ " (`id`, `idtype`) VALUES ('" + space.get("uid") + "', 'uid')");
			}
			if (eventId == 0) {
				List<Map<String, Object>> eventPicList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("eventpic") + " ep LEFT JOIN "
						+ JavaCenterHome.getTableName("event")
						+ " e ON ep.eventid=e.eventid WHERE ep.picid='" + picId + "'");
				if (eventPicList.size() > 0) {
					request.setAttribute("event", eventPicList.get(0));
				}
			}
			request.setAttribute("hash", Common.md5(pic.get("uid") + "\t" + pic.get("dateline")));
			id = (Integer) pic.get("picid");
			request.setAttribute("id", id);
			String idtype = "picid";
			request.setAttribute("idtype", idtype);
			Map globalClick = Common.getCacheDate(request, response, "/data/cache/cache_click.jsp",
					"globalClick");
			Map<Object, Map> clicks = Common.empty(globalClick.get("picid")) ? new HashMap()
					: (Map) globalClick.get("picid");
			Set clicksKeys = clicks.keySet();
			Map value = null;
			int maxClickNum = 0;
			for (Object clicksKey : clicksKeys) {
				value = clicks.get(clicksKey);
				value.put("clicknum", pic.get("click_" + clicksKey));
				value.put("classid", Common.rand(1, 4));
				if (value.get("clicknum") != null && (Integer) value.get("clicknum") > maxClickNum) {
					maxClickNum = (Integer) value.get("clicknum");
				}
			}
			request.setAttribute("maxclicknum", maxClickNum);
			List<Map<String, Object>> clickUserList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("clickuser") + " WHERE id='" + id + "' AND idtype='"
					+ idtype + "' ORDER BY dateline DESC LIMIT 0,18");
			sNames = sNames == null ? (Map<Integer, String>) request.getAttribute("sNames") : sNames;
			for (Map<String, Object> clickUser : clickUserList) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) clickUser.get("uid"),
						(String) clickUser.get("username"), "", 0);
				clickUser.put("clickname", clicks.get(clickUser.get("clickid")).get("name"));
			}
			request.setAttribute("topic", Common.getTopic(request, (Integer) pic.get("topicid")));

			if (!(Boolean) space.get("self")) {
				Map<String, Object> TPL = new HashMap<String, Object>();
				TPL.put("spacetitle", "相册 - " + album.get("albumname"));
				TPL.put("spacemenus", new String[] {
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=album&view=me\">TA的所有相册</a>",
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=album&id=" + pic.get("albumid")
								+ "\">" + album.get("albumname") + "</a>",
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=album&picid=" + pic.get("picid")
								+ "\">查看图片</a>"});
				request.setAttribute("TPL", TPL);
			}
			try {
				String title = (String) pic.get("title");
				title = title == null || "".equals(title) ? "" : Common.getStr(title, 60, false, false,
						false, 0, -1, request, response);
				title = "".equals(title) ? "" : title + " - ";
				request.setAttribute("navtitle", title + album.get("albumname") + " - " + "相册 - ");
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("clicks", clicks);
			request.setAttribute("clickuserlist", clickUserList);
			request.setAttribute("picid", picId);
			request.setAttribute("theurl", theURL);
			request.setAttribute("album", album);
			request.setAttribute("pic", pic);
			request.setAttribute("managealbum", Common.checkPerm(request, response, "managealbum"));
			if (eventId == 0) {
				sNames = sNames == null ? (Map<Integer, String>) request.getAttribute("sNames") : sNames;
				Common.realname_get(sGlobal, sConfig, sNames, space);
				request.setAttribute("tpl_css", "album");
				return include(request, response, sConfig, sGlobal, "space_album_pic.jsp");
			} else {
				return null;
			}
		} else {
			int perPage = 12;
			perPage = Common.mobPerpage(request, perPage);

			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			Map<String, String[]> paramMap = request.getParameterMap();
			String view = request.getParameter("view");
			if (Common.empty(view)
					&& (space.get("friendnum") == null || (Integer) space.get("friendnum") < (Integer) sConfig
							.get("showallfriendnum"))) {
				paramMap.put("view", new String[] {"all"});
				view = "all";
			}
			String theURL = null;
			Map<String, String> actives = new HashMap<String, String>();
			String whereSQL = null;
			boolean picMode = false;
			String fIndex = "";
			int count = 0;
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			Map globalClick = Common.getCacheDate(request, response, "/data/cache/cache_click.jsp",
					"globalClick");
			Map<Object, Map> clicks = Common.empty(globalClick.get("picid")) ? new HashMap()
					: (Map) globalClick.get("picid");
			if ("click".equals(view)) {
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=click";
				actives.put("click", " class=\"active\"");
				int clickId = Common.intval(request.getParameter("clickid"));
				Map clickActives = new HashMap();
				if (clickId > 0) {
					theURL += "&clickid=" + clickId;
					whereSQL = " AND c.clickid='" + clickId + "'";
					clickActives.put(clickId, " class=\"current\"");
				} else {
					whereSQL = "";
					clickActives.put("all", " class=\"current\"");
				}
				request.setAttribute("click_actives", clickActives);
				picMode = true;

				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("clickuser") + " c WHERE c.uid='" + space.get("uid")
						+ "' AND c.idtype='picid' " + whereSQL);
				if (count > 0) {
					List<Map<String, Object>> list = dataBaseService
							.executeQuery("SELECT p.*,a.albumname, a.username, c.clickid FROM "
									+ JavaCenterHome.getTableName("clickuser") + " c LEFT JOIN "
									+ JavaCenterHome.getTableName("pic") + " p ON p.picid=c.id LEFT JOIN "
									+ JavaCenterHome.getTableName("album")
									+ " a ON a.albumid=p.albumid	WHERE c.uid='" + space.get("uid")
									+ "' AND c.idtype='picid' " + whereSQL
									+ " ORDER BY c.dateline DESC LIMIT " + start + "," + perPage);
					String default_albumname=Common.getMessage(request, "default_albumname");
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"),
								(Integer) value.get("thumb"), (Integer) value.get("remote"), true));
						if(Common.empty(value.get("albumid"))){
							value.put("albumname", default_albumname);
							value.put("albumid", -1);
						}
					}
					request.setAttribute("list", list);
				}
			} else if ("all".equals(view)) {
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=all";
				actives.put("all", " class=\"active\"");
				whereSQL = "1";
				List<String> orders = new ArrayList<String>();
				orders.add("hot");
				orders.add("dateline");
				Set clicksKeys = clicks.keySet();
				for (Object clicksKey : clicksKeys) {
					orders.add("click_" + clicks.get(clicksKey).get("clickid"));
				}
				String orderby = request.getParameter("orderby");
				if (!orders.contains(orderby)) {
					paramMap.put("orderby", null);
					orderby = "";
				}
				int day = Common.intval(request.getParameter("day"));
				int hotday = 7;
				paramMap.put("day", new String[] {day + ""});
				paramMap.put("hotday", new String[] {hotday + ""});
				Map<String, String> allActives = new HashMap<String, String>();

				if ("dateline".equals(orderby)) {
					allActives.put("dateline", " class=\"current\"");
					theURL = "space.jsp?uid=" + space.get("uid") + "&do=album&view=all&orderby=" + orderby;
				} else {
					String orderSQL = null;
					if (!Common.empty(orderby)) {
						orderSQL = "p." + orderby;
						theURL = "space.jsp?uid=" + space.get("uid") + "&do=album&view=all&orderby="
								+ orderby;
						allActives.put(orderby, " class=\"current\"");
						Map<String, String> dayActives = new HashMap<String, String>();
						if (day > 0) {
							hotday = day;
							paramMap.put("hotday", new String[] {hotday + ""});
							int dayTime = (Integer) sGlobal.get("timestamp") - day * 3600 * 24;
							whereSQL += " AND p.dateline>='" + dayTime + "'";
							theURL += "&day=" + day;
							dayActives.put(day + "", " class=\"active\"");
						} else {
							dayActives.put("0", " class=\"active\"");
						}
						request.setAttribute("day_actives", dayActives);
					} else {
						orderSQL = "p.dateline";
						int feedHotMin = (Integer) sConfig.get("feedhotmin");
						int minHot = feedHotMin < 1 ? 3 : feedHotMin;
						whereSQL += " AND p.hot>='" + minHot + "'";
						theURL = "space.jsp?uid=" + space.get("uid") + "&do=album&view=all";
						allActives.put("all", " class=\"current\"");
					}

					picMode = true;
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("pic") + " p WHERE " + whereSQL);
					if (count > 0) {
						List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(count);
						List<Map<String, Object>> picList = dataBaseService
								.executeQuery("SELECT p.*, a.username, a.albumname, a.friend, a.target_ids FROM "
										+ JavaCenterHome.getTableName("pic")
										+ " p LEFT JOIN "
										+ JavaCenterHome.getTableName("album")
										+ " a ON a.albumid=p.albumid WHERE "
										+ whereSQL
										+ " ORDER BY "
										+ orderSQL + " DESC LIMIT " + start + "," + perPage);
						int priCount = 0;
						String default_albumname=Common.getMessage(request, "default_albumname");
						for (Map<String, Object> value : picList) {
							if ((Integer) value.get("friend") != 4
									&& Common.ckFriend(sGlobal, space, (Integer) value.get("uid"),
											(Integer) value.get("friend"), (String) value.get("target_ids"))) {
								Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
										(String) value.get("username"), "", 0);
								value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"),
										(Integer) value.get("thumb"), (Integer) value.get("remote"), true));
								list.add(value);
								if(Common.empty(value.get("albumid"))){
									value.put("albumname", default_albumname);
									value.put("albumid", -1);
								}
							} else {
								priCount++;
							}
						}
						request.setAttribute("pricount", priCount);
						request.setAttribute("list", list);
					}
				}
				request.setAttribute("all_actives", allActives);
			} else {

				if (Common.empty(space.get("feedfriend"))) {
					paramMap.put("view", new String[] {"me"});
					view = "me";
				}

				if ("me".equals(view)) {
					whereSQL = "uid='" + space.get("uid") + "'";
					theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
							+ "&view=me";
					actives.put("me", " class=\"active\"");
				} else {
					whereSQL = "uid IN (" + space.get("feedfriend") + ")";
					theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
							+ "&view=we";
					fIndex = "USE INDEX(updatetime)";
					actives.put("we", " class=\"active\"");
					String fUserName = Common.trim(request.getParameter("fusername"));
					int fuid = Common.intval(request.getParameter("fuid"));
					if (!Common.empty(fUserName)) {
						fuid = Common.getUid(sConfig, fUserName);
					}
					if (fuid > 0 && Common.in_array((String[]) space.get("friends"), fuid)) {
						whereSQL = "uid='" + fuid + "'";
						theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
								+ "&fuid=" + fuid;
						Map<Integer, String> fuidActives = new HashMap<Integer, String>();
						fuidActives.put(fuid, " selected");
						request.setAttribute("fuid_actives", fuidActives);
					}
					List<Map<String, Object>> userList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + space.get("uid")
							+ "' AND status='1' ORDER BY num DESC, dateline DESC LIMIT 0,500");
					for (Map<String, Object> value : userList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("fuid"),
								(String) value.get("fusername"), "", 0);
					}
					request.setAttribute("userlist", userList);
				}
			}
			if (picMode == false) {
				int friend = Common.intval(request.getParameter("friend"));
				paramMap.put("friend", new String[] {friend + ""});
				if (friend > 0) {
					whereSQL += " AND friend='" + friend + "'";
					theURL += "&friend=" + friend;
				}
				String searchKey = Common.stripSearchKey(request.getParameter("searchkey"));
				if (!Common.empty(searchKey)) {
					whereSQL += " AND albumname LIKE '%" + searchKey + "%'";
					theURL += "&searchkey=" + searchKey;
					Map<String, Object> resultMap = Common.ckSearch(theURL, request, response);
					if (resultMap != null) {
						return showMessage(request, response, (String) resultMap.get("msgkey"),
								(String) resultMap.get("url_forward"), (Integer) resultMap.get("second"),
								(String[]) resultMap.get("args"));
					}
				}
				request.setAttribute("searchkey", searchKey);
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("album") + " WHERE " + whereSQL);
				int picNum=0;
				boolean isOnlyDefault=false;
				if("me".equals(view)){
					if (!(Boolean) space.get("self")) {
						
						picNum= dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pic")
								+ " WHERE `albumid`='0' AND `uid`='" + space.get("uid") + "' LIMIT 1 ");
						if (picNum > 0) {
							if(count==0){
								isOnlyDefault=true;
							}
							count+=1;
						}
					}
				}
				if (("uid='" + space.get("uid") + "'").equals(whereSQL)){
					Integer albumNum=(Integer) space.get("albumnum");
					albumNum=albumNum==null ? 0 : albumNum;
					if(albumNum != count){
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET albumnum='" + count + "' WHERE uid='" + space.get("uid") + "'");
					}
				}
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				if (count > 0 && !isOnlyDefault) {
					list = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("album") + " " + fIndex + " WHERE " + whereSQL
							+ " ORDER BY updatetime DESC LIMIT " + start + "," + perPage);
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						if ((Integer) value.get("friend") != 4
								&& Common.ckFriend(sGlobal, space, (Integer) value.get("uid"),
										(Integer) value.get("friend"), (String) value.get("target_ids"))) {
							value.put("pic", Common.pic_cover_get(sConfig, (String) value.get("pic"),
									(Integer) value.get("picflag")));
						} else {
							value.put("pic", "image/nopublish.jpg");
						}
					}
				}
				if (picNum > 0) {
					List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT filepath,thumb,remote,dateline FROM "
							+ JavaCenterHome.getTableName("pic")+" WHERE `albumid`='0' AND `uid`='" + space.get("uid") + "' ORDER BY dateline DESC LIMIT 1");
					Map<String, Object> defaultAlbum=picList.get(0);
					defaultAlbum.put("pic", Common.pic_get(sConfig, (String) defaultAlbum.get("filepath"), (Integer) defaultAlbum
							.get("thumb"), (Integer) defaultAlbum.get("remote"), true));
					defaultAlbum.put("uid", space.get("uid"));
					defaultAlbum.put("albumid", -1);
					defaultAlbum.put("albumname", Common.getMessage(request, "default_albumname"));
					defaultAlbum.put("picnum", picNum);
					list.add(0,defaultAlbum);
				}
				request.setAttribute("list", list);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL, null,
					null));
			Common.realname_get(sGlobal, sConfig, sNames, space);

			if (!(Boolean) space.get("self")) {
				Map<String, Object> TPL = new HashMap<String, Object>();
				TPL.put("spacetitle", "相册");
				TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
						+ "&do=album&view=me\">TA的所有相册</a>"});
				request.setAttribute("TPL", TPL);
			}
			request.setAttribute("clicks", clicks);
			request.setAttribute("picmode", picMode);
			request.setAttribute("actives", actives);
			request.setAttribute("count", count);
			request.setAttribute("tpl_css", "album");
			request.setAttribute("navtitle", "相册 - ");
			return include(request, response, sConfig, sGlobal, "space_album_list.jsp");
		}
	}

	
	public ActionForward space_blog(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		int page = Common.intval(request.getParameter("page"));
		page = page < 1 ? 1 : page;
		int id = Common.intval(request.getParameter("id"));
		String pageName = null;
		String navTitle = null;
		int classId = Common.intval(request.getParameter("classid"));
		Map globalClick = Common
				.getCacheDate(request, response, "/data/cache/cache_click.jsp", "globalClick");
		Map<Object, Map> clicks = Common.empty(globalClick.get("blogid")) ? new HashMap() : (Map) globalClick
				.get("blogid");
		if (id > 0) {
			List<Map<String, Object>> blogList = dataBaseService.executeQuery("SELECT bf.*, b.* FROM "
					+ JavaCenterHome.getTableName("blog") + " b LEFT JOIN "
					+ JavaCenterHome.getTableName("blogfield") + " bf ON bf.blogid=b.blogid WHERE b.blogid='"
					+ id + "' AND b.uid='" + space.get("uid") + "'");
			Map<String, Object> blog = null;
			if (blogList.size() == 0) {
				return showMessage(request, response, "view_to_info_did_not_exist");
			} else {
				blog = blogList.get(0);
			}
			Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
			if (!Common.ckFriend(sGlobal, space, (Integer) blog.get("uid"), (Integer) blog.get("friend"),
					(String) blog.get("target_ids"))) {
				setPrivacy(sGlobal, space);
				return include(request, response, sConfig, sGlobal, "space_privacy.jsp");
			} else if (!(Boolean) space.get("self") && (Integer) blog.get("friend") == 4) {
				String cookieName = "view_pwd_blog_" + blog.get("blogid");
				String cookieValue = Common.empty(sCookie.get(cookieName)) ? "" : sCookie.get(cookieName);
				if (!cookieValue.equals(Common.md5(Common.md5((String) blog.get("password"))))) {
					request.setAttribute("invalue", blog);
					return include(request, response, sConfig, sGlobal, "do_inputpwd.jsp");
				}
			}
			blog.put("tag", Common.empty(blog.get("tag")) ? new HashMap() : Serializer.unserialize(
					(String) blog.get("tag"), false));
			blog.put("message", blogService.blogBBCode((String) blog.get("message")));
			Integer jcTagRelatedTime = (Integer) sConfig.get("jc_tagrelatedtime");
			if (!Common.empty(jcTagRelatedTime)
					&& ((Integer) sGlobal.get("timestamp") - (Integer) blog.get("relatedtime") > jcTagRelatedTime)) {
				blog.put("related", new HashMap());
			}
			if (!Common.empty(blog.get("tag")) && Common.empty(blog.get("related"))) {
				blog.put("related", null);
				int tagCount = -1;
				Map<Object,Object> tagMap = (Map) blog.get("tag");
				List bTags = new ArrayList(tagMap.size());
				List bTagIds = new ArrayList(tagMap.size());
				for (Map.Entry<Object, Object> entry : tagMap.entrySet()) {
					bTags.add(entry.getValue());
					bTagIds.add(entry.getKey());
					tagCount++;
				}
				if (!Common.empty(jcTagRelatedTime) && !Common.empty(sConfig.get("jc_status"))) {
				} else {
					List<Map<String, Object>> tagBlogList = dataBaseService
							.executeQuery("SELECT DISTINCT blogid FROM "
									+ JavaCenterHome.getTableName("tagblog") + " WHERE tagid IN ("
									+ Common.sImplode(bTagIds) + ") AND blogid<>'" + blog.get("blogid")
									+ "' ORDER BY blogid DESC LIMIT 0,10");
					List tagBlogIds = new ArrayList(tagBlogList.size());
					for (Map<String, Object> value : tagBlogList) {
						tagBlogIds.add(value.get("blogid"));
					}
					if (!tagBlogIds.isEmpty()) {
						blogList = dataBaseService.executeQuery("SELECT uid,username,subject,blogid FROM "
								+ JavaCenterHome.getTableName("blog") + " WHERE blogid IN ("
								+ Common.sImplode(tagBlogIds) + ")");
						Map appMap = null;
						Map tempMap = null;
						TreeMap<Integer, Map<String, Object>> tempList = null;
						int JC_APPID = Common.intval(JavaCenterHome.jchConfig.get("JC_APPID"));
						for (Map<String, Object> value : blogList) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), "", 0);
							value.put("url", "space.jsp?uid=" + value.get("uid") + "&do=blog&id="
									+ value.get("blogid"));
							appMap = (Map) blog.get("related");
							if (appMap == null) {
								appMap = new HashMap();
							}
							tempMap = (Map) appMap.get(JC_APPID);
							if (tempMap == null) {
								tempMap = new HashMap();
							}
							tempList = (TreeMap) tempMap.get("data");
							if (tempList == null) {
								tempList = new TreeMap<Integer, Map<String, Object>>();
							}
							if (tempList.size() == 0) {
								tempList.put(0, value);
							} else {
								tempList.put(tempList.lastKey() + 1, value);
							}
							tempMap.put("data", tempList);
							appMap.put(JC_APPID, tempMap);
							blog.put("related", appMap);
						}
						appMap = (Map) blog.get("related");
						if (appMap == null) {
							appMap = new HashMap();
						}
						tempMap = (Map) appMap.get(JC_APPID);
						if (tempMap == null) {
							tempMap = new HashMap();
						}
						tempMap.put("type", "JCHOME");
						appMap.put(JC_APPID, tempMap);
						blog.put("related", appMap);
					}
				}
				if (!Common.empty(blog.get("related")) && blog.get("related") instanceof Map) {
					Map appMap = (Map) blog.get("related");
					Set appKeys = appMap.keySet();
					Map values = null;
					Map globalTagTpl=null;
					if(new File(Common.getSiteUrl(request)+"data/cache/cache_tagtpl.jsp").exists()){
						globalTagTpl = Common.getCacheDate(request, response, "/data/cache/cache_tagtpl.jsp",
							"globalTagTpl");
					}
					Map dataMap =globalTagTpl==null ? null : (Map) globalTagTpl.get("data");
					Map tempMap = null;
					Object itemObject = null;
					Map itemValue = null;
					List<String> searchs = null;
					List replaces = null;
					Map newMap = null;
					String template = null;
					for (Object appId : appKeys) {
						values = (Map) appMap.get(appId);
						if (!Common.empty(values.get("data"))
								&& !Common.empty(dataMap)
								&& !Common.empty(dataMap.get(appId))
								&& !Common.empty((template = (String) ((Map) dataMap.get(appId))
										.get("template")))) {
							tempMap = (Map) values.get("data");
							Set itemKeys = tempMap.keySet();
							for (Object itemKey : itemKeys) {
								itemObject = tempMap.get(itemKey);
								if (!Common.empty(itemObject) && itemObject instanceof Map) {
									itemValue = (Map) itemObject;
									searchs = new ArrayList<String>();
									replaces = new ArrayList();
									Set keys = itemValue.keySet();
									for (Object key : keys) {
										searchs.add("{" + key + "}");
										replaces.add(itemValue.get(key));
									}
									for (int j = 0; j < searchs.size(); j++) {
										template = template.replace(searchs.get(j), replaces.get(j) + "");
									}
									newMap = new HashMap();
									newMap.put("html", Common.stripSlashes(template));
									tempMap.put(itemKey, newMap);
								} else {
									tempMap.remove(itemKey);
								}
							}
						} else {
							values.put("data", "");
						}
						if (Common.empty(values.get("data"))) {
							appMap.remove(appId);
						}
					}
				}
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("blogfield")
						+ " SET related='"
						+ Common.addSlashes(Serializer.serialize(Common.sStripSlashes(blog.get("related"))))
						+ "',relatedtime='" + sGlobal.get("timestamp") + "' WHERE blogid='"
						+ blog.get("blogid") + "'");
			} else {
				blog.put("related", Common.empty(blog.get("related")) ? new HashMap() : Serializer
						.unserialize((String) blog.get("related"), false));
			}
			List<Map<String, Object>> otherList = new ArrayList<Map<String, Object>>();
			blogList = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("blog")
					+ " WHERE uid='" + space.get("uid") + "' ORDER BY dateline DESC LIMIT 0,6");
			for (Map<String, Object> value : blogList) {
				if (value.get("blogid") != blog.get("blogid") && Common.empty(value.get("friend"))) {
					otherList.add(value);
				}
			}
			List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
			blogList = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("blog")
					+ " WHERE hot>=3 ORDER BY dateline DESC LIMIT 0,6");
			for (Map<String, Object> value : blogList) {
				if (value.get("blogid") != blog.get("blogid") && Common.empty(value.get("friend"))) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					newList.add(value);
				}
			}
			int perPage = 30;
			perPage = Common.mobPerpage(request, perPage);
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			int count = (Integer) blog.get("replynum");
			if (count > 0) {
				int cid = Common.intval(request.getParameter("cid"));
				request.setAttribute("cid", cid);
				String cSql = cid > 0 ? "cid='" + cid + "' AND" : "";
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("comment") + " WHERE " + cSql + " id='" + id
						+ "' AND idtype='blogid' ORDER BY dateline LIMIT " + start + "," + perPage);
				for (Map<String, Object> value : list) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
							(String) value.get("author"), "", 0);
				}
				request.setAttribute("list", list);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
					"space.jsp?uid=" + blog.get("uid") + "&do=" + request.getAttribute("do") + "&id=" + id,
					"", "content"));
			if (!(Boolean) space.get("self")
					&& !blog.get("blogid").toString().equals(sCookie.get("view_blogid"))) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("blog")
						+ " SET viewnum=viewnum+1 WHERE blogid='" + blog.get("blogid") + "'");
				dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("log")
						+ " (id, idtype) VALUES ('" + space.get("uid") + "', 'uid')");
				CookieHelper.setCookie(request, response, "view_blogid", blog.get("blogid").toString());
			}
			Set clickKeys = clicks.keySet();
			Map value = null;
			int maxClickNum = 0;
			for (Object clickKey : clickKeys) {
				value = clicks.get(clickKey);
				value.put("clicknum", blog.get("click_" + clickKey));
				value.put("classid", Common.rand(1, 4));
				if (value.get("clicknum") != null && (Integer) value.get("clicknum") > maxClickNum) {
					maxClickNum = (Integer) value.get("clicknum");
				}
			}
			request.setAttribute("maxclicknum", maxClickNum);
			id = (Integer) blog.get("blogid");
			String idType = "blogid";
			String hash = Common.md5(blog.get("uid") + "\t" + blog.get("dateline"));
			request.setAttribute("id", id);
			request.setAttribute("idtype", idType);
			request.setAttribute("hash", hash);
			List<Map<String, Object>> clickUserList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("clickuser") + " WHERE id='" + id + "' AND idtype='"
					+ idType + "' ORDER BY dateline DESC LIMIT 0,18");
			for (Map<String, Object> clickUser : clickUserList) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) clickUser.get("uid"),
						(String) clickUser.get("username"), "", 0);
				clickUser.put("clickname", clicks.get(clickUser.get("clickid")).get("name"));
			}
			request.setAttribute("topic", Common.getTopic(request, (Integer) blog.get("topicid")));
			Common.realname_get(sGlobal, sConfig, sNames, space);
			if (!(Boolean) space.get("self")) {
				Map TPL = new HashMap();
				TPL.put("spacetitle", "日志");
				TPL.put("spacemenus", new String[] {
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
								+ "&view=me\">TA的所有日志</a>",
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=blog&id=" + blog.get("blogid")
								+ "\">查看日志</a>"});
				request.setAttribute("TPL", TPL);
			}

			navTitle = blog.get("subject") + " - " + "日志 - ";
			pageName = "space_blog_view.jsp";
			request.setAttribute("manageBlog", Common.checkPerm(request, response, "manageblog"));
			request.setAttribute("blog", blog);
			request.setAttribute("otherlist", otherList);
			request.setAttribute("newlist", newList);
			request.setAttribute("clickuserlist", clickUserList);
		} else {
			int perPage = 10;
			perPage = Common.mobPerpage(request, perPage);
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			Integer count = 0;
			String orderSql = "b.dateline";
			String view = request.getParameter("view");
			if (Common.empty(view)
					&& (space.get("friendnum") == null || (Integer) space.get("friendnum") < (Integer) sConfig
							.get("showallfriendnum"))) {
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("view", new String[] {"all"});
				view = "all";
			}
			String fIndex = "";
			List<Map<String, Object>> blogList = null;
			String whereSQL = null;
			String theURL = null;
			Map actives = new HashMap();
			if ("click".equals(view)) {
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=click";
				actives.put("click", " class=\"active\"");

				int clickId = Common.intval(request.getParameter("clickid"));
				Map clickActives = new HashMap();
				if (clickId > 0) {
					theURL += "&clickid=" + clickId;
					whereSQL = " AND c.clickid='" + clickId + "'";
					clickActives.put(clickId, " class=\"current\"");
				} else {
					whereSQL = "";
					clickActives.put("all", " class=\"current\"");
				}
				request.setAttribute("click_actives", clickActives);
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("clickuser") + " c WHERE c.uid='" + space.get("uid")
						+ "' AND c.idtype='blogid' " + whereSQL);
				if (count > 0) {
					blogList = dataBaseService
							.executeQuery("SELECT b.*, bf.message, bf.target_ids, bf.magiccolor FROM "
									+ JavaCenterHome.getTableName("clickuser") + " c LEFT JOIN "
									+ JavaCenterHome.getTableName("blog") + " b ON b.blogid=c.id LEFT JOIN "
									+ JavaCenterHome.getTableName("blogfield")
									+ " bf ON bf.blogid=c.id WHERE c.uid='" + space.get("uid")
									+ "' AND c.idtype='blogid' " + whereSQL
									+ " ORDER BY c.dateline DESC LIMIT " + start + "," + perPage);
				}
			} else {
				Map<String, String[]> paramMap = request.getParameterMap();
				if ("all".equals(view)) {
					whereSQL = "1";
					actives.put("all", " class=\"active\"");
					List<String> orders = new ArrayList<String>();
					orders.add("dateline");
					orders.add("replynum");
					orders.add("viewnum");
					orders.add("hot");
					Set clickKeys = clicks.keySet();
					for (Object clickKey : clickKeys) {
						orders.add("click_" + clicks.get(clickKey).get("clickid"));
					}
					String orderby = request.getParameter("orderby");
					if (!orders.contains(orderby)) {
						paramMap.put("orderby", null);
						orderby = "";
					}
					int day = Common.intval(request.getParameter("day"));
					paramMap.put("day", new String[] {day + ""});
					paramMap.put("hotday", new String[] {"7"});
					Map allActives = new HashMap();
					request.setAttribute("all_actives", allActives);
					if (!Common.empty(orderby)) {
						orderSql = "b." + orderby;
						theURL = "space.jsp?uid=" + space.get("uid") + "&do=blog&view=all&orderby=" + orderby;
						allActives.put(orderby, " class=\"current\"");
						Map dayActives = new HashMap();
						request.setAttribute("day_actives", dayActives);
						if (day > 0) {
							paramMap.put("hotday", new String[] {day + ""});
							int dayTime = (Integer) sGlobal.get("timestamp") - day * 3600 * 24;
							whereSQL += " AND b.dateline>='" + dayTime + "'";
							theURL += "&day=" + day;
							dayActives.put(day + "", " class=\"active\"");
						} else {
							dayActives.put("0", " class=\"active\"");
						}
					} else {
						theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
								+ "&view=all";
						int feedHotMin = (Integer) sConfig.get("feedhotmin");
						int minHot = feedHotMin < 1 ? 3 : feedHotMin;
						whereSQL += " AND b.hot>='" + minHot + "'";
						allActives.put("all", " class=\"current\"");
					}
				} else {
					if (Common.empty(space.get("feedfriend")) || classId > 0) {
						paramMap.put("view", new String[] {"me"});
						view = "me";
					}

					if ("me".equals(view)) {
						whereSQL = "b.uid='" + space.get("uid") + "'";
						theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
								+ "&view=me";
						actives.put("me", " class=\"active\"");
						List<Map<String, Object>> classList = dataBaseService
								.executeQuery("SELECT classid, classname FROM "
										+ JavaCenterHome.getTableName("class") + " WHERE uid='"
										+ space.get("uid") + "'");
						Map classArr = new LinkedHashMap();
						for (Map<String, Object> value : classList) {
							classArr.put(value.get("classid"), value.get("classname"));
						}
						request.setAttribute("classarr", classArr);
					} else {
						whereSQL = "b.uid IN (" + space.get("feedfriend") + ")";
						theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
								+ "&view=we";
						fIndex = "USE INDEX(dateline)";
						String fUserName = request.getParameter("fusername");
						int fUid = Common.intval(request.getParameter("fuid"));
						if (!Common.empty(fUserName)) {
							fUid = Common.getUid(sConfig, fUserName);
						}
						if (fUid > 0 && Common.in_array((String[]) space.get("friends"), fUid)) {
							whereSQL = "b.uid = '" + fUid + "'";
							theURL = "space.jsp?uid=" + space.get("uid") + "&do="
									+ request.getAttribute("do") + "&view=we&fuid=" + fUid;
							fIndex = "";
							Map fuidActives = new HashMap();
							fuidActives.put(fUid, " selected");
							request.setAttribute("fuid_actives", fuidActives);
						}

						actives.put("we", " class=\"active\"");
						List<Map<String, Object>> userList = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + space.get("uid")
								+ "' AND status='1' ORDER BY num DESC, dateline DESC LIMIT 0,500");
						for (Map<String, Object> value : userList) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("fuid"),
									(String) value.get("fusername"), "", 0);
						}
						request.setAttribute("userlist", userList);
					}
				}
				if (classId > 0) {
					whereSQL += " AND b.classid='" + classId + "'";
					theURL += "&classid=" + classId;
				}
				int friend = Common.intval(request.getParameter("friend"));
				paramMap.put("friend", new String[] {friend + ""});
				if (friend > 0) {
					whereSQL += " AND b.friend='" + friend + "'";
					theURL += "&friend=" + friend;
				}
				String searchKey = Common.stripSearchKey(request.getParameter("searchkey"));
				if (!Common.empty(searchKey)) {
					whereSQL += " AND b.subject LIKE '%" + searchKey + "%'";
					theURL += "&searchkey=" + request.getParameter("searchkey");
					Map<String, Object> resultMap = Common.ckSearch(theURL, request, response);
					if (resultMap != null) {
						return showMessage(request, response, (String) resultMap.get("msgkey"),
								(String) resultMap.get("url_forward"), (Integer) resultMap.get("second"),
								(String[]) resultMap.get("args"));
					}
				}
				request.setAttribute("searchkey", searchKey);

				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("blog") + " b WHERE " + whereSQL);
				if (("b.uid='" + space.get("uid") + "'").equals(whereSQL)
						&& (Integer) space.get("blognum") != count) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET blognum='" + count + "' WHERE uid='" + space.get("uid") + "'");
				}
				if (count > 0) {
					blogList = dataBaseService
							.executeQuery("SELECT bf.message, bf.target_ids, bf.magiccolor, b.* FROM "
									+ JavaCenterHome.getTableName("blog") + " b " + fIndex + " LEFT JOIN "
									+ JavaCenterHome.getTableName("blogfield")
									+ " bf ON bf.blogid=b.blogid	WHERE " + whereSQL + "	ORDER BY " + orderSql
									+ " DESC LIMIT " + start + "," + perPage);
				}
			}

			if (count > 0) {
				int inAjax = (Integer) sGlobal.get("inajax");
				int sumMarylen = 300;
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				int priCount = 0;
				for (Map<String, Object> value : blogList) {
					if (Common.ckFriend(sGlobal, space, (Integer) value.get("uid"), (Integer) value
							.get("friend"), (String) value.get("target_ids"))) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						if ((Integer) value.get("friend") == 4) {
							value.remove("message");
							value.remove("pic");
						} else {
							try {
								value.put("message", Common.getStr((String) value.get("message"), sumMarylen,
										false, false, false, 0, -1, request, response));
							} catch (Exception e) {
								return showMessage(request, response, e.getMessage());
							}
						}
						if (!Common.empty(value.get("pic"))) {
							value.put("pic", Common.pic_cover_get(sConfig, (String) value.get("pic"),
									(Integer) value.get("picflag")));
						}
						list.add(value);
					} else {
						priCount++;
					}
				}
				request.setAttribute("list", list);
				request.setAttribute("pricount", priCount);
			}
			request.setAttribute("multi", Common
					.multi(request, count, perPage, page, maxPage, theURL, "", ""));
			Common.realname_get(sGlobal, sConfig, sNames, space);

			if (!(Boolean) space.get("self")) {
				Map TPL = new HashMap();
				TPL.put("spacetitle", "日志");
				TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid") + "&do="
						+ request.getAttribute("do") + "&view=me\">TA的所有日志</a>"});
				request.setAttribute("TPL", TPL);
			}
			navTitle = "日志 - ";
			pageName = "space_blog_list.jsp";
			request.setAttribute("count", count);
			request.setAttribute("theurl", theURL);
			request.setAttribute("actives", actives);
		}

		Map<Integer, String> friendsName = new TreeMap<Integer, String>();
		friendsName.put(1, "仅好友可见");
		friendsName.put(2, "指定好友可见");
		friendsName.put(3, "仅自己可见");
		friendsName.put(4, "凭密码可见");
		request.setAttribute("friendsName", friendsName);
		request.setAttribute("tpl_css", "blog");
		request.setAttribute("navtitle", navTitle);
		request.setAttribute("clicks", clicks);
		return include(request, response, sConfig, sGlobal, pageName);
	}

	
	public ActionForward space_doing(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		request.setAttribute("navtitle", "记录 - ");
		int perPage = 20;
		perPage = Common.mobPerpage(request, perPage);
		int page = Common.intval(request.getParameter("page"));
		page = page < 1 ? 1 : page;
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		String view = request.getParameter("view");
		if (Common.empty(view)
				&& (space.get("friendnum") == null || (Integer) space.get("friendnum") < (Integer) sConfig
						.get("showallfriendnum"))) {
			Map<String, String[]> paramMap = request.getParameterMap();
			paramMap.put("view", new String[] {"all"});
			view = "all";
		}
		String fIndex = "";
		String whereSQL = null;
		String theURL = null;
		Map<String, String> actives = new HashMap<String, String>();
		if ("all".equals(view)) {
			whereSQL = "1";
			theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do") + "&view=all";
			fIndex = "USE INDEX(dateline)";
			actives.put("all", " class=\"active\"");
		} else {
			if (Common.empty(space.get("feedfriend"))) {
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("view", new String[] {"me"});
				view = "me";
			}
			if ("me".equals(view)) {
				whereSQL = "uid='" + (Integer) space.get("uid") + "'";
				theURL = "space.jsp?uid=" + (Integer) space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=me";
				actives.put("me", " class=\"active\"");
			} else {
				whereSQL = "uid IN (" + space.get("feedfriend") + "," + (Integer) space.get("uid") + ")";
				theURL = "space.jsp?uid=" + (Integer) space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=we";
				fIndex = "USE INDEX(dateline)";
				actives.put("we", " class=\"active\"");
			}
		}
		int count = 0;
		int doid = Common.intval(request.getParameter("doid"));
		if (doid > 0) {
			count = 1;
			whereSQL = "doid='" + doid + "'";
			theURL += "&doid=" + doid;
		}

		if (count == 0) {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("doing")
					+ " WHERE " + whereSQL);
			if (("uid='" + space.get("uid") + "'").equals(whereSQL) && space.get("doingnum") != null
					&& (Integer) space.get("doingnum") != count) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET doingnum='" + count + "' WHERE uid='" + space.get("uid") + "'");
			}
		}
		List doIds = null;
		List<Map<String, Object>> doList = null;
		Map<Integer, String> sNames = null;
		if (count > 0) {
			doList = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("doing")
					+ " " + fIndex + " WHERE " + whereSQL + " ORDER BY dateline DESC LIMIT " + start + ","
					+ perPage);
			sNames = (Map<Integer, String>) request.getAttribute("sNames");
			doIds = new ArrayList(doList.size());
			for (Map<String, Object> value : doList) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), "", 0);
				doIds.add(value.get("doid"));
			}
		}
		if (doid > 0) {
			Map doValue = Common.empty(doList) ? null : doList.get(0);
			if (doValue != null && !doValue.isEmpty()) {
				if (doValue.get("uid").equals(sGlobal.get("supe_uid"))) {
					actives.clear();
					actives.put("me", " class=\"active\"");
				} else {
					space = Common.getSpace(request, sGlobal, sConfig, doValue.get("uid"));
					request.setAttribute("space", space);
					actives.put("all", " class=\"active\"");
				}
			}
		}
		Map newDoIds = null;
		TreeService tree = null;
		if (doIds != null && !doIds.isEmpty()) {
			tree = new TreeService();
			newDoIds = new HashMap();
			List<Map<String, Object>> docommentList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("docomment") + " USE INDEX(dateline) WHERE doid IN ("
					+ Common.sImplode(doIds) + ") ORDER BY dateline");
			for (Map<String, Object> value : docommentList) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), "", 0);
				newDoIds.put(value.get("doid"), value.get("doid"));
				if (Common.empty(value.get("upid"))) {
					value.put("upid", "do" + value.get("doid"));
				}
				tree.setNode((Integer) value.get("id"), value.get("upid"), value);
			}
		}
		if (newDoIds != null && !newDoIds.isEmpty()) {
			Set doIdKeys = newDoIds.keySet();
			List<Object> values = null;
			Map one = null;
			String highlight = request.getParameter("highlight");
			Map<Object, List> clist = new HashMap<Object, List>();
			List tempList = null;
			for (Object doIdKey : doIdKeys) {
				values = tree.getChilds("do" + doIdKey);
				for (Object id : values) {
					one = tree.getValue(id);
					one.put("layer", tree.getLayer(id, 0) * 2 - 2);
					one.put("style", "padding-left:" + one.get("layer") + "em;");
					if (!Common.empty(highlight) && highlight.equals(one.get("id") + "")) {
						one.put("style", "color:red;font-weight:bold;");
					}
					tempList = clist.get(doIdKey);
					if (tempList == null) {
						tempList = new ArrayList(doIdKeys.size());
					}
					tempList.add(one);
					clist.put(doIdKey, tempList);
				}
			}
			request.setAttribute("clist", clist);
		}
		request.setAttribute("multi", Common
				.multi(request, count, perPage, page, maxPage, theURL, null, null));
		if (!Common.empty(space.get("mood")) && Common.empty(start)) {
			List<Map<String, Object>> ssfList = dataBaseService
					.executeQuery("SELECT s.uid,s.username,s.name,s.namestatus,s.mood,s.updatetime,s.groupid,sf.note,sf.sex FROM "
							+ JavaCenterHome.getTableName("space")
							+ " s LEFT JOIN "
							+ JavaCenterHome.getTableName("spacefield")
							+ " sf ON sf.uid=s.uid WHERE s.mood='"
							+ space.get("mood")
							+ "' ORDER BY s.updatetime DESC LIMIT 0,13");
			sNames = sNames == null ? (Map<Integer, String>) request.getAttribute("sNames") : sNames;
			List<Map<String, Object>> moodList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> value : ssfList) {
				if ((Integer) value.get("uid") != (Integer) space.get("uid")) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), (String) value.get("name"), (Integer) value.get("namestatus"));
					moodList.add(value);
					if (moodList.size() == 12) {
						break;
					}
				}
			}
			request.setAttribute("moodlist", moodList);
		}
		if (!(Boolean) space.get("self")) {
			Map TPL = new HashMap();
			TPL.put("spacetitle", "记录");
			TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
					+ "&do=doing&view=me\">TA的所有记录</a>"});
			request.setAttribute("TPL", TPL);
		}
		Common.realname_get(sGlobal, sConfig, sNames, space);
		request.setAttribute("tpl_css", "doing");
		request.setAttribute("count", count);
		request.setAttribute("dolist", doList);
		request.setAttribute("actives", actives);
		request.setAttribute("theurl", theURL);
		return include(request, response, sConfig, sGlobal, "space_doing.jsp");
	}

	
	public ActionForward space_event(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int eventId = Common.intval(request.getParameter("id"));
		String view = Common.trim(request.getParameter("view"));
		Map<Integer, Map<String, Object>> globalEventClass = Common.getCacheDate(request, response,
				"/data/cache/cache_eventclass.jsp", "globalEventClass");
		view = Common.empty(view) ? "all" : view;
		if (eventId > 0) {
			if ("me".equals(view)) {
				view = "all";
			}
			List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT e.*, ef.* FROM "
					+ JavaCenterHome.getTableName("event") + " e LEFT JOIN "
					+ JavaCenterHome.getTableName("eventfield")
					+ " ef ON e.eventid=ef.eventid WHERE e.eventid='" + eventId + "'");
			if (eventList.isEmpty()) {
				return showMessage(request, response, "event_does_not_exist");
			}
			Map<String, Object> event = eventList.get(0);
			if ((Integer) event.get("grade") == 0 && event.get("uid") != sGlobal.get("supe_uid")
					&& !Common.checkPerm(request, response, "manageevent")) {
				return showMessage(request, response, "event_under_verify");
			}
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			Common.realname_set(sGlobal, sConfig, sNames, (Integer) event.get("uid"), (String) event
					.get("username"), "", 0);
			List<Map<String, Object>> userEventList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("userevent") + " WHERE eventid='" + eventId + "' AND uid='"
					+ sGlobal.get("supe_uid") + "'");
			Map<String, Object> supeUserEvent = null;
			if (userEventList.isEmpty()) {
				sGlobal.put("supe_userevent", null);
			} else {
				supeUserEvent = userEventList.get(0);
				sGlobal.put("supe_userevent", supeUserEvent);
			}
			boolean allowManage = false;
			if ((supeUserEvent != null && (Integer) supeUserEvent.get("status") >= 3)
					|| Common.checkPerm(request, response, "manageevent")) {
				allowManage = true;
			}
			if ((Integer) event.get("public") == 0
					&& (supeUserEvent == null || (Integer) supeUserEvent.get("status") < 2) && !allowManage) {
				List<Map<String, Object>> eventinvite = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("eventinvite") + " WHERE eventid = '" + eventId
						+ "' AND touid = '" + sGlobal.get("supe_uid") + "' LIMIT 1");
				if (eventinvite.isEmpty()) {
					return showMessage(request, response, "event_not_public");
				}
			}
			if ("thread".equals(view) && (Integer) event.get("tagid") == 0) {
				view = "all";
			}
			if ("member".equals(view)) {
				String statusStr = request.getParameter("status");
				int status = statusStr != null ? Common.intval(statusStr) : 2;
				Map<String, String> submenus = new HashMap<String, String>();
				if (status > 1) {
					submenus.put("member", "class=\"active\"");
				} else if (status > 0) {
					submenus.put("follow", " class=\"active\"");
				} else if (status == 0) {
					submenus.put("verify", " class=\"active\"");
				}
				String statusSQL = null;
				String orderby = " ORDER BY ue.dateline ASC";
				if (status >= 2) {
					statusSQL = " AND ue.status >= 2";
					orderby = " ORDER BY ue.status DESC";
				} else {
					statusSQL = " AND ue.status = '" + status + "'";
				}
				String filter = "";
				String key = request.getParameter("key");
				if (!Common.empty(key)) {
					key = Common.stripSearchKey(key);
					filter = " AND ue.username LIKE '%" + key + "%' ";
				}
				int perPage = 10;
				int page = Common.intval(request.getParameter("page"));
				if (page < 1) {
					page = 1;
				}
				int start = (page - 1) * perPage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String result = Common.ckStart(start, perPage, maxPage);
				if (result != null) {
					return showMessage(request, response, result);
				}
				int count = dataBaseService.findRows("SELECT count(*) FROM "
						+ JavaCenterHome.getTableName("userevent") + " ue WHERE ue.eventid = '" + eventId
						+ "' " + statusSQL + " " + filter);
				List fuids = null;
				List<Map<String, Object>> members = null;
				if (count > 0) {
					members = dataBaseService.executeQuery("SELECT ue.*, sf.* FROM "
							+ JavaCenterHome.getTableName("userevent") + " ue LEFT JOIN "
							+ JavaCenterHome.getTableName("spacefield")
							+ " sf ON ue.uid = sf.uid WHERE ue.eventid = '" + eventId + "' " + statusSQL
							+ " " + filter + " " + orderby + " LIMIT " + start + ", " + perPage);
					fuids = new ArrayList(members.size());
					for (Map<String, Object> value : members) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						fuids.add(value.get("uid"));
						if (!Common.empty("template") && supeUserEvent != null
								&& (Integer) supeUserEvent.get("status") >= 3) {
							try {
								value.put("template", Common.nl2br(Common.getStr((String) value
										.get("template"), 255, true, false, false, 0, 0, request, response)));
							} catch (Exception e) {
								return showMessage(request, response, e.getMessage());
							}
						}
					}
				}
				if (fuids != null && fuids.size() > 0) {
					List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("session") + " WHERE uid IN ("
							+ Common.sImplode(fuids) + ")");
					Map ols = new HashMap();
					for (Map<String, Object> value : sessionList) {
						if (Common.empty(value.get("magichidden"))) {
							ols.put(value.get("uid"), value.get("lastactivity"));
						}
					}
					request.setAttribute("ols", ols);
				}
				if (supeUserEvent != null && (Integer) supeUserEvent.get("status") >= 3) {
					int verifyNum = 0;
					if (status == 0) {
						verifyNum = members == null ? 0 : members.size();
					} else {
						verifyNum = dataBaseService.findRows("SELECT count(*) FROM "
								+ JavaCenterHome.getTableName("userevent") + " WHERE eventid = '" + eventId
								+ "' AND status=0");
					}
					request.setAttribute("verifynum", verifyNum);
				}
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
						"space.jsp?do=event&id=" + eventId + "&view=member&status=" + status, null, null));
				request.setAttribute("status", status);
				request.setAttribute("submenus", submenus);
				request.setAttribute("members", members);
			} else if ("pic".equals(view)) {
				int picId = Common.intval(request.getParameter("picid"));
				int picCount = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("eventpic") + " WHERE eventid = '" + eventId + "'");
				if (picId > 0) {
					Map<String, String[]> paramMap = request.getParameterMap();
					paramMap.put("id", new String[] {"0"});
					List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("pic") + " WHERE picid='" + picId + "' LIMIT 1");
					if (picList.size() > 0) {
						Map<String, Object> pic = picList.get(0);
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) pic.get("uid"), (String) pic
								.get("username"), "", 0);
						request.setAttribute("eventId", eventId);
						request.setAttribute("eventPic", pic);
						request.setAttribute("picCount", picCount);
						try {
							invokeMethod(this, "space_album", request, response);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					int perPage = 12;
					int page = Common.intval(request.getParameter("page"));
					if (page < 1) {
						page = 1;
					}
					int start = (page - 1) * perPage;
					int maxPage = (Integer) sConfig.get("maxpage");
					String result = Common.ckStart(start, perPage, maxPage);
					if (result != null) {
						return showMessage(request, response, result);
					}
					String theURL = "space.jsp?do=event&id=" + eventId + "&view=pic";
					List badPicIds = new ArrayList();
					List<Map<String, Object>> eventpicList = dataBaseService
							.executeQuery("SELECT pic.*, ep.* FROM "
									+ JavaCenterHome.getTableName("eventpic") + " ep LEFT JOIN "
									+ JavaCenterHome.getTableName("pic")
									+ " pic ON ep.picid=pic.picid WHERE ep.eventid='" + eventId
									+ "' ORDER BY ep.picid DESC LIMIT " + start + ", " + perPage);
					List<Map<String, Object>> photoList = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> value : eventpicList) {
						if (Common.empty(value.get("filepath"))) {
							badPicIds.add(value.get("picid"));
							continue;
						}
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"),
								(Integer) value.get("thumb"), (Integer) value.get("remote"), true));
						photoList.add(value);
					}
					request.setAttribute("photolist", photoList);
					if (badPicIds.size() > 0) {
						picCount = picCount - badPicIds.size();
						dataBaseService.executeUpdate("DELETE FROM "
								+ JavaCenterHome.getTableName("eventpic") + " WHERE eventid='" + eventId
								+ "' AND picid IN (" + Common.sImplode(badPicIds) + ")");
					}
					if (picCount != (Integer) event.get("picnum")) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
								+ " SET picnum='" + picCount + "' WHERE eventid='" + eventId + "'");
					}
					request.setAttribute("multi", Common.multi(request, picCount, perPage, page, maxPage,
							theURL, null, null));
					request.setAttribute("piccount", picCount);
				}
				request.setAttribute("picid", picId);
			} else if ("thread".equals(view)) {
				int perPage = 20;
				int page = Common.intval(request.getParameter("page"));
				if (page < 1) {
					page = 1;
				}
				int start = (page - 1) * perPage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String result = Common.ckStart(start, perPage, maxPage);
				if (result != null) {
					return showMessage(request, response, result);
				}
				String theURL = "space.jsp?do=event&id=" + eventId + "&view=thread";
				int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("thread") + " WHERE eventid='" + eventId + "'");
				if (count > 0) {
					List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("thread") + " WHERE eventid='" + eventId
							+ "' ORDER BY lastpost DESC LIMIT " + start + "," + perPage);
					for (Map<String, Object> value : threadList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						if (!Common.empty(value.get("magicegg"))) {
							StringBuffer magiceggImage = new StringBuffer();
							for (int i = 0; i < (Integer) value.get("magicegg"); i++) {
								magiceggImage.append("<img src=\"image/magic/egg/" + Common.rand(1, 6)
										+ ".gif\" />");
							}
							value.put("magiceggImage", magiceggImage.toString());
						}
					}
					request.setAttribute("threadlist", threadList);
				}
				if (count != (Integer) event.get("threadnum")) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
							+ " SET threadnum='" + count + "' WHERE eventid='" + eventId + "'");
				}
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL,
						null, null));
			} else if ("comment".equals(view)) {
				int perPage = 20;
				int page = Common.intval(request.getParameter("page"));
				if (page < 1) {
					page = 1;
				}
				int start = (page - 1) * perPage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String result = Common.ckStart(start, perPage, maxPage);
				if (result != null) {
					return showMessage(request, response, result);
				}
				String theURL = "space.jsp?do=event&id=" + eventId + "&view=comment";
				int cid = Common.intval(request.getParameter("cid"));
				String csql = cid != 0 ? "cid='" + cid + "' AND" : "";
				request.setAttribute("cid", cid);

				int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("comment") + " WHERE " + csql + " id='" + eventId
						+ "' AND idtype='eventid'");
				if (count > 0) {
					List<Map<String, Object>> comments = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("comment") + " WHERE " + csql + " id='" + eventId
							+ "' AND idtype='eventid' ORDER BY dateline DESC LIMIT " + start + "," + perPage);
					for (Map<String, Object> value : comments) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
								(String) value.get("author"), "", 0);
					}
					request.setAttribute("comments", comments);
				}
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL,
						"", "comment_ul"));
			} else {
				event.put("detail", blogService.blogBBCode((String) event.get("detail")));
				if (!Common.empty(event.get("poster"))) {
					event.put("pic", Common.pic_get(sConfig, (String) event.get("poster"), (Integer) event
							.get("thumb"), (Integer) event.get("remote"), false));
				} else {
					event.put("pic", globalEventClass.get(event.get("classid")).get("poster"));
				}
				List<Map<String, Object>> admins = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("userevent") + " WHERE eventid = '" + eventId
						+ "' AND status IN ('3', '4') ORDER BY status DESC");
				List relateduids = new ArrayList(admins.size());
				for (Map<String, Object> value : admins) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					relateduids.add(value.get("uid"));
				}
				request.setAttribute("admins", admins);
				List<Map<String, Object>> members = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("userevent") + " WHERE eventid = '" + eventId
						+ "' AND status=2 ORDER BY dateline DESC LIMIT 14");
				for (Map<String, Object> value : members) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					relateduids.add(value.get("uid"));
				}
				request.setAttribute("members", members);
				List<Map<String, Object>> follows = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("userevent") + " WHERE eventid = '" + eventId
						+ "' AND status=1 ORDER BY dateline DESC LIMIT 12");
				for (Map<String, Object> value : follows) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
				}
				request.setAttribute("follows", follows);
				int verifyNum = 0;
				if (supeUserEvent != null && (Integer) supeUserEvent.get("status") >= 3) {
					verifyNum = dataBaseService.findRows("SELECT count(*) FROM "
							+ JavaCenterHome.getTableName("userevent") + " WHERE eventid = '" + eventId
							+ "' AND status=0");
				}
				Map<Object, Map<String, Object>> relatedEvents = new LinkedHashMap<Object, Map<String, Object>>();
				if (relateduids.size() > 0) {
					List<Map<String, Object>> userEventList2 = dataBaseService
							.executeQuery("SELECT e.*, ue.* FROM " + JavaCenterHome.getTableName("userevent")
									+ " ue LEFT JOIN " + JavaCenterHome.getTableName("event")
									+ " e ON ue.eventid=e.eventid WHERE ue.uid IN ("
									+ Common.sImplode(relateduids) + ") ORDER BY ue.dateline DESC LIMIT 0,8");
					for (Map<String, Object> value : userEventList2) {
						relatedEvents.put(value.get("eventid"), value);
						if ((Integer) sGlobal.get("timestamp") <= (Integer) value.get("endtime")) {
						}
					}
				}
				request.setAttribute("relatedevents", relatedEvents);
				List<Map<String, Object>> comments = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("comment") + " WHERE id = '" + eventId
						+ "' AND idtype='eventid' ORDER BY dateline DESC LIMIT 20");
				for (Map<String, Object> value : comments) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
							(String) value.get("author"), "", 0);
				}
				request.setAttribute("comments", comments);
				List badPicIds = new ArrayList();
				List<Map<String, Object>> eventpicList = dataBaseService
						.executeQuery("SELECT pic.*, ep.* FROM " + JavaCenterHome.getTableName("eventpic")
								+ " ep LEFT JOIN " + JavaCenterHome.getTableName("pic")
								+ " pic ON ep.picid = pic.picid WHERE ep.eventid='" + eventId
								+ "' ORDER BY ep.picid DESC LIMIT 10");
				List<Map<String, Object>> photoList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> value : eventpicList) {
					if (Common.empty(value.get("filepath"))) {
						badPicIds.add(value.get("picid"));
						continue;
					}
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"), (Integer) value
							.get("thumb"), (Integer) value.get("remote"), true));
					photoList.add(value);
				}
				request.setAttribute("photolist", photoList);
				if (badPicIds.size() > 0) {
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("eventpic")
							+ " WHERE eventid='" + eventId + "' AND picid IN (" + Common.sImplode(badPicIds)
							+ ")");
				}
				if (!Common.empty(event.get("tagid"))) {
					int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("thread") + " WHERE eventid='" + eventId + "'");
					if (count > 0) {
						List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("thread") + " WHERE eventid='" + eventId
								+ "' ORDER BY lastpost DESC LIMIT 10");
						for (Map<String, Object> value : threadList) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), "", 0);
						}
						request.setAttribute("threadlist", threadList);
					}
				}
				if (event.get("uid") != sGlobal.get("supe_uid")) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
							+ " SET viewnum=viewnum+1 WHERE eventid='" + eventId + "'");
					event.put("viewnum", (Integer) event.get("viewnum") + 1);
				}
				if ((Integer) event.get("starttime") > (Integer) sGlobal.get("timestamp")) {
					int starttime = (Integer) event.get("starttime");
					int timestamp = (Integer) sGlobal.get("timestamp");
					String[] startTimes = Common.gmdate("MM-dd-yyyy", starttime, "0").split("-");
					String[] timeStamps = Common.gmdate("MM-dd-yyyy", timestamp, "0").split("-");
					int countdown = (Common.mkTime(0, 0, 0, Integer.valueOf(startTimes[0]), Integer
							.valueOf(startTimes[1]), Integer.valueOf(startTimes[2])) - Common.mkTime(0, 0, 0,
							Integer.valueOf(timeStamps[0]), Integer.valueOf(timeStamps[1]), Integer
									.valueOf(timeStamps[2]))) / 86400;
					request.setAttribute("countdown", countdown);
				}
				request.setAttribute("verifynum", verifyNum);
				request.setAttribute("topic", Common.getTopic(request, (Integer) event.get("topicid")));
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			Map<String, String> menu = new HashMap<String, String>();
			menu.put(view, " class=\"active\"");
			request.setAttribute("menu", menu);
			request.setAttribute("event", event);
			request.setAttribute("tpl_css", "event");
			request.setAttribute("eventid", eventId);
			request.setAttribute("view", view);
			request.setAttribute("manageevent", Common.checkPerm(request, response, "manageevent"));
			request.setAttribute("navtitle", Common.empty(event.get("title")) ? "" : event.get("title")
					+ " - " + "活动 - ");
			return include(request, response, sConfig, sGlobal, "space_event_view.jsp");
		} else {
			if (!Common.in_array(new String[] {"friend", "me", "all", "recommend", "city"}, view)) {
				view = "all";
			}
			if ("friend".equals(view) && Common.empty(space.get("friendnum"))) {
				view = "me";
			}
			String type = request.getParameter("type");
			if ("all".equals(view) || "city".equals(view)) {
				type = "over".equals(type) ? type : "going";
			} else if ("me".equals(view) || "friend".equals(view)) {
				type = Common.in_array(new String[] {"join", "follow", "org", "self"}, type) ? type : "all";
			} else if ("recommend".equals(view)) {
				type = "admin".equals(type) ? type : "hot";
			}
			Map<String, String[]> paramMap = request.getParameterMap();
			Map<String, String> menu = new HashMap<String, String>();
			menu.put(view, " class=\"active\"");
			request.setAttribute("menu", menu);
			request.setAttribute("tpl_css", "event");
			request.setAttribute("navtitle", "活动 - ");
			request.setAttribute("type", type);
			request.setAttribute("view", view);
			if ("city".equals(view)) {
				String province = request.getParameter("province");
				if (Common.empty(province)) {
					province = (String) space.get("resideprovince");
					paramMap.put("province", new String[] {province});
					paramMap.put("city", new String[] {(String) space.get("residecity")});
					if (Common.empty(province)) {
						Map<String, String> submenus = new HashMap<String, String>();
						submenus.put(type, " class=\"active\"");
						request.setAttribute("submenus", submenus);
						return include(request, response, sConfig, sGlobal, "space_event_list.jsp");
					}
				}
			}
			if ("all".equals(view)) {
				List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("event")
						+ " WHERE grade = 2 ORDER BY recommendtime DESC LIMIT 4");
				List<Map<String, Object>> recommendEvents = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> value : eventList) {
					if ((Integer) value.get("deadline") > (Integer) sGlobal.get("timestamp")) {
						if (!Common.empty(value.get("poster"))) {
							value.put("pic", Common.pic_get(sConfig, (String) value.get("poster"),
									(Integer) value.get("thumb"), (Integer) value.get("remote"), true));
						} else {
							value.put("pic", globalEventClass.get(value.get("classid")).get("poster"));
						}
						recommendEvents.add(value);
					}
				}
				request.setAttribute("recommendevents", recommendEvents);
			}
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if ("friend".equals(view)) {
				List<Map<String, Object>> hotEvents = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("event") + " WHERE endtime > '"
						+ sGlobal.get("timestamp") + "' ORDER BY membernum LIMIT 6");
				for (Map<String, Object> value : hotEvents) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
				}
				request.setAttribute("hotevents", hotEvents);
			}
			if (!Common.empty(space.get("feedfriend")) && !"friend".equals(view) && !"me".equals(view)) {
				List<Map<String, Object>> friendEventList = dataBaseService
						.executeQuery("SELECT ue.*, e.*, ue.uid as fuid, ue.username as fusername FROM "
								+ JavaCenterHome.getTableName("userevent") + " ue LEFT JOIN "
								+ JavaCenterHome.getTableName("event")
								+ " e ON ue.eventid=e.eventid WHERE ue.uid IN (" + space.get("feedfriend")
								+ ") AND ue.status >= 2 ORDER BY ue.dateline DESC LIMIT 6");
				LinkedHashMap<Object, Map<String, Object>> friendEvents = new LinkedHashMap<Object, Map<String, Object>>();
				Map<String, Object> tempMap = null;
				List tempList = null;
				for (Map<String, Object> value : friendEventList) {
					if (friendEvents.get(value.get("eventid")) != null) {
						tempMap = friendEvents.get(value.get("eventid"));
						tempList = (List) tempMap.get("friends");
						tempList.add(value.get("fuid"));
						tempMap.put("friends", tempList);
						friendEvents.put(value.get("eventid"), tempMap);
					} else {
						tempList = new ArrayList();
						tempList.add(value.get("fuid"));
						value.put("friends", tempList);
						friendEvents.put(value.get("eventid"), value);
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("fuid"),
								(String) value.get("fusername"), "", 0);
					}
				}
				request.setAttribute("friendevents", friendEvents);
			}
			if (!"me".equals(view)) {
				List<Map<String, Object>> followEvents = dataBaseService
						.executeQuery("SELECT ue.*, e.* FROM " + JavaCenterHome.getTableName("userevent")
								+ " ue LEFT JOIN " + JavaCenterHome.getTableName("event")
								+ " e ON ue.eventid=e.eventid WHERE ue.uid = '" + sGlobal.get("supe_uid")
								+ "' AND ue.status = 1 ORDER BY ue.dateline LIMIT 6");
				for (Map<String, Object> value : followEvents) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
				}
				request.setAttribute("followevents", followEvents);
			}
			int perPage = 10;
			int page = Common.intval(request.getParameter("page"));
			if (page < 1) {
				page = 1;
			}
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			String uidStr = request.getParameter("uid");
			int uid = Common.empty(uidStr) ? (Integer) sGlobal.get("supe_uid") : Common.intval(uidStr);
			String theURL = "space.jsp?uid=" + uid + "&do=event&view=" + view;

			List<String> wheres = new ArrayList<String>();
			String fromSQL = "";
			String joinSQL = "";
			String orderby = "";
			String SQL = "";
			boolean needQuery = true;
			int count = 0;
			if ("recommend".equals(view)) {
				fromSQL = JavaCenterHome.getTableName("event") + " e";
				if ("admin".equals(type)) {
					wheres.add("e.grade = 2");
					orderby = "e.recommendtime DESC";
					theURL += "&type=admin";
				} else {
					wheres.add("e.endtime > '" + sGlobal.get("timestamp") + "'");
					orderby = "e.membernum DESC";
					theURL += "&type=hot";
				}
			} else if ("city".equals(view) || "all".equals(view)) {
				fromSQL = JavaCenterHome.getTableName("event") + " e";
				if ("over".equals(type)) {
					wheres.add("e.endtime < '" + sGlobal.get("timestamp") + "'");
					orderby = "e.eventid DESC";
					theURL += "&type=over";
				} else {
					wheres.add("e.endtime >= '" + sGlobal.get("timestamp") + "'");
					orderby = "e.eventid DESC";
					theURL += "&type=going";
				}
			} else if ("friend".equals(view)) {
				SQL = "SELECT DISTINCT(eventid) FROM " + JavaCenterHome.getTableName("userevent")
						+ " WHERE uid IN (" + space.get("feedfriend") + ")";
				if ("follow".equals(type)) {
					SQL += " AND status IN (0,1)";
					theURL += "&type=follow";
				} else if ("org".equals(type)) {
					SQL += " AND status IN (3,4)";
					theURL += "&type=org";
				} else if ("join".equals(type)) {
					SQL += " AND status IN (2,3,4)";
					theURL += "&type=join";
				}
				Map<String, Object> sucess = dataBaseService.execute(SQL);
				count = (Integer) sucess.get("sucess");
				if (count > 0) {
					SQL += " ORDER BY eventid DESC LIMIT " + start + ", " + perPage;
					List<Map<String, Object>> userEventList = dataBaseService.executeQuery(SQL);
					List ids = new ArrayList(userEventList.size());
					for (Map<String, Object> value : userEventList) {
						ids.add(value.get("eventid"));
					}
					fromSQL = JavaCenterHome.getTableName("event") + " e";
					joinSQL = "LEFT JOIN " + JavaCenterHome.getTableName("userevent")
							+ " ue ON e.eventid = ue.eventid";
					wheres.add("e.eventid IN (" + Common.sImplode(ids) + ")");
					orderby = " e.eventid DESC";
					SQL = "SELECT e.*, ue.uid as fuid, ue.username as fusername, ue.status FROM " + fromSQL
							+ " " + joinSQL + " WHERE " + Common.implode(wheres, " AND ");
				}
				needQuery = false;
			} else if ("me".equals(view)) {
				fromSQL = JavaCenterHome.getTableName("userevent") + " ue";
				joinSQL = "LEFT JOIN " + JavaCenterHome.getTableName("event") + " e ON e.eventid=ue.eventid";
				orderby = "ue.dateline DESC";
				wheres.add("ue.uid = '" + space.get("uid") + "'");
				if ("follow".equals(type)) {
					wheres.add("ue.status IN (0,1)");
					theURL += "&type=follow";
				} else if ("org".equals(type)) {
					wheres.add("ue.status IN (3,4)");
					theURL += "&type=org";
				} else if ("join".equals(type)) {
					wheres.add("ue.status IN (2,3,4)");
					theURL += "&type=join";
				} else if ("self".equals(type)) {
					needQuery = false;
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("event") + " WHERE uid='" + space.get("uid")
							+ "' LIMIT 1");
					if ((Integer) space.get("eventnum") != count) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET eventnum='" + count + "' WHERE uid='" + space.get("uid") + "'");
					}
					SQL = "SELECT * FROM " + JavaCenterHome.getTableName("event") + " e WHERE e.uid='"
							+ space.get("uid") + "' ORDER BY e.dateline DESC LIMIT " + start + ", " + perPage;
				}
				if (!Common.empty(request.getParameter("classid"))
						|| !Common.empty(request.getParameter("date"))
						|| !Common.empty(request.getParameter("province"))
						|| !Common.empty(request.getParameter("city"))) {
					fromSQL = JavaCenterHome.getTableName("userevent") + " ue, "
							+ JavaCenterHome.getTableName("event") + " e";
					wheres.add(" ue.eventid = e.eventid");
					joinSQL = "";
				}
			}
			String classIdStr = request.getParameter("classid");
			if (!Common.empty(classIdStr)) {
				int classId = Common.intval(classIdStr);
				paramMap.put("classid", new String[] {classId + ""});
				wheres.add("e.classid = '" + classId + "'");
				theURL += "&classid=" + classId;
			}
			String date = request.getParameter("date");
			if (!Common.empty(date)) {
				int dayStart = Common.strToTime(date, Common.getTimeOffset(sGlobal, sConfig));
				int dayEnd = dayStart + 86400;
				wheres.add("e.starttime <= '" + dayEnd + "' AND e.endtime >= '" + dayStart + "'");
				theURL += "&date=" + date;
			}
			String province = request.getParameter("province");
			if (!Common.empty(province)) {
				try {
					province = Common.getStr(province, 20, true, true, false, 0, 0, request, response);
				} catch (Exception e) {
					return showMessage(request, response, e.getMessage());
				}
				paramMap.put("province", new String[] {province});
				wheres.add("e.province ='" + province + "'");
				theURL += "&province=" + province;
			}
			String city = request.getParameter("city");
			if (!Common.empty(city)) {
				try {
					city = Common.getStr(city, 20, true, true, false, 0, 0, request, response);
				} catch (Exception e) {
					return showMessage(request, response, e.getMessage());
				}
				paramMap.put("city", new String[] {city});
				wheres.add("e.city ='" + city + "'");
				theURL += "&city=" + city;
			}
			Map<String, String> submenus = new HashMap<String, String>();
			submenus.put(type, " class=\"active\"");
			String searchKey = Common.stripSearchKey(request.getParameter("searchkey"));
			if (!Common.empty(searchKey)) {
				wheres = new ArrayList<String>();
				submenus = null;
				wheres.add("e.title LIKE '%" + searchKey + "%'");
				theURL += "&searchkey=" + searchKey;
				Map<String, Object> resultMap = Common.ckSearch(theURL, request, response);
				if (resultMap != null) {
					return showMessage(request, response, (String) resultMap.get("msgkey"),
							(String) resultMap.get("url_forward"), (Integer) resultMap.get("second"),
							(String[]) resultMap.get("args"));
				}
				request.setAttribute("searchkey", searchKey);
			}
			if (wheres.isEmpty()) {
				wheres.add("1");
			}
			if (needQuery) {
				SQL = "SELECT COUNT(*) FROM " + fromSQL + " WHERE " + Common.implode(wheres, " AND ");
				count = dataBaseService.findRows(SQL);
			}
			if (count > 0) {
				if (needQuery) {
					SQL = "SELECT e.* FROM " + fromSQL + " " + joinSQL + " WHERE "
							+ Common.implode(wheres, " AND ") + " ORDER BY " + orderby + " LIMIT " + start
							+ ", " + perPage;
				}
				LinkedHashMap<Object, Map<String, Object>> eventMap = new LinkedHashMap<Object, Map<String, Object>>();
				LinkedHashMap<Object, List<Map<String, Object>>> fevents = new LinkedHashMap<Object, List<Map<String, Object>>>();
				List<Map<String, Object>> eventList = dataBaseService.executeQuery(SQL);
				List<Map<String, Object>> tempList = null;
				LinkedHashMap<String, Object> tempMap = null;
				for (Map<String, Object> event : eventList) {
					if (!Common.empty(event.get("poster"))) {
						event.put("pic", Common.pic_get(sConfig, (String) event.get("poster"),
								(Integer) event.get("thumb"), (Integer) event.get("remote"), true));
					} else {
						event.put("pic", globalEventClass.get(event.get("classid")).get("poster"));
					}
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) event.get("uid"), (String) event
							.get("username"), "", 0);
					if ("friend".equals(view)) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) event.get("fuid"),
								(String) event.get("fusername"), "", 0);
						tempList = fevents.get(event.get("eventid"));
						if (tempList == null) {
							tempList = new ArrayList<Map<String, Object>>();
						}
						tempMap = new LinkedHashMap<String, Object>();
						tempMap.put("fuid", event.get("fuid"));
						tempMap.put("fusername", event.get("fusername"));
						tempMap.put("status", event.get("status"));
						tempList.add(tempMap);
						fevents.put(event.get("eventid"), tempList);
					}
					eventMap.put(event.get("eventid"), event);
				}
				request.setAttribute("fevents", fevents);
				request.setAttribute("eventlist", eventMap);
			}
			if (!Common.empty(sGlobal.get("inajax"))) {
				request.setAttribute("count", count);
			} else {
				if (!(Boolean) (space.get("self"))) {
					Map TPL = new HashMap();
					TPL.put("spacetitle", "活动");
					TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
							+ "&do=event&view=me\">TA的所有活动</a>"});
					request.setAttribute("TPL", TPL);
				}
				request.setAttribute("theurl", theURL);
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL, null,
					null));
			request.setAttribute("submenus", submenus);
			return include(request, response, sConfig, sGlobal, "space_event_list.jsp");
		}
	}

	
	public ActionForward space_feed(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (Common.empty(sConfig.get("showallfriendnum")) || (Integer) sConfig.get("showallfriendnum") < 1) {
			sConfig.put("showallfriendnum", 10);
		}
		if (Common.empty(sConfig.get("feedhotday"))) {
			sConfig.put("feedhotday", 2);
		}
		boolean isNewer = space.get("friendnum") == null
				|| (Integer) space.get("friendnum") < (Integer) sConfig.get("showallfriendnum") ? true
				: false;
		Map<String, String[]> paramMap = request.getParameterMap();
		String view = request.getParameter("view");
		if (Common.empty(view) && (Boolean) space.get("self") && isNewer) {
			paramMap.put("view", new String[] {"all"});
			view = "all";
		}
		int feedMaxNum = (Integer) sConfig.get("feedmaxnum");
		int perPage = feedMaxNum < 50 ? 50 : feedMaxNum;
		perPage = Common.mobPerpage(request, perPage);

		if ("hot".equals(view)) {
			perPage = 50;
		}

		int start = Common.intval(request.getParameter("start"));
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		sGlobal.put("today", Common.strToTime(Common.sgmdate(request, "yyyy-MM-dd", 0), Common.getTimeOffset(
				sGlobal, sConfig)));
		int feedHotMin = (Integer) sConfig.get("feedhotmin");
		int minHot = feedHotMin < 1 ? 3 : feedHotMin;
		sGlobal.put("gift_appid", "1027468");

		String action = (String) request.getAttribute("do");
		String whereSQL = null;
		String orderSQL = null;
		String theURL = null;
		String f_index = null;

		Map TPL = request.getAttribute("TPL") != null ? (Map) request.getAttribute("TPL") : new HashMap();
		if ("all".equals(view)) {
			whereSQL = "1";
			orderSQL = "dateline DESC";
			theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + action + "&view=all";
			f_index = "";
		} else if ("hot".equals(view)) {
			whereSQL = "hot>=" + minHot;
			orderSQL = "dateline DESC";
			theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + action + "&view=hot";
			f_index = "";
		} else {
			if (Common.empty(space.get("feedfriend"))) {
				paramMap.put("view", new String[] {"me"});
				view = "me";
			}
			if ("me".equals(view)) {
				whereSQL = "uid='" + space.get("uid") + "'";
				orderSQL = "dateline DESC";
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + action + "&view=me";
				f_index = "";
			} else {
				whereSQL = "uid IN ('0'," + space.get("feedfriend") + ")";
				orderSQL = "dateline DESC";
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + action + "&view=we";
				f_index = "USE INDEX(dateline)";
				paramMap.put("view", new String[] {"we"});
				view = "we";
				TPL.put("hidden_time", 1);
			}
		}
		int appId = Common.intval(request.getParameter("appid"));
		if (appId > 0) {
			whereSQL += " AND appid='" + appId + "'";
		}
		String icon = Common.trim(request.getParameter("icon"));
		if (!Common.empty(icon)) {
			whereSQL += " AND icon='" + icon + "'";
		}
		String filter = Common.trim(request.getParameter("filter"));
		if ("site".equals(filter)) {
			whereSQL += " AND appid>0";
		} else if ("myapp".equals(filter)) {
			whereSQL += " AND appid='0'";
		}

		int count = 0;
		List<Map<String, Object>> feedList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("feed") + " " + f_index + "	WHERE " + whereSQL + "	ORDER BY "
				+ orderSQL + "	LIMIT " + start + "," + perPage);
		LinkedHashMap<Object, Map> feed_list = new LinkedHashMap<Object, Map>();
		LinkedHashMap<Object, LinkedHashMap> appfeedList = new LinkedHashMap<Object, LinkedHashMap>();
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		if (view.equals("me") || view.equals("hot")) {
			for (Map<String, Object> value : feedList) {
				if (Common.ckFriend(sGlobal, space, (Integer) value.get("uid"),
						(Integer) value.get("friend"), (String) value.get("target_ids"))) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					feed_list.put(value.get("feedid"), value);
				}
				count++;
			}
		} else {
			String[] hidden_icons = null;
			String feedHiddenIcon = (String) sConfig.get("feedhiddenicon");
			if (!Common.empty(feedHiddenIcon)) {
				sConfig.put("feedhiddenicon", (feedHiddenIcon = feedHiddenIcon.replace(" ", "")));
				hidden_icons = feedHiddenIcon.split(",");
			}
			Map privacy = (Map) space.get("privacy");
			Map filterIcon = privacy == null ? null : (Map) privacy.get("filter_icon");
			space.put("filter_icon", Common.empty(filterIcon) ? new HashSet() : filterIcon.keySet());
			LinkedHashMap hashData = new LinkedHashMap();
			LinkedHashMap icon_num = new LinkedHashMap();
			List tempList = null;
			boolean isMyApp = false;
			LinkedHashMap hashDataMap = null;
			int filterCount = 0;
			List<Map<String, Object>> filter_list = new ArrayList<Map<String, Object>>();
			LinkedHashMap hiddenfeed_num = new LinkedHashMap();
			LinkedHashMap<Object, List<Map>> hiddenfeed_list = new LinkedHashMap<Object, List<Map>>();
			for (Map<String, Object> value : feedList) {
				LinkedHashMap tempMap = (LinkedHashMap) feed_list.get(value.get("hash_data"));
				if (Common.empty(tempMap) || Common.empty(tempMap.get(value.get("uid")))) {
					if (Common.ckFriend(sGlobal, space, (Integer) value.get("uid"), (Integer) value
							.get("friend"), (String) value.get("target_ids"))) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						if (ckIconUid(value, space)) {
							isMyApp = Common.isNumeric(value.get("icon")) ? true : false;
							if (!Common.empty(sConfig.get("my_showgift"))
									&& value.get("icon").toString().equals(sGlobal.get("gift_appid"))) {
								isMyApp = false;
							}
							if (((isMyApp && Common.in_array(hidden_icons, "myop")) || Common.in_array(
									hidden_icons, value.get("icon")))
									&& !Common.empty(icon_num.get(value.get("icon")))) {
								hiddenfeed_num.put(value.get("icon"),
										hiddenfeed_num.get(value.get("icon")) == null ? 1
												: ((Integer) hiddenfeed_num.get(value.get("icon"))) + 1);
								tempList = hiddenfeed_list.get(value.get("icon"));
								if (tempList == null) {
									tempList = new ArrayList();
								}
								Common.mkFeed(sNames, sConfig, request, value, null);
								tempList.add(value);
								hiddenfeed_list.put(value.get("icon"), tempList);
							} else {
								if (isMyApp) {
									if (appfeedList.get(value.get("hash_data")) == null) {
										hashDataMap = new LinkedHashMap();
									}
									hashDataMap.put(value.get("uid"), value);
									appfeedList.put(value.get("hash_data"), hashDataMap);
								} else {
									if ((hashDataMap = (LinkedHashMap) feed_list.get(value.get("hash_data"))) == null) {
										hashDataMap = new LinkedHashMap();
									}
									hashDataMap.put(value.get("uid"), value);
									feed_list.put(value.get("hash_data"), hashDataMap);
								}
							}
							icon_num.put(value.get("icon"), icon_num.get(value.get("icon")) == null ? 1
									: ((Integer) icon_num.get(value.get("icon"))) + 1);
						} else {
							filterCount++;
							value = Common.mkFeed(sNames, sConfig, request, value, null);
							filter_list.add(value);
						}
					}
				}
				count++;
			}
			request.setAttribute("hiddenfeed_list", hiddenfeed_list);
			request.setAttribute("hiddenfeed_num", hiddenfeed_num);
			request.setAttribute("filtercount", filterCount);
			request.setAttribute("filter_list", filter_list);
		}
		LinkedHashMap<Object, Map<String, Object>> hotList = new LinkedHashMap<Object, Map<String, Object>>();
		if ((Boolean) space.get("self") && Common.empty(start)){
			LinkedHashMap<Object, Map<String, Object>> hotListAll = new LinkedHashMap<Object, Map<String, Object>>();
			if ((Integer) sConfig.get("feedhotnum") > 0 && (view.equals("we") || view.equals("all"))) {
				double hotStartTime = (Integer) sGlobal.get("timestamp") - Double.parseDouble(sConfig.get("feedhotday").toString())
						* 3600 * 24;
				List<Map<String, Object>> feeds = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("feed") + " USE INDEX(hot) WHERE dateline>='"
						+ hotStartTime + "' ORDER BY hot DESC LIMIT 0,10");
				for (Map<String, Object> value : feeds) {
					if ((Integer) value.get("hot") > 0
							&& Common.ckFriend(sGlobal, space, (Integer) value.get("uid"), (Integer) value
									.get("friend"), (String) value.get("target_ids"))) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						value = Common.mkFeed(sNames, sConfig, request, value, null);
						if (Common.empty(hotList)) {
							hotList.put(value.get("feedid"), value);
						} else {
							hotListAll.put(value.get("feedid"), value);
						}
					}
				}
				int nextHotNum = (Integer) sConfig.get("feedhotnum") - 1;
				if (nextHotNum > 0) {
					if (hotListAll.size() > nextHotNum) {
						Object[] hotListKey = Common.arrayRand(hotListAll, nextHotNum);
						if (nextHotNum == 1) {
							hotList.put(hotListKey[0], hotListAll.get(hotListKey[0]));
						} else {
							for (Object key : hotListKey) {
								hotList.put(key, hotListAll.get(key));
							}
						}
					} else {
						hotList = hotListAll;
					}
				}

			}
		}
		Common.realname_get(sGlobal, sConfig, sNames, space);
		LinkedHashMap list = new LinkedHashMap();
		Set feedKeys = feed_list.keySet();
		if (view.equals("hot")) {
			Map value = null;
			List tempList = null;
			for (Object feedKey : feedKeys) {
				value = feed_list.get(feedKey);
				value = Common.mkFeed(sNames, sConfig, request, value, null);
				if (list.get("today") == null) {
					tempList = new ArrayList(feedKeys.size());
				}
				tempList.add(value);
				list.put("today", tempList);
			}
		} else if ("me".equals(view)) {
			int dateline = 0;
			int today = (Integer) sGlobal.get("today");
			String theday = null;
			Map value = null;
			List todayList = null;
			List yesterdayList = null;
			List thedayList = null;
			for (Object feedKey : feedKeys) {
				value = feed_list.get(feedKey);
				if (!Common.empty(hotList.get(value.get("feedid")))) {
					continue;
				}
				value = Common.mkFeed(sNames, sConfig, request, value, null);
				dateline = (Integer) value.get("dateline");
				if (dateline >= today) {
					if (list.get("today") == null) {
						todayList = new ArrayList();
					}
					todayList.add(value);
					list.put("today", todayList);
				} else if (dateline >= today - 3600 * 24) {
					if (list.get("yesterday") == null) {
						yesterdayList = new ArrayList();
					}
					yesterdayList.add(value);
					list.put("yesterday", yesterdayList);
				} else {
					theday = Common.sgmdate(request, "yyyy-MM-dd", dateline);
					if (list.get(theday) == null) {
						thedayList = new ArrayList();
					}
					thedayList.add(value);
					list.put(theday, thedayList);
				}
			}
		} else {
			int dateline = 0;
			int today = (Integer) sGlobal.get("today");
			String theday = null;
			List todayList = null;
			List yesterdayList = null;
			List thedayList = null;
			for (Object feedKey : feedKeys) {
				Map<Object, Map> values = feed_list.get(feedKey);
				Set keys = values.keySet();
				List actors = new ArrayList(keys.size());
				Map a_value = null;
				for (Object key : keys) {
					Map value = values.get(key);
					if (Common.empty(a_value)) {
						a_value = value;
					}
					actors.add("<a href=\"space.jsp?uid=" + value.get("uid") + "\">"
							+ sNames.get((Integer) value.get("uid")) + "</a>");
				}
				if (!Common.empty(hotList.get(a_value.get("feedid")))) {
					continue;
				}
				a_value = Common.mkFeed(sNames, sConfig, request, a_value, actors);
				dateline = (Integer) a_value.get("dateline");
				if (dateline >= today) {
					if (list.get("today") == null) {
						todayList = new ArrayList();
					}
					todayList.add(a_value);
					list.put("today", todayList);
				} else if (dateline >= today - 3600 * 24) {
					if (list.get("yesterday") == null) {
						yesterdayList = new ArrayList();
					}
					yesterdayList.add(a_value);
					list.put("yesterday", yesterdayList);
				} else {
					theday = Common.sgmdate(request, "yyyy-MM-dd", dateline);
					if (list.get(theday) == null) {
						thedayList = new ArrayList();
					}
					thedayList.add(a_value);
					list.put(theday, thedayList);
				}
			}
			Map value = null;
			List appList = null;
			Set appFeedKeys = appfeedList.keySet();
			for (Object appFeedKey : appFeedKeys) {
				Map<Object, Map> values = appfeedList.get(appFeedKey);
				Set keys = values.keySet();
				Map a_value = null;
				List actors = new ArrayList(keys.size());
				for (Object key : keys) {
					value = values.get(key);
					if (Common.empty(a_value)) {
						a_value = value;
					}
					actors.add("<a href=\"space.jsp?uid=" + value.get("uid") + "\">"
							+ sNames.get((Integer) value.get("uid")) + "</a>");
				}
				a_value = Common.mkFeed(sNames, sConfig, request, a_value, actors);
				if (list.get("app") == null) {
					appList = new ArrayList();
				}
				appList.add(a_value);
				list.put("app", appList);
			}
		}
		Map myActives = new LinkedHashMap();
		myActives.put(Common.in_array(new String[] {"site", "myapp"}, filter) ? filter : "all",
				" class=\"active\"");
		Map actives = new LinkedHashMap();
		actives.put(Common.in_array(new String[] {"me", "all", "hot"}, view) ? view : "we",
				" class=\"active\"");
		LinkedHashMap<String,String> icons=new LinkedHashMap<String, String>();
		icons.put("doing", "记录");
		icons.put("blog", "撰写日志");
		icons.put("album", "上传图片");
		icons.put("share", "添加分享");
		icons.put("poll", "发起/参与投票");
		icons.put("thread", "发起话题");
		icons.put("post", "对话题回复");
		icons.put("mtag", "加入群组");
		icons.put("event", "发起/参与活动");
		icons.put("friend", "添加好友");
		icons.put("comment", "发表评论");
		icons.put("wall", "发表留言");
		icons.put("show", "竞价排名");
		icons.put("task", "完成任务");
		icons.put("profile", "更新个人资料");
		icons.put("click", "对日志/图片/话题表态");
		Map feedIcon_actives=new HashMap();
		feedIcon_actives.put(icons.keySet().contains(icon) ? icon : "all"," class=\"current\"");
		request.setAttribute("icons", icons);
		request.setAttribute("feedIcon_actives", feedIcon_actives);
		if ((Integer) space.get("uid") > 0 && !(Boolean) space.get("self")) {
			request.setAttribute("isAdmin", Common.checkPerm(request, response, "admin"));
			request.setAttribute("allowstat", Common.checkPerm(request, response, "allowstat"));
			Map userGroup = (Map) request.getAttribute("usergroup" + space.get("groupid"));
			if (userGroup != null) {
				request.setAttribute("gColor", Common.getColor(userGroup));
				request.setAttribute("gIcon", Common.getIcon(userGroup));
			}
			TPL.put("spacetitle", "动态");
			TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
					+ "&do=feed&view=me\">TA的近期动态</a>"});
		}
		request.setAttribute("count", count);
		request.setAttribute("perpage", perPage);
		request.setAttribute("start", start);
		request.setAttribute("navtitle", "动态 - ");
		request.setAttribute("isnewer", isNewer);
		request.setAttribute("actives", actives);
		request.setAttribute("hotlist", hotList);
		request.setAttribute("TPL", TPL);
		request.setAttribute("list", list);
		return include(request, response, sConfig, sGlobal, "space_feed.jsp");
	}

	
	public ActionForward space_friend(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		int perPage = 24;
		perPage = Common.mobPerpage(request, perPage);

		int count = 0;
		int page = Common.intval(request.getParameter("page"));
		if (page < 1)
			page = 1;
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		LinkedHashMap<Object, Map<String, Object>> list = new LinkedHashMap<Object, Map<String, Object>>();
		Map<String, String> actives = new HashMap<String, String>();
		List fuids = new ArrayList();
		String view = request.getParameter("view");
		String type = request.getParameter("type");
		if ("online".equals(view)) {
			String theURL = "space.jsp?uid=" + space.get("uid") + "&do=friend&view=online";
			actives.put("me", " class=\"active\"");
			String whereSQL = null;
			if ("near".equals(type)) {
				theURL += "&type=near";
				whereSQL = " WHERE main.ip='" + Common.getOnlineIP(request, true) + "'";
			} else if ("friend".equals(type) && !Common.empty(space.get("feedfriend"))) {
				theURL += "&type=friend";
				whereSQL = " WHERE main.uid IN (" + space.get("feedfriend") + ")";
			} else {
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("type", new String[] {"all"});
				theURL += "&type=all";
				whereSQL = " WHERE 1";
			}
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("session")
					+ " main " + whereSQL);
			if (count > 0) {
				List<Map<String, Object>> sessionList = dataBaseService
						.executeQuery("SELECT f.resideprovince, f.residecity, f.sex, f.note, f.spacenote, main.* FROM "
								+ JavaCenterHome.getTableName("session")
								+ " main LEFT JOIN "
								+ JavaCenterHome.getTableName("spacefield")
								+ " f ON f.uid=main.uid "
								+ whereSQL
								+ " ORDER BY main.lastactivity DESC LIMIT "
								+ start
								+ ","
								+ perPage);
				Map ols = new HashMap();
				request.setAttribute("ols", ols);
				for (Map<String, Object> value : sessionList) {
					if (!Common.empty(value.get("magichidden"))) {
						count = count - 1;
						continue;
					}
					if ("near".equals(type)) {
						if (value.get("uid") == space.get("uid")) {
							count = count - 1;
							continue;
						}
					}
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					value.put("p", Common.urlEncode((String) value.get("resideprovince")));
					value.put("c", Common.urlEncode((String) value.get("residecity")));
					value.put("isfriend", (value.get("uid").equals(space.get("uid")) || (!Common.empty(space
							.get("friends")) && Common.in_array((String[]) space.get("friends"), value
							.get("uid")))) ? true : false);
					ols.put(value.get("uid"), value.get("lastactivity"));
					try {
						value.put("note", Common.getStr((String) value.get("note"), 35, false, false, false,
								0, -1, request, response));
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
					list.put(value.get("uid"), value);
				}
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL, null,
					null));
		} else if ("visitor".equals(view) || "trace".equals(view)) {
			String theURL = "space.jsp?uid=" + space.get("uid") + "&do=friend&view=" + view;
			actives.put("me", " class=\"active\"");
			List<Map<String, Object>> visitorList = null;
			if ("visitor".equals(view)) {
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("visitor") + " main WHERE main.uid='"
						+ space.get("uid") + "'");
				visitorList = dataBaseService
						.executeQuery("SELECT f.resideprovince, f.residecity, f.note, f.spacenote, f.sex, main.vuid AS uid, main.vusername AS username, main.dateline FROM "
								+ JavaCenterHome.getTableName("visitor")
								+ " main LEFT JOIN "
								+ JavaCenterHome.getTableName("spacefield")
								+ " f ON f.uid=main.vuid WHERE main.uid='"
								+ space.get("uid")
								+ "' ORDER BY main.dateline DESC LIMIT " + start + "," + perPage);
			} else {
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("visitor") + " main WHERE main.vuid='"
						+ space.get("uid") + "'");
				visitorList = dataBaseService
						.executeQuery("SELECT s.username, s.name, s.namestatus, s.groupid, f.resideprovince, f.residecity, f.note, f.spacenote, f.sex, main.uid AS uid, main.dateline FROM "
								+ JavaCenterHome.getTableName("visitor")
								+ " main LEFT JOIN "
								+ JavaCenterHome.getTableName("space")
								+ " s ON s.uid=main.uid LEFT JOIN "
								+ JavaCenterHome.getTableName("spacefield")
								+ " f ON f.uid=main.uid WHERE main.vuid='"
								+ space.get("uid")
								+ "' ORDER BY main.dateline DESC LIMIT " + start + "," + perPage);
			}
			if (count > 0) {
				for (Map<String, Object> value : visitorList) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), (String) value.get("name"), value.get("namestatus") == null ? 0
							: (Integer) value.get("namestatus"));
					value.put("p", Common.urlEncode((String) value.get("resideprovince")));
					value.put("c", Common.urlEncode((String) value.get("residecity")));
					value.put("isfriend", (value.get("uid").equals(space.get("uid")) || (!Common.empty(space
							.get("friends")) && Common.in_array((String[]) space.get("friends"), value
							.get("uid")))) ? true : false);
					fuids.add(value.get("uid"));
					try {
						value.put("note", Common.getStr((String) value.get("note"), 28, false, false, false,
								0, -1, request, response));
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
					list.put(value.get("uid"), value);
				}
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL, null,
					null));
		} else if ("blacklist".equals(view)) {
			String theURL = "space.jsp?uid=" + space.get("uid") + "&do=friend&view=" + view;
			actives.put("me", " class=\"active\"");

			count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("blacklist") + " main WHERE main.uid='" + space.get("uid")
					+ "'");
			if (count > 0) {
				List<Map<String, Object>> blacklist = dataBaseService
						.executeQuery("SELECT s.username, s.name, s.namestatus, s.groupid, main.dateline, main.buid AS uid FROM "
								+ JavaCenterHome.getTableName("blacklist")
								+ " main LEFT JOIN "
								+ JavaCenterHome.getTableName("space")
								+ " s ON s.uid=main.buid WHERE main.uid='"
								+ space.get("uid")
								+ "' ORDER BY main.dateline DESC LIMIT " + start + "," + perPage);
				for (Map<String, Object> value : blacklist) {
					value.put("isfriend", false);
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), (String) value.get("name"), (Integer) value.get("namestatus"));
					fuids.add(value.get("uid"));
					list.put(value.get("uid"), value);
				}
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL, null,
					null));
		} else {
			String theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do");
			actives.put("me", " class=\"active\"");

			Map<String, String[]> paramMap = request.getParameterMap();
			paramMap.put("view", new String[] {"me"});
			view = "me";
			String whereSQL = null;
			Map<Integer, String> groups = null;
			int group = 0;
			if ((Boolean) space.get("self")) {
				groups = Common.getFriendGroup(request);
				request.setAttribute("groups", groups);
				String groupStr = request.getParameter("group");
				group = groupStr == null ? -1 : Common.intval(groupStr);
				if (group > -1) {
					whereSQL = "AND main.gid='" + group + "'";
					theURL += "&group=" + group;
				}
			}
			String searchKey = request.getParameter("searchkey");
			if (!Common.empty(searchKey)) {
				whereSQL = "AND main.fusername='" + searchKey + "'";
				theURL += "&searchkey=" + searchKey;
			}
			if (!Common.empty(space.get("friendnum"))) {
				if (whereSQL != null) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("friend") + " main WHERE main.uid='"
							+ space.get("uid") + "' AND main.status='1' " + whereSQL);
				} else {
					whereSQL = "";
					count = (Integer) space.get("friendnum");
				}
				if (count > 0) {
					List<Map<String, Object>> friendList = dataBaseService
							.executeQuery("SELECT s.*, f.resideprovince, f.residecity, f.note, f.spacenote, f.sex, main.gid, main.num FROM "
									+ JavaCenterHome.getTableName("friend")
									+ " main LEFT JOIN "
									+ JavaCenterHome.getTableName("space")
									+ " s ON s.uid=main.fuid LEFT JOIN "
									+ JavaCenterHome.getTableName("spacefield")
									+ " f ON f.uid=main.fuid WHERE main.uid='"
									+ space.get("uid")
									+ "' AND main.status='1' "
									+ whereSQL
									+ " ORDER BY main.num DESC, main.dateline DESC LIMIT "
									+ start
									+ ","
									+ perPage);
					for (Map<String, Object> value : friendList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), (String) value.get("name"), (Integer) value
										.get("namestatus"));
						value.put("p", Common.urlEncode((String) value.get("resideprovince")));
						value.put("c", Common.urlEncode((String) value.get("residecity")));
						value.put("isfriend", true);
						value.put("group", groups != null ? groups.get(value.get("gid")) : "");
						fuids.add(value.get("uid"));
						try {
							value.put("note", Common.getStr((String) value.get("note"), 28, false, false,
									false, 0, -1, request, response));
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
						list.put(value.get("uid"), value);
					}
				}
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL,
						null, null));
			}
			if ((Boolean) space.get("self")) {
				Map groupSelect = new HashMap();
				groupSelect.put(group, " class=\"current\"");
				request.setAttribute("groupselect", groupSelect);
				int maxFriendNum = (Integer) Common.checkPerm(request, response, sGlobal, "maxfriendnum");
				if (maxFriendNum > 0) {
					maxFriendNum = maxFriendNum
							+ (space.get("addfriend") == null ? 0 : (Integer) space.get("addfriend"));
					request.setAttribute("maxfriendnum", maxFriendNum);
				}
			}
		}
		if (fuids.size() > 0) {
			List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("session") + " WHERE uid IN (" + Common.sImplode(fuids)
					+ ")");
			Map ols = new HashMap();
			String[] source = new String[] {"me", "trace", "blacklist"};
			for (Map<String, Object> value : sessionList) {
				if (Common.empty(value.get("magichidden"))) {
					ols.put(value.get("uid"), value.get("lastactivity"));
				} else if (list.get(value.get("uid")) != null && !Common.in_array(source, view)) {
					list.remove((value.get("uid")));
					count = count - 1;
				}
			}
			request.setAttribute("ols", ols);
		}

		Common.realname_get(sGlobal, sConfig, sNames, space);
		if (Common.empty(view) || "all".equals(view)) {
			Map<String, String[]> paramMap = request.getParameterMap();
			paramMap.put("view", new String[] {"me"});
			view = "me";
		}
		Map<String, String> a_actives = new HashMap<String, String>();
		a_actives.put(view + (type == null ? "" : type), " class=\"current\"");
		request.setAttribute("list", list);
		List<Map<String, Object>> listForSpaceList = null;
		if (Common.empty(sGlobal.get("inajax")) && !(Boolean) space.get("self")) {
			Map<String, Object> TPL = new HashMap<String, Object>();
			TPL.put("spacetitle", "好友");
			TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
					+ "&do=friend&view=me\">TA的好友列表</a>"});
			request.setAttribute("TPL", TPL);
			listForSpaceList = new ArrayList<Map<String, Object>>();
			request.setAttribute("list", listForSpaceList);
		}
		Set keys = list.keySet();
		Map<String, Object> value = null;
		for (Object key : keys) {
			value = list.get(key);
			value.put("gColor", value.get("groupid") == null ? "" : Common.getColor(request, response,
					(Integer) value.get("groupid")));
			value.put("gIcon", value.get("groupid") == null ? "" : Common.getIcon(request, response,
					(Integer) value.get("groupid")));
			if (Common.empty(sGlobal.get("inajax")) && !(Boolean) space.get("self")) {
				listForSpaceList.add(value);
			}
		}
		request.setAttribute("actives", actives);
		request.setAttribute("a_actives", a_actives);
		request.setAttribute("count", count);
		request.setAttribute("navtitle", "好友 - ");
		return include(request, response, sConfig, sGlobal, "space_friend.jsp");
	}

	
	public ActionForward space_index(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (!Common.empty(space.get("namestatus"))) {
			if (!cpService.checkRealName(request, "viewspace")) {
				sGlobal.put("realname_privacy", true);
				setPrivacy(sGlobal, space);
				return include(request, response, sConfig, sGlobal, "space_privacy.jsp");
			}
		}
		sGlobal.put("space_theme", space.get("theme"));
		sGlobal.put("space_css", space.get("css"));
		space.put("isfriend", space.get("self"));
		if (Common.in_array((String[]) space.get("friends"), sGlobal.get("supe_uid"))) {
			space.put("isfriend", true);
		}
		space.put("sex_org", space.get("sex"));
		space
				.put(
						"sex",
						space.get("sex") != null ? (Integer) space.get("sex") == 1 ? "<a href=\"cp.jsp?ac=friend&op=search&sex=1&searchmode=1\">"
								+ Common.getMessage(request, "man") + "</a>"
								: ((Integer) space.get("sex") == 2 ? "<a href=\"cp.jsp?ac=friend&op=search&sex=2&searchmode=1\">"
										+ Common.getMessage(request, "woman") + "</a>"
										: "")
								: "");
		space.put("birth", (!Common.empty(space.get("birthyear")) ? space.get("birthyear")
				+ Common.getMessage(request, "year") : "")
				+ (!Common.empty(space.get("birthmonth")) ? space.get("birthmonth")
						+ Common.getMessage(request, "month") : "")
				+ (!Common.empty(space.get("birthday")) ? space.get("birthday")
						+ Common.getMessage(request, "day") : ""));
		space
				.put(
						"marry",
						"1".equals(space.get("marry") + "") ? "<a href=\"cp.jsp?ac=friend&op=search&marry=1&searchmode=1\">"
								+ Common.getMessage(request, "unmarried") + "</a>"
								: ("2".equals(space.get("marry") + "") ? "<a href=\"cp.jsp?ac=friend&op=search&marry=2&searchmode=1\">"
										+ Common.getMessage(request, "married") + "</a>"
										: ""));
		space
				.put(
						"birthcity",
						Common
								.trim((!Common.empty(space.get("birthprovince")) ? "<a href=\"cp.jsp?ac=friend&op=search&birthprovince="
										+ Common.urlEncode((String) space.get("birthprovince"))
										+ "&searchmode=1\">" + space.get("birthprovince") + "</a>"
										: "")
										+ (!Common.empty(space.get("birthcity")) ? " <a href=\"cp.jsp?ac=friend&op=search&birthcity="
												+ Common.urlEncode((String) space.get("birthcity"))
												+ "&searchmode=1\">" + space.get("birthcity") + "</a>"
												: "")));
		space
				.put(
						"residecity",
						Common
								.trim((!Common.empty(space.get("resideprovince")) ? "<a href=\"cp.jsp?ac=friend&op=search&resideprovince="
										+ Common.urlEncode((String) space.get("resideprovince"))
										+ "&searchmode=1\">" + space.get("resideprovince") + "</a>"
										: "")
										+ (!Common.empty(space.get("residecity")) ? " <a href=\"cp.jsp?ac=friend&op=search&residecity="
												+ Common.urlEncode((String) space.get("residecity"))
												+ "&searchmode=1\">" + space.get("residecity") + "</a>"
												: "")));
		space.put("qq", Common.empty(space.get("qq")) ? ""
				: "<a target=\"_blank\" href=\"http://wpa.qq.com/msgrd?V=1&Uin=" + space.get("qq") + "&Site="
						+ space.get("username") + "&Menu=yes\">" + space.get("qq") + "</a>");
		List<Map<String, Object>> spaceInfoList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("spaceinfo") + " WHERE uid='" + space.get("uid")
				+ "' AND type IN ('base', 'contact')");
		boolean v_friend = false;
		for (Map<String, Object> value : spaceInfoList) {
			v_friend = Common.ckFriend(sGlobal, space, (Integer) value.get("uid"), (Integer) value
					.get("friend"), null);
			if (!v_friend) {
				space.put((String) value.get("subtype"), null);
			}
		}
		Common.getCacheDate(request, response, "/data/cache/usergroup.jsp", "globalGroupTitle");
		space.put("star", Common.getStar(sConfig, space.get("experience") == null ? 0 : (Integer) space
				.get("experience")));
		space.put("domainurl", Common.spaceDomain(request, space, sConfig));
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		if (Common.ckPrivacy(sGlobal, sConfig, space, "feed", 0)) {
			List<Map<String, Object>> feed = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("feed") + " WHERE uid='" + space.get("uid")
					+ "' ORDER BY dateline DESC LIMIT 0,20");
			List<Map<String, Object>> feedList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> value : feed) {
				if (Common.ckFriend(sGlobal, space, (Integer) value.get("uid"),
						(Integer) value.get("friend"), (String) value.get("target_ids"))) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					Common.mkFeed(sNames, sConfig, request, value, null);
					feedList.add(value);
				}
			}
			int feedNum = feedList.size();
			Map TPL = new HashMap();
			TPL.put("hidden_hot", 1);
			request.setAttribute("TPL", TPL);
			request.setAttribute("feedlist", feedList);
		}
		Map olUids = new HashMap();
		if (Common.ckPrivacy(sGlobal, sConfig, space, "friend", 0)) {
			List<Map<String, Object>> friendList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + space.get("uid")
					+ "' AND status='1' ORDER BY num DESC, dateline DESC LIMIT 0,16");
			for (Map<String, Object> value : friendList) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("fuid"), (String) value
						.get("fusername"), "", 0);
				olUids.put(value.get("fuid"), value.get("fuid"));
			}
			if (friendList.size() > 0 && Common.empty(space.get("friendnum"))) {
				cpService.friendCache(request, sGlobal, sConfig, (Integer) space.get("uid"));
			}
			request.setAttribute("friendlist", friendList);
		}
		Map<Object, Map<String, Object>> visitorList = new LinkedHashMap<Object, Map<String, Object>>();
		List<Map<String, Object>> visitors = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("visitor") + " WHERE uid='" + space.get("uid")
				+ "' ORDER BY dateline DESC LIMIT 0,16");
		for (Map<String, Object> value : visitors) {
			if (!Common.empty(value.get("vusername"))) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("vuid"), (String) value
						.get("vusername"), "", 0);
			}
			value.put("isfriend", false);
			if (Common.in_array((String[]) space.get("friends"), value.get("vuid"))) {
				value.put("isfriend", true);
			}
			olUids.put(value.get("vuid"), value.get("vuid"));
			visitorList.put(value.get("vuid"), value);
		}
		Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
		String[] viewUids = !Common.empty(sCookie.get("viewuids")) ? sCookie.get("viewuids").split("_")
				: null;
		if ((Integer) sGlobal.get("supe_uid") > 0 && !(Boolean) space.get("self")
				&& !Common.in_array(viewUids, space.get("uid"))) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET viewnum=viewnum+1 WHERE uid='" + space.get("uid") + "'");
			HashSet<String> viewUidHs = new HashSet<String>();
			if (viewUids != null) {
				CollectionUtils.addAll(viewUidHs, viewUids);
			}
			viewUidHs.add(space.get("uid").toString());
			CookieHelper.setCookie(request, response, "viewuids", Common.implode(viewUidHs, "_"));
		}
		if (space.get("blognum") != null && (Integer) space.get("blognum") > 0
				&& Common.ckPrivacy(sGlobal, sConfig, space, "blog", 0)) {
			List<Map<String, Object>> bbfList = dataBaseService
					.executeQuery("SELECT b.uid, b.blogid, b.subject, b.dateline, b.pic, b.picflag, b.viewnum, b.replynum, b.friend, b.password, bf.message, bf.target_ids FROM "
							+ JavaCenterHome.getTableName("blog")
							+ " b LEFT JOIN "
							+ JavaCenterHome.getTableName("blogfield")
							+ " bf ON bf.blogid=b.blogid WHERE b.uid='"
							+ space.get("uid")
							+ "' ORDER BY b.dateline DESC LIMIT 0,5");
			List<Map<String, Object>> blogList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> value : bbfList) {
				if (Common.ckFriend(sGlobal, space, (Integer) value.get("uid"),
						(Integer) value.get("friend"), (String) value.get("target_ids"))) {
					if (!Common.empty(value.get("pic"))) {
						value.put("pic", Common.pic_cover_get(sConfig, (String) value.get("pic"),
								(Integer) value.get("picflag")));
					}
					try {
						value.put("message", (Integer) value.get("friend") == 4 ? "" : Common.getStr(
								(String) value.get("message"), 150, false, false, false, 0, -1, request,
								response));
					} catch (Exception e) {
						e.printStackTrace();
						return showMessage(request, response, e.getMessage());
					}
					blogList.add(value);
				}
			}
			request.setAttribute("bloglist", blogList);
			int blogNum = blogList.size();
		}
		if (space.get("albumnum") != null && (Integer) space.get("albumnum") > 0
				&& Common.ckPrivacy(sGlobal, sConfig, space, "album", 0)) {
			List<Map<String, Object>> album = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("album") + " WHERE uid='" + space.get("uid")
					+ "' ORDER BY updatetime DESC LIMIT 0,6");
			List<Map<String, Object>> albumList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> value : album) {
				if (Common.ckFriend(sGlobal, space, (Integer) value.get("uid"),
						(Integer) value.get("friend"), (String) value.get("target_ids"))) {
					value.put("pic", Common.pic_cover_get(sConfig, (String) value.get("pic"), (Integer) value
							.get("picflag")));
					albumList.add(value);
				}
			}
			request.setAttribute("albumlist", albumList);
		}
		if (Common.ckPrivacy(sGlobal, sConfig, space, "wall", 0)) {
			List<Map<String, Object>> wallList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("comment") + " WHERE id='" + space.get("uid")
					+ "' AND idtype='uid' ORDER BY dateline DESC LIMIT 0,5");
			for (Map<String, Object> value : wallList) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"), (String) value
						.get("author"), "", 0);
				try {
					value.put("message", Common.strlen((String) value.get("message")) > 500 ? Common
							.getStr((String) value.get("message"), 500, false, false, false, 0, -1, request,
									response)
							+ " ..." : value.get("message"));
				} catch (Exception e) {
					e.printStackTrace();
					return showMessage(request, response, e.getMessage());
				}
			}
			request.setAttribute("wallList", wallList);
		}
		List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("session") + " WHERE uid = '" + space.get("uid") + "'");
		Map<String, Object> sessionOnLine = null;
		if (sessionList.size() > 0) {
			sessionOnLine = sessionList.get(0);
		}
		String OnLine = (sessionOnLine == null || !Common.empty(sessionOnLine.get("magichidden"))) ? null
				: Common.sgmdate(request, "HH:mm:ss", (Integer) sessionOnLine.get("lastactivity"), true);
		request.setAttribute("isonline", OnLine);
		String theme = request.getParameter("theme");
		theme = Common.empty(theme) ? null : theme.replaceAll("(?i)[^(0-9a-z)]", "");
		if ("jchomedefault".equals(theme)) {
			sGlobal.remove("space_theme");
			sGlobal.remove("space_css");
		} else if (theme != null && !"".equals(theme)) {
			File cssFile = new File(JavaCenterHome.jchRoot + "/theme/" + theme + "/style.css");
			if (cssFile.exists()) {
				sGlobal.put("space_theme", theme);
				sGlobal.remove("space_css");
			}
		} else {
			Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
			if (!(Boolean) space.get("self") && member != null && !Common.empty(member.get("nocss"))) {
				sGlobal.remove("space_theme");
				sGlobal.remove("space_css");
			}
		}
		if (!(Boolean) space.get("self") && (Integer) sGlobal.get("supe_uid") > 0) {
			List<Map<String, Object>> visitorByUid = dataBaseService.executeQuery("SELECT dateline FROM "
					+ JavaCenterHome.getTableName("visitor") + " WHERE uid='" + space.get("uid")
					+ "' AND vuid='" + sGlobal.get("supe_uid") + "'");
			Map<String, Object> visitor = null;
			if (visitorByUid.size() > 0) {
				visitor = visitorByUid.get(0);
			}
			boolean is_anonymous = Common.empty(sCookie.get("anonymous_visit_" + sGlobal.get("supe_uid")
					+ "_" + space.get("uid"))) ? false : true;
			if (visitor == null || Common.empty(visitor.get("dateline"))) {
				dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("visitor")
						+ " (`uid`, `vuid`, `vusername`, `dateline`) VALUES ('" + space.get("uid") + "', '"
						+ sGlobal.get("supe_uid") + "', '"
						+ (is_anonymous ? "" : sGlobal.get("supe_username")) + "', '"
						+ sGlobal.get("timestamp") + "') ");
				showCredit(request, sGlobal, sConfig, space);
			} else {
				if ((Integer) sGlobal.get("timestamp") - (Integer) visitor.get("dateline") >= 300) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("visitor")
							+ " SET dateline='" + sGlobal.get("timestamp") + "',vusername='"
							+ (is_anonymous ? "" : sGlobal.get("supe_username")) + "' WHERE uid='"
							+ space.get("uid") + "' AND vuid='" + sGlobal.get("supe_uid") + "'");
				}
				if ((Integer) sGlobal.get("timestamp") - (Integer) visitor.get("dateline") >= 3600) {
					showCredit(request, sGlobal, sConfig, space);
				}
			}
			Common.getReward("visit", true, 0, space.get("uid") + "", true, request, response);
		}
		space.put("magiccredit", 0);
		Map globalMagic = (Map) request.getAttribute("globalMagic");
		if (!Common.empty(globalMagic.get("gift")) && (Integer) sGlobal.get("supe_uid") > 0) {
			List<Map<String, Object>> magicuselogList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid='" + space.get("uid")
					+ "' AND mid='gift' LIMIT 1");
			if (magicuselogList.size() > 0) {
				Map<String, Object> value = magicuselogList.get(0);
				Map data = Common.empty(value.get("data")) ? new HashMap() : Serializer.unserialize(
						(String) value.get("data"), false);
				if (data.get("left") == null || (Integer) data.get("left") <= 0) {
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("magicuselog")
							+ " WHERE uid = '" + space.get("uid") + "' AND mid = 'gift'");
				}
				Map<Integer, Integer> receiver = (Map<Integer, Integer>) data.get("receiver");
				if (Common.empty(data.get("receiver")) || !receiver.containsValue(sGlobal.get("supe_uid"))) {
					space.put("magiccredit", (Integer) data.get("left") >= (Integer) data.get("chunk") ? data
							.get("chunk") : data.get("left"));
				}
			}
		}
		request.setAttribute("mids", new String[] {"viewmagiclog", "viewmagic", "viewvisitor"});
		if (olUids.size() > 0) {
			List<Map<String, Object>> sessions = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("session") + " WHERE uid IN (" + Common.sImplode(olUids)
					+ ")");
			Map ols = new HashMap();
			for (Map<String, Object> value : sessions) {
				if (Common.empty(value.get("magichidden"))) {
					ols.put(value.get("uid"), true);
				} else if (!Common.empty(visitorList.get(value.get("uid")))) {
					visitorList.remove(value.get("uid"));
				}
			}
			request.setAttribute("ols", ols);
		}
		request.setAttribute("visitorlist", visitorList);
		Map<Object, Map<String, Object>> userApp = new LinkedHashMap<Object, Map<String, Object>>();
		if (!Common.empty(sConfig.get("my_status"))) {
			List<Map<String, Object>> userappList = dataBaseService
					.executeQuery("SELECT main.*, field.* FROM " + JavaCenterHome.getTableName("userapp")
							+ " main LEFT JOIN " + JavaCenterHome.getTableName("userappfield")
							+ " field ON field.uid=main.uid AND field.appid=main.appid WHERE main.uid='"
							+ space.get("uid") + "' ORDER BY main.displayorder DESC");
			Map<String, Object> temp = new LinkedHashMap<String, Object>();
			for (Map<String, Object> value : userappList) {
				userApp.put(value.get("appid"), value);
			}
			space.put("userapp", userApp);
		}
		if (userApp.size() > 0) {
			Map<String, Object> value = null;
			List guideList = new ArrayList();
			List narrowList = new ArrayList();
			List wideList = new ArrayList();
			for (Map.Entry<Object, Map<String, Object>> entry : userApp.entrySet()) {
				value = entry.getValue();
				if (!Common.empty(value.get("allowprofilelink")) && !Common.empty(value.get("profilelink"))) {
					guideList.add(value);
				}
				if (Common.ckAppPrivacy(sGlobal, space, (Integer) value.get("privacy"))
						&& !Common.empty(value.get("myml"))) {
					value.put("appurl", "userapp.jsp?id=" + value.get("appid"));
					if (!Common.empty(value.get("narrow"))) {
						narrowList.add(value);
					} else {
						wideList.add(value);
					}
				}
			}
			request.setAttribute("guidelist", guideList);
			request.setAttribute("narrowlist", narrowList);
			request.setAttribute("widelist", wideList);
		}
		Common.realname_get(sGlobal, sConfig, sNames, space);
		if (!(Boolean) space.get("self") && (Integer) sGlobal.get("supe_uid") > 0) {
			cpService.addFriendNum(sGlobal, (Integer) space.get("uid"), (String) space.get("username"));
		}
		request.removeAttribute("globalAd");
		Map<String, String[]> paramMap = request.getParameterMap();
		paramMap.put("view", new String[] {"me"});
		request.setAttribute("tpl_css", "space");
		boolean manageName = Common.checkPerm(request, response, "managename");
		request.setAttribute("manageName", manageName);
		if (!manageName) {
			boolean manageSpaceGroup = Common.checkPerm(request, response, "managespacegroup");
			request.setAttribute("manageSpaceGroup", manageSpaceGroup);
			if (!manageSpaceGroup) {
				boolean manageSpaceInfo = Common.checkPerm(request, response, "managespaceinfo");
				request.setAttribute("manageSpaceInfo", manageSpaceInfo);
				if (!manageSpaceInfo) {
					boolean manageSpaceCredit = Common.checkPerm(request, response, "managespacecredit");
					request.setAttribute("manageSpaceCredit", manageSpaceCredit);
					if (!manageSpaceCredit) {
						boolean manageSpaceNote = Common.checkPerm(request, response, "managespacenote");
						request.setAttribute("manageSpaceNote", manageSpaceNote);
					}
				}
			}
		}
		if (space.get("groupid") != null) {
			request
					.setAttribute("gColor", Common
							.getColor(request, response, (Integer) space.get("groupid")));
			request.setAttribute("gIcon", Common.getIcon(request, response, (Integer) space.get("groupid")));
		}
		return include(request, response, sConfig, sGlobal, "space_index.jsp");
	}

	
	public ActionForward space_info(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (!Common.empty(space.get("namestatus"))) {
			if (!cpService.checkRealName(request, "viewspace")) {
				return showMessage(request, response, "no_privilege_realname");
			}
		}
		space.put("sex_org", space.get("sex"));
		space
				.put(
						"sex",
						space.get("sex") != null ? (Integer) space.get("sex") == 1 ? "<a href=\"cp.jsp?ac=friend&op=search&sex=1&searchmode=1\">"
								+ Common.getMessage(request, "man") + "</a>"
								: ((Integer) space.get("sex") == 2 ? "<a href=\"cp.jsp?ac=friend&op=search&sex=2&searchmode=1\">"
										+ Common.getMessage(request, "woman") + "</a>"
										: "")
								: "");
		space.put("birth", (!Common.empty(space.get("birthyear")) ? space.get("birthyear")
				+ Common.getMessage(request, "year") : "")
				+ (!Common.empty(space.get("birthmonth")) ? space.get("birthmonth")
						+ Common.getMessage(request, "month") : "")
				+ (!Common.empty(space.get("birthday")) ? space.get("birthday")
						+ Common.getMessage(request, "day") : ""));
		space
				.put(
						"marry",
						"1".equals(space.get("marry") + "") ? "<a href=\"cp.jsp?ac=friend&op=search&marry=1&searchmode=1\">"
								+ Common.getMessage(request, "unmarried") + "</a>"
								: ("2".equals(space.get("marry") + "") ? "<a href=\"cp.jsp?ac=friend&op=search&marry=2&searchmode=1\">"
										+ Common.getMessage(request, "married") + "</a>"
										: ""));
		space
				.put(
						"birthcity",
						Common
								.trim((!Common.empty(space.get("birthprovince")) ? "<a href=\"cp.jsp?ac=friend&op=search&birthprovince="
										+ Common.urlEncode((String) space.get("birthprovince"))
										+ "&searchmode=1\">" + space.get("birthprovince") + "</a>"
										: "")
										+ (!Common.empty(space.get("birthcity")) ? " <a href=\"cp.jsp?ac=friend&op=search&birthcity="
												+ Common.urlEncode((String) space.get("birthcity"))
												+ "&searchmode=1\">" + space.get("birthcity") + "</a>"
												: "")));
		space
				.put(
						"residecity",
						Common
								.trim((!Common.empty(space.get("resideprovince")) ? "<a href=\"cp.jsp?ac=friend&op=search&resideprovince="
										+ Common.urlEncode((String) space.get("resideprovince"))
										+ "&searchmode=1\">" + space.get("resideprovince") + "</a>"
										: "")
										+ (!Common.empty(space.get("residecity")) ? " <a href=\"cp.jsp?ac=friend&op=search&residecity="
												+ Common.urlEncode((String) space.get("residecity"))
												+ "&searchmode=1\">" + space.get("residecity") + "</a>"
												: "")));
		space.put("qq", Common.empty(space.get("qq")) ? ""
				: "<a target=\"_blank\" href=\"http://wpa.qq.com/msgrd?V=1&Uin=" + space.get("qq") + "&Site="
						+ space.get("username") + "&Menu=yes\">" + space.get("qq") + "</a>");
		Common.getCacheDate(request, response, "/data/cache/usergroup.jsp", "globalGroupTitle");
		Map fields = Common.getCacheDate(request, response, "/data/cache/cache_profilefield.jsp",
				"globalProfilefield");
		List<Map<String, Object>> spaceInfoList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("spaceinfo") + " WHERE uid='" + space.get("uid") + "'");
		boolean v_friend = false;
		List<Map<String, Object>> tempList = null;
		for (Map<String, Object> value : spaceInfoList) {
			v_friend = Common.ckFriend(sGlobal, space, (Integer) value.get("uid"), (Integer) value
					.get("friend"), "");
			if ("base".equals(value.get("type")) || "contact".equals(value.get("type"))) {
				if (!v_friend) {
					space.put((String) value.get("subtype"), "");
				}
			} else {
				if (v_friend) {
					tempList = (List) space.get((String) value.get("type"));
					if (tempList == null) {
						tempList = new ArrayList<Map<String, Object>>();
					}
					tempList.add(value);
					space.put((String) value.get("type"), tempList);
				}
			}
		}
		space.put("profile_base", false);
		String[] baseInfos = new String[] {"sex", "birthday", "blood", "marry", "residecity", "birthcity"};
		for (String value : baseInfos) {
			if (!Common.empty(space.get(value))) {
				space.put("profile_base", true);
			}
		}

		Set keys = fields.keySet();
		String fieldValue = null;
		Map tempMap = null;
		for (Object fieldId : keys) {
			tempMap = (Map) fields.get(fieldId);
			if (!Common.empty(fieldValue = (String) space.get("field_" + fieldId))
					&& Common.empty(tempMap.get("invisible"))) {
				space.put("profile_base", true);
				tempMap.put("fieldvalue", fieldValue);
				tempMap.put("urlvalue", Common.urlEncode(fieldValue));
			}

		}
		space.put("profile_contact", false);
		String[] contactInfos = new String[] {"mobile", "qq", "msn"};
		for (String value : contactInfos) {
			if (!Common.empty(space.get(value))) {
				space.put("profile_contact", true);
			}
		}
		space.put("star", Common.getStar(sConfig, space.get("experience") == null ? 0 : (Integer) space
				.get("experience")));
		request.setAttribute("tpl_css", "space");
		if (!Common.empty(space.get("info"))) {
			Map<String, String> infos = new HashMap<String, String>();
			infos.put("trainwith", "我想结交");
			infos.put("interest", "兴趣爱好");
			infos.put("book", "喜欢的书籍");
			infos.put("movie", "喜欢的电影");
			infos.put("tv", "喜欢的电视");
			infos.put("music", "喜欢的音乐");
			infos.put("game", "喜欢的游戏");
			infos.put("sport", "喜欢的运动");
			infos.put("idol", "偶像");
			infos.put("motto", "座右铭");
			infos.put("wish", "最近心愿");
			infos.put("intro", "我的简介");
			request.setAttribute("infos", infos);
		}
		request.setAttribute("gIcon", space.get("groupid") == null ? "" : Common.getIcon(request, response,
				(Integer) space.get("groupid")));
		request.setAttribute("fields", fields);
		return include(request, response, sConfig, sGlobal, "space_info.jsp");
	}

	
	public ActionForward space_mood(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int perPage = 20;
		int page = Common.intval(request.getParameter("page"));
		page = page < 1 ? 1 : page;
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		if (!Common.empty(space.get("mood"))) {
			String theURL = "space.jsp?uid=" + space.get("uid") + "&do=mood";
			int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("space") + " s WHERE s.mood='" + space.get("mood") + "'");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService
						.executeQuery("SELECT s.*,sf.note,sf.sex FROM "
								+ JavaCenterHome.getTableName("space") + " s LEFT JOIN "
								+ JavaCenterHome.getTableName("spacefield")
								+ " sf ON sf.uid=s.uid WHERE s.mood='" + space.get("mood")
								+ "' ORDER BY s.updatetime DESC LIMIT " + start + "," + perPage);
				for (Map<String, Object> value : list) {
					value.put("isfriend", (value.get("uid").equals(space.get("uid")) || (!Common.empty(space
							.get("friends")) && Common.in_array((String[]) space.get("friends"), value
							.get("uid")))) ? true : false);
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), (String) value.get("name"), (Integer) value.get("namestatus"));
					value.put("gColor", Common.getColor(request, response, (Integer) value.get("groupid")));
					value.put("gIcon", Common.getIcon(request, response, (Integer) value.get("groupid")));
				}
				request.setAttribute("list", list);
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL, null,
					null));
		}
		request.setAttribute("navtitle", "同心情 - ");
		return include(request, response, sConfig, sGlobal, "space_mood.jsp");
	}

	
	public ActionForward space_mtag(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int page = Common.intval(request.getParameter("page"));
		page = page < 1 ? 1 : page;
		int id = Common.intval(request.getParameter("id"));
		int tagId = Common.intval(request.getParameter("tagid"));
		String tagName = Common.trim(request.getParameter("tagname"));
		int fieldId = Common.intval(request.getParameter("fieldid"));
		Map<String, String[]> paramMap = request.getParameterMap();
		paramMap.put("fieldid", new String[] {fieldId + ""});
		Map<Integer, Map<String, Object>> globalProfield = Common.getCacheDate(request, response,
				"/data/cache/cache_profield.jsp", "globalProfield");
		if (!Common.empty(tagName)) {
			Set<Integer> keys = globalProfield.keySet();
			Map<String, Object> value = null;
			List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
			for (Integer key : keys) {
				value = globalProfield.get(key);
				if ("text".equals(value.get("formtype"))) {
					fields.add(value);
				}
			}
			String plusSQL = null;
			Map<String, Object> field = null;
			if (fieldId > 0) {
				plusSQL = " AND fieldid='" + fieldId + "'";
				field = globalProfield.get(fieldId);
			} else {
				plusSQL = "";
				field = new HashMap<String, Object>();
			}
			List<Map<String, Object>> tagList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("mtag") + " WHERE tagname='" + tagName + "' " + plusSQL);
			if (Common.empty(tagList)) {
				boolean allowmk = false;
				if (field.size() > 0 && !"text".equals(field.get("formtype"))) {
					List<Map<String, Object>> profieldList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("profield") + " WHERE fieldid='" + fieldId + "'");
					if (profieldList.size() > 0) {
						field = profieldList.get(0);
						String[] choice = ((String) field.get("choice")).split("\n");
						field.put("choice", choice);
						String s = Common.stripSlashes(tagName);
						for (String subValue : choice) {
							subValue = Common.trim(subValue);
							if (s.equals(subValue)) {
								tagId = dataBaseService.insert("INSERT INTO "
										+ JavaCenterHome.getTableName("mtag")
										+ " (tagname, fieldid,announcement,pic,moderator) VALUES ('"
										+ Common.addSlashes(s) + "', '" + fieldId + "','','','')");
								return showMessage(request, response, "do_sucess", "space.jsp?do=mtag&tagid="
										+ tagId, 0);
							}
						}
					}
				} else if (fields.size() > 0) {
					allowmk = true;
				}
				if (!allowmk) {
					return showMessage(request, response, "mtag_creat_error");
				}
			} else if (tagList.size() == 1) {
				return showMessage(request, response, "do_sucess", "space.jsp?do=mtag&tagid="
						+ tagList.get(0).get("tagid"), 0);
			}
			request.setAttribute("fields", fields);
			request.setAttribute("tagname", tagName);
			request.setAttribute("taglist", tagList);
			request.setAttribute("tpl_css", "thread");
			return include(request, response, sConfig, sGlobal, "space_mtag_tagname.jsp");
		} else if (id != 0) {
			int perPage = 20;
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("mtag") + " WHERE fieldid='" + id + "'");
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("mtag") + " WHERE fieldid='" + id
						+ "' ORDER BY membernum DESC LIMIT " + start + "," + perPage);
				for (Map<String, Object> value : list) {
					if (Common.empty(value.get("pic"))) {
						value.put("pic", "image/nologo.jpg");
					}
				}
				request.setAttribute("list", list);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
					"space.jsp?uid=" + space.get("uid") + "&do=mtag&id=" + id, null, null));
			Map sub_actives = new HashMap();
			sub_actives.put(id, " class=\"active\"");
			Map fieldids = new HashMap();
			fieldids.put(id, " selected");
			request.setAttribute("sub_actives", sub_actives);
			request.setAttribute("navtitle", (globalProfield.get(id) == null ? "" : (String) globalProfield
					.get(id).get("title")
					+ " - ")
					+ "群组 - ");
			request.setAttribute("tpl_css", "thread");
			return include(request, response, sConfig, sGlobal, "space_mtag_field.jsp");
		} else if (tagId > 0) {
			String view = request.getParameter("view");
			Map<String, String> actives = new HashMap<String, String>();
			actives.put(view, " class=\"active\"");
			request.setAttribute("actives", actives);
			Map mtag = null;
			try {
				mtag = Common.getMtag(request, response, (Integer) sGlobal.get("supe_uid"), tagId);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			if (!Common.empty(mtag.get("close"))) {
				return showMessage(request, response, "mtag_close");
			}
			int eventNum = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("event") + " WHERE tagid='" + tagId + "'");
			if ("list".equals(view) || "digest".equals(view)) {
				int perPage = 30;

				int start = (page - 1) * perPage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String result = Common.ckStart(start, perPage, maxPage);
				if (result != null) {
					return showMessage(request, response, result);
				}
				String theURL = "space.jsp?uid=" + space.get("uid") + "&do=mtag&tagid=" + tagId + "&view="
						+ view;
				String whereSQL = "list".equals(view) ? "" : " AND main.digest='1'";
				String searchKey = Common.stripSearchKey(request.getParameter("searchkey"));
				if (!Common.empty(searchKey)) {
					whereSQL += "AND main.subject LIKE '%" + searchKey + "%' ";
					theURL += "&searchkey=" + searchKey;
					request.setAttribute("searchkey", searchKey);
				}
				int count = 0;
				if (!Common.empty(mtag.get("allowview"))) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("thread") + " main WHERE main.tagid='" + tagId
							+ "' " + whereSQL);
					Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
					if (count > 0) {
						List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT main.* FROM "
								+ JavaCenterHome.getTableName("thread") + " main WHERE main.tagid='" + tagId
								+ "' " + whereSQL
								+ " ORDER BY main.displayorder DESC, main.lastpost DESC LIMIT " + start + ","
								+ perPage);
						for (Map<String, Object> value : list) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), "", 0);
							Common.realname_set(sGlobal, sConfig, sNames,
									(Integer) value.get("lastauthorid"), (String) value.get("lastauthor"),
									"", 0);
							if (!Common.empty(value.get("magicegg"))) {
								StringBuffer magiceggImage = new StringBuffer();
								for (int i = 0; i < (Integer) value.get("magicegg"); i++) {
									magiceggImage.append("<img src=\"image/magic/egg/" + Common.rand(1, 6)
											+ ".gif\" />");
								}
								value.put("magiceggImage", magiceggImage.toString());
							}
						}
						request.setAttribute("list", list);
					}
					request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
							theURL, null, null));
					Common.realname_get(sGlobal, sConfig, sNames, space);
				}
				request.setAttribute("tpl_css", "thread");
				String title = (Common.empty(mtag.get("tagname")) ? "" : mtag.get("tagname") + " - ")
						+ (Common.empty(mtag.get("title")) ? "" : mtag.get("title") + " - ");
				if ("list".equals(view)) {
					request.setAttribute("navtitle", title + "讨论区 - ");
				} else if ("digest".equals(view)) {
					request.setAttribute("navtitle", title + "精华区 - ");
				}
				request.setAttribute("mtag", mtag);
				request.setAttribute("eventnum", eventNum);
				return include(request, response, sConfig, sGlobal, "space_mtag_list.jsp");
			} else if ("member".equals(view)) {
				int perPage = 50;
				int start = (page - 1) * perPage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String result = Common.ckStart(start, perPage, maxPage);
				if (result != null) {
					return showMessage(request, response, result);
				}
				String whereSQL = "";
				String key = Common.stripSearchKey(request.getParameter("key"));
				if (!Common.empty(key)) {
					whereSQL = " AND main.username LIKE '%" + key + "%' ";
				}
				int count = 0;
				if (!Common.empty(mtag.get("allowview"))) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("tagspace") + " main WHERE main.tagid='" + tagId
							+ "' " + whereSQL);
					Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
					List fuids = null;
					if (count > 0) {
						List<Map<String, Object>> list = dataBaseService
								.executeQuery("SELECT field.*, main.username, main.grade FROM "
										+ JavaCenterHome.getTableName("tagspace") + " main LEFT JOIN "
										+ JavaCenterHome.getTableName("spacefield")
										+ " field ON field.uid=main.uid WHERE main.tagid='" + tagId + "' "
										+ whereSQL + " ORDER BY main.grade DESC LIMIT " + start + ","
										+ perPage);
						fuids = new ArrayList(list.size());
						for (Map<String, Object> value : list) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), "", 0);
							value.put("p", Common.urlEncode((String) value.get("resideprovince")));
							value.put("c", Common.urlEncode((String) value.get("residecity")));
							fuids.add(value.get("uid"));
						}
						request.setAttribute("list", list);
					}
					if (fuids != null && fuids.size() > 0) {
						List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("session") + " WHERE uid IN ("
								+ Common.sImplode(fuids) + ")");
						Map ols = new HashMap();
						for (Map<String, Object> value : sessionList) {
							if (Common.empty(value.get("magichidden"))) {
								ols.put(value.get("uid"), value.get("lastactivity"));
							}
						}
						request.setAttribute("ols", ols);
					}
					request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
							"space.jsp?uid=" + space.get("uid") + "&do=mtag&tagid=" + tagId + "&view=member",
							null, null));
					Common.realname_get(sGlobal, sConfig, sNames, space);
				}
				request.setAttribute("tagid", tagId);
				request.setAttribute("eventnum", eventNum);
				request.setAttribute("mtag", mtag);
				request.setAttribute("navtitle", (Common.empty(mtag.get("tagname")) ? "" : mtag
						.get("tagname")
						+ " - ")
						+ (Common.empty(mtag.get("title")) ? "" : mtag.get("title") + " - ") + "成员 - ");
				request.setAttribute("tpl_css", "thread");
				return include(request, response, sConfig, sGlobal, "space_mtag_member.jsp");
			} else if ("event".equals(view)) {
				int perPage = 10;
				int start = (page - 1) * perPage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String result = Common.ckStart(start, perPage, maxPage);
				if (result != null) {
					return showMessage(request, response, result);
				}
				if (eventNum > 0) {
					Map<Integer, Map<String, Object>> globalEventClass = Common.getCacheDate(request,
							response, "/data/cache/cache_eventclass.jsp", "globalEventClass");
					List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("event") + " WHERE tagid='" + tagId
							+ "' ORDER BY eventid DESC LIMIT " + start + ", " + perPage);
					for (Map<String, Object> value : eventList) {
						if (!Common.empty(value.get("poster"))) {
							value.put("pic", Common.pic_get(sConfig, (String) value.get("poster"),
									(Integer) value.get("thumb"), (Integer) value.get("remote"), true));
						} else {
							value
									.put(
											"pic",
											globalEventClass.get(value.get("classid")) == null ? "image/event/default.jpg"
													: globalEventClass.get(value.get("classid"))
															.get("poster"));
						}
					}
					request.setAttribute("eventlist", eventList);
				}
				request.setAttribute("multi", Common.multi(request, eventNum, perPage, page, maxPage,
						"space.jsp?uid=" + space.get("uid") + "&do=mtag&tagid=" + tagId + "&view=event",
						null, null));
				request.setAttribute("navtitle", (Common.empty(mtag.get("tagname")) ? "" : mtag
						.get("tagname")
						+ " - ")
						+ (Common.empty(mtag.get("title")) ? "" : mtag.get("title") + " - ") + "活动 - ");
				request.setAttribute("mtag", mtag);
				if (!Common.empty(mtag.get("allowpost"))) {
					request.setAttribute("tagid", tagId);
				}
				request.setAttribute("eventnum", eventNum);
				request.setAttribute("tpl_css", "thread");
				request.setAttribute("view", view);
				return include(request, response, sConfig, sGlobal, "space_mtag_event.jsp");
			} else {
				Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
				if (!Common.empty(mtag.get("allowview"))) {
					List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT main.* FROM "
							+ JavaCenterHome.getTableName("thread") + " main WHERE main.tagid='" + tagId
							+ "' ORDER BY main.displayorder DESC, main.lastpost DESC LIMIT 0,50");
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("lastauthorid"),
								(String) value.get("lastauthor"), "", 0);
						if (!Common.empty(value.get("magicegg"))) {
							StringBuffer magiceggImage = new StringBuffer();
							for (int i = 0; i < (Integer) value.get("magicegg"); i++) {
								magiceggImage.append("<img src=\"image/magic/egg/" + Common.rand(1, 6)
										+ ".gif\" />");
							}
							value.put("magiceggImage", magiceggImage.toString());
						}
					}
					request.setAttribute("list", list);
					List<Map<String, Object>> starList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='" + tagId
							+ "' AND grade='1'");
					for (Map<String, Object> value : starList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					starList = (List) Common.sarrayRand(starList, 12);
					request.setAttribute("starlist", starList);
					List<Map<String, Object>> memberList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='" + tagId
							+ "' AND grade='0' LIMIT 0,12");
					for (Map<String, Object> value : memberList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					request.setAttribute("memberlist", memberList);
				}
				List<Map<String, Object>> modList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='" + tagId
						+ "' AND grade>'7' ORDER BY grade DESC LIMIT 0,12");
				for (Map<String, Object> value : modList) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
				}
				request.setAttribute("modlist", modList);
				if ((Integer) mtag.get("grade") >= 8) {
					List<Map<String, Object>> checkList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='" + tagId
							+ "' AND grade='-2' LIMIT 0,12");
					for (Map<String, Object> value : checkList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					request.setAttribute("checklist", checkList);
				}

				Common.realname_get(sGlobal, sConfig, sNames, space);
				request.setAttribute("eventnum", eventNum);
				request.setAttribute("managemtag", Common.checkPerm(request, response, "managemtag"));
				request.setAttribute("mtag", mtag);
				request.setAttribute("navtitle", (Common.empty(mtag.get("tagname")) ? "" : mtag
						.get("tagname")
						+ " - ")
						+ (Common.empty(mtag.get("title")) ? "" : mtag.get("title") + " - ") + "首页 - ");
				request.setAttribute("tpl_css", "thread");
				return include(request, response, sConfig, sGlobal, "space_mtag_index.jsp");
			}
		} else {
			String theURL = "space.jsp?uid=" + space.get("uid") + "&do=mtag";
			String view = request.getParameter("view");
			if (Common.empty(view)) {
				view = "me";
				paramMap.put("view", new String[] {"me"});
			}
			if (!Common.in_array(new String[] {"me", "hot", "recommend", "manage"}, view)) {
				view = "hot";
				paramMap.put("view", new String[] {"hot"});
			}
			theURL += "&view=" + view;
			Map<String, String> actives = new HashMap<String, String>();
			actives.put(view, " class=\"active\"");
			request.setAttribute("actives", actives);
			String orderby = request.getParameter("orderby");
			if (!Common.in_array(new String[] {"threadnum", "postnum", "membernum"}, orderby)) {
				orderby = "threadnum";
				paramMap.put("orderby", new String[] {"threadnum"});
			} else {
				theURL += "&orderby=" + orderby;
			}
			Map<String, String> orderbys = new HashMap<String, String>();
			orderbys.put(orderby, " class=\"active\"");
			request.setAttribute("orderbys", orderbys);
			List<String> wheres = new ArrayList<String>();
			Map pro_actives = new HashMap();
			if (fieldId > 0) {
				wheres.add("mt.fieldid='" + fieldId + "'");
				theURL += "&fieldid=" + fieldId;
				pro_actives.put(fieldId, " class=\"current\"");
			} else {
				pro_actives.put("all", " class=\"current\"");
			}
			request.setAttribute("pro_actives", pro_actives);
			int perPage = 20;
			int start = (page - 1) * perPage;

			String SQL = null;
			String countSQL = null;
			if ("me".equals(view) || "manage".equals(view)) {
				String sqlPlus = "manage".equals(view) ? " AND main.grade='9'" : "";
				if (fieldId > 0) {
					countSQL = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("tagspace") + " main, "
							+ JavaCenterHome.getTableName("mtag") + " mt WHERE main.uid='" + space.get("uid")
							+ "' " + sqlPlus + " AND mt.tagid=main.tagid AND "
							+ Common.implode(wheres, " AND ");
					SQL = "SELECT main.*,mt.* FROM " + JavaCenterHome.getTableName("tagspace") + " main, "
							+ JavaCenterHome.getTableName("mtag") + " mt WHERE main.uid='" + space.get("uid")
							+ "' " + sqlPlus + " AND mt.tagid=main.tagid AND "
							+ Common.implode(wheres, " AND ") + " ORDER BY mt." + orderby + " DESC LIMIT "
							+ start + "," + perPage;
				} else {
					countSQL = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("tagspace")
							+ " main WHERE main.uid='" + space.get("uid") + "' " + sqlPlus;
					SQL = "SELECT main.*,mt.* FROM " + JavaCenterHome.getTableName("tagspace")
							+ " main LEFT JOIN " + JavaCenterHome.getTableName("mtag")
							+ " mt ON mt.tagid=main.tagid WHERE main.uid='" + space.get("uid") + "' "
							+ sqlPlus + " ORDER BY mt." + orderby + " DESC LIMIT " + start + "," + perPage;
				}
			} else {
				if ("recommend".equals(view)) {
					wheres.add("mt.recommend='1'");
				}
				String searchKey = Common.stripSearchKey(request.getParameter("searchkey"));
				if (!Common.empty(searchKey)) {
					wheres.add("mt.tagname LIKE '%" + searchKey + "%'");
					theURL += "&searchkey=" + searchKey;
					request.setAttribute("searchkey", searchKey);
				}
				String whereSQL = Common.empty(wheres) ? "1" : Common.implode(wheres, " AND ");
				countSQL = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("mtag") + " mt WHERE "
						+ whereSQL;
				SQL = "SELECT mt.* FROM " + JavaCenterHome.getTableName("mtag") + " mt WHERE " + whereSQL
						+ " ORDER BY mt." + orderby + " DESC LIMIT " + start + "," + perPage;
			}

			int count = dataBaseService.findRows(countSQL);
			List tagIds = null;
			Map tagNames = null;
			if (count > 0) {
				tagNames = new HashMap();
				List<Map<String, Object>> list = dataBaseService.executeQuery(SQL);
				tagIds = new ArrayList(list.size());
				for (Map<String, Object> value : list) {
					Map<String, Object> profield = globalProfield.get(value.get("fieldid"));
					value.put("title", profield == null ? "" : profield.get("title").toString());
					if (Common.empty(value.get("pic"))) {
						value.put("pic", "image/nologo.jpg");
					}
					tagIds.add(value.get("tagid"));
					tagNames.put(value.get("tagid"), value.get("tagname"));
				}
				request.setAttribute("list", list);
				int maxPage = (Integer) sConfig.get("maxpage");
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL,
						null, null));
			}

			if (tagIds != null && tagIds.size() > 0) {
				List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("thread") + " WHERE tagid IN ("
						+ Common.sImplode(tagIds) + ") ORDER BY dateline DESC LIMIT 0,10");
				Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
				for (Map<String, Object> value : threadList) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("lastauthorid"),
							(String) value.get("lastauthor"), "", 0);
					try {
						value.put("tagname", Common.getStr((String) tagNames.get(value.get("tagid")), 20,
								false, false, false, 0, 0, request, response));
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
				}
				request.setAttribute("threadlist", threadList);
			}
			request.setAttribute("navtitle", "群组 - ");
			request.setAttribute("tpl_css", "thread");
			return include(request, response, sConfig, sGlobal, "space_mtag.jsp");
		}
	}

	
	public ActionForward space_notice(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int perPage = 30;
		perPage = Common.mobPerpage(request, perPage);
		int page = Common.intval(request.getParameter("page"));
		page = page <= 1 ? 1 : page;
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		int count = 0;
		String view = request.getParameter("view");
		view = !Common.empty(view) && "userapp".equals(view) ? view : "notice";
		Map<String, String> actives = new HashMap<String, String>();
		actives.put(view, " class=\"active\"");
		if ("userapp".equals(view)) {
			if ("del".equals(request.getParameter("op"))) {
				int appId = Common.intval(request.getParameter("appid"));
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("myinvite")
						+ " WHERE appid='" + appId + "' AND touid='" + sGlobal.get("supe_uid") + "'");
				return showMessage(request, response, "do_success", "space.jsp?do=notice&view=userapp", 0);
			}
			start = Common.intval(request.getParameter("start"));
			int filtrate = start;
			int type = Common.intval(request.getParameter("type"));
			List<Map<String, Object>> myInviteList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("myinvite") + " WHERE touid='" + sGlobal.get("supe_uid")
					+ "' ORDER BY dateline DESC");
			String key = null;
			Map<String, List<Map<String, Object>>> apps = new LinkedHashMap<String, List<Map<String, Object>>>();
			Map<String, List<Map<String, Object>>> list = new LinkedHashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> appTempList = null;
			List<Map<String, Object>> tempList = null;
			for (Map<String, Object> value : myInviteList) {
				key = Common.md5(value.get("typename") + "" + value.get("type"));
				appTempList = apps.get(key);
				if (appTempList == null) {
					appTempList = new ArrayList<Map<String, Object>>();
				}
				appTempList.add(value);
				apps.put(key, appTempList);
				if (filtrate > 0) {
					filtrate--;
				} else {
					if (count < perPage) {
						if ((type > 0 && (Integer) value.get("appid") == type) || type == 0) {
							tempList = list.get(key);
							if (tempList == null) {
								tempList = new ArrayList<Map<String, Object>>();
							}
							tempList.add(value);
							list.put(key, tempList);
							count++;
						}
					}
				}
			}
			int myInviteNum = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("myinvite") + " WHERE touid='" + space.get("uid")
					+ "' LIMIT 1 ");
			if (myInviteNum != (space.get("myinvitenum") == null ? 0 : (Integer) space.get("myinvitenum"))) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET myinvitenum='" + myInviteNum + "' WHERE uid='" + space.get("uid") + "'");
			}
			try {
				request.setAttribute("multi", Common.smulti(sGlobal, start, perPage, count, "space.jsp?do="
						+ request.getAttribute("do") + "&view=userapp", null));
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("apparr", apps);
			request.setAttribute("list", list);
		} else {
			if (!Common.empty(request.getParameter("ignore"))) {
				dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("notification")
						+ " SET new=0 WHERE new='1' AND uid='" + sGlobal.get("supe_uid") + "'");
				dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET notenum='0' WHERE uid='" + sGlobal.get("supe_uid") + "'");
				space.put("notenum", 0);
			}
			Map<String, String> noticeTypes = new LinkedHashMap<String, String>();
			noticeTypes.put("wall", Common.getMessage(request, "wall"));
			noticeTypes.put("piccomment", Common.getMessage(request, "pic_comment"));
			noticeTypes.put("blogcomment", Common.getMessage(request, "blog_comment"));
			noticeTypes.put("clickblog", Common.getMessage(request, "clickblog"));
			noticeTypes.put("clickpic", Common.getMessage(request, "clickpic"));
			noticeTypes.put("clickthread", Common.getMessage(request, "clickthread"));
			noticeTypes.put("sharecomment", Common.getMessage(request, "share_comment"));
			noticeTypes.put("sharenotice", Common.getMessage(request, "share_notice"));
			noticeTypes.put("doing", Common.getMessage(request, "doing_comment"));
			noticeTypes.put("friend", Common.getMessage(request, "friend_notice"));
			noticeTypes.put("post", Common.getMessage(request, "thread_comment"));
			noticeTypes.put("credit", Common.getMessage(request, "credit"));
			noticeTypes.put("mtag", Common.getMessage(request, "mtag"));
			noticeTypes.put("event", Common.getMessage(request, "event"));
			noticeTypes.put("eventcomment", Common.getMessage(request, "event_comment"));
			noticeTypes.put("eventmember", Common.getMessage(request, "event_member"));
			noticeTypes.put("eventmemberstatus", Common.getMessage(request, "event_memberstatus"));
			noticeTypes.put("poll", Common.getMessage(request, "poll"));
			noticeTypes.put("pollcomment", Common.getMessage(request, "poll_comment"));
			noticeTypes.put("pollinvite", Common.getMessage(request, "poll_invite"));

			String type = Common.trim(request.getParameter("type"));
			String typeSQL = Common.empty(type) ? "" : "AND type='" + type + "'";

			count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("notification") + " WHERE uid='" + sGlobal.get("supe_uid")
					+ "' " + typeSQL);
			List newIds = new ArrayList();
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("notification") + " WHERE uid='"
						+ sGlobal.get("supe_uid") + "' " + typeSQL + " ORDER BY dateline DESC LIMIT " + start
						+ "," + perPage);
				for (Map<String, Object> value : list) {
					if (!Common.empty(value.get("authorid"))) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
								(String) value.get("author"), "", 0);
						if (value.get("authorid") != space.get("uid") && !Common.empty(space.get("friends"))
								&& !Common.in_array((String[]) space.get("friends"), value.get("authorid"))) {
							value.put("isfriend", false);
						} else {
							value.put("isfriend", true);
						}
					}
					if (!Common.empty(value.get("new"))) {
						newIds.add(value.get("id"));
						value.put("style", "color:#000;font-weight:bold;");
					} else {
						value.put("style", null);
					}
				}
				request.setAttribute("multi", Common.multi(request, count, perPage, count, maxPage,
						"space.jsp?do=" + request.getAttribute("do"), null, null));
				request.setAttribute("list", list);
			}
			if (newIds.size() > 0) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("notification")
						+ " SET new='0' WHERE id IN (" + Common.sImplode(newIds) + ")");
				int newCount = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("notification") + " WHERE uid='"
						+ sGlobal.get("supe_uid") + "' AND new='1'");
				space.put("notenum", newCount);
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET notenum='" + newCount + "' WHERE uid='" + sGlobal.get("supe_uid") + "'");
			}
			int newNum = 0;
			Map member = null;
			Integer newpm = (member = (Map) sGlobal.get("member")) == null ? 0 : (Integer) member
					.get("newpm");
			space.put("pmnum", newpm);
			String[] nums = new String[] {"notenum", "pokenum", "addfriendnum", "mtaginvitenum",
					"eventinvitenum", "myinvitenum"};
			for (String value : nums) {
				newNum = newNum + (space.get(value) == null ? 0 : (Integer) space.get(value));
			}
			member = member == null ? new HashMap() : member;
			member.put("notenum", space.get("notenum"));
			member.put("allnotenum", newNum);
			sGlobal.put("member", member);
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("newnum", newNum);
			request.setAttribute("noticetypes", noticeTypes);
		}
		request.setAttribute("actives", actives);
		request.setAttribute("view", view);
		request.setAttribute("navtitle", "通知 - ");
		return include(request, response, sConfig, sGlobal, "space_notice.jsp");
	}

	
	public ActionForward space_pm(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		List<Map<String, Object>> list = null;
		int pmid = Common.intval(request.getParameter("pmid"));
		int toUid = Common.intval(request.getParameter("touid"));
		int dateRange = Common.intval(request.getParameter("daterange"));
		if (dateRange == 0) {
			dateRange = 1;
		}
		String subop = request.getParameter("subop");
		Map actives = new HashMap();
		if ("view".equals(subop)) {
			if (toUid > 0) {
				int timeStamp = (Integer) sGlobal.get("timestamp");
				int today = (int) (timeStamp - (timeStamp + Float.valueOf(Common.getTimeOffset(sGlobal,
						sConfig))) % 86400);
				int startTime = 0;
				if (dateRange == 1) {
					startTime = today;
				} else if (dateRange == 2) {
					startTime = today - 86400;
				} else if (dateRange == 3) {
					startTime = today - 172800;
				} else if (dateRange == 4) {
					startTime = today - 604800;
				} else if (dateRange == 5) {
					startTime = 0;
				}
				int endTime = timeStamp;
				list = pmService.getPmByToUid((Integer) sGlobal.get("supe_uid"), toUid, startTime, endTime);
				boolean status = false;
				JcHomeCode jcHomeCode = new JcHomeCode();
				for (Map<String, Object> pm : list) {
					pm.put("message", jcHomeCode.complie((String) pm.get("message")));
					if (!status) {
						status = (!Common.empty(pm.get("msgtoid")) && !Common.empty(pm.get("new")));
					}
				}
				if (status) {
					pmService.setPmStatus((Integer) sGlobal.get("supe_uid"), toUid, pmid, 0);
				}
				pmid = list.size() == 0 ? 0 : (Integer) list.get(0).get("pmid");
				request.setAttribute("pmid", pmid);
				request.setAttribute("daterange", dateRange);
			} else if (pmid > 0) {
				list = pmService.getPmByPmid((Integer) sGlobal.get("supe_uid"), pmid);
				boolean status = false;
				JcHomeCode jcHomeCode = new JcHomeCode();
				for (Map<String, Object> pm : list) {
					pm.put("message", jcHomeCode.complie((String) pm.get("message")));
					if (!status) {
						status = (!Common.empty(pm.get("msgtoid")) && !Common.empty(pm.get("new")));
					}
				}
				if (status) {
					pmService.setPmStatus((Integer) sGlobal.get("supe_uid"), toUid, pmid, 0);
				}
			}
			actives.put(dateRange + "", " class=\"active\"");
		} else if ("ignore".equals(subop)) {
			Map ignorelist = pmService.getBlackls((Integer) sGlobal.get("supe_uid"), null);
			actives.put("ignore", " class=\"active\"");
			request.setAttribute("ignorelist", ignorelist);
		} else {
			String filter = request.getParameter("filter");
			filter = Common.in_array(new String[] {"newpm", "privatepm", "systempm", "announcepm"}, filter) ? filter
					: (!Common.empty(space.get("newpm")) ? "newpm" : "privatepm");
			int perPage = 10;
			perPage = Common.mobPerpage(request, perPage);
			int page = Common.intval(request.getParameter("page"));
			if (page < 1)
				page = 1;


			String folder = "inbox";
			int count = pmService.getNum((Integer) sGlobal.get("supe_uid"), folder, filter);
			int start = pmService.getPageStart(page, perPage, count);
			int msgLen = 100;
			list = pmService.getPmList((Integer) sGlobal.get("supe_uid"), count, folder, filter, start,
					perPage);
			for (Map<String, Object> pm : list) {
				pm.put("message", Common.htmlSpecialChars(pmService.removeCode((String) pm.get("message"),
						msgLen)));
				pm.remove("folder");
			}
			int maxPage = (Integer) sConfig.get("maxpage");
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
					"space.jsp?do=pm&filter=" + filter, null, null));
			Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
			if (member != null) {
				if (!Common.empty(member.get("newpm"))) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET newpm='0' WHERE uid='" + sGlobal.get("supe_uid") + "'");
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("newpm")
							+ " WHERE uid='" + sGlobal.get("supe_uid") + "'");
				}
			}
			actives.put(filter, " class=\"active\"");
			request.setAttribute("count", count);
		}
		if (list != null && list.size() > 0) {
			int today = (int) ((Integer) sGlobal.get("timestamp") - ((Integer) sGlobal.get("timestamp") + Float
					.valueOf(Common.getTimeOffset(sGlobal, sConfig)) * 3600) % 86400);
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			for (Map<String, Object> value : list) {
				if (value.get("msgfromid") != null) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("msgfromid"),
							(String) value.get("msgfrom"), "", 0);
				}
				value.put("daterange", 5);
				if ((Integer) value.get("dateline") >= today) {
					value.put("daterange", 1);
				} else if ((Integer) value.get("dateline") >= today - 86400) {
					value.put("daterange", 2);
				} else if ((Integer) value.get("dateline") >= today - 172800) {
					value.put("daterange", 3);
				}
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
		}
		request.setAttribute("navtitle", "短消息 - ");
		request.setAttribute("touid", toUid);
		request.setAttribute("actives", actives);
		request.setAttribute("list", list);
		return include(request, response, sConfig, sGlobal, "space_pm.jsp");
	}

	
	public ActionForward space_poll(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int pid = Common.intval(request.getParameter("pid"));
		int page = Common.intval(request.getParameter("page"));
		page = page <= 1 ? 1 : page;
		if (pid > 0) {
			int perPage = 20;
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			Map<String, Object> poll = null;
			List<Map<String, Object>> pollList = dataBaseService.executeQuery("SELECT pf.*, p.* FROM "
					+ JavaCenterHome.getTableName("poll") + " p LEFT JOIN "
					+ JavaCenterHome.getTableName("pollfield") + " pf ON pf.pid=p.pid WHERE p.pid='" + pid
					+ "'");
			if (pollList.size() > 0) {
				poll = pollList.get(0);
			}
			if (poll == null) {
				return showMessage(request, response, "view_to_info_did_not_exist");
			}
			if (!Common.empty(poll.get("credit")) && !Common.empty(poll.get("percredit"))
					&& (Integer) poll.get("credit") < (Integer) poll.get("percredit")) {
				poll.put("percredit", poll.get("credit"));
			}
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			Common.realname_set(sGlobal, sConfig, sNames, (Integer) poll.get("uid"), (String) poll
					.get("username"), "", 0);
			boolean allowedVote = true;
			if (!Common.empty(poll.get("sex")) && poll.get("sex") != ((Map) sGlobal.get("member")).get("sex")) {
				allowedVote = false;
			}
			boolean expiration = false;
			if (!Common.empty(poll.get("expiration"))
					&& (Integer) poll.get("expiration") < (Integer) sGlobal.get("timestamp")) {
				allowedVote = false;
				expiration = true;
				if (Common.empty(poll.get("summary")) && Common.empty(poll.get("notify"))) {
					String note = Common.getMessage(request, "cp_note_poll_finish", new String[] {
							"space.jsp?uid=" + poll.get("uid") + "&do=poll&pid=" + poll.get("pid"),
							(String) poll.get("subject")});
					int supe_uid = (Integer) sGlobal.get("supe_uid");
					String supe_username = (String) sGlobal.get("supe_username");
					sGlobal.put("supe_uid", 0);
					sGlobal.put("supe_username", "");
					cpService.addNotification(request, sGlobal, sConfig, (Integer) poll.get("uid"), "poll",
							note, false);
					sGlobal.put("supe_uid", supe_uid);
					sGlobal.put("supe_username", supe_username);
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("pollfield")
							+ "  SET notify='1' WHERE pid='" + poll.get("pid") + "'");
				}
			}

			request.setAttribute("allowedvote", allowedVote);
			request.setAttribute("hasvoted", dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("polluser") + "  WHERE uid='" + sGlobal.get("supe_uid")
					+ "' AND pid='" + pid + "' "));
			float allVote = 0;
			List<Map<String, Object>> option = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("polloption") + " WHERE pid='" + pid + "' ORDER BY oid");
			for (Map<String, Object> value : option) {
				allVote += (Integer) value.get("votenum");
			}
			request.setAttribute("option", option);
			NumberFormat formater = DecimalFormat.getInstance();
			formater.setMaximumFractionDigits(2);
			String percent = null;
			for (Map<String, Object> value : option) {
				if (!Common.empty(value.get("votenum")) && allVote > 0) {
					percent = formater.format((Integer) value.get("votenum") / allVote);
					value.put("width", Math.round(Float.valueOf(percent) * 160));
					value.put("percent", Math.round(Float.valueOf(percent) * 100));
				} else {
					value.put("width", 0);
					value.put("percent", 0);
				}
			}
			boolean isFriend = true;
			if (!Common.empty(poll.get("noreply"))) {
				isFriend = (Boolean) space.get("self");
				if (!Common.empty(space.get("friends"))
						&& Common.in_array((String[]) space.get("friends"), sGlobal.get("supe_uid"))) {
					isFriend = true;
				}
			}
			if (isFriend) {
				int count = (Integer) poll.get("replynum");
				if (count > 0) {
					int cid = Common.intval(request.getParameter("cid"));
					request.setAttribute("cid", cid);
					String csql = cid > 0 ? "cid='" + cid + "' AND" : "";
					List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("comment") + " WHERE " + csql + " id='" + pid
							+ "' AND idtype='pid' ORDER BY dateline LIMIT " + start + "," + perPage);
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
								(String) value.get("author"), "", 0);
					}
					request.setAttribute("list", list);
				}
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
						"space.jsp?uid=" + poll.get("uid") + "&do=" + request.getAttribute("do")
								+ "&pid=$pid", null, "div_main_content"));
			}
			List<Map<String, Object>> newPoll = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("poll") + " ORDER BY dateline DESC LIMIT 0, 10");
			for (Map<String, Object> value : newPoll) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), "", 0);
			}
			request.setAttribute("newpoll", newPoll);
			int timeRange = (Integer) sGlobal.get("timestamp") - 2592000;
			List<Map<String, Object>> hotPoll = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("poll") + " WHERE lastvote >= '" + timeRange
					+ "' ORDER BY voternum DESC LIMIT 0, 10");
			for (Map<String, Object> value : hotPoll) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), "", 0);
			}
			request.setAttribute("hotpoll", hotPoll);
			request.setAttribute("topic", Common.getTopic(request, (Integer) poll.get("topicid")));

			if (!(Boolean) space.get("self")) {
				Map<String, Object> TPL = new HashMap<String, Object>();
				TPL.put("spacetitle", "投票");
				TPL.put("spacemenus", new String[] {
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
								+ "&view=me\">TA的所有投票</a>",
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=poll&pid=" + pid
								+ "\">查看投票详情</a>"});
				request.setAttribute("TPL", TPL);
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("pid", pid);
			request.setAttribute("isfriend", isFriend);
			request.setAttribute("expiration", expiration);
			request.setAttribute("managepoll", Common.checkPerm(request, response, "managepoll"));
			request.setAttribute("tpl_css", "poll");
			request.setAttribute("poll", poll);
			request.setAttribute("navtitle", poll.get("subject") + " - " + "投票 - ");
			return include(request, response, sConfig, sGlobal, "space_poll_view.jsp");
		} else {

			String view = request.getParameter("view");
			Map<String, String[]> paramMap = request.getParameterMap();
			if (Common.empty(view)) {
				paramMap.put("view", new String[] {"new"});
				view = "new";
			} else {
				view = Common.trim(view);
				paramMap.put("view", new String[] {view});
			}
			if ("all".equals(view)) {
				view = "new";
				paramMap.put("view", new String[] {"new"});
			}
			int perPage = 10;
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}

			int count = 0;
			String whereSQL = null;
			String indexSQL = null;
			String leftSQL = null;
			String orderSQL = "p.dateline";
			String countTable = JavaCenterHome.getTableName("poll") + " p ";
			String theURL = null;
			List<String> wheres = new ArrayList<String>();
			if ("new".equals(view)) {
				indexSQL = "USE INDEX (dateline)";
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=new";
			} else if ("hot".equals(view)) {
				String filtrateStr = request.getParameter("filtrate");
				if (Common.empty(filtrateStr)) {
					paramMap.put("filtrate", new String[] {"all"});
					filtrateStr = "all";
				} else {
					filtrateStr = Common.trim(filtrateStr);
					paramMap.put("filtrate", new String[] {filtrateStr});
				}

				indexSQL = "USE INDEX (voternum)";
				orderSQL = "p.voternum";
				int timeRange = 0;
				int timestamp = (Integer) sGlobal.get("timestamp");
				if ("week".equals(filtrateStr)) {
					timeRange = timestamp - 604800;
				} else if ("month".equals(filtrateStr)) {
					timeRange = timestamp - 2592000;
				}
				if (timeRange > 0) {
					wheres.add("p.lastvote >= '" + timeRange + "'");
				}
				Map<String, String> filtrate = new HashMap<String, String>();
				filtrate.put(filtrateStr, " class=\"active\"");
				request.setAttribute("filtrate", filtrate);
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=hot";
			} else if ("friend".equals(view)) {
				indexSQL = "USE INDEX (dateline)";
				wheres.add("p.uid IN (" + space.get("feedfriend") + ")");
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=friend";
			} else if ("reward".equals(view)) {
				indexSQL = "USE INDEX (percredit)";
				orderSQL = "p.percredit DESC, p.dateline";
				wheres.add("p.percredit > 0");
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=reward";
			} else {
				String filtrateStr = request.getParameter("filtrate");
				if (Common.empty(filtrateStr)) {
					paramMap.put("filtrate", new String[] {"me"});
					filtrateStr = "me";
				} else {
					filtrateStr = Common.trim(filtrateStr);
					paramMap.put("filtrate", new String[] {filtrateStr});
				}

				if ("join".equals(filtrateStr)) {
					leftSQL = JavaCenterHome.getTableName("polluser") + " pu LEFT JOIN ";
					indexSQL = " ON p.pid=pu.pid ";
					wheres.add("pu.uid='" + space.get("uid") + "'");
					orderSQL = "pu.dateline";
					countTable = JavaCenterHome.getTableName("polluser") + " pu ";
				} else if ("expiration".equals(filtrateStr)) {
					countTable = JavaCenterHome.getTableName("polluser") + " pu, "
							+ JavaCenterHome.getTableName("poll") + " p";
					orderSQL = "pu.dateline";
					wheres.add("pu.uid='" + space.get("uid")
							+ "' AND pu.pid=p.pid  AND p.expiration>0 AND p.expiration<='"
							+ sGlobal.get("timestamp") + "'");
				} else {
					wheres.add("p.uid='" + space.get("uid") + "'");
				}
				Map<String, String> filtrate = new HashMap<String, String>();
				filtrate.put(filtrateStr, " class=\"active\"");
				request.setAttribute("filtrate", filtrate);
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do")
						+ "&view=me&filtrate=" + filtrateStr;
			}
			String searchKey = Common.stripSearchKey(request.getParameter("searchkey"));
			if (!Common.empty(searchKey)) {
				wheres.add("p.subject LIKE '%" + searchKey + "%'");
				theURL += "&searchkey=" + request.getParameter("searchkey");
				Map<String, Object> resultMap = Common.ckSearch(theURL, request, response);
				if (resultMap != null) {
					return showMessage(request, response, (String) resultMap.get("msgkey"),
							(String) resultMap.get("url_forward"), (Integer) resultMap.get("second"),
							(String[]) resultMap.get("args"));
				}
				request.setAttribute("searchkey", searchKey);
			}
			if (wheres.size() > 0) {
				whereSQL = " WHERE " + Common.implode(wheres, " AND ");
			}
			whereSQL = whereSQL == null ? "" : whereSQL;
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + countTable + " " + whereSQL);
			if (("p.uid=" + space.get("uid") + "'").equals(whereSQL)
					&& (Integer) space.get("pollnum") != count) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET pollnum='" + count + "' WHERE uid='" + space.get("uid") + "'");
			}
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (count > 0) {
				String filtrate = request.getParameter("filtrate");
				List<Map<String, Object>> list = null;
				if ("expiration".equals(filtrate)) {
					list = dataBaseService.executeQuery("SELECT p.*,pf.* FROM "
							+ JavaCenterHome.getTableName("polluser") + " pu, "
							+ JavaCenterHome.getTableName("poll") + " p,"
							+ JavaCenterHome.getTableName("pollfield") + " pf " + whereSQL
							+ " AND p.pid=pf.pid	ORDER BY " + orderSQL + " DESC LIMIT " + start + ","
							+ perPage);
				} else {
					indexSQL = indexSQL == null ? "" : indexSQL;
					leftSQL = leftSQL == null ? "" : leftSQL;
					list = dataBaseService.executeQuery("SELECT p.*,pf.* FROM " + leftSQL + " "
							+ JavaCenterHome.getTableName("poll") + " p " + indexSQL + " LEFT JOIN "
							+ JavaCenterHome.getTableName("pollfield") + " pf ON pf.pid=p.pid " + whereSQL
							+ " ORDER BY " + orderSQL + " DESC LIMIT " + start + "," + perPage);
				}
				Map userList = new HashMap();
				for (Map<String, Object> value : list) {
					if (!Common.empty(value.get("credit")) && !Common.empty(value.get("percredit"))
							&& (Integer) value.get("credit") < (Integer) value.get("percredit")) {
						value.put("percredit", value.get("credit"));
					}
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					value.put("option", Serializer.unserialize((String) value.get("option")));
					userList.put(value.get("uid"), value.get("username"));
				}
				request.setAttribute("list", list);
				request.setAttribute("userList", userList);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL, null,
					null));
			Common.realname_get(sGlobal, sConfig, sNames, space);
			if (Common.empty(sGlobal.get("inajax"))) {
				if (!(Boolean) space.get("self")) {
					Map<String, Object> TPL = new HashMap<String, Object>();
					TPL.put("spacetitle", "投票");
					TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
							+ "&do=" + request.getAttribute("do") + "&view=me\">TA的所有投票</a>"});
					request.setAttribute("TPL", TPL);
				}
				Map<String, String> actives = new HashMap<String, String>();
				actives.put(view, " class=\"active\"");
				request.setAttribute("actives", actives);
			}
			request.setAttribute("count", count);
			request.setAttribute("tpl_css", "poll");
			request.setAttribute("navtitle", "投票 - ");
			return include(request, response, sConfig, sGlobal, "space_poll_list.jsp");
		}
	}

	
	public ActionForward space_share(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int id = Common.intval(request.getParameter("id"));
		int page = Common.intval(request.getParameter("page"));
		page = page < 1 ? 1 : page;
		if (id > 0) {
			List<Map<String, Object>> shareList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("share") + " WHERE sid='" + id + "' AND uid='"
					+ space.get("uid") + "'");
			if (shareList.size() == 0) {
				return showMessage(request, response, "share_does_not_exist");
			}
			Map<String, Object> share = shareList.get(0);
			Common.mkShare(share);
			int perPage = 50;
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			int cid = Common.intval(request.getParameter("cid"));
			request.setAttribute("cid", cid);
			String csql = cid > 0 ? "cid='" + cid + "' AND" : "";
			int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("comment") + " WHERE " + csql + " id='" + id
					+ "' AND idtype='sid'");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("comment") + " WHERE " + csql + " id='" + id
						+ "' AND idtype='sid' ORDER BY dateline LIMIT " + start + "," + perPage);
				for (Map<String, Object> value : list) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
							(String) value.get("author"), "", 0);
				}
				request.setAttribute("list", list);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
					"space.jsp?uid=" + share.get("uid") + "&do=share&id=" + id, null, "comment_ul"));
			request.setAttribute("topic", Common.getTopic(request, (Integer) share.get("topicid")));
			Common.realname_get(sGlobal, sConfig, sNames, space);
			String tplTitle = null;
			try {
				tplTitle = Common.getStr((String) share.get("title_template"), 0, false, false, false, 0, -1,
						request, response);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			if (!(Boolean) space.get("self")) {
				Map TPL = new HashMap();
				TPL.put("spacetitle", "分享");
				TPL.put("spacemenus", new String[] {
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=share&view=me\">TA的所有分享</a>",
						"<a href=\"space.jsp?uid=" + space.get("uid") + "&do=share&id=" + share.get("sid")
								+ "\">查看分享</a>"});
				request.setAttribute("TPL", TPL);
			}
			request.setAttribute("id", id);
			request.setAttribute("manageshare", Common.checkPerm(request, response, "manageshare"));
			request.setAttribute("share", share);
			request.setAttribute("navtitle", (Common.empty(tplTitle) ? "" : tplTitle + " - ") + "分享 - ");
			return include(request, response, sConfig, sGlobal, "space_share_view.jsp");
		} else {
			String view = request.getParameter("view");
			if (Common.empty(view)
					&& (space.get("friendnum") == null || (Integer) space.get("friendnum") < (Integer) sConfig
							.get("showallfriendnum"))) {
				view = "all";
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("view", new String[] {"all"});
			}

			int perPage = 20;
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			String f_index = "";
			String whereSQL = null;
			String theURL = null;
			String action = (String) request.getAttribute("do");
			Map<String, String> actives = new HashMap<String, String>();
			if ("all".equals(view)) {
				whereSQL = "1";
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + action + "&view=all";
				actives.put("all", " class=\"active\"");
			} else if (Common.empty(space.get("feedfriend"))) {
				whereSQL = "uid='" + space.get("uid") + "'";
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + action + "&view=me";
				actives.put("me", " class=\"active\"");
			} else {
				whereSQL = "uid IN (" + space.get("feedfriend") + ")";
				theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + action + "&view=we";
				f_index = "USE INDEX(dateline)";
				actives.put("we", " class=\"active\"");
			}
			String type = Common.trim(request.getParameter("type"));
			Map<String, String> sub_actives = new HashMap<String, String>();
			if (!Common.empty(type)) {
				sub_actives.put("type_" + type, " class=\"active\"");
				whereSQL += " AND type='" + type + "'";
			} else {
				sub_actives.put("type_all", " class=\"active\"");
			}
			int sid = Common.intval(request.getParameter("sid"));
			String shareSQL = sid > 0 ? "sid='" + sid + "' AND" : "";
			int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("share") + " WHERE " + shareSQL + " " + whereSQL);
			if ("".equals(shareSQL) && ("uid='" + space.get("uid") + "'").equals(whereSQL)
					&& space.get("sharenum") != null && (Integer) space.get("sharenum") != count) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET sharenum='" + count + "' WHERE uid='" + space.get("uid") + "'");
			}
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("share") + " " + f_index + " WHERE " + shareSQL + " "
						+ whereSQL + " ORDER BY dateline DESC LIMIT " + start + "," + perPage);
				for (Map<String, Object> value : list) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					Common.mkShare(value);
				}
				request.setAttribute("list", list);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theURL
					+ "&type=" + type, null, null));
			Common.realname_get(sGlobal, sConfig, sNames, space);
			if (!Common.empty(sGlobal.get("inajax"))) {
				request.setAttribute("count", count);
			} else {
				if (!(Boolean) space.get("self")) {
					Map TPL = new HashMap();
					TPL.put("spacetitle", "分享");
					TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
							+ "&do=share&view=me\">TA的所有分享</a>"});
					request.setAttribute("TPL", TPL);
				} else {
					request.setAttribute("actives", actives);
				}
			}
			request.setAttribute("theurl", theURL);
			request.setAttribute("sub_actives", sub_actives);
			request.setAttribute("navtitle", "分享 - ");
			return include(request, response, sConfig, sGlobal, "space_share_list.jsp");
		}
	}

	
	public ActionForward space_tag(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int id = Common.intval(request.getParameter("id"));
		String name = request.getParameter("name");
		name = Common.empty(name) ? null : Common.stripSearchKey(name);
		int start = Common.intval(request.getParameter("start"));
		int maxPage = (Integer) sConfig.get("maxpage");
		int count = 0;
		if (id > 0 || !Common.empty(name)) {
			int perPage = 30;
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			List<Map<String, Object>> tagList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("tag") + " WHERE "
					+ (id > 0 ? "tagid='" + id + "'" : "tagname='" + name + "'") + " LIMIT 1");
			if (tagList.isEmpty()) {
				return showMessage(request, response, "tag_does_not_exist");
			}
			Map<String, Object> tag = tagList.get(0);
			if ((Integer) tag.get("close") > 0) {
				return showMessage(request, response, "tag_locked");
			}
			int priNum = 0;
			List<Map<String, Object>> tagBlogList = dataBaseService.executeQuery("SELECT blog.* FROM "
					+ JavaCenterHome.getTableName("tagblog") + " tb , " + JavaCenterHome.getTableName("blog")
					+ " blog WHERE tb.tagid='" + tag.get("tagid") + "' AND blog.blogid=tb.blogid LIMIT "
					+ start + "," + perPage);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			for (Map<String, Object> value : tagBlogList) {
				if (Common.empty(value.get("friend"))) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					list.add(value);
				} else {
					priNum++;
				}
				count++;
			}
			request.setAttribute("list", list);
			request.setAttribute("prinum", priNum);
			Common.realname_get(sGlobal, sConfig, sNames, space);
			try {
				request.setAttribute("multi", Common.smulti(sGlobal, start, perPage, count, "space.jsp?uid="
						+ space.get("uid") + "&do=" + request.getAttribute("do") + "&id=" + id, null));
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("tag", tag);
			request.setAttribute("navtitle", (Common.empty(tag.get("tagname")) ? "" : tag.get("tagname")
					+ " - ")
					+ "标签 - ");
			return include(request, response, sConfig, sGlobal, "space_tag_view.jsp");
		} else {
			int perPage = 100;
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("tag") + " ORDER BY blognum DESC LIMIT " + start + ","
					+ perPage);
			count = list.size();
			try {
				request.setAttribute("multi", Common.smulti(sGlobal, start, perPage, count, "space.jsp?uid="
						+ space.get("uid") + "&do=" + request.getAttribute("do"), null));
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("list", list);
			request.setAttribute("navtitle", "标签 - ");
			return include(request, response, sConfig, sGlobal, "space_tag_list.jsp");
		}
	}

	
	public ActionForward space_thread(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int page = Common.intval(request.getParameter("page"));
		page = page < 1 ? 1 : page;
		int id = Common.intval(request.getParameter("id"));
		if (id > 0) {
			int eventId = Common.intval(request.getParameter("eventid"));
			Map<String, Object> event = null;
			Map<String, Object> userEvent = null;
			if (eventId > 0) {
				List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT e.* FROM "
						+ JavaCenterHome.getTableName("event") + " e WHERE e.eventid='" + eventId + "'");
				if (eventList.size() == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}
				event = eventList.get(0);
				List<Map<String, Object>> userEventList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("userevent") + " WHERE uid = '"
						+ sGlobal.get("supe_uid") + "' AND eventid = '" + eventId + "'");
				if (userEventList.size() > 0) {
					userEvent = userEventList.get(0);
				}
			}
			Map<String, Object> thread = null;
			List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("thread") + " WHERE tid='" + id + "' LIMIT 1");
			if (threadList.size() == 0) {
				return showMessage(request, response, "topic_does_not_exist");
			}
			thread = threadList.get(0);
			space = Common.getSpace(request, sGlobal, sConfig, thread.get("uid"));
			if ((Integer) space.get("flag") == -1) {
				return showMessage(request, response, "space_has_been_locked");
			}
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			Common.realname_set(sGlobal, sConfig, sNames, (Integer) thread.get("uid"), (String) thread
					.get("username"), "", 0);
			int tagid = (Integer) thread.get("tagid");
			if (eventId > 0 && (Integer) event.get("tagid") != tagid) {
				return showMessage(request, response, "event_mtag_not_match");
			}
			if (eventId == 0 && !Common.empty(thread.get("eventid"))) {
				List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("event") + " WHERE eventid='" + thread.get("eventid")
						+ "' LIMIT 1");
				if (eventList.size() == 0) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread")
							+ " SET eventid='0' WHERE ");
				} else {
					event = eventList.get(0);
				}
			}
			Map<String, Object> mtag = null;
			try {
				mtag = Common.getMtag(request, response, (Integer) sGlobal.get("supe_uid"), tagid);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			if (!Common.empty(mtag.get("close"))) {
				return showMessage(request, response, "mtag_close");
			}
			if (eventId > 0 && Common.empty(event.get("public"))
					&& (userEvent == null || (Integer) userEvent.get("status") < 2)) {
				return showMessage(request, response, "event_memberstatus_limit", "space.jsp?do=event");
			} else if (Common.empty(mtag.get("allowview"))) {
				return showMessage(request, response, "mtag_not_allow_to_do", "space.jsp?do=mtag&tagid="
						+ tagid);
			}
			int perPage = 30;
			int start = (page - 1) * perPage;
			int count = (Integer) thread.get("replynum");
			if (count % perPage == 0) {
				perPage = perPage + 1;
			}
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			int pid = Common.intval(request.getParameter("pid"));
			String pSQL = pid > 0 ? "(isthread='1' OR pid='" + pid + "') AND" : "";
			int postNum = start;
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("post") + " WHERE " + pSQL + " tid='" + thread.get("tid")
					+ "' ORDER BY dateline LIMIT " + start + "," + perPage);
			for (Map<String, Object> value : list) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), "", 0);
				value.put("num", postNum);
				postNum++;
			}
			if (list.size() > 0 && !Common.empty(list.get(0).get("isthread"))) {
				Map<String, Object> content = list.get(0);
				thread.put("content", content);
				content.put("message", blogService.blogBBCode((String) content.get("message")));
				list.remove(0);
			} else {
				thread.put("content", null);
			}
			request.setAttribute("list", list);
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
					"space.jsp?uid=" + thread.get("uid") + "&do=" + request.getAttribute("do") + "&id=" + id,
					null, null));
			Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
			if (!(Boolean) space.get("self") && !("" + id).equals(sCookie.get("view_tid"))) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread")
						+ " SET viewnum=viewnum+1 WHERE tid='" + id + "'");
				dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("log")
						+ " (`id`, `idtype`) VALUES ('" + space.get("uid") + "', 'uid') ");
				CookieHelper.setCookie(request, response, "view_tid", id + "");
			}
			request.setAttribute("hash", Common.md5(thread.get("uid") + "\t" + thread.get("dateline")));
			id = (Integer) thread.get("tid");
			request.setAttribute("id", id);
			String idtype = "tid";
			request.setAttribute("idtype", idtype);
			Map globalClick = Common.getCacheDate(request, response, "/data/cache/cache_click.jsp",
					"globalClick");
			Map<Object, Map> clicks = Common.empty(globalClick.get("tid")) ? new HashMap()
					: (Map) globalClick.get("tid");
			Set clickKeys = clicks.keySet();
			Map value = null;
			int maxclicknum = 0;
			for (Object clickKey : clickKeys) {
				value = clicks.get(clickKey);
				value.put("clicknum", thread.get("click_" + clickKey));
				value.put("classid", Common.rand(1, 4));
				if (value.get("clicknum") != null && (Integer) value.get("clicknum") > maxclicknum) {
					maxclicknum = (Integer) value.get("clicknum");
				}
			}
			request.setAttribute("maxclicknum", maxclicknum);
			List<Map<String, Object>> clickUserList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("clickuser") + " WHERE id='" + id + "' AND idtype='"
					+ idtype + "' ORDER BY dateline DESC LIMIT 0,18");
			for (Map<String, Object> clickUser : clickUserList) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) clickUser.get("uid"),
						(String) clickUser.get("username"), "", 0);
				clickUser.put("clickname", clicks.get(clickUser.get("clickid")).get("name"));
			}
			request.setAttribute("clickuserlist", clickUserList);
			request.setAttribute("topic", Common.getTopic(request, (Integer) thread.get("topicid")));
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("eventid", eventId);
			request.setAttribute("event", event);
			if (eventId > 0) {
				request.setAttribute("userevent", userEvent);
			}
			if (!Common.empty(thread.get("magicegg"))) {
				StringBuffer magiceggImage = new StringBuffer();
				for (int i = 0; i < (Integer) thread.get("magicegg"); i++) {
					magiceggImage.append("<img src=\"image/magic/egg/" + Common.rand(1, 6) + ".gif\" />");
				}
				thread.put("magiceggImage", magiceggImage.toString());
			}
			request.setAttribute("mtag", mtag);
			request.setAttribute("thread", thread);
			request.setAttribute("managethread", Common.checkPerm(request, response, "managethread"));
			request.setAttribute("pid", pid);
			request.setAttribute("clicks", clicks);
			request.setAttribute("navtitle", (!Common.empty(thread.get("subject")) ? thread.get("subject")
					+ " - " : "")
					+ (!Common.empty(mtag.get("tagname")) ? mtag.get("tagname") + " - " : "")
					+ (!Common.empty(mtag.get("title")) ? mtag.get("title") + " - " : "") + "话题 - ");
			request.setAttribute("tpl_css", "thread");
			return include(request, response, sConfig, sGlobal, "space_thread_view.jsp");
		} else {
			int perPage = 30;
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			String view = request.getParameter("view");
			Map<String, String[]> paramMap = request.getParameterMap();
			if (!Common.in_array(new String[] {"hot", "new", "me", "all"}, view)) {
				paramMap.put("view", new String[] {"hot"});
				view = "hot";
			}
			String whereSQL = null;
			String f_index = "";
			if ("hot".equals(view)) {
				int minHot = (Integer) sConfig.get("feedhotmin");
				minHot = minHot < 1 ? 3 : minHot;
				whereSQL = "main.hot>='" + minHot + "'";
				if (page == 1) {
					Map<Object, Map> globalProfield = Common.getCacheDate(request, response,
							"/data/cache/cache_profield.jsp", "globalProfield");
					List<Map<String, Object>> rlist = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("mtag")
							+ " mt ORDER BY mt.threadnum DESC LIMIT 0,6");
					for (Map<String, Object> value : rlist) {
						value.put("title", globalProfield.get(value.get("fieldid")).get("title"));
						if (Common.empty(value.get("pic"))) {
							value.put("pic", "image/nologo.jpg");
						}
					}
					request.setAttribute("rlist", rlist);
				}
			} else if ("me".equals(view)) {
				whereSQL = "main.uid='" + space.get("uid") + "'";
			} else {
				whereSQL = "1";
				f_index = "USE INDEX (lastpost)";
			}
			String theURL = "space.jsp?uid=" + space.get("uid") + "&do=thread&view=" + view;
			int count = 0;
			String searchKey = Common.stripSearchKey(request.getParameter("searchkey"));
			if (!Common.empty(searchKey)) {
				whereSQL = "main.subject LIKE '%" + searchKey + "%'";
				theURL += "&searchkey=" + searchKey;
				Map<String, Object> resultMap = Common.ckSearch(theURL, request, response);
				if (resultMap != null) {
					return showMessage(request, response, (String) resultMap.get("msgkey"),
							(String) resultMap.get("url_forward"), (Integer) resultMap.get("second"),
							(String[]) resultMap.get("args"));
				}
				request.setAttribute("searchkey", searchKey);
			}

			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (whereSQL != null) {
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("thread") + " main WHERE " + whereSQL);
				if (("main.uid='" + space.get("uid") + "'").equals(whereSQL)
						&& space.get("threadnum") != null && (Integer) space.get("threadnum") != count) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET threadnum='" + count + "' WHERE uid='" + space.get("uid") + "'");
				}
				if (count > 0) {
					List<Map<String, Object>> list = dataBaseService
							.executeQuery("SELECT main.*,field.tagname,field.membernum,field.fieldid,field.pic FROM "
									+ JavaCenterHome.getTableName("thread")
									+ " main "
									+ f_index
									+ " LEFT JOIN "
									+ JavaCenterHome.getTableName("mtag")
									+ " field ON field.tagid=main.tagid WHERE "
									+ whereSQL
									+ " ORDER BY main.lastpost DESC LIMIT " + start + "," + perPage);
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("lastauthorid"),
								(String) value.get("lastauthor"), "", 0);
						try {
							value.put("tagname", Common.getStr((String) value.get("tagname"), 20, false,
									false, false, 0, 0, request, response));
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
						if (Common.empty(value.get("pic"))) {
							value.put("pic", "image/nologo.jpg");
						}
						if (Common.empty(sGlobal.get("inajax"))) {
							if (!Common.empty(value.get("magicegg"))) {
								StringBuffer magiceggImage = new StringBuffer();
								for (int i = 0; i < (Integer) value.get("magicegg"); i++) {
									magiceggImage.append("<img src=\"image/magic/egg/" + Common.rand(1, 6)
											+ ".gif\" />");
								}
								value.put("magiceggImage", magiceggImage.toString());
							}
						}
					}
					request.setAttribute("list", list);
					request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
							theURL, null, null));
				}
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			if (Common.empty(sGlobal.get("inajax"))) {
				if (!(Boolean) space.get("self")) {
					Map TPL = new HashMap();
					TPL.put("spacetitle", "话题");
					TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
							+ "&do=thread&view=me\">TA的所有话题</a>"});
					request.setAttribute("TPL", TPL);
				} else {
					Map<String, String> actives = new HashMap<String, String>();
					actives.put(view, " class=\"active\"");
					request.setAttribute("actives", actives);
				}
			}

			request.setAttribute("count", count);
			request.setAttribute("tpl_css", "thread");
			request.setAttribute("navtitle", "群组话题 - ");
			return include(request, response, sConfig, sGlobal, "space_thread_list.jsp");
		}
	}

	
	public ActionForward space_top(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int perPage = 20;
		int page = Common.intval(request.getParameter("page"));
		page = page <= 1 ? 1 : page;
		int start = (page - 1) * perPage;
		if (Common.empty(sConfig.get("networkpage"))) {
			start = 0;
		}
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		String cacheFile = null;
		double cacheTime = Double.parseDouble(sConfig.get("topcachetime").toString());
		if (cacheTime < 5){
			cacheTime = 5;
			sConfig.put("topcachetime", "5");
		}
		int count = 0;
		int now_pos = 0;

		String view = request.getParameter("view");
		if (!Common.in_array(new String[] {"online", "mm", "gg", "credit", "experience", "friendnum",
				"viewnum", "updatetime"}, view)) {
			view = "show";
			Map<String, String[]> paramMap = request.getParameterMap();
			paramMap.put("view", new String[] {"show"});
		}
		String cSQL = null;
		String SQL = null;
		String cookieName = null;
		Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
		if ("show".equals(view)) {
			cSQL = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("show");
			SQL = "SELECT main.*,space.*, field.* FROM " + JavaCenterHome.getTableName("show")
					+ " main LEFT JOIN " + JavaCenterHome.getTableName("space")
					+ " space ON space.uid=main.uid LEFT JOIN " + JavaCenterHome.getTableName("spacefield")
					+ " field ON field.uid=main.uid ORDER BY main.credit DESC";
			String timestamp = sGlobal.get("timestamp").toString();
			if (timestamp.substring(timestamp.length() - 1, timestamp.length()) == "0") {
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("show")
						+ " WHERE credit<1");
			}
			Map<String, Object> whereArr = new HashMap<String, Object>();
			whereArr.put("uid", space.get("uid"));
			space.put("showcredit", Common.intval(Common.getCount("show", whereArr, "credit")));
			cookieName = "space_top_" + view;
			if (!Common.empty(sCookie.get(cookieName))) {
				now_pos = Integer.valueOf(sCookie.get(cookieName));
			} else {
				now_pos = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("show") + " WHERE credit>'" + space.get("showcredit")
						+ "'");
				now_pos++;
				CookieHelper.setCookie(request, response, cookieName, now_pos + "");
			}
		} else if ("mm".equals(view)) {
			count = 100;
			cacheFile = JavaCenterHome.jchRoot + "/data/cache/cache_top_mm.txt";
			SQL = "SELECT main.*, field.* FROM " + JavaCenterHome.getTableName("space") + " main, "
					+ JavaCenterHome.getTableName("spacefield")
					+ " field WHERE field.sex='2' AND field.uid=main.uid ORDER BY main.viewnum DESC";
			cookieName = "space_top_" + view;
			if (!Common.empty(sCookie.get(cookieName))) {
				now_pos = Integer.valueOf(sCookie.get(cookieName));
			} else {
				if (space.get("sex") != null && (Integer) space.get("sex") == 2) {
					now_pos = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("space") + " s, "
							+ JavaCenterHome.getTableName("spacefield") + " f WHERE s.viewnum>'"
							+ space.get("viewnum") + "' AND f.sex='2' AND f.uid=s.uid");
					now_pos++;
				} else {
					now_pos = -1;
				}
				CookieHelper.setCookie(request, response, cookieName, now_pos + "");
			}
		} else if ("gg".equals(view)) {
			count = 100;
			cacheFile = JavaCenterHome.jchRoot + "/data/cache/cache_top_gg.txt";
			SQL = "SELECT main.*, field.* FROM " + JavaCenterHome.getTableName("space") + " main, "
					+ JavaCenterHome.getTableName("spacefield")
					+ " field WHERE field.sex='1' AND field.uid=main.uid ORDER BY main.viewnum DESC";
			cookieName = "space_top_" + view;
			if (!Common.empty(sCookie.get(cookieName))) {
				now_pos = Integer.valueOf(sCookie.get(cookieName));
			} else {
				if (space.get("sex") != null && (Integer) space.get("sex") == 1) {
					now_pos = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("space") + " s, "
							+ JavaCenterHome.getTableName("spacefield") + " f WHERE s.viewnum>'"
							+ space.get("viewnum") + "' AND f.sex='1' AND f.uid=s.uid");
					now_pos++;
				} else {
					now_pos = -1;
				}
				CookieHelper.setCookie(request, response, cookieName, now_pos + "");
			}
		} else if ("credit".equals(view)) {
			count = 100;
			cacheFile = JavaCenterHome.jchRoot + "/data/cache/cache_top_credit.txt";
			SQL = "SELECT main.*, field.* FROM " + JavaCenterHome.getTableName("space") + " main LEFT JOIN "
					+ JavaCenterHome.getTableName("spacefield")
					+ " field ON field.uid=main.uid ORDER BY main.credit DESC";
			cookieName = "space_top_" + view;
			if (!Common.empty(sCookie.get(cookieName))) {
				now_pos = Integer.valueOf(sCookie.get(cookieName));
			} else {
				now_pos = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("space") + " s WHERE s.credit>'" + space.get("credit")
						+ "'");
				now_pos++;
				CookieHelper.setCookie(request, response, cookieName, now_pos + "");
			}
		} else if ("experience".equals(view)) {
			count = 100;
			cacheFile = JavaCenterHome.jchRoot + "/data/cache/cache_top_experience.txt";
			SQL = "SELECT main.*, field.* FROM " + JavaCenterHome.getTableName("space") + " main LEFT JOIN "
					+ JavaCenterHome.getTableName("spacefield")
					+ " field ON field.uid=main.uid ORDER BY main.experience DESC";
			cookieName = "space_top_" + view;
			if (!Common.empty(sCookie.get(cookieName))) {
				now_pos = Integer.valueOf(sCookie.get(cookieName));
			} else {
				now_pos = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("space") + " s WHERE s.experience>'"
						+ space.get("experience") + "'");
				now_pos++;
				CookieHelper.setCookie(request, response, cookieName, now_pos + "");
			}
		} else if ("friendnum".equals(view)) {
			count = 100;
			cacheFile = JavaCenterHome.jchRoot + "/data/cache/cache_top_friendnum.txt";
			SQL = "SELECT main.*, field.* FROM " + JavaCenterHome.getTableName("space") + " main LEFT JOIN "
					+ JavaCenterHome.getTableName("spacefield")
					+ " field ON field.uid=main.uid ORDER BY main.friendnum DESC";
			cookieName = "space_top_" + view;
			if (!Common.empty(sCookie.get(cookieName))) {
				now_pos = Integer.valueOf(sCookie.get(cookieName));
			} else {
				now_pos = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("space") + " s WHERE s.friendnum>'"
						+ space.get("friendnum") + "'");
				now_pos++;
				CookieHelper.setCookie(request, response, cookieName, now_pos + "");
			}
		} else if ("viewnum".equals(view)) {
			count = 100;
			cacheFile = JavaCenterHome.jchRoot + "/data/cache/cache_top_viewnum.txt";
			SQL = "SELECT main.*, field.* FROM " + JavaCenterHome.getTableName("space") + " main LEFT JOIN "
					+ JavaCenterHome.getTableName("spacefield")
					+ " field ON field.uid=main.uid ORDER BY main.viewnum DESC";
			cookieName = "space_top_" + view;
			if (!Common.empty(sCookie.get(cookieName))) {
				now_pos = Integer.valueOf(sCookie.get(cookieName));
			} else {
				now_pos = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("space") + " s WHERE s.viewnum>'"
						+ space.get("viewnum") + "'");
				now_pos++;
				CookieHelper.setCookie(request, response, cookieName, now_pos + "");
			}
		} else if ("online".equals(view)) {
			cSQL = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("session");
			SQL = "SELECT field.*, space.*, main.* FROM " + JavaCenterHome.getTableName("session")
					+ " main USE INDEX (lastactivity) LEFT JOIN " + JavaCenterHome.getTableName("space")
					+ " space ON space.uid=main.uid LEFT JOIN " + JavaCenterHome.getTableName("spacefield")
					+ " field ON field.uid=main.uid ORDER BY main.lastactivity DESC";
			now_pos = -1;
		} else if ("updatetime".equals(view)) {
			cSQL = "SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("space");
			SQL = "SELECT main.*, field.* FROM " + JavaCenterHome.getTableName("space")
					+ " main USE INDEX (updatetime) LEFT JOIN " + JavaCenterHome.getTableName("spacefield")
					+ " field ON field.uid=main.uid ORDER BY main.updatetime DESC";
			now_pos = -1;
		}
		List<Map<String, Object>> forSpaceList = null;
		boolean cacheMode = false;
		if (count == 0) {
			count = Common.empty(sConfig.get("networkpage")) ? 1 : dataBaseService.findRows(cSQL);
			String multi = Common.multi(request, count, perPage, page, maxPage, "space.jsp?do=top&view=" + view,
					null, null);
			request.setAttribute("multi", multi);
		} else {
			cacheMode = true;
			start = 0;
			perPage = count;
			File file = null;
			if (cacheFile != null) {
				file = new File(cacheFile);
			}
			int timestamp = (Integer) sGlobal.get("timestamp");
			if (cacheFile != null && file.exists()
					&& timestamp - (file.lastModified() / 1000) < cacheTime * 60) {
				forSpaceList = Serializer.unserialize(FileHelper.readFile(cacheFile));
			}
		}
		if (count > 0 && Common.empty(forSpaceList)) {
			forSpaceList = dataBaseService.executeQuery(SQL + " LIMIT " + start + "," + perPage);
			if (cacheMode && cacheFile != null) {
				FileHelper.writeFile(cacheFile, Serializer.serialize(forSpaceList));
			}
		}
		List fuids = null;
		Map<Object, Map<String, Object>> list = null;
		if (forSpaceList != null) {
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			fuids = new ArrayList(forSpaceList.size());
			list = new HashMap<Object, Map<String, Object>>();
			for (Map<String, Object> value : forSpaceList) {
				value.put("isfriend", (value.get("uid").equals(space.get("uid")) || (!Common.empty(space
						.get("friends")) && Common
						.in_array((String[]) space.get("friends"), value.get("uid")))) ? true : false);
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), (String) value.get("name"), value.get("namestatus") == null ? 0
						: (Integer) value.get("namestatus"));
				fuids.add(value.get("uid"));
				value.put("gColor", Common.getColor(request, response, (Integer) value.get("groupid")));
				value.put("gIcon", Common.getIcon(request, response, (Integer) value.get("groupid")));
				list.put(value.get("uid"), value);
			}
		}
		if (fuids != null && fuids.size() > 0) {
			List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("session") + " WHERE uid IN (" + Common.sImplode(fuids)
					+ ")");
			Map ols = new HashMap();
			for (Map<String, Object> value : sessionList) {
				if (Common.empty(value.get("magichidden"))) {
					ols.put(value.get("uid"), value.get("lastactivity"));
				} else if ("online".equals(view) && !Common.empty(list.get(value.get("uid")))) {
					forSpaceList.remove(list.get(value.get("uid")));
				}
			}
			request.setAttribute("ols", ols);
		}
		Map<String, String> actives = new HashMap<String, String>();
		request.setAttribute("list", forSpaceList);
		actives.put(view, " class=\"active\"");
		request.setAttribute("cache_mode", cacheMode);
		request.setAttribute("cache_time", cacheTime);
		request.setAttribute("actives", actives);
		request.setAttribute("now_pos", now_pos);
		request.setAttribute("navtitle", "排名 - ");
		return include(request, response, sConfig, sGlobal, "space_top.jsp");
	}

	
	public ActionForward space_topic(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int page = Common.intval(request.getParameter("page"));
		if (page < 1) {
			page = 1;
		}
		int perPage = 10;
		int start = (page - 1) * perPage;
		int topicId = Common.intval(request.getParameter("topicid"));
		if (topicId == 0) {
			String view = request.getParameter("view");
			List<Map<String, Object>> list = null;
			int count = 0;
			if ("hot".equals(view)) {
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("topic") + " WHERE 1 LIMIT 1");
				list = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("topic")
						+ " ORDER BY joinnum DESC LIMIT " + start + "," + perPage);
			} else if ("me".equals(view)) {
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("topicuser") + " WHERE uid='" + sGlobal.get("supe_uid")
						+ "' LIMIT 1");
				list = dataBaseService.executeQuery("SELECT t.* FROM "
						+ JavaCenterHome.getTableName("topicuser") + " tu LEFT JOIN "
						+ JavaCenterHome.getTableName("topic") + " t ON t.topicid=tu.topicid WHERE tu.uid='"
						+ sGlobal.get("supe_uid") + "' ORDER BY tu.dateline DESC LIMIT " + start + ","
						+ perPage);
			} else {
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("view", new String[] {"new"});
				view = "new";
				count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("topic") + " WHERE 1 LIMIT 1");
				list = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("topic")
						+ " ORDER BY lastpost DESC LIMIT " + start + "," + perPage);
			}
			Map<String, String> actives = new HashMap<String, String>();
			actives.put(view, " class=\"active\"");
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			if (count > 0) {
				for (Map<String, Object> value : list) {
					value.put("pic", Common.pic_get(sConfig, (String) value.get("pic"), (Integer) value
							.get("thumb"), (Integer) value.get("remote"), true));
					value.put("lastpost", Common.sgmdate(request, "MM-dd HH:mm", (Integer) value
							.get("lastpost")));
					value.put("dateline", Common.sgmdate(request, "MM-dd HH:mm", (Integer) value
							.get("dateline")));
					value.put("endtime", Common.empty(value.get("endtime")) ? null : Common.sgmdate(request,
							"MM-dd HH:mm", (Integer) value.get("endtime")));
					try {
						value.put("message", Common.getStr((String) value.get("message"), 200, false, false,
								false, 0, -1, request, response));
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
				}
				request.setAttribute("list", list);
				int maxPage = (Integer) sConfig.get("maxpage");
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
						"space.jsp?do=topic", null, null));
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("actives", actives);
			request.setAttribute("allowtopic", Common.checkPerm(request, response, "allowtopic"));
			request.setAttribute("tpl_css", "event");
			request.setAttribute("navtitle", "热闹 - ");
			return include(request, response, sConfig, sGlobal, "space_topic_list.jsp");
		} else {
			Map<String, Object> topic = Common.getTopic(request, topicId);
			if (topic.isEmpty()) {
				return showMessage(request, response, "topic_no_found");
			}
			boolean manageTopic = Common.checkPerm(request, response, "managetopic");
			if (topic.get("uid").equals(sGlobal.get("supe_uid"))) {
				manageTopic = true;
			}
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			Common.realname_set(sGlobal, sConfig, sNames, (Integer) topic.get("uid"), (String) topic
					.get("username"), "", 0);
			Map<String, Integer> perPages = new HashMap<String, Integer>();
			start = 0;
			String view = request.getParameter("view");
			String[] joinType = (String[]) topic.get("jointype");
			if (!Common.empty(view) && Common.in_array(joinType, view)) {
				perPages.put(view, 30);
				start = (page - 1) * perPages.get(view);
			} else if ("space".equals(view)) {
				perPages.put("space", 20);
				start = (page - 1) * perPages.get(view);
			} else {
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("view", new String[] {"index"});
				view = "index";
				perPages.put("blog", 10);
				perPages.put("pic", 15);
				perPages.put("thread", 10);
				perPages.put("poll", 10);
				perPages.put("event", 10);
				perPages.put("share", 10);
				perPages.put("space", 21);
			}
			boolean isQuery = false;
			int count = 0;
			if (perPages.get("blog") != null && Common.in_array(joinType, "blog")) {
				if ("blog".equals(view)) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("blog") + " WHERE topicid='" + topicId
							+ "' LIMIT 1");
				} else {
					isQuery = true;
				}
				if (count > 0 || isQuery) {
					List<Map<String, Object>> blogList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("blog") + " WHERE topicid='" + topicId
							+ "' ORDER BY dateline DESC LIMIT " + start + "," + perPages.get("blog"));
					for (Map<String, Object> value : blogList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					request.setAttribute("bloglist", blogList);
				}
			}
			if (perPages.get("pic") != null && Common.in_array(joinType, "pic")) {
				if ("pic".equals(view)) {
					count = dataBaseService
							.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("pic")
									+ " WHERE topicid='" + topicId + "' LIMIT 1");
				} else {
					isQuery = true;
				}
				if (count > 0 || isQuery) {
					List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("pic") + " WHERE topicid='" + topicId
							+ "' ORDER BY dateline DESC LIMIT " + start + "," + perPages.get("pic"));
					for (Map<String, Object> value : picList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"),
								(Integer) value.get("thumb"), (Integer) value.get("remote"), true));
					}
					request.setAttribute("piclist", picList);
				}
			}
			if (perPages.get("thread") != null && Common.in_array(joinType, "thread")) {
				if ("thread".equals(view)) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("thread") + " WHERE topicid='" + topicId
							+ "' LIMIT 1");
				} else {
					isQuery = true;
				}
				if (count > 0 || isQuery) {
					List<Map<String, Object>> threadList = dataBaseService
							.executeQuery("SELECT t.*, m.tagname FROM "
									+ JavaCenterHome.getTableName("thread") + " t LEFT JOIN "
									+ JavaCenterHome.getTableName("mtag")
									+ " m ON m.tagid=t.tagid WHERE t.topicid='" + topicId
									+ "' ORDER BY t.dateline DESC LIMIT " + start + ","
									+ perPages.get("thread"));
					for (Map<String, Object> value : threadList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					request.setAttribute("threadlist", threadList);
				}
			}
			if (perPages.get("poll") != null && Common.in_array(joinType, "poll")) {
				if ("poll".equals(view)) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("poll") + " WHERE topicid='" + topicId
							+ "' LIMIT 1");
				} else {
					isQuery = true;
				}
				if (count > 0 || isQuery) {
					List<Map<String, Object>> pollList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("poll") + " WHERE topicid='" + topicId
							+ "' ORDER BY dateline DESC LIMIT " + start + "," + perPages.get("poll"));
					for (Map<String, Object> value : pollList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					request.setAttribute("polllist", pollList);
				}
			}
			if (perPages.get("event") != null && Common.in_array(joinType, "event")) {
				if ("event".equals(view)) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("event") + " WHERE topicid='" + topicId
							+ "' LIMIT 1");
				} else {
					isQuery = true;
				}
				if (count > 0 || isQuery) {
					List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("event") + " WHERE topicid='" + topicId
							+ "' ORDER BY dateline DESC LIMIT " + start + "," + perPages.get("event"));
					for (Map<String, Object> value : eventList) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					request.setAttribute("eventlist", eventList);
				}
			}
			if (perPages.get("share") != null && Common.in_array(joinType, "share")) {
				if ("share".equals(view)) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("share") + " WHERE topicid='" + topicId
							+ "' LIMIT 1");
				} else {
					isQuery = true;
				}
				if (count > 0 || isQuery) {
					List<Map<String, Object>> shareList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("share") + " WHERE topicid='" + topicId
							+ "' ORDER BY dateline DESC LIMIT " + start + "," + perPages.get("share"));
					for (Map<String, Object> value : shareList) {
						Common.mkShare(value);
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					request.setAttribute("sharelist", shareList);
				}
			}
			if (perPages.get("space") != null) {
				List<Map<String, Object>> spaceList = null;
				if ("space".equals(view)) {
					count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("topicuser") + " WHERE topicid='" + topicId
							+ "' LIMIT 1");
					spaceList = dataBaseService.executeQuery("SELECT s.* FROM "
							+ JavaCenterHome.getTableName("topicuser") + " tu LEFT JOIN "
							+ JavaCenterHome.getTableName("space") + " s ON s.uid=tu.uid WHERE tu.topicid='"
							+ topicId + "' ORDER BY tu.dateline DESC LIMIT " + start + ","
							+ perPages.get("space"));
				} else {
					spaceList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("topicuser") + " WHERE topicid='" + topicId
							+ "' ORDER BY dateline DESC LIMIT " + start + "," + perPages.get("space"));
				}
				for (Map<String, Object> value : spaceList) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), (String) value.get("name"), value.get("namestatus") == null ? 0
							: (Integer) value.get("namestatus"));
					value.put("isfriend", (value.get("uid").equals(space.get("uid")) || (!Common.empty(space
							.get("friends")) && Common.in_array((String[]) space.get("friends"), value
							.get("uid")))) ? true : false);
					value.put("gColor", value.get("groupid") == null ? null : Common.getColor(request,
							response, (Integer) value.get("groupid")));
					value.put("gIcon", value.get("groupid") == null ? null : Common.getIcon(request,
							response, (Integer) value.get("groupid")));
				}
				request.setAttribute("list", spaceList);
			}
			if (count > 0) {
				int maxPage = (Integer) sConfig.get("maxpage");
				request.setAttribute("multi", Common.multi(request, count, perPages.get(view), page, maxPage,
						"space.jsp?do=topic&topicid=" + topicId + "&view=" + view, null, null));
			}
			Common.realname_get(sGlobal, sConfig, sNames, space);
			Map<String, String> sub_actives = new HashMap<String, String>();
			sub_actives.put(view, " style=\"font-weight:bold;\"");
			request.setAttribute("sub_actives", sub_actives);
			request.setAttribute("tpl_css", "event");
			request.setAttribute("topic", topic);
			request.setAttribute("topicid", topicId);
			request.setAttribute("managetopic", manageTopic);
			request.setAttribute("navtitle", (Common.empty(topic.get("subject")) ? "" : topic.get("subject")
					+ " - ")
					+ "热闹 - ");
			return include(request, response, sConfig, sGlobal, "space_topic_view.jsp");
		}
	}

	
	public ActionForward space_videophoto(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (Common.empty(sConfig.get("videophoto"))) {
			return showMessage(request, response, "no_open_videophoto");
		}
		if (!cpService.checkVideoPhoto(request, response, "viewphoto", space)) {
			return showMessage(request, response, "no_privilege_videophoto");
		}
		request.setAttribute("videophoto", cpService.getVideoPic((String) space.get("videopic")));
		return include(request, response, sConfig, sGlobal, "space_videophoto.jsp");
	}

	
	public ActionForward space_wall(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int perPage = 20;
		perPage = Common.mobPerpage(request, perPage);
		int page = Common.intval(request.getParameter("page"));
		if (page < 1) {
			page = 1;
		}
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		String theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + request.getAttribute("do");
		int cid = Common.intval(request.getParameter("cid"));
		request.setAttribute("cid", cid);
		String csql = cid > 0 ? "cid='" + cid + "' AND" : "";
		int count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("comment")
				+ " WHERE " + csql + " id='" + space.get("uid") + "' AND idtype='uid'");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		if (count > 0) {
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("comment") + " WHERE " + csql + " id='" + space.get("uid")
					+ "' AND idtype='uid' ORDER BY dateline DESC LIMIT " + start + "," + perPage);
			for (Map<String, Object> value : list) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"), (String) value
						.get("author"), "", 0);
			}
			request.setAttribute("list", list);
		}
		request.setAttribute("multi", Common
				.multi(request, count, perPage, page, maxPage, theURL, null, null));
		Common.realname_get(sGlobal, sConfig, sNames, space);
		if (!(Boolean) space.get("self")) {
			Map TPL = new HashMap();
			TPL.put("spacetitle", "留言");
			TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
					+ "&do=wall&view=me\">TA的所有留言</a>"});
			request.setAttribute("TPL", TPL);
		}

		request.setAttribute("navtitle", "留言 - ");
		return include(request, response, sConfig, sGlobal, "space_wall.jsp");
	}
	
	
	public ActionForward space_addrbook(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		String op = request.getParameter("op");
		
		Map currUser = (Map) sGlobal.get("member");
		int userid = (Integer) sGlobal.get("supe_uid");
		String username = (String) sGlobal.get("supe_username");
		String name = (String) currUser.get("name");
		String friends = Common.implode(currUser.get("friends"), ",");
		boolean friendEmpty = friends.length() == 0 ? true : false;
		
		if("visitor".equals(op) || "visitorlist".equals(op)) {
			boolean visitorSubmit = false;
			try {
				visitorSubmit = submitCheck(request, "visitorsubmit");
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			if(visitorSubmit) {
				int fromUid = (Integer) sGlobal.get("supe_uid");
				String refer = request.getParameter("refer");
				String users = request.getParameter("touid");
				if(users == null) {
					users = request.getParameter("tousers");
					List<Map<String, Object>> friendList = null;
					if("friend".equals(users) && !friendEmpty) {
						friendList = dataBaseService.executeQuery("SELECT sf.uid FROM "+JavaCenterHome.getTableName("spacefield")+" sf WHERE sf.uid IN("+friends+") AND sf.mobile=''");
					} else if("online".equals(users) && !friendEmpty) {
						friendList = dataBaseService.executeQuery("SELECT sf.uid FROM "+JavaCenterHome.getTableName("spacefield")+" sf INNER JOIN "+JavaCenterHome.getTableName("session")+" s ON sf.uid=s.uid WHERE sf.uid IN("+friends+") AND sf.mobile=''");
					} else if("near".equals(users)) {
						friendList = dataBaseService.executeQuery("SELECT sf.uid FROM "+JavaCenterHome.getTableName("spacefield")+" sf INNER JOIN "+JavaCenterHome.getTableName("session")+" s ON sf.uid=s.uid WHERE s.ip='"+Common.getOnlineIP(request, true)+"' AND sf.uid<>'"+userid+"' AND sf.mobile=''");
					} else if("visitor".equals(users)) {
						friendList = dataBaseService.executeQuery("SELECT sf.uid FROM "+JavaCenterHome.getTableName("spacefield")+" sf INNER JOIN "+JavaCenterHome.getTableName("visitor")+" v ON sf.uid=v.vuid WHERE v.uid='"+userid+"' AND sf.mobile=''");
					} else if("trace".equals(users)) {
						friendList = dataBaseService.executeQuery("SELECT sf.uid FROM "+JavaCenterHome.getTableName("spacefield")+" sf INNER JOIN "+JavaCenterHome.getTableName("visitor")+" v ON sf.uid=v.uid WHERE v.vuid='"+userid+"' AND sf.mobile=''");
					}
					if(friendList != null && friendList.size() > 0) {
						String temp = "";
						users = "";
						for(Map<String, Object> of : friendList) {
							users += temp+of.get("uid");
							temp = ",";
						}
					} else {
						users = null;
					}
				}
				if(users == null || users.length() == 0) {
					return showMessage(request, response, "visitor_donot", refer, 0);
				}
				if(name == null || name.length() == 0) {
					name = username;
				}
				String message = name+" 邀请您完善联系方式，方便大家联系!\n"+Common.getSiteUrl(request)+"cp.jsp?ac=profile&op=contact\n(如果不能点击该链接地址，请复制并粘贴到浏览器的地址输入框)";
				try {
					int pid = pmService.jcSendPm(request, response, fromUid, users, "", message, 0, false, true);
					if(pid > 0) {
						return showMessage(request, response, "visitor_success", refer, 0);
					} else {
						return showMessage(request, response, "visitor_failed", refer, 0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			request.setAttribute("op", op);
			return include(request, response, sConfig, sGlobal, "space_addrbook.jsp");
			
		} else if("export".equals(op)) {
			boolean fillSubmit = false;
			boolean exportSubmit = false;
			try {
				if(!(fillSubmit = submitCheck(request, "fillsubmit")))
					exportSubmit = submitCheck(request, "exportsubmit");
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			if(fillSubmit) {
				String mobile = request.getParameter("mobile");
				String refer = request.getParameter("refer");
				if(mobile.length() > 11) {
					mobile = mobile.substring(0, 11);
				}
				if(mobile.trim().length() > 0) {
					dataBaseService.execute("UPDATE "+JavaCenterHome.getTableName("spacefield")+" SET mobile='"+mobile+"' WHERE uid='"+userid+"'");
					request.setAttribute("op", "export");
					return showMessage(request, response, "export_addrbook_fill_success", refer, 0);
				} else {
					request.setAttribute("op", "fill");
					return showMessage(request, response, "export_addrbook_mobile_empty", refer, 0);
				}
			} else if(exportSubmit) {
				String exportusers = request.getParameter("exportusers");
				List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
				String sql = "SELECT s.uid, s.username, s.name, sf.mobile, sf.email FROM "+JavaCenterHome.getTableName("space")+" s LEFT JOIN "+JavaCenterHome.getTableName("spacefield")+" sf ON s.uid=sf.uid";
				if("online".equals(exportusers) && !friendEmpty) {
					userList = dataBaseService.executeQuery(sql+" LEFT JOIN "+JavaCenterHome.getTableName("session")+" se ON s.uid=se.uid WHERE s.uid IN("+friends+")");
				} else if("near".equals(exportusers)) {
					userList = dataBaseService.executeQuery(sql+" LEFT JOIN "+JavaCenterHome.getTableName("session")+" se ON s.uid=se.uid WHERE se.ip='"+Common.getOnlineIP(request, true)+"'");
				} else if("visitor".equals(exportusers)) {
					userList = dataBaseService.executeQuery(sql+" LEFT JOIN "+JavaCenterHome.getTableName("visitor")+" v ON s.uid=v.vuid WHERE v.uid='"+userid+"'");
				} else if("trace".equals(exportusers)) {
					userList = dataBaseService.executeQuery(sql+" LEFT JOIN "+JavaCenterHome.getTableName("visitor")+" v ON s.uid=v.uid WHERE v.vuid='"+userid+"'");
				} else if(!friendEmpty) {
					userList = dataBaseService.executeQuery(sql+" WHERE s.uid IN("+friends+")");
				}
				List<String[]> rows = new ArrayList<String[]>();
				Map<String, String> mobile = new HashMap<String, String>();
				String uids = "", temp = "";
				for(int i = 0; i < userList.size(); i++) {
					uids += temp+userList.get(i).get("uid");
					temp = ",";
				}
				if(uids.length() > 0) {
					List<Map<String, Object>> infoList = dataBaseService.executeQuery("SELECT uid, subtype, friend FROM "+JavaCenterHome.getTableName("spaceinfo")+" WHERE uid IN("+uids+") AND subtype='mobile'");
					for(Map<String, Object> info : infoList) {
						String infoUid = String.valueOf(info.get("uid"));
						String privacy = String.valueOf(info.get("friend"));
						String userFriends = (String) friends;
						if((privacy.equals("3") || (privacy.equals("1") && (userFriends.indexOf(infoUid) == -1)))) {
							mobile.put(infoUid, "保密");
						}
					}
				}
				
				for(int i = 0; i < userList.size(); i++) {
					Map<String, Object> user = userList.get(i);
					String uid = String.valueOf(user.get("uid"));
					String rowMobile = mobile.get(uid) != null ? "保密" : (String) user.get("mobile");
					String[] row = {(String) user.get("username"), (String) user.get("name"), rowMobile , (String) user.get("email")};
					rows.add(row);
				}
				String excelPath = JavaCenterHome.jchRoot+"./data/temp/addrbook.xls";
				File excelFile = writeToExcel(rows, excelPath, "addrbook");
				if(excelFile != null && excelFile.exists()) {
					response.reset();
					response.setHeader("Content-disposition", "attachment; filename=addrbook.xls");
					response.setHeader("Content-Type", "application/octet-stream");
					response.setHeader("Pragma", "no-store");
					response.setHeader("Robots", "none");
					InputStream input = null;
					OutputStream output = null;
					BufferedInputStream buffInput = null;
					BufferedOutputStream buffOutput = null;
					try {
						byte[] bytes = new byte[1024];
						int len = 0;
						input = new FileInputStream(excelFile);
						output = response.getOutputStream();
						buffInput = new BufferedInputStream(input);
						buffOutput = new BufferedOutputStream(output);
						while((len = buffInput.read(bytes)) > 0) {
							buffOutput.write(bytes, 0, len);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						buffInput.close();
						input.close();
						buffOutput.flush();
						buffOutput.close();
						output.close();
						excelFile.delete();
					}
					return null;
				}
			}
			String myMobile = (String) currUser.get("mobile");
			if(myMobile == null || myMobile.length() == 0) {
				op = "fill";
			}
			request.setAttribute("op", op);
			return include(request, response, sConfig, sGlobal, "space_addrbook.jsp");
		}
		
		int count = 0;
		int perPage = 20;
		perPage = Common.mobPerpage(request, perPage);
		int page = Common.intval(request.getParameter("page"));
		if(page < 1)  page = 1;
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) return showMessage(request, response, result);
		
		String addrType = request.getParameter("addr_type");
		String dataType = request.getParameter("data_type");
		
		String sql = null;
		String url = null;
		String permSql = null;
		
		String whereSql = "";
		String rightTbl = "";
		String queryUrl = "";
		
		if(dataType != null) {
			if("visitor".equals(addrType)) {
				rightTbl = " LEFT JOIN "+JavaCenterHome.getTableName("spacefield")+" sf ON main.vuid=sf.uid";
			} else {
				rightTbl = " LEFT JOIN "+JavaCenterHome.getTableName("spacefield")+" sf ON main.uid=sf.uid";
			}
			if("mobile".equals(dataType)) {
				whereSql = " AND sf.mobile<>''";
				queryUrl = "&data_type=mobile";
			}
		}
		
		List<Map<String, Object>> addrList = null;
		
		if("online".equals(addrType) && !friendEmpty) {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("session")+" main"+rightTbl+" WHERE main.uid IN("+friends+")"+whereSql);
			url = "space.jsp?uid="+userid+"&do=addrbook&addr_type=online"+queryUrl;
			sql = "SELECT s.uid, s.username, s.name, sf.sex, sf.email, sf.mobile, sf.birthprovince, sf.birthcity, sf.resideprovince, sf.residecity, sf.marry"+
					" FROM "+JavaCenterHome.getTableName("session")+" main"+
					" LEFT JOIN "+JavaCenterHome.getTableName("space")+" s ON main.uid=s.uid"+
					" LEFT JOIN "+JavaCenterHome.getTableName("spacefield")+" sf ON main.uid=sf.uid"+
					" WHERE main.uid IN("+friends+")"+whereSql+
					" ORDER BY main.lastactivity DESC"+
					" LIMIT "+start+", "+perPage;
			addrList = dataBaseService.executeQuery(sql);
		} else if("near".equals(addrType)) {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("session")+" main"+rightTbl+" WHERE main.ip='"+Common.getOnlineIP(request, true)+"' AND main.uid<>'"+userid+"'"+whereSql);
			url = "space.jsp?uid="+userid+"&do=addrbook&addr_type=near"+queryUrl;
			sql = "SELECT s.uid, s.username, s.name, sf.sex, sf.email, sf.mobile, sf.birthprovince, sf.birthcity, sf.resideprovince, sf.residecity, sf.marry"+
					" FROM "+JavaCenterHome.getTableName("session")+" main"+
					" LEFT JOIN "+JavaCenterHome.getTableName("space")+" s ON main.uid=s.uid"+
					" LEFT JOIN "+JavaCenterHome.getTableName("spacefield")+" sf ON main.uid=sf.uid"+
					" WHERE main.ip='"+Common.getOnlineIP(request, true)+"' AND main.uid<>'"+userid+"'"+whereSql+
					" ORDER BY main.lastactivity DESC"+
					" LIMIT "+start+", "+perPage;
			addrList = dataBaseService.executeQuery(sql);
		} else if("visitor".equals(addrType)) {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("visitor")+" main"+rightTbl+" WHERE main.uid='"+userid+"'"+whereSql);
			url = "space.jsp?uid="+userid+"&do=addrbook&addr_type=visitor"+queryUrl;
			sql = "SELECT s.uid, s.username, s.name, sf.sex, sf.email, sf.mobile, sf.birthprovince, sf.birthcity, sf.resideprovince, sf.residecity, sf.marry"+
					" FROM "+JavaCenterHome.getTableName("visitor")+" main"+
					" LEFT JOIN "+JavaCenterHome.getTableName("space")+" s ON main.vuid=s.uid"+
					" LEFT JOIN "+JavaCenterHome.getTableName("spacefield")+" sf ON main.vuid=sf.uid"+
					" WHERE main.uid='"+userid+"'"+whereSql+
					" ORDER BY main.dateline DESC"+
					" LIMIT "+start+", "+perPage;
			addrList = dataBaseService.executeQuery(sql);
		} else if("trace".equals(addrType)) {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("visitor")+" main"+rightTbl+" WHERE main.vuid='"+userid+"'"+whereSql);
			url = "space.jsp?uid="+userid+"&do=addrbook&addr_type=trace"+queryUrl;
			sql = "SELECT s.uid, s.username, s.name, sf.sex, sf.email, sf.mobile, sf.birthprovince, sf.birthcity, sf.resideprovince, sf.residecity, sf.marry"+
					" FROM "+JavaCenterHome.getTableName("visitor")+" main"+
					" LEFT JOIN "+JavaCenterHome.getTableName("space")+" s ON main.uid=s.uid"+
					" LEFT JOIN "+JavaCenterHome.getTableName("spacefield")+" sf ON main.uid=sf.uid"+
					" WHERE main.vuid='"+userid+"'"+whereSql+
					" ORDER BY main.dateline DESC"+
					" LIMIT "+start+", "+perPage;
			addrList = dataBaseService.executeQuery(sql);
		} else if(!friendEmpty) {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("friend")+" main"+rightTbl+" WHERE main.fuid='"+userid+"'"+whereSql);
			url = "space.jsp?uid="+userid+"&do=addrbook&addr_type=friend"+queryUrl;
			sql = "SELECT s.uid, s.username, s.name, sf.sex, sf.email, sf.mobile, sf.birthprovince, sf.birthcity, sf.resideprovince, sf.residecity, sf.marry"+
					" FROM "+JavaCenterHome.getTableName("space")+" s"+
					" LEFT JOIN "+JavaCenterHome.getTableName("spacefield")+" sf ON s.uid=sf.uid"+
					" WHERE s.uid IN("+friends+")"+whereSql+
					" ORDER BY s.uid"+
					" LIMIT "+start+", "+perPage;
			addrType = "friend";
			addrList = dataBaseService.executeQuery(sql);
		}
		
		String multi = Common.multi(request, count, perPage, page, maxPage, url, null, null);
		
		int addrSize = addrList != null ? addrList.size() : 0;
		Object[] uids = new Object[addrSize];
		for(int i = 0; i < addrSize; i++) {
			Map addr = addrList.get(i);
			addr.put("uid", String.valueOf(addr.get("uid")));
			uids[i] = addr.get("uid");
		}
		
		Map<String, Object> marry = new HashMap<String, Object>();
		Map<String, Object> email = new HashMap<String, Object>();
		Map<String, Object> mobile = new HashMap<String, Object>();
		Map<String, Object> birthcity = new HashMap<String, Object>();
		Map<String, Object> residecity = new HashMap<String, Object>();
		
		if(uids.length != 0) {
			List<Map<String, Object>> infoList = dataBaseService.executeQuery("SELECT uid, subtype, friend FROM "+JavaCenterHome.getTableName("spaceinfo")+" WHERE uid IN("+Common.implode(uids, ",")+") AND subtype IN('marry', 'mobile', 'email', 'birthcity', 'residecity')");
			for(Map<String, Object> info : infoList) {
				String subType = (String) info.get("subtype");
				String infoUid = String.valueOf(info.get("uid"));
				String privacy = String.valueOf(info.get("friend"));
				String userFriends = (String) friends;
				if("marry".equals(subType) && (privacy.equals("3") || (privacy.equals("1") && (userFriends.indexOf(infoUid) == -1)))) {
					marry.put(infoUid, "保密");
				} else if("email".equals(subType) && (privacy.equals("3") || (privacy.equals("1") && (userFriends.indexOf(infoUid) == -1)))) {
					email.put(infoUid, "保密");
				} else if("mobile".equals(subType) && (privacy.equals("3") || (privacy.equals("1") && (userFriends.indexOf(infoUid) == -1)))) {
					mobile.put(infoUid, "保密");
				} else if("birthcity".equals(subType) && (privacy.equals("3") || (privacy.equals("1") && (userFriends.indexOf(infoUid) == -1)))) {
					birthcity.put(infoUid, "保密");
				} else if("residecity".equals(subType) && (privacy.equals("3") || (privacy.equals("1") && (userFriends.indexOf(infoUid) == -1)))) {
					residecity.put(infoUid, "保密");
				}
			}
		}
		request.setAttribute("marry", marry);
		request.setAttribute("email", email);
		request.setAttribute("mobile", mobile);
		request.setAttribute("birthcity", birthcity);
		request.setAttribute("residecity", residecity);
		
		request.setAttribute("active_"+addrType, " class=\"current\"");
		request.setAttribute("addrlist", addrList);
		request.setAttribute("addrtype", addrType);
		request.setAttribute("multi", multi);
		request.setAttribute("refer", "space.jsp?do=addrbook");
		request.setAttribute("tpl_css", "addrbook");
		return include(request, response, sConfig, sGlobal, "space_addrbook.jsp");
	}

	
	public ActionForward space_gift(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		
		String view = request.getParameter("view");
		int perPage = 18;
		int count = 0;
		if("list".equals(view)) {
			perPage = 10;
		}
		perPage = Common.mobPerpage(request, perPage);
		int page = Common.intval(request.getParameter("page"));
		if(page < 1) {
			page = 1;
		}
		int start = (page - 1) * perPage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		
		if("setting".equals(view)) {
			if(sGlobal.get("member")!=null){
				request.setAttribute("showgiftlink", ((Map) sGlobal.get("member")).get("showgiftlink"));
			}
		} else if("list".equals(view)){
			boolean isAjaxPost = Common.empty(sGlobal.get("inajax")) ? false : true;
			String reqType = request.getParameter("reqtype");
			String giftType = request.getParameter("type");
			if(giftType == null) {
				giftType = "defGift";
			}
			if(isAjaxPost && reqType != null) {
				if(reqType.equals("tips")) {
					int advGiftCount = (Integer) ((Map) sGlobal.get("member")).get("advgiftcount"); 
					request.setAttribute("advgiftcount", advGiftCount);
				} else if(reqType.equals("balance")){
					request.setAttribute("balance", 0);
				} else if(reqType.equals("feescate")) {
					List<Map<String, Object>> feeCategories = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("gifttype")+" WHERE fee=1 ORDER BY `order` ASC");
					request.setAttribute("feecatelist", feeCategories);
					request.setAttribute("gifttype", giftType);
				}
				return include(request, response, sConfig, sGlobal, "space_gift_list.jsp");
			}
			if(isAjaxPost) {
				List<Map<String, Object>> giftList;
				List<String> countTotal;
				
				if(giftType.equals("feeGift") || giftType.equals("feesAll")) {
					giftList = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("gift")+" g INNER JOIN "+JavaCenterHome.getTableName("gifttype")+" gt ON g.typeid=gt.typeid WHERE g.price>0 LIMIT "+start+", "+perPage);
					countTotal = dataBaseService.executeQuery("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("gift")+" WHERE price>0", 1);
				} else {
					giftList = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("gift")+" g INNER JOIN "+JavaCenterHome.getTableName("gifttype")+" gt ON g.typeid=gt.typeid WHERE g.typeid='"+giftType+"' LIMIT "+start+", "+perPage);
					countTotal = dataBaseService.executeQuery("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("gift")+" WHERE typeid='"+giftType+"'", 1);
				}
				count = Common.intval(countTotal.get(0));
				String multi = Common.multi(request, count, perPage, page, maxPage, "space.jsp?do=gift&view=list&type="+giftType, "giftData", null);
				
				request.setAttribute("multi", multi);		
				request.setAttribute("giftlist", giftList);
				return include(request, response, sConfig, sGlobal, "space_gift_list.jsp");
			}
		}else if("sent".equals(view)) {
			List<String> countTotal = dataBaseService.executeQuery("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("giftsent")+" WHERE senderid='"+sGlobal.get("supe_uid")+"'", 1);
			count = Common.intval(countTotal.get(0));
			List<Map<String, Object>> giveList = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("giftsent")+" gs INNER JOIN "+JavaCenterHome.getTableName("gift")+" g ON gs.giftid=g.giftid WHERE gs.senderid='"+sGlobal.get("supe_uid")+"' ORDER BY sendtime DESC LIMIT "+start+","+perPage);
			
			request.setAttribute("giftnum", count);
			request.setAttribute("giftlist", giveList);
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, "space.jsp?uid="+sGlobal.get("supe_uid")+"&do=gift&view=sent", null, null));
			
		}else{
			view = "got";
			Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
			List<String> countTotal = dataBaseService.executeQuery("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("giftreceived")+" WHERE receiverid='"+space.get("uid")+"'", 1);
			count = Common.intval(countTotal.get(0));
			List<String> countFee = dataBaseService.executeQuery("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("giftreceived")+" WHERE receiverid='"+space.get("uid")+"' AND fee='1'", 1);
			int feeNum = Common.intval(countFee.get(0));
			List<Map<String, Object>> receiveList = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("giftreceived")+" gr INNER JOIN "+JavaCenterHome.getTableName("gift")+" g ON gr.giftid=g.giftid WHERE gr.receiverid='"+space.get("uid")+"' ORDER BY gr.fee DESC, gr.receipttime DESC LIMIT "+start+","+perPage);
			if((Boolean) space.get("self")) {
				dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("giftreceived")+" SET status='0' WHERE status='1' AND receiverid='"+space.get("uid")+"'");
			}

			request.setAttribute("feenum", feeNum);
			request.setAttribute("giftnum", count);
			request.setAttribute("giftlist", receiveList);

			if(Common.empty(sGlobal.get("inajax")) == false) {
				String theUrl = "space.jsp?uid="+space.get("uid")+"&do="+request.getAttribute("do")+"&view=me";
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theUrl, null, null));
				return include(request, response, sConfig, sGlobal, "space_gift_list.jsp");
			} else {
				String theUrl = "space.jsp?uid="+sGlobal.get("supe_uid")+"&do=gift&view=got";
				request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage, theUrl, null, null));
			}
		}
		request.setAttribute("active_"+view, " class=\"active\"");
		return include(request, response, sConfig, sGlobal, "space_gift.jsp");
	}
	private void setPrivacy(Map<String, Object> sGlobal,Map<String, Object> space) {
		space.put("isfriend", space.get("self"));
		if (Common.in_array((String[]) space.get("friends"), sGlobal.get("supe_uid"))) {
			space.put("isfriend", true);
		}
	}
	private boolean ckIconUid(Map feed, Map space) {
		if (!Common.empty(space.get("filter_icon"))) {
			String key = feed.get("icon") + "|0";
			if (((Set) space.get("filter_icon")).contains(key)) {
				return false;
			} else {
				key = feed.get("icon") + "|" + feed.get("uid");
				if (((Set) space.get("filter_icon")).contains(key)) {
					return false;
				}
			}
		}
		return true;
	}

	
	private Object ckFriendAlbum(Map<String, Object> album, Map<String, Object> sGlobal,
			Map<String, Object> sConfig, Map<String, Object> space, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.ckFriend(sGlobal, space, (Integer) album.get("uid"), (Integer) album.get("friend"),
				(String) album.get("target_ids"))) {
			setPrivacy(sGlobal, space);
			return include(request, response, sConfig, sGlobal, "space_privacy.jsp");
		} else if (!(Boolean) space.get("self") && (Integer) album.get("friend") == 4) {
			String cookieName = "view_pwd_album_" + album.get("albumid");
			Map<String, String> sCookie = (Map<String, String>) request.getAttribute("sCookie");
			String cookieValue = Common.empty(sCookie.get(cookieName)) ? "" : sCookie.get(cookieName);
			if (!cookieValue.equals(Common.md5(Common.md5((String) album.get("password"))))) {
				request.setAttribute("invalue", album);
				return include(request, response, sConfig, sGlobal, "do_inputpwd.jsp");
			}
		}
		return false;
	}
	private String myShowGift(Map<String, Object> sGlobal, Map<String, Object> sConfig) {
		Map myUserApp = (Map) sGlobal.get("my_userapp");
		if ((Integer) sConfig.get("my_showgift") > 0 && !Common.empty(myUserApp)
				&& !Common.empty(myUserApp.get("gift_appid"))) {
			return "<script language=\"javascript\" type=\"text/javascript\" src=\"http://gift.jsprun-apps.com/recommend.js\"></script>";
		} else {
			return null;
		}
	}
	private void showCredit(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> sConfig, Map<String, Object> space) {
		Map whereArr = new HashMap();
		whereArr.put("uid", space.get("uid"));
		String showCredit = Common.getCount("show", whereArr, "credit");
		if (Common.intval(showCredit) > 0) {
			if ("1".equals(showCredit)) {
				cpService.addNotification(request, sGlobal, sConfig, (Integer) space.get("uid"), "show",
						Common.getMessage(request, "cp_note_show_out"), false);
			}
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("show")
					+ " SET credit=credit-1 WHERE uid='" + space.get("uid") + "' AND credit>0");
		}
	}
	
	  
	public File writeToExcel(List<String[]> rows, String excelPath, String sheetName) {
		WritableWorkbook workbook = null;
		WritableSheet sheet = null;
		File excelFile = null;
		try {
			excelFile = new File(excelPath);
			workbook = Workbook.createWorkbook(excelFile);
	    	sheet = workbook.createSheet(sheetName, 0);
	    	for(int i = 0; i < rows.size(); i++) {
	    		String[] row = rows.get(i);
		    	for(int j = 0; j < row.length; j++) {
		    		String value = row[j];
		    		Label label = new Label(j, i, value);
		    		sheet.addCell(label);
		    	}
	    	}
	    	if(workbook != null) workbook.write();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(workbook != null) workbook.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return excelFile;
	}

	
	public ActionForward space_home(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (Common.empty(sConfig.get("showallfriendnum")) || (Integer) sConfig.get("showallfriendnum") < 1) {
			sConfig.put("showallfriendnum", 10);
		}
		if (Common.empty(sConfig.get("feedhotday"))) {
			sConfig.put("feedhotday", 2);
		}
		boolean isNewer = space.get("friendnum") == null
				|| (Integer) space.get("friendnum") < (Integer) sConfig.get("showallfriendnum") ? true
				: false;
		Map<String, String[]> paramMap = request.getParameterMap();
		String view = "all";
		paramMap.put("view", new String[]{view});
		int feedMaxNum = (Integer) sConfig.get("feedmaxnum");
		int perPage = feedMaxNum < 50 ? 50 : feedMaxNum;
		perPage = Common.mobPerpage(request, perPage);
	
		int start = Common.intval(request.getParameter("start"));
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perPage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		sGlobal.put("today", Common.strToTime(Common.sgmdate(request, "yyyy-MM-dd", 0), Common.getTimeOffset(
				sGlobal, sConfig)));
		int feedHotMin = (Integer) sConfig.get("feedhotmin");
		int minHot = feedHotMin < 1 ? 3 : feedHotMin;
		sGlobal.put("gift_appid", "1027468");
	
		String action = (String) request.getAttribute("do");
		Map TPL = request.getAttribute("TPL") != null ? (Map) request.getAttribute("TPL") : new HashMap();
		String whereSQL = "1";
		String orderSQL = "dateline DESC";
		String theURL = "space.jsp?uid=" + space.get("uid") + "&do=" + action + "&view=all";
		int appId = Common.intval(request.getParameter("appid"));
		if (appId > 0) {
			whereSQL += " AND appid='" + appId + "'";
		}
		String icon = Common.trim(request.getParameter("icon"));
		if (!Common.empty(icon)) {
			whereSQL += " AND icon='" + icon + "'";
		}
		String filter = Common.trim(request.getParameter("filter"));
		if ("site".equals(filter)) {
			whereSQL += " AND appid>0";
		} else if ("myapp".equals(filter)) {
			whereSQL += " AND appid='0'";
		}
	
		int count = 0;
		List<Map<String, Object>> feedList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("feed") +"	WHERE " + whereSQL + "	ORDER BY "
				+ orderSQL + "	LIMIT " + start + "," + perPage);
		LinkedHashMap<Object, Map> feed_list = new LinkedHashMap<Object, Map>();
		LinkedHashMap<Object, LinkedHashMap> appfeedList = new LinkedHashMap<Object, LinkedHashMap>();
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		String[] hidden_icons = null;
		String feedHiddenIcon = (String) sConfig.get("feedhiddenicon");
		if (!Common.empty(feedHiddenIcon)) {
			sConfig.put("feedhiddenicon", (feedHiddenIcon = feedHiddenIcon.replace(" ", "")));
			hidden_icons = feedHiddenIcon.split(",");
		}
		Map privacy = (Map) space.get("privacy");
		Map filterIcon = privacy == null ? null : (Map) privacy.get("filter_icon");
		space.put("filter_icon", Common.empty(filterIcon) ? new HashSet() : filterIcon.keySet());
		LinkedHashMap hashData = new LinkedHashMap();
		LinkedHashMap icon_num = new LinkedHashMap();
		boolean isMyApp = false;
		LinkedHashMap hashDataMap = null;
		int filterCount = 0;
		List<Map<String, Object>> filter_list = new ArrayList<Map<String, Object>>();
		LinkedHashMap hiddenfeed_num = new LinkedHashMap();
		LinkedHashMap<Object, List<Map>> hiddenfeed_list = new LinkedHashMap<Object, List<Map>>();
		for (Map<String, Object> value : feedList) {
			LinkedHashMap tempMap = (LinkedHashMap) feed_list.get(value.get("hash_data"));
			if (Common.empty(tempMap) || Common.empty(tempMap.get(value.get("uid")))) {
				if (Common.ckFriend(sGlobal, space, (Integer) value.get("uid"), (Integer) value
						.get("friend"), (String) value.get("target_ids"))) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
							(String) value.get("username"), "", 0);
					if (ckIconUid(value, space)) {
						isMyApp = Common.isNumeric(value.get("icon")) ? true : false;
						if (!Common.empty(sConfig.get("my_showgift"))
								&& value.get("icon").toString().equals(sGlobal.get("gift_appid"))) {
							isMyApp = false;
						}
						if (((isMyApp && Common.in_array(hidden_icons, "myop")) || Common.in_array(
								hidden_icons, value.get("icon")))
								&& !Common.empty(icon_num.get(value.get("icon")))) {
							hiddenfeed_num.put(value.get("icon"),
									hiddenfeed_num.get(value.get("icon")) == null ? 1
											: ((Integer) hiddenfeed_num.get(value.get("icon"))) + 1);
							List tempList = hiddenfeed_list.get(value.get("icon"));
							if (tempList == null) {
								tempList = new ArrayList();
							}
							Common.mkFeed(sNames, sConfig, request, value, null);
							tempList.add(value);
							hiddenfeed_list.put(value.get("icon"), tempList);
						} else {
							if (isMyApp) {
								if (appfeedList.get(value.get("hash_data")) == null) {
									hashDataMap = new LinkedHashMap();
								}
								hashDataMap.put(value.get("uid"), value);
								appfeedList.put(value.get("hash_data"), hashDataMap);
							} else {
								if ((hashDataMap = (LinkedHashMap) feed_list.get(value.get("hash_data"))) == null) {
									hashDataMap = new LinkedHashMap();
								}
								hashDataMap.put(value.get("uid"), value);
								feed_list.put(value.get("hash_data"), hashDataMap);
							}
						}
						icon_num.put(value.get("icon"), icon_num.get(value.get("icon")) == null ? 1
								: ((Integer) icon_num.get(value.get("icon"))) + 1);
					} else {
						filterCount++;
						value = Common.mkFeed(sNames, sConfig, request, value, null);
						filter_list.add(value);
					}
				}
			}
			count++;
		}
		request.setAttribute("hiddenfeed_list", hiddenfeed_list);
		request.setAttribute("hiddenfeed_num", hiddenfeed_num);
		request.setAttribute("filtercount", filterCount);
		request.setAttribute("filter_list", filter_list);
		
		LinkedHashMap<Object, Map<String, Object>> hotList = new LinkedHashMap<Object, Map<String, Object>>();
		if ((Boolean) space.get("self") && Common.empty(start)) {
			space.put("pmnum", ((Map) sGlobal.get("member")) == null ? 0 : ((Map) sGlobal.get("member"))
					.get("newpm"));
			if (Common.checkPerm(request, response, "managereport")) {
				space.put("reportnum", dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("report") + " WHERE new='1'"));
			}
			if (Common.checkPerm(request, response, "manageevent")) {
				space.put("eventverifynum", dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("event") + " WHERE grade='0'"));
			}
			if (!Common.empty(sConfig.get("realname")) && Common.checkPerm(request, response, "managename")) {
				space.put("namestatusnum", dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("space") + " WHERE namestatus='0' AND name!=''"));
			}
			List olUids = new ArrayList();
			if ((Integer) sConfig.get("newspacenum") > 0) {
				List<Map<String, Object>> newSpaceList = (List<Map<String, Object>>) Serializer
						.unserialize(Common.getData("newspacelist"));
				int uid = 0;
				if (newSpaceList != null) {
					for (Map<String, Object> value : newSpaceList) {
						uid = Integer.valueOf(value.get("uid").toString());
						value.put("uid", uid);
						olUids.add(uid);
						Common.realname_set(sGlobal, sConfig, sNames, uid, (String) value.get("username"),
								(String) value.get("name"), Integer.valueOf(value.get("namestatus").toString()));
					}
					request.setAttribute("newspacelist", newSpaceList);
				}
			}
			List<Map<String, Object>> visitors = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("visitor") + " WHERE uid='" + space.get("uid")
					+ "' ORDER BY dateline DESC LIMIT 0,12");
			LinkedHashMap<Object, Map> visitorList = new LinkedHashMap<Object, Map>();
			for (Map<String, Object> value : visitors) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("vuid"), (String) value
						.get("vusername"), "", 0);
				visitorList.put(value.get("vuid"), value);
				olUids.add(value.get("vuid"));
			}
			Map ols = new LinkedHashMap();
			request.setAttribute("ols", ols);
			if (olUids.size() > 0) {
				List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("session") + " WHERE uid IN ("
						+ Common.sImplode(olUids) + ")");
				for (Map<String, Object> value : sessionList) {
					if ((Integer) value.get("magichidden") == 0) {
						ols.put(value.get("uid"), 1);
					} else if (visitorList.get(value.get("uid")) != null) {
						visitorList.remove(value.get("uid"));
					}
				}
			}
			request.setAttribute("visitorlist", visitorList);
	
			LinkedHashMap olUidsMap = new LinkedHashMap();
			List<Map<String, Object>> olFriendList = new ArrayList<Map<String, Object>>();
			int olfCount = 0;
			if (!Common.empty(space.get("feedfriend"))) {
				List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("session") + " WHERE uid IN ("
						+ space.get("feedfriend") + ") ORDER BY lastactivity DESC LIMIT 0,15");
				for (Map<String, Object> value : sessionList) {
					if ((Integer) value.get("magichidden") == 0) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						olFriendList.add(value);
						ols.put(value.get("uid"), 1);
						olUidsMap.put(value.get("uid"), value.get("uid"));
						olfCount++;
					}
				}
			}
			if (olfCount < 15) {
				List<Map<String, Object>> friendList = dataBaseService
						.executeQuery("SELECT fuid AS uid, fusername AS username, num FROM "
								+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + space.get("uid")
								+ "' AND status='1' ORDER BY num DESC, dateline DESC LIMIT 0,30");
				for (Map<String, Object> value : friendList) {
					if (Common.empty(olUidsMap.get(value.get("uid")))) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						olFriendList.add(value);
						olfCount++;
						if (olfCount == 15) {
							break;
						}
					}
				}
			}
			request.setAttribute("olfriendlist", olFriendList);
			request.setAttribute("task", spaceService.getTask(request, response));
			if (!Common.empty(space.get("feedfriend"))) {
				Integer timesTamp = (Integer) sGlobal.get("timestamp");
				String[] ss = Common.sgmdate(request, "M-d", timesTamp - 3600 * 24 * 3).split("-");
				int s_month = Integer.valueOf(ss[0]);
				int s_day = Integer.valueOf(ss[1]);
				String[] ns = Common.sgmdate(request, "M-d", timesTamp).split("-");
				int n_month = Integer.valueOf(ns[0]);
				int n_day = Integer.valueOf(ns[1]);
				String[] es = Common.sgmdate(request, "M-d", timesTamp + 3600 * 24 * 7).split("-");
				int e_month = Integer.valueOf(es[0]);
				int e_day = Integer.valueOf(es[1]);
				if (e_month == s_month) {
					whereSQL = "sf.birthmonth='" + s_month + "' AND sf.birthday>='" + s_day
							+ "' AND sf.birthday<='" + e_day + "'";
				} else {
					whereSQL = "(sf.birthmonth='" + s_month + "' AND sf.birthday>='" + s_day
							+ "') OR (sf.birthmonth='" + e_month + "' AND sf.birthday<='" + e_day
							+ "' AND sf.birthday>'0')";
				}
				List<Map<String, Object>> spaceField = dataBaseService
						.executeQuery("SELECT s.uid,s.username,s.name,s.namestatus,s.groupid,sf.birthyear,sf.birthmonth,sf.birthday FROM "
								+ JavaCenterHome.getTableName("spacefield")
								+ " sf LEFT JOIN "
								+ JavaCenterHome.getTableName("space")
								+ " s ON s.uid=sf.uid WHERE (sf.uid IN ("
								+ space.get("feedfriend")
								+ ")) AND (" + whereSQL + ")");
				String key = null;
				List tempList = null;
				TreeMap birthList = new TreeMap();
				for (Map<String, Object> value : spaceField) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), (String) value.get("name"), (Integer) value.get("namestatus"));
					key = Common.sprintf("00", (Integer) value.get("birthmonth"))
							+ Common.sprintf("00", (Integer) value.get("birthday"));
					if ((Integer) value.get("birthmonth") == n_month
							&& (Integer) value.get("birthday") == n_day) {
						value.put("birth", "今天");
					} else {
						value.put("birth", value.get("birthmonth") + "-" + value.get("birthday"));
					}
					if ((tempList = (List) birthList.get(key)) == null) {
						tempList = new ArrayList<Map<String, Object>>(spaceField.size());
					}
					tempList.add(value);
					birthList.put(key, tempList);
				}
				request.setAttribute("birthList", birthList);
			}
			space.put("star", Common.getStar(sConfig, space.get("experience") == null ? 0 : (Integer) space
					.get("experience")));
			space.put("domainurl", Common.spaceDomain(request, space, sConfig));
			if(Common.empty(icon)){
				LinkedHashMap<Object, Map<String, Object>> hotListAll = new LinkedHashMap<Object, Map<String, Object>>();
				if ((Integer) sConfig.get("feedhotnum") > 0 && view.equals("all")) {
					double hotStartTime = (Integer) sGlobal.get("timestamp") - Double.parseDouble(sConfig.get("feedhotday").toString())
							* 3600 * 24;
					List<Map<String, Object>> feeds = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("feed") + " USE INDEX(hot) WHERE dateline>='"
							+ hotStartTime + "' ORDER BY hot DESC LIMIT 0,10");
					for (Map<String, Object> value : feeds) {
						if ((Integer) value.get("hot") > 0
								&& Common.ckFriend(sGlobal, space, (Integer) value.get("uid"), (Integer) value
										.get("friend"), (String) value.get("target_ids"))) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), "", 0);
							value = Common.mkFeed(sNames, sConfig, request, value, null);
							if (Common.empty(hotList)) {
								hotList.put(value.get("feedid"), value);
							} else {
								hotListAll.put(value.get("feedid"), value);
							}
						}
					}
					int nextHotNum = (Integer) sConfig.get("feedhotnum") - 1;
					if (nextHotNum > 0) {
						if (hotListAll.size() > nextHotNum) {
							Object[] hotListKey = Common.arrayRand(hotListAll, nextHotNum);
							if (nextHotNum == 1) {
								hotList.put(hotListKey[0], hotListAll.get(hotListKey[0]));
							} else {
								for (Object key : hotListKey) {
									hotList.put(key, hotListAll.get(key));
								}
							}
						} else {
							hotList = hotListAll;
						}
					}
				}
			}
			List<Map<String, Object>> topicList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("topic") + " ORDER BY lastpost DESC LIMIT 0,1");
			for (Map<String, Object> value : topicList) {
				value.put("pic", !Common.empty(value.get("pic")) ? Common.pic_get(sConfig, (String) value
						.get("pic"), (Integer) value.get("thumb"), (Integer) value.get("remote"), true) : "");
			}
			request.setAttribute("topiclist", topicList);
			space.put("allnum", 0);
			String[] strNum = new String[] {"notenum", "addfriendnum", "mtaginvitenum", "eventinvitenum",
					"myinvitenum", "pokenum", "reportnum", "namestatusnum", "eventverifynum"};
			for (String value : strNum) {
				space.put("allnum", (Integer) space.get("allnum")
						+ (space.get(value) == null ? 0 : (Integer) space.get(value)));
			}
		}
		Common.realname_get(sGlobal, sConfig, sNames, space);
		LinkedHashMap list = new LinkedHashMap();
		Set feedKeys = feed_list.keySet();
		int dateline = 0;
		int today = (Integer) sGlobal.get("today");
		String theday = null;
		List todayList = null;
		List yesterdayList = null;
		List thedayList = null;
		for (Object feedKey : feedKeys) {
			Map<Object, Map> values = feed_list.get(feedKey);
			Set keys = values.keySet();
			List actors = new ArrayList(keys.size());
			Map a_value = null;
			for (Object key : keys) {
				Map value = values.get(key);
				if (Common.empty(a_value)) {
					a_value = value;
				}
				actors.add("<a href=\"space.jsp?uid=" + value.get("uid") + "\">"
						+ sNames.get((Integer) value.get("uid")) + "</a>");
			}
			if (!Common.empty(hotList.get(a_value.get("feedid")))) {
				continue;
			}
			a_value = Common.mkFeed(sNames, sConfig, request, a_value, actors);
			dateline = (Integer) a_value.get("dateline");
			if (dateline >= today) {
				if (list.get("today") == null) {
					todayList = new ArrayList();
				}
				todayList.add(a_value);
				list.put("today", todayList);
			} else if (dateline >= today - 3600 * 24) {
				if (list.get("yesterday") == null) {
					yesterdayList = new ArrayList();
				}
				yesterdayList.add(a_value);
				list.put("yesterday", yesterdayList);
			} else {
				theday = Common.sgmdate(request, "yyyy-MM-dd", dateline);
				if (list.get(theday) == null) {
					thedayList = new ArrayList();
				}
				thedayList.add(a_value);
				list.put(theday, thedayList);
			}
		}
		Map value = null;
		List appList = null;
		Set appFeedKeys = appfeedList.keySet();
		for (Object appFeedKey : appFeedKeys) {
			Map<Object, Map> values = appfeedList.get(appFeedKey);
			Set keys = values.keySet();
			Map a_value = null;
			List actors = new ArrayList(keys.size());
			for (Object key : keys) {
				value = values.get(key);
				if (Common.empty(a_value)) {
					a_value = value;
				}
				actors.add("<a href=\"space.jsp?uid=" + value.get("uid") + "\">"
						+ sNames.get((Integer) value.get("uid")) + "</a>");
			}
			a_value = Common.mkFeed(sNames, sConfig, request, a_value, actors);
			if (list.get("app") == null) {
				appList = new ArrayList();
			}
			appList.add(a_value);
			list.put("app", appList);
		}
		String templateDir = JavaCenterHome.jchRoot + "template";
		File[] tplDir = Common.readDir(templateDir);
		if (tplDir != null) {
			Map<String, String> templates = new LinkedHashMap<String, String>();
			Map<String, String> defaultTemplate = new HashMap<String, String>();
			for (File dir : tplDir) {
				File styleFile = new File(templateDir + "/" + dir.getName() + "/style.css");
				if (styleFile.exists()) {
					File templateImageFile = new File(templateDir + "/" + dir.getName()
							+ "/image/template.gif");
					String tplIcon = templateImageFile.exists() ? "template/" + dir.getName()
							+ "/image/template.gif" : "image/tlpicon.gif";
					if (dir.getName().equals(sConfig.get("template"))) {
						defaultTemplate.put("name", dir.getName());
						defaultTemplate.put("icon", tplIcon);
					} else {
						templates.put(dir.getName(), tplIcon);
					}
				}
			}
			TPL.put("templates", templates);
			TPL.put("defaultTemplate", defaultTemplate);
		}
		Map myActives = new HashMap();
		myActives.put(Common.in_array(new String[] {"site", "myapp"}, filter) ? filter : "all",
				" class=\"active\"");
		Map actives = new HashMap();
		String key=null;
		if(Common.in_array(new String[] {"doing", "album", "blog", "poll", "thread", "event", "share"}, icon)){
			key=icon;
		}else{
			key="all";
		}
		actives.put(key," class=\"active\"");
		if ((Integer) space.get("uid") > 0 && !(Boolean) space.get("self")) {
			Map userGroup = (Map) request.getAttribute("usergroup" + space.get("groupid"));
			if (userGroup != null) {
				request.setAttribute("gColor", Common.getColor(userGroup));
				request.setAttribute("gIcon", Common.getIcon(userGroup));
			}
			TPL.put("spacetitle", "动态");
			TPL.put("spacemenus", new String[] {"<a href=\"space.jsp?uid=" + space.get("uid")
					+ "&do=feed&view=me\">TA的近期动态</a>"});
		}
		request.setAttribute("count", count);
		request.setAttribute("perpage", perPage);
		request.setAttribute("start", start);
		request.setAttribute("navtitle", "首页 - ");
		request.setAttribute("isnewer", isNewer);
		request.setAttribute("actives", actives);
		request.setAttribute("hotlist", hotList);
		request.setAttribute("TPL", TPL);
		request.setAttribute("list", list);
		request.setAttribute("my_checkupdate", Common.myCheckUpdate(request, response));
		request.setAttribute("my_showgift", myShowGift(sGlobal, sConfig));
		return include(request, response, sConfig, sGlobal, "space_home.jsp");
	}
}