package com.joe.utilities.core.web.filter;

import javax.servlet.*;    
import javax.servlet.http.HttpServletResponse;    
import java.io.IOException;    
   
  
public class ForceNoCacheFilter implements Filter {    
   
public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException    
{    
   ((HttpServletResponse) response).setHeader("Cache-Control","no-cache");    
   ((HttpServletResponse) response).setHeader("Pragma","no-cache");    
   ((HttpServletResponse) response).setDateHeader ("Expires", -1);    
   filterChain.doFilter(request, response);    
}    
   
	public void destroy()    {}    
   
     public void init(FilterConfig filterConfig) throws ServletException {}
}    
