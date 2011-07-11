package cn.jcenterhome.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import cn.jcenterhome.service.AdminDeleteService;
import cn.jcenterhome.service.BlogService;
import cn.jcenterhome.service.CacheService;
import cn.jcenterhome.service.CpService;
import cn.jcenterhome.service.CronService;
import cn.jcenterhome.service.DataBaseService;
import cn.jcenterhome.service.FeedService;
import cn.jcenterhome.service.MagicService;
import cn.jcenterhome.service.OpService;
import cn.jcenterhome.service.PmService;
import cn.jcenterhome.service.SpaceService;
import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.vo.MessageVO;


public class BaseAction extends DispatchAction {
	protected DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
	protected CacheService cacheService = (CacheService) BeanFactory.getBean("cacheService");
	protected AdminDeleteService adminDeleteService = (AdminDeleteService) BeanFactory
			.getBean("adminDeleteService");
	protected OpService opService = (OpService) BeanFactory.getBean("opService");
	protected CpService cpService = (CpService) BeanFactory.getBean("cpService");
	protected CronService cronService = (CronService) BeanFactory.getBean("cronService");
	protected SpaceService spaceService = (SpaceService) BeanFactory.getBean("spaceService");
	protected MagicService magicService = (MagicService) BeanFactory.getBean("magicService");
	protected BlogService blogService = (BlogService) BeanFactory.getBean("blogService");
	protected FeedService feedService = (FeedService) BeanFactory.getBean("feedService");
	protected PmService pmService = (PmService) BeanFactory.getBean("pmService");

	
	protected ActionForward cpMessage(HttpServletRequest request, ActionMapping mapping, MessageVO msgVO) {
		return cpMessage(request, mapping, msgVO.getMsgKey(), msgVO.getForwardURL(), msgVO.getSecond(), msgVO
				.getArgs());
	}

	
	protected ActionForward cpMessage(HttpServletRequest request, ActionMapping mapping, String msgKey) {
		return cpMessage(request, mapping, msgKey, null);
	}

	
	protected ActionForward cpMessage(HttpServletRequest request, ActionMapping mapping, String msgKey,
			String forwardURL) {
		return cpMessage(request, mapping, msgKey, forwardURL, 1);
	}

	
	protected ActionForward cpMessage(HttpServletRequest request, ActionMapping mapping, String msgKey,
			String forwardURL, int second, Object... args) {
		request.removeAttribute("globalAd");

		request.setAttribute("message", Common.getMessage(request, msgKey, args));
		request.setAttribute("url_forward", forwardURL);
		request.setAttribute("second", second);
		return mapping.findForward("cpmessage");
	}

	
	@SuppressWarnings("unchecked")
	protected ActionForward showMessage(HttpServletRequest request, HttpServletResponse response,
			MessageVO msgVO) {
		return showMessage(request, response, msgVO.getMsgKey(), msgVO.getForwardURL(), msgVO.getSecond(),
				msgVO.getArgs());
	}

	
	protected ActionForward showMessage(HttpServletRequest request, HttpServletResponse response,
			String msgkey) {
		return showMessage(request, response, msgkey, null);
	}

	
	protected ActionForward showMessage(HttpServletRequest request, HttpServletResponse response,
			String msgkey, String url_forward) {
		return showMessage(request, response, msgkey, url_forward, 1);
	}

	
	protected ActionForward showMessage(HttpServletRequest request, HttpServletResponse response,
			String msgKey, String forwardURL, int second, Object... args) {
		if ("".equals(forwardURL)) {
			forwardURL = null;
		}
		request.removeAttribute("globalAd");
		request.removeAttribute("sCookie");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String mobile = (String) sGlobal.get("mobile");
		String message = Common.getMessage(request, msgKey, args);
		if (Common.empty(mobile)) {
			int inajax = (Integer) sGlobal.get("inajax");
			if (inajax == 0 && forwardURL != null && second == 0) {
				response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
				response.setHeader("Location", forwardURL);
			} else {
				if (inajax > 0) {
					if (forwardURL != null) {
						message = "<a href=\"" + forwardURL + "\">" + message + "</a><ajaxok>";
					}
					try {
						PrintWriter out=response.getWriter();
						out.write(message);
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (forwardURL != null) {
						message = "<a href=\"" + forwardURL + "\">" + message
								+ "</a><script>setTimeout(\"window.location.href ='" + forwardURL + "';\", "
								+ (second * 1000) + ");</script>";
					}
					request.setAttribute("url_forward", forwardURL);
					request.setAttribute("message", message);
					include(request, response, sConfig, sGlobal, "showmessage.jsp");
				}
			}
		} else {
			request.setAttribute("url_forward", forwardURL);
			request.setAttribute("message", message);
			include(request, response, sConfig, sGlobal, "showmessage.jsp");
		}
		return null;
	}

	
	protected Map<String, String> getWheres(String[] intKeys, String[] strKeys, List<String[]> randKeys,
			String[] likeKeys, String pre, Map<String, String[]> paramMap, String timeoffset) {
		String prefix = " AND ";
		if (pre != null) {
			prefix += pre;
		}
		StringBuffer sql = new StringBuffer();
		StringBuffer url = new StringBuffer();
		if (intKeys != null) {
			for (String var : intKeys) {
				String[] values = paramMap.get(var);
				String value = values != null ? values[0] : null;
				if (Common.strlen(value) > 0) {
					sql.append(prefix + var + "='" + Common.intval(value) + "'");
					url.append("&" + var + "=" + value);
				}
			}
		}
		if (strKeys != null) {
			for (String var : strKeys) {
				String[] values = paramMap.get(var);
				String value = values != null ? values[0].trim() : null;
				if (Common.strlen(value) > 0) {
					sql.append(prefix + var + "='" + value + "'");
					url.append("&" + var + "=" + Common.urlEncode(value));
				}
			}
		}
		if (randKeys != null) {
			String method = null;
			String var = null;
			for (String[] vars : randKeys) {
				method = vars[0];
				var = vars[1];

				String[] values1 = paramMap.get(var + 1);
				String[] values2 = paramMap.get(var + 2);
				String temp1 = values1 != null ? values1[0].trim() : null;
				String temp2 = values2 != null ? values2[0].trim() : null;
				int value1 = 0;
				int value2 = 0;
				if ("sstrtotime".equals(method)) {
					value1 = Common.strToTime(temp1, timeoffset);
					value2 = Common.strToTime(temp2, timeoffset);
				} else if ("intval".equals(method)) {
					value1 = Common.intval(temp1);
					value2 = Common.intval(temp2);
				}
				if (value1 > 0) {
					sql.append(prefix + var + ">='" + value1 + "'");
					url.append("&" + var + "1=" + Common.urlEncode(temp1));
				}
				if (value2 > 0) {
					sql.append(prefix + var + "<='" + value2 + "'");
					url.append("&" + var + "2=" + Common.urlEncode(temp2));
				}
			}
		}
		if (likeKeys != null) {
			for (String var : likeKeys) {
				String[] values = paramMap.get(var);
				String value = values != null ? values[0].trim() : null;
				if (Common.strlen(value) > 0) {
					sql.append(prefix + var + " LIKE BINARY '%" + value + "%'");
					url.append("&" + var + "=" + Common.urlEncode(value));
				}
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("sql", sql.length() > 0 ? sql.substring(5) : null);
		map.put("url", url.toString());
		return map;
	}

	
	protected Map<String, String> getOrders(String[] allowOrders, String defaultStr, String pre,
			Map<String, String[]> paramMap) {
		String[] orderbys = paramMap.get("orderby");
		String[] orderscs = paramMap.get("ordersc");
		if (Common.empty(orderbys) || !Common.in_array(allowOrders, orderbys[0])) {
			orderbys = new String[] {defaultStr};
			paramMap.put("orderby", orderbys);
			if (Common.empty(orderscs)) {
				orderscs = new String[] {"desc"};
				paramMap.put("ordersc", orderscs);
			}
		}
		StringBuffer sql = new StringBuffer();
		StringBuffer url = new StringBuffer();
		if (pre == null) {
			sql.append(" ORDER BY " + orderbys[0] + " ");
		} else {
			sql.append(" ORDER BY " + pre + orderbys[0] + " ");
		}
		url.append("&orderby=" + orderbys[0]);
		if (!Common.empty(orderscs) && "desc".equals(orderscs[0])) {
			url.append("&ordersc=desc");
			sql.append(" DESC ");
		} else {
			url.append("&ordersc=asc");
		}
		Map<String, String> orders = new HashMap<String, String>();
		orders.put("sql", sql.toString());
		orders.put("url", url.toString());
		return orders;
	}

	
	protected boolean submitCheck(HttpServletRequest request, String var) throws Exception {
		if ("POST".equals(request.getMethod()) && !Common.empty(request.getParameter(var))) {
			String referer = request.getHeader("Referer");
			if (Common.empty(referer)
					|| referer.replaceAll("https?://([^:/]+).*", "$1").equals(
							request.getHeader("Host").replaceAll("([^:]+).*", "$1"))
					&& formHash(request).equals(request.getParameter("formhash"))) {
				return true;
			} else {
				throw new Exception("submit_invalid");
			}
		}
		return false;
	}

	
	protected boolean submitCheck(HttpServletRequest request, String submitModule, String formhash)
			throws Exception {
		if ("POST".equals(request.getMethod()) && !Common.empty(submitModule)) {
			String referer = request.getHeader("Referer");
			if (Common.empty(referer)
					|| referer.replaceAll("https?://([^:/]+).*", "$1").equals(
							request.getHeader("Host").replaceAll("([^:]+).*", "$1"))
					&& formHash(request).equals(formhash)) {
				return true;
			} else {
				throw new Exception("submit_invalid");
			}
		}
		return false;
	}

	
	@SuppressWarnings("unchecked")
	protected String formHash(HttpServletRequest request) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String formhash = (String) sGlobal.get("formhash");
		if (Common.empty(formhash)) {
			String timestamp = sGlobal.get("timestamp").toString();
			char split = '|';
			StringBuffer temp = new StringBuffer();
			temp.append(timestamp.substring(0, timestamp.length() - 7));
			temp.append(split);
			temp.append(sGlobal.get("supe_uid"));
			temp.append(split);
			temp.append(Common.md5((String) sConfig.get("sitekey")));
			temp.append(split);
			if (request.getAttribute("in_admincp") != null) {
				temp.append("Only For JavaCenter Home AdminCP");
			}
			formhash = Common.md5(temp.toString()).substring(8, 16);
			sGlobal.put("formhash", formhash);
		}
		return formhash;
	}

	
	@SuppressWarnings("unchecked")
	protected ActionForward include(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> sConfig, Map<String, Object> sGlobal, String pageName) {
		String tpl = null;
		try {
			tpl = Common.template(sConfig, sGlobal, pageName);
			request.getRequestDispatcher(tpl).include(request, response);
		} catch (Exception e) {
			try {
				response.getWriter().write("Template file : " + tpl + " Not found or have no access!<br>");
				response.getWriter().write("Message : " + e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	
	protected ActionForward invokeMethod(Object obj, String methodName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return (ActionForward) obj.getClass().getMethod(methodName, HttpServletRequest.class,
				HttpServletResponse.class).invoke(obj, request, response);
	}
}