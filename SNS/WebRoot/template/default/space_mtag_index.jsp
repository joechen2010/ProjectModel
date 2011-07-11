<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title">
	<img src="image/app/mtag.gif"><a href="space.jsp?do=mtag&id=${mtag.fieldid}">${mtag.title}</a> -
	<a href="space.jsp?do=mtag&tagid=${mtag.tagid}">${mtag.tagname}</a>
</h2>
<div class="tabs_header">
	<a href="cp.jsp?ac=share&type=mtag&id=${mtag.tagid}" id="a_share" class="a_share" onclick="ajaxmenu(event, this.id, 1)">分享</a>
	<div class="r_option">
		<c:if test="${managemtag}">
		<a href="admincp.jsp?ac=mtag&tagid=${mtag.tagid}" target="_blank">群组管理</a><span class="pipe">|</span>
		</c:if>
		<a href="cp.jsp?ac=common&op=report&idtype=tagid&id=${mtag.tagid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">举报</a><span class="pipe">|</span>
	</div>
	<ul class="tabs">
		<li class="active"><a href="javascript:;"><span>首页</span></a></li>
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=list"><span>讨论区</span></a></li>
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=digest"><span>精华区</span></a></li>
		<c:if test="${eventnum>0}">
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=event"><span>群组活动</span></a></li>
		</c:if>
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=member"><span>成员列表</span></a></li>
		<c:if test="${mtag.allowthread>0}">
		<li class="null"><a href="cp.jsp?ac=thread&tagid=${mtag.tagid}">发起新话题</a></li>
		</c:if>
		<c:if test="${jch:jchEmpty(mtag.ismember) && mtag.joinperm<2}">
		<li class="null"><a href="cp.jsp?ac=mtag&op=join&tagid=${mtag.tagid}" id="mtag_join_${mtag.tagid}" onclick="ajaxmenu(event, this.id)">加入该群组</a></li>	
		</c:if>
	</ul>
</div>

<div id="content">

	<div class="affiche">
		<div id="space_avatar" class="floatleft"><img src="${mtag.pic}" alt="${mtag.tagname}" width="48" /></div>
		<h3>公告</h3>
		<div class="article">
			${!jch:jchEmpty(mtag.announcement) ? mtag.announcement : '还没有公告'}
		</div>
	</div>

	<div class="topic_list">
	<c:choose>
	<c:when test="${not empty list}">
		<table cellspacing="0" cellpadding="0">
			<thead>
				<tr>
					<td class="subject">主题</td>
					<td class="author"><c:if test="${param.view!='me'}">作者 </c:if>(回应/阅读)</td>
					<td class="lastpost">最后更新</td>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${list}" var="value" varStatus="key">
				<tr <c:if test="${key.index%2==1}"> class="alt"</c:if>>
					<td class="subject">
					<!--<div style="width:290px;word-break:break-all;">-->
					<c:if test="${value.displayorder>0}"> [顶] </c:if>
					<c:if test="${value.digest>0}"> [精] </c:if>
					<c:if test="${value.eventid>0}"> [活] </c:if>
					${value.magiceggImage}
					<a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}" <c:if test="${!jch:jchEmpty(value.magiccolor)}"> class="magiccolor${value.magiccolor}"</c:if>>${value.subject}</a>
					<c:if test="${value.hot>0}">
					<br><span class="gray">${value.hot} 人推荐</span>
					</c:if>
					<!--</div>-->
					</td>
					<td class="author"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a><br>${value.replynum}/${value.viewnum}</td>
					<td class="lastpost"><a href="space.jsp?uid=${value.lastauthorid}" title="${sNames[value.lastauthorid]}">${sNames[value.lastauthorid]}</a><br><jch:date dateformat="MM-dd HH:mm" timestamp="${value.lastpost}" format="1"/></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		<div class="r_option">
		<a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=list">查看全部话题列表</a>
		</div>
	</c:when>
	<c:otherwise>
		<div class="c_form">
			<c:choose>
				<c:when test="${jch:jchEmpty(mtag.allowview)}">
					本群组目前不是公开状态，只对内部成员开放。<br>
					<c:choose>
						<c:when test="${mtag.grade==-2}">
							您的加入申请审核中。请等候。
						</c:when>
						<c:when test="${mtag.joinperm==1}">
							您可以<a href="cp.jsp?ac=mtag&op=join&tagid=${mtag.tagid}" id="a_mtag_join_${mtag.tagid}" onclick="ajaxmenu(event, this.id)">选择加入该群组</a>，但你的申请需要经群主审核后才有效。
						</c:when>
						<c:when test="${mtag.joinperm==2}">
							您需要在收到群主的邀请后才能加入该群组。
						</c:when>
						<c:otherwise>
							您可以立即<a href="cp.jsp?ac=mtag&op=join&tagid=${mtag.tagid}" id="a_mtag_join_${mtag.tagid}" onclick="ajaxmenu(event, this.id)">选择加入该群组</a>。
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					还没有话题。
				</c:otherwise>
			</c:choose>
		</div>
	</c:otherwise>
	</c:choose>
	</div>
	
