<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<h2 class="title"><img src="image/app/topic.gif" />热闹 - <a href="space.jsp?do=topic&topicid=${topicid}">${topic.subject}</a></h2>
<div class="tabs_header">
	<ul class="tabs">
		<li class="active"><a href="space.jsp?do=topic&topicid=${topicid}"><span>查看热闹</span></a></li>
		<li><a href="space.jsp?do=topic"><span>热闹列表</span></a></li>
	</ul>
	<c:if test="${managetopic}">
		<div class="r_option">
			<a href="cp.jsp?ac=topic&op=edit&topicid=${topic.topicid}">编辑</a> |
			<a href="cp.jsp?ac=topic&op=delete&topicid=${topic.topicid}" id="a_delete_${topic.topicid}" onclick="ajaxmenu(event,this.id);" id="a_ignore_tid_${value.tid}" onclick="ajaxmenu(event, this.id)">删除</a>
		</div>
	</c:if>
</div>
<div id="content">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_topic_inc.jsp')}" />
	<c:if test="${jch:inArray(fn:split('pic,index', ','),param.view) && jch:inArray(topic.jointype,'pic')}">
		<div class="feed">
			<h3 class="feed_header">
				<div class="r_option">
					<c:if test="${topic.allowjoin}"><a href="cp.jsp?ac=upload&topicid=${topicid}" style="font-weight: bold;">我来添加图片</a><span class="pipe">|</span></c:if>
					<a href="space.jsp?do=topic&topicid=${topicid}&view=pic">全部</a>
				</div>
				图片
			</h3>
			<c:choose>
				<c:when test="${not empty piclist}">
					<table cellspacing="6" cellpadding="0" width="100%" class="photo_list">
						<tr>
							<c:forEach items="${piclist}" var="value" varStatus="key">
								<td align="center">
									<a href="space.jsp?uid=${value.uid}&do=album&picid=${value.picid}"><img src="${value.pic}" alt="" width="100" /></a>
									<p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></p>
									<p class="gray"><jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1" /></p>
									<c:if test="${managetopic}"><p class="gray"><a href="cp.jsp?ac=topic&op=ignore&topicid=${value.topicid}&id=${value.picid}&idtype=picid" id="a_ignore_picid_${value.picid}" onclick="ajaxmenu(event, this.id)">剔除</a></p></c:if>
								</td>
								${key.index%3==2 ? "</tr><tr>" : ""}
							</c:forEach>
						</tr>
					</table>
				</c:when>
				<c:otherwise><div class="c_form gray">现在还没有相关图片，您可以 <a href="cp.jsp?ac=upload&topicid=${topicid}">上传图片</a> 来凑个热闹。</div></c:otherwise>
			</c:choose>
		</div>
	</c:if>
	<c:if test="${jch:inArray(fn:split('blog,index', ','),param.view) && jch:inArray(topic.jointype,'blog')}">
		<div class="feed">
			<h3 class="feed_header">
				<div class="r_option">
					<c:if test="${topic.allowjoin}"><a href="cp.jsp?ac=blog&topicid=${topicid}" style="font-weight: bold;">我来发表日志</a><span class="pipe">|</span></c:if>
					<a href="space.jsp?do=topic&topicid=${topicid}&view=blog">全部</a>
				</div>
				日志
			</h3>
			<c:choose>
				<c:when test="${not empty bloglist}">
					<ul class="line_list">
						<c:forEach items="${bloglist}" var="value">
							<li>
								<table width="100%">
									<tr>
										<td><a href="space.jsp?uid=${value.uid}&do=blog&id=${value.blogid}">${value.subject}</a></td>
										<td width="120" align="center"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></td>
										<td width="80" align="right">${jch:sgmdate(pageContext.request,'MM-dd HH:mm',value.dateline,true)}<c:if test="${managetopic}"><p class="gray"><a href="cp.jsp?ac=topic&op=ignore&topicid=${value.topicid}&id=${value.blogid}&idtype=blogid" id="a_ignore_blogid_${value.blogid}" onclick="ajaxmenu(event, this.id)">剔除</a></p></c:if></td>
									</tr>
								</table>
							</li>
						</c:forEach>
					</ul>
				</c:when>
				<c:otherwise><div class="c_form gray">现在还没有相关日志，您可以 <a href="cp.jsp?ac=blog&topicid=${topicid}">发表日志</a> 来凑个热闹。</div></c:otherwise>
			</c:choose>
		</div>
	</c:if>
	<c:if test="${jch:inArray(fn:split('thread,index', ','),param.view) && jch:inArray(topic.jointype,'thread')}">
		<div class="feed">
			<h3 class="feed_header">
				<div class="r_option">
					<c:if test="${topic.allowjoin}"><a href="cp.jsp?ac=thread&topicid=${topicid}" style="font-weight: bold;">我来发起话题</a><span class="pipe">|</span></c:if>
					<a href="space.jsp?do=topic&topicid=${topicid}&view=thread">全部</a>
				</div>
				话题
			</h3>
			<c:choose>
				<c:when test="${not empty threadlist}">
					<ul class="line_list">
						<c:forEach items="${threadlist}" var="value">
							<li>
								<table width="100%">
									<tr>
										<td><a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}">${value.subject}</a><p class="gray"><a href="space.jsp?do=mtag&tagid=${value.tagid}">${value.tagname}</a></p></td>
										<td width="120" align="center"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></td>
										<td width="80" align="right"><jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1" /><c:if test="${managetopic}"><p class="gray"><a href="cp.jsp?ac=topic&op=ignore&topicid=${value.topicid}&id=${value.tid}&idtype=tid" id="a_ignore_tid_${value.tid}" onclick="ajaxmenu(event, this.id)" id="a_ignore_tid_${value.tid}" onclick="ajaxmenu(event, this.id)">剔除</a></p></c:if></td>
									</tr>
								</table>
							</li>
						</c:forEach>
					</ul>
				</c:when>
				<c:otherwise><div class="c_form gray">现在还没有相关话题，您可以 <a href="cp.jsp?ac=thread&topicid=${topicid}">发起话题</a> 来凑个热闹。</div></c:otherwise>
			</c:choose>
		</div>
	</c:if>
	<c:if test="${jch:inArray(fn:split('poll,index', ','),param.view) && jch:inArray(topic.jointype,'poll')}">
		<div class="feed">
			<h3 class="feed_header">
				<div class="r_option">
					<c:if test="${topic.allowjoin}"><a href="cp.jsp?ac=poll&topicid=${topicid}" style="font-weight: bold;">我来发起投票</a><span class="pipe">|</span></c:if>
					<a href="space.jsp?do=topic&topicid=${topicid}&view=poll">全部</a>
				</div>
				投票
			</h3>
			<c:choose>
				<c:when test="${not empty polllist}">
					<ul class="line_list">
						<c:forEach items="${polllist}" var="value">
							<li>
								<table width="100%">
									<tr>
										<td><a href="space.jsp?uid=${value.uid}&do=poll&pid=${value.pid}">${value.subject}</a><p class="gray">${value.multiple > 0 ? "多选" : "单选"}</p></td>
										<td width="120" align="center"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></td>
										<td width="80" align="right"><jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1" /><c:if test="${managetopic}"><p class="gray"><a href="cp.jsp?ac=topic&op=ignore&topicid=${value.topicid}&id=${value.pid}&idtype=pid" id="a_ignore_pid_${value.pid}" onclick="ajaxmenu(event, this.id)">剔除</a></p></c:if></td>
									</tr>
								</table>
							</li>
						</c:forEach>
					</ul>
				</c:when>
				<c:otherwise><div class="c_form gray">现在还没有相关投票，您可以 <a href="cp.jsp?ac=poll&topicid=${topicid}">发起投票</a> 来凑个热闹。</div></c:otherwise>
			</c:choose>
		</div>
	</c:if>
	<c:if test="${jch:inArray(fn:split('event,index', ','),param.view) && jch:inArray(topic.jointype,'event')}">
		<div class="feed">
			<h3 class="feed_header">
				<div class="r_option">
					<c:if test="${topic.allowjoin}"><a href="cp.jsp?ac=event&topicid=${topicid}" style="font-weight: bold;">我来发起活动</a><span class="pipe">|</span></c:if>
					<a href="space.jsp?do=topic&topicid=${topicid}&view=event">全部</a>
				</div>
				活动
			</h3>
			<c:choose>
				<c:when test="${not empty eventlist}">
					<ul class="line_list">
						<c:forEach items="${eventlist}" var="value">
							<li>
								<table width="100%">
									<tr>
										<td><a href="space.jsp?uid=${value.uid}&do=event&id=${value.eventid}">${value.title}</a><p class="gray">活动开始：<jch:date dateformat="MM-dd HH:mm" timestamp="${value.starttime}" format="1" /></p></td>
										<td width="120" align="center"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></td>
										<td width="80" align="right"><jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1" /><c:if test="${managetopic}"><p class="gray"><a href="cp.jsp?ac=topic&op=ignore&topicid=${value.topicid}&id=${value.eventid}&idtype=eventid" id="a_ignore_eventid_${value.eventid}" onclick="ajaxmenu(event, this.id)">剔除</a></p></c:if></td>
									</tr>
								</table>
							</li>
						</c:forEach>
					</ul>
				</c:when>
				<c:otherwise><div class="c_form gray">现在还没有相关活动，您可以 <a href="cp.jsp?ac=event&topicid=${topicid}">发起活动</a> 来凑个热闹。</div></c:otherwise>
			</c:choose>
		</div>
	</c:if>
	<c:if test="${jch:inArray(fn:split('share,index', ','),param.view) && jch:inArray(topic.jointype,'share')}">
		<div class="feed">
			<h3 class="feed_header">
				<div class="r_option">
					<c:if test="${topic.allowjoin}"><a href="cp.jsp?ac=share&topicid=${topicid}" style="font-weight: bold;">我来添加分享</a><span class="pipe">|</span></c:if>
					<a href="space.jsp?do=topic&topicid=${topicid}&view=share">全部</a>
				</div>
				分享
			</h3>
			<c:choose>
				<c:when test="${not empty sharelist}">
					<ul class="line_list">
						<c:forEach items="${sharelist}" var="value">
							<c:set var="share" value="${value}" scope="request"></c:set>
							<jsp:include page="${jch:template(sConfig, sGlobal, 'space_share_li.jsp')}" />
						</c:forEach>
					</ul>
				</c:when>
				<c:otherwise><div class="c_form gray">现在还没有相关分享，您可以 <a href="cp.jsp?ac=share&topicid=${topicid}">添加分享</a> 来凑个热闹。</div></c:otherwise>
			</c:choose>
		</div>
	</c:if>
	<c:choose>
		<c:when test="${param.view=='space'}">
			<c:choose>
				<c:when test="${not empty list}">
					<div class="feed">
						<h3 class="feed_header"><a href="space.jsp?do=topic&topicid=${topicid}&view=space" class="r_option">全部</a> 用户</h3>
						<jsp:include page="${jch:template(sConfig, sGlobal, 'space_list.jsp')}" />
					</div>
				</c:when>
				<c:otherwise><div class="c_form">还没有相关列表。</div></c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise><div class="page">${multi}</div></c:otherwise>
	</c:choose>
