package cn.jcenterhome.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	private final int BUFFER = 2048;

	private int decompress_count = 0;
	private int decompress_file_count = 0;
	private int decompress_folder_count = 0;

	
	public boolean compress(String zipFileName, File sourceFile) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFileName);
			zos = new ZipOutputStream(fos);
			if (!zip(zos, sourceFile, sourceFile.getName())) {
				throw new Exception();
			}
			zos.close();
		} catch (Exception e) {
			try {
				if (fos != null)
					fos.close();
				if (zos != null)
					zos.close();
			} catch (IOException ioe) {
			}
			deleteDir(new File(zipFileName));
			return false;
		}
		return true;
	}

	
	public boolean compress(String zipFileName, File[] files) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFileName);
			zos = new ZipOutputStream(fos);
			for (File f : files) {
				if (!zip(zos, f, f.getName())) {
					throw new Exception();
				}
			}
			zos.close();
		} catch (Exception e) {
			try {
				if (fos != null)
					fos.close();
				if (zos != null)
					zos.close();
			} catch (IOException ioe) {
			}
			deleteDir(new File(zipFileName));
			return false;
		}
		return true;
	}

	
	@SuppressWarnings("unchecked")
	public boolean decompress(ZipFile zipFile, String toDir, String fileSuffix) {
		decompress_count = 0;
		decompress_file_count = 0;
		decompress_folder_count = 0;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			String separator = File.separator;
			Enumeration e = zipFile.entries();
			ZipEntry entry = null;
			byte[] datas = null;
			while (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();
				if (fileSuffix != null && !entry.getName().endsWith(fileSuffix)) {
					continue;
				}
				if (entry.isDirectory()) {
					File dir = new File(toDir + separator + entry.getName());
					dir.mkdirs();
					decompress_folder_count++;
				} else {
					File file = new File(toDir + separator + entry.getName());
					bis = new BufferedInputStream(zipFile.getInputStream(entry));
					bos = new BufferedOutputStream(new FileOutputStream(file));
					int byteNum;
					datas = new byte[BUFFER];
					while ((byteNum = bis.read(datas, 0, BUFFER)) != -1) {
						bos.write(datas, 0, byteNum);
					}
					bos.flush();
					bos.close();
					bis.close();
					decompress_file_count++;
				}
				decompress_count++;
			}
		} catch (Exception e) {
			try {
				if (bos != null)
					bos.close();
				if (bis != null)
					bis.close();
			} catch (IOException ioe) {
			}
			deleteDir(new File(toDir));
			return false;
		}
		return true;
	}

	
	@SuppressWarnings("unchecked")
	public boolean decompress(ZipFile zipFile, String toDir) {
		return decompress(zipFile, toDir, null);
	}

	
	public void entryToFile(ZipFile zipFile, ZipEntry entry, File file) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(zipFile.getInputStream(entry));
			bos = new BufferedOutputStream(new FileOutputStream(file));
			int byteNum;
			byte[] datas = new byte[BUFFER];
			while ((byteNum = bis.read(datas, 0, BUFFER)) != -1) {
				bos.write(datas, 0, byteNum);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (bis != null)
					bis.close();
			} catch (IOException ioe) {
			}
		}
	}

	
	public String getEntryContent(ZipFile zipFile, ZipEntry entry) {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(zipFile.getInputStream(entry));
			StringBuffer strBuf = new StringBuffer();
			byte[] datas = new byte[BUFFER];
			while (bis.read(datas, 0, BUFFER) != -1) {
				strBuf.append(new String(datas));
			}
			return strBuf.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null)
					bis.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public ZipEntry getFirstEntry(ZipFile zipFile) {
		Enumeration e = zipFile.entries();
		if (e.hasMoreElements()) {
			return (ZipEntry) e.nextElement();
		}
		return null;
	}

	
	public int getDecompressCount() {
		return decompress_count;
	}

	
	public int getDecompressFileCount() {
		return decompress_file_count;
	}

	
	public int getDecompressFolderCount() {
		return decompress_folder_count;
	}

	
	private boolean zip(ZipOutputStream zos, File sourceFile, String entryPath) {
		if (entryPath == null) {
			entryPath = "";
		}
		BufferedInputStream bis = null;
		try {
			if (sourceFile.isDirectory()) {
				if (entryPath.length() != 0) {
					entryPath += "/";
				}
				ZipEntry entry = new ZipEntry(entryPath);
				zos.putNextEntry(entry);
				File[] folder = sourceFile.listFiles();
				for (File file : folder) {
					if (!zip(zos, file, entryPath + file.getName())) {
						throw new Exception();
					}
				}
			} else {
				ZipEntry entry = new ZipEntry(entryPath);
				zos.putNextEntry(entry);
				FileInputStream fis = new FileInputStream(sourceFile);
				bis = new BufferedInputStream(fis, BUFFER);
				int byteNum;
				byte[] datas = new byte[BUFFER];
				while ((byteNum = bis.read(datas, 0, BUFFER)) != -1) {
					zos.write(datas, 0, byteNum);
				}
				bis.close();
			}
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (bis != null)
					bis.close();
			} catch (IOException ioe) {
			}
		}
		return true;
	}

	
	private void deleteDir(File del) {
		if (del != null && del.exists()) {
			if (del.isDirectory()) {
				File[] files = del.listFiles();
				for (File f : files) {
					if (f.isFile()) {
						f.delete();
					} else {
						deleteDir(f);
					}
				}
			} else {
				del.delete();
			}
		}
	}
}
