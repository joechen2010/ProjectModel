package com.joe.jsf.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;

/***
 * An abstract class to represent a paginated DataModel
 * instance.
 * 
 * @author minger
 *
 * @param <RowType>
 */
public abstract class AbstractPaginatedDataModel<RowType>
    extends DataModel {

    private int rowIndex = 0;
    private int loadedRowStart = -1;
    private int loadedRowEnd = -1;
    private List<RowType> loadedRows;
    private Integer rowCount;
    private Map<Long, Boolean> selectionModel =
        new HashMap<Long, Boolean>();


    /***
     * Constructor
     */
    public AbstractPaginatedDataModel() {
        super();
    }
    
    /***
     * Get the page size for this data model
     * @return
     */
    protected abstract int getPageSize();
    
    /***
     * Load the specified range of rows.
     * 
     * @param startRow The starting row (inclusive)
     * @param endRow The ending row (inclusive)
     * @return
     */
    protected abstract List<RowType> loadRows(int startRow, int endRow);
    
    /***
     * Load the count of rows in the total row set.
     * 
     * @return
     */
    protected abstract int loadRowCount();
    

    private void loadRows(int rowIndex) {
        int pageSize = getPageSize();
        
        int startRow = ((rowIndex / pageSize) * pageSize);
        int endRow = Math.min(rowCount, startRow + pageSize) - 1;
        
        this.loadedRows = loadRows(startRow, endRow);
                
        loadedRowStart = startRow;

        loadedRowEnd = endRow;

        selectionModel.clear();
    }
    
    @Override
    public final int getRowCount() {
        if (rowCount == null) {
            rowCount = loadRowCount();
        }
        return rowCount.intValue();
    }

    @Override
    public Object getRowData() {
        getRowCount();
        if (! (rowIndex >= loadedRowStart && rowIndex <= loadedRowEnd)) {
            if (rowIndex < rowCount) {
                loadRows(rowIndex);
            }
            else {
                // deal with a bad row index;
            }
        }
        if (rowIndex - loadedRowStart >= loadedRows.size()) {
            return null;
        }
        return loadedRows.get(rowIndex - loadedRowStart);
    }

    @Override
    public final int getRowIndex() {
        return this.rowIndex;
    }


    @Override
    public final boolean isRowAvailable() {
        return rowIndex < rowCount;
    }

    @Override
    public final void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public void setWrappedData(Object data) {
    }

    @Override
    public Object getWrappedData() {
        return null;
    }
    
    /***
     * Get the rows which have currently been loaded.
     * 
     * @return
     */
    public final List<RowType> getLoadedRows() {
        return Collections.unmodifiableList(loadedRows);
    }

    /***
     * Refresh this data model by resetting the state, and loading
     * the rowset for the very first row, and clearing the selection
     * model.
     * 
     */
    public final void refresh() {
        this.refresh(true);
    }
    
    /***
     * Refresh this data model by resetting the state, and loading
     * the rowset for the very first row, and clearing the selection
     * model.
     * 
     */
    public final void refresh(boolean loadRows) {
        this.rowCount = null;
        this.loadedRows = null;
        this.loadedRowStart = 0;
        this.loadedRowEnd = 0;
        this.selectionModel.clear();
        getRowCount();
        if (this.rowCount > 0) {
            this.rowIndex = 0;
            if (loadRows) {
                loadRows(this.rowIndex);
            }
        }
        else {
            this.rowIndex = -1;
        }
    }

    /***
     * Gets the selection model.
     * 
     * @return
     */
    public final Map<Long, Boolean> getSelectionModel() {
        return selectionModel;
    }

    public final int getLoadedRowStart() {
        return loadedRowStart;
    }

    public final int getLoadedRowEnd() {
        return loadedRowEnd;
    }

    public final boolean isRowLoaded(int row) {
        return row >= this.loadedRowStart && row <= this.loadedRowEnd;
    }
     
    public final int getFirstDisplayRow() {
        if (loadedRowStart < 0) {
            return 0;
        }
        else {
            return loadedRowStart;
        }
    }
}
