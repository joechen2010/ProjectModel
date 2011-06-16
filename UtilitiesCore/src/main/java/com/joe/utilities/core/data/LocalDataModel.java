package com.joe.utilities.core.data;

import java.util.List;

import com.joe.utilities.core.facade.IFacade;






/**
 * A special type of JSF DataModel to allow a datatable and datapaginator
 * to page through a large set of data without having to hold the entire
 * set of data in memory at once.
 * Any time a managed bean wants to avoid holding an entire dataset,
 * the managed bean declares this inner class which extends PagedListDataModel
 * and implements the fetchData method. fetchData is called
 * as needed when the table requires data that isn't available in the
 * current data page held by this object.
 * This requires the managed bean (and in general the business
 * method that the managed bean uses) to provide the data wrapped in
 * a DataPage object that provides info on the full size of the dataset.
 */
@SuppressWarnings("unchecked")
public class LocalDataModel extends PagedListDataModel{

	
	private IFacade facade;
	private String sortColumn;
	private boolean ascSorting;
	
	public LocalDataModel(IFacade facade, int pageSize, String sortColumn, boolean ascSorting) {
        super(pageSize);
        this.facade = facade;
        this.sortColumn = sortColumn;
        this.ascSorting = ascSorting;
    }

    public DataPage fetchPage(int startRow, int pageSize) {
        // call enclosing managed bean method to fetch the data
        return getDataPage(startRow, pageSize);
    }
	
    /**
     * This is where the Customer data is retrieved from the database and
     * returned as a list of CustomerBean objects for display in the UI.
     */
    private DataPage getDataPage(int startRow, int pageSize) {
        // Retrieve the total number of customers from the database.  This
        // number is required by the DataPage object so the paginator will know
        // the relative location of the page data.
        int totalNumberCustomers = facade.getDataCount();

        // Calculate indices to be displayed in the ui.
        int endIndex = startRow + pageSize;
        if (endIndex > totalNumberCustomers) {
            endIndex = totalNumberCustomers;
        }

        // Query database for sorted results.
        List pageCustomers = facade.findPage(sortColumn, ascSorting, startRow, endIndex - startRow);

        // Reset the dirtyData flag.
        setDirtyData(false);

        return new DataPage(totalNumberCustomers, startRow, pageCustomers);
    }

    
	public IFacade getFacade() {
		return facade;
	}

	public void setFacade(IFacade facade) {
		this.facade = facade;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	public boolean isAscSorting() {
		return ascSorting;
	}

	public void setAscSorting(boolean ascSorting) {
		this.ascSorting = ascSorting;
	}
    
}
