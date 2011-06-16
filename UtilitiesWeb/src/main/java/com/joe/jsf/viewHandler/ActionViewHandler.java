/**
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  @version 
 * 
 */

package com.joe.jsf.viewHandler;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.servlet.http.HttpServletRequest;

/**
 *  
 * Action viewHandler. This class intercept renderView() call during 6-th phase. If 
 * request URL is matched to specified prefix, than current NavigationHandler will be
 * called to perform navigation according to rules. Two specific parameters are taken 
 * into account : <b>action</b> and <b>action-binding</b>. If <b>action-binding</b> exists,
 * it will be executed first to obtain outcome string, otherwise <b>action</b> parameter 
 * will be used for outcome. 
 * 
 *  demo URL: http://localhost:8080/test/action.jsf?action=actions.action
 *  rewrited URL : http://localhost:8080/test/action/actions.action
 */
public class ActionViewHandler extends ViewHandler {
	public static final String ACTION_PREFIX_PARAM = "org.joe.jsf.ACTION_PREFX";
	public static final String DEFAULT_ACTION_PREFIX = "/action";
	
	private ViewHandler viewHandler;
	private String actionPrefix = null;
	
	public ActionViewHandler(ViewHandler defaultViewHandler) {
		viewHandler = defaultViewHandler;
	}

	public Locale calculateLocale(FacesContext context) {
		return viewHandler.calculateLocale(context);
	}

	public String calculateRenderKitId(FacesContext context) {
		return viewHandler.calculateRenderKitId(context);
	}

	public UIViewRoot createView(FacesContext context, String strUID) {
		UIViewRoot ret = viewHandler.createView(context, strUID);
		return ret;
	}

	public String getActionURL(FacesContext context, String strUID) {
		return viewHandler.getActionURL(context, strUID);
	}

	public String getResourceURL(FacesContext context, String strUID) {
		return viewHandler.getResourceURL(context, strUID);
	}

	public void renderView(FacesContext context, UIViewRoot viewRoot) throws IOException, FacesException {
		String viewId = viewRoot.getViewId();
		if (!context.getResponseComplete() && isMapped(viewId)) {
			NavigationHandler nh = context.getApplication().getNavigationHandler();
			ViewHandler vh = context.getApplication().getViewHandler();
			String action = (String) context.getExternalContext().getRequestParameterMap().get("action");
			String outcome = (String) context.getExternalContext().getRequestParameterMap().get("outcome");
			if (action!=null) {
				String method = extractMethodName(action);
				MethodBinding mb = context.getApplication().createMethodBinding("#{"+action+"}", new Class[0]);
				outcome = mb.invoke(context, new Object[0]).toString();
				nh.handleNavigation(context, method, outcome);
				if (!context.getResponseComplete() && context.getViewRoot().equals(viewRoot)) {
					throw new FacesException("No navigation rules from viewId="+viewId+", action="+action+", outcome="+outcome+" found.");
				}
			} else {
				nh.handleNavigation(context, null, outcome);
				if (!context.getResponseComplete() && context.getViewRoot().equals(viewRoot)) {
					throw new FacesException("No navigation rules from viewId="+viewId+", outcome="+outcome+" found.");
				}
			}
			if (!context.getResponseComplete()) {
				vh.renderView(context,context.getViewRoot());
			};
		} else {
			viewHandler.renderView(context, viewRoot);
		}
	}
	
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		UIViewRoot ret = viewHandler.restoreView(context, viewId);
		return ret;
	}

	public void writeState(FacesContext context) throws IOException {
		viewHandler.writeState(context);
	}

	private synchronized void init(FacesContext context) {
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		actionPrefix = (String) request.getSession().getServletContext().getInitParameter(ACTION_PREFIX_PARAM);
		if (actionPrefix==null || (actionPrefix!=null && actionPrefix.length()==0)) {
			actionPrefix = DEFAULT_ACTION_PREFIX;
		}
	}
	
	private String extractMethodName(String methodBinding) {
		if (methodBinding!=null && methodBinding.length()>1) {
			if (methodBinding.charAt(methodBinding.length()-1) == ']') {
				int pos = methodBinding.lastIndexOf('[');
				if (pos>0) {
					return methodBinding.substring(pos+1, methodBinding.length()-1);
				} else {
					throw new FacesException("Illegal method binding: "+methodBinding);
				}
			} else {
				int pos = methodBinding.lastIndexOf('.');
				if (pos>0) {
					return methodBinding.substring(pos+1);
				} else {
					throw new FacesException("Illegal method binding: "+methodBinding);
				}
			}
		} else {
			return null;
		}
	}
	
	private String getActionPrefix() {
		if (actionPrefix == null) {
			init(FacesContext.getCurrentInstance());
		}
		return actionPrefix;
	}
	
	private boolean isMapped(String viewId) {
		if (viewId != null) {
			return viewId.startsWith(getActionPrefix());
		} else {
			return false;
		}
	}
}
