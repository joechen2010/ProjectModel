<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_header.jsp')}" />
<div class="c_form">
	<table cellspacing="0" cellpadding="0" class="formtable">
		<c:choose>
			<c:when test="${param.op == 'check'}">
				<caption>
					<h2>申请视频认证</h2>
				</caption>
				<tr>
					<td>${videoFlash}</td>
				</tr>
			</c:when>
			<c:otherwise>
				<caption>
					<h2>${space.videostatus > 0 || !jch:jchEmpty(space.videopic) ? "我的视频认证照片" : "申请视频认证"}</h2>
					<p>
						<c:choose>
							<c:when test="${space.videostatus>0}">以下是您认证的视频照片。<c:if test="${sConfig.videophotochange>0}">您可以 <a href="cp.jsp?ac=videophoto&op=check">重新进入视频认证程序</a>。</c:if></c:when>
							<c:when test="${space.videostatus==0 && !jch:jchEmpty(space.videopic)}">以下是您上传的视频照片。请您耐心等待站长的审核</c:when>
							<c:otherwise>您需要准备好摄像头，然后点击下面的按钮进行视频认证，通常认证通过后，是不允许重复认证的。</c:otherwise>
						</c:choose>
					</p>
				</caption>
				<tr>
					<td>
						<c:choose>
							<c:when test="${space.videostatus > 0 || (space.videostatus==0 && !jch:jchEmpty(space.videopic))}"><img src="${videophoto}" id="avatar" alt="视频头像" width="100" /></c:when>
							<c:when test="${sConfig.videophoto==1}"><a href="cp.jsp?ac=videophoto&op=check"><img src="image/videophoto_btn.gif"></a></c:when>
						</c:choose>
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</table>
	<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'managevideophoto')}">
		<table cellspacing="0" cellpadding="0" class="formtable">
			<caption>
				<h2>视频认证审核</h2>
				<p>您可以进入视频认证平台，自行进行设置认证审核管理</p>
			</caption>
			<tr>
				<td><a href="admincp.jsp?ac=space&videostatus=0&tab=4" class="submit">视频认证审核管理</a></td>
			</tr>
		</table>
	</c:if>
</div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />