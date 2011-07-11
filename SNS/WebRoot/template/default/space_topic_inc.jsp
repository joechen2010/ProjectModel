<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<div class="affiche">
	<table width="100%">
		<tr>
			<c:if test="${!jch:jchEmpty(topic.pic)}"><td width="160" id="event_icon" valign="top"><img src="${topic.pic}" width="150"></td></c:if>
			<td valign="top">
				<h2><a href="space.jsp?do=topic&topicid=${topic.topicid}">${topic.subject}</a></h2>
				<div style="padding: 5px 0;">${topic.message}</div>
				<ul>
					<li class="gray">发起作者: <a href="space.jsp?uid=${topic.uid}">${sNames[topic.uid]}</a></li>
					<li class="gray">发起时间: ${topic.dateline}</li>
					<c:if test="${!jch:jchEmpty(topic.endtime)}"><li class="gray">参与截止: ${topic.endtime}</li></c:if>
					<c:if test="${topic.joinnum>0}"><li class="gray">参与人次: ${topic.joinnum}</li></c:if>
					<li class="gray">最后参与: ${topic.lastpost}</li>
				</ul>
				<c:choose>
					<c:when test="${topic.allowjoin}">
						<a href="${topic.joinurl}" class="feed_po" id="hot_add" onmouseover="showMenu(this.id)">凑个热闹</a>
						<ul id="hot_add_menu" class="dropmenu_drop" style="display: none;">
							<c:if test="${jch:inArray(topic.jointype,'blog')}"><li><a href="cp.jsp?ac=blog&topicid=${topicid}">发表日志</a></li></c:if>
							<c:if test="${jch:inArray(topic.jointype,'pic')}"><li><a href="cp.jsp?ac=upload&topicid=${topicid}">上传图片</a></li></c:if>
							<c:if test="${jch:inArray(topic.jointype,'thread')}"><li><a href="cp.jsp?ac=thread&topicid=${topicid}">发起话题</a></li></c:if>
							<c:if test="${jch:inArray(topic.jointype,'poll')}"><li><a href="cp.jsp?ac=poll&topicid=${topicid}">发起投票</a></li></c:if>
							<c:if test="${jch:inArray(topic.jointype,'event')}"><li><a href="cp.jsp?ac=event&topicid=${topicid}">发起活动</a></li></c:if>
							<c:if test="${jch:inArray(topic.jointype,'share')}"><li><a href="cp.jsp?ac=share&topicid=${topicid}">添加分享</a></li></c:if>
						</ul>
					</c:when>
					<c:otherwise><p class="r_option">该热闹已经截止</p></c:otherwise>
				</c:choose>
			</td>
		</tr>
	</table>
</div>