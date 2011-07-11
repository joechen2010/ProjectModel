<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title">标签(Tag)</h2>

<div class="album_list">
	<table cellspacing="6" cellpadding="0" width="100%">
		<tr>
		<c:forEach items="${list}" var="value" varStatus="key">
		<td class="album">
			<a href="space.jsp?uid=${space.uid}&do=tag&id=${value.tagid}">${value.tagname}</a>(${value.blognum})
		</td>
		<c:if test="${key.index%5==4}"></tr><tr></c:if>
		</c:forEach>
		</tr>
	</table>
</div>

<div class="page">${multi}</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>