package cn.jcenterhome.web.action.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.web.action.BaseAction;


public class TemplateAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int supe_uid = (Integer) sGlobal.get("supe_uid");
		if (Common.empty(JavaCenterHome.jchConfig.get("allowedittpl"))
				|| !Common.checkPerm(request, response, "managetemplate") || !Common.ckFounder(supe_uid)) {
			return showMessage(request, response, "cp_no_authority_management_operation_edittpl");
		}
		String tpldir = JavaCenterHome.jchRoot + "./template/default/";
		try {
			if (submitCheck(request, "editsubmit")) {
				String filename = checkFileName(request.getParameter("filename"), tpldir);
				String filefullname = tpldir + filename;
				String d_file = filefullname + ".bak";
				File source = new File(filefullname);
				File destination = new File(d_file);
				if (!destination.exists()) {
					copyFile(source, destination);
				}

				String content = request.getParameter("content");
				FileHelper.writeFile(source, Common.stripSlashes(content));
				return showMessage(request, response, "do_success", "admincp.jsp?ac=template");
			}
		} catch (Exception e) {
			return showMessage(request, response, e.getMessage());
		}
		String op = request.getParameter("op");
		if (Common.empty(op)) {
			Map<String, List<String[]>> tpls = new LinkedHashMap<String, List<String[]>>();
			List<String[]> subList;
			File tpldirFile = new File(tpldir);
			if (tpldirFile.isDirectory()) {
				File[] subFileList = tpldirFile.listFiles();
				for (File subFile : subFileList) {
					String subFileName = subFile.getName();
					if (subFile.isFile() && subFileName.endsWith(".jsp")) {
						int status = 0;
						if (new File(tpldirFile, subFileName + ".bak").isFile()) {
							status = 1;
						}
						int pos = subFileName.indexOf("_");
						if (pos >= 0) {
							subList = tpls.get(subFileName.substring(0, pos));
							if (subList == null) {
								subList = new ArrayList<String[]>();
								tpls.put(subFileName.substring(0, pos), subList);
							}
							subList.add(new String[] {subFileName, String.valueOf(status)});
						} else {
							subList = tpls.get("base");
							if (subList == null) {
								subList = new ArrayList<String[]>();
								tpls.put("base", subList);
							}
							subList.add(new String[] {subFileName, String.valueOf(status)});
						}
					}
				}
			}
			request.setAttribute("tpls", tpls);
		} else if (op.equals("edit")) {
			String filename;
			try {
				filename = checkFileName(request.getParameter("filename"), tpldir);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			String filefullname = tpldir + filename;
			String content = (Common.htmlSpecialChars(FileHelper.readFile(filefullname))).trim();
			request.setAttribute("content", content);
			request.setAttribute("filename", filename);
		} else if (op.equals("repair")) {
			String filename;
			try {
				filename = checkFileName(request.getParameter("filename"), tpldir);
			} catch (Exception e) {
				return showMessage(request, response, e.getMessage());
			}
			String filefullname = tpldir + filename;
			String d_file = filefullname + ".bak";
			File backFile = new File(d_file);
			if (backFile.exists()) {
				try {
					copyFile(backFile, new File(filefullname));
				} catch (IOException e) {
					return showMessage(request, response, e.getMessage());
				}
				backFile.delete();
			} else {
				return showMessage(request, response, "cp_designated_template_files_can_not_be_restored");
			}
			return showMessage(request, response, "do_success", "admincp.jsp?ac=template");
		}
		request.setAttribute("op", op);
		return mapping.findForward("template");
	}

	private String checkFileName(String fileName, String tplDir) throws Exception {
		boolean isedit = false;
		if (!Common.empty(fileName)) {
			fileName = fileName.replace("..", "").replace("/", "").replace("\\", "");
			if (!Common.empty(fileName) && Common.fileext(fileName).equals("jsp")) {
				File file = new File(tplDir, fileName);
				if (file.canWrite()) {
					isedit = true;
				}
			}
		}
		if (!isedit) {
			throw new Exception("cp_template_files_editing_failure_check_directory_competence");
		}
		return fileName;
	}

	private void copyFile(File source, File destination) throws IOException {

		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileInputStream = new FileInputStream(source);
			fileOutputStream = new FileOutputStream(destination);
			int bufferLength = 1024;
			byte[] buffer = new byte[bufferLength];
			int readCount = 0;
			while ((readCount = fileInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, readCount);
			}
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
	}
}