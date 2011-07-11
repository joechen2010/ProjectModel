<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
	<div id="space_event">
		<h3 class="feed_header">
			<a href="cp.jsp?ac=event" class="r_option" target="_blank">组织活动</a>
			活动(共 ${count} 个)</h3>
		<c:choose>
		<c:when test="${not empty eventlist}">
		<ul class="line_list">
		<c:forEach items="${eventlist}" var="event">
			<li>
				<p class="r_option">
					<c:if test="${event.value.grade== 0}">
					<span class="event_state">待审核</span>
					</c:if>
					<c:choose>
					<c:when test="${event.value.endtime<sGlobal.timestamp}">
					<span class="event_state">已结束</span>
					</c:when>
					<c:when test="${event.value.deadline<sGlobal.timestamp}">
					<span class="event_state">报名截止</span>
					</c:when>
					</c:choose>
				</p>
				<h4><a href="space.jsp?do=event&id=${event.value.eventid}" target="_blank">${event.value.title}</a><span class="gray">[${globalEventClass[event.value.classid].classname}]</span></h4>
	
				<p><span class="gray">活动时间:</span> 	<jch:date dateformat="MM月dd日 HH:mm" timestamp="${event.value.starttime}"/> - <jch:date dateformat="MM月dd日 HH:mm" timestamp="${event.value.endtime}"/></p>
				<p><span class="gray">活动地点:</span> 	${event.value.province} ${event.value.city} ${event.value.location}</p>
			</li>
		</c:forEach>
		</ul>
		<div class="page">${multi}</div>
		
		</c:when>
		<c:otherwise>
		<div class="c_form">还没有相关的活动。</div>
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
	<input name="searchsubmit" value="搜索活动" class="submit" type="submit">
	<input type="hidden" name="searchmode" value="1" />
	<input type="hidden" name="do" value="event" />
	<input type="hidden" name="view" value="all" />
</form>
</div>
<h2 class="title"><img src="image/app/event.gif" />活动</h2>
<div class="tabs_header">
    <ul class="tabs">
        <li ${menu.all}><a href="space.jsp?do=event&view=all"><span>全部活动</span></a></li>
        <li ${menu.recommend}><a href="space.jsp?do=event&view=recommend"><span>推荐活动</span></a></li>
        <li ${menu.city}><a href="space.jsp?do=event&view=city"><span>同城活动</span></a></li>
        <c:if test="${space.friendnum>0}">
        <li ${menu.friend}><a href="space.jsp?do=event&view=friend"><span>好友的活动</span></a></li>
       </c:if>
		<li ${menu.me}><a href="space.jsp?uid=${space.uid}&do=event&view=me"><span>我的活动</span></a></li>
		<li class="null"><a href="cp.jsp?ac=event" class="t_button">发起新活动</a></li>
    </ul>
</div>
</c:when>
<c:otherwise>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}"/>
</c:otherwise>
</c:choose>
<div class="h_status">
	<!--{if $menu[all]}-->
	<c:choose>
	<c:when test="${not empty menu.all}">
	<a ${submenus.going} href="space.jsp?do=event&view=all&type=going">尚未结束的活动</a>
	<span class="pipe">|</span><a ${submenus.over} href="space.jsp?do=event&view=all&type=over">已结束的活动</a>
	</c:when>
	<c:when test="${not empty menu.recommend}">
	<a ${submenus.hot} href="space.jsp?do=event&view=recommend&type=hot">热门活动</a>
	<span class="pipe">|</span><a ${submenus.admin} href="space.jsp?do=event&view=recommend&type=admin">站长推荐</a>
	</c:when>
	<c:when test="${not empty menu.city}">
	<a ${submenus.going} href="space.jsp?do=event&view=city&type=going">尚未结束的活动</a>
	<span class="pipe">|</span><a ${submenus.over} href="space.jsp?do=event&view=city&type=over">已结束的活动</a>
	</c:when>
	<c:when test="${not empty menu.friend}">
	<a ${submenus.all} href="space.jsp?do=event&view=friend&type=all">全部</a>
	<span class="pipe">|</span><a ${submenus.join} href="space.jsp?do=event&view=friend&type=join">参加的活动</a>
	<span class="pipe">|</span><a ${submenus.follow} href="space.jsp?do=event&view=friend&type=follow">关注的活动</a>
	<span class="pipe">|</span><a ${submenus.org} href="space.jsp?do=event&view=friend&type=org">组织的活动</a>
	</c:when>
	<c:when test="${not empty menu.me}">
	<a ${submenus.all} href="space.jsp?uid=${space.uid}&do=event&view=me&type=all">全部</a>
	<span class="pipe">|</span><a ${submenus.join} href="space.jsp?uid=${space.uid}&do=event&view=me&type=join">参加的活动</a>
	<span class="pipe">|</span><a ${submenus.follow} href="space.jsp?uid=${space.uid}&do=event&view=me&type=follow">关注的活动</a>
	<span class="pipe">|</span><a ${submenus.org} href="space.jsp?uid=${space.uid}&do=event&view=me&type=org">组织的活动</a>
	</c:when>
	</c:choose>
