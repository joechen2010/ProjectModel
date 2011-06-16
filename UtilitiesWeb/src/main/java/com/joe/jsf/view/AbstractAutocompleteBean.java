package com.joe.jsf.view;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.PropertyUtils;

import com.icesoft.faces.component.selectinputtext.SelectInputText;


/***
 * Abstract bean for the icefaces autocomplete control.  This can be embedded
 * inside of a controller, once extended, to provide the necessary capabilities
 * to do autocomplete querying, and state tracking.
 * 
 * @author minger
 *
 * @param <ResultType>
 */
public abstract class AbstractAutocompleteBean<ResultType> {

    private int querySize = 10;
    
    private List<SelectItem> queryMatches;
    
    private String selectedValueString = "";
    
    private ResultType selectedValue;
    private boolean emptySelectedValueValid = false;

	private static final String NO_RESULTS_FOUND = "No Results Found";
    
    private AutocompleteBeanListener<ResultType> listener;

    /***
     * Default constructor.
     */
    protected AbstractAutocompleteBean() {
        super();
    }
 
    protected AbstractAutocompleteBean( boolean emptySelectedValueValid) {
        super();
        this.emptySelectedValueValid=emptySelectedValueValid;
    }  
    
    public void setListener(AutocompleteBeanListener<ResultType> listener) {
        this.listener = listener;
    }

    /***
     * Query for a particular partial string that was entered in the
     * text box.
     * 
     * @param partial The string that was entered in the text box.
     * @param querySize The number of results to query for.
     * @return The list of results which match the query.  The list should
     * be no larger than the requested querySize.
     */
    protected abstract List<ResultType> query(String partial, int querySize);
    
    /***
     * Create a {@link SelectItem} from a given result.
     * 
     * @param result
     * @return
     */
    protected abstract SelectItem createSelectItem(ResultType result);
    
    
    
    public boolean isEmptySelectedValueValid() {
		return emptySelectedValueValid;
	}

	public void setEmptySelectedValueValid(boolean emptySelectedValueValid) {
		this.emptySelectedValueValid = emptySelectedValueValid;
	}
	
    /***
     * The value change listener which handles the logic for when something is
     * typed in the text box.
     * 
     * @param event
     */
    @SuppressWarnings("unchecked")
    public void selectInputValueChanged(ValueChangeEvent event) {

        if (event.getComponent() instanceof SelectInputText) {
        	
        	// clear out the old list of matches
            if(this.queryMatches != null) {
            	queryMatches.clear();
            	queryMatches = null;
            }
            

            // get the number of displayable records from the component
            SelectInputText autoComplete =
                    (SelectInputText) event.getComponent();

            // get the new value typed by component user.
            String newWord = (String) event.getNewValue();            
            String oldWord = (String) event.getOldValue();
            
            // grab the row size to limit the queries
            if (autoComplete.getRows() > 0) {
            	this.querySize = autoComplete.getRows();
            }
            
            // check entered text few new matching values
            if ("".equals(newWord)) {
                // user cleared field, so present an empty selectbox
            	queryMatches = new ArrayList<SelectItem>(0);
            	if (listener != null) {
            	    listener.autocompleteValueBlank();
            	}
            }
            else {
            	// get a new query using the new text entered
                List<ResultType> matches = query(newWord, querySize);
                queryMatches = new ArrayList<SelectItem>(matches.size());
                for (ResultType result : matches) {
                	queryMatches.add(createSelectItem(result)); 
                }
                
                // if empty, then no results found...put a placeholder
                if (queryMatches.isEmpty()) {
                	SelectItem noResult = new SelectItem("", NO_RESULTS_FOUND);
                	queryMatches.add(noResult);
                }
            }
            
            // if there is a selected item and the value has changed to something different
            // then find the object of the same name
            if (autoComplete.getSelectedItem() != null) {
            	if (!autoComplete.getSelectedItem().getValue().equals("")) {
            		selectedValue = getQueryMatch(newWord);            		
                    if (listener != null) {
                        listener.autocompleteValueSelected(selectedValue);
                    }
            	}
            	else {
                    autoComplete.setValue("");
                    this.selectedValue = null;
                    this.selectedValueString = "";
                    queryMatches.clear();
                    queryMatches = null;
                    if (listener != null) {
                        listener.autocompleteValueSelectedNoResultsFound();
                    }
            	}
            }
            // if there was no selection we still want to see if a proper
            // city was typed and update our selectedCity instance.
            else {
                ResultType tmp = getQueryMatch(newWord);
                if (tmp != null){
                    selectedValue = tmp;
                    if (listener != null) {
                        listener.autocompleteValueSelected(selectedValue);
                    }
                } else {
                	selectedValue = null;
                }
            }
           
            // if there is a valid selectedValue, and the 
            if (selectedValue != null && newWord.equalsIgnoreCase(oldWord)) {
            	queryMatches.clear();
            	queryMatches = null;
            }


        }
    }

