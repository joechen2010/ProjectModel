<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
	<div id="space_share">
		<h3 class="feed_header">
			<a href="space.jsp?do=share&view=me" class="r_option" target="_blank">添加分享</a>
			分享(共 ${count} 个)</h3>
		
		<c:choose>
		<c:when test="${not empty list}">
		<ul class="line_list">
		<c:forEach items="${list}" var="value">
		<c:set var="share" value="${value}" scope="request"></c:set>
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_share_li.jsp')}"/>
		</c:forEach>
			<div class="page">${multi}</div>
		</ul>
		</c:when>
		<c:otherwise>
		<div class="c_form">现在还没有分享</div>
		</c:otherwise>
		</c:choose>
		
	</div><br>
</c:when>
<c:otherwise>

<c:choose>
<c:when test="${space.self}">
<h2 class="title"><img src="image/app/share.gif" />分享</h2>
<div class="tabs_header">
	<ul class="tabs">
		<c:if test="${space.friendnum>0}"><li${actives.we}><a href="space.jsp?uid=${space.uid}&do=share&view=we"><span>好友的分享</span></a></li></c:if>
		<li${actives.all}><a href="space.jsp?uid=${space.uid}&do=share&view=all"><span>大家的分享</span></a></li>
		<li${actives.me}><a href="space.jsp?uid=${space.uid}&do=share&view=me"><span>我的分享</span></a></li>
	</ul>
</div>
</c:when>
<c:otherwise>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}"/>
</c:otherwise>
</c:choose>

<div class="h_status">
按分享类型查看：
<a href="${theurl}"${sub_actives.type_all}>全部</a><span class="pipe">|</span>
<a href="${theurl}&type=link"${sub_actives.type_link}>网址</a><span class="pipe">|</span>
<a href="${theurl}&type=video"${sub_actives.type_video}>视频</a><span class="pipe">|</span>
<a href="${theurl}&type=music"${sub_actives.type_music}>音乐</a><span class="pipe">|</span>
<a href="${theurl}&type=flash"${sub_actives.type_flash}>Flash</a><span class="pipe">|</span>
<a href="${theurl}&type=blog"${sub_actives.type_blog}>日志</a><span class="pipe">|</span>
<a href="${theurl}&type=album"${sub_actives.type_album}>相册</a><span class="pipe">|</span>
<a href="${theurl}&type=pic"${sub_actives.type_pic}>图片</a><span class="pipe">|</span>
<a href="${theurl}&type=mtag"${sub_actives.type_mtag}>群组</a><span class="pipe">|</span>
<a href="${theurl}&type=thread"${sub_actives.type_thread}>话题</a><span class="pipe">|</span>
<a href="${theurl}&type=poll"${sub_actives.type_poll}>投票</a><span class="pipe">|</span>
<a href="${theurl}&type=event"${sub_actives.type_event}>活动</a><span class="pipe">|</span>
<a href="${theurl}&type=space"${sub_actives.type_space}>用户</a><span class="pipe">|</span>
<a href="${theurl}&type=tag"${sub_actives.type_tag}>TAG</a>
</div>

<div id="content">
	<ul id="share_ul">
		<c:choose>
		<c:when test="${not empty list}">
		<c:forEach items="${list}" var="value">
		<c:set var="share" value="${value}" scope="request"></c:set>
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_share_li.jsp')}"/>
		</c:forEach>
			<div class="page">${multi}</div>
		</c:when>
		<c:otherwise>
		<li><div class="c_form">现在还没有分享。</div></li>
		</c:otherwise>
		</c:choose>
	</ul>

</div>


<div id="sidebar">
	<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
	<form id="shareform" name="shareform" action="cp.jsp?ac=share&type=link" method="post">
	<table cellspacing="2" cellpadding="2" width="100%">
		<tr><td><strong>分享网址、视频、音乐、Flash:</strong></td></tr>
		<tr><td><input type="text" class="t_input" name="link" onfocus="javascript:if('http://'==this.value)this.value='';" onblur="javascript:if(''==this.value)this.value='http://'" id="share_link" style="width:98%;" value="http://" /></td></tr>
		<tr><td><strong>描述:</strong></td></tr>
		<tr>
			<td>
				<textarea id="share_general" name="general" style="width:98%;" rows="3" onkeydown="ctrlEnter(event, 'sharesubmit_btn')"></textarea>
			</td>
		</tr>
		<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'seccode')}">
		<tr>
			<td>
			<c:choose>
			<c:when test="${sConfig.questionmode==1}">
					<p>请正确回答下面问题后再提交</p>
					<p>${jch:question(pageContext.request,pageContext.response)}</p>
					<p><input type="text" id="seccode" name="seccode" value="" size="15" class="t_input"></p>
			</c:when>
			<c:otherwise>
					<p><script>seccode();</script></p>
					<p>请输入上面的4位字母或数字，看不清可<a href="javascript:updateseccode()">更换一张</a></p>
					<p><input type="text" id="seccode" name="seccode" value="" size="15" class="t_input"></p>
			</c:otherwise>
			</c:choose>
			</td>
		</tr>
		</c:if>
		<tr><td>
		<input type="hidden" name="refer" value="space.jsp?uid=${space.uid}&do=share&view=me" />
		<input type="hidden" name="sharesubmit" value="true" />
		<input type="button" id="sharesubmit_btn" name="sharesubmit_btn" value="分享" class="submit" onclick="ajaxpost('shareform', 'share_add')" />
		</td></tr>
		<tr><td id="__shareform"></td></tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" /></form>
	</div></div></div></div>

	<div class="sidebox" id="help">
		<h2 class="title">如何分享网页？</h2>
		<p>直接填写网址。</p>
		<h2 class="title">如何分享视频？</h2>
		<p>填写视频所在网页的网址。(不需要填写视频的真实地址)</p>
		<p>我们支持的视频网站有：<br>Youtube、优酷、酷6、Mofile、偶偶视频、UUME、我秀、爆米花、第九频道、百度贴吧视频、新浪视频、搜狐视频。</p>
		<h2 class="title">如何分享音乐？</h2>
		<p>填写音乐文件的网址。(后缀需要是mp3或者wma)</p>
		<h2 class="title">如何分享 Flash？</h2>
		<p>填写 Flash 文件的网址。(后缀需要是swf)</p>
	</div>

</div>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>