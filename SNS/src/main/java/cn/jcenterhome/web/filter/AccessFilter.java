package cn.jcenterhome.web.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class AccessFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		PrintWriter out = res.getWriter();
		out.write("Access Denied");
		out.flush();
		out.close();
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}
