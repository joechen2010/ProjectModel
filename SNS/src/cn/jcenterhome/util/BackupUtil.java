package cn.jcenterhome.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
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


public class BackupUtil {

	private String sqlCompat;
	private String dumpCharset;
	private String version;
	private boolean useHex;
	private boolean useExtend;
	private int volumeSize = 2048 * 1000;
	private final int n = 300;

	
	public BackupUtil(String sqlCompat, String dumpCharset, int volumeSize, boolean useHex, boolean useExtend) {
		this.useHex = useHex;
		this.useExtend = useExtend;
		this.sqlCompat = sqlCompat != null && sqlCompat.length() > 0 ? sqlCompat : "";
		this.dumpCharset = dumpCharset != null && dumpCharset.length() > 0 ? dumpCharset : null;
		if (volumeSize > 0) {
			this.volumeSize = volumeSize;
		}
		version = fetchRow("SELECT VERSION()").get("VERSION()");
	}

	
	public BackupInfo dump(StringBuffer container, String table, int offset, boolean dumpStruct) {
		BackupInfo ret = new BackupInfo();
		container = container == null ? new StringBuffer() : container;

		StringBuffer tableStru = new StringBuffer();
		StringBuffer tableData = new StringBuffer();

		
		if (offset == 0 && dumpStruct) {
			Map<String, String> tableCreate = null;
			Map<String, String> tableStatus = null;
			try {
				tableCreate = fetchRow("SHOW CREATE TABLE " + table);
				tableStatus = fetchRow("SHOW TABLE STATUS LIKE '" + table + "'");
			} catch (Exception e) {
				return ret;
			}
			tableStru.append("DROP TABLE IF EXISTS " + table + ";\n");
			if ("MYSQL41".equals(sqlCompat) && version.compareTo("4.1") < 0 && dumpCharset != null) {
				tableStru.append(tableCreate.get("Create Table").replaceAll("TYPE=(.+)",
						"ENGINE=$1 DEFAULT CHARSET=" + dumpCharset));
			} else if (version.compareTo("4.1") > 0 && dumpCharset != null) {
				tableStru.append(tableCreate.get("Create Table").replaceAll("(DEFAULT)*\\s*CHARSET=.+",
						"DEFAULT CHARSET=" + dumpCharset));
			} else {
				tableStru.append(tableCreate.get("Create Table"));
			}
			if (tableStatus.get("Auto_increment") == null) {
				tableStru.append(";");
			} else {
				tableStru.append(" AUTO_INCREMENT=" + tableStatus.get("Auto_increment") + ";");
			}
			if ("MYSQL40".equals(sqlCompat) && version.compareTo("4.1") >= 0 && version.compareTo("5.1") < 0) {
				if (tableStatus.get("Auto_increment").length() > 0) {
					int tempPosition = tableStru.indexOf(",");
					tableStru.insert(tempPosition, " auto_increment");
				}
				if (tableStatus.get("Engine").equals("MEMORY")) {
					int tempPosition = tableStru.indexOf("TYPE=MEMORY");
					if (tempPosition != -1) {
						tableStru.replace(tempPosition, 11, "TYPE=HEAP");
					}
				}
			}
		}
		

		
		List<Map<String, String>> fieldList = fetchRows("SHOW FULL COLUMNS FROM " + table);
		Map<String, String> firstField = fieldList.get(0);
		int recordNum = n;
		int fieldNum = 0;
		a: while (container.length() + tableStru.length() + tableData.length() + 500 < volumeSize
				&& recordNum == n) {
			ret.setRunBackupData(true);
			List<String[]> recordList = null;
			if (firstField.get("Extra").equals("auto_increment")) {
				recordList = fetchValues("SELECT * FROM " + table + " WHERE " + firstField.get("Field")
						+ " > " + offset + " LIMIT " + n);
			} else {
				recordList = fetchValues("SELECT * FROM " + table + " LIMIT " + offset + "," + n);
			}
			if ((recordNum = recordList.size()) > 0) {
				fieldNum = recordList.get(0).length;
				String extendComma = "";
				StringBuffer extendValues = new StringBuffer();
				for (String[] row : recordList) {
					String comma = "";
					StringBuffer values = new StringBuffer();
					for (int i = 0; i < fieldNum; i++) {
						String ftype = fieldList.get(i).get("Type");
						values.append(comma);
						if (useHex && row[i] != null && !"".equals(row[i])
								&& (ftype.indexOf("char") != -1 || ftype.indexOf("text") != -1)) {
							values.append("0x" + bin2hex(row[i]));
						} else {
							values.append("'" + escapeString(row[i]) + "'");
						}
						comma = ",";
					}
					if (useExtend) {
						if (extendValues.length() + container.length() + tableStru.length()
								+ tableData.length() + 500 < volumeSize) {
							offset = firstField.get("Extra").equals("auto_increment") ? Integer
									.parseInt(row[0]) : offset + 1;
							extendValues.append(extendComma + " (" + values + ")");
							extendComma = ",";
						} else {
							tableData.append("INSERT INTO " + table + " VALUES " + extendValues + ";\n");
							ret.setComplete(false);
							break a;
						}
					} else {
						if (values.length() + container.length() + tableStru.length() + tableData.length()
								+ 500 < volumeSize) {
							offset = firstField.get("Extra").equals("auto_increment") ? Integer
									.parseInt(row[0]) : offset + 1;
							tableData.append("INSERT INTO " + table + " VALUES (" + values + ");\n");
						} else {
							ret.setComplete(false);
							break a;
						}
					}
				}
				if (useExtend) {
					tableData.append("INSERT INTO " + table + " VALUES " + extendValues + ";\n");
				}
			}
		}
		
		if (tableStru.length() > 0) {
			container.append(tableStru);
			container.append("\n\n");
		}
		container.append(tableData);
		container.append("\n");
		ret.setOffset(offset);
		ret.setStructure(tableStru.toString());
		ret.setData(tableData.toString());
		return ret;
	}

	
	private String bin2hex(String str) {
		if (str != null || str.length() > 0) {
			char[] digital = "0123456789ABCDEF".toCharArray();
			try {
				byte[] bytes = str.getBytes(dumpCharset);
				StringBuffer hex = new StringBuffer();
				for (byte b : bytes) {
					hex.append(digital[(b & 0xf0) >> 4]);
					hex.append(digital[b & 0x0f]);
				}
				return hex.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	
	private String escapeString(String str) {
		if (str != null) {
			StringBuffer sb = new StringBuffer(str.length() * 2);
			StringCharacterIterator iterator = new StringCharacterIterator(str);
			char character = iterator.current();
			while (character != StringCharacterIterator.DONE) {
				switch (character) {
					case '"':
						sb.append("\\\"");
						break;
					case '\'':
						sb.append("\\'");
						break;
					case '\\':
						sb.append("\\\\");
						break;
					case '\n':
						sb.append("\\n");
						break;
					case '\r':
						sb.append("\\r");
						break;
					default:
						sb.append(character);
						break;
				}
				character = iterator.next();
			}
			return sb.toString();
		}
		return "";
	}

	private Map<String, String> fetchRow(String sql) {
		List<Map<String, String>> rows = fetchRows(sql);
		return rows.isEmpty() ? new HashMap<String, String>() : rows.get(0);
	}

	@SuppressWarnings("deprecation")
	private List<Map<String, String>> fetchRows(String sql) {
		List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
		Session session = null;
		Connection conn = null;
		Transaction tran = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int totalField = metaData.getColumnCount();
			Map<String, String> data = null;
			while (rs.next()) {
				data = new HashMap<String, String>();
				datas.add(data);
				for (int i = 1; i <= totalField; i++) {
					data.put(metaData.getColumnLabel(i), rs.getString(i));
				}
			}
			tran.commit();
		} catch (SQLException ex) {
			tran.rollback();
			ex.printStackTrace();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
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
		return datas;
	}

	@SuppressWarnings("deprecation")
	private List<String[]> fetchValues(String sql) {
		List<String[]> datas = new ArrayList<String[]>();
		Session session = null;
		Connection conn = null;
		Transaction tran = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			session = SessionFactory.getSession();
			tran = session.beginTransaction();
			conn = session.connection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int totalField = metaData.getColumnCount();
			String[] data = null;
			while (rs.next()) {
				data = new String[totalField];
				for (int i = 0; i < totalField; i++) {
					data[i] = rs.getString(i + 1);
				}
				datas.add(data);
			}
			tran.commit();
		} catch (SQLException ex) {
			tran.rollback();
			ex.printStackTrace();
		} finally {
			try {
				if (tran != null) {
					tran = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
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
		return datas;
	}

	public static void main(String[] args) {
	}
}
