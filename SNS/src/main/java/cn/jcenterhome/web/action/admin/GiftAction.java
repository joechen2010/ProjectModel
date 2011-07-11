package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileUploadUtil;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class GiftAction extends BaseAction {

	
	@SuppressWarnings("unchecked")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "managegift")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		
		String op = request.getParameter("op");
		try {
			FileUploadUtil req = new FileUploadUtil();
			req.parse(request, JavaCenterHome.JCH_CHARSET);
			if(submitCheck(request, req.getParameter("giftsubmit"), req.getParameter("formhash"))) {
				
				int giftId = Common.intval(req.getParameter("giftid"));
				int price = Common.intval(req.getParameter("price"));
				String giftName = req.getParameter("giftname");
				String tips = req.getParameter("tips");
				String typeId = req.getParameter("category");
				
				String iconPath = null;
				if(Common.empty(giftName)){
					return cpMessage(request, mapping, "cp_gift_name_can_not_be_empty");
				}
				if("defGift".equals(typeId)) {
					iconPath = "image/gift/";
					if(price > 0)
						price = 0;
				} else if("advGift".equals(typeId)) {
					iconPath = "image/gift/advanced/";
					if(price > 0)
						price = 0;
				} else {
					if(price <= 0)
						return showMessage(request, response, "gift_price_is_null");
					iconPath = "image/gift/fees/";
					typeId = req.getParameter("feecate");
				}
				
				Map<String, Object> setData = new HashMap<String, Object>();
				
				FileItem item = req.getFileItem("icon");
				int timestamp = Common.time();
				if(item.getName().length() != 0) {
					String iconExt = Common.fileext(item.getName()).toLowerCase();
					String icon = iconPath+System.currentTimeMillis()+"_"
							+Common.getRandStr(6, false)+"."+iconExt;
					FileUploadUtil.write2file(item, new File(JavaCenterHome.jchRoot+icon));
					setData.put("icon", icon);
				}
				setData.put("giftname", giftName);
				setData.put("tips", tips);
				setData.put("typeid", typeId);
				setData.put("addtime", timestamp);
				setData.put("price", price);
				
				if(giftId > 0) {
					Map<String, Object> whereData = new HashMap<String, Object>();
					whereData.put("giftid", giftId);
					dataBaseService.updateTable("gift", setData, whereData);
				} else {
					if(setData.get("icon") == null) {
						return showMessage(request, response, "gift_icon_is_null");
					}
					setData.put("buycount", 0);
					giftId = dataBaseService.insertTable("gift", setData, true, false);
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=gift", 1);
				
			
			} else if(submitCheck(request, "giftdeletesubmit")) {
				String giftId = request.getParameter("giftid");
				String used = request.getParameter("used");
				if(used != null) {
					dataBaseService.executeUpdate("DELETE FROM "+JavaCenterHome.getTableName("giftreceived")
							+" WHERE giftid='"+giftId+"'");
				}
				int ret = dataBaseService.executeUpdate("DELETE FROM "+JavaCenterHome.getTableName("gift")
						+" WHERE giftid='"+giftId+"'");
				if(ret != 0) {
					String typeId = request.getParameter("typeid");
					String icon = request.getParameter("icon");
					File giftIcon = new File(JavaCenterHome.jchRoot+icon);
					if(giftIcon.exists()) {
						giftIcon.delete();
					}
					
				}
				
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=gift", 1);
			
			
			} else if(submitCheck(request, "categorysubmit")) {
				String[] typeIds = request.getParameterValues("typeid[]");
				String[] typeNames = request.getParameterValues("typename[]");
				String[] orders = request.getParameterValues("order[]");
				for(int i = 0; i < typeIds.length; i++) {
					Map<String, Object> setData = new HashMap<String, Object>();
					Map<String, Object> whereData = new HashMap<String, Object>();
					setData.put("typeid", typeIds[i]);
					setData.put("typename", typeNames[i]);
					setData.put("order", orders[i]);
					whereData.put("typeid", typeIds[i]);
					dataBaseService.updateTable("gifttype", setData, whereData);
				}
				List<String> insDatas = new ArrayList<String>();
				for(int i = typeIds.length; i < typeNames.length; i++) {
					if(typeNames[i].length() != 0) {
						String data = "('"+Common.getRandStr(6, false)+i+"', '"+typeNames[i]+"', '"+Common.intval(orders[i])+"', '1')";
						insDatas.add(data);
					}
				}
				if(insDatas.size() > 0) {
					dataBaseService.executeUpdate("INSERT INTO "+JavaCenterHome.getTableName("gifttype")
							+" (typeid, typename, `order`, fee) VALUES "+Common.implode(insDatas, ","));
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=gift&view=category", 1);
				
			
			} else if(submitCheck(request, "categorydeletesubmit")) {
				String typeId = request.getParameter("typeid");
				String used = request.getParameter("used");
				if(used != null) {
					List<String> giftIds = dataBaseService.executeQuery("SELECT giftid FROM "+JavaCenterHome.getTableName("gift")+" WHERE typeid='"+typeId+"'", 1);
					dataBaseService.executeUpdate("DELETE FROM "+JavaCenterHome.getTableName("giftreceived")+" WHERE giftid IN ("+Common.sImplode(giftIds)+")");
					dataBaseService.executeUpdate("DELETE FROM "+JavaCenterHome.getTableName("gift")+" WHERE typeid='"+typeId+"'");
				}
				dataBaseService.executeUpdate("DELETE FROM "+JavaCenterHome.getTableName("gifttype")+" WHERE typeid='"+typeId+"'");
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=gift&view=category", 1);
				
			}
		} catch(Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		List<Map<String, Object>> categories = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("gifttype")+" ORDER BY fee, `order` ASC");
		Map<String, Map> categoryMap = new LinkedHashMap<String, Map>();
		for(Map<String, Object> cate : categories) {
			categoryMap.put((String) cate.get("typeid"), cate);
		}
		
		if("edit".equals(op) || "delete".equals(op)) {
			String typeId = request.getParameter("typeid");
			if(typeId != null) {
				List<Map<String, Object>> types = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("gifttype")+" WHERE typeid='"+typeId+"'");
				if(types.size() > 0) {
					List<String> count = dataBaseService.executeQuery("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("gift")+" WHERE typeid='"+typeId+"'", 1);
					if(Common.intval(count.get(0)) > 0) {
						request.setAttribute("used", true);
					}
					request.setAttribute("type", types.get(0));
				}
			} else {
				String giftId = request.getParameter("giftid");
				List<Map<String, Object>> gifts = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("gift")+" WHERE giftid='"+giftId+"'");
				if(gifts.size() > 0) {
					request.setAttribute("gift", gifts.get(0));
				}
				if("delete".equals(op)) {
					List<String> count = dataBaseService.executeQuery("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("giftreceived")+" WHERE giftid="+giftId, 1);
					if(Common.intval(count.get(0)) > 0) {
						request.setAttribute("used", true);
					}
				}
			}
		} else {
			if("category".equals(request.getParameter("view"))) {
				request.setAttribute("categorysize", categories.size());
				request.setAttribute("actives_category", " class=\"active\"");
			} else {
				int perpage = Common.intval(request.getParameter("perpage"));
				if (!Common.in_array(new Integer[] { 20, 50, 100, 1000 }, perpage)) {
					perpage = 20;
				}
				int page = Math.max(Common.intval(request.getParameter("page")), 1);
				int start = (page - 1) * perpage;
				Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
				int maxPage = (Integer) sConfig.get("maxpage");
				String result = Common.ckStart(start, perpage, maxPage);
				if (result != null) {
					return showMessage(request, response, result);
				}
				int count = 0;
				List<String> countStr = dataBaseService.executeQuery("SELECT COUNT(*) FROM "+JavaCenterHome.getTableName("gift"), 1);
				if(countStr.size() > 0) {
					count = Common.intval(countStr.get(0));
				}
				List<Map<String, Object>> gifts = null;
				if(count > 0) {
					gifts = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("gift")+" ORDER BY addtime DESC LIMIT "+start+","+perpage);
					request.setAttribute("gifts", gifts);
				}
				request.setAttribute("actives_gift", " class=\"active\"");
				request.setAttribute("multi", Common.multi(request, count, perpage, page, maxPage, "admincp.jsp?ac=gift", null, null));
			}
		}
		
		request.setAttribute("categorymap", categoryMap);
		return mapping.findForward("gift");
	}

}