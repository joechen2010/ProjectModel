<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:if test="${jch:jchEmpty(ajax_edit)}"><li id="share_${share.sid}_li"></c:if>
	<div class="title">
		<div class="r_option">
		<c:if test="${share.sid>0 && (sGlobal.supe_uid==share.uid || jch:checkPerm(pageContext.request, pageContext.response,'manageshare'))}">
		<a href="cp.jsp?ac=topic&op=join&id=${share.sid}&idtype=sid" id="a_topicjoin_${share.sid}" onclick="ajaxmenu(event, this.id)">凑热闹</a><span class="pipe">|</span>
		</c:if>
		<c:if test="${share.sid>0}"><a href="space.jsp?uid=${share.uid}&do=share&id=${share.sid}">评论</a>&nbsp;</c:if>
		<c:if test="${share.uid==sGlobal.supe_uid}"><c:if test="${share.type=='link' || share.type=='video' || share.type=='music'}"><span class="pipe">|</span></c:if><a href="cp.jsp?ac=share&op=delete&sid=${share.sid}" id="s_${share.sid}_delete" onclick="ajaxmenu(event, this.id)">删除</a></c:if>
		</div>
		<a href="space.jsp?uid=${share.uid}">${sNames[share.uid]}</a> <a href="space.jsp?uid=${share.uid}&do=share&id=${share.sid}">${share.title_template}</a>
		&nbsp;<span class="gray"><jch:date dateformat="yyyy-MM-dd HH:mm" timestamp="${share.dateline}" format="1"/></span>
	</div>
	<div class="feed">
	<div style="width:100%;overflow:hidden;">
	<c:if test="${!jch:jchEmpty(share.image)}">
	<a href="${share.image_link}"><img src="${share.image}" class="summaryimg image" alt="" width="70" /></a>
	</c:if>
	<div class="detail">
	${share.body_template}
	</div>
	<c:choose>
	<c:when test="${'video'==share.type}">
	<div class="media">
	<img src="image/vd.gif" alt="点击播放" onclick="javascript:showFlash('${share.body_data.host}', '${share.body_data.flashvar}', this, '${share.sid}');" style="cursor:pointer;" />
	</div>
	</c:when>
	<c:when test="${'music'==share.type}">
	<div class="media">
	<img src="image/music.gif" alt="点击播放" onclick="javascript:showFlash('music', '${share.body_data.musicvar}', this, '${share.sid}');" style="cursor:pointer;" />
	</div>
	</c:when>
	<c:when test="${'flash'==share.type}">
	<div class="media">
	<img src="image/flash.gif" alt="点击查看" onclick="javascript:showFlash('flash', '${share.body_data.flashaddr}', this, '${share.sid}');" style="cursor:pointer;" />
	</div>
	</c:when>
	</c:choose>
	<div class="quote"><span id="quote" class="q">${share.body_general}</span></div>
	</div>
	</div>
<c:if test="${jch:jchEmpty(ajax_edit)}"></li></c:if>