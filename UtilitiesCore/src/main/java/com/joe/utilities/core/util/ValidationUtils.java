package com.joe.utilities.core.util;

import java.util.HashSet;
import java.util.Set;

import com.joe.utilities.core.serviceLocator.ServiceLocator;


/**
* Utility methods common to validation logic
* @author Dave Ousey
*
* Creation date: 1/16/2007 2 PM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public class ValidationUtils
{
    private static final Set<Character> invalidCharacters = new HashSet<Character> (8);

    static
    {
        invalidCharacters.add('~');
        invalidCharacters.add('`');
        invalidCharacters.add('^');
        invalidCharacters.add('{');
        invalidCharacters.add('}');
        invalidCharacters.add('[');
        invalidCharacters.add(']');
        invalidCharacters.add('|');
    }

     /**
     * Method containsInvalidCharacters. Tests input string to determine if there are any invalid characters
     * @param value
     * @return boolean
     */
    public static boolean containsInvalidCharacters(String value)
    {
        if (value != null)
        {
            for (int i=0; i<value.length(); i++)
            {
                if (invalidCharacters.contains(value.charAt(i)))
                    return true;
            }
        }

        return false;
    }
 
    
   
}
