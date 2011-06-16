/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.joe.utilities.core.data;

/**
 * <p>The DataSource class is a utility class used by the data table paginator
 * and commandSortHeader example.</p>
 *
 * @since 0.3.0
 */
public abstract class DataSource {

    // Sortable Headers
    protected String sortColumnName;
    protected boolean sortAscending;

    // DataModel bound to dataTable
    protected PagedListDataModel<?> onePageDataModel;
    // bound to rows attribute in dataTable
    protected int pageSize = 15;

    protected DataSource(String defaultSortColumn) {
        sortColumnName = defaultSortColumn;
        sortAscending = isDefaultAscending(defaultSortColumn);
    }

    /**
     * Is the default sortColumnName direction for the given column "sortAscending" ?
     */
    protected abstract boolean isDefaultAscending(String sortColumn);

    /**
     * Gets the sortColumnName column.
     *
     * @return column to sortColumnName
     */
    public String getSortColumnName() {
        return sortColumnName;
    }

    /**
     * Sets the sortColumnName column.
     *
     * @param sortColumnName column to sortColumnName
     */
    public void setSortColumnName(String sortColumnName) {
        if (!sortColumnName.equals(this.sortColumnName)) {
            onePageDataModel.setDirtyData();
            this.sortColumnName = sortColumnName;
        }
    }

    /**
     * Is the sortColumnName sortAscending?
     *
     * @return true if the sortAscending sortColumnName otherwise false
     */
    public boolean isSortAscending() {
        return sortAscending;
    }

    /**
     * Sets sortColumnName type.
     *
     * @param sortAscending true for sortAscending sortColumnName, false for descending sortColumnName.
     */
    public void setSortAscending(boolean sortAscending) {
        if (sortAscending != (this.sortAscending)) {
            onePageDataModel.setDirtyData();
            this.sortAscending = sortAscending;
        }
    }

    public PagedListDataModel<?> getOnePageDataModel() {
        return onePageDataModel;
    }

    public int getPageSize() {
        return pageSize;
    }
}