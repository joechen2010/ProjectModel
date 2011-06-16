package com.joe.utilities.core.util;


/**
 * The Class MemoryNode. This class is used by MemoryCounter to track memory
 * usage. Each MemoryNode instance refers to specific amount of memory (in KB)
 * used by the given object. The object's class name and "toString" result is
 * recorded with the memory usage.
 */
public final class MemoryNode
{

	/** The class name. */
	private String className;

	/** The memory. */
	private long memory;

	/** The object as string. */
	private String objectAsString;
	
	private String fieldName;

	/**
	 * Instantiates a new memory node.
	 * 
	 * @param className
	 * the class name
	 * @param memory
	 * the memory
	 * @param objectAsString
	 * the object as string
	 */
	public MemoryNode(String className, long memory, String objectAsString, String fieldName)
	{
		super();
		this.className = className;
		this.memory = memory;
		this.objectAsString = objectAsString;
		this.fieldName = fieldName;
	}

	/**
	 * Gets the class name.
	 * 
	 * @return the class name
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * @return
	 */
	public String getFieldName() 
	{
		return fieldName;
	}

	/**
	 * Gets the memory.
	 * 
	 * @return the memory
	 */
	public long getMemory()
	{
		return memory;
	}

	/**
	 * Gets the object as string.
	 * 
	 * @return the object as string
	 */
	public String getObjectAsString()
	{
		return objectAsString;
	}
	
	@Override
	public String toString()
	{
		if (fieldName != null)
			return "Field: '" + fieldName + "', Class: '" + className + "' [" + memory + " KB]: " + objectAsString;
		else
			return "Class: '" +className + "' [" + memory + " KB]: " + objectAsString;
	}
}
