package cn.jcenterhome.web.action.admin;

import java.text.SimpleDateFormat;
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


public class ReportAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		boolean allowmanage = Common.checkPerm(request, response, "managereport");
		if (!allowmanage) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		try {
			if (submitCheck(request, "listsubmit")) {
				String[] ids = request.getParameterValues("ids");
				if (ids != null) {
					int optype = Common.intval(request.getParameter("optype"));
					if (optype == 1) {
						dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("report")
								+ " SET num=0 WHERE rid IN (" + Common.sImplode(ids) + ")");
					} else {
						if (optype == 3) {
							deleteInfo(request, response, sGlobal, ids);
						}
						dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
								+ " WHERE rid IN (" + Common.sImplode(ids) + ")");
					}
					return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
				} else {
					return cpMessage(request, mapping, "choosing_to_operate_the_report");
				}
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		String op = request.getParameter("op");
		if (op != null) {
			int rid = Common.intval(request.getParameter("rid"));
			if (rid == 0) {
				return cpMessage(request, mapping, "cp_the_right_to_report_the_specified_id",
						"admincp.jsp?ac=report");
			}
			if ("delete".equals(op)) {
				if ("delinfo".equals(request.getParameter("subop"))) {
					try {
						deleteInfo(request, response, sGlobal, rid);
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
				}
				dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
						+ " WHERE rid=" + rid);
			} else if ("ignore".equals(op)) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("report")
						+ " SET num=0 WHERE rid=" + rid);
			}
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=report");
		}
		String status = request.getParameter("status");
		Map<String, String[]> paramMap = request.getParameterMap();
		String[] intKeys = null;
		if (status == null || "".equals(status) || Common.intval(status) == 1) {
			paramMap.put("num1", new String[] { "1" });
			paramMap.put("status", new String[] { "1" });
		} else if (Common.intval(status) == 0) {
			paramMap.put("num", new String[] { "0" });
			paramMap.put("num1", new String[] { "0" });
			intKeys = new String[] { "num" };
		}

		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=report");
		String[] strkeys = new String[] { "idtype" };
		List<String[]> randkeys = new ArrayList<String[]>();
		randkeys.add(new String[] { "intval", "num" });
		Map<String, String> wheres = getWheres(intKeys, strkeys, randkeys, null, "", paramMap, null);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpurl.append(wheres.get("url"));

		request.setAttribute("active_" + request.getParameter("status"), " class='active'");
		Map<String, String> orders = getOrders(new String[] { "dateline", "num" }, "new,num", null, paramMap);
		String ordersql = orders.get("sql");
		mpurl.append(orders.get("url"));
		request.setAttribute("orderby_" + request.getParameter("orderby"), " selected");
		request.setAttribute("ordersc_" + request.getParameter("ordersc"), " selected");

		request.setAttribute("scstr", "asc".equals(request.getParameter("ordersc")) ? "desc" : "asc");
		int perpage = Common.intval(request.getParameter("perpage"));
		if (!Common.in_array(new Integer[] { 20, 50, 100, 1000 }, perpage)) {
			perpage = 20;
		}
		int page = Math.max(Common.intval(request.getParameter("page")), 1);
		int start = (page - 1) * perpage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perpage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		int count = 1;
		String selectsql = null;
		if (perpage > 100) {
			selectsql = "rid";
		} else {
			count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("report")
					+ " WHERE " + whereSQL);
			selectsql = "*";
		}
		mpurl.append("&perpage=" + perpage);
		request.setAttribute("perpage_" + perpage, " selected");

		if (count > 0) {
			List<Map<String, Object>> reportList = dataBaseService.executeQuery("SELECT " + selectsql
					+ " FROM " + JavaCenterHome.getTableName("report") + " WHERE " + whereSQL + " "
					+ ordersql + " LIMIT " + start + "," + perpage);
			request.setAttribute("multi", Common.multi(request, count, perpage, page, maxPage, mpurl
					.toString(), null, null));
			if (perpage > 100) {
				count = reportList.size();
				request.setAttribute("list", reportList);
			} else {
				String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
				SimpleDateFormat reportSDF = Common.getSimpleDateFormat("yyyy-MM-dd", timeoffset);
				List<Integer> readIds = new ArrayList<Integer>();
				Map<String, Integer> emptyIds = new HashMap<String, Integer>();
				Map<String, List<Integer>> ids = new LinkedHashMap<String, List<Integer>>();
				Map<String, Map<Integer, Map<String, Object>>> list = new LinkedHashMap<String, Map<Integer, Map<String, Object>>>();
				for (Map<String, Object> report : reportList) {
					int rid = (Integer) report.get("rid");
					int id = (Integer) report.get("id");
					String idType = (String) report.get("idtype");
					String infoId = idType + id;
					report.put("dateline", Common.gmdate(reportSDF, (Integer) report.get("dateline")));
					report.put("user", Serializer.unserialize((String) report.get("uids"), false));
					report.put("infoId", infoId);
					emptyIds.put(infoId, rid);
					if ((Integer) report.get("new") > 0) {
						readIds.add(rid);
					}
					List<Integer> temp = ids.get(idType);
					if (temp == null) {
						temp = new ArrayList<Integer>();
						ids.put(idType, temp);
					}
					temp.add(id);
					Map<Integer, Map<String, Object>> blogs = list.get(idType);
					if (blogs == null) {
						blogs = new LinkedHashMap<Integer, Map<String, Object>>();
						list.put(idType, blogs);
					}
					blogs.put(id, report);
				}
				if (readIds.size() > 0) {
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("report")
							+ " SET new=0 WHERE rid IN(" + Common.implode(readIds, ",") + ")");
				}
				List<Map<String, Object>> temps;
				Set<String> idTypes = ids.keySet();
				for (String idType : idTypes) {
					String newIds = Common.sImplode(ids.get(idType));
					if ("blogid".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("blog") + " WHERE blogid IN (" + newIds + ")");
						for (Map<String, Object> blog : temps) {
							int blogId = (Integer) blog.get("blogid");
							list.get(idType).get(blogId).put("info", blog);
							emptyIds.remove(idType + blogId);
						}
					} else if ("picid".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("pic") + " WHERE picid IN (" + newIds + ")");
						for (Map<String, Object> pic : temps) {
							int picId = (Integer) pic.get("picid");
							pic.put("pic", Common.pic_get(sConfig, (String) pic.get("filepath"),
									(Integer) pic.get("thumb"), (Integer) pic.get("remote"), true));
							list.get(idType).get(picId).put("info", pic);
							emptyIds.remove(idType + picId);
						}
					} else if ("albumid".equals(idType)) {
						temps = dataBaseService
								.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("album")
										+ " WHERE albumid IN (" + newIds + ")");
						for (Map<String, Object> album : temps) {
							int albumId = (Integer) album.get("albumid");
							album.put("pic", Common.pic_cover_get(sConfig, (String) album.get("pic"),
									(Integer) album.get("picflag")));
							list.get(idType).get(albumId).put("info", album);
							emptyIds.remove(idType + albumId);
						}
					} else if ("tid".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("thread") + " WHERE tid IN (" + newIds + ")");
						for (Map<String, Object> thread : temps) {
							int tid = (Integer) thread.get("tid");
							list.get(idType).get(tid).put("info", thread);
							emptyIds.remove(idType + tid);
						}
					} else if ("tagid".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("mtag") + " WHERE tagid IN (" + newIds + ")");
						for (Map<String, Object> mtag : temps) {
							int tagId = (Integer) mtag.get("tagid");
							list.get(idType).get(tagId).put("info", mtag);
							emptyIds.remove(idType + tagId);
						}
					} else if ("sid".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("share") + " WHERE sid IN (" + newIds + ")");
						SimpleDateFormat shareSDF = Common
								.getSimpleDateFormat("yyyy-MM-dd HH:mm", timeoffset);
						for (Map<String, Object> share : temps) {
							Common.mkShare(share);
							int sid = (Integer) share.get("sid");
							share.put("dateline", Common.gmdate(shareSDF, (Integer) share.get("dateline")));
							list.get(idType).get(sid).put("info", share);
							emptyIds.remove(idType + sid);
						}
					} else if ("uid".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("space") + " WHERE uid IN (" + newIds + ")");
						for (Map<String, Object> space : temps) {
							int uid = (Integer) space.get("uid");
							space.put("avatar", Common.avatar(uid, "middle", false, sGlobal, sConfig));
							list.get(idType).get(uid).put("info", space);
							emptyIds.remove(idType + uid);
						}
					} else if ("eventid".equals(idType)) {
						temps = dataBaseService
								.executeQuery("SELECT * FROM " + JavaCenterHome.getTableName("event")
										+ " WHERE eventid IN (" + newIds + ")");
						for (Map<String, Object> event : temps) {
							int eventId = (Integer) event.get("eventid");
							list.get(idType).get(eventId).put("info", event);
							emptyIds.remove(idType + eventId);
						}
					} else if ("pid".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("poll") + " WHERE pid IN (" + newIds + ")");
						for (Map<String, Object> poll : temps) {
							int pid = (Integer) poll.get("pid");
							list.get(idType).get(pid).put("info", poll);
							emptyIds.remove(idType + pid);
						}
					} else if ("comment".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("comment") + " WHERE cid IN (" + newIds + ")");
						try {
							for (Map<String, Object> comment : temps) {
								int id = (Integer) comment.get("id");
								int cid = (Integer) comment.get("cid");
								StringBuffer url = new StringBuffer();
								url.append("space.jsp?uid=" + comment.get("uid") + "&do=");
								String commentIdType = (String) comment.get("idtype");
								if ("uid".equals(commentIdType)) {
									url.append("wall&view=me&cid=" + cid);
								} else if ("picid".equals(commentIdType)) {
									url.append("album&picid=" + id + "&cid=" + cid);
								} else if ("blogid".equals(commentIdType)) {
									url.append("blog&id=" + id + "&cid=" + cid);
								} else if ("sid".equals(commentIdType)) {
									url.append("share&id=" + id + "&cid=" + cid);
								} else if ("pid".equals(commentIdType)) {
									url.append("poll&pid=" + id + "&cid=" + cid);
								} else if ("eventid".equals(commentIdType)) {
									url.append("event&id=" + id + "&cid=" + cid);
								}
								comment.put("url", url.toString());
								comment.put("message", Common.getStr((String) comment.get("message"), 150,
										true, true, false, 0, -1, request, response));
								list.get(idType).get(cid).put("info", comment);
								emptyIds.remove("comment" + cid);
							}
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
					} else if ("post".equals(idType)) {
						temps = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("post") + " WHERE pid IN (" + newIds + ")");
						try {
							for (Map<String, Object> post : temps) {
								int pid = (Integer) post.get("pid");
								post.put("message", Common.getStr((String) post.get("message"), 150, false,
										false, false, 0, 0, request, response));
								list.get(idType).get(pid).put("info", post);
								emptyIds.remove(idType + pid);
							}
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
					}
				}
				if (emptyIds.size() > 0) {
					dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("report")
							+ " WHERE rid IN (" + Common.sImplode(emptyIds) + ")");
				}
				request.setAttribute("list", list);
				if(list.size()%perpage==1){
					mpurl.append("&page="+(page-1));
				}else{
					mpurl.append("&page="+page);
				}
			}
		}
		request.setAttribute("FORMHASH", formHash(request));
		request.setAttribute("count", count);
		request.setAttribute("mpurl", mpurl);
		request.setAttribute("allowmanage", allowmanage);
		request.setAttribute("perpage", perpage);
		Map<String, String> idTypes = new LinkedHashMap<String, String>();
		idTypes.put("picid", "图片");
		idTypes.put("albumid", "相册");
		idTypes.put("blogid", "日志");
		idTypes.put("tagid", "群组");
		idTypes.put("tid", "话题");
		idTypes.put("uid", "空间");
		idTypes.put("sid", "分享");
		idTypes.put("pid", "投票");
		idTypes.put("eventid", "活动");
		idTypes.put("comment", "评论");
		idTypes.put("post", "话题回复");
		request.setAttribute("idTypes", idTypes);
		return mapping.findForward("report");
	}

	
	private void deleteInfo(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Object ids) throws Exception {
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		List<Map<String, Object>> reportList = dataBaseService.executeQuery("SELECT id, idtype, uids FROM "
				+ JavaCenterHome.getTableName("report") + " WHERE rid IN (" + Common.sImplode(ids) + ")");
		TreeMap<Integer, String> users = null;
		List<Integer> reportUser = new ArrayList<Integer>();
		Map<String, List<Integer>> delType = new HashMap<String, List<Integer>>();
		for (Map<String, Object> report : reportList) {
			users = (TreeMap<Integer, String>) Serializer.unserialize((String) report.get("uids"), true);
			reportUser.add(users.firstKey());
			String idType = (String) report.get("idtype");
			List<Integer> reportIds = delType.get(idType);
			if (reportIds == null) {
				reportIds = new ArrayList<Integer>();
				delType.put(idType, reportIds);
			}
			reportIds.add((Integer) report.get("id"));
		}
		Map<String, Object> member = (Map<String, Object>) sGlobal.get("member");
		int gid = Common.getGroupid(request, response, (Integer) member.get("experience"), (Integer) member
				.get("groupid"));
		Map<String, Object> usergroup = (Map<String, Object>) request.getAttribute("usergroup" + gid);
		usergroup.put("managebatch", 1);
		int i = 0;
		Set<String> idTypes = delType.keySet();
		for (String idType : idTypes) {
			List<Integer> reportIds = delType.get(idType);
			if ("blogid".equals(idType)) {
				usergroup.put("manageblog", 1);
				adminDeleteService.deleteBlogs(request, response, supe_uid, reportIds);
			} else if ("picid".equals(idType)) {
				usergroup.put("managealbum", 1);
				adminDeleteService.deletePics(request, response, supe_uid, reportIds);
			} else if ("albumid".equals(idType)) {
				usergroup.put("managealbum", 1);
				adminDeleteService.deleteAlbums(request, response, supe_uid, reportIds);
			} else if ("tid".equals(idType)) {
				usergroup.put("managethread", 1);
				adminDeleteService.deleteThreads(request, response, supe_uid, 0, reportIds);
			} else if ("tagid".equals(idType)) {
				usergroup.put("managemtag", 1);
				adminDeleteService.deleteMtag(request, response, reportIds);
			} else if ("sid".equals(idType)) {
				usergroup.put("manageshare", 1);
				adminDeleteService.deleteShares(request, response, supe_uid, reportIds);
			} else if ("uid".equals(idType)) {
				usergroup.put("managedelspace", 1);
				for (Integer uid : reportIds) {
					adminDeleteService.deleteSpace(request, response, uid, false);
				}
			} else if ("eventid".equals(idType)) {
				usergroup.put("manageevent", 1);
				adminDeleteService.deleteEvents(request, response, sGlobal, reportIds);
			} else if ("pid".equals(idType)) {
				usergroup.put("managepoll", 1);
				adminDeleteService.deletePolls(request, response, supe_uid, reportIds);
			} else if ("comment".equals(idType)) {
				usergroup.put("managecomment", 1);
				adminDeleteService.deleteComments(request, response, supe_uid, reportIds);
			} else if ("post".equals(idType)) {
				usergroup.put("managethread", 1);
				adminDeleteService.deletePosts(request, response, supe_uid, 0, reportIds);
			}
			Common.getReward("report", true, reportUser.get(i), "", false, request, response);
			i++;
		}
	}
}