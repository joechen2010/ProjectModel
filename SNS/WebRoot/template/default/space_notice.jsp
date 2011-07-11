<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title"><img src="image/icon/pm.gif">通知</h2>

<div class="tabs_header">
	<ul class="tabs">
		<li><a href="space.jsp?do=pm"><span>短消息</span></a></li>
		<li${actives.notice}><a href="space.jsp?do=notice"><span>通知</span></a></li>
		<c:if test="${!jch:jchEmpty(sConfig.my_status)}">
		<li${actives.userapp}><a href="space.jsp?do=notice&view=userapp"><span>应用消息</span></a></li>
		</c:if>
	</ul>
</div>

<c:choose>
<c:when test="${view=='userapp'}">
	<script type="text/javascript">
		function jsprun_add_userapp(hash, url) {
			if(isUndefined(url)) {
				$(hash).innerHTML = "<tr><td colspan=\"2\">成功忽略了该条应用消息</td></tr>";
			} else {
				$(hash).innerHTML = "<tr><td colspan=\"2\">正在引导您进入...</td></tr>";
			}
			var x = new Ajax();
			x.get('do.jsp?ac=ajax&op=deluserapp&hash='+hash, function(s){
				if(!isUndefined(url)) {
					location.href = url;
				}
			});
		}
	</script>
	
	<div id="content">

		<style>
			.topicList table{width:100%;margin:10px 0 5px 0;}
			.topicList td{color:#333;}
		</style>
		<c:choose>
		<c:when test="${not empty list}">
		<div class="m_box">
		<c:forEach items="${list}" var="invite">
			<h3 class="feed_header">
				<a href="space.jsp?do=notice&view=userapp&op=del&appid=${invite.value[0].appid}" class="r_option">忽略该应用的所有邀请</a>
				<a href="userapp.jsp?id=${invite.value[0].appid}&uid=${space.uid}" title="${apparr[invite.value[0].appid]}"><img src="http://appicon.jsprun.com/icons/${invite.value[0].appid}" alt="${apparr[invite.value[0].appid]}" align="absmiddle" /></a> 
				您有 ${fn:length(invite.value)} 个 ${invite.value[0].typename} <c:choose><c:when test="${!jch:jchEmpty(invite.value[0].type)}">请求</c:when><c:otherwise>邀请</c:otherwise></c:choose>
			</h3>
			<table cellpadding="0" cellspacing="0" width="100%" class="topic_list">
			<c:forEach items="${invite.value}" var="value">
				<tbody>
				<tr>
					<td width="60" valign="top">
						<div class="avatar48">
							<a href="space.jsp?uid=${value.fromuid}" class="avatarlink">${jch:avatar1(value.fromuid,sGlobal, sConfig)}</a>
						</div>
					</td>
					<td id="${value.hash}">
						${value.myml}
					</td>
				</tr>
				</tbody>
			</c:forEach>
			</table>
		</c:forEach>
		</div>
		<div class="page">${multi}</div>
		</c:when>
		<c:otherwise>
		<div class="c_form">
			没有新的应用请求或邀请
		</div>
		</c:otherwise>
		</c:choose>
	</div>
	
	<div id="sidebar">
		<div class="sidebox">
		<h2 class="title">应用分类</h2>
		<ul class="line_list">
			<li><a href="space.jsp?do=notice&view=userapp">查看全部应用消息</a></li>
			<!--{loop $apparr $type $val}-->
			<c:forEach items="${apparr}" var="val">
			<li><a href="userapp.jsp?id=${val.value[0].appid}&uid=${space.uid}" title="${val.value[0].typename}"><img src="http://appicon.jsprun.com/icons/${val.value[0].appid}" alt="${val.value[0].typename}" /></a><a href="space.jsp?do=notice&view=userapp&type=${val.value[0].appid}">${fn:length(val.value)} 个 ${val.value[0].typename}<c:choose><c:when test="${!jch:jchEmpty(val.value[0].type)}">请求</c:when><c:otherwise>邀请</c:otherwise></c:choose> </a></li>
			</c:forEach>
		</ul>
		</div>
	</div>

</c:when>
<c:otherwise>
	
	<div id="content">
		
		<div class="h_status">
		提示：当你感觉有些通知对你造成骚扰，请点击通知右侧的屏蔽小图标，屏蔽此类通知。
		</div>
			
		<c:if test="${newnum>0}">
		<div class="mgs_list">
			<c:if test="${space.notenum>0}"><div><img src="image/icon/notice.gif"><a href="space.jsp?do=notice"><strong>${space.notenum}</strong> 条新通知</a></div></c:if>
			<c:if test="${space.addfriendnum>0}"><div><img src="image/icon/friend.gif" alt="" /><a href="cp.jsp?ac=friend&op=request"><strong>${space.addfriendnum}</strong> 个好友请求</a></div></c:if>
			<c:if test="${space.mtaginvitenum>0}"><div><img src="image/icon/mtag.gif" alt="" /><a href="cp.jsp?ac=mtag&op=mtaginvite"><strong>${space.mtaginvitenum}</strong> 个群组邀请</a></div></c:if>
			<c:if test="${space.eventinvitenum>0}"><div><img src="image/icon/event.gif" alt="" /><a href="cp.jsp?ac=event&op=eventinvite"><strong>${space.eventinvitenum}</strong> 个活动邀请</a></div></c:if>
			<c:if test="${space.myinvitenum>0}"><div><img src="image/icon/userapp.gif" alt="" /><a href="space.jsp?do=notice&view=userapp"><strong>${space.myinvitenum}</strong> 个应用消息</a></div></c:if>
			<c:if test="${space.pokenum>0}"><div><img src="image/icon/poke.gif" alt="" /><a href="cp.jsp?ac=poke"><strong> ${space.pokenum}</strong> 个新招呼</a></div></c:if>
		</div>
		</c:if>

		
		<c:choose>
		<c:when test="${not empty list}">
		<table cellpadding="0" cellspacing="0" width="100%" class="topic_list">
		<c:forEach items="${list}" var="value">
			<tbody>
			<tr>
			<td width="60" valign="top">
			<c:choose>
			<c:when test="${!jch:jchEmpty(value.authorid)}">
				<div class="avatar48">
					<a href="space.jsp?uid=${value.authorid}" class="avatarlink">${jch:avatar1(value.authorid,sGlobal, sConfig)}</a>
				</div>
			</c:when>
			<c:otherwise>
				<div class="avatar48"><img src="image/systempm.gif" width="48" height="48" /></div>
			</c:otherwise>
			</c:choose>
			</td>
			<td>
				
					<a href="cp.jsp?ac=common&op=ignore&authorid=${value.authorid}&type=${value.type}" id="a_note_${value.id}" onclick="ajaxmenu(event, this.id)" class="float_cancel">屏蔽</a>

					<div style="padding:10px 0 5px 0;${value.style}">
						<c:if test="${!jch:jchEmpty(value.authorid)}">
						<a href="space.jsp?uid=${value.authorid}">${sNames[value.authorid]}</a>
						</c:if>
						${value.note}
						<p class="time">&nbsp;<jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1"/></p>
					</div>
					
					<c:if test="${!jch:jchEmpty(value.authorid) && !value.isfriend}">
					<p>
						<a href="cp.jsp?ac=friend&op=add&uid=${value.authorid}" id="add_note_friend_${value.authorid}" onclick="ajaxmenu(event, this.id, 1)">加为好友</a>
						<span class="pipe">|</span>
						<a href="cp.jsp?ac=poke&op=send&uid=${value.authorid}" id="a_poke_${value.authorid}" onclick="ajaxmenu(event, this.id, 1)">打个招呼</a>
					</p>
					</c:if>
					
				</td>
			</tr>
			</tbody>
		</c:forEach>
		
		<c:if test="${view!='userapp' && space.notenum>0}">
			<tbody>
			<tr>
			<td width="60">
			</td>
			<td align="center"><a href="space.jsp?do=notice&ignore=all">&raquo; 将后续页面所有未读新通知视为已读</a></td>
			</tr>
			</tbody>
		</c:if>
		
		</table>
		

		<div class="page">${multi}</div>
		</c:when>
		<c:otherwise>
		<div class="c_form">
		没有新的通知。
		</div>
		</c:otherwise>
		</c:choose>
	</div>
	
	<div id="sidebar">		
		<div class="sidebox">
			<h2 class="title">通知分类</h2>
			<ul class="line_list">
				<li><a href="space.jsp?do=notice">查看全部通知</a></li>
				<c:forEach items="${noticetypes}" var="noticetype">
				<li><a href="space.jsp?do=notice&type=${noticetype.key}">${noticetype.value}</a></li>
				</c:forEach>
			</ul>
		</div>
		
	</div>

</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>