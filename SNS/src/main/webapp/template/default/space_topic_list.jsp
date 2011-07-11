<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<h2 class="title"><img src="image/app/topic.gif" />热闹</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li${actives.new}><a href="space.jsp?do=topic&view=new"><span>最新参与</span></a></li>
		<li${actives.hot}><a href="space.jsp?do=topic&view=hot"><span>最热参与</span></a></li>
		<li${actives.me}><a href="space.jsp?do=topic&view=me"><span>我参与的热闹</span></a></li>
		<c:if test="${allowtopic}"><li class="null"><a href="cp.jsp?ac=topic">添加新热闹</a></li></c:if>
	</ul>
	<c:if test="${not empty sGlobal.refer}"><div class="r_option"><a href="${sGlobal.refer}">&laquo; 返回上一页</a></div></c:if>
</div>
<c:choose>
	<c:when test="${not empty list}">
		<div class="event_list">
			<ol>
				<c:forEach items="${list}" var="value">
					<li>
						<div class="event_icon"><a href="space.jsp?do=topic&topicid=${value.topicid}"><img src="${value.pic}"></a></div>
						<div class="hotspot"><a href="space.jsp?do=topic&topicid=${value.topicid}">${value.joinnum}</a></div>
						<div class="event_content">
							<h4 class="event_title"><a href="space.jsp?do=topic&topicid=${value.topicid}">${value.subject}</a></h4>
							<ul>
								<li>${value.message} ...</li>
								<li class="gray">发起作者: <a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></li>
								<li class="gray">发起时间: ${value.dateline}</li>
								<c:if test="${not empty value.endtime}"><li class="gray">参与截止: ${value.endtime}</li></c:if>
								<li class="gray">最后参与: ${value.lastpost}</li>
							</ul>
						</div>
					</li>
				</c:forEach>
			</ol>
		</div>
	</c:when>
	<c:otherwise><div class="c_form">目前还没有相关列表。</div></c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />