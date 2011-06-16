package com.joe.utilities.core.startup.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.utilities.core.util.ReturnStatus;



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
/**
 * @author rrichard
 * 
 */
public final class ApplicationAvailableCheck extends BaseExclusionFilterABS implements Filter {
    private static Log logger = LogFactory.getLog(ApplicationAvailableCheck.class);

    private boolean appAvailable;

    private ReturnStatus errors;
    
    private Throwable startupException;

    /**
     * Default constructor
     */
    public ApplicationAvailableCheck() {
        super();
        appAvailable = true;
        errors = null;
        startupException = null;
    }


    /**
     * @see com.med.careplannerweb.startup.filter.BaseExclusionFilterABS#doMyInit(javax.servlet.FilterConfig)
     */
    public void doMyInit(FilterConfig config) throws ServletException {
        logger.debug("doMyInit:  Entering");
        Boolean working = (Boolean) config.getServletContext().getAttribute("Working");

        if (working != null) {
            this.appAvailable = working.booleanValue();
            this.errors = (ReturnStatus) config.getServletContext().getAttribute("Messages");
            this.startupException = (Throwable) config.getServletContext().getAttribute("Exception");
        }
        logger.debug("appAvailable:=" + this.appAvailable);
        logger.debug("errors:=" + this.errors);
        logger.debug("doMyInit:  Exiting");
    }

    /**
     * @see com.med.careplannerweb.startup.filter.BaseExclusionFilterABS#doMyFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doMyFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        
        HttpServletResponse response = (HttpServletResponse) resp;

        if (this.appAvailable) 
        {
        	long start = System.currentTimeMillis();
            chain.doFilter(req, resp);
            long end = System.currentTimeMillis();
            
            // Log lengthy transactions to info
            if (logger.isInfoEnabled() && (end - start > 10000))
            	logger.info("doMyFilter: Transaction time > 10 seconds: ("+(end-start)+" ms): Parameters = " + req.getParameterMap());
            else if (logger.isDebugEnabled())
            	logger.debug("doMyFilter: Processing Time in Miliseconds:" + (end-start));
            
            // Session monitor
           // SessionMonitorBean.recordSessionData(((HttpServletRequest)req).getSession(false));
            
        } 
        else 
        {
            logger.warn("The Application was marked unavailable");
            req.setAttribute("Messages", this.errors);
            req.setAttribute("Exception", this.startupException);
            response.sendError(503);
        }
    }

    /**
     * @see com.med.careplannerweb.startup.filter.BaseExclusionFilterABS#doMyDestroy()
     */
    public void doMyDestroy() {
    }

}