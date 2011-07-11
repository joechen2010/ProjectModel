package cn.jcenterhome.dao;

import java.util.List;
import java.util.Map;

import cn.jcenterhome.vo.TableColumnsVO;
import cn.jcenterhome.vo.TableFieldVO;
import cn.jcenterhome.vo.TableStatusVO;


public interface DataBaseDao {
	
	public List<Map<String, Object>> executeQuery(String sql);

	
	public List<String> executeQuery(String sql, int columnIndex);

	
	public int executeUpdate(String sql);

	
	public Map<String, Object> execute(String sql);

	
	public int insert(String sql);

	
	public String findFirst(String sql, int columnIndex);

	
	public int findRows(String sql);

	
	public long findTableSize(String sql);

	
	public List<TableFieldVO> findTableFields(String tableName);

	
	public List<TableColumnsVO> findTableColumns(String tableName);

	
	public List<TableStatusVO> findTableStatus(String sql);

	
	@SuppressWarnings("unchecked")
	public Map sqldumptable(List<String> excepttables, String table, int startfrom, long currsize,
			long sizelimit, boolean complete, String version, int extendins, String sqlcompat,
			String dumpcharset, String sqlcharset, boolean usehex);
}