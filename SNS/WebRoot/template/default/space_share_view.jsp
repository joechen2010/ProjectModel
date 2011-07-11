<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${space.self}">
<h2 class="title"><img src="image/app/share.gif" />分享</h2>
<div class="tabs_header">
	<ul class="tabs">
		<c:if test="${space.friendnum>0}"><li${actives.we}><a href="space.jsp?uid=${space.uid}&do=share&view=we"><span>好友的分享</span></a></li></c:if>
		<li${actives.all}><a href="space.jsp?uid=${space.uid}&do=share&view=all"><span>大家的分享</span></a></li>
		<li class="active"><a href="space.jsp?uid=${space.uid}&do=share&view=me"><span>我的分享</span></a></li>
	</ul>
	<c:if test="${not empty sGlobal.refer}">
	<div class="r_option">
		<a  href="${sGlobal.refer}">&laquo; 返回上一页</a>
	</div>
	</c:if>
</div>
</c:when>
<c:otherwise>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}"/>
</c:otherwise>
</c:choose>

<div id="share_ul">
	<div class="title">
		<div class="r_option">
			<c:if test="${sGlobal.supe_uid==share.uid || manageshare}">
			<a href="cp.jsp?ac=topic&op=join&id=${share.sid}&idtype=sid" id="a_topicjoin_${share.sid}" onclick="ajaxmenu(event, this.id)">凑热闹</a><span class="pipe">|</span>
			<a href="cp.jsp?ac=share&sid=${share.sid}&op=delete&type=view" id="share_delete_${share.sid}" onclick="ajaxmenu(event, this.id)">删除</a><span class="pipe">|</span>
			</c:if>
			<c:if test="${manageshare}">
			<a href="cp.jsp?ac=share&sid=${share.sid}&op=edithot" id="a_hot_${share.sid}" onclick="ajaxmenu(event, this.id)">热度</a><span class="pipe">|</span>
			</c:if>
			<a href="cp.jsp?ac=common&op=report&idtype=sid&id=${share.sid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">举报</a>
		</div>
		<h1>
			${share.title_template}
		</h1>
		<p style="padding:5px 0;">
			<c:if test="${share.hot>0}"><span class="hot"><em>热</em>${share.hot}</span></c:if>
			<jch:date dateformat="yyyy-MM-dd HH:mm" timestamp="${share.dateline}" format="1"/>
		</p>
	</div>
	<div id="share_article" class="article">

	<c:if test="${not empty topic}">
	<div class="r_option">
		<img src="image/app/topic.gif" align="absmiddle">
		<strong>凑个热闹</strong>：<a href="space.jsp?do=topic&topicid=${topic.topicid}">${topic.subject}</a>
	</div>
	</c:if>


	<c:if test="${!jch:jchEmpty(share.image)}">
	<div id="share_image" style="padding:10px 0;">
		<a href="${share.image_link}"><img src="${share.image}" class="summaryimg image" alt="" /></a>
	</div>
	</c:if>



	${share.body_template}

	<c:choose>
	<c:when test="${'video'==share.type}">
	<div class="box" id="flash_div_${share.sid}">
		<script>showFlash('${share.body_data.host}', '${share.body_data.flashvar}', '', '${share.sid}');</script>
	</div>
	</c:when>
	<c:when test="${'music'==share.type}">
	<div class="box" id="flash_div_${share.sid}">
		<script>showFlash('music', '${share.body_data.musicvar}', '', '${share.sid}');</script>
	</div>
	</c:when>
	<c:when test="${'flash'==share.type}">
	<div class="box" id="flash_div_${share.sid}">
		<script>showFlash('flash', '${share.body_data.flashaddr}', '', '${share.sid}');</script>
	</div>
	</c:when>
	</c:choose>

	</div>
	<div class="quote"><span class="q">${share.body_general}</span></div>
</div>
<div class="page">${multi}</div>
<div class="comments_list" id="comment">

	<c:if test="${cid>0}">
	<div class="notice">
		当前只显示与你操作相关的单个评论，<a href="space.jsp?uid=${share.uid}&do=share&id=${share.sid}">点击此处查看全部评论</a>
	</div>
	</c:if>

	<ul id="comment_ul">
	<c:forEach items="${list}" var="value">
	<c:set var="comment" value="${value}" scope="request"></c:set>
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_comment_li.jsp')}"/>
	</c:forEach>
	</ul>
</div>
<div class="page">${multi}</div>

<c:if test="${jch:jchEmpty(share.noreply)}">
<form id="quickcommentform_${id}" name="quickcommentform_${id}" action="cp.jsp?ac=comment" method="post" class="quickpost">
<table cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<a href="###" id="comment_face" onclick="showFace(this.id, 'comment_message');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
			<c:if test="${not empty globalMagic.doodle}">
			<a id="a_magic_doodle" href="magic.jsp?mid=doodle&showid=comment_doodle&target=comment_message" onclick="ajaxmenu(event, this.id, 1)"><img src="image/magic/doodle.small.gif" class="magicicon" />涂鸦板</a>
			</c:if>
			<br />
			<textarea id="comment_message" name="message" rows="5" onkeydown="ctrlEnter(event, 'commentsubmit_btn');" style="width:380px;"></textarea>
		</td>
	</tr>
	<tr>
		<td>
			<input type="hidden" name="refer" value="space.jsp?uid=${share.uid}&do=${do}&id=${id}" />
			<input type="hidden" name="id" value="${id}">
			<input type="hidden" name="idtype" value="sid">
			<input type="hidden" name="commentsubmit" value="true" />
			<input type="button" id="commentsubmit_btn" name="commentsubmit_btn" class="submit" value="评论" onclick="ajaxpost('quickcommentform_${id}', 'comment_add')" />
			<span id="__quickcommentform_${id}"></span>
		</td>
	</tr>
</table>
<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</c:if>

<script type="text/javascript">
<c:if test="${!jch:jchEmpty(share.image)}">
resizeImg('share_image','500');
</c:if>
var elems = selector('div[class~=magicflicker]'); 
for(var i=0; i<elems.length; i++){
	magicColor(elems[i]);
}

</script>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>