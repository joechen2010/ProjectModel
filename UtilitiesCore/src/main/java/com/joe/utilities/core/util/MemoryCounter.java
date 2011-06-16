package com.joe.utilities.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.joe.utilities.core.data.Node;
import com.joe.utilities.core.data.Tree;

/**
 * The Class MemoryCounter.
 * 
 * @link http://www.javaspecialists.co.za/archive/Issue078.html
 * Enhanced/modified by Dave Ousey
 * This class can estimate how much memory an Object uses. It is fairly accurate
 * for JDK 1.4.2. It is based on the newsletter #29.
 */
public final class MemoryCounter
{
	/** The Constant sizes. */
	private static final MemorySizes sizes = new MemorySizes();

	/** The visited. */
	private final Map visited = new IdentityHashMap();

	/** The report stack level. */
	private int reportStackLevel = 10;
	
	/** The stack level. */
	private int stackLevel = 0;
	
	/** The report memory minimum. */
	private long reportMemoryMinimum = 1024;
	
	/** The skip known static classes. */
	private boolean skipKnownStaticClasses = false;
	
	/** The generate memory tree. */
	private boolean generateMemoryTree = false;
		
	/** The memory tree. */
	private Tree<MemoryNode> memoryTree;
	
	/** The known static classes to skip. */
	private static Set<String> knownStaticClassesToSkip;
	
	/** The known static classes to always skip. */
	private static Set<String> knownStaticClassesToAlwaysSkip;
	static
	{
		// Setup known, major static objects: Spring Framework
		knownStaticClassesToSkip = new HashSet<String>(8);
		
		// Spring/Hibernate
		knownStaticClassesToSkip.add("org.springframework.beans.factory.support.DefaultListableBeanFactory");
		knownStaticClassesToSkip.add("org.hibernate.impl.SessionFactoryImpl");
		knownStaticClassesToSkip.add("org.springframework.orm.hibernate3.HibernateTemplate");
		knownStaticClassesToSkip.add("org.springframework.orm.hibernate3.HibernateTransactionManager");
		knownStaticClassesToSkip.add("org.springframework.aop.framework.JdkDynamicAopProxy");
		
		// Setup known, major static objects: Spring Framework
		knownStaticClassesToAlwaysSkip = new HashSet<String>(7);
		
		//Application level medecision view handler.
		knownStaticClassesToAlwaysSkip.add("com.med.jsf.viewHandlers.classViewHandler.ClassViewHandler");
		
		// Used by Log4J
		knownStaticClassesToAlwaysSkip.add("org.apache.log4j.spi.RootLogger");
		
		// JBoss server core objects
		knownStaticClassesToAlwaysSkip.add("org.jboss.mx");
		knownStaticClassesToAlwaysSkip.add("org.jboss.mq");
		knownStaticClassesToAlwaysSkip.add("org.jboss.web.tomcat.service.WebAppClassLoader");
		
		// App server
		knownStaticClassesToAlwaysSkip.add("org.apache.catalina.core");
		
		// Any thread groups
		knownStaticClassesToAlwaysSkip.add("java.lang.ThreadGroup");
	}

		
	/**
	 * Instantiates a new memory counter.
	 * 
	 * @param skipKnownStaticClasses the skip known static classes
	 * @param reportStackLevel the report stack level
	 * @param reportMemoryMinimum the report memory minimum
	 * @param generateMemoryTree the generate memory tree
	 */
	public MemoryCounter(boolean skipKnownStaticClasses, int reportStackLevel, long reportMemoryMinimum, boolean generateMemoryTree)
	{
		super();
		this.skipKnownStaticClasses = skipKnownStaticClasses;
		this.reportStackLevel = reportStackLevel;
		this.reportMemoryMinimum = reportMemoryMinimum;
		
		this.generateMemoryTree = generateMemoryTree;
		if (generateMemoryTree)
			memoryTree = new Tree<MemoryNode>();
	}

	/**
	 * Estimate.
	 * 
	 * @param obj the obj
	 * 
	 * @return the long
	 */
	public synchronized long estimate(Object obj)
	{
		if (generateMemoryTree)
			memoryTree.clear();
		
		long result = _estimate(obj, null, null);
		visited.clear();
				
		return result;
	}

	/**
	 * Skip object.
	 * 
	 * @param obj the obj
	 * 
	 * @return true, if successful
	 */
	private boolean skipObject(Object obj)
	{
		if (obj instanceof String)
		{
			// this will not cause a memory leak since
			// unused interned Strings will be thrown away
			if (obj == ((String) obj).intern())
				return true;
		}
		
		if (obj == null)
			return true;
		if (visited.containsKey(obj))
			return true;
		
		// Skip static classes.  Some are always skipped, some are skipped if requested
		String className = obj.getClass().getName();
		for (String filteredClass : knownStaticClassesToAlwaysSkip)
		{
			if (className.startsWith(filteredClass))
				return true;
		}
		if (skipKnownStaticClasses && knownStaticClassesToSkip.contains(className))
			return true;
		
		return false;
	}

	/**
	 * _estimate.
	 * 
	 * @param obj the obj
	 * @param loggingPrefix the logging prefix
	 * 
	 * @return the long
	 */
	private long _estimate(Object obj, Node<MemoryNode> parentNode, String fieldName)
	{
		// Skip known static objects or objects already encountered recursively
		if (skipObject(obj))
			return 0;
		
		// Mark object as processed to avoid repeated estimated if discovered later in object graph
		visited.put(obj, null);
		
		long result = 0;
		Class clazz = obj.getClass();
		if (clazz.isArray())
			return _estimateArray(obj, parentNode, fieldName);
		
		// Create empty node, will populate once we have estimated memory
		Node<MemoryNode> objectMemoryNode = null;
		if (generateMemoryTree)
			objectMemoryNode = new Node<MemoryNode>(null);
		
		// Note that we're down a stack level
		stackLevel++;
		
		while (clazz != null)
		{
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				if (!Modifier.isStatic(fields[i].getModifiers()))
				{
					if (fields[i].getType().isPrimitive())
					{
						result += sizes.getPrimitiveFieldSize(fields[i].getType());
					}
					else
					{
						result += sizes.getPointerSize();
						fields[i].setAccessible(true);
						try
						{
							Object child = fields[i].get(obj);
							if (child != null)
							{
								result += _estimate(child, objectMemoryNode, fields[i].getName());
								
							}
						}
						catch (IllegalAccessException ex)
						{
							assert false;
						}
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		
		result += sizes.getClassSize();
		long memory = roundUpToNearestEightBytes(result);
		if (generateMemoryTree && stackLevel <= reportStackLevel)
		{
			// Call toString.  Hibernate objects may explode here.
			String objectString = "";
			try
			{
				objectString = obj.toString();
			}
			catch (Throwable t)
			{
				objectString = t.getMessage();
			}
			
			// Always record root node.  Record child nodes only if they exceed minimal interesting memory threshold
			objectMemoryNode.setData(new MemoryNode(obj.getClass().getName(), memory/1024L, objectString, fieldName));
			if (parentNode != null && memory > reportMemoryMinimum)
				parentNode.addChildNode(objectMemoryNode);
			else if (parentNode == null)
				memoryTree.setRootElement(objectMemoryNode);
		}
		stackLevel--;
		return memory;
	}
	
	/**
	 * Round up to nearest eight bytes.
	 * 
	 * @param result the result
	 * 
	 * @return the long
	 */
	private long roundUpToNearestEightBytes(long result)
	{
		if ((result % 8) != 0)
		{
			result += 8 - (result % 8);
		}
		return result;
	}

	/**
	 * _estimate array.
	 * 
	 * @param obj the obj
	 * 
	 * @return the long
	 */
	protected long _estimateArray(Object obj, Node<MemoryNode> parentNode, String fieldName)
	{
		// Create empty node, will populate once we have estimated memory
		Node<MemoryNode> objectMemoryNode = null;
		if (generateMemoryTree)
			objectMemoryNode = new Node<MemoryNode>(null);
		
		long memory = 16;
		int length = Array.getLength(obj);
		if (length != 0)
		{
			Class arrayElementClazz = obj.getClass().getComponentType();
			if (arrayElementClazz.isPrimitive())
			{
				long value = length * sizes.getPrimitiveArrayElementSize(arrayElementClazz);
				memory += value;

			}
			else
			{
				for (int i = 0; i < length; i++)
				{
					memory += sizes.getPointerSize() + _estimate(Array.get(obj, i), objectMemoryNode, fieldName);
				}
			}
		}
		
		// Record memory  
		if (generateMemoryTree && stackLevel <= reportStackLevel)
		{
			objectMemoryNode.setData(new MemoryNode(obj.getClass().getComponentType().getName() + "[]", memory/1024L, "Array", fieldName));
			if (parentNode != null && memory > reportMemoryMinimum)
				parentNode.addChildNode(objectMemoryNode);
			else if (parentNode == null)
				memoryTree.setRootElement(objectMemoryNode);
		}
		
		return memory;
	}

	/**
	 * Gets the memory tree.
	 * 
	 * @return the memory tree
	 */
	public Tree<MemoryNode> getMemoryTree()
	{
		return memoryTree;
	}
}
