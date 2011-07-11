
package cn.jcenterhome.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.phprpc.util.AssocArray;
import org.phprpc.util.PHPSerializer;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;

import cn.jcenterhome.service.AdminDeleteService;
import cn.jcenterhome.service.TreeService;
import cn.jcenterhome.util.BBCode;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.CookieHelper;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.FileUploadUtil;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.vo.MessageVO;
import cn.jcenterhome.web.servlet.PostHandler;

public class CpAction extends BaseAction {
	private String[] acs = {"space", "doing", "upload", "comment", "blog", "album", "relatekw", "common",
			"class", "thread", "mtag", "poke", "friend", "avatar", "profile", "theme", "import",
			"feed", "privacy", "pm", "share", "invite", "sendmail", "userapp", "task", "credit", "password",
			"domain", "event", "poll", "topic", "click", "magic", "top", "videophoto", "gift"};

	@SuppressWarnings("unchecked")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String ac = request.getParameter("ac");
		if (ac == null || ac.length() == 0 || !Common.in_array(acs, ac)) {
			ac = "profile";
		}
		int supeUID = (Integer) sGlobal.get("supe_uid");
		if (supeUID == 0) {
			String charset = JavaCenterHome.JCH_CHARSET;
			if (request.getMethod().equals("GET")) {
				CookieHelper.setCookie(request, response, "_refer", URLEncoder.encode((String) request
						.getAttribute("requestURI"), charset));
			} else {
				CookieHelper.setCookie(request, response, "_refer", URLEncoder.encode("cp.jsp?ac=" + ac,
						charset));
			}
			return showMessage(request, response, "to_login", "do.jsp?ac=" + sConfig.get("login_action"));
		}
		Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, supeUID);
		if (space == null || space.size() == 0) {
			return showMessage(request, response, "space_does_not_exist");
		}
		if (!ac.equals("common") && !ac.equals("pm")) {
			String message = Common.checkClose(request, response, supeUID);
			if (message != null) {
				return showMessage(request, response, message);
			}
			if ((Integer) space.get("flag") == -1) {
				return showMessage(request, response, "space_has_been_locked");
			}
			if (Common.checkPerm(request, response, "banvisit")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				}
				return showMessage(request, response, "you_do_not_have_permission_to_visit");
			}
			if (ac.equals("userapp") && !Common.checkPerm(request, response, "allowmyop")) {
				return showMessage(request, response, "no_privilege");
			}
		}

		Map actives = new HashMap();
		actives.put(ac, " class=active");
		request.setAttribute("actives", actives);
		request.setAttribute("space", space);

		return invokeMethod(this, "cp_" + ac, request, response);
	}

	public ActionForward cp_album(HttpServletRequest request, HttpServletResponse response) {
		

		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");

		int albumid = Common.intval(request.getParameter("albumid"));
		int picid = Common.intval(request.getParameter("picid"));

		int supe_uid = (Integer) sGlobal.get("supe_uid");

		String op = request.getParameter("op");
		if ("edit".equals(op)) {
			if (albumid < 1) {
				return showMessage(request, response, "photos_do_not_support_the_default_settings",
						"cp.jsp?ac=album&op=editpic", 0);
			}
			List<Map<String, Object>> albums = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("album") + " WHERE albumid='" + albumid + "'");
			if (Common.empty(albums)) {
				return showMessage(request, response, "no_privilege");
			}
			Map<String, Object> album = albums.get(0);
			if ((Integer) album.get("uid") != supe_uid && !Common.checkPerm(request, response, "managealbum")) {
				return showMessage(request, response, "no_privilege");
			}

			try {
				if (submitCheck(request, "editsubmit")) {
					String albumname = Common.getStr(request.getParameter("albumname"), 50, true, true, true,
							0, 0, request, response);
					if (Common.empty(albumname)) {
						return showMessage(request, response, "album_name_errors");
					}
					int friend = Common.intval(request.getParameter("friend"));
					String target_ids = "";
					String password = request.getParameter("password");
					if (friend == 2) {
						List<String> uids = null;
						String target_names = request.getParameter("target_names");
						String[] names = Common.empty(target_names) ? null : target_names.trim().replaceAll(
								Common.getMessage(request, "cp_tab_space"), " ").split(" ");
						if (!Common.empty(names)) {
							uids = dataBaseService.executeQuery("SELECT uid FROM "
									+ JavaCenterHome.getTableName("space") + " WHERE username IN ("
									+ Common.sImplode(names) + ")", 1);
						}
						if (Common.empty(uids)) {
							friend = 3;
						} else {
							target_ids = Common.implode(uids, ",");
						}
					} else if (friend == 4) {
						password = Common.trim(password);
						if (password.equals("")) {
							friend = 0;
						}
					}
					if (friend != 2) {
						target_ids = "";
					}
					if (friend != 4) {
						password = "";
					}

					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("albumname", albumname);
					setData.put("friend", friend);
					setData.put("password", password);
					setData.put("target_ids", target_ids);
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("albumid", albumid);
					dataBaseService.updateTable("album", setData, whereData);
					return showMessage(request, response, "do_success", "cp.jsp?ac=album&op=edit&albumid="
							+ albumid);
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			album.put("target_names", "");
			int friend = (Integer) album.get("friend");
			request.setAttribute("friend_" + friend, " selected");

			String passwordstyle = "display:none", selectgroupstyle = "display:none";
			if (friend == 4) {
				passwordstyle = "";
			} else if (friend == 2) {
				selectgroupstyle = "";
				String target_ids = (String) album.get("target_ids");
				if (!Common.empty(target_ids)) {
					List<String> names = dataBaseService.executeQuery("SELECT username FROM "
							+ JavaCenterHome.getTableName("space") + " WHERE uid IN (" + target_ids + ")", 1);
					album.put("target_names", Common.implode(names, " "));
				}
			}

			request.setAttribute("albumid", albumid);
			request.setAttribute("album", album);
			request.setAttribute("passwordstyle", passwordstyle);
			request.setAttribute("selectgroupstyle", selectgroupstyle);
			request.setAttribute("groups", Common.getFriendGroup(request));
		} else if ("delete".equals(op)) {
			List<Map<String, Object>> albumsList = cpService.getAlbums(supe_uid);
			if (Common.empty(albumsList)) {
				return showMessage(request, response, "no_privilege");
			}
			Map<Integer, Map<String, Object>> albums = new LinkedHashMap<Integer, Map<String, Object>>();
			for (Map<String, Object> value : albumsList) {
				albums.put((Integer) value.get("albumid"), value);
			}

			try {
				if (submitCheck(request, "deletesubmit")) {
					int moveto = Common.intval(request.getParameter("moveto"));
					if (moveto < 0) {
						if (!adminDeleteService.deleteAlbums(request, response, supe_uid,
								new Integer[] {albumid})) {
							return showMessage(request, response, "no_privilege");
						}
					} else {
						if (moveto != 0 && Common.empty(albums.get(moveto))) {
							moveto = 0;
						}
						Map<String, Object> setData = new HashMap<String, Object>();
						Map<String, Object> whereData = new HashMap<String, Object>();
						if (moveto > 0) {
							Map<String, Object> album = albums.get(albumid);
							setData.put("albumid", moveto);
							whereData.put("albumid", albumid);
							dataBaseService.updateTable("pic", setData, whereData);
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("album")
									+ " SET picnum=picnum+" + album.get("picnum") + ", updatetime='"
									+ sGlobal.get("timestamp") + "' WHERE albumid='" + moveto + "'");
						} else {
							setData.put("albumid", 0);
							whereData.put("albumid", albumid);
							dataBaseService.updateTable("pic", setData, whereData);
						}
						dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("album")
								+ " WHERE albumid='" + albumid + "'");
					}
					return showMessage(request, response, "do_success", "space.jsp?do=album&view=me");
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("albumid", albumid);
			request.setAttribute("albums", albums);
		} else if ("editpic".equals(op)) {

			boolean managealbum = Common.checkPerm(request, response, "managealbum");

			List<Map<String, Object>> query;
			Map<String, Object> album = null;
			if (albumid > 0) {
				query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("album")
						+ " WHERE albumid='" + albumid + "'");
				album = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(album)) {
					return showMessage(request, response, "no_privilege");
				}

				if ((Integer) album.get("uid") != supe_uid && !managealbum) {
					return showMessage(request, response, "no_privilege");
				}
			}

			try {
				if (submitCheck(request, "editpicsubmit")) {
					String subop = request.getParameter("subop");
					if ("delete".equals(subop)) {
						Map<String, String> deleteids = new HashMap<String, String>();
						Map<String, String> title_RequestParameter = (Map<String, String>) getParameters(
								request, "title");
						Map<String, String> ids = (Map<String, String>) getParameters(request, "ids");
						String title;
						String picidTemp;
						String value;
						for (Entry<String, String> entry : title_RequestParameter.entrySet()) {
							picidTemp = entry.getKey();
							value = entry.getValue();
							if (Common.empty(ids.get(picidTemp))) {
								title = Common.getStr(value, 150, true, true, true, 0, 0, request, response);
								Map<String, Object> wherearr = new HashMap<String, Object>();
								wherearr.put("picid", picidTemp);
								if (!managealbum)
									wherearr.put("uid", supe_uid);

								Map<String, Object> setData = new HashMap<String, Object>();
								setData.put("title", title);
								dataBaseService.updateTable("pic", setData, wherearr);
							} else {
								deleteids.put(picidTemp, picidTemp);
							}
						}
						if (!Common.empty(deleteids)) {
							adminDeleteService.deletePics(request, response, supe_uid, deleteids);
						}
					} else if ("update".equals(subop)) {
						Map<String, String> title_RequestParameter = (Map<String, String>) getParameters(
								request, "title");
						String title;
						String value;
						String picidTemp;
						for (Entry<String, String> entry : title_RequestParameter.entrySet()) {
							picidTemp = entry.getKey();
							value = entry.getValue();
							try {
								title = Common.getStr(value, 150, true, true, true, 0, 0, request, response);
							} catch (Exception e) {
								e.printStackTrace();
								return showMessage(request, response, e.getMessage());
							}
							Map<String, Object> wherearr = new HashMap<String, Object>();
							wherearr.put("picid", picidTemp);
							if (!managealbum)
								wherearr.put("uid", supe_uid);

							Map<String, Object> setData = new HashMap<String, Object>();
							setData.put("title", title);
							dataBaseService.updateTable("pic", setData, wherearr);
						}

					} else if ("move".equals(subop)) {
						Map<String, String> title_RequestParameter = (Map<String, String>) getParameters(
								request, "title");
						String title;
						String value;
						String picidTemp;
						for (Entry<String, String> entry : title_RequestParameter.entrySet()) {
							value = entry.getValue();
							title = Common.getStr(value, 150, true, true, true, 0, 0, request, response);
							picidTemp = entry.getKey();
							Map<String, Object> wherearr = new HashMap<String, Object>();
							wherearr.put("picid", picidTemp);
							if (!managealbum)
								wherearr.put("uid", supe_uid);

							Map<String, Object> setData = new HashMap<String, Object>();
							setData.put("title", title);
							dataBaseService.updateTable("pic", setData, wherearr);
						}
						Map<String, String> ids = (Map<String, String>) getParameters(request, "ids");
						if (!Common.empty(ids)) {
							String plussql = managealbum ? "" : "AND uid=" + supe_uid;
							int newalbumid = Common.intval(request.getParameter("newalbumid"));
							if (newalbumid != 0) {
								query = dataBaseService.executeQuery("SELECT albumid FROM "
										+ JavaCenterHome.getTableName("album") + " WHERE albumid='"
										+ newalbumid + "' " + plussql);
								album = query.size() > 0 ? query.get(0) : null;
								if (Common.empty(album)) {
									newalbumid = 0;
								}
							}
							int updatecount = dataBaseService.executeUpdate("UPDATE "
									+ JavaCenterHome.getTableName("pic") + " SET albumid='" + newalbumid
									+ "' WHERE picid IN (" + Common.sImplode(ids) + ") " + plussql);
							if (updatecount != 0) {
								if (albumid > 0) {
									dataBaseService.executeUpdate("UPDATE "
											+ JavaCenterHome.getTableName("album") + " SET picnum=picnum-"
											+ updatecount + " WHERE albumid='" + albumid + "' " + plussql);
									album_update_pic(sGlobal, space, albumid);
								}
								if (newalbumid != 0) {
									dataBaseService.executeUpdate("UPDATE "
											+ JavaCenterHome.getTableName("album") + " SET picnum=picnum+"
											+ updatecount + " WHERE albumid='" + newalbumid + "' " + plussql);
									album_update_pic(sGlobal, space, newalbumid);
								}
							}
						}
					}
					String refer = request.getParameter("refer");
					String page = request.getParameter("page");
					page = page == null ? "" : page;
					String url = Common.empty(refer) ? "cp.jsp?ac=album&op=editpic&albumid=" + albumid
							+ "&page=" + page : refer;
					return showMessage(request, response, "do_success", url, 0);

				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			int perpage = 10;
			int page = Common.intval(request.getParameter("page"));
			if (page < 1)
				page = 1;
			int start = (page - 1) * perpage;
			int maxpage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perpage, maxpage);
			if (result != null) {
				return showMessage(request, response, result);
			}

			String picsql = picid != 0 ? "picid='" + picid + "' AND " : "";

			String wheresql;
			int count;
			if (albumid > 0) {
				wheresql = "albumid='" + albumid + "'";
				count = (Integer) album.get("picnum");
			} else {
				wheresql = "albumid='0' AND uid='" + supe_uid + "'";
				query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
						+ JavaCenterHome.getTableName("pic") + " WHERE " + picsql + " " + wheresql);
				count = query.size() > 0 ? (Integer) query.get(0).get("cont") : 0;
			}

			List<Map<String, Object>> list = null;
			if (count != 0) {
				if (page > 1 && start >= count) {
					page--;
					start = (page - 1) * perpage;
				}
				query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("pic")
						+ " WHERE " + picsql + " " + wheresql + " ORDER BY dateline DESC LIMIT " + start
						+ "," + perpage);
				for (Map<String, Object> value : query) {
					value.put("title", BBCode.html2bbcode((String) value.get("title")));
					value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"), (Integer) value
							.get("thumb"), (Integer) value.get("remote"), true));
					value.put("bigpic", Common.pic_get(sConfig, (String) value.get("filepath"),
							(Integer) value.get("thumb"), (Integer) value.get("remote"), false));
				}
				list = query;
			}

			String multi = Common.multi(request, count, perpage, page, maxpage,
					"cp.jsp?ac=album&op=editpic&albumid=" + albumid, "", "");
			List<Map<String, Object>> albumlist = cpService.getAlbums(supe_uid);
			request.setAttribute("albumid", albumid);
			request.setAttribute("album", album);
			request.setAttribute("list", list);
			request.setAttribute("albumlist", albumlist);
			request.setAttribute("page", page);
			request.setAttribute("multi", multi);
		} else if ("setpic".equals(op)) {
			String uidsql = Common.checkPerm(request, response, "managealbum") ? "" : "AND uid='" + supe_uid
					+ "'";
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("pic") + " WHERE picid='" + picid + "' " + uidsql);
			Map<String, Object> pic = query.size() > 0 ? query.get(0) : null;
			if (!Common.empty(pic)) {
				if ((Integer) pic.get("albumid") != 0) {
					pic.put("picflag", (Integer) pic.get("remote") != 0 ? 2 : 1);
					pic.put("filepath", pic.get("filepath")
							+ ((Integer) pic.get("thumb") != 0 ? ".thumb.jpg" : ""));
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("pic", pic.get("filepath"));
					setData.put("picflag", pic.get("picflag"));

					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("albumid", pic.get("albumid"));
					dataBaseService.updateTable("album", setData, whereData);
				}
			}
			return showMessage(request, response, "do_success");

		} else if ("edittitle".equals(op)) {

			String uidsql = Common.checkPerm(request, response, "managealbum") ? "" : "AND uid='" + supe_uid
					+ "'";
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("pic") + " WHERE picid='" + picid + "' " + uidsql);
			Map<String, Object> pic = query.size() > 0 ? query.get(0) : null;

			request.setAttribute("pic", pic);
		} else if ("edithot".equals(op)) {
			if (!Common.checkPerm(request, response, "managealbum")) {
				return showMessage(request, response, "no_privilege");
			}

			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("pic") + " WHERE picid='" + picid + "'");
			Map<String, Object> pic = query.size() > 0 ? query.get(0) : null;
			if (Common.empty(pic)) {
				return showMessage(request, response, "no_privilege");
			}
			try {
				if (submitCheck(request, "hotsubmit")) {
					int hot = Common.intval(request.getParameter("hot"));
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("hot", hot);
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("picid", picid);
					dataBaseService.updateTable("pic", setData, whereData);
					if (hot > 0) {
						feedService.feedPublish(request, response, picid, "picid", false);
					} else {
						whereData.clear();
						whereData.put("id", picid);
						whereData.put("idtype", "picid");
						dataBaseService.updateTable("feed", setData, whereData);
					}
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("picid", picid);
			request.setAttribute("pic", pic);
		}
		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "cp_album.jsp");
	}

	private void album_update_pic(Map<String, Object> sGlobal, Map<String, Object> space, int albumid) {

		Map<String, Object> pic = new HashMap<String, Object>();
		pic.put("filepath", "");
		pic.put("picflag", 0);
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("pic") + " WHERE albumid='" + albumid + "' AND uid='"
				+ supe_uid + "' ORDER BY dateline DESC LIMIT 1");
		int tempI;
		for (Map<String, Object> value : query) {
			tempI = (Integer) value.get("remote");
			pic.put("picflag", tempI != 0 ? 2 : 1);
			tempI = (Integer) value.get("thumb");
			pic.put("filepath", (String) value.get("filepath") + (tempI != 0 ? ".thumb.jpg" : ""));
		}
		Map<String, Object> setData = new HashMap<String, Object>();
		setData.put("pic", pic.get("filepath"));
		setData.put("picflag", pic.get("picflag"));
		Map<String, Object> whereData = new HashMap<String, Object>();
		whereData.put("albumid", albumid);
		whereData.put("uid", supe_uid);
		dataBaseService.updateTable("album", setData, whereData);
	}

	
	private Object getParameters(HttpServletRequest request, String prefix) {
		return getParameters(request, prefix, false);
	}

	
	private Object getParameters(HttpServletRequest request, String prefix, boolean isCheckBox) {
		Map<String, String[]> primalParameters = request.getParameterMap();
		if (primalParameters == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		String key;
		String[] value;
		String prefix_ = null;
		if (prefix != null) {
			prefix_ = prefix + "[";
		}
		for (Entry<String, String[]> primalPE : primalParameters.entrySet()) {
			key = primalPE.getKey();
			if (prefix == null || key.startsWith(prefix_)) {
				value = primalPE.getValue();
				if (!getParametersSetResultMap(result, key, value, isCheckBox)) {
					return null;
				}
			}
		}
		if (prefix != null) {
			return result.get(prefix);
		}
		return result;
	}

	private String disposeParameter(String parameterName) {
		if (parameterName.endsWith("[]")) {
			return parameterName.substring(0, parameterName.length() - 2);
		} else {
			return parameterName;
		}
	}

	private boolean getParametersSetResultMap(Map<String, Object> result, String key, String[] value,
			boolean isCheckBox) {
		key = disposeParameter(key);
		return getParametersParseKey(new StringBuilder(key), result, value, isCheckBox);
	}

	private boolean getParametersParseKey(StringBuilder operatingKey, Map<String, Object> supMap,
			String[] value, boolean isCheckBox) {
		int tempI = operatingKey.indexOf("[");
		int tempII = operatingKey.indexOf("]");
		if (tempI < 0) {
			putValue(supMap, operatingKey.toString(), value, isCheckBox);
			return true;
		} else if (tempII < tempI) {
			return false;
		}
		String subKey = operatingKey.substring(0, tempI);
		Map<String, Object> subMap = (Map<String, Object>) supMap.get(subKey);
		if (subMap == null) {
			subMap = new HashMap<String, Object>();
			supMap.put(subKey, subMap);
		}
		operatingKey.deleteCharAt(tempII);
		operatingKey.delete(0, tempI + 1);
		return getParametersParseKey(operatingKey, subMap, value, isCheckBox);
	}

	private void putValue(Map<String, Object> targetMap, String key, String[] value, boolean isCheckBox) {
		if (isCheckBox || value == null || value.length == 0) {
			targetMap.put(key, value);
		} else {
			targetMap.put(key, value[0]);
		}
	}

	public ActionForward cp_avatar(HttpServletRequest request, HttpServletResponse response) {
		try {
			String a = request.getParameter("a");
			if (!Common.empty(a)) {
				String result = Common.checkInput(request);
				if (result == null) {
					if ("uploadAvatar".equals(a)) {
						result = uploadAvatar();
					} else if ("rectAvatar".equals(a)) {
						result = rectAvatar();
					}
				}
				PrintWriter out = response.getWriter();
				out.write(result);
				out.flush();
				return null;
			} else if (submitCheck(request, "avatarsubmit")) {
				return showMessage(request, response, "do_success", "cp.jsp?ac=avatar", 0);
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		Object avatarFlash = Common.avatar(request, supe_uid,
				Common.empty(sConfig.get("avatarreal")) ? "virtual" : "real", true);
		request.setAttribute("avatarFlash", avatarFlash);
		List<String> sets = new ArrayList<String>();
		boolean avatarExists = cpService.ckavatar(sGlobal, sConfig, supe_uid);
		int avatar = (Integer) space.get("avatar");
		int timestamp = (Integer) sGlobal.get("timestamp");
		if (avatarExists) {
			if (avatar == 0) {
				Map<String, Integer> reward = Common.getReward("setavatar", false, 0, "", true, request,
						response);
				int credit = reward.get("credit");
				int experience = reward.get("experience");
				if (credit != 0) {
					sets.add("credit=credit+" + credit);
				}
				if (experience != 0) {
					sets.add("experience=experience+" + experience);
				}
				sets.add("avatar=1");
				sets.add("updatetime=" + timestamp);
			}
		} else {
			if (avatar == 1) {
				sets.add("avatar=0");
			}
		}

		if (sets.size() > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
					+ Common.implode(sets, ",") + " WHERE uid='" + supe_uid + "'");
			if ((Integer) sConfig.get("my_status") == 1) {
				Map<String, Object> insertData = new HashMap<String, Object>();
				insertData.put("uid", supe_uid);
				insertData.put("action", "update");
				insertData.put("dateline", timestamp);
				dataBaseService.insertTable("userlog", insertData, false, true);
			}
		}
		return include(request, response, sConfig, sGlobal, "cp_avatar.jsp");
	}

	private String uploadAvatar() {
		return null;
	}

	private String rectAvatar() {
		boolean success = true;
		if (success) {
			return "<?xml version=\"1.0\" ?><root><face success=\"1\"/></root>";
		} else {
			return "<?xml version=\"1.0\" ?><root><face success=\"0\"/></root>";
		}
	}

	private String decodeFlashData(byte[] s) {
		StringBuffer r = new StringBuffer();
		return r.toString();
	}

	
	public ActionForward cp_blog(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");

		int blogId = Common.intval(request.getParameter("blogid"));
		String op = Common.empty(request.getParameter("op")) ? "" : request.getParameter("op");

		Map<String, Object> blog = new HashMap<String, Object>();
		if (!Common.empty(blogId)) {
			List<Map<String, Object>> blogs = dataBaseService.executeQuery("SELECT bf.*,b.* FROM "
					+ JavaCenterHome.getTableName("blog") + " b LEFT JOIN "
					+ JavaCenterHome.getTableName("blogfield") + " bf ON bf.blogid=b.blogid WHERE b.blogid='"
					+ blogId + "'");
			if (blogs.size() != 0) {
				blog = blogs.get(0);
			}
		}

		if (blog.size() == 0) {
			if (!Common.checkPerm(request, response, "allowblog")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				}
				return showMessage(request, response, "no_authority_to_add_log");
			}
			if (!cpService.checkRealName(request, "blog")) {
				return showMessage(request, response, "no_privilege_realname");
			}
			if (!cpService.checkVideoPhoto(request, response, "blog")) {
				return showMessage(request, response, "no_privilege_videophoto");
			}
			switch (cpService.checkNewUser(request, response)) {
				case 1:
					break;
				case 2:
					return showMessage(request, response, "no_privilege_newusertime", "", 1, String
							.valueOf(sConfig.get("newusertime")));
				case 3:
					return showMessage(request, response, "no_privilege_avatar");
				case 4:
					return showMessage(request, response, "no_privilege_friendnum", "", 1, String
							.valueOf(sConfig.get("need_friendnum")));
				case 5:
					return showMessage(request, response, "no_privilege_email");
			}
			int waitTime = Common.checkInterval(request, response, "post");
			if (waitTime > 0) {
				return showMessage(request, response, "operating_too_fast", "", 1, String.valueOf(waitTime));
			}
			try {
				String subject = request.getParameter("subject");
				String message = request.getParameter("message");
				if (!Common.empty(subject)) {
					blog.put("subject", Common.getStr(subject, 80, true, false, false, 0, 0, request,
							response));
				}
				if (!Common.empty(message)) {
					blog.put("message", Common.getStr(message, 5000, true, false, false, 0, 0, request,
							response));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (!sGlobal.get("supe_uid").equals(blog.get("uid")) && !Common.checkPerm(request, response, "manageblog")) {
				return showMessage(request, response, "no_authority_operation_of_the_log");
			}
		}

		try {
			if (submitCheck(request, "blogsubmit")) {
				
				if (blog.get("blogid") == null) {
					blog = new HashMap<String, Object>();
				} else {
					if (!Common.checkPerm(request, response, "allowblog")) {
						MessageVO msgVO = Common.ckSpaceLog(request);
						if (msgVO != null) {
							return showMessage(request, response, msgVO);
						}
						return showMessage(request, response, "no_authority_to_add_log");
					}
				}
				if (Common.checkPerm(request, response, "seccode")
						&& !cpService.checkSeccode(request, response, sGlobal, sConfig, request
								.getParameter("seccode"))) {
					return showMessage(request, response, "incorrect_code");
				}
				Map<String, Object> newBlog = blogService.blogPost(request, response, blog);
				if (newBlog == null) {
					return showMessage(request, response, "that_should_at_least_write_things");
				} else if (Common.empty(blog) && !Common.empty(newBlog.get("topicid"))) {
					return showMessage(request, response, "do_success", "space.jsp?do=topic&topicid="
							+ newBlog.get("topicid") + "&view=blog", 0);
				} else {
					return showMessage(request, response, "do_success", "space.jsp?uid=" + newBlog.get("uid")
							+ "&do=blog&id=" + newBlog.get("blogid"), 0);
				}
			}

			if (op.equals("delete")) {
				

				if (submitCheck(request, "deletesubmit")) {
					if (blogService.deleteBlogs(request, response, blogId) != null) {
						return showMessage(request, response, "do_success", "space.jsp?uid="
								+ blog.get("uid") + "&do=blog&view=me");
					} else {
						return showMessage(request, response, "failed_to_delete_operation");
					}
				}

			} else if (op.equals("goto")) {
				

				int id = Common.intval(request.getParameter("id"));
				Map<String, Object> whereArr = new HashMap<String, Object>();
				whereArr.put("blogid", id);
				int uid = id != 0 ? Common.intval(Common.getCount("blog", whereArr, "uid")) : 0;
				return showMessage(request, response, "do_success", "space.jsp?uid=" + uid + "&do=blog&id="
						+ id, 0);

			} else if (op.equals("edithot")) {
				

				if (!Common.checkPerm(request, response, "manageblog")) {
					return showMessage(request, response, "no_privilege");
				}
				if (submitCheck(request, "hotsubmit")) {
					int hot = Common.intval(request.getParameter("hot"));
					Map<String, Object> setData = new HashMap<String, Object>();
					Map<String, Object> whereData = new HashMap<String, Object>();
					setData.put("hot", hot);
					whereData.put("blogid", blog.get("blogid"));
					dataBaseService.updateTable("blog", setData, whereData);
					if (hot > 0) {
						feedService.feedPublish(request, response, (Integer) blog.get("blogid"), "blogid",
								false);
					} else {
						whereData = new HashMap<String, Object>();
						whereData.put("id", blog.get("blogid"));
						whereData.put("idtype", "blogid");
						dataBaseService.updateTable("feed", setData, whereData);
					}
					return showMessage(request, response, "do_success", "space.jsp?uid=" + blog.get("uid")
							+ "&do=blog&id=" + blog.get("blogid"), 0);
				}

			} else {
				
				Map<Integer, Object> classArr = !Common.empty(blog.get("uid")) ? cpService
						.getClassArr((Integer) blog.get("uid")) : cpService.getClassArr((Integer) sGlobal
						.get("supe_uid"));
				List<Map<String, Object>> albums = cpService.getAlbums((Integer) sGlobal.get("supe_uid"));
				Map tags = Common.empty(blog.get("tag")) ? new HashMap() : Serializer.unserialize(
						(String) blog.get("tag"), true);
				blog.put("tag", Common.implode(tags, " "));
				blog.put("target_names", "");
				String passwordStyle = "display:none";
				String selectGroupStyle = "display:none";
				if (blog.get("friend") != null && (Integer) blog.get("friend") == 4) {
					passwordStyle = "";
				} else if (blog.get("friend") != null && (Integer) blog.get("friend") == 2) {
					selectGroupStyle = "";
					if (!Common.empty(blog.get("target_ids"))) {
						List<String> names = dataBaseService.executeQuery("SELECT username FROM "
								+ JavaCenterHome.getTableName("space") + " WHERE uid IN ("
								+ blog.get("target_ids") + ")", 1);
						blog.put("target_names", Common.implode(names, " "));
					}
				}

				String message = blog.get("message") == null ? "" : ((String) blog.get("message")).replace(
						"&amp;", "&amp;amp;");
				blog.put("message", Common.sHtmlSpecialChars(message));
				int allowHtml = (Integer) Common.checkPerm(request, response, sGlobal, "allowhtml");

				int topicId = Common.intval(request.getParameter("topicid"));
				if (topicId != 0) {
					Map<String, Object> topic = Common.getTopic(request, topicId);
					if (topic != null) {
						Map<String, String> actives = new HashMap<String, String>();
						actives.put("blog", " class='active'");
						request.setAttribute("topic", topic);
						request.setAttribute("topicid", topicId);
					}
				}

				Map<String, String> menuActives = new HashMap<String, String>();
				menuActives.put("space", " class='active'");

				boolean blogPrivacy = Common.ckPrivacy(sGlobal, sConfig, space, "blog", 1);
				request.setAttribute("classarr", classArr);
				request.setAttribute("allowhtml", allowHtml);
				request.setAttribute("groups", Common.getFriendGroup(request));
				request.setAttribute("friend", blog.get("friend"));
				request.setAttribute("selectgroupstyle", selectGroupStyle);
				request.setAttribute("passwordstyle", passwordStyle);
				request.setAttribute("blogprivacy", blogPrivacy);
				request.setAttribute("albums", albums);
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		request.setAttribute("blogid", blogId);
		request.setAttribute("blog", blog);
		return include(request, response, sConfig, sGlobal, "cp_blog.jsp");
	}

	
	public ActionForward cp_class(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		int classId = Common.empty(request.getParameter("classid")) ? 0 : Common.intval(request
				.getParameter("classid"));
		String op = request.getParameter("op");

		Map classMap = null;
		if (classId != 0) {
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("class") + " WHERE classid='" + classId + "' AND uid='"
					+ sGlobal.get("supe_uid") + "'");
			if (query.isEmpty() == false) {
				classMap = query.get(0);
			}
		}
		if (classMap == null || classMap.isEmpty()) {
			return showMessage(request, response, "did_not_specify_the_type_of_operation");
		}

		try {
			if ("edit".equals(op)) {
				
				if (submitCheck(request, "editsubmit")) {
					String className = Common.getStr(request.getParameter("classname"), 40, true, true, true,
							0, 0, request, response);
					if (className.length() < 1) {
						return showMessage(request, response, "enter_the_correct_class_name");
					}
					Map set = new HashMap();
					set.put("classname", className);
					Map where = new HashMap();
					where.put("classid", classId);
					dataBaseService.updateTable("class", set, where);
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
			} else if ("delete".equals(op)) {
				
				if (submitCheck(request, "deletesubmit")) {
					Map set = new HashMap();
					set.put("classid", 0);
					Map where = new HashMap();
					where.put("classid", classId);
					dataBaseService.updateTable("blog", set, where);
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("class")
							+ " WHERE classid='" + classId + "'");
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		request.setAttribute("classmap", classMap);
		request.setAttribute("classid", classId);
		request.setAttribute("tpl_titles", new String[] {"首页"});
		return include(request, response, sConfig, sGlobal, "cp_class.jsp");
	}

	public ActionForward cp_click(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		String tempS = request.getParameter("clickid");
		int clickid = Common.empty(tempS) ? 0 : Common.intval(tempS);
		tempS = request.getParameter("idtype");
		String idtype = Common.empty(tempS) ? "" : tempS.trim();
		tempS = request.getParameter("id");
		int id = Common.empty(tempS) ? 0 : Common.intval(tempS);
		Map<String, Map<Integer, Map<String, Object>>> globalTask = Common.getCacheDate(request, response,
				"/data/cache/cache_click.jsp", "globalClick");

		Map<Integer, Map<String, Object>> tempMap = globalTask.get(idtype);
		Map<Integer, Map<String, Object>> clicks = Common.empty(tempMap) ? new LinkedHashMap<Integer, Map<String, Object>>()
				: tempMap;

		Map<String, Object> click = clicks.get(clickid);

		if (Common.empty(click)) {
			return showMessage(request, response, "click_error");
		}
		String sql;
		String tablename;
		if ("picid".equals(idtype)) {
			sql = "SELECT p.*, s.username, a.friend, pf.hotuser FROM " + JavaCenterHome.getTableName("pic")
					+ " p " + "LEFT JOIN " + JavaCenterHome.getTableName("picfield")
					+ " pf ON pf.picid=p.picid " + "LEFT JOIN " + JavaCenterHome.getTableName("album")
					+ " a ON a.albumid=p.albumid " + "LEFT JOIN " + JavaCenterHome.getTableName("space")
					+ " s ON s.uid=p.uid " + "WHERE p.picid='" + id + "'";
			tablename = JavaCenterHome.getTableName("pic");
		} else if ("tid".equals(idtype)) {
			sql = "SELECT t.*, p.hotuser FROM " + JavaCenterHome.getTableName("thread") + " t "
					+ "LEFT JOIN " + JavaCenterHome.getTableName("post")
					+ " p ON p.tid='$id' AND p.isthread='1' " + "WHERE t.tid='" + id + "'";
			tablename = JavaCenterHome.getTableName("thread");
		} else {
			idtype = "blogid";
			sql = "SELECT b.*, bf.hotuser FROM " + JavaCenterHome.getTableName("blog") + " b " + "LEFT JOIN "
					+ JavaCenterHome.getTableName("blogfield") + " bf ON bf.blogid=b.blogid "
					+ "WHERE b.blogid='" + id + "'";
			tablename = JavaCenterHome.getTableName("blog");
		}
		List<Map<String, Object>> query = dataBaseService.executeQuery(sql);
		Map<String, Object> item = query.size() > 0 ? query.get(0) : null;
		if (Common.empty(item)) {
			return showMessage(request, response, "click_item_error");
		}

		int itemUid = (Integer) item.get("uid");
		String hash = Common.md5(itemUid + "\t" + item.get("dateline"));

		String op = request.getParameter("op");
		if ("add".equals(op)) {
			if (!Common.checkPerm(request, response, "allowclick")
					|| !hash.equals(request.getParameter("hash"))) {
				return showMessage(request, response, "no_privilege");
			}

			if (itemUid == supe_uid) {
				return showMessage(request, response, "click_no_self");
			}
			if (cpService.isBlackList(itemUid, supe_uid) != 0) {
				return showMessage(request, response, "is_blacklist");
			}
			query = dataBaseService
					.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("clickuser")
							+ " WHERE uid='" + space.get("uid") + "' AND id='" + id + "' AND idtype='"
							+ idtype + "'");
			if (query.size() > 0) {
				return showMessage(request, response, "click_have");
			}
			int timestamp = (Integer) sGlobal.get("timestamp");
			Map<String, Object> setarr = new HashMap<String, Object>();
			setarr.put("uid", space.get("uid"));
			setarr.put("username", sGlobal.get("supe_username"));
			setarr.put("id", id);
			setarr.put("idtype", idtype);
			setarr.put("clickid", clickid);
			setarr.put("dateline", timestamp);
			dataBaseService.insertTable("clickuser", setarr, false, false);
			dataBaseService.executeUpdate("UPDATE " + tablename + " SET click_" + clickid + "=click_"
					+ clickid + "+1 WHERE " + idtype + "='" + id + "'");
			cpService.updateHot(request, response, idtype, id, (String) item.get("hotuser"));
			Common.realname_set(sGlobal, sConfig, sNames, itemUid, (String) item.get("username"), "", 0);
			Common.realname_get(sGlobal, sConfig, sNames, space);
			Map<String, Object> fs = new HashMap<String, Object>();
			String q_note;
			String note_type;
			if ("blogid".equals(idtype)) {
				fs.put("title_template", Common.getMessage(request, "cp_feed_click_blog"));
				Map<String, String> tempM = new HashMap<String, String>();
				tempM.put("touser", "<a href=\"space.jsp?uid=" + itemUid + "\">" + sNames.get(itemUid)
						+ "</a>");
				tempM.put("subject", "<a href=\"space.jsp?uid=" + itemUid + "&do=blog&id="
						+ item.get("blogid") + "\">" + item.get("subject") + "</a>");
				tempM.put("click", (String) click.get("name"));
				fs.put("title_data", tempM);
				fs.put("body_general", "");
				note_type = "clickblog";
				q_note = Common.getMessage(request, "cp_note_click_blog", "space.jsp?uid=" + itemUid
						+ "&do=blog&id=" + item.get("blogid"), item.get("subject"));
			} else if ("tid".equals(idtype)) {
				fs.put("title_template", Common.getMessage(request, "cp_feed_click_thread"));
				Map<String, String> tempM = new HashMap<String, String>();
				tempM.put("touser", "<a href=\"space.jsp?uid=" + itemUid + "\">" + sNames.get(itemUid)
						+ "</a>");
				tempM.put("subject", "<a href=\"space.jsp?uid=" + itemUid + "&do=thread&id="
						+ item.get("tid") + "\">" + item.get("subject") + "</a>");
				tempM.put("click", (String) click.get("name"));
				fs.put("title_data", tempM);
				fs.put("body_general", "");
				note_type = "clickthread";
				q_note = Common.getMessage(request, "cp_note_click_thread", "space.jsp?uid=" + itemUid
						+ "&do=thread&id=" + item.get("tid"), item.get("subject"));
			} else {
				fs.put("title_template", Common.getMessage(request, "cp_feed_click_pic"));
				Map<String, String> tempM = new HashMap<String, String>();
				tempM.put("touser", "<a href=\"space.jsp?uid=" + itemUid + "\">" + sNames.get(itemUid)
						+ "</a>");
				tempM.put("click", (String) click.get("name"));
				fs.put("title_data", tempM);
				fs.put("images", new String[] {Common.pic_get(sConfig, (String) item.get("filepath"),
						(Integer) item.get("thumb"), (Integer) item.get("remote"), true)});
				fs.put("image_links", new String[] {"space.jsp?uid=" + itemUid + "&do=album&picid="
						+ item.get("picid")});
				fs.put("body_general", item.get("title"));
				note_type = "clickpic";
				q_note = Common.getMessage(request, "cp_note_click_pic", "space.jsp?uid=" + itemUid
						+ "&do=album&picid=" + item.get("picid"));
			}
			if (Common.empty(item.get("friend")) && Common.ckPrivacy(sGlobal, sConfig, space, "click", 1)) {
				cpService.addFeed(sGlobal, "click", (String) fs.get("title_template"), (Map) fs
						.get("title_data"), "", new HashMap(), (String) fs.get("body_general"), (String[]) fs
						.get("images"), (String[]) fs.get("image_links"), "", 0, 0, id, idtype, false);
			}
			Common.getReward("click", true, 0, idtype + id, true, request, response);
			cpService.updateStat(request, "click", false);
			cpService.addNotification(request, sGlobal, sConfig, itemUid, note_type, q_note, false);

			return showMessage(request, response, "click_success", (String) sGlobal.get("refer"));
		} else if ("show".equals(op)) {

			Map<String, Object> value_;
			int key;
			Integer clicknum;
			int maxclicknum = 0;
			for (Entry<Integer, Map<String, Object>> key_value : clicks.entrySet()) {
				key = key_value.getKey();
				value_ = key_value.getValue();
				if (value_ == null) {
					value_ = new HashMap<String, Object>();
					clicks.put(key, value_);
				}
				clicknum = (Integer) item.get("click_" + key);
				clicknum = clicknum == null ? 0 : clicknum;
				value_.put("clicknum", clicknum);
				value_.put("classid", Common.rand(1, 4));
				if (clicknum > maxclicknum) {
					maxclicknum = clicknum;
				}
			}
			request.setAttribute("maxclicknum", maxclicknum);

			tempS = request.getParameter("start");
			int start = Common.intval(tempS);
			if (start < 0)
				start = 0;
			int perpage = 18;

			int count = 0;

			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("clickuser")
					+ " " + "WHERE id='" + id + "' AND idtype='" + idtype + "' " + "ORDER BY dateline DESC "
					+ "LIMIT " + start + "," + perpage);
			for (Map<String, Object> value : query) {
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
						.get("username"), "", 0);
				value.put("clickname", clicks.get(value.get("clickid")).get("name"));
				count++;
			}
			request.setAttribute("clickuserlist", query);

			Common.realname_get(sGlobal, sConfig, sNames, space);

			String click_multi;
			try {
				click_multi = Common.smulti(sGlobal, start, perpage, count,
						"cp.jsp?ac=click&op=show&clickid=" + clickid + "&idtype=" + idtype + "&id=" + id,
						"click_div");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("click_multi", click_multi);
		}

		request.setAttribute("clicks", clicks);
		request.setAttribute("hash", hash);
		request.setAttribute("idtype", idtype);
		request.setAttribute("id", id);
		request.setAttribute("op", op);

		request.setAttribute("navtitle", "好友 - ");
		return include(request, response, sConfig, sGlobal, "cp_click.jsp");
	}

	
	public ActionForward cp_comment(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		try {
			Map toSpace = null, pic = null, blog = null, album = null, share = null, event = null, poll = null;
			int cid = Common.empty(request.getParameter("cid")) ? 0 : Common.intval(request
					.getParameter("cid"));
			int supeUid = (Integer) sGlobal.get("supe_uid");
			if (submitCheck(request, "commentsubmit")) {
				
				if (!Common.checkPerm(request, response, "allowcomment")) {
					MessageVO msgVO = Common.ckSpaceLog(request);
					if (msgVO != null) {
						return showMessage(request, response, msgVO);
					}
					return showMessage(request, response, "no_privilege");
				}
				if (!cpService.checkRealName(request, "comment")) {
					return showMessage(request, response, "no_privilege_realname");
				}
				switch (cpService.checkNewUser(request, response)) {
					case 1:
						break;
					case 2:
						return showMessage(request, response, "no_privilege_newusertime", "", 1, String
								.valueOf(sConfig.get("newusertime")));
					case 3:
						return showMessage(request, response, "no_privilege_avatar");
					case 4:
						return showMessage(request, response, "no_privilege_friendnum", "", 1, String
								.valueOf(sConfig.get("need_friendnum")));
					case 5:
						return showMessage(request, response, "no_privilege_email");
				}
				int waitTime = Common.checkInterval(request, response, "post");
				if (waitTime > 0) {
					return showMessage(request, response, "operating_too_fast", "", 1, String
							.valueOf(waitTime));
				}

				String idType = request.getParameter("idtype");
				String message = Common.getStr(request.getParameter("message"), 0, true, true, true, 2, 0,
						request, response);
				if (message.length() < 2) {
					return showMessage(request, response, "content_is_too_short");
				}
				String summay = Common.getStr(message, 150, true, true, false, 0, -1, request, response);
				int id = Common.intval(request.getParameter("id"));

				int authorId = 0;
				Map<Integer, String> sn = (Map<Integer, String>) request.getAttribute("sNames");
				Map comment = null;
				Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
				if (cid != 0) {
					List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("comment") + " WHERE cid='" + cid + "' AND id='"
							+ id + "' AND idtype='" + idType + "'");
					comment = query.size() == 0 ? new HashMap() : query.get(0);
					authorId = (Integer) comment.get("authorid");
					if (comment.size() != 0 && authorId != supeUid) {
						if ("".equals(comment.get("author"))) {
							sn.put(authorId, Common.getMessage(request, "hidden_username"));
						} else {

							Common.realname_set(sGlobal, sConfig, sn, authorId, (String) comment
									.get("author"), "", 0);
							Common.realname_get(sGlobal, sConfig, sn, space);
						}
						comment.put("message", comment.get("message").toString().replaceAll(
								"(?is)<div class=\"quote\"><span class=\"q\">.*?</span></div>", ""));
						comment.put("message", BBCode.html2bbcode((String) comment.get("message")));
						message = Common.addSlashes("<div class=\"quote\"><span class=\"q\"><b>"
								+ sn.get(authorId)
								+ "</b>: "
								+ Common.getStr((String) comment.get("message"), 150, false, false, false, 2,
										1, request, response) + "</span></div>")
								+ message;
						if ("uid".equals(comment.get("idtype"))) {
							id = authorId;
						}
					}
				}

				List hotarr = new ArrayList();
				String statType = "";

				if ("uid".equals(idType)) {
					toSpace = Common.getSpace(request, sGlobal, sConfig, id);
					statType = "wall";
				} else if ("picid".equals(idType)) {
					List<Map<String, Object>> query = dataBaseService
							.executeQuery("SELECT p.*, pf.hotuser FROM " + JavaCenterHome.getTableName("pic")
									+ " p LEFT JOIN " + JavaCenterHome.getTableName("picfield")
									+ " pf ON pf.picid=p.picid WHERE p.picid='" + id + "'");
					pic = query.size() == 0 ? new HashMap() : query.get(0);
					if (pic.size() == 0) {
						return showMessage(request, response, "view_images_do_not_exist");
					}
					toSpace = Common.getSpace(request, sGlobal, sConfig, pic.get("uid"));
					album = new HashMap();

					if (!Common.empty(pic.get("albumid"))) {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("album") + " WHERE albumid='"
								+ pic.get("albumid") + "'");
						if (query.size() == 0) {
							Map set = new HashMap();
							set.put("albumid", 0);
							Map where = new HashMap();
							where.put("albumid", pic.get("albumid"));
							dataBaseService.updateTable("pic", set, where);
						} else {
							album = query.get(0);
						}
					}
					if (Common.empty(album)) {
						album.put("friend", 0);
					}
					int friend = (Integer) album.get("friend");
					if (!Common.ckFriend(sGlobal, space, Common.intval(String.valueOf(album.get("uid"))),
							friend, (String) album.get("target_ids"))) {
						return showMessage(request, response, "no_privilege");
					} else if (Common.empty(toSpace.get("self")) && friend == 4) {
						Map<String, Object> sCookie = (Map<String, Object>) request.getAttribute("sCookie");
						String cookieName = "view_pwd_album_" + album.get("albumid");
						String cookieValue = Common.empty(sCookie.get("cookiename")) ? "" : (String) sCookie
								.get("cookiename");
						if (!cookieValue.equals(Common.md5(Common.md5((String) album.get("password"))))) {
							return showMessage(request, response, "no_privilege");
						}
					}
					hotarr.add("picid");
					hotarr.add(pic.get("picid"));
					hotarr.add(pic.get("hotuser"));
					statType = "piccomment";
				} else if ("blogid".equals(idType)) {
					List<Map<String, Object>> query = dataBaseService
							.executeQuery("SELECT b.*, bf.target_ids, bf.hotuser FROM "
									+ JavaCenterHome.getTableName("blog") + " b LEFT JOIN "
									+ JavaCenterHome.getTableName("blogfield")
									+ " bf ON bf.blogid=b.blogid WHERE b.blogid='" + id + "'");
					blog = query.size() == 0 ? new HashMap() : query.get(0);
					if (blog.size() == 0) {
						return showMessage(request, response, "view_to_info_did_not_exist");
					}
					toSpace = Common.getSpace(request, sGlobal, sConfig, blog.get("uid"));
					if (!Common.ckFriend(sGlobal, space, (Integer) blog.get("uid"), (Integer) blog
							.get("friend"), (String) blog.get("target_ids"))) {
						return showMessage(request, response, "no_privilege");
					} else if (Common.empty(toSpace.get("self")) && (Integer) blog.get("friend") == 4) {
						Map<String, Object> sCookie = (Map<String, Object>) request.getAttribute("sCookie");
						String cookieName = "view_pwd_blog_" + blog.get("blogid");
						String cookieValue = Common.empty(sCookie.get("cookiename")) ? "" : (String) sCookie
								.get("cookiename");
						if (!cookieValue.equals(Common.md5(Common.md5((String) blog.get("password"))))) {
							return showMessage(request, response, "no_privilege");
						}
					}
					if (!Common.empty(blog.get("noreply"))) {
						return showMessage(request, response, "do_not_accept_comments");
					}
					if (!Common.empty(blog.get("target_ids"))) {
						blog.put("target_ids", blog.get("target_ids") + "," + blog.get("uid"));
					}
					hotarr.add("blogid");
					hotarr.add(blog.get("blogid"));
					hotarr.add(blog.get("hotuser"));
					statType = "blogcomment";
				} else if ("sid".equals(idType)) {
					List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("share") + " WHERE sid='" + id + "'");
					share = query.size() == 0 ? new HashMap() : query.get(0);
					if (share.size() == 0) {
						return showMessage(request, response, "sharing_does_not_exist");
					}
					toSpace = Common.getSpace(request, sGlobal, sConfig, share.get("uid"));
					hotarr.add("sid");
					hotarr.add(share.get("sid"));
					hotarr.add(share.get("hotuser"));
					statType = "sharecomment";
				} else if ("pid".equals(idType)) {
					List<Map<String, Object>> query = dataBaseService
							.executeQuery("SELECT p.*, pf.hotuser FROM "
									+ JavaCenterHome.getTableName("poll") + " p LEFT JOIN "
									+ JavaCenterHome.getTableName("pollfield")
									+ " pf ON pf.pid=p.pid WHERE p.pid='" + id + "'");
					poll = query.size() == 0 ? new HashMap() : query.get(0);
					if (poll.size() == 0) {
						return showMessage(request, response, "voting_does_not_exist");
					}
					toSpace = Common.getSpace(request, sGlobal, sConfig, poll.get("uid"));
					if (!Common.empty(poll.get("noreply"))) {
						if (Common.empty(toSpace.get("self"))
								&& !Common.in_array((String[]) toSpace.get("friends"), sGlobal
										.get("supe_uid"))) {
							return showMessage(request, response, "the_vote_only_allows_friends_to_comment");
						}
					}
					hotarr.add("pid");
					hotarr.add(poll.get("pid"));
					hotarr.add(poll.get("hotuser"));
					statType = "pollcomment";
				} else if ("eventid".equals(idType)) {
					List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT e.*, ef.* FROM "
							+ JavaCenterHome.getTableName("event") + " e LEFT JOIN "
							+ JavaCenterHome.getTableName("eventfield")
							+ " ef ON e.eventid=ef.eventid WHERE e.eventid='" + id + "'");
					event = query.size() == 0 ? new HashMap() : query.get(0);
					if (event.size() == 0) {
						return showMessage(request, response, "event_does_not_exist");
					}
					if ((Integer) event.get("grade") < -1) {
						return showMessage(request, response, "event_is_closed");
					} else if ((Integer) event.get("grade") <= 0) {
						return showMessage(request, response, "event_under_verify");
					}
					if (Common.empty(event.get("allowpost"))) {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("userevent") + " WHERE eventid='" + id
								+ "' AND uid='" + sGlobal.get("supe_uid") + "' LIMIT 1");
						Map value = query.size() == 0 ? null : query.get(0);
						if (value == null || value.size() == 0 || (Integer) value.get("status") < 2) {
							return showMessage(request, response, "event_only_allows_members_to_comment");
						}
					}
					toSpace = Common.getSpace(request, sGlobal, sConfig, event.get("uid"));
					hotarr.add("eventid");
					hotarr.add(event.get("eventid"));
					hotarr.add(event.get("hotuser"));
					statType = "eventcomment";
				} else {
					return showMessage(request, response, "non_normal_operation");
				}

				if (Common.empty(toSpace)) {
					return showMessage(request, response, "space_does_not_exist");
				}
				if ((Integer) toSpace.get("videostatus") == 1) {
					if ("uid".equals(idType)
							&& !cpService.checkVideoPhoto(request, response, "wall", toSpace)) {
						return showMessage(request, response, "no_privilege_videophoto");
					} else if (!cpService.checkVideoPhoto(request, response, "comment")) {
						return showMessage(request, response, "no_privilege_videophoto");
					}
				}
				int toSpaceUid = (Integer) toSpace.get("uid");
				if (cpService.isBlackList(toSpaceUid, supeUid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				if (hotarr.size() != 0 && toSpaceUid != supeUid) {
					cpService.updateHot(request, response, (String) hotarr.get(0), (Integer) hotarr.get(1),
							(String) hotarr.get(2));
				}
				Map fs = new HashMap();
				fs.put("icon", "comment");
				fs.put("target_ids", "");
				fs.put("friend", 0);
				if ("uid".equals(idType)) {
					Map titleData = new HashMap();
					titleData.put("touser", "<a href=\"space.jsp?uid=" + toSpaceUid + "\">"
							+ sn.get(toSpaceUid) + "</a>");
					fs.put("icon", "wall");
					fs.put("title_template", Common.getMessage(request, "cp_feed_comment_space"));
					fs.put("title_data", titleData);
					fs.put("body_template", "");
					fs.put("body_data", null);
					fs.put("body_general", "");
					fs.put("images", null);
					fs.put("image_links", null);
				} else if ("picid".equals(idType)) {
					Map titleData = new HashMap();
					titleData.put("touser", "<a href=\"space.jsp?uid=" + toSpaceUid + "\">"
							+ sn.get(toSpaceUid) + "</a>");
					Map bodyData = new HashMap();
					bodyData.put("pic_title", pic.get("title"));
					String[] images = {Common.pic_get(sConfig, (String) pic.get("filepath"), (Integer) pic
							.get("thumb"), (Integer) pic.get("remote"), true)};
					String[] imageLinks = {"space.jsp?uid=" + toSpaceUid + "&do=album&picid="
							+ pic.get("picid")};
					fs.put("title_template", Common.getMessage(request, "cp_feed_comment_image"));
					fs.put("title_data", titleData);
					fs.put("body_template", "{pic_title}");
					fs.put("body_data", bodyData);
					fs.put("body_general", summay);
					fs.put("images", images);
					fs.put("image_links", imageLinks);
					fs.put("target_ids", album.get("target_ids"));
					fs.put("friend", album.get("friend"));
				} else if ("blogid".equals(idType)) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("blog")
							+ " SET replynum=replynum+1 WHERE blogid='" + id + "'");
					Map titleData = new HashMap();
					titleData.put("touser", "<a href=\"space.jsp?uid=" + toSpaceUid + "\">"
							+ sn.get(toSpaceUid) + "</a>");
					titleData.put("blog", "<a href=\"space.jsp?uid=" + toSpaceUid + "&do=blog&id=" + id
							+ "\">" + blog.get("subject") + "</a>");
					fs.put("title_template", Common.getMessage(request, "cp_feed_comment_blog"));
					fs.put("title_data", titleData);
					fs.put("body_template", "");
					fs.put("body_data", null);
					fs.put("body_general", "");
					fs.put("target_ids", blog.get("target_ids"));
					fs.put("friend", blog.get("friend"));
				} else if ("sid".equals(idType)) {
					Map titleData = new HashMap();
					titleData.put("touser", "<a href=\"space.jsp?uid=" + toSpaceUid + "\">"
							+ sn.get(toSpaceUid) + "</a>");
					titleData.put("share", "<a href=\"space.jsp?uid="
							+ toSpaceUid
							+ "&do=share&id="
							+ id
							+ "\">"
							+ ((String) share.get("title_template")).replace(Common.getMessage(request,
									"cp_share_action"), "") + "</a>");
					fs.put("title_template", Common.getMessage(request, "cp_feed_comment_share"));
					fs.put("title_data", titleData);
					fs.put("body_template", "");
					fs.put("body_data", null);
					fs.put("body_general", "");
				} else if ("eventid".equals(idType)) {
					Map titleData = new HashMap();
					titleData.put("touser", "<a href=\"space.jsp?uid=" + toSpaceUid + "\">"
							+ sn.get(toSpaceUid) + "</a>");
					titleData.put("event", "<a href=\"space.jsp?do=event&id=" + event.get("eventid") + "\">"
							+ event.get("title") + "</a>");
					fs.put("title_template", Common.getMessage(request, "cp_feed_comment_event"));
					fs.put("title_data", titleData);
					fs.put("body_template", "");
					fs.put("body_data", null);
					fs.put("body_general", "");
				} else if ("pid".equals(idType)) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("poll")
							+ " SET replynum=replynum+1 WHERE pid='" + id + "'");
					Map titleData = new HashMap();
					titleData.put("touser", "<a href=\"space.jsp?uid=" + toSpaceUid + "\">"
							+ sn.get(toSpaceUid) + "</a>");
					titleData.put("poll", "<a href=\"space.jsp?uid=" + toSpaceUid + "&do=poll&pid=" + id
							+ "\">" + poll.get("subject") + "</a>");
					fs.put("title_template", Common.getMessage(request, "cp_feed_comment_poll"));
					fs.put("title_data", titleData);
					fs.put("body_template", "");
					fs.put("body_data", null);
					fs.put("body_general", "");
					fs.put("friend", 0);
				}
				Map setarr = new HashMap();
				setarr.put("uid", toSpaceUid);
				setarr.put("id", id);
				setarr.put("idtype", request.getParameter("idtype"));
				setarr.put("authorid", sGlobal.get("supe_uid"));
				setarr.put("author", sGlobal.get("supe_username"));
				setarr.put("dateline", sGlobal.get("timestamp"));
				setarr.put("message", message);
				setarr.put("ip", Common.getOnlineIP(request));
				cid = dataBaseService.insertTable("comment", setarr, true, false);

				String action = "comment";
				String becomment = "getcomment";
				String msg = null;
				String magValues = null;
				String noteType = null;
				String note = null;
				String msgType = null;
				String nUrl = null;
				String qMsgType = null;
				String qNote = null;
				if ("uid".equals(idType)) {
					nUrl = "space.jsp?uid=" + toSpaceUid + "&do=wall&cid=" + cid;
					noteType = "wall";
					note = Common.getMessage(request, "cp_note_wall", nUrl);
					qNote = Common.getMessage(request, "cp_note_wall_reply", nUrl);
					if (comment != null && comment.isEmpty() == false) {
						msg = "note_wall_reply_success";
						magValues = (String) sn.get(toSpaceUid);
						becomment = "";
					} else {
						msg = "do_success";
						magValues = null;
						becomment = "getguestbook";
					}
					msgType = "cp_comment_friend";
					qMsgType = "cp_comment_friend_reply";
					action = "guestbook";
				} else if ("picid".equals(idType)) {
					nUrl = "space.jsp?uid=" + toSpaceUid + "&do=album&picid=" + id + "&cid=" + cid;
					noteType = "piccomment";
					note = Common.getMessage(request, "cp_note_pic_comment", nUrl);
					qNote = Common.getMessage(request, "cp_note_pic_comment_reply", nUrl);
					msg = "do_success";
					magValues = null;
					msgType = "cp_photo_comment";
					qMsgType = "cp_photo_comment_reply";
				} else if ("blogid".equals(idType)) {
					nUrl = "space.jsp?uid=" + toSpaceUid + "&do=blog&id=" + id + "&cid=" + cid;
					noteType = "blogcomment";
					note = Common.getMessage(request, "cp_note_blog_comment", new String[] {nUrl,
							(String) blog.get("subject")});
					qNote = Common.getMessage(request, "cp_note_blog_comment_reply", nUrl);
					msg = "do_success";
					magValues = null;
					msgType = "cp_blog_comment";
					qMsgType = "cp_blog_comment_reply";
				} else if ("sid".equals(idType)) {
					nUrl = "space.jsp?uid=" + toSpaceUid + "&do=share&id=" + id + "&cid=" + cid;
					noteType = "sharecomment";
					note = Common.getMessage(request, "cp_note_share_comment", nUrl);
					qNote = Common.getMessage(request, "cp_note_share_comment_reply", nUrl);
					msg = "do_success";
					magValues = null;
					msgType = "cp_share_comment";
					qMsgType = "cp_share_comment_reply";
				} else if ("pid".equals(idType)) {
					nUrl = "space.jsp?uid=" + toSpaceUid + "&do=poll&pid=" + id + "&cid=" + cid;
					noteType = "pollcomment";
					note = Common.getMessage(request, "cp_note_poll_comment", new String[] {nUrl,
							(String) poll.get("subject")});
					qNote = Common.getMessage(request, "cp_note_poll_comment_reply", nUrl);
					msg = "do_success";
					magValues = null;
					msgType = "cp_poll_comment";
					qMsgType = "cp_poll_comment_reply";
				} else if ("eventid".equals(idType)) {
					nUrl = "space.jsp?do=event&id=" + id + "&view=comment&cid=" + cid;
					noteType = "eventcomment";
					note = Common.getMessage(request, "cp_note_event_comment", nUrl);
					qNote = Common.getMessage(request, "cp_note_event_comment_reply", nUrl);
					msg = "do_success";
					magValues = null;
					msgType = "cp_event_comment";
					qMsgType = "cp_event_comment_reply";
				}

				if (comment == null || comment.isEmpty()) {
					if (toSpaceUid != supeUid) {
						if (Common.ckPrivacy(sGlobal, sConfig, space, "comment", 1)) {
							cpService.addFeed(sGlobal, (String) fs.get("icon"), (String) fs
									.get("title_template"), (Map) fs.get("title_data"), (String) fs
									.get("body_template"), (Map) fs.get("body_data"), (String) fs
									.get("body_general"), (String[]) fs.get("images"), (String[]) fs
									.get("image_links"), (String) fs.get("target_ids"), (Integer) fs
									.get("friend"), 0, id, idType, false);
						}
						cpService.addNotification(request, sGlobal, sConfig, toSpaceUid, noteType, note,
								false);
						if ("uid".equals(idType)
								&& (Integer) toSpace.get("updatetime") == (Integer) toSpace.get("dataline")) {
						}
						String[] args = new String[] {(String) sn.get(space.get("uid")),
								(String) Common.sHtmlSpecialChars(Common.getSiteUrl(request) + nUrl)};
						cpService.sendMail(request, response, toSpaceUid, "", Common.getMessage(request,
								msgType, args), "", msgType);
					}
				} else if (authorId != supeUid) {
					String[] args = new String[] {(String) sn.get(space.get("uid")),
							(String) Common.sHtmlSpecialChars(Common.getSiteUrl(request) + nUrl)};
					cpService.sendMail(request, response, authorId, "", Common.getMessage(request, qMsgType,
							args), "", qMsgType);
					cpService.addNotification(request, sGlobal, sConfig, authorId, noteType,
							qNote == null ? "" : qNote, false);
				}
				if (!Common.empty(statType)) {
					cpService.updateStat(request, statType, false);
				}
				if (toSpaceUid != supeUid) {
					String needle = String.valueOf(id);
					if ("uid".equals(idType) == false) {
						needle = idType + id;
					} else {
						needle = String.valueOf(toSpaceUid);
					}
					Common.getReward(action, true, 0, needle, true, request, response);
					if (!Common.empty(becomment)) {
						if ("uid".equals(idType)) {
							needle = String.valueOf(supeUid);
						}
						Common.getReward(becomment, true, toSpaceUid, needle, false, request, response);
					}
				}
				return showMessage(request, response, msg, request.getParameter("refer"), 0, magValues);
			}

			String op = request.getParameter("op");
			if ("edit".equals(op)) {
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("comment") + " WHERE cid='" + cid + "' AND authorid='"
						+ supeUid + "'");
				Map comment = query.size() == 0 ? null : query.get(0);
				if (comment == null) {
					return showMessage(request, response, "no_privilege");
				}
				if (submitCheck(request, "editsubmit")) {
					String message = Common.getStr(request.getParameter("message"), 0, true, true, true, 2,
							0, request, response);
					if (message.length() < 2) {
						return showMessage(request, response, "content_is_too_short");
					}
					Map set = new HashMap();
					set.put("message", message);
					Map where = new HashMap();
					where.put("cid", comment.get("cid"));
					dataBaseService.updateTable("comment", set, where);
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
				comment.put("message", BBCode.html2bbcode((String) comment.get("message")));
				request.setAttribute("comment", comment);
			} else if ("delete".equals(op)) {
				if (submitCheck(request, "deletesubmit")) {
					AdminDeleteService ads = new AdminDeleteService();
					if (ads.deleteComments(request, response, supeUid, cid)) {
						return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
					} else {
						return showMessage(request, response, "no_privilege");
					}
				}
			} else if ("reply".equals(op)) {
				
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("comment") + " WHERE cid='" + cid + "'");
				Map comment = query.size() == 0 ? null : query.get(0);
				if (comment == null) {
					return showMessage(request, response, "comments_do_not_exist");
				}
				request.setAttribute("comment", comment);
			} else {
				return showMessage(request, response, "no_privilege");
			}

			request.setAttribute("cid", cid);
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}

		request.setAttribute("navtitle", "好友 - ");
		return include(request, response, sConfig, sGlobal, "cp_comment.jsp");
	}

	
	public ActionForward cp_common(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String op = Common.trim(request.getParameter("op"));
		if ("logout".equals(op)) {
			if (sGlobal.get("uhash").equals(request.getParameter("uhash"))) {
				int supe_uid = (Integer) sGlobal.get("supe_uid");
				if (supe_uid > 0) {
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("session")
							+ " WHERE uid=" + supe_uid);
					dataBaseService.executeUpdate("DELETE FROM "
							+ JavaCenterHome.getTableName("adminsession") + " WHERE uid=" + supe_uid);
				}
				CookieHelper.clearCookie(request, response);
				CookieHelper.removeCookie(request, response, "_refer");
			}
			return showMessage(request, response, "security_exit", "index.jsp", 1, "");
		} else if ("seccode".equals(op)) {
			if (cpService.checkSeccode(request, response, sGlobal, sConfig, request.getParameter("code"))) {
				return showMessage(request, response, "succeed");
			} else {
				return showMessage(request, response, "incorrect_code");
			}
		} else if ("report".equals(op)) {
			
			String idType = Common.trim(request.getParameter("idtype"));
			int id = Common.intval(request.getParameter("id"));
			String[] idTypes = {"picid", "blogid", "albumid", "tagid", "tid", "sid", "uid", "pid", "eventid",
					"comment", "post"};
			if (!Common.in_array(idTypes, idType)) {
				return showMessage(request, response, "report_error");
			}
			Map space = (Map) request.getAttribute("space");
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("report") + " WHERE id='" + id + "' AND idtype='" + idType
					+ "'");
			Map uidArr = null;
			Map<String, Object> report = null;
			if (query.size() > 0) {
				report = query.get(0);
				uidArr = Serializer.unserialize((String) report.get("uids"), false);
				if (!Common.empty(uidArr.get(space.get("uid")))) {
					return showMessage(request, response, "repeat_report");
				}
			} else {
				uidArr = new HashMap();
				report = new HashMap<String, Object>();
			}
			try {
				if (submitCheck(request, "reportsubmit")) {
					String reason = Common.getStr(request.getParameter("reason"), 150, true, true, false, 0,
							0, request, response);
					reason = "<li><strong><a href=\"space.jsp?uid=" + space.get("uid")
							+ "\" target=\"_blank\">" + sGlobal.get("supe_username") + "</a>:</strong> "
							+ reason + " ("
							+ Common.sgmdate(request, "MM-dd HH:mm", (Integer) sGlobal.get("timestamp"))
							+ ")</li>";
					uidArr.put(space.get("uid"), space.get("username"));
					String uids = Common.addSlashes(Serializer.serialize(uidArr));
					if (Common.empty(report)) {
						Map<String, Object> setarr = new HashMap<String, Object>();
						setarr.put("id", id<0 ? 0 : id);
						setarr.put("idtype", idType);
						setarr.put("num", 1);
						setarr.put("new", 1);
						setarr.put("reason", reason);
						setarr.put("uids", uids);
						setarr.put("dateline", sGlobal.get("timestamp"));
						dataBaseService.insertTable("report", setarr, false, false);
					} else {
						reason = Common.addSlashes((String) report.get("reason")) + reason;
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("report")
								+ " SET num=num+1, reason='" + reason + "', dateline='"
								+ sGlobal.get("timestamp") + "', uids='" + uids + "' WHERE rid='"
								+ report.get("rid") + "'");
					}
					return showMessage(request, response, "report_success");
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			Integer num = (Integer) report.get("num");
			if (num != null && num < 1) {
				return showMessage(request, response, "the_normal_information");
			}
			String reason = Common.getData("reason");
			if(reason != null) {
				String[] reasonArr = reason.replaceAll("(\\s*(\r\n|\n\r|\n|\r)\\s*)", "\r\n").trim().split("\r\n");
				if(Common.isArray(reasonArr) && reasonArr.length == 1 && Common.empty(reasonArr[0])) {
					reasonArr = null;
				}
				request.setAttribute("reason", reasonArr);
			}
			
			request.setAttribute("idType", idType);
			request.setAttribute("id", id);
		} else if ("ignore".equals(op)) {
			String type = Common.trim(request.getParameter("type")).replaceAll("[^0-9a-zA-Z\\_\\-\\.]", "");
			
			try {
				if (submitCheck(request, "ignoresubmit")) {
					int authorId = Common.intval(request.getParameter("authorid"));
					if (!Common.empty(type)) {
						Map space = (Map) request.getAttribute("space");
						Map privacy = (Map) space.get("privacy");
						String typeUid = type + "|" + authorId;
						if (Common.empty(privacy.get("filter_note"))
								|| !Common.isArray(privacy.get("filter_note"))) {
							privacy.put("filter_note", new HashMap());
						}
						Map filterNote = (Map) privacy.get("filter_note");
						filterNote.put(typeUid, typeUid);
						cpService.privacyUpdate(privacy, (Integer) sGlobal.get("supe_uid"));
					}
					return showMessage(request, response, "do_success", request.getParameter("refer"));
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			String formId = Common.getRandStr(8, false);
			request.setAttribute("formid", formId);
			request.setAttribute("type", type);
		} else if ("getuserapp".equals(op)) {
			ArrayList myUserApp = new ArrayList();
			if (Common.empty(request.getParameter("subop"))) {
				Iterator it = ((Map) sGlobal.get("my_userapp")).keySet().iterator();
				Map userApp = (Map) sGlobal.get("userapp");
				while (it.hasNext()) {
					Map value = (Map) it.next();
					if (!Common.empty(value.get("allowsidenav")) && userApp.containsKey(value.get("appid"))) {
						myUserApp.add(value);
					}
				}
			} else {
				myUserApp = (ArrayList) sGlobal.get("my_menu");
			}
			request.setAttribute("my_userapp", myUserApp);
		} else if ("closefeedbox".equals(op)) {
			CookieHelper.setCookie(request, response, "closefeedbox", "1");
		} else if ("changetpl".equals(op)) {
			String dir = Common.trim(request.getParameter("name")).replace(".", "");
			if (!Common.empty(dir)) {
				File file = new File(JavaCenterHome.jchRoot + "/template/" + dir + "/style.css");
				if (file.exists()) {
					CookieHelper.setCookie(request, response, "mytemplate", dir, 365 * 24 * 3600);
				}
			}
			return showMessage(request, response, "do_success", "space.jsp?do=home", 0);
		}
		return include(request, response, sConfig, sGlobal, "cp_common.jsp");
	}

	
	public ActionForward cp_credit(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");

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

		String op = request.getParameter("op");
		if (Common.empty(op)) {
			op = "base";
		}
		if (op.equals("base")) {
			String maxAttachSizeStr = null;
			int maxAttachSize = (Integer) Common.checkPerm(request, response, sGlobal, "maxattachsize");
			int percent = 0;
			if (maxAttachSize == 0) {
				maxAttachSizeStr = "-";
			} else {
				maxAttachSize = maxAttachSize + (Integer) space.get("addsize");
				percent = (int) (((Integer) space.get("attachsize")) / (float) maxAttachSize * 100);
				maxAttachSizeStr = Common.formatSize(maxAttachSize).replaceFirst("\\.\\d*", "");
			}
			space.put("attachsize", Common.formatSize((Integer) space.get("attachsize")));
			space.put("grouptitle", Common.checkPerm(request, response, sGlobal, "grouptitle"));

			String theUrl = "cp.jsp?ac=credit&perpage=" + perPage;
			String t_creditlog = JavaCenterHome.getTableName("creditlog");
			Object spaceUid = space.get("uid");
			int count = dataBaseService.findRows("SELECT count(*) FROM " + t_creditlog + " WHERE uid='"
					+ spaceUid + "'");
			if (count > 0) {
				String t_creditrule = JavaCenterHome.getTableName("creditrule");
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT r.rulename, c.* FROM "
						+ t_creditlog + " c LEFT JOIN " + t_creditrule + " r ON r.rid=c.rid WHERE c.uid='"
						+ spaceUid + "' ORDER BY dateline DESC LIMIT " + start + "," + perPage);
				String format = "MM-dd HH:mm";
				for (Map<String, Object> value : list) {
					value.put("dateline", Common.sgmdate(request, format, (Integer) value.get("dateline")));
				}
				String multi = Common.multi(request, count, perPage, page, maxPage, theUrl, null, null);
				request.setAttribute("list", list);
				request.setAttribute("multi", multi);
			}
			int groupId = (Integer) space.get("groupid");
			String star = Common.getStar(sConfig, (Integer) space.get("experience"));
			String color = Common.getColor(request, response, groupId);
			String icon = Common.getIcon(request, response, groupId);
			String format = "yyyy-MM-dd";
			String dateline = Common.sgmdate(request, format, (Integer) space.get("dateline"), true);
			String lastLogin = Common.sgmdate(request, format, (Integer) space.get("lastlogin"), true);
			String updateTime = Common.sgmdate(request, format, (Integer) space.get("updatetime"), true);

			request.setAttribute("star", star);
			request.setAttribute("color", color);
			request.setAttribute("icon", icon);
			request.setAttribute("maxattachsize", maxAttachSizeStr);
			request.setAttribute("percent", percent);
			request.setAttribute("dateline", dateline);
			request.setAttribute("lastlogin", lastLogin);
			request.setAttribute("updatetime", updateTime);

		} else if (op.equals("exchange")) {
			return showMessage(request, response, "integral_convertible_unopened");
		} else if (op.equals("rule")) {
			List wherearr = new ArrayList();

			String theUrl = "cp.jsp?ac=credit&op=rule&perpage=" + perPage;
			Map perPages = new HashMap();
			perPages.put(String.valueOf(perPage), " selected");

			String rid = request.getParameter("rid");
			if (rid != null && rid.trim().length() != 0) {
				wherearr.add("rid='" + Common.intval(rid) + "'");
			}

			String rewardType = request.getParameter("rewardtype");
			if (rewardType != null) {
				int rewardTypeInt = Common.intval(rewardType);
				wherearr.add("rewardtype='" + rewardTypeInt + "'");
				theUrl += "&rewardtype=" + rewardTypeInt;
			}

			String whereSql = "";
			if (wherearr.isEmpty() == false) {
				whereSql = " WHERE " + Common.implode(wherearr, " AND ");
			}
			String[] cycleTypes = {"一次性", "每天", "整点", "间隔分钟", "不限周期"};
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("creditrule") + " " + whereSql + " ORDER BY rid DESC");
			ArrayList list = new ArrayList();
			ArrayList list2 = new ArrayList();
			for (Map<String, Object> value : query) {
				if ((Integer) value.get("rewardtype") == 1) {
					value.put("cycletype", cycleTypes[(Integer) value.get("cycletype")]);
					list.add(value);
				} else {
					list2.add(value);
				}
			}

			request.setAttribute("list", list);
			request.setAttribute("list2", list2);

		} else if (op.equals("usergroup")) {
			space.put("grouptitle", Common.checkPerm(request, response, sGlobal, "grouptitle"));

			ArrayList groups = new ArrayList();
			ArrayList sGroups = new ArrayList();
			boolean highest = true;
			int lower = 0;

			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("usergroup") + " ORDER BY explower DESC");
			for (Map<String, Object> value : query) {
				int gid = (Integer) value.get("gid");
				value.put("color", Common.getColor(request, response, gid));
				value.put("icon", Common.getIcon(request, response, gid));
				if (Common.empty(value.get("system"))) {
					if (highest) {
						value.put("exphigher", 999999999);
						highest = false;
					} else {
						value.put("exphigher", lower - 1);
					}
					lower = (Integer) value.get("explower");
					groups.add(value);
				} else {
					sGroups.add(value);
				}
			}
			request.setAttribute("groups", groups);
			request.setAttribute("s_groups", sGroups);
		}

		request.setAttribute("cat_actives_" + op, " class=\"active\"");
		return include(request, response, sConfig, sGlobal, "cp_credit.jsp");
	}

	
	public ActionForward cp_doing(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");

		int doId = Common.intval(request.getParameter("doid"));
		int id = Common.intval(request.getParameter("id"));

		String refer = request.getParameter("refer");
		if (Common.empty(refer)) {
			refer = "space.jsp?do=doing&view=me";
		}

		try {
			if (submitCheck(request, "addsubmit")) {
				
				int addDoing = 1;
				String spaceNote = request.getParameter("spacenote");
				if (Common.empty(spaceNote)) {
					
					if (!Common.checkPerm(request, response, "allowdoing")) {
						MessageVO msgVO = Common.ckSpaceLog(request);
						if (msgVO != null) {
							return showMessage(request, response, msgVO);
						}
						return showMessage(request, response, "no_privilege");
					}
					if (!cpService.checkRealName(request, "doing")) {
						return showMessage(request, response, "no_privilege_realname");
					}
					if (!cpService.checkVideoPhoto(request, response, "doing")) {
						return showMessage(request, response, "no_privilege_videophoto");
					}
					switch (cpService.checkNewUser(request, response)) {
						case 1:
							break;
						case 2:
							return showMessage(request, response, "no_privilege_newusertime", "", 1, String
									.valueOf(sConfig.get("newusertime")));
						case 3:
							return showMessage(request, response, "no_privilege_avatar");
						case 4:
							return showMessage(request, response, "no_privilege_friendnum", "", 1, String
									.valueOf(sConfig.get("need_friendnum")));
						case 5:
							return showMessage(request, response, "no_privilege_email");
					}
					if (Common.checkPerm(request, response, "seccode")
							&& !cpService.checkSeccode(request, response, sGlobal, sConfig, request
									.getParameter("seccode"))) {
						return showMessage(request, response, "incorrect_code");
					}
					int waitTime = Common.checkInterval(request, response, "post");
					if (waitTime > 0) {
						return showMessage(request, response, "operating_too_fast", "", 1, waitTime);
					}
				} else {
					
					if (!Common.checkPerm(request, response, "allowdoing")) {
						addDoing = 0;
					}
					if (!cpService.checkRealName(request, "doing")) {
						addDoing = 0;
					}
					if (!cpService.checkVideoPhoto(request, response, "doing")) {
						addDoing = 0;
					}
					if (!(cpService.checkNewUser(request, response) == 1)) {
						addDoing = 0;
					}
					int waitTime = Common.checkInterval(request, response, "post");
					if (waitTime > 0) {
						addDoing = 0;
					}
				}
				String message = Common.trim(request.getParameter("message"));
				Matcher m = Pattern.compile("(?s)\\[em\\:(\\d+)\\:\\]").matcher(message);
				int mood = m.find() ? Common.intval(m.group(1)) : 0;
				message = Common.getStr(message, 200, true, true, true, 0, 0, request, response);
				message = message.replaceAll("(?is)\\[em:(\\d+):]",
						"<img src=\"image/face/$1.gif\" class=\"face\">");
				message = message.replaceAll("(?is)\\<br.*?\\>", " ");
				if (message.length() < 1) {
					return showMessage(request, response, "should_write_that");
				}

				Map setmap = new HashMap();

				int newDoId = 0;
				if (addDoing != 0) {
					
					setmap.put("uid", sGlobal.get("supe_uid"));
					setmap.put("username", sGlobal.get("supe_username"));
					setmap.put("dateline", sGlobal.get("timestamp"));
					setmap.put("message", message);
					setmap.put("mood", mood);
					setmap.put("ip", Common.getOnlineIP(request));
					newDoId = dataBaseService.insertTable("doing", setmap, true, false);
				}
				setmap = new HashMap();
				setmap.put("note", message);

				Map reward = null;
				if (!Common.empty(spaceNote)) {
					
					reward = Common.getReward("updatemood", false, 0, "", true, request, response);
					setmap.put("spacenote", message);
				} else {
					
					reward = Common.getReward("doing", false, 0, "", true, request, response);
				}

				Map where = new HashMap();
				where.put("uid", sGlobal.get("supe_uid"));
				dataBaseService.updateTable("spacefield", setmap, where);

				int credit = 0;
				int experience = 0;
				if (!Common.empty(reward.get("credit"))) {
					credit = (Integer) reward.get("credit");
				}
				if (!Common.empty(reward.get("experience"))) {
					experience = (Integer) reward.get("experience");
				}
				setmap = new HashMap();
				setmap.put("mood", "mood='" + mood + "'");
				setmap.put("updatetime", "updatetime='" + sGlobal.get("timestamp") + "'");
				setmap.put("credit", "credit=credit+" + credit);
				setmap.put("experience", "experience=experience+" + experience);
				setmap.put("lastpost", "lastpost='" + sGlobal.get("timestamp") + "'");
				if (addDoing != 0) {
					if (Common.empty(space.get("doingnum"))) {
						where = new HashMap();
						where.put("uid", space.get("uid"));
						int doingNum = Common.intval(Common.getCount("doing", where, null));
						setmap.put("doingnum", "doingnum='" + doingNum + "'");
					} else {
						setmap.put("doingnum", "doingnum=doingnum+1");
					}
				}
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
						+ Common.implode(setmap, ",") + " WHERE uid='" + sGlobal.get("supe_uid") + "'");
				if (addDoing != 0 && Common.ckPrivacy(sGlobal, sConfig, space, "doing", 1)) {
					Map messagemap = new HashMap();
					messagemap.put("message", message);
					Map feedmap = new HashMap();
					feedmap.put("appid", JavaCenterHome.jchConfig.get("JC_APPID"));
					feedmap.put("icon", "doing");
					feedmap.put("uid", sGlobal.get("supe_uid"));
					feedmap.put("username", sGlobal.get("supe_username"));
					feedmap.put("dateline", sGlobal.get("timestamp"));
					feedmap.put("title_template", Common.getMessage(request, "cp_feed_doing_title"));
					feedmap.put("title_data", Common.sAddSlashes(Serializer.serialize(Common
							.sStripSlashes(messagemap))));
					feedmap.put("body_template", "");
					feedmap.put("body_data", "");
					feedmap.put("body_general", "");
					feedmap.put("target_ids", "");
					feedmap.put("id", newDoId);
					feedmap.put("idtype", "doid");
					feedmap.put("hash_template", Common.md5(feedmap.get("title_template") + "\t"
							+ feedmap.get("body_template")));
					feedmap.put("hash_data", Common.md5(feedmap.get("title_template") + "\t"
							+ feedmap.get("title_data") + "\t" + feedmap.get("body_template") + "\t"
							+ feedmap.get("body_data")));
					dataBaseService.insertTable("feed", feedmap, false, false);
				}
				cpService.updateStat(request, "doing", false);
				return showMessage(request, response, "do_success", refer, 0);

			} else if (submitCheck(request, "commentsubmit")) {
				
				if (!Common.checkPerm(request, response, "allowdoing")) {
					MessageVO msgVO = Common.ckSpaceLog(request);
					if (msgVO != null) {
						return showMessage(request, response, msgVO);
					}
					return showMessage(request, response, "no_privilege");
				}
				if (!cpService.checkRealName(request, "doing")) {
					return showMessage(request, response, "no_privilege_realname");
				}
				switch (cpService.checkNewUser(request, response)) {
					case 1:
						break;
					case 2:
						return showMessage(request, response, "no_privilege_newusertime", "", 1, String
								.valueOf(sConfig.get("newusertime")));
					case 3:
						return showMessage(request, response, "no_privilege_avatar");
					case 4:
						return showMessage(request, response, "no_privilege_friendnum", "", 1, String
								.valueOf(sConfig.get("need_friendnum")));
					case 5:
						return showMessage(request, response, "no_privilege_email");
				}
				int waitTime = Common.checkInterval(request, response, "post");
				if (waitTime > 0) {
					return showMessage(request, response, "operating_too_fast", "", 1, String
							.valueOf(waitTime));
				}

				String message = Common.getStr(request.getParameter("message"), 200, true, true, true, 0, 0,
						request, response);
				message = message.replaceAll("(?is)\\[em:(\\d+):]",
						"<img src=\"image/face/$1.gif\" class=\"face\">");
				message = message.replaceAll("(?is)\\<br.*?\\>", " ");
				if (message.length() < 1) {
					return showMessage(request, response, "should_write_that");
				}
				Map updo = null;
				if (id != 0) {
					
					List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("docomment") + " WHERE id='" + id + "'");
					if (query.size() != 0) {
						updo = query.get(0);
					}
				}
				if (Common.empty(updo) && doId != 0) {
					List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("doing") + " WHERE doid='" + doId + "'");
					if (query.size() != 0) {
						updo = query.get(0);
					}
				}
				if (Common.empty(updo)) {
					return showMessage(request, response, "docomment_error");
				} else {
					if (cpService.isBlackList((Integer) updo.get("uid"), (Integer) sGlobal.get("supe_uid")) != 0) {
						return showMessage(request, response, "is_blacklist");
					}
				}

				Integer grade = (Integer) updo.get("grade");
				Integer tmpId = (Integer) updo.get("id");
				updo.put("grade", grade != null ? grade : 0);
				updo.put("id", tmpId != null ? tmpId : 0);

				Map setmap = new HashMap();
				setmap.put("doid", updo.get("doid"));
				setmap.put("upid", updo.get("id"));
				setmap.put("uid", sGlobal.get("supe_uid"));
				setmap.put("username", sGlobal.get("supe_username"));
				setmap.put("dateline", sGlobal.get("timestamp"));
				setmap.put("message", message);
				setmap.put("ip", Common.getOnlineIP(request));
				setmap.put("grade", (Integer) updo.get("grade") + 1);
				if ((Integer) updo.get("grade") >= 3) {
					setmap.put("upid", updo.get("upid"));
				}
				int newId = dataBaseService.insertTable("docomment", setmap, true, false);
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("doing")
						+ " SET replynum=replynum+1 WHERE doid='" + updo.get("doid") + "'");
				if ((Integer) updo.get("uid") != (Integer) sGlobal.get("supe_uid")) {
					String note = Common.getMessage(request, "cp_note_doing_reply",
							"space.jsp?do=doing&doid=" + updo.get("doid") + "&highlight=" + newId);
					cpService.addNotification(request, sGlobal, sConfig, (Integer) updo.get("uid"), "doing",
							note, false);
					Common.getReward("comment", true, 0, "doing" + updo.get("doid"), true, request, response);
				}
				cpService.updateStat(request, "docomment", false);
				return showMessage(request, response, "do_success", refer, 0);
			}

			String op = request.getParameter("op");
			if ("delete".equals(op)) {
				if (submitCheck(request, "deletesubmit")) {
					if (id != 0) {
						boolean allowManage = Common.checkPerm(request, response, "managedoing");
						List<Map<String, Object>> query = dataBaseService
								.executeQuery("SELECT dc.*, d.uid as duid FROM "
										+ JavaCenterHome.getTableName("docomment") + " dc, "
										+ JavaCenterHome.getTableName("doing") + " d WHERE dc.id='" + id
										+ "' AND dc.doid=d.doid");
						if (query.size() != 0) {
							Map<String, Object> value = query.get(0);
							if (allowManage
									|| (Integer) value.get("uid") == (Integer) sGlobal.get("supe_uid")
									|| (Integer) value.get("duid") == (Integer) sGlobal.get("supe_uid")) {
								Map set = new HashMap();
								set.put("uid", 0);
								set.put("username", "");
								set.put("message", "");
								Map where = new HashMap();
								where.put("id", id);
								dataBaseService.updateTable("docomment", set, where);
								if ((Integer) value.get("uid") != (Integer) sGlobal.get("supe_uid")
										&& (Integer) value.get("duid") != (Integer) sGlobal.get("supe_uid")) {
									Common.getReward("delcomment", true, (Integer) value.get("uid"), "",
											true, request, response);
								}
							}
						}
					} else {
						adminDeleteService.deleteDoings(request, response, (Integer) sGlobal.get("supe_uid"),
								doId);
					}
					return showMessage(request, response, "do_success", refer, 0);
				}
			} else if ("getcomment".equals(op)) {
				
				TreeService tree = new TreeService();

				List list = new ArrayList();
				int highLight = 0;
				int count = 0;
				if (Common.empty(request.getParameter("close"))) {
					List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("docomment") + " WHERE doid='" + doId
							+ "' ORDER BY dateline");
					for (Map<String, Object> value : query) {
						Common.realname_set(sGlobal, sConfig, (Map<Integer, String>) request
								.getAttribute("sNames"), (Integer) value.get("uid"), (String) value
								.get("username"), "", 0);
						tree.setNode((Integer) value.get("id"), value.get("upid"), value);
						count++;
						value.put("authorid", space.get("uid"));
						if (!Common.empty(value.get("authorid"))) {
							
							highLight = (Integer) value.get("id");
						}
					}
				}
				if (count != 0) {
					List values = tree.getChilds(0);
					int spaceUid = (Integer) space.get("uid");
					for (Object vid : values) {
						Map one = tree.getValue(vid);
						one.put("layer", tree.getLayer(vid, 0) * 2);
						one.put("style", "padding-left:" + one.get("layer") + "em;");
						if ((Integer) one.get("id") == highLight && (Integer) one.get("uid") == spaceUid) {
							one.put("style", one.get("style") + "color:red;font-weight:bold;");
						}
						list.add(one);
					}
				}
				Common.realname_get(sGlobal, sConfig, (Map<Integer, String>) request.getAttribute("sNames"),
						space);
				request.setAttribute("list", list);
				request.setAttribute("reques", request);
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		request.setAttribute("doid", doId);
		request.setAttribute("id", id);
		return include(request, response, sConfig, sGlobal, "cp_doing.jsp");
	}

	
	public ActionForward cp_domain(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Object result = Common.checkPerm(request, response, sGlobal, "domainlength");
		int domainLength = result != null ? (Integer) result : 0;
		Map reward = null;
		if (!Common.empty(sConfig.get("allowdomain")) && !Common.empty(sConfig.get("domainroot"))
				&& domainLength != 0) {
			reward = Common.getReward("modifydomain", false, 0, "", true, request, response);
		} else {
			return showMessage(request, response, "no_privilege");
		}

		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int rewardCredit = (Integer) reward.get("credit");
		int rewardExperience = (Integer) reward.get("experience");
		try {
			if (submitCheck(request, "domainsubmit")) {
				Map setarr = new HashMap();
				String domain = request.getParameter("domain").trim().toLowerCase();
				String spaceDomain = (String) space.get("domain");
				if (domain.equals(spaceDomain) == false) {
					if (!Common.empty(spaceDomain) && (rewardCredit != 0 || rewardExperience != 0)) {
						int spaceCredit = (Integer) space.get("credit");
						int spaceExperience = (Integer) space.get("experience");
						if (spaceExperience >= rewardExperience) {
							setarr.put("experience", spaceExperience - rewardExperience);
						} else {
							String[] args = new String[] {String.valueOf(spaceExperience),
									String.valueOf(rewardExperience)};
							return showMessage(request, response, "experience_inadequate", "", 1, args);
						}
						if (spaceCredit >= rewardCredit) {
							setarr.put("credit", spaceCredit - rewardCredit);
						} else {
							String[] args = new String[] {String.valueOf(spaceCredit),
									String.valueOf(rewardCredit)};
							return showMessage(request, response, "integral_inadequate", "", 1, args);
						}
					}
					if (domainLength == 0 || domain.length() == 0) {
						setarr.put("domain", "");
					} else {
						int domainLen = domain.length();
						if (domainLen < domainLength) {
							return showMessage(request, response, "domain_length_error", "", 1, String
									.valueOf(domainLength));
						}
						if (domainLen > 30) {
							return showMessage(request, response,
									"two_domain_length_not_more_than_30_characters");
						}
						if (domain.matches("^[a-z][a-z0-9]*$") == false) {
							return showMessage(request, response,
									"only_two_names_from_english_composition_and_figures");
						}
						if (Common.isHoldDomain(sConfig, domain)) {
							return showMessage(request, response, "domain_be_retained");
						}
						
						Map where = new HashMap();
						where.put("domain", domain);
						int count = Common.intval(Common.getCount("space", where, null));
						if(count > 0) {
							return showMessage(request, response, "two_domain_have_been_occupied");
						}
						setarr.put("domain", domain);
					}
				}
				if (setarr.isEmpty() == false) {
					Map where = new HashMap();
					where.put("uid", sGlobal.get("supe_uid"));
					dataBaseService.updateTable("space", setarr, where);
				}
				return showMessage(request, response, "do_success", "cp.jsp?ac=domain");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		Map actives = new HashMap();
		actives.put(request.getParameter("ac"), " class=\"active\"");
		request.setAttribute("domainlength", domainLength);
		request.setAttribute("actives", actives);
		request.setAttribute("reward", reward);
		
		return include(request, response, sConfig, sGlobal, "cp_domain.jsp");
	}

	
	public ActionForward cp_event(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");

		int supe_uid = (Integer) sGlobal.get("supe_uid");
		String supe_username = (String) sGlobal.get("supe_username");
		int timestamp = (Integer) sGlobal.get("timestamp");

		int eventid = 0;
		String tempS = request.getParameter("id");
		if (tempS != null) {
			eventid = Common.intval(tempS);
		}

		tempS = request.getParameter("op");
		String op = Common.empty(tempS) ? "edit" : tempS;
		Map<String, String> menus = new HashMap<String, String>();
		menus.put(op, " class='active'");
		boolean allowmanage = false;
		List<Map<String, Object>> query;
		Map<String, Object> event = null;
		if (eventid != 0) {
			query = dataBaseService.executeQuery("SELECT e.*, ef.* FROM "
					+ JavaCenterHome.getTableName("event") + " e LEFT JOIN "
					+ JavaCenterHome.getTableName("eventfield")
					+ " ef ON e.eventid=ef.eventid WHERE e.eventid='" + eventid + "'");
			event = query.size() > 0 ? query.get(0) : null;
			if (event == null) {
				return showMessage(request, response, "event_does_not_exist");
			}
			int eventGrade = (Integer) event.get("grade");
			int eventUid = (Integer) event.get("uid");
			if ((eventGrade == -1 || eventGrade == 0) && eventUid != supe_uid
					&& !Common.checkPerm(request, response, "manageevent")) {
				return showMessage(request, response, "event_under_verify");
			}

			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("userevent")
					+ " WHERE eventid='" + eventid + "' AND uid='" + supe_uid + "'");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : new HashMap<String, Object>();
			sGlobal.put("supe_userevent", value);
			Integer status = (Integer) value.get("status");
			if ((status != null && status >= 3) || Common.checkPerm(request, response, "manageevent")) {
				allowmanage = true;
			}
		}
		Map<Integer, Map<String, Object>> globalEventClass = Common.getCacheDate(request, response,
				"/data/cache/cache_eventclass.jsp", "globalEventClass");
		if (Common.empty(globalEventClass)) {
			try {
				cacheService.eventclass_cache();
			} catch (IOException e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			globalEventClass = Common.getCacheDate(request, response, "/data/cache/cache_eventclass.jsp",
					"globalEventClass");
		}
		FileUploadUtil upload;
		try {
			upload = getParsedFileUploadUtil(request);
			if (submitCheckForMulti(request, upload, "eventsubmit")) {
				if (Common.checkPerm(request, response, "seccode")
						&& !cpService.checkSeccode(request, response, sGlobal, sConfig, upload
								.getParameter("seccode"))) {
					return showMessage(request, response, "incorrect_code");
				}
				Map<String, Object> arr1 = new HashMap<String, Object>();
				String arr1Title;
				try {
					arr1Title = Common.getStr(upload.getParameter("title"), 80, true, true, true, 0, 0,
							request, response);
				} catch (Exception exception) {
					return showMessage(request, response, exception.getMessage());
				}
				arr1.put("title", arr1Title);
				arr1.put("classid", Common.intval(upload.getParameter("classid")));
				try {
					arr1.put("province", Common.getStr(upload.getParameter("province"), 20, true, true,
							false, 0, 0, request, response));
				} catch (Exception exception) {
					return showMessage(request, response, exception.getMessage());
				}
				try {
					arr1.put("city", Common.getStr(upload.getParameter("city"), 20, true, true, false, 0, 0,
							request, response));
				} catch (Exception exception) {
					return showMessage(request, response, exception.getMessage());
				}
				try {
					arr1.put("location", Common.getStr(upload.getParameter("location"), 80, true, true, true,
							0, 0, request, response));
				} catch (Exception exception) {
					return showMessage(request, response, exception.getMessage());
				}
				String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
				int arr1Starttime = Common.strToTime(upload.getParameter("starttime"), timeoffset,
						"yyyy-MM-dd HH:mm");
				arr1.put("starttime", arr1Starttime);
				int arr1Endtime = Common.strToTime(upload.getParameter("endtime"), timeoffset,
						"yyyy-MM-dd HH:mm");
				arr1.put("endtime", arr1Endtime);
				int arr1Deadline = Common.strToTime(upload.getParameter("deadline"), timeoffset,
						"yyyy-MM-dd HH:mm");
				arr1.put("deadline", arr1Deadline);
				arr1.put("public", Common.intval(upload.getParameter("public")));
				Map<String, Object> arr2 = new HashMap<String, Object>();
				try {
					arr2.put("detail", Common.getStr(upload.getParameter("detail"), 0, true, true, true, 0,
							1, request, response));
				} catch (Exception exception) {
					return showMessage(request, response, exception.getMessage());
				}
				arr2.put("limitnum", Common.intval(upload.getParameter("limitnum")));
				arr2.put("verify", Common.intval(upload.getParameter("verify")));
				arr2.put("allowpost", Common.intval(upload.getParameter("allowpost")));
				arr2.put("allowpic", Common.intval(upload.getParameter("allowpic")));
				arr2.put("allowfellow", Common.intval(upload.getParameter("allowfellow")));
				arr2.put("allowinvite", Common.intval(upload.getParameter("allowinvite")));
				try {
					arr2.put("template", Common.getStr(upload.getParameter("template"), 255, true, true,
							true, 0, 0, request, response));
				} catch (Exception exception) {
					return showMessage(request, response, exception.getMessage());
				}
				if (Common.empty(arr1.get("title"))) {
					return showMessage(request, response, "event_title_empty");
				} else if (Common.empty(arr1.get("classid"))) {
					return showMessage(request, response, "event_classid_empty");
				} else if (Common.empty(arr1.get("city"))) {
					return showMessage(request, response, "event_city_empty");
				} else if (Common.empty(arr2.get("detail"))) {
					return showMessage(request, response, "event_detail_empty");
				} else if (arr1Endtime - arr1Starttime > 60 * 24 * 3600) {
					return showMessage(request, response, "event_bad_time_range");
				} else if (arr1Endtime < arr1Starttime) {
					return showMessage(request, response, "event_bad_endtime");
				} else if (arr1Deadline > arr1Endtime) {
					return showMessage(request, response, "event_bad_deadline");
				} else if (eventid == 0 && arr1Starttime < timestamp) {
					return showMessage(request, response, "event_bad_starttime");
				}
				Map<String, Object> pic = null;

				if (upload.isMultipart()) {
					FileItem fileItem = upload.getFileItem("poster");
					Object picob = cpService.savePic(request, response, fileItem, "-1", arr1Title, 0);
					if (Common.isArray(picob)) {
						pic = (Map<String, Object>) picob;
						if (!Common.empty(pic.get("filepath"))) {
							arr1.put("poster", pic.get("filepath"));
							arr1.put("thumb", pic.get("thumb"));
							arr1.put("remote", pic.get("remote"));
						}
					}
				}
				String tagidString = upload.getParameter("tagid");
				int tagid = 0;
				if (!Common.empty(tagidString)
						&& (eventid == 0 || ((Integer) event.get("uid") == supe_uid)
								&& !tagidString.equals(String.valueOf(event.get("tagid"))))) {
					tagid = Common.intval(tagidString);
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='" + tagid
							+ "' AND uid='" + supe_uid + "' LIMIT 1");
					Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
					if (value != null) {
						if ((Integer) value.get("grade") == 9) {
							arr1.put("tagid", value.get("tagid"));
						}
					}
				}

				if (eventid != 0) {
					if (allowmanage) {
						if ((Integer) event.get("grade") == -1 && (Integer) event.get("uid") == supe_uid) {
							arr1.put("grade", 0);
						}
						Map<String, Object> whereData = new HashMap<String, Object>();
						whereData.put("eventid", eventid);
						dataBaseService.updateTable("event", arr1, whereData);
						dataBaseService.updateTable("eventfield", arr2, whereData);
						tempS = upload.getParameter("sharepic");
						if (!Common.empty(tempS) && pic != null && !Common.empty(pic.get("picid"))) {
							Map<String, Object> arr = new HashMap<String, Object>();
							arr.put("eventid", eventid);
							arr.put("picid", pic.get("picid"));
							arr.put("uid", supe_uid);
							arr.put("username", supe_username);
							arr.put("dateline", timestamp);
							dataBaseService.insertTable("eventpic", arr, false, false);
						}
						return showMessage(request, response, "do_success", "space.jsp?do=event&id="
								+ eventid, 0);
					} else {
						return showMessage(request, response, "no_privilege_edit_event");
					}
				} else {
					if (!cpService.checkRealName(request, "event")) {
						return showMessage(request, response, "no_privilege_realname");
					}
					if (!cpService.checkVideoPhoto(request, response, "event")) {
						return showMessage(request, response, "no_privilege_videophoto");
					}
					switch (cpService.checkNewUser(request, response)) {
						case 1:
							break;
						case 2:
							return showMessage(request, response, "no_privilege_newusertime", "", 1, String
									.valueOf(sConfig.get("newusertime")));
						case 3:
							return showMessage(request, response, "no_privilege_avatar");
						case 4:
							return showMessage(request, response, "no_privilege_friendnum", "", 1, String
									.valueOf(sConfig.get("need_friendnum")));
						case 5:
							return showMessage(request, response, "no_privilege_email");
					}

					int topicid = cpService.checkTopic(request,
							Common.intval(upload.getParameter("topicid")), "event");
					arr1.put("topicid", topicid);
					arr1.put("uid", supe_uid);
					arr1.put("username", supe_username);
					arr1.put("dateline", timestamp);
					arr1.put("updatetime", timestamp);
					arr1.put("membernum", 1);
					arr1.put("grade", !Common.empty(Common.checkPerm(request, response, sGlobal,
							"verifyevent")) ? 0 : 1);
					eventid = dataBaseService.insertTable("event", arr1, true, false);
					if (eventid == 0) {
						return showMessage(request, response, "event_create_failed");
					}
					arr2.put("eventid", eventid);
					arr2.put("hotuser", "");
					dataBaseService.insertTable("eventfield", arr2, false, false);
					tempS = upload.getParameter("sharepic");
					if (!Common.empty(tempS) && pic != null && !Common.empty(pic.get("picid"))) {
						Map<String, Object> arr = new HashMap<String, Object>();
						arr.put("eventid", eventid);
						arr.put("picid", pic.get("picid"));
						arr.put("uid", supe_uid);
						arr.put("username", supe_username);
						arr.put("dateline", timestamp);
						dataBaseService.insertTable("eventpic", arr, false, false);
					}
					Map<String, Object> arr3 = new HashMap<String, Object>();
					arr3.put("eventid", eventid);
					arr3.put("uid", supe_uid);
					arr3.put("username", supe_username);
					arr3.put("status", 4);
					arr3.put("fellow", 0);
					tempS = (String) arr1.get("template");
					tempS = tempS == null ? "" : tempS;
					arr3.put("template", tempS);
					arr3.put("dateline", timestamp);
					dataBaseService.insertTable("userevent", arr3, false, false);
					if ((Integer) arr1.get("grade") > 0) {
						tempS = upload.getParameter("makefeed");
						if (!Common.empty(tempS)) {
							feedService.feedPublish(request, response, eventid, "eventid", true);
						}
					}
					cpService.updateStat(request, "event", false);
					String eventnumsql;
					if (Common.empty(space.get("eventnum"))) {
						Map<String, Object> whereArr = new HashMap<String, Object>();
						whereArr.put("uid", space.get("uid"));
						space.put("eventnum", Common.getCount("event", whereArr, null));
						eventnumsql = "eventnum=" + space.get("eventnum");
					} else {
						eventnumsql = "eventnum=eventnum+1";
					}
					Map<String, Integer> reward = Common.getReward("createevent", false, 0, "", true,
							request, response);
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
							+ eventnumsql + ", lastpost='" + timestamp + "', updatetime='" + timestamp
							+ "', credit=credit+" + reward.get("credit") + ", experience=experience+"
							+ reward.get("experience") + " WHERE uid='" + supe_uid + "'");

					String url;

					if (topicid != 0) {
						cpService.topicJoin(request, topicid, supe_uid, supe_username);
						url = "space.jsp?do=topic&topicid=" + topicid + "&view=event";
					} else {
						url = "space.jsp?do=event&id=" + eventid;
					}
					return showMessage(request, response, "do_success", url, 0);
				}
			}
			if ("invite".equals(op)) {
				
				Map<String, Object> supeUserEvent = (Map<String, Object>) sGlobal.get("supe_userevent");
				if (((event == null || Common.empty(event.get("allowinvite"))) && (supeUserEvent == null || (Integer) supeUserEvent
						.get("status") < 3))
						|| (supeUserEvent == null || (Integer) supeUserEvent.get("status") < 2)) {
					return showMessage(request, response, "no_privilege_do_eventinvite");
				}

				if (submitCheck(request, "invitesubmit")) {
					Map<String, Object> arr = new LinkedHashMap<String, Object>();
					arr.put("uid", supe_uid);
					arr.put("username", supe_username);
					arr.put("eventid", eventid);
					arr.put("dateline", timestamp);
					List<String> inserts = new ArrayList<String>();
					List<Integer> touids = new ArrayList<Integer>();
					String[] ids = request.getParameterValues("ids[]");
					if (ids != null) {
						try {
							StringBuilder builder = new StringBuilder();
							int touid;
							for (int i = 0; i < ids.length; i++) {
								touid = Common.intval(ids[i]);
								arr.put("touid", touid);
								arr.put("tousername", Common.getStr(request.getParameterValues("names[]")[i],
										15, true, true, false, 0, 0, request, response));
								builder.append("(");
								builder.append(Common.sImplode(arr));
								builder.append(")");
								inserts.add(builder.toString());
								touids.add(touid);
								builder.delete(0, builder.length());
							}
						} catch (Exception exception) {
							return showMessage(request, response, exception.getMessage());
						}
					}
					if (!Common.empty(inserts)) {
						dataBaseService.execute("INSERT INTO " + JavaCenterHome.getTableName("eventinvite")
								+ "(uid, username, eventid, dateline, touid, tousername) VALUES "
								+ Common.implode(inserts, ","));
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET eventinvitenum=eventinvitenum+1 WHERE uid IN ("
								+ Common.sImplode(touids) + ")");
					}
					tempS = request.getParameter("group");
					int getGroup = !Common.empty(tempS) ? Common.intval(tempS) : -1;
					tempS = request.getParameter("page");
					int getPage = Common.empty(tempS) ? 0 : Common.intval(tempS);
					return showMessage(request, response, "do_success", "cp.jsp?ac=event&op=invite&id="
							+ eventid + "&group=" + getGroup + "&page=" + getPage, 2);
				}
				int perpage = 21;
				tempS = request.getParameter("page");
				int page = Common.empty(tempS) ? 0 : Common.intval(tempS);
				if (page < 1)
					page = 1;
				int start = (page - 1) * perpage;
				int maxPage = (Integer) sConfig.get("maxpage");
				if ((tempS = Common.ckStart(start, perpage, maxPage)) != null) {
					return showMessage(request, response, tempS);
				}

				List<String> wherearr = new ArrayList<String>();
				String key = Common.stripSearchKey(request.getParameter("key"));
				if (!Common.empty(key)) {
					wherearr.add(" fusername LIKE '%" + key + "%' ");
				}

				tempS = request.getParameter("group");
				int group = !Common.empty(tempS) ? Common.intval(tempS) : -1;
				if (group >= 0) {
					wherearr.add(" gid='" + group + "'");
				}

				String sql = wherearr.size() > 0 ? "AND" + Common.implode(wherearr, " AND ") : "";

				query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
						+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + supe_uid
						+ "' AND status='1' " + sql);
				int count = query.size() > 0 ? (Integer) (query.get(0).get("cont")) : 0;

				List<Integer> fuids = new ArrayList<Integer>();
				List<Map<String, Object>> list = null;
				if (count != 0) {
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + supe_uid
							+ "' AND status='1' " + sql + " ORDER BY num DESC, dateline DESC LIMIT " + start
							+ "," + perpage);
					int fuid;
					for (Map<String, Object> value : query) {
						fuid = (Integer) value.get("fuid");
						Common.realname_set(sGlobal, sConfig, sNames, fuid, (String) value.get("fusername"),
								"", 0);
						fuids.add(fuid);
					}
					list = query;
				}

				Map<Integer, Integer> joins = new HashMap<Integer, Integer>();
				if (fuids.size() > 0) {
					query = dataBaseService.executeQuery("SELECT uid FROM "
							+ JavaCenterHome.getTableName("userevent") + " WHERE eventid='" + eventid
							+ "' AND uid IN (" + Common.sImplode(fuids) + ") AND status > 1");
					int vuid;
					for (Map<String, Object> value : query) {
						vuid = (Integer) value.get("uid");
						joins.put(vuid, vuid);
					}
					query = dataBaseService.executeQuery("SELECT touid FROM "
							+ JavaCenterHome.getTableName("eventinvite") + " WHERE eventid='" + eventid
							+ "' AND touid IN (" + Common.sImplode(fuids) + ")");
					for (Map<String, Object> value : query) {
						vuid = (Integer) value.get("touid");
						joins.put(vuid, vuid);
					}
				}
				Map<Integer, String> groups = Common.getFriendGroup(request);
				Map<Integer, String> groupselect = new HashMap<Integer, String>();
				groupselect.put(group, " selected");

				String multi = Common.multi(request, count, perpage, page, maxPage,
						"cp.jsp?ac=event&op=invite&id=" + eventid + "&group=" + group + "&key=" + key, null,
						null);
				request.setAttribute("group", group);
				request.setAttribute("page", page);
				request.setAttribute("list", list);
				request.setAttribute("joins", joins);
				request.setAttribute("multi", multi);
				request.setAttribute("groups", groups);
			} else if ("members".equals(op)) {
				
				Map<String, Object> supeUserEvent = (Map<String, Object>) sGlobal.get("supe_userevent");
				if (supeUserEvent == null || (Integer) supeUserEvent.get("status") < 3) {
					return showMessage(request, response, "no_privilege_manage_event_members");
				}

				if (submitCheck(request, "memberssubmit")) {
					String[] ids = request.getParameterValues("ids[]");
					boolean rz;
					if (!Common.empty(ids)) {
						Object object = verify_eventmembers(request, sGlobal, event, ids, request
								.getParameter("newstatus"));
						if (object instanceof MessageVO) {
							return showMessage(request, response, (MessageVO) object);
						}
						rz = !Common.empty(object);
					} else {
						rz = false;
					}
					String status = request.getParameter("status");
					status = status == null ? "" : status;
					if (rz) {
						return showMessage(request, response, "do_success", "cp.jsp?ac=event&op=members&id="
								+ eventid + "&status=" + status, 2);
					} else {
						return showMessage(request, response, "choose_right_eventmember",
								"cp.jsp?ac=event&op=members&id=" + eventid + "&status=" + status, 5);
					}
				}
				int perpage = 24;
				tempS = request.getParameter("start");
				int start = Common.empty(tempS) ? 0 : Common.intval(tempS);
				int count = 0;
				String wheresql;
				String key = request.getParameter("key");
				String status = request.getParameter("status");
				if (!Common.empty(key)) {
					key = Common.stripSearchKey(key);
					wheresql = " AND username LIKE '%" + key + "%' ";
				} else {
					status = Common.intval(status) + "";
					wheresql = " AND status='" + status + "'";
				}
				int maxPage = (Integer) sConfig.get("maxpage");
				if ((tempS = Common.ckStart(start, perpage, maxPage)) != null) {
					return showMessage(request, response, tempS);
				}

				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("userevent") + " WHERE eventid='" + eventid + "' "
						+ wheresql + " LIMIT " + start + "," + perpage);
				for (Map<String, Object> value : query) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					tempS = (String) value.get("template");
					if (tempS != null) {
						value.put("template", Common.nl2br(Common.htmlSpecialChars(tempS)));
					} else {
						value.put("template", "");
					}
					count++;
				}
				List<Map<String, Object>> list = query;
				if (!Common.empty(key)) {
					if (list.size() > 0) {
						status = String.valueOf(list.get(0).get("status"));
					} else {
						status = "";
					}
				}
				String multi;
				try {
					multi = Common.smulti(sGlobal, start, perpage, count, "cp.jsp?ac=event&op=members&id="
							+ eventid + "&status=" + status + "&key=" + key, null);
				} catch (Exception e) {
					return showMessage(request, response, e.getMessage());
				}

				request.setAttribute("status", status);
				request.setAttribute("list", list);
				request.setAttribute("multi", multi);
			} else if ("member".equals(op)) {
				
				Map<String, Object> supeUserEvent = (Map<String, Object>) sGlobal.get("supe_userevent");
				if (supeUserEvent == null || (Integer) supeUserEvent.get("status") < 3) {
					return showMessage(request, response, "no_privilege_manage_event_members");
				}

				try {
					if (submitCheck(request, "membersubmit")) {
						String statusString = request.getParameter("status");
						int status = Common.intval(statusString);
						boolean rz;
						String uid = request.getParameter("uid");
						if (!Common.empty(uid)) {
							Object object = verify_eventmembers(request, sGlobal, event, new String[] {uid},
									statusString);
							if (object instanceof MessageVO) {
								return showMessage(request, response, (MessageVO) object);
							}
							rz = !Common.empty(object);
						} else {
							rz = false;
						}
						if (rz) {
							String refer = request.getParameter("refer");
							refer = Common.empty(refer) ? "space.jsp?do=event&id=" + eventid
									+ "&view=member&status=" + status : refer;
							return showMessage(request, response, "do_success", refer, 0);
						} else {
							return showMessage(request, response, "choose_right_eventmember");
						}
					}
				} catch (Exception e) {
					return showMessage(request, response, e.getMessage());
				}
				int uid = Common.intval(request.getParameter("uid"));
				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("userevent") + " WHERE uid='" + uid + "' AND eventid='"
						+ eventid + "'");
				Map<String, Object> userevent = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(userevent)) {
					return showMessage(request, response, "choose_right_eventmember");
				}
				try {
					tempS = Common.nl2br(Common.getStr((String) userevent.get("template"), 255, true, false,
							true, 0, 0, request, response));
				} catch (Exception e) {
					return showMessage(request, response, e.getMessage());
				}
				userevent.put("template", tempS);

				request.setAttribute("uid", uid);
				request.setAttribute("userevent", userevent);
			}
			else if ("pic".equals(op)) {
				
				if (!allowmanage) {
					return showMessage(request, response, "no_privilege_manage_event_pic");
				}
				if (submitCheck(request, "deletepicsubmit")) {
					String[] ids = request.getParameterValues("ids[]");
					if (!Common.empty(ids)) {
						dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("eventpic")
								+ " WHERE eventid='" + eventid + "' AND picid IN (" + Common.sImplode(ids)
								+ ")");
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
								+ " SET picnum = (SELECT COUNT(*) FROM "
								+ JavaCenterHome.getTableName("eventpic") + " WHERE eventid='" + eventid
								+ "') WHERE eventid = '" + eventid + "'");
						return showMessage(request, response, "do_success", "cp.jsp?ac=event&op=pic&id="
								+ eventid, 0);
					} else {
						return showMessage(request, response, "choose_event_pic");
					}
				}
				int perpage = 16;
				tempS = request.getParameter("page");
				int page = Common.empty(tempS) ? 1 : Common.intval(tempS);
				if (page < 1)
					page = 1;
				int start = (page - 1) * perpage;
				int maxPage = (Integer) sConfig.get("maxpage");
				if ((tempS = Common.ckStart(start, perpage, maxPage)) != null) {
					return showMessage(request, response, tempS);
				}
				String theurl = "cp.jsp?ac=event&id=" + eventid + "&op=pic";

				List<Map<String, Object>> photolist = null;
				int count = 0;
				query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
						+ JavaCenterHome.getTableName("eventpic") + " WHERE eventid = '" + eventid + "'");
				if (query.size() > 0) {
					count = (Integer) query.get(0).get("cont");
				}
				if (count != 0) {
					query = dataBaseService.executeQuery("SELECT pic.* FROM "
							+ JavaCenterHome.getTableName("eventpic") + " ep LEFT JOIN "
							+ JavaCenterHome.getTableName("pic")
							+ " pic ON ep.picid=pic.picid WHERE ep.eventid='" + eventid
							+ "' ORDER BY ep.picid DESC LIMIT " + start + ", " + perpage);
					for (Map<String, Object> value : query) {
						value.put("pic", Common.pic_get(sConfig, (String) value.get("filepath"),
								(Integer) value.get("thumb"), (Integer) value.get("remote"), true));
					}
					photolist = query;
				}
				String multi = Common.multi(request, count, perpage, page, maxPage, theurl, null, null);
				int photolistSize = photolist == null ? 0 : photolist.size();
				request.setAttribute("photolistSize", photolistSize);
				request.setAttribute("photolist", photolist);
				request.setAttribute("multi", multi);
			} else if ("thread".equals(op)) {
				
				if (!allowmanage) {
					return showMessage(request, response, "no_privilege_manage_event_thread");
				}
				if (Common.empty(event.get("tagid"))) {
					return showMessage(request, response, "event_has_not_mtag");
				}

				try {
					if (submitCheck(request, "delthreadsubmit")) {
						String[] ids = request.getParameterValues("ids[]");
						if (!Common.empty(ids)) {
							dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("thread")
									+ " WHERE eventid='" + eventid + "' AND tid IN (" + Common.sImplode(ids)
									+ ")");
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
									+ " SET threadnum = (SELECT COUNT(*) FROM "
									+ JavaCenterHome.getTableName("thread") + " WHERE eventid='" + eventid
									+ "') WHERE eventid = '" + eventid + "'");
							return showMessage(request, response, "do_success", "cp.jsp?ac=event&id="
									+ eventid + "&op=thread", 0);
						} else {
							return showMessage(request, response, "choose_event_thread");
						}
					}
				} catch (Exception e) {
					return showMessage(request, response, e.getMessage());
				}
				int perpage = 20;
				tempS = request.getParameter("page");
				int page = Common.empty(tempS) ? 1 : Common.intval(tempS);
				if (page < 1)
					page = 1;
				int start = (page - 1) * perpage;
				int maxPage = (Integer) sConfig.get("maxpage");
				if ((tempS = Common.ckStart(start, perpage, maxPage)) != null) {
					return showMessage(request, response, tempS);
				}
				List<Map<String, Object>> threadlist = null;
				int count = 0;
				query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
						+ JavaCenterHome.getTableName("thread") + " WHERE eventid = '" + eventid + "'");
				if (query.size() > 0) {
					count = (Integer) query.get(0).get("cont");
				}
				if (count != 0) {
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("thread") + " WHERE eventid='" + eventid
							+ "' ORDER BY lastpost DESC LIMIT " + start + ", " + perpage);
					for (Map<String, Object> value : query) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("lastauthorid"),
								(String) value.get("lastauthor"), "", 0);
					}
					threadlist = query;
				}
				String multi = Common.multi(request, count, perpage, page, maxPage, "cp.jsp?ac=event&id="
						+ eventid + "&op=thread", null, null);
				request.setAttribute("threadlist", threadlist);
			} else if ("join".equals(op)) {
				boolean popupmenu_box;
				if (cpService.isBlackList((Integer) event.get("uid"), supe_uid) != 0) {
					popupmenu_box = true;
					return showMessage(request, response, "is_blacklist");
				}
				if (Common.empty(sGlobal.get("supe_userevent"))) {
					popupmenu_box = true;
					if (timestamp > (Integer) event.get("endtime")) {
						return showMessage(request, response, "event_is_over");
					}

					if (timestamp > (Integer) event.get("deadline")) {
						return showMessage(request, response, "event_meet_deadline");
					}

					if ((Integer) event.get("limitnum") > 0
							&& (Integer) event.get("membernum") >= (Integer) event.get("limitnum")) {
						return showMessage(request, response, "event_already_full");
					}
					if ((Integer) event.get("public") < 2) {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("eventinvite") + " WHERE eventid = '"
								+ event.get("eventid") + "' AND touid = '" + supe_uid + "' LIMIT 1");
						Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
						if (Common.empty(value)) {
							return showMessage(request, response, "event_join_limit");
						}
					}
				}
				if (submitCheck(request, "joinsubmit")) {
					Map<String, Object> supe_userevent = (Map<String, Object>) sGlobal.get("supe_userevent");
					boolean supe_usereventNotEmpty = !Common.empty(supe_userevent);
					Integer supe_usereventStatus = supe_usereventNotEmpty ? (Integer) supe_userevent
							.get("status") : null;

					if (supe_usereventStatus != null && supe_usereventStatus == 0) {
						Map<String, Object> arr = new HashMap<String, Object>();
						tempS = request.getParameter("fellow");
						if (tempS != null) {
							arr.put("fellow", Common.intval(tempS));
						}
						tempS = request.getParameter("template");
						if (!Common.empty(tempS)) {
							try {
								tempS = Common.getStr(tempS, 255, true, true, true, 0, 0, request, response);
							} catch (Exception e) {
								return showMessage(request, response, e.getMessage());
							}
							arr.put("template", tempS);
						}
						if (!Common.empty(arr)) {
							Map<String, Object> whereData = new HashMap<String, Object>();
							whereData.put("eventid", eventid);
							whereData.put("uid", supe_uid);
							dataBaseService.updateTable("userevent", arr, whereData);
						}
						return showMessage(request, response, "do_success", "space.jsp?do=event&id="
								+ eventid, 2);
					}
					if (supe_usereventStatus != null && supe_usereventStatus > 1) {
						Map<String, Object> arr = new HashMap<String, Object>();
						int num = 0;

						tempS = request.getParameter("fellow");
						if (tempS != null) {
							int fellow = Common.intval(tempS);
							arr.put("fellow", fellow);
							Integer supe_usereventFellow = (Integer) supe_userevent.get("fellow");
							supe_usereventFellow = supe_usereventFellow == null ? 0 : supe_usereventFellow;
							num = fellow - supe_usereventFellow;
							int eventLimitnum = (Integer) event.get("limitnum");
							if (eventLimitnum > 0 && num + (Integer) event.get("membernum") > eventLimitnum) {
								return showMessage(request, response, "event_already_full");
							}
						}
						tempS = request.getParameter("template");
						if (!Common.empty(tempS)) {
							arr.put("template", tempS);
						}
						if (!Common.empty(arr)) {
							Map<String, Object> whereData = new HashMap<String, Object>();
							whereData.put("eventid", eventid);
							whereData.put("uid", supe_uid);
							dataBaseService.updateTable("userevent", arr, whereData);
						}
						if (num != 0) {
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
									+ " SET membernum = membernum + " + num + " WHERE eventid=" + eventid);
						}
						return showMessage(request, response, "do_success", "space.jsp?do=event&id="
								+ eventid, 0);
					}
					int arrStatus = 2;
					Map<String, Object> arr = new HashMap<String, Object>();
					arr.put("eventid", eventid);
					arr.put("uid", supe_uid);
					arr.put("username", supe_username);
					arr.put("template", event.get("template"));
					arr.put("fellow", 0);
					arr.put("dateline", timestamp);
					int num = 1;
					String numsql;

					tempS = request.getParameter("fellow");
					if (!Common.empty(tempS)) {
						int fellow = Common.intval(tempS);
						arr.put("fellow", fellow);
						num += fellow;
					}
					tempS = request.getParameter("template");
					if (!Common.empty(tempS)) {
						try {
							tempS = Common.getStr(tempS, 255, true, true, true, 0, 0, request, response);
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
						arr.put("template", tempS);
					}
					int eventLimitnum = (Integer) event.get("limitnum");
					if (eventLimitnum > 0 && num + (Integer) event.get("membernum") > eventLimitnum) {
						return showMessage(request, response, "event_will_full");
					}
					numsql = " membernum = membernum + " + num + " ";
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("eventinvite") + " WHERE eventid='" + eventid
							+ "' AND touid='" + supe_uid + "'");
					Map<String, Object> eventinvite = query.size() > 0 ? query.get(0) : null;
					if (!Common.empty(event.get("verify")) && Common.empty(eventinvite)) {
						arrStatus = 0;
					}
					arr.put("status", arrStatus);
					if (supe_usereventStatus != null && supe_usereventStatus == 1) {
						Map<String, Object> whereData = new HashMap<String, Object>();
						whereData.put("uid", supe_uid);
						whereData.put("eventid", eventid);
						dataBaseService.updateTable("userevent", arr, whereData);
						numsql += ",follownum = follownum - 1 ";
					} else {
						dataBaseService.insertTable("userevent", arr, false, false);
					}

					int eventUid = (Integer) event.get("uid");
					if (arrStatus == 2) {
						dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("event") + " SET "
								+ numsql + " WHERE eventid = '" + eventid + "'");
						if (Common.ckPrivacy(sGlobal, sConfig, space, "join", 0)) {
							Common.realname_set(sGlobal, sConfig, sNames, eventUid, (String) event
									.get("username"), "", 0);
							Common.realname_get(sGlobal, sConfig, sNames, space);
							Map<String, Object> title_data = new HashMap<String, Object>();
							title_data.put("title", event.get("title"));
							title_data.put("eventid", event.get("eventid"));
							title_data.put("uid", eventUid);
							title_data.put("username", sNames.get(eventUid));
							cpService.addFeed(sGlobal, "event", Common.getMessage(request, "cp_event_join"),
									title_data, "", null, "", null, null, "", 0, 0, 0, "", false);
						}
					} else if (arrStatus == 0) {
						if (supe_usereventStatus != null && supe_usereventStatus == 1) {
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
									+ " SET follownum = follownum - 1 WHERE eventid = '" + eventid + "'");
						}
						List<Integer> note_ids = new ArrayList<Integer>();
						List<String> note_inserts = new ArrayList<String>();
						int eventEventid = (Integer) event.get("eventid");
						String note_msg = Common.getMessage(request, "cp_event_join_verify",
								"space.jsp?do=event&id=" + eventEventid, event.get("title"),
								"cp.jsp?ac=event&id=" + eventEventid + "&op=members&status=0&key="
										+ supe_username);
						query = dataBaseService.executeQuery("SELECT ue.*, sf.* FROM "
								+ JavaCenterHome.getTableName("userevent") + " ue LEFT JOIN "
								+ JavaCenterHome.getTableName("spacefield")
								+ " sf ON ue.uid=sf.uid WHERE ue.eventid='" + eventid
								+ "' AND ue.status >= 3");
						Map<String, Object> privacyM;
						Set<String> filter;
						Map<String, Object> filter_noteM;
						Map<String, Object> note = new HashMap<String, Object>();
						note.put("type", "eventmember");
						note.put("authorid", supe_uid);
						StringBuilder builder = new StringBuilder();
						int valueUid;
						for (Map<String, Object> value : query) {
							tempS = (String) value.get("privacy");
							privacyM = Common.empty(tempS) ? new HashMap<String, Object>()
									: (Map<String, Object>) Serializer.unserialize(tempS);
							value.put("privacy", privacyM);
							filter_noteM = (Map<String, Object>) privacyM.get("filter_note");
							filter = Common.empty(filter_noteM) ? new HashSet<String>() : filter_noteM
									.keySet();
							if (cpService.checkNoteUid(note, filter)) {
								valueUid = (Integer) value.get("uid");
								note_ids.add(valueUid);
								builder.append("('");
								builder.append(valueUid);
								builder.append("', 'eventmember', '1', '");
								builder.append(supe_uid);
								builder.append("', '");
								builder.append(supe_username);
								builder.append("', '");
								builder.append(Common.addSlashes(note_msg));
								builder.append("', '");
								builder.append(timestamp);
								builder.append("')");
								note_inserts.add(builder.toString());
								builder.delete(0, builder.length());
							}
						}
						if (!Common.empty(note_inserts)) {
							dataBaseService
									.execute("INSERT INTO "
											+ JavaCenterHome.getTableName("notification")
											+ " (`uid`, `type`, `new`, `authorid`, `author`, `note`, `dateline`) VALUES "
											+ Common.implode(note_inserts, ","));
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
									+ " SET notenum=notenum+1 WHERE uid IN (" + Common.sImplode(note_ids)
									+ ")");
						}
						try {
							cpService.sendMail(request, response, eventUid, "", Common.getMessage(request,
									"event_application"), note_msg, "event");
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
					}
					Common.getReward("joinevent", true, 0, eventid + "", true, request, response);
					cpService.updateStat(request, "eventjoin", false);
					if (!Common.empty(eventinvite)) {
						dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("eventinvite")
								+ " WHERE eventid='" + eventid + "' AND touid='" + supe_uid + "'");
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET eventinvitenum=eventinvitenum-1 WHERE uid = '" + supe_uid
								+ "' AND eventinvitenum>0");
					}
					return showMessage(request, response, "do_success", "space.jsp?do=event&id=" + eventid, 0);
				}
			} else if ("quit".equals(op)) {
				if (eventid == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}

				if (submitCheck(request, "quitsubmit")) {
					String tourl = "space.jsp?do=event&id=" + eventid;
					int uid = supe_uid;
					Map<String, Object> userevent = (Map<String, Object>) sGlobal.get("supe_userevent");
					if (!Common.empty(userevent) && (Integer) event.get("uid") != uid) {
						dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("userevent")
								+ " WHERE eventid='" + eventid + "' AND uid='" + uid + "'");
						if ((Integer) userevent.get("status") >= 2) {
							int num = 1 + (Integer) userevent.get("fellow");
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
									+ " SET membernum = membernum - " + num + " WHERE eventid='" + eventid
									+ "'");
						}
						return showMessage(request, response, "do_success", tourl, 0);
					} else {
						return showMessage(request, response, "cannot_quit_event", tourl, 2);
					}
				}

			} else if ("follow".equals(op)) {
				if (eventid == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}
				Map<String, Object> supe_userevent = (Map<String, Object>) sGlobal.get("supe_userevent");
				boolean popupmenu_box = false;
				if (!Common.empty(supe_userevent)) {
					popupmenu_box = true;
					if ((Integer) supe_userevent.get("status") <= 1) {
						return showMessage(request, response, "event_has_followed");
					} else {
						return showMessage(request, response, "event_has_joint");
					}
				}
				if (submitCheck(request, "followsubmit")) {
					Map<String, Object> arr = new HashMap<String, Object>();
					arr.put("eventid", eventid);
					arr.put("uid", supe_uid);
					arr.put("username", supe_username);
					arr.put("status", 1);
					arr.put("fellow", 0);
					arr.put("template", event.get("template"));
					dataBaseService.insertTable("userevent", arr, false, false);

					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
							+ " SET follownum = follownum + 1 WHERE eventid='" + eventid + "'");
					return showMessage(request, response, "do_success", "space.jsp?do=event&id=" + eventid, 0);
				}

			} else if ("cancelfollow".equals(op)) {
				if (eventid == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}

				if (submitCheck(request, "cancelfollowsubmit")) {
					Map<String, Object> supe_userevent = (Map<String, Object>) sGlobal.get("supe_userevent");
					if (!Common.empty(supe_userevent) && (Integer) supe_userevent.get("status") == 1) {
						dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("userevent")
								+ " WHERE uid='" + supe_uid + "' AND eventid='" + eventid + "'");
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
								+ " SET follownum = follownum - 1 WHERE eventid='" + eventid + "'");
					}
					return showMessage(request, response, "do_success", "space.jsp?do=event&id=" + eventid, 0);
				}
			} else if ("eventinvite".equals(op)) {
				
				if (!Common.empty(request.getParameter("r"))) {
					tempS = request.getParameter("page");
					String tourl = "cp.jsp?ac=event&op=eventinvite"
							+ (tempS != null ? "&page=" + Common.intval(tempS) : "");
					if (eventid != 0) {
						dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("eventinvite")
								+ " WHERE eventid = '" + eventid + "' AND touid = '" + supe_uid + "'");
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET eventinvitenum=eventinvitenum-1 WHERE uid = '" + supe_uid
								+ "' AND eventinvitenum>0");
					} else {
						dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("eventinvite")
								+ " WHERE touid = '" + supe_uid + "'");
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET eventinvitenum=0 WHERE uid = '" + supe_uid + "'");
					}

					return showMessage(request, response, "do_success", tourl, 0);
				}
				int perpage = 20;
				tempS = request.getParameter("page");
				int page = Common.empty(tempS) ? 1 : Common.intval(tempS);
				if (page < 1)
					page = 1;
				int start = (page - 1) * perpage;
				int maxPage = (Integer) sConfig.get("maxpage");
				if ((tempS = Common.ckStart(start, perpage, maxPage)) != null) {
					return showMessage(request, response, tempS);
				}
				String theurl = "cp.jsp?ac=event&op=eventinvite";

				Map<String, Object> whereArr = new HashMap<String, Object>();
				whereArr.put("touid", supe_uid);
				int count = Common.intval(Common.getCount("eventinvite", whereArr, null));
				if (count != (Integer) space.get("eventinvitenum")) {
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("eventinvitenum", count);
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("uid", space.get("uid"));
					dataBaseService.updateTable("space", setData, whereData);
				}

				List<Map<String, Object>> eventinvites = null;
				if (count > 0) {
					query = dataBaseService.executeQuery("SELECT ei.*, e.*, ei.dateline as invitetime FROM "
							+ JavaCenterHome.getTableName("eventinvite") + " ei LEFT JOIN "
							+ JavaCenterHome.getTableName("event")
							+ " e ON ei.eventid=e.eventid WHERE ei.touid='" + supe_uid + "' limit " + start
							+ ", " + perpage);
					for (Map<String, Object> value : query) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						if (!Common.empty(value.get("poster"))) {
							value.put("pic", Common.pic_get(sConfig, (String) value.get("poster"),
									(Integer) value.get("thumb"), (Integer) value.get("remote"), true));
						} else {
							value.put("pic", globalEventClass.get(value.get("classid")).get("poster"));
						}
					}
					eventinvites = query;
				}
				String multi = Common.multi(request, count, perpage, page, maxPage, theurl, null, null);
				request.setAttribute("eventinvites", eventinvites);
				request.setAttribute("multi", multi);
			} else if ("acceptinvite".equals(op)) {
				if (eventid == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}

				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("eventinvite") + " WHERE eventid='" + eventid
						+ "' AND touid='" + supe_uid + "' LIMIT 1");
				Map<String, Object> eventinvite = query.size() > 0 ? query.get(0) : null;

				if (Common.empty(eventinvite)) {
					return showMessage(request, response, "eventinvite_does_not_exist");
				}

				dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("eventinvite")
						+ " WHERE eventid='" + eventid + "' AND touid='" + supe_uid + "'");
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET eventinvitenum=eventinvitenum-1 WHERE uid = '" + supe_uid
						+ "' AND eventinvitenum>0");

				if (cpService.isBlackList((Integer) event.get("uid"), supe_uid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				if (timestamp > (Integer) event.get("endtime")) {
					return showMessage(request, response, "event_is_over");
				}
				if (timestamp > (Integer) event.get("deadline")) {
					return showMessage(request, response, "event_meet_deadline");
				}
				int eventLimitnum = (Integer) event.get("limitnum");
				int eventMembernum = (Integer) event.get("membernum");
				if (eventLimitnum > 0 && eventMembernum >= eventLimitnum) {
					return showMessage(request, response, "event_already_full");
				}

				String numsql = "membernum = membernum + 1";
				Map<String, Object> supe_userevent = (Map<String, Object>) sGlobal.get("supe_userevent");
				if (Common.empty(supe_userevent)) {
					Map<String, Object> arr = new HashMap<String, Object>();
					arr.put("eventid", eventid);
					arr.put("uid", supe_uid);
					arr.put("username", supe_username);
					arr.put("status", 2);
					arr.put("template", event.get("template"));
					arr.put("fellow", 0);
					arr.put("dateline", timestamp);
					dataBaseService.insertTable("userevent", arr, false, false);
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event") + " SET "
							+ numsql + " WHERE eventid = '" + eventid + "'");
					if (Common.ckPrivacy(sGlobal, sConfig, space, "join", 0)) {
						int eventUid = (Integer) event.get("uid");
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) eventUid, (String) event
								.get("username"), "", 0);
						Common.realname_get(sGlobal, sConfig, sNames, space);
						Map<String, Object> title_data = new HashMap<String, Object>();
						title_data.put("title", event.get("title"));
						title_data.put("eventid", event.get("eventid"));
						title_data.put("uid", eventUid);
						title_data.put("username", sNames.get(eventUid));
						cpService.addFeed(sGlobal, "event", Common.getMessage(request, "cp_event_join"),
								title_data, "", null, "", null, null, "", 0, 0, 0, "", false);
					}
				} else if ((Integer) supe_userevent.get("status") < 2) {
					Map<String, Object> arr = new HashMap<String, Object>();
					arr.put("status", 2);
					if ((Integer) supe_userevent.get("status") == 1) {
						numsql += ",follownum = follownum - 1 ";
					}
					if (eventLimitnum > 0
							&& eventMembernum + (Integer) supe_userevent.get("fellow") > eventLimitnum) {
						arr.put("fellow", 0);
					}

					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("uid", supe_uid);
					whereData.put("eventid", eventid);
					dataBaseService.updateTable("userevent", arr, whereData);
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event") + " SET "
							+ numsql + " WHERE eventid = '" + eventid + "'");
					if (Common.ckPrivacy(sGlobal, sConfig, space, "join", 0)) {
						int eventUid = (Integer) event.get("uid");
						Map<String, Object> title_data = new HashMap<String, Object>();
						title_data.put("title", event.get("title"));
						title_data.put("eventid", event.get("eventid"));
						title_data.put("uid", eventUid);
						title_data.put("username", event.get("username"));
						cpService.addFeed(sGlobal, "event", Common.getMessage(request, "cp_event_join"),
								title_data, "", null, "", null, null, "", 0, 0, 0, "", false);
					}
				}
				return showMessage(request, response, Common.getMessage(request, "cp_event_accept_success",
						"space.jsp?do=event&id=" + event.get("eventid")));
			} else if ("delete".equals(op)) {
				if (eventid == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}

				if (!allowmanage) {
					return showMessage(request, response, "no_privilege");
				}

				if (submitCheck(request, "deletesubmit")) {
					adminDeleteService.deleteEvents(request, response, sGlobal, new Integer[] {eventid});
					return showMessage(request, response, "do_success", "space.jsp?do=event", 2);
				}
			} else if ("print".equals(op)) {
				
				if (eventid == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}
				if (submitCheck(request, "printsubmit")) {
					List<Map<String, Object>> members;
					List uid;
					if (!Common.empty(request.getParameter("admin"))) {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("userevent") + " WHERE eventid='" + eventid
								+ "' AND status > 1 ORDER BY status DESC, dateline ASC");
					} else {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("userevent") + " WHERE eventid='" + eventid
								+ "' AND status = 2 ORDER BY dateline ASC");
					}
					for (Map<String, Object> value : query) {
						value.put("template", Common.nl2br(Common.htmlSpecialChars((String) value
								.get("template"))));
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
					}
					members = query;
					Common.realname_get(sGlobal, sConfig, sNames, space);
					request.setAttribute("event", event);
					request.setAttribute("members", members);
					return include(request, response, sConfig, sGlobal, "cp_event_sheet.jsp");
				}

			} else if ("close".equals(op)) {
				if (eventid == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}
				if (!allowmanage) {
					return showMessage(request, response, "no_privilege");
				}
				if ((Integer) event.get("grade") < 1 || (Integer) event.get("endtime") > timestamp) {
					return showMessage(request, response, "event_can_not_be_closed");
				}
				if (submitCheck(request, "closesubmit")) {
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("grade", -2);
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("eventid", eventid);
					dataBaseService.updateTable("event", setData, whereData);
					return showMessage(request, response, "do_success", "space.jsp?do=event&id=" + eventid, 0);
				}
			} else if ("open".equals(op)) {
				if (eventid == 0) {
					return showMessage(request, response, "event_does_not_exist");
				}
				if (!allowmanage) {
					return showMessage(request, response, "no_privilege");
				}

				if ((Integer) event.get("grade") != -2 || (Integer) event.get("endtime") > timestamp) {
					return showMessage(request, response, "event_can_not_be_opened");
				}
				if (submitCheck(request, "opensubmit")) {
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("grade", 1);
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("eventid", eventid);
					dataBaseService.updateTable("event", setData, whereData);
					return showMessage(request, response, "do_success", "space.jsp?do=event&id=" + eventid, 0);
				}
			} else if ("calendar".equals(op)) {
				
				List<String> match = null;
				String monthGet = request.getParameter("month");
				String dateGet = request.getParameter("date");
				if (Common.empty(monthGet)) {
					match = Common.pregMatch(dateGet, "^(\\d{4}-\\d{1,2})");
					if (!Common.empty(match)) {
						monthGet = match.get(1);
					}
				}
				if (monthGet != null) {
					match = Common.pregMatch(monthGet, "^(\\d{4})-(\\d{1,2})$");
				}
				int year;
				int month;
				if (!Common.empty(match)) {
					year = Common.intval(match.get(1));
					month = Common.intval(match.get(2));
				} else {
					year = Common.intval(Common.sgmdate(request, "yyyy", timestamp));
					month = Common.intval(Common.sgmdate(request, "MM", timestamp));
				}

				String nextmonth;
				String premonth;
				if (month == 12) {
					nextmonth = (year + 1) + "-" + "1";
					premonth = year + "-11";
				} else if (month == 1) {
					nextmonth = year + "-2";
					premonth = (year - 1) + "-12";
				} else {
					nextmonth = year + "-" + (month + 1);
					premonth = year + "-" + (month - 1);
				}

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MONTH, month - 1);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.YEAR, year);
				int daystart = (int) (calendar.getTimeInMillis() / 1000);

				int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
				int dayscount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				calendar.add(Calendar.MONTH, 1);
				int dayend = (int) (calendar.getTimeInMillis() / 1000);

				Map<Integer, Map<String, Object>> days = new LinkedHashMap<Integer, Map<String, Object>>();
				Map<String, Object> subM;
				for (int i = 1; i <= dayscount; i++) {
					subM = new HashMap<String, Object>();
					subM.put("count", 0);
					subM.put("events", new ArrayList<Map<String, Object>>());
					subM.put("class", "");
					days.put(i, subM);
				}
				query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("event")
						+ " WHERE starttime < " + dayend + " AND endtime > " + daystart
						+ " ORDER BY eventid DESC LIMIT 100");
				int tempInt;
				int start;
				int end;
				List<Map<String, Object>> subList;
				for (Map<String, Object> value : query) {
					if ((Integer) value.get("public") < 1 || (tempInt = (Integer) value.get("grade")) == 0
							|| tempInt == -1) {
						continue;
					}
					tempInt = (Integer) value.get("starttime");
					if (tempInt < daystart) {
						start = 1;
					} else {
						calendar.setTimeInMillis(tempInt * 1000L);
						start = calendar.get(Calendar.DAY_OF_MONTH);
					}
					tempInt = (Integer) value.get("endtime");
					if (tempInt > dayend) {
						end = dayscount;
					} else {
						calendar.setTimeInMillis(tempInt * 1000L);
						end = calendar.get(Calendar.DAY_OF_MONTH);
					}
					for (int i = start; i <= end; i++) {
						subM = days.get(i);
						tempInt = (Integer) subM.get("count");
						if (tempInt < 10) {
							subList = (List<Map<String, Object>>) subM.get("events");
							subList.add(value);
							subM.put("count", tempInt + 1);
							subM.put("class", " on_link");
						}
					}
				}

				int d = 0;
				if (month == Common.intval(Common.sgmdate(request, "MM", timestamp))
						&& year == Common.intval(Common.sgmdate(request, "yyyy", timestamp))) {
					d = Common.intval(Common.sgmdate(request, "dd", timestamp));
					subM = days.get(d);
					subM.put("class", "on_today");
				}

				if (!Common.empty(dateGet)) {
					int t = Common.strToTime(dateGet, Common.getTimeOffset(sGlobal, sConfig));
					if (month == Common.intval(Common.sgmdate(request, "MM", t))
							&& year == Common.intval(Common.sgmdate(request, "yyyy", t))) {
						d = Common.intval(Common.sgmdate(request, "dd", t));
						subM = days.get(d);
						subM.put("class", "on_select");
					}
				}
				String url = request.getParameter("url");
				url = !Common.empty(url) ? url.replaceAll("date=[\\d\\-]+", "") : "space.jsp?do=event";

				request.setAttribute("premonth", premonth);
				request.setAttribute("nextmonth", nextmonth);
				request.setAttribute("year", year);
				request.setAttribute("month", month);
				request.setAttribute("week", week);
				request.setAttribute("days", days);
				request.setAttribute("url", url);
			} else if ("edithot".equals(op)) {
				if (!Common.checkPerm(request, response, "manageevent")) {
					return showMessage(request, response, "no_privilege");
				}

				if (submitCheck(request, "hotsubmit")) {
					int hot = Common.intval(request.getParameter("hot"));
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("hot", hot);

					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("eventid", eventid);
					dataBaseService.updateTable("event", setData, whereData);

					if (hot > 0) {
						feedService.feedPublish(request, response, eventid, "eventid", false);
					} else {
						whereData.clear();
						whereData.put("id", eventid);
						whereData.put("idtype", eventid);
						dataBaseService.updateTable("feed", setData, whereData);
					}
					return showMessage(request, response, "do_success", "space.jsp?uid=" + event.get("uid")
							+ "&do=event&id=" + eventid, 0);
				}
			} else if ("edit".equals(op)) {
				

				if (eventid != 0) {
					if (!allowmanage) {
						return showMessage(request, response, "no_privilege_edit_event");
					}
				} else {
					if (!Common.checkPerm(request, response, "allowevent")) {
						return showMessage(request, response, "no_privilege_add_event");
					}
					if (!cpService.checkRealName(request, "event")) {
						return showMessage(request, response, "no_privilege_realname");
					}
					if (!cpService.checkVideoPhoto(request, response, "event")) {
						return showMessage(request, response, "no_privilege_videophoto");
					}
					switch (cpService.checkNewUser(request, response)) {
						case 1:
							break;
						case 2:
							return showMessage(request, response, "no_privilege_newusertime", "", 1, String
									.valueOf(sConfig.get("newusertime")));
						case 3:
							return showMessage(request, response, "no_privilege_avatar");
						case 4:
							return showMessage(request, response, "no_privilege_friendnum", "", 1, String
									.valueOf(sConfig.get("need_friendnum")));
						case 5:
							return showMessage(request, response, "no_privilege_email");
					}
					event = new HashMap<String, Object>();
					event.put("eventid", "");
					int starttime = (int) (Math.ceil(timestamp / 3600D) * 3600 + 7200);
					event.put("starttime", starttime);
					event.put("endtime", starttime + 14400);
					event.put("deadline", starttime);
					event.put("allowinvite", 1);
					event.put("allowpost", 1);
					event.put("allowpic", 1);
					event.put("allowfellow", 0);
					event.put("verify", 0);
					event.put("public", 2);
					event.put("limitnum", 0);
					event.put("province", space.get("resideprovince"));
					event.put("city", space.get("residecity"));
					Map<String, Object> topic = null;
					int topicid = Common.intval(request.getParameter("topicid"));
					if (topicid != 0) {
						topic = Common.getTopic(request, topicid);
					}
					Map<String, String> actives = null;
					if (!Common.empty(topic)) {
						actives = new HashMap<String, String>();
						actives.put("event", " class=\"active\"");
					}

					request.setAttribute("topicid", topicid);
					request.setAttribute("topic", topic);
				}
				List<Map<String, Object>> mtags = null;
				Integer eventUid = (Integer) event.get("uid");
				if (eventid == 0 || (eventUid != null && eventUid.intValue() == supe_uid)) {
					query = dataBaseService.executeQuery("SELECT mtag.* FROM "
							+ JavaCenterHome.getTableName("tagspace") + " st LEFT JOIN "
							+ JavaCenterHome.getTableName("mtag")
							+ " mtag ON st.tagid=mtag.tagid WHERE st.uid='" + supe_uid + "' AND st.grade=9");
					mtags = query;
				}

				int tagid = Common.intval(request.getParameter("tagid"));
				if (tagid != 0 && Common.empty(event.get("tagid"))) {
					event.put("tagid", tagid);
				}
				Map<String, Object> subM;
				Object tempOb;
				for (Entry<Integer, Map<String, Object>> entry : globalEventClass.entrySet()) {
					subM = entry.getValue();
					tempOb = subM.get("template");
					if (tempOb != null) {
						subM.put("template", String.valueOf(tempOb).replace("\r\n", "<br>").replace("\r",
								"<br>").replace("\n", "<br>"));
					}
				}
				request.setAttribute("globalEventClass", globalEventClass);
				request.setAttribute("mtags", mtags);
				request.setAttribute("ckPrivacy", Common.ckPrivacy(sGlobal, sConfig, space, "event", 1));
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		Common.realname_get(sGlobal, sConfig, sNames, space);
		
		request.setAttribute("op", op);
		request.setAttribute("eventid", eventid);
		request.setAttribute("allowmanage", allowmanage);
		request.setAttribute("event", event);
		request.setAttribute("menus", menus);
		return include(request, response, sConfig, sGlobal, "cp_event.jsp");
	}

	private Object verify_eventmembers(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> event, String[] uids, String statusString) {

		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int timestamp = (Integer) sGlobal.get("timestamp");
		String supe_username = (String) sGlobal.get("supe_username");

		Map<String, Object> supeUserEvent = (Map<String, Object>) sGlobal.get("supe_userevent");
		if (supeUserEvent == null || (Integer) supeUserEvent.get("status") < 3) {
			return new MessageVO("no_privilege_manage_event_members");
		}
		int eventid = (Integer) supeUserEvent.get("eventid");
		List<Map<String, Object>> query;
		if (event == null || eventid != (Integer) event.get("eventid")) {
			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("event")
					+ " WHERE eventid='" + eventid + "'");
			try {
				event = query.get(0);
			} catch (IndexOutOfBoundsException exception) {
				return new MessageVO(exception.getMessage());
			}
		}

		int status = Common.intval(statusString);
		if (status < -1 || status > 3) {
			return new MessageVO("bad_userevent_status");
		}

		if ((Integer) event.get("verify") == 0 && status == 0) {
			return new MessageVO("event_not_set_verify");
		}
		int eventUid = (Integer) event.get("uid");
		if (status == 3 && supe_uid != eventUid) {
			return new MessageVO("only_creator_can_set_admin");
		}

		List<Integer> newids = new ArrayList<Integer>();
		Map<Integer, Map<String, Object>> userevents = new HashMap<Integer, Map<String, Object>>();
		Map<Integer, String> actions = new HashMap<Integer, String>();
		int num = 0;
		query = dataBaseService.executeQuery("SELECT ue.*, sf.* FROM "
				+ JavaCenterHome.getTableName("userevent") + " ue LEFT JOIN "
				+ JavaCenterHome.getTableName("spacefield") + " sf ON ue.uid=sf.uid WHERE ue.uid IN ("
				+ Common.sImplode(uids) + ") AND ue.eventid='" + eventid + "'");
		int valueStatus;
		int valueUid;
		for (Map<String, Object> value : query) {
			valueStatus = (Integer) value.get("status");
			valueUid = (Integer) value.get("uid");
			if (valueStatus == status || eventUid == valueUid || valueStatus == 1) {
				continue;
			}
			if (status == 2 || status == 3 || status == 0 || status == -1) {
				newids.add(valueUid);
				userevents.put(valueUid, value);
				if (status == 2) {
					if (valueStatus == 0) {
						actions.put(valueUid, "set_verify");
						num += ((Integer) value.get("fellow") + 1);
					} else if (valueStatus == 3) {
						actions.put(valueUid, "unset_admin");
					}
				} else if (status == 3) {
					actions.put(valueUid, "set_admin");
					if (valueStatus == 0) {
						num += ((Integer) value.get("fellow") + 1);
					}
				} else if (status == 0) {
					actions.put(valueUid, "unset_verify");
					if (valueStatus >= 2) {
						num -= ((Integer) value.get("fellow") + 1);
					}
				} else if (status == -1) {
					actions.put(valueUid, "set_delete");
					if (valueStatus >= 2) {
						num -= ((Integer) value.get("fellow") + 1);
					}
				}
			}
		}

		if (Common.empty(newids))
			return newids;
		int eventLimitnum = (Integer) event.get("limitnum");
		if (eventLimitnum > 0 && (Integer) event.get("membernum") + num > eventLimitnum) {
			return new MessageVO("event_will_full");
		}

		List<String> note_inserts = new ArrayList<String>();
		List<String> feed_inserts = new ArrayList<String>();
		List<Integer> note_ids = new ArrayList<Integer>();
		Map<String, Object> subMap = new HashMap<String, Object>();
		subMap.put("title", event.get("title"));
		subMap.put("eventid", event.get("eventid"));
		subMap.put("uid", event.get("uid"));
		subMap.put("username", event.get("username"));
		Map<String, Object> feedarr = new HashMap<String, Object>();
		feedarr.put("appid", JavaCenterHome.jchConfig.get("JC_APPID"));
		feedarr.put("icon", "event");
		feedarr.put("uid", "");
		feedarr.put("username", "");
		feedarr.put("dateline", timestamp);
		feedarr.put("title_template", Common.getMessage(request, "cp_event_join"));
		feedarr.put("title_data", subMap);
		feedarr.put("body_template", "");
		feedarr.put("body_data", new HashMap());
		feedarr.put("body_general", "");
		feedarr.put("image_1", "");
		feedarr.put("image_1_link", "");
		feedarr.put("image_2", "");
		feedarr.put("image_2_link", "");
		feedarr.put("image_3", "");
		feedarr.put("image_3_link", "");
		feedarr.put("image_4", "");
		feedarr.put("image_4_link", "");
		feedarr.put("target_ids", "");
		feedarr.put("friend", "friend");
		feedarr = (Map<String, Object>) Common.sStripSlashes(feedarr);
		feedarr.put("title_data", Serializer.serialize(Common.sStripSlashes(feedarr.get("title_data"))));
		feedarr.put("body_data", Serializer.serialize(Common.sStripSlashes(feedarr.get("body_data"))));
		feedarr.put("hash_template", Common.md5(feedarr.get("title_template") + "\t"
				+ feedarr.get("body_template")));
		feedarr.put("hash_data", Common.md5(feedarr.get("title_template") + "\t" + feedarr.get("title_data")
				+ "\t" + feedarr.get("body_template") + "\t" + feedarr.get("body_data")));
		feedarr = (Map<String, Object>) Common.sAddSlashes(feedarr);

		Map<String, Object> mapInUserevents;
		StringBuilder builder = new StringBuilder();
		for (int id : newids) {
			mapInUserevents = userevents.get(id);
			if (status > 1 && (Integer) mapInUserevents.get("status") == 0) {
				feedarr.put("uid", mapInUserevents.get("uid"));
				feedarr.put("username", mapInUserevents.get("username"));
				builder.append("('");
				builder.append(feedarr.get("appid"));
				builder.append("', 'event', '");
				builder.append(feedarr.get("uid"));
				builder.append("', '");
				builder.append(feedarr.get("username"));
				builder.append("', '");
				builder.append(feedarr.get("dateline"));
				builder.append("', '0', '");
				builder.append(feedarr.get("hash_template"));
				builder.append("', '");
				builder.append(feedarr.get("hash_data"));
				builder.append("', '");
				builder.append(feedarr.get("title_template"));
				builder.append("', '");
				builder.append(feedarr.get("title_data"));
				builder.append("', '");
				builder.append(feedarr.get("body_template"));
				builder.append("', '");
				builder.append(feedarr.get("body_data"));
				builder.append("', '");
				builder.append(feedarr.get("body_general"));
				builder.append("', '");
				builder.append(feedarr.get("image_1"));
				builder.append("', '");
				builder.append(feedarr.get("image_1_link"));
				builder.append("', '");
				builder.append(feedarr.get("image_2"));
				builder.append("', '");
				builder.append(feedarr.get("image_2_link"));
				builder.append("', '");
				builder.append(feedarr.get("image_3"));
				builder.append("', '");
				builder.append(feedarr.get("image_3_link"));
				builder.append("', '");
				builder.append(feedarr.get("image_4"));
				builder.append("', '");
				builder.append(feedarr.get("image_4_link"));
				builder.append("')");
				feed_inserts.add(builder.toString());
				builder.delete(0, builder.length());
			}
			mapInUserevents.put("privacy",
					Common.empty(mapInUserevents.get("privacy")) ? new HashMap<String, Object>() : Serializer
							.unserialize((String) mapInUserevents.get("privacy")));
			Map<String, Object> tempM = (Map<String, Object>) ((Map<String, Object>) mapInUserevents
					.get("privacy")).get("filter_note");
			Set<String> filter = Common.empty(tempM) ? new HashSet<String>() : tempM.keySet();
			if (tempM == null) {
				tempM = new HashMap<String, Object>();
			} else {
				tempM.clear();
			}
			tempM.put("type", "eventmemberstatus");
			tempM.put("authorid", supe_uid);

			if (cpService.checkNoteUid(tempM, filter)) {
				note_ids.add(id);
				String actionsValue = actions.get(id);
				actionsValue = actionsValue == null ? "" : actionsValue;
				String note_msg = Common.getMessage(request, "cp_eventmember_" + actionsValue,
						"space.jsp?do=event&id=" + event.get("eventid"), event.get("title"));
				builder.append("('");
				builder.append(id);
				builder.append("', 'eventmemberstatus', '1', '");
				builder.append(supe_uid);
				builder.append("', '");
				builder.append(supe_username);
				builder.append("', '");
				builder.append(Common.addSlashes(note_msg));
				builder.append("', '");
				builder.append(timestamp);
				builder.append("')");
				note_inserts.add(builder.toString());
				builder.delete(0, builder.length());
			}
		}

		if (!Common.empty(note_ids)) {
			dataBaseService.execute("INSERT INTO " + JavaCenterHome.getTableName("notification")
					+ " (`uid`, `type`, `new`, `authorid`, `author`, `note`, `dateline`) VALUES "
					+ Common.implode(note_inserts, ","));
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET notenum=notenum+1 WHERE uid IN (" + Common.sImplode(note_ids) + ")");
		}
		if (!Common.empty(feed_inserts)) {
			dataBaseService
					.execute("INSERT INTO "
							+ JavaCenterHome.getTableName("feed")
							+ " (`appid` ,`icon` ,`uid` ,`username` ,`dateline` ,`friend` ,`hash_template` ,`hash_data` ,`title_template` ,`title_data` ,`body_template` ,`body_data` ,`body_general` ,`image_1` ,`image_1_link` ,`image_2` ,`image_2_link` ,`image_3` ,`image_3_link` ,`image_4` ,`image_4_link`)  VALUES "
							+ Common.implode(feed_inserts, ","));
		}

		if (status == -1) {
			dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("userevent")
					+ " WHERE uid IN (" + Common.sImplode(newids) + ") AND eventid='" + eventid + "'");
		} else {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("userevent")
					+ " SET status='" + status + "' WHERE uid IN (" + Common.sImplode(newids)
					+ ") AND eventid='" + eventid + "'");
		}
		if (num != 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
					+ " SET membernum = membernum + " + num + " WHERE eventid='" + eventid + "'");
		}
		return newids;
	}

	
	public ActionForward cp_feed(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");

		int feedId = Common.intval(request.getParameter("feedid"));
		int page = Common.intval(request.getParameter("page"));
		if (page < 1) {
			page = 1;
		}

		Map feed = null;
		if (feedId != 0) {
			List<Map<String, Object>> feedList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("feed") + " WHERE feedid='" + feedId + "'");
			if (feedList.size() == 0) {
				return showMessage(request, response, "feed_no_found");
			} else {
				feed = feedList.get(0);
			}
		}

		try {
			Map<String, String[]> params = request.getParameterMap();
			if (submitCheck(request, "commentsubmit")) {
				
				if (Common.empty(feed.get("id")) || Common.empty(feed.get("idtype"))) {
					return showMessage(request, response, "non_normal_operation");
				}
				if ("doid".equals(feed.get("idtype"))) {
					
					params.put("id", new String[] {request.getParameter("cid")});
					params.put("doid", new String[] {String.valueOf(feed.get("id"))});
					return cp_doing(request, response);
				} else {
					params.put("id", new String[] {String.valueOf(feed.get("id"))});
					params.put("idtype", new String[] {String.valueOf(feed.get("idtype"))});
					return cp_comment(request, response);
				}
			}

			String op = request.getParameter("op");
			if ("delete".equals(op)) {
				
				if (submitCheck(request, "feedsubmit")) {
					if (adminDeleteService.deleteFeeds(request, response, (Integer) sGlobal.get("supe_uid"),
							feedId)) {
						return showMessage(request, response, "do_success", request.getParameter("refer"));
					} else {
						return showMessage(request, response, "no_privilege");
					}
				}
			} else if ("ignore".equals(op)) {
				
				String icon = Common.empty(request.getParameter("icon")) ? "" : request.getParameter("icon")
						.replaceAll("[^0-9a-zA-Z\\_\\-\\.]", "");
				if (submitCheck(request, "feedignoresubmit")) {
					int uid = Common.empty(request.getParameter("uid")) ? 0 : Common.intval(request
							.getParameter("uid"));
					if (icon.length() != 0) {
						String iconUid = icon + "|" + uid;
						Map privacyMap = (Map) space.get("privacy");
						if (Common.empty(privacyMap.get("filter_icon"))
								|| !Common.isArray(privacyMap.get("filter_icon"))) {
							privacyMap.put("filter_icon", new HashMap());
						}
						Map filterIconMap = (Map) privacyMap.get("filter_icon");
						filterIconMap.put(iconUid, iconUid);
						cpService.privacyUpdate(privacyMap, (Integer) sGlobal.get("supe_uid"));
					}
					return showMessage(request, response, "do_success", request.getParameter("refer"));
				}
			} else if ("get".equals(op)) {
				
				int cpMode = 1;
				int start = Common.intval(request.getParameter("start"));
				if (start < 1) {
					start = (Integer) sConfig.get("feedmaxnum") < 50 ? 50 : (Integer) sConfig
							.get("feedmaxnum");
					start = start + 1;
				}
				Map tpl = new HashMap();
				tpl.put("getmore", 1);
				params.put("start", new String[] {String.valueOf(start)});

				request.setAttribute("TPL", tpl);
				SpaceAction sa = new SpaceAction();
				return sa.space_feed(request, response);
			} else if ("getcomment".equals(op)) {
				
				if (Common.empty(feed.get("id")) || Common.empty(feed.get("idtype"))) {
					return showMessage(request, response, "non_normal_operation");
				}
				feedId = (Integer) feed.get("feedid");
				String multi = "";
				if ("doid".equals(feed.get("idtype"))) {
					params.put("doid", new String[] {String.valueOf(feed.get("id"))});
					return cp_doing(request, response);
				} else {
					int perPage = 5;
					int start = (page - 1) * perPage;
					int maxPage = (Integer) sConfig.get("maxpage");
					String message = Common.ckStart(start, perPage, maxPage);
					if (message != null) {
						return showMessage(request, response, message);
					}
					Map where = new HashMap();
					where.put("id", feed.get("id"));
					where.put("idtype", feed.get("idtype"));
					String count = Common.getCount("comment", where, null);
					Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
					if (!Common.empty(count)) {
						List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("comment") + " WHERE id='" + feed.get("id")
								+ "' AND idtype='" + feed.get("idtype") + "' ORDER BY dateline LIMIT "
								+ start + "," + perPage);
						for (Map<String, Object> value : list) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("authorid"),
									(String) value.get("author"), "", 0);
						}
						multi = Common.multi(request, Common.intval(count), perPage, page, maxPage,
								"cp.jsp?ac=feed&op=getcomment&feedid=" + feedId, "feedcomment_" + feedId, "");
						request.setAttribute("multi", multi);
						request.setAttribute("list", list);
					}
					Common.realname_get(sGlobal, sConfig, sNames, space);
				}
			} else if ("menu".equals(op)) {
				
				boolean allowManage = Common.checkPerm(request, response, "managefeed");
				if (Common.empty(feed.get("uid"))) {
					
					return showMessage(request, response, "non_normal_operation");
				}
				request.setAttribute("feed", feed);
				request.setAttribute("managefeed", allowManage);
			} else {
				String url = "space.jsp?uid=" + feed.get("uid");
				String idType = (String) feed.get("idtype");
				if ("doid".equals(idType)) {
					url += "&do=doing&id=" + feed.get("id");
				} else if ("blogid".equals(idType)) {
					url += "&do=blog&id=" + feed.get("id");
				} else if ("picid".equals(idType)) {
					url += "&do=album&picid=" + feed.get("id");
				} else if ("albumid".equals(idType)) {
					url += "&do=album&id=" + feed.get("id");
				} else if ("tid".equals(idType)) {
					url += "&do=thread&id=" + feed.get("id");
				} else if ("sid".equals(idType)) {
					url += "&do=share&id=" + feed.get("id");
				} else if ("pid".equals(idType)) {
					url += "&do=poll&id=" + feed.get("id");
				} else if ("eventid".equals(idType)) {
					url += "&do=event&id=" + feed.get("id");
				}
				return showMessage(request, response, "do_success", url, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return showMessage(request, response, e.getMessage());
		}
		request.setAttribute("feedid", feedId);
		return include(request, response, sConfig, sGlobal, "cp_feed.jsp");
	}

	public ActionForward cp_friend(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		String op = request.getParameter("op");
		int uid = Common.intval(request.getParameter("uid"));
		space.put("key", Common.spaceKey(space, sConfig, 0));
		Map<String, String> actives = new HashMap<String, String>();
		actives.put(op, " class=\"active\"");
		request.setAttribute("actives", actives);
		try {
			if ("add".equals(op)) {
				if (!Common.checkPerm(request, response, "allowfriend")) {
					MessageVO msgVO = Common.ckSpaceLog(request);
					if (msgVO != null) {
						return showMessage(request, response, msgVO);
					}
					return showMessage(request, response, "no_privilege");
				}
				if (uid == (Integer) sGlobal.get("supe_uid")) {
					return showMessage(request, response, "friend_self_error");
				}
				if (Common.in_array((String[]) space.get("friends"), uid)) {
					return showMessage(request, response, "you_have_friends");
				}
				if (!cpService.checkRealName(request, "friend")) {
					return showMessage(request, response, "no_privilege_realname");
				}
				Map<String, Object> toSpace = Common.getSpace(request, sGlobal, sConfig, uid);
				if (Common.empty(toSpace)) {
					return showMessage(request, response, "space_does_not_exist");
				}
				if (cpService.isBlackList((Integer) toSpace.get("uid"), (Integer) sGlobal.get("supe_uid")) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				Map<Integer, String> groups = Common.getFriendGroup(request);
				int status = Common.getFriendStatus((Integer) sGlobal.get("supe_uid"), uid);
				if (status == 1) {
					return showMessage(request, response, "you_have_friends");
				} else {
					int maxFriendNum = (Integer) Common.checkPerm(request, response, sGlobal, "maxfriendnum");
					if (maxFriendNum != 0
							&& (Integer) space.get("friendnum") >= maxFriendNum
									+ (Integer) space.get("addfriend")) {
						Map globalMagic = Common.getCacheDate(request, response,
								"/data/cache/cache_magic.jsp", "globalMagic");
						if (!Common.empty(globalMagic.get("friendnum"))) {
							return showMessage(request, response,
									"enough_of_the_number_of_friends_with_magic");
						} else {
							return showMessage(request, response, "enough_of_the_number_of_friends");
						}
					}
					int fStatus = Common.getFriendStatus(uid, (Integer) sGlobal.get("supe_uid"));
					if (fStatus == -1) {
						if (status == -1) {
							if (!Common.empty(toSpace.get("videostatus"))) {
								if (!cpService.checkVideoPhoto(request, response, "friend", toSpace)) {
									return showMessage(request, response, "no_privilege_videophoto");
								}
							}
							if (submitCheck(request, "addsubmit")) {
								Map<String, Object> serArr = new HashMap<String, Object>();
								serArr.put("uid", sGlobal.get("supe_uid"));
								serArr.put("fuid", uid);
								serArr.put("fusername", Common.addSlashes((String) toSpace.get("username")));
								serArr.put("gid", Common.intval(request.getParameter("gid")));
								serArr.put("note", Common.getStr(request.getParameter("note"), 50, true,
										true, false, 0, 0, request, response));
								serArr.put("dateline", sGlobal.get("timestamp"));
								dataBaseService.insertTable("friend", serArr, false, false);
								cpService.sendMail(request, response, uid, "", Common.getMessage(request,
										"cp_friend_subject", new String[] {
												sNames.get(space.get("uid")),
												Common.getSiteUrl(request)
														+ "cp.jsp?ac=friend&amp;op=request"}), "",
										"friend_add");
								dataBaseService.executeUpdate("UPDATE "
										+ JavaCenterHome.getTableName("space")
										+ " SET addfriendnum=addfriendnum+1 WHERE uid='" + uid + "'");
								return showMessage(request, response, "request_has_been_sent");
							} else {
								request.setAttribute("op", op);
								request.setAttribute("tospace", toSpace);
								request.setAttribute("groups", groups);
								return include(request, response, sConfig, sGlobal, "cp_friend.jsp");
							}
						} else {
							return showMessage(request, response, "waiting_for_the_other_test");
						}
					} else {
						if (submitCheck(request, "add2submit")) {
							int gid = Common.intval(request.getParameter("gid"));

							cpService.updateFriend(request, sGlobal, sConfig, (Integer) space.get("uid"),
									(String) space.get("username"), (Integer) toSpace.get("uid"),
									(String) toSpace.get("username"), "add", gid);
							if (Common.ckPrivacy(sGlobal, sConfig, space, "friend", 1)) {
								Map<String, String> fs = new HashMap<String, String>();
								fs.put("icon", "friend");
								fs.put("title_template", Common.getMessage(request, "cp_feed_friend_title"));
								fs.put("body_template", "");
								fs.put("body_general", "");
								Map titleData = new HashMap();
								titleData.put("touser", "<a href=\"space.jsp?uid=" + toSpace.get("uid")
										+ "\">" + sNames.get(toSpace.get("uid")) + "</a>");
								cpService.addFeed(sGlobal, fs.get("icon"), fs.get("title_template"),
										titleData, fs.get("body_template"), null, fs.get("body_general"),
										null, null, "", 0, 0, 0, "", false);
							}
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
									+ " SET addfriendnum=addfriendnum-1 WHERE uid='" + space.get("uid")
									+ "' AND addfriendnum>0");
							cpService.addNotification(request, sGlobal, sConfig, uid, "friend", Common
									.getMessage(request, "cp_note_friend_add"), false);
							return showMessage(request, response, "friends_add", request
									.getParameter("refer"), 1, new String[] {sNames.get(toSpace.get("uid"))});
						} else {
							op = "add2";
							request.setAttribute("op", op);
							request.setAttribute("tospace", toSpace);
							request.setAttribute("groups", groups);
							return include(request, response, sConfig, sGlobal, "cp_friend.jsp");
						}
					}
				}
			} else if ("ignore".equals(op)) {
				if (uid > 0) {
					if (submitCheck(request, "friendsubmit")) {
						int fStatus = Common.getFriendStatus(uid, (Integer) space.get("uid"));
						if (fStatus == 1) {
							cpService.updateFriend(request, sGlobal, sConfig, (Integer) sGlobal
									.get("supe_uid"), (String) sGlobal.get("supe_username"), uid, "",
									"ignore", 0);
						} else if (fStatus == 0) {
							cpService.ignoreRequest(space, sConfig, uid);
						}
						return showMessage(request, response, "do_success", "cp.jsp?ac=friend&op=request", 0);
					}
				} else if (space.get("key").toString().equals(request.getParameter("key"))) {
					List<Map<String, Object>> fUids = dataBaseService.executeQuery("SELECT uid FROM "
							+ JavaCenterHome.getTableName("friend") + " WHERE fuid='" + space.get("uid")
							+ "' AND status='0' LIMIT 0,1");
					if (fUids.size() > 0) {
						Map<String, Object> value = fUids.get(0);
						uid = (Integer) value.get("uid");
						Map whereArr = new HashMap();
						whereArr.put("uid", uid);
						String userName = Common.getCount("space", whereArr, "username");
						cpService.ignoreRequest(space, sConfig, uid);
						return showMessage(request, response, "friend_ignore_next",
								"cp.jsp?ac=friend&op=ignore&confirm=1&key=" + space.get("key"), 1, userName);
					} else {
						return showMessage(request, response, "do_success", "cp.jsp?ac=friend&op=request", 0);
					}
				} else {
					return showMessage(request, response, "specified_user_is_not_your_friend");
				}
			} else if ("addconfirm".equals(op)) {
				if (space.get("key").toString().equals(request.getParameter("key"))) {
					int maxFriendNum = (Integer) Common.checkPerm(request, response, sGlobal, "maxfriendnum");
					if (maxFriendNum != 0
							&& (Integer) space.get("friendnum") >= maxFriendNum
									+ (Integer) space.get("addfriend")) {
						Map globalMagic = Common.getCacheDate(request, response,
								"/data/cache/cache_magic.jsp", "globalMagic");
						if (!Common.empty(globalMagic.get("friendnum"))) {
							return showMessage(request, response,
									"enough_of_the_number_of_friends_with_magic");
						} else {
							return showMessage(request, response, "enough_of_the_number_of_friends");
						}
					}
					List<Map<String, Object>> uids = dataBaseService.executeQuery("SELECT uid FROM "
							+ JavaCenterHome.getTableName("friend") + " WHERE fuid='" + space.get("uid")
							+ "' AND status='0' LIMIT 0,1");
					if (uids.size() > 0) {
						Map<String, Object> value = uids.get(0);
						uid = (Integer) value.get("uid");
						Map whereArr = new HashMap();
						whereArr.put("uid", uid);
						String userName = Common.getCount("space", whereArr, "username");
						cpService.updateFriend(request, sGlobal, sConfig, (Integer) space.get("uid"),
								(String) space.get("username"), uid, userName, "add", 0);
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET addfriendnum=addfriendnum-1 WHERE uid='" + space.get("uid")
								+ "' AND addfriendnum>0");
						return showMessage(request, response, "friend_addconfirm_next",
								"cp.jsp?ac=friend&op=addconfirm&key=" + space.get("key"), 1, userName);
					}
				}
				return showMessage(request, response, "do_success", "cp.jsp?ac=friend&op=request", 0);
			} else if ("syn".equals(op)) {
				return null;
			} else if ("find".equals(op)) {
				int maxNum = 18;
				List noUids = new ArrayList();
				if (space.get("friends") != null) {
					CollectionUtils.addAll(noUids, (String[]) space.get("friends"));
				}
				noUids.add(space.get("uid").toString());
				List<Map<String, Object>> nearList = new ArrayList<Map<String, Object>>(maxNum);
				int i = 0;
				String myIp = Common.getOnlineIP(request, true);
				List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("session") + " WHERE ip='" + myIp + "' LIMIT 0,200");
				for (Map<String, Object> value : sessionList) {
					if (!noUids.contains(value.get("uid").toString())) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						nearList.add(value);
						i++;
						if (i >= maxNum) {
							break;
						}
					}
				}
				request.setAttribute("nearList", nearList);
				i = 0;
				if (!Common.empty(space.get("feedfriend"))) {
					Map friendList = new HashMap(maxNum);
					List<Map<String, Object>> friends = dataBaseService
							.executeQuery("SELECT fuid AS uid, fusername AS username FROM "
									+ JavaCenterHome.getTableName("friend") + " WHERE uid IN ("
									+ space.get("feedfriend") + ") LIMIT 0,200");
					for (Map<String, Object> value : friends) {
						if (!noUids.contains(value.get("uid").toString())
								&& !Common.empty(value.get("username"))) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), "", 0);
							friendList.put(value.get("uid"), value);
							i++;
							if (i >= maxNum) {
								break;
							}
						}
					}
					request.setAttribute("friendList", friendList);
				}
				i = 0;
				List<Map<String, Object>> onLineList = new ArrayList<Map<String, Object>>(maxNum);
				List<Map<String, Object>> onLines = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("session") + " LIMIT 0,200");
				for (Map<String, Object> value : onLines) {
					if (!noUids.contains(value.get("uid").toString())) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), null, 0);
						onLineList.add(value);
						i++;
						if (i >= maxNum) {
							break;
						}
					}
				}
				request.setAttribute("onLineList", onLineList);
				Common.realname_get(sGlobal, sConfig, sNames, space);
			} else if ("changegroup".equals(op)) {
				if (submitCheck(request, "changegroupsubmit")) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("friend")
							+ " SET gid='" + Common.intval(request.getParameter("group")) + "' WHERE uid='"
							+ sGlobal.get("supe_uid") + "' AND fuid='" + uid + "'");
					cpService.friendCache(request, sGlobal, sConfig, (Integer) sGlobal.get("supe_uid"));
					return showMessage(request, response, "do_success", (String) sGlobal.get("refer"));
				}
				List<Map<String, Object>> friends = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + sGlobal.get("supe_uid")
						+ "' AND fuid='" + uid + "'");
				if (friends.isEmpty()) {
					return showMessage(request, response, "specified_user_is_not_your_friend");
				}
				Map<String, Object> friend = friends.get(0);
				Map groupSelect = new HashMap();
				groupSelect.put(friend.get("gid"), " checked");
				Map<Integer, String> groups = Common.getFriendGroup(request);
				request.setAttribute("groups", groups);
				request.setAttribute("groupSelect", groupSelect);
			} else if ("changenum".equals(op)) {
				if (submitCheck(request, "changenumsubmit")) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("friend")
							+ " SET num='" + Common.intval(request.getParameter("num")) + "' WHERE uid='"
							+ sGlobal.get("supe_uid") + "' AND fuid='" + uid + "'");
					cpService.friendCache(request, sGlobal, sConfig, (Integer) sGlobal.get("supe_uid"));
					return showMessage(request, response, "do_success", (String) sGlobal.get("refer"), 0);
				}
				List<Map<String, Object>> friends = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + sGlobal.get("supe_uid")
						+ "' AND fuid='" + uid + "'");
				if (friends.isEmpty()) {
					return showMessage(request, response, "specified_user_is_not_your_friend");
				}
				request.setAttribute("friend", friends.get(0));
			} else if ("group".equals(op)) {
				if (submitCheck(request, "groupsubmin")) {
					String[] fUids = request.getParameterValues("fuids");
					if (Common.empty(fUids)) {
						return showMessage(request, response, "please_correct_choice_groups_friend");
					}
					int groupId = Common.intval(request.getParameter("group"));
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("friend")
							+ " SET gid='" + groupId + "' WHERE uid='" + sGlobal.get("supe_uid")
							+ "' AND fuid IN (" + Common.sImplode(fUids) + ") AND status='1'");
					cpService.friendCache(request, sGlobal, sConfig, (Integer) sGlobal.get("supe_uid"));
					return showMessage(request, response, "do_success", (String) sGlobal.get("refer"));
				}
				int perPage = 50;
				int page = Common.intval(request.getParameter("page"));
				if (page < 1) {
					page = 1;
				}
				int start = (page - 1) * perPage;
				if (!Common.empty(space.get("friendnum"))) {
					Map<Integer, String> groups = Common.getFriendGroup(request);
					String theURL = "cp.jsp?ac=friend&op=group";
					int group = request.getParameter("group") == null ? -1 : Common.intval(request
							.getParameter("group"));
					String whereSQL = "";
					if (group > -1) {
						whereSQL = "AND main.gid='" + group + "'";
						theURL += "&group=" + group;
					}
					int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("friend") + " main WHERE main.uid='"
							+ space.get("uid") + "' AND main.status='1' " + whereSQL);
					List<Map<String, Object>> list = dataBaseService
							.executeQuery("SELECT main.fuid AS uid,main.fusername AS username, main.gid, main.num FROM "
									+ JavaCenterHome.getTableName("friend")
									+ " main WHERE main.uid='"
									+ space.get("uid")
									+ "' AND main.status='1' "
									+ whereSQL
									+ " ORDER BY main.dateline DESC LIMIT " + start + "," + perPage);
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						value.put("group", groups.get(value.get("gid")));
					}
					request.setAttribute("list", list);
					request.setAttribute("multi", Common.multi(request, count, perPage, page,
							(Integer) sConfig.get("maxpage"), theURL, null, null));
				}
				Map<Integer, String> groups = Common.getFriendGroup(request);
				request.setAttribute("groups", groups);
				actives.put("group", " class=\"active\"");
				Common.realname_get(sGlobal, sConfig, sNames, space);
			} else if ("request".equals(op)) {
				if (submitCheck(request, "requestsubmin")) {
					return showMessage(request, response, "do_success", (String) sGlobal.get("refer"));
				}
				int maxFriendnum = (Integer) Common.checkPerm(request, response, sGlobal, "maxfriendnum");
				if (maxFriendnum > 0) {
					maxFriendnum = maxFriendnum + (Integer) space.get("addfriend");
				}
				int perPage = 20;
				int page = Common.intval(request.getParameter("page"));
				if (page < 1) {
					page = 1;
				}
				int start = (page - 1) * perPage;
				String[] friend1 = (String[]) space.get("friends");
				Map whereArr = new HashMap();
				whereArr.put("fuid", space.get("uid"));
				whereArr.put("status", 0);
				int count = Common.intval(Common.getCount("friend", whereArr, null));
				if (count > 0) {
					List<Map<String, Object>> list = dataBaseService
							.executeQuery("SELECT f.dateline,f.note,f.fuid, s.*, sf.friend FROM "
									+ JavaCenterHome.getTableName("friend") + " f LEFT JOIN "
									+ JavaCenterHome.getTableName("space") + " s ON s.uid=f.uid LEFT JOIN "
									+ JavaCenterHome.getTableName("spacefield")
									+ " sf ON sf.uid=f.uid WHERE f.fuid='" + space.get("uid")
									+ "' AND f.status='0' ORDER BY f.dateline DESC LIMIT " + start + ","
									+ perPage);
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), "", 0);
						String[] cFriend = {};
						String[] friend2 = Common.empty(value.get("friend")) ? null : value.get("friend")
								.toString().split(",");
						if (friend1 != null && friend2 != null) {
							cFriend = getArrayIntersect(friend1, friend2);
						}
						value.put("cfriend", Common.implode(cFriend, ","));
						value.put("cfcount", cFriend.length);
					}
					request.setAttribute("list", list);
				}
				if (count != (Integer) space.get("addfriendnum")) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET addfriendnum='" + count + "' WHERE uid='" + space.get("uid") + "'");
				}
				request.setAttribute("multi", Common.multi(request, count, perPage, page, (Integer) sConfig
						.get("maxpage"), "cp.jsp?ac=friend&op=request", null, null));
				Common.realname_get(sGlobal, sConfig, sNames, space);
				request.setAttribute("maxfriendnum", maxFriendnum);
			} else if ("groupname".equals(op)) {
				Map<Integer, String> groups = Common.getFriendGroup(request);
				int group = Common.intval(request.getParameter("group"));
				if (groups.get(group) == null) {
					return showMessage(request, response, "change_friend_groupname_error");
				}
				if (submitCheck(request, "groupnamesubmit")) {
					Map<String, Object> privacy = (Map<String, Object>) space.get("privacy");
					Map<Integer, String> groupList = null;
					if (privacy != null) {
						groupList = (Map<Integer, String>) privacy.get("groupname");
					}
					groupList = groupList == null ? new HashMap<Integer, String>() : groupList;
					String groupName = Common.getStr(request.getParameter("groupname"), 20, true, true,
							false, 0, 0, request, response);
					groupList.put(group, groupName);
					if (privacy != null) {
						privacy.put("groupname", groupList);
						space.put("privacy", privacy);
					}
					cpService.privacyUpdate(privacy, (Integer) sGlobal.get("supe_uid"));
					return showMessage(request, response, "do_success", request.getParameter("refer"));
				}
				request.setAttribute("group", group);
				request.setAttribute("groups", groups);
			} else if ("groupignore".equals(op)) {
				Map<Integer, String> groups = Common.getFriendGroup(request);
				int group = Common.intval(request.getParameter("group"));
				if (groups.get(group) == null) {
					return showMessage(request, response, "change_friend_groupname_error");
				}
				if (submitCheck(request, "groupignoresubmit")) {

					Map<String, Object> privacy = (Map<String, Object>) space.get("privacy");
					Map<Integer, Integer> filterGid = null;
					if (privacy != null) {
						filterGid = (Map<Integer, Integer>) privacy.get("filter_gid");
					}
					filterGid = filterGid == null ? new HashMap<Integer, Integer>() : filterGid;
					if (filterGid.get(group) != null) {
						filterGid.remove(group);
					} else {
						filterGid.put(group, group);
					}
					if (privacy != null) {
						privacy.put("filter_gid", filterGid);
						space.put("privacy", privacy);
					}
					cpService.privacyUpdate(privacy, (Integer) sGlobal.get("supe_uid"));
					cpService.friendCache(request, sGlobal, sConfig, (Integer) sGlobal.get("supe_uid"));
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
				request.setAttribute("group", group);
			} else if ("blacklist".equals(op)) {
				if ("delete".equals(request.getParameter("subop"))) {
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("blacklist")
							+ " WHERE uid='" + space.get("uid") + "' AND buid='" + uid + "'");
					return showMessage(request, response, "do_success",
							"space.jsp?do=friend&view=blacklist&start=" + request.getParameter("start"), 0);
				}
				if (submitCheck(request, "blacklistsubmit")) {
					String userName = Common.trim(request.getParameter("username"));
					List<Map<String, Object>> spaceList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("space") + " WHERE username='" + userName + "'");
					if (spaceList.isEmpty()) {
						return showMessage(request, response, "space_does_not_exist");
					}
					Map<String, Object> toSpace = spaceList.get(0);
					if (toSpace.get("uid").equals(space.get("uid"))) {
						return showMessage(request, response, "unable_to_manage_self");
					}
					if (Common.in_array((String[]) space.get("friends"), toSpace.get("uid"))) {
						cpService.updateFriend(request, sGlobal, sConfig, (Integer) sGlobal.get("supe_uid"),
								(String) sGlobal.get("supe_username"), (Integer) toSpace.get("uid"), "",
								"ignore", 0);
					}
					Map insertData = new HashMap();
					insertData.put("uid", space.get("uid"));
					insertData.put("buid", toSpace.get("uid"));
					insertData.put("dateline", sGlobal.get("timestamp"));
					dataBaseService.insertTable("blacklist", insertData, false, true);

					return showMessage(request, response, "do_success",
							"space.jsp?do=friend&view=blacklist&start=" + request.getParameter("start"), 0);
				}
			} else if ("rand".equals(op)) {
				Object[] randUids = null;
				if ((Integer) space.get("friendnum") < 5) {
					List<Map<String, Object>> sessionList = dataBaseService.executeQuery("SELECT uid FROM "
							+ JavaCenterHome.getTableName("session") + " LIMIT 0,100");
					List onlineList = new ArrayList(sessionList.size());
					for (Map<String, Object> value : sessionList) {
						if (!value.get("uid").equals(space.get("uid"))) {
							onlineList.add(value.get("uid"));
						}
					}
					randUids = (Object[]) Common.sarrayRand(arrayMerge(onlineList.toArray(), (String[]) space
							.get("friends")), 1);
				} else {
					randUids = (Object[]) Common.sarrayRand(space.get("friends"), 1);
				}
				return showMessage(request, response, "do_success", "space.jsp?uid="
						+ (randUids == null ? "" : randUids[randUids.length - 1]), 0);
			} else if ("getcfriend".equals(op)) {
				String[] fuids = Common.empty(request.getParameter("fuid")) ? null : request.getParameter(
						"fuid").split(",");
				Map<Integer, Integer> newfUids = new HashMap<Integer, Integer>(fuids == null ? 0
						: fuids.length);
				if (fuids != null) {
					for (String value : fuids) {
						int fuid = Common.intval(value);
						if (fuid != 0) {
							newfUids.put(fuid, fuid);
						}
					}
				}
				if (!newfUids.isEmpty()) {
					List<Map<String, Object>> list = dataBaseService
							.executeQuery("SELECT uid,username,name,namestatus FROM "
									+ JavaCenterHome.getTableName("space") + " WHERE uid IN ("
									+ Common.sImplode(newfUids) + ") LIMIT 0,15");
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), (String) value.get("name"), (Integer) value
										.get("namestatus"));
					}
					request.setAttribute("list", list);
					Common.realname_get(sGlobal, sConfig, sNames, space);
				}
			} else if ("search".equals(op)) {
				Map<Integer, Map<String, Object>> fields = Common.getCacheDate(request, response,
						"/data/cache/cache_profilefield.jsp", "globalProfilefield");
				if (!Common.empty(request.getParameter("searchsubmit"))
						|| !Common.empty(request.getParameter("searchmode"))) {
					Map<String, String[]> paramMap = request.getParameterMap();
					paramMap.put("searchsubmit", new String[] {1 + ""});
					paramMap.put("searchmode", new String[] {1 + ""});
					List<String> whereArr = new ArrayList<String>();
					Map<String, String> fromArr = new HashMap<String, String>();
					String fSQL = "";

					fromArr.put("space", JavaCenterHome.getTableName("space") + " s");
					String searchKey = request.getParameter("searchkey");
					if (!Common.empty(Common.stripSearchKey(searchKey))) {
						whereArr.add("(s.name='" + searchKey + "' OR s.username='" + searchKey + "')");
					} else {
						for (String value : new String[] {"uid", "username", "name", "videostatus", "avatar"}) {
							if (!Common.empty(request.getParameter(value))) {
								whereArr.add("s." + value + "='" + request.getParameter(value) + "'");
							}
						}
					}
					String spaceField = null;
					for (String value : new String[] {"sex", "qq", "msn", "birthyear", "birthmonth",
							"birthday", "blood", "marry", "birthprovince", "birthcity", "resideprovince",
							"residecity"}) {
						if (!Common.empty(request.getParameter(value))) {
							fromArr.put("spacefield", JavaCenterHome.getTableName("spacefield") + " sf");
							spaceField = "sf.uid=s.uid";
							whereArr.add("sf." + value + "='" + request.getParameter(value) + "'");
							fSQL += ", sf." + value;
						}
					}
					int startAge, endAge;
					endAge = startAge = 0;
					if (!Common.empty(request.getParameter("endage"))) {
						startAge = Integer.valueOf(Common.sgmdate(request, "yyyy", 0))
								- Common.intval(request.getParameter("endage"));
					}
					if (!Common.empty(request.getParameter("startage"))) {
						endAge = Integer.valueOf(Common.sgmdate(request, "yyyy", 0))
								- Common.intval(request.getParameter("startage"));
					}
					if (startAge != 0 || endAge != 0) {
						fromArr.put("spacefield", JavaCenterHome.getTableName("spacefield") + " sf");
						spaceField = "sf.uid=s.uid";
					}
					if (startAge != 0 && endAge != 0 && endAge > startAge) {
						whereArr.add("(sf.birthyear>=" + startAge + " AND sf.birthyear<=" + endAge + ")");
					} else if (startAge != 0 && endAge == 0) {
						whereArr.add("sf.birthyear>=" + startAge);
					} else if (startAge == 0 && endAge != 0) {
						whereArr.add("sf.birthyear<=" + endAge);
					}
					boolean haveField = false;
					for (Entry<Integer, Map<String, Object>> e : fields.entrySet()) {
						if (!Common.empty(e.getValue().get("allowsearch"))) {
							String field = Common.stripSearchKey(request.getParameter("field_" + e.getKey()));
							if (!Common.empty(field)) {
								haveField = true;
								whereArr.add("sf.field_" + e.getKey() + " LIKE '%" + field + "%'");
							}
						}
					}
					if (haveField) {
						fromArr.put("spacefield", JavaCenterHome.getTableName("spacefield") + " sf");
						spaceField = "sf.uid=s.uid";
					}
					String type = request.getParameter("type");
					String spaceInfo = null;
					if ("edu".equals(type) || "work".equals(type)) {
						for (String value : new String[] {"type", "title", "subtitle", "startyear"}) {
							if (!Common.empty(request.getParameter(value))) {
								fromArr.put("spaceinfo", JavaCenterHome.getTableName("spaceinfo") + " si");
								spaceInfo = "si.uid=s.uid";
								whereArr.add("si." + value + "='" + request.getParameter(value) + "'");
							}
						}
					}

					if (!whereArr.isEmpty()) {
						List<Map<String, Object>> searchList = dataBaseService.executeQuery("SELECT s.* "
								+ fSQL + " FROM " + Common.implode(fromArr, ",") + " WHERE "
								+ Common.implode(whereArr, " AND ")
								+ (spaceField == null ? "" : " AND " + spaceField)
								+ (spaceInfo == null ? "" : " AND " + spaceInfo) + " LIMIT 0,500");
						Set<Map<String, Object>> list = new LinkedHashSet<Map<String, Object>>(searchList
								.size());
						for (Map<String, Object> value : searchList) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), (String) value.get("name"),
									(Integer) value.get("namestatus"));
							value.put("isfriend", (value.get("uid").equals(space.get("uid")) || (Common
									.in_array((String[]) space.get("friends"), value.get("uid")))) ? true
									: false);
							value.put("gColor", Common.getColor(request, response, (Integer) value
									.get("groupid")));
							value.put("gIcon", Common.getIcon(request, response, (Integer) value
									.get("groupid")));
							list.add(value);
						}
						request.setAttribute("list", list);
					}
					Common.realname_get(sGlobal, sConfig, sNames, space);
				} else {
					StringBuffer yearHTML = new StringBuffer();
					int nowy = Integer.valueOf(Common.sgmdate(request, "yyyy", 0));
					for (int i = 0; i < 50; i++) {
						int they = nowy - i;
						yearHTML.append("<option value=\"" + they + "\">" + they + "</option>");
					}
					request.setAttribute("yearhtml", yearHTML);
					Map sexArr = new HashMap();
					sexArr.put(space.get("sex").toString(), " checked");
					request.setAttribute("sexarr", sexArr);
					String all = request.getParameter("all");
					StringBuffer birthYearHTML = new StringBuffer();
					for (int i = 0; i < 100; i++) {
						int they = nowy - i;
						String selectStr = "";
						if (Common.empty(all)) {
							selectStr = they == (Integer) space.get("birthyear") ? " selected" : "";
						}
						birthYearHTML.append("<option value=\"" + they + "\"" + selectStr + ">" + they
								+ "</option>");
					}
					request.setAttribute("birthyearhtml", birthYearHTML.toString());
					String birthMonthHTML = "";
					for (int i = 1; i < 13; i++) {
						String selectStr = "";
						if (Common.empty(all)) {
							selectStr = i == (Integer) space.get("birthmonth") ? " selected" : "";
						}
						birthMonthHTML += "<option value=\"" + i + "\"" + selectStr + ">" + i + "</option>";
					}
					request.setAttribute("birthmonthhtml", birthMonthHTML.toString());
					StringBuffer birthdayHTML = new StringBuffer();
					for (int i = 1; i < 29; i++) {
						String selectStr = "";
						if (Common.empty(all)) {
							selectStr = i == (Integer) space.get("birthday") ? " selected" : "";
						}
						birthdayHTML
								.append("<option value=\"" + i + "\"" + selectStr + ">" + i + "</option>");
					}
					request.setAttribute("birthdayhtml", birthdayHTML.toString());
					String bloodHTML = "";
					for (String value : new String[] {"A", "B", "O", "AB"}) {
						String selectStr = "";
						if (Common.empty(all)) {
							selectStr = value.equals(space.get("blood")) ? " selected" : "";
						}
						bloodHTML += "<option value=\"" + value + "\"" + selectStr + ">" + value
								+ "</option>";
					}
					request.setAttribute("bloodhtml", bloodHTML.toString());
					Map marryArr = new HashMap();
					marryArr.put(space.get("marry").toString(), " selected");
					request.setAttribute("marryarr", marryArr);
					List<Integer> removeKeys = new ArrayList<Integer>(fields.size());
					for (Entry<Integer, Map<String, Object>> e : fields.entrySet()) {
						Map<String, Object> fValue = e.getValue();
						if (!Common.empty(fValue.get("allowsearch"))) {
							if ("text".equals(fValue.get("formtype"))) {
								fValue.put("html", "<input type=\"text\" name=\"field_" + e.getKey()
										+ "\" value=\"\" class=\"t_input\">");
							} else {
								StringBuffer HTML = new StringBuffer();
								HTML.append("<select name=\"field_" + e.getKey()
										+ "\"><option value=\"\">---</option>");
								String[] optionArr = fValue.get("choice").toString().split("\n");
								for (String ov : optionArr) {
									ov = ov.trim();
									if (!"".equals(ov)) {
										HTML.append("<option value=\"" + ov + "\">" + ov + "</option>");
									}
								}
								HTML.append("</select>");
								fValue.put("html", HTML.toString());
							}
						} else {
							removeKeys.add(e.getKey());
						}
					}
					for (Integer removeKey : removeKeys) {
						fields.remove(removeKey);
					}
					request.setAttribute("fields", fields);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return showMessage(request, response, e.getMessage());
		}
		request.setAttribute("op", op);
		request.setAttribute("uid", uid);
		return include(request, response, sConfig, sGlobal, "cp_friend.jsp");
	}

	
	public ActionForward cp_import(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		if (!Common.checkPerm(request, response, "allowblog")) {
			MessageVO msgVO = Common.ckSpaceLog(request);
			if (msgVO != null) {
				return showMessage(request, response, msgVO);
			}
			return showMessage(request, response, "no_privilege");
		}
		if (!cpService.checkRealName(request, "blog")) {
			return showMessage(request, response, "no_privilege_realname");
		}
		if (!cpService.checkVideoPhoto(request, response, "blog")) {
			return showMessage(request, response, "no_privilege_videophoto");
		}
		switch (cpService.checkNewUser(request, response)) {
			case 1:
				break;
			case 2:
				return showMessage(request, response, "no_privilege_newusertime", "", 1, String
						.valueOf(sConfig.get("newusertime")));
			case 3:
				return showMessage(request, response, "no_privilege_avatar");
			case 4:
				return showMessage(request, response, "no_privilege_friendnum", "", 1, String.valueOf(sConfig
						.get("need_friendnum")));
			case 5:
				return showMessage(request, response, "no_privilege_email");
		}
		int waitTime = Common.checkInterval(request, response, "post");
		if (waitTime > 0) {
			return showMessage(request, response, "operating_too_fast", "", 1, String.valueOf(waitTime));
		}

		try {
			String siteUrl = Common.getSiteUrl(request);
			File userFile = new File(JavaCenterHome.jchRoot + "./data/temp/" + sGlobal.get("supe_uid")
					+ ".data");
			if (submitCheck(request, "importsubmit")) {
				Map reward = Common.getReward("blogimport", false, 0, "", true, request, response);
				Map space = (Map) request.getAttribute("space");
				int spaceExperience = (Integer) space.get("experience");
				int spaceCredit = (Integer) space.get("credit");
				int rewardExperience = (Integer) reward.get("experience");
				int rewardCredit = (Integer) reward.get("credit");
				if (spaceExperience < rewardExperience) {
					return showMessage(request, response, "experience_inadequate", "", 1, new String[] {
							String.valueOf(spaceExperience), String.valueOf(rewardExperience)});
				}
				if (spaceCredit < rewardCredit) {
					return showMessage(request, response, "integral_inadequate", "", 1, new String[] {
							String.valueOf(spaceCredit), String.valueOf(rewardCredit)});
				}
				String url = request.getParameter("url").trim();
				Map urls = cpService.parseUrl(url);
				if (Common.empty(url) || urls.isEmpty()) {
					return showMessage(request, response, "url_is_not_correct");
				}
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				config.setServerURL(new URL(url));
				XmlRpcClient client = new XmlRpcClient();
				client.setConfig(config);
				Vector params = new Vector();
				params.addElement("blog");
				params.addElement(Common.sHtmlSpecialChars(Common.siconv(request.getParameter("username"),
						"utf-8", "", "")));
				params.addElement(Common.sHtmlSpecialChars(request.getParameter("password")));
				params.addElement(sConfig.get("importnum"));
				Object[] results = (Object[]) client.execute("metaWeblog.getRecentPosts", params);
				if(results == null || results.length == 0) {
					return showMessage(request, response, "blog_import_no_data", null, 1, "<textarea name=\"tmp[]\" style=\"width:98%;\" rows=\"4\">no data</textarea>");
				}
				HashMap last = (HashMap) results[results.length - 1];
				if(last.containsKey("postid") == false) {
					return showMessage(request, response, "blog_import_no_data", null, 1, Common.implode(last, ","));
				}
				PHPSerializer phpSerializer = new PHPSerializer(JavaCenterHome.JCH_CHARSET);
				FileHelper.writeFile(userFile, phpSerializer.serialize(results));

				request.setAttribute("results", results);
				request.setAttribute("incount", 0);

			} else if (submitCheck(request, "import2submit")) {
				ArrayList results = null;
				if (userFile.exists()) {
					String result = FileHelper.readFile(userFile);
					if (Common.empty(result) == false) {
						PHPSerializer phpSerializer = new PHPSerializer(JavaCenterHome.JCH_CHARSET);
						results = ((AssocArray) phpSerializer.unserialize(result)).toArrayList();
					}
				}
				String[] ids = request.getParameterValues("ids[]");
				if (Common.empty(results) || Common.empty(ids)) {
					return showMessage(request, response, "choose_at_least_one_log", "cp.jsp?ac=import");
				}
				int allCount = 0;
				int inCount = 0;
				ArrayList newResults = new ArrayList();
				for (int i = 0, size = results.size(); i < size; i++) {
					int key = i;
					allCount += 1;
					Map currBlog = ((AssocArray) results.get(i)).toHashMap();
					if (currBlog.get("dateCreated") instanceof Calendar) {
						Calendar calendar = (Calendar) currBlog.get("dateCreated");
						int dateline = (int) (calendar.getTimeInMillis() / 1000);
						currBlog.put("dateCreated", Common.gmdate("yyyyMMdd'T'HH:mm:ss", dateline, String
								.valueOf(sConfig.get("timeoffset"))));
					}
					if (Common.in_array(ids, key)) {
						Map value = (Map) Common.sAddSlashes(currBlog);
						int dateline = Common.strToTime(value.get("dateCreated").toString(), String
								.valueOf(sConfig.get("timeoffset")), "yyyyMMdd'T'HH:mm:ss");
						String subject = Common.getStr(value.get("title").toString(), 80, true, true, true,
								0, 0, request, response);
						String message = value.containsKey("description") ? value.get("description")
								.toString() : value.get("content").toString();
						message = Common.getStr(message, 0, true, true, true, 0, 1, request, response);
						message = blogService.checkHtml(request, response, message);
						if (Common.empty(subject) || Common.empty(message)) {
							currBlog.put("status", "--");
							currBlog.put("blogid", 0);
							continue;
						}
						Map blogarr = new HashMap();
						blogarr.put("uid", sGlobal.get("supe_uid"));
						blogarr.put("username", sGlobal.get("supe_username"));
						blogarr.put("subject", subject);
						blogarr.put("pic", blogService.getMessagePic(message));
						blogarr.put("dateline", dateline != 0 ? dateline : sGlobal.get("timestamp"));
						int blogId = dataBaseService.insertTable("blog", blogarr, true, false);
						Map fieldarr = new HashMap();
						fieldarr.put("blogid", blogId);
						fieldarr.put("uid", sGlobal.get("supe_uid"));
						fieldarr.put("message", message);
						fieldarr.put("postip", Common.getOnlineIP(request));
						fieldarr.put("related", "");
						fieldarr.put("target_ids", "");
						fieldarr.put("hotuser", "");
						dataBaseService.insertTable("blogfield", fieldarr, false, false);
						inCount += 1;

						currBlog.put("status", "OK");
						currBlog.put("blogid", blogId);
					} else {
						currBlog.put("status", "--");
						currBlog.put("blogid", 0);
					}
					newResults.add(currBlog);
				}
				if (inCount != 0) {
					Common.getReward("blogimport", true, 0, "", true, request, response);
					userFile.delete();
				}
				request.setAttribute("results", newResults);
				request.setAttribute("incount", inCount);
			} else if (submitCheck(request, "resubmit")) {
				userFile.delete();
			}
			request.setAttribute("siteurl", siteUrl);
		} catch (XmlRpcException xre) {
			return showMessage(request, response, "blog_import_no_data", null, 1, "<textarea name=\"tmp[]\" style=\"width:98%;\" rows=\"4\">"+xre.code+", "+xre.getMessage()+"</textarea>");
		} catch (IllegalAccessException iace) {
			iace.printStackTrace();
		} catch (IllegalArgumentException iare) {
			iare.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return showMessage(request, response, e.getMessage());
		}
		return include(request, response, sConfig, sGlobal, "cp_import.jsp");
	}

	public ActionForward cp_invite(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		String siteURL = Common.getSiteUrl(request);
		int maxCount = 50;
		Map<String, Integer> reward = Common.getReward("invitecode", false, 0, "", true, request, response);
		int appId = Common.intval(request.getParameter("app"));
		String inviteApp, inviteCode = "";
		inviteApp = "";
		if (Common.empty(reward.get("credit")) || appId != 0) {
			reward.put("credit", 0);
			inviteCode = Common.spaceKey(space, sConfig, appId);
		}
		String spaceURL = siteURL + "space.jsp?uid=" + sGlobal.get("supe_uid");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		String sizeType = "middle";
		String avatar = "<img src=\"" + siteURL
				+ Common.avatar((Integer) space.get("uid"), sizeType, true, sGlobal, sConfig)
				+ "\" onerror=\"this.onerror=null;this.src=\'" + siteURL + "data/avatar/noavatar_" + sizeType
				+ ".gif\'\">";
		String[] mailArgs = {
				"<a href=\"" + spaceURL + "\">" + avatar + "</a><br>" + sNames.get(space.get("uid")),
				sNames.get(space.get("uid")), (String) sConfig.get("sitename"), "", "", spaceURL, ""};
		Map<String, Object> appInfo = null;
		if (appId != 0) {
			List<Map<String, Object>> appList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("myapp") + " WHERE appid='"+appId+"'");
			if (!appList.isEmpty()) {
				appInfo = appList.get(0);
				inviteApp = "&amp;app=" + appId;
				mailArgs[6] = (String) appInfo.get("appname");
			} else {
				appId = 0;
			}
		}
		try {
			if (submitCheck(request, "emailinvite")) {
				if (!Common.empty(sConfig.get("closeinvite"))) {
					return showMessage(request, response, "close_invite");
				}
				Object[] mails = Common.uniqueArray(Common.trim(request.getParameter("email")).split(","));
				int inviteNum = 0;
				List<String> failingMail = new ArrayList<String>(mails.length);
				for (Object mail : mails) {
					String value = mail.toString().trim();
					if (Common.empty(value) || !Common.isEmail(value)) {
						failingMail.add(value);
						continue;
					}

					if (reward.get("credit") != 0) {
						int credit = reward.get("credit") * (inviteNum + 1);
						if (credit > (Integer) space.get("credit")) {
							failingMail.add(value);
							continue;
						}
						String code = Common.getRandStr(6, false).toLowerCase();
						Map<String, Object> setArr = new HashMap<String, Object>();
						setArr.put("uid", sGlobal.get("supe_uid"));
						setArr.put("code", code);
						setArr.put("email", Common.sAddSlashes(value));
						setArr.put("type", 1);
						int id = dataBaseService.insertTable("invite", setArr, true, false);
						if (id != 0) {
							mailArgs[4] = siteURL + "invite.jsp?" + id + code + inviteApp;
							createMail(request, response, sConfig, sNames, space, value, mailArgs, appInfo);
							inviteNum++;
						} else {
							failingMail.add(value);
						}
					} else {
						mailArgs[4] = siteURL + "invite.jsp?u=" + space.get("uid") + "&amp;c=" + inviteCode
								+ inviteApp;
						if (appId != 0) {
							mailArgs[6] = (String) appInfo.get("appname");
						}
						createMail(request, response, sConfig, sNames, space, value, mailArgs, appInfo);
					}
				}
				if (reward.get("credit") != 0 && inviteNum != 0) {
					int credit = reward.get("credit") * inviteNum;
					dataBaseService
							.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
									+ " SET credit=credit-" + credit + " WHERE uid='"
									+ sGlobal.get("supe_uid") + "'");
				}
				if (!failingMail.isEmpty()) {
					return showMessage(request, response, "send_result_2", null, 1, Common.implode(
							failingMail, "<br>"));
				} else {
					return showMessage(request, response, "send_result_1");
				}
			}
			String op = request.getParameter("op");
			if ("resend".equals(op)) {
				int id = Common.intval(request.getParameter("id"));
				if (submitCheck(request, "resendsubmit")) {
					if (id == 0) {
						return showMessage(request, response, "send_result_3");
					}
					List<Map<String, Object>> inviteList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("invite") + " WHERE id='" + id + "' AND uid='"
							+ sGlobal.get("supe_uid") + "' ORDER BY id DESC");
					if (!inviteList.isEmpty()) {
						Map<String, Object> invite = inviteList.get(0);
						String inviteURL = null;
						if (reward.get("credit") != 0) {
							inviteURL = siteURL + "invite.jsp?" + invite.get("id") + invite.get("code");
						} else {
							inviteURL = siteURL + "invite.jsp?u=" + space.get("uid") + "&amp;c=" + inviteCode;
						}
						mailArgs[4] = inviteURL;
						createMail(request, response, sConfig, sNames, space, (String) invite.get("email"),
								mailArgs, appInfo);
						return showMessage(request, response, "send_result_1", request.getParameter("refer"));
					} else {
						return showMessage(request, response, "send_result_3");
					}
				}
				request.setAttribute("id", id);
			} else if ("delete".equals(op)) {
				int id = Common.intval(request.getParameter("id"));
				if (id == 0) {
					return showMessage(request, response, "there_is_no_record_of_invitation_specified");
				}
				List<Map<String, Object>> inviteList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("invite") + " WHERE id='" + id + "' AND uid='"
						+ sGlobal.get("supe_uid") + "'");
				if (!inviteList.isEmpty()) {
					if (submitCheck(request, "deletesubmit")) {
						dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("invite")
								+ " WHERE id='" + id + "'");
						return showMessage(request, response, "do_success", request.getParameter("refer"));
					}
				} else {
					return showMessage(request, response, "there_is_no_record_of_invitation_specified");
				}
				request.setAttribute("id", id);
			} else {
				List list = new ArrayList();
				List<Map<String, Object>> fList = new ArrayList<Map<String, Object>>();
				int count = 0;
				List<Map<String, Object>> inviteList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("invite") + " WHERE uid='" + sGlobal.get("supe_uid")
						+ "' ORDER BY id DESC");
				int credit = reward.get("credit");
				String inviteURL = null;
				List<Map<String, Object>> mailList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> value : inviteList) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("fuid"), (String) value
							.get("fusername"), "", 0);
					if (!Common.empty(value.get("fuid"))) {
						fList.add(value);
					} else {
						if (credit != 0) {
							inviteURL = siteURL + "invite.jsp?" + value.get("id") + value.get("code");
						} else {
							inviteURL = siteURL + "invite.jsp?u=" + space.get("uid") + "&amp;c=" + inviteCode
									+ inviteApp;
						}
						if (!Common.empty(value.get("type"))) {
							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("email", value.get("email"));
							tempMap.put("url", inviteURL);
							tempMap.put("id", value.get("id"));
							mailList.add(tempMap);
						} else {
							list.add(inviteURL);
							count++;
						}
					}
				}
				request.setAttribute("maillist", mailList);
				request.setAttribute("flist", fList);
				if (inviteURL != null) {
					mailArgs[4] = inviteURL;
				} else if (credit != 0) {
					mailArgs[4] = siteURL + "invite.jsp?{xxxxxx}";
				} else {
					mailArgs[4] = siteURL + "invite.jsp?u=" + space.get("uid") + "&amp;c=" + inviteCode
							+ inviteApp;
				}
				Common.realname_get(sGlobal, sConfig, sNames, space);
				if (credit != 0) {
					request.setAttribute("list_str", list.isEmpty() ? null : Common.implode(list, "\n"));

					int maxcount_my = maxCount - count;
					int maxInviteNum = credit == 0 ? maxcount_my : new Float(Float.valueOf((Integer) space
							.get("credit"))
							/ credit).intValue();
					if (maxInviteNum > maxcount_my) {
						maxInviteNum = maxcount_my;
					}
					if (maxInviteNum < 0) {
						maxInviteNum = 0;
					}
					request.setAttribute("maxinvitenum", maxInviteNum);
					if (submitCheck(request, "invitesubmit")) {
						if (!Common.empty(sConfig.get("closeinvite"))) {
							return showMessage(request, response, "close_invite");
						}
						int inviteNum = Common.intval(request.getParameter("invitenum"));
						if (inviteNum > maxInviteNum) {
							inviteNum = maxInviteNum;
						}
						int decreaseCredit = credit * inviteNum;
						if (inviteNum == 0 || (credit != 0 && decreaseCredit > (Integer) space.get("credit"))) {
							return showMessage(request, response, "invite_error");
						}
						List<String> codes = new ArrayList<String>(inviteNum);
						for (int i = 0; i < inviteNum; i++) {
							codes.add("(" + sGlobal.get("supe_uid") + ", '"
									+ Common.getRandStr(6, false).toLowerCase() + "')");
						}
						if (!codes.isEmpty()) {
							dataBaseService.executeUpdate("INSERT INTO "
									+ JavaCenterHome.getTableName("invite") + " (uid, code) VALUES "
									+ Common.implode(codes, ","));
							if (decreaseCredit != 0) {
								dataBaseService.executeUpdate("UPDATE "
										+ JavaCenterHome.getTableName("space") + " SET credit=credit-"
										+ decreaseCredit + " WHERE uid='" + sGlobal.get("supe_uid") + "'");
							}
						}
						return showMessage(request, response, "do_success", "cp.jsp?ac=invite", 0);
					}
				}
				request.setAttribute("uri", request.getContextPath() + "/");
				request.setAttribute("appid", appId);
				if (appId != 0) {
					request.setAttribute("appinfo", appInfo);
				}
				request.setAttribute("credit", credit);
				request.setAttribute("mailvar", mailArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return showMessage(request, response, e.getMessage());
		}
		return include(request, response, sConfig, sGlobal, "cp_invite.jsp");
	}

	public ActionForward cp_magic(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		

		String op = request.getParameter("op");
		op = Common.empty(op) ? "view" : op;
		String mid = Common.trim(request.getParameter("mid"));
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int timestamp = (Integer) sGlobal.get("timestamp");
		Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, supe_uid);

		if (!Common.checkPerm(request, response, "allowmagic")) {
			MessageVO msgVO = Common.ckSpaceLog(request);
			if (msgVO != null) {
				return showMessage(request, response, msgVO);
			}
			return showMessage(request, response, "magic_groupid_not_allowed");
		}
		Map<String, Object> magic = null;
		if (!mid.equals("")) {
			Object result = magicService.magic_get(mid);
			if (result instanceof MessageVO) {
				return showMessage(request, response, (MessageVO) result);
			}
			magic = (Map<String, Object>) result;
		}
		boolean sc_buysubmit = false;
		boolean sc_presentsubmit = false;
		try {
			sc_buysubmit = submitCheck(request, "buysubmit");
			if (!sc_buysubmit) {
				sc_presentsubmit = submitCheck(request, "presentsubmit");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		if (sc_buysubmit) {

			if (mid.equals("")) {
				return showMessage(request, response, "unknown_magic");
			}
			Object result = magicService.magic_buy_get(request, response, magic, sGlobal, space);
			if (result instanceof MessageVO) {
				return showMessage(request, response, (MessageVO) result);
			}
			Map<String, Object> datas = (Map<String, Object>) result;
			Map<String, Object> magicstore = (Map<String, Object>) datas.get("magicstore");
			Map<String, Object> coupon = (Map<String, Object>) datas.get("coupon");
			result = magicService
					.magic_buy_post(request, response, sGlobal, space, magic, magicstore, coupon);
			if (result instanceof MessageVO) {
				return showMessage(request, response, (MessageVO) result);
			}
			int charge = (Integer) result;

			if ((Integer) magic.get("experience") != 0) {
				String buynumS = request.getParameter("buynum");
				int buynum = buynumS != null ? Common.intval(buynumS.trim()) : 0;
				return showMessage(request, response, "magicbuy_success_with_experence", request
						.getParameter("refer"), 0, charge + "", ((Integer) magic.get("experience") * buynum)
						+ "");
			} else {
				return showMessage(request, response, "magicbuy_success", request.getParameter("refer"), 0,
						charge + "");
			}

		} else if (sc_presentsubmit) {

			if (mid.equals("")) {
				return showMessage(request, response, "unknown_magic");
			}

			if (mid.equals("license")) {
				return showMessage(request, response, "magic_can_not_be_presented");
			}
			String fusername = request.getParameter("fusername");
			fusername = fusername == null ? fusername : fusername.trim();
			if (Common.empty(fusername)) {
				return showMessage(request, response, "bad_friend_username_given");
			}
			try {
				fusername = Common.getStr(fusername, 15, false, false, false, 0, 0, request, response);
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("friend") + " WHERE uid = '" + supe_uid
					+ "' AND fusername='" + fusername + "'");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
			if (value == null) {
				return showMessage(request, response, "bad_friend_username_given");
			}
			int fuid = (Integer) value.get("fuid");
			Map<String, Map<String, Object>> usermagics = new HashMap<String, Map<String, Object>>();
			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("usermagic")
					+ " WHERE uid='" + supe_uid + "' AND mid IN('license', '" + mid + "')");
			for (Map<String, Object> value_ : query) {
				usermagics.put((String) value_.get("mid"), value_);
			}

			Map<String, Object> tempMap = usermagics.get("license");
			if (tempMap == null || (Integer) tempMap.get("count") == 0) {
				return showMessage(request, response, "has_no_more_present_magic");
			}

			tempMap = usermagics.get(mid);
			if (tempMap == null || (Integer) tempMap.get("count") == 0) {
				return showMessage(request, response, "has_no_more_magic", null, 0, (String) magic
						.get("name"), "a_buy_" + mid, "cp.jsp?ac=magic&op=buy&mid=" + mid);
			}
			dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("usermagic")
					+ " SET count = count - 1 WHERE uid = '" + supe_uid + "' AND mid IN ('license', '" + mid
					+ "')");
			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("usermagic")
					+ " WHERE uid='" + fuid + "' AND mid='" + mid + "'");
			value = query.size() > 0 ? query.get(0) : null;
			int count = value != null ? (Integer) value.get("count") + 1 : 1;

			Map<String, Object> insertData = new HashMap<String, Object>();
			insertData.put("uid", fuid);
			insertData.put("username", fusername);
			insertData.put("mid", mid);
			insertData.put("count", count);
			dataBaseService.insertTable("usermagic", insertData, false, true);
			insertData.clear();
			insertData.put("uid", fuid);
			insertData.put("username", fusername);
			insertData.put("mid", mid);
			insertData.put("count", 1);
			insertData.put("type", 2);
			insertData.put("fromid", supe_uid);
			insertData.put("credit", 0);
			insertData.put("dateline", timestamp);
			dataBaseService.insertTable("magicinlog", insertData, false, false);
			String note = Common.getMessage(request, "cp_magic_present_note", (String) magic.get("name"),
					"cp.jsp?ac=magic&view=me&mid=" + mid);
			note = note == null ? "magic_present_note" : note;
			cpService.addNotification(request, sGlobal, sConfig, fuid, "magic", note, false);
			return showMessage(request, response, "magicpresent_success", request.getParameter("refer"), 0,
					fusername);
		}

		if ("buy".equals(op)) {
			
			Object result = magicService.magic_buy_get(request, response, magic, sGlobal, space);
			if (result instanceof MessageVO) {
				return showMessage(request, response, (MessageVO) result);
			}
			Map<String, Object> datas = (Map<String, Object>) result;
			Map<String, Object> magicstore = (Map<String, Object>) datas.get("magicstore");
			Map<String, Object> coupon = (Map<String, Object>) datas.get("coupon");

			request.setAttribute("mid", mid);
			request.setAttribute("magicstore", magicstore);
			request.setAttribute("coupon", coupon);
			request.setAttribute("discount", datas.get("discount"));
			request.setAttribute("charge", datas.get("charge"));

			String ac = request.getParameter("ac");
			request.setAttribute("ac", ac != null ? ac.trim() : "");
			request.setAttribute("magic", magic);

		} else if ("present".equals(op)) {

			
			if (mid.equals("license")) {
				return showMessage(request, response, "magic_can_not_be_presented");
			}
			Map<String, Map<String, Object>> usermagics = new HashMap<String, Map<String, Object>>();
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("usermagic") + " WHERE uid='" + supe_uid
					+ "' AND mid IN('license', '" + mid + "')");
			for (Map<String, Object> value : query) {
				usermagics.put((String) value.get("mid"), value);
			}
			Map<String, Object> tempMap = usermagics.get("license");
			if (tempMap == null || (Integer) tempMap.get("count") == 0) {
				return showMessage(request, response, "has_no_more_present_magic");
			}
			tempMap = usermagics.get(mid);
			if (tempMap == null || (Integer) tempMap.get("count") == 0) {
				return showMessage(request, response, "has_no_more_magic", null, 0, (String) magic
						.get("name"), "a_buy_" + mid, "cp.jsp?ac=magic&op=buy&mid=" + mid);
			}

			request.setAttribute("mid", mid);
			request.setAttribute("magic", magic);
		} else if ("showusage".equals(op)) {
			
			if (mid.equals("")) {
				return showMessage(request, response, "unknown_magic");
			}
			request.setAttribute("mid", mid);
		} else if ("receive".equals(op)) {

			String uidS = request.getParameter("uid");
			int uid = uidS != null ? Common.intval(uidS.trim()) : 0;
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid='" + uid
					+ "' AND mid='gift' LIMIT 1");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
			String value_data;
			if (value != null && (value_data = (String) value.get("data")) != null && !value_data.equals("")) {
				Map<String, Object> data = Serializer.unserialize(value_data, false);
				if ((Integer) data.get("left") <= 0) {
					return showMessage(request, response, "magic_gift_already_given_out");
				}
				Map<Integer, Integer> receiver = (Map<Integer, Integer>) data.get("receiver");
				if (receiver == null) {
					receiver = new HashMap<Integer, Integer>();
					data.put("receiver", receiver);
				}
				int receiverIndex = 0;
				for (Entry<Integer, Integer> entry : receiver.entrySet()) {
					if (entry.getValue() == supe_uid) {
						return showMessage(request, response, "magic_had_got_gift");
					}
					receiverIndex = Math.max(receiverIndex, entry.getKey());
				}
				int data_left = (Integer) data.get("left");
				int data_chunk = (Integer) data.get("chunk");
				int credit = Math.min(data_chunk, data_left);
				receiver.put(++receiverIndex, supe_uid);
				data_left = data_left - credit;
				data.put("left", data_left);
				if (data_left > 0) {
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("data", Serializer.serialize(data));

					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("logid", value.get("logid"));
					dataBaseService.updateTable("magicuselog", setData, whereData);
				} else {
					dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("magicuselog")
							+ " WHERE logid = '" + value.get("logid") + "'");
				}
				dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit = credit + '" + credit + "' WHERE uid='" + supe_uid + "'");
				return showMessage(request, response, "magic_got_gift", null, 0, credit + "");
			} else {
				return showMessage(request, response, "magic_has_no_gift");
			}
		} else if ("appear".equals(op)) {
			
			Map<String, Object> session_member = (Map<String, Object>) sGlobal.get("session");
			if (session_member == null || (Integer) session_member.get("magichidden") == 0) {
				return showMessage(request, response, "magic_not_hidden_yet");
			}
			boolean scb = false;
			try {
				scb = submitCheck(request, "appearsubmit");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			if (scb) {

				Map<String, Object> setData = new HashMap<String, Object>();
				Map<String, Object> whereData = new HashMap<String, Object>();
				setData.put("magichidden", "0");
				whereData.put("uid", supe_uid);
				dataBaseService.updateTable("session", setData, whereData);
				setData.clear();
				setData.put("expire", timestamp);
				whereData.put("mid", "invisible");
				dataBaseService.updateTable("magicuselog", setData, whereData);
				return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
			}
		} else if ("retrieve".equals(op)) {

			
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid = '" + supe_uid
					+ "' AND mid = 'gift'");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : null;

			int leftcredit = 0;
			String dataS = null;
			if (value == null) {
				return showMessage(request, response, "not_set_gift");
			} else if ((dataS = (String) value.get("data")) != null && !dataS.equals("")) {
				Map<String, Object> data = Serializer.unserialize(dataS, false);
				leftcredit = (Integer) data.get("left");
			}
			boolean scb = false;
			try {
				scb = submitCheck(request, "retrievesubmit");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			if (scb) {
				dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("magicuselog")
						+ " WHERE uid = '" + supe_uid + "' AND mid = 'gift'");
				dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit = credit + " + leftcredit + " WHERE uid = '" + supe_uid + "'");
				return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
			}
			request.setAttribute("leftcredit", leftcredit);
		} else if ("cancelsuperstar".equals(op)) {
			

			mid = "superstar";
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("spacefield") + " WHERE uid = '" + supe_uid + "'");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
			Integer magicstar;
			if (value == null || (magicstar = (Integer) value.get("magicstar")) == null || magicstar == 0) {
				return showMessage(request, response, "not_superstar_yet");
			}
			boolean scb = false;
			try {
				scb = submitCheck(request, "cancelsubmit");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			if (scb) {
				Map<String, Object> setData = new HashMap<String, Object>();
				Map<String, Object> whereData = new HashMap<String, Object>();
				setData.put("magicstar", 0);
				whereData.put("uid", supe_uid);
				dataBaseService.updateTable("spacefield", setData, whereData);

				setData.clear();
				setData.put("expire", timestamp);
				whereData.put("mid", "superstar");
				dataBaseService.updateTable("magicuselog", setData, whereData);
				return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
			}
		} else if ("cancelflicker".equals(op)) {
			
			mid = "flicker";
			String idtype = "cid";
			String idS = request.getParameter("id");
			int id = idS != null ? Common.intval(idS.trim()) : 0;
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("comment") + " WHERE cid = '" + id + "' AND authorid = '"
					+ supe_uid + "'");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
			Integer magicflicker;
			if (value == null || (magicflicker = (Integer) value.get("magicflicker")) == null
					|| magicflicker == 0) {
				return showMessage(request, response, "no_flicker_yet");
			}
			boolean scb = false;
			try {
				scb = submitCheck(request, "cancelsubmit");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			if (scb) {
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("magicflicker", 0);
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("cid", id);
				whereData.put("authorid", supe_uid);
				dataBaseService.updateTable("comment", setData, whereData);
				return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
			}
			request.setAttribute("id", id);
			request.setAttribute("idtype", idtype);
			request.setAttribute("mid", mid);
		} else if ("cancelcolor".equals(op)) {
			
			mid = "color";
			String idS = request.getParameter("id");
			int id = idS != null ? Common.intval(idS.trim()) : 0;
			String idtype = request.getParameter("idtype");
			if (idtype == null) {
				return showMessage(request, response, "access error : 00001");
			}
			idtype = idtype.trim();
			Map<String, String> mapping = new HashMap<String, String>();
			mapping.put("blogid", "blogfield");
			mapping.put("tid", "thread");
			String tablename = mapping.get(idtype);
			if (Common.empty(tablename)) {
				return showMessage(request, response, "no_color_yet");
			}
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName(tablename) + " WHERE " + idtype + " = '" + id
					+ "' AND uid = '" + supe_uid + "'");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
			Integer magiccolor;
			if (value == null || (magiccolor = (Integer) value.get("magiccolor")) == null || magiccolor == 0) {
				return showMessage(request, response, "no_color_yet");
			}

			boolean scb = false;
			try {
				scb = submitCheck(request, "cancelsubmit");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			if (scb) {
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("magiccolor", 0);
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put(idtype, id);
				dataBaseService.updateTable(tablename, setData, whereData);
				query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("feed")
						+ " WHERE id = '" + id + "' AND idtype = '" + idtype + "'");
				Map<String, Object> feed = query.size() > 0 ? query.get(0) : null;
				if (feed != null) {
					String body_data = (String) feed.get("body_data");
					Map body_data_subMap = Serializer.unserialize(body_data, false);
					body_data_subMap.remove("magic_color");
					body_data = Serializer.serialize(body_data_subMap);

					setData.clear();
					whereData.clear();
					setData.put("body_data", body_data);
					whereData.put("feedid", feed.get("feedid"));
					dataBaseService.updateTable("feed", setData, whereData);
				}
				return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
			}

			request.setAttribute("id", id);
			request.setAttribute("idtype", idtype);
			request.setAttribute("mid", mid);
		} else if ("cancelframe".equals(op)) {
			
			mid = "frame";
			String idtype = "picid";
			String idS = request.getParameter("id");
			int id = idS != null ? Common.intval(idS.trim()) : 0;
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("pic") + " WHERE picid = '" + id + "' AND uid = '"
					+ supe_uid + "'");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
			Integer magicframe;
			if (value == null || (magicframe = (Integer) value.get("magicframe")) == null || magicframe == 0) {
				return showMessage(request, response, "no_frame_yet");
			}
			boolean scb = false;
			try {
				scb = submitCheck(request, "cancelsubmit");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			if (scb) {
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("magicframe", 0);
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("picid", id);
				dataBaseService.updateTable("pic", setData, whereData);
				return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
			}

			request.setAttribute("id", id);
			request.setAttribute("idtype", idtype);
			request.setAttribute("mid", mid);
		} else if ("cancelbgimage".equals(op)) {
			
			mid = "bgimage";
			String idtype = "blogid";
			String idS = request.getParameter("id");
			int id = idS != null ? Common.intval(idS.trim()) : 0;
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("blogfield") + " WHERE blogid = '" + id + "' AND uid = '"
					+ supe_uid + "'");
			Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
			Integer magicpaper;
			if (value == null || (magicpaper = (Integer) value.get("magicpaper")) == null || magicpaper == 0) {
				return showMessage(request, response, "no_bgimage_yet");
			}
			boolean scb = false;
			try {
				scb = submitCheck(request, "cancelsubmit");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			if (scb) {
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("magicpaper", 0);
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("blogid", id);
				dataBaseService.updateTable("blogfield", setData, whereData);
				return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
			}

			request.setAttribute("id", id);
			request.setAttribute("idtype", idtype);
			request.setAttribute("mid", mid);
		} else {

			String view = request.getParameter("view");
			if (view != null) {
				view = view.trim();
			}
			

			if ("me".equals(view)) {
				
				Map<String, String> types = new HashMap<String, String>();
				types.put("list", " class=\"active\"");
				request.setAttribute("types", types);
				Map<String, Map<String, Object>> list = null;
				StringBuilder ids = new StringBuilder();
				Map<String, Map<String, Object>> magics = new HashMap<String, Map<String, Object>>();
				List<Map<String, Object>> query = null;
				if (!mid.equals("")) {
					magics.put(mid, magic);
					ids.append("'");
					ids.append(mid);
					ids.append("'");
				} else {
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("magic") + " WHERE close = '0'");
					String tempS;
					Pattern pattern = Pattern.compile(",");
					boolean existMid = false;
					for (Map<String, Object> value : query) {
						tempS = (String) value.get("forbiddengid");
						if (tempS != null) {
							value.put("forbiddengid", pattern.split(tempS));
						} else {
							value.put("forbiddengid", new String[0]);
						}
						tempS = (String) value.get("mid");
						magics.put(tempS, value);
						if (existMid) {
							ids.append(",");
						} else {
							existMid = true;
						}
						ids.append("'");
						ids.append(tempS);
						ids.append("'");
					}
				}
				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("usermagic") + " WHERE uid='" + supe_uid
						+ "' AND mid IN (" + ids.toString() + ") AND count > 0");
				if (query.size() > 0) {
					list = new LinkedHashMap<String, Map<String, Object>>();
					for (Map<String, Object> value : query) {
						list.put((String) value.get("mid"), value);
					}
				}
				request.setAttribute("list", list);
				request.setAttribute("magics", magics);
				request.setAttribute("mid", mid);
			} else if ("log".equals(view)) {
				

				String type = request.getParameter("type");
				type = type != null
						&& ((type = type.trim()).equals("in") || type.equals("out") || type.equals("present")) ? type
						: "in";
				request.setAttribute("gType", type);

				Map<String, String> types = new HashMap<String, String>();
				types.put(type, " class=\"active\"");
				request.setAttribute("types", types);
				int perpage = 20;
				String pageGet = request.getParameter("page");
				int page = Common.empty(pageGet) ? 0 : Common.intval(pageGet);
				if (page < 1)
					page = 1;
				int start = (page - 1) * perpage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String result = Common.ckStart(start, perpage, maxPage);
				if (result != null) {
					return showMessage(request, response, result);
				}

				List<Map<String, Object>> list = null;
				List<Map<String, Object>> query = null;
				int count = 0;
				if ("in".equals(type)) {

					List<Integer> uids = null;
					query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
							+ JavaCenterHome.getTableName("magicinlog") + " WHERE uid = '" + supe_uid + "'");
					count = query.size() > 0 ? (Integer) query.get(0).get("cont") : 0;
					if (count != 0) {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("magicinlog") + " WHERE uid = '" + supe_uid
								+ "' ORDER BY dateline DESC LIMIT " + start + ", " + perpage);
						list = query.size() > 0 ? query : null;
						uids = new ArrayList<Integer>();
						for (Map<String, Object> value : query) {
							value.put("dateline", Common.sgmdate(request, "MM-dd HH:mm", (Integer) value
									.get("dateline"), true));
							if ((Integer) value.get("type") == 2) {
								uids.add((Integer) value.get("fromid"));
							}
						}

					}
					if (uids != null && uids.size() > 0) {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("member") + " WHERE uid IN ("
								+ Common.sImplode(uids) + ")");
						for (Map<String, Object> value : query) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), "", 0);
						}
						Common.realname_get(sGlobal, sConfig, sNames, space);
					}
				} else if ("present".equals(type)) {

					query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
							+ JavaCenterHome.getTableName("magicinlog") + " WHERE type = 2 AND fromid = '"
							+ supe_uid + "'");
					count = query.size() > 0 ? (Integer) query.get(0).get("cont") : 0;
					if (count != 0) {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("magicinlog")
								+ " WHERE type = 2 AND fromid = '" + supe_uid
								+ "' ORDER BY dateline DESC LIMIT " + start + ", " + perpage);
						list = query.size() > 0 ? query : null;
						for (Map<String, Object> value : query) {
							value.put("dateline", Common.sgmdate(request, "MM-dd HH:mm", (Integer) value
									.get("dateline"), true));
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
									(String) value.get("username"), "", 0);
						}
					}
					Common.realname_get(sGlobal, sConfig, sNames, space);
				} else {
					query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
							+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid = '" + supe_uid + "'");
					count = query.size() > 0 ? (Integer) query.get(0).get("cont") : 0;
					if (count != 0) {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("magicuselog") + " WHERE uid = '" + supe_uid
								+ "' ORDER BY dateline DESC LIMIT " + start + ", " + perpage);
						list = query.size() > 0 ? query : null;
						for (Map<String, Object> value : query) {
							value.put("dateline", Common.sgmdate(request, "MM-dd HH:mm", (Integer) value
									.get("dateline"), true));
							value.put("data", Serializer.unserialize((String) value.get("data"), false));
							value.put("expire", Common.sgmdate(request, "MM-dd HH:mm", (Integer) value
									.get("expire"), false));
						}
					}
				}
				String theurl = "cp.jsp?ac=magic&view=log&type=" + type;
				String multi = Common.multi(request, count, perpage, page, maxPage, theurl, "", "");
				request.setAttribute("multi", multi);
				request.setAttribute("list", list);
			} else {
				

				view = "store";
				String order = request.getParameter("order");
				if (order != null) {
					order = order.trim();
				}
				order = "hot".equals(order) ? order : "default";
				Map<String, String> orders = new HashMap<String, String>();
				orders.put(order, " class=\"active\"");
				request.setAttribute("orders", orders);

				List<Map<String, Object>> query;
				Map<String, Map<String, Object>> magics = new HashMap<String, Map<String, Object>>();
				List<String> ids = null;
				Map<String, Map<String, Object>> list = new LinkedHashMap<String, Map<String, Object>>();
				String[] blacklist = {"coupon"};
				if (!mid.equals("")) {
					magics.put(mid, magic);
					ids = new ArrayList<String>(1);
					ids.add(mid);
				} else {
					String orderby = order.equals("hot") ? "" : " ORDER BY displayorder";
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("magic") + orderby);
					String tempS;
					Pattern pattern = Pattern.compile(",");
					ids = new ArrayList<String>(query.size());
					for (Map<String, Object> value : query) {
						if ((Integer) value.get("close") == 1 || Common.in_array(blacklist, value.get("mid"))) {
							continue;
						}
						tempS = (String) value.get("forbiddengid");
						if (tempS != null) {
							value.put("forbiddengid", pattern.split(tempS));
						} else {
							value.put("forbiddengid", new String[0]);
						}
						tempS = (String) value.get("mid");
						magics.put(tempS, value);
						ids.add(tempS);
					}
				}

				if (Common.empty(magics)) {
					return showMessage(request, response, "magic_store_is_closed");
				}

				String orderby = order.equals("hot") ? " ORDER BY sellcount DESC" : "";
				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("magicstore") + " WHERE mid IN ("
						+ Common.sImplode(ids) + ")" + orderby);
				String[] oldids = new String[query.size()];
				int ti = 0;
				int providecount;
				String ts;
				for (Map<String, Object> value : query) {
					ts = (String) value.get("mid");
					list.put(ts, value);
					oldids[ti++] = ts;
					providecount = (Integer) magics.get(ts).get("providecount");
					if ((Integer) value.get("storage") < providecount
							&& (Integer) value.get("lastprovide")
									+ (Integer) magics.get(ts).get("provideperoid") < timestamp) {
						dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("magicstore")
								+ " SET storage = '" + providecount + "', lastprovide = '" + timestamp
								+ "' WHERE mid = '" + ts + "'");
						list.get(ts).put("storage", providecount);
					}
				}

				List<String> newids = new ArrayList<String>();
				for (String id : ids) {
					if (!Common.in_array(oldids, id)) {
						newids.add(id);
					}
				}

				int newidsSize = newids.size();
				if (newidsSize > 0) {
					String[] inserts = new String[newidsSize];
					ti = 0;
					StringBuilder builder = new StringBuilder();
					Map<String, Object> listValue;
					for (String id : newids) {
						builder.delete(0, builder.length());
						builder.append("('");
						builder.append(id);
						builder.append("', '");
						builder.append(magics.get(id).get("providecount"));
						builder.append("', '");
						builder.append(timestamp);
						builder.append("')");
						inserts[ti++] = builder.toString();
						listValue = new HashMap<String, Object>();
						listValue.put("mid", id);
						listValue.put("storage", magics.get(id).get("providecount"));
						listValue.put("lastprovide", timestamp);
						list.put(id, listValue);
					}
					dataBaseService.execute("INSERT INTO " + JavaCenterHome.getTableName("magicstore")
							+ "(mid, storage, lastprovide) VALUES " + Common.implode(inserts, ","));
				}

				if (order.equals("default")) {
					Map<String, Map<String, Object>> tempMap = new LinkedHashMap<String, Map<String, Object>>();
					for (String id : ids) {
						tempMap.put(id, list.get(id));
					}
					list = tempMap;
				}

				request.setAttribute("space", space);
				request.setAttribute("blacklist", blacklist);
				request.setAttribute("magics", magics);
				request.setAttribute("mid", mid);
				request.setAttribute("list", list);
			}

			Map<String, String> actives = new HashMap<String, String>();
			actives.put(view, " class=\"active\"");
			request.setAttribute("actives", actives);
		}

		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "cp_magic.jsp");
	}

	public ActionForward cp_mtag(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<Object, Map<String, Object>> profields = new HashMap<Object, Map<String, Object>>();
		List<Map<String, Object>> profieldList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("profield") + " ORDER BY displayorder");
		if (profieldList.size() > 0) {
			Map<Object, Object> textList = new LinkedHashMap<Object, Object>();
			List<Map<String, Object>> choiceList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> profield : profieldList) {
				if ("text".equals(profield.get("formtype"))) {
					textList.put(profield.get("fieldid"), profield.get("title"));
				} else {
					String[] choice = ((String) profield.get("choice")).split("\n");
					int size = choice.length;
					for (int i = 0; i < size; i++) {
						choice[i] = choice[i].trim();
					}
					profield.put("choice", choice);
					choiceList.add(profield);
				}
				profields.put(profield.get("fieldid"), profield);
			}
			request.setAttribute("textList", textList);
			request.setAttribute("choiceList", choiceList);
		}
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		String supe_username = (String) sGlobal.get("supe_username");
		int timestamp = (Integer) sGlobal.get("timestamp");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");

		String op = request.getParameter("op");
		String subop = request.getParameter("subop");
		if ("manage".equals(op)) {
			if (Common.empty(subop)) {
				subop = "base";
			}
			boolean managemtag = false;
			int tagId = Common.intval(request.getParameter("tagid"));
			try {
				Map<String, Object> mtag = Common.getMtag(request, response, supe_uid, tagId);
				int grade = (Integer) mtag.get("grade");
				if (submitCheck(request, "invitesubmit") || "invite".equals(subop)) {
					if (Common.empty(mtag.get("allowinvite"))) {
						return showMessage(request, response, "no_privilege");
					}
				} else {
					if (grade < 8) {
						return showMessage(request, response, "no_privilege");
					}
				}
				if (submitCheck(request, "basesubmit")) {
					Map<String, Object> setData = new HashMap<String, Object>();
					if (grade == 9) {
						Map<String, Object> field = profields.get(mtag.get("fieldid"));
						setData.put("joinperm", Common.empty(field.get("manualmember")) ? 0 : Common
								.intval(request.getParameter("joinperm")));
						setData.put("viewperm", Common.intval(request.getParameter("viewperm")));
						setData.put("threadperm", Common.intval(request.getParameter("threadperm")));
						setData.put("postperm", Common.intval(request.getParameter("postperm")));
						setData.put("closeapply", Common.intval(request.getParameter("closeapply")));
					}
					setData.put("pic", cpService.getPicUrlt(request.getParameter("pic"), 150));
					setData.put("announcement", Common.getStr(request.getParameter("announcement"), 5000,
							true, true, true, 1, 0, request, response));
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("tagid", tagId);
					dataBaseService.updateTable("mtag", setData, whereData);
					return showMessage(request, response, "do_success", "cp.jsp?ac=mtag&op=manage&tagid="
							+ tagId + "&subop=" + subop);
				} else if (submitCheck(request, "memberssubmit")) {
					int newGrade = Common.intval(request.getParameter("newGrade"));
					String[] ids = request.getParameterValues("ids");
					String result = mtag_manageMember(request, response, sGlobal, mtag, ids, newGrade);
					if (result != null) {
						return showMessage(request, response, result);
					}
					return showMessage(request, response, "do_success", "cp.jsp?ac=mtag&op=manage&tagid="
							+ tagId + "&subop=" + subop + "&grade=" + request.getParameter("grade"));
				} else if (submitCheck(request, "invitesubmit")) {
					String[] ids = request.getParameterValues("ids");
					if (ids != null) {
						List<String> haves = null;
						List<String> uids = dataBaseService.executeQuery("SELECT uid FROM "
								+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid=" + tagId
								+ " AND uid IN (" + Common.sImplode(ids) + ")", 1);
						if (uids.size() > 0) {
							haves = new ArrayList<String>();
							for (String uid : uids) {
								haves.add(uid);
							}
						}
						List<String> nones = new ArrayList<String>();
						for (String id : ids) {
							if (!Common.in_array(haves, id)) {
								nones.add(id);
							}
						}
						if (nones.size() > 0) {
							List<Map<String, Object>> friends = dataBaseService.executeQuery("SELECT * FROM "
									+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + supe_uid
									+ "' AND fuid IN (" + Common.sImplode(nones) + ") AND status='1'");
							if (friends.size() > 0) {
								List<Object> toUids = new ArrayList<Object>();
								List<String> inserts = new ArrayList<String>();
								for (Map<String, Object> friend : friends) {
									toUids.add(friend.get("fuid"));
									inserts.add("('" + friend.get("fuid") + "', " + tagId + ", " + supe_uid
											+ ", '" + supe_username + "', " + timestamp + ")");
								}
								if (toUids.size() > 0) {
									dataBaseService.executeUpdate("UPDATE "
											+ JavaCenterHome.getTableName("space")
											+ " SET mtaginvitenum=mtaginvitenum+1 WHERE uid IN ("
											+ Common.sImplode(toUids) + ")");
									dataBaseService.executeUpdate("REPLACE INTO "
											+ JavaCenterHome.getTableName("mtaginvite")
											+ " (uid,tagid,fromuid,fromusername,dateline) VALUES "
											+ Common.implode(inserts, ","));
								}
							}
						}
					}
					return showMessage(request, response, "do_success", "cp.jsp?ac=mtag&op=manage&tagid="
							+ tagId + "&subop=invite&page=" + request.getParameter("page") + "&group="
							+ request.getParameter("group") + "&start=" + request.getParameter("start"));
				} else if (submitCheck(request, "membersubmit")) {
					int newGrade = Common.intval(request.getParameter("newGrade"));
					String result = mtag_manageMember(request, response, sGlobal, mtag, request
							.getParameterValues("uid"), newGrade);
					if (result != null) {
						return showMessage(request, response, result);
					}
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
				if ("member".equals(subop)) {
					int uid = Common.intval(request.getParameter("uid"));
					List<String> grades = dataBaseService.executeQuery("SELECT grade FROM "
							+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='"
							+ request.getParameter("tagid") + "' AND uid='" + uid + "' LIMIT 1", 1);
					if (grades.size() > 0) {
						String gradeSelect = "grade" + grades.get(0);
						request.setAttribute(gradeSelect.replace("-", "_"), " selected");
					}
				} else if ("members".equals(subop)) {
					int perPage = 24;
					int start = Common.intval(request.getParameter("start"));
					int maxPage = (Integer) sConfig.get("maxpage");
					String result = Common.ckStart(start, perPage, maxPage);
					if (result != null) {
						return showMessage(request, response, result);
					}
					request.setAttribute("start", start);
					String key = Common.stripSearchKey(request.getParameter("key"));
					String whereSQL = Common.empty(key) ? "" : " AND username LIKE '%" + key + "%' ";
					int inputGrade = Common.intval(request.getParameter("grade"));
					List<Map<String, Object>> tagSpaces = dataBaseService
							.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("tagspace")
									+ " WHERE tagid=" + tagId + " AND grade=" + inputGrade + " " + whereSQL
									+ " LIMIT " + start + "," + perPage);
					int count = tagSpaces.size();
					if (count > 0) {
						for (Map<String, Object> tagSpace : tagSpaces) {
							Common.realname_set(sGlobal, sConfig, sNames, (Integer) tagSpace.get("uid"),
									(String) tagSpace.get("username"), "", 0);
						}
						Common.realname_get(sGlobal, sConfig, sNames, space);
						request.setAttribute("tagSpaces", tagSpaces);
					}
					request.setAttribute("multi", Common.smulti(sGlobal, start, perPage, count,
							"cp.jsp?ac=mtag&op=manage&tagid=" + tagId + "&subop=members&grade=" + inputGrade
									+ "&key=" + key, null));
					request.setAttribute("tagId", tagId);
					request.setAttribute("grade", inputGrade);
				} else if ("invite".equals(subop)) {
					int perPage = 10;
					int page = Common.intval(request.getParameter("page"));
					if (page < 1) {
						page = 1;
					}
					request.setAttribute("page", page);
					int start = (page - 1) * perPage;

					int maxPage = (Integer) sConfig.get("maxpage");
					String result = Common.ckStart(start, perPage, maxPage);
					if (result != null) {
						return showMessage(request, response, result);
					}
					String key = Common.stripSearchKey(request.getParameter("key"));
					String whereSQL = Common.empty(key) ? "" : " AND fusername LIKE '%" + key + "%'";

					String group = request.getParameter("group");
					int gid = group == null ? -1 : Common.intval(group);
					if (gid >= 0) {
						whereSQL += " AND gid='" + gid + "'";
					}
					request.setAttribute("gid", gid);
					int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + supe_uid
							+ "' AND status='1' " + whereSQL);
					if (count > 0) {
						List<Map<String, Object>> friends = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + supe_uid
								+ "' AND status='1' " + whereSQL + " ORDER BY num DESC, dateline DESC LIMIT "
								+ start + "," + perPage);
						if (friends.size() > 0) {
							List<Integer> fuids = new ArrayList<Integer>();
							for (Map<String, Object> friend : friends) {
								int fuid = (Integer) friend.get("fuid");
								Common.realname_set(sGlobal, sConfig, sNames, fuid, (String) friend
										.get("fusername"), "", 0);
								fuids.add(fuid);
							}
							Common.realname_get(sGlobal, sConfig, sNames, space);
							request.setAttribute("friends", friends);

							Map<Integer, Integer> joins = new HashMap<Integer, Integer>();
							String uids = Common.sImplode(fuids);
							List<Map<String, Object>> tagSpaces = dataBaseService
									.executeQuery("SELECT uid FROM "
											+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='"
											+ tagId + "' AND uid IN (" + uids + ")");
							for (Map<String, Object> tagSpace : tagSpaces) {
								int uid = (Integer) tagSpace.get("uid");
								joins.put(uid, uid);
							}
							List<Map<String, Object>> mtagInvites = dataBaseService
									.executeQuery("SELECT uid FROM "
											+ JavaCenterHome.getTableName("mtaginvite") + " WHERE tagid='"
											+ tagId + "' AND uid IN (" + uids + ")");
							for (Map<String, Object> mtagInvite : mtagInvites) {
								int uid = (Integer) mtagInvite.get("uid");
								joins.put(uid, uid);
							}
							request.setAttribute("joins", joins);
						}
						String multi = Common.multi(request, count, perPage, page, maxPage,
								"cp.jsp?ac=mtag&op=manage&tagid=" + tagId + "&subop=invite&group=" + group
										+ "&key=" + key, null, null);
						request.setAttribute("multi", multi);
					}
					request.setAttribute("groups", Common.getFriendGroup(request));
				} else {
					Map<String, Object> field = profields.get(mtag.get("fieldid"));
					request.setAttribute("field", field);
					mtag.put("announcement", BBCode.html2bbcode((String) mtag.get("announcement")));
					request.setAttribute("joinPerm_" + mtag.get("joinperm"), " selected");
					request.setAttribute("viewPerm_" + mtag.get("viewperm"), " selected");
					request.setAttribute("threadPerm_" + mtag.get("threadperm"), " selected");
					request.setAttribute("postPerm_" + mtag.get("postperm"), " selected");
					request.setAttribute("closeApply_" + mtag.get("closeapply"), " checked");
				}
				request.setAttribute("active_" + subop, " class=\"active\"");
				request.setAttribute("mtag", mtag);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
		} else if ("join".equals(op)) {
			int tagId = Common.intval(request.getParameter("tagid"));
			try {
				if (submitCheck(request, "joinsubmit")) {
					Object result = mtag_join(request, profields, "tagid", String.valueOf(tagId), 0);
					if (Common.empty(result)) {
						return showMessage(request, response, "mtag_join_error");
					} else {
						if (result instanceof MessageVO) {
							return showMessage(request, response, (MessageVO) result);
						}
						Map<String, Object> mtag = (Map<String, Object>) result;
						return showMessage(request, response, "join_success", "space.jsp?uid=" + supe_uid
								+ "&do=mtag&tagid=" + mtag.get("tagid"), 0);
					}
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("tagId", tagId);
		} else if ("out".equals(op)) {
			int tagId = Common.intval(request.getParameter("tagid"));
			try {
				if (submitCheck(request, "outsubmit")) {
					if (tagId > 0) {
						Map<String, Object> mtag = Common.getMtag(request, response, supe_uid, tagId);
						if (!Common.empty(mtag)) {
							if (((Integer) mtag.get("joinperm") > 0 || (Integer) mtag.get("viewperm") > 0)
									&& (Integer) mtag.get("grade") == 9) {
								int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
										+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='" + tagId
										+ "' AND grade='9'");
								if (count < 2) {
									return showMessage(request, response, "failure_to_withdraw_from_group");
								}
							}
							if ((Integer) mtag.get("grade") != -9) {
								mtag_out(tagId, supe_uid);
							}
						}
					}
					return showMessage(request, response, "do_success", "space.jsp?do=mtag");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("tagId", tagId);
		} else if ("mtaginvite".equals(op)) {
			List<Map<String, Object>> invites = dataBaseService.executeQuery("SELECT mtag.*, i.* FROM "
					+ JavaCenterHome.getTableName("mtaginvite") + " i LEFT JOIN "
					+ JavaCenterHome.getTableName("mtag") + " mtag ON mtag.tagid=i.tagid WHERE i.uid='"
					+ supe_uid + "' ORDER BY i.dateline DESC");
			int count = invites.size();
			if (count > 0) {
				for (Map<String, Object> invite : invites) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) invite.get("fromuid"),
							(String) invite.get("fromusername"), "", 0);
					invite.put("title", profields.get(invite.get("fieldid")).get("title"));
					if (Common.empty(invite.get("pic"))) {
						invite.put("pic", "image/nologo.jpg");
					}
					invite.put("dateline", Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) invite
							.get("dateline"), true));
				}
				request.setAttribute("invites", invites);
				Common.realname_get(sGlobal, sConfig, sNames, space);
			}
			if (count != (Integer) space.get("mtaginvitenum")) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET mtaginvitenum=" + count + " WHERE uid='" + space.get("uid") + "'");
			}
		} else if ("inviteconfirm".equals(op)) {
			int tagId = Common.intval(request.getParameter("tagid"));
			if (tagId > 0 && !Common.empty(request.getParameter("r"))) {
				int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid=" + tagId + " AND uid="
						+ supe_uid);
				if (count == 0) {
					List<Map<String, Object>> invites = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("mtaginvite") + " WHERE tagid=" + tagId
							+ " AND uid=" + supe_uid);
					if (invites.size() > 0) {
						Map<String, Object> invite = invites.get(0);
						Map<String, Object> mtag = null;
						try {
							mtag = Common.getMtag(request, response, supe_uid, tagId);
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
						int fieldId = (Integer) mtag.get("fieldid");
						Map<String, Object> field = (Map<String, Object>) mtag.get("field");
						int maxInputNum = 0;
						String formType = (String) field.get("formtype");
						if ("text".equals(formType) || "multi".equals(formType)) {
							maxInputNum = (Integer) field.get("inputnum");
						} else if ("select".equals(formType)) {
							maxInputNum = 1;
						}
						if (maxInputNum > 0) {
							int myInputNum = dataBaseService.findRows("SELECT COUNT(*) FROM "
									+ JavaCenterHome.getTableName("tagspace") + " ts, "
									+ JavaCenterHome.getTableName("mtag")
									+ " mtag WHERE ts.tagid=mtag.tagid AND ts.uid=" + supe_uid
									+ " AND mtag.fieldid=" + fieldId);
							if (myInputNum >= maxInputNum) {
								return showMessage(request, response, "mtag_join_field_error", null, 1,
										new String[] {(String) field.get("title"),
												String.valueOf(maxInputNum)});
							}
						}
						Map<String, Object> insertData = new HashMap<String, Object>();
						insertData.put("tagid", tagId);
						insertData.put("uid", supe_uid);
						insertData.put("username", supe_username);
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag")
								+ " SET membernum=membernum+1 WHERE tagid=" + tagId);
						dataBaseService.insertTable("tagspace", insertData, false, true);
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) invite.get("fromuid"),
								(String) invite.get("fromusername"), "", 0);
						Common.realname_get(sGlobal, sConfig, sNames, space);

						if (Common.ckPrivacy(sGlobal, sConfig, space, "mtag", 1)) {
							Map<String, String> title_data = new HashMap<String, String>();
							title_data.put("mtag", "<a href=\"space.jsp?do=mtag&tagid=" + tagId + "\">"
									+ mtag.get("tagname") + "</a>");
							title_data.put("field", "<a href=\"space.jsp?do=mtag&id=" + mtag.get("fieldid")
									+ "\">" + mtag.get("title") + "</a>");
							title_data.put("fromusername", "<a href=\"space.jsp?uid=" + invite.get("fromuid")
									+ "\">" + sNames.get(invite.get("fromuid")) + "</a>");
							cpService.addFeed(sGlobal, "mtag", Common.getMessage(request,
									"cp_feed_mtag_join_invite"), title_data, "", null, "", null, null, "", 0,
									0, 0, "", false);
						}
						dataBaseService.executeUpdate("DELETE FROM "
								+ JavaCenterHome.getTableName("mtaginvite") + " WHERE tagid=" + tagId
								+ " AND uid=" + supe_uid);
						int mtagInviteNum = (Integer) space.get("mtaginvitenum");
						if (mtagInviteNum > 0) {
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
									+ " SET mtaginvitenum=" + (mtagInviteNum - 1) + " WHERE uid='"
									+ space.get("uid") + "'");
						}
						return showMessage(request, response, "invite_mtag_ok", null, 1, new String[] {String
								.valueOf(tagId)});
					}
				}
			}
			if (tagId > 0) {
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mtaginvite")
						+ " WHERE tagid=" + tagId + " AND uid=" + supe_uid);
				int mtagInviteNum = (Integer) space.get("mtaginvitenum");
				if (mtagInviteNum > 0) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET mtaginvitenum=" + (mtagInviteNum - 1) + " WHERE uid='" + space.get("uid")
							+ "'");
				}
				return showMessage(request, response, "invite_mtag_cancel");
			} else if (tagId == 0) {
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mtaginvite")
						+ " WHERE uid=" + supe_uid);
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET mtaginvitenum=0 WHERE uid='" + space.get("uid") + "'");
				return showMessage(request, response, "do_success", "cp.jsp?ac=mtag&op=mtaginvite", 0);
			}
			return showMessage(request, response, "invite_mtag_cancel", "cp.jsp?ac=mtag&op=mtaginvite", 0);
		} else if ("apply".equals(op)) {
			int tagId = Common.intval(request.getParameter("tagid"));
			try {
				if (tagId > 0 && submitCheck(request, "pmsubmit")) {
					String message = request.getParameter("message");
					if (Common.empty(message)) {
						return showMessage(request, response, "fill_out_the_grounds_for_the_application");
					}
					Map<String, Object> mtag = Common.getMtag(request, response, supe_uid, tagId);
					String mtagUrl = "cp.jsp?ac=mtag&tagid=" + tagId + "&op=manage&subop=members&key="
							+ supe_username;
					message = Common.getStr(message, 0, true, true, true, 0, 0, request, response);
					message = Common.addSlashes(Common.stripSlashes(Common.getMessage(request,
							"cp_apply_mtag_manager", new String[] {mtagUrl, (String) mtag.get("tagname"),
									message})));
					List<String> uids = dataBaseService.executeQuery("SELECT uid FROM "
							+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid=" + tagId
							+ " AND grade > 8 LIMIT 0 , 5", 1);

					if (uids.isEmpty()) {
						List<String> gids = dataBaseService.executeQuery("SELECT gid FROM "
								+ JavaCenterHome.getTableName("usergroup") + " WHERE managemtag='1'", 1);
						if (gids.size() > 0) {
							uids = dataBaseService.executeQuery("SELECT uid FROM "
									+ JavaCenterHome.getTableName("space") + " WHERE groupid IN ("
									+ Common.sImplode(gids) + ") LIMIT 0 , 5", 1);
						}
					}
					if (!uids.isEmpty()) {
						List<String> notes = new ArrayList<String>();
						for (String uid : uids) {
							notes.add("(" + uid + ", 'mtag', 1, " + supe_uid + ", '" + supe_username + "', '"
									+ message + "', " + timestamp + ")");
						}
						dataBaseService.executeUpdate("INSERT INTO "
								+ JavaCenterHome.getTableName("notification")
								+ " (uid, type, new, authorid, author, note, dateline) values "
								+ Common.implode(notes, ","));
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET notenum=notenum+1 WHERE uid IN (" + Common.sImplode(uids) + ")");
					}
					return showMessage(request, response, "do_success");
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("tagId", tagId);
		} else {
			if (!Common.checkPerm(request, response, "allowmtag")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				}
				return showMessage(request, response, "no_privilege");
			}
			cpService.checkRealName(request, "thread");
			cpService.checkVideoPhoto(request, response, "thread");
			cpService.checkNewUser(request, response);
			try {
				if (submitCheck(request, "textsubmit")) {
					String tagName = Common.getStr(request.getParameter("tagname"), 40, true, true, true, 0,
							0, request, response);
					int fieldId = Common.intval(request.getParameter("fieldid"));
					Map<String, Object> profield = profields.get(fieldId);
					if (Common.empty(profield) || !"text".equals(profield.get("formtype"))) {
						return showMessage(request, response, "mtag_fieldid_does_not_exist");
					}
					if (Common.strlen(tagName) < 2) {
						return showMessage(request, response, "mtag_tagname_error");
					}

					if (Common.empty(request.getParameter("joinmode"))) {
						String newTagName = Common.stripSlashes(tagName);
						List<Map<String, Object>> mtags = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("mtag") + " WHERE tagname='" + tagName
								+ "' AND fieldid='" + fieldId + "'");
						if (mtags.size() == 0) {
							String key = Common.stripSearchKey(tagName);
							List<Map<String, Object>> likemtags = dataBaseService
									.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("mtag")
											+ " WHERE tagname LIKE '%" + key
											+ "%' ORDER BY membernum DESC LIMIT 0,20");
							request.setAttribute("likemtags", likemtags);
						} else {
							Map<String, Object> findmtag = mtags.get(0);
							if (Common.empty(findmtag.get("pid"))) {
								findmtag.put("pic", "image/nologo.jpg");
							}
							request.setAttribute("findmtag", findmtag);
						}
						request.setAttribute("fieldId", fieldId);
						request.setAttribute("newTagName", newTagName);
						request.setAttribute("profield", profield);
						request.setAttribute("op", "confirm");
						request.setAttribute("subop", subop);
						return include(request, response, sConfig, sGlobal, "cp_mtag.jsp");
					} else {
						Object result = mtag_join(request, profields, "tagname",
								Common.stripSlashes(tagName), fieldId);
						if (Common.empty(result)) {
							return showMessage(request, response, "mtag_join_error");
						} else {
							if (result instanceof MessageVO) {
								return showMessage(request, response, (MessageVO) result);
							}
							Map<String, Object> mtag = (Map<String, Object>) result;
							return showMessage(request, response, "join_success", "space.jsp?uid=" + supe_uid
									+ "&do=mtag&tagid=" + mtag.get("tagid"), 0);
						}
					}
				} else if (submitCheck(request, "choicesubmit")) {
					List<Map<String, Object>> mtags = new ArrayList<Map<String, Object>>();
					Map<String, String[]> params = request.getParameterMap();
					Set<String> keys = params.keySet();
					for (String key : keys) {
						if (key.startsWith("tagname_")) {
							int fieldId = Common.intval(key.substring(8));
							Map<String, Object> profield = profields.get(fieldId);
							String formType = (String) profield.get("formtype");
							if ("multi".equals(formType) || "select".equals(formType)) {
								String[] values = params.get(key);
								if (values != null && values.length > 0) {
									for (String value : values) {
										value = Common.stripSlashes(value);
										if (Common.in_array(profield.get("choice"), value)) {
											Object result = mtag_join(request, profields, "tagname", value,
													fieldId);
											if (!Common.empty(result)) {
												if (result instanceof MessageVO) {
													return showMessage(request, response, (MessageVO) result);
												}
												mtags.add((Map<String, Object>) result);
											}
										}
									}
								}
							} else {
								continue;
							}
						}
					}
					if (mtags.isEmpty()) {
						return showMessage(request, response, "do_success", "cp.jsp?ac=mtag");
					} else {
						request.setAttribute("op", "multiresult");
						request.setAttribute("subop", subop);
						request.setAttribute("mtags", mtags);
						return include(request, response, sConfig, sGlobal, "cp_mtag.jsp");
					}
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			List<Map<String, Object>> mtags = dataBaseService
					.executeQuery("SELECT mtag.tagname, mtag.fieldid FROM "
							+ JavaCenterHome.getTableName("tagspace") + " main LEFT JOIN "
							+ JavaCenterHome.getTableName("mtag")
							+ " mtag ON mtag.tagid=main.tagid WHERE main.uid=" + supe_uid);
			if (mtags.size() > 0) {
				Map<Object, List<Object>> exist_mtags = new HashMap<Object, List<Object>>();
				for (Map<String, Object> mtag : mtags) {
					Object fieldId = mtag.get("fieldid");
					List<Object> tagNames = exist_mtags.get(fieldId);
					if (tagNames == null) {
						tagNames = new ArrayList<Object>();
					}
					tagNames.add(mtag.get("tagname"));
					exist_mtags.put(fieldId, tagNames);
				}
				request.setAttribute("exist_mtags", exist_mtags);
			}
		}
		request.setAttribute("op", op);
		request.setAttribute("subop", subop);
		return include(request, response, sConfig, sGlobal, "cp_mtag.jsp");
	}

	
	private Object mtag_join(HttpServletRequest request, Map<Object, Map<String, Object>> profields,
			String type, String key, int fieldId) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<String, Object> mtag = new HashMap<String, Object>();
		key = Common.addSlashes(key);
		int haveJoin = 0;
		String whereSQL = null;
		if ("tagid".equals(type)) {
			whereSQL = "main.tagid='" + key + "'";
		} else {
			if (Common.strlen(key) < 2) {
				return new MessageVO("mtag_tagname_error");
			}
			whereSQL = "main.tagname='" + key + "' AND main.fieldid='" + fieldId + "'";
		}
		int tagId = 0;
		List<Map<String, Object>> mtags = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("mtag") + " main WHERE " + whereSQL + " LIMIT 1");
		if (mtags.size() > 0) {
			mtag = mtags.get(0);
			tagId = (Integer) mtag.get("tagid");
			fieldId = (Integer) mtag.get("fieldid");
			haveJoin = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid = " + mtag.get("tagid")
					+ " AND uid = " + sGlobal.get("supe_uid"));
		} else if ("tagid".equals(type)) {
			return mtag;
		} else {
			mtag.put("tagname", key);
			mtag.put("fieldid", fieldId);
			mtag.put("membernum", 0);
			mtag.put("threadnum", 0);
			mtag.put("postnum", 0);
			mtag.put("close", 0);
			mtag.put("announcement", "");
			mtag.put("pic", "");
			mtag.put("closeapply", 0);
			mtag.put("joinperm", 0);
			mtag.put("viewperm", 0);
			mtag.put("threadperm", 0);
			mtag.put("postperm", 0);
			mtag.put("recommend", 0);
			mtag.put("moderator", "");
			tagId = dataBaseService.insertTable("mtag", mtag, true, false);
			mtag.put("tagid", tagId);
		}
		Map<String, Object> field = profields.get(fieldId);
		mtag.put("title", field.get("title"));
		if (haveJoin > 0) {
			return mtag;
		}
		int maxInputNum = 0;
		String formType = (String) field.get("formtype");
		if ("text".equals(formType) || "multi".equals(formType)) {
			maxInputNum = (Integer) field.get("inputnum");
		} else if ("select".equals(formType)) {
			maxInputNum = 1;
		}
		if (maxInputNum > 0) {
			int myInputNum = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("tagspace") + " ts, " + JavaCenterHome.getTableName("mtag")
					+ " mtag WHERE ts.tagid=mtag.tagid AND ts.uid='" + sGlobal.get("supe_uid")
					+ "' AND mtag.fieldid='" + fieldId + "'");
			if (myInputNum >= maxInputNum) {
				MessageVO messageVO = new MessageVO("mtag_join_field_error");
				messageVO.setArgs(field.get("title"), maxInputNum);
				return messageVO;
			}
		}
		Map<String, Object> insertData = new HashMap<String, Object>();
		insertData.put("tagid", tagId);
		insertData.put("uid", sGlobal.get("supe_uid"));
		insertData.put("username", sGlobal.get("supe_username"));
		int joinPerm = (Integer) mtag.get("joinperm");
		int grade = 0;
		if (joinPerm == 2) {
			return null;
		} else if (joinPerm == 1) {
			grade = -2;
		} else {
			int modCount = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid='" + tagId + "' AND grade>=8");
			if (modCount > 0) {
				grade = 0;
			} else if ((Integer) field.get("manualmoderator") == 0) {
				grade = 9;
			}
			if (Common.ckPrivacy(sGlobal, sConfig, space, "mtag", 1)) {
				Map<String, String> title_data = new HashMap<String, String>();
				title_data.put("mtag", "<a href=\"space.jsp?do=mtag&tagid=" + tagId + "\">"
						+ mtag.get("tagname") + "</a>");
				title_data.put("field", "<a href=\"space.jsp?do=mtag&id=" + mtag.get("fieldid") + "\">"
						+ mtag.get("title") + "</a>");
				cpService.addFeed(sGlobal, "mtag", Common.getMessage(request, "cp_feed_mtag_join"),
						title_data, "", null, "", null, null, "", 0, 0, 0, "", false);
			}
		}
		insertData.put("grade", grade);
		mtag.put("grade", grade);
		dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag")
				+ " SET membernum=membernum+1 WHERE tagid='" + tagId + "'");
		dataBaseService.insertTable("tagspace", insertData, false, true);
		mtag.put("membernum", (Integer) mtag.get("membernum") + 1);
		return mtag;
	}
	private void mtag_out(int tagId, Object uids) {
		dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagspace")
				+ " WHERE tagid=" + tagId + " AND uid IN (" + Common.sImplode(uids) + ")");
		int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
				+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid=" + tagId);
		if (count > 0) {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag") + " SET membernum="
					+ count + " WHERE tagid=" + tagId);
		} else {
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("tagspace")
					+ " WHERE tagid=" + tagId);
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mtag")
					+ " WHERE tagid=" + tagId);
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("thread")
					+ " WHERE tagid=" + tagId);
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("post")
					+ " WHERE tagid=" + tagId);
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("mtaginvite")
					+ " WHERE tagid=" + tagId);
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
					+ " WHERE id=" + tagId + " AND idtype='tagid'");
		}
	}
	private String mtag_manageMember(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> mtag, String[] uids, int newGrade) {
		if (Common.empty(uids)) {
			return null;
		}
		boolean managemtag = Common.checkPerm(request, response, "managemtag");
		int grade = (Integer) mtag.get("grade");
		int tagId = (Integer) mtag.get("tagid");
		if (grade < 9 && newGrade >= 8 && !managemtag) {
			return "no_privilege";
		}
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		List<Integer> newUids = new ArrayList<Integer>();
		List<Map<String, Object>> tagSpaces = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("tagspace") + " WHERE tagid=" + tagId + " AND uid IN ("
				+ Common.sImplode(uids) + ")");
		for (Map<String, Object> tagSpace : tagSpaces) {
			int uid = (Integer) tagSpace.get("uid");
			if ((Integer) tagSpace.get("grade") < 8 || (grade == 9 && uid != supe_uid) || managemtag) {
				newUids.add(uid);
			}
		}
		if (Common.empty(newUids)) {
			return "mtag_managemember_no_privilege";
		}
		String note_msg = Common.addSlashes(Common.getMessage(request, "cp_note_members_grade_" + newGrade,
				mtag.get("tagid").toString(), (String) mtag.get("tagname")));
		List<Integer> n_uids = new ArrayList<Integer>();
		List<String> notes = new ArrayList<String>();
		int timestamp = (Integer) sGlobal.get("timestamp");
		for (int uid : newUids) {
			if (uid != supe_uid) {
				n_uids.add(uid);
				notes.add("(" + uid + ", 'mtag', 1, " + supe_uid + ", '" + sGlobal.get("supe_username")
						+ "', '" + note_msg + "', " + timestamp + ")");
			}
		}
		if (n_uids.size() > 0) {
			dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("notification")
					+ " (uid, type, new, authorid, author, note, dateline) VALUES "
					+ Common.implode(notes, ","));
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET notenum=notenum+1 WHERE uid IN (" + Common.sImplode(n_uids) + ")");
		}

		if (newGrade == -9) {
			mtag_out(tagId, newUids);
		} else {
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("tagspace")
					+ " SET grade='" + newGrade + "' WHERE tagid=" + tagId + " AND uid IN ("
					+ Common.sImplode(newUids) + ")");
		}
		return null;
	}

	
	public ActionForward cp_password(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		try {
			if (submitCheck(request, "pwdsubmit")) {
				String oldPassword = request.getParameter("password");
				String newPassword1 = request.getParameter("newpasswd1");
				String newPassword2 = request.getParameter("newpasswd2");
				if (newPassword1.equals(newPassword2) == false) {
					return showMessage(request, response, "password_inconsistency");
				}
				if (newPassword1.equals(Common.addSlashes(newPassword1)) == false
						|| newPassword1.trim().equals("")) {
					return showMessage(request, response, "profile_passwd_illegal");
				}
				String userName = (String) sGlobal.get("supe_username");
				List<Map<String, Object>> members = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("member") + " WHERE username = '" + userName + "'");
				if (members.isEmpty()) {
					return showMessage(request, response, "to_login", "do.jsp?ac="
							+ sConfig.get("login_action"));
				}
				Map<String, Object> member = members.get(0);
				oldPassword = Common.md5(Common.md5(oldPassword) + member.get("salt"));
				if (oldPassword.equals(member.get("password")) == false) {
					return showMessage(request, response, "old_password_invalid");
				}
				newPassword1 = Common.md5(Common.md5(newPassword1) + member.get("salt"));
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("member")
						+ " SET password='" + newPassword1 + "' WHERE username='" + userName + "'");
				CookieHelper.clearCookie(request, response);
				return showMessage(request, response, "getpasswd_succeed", "do.jsp?ac="
						+ sConfig.get("login_action"));
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		return include(request, response, sConfig, sGlobal, "cp_password.jsp");
	}
	
	public ActionForward cp_pm(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int pmid = Common.intval(request.getParameter("pmid"));
		int uid = Common.intval(request.getParameter("uid"));
		int toUid = 0;
		if (uid > 0) {
			if (uid == (Integer) sGlobal.get("supe_uid")) {
				return showMessage(request, response, "not_to_their_own_greeted_send");
			}
			toUid = uid;
		} else {
			toUid = Common.intval(request.getParameter("touid"));
		}
		String op = request.getParameter("op");
		if ("checknewpm".equals(op)) {
			if (!Common.empty(sGlobal.get("supe_uid"))) {
				int newpm = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("newpm") + " WHERE uid='" + sGlobal.get("supe_uid")
						+ "'");
				if (newpm > 0) {
					newpm = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("pms")
							+ " WHERE (related='0' AND msgfromid>'0' OR msgfromid='0') AND msgtoid='"
							+ sGlobal.get("supe_uid") + "' AND folder='inbox' AND new='1'");
				}
				Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
				if (member != null) {
					if ((Integer) member.get("newpm") != newpm) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET newpm='" + newpm + "' AND uid='" + sGlobal.get("supe_uid") + "'");
					}
				}
			}
			CookieHelper.setCookie(request, response, "checkpm", "1", 30);
			return null;
		} else if ("delete".equals(op)) {
			String folder = "inbox".equals(request.getParameter("folder")) ? "inbox" : "outbox";
			try {
				if (submitCheck(request, "deletesubmit")) {
					int affectedRows = dataBaseService.executeUpdate("DELETE FROM "
							+ JavaCenterHome.getTableName("pms") + " WHERE msgtoid='"
							+ sGlobal.get("supe_uid") + "' AND pmid='" + pmid + "'");
					if (affectedRows > 0) {
						return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
					} else {
						return showMessage(request, response, "this_message_could_not_be_deleted");
					}
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("folder", folder);
		} else if ("send".equals(op)) {
			int waitTime = Common.checkInterval(request, response, "post");
			if (waitTime > 0) {
				return showMessage(request, response, "operating_too_fast", null, 1, new String[] {waitTime
						+ ""});
			}
			int result = cpService.checkNewUser(request, response);
			switch (result) {
				case 1:
					break;
				case 2:
					return showMessage(request, response, "no_privilege_newusertime", "", 1, String
							.valueOf(sConfig.get("newusertime")));
				case 3:
					return showMessage(request, response, "no_privilege_avatar");
				case 4:
					return showMessage(request, response, "no_privilege_friendnum", "", 1, String
							.valueOf(sConfig.get("need_friendnum")));
				case 5:
					return showMessage(request, response, "no_privilege_email");
			}
			if (toUid > 0) {
				if (cpService.isBlackList(toUid, (Integer) sGlobal.get("supe_uid")) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
			}
			Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
			try {
				if (submitCheck(request, "pmsubmit")) {
					String userName = request.getParameter("username");
					String message = Common.trim(request.getParameter("message"));
					if (Common.empty(message)) {
						return showMessage(request, response, "unable_to_send_air_news");
					}
					String subject = "";
					int returnPmId = 0;
					if (toUid > 0) {
						returnPmId = pmService.jcSendPm(request, response, (Integer) sGlobal.get("supe_uid"),
								toUid + "", subject, message, pmid, false, false);
						if (returnPmId > 0) {
							cpService.sendMail(request, response, toUid, "", Common.getMessage(request, "cp_friend_pm", new String[] {sNames.get(space.get("uid")),
									Common.getSiteUrl(request) + "space.jsp?do=pm"}), "", "friend_pm");
							PostHandler.getInstance().send(toUid, "new");
						}
					} else if (!Common.empty(userName)) {
						List<String> newUsers = new ArrayList<String>();
						String[] users = userName.split(",");
						for (String value : users) {
							value = value.trim();
							if (!Common.empty(value)) {
								newUsers.add(value);
							}
						}
						if (newUsers.size() > 0) {
							returnPmId = pmService.jcSendPm(request, response, (Integer) sGlobal
									.get("supe_uid"), Common.implode(newUsers, ","), subject, message, pmid,
									true, false);
						}
						toUid = 0;
						if (returnPmId > 0) {
							List<Map<String, Object>> spaceList = dataBaseService
									.executeQuery("SELECT uid FROM " + JavaCenterHome.getTableName("space")
											+ " WHERE username IN (" + Common.sImplode(users) + ')');
							for (Map<String, Object> value : spaceList) {
								if (toUid == 0) {
									toUid = (Integer) value.get("uid");
								}
								cpService.sendMail(request, response, (Integer) value.get("uid"), "", Common
										.getMessage(request, "cp_friend_pm", new String[] {
												sNames.get(space.get("uid")),
												Common.getSiteUrl(request) + "space.jsp?do=pm"}), "",
										"friend_pm");
							}
							PostHandler.getInstance().send(toUid, "new");
						}
					}
					if (returnPmId > 0) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET lastpost='" + sGlobal.get("timestamp") + "' WHERE uid='"
								+ sGlobal.get("supe_uid") + "'");
						return showMessage(request, response, "do_success",
								"space.jsp?do=pm&filter=privatepm");
					} else {
						if (Common.in_array(new Integer[] {-1, -2, -3, -4}, returnPmId)) {
							return showMessage(request, response, "message_can_not_send"
									+ Math.abs(returnPmId));
						} else {
							return showMessage(request, response, "message_can_not_send");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
		} else if ("ignore".equals(op)) {
			try {
				if (submitCheck(request, "ignoresubmit")) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("member")
							+ " SET blacklist='" + request.getParameter("ignorelist") + "' WHERE uid='"
							+ sGlobal.get("supe_uid") + "'");
					return showMessage(request, response, "do_success", "space.jsp?do=pm&view=ignore");
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
		} else {
			int result = cpService.checkNewUser(request, response);
			switch (result) {
				case 1:
					break;
				case 2:
					return showMessage(request, response, "no_privilege_newusertime", "", 1, String
							.valueOf(sConfig.get("newusertime")));
				case 3:
					return showMessage(request, response, "no_privilege_avatar");
				case 4:
					return showMessage(request, response, "no_privilege_friendnum", "", 1, String
							.valueOf(sConfig.get("need_friendnum")));
				case 5:
					return showMessage(request, response, "no_privilege_email");
			}
			if (!Common.checkPerm(request, response, "allowpm")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				}
				return showMessage(request, response, "no_privilege");
			}
			if (!Common.empty(space.get("friendnum"))) {
				List<Map<String, Object>> friends = dataBaseService
						.executeQuery("SELECT fuid AS uid, fusername AS username FROM "
								+ JavaCenterHome.getTableName("friend") + " WHERE uid="
								+ sGlobal.get("supe_uid")
								+ " AND status='1' ORDER BY num DESC, dateline DESC LIMIT 0,100");
				List fNamee = new ArrayList(friends.size());
				for (Map<String, Object> value : friends) {
					value.put("username", Common.sAddSlashes(value.get("username")));
					fNamee.add(value.get("username"));
				}
				request.setAttribute("friendstr", Common.implode(fNamee, ","));
				request.setAttribute("friends", friends);
			}
		}
		request.setAttribute("touid", toUid);
		request.setAttribute("pmid", pmid);
		return include(request, response, sConfig, sGlobal, "cp_pm.jsp");
	}

	public ActionForward cp_poke(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		int uid = Common.intval(request.getParameter("uid"));
		if (uid == (Integer) sGlobal.get("supe_uid")) {
			return showMessage(request, response, "not_to_their_own_greeted");
		}
		String op = request.getParameter("op");
		if ("send".equals(op) || "reply".equals(op)) {
			if (!Common.checkPerm(request, response, "allowpoke")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				}
				return showMessage(request, response, "no_privilege");
			}
			if (!cpService.checkRealName(request, "poke")) {
				return showMessage(request, response, "no_privilege_realname");
			}
			int result = cpService.checkNewUser(request, response);
			switch (result) {
				case 1:
					break;
				case 2:
					return showMessage(request, response, "no_privilege_newusertime", "", 1, String
							.valueOf(sConfig.get("newusertime")));
				case 3:
					return showMessage(request, response, "no_privilege_avatar");
				case 4:
					return showMessage(request, response, "no_privilege_friendnum", "", 1, String
							.valueOf(sConfig.get("need_friendnum")));
				case 5:
					return showMessage(request, response, "no_privilege_email");
			}
			Map<String, Object> toSpace = null;
			String userName = request.getParameter("username");
			if (uid > 0) {
				toSpace = Common.getSpace(request, sGlobal, sConfig, uid);
			} else if (!Common.empty(userName)) {
				toSpace = Common.getSpace(request, sGlobal, sConfig, userName, "username", false);
			}
			if (toSpace != null && !Common.empty(toSpace.get("videostatus"))) {
				if (!cpService.checkVideoPhoto(request, response, "poke", toSpace)) {
					return showMessage(request, response, "no_privilege_videophoto");
				}
			}
			if (toSpace != null
					&& cpService.isBlackList((Integer) toSpace.get("uid"), (Integer) sGlobal.get("supe_uid")) != 0) {
				return showMessage(request, response, "is_blacklist");
			}
			try {
				if (submitCheck(request, "pokesubmit")) {
					if (toSpace == null) {
						return showMessage(request, response, "space_does_not_exist");
					}
					uid = (Integer) toSpace.get("uid");
					if (uid == (Integer) sGlobal.get("supe_uid")) {
						return showMessage(request, response, "not_to_their_own_greeted");
					}
					int oldPoke = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("poke") + " WHERE uid='" + uid + "' AND fromuid='"
							+ sGlobal.get("supe_uid") + "' LIMIT 1");
					Map<String, Object> setArr = new HashMap<String, Object>();
					setArr.put("uid", uid);
					setArr.put("fromuid", sGlobal.get("supe_uid"));
					setArr.put("fromusername", sGlobal.get("supe_username"));
					setArr.put("note", Common.getStr(request.getParameter("note"), 50, true, true, false, 0,
							0, request, response));
					setArr.put("dateline", sGlobal.get("timestamp"));
					setArr.put("iconid", Common.intval(request.getParameter("iconid")));
					dataBaseService.insertTable("poke", setArr, false, true);
					if (oldPoke == 0) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET pokenum=pokenum+1 WHERE uid='" + uid + "'");
					}
					cpService.addFriendNum(sGlobal, (Integer) toSpace.get("uid"), (String) toSpace
							.get("username"));
					cpService.sendMail(request, response, uid, "", Common.getMessage(request,
							"cp_poke_subject", new String[] {sNames.get(space.get("uid")),
									Common.getSiteUrl(request) + "cp.jsp?ac=poke"}), "", "poke");
					if ("reply".equals(op)) {
						dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("poke")
								+ " WHERE uid='" + sGlobal.get("supe_uid") + "' AND fromuid='" + uid + "'");
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET pokenum=pokenum-1 WHERE uid='" + sGlobal.get("supe_uid")
								+ "' AND pokenum>0");
					}
					Common.getReward("poke", true, 0, uid + "", true, request, response);
					cpService.updateStat(sGlobal, sConfig, "poke", false);
					return showMessage(request, response, "poke_success", request.getParameter("refer"), 1,
							sNames.get(toSpace.get("uid")));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("tospace", toSpace);
		} else if ("ignore".equals(op)) {
			String where = uid == 0 ? "" : "AND fromuid='" + uid + "'";
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("poke")
					+ " WHERE uid='" + sGlobal.get("supe_uid") + "' " + where);
			int pokeNum = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("poke") + " WHERE uid='" + space.get("uid") + "' LIMIT 1");
			if (pokeNum != (Integer) space.get("pokenum")) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET pokenum='" + pokeNum + "' AND uid='" + space.get("uid") + "'");
			}
			return showMessage(request, response, "has_been_hailed_overlooked");
		} else {
			int perPage = 20;
			int page = Common.intval(request.getParameter("page"));
			if (page < 1)
				page = 1;
			int start = (page - 1) * perPage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String tempS = Common.ckStart(start, perPage, maxPage);
			if (tempS != null) {
				return showMessage(request, response, tempS);
			}
			int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
					+ JavaCenterHome.getTableName("poke") + " WHERE uid='" + space.get("uid") + "'");
			if (count > 0) {
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("poke") + " WHERE uid='" + space.get("uid")
						+ "' ORDER BY dateline DESC LIMIT " + start + "," + perPage);
				for (Map<String, Object> value : list) {
					value.put("uid", value.get("fromuid"));
					value.put("username", value.get("fromusername"));
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
					value.put("isfriend", (value.get("uid") == space.get("uid") || (Common.in_array(
							(String[]) space.get("friends"), value.get("uid")))) ? true : false);
				}
				request.setAttribute("list", list);
			}
			request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
					"cp.jsp?ac=poke", null, null));
			if (count != (Integer) space.get("pokenum")) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET pokenum='" + count + "' WHERE uid='" + space.get("uid") + "'");
			}
		}
		Common.realname_get(sGlobal, sConfig, sNames, space);
		Map<Integer, String> icons = new LinkedHashMap<Integer, String>();
		icons.put(0, "不用动作");
		icons.put(1, "<img src=\"image/poke/cyx.gif\" /> 踩一下");
		icons.put(2, "<img src=\"image/poke/wgs.gif\" /> 握个手");
		icons.put(3, "<img src=\"image/poke/wx.gif\" /> 微笑");
		icons.put(4, "<img src=\"image/poke/jy.gif\" /> 加油");
		icons.put(5, "<img src=\"image/poke/pmy.gif\" /> 抛媚眼");
		icons.put(6, "<img src=\"image/poke/yb.gif\" /> 拥抱");
		icons.put(7, "<img src=\"image/poke/fw.gif\" /> 飞吻");
		icons.put(8, "<img src=\"image/poke/nyy.gif\" /> 挠痒痒");
		icons.put(9, "<img src=\"image/poke/gyq.gif\" /> 给一拳");
		icons.put(10, "<img src=\"image/poke/dyx.gif\" /> 电一下");
		icons.put(11, "<img src=\"image/poke/yw.gif\" /> 依偎");
		icons.put(12, "<img src=\"image/poke/ppjb.gif\" /> 拍拍肩膀");
		icons.put(13, "<img src=\"image/poke/yyk.gif\" /> 咬一口");
		request.setAttribute("icons", icons);
		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "cp_poke.jsp");
	}

	public ActionForward cp_poll(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int pid = Common.intval(request.getParameter("pid"));
		String op = request.getParameter("op");
		Map<String, Object> poll = null;
		sConfig.put("maxreward", (Integer) sConfig.get("maxreward") < 2 ? 10 : sConfig.get("maxreward"));
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		if (pid != 0) {
			List<Map<String, Object>> pollList = dataBaseService.executeQuery("SELECT pf.*, p.* FROM "
					+ JavaCenterHome.getTableName("poll") + " p LEFT JOIN "
					+ JavaCenterHome.getTableName("pollfield") + " pf ON pf.pid=p.pid WHERE p.pid='" + pid
					+ "'");
			if (!pollList.isEmpty()) {
				poll = pollList.get(0);
				Common.realname_set(sGlobal, sConfig, sNames, (Integer) poll.get("uid"), (String) poll
						.get("username"), null, 0);
			}
		}
		if (poll == null) {

			if (!Common.checkPerm(request, response, "allowpoll")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				}
				return showMessage(request, response, "no_authority_to_add_poll");
			}
			if (!cpService.checkRealName(request, "poll")) {
				return showMessage(request, response, "no_privilege_realname");
			}
			if (!cpService.checkVideoPhoto(request, response, "poll")) {
				return showMessage(request, response, "no_privilege_videophoto");
			}
			int result = cpService.checkNewUser(request, response);
			switch (result) {
				case 1:
					break;
				case 2:
					return showMessage(request, response, "no_privilege_newusertime", "", 1, String
							.valueOf(sConfig.get("newusertime")));
				case 3:
					return showMessage(request, response, "no_privilege_avatar");
				case 4:
					return showMessage(request, response, "no_privilege_friendnum", "", 1, String
							.valueOf(sConfig.get("need_friendnum")));
				case 5:
					return showMessage(request, response, "no_privilege_email");
			}
			int waittTime = Common.checkInterval(request, response, "post");
			if (waittTime > 0) {
				return showMessage(request, response, "operating_too_fast", null, 1, waittTime);
			}
		} else {
			if (!Common.in_array(new String[] {"vote", "get", "invite"}, op)
					&& !sGlobal.get("supe_uid").equals(poll.get("uid"))
					&& !Common.checkPerm(request, response, "managepoll")) {
				return showMessage(request, response, "no_authority_operation_of_the_poll");
			}
		}
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		try {
			if (submitCheck(request, "pollsubmit")) {
				int topicId = Common.intval(request.getParameter("topicid"));
				topicId = cpService.checkTopic(request, topicId, "poll");
				if (Common.checkPerm(request, response, "seccode")
						&& !cpService.checkSeccode(request, response, sGlobal, sConfig, request
								.getParameter("seccode"))) {
					return showMessage(request, response, "incorrect_code");
				}
				int maxOption = 20;
				String subject = Common.getStr(request.getParameter("subject"), 80, true, true, true, 0, 0,
						request, response);
				if (Common.strlen(subject) < 2) {
					return showMessage(request, response, "title_not_too_little");
				}
				String[] optionTemp = request.getParameterValues("option");
				Object[] options = null;
				if (optionTemp != null) {
					options = Common.uniqueArray(optionTemp);
				}
				List<String> newOption = new ArrayList<String>(maxOption);
				List<String> preView = new ArrayList<String>();
				if (options != null) {
					for (Object obj : options) {
						String option = Common.getStr(Common.trim(obj.toString()), 80, true, true, true, 0,
								0, request, response);
						if (Common.strlen(option) > 0 && newOption.size() < maxOption) {
							newOption.add(option);
							if (preView.size() < 2) {
								preView.add(option);
							}
						}
					}
				}
				maxOption = newOption.size();
				if (maxOption < 2) {
					return showMessage(request, response, "add_at_least_two_further_options");
				}
				int credit = Math.abs(Common.intval(request.getParameter("credit")));
				int perCredit = Math.abs(Common.intval(request.getParameter("percredit")));
				if (credit > (Integer) space.get("credit")) {
					return showMessage(request, response, "the_total_reward_should_not_overrun", null, 1,
							space.get("credit").toString());
				} else if (credit < perCredit) {
					return showMessage(request, response, "wrong_total_reward");
				} else if (credit != 0 || perCredit != 0) {
					if (credit == 0) {
						return showMessage(request, response, "the_total_reward_should_not_be_empty");
					} else if (perCredit == 0) {
						return showMessage(request, response, "average_reward_should_not_be_empty");
					}
				}
				if (perCredit != 0 && perCredit > (Integer) sConfig.get("maxreward")) {
					return showMessage(request, response, "average_reward_can_not_exceed", null, 1, sConfig
							.get("maxreward").toString());
				}
				String message = Common.getStr(request.getParameter("message"), 0, true, true, true, 2, 0,
						request, response);

				int maxChoice = Common.intval(request.getParameter("maxchoice"));
				maxChoice = maxChoice < maxOption ? maxChoice : maxOption;
				int expiration = 0;
				if (!Common.empty(request.getParameter("expiration"))) {
					expiration = Common.strToTime(Common.trim(request.getParameter("expiration"))
							+ " 23:59:59", Common.getTimeOffset(sGlobal, sConfig), "yyyy-MM-dd HH:mm:ss");
					if (expiration <= (Integer) sGlobal.get("timestamp")) {
						return showMessage(request, response, "time_expired_error");
					}
				}
				Map<String, Object> insertData = new HashMap<String, Object>();
				insertData.put("uid", sGlobal.get("supe_uid"));
				insertData.put("username", sGlobal.get("supe_username"));
				insertData.put("subject", subject);
				insertData.put("multiple", maxChoice > 1 ? 1 : 0);
				insertData.put("maxchoice", maxChoice);
				insertData.put("sex", Common.intval(request.getParameter("sex")));
				insertData.put("noreply", Common.intval(request.getParameter("noreply")));
				insertData.put("credit", credit < 0 ? 0 : credit);
				insertData.put("percredit", perCredit < 0 ? 0 : perCredit);
				insertData.put("expiration", expiration);
				insertData.put("dateline", sGlobal.get("timestamp"));
				insertData.put("topicid", topicId);
				pid = dataBaseService.insertTable("poll", insertData, true, false);
				insertData = new HashMap<String, Object>();
				insertData.put("summary", "");
				insertData.put("invite", "");
				insertData.put("hotuser", "");
				insertData.put("pid", pid);
				insertData.put("message", message);
				insertData.put("option", Common.sAddSlashes(Serializer.serialize(preView)));
				dataBaseService.insertTable("pollfield", insertData, false, false);
				List<String> optionArr = new ArrayList<String>(newOption.size());
				for (String value : newOption) {
					optionArr.add("('" + pid + "', '" + value + "')");
				}
				dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("polloption")
						+ " (`pid`, `option`) VALUES " + Common.implode(optionArr, ","));
				cpService.updateStat(sGlobal, sConfig, "poll", false);
				String pollNumSQL = null;
				if (Common.empty(space.get("pollnum"))) {
					Map whereArr = new HashMap();
					whereArr.put("uid", space.get("uid"));
					space.put("pollnum", Common.getCount("poll", whereArr, null));
					pollNumSQL = "pollnum=" + space.get("pollnum");
				} else {
					pollNumSQL = "pollnum=pollnum+1";
				}
				Map<String, Integer> reward = Common.getReward("createpoll", false, 0, "", true, request,
						response);
				int updateCredit = reward.get("credit");
				if (credit > 0) {
					updateCredit = updateCredit - credit;
				}
				dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
						+ pollNumSQL + ", lastpost='" + sGlobal.get("timestamp") + "', updatetime='"
						+ sGlobal.get("timestamp") + "', credit=credit+" + updateCredit
						+ ", experience=experience+" + reward.get("experience") + " WHERE uid='"
						+ sGlobal.get("supe_uid") + "'");
				if (!Common.empty(request.getParameter("makefeed"))) {
					feedService.feedPublish(request, response, pid, "pid", true);
				}
				String URL = null;
				if (topicId != 0) {
					cpService.topicJoin(request, topicId, (Integer) sGlobal.get("supe_uid"), (String) sGlobal
							.get("supe_username"));
					URL = "space.jsp?do=topic&topicid=" + topicId + "&view=poll";
				} else {
					URL = "space.jsp?uid=" + space.get("uid") + "&do=poll&pid=" + pid;
				}
				return showMessage(request, response, "do_success", URL, 0);
			}
			if ("addopt".equals(op)) {
				int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("polloption") + " p WHERE pid='" + pid + "'");
				if (count >= 20) {
					return showMessage(request, response, "option_exceeds_the_maximum_number_of", request
							.getParameter("refer"));
				}
				if (submitCheck(request, "addopt")) {
					String newOption = Common.getStr(request.getParameter("newoption"), 80, true, true, true,
							0, 0, request, response);
					if (Common.strlen(newOption) < 1) {
						return showMessage(request, response, "added_option_should_not_be_empty");
					}
					Map<String, Object> insertData = new HashMap<String, Object>();
					insertData.put("pid", pid);
					insertData.put("option", newOption);
					dataBaseService.insertTable("polloption", insertData, false, false);
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
			} else if ("delete".equals(op)) {
				if (submitCheck(request, "deletesubmit")) {
					if (adminDeleteService.deletePolls(request, response, (Integer) sGlobal.get("supe_uid"),
							pid)) {
						return showMessage(request, response, "do_success", "space.jsp?uid="
								+ poll.get("uid") + "&do=poll&view=me");
					} else {
						return showMessage(request, response, "failed_to_delete_operation");
					}
				}

			} else if ("modify".equals(op)) {
				if (submitCheck(request, "modifysubmit")) {
					int expiration = 0;
					if (!Common.empty(request.getParameter("expiration"))) {
						expiration = Common.strToTime(Common.trim(request.getParameter("expiration"))
								+ " 23:59:59", Common.getTimeOffset(sGlobal, sConfig), "yyyy-MM-dd HH:mm:ss");
						if (expiration <= (Integer) sGlobal.get("timestamp")) {
							return showMessage(request, response, "time_expired_error", request
									.getParameter("refer"));
						}
					}
					Map setData = new HashMap();
					setData.put("expiration", expiration);
					Map whereData = new HashMap();
					whereData.put("pid", pid);
					dataBaseService.updateTable("poll", setData, whereData);
					return showMessage(request, response, "do_success", "space.jsp?uid=" + space.get("uid")
							+ "&do=poll&pid=" + pid, 0);
				}
				request.setAttribute("poll", poll);
			} else if ("summary".equals(op)) {
				if (submitCheck(request, "summarysubmit")) {
					String summary = Common.getStr(request.getParameter("summary"), 0, true, true, true, 2,
							0, request, response);
					Map setData = new HashMap();
					setData.put("summary", summary);
					Map whereData = new HashMap();
					whereData.put("pid", pid);
					dataBaseService.updateTable("pollfield", setData, whereData);
					return showMessage(request, response, "do_success", "space.jsp?uid=" + space.get("uid")
							+ "&do=poll&pid=" + pid, 0);
				}
				poll = poll == null ? new HashMap<String, Object>() : poll;
				poll
						.put("summary", BBCode.html2bbcode(poll.get("summary").toString().replace("<br/>",
								"\n")));
				request.setAttribute("poll", poll);
			} else if ("vote".equals(op)) {
				if (submitCheck(request, "votesubmit")) {
					if (Common.empty(poll)) {
						return showMessage(request, response, "voting_does_not_exist");
					}
					if (!Common.empty(poll.get("sex")) && !poll.get("sex").equals(space.get("sex"))) {
						return showMessage(request, response, "no_privilege");
					}
					int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
							+ JavaCenterHome.getTableName("polluser") + " WHERE uid='"
							+ sGlobal.get("supe_uid") + "' AND pid='" + pid + "'");
					if (count > 0) {
						return showMessage(request, response, "already_voted");
					}
					String[] option = request.getParameterValues("option");
					List<Integer> optionArr = new ArrayList<Integer>(option == null ? 0 : option.length);
					if (option != null) {
						for (String val : option) {
							optionArr.add(Common.intval(val));
							if (optionArr.size() >= (Integer) poll.get("maxchoice")) {
								break;
							}
						}
					}
					List<Map<String, Object>> pollOptions = dataBaseService
							.executeQuery("SELECT `option` FROM " + JavaCenterHome.getTableName("polloption")
									+ " WHERE oid IN ('" + Common.implode(optionArr, "','") + "') AND pid='"
									+ pid + "'");
					List<String> list = new ArrayList<String>(pollOptions.size());
					for (Map<String, Object> value : pollOptions) {
						list.add(Common.sAddSlashes(value.get("option")).toString());
					}
					if (list.isEmpty()) {
						return showMessage(request, response, "please_select_items_to_vote");
					}
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("polloption")
							+ " SET votenum=votenum+1 WHERE oid IN ('" + Common.implode(optionArr, "','")
							+ "') AND pid='" + pid + "'");
					Map<String, Object> insertData = new HashMap<String, Object>();
					insertData.put("uid", sGlobal.get("supe_uid"));
					insertData.put("username", !Common.empty(request.getParameter("anonymous")) ? ""
							: sGlobal.get("supe_username"));
					insertData.put("pid", pid);
					insertData.put("option", Common.sAddSlashes('"' + Common.implode(list, Common.getMessage(
							request, "cp_poll_separator")) + '"'));
					insertData.put("dateline", sGlobal.get("timestamp"));
					dataBaseService.insertTable("polluser", insertData, false, false);

					String SQL = "";
					if ((Integer) poll.get("credit") > 0 && (Integer) poll.get("percredit") > 0
							&& !poll.get("uid").equals(sGlobal.get("supe_uid"))) {
						if ((Integer) poll.get("credit") <= (Integer) poll.get("percredit")) {
							poll.put("percredit", poll.get("credit"));
							SQL = ",percredit=0";
						}
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET credit=credit+" + poll.get("percredit") + " WHERE uid='"
								+ sGlobal.get("supe_uid") + "'");
					} else {
						poll.put("percredit", 0);
					}
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("poll")
							+ " SET voternum=voternum+1, lastvote='" + sGlobal.get("timestamp")
							+ "', credit=credit-" + poll.get("percredit") + " " + SQL + " WHERE pid='" + pid
							+ "'");
					Common.realname_get(sGlobal, sConfig, sNames, space);
					if (!poll.get("uid").equals(sGlobal.get("supe_uid"))) {
						Common.getReward("joinpoll", true, 0, pid + "", true, request, response);
					}
					if (!poll.get("uid").equals(sGlobal.get("supe_uid"))) {
						cpService.updateHot(request, response, "pid", (Integer) poll.get("pid"),
								(String) poll.get("hotuser"));
					}
					cpService.updateStat(sGlobal, sConfig, "pollvote", false);
					if (request.getParameter("anonymous") == null
							&& !sGlobal.get("supe_uid").equals(poll.get("uid"))
							&& Common.ckPrivacy(sGlobal, sConfig, space, "joinpoll", 1)) {
						String title_template = Common.getMessage(request, "cp_take_part_in_the_voting");
						Map title_data = new HashMap();
						title_data.put("touser", "<a href=\"space.jsp?uid=" + poll.get("uid") + "\">"
								+ sNames.get(poll.get("uid")) + "</a>");
						title_data.put("url", "space.jsp?uid=" + poll.get("uid") + "&do=poll&pid=" + pid);
						title_data.put("subject", poll.get("subject"));
						title_data.put("reward", (Integer) poll.get("percredit") > 0 ? Common.getMessage(
								request, "cp_reward") : "");
						cpService.addFeed(sGlobal, "poll", title_template, title_data, "", null, "", null,
								null, "", 0, 0, 0, "", false);
					}
					return showMessage(request, response, "do_success",
							"space.jsp?uid="
									+ poll.get("uid")
									+ "&do=poll&pid="
									+ pid
									+ ((Integer) poll.get("percredit") > 0 ? "&reward="
											+ poll.get("percredit") : ""), 0);
				}
			} else if ("endreward".equals(op)) {
				if (submitCheck(request, "endrewardsubmit")) {
					Map setData = new HashMap();
					setData.put("credit", 0);
					setData.put("percredit", 0);
					Map whereData = new HashMap();
					whereData.put("pid", pid);
					dataBaseService.updateTable("poll", setData, whereData);
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET credit=credit+" + poll.get("credit") + " WHERE uid='" + poll.get("uid")
							+ "'");
					return showMessage(request, response, "do_success", "space.jsp?uid=" + poll.get("uid")
							+ "&do=poll&pid=" + pid, 0);
				}
			} else if ("addreward".equals(op)) {
				if (submitCheck(request, "addrewardsubmit")) {
					int credit = Common.intval(request.getParameter("addcredit"));
					int perCredit = Common.intval(request.getParameter("addpercredit"));

					if (credit == 0 && perCredit == 0) {
						return showMessage(request, response, "fill_in_at_least_an_additional_value");
					} else if (credit > (Integer) space.get("credit")) {
						return showMessage(request, response, "the_total_reward_should_not_overrun", null, 1,
								space.get("credit").toString());
					} else if ((credit + (Integer) poll.get("credit")) < (perCredit + (Integer) poll
							.get("percredit"))) {
						return showMessage(request, response, "wrong_total_reward");
					}
					if (perCredit != 0
							&& (perCredit + (Integer) poll.get("percredit")) > (Integer) sConfig
									.get("maxreward")) {
						return showMessage(request, response, "average_reward_can_not_exceed", null, 1,
								sConfig.get("maxreward").toString());
					}
					if (credit > 0) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET credit=credit-" + credit + " WHERE uid='" + sGlobal.get("supe_uid")
								+ "'");
					}
					dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("poll")
							+ " SET credit=credit+" + credit + ",percredit=percredit+" + perCredit
							+ " WHERE pid='" + pid + "'");
					return showMessage(request, response, "do_success", "space.jsp?uid=" + poll.get("uid")
							+ "&do=poll&pid=" + pid, 0);
				}
				int maxReward = (Integer) sConfig.get("maxreward") - (Integer) poll.get("percredit");
				request.setAttribute("maxreward", maxReward);
				request.setAttribute("poll", poll);
			} else if ("get".equals(op)) {
				int perPage = 20;
				int page = Common.intval(request.getParameter("page"));
				if (page < 1) {
					page = 1;
				}
				int start = (page - 1) * perPage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String str = Common.ckStart(start, perPage, maxPage);
				if (str != null) {
					return showMessage(request, response, str);
				}
				String filtrate = Common.trim(request.getParameter("filtrate"));
				filtrate = Common.empty(filtrate) ? "new" : filtrate;
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("filtrate", new String[] {filtrate});
				List<String> whereArr = new ArrayList<String>();
				if ("we".equals(filtrate)) {
					if (Common.empty(space.get("feedfriend"))) {
						space.put("feedfriend", 0);
					}
					whereArr.add("uid IN (" + space.get("feedfriend") + ")");
				}
				whereArr.add("pid='" + pid + "'");
				String whereSQL = " WHERE " + Common.implode(whereArr, " AND ");
				int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("polluser") + " " + whereSQL);
				if (count > 0) {
					List<Map<String, Object>> voteResult = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("polluser") + " " + whereSQL
							+ " ORDER BY dateline DESC LIMIT " + start + "," + perPage);
					for (Map<String, Object> value : voteResult) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"),
								(String) value.get("username"), null, 0);
					}
					request.setAttribute("voteresult", voteResult);
					request.setAttribute("multi", Common.multi(request, count, perPage, page, maxPage,
							"cp.jsp?ac=poll&op=get&pid=" + pid + "&filtrate=" + filtrate, "showvoter", null));
					Common.realname_get(sGlobal, sConfig, sNames, space);
				}
			} else if ("invite".equals(op)) {
				String[] uidArr = poll.get("invite").toString().split(",");
				Map<String, Integer> newUid = new HashMap<String, Integer>();
				for (int i = 0; i < uidArr.length; i++) {
					newUid.put(uidArr[i], i);
				}
				if (submitCheck(request, "invitesubmit")) {
					String[] ids = request.getParameterValues("ids");
					if (ids != null) {
						Map newIds = new HashMap();
						for (int i = 0; i < ids.length; i++) {
							String uid = ids[i];
							if (newUid.get(uid) == null) {
								newIds.put(i, Common.intval(uid));
							}
						}
						List<Map<String, Object>> spaceList = dataBaseService.executeQuery("SELECT uid FROM "
								+ JavaCenterHome.getTableName("space") + " WHERE uid IN ("
								+ Common.sImplode(newIds) + ")");
						newIds = new HashMap();
						for (Map<String, Object> value : spaceList) {
							newIds.put(value.get("uid"), value.get("uid"));
						}
						List<Map<String, Object>> pollUserList = dataBaseService
								.executeQuery("SELECT uid FROM " + JavaCenterHome.getTableName("polluser")
										+ " WHERE uid IN (" + Common.sImplode(newIds) + ") AND pid='" + pid
										+ "'");
						for (Map<String, Object> value : pollUserList) {
							newIds.remove(value.get("uid"));
						}
						String[] newInvite = arrayMerge(uidArr, newIds.values().toArray());
						if (newInvite.length > 0) {
							dataBaseService.executeUpdate("UPDATE "
									+ JavaCenterHome.getTableName("pollfield") + " SET invite='"
									+ Common.implode(newInvite, ",") + "' WHERE pid='" + pid + "'");
						}
						String note = Common.getMessage(request, "cp_note_poll_invite", new String[] {
								"space.jsp?uid=" + poll.get("uid") + "&do=poll&pid=" + poll.get("pid"),
								poll.get("subject").toString(),
								(Integer) poll.get("percredit") > 0 ? Common.getMessage(request, "cp_reward")
										: ""});
						for (Object uid : newIds.values()) {
							if (!Common.empty(uid) && !uid.equals(sGlobal.get("supe_uid"))) {
								cpService.addNotification(request, sGlobal, sConfig, (Integer) uid,
										"pollinvite", note, false);
							}
						}
					}
					return showMessage(request, response, "do_success", "space.jsp?uid=" + poll.get("uid")
							+ "&do=poll&pid=" + pid);
				}
				int perPage = 20;
				int page = Common.intval(request.getParameter("page"));
				if (page < 1) {
					page = 1;
				}
				int start = (page - 1) * perPage;
				int maxPage = (Integer) sConfig.get("maxpage");
				String str = Common.ckStart(start, perPage, maxPage);
				if (str != null) {
					return showMessage(request, response, str);
				}
				List<String> whereArr = new ArrayList<String>();
				String key = Common.stripSearchKey(request.getParameter("key"));
				if (!Common.empty(key)) {
					whereArr.add(" fusername LIKE '%" + key + "%' ");
				}
				int group = request.getParameter("group") != null ? Common.intval(request
						.getParameter("group")) : -1;
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("group", new String[] {group + ""});
				if (group >= 0) {
					whereArr.add(" gid='" + group + "'");
				}
				String SQL = !whereArr.isEmpty() ? "AND" + Common.implode(whereArr, " AND ") : "";
				int count = dataBaseService.findRows("SELECT COUNT(*) FROM "
						+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + sGlobal.get("supe_uid")
						+ "' AND status='1' " + SQL);
				List<Object> fUids = new ArrayList<Object>();
				if (count > 0) {
					List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("friend") + " WHERE uid='"
							+ sGlobal.get("supe_uid") + "' AND status='1' " + SQL
							+ " ORDER BY num DESC, dateline DESC LIMIT " + start + "," + perPage);
					for (Map<String, Object> value : list) {
						Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("fuid"),
								(String) value.get("fusername"), null, 0);
						fUids.add(value.get("fuid"));
					}
					request.setAttribute("list", list);
				}
				Map inviteArr = new HashMap();
				List<Map<String, Object>> pollUserList = dataBaseService.executeQuery("SELECT uid FROM "
						+ JavaCenterHome.getTableName("polluser") + " WHERE uid IN ("
						+ Common.sImplode(fUids) + ") AND pid='" + pid + "'");
				for (Map<String, Object> value : pollUserList) {
					inviteArr.put(value.get("uid"), value.get("uid"));
				}
				for (String strUid : uidArr) {
					int uid = Common.intval(strUid);
					inviteArr.put(uid, uid);
				}
				Common.realname_get(sGlobal, sConfig, sNames, space);
				request.setAttribute("groups", Common.getFriendGroup(request));
				Map groupSelect = new HashMap();
				groupSelect.put(group + "", " selected");
				request.setAttribute("multi", Common
						.multi(request, count, perPage, page, maxPage, "cp.jsp?ac=poll&op=invite&pid="
								+ poll.get("pid") + "&group=" + group + "&key=" + key, null, null));
				request.setAttribute("poll", poll);
				request.setAttribute("invitearr", inviteArr);
			} else if ("edithot".equals(op)) {
				if (!Common.checkPerm(request, response, "managepoll")) {
					return showMessage(request, response, "no_privilege");
				}
				if (submitCheck(request, "hotsubmit")) {
					int hot = Common.intval(request.getParameter("hot"));
					Map setData = new HashMap();
					setData.put("hot", hot);
					Map whereData = new HashMap();
					whereData.put("pid", pid);
					dataBaseService.updateTable("poll", setData, whereData);
					if (hot > 0) {
						feedService.feedPublish(request, response, hot, "hot", false);
					} else {
						whereData = new HashMap();
						whereData.put("id", pid);
						whereData.put("idtype", "pid");
						dataBaseService.updateTable("feed", setData, whereData);
					}
					return showMessage(request, response, "do_success", "space.jsp?uid=" + poll.get("uid")
							+ "&do=poll&pid=" + pid, 0);
				}
				request.setAttribute("poll", poll);
			} else {
				int topicId = Common.intval(request.getParameter("topicid"));
				Map topic = null;
				if (topicId > 0) {
					topic = Common.getTopic(request, topicId);
					request.setAttribute("topic", topic);
				}
				if (topic != null) {
					Map actives = new HashMap();
					actives.put("poll", " class=\"active\"");
					request.setAttribute("actives", actives);
				}
				request.setAttribute("topicid", topicId);
				Integer[] option = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
						18, 19, 20};
				request.setAttribute("option", option);
				request.setAttribute("ckPrivacyBypoll", Common.ckPrivacy(sGlobal, sConfig, space, "poll", 1));
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		request.setAttribute("pid", pid);
		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "cp_poll.jsp");
	}

	
	public ActionForward cp_privacy(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		try {
			if (submitCheck(request, "privacysubmit")) {
				
				Map view = new HashMap();
				Map feed = new HashMap();

				Map privacy = (Map) space.get("privacy");
				privacy.put("view", view);
				privacy.put("feed", feed);

				Pattern pattern = Pattern.compile(".*\\[(.*)\\]$");
				Enumeration parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String name = (String) parameterNames.nextElement();
					String key = pattern.matcher(name).replaceAll("$1");
					String val = request.getParameter(name);
					if (name.startsWith("privacy[view]")) {
						
						view.put(key, Common.intval(val));
					} else if (name.startsWith("privacy[feed]")) {
						
						feed.put(key, 1);
					}
				}
				cpService.privacyUpdate(privacy, (Integer) sGlobal.get("supe_uid"));

				if (!Common.empty(sConfig.get("my_status"))) {
					Map insertmap = new HashMap();
					insertmap.put("uid", (Integer) sGlobal.get("supe_uid"));
					insertmap.put("action", "update");
					insertmap.put("dateline", sGlobal.get("timestamp"));
					dataBaseService.insertTable("userlog", insertmap, false, true);
				}

				return showMessage(request, response, "do_success", "cp.jsp?ac=privacy");

			} else if (submitCheck(request, "privacy2submit")) {
				

				Map filterIcon = new HashMap();
				Map filterGid = new HashMap();
				Map filterNote = new HashMap();

				Map privacy = (Map) space.get("privacy");
				privacy.put("filter_icon", filterIcon);
				privacy.put("filter_gid", filterGid);
				privacy.put("filter_note", filterNote);

				Pattern pattern = Pattern.compile(".*\\[(.*)\\]$");
				Enumeration parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String name = (String) parameterNames.nextElement();
					String key = pattern.matcher(name).replaceAll("$1");
					String val = request.getParameter(name);
					if (name.startsWith("privacy[filter_icon]")) {
						
						filterIcon.put(key, 1);
					} else if (name.startsWith("privacy[filter_gid]")) {
						
						filterGid.put(Integer.valueOf(key), Common.intval(val));
					} else if (name.startsWith("privacy[filter_note]")) {
						
						filterNote.put(key, 1);
					}
				}
				cpService.privacyUpdate(privacy, (Integer) sGlobal.get("supe_uid"));
				cpService.friendCache(request, sGlobal, sConfig, (Integer) sGlobal.get("supe_uid"));
				return showMessage(request, response, "do_success", "cp.jsp?ac=privacy&op=view");
			}
			String op = request.getParameter("op");
			if ("view".equals(op)) {
				

				Map<Integer, String> groups = Common.getFriendGroup(request);
				Map<String, String> icons = new HashMap<String, String>();
				Map<String, String> uids = new HashMap<String, String>();
				Map<String, String> types = new HashMap<String, String>();
				Map<String, String> appids = new HashMap<String, String>();
				Map<String, Object> users = new HashMap<String, Object>();
				Map<String, Object> iconnames = new HashMap<String, Object>();

				Map<String, Object> privacy = (Map<String, Object>) space.get("privacy");
				Map filterIcon = (Map) privacy.get("filter_icon");
				Map filterNote = (Map) privacy.get("filter_note");
				if (!Common.empty(filterIcon)) {
					Set<String> keys = filterIcon.keySet();
					for (String key : keys) {
						String[] arr = key.split("\\|");
						icons.put(key, arr[0]);
						uids.put(key, arr[1]);
						if (Common.isNumeric(arr[0])) {
							appids.put(key, arr[0]);
						}
					}
				}
				if (!Common.empty(filterNote)) {
					Set<String> keys = filterNote.keySet();
					for (String key : keys) {
						String[] arr = key.split("\\|");
						types.put(key, arr[0]);
						uids.put(key, arr[1]);
						if (Common.isNumeric(arr[0])) {
							appids.put(key, arr[0]);
						}
					}
				}

				if (uids.size() > 0) {
					List<Map<String, Object>> query = dataBaseService
							.executeQuery("SELECT uid, username FROM " + JavaCenterHome.getTableName("space")
									+ " WHERE uid IN (" + Common.sImplode(uids) + ")");
					for (Map<String, Object> value : query) {
						users.put(String.valueOf(value.get("uid")), value.get("username"));
					}
				}
				if (appids.size() > 0) {
					List<Map<String, Object>> query = dataBaseService
							.executeQuery("SELECT appid, appname FROM "
									+ JavaCenterHome.getTableName("myapp") + " WHERE appid IN ("
									+ Common.sImplode(appids) + ")");
					for (Map<String, Object> value : query) {
						iconnames.put(String.valueOf(value.get("appid")), value.get("appname"));
					}
				}
				iconnames.put("activity", "日志");
				iconnames.put("album", "相册");
				iconnames.put("blog", "日志");
				iconnames.put("comment", "评论");
				iconnames.put("blogcomment", "日志评论");
				iconnames.put("clickblog", "日志表态");
				iconnames.put("clickpic", "图片表态");
				iconnames.put("clickthread", "话题表态");
				iconnames.put("piccomment", "图片评论");
				iconnames.put("sharecomment", "分享评论");
				iconnames.put("debate", "论坛辩论");
				iconnames.put("jsprun", "论坛");
				iconnames.put("doing", "记录");
				iconnames.put("friend", "好友");
				iconnames.put("goods", "商品");
				iconnames.put("mood", "心情");
				iconnames.put("mtag", "群组");
				iconnames.put("event", "活动");
				iconnames.put("eventcomment", "活动留言");
				iconnames.put("eventmember", "活动成员管理");
				iconnames.put("eventmemberstatus", "活动成员身份");
				iconnames.put("network", "随便看看");
				iconnames.put("poll", "论坛投票");
				iconnames.put("post", "论坛回贴");
				iconnames.put("profile", "更新个人资料");
				iconnames.put("reward", "论坛悬赏");
				iconnames.put("share", "分享");
				iconnames.put("sharenotice", "分享通知");
				iconnames.put("show", "排行榜");
				iconnames.put("task", "有奖任务");
				iconnames.put("thread", "话题");
				iconnames.put("post", "话题回复");
				iconnames.put("video", "视频");
				iconnames.put("wall", "留言");
				iconnames.put("credit", "赠送竞价积分");
				iconnames.put("poll", "投票");
				iconnames.put("pollcomment", "投票评论");
				iconnames.put("pollinvite", "投票邀请");
				request.setAttribute("groups", groups);
				request.setAttribute("uids", uids);
				request.setAttribute("icons", icons);
				request.setAttribute("users", users);
				request.setAttribute("types", types);
				request.setAttribute("iconnames", iconnames);
				request.setAttribute("cat_active_view", " class=\"active\"");

			} else if ("getgroup".equals(op)) {
				
				int gid = Common.intval(request.getParameter("gid"));
				List<String> users = dataBaseService.executeQuery("SELECT fusername FROM "
						+ JavaCenterHome.getTableName("friend") + " WHERE uid='" + sGlobal.get("supe_uid")
						+ "' AND status='1' AND gid='" + gid + "'", 1);
				String ustr = Common.empty(users) ? "" : (String) Common.sHtmlSpecialChars(Common.implode(
						users, " "));
				return showMessage(request, response, ustr);

			} else {
				
				Map<String, Object> privacy = (Map<String, Object>) space.get("privacy");
				Map view = (Map) privacy.get("view");
				Map feed = (Map) privacy.get("feed");
				Map viewShow = new HashMap();
				Map feedShow = new HashMap();
				for (Iterator it = view.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					viewShow.put(key + "_" + view.get(key), " selected");
				}
				for (Iterator it = feed.keySet().iterator(); it.hasNext();) {
					feedShow.put(it.next(), " checked");
				}
				request.setAttribute("view", viewShow);
				request.setAttribute("feed", feedShow);
				request.setAttribute("cat_active_base", " class=\"active\"");
			}

		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		return include(request, response, sConfig, sGlobal, "cp_privacy.jsp");
	}

	
	public ActionForward cp_profile(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");

		String[] ops = {"base", "contact", "edu", "work", "info"};
		String op = request.getParameter("op");
		if (!Common.in_array(ops, op)) {
			op = "base";
		}
		Object uid = space.get("uid");
		String tname = JavaCenterHome.getTableName("spaceinfo");
		try {
			if (op.equals("base")) {
				if (submitCheck(request, "profilesubmit") || submitCheck(request, "nextsubmit")) {
					
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("birthyear", Common.intval(request.getParameter("birthyear")));
					setData.put("birthmonth", Common.intval(request.getParameter("birthmonth")));
					setData.put("birthday", Common.intval(request.getParameter("birthday")));
					setData.put("blood", Common.getStr(request.getParameter("blood"), 5, true, true, false,
							0, 0, request, response));
					setData.put("marry", Common.intval(request.getParameter("marry")));
					setData.put("birthprovince", Common.getStr(request.getParameter("birthprovince"), 20,
							true, true, false, 0, 0, request, response));
					setData.put("birthcity", Common.getStr(request.getParameter("birthcity"), 20, true, true,
							false, 0, 0, request, response));
					setData.put("resideprovince", Common.getStr(request.getParameter("resideprovince"), 20,
							true, true, false, 0, 0, request, response));
					setData.put("residecity", Common.getStr(request.getParameter("residecity"), 20, true,
							true, false, 0, 0, request, response));
					int sex = Common.intval(request.getParameter("sex"));
					if (!Common.empty(sex) && Common.empty(space.get("sex"))) {
						setData.put("sex", sex);
					}
					File profileCache = new File(JavaCenterHome.jchRoot + "data/cache/cache_profilefield.jsp");
					if (!profileCache.exists()) {
						cacheService.profilefield_cache();
					}
					Map<Integer, Map> profileFields = Common.getCacheDate(request, response,
							"/data/cache/cache_profilefield.jsp", "globalProfilefield");
					Set<Entry<Integer, Map>> entrys = profileFields.entrySet();
					for (Entry<Integer, Map> entry : entrys) {
						int key = entry.getKey();
						Map value = entry.getValue();
						if ("select".equals(value.get("formtype"))) {
							value.put("maxsize", 255);
						}
						setData.put("field_" + key, Common.getStr(request.getParameter("field_" + key),
								(Integer) value.get("maxsize"), true, true, false, 0, 0, request, response));
						if (!Common.empty(value.get("required")) && Common.empty(setData.get("field_" + key))) {
							return showMessage(request, response, "field_required", null, 1, value
									.get("title"));
						}
					}
					Map whereData = new HashMap();
					whereData.put("uid", sGlobal.get("supe_uid"));
					dataBaseService.updateTable("spacefield", setData, whereData);
					List<String> inserts = new ArrayList<String>();
					Pattern p = Pattern.compile("friend\\[(.*)\\]");
					for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
						String paramName = e.nextElement();
						if (paramName.startsWith("friend[")) {
							String key = p.matcher(paramName).replaceAll("$1");
							int value = Common.intval(request.getParameter(paramName));
							inserts.add("('base','" + key + "','" + uid + "','','" + value + "')");
						}
					}
					if (inserts.size() > 0) {
						dataBaseService.executeUpdate("DELETE FROM " + tname + " WHERE uid='" + uid
								+ "' AND type='base'");
						dataBaseService.executeUpdate("INSERT INTO " + tname
								+ " (type,subtype,uid,title,friend) VALUES " + Common.implode(inserts, ","));
					}
					setData = new HashMap();
					setData.put("name", Common.getStr(request.getParameter("name"), 10, true, true, true, 0,
							0, request, response));
					setData.put("namestatus", Common.empty(sConfig.get("namecheck")) ? 1 : 0);

					boolean manageName = Common.checkPerm(request, response, "managename");
					if (manageName) {
						
						setData.put("namestatus", 1);
					}
					int length = Common.strlen((String) setData.get("name"));
					if (length > 0 && length < 4) {
						return showMessage(request, response, "realname_too_short");
					}
					String newName = (String) setData.get("name");
					String oldName = (String) space.get("name");
					boolean autoCheck = (Integer) setData.get("namestatus") == 1 ? true : false;
					if (!newName.equals(oldName) || autoCheck) {
						boolean realNameCheck = !Common.empty(sConfig.get("realname")) ? true : false;
						if (realNameCheck && Common.empty(oldName) && !newName.equals(oldName) && autoCheck) {
							
							Map reward = Common.getReward("realname", false, 0, "", true, request, response);
							int credit = (Integer) reward.get("credit");
							int experience = (Integer) reward.get("experience");
							if (credit != 0) {
								setData.put("credit", (Integer) space.get("credit") + credit);
							}
							if (experience != 0) {
								setData.put("experience", (Integer) space.get("experience") + experience);
							}
						} else if (realNameCheck && !Common.empty(space.get("namestatus")) && !manageName) {
							
							Map reward = Common.getReward("editrealname", false, 0, "", true, request,
									response);
							int credit = (Integer) reward.get("credit");
							int experience = (Integer) reward.get("experience");
							if (!Common.empty(oldName) && !newName.equals(oldName)
									&& (credit != 0 || experience != 0)) {
								int spaceExperience = (Integer) space.get("experience");
								if (spaceExperience >= experience) {
									
									setData.put("experience", spaceExperience - experience);
								} else {
									String[] args = {String.valueOf(spaceExperience),
											String.valueOf(experience)};
									return showMessage(request, response, "experience_inadequate", null, 1,
											args);
								}
								int spaceCredit = (Integer) space.get("credit");
								if (spaceCredit >= credit) {
									
									setData.put("credit", spaceCredit - credit);
								} else {
									String[] args = {String.valueOf(spaceCredit), String.valueOf(credit)};
									return showMessage(request, response, "integral_inadequate", null, 1,
											args);
								}
							}
						}
						whereData = new HashMap();
						whereData.put("uid", sGlobal.get("supe_uid"));
						dataBaseService.updateTable("space", setData, whereData);
					}
					if (!Common.empty(sConfig.get("my_status"))) {
						Map insertData = new HashMap();
						insertData.put("uid", sGlobal.get("supe_uid"));
						insertData.put("action", "update");
						insertData.put("dateline", sGlobal.get("timestamp"));
						insertData.put("type", 0);
						dataBaseService.insertTable("userlog", insertData, false, true);
					}
					if (Common.ckPrivacy(sGlobal, sConfig, space, "profile", 1)) {
						cpService.addFeed(sGlobal, "profile", Common.getMessage(request,
								"cp_feed_profile_update_base"), null, "", null, "", null, null, "", 0, 0, 0,
								"", false);
					}
					String url = null;
					if (submitCheck(request, "nextsubmit")) {
						url = "cp.jsp?ac=profile&op=contact";
					} else {
						url = "cp.jsp?ac=profile&op=base";
					}
					return showMessage(request, response, "update_on_successful_individuals", url);
				}

				
				Map sexmap = new HashMap();
				sexmap.put(String.valueOf(space.get("sex")), " checked");
				StringBuffer birthYearHtml = new StringBuffer();
				int nowYear = Common.intval(Common.sgmdate(request, "yyyy", (Integer) sGlobal
						.get("timestamp")));
				for (int i = 0; i < 100; i++) {
					int they = nowYear - i;
					String selected = they == (Integer) space.get("birthyear") ? "selected" : "";
					birthYearHtml.append("<option value=\"" + they + "\" " + selected + ">" + they
							+ "</option>");
				}
				StringBuffer birthMonthHtml = new StringBuffer();
				for (int i = 1; i < 13; i++) {
					String selected = i == (Integer) space.get("birthmonth") ? "selected" : "";
					birthMonthHtml.append("<option value=\"" + i + "\" " + selected + ">" + i + "</option>");
				}
				StringBuffer birthDayHtml = new StringBuffer();
				for (int i = 1; i < 32; i++) {
					String selected = i == (Integer) space.get("birthday") ? "selected" : "";
					birthDayHtml.append("<option value=\"" + i + "\" " + selected + ">" + i + "</option>");
				}
				StringBuffer bloodHtml = new StringBuffer();
				String[] blood = {"A", "B", "O", "AB"};
				for (String value : blood) {
					String selected = value.equals(space.get("blood")) ? "selected" : "";
					bloodHtml.append("<option value=\"" + value + "\" " + selected + ">" + value
							+ "</option>");
				}
				Map marriagemap = new HashMap();
				marriagemap.put(String.valueOf(space.get("marry")), " selected");
				List profileFields = new ArrayList();
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("profilefield") + " ORDER BY displayorder");
				for (Map field : query) {
					int fieldId = (Integer) field.get("fieldid");
					if ("text".equals(field.get("formtype"))) {
						field.put("formhtml", "<input type=\"text\" name=\"field_" + fieldId + "\" value=\""
								+ space.get("field_" + fieldId) + "\" class=\"t_input\">");
					} else {
						StringBuffer formHtml = new StringBuffer();
						formHtml.append("<select name=\"field_" + fieldId + "\">");
						if (Common.empty(field.get("required"))) {
							formHtml.append("<option value=\"\"></option>");
						}
						String[] options = ((String) field.get("choice")).split("\n");
						for (String option : options) {
							option = option.trim();
							if (option.length() != 0) {
								String selected = option.equals(space.get("field_" + fieldId)) ? "selected"
										: "";
								formHtml.append("<option value=\"" + option + "\" " + selected + ">" + option
										+ "</option>");
							}
						}
						formHtml.append("</select>");
						field.put("formhtml", formHtml.toString());
					}
					profileFields.add(field);
				}
				Map friendmap = new HashMap();
				List<Map<String, Object>> infoList = dataBaseService.executeQuery("SELECT * FROM " + tname
						+ " WHERE uid='" + space.get("uid") + "' AND type='base'");
				for (Map info : infoList) {
					Map value = new HashMap();
					value.put(String.valueOf(info.get("friend")), " selected");
					friendmap.put(info.get("subtype"), value);
				}

				request.setAttribute("username", Common.stripSlashes((String) space.get("username")));
				request.setAttribute("name", Common.stripSlashes((String) space.get("name")));
				request.setAttribute("namechange", Common.empty(sConfig.get("namechange")) ? 0 : request
						.getParameter("namechange"));
				request.setAttribute("sexmap", sexmap);
				request.setAttribute("marriagemap", marriagemap);
				request.setAttribute("friendmap", friendmap);
				request.setAttribute("birthyearhtml", birthYearHtml.toString());
				request.setAttribute("birthmonthhtml", birthMonthHtml.toString());
				request.setAttribute("birthdayhtml", birthDayHtml.toString());
				request.setAttribute("bloodhtml", bloodHtml.toString());
				request.setAttribute("profilefields", profileFields);

			} else if (op.equals("contact")) {
				if ("1".equals(request.getParameter("resend"))) {
					
					String toEmail = !Common.empty(space.get("newemail")) ? (String) space.get("newemail")
							: (String) space.get("email");
					cpService.sendEmailCheck(request, response, (Integer) space.get("uid"), toEmail);
					return showMessage(request, response, "do_success", "cp.jsp?ac=profile&op=contact");
				}

				if (submitCheck(request, "profilesubmit") || submitCheck(request, "nextsubmit")) {
					
					Map setmap = new HashMap();
					setmap.put("mobile", Common.getStr(request.getParameter("mobile"), 40, true, true, false,
							0, 0, request, response));
					setmap.put("qq", Common.getStr(request.getParameter("qq"), 20, true, true, false, 0, 0,
							request, response));
					setmap.put("msn", Common.getStr(request.getParameter("msn"), 80, true, true, false, 0, 0,
							request, response));
					String newEmail = request.getParameter("email");
					String oldEmail = (String) space.get("email");
					if (newEmail != null && Common.isEmail(newEmail) && !newEmail.equals(oldEmail)) {
						if (!Common.empty(sConfig.get("uniqueemail"))) {
							
							Map where = new HashMap();
							where.put("email", newEmail);
							where.put("emailcheck", 1);
							if (!Common.empty(Common.getCount("spacefield", where, null))) {
								return showMessage(request, response, "uniqueemail_check");
							}
						}
						String password = request.getParameter("password");
						List<Map<String, Object>> members = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("member") + " WHERE uid = '"
								+ sGlobal.get("supe_uid") + "'");
						if (members.size() != 0) {
							Map<String, Object> member = members.get(0);
							password = Common.md5(Common.md5(password) + member.get("salt"));
							if (!password.equals(member.get("password"))) {
								return showMessage(request, response, "password_is_not_passed");
							}
						} else {
							return showMessage(request, response, "password_is_not_passed");
						}

						if (newEmail == null || newEmail.length() == 0) {
							
							setmap.put("email", "");
							setmap.put("emailcheck", 0);
						} else if (!newEmail.equals(oldEmail)) {
							if (!Common.empty(space.get("emailcheck"))) {
								setmap.put("newemail", newEmail);
							} else {
								setmap.put("email", newEmail);
							}
							cpService.sendEmailCheck(request, response, (Integer) space.get("uid"), newEmail);
						}
					}
					Map wheremap = new HashMap();
					wheremap.put("uid", sGlobal.get("supe_uid"));
					dataBaseService.updateTable("spacefield", setmap, wheremap);
					List inserts = new ArrayList();
					Pattern p = Pattern.compile("friend\\[(.*)\\]");
					for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
						String paramName = (String) e.nextElement();
						if (paramName.startsWith("friend[")) {
							String key = p.matcher(paramName).replaceAll("$1");
							int value = Common.intval(request.getParameter(paramName));
							inserts.add("('contact','" + key + "','" + space.get("uid") + "','','" + value
									+ "')");
						}
					}
					if (inserts.size() != 0) {
						dataBaseService.executeUpdate("DELETE FROM " + tname + " WHERE uid='"
								+ space.get("uid") + "' AND type='contact'");
						dataBaseService.executeUpdate("INSERT INTO " + tname
								+ " (type,subtype,uid,title,friend) VALUES " + Common.implode(inserts, ","));
					}
					if (!Common.empty(sConfig.get("my_status"))) {
						Map insertmap = new HashMap();
						insertmap.put("uid", sGlobal.get("supe_uid"));
						insertmap.put("action", "update");
						insertmap.put("dateline", sGlobal.get("timestamp"));
						insertmap.put("type", 2);
						dataBaseService.insertTable("userlog", insertmap, false, true);
					}
					if (Common.ckPrivacy(sGlobal, sConfig, space, "profile", 1)) {
						cpService.addFeed(sGlobal, "profile", Common.getMessage(request,
								"cp_feed_profile_update_contact"), null, "", null, "", null, null, "", 0, 0,
								0, "", false);
					}

					if (submitCheck(request, "nextsubmit")) {
						return showMessage(request, response, "update_on_successful_individuals",
								"cp.jsp?ac=profile&op=edu");
					} else {
						return showMessage(request, response, "update_on_successful_individuals",
								"cp.jsp?ac=profile&op=contact");
					}
				}
				
				Map friendmap = new HashMap();
				List<Map<String, Object>> infoList = dataBaseService.executeQuery("SELECT * FROM " + tname
						+ " WHERE uid='" + space.get("uid") + "' AND type='contact'");
				for (Map info : infoList) {
					Map value = new HashMap();
					value.put(String.valueOf(info.get("friend")), " selected");
					friendmap.put(info.get("subtype"), value);
				}
				request.setAttribute("friendmap", friendmap);

			} else if (op.equals("edu")) {
				if ("delete".equals(request.getParameter("subop"))) {
					
					int infoId = Common.intval(request.getParameter("infoid"));
					if (infoId != 0) {
						dataBaseService.executeUpdate("DELETE FROM " + tname + " WHERE infoid='" + infoId
								+ "' AND uid='" + uid + "' AND type='edu'");
					}
				}
				if (submitCheck(request, "profilesubmit") || submitCheck(request, "nextsubmit")) {
					
					List inserts = new ArrayList();

					String[] title = request.getParameterValues("title[]");
					String[] subTitle = request.getParameterValues("subtitle[]");
					String[] startYear = request.getParameterValues("startyear[]");
					String[] friend = request.getParameterValues("friend[]");
					int groupCount = title.length;
					for (int i = 0; i < groupCount; i++) {
						title[i] = Common.getStr(title[i], 100, true, true, false, 0, 0, request, response);
						if (title[i].length() != 0) {
							subTitle[i] = Common.getStr(subTitle[i], 20, true, true, false, 0, 0, request,
									response);
							startYear[i] = String.valueOf(Common.intval(startYear[i]));
							friend[i] = String.valueOf(Common.intval(friend[i]));
							inserts.add("('" + uid + "','edu','" + title[i] + "','" + subTitle[i] + "','"
									+ startYear[i] + "','" + friend[i] + "')");
						}
					}
					if (inserts.size() != 0) {
						dataBaseService.executeUpdate("INSERT INTO " + tname
								+ " (uid,type,title,subtitle,startyear,friend) VALUES "
								+ Common.implode(inserts, ","));
					}
					if (!Common.empty(sConfig.get("my_status"))) {
						Map insertmap = new HashMap();
						insertmap.put("uid", sGlobal.get("supe_uid"));
						insertmap.put("action", "update");
						insertmap.put("dateline", sGlobal.get("timestamp"));
						insertmap.put("type", 2);
						dataBaseService.insertTable("userlog", insertmap, false, true);
					}
					if (Common.ckPrivacy(sGlobal, sConfig, space, "profile", 1)) {
						cpService.addFeed(sGlobal, "profile", Common.getMessage(request,
								"cp_feed_profile_update_edu"), null, "", null, "", null, null, "", 0, 0, 0,
								"", false);
					}
					if (submitCheck(request, "nextsubmit")) {
						return showMessage(request, response, "update_on_successful_individuals",
								"cp.jsp?ac=profile&op=work");
					} else {
						return showMessage(request, response, "update_on_successful_individuals",
								"cp.jsp?ac=profile&op=edu");
					}
				}
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM " + tname
						+ " WHERE uid='" + uid + "' AND type='edu' ORDER BY startyear");
				for (Map<String, Object> value : list) {
					value.put("title_s", Common.urlEncode((String) value.get("title")));
					value.put("friend", String.valueOf(value.get("friend")));
				}
				request.setAttribute("list", list);

			} else if (op.equals("work")) {
				if ("delete".equals(request.getParameter("subop"))) {
					
					int infoId = Common.intval(request.getParameter("infoid"));
					if (infoId != 0) {
						dataBaseService.executeUpdate("DELETE FROM " + tname + " WHERE infoid='" + infoId
								+ "' AND uid='" + uid + "' AND type='work'");
					}
				}
				if (submitCheck(request, "profilesubmit") || submitCheck(request, "nextsubmit")) {
					
					List inserts = new ArrayList();

					String[] title = request.getParameterValues("title[]");
					String[] subTitle = request.getParameterValues("subtitle[]");
					String[] startYear = request.getParameterValues("startyear[]");
					String[] startMonth = request.getParameterValues("startmonth[]");
					String[] endYear = request.getParameterValues("endyear[]");
					String[] endMonth = request.getParameterValues("endmonth[]");
					String[] friend = request.getParameterValues("friend[]");
					int groupCount = title.length;
					for (int i = 0; i < groupCount; i++) {
						title[i] = Common.getStr(title[i], 100, true, true, false, 0, 0, request, response);
						if (title[i].length() != 0) {
							subTitle[i] = Common.getStr(subTitle[i], 20, true, true, false, 0, 0, request,
									response);
							startYear[i] = String.valueOf(Common.intval(startYear[i]));
							startMonth[i] = String.valueOf(Common.intval(startMonth[i]));
							endYear[i] = String.valueOf(Common.intval(endYear[i]));
							endMonth[i] = endYear[i].equals("0") == false ? String.valueOf(Common
									.intval(endMonth[i])) : "0";
							friend[i] = String.valueOf(Common.intval(friend[i]));
							inserts.add("('" + uid + "','work','" + title[i] + "','" + subTitle[i] + "','"
									+ startYear[i] + "','" + startMonth[i] + "','" + endYear[i] + "','"
									+ endMonth[i] + "','" + friend[i] + "')");
						}
					}
					if (inserts.size() != 0) {
						dataBaseService
								.executeUpdate("INSERT INTO "
										+ tname
										+ " (uid,type,title,subtitle,startyear,startmonth,endyear,endmonth,friend) VALUES "
										+ Common.implode(inserts, ","));
					}
					if (!Common.empty(sConfig.get("my_status"))) {
						Map insertmap = new HashMap();
						insertmap.put("uid", sGlobal.get("supe_uid"));
						insertmap.put("action", "update");
						insertmap.put("dateline", sGlobal.get("timestamp"));
						insertmap.put("type", 2);
						dataBaseService.insertTable("userlog", insertmap, false, true);
					}
					if (Common.ckPrivacy(sGlobal, sConfig, space, "profile", 1)) {
						cpService.addFeed(sGlobal, "profile", Common.getMessage(request,
								"cp_feed_profile_update_work"), null, "", null, "", null, null, "", 0, 0, 0,
								"", false);
					}
					if (submitCheck(request, "nextsubmit")) {
						return showMessage(request, response, "update_on_successful_individuals",
								"cp.jsp?ac=profile&op=info");
					} else {
						return showMessage(request, response, "update_on_successful_individuals",
								"cp.jsp?ac=profile&op=work");
					}
				}
				List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM " + tname
						+ " WHERE uid='" + uid
						+ "' AND type='work' ORDER BY startyear,startmonth,endyear,endmonth");
				for (Map<String, Object> value : list) {
					value.put("title_s", Common.urlEncode((String) value.get("title")));
					value.put("friend", String.valueOf(value.get("friend")));
				}
				request.setAttribute("list", list);

			} else if (op.equals("info")) {
				if (submitCheck(request, "profilesubmit")) {
					
					Pattern p = Pattern.compile("info\\[(.+)\\]");
					List inserts = new ArrayList();
					for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
						String elementName = (String) e.nextElement();
						if (elementName.startsWith("info[")) {
							String key = p.matcher(elementName).replaceAll("$1");
							String value = Common.getStr(request.getParameter(elementName), 500, true, true,
									false, 0, 0, request, response);
							String friend = request.getParameter("info_friend[" + key + "]");
							inserts.add("('" + uid + "','info','" + key + "','" + value + "','" + friend
									+ "')");
						}
					}
					if (inserts.isEmpty() == false) {
						dataBaseService.executeUpdate("DELETE FROM " + tname + " WHERE uid='" + uid
								+ "' AND type='info'");
						dataBaseService.executeUpdate("INSERT INTO " + tname
								+ " (uid,type,subtype,title,friend) VALUES " + Common.implode(inserts, ","));
					}
					if (!Common.empty(sConfig.get("my_status"))) {
						Map insert = new HashMap();
						insert.put("uid", sGlobal.get("supe_uid"));
						insert.put("action", "update");
						insert.put("dateline", sGlobal.get("timestamp"));
						insert.put("type", 2);
						dataBaseService.insertTable("userlog", insert, false, true);
					}
					if (Common.ckPrivacy(sGlobal, sConfig, space, "profile", 1)) {
						cpService.addFeed(sGlobal, "profile", Common.getMessage(request,
								"cp_feed_profile_update_info"), null, "", null, "", null, null, "", 0, 0, 0,
								"", false);
					}
					return showMessage(request, response, "update_on_successful_individuals",
							"cp.jsp?ac=profile&op=info");
				}
				Map infoarr = new LinkedHashMap();
				infoarr.put("trainwith", "我想结交");
				infoarr.put("interest", "兴趣爱好");
				infoarr.put("book", "喜欢的书籍");
				infoarr.put("movie", "喜欢的电影");
				infoarr.put("tv", "喜欢的电视");
				infoarr.put("music", "喜欢的音乐");
				infoarr.put("game", "喜欢的游戏");
				infoarr.put("sport", "喜欢的运动");
				infoarr.put("idol", "偶像");
				infoarr.put("motto", "座右铭");
				infoarr.put("wish", "最近心愿");
				infoarr.put("intro", "我的简介");

				Map list = new HashMap();
				Map friends = new HashMap();
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM " + tname
						+ " WHERE uid='" + uid + "' AND type='info'");
				for (Map<String, Object> value : query) {
					list.put(value.get("subtype"), value);
					Map map = new HashMap();
					map.put(String.valueOf(value.get("friend")), " selected");
					friends.put(value.get("subtype"), map);
				}
				request.setAttribute("list", list);
				request.setAttribute("friends", friends);
				request.setAttribute("infoarr", infoarr);
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}

		if (op.equals("edu") || op.equals("work")) {
			StringBuffer yearHtml = new StringBuffer();
			int nowYear = Common.intval(Common.sgmdate(request, "yyyy", (Integer) sGlobal.get("timestamp")));
			for (int i = 0; i < 50; i++) {
				int they = nowYear - i;
				yearHtml.append("<option value=\"" + they + "\">" + they + "</option>");
			}
			StringBuffer monthHtml = new StringBuffer();
			for (int i = 1; i < 13; i++) {
				monthHtml.append("<option value=\"" + i + "\">" + i + "</option>");
			}
			request.setAttribute("yearhtml", yearHtml);
			request.setAttribute("monthhtml", monthHtml);
		}
		String theUrl = "cp.jsp?ac=profile&op=" + op;
		Map farr = new HashMap();
		farr.put("0", "全用户");
		farr.put("1", "仅好友");
		farr.put("3", "仅自己");

		request.setAttribute("cat_actives_" + op, " class=\"active\"");
		request.setAttribute("farr", farr);
		request.setAttribute("theurl", theUrl);
		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "cp_profile.jsp");
	}

	
	public ActionForward cp_relatekw(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {

		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		if (Common.empty(sConfig.get("headercharset"))) {
			Map<String, String> jchConf = JavaCenterHome.jchConfig;
			
			response.setContentType("text/html; charset=" + jchConf.get("charset"));
		}
		sGlobal.put("inajax", 1);

		String subjectEncode = Common.urlDecode(Common.stripTags(request.getParameter("subjectenc")));
		try {
			List<String> keywords = getKeyWord(subjectEncode);
			String result = Common.implode(keywords, " ");
			return showMessage(request, response, result.trim());
		} catch (Exception e) {
			return showMessage(request, response, " ");
		}

	}

	
	public ActionForward cp_sendmail(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (Common.empty(sConfig.get("sendmailday"))) {
			return showMessage(request, response, "no_privilege");
		}
		try {
			if (submitCheck(request, "setsendemailsubmit")) {
				Map<String, String> sendMail = new HashMap<String, String>();
				Map<String, String[]> sendMails = request.getParameterMap();
				Set<String> keys = sendMails.keySet();
				String var = null, value = null;
				for (String key : keys) {
					if (key.startsWith("sendmail_")) {
						var = key.substring(key.indexOf("_") + 1);
						value = sendMails.get(key)[0].trim();
						sendMail.put(var, value);
					}
				}
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("sendmail", Common.addSlashes(Serializer.serialize(sendMail)));
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("uid", space.get("uid"));
				dataBaseService.updateTable("spacefield", setData, whereData);
				return showMessage(request, response, "do_success", "cp.jsp?ac=sendmail");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		if (Common.empty(space.get("email"))) {
			return showMessage(request, response, "email_input");
		}
		Map<String, String> sendMail = Serializer.unserialize((String) space.get("sendmail"), false);
		if (Common.empty(sendMail)) {
			request.setAttribute("checked", " checked");
			request.setAttribute("selected", " selected");
		} else {
			Map<String, String> pitchOn = new HashMap<String, String>();
			Set<String> keys = sendMail.keySet();
			for (String key : keys) {
				String value = sendMail.get(key);
				if ("frequency".equals(key)) {
					pitchOn.put("frequency_" + value, " selected");
				} else {
					pitchOn.put(key, "0".equals(value) ? "" : " checked");
				}
			}
			request.setAttribute("pitchOn", pitchOn);
		}
		return include(request, response, sConfig, sGlobal, "cp_sendmail.jsp");
	}

	public ActionForward cp_share(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");

		String tempS = request.getParameter("sid");
		int sid = Common.intval(tempS);
		String op = request.getParameter("op");
		int supe_uid = (Integer) sGlobal.get("supe_uid");

		

		if ("delete".equals(op)) {
			
			boolean sc = false;
			try {
				sc = submitCheck(request, "deletesubmit");
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			String type = request.getParameter("type");
			if (sc) {
				adminDeleteService.deleteShares(request, response, supe_uid, new Integer[] {sid});
				return showMessage(request, response, "do_success",
						"view".equals(type) ? "space.jsp?do=share" : request.getParameter("refer"), 0);
			}
			request.setAttribute("sid", sid);
			request.setAttribute("type", type);
		} else if ("edithot".equals(op)) {
			
			if (!Common.checkPerm(request, response, "manageshare")) {
				return showMessage(request, response, "no_privilege");
			}

			List<Map<String, Object>> query;
			Map<String, Object> share = null;
			if (sid != 0) {
				query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("share")
						+ " WHERE sid='" + sid + "'");
				share = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(share)) {
					return showMessage(request, response, "no_privilege");
				}
			}
			try {
				if (submitCheck(request, "hotsubmit")) {
					tempS = request.getParameter("hot");
					int hot = Common.intval(tempS);
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("hot", hot);
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("sid", sid);
					dataBaseService.updateTable("share", setData, whereData);
					if (hot > 0) {
						feedService.feedPublish(request, response, sid, "sid", false);
					} else {
						whereData.clear();
						whereData.put("id", sid);
						whereData.put("idtype", "sid");
						dataBaseService.updateTable("feed", setData, whereData);
					}
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("sid", sid);
			request.setAttribute("share", share);
		} else {
			
			if (!Common.checkPerm(request, response, "allowshare")) {
				MessageVO msgVO = Common.ckSpaceLog(request);
				if (msgVO != null) {
					return showMessage(request, response, msgVO);
				}
				return showMessage(request, response, "no_privilege");
			}
			if (!cpService.checkRealName(request, "share")) {
				return showMessage(request, response, "no_privilege_realname");
			}
			if (!cpService.checkVideoPhoto(request, response, "share")) {
				return showMessage(request, response, "no_privilege_videophoto");
			}
			int result = cpService.checkNewUser(request, response);
			switch (result) {
				case 1:
					break;
				case 2:
					return showMessage(request, response, "no_privilege_newusertime", "", 1, String
							.valueOf(sConfig.get("newusertime")));
				case 3:
					return showMessage(request, response, "no_privilege_avatar");
				case 4:
					return showMessage(request, response, "no_privilege_friendnum", "", 1, String
							.valueOf(sConfig.get("need_friendnum")));
				case 5:
					return showMessage(request, response, "no_privilege_email");
			}

			tempS = request.getParameter("type");
			String type = Common.empty(tempS) ? "" : tempS;
			tempS = request.getParameter("id");
			int id = Common.empty(tempS) ? 0 : Common.intval(tempS);
			int note_uid = 0;
			String note_message = "";

			Object[] hotarr;
			Map<String, Object> arr = new HashMap<String, Object>();
			PHPSerializer serializer = new PHPSerializer(JavaCenterHome.JCH_CHARSET);
			if ("space".equals(type)) {
				if (id == (Integer) space.get("uid")) {
					return showMessage(request, response, "share_space_not_self");
				}

				Map<String, Object> tospace = Common.getSpace(request, sGlobal, sConfig, id);
				if (Common.empty(tospace)) {
					return showMessage(request, response, "space_does_not_exist");
				}
				if (cpService.isBlackList((Integer) tospace.get("uid"), supe_uid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}

				arr.put("title_template", Common.getMessage(request, "cp_share_space"));
				arr.put("body_template", "<b>{username}</b><br>{reside}<br>{spacenote}");
				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("username", "<a href=\"space.jsp?uid=" + id + "\">"
						+ sNames.get((Integer) tospace.get("uid")) + "</a>");
				body_data.put("reside", (String) tospace.get("resideprovince")
						+ (String) tospace.get("residecity"));
				body_data.put("spacenote", (String) tospace.get("spacenote"));
				arr.put("body_data", body_data);
				body_data = null;
				arr.put("image", cpService.ckavatar(sGlobal, sConfig, id) ? Common.avatar(id, "middle", true, sGlobal, sConfig): "data/avatar/noavatar_middle.gif");
				arr.put("image_link", "space.jsp?uid=" + id);
				note_uid = id;
				note_message = Common.getMessage(request, "cp_note_share_space");
			} else if ("blog".equals(type)) {
				List<Map<String, Object>> query = dataBaseService
						.executeQuery("SELECT b.*,bf.message,bf.hotuser FROM "
								+ JavaCenterHome.getTableName("blog") + " b " + "LEFT JOIN "
								+ JavaCenterHome.getTableName("blogfield") + " bf ON bf.blogid=b.blogid "
								+ "WHERE b.blogid='" + id + "'");
				Map<String, Object> blog = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(blog)) {
					return showMessage(request, response, "blog_does_not_exist");
				}
				int blogUid = (Integer) blog.get("uid");
				if (blogUid == (Integer) space.get("uid")) {
					return showMessage(request, response, "share_not_self");
				}
				if ((Integer) blog.get("friend") != 0) {
					return showMessage(request, response, "logs_can_not_share");
				}
				if (cpService.isBlackList(blogUid, supe_uid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				Common.realname_set(sGlobal, sConfig, sNames, blogUid, (String) blog.get("username"), "", 0);
				Common.realname_get(sGlobal, sConfig, sNames, space);

				int blogid = (Integer) blog.get("blogid");
				arr.put("title_template", Common.getMessage(request, "cp_share_blog"));
				arr.put("body_template", "<b>{subject}</b><br>{username}<br>{message}");
				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("subject", "<a href=\"space.jsp?uid=" + blogUid + "&do=blog&id=" + blogid
						+ "\">" + blog.get("subject") + "</a>");
				body_data.put("username", "<a href=\"space.jsp?uid=" + blogUid + "\">" + sNames.get(blogUid)
						+ "</a>");
				try {
					tempS = Common.getStr((String) blog.get("message"), 150, false, true, false, 0, -1,
							request, response);
				} catch (Exception e) {
					e.printStackTrace();
					return showMessage(request, response, e.getMessage());
				}
				body_data.put("message", tempS);
				arr.put("body_data", body_data);
				if (!Common.empty(blog.get("pic"))) {
					arr.put("image", Common.pic_cover_get(sConfig, (String) blog.get("pic"), (Integer) blog
							.get("picflag")));
					arr.put("image_link", "space.jsp?uid=" + blogUid + "&do=blog&id=" + blogid);
				}
				note_uid = blogUid;
				note_message = Common.getMessage(request, "cp_note_share_blog", "space.jsp?uid=" + blogUid
						+ "&do=blog&id=" + blogid, (String) blog.get("subject"));
				hotarr = new Object[] {"blogid", blogid, blog.get("hotuser")};
			} else if ("album".equals(type)) {
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("album") + " WHERE albumid='" + id + "'");
				Map<String, Object> album = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(album)) {
					return showMessage(request, response, "album_does_not_exist");
				}
				int albumUid = (Integer) album.get("uid");
				if (albumUid == (Integer) space.get("uid")) {
					return showMessage(request, response, "share_not_self");
				}
				if ((Integer) album.get("friend") != 0) {
					return showMessage(request, response, "album_can_not_share");
				}
				if (cpService.isBlackList(albumUid, supe_uid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				Common
						.realname_set(sGlobal, sConfig, sNames, albumUid, (String) album.get("username"), "",
								0);
				Common.realname_get(sGlobal, sConfig, sNames, space);

				arr.put("title_template", Common.getMessage(request, "cp_share_album"));
				arr.put("body_template", "<b>{albumname}</b><br>{username}");

				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("albumname", "<a href=\"space.jsp?uid=" + albumUid + "&do=album&id="
						+ album.get("albumid") + "\">" + album.get("albumname") + "</a>");
				body_data.put("username", "<a href=\"space.jsp?uid=" + albumUid + "\">"
						+ sNames.get(albumUid) + "</a>");

				arr.put("body_data", body_data);
				arr.put("image", Common.pic_cover_get(sConfig, (String) album.get("pic"), (Integer) album
						.get("picflag")));
				arr.put("image_link", "space.jsp?uid=" + albumUid + "&do=album&id=" + album.get("albumid"));
				note_uid = albumUid;
				note_message = Common.getMessage(request, "cp_note_share_album", "space.jsp?uid=" + albumUid
						+ "&do=album&id=" + album.get("albumid"), (String) album.get("albumname"));
			} else if ("pic".equals(type)) {
				List<Map<String, Object>> query = dataBaseService
						.executeQuery("SELECT album.albumid, album.albumname, album.friend, pic.*, pf.* "
								+ "FROM " + JavaCenterHome.getTableName("pic") + " pic " + "LEFT JOIN "
								+ JavaCenterHome.getTableName("picfield") + " pf ON pf.picid=pic.picid "
								+ "LEFT JOIN " + JavaCenterHome.getTableName("album")
								+ " album ON album.albumid=pic.albumid " + "WHERE pic.picid='" + id + "'");

				Map<String, Object> pic = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(pic)) {
					return showMessage(request, response, "image_does_not_exist");
				}
				int picUid = (Integer) pic.get("uid");
				if (picUid == (Integer) space.get("uid")) {
					return showMessage(request, response, "share_not_self");
				}
				if ((Integer) pic.get("friend") != 0) {
					return showMessage(request, response, "image_can_not_share");
				}
				if (cpService.isBlackList(picUid, supe_uid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				if (Common.empty(pic.get("albumid")))
					pic.put("albumid", 0);
				if (Common.empty(pic.get("albumname")))
					pic.put("albumname", Common.getMessage(request, "cp_default_albumname"));
				Common.realname_set(sGlobal, sConfig, sNames, picUid, (String) pic.get("username"), "", 0);
				Common.realname_get(sGlobal, sConfig, sNames, space);

				int picid = (Integer) pic.get("picid");
				arr.put("title_template", Common.getMessage(request, "cp_share_image"));
				arr.put("body_template", Common.getMessage(request, "cp_album")
						+ ": <b>{albumname}</b><br>{username}<br>{title}");

				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("albumname", "<a href=\"space.jsp?uid=" + picUid + "&do=album&id="
						+ pic.get("albumid") + "\">" + pic.get("albumname") + "</a>");
				body_data.put("username", "<a href=\"space.jsp?uid=" + picUid + "\">" + sNames.get(picUid)
						+ "</a>");
				try {
					tempS = Common.getStr((String) pic.get("title"), 100, false, true, false, 0, -1, request,
							response);
				} catch (Exception e) {
					e.printStackTrace();
					return showMessage(request, response, e.getMessage());
				}
				body_data.put("title", tempS);
				arr.put("body_data", body_data);
				arr.put("image", Common.pic_get(sConfig, (String) pic.get("filepath"), (Integer) pic
						.get("thumb"), (Integer) pic.get("remote"), true));
				arr.put("image_link", "space.jsp?uid=" + picUid + "&do=album&picid=" + picid);
				note_uid = picUid;
				note_message = Common.getMessage(request, "cp_note_share_pic", "space.jsp?uid=" + picUid
						+ "&do=album&picid=" + picid, (String) pic.get("albumname"));

				hotarr = new Object[] {"picid", picid, pic.get("hotuser")};
			} else if ("thread".equals(type)) {
				List<Map<String, Object>> query = dataBaseService
						.executeQuery("SELECT t.*, p.message, p.hotuser FROM "
								+ JavaCenterHome.getTableName("thread") + " t " + "LEFT JOIN "
								+ JavaCenterHome.getTableName("post")
								+ " p ON p.tid=t.tid AND p.isthread='1' " + "WHERE t.tid='" + id + "'");

				Map<String, Object> thread = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(thread)) {
					return showMessage(request, response, "topics_does_not_exist");
				}
				int threadUid = (Integer) thread.get("uid");
				if (threadUid == (Integer) space.get("uid")) {
					return showMessage(request, response, "share_not_self");
				}
				if (cpService.isBlackList(threadUid, supe_uid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				Map globalProfield = Common.getCacheDate(request, response, "/data/cache/cache_profield.jsp",
						"globalProfield");

				query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("mtag")
						+ " WHERE tagid='" + thread.get("tagid") + "'");
				Map<String, Object> mtag = query.size() > 0 ? query.get(0) : new HashMap<String, Object>();
				if (globalProfield == null) {
					tempS = "";
				} else {
					Map tempM = (Map) globalProfield.get(mtag.get("fieldid"));
					if (tempM == null) {
						tempS = "";
					} else {
						tempS = (String) tempM.get("title");
					}
				}
				mtag.put("title", tempS);
				Common.realname_set(sGlobal, sConfig, sNames, threadUid, (String) thread.get("username"), "",
						0);
				Common.realname_get(sGlobal, sConfig, sNames, space);

				int threadTid = (Integer) thread.get("tid");
				arr.put("title_template", Common.getMessage(request, "cp_share_thread"));
				arr.put("body_template", "<b>{subject}</b><br>{username}<br>"
						+ Common.getMessage(request, "cp_mtag") + ": {mtag} ({field})<br>{message}");

				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("subject", "<a href=\"space.jsp?uid=" + threadUid + "&do=thread&id="
						+ threadTid + "\">" + thread.get("subject") + "</a>");
				body_data.put("username", "<a href=\"space.jsp?uid=" + threadUid + "\">"
						+ sNames.get(threadUid) + "</a>");
				body_data.put("mtag", "<a href=\"space.jsp?do=mtag&tagid=" + mtag.get("tagid") + "\">"
						+ mtag.get("tagname") + "</a>");
				body_data.put("field", "<a href=\"space.jsp?do=mtag&id="+mtag.get("fieldid")+"\">"+mtag.get("title")+"</a>");
				try {
					tempS = Common.getStr((String) thread.get("message"), 150, false, true, false, 0, -1,
							request, response);
				} catch (Exception e) {
					e.printStackTrace();
					return showMessage(request, response, e.getMessage());
				}
				body_data.put("message", tempS);
				arr.put("body_data", body_data);

				arr.put("image", "");
				arr.put("image_link", "");
				note_uid = threadUid;
				note_message = Common.getMessage(request, "cp_note_share_thread", "space.jsp?uid="
						+ threadUid + "&do=thread&id=" + threadTid, (String) thread.get("subject"));

				hotarr = new Object[] {"picid", threadTid, thread.get("hotuser")};
			} else if ("mtag".equals(type)) {
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("mtag") + " WHERE tagid='" + id + "'");
				Map<String, Object> mtag = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(mtag)) {
					return showMessage(request, response, "designated_election_it_does_not_exist");
				}

				Map globalProfield = Common.getCacheDate(request, response, "/data/cache/cache_profield.jsp",
						"globalProfield");
				if (globalProfield == null) {
					tempS = "";
				} else {
					Map tempM = (Map) globalProfield.get(mtag.get("fieldid"));
					if (tempM == null) {
						tempS = "";
					} else {
						tempS = (String) tempM.get("title");
					}
				}
				mtag.put("title", tempS);

				arr.put("title_template", Common.getMessage(request, "cp_share_mtag"));
				arr.put("body_template", "<b>{mtag}</b><br>{field}<br>"
						+ Common.getMessage(request, "cp_share_mtag_membernum"));

				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("mtag", "<a href=\"space.jsp?do=mtag&tagid=" + mtag.get("tagid") + "\">"
						+ mtag.get("tagname") + "</a>");
				body_data.put("field", "<a href=\"space.jsp?do=mtag&id=" + mtag.get("fieldid") + "\">"
						+ mtag.get("title") + "</a>");
				body_data.put("membernum", mtag.get("membernum").toString());
				arr.put("body_data", body_data);

				arr.put("image", mtag.get("pic"));
				arr.put("image_link", "space.jsp?do=mtag&tagid=" + mtag.get("tagid"));
			} else if ("tag".equals(type)) {
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("tag") + " WHERE tagid='" + id + "'");
				Map<String, Object> tag = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(tag)) {
					return showMessage(request, response, "tag_does_not_exist");
				}
				arr.put("title_template", Common.getMessage(request, "cp_share_tag"));
				arr.put("body_template", "<b>{tagname}</b><br>"
						+ Common.getMessage(request, "cp_share_tag_blognum"));

				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("tagname", "<a href=\"space.jsp?do=tag&id=" + tag.get("tagid") + "\">"
						+ tag.get("tagname") + "</a>");
				body_data.put("blognum", tag.get("blognum").toString());
				arr.put("body_data", body_data);

				arr.put("image", "");
				arr.put("image_link", "");
			} else if ("event".equals(type)) {
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT e.*, ef.hotuser "
						+ "FROM " + JavaCenterHome.getTableName("event") + " e " + "LEFT JOIN "
						+ JavaCenterHome.getTableName("eventfield") + " ef " + "ON ef.eventid=e.eventid "
						+ "WHERE e.eventid='" + id + "'");

				Map<String, Object> event = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(event)) {
					return showMessage(request, response, "event_does_not_exist");
				}
				int eventUid = (Integer) event.get("uid");
				if (eventUid == (Integer) space.get("uid")) {
					return showMessage(request, response, "share_not_self");
				}
				if (cpService.isBlackList(eventUid, supe_uid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				arr.put("title_template", Common.getMessage(request, "cp_share_event"));
				arr.put("body_template", "<b>{eventname}</b><br>"
						+ Common.getMessage(request, "cp_event_time") + ": {eventtime}<br>"
						+ Common.getMessage(request, "cp_event_location") + ": {eventlocation}<br>"
						+ Common.getMessage(request, "cp_event_creator") + ": {eventcreator}");

				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("eventname", "<a href=\"space.jsp?do=event&id=" + event.get("eventid") + "\">"
						+ event.get("title") + "</a>");
				body_data.put("eventtime", Common.sgmdate(request, "MM-dd HH:mm", (Integer) event
						.get("starttime"))
						+ " - " + Common.sgmdate(request, "MM-dd HH:mm", (Integer) event.get("endtime")));
				body_data.put("eventlocation", event.get("province") + " " + event.get("city") + " "
						+ event.get("location"));
				body_data.put("eventcreator", (String) event.get("username"));
				arr.put("body_data", body_data);

				if (Common.empty(event.get("poster"))) {
					Map globalEventClass = Common.getCacheDate(request, response,
							"/data/cache/cache_eventclass.jsp", "globalEventClass");
					if (globalEventClass == null) {
						tempS = "";
					} else {
						Map tempM = (Map) globalEventClass.get(event.get("classid"));
						if (tempM == null) {
							tempS = "";
						} else {
							tempS = (String) tempM.get("poster");
						}
					}
				} else {
					tempS = JavaCenterHome.jchConfig.get("attachUrl") + event.get("poster");
				}
				arr.put("image", tempS);
				arr.put("image_link", "space.jsp?do=event&id=" + event.get("eventid"));
				hotarr = new Object[] {"eventid", event.get("eventid"), event.get("hotuser")};
			} else if ("poll".equals(type)) {
				List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT p.*,pf.* FROM "
						+ JavaCenterHome.getTableName("poll") + " p " + "LEFT JOIN "
						+ JavaCenterHome.getTableName("pollfield") + " pf ON pf.pid=p.pid " + "WHERE p.pid='"
						+ id + "'");

				Map<String, Object> poll = query.size() > 0 ? query.get(0) : null;
				if (Common.empty(poll)) {
					return showMessage(request, response, "poll_does_not_exist");
				}
				int pollUid = (Integer) poll.get("uid");
				if (pollUid == (Integer) space.get("uid")) {
					return showMessage(request, response, "share_not_self");
				}
				if (cpService.isBlackList(pollUid, supe_uid) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				if (Common.empty(poll.get("albumid")))
					poll.put("albumid", 0);
				if (Common.empty(poll.get("albumname")))
					poll.put("albumname", Common.getMessage(request, "default_albumname"));
				Common.realname_set(sGlobal, sConfig, sNames, pollUid, (String) poll.get("username"), "", 0);
				Common.realname_get(sGlobal, sConfig, sNames, space);

				int pid = (Integer) poll.get("pid");
				arr.put("title_template", Common.getMessage(request, "cp_share_poll", !Common.empty(poll
						.get("percredit")) ? Common.getMessage(request, "cp_reward") : ""));
				arr.put("body_template", "<b>{subject}</b><br>{user}<br>{option}");

				StringBuilder optionstr = new StringBuilder();
				List<String> subList = Serializer.unserialize((String) poll.get("option"));
				poll.put("option", subList);
				String val;
				for (int key = 0; key < subList.size(); key++) {
					val = subList.get(key);
					optionstr.append("<input type=\"");
					if (!Common.empty(poll.get("multiple"))) {
						optionstr.append("checkbox");
					} else {
						optionstr.append("radio");
					}
					optionstr.append("\" disabled name=\"poll_");
					optionstr.append(key);
					optionstr.append("\"/>");
					optionstr.append(val);
					optionstr.append("<br/>");
				}

				Map<String, String> body_data = new HashMap<String, String>();
				body_data.put("user", "<a href=\"space.jsp?uid=" + pollUid + "\">" + sNames.get(pollUid)
						+ "</a>");
				body_data.put("subject", "<a href=\"space.jsp?uid=" + pollUid + "&do=poll&pid=" + pid + "\">"
						+ poll.get("subject") + "</a>");
				body_data.put("option", optionstr.toString());
				arr.put("body_data", body_data);
				note_uid = pollUid;
				note_message = Common.getMessage(request, "cp_note_share_poll", "space.jsp?uid=" + pollUid
						+ "&do=poll&pid=" + pid, (String) poll.get("subject"));
				hotarr = new Object[] {"pid", pid, poll.get("hotuser")};
			} else {
				
				Map<String, Object> topic = null;
				tempS = request.getParameter("topicid");
				int topicid = Common.intval(tempS);
				if (topicid != 0) {
					topic = Common.getTopic(request, topicid);
				}
				if (!Common.empty(topic)) {
					Map<String, String> actives = new HashMap<String, String>();
					actives.put("share", " class=\"active\"");
				}

				sGlobal.put("refer", "space.jsp?do=share&view=me");
				type = "link";
				op = "link";
				request.setAttribute("topic", topic);
				request.setAttribute("topicid", topicid);
			}
			try {
				if (submitCheck(request, "sharesubmit")) {
					int topicid = Common.intval(request.getParameter("topicid"));
					topicid = cpService.checkTopic(request, topicid, "share");
					if ("link".equals(type)
							&& Common.checkPerm(request, response, "seccode")
							&& !cpService.checkSeccode(request, response, sGlobal, sConfig, request
									.getParameter("seccode"))) {
						return showMessage(request, response, "incorrect_code");
					}

					String refer = request.getParameter("refer");
					if (Common.empty(refer)) {
						refer = "space.jsp?do=share&view=me";
					}

					if ("link".equals(type)) {
						String link = request.getParameter("link");
						link = link != null ? link : "";
						link = (String) Common.sHtmlSpecialChars(link.trim());
						if (!Common.empty(link)) {
							if (!link.matches("(?i)^(http|ftp|https|mms)://.{4,300}$")) {
								link = "";
							}
						}
						if (Common.empty(link)) {
							return showMessage(request, response, "url_incorrect_format");
						}
						arr.put("title_template", Common.getMessage(request, "cp_share_link"));
						arr.put("body_template", "{link}");

						String link_text;
						try {
							link_text = Common.sub_url(link, 45);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							return showMessage(request, response, e.getMessage());
						}

						Map<String, String> body_data = new HashMap<String, String>();
						body_data.put("link", "<a href=\"" + link + "\" target=\"_blank\">" + link_text
								+ "</a>");
						body_data.put("data", link);
						arr.put("body_data", body_data);
						Map parseLink = cpService.parseUrl(link);
						Pattern pattern = Pattern
								.compile("(?i)(youku.com|youtube.com|5show.com|ku6.com|sohu.com|mofile.com|sina.com.cn)");
						Matcher matcher = pattern.matcher((String) parseLink.get("host"));
						if (matcher.find()) {
							String hosts_1 = matcher.group(1);
							String flashvar = getflash(link, hosts_1);
							if (!Common.empty(flashvar)) {
								arr.put("title_template", Common.getMessage(request, "cp_share_video"));
								type = "video";
								body_data.put("flashvar", flashvar);
								body_data.put("host", hosts_1);
							}
						}
						pattern = Pattern.compile("(?i)\\.(mp3|wma)$");
						matcher = pattern.matcher(link);
						if (matcher.find()) {
							arr.put("title_template", Common.getMessage(request, "cp_share_music"));
							body_data.put("musicvar", link);
							type = "music";
						}
						pattern = Pattern.compile("(?i)\\.swf$");
						matcher = pattern.matcher(link);
						if (matcher.find()) {
							arr.put("title_template", Common.getMessage(request, "cp_share_flash"));
							body_data.put("flashaddr", link);
							type = "flash";
						}
					}

					try {
						arr.put("body_general", Common.getStr(request.getParameter("general"), 150, true,
								true, true, 1, 0, request, response));
					} catch (Exception e) {
						e.printStackTrace();
						return showMessage(request, response, e.getMessage());
					}
					arr.put("type", type);
					arr.put("uid", supe_uid);
					arr.put("username", sGlobal.get("supe_username"));
					arr.put("dateline", sGlobal.get("timestamp"));
					arr.put("topicid", topicid);
					arr.put("body_data", Serializer.serialize(arr.get("body_data")));
					Map<String, Object> setarr = (Map<String, Object>) Common.sAddSlashes(arr);
					if (setarr.get("hotuser") == null) {
						setarr.put("hotuser", "");
					}
					if (setarr.get("title_template") == null) {
						setarr.put("title_template", "");
					}
					sid = dataBaseService.insertTable("share", setarr, true, false);
					cpService.updateStat(request, "share", false);
					if (note_uid != 0 && note_uid != supe_uid) {
						cpService.addNotification(request, sGlobal, sConfig, note_uid, "sharenotice",
								note_message, false);
					}
					String sharenumsql;
					if (Common.empty(space.get("sharenum"))) {
						Map<String, Object> whereArr = new HashMap<String, Object>();
						whereArr.put("uid", space.get("uid"));
						tempS = Common.getCount("share", whereArr, null);
						space.put("sharenum", tempS);
						sharenumsql = "sharenum=" + tempS;
					} else {
						sharenumsql = "sharenum=sharenum+1";
					}
					String needle = id != 0 ? type + id : "";
					Map<String, Integer> reward = Common.getReward("createshare", false, 0, needle, true,
							request, response);
					int timestamp = (Integer) sGlobal.get("timestamp");
					Integer credit = reward.get("credit");
					if (credit == null) {
						credit = 0;
						reward.put("credit", credit);
					}
					Integer experience = reward.get("experience");
					if (experience == null) {
						experience = 0;
						reward.put("experience", experience);
					}
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
							+ sharenumsql + ", lastpost='" + timestamp + "', updatetime='" + timestamp
							+ "', credit=credit+" + credit + ", experience=experience+" + experience
							+ " WHERE uid='" + supe_uid + "'");
					if (Common.ckPrivacy(sGlobal, sConfig, space, "share", 1)) {
						feedService.feedPublish(request, response, sid, "sid", true);
					}

					String url;
					if (topicid != 0) {
						cpService
								.topicJoin(request, topicid, supe_uid, (String) sGlobal.get("supe_username"));
						url = "space.jsp?do=topic&topicid=" + topicid + "&view=share";
					} else {
						url = refer;
					}
					return showMessage(request, response, "do_success", url, 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return showMessage(request, response, e.getMessage());
			}
			arr.put("body_data", Serializer.serialize(arr.get("body_data")));
			Common.mkShare(arr);
			Common.realname_get(sGlobal, sConfig, sNames, space);
			request.setAttribute("id", id);
			request.setAttribute("type", type);
			request.setAttribute("share", arr);
		}

		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "cp_share.jsp");
	}

	private String getflash(String link, String host) {
		String returnString = "";
		if ("youku.com".equals(host)) {
			String regex = "id\\_(\\w+)[=.]";
			String matcher = getMatcherString(regex, link);
			if (!Common.empty(matcher)) {
				returnString = matcher;
			}
		} else if ("ku6.com".equals(host)) {
			String regex = "/index_([\\w\\-]+)\\.html";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher2 = pattern.matcher(link);
			if (!matcher2.find()) {
				regex = "/([\\w\\-]+)\\.html";
				String matcher = getMatcherString(regex, link);
				if (!Common.empty(matcher)) {
					returnString = matcher;
				}
			}
		} else if ("youtube.com".equals(host)) {
			String regex = "v=([\\w\\-]+)";
			String matcher = getMatcherString(regex, link);
			if (!Common.empty(matcher)) {
				returnString = matcher;
			}
		} else if ("5show.com".equals(host)) {
			String regex = "/(\\d+)\\.shtml";
			String matcher = getMatcherString(regex, link);
			if (!Common.empty(matcher)) {
				returnString = matcher;
			}
		} else if ("mofile.com".equals(host)) {
			String regex = "/(\\w+)/*$";
			String matcher = getMatcherString(regex, link);
			if (!Common.empty(matcher)) {
				returnString = matcher;
			}
		} else if ("sina.com.cn".equals(host)) {
			String regex = "/(\\d+)-(\\d+)\\.html";
			String matcher = getMatcherString(regex, link);
			if (!Common.empty(matcher)) {
				returnString = matcher;
			}
		} else if ("sohu.com".equals(host)) {
			String regex = "/(\\d+)/*$";
			String matcher = getMatcherString(regex, link);
			if (!Common.empty(matcher)) {
				returnString = matcher;
			}
		}
		return returnString;
	}

	
	private String getMatcherString(String regex, String input) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public ActionForward cp_space(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		return include(request, response, sConfig, sGlobal, "cp_space.jsp");
	}

	public ActionForward cp_task(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");

		int timestamp = (Integer) sGlobal.get("timestamp");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, supe_uid);

		Map<Integer, Map<String, Object>> globalTask = Common.getCacheDate(request, response,
				"/data/cache/cache_task.jsp", "globalTask");

		String taskidS = request.getParameter("taskid");
		taskidS = taskidS != null ? taskidS.trim() : "";
		int taskid = Common.empty(taskidS) ? 0 : Common.intval(taskidS);
		String view = request.getParameter("view");
		view = view != null ? view.trim() : "";

		
		Map<String, String> actives = new HashMap<String, String>();
		if (taskid != 0) {
			
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("task") + " WHERE taskid='" + taskid + "'");
			Map<String, Object> task = query.size() > 0 ? query.get(0) : null;
			if (task == null || (Integer) task.get("starttime") > timestamp) {
				return showMessage(request, response, "task_unavailable");
			} else {
				String tempImage = (String) task.get("image");
				tempImage = tempImage == null || Common.empty((tempImage = tempImage.trim())) ? "image/task.gif"
						: tempImage;
				task.put("image", tempImage);
			}

			if ("member".equals(view)) {

				
				int perpage = 20;
				String tempS = request.getParameter("page");
				int page = Common.empty(tempS) ? 1 : Common.intval(tempS);
				page = Math.max(page, 1);
				int start = (page - 1) * perpage;
				List<Map<String, Object>> list = null;
				int maxPage = (Integer) sConfig.get("maxpage");
				tempS = Common.ckStart(start, perpage, maxPage);
				if (tempS != null) {
					return showMessage(request, response, tempS);
				}

				String theurl = "cp.jsp?ac=task&taskid=" + taskid + "&view=" + view;

				query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "
						+ JavaCenterHome.getTableName("usertask") + " main WHERE main.taskid='" + taskid
						+ "' AND main.isignore='0'");
				int count = query.size() > 0 ? (Integer) query.get(0).get("cont") : 0;
				if (count != 0) {
					tempS = "SELECT s.*, sf.sex, main.dateline " + "FROM "
							+ JavaCenterHome.getTableName("usertask") + " main " + "LEFT JOIN "
							+ JavaCenterHome.getTableName("space") + " s ON s.uid=main.uid LEFT JOIN "
							+ JavaCenterHome.getTableName("spacefield") + " sf ON sf.uid=s.uid  "
							+ "WHERE main.taskid='" + taskid + "' AND main.isignore='0' "
							+ "ORDER BY main.dateline DESC " + "LIMIT " + start + "," + perpage;
					query = dataBaseService.executeQuery(tempS);
					int valueUid;
					int spaceUid = (Integer) space.get("uid");
					String[] friends = (String[]) space.get("friends");
					boolean tempB = friends != null && friends.length > 0;
					Integer namestatus;
					for (Map<String, Object> value : query) {
						namestatus = (namestatus = (Integer) value.get("namestatus")) == null ? 0
								: namestatus;
						valueUid = (Integer) value.get("uid");
						Common.realname_set(sGlobal, sConfig, sNames, valueUid, (String) value
								.get("username"), (String) value.get("name"), (Integer) value
								.get("namestatus"));
						value.put("isfriend", valueUid == spaceUid
								|| (tempB && Common.in_array(friends, valueUid)));
						value.put("gColor", Common
								.getColor(request, response, (Integer) value.get("groupid")));
						value.put("gIcon", Common.getIcon(request, response, (Integer) value.get("groupid")));
					}
					list = query;
				}
				String multi = Common.multi(request, count, perpage, page, maxPage, theurl, null, null);
				request.setAttribute("multi", multi);
				request.setAttribute("list", list);
			} else {
				boolean done = false;
				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("usertask") + " WHERE uid='" + supe_uid
						+ "' AND taskid='" + taskid + "'");
				Map<String, Object> usertask = query.size() > 0 ? query.get(0) : null;
				if (!Common.empty(usertask)) {
					Integer maxnum = (Integer) task.get("maxnum");
					if (maxnum != null && maxnum != 0 && maxnum <= (Integer) task.get("num")) {
						task.put("done", 1);
						done = true;
					} else {
						int allownext = 0;
						int lasttime = (Integer) usertask.get("dateline");
						String nexttype = (String) task.get("nexttype");
						if ("day".equals(nexttype)) {
							if (!Common.sgmdate(request, "yyyyMMdd", timestamp).equals(
									Common.sgmdate(request, "yyyyMMdd", lasttime))) {
								allownext = 1;
							}
						} else if ("hour".equals(nexttype)) {
							if (!Common.sgmdate(request, "yyyyMMddHH", timestamp).equals(
									Common.sgmdate(request, "yyyyMMddHH", lasttime))) {
								allownext = 1;
							}
						} else if ((Integer) task.get("nexttime") != 0) {
							if (timestamp - lasttime >= (Integer) task.get("nexttime")) {
								allownext = 1;
							}
						}

						if (allownext != 0) {
							task.put("done", 0);
						} else {
							task.put("done", 1);
							done = true;
						}
					}
					task.put("dateline", usertask.get("dateline"));
					task.put("ignore", done ? usertask.get("isignore") : 0);
				}
				String op = request.getParameter("op");
				op = op != null ? op.trim() : "";
				if (done && (Integer) task.get("ignore") != 0 && "redo".equals(op)) {
					dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("usertask")
							+ " WHERE uid='" + supe_uid + "' AND taskid='" + taskid + "'");
					return showMessage(request, response, "do_success", "cp.jsp?ac=task&taskid=" + taskid, 0);
				}

				sGlobal.put("task_maxnum", 0);
				sGlobal.put("task_available", 0);
				if (!done) {
					Integer maxnumInteger = (Integer) task.get("maxnum");
					int maxnum = maxnumInteger != null ? maxnumInteger : 0;
					task.put("maxnum", maxnum);
					if (maxnum != 0 && maxnum <= (Integer) task.get("num")) {
						task.put("done", 1);
						sGlobal.put("task_maxnum", 1);
						done = true;
					} else if (Common.empty(task.get("available"))) {
						task.put("done", 1);
						sGlobal.put("task_available", 1);
						done = true;
					}
					if (done && !Common.empty(globalTask.get((Integer) task.get("taskid")))) {
						try {
							cacheService.task_cache();
						} catch (Exception e) {
							e.printStackTrace();
							return showMessage(request, response, e.getMessage());
						}
					}
				}
				if (!done) {
					task.put("result", "");
					task.put("guide", "");
					Map<String, Object> setarr = new HashMap<String, Object>();
					setarr.put("uid", supe_uid);
					setarr.put("username", sGlobal.get("supe_username"));
					setarr.put("taskid", task.get("taskid"));
					setarr.put("dateline", timestamp);
					setarr.put("credit", task.get("credit"));

					if ("ignore".equals(op)) {
						setarr.put("isignore", 1);
						dataBaseService.insertTable("usertask", setarr, false, true);
						return showMessage(request, response, "do_success",
								"cp.jsp?ac=task&taskid=" + taskid, 0);
					}
					ActionForward actionForward = executeTask(request, response, task, space);
					if (actionForward != null) {
						return actionForward;
					}

					Integer doneItg = (Integer) task.get("done");
					if (doneItg != null && doneItg != 0) {

						task.put("dateline", timestamp);
						dataBaseService.insertTable("usertask", setarr, false, true);
						dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("task")
								+ " SET num=num+1 WHERE taskid='" + task.get("taskid") + "'");
						int tempI = (Integer) task.get("credit");
						if (tempI != 0) {
							dataBaseService.execute("UPDATE " + JavaCenterHome.getTableName("space")
									+ " SET credit=credit+" + tempI + " WHERE uid='" + supe_uid + "'");
							space.put("credit", (Integer) space.get("credit") + tempI);
						}
						if (Common.ckPrivacy(sGlobal, sConfig, space, "task", 1)) {
							Map<String, Object> fs = new HashMap<String, Object>();
							Map<String, Object> subMap = new HashMap<String, Object>();
							subMap.put("task", "<a href=\"cp.jsp?ac=task&taskid=" + task.get("taskid")
									+ "\">" + task.get("name") + "</a>");
							subMap.put("credit", tempI);
							fs.put("title_template", tempI != 0 ? Common.getMessage(request,
									"cp_feed_task_credit") : Common.getMessage(request, "cp_feed_task"));
							fs.put("title_data", subMap);

							cpService.addFeed(sGlobal, "task", (String) fs.get("title_template"),
									(Map<String, Object>) fs.get("title_data"), "", null, "", null, null, "",
									0, 0, 0, "", false);
						}
						tempI = (Integer) task.get("maxnum");
						if (tempI != 0 && tempI <= ((Integer) task.get("num")) + 1) {
							try {
								cacheService.task_cache();
							} catch (Exception e) {
								e.printStackTrace();
								return showMessage(request, response, e.getMessage());
							}
						}
					}
				} else {
					ActionForward actionForward = executeTask(request, response, task, space);
					if (actionForward != null) {
						return actionForward;
					}
				}
				query = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("usertask") + " WHERE taskid='" + taskid
						+ "' AND isignore='0' ORDER BY dateline DESC LIMIT 0,15");
				for (Map<String, Object> value : query) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) value.get("uid"), (String) value
							.get("username"), "", 0);
				}
				Common.realname_get(sGlobal, sConfig, sNames, space);
				request.setAttribute("taskspacelist", query);
			}

			request.setAttribute("task", task);
			actives.put("do", " class=\"active\"");
		} else {

			
			int done_per = 0, todo_num = 0, all_num = 0;
			Map<Integer, Map<String, Object>> usertasks = new HashMap<Integer, Map<String, Object>>();
			Map<Integer, Integer> taskids = new HashMap<Integer, Integer>();
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("usertask") + " WHERE uid='" + supe_uid + "'");
			Integer taskid_temp;
			for (Map<String, Object> value : query) {
				taskid_temp = (Integer) value.get("taskid");
				usertasks.put(taskid_temp, value);
				taskids.put(taskid_temp, taskid_temp);
			}
			List<Map<String, Object>> tasklist = null;
			if ("done".equals(view)) {
				if (taskids.size() > 0) {
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("task") + " WHERE taskid IN ("
							+ Common.sImplode(taskids) + ") ORDER BY displayorder");
					if (query.size() > 0) {
						String tempS;
						Map<String, Object> tempM;
						for (Map<String, Object> value : query) {
							tempS = (String) value.get("image");
							if (Common.empty(tempS)) {
								value.put("image", "image/task.gif");
							}
							value.put("done", 1);
							taskid_temp = (Integer) value.get("taskid");
							tempM = usertasks.get(taskid_temp);
							value.put("ignore", tempM == null ? 0 : tempM.get("isignore"));
						}
						tasklist = query;
					}
				}
			} else {
				query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("task")
						+ " WHERE available='1' ORDER BY displayorder");
				int allownext = 0;
				int lasttime = 0;
				String nexttype;
				Integer nexttime;
				Map<String, Object> tempM;
				for (Map<String, Object> value : query) {
					if ((Common.empty(value.get("maxnum")) || (Integer) value.get("maxnum") > (Integer) value
							.get("num"))
							&& (Common.empty(value.get("starttime")) || (Integer) value.get("starttime") <= timestamp)
							&& (Common.empty(value.get("endtime")) || (Integer) value.get("endtime") >= timestamp)) {
						lasttime = 0;
						allownext = 0;
						taskid_temp = (Integer) value.get("taskid");

						all_num++;

						tempM = usertasks.get(taskid_temp);
						if (tempM != null) {
							lasttime = (Integer) tempM.get("dateline");
						}
						nexttype = (String) value.get("nexttype");
						nexttime = (Integer) value.get("nexttime");

						if (Common.empty(lasttime)) {
							allownext = 1;
						} else if ("day".equals(nexttype)) {
							if (!Common.sgmdate(request, "yyyyMMdd", timestamp).equals(
									Common.sgmdate(request, "yyyyMMdd", lasttime))) {
								allownext = 1;
							}
						} else if ("hour".equals(nexttype)) {
							if (!Common.sgmdate(request, "yyyyMMddHH", timestamp).equals(
									Common.sgmdate(request, "yyyyMMddHH", lasttime))) {
								allownext = 1;
							}
						} else if (nexttime != null && nexttime != 0) {
							if (timestamp - lasttime >= nexttime) {
								allownext = 1;
							}
						}

						if (allownext != 0) {
							todo_num++;
							if (Common.empty(value.get("image"))) {
								value.put("value", "image/task.gif");
							}
							value.put("done", 0);
							if (tasklist == null) {
								tasklist = new ArrayList<Map<String, Object>>();
							}
							tasklist.add(value);
						}
					}
				}
				done_per = Common.empty(all_num) ? 100 : (all_num - todo_num) * 100 / all_num;
			}
			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("usertask")
					+ " WHERE isignore='0' ORDER BY dateline DESC LIMIT 0,20");
			int tempUid;
			String taskname;
			Map<String, Object> tempM;
			Map<Integer, Map<String, Object>> taskspacelist = new LinkedHashMap<Integer, Map<String, Object>>();
			for (Map<String, Object> value : query) {
				tempUid = (Integer) value.get("uid");
				Common.realname_set(sGlobal, sConfig, sNames, tempUid, (String) value.get("username"), "", 0);
				tempM = globalTask.get((Integer) value.get("taskid"));
				taskname = tempM != null ? (String) tempM.get("name") : null;
				if (taskname != null && !taskname.equals("")) {
					value.put("taskname", taskname);
					taskspacelist.put(tempUid, value);
				}
			}

			Common.realname_get(sGlobal, sConfig, sNames, space);
			if ("done".equals(view)) {
				actives.put("done", " class=\"active\"");
			} else {
				actives.put("task", " class=\"active\"");
			}

			request.setAttribute("done_per", done_per);
			request.setAttribute("tasklist", tasklist);
			request.setAttribute("taskspacelist", taskspacelist);
		}

		request.setAttribute("actives", actives);
		request.setAttribute("view", view);
		return include(request, response, sConfig, sGlobal, "cp_task.jsp");
	}

	
	public ActionForward cp_theme(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String op = Common.trim(request.getParameter("op"));

		boolean allowCss = Common.checkPerm(request, response, "allowcss");
		try {
			if (submitCheck(request, "csssubmit")) {
				String css = Common.trim(request.getParameter("css"));
				String result = checkSecurity(css);
				if (result != null) {
					return showMessage(request, response, result);
				}
				css = allowCss ? Common.getStr(css, 5000, true, true, false, 0, 0, request, response) : "";
				int nocss = Common.empty(request.getParameter("nocss")) ? 0 : 1;
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("theme", "");
				setData.put("css", css);
				setData.put("nocss", nocss);
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("uid", sGlobal.get("supe_uid"));
				dataBaseService.updateTable("spacefield", setData, whereData);
				return showMessage(request, response, "do_success", "cp.jsp?ac=theme&op=diy&view=ok", 0);
			} else if (submitCheck(request, "timeoffsetsubmit")) {
				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("timeoffset", request.getParameter("timeoffset"));
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("uid", sGlobal.get("supe_uid"));
				dataBaseService.updateTable("spacefield", setData, whereData);
				return showMessage(request, response, "do_success", "cp.jsp?ac=theme");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		String jchRoot = JavaCenterHome.jchRoot;
		String dir = Common.trim(request.getParameter("dir"));
		if (!Common.empty(dir)) {
			dir = dir.replaceAll("(?i)[^(0-9a-z)]", "");
			if (!"jchomedefault".equals(dir)) {
				File cssFile = new File(jchRoot + "theme/" + dir + "/style.css");
				if (!cssFile.exists()) {
					return showMessage(request, response, "theme_does_not_exist");
				}
			}
		}
		if ("use".equals(op)) {
			Map<String, Object> setData = new HashMap<String, Object>();
			setData.put("theme", "jchomedefault".equals(dir) ? "" : dir);
			setData.put("css", "");
			Map<String, Object> whereData = new HashMap<String, Object>();
			whereData.put("uid", sGlobal.get("supe_uid"));
			dataBaseService.updateTable("spacefield", setData, whereData);
			return showMessage(request, response, "do_success", "space.jsp", 0);
		} else if ("diy".equals(op)) {
			String view = request.getParameter("view");
			if (view != null) {
				request.setAttribute("lastSaveTime", Common.sgmdate(request, "HH:mm:ss", (Integer) sGlobal
						.get("timestamp")));
			}
			request.setAttribute("allowCss", allowCss);
		} else {
			List<Map<String, String>> themes = new ArrayList<Map<String, String>>();
			Map<String, String> defaultTheme = new HashMap<String, String>();
			defaultTheme.put("dir", "jchomedefault");
			defaultTheme.put("name", Common.getMessage(request, "cp_the_default_style"));
			defaultTheme.put("pic", "image/theme_default.jpg");
			themes.add(defaultTheme);
			Map<String, String> diyTheme = new HashMap<String, String>();
			diyTheme.put("dir", "jchomediy");
			diyTheme.put("name", Common.getMessage(request, "cp_the_diy_style"));
			diyTheme.put("pic", "image/theme_diy.jpg");
			themes.add(diyTheme);
			File[] themeDirs = Common.readDir(jchRoot + "theme");
			if (themeDirs != null) {
				for (File file : themeDirs) {
					String dirName = file.getName();
					String nowDir = jchRoot + "theme/" + dirName;
					if (new File(nowDir + "/style.css").exists()
							&& new File(nowDir + "/preview.jpg").exists()) {
						Map<String, String> theme = new HashMap<String, String>();
						theme.put("dir", dirName);
						theme.put("name", getCssName(dirName));
						themes.add(theme);
					}
				}
			}
			request.setAttribute("themes", themes);
			request.setAttribute("currentTime", Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) sGlobal
					.get("timestamp")));
			request.setAttribute("timeZoneIDs", Common.getTimeZoneIDs());
		}
		return include(request, response, sConfig, sGlobal, "cp_theme.jsp");
	}

	
	private String getCssName(String dirName) {
		String css = FileHelper.readFile(JavaCenterHome.jchRoot + "theme/" + dirName + "/style.css");
		String name = null;
		if (Common.empty(css)) {
			name = "No name";
		} else {
			List<String> mathes = Common.pregMatch(css, "(?i)\\[name\\](.+?)\\[\\/name\\]");
			if (mathes.size() == 2) {
				name = (String) Common.sHtmlSpecialChars(mathes.get(1));
			}
		}
		return name;
	}

	
	private String checkSecurity(String str) {

		str = str.replaceAll("(?is)\\/\\*[\\n\\r]*(.+?)[\n\r]*\\*\\/", "");
		str = str.replaceAll("(?i)[^a-z0-9]+", "");
		if (Common.matches(str, "(?i)(expression|implode|javascript)")) {
			return "css_contains_elements_of_insecurity";
		}
		return null;
	}

	public ActionForward cp_thread(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int eventId = Common.intval(request.getParameter("eventid"));
		Map<String, Object> event = null;
		Map<String, Object> userEvent = null;
		if (eventId != 0) {
			List<Map<String, Object>> eventList = dataBaseService.executeQuery("SELECT e.* FROM "
					+ JavaCenterHome.getTableName("event") + " e WHERE e.eventid='" + eventId + "'");
			if (eventList.isEmpty()) {
				return showMessage(request, response, "event_does_not_exist");
			} else {
				event = eventList.get(0);
			}
			if ((Integer) event.get("grade") == -2) {
				return showMessage(request, response, "event_is_closed");
			} else if ((Integer) event.get("grade") < 1) {
				return showMessage(request, response, "event_under_verify");
			}
			List<Map<String, Object>> userEventList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("userevent") + " WHERE uid = '" + sGlobal.get("supe_uid")
					+ "' AND eventid = '" + eventId + "'");
			if (!userEventList.isEmpty()) {
				userEvent = userEventList.get(0);
			}
			if (userEvent == null || (Integer) userEvent.get("status") < 2) {
				return showMessage(request, response, "event_only_allows_member_thread");
			}
		}
		try {
			if (submitCheck(request, "threadsubmit")) {
				int tid = Common.intval(request.getParameter("tid"));
				int tagId = Common.intval(request.getParameter("tagid"));
				if (eventId != 0 && (Integer) event.get("tagid") != tagId) {
					return showMessage(request, response, "event_mtag_not_match");
				}
				if (!Common.checkPerm(request, response, "allowthread")) {
					MessageVO msgVO = Common.ckSpaceLog(request);
					if (msgVO != null) {
						return showMessage(request, response, msgVO);
					}
					return showMessage(request, response, "no_privilege");
				}
				if (tid == 0) {
					if (Common.checkPerm(request, response, "seccode")
							&& !cpService.checkSeccode(request, response, sGlobal, sConfig, request
									.getParameter("seccode"))) {
						return showMessage(request, response, "incorrect_code");
					}
					if (!cpService.checkRealName(request, "thread")) {
						return showMessage(request, response, "no_privilege_realname");
					}
					if (!cpService.checkVideoPhoto(request, response, "thread")) {
						return showMessage(request, response, "no_privilege_videophoto");
					}
					int result = cpService.checkNewUser(request, response);
					switch (result) {
						case 1:
							break;
						case 2:
							return showMessage(request, response, "no_privilege_newusertime", "", 1, String
									.valueOf(sConfig.get("newusertime")));
						case 3:
							return showMessage(request, response, "no_privilege_avatar");
						case 4:
							return showMessage(request, response, "no_privilege_friendnum", "", 1, String
									.valueOf(sConfig.get("need_friendnum")));
						case 5:
							return showMessage(request, response, "no_privilege_email");
					}
					Map<String, Object> mtag = checkMtagSpace(request, response, event, userEvent, tagId);
					if (mtag == null) {
						return null;
					}
					if (Common.empty(mtag.get("allowthread"))) {
						return showMessage(request, response, "no_privilege");
					}
					int waitTime = Common.checkInterval(request, response, "post");
					if (waitTime > 0) {
						return showMessage(request, response, "operating_too_fast", null, 1, String
								.valueOf(waitTime));
					}
				} else {
					List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("thread") + " WHERE tid='" + tid + "'");
					if (threadList.isEmpty()) {
						return showMessage(request, response, "no_privilege");
					}
					Map<String, Object> thread = threadList.get(0);
					tagId = (Integer) thread.get("tagid");
					Map<String, Object> mtag = checkMtagSpace(request, response, event, userEvent, tagId);
					if (mtag == null) {
						return null;
					}
					if ((Integer) mtag.get("grade") < 8 && !thread.get("uid").equals(sGlobal.get("supe_uid"))
							&& (userEvent == null || (Integer) userEvent.get("status") < 3)) {
						return showMessage(request, response, "no_privilege");
					}
				}
				String subject = Common.getStr(request.getParameter("subject"), 80, true, true, true, 0, 0,
						request, response);
				if (Common.strlen(subject) < 2) {
					return showMessage(request, response, "title_not_too_little");
				}
				String message = blogService.checkHtml(request, response, Common.trim(request
						.getParameter("message")));
				message = Common.getStr(message, 0, true, false, true, 0, 1, request, response);
				message = message.replaceAll("(?i)<div></div>", "");
				String titlePic = null;
				List<Integer> picIds = new ArrayList<Integer>();
				Map<Integer, String> picIdForm = new HashMap<Integer, String>();
				for (Enumeration paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
					String key = (String) paramNames.nextElement();
					if (key.startsWith("picids[")) {
						int picId = Integer.parseInt(key.replaceAll("picids\\[(\\d+)\\]", "$1"));
						picIdForm.put(picId, request.getParameter(key));
						picIds.add(picId);
					}
				}
				Map uploads = new LinkedHashMap();
				if (!picIds.isEmpty()) {
					List<Map<String, Object>> picList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("pic") + " WHERE picid IN ("
							+ Common.sImplode(picIds) + ") AND uid='" + sGlobal.get("supe_uid") + "'");
					Map<String, Object> tempValue = null;
					for (Map<String, Object> value : picList) {
						tempValue = value;
						if (Common.empty(titlePic) && !Common.empty(value.get("thumb"))) {
							titlePic = Common.pic_get(sConfig, (String) value.get("filepath"),
									(Integer) value.get("thumb"), (Integer) value.get("remote"), true);
						}
						uploads.put(picIdForm.get(value.get("picid")), value);
					}
					if (Common.empty(titlePic) && tempValue != null) {
						titlePic = Common.pic_get(sConfig, (String) tempValue.get("filepath"),
								(Integer) tempValue.get("thumb"), (Integer) tempValue.get("remote"), true);
					}
				}
				if (uploads.size() > 0) {
					String regex = "(?i)<img.*src=\'(.+?)\'.*?_jchome_localimg_([0-9]+).+?src=\"(.+?)\"";
					if (Common.matches(message, regex)) {
						message = message.replaceAll(regex, "<IMG id=_jchome_localimg_$2 src=\"$1\"");
					} else if (Common
							.matches(
									message,
									regex = "(?i)<img\\s.*?_jchome_localimg_([0-9]+).+?src=\'(.+?)\'.+?src=\"(.+?)\"")) {
						message = message.replaceAll(regex, "<IMG id=_jchome_localimg_$1 src=\"$2\"");
					} else {
						Matcher m = Pattern.compile("(?i)\\[local\\](\\d+)\\[\\/local\\]").matcher(message);
						while (m.find()) {
							String id = m.group(1);
							if (uploads.get(id) != null) {
								message = message.replace("[local]" + id + "[/local]",
										"<IMG id=_jchome_localimg_" + id + " src=\"img_" + id + "\">");
							}
						}
					}
					Matcher m = Pattern.compile("(?i)<img\\s.*?_jchome_localimg_([0-9]+).+?src=\"(.+?)\"")
							.matcher(message);
					List<String> matches1 = new ArrayList<String>();
					List<String> matches2 = new ArrayList<String>();
					while (m.find()) {
						matches1.add(m.group(1));
						matches2.add(m.group(2));
					}

					int matchesLen = matches1.size();
					if (matchesLen != 0) {
						
						for (int i = 0; i < matchesLen; i++) {
							String index = matches1.get(i);
							Map value = (Map) uploads.get(index);
							if (!Common.empty(value)) {
								String search = matches2.get(i);
								String idSearch = "_jchome_localimg_" + index;
								String replace = Common.pic_get(sConfig, (String) value.get("filepath"),
										(Integer) value.get("thumb"), (Integer) value.get("remote"), false);
								message = message.replace(matches2.get(i), replace);
								message = message.replace(idSearch, "jchomelocalimg[]");
								uploads.remove(index);
							}
						}
					}
					for (Iterator it = uploads.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						Map value = (Map) uploads.get(key);
						String picUrl = Common.pic_get(sConfig, (String) value.get("filepath"),
								(Integer) value.get("thumb"), (Integer) value.get("remote"), false);
						message += "<div class=\"jchome-message-pic\"><img src=\"" + picUrl + "\"><p>"
								+ value.get("title") + "</p></div>";
					}
				}
				String checkMessage = message.replaceAll("(?is)(<div>|</div>|\\s)+", "");
				if (Common.strlen(message) < 2) {
					return showMessage(request, response, "content_is_not_less_than_four_characters");
				}
				message = Common.addSlashes(message);

				if (tid == 0) {
					int topicId = Common.intval(request.getParameter("topicid"));
					topicId = cpService.checkTopic(request, topicId, "thread");
					if (Common.empty(titlePic)) {
						titlePic = blogService.getMessagePic(message);
					}
					Map<String, Object> threadSetArr = new HashMap<String, Object>();
					threadSetArr.put("tagid", tagId);
					threadSetArr.put("uid", sGlobal.get("supe_uid"));
					threadSetArr.put("username", sGlobal.get("supe_username"));
					threadSetArr.put("dateline", sGlobal.get("timestamp"));
					threadSetArr.put("subject", subject);
					threadSetArr.put("lastpost", sGlobal.get("timestamp"));
					threadSetArr.put("lastauthor", sGlobal.get("supe_username"));
					threadSetArr.put("lastauthorid", sGlobal.get("supe_uid"));
					threadSetArr.put("topicid", topicId);
					if (eventId != 0) {
						threadSetArr.put("eventid", eventId);
					}
					tid = dataBaseService.insertTable("thread", threadSetArr, true, false);
					if (eventId != 0) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
								+ " SET threadnum=threadnum+1, updatetime='" + sGlobal.get("timestamp")
								+ "' WHERE eventid='" + eventId + "'");
					}
					Map<String, Object> postSetArr = new HashMap<String, Object>();
					postSetArr.put("tagid", tagId);
					postSetArr.put("tid", tid);
					postSetArr.put("uid", sGlobal.get("supe_uid"));
					postSetArr.put("username", sGlobal.get("supe_username"));
					postSetArr.put("ip", Common.getOnlineIP(request));
					postSetArr.put("dateline", sGlobal.get("timestamp"));
					postSetArr.put("message", message);
					postSetArr.put("isthread", 1);
					postSetArr.put("hotuser", "");
					dataBaseService.insertTable("post", postSetArr, false, false);
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag")
							+ " SET threadnum=threadnum+1 WHERE tagid='" + tagId + "'");
					cpService.updateStat(sGlobal, sConfig, "thread", false);
					String threadNumSQL = null;
					if (Common.empty(space.get("threadnum"))) {
						Map whereArr = new HashMap();
						whereArr.put("uid", space.get("uid"));
						space.put("threadnum", Integer.valueOf(Common.getCount("thread", whereArr, null)));
						threadNumSQL = "threadnum=" + space.get("threadnum");
					} else {
						threadNumSQL = "threadnum=threadnum+1";
					}
					Map<String, Integer> reward = Common.getReward("publishthread", false, 0, "", true,
							request, response);
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
							+ threadNumSQL + ", lastpost='" + sGlobal.get("timestamp") + "', updatetime='"
							+ sGlobal.get("timestamp") + "', credit=credit+" + reward.get("credit")
							+ ", experience=experience+" + reward.get("experience") + " WHERE uid='"
							+ sGlobal.get("supe_uid") + "'");
				} else {
					Map threadSetData = new HashMap();
					threadSetData.put("tagid", tagId);
					threadSetData.put("subject", subject);
					Map whereData = new HashMap();
					whereData.put("tid", tid);
					dataBaseService.updateTable("thread", threadSetData, whereData);
					Map postSetData = new HashMap();
					postSetData.put("tagid", tagId);
					postSetData.put("ip", Common.getOnlineIP(request));
					postSetData.put("message", message);
					postSetData.put("pic", "");
					if (Common.checkPerm(request, response, "edittrail")) {
						message = message
								+ Common.sAddSlashes(Common.getMessage(request, "cp_thread_edit_trail",
										new String[] {sGlobal.get("supe_username").toString(),
												Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss", 0)}));
						postSetData.put("message", message);
					}
					whereData = new HashMap();
					whereData.put("tid", tid);
					whereData.put("isthread", 1);
					dataBaseService.updateTable("post", postSetData, whereData);
				}
				if (!Common.empty(request.getParameter("makefeed"))) {
					feedService.feedPublish(request, response, tid, "tid", tid == 0 ? true : false);
				}
				int topicId = Common.intval(request.getParameter("topicid"));
				String toURL = null;
				if (topicId != 0) {
					cpService.topicJoin(request, topicId, (Integer) sGlobal.get("supe_uid"), (String) sGlobal
							.get("supe_username"));
					toURL = "space.jsp?do=topic&topicid=" + topicId + "&view=thread";
				} else {
					toURL = "space.jsp?uid=" + sGlobal.get("supe_uid") + "&do=thread&id=" + tid;
					if (eventId != 0) {
						toURL += "&eventid=" + eventId;
					}
				}
				return showMessage(request, response, "do_success", toURL, 0);
			} else if (submitCheck(request, "postsubmit")) {
				if (!Common.checkPerm(request, response, "allowpost")) {
					MessageVO msgVO = Common.ckSpaceLog(request);
					if (msgVO != null) {
						return showMessage(request, response, msgVO);
					}
					return showMessage(request, response, "no_privilege");
				}
				if (!cpService.checkRealName(request, "post")) {
					return showMessage(request, response, "no_privilege_realname");
				}
				if (!cpService.checkVideoPhoto(request, response, "post")) {
					return showMessage(request, response, "no_privilege_videophoto");
				}
				int result = cpService.checkNewUser(request, response);
				switch (result) {
					case 1:
						break;
					case 2:
						return showMessage(request, response, "no_privilege_newusertime", "", 1, String
								.valueOf(sConfig.get("newusertime")));
					case 3:
						return showMessage(request, response, "no_privilege_avatar");
					case 4:
						return showMessage(request, response, "no_privilege_friendnum", "", 1, String
								.valueOf(sConfig.get("need_friendnum")));
					case 5:
						return showMessage(request, response, "no_privilege_email");
				}
				int waitTime = Common.checkInterval(request, response, "post");
				if (waitTime > 0) {
					return showMessage(request, response, "operating_too_fast", null, 1, String
							.valueOf(waitTime));
				}
				int tid = Common.intval(request.getParameter("tid"));
				Map<String, Object> thread = null;
				if (tid != 0) {
					List<Map<String, Object>> threadList = dataBaseService
							.executeQuery("SELECT t.*, p.* FROM " + JavaCenterHome.getTableName("thread")
									+ " t LEFT JOIN " + JavaCenterHome.getTableName("post")
									+ " p ON p.tid=t.tid AND p.isthread=1 WHERE t.tid='" + tid + "'");
					if (!threadList.isEmpty()) {
						thread = threadList.get(0);
					}
				}
				if (thread == null) {
					return showMessage(request, response, "the_discussion_topic_does_not_exist");
				}
				if (cpService.isBlackList((Integer) thread.get("uid"), (Integer) sGlobal.get("supe_uid")) != 0) {
					return showMessage(request, response, "is_blacklist");
				}
				Map<String, Object> mtag = checkMtagSpace(request, response, event, userEvent,
						(Integer) thread.get("tagid"));
				if (mtag == null) {
					return null;
				}
				if (Common.empty(mtag.get("allowpost"))) {
					return showMessage(request, response, "no_privilege");
				}
				String message = request.getParameter("message");
				String[] pics = request.getParameterValues("pics");
				if (pics != null) {
					for (String pic : pics) {
						String picURL = cpService.getPicUrlt(pic);
						if (!Common.empty(picURL)) {
							message += "\n[img]" + picURL + "[/img]";
						}
					}
				}
				message = Common.getStr(message, 0, true, true, true, 2, 0, request, response);
				if (Common.strlen(message) < 2) {
					return showMessage(request, response, "content_is_not_less_than_four_characters");
				}
				String summay = Common.getStr(message, 150, true, true, false, 0, 0, request, response);
				int pid = Common.intval(request.getParameter("pid"));
				List<Map<String, Object>> postList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("post") + " WHERE pid='" + pid + "' AND tid='" + tid
						+ "' AND isthread='0'");
				Map<String, Object> post = null;
				Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
				if (!postList.isEmpty()) {
					post = postList.get(0);
					if (cpService.isBlackList((Integer) post.get("uid"), (Integer) sGlobal.get("supe_uid")) != 0) {
						return showMessage(request, response, "is_blacklist");
					}
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) post.get("uid"), (String) post
							.get("username"), null, 0);
					Common.realname_get(sGlobal, sConfig, sNames, space);
					String postMessage = post.get("message").toString();
					postMessage = postMessage.replaceAll(
							"(?is)<div class=\"quote\"><span class=\"q\">.*?</span></div>", "");

					postMessage = postMessage.replaceAll("(?is)<ins class=\"modify\".+?</ins>", "");
					postMessage = BBCode.html2bbcode(postMessage);
					message = Common.addSlashes("<div class=\"quote\"><span class=\"q\"><b>"
							+ sNames.get(post.get("uid")) + "</b>: "
							+ Common.getStr(postMessage, 150, false, false, false, 2, 1, request, response)
							+ "</span></div>")
							+ message;
					post.put("message", postMessage);
				}
				Map setArr = new HashMap();
				setArr.put("tagid", (Integer) thread.get("tagid"));
				setArr.put("tid", tid);
				setArr.put("uid", sGlobal.get("supe_uid"));
				setArr.put("username", sGlobal.get("supe_username"));
				setArr.put("ip", Common.getOnlineIP(request));
				setArr.put("dateline", sGlobal.get("timestamp"));
				setArr.put("message", message);
				setArr.put("hotuser", "");
				pid = dataBaseService.insertTable("post", setArr, true, false);
				String subject = Common.getMessage(request, "cp_mtag_reply", new String[] {
						sNames.get(space.get("uid")),
						Common.sHtmlSpecialChars(
								Common.getSiteUrl(request) + "space.jsp?uid=" + thread.get("uid")
										+ "&do=thread&id=" + thread.get("tid")).toString()});
				cpService.sendMail(request, response, (Integer) thread.get("uid"), "", subject, "",
						"mtag_reply");
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("thread")
						+ " SET replynum=replynum+1, lastpost='" + sGlobal.get("timestamp")
						+ "', lastauthor='" + sGlobal.get("supe_username") + "', lastauthorid='"
						+ sGlobal.get("supe_uid") + "' WHERE tid='" + tid + "'");
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("mtag")
						+ " SET postnum=postnum+1 WHERE tagid='" + thread.get("tagid") + "'");
				if (Common.empty(post) && !thread.get("uid").equals(sGlobal.get("supe_uid"))) {
					Common.getReward("replythread", true, 0, thread.get("tid").toString(), true, request,
							response);
					Common.realname_set(sGlobal, sConfig, sNames, (Integer) thread.get("uid"),
							(String) thread.get("username"), null, 0);
					Common.realname_get(sGlobal, sConfig, sNames, space);

					if (Common.empty(mtag.get("viewperm"))) {
						if (Common.ckPrivacy(sGlobal, sConfig, space, "post", 1)) {
							String title_template = Common.getMessage(request, "cp_feed_thread_reply");
							Map title_data = new HashMap();
							title_data.put("touser", "<a href=\"space.jsp?uid=" + thread.get("uid") + "\">"
									+ sNames.get(thread.get("uid")) + "</a>");
							title_data.put("thread", "<a href=\"space.jsp?uid=" + thread.get("uid")
									+ "&do=thread&id=" + thread.get("tid") + "\">" + thread.get("subject")
									+ "</a>");
							cpService.addFeed(sGlobal, "post", title_template, title_data, "", null, "",
									null, null, "", 0, 0, 0, "", false);
						}
					}
					String note = Common.getMessage(request, "cp_note_thread_reply")
							+ " <a href=\"space.jsp?uid=" + thread.get("uid") + "&do=thread&id="
							+ thread.get("tid") + "&pid=" + pid + "\" target=\"_blank\">"
							+ thread.get("subject") + "</a>";
					cpService.addNotification(request, sGlobal, sConfig, (Integer) thread.get("uid"), "post",
							note, false);
				} else if (!Common.empty(post)) {
					String note = Common.getMessage(request, "cp_note_post_reply", new String[] {
							"space.jsp?uid=" + thread.get("uid") + "&do=thread&id=" + thread.get("tid"),
							thread.get("subject").toString(),
							"space.jsp?uid=" + thread.get("uid") + "&do=thread&id=" + thread.get("tid")
									+ "&pid=" + pid});
					cpService.addNotification(request, sGlobal, sConfig, (Integer) post.get("uid"), "post",
							note, false);
				}
				if (!thread.get("uid").equals(sGlobal.get("supe_uid"))) {
					cpService.updateHot(request, response, "tid", (Integer) thread.get("tid"),
							(String) thread.get("hotuser"));
				}
				cpService.updateStat(sGlobal, sConfig, "post", false);
				return showMessage(request, response, "do_success", "space.jsp?uid="
						+ sGlobal.get("supe_uid") + "&do=thread&id=" + tid + "&pid=" + pid, 0);
			} else if (submitCheck(request, "posteditsubmit")) {
				int pid = Common.intval(request.getParameter("pid"));
				List<Map<String, Object>> postList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("post") + " WHERE pid='" + pid + "'");
				Map<String, Object> post = null;
				if (postList.isEmpty()) {
					return showMessage(request, response, "no_privilege");
				} else {
					post = postList.get(0);
				}
				int tagId = (Integer) post.get("tagid");
				Map<String, Object> mtag = checkMtagSpace(request, response, event, userEvent, tagId);
				if (mtag == null) {
					return null;
				}
				if ((Integer) mtag.get("grade") < 8 && !post.get("uid").equals(sGlobal.get("supe_uid"))
						&& (userEvent == null || (Integer) userEvent.get("status") < 3)) {
					return showMessage(request, response, "no_privilege");
				}
				String message = request.getParameter("message");
				String[] pics = request.getParameterValues("pics");
				if (pics != null) {
					for (String pic : pics) {
						String picURL = cpService.getPicUrlt(pic);
						if (!Common.empty(picURL)) {
							message += "\n[img]" + picURL + "[/img]";
						}
					}
				}
				message = Common.getStr(message, 0, true, true, true, 2, 0, request, response);
				if (Common.strlen(message) < 2) {
					return showMessage(request, response, "content_is_too_short");
				}
				if (Common.checkPerm(request, response, "edittrail")
						|| (!Common.empty(post.get("uid")) && !post.get("uid").equals(space.get("uid")))) {
					Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
					message = message
							+ Common.sAddSlashes(Common.getMessage(request, "cp_thread_edit_trail",
									new String[] {sNames.get(sGlobal.get("supe_uid")),
											Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss", 0)}));
				}
				Map setData = new HashMap();
				setData.put("message", message);
				Map whereData = new HashMap();
				whereData.put("pid", pid);
				dataBaseService.updateTable("post", setData, whereData);
				return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
			}

			int pid = Common.intval(request.getParameter("pid"));
			int tid = Common.intval(request.getParameter("tid"));
			int tagId = Common.intval(request.getParameter("tagid"));
			Map<String, Object> thread = null;
			Map<String, Object> post = null;
			String op = request.getParameter("op");
			if ("edit".equals(op)) {
				List<Map<String, Object>> postList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("post") + " WHERE pid='" + pid + "'");
				if (postList.isEmpty()) {
					return showMessage(request, response, "no_privilege");
				} else {
					post = postList.get(0);
				}
				post.put("message", post.get("message").toString().replaceAll(
						"(?is)<ins class=\"modify\".+?</ins>", ""));
				tagId = (Integer) post.get("tagid");
				Map<String, Object> mtag = checkMtagSpace(request, response, event, userEvent, tagId);
				if (mtag == null) {
					return null;
				}
				if ((Integer) mtag.get("grade") < 8 && !post.get("uid").equals(sGlobal.get("supe_uid"))
						&& (userEvent == null || (Integer) userEvent.get("status") < 3)) {
					return showMessage(request, response, "no_privilege");
				}
				if (!Common.empty(post.get("isthread"))) {
					List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("thread") + " WHERE tid='" + post.get("tid") + "'");
					thread = threadList.get(0);
				}
				String message = post.get("message").toString();
				if (thread != null) {
					message = message.replace("&amp;", "&amp;amp;");
					message = (String) Common.sHtmlSpecialChars(message);
					op = null;
					Map<String, String[]> paramMap = request.getParameterMap();
					paramMap.put("op", null);
					request.setAttribute("albums", cpService.getAlbums((Integer) sGlobal.get("supe_uid")));
					if (!Common.empty(post.get("pic"))) {
						message += "<div><img src=\"" + post.get("pic") + "\"></div>";
					}
				} else {
					message = BBCode.html2bbcode(message);
				}
				post.put("message", message);
				request.setAttribute("mtag", mtag);
				request.setAttribute("post", post);
			} else if ("delete".equals(op)) {
				if (submitCheck(request, "postdeletesubmit")) {
					List<Map<String, Object>> delPosts = adminDeleteService.deletePosts(request, response,
							(Integer) sGlobal.get("supe_uid"), tagId, pid);
					if (Common.empty(delPosts)) {
						return showMessage(request, response, "no_privilege");
					} else {
						post = delPosts.get(0);
						String URL = null;
						if (!Common.empty(post.get("isthread"))) {
							URL = "space.jsp?uid=" + post.get("uid") + "&do=mtag&tagid=" + post.get("tagid")
									+ "&view=list";
						} else {
							URL = request.getParameter("refer");
						}
						return showMessage(request, response, "do_success", URL, 0);
					}
				}
			} else if ("reply".equals(op)) {
				if (eventId != 0) {
					if (userEvent == null || (Integer) userEvent.get("status") < 2) {
						return showMessage(request, response, "event_only_allows_member_thread");
					}
				}
				List<Map<String, Object>> postList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("post") + " WHERE pid='" + pid + "'");
				if (postList.isEmpty()) {
					return showMessage(request, response, "posting_does_not_exist");
				} else {
					post = postList.get(0);
				}
				request.setAttribute("post", post);
			} else if ("digest".equals(op)) {
				opService.digestThreads(request, response, (Integer) sGlobal.get("supe_uid"), tagId, request
						.getParameter("cancel") == null ? 1 : 0, tid);
				return showMessage(request, response, "do_success");
			} else if ("top".equals(op)) {
				opService.topThreads(request, response, (Integer) sGlobal.get("supe_uid"), tagId, request
						.getParameter("cancel") == null ? 1 : 0, tid);
				return showMessage(request, response, "do_success");
			} else if ("edithot".equals(op)) {
				if (!Common.checkPerm(request, response, "managethread")) {
					return showMessage(request, response, "no_privilege");
				}
				List<Map<String, Object>> threadList = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("thread") + " WHERE tid='" + tid + "'");
				if (threadList.isEmpty()) {
					return showMessage(request, response, "no_privilege");
				} else {
					thread = threadList.get(0);
				}
				if (submitCheck(request, "hotsubmit")) {
					int hot = Common.intval(request.getParameter("hot"));
					Map setData = new HashMap();
					setData.put("hot", hot);
					Map whereData = new HashMap();
					whereData.put("tid", tid);
					dataBaseService.updateTable("thread", setData, whereData);
					if (hot > 0) {
						feedService.feedPublish(request, response, tid, "tid", false);
					} else {
						setData = new HashMap();
						setData.put("hot", hot);
						whereData = new HashMap();
						whereData.put("id", tid);
						whereData.put("idtype", "tid");
						dataBaseService.updateTable("feed", setData, whereData);
					}
					return showMessage(request, response, "do_success", "space.jsp?uid=" + thread.get("uid")
							+ "&do=thread&id=" + tid, 0);
				}
			} else {
				if (!Common.checkPerm(request, response, "allowthread")) {
					MessageVO msgVO = Common.ckSpaceLog(request);
					if (msgVO != null) {
						return showMessage(request, response, msgVO);
					}
					return showMessage(request, response, "no_privilege");
				}
				if (!cpService.checkRealName(request, "thread")) {
					return showMessage(request, response, "no_privilege_realname");
				}
				if (!cpService.checkVideoPhoto(request, response, "thread")) {
					return showMessage(request, response, "no_privilege_videophoto");
				}
				int result = cpService.checkNewUser(request, response);
				switch (result) {
					case 1:
						break;
					case 2:
						return showMessage(request, response, "no_privilege_newusertime", "", 1, String
								.valueOf(sConfig.get("newusertime")));
					case 3:
						return showMessage(request, response, "no_privilege_avatar");
					case 4:
						return showMessage(request, response, "no_privilege_friendnum", "", 1, String
								.valueOf(sConfig.get("need_friendnum")));
					case 5:
						return showMessage(request, response, "no_privilege_email");
				}
				Map<String, Object> mtag = null;
				if (tagId != 0) {
					mtag = checkMtagSpace(request, response, event, userEvent, tagId);
					if (mtag == null) {
						return null;
					}
					if (Common.empty(mtag.get("allowthread"))) {
						return showMessage(request, response, "no_privilege");
					}
				}
				request.setAttribute("albums", cpService.getAlbums((Integer) sGlobal.get("supe_uid")));
				if (mtag == null) {
					Map<Object, Map<String, Object>> profield = Common.getCacheDate(request, response,
							"/data/cache/cache_profield.jsp", "globalProfield");
					tagId = 0;
					Map<Object, Map<Object, Map<String, Object>>> mtagList = new LinkedHashMap<Object, Map<Object, Map<String, Object>>>();
					List<Map<String, Object>> tempList = dataBaseService
							.executeQuery("SELECT main.*,field.tagname,field.membernum,field.fieldid,field.close FROM "
									+ JavaCenterHome.getTableName("tagspace")
									+ " main LEFT JOIN "
									+ JavaCenterHome.getTableName("mtag")
									+ " field ON field.tagid=main.tagid WHERE main.uid='"
									+ sGlobal.get("supe_uid") + "' AND main.grade>=0");
					boolean haveMtag = false;
					for (Map<String, Object> value : tempList) {
						haveMtag = true;
						if (Common.empty(value.get("close"))
								&& (Integer) value.get("membernum") >= (Integer) profield.get(
										value.get("fieldid")).get("mtagminnum")) {
							Map<Object, Map<String, Object>> tempMap = mtagList.get(value.get("fieldid"));
							if (tempMap == null) {
								tempMap = new LinkedHashMap<Object, Map<String, Object>>();
							}
							tempMap.put(value.get("tagid"), value);
							mtagList.put(value.get("fieldid"), tempMap);
						}
					}
					if (mtagList.isEmpty()) {
						if (haveMtag) {
							return showMessage(request, response, "no_mtag_allow_thread");
						} else {
							return showMessage(request, response, "settings_of_your_mtag");
						}
					}
					request.setAttribute("mtagList", mtagList);
				}
				int topicId = Common.intval(request.getParameter("topicid"));
				Map<String, String[]> paramMap = request.getParameterMap();
				paramMap.put("op", new String[] {topicId + ""});
				Map<String, Object> topic = null;
				if (topicId != 0) {
					request.setAttribute("topic", Common.getTopic(request, topicId));
				}
				request.setAttribute("topicid", topicId);
				if (!Common.empty(topic)) {
					Map actives = new HashMap();
					actives.put("thread", " class=\"active\"");
					request.setAttribute("actives", actives);
				}
				if (eventId != 0) {
					request.setAttribute("event", event);
				}
				request.setAttribute("mtag", mtag);
			}
			request.setAttribute("ckprivacy", Common.ckPrivacy(sGlobal, sConfig, space, "thread", 1));
			request.setAttribute("tid", tid);
			request.setAttribute("pid", pid);
			request.setAttribute("tagid", tagId);
			request.setAttribute("eventid", eventId);
			request.setAttribute("thread", thread);
		} catch (Exception e) {
			e.printStackTrace();
			return showMessage(request, response, e.getMessage());
		}
		return include(request, response, sConfig, sGlobal, "cp_thread.jsp");
	}

	public ActionForward cp_top(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		try {
			if (submitCheck(request, "friendsubmit")) {
				int showCredit = Common.intval(request.getParameter("stakecredit"));
				if (showCredit > (Integer) space.get("credit")) {
					showCredit = (Integer) space.get("credit");
				}
				if (showCredit < 1) {
					return showMessage(request, response, "showcredit_error");
				}
				String fUserName = Common.trim(request.getParameter("fusername"));
				Map whereArr = new HashMap();
				whereArr.put("uid", space.get("uid"));
				whereArr.put("fusername", fUserName);
				whereArr.put("status", 1);
				String fUid = Common.getCount("friend", whereArr, "fuid");
				if (Common.empty(fUserName) || Common.empty(fUid) || fUid.equals(space.get("uid").toString())) {
					return showMessage(request, response, "showcredit_fuid_error");
				}
				whereArr = new HashMap();
				whereArr.put("uid", fUid);
				int count = Common.intval(Common.getCount("show", whereArr, null));
				if (count != 0) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("show")
							+ " SET credit=credit+" + showCredit + " WHERE uid='" + fUid + "'");
				} else {
					Map insertData = new HashMap();
					insertData.put("uid", fUid);
					insertData.put("username", fUserName);
					insertData.put("credit", showCredit);
					dataBaseService.insertTable("show", insertData, false, true);
				}
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + showCredit + " WHERE uid='" + space.get("uid") + "'");
				cpService.addNotification(request, sGlobal, sConfig, Integer.valueOf(fUid), "credit", Common
						.getMessage(request, "cp_note_showcredit", String.valueOf(showCredit)), false);
				Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
				Common.realname_set(sGlobal, sConfig, sNames, Integer.valueOf(fUid), fUserName, "", 0);
				Common.realname_get(sGlobal, sConfig, sNames, space);
				if (Common.ckPrivacy(sGlobal, sConfig, space, "show", 1)) {
					Map title_data = new HashMap();
					title_data.put("fusername", "<a href=\"space.jsp?uid=" + fUid + "\">" + sNames.get(Integer.valueOf(fUid))
							+ "</a>");
					title_data.put("credit", showCredit);
					cpService.addFeed(sGlobal, "show", Common.getMessage(request, "cp_feed_showcredit"),
							title_data, "", null, "", null, null, "", 0, 0, 0, "", false);
				}
				return showMessage(request, response, "showcredit_friend_do_success", "space.jsp?do=top");
			} else if (submitCheck(request, "showsubmit")) {
				int showCredit = Common.intval(request.getParameter("showcredit"));
				if (showCredit > (Integer) space.get("credit")) {
					showCredit = (Integer) space.get("credit");
				}
				if (showCredit < 1) {
					return showMessage(request, response, "showcredit_error");
				}
				String note = Common.getStr(request.getParameter("note"), 100, true, true, true, 0, 0,
						request, response);
				Map whereArr = new HashMap();
				whereArr.put("uid", sGlobal.get("supe_uid"));
				int count = Common.intval(Common.getCount("show", whereArr, null));
				if (count != 0) {
					String noteSQL = !Common.empty(note) ? ", note='" + note + "'" : "";
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("show")
							+ " SET credit=credit+" + showCredit + noteSQL + " WHERE uid='"
							+ sGlobal.get("supe_uid") + "'");
				} else {
					Map insertData = new HashMap();
					insertData.put("uid", sGlobal.get("supe_uid"));
					insertData.put("username", sGlobal.get("supe_username"));
					insertData.put("note", note);
					insertData.put("credit", showCredit);
					dataBaseService.insertTable("show", insertData, false, true);
				}
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
						+ " SET credit=credit-" + showCredit + " WHERE uid='" + space.get("uid") + "'");
				if (Common.ckPrivacy(sGlobal, sConfig, space, "show", 1)) {
					Map title_data = new HashMap();
					title_data.put("credit", showCredit);
					cpService.addFeed(sGlobal, "show", Common.getMessage(request, "cp_feed_showcredit_self"),
							title_data, "", null, note, null, null, "", 0, 0, 0, "", false);
				}
				return showMessage(request, response, "showcredit_do_success", "space.jsp?do=top");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		return showMessage(request, response, "do_success", "space.jsp?do=top", 0);
	}

	public ActionForward cp_topic(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int timestamp = (Integer) sGlobal.get("timestamp");
		String tempS = request.getParameter("topicid");
		int topicid = Common.empty(tempS) ? 0 : Common.intval(tempS);
		tempS = request.getParameter("id");
		int id = Common.empty(tempS) ? 0 : Common.intval(tempS);
		tempS = request.getParameter("idtype");
		String idtype = Common.empty(tempS) ? "" : tempS.trim();
		tempS = request.getParameter("op");
		String op = Common.empty(tempS) ? "" : tempS;

		List<Map<String, Object>> query;
		Map<String, Object> topic = null;
		if (topicid != 0) {
			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("topic")
					+ " WHERE topicid='" + topicid + "'");
			topic = query.size() > 0 ? query.get(0) : null;
		}
		if (Common.empty(topic)) {
			if (!"join".equals(op)) {
				if (!Common.checkPerm(request, response, "allowtopic")) {
					Common.ckSpaceLog(request);
					return showMessage(request, response, "no_privilege");
				}
			}
			topicid = 0;
		} else {
			if (!"join".equals(op)) {
				if (supe_uid != (Integer) topic.get("uid")
						&& !Common.checkPerm(request, response, "managetopic")) {
					return showMessage(request, response, "no_privilege");
				}
			}
			topic.put("pic", Common.pic_get(sConfig, (String) topic.get("pic"), (Integer) topic.get("thumb"),
					(Integer) topic.get("remote"), true));
		}

		boolean sc;
		FileUploadUtil upload = getParsedFileUploadUtil(request);
		try {
			sc = submitCheckForMulti(request, upload, "topicsubmit");
		} catch (Exception e) {
			e.printStackTrace();
			return showMessage(request, response, e.getMessage());
		}
		if (sc) {
			Map<String, Object> setarr = new HashMap<String, Object>();
			String subject;
			String message;
			try {
				subject = Common.getStr(upload.getParameter("subject"), 80, true, true, false, 0, 0, request,
						response);
				message=Common.getStr(upload.getParameter("message"), 0, true, true, false, 0, 0, request,
						response);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			setarr.put("subject", subject);
			setarr.put("message", message);
			String[] tempSA = upload.getParameterValues("jointype[]");
			setarr.put("jointype", Common.empty(tempSA) ? "" : Common.implode(tempSA, ","));
			tempSA = upload.getParameterValues("joingid[]");
			setarr.put("joingid", Common.empty(tempSA) ? "" : Common.implode(tempSA, ","));
			tempS = upload.getParameter("endtime");
			setarr.put("endtime", Common.empty(tempS) ? 0 : Common.strToTime(tempS, Common.getTimeOffset(
					sGlobal, sConfig), "yyyy-MM-dd HH:mm"));

			if (Common.strlen(subject) < 4) {
				return showMessage(request, response, "topic_subject_error");
			}
			FileItem fileItem = upload.getFileItem("pic");
			if (fileItem != null && fileItem.getSize() > 0) {
				Object ob = cpService.savePic(request, response, fileItem, "-1", "", 0);
				if (!Common.empty(ob) && Common.isArray(ob)) {
					Map<String, Object> filearr = (Map<String, Object>) ob;
					setarr.put("pic", filearr.get("filepath"));
					setarr.put("thumb", filearr.get("thumb"));
					setarr.put("remote", filearr.get("remote"));
				}
			}

			if (Common.empty(topicid)) {
				setarr.put("uid", supe_uid);
				setarr.put("username", sGlobal.get("supe_username"));
				setarr.put("dateline", timestamp);
				setarr.put("lastpost", timestamp);

				topicid = dataBaseService.insertTable("topic", setarr, true, false);
			} else {
				Map<String, Object> whereData = new HashMap<String, Object>();
				whereData.put("topicid", topicid);
				dataBaseService.updateTable("topic", setarr, whereData);
			}
			return showMessage(request, response, "do_success", "space.jsp?do=topic&topicid=" + topicid, 0);
		}

		

		if ("delete".equals(op)) {
			
			try {
				if (submitCheck(request, "deletesubmit")) {
					if (adminDeleteService.deletetopics(request, response, sGlobal, topicid)) {
						return showMessage(request, response, "do_success", "space.jsp?do=topic");
					} else {
						return showMessage(request, response, "failed_to_delete_operation");
					}
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("topicid", topicid);
		} else if ("join".equals(op)) {
			
			String tablename = cpService.getTablebyIdType(idtype);
			Map<String, Object> item = null;
			if (!Common.empty(tablename) && id != 0) {
				if (tablename.equals("pic")) {
					query = dataBaseService.executeQuery("SELECT s.username, p.* FROM "
							+ JavaCenterHome.getTableName("pic") + " p " + "LEFT JOIN "
							+ JavaCenterHome.getTableName("space") + " s ON s.uid=p.uid " + "WHERE p.picid='"
							+ id + "'");
				} else {
					query = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName(tablename) + " WHERE " + idtype + "='" + id + "'");
				}
				item = query.size() > 0 ? query.get(0) : null;
			}
			if (Common.empty(item)) {
				return showMessage(request, response, "no_privilege");
			}
			int uid = (Integer) item.get("uid");
			if (supe_uid != uid && !Common.checkPerm(request, response, "managetopic")
					&& !Common.checkPerm(request, response, "manage" + tablename)) {
				return showMessage(request, response, "no_privilege");
			}

			Map<Integer, Map<String, Object>> tlist = new LinkedHashMap<Integer, Map<String, Object>>();
			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("topic")
					+ " ORDER BY lastpost DESC LIMIT 0,50");
			String[] jointype;
			String[] joingid;
			Integer endtime;
			for (Map<String, Object> value : query) {
				tempS = (String) value.get("jointype");
				if (!Common.empty(tempS)) {
					jointype = tempS.split(",");
				} else {
					jointype = null;
				}
				if (!Common.empty(jointype) && !Common.in_array(jointype, tablename)) {
					continue;
				}
				if (supe_uid == uid) {
					tempS = (String) value.get("joingid");
					if (!Common.empty(tempS)) {
						joingid = tempS.split(",");
					} else {
						joingid = null;
					}
					if (!Common.empty(joingid) && !Common.in_array(joingid, space.get("groupid"))) {
						continue;
					}
				}
				endtime = (Integer) value.get("endtime");
				if (endtime != 0 && timestamp > endtime) {
					continue;
				}
				tlist.put((Integer) value.get("topicid"), value);
			}
			if (Common.empty(tlist)) {
				return showMessage(request, response, "topic_list_none");
			}
			try {
				if (submitCheck(request, "joinsubmit")) {
					int newtopicid = Common.intval(request.getParameter("newtopicid"));
					if (Common.empty(tlist.get(newtopicid))) {
						newtopicid = 0;
					}
					Map<String, Object> setData = new HashMap<String, Object>();
					setData.put("topicid", newtopicid);
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put(idtype, id);
					dataBaseService.updateTable(tablename, setData, whereData);
					if (newtopicid != 0) {
						cpService.topicJoin(request, newtopicid, uid, Common.addSlashes((String) item
								.get("username")));
					} else {
						query = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("topicuser") + " WHERE uid='" + uid
								+ "' AND topicid='" + item.get("topicid") + "'");
						Map<String, Object> value = query.size() > 0 ? query.get(0) : null;
						if (!Common.empty(value)) {
							dataBaseService.execute("DELETE FROM " + JavaCenterHome.getTableName("topicuser")
									+ " WHERE id='" + value.get("id") + "'");
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("topic")
									+ " SET joinnum=joinnum-1 WHERE topicid='" + item.get("topicid")
									+ "' AND joinnum>0");
						}
					}
					return showMessage(request, response, "do_success", request.getParameter("refer"), 0);
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			request.setAttribute("id", id);
			request.setAttribute("idtype", idtype);
			request.setAttribute("tlist", tlist);
		} else if ("ignore".equals(op)) {
			
			request.setAttribute("topicid", topicid);
			request.setAttribute("id", id);
			request.setAttribute("idtype", idtype);
		} else {
			
			if (topic == null) {
				topic = new HashMap<String, Object>();
			}
			Map<String, String> jointypes = new HashMap<String, String>();
			tempS = (String) topic.get("jointype");
			String[] tempSA = null;
			if (tempS != null) {
				tempSA = tempS.split(",");
			}
			topic.put("jointype", tempSA);
			if (tempSA != null) {
				for (String value : tempSA) {
					jointypes.put(value, " checked");
				}
			}

			Map<String, String> joingids = new HashMap<String, String>();
			tempS = (String) topic.get("joingid");
			tempSA = null;
			if (tempS != null) {
				tempSA = tempS.split(",");
			}
			topic.put("joingid", tempSA);
			if (tempSA != null) {
				for (String value : tempSA) {
					joingids.put(value, " checked");
				}
			}

			Object endtimeO = topic.get("endtime");
			if (!Common.empty(endtimeO)) {
				topic.put("endtime", Common.sgmdate(request, "yyyy-MM-dd HH:mm", (Integer) endtimeO));
			} else {
				topic.put("endtime", "");
			}
			Map<Integer, Map<String, Map<String, Object>>> usergroups = new LinkedHashMap<Integer, Map<String, Map<String, Object>>>();
			usergroups.put(-1, new LinkedHashMap<String, Map<String, Object>>());
			usergroups.put(1, new LinkedHashMap<String, Map<String, Object>>());
			usergroups.put(0, new LinkedHashMap<String, Map<String, Object>>());

			query = dataBaseService.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("usergroup"));
			Map<String, Map<String, Object>> tempM;
			for (Map<String, Object> value : query) {
				tempM = usergroups.get((Integer) value.get("system"));
				if (tempM != null) {
					tempM.put(String.valueOf(value.get("gid")), value);
				}
			}
			request.setAttribute("topicid", topicid);
			request.setAttribute("topic", topic);
			request.setAttribute("jointypes", jointypes);
			request.setAttribute("joingids", joingids);
			request.setAttribute("usergroups", usergroups);
		}

		request.setAttribute("op", op);
		return include(request, response, sConfig, sGlobal, "cp_topic.jsp");
	}

	
	public ActionForward cp_gift(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (!Common.checkPerm(request, response, "allowgift")) {
			MessageVO msgVO = Common.ckSpaceLog(request);
			if (msgVO != null) {
				return showMessage(request, response, msgVO);
			}
			return showMessage(request, response, "gift_no_authority_to_send");
		}
		if (!cpService.checkRealName(request, "gift")) {
			return showMessage(request, response, "no_privilege_realname");
		}
		if (!cpService.checkVideoPhoto(request, response, "gift")) {
			return showMessage(request, response, "no_privilege_videophoto");
		}
		switch (cpService.checkNewUser(request, response)) {
			case 1:
				break;
			case 2:
				return showMessage(request, response, "no_privilege_newusertime", "", 1, String.valueOf(sConfig.get("newusertime")));
			case 3:
				return showMessage(request, response, "no_privilege_avatar");
			case 4:
				return showMessage(request, response, "no_privilege_friendnum", "", 1, String.valueOf(sConfig.get("need_friendnum")));
			case 5:
				return showMessage(request, response, "no_privilege_email");
		}
		try {
			if(submitCheck(request, "giftsubmit")) {
				int waitTime = Common.checkInterval(request, response, "post");
				if (waitTime > 0) {
					return showMessage(request, response, "operating_too_fast", "", 1, String.valueOf(waitTime));
				}
				if (Common.checkPerm(request, response, "seccode") && !cpService.checkSeccode(request, response, sGlobal, sConfig, request.getParameter("seccode"))) {
					return showMessage(request, response, "incorrect_code");
				}
				
				
				String giftid = request.getParameter("giftid");
				String username = request.getParameter("username");
				if(giftid == null) {
					return showMessage(request, response, "gift_no_selected");
				}
				if(username == null) {
					return showMessage(request, response, "gift_no_selected_receiver");
				}
				List<Map<String, Object>> giftList = dataBaseService.executeQuery("SELECT price, typeid FROM "+JavaCenterHome.getTableName("gift")+" WHERE giftid='"+giftid+"'");
				if(giftList.size() == 0) {
					return showMessage(request, response, "gift_not_exist");
				}
				List<Map<String, Object>> receiverList = dataBaseService.executeQuery("SELECT uid,name,username FROM "+JavaCenterHome.getTableName("space")+" WHERE username IN ("+Common.sImplode(username.split(","))+")");
				int recSize = receiverList.size();
				if(recSize == 0) {
					return showMessage(request, response, "gift_user_do_not_exist");
				}
				int supe_uid=(Integer) sGlobal.get("supe_uid");
				if(recSize==1){
					int toUid=(Integer)receiverList.get(0).get("uid");
					if (toUid== supe_uid) {
						return showMessage(request, response, "not_to_their_own_gift_send");
					}
					if (cpService.isBlackList(toUid, supe_uid) != 0) {
						return showMessage(request, response, "is_blacklist");
					}
				}else{
					Iterator<Map<String, Object>> i=receiverList.iterator();
					while(i.hasNext()){
						Map<String, Object> receiver=i.next();
						if ((Integer)receiver.get("uid")== supe_uid || cpService.isBlackList((Integer)receiver.get("uid"), supe_uid)!= 0) {
							i.remove();
							receiverList.remove(receiver);
						}
					}
					recSize = receiverList.size();
				}
				String giftType = (String) giftList.get(0).get("typeid");
				int giftPrice = (Integer) giftList.get(0).get("price");
				
				if(giftType.equals("advGift")) {
					int advgiftcount = (Integer) ((Map) sGlobal.get("member")).get("advgiftcount");
					if(advgiftcount == 0) {
						return showMessage(request, response, "gift_can_not_send_adv");
					}
					dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("space")+" SET advgiftcount=advgiftcount-1 WHERE uid='"+sGlobal.get("supe_uid")+"'");
				} else if(giftPrice > 0) {
					
					
					
				}
				
				Integer[] receiverIds = new Integer[recSize];
				String[] receivers = new String[recSize];
				
				List<String> insDatasReceived = new ArrayList<String>();
				List<String> insDatasSent = new ArrayList<String>();
				String sender = (String) ((Map) sGlobal.get("member")).get("name");
				if(Common.empty(sender)) {
					sender = (String) sGlobal.get("supe_username");
				}
				for(int i = 0; i < recSize; i++) {
					Map<String, Object> user = receiverList.get(i);
					receiverIds[i] = (Integer) user.get("uid");
					if(Common.empty(user.get("name"))) {
						receivers[i] = String.valueOf(user.get("username"));
					} else {
						receivers[i] = String.valueOf(user.get("name"));
					}
					
					String insDataReceived = "('"+sGlobal.get("supe_uid")+"', '"+sender+"','"+receiverIds[i]+"', '"+receivers[i]+"', '"+giftid+"', '"+Common.intval(request.getParameter("quiet"))+"','"+Common.intval(request.getParameter("anonymous"))+"', '1' ";
					String insDataSent = "('"+sGlobal.get("supe_uid")+"', '"+sender+"','"+receiverIds[i]+"', '"+receivers[i]+"', '"+giftid+"', '"+Common.intval(request.getParameter("quiet"))+"','"+Common.intval(request.getParameter("anonymous"))+"' ";
					if(request.getParameter("timed") != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
						insDataReceived += ",'1','1','"+sdf.parse(request.getParameter("month")+request.getParameter("day")+request.getParameter("hour")+request.getParameter("minute"))+"')";
						insDataSent += ",'1','"+sdf.parse(request.getParameter("month")+request.getParameter("day")+request.getParameter("hour")+request.getParameter("minute"))+"')";
					} else {
						insDataReceived += ",'0','0','"+sGlobal.get("timestamp")+"')";
						insDataSent += ",'0','"+sGlobal.get("timestamp")+"')";
					}
					insDatasReceived.add(insDataReceived);
					insDatasSent.add(insDataSent);
				}
				if(insDatasReceived.size() > 0) {
					dataBaseService.executeUpdate("INSERT INTO "+JavaCenterHome.getTableName("giftreceived")+" (senderid,sender,receiverid,receiver,giftid,quiet,anonymous,status,timed,fee,receipttime) VALUES "+Common.implode(insDatasReceived, ","));
					dataBaseService.executeUpdate("INSERT INTO "+JavaCenterHome.getTableName("giftsent")+" (senderid,sender,receiverid,receiver,giftid,quiet,anonymous,timed,sendtime) VALUES "+Common.implode(insDatasSent, ","));
					dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("space")+" SET giftnum=giftnum+1 WHERE uid IN ("+Common.sImplode(receiverIds)+")");
				}
				boolean isAnonymous = Common.intval(request.getParameter("anonymous")) == 0 ? false : true;
				for(int i = 0; i < receiverIds.length; i++) {
					String message = request.getParameter("message");
					Matcher m = Pattern.compile("(?s)\\[em\\:(\\d+)\\:\\]").matcher(message);
					int mood = m.find() ? Common.intval(m.group(1)) : 0;
					message = Common.getStr(message, 200, true, true, true, 0, 0, request, response);
					message = message.replaceAll("(?is)\\[em:(\\d+):]", "<img src=\"image/face/$1.gif\" class=\"face\">");
					message = message.replaceAll("(?is)\\<br.*?\\>", " ");
					String[] params = {"space.jsp?do=gift&view=got", message};
					String messageKey = isAnonymous ? "gift_note_event_gift_anonymous" : "gift_note_event_gift";
					String note = Common.getMessage(request, messageKey, params);
					cpService.addGiftNotification(request, sGlobal, sConfig, receiverIds[i], "gift", note, false, isAnonymous);
				}
				return showMessage(request, response, "gift_sent_success", "space.jsp?do=gift&view=sent", 2, Common.implode(receivers, ","));
			
			} else if (submitCheck(request, "settingsubmit")) {
				int showlink = Common.intval(request.getParameter("showlink"));
				dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("space")+" SET showgiftlink='"+showlink+"' WHERE uid='"+sGlobal.get("supe_uid")+"'");
				return showMessage(request, response, "do_success", "space.jsp?do=gift&view=setting", 2);
			
			} else if(submitCheck(request, "deletesubmit")) {
				String id = request.getParameter("id");
				String delType = request.getParameter("deltype");
				if(id != null) {
					if("sent".equals(delType)) {
						dataBaseService.executeUpdate("DELETE FROM "+JavaCenterHome.getTableName("giftsent")+" WHERE gsid='"+id+"'");
					}
					else {
						dataBaseService.executeUpdate("DELETE FROM "+JavaCenterHome.getTableName("giftreceived")+" WHERE grid='"+id+"'");
						dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("space")+" SET giftnum=giftnum-1 WHERE uid ='"+sGlobal.get("supe_uid")+"'");
					}
				}
				return showMessage(request, response, "do_success", "space.jsp?do=gift&view="+delType, 2);
			}
		} catch(Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		String giftType = request.getParameter("type");
		if(giftType == null) {
			giftType = "defGift";
		}
		if (!Common.empty(space.get("friendnum"))) {
			List<Map<String, Object>> friends = dataBaseService.executeQuery("SELECT fuid AS uid, fusername AS username FROM "+JavaCenterHome.getTableName("friend")+" WHERE uid="+sGlobal.get("supe_uid")+" AND status='1' ORDER BY num DESC, dateline DESC LIMIT 0, 100");
			List fNamee = new ArrayList(friends.size());
			for (Map<String, Object> value : friends) {
				value.put("username", Common.sAddSlashes(value.get("username")));
				fNamee.add(value.get("username"));
			}

			request.setAttribute("friendstr", Common.implode(fNamee, ","));
			request.setAttribute("friends", friends);
		}
		List<Map<String, Object>> categories = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("gifttype")+" WHERE fee=0 AND typeid<>'feeGift' ORDER BY `order` ASC");
		
		request.setAttribute("categories", categories);
		request.setAttribute("firstcate", giftType);
		request.setAttribute("defreceiver", request.getParameter("defreceiver"));
		return include(request, response, sConfig, sGlobal, "/cp_gift.jsp");
	}
	
	private boolean submitCheckForMulti(HttpServletRequest request, FileUploadUtil upload, String var)
			throws Exception {

		if ("POST".equals(request.getMethod()) && !Common.empty(upload.getParameter(var))) {
			String referer = request.getHeader("Referer");
			if (Common.empty(referer)
					|| referer.replaceAll("https?://([^:/]+).*", "$1").equals(
							request.getHeader("Host").replaceAll("([^:]+).*", "$1"))
					&& formHash(request).equals(upload.getParameter("formhash"))) {
				return true;
			} else {
				throw new Exception("submit_invalid");
			}
		}
		return false;
	}

	private FileUploadUtil getParsedFileUploadUtil(HttpServletRequest request) {
		FileUploadUtil upload = new FileUploadUtil(new File(JavaCenterHome.jchRoot + "./data/temp"), 4096);
		try {
			upload.parse(request, JavaCenterHome.JCH_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return upload;
	}

	
	public ActionForward cp_upload(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");

		PrintWriter out = null;

		FileUploadUtil upload = new FileUploadUtil(new File(JavaCenterHome.jchRoot + "./data/temp"), 4096);
		try {
			out = response.getWriter();
			upload.parse(request, JavaCenterHome.JCH_CHARSET);
		} catch (Exception e) {
			if (e instanceof SizeLimitExceededException) {
				out.print("<script>");
				out.print("parent.uploadStat = '"
						+ Common.getMessage(request, "cp_upload_size_too_big", JavaCenterHome.jchConfig
								.get("upload_max_filesize")) + "';");
				out.print("parent.upload();");
				out.print("</script>");
				out.flush();
				out.close();
				return null;
			}
		}

		int albumID = Common.empty(upload.getParameter("albumid")) ? 0 : Common.intval(upload
				.getParameter("albumid"));
		String eventid = upload.getParameter("eventid");
		eventid = eventid == null ? request.getParameter("eventid") : eventid;
		int eventID = Common.empty(eventid) ? 0 : Common.intval(eventid);
		Map<String, Object> event = null;
		if (eventID != 0) {
			String sql = "SELECT e.*,ef.* FROM " + JavaCenterHome.getTableName("event") + " e LEFT JOIN "
					+ JavaCenterHome.getTableName("eventfield")
					+ " ef ON e.eventid=ef.eventid WHERE e.eventid='" + eventID + "'";
			List<Map<String, Object>> eventList = dataBaseService.executeQuery(sql);
			if (eventList.size() == 0) {
				return showMessage(request, response, "event_does_not_exist");
			}
			event = eventList.get(0);
			int grade = (Integer) event.get("grade");
			if (grade == -2) {
				return showMessage(request, response, "event_is_closed");
			} else if (grade < 1) {
				return showMessage(request, response, "event_under_verify");
			}
			sql = "SELECT * FROM " + JavaCenterHome.getTableName("userevent") + " WHERE uid='"
					+ sGlobal.get("supe_uid") + "' AND eventid='" + eventID + "'";
			List<Map<String, Object>> userEventList = dataBaseService.executeQuery(sql);
			Map<String, Object> userEvent = userEventList.size() > 0 ? userEventList.get(0) : null;
			if (userEvent != null) {
				int allowPic = (Integer) event.get("allowpic");
				int eventStatus = (Integer) userEvent.get("status");
				if (allowPic == 0 && eventStatus < 3) {
					return showMessage(request, response, "event_only_allows_admins_to_upload");
				}
				if (allowPic != 0 && eventStatus < 2) {
					return showMessage(request, response, "event_only_allows_members_to_upload");
				}
			}
		}

		try {
			if (submitCheck(request, upload.getParameter("albumsubmit"), upload.getParameter("formhash"))) {
				if ("creatalbum".equals(upload.getParameter("albumop"))) {
					String albumName = upload.getParameter("albumname");
					if (albumName == null || albumName.length() == 0) {
						albumName = Common.gmdate("yyyyMMdd", (Integer) sGlobal.get("timestamp"),
								(String) sConfig.get("timeoffset"));
					} else {
						albumName = Common.getStr(albumName, 50, true, true, false, 0, 0, request, response);
					}
					int friend = Common.intval(upload.getParameter("friend"));
					String targetIDs = "";
					String password = "";
					if (friend == 2) {
						
						List friendUIDs = new ArrayList();
						String[] names = null;
						String friendNames = upload.getParameter("target_names");
						if (friendNames != null && friendNames.length() != 0) {
							friendNames = friendNames.replaceAll(Common.getMessage(request, "cp_tab_space"),
									" ");
							names = friendNames.split(" ");
						}
						if (names != null) {
							List<Map<String, Object>> values = dataBaseService
									.executeQuery("SELECT uid FROM " + JavaCenterHome.getTableName("space")
											+ " WHERE username IN (" + Common.sImplode(names) + ")");
							for (Map<String, Object> value : values) {
								friendUIDs.add(value.get("uid"));
							}
						}
						if (friendUIDs.size() == 0) {
							friend = 3;
						} else {
							targetIDs = Common.implode(friendUIDs, ",");
						}
					} else if (friend == 4) {
						password = upload.getParameter("password");
						if (password == null || password.trim().length() == 0) {
							friend = 0;
						}
					}
					Map setarr = new HashMap();
					setarr.put("albumname", albumName);
					setarr.put("uid", sGlobal.get("supe_uid"));
					setarr.put("username", sGlobal.get("supe_username"));
					setarr.put("dateline", sGlobal.get("timestamp"));
					setarr.put("updatetime", sGlobal.get("timestamp"));
					setarr.put("friend", friend);
					setarr.put("password", password);
					setarr.put("target_ids", targetIDs);
					albumID = dataBaseService.insertTable("album", setarr, true, false);
					Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
					String albumNumSQL = null;
					if (Common.empty(space.get("albumnum"))) {
						Map wherearr = new HashMap();
						wherearr.put("uid", space.get("uid"));
						space.put("albumnum", Common.getCount("album", wherearr, null));
						albumNumSQL = "albumnum=" + space.get("albumnum");
					} else {
						albumNumSQL = "albumnum=albumnum+1";
					}
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET "
							+ albumNumSQL + ",updatetime='" + sGlobal.get("timestamp") + "' WHERE uid='"
							+ sGlobal.get("supe_uid") + "'");
				}

				int topicID = Common.intval(upload.getParameter("topicid"));
				topicID = cpService.checkTopic(request, topicID, "pic");
				if (Common.empty(sGlobal.get("mobile"))) {
					out = response.getWriter();
					out.print("<script>");
					out.print("parent.no_insert = 1;");
					out.print("parent.albumid = " + albumID + ";");
					out.print("parent.topicid = " + topicID + ";");
					out.print("parent.start_upload();");
					out.print("</script>");
					out.flush();
					out.close();
					return null;
				} else {
					return showMessage(request, response, "do_success", "cp.jsp?ac=upload");
				}
			} else if (submitCheck(request, upload.getParameter("uploadsubmit"), upload
					.getParameter("formhash"))) {
				if (!Common.checkPerm(request, response, "allowupload")) {
					if (Common.empty(sGlobal.get("mobile"))) {
						out = response.getWriter();
						out.print("<script>");
						out.print("alert(\"" + Common.getMessage(request, "cp_not_allow_upload") + "\");");
						out.print("</script>");
						return null;
					} else {
						return showMessage(request, response, Common.getMessage(request,
								"cp_not_allow_upload"));
					}
				}
				int picID = 0;
				int topicID = Common.intval(upload.getParameter("topicid"));
				topicID = cpService.checkTopic(request, topicID, "pic");
				String title = upload.getParameter("pic_title");
				Object uploadFiles = cpService.savePic(request, response, upload.getFileItem("attach"),
						upload.getParameter("albumid"), title, topicID);
				String uploadStat = "1";
				boolean tempB = !Common.empty(uploadFiles);
				boolean tempBB = Common.isArray(uploadFiles);
				if (tempB && tempBB) {
					Map<String, Object> map = (Map<String, Object>) uploadFiles;
					albumID = (Integer) map.get("albumid");
					picID = (Integer) map.get("picid");
					if (eventID != 0) {
						Map<String, Object> arr = new HashMap<String, Object>();
						arr.put("eventid", eventID);
						arr.put("picid", picID);
						arr.put("uid", sGlobal.get("supe_uid"));
						arr.put("username", sGlobal.get("supe_username"));
						arr.put("dateline", sGlobal.get("timestamp"));
						try {
							dataBaseService.insertTable("eventpic", arr, false, false);
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				} else {
					uploadStat = (String) uploadFiles;
				}

				if (!Common.empty(sGlobal.get("mobile"))) {
					if (picID != 0) {
						return showMessage(request, response, "do_success", "space.jsp?do=album&picid="
								+ picID);
					} else {
						return showMessage(request, response, uploadStat, "cp.jsp?ac=upload");
					}
				} else {
					out = response.getWriter();
					out.print("<script>");
					out.print("parent.albumid = " + albumID + ";");
					out.print("parent.topicid = " + topicID + ";");
					out.print("parent.uploadStat = '" + uploadStat + "';");
					out.print("parent.picid = " + picID + ";");
					out.print("parent.upload();");
					out.print("</script>");
					out.flush();
					out.close();
				}
				return null;
			} else if (submitCheck(request, upload.getParameter("viewAlbumid"), upload
					.getParameter("formhash"))) {
				if (eventID != 0) {
					int dateline = (Integer) sGlobal.get("timestamp") - 600;
					List<Map<String, Object>> values = dataBaseService.executeQuery("SELECT pic.* FROM "
							+ JavaCenterHome.getTableName("eventpic")
							+ " ep LEFT JOIN "
							+ JavaCenterHome.getTableName("pic")
							+ " pic ON ep.picid=pic.picid WHERE ep.uid='"
							+ sGlobal.get("supe_uid") + "' AND ep.eventid='" + eventID
							+ "' AND ep.dateline > " + dateline + " ORDER BY ep.dateline DESC LIMIT 4");
					String[] imgs = new String[values.size()];
					String[] imglinks = new String[values.size()];
					Map<String, Object> value = null;
					for (int i = 0; i < values.size(); i++) {
						value = values.get(i);
						imgs[i] = Common.pic_get(sConfig, (String) value.get("filepath"), (Integer) value
								.get("thumb"), (Integer) value.get("remote"), true);
						imglinks[i] = "space.jsp?do=event&id=" + eventID + "&view=pic&picid="
								+ value.get("picid");
					}
					int picNum = 0;
					if (imgs.length > 0) {
						picNum = dataBaseService.findRows("SELECT COUNT(*) FROM "
								+ JavaCenterHome.getTableName("eventpic") + " WHERE eventid='" + eventID
								+ "'");
						Map bodyData = new HashMap();
						bodyData.put("eventid", eventID);
						bodyData.put("title", event.get("title"));
						bodyData.put("picnum", picNum);
						cpService.addFeed(sGlobal, "event", Common.getMessage(request,
								"cp_event_feed_share_pic_title"), null, Common.getMessage(request,
								"cp_event_feed_share_pic_info"), bodyData, "", imgs, imglinks, "", 0, 0, 0,
								"", false);
					}
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("event")
							+ " SET picnum='" + picNum + "',updatetime='" + sGlobal.get("timestamp")
							+ "' WHERE eventid='" + eventID + "'");
					return showMessage(request, response, "do_success", "space.jsp?do=event&view=pic&id="
							+ eventID, 0);
				} else {
					Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
					if (Common.ckPrivacy(sGlobal, sConfig, space, "upload", 1)) {
						feedService.feedPublish(request, response, Common.intval(request
								.getParameter("opalbumid")), "albumid", false);
					}
					String url;
					int topicID = Common.intval(request.getParameter("topicid"));
					if (topicID != 0) {
						cpService.topicJoin(request, topicID, (Integer) sGlobal.get("supe_uid"),
								(String) sGlobal.get("supe_username"));
						url = "space.jsp?do=topic&topicid=" + topicID + "&view=pic";
					} else {
						url = "space.jsp?uid="
								+ sGlobal.get("supe_uid")
								+ "&do=album&id="
								+ (Common.empty(request.getParameter("opalbumid")) ? -1 : request
										.getParameter("opalbumid"));
					}
					return showMessage(request, response, "upload_images_completed", url, 0);
				}
			} else {
				Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
				if (!Common.checkPerm(request, response, "allowupload")) {
					MessageVO msgVO = Common.ckSpaceLog(request);
					if (msgVO != null) {
						return showMessage(request, response, msgVO);
					}
					return showMessage(request, response, "no_privilege");
				}
				if (!cpService.checkRealName(request, "album")) {
					return showMessage(request, response, "no_privilege_realname");
				}
				if (!cpService.checkVideoPhoto(request, response, "album")) {
					return showMessage(request, response, "no_privilege_videophoto");
				}
				int result = cpService.checkNewUser(request, response);
				switch (result) {
					case 1:
						break;
					case 2:
						return showMessage(request, response, "no_privilege_newusertime", "", 1, String
								.valueOf(sConfig.get("newusertime")));
					case 3:
						return showMessage(request, response, "no_privilege_avatar");
					case 4:
						return showMessage(request, response, "no_privilege_friendnum", "", 1, String
								.valueOf(sConfig.get("need_friendnum")));
					case 5:
						return showMessage(request, response, "no_privilege_email");
				}
				String siteURL = Common.getSiteUrl(request);
				List<Map<String, Object>> albums = cpService.getAlbums((Integer) sGlobal.get("supe_uid"));
				String haveAttachSize = null;
				int maxAttachSize = (Integer) Common.checkPerm(request, response, sGlobal, "maxattachsize");
				if (maxAttachSize != 0) {
					maxAttachSize += (Integer) space.get("addsize");
					haveAttachSize = Common.formatSize(maxAttachSize - (Integer) space.get("attachsize"));
				} else {
					haveAttachSize = "0";
				}
				Map<String, String> actives;
				String activeKey = upload.getParameter("op");
				if ("flash".equals(activeKey) || "cam".equals(activeKey)) {
					actives = new HashMap<String, String>();
					actives.put(activeKey, " class=\"active\"");
				} else {
					actives = new HashMap<String, String>();
					actives.put("js", " class=\"active\"");
				}
				int topicID = Common.intval(upload.getParameter("topicid"));
				Map<String, Object> topic = topicID == 0 ? new HashMap<String, Object>() : Common.getTopic(
						request, topicID);
				if (topic.size() != 0) {
					actives = new HashMap<String, String>();
					actives.put("upload", " class=\"active\"");
					request.setAttribute("perm", Common.checkPerm(request, response, "managetopic"));
				}
				request.setAttribute("siteurl", siteURL);
				request.setAttribute("albums", albums);
				request.setAttribute("haveattachsize", haveAttachSize);
				request.setAttribute("groups", Common.getFriendGroup(request));
				request.setAttribute("topic", topic);
				request.setAttribute("topicid", topicID);
				request.setAttribute("actives", actives);
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		request.setAttribute("event", event);
		request.setAttribute("eventid", eventID);
		request.setAttribute("albumid", albumID);
		request.setAttribute("formhash", formHash(request));
		return include(request, response, sConfig, sGlobal, "cp_upload.jsp");
	}

	public ActionForward cp_userapp(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		return include(request, response, sConfig, sGlobal, "cp_userapp.jsp");
	}

	public ActionForward cp_videophoto(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		if (Common.empty(sConfig.get("videophoto"))) {
			return showMessage(request, response, "no_open_videophoto");
		}
		String videoPic = (String) space.get("videopic");
		int videoStatus = (Integer) space.get("videostatus");
		String oldVideoPhoto = null;
		if (!Common.empty(videoPic)) {
			oldVideoPhoto = cpService.getVideoPic(videoPic);
			request.setAttribute("videophoto", oldVideoPhoto);
		}
		try {
			if (submitCheck(request, "uploadsubmit")) {
				ServletInputStream sis = null;
				FileOutputStream fos = null;
				PrintWriter out = null;
				try {
					response.setHeader("Expires", "0");
					response.setHeader("Cache-Control",
							"no-store, private, post-check=0, pre-check=0, max-age=0");
					response.setHeader("Pragma", "no-cache");
					response.setContentType("text/html");
					out = response.getWriter();
					if (!Common.empty(videoStatus) && Common.empty(sConfig.get("videophotochange"))) {
						out.write("-1");
						return null;
					}
					if (videoStatus == 0 && !Common.empty(videoPic)) {
						out.write("-2");
						return null;
					}
					int uid = (Integer) sGlobal.get("supe_uid");
					int timestamp = (Integer) sGlobal.get("timestamp");
					String newFileName = Common.md5(String.valueOf(timestamp).substring(0, 7) + uid);
					String jchRoot = JavaCenterHome.jchRoot + "/";
					File file = new File(jchRoot + "data/video/" + newFileName.substring(0, 1) + "/"
							+ newFileName.substring(1, 2));
					if (!file.exists() && !file.isDirectory() && !file.mkdirs()) {
						out.write("Can not write to the data/video folder!");
						return null;
					}
					if (oldVideoPhoto != null) {
						file = new File(jchRoot + oldVideoPhoto);
						if (file.exists())
							file.delete();
					}
					sis = request.getInputStream();
					fos = new FileOutputStream(jchRoot + cpService.getVideoPic(newFileName));
					byte[] buffer = new byte[256];
					int count = 0;
					while ((count = sis.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
					boolean videoPhotoCheck = Common.empty(sConfig.get("videophotocheck"));
					videoStatus = videoPhotoCheck ? 1 : 0;
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("spacefield")
							+ " SET videopic='" + newFileName + "' WHERE uid='" + uid + "'");
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET videostatus='" + videoStatus + "' WHERE uid='" + uid + "'");
					List<String> sets = new ArrayList<String>();
					Map<String, Integer> reward = Common.getReward("videophoto", false, 0, "", true, request,
							response);
					int credit = reward.get("credit");
					int experience = reward.get("experience");
					if (credit != 0) {
						sets.add("credit=credit+" + credit);
					}
					if (experience != 0) {
						sets.add("experience=experience+" + experience);
					}
					sets.add("updatetime=" + timestamp);
					if (sets.size() > 0) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
								+ " SET " + Common.implode(sets, ",") + " WHERE uid='" + uid + "'");
					}
					if (videoPhotoCheck) {
						out.write("2");
					} else {
						out.write("1");
					}
					return null;
				} catch (Exception e) {
					out.write("Upload an exception occurred during the");
					return null;
				} finally {
					try {
						if (fos != null) {
							fos.flush();
							fos.close();
							fos = null;
						}
						if (sis != null) {
							sis.close();
							sis = null;
						}
						if (out != null) {
							out.flush();
							out.close();
							out = null;
						}
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		String op = request.getParameter("op");
		if ("check".equals(op)) {
			if ((videoStatus > 0 && Common.empty(sConfig.get("videophotochange")))
					|| (videoStatus == 0 && !Common.empty(videoPic))) {
				request.getParameterMap().remove("op");
			} else {
				String flashSrc = "image/videophoto.swf?post_url="
						+ Common.urlEncode(Common.getSiteUrl(request) + "cp.jsp")
						+ "&agrs="
						+ Common.urlEncode("ac=videophoto&uid=" + sGlobal.get("supe_uid")
								+ "&uploadsubmit=true&formhash=" + formHash(request));
				String videoFlash = "<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,0,0\" width=\"560\" height=\"390\" id=\"videoCheck\" align=\"middle\">"
						+ "<param name=\"allowScriptAccess\" value=\"always\" />"
						+ "<param name=\"scale\" value=\"exactfit\" />"
						+ "<param name=\"wmode\" value=\"transparent\" />"
						+ "<param name=\"quality\" value=\"high\" />"
						+ "<param name=\"bgcolor\" value=\"#ffffff\" />"
						+ "<param name=\"movie\" value=\""
						+ flashSrc
						+ "\" />"
						+ "<param name=\"menu\" value=\"false\" />"
						+ "<embed src=\""
						+ flashSrc
						+ "\" quality=\"high\" bgcolor=\"#ffffff\" width=\"560\" height=\"390\" name=\"videoCheck\" align=\"middle\" allowScriptAccess=\"always\" allowFullScreen=\"false\" scale=\"exactfit\"  wmode=\"transparent\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" />"
						+ "</object>";
				request.setAttribute("videoFlash", videoFlash);
			}
		}
		return include(request, response, sConfig, sGlobal, "cp_videophoto.jsp");
	}

	
	private ActionForward executeTask(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> task, Map<String, Object> space) {
		request.setAttribute("task", task);
		request.setAttribute("space", space);
		request.setAttribute("cpService", cpService);
		request.setAttribute("dataBaseService", dataBaseService);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/source/task/"
				+ ((String) task.get("filename")));
		try {
			dispatcher.include(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			return showMessage(request, response, e.getMessage());
		}
		return null;
	}
	private String[] getArrayIntersect(String[] s1, String[] s2) {
		String[] result = {};
		if (s1 == null || s2 == null || s1.length == 0 || s2.length == 0) {
			return result;
		}
		List<String> list = new ArrayList<String>(s1.length > s2.length ? s1.length : s2.length);
		for (String str1 : s1) {
			for (String str2 : s2) {
				if (str1.equals(str2)) {
					list.add(str1);
				}
			}
		}
		return list.toArray(result);
	}
	private String[] arrayMerge(Object[] obj1, Object[] obj2) {
		String[] result = {};
		Set<String> set = new HashSet<String>(obj1.length + obj2.length);
		for (Object o1 : obj1) {
			set.add(o1.toString());
		}
		for (Object o2 : obj2) {
			set.add(o2.toString());
		}
		return set.toArray(result);
	}

	private void createMail(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sConfig, Map<Integer, String> sNames, Map<String, Object> space, String mail,
			String[] mailArgs, Map appInfo) {
		try {
			mailArgs[3] = Common.getStr(request.getParameter("saymsg"), 500, false, false, false, 0, 0,
					request, response);
			String subject = null;
			if (appInfo != null) {
				subject = Common.getMessage(request, "cp_app_invite_subject", new String[] {
						sNames.get(space.get("uid")), (String) sConfig.get("sitename"),
						(String) appInfo.get("appname")});
			} else {
				subject = Common.getMessage(request, "cp_invite_subject", new String[] {
						sNames.get(space.get("uid")), (String) sConfig.get("sitename")});
			}
			String message = Common.getMessage(request, (appInfo != null ? "cp_app_invite_massage"
					: "cp_invite_massage"), mailArgs);
			cpService.sendMail(request, response, 0, mail, subject, message, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Map<String, Object> checkMtagSpace(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> event, Map<String, Object> userEvent, int tagId) {
		Map<String, Object> mtag = null;
		int supe_uid = (Integer) ((Map) request.getAttribute("sGlobal")).get("supe_uid");
		if (!Common.empty(event)) {
			if (Common.empty(userEvent) || (Integer) userEvent.get("status") < 2) {
				showMessage(request, response, "event_only_allows_member_thread");
				return null;
			}
			if ((Integer) event.get("tagid") != tagId) {
				showMessage(request, response, "event_mtag_not_match");
				return null;
			}
			try {
				mtag = Common.getMtag(request, response, supe_uid, tagId);
			} catch (Exception e) {
				showMessage(request, response, e.getMessage());
				return null;
			}
			if (!Common.empty(mtag.get("close"))) {
				showMessage(request, response, "mtag_close");
				return null;
			}
			return mtag;
		}
		if (tagId != 0) {
			try {
				mtag = Common.getMtag(request, response, supe_uid, tagId);
			} catch (Exception e) {
				showMessage(request, response, e.getMessage());
				return null;
			}
			if (mtag != null) {
				if (!Common.empty(mtag.get("close"))) {
					showMessage(request, response, "mtag_close");
					return null;
				}
				if (Common.empty(mtag.get("allowview"))) {
					showMessage(request, response, "mtag_not_allow_to_do");
					return null;
				}
				Map<String, Object> field = (Map<String, Object>) mtag.get("field");
				if (!Common.empty(field.get("mtagminnum"))
						&& (Integer) mtag.get("membernum") < (Integer) field.get("mtagminnum")) {
					showMessage(request, response, "mtag_minnum_erro", null, 1, field.get("mtagminnum")
							.toString());
					return null;
				}
			}
		}
		if (Common.empty(mtag)) {
			showMessage(request, response, "first_select_a_mtag");
			return null;
		}
		return mtag;
	}

	private List<String> getKeyWord(String text) throws IOException {
		List<String> keywords = new ArrayList<String>();
		if (!Common.empty(text)) {
			Map<String, Integer> words = new HashMap<String, Integer>();
			Analyzer analyzer = new IKAnalyzer(true);
			StringReader reader = new StringReader(text);
			TokenStream tokenStream = analyzer.tokenStream("*", reader);
			TermAttribute termAtt = (TermAttribute) tokenStream.getAttribute(TermAttribute.class);
			while (tokenStream.incrementToken()) {
				String word = termAtt.term();
				if (word.length() > 1 && Common.strlen(word) > 2) {
					Integer count = words.get(word);
					if (count == null) {
						count = 0;
					}
					words.put(word, count + 1);
				}
			}
			if (words.size() > 0) {
				Directory dir = null;
				IndexSearcher searcher = null;
				try {
					String fieldName = "text";
					dir = new RAMDirectory();
					IndexWriter writer = new IndexWriter(dir, analyzer, true,
							IndexWriter.MaxFieldLength.LIMITED);
					Document doc = new Document();
					doc.add(new Field(fieldName, text, Field.Store.YES, Field.Index.ANALYZED));
					writer.addDocument(doc);
					writer.close();

					searcher = new IndexSearcher(dir);
					searcher.setSimilarity(new IKSimilarity());
					Set<String> keys = words.keySet();
					Map<String, Float> temps = new HashMap<String, Float>();
					for (String key : keys) {
						int count = words.get(key);
						Query query = IKQueryParser.parse(fieldName, key);
						TopDocs topDocs = searcher.search(query, 1);
						if (topDocs.totalHits > 0) {
							temps.put(key, topDocs.getMaxScore() * count);
						}
					}
					Entry<String, Float>[] keywordEntry = getSortedHashtableByValue(temps);
					for (Entry<String, Float> entry : keywordEntry) {
						if (keywords.size() < 5) {
							keywords.add(entry.getKey());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						searcher.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						dir.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return keywords;
	}

	
	@SuppressWarnings("unchecked")
	private Entry<String, Float>[] getSortedHashtableByValue(Map<String, Float> h) {
		Set<Entry<String, Float>> set = h.entrySet();
		Entry<String, Float>[] entries = set.toArray(new Entry[set.size()]);
		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Entry entry1 = (Entry) arg0;
				Entry entry2 = (Entry) arg1;
				Float value1 = (Float) entry1.getValue();
				Float value2 = (Float) entry2.getValue();
				int size = value2.compareTo(value1);
				if (size == 0) {
					String key1 = (String) entry1.getKey();
					String key2 = (String) entry2.getKey();
					return key1.compareTo(key2);
				}
				return size;
			}
		});
		return entries;
	}
}