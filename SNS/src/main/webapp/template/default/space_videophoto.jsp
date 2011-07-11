<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

	<h1>视频认证照</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	<div class="popupmenu_inner">
		<p>这是已经通过审核的认证照片</p>
		<p><a href="${videophoto}" target="_blank"><img src="${videophoto}" alt="视频认证照" width="100"></a></p>
	</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>