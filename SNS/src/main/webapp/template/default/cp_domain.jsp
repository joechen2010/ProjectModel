<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_header.jsp')}"/>

<div class="c_form">
	<form method="post" action="cp.jsp?ac=domain">
	<table cellspacing="0" cellpadding="0" class="formtable">
		<caption>
			<h2>空间域名</h2>
			<p>域名可使用最少 ${domainlength} 个 ，最多 30 个的字母或数字组合，且必须字母开头。</p>
		</caption>
		<c:if test="${!empty space.domain && reward.credit > 0}">
		<tr>
			<th width="100">积分</th>
			<td>修改域名将需要支付积分 ${reward.credit} 个，您现在有积分 ${space.credit} 个</td>
		</tr>
		</c:if>
		<c:if test="${!empty space.domain && reward.experience > 0}">
		<tr>
			<th width="100">经验</th>
			<td>修改域名将需要支付经验 ${reward.experience} 个，您现在有经验 ${space.experience} 个</td>
		</tr>
		</c:if>
		<tr>
			<th width="100">空间域名</th>
			<td>http://<input type="text" name="domain" value="${space.domain}" size="10" class="t_input" />.${sConfig.domainroot}</td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td><input type="submit" name="domainsubmit" value="修改域名" class="submit" /></td>
		</tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" /> 
	</form>
</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>