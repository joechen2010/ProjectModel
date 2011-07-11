package cn.jcenterhome.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CookieHelper {
	
	public static Map<String, String> getCookies(HttpServletRequest request) {
		Map<String, String> sCookie = new HashMap<String, String>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			Map<String, String> jchConfig = JavaCenterHome.jchConfig;
			String cookiePre = jchConfig.get("cookiePre");
			int prelength = Common.strlen(cookiePre);
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				if (name.startsWith(cookiePre)) {
					sCookie.put(name.substring(prelength), Common.urlDecode(Common.addSlashes(cookie
							.getValue())));
				}
			}
		}
		return sCookie;
	}

	
	public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String key) {
		setCookie(request, response, key, "", 0);
	}

	
	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String key,
			String value) {
		setCookie(request, response, key, value, -1);
	}

	
	@SuppressWarnings("unchecked")
	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String key,
			String value, int maxAge) {
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		Cookie cookie = new Cookie(jchConfig.get("cookiePre") + key, Common.urlEncode(value));
		cookie.setMaxAge(maxAge);
		cookie.setPath(jchConfig.get("cookiePath"));
		if (!Common.empty(jchConfig.get("cookieDomain"))) {
			cookie.setDomain(jchConfig.get("cookieDomain"));
		}
		cookie.setSecure(request.getServerPort() == 443 ? true : false);
		response.addCookie(cookie);
	}

	
	@SuppressWarnings("unchecked")
	public static void clearCookie(HttpServletRequest request, HttpServletResponse response) {
		removeCookie(request, response, "auth");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		sGlobal.put("supe_uid", 0);
		sGlobal.put("supe_username", "");
		sGlobal.remove("member");
	}
}