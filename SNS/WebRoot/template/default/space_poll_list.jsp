<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
	<div id="space_poll">
		<h3 class="feed_header">
			<a href="cp.jsp?ac=poll" class="r_option" target="_blank">发起投票</a>
			投票(共 ${count} 个)</h3>
		<c:choose>
		<c:when test="${not empty list}">
		<ul class="line_list">
		<c:forEach items="${list}" var="value">
			<li>
				<span class="gray r_option">${value.voternum}人投票</span>
				<h4><a href="space.jsp?uid=${space.uid}&do=poll&pid=${value.pid}" target="_blank">${value.subject}</a></h4>
				<div class="detail">
				 <c:forEach items="${value.option}" var="val">
					<p><input type="${value.multiple>0 ? 'checkbox' : 'radio'}" disabled name="poll_${value.pid}"/> ${val}</p>
				</c:forEach>
				</div>
			</li>
		</c:forEach>
		</ul>
		<div class="page">${multi}</div>
		
		</c:when>
		<c:otherwise>
		<div class="c_form">还没有相关的投票。</div>
		</c:otherwise>
		</c:choose>
	</div><br>
</c:when>
<c:otherwise>

<c:choose>
<c:when test="${space.self}">
<div class="searchbar floatright">
<form method="get" action="space.jsp">
	<input name="searchkey" value="" size="15" class="t_input" type="text">
	<input name="searchsubmit" value="搜索投票" class="submit" type="submit">
	<input type="hidden" name="searchmode" value="1" />
	<input type="hidden" name="do" value="poll" />
	<input type="hidden" name="view" value="new" />
</form>
</div>
<h2 class="title"><img src="image/app/poll.gif" />投票</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li${actives.new}><a href="space.jsp?uid=${space.uid}&do=${do}&view=new"><span>最新投票</span></a></li>
		<li${actives.hot}><a href="space.jsp?uid=${space.uid}&do=${do}&view=hot"><span>最热投票</span></a></li>
		<li${actives.reward}><a href="space.jsp?uid=${space.uid}&do=${do}&view=reward"><span>悬赏投票</span></a></li>
		<c:if test="${space.friendnum>0}"><li${actives.friend}><a href="space.jsp?uid=${space.uid}]&do=${do}&view=friend"><span>好友投票</span></a></li></c:if>
		<li${actives.me}><a href="space.jsp?uid=${space.uid}&do=${do}&view=me"><span>我的投票</span></a></li>
		<li class="null"><a href="cp.jsp?ac=poll">发起投票</a></li>
	</ul>
</div>
</c:when>
<c:otherwise>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}"/>
<div class="h_status">按照投票发起时间排序</div>
</c:otherwise>
</c:choose>

<c:if test="${space.self && jch:inArray(fn:split('me,hot', ','),param.view)}">
<div class="h_status">
	<c:choose>
	<c:when test="${param.view=='hot'}">
	范围：
	<a href="space.jsp?uid=${space.uid}&do=${do}&view=hot&filtrate=all"${filtrate.all}>全部</a>
	<span class="pipe">|</span><a href="space.jsp?uid=${space.uid}&do=${do}&view=hot&filtrate=week"${filtrate.week}>本周最热</a>
	<span class="pipe">|</span><a href="space.jsp?uid=${space.uid}&do=${do}&view=hot&filtrate=month"${filtrate.month}>本月最热</a>
	</c:when>
	<c:otherwise>
	类型：
	<a href="space.jsp?uid=${space.uid}&do=${do}&view=me"${filtrate.me}>我发起的</a>
	<span class="pipe">|</span><a href="space.jsp?uid=${space.uid}&do=${do}&view=me&filtrate=join"${filtrate.join}>我参与的</a>
	<span class="pipe">|</span><a href="space.jsp?uid=${space.uid}&do=${do}&view=me&filtrate=expiration"${filtrate.expiration}>已过期的</a>
	</c:otherwise>
	</c:choose>
</div>
</c:if>

<c:if test="${not empty searchkey}">
<div class="h_status">以下是搜索投票 <span style="color:red;font-weight:bold;">${searchkey}</span> 结果列表</div>
</c:if>
	
<div id="content" style="width:100%;">
	<c:choose>
	<c:when test="${count>0}">
	<div class="poll_list">
		<ul>
		<c:forEach items="${list}" var="value">
			<li>
				<div class="poll_user">
				<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
				<p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></p>
				</div>
				<div class="poll_content">

					<h4>
						<c:if test="${value.percredit>0}"><span class="poll_reward">+${value.percredit}</span></c:if><a href="space.jsp?uid=${value.uid}&do=${do}&pid=${value.pid}">${value.subject}</a>
					</h4>
					<div class="poll_options">
						<ol>
						 <c:forEach items="${value.option}" var="val">
							<li><input type="${value.multiple>0 ? 'checkbox' : 'radio'}" disabled name="poll_${value.pid}"/> ${val}</li>
						 </c:forEach>
							<li>……</li>
						 </ol>
						 <p>
						 	<span class="gray"><jch:date dateformat="yyyy-MM-dd HH:mm" timestamp="${value.dateline}" format="1"/></span>
						 	<span class="pipe">|</span>
						 	<a href="space.jsp?uid=${value.uid}&do=${do}&pid=${value.pid}#comment">评论(${value.replynum})</a>
						 	<c:if test="${param.view=='me' && !jch:jchEmpty(value.expiration) && value.expiration<sGlobal.timestamp}">
						 	<span class="pipe">|</span>
						 	<span class="gray">投票已结束</span><c:if test="${empty value.summary}">&nbsp;<a class="green" href="space.jsp?uid=${value.uid}&do=${do}&pid=${value.pid}">去写写投票总结</a></c:if>
						 	</c:if>
						 </p>
					</div>
				</div>
				<div class="poll_status">
					<a href="space.jsp?uid=${value.uid}&do=${do}&pid=${value.pid}" class="poll_joins pj_${jch:getRandStr(1,true)}"><span>${value.voternum}</span>人参与</a> <a href="space.jsp?uid=${value.uid}&do=${do}&pid=${value.pid}" class="go2_poll">去投票</a>
				</div>
			</li>
		</c:forEach>
			
		</ul>
	</div>
	
	<div class="page">${multi}</div>
	</c:when>
	<c:otherwise>
	<div class="c_form">还没有相关的投票。</div>
	</c:otherwise>
	</c:choose>

</div>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>