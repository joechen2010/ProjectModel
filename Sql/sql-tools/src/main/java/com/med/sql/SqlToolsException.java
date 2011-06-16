package com.med.sql;

import org.apache.commons.lang.exception.NestableException;

public class SqlToolsException extends NestableException {

	/**
	 * Required for serialization support.
	 * 
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>XmlPropertiesException</code> without specified
	 * detail message.
	 */
	public SqlToolsException() {
		super();
	}

	/**
	 * Constructs a new <code>SqlToolsException</code> with specified detail
	 * message.
	 * 
	 * @param msg
	 *            The error message.
	 */
	public SqlToolsException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a new <code>SqlToolsException</code> with specified nested
	 * <code>Throwable</code>.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public SqlToolsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new <code>SqlToolsException</code> with specified detail
	 * message and nested <code>Throwable</code>.
	 * 
	 * @param msg
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public SqlToolsException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
