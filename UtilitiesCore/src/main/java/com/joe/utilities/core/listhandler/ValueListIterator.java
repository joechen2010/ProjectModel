/*
 * MEDecision, Inc. Software Development Infrastructure, Version 1.0
 *
 * Copyright (c) 2007 MEDecision, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of 
 * MEDecision, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with MEDecision, Inc.
 *
 * MEDecision, Inc MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. MEDecision, Inc SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
  * Created on Apr 5, 2007
 *
 */
package com.joe.utilities.core.listhandler;

import java.util.List;

/**
 * @author rrichard
 *
 */
public interface ValueListIterator<Output> {

    /**
     * Returns the size of the master List.
     * @return
     */
    public int getSize();

    /**
     * Returns the maximum number of recorders fetched per page.
     * @return
     */
    public int getPageSize();
    
    /**
     * Calculates and returns the total number of pages based on the 
     * size of the master list, and the maximum recorders fetched per page.
     * @return
     */
    public int getTotalPages();
    
    /**
     * Returns the current page number.
     * @return Returns the current page number.
     */
    public int getPageNum();
    
    
    /**
     * Returns the Index number [of the first Record] for the current Page.
     * @return Returns the Index number [of the first Record] for the current Page.
     */
    public int getIndex();
    
    /**
     * Returns the element indicated by index.  
     * @param index
     * @return Returns null if the index is invalid.
     */
    public Output getElement(int index);
    
    /**
     * Returns the specified page number from the Value list.
     * If the page number is less than 1, returns the First page.
     * If the page number is more the the max number of pages [{@link #getTotalPages()}],
     * returns the Last page.
     * @param pageNum
     * @return
     */
    public List<Output> getPage(int pageNum);
    
    /**
     * Returns the Next page of Items from the Value list.
     * Returns the Last page if no more pages exist.
     * @return
     */
    public List<Output> getNextPage();
    
    /**
     * Returns the Previous page of Items from the Value list.
     * Returns the First Page no more previous pages exist.
     * @return
     */
    public List<Output> getPrevPage();
    
    /**
     * Resets the paging stats back to page 1.
     */
    public void restart();
}
