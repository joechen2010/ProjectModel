<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<h2 class="title"><img src="image/icon/profile.gif">个人设置</h2>
<div class="tabs_header">
	<a href="admincp.jsp" class="r_option">&raquo; 高级管理</a>
	<ul class="tabs">
		<li${actives.profile}><a href="cp.jsp?ac=profile"><span>个人资料</span></a></li>
		<li${actives.avatar}><a href="cp.jsp?ac=avatar"><span>我的头像</span></a></li>
		<c:if test="${sConfig.videophoto==1}"><li${actives.videophoto}><a href="cp.jsp?ac=videophoto"><span>视频认证</span></a></li></c:if>
		<li${actives.credit}><a href="cp.jsp?ac=credit"><span>我的积分</span></a></li>
		<c:if test="${sConfig.allowdomain ==1 && !empty sConfig.domainroot && jch:checkPerm(pageContext.request, pageContext.response,'domainlength')}"><li${actives.domain}><a href="cp.jsp?ac=domain"><span>我的域名</span></a></li></c:if>
		<c:if test="${sConfig.sendmailday>0}"><li${actives.sendmail}><a href="cp.jsp?ac=sendmail"><span>邮件提醒</span></a></li></c:if>
		<li${actives.privacy}><a href="cp.jsp?ac=privacy"><span>隐私筛选</span></a></li>
		<li${actives.theme}><a href="cp.jsp?ac=theme"><span>个性化设置</span></a></li>
	</ul>
</div>