</div>

<div id="sidebar">



	<div class="sidebox">
		<h2 class="title">群组菜单</h2>
		<ul class="menu_list">
			<c:if test="${mtag.allowthread>0}">
			<li><a href="cp.jsp?ac=thread&tagid=${mtag.tagid}">发起话题</a></li>
			</c:if>
			<c:if test="${jch:jchEmpty(mtag.ismember) && mtag.joinperm<2}">
			<li><a href="cp.jsp?ac=mtag&op=join&tagid=${mtag.tagid}" id="a_mtag_join_${mtag.tagid}" onclick="ajaxmenu(event, this.id)">加入群组</a></li>	
			</c:if>
			
			<c:if test="${mtag.grade== 9}">
			<li><a href="cp.jsp?ac=event&tagid=${mtag.tagid}">发起活动</a></li>
			</c:if>
			<c:if test="${mtag.grade >= 8}">
			<li><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=base">群组设置</a></li>
			</c:if>
			<c:choose>
			<c:when test="${mtag.grade >= 8}">
			<li><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members">成员管理</a></li>
			<li><a href="admincp.jsp?ac=thread&perpage=20&tagid=${mtag.tagid}&searchsubmit=1">话题管理</a></li>
			<li><a href="admincp.jsp?ac=post&perpage=20&tagid=${mtag.tagid}&searchsubmit=1">回帖管理</a></li>
			</c:when>
			<c:when test="${mtag.ismember>0 && mtag.closeapply==0}">
			<li><a href="cp.jsp?ac=mtag&op=apply&tagid=${mtag.tagid}" id="a_apply" onclick="ajaxmenu(event, this.id)">群主申请</a></li>
			</c:when>
			</c:choose>
			<c:if test="${mtag.allowinvite>0}">
			<li><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=invite">邀请好友</a></li>
			</c:if>
			<c:if test="${mtag.ismember>0}">
			<li><a href="cp.jsp?ac=mtag&op=out&tagid=${mtag.tagid}" id="a_ignore_top" onclick="ajaxmenu(event, this.id)">退出群组</a></li>
			</c:if>
		</ul>
	</div>


	<div class="sidebox">
		<h2 class="title">群组概况</h2>
		<div style="padding:0 0 0 40px;">
			<p>成员数：${mtag.membernum}</p>
			<p>话题数：${mtag.threadnum}</p>
			<p>回帖数：${mtag.postnum}</p>
		</div>
	</div>

	<c:if test="${not empty modlist}">
	<div class="sidebox">
		<h2 class="title">
			<span class="r_option"><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=member">全部</a></span>
			群主
		</h2>
		<ul class="avatar_list">
		<c:forEach items="${modlist}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal,sConfig)}</a></div>
				<p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></p>
			</li>
		</c:forEach>
		</ul>
	</div>
	</c:if>
<c:if test="${not empty checklist}">
	<div class="sidebox">
		<h2 class="title">
		<span class="r_option"><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members&grade=-2">立即处理</a></span>
		有新的待审核成员
		</h2>
		<ul class="avatar_list">
		<c:forEach items="${checklist}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal,sConfig)}</a></div>
				<p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></p>
			</li>
		</c:forEach>
		</ul>
	</div>
</c:if>
<c:if test="${not empty starlist}">
	<div class="sidebox">
		<h2 class="title">明星成员</h2>
		<ul class="avatar_list s_clear">
		<c:forEach items="${starlist}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal,sConfig)}</a></div>
				<p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></p>
			</li>
		</c:forEach>
		</ul>
	</div>
</c:if>
	<c:if test="${not empty memberlist}">
	<div class="sidebox">
		<h2 class="title">
			<span class="r_option"><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=member">全部(${mtag.membernum})</a></span>
			普通成员
		</h2>
		<ul class="avatar_list">
		<c:forEach items="${memberlist}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal,sConfig)}</a></div>
				<p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></p></li>
		</c:forEach>
		</ul>
	</div>
	</c:if>

</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>