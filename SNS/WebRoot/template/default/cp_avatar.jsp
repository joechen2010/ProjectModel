<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_header.jsp')}" />
<script type="text/javascript">
	function updateavatar() {
		window.location.reload();
	}
</script>
<form method="post" action="cp.jsp?ac=avatar&ref" class="c_form">
	<table cellspacing="0" cellpadding="0" class="formtable">
		<caption>
			<h2>当前我的头像</h2>
			<p>如果您还没有设置自己的头像，系统会显示为默认头像，您需要自己上传一张新照片来作为自己的个人头像。</p>
		</caption>
		<tr valign="top">
			<td>${jch:avatar2(space.uid,"big",false,sGlobal,sConfig)}</td>
			<td>${jch:avatar2(space.uid,"middle",false,sGlobal,sConfig)}</td>
			<td>${jch:avatar2(space.uid,"small",false,sGlobal,sConfig)}</td>
			<td width="100%"></td>
		</tr>
	</table>
	<table cellspacing="0" cellpadding="0" class="formtable">
		<caption>
			<h2>设置我的新头像</h2>
			<p>请选择一个新照片进行上传编辑。</p>
		</caption>
		<tr><td>${avatarFlash}</td></tr>
		<tr><td>提示：头像保存后，您可能需要刷新一下本页面(按F5键)，才能查看最新的头像效果。</td></tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />