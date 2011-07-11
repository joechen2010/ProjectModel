<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="bdrcontent">
	<div class="message"><c:choose>
		<c:when test="${empty url_forward}">${message}</c:when>
		<c:otherwise><a href="${url_forward}">${message}</a><script>setTimeout("window.location.href='${url_forward}';", ${second * 1000});</script></c:otherwise>
	</c:choose></div>
</div>
<div class="footactions">
	&nbsp;
	<c:if test="${not empty url_forward}"><a href="${url_forward}">确定</a> &nbsp;</c:if>
	<a href="javascript:history.back(-1)">返回上一页</a> &nbsp;
	<a href="admincp.jsp">管理首页</a>
</div>
<jsp:directive.include file="footer.jsp" />