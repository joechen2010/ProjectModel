package com.joe.facelets.table;



import com.joe.facelets.table.ListTableSource.CustomComparator;
import com.joe.facelets.table.filters.Filter;
import com.joe.facelets.table.formatters.Format;

public class Column {
	private String name;
	private String key;
	private Filter filter;
	private Format format;
	private String cellStyleClass;
	private boolean visible = true;
	private boolean sortable = true;
	private CustomComparator sort;
	
	public Column(String name, String key, String cellStyleClass) {
	    this.name = name;
	    this.key = key;
	    this.cellStyleClass = cellStyleClass;
	}
	
	public Column(String name, String key) {
	    this.name = name;
	    this.key = key;
	}
	
	public CustomComparator getSort() {
		return sort;
	}
	public void setSort(CustomComparator sort) {
		this.sort = sort;
	}
	public String getCellStyleClass() {
		return cellStyleClass;
	}
	public void setCellStyleClass(String cellStyleClass) {
		this.cellStyleClass = cellStyleClass;
	}
	
	public boolean isSortable() {
		return sortable;
	}
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
}
