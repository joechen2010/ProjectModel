package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.BackupInfo;
import cn.jcenterhome.util.BackupUtil;
import cn.jcenterhome.util.Base64;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.util.ZipUtil;
import cn.jcenterhome.vo.TableStatusVO;
import cn.jcenterhome.web.action.BaseAction;


public class BackupAction extends BaseAction {

	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, String> jchConf = JavaCenterHome.jchConfig;
		boolean isManager = Common.checkPerm(request, response, "managebackup");
		boolean isFounder = Common.ckFounder((Integer) sGlobal.get("supe_uid"));
		if (!isManager || !isFounder) {
			return cpMessage(request, mapping, "cp_no_authority_management_operation_backup");
		}

		int volume = Common.intval(request.getParameter("volume")) + 1;
		String jchRoot = JavaCenterHome.jchRoot;
		String backupDir = (String) Common.getData("backupdir", false);
		if (Common.empty(backupDir)) {
			backupDir = Common.getRandStr(6, false);
			Common.setData("backupdir", backupDir, false);
		}
		backupDir = "backup_" + backupDir;
		File backup = new File(jchRoot + "data/" + backupDir);
		if (!backup.exists() || !backup.isDirectory()) {
			backup.mkdirs();
		}
		String dbCharset = (Common.empty(jchConf.get("dbCharset")) ? jchConf.get("charset") : jchConf
				.get("dbCharset")).replace("-", "");
		request.setAttribute("dbCharset", dbCharset);
		try {
			if (submitCheck(request, "delexportsubmit")) {
				String[] delexports = request.getParameterValues("delexport[]");
				if (delexports != null) {
					for (String delexport : delexports) {
						String fileExtend = Common.fileext(delexport);
						if ("sql".equals(fileExtend) || "zip".equals(fileExtend)) {
							delexport = delexport.replace("..", "");
							File backupFile = new File(jchRoot + "data/" + delexport);
							if (backupFile.exists()) {
								backupFile.delete();
							}
						}
					}
				}
				return cpMessage(request, mapping, "do_success", "admincp.jsp?ac=backup");
			} else if (submitCheck(request, "importsubmit")) {
				String dataFile = Common.trim(request.getParameter("datafile")).replace("..", "");
				if (!new File(jchRoot + "data/" + dataFile).exists()) {
					return cpMessage(request, mapping, "cp_data_import_failed_the_file_does_not_exist");
				} else {
					String fileExtend = Common.fileext(dataFile);
					if ("sql".equals(fileExtend)) {
						return cpMessage(request, mapping, "cp_start_transferring_data",
								"admincp.jsp?ac=backup&op=import&do=import&datafile=" + dataFile);
					} else if ("zip".equals(fileExtend)) {
						return cpMessage(request, mapping, "cp_start_transferring_data",
								"admincp.jsp?ac=backup&op=import&do=zip&datafile=" + dataFile);
					} else {
						return cpMessage(request, mapping, "cp_wrong_data_file_format_into_failure");
					}
				}
			}
		} catch (Exception e1) {
			return showMessage(request, response, e1.getMessage());
		}
		String op = request.getParameter("op");
		int timeStamp = (Integer) sGlobal.get("timestamp");
		String dbVersion = dataBaseService.showVersion();
		if (Common.empty(op)) {
			String fileName = Common.sgmdate(request, "yyMMdd", timeStamp) + "_"
					+ Common.getRandStr(8, false);
			List<TableStatusVO> jchome_tableList = fetchTableList(jchConf.get("tablePre"));
			if (backup != null && backup.exists() && backup.isDirectory()) {
				List<Map<String, String>> exportLog = getBackup(request, backupDir, backup);
				request.setAttribute("filename", fileName);
				request.setAttribute("exportlog", exportLog);
				request.setAttribute("backupdir", backupDir);
				request.setAttribute("jchome_tablelist", jchome_tableList);
				request.setAttribute("dbversion", Common.intval(dbVersion));
			} else {
				return showMessage(request, response, "cp_directory_does_not_exist_or_can_not_be_accessed",
						null, 0, jchRoot + "data/");
			}
		} else if ("export".equals(op)) {
			String type = request.getParameter("type");
			String method = request.getParameter("method");
			String fileName = request.getParameter("filename");
			String sqlCompat = request.getParameter("sqlcompat");
			String sqlCharset = request.getParameter("sqlcharset");
			Integer useZip = Common.intval(request.getParameter("usezip"));
			boolean useHex = "1".equals(request.getParameter("usehex")) ? true : false;
			boolean useExtend = "1".equals(request.getParameter("extendins")) ? true : false;

			if (fileName.length() == 0 || fileName.length() > 40) {
				return cpMessage(request, mapping, "cp_documents_were_incorrect_length");
			} else {
				fileName.replace(".", "_").replaceAll("(?i)[^a-z0-9]", "");
			}
			List<String> tables = new ArrayList<String>();
			if ("jchomes".equals(type)) {
				tables = tableNames(fetchTableList(jchConf.get("tablePre")));
			} else if ("custom".equals(type)) {
				if (request.getParameter("setup") != null) {
					String[] temp = request.getParameterValues("customtables[]");
					if (!Common.empty(temp)) {
						for (String obj : temp) {
							tables.add(obj);
						}
					}
					Common.setData("custombackup", tables, false);
				} else {
					String customBackup = (String) Common.getData("custombackup", false);
					tables = Serializer.unserialize(customBackup);
				}
			}
			if (Common.empty(tables) || !Common.isArray(tables)) {
				return cpMessage(request, mapping, "cp_backup_table_wrong");
			}
			dataBaseService.execute("SET SQL_QUOTE_SHOW_CREATE=0");

			boolean isSqlCharsetEmpty = Common.empty(sqlCharset);
			String encode = timeStamp + "," + JavaCenterHome.JCH_VERSION + "," + type + "," + method + ","
					+ volume;
			String identify = "# Identify: " + Base64.encode(encode, JavaCenterHome.JCH_CHARSET) + "\n";
			String dumpCharset = isSqlCharsetEmpty ? jchConf.get("charset").replace("-", "") : sqlCharset;
			String setNames = (!isSqlCharsetEmpty && dbVersion.compareTo("4.1") > 0 && (Common
					.empty(sqlCompat) || "MYSQL41".equals(sqlCompat))) ? "SET NAMES '" + dumpCharset
					+ "';\n\n" : "";
			String backupFile = jchRoot + "data/" + backupDir + "/" + fileName;
			if (dbVersion.compareTo("4.1") > 0) {
				if (!isSqlCharsetEmpty) {
					dataBaseService.execute("SET NAMES '" + sqlCharset + "'");
				}
				if ("MYSQL40".equals(sqlCompat)) {
					dataBaseService.execute("SET SQL_MODE='MYSQL40'");
				} else if ("MYSQL41".equals(sqlCompat)) {
					dataBaseService.execute("SET SQL_MODE=' '");
				}
			}
			if ("multivol".equals(method)) {
				int sizeLimit = Common.intval(request.getParameter("sizelimit"));
				int startFrom = Common.intval(request.getParameter("startfrom"));
				int tableId = Common.intval(request.getParameter("tableid"));
				int tableNum = tables.size();
				int fileSize = sizeLimit * 1000;
				boolean dumpStruct = "false".equals(request.getParameter("dstruct")) ? false : true;

				boolean complete = true;
				boolean runBackupData = true;
				StringBuffer sqlDump = new StringBuffer();
				BackupUtil backupObj = new BackupUtil(sqlCompat, dumpCharset, fileSize, useHex, useExtend);

				for (; complete && tableId < tableNum && sqlDump.length() + 500 < fileSize; tableId++) {
					BackupInfo info = backupObj.dump(sqlDump, tables.get(tableId), startFrom, dumpStruct);
					if (!dumpStruct)
						dumpStruct = true;
					runBackupData = info.isRunBackupData();
					if (complete = info.isComplete()) {
						startFrom = 0;
					} else {
						startFrom = info.getOffset();
					}
				}

				if (!complete || !runBackupData) {
					tableId--;
					dumpStruct = false;
				}

				if (sqlDump.length() > 0) {
					StringBuffer header = new StringBuffer();
					header.append(identify);
					header.append("# JavaCenter Home Multi-Volume Data Dump Vol." + volume + "\n");
					header.append("# Version: JavaCenterHome " + JavaCenterHome.JCH_VERSION + "\n");
					header.append("# Time: "
							+ Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss", (Integer) sGlobal
									.get("timestamp")) + "\n");
					header.append("# Type: " + type + "\n");
					header.append("# Table Prefix: " + jchConf.get("tablePre") + "\n");
					header.append("#\n");
					header.append("# JavaCenter Home: http://www.jsprun.net\n");
					header.append("# Please visit our website for newest infomation about JavaCenter Home\n");
					header.append("# ---------------------------------------------------------\n\n\n");
					header.append(setNames);
					sqlDump.insert(0, header);

					String dumpFileName = String.format(backupFile + "-%d.sql", volume);
					File dumpFile = new File(dumpFileName);
					if (!FileHelper.writeFile(dumpFile, sqlDump.toString())) {
						return cpMessage(request, mapping,
								"cp_failure_writes_the_document_check_file_permissions",
								"admincp.jsp?ac=backup", 1);
					}
					if (useZip == 2) {
						String zipFile = String.format(backupFile + "-%d.zip", volume);
						ZipUtil zip = new ZipUtil();
						if (!zip.compress(zipFile, dumpFile)) {
							return cpMessage(request, mapping,
									"cp_failure_writes_the_document_check_file_permissions",
									"admincp.jsp?ac=backup");
						}
						dumpFile.delete();
					}

					String forwardURL = "admincp.jsp?ac=backup" + "&op=export&type=" + Common.urlDecode(type)
							+ "&filename=" + Common.urlEncode(fileName) + "&method=multivol&sizelimit="
							+ sizeLimit + "&tableid=" + tableId + "&startfrom=" + startFrom + "&extendins="
							+ (useExtend ? 1 : 0) + "&sqlcharset=" + Common.urlEncode(sqlCharset)
							+ "&sqlcompat=" + Common.urlEncode(sqlCompat) + "&usehex=" + (useHex ? 1 : 0)
							+ "&usezip=" + useZip + "&volume=" + volume + "&dstruct=" + dumpStruct;
					return cpMessage(request, mapping, "cp_vol_backup_database", forwardURL, 1, String
							.valueOf(volume));
				} else {
					if (useZip == 1) {
						String zipFile = backupFile + ".zip";
						File[] files = new File[volume - 1];
						for (int i = 1; i < volume; i++) {
							files[i - 1] = new File(String.format(backupFile + "-%d.sql", i));
						}
						if (files.length > 0) {
							ZipUtil zip = new ZipUtil();
							zip.compress(zipFile, files);
						} else {
							return cpMessage(request, mapping, "cp_complete_database_backup",
									"admincp.jsp?ac=backup", 1, String.valueOf(volume - 1));
						}
						for (File sqlf : files) {
							sqlf.delete();
						}
						FileHelper.writeFile(jchRoot + "./data/" + backupDir + "/index.htm", "", true);
						return cpMessage(request, mapping,
								"cp_successful_data_compression_and_backup_server_to",
								"admincp.jsp?ac=backup", 1);
					} else {
						FileHelper.writeFile(jchRoot + "./data/" + backupDir + "/index.htm", "", true);
						return cpMessage(request, mapping, "cp_complete_database_backup",
								"admincp.jsp?ac=backup", 1, String.valueOf(volume - 1));
					}
				}
			} else {
				List<Map<String, Object>> query = dataBaseService
						.executeQuery("SHOW VARIABLES LIKE 'basedir'");
				String host = jchConf.get("dbHost");
				String port = jchConf.get("dbPort");
				String mysqlBase = query.isEmpty() ? "" : (String) (query.get(0).get("Value"));
				String mysqlBinPath = mysqlBase.equals("/") ? "" : mysqlBase + "bin" + File.separatorChar;
				String dumpFile = backupFile + ".sql";
				File shellDumpFile = new File(dumpFile);
				StringBuffer tablesStr = new StringBuffer();
				for (Object value : tables) {
					tablesStr.append(value);
					tablesStr.append(" ");
				}
				try {
					StringBuffer shell = new StringBuffer();
					shell.append("\"" + mysqlBinPath + "mysqldump\" --force --quick --default-character-set="
							+ dbCharset);
					shell.append(dbVersion.compareTo("4.1") > 0 ? " --skip-opt --create-options" : " -all");
					shell.append(" --add-drop-table");
					shell.append(useExtend ? " --extended-insert" : "");
					shell
							.append(dbVersion.compareTo("4.1") > 0 && "MYSQL40".equals(sqlCompat) ? " --compatible=mysql40"
									: "");
					shell.append(" --host=" + host);
					shell.append(port.length() != 0 ? (Common.isNumeric(port) ? " --port=" + port
							: " --sock=" + port) : "");
					shell.append(" --user=" + jchConf.get("dbUser"));
					shell.append(" --password=" + jchConf.get("dbPw"));
					shell.append(" " + jchConf.get("dbName"));
					shell.append(" " + tablesStr);
					shell.append(" > \"" + shellDumpFile.getAbsolutePath() + "\" ");
					System.out.println(shell);
					String osName = System.getProperty("os.name");
					if (osName.startsWith("Windows")) {
						Runtime.getRuntime().exec("cmd.exe /c " + shell);
					} else if (osName.startsWith("Linux")) {
						Runtime.getRuntime().exec(shell.toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (shellDumpFile.exists()) {
					if (shellDumpFile.canWrite()) {
						String header = identify + setNames + "\n #";
						FileHelper.writeFile(dumpFile, header, 0, header.length());
					}
					if (useZip != 0) {
						ZipUtil zip = new ZipUtil();
						String zipFileName = backupFile + ".zip";
						try {
							zip.compress(zipFileName, shellDumpFile);
							shellDumpFile.delete();
							FileHelper.writeFile(jchRoot + "./data/" + backupDir + "/index.htm", "", true);
							return cpMessage(request, mapping,
									"cp_successful_data_compression_and_backup_server_to",
									"admincp.jsp?ac=backup", 1);
						} catch (Exception e) {
							return cpMessage(request, mapping, "cp_backup_file_compression_failure",
									"admincp.jsp?ac=backup", 1);
						}
					} else {
						FileHelper.writeFile(jchRoot + "./data/" + backupDir + "/index.htm", "", true);
						return cpMessage(request, mapping,
								"cp_successful_data_compression_and_backup_server_to",
								"admincp.jsp?ac=backup");
					}
				} else {
					return cpMessage(request, mapping, "cp_shell_backup_failure", "admincp.jsp?ac=backup", 1);
				}
			}
		} else if ("import".equals(op)) {
			String paramDo = request.getParameter("do");
			if ("zip".equals(paramDo)) {
				try {
					String dataFile = Common.trim(request.getParameter("datafile"));
					ZipUtil zip = new ZipUtil();
					ZipFile zipFile = new ZipFile(jchRoot + "./data/" + dataFile);
					String importFile = zip.getFirstEntry(zipFile).getName();
					if (zipFile.size() == 0 || !importFile.endsWith(".sql")) {
						return cpMessage(request, mapping, "cp_data_file_does_not_exist");
					}
					request.setAttribute("baseName", dataFile.substring(dataFile.lastIndexOf("/") + 1));
					String entryContent = zip.getEntryContent(zipFile, zip.getFirstEntry(zipFile));
					String[] identify = Base64.decode(
							entryContent.substring(0, 256).replaceAll("(?s)^# Identify:\\s*(\\w+).*", "$1"),
							JavaCenterHome.JCH_CHARSET).split(",");
					request.setAttribute("identify", identify);
					boolean confirm = request.getParameter("confirm") != null ? true : false;
					if (!confirm && !"2.0".equals(identify[1])) {
						request.setAttribute("dataFile", dataFile);
						request.setAttribute("showform", 1);
						return mapping.findForward("backup");
					}
					String toDir = jchRoot + "./data/" + backupDir;
					zip.decompress(zipFile, toDir, ".sql");
					if (zip.getDecompressFileCount() == 0) {
						return cpMessage(request, mapping, "cp_data_file_does_not_exist");
					}
					int multiVolume = request.getParameter("multivol") != null ? Common.intval(request
							.getParameter("multivol")) : 0;
					String dataFileVol1 = request.getParameter("datafile_vol1") != null ? request
							.getParameter("datafile_vol1") : "";
					if (!Common.empty(multiVolume)) {
						multiVolume++;
						dataFile = dataFile.replaceAll("-(\\d+)(\\..+)$", "-" + multiVolume + "$2");
						File df = new File(jchRoot + "./data/" + dataFile);
						if (df.exists()) {
							return cpMessage(request, mapping, "cp_decompress_data_files_success",
									"admincp.jsp?ac=backup&op=import&do=zip&multivol=" + multiVolume
											+ "&datafile_vol1=" + dataFileVol1 + "&datafile=" + dataFile
											+ "&confirm=yes", 1, "" + multiVolume);
						} else {
							request.setAttribute("showform", 2);
							request.setAttribute("datafile_vol1", dataFileVol1);
							return mapping.findForward("backup");
						}
					}
					if ("multivol".equals(identify[3]) && "1".equals(identify[4])
							&& dataFile.matches(".*-1(\\..+)$")) {
						dataFileVol1 = dataFile.replace(".zip", ".sql");
						dataFile = dataFile.replaceAll("-1(\\..+)$", "-2$1");
						File df = new File(jchRoot + "./data/" + dataFile);
						if (df.exists()) {
							request.setAttribute("showform", 3);
							request.setAttribute("datafile", dataFile);
							request.setAttribute("datafile_vol1", dataFileVol1);
							return mapping.findForward("backup");
						}
					}
					request.setAttribute("showform", 4);
					request.setAttribute("backupdir", backupDir);
					request.setAttribute("importfile", importFile);
				} catch (IOException e) {
					return cpMessage(request, mapping, e.getMessage());
				}
			} else if ("import".equals(paramDo)) {
				String sqlDump = null;
				String[] identify = null;
				String dataFile = Common.trim(request.getParameter("datafile")).replace("..", "");
				String dataFileRoot = jchRoot + "data/" + dataFile;
				File sqlFile = new File(dataFileRoot);
				if (sqlFile.exists() && sqlFile.isFile()) {
					sqlDump = FileHelper.readFile(sqlFile, 256);
					identify = Base64.decode(sqlDump.replaceAll("(?s)^# Identify:\\s*(\\w+).*", "$1"),
							JavaCenterHome.JCH_CHARSET).split(",");
					if ("multivol".equals(identify[3])) {
						sqlDump += FileHelper.readFile(sqlFile, (int) sqlFile.length());
					}
				} else {
					if (request.getParameter("autoimport") != null) {
						return cpMessage(request, mapping, "cp_the_volumes_of_data_into_databases_success",
								"admincp.jsp?ac=backup", 1);
					} else {
						return cpMessage(request, mapping, "cp_data_file_does_not_exist");
					}
				}
				if ("multivol".equals(identify[3])) {
					String[] sqlQuery = splitSql(sqlDump);
					sqlDump = null;
					for (String sql : sqlQuery) {
						sql = synTableStruct(sql.trim(), dbVersion.compareTo("4.1") > 0, dbCharset);
						if (!Common.empty(sql)) {
							Map<String, Object> result = dataBaseService.execute(sql);
							if (result.get("error") != null && (Integer) result.get("errorCode") != 1062) {
							}
						}
					}
					String delunzip = request.getParameter("delunzip");
					if (delunzip != null) {
						File unzipFile = new File(jchRoot + "./data/" + dataFile);
						unzipFile.delete();
					}
					String dataFile_next = dataFile.replaceAll("-(" + Common.intval(identify[4])
							+ ")(\\..+)$", "-" + (Common.intval(identify[4]) + 1) + "$2");
					request.setAttribute("datafile_next", dataFile_next);
					if ("1".equals(identify[4])) {
						request.setAttribute("showform", 5);
					} else if (request.getParameter("autoimport") != null) {
						return cpMessage(request, mapping, "cp_data_files_into_success",
								"admincp.jsp?ac=backup&op=import&do=import&datafile=" + dataFile_next
										+ "&autoimport=yes" + (delunzip != null ? "&delunzip=yes" : ""), 1,
								identify[4]);
					} else {
						return cpMessage(request, mapping, "cp_the_volumes_of_data_into_databases_success",
								"admincp.jsp?ac=backup");
					}
				} else if ("shell".equals(identify[3])) {
					List<Map<String, Object>> query = dataBaseService
							.executeQuery("SHOW VARIABLES LIKE 'basedir'");
					String mysqlBase = query.isEmpty() ? "" : (String) (query.get(0).get("Value"));
					String mysqlBin = mysqlBase.equals("/") ? "" : Common.addSlashes(mysqlBase) + "bin/";
					String dbHost = "";
					String dbPort = "";
					String dbUser = "";
					String dbPwd = "";
					String dbName = "";
					try {
						String shell = "\""
								+ mysqlBin
								+ "mysql\" --default-character-set="
								+ dbCharset
								+ " -h"
								+ dbHost
								+ (Common.empty(dbPort) ? "" : (Common.isNumeric(dbPort) ? " -P" + dbPort
										: " -S" + dbPort)) + " -u" + dbUser + " -p" + dbPwd + " " + dbName
								+ " < " + dataFile;
						Runtime.getRuntime().exec(shell);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return cpMessage(request, mapping, "cp_the_volumes_of_data_into_databases_success",
							"admincp.jsp?ac=backup", 1);
				} else {
					return cpMessage(request, mapping, "cp_data_file_format_is_wrong_not_into");
				}
			}
		}
		return mapping.findForward("backup");
	}

	
	private String synTableStruct(String sql, boolean mysqlGt41, String dbcharset) {
		int pos = sql.indexOf("CREATE TABLE");
		if (pos == -1 || pos >= 18) {
			return sql;
		}
		boolean gt41 = sql.indexOf("ENGINE=") == -1 ? false : true;
		
		if (gt41 == mysqlGt41) {
			if (gt41 && dbcharset != null) {
				sql = sql.replaceAll("(?i) character set \\w+", "");
				sql = sql.replaceAll("(?i) collate \\w+", "");
				sql = sql.replaceAll("(?is)DEFAULT CHARSET=\\w+", "DEFAULT CHARSET=" + dbcharset);
			}
			return sql;
		}
		if (mysqlGt41) {
			sql = sql.replaceAll("(?i)TYPE=HEAP", "ENGINE=MEMORY DEFAULT CHARSET=" + dbcharset);
			sql = sql.replaceAll("(?is)TYPE=(\\w+)", "ENGINE=$1 DEFAULT CHARSET=" + dbcharset);
			return sql;
		} else {
			sql = sql.replaceAll("(?i)character set \\w+", "");
			sql = sql.replaceAll("(?i)collate \\w+", "");
			sql = sql.replaceAll("(?i)ENGINE=MEMORY", "ENGINE=HEAP");
			sql = sql.replaceAll("(?is)\\s*DEFAULT CHARSET=\\w+", "");
			sql = sql.replaceAll("(?is)\\s*COLLATE=\\w+", "");
			sql = sql.replaceAll("(?is)ENGINE=(\\w+)(.*)", "TYPE=$1$2");
			return sql;
		}
	}

	
	private String[] splitSql(String sqlDump) {
		int num = 0;
		sqlDump = sqlDump.replace("\r", "\n");
		String[] queriesArray = sqlDump.trim().split(";\n");
		String[] ret = new String[queriesArray.length];
		sqlDump = null;
		for (String query : queriesArray) {
			String[] queries = query.trim().split("\n");
			ret[num] = ret[num] != null ? ret[num] : "";
			for (String sub : queries) {
				boolean bl = !Common.empty(sub) && "#".equals(String.valueOf(sub.charAt(0)));
				ret[num] += bl ? "" : sub;
			}
			num++;
		}
		return ret;
	}

	
	private List<String> tableNames(List<TableStatusVO> list) {
		List<String> ret = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			ret.add(list.get(i).getName());
		}
		return ret;
	}

	
	private List<Map<String, String>> getBackup(HttpServletRequest request, String backupDir, File backup) {
		List<Map<String, String>> exportLog = new ArrayList<Map<String, String>>();
		File[] bakupFiles = backup.listFiles();
		Pattern sqlPattern = Pattern.compile("(?i).+\\.sql$");
		Pattern zipPattern = Pattern.compile("(?i).+\\.zip$");
		for (File f : bakupFiles) {
			if (f.isFile()) {
				String fileName = backupDir + "/" + f.getName();
				String fileSize = Common.formatSize(f.length());
				String date = Common.sgmdate(request, "yyyy-MM-dd HH:mm:ss", (int) (f.lastModified() / 1000));
				Map<String, String> temp = new HashMap<String, String>();
				if (sqlPattern.matcher(f.getName()).matches()) {
					String identifyStr = FileHelper.readFile(f, 256).replaceAll(
							"(?s)^# Identify:\\s*(\\w+).*", "$1");
					String[] identify = Base64.decode(identifyStr, JavaCenterHome.JCH_CHARSET).split(",");
					if (!"multivol".equals(identify[3])) {
						identify[4] = "";
					}
					temp.put("version", identify[1]);
					temp.put("type", identify[2]);
					temp.put("method", identify[3]);
					temp.put("volume", identify[4]);
					temp.put("filename", fileName);
					temp.put("basename", f.getName());
					temp.put("dateline", date);
					temp.put("size", fileSize);
					exportLog.add(temp);
				} else if (zipPattern.matcher(f.getName()).matches()) {
					temp.put("type", "zip");
					temp.put("filename", fileName);
					temp.put("basename", f.getName());
					temp.put("size", fileSize);
					temp.put("dateline", date);
					temp.put("method", "");
					temp.put("volume", "");
					exportLog.add(temp);
				}
			}
		}
		return exportLog;
	}

	
	private List<TableStatusVO> fetchTableList(String tablePre) {
		if (tablePre == null || tablePre.length() == 0) {
			tablePre = "*";
		}
		List<TableStatusVO> query = dataBaseService.findTableStatus("SHOW TABLE STATUS LIKE '" + tablePre
				+ "%'");
		List<TableStatusVO> tables = new ArrayList<TableStatusVO>();
		for (TableStatusVO table : query) {
			if (table.getName().indexOf("cache") == -1) {
				tables.add(table);
			}
		}
		return tables;
	}
}