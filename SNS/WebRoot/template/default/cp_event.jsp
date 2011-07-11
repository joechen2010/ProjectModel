<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:if test="${jch:jchEmpty(topic) && (op == 'edit' || op == 'pic' || op == 'thread' || op == 'members' || op == 'comment' || op == 'invite' || op == 'eventinvite')}">
<c:set scope="page" var="opInArray" value="true"/>
<style type="text/css">
	.poster_pre{
		max-width: 80px; max-height: 104px;}
	.poster{
		max-width: 200px; max-height: 260px;}
</style>
<div id="mainarea">
    <h2 class="title"><img src="image/app/event.gif" />活动 <c:if test="${eventid != 0}">- <a href="space.jsp?do=event&id=${event.eventid}">${event.title}</a></c:if></h2>
    <div class="tabs_header">
        <ul class="tabs">
			<c:choose>
			<c:when test="${eventid != 0}">
				<c:if test="${allowmanage}">
				<li ${menus.edit}><a href="cp.jsp?ac=event&op=edit&id=${eventid}"><span>修改活动</span></a></li>
				</c:if>
				<c:if test="${sGlobal.supe_userevent.status >1 && (sGlobal.supe_userevent.status >=3 || !jch:jchEmpty(event.allowinvite))}">
				<li ${menus.invite}><a href="cp.jsp?ac=event&op=invite&id=${eventid}"><span>邀请好友</span></a></li>
				</c:if>
				<c:if test="${sGlobal.supe_userevent.status >= 3 }">
				<li ${menus.members}><a href="cp.jsp?ac=event&op=members&id=${eventid}"><span>成员管理</span></a></li>
				</c:if>
				<c:if test="${allowmanage}">
				<li ${menus.pic}><a href="cp.jsp?ac=event&op=pic&id=${eventid}"><span>照片管理</span></a></li>
					<c:if test="${!jch:jchEmpty(event.tagid)}">
				<li ${menus.thread}><a href="cp.jsp?ac=event&op=thread&id=${eventid}"><span>话题管理</span></a></li>	
					</c:if>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:choose>
				<c:when test="${'eventinvite' == op}">
				<li class="active"><a href="cp.jsp?ac=event&op=eventinvite"><span>活动邀请</span></a></li>
				</c:when>
				<c:otherwise>
				<li class="active"><a href="cp.jsp?ac=event"><span>发起活动</span></a></li>
				</c:otherwise>
				</c:choose>
			</c:otherwise>
			</c:choose>
			<li><a href="space.jsp?do=event&id=${eventid}"><span>返回活动首页</span></a></li>
        </ul>
        <c:if test="${!empty menus.members}">
        <form action="cp.jsp" method="get" id="searchform" name="searchform">
		<div style="margin: 0pt 6px 5px 0pt; float: right;">
		<table cellspacing="0" cellpadding="0">
		<tbody>
		<tr>
		<td style="padding: 0pt;"><input type="text" style="border-right: medium none; width: 160px;" tabindex="1" class="t_input" onfocus="if(this.value=='搜索成员')this.value='';" value="搜索成员" name="key" id="key"/></td>
		<td style="padding: 0pt;"><a href="javascript:$('searchform').submit();"><img alt="搜索" src="image/search_btn.gif"/></a></td>
		</tr>
		</tbody>
		</table>
		</div>
		<input type="hidden" value="event" name="ac"/>
		<input type="hidden" value="${eventid }" name="id"/>
		<input type="hidden" value="members" name="op"/>
		<input type="hidden" value="${param.status }" name="status"/>
		<input type="hidden" value="${jch:formHash(sGlobal,sConfig,false)}" name="formhash"/>
		</form>
        </c:if>
    </div>
</c:if>

<c:choose>
<c:when test="${op == 'join'}">
	<c:choose>
	<c:when test="${!jch:jchEmpty(event.allowfellow) || !jch:jchEmpty(event.template)}">
	<div>
		<h1>填写报名信息</h1>
		<form action="cp.jsp?ac=event&op=join&id=${event.eventid}" method="post" style="padding: 5px 10px;">
			<c:if test="${!jch:jchEmpty(event.allowfellow)}">
			<p>
				<span>携带人数</span>
				<input name="fellow" type="text" size="4" value="${jch:jchEmpty(sGlobal.supe_userevent.fellow) ? 0 : sGlobal.supe_userevent.fellow }" />
				<span class="tiptext">（如果你带朋友参加，请注明携带人数）</span>
			</p>
			</c:if>
			<c:if test="${!jch:jchEmpty(event.template)}">
			<p>
				<span>报名信息</span><span class="tiptext">（请按活动发起者给出的模板填写）</span><br />
				<textarea name="template" rows="4" style="width: 320px;">${jch:jchEmpty(sGlobal.supe_userevent.template) ? event.template : sGlobal.supe_userevent.template }</textarea>
			</p>
			</c:if>
			<p class="btn_line"><br />
				<input type="submit" class="submit" name="joinsubmit" value="确定" />
				<input type="button" class="button" value="取消" onclick="hideMenu()" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
	</c:when>
	<c:otherwise>
	<div>
		<form method="post" name="eventform" action="cp.jsp?ac=event&op=join&id=${eventid}">
			<h1>您确定${!jch:jchEmpty(event.verify) ? '报名' : '参加'}此活动吗？</h1>
			<p class="btn_line"><br />
				<input type="hidden" name="refer" value="${sGlobal.refer}">
				<input type="submit" name="joinsubmit" value="确定" class="submit" />
				<input type="button" name="btnclose" value="取消" onclick="hideMenu();" class="button" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
	</c:otherwise>
	</c:choose>

</c:when>

<c:when test="${op == 'follow'}">
	<div>
		<form method="post" name="eventform" action="cp.jsp?ac=event&op=follow&id=${eventid}">
			<h1>您确定关注此活动吗？</h1>
			<p class="btn_line"><br />
				<input type="hidden" name="refer" value="${sGlobal.refer}" />
				<input type="submit" name="followsubmit" value="确定" class="submit" />
				<input type="button" name="btnclose" value="取消" onclick="hideMenu();" class="button" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}">
		</form>
	</div>
</c:when>

<c:when test="${op == 'cancelfollow'}">
	<div>
		<form method="post" name="eventform" action="cp.jsp?ac=event&op=cancelfollow&id=${eventid}">
			<h1>您确定取消关注此活动吗？</h1>
			<p class="btn_line"><br />
				<input type="hidden" name="refer" value="${sGlobal.refer}">
				<input type="submit" name="cancelfollowsubmit" value="确定" class="submit" />
				<input type="button" name="btnclose" value="取消" onclick="hideMenu();" class="button" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
</c:when>

<c:when test="${op == 'quit'}">
	<div>
		<form method="post" name="eventform" action="cp.jsp?ac=event&op=quit&id=${eventid}">
			<h1>您确定退出此活动吗？</h1>
			<p class="btn_line"><br />
				<input type="hidden" name="refer" value="${sGlobal.refer}">
				<input type="submit" name="quitsubmit" value="确定" class="submit" />
				<input type="button" name="btnclose" value="取消" onclick="hideMenu();" class="button" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
</c:when>

<c:when test="${op == 'delete'}">
	<div>
		<form method="post" name="eventform" action="cp.jsp?ac=event&op=delete&id=${eventid}">
			<h1>您确定取消此活动吗？</h1>
			<p>活动取消将删除所有活动相关信息。<br />此操作不可恢复！</p>
			<p class="btn_line"><br />
				<input type="hidden" name="refer" value="${sGlobal.refer}">
				<input type="submit" name="deletesubmit" value="确定" class="submit" />
				<input type="button" name="btnclose" value="取消" onclick="hideMenu();" class="button" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
</c:when>

<c:when test="${op == 'member'}">
	<div id="memberevent">
		<div><a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a></div>
		<br clear="both" />
		<form method="post" name="eventform" id="eventmember_${uid}" action="cp.jsp?ac=event&op=member&id=${eventid }">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			<input type="hidden" name="refer" value="${sGlobal.refer}">
			<input type="hidden" name="uid" value="${userevent.uid}">
			<p>
				设为：
				<select name="status">
					<option value="2">普通成员</option>
					<c:if test="${sGlobal.supe_uid == event.uid}">
					<option value="3">组织者</option>
					</c:if>
					<c:if test="${!jch:jchEmpty(event.verify)}">
					<option value="0">待审核</option>
					</c:if>
					<option value="-1">踢出成员</option>
				</select> &nbsp;
				<input type="submit" name="membersubmit" value="提交" class="submit" />
			</p>
		</form>
	</div>
</c:when>

<c:when test="${op == 'print'}">
	<div>
		<div><a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a></div>
		<br clear="both" />
		<form method="post" target="_blank" name="eventform" action="cp.jsp?ac=event&op=print&id=${eventid}">
			<h2>设置打印选项</h2>
			<p>内容：
				<!--input type="checkbox" id="ckavatarbig" name="avatarbig" /> <lable for="ckavatarbig">大头像</lable-->
				<input type="checkbox" id="ckavatarbig" name="avatarsmall" checked="checked" /> <lable for="ckavatarsmall">头像</lable>
				<input type="checkbox" id="ckusername" name="username" checked="checked" /> <lable for="ckusername">姓名</lable>
				<c:if test="${event.allowfellow !=0 }">
				<input type="checkbox" id="ckfellow" name="fellow" checked="checked" /> <lable for="ckfellow">携带人数</lable>
				</c:if>
				<c:if test="${!jch:jchEmpty(event.template)}">
				<input type="checkbox" id="cktemplate" name="template" checked="checked" /> <lable for="cktemplate">报名信息</lable>
				</c:if>
			</p>
			<p>选项：
				<input type="checkbox" id="ckadmin" name="admin" /> <lable for="ckadmin">包括组织者</lable>
			</p>
			<p class="btn_line">
				<input type="hidden" name="refer" value="${sGlobal.refer}">
				<input type="submit" name="printsubmit" value="确定" class="submit" />
				<input type="button" name="btnclose" value="取消" onclick="hideMenu();" class="button" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
