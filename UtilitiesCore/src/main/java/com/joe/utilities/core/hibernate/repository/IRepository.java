package com.joe.utilities.core.hibernate.repository;

import java.util.List;

public interface IRepository{

	public int getDataCount();
	
	public List<?> findPage(String sortColumnName, boolean sortAscending, int startRow, int maxResults);
	
}
