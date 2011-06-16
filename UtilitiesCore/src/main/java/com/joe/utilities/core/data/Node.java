package com.joe.utilities.core.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a node of the Tree<T> class. The Node<T> is also a container,
 * and can be thought of as instrumentation to determine the location of the
 * type T in the Tree<T>.
 */
public class Node<T>
{
	/** The parent node. */
	private Node<T> parentNode = null;

	/** The data. */
	public T data;

	/** The children.  */
	public List<Node<T>> children = new ArrayList<Node<T>>(4);

	/**
	 * Instantiates a new node.
	 * 
	 * @param data the data
	 */
	public Node(T data)
	{
		this(null, data);
	}
	

	/**
	 * Instantiates a new node.
	 * 
	 * @param parentNode the parent node
	 * @param data the data
	 */
	public Node(Node<T> parentNode, T data)
	{
		super();
		this.parentNode = parentNode;
		this.data = data;
	}

	/**
	 * Return the children of Node<T>. The Tree<T> is represented by a single
	 * root Node<T> whose children are represented by a List<Node<T>>. Each
	 * of these Node<T> elements in the List can have children. The
	 * getChildren() method will return the children of a Node<T>.
	 * 
	 * @return the children of Node<T>
	 */
	public List<Node<T>> getChildren()
	{
		return new ArrayList<Node<T>>(this.children);
	}

	/**
	 * Returns the number of immediate children of this Node<T>.
	 * 
	 * @return the number of immediate children.
	 */
	public int size()
	{
		return children.size();
	}
	
	/**
	 * Sub tree size.
	 * 
	 * @return the int
	 */
	public int subTreeSize()
	{
		int count = children.size();
		for (Node<T> child : children)
		{
			count += child.subTreeSize();
		}
		return count;
	}

	/**
	 * Adds a child to the list of children for this Node<T>. The addition of
	 * the first child will create a new List<Node<T>>.
	 * 
	 * @param child a Node<T> object to set.
	 */
	public Node<T> addChildNode(Node<T> child)
	{
		if (child == null)
			throw new IllegalArgumentException("Cannot add null child node.");
		
		// Already added
		if (child.parentNode == this)
			return child;
		
		// Detach from existing parent.  Establish this node as new parent
		setParentNode(child);
		
		// Add as child
		children.add(child);
		
		return child;
	}
	
	
	/**
	 * Adds the child.  Convenience method to construct the Node with the given data
	 * 
	 * @param data the data
	 */
	public Node<T> addChild(T data)
	{
		return addChildNode(new Node<T>(data));
	}
	
	/**
	 * Sets the parent node.
	 * 
	 * @param child the new parent node
	 */
	private void setParentNode(Node<T> child)
	{
		// Already parent.
		if (child.parentNode == this)
			return;
		
		// Detach from existing parent.  Establish this node as new parent
		if (child.parentNode != null)
			child.parentNode.removeChildNode(child);
		child.parentNode = this;		
	}
	
	/**
	 * Clear.  
	 */
	public void clear()
	{
		// Recursively clear the subnodes
		for (Node<T> e : this.children)
		{
			if (e != null)
				e.clear();
		}
		
		// Release references to child nodes
		this.children.clear();
	}
	
	/**
	 * Remove the Node<T> element at index index of the List<Node<T>>.
	 * 
	 * @param index the index of the element to delete.
	 * 
	 * @throws IndexOutOfBoundsException if thrown.
	 */
	public Node<T> removeChildNode(Node<T> child)
	{
		// Detach
		if (child != null)
		{
			child.parentNode = null;
			children.remove(child);
		}
	
		return child;
	}
	
	/**
	 * Gets the node depth.
	 * 
	 * @return the node depth
	 */
	public int getNodeDepth()
	{
		if (parentNode != null)
			return 1+parentNode.getNodeDepth();
		else
			return 0;
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public T getData()
	{
		return this.data;
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public void setData(T data)
	{
		this.data = data;
	}
	
	/**
	 * To string.
	 * 
	 * @return the string
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder(64);
		
		int depth = getNodeDepth();
		if (depth > 0)
		{
			sb.append(StringUtils.repeat("  ", depth));
			sb.append("|->");
		}
		
		sb.append(data);
		
		for (Node<T> node : this.children)
		{
			sb.append((char)10);
			sb.append(node.toString());
		}
		
		return sb.toString();
	}

	
}
