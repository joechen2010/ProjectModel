
package cn.jcenterhome.web.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.PropertiesHelper;


public class InitFilter implements Filter {
	public void init(FilterConfig fc) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		request.setCharacterEncoding(JavaCenterHome.JCH_CHARSET);
		response.setCharacterEncoding(JavaCenterHome.JCH_CHARSET);
		try {
			Map params = request.getParameterMap();
			Method method = params.getClass().getMethod("setLocked",boolean.class);
			method.invoke(params,false);
			Common.sAddSlashes(params);
		} catch (Exception e) {
		}
		if (JavaCenterHome.jchRoot == null) {
			HttpSession session = request.getSession();
			JavaCenterHome.jchRoot = session.getServletContext().getRealPath("/");
		}
		Map<String, String> jchConfig = JavaCenterHome.jchConfig;
		if (jchConfig.isEmpty()) {
			try {
				initConfig(request, jchConfig);
			} catch (IOException e) {
				response.getWriter().write("读取配置文件(./config.properties)出错：" + e.getMessage());
				return;
			}
		}
		chain.doFilter(req, res);
	}

	
	private synchronized void initConfig(HttpServletRequest request, Map<String, String> jchConfig)
			throws IOException {
		PropertiesHelper propHelper = new PropertiesHelper(JavaCenterHome.jchRoot + "config.properties");
		Properties config = propHelper.getProperties();
		Set<Object> keys = config.keySet();
		for (Object key : keys) {
			String k = (String) key;
			String v = (String) config.get(key);
			jchConfig.put(k, v);
		}
		String siteUrl = jchConfig.get("siteUrl");
		if (Common.empty(siteUrl)) {
			jchConfig.put("siteUrl", Common.getSiteUrl(request));
		}
		ServletContext servletContext=request.getSession().getServletContext();
		servletContext.setAttribute("jchConfig", JavaCenterHome.jchConfig);
	}

	public void destroy() {
	}
}