<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<form method="post" action="do.jsp?ac=inputpwd" class="c_form">
	<table cellpadding="0" cellspacing="0" class="formtable">
		<caption>
			<h2>密码验证</h2>
			<p>您需要正确输入密码后才能继续查看</p>
		</caption>
		<tr>
			<th width="100">输入密码</th>
			<td><input type="password" name="viewpwd" value="" class="t_input" /></td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>
				<input type="hidden" name="refer" value="${requestURI}" />
				<input type="hidden" name="blogid" value="${invalue.blogid}" />
				<input type="hidden" name="albumid" value="${invalue.albumid}" />
				<input type="hidden" name="pwdsubmit" value="true" />
				<input type="submit" name="submit" value="提交" class="submit" />
			</td>
		</tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />