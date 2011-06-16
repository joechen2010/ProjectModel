package com.joe.jsf.web.view;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.tree.IceUserObject;
import com.joe.jsf.helper.ManagedBeanUtility;
import com.joe.utilities.core.data.Node;
import com.joe.utilities.core.data.Tree;
import com.joe.utilities.core.lookup.CacheManager;
import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.util.MemoryCounter;
import com.joe.utilities.core.util.MemoryNode;

/**
 * The Class SessionMonitorBean. This class records session statistic related to
 * estimated memory size for active sessions. This class also serves as an
 * application scope JSF managed bean to service the "sessionMonitor.jspx" page.
 * 
 * @author Dave Ousey
 */
@ManagedBean(name="SessionMonitorBean")
@ApplicationScoped
public class SessionMonitorBean
{

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(SessionMonitorBean.class);

	/** Session monitoring enabled?. */
	private static boolean enabled = false;

	/** The current session state by session id. */
	private static Map<String, SessionMonitorState> currentSessionStateBySessionID = Collections.synchronizedMap(new TreeMap<String, SessionMonitorState>());

	/** Selected monitor state to view recorded detail. */
	private static SessionMonitorState selectedSessionMonitorState;

	/** The cache manager text. */
	private String cacheManagerText = "Not estimated";

	/** The cache manager memory tree. */
	private Tree<MemoryNode> cacheManagerMemoryTree = null;

	/** The cache manager memory tree model. */
	private DefaultTreeModel cacheManagerMemoryTreeModel = null;

	/** The service locator text. */
	private String serviceLocatorText = "Not estimated";

	/** The service locator memory tree. */
	private Tree<MemoryNode> serviceLocatorMemoryTree = null;

	/** The cache manager memory tree model. */
	private DefaultTreeModel serviceLocatorMemoryTreeModel = null;

	/** The stack level input. */
	private static int stackLevel = 5;

	/** The memory threshold input. */
	private static long memoryThreshold = 64L;

	/** The filter me decision classes. */
	private static boolean filterMEDecisionClasses = false;

	/**
	 * Default constructor.
	 */
	public SessionMonitorBean()
	{

	}

