package cn.jcenterhome.web.filter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.service.CacheService;
import cn.jcenterhome.service.DataBaseService;
import cn.jcenterhome.service.SpaceService;
import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.CookieHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.SessionFactory;


public class CommonFilter implements Filter {
	private String[] cacheNames = {"app", "userapp", "ad", "magic"};
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
	private CacheService cacheService = (CacheService) BeanFactory.getBean("cacheService");
	private SpaceService spaceService = (SpaceService) BeanFactory.getBean("spaceService");

	public void init(FilterConfig fc) throws ServletException {
	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		request.setAttribute("IN_JCHOME", JavaCenterHome.IN_JCHOME);
		request.setAttribute("JCH_VERSION", JavaCenterHome.JCH_VERSION);
		request.setAttribute("JCH_RELEASE", JavaCenterHome.JCH_RELEASE);
		Map<String, Object> sGlobal = new HashMap<String, Object>();
		long currentTime = System.currentTimeMillis();
		int timestamp = (int) (currentTime / 1000);
		sGlobal.put("timestamp", timestamp);
		sGlobal.put("starttime", currentTime);
		request.setAttribute("sGlobal", sGlobal);

		Map<String, String> sCookie = CookieHelper.getCookies(request);
		Map<Integer, String> sNames = new HashMap<Integer, String>();
		Map<String, Object> space = new HashMap<String, Object>();
		request.setAttribute("sCookie", sCookie);
		request.setAttribute("sNames", sNames);
		request.setAttribute("space", space);
		if (SessionFactory.getSessionFactory() == null) {
			try {
				SessionFactory.buildSessionFactory();
			} catch (SQLException e) {
				JavaCenterHome.jchConfig.clear();
				Common.showMySQLMessage(response, "Can not connect to MySQL server", null, e);
				return;
			}
		}
		String jchRoot = JavaCenterHome.jchRoot;
		try {
			File configFile = new File(jchRoot + "data/cache/cache_config.jsp");
			if (!configFile.exists()) {
				cacheService.updateCache();
			}
			request.getRequestDispatcher("/data/cache/cache_config.jsp").include(request, response);
		} catch (Exception e) {
			response.getWriter().write(e.getMessage());
			return;
		}
		for (String cacheName : cacheNames) {
			request.getRequestDispatcher("/data/cache/cache_" + cacheName + ".jsp")
					.include(request, response);
		}

		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		if (Common.empty(jchConfig.get("sitename"))) {
			List<Map<String, Object>> configs = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("config")
					+ " WHERE var IN ('jchid', 'sitename', 'close', 'adminemail', 'lastupdate')");
			for (Map<String, Object> config : configs) {
				String variable = (String) config.get("var");
				String value = (String) config.get("datavalue");
				if (variable != null) {
					jchConfig.put(variable, value);
				}
			}
		}
		String sitekey = (String) sConfig.get("sitekey");
		String mobile = request.getParameter("mobile");
		String m_timestamp = request.getParameter("m_timestamp");
		if (Common.empty(m_timestamp) || !Common.md5(m_timestamp + "\t" + sitekey).equals(mobile)) {
			mobile = null;
		}
		sGlobal.put("super_uid", 0);
		sGlobal.put("supe_username", "");
		sGlobal.put("inajax", Common.intval(request.getParameter("inajax")));
		sGlobal.put("mobile", mobile);
		sGlobal.put("refer", Common.trim(request.getHeader("Referer")));
		if (Common.empty((String) sConfig.get("login_action"))) {
			sConfig.put("login_action", Common.md5("login" + Common.md5(sitekey)));
		}
		if (Common.empty((String) sConfig.get("register_action"))) {
			sConfig.put("register_action", Common.md5("register" + Common.md5(sitekey)));
		}
		if (Common.empty((String) sConfig.get("template"))) {
			sConfig.put("template", "default");
		}
		String myTemplate = sCookie.get("mytemplate");
		if (!Common.empty(myTemplate)) {
			myTemplate = myTemplate.trim().replace(".", "");
			File styleFile = new File(jchRoot + "template/" + myTemplate + "/style.css");
			if (styleFile.exists()) {
				sConfig.put("template", myTemplate);
			} else {
				CookieHelper.removeCookie(request, response, "mytemplate");
			}
		}
		String requestURI = (String) request.getAttribute("requestURI");
		if (requestURI == null) {
			String queryString = request.getQueryString();
			if (Common.empty(queryString)) {
				requestURI = request.getRequestURI();
			} else {
				requestURI = request.getRequestURI() + "?" + queryString;
			}
			request.setAttribute("requestURI", requestURI);
			if (requestURI != null) {
				String temp = Common.urlEncode(requestURI);
				if (temp.indexOf("<") >= 0 || temp.indexOf("\"") >= 0) {
					Map params = request.getParameterMap();
					Common.sHtmlSpecialChars(params);
				}
			}
		}
		checkAuth(request, response, sGlobal, sConfig, sCookie);
		sGlobal.put("uhash", Common.md5(sGlobal.get("supe_uid") + "\t"
				+ String.valueOf(timestamp).substring(0, 6)));
		getUserApp(request, sGlobal, sConfig);
		chain.doFilter(req, res);
	}

	
	private void checkAuth(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sGlobal, Map<String, Object> sConfig, Map<String, String> sCookie) {
		String m_auth = request.getParameter("m_auth");
		if (sGlobal.get("mobile") != null && m_auth != null) {
			sCookie.put("auth", m_auth);
		}
		String username = null;
		String auth = sCookie.get("auth");
		if (auth != null && auth.length() > 0) {
			String[] values = Common.authCode(auth, "DECODE", null, 0).split("\t");
			if (values.length > 1) {
				String password = values[0];
				int supe_uid = Common.intval(values[1]);
				if (password.length() > 0 && supe_uid > 0) {
					List<Map<String, Object>> members = dataBaseService.executeQuery("SELECT * FROM "
							+ JavaCenterHome.getTableName("session") + " WHERE uid=" + supe_uid);
					if (members.size() > 0) {
						Map<String, Object> member = members.get(0);
						if (((String) member.get("password")).equals(password)) {
							username = (String) member.get("username");
							sGlobal.put("supe_username", Common.addSlashes(username));
							sGlobal.put("session", member);
						} else {
							supe_uid = 0;
						}
					} else {
						members = dataBaseService.executeQuery("SELECT * FROM "
								+ JavaCenterHome.getTableName("member") + " WHERE uid=" + supe_uid);
						if (members.size() > 0) {
							Map<String, Object> member = members.get(0);
							if (((String) member.get("password")).equals(password)) {
								username = (String) member.get("username");
								String supe_username = Common.addSlashes(username);
								sGlobal.put("supe_username", supe_username);
								spaceService.insertSession(request, response, sGlobal, sConfig, supe_uid,
										supe_username, password);
							} else {
								supe_uid = 0;
							}
						} else {
							supe_uid = 0;
						}
					}
				} else {
					supe_uid = 0;
				}
				sGlobal.put("supe_uid", supe_uid);
			}
		}
		if (Common.empty(sGlobal.get("supe_uid"))) {
			CookieHelper.clearCookie(request, response);
		} else {
			sGlobal.put("username", username);
		}
	}

	
	private void getUserApp(HttpServletRequest request, Map<String, Object> sGlobal,
			Map<String, Object> sConfig) {
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		int my_status = (Integer) sConfig.get("my_status");

		Map<Integer, Map<String, Object>> my_userapp = new HashMap<Integer, Map<String, Object>>();
		List<Map<String, Object>> my_menu = new ArrayList<Map<String, Object>>();
		int my_menu_more = 0;
		if (supe_uid > 0 && my_status > 0) {
			Map<String, Object> space = Common.getSpace(request, sGlobal, sConfig, supe_uid);
			int showCount = 0;
			List<Map<String, Object>> userApps = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("userapp") + " WHERE uid=" + supe_uid
					+ " ORDER BY menuorder DESC");
			if (userApps.size() > 0) {
				Map<Integer, Map<String, Object>> userApp = (Map<Integer, Map<String, Object>>) request
						.getAttribute("globalUserApp");
				for (Map<String, Object> value : userApps) {
					int appId = (Integer) value.get("appid");
					my_userapp.put(appId, value);
					if ((Integer) value.get("allowsidenav") > 0 && userApp.get(appId) == null) {
						int menuNum = (Integer) space.get("menunum");
						if (menuNum < 5) {
							menuNum = 10;
						}
						if (menuNum > 100 || showCount < menuNum) {
							my_menu.add(value);
							showCount++;
						} else {
							my_menu_more = 1;
						}
						space.put("menunum", menuNum);
					}
				}
			}
		}
		sGlobal.put("my_userapp", my_userapp);
		sGlobal.put("my_menu", my_menu);
		sGlobal.put("my_menu_more", my_menu_more);
	}

	public void destroy() {
	}
}