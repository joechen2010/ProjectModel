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


/**
 * @author rrichard
 *
 */
public abstract class ValueListHandlerABS<Input,Output> implements ValueListIterator<Output> {

    private SearchResult<Input> list = null;
    private int currPage = 0;
    private int pageSize = 5;
    
    /**
     * 
     */
    public ValueListHandlerABS(SearchResult<Input> list, int maxFetch) {
        this.list = list;
        if (maxFetch > 5) {
            this.pageSize = maxFetch;
        }
    }
    
    /**
     * Provides the opportunity for the implementer to either override the
     * value from the List, or to simple return the value from the list.
     * @param element
     * @return
     */
    protected abstract Output returnElement(Input element);
    
    /**
     * Provides the opportunity for the implementer to either override the
     * value from the List, or to simple return the value from the list.
     * @param element
     * @return
     */
    protected SearchResult<Output> returnElements(int startIndex, int maxRecords) {
        SearchResult<Output> returnValue = new SearchResult<Output>(maxRecords);
        
        if (list != null) {
            int endIndex = startIndex + maxRecords;
            for (;startIndex < list.size() && startIndex < endIndex; startIndex++) {
                returnValue.add(getElement(startIndex));
            }
        }
        
        return returnValue;
    }
    
    
    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#getElement(int)
     */
    public Output getElement(int index) {
        if (index < 0 || list == null || index >= list.size()) {
            return null;
        }
        return returnElement(list.get(index));
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#getNextPage()
     */
    public SearchResult<Output> getNextPage() {
        int pageNum = currPage + 1;
        if (pageNum > getTotalPages()) {
            pageNum = getTotalPages();
        }
        return getPage(pageNum);
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#getPrevPage()
     */
    public SearchResult<Output> getPrevPage() {
        int pageNum = currPage - 1;
        if (pageNum < 1) {
            pageNum = 1;
        }
        return getPage(pageNum);
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#getSize()
     */
    public int getSize() {
        int size = 0;
        if (list != null) {
            size = list.size();
        }
        return size;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#getPageSize()
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageNum
     * @return
     */
    private int getAdjustedPageNum(int pageNum) {
        int newPageNum = 0;
        if (pageNum < 1) {
            newPageNum = 1;
        } else if (pageNum > getTotalPages()) {
            newPageNum = getTotalPages();
        } else {
            newPageNum = pageNum;
        }
        return newPageNum;
    }
    
    public int getIndex() {
        int startIndex = (getPageNum() > 1) ? (getPageNum() - 1) * getPageSize(): 0;
        return startIndex;
    }
    
    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#restart()
     */
    public void restart() {
        currPage = 0;
    }
    
    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#getPage(int)
     */
    public SearchResult<Output> getPage(int pageNum) {
        SearchResult<Output> returnValue = new SearchResult<Output>(pageSize);
        
        if (list != null) {
            this.currPage = getAdjustedPageNum(pageNum);
            
            int startIndex = getIndex();
            
            returnValue = returnElements(startIndex, pageSize);
        }
        
        return returnValue;
    }
    
    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#getPageNum()
     */
    public int getPageNum() {
        return currPage;
    }
    
    /* (non-Javadoc)
     * @see com.med.utilities.core.listhandler.ValueListIterator#getTotalPages()
     */
    public int getTotalPages() {
        int pages = 0;
        if (list != null) {
            pages = (list.size() / pageSize);
            if ((list.size() % pageSize) > 0) {
                pages++;
            }
        }
        return pages;
    }

}
