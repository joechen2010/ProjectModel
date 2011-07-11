<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="cn.jcenterhome.util.PropertiesHelper"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%@page import="cn.jcenterhome.service.SpaceService"%>
<%@page import="cn.jcenterhome.util.CookieHelper"%>
<%@page import="cn.jcenterhome.util.FileHelper"%>
<%@page import="cn.jcenterhome.util.SessionFactory"%>
<%@ include file="install_method.jsp"%>
<%
	try{
		String jchRoot = JavaCenterHome.jchRoot;
		File configFile = new File(jchRoot+ "config.properties");
		if(configFile.exists()){
			initConfig(request);
			JavaCenterHome.clearTableNames();
		}else{
			showMessage("您需要首先将 config.properties 系统配置文件上传到程序的根目录下，再重新运行本程序", 999, 0, request, response);
			return;
		}
		long currentTime = System.currentTimeMillis();
		int timestamp = (int) (currentTime / 1000);
		Map<String, Object> sGlobal = new HashMap<String, Object>();
		sGlobal.put("timestamp", timestamp);
		request.setAttribute("sGlobal", sGlobal);
		Map<String, String> sCookie = CookieHelper.getCookies(request);
		Map<Integer, String> sNames = new HashMap<Integer, String>();
		Map<String, Object> space = new HashMap<String, Object>();
		request.setAttribute("sCookie", sCookie);
		request.setAttribute("sNames", sNames);
		request.setAttribute("space", space);
		String formHash = Common.formHash(sGlobal, new HashMap(), false);
		request.setAttribute("formHash", formHash);
		int step = Common.intval(request.getParameter("step"));
		request.setAttribute("step",step);
		String theURL = "index.jsp";
		request.setAttribute("theurl", theURL);
		File sqlFile = new File(jchRoot + "/install/install.sql");
		if (!sqlFile.exists()) {
			showMessage("请上传最新的 install.sql 数据库结构文件到程序的 /install 目录下面，再重新运行本程序", 999, 0, request, response);
			return;
		}

		File lockFile = new File(jchRoot + "/data/install.lock");
		if (lockFile.exists()) {
			showMessage("警告!您已经安装过JavaCenter Home<br> 为了保证数据安全，请立即手动删除 install 目录下所有文件<br> 如果您想重新安装JavaCenter Home，请删除 data/install.lock 文件，再运行安装文件", 0, 0, request, response);
			return;
		}
		if (!configFile.canWrite()) {
			showMessage("文件 " + configFile.getPath() + " 读写权限设置错误，请设置为可写，再执行安装程序", 0, 0, request, response);
			return;
		}
		if ("POST".equals(request.getMethod()) && !Common.empty(request.getParameter("sqlsubmit"))) {
			step=2;
			request.setAttribute("step",step-1);
			Map<String, String> dbInfo = new HashMap();
			for (Enumeration paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
				String key = (String) paramNames.nextElement();
				if (key.startsWith("db[")) {
					String k = key.replaceAll("db\\[([a-zA-Z]+)\\]", "$1");
					dbInfo.put(k, request.getParameter(key));
				}
			}
			if(Common.empty(dbInfo.get("tablePre"))) {
				showMessage("填写的表名前缀不能为空", 0, 0, request, response);
				return;
			}
			PropertiesHelper p = new PropertiesHelper(configFile.toString());
			p.setCharset(JavaCenterHome.JCH_CHARSET);
			for (Map.Entry<String, String> entry : dbInfo.entrySet()) {
				p.setValue(entry.getKey(),entry.getValue());
			}
			p.saveProperties();
			
			boolean haveData = false;
			if(!mysql_connect(dbInfo.get("dbHost"), dbInfo.get("dbPort"),null,dbInfo.get("dbUser"), dbInfo.get("dbPw"),"")) {
				showMessage("数据库连接信息填写错误，请确认", 0, 0, request, response);
				return;
			}
			if(mysql_select_db(dbInfo.get("dbName"))) {
				ResultSet resultSet=mysql_query("SELECT COUNT(*) FROM "+dbInfo.get("tablePre")+"space");
				if(resultSet!=null && resultSet.next()) {
					haveData = true;
					resultSet.close();
				}
			} else {
				if(!mysql_update("CREATE DATABASE `"+dbInfo.get("dbName")+"`")) {
					showMessage("设定的JavaCenter Home数据库无权限操作，请先手工操作后，再执行安装程序", 0, 0, request,response);
					return;
				}
			}
			if(haveData) {
				showMessage("危险!指定的JavaCenter Home数据库已有数据，如果继续将会清空原有数据!", step, 0, request,response);
				return;
			} else {
				showMessage("数据库配置成功，进入下一步操作", step, 1, request,response);
				return;
			}
		}
		else if (submitcheck(request,response,"opensubmit")) {
			Map<String, Object> sConfig = Common.getCacheDate(request,response,"/data/cache/cache_config.jsp", "sConfig");
			step =4;
			request.setAttribute("step",step);
			String userName = Common.trim(request.getParameter("username"));
			String password = Common.trim(request.getParameter("password"));
			String email=request.getParameter("email");
			int result = checkName(userName,request,response);
			if (result == -1) {
				showMessage(Common.getMessage(request,"user_name_is_not_legitimate"), 0, 0, request,response);
				return;
			}
			String salt = Common.getRandStr(6, false);
			password = Common.md5(Common.md5(password) + salt);
			Map<String, Object> insertData = new HashMap<String, Object>();
			insertData.put("username", userName);
			insertData.put("password", password);
			insertData.put("blacklist", "");
			insertData.put("salt", salt);
			DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
			int newUid = dataBaseService.insertTable("member", insertData, true, false);
			if (newUid <= 0) {
				showMessage(Common.getMessage(request,"register_error"), 0, 0, request,response);
				return;
			}
			SpaceService spaceService = (SpaceService) BeanFactory.getBean("spaceService");
			sGlobal.put("supe_uid",newUid);
			spaceService.openSpace(request, response, sGlobal, sConfig, newUid, userName, 1, email);
			dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("space")+" SET flag=1 WHERE username='"+userName+"'");
			spaceService.insertSession(request, response, sGlobal, sConfig, newUid, userName,password);
			CookieHelper.setCookie(request, response, "auth", Common.authCode(password + "\t"+ newUid, "ENCODE", null, 0), 2592000);
			CookieHelper.setCookie(request, response, "loginuser", userName, 31536000);
			CookieHelper.removeCookie(request, response, "_refer");
			FileHelper.writeFile(lockFile,"JavaCenterHome");
			showMessage("<font color=\"red\">恭喜! JavaCenter Home安装全部完成!</font><br>为了您的数据安全，请登录ftp，删除install目录<br><br>	您的管理员身份已经成功确认，并已经开通空间。接下来，您可以：<br><br><a href=\"../index.jsp\" target=\"_blank\">进入空间首页</a><br>进入空间首页，开始JavaCenter Home之旅<br><a href=\"../admincp.jsp\" target=\"_blank\">进入管理平台</a><br>以管理员身份对站点参数进行设置", 999, 0, request,response);
			return;
		}
		if(step==2){
			request.setAttribute("sqlFile",sqlFile.getPath());
		}
	}catch(Exception e){
		String message=getErrorMessage();
		if(message==null){
			message=e.getMessage();
		}
		showMessage(message, 999, 0, request,response);
		return;
	}finally{
		mysql_close();
	}
%>
<c:choose>
	<c:when test="${step==0}"><jsp:forward page="install_step_1.jsp"></jsp:forward></c:when>
	<c:when test="${step==1}"><jsp:forward page="install_step_2.jsp"></jsp:forward></c:when>
	<c:when test="${step==2}"><jsp:forward page="install_step_3.jsp"></jsp:forward></c:when>
	<c:when test="${step==3}"><jsp:forward page="install_step_4.jsp"></jsp:forward></c:when>
	<c:when test="${step==4}"><jsp:forward page="install_step_5.jsp"></jsp:forward></c:when>
</c:choose>