</c:when>

<c:when test="${op == 'close'}">
	<div>
		<form method="post" name="eventform" action="cp.jsp?ac=event&op=close&id=${eventid}">
			<h1>您确定要关闭活动吗？</h1>
			<p>活动关闭之后将只允许进行浏览。</p>
			<p class="btn_line"><br />
				<input type="hidden" name="refer" value="${sGlobal.refer}">
				<input type="submit" name="closesubmit" value="确定" class="submit" />
				<input type="button" name="btnclose" value="取消" onclick="hideMenu();" class="button" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
</c:when>

<c:when test="${op == 'open'}">
	<div>
		<form method="post" name="eventform" action="cp.jsp?ac=event&op=open&id=${eventid}">
			<h1>您确定要重新开启活动吗？</h1>
			<p class="btn_line"><br />
				<input type="hidden" name="refer" value="${sGlobal.refer}">
				<input type="submit" name="opensubmit" value="确定" class="submit" />
				<input type="button" name="btnclose" value="取消" onclick="hideMenu();" class="button" />
			</p>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
</c:when>

<c:when test="${op == 'calendar'}">
	<div class="calendar">
		<div id="calendar_pre" class="floatleft"><a href="#" onclick="showcalendar('${premonth}'); this.blur(); return false;">&lt;&lt;</a></div>
		<div id="calendar_next" class="floatright"><a href="#" onclick="showcalendar('${nextmonth}'); this.blur(); return false;">&gt;&gt;</a></div>
		<span id="thecalendar_year">${year}</span>年 <span id="thecalendar_month">${month}</span>月
		<ul>
			<li class="calendarli calendarweek">日</li>
			<li class="calendarli calendarweek">一</li>
			<li class="calendarli calendarweek">二</li>
			<li class="calendarli calendarweek">三</li>
			<li class="calendarli calendarweek">四</li>
			<li class="calendarli calendarweek">五</li>
			<li class="calendarli calendarweek">六</li>	
			<c:forEach begin="1" end="${week}">
			<li class="calendarblank">&nbsp;</li>
			</c:forEach>
			<c:forEach items="${days}" var="key_value">
				<c:choose>
				<c:when test="${key_value.value.count != 0}">
			<li class="calendarli ${key_value.value.class}" onmouseover="$('day_${key_value.key }').style.display='block';" onmouseout="$('day_${key_value.key }').style.display='none';">
				<a href="${url}&date=${year}-${month}-${key_value.key }">${key_value.key }</a>
				<div class="dayevents" id="day_${key_value.key }" style="display:none;">
					<ul class="news_list">
					<c:forEach items="${key_value.value.events}" var="value_event">
						<li class="dayeventsli"><a href="space.jsp?do=event&id=${value_event.eventid}">${value_event.title}</a></li>
					</c:forEach>
					</ul>
				</div>
			</li>	
				</c:when>
				<c:otherwise>
			<li class="calendarli">${key_value.key }</li>	
				</c:otherwise>
				</c:choose>
			</c:forEach>
		</ul>
	</div>
</c:when>

