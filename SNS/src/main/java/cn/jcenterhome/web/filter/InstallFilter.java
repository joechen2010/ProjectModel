package cn.jcenterhome.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.jcenterhome.util.JavaCenterHome;


public class InstallFilter implements Filter {
	public void init(FilterConfig fc) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		request.setCharacterEncoding(JavaCenterHome.JCH_CHARSET);
		response.setCharacterEncoding(JavaCenterHome.JCH_CHARSET);
		response.setContentType("text/html; charset=" + JavaCenterHome.JCH_CHARSET);
		if (JavaCenterHome.jchRoot == null) {
			HttpSession session = request.getSession();
			JavaCenterHome.jchRoot = session.getServletContext().getRealPath("/");
		}
		request.setAttribute("IN_JCHOME", JavaCenterHome.IN_JCHOME);
		request.setAttribute("JCH_VERSION", JavaCenterHome.JCH_VERSION);
		request.setAttribute("JCH_RELEASE", JavaCenterHome.JCH_RELEASE);
		request.getRequestDispatcher("/install/index.jsp").forward(request, response);
		return;
	}

	public void destroy() {
	}
}