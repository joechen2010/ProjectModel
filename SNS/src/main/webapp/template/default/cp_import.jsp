<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title"><img src="image/app/blog.gif" />日志</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li><a href="cp.jsp?ac=blog"><span>发表新日志</span></a></li>
		<li class="active"><a href="cp.jsp?ac=import"><span>日志导入</span></a></li>
		<li><a href="space.jsp?uid=${space.uid}&do=blog&view=me"><span>返回我的日志</span></a></li>
	</ul>
</div>

<c:choose>
<c:when test="${!empty results}">

<form method="post" action="cp.jsp?ac=import" class="c_form">
<table cellspacing="0" cellpadding="0" class="formtable">
<caption>
	<h2><c:choose><c:when test="${incount != 0}">导入结果</c:when><c:otherwise>选择要导入的日志</c:otherwise></c:choose></h2>
	<c:if test="${paycredit > 0}">
	<p>使用本功能需要支付积分: ${paycredit} (您现在的积分数 <a href="cp.jsp?ac=credit">${space.credit}</a>)</p>
	</c:if>
</caption>

<c:forEach items="${results}" var="value" varStatus="st">
<tr>
	<td><c:if test="${incount == 0}"><input type="checkbox" name="ids[]" value="${st.index}"></c:if> <c:choose><c:when test="${value.blogid > 0}"><a href="space.jsp?uid=${space.uid}&do=blog&id=${value.blogid}" target="_blank">${value.title}</a></c:when><c:otherwise>${value.title}</c:otherwise></c:choose></td>
	<td>${value.dateCreated}</td>
	<c:if test="${incount != 0}"><td>${value.status}</td></c:if>
</tr>
</c:forEach>

<c:if test="${incount == 0}">
<tr>
	<td colspan="3">
	<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选
	<input type="submit" name="import2submit" value="导入" class="submit" />
	<input type="submit" name="resubmit" value="重置" class="submit" />
	</td>
</tr>
</c:if>

</table>

<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>

</c:when>
<c:otherwise>

<form method="post" action="cp.jsp?ac=import" class="c_form">
<table cellspacing="0" cellpadding="0" class="formtable">
	<caption>
		<h2>日志导入</h2>
		<p>本空间支持采用XML-RPC规范的MetaWeblog API<br />
		地址为: ${siteurl}xmlrpc.jsp<br />
		通过本接口，你可以轻松的将你在非本站空间上面的日志批量搬入到当前空间上面来；<br />
		这需要你原来的空间也要提供XMLRPC规范的API接口，详情可以咨询原来空间站点的管理员
		</p>
	</caption>
	<c:if test="${paycredit > 0}">
	<tr><th width="120">需要积分</th><td>使用本功能需要支付积分: ${paycredit} (您现在的积分数 <a href="cp.jsp?ac=credit">${space.credit}</a>)</td></tr>
	</c:if>
	<tr><th width="120">原空间API访问地址</th><td><input type="text" class="t_input" name="url" value="http://" size="40" />
		<br />通常为类似 http://j.jsprun.net/xmlrpc.jsp 这样的URL链接
		</td></tr>
	<tr><th>原空间用户名</th><td><input type="text" class="t_input" name="username" value="" /></td></tr>
	<tr><th>原空间密码</th><td><input type="password" class="t_input" name="password" value="" /></td></tr>
	<tr><th>单次获取日志数</th><td>${sConfig.importnum}</td></tr>
	<tr><th>&nbsp;</th><td><input type="submit" name="importsubmit" value="提交" class="submit" /></td></tr>
</table>

<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
	

</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>