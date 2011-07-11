<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:choose>
<c:when test="${op == 'use'}">
	<c:if test="${!frombuy}">
	<h1>使用道具</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	</c:if>
	<div class="toolly" id="__magicuse_form_${mid }">
		<form method="post" id="magicuse_form_${mid }" action="magic.jsp?mid=${mid }&idtype=${idtype }&id=${id }">
			<div class="magic_img">
				<img src="image/magic/${mid }.gif" alt="${magic.name }" />
			</div>
			<div class="magic_info">
				<h3>${magic.name }</h3>
				<p class="gray">${magic.description }</p>
				<p>拥有个数: ${usermagic.count }</p>
				<div>
					<h4>好友名单</h4>
					<div>
						<label for="friendinput">输入好友用户名点添加，最多点 ${magic.custom.maxcall } 个好友</label><br />
						<input class="t_input" type="text" id="friendinput" />
						<input type="button" class="button" value="添加" onclick="addFriendCall();" />
					</div>
					<div id="friends"></div>
					<br style="clear:both;"/>
				</div>
				<p class="btn_line">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<input type="hidden" name="refer" value="${sGlobal.refer }"/>
					<input type="hidden" name="usesubmit" value="1" />
					<input type="button" value="使用" class="submit" onclick="ajaxpost('magicuse_form_${mid }')" />
				</p>
			</div>
		</form>
	</div>
</c:when>
<c:when test="${op == 'show'}">
	<div style="width:420px;">
		<p>
			已通知以下好友
		</p>
		<c:if test="${!empty list}">
		<ul class="avatar_list">
			<c:forEach items="${list}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.fuid }">${jch:avatar2(value.fuid,'small',false,sGlobal,sConfig) }</a></div>
				<p><a href="space.jsp?uid=${value.fuid }" title="${sNames[value.fuid]}">${sNames[value.fuid]}</a></p>
			</li>
			</c:forEach>
		</ul>
		</c:if>
	</div>
</c:when>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>