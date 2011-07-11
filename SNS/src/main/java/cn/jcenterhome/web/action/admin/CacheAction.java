package cn.jcenterhome.web.action.admin;

import java.io.File;
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


public class CacheAction extends BaseAction {
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managecache")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		try {
			if (submitCheck(request, "cachesubmit")) {
				String[] cacheTypes = request.getParameterValues("cachetype");
				if (cacheTypes == null || Common.in_array(cacheTypes, "database")) {
					cacheService.config_cache();
					cacheService.usergroup_cache();
					cacheService.profilefield_cache();
					cacheService.profield_cache();
					cacheService.censor_cache();
					cacheService.block_cache();
					cacheService.eventclass_cache();
					cacheService.magic_cache();
					cacheService.click_cache();
					cacheService.task_cache();
					cacheService.ad_cache();
					cacheService.creditrule_cache();
					cacheService.userapp_cache();
					cacheService.app_cache();
					cacheService.network_cache();
				}
				if (cacheTypes == null || Common.in_array(cacheTypes, "block")) {
					Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
					cacheService.block_data_cache(sConfig);
				}
				if (cacheTypes == null || Common.in_array(cacheTypes, "ad")) {
					List<Map<String, Object>> ads = dataBaseService.executeQuery("SELECT adid, adcode FROM "
							+ JavaCenterHome.getTableName("ad"));
					for (Map<String, Object> ad : ads) {
						StringBuffer html = new StringBuffer();
						Map<String, Object> adCode = Serializer.unserialize((String) ad.get("adcode"), false);
						String type = (String) adCode.get("type");
						if ("html".equals(type)) {
							html.append(adCode.get("html"));
						} else if ("flash".equals(type)) {
							Object flashheight = adCode.get("flashheight");
							Object flashwidth = adCode.get("flashwidth");
							String flashUrl = (String) adCode.get("flashurl");
							String width = Common.empty(flashwidth) ? "" : "width=\"" + flashwidth + "\"";
							String height = Common.empty(flashheight) ? "" : "height=\"" + flashheight + "\"";
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
						} else if ("image".equals(type)) {
							Object imageheight = adCode.get("imageheight");
							Object imagewidth = adCode.get("imagewidth");
							String imagesrc = (String) adCode.get("imagesrc");
							String imageurl = (String) adCode.get("imageurl");
							String imagealt = (String) adCode.get("imagealt");
							String width = Common.empty(imagewidth) ? "" : "width=\"" + imagewidth + "\"";
							String height = Common.empty(imageheight) ? "" : "height=\"" + imageheight + "\"";
							html.append("<a href=\"" + imageurl + "\" target=\"_blank\"><img src=\""
									+ imagesrc + "\" " + width + " " + height + " border=\"0\" alt=\""
									+ imagealt + "\"></a>");
						} else if ("text".equals(type)) {
							String textcontent = (String) adCode.get("textcontent");
							String texturl = (String) adCode.get("texturl");
							Object textsize = adCode.get("textsize");
							String size = Common.empty(textsize) ? "" : "style=\"font-size:" + textsize
									+ "px;\"";
							html.append("<span style=\"padding:0.8em\"><a href=\"" + texturl
									+ "\" target=\"_blank\" " + size + ">" + textcontent + "</a></span>");
						}
						String tpl = JavaCenterHome.jchRoot + "data/adtpl/" + ad.get("adid") + ".htm";
						FileHelper.writeFile(tpl, html.toString(), request);
					}
				}
				if (cacheTypes == null || Common.in_array(cacheTypes, "network")) {
					File[] files = Common.readDir(JavaCenterHome.jchRoot + "data/cache", "txt");
					if (files != null) {
						for (File file : files) {
							if (file.isFile()) {
								file.delete();
							}
						}
					}
				}
				JavaCenterHome.jchConfig.clear();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=cache");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return showMessage(request, response, e1.getMessage());
		}
		return mapping.findForward("cache");
	}
}