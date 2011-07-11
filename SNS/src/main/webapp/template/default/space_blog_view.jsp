<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:choose>
<c:when test="${space.self}">
<h2 class="title"><img src="image/app/blog.gif" />日志</h2>
<div class="tabs_header">
	<ul class="tabs">
		<c:if test="${space.friendnum>0}"><li${actives.we}><a href="space.jsp?uid=${space.uid}&do=${do}&view=we"><span>好友最新日志</span></a></li></c:if>
		<li class="active"><a href="space.jsp?uid=${space.uid}&do=${do}&view=me"><span>我的日志</span></a></li>
		<li><a href="space.jsp?uid=${space.uid}&do=${do}&view=click"><span>我踩过的日志</span></a></li>
		<li><a href="space.jsp?uid=${space.uid}&do=${do}&view=all"><span>大家的日志</span></a></li>
		<li class="null"><a href="cp.jsp?ac=blog">发表新日志</a></li>
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

<script type="text/javascript" charset="${jchConfig.charset }" src="source/script_calendar.js"></script>

<div class="entry" style="padding:0 0 10px;">

	<div class="title">
		<h1<c:if test="${blog.magiccolor>0}"> class="magiccolor${blog.magiccolor}"</c:if>>${blog.subject}</h1>
		<c:if test="${blog.friend>0}">
		<span class="r_option locked gray">${friendsName[blog.friend]}</span>
		</c:if>
		<c:if test="${blog.hot>0}"><span class="hot"><em>热</em>${blog.hot}</span></c:if>
		<c:if test="${blog.friend>0}">
		<span class="r_option locked gray">
		<a href="space.jsp?uid=${space.uid}&do=${do}&view=me&friend=${blog.friend}" class="gray">${friendsName[value.friend]}</a>
		</span>
		</c:if>
		<c:if test="${blog.viewnum>0}"><span class="gray">已有 ${blog.viewnum } 次阅读</span></c:if>
		&nbsp; <span class="gray"><jch:date dateformat="yyyy-MM-dd HH:mm" timestamp="${blog.dateline}" format="1"/></span>
		<c:if test="${not empty blog.tag}">
		&nbsp; <a href="space.jsp?uid=${blog.uid}&do=tag">标签</a>:&nbsp;
		<c:forEach items="${blog.tag}" var="tag">
		<a href="space.jsp?uid=${blog.uid}&do=tag&id=${tag.key}">${tag.value}</a>&nbsp;
		</c:forEach>
		</c:if>
	</div>


	<div id="blog_article" class="article<c:if test="${blog.magicpaper>0}"> magicpaper${blog.magicpaper}</c:if>">
		<div class="resizeimg">
			<div class="resizeimg2">
			<div class="resizeimg3">
			<div class="resizeimg4">
			<c:if test="${not empty globalAd.rightside}"><div style="float: right; padding:5px;">${jch:showAd(globalAd.rightside)}</div></c:if>
			${blog.message}
			</div>
			</div>
			</div>
		</div>
	</div>
</div>

