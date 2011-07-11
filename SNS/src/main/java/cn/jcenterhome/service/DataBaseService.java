package cn.jcenterhome.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.jcenterhome.dao.DataBaseDao;
import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.vo.TableColumnsVO;
import cn.jcenterhome.vo.TableFieldVO;
import cn.jcenterhome.vo.TableStatusVO;


public class DataBaseService {
	private DataBaseDao dataBaseDao = (DataBaseDao) BeanFactory.getBean("dataBaseDao");

	
	public List<Map<String, Object>> executeQuery(String sql) {
		return dataBaseDao.executeQuery(sql);
	}

	
	public List<String> executeQuery(String sql, int columnIndex) {
		return dataBaseDao.executeQuery(sql, columnIndex);
	}

	
	public int executeUpdate(String sql) {
		return dataBaseDao.executeUpdate(sql);
	}

	
	public Map<String, Object> execute(String sql) {
		return dataBaseDao.execute(sql);
	}

	
	public int insert(String sql) {
		return dataBaseDao.insert(sql);
	}

	
	public String findFirst(String sql, int columnIndex) {
		return dataBaseDao.findFirst(sql, columnIndex);
	}

	
	public int findRows(String sql) {
		return dataBaseDao.findRows(sql);
	}

	
	public long findTableSize(String sql) {
		return dataBaseDao.findTableSize(sql);
	}

	
	public List<TableFieldVO> findTableFields(String tableName) {
		return dataBaseDao.findTableFields(tableName);
	}

	
	public List<TableColumnsVO> findTableColumns(String tableName) {
		return dataBaseDao.findTableColumns(tableName);
	}

	
	public List<TableStatusVO> findTableStatus(String sql) {
		return dataBaseDao.findTableStatus(sql);
	}

	
	public String showVersion() {
		return dataBaseDao.findFirst("SELECT VERSION()", 1);
	}

	
	public String showBasedir() {
		return dataBaseDao.findFirst("SHOW VARIABLES LIKE 'basedir'", 2);
	}

	
	public String showCreateSql(String tableName) {
		return dataBaseDao.findFirst("SHOW CREATE TABLE " + tableName, 2);
	}

	
	public List<String> showTableNames(String prefix) {
		return dataBaseDao.executeQuery("SHOW TABLES LIKE '" + prefix + "%';", 1);
	}

	
	public int insertTable(String tableName, Map<String, Object> insertData, boolean returnId, boolean replace) {
		String comma = "";
		StringBuffer insertKey = new StringBuffer();
		StringBuffer insertValue = new StringBuffer();
		for (Entry<String, Object> entry : insertData.entrySet()) {
			insertKey.append(comma + "`" + entry.getKey() + "`");
			insertValue.append(comma + "'" + entry.getValue() + "'");
			comma = ", ";
		}
		String sql = (replace ? "REPLACE" : "INSERT") + " INTO " + JavaCenterHome.getTableName(tableName)
				+ " (" + insertKey + ") VALUES (" + insertValue + ")";
		if (returnId && !replace) {
			return dataBaseDao.insert(sql);
		} else {
			dataBaseDao.executeUpdate(sql);
			return 0;
		}
	}

	
	public void updateTable(String tableName, Map<String, Object> setData, Map<String, Object> whereData) {
		String comma = "";
		StringBuffer setSQL = new StringBuffer();
		for (Entry<String, Object> entry : setData.entrySet()) {
			setSQL.append(comma + "`" + entry.getKey() + "`='" + entry.getValue() + "'");
			comma = ", ";
		}
		comma = "";
		StringBuffer whereSQL = new StringBuffer();
		if (whereData == null || whereData.isEmpty()) {
			whereSQL.append("1");
		} else {
			for (Entry<String, Object> entry : whereData.entrySet()) {
				whereSQL.append(comma + "`" + entry.getKey() + "`='" + entry.getValue() + "'");
				comma = " AND ";
			}
		}
		dataBaseDao.executeUpdate("UPDATE " + JavaCenterHome.getTableName(tableName) + " SET " + setSQL
				+ " WHERE " + whereSQL);
	}

	
	@SuppressWarnings("unchecked")
	public Map sqldumptable(List<String> excepttables, String table, int startfrom, long currsize,
			long sizelimit, boolean complete, String version, int extendins, String sqlcompat,
			String dumpcharset, String sqlcharset, boolean usehex) {
		return dataBaseDao.sqldumptable(excepttables, table, startfrom, currsize, sizelimit, complete,
				version, extendins, sqlcompat, dumpcharset, sqlcharset, usehex);
	}
}