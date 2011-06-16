package com.joe.facelets.table.filters;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;

import org.apache.commons.lang.StringUtils;

import com.icesoft.faces.component.selectinputtext.SelectInputText;

public class TextFilter extends Filter {
	
	//TODO: provide some comments here as to why this is needed...
	@SuppressWarnings("deprecation")
	public static class DummyBinding extends MethodBinding {
		@SuppressWarnings("unchecked")
		@Override
		public Class getType(FacesContext context) throws MethodNotFoundException {
			return null;
		}

		@Override
		public Object invoke(FacesContext context, Object[] params) throws EvaluationException, MethodNotFoundException {
			return null;
		}
		
	}
	
	public TextFilter() {
		createFilterComponent();
	}

	private void createFilterComponent() {
		filterComponent = new SelectInputText();
		((SelectInputText)filterComponent).setOptions("{frequency:0.5}");
		((SelectInputText)filterComponent).setTextChangeListener(new DummyBinding());
	}

	@Override
	public boolean isMatch(Object obj) {
		String filter = getValue();
		oldValue = filter;

		String cell = obj.toString();

		return (StringUtils.isBlank(filter) || (cell).toLowerCase().startsWith(filter.toLowerCase().trim()));
	}

}
