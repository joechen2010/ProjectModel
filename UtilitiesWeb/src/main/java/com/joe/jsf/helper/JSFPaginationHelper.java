package com.joe.jsf.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

public class JSFPaginationHelper {

	private int perPage = 100;
	private int currentRecord;
	private int totalRecords;
	
	
	public int getCurrentRecord() {
		return currentRecord;
	}

	public void setCurrentRecord(int currentRecord) {
		this.currentRecord = currentRecord;
	}

	public int getPerPage() {
		return perPage;
	}

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public int getCurrentPage()
    {
        return getPageNumber(currentRecord + 1, perPage);
    }

    public int getTotalPages()
    {
        return getPageNumber(totalRecords, perPage);
    }

    private int getPageNumber(int size, int count)
    {
        int numPages = 0;
        if (size >= 0 || count > 0)
        {
            BigDecimal bigD = new BigDecimal(size);
            bigD = bigD.divide(new BigDecimal(count), BigDecimal.ROUND_UP);
            numPages = bigD.intValue();
        }
        return numPages;
    }
	
	public void gotoPreviousPage(){
		int newIndex = currentRecord - perPage;
        if (newIndex >= 0)
        {
            currentRecord = newIndex;
        }
	}
	
	public void gotoNextPage(){
		int newIndex = currentRecord + perPage;
        if (newIndex < totalRecords)
        {
            currentRecord = newIndex;
        }
	}

	public void gotoFirstPage(){
        currentRecord = 0;
	}
	
	public void gotoLastPage(){
        currentRecord = (getTotalPages() - 1) * perPage;
	}
	
	public void pageValueChange(ValueChangeEvent event) {
		currentRecord = Integer.parseInt(event.getNewValue().toString());
	}
	
	public List<SelectItem> getPageSelectionItems(){
		List<SelectItem> pageSelectionItems = new ArrayList<SelectItem>();
		for(int i=0; i < getTotalPages(); i++){
			SelectItem item = null;
			item = new SelectItem(i*perPage, (i+1) + " of " + getTotalPages());
			pageSelectionItems.add(item);
		}
		return pageSelectionItems;
		
	}
	
	
	
	
}
