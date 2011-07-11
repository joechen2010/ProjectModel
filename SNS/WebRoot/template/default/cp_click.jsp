<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:if test="${op == 'show'}">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_click.jsp')}"/>
</c:if>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>