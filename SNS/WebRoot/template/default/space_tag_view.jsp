<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title">标签 - ${tag.tagname} (${tag.blognum} 篇日志)</h2>
<div class="tabs_header">
	<a href="cp.jsp?ac=share&type=tag&id=${tag.tagid}" id="a_share" onclick="ajaxmenu(event, this.id, 1)" class="a_share">分享</a>
	<ul class="tabs">
		<li class="active"><a href="space.jsp?do=tag&id=${tag.tagid}"><span>日志列表</span></a></li>
		<li><a href="space.jsp?do=tag"><span>返回标签列表</span></a></li>
	</ul>
</div>

<div class="topic_list">
	<table cellspacing="0" cellpadding="0" class="infotable">
		<thead>
			<tr>
				<td class="subject">标题</td>
				<td class="author">作者(回复数)</td>
				<td class="lastpost">发布时间</td>
			</tr>
		</thead>
		<c:forEach items="${list}" var="value" varStatus="key">
		<tr<c:if test="${key.index%2==1}"> class="alt"</c:if>>
			<td class="subject"><a href="space.jsp?uid=${value.uid}&do=blog&id=${value.blogid}" target="_blank">${value.subject}</a></td>
			<td class="author"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a><em>(${value.replynum})</em></td>
			<td class="lastpost"><jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1"/></td>
		</tr>
		</c:forEach>
	</table>
	
	<c:if test="${prinum>0}">
	<div class="notice">本页有 ${prinum} 篇日志因隐私设置而隐藏</div>
	</c:if>
	
	<div class="page">${multi}</div>
</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>