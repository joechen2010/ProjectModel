package com.joe.jsf.web.view;

import javax.servlet.http.HttpSession;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.utilities.core.data.Tree;
import com.joe.utilities.core.util.MemoryCounter;
import com.joe.utilities.core.util.MemoryNode;

/**
 * The Class SessionMonitorState.
 * 
 * @author dousey
 */
public class SessionMonitorState
{
	
	/** The Constant log. */
	private static final Log log = LogFactory.getLog(SessionMonitorState.class);
	
	/** The current session size. */
	private long currentSessionSize;

	/** The session id. */
	private String sessionID;

	/** The user id. */
	private String userID;

	/** The user name. */
	private String userName;

	/** The page hits. */
	private long pageHits;

	/** The max session size ever for this session. */
	private long maxSessionSize;
	
	/** Tree view of session. */
	private Tree<MemoryNode> detailMemoryTree;
	
	/** The cache manager memory tree model. */
	private DefaultTreeModel detailMemoryTreeModel = null;

	/**
	 * Instantiates a new session monitor state.
	 * 
	 * @param sessionID the session id
	 */
	public SessionMonitorState(String sessionID)
	{
		super();
		this.sessionID = sessionID;
		this.userID = "???";
		this.userName = "Not logged in yet";
	}

	/**
	 * Update.
	 * 
	 * @param session the session
	 * @param createMemoryTree Create a memory tree
	 * @param reportStackLevel the report stack level
	 * @param reportMemoryMinimum the report memory minimum
	 */
	public void updateSessionData(HttpSession session, boolean createMemoryTree, int reportStackLevel, long reportMemoryMinimum)
	{
		
		// Increment # of page hits (really filter hits for this session)
		pageHits++;
		
		long startSessionMeasureTime = System.currentTimeMillis();
	
		MemoryCounter memoryCounter = new MemoryCounter(true, reportStackLevel, reportMemoryMinimum, createMemoryTree);
		long size = memoryCounter.estimate(session)/1024L;
		
		// Update memory tree
		if (createMemoryTree)
		{
			clearDetailMemoryTree();
			detailMemoryTree = memoryCounter.getMemoryTree();
			detailMemoryTreeModel = SessionMonitorBean.createICEFacesDefaultTreeModelFromTree(detailMemoryTree);
		}
	
		// Update max session size if necessary
		this.currentSessionSize = size;
		if (this.maxSessionSize < currentSessionSize)
			this.maxSessionSize = currentSessionSize;

		// Trace message that measurement took a while
		long endSessionMeasureTime = System.currentTimeMillis();
		String time = "" + ((endSessionMeasureTime - startSessionMeasureTime)/1000L);
		log.warn("Session monitoring enabled: It took more than "+time+" seconds to estimate memory size for session of user: " + this.userName);
	}

	/**
	 * Gets the current session size.
	 * 
	 * @return the current session size
	 */
	public long getCurrentSessionSize()
	{
		return currentSessionSize;
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public String getSessionID()
	{
		return sessionID;
	}

	/**
	 * Gets the user id.
	 * 
	 * @return the user id
	 */
	public String getUserID()
	{
		return userID;
	}

	/**
	 * Gets the user name.
	 * 
	 * @return the user name
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Gets the page hits.
	 * 
	 * @return the page hits
	 */
	public long getPageHits()
	{
		return pageHits;
	}

	/**
	 * Gets the max session size.
	 * 
	 * @return the max session size
	 */
	public long getMaxSessionSize()
	{
		return maxSessionSize;
	}

	/**
	 * Gets the detail memory tree.
	 * 
	 * @return the detail memory tree
	 */
	public DefaultTreeModel getDetailMemoryTreeModel()
	{
		return detailMemoryTreeModel;
	}

	/**
	 * Clear detail memory tree.
	 */
	public void clearDetailMemoryTree()
	{
		if (this.detailMemoryTree != null)
		{
			this.detailMemoryTree.clear();
			this.detailMemoryTree = null;
		}
	}
}
