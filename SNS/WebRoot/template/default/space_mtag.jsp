<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<div class="searchbar floatright">
<form method="get" action="space.jsp">
	<input name="searchkey" value="" size="15" class="t_input" type="text">
	<input name="searchsubmit" value="搜索群组" class="submit" type="submit">
	<input type="hidden" name="searchmode" value="1" />
	<input type="hidden" name="do" value="mtag" />
	<input type="hidden" name="view" value="hot" />
</form>
</div>
<h2 class="title"><img src="image/app/mtag.gif">群组</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li><a href="space.jsp?uid=${space.uid}&do=thread"><span>群组话题</span></a></li>
		<li${actives.me}><a href="space.jsp?do=mtag&view=me"><span>我参与的群组</span></a></li>
		<li${actives.manage}><a href="space.jsp?do=mtag&view=manage"><span>我管理的群组</span></a></li>
		<li${actives.hot}><a href="space.jsp?do=mtag&view=hot"><span>热门群组</span></a></li>
		<li${actives.recommend}><a href="space.jsp?do=mtag&view=recommend"><span>推荐群组</span></a></li>
		<li class="null"><a href="cp.jsp?ac=thread">发起新话题</a></li>
		<li class="null"><a href="cp.jsp?ac=mtag">创建群组</a></li>
	</ul>
</div>

<div class="h_status">
	排序方式：
	<a href="space.jsp?do=mtag&view=${param.view}&fieldid=${param.fieldid}&orderby=threadnum"${orderbys.threadnum}>话题数排行</a><span class="pipe">|</span>
	<a href="space.jsp?do=mtag&view=${param.view}&fieldid=${param.fieldid}&orderby=postnum"${orderbys.postnum}>回帖数排行</a><span class="pipe">|</span>
	<a href="space.jsp?do=mtag&view=${param.view}&fieldid=${param.fieldid}&orderby=membernum"${orderbys.membernum}>成员数排行</a>
</div>

<c:if test="${!jch:jchEmpty(searchkey)}">
<div class="h_status">以下是搜索群组 <span style="color:red;font-weight:bold;">${searchkey}</span> 结果列表</div>
</c:if>
	
<div id="content">

<c:if test="${not empty rlist}">
	<div>
		<h3 class="l_status">
			<div class="r_option"><a href="space.jsp?do=mtag&view=recommend">更多推荐</a></div>
			群组推荐
		</h3>
		<table cellspacing="5" cellpadding="5"><tr>
		<c:forEach items="${rlist}" var="value" varStatus="key">
			<td width="80">
				<div class="threadimg60"><a href="space.jsp?do=mtag&tagid=${value.tagid}"><img src="${value.pic}" style="width:60px;"></a></div>
			</td>
			<td width="200">
				<a href="space.jsp?do=mtag&tagid=${value.tagid}" style="font-weight:bold;">${value.tagname}</a>
				<div class="gray">${value.title}</div>
				已有 <span class="num">${value.membernum}</span> 人加入
				<div class="gray">
					话题: ${value.threadnum}, 回帖: ${value.postnum}
				</div>
			</td>
		<c:if test="${key.index%2==1}"></tr><tr></c:if>
		</c:forEach>
		</tr></table>
	</div>
</c:if>

<c:choose>
<c:when test="${not empty list}">
	<div>
		<c:if test="${not empty rlist}">
		<h3 class="l_status">
			我的群组
		</h3>
		</c:if>
		<table cellspacing="5" cellpadding="5"><tr>
		<c:forEach items="${list}" var="value" varStatus="key">
			<td width="80">
				<div class="threadimg60"><a href="space.jsp?do=mtag&tagid=${value.tagid}"><img src="${value.pic}" style="width:60px;"></a></div>
			</td>
			<td width="200">
				<a href="space.jsp?do=mtag&tagid=${value.tagid}" style="font-weight:bold;">${value.tagname}</a>
				<div class="gray">${value.title}</div>
				已有 <span class="num">${value.membernum}</span> 人加入
				<div class="gray">
					话题: ${value.threadnum}, 回帖: ${value.postnum}
				</div>
			</td>
		<c:if test="${key.index%2==1}"></tr><tr></c:if>
		</c:forEach>
		</tr></table>
	</div>
	
	<div class="page">${multi}</div>

</c:when>
<c:otherwise>
	<div class="c_form">还没有群组。</div>
</c:otherwise>
</c:choose>

</div>

<div id="sidebar">

	<div class="sidebox">
	<h2 class="title">群组分类</h2>
	<ul class="post_list line_list">
		<li${pro_actives.all}><a href="space.jsp?do=mtag&view=${param.view}&orderby=${param.orderby}">全部</a>
		<c:forEach items="${globalProfield}" var="profield">
		<li${pro_actives[profield.value.fieldid]}><a href="space.jsp?do=mtag&view=${param.view}&fieldid=${profield.value.fieldid}&orderby=${param.orderby}">${profield.value.title}</a></li>
		</c:forEach>
	</ul>
	</div>
	
	
	<c:if test="${not empty threadlist}">
	<div class="sidebox">
	<h2 class="title">这些群组的话题更新</h2>
	<ul class="line_list">
		<c:forEach items="${threadlist}" var="value">
			<li>
				<a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}" <c:if test="${!jch:jchEmpty(value.magiccolor)}"> class="magiccolor${value.magiccolor}"</c:if>>${value.subject}</a>
				<c:if test="${value.hot>0}">
				<br><span class="gray">${value.hot} 人推荐</span>
				</c:if>
			</li>
		</c:forEach>
	</ul>
	</div>
	</c:if>

</div>

<!--/sidebar-->
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>