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
		<form method="post" id="magicuse_form_${mid }" action="magic.jsp?mid=${mid }&showid=${param.showid }&target=${param.target }&from=${param.from }">
			<div class="magic_img">
				<img src="image/magic/${mid }.gif" alt="${magic.name }" />
			</div>
			<div class="magic_info">
				<h3>${magic.name }</h3>
				<p class="gray">${magic.description }</p>
				<p>拥有个数: ${usermagic.count }</p>
				<p class="btn_line">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<input type="hidden" name="refer" value="${sGlobal.refer }"/>
					<input type="hidden" name="usesubmit" value="1" />
					<input type="button" value="使用" class="submit" onclick="ajaxpost('magicuse_form_${mid }', '', '', 1)" />
				</p>
			</div>
		</form>
	</div>
</c:when>
<c:when test="${op == 'show'}">

	<p>
		涂鸦完成记住点"保存"
	</p>
	<div id="doodle_bg">
		<object id="show_doodle_${param.showid }" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,45,0" width="438" height="304">
			<param name="movie" value="image/doodle.swf?fid=${param.showid }&oid=${param.target }&from=${param.from }" />
			<param name="quality" value="high" />
			<param name="allowScriptAccess" value="always" />
			<embed src="image/doodle.swf?fid=${param.showid }&oid=${param.target }&from=${param.from }" allowScriptAccess="always" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="438" height="304">
			</embed>
		</object>
	</div>
</c:when>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>