<div style="padding:0 0 10px;">
	<div style="text-align: right; padding-top:10px; ">
		<a href="cp.jsp?ac=share&type=blog&id=${blog.blogid}" id="a_share" onclick="ajaxmenu(event, this.id, 1)" class="a_share">分享</a>

		<c:if test="${sGlobal.supe_uid==blog.uid}">
			<c:if test="${not empty globalMagic.bgimage}">
			<img src="image/magic/bgimage.small.gif" class="magicicon">
				<c:choose>
				<c:when test="${blog.magicpaper>0}">
			<a href="cp.jsp?ac=magic&op=cancelbgimage&idtype=blogid&id=${blog.blogid}" id="a_magic_bgimage" onclick="ajaxmenu(event,this.id, 1)">取消${globalMagic.bgimage}</a>
				</c:when>
				<c:otherwise>
			<a href="magic.jsp?mid=bgimage&idtype=blogid&id=${blog.blogid}" id="a_magic_bgimage" onclick="ajaxmenu(event,this.id, 1)">${globalMagic.bgimage}</a>	
				</c:otherwise>
				</c:choose>
			</c:if>
			<c:if test="${not empty globalMagic.call}">
			<img src="image/magic/call.small.gif" class="magicicon">
			<a href="magic.jsp?mid=call&idtype=blogid&id=${blog.blogid}" id="a_magic_call" onclick="ajaxmenu(event,this.id, 1)">${globalMagic.call}</a>
			</c:if>
			<c:if test="${not empty globalMagic.updateline}">
			<img src="image/magic/updateline.small.gif" class="magicicon">
			<a href="magic.jsp?mid=updateline&idtype=blogid&id=${blog.blogid}" id="a_magic_updateline" onclick="ajaxmenu(event,this.id, 1)">${globalMagic.updateline}</a>
			</c:if>
			<c:if test="${not empty globalMagic.downdateline}">
			<img src="image/magic/downdateline.small.gif" class="magicicon">
			<a href="magic.jsp?mid=downdateline&idtype=blogid&id=${blog.blogid}" id="a_magic_downdateline" onclick="ajaxmenu(event,this.id, 1)">${globalMagic.downdateline}</a>
			</c:if>
			<c:if test="${not empty globalMagic.color}">
			<img src="image/magic/color.small.gif" class="magicicon">
				<c:choose>
				<c:when test="${blog.magiccolor>0}">
			<a href="cp.jsp?ac=magic&op=cancelcolor&idtype=blogid&id=${blog.blogid}" id="a_magic_color" onclick="ajaxmenu(event,this.id)">取消${globalMagic.color}</a>
				</c:when>
				<c:otherwise>
			<a href="magic.jsp?mid=color&idtype=blogid&id=${blog.blogid}" id="a_magic_color" onclick="ajaxmenu(event,this.id, 1)">${globalMagic.color}</a>
				</c:otherwise>
				</c:choose>
			</c:if>
			<c:if test="${not empty globalMagic.hot}">
			<img src="image/magic/hot.small.gif" class="magicicon">
			<a href="magic.jsp?mid=hot&idtype=blogid&id=${blog.blogid}" id="a_magic_hot" onclick="ajaxmenu(event,this.id, 1)">${globalMagic.hot}</a>
			</c:if>
			<span class="pipe">|</span>
		</c:if>

		<c:if test="${sGlobal.supe_uid==blog.uid || manageBlog}">
		<a href="cp.jsp?ac=topic&op=join&id=${blog.blogid}&idtype=blogid" id="a_topicjoin_${blog.blogid}" onclick="ajaxmenu(event, this.id)">凑热闹</a><span class="pipe">|</span>
		<a href="cp.jsp?ac=blog&blogid=${blog.blogid}&op=edit">编辑</a><span class="pipe">|</span>
		<a href="cp.jsp?ac=blog&blogid=${blog.blogid}&op=delete" id="blog_delete_${blog.blogid}" onclick="ajaxmenu(event, this.id)">删除</a><span class="pipe">|</span>
		</c:if>
		<c:if test="${manageBlog}">
		<a href="cp.jsp?ac=blog&blogid=${blog.blogid}&op=edithot" id="blog_hot_${blog.blogid}" onclick="ajaxmenu(event, this.id)">热度</a><span class="pipe">|</span>
		</c:if>
		<a href="cp.jsp?ac=common&op=report&idtype=blogid&id=${blog.blogid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">举报</a>
	</div>

</div>

