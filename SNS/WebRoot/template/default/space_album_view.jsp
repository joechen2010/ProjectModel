<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${space.self}">
		<div class="searchbar floatright">
			<input name="key" value="" size="26" class="t_input" type="text">
			<input name="searchsubmit" value="搜索图片" class="submit" type="submit">
		</div>
		<h2 class="title"><img src="image/app/album.gif">相册 - ${album.albumname}</h2>
		<div class="tabs_header">
			<ul class="tabs">
				<li class="active"><a href="space.jsp?uid=${album.uid}&do=album&id=${album.albumid}"><span>查看图片列表</span></a></li>
				<li><a href="space.jsp?uid=${space.uid}&do=album&view=me"><span>返回我的相册</span></a></li>
				<li class="null"><a href="cp.jsp?ac=album&op=edit&albumid=${album.albumid}">管理相册</a></li>
				<li class="null"><a href="cp.jsp?ac=upload&albumid=${album.albumid}">上传新图片</a></li>
			</ul>
			<c:if test="${not empty sGlobal.refer}"><div class="r_option"><a href="${sGlobal.refer}">&laquo; 返回上一页</a></div></c:if>
		</div>
	</c:when>
	<c:otherwise><jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}" /></c:otherwise>
</c:choose>
<div class="h_status">
	<a href="cp.jsp?ac=share&type=album&id=${album.albumid}" id="a_share" onclick="ajaxmenu(event, this.id, 1)" class="a_share">分享</a>
	<div class="r_option"><c:if test="${sGlobal.supe_uid==album.uid || managealbum}"><a href="admincp.jsp?ac=album&albumid=${album.albumid}" target="_blank">删除</a><span class="pipe">|</span></c:if><a href="cp.jsp?ac=common&op=report&idtype=albumid&id=${album.albumid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">举报</a><span class="pipe">|</span></div>
	${album.albumname} -
	<c:if test="${album.picnum>0}">共 ${album.picnum} 张图片</c:if>
	<c:if test="${album.friend>0}"><span class="locked gray">${friendsName[value.friend]}</span></c:if>
</div>
<c:choose>
	<c:when test="${not empty list}">
		<table cellspacing="6" cellpadding="0" width="100%" class="photo_list">
			<tr>
				<c:forEach items="${list}" var="value" varStatus="key">
					<td align="center"><a href="space.jsp?uid=${value.uid}&do=${do}&picid=${value.picid}"><img src="${value.pic}" alt="" /></a></td>
					${key.index%4 == 3 ? "</tr><tr>" : ""}
				</c:forEach>
			</tr>
		</table>
		<div class="page">${multi}</div>
	</c:when>
	<c:otherwise><div class="c_form">该相册下还没有图片。</div></c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />