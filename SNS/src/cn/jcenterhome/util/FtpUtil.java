package cn.jcenterhome.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;


public class FtpUtil {
	private static final int JC_FTPClientYES = 1;
	private static final int JC_FTPClientNO = -1;
	private static final int JC_FTPClientException = -2;
	private FTPClient fc;

	
	public int sftp_connect(HttpServletRequest request) {
		Map<String, Object> setting = (Map<String, Object>) request.getAttribute("globalSetting");
		int ftpssl = Common.intval(setting.get("ftpssl") + "");
		String ftphost = setting.get("ftphost") + "";
		int ftpport = Common.intval(setting.get("ftpport") + "");
		String ftpuser = setting.get("ftpuser") + "";
		String ftppassword = setting.get("ftppassword") + "";
		int ftppasv = Common.intval(setting.get("ftppasv") + "");
		String ftpdir = setting.get("ftpdir") + "";
		int ftptimeout = Common.intval(setting.get("ftptimeout") + "");
		
		if (ftpssl > 0) {
			try {
				fc = new FTPSClient();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return JC_FTPClientException;
			}
		}else{
			fc = new FTPClient();
		}
		try {
			fc.setConnectTimeout(20000);
			InetAddress inetAddress = InetAddress.getByName(ftphost);
			fc.connect(inetAddress, ftpport);
			if (fc.login(ftpuser, ftppassword)) {
				if (ftppasv > 0) {
					fc.pasv();
				}
				if (ftptimeout > 0) {
					fc.setDataTimeout(ftptimeout);
				}
				if (fc.changeWorkingDirectory(ftpdir)) {
					return JC_FTPClientYES;
				} else {
					FileHelper.writeLog(request, "FTP", "CHDIR " + ftpdir + " ERROR.");
					try {
						fc.disconnect();
						fc = null;
					} catch (Exception e1) {
					}
					return JC_FTPClientNO;
				}
			} else {
				FileHelper.writeLog(request, "FTP", "530 NOT LOGGED IN.");
				try {
					fc.disconnect();
					fc = null;
				} catch (Exception e1) {
				}
				return JC_FTPClientNO;
			}
		} catch (Exception e) {
			FileHelper.writeLog(request, "FTP", "COULDN'T CONNECT TO " + ftphost + ":" + ftpport + ".");
			e.printStackTrace();
			if (fc != null) {
				try {
					fc.disconnect();
					fc = null;
				} catch (Exception e1) {
				}
			}
			return JC_FTPClientException;
		}
	}

	
	public void sftp_close() {
		if (fc != null) {
			try {
				fc.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				fc = null;
			}
		}
	}

	
	public boolean sftp_delete(HttpServletRequest request, String deletPath) {
		try {
			if (fc != null) {
				deletPath = wipespecial(deletPath);
				fc.deleteFile(deletPath);
				return true;
			} else {
				if (sftp_connect(request) > 1) {
					return sftp_delete(request, deletPath);
				} else {
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String wipespecial(String str) {
		str = str.replace("..", "").replace("\n", "").replace("\r", "");
		return str;
	}

	
	public boolean ftpUpload(HttpServletRequest request, String source, String destination) {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		if (
			sftp_connect(request) < 0) {
			return false;
		}

		
		String[] dirs = null;
		String[] temporaryDirs = destination.split("/");
		if(temporaryDirs.length > 1){
			if(temporaryDirs[0].equals("")){
				temporaryDirs[0] = "/";
			}
			if(temporaryDirs[0].equals(".")){
				temporaryDirs[0] = "/";
				int dirsL = temporaryDirs.length - 2;
				dirs = new String[dirsL];
				System.arraycopy(temporaryDirs, 1, dirs, 0, dirsL);
			}else{
				int dirsL = temporaryDirs.length - 1;
				dirs = new String[dirsL];
				System.arraycopy(temporaryDirs, 0, dirs, 0, dirsL);
			}
			destination = temporaryDirs[temporaryDirs.length-1];
		}else{
			dirs = new String[0];
		}
		for(String dir : dirs){
			if (!sftp_chdir(dir)) {
				if (!sftp_mkdir(dir)) {
					FileHelper.writeLog(request, "FTP", "MKDIR '" + dir + "' ERROR.");
					return false;
				}
				if (!sftp_chmod("0777", dir)) {
					sftp_site("'CHMOD 0777 " + dir + "'");
				}
				if (!sftp_chdir(dir)) {
					FileHelper.writeLog(request, "FTP", "CHDIR '" + dir + "' ERROR");
					return false;
				}
				sftp_put("index.htm", JavaCenterHome.jchRoot + "./data/index.htm", FTP.BINARY_FILE_TYPE, 0);
			}
		}

		if (sftp_put(destination, source, FTP.BINARY_FILE_TYPE, 0)) {
			File file = new File(source + ".thumb.jpg");
			if (file.exists()) {
				if (sftp_put(destination + ".thumb.jpg", source + ".thumb.jpg", FTP.BINARY_FILE_TYPE, 0)) {
					file.delete();
					new File(source).delete();
					sftp_close();
					return true;
				} else {
					sftp_delete(request, destination);
				}
			} else {
				new File(source).delete();
				sftp_close();
				return true;
			}
		}
		FileHelper.writeLog(request, "FTP", "Upload '" + source + "' To '" + destination + "' error.");
		return false;
	}

	
	public boolean sftp_put(String remoteFile, String localFile, int mode, int startPos) {
		remoteFile = wipespecial(remoteFile);
		localFile = wipespecial(localFile);
		InputStream inputStream = null;
		boolean flag = false;
		try {
			inputStream = new FileInputStream(localFile);
			if (startPos > 0) {
				inputStream.skip(startPos - 1);
			}
			fc.setFileType(mode);
			flag = fc.storeFile(remoteFile, inputStream);
		} catch (IOException e) {
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return flag;
	}

	
	public boolean sftp_chmod(String mode, String filename) {
		int mod = Common.intval(mode);
		filename = wipespecial(filename);
		return sftp_site("CHMOD " + mod + " " + filename);
	}

	
	public boolean sftp_site(String cmd) {
		boolean flag = false;
		cmd = wipespecial(cmd);
		try {
			flag = fc.sendSiteCommand(cmd);
		} catch (IOException e) {
		}
		return flag;
	}

	
	public boolean sftp_mkdir(String directory) {
		directory = wipespecial(directory);
		try {
			return fc.makeDirectory(directory);
		} catch (IOException e) {
			return false;
		}
	}

	
	public boolean sftp_chdir(String directory) {
		directory = wipespecial(directory);
		try {
			return fc.changeWorkingDirectory(directory);
		} catch (IOException e) {
			return false;
		}
	}
}
