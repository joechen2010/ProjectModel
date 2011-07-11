package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class SpaceAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		boolean managename = Common.checkPerm(request, response, "managename");
		boolean managespacegroup = Common.checkPerm(request, response, "managespacegroup");
		boolean managespaceinfo = Common.checkPerm(request, response, "managespaceinfo");
		boolean managespacecredit = Common.checkPerm(request, response, "managespacecredit");
		boolean managespacenote = Common.checkPerm(request, response, "managespacenote");
		boolean manageconfig = Common.checkPerm(request, response, "manageconfig");
		boolean managedelspace = Common.checkPerm(request, response, "managedelspace");

		boolean noprivilege = !managename && !managespacegroup && !managespaceinfo && !managespacecredit
				&& !managespacenote && !managedelspace;
		if (noprivilege) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		int uid = Common.intval(request.getParameter("uid"));
		Map<String, Object> member = null;
		String videopic = null;
		if (uid > 0) {
			List<Map<String, Object>> spaceList = dataBaseService.executeQuery("SELECT s.*, sf.* FROM "
					+ JavaCenterHome.getTableName("space") + " s LEFT JOIN "
					+ JavaCenterHome.getTableName("spacefield") + " sf ON sf.uid=s.uid	WHERE s.uid=" + uid);
			if (spaceList.isEmpty()) {
				return cpMessage(request, mapping, "cp_designated_users_do_not_exist");
			}
			member = spaceList.get(0);
			member.put("addsize", (Integer) member.get("addsize") / (1024 * 1024));
			String ip = member.get("ip").toString();
			member.put("ip", Common.strlen(ip) < 9 ? "-" : Common.intval(ip.substring(0, 3)) + "."
					+ Common.intval(ip.substring(3, 6)) + "." + Common.intval(ip.substring(6, 9)) + ".1~255");
			if (!Common.empty(sConfig.get("videophoto")) && !Common.empty(member.get("videopic"))) {
				videopic = cpService.getVideoPic((String) member.get("videopic"));
			} else {
				member.put("videostatus", 0);
			}
		}
		if (uid != supe_uid && Common.ckFounder(uid)) {
			return cpMessage(request, mapping, "cp_not_have_permission_to_operate_founder");
		}
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		int timestamp = (Integer) sGlobal.get("timestamp");
		SimpleDateFormat spaceSDF = Common.getSimpleDateFormat("yyyy-MM-dd HH:mm", timeoffset);
		Map<Integer, Map<String, Object>> profilefields = Common.getCacheDate(request, response,
				"/data/cache/cache_profilefield.jsp", "globalProfilefield");
		try {
			if (submitCheck(request, "usergroupsubmit")) {
				if (Common.empty(member)) {
					return cpMessage(request, mapping, "cp_designated_users_do_not_exist");
				}
				Map<String, Object> setarr = new HashMap<String, Object>();
				if (managespacecredit) {
					setarr.put("addsize", Common.intval(request.getParameter("addsize")) * 1024 * 1024);
					setarr.put("credit", Common.intval(request.getParameter("credit")));
					setarr.put("experience", Common.intval(request.getParameter("experience")));
				} else {
					setarr.put("credit", (Integer) member.get("credit"));
					setarr.put("experience", (Integer) member.get("experience"));
				}
				String email = null;
				int emailcheck = 0;
				if (managespaceinfo) {
					DynaActionForm actionForm = (DynaActionForm) form;
					FormFile formFile = (FormFile) actionForm.get("newvideopic");
					if (formFile != null && formFile.getFileSize() > 0) {
						if (formFile.getFileName().matches(".*\\.(jpg|jpeg|gif|bmp)$")) {
							String newvideopic = cpService.videoPicUpload(formFile, uid, timestamp);
							if (newvideopic != null) {
								if (!Common.empty(member.get("videopic"))) {
									File file = new File(JavaCenterHome.jchRoot + "/"
											+ cpService.getVideoPic((String) member.get("videopic")));
									if (file.exists()) {
										file.delete();
									}
								}
								member.put("videopic", newvideopic);
							}
						}
					}
					int videostatus = Common.intval(request.getParameter("videostatus"));
					if (Common.empty(member.get("videopic"))) {
						videostatus = 0;
					}
					if(videostatus==2){
						File file = new File(JavaCenterHome.jchRoot + "/"
								+ cpService.getVideoPic((String) member.get("videopic")));
						if (file.exists()) {
							file.delete();
						}
						member.put("videopic", "");
						videostatus=0;
					}
					setarr.put("videostatus", videostatus);
					try {
						email = Common.getStr(request.getParameter("email"), 100, true, true, false, 0, 0,
								request, response);
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
					emailcheck = Common.intval(request.getParameter("emailcheck"));
					if (emailcheck > 0 && !Common.empty(email)) {
						Map<String, Integer> reward = Common.getReward("realemail", false, uid, "", false,
								request, response);
						if (reward.get("credit") > 0) {
							setarr.put("credit", (Integer) setarr.get("credit") + reward.get("credit"));
						}
						if (reward.get("experience") > 0) {
							setarr.put("experience", (Integer) setarr.get("experience")
									+ reward.get("experience"));
						}
					}
					setarr.put("domain", Common.trim(request.getParameter("domain")));
					setarr.put("addfriend", Common.intval(request.getParameter("addfriend")));
				}

				if (managespacegroup) {
					if ((Integer) member.get("flag") != -1) {
						int flag = Common.intval(request.getParameter("flag"));
						int result = 0;
						if (flag == 1) {
							result = 1;
						} else {
							flag = 0;
							result = 1;
						}
						if (result != 0) {
							setarr.put("flag", flag);
						}
					}
					if (uid != supe_uid || Common.ckFounder(supe_uid)) {
						int groupId = Common.intval(request.getParameter("groupid"));
						int expiration = 0;
						if (groupId == 0) {
							groupId = Common.getGroupid(request, response, Common.intval(request
									.getParameter("experience")), groupId);
						} else {
							expiration = Common.strToTime(request.getParameter("expiration"), timeoffset,
									"yyyy-MM-dd HH:mm");
							if (expiration > 0 && expiration <= timestamp) {
								return cpMessage(request, mapping, "time_expired_error");
							}
						}

						Map<String, Object> group = Common.getCacheDate(request, response,
								"/data/cache/usergroup_" + groupId + ".jsp", "usergroup" + groupId);
						if ((Integer) group.get("manageconfig") > 0 && !Common.ckFounder(supe_uid)) {
							return cpMessage(request, mapping, "cp_no_authority_management_operation");
						}
						if (expiration > 0) {
							dataBaseService.executeUpdate("REPLACE INTO "
									+ JavaCenterHome.getTableName("spacelog")
									+ " (uid,username,opuid,opusername,expiration,dateline,flag) VALUES ("
									+ member.get("uid") + ",'"
									+ Common.addSlashes((String) member.get("username")) + "'," + supe_uid
									+ ",'" + sGlobal.get("supe_username") + "'," + expiration + ","
									+ timestamp + ",1)");
						}
						setarr.put("groupid", groupId);
					}
				}
				if (managename) {
					try {
						setarr.put("name", Common.getStr(request.getParameter("name"), 20, true, true, false,
								0, 0, request, response));
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
					int nameStatus = Common.intval(request.getParameter("namestatus"));
					if (nameStatus > 0 && !Common.empty(setarr.get("name"))) {
						Map<String, Integer> reward = Common.getReward("realname", false, uid, "", false,
								request, response);
						if (reward.get("credit") > 0) {
							setarr.put("credit", (Integer) setarr.get("credit") + reward.get("credit"));
						}
						if (reward.get("experience") > 0) {
							setarr.put("experience", (Integer) setarr.get("experience")
									+ reward.get("experience"));
						}
					}
					setarr.put("namestatus", nameStatus);
				}

				if (setarr.size() > 0) {
					Set<String> keys = setarr.keySet();
					StringBuffer updateStr = new StringBuffer();
					for (String key : keys) {
						updateStr.append(key + "='" + setarr.get(key) + "',");
					}
					String sql = "UPDATE " + JavaCenterHome.getTableName("space") + " SET "
							+ updateStr.substring(0, updateStr.length() - 1) + " WHERE uid=" + uid;
					dataBaseService.executeUpdate(sql);
				}

				if (managespaceinfo) {
					setarr = new HashMap<String, Object>();
					setarr.put("email", email == null ? "" : email);
					setarr.put("emailcheck", emailcheck);
					try {
						setarr.put("qq", Common.getStr(request.getParameter("qq"), 20, true, true, false, 0,
								0, request, response));
						setarr.put("msn", Common.getStr(request.getParameter("msn"), 80, true, true, false,
								0, 0, request, response));
						setarr.put("blood", Common.getStr(request.getParameter("blood"), 5, true, true,
								false, 0, 0, request, response));
						setarr.put("birthprovince", Common.getStr(request.getParameter("birthprovince"), 20,
								true, true, false, 0, 0, request, response));
						setarr.put("birthcity", Common.getStr(request.getParameter("birthcity"), 20, true,
								true, false, 0, 0, request, response));
						setarr.put("resideprovince", Common.getStr(request.getParameter("resideprovince"),
								20, true, true, false, 0, 0, request, response));
						setarr.put("residecity", Common.getStr(request.getParameter("residecity"), 20, true,
								true, false, 0, 0, request, response));
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
					setarr.put("sex", Common.intval(request.getParameter("sex")));
					setarr.put("birthyear", Common.intval(request.getParameter("birthyear")));
					setarr.put("birthmonth", Common.intval(request.getParameter("birthmonth")));
					setarr.put("birthday", Common.intval(request.getParameter("birthday")));
					setarr.put("marry", Common.intval(request.getParameter("marry")));
					setarr.put("videopic", member.get("videopic"));

					Set<Integer> fields = profilefields.keySet();
					for (Integer field : fields) {
						Map<String, Object> value = profilefields.get(field);
						int maxSize = (Integer) value.get("maxsize");
						if ("formtype".equals(value.get("formtype"))) {
							maxSize = 255;
						}
						try {
							setarr.put("field_" + field, Common.getStr(
									request.getParameter("field_" + field), maxSize, true, true, false, 0, 0,
									request, response));
						} catch (Exception e) {
							return showMessage(request, response, e.getMessage());
						}
					}
					if (!Common.empty(request.getParameter("clearcss"))) {
						setarr.put("css", "");
					}
					Set<String> updateKeys = setarr.keySet();
					StringBuffer updateStr = new StringBuffer();
					for (String key : updateKeys) {
						updateStr.append(key + "='" + setarr.get(key) + "',");
					}
					String sql = "UPDATE " + JavaCenterHome.getTableName("spacefield") + " SET "
							+ updateStr.substring(0, updateStr.length() - 1) + " WHERE uid=" + uid;
					dataBaseService.executeUpdate(sql);
				}
				if ((!Common.empty(sConfig.get("my_status")))) {
					String sql = "REPLACE INTO " + JavaCenterHome.getTableName("userlog")
							+ " (uid, action, dateline) VALUES (" + uid + ",'update'," + timestamp + ")";
					dataBaseService.executeUpdate(sql);
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=space&op=manage&uid=" + uid);
			} else if (submitCheck(request, "listsubmit")) {
				int optype = Common.intval(request.getParameter("optype"));
				String[] uids = request.getParameterValues("uids");
				String mpurl = request.getParameter("mpurl");
				if (uids != null) {
					boolean createLog = false;
					switch (optype) {
					case 1:
						if (managename) {
							mpurl += "&namestatus=0";
							Map<String, Integer> reward = null;
							for (String uidKey : uids) {
								int val = Common.intval(uidKey);
								reward = Common.getReward("realname", false, val, "", false, request,
										response);
								dataBaseService.executeUpdate("UPDATE "
										+ JavaCenterHome.getTableName("space")
										+ " SET namestatus=1, credit=credit+" + reward.get("credit")
										+ ", experience=experience+" + reward.get("experience")
										+ " WHERE uid=" + val + " AND name!=''");
							}
							createLog = true;
						}
						break;
					case 2:
						if (managename) {
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
									+ " SET namestatus=0 WHERE uid IN (" + Common.sImplode(uids) + ")");
							mpurl += "&namestatus=1";
							createLog = true;
						}
						break;
					case 3:
						if (managename) {
							dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
									+ " SET name='',namestatus=0 WHERE uid IN (" + Common.sImplode(uids)
									+ ")");
						}
						break;
					case 4:
					case 5:
						if (managespacenote) {
							request.setAttribute("optype", optype);
							request.setAttribute("uids", Common.implode(uids, ","));
							request.setAttribute("FORMHASH", formHash(request));
							request.setAttribute("mpurl", mpurl);
							return mapping.findForward("space_manage");
						}
						break;
					case 6:
						if (managespaceinfo) {
							dataBaseService.executeUpdate("UPDATE "
									+ JavaCenterHome.getTableName("spacefield")
									+ " SET css='' WHERE uid IN (" + Common.sImplode(uids) + ")");
							createLog = true;
						}
						break;
					case 7:
						if (manageconfig) {
							request.setAttribute("optype", optype);
							request.setAttribute("uids", Common.implode(uids, ","));
							request.setAttribute("FORMHASH", formHash(request));
							request.setAttribute("mpurl", mpurl);
							return mapping.findForward("space_manage");
						}
						break;
					default:
						return cpMessage(request, mapping, "cp_choice_batch_action");
					}
					if (createLog && !Common.empty(sConfig.get("my_status"))) {
						String comma = "";
						StringBuffer values = new StringBuffer();
						for (String uidStr : uids) {
							values.append(comma + "(" + Common.intval(uidStr) + ",'update'," + timestamp);
							comma = ",";
						}
						dataBaseService.executeUpdate("REPLACE INTO "
								+ JavaCenterHome.getTableName("userlog") + " (uid, action, dateline) VALUES "
								+ values.toString());
					}
					return cpMessage(request, mapping, "do_success", mpurl);
				} else {
					return cpMessage(request, mapping, "cp_choose_to_operate_space");
				}
			} else if (submitCheck(request, "sendemailsubmit")) {
				if (!managespacenote) {
					return cpMessage(request, mapping, "cp_no_authority_management_operation");
				}
				String[] touids = null;
				String subject = Common.trim(request.getParameter("subject"));
				String message = Common.trim(request.getParameter("message"));
				if (!Common.empty(subject) || !Common.empty(message)) {
					String uids = Common.trim(request.getParameter("uids"));
					touids = Common.empty(uids) ? null : uids.split(",");
				}
				if (touids != null && touids.length > 0) {
					List<Map<String, Object>> spacefieldList = dataBaseService
							.executeQuery("SELECT email, emailcheck FROM "
									+ JavaCenterHome.getTableName("spacefield") + " WHERE uid IN ("
									+ Common.sImplode(touids) + ")");
					try {
						for (Map<String, Object> value : spacefieldList) {
							if (!Common.empty(value.get("email"))) {
								cpService.sendMail(request, response, 0, (String) value.get("email"),
										subject, message, "");
							}
						}
					} catch (Exception e) {
						return showMessage(request, response, e.getMessage());
					}
				}
				return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
			} else if (submitCheck(request, "pokesubmit")) {
				if (!managespacenote) {
					return cpMessage(request, mapping, "cp_no_authority_management_operation");
				}
				String uids = Common.trim(request.getParameter("uids"));
				String[] touids = Common.empty(uids) ? null : uids.split(",");
				String note = null;
				try {
					note = Common.getStr(request.getParameter("note"), 50, true, true, false, 0, 0, request,
							response);
				} catch (Exception e) {
					return showMessage(request, response, e.getMessage());
				}
				if (touids != null && touids.length > 0) {
					Map<String, String> uidsMap = new HashMap<String, String>();
					List<String> replaces = new ArrayList<String>();
					String supe_username = (String) sGlobal.get("supe_username");
					for (String touid : touids) {
						int temp = Common.intval(touid);
						if (temp > 0 && temp != supe_uid) {
							replaces.add("(" + temp + "," + supe_uid + ",'" + supe_username + "','" + note
									+ "'," + timestamp + ")");
							uidsMap.put(touid, touid);
						}
					}
					if (replaces.size() > 0) {
						List<String> pokeList = dataBaseService.executeQuery("SELECT uid FROM "
								+ JavaCenterHome.getTableName("poke") + " WHERE uid IN ("
								+ Common.sImplode(uidsMap) + ") AND fromuid=" + supe_uid, 1);
						for (String value : pokeList) {
							uidsMap.remove(value);
						}
						dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("poke")
								+ " (uid,fromuid,fromusername,note,dateline) VALUES "
								+ Common.implode(replaces, ","));
						if (uidsMap.size() > 0) {
							String sql = "UPDATE " + JavaCenterHome.getTableName("space")
									+ " SET pokenum=pokenum+1 WHERE uid IN(" + Common.sImplode(uidsMap) + ")";
							dataBaseService.executeUpdate(sql);
						}
					}
				}
				return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
			} else if (submitCheck(request, "magicsubmit")) {
				if (!manageconfig) {
					return cpMessage(request, mapping, "cp_no_authority_management_operation");
				}
				String uids = Common.trim(request.getParameter("uids"));
				String[] touids = Common.empty(uids) ? null : uids.split(",");

				String[] magicaward = request.getParameterValues("magicaward");
				Map<String, Integer> presents = new HashMap<String, Integer>();
				List<String> mids = new ArrayList<String>();
				if (magicaward != null) {
					for (String value : magicaward) {
						String[] list = value.split(",");
						if (list.length > 0) {
							String mid = list[0];
							int count = 0;
							if (list.length > 1) {
								count = Common.intval(list[1]);
							}
							mids.add(mid);
							presents.put(mid, count);
						}
					}
				}

				if (touids != null && touids.length > 0 && mids.size() > 0) {
					Map<Integer, String> names = new HashMap<Integer, String>();
					Map<Integer, Map<String, Integer>> owns = new HashMap<Integer, Map<String, Integer>>();
					List<Map<String, Object>> userMagicList = dataBaseService
							.executeQuery("SELECT uid,mid,count FROM "
									+ JavaCenterHome.getTableName("usermagic") + " WHERE uid IN ("
									+ Common.sImplode(touids) + ") AND mid IN (" + Common.sImplode(mids)
									+ ")");
					for (Map<String, Object> userMagic : userMagicList) {
						Map<String, Integer> own = owns.get((Integer) userMagic.get("uid"));
						if (own == null) {
							own = new HashMap<String, Integer>();
							owns.put((Integer) userMagic.get("uid"), own);
						}
						own.put((String) userMagic.get("mid"), (Integer) userMagic.get("count"));
					}
					List<Map<String, Object>> memberList = dataBaseService
							.executeQuery("SELECT uid,username FROM " + JavaCenterHome.getTableName("member")
									+ " WHERE uid IN (" + Common.sImplode(touids) + ")");
					for (Map<String, Object> value : memberList) {
						names.put((Integer) value.get("uid"), (String) Common.sAddSlashes(value
								.get("username")));
					}
					List<String> note_presents = null;
					List<String> log_inserts = new ArrayList<String>();
					List<String> inserts = new ArrayList<String>();
					List<String> note_inserts = new ArrayList<String>();
					Map<String, String> magic = (Map<String, String>) request.getAttribute("globalMagic");
					Set<String> keys = presents.keySet();
					for (String touid : touids) {
						note_presents = new ArrayList<String>();
						for (String mid : keys) {
							int count = presents.get(mid);
							note_presents.add("<a href=\"cp.jsp?ac=magic&view=me&mid=" + mid
									+ "\" target=\"_blank\">" + magic.get(mid) + "</a>(" + count
									+ Common.getMessage(request, "cp_magicunit") + ")");
							log_inserts.add("(" + touid + ", '" + names.get(touid) + "', '" + mid + "', "
									+ count + ", 2, " + supe_uid + ", 0, " + timestamp + ")");
							count = count
									+ (owns.get(touid) != null ? (Integer) (owns.get(touid).get(mid)) : 0);
							inserts.add("(" + touid + ", '" + names.get(touid) + "', '" + mid + "', " + count
									+ ")");
						}
						String note = Common.getMessage(request, "cp_present_user_magics", Common.implode(
								note_presents, "; "));
						note_inserts.add("(" + touid + ",'system',1,0,'','" + note + "'," + timestamp + ")");
					}
					dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("usermagic")
							+ " (uid, username, mid, count) VALUES " + Common.implode(inserts, ","));
					dataBaseService.executeUpdate("INSERT INTO " + JavaCenterHome.getTableName("magicinlog")
							+ " (uid, username, mid, count, type, fromid, credit, dateline) VALUES "
							+ Common.implode(log_inserts, ","));
					dataBaseService.executeUpdate("INSERT INTO "
							+ JavaCenterHome.getTableName("notification")
							+ " (uid, type, new, authorid, author, note, dateline) VALUES "
							+ Common.implode(note_inserts, ","));
					dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
							+ " SET notenum = notenum + 1 WHERE uid IN (" + Common.sImplode(touids) + ")");
				}
				return cpMessage(request, mapping, "do_success", request.getParameter("mpurl"));
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}

		String op = request.getParameter("op");
		if ("delete".equals(op)) {
			if (!managedelspace) {
				cpMessage(request, mapping, "cp_no_authority_management_operation");
			}
			try {
				if (uid > 0 && adminDeleteService.deleteSpace(request, response, uid, false)) {
					return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=space");
				} else {
					return cpMessage(request, mapping, "cp_choose_to_delete_the_space");
				}
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
		} else if ("close".equals(op)) {
			if (!managespaceinfo) {
				return cpMessage(request, mapping, "cp_no_authority_management_operation");
			} else if (Common.empty(member)) {
				return cpMessage(request, mapping, "cp_designated_users_do_not_exist");
			}
			int flag = (Integer) member.get("flag") == -1 ? 0 : -1;
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space") + " SET flag="
					+ flag + " WHERE uid=" + uid);
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=space&op=manage&uid=" + uid);
		} else if ("deleteavatar".equals(op)) {
			if (!managespaceinfo) {
				return cpMessage(request, mapping, "cp_no_authority_management_operation");
			}
			Common.deleteAvatar(sGlobal, uid);

			Map<String, Integer> reward = Common.getReward("delavatar", false, uid, "", true, request,
					response);
			dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("space")
					+ " SET avatar=0, credit=credit-" + reward.get("credit") + ", experience=experience-"
					+ reward.get("experience") + " WHERE uid=" + uid);
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=space&op=manage&uid=" + uid);
		} else if ("manage".equals(op)) {
			if (Common.empty(member)) {
				return cpMessage(request, mapping, "cp_designated_users_do_not_exist");
			}
			if (managespaceinfo) {
				int nowy = Common.intval(Common.sgmdate(request, "yyyy", 0));
				StringBuffer birthyeayHtml = new StringBuffer();
				for (int i = 0; i < 80; i++) {
					int they = nowy - i;
					birthyeayHtml.append("<option value=\"" + they + "\""
							+ (they == (Integer) member.get("birthyear") ? " selected" : "") + ">" + they
							+ "</option>");
				}
				request.setAttribute("birthyeayhtml", birthyeayHtml);
				StringBuffer birthmonthHtml = new StringBuffer();
				for (int i = 1; i < 13; i++) {
					birthmonthHtml.append("<option value=\"" + i + "\""
							+ (i == (Integer) member.get("birthmonth") ? " selected" : "") + ">" + i
							+ "</option>");
				}
				request.setAttribute("birthmonthhtml", birthmonthHtml);
				StringBuffer birthdayHtml = new StringBuffer();
				for (int i = 1; i < 32; i++) {
					birthdayHtml.append("<option value=\"" + i + "\""
							+ (i == (Integer) member.get("birthday") ? " selected" : "") + ">" + i
							+ "</option>");
				}
				request.setAttribute("birthdayhtml", birthdayHtml);
				StringBuffer bloodHtml = new StringBuffer();
				String[] array = new String[] { "A", "B", "O", "AB" };
				for (String value : array) {
					bloodHtml.append("<option value=\"" + value + "\""
							+ (value.equals(member.get("blood")) ? " selected" : "") + ">" + value
							+ "</option>");
				}
				request.setAttribute("bloodhtml", bloodHtml);
				List<Map<String, Object>> profileFields = dataBaseService.executeQuery("SELECT * FROM "
						+ JavaCenterHome.getTableName("profilefield") + " ORDER BY displayorder");
				for (Map<String, Object> value : profileFields) {
					int fieldid = (Integer) value.get("fieldid");
					StringBuffer formHtml = new StringBuffer();
					if ("text".equals(value.get("formtype"))) {
						formHtml.append("<input type=\"text\" name=\"field_" + fieldid + "\" value=\""
								+ member.get("field_" + fieldid) + "\" class=\"t_input\">");
					} else {
						formHtml.append("<select name=\"field_" + fieldid + "\">");
						if (Common.empty(value.get("required"))) {
							formHtml.append("<option value=\"\">---</option>");
						}
						String[] options = ((String) value.get("choice")).split("\n");
						for (String ov : options) {
							ov = ov.trim();
							if (!Common.empty(ov)) {
								formHtml.append("<option value=\"" + ov + "\""
										+ (ov.equals(member.get("field_" + fieldid)) ? " selected" : "")
										+ ">" + ov + "</option>");
							}
						}
						formHtml.append("</select>");
					}
					value.put("formhtml", formHtml.toString());
				}
				request.setAttribute("profileFields", profileFields);
			}

			if (managespacegroup) {
				List<String> expirations = dataBaseService.executeQuery("SELECT expiration FROM "
						+ JavaCenterHome.getTableName("spacelog") + " WHERE uid=" + member.get("uid"), 1);
				if (expirations.size() > 0) {
					int expiration = Integer.parseInt(expirations.get(0));
					member.put("expiration", expiration > 0 ? Common.sgmdate(request, "yyyy-MM-dd HH:mm",
							expiration) : "");
				}
				String expirationStyle = " style=\"display:none;\"";
				List<Map<String, Object>> userGroups = dataBaseService
						.executeQuery("SELECT gid,grouptitle FROM "
								+ JavaCenterHome.getTableName("usergroup") + " WHERE system!=0");
				for (Map<String, Object> userGroup : userGroups) {
					int gid = (Integer) userGroup.get("gid");
					if (gid == (Integer) member.get("groupid")) {
						expirationStyle = null;
					}
				}
				request.setAttribute("userGroups", userGroups);
				request.setAttribute("expirationStyle", expirationStyle);
			}
			member.put("dateline", Common.gmdate(spaceSDF, (Integer) member.get("dateline")));
			if ((Integer) member.get("updatetime") > 0) {
				member.put("updatetime", Common.gmdate(spaceSDF, (Integer) member.get("updatetime")));
			} else {
				member.put("updatetime", "-");
			}
			member.put("lastlogin", Common.gmdate(spaceSDF, (Integer) member.get("lastlogin")));
			member.put("avatar", Common.avatar((Integer) member.get("uid"), "big", false, sGlobal, sConfig));
			request.setAttribute("managename", managename);
			request.setAttribute("managedelspace", managedelspace);
			request.setAttribute("managespaceinfo", managespaceinfo);
			request.setAttribute("managespacecredit", managespacecredit);
			request.setAttribute("managespacegroup", managespacegroup);
			request.setAttribute("uid", uid);
			request.setAttribute("member", member);
			request.setAttribute("videopic", videopic);
			request.setAttribute("FORMHASH", formHash(request));
			return mapping.findForward("space_manage");
		}
		StringBuffer mpurl = new StringBuffer("admincp.jsp?ac=space");

		String pre = "s.";
		String[] intkeys = new String[] { "uid", "groupid", "namestatus", "avatar", "videostatus", "opuid", "flag" };
		String[] strkeys = new String[] { "username", "opusername" };
		List<String[]> randkeys = new ArrayList<String[]>();
		randkeys.add(new String[] { "sstrtotime", "dateline" });
		randkeys.add(new String[] { "sstrtotime", "updatetime" });
		randkeys.add(new String[] { "sstrtotime", "lastpost" });
		randkeys.add(new String[] { "sstrtotime", "lastlogin" });
		randkeys.add(new String[] { "intval", "credit" });
		randkeys.add(new String[] { "intval", "experience" });
		String[] likekeys = new String[] { "name" };
		Map<String, String[]> paramMap = request.getParameterMap();
		Map<String, String> wheres = getWheres(intkeys, strkeys, randkeys, likekeys, pre, paramMap,
				timeoffset);
		String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");
		mpurl.append(wheres.get("url"));
		if ("1".equals(request.getParameter("namestatus"))) {
			whereSQL += " AND s.name!=''";
		}
		String tab = request.getParameter("tab");
		if (tab == null) {
			tab = "all";
		} else {
			mpurl.append("&tab=" + tab);
		}
		request.setAttribute("active_" + tab, " class=\"active\"");
		String fieldSQL="";
		if("0".equals(request.getParameter("videostatus")) && "4".equals(tab)){
			fieldSQL="LEFT JOIN "+ JavaCenterHome.getTableName("spacefield") + " sf ON s.uid=sf.uid ";
			whereSQL += " AND sf.videopic!=''";
		}
		Map<String, String> orders = getOrders(
				new String[] { "dateline", "updatetime", "friendnum", "credit", "viewnum", "experience" },
				"uid", pre, paramMap);
		String ordersql = orders.get("sql");
		mpurl.append(orders.get("url"));
		request.setAttribute("orderby_" + request.getParameter("orderby"), " selected");
		request.setAttribute("ordersc_" + request.getParameter("ordersc"), " selected");
		int perpage = Common.intval(request.getParameter("perpage"));
		if (!Common.in_array(new Integer[] { 20, 50, 100 }, perpage)) {
			perpage = 20;
		}
		mpurl.append("&perpage=" + perpage);
		request.setAttribute("perpage_" + perpage, " selected");
		int page = Math.max(Common.intval(request.getParameter("page")), 1);
		int start = (page - 1) * perpage;
		int maxPage = (Integer) sConfig.get("maxpage");
		String result = Common.ckStart(start, perpage, maxPage);
		if (result != null) {
			return showMessage(request, response, result);
		}
		Map<Object, String> userGroups = new HashMap<Object, String>();
		List<Map<String, Object>> userGroupList = dataBaseService.executeQuery("SELECT gid, grouptitle FROM "
				+ JavaCenterHome.getTableName("usergroup"));
		for (Map<String, Object> value : userGroupList) {
			userGroups.put(value.get("gid"), (String) value.get("grouptitle"));
		}
		int count = dataBaseService.findRows("SELECT COUNT(*) FROM " + JavaCenterHome.getTableName("space")
				+ " s "+fieldSQL+"WHERE " + whereSQL);
		if (count > 0) {
			Map<Integer, String> flags = new HashMap<Integer, String>();
			flags.put(-1, "<span style=\"color:blue;\">锁定</span>");
			flags.put(0, "正常");
			flags.put(1, "<span style=\"color:red;\">保护</span>");
			List<Object> uids = new ArrayList<Object>();
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT s.* FROM "
					+ JavaCenterHome.getTableName("space") + " s "+fieldSQL+"WHERE " + whereSQL + " " + ordersql
					+ " LIMIT " + start + "," + perpage);
			for (Map<String, Object> value : list) {
				value.put("grouptitle", userGroups.get(value.get("groupid")));
				value.put("addsize", Common.formatSize((Integer) value.get("addsize")));
				value.put("attachsize", Common.formatSize((Integer) value.get("attachsize")));
				value.put("avatar", Common.avatar((Integer) value.get("uid"), "small", false, sGlobal,
						sConfig));
				value.put("dateline", Common.gmdate(spaceSDF, (Integer) value.get("dateline")));
				if ((Integer) value.get("updatetime") > 0) {
					value.put("updatetime", Common.gmdate(spaceSDF, (Integer) value.get("updatetime")));
				} else {
					value.put("updatetime", "-");
				}
				value.put("flag", flags.get(value.get("flag")));
				uids.add(value.get("uid"));
			}
			if (uids.size() > 0) {
				Map<Object, Map<String, Object>> fusers = new HashMap<Object, Map<String, Object>>();
				List<Map<String, Object>> spaceLogList = dataBaseService
						.executeQuery("SELECT uid,opuid,opusername,expiration FROM "
								+ JavaCenterHome.getTableName("spacelog") + " WHERE uid IN ("
								+ Common.sImplode(uids) + ")");
				for (Map<String, Object> spaceLog : spaceLogList) {
					if ((Integer) spaceLog.get("expiration") > 0) {
						spaceLog.put("expiration", Common.gmdate(spaceSDF, (Integer) spaceLog
								.get("expiration")));
					} else {
						spaceLog.put("expiration", "-");
					}
					fusers.put(spaceLog.get("uid"), spaceLog);
				}
				request.setAttribute("fusers", fusers);
			}
			request.setAttribute("multi", Common.multi(request, count, perpage, page, maxPage, mpurl
					.toString(), null, null));
			request.setAttribute("list", list);
			if(list.size()%perpage==1){
				mpurl.append("&page="+(page-1));
			}else{
				mpurl.append("&page=" + page);
			}
		}
		request.setAttribute("managename", managename);
		request.setAttribute("managespacenote", managespacenote);
		request.setAttribute("manageconfig", manageconfig);
		request.setAttribute("managespaceinfo", managespaceinfo);
		request.setAttribute("FORMHASH", formHash(request));
		request.setAttribute("count", count);
		request.setAttribute("mpurl", mpurl);
		request.setAttribute("userGroups", userGroups);
		return mapping.findForward("space");
	}
}