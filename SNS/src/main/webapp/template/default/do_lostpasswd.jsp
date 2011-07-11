<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:choose>
	<c:when test="${op=='email'}">
		<form id="theform" action="do.jsp?ac=lostpasswd&op=email" method="POST" class="c_form">
			<table cellpadding="0" cellspacing="0" class="formtable">
				<caption><h2>取回密码</h2><p>第二步，请正确输入您在本站注册的email地址。</p></caption>
				<tr><th width="120">邮箱</th><td><input type="text" id="email" name="email" value="${email}" class="t_input"> <span id="s_email"></span></td></tr>
				<tr><th>&nbsp;</th><td><input type="submit" id="emailsubmit" name="emailsubmit" value="取回密码" class="submit" /></td></tr>
			</table>
			<input type="hidden" id="username" name="username" value="${username}">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</c:when>
	<c:when test="${op=='reset'}">
		<form id="theform" action="do.jsp?ac=lostpasswd&op=reset" method="POST" class="c_form">
			<table cellpadding="0" cellspacing="0" class="formtable">
				<caption><h2>重设密码</h2></caption>
				<tr><th width="100">用户名</th><td>${space.username}</td></tr>
				<tr><th>新密码</th><td><input type="password" id="newpasswd1" name="newpasswd1" value="" class="t_input"> <span id="s_newpasswd1"></span></td></tr>
				<tr><th>确认新密码</th><td><input type="password" id="newpasswd2" name="newpasswd2" value="" class="t_input"> <span id="s_newpasswd2"></span></td></tr>
				<tr><th>&nbsp;</th><td><input type="submit" id="resetsubmit" name="resetsubmit" value="重设密码" class="submit" /></td></tr>
			</table>
			<input type="hidden" name="uid" value="${param.uid}" />
			<input type="hidden" name="id" value="${param.id}" />
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</c:when>
	<c:otherwise>
		<form id="theform" action="do.jsp?ac=lostpasswd" method="POST" class="c_form">
			<table cellpadding="0" cellspacing="0" class="formtable">
				<caption><h2>取回密码</h2><p>第一步，请输入您在本站注册的用户名。</p></caption>
				<tr><th width="120">用户名</th><td><input type="text" id="username" name="username" value="" class="t_input"> <span id="s_username"></span></td></tr>
				<tr><th>&nbsp;</th><td><input type="submit" id="lostpwsubmit" name="lostpwsubmit" value="下一步" class="submit" /></td></tr>
			</table>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>