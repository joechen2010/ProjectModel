package com.joe.facelets.table.filters;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

import com.icesoft.faces.component.ext.HtmlSelectOneMenu;

public class ListFilter extends Filter {
	private List<SelectItem> items;
	
	public ListFilter(List<SelectItem> items) {
		this.items = new ArrayList<SelectItem>(items);
		this.items.add(0,new SelectItem("", ""));
		createFilterComponent();
	}
	
	public ListFilter(List<SelectItem> items, int labelMaxChars) {
		this.items = new ArrayList<SelectItem>(items);
		this.items.add(0,new SelectItem("", ""));
		createFilterComponent();
		applyLabelLimit(labelMaxChars);
	}

	private void createFilterComponent() {
		filterComponent = new HtmlSelectOneMenu();
		
		List<UIComponent> children = filterComponent.getChildren();
		
		for(SelectItem item: items) {
			UISelectItem select = new UISelectItem();
			select.setValue(item);
			children.add(select);
		}
		
	}
	
	
	@Override
	public boolean isMatch(Object obj) {
		String filter = getValue();
		oldValue = filter;
		if(filter.equals("")) return true;
		
		return filter.equals(obj.toString());
	}

	/**
	 * In order to limit the width of the dropdown in the 
	 * homepage, truncate the description for each element
	 * to the specified character length.<BR>
	 * <BR>
	 * This will not affect the filtering of elements because the
	 * compare is done on the value of the SelectItem.
	 */
	@Override
	public void applyLabelLimit(int maxChars) {
		String text = "";
		for(SelectItem item: items) {
			text = item.getLabel();
			// only modify if the text needs modifying
			if(text != null && text.length() > maxChars) {
				item.setLabel(text.substring(0, maxChars - 1));
			}
		}
	}
}
