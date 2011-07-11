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
		<form method="post" id="magicuse_form_${mid}" action="magic.jsp?mid=${mid}&idtype=${idtype }&id=${id }">
			<div class="magic_img">
				<img src="image/magic/${mid}.gif" alt="${magic.name }" />
			</div>
			<div class="magic_info">
				<h3>${magic.name }</h3>
				<p class="gray">${magic.description }</p>
				<p>拥有个数: ${usermagic.count }</p>
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
	<div style="width:450px;">
		<c:choose>
		<c:when test="${!empty list}">
		<p>
			用户拥有的道具
		</p>
		<ul>
			<c:forEach items="${list}" var="value">
			<li style="float:left; padding:4px; width:80px; text-align:center;">
				<a href="cp.jsp?ac=magic&view=store&mid=${value.mid }" target="_blank">
					<img src="image/magic/${value.mid }.gif" alt="${globalMagic[value.mid] }" />
				</a>
				<br />
				<a href="cp.jsp?ac=magic&view=store&mid=${value.mid }" target="_blank">${globalMagic[value.mid] }</a>
			</li>
			</c:forEach>
		</ul>
		</c:when>
		<c:otherwise>
		<p>该用户还没有任何道具</p>
		</c:otherwise>
		</c:choose>
	</div>
</c:when>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>