<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="cn.jcenterhome.util.SessionFactory"%>
<%@page import="cn.jcenterhome.service.CacheService"%>
<%@ include file="install_method.jsp"%>
<%
	try {
		SessionFactory.rebuildSessionFactory();
	} catch (SQLException e) {
		Common.showMySQLMessage(response, "Can not connect to MySQL server", null, e);
		return;
	}
	try {
		CacheService cacheService = (CacheService) BeanFactory.getBean("cacheService");
		cacheService.config_cache();
		cacheService.usergroup_cache();
		cacheService.profilefield_cache();
		cacheService.profield_cache();
		cacheService.censor_cache();
		cacheService.block_cache();
		cacheService.eventclass_cache();
		cacheService.magic_cache();
		cacheService.click_cache();
		cacheService.task_cache();
		cacheService.ad_cache();
		cacheService.creditrule_cache();
		cacheService.userapp_cache();
		cacheService.app_cache();
		cacheService.network_cache();
	} catch (Exception e) {
		String message = e.getMessage();
		if (message == null) {
			message = e.getCause().getMessage();
		}
		out.print("All the cache update fails：<br>" + message);
		return;
	}
%>
<jsp:include page="install_header.jsp"></jsp:include>
<form method="post" action="${theurl}">
	<table>
		<tr>
			<td colspan="2">
				程序数据安装完成!<br><br>
				最后，请输入用户名和密码<br>
				系统将自动为您开通站内第一个空间，并将您设为管理员!
			</td>
		</tr>
		<tr>
			<td>您的用户名</td>
			<td><input type="text" name="username" value="" size="30"></td>
		</tr>
		<tr>
			<td>您的密码</td>
			<td><input type="password" name="password" value="" size="30"></td>
		</tr>
		<tr>
			<td>您的Email</td>
			<td><input type="text" name="email" value="webmastor@yourdomain.com" size="30"></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" name="opensubmit" value="开通管理员空间"></td>
		</tr>
	</table>
	<input type="hidden" name="formhash" value="${formHash}">
</form>
<jsp:include page="install_footer.jsp"></jsp:include>