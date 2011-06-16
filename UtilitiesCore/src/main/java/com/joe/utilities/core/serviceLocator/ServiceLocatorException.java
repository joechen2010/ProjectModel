/*
 * Created on Nov 9, 2006
 */
package com.joe.utilities.core.serviceLocator;

/**
 * @author lshalevi
 */
public class ServiceLocatorException extends RuntimeException
{
    public ServiceLocatorException ()
    {
        super();
    }

    public ServiceLocatorException (String message)
    {
        super(message);
    }

    public ServiceLocatorException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceLocatorException (Throwable cause)
    {
        super(cause);
    }

}
