<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${op == 'comment'}">
		<c:forEach items="${list}" var="value">
			<c:set var="comment" value="${value}" scope="request"></c:set>
			<jsp:include page="${jch:template(sConfig, sGlobal, 'space_comment_li.jsp')}"/>
		</c:forEach>
	</c:when>
	<c:when test="${op == 'getfriendgroup'}">
	${group}
	</c:when>
	<c:when test="${op == 'getfriendname'}">
	${groupname}
	</c:when>
	<c:when test="${op == 'getmtagmember'}">
		<!--{if $tagspace[grade]==9}-->群主
	<!--{elseif $tagspace[grade]==8}-->副群主
	<!--{elseif $tagspace[grade]==1}-->明星
	<!--{elseif $tagspace[grade]==-1}-->待审核
	<!--{elseif $tagspace[grade]==-2}-->禁言
	<!--{else}-->普通会员
	<!--{/if}-->
	</c:when>
	<c:when test="${op == 'post'}">
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_post_li.jsp')}"/>
	</c:when>
	<c:when test="${op == 'share'}">
		<c:if test="${!empty share}">
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_share_li.jsp')}" />
		</c:if>
	</c:when>
	<c:when test="${op == 'album'}">
		<table cellspacing="2" cellpadding="2">
			<tr>
				<c:forEach items="${piclist}" var="value" varStatus="status">
				<td>
					<img src="${value.pic}" width="60"
						onclick="insertImage('${value.bigpic}');" style="cursor: hand;">
				</td>
				<c:if test="${status.index % 5 == 4}">
			</tr>
			<tr>
				</c:if>
				</c:forEach>
			</tr>
		</table>
		<div class="page">
			${multi}
		</div>
	</c:when>
	<c:when test="${op == 'getreward'&&(rule.credit>0||rule.experience>0)}">
		<div class="popupmenu_layer">
			<p>${rule.rulename}</p>
			<p class="btn_line">
				<c:if test="${rule.credit>0}">积分 <strong>+${rule.credit}</strong></c:if>
				<c:if test="${rule.experience>0}">经验 <strong>+${rule.experience}</strong></c:if>
			</p>
			<c:if test="${rule.cyclenum>0}"><p>本周期内，您还有 ${rule.cyclenum} 次机会</p></c:if>
		</div>
	</c:when>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />