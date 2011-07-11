<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:set var="tpl_noSideBar" value="1" scope="request"/>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<div class="showmessage">
	<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
		<caption><h2>信息提示</h2></caption>
		<p>${message}</p>
		<p class="op"><c:choose>
			<c:when test="${not empty url_forward}"><a href="${url_forward}">页面跳转中...</a></c:when>
			<c:otherwise><a href="javascript:history.go(-1);">返回上一页</a> | <a href="index.jsp">返回首页</a></c:otherwise>
		</c:choose></p>
	</div></div></div></div>
</div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>