	/**
	 * Records session data.
	 * 
	 * @param session the session
	 */
	public static void recordSessionData(HttpSession session)
	{
		// Do nothing if session monitoring is not turned on.
		if (!enabled)
			return;

		// No session: Logoff scenario
		if (session == null)
			return;

		// Get session ID
		String sessionID = session.getId();
		if (sessionID == null)
			return;

		// Cache user on session

		// Lookup session state for session ID - create if necessary
		SessionMonitorState sessionState = currentSessionStateBySessionID.get(sessionID);
		if (sessionState == null)
		{
			sessionState = new SessionMonitorState(sessionID);
			currentSessionStateBySessionID.put(sessionID, sessionState);
		}

		// Update session state. If given session ID is marked as the record
		// session state, then pass a flag to generate complete memory tree.
		boolean createMemoryTree = (selectedSessionMonitorState != null && selectedSessionMonitorState.getSessionID().equals(sessionID));
		sessionState.updateSessionData(session, createMemoryTree, stackLevel, memoryThreshold*1024L);
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Invoke enable.
	 * 
	 * @param event the event
	 * 
	 * @return the string
	 */
	public String invokeEnable(ActionEvent event)
	{
		enabled = true;
		return null;
	}

	/**
	 * Invoke disable.
	 * 
	 * @param event the event
	 * 
	 * @return the string
	 */
	public String invokeDisable(ActionEvent event)
	{
		enabled = false;
		return null;
	}

	/**
	 * Clear. Action referenced by JSP
	 * 
	 * @param event the event
	 * 
	 * @return the string
	 */
	public String invokeClear(ActionEvent event)
	{
		currentSessionStateBySessionID.clear();
		selectedSessionMonitorState = null;
		return null;
	}

	/**
	 * Invoke refresh.
	 * 
	 * @param event the event
	 * 
	 * @return the string
	 */
	public String invokeRefresh(ActionEvent event)
	{
		return null;
	}

	/**
	 * Invoke detail.
	 * 
	 * @param event the event
	 * 
	 * @return the string
	 */
	public String invokeRecordSession(ActionEvent event)
	{
		String sessionID = ManagedBeanUtility.getRequestParameter("sessionID");
		selectedSessionMonitorState = currentSessionStateBySessionID.get(sessionID);
		return null;
	}

	/**
	 * Invoke estimate cache manager.
	 * 
	 * @param event the event
	 * 
	 * @return the string
	 */
	public String invokeEstimateCacheManager(ActionEvent event)
	{
		MemoryCounter memoryCounter = new MemoryCounter(false, stackLevel, memoryThreshold*1024L, true);

		long startTime = System.currentTimeMillis();
		memoryCounter.estimate(CacheManager.getInstance());
		long endTime = System.currentTimeMillis();
		cacheManagerText = "Last estimated at " + new Date();
		cacheManagerMemoryTree = memoryCounter.getMemoryTree();
		cacheManagerMemoryTreeModel = createICEFacesDefaultTreeModelFromTree(cacheManagerMemoryTree);
		log.warn("Session monitoring enabled: It took more than " + (endTime - startTime) + " milliseconds to estimate memory size for CacheManager");
		return null;
	}

	/**
	 * Invoke estimate service locator.
	 * 
	 * @param event the event
	 * 
	 * @return the string
	 */
	public String invokeEstimateServiceLocator(ActionEvent event)
	{
		MemoryCounter memoryCounter = new MemoryCounter(false, stackLevel, memoryThreshold*1024L, true);
		long startTime = System.currentTimeMillis();
		memoryCounter.estimate(ServiceLocator.getInstance());
		long endTime = System.currentTimeMillis();
		serviceLocatorText = "Last estimated at " + new Date();
		serviceLocatorMemoryTree = memoryCounter.getMemoryTree();
		serviceLocatorMemoryTreeModel = createICEFacesDefaultTreeModelFromTree(serviceLocatorMemoryTree);
		log.warn("Session monitoring enabled: It took more than " + (endTime - startTime) + " milliseconds to estimate memory size for ServiceLocator");
		return null;
	}

	/**
	 * Checks if is selected session monitor state exists.
	 * 
	 * @return true, if is selected session monitor state exists
	 */
	public boolean isSelectedSessionMonitorStateExists()
	{
		return selectedSessionMonitorState != null;
	}

	/**
	 * Gets the session monitor list.
	 * 
	 * @return the session monitor list
	 */
	public Collection<SessionMonitorState> getSessionMonitorList()
	{
		return new ArrayList<SessionMonitorState>(currentSessionStateBySessionID.values());
	}

	/**
	 * Checks if is monitor state exists.
	 * 
	 * @return true, if is monitor state exists
	 */
	public boolean isMonitorStateExists()
	{
		return currentSessionStateBySessionID.size() > 0;
	}

	/**
	 * Gets the last refresh time.
	 * 
	 * @return the last refresh time
	 */
	public Date getLastRefreshTime()
	{
		return new Date();
	}

	/**
	 * Gets the selected session monitor state.
	 * 
	 * @return the selected session monitor state
	 */
	public SessionMonitorState getSelectedSessionMonitorState()
	{
		return selectedSessionMonitorState;
	}

	/**
	 * Returns the default time zone from the JVM so that it can be used by the
	 * date/time converter to convert to the same time zone.
	 * 
	 * @return the time zone
	 */
	public TimeZone getTimeZone()
	{
		return TimeZone.getDefault();
	}

	/**
	 * Gets the cache manager text.
	 * 
	 * @return the cache manager text
	 */
	public String getCacheManagerText()
	{
		return cacheManagerText;
	}

	/**
	 * Gets the service locator text.
	 * 
	 * @return the service locator text
	 */
	public String getServiceLocatorText()
	{
		return serviceLocatorText;
	}

	/**
	 * Gets the total measured session size.
	 * 
	 * @return the total measured session size
	 */
	public long getTotalMeasuredSessionSize()
	{
		long total = 0;
		for (SessionMonitorState sessionState : currentSessionStateBySessionID.values())
		{
			total += sessionState.getCurrentSessionSize();
		}
		return total;
	}

	/**
	 * Creates the ice faces default tree model from tree.
	 * 
	 * @param tree the tree
	 * 
	 * @return the default tree model
	 */
	public static DefaultTreeModel createICEFacesDefaultTreeModelFromTree(Tree<MemoryNode> tree)
	{
		if (tree.getRootElement() != null)
		{
			DefaultMutableTreeNode rootIceNode = createICEFacesDefaultMutableTreeNode(tree.getRootElement());
			if (rootIceNode == null)
			{
				// Create default root node 
				rootIceNode = new DefaultMutableTreeNode();
				IceUserObject rootObject = new IceUserObject(rootIceNode);
				rootIceNode.setUserObject(rootObject);
				rootObject.setExpanded(false);
				rootObject.setLeaf(true);
				rootObject.setText("All items filtered for ");
				
				MemoryNode memoryNode = tree.getRootElement().getData();
				rootObject.setText("Filtered: " + memoryNode.getClassName() + ": [" + NumberFormat.getInstance().format(memoryNode.getMemory()) + " KB]");
			}
			
			return new DefaultTreeModel(rootIceNode);
		}
		else
			throw new IllegalArgumentException("Tree has no root element");
	}

	/**
	 * Creates the ice faces default mutable tree node.
	 * 
	 * @param node the node
	 * 
	 * @return the default mutable tree node
	 */
	public static DefaultMutableTreeNode createICEFacesDefaultMutableTreeNode(Node<MemoryNode> node)
	{
		List<Node<MemoryNode>> childNodes = node.getChildren();

		// Filter check
		if (filterMEDecisionClasses && !isMEDecisionNode(node))
			return null;
		
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
		IceUserObject rootObject = new IceUserObject(treeNode);
		treeNode.setUserObject(rootObject);
		rootObject.setExpanded(false);

		// Setup text
		MemoryNode memoryNode = node.getData();
		if (memoryNode.getFieldName() != null)
			rootObject.setText(memoryNode.getClassName() + ": [Field = '" + memoryNode.getFieldName() + "'] [" + NumberFormat.getInstance().format(memoryNode.getMemory()) + " KB]");
		else
			rootObject.setText(memoryNode.getClassName() + ": [" + NumberFormat.getInstance().format(memoryNode.getMemory()) + " KB]");

		// Deal with child nodes
		boolean addedChild = false;
		for (Node<MemoryNode> childNode : childNodes)
		{
			DefaultMutableTreeNode childIceNode = createICEFacesDefaultMutableTreeNode(childNode);
			if (childIceNode != null)
			{
				treeNode.add(childIceNode);
				addedChild = true;
			}
		}

		rootObject.setLeaf(!addedChild);
		
		return treeNode;
	}
	
	/**
	 * Checks if is mE decision node.
	 * @param node the node
	 * 
	 * @return true, if is mE decision node
	 */
	private static boolean isMEDecisionNode(Node<MemoryNode> node)
	{
		// Ask self
		if (node.getData().getClassName().startsWith("com.med"))
		{
			return true;
		}
		else 
		{
			// Ask children
			for (Node<MemoryNode> childNode : node.getChildren())
			{
				if (isMEDecisionNode(childNode))
					return true;
			}
		}
		
		return false;
	}

	/**
	 * Gets the cache manager memory tree.
	 * 
	 * @return the cache manager memory tree
	 */
	public DefaultTreeModel getCacheManagerMemoryTreeModel()
	{
		return cacheManagerMemoryTreeModel;
	}

	/**
	 * Gets the service locator memory tree.
	 * 
	 * @return the service locator memory tree
	 */
	public DefaultTreeModel getServiceLocatorMemoryTreeModel()
	{
		return serviceLocatorMemoryTreeModel;
	}

	/**
	 * Checks if is selected session detail tree exists.
	 * 
	 * @return true, if is selected session detail tree exists
	 */
	public boolean isSelectedSessionDetailTreeExists()
	{
		return selectedSessionMonitorState != null && selectedSessionMonitorState.getDetailMemoryTreeModel() != null;
	}

	/**
	 * Checks if is service locator memory tree exists.
	 * 
	 * @return true, if is service locator memory tree exists
	 */
	public boolean isServiceLocatorMemoryTreeExists()
	{
		return serviceLocatorMemoryTree != null;
	}

	/**
	 * Checks if is cache manager memory tree exists.
	 * 
	 * @return true, if is cache manager memory tree exists
	 */
	public boolean isCacheManagerMemoryTreeExists()
	{
		return cacheManagerMemoryTree != null;
	}

	/**
	 * Gets the selected session memory tree.
	 * 
	 * @return the selected session memory tree
	 */
	public DefaultTreeModel getSelectedSessionMemoryTreeModel()
	{
		return selectedSessionMonitorState.getDetailMemoryTreeModel();
	}

	/**
	 * Gets the stack level.
	 * 
	 * @return the stack level 
	 */
	public int getStackLevel()
	{
		return stackLevel;
	}

	/**
	 * Sets the stack level .
	 * 
	 * @param stackLevelInput the new stack level 
	 */
	public void setStackLevel(int stackLevel)
	{
		SessionMonitorBean.stackLevel = stackLevel;
	}

	/**
	 * Gets the memory threshold .
	 * 
	 * @return the memory threshold 
	 */
	public long getMemoryThreshold()
	{
		return memoryThreshold;
	}

	/**
	 * Sets the memory threshold input.
	 * 
	 * @param memoryThresholdInput the new memory threshold input
	 */
	public void setMemoryThreshold(long memoryThreshold)
	{
		SessionMonitorBean.memoryThreshold = memoryThreshold;
		
	}

	/**
	 * Checks if is filter me decision classes.
	 * 
	 * @return true, if is filter me decision classes
	 */
	public boolean isFilterMEDecisionClasses()
	{
		return SessionMonitorBean.filterMEDecisionClasses;
	}

	/**
	 * Sets the filter me decision classes.
	 * 
	 * @param filterMEDecisionClasses the new filter me decision classes
	 */
	public void setFilterMEDecisionClasses(boolean filterMEDecisionClasses)
	{
		SessionMonitorBean.filterMEDecisionClasses = filterMEDecisionClasses;
	}
 
}
