<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title"><img src="image/app/doing.gif">记录</h2>
<div class="tabs_header">
	<ul class="tabs">
		<c:if test="${space.self}">
		<c:if test="${space.friendnum>0}"><li><a href="space.jsp?uid=${space.uid}&do=doing&view=we"><span>最新好友记录</span></a></li></c:if>
		<li><a href="space.jsp?uid=${space.uid}&do=doing&view=all"><span>大家的记录</span></a></li>
		<li><a href="space.jsp?uid=${space.uid}&do=doing&view=me"><span>我的记录</span></a></li>
		</c:if>
		<li class="active"><a href="space.jsp?uid=${space.uid}&do=mood&view=all"><span>同心情的朋友</span></a></li>
	</ul>
</div>

<div class="h_status">
	他们现在与你有着同样的心情
</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'space_list.jsp')}"/>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>