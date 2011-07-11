package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.dao.DataBaseDaoImpl;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.web.action.BaseAction;


public class BlockAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		if (!Common.checkPerm(request, response, "manageblock")) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation");
		}
		boolean valueSubmit = false;
		boolean codeSubmit = false;
		try {
			valueSubmit = submitCheck(request, "valuesubmit");
			if (!valueSubmit)
				codeSubmit = submitCheck(request, "codesubmit");
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		String op = request.getParameter("op");
		if (valueSubmit) {
			String blockName = (String) Common.sHtmlSpecialChars(request.getParameter("blockname").trim());
			if (blockName.length() == 0) {
				return cpMessage(request, mapping, "cp_correctly_completed_module_name");
			}
			String blockSql = getBlockSql(request.getParameter("blocksql"));
			Map<String,Object> dataInfo = dataBaseService.execute(sqlFilter(blockSql, (Integer) sGlobal.get("timestamp")));
			if (blockSql != null && dataInfo.get("errorCode") != null) {
				String[] args = { (String)dataInfo.get("error"), String.valueOf(dataInfo.get("errorCode")) };
				return cpMessage(request, mapping, "cp_sql_statements_can_not_be_completed_for_normal", "", 1,args);
			}
			int bid = Common.intval(request.getParameter("bid"));
			if (bid != 0) {
				dataBaseService.executeUpdate("UPDATE " + JavaCenterHome.getTableName("block")
						+ " SET `blockname`='" + blockName + "',`blocksql`='" + blockSql + "' WHERE `bid`='"
						+ bid + "'");
			} else {
				bid = dataBaseService.insert("INSERT INTO " + JavaCenterHome.getTableName("block")
						+ " (`blockname`,`blocksql`,`htmlcode`) VALUES ('" + blockName + "','" + blockSql
						+ "','')");
			}
			return cpMessage(request, mapping, "cp_enter_the_next_step", "admincp.jsp?ac=block&op=code&id=" + bid, 1);
		}
		else if (codeSubmit) {
			int bid = Common.intval(request.getParameter("bid"));
			int cacheTime = Common.intval(request.getParameter("cachetime"));
			int startNum = Common.intval(request.getParameter("startnum"));
			int num = Common.intval(request.getParameter("num"));
			int perPage = Common.intval(request.getParameter("perpage"));
			String cacheName = request.getParameter("cachename");
			String htmlCode = request.getParameter("htmlcode").trim();
			Map<String, Object> block = getBlock(bid);
			if (perPage != 0) {
				num = 0;
			}
			htmlCode = Common.addSlashes(Common.stripSlashes(htmlCode).replaceAll(
					"(?i)href=\"(?!http://)(.+?)\"", "href=\"" + Common.getSiteUrl(request) + "$1\""));
			String sql = "UPDATE " + JavaCenterHome.getTableName("block") + " SET `cachename`='" + cacheName
					+ "',`cachetime`='" + cacheTime + "',`startnum`='" + startNum + "',`num`='" + num
					+ "',`perpage`='" + perPage + "',`htmlcode`='" + htmlCode + "' WHERE `bid`='" + bid + "'";
			dataBaseService.executeUpdate(sql);
			try {
				cacheService.block_cache();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!Common.empty(block.get("blocksql"))) {
				String perStr = null;
				if (Common.empty(perPage)) {
					perStr = "";
					if (Common.empty(num)) {
						num = 1;
					}
					block.put("blocksql", block.get("blocksql") + " LIMIT " + startNum + "," + num);
				} else {
					perStr = "perpage/" + perPage + "/";
				}

				htmlCode = "<%@ page language=\"java\"  pageEncoding=\""+JavaCenterHome.JCH_CHARSET+"\"%>\r\n<%@ taglib uri=\"http://java.sun.com/jsp/jstl/core\" prefix=\"c\"%>\r\n<%@ taglib uri=\"http://jchome.jsprun.com/jch\" prefix=\"jch\"%>\r\n${jch:showData(pageContext.request,\""+perStr+"sql/"
						+ Common.urlEncode((String) block.get("blocksql"))
						+ "/cachename/" + cacheName + "/cachetime/" + cacheTime + "\")}\r\n"
						+ Common.stripSlashes(htmlCode);
			}
			String tpl = JavaCenterHome.jchRoot + "./data/blocktpl/" + bid + ".jsp";
			FileHelper.writeFile(tpl, htmlCode,request);
			return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=block");
		}
		
		Map<String, String> jchConf = JavaCenterHome.jchConfig;
		if (op == null) {
			Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
			String page = request.getParameter("page");
			int perPage = 20;
			int maxPage = Common.intval(String.valueOf(sConfig.get("maxpage")));
			int curPage = Common.empty(page) ? 1 : Common.intval(page);
			if (curPage < 1) {
				curPage = 1;
			}
			int startRow = (curPage - 1) * perPage;
			String result = Common.ckStart(startRow, perPage, maxPage);
			if (result != null) {
				return showMessage(request, response, "length_is_not_within_the_scope_of");
			}
			int total = Common.intval(Common.getCount("block", null, null));
			if (total > 0) {
				String sql = "SELECT * FROM " + JavaCenterHome.getTableName("block")
						+ " ORDER BY bid DESC LIMIT " + startRow + "," + perPage;
				List<Map<String, Object>> blockList = dataBaseService.executeQuery(sql);
				String multi = Common.multi(request, total, perPage, curPage, maxPage,
						"admincp.jsp?ac=block", null, null);
				request.setAttribute("list", blockList);
				request.setAttribute("multi", multi);
			}
			request.setAttribute("active", " class=\"active\"");
		}
		else if (op.equals("code")) {
			int bid = Common.intval(request.getParameter("id"));
			Map<String, Object> block = getBlock(bid);
			if (block == null) {
				return cpMessage(request, mapping, "cp_designated_data_transfer_module_does_not_exist");
			}
			String blockSql = (String) block.get("blocksql");
			List<String> keys = new ArrayList<String>();
			List<String> colname = new ArrayList<String>();
			Map<String, String> colNames = new HashMap<String, String>();
			if (!Common.empty(blockSql)) {
				DataBaseDaoImpl dbimpl = new DataBaseDaoImpl();
				List<Map<String,Object>> blockList = dbimpl.executeQueryByBlock(sqlFilter(blockSql,(Integer) sGlobal.get("timestamp")));
				Map<String,Object> value = blockList.size() > 0 ? blockList.get(0) : null;
				if (value != null) {
					List<String> columnNames = (List<String>)value.get("columnname");
					for (String keyName : columnNames) {
						try {
							if (keys.size() < 2)
								keys.add(keyName);
							colname.add(keyName);
							colNames.put(keyName, Common.getStr(String.valueOf(value.get(keyName)), 40, false,false, false, 0, 0, request, response));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				request.setAttribute("colname", colname);
				request.setAttribute("colnames", colNames);
				Map<String, String> colNames_slash = new HashMap<String, String>();
				for (Iterator<String> it = colNames.keySet().iterator(); it.hasNext();) {
					String key = it.next();
					colNames_slash.put(key, Common.addCSlashes((String) colNames.get(key),new char[] { '\'', '\\' }));
				}
				request.setAttribute("colnames_slash", colNames_slash);
			}
			if (Common.empty(block.get("cachename"))) {
				block.put("cachename", "block" + block.get("bid"));
			}
			if (Common.empty(block.get("htmlcode")) && !colNames.isEmpty()) {
				StringBuffer htmlCode = new StringBuffer();
				htmlCode.append("<ul>\r\n");
				htmlCode.append("<c:forEach items=\"${sBlock." + block.get("cachename")
						+ "}\" var=\"value\">\r\n");
				htmlCode.append("<li>${value." + keys.get(0) + "} ${value." + keys.get(1) + "}\r\n");
				htmlCode.append("</c:forEach>\r\n");
				htmlCode.append("</ul>\r\n");
				block.put("htmlcode", htmlCode.toString());
			}
			block.put("htmlcode", Common.sHtmlSpecialChars(block.get("htmlcode")));
			request.setAttribute("block", block);
			request.setAttribute("formhash", formHash(request));
		}
		else if (op.equals("add")) {
			Map<String, Map<Integer, String>> tables = getTables(jchConf.get("tablePre"));
			Map<String, String> sqlTables = new HashMap<String, String>();
			Map<String, String> sqls = new HashMap<String, String>();
			sqlTables.put("blog", JavaCenterHome.getTableName("blog"));
			sqlTables.put("album", JavaCenterHome.getTableName("album"));
			sqlTables.put("thread", JavaCenterHome.getTableName("thread"));
			sqlTables.put("feed", JavaCenterHome.getTableName("feed"));
			sqlTables.put("space", JavaCenterHome.getTableName("space"));
			sqlTables.put("pic", JavaCenterHome.getTableName("pic"));
			sqlTables.put("mtag", JavaCenterHome.getTableName("mtag"));
			sqls.put("blog", "SELECT * FROM `" + JavaCenterHome.getTableName("blog")
					+ "` AS `blog`WHEREORDER");
			sqls.put("album", "SELECT * FROM `" + JavaCenterHome.getTableName("album")
					+ "` AS `album`WHEREORDER");
			sqls.put("thread", "SELECT * FROM `" + JavaCenterHome.getTableName("thread")
					+ "` AS `thread`WHEREORDER");
			sqls.put("feed", "SELECT * FROM `" + JavaCenterHome.getTableName("feed")
					+ "` AS `feed`WHEREORDER");
			sqls.put("space", "SELECT * FROM `" + JavaCenterHome.getTableName("space")
					+ "` AS `space`WHEREORDER");
			sqls.put("pic", "SELECT * FROM `" + JavaCenterHome.getTableName("pic") + "` AS `pic`WHEREORDER");
			sqls.put("mtag", "SELECT * FROM `" + JavaCenterHome.getTableName("mtag")
					+ "` AS `mtag`WHEREORDER");
			List<Map<String, Object>> list = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("profield") + " ORDER BY displayorder");
			List<Map<String, Object>> userGroupArr = dataBaseService
					.executeQuery("SELECT gid,grouptitle FROM " + JavaCenterHome.getTableName("usergroup"));
			request.setAttribute("list", list);
			request.setAttribute("usergrouparr", userGroupArr);
			request.setAttribute("sqls", sqls);
			request.setAttribute("sqltables", sqlTables);
			request.setAttribute("tables", tables);
			request.setAttribute("formhash", formHash(request));
		}
		else if (op.equals("blocksql")) {
			Map<String, Object> block = getBlock(Common.intval(request.getParameter("id")));
			Map<String, Map<Integer, String>> tables = getTables(jchConf.get("tablePre"));
			request.setAttribute("block", block);
			request.setAttribute("tables", tables);
			request.setAttribute("formhash", formHash(request));
		}
		else if (op.equals("tpl")) {
			int bid = Common.intval(request.getParameter("id"));
			String code = (String) Common.sHtmlSpecialChars("<jsp:include flush=\"true\" page=\"/data/blocktpl/"+bid+".jsp\" />");
			request.setAttribute("code", code);
		}
		else if (op.equals("js")) {
			int bid = Common.intval(request.getParameter("id"));
			String code = (String) Common
					.sHtmlSpecialChars("<script language=\"javascript\" type=\"text/javascript\" src=\""
							+ Common.getSiteUrl(request) + "js.jsp?id=" + bid + "\"></script>");
			request.setAttribute("code", code);
		}
		else if (op.equals("delete")) {
			int bid = Common.intval(request.getParameter("id"));
			if (bid != 0 && Common.checkPerm(request, response, "managead") && deleteBlock(bid)) {
				return cpMessage(request, mapping, "cp_a_call_to_delete_the_specified_modules_success",
						"admincp.jsp?ac=block");
			} else {
				return cpMessage(request, mapping, "cp_choose_to_delete_the_data_transfer_module",
						"admincp.jsp?ac=block");
			}
		}
		request.setAttribute("turl", "admincp.jsp?ac=block");
		return mapping.findForward("block");
	}

	private String getBlockSql(String sql) {
		if (sql != null && sql.length() > 15) {
			sql = sql.replaceAll("(?i)(select)", "");
			sql = sql.replaceAll("(?i)(\\s+limit.+)", "");
			sql = sql.replace(";", "");
			sql = "SELECT " + sql.trim();
		} else {
			sql = "";
		}
		return sql;
	}
	private boolean deleteBlock(int bid) {
		List<Map<String, Object>> blockList = dataBaseService.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("block") + " WHERE bid=" + bid);
		if (blockList.size() > 0) {
			String tpl = JavaCenterHome.jchRoot + "./data/blocktpl/" + blockList.get(0).get("bid") + ".jsp";
			File tplfile = new File(tpl);
			if(tplfile.exists()){
				FileHelper.writeFile(tplfile, " ");
				try {
					cacheService.block_cache();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dataBaseService.executeUpdate("DELETE FROM " + JavaCenterHome.getTableName("block")
					+ " WHERE bid=" + bid);
			return true;
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	private Map<String, Map<Integer, String>> getTables(String tablePrefix) {
		String filePath = JavaCenterHome.jchRoot + "data/data_table_" + JavaCenterHome.JCH_RELEASE + ".txt";
		String content = FileHelper.readFile(filePath).trim();
		Map<String, Map<Integer, String>> tables = null;
		if (content.length() > 0) {
			tables = (Map<String, Map<Integer, String>>) Serializer.unserialize(content, true);
		} else {
			List<String> tableNames = dataBaseService.executeQuery("SHOW TABLES LIKE '" + tablePrefix + "%'",
					1);
			tables = new TreeMap<String, Map<Integer, String>>();
			for (String table : tableNames) {
				if (table.indexOf("cache") == -1) {
					String createSql = dataBaseService.executeQuery("SHOW CREATE TABLE " + table, 2).get(0);
					tables.put(table, getColumns(createSql));
				}
			}
			FileHelper.writeFile(filePath, Serializer.serialize(tables));
		}
		return tables;
	}
	private Map<Integer, String> getColumns(String createSql) {
		Map<Integer, String> columns = new TreeMap<Integer, String>();
		int i = 0;
		String[] values = createSql.split("\n");
		for (String value : values) {
			value = Common.trim(value);
			value = value.substring(0, value.indexOf(" ")).replace("`", "");
			if (!value.matches("(?i)CREATE|PRIMARY|KEY|\\)")) {
				columns.put(i, value);
				i++;
			}
		}
		return columns;
	}
	private String sqlFilter(String sql, int timeStamp) {
		if (sql != null && sql.length() > 0) {
			Matcher m = Pattern.compile("\\[(\\d+)\\]").matcher(sql);
			if (m.find()) {
				int time = Common.intval(m.group(1));
				int temptime = timeStamp - time;
				StringBuffer buffer = new StringBuffer();
				m.appendReplacement(buffer, temptime+"");
				m.appendTail(buffer);
				sql = buffer.toString();
			}
			sql = Common.stripSlashes(sql) + " LIMIT 1";
		}
		return sql;
	}
	private Map<String, Object> getBlock(int bid) {
		Map<String, Object> block = null;
		if (bid != 0) {
			List<Map<String, Object>> blockList = dataBaseService.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("block") + " WHERE bid='" + bid + "'");
			if (blockList.size() > 0) {
				block = blockList.get(0);
			}
		}
		return block;
	}
}