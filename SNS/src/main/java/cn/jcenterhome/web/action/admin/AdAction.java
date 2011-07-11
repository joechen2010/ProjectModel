package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.web.action.BaseAction;


public class AdAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managead")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		try {
			if (submitCheck(request, "adsubmit")) {
				int adId = Common.intval(request.getParameter("adid"));
				String title = Common.getStr(request.getParameter("title"), 50, true, true, false, 0, 0,
						request, response);
				if (Common.empty(title)) {
					title = "AD" + Common.sgmdate(request, "MdHms", 0);
				}
				int system = Common.intval(request.getParameter("system"));
				StringBuffer html = new StringBuffer();
				Map<String, Object> adCodes = new HashMap<String, Object>();
				String adCodeType = request.getParameter("adcode[type]");
				if ("html".equals(adCodeType)) {
					String adcodeHtml = request.getParameter("adcode[html]");
					adCodes.put("html", adcodeHtml);
					html.append(Common.stripSlashes(adcodeHtml));
				} else if ("flash".equals(adCodeType)) {
					adCodes.put("flashheight", Common.intval(request.getParameter("adcode[flashheight]")));
					adCodes.put("flashwidth", Common.intval(request.getParameter("adcode[flashwidth]")));
					adCodes.put("flashurl", request.getParameter("adcode[flashurl]"));
					String width = Common.empty(adCodes.get("flashwidth")) ? "" : "width=\""
							+ adCodes.get("flashwidth") + "\"";
					String height = Common.empty(adCodes.get("flashheight")) ? "" : "height=\""
							+ adCodes.get("flashheight") + "\"";
					String flashUrl = Common.stripSlashes((String) adCodes.get("flashurl"));
					html
							.append("<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" adcodebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,45,0\" "
									+ width + " " + height + ">\n");
					html.append("<param name=\"movie\" value=\"" + flashUrl + "\" />\n");
					html.append("<param name=\"quality\" value=\"high\" />\n");
					html
							.append("<embed src=\""
									+ flashUrl
									+ "\" quality=\"high\" pluginspage=\"http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash\" type=\"application/x-shockwave-flash\" "
									+ width + " " + height + "></embed>\n");
					html.append("</object>\n");
				} else if ("image".equals(adCodeType)) {
					adCodes.put("imageheight", Common.intval(request.getParameter("adcode[imageheight]")));
					adCodes.put("imagewidth", Common.intval(request.getParameter("adcode[imagewidth]")));
					adCodes.put("imagesrc", request.getParameter("adcode[imagesrc]"));
					adCodes.put("imageurl", request.getParameter("adcode[imageurl]"));
					adCodes.put("imagealt", Common.getStr(request.getParameter("adcode[imagealt]"), 200,
							true, true, false, 0, 0, request, response));
					String width = Common.empty(adCodes.get("imagewidth")) ? "" : "width=\""
							+ adCodes.get("imagewidth") + "\"";
					String height = Common.empty(adCodes.get("imageheight")) ? "" : "height=\""
							+ adCodes.get("imageheight") + "\"";
					html.append("<a href=\"" + adCodes.get("imageurl") + "\" target=\"_blank\"><img src=\""
							+ Common.stripSlashes((String) adCodes.get("imagesrc")) + "\" " + width + " "
							+ height + " border=\"0\" alt=\"" + adCodes.get("imagealt") + "\"></a>");
				} else if ("text".equals(adCodeType)) {
					adCodes.put("textcontent", Common.getStr(request.getParameter("adcode[textcontent]"), 0,
							true, true, false, 0, 0, request, response));
					adCodes.put("texturl", request.getParameter("adcode[texturl]"));
					adCodes.put("textsize", Common.intval(request.getParameter("adcode[textsize]")));
					String size = Common.empty(adCodes.get("textsize")) ? "" : "style=\"font-size:"
							+ adCodes.get("textsize") + "px;\"";
					html.append("<span style=\"padding:0.8em\"><a href=\""
							+ Common.stripSlashes((String) adCodes.get("texturl")) + "\" target=\"_blank\" "
							+ size + ">" + adCodes.get("textcontent") + "</a></span>");
				}

				if (adCodes.isEmpty()) {
					return cpMessage(request, mapping, "cp_please_check_whether_the_option_complete_required");
				} else {
					adCodes.put("type", adCodeType);
				}

				Map<String, Object> setData = new HashMap<String, Object>();
				setData.put("title", title);
				setData.put("pagetype", request.getParameter("pagetype"));
				setData.put("adcode", Common.addSlashes(Serializer.serialize(Common.sStripSlashes(adCodes))));
				setData.put("system", system);
				setData.put("available", system == 0 ? 1 : Common.intval(request.getParameter("available")));
				if (adId == 0) {
					adId = dataBaseService.insertTable("ad", setData, true, false);
				} else {
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("adid", adId);
					dataBaseService.updateTable("ad", setData, whereData);
				}
				String tpl = JavaCenterHome.jchRoot + "data/adtpl/" + adId + ".htm";
				FileHelper.writeFile(tpl, html.toString(), request);

				cacheService.ad_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=ad");
			} else if (submitCheck(request, "delsubmit")) {
				String[] adIds = request.getParameterValues("adids[]");
				if (!Common.empty(adIds) && deleteAds(request, adIds)) {
					cacheService.ad_cache();
					return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=ad");
				} else {
					return cpMessage(request, mapping, "cp_please_choose_to_remove_advertisements",
							"admincp.jsp?ac=ad");
				}
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		String op = request.getParameter("op");
		if (Common.empty(op)) {
			String sql = null;
			String pageType = request.getParameter("pagetype");
			if (Common.empty(pageType)) {
				sql = "";
			} else {
				sql = " WHERE pagetype='" + pageType + "'";
			}
			List<Map<String, Object>> query = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("ad") + sql + " ORDER BY adid DESC");
			if (query.size() > 0) {
				Map<String, List<Map<String, Object>>> listValue = new HashMap<String, List<Map<String, Object>>>();
				for (Map<String, Object> ad : query) {
					int available = (Integer) ad.get("available");
					String system = String.valueOf(ad.get("system"));
					ad.put("available", available == 0 ? "-" : "有效");
					ad.put("adcode", Serializer.unserialize((String) ad.get("adcode"), false));
					List<Map<String, Object>> temps = listValue.get(system);
					if (temps == null) {
						temps = new ArrayList<Map<String, Object>>();
						listValue.put(system, temps);
					}
					temps.add(ad);
				}
				request.setAttribute("listvalue", listValue);
			}
			Map<String, String> pageTypes = new HashMap<String, String>();
			pageTypes.put("header", "页头");
			pageTypes.put("rightside", "内容页面");
			pageTypes.put("footer", "页脚");
			pageTypes.put("couplet", "对联");
			pageTypes.put("contenttop", "页面主区域上方");
			pageTypes.put("contentbottom", "页面主区域下方");
			pageTypes.put("feedbox", "动态置顶位");
			request.setAttribute("pageTypes", pageTypes);
			request.setAttribute("actives_view", " class=\"active\"");
		} else if ("add".equals(op) || "edit".equals(op)) {
			int adId = Common.intval(request.getParameter("adid"));
			Map<String, Object> adValue = null;
			if (adId > 0) {
				String sql = "SELECT * FROM " + JavaCenterHome.getTableName("ad") + " WHERE adid='" + adId
						+ "'";
				List<Map<String, Object>> values = dataBaseService.executeQuery(sql);
				adValue = values.isEmpty() ? null : values.get(0);
			}
			Map<String, String> adCode = null;
			if (Common.empty(adValue)) {
				adValue = new HashMap<String, Object>();
				adValue.put("adid", 0);
				adValue.put("system", 1);
				adValue.put("pagetype", "leftside");
				adValue.put("available", 1);
				adCode = new HashMap<String, String>();
				adCode.put("type", "html");
			} else {
				adCode = Serializer.unserialize((String) adValue.get("adcode"), false);
			}
			adValue.put("adcode", adCode);
			request.setAttribute("system_" + adValue.get("system"), " checked");
			request.setAttribute("pagetype_" + adValue.get("pagetype"), " selected");
			request.setAttribute("available_" + adValue.get("available"), " checked");
			request.setAttribute("adcode_" + adCode.get("type"), " selected");
			request.setAttribute("advalue", adValue);
		} else if ("tpl".equals(op)) {
			int adId = Common.intval(request.getParameter("adid"));
			String adCode = (String) Common.sHtmlSpecialChars("${jch:showAdById(" + adId + ")}");
			request.setAttribute("adcode", adCode);
		} else if ("js".equals(op)) {
			int adId = Common.intval(request.getParameter("adid"));
			String adCode = (String) Common.sHtmlSpecialChars("<script type=\"text/javascript\" src=\""
					+ Common.getSiteUrl(request) + "js.jsp?adid=" + adId + "\"></script>");
			request.setAttribute("adcode", adCode);
		}
		return mapping.findForward("ad");
	}

	
	private boolean deleteAds(HttpServletRequest request, String[] adIds) {
		String tableName = JavaCenterHome.getTableName("ad");
		List<String> newIds = dataBaseService.executeQuery("SELECT adid FROM " + tableName
				+ " WHERE adid IN (" + Common.sImplode(adIds) + ")", 1);
		if (newIds.isEmpty()) {
			return false;
		}
		String tplDir = JavaCenterHome.jchRoot + "data/adtpl/";
		for (String newId : newIds) {
			new File(tplDir + newId + ".htm").delete();
		}
		dataBaseService.executeUpdate("DELETE FROM " + tableName + " WHERE adid IN ("
				+ Common.sImplode(newIds) + ")");
		return true;
	}
}