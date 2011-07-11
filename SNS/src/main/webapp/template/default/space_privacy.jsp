<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<div class="c_form">

<h2 class="l_status title">
	<c:choose>
	<c:when test="${sGlobal.realname_privacy}">
		${sNames[space.uid]} 为实名认证用户，你需要通过实名认证后才能继续访问
	</c:when>
	<c:otherwise>
		由于 ${sNames[space.uid]} 的隐私设置，你不能访问当前内容
	</c:otherwise>
	</c:choose>
</h2>

<div class="thumb_list">
	<table cellpadding="0" cellspacing="0">
		<tr>
			<td class="image">
				<div class="ar_r_t"><div class="ar_l_t"><div class="ar_r_b"><div class="ar_l_b">
				<a href="space.jsp?uid=${space.uid}">${jch:avatar2(space.uid, 'big', false, sGlobal, sConfig)}</a>
				</div></div></div></div>
			</td>
			<td>
				<h6><a href="space.jsp?uid=${space.uid}">${sNames[space.uid]}</a></h6>
				<p class="l_status">
					<a href="space.jsp?uid=${space.uid}&do=friend">查看好友列表</a>
					<c:choose>
					<c:when test="${space.isfriend}">
					<span class="pipe">|</span><a href="cp.jsp?ac=friend&op=ignore&uid=${space.uid}" id="a_ignore" onclick="ajaxmenu(event, this.id)">解除好友关系</a>
					</c:when>
					<c:otherwise>
					<span class="pipe">|</span><a href="cp.jsp?ac=friend&op=add&uid=${space.uid}" id="a_friend" onclick="ajaxmenu(event, this.id, 1)">加为好友</a>
					</c:otherwise>
					</c:choose>
					<span class="pipe">|</span><a href="cp.jsp?ac=poke&op=send&uid=${space.uid}" id="a_poke" onclick="ajaxmenu(event, this.id, 1)">打招呼</a>
					<span class="pipe">|</span><a href="cp.jsp?ac=pm&uid=${space.uid}" id="a_pm" onclick="ajaxmenu(event, this.id, 1)">发短消息</a>
					<span class="pipe">|</span><a href="cp.jsp?ac=common&op=report&idtype=uid&id=${space.uid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">违规举报</a>
					<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'managename') || jch:checkPerm(pageContext.request,pageContext.response,'managespacegroup') || jch:checkPerm(pageContext.request,pageContext.response,'managespaceinfo') || jch:checkPerm(pageContext.request,pageContext.response,'managespacecredit') || jch:checkPerm(pageContext.request,pageContext.response,'managespacenote')}">
					<span class="pipe">|</span><a id="a_manage" href="admincp.jsp?ac=space&op=manage&uid=${space.uid}">管理用户</a>
					</c:if>
				</p>
				<c:if test="${not empty space.note}">
				<p>${space.note}</p>
				</c:if>
				<c:if test="${not empty space.resideprovince || not empty space.residecity}">
				<p><span class="gray">地区：</span>${space.resideprovince} ${space.residecity}</p>
				</c:if>
				<c:if test="${!space.isfriend}">
				<p style="padding:20px 0 0 0;font-weight:bold;">${sNames[space.uid]} 有 ${space.friendnum} 名好友, ${space.credit} 个积分, ${space.viewnum} 个浏览量</p>
				<p>与${sNames[space.uid]}成为好友后，您可以第一时间关注到${sNames[space.uid]}的更新信息。</p>
				<p style="padding:10px 0 0 0;"><a href="cp.jsp?ac=friend&op=add&uid=${space.uid}" id="a_friend" onclick="ajaxmenu(event, this.id, 1)" class="submit">加为好友</a></p>
				</c:if>
			</td>
		</tr>
	</table>
</div>

</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>