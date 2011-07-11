<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<div class="digc">
	<table>
		<tr>
		<c:forEach items="${clicks}" var="click" varStatus="stat">
		<c:set var="height" value="${maxclicknum >0 ? click.value.clicknum*50/maxclicknum : 0}"></c:set>
		<c:set var="clicknum" value="${clicknum+click.value.clicknum}"></c:set>
		<c:set var="clickid" value="${click.value.clickid}"></c:set>
			<td valign="bottom"><a href="cp.jsp?ac=click&op=add&clickid=${click.value.clickid}&idtype=${idtype}&id=${id}&hash=${hash}" id="click_${idtype}_${id}_${click.value.clickid}" onclick="ajaxmenu(event, this.id, 0, 2000, 'show_click')"><c:if test="${click.value.clicknum>0}"><div class="digcolumn"><div class="digchart dc${click.value.classid}" style="height:<fmt:formatNumber type="number" value="${height}"  pattern="0"/>px;"><em>${click.value.clicknum}</em></div></div></c:if><div class="digface"><img src="image/click/${click.value.icon }" alt="" /><br />${click.value.name}</div></a></td>
		</c:forEach>
		</tr>
	</table>
</div>

<c:if test="${not empty clickuserlist}">
<div class="trace" style="padding-bottom: 10px;">

	<div>
		<h2>
			刚表态过的朋友 (<a href="javascript:;" onclick="show_click('click_${idtype}_${id}_${clickid}')">${clicknum} 人</a>)
			<c:if test="${not empty globalMagic.anonymous}">
			<img src="image/magic/anonymous.small.gif" class="magicicon" />
			<a id="a_magic_anonymous" href="magic.jsp?mid=anonymous&idtype=${idtype}&id=${id}" onclick="ajaxmenu(event,this.id, 1)" class="gray">${globalMagic.anonymous}</a>
			</c:if>
		</h2>
	</div>
	<div id="trace_div">
		<ul class="avatar_list" id="trace_ul">
		<c:forEach items="${clickuserlist}" var="value">
		<li>
			<c:choose>
			<c:when test="${not empty value.username}">
			<div class="avatar48"><a href="space.jsp?uid=${value.uid}" target="_blank" title="${value.clickname}">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
			<p><a href="space.jsp?uid=${value.uid}"  title="${sNames[value.uid]}" target="_blank">${sNames[value.uid]}</a></p>
			</c:when>
			<c:otherwise>
			<div class="avatar48"><img src="image/magic/hidden.gif" alt="${value.clickname}" class="avatar" /></div>
			<p>匿名</p>
			</c:otherwise>
			</c:choose>
		</li>
		</c:forEach>
		</ul>
	</div>
</div>
</c:if>

<c:if test="${not empty click_multi}"><div class="page">${click_multi}</div></c:if>