</div>
<div id="sidebar">
	<div class="sidebox">
		<h2 class="title">分类查看</h2>
		<ul class="post_list line_list">
			<li${sub_actives.index}><a href="space.jsp?do=topic&topicid=${topicid}&view=index"><span>全部</span></a></li>
			<c:if test="${jch:inArray(topic.jointype,'blog')}"><li${sub_actives.blog}><a href="space.jsp?do=topic&topicid=${topicid}&view=blog"><span>日志</span></a></li></c:if>
			<c:if test="${jch:inArray(topic.jointype,'pic')}"><li${sub_actives.pic}><a href="space.jsp?do=topic&topicid=${topicid}&view=pic"><span>图片</span></a></li></c:if>
			<c:if test="${jch:inArray(topic.jointype,'thread')}"><li${sub_actives.thread}><a href="space.jsp?do=topic&topicid=${topicid}&view=thread"><span>话题</span></a></li></c:if>
			<c:if test="${jch:inArray(topic.jointype,'poll')}"><li${sub_actives.poll}><a href="space.jsp?do=topic&topicid=${topicid}&view=poll"><span>投票</span></a></li></c:if>
			<c:if test="${jch:inArray(topic.jointype,'event')}"><li${sub_actives.event}><a href="space.jsp?do=topic&topicid=${topicid}&view=event"><span>活动</span></a></li></c:if>
			<c:if test="${jch:inArray(topic.jointype,'share')}"><li${sub_actives.share}><a href="space.jsp?do=topic&topicid=${topicid}&view=share"><span>分享</span></a></li></c:if>
			<li${sub_actives.space}><a href="space.jsp?do=topic&topicid=${topicid}&view=space"><span>用户</span></a></li>
		</ul>
	</div>
	<c:if test="${param.view!='space' && not empty list}">
		<div class="sidebox">
			<h2 class="title"><p class="r_option"><a href="space.jsp?do=topic&topicid=${topicid}&view=space">全部</a></p> 参与成员</h2>
			<ul class="avatar_list">
				<c:forEach items="${list}" var="value">
					<li>
						<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
						<p><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
						<p class="gray"><jch:date dateformat="HH:mm" timestamp="${value.dateline}" format="1" /></p>
					</li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
</div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />