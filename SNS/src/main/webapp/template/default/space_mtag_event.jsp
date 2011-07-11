<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title">
	<img src="image/app/mtag.gif"><a href="space.jsp?do=mtag&id=${mtag.fieldid}">${mtag.title}</a> -
	<a href="space.jsp?do=mtag&tagid=${mtag.tagid}">${mtag.tagname}</a>
</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}"><span>首页</span></a></li>
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=list"><span>讨论区</span></a></li>
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=digest"><span>精华区</span></a></li>		
		<c:if test="${eventnum>0}">
		<li class="active"><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=event"><span>群组活动</span></a></li>
		</c:if>
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=member"><span>成员列表</span></a></li>
		<c:if test="${mtag.allowpost>0}"><li class="null"><a href="cp.jsp?ac=thread&tagid=${tagid}">发起新话题</a></li></c:if>
	</ul>
</div>

<c:choose>
<c:when test="${not empty eventlist}">
<div class="event_list">
	<ol>
	<c:forEach items="${eventlist}" var="event">
		<c:choose>
		<c:when test="${event.uid!=sGlobal.supe_uid && (event.grade==0 || event.public==0)}">
		<c:set var="hiddencount" value="${hiddencount+1}"></c:set>
		</c:when>
		<c:otherwise>
		<li>
			<div class="event_icon">
				<a href="space.jsp?do=event&id=${event.eventid}"><img class="poster_pre" src="${event.pic}" alt="${event.title}" onerror="this.src='${globalEventClass[event.classid].poster}'"></a>
			</div>
			<div class="event_content">
				<h4 class="event_title"><a href="space.jsp?do=event&id=${event.eventid}">${event.title}</a><span class="gray">[${globalEventClass[event.classid].classname}]</span></h4>
				<ul>
					<li>
						<span class="gray">活动时间:</span> 	<jch:date dateformat="M月d日 HH:mm" timestamp="${event.starttime}"/> - <jch:date dateformat="M月d日 HH:mm" timestamp="${event.endtime}"/>
						<c:if test="${event.grade==0}">
						<span class="event_state"> 待审核</span>
						</c:if>
						<c:choose>
						<c:when test="${event.endtime<sGlobal.timestamp}">
						<span class="event_state"> 已结束</span>
						</c:when>
						<c:when test="${event.deadline<sGlobal.timestamp}">
						<span class="event_state"> 报名截止</span>
						</c:when>			
						</c:choose>	
					</li>
					<li><span class="gray">活动地点:</span>
						<a href="space.jsp?uid=${param.uid}&do=event&view=${view}&type=${type}&classid=${param.classid}&province=${event.province}&date=${param.date}">${event.province}</a>
						<a href="space.jsp?uid=${param.uid}&do=event&view=${view}&type=${type}&classid=${param.classid}&province=${event.province}&city=${event.city}&date=${param.date}">${event.city}</a>
						${event.location}
					</li>
					<li><span class="gray">发起人:</span> <a href="space.jsp?uid=${event.uid}">${sNames[event.uid]}</a></li>
					<li style="margin: 5px 0 0;">${event.viewnum} 次查看&nbsp; ${event.membernum} 人参加&nbsp; ${event.follownum} 人关注</li>
				</ul>
			</div>
		</li>
		</c:otherwise>
		</c:choose>
	</c:forEach>
	</ol>
	<c:if test="${hiddencount>0}">
	<div>本页有 ${hiddencount} 个活动因隐私设置而隐藏</div>
	</c:if>
	<div class="page">${multi}</div>
</div>
</c:when>
<c:otherwise>
<div class="c_form">	
	现在还没有群组活动。
</div>
</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>