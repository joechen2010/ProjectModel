<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
	<div id="space_photo">
		<h3 class="feed_header">
			<a href="cp.jsp?ac=thread" class="r_option" target="_blank">发表话题</a>
			群组话题(共 ${count} 个)</h3>
		<c:choose>
		<c:when test="${not empty list}">
		<ul class="line_list">
		<c:forEach items="${list}" var="value">
			<li><a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}" target="_blank">${value.subject}</a>
			<span class="gray">&nbsp;<a href="space.jsp?do=mtag&tagid=${value.tagid}" target="_blank">${value.tagname}</a></span></li>
		</c:forEach>
		</ul>
		<div class="page">${multi}</div>
		</c:when>
		<c:otherwise>
		<div class="c_form">还没有话题列表。</div>
		</c:otherwise>
		</c:choose>
	</div><br>
</c:when>
<c:otherwise>

<c:choose>
<c:when test="${space.self}">
<div class="searchbar floatright">
<form method="post" action="space.jsp">
	<input name="searchkey" value="" size="15" class="t_input" type="text">
	<input name="searchsubmit" value="搜索话题" class="submit" type="submit">
	<input type="hidden" name="searchmode" value="1" />
	<input type="hidden" name="do" value="thread" />
	<input type="hidden" name="view" value="all" />
</form>
</div>
<h2 class="title"><img src="image/app/mtag.gif">群组 - 话题</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li class="active"><a href="space.jsp?uid=${space.uid}&do=thread"><span>群组话题</span></a></li>
		<li><a href="space.jsp?do=mtag&view=me"><span>我参与的群组</span></a></li>
		<li><a href="space.jsp?do=mtag&view=manage"><span>我管理的群组</span></a></li>
		<li><a href="space.jsp?do=mtag&view=hot"><span>热门群组</span></a></li>
		<li><a href="space.jsp?do=mtag&view=recommend"><span>推荐群组</span></a></li>
		<li class="null"><a href="cp.jsp?ac=thread">发起新话题</a></li>
		<li class="null"><a href="cp.jsp?ac=mtag">创建群组</a></li>
	</ul>
</div>
<div class="h_status">
	<a href="space.jsp?uid=${space.uid}&do=thread&view=hot"${actives.hot}>热门话题</a><span class="pipe">|</span>
	<a href="space.jsp?uid=${space.uid}&do=thread&view=new"${actives.new}>最新话题</a><span class="pipe">|</span>
	<a href="space.jsp?uid=${space.uid}&do=thread&view=me"${actives.me}>我的话题</a>
</div>
</c:when>
<c:otherwise>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}"/>
<div class="h_status">按照发布时间排序</div>
</c:otherwise>
</c:choose>

<c:if test="${!jch:jchEmpty(searchkey)}">
<div class="h_status">以下是搜索话题 <span style="color:red;font-weight:bold;">${searchkey}</span> 结果列表</div>
</c:if>

<c:choose>
<c:when test="${not empty list}">
<div class="topic_list">
	<table cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<td class="subject">主题</td>
				<td class="mtag">群组</td>
				<td class="author"><c:if test="${param.view!='me'}">作者 </c:if>(回应/阅读)</td>
				<td class="lastpost">最后更新</td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="value" varStatus="key">
			<tr<c:if test="${key.index%2==1}"> class="alt"</c:if>>
				<td class="subject">
					${value.magiceggImage} <a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}" <c:if test="${!jch:jchEmpty(value.magiccolor)}"> class="magiccolor${value.magiccolor}"</c:if>>${value.subject}</a>
					<c:if test="${value.hot>0}">
					<br><span class="gray">${value.hot} 人推荐</span>
					</c:if>
				</td>
				<td><a href="space.jsp?do=mtag&tagid=${value.tagid}">${value.tagname}</a></td>
				<td class="author"><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}">${sNames[value.uid]}</a><br><em>${value.replynum}/${value.viewnum}</em></td>
				<td class="lastpost"><a href="space.jsp?uid=${value.lastauthorid}" title="${sNames[value.lastauthorid]}">${sNames[value.lastauthorid]}</a><br><jch:date dateformat="MM-dd HH:mm" timestamp="${value.lastpost}" format="1"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="page">${multi}</div>
</div>
</c:when>
<c:otherwise>
<div class="c_form">还没有话题列表。</div>
</c:otherwise>
</c:choose>


<c:if test="${not empty rlist}">
	<div style="padding-bottom:10px;">
		<h3 class="l_status">
			<div class="r_option"><a href="space.jsp?do=mtag&view=hot">查看更多</a></div>
			热门群组
		</h3>
		<ul class="thread_list">
		<c:forEach items="${rlist}" var="value">
			<li>
			<table width="100%">
				<tr>
				<td width="80">
					<div class="threadimg60"><a href="space.jsp?do=mtag&tagid=${value.tagid}"><img src="${value.pic}" style="width:60px;"></a></div>
				</td>
				<td>
					<a href="space.jsp?do=mtag&tagid=${value.tagid}" style="font-weight:bold;">${value.tagname}</a>
					<div class="gray">${value.title}</div>
					已有 <span class="num">${value.membernum}</span> 人加入
					<div class="gray">
						话题: ${value.threadnum}, 回帖: ${value.postnum}
					</div>
				</td>
				</tr></table>
			</li>
		</c:forEach>
		</ul>
	</div>
</c:if>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>