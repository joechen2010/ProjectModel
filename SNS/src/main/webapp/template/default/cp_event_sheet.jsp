<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>${event.title} 签到表</title>
	<meta http-equiv="content-type" content="text/html; charset="${jchConfig.charset }" />
	<meta http-equiv="x-ua-compatible" content="ie=7" />
	<style type="text/css">
		body{font-size: 12px;}
		h1{text-align:center; font-size: 18px;}
		table{width: 98%; background:#DDD;text-align:left;}
		table tr{}
		table th{ background: #EEE; }
		table td{ background: #FFF; }
	</style>
</head>
<body>
	<h1>${event.title} 签到表</h1>
	<table border="0" cellpadding="5" cellspacing="1">
		<tbody>
			<tr>
				<c:if test="${!jch:jchEmpty(param.avatarbig)}"><th>头像</th></c:if>
				<c:if test="${!jch:jchEmpty(param.avatarsmall)}"><th>头像</th></c:if>
				<c:if test="${!jch:jchEmpty(param.username)}"><th>姓名</th></c:if>
				<c:if test="${!jch:jchEmpty(param.fellow)}"><th>携带人数</th></c:if>
				<c:if test="${!jch:jchEmpty(param.template)}"><th>报名信息</th></c:if>
				<th>签到</th>
			</tr>
			<c:forEach items="${members}" var="value">
			<tr>
				<c:if test="${!jch:jchEmpty(param.avatarbig)}"><td>${jch:avatar2(value.uid,'big',false,sGlobal, sConfig)}</td></c:if>
				<c:if test="${!jch:jchEmpty(param.avatarsmall)}"><td>${jch:avatar1(value.uid,sGlobal, sConfig)}</td></c:if>
				<c:if test="${!jch:jchEmpty(param.username)}"><td>${sNames[value.uid]}<c:if test="${value.status >= 3}">（组织者）</c:if></td></c:if>
				<c:if test="${!jch:jchEmpty(param.fellow)}"><td>${value.fellow}</td></c:if>
				<c:if test="${!jch:jchEmpty(param.template)}"><td>${value.template}</td></c:if>
				<td>&nbsp;&nbsp;</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>