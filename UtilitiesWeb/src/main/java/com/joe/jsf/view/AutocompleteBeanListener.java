package com.joe.jsf.view;

/***
 * A listener for events on an autocomplete bean
 * @author minger
 *
 * @param <ResultType>
 */
public interface AutocompleteBeanListener<ResultType> {
    /***
     * Response to the event when an actual value is selected.
     * 
     * @param value
     */
    void autocompleteValueSelected(ResultType value);

    /***
     * Respond to the field being blanked out
     */
    void autocompleteValueBlank();

    /***
     * Respond to the no results found item being selected.
     */
    void autocompleteValueSelectedNoResultsFound();
}
