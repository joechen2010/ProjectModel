package cn.jcenterhome.web.action.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.web.action.BaseAction;


public class NetworkAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managenetwork")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		try {
			if (submitCheck(request, "networksubmit")) {
				Map<String, Object> network = (Map<String, Object>) getParameters(request, "network");
				if (network != null) {
					Pattern pattern = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
					Pattern separatorPattern = Pattern.compile(",");
					String[] arr = null;
					Map<Integer, Integer> narr = null;
					int tempV = 0;
					for (Entry<String, Object> entry : network.entrySet()) {
						String type = entry.getKey();
						Map<String, Object> values = (Map<String, Object>) entry.getValue();
						for (Entry<String, Object> subEntry : values.entrySet()) {
							String key = subEntry.getKey();
							String value = "";
							value = ((String) subEntry.getValue()).trim();
							if (value.indexOf(",") >= 0) {
								narr = new HashMap<Integer, Integer>();
								arr = separatorPattern.split(value);
								for (String v : arr) {
									v = v.trim();
									tempV = Common.intval(v);
									if (tempV != 0)
										narr.put(tempV, tempV);
								}
								value = Common.implode(narr, ",");
							} else if (pattern.matcher(value).matches()) {
								value = String.valueOf(Common.intval(value));
							}
							values.put(key, value);
						}
						network.put(type, values);
					}
					Common.setData("network", network, false);
					cacheService.network_cache();
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=network");
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		Map<String, Map<String, Object>> globalNetWork = Common.getCacheDate(request, response,
				"/data/cache/cache_network.jsp", "globalNetWork");
		Map<String, String> orders= new HashMap<String, String>();
		String[] types = new String[] {"blog", "pic", "thread", "poll", "event","share"};
		for (String type : types) {
			Map<String, Object> netWork = globalNetWork.get(type);
			if(netWork!=null){
				orders.put("order_"+type+"_"+netWork.get("order"), " selected");
				orders.put("sc_"+type+"_"+netWork.get("sc"), " selected");
			}
		}
		request.setAttribute("orders", orders);
		return mapping.findForward("network");
	}

	
	private Object getParameters(HttpServletRequest request, String prefix) {
		return getParameters(request, prefix, false);
	}

	
	private Object getParameters(HttpServletRequest request, String prefix, boolean isCheckBox) {
		Map<String, String[]> primalParameters = request.getParameterMap();
		if (primalParameters == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		String key;
		String[] value;
		String prefix_ = null;
		if (prefix != null) {
			prefix_ = prefix + "[";
		}
		for (Entry<String, String[]> primalPE : primalParameters.entrySet()) {
			key = primalPE.getKey();
			if (prefix == null || key.startsWith(prefix_)) {
				value = primalPE.getValue();
				if (!getParametersSetResultMap(result, key, value, isCheckBox)) {
					return null;
				}
			}
		}
		if (prefix != null) {
			return result.get(prefix);
		}
		return result;
	}

	private String disposeParameter(String parameterName) {
		if (parameterName.endsWith("[]")) {
			return parameterName.substring(0, parameterName.length() - 2);
		} else {
			return parameterName;
		}
	}

	private boolean getParametersSetResultMap(Map<String, Object> result, String key, String[] value,
			boolean isCheckBox) {
		key = disposeParameter(key);
		return getParametersParseKey(new StringBuilder(key), result, value, isCheckBox);
	}

	private boolean getParametersParseKey(StringBuilder operatingKey, Map<String, Object> supMap,
			String[] value, boolean isCheckBox) {
		int tempI = operatingKey.indexOf("[");
		int tempII = operatingKey.indexOf("]");
		if (tempI < 0) {
			putValue(supMap, operatingKey.toString(), value, isCheckBox);
			return true;
		} else if (tempII < tempI) {
			return false;
		}
		String subKey = operatingKey.substring(0, tempI);
		Map<String, Object> subMap = (Map<String, Object>) supMap.get(subKey);
		if (subMap == null) {
			subMap = new HashMap<String, Object>();
			supMap.put(subKey, subMap);
		}
		operatingKey.deleteCharAt(tempII);
		operatingKey.delete(0, tempI + 1);
		return getParametersParseKey(operatingKey, subMap, value, isCheckBox);
	}

	private void putValue(Map<String, Object> targetMap, String key, String[] value, boolean isCheckBox) {
		if (isCheckBox || value == null || value.length == 0) {
			targetMap.put(key, value);
		} else {
			targetMap.put(key, value[0]);
		}
	}
}