    /***
     * The value change listener which handles the logic for when something is
     * typed in the text box.  Forces the showing and hiding of the autocomplete
     * box.  This method is for use with older Alineo pages that have stylesheet
     * conflicts causing the autocomplete box to react badly and not hide when
     * it is supposed to.
     * 
     * @param event
     */
    @SuppressWarnings("unchecked")
    public void selectInputValueChangedForceHide(ValueChangeEvent event) {
    	if (event.getComponent() instanceof SelectInputText) {
    		
    		// get the number of displayable records from the component
            SelectInputText autoComplete =
                    (SelectInputText) event.getComponent();
            
	        // complete hack to get autocomplete elements in existing
	        // Alineo jspx pages to act correctly.  Whenever any 
	        // action is taken on the autocomplete component we
	        // want to set the display to block and make it so
	        // icefaces cannot overwrite it.
	        autoComplete.setStyle("display:block!important;");
	    	
	        // call the normal value change event
	        selectInputValueChanged(event);
	        
	    	// set the new list of matches unless a selected value,
	        // in which case we can ignore the dropdown altogether.
	        if (queryMatches == null || queryMatches.size() == 0 || 
	        		((queryMatches.size() == 1) && 
	        		 (!NO_RESULTS_FOUND.equalsIgnoreCase(queryMatches.get(0).getLabel())))) {	        	
	        	// complete hack to get autocomplete elements in existing
	            // Alineo jspx pages to act correctly.  When the user
	        	// selects an item or types an element that matches one
	        	// item, we can hide the autocomplete box.
	        	autoComplete.setStyle("display:none;");
	        	queryMatches = null;
	        }
	        
    	}
    }
    
    /***
     * Looks in the previously queried data for a match.
     * 
     * @param label
     * @return
     */
    @SuppressWarnings("unchecked")
    private ResultType getQueryMatch(String label) {
        if (queryMatches != null) {
            SelectItem selectItem;
            for(int i = 0, max = queryMatches.size(); i < max; i++){
                selectItem = (SelectItem)queryMatches.get(i);
                if (selectItem.getLabel().compareToIgnoreCase(label) == 0 && 
                		!NO_RESULTS_FOUND.equalsIgnoreCase(selectItem.getLabel())) {
                    return (ResultType) selectItem.getValue();
                }
            }
        }
        return null;
    }

    /***
     * See if the selected value string is a valid value.  If it is, you can rely on calling
     * {@link #getSelectedValue()} method after this to retrieve that value.
     * 
     * @param nestedPropertyPath
     * @return
     */
    public boolean isSelectedValueStringValid(String nestedPropertyPath) {
    	
    	if (this.emptySelectedValueValid && selectedValueString.trim().length()==0){
    		selectedValue=null;
    		return true;
    	}   		
    	
        List<ResultType> results = query(selectedValueString, 1);        
        if (results.size() == 1) {
            ResultType r0 = results.get(0);
            try {
                String val = (String) PropertyUtils.getNestedProperty(r0, nestedPropertyPath);
                if (val != null && !val.equalsIgnoreCase(NO_RESULTS_FOUND) && val.equalsIgnoreCase(selectedValueString)) {
                    selectedValue = r0;
                    return true;
                }
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return false;        
    }
    
    public String getSelectedValueString() {
        return selectedValueString;
    }

    public void setSelectedValueString(String selectedValueString) {
        this.selectedValueString = selectedValueString;
    }

    public ResultType getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(ResultType selectedValue) {
        this.selectedValue = selectedValue;
    }

    public List<SelectItem> getQueryMatches() {
        return queryMatches;
    }
    
    public void reset() {
        queryMatches = null;
        selectedValueString = "";
        selectedValue = null;
    }
}