<c:when test="${op == 'invite'}">
	<c:choose>
	<c:when test="${event.grade == -2 }">
	<div id="content">
		<p>您现在不能给好友发送邀请，因为此活动已经关闭。</p>
	</div>
	</c:when>
	<c:when test="${event.grade <= 0 }">
	<div id="content">
		<p>您现在不能给好友发送邀请，因为活动还未通过审核。</p>
	</div>
	</c:when>
	<c:when test="${sGlobal.timestamp > event.deadline}">
	<div id="content">
		<p>您现在不能给好友发送邀请，因为活动已经截止报名了。</p>
	</div>
	</c:when>
	<c:when test="${event.limitnum > 0 && event.limitnum <= event.membernum}">
	<div id="content">
		<p>您现在不能给好友发送邀请，因为活动人数已满。</p>
	</div>
	</c:when>
	<c:otherwise>
	<div id="content" style="width: 640px;">
		<form id="edit_form" name="edit_form" class="c_form" method="post" action="cp.jsp?ac=event&op=invite&id=${event.eventid}&group=${group}&page=${page}">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			<div class="h_status">
				您可以给未加入本活动的好友们发送邀请。
			</div>
			<div class="h_status">
				<c:choose>
				<c:when test="${!jch:jchEmpty(list)}">
				<ul class="avatar_list">
				<c:forEach items="${list}" var="value">
					<li><div class="avatar48"><a href="space.jsp?uid=${value.fuid}" title="${sNames[value.fuid] }">${jch:avatar1(value.fuid,sGlobal, sConfig)}</a></div>
						<p>
						<a href="space.jsp?uid=${value.fuid}" title="${sNames[value.fuid] }">${sNames[value.fuid] }</a>
						</p>
						<p><c:choose><c:when test="${jch:jchEmpty(joins[value.fuid])}"><input type="hidden" name="names[]" value="${value.fusername}"><input type="checkbox" name="ids[]" value="${value.fuid}">选定</c:when><c:otherwise>已邀请</c:otherwise></c:choose>
						</p>
					</li>
				</c:forEach>
				</ul>
				<div class="page">${multi}</div>
				</c:when>
				<c:otherwise>
				<div class="c_form">还没有好友。</div>
				</c:otherwise>
				</c:choose>
			</div>
			<p>
				<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')"><label for="chkall">全选</label> &nbsp;
				<input type="hidden" name="invitesubmit" value="1" />
				<input type="submit" name="bnt_invitesubmit" value="邀请" class="submit" />
			</p>
		</form>
	</div>
	<div id="sidebar" style="width: 150px;">
		<div class="cat">
			<h3>好友分类</h3>
			<ul class="post_list line_list">
				<li${group == -1 ? ' class="current"' : ''}><a href="cp.jsp?ac=event&id=${eventid}&op=invite&group=-1">全部好友</a></li>
				<c:forEach items="${groups}" var="key_value">
				<li${group == key_value.key ? ' class="current"' : ''}><a href="cp.jsp?ac=event&id=${eventid}&op=invite&group=${key_value.key}">${key_value.value}</a></li>
				</c:forEach>
			</ul>
		</div>
	</div>
	</c:otherwise>
	</c:choose>
</c:when>

<c:when test="${op == 'members'}">
	<div id="content" style="width: 640px;">
		<form id="edit_form" name="edit_form" class="c_form" method="post" action="cp.jsp?ac=event&op=members&status=${status}&id=${event.eventid}">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />	
			<div class="h_status">
				选择相应的用户进行用户状态变更。
				<c:if test="${event.limitnum != 0}"><span style="color:#f00">剩余 ${event.limitnum - event.membernum}  个活动成员名额</span></c:if>
			</div>	
			<div class="h_status">
				<c:choose>
				<c:when test="${!jch:jchEmpty(list)}">
				<table >
					<tbody>
					<c:forEach items="${list}" var="value">
					<tr>
						<td width="40"><input type="checkbox" name="ids[]" value="${value.uid}"></td>
						<td width="80">
							<div><a href="space.jsp?uid=${value.uid}" target="_blank">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
							<p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid] }</a></p>
						</td>
						<td>
							<c:if test="${event.allowfellow != 0}"><h2>携带人数：${value.fellow}</h2></c:if>
							<c:if test="${!jch:jchEmpty(event.template)}">
							<h2>报名信息：</h2>
							<p>${value.template}</p>
							</c:if>
						</td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<div class="page">${multi}</div>
				</c:when>
				<c:otherwise>
				<div class="c_form">还没有相关成员。</div>
				</c:otherwise>
				</c:choose>
			</div>
			<p>
				<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')"><label for="chkall">全选</label> &nbsp;
				设为：
				<select name="newstatus">
					<option value="2">普通成员</option>
					<c:if test="${sGlobal.supe_uid == event.uid}">
					<option value="3">组织者</option>
					</c:if>
					<c:if test="${!jch:jchEmpty(event.verify)}">
					<option value="0">待审核</option>
					</c:if>
					<option value="-1">踢出成员</option>
				</select>
				<input type="submit" name="memberssubmit" value="提交" class="submit" />
			</p>
		</form>
	</div>
	
	<div id="sidebar" style="width: 150px;">
		<div class="cat">
			<h3>成员状态</h3>
			<ul class="post_list line_list">
				<li${status == 0 ? ' class="current"' : ''}><a href="cp.jsp?ac=event&op=members&id=${eventid}&status=0">待审核</a></li>
				<li${status == 2 ? ' class="current"' : ''}><a href="cp.jsp?ac=event&op=members&id=${eventid}&status=2">普通成员</a></li>
				<li${status == 3 ? ' class="current"' : ''}><a href="cp.jsp?ac=event&op=members&id=${eventid}&status=3">组织者</a></li>
			</ul>
		</div>
	</div>
</c:when>