</div>

<c:if test="${!jch:jchEmpty(searchkey)}">
<div class="h_status">以下是搜索活动 <span style="color:red;font-weight:bold;">${searchkey}</span> 结果列表</div>
</c:if>

<div id="content">
	<c:if test="${view=='all' && not empty recommendevents}">
	<div class="rec_event_list">
		<h2>
			<div class="r_option"><a href="space.jsp?do=event&view=recommend&type=admin">更多</a></div>
			站长推荐
		</h2>
		<ol>
			<c:forEach items="${recommendevents}" var="value">
			<li>
				<div class="event_icon">
					<a href="space.jsp?do=event&id=${value.eventid}"><img class="poster_pre" src="${value.pic}" alt="${value.title}" onerror="this.src='${globalEventClass[value.classid].poster}'"></a>
				</div>
				<div class="event_content">
					<h4><a href="space.jsp?do=event&id=${value.eventid}">${value.title}</a> <span class="gray">[${globalEventClass[value.classid].classname}]</span></h4>
					<p>活动时间: <jch:date dateformat="MM月dd日 HH:mm" timestamp="${value.starttime}"/> - <jch:date dateformat="MM月dd日 HH:mm" timestamp="${value.endtime}"/></p>
				</div>
			</li>
			</c:forEach>
		</ol>
	</div>
	</c:if>
	
	<c:if test="${view=='city'}">
	<div>
		<c:choose>
		<c:when test="${!jch:jchEmpty(param.city)}">
			您现在浏览的是 <b>${param.province} - ${param.city}</b> 的活动。
			<a href="#" onclick="$('viewcityevents').style.display=''; this.blur(); return false;">浏览其他城市</a>
		</c:when>
		<c:when test="${!jch:jchEmpty(param.province)}">
			您现在浏览的是 ${param.province} 的活动。
			<c:if test="${space.province==param.province && !jch:jchEmpty(space.city)}">
			您还可以只浏览 <a href="space.jsp?do=event&view=city&province=${space.province}&city=${space.city}">${space.city}</a> 的活动。
			</c:if>
			<a href="#" onclick="$('viewcityevents').style.display=''; this.blur(); return false;">浏览其他城市</a>
		</c:when>
		</c:choose>
		<script type="text/javascript" src="source/script_city.js"></script>
		<form id="viewcityevents" action="space.jsp" method="GET" style="display:none;">
			<input type="hidden" name="do" value="event" />
			<input type="hidden" name="view" value="city" />
			<span id="eventcitybox"></span>
			 <script type="text/javascript">
				showprovince('province', 'city', '${param.province}', 'eventcitybox');
                showcity('city', '${param.city}', 'province', 'eventcitybox');
            </script>
			<input class="submit" type="submit" value="浏览" />
		</form>
		<c:if test="${empty space.resideprovince}">
		<div class="c_form">
			您还没有设置居住城市。<a href="cp.jsp?ac=profile" target="_blank">现在就去设置</a>
		</div>
		</c:if>
	</div>
	</c:if>

	<c:choose>
	<c:when test="${not empty eventlist}">
	<div class="event_list">
		<ol>
		<c:forEach items="${eventlist}" var="event">
			<c:choose>
			<c:when test="${event.value.uid != sGlobal.supe_uid && (event.value.grade==0 || (event.value.public==0 && (param.view!='me' || param.uid != sGlobal.supe_uid)))}">
			<c:set var="hiddencount" value="${hiddencount+1}"></c:set>
			</c:when>
			<c:otherwise>
			<li>
				<div class="event_icon">
					<a href="space.jsp?do=event&id=${event.value.eventid}"><img class="poster_pre" src="${event.value.pic}" alt="${event.value.title}" onerror="this.src='${globalEventClass[event.value.classid].poster}'"></a>
				</div>
				<div class="event_content">
					<h4 class="event_title"><a href="space.jsp?do=event&id=${event.value.eventid}">${event.value.title}</a><span class="gray">[${globalEventClass[event.value.classid].classname}]</span></h4>
					<ul>
						<li>
							<span class="gray">活动时间:</span> 	<jch:date dateformat="MM月dd日 HH:mm" timestamp="${event.value.starttime}"/> - <jch:date dateformat="MM月dd日 HH:mm" timestamp="${event.value.endtime}"/>
							<c:if test="${event.value.grade==0}">
							<span class="event_state"> 待审核</span>
							</c:if>
							<c:choose>
							<c:when test="${event.value.endtime<sGlobal.timestamp}">
							<span class="event_state">已结束</span>
							</c:when>
							<c:when test="${event.value.deadline<sGlobal.timestamp}">
							<span class="event_state">报名截止</span>
							</c:when>
							</c:choose>
						</li>
						<li><span class="gray">活动地点:</span>
							<a href="space.jsp?uid=${param.uid}&do=event&view=${view}&type=${type}&classid=${param.classid}&province=${event.value.province}&date=${param.date}">${event.value.province}</a>
							<a href="space.jsp?uid=${param.uid}&do=event&view=${view}&type=${type}&classid=${param.classid}&province=${event.value.province}&city=${event.value.city}&date=${param.date}">${event.value.city}</a>
							${event.value.location}
						</li>
						<li><span class="gray">发起人:</span> <a href="space.jsp?uid=${event.value.uid}">${sNames[event.value.uid]}</a></li>
						<c:if test="${not empty fevents[event.value.eventid]}">
						<li><span class="gray">好友:</span>
							<c:forEach items="${fevents[event.value.eventid]}" var="value">
							<a href="space.jsp?uid=${value.fuid}">${sNames[value.fuid]}</a>
							<c:choose>
							<c:when test="${value.status== 2}"><span class="gray">参加</span></c:when>
							<c:when test="${value.status== 3}"><span class="gray">组织者</span></c:when>
							<c:when test="${value.status== 4}"><span class="gray">发起者</span></c:when>
							<c:otherwise><span class="gray">关注</span></c:otherwise>
							</c:choose>
							&nbsp;
							</c:forEach>
						</li>
						</c:if>
						<li style="margin: 5px 0 0;">${event.value.viewnum} 次查看&nbsp; ${event.value.membernum} 人参加&nbsp; ${event.value.follownum} 人关注</li>
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
		<c:choose>
		<c:when test="${view=='me'}">
		现在还没有相关的活动
		</c:when>
		<c:otherwise>
		<br/>
		还没有相关的活动。您可以 <a href="cp.jsp?ac=event">发起一个新活动</a>
		</c:otherwise>	
		</c:choose>
	</div>
	</c:otherwise>
	</c:choose>
