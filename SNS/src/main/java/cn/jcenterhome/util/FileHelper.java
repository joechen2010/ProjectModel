package cn.jcenterhome.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.cfg.Environment;


public final class FileHelper {
	
	public static boolean writeFile(File file, String content) {
		return writeFile(file.getPath(), content, false);
	}

	
	public static boolean writeFile(String filePath, String content) {
		return writeFile(filePath, content, false);
	}

	
	public static boolean writeFile(String filePath, String content, boolean append) {
		try {
			File file = new File(filePath);
			FileOutputStream out = new FileOutputStream(file, append);
			OutputStreamWriter fwout = new OutputStreamWriter(out, JavaCenterHome.JCH_CHARSET);
			BufferedWriter bw = new BufferedWriter(fwout);
			FileLock fl = out.getChannel().tryLock();
			if (fl.isValid()) {
				bw.write(content);
				fl.release();
			}
			bw.flush();
			fwout.flush();
			out.flush();
			bw.close();
			fwout.close();
			out.close();
			bw = null;
			fwout = null;
			out = null;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	public static boolean writeFile(String filePath, String content, HttpServletRequest request) {
		boolean flag = writeFile(filePath, content);
		if (!flag) {
			writeLog(request, "error", "File: " + filePath + " write error.");
		}
		return flag;
	}

	
	@SuppressWarnings("unchecked")
	public static void writeLog(HttpServletRequest request, String fileName, String log) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		int timestamp = (Integer) sGlobal.get("timestamp");
		String timeoffset = Common.getTimeOffset(sGlobal, sConfig);
		String onlineIP = Common.getOnlineIP(request);
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		String requestURI = (String) request.getAttribute("requestURI");
		writeLog(timestamp, timeoffset, onlineIP, supe_uid, requestURI, fileName, log);
	}

	
	
	@SuppressWarnings("unchecked")
	public static void writeLog(int timestamp, String timeoffset, String onlineIP, int supe_uid,
			String requestURI, String fileName, String log) {
		char split = '\t';
		StringBuffer logContent = new StringBuffer();
		logContent.append(Common.gmdate("yyyy-MM-dd HH:mm:ss", timestamp, timeoffset));
		logContent.append(split);
		logContent.append(onlineIP);
		logContent.append(split);
		logContent.append(supe_uid);
		logContent.append(split);
		logContent.append(requestURI);
		logContent.append(split);
		logContent.append(log.trim().replaceAll("(\r\n|\r|\n)", " "));
		logContent.append('\n');

		String yearMonth = Common.gmdate("yyyyMM", timestamp, timeoffset);
		String logDir = JavaCenterHome.jchRoot + "data/log/";
		File logDirFile = new File(logDir);
		if (!logDirFile.isDirectory()) {
			logDirFile.mkdir();
		}
		String logFileName = logDir + yearMonth + "_" + fileName + ".log";
		File logFile = new File(logFileName);
		if (logFile.length() > 2048000) {
			File[] files = logDirFile.listFiles();
			int id = 0;
			int maxid = 0;
			for (File file : files) {
				String name = file.getName();
				if (name.matches("^" + yearMonth + "_" + fileName + "_(\\d)*\\.log$")) {
					id = Integer.valueOf(name.substring(name.lastIndexOf("_") + 1, name.lastIndexOf(".")));
					if (id > maxid) {
						maxid = id;
					}
				}
			}
			files = null;
			logDirFile = null;
			logFile.renameTo(new File(logDir + yearMonth + "_" + fileName + "_" + (maxid + 1) + ".log"));
		}
		writeFile(logFileName, logContent.toString(), true);
	}

	public static boolean writeFile(String filePath, String content, int off, int len) {
		File file = new File(filePath);
		if (file.exists()) {
			FileOutputStream outputStream = null;
			OutputStreamWriter outputWriter = null;
			BufferedWriter bufWriter = null;
			try {
				outputStream = new FileOutputStream(filePath);
				outputWriter = new OutputStreamWriter(outputStream, JavaCenterHome.JCH_CHARSET);
				bufWriter = new BufferedWriter(outputWriter);
				FileLock fl = outputStream.getChannel().tryLock();
				if (fl.isValid()) {
					bufWriter.write(content, off, len);
					fl.release();
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					bufWriter.close();
					outputWriter.close();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	
	public static String readFile(File file, int len) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			byte datas[] = new byte[len];
			bis.read(datas, 0, len);
			return new String(datas,JavaCenterHome.JCH_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public static String readFile(String filePath) {
		return readFile(new File(filePath));
	}

	
	public static String readFile(File file) {
		StringBuffer content = new StringBuffer();
		if (file != null && file.exists()) {
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis, JavaCenterHome.JCH_CHARSET);
				br = new BufferedReader(isr);
				String temp = null;
				while ((temp = br.readLine()) != null) {
					content.append(temp);
					content.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null) {
						br.close();
					}
					if (isr != null) {
						isr.close();
					}
					if (fis != null) {
						fis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content.toString().trim();
	}

	
	public static List<String> readFileToList(File file) {
		List<String> lines = new ArrayList<String>();
		if (file != null && file.exists()) {
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis, JavaCenterHome.JCH_CHARSET);
				br = new BufferedReader(isr);
				String temp = null;
				while ((temp = br.readLine()) != null) {
					lines.add(temp);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null) {
						br.close();
					}
					if (isr != null) {
						isr.close();
					}
					if (fis != null) {
						fis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	public static InputStream getResourceAsStream(String resource) throws FileNotFoundException {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		if (stream == null) {
			stream = Environment.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			stream = Environment.class.getClassLoader().getResourceAsStream(stripped);
		}
		if (stream == null) {
			throw new FileNotFoundException(resource + " not found");
		}
		return stream;
	}
}