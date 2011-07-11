package cn.jcenterhome.web.action.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class CreditAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managecredit")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		try {
			if (submitCheck(request, "creditsubmit")) {
				int rid = Common.intval(request.getParameter("rid"));
				String sql = "SELECT * FROM " + JavaCenterHome.getTableName("creditrule") + " WHERE rid="
						+ rid;
				List<Map<String, Object>> query = dataBaseService.executeQuery(sql);
				if (query.isEmpty()) {
					return cpMessage(request, mapping, "cp_rules_do_not_exist_points",
							"admincp.jsp?ac=credit");
				}
				Map<String, Object> rule = query.get(0);
				int rewardType = (Integer) rule.get("rewardtype");
				int cycleType = Common.intval(request.getParameter("cycletype"));
				int credit = Common.range(request.getParameter("credit"), 16777215, 0);
				int experience = Common.range(request.getParameter("experience"), 16777215, 0);
				int cycleTime = Common.range(request.getParameter("cycletime"), 2147483647, 0);
				int rewardNum = Common.range(request.getParameter("rewardnum"), 127, 0);

				StringBuffer setSql = new StringBuffer();
				setSql.append("`credit`='" + credit + "'");
				setSql.append(",`experience`='" + experience + "'");
				setSql.append(",`cycletype`='" + cycleType + "'");
				setSql.append(",`cycletime`='" + cycleTime + "'");
				setSql.append(",`rewardnum`='" + rewardNum + "'");

				if (rewardType == 0) {
					setSql.append(",`cycletype`='" + 0 + "'");
					setSql.append(",`cycletime`='" + 0 + "'");
					setSql.append(",`rewardnum`='" + 1 + "'");
				} else if (cycleType == 0) {
					setSql.append(",`cycletime`='" + 0 + "'");
					setSql.append(",`rewardnum`='" + 1 + "'");
				}

				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("creditrule") + " SET "
						+ setSql + " WHERE `rid`='" + rid + "'");
				cacheService.creditrule_cache();
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=credit&rewardtype="
						+ rewardType, 1);
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		if ("edit".equals(request.getParameter("op"))) {
			int rid = Common.intval(request.getParameter("rid"));
			Map<String, Object> rule = null;
			if (rid > 0) {
				String sql = "SELECT * FROM " + JavaCenterHome.getTableName("creditrule") + " WHERE rid='"
						+ rid + "'";
				List<Map<String, Object>> query = dataBaseService.executeQuery(sql);
				rule = query.isEmpty() ? null : query.get(0);
			}
			if (rule == null) {
				return cpMessage(request, mapping, "cp_rules_do_not_exist_points", "admincp.jsp?ac=credit");
			}
			request.setAttribute("rule", rule);
		} else {
			Map<String, String[]> paramMap = request.getParameterMap();
			String[] rewardTypes = paramMap.get("rewardtype");
			int rewardType = rewardTypes == null ? 1 : Common.intval(rewardTypes[0]);
			paramMap.put("rewardtype", new String[] {String.valueOf(rewardType)});
			request.setAttribute("actives_" + rewardType, " class='active'");
			String[] intKeys = {"rewardtype", "cycletype"};
			List<String[]> randKeys = new ArrayList<String[]>();
			randKeys.add(new String[] {"intval", "credit"});
			randKeys.add(new String[] {"intval", "experience"});
			String[] likeKeys = {"rulename"};
			Map<String, String> wheres = getWheres(intKeys, null, randKeys, likeKeys, null, paramMap, null);
			String whereSQL = wheres.get("sql") == null ? "1" : wheres.get("sql");

			String sql = "SELECT * FROM " + JavaCenterHome.getTableName("creditrule") + " WHERE " + whereSQL;
			List<Map<String, Object>> list = dataBaseService.executeQuery(sql);
			request.setAttribute("rewardtype", rewardType);
			request.setAttribute("list", list);
		}
		Map<Integer, String> rewardTypes = new HashMap<Integer, String>();
		rewardTypes.put(0, "扣分");
		rewardTypes.put(1, "加分");
		request.setAttribute("rewardTypes", rewardTypes);
		Map<Integer, String> cycleTypes = new HashMap<Integer, String>();
		cycleTypes.put(0, "一次性");
		cycleTypes.put(1, "每天");
		cycleTypes.put(2, "整点");
		cycleTypes.put(3, "间隔分钟");
		cycleTypes.put(4, "不限周期");
		request.setAttribute("cycleTypes", cycleTypes);
		return mapping.findForward("credit");
	}
}