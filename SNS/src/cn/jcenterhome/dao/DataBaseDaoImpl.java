
package cn.jcenterhome.dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.SessionFactory;
import cn.jcenterhome.vo.FieldVO;
import cn.jcenterhome.vo.TableColumnsVO;
import cn.jcenterhome.vo.TableFieldVO;
import cn.jcenterhome.vo.TableStatusVO;


public class DataBaseDaoImpl implements DataBaseDao {
	
	public List<Map<String, Object>> executeQuery(String sql) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			List<FieldVO> fields = new ArrayList<FieldVO>(columnCount);
			FieldVO field = null;
			for (int i = 1; i <= columnCount; i++) {
				field = new FieldVO();
				field.setName(rsmd.getColumnLabel(i));
				field.setInt(rsmd.getColumnTypeName(i).contains("INT"));
				fields.add(field);
			}
			rsmd = null;
			Map<String, Object> row = null;
			while (rs.next()) {
				row = new HashMap<String, Object>(columnCount);
				for (FieldVO obj : fields) {
					if (obj.isInt()) {
						row.put(obj.getName(), rs.getInt(obj.getName()));
					} else {
						row.put(obj.getName(), rs.getString(obj.getName()));
					}
				}
				rows.add(row);
			}
			tran.commit();
		} catch (SQLException e) {
			tran.rollback();
			e.printStackTrace();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rows;
	}

	
	public List<Map<String, Object>> executeQueryByBlock(String sql) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			List<FieldVO> fields = new ArrayList<FieldVO>(columnCount);
			FieldVO field = null;
			for (int i = 1; i <= columnCount; i++) {
				field = new FieldVO();
				field.setName(rsmd.getColumnLabel(i));
				field.setInt(rsmd.getColumnTypeName(i).contains("INT"));
				fields.add(field);
			}
			rsmd = null;
			Map<String, Object> row = null;
			while (rs.next()) {
				row = new HashMap<String, Object>(columnCount);
				List<String> columnNames = new ArrayList<String>();
				for (FieldVO obj : fields) {
					if (obj.isInt()) {
						row.put(obj.getName(), rs.getInt(obj.getName()));
					} else {
						row.put(obj.getName(), rs.getString(obj.getName()));
					}
					columnNames.add(obj.getName());
				}
				row.put("columnname", columnNames);
				rows.add(row);
			}
			tran.commit();
		} catch (SQLException e) {
			tran.rollback();
			e.printStackTrace();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rows;
	}

	
	public List<String> executeQuery(String sql, int columnIndex) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		List<String> rows = new ArrayList<String>();
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				rows.add(rs.getString(columnIndex));
			}
			tran.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			tran.rollback();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rows;
	}

	
	public int executeUpdate(String sql) {
		Connection conn = null;
		Statement stmt = null;
		int rows = 0;
		Transaction tran = null;
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			stmt = conn.createStatement();
			stmt.setEscapeProcessing(false);
			rows = stmt.executeUpdate(sql);
			tran.commit();
		} catch (SQLException e) {
			tran.rollback();
			e.printStackTrace();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rows;
	}

	
	public Map<String, Object> execute(String sql) {
		Session session = null;
		Connection conn = null;
		Transaction tran = null;
		Map<String, Object> infos = new HashMap<String, Object>();
		Statement pstmt = null;
		try {
			session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.createStatement();
			pstmt.setEscapeProcessing(false);
			boolean result = pstmt.execute(sql);
			int num = 0;
			if (result) {
				ResultSet rs = pstmt.executeQuery(sql);
				if (rs.last()) {
					num = rs.getRow();
				}
			} else {
				num = pstmt.getUpdateCount();
			}
			infos.put("sucess", num);
			tran.commit();
		} catch (SQLException e) {
			tran.rollback();
			infos.put("error", e.getMessage());
			infos.put("errorCode", e.getErrorCode());
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				infos.put("error", e.getMessage());
				infos.put("errorCode", e.getErrorCode());
			}
		}
		return infos;
	}

	
	public int insert(String sql) {
		int id = 0;
		Connection conn = null;
		Statement stmt = null;
		Transaction tran = null;
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			stmt = conn.createStatement();
			stmt.setEscapeProcessing(false);
			int rows = stmt.executeUpdate(sql);
			if (rows > 0) {
				ResultSet rs = stmt.executeQuery("SELECT last_insert_id()");
				if (rs.next()) {
					id = rs.getInt(1);
				}
				rs.close();
				rs = null;
			}
			tran.commit();
		} catch (SQLException e) {
			tran.rollback();
			e.printStackTrace();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}

	
	public String findFirst(String sql, int columnIndex) {
		String result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getString(columnIndex);
			}
			tran.commit();
		} catch (SQLException e) {
			tran.rollback();
			e.printStackTrace();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	
	public int findRows(String sql) {
		int line = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				line = rs.getInt(1);
			}
			tran.commit();
		} catch (SQLException e) {
			tran.rollback();
			e.printStackTrace();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return line;
	}

	
	public long findTableSize(String sql) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		long dataSize = 0;
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				dataSize += rs.getLong("Data_length");
				dataSize += rs.getLong("Index_length");
			}
			tran.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (tran != null) {
				tran.rollback();
			}
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dataSize;
	}

	
	public List<TableFieldVO> findTableFields(String tableName) {
		List<TableFieldVO> fieldVOs = new ArrayList<TableFieldVO>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement("SHOW FIELDS FROM " + tableName);
			rs = pstmt.executeQuery();
			TableFieldVO fieldVO = null;
			while (rs.next()) {
				fieldVO = new TableFieldVO();
				fieldVO.setField(rs.getString("Field"));
				fieldVO.setType(rs.getString("Type"));
				fieldVO.setAllowNull(rs.getString("Null"));
				fieldVO.setKey(rs.getString("Key"));
				fieldVO.setDefaultValue(rs.getString("Default"));
				fieldVO.setExtra(rs.getString("Extra"));
				fieldVOs.add(fieldVO);
			}
			tran.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			tran.rollback();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return fieldVOs;
	}

	
	public List<TableColumnsVO> findTableColumns(String tableName) {
		List<TableColumnsVO> fullColumnsVOs = new ArrayList<TableColumnsVO>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement("SHOW FULL COLUMNS FROM " + tableName);
			rs = pstmt.executeQuery();
			TableColumnsVO fullColumnsVO = null;
			while (rs.next()) {
				fullColumnsVO = new TableColumnsVO();
				fullColumnsVO.setField(rs.getString("Field"));
				fullColumnsVO.setType(rs.getString("Type"));
				fullColumnsVO.setCollation(rs.getString("Collation"));
				fullColumnsVO.setAllowNull(rs.getString("Null"));
				fullColumnsVO.setKey(rs.getString("Key"));
				fullColumnsVO.setDefaultValue(rs.getString("Default"));
				fullColumnsVO.setExtra(rs.getString("Extra"));
				fullColumnsVO.setPrivileges(rs.getString("Privileges"));
				fullColumnsVO.setComment(rs.getString("Comment"));
				fullColumnsVOs.add(fullColumnsVO);
			}
			tran.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			tran.rollback();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return fullColumnsVOs;
	}

	
	public List<TableStatusVO> findTableStatus(String sql) {
		List<TableStatusVO> tableStatusVOs = new ArrayList<TableStatusVO>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction tran = null;
		try {
			Session session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			TableStatusVO tableStatusVO = null;
			while (rs.next()) {
				tableStatusVO = new TableStatusVO();
				tableStatusVO.setName(rs.getString("Name"));
				tableStatusVO.setEngine(rs.getString("Engine"));
				tableStatusVO.setRows(rs.getLong("Rows"));
				tableStatusVO.setData_length(rs.getLong("Data_length"));
				tableStatusVO.setIndex_length(rs.getLong("Index_length"));
				tableStatusVO.setData_free(rs.getLong("Data_free"));
				tableStatusVO.setAuto_increment(rs.getString("Auto_increment"));
				tableStatusVO.setCollation(rs.getString("Collation"));
				tableStatusVOs.add(tableStatusVO);
			}
			tran.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			tran.rollback();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tableStatusVOs;
	}

	
	public Map sqldumptable(List<String> excepttables, String table, int startfrom, long currsize,
			long sizelimit, boolean complete, String version, int extendins, String sqlcompat,
			String dumpcharset, String sqlcharset, boolean usehex) {
		int offset = 300;
		Map map = new HashMap();
		StringBuffer tabledump = new StringBuffer();
		if (table.contains("adminsessions")) {
			map.put("tabledump", tabledump);
			return map;
		}
		List<TableFieldVO> fieldStatusVOs = findTableFieldStatus(table);
		if (fieldStatusVOs == null || fieldStatusVOs.size() <= 0) {
			map.put("tabledump", tabledump);
			return map;
		}
		if (startfrom == 0) {
			String createtable = findFirst("SHOW CREATE TABLE " + table, 2).replaceAll("`", "");
			if (createtable != null && createtable.length() > 0) {
				tabledump.append("DROP TABLE IF EXISTS " + table + ";\n");
			} else {
				map.put("tabledump", tabledump);
				return map;
			}
			tabledump.append(table.indexOf(".") != -1 ? createtable : createtable.replaceFirst(
					"CREATE TABLE " + table, "CREATE TABLE " + table.substring(table.indexOf(".") + 1)));

			TableStatusVO tableStatusVO = this.findTableStatus("SHOW TABLE STATUS LIKE '" + table + "';")
					.get(0);
			if (sqlcompat.equals("MYSQL41") && version.compareTo("4.1") < 0) {
				tabledump.replace(0, tabledump.length(), tabledump.toString().replaceFirst("TYPE\\=(.+)",
						"ENGINE=" + tableStatusVO.getEngine() + " DEFAULT CHARSET=" + dumpcharset));
			} else if (sqlcompat.equals("MYSQL40") && version.compareTo("4.1") >= 0
					&& version.compareTo("5.1") < 0) {
				tabledump.replace(0, tabledump.length(), tabledump.toString().replaceFirst("ENGINE\\=(.+)",
						"TYPE=" + tableStatusVO.getEngine()));
			} else if (version.compareTo("4.1") > 0 && sqlcharset.length() > 0) {
				tabledump.replace(0, tabledump.length(), tabledump.toString().replaceFirst(
						"(DEFAULT)*\\s*CHARSET=.+", "DEFAULT CHARSET=" + sqlcharset));
			}
			tabledump.append((tableStatusVO.getAuto_increment() != null
					&& !"".equals(tableStatusVO.getAuto_increment()) ? " AUTO_INCREMENT="
					+ tableStatusVO.getAuto_increment() : "")
					+ ";\n\n");
			if (sqlcompat.equals("MYSQL40") && version.compareTo("4.1") >= 0 && version.compareTo("5.1") < 0) {
				if (tableStatusVO.getAuto_increment() != null
						&& !"".equals(tableStatusVO.getAuto_increment())) {
					tabledump.insert(tabledump.indexOf(","), " auto_increment");
				}
				if ("MEMORY".equals(tableStatusVO.getEngine())) {
					int index = tabledump.indexOf("TYPE=MEMORY");
					if (index > 0) {
						tabledump.replace(index, "TYPE=MEMORY".length() + index, "TYPE=HEAP");
					}
				}
			}
		}
		if (!excepttables.contains(table)) {
			int tabledumped = 0;
			int numrows = offset;
			TableFieldVO firstfield = fieldStatusVOs.get(0);
			if (extendins == 0) {
				while ((currsize + tabledump.length()) < sizelimit && numrows == offset && complete) {
					String selectsql = null;
					if ("auto_increment".equals(firstfield.getExtra())) {
						selectsql = "SELECT * FROM " + table + " WHERE " + firstfield.getField() + " > "
								+ startfrom + " LIMIT " + offset + ";";
					} else {
						selectsql = "SELECT * FROM " + table + " LIMIT " + startfrom + ", " + offset + ";";
					}
					tabledumped = 1;
					List<Map<String, Object>> rows = this.executeQuery(selectsql);
					if (rows != null) {
						numrows = rows.size();
						StringBuffer t = null;
						for (Map<String, Object> row : rows) {
							t = new StringBuffer();
							for (TableFieldVO fieldStatusVO : fieldStatusVOs) {
								String type = fieldStatusVO.getType();
								String value = row.get(fieldStatusVO.getField()).toString();
								if (value == null) {
									if ("date".equals(type)) {
										value = "0000-00-00";
									} else {
										value = "";
									}
								}
								t.append(","
										+ (usehex && !value.equals("")
												&& (type.contains("char") || type.contains("text")) ? "0x"
												+ bin2hex(value, JavaCenterHome.JCH_CHARSET) : "\'"
												+ mysqlEscapeString(value) + "\'"));
							}
							if ((t.length() + currsize + tabledump.length()) < sizelimit) {
								if ("auto_increment".equals(firstfield.getExtra())) {
									startfrom = Integer.valueOf(row.get(firstfield.getField()).toString());
								} else {
									startfrom++;
								}
								if (t.length() > 0) {
									t.deleteCharAt(0);
									tabledump.append("INSERT INTO " + table + " VALUES (" + t + ");\n");
								}
							} else {
								complete = false;
								break;
							}
						}
					} else {
						break;
					}
				}
			} else {
				while (currsize + tabledump.length() < sizelimit && numrows == offset && complete) {
					String selectsql = null;
					if ("auto_increment".equals(firstfield.getExtra())) {
						selectsql = "SELECT * FROM " + table + " WHERE " + firstfield.getField() + " > "
								+ startfrom + " LIMIT " + offset + ";";
					} else {
						selectsql = "SELECT * FROM " + table + " LIMIT " + startfrom + ", " + offset + ";";
					}
					tabledumped = 1;
					List<Map<String, Object>> rows = this.executeQuery(selectsql);
					if (rows != null) {
						numrows = rows.size();
						StringBuffer t1 = new StringBuffer();
						for (Map<String, Object> row : rows) {
							StringBuffer t2 = new StringBuffer();
							for (TableFieldVO fieldStatusVO : fieldStatusVOs) {
								String type = fieldStatusVO.getType();
								String value = row.get(fieldStatusVO.getField()).toString();
								if (value == null) {
									if ("date".equals(type)) {
										value = "0000-00-00";
									} else {
										value = "";
									}
								}
								t2.append(","
										+ (usehex && !value.equals("")
												&& (type.contains("char") || type.contains("text")) ? "0x"
												+ bin2hex(value, JavaCenterHome.JCH_CHARSET) : "\'"
												+ mysqlEscapeString(value) + "\'"));
							}
							if (t1.length() + currsize + tabledump.length() < sizelimit) {
								if ("auto_increment".equals(firstfield.getExtra())) {
									startfrom = Integer.valueOf(row.get(firstfield.getField()).toString());
								} else {
									startfrom++;
								}
								if (t2.length() > 0) {
									t2.deleteCharAt(0);
									t1.append(",(" + t2 + ")");
								}
							} else {
								complete = false;
								break;
							}
						}
						if (t1.length() > 0) {
							t1.deleteCharAt(0);
							tabledump.append("INSERT INTO " + table + " VALUES " + t1 + ";\n");
						}
					} else {
						break;
					}
				}
			}
			tabledump.append("\n");
			map.put("startfrom", startfrom);
			map.put("complete", complete);
		}
		map.put("tabledump", tabledump);
		return map;
	}

	private List<TableFieldVO> findTableFieldStatus(String tableName) {
		List<TableFieldVO> fieldStatusVOs = new ArrayList<TableFieldVO>();
		String sql = "SHOW FULL COLUMNS FROM " + tableName;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Transaction transaction = null;
		try {
			Session session = SessionFactory.getSession();
			transaction = session.beginTransaction();
			conn = session.connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			TableFieldVO fieldStatusVO = null;
			while (rs.next()) {
				fieldStatusVO = new TableFieldVO();
				fieldStatusVO.setField(rs.getString("Field"));
				fieldStatusVO.setType(rs.getString("Type"));
				fieldStatusVO.setAllowNull(rs.getString("Null"));
				fieldStatusVO.setKey(rs.getString("Key"));
				fieldStatusVO.setDefaultValue(rs.getString("Default"));
				fieldStatusVO.setExtra(rs.getString("Extra"));
				fieldStatusVOs.add(fieldStatusVO);
			}
			transaction.commit();
		} catch (Exception exception) {
			exception.printStackTrace();
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			try {
				if (transaction != null) {
					transaction = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return fieldStatusVOs;
	}

	private static String bin2hex(String s, String charset) {
		char[] digital = "0123456789abcdef".toCharArray();
		StringBuffer sb = new StringBuffer();
		byte[] bs = null;
		try {
			bs = s.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(digital[bit]);
			bit = bs[i] & 0x0f;
			sb.append(digital[bit]);
		}
		return sb.toString();
	}

	private static String mysqlEscapeString(String text) {
		StringBuffer sb = new StringBuffer(text.length() * 2);
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			switch (character) {
				case '"':
					sb.append("\\\"");
					break;
				case '\'':
					sb.append("\\\'");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\n':
					sb.append("\\n");
					break;
				default:
					sb.append(character);
					break;
			}
			character = iterator.next();
		}
		return sb.toString();
	}
}