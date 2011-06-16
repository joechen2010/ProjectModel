package com.joe.utilities.core.data;

/**
 * 
 * @author Linlin Yu
 * 
 */
public class PageRequest {
	private int pageSize;
	private int pageNumber;
	private String sortingColumn;
	private String sortingDirection;

	public PageRequest() {

	}

	public PageRequest(int pageSize) {
		super();
		this.pageSize = pageSize;
	}

	public PageRequest(int pageSize, String sortingColumn,
			String sortingDirection) {
		super();
		this.pageSize = pageSize;
		this.sortingColumn = sortingColumn;
		this.sortingDirection = sortingDirection;
	}

	private int start;
	private int limit;

	public int getStart() {
		start = (this.pageNumber - 1) * this.pageSize;
		return start;
	}

	public int getLimit() {
		return this.pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getSortingColumn() {
		return sortingColumn;
	}

	public void setSortingColumn(String sortingColumn) {
		this.sortingColumn = sortingColumn;
	}

	public String getSortingDirection() {
		return sortingDirection;
	}

	public void setSortingDirection(String sortingDirection) {
		this.sortingDirection = sortingDirection;
	}

}
