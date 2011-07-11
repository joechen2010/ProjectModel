<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${space.self}">
		<div class="searchbar floatright">
			<form method="get" action="space.jsp">
				<input name="searchkey" value="" size="15" class="t_input" type="text">
				<input name="searchsubmit" value="搜索相册" class="submit" type="submit">
				<input type="hidden" name="do" value="album" />
				<input type="hidden" name="view" value="all" />
				<input type="hidden" name="orderby" value="dateline" />
			</form>
		</div>
		<h2 class="title"><img src="image/app/album.gif">相册 - ${album.albumname}</h2>
		<div class="tabs_header">
			<ul class="tabs">
				<li class="active"><a href="space.jsp?uid=${pic.uid}&do=album&id=${pic.albumid}"><span>查看图片</span></a></li>
				<li><a href="space.jsp?uid=${space.uid}&do=album&id=${pic.albumid}"><span>返回列表</span></a></li>
				<li><a href="space.jsp?uid=${space.uid}&do=album&view=me"><span>返回我的相册</span></a></li>
				<li class="null"><a href="cp.jsp?ac=upload&albumid=${pic.albumid}">上传新图片</a></li>
			</ul>
			<c:if test="${not empty sGlobal.refer}"><div class="r_option"><a href="${sGlobal.refer}">&laquo; 返回上一页</a></div></c:if>
		</div>
	</c:when>
	<c:otherwise><jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}" /></c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_pic.jsp')}" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />