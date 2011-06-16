package com.joe.jsf.utilities;

import java.util.Comparator;

import javax.faces.model.SelectItem;

public class SelectItemSortByLabel implements Comparator<SelectItem> {

	public int compare(SelectItem o1, SelectItem o2) {
		if(null == o1 && null == o2)
			return 0;
		if(null == o1)
			return -1;
		if(null == o2)
			return 1;
		return o1.getLabel().compareToIgnoreCase(o2.getLabel());
	}

}