</div>

<div id="sidebar">

	<c:if test="${view=='all'}">
	<div class="sidebox">			
		<h2 class="title">
			<p class="r_option">
				<a href="space.jsp?uid=${param.uid}&do=event&view=${view}&type=${type}&classid=${param.classid}&province=${param.province}&city=${param.city}">全部</a>
			</p>
			日历
		</h2>
		<div class="calendarbox" id="eventcalendar"></div>
	</div>
	<script type="text/javascript" charset="${jchConfig.charset}">
		function showcalendar(month){
			var month = month ? "&month="+month : "";
			ajaxget('cp.jsp?ac=event&op=calendar' + month + '&date=${param.date}&url=${jch:urlEncoder(theurl)}', 'eventcalendar');
		}
		showcalendar();
	</script>
	</c:if>
	
	<c:if test="${view!='friend'}">
	<div class="sidebox">
		<h2 class="title">
			<p class="r_option">
				<a href="space.jsp?uid=${param.uid}&do=event&view=${view}&type=${type}&date=${param.date}&uid=${param.uid}">全部</a>
			</p>
			分类
		</h2>
		<ul class="event_cat">
		<c:forEach items="${globalEventClass}" var="eventClass">
			<li<c:if test="${param.classid==eventClass.value.classid}"> class="on"</c:if>>
			<a href="space.jsp?uid=${param.uid}&do=event&view=${view}&type=${type}&classid=${eventClass.value.classid}&province=${param.province}&city=${param.city}&date=${param.date}">${eventClass.value.classname}</a></li>
		</c:forEach>
		</ul>
	</div>
	</c:if>

	<c:if test="${not empty followevents}">
	<div class="sidebox">
		<h2 class="title">
			<div class="r_option"><a href="space.jsp?do=event&view=me&type=follow">更多</a></div>
			我关注的活动
		</h2>
		<ul class="attention">
			<c:forEach items="${followevents}" var="value">
			<li style="height: auto;">
				<p>
					<a href="space.jsp?do=event&id=${value.eventid}">${value.title}</a>					
				</p>
				<p class="gray" style="text-align:left">
					<c:choose>
					<c:when test="${sGlobal.timestamp>value.endtime}">
						已结束
					</c:when>
					<c:otherwise>
					<jch:date dateformat="M月d日" timestamp="${value.starttime}"/>
					</c:otherwise>
					</c:choose>&nbsp;		
					${value.membernum} 人参加 /				
					${value.follownum} 人关注
				</p>
				<p>
					<c:if test="${value.threadnum>0}">
					<a href="space.jsp?do=event&id=${value.eventid}&view=thread">
						${value.threadnum} 个话题
					</a> &nbsp;
					</c:if>
					<c:if test="${value.picnum>0}">
					<a href="space.jsp?do=event&id=${value.eventid}&view=pic">
						${value.picnum} 张照片
					</a>
					</c:if>
				</p>
			</li>
			</c:forEach>
		</ul>
	</div>
	</c:if>

	<c:if test="${not empty friendevents}">
	<div class="sidebox">
		<h2 class="title">
			<div class="r_option"><a href="space.jsp?do=event&view=friend">更多</a></div>
			好友最近参加的活动
		</h2>
		<ul class="attention">			
			<c:forEach items="${friendevents}" var="friendEvent">
			<li style="height: auto;">
				<p>
					<a href="space.jsp?do=event&id=${friendEvent.value.eventid}">${friendEvent.value.title}</a>					
				</p>
				<p class="gray" style="text-align:left">
					好友：
					<c:forEach items="${friendEvent.value.friends}" var="fuid">
					<a href="space.jsp?uid=${fuid}" target="_blank">${sNames[fuid]}</a>&nbsp;
					</c:forEach>
				</p>
				<p class="gray" style="text-align:left">
					<c:choose>
					<c:when test="${sGlobal.timestamp>friendEvent.value.endtime}">
						已结束
					</c:when>
					<c:otherwise>
					<jch:date dateformat="M月d日" timestamp="${friendEvent.value.starttime}"/>
					</c:otherwise>
					</c:choose>&nbsp;		
					${friendEvent.value.membernum} 人参加 /				
					${friendEvent.value.follownum} 人关注
				</p>
				<p>
					<c:if test="${friendEvent.value.threadnum>0}">
					<a href="space.jsp?do=event&id=${friendEvent.value.eventid}&view=thread">
						${friendEvent.value.threadnum} 个话题
					</a> &nbsp;
					</c:if>
					<c:if test="${friendEvent.value.picnum>0}">
					<a href="space.jsp?do=event&id=${friendEvent.value.eventid}&view=pic">
						${friendEvent.value.picnum} 张照片
					</a>
					</c:if>
				</p>
			</li>
			</c:forEach>
		</ul>
	</div>
	</c:if>
	
	<c:if test="${not empty hotevents}">
	<div class="sidebox">
		<h2 class="title">
			<div class="r_option"><a href="space.jsp?do=event&view=recommend&type=hot">更多</a></div>
			热门活动
		</h2>
		<ul class="attention">
			<c:forEach items="${hotevents}" var="value">
			<li style="height: auto;">
				<p>
					<a href="space.jsp?do=event&id=${value.eventid}">${value.title}</a>					
				</p>
				<p class="gray" style="text-align:left">
					<c:choose>
					<c:when test="${sGlobal.timestamp>value.endtime}">
						已结束
					</c:when>
					<c:otherwise>
					<jch:date dateformat="M月d日" timestamp="${value.starttime}"/>
					</c:otherwise>
					</c:choose>&nbsp;		
					${value.membernum} 人参加 /				
					${value.follownum} 人关注
				</p>
				<p>
					<c:if test="${value.threadnum>0}">
					<a href="space.jsp?do=event&id=${value.eventid}&view=thread">
						${value.threadnum} 个话题
					</a> &nbsp;
					</c:if>
					<c:if test="${value.picnum>0}">
					<a href="space.jsp?do=event&id=${value.eventid}&view=pic">
						${value.picnum} 张照片
					</a>
					</c:if>
				</p>
			</li>
			</c:forEach>
		</ul>
	</div>
	</c:if>
	
</div>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>