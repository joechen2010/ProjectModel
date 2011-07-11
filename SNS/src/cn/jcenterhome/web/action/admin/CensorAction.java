package cn.jcenterhome.web.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.web.action.BaseAction;


public class CensorAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managecensor")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}

		try {
			if (submitCheck(request, "censorsubmit")) {
				String censor = Common.trim(request.getParameter("censor"));
				String[] censorDatas = censor.split("\n");
				List<String> newData = new ArrayList<String>();
				for (String censorData : censorDatas) {
					String[] list = censorData.split("=");
					int length = list.length;
					if (list != null && length > 0) {
						String newFind = Common.trim(list[0]);
						if (Common.strlen(newFind) >= 3) {
							String newReplace = null;
							if (length > 1) {
								newReplace = list[1];
							} else {
								newReplace = "";
							}
							newReplace = newReplace.trim();
							if (Common.strlen(newReplace) < 1) {
								newReplace = "**";
							}
							newData.add(newFind + "=" + newReplace);
						}
					}
				}
				Common.setData("censor", newData.size() > 0 ? Common.implode(newData, "\n") : "", false);
				cacheService.censor_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=censor");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		request.setAttribute("censor", Common.getData("censor"));
		request.setAttribute("banflag", "{BANNED}");
		return mapping.findForward("censor");
	}
}