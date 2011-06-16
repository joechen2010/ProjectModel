package com.joe.jsf.web.view;

import java.util.Date;

/**
 * This class include selected user/department and date range so that the UI logic can recreate that original search.
 * 
 * @author GRT Creation date: May 9, 2008 12:40:00 PM Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */

public class HomePageSearchCriteria {

    private String selectedViewListCode;
    private String dueDateType;
    private Date startDate;
    private Date throughDate;
    private boolean isFromActivity;
    private String ownerFilter;
    private String memberNameFilter;
    private String typeFilter;
    private String priorityFilter;
    private String reasonFilter;
    private String sortColumn;

    /** Default constructor */
    public HomePageSearchCriteria() {}

    /**
     * @return returns the selectedViewListCode.
     */
    public String getSelectedViewListCode() {
        return selectedViewListCode;
    }

    /**
     * @param selectedViewListCode the selectedViewListCode to set
     */
    public void setSelectedViewListCode(String selectedViewListCode) {
        this.selectedViewListCode = selectedViewListCode;
    }

    /**
     * @return returns the selectedUserOrDeptCode.
     */
    public String getSelectedUserOrDeptCode() {
        return (selectedViewListCode != null) ? selectedViewListCode.substring(2) : "";

    }

    /**
     * @return returns the dueDateType.
     */
    public String getDueDateType() {
        return dueDateType;
    }

    /**
     * @param dueDateType the dueDateType to set
     */
    public void setDueDateType(String dueDateType) {
        this.dueDateType = dueDateType;
    }

    /**
     * @return returns the throughDate.
     */
    public Date getThroughDate() {
        return throughDate;
    }

    /**
     * @param throughDate the throughDate to set
     */
    public void setThroughDate(Date throughDate) {
        this.throughDate = throughDate;
    }

    /**
     * @return returns the isDeptFlag.
     */
    public boolean isDeptFlag() {
        return selectedViewListCode.startsWith("D-");
    }

    /**
     * @return returns the isIndividualFlag.
     */
    public boolean isIndividualFlag() {
        return selectedViewListCode.startsWith("I-");
    }

    /**
     * @return returns the isFromActivity.
     */
    public boolean isFromActivity() {
        return isFromActivity;
    }

    /**
     * @param isFromActivity the isFromActivity to set
     */
    public void setFromActivity(boolean isFromActivity) {
        this.isFromActivity = isFromActivity;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the ownerFilter
     */
    public String getOwnerFilter() {
        return ownerFilter;
    }

    /**
     * @param ownerFilter the ownerFilter to set
     */
    public void setOwnerFilter(String ownerFilter) {
        this.ownerFilter = ownerFilter;
    }

    /**
     * @return the memberNameFilter
     */
    public String getMemberNameFilter() {
        return memberNameFilter;
    }

    /**
     * @param memberNameFilter the memberNameFilter to set
     */
    public void setMemberNameFilter(String memberNameFilter) {
        this.memberNameFilter = memberNameFilter;
    }

    /**
     * @return the typeFilter
     */
    public String getTypeFilter() {
        return typeFilter;
    }

    /**
     * @param typeFilter the typeFilter to set
     */
    public void setTypeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
    }

    /**
     * @return the priorityFilter
     */
    public String getPriorityFilter() {
        return priorityFilter;
    }

    /**
     * @param priorityFilter the priorityFilter to set
     */
    public void setPriorityFilter(String priorityFilter) {
        this.priorityFilter = priorityFilter;
    }

    /**
     * @return the sortColumn
     */
    public String getSortColumn() {
        return sortColumn;
    }

    /**
     * @param sortColumn the sortColumn to set
     */
    public void setSortColumn(String sortCol) {
        this.sortColumn = sortCol;
    }

    
    /**
     * @return the reasonFilter
     */
    public String getReasonFilter() {
        return reasonFilter;
    }

    
    /**
     * @param reasonFilter the reasonFilter to set
     */
    public void setReasonFilter(String reasonFilter) {
        this.reasonFilter = reasonFilter;
    }
}