<div id="content">

	<div id="click_div">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_click.jsp')}"/>
	</div>

	<div class="comments" id="div_main_content">
		<h2>
			<c:if test="${blog.noreply==0}">
			<a href="#quickcommentform_${id}" class="r_option">发表评论</a>
			</c:if>
			评论 (<span id="comment_replynum">${blog.replynum}</span> 个评论)</h2>
		<div class="page">${multi}</div>
		<div class="comments_list" id="comment">
			<c:if test="${cid>0}">
			<div class="notice">
				当前只显示与你操作相关的单个评论，<a href="space.jsp?uid=${blog.uid}&do=blog&id=${blog.blogid}">点击此处查看全部评论</a>
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

		<c:if test="${blog.noreply==0}">
		<form id="quickcommentform_${id}" name="quickcommentform_${id}" action="cp.jsp?ac=comment" method="post" class="quickpost">

			<table cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<a href="###" id="comment_face" title="插入表情" onclick="showFace(this.id, 'comment_message');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
						<c:if test="${not empty globalMagic.doodle}">
						<a id="a_magic_doodle" href="magic.jsp?mid=doodle&showid=comment_doodle&target=comment_message" onclick="ajaxmenu(event, this.id, 1)"><img src="image/magic/doodle.small.gif" class="magicicon" />涂鸦板</a>
						</c:if>
						<br />
						<textarea id="comment_message" onkeydown="ctrlEnter(event, 'commentsubmit_btn');" name="message" rows="5" style="width:500px;"></textarea>
					</td>
				</tr>
				<tr>
					<td>
						<input type="hidden" name="refer" value="space.jsp?uid=${blog.uid}&do=${do}&id=${id}" />
						<input type="hidden" name="id" value="${id}">
						<input type="hidden" name="idtype" value="blogid">
						<input type="hidden" name="commentsubmit" value="true" />
						<input type="button" id="commentsubmit_btn" name="commentsubmit_btn" class="submit" value="评论" onclick="ajaxpost('quickcommentform_${id}', 'comment_add')" />
						<div id="__quickcommentform_${id}"></div>
					</td>
				</tr>
			</table>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" /></form>
		<br />
		</c:if>
	</div>

</div>


<div id="sidebar" style="padding-top:20px;">

	<c:if test="${not empty topic}">
	<div class="affiche">
		<img src="image/app/topic.gif" align="absmiddle">
		<strong>凑个热闹</strong>：
		<a href="space.jsp?do=topic&topicid=${topic.topicid}">${topic.subject}</a>
	</div>
	</c:if>

	<c:if test="${not empty otherlist}">
	<div class="sidebox">
		<h2 class="title">
			<a href="space.jsp?uid=${blog.uid}&do=blog&view=me" class="r_option">全部</a>
			作者的其他最新日志
		</h2>
		<ul class="news_list">
			<c:forEach items="${otherlist}" var="value">
			<li style="height:auto;"><a href="space.jsp?uid=${value.uid}&do=blog&id=${value.blogid}">${value.subject}</a></li>
			</c:forEach>
		</ul>
	</div>
	</c:if>

	<c:if test="${not empty newlist}">
	<div class="sidebox">
	<h2 class="title">热门日志导读</h2>
		<ul class="news_list">
			<c:forEach items="${newlist}" var="value">
			<li style="height:auto;"><a href="space.jsp?uid=${value.uid}" style="font-weight:bold;">${sNames[value.uid]}</a>: <a href="space.jsp?uid=${value.uid}&do=blog&id=${value.blogid}">${value.subject}</a></li>
			</c:forEach>
		</ul>
	</div>
	</c:if>
	
	<c:if test="${not empty blog.related}">
	<c:forEach items="${blog.related}" var="values">
	<div class="sidebox">
	<h2 class="title">您可能感兴趣的<c:if test="${not empty globalApp[values.key].name}">(${globalApp[values.key].name})</c:if></h2>
		<ul class="news_list">
			<c:forEach items="${values.value.data}" var="value">
			<li style="height:auto;">${value.value.html}</li>
			</c:forEach>
		</ul>
	</div>
	</c:forEach>
	</c:if>
	
</div>


<script type="text/javascript">
<!--
function closeSide2(oo) {
	if($('sidebar').style.display == 'none'){
		$('content').style.cssText = '';
		$('sidebar').style.display = 'block';
		oo.innerHTML = '&raquo; 关闭侧边栏';
	}
	else{
		$('content').style.cssText = 'margin: 0pt; width: 810px;';
		$('sidebar').style.display = 'none';
		oo.innerHTML = '&laquo; 打开侧边栏';
	}
}
function addFriendCall(){
	var el = $('friendinput');
	if(!el || el.value == "")	return;
	var s = '<input type="checkbox" name="fusername[]" value="'+el.value+'" id="'+el.value+'" checked>';
	s += '<label for="'+el.value+'">'+el.value+'</label>';
	s += '<br />';
	$('friends').innerHTML += s;
	el.value = '';
}
resizeImg('blog_article','700');
resizeImg('div_main_content','450');
var elems = selector('div[class~=magicflicker]'); 
for(var i=0; i<elems.length; i++){
	magicColor(elems[i]);
}

-->
</script>


<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>