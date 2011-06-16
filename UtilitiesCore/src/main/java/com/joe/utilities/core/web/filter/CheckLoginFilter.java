package com.joe.utilities.core.web.filter;


import javax.servlet.*;    
import javax.servlet.http.HttpServletRequest;    
import javax.servlet.http.HttpServletResponse;    
import javax.servlet.http.HttpSession;    
import java.util.List;    
import java.util.ArrayList;    
import java.util.StringTokenizer;    
import java.io.IOException;    
   

public class CheckLoginFilter    implements Filter    
{    
     protected FilterConfig filterConfig = null;    
     private String redirectURL = null;    
     private List notCheckURLList = new ArrayList();    
     private String sessionKey = null;    
   
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException    
{    
   HttpServletRequest request = (HttpServletRequest) servletRequest;    
   HttpServletResponse response = (HttpServletResponse) servletResponse;    
   
    HttpSession session = request.getSession();    
   if(sessionKey == null)    
   {    
    filterChain.doFilter(request, response);    
    return;    
   }    
   if((!checkRequestURIIntNotFilterList(request)) && session.getAttribute(sessionKey) == null)    
   {    
    response.sendRedirect(request.getContextPath() + redirectURL);    
    return;    
   }    
   filterChain.doFilter(servletRequest, servletResponse);    
}    
   
public void destroy()    
{    
   notCheckURLList.clear();    
}    
   
private boolean checkRequestURIIntNotFilterList(HttpServletRequest request)    
{    
   String uri = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());    
   return notCheckURLList.contains(uri);    
}    
   
	public void init(FilterConfig filterConfig) throws ServletException    
	{    
	   this.filterConfig = filterConfig;    
	   redirectURL = filterConfig.getInitParameter("redirectURL");    
	   sessionKey = filterConfig.getInitParameter("checkSessionKey");    
	   
	   String notCheckURLListStr = filterConfig.getInitParameter("notCheckURLList");    
	   
	   if(notCheckURLListStr != null)    
	   {    
	    StringTokenizer st = new StringTokenizer(notCheckURLListStr, ";");    
	    notCheckURLList.clear();    
	    while(st.hasMoreTokens())    
	    {    
	     notCheckURLList.add(st.nextToken());    
	    }    
	   }    
	}    
	
	
	
	
}    

