package com.joe.utilities.core.hibernate.repository.impl;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.utilities.core.hibernate.repository.CacheFlushRepository;
import com.joe.utilities.core.lookup.CacheManager;
import com.joe.utilities.core.lookup.LookupManager;

/**
 * This repository class contains logic for both subscription (onMessage) and
 * publishing (broadcast*) to inform subscribers (usually is disparate
 * processes) to flush their respective JVN CacheManager instances.
 * 
 * @author dousey
 * 
 */
public class CacheFlushRepositoryJMSImpl implements CacheFlushRepository, MessageListener
{

	private static Log log = LogFactory.getLog(CacheFlushRepositoryJMSImpl.class);
	
	private static String FLUSH_LEVEL_FULL = "full";
	private static String FLUSH_LEVEL_MCO = "mco";
	private static String FLUSH_LEVEL_GROUP = "group";
	private static String FLUSH_LEVEL_ITEM = "item";

	/** Default constructor */
	public CacheFlushRepositoryJMSImpl()
	{
		super();
	}

	
	/**
	 * onMessage - This method is called asynchronously in response (subscription side) to a
	 * published cache flush broadcast
	 * 
	 * @param message
	 * void
	 */
	public void onMessage(Message message)
	{
		// Expecting a MapMessage
		MapMessage mapMessage = (MapMessage) message;

		// Extract cache flush instructions
		String level;
		String groupName;
		String cacheKey;
		String mcoID;
		try
		{
			level = mapMessage.getString("level");
			mcoID = mapMessage.getString("mcoID");
			groupName = mapMessage.getString("groupName");
			cacheKey = mapMessage.getString("cacheKey");
		}
		catch (JMSException e)
		{
			throw new RuntimeException("Unexpected JMSException while reading cache flush message", e);
		}
		
		// Process the flush
		processFlush(mcoID, level, groupName, cacheKey);
	}

	/**
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlush()
	 */
	public void broadcastCacheFlush()
	{
		publishMessage(FLUSH_LEVEL_FULL, null, null, null);
	}

	/**
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlushGroup(java.lang.String)
	 */
	public void broadcastCacheFlushGroup(String groupName)
	{
		publishMessage(FLUSH_LEVEL_GROUP, null, groupName, null);
	}

	/**
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlushItem(java.lang.String)
	 */
	public void broadcastCacheFlushItem(String cacheKey)
	{
		publishMessage(FLUSH_LEVEL_ITEM, null, null, cacheKey);
	}

	/**
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlushGroup(java.lang.String, java.lang.String)
	 */
	public void broadcastCacheFlushGroup(String mcoID, String groupName)
	{
		publishMessage(FLUSH_LEVEL_GROUP, mcoID, groupName, null);
	}

	/**
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlushItem(java.lang.String, java.lang.String)
	 */
	public void broadcastCacheFlushItem(String mcoID, String cacheKey)
	{
		publishMessage(FLUSH_LEVEL_ITEM, mcoID, null, cacheKey);
	}

	/**
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastMCOFlush(java.lang.String)
	 */
	public void broadcastMCOFlush(String mcoID)
	{
		publishMessage(FLUSH_LEVEL_MCO, mcoID, null, null);
	}

	/**
	 * publishMessage
	 * @param level
	 * @param mcoID
	 * @param groupName
	 * @param cacheKey
	 * void
	 */
	private void publishMessage(String level, String mcoID, String groupName, String cacheKey)
	{
		/*Connection connection = null;
		MessageProducer publisher = null;
		String topicName = Globals.getString("com.med.config.jmsSource.cache-flush-topic.topic-name");
		
		// Flush and exit if app server is not configured to publish
		if (topicName == null)
		{
			// Process the flush for this JVM instance.  Otherwise, this app server is likely configured as a subscriber to flush messages
			processFlush(mcoID, level, groupName, cacheKey);			
			return;
		}

		try
		{
			connection = JMSSourceFactory.createConnection("cache-flush-topic");
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = JMSSourceFactory.createTopic("cache-flush-topic", session, topicName);
			publisher = session.createProducer(topic);
			publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			MapMessage mapMessage = session.createMapMessage();
			mapMessage.setString("level", level);
			if (mcoID != null) mapMessage.setString("mcoID", mcoID);
			if (groupName != null) mapMessage.setString("groupName", groupName);
			if (cacheKey != null) mapMessage.setString("cacheKey", cacheKey);

			connection.start();
			publisher.send(mapMessage);

			connection.stop();
		}

		catch (JMSException je)
		{
			throw new RuntimeException("Error processing JMS request: " + je.getMessage(), je);
		}
		finally
		{
			// Clean up Connection
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (JMSException e)
				{
					log.error("Exception while closing JMS connection", e);
				}
				connection = null;
			}
		}*/
	}
	
	/**
	 * Process flush.
	 * 
	 * @param mcoID the mco id
	 * @param level the level
	 * @param groupName the group name
	 * @param cacheKey the cache key
	 */
	private void processFlush(String mcoID, String level, String groupName, String cacheKey)
	{
		// Use default MCO ID if MCO ID is not defined.
		if (mcoID == null)
			mcoID = LookupManager.DEFAULT_MCO_ID;

		// React to message content
		if (FLUSH_LEVEL_FULL.equals(level))
			CacheManager.flushAll();
		else if (FLUSH_LEVEL_MCO.equals(level))
			CacheManager.flushMCO(mcoID);
		else if (FLUSH_LEVEL_GROUP.equals(level))
			CacheManager.flushGroup(mcoID, groupName);
		else if (FLUSH_LEVEL_ITEM.equals(level))
			CacheManager.flushItem(mcoID, cacheKey);
	}
	
}