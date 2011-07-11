
package cn.jcenterhome.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Md5Util;
import cn.jcenterhome.util.Serializer;


public class BlockService {
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
	@SuppressWarnings("unchecked")
	public void block_batch(HttpServletRequest request,String param) {
		Map<String, Object> SBLOCK = new HashMap<String, Object>();
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		String cachekey = smd5(param);
		Map<String,Object> paramarr = parseparameter(param);
		if(Common.empty(sConfig.get("allowcache"))) {
			paramarr.put("cachetime", 0);
		} else {
			paramarr.put("cachetime", Common.intval((String)paramarr.get("cachetime")));
		}
		
		if(!Common.empty(paramarr.get("perpage"))) {
			String pages = request.getParameter("page");
			int page = Common.empty(pages)?1:Common.intval(pages);
			if(page<1) page = 1;
			if(page>1&&(Integer)paramarr.get("cachetime")>0){
				cachekey = smd5(param+page);
			}
		}
		Map caches = new HashMap();
		if((Integer)paramarr.get("cachetime")>0) {
			caches = block_get(cachekey,sGlobal,sConfig);
		}
		if(!Common.empty(caches.get("mtime")) && (Integer)sGlobal.get("timestamp")-(Integer)caches.get("mtime") <= (Integer)paramarr.get("cachetime")) {
			SBLOCK.put((String) paramarr.get("cachename"), caches.get("values"));
			SBLOCK.put(paramarr.get("cachename")+"_multipage",caches.get("multi"));
			
		} else {
			List<Map<String, Object>> blockarr = new ArrayList<Map<String, Object>>();
			Map<String, Object> results = getparamsql(request,paramarr,sGlobal,sConfig);
			if((Integer)results.get("count")>0) {
				List<Map<String,Object>> values = dataBaseService.executeQuery((String)results.get("sql"));
				blockarr = values;
			}
			SBLOCK.put((String) paramarr.get("cachename"), blockarr);
			SBLOCK.put(paramarr.get("cachename")+"_multipage", results.get("multi"));
			if((Integer)paramarr.get("cachetime")>0) {
				Map<String,Object> multiMap = new HashMap<String,Object>();
				multiMap.put("multipage", results.get("multi"));
				blockarr.add(multiMap);
				block_set(cachekey, blockarr,sGlobal,sConfig);
			}
		}
		request.setAttribute("sBlock", SBLOCK);
	}
	private String smd5(String str) {
		return Md5Util.encode(str).substring(8, 16);
	}
	private Map<String,Object> parseparameter(String param) {
		Map<String,Object> paramarr = new HashMap<String,Object>();
		String[]sarr = param.split("/");
		if(Common.empty(sarr)) return paramarr;
		int length = sarr.length;
		for(int i=0; i<length; i=i+2) {
			if((i+1)<length&&!Common.empty(sarr[i+1])) paramarr.put(sarr[i], Common.urlDecode(sarr[i+1]).replace("/", "").replace("\\", ""));
		}
		return paramarr;
	}
	@SuppressWarnings("unchecked")
	private Map<String,Object> getparamsql(HttpServletRequest request,Map<String,Object> paramarr,Map<String, Object> sGlobal,Map<String, Object> sConfig) {
		String sql = (String) paramarr.get("sql");
		Map<String,Object> result = new HashMap<String,Object>();
		Matcher m = Pattern.compile("\\[(\\d+)\\]").matcher(sql);
		if (m.find()) {
			int time = Common.intval(m.group(1));
			int temptime = (Integer) sGlobal.get("timestamp") - time;
			StringBuffer buffer = new StringBuffer();
			m.appendReplacement(buffer, temptime+"");
			m.appendTail(buffer);
			sql = buffer.toString();
		}
		paramarr.put("sql", sql);
		String sqlstring = "SELECT"+sql.trim().replace(";","").replaceAll("(?i)^(select)", "");
		if(Common.empty(paramarr.get("perpage"))) {
			result.put("count", 1);
			result.put("sql", sqlstring);
			result.put("multi", "");
			return result;
		}
		
		int listcount = 0;
		String countsql = "";
		if(Common.empty(countsql)) {
			countsql = getcountsql(sqlstring, "SELECT\\s(.+?)\\sFROM\\s(.+?)\\sWHERE\\s(.+?)\\sORDER", 2, 3);
		}
		if(Common.empty(countsql)) {
			countsql = getcountsql(sqlstring, "SELECT\\s(.+?)\\sFROM\\s(.+?)\\sWHERE\\s(.+?)\\sLIMIT", 2, 3);
		}
		if(Common.empty(countsql)) {
			countsql = getcountsql(sqlstring, "SELECT\\s(.+?)\\sFROM\\s(.+?)\\sWHERE\\s(.+?)$", 2, 3);
		}
		if(Common.empty(countsql)) {
			countsql = getcountsql(sqlstring, "SELECT\\s(.+?)\\sFROM\\s(.+?)\\sORDER", 2, -1);
		}
		if(Common.empty(countsql)) {
			countsql = getcountsql(sqlstring, "SELECT\\s(.+?)\\sFROM\\s(.+?)\\sLIMIT", 2, -1);
		}
		if(Common.empty(countsql)) {
			countsql = getcountsql(sqlstring, "SELECT\\s(.+?)\\sFROM\\s(.+?)$", 2, -1);
		}
		String multi  = "";
		if(!Common.empty(countsql)) {
			listcount = dataBaseService.findRows(countsql);
			if(listcount>0) {
				int page = Math.max(Common.intval(request.getParameter("page")),1);
				int perpage = Common.intval((String)paramarr.get("perpage"));
				int start = (page-1)*perpage;
				List<String> urlplus = new ArrayList<String>();
				Map<String,String[]> values = (Map<String,String[]>)request.getParameterMap();
				Set<String> keys = values.keySet();
				for(String key:keys) {
					if(!"page".equals(key)) urlplus.add(Common.urlEncode(key)+"="+Common.urlEncode(values.get(key)[0]));
				}
				String mpurl = request.getRequestURI().replace(".do", ".jsp")+(Common.empty(urlplus)?"":"?"+Common.implode(urlplus, "&"));
				if(start >= listcount) {
					page = listcount/perpage;
					start = (page-1)*perpage;;
				}
				int maxPage = (Integer) sConfig.get("maxpage");
				multi = Common.multi(request, listcount, perpage, page, maxPage, mpurl, null, null);
				sqlstring = sqlstring.replaceAll("(?is) LIMIT(.+?)$", "");
				sqlstring += " LIMIT "+start+","+paramarr.get("perpage");
			}
		}
		result.put("count", listcount);
		result.put("sql", sqlstring);
		result.put("multi", multi);
		return result;
	}
	private String getcountsql(String sqlstring, String rule, int tablename, int where) {
		Matcher m = Pattern.compile("(?i)"+rule).matcher(sqlstring);
		String countsql = "";
		if(m.find()){
			countsql = "SELECT COUNT(*) FROM "+m.group(tablename)+" WHERE "+(where<0 ? "1" : m.group(where));
		}
		return countsql;
	}
	@SuppressWarnings("unchecked")
	private Map<String,Object> block_get(String cachekey,Map<String,Object>SGLOBAL,Map<String,Object>SCONFIG) {
		Map<String,Object> caches = new HashMap<String,Object>();
		caches.put("mtime", 0);
		if("file".equals(SCONFIG.get("cachemode"))) {
			String cachefile = JavaCenterHome.jchRoot+"./data/block_cache/"+getcachedirname(cachekey, "/",SCONFIG)+cachekey+".data";
			File file = new File(cachefile);
			if(file.exists()) {
				String data = FileHelper.readFile(file);
				List<Map<String,Object>> blockarr = (List<Map<String,Object>>)Serializer.unserialize(data);
				Object multipage = blockarr.size()>0 ? blockarr.get(blockarr.size()-1).get("multipage"):"";
				if(!Common.empty(multipage)) {
					caches.put("multi", multipage);
					blockarr.remove(blockarr.size()-1);
				} else {
					caches.put("multi", "");
				}
				caches.put("values", blockarr);
				caches.put("mtime", (int)(file.lastModified()/1000));
			}
		} else {
			String thetable = JavaCenterHome.getTableName("cache"+getcachedirname(cachekey,"",SCONFIG));
			List<Map<String,Object>> resultList = dataBaseService.executeQuery("SELECT * FROM "+thetable+" WHERE cachekey = '"+cachekey+"'");
			if(resultList!=null){
				if(resultList.size()>0){
					Map<String,Object> result = resultList.get(0);
					List<Map<String,Object>> blockarr = (List<Map<String,Object>>)Serializer.unserialize((String)result.get("value"));
					Object multipage = blockarr.size()>0 ? blockarr.get(blockarr.size()-1).get("multipage"):"";
					if(!Common.empty(multipage)) {
						caches.put("multi", multipage);
						blockarr.remove(blockarr.size()-1);
					} else {
						caches.put("multi", "");
					}
					caches.put("values", blockarr);
					caches.put("mtime", result.get("mtime"));
				}
			}else{
				String basetable = JavaCenterHome.getTableName("cache");
				List<Map<String,Object>> creatablelist = dataBaseService.executeQuery("SHOW CREATE TABLE "+basetable);
				Map<String,Object> creattable = creatablelist.get(0);
				String sql = ((String) creattable.get("Create Table")).replace(basetable,thetable);
				dataBaseService.executeUpdate(sql);
			}
		}
		
		return caches;
	}
	private String getcachedirname(String cachekey, String ext,Map<String,Object>SCONFIG) {
		return Common.empty(SCONFIG.get("cachegrade"))?"":cachekey.substring(0, (Integer)SCONFIG.get("cachegrade"))+ext;
	}
	private void block_set(String cachekey, List<Map<String,Object>>blockarr,Map<String,Object>sGLOBAL,Map<String,Object>sConfig) {
		String blockvalue = Serializer.serialize(blockarr);
		if("file".equals(sConfig.get("cachemode"))){
			boolean dircheck = false;
			String cachedir = JavaCenterHome.jchRoot+"./data/block_cache/";
			File dirfile = new File(cachedir);
			if(!dirfile.isDirectory()){
				dirfile.mkdir();
			}
			cachedir += getcachedirname(cachekey,"/",sConfig);
			dirfile = new File(cachedir);
			if(!dirfile.isDirectory()){
				if(dirfile.mkdir()){
					dircheck = true;
				}
			}else{
				dircheck = true;
			}
			if(dircheck){
				String cachefile = cachedir+cachekey+".data";
				FileHelper.writeFile(cachefile, blockvalue.toString());
			}
		}else{
			String thetable = JavaCenterHome.getTableName("cache"+getcachedirname(cachekey,"",sConfig));
			dataBaseService.executeUpdate("REPLACE INTO "+thetable+" (cachekey, value, mtime) VALUES ('"+cachekey+"', '"+Common.sAddSlashes(blockvalue)+"', '"+sGLOBAL.get("timestamp")+"')");
		}
	}
}