<c:when test="${op == 'thread'}">
	<div id="d_edit_form">
		<form id="edit_form" name="edit_form" class="c_form" method="post" action="cp.jsp?ac=event&op=thread&id=${event.eventid}" onsubmit="return confirm('此操作不可恢复，确认吗?')">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			<c:choose>
			<c:when test="${!jch:jchEmpty(threadlist)}">
			<div class="topic_list">
				<table cellspacing="0" cellpadding="0">
					<thead>
						<tr>
							<td width="30">选择</td>
							<td class="subject">主题</td>
							<td class="author">作者 (回应/阅读)</td>
							<td class="lastpost">最后更新</td>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${threadlist}" var="value" varStatus="key">
						<tr ${key.index%2 == 1 ? 'class="alt"' : ''}>
							<td>
								<input type="checkbox" name="ids[]" value="${value.tid}" />
							</td>
							<td class="subject">
							<a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}&eventid=${eventid}">${value.subject}</a>
							</td>
							<td class="author"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a><br><em>${value.replynum}/${value.viewnum}</em></td>
							<td class="lastpost"><a href="space.jsp?uid=${value.lastauthorid}" title="${sNames[value.lastauthorid]}">${sNames[value.lastauthorid]}</a><br>${jch:sgmdate(pageContext.request, 'MM-dd HH:mm', value.lastpost,true)}</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
				<p>
					<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')"><label for="chkall">全选</label> &nbsp;
					<input type="submit" name="delthreadsubmit" value="删除" class="submit" />
				</p>
			</div>
			</c:when>
			<c:otherwise>
			<div class="c_form" style="text-align:center;">还没有相关话题。</div>
			</c:otherwise>
			</c:choose>
		</form>
	</div>
</c:when>

<c:when test="${op == 'edithot'}">
	<h1>调整热度</h1>
	<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
	<div class="popupmenu_inner">
		<form method="post" action="cp.jsp?ac=event&op=edithot&id=${eventid}">
			<p class="btn_line">
				新的热度：<input type="text" name="hot" value="${event.hot}" size="5"> 
				<input type="hidden" name="refer" value="${sGlobal.refer}" />
				<input type="hidden" name="hotsubmit" value="true" />
				<input type="submit" name="btnsubmit" value="确定" class="submit" />
			</p>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
</c:when>

<c:when test="${op == 'pic'}">
	<div id="d_edit_form">
		<form id="edit_form" name="edit_form" class="c_form" method="post" action="cp.jsp?ac=event&op=pic&id=${event.eventid}" onsubmit="return confirm('此操作不可恢复，确认吗?')">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			<div id="album" class="article">
				<c:choose>
				<c:when test="${photolistSize > 0}">
				<table width="100%" cellspacing="6" cellpadding="0" class="photo_list">
					<tbody>
						<tr>
						<c:forEach items="${photolist}" var="value" varStatus="key">
							<td>
								<a href="space.jsp?do=event&id=${eventid}&view=pic&picid=${value.picid}"><img src="${value.pic}" alt="${value.title}" /></a>
								<br />
								<input type="checkbox" value="${value.picid}" name="ids[]"/>选定
							</td>
							<c:if test="${key.index%4 == 3}"></tr><tr></c:if>
						</c:forEach>
						</tr>
					</tbody>
				</table>
				<div class="page">${multi}</div>
				<p>
					<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')"><label for="chkall">全选</label> &nbsp;
					<input type="submit" name="deletepicsubmit" value="删除选定" class="submit" />
				</p>
				</c:when>
				<c:otherwise>
				<p align="center">还没有活动照片</p>
				</c:otherwise>
				</c:choose>
				</div>
		</form>
	</div>
</c:when>

