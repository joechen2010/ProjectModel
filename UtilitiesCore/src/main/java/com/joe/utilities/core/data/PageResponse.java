package com.joe.utilities.core.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author Linlin Yu
 *
 * @param <E>
 */
public class PageResponse<E> {
	private long totalCount;
	private long start;
	private long end;
	private List<E> list;

	public PageResponse(long totalCount, List<E> list) {
		super();
		this.totalCount = totalCount;
		this.list = list;
	}

	public PageResponse() {
		super();
		this.totalCount = 0;
		this.list = new ArrayList<E>();
	}
	
	public PageResponse(PageRequest pageRequest) {
		super();
		this.totalCount = 0;
		this.list = new ArrayList<E>();
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

}
