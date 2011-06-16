package com.joe.utilities.core.startup.filter;

import java.io.IOException;
import java.util.Random;

import javax.naming.AuthenticationException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.SessionManager;

import com.joe.utilities.core.logging.LoggingContextHelper;

public final class LoggingContextFilter implements Filter {
    private static Log logger = LogFactory.getLog(LoggingContextFilter.class);


    /**
     * Default constructor
     */
    public LoggingContextFilter() {
        super();
    }


	public void destroy() {
		if (logger.isDebugEnabled()) logger.debug("destroy:  Entering");
		
	}


	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		if (logger.isDebugEnabled()) logger.debug("doFilter:  Entering");

		LoggingContextHelper.initializeUserId();
		LoggingContextHelper.initializeTransactionId();
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession(true);
		try
		{
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(LoggingContextHelper.getValueFromContext(LoggingContextHelper.TRANSACTION_ID) + 
					" - User ID: " + 
					LoggingContextHelper.getValueFromContext(LoggingContextHelper.USER_ID));
		}
		chain.doFilter(req, resp);
	}


	public void init(FilterConfig arg0) throws ServletException {
		if (logger.isDebugEnabled()) logger.debug("init:  Entering");
		
	}

}