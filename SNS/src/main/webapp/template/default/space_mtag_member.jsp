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
		<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=event"><span>群组活动</span></a></li>
		</c:if>
		<li class="active"><a href="space.jsp?do=mtag&tagid=${mtag.tagid}&view=member"><span>成员列表</span></a></li>
		<c:if test="${mtag.allowpost>0}"><li class="null"><a href="cp.jsp?ac=thread&tagid=${tagid}">发起新话题</a></li></c:if>
	</ul>
</div>

<script>
function searchFriend() {
	$('searchform').submit();
}
</script>

<div class="h_status">
<form name="searchform" id="searchform" method="get" action="space.jsp?do=mtag&tagid=${mtag.tagid}&view=member">
<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td>
			成员列表
			<c:if test="${mtag.grade>=8}"><span class="pipe">|</span><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members">成员批量管理</a></c:if>
			</td>
		<td align="right">
			<table cellspacing="0" cellpadding="0">
				<tr>
					<td style="padding: 0;"><input type="text" id="key" name="key" value="搜索成员" onfocus="if(this.value=='搜索成员')this.value='';" class="t_input" tabindex="1" style="width: 160px; border-right: none;" /></td>
					<td style="padding: 0;"><a href="javascript:searchFriend();"><img src="image/search_btn.gif" alt="搜索" /></a></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<input type="hidden" name="do" value="mtag">
<input type="hidden" name="tagid" value="${mtag.tagid}">
<input type="hidden" name="view" value="member">
<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" /></form>
</div>

<div class="thumb_list">
	<c:choose>
	<c:when test="${not empty list}">
	<table cellspacing="6" cellpadding="0">
	<c:forEach items="${list}" var="value" varStatus="key">
		<tr>
			<td class="thumb <c:if test="${!jch:jchEmpty(ols[value.uid])}">online</c:if>">
				<table cellpadding="4" cellspacing="4">
					<tr>
						<td class="image">
						<div class="ar_r_t"><div class="ar_l_t"><div class="ar_r_b"><div class="ar_l_b">
						<a href="space.jsp?uid=${value.uid}">${jch:avatar2(value.uid, 'big', false, sGlobal, sConfig)}</a>
						</div></div></div></div>
						</td>
						<td>
							<h6>
								<a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a>
								<c:if test="${!jch:jchEmpty(value.videostatus)}">
								<img src="image/videophoto.gif" align="absmiddle"> <span class="gray">已通过视频认证</span>
								</c:if>
							</h6>
							<p class="l_status">
							<c:if test="${mtag.grade>=8}">
								<a href="cp.jsp?ac=mtag&op=manage&subop=member&tagid=${mtag.tagid}&uid=${value.uid}" id="a_mod_${key.index}" onclick="ajaxmenu(event, this.id, 1)" class="r_option" style="padding-left:0.5em;">管理该成员</a>
							</c:if>
								<span class="r_option gray" id="mtag_member_${value.uid}">
								<c:choose>
								<c:when test="${value.grade==9}">群主</c:when>
								<c:when test="${value.grade==8}">副群主</c:when>
								<c:when test="${value.grade==1}">明星</c:when>
								<c:when test="${value.grade==-1}">禁言</c:when>
								<c:when test="${value.grade==-2}">待审核</c:when>
								</c:choose>
								</span>
								<a href="cp.jsp?ac=friend&op=add&uid=${value.uid}" id="a_friend_${key.index}" onclick="ajaxmenu(event, this.id, 1)">加为好友</a>
								<span class="pipe">|</span><a href="cp.jsp?ac=pm&uid=${value.uid}" id="a_pm_${key.index}" onclick="ajaxmenu(event, this.id, 1)">发短消息</a>
								<span class="pipe">|</span><a href="cp.jsp?ac=poke&op=send&uid=${value.uid}" id="a_poke_${key.index}" onclick="ajaxmenu(event, this.id, 1)">打招呼</a>
							</p>
							
							<c:if test="${!jch:jchEmpty(ols[value.uid])}">
							<p><span class="gray">动作：</span><jch:date dateformat="MM-dd HH:mm" timestamp="${ols[value.uid]}" format="1"/></p>
							</c:if>

							<c:if test="${!jch:jchEmpty(value.resideprovince) || !jch:jchEmpty(value.residecity)}">
							<p><span class="gray">地区：</span><a href="cp.jsp?ac=friend&op=search&resideprovince=${value.p}&residecity=&searchmode=1">${value.resideprovince}</a> <a href="cp.jsp?ac=friend&op=search&resideprovince=${value.p}&residecity=${value.c}&searchmode=1">${value.residecity}</a></p>
							</c:if>
	
							<c:if test="${!jch:jchEmpty(value.note)}">
							<p><span class="gray">状态：</span>${value.note}</p>
							</c:if>

						</td>
					</tr>
				</table>
			</td>
		</tr>
	</c:forEach>
	</table>
	</c:when>
	<c:otherwise>
	<div class="c_form">没有找到可浏览的成员信息。</div>
	</c:otherwise>
	</c:choose>
</div>

<div class="page">${multi}</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>