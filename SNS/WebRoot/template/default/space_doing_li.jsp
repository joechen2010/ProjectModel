<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<ol><c:forEach items="${list}" var="value">
	<c:if test="${value.uid>0}"><li style="${value.style}">
		<a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a>: ${value.message}
		<span class="doingtime">(<jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1" />)</span>
		<a href="javascript:;" onclick="docomment_form(${value.doid}, ${value.id});" class="re">回复</a>
		<c:if test="${value.uid==sGlobal.supe_uid || dv.uid==sGlobal.supe_uid}"><a href="cp.jsp?ac=doing&op=delete&doid=${value.doid}&id=${value.id}" id="doing_delete_${value.doid}_${value.id}" onclick="ajaxmenu(event, this.id)" class="gray">删除</a></c:if>
		<span id="docomment_form_${value.doid}_${value.id}"></span>
	</li></c:if>
</c:forEach></ol>