<c:when test="${op == 'eventinvite'}">
	<div class="h_status">
		您的好友邀请您加入以下活动
		<c:if test="${!jch:jchEmpty(eventinvites)}">
			<c:set var="eventinvitesNotEmpty" value="true"/>
			<span class="pipe">|</span>
			<a href="cp.jsp?ac=event&op=eventinvite&r=1"><span>忽略所有邀请</span></a>
		</c:if>
	</div>
	
	<div class="c_form">
		<c:choose>
		<c:when test="${eventinvitesNotEmpty}">
		<table cellspacing="4" cellpadding="4" class="formtable">
		<c:forEach items="${eventinvites}" var="value">
		<tr>
			<td width="100px">
				<div>
					<a href="space.jsp?do=event&id=${value.eventid}" target="_blank">
						<img src="${value.pic}" alt="${value.title}" class="poster_pre">
					</a>
				</div>
			</td>
			<td class="h_status">
				<p><a href="space.jsp?do=event&id=${value.eventid}" target="_blank" style="font-size:14px;font-weight:bold;">
				${value.title}</a></p>
				<br>活动时间：${jch:sgmdate(pageContext.request, 'MM月dd日 HH:mm', value.starttime,false)} - ${jch:sgmdate(pageContext.request, 'MM月dd日 HH:mm', value.endtime,false)}
							<c:choose>
							<c:when test="${value.endtime < sGlobal.timestamp}">
							<span class="event_state"> 已结束</span>
							</c:when>
							<c:when test="${value.deadline < sGlobal.timestamp}">
							<span class="event_state"> 报名截止</span>
							</c:when>
							</c:choose>
				<br>活动地点：${value.province} - ${value.city} ${value.location}
				<br>发起人：<a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a>
				<div id="eventinvite_${value.eventid}" style="padding:0.5em 0 0.5em 0;">
					邀请好友：<a href="space.jsp?uid=${value.uid}" target="_blank">${sNames[value.uid]}</a>
					<br>邀请时间：${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', value.invitetime,true)}
					<p style="padding:1em 0 0 0;">
						<a id="a_accept" href="cp.jsp?ac=event&op=acceptinvite&id=${value.eventid}" class="submit" onclick="ajaxget(this.href, 'eventinvite_${value.eventid}'); return false;">接受邀请</a>
						<a href="cp.jsp?ac=event&op=eventinvite&id=${value.eventid}&r=1" class="button">忽略</a>
					</p>
				</div>
			</td>
		</tr>
		</c:forEach>
		</table>
		<div class="page">${multi}</div>
		</c:when>
		<c:otherwise>
		暂时没有新的活动邀请。
		</c:otherwise>
		</c:choose>
	</div>
</c:when>

<c:otherwise>
	<c:if test="${!jch:jchEmpty(topic)}">
		<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_topic_menu.jsp')}"/>
	</c:if>

	<div class="c_form">
		<form id="edit_form" name="edit_form" method="post" enctype="multipart/form-data" action="cp.jsp?ac=event&op=edit&id=${eventid}">
			<table class="infotable" width="100%" cellspacing="4" cellpadding="4">				
				<tbody>					
					<tr>
						<th>活动名称 *</th>
						<td>
							<input class="t_input" id="title" name="title" value="${event.title}" size="56" type="text" maxlength="80">
						</td>
					</tr>
					<tr>
						<th>活动城市 *</th>
						<td id="citybox">
							<script type="text/javascript" src="source/script_city.js" charset="${jchConfig.charset }"></script>
                            <script type="text/javascript" charset="${jchConfig.charset }">
								showprovince('province', 'city', '${event.province}', 'citybox');
                                showcity('city', '${event.city}', 'province', 'citybox');
                            </script>
						</td>
					</tr>
					<tr>
						<th>活动地点</th>
						<td>
							<input class="t_input" id="location" name="location" value="${event.location}" size="56" type="text" maxlength="80">
						</td>
					</tr>
					<tr>
						<th>活动时间 *</th>
						<td>
							<script type="text/javascript" src="source/script_calendar.js" charset="${jchConfig.charset }"></script>
							<input type="text" name="starttime" id="starttime" value="${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', event.starttime,false)}"  onclick="showcalendar(event,this,1,'${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', sGlobal.timestamp,false)}', '${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', sGlobal.timestamp+ (3600 * 24 * 60),false)}')" />
						 至
							<input type="text" name="endtime" id="endtime" value="${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', event.endtime,false)}"  onclick="showcalendar(event,this,1,'${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', sGlobal.timestamp,false)}', '${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', sGlobal.timestamp+ (3600 * 24 * 60),false)}')" />
						</td>
					</tr>
					<tr>
						<th>报名截止 *</th>
						<td>
							<input type="text" name="deadline" id="deadline" value="${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', event.deadline,false)}"  onclick="showcalendar(event,this,1,'${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', sGlobal.timestamp,false)}', '${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', sGlobal.timestamp+ (3600 * 24 * 60),false)}')" />
						</td>
					</tr>
					<tr>
						<th width="100" style="vertical-align: top;">活动分类 *</th>
						<td>
							<select name="classid" id="classid" onchange="reset_eventclass(this.value)">
								<option value="-1">
									请选择活动分类
								</option>
								<c:if test="${!empty globalEventClass}">
								<c:forEach items="${globalEventClass}" var="key_value">
								<option value="${key_value.key}" ${event.classid == key_value.key ? ' selected' : ''} >${key_value.value.classname}</option>
								</c:forEach>
								</c:if>
							</select>
							<div id="classid_info"></div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
						<a id="doodleBox" href="magic.jsp?mid=doodle&showid=blog_doodle&target=jchome-ttHtmlEditor&from=editor" style="display:none"></a>
						<textarea class="userData" name="detail" id="jchome-ttHtmlEditor" style="height:100%;width:100%;display:none;border:0px">${event.detail}</textarea>
						<iframe src="editor.jsp?charset=${jchConfig.charset }&allowhtml=0&doodle=${globalMagic != null && globalMagic.doodle != null ? '1' : ''}" name="jchome-ifrHtmlEditor" id="jchome-ifrHtmlEditor" scrolling="no" border="0" frameborder="0" style="width:100%;border: 1px solid #C5C5C5;" height="400"></iframe></td>
					</tr>
					<tr>
						<th style="vertical-align: top;">活动海报</th>
						<td>
							<input type="file" name="poster" /> 图片将上传到您的默认相册。<br />
							<input type="checkbox" id="sharepic" name="sharepic" value="1" />
							<label for="sharepic">同时把海报共享到活动相册</label>
						</td>
					</tr>
					<c:if test="${!jch:jchEmpty(mtags)}">
					<tr>
						<th>关联群组</th>
						<td>
							<select name="tagid">
							<option value="">选择关联群组</option>
							<c:forEach items="${mtags}" var="value">
							<option value="${value.tagid}" ${value.tagid == event.tagid ? 'selected' : ''} >${value.tagname}</option>
							</c:forEach>
							</select>
							必须是您自己创建的群组，关联后活动话题会同步到该群组。
						</td>
					</tr>
					</c:if>
				</tbody>
			</table>
			<table class="infotable" width="100%" cellspacing="4" cellpadding="4">
				<tbody>
				<tr>
					<td colspan="2">
						<a id="toggle_advanced" href="#" onclick="toggle_advanced(); this.blur(); return false;">
						展开高级设置</a>						
					</td>
				</tr>
				</tbody>
			</table>
			<table id="advanced_info" class="infotable" width="100%" cellspacing="4" cellpadding="4" style="display:none">
				<tbody>
					<tr>
						<th width="100">活动人数</th>
						<td>
							<input name="limitnum" value="${event.limitnum}" id="limitnum" type="text" size="4" maxlength="8">
                            <span class="tiptext">
                                活动参加人数限制，设为 0 表示无限制。
                            </span>
						</td>
					</tr>
					<tr>
						<th width="100" style="vertical-align: top;">活动隐私</th>
						<td>
							<select name="public" id="public">
								<option ${event.public == 2 ? 'selected="selected"' : ''} value="2">公开活动，所有人可见可加入</option>
								<option ${event.public == 1 ? 'selected="selected"' : ''} value="1">半公开活动，所有人可见, 邀请才能加入</option>
								<option ${event.public == 0 ? 'selected="selected"' : ''} value="0">私密活动，被邀请者可见</option>
							</select>
						</td>
					</tr>
					<tr>
						<th width="100" style="vertical-align: top;">活动选项</th>
						<td>
							<input name="allowinvite" id="allowinvite" value="1" type="checkbox"${event.allowinvite != 0 ? ' checked="checked"' : ''}>
                            <label for="allowinvite">
                                允许参与者邀请好友，被邀请者加入活动不需要审核
                            </label>
                            <br>
							<input name="allowpic" id="allowpic" value="1" type="checkbox"${event.allowpic != 0 ? ' checked="checked"' : ''}>
                            <label for="allowpic">
                                允许参与者共享活动照片
                            </label>
                            <br>
							<input name="allowpost" id="allowpost" value="1" type="checkbox"${event.allowpost != 0 ? ' checked="checked"' : ''}>
                            <label for="allowpost">
                                允许所有人发布留言
                            </label>
                            <br>
                            <input name="verify" id="verify" value="1" type="checkbox"${event.verify != 0 ? ' checked="checked"' : ''}>
                            <label for="verify">
                                参加活动需要审批
                            </label>
							<br>
                            <input name="allowfellow" id="allowfellow" value="1" type="checkbox"${event.allowfellow != 0 ? ' checked="checked"' : ''}>
                            <label for="allowfellow">
                                允许参加者携带朋友，携带朋友数会占用活动参与者名额
                            </label>
						</td>
					</tr>
					<tr>
						<th width="100" style="vertical-align: top;">报名信息</th>
						<td>
							如果要求参加者填写报名信息（最多255个字符），你可以在此处给出一个格式模板。留空表示不要求填写。<br />
                            <textarea id="template" name="template" rows="4" cols="72">${event.template}</textarea>
						</td>
					</tr>
					</tr>
				</tbody>
			</table>
			<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'seccode')}">
			<table class="infotable" width="100%" cellspacing="4" cellpadding="4">
				<tbody>
				<c:choose>
				<c:when test="${!jch:jchEmpty(sConfig.questionmode)}">
				<tr>
					<th width="100" style="vertical-align: top;">请回答验证问题</th>
					<td>
						<p>${jch:question(pageContext.request,pageContext.response)}</p>
						<input type="text" id="seccode" name="seccode" value="" size="15" class="t_input" />
					</td>
				</tr>
				</c:when>
				<c:otherwise>
				<tr>
					<th width="100" style="vertical-align: top;">请填写验证码</th>
					<td>
						<script type="text/javascript" charset="${jchConfig.charset }">seccode();</script>
						<p>请输入上面的4位字母或数字，看不清可<a href="javascript:updateseccode()">更换一张</a></p>
						<input type="text" id="seccode" name="seccode" value="" size="15" class="t_input" />
					</td>
				</tr>
				</c:otherwise>
				</c:choose>
				</tbody>
			</table>
			</c:if>
			<table class="infotable" width="100%" cellspacing="4" cellpadding="4">
				<tbody>
					<c:if test="${jch:jchEmpty(eventid)}">
					<tr>
						<th width="100">动态选项</th>
						<td>
							<input type="checkbox" name="makefeed" id="makefeed" value="1"${ckPrivacy ? ' checked' : ''}> 产生动态 (<a href="cp.jsp?ac=privacy#feed" target="_blank">更改默认设置</a>)
						</td>
					</tr>
					</c:if>
					<tr>
						<th width="100">&nbsp;</th>
						<td>
							<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
                            <input type="hidden" name="topicid" value="${topicid}" />
                            <input type="hidden" name="eventsubmit" value="1" />
                            <input class="submit" value="${!jch:jchEmpty(param.id) ? '保存' : '提交' }" type="button" onclick="check_eventpost()"/>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>

	<script type="text/javascript" src="image/editor/editor_function.js" charset="${jchConfig.charset }"></script>
	<script type="text/javascript" charset="${jchConfig.charset }">
		var eventclass = [];
		<c:forEach items="${globalEventClass}" var="key_value">
		eventclass["${key_value.key}"] = {};
		<c:forEach items="${key_value.value}" var="k_v">
			eventclass["${key_value.key}"]["${k_v.key}"] = '${k_v.value}';
		</c:forEach>
		</c:forEach>
		function reset_eventclass(classid){
			var selclass = eventclass[classid];
			var o = $("jchome-ifrHtmlEditor").contentWindow.document.getElementById("HtmlEditor").contentWindow.document.body;
			var append =false;
			if(selclass && selclass['template'] && (trim(o.innerHTML.replace(/<br[ \/]*>|<div><\/div>/img, '')) == "" || confirm("要添加站长设定的活动分类模板到活动介绍吗？"))){
				append = true;
			}
			if (append){
				o.innerHTML = selclass['template'] + "<br/>" + o.innerHTML;				
				$("classid_info").innerHTML = "请参考站长设定的模板填写活动介绍";				
			}
		}
		function toggle_advanced(){
			var el = $("advanced_info");
			if (el.style.display == "none"){
				el.style.display = "";
				$("toggle_advanced").innerHTML = "隐藏高级设置";
			} else {
				el.style.display = "none";
				$("toggle_advanced").innerHTML = "展开高级设置";
			}
		}
		function check_eventpost(){
			if (parseInt($("classid").value) < 0){
				alert("活动类型必须选择。");
				$("classid").focus();
				return false;
			}
			var val = trim($("title").value);
			if ( val == "" ){
				alert("活动标题不能为空！");
				$("title").focus();
				return false;
			} else if (val.replace(/[^\x00-\xff]/g, "**").length > 80){
				alert("活动标题太长，请限制在80个字符内！");
				$("title").focus();
				return false;
			}
			if($('city').value == ""){
				alert("活动举办城市不能为空。");
				$("city").focus();
				return false;
			}
			var deadline = parsedate($("deadline").value).getTime();
			var starttime = parsedate($("starttime").value).getTime();
			var endtime = parsedate($("endtime").value).getTime();
			<c:if test="${eventid == 0}">
			var nowtime = new Date().getTime();
			if (starttime < nowtime){
				alert("活动开始时间不能早于现在。");
				$("starttime").focus();
				return false;
			}
			</c:if>
			if (endtime - deadline < 0){
				alert("报名截止时间不能晚于活动结束时间。");
				$("deadline").focus();
				return false;
			}
			if (endtime - starttime < 0){
				alert("活动结束时间不能早于开始时间。");
				$("endtime").focus();
				return false;
			}
			if (endtime - starttime > 60 * 24 * 3600 * 1000){
				alert("活动持续时间不能超过 60 天。");
				$("endtime").focus();
				return false;
			}
			if (! /^[0-9]{1,8}$/.test($("limitnum").value)){
				alert("活动人数输入不正确。");
				$("limitnum").focus();
				return false;
			}
			
		    var makefeed = $('makefeed');
		    if(makefeed) {
		    	if(makefeed.checked == false) {
		    		if(!confirm("友情提醒：您确定此次发布不产生动态吗？\n有了动态，好友才能及时看到你的更新。")) {
		    			return false;
		    		}
		    	}
		    }
			edit_save();
			val = trim($("jchome-ttHtmlEditor").value.replace(/<br[ \/]*>|<div><\/div>/img,''));
			if (val == ""){
				alert("活动描述不能为空。");
				return false;
			}
			if($('seccode')) {
				var code = $('seccode').value;
				var x = new Ajax();
				x.get('cp.jsp?ac=common&op=seccode&code=' + code, function(s){
					s = trim(s);
					if(s.indexOf('succeed') == -1) {
						alert(s);
						$('seccode').focus();
		           		return false;
					} else {
						$("edit_form").submit();
					}
				});
		    } else {
		    	$("edit_form").submit();
		    }
		}
	</script>
</c:otherwise>
</c:choose>

<c:if test="${opInArray }">
</div>
</c:if>
	
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>