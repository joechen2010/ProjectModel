<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<div class="searchbar floatright">
<form method="get" action="space.jsp">
	<input name="searchkey" value="" size="15" class="t_input" type="text">
	<input name="searchsubmit" value="搜索话题" class="submit" type="submit">
	<input type="hidden" name="searchmode" value="1" />
	<input type="hidden" name="tagid" value="${mtag.tagid}" />
	<input type="hidden" name="do" value="mtag" />
	<input type="hidden" name="view" value="${param.view}" />
</form>
</div>
<h2 class="title">
	<img src="image/app/mtag.gif"><a href="space.jsp?do=mtag&id=${mtag.fieldid}">${mtag.title}</a> -
	<a href="space.jsp?do=mtag&tagid=${mtag.tagid}">${mtag.tagname}</a>
</h2>

<div class="tabs_header">
	<ul class="tabs">
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}"><span>首页</span></a></li>
		<li${actives.list}><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=list"><span>讨论区</span></a></li>
		<li${actives.digest}><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=digest"><span>精华区</span></a></li>
		<c:if test="${eventnum>0}">
		<li${actives.event}><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=event"><span>群组活动</span></a></li>
		</c:if>
		<li${actives.member}><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=member"><span>成员列表</span></a></li>
		<c:choose>
		<c:when test="${jch:jchEmpty(mtag.ismember) && mtag.joinperm < 2}">
		<li class="null"><a href="cp.jsp?ac=mtag&op=join&tagid=${mtag.tagid}" id="mtag_join_${mtag.tagid}" onclick="ajaxmenu(event, this.id)">加入该群组</a></li>
		</c:when>
		<c:when test="${mtag.allowpost>0}">	
		<li class="null"><a href="cp.jsp?ac=thread&tagid=${mtag.tagid}">发起新话题</a></li>
		</c:when>
		</c:choose>
	</ul>
</div>

<c:if test="${not empty searchkey}">
<div class="h_status">以下是搜索话题 <span style="color:red;font-weight:bold;">${searchkey}</span> 结果列表</div>
</c:if>

<c:choose>
<c:when test="${not empty list}">
<div class="topic_list">
	<table cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<td class="subject">主题</td>
				<c:if test="${empty mtag}"><td class="mtag">群组</td></c:if>
				<td class="author">作者 (回应/阅读)</td>
				<td class="lastpost">最后更新</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="value" varStatus="key">
			<tr<c:if test="${key.index%2==1}"> class="alt"</c:if>>
				<td class="subject">
				<c:if test="${value.displayorder>0}"> [顶] </c:if>
				<c:if test="${value.digest>0}"> [精] </c:if>
				<c:if test="${value.eventid>0}"> [活] </c:if>
				${value.magiceggImage}
				<a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}" <c:if test="${!jch:jchEmpty(value.magiccolor)}"> class="magiccolor${value.magiccolor}"</c:if>>${value.subject}</a>
				<c:if test="${value.hot>0}">
				<br><span class="gray">${value.hot} 人推荐</span>
				</c:if>
				</td>
				<c:if test="${empty mtag}"><td class="mtag"><a href="space.jsp?do=mtag&tagid=${value.tagid}">${value.tagname}</a></td></c:if>
				<td class="author"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a><br><em>${value.replynum}/${value.viewnum}</em></td>
				<td class="lastpost"><a href="space.jsp?uid=${value.lastauthorid}" title="${sNames[value.lastauthorid]}">${sNames[value.lastauthorid]}</a><br><jch:date dateformat="MM-dd HH:mm" timestamp="${value.lastpost}" format="1"/></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>	
<div class="page">${multi}</div>
</c:when>
<c:otherwise>
<div class="c_form">还没有相关话题。</div>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>