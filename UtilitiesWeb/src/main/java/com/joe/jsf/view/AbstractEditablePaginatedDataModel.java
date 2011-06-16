package com.joe.jsf.view;

import java.util.List;





/***
 * AbstractPaginatedDataModel which also encapsulates the idea of
 * being able to add and edit records as well.
 * 
 * @author minger
 *
 * @param <KeyType>
 * @param <RowType>
 */
public abstract class AbstractEditablePaginatedDataModel<KeyType, RowType>
    extends AbstractPaginatedDataModel<RowType> {

    private boolean editMode = false;
    private KeyType editKey = null;
    
    public AbstractEditablePaginatedDataModel() {
        super();
    }

    /***
     * Load a row with a particular key.
     * 
     * @param key
     * @return
     */
    protected abstract RowType loadRow(KeyType key);
    
    /***
     * A row was loaded, so you should populate some transfer objects
     * in your data model with the row's data.  This is here so you
     * can make the choice whether you want to use the domain object
     * or transfer object(s).
     * 
     * @param row
     */
    protected abstract void editRowLoaded(RowType row);
    
    /***
     * An invalid row id was asked for.
     * 
     * @param key
     */
    protected abstract void invalidRowId(KeyType key);
    
    /***
     * Populate a new object based on transfer data contained
     * in this data model.  This transfer data should be seperate
     * from the edit transfer data.
     * @return
     */
    protected abstract RowType populateNew(String userId);

    /***
     * Populate the edit object based on the transfer data contained
     * in this data model.  The original object will be loaded from
     * the datastore first, and then passed into this routine for
     * population from the transfer data.
     * @return
     */
    protected abstract void populateEdit(RowType original, String userId);
    
    
    /***
     * Save a new instance to the data store.
     * 
     * @param rowType
     */
   // protected abstract void saveNew(RowType rowType) throws EvaluationException;
    
    /***
     * Save an existing instance to the data store.
     * 
     * @param rowType
     */
   // protected abstract void saveUpdate(RowType rowType) throws EvaluationException;

    /***
     * Validate a new instance.
     * 
     * @param rowType
     */
   // protected abstract ReturnStatus validateNew(RowType rowType);
    
    /***
     * Validate an existing instance.
     * 
     * @param rowType
     */
   // protected abstract ReturnStatus validateExisting(RowType rowType);


    /***
     * Load the specified range of rows.
     * 
     * @param startRow The starting row (inclusive)
     * @param endRow The ending row (inclusive)
     * @return
     */
    protected abstract List<RowType> doLoadRows(int startRow, int endRow);

    @Override
    protected List<RowType> loadRows(int startRow, int endRow) {
        if (editMode) {
            stopEditMode();
        }
        return doLoadRows(startRow, endRow);
    }

    public void startEditMode(KeyType key) {
        RowType loadedRow = loadRow(key);
        if (loadedRow != null) {
            editMode = true;
            editKey = key;
            editRowLoaded(loadedRow);
        }
        else {
            invalidRowId(key);
        }
    }
    
    public void stopEditMode() {
        editMode = false;
        editKey = null;
    }

    public boolean isEditMode() {
        return editMode;
    }
    
    public KeyType getEditKey() {
        return editKey;
    }

   /* public RowType save(String userId) throws EvaluationException {
        RowType instance = populateNew(userId);
        ReturnStatus status = validateNew(instance);
        if (status.hasErrors()) {
            throw new EvaluationException(status);
        }
        else {
            saveNew(instance);
        }
        return instance;
    }
    
    public RowType update(String userId) throws EvaluationException {
        if (editMode) {
            RowType instance = loadRow(editKey);
            if (instance != null) {
            	populateEdit(instance, userId);
                ReturnStatus status = validateExisting(instance);
                if (status.hasErrors()) {
                    throw new EvaluationException(status);
                }
                else {
                    saveUpdate(instance);
                }
            }
            else {
                // throw some kind of faces message or something since we're dealing with
                // something that we can't work with
            }
            return instance;
        }
        else {
            throw new IllegalStateException("Cannot update when not in edit mode.");
        }        
    }*/
}
