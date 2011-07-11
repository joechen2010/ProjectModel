<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<h2 class="title">
	<img src="image/app/topic.gif" />热闹 - <a href="space.jsp?do=topic&topicid=${topicid}">${topic.subject}</a>
</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li class="active"><a href="javascript:;"><span>凑个热闹</span></a></li>
		<li><a href="space.jsp?do=topic&topicid=${topicid}"><span>查看热闹</span></a></li>
	</ul>
	<c:if test="${jch:checkPerm(pageContext.request, pageContext.response,'managetopic') || topic.uid == sGlobal.supe_uid}">
	<div class="r_option">
		<a href="cp.jsp?ac=topic&op=edit&topicid=${topic.topicid}">编辑</a> | 
		<a href="cp.jsp?ac=topic&op=delete&topicid=${topic.topicid}" id="a_delete_${topic.topicid}" onclick="ajaxmenu(event,this.id);">删除</a>
		</p>
	</div>
	</c:if>
</div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_topic_inc.jsp')}"/>