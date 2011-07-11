package cn.jcenterhome.web.action.admin;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.service.StatService;
import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.web.action.BaseAction;


public class StatAction extends BaseAction {
	private StatService statService = (StatService) BeanFactory.getBean("statService");

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managestat")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		String turl = "admincp.jsp?ac=stat";
		int perpage = Common.intval(request.getParameter("perpage"));
		String countType = request.getParameter("counttype");
		countType = Common.empty(countType) ? "" : countType.trim() + "Stat";
		if (perpage > 0 && !Common.empty(countType)) {
			int start = Common.intval(request.getParameter("start"));
			if (start < 0) {
				start = 0;
			}
			boolean isExecute = false;
			try {
				Class[] paramTypes = {int.class, int.class};
				Object[] params = {start, perpage};
				Method method = statService.getClass().getMethod(countType, paramTypes);
				isExecute = (Boolean) method.invoke(statService, params);
			} catch (Exception e) {
				return cpMessage(request, mapping, "cp_choose_to_reconsider_statistical_data_types");
			}
			if (isExecute) {
				String jump = turl + "&counttype=" + request.getParameter("counttype") + "&perpage="
						+ request.getParameter("perpage") + "&start=" + (start + perpage);
				return cpMessage(request, mapping, "cp_data_processing_please_wait_patiently", jump, 0, jump,
						start, turl);
			} else {
				return cpMessage(request, mapping, "do_success", turl);
			}
		}
		return mapping.findForward("stat");
	}
}