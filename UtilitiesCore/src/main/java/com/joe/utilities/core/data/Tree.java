package com.joe.utilities.core.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Tree of Objects of generic type T. The Tree is represented as a
 * single rootElement which points to a List<Node<T>> of children. There is no
 * restriction on the number of children that a particular node may have. This
 * Tree provides a method to serialize the Tree into a List by doing a pre-order
 * traversal. It has several methods to allow easy updating of Nodes in the
 * Tree.
 */
public class Tree<T>
{

	/** The root element. */
	private Node<T> rootElement;


	/**
	 * Instantiates a new tree.
	 * 
	 * @param rootElement the root element
	 */
	public Tree(T data)
	{
		this(new Node<T>(data));
	}
	
	/**
	 * Instantiates a new tree.
	 * 
	 * @param rootElement the root element
	 */
	public Tree(Node<T> rootElement)
	{
		super();
		this.rootElement = rootElement;
	}
	
	/**
	 * Instantiates a new tree.
	 */
	public Tree()
	{
		this((Node<T>)null);
	}

	/**
	 * Return the root Node of the tree.
	 * 
	 * @return the root element.
	 */
	public Node<T> getRootElement()
	{
		return this.rootElement;
	}

	/**
	 * Set the root Element for the tree.
	 * 
	 * @param rootElement the root element to set.
	 */
	public Node<T> setRootElement(T data)
	{
		return setRootElement(new Node<T>(data));
	}
	
	/**
	 * Set the root Element for the tree.
	 * 
	 * @param rootElement the root element to set.
	 */
	public  Node<T> setRootElement(Node<T> rootElement)
	{
		// Clear references if changing an existing root element
		if (this.rootElement != null && this.rootElement != rootElement)
			this.rootElement.clear();

		// Assign new root element
		this.rootElement = rootElement;
		return this.rootElement;
	}

	/**
	 * Clear.
	 */
	public void clear()
	{
		if (rootElement != null)
			rootElement.clear();
	}
	
	/**
	 * Size.
	 * 
	 * @return the int
	 */
	public int size()
	{
		if (rootElement != null)
			return 1 + rootElement.subTreeSize();
		else
			return 0;
	}
	
	/**
	 * Returns the Tree<T> as a List of Node<T> objects. The elements of the
	 * List are generated from a pre-order traversal of the tree.
	 * 
	 * @return a List<Node<T>>.
	 */
	public List<Node<T>> toList()
	{
		List<Node<T>> list = new ArrayList<Node<T>>(16);
		traverse(rootElement, list);
		return list;
	}

	/**
	 * Returns a String representation of the Tree. The elements are generated
	 * from a pre-order traversal of the Tree.
	 * 
	 * @return the String representation of the Tree.
	 */
	public String toString()
	{
		if (rootElement != null)
		{
			if (rootElement.getData() != null)
				return "Tree of class " + rootElement.getData().getClass().getName() + ":\n"+ rootElement.toString();
			else
				return "Tree:\n"+ rootElement.toString();
		}
		else
		{
			return "Tree has no root element";
		}
	}

	/**
	 * Walks the Tree in pre-order style. This is a recursive method, and is
	 * called from the toList() method with the root element as the first
	 * argument. It appends to the second argument, which is passed by reference *
	 * as it recurses down the tree.
	 * 
	 * @param element the starting element.
	 * @param list the output of the walk.
	 */
	private void traverse(Node<T> element, List<Node<T>> list)
	{
		if (element == null)
			throw new IllegalStateException("Tree node (root or child) is not yet defined.  Cannot traverse tree.");
		
		list.add(element);
		for (Node<T> data : element.getChildren())
		{
			traverse(data, list);
		}
	}

}
