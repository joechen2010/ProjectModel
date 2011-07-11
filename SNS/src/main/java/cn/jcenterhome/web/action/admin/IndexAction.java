package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.web.action.BaseAction;


public class IndexAction extends BaseAction {

	@SuppressWarnings("unchecked")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Map<String, Integer>> menus = (Map<String, Map<String, Integer>>) request
				.getAttribute("menus");
		if (!Common.empty(menus.get("menu0").get("config"))) {
			String jchRoot = JavaCenterHome.jchRoot;
			File installFile = new File(jchRoot + "install/index.jsp");
			File lockFile = new File(jchRoot + "data/install.lock");
			if (installFile.exists() && !lockFile.exists()) {
				try {
					lockFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Map<String, Object> statistics = getStatistics(request);
			HttpSession session = request.getSession(false);
			ModuleConfig ac = (ModuleConfig) session.getServletContext().getAttribute(Globals.MODULE_KEY);
			String fileUpload = ac.getControllerConfig().getMaxFileSize();
			long size = (Long) statistics.get("dbsize");
			String attachSize = request.getParameter("attachsize");
			if (attachSize == null) {
				attachSize = "<a href=\"admincp.jsp?attachsize\">------</a>";
			} else {
				attachSize = dataBaseService.findFirst("SELECT SUM(size) as totalsize FROM "
						+ JavaCenterHome.getTableName("pic"), 1);
				attachSize = Common.empty(attachSize) ? "unknown" : Common.formatSize(Long
						.parseLong(attachSize));
			}

			request.setAttribute("os", System.getProperty("os.name") + " / JDK v" + statistics.get("jdk"));
			request.setAttribute("serverInfo", servlet.getServletContext().getServerInfo());
			request.setAttribute("fileupload",
					Common.empty(fileUpload) ? "<font color=\"red\">Prohibition</font>" : fileUpload);
			request.setAttribute("dbsize", size > 0 ? Common.formatSize(size) : "unknown");
			request.setAttribute("attachsize", attachSize);
			request.setAttribute("statistics", statistics);
			request.setAttribute("my_checkupdate", Common.myCheckUpdate(request, response));
		}
		return mapping.findForward("index");
	}

	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getStatistics(HttpServletRequest request) {
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;

		long dbSize = dataBaseService.findTableSize("SHOW TABLE STATUS LIKE '"
				+ jchConfig.get("tablePre") + "%'");

		String siteKey = Common.trim((String) sConfig.get("sitekey"));
		if (Common.empty(siteKey)) {
			siteKey = mkSiteKey(request);
			dataBaseService.executeUpdate("REPLACE INTO " + JavaCenterHome.getTableName("config")
					+ " (var, datavalue) VALUES ('sitekey', '" + siteKey + "')");
			try {
				cacheService.config_cache();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> statistics = new HashMap<String, Object>();
		statistics.put("sitekey", siteKey + 1);
		statistics.put("version", JavaCenterHome.JCH_VERSION);
		statistics.put("release", JavaCenterHome.JCH_RELEASE);
		statistics.put("jdk", System.getProperty("java.version"));
		statistics.put("mysql", dataBaseService.showVersion());
		statistics.put("dbsize", dbSize);
		statistics.put("charset", jchConfig.get("charset"));
		statistics.put("sitename", sConfig.get("sitename").toString().replaceAll("(?s)[\'\"\\s]", ""));
		statistics.put("feednum", Common.getCount("feed", null, null));
		statistics.put("blognum", Common.getCount("blog", null, null));
		statistics.put("albumnum", Common.getCount("pic", null, null));
		statistics.put("threadnum", Common.getCount("thread", null, null));
		statistics.put("sharenum", Common.getCount("share", null, null));
		statistics.put("commentnum", Common.getCount("comment", null, null));
		statistics.put("myappnum", Common.getCount("myapp", null, null));
		statistics.put("spacenum", Common.getCount("space", null, null));
		String charset = "gbk";
		statistics.put("update", Common.urlEncode(Serializer.serialize(statistics, charset), charset) + "&h=" + Common.md5(request.getHeader("User-Agent") + "|" + Common.implode(statistics, "|")).substring(8, 16));
		return statistics;
	}

	
	@SuppressWarnings("unchecked")
	private String mkSiteKey(HttpServletRequest request) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		StringBuffer siteStr = new StringBuffer();
		siteStr.append(request.getLocalAddr());
		siteStr.append(request.getHeader("User-Agent"));
		siteStr.append(jchConfig.get("dbHost"));
		siteStr.append(jchConfig.get("dbUser"));
		siteStr.append(jchConfig.get("dbPw"));
		siteStr.append(jchConfig.get("dbName"));
		siteStr.append(sGlobal.get("timestamp").toString().substring(0, 6));
		return Common.md5(siteStr.toString()).substring(8, 14) + Common.getRandStr(10, false);
	}
}