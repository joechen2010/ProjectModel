package com.joe.utilities.core.facade;

import java.util.List;

import com.joe.utilities.core.manager.facade.ICommonFacade;

public interface IFacade{
	
	public List<?> findPage(String sortColumnName, boolean sortAscending, int startRow, int maxResults);
	
	public int getDataCount();
	
}
