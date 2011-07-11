package cn.jcenterhome.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import cn.jcenterhome.dao.DataBaseDao;
import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;

public class CacheService {
	private DataBaseDao dataBaseDao = (DataBaseDao) BeanFactory.getBean("dataBaseDao");
	private String[] cacheNames = {"config", "network", "usergroup", "profilefield", "profield", "censor",
			"creditrule", "ad", "task", "click", "block", "userapp", "app", "eventclass", "magic"};

	
	public void updateCache() throws Exception {
		try {
			for (String cacheName : cacheNames) {
				Method method = this.getClass().getMethod(cacheName + "_cache");
				method.invoke(this);
			}
		} catch (Exception e) {
			String message = e.getMessage();
			if (message == null) {
				message = e.getCause().getMessage();
			}
			throw new Exception("All the cache update fails£º<br>" + message);
		}
	}

	
	public void config_cache() throws IOException {
		config_cache(true);
	}

	
	public void config_cache(boolean updateData) throws IOException {
		Map<String, Object> sConfig = new HashMap<String, Object>();
		List<Map<String, Object>> configs = dataBaseDao.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("config"));
		for (Map<String, Object> value : configs) {
			String var = (String) value.get("var");
			Object val = value.get("datavalue");
			if ("privacy".equals(var)) {
				val = Common.empty(val) ? val : Serializer.unserialize((String) val, false);
			}
			sConfig.put(var, val);
		}
		cache_write("cache_config", "sConfig", sConfig);
		if (updateData) {
			String setting = Common.getData("setting");
			cache_write("cache_setting", "globalSetting", Serializer.unserialize(setting, false));

			String mail = Common.getData("mail");
			cache_write("cache_mail", "globalMail", Serializer.unserialize(mail, false));

			String spam = Common.getData("spam");
			cache_write("cache_spam", "globalSpam", Serializer.unserialize(spam, false));
		}
	}

	
	public void network_cache() throws IOException {
		String network = Common.getData("network");
		cache_write("cache_network", "globalNetWork", Serializer.unserialize(network, false));
	}

	
	public void usergroup_cache() throws IOException {
		Map<Integer, Map<String, String>> groupTitles = new LinkedHashMap<Integer, Map<String, String>>();
		boolean highest = true;
		int lower = 999999999;
		List<Map<String, Object>> usergroups = dataBaseDao.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("usergroup") + " ORDER BY explower DESC");
		for (Map<String, Object> usergroup : usergroups) {
			Integer gid = (Integer) usergroup.get("gid");
			usergroup.put("maxattachsize", ((Integer) usergroup.get("maxattachsize")) * 1024 * 1024);
			if ((Integer) usergroup.get("system") == 0) {
				if (highest) {
					usergroup.put("exphigher", lower);
					highest = false;
				} else {
					usergroup.put("exphigher", lower - 1);
				}
				lower = (Integer) usergroup.get("explower");
			}
			String magicaward = (String) usergroup.get("magicaward");
			if (magicaward.length() > 0) {
				usergroup.put("magicaward", Serializer.unserialize((String) usergroup.get("magicaward"),
						false));
			}
			Map<String, String> groupTitle = new LinkedHashMap<String, String>();
			groupTitle.put("grouptitle", (String) usergroup.get("grouptitle"));
			groupTitle.put("color", (String) usergroup.get("color"));
			groupTitle.put("icon", (String) usergroup.get("icon"));
			groupTitles.put(gid, groupTitle);
			cache_write("usergroup_" + gid, "usergroup" + gid, usergroup);
		}
		cache_write("usergroup", "globalGroupTitle", groupTitles);
	}

	
	public void profilefield_cache() throws IOException {
		Map<Integer, Map<String, Object>> datas = new LinkedHashMap<Integer, Map<String, Object>>();
		List<Map<String, Object>> profilefields = dataBaseDao
				.executeQuery("SELECT fieldid, title, formtype, maxsize, required, invisible, allowsearch, choice FROM "
						+ JavaCenterHome.getTableName("profilefield") + " ORDER BY displayorder");
		for (Map<String, Object> value : profilefields) {
			datas.put((Integer) value.get("fieldid"), value);
		}
		cache_write("cache_profilefield", "globalProfilefield", datas);
	}

	
	public void profield_cache() throws IOException {
		Map<Integer, Map<String, Object>> datas = new LinkedHashMap<Integer, Map<String, Object>>();
		List<Map<String, Object>> profields = dataBaseDao
				.executeQuery("SELECT fieldid, title, formtype, inputnum, mtagminnum, manualmoderator, manualmember FROM "
						+ JavaCenterHome.getTableName("profield") + " ORDER BY displayorder");
		for (Map<String, Object> value : profields) {
			datas.put((Integer) value.get("fieldid"), value);
		}
		cache_write("cache_profield", "globalProfield", datas);
	}

	
	public void censor_cache() throws IOException {

		List<String> banned = new ArrayList<String>();
		List<String> banwords = new ArrayList<String>();

		Map<String, Object> globalCensor = new HashMap<String, Object>();
		String censorDatavalue = (String) Common.getData("censor");
		if (censorDatavalue != null) {
			String[] censorarr = censorDatavalue.split("\n");
			String[] tempArray;
			String find = null;
			String replace = null;
			String findword;
			Pattern pattern = Pattern.compile("\\\\\\{(\\d+)\\\\\\}");
			if (censorarr != null) {
				StringBuilder builder = new StringBuilder("(?i)");
				int builderPrimalLen = builder.length();
				for (String censor : censorarr) {
					censor = censor.trim();
					if (Common.empty(censor)) {
						continue;
					}

					tempArray = censor.split("=");
					if (tempArray.length > 1) {
						find = tempArray[0];
						replace = tempArray[1];
					} else {
						continue;
					}
					if (find.equals("")) {
						continue;
					}
					findword = find;
					find = pattern.matcher(Common.pregQuote(find, '/')).replaceAll(".{0,\\\\1}");
					if ("{BANNED}".equals(replace)) {
						banwords.add(pattern.matcher(Common.pregQuote(findword, '/')).replaceAll("*"));
						banned.add(find);
					} else {
						Map<String, LinkedHashMap<Integer, String>> filterValue = (Map<String, LinkedHashMap<Integer, String>>) globalCensor
								.get("filter");
						if (filterValue == null) {
							filterValue = new HashMap<String, LinkedHashMap<Integer, String>>();
							globalCensor.put("filter", filterValue);
						}
						LinkedHashMap<Integer, String> findValue = filterValue.get("find");
						if (findValue == null) {
							findValue = new LinkedHashMap<Integer, String>();
							filterValue.put("find", findValue);
						}
						findValue.put(findValue.size(), builder.append(find).toString());
						builder.delete(builderPrimalLen, builder.length());

						LinkedHashMap<Integer, String> replaceValue = filterValue.get("replace");
						if (replaceValue == null) {
							replaceValue = new LinkedHashMap<Integer, String>();
							filterValue.put("replace", replaceValue);
						}
						replaceValue.put(replaceValue.size(), replace);
					}
				}
			}
		}

		if (banned.size() > 0) {
			globalCensor.put("banned", "(?i)(" + Common.implode(banned, "|") + ")");
			globalCensor.put("banword", Common.implode(banwords, ", "));
		}

		cache_write("cache_censor", "globalCensor", globalCensor);
	}

	
	public void creditrule_cache() throws Exception {
		Map<String, Map<String, Object>> datas = new LinkedHashMap<String, Map<String, Object>>();
		List<Map<String, Object>> creditrules = dataBaseDao.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("creditrule") + "");
		for (Map<String, Object> value : creditrules) {
			datas.put((String) value.get("action"), value);
		}
		cache_write("cache_creditrule", "globalCreditrule", datas);
	}

	
	public void ad_cache() throws Exception {
		Map<String, TreeMap<Integer, Object>> datas = new LinkedHashMap<String, TreeMap<Integer, Object>>();
		List<Map<String, Object>> ads = dataBaseDao.executeQuery("SELECT adid, pagetype FROM "
				+ JavaCenterHome.getTableName("ad") + " WHERE system='1' AND available='1'");
		TreeMap<Integer, Object> tempMap = null;
		for (Map<String, Object> value : ads) {
			tempMap = datas.get((String) value.get("pagetype"));
			if (tempMap == null) {
				tempMap = new TreeMap<Integer, Object>();
			}
			tempMap.put(tempMap.size(), value.get("adid"));
			datas.put((String) value.get("pagetype"), tempMap);
		}
		cache_write("cache_ad", "globalAd", datas);
	}

	
	public void task_cache() throws Exception {
		int timestamp = (int) (System.currentTimeMillis() / 1000);
		Map<Integer, Map<String, Object>> datas = new LinkedHashMap<Integer, Map<String, Object>>();
		List<Map<String, Object>> tasks = dataBaseDao.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("task") + " WHERE available='1' ORDER BY displayorder");
		for (Map<String, Object> value : tasks) {
			int endtime = (Integer) value.get("endtime");
			int maxnum = (Integer) value.get("maxnum");
			int num = (Integer) value.get("num");
			if ((endtime == 0 || endtime >= timestamp) && (maxnum == 0 || maxnum > num)) {
				datas.put((Integer) value.get("taskid"), value);
			}
		}
		cache_write("cache_task", "globalTask", datas);
	}

	
	public void click_cache() throws Exception {
		Map<String, Map<Integer, Map<String, Object>>> datas = new LinkedHashMap<String, Map<Integer, Map<String, Object>>>();
		List<Map<String, Object>> clicks = dataBaseDao.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("click") + " ORDER BY displayorder");
		for (Map<String, Object> value : clicks) {
			String idtype = (String) value.get("idtype");
			Map<Integer, Map<String, Object>> idtypes = datas.get(idtype);
			if (idtypes == null) {
				idtypes = new LinkedHashMap<Integer, Map<String, Object>>();
				datas.put(idtype, idtypes);
			}
			idtypes.put((Integer) value.get("clickid"), value);
		}
		cache_write("cache_click", "globalClick", datas);
	}

	
	public void block_cache() throws Exception {
		Map<Integer, Integer> datas = new LinkedHashMap<Integer, Integer>();
		List<Map<String, Object>> blocks = dataBaseDao.executeQuery("SELECT bid, cachetime FROM "
				+ JavaCenterHome.getTableName("block"));
		for (Map<String, Object> value : blocks) {
			datas.put((Integer) value.get("bid"), (Integer) value.get("cachetime"));
		}
		cache_write("cache_block", "globalBlock", datas);
	}

	
	public void userapp_cache() throws IOException {
		byte my_status = 1;
		Map<Integer, Map<String, Object>> datas = new LinkedHashMap<Integer, Map<String, Object>>();
		if (my_status > 0) {
			List<Map<String, Object>> userapps = dataBaseDao.executeQuery("SELECT * FROM "
					+ JavaCenterHome.getTableName("myapp") + " WHERE flag='1' ORDER BY displayorder");
			for (Map<String, Object> value : userapps) {
				datas.put((Integer) value.get("appid"), value);
			}
		}
		cache_write("cache_userapp", "globalUserApp", datas);
	}

	
	public void app_cache() throws IOException {
		Map<String, Object> data = new HashMap<String, Object>();
		cache_write("cache_app", "globalApp", data);
	}

	
	public void eventclass_cache() throws IOException {
		Map<Integer, Map<String, Object>> datas = new LinkedHashMap<Integer, Map<String, Object>>();
		List<Map<String, Object>> eventclasses = dataBaseDao.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("eventclass") + " ORDER BY displayorder");
		for (Map<String, Object> value : eventclasses) {
			Integer classid = (Integer) value.get("classid");
			Integer poster = (Integer) value.get("poster");
			if (poster > 0) {
				value.put("poster", "data/event/" + classid + ".jpg");
			} else {
				value.put("poster", "image/event/default.jpg");
			}
			datas.put(classid, value);
		}
		cache_write("cache_eventclass", "globalEventClass", datas);
	}

	
	public void magic_cache() throws Exception {
		Map<Object, Object> magic = new LinkedHashMap<Object, Object>();
		List<Map<String, Object>> magics = dataBaseDao.executeQuery("SELECT mid, name FROM "
				+ JavaCenterHome.getTableName("magic") + " WHERE close='0'");
		for (Map<String, Object> value : magics) {
			magic.put(value.get("mid"), value.get("name"));
		}
		cache_write("cache_magic", "globalMagic", magic);
	}

	
	public void delTreeDir(String dir) {
		File[] subFiles = Common.readDir(dir);
		if (subFiles != null) {
			for (File subFile : subFiles) {
				if (subFile.isFile()) {
					subFile.delete();
				} else {
					delTreeDir(subFile.getPath());
				}
			}
		}
	}

	
	public void block_data_cache(Map<String, Object> sConfig) {
		if ("database".equals(sConfig.get("cachemode"))) {
			List<String> query = dataBaseDao.executeQuery("SHOW TABLES LIKE '"
					+ JavaCenterHome.jchConfig.get("tablePre") + "cache%';", 1);
			for (String table : query) {
				dataBaseDao.execute("TRUNCATE TABLE `" + table + "`");
			}
		} else {
			delTreeDir(JavaCenterHome.jchRoot + "data/block_cache");
		}
	}
	@SuppressWarnings("unchecked")
	private String arrayeval(String var, Map values, int level) {
		StringBuffer space = new StringBuffer();
		for (int i = 0; i < level; i++) {
			space.append("\t");
		}
		StringBuffer curdata = new StringBuffer();
		if (values instanceof LinkedHashMap) {
			curdata.append("Map " + var + "= new LinkedHashMap();\n");
		} else {
			curdata.append("Map " + var + "= new HashMap();\n");
		}
		Object keyTemp = null;
		String valTemp = null;
		Set<Object> keys = values.keySet();
		for (Object key : keys) {
			Object val = values.get(key);
			if (key instanceof String) {
				keyTemp = "\"" + key + "\"";
			} else {
				keyTemp = key;
			}
			boolean isMap = val instanceof Map;
			if (isMap) {
				String temp = key.toString();
				String varTemp = var + temp.substring(0, 1).toUpperCase() + temp.substring(1);
				curdata.append(space);
				curdata.append("\t");
				curdata.append(arrayeval(varTemp, (Map) val, level + 1));
				curdata.append(space);
				curdata.append(var + ".put(" + keyTemp + "," + varTemp + ");\n");
			} else {
				valTemp = val.toString();
				if (!valTemp.matches("^\\-?\\d+$") || Common.strlen(valTemp) > 12) {
					valTemp = Common.addSlashes(valTemp);
					valTemp = valTemp.replaceAll("\r\n", "\\\\r\\\\n");
					val = "\"" + valTemp + "\"";
				}
				curdata.append(space);
				curdata.append(var + ".put(" + keyTemp + "," + val + ");\n");
			}
		}
		return curdata.toString();
	}
	@SuppressWarnings("unchecked")
	private void cache_write(String fileName, String var, Map values) throws IOException {
		String cachePath = JavaCenterHome.jchRoot + "data/cache/" + fileName + ".jsp";
		StringBuffer cacheContent = new StringBuffer();
		cacheContent.append("<%@ page language=\"java\" import=\"java.util.*\" pageEncoding=\"");
		cacheContent.append(JavaCenterHome.JCH_CHARSET);
		cacheContent.append("\"%>\n");
		cacheContent.append("<%\n");
		cacheContent.append(arrayeval(var, values, 0));
		cacheContent.append("request.setAttribute(\"" + var + "\"," + var + ");\n");
		cacheContent.append("%>");
		if (!FileHelper.writeFile(cachePath, cacheContent.toString())) {
			throw new IOException("File: " + cachePath + " write error.");
		}
	}
}