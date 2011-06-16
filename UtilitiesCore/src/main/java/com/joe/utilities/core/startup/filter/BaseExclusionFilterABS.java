package com.joe.utilities.core.startup.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
* VerifyAuthentication is a SecuritySVC Helper class designed to help centralize security for http applications.
* It uses <code>SecurityHelper</code> to accomplish the following on a per request bases:<br>
* <ul>
* <li>Checks if SecurityToken is valid. 
* <br>{@link com.med.security.presentation.SecurityHelper#isValidSecurityToken(HttpServletRequest request, HttpSession session)}
* <li>Checks if the user has been authenticated. 
* <br>{@link com.med.security.presentation.SecurityHelper#isValidUser(HttpSession session)}
* <li>Checks if the user has access to the Page. 
* <br>{@link com.med.security.presentation.SecurityHelper#isValidAccess(HttpServletRequest request, HttpSession session)}
* <li>Generates a new SecuirtyToken. 
* <br>{@link com.med.security.presentation.SecurityHelper#genSecurityToken(HttpSession session, HttpServletResponse response)}
* </ul>
* 
* @author rrichard
* @see com.med.security.authenticate.AuthenticationProvider
* @see com.med.security.presentation.tag.Authorized
* @see com.med.security.presentation.SecurityHelper
*
*/
public abstract class BaseExclusionFilterABS implements Filter {
	private static Log logger = LogFactory.getLog(BaseExclusionFilterABS.class);
	private LinkedList excludePatterns;
	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	
	public BaseExclusionFilterABS() {
		super();
	}

	/* (non-Java-doc)
	 * @see javax.servlet.Filter#init(FilterConfig arg0)
	 */
	  public void init( FilterConfig config ) throws ServletException
	  {
	    // parse all of the initialization parameters, collecting the exclude
	    // patterns and the max wait parameters
	    Enumeration enumeration = config.getInitParameterNames();
	    excludePatterns = new LinkedList();
	    while( enumeration.hasMoreElements() )
	    {
	      String paramName = ( String )enumeration.nextElement();
	      String paramValue = config.getInitParameter( paramName );
	      if( paramName.startsWith( "excludePattern" ) )
	      {
	        // compile the pattern only this once
	        Pattern excludePattern = Pattern.compile( paramValue );
	        excludePatterns.add( excludePattern );
	      }
	    }
        doMyInit(config);
	  }
      

	/* (non-Java-doc)
	 * @see javax.servlet.Filter#doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
       	int contextEnd = request.getContextPath().length() + 1;
       	String appResource = request.getRequestURI().substring(contextEnd);
        if (logger.isDebugEnabled()) logger.debug("appResource:=" + appResource);
	    if( isFilteredRequest(appResource)) {
	      chain.doFilter( request, response );
	    } else {
	        doMyFilter(req, resp, chain);
        }
	}
    

	  /**
	   * Look through the filter's configuration, and determine whether or not it
	   * should synchronize this request with others.
	   *
	   * @param httpRequest
	   * @return
	   */
	  private boolean isFilteredRequest(String path)
	  {
	    // iterate through the exclude patterns.  If one matches this path,
	    // then the request is excluded.
	    Iterator patternIter = excludePatterns.iterator();
		if (logger.isDebugEnabled()) logger.debug(".isFilteredRequest() path:=" + path);
	    while(patternIter.hasNext()) {
	      Pattern p = (Pattern)patternIter.next();
          if (logger.isDebugEnabled())logger.debug(".isFilteredRequest() p:=" + p.pattern());
	      Matcher m = p.matcher( path );
	      if(m.matches()) {
	        // at least one of the patterns excludes this request
            if (logger.isDebugEnabled()) logger.debug(".isFilteredRequest() Match Found");
	        return true;
	      }
	    }	    
	    // this path is not excluded
	    return false;
	  }

	  /* (non-Java-doc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
        doMyDestroy();
	}
    
    public abstract void doMyInit(FilterConfig config) throws ServletException;
    public abstract void doMyFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException;
    public abstract void doMyDestroy();

}