package com.joe.facelets.table;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.icesoft.faces.context.effects.Effect;

public abstract class TableSource {
	protected boolean sortDecending = false;
	protected String sortKey = null;
	protected boolean oldDecending;
	protected String oldKey;
	protected Effect tableChangeEffect;
	protected List<Column> columns = new ArrayList<Column>();
	protected int rowsPerPage = 13;

	public TableSource(List<Column> columns) {
		this.columns = columns;
	}

	private void triggerEffect() {
		if (tableChangeEffect != null) {
			tableChangeEffect.setFired(false);
		}
	}

	public void clearFilters() {
		for (Column c : columns) {
			if (c.getFilter() != null) {
				c.getFilter().clear();
			}
		}
	}

	public boolean hasFilterChanged() {
		for (Column c : columns) {
			if (c.getFilter() != null) {
				if (c.getFilter().hasChanged()) {
					triggerEffect();
					return true;
				}
			}
		}
		return false;
	}

	public void deselectAll() {
		setAllSelected(false);
	}

	public void setAllSelected(boolean selected) {
		for (Row r : getFilteredRows()) {
			r.setSelected(selected);
		}
	}

	public boolean isAllSelected() {
		for (Row r : getFilteredRows()) {
			if (!r.isSelected())
				return false;
		}
		return true;
	}

	public boolean hasSortChanged() {
		if (!(sortKey == null || (oldKey == sortKey && oldDecending == sortDecending))) {
			triggerEffect();
			return true;
		}
		return false;
	}

	public abstract DataModel getDataModel();

	public abstract List<Row> getFilteredRows();

	public abstract List<Row> getRows();

	public List<Column> getColumns() {
		return columns;
	}

	public String getColumnClasses() {
		final List<String> result = new ArrayList<String>();

		for (final Column c : columns) {
			result.add(c.getCellStyleClass());
		}

		return StringUtils.join(result, ", ");
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Effect getTableChangeEffect() {
		return tableChangeEffect;
	}

	public void setTableChangeEffect(Effect tableChangeEffect) {
		this.tableChangeEffect = tableChangeEffect;
	}

	public boolean isSortAscending() {
		return sortDecending;
	}

	public void setSortAscending(boolean sortAscending) {
		this.sortDecending = sortAscending;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}
}
