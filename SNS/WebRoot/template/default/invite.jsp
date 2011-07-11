<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title"><img src="image/icon/friend.gif">好友邀请<c:if test="${not empty userapp}">一起玩 ${userapp.appname}</c:if></h2>
<div class="tabs_header">
	<ul class="tabs">
		<li class="active"><a href="${theURL}"><span>好友邀请</span></a></li>
		<li><a href="space.jsp?${urlPlus}" target="_blank"><span>访问好友主页</span></a></li>
	</ul>
</div>

<div id="content">

	<div class="c_mgs"><div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
	<table cellpadding="2" cellspacing="2" width="100%">
	<tr>
	<td>
		<strong>欢迎您，<a href="space.jsp?${urlPlus}" target="_blank">${sNames[space.uid]}</a> 热情邀请您为好友<c:if test="${not empty userapp}">，并一起玩 ${userapp.appname}</c:if>。</strong>
		<br>成为好友后，您们就可以一起讨论话题，及时关注对方的更新，还可以玩有趣的游戏 ${userapp.appname} ...
		<br>您也可以方便快捷地发布自己的日志、上传图片、记录生活点滴与好友分享。
		<br>还等什么呢？赶快加入我们吧。
	</td>
	<c:if test="${not empty userapp}"><td><img src="http://appicon.jsprun.com/logos/${userapp.appid}" alt="${userapp.appname}" /></td></c:if>
	</tr>
	</table>
	</div></div></div></div></div>


	<table cellpadding="2" cellspacing="2">
		<tr>
			<td>
			<div class="ar_r_t"><div class="ar_l_t"><div class="ar_r_b"><div class="ar_l_b">
			<a href="space.jsp?${urlPlus}" class="avatarlink">${jch:avatar2(space.uid,'middle',false,sGlobal,sConfig)}</a>
			</div></div></div></div>
			</td>
			<td>
				<h6 class="l_status"><a href="space.jsp?${urlPlus}">${sNames[space.uid]}</a></h6>
				<p>来自 ${space.resideprovince} ${space.residecity}</p>
				<p>已有 ${space.friendnum} 个好友, ${numMap.album} 个相册, ${numMap.doing} 条记录, ${numMap.blog} 篇日志, ${numMap.thread} 个话题, ${numMap.tagspace} 个群组</p>
				<c:if test="${!jch:jchEmpty(space.note)}"><div class="quote"><span id="quote" class="q">${space.note}</span></div></c:if>
				<p style="padding-top:1em;">
			<c:choose>
			<c:when test="${sGlobal.supe_uid>0}">
				<form method="post" name="commentform" action="${theURL}">
				<input type="hidden" name="invitesubmit" value="1">
				<input type="submit" name="invitesubmit_submit" value="接受邀请" class="submit" />
				<a href="space.jsp" class="button">忽略此邀请</a>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
				</form>
			</c:when>
			<c:otherwise>
				<c:if test="${jch:jchEmpty(sConfig.closeinvite)}">
				<a href="do.jsp?ac=${sConfig.register_action}&${urlPlus}" class="submit">接受邀请，我要注册</a>
				</c:if>
				<a href="do.jsp?ac=${sConfig.login_action}&${urlPlus}" class="submit">马上登录</a>
			</c:otherwise>
			</c:choose>
				</p>
			</td>
		</tr>
	</table>
</div>

<div id="sidebar">
	<div class="sidebox">
		<h2 class="title">${sNames[space.uid]}的好友们</h2>
		<ul class="avatar_list">
			<c:forEach items="${flist}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
				<p><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
			</li>
			</c:forEach>
		</ul>
	</div>
</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>