<%@ page language="java"  pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
<div class="maininner">
	<form method="post" action="admincp.jsp?ac=app">
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
	<div class="bdrcontent">
	<p>以下列出的是JavaCenter中已经安装的所有产品应用。</p>
	<p>当您安装了新的基于JavaCenter的应用后，需要在本页面进行配置修改，并提交更新后，新安装的应用才能在JavaCenter Home中显示。</p>
	<p>如果您发现某个应用的信息有误，请到JavaCenter的管理平台进行应用参数的修正。</p>
	</div>
	<br>
	
	<c:forEach items="${appList}" var="data">
	<c:set var="template" value="${jch:stripSlashes(jch:htmlSpecialChars1(!jch:jchEmpty(relatedtag['data'][data.appid].template) ? relatedtag['data'][data.appid].template : data['tagtemplates'].template))}"/>
	<c:set var="name" value="${jch:stripSlashes(jch:htmlSpecialChars1(!jch:jchEmpty(relatedtag['data'][data.appid].name) ? relatedtag['data'][data.appid].name : data.name))}"/>
	<c:choose>
	<c:when test="${empty relatedtag['data'][data.appid].open || relatedtag['data'][data.appid].open>0}">
	<c:set var="yes" value="checked='checked'"/>
	<c:set var="no" value=""/>
	</c:when>
	<c:otherwise>
	<c:set var="no" value="checked='checked'"/>
	</c:otherwise>
	</c:choose>
	<div class="bdrcontent">
	<div class="title">
		<h3>${data.name}</h3>
	</div>
	<table cellspacing="0" cellpadding="0" class="formtable">
	<tr>
		<th style="width: 200px;">显示名称:</th>
		<td><input type="text" name="relatedtag[data][${data.appid}][name]" value="${name}"></td>
	</tr>
	<tr>
		<th>访问地址:</th>
		<td>
		<a href="${data.url}" target="_blank">${data.url}</a>
		<input type="hidden" name="relatedtag[data][${data.appid}][url]" value="${data.url}" size="50"></td>
	</tr>
	<tr>
		<th>导航中是否显示:</th>
		<td>
		<c:choose>
		<c:when test="${data.appid==jchConfig.JC_APPID}">
		<input type="hidden" name="relatedtag[data][${data.appid}][open]" value="0">当前产品，不需要选择
		</c:when>
		<c:otherwise>
		<input type="radio" name="relatedtag[data][${data.appid}][open]" value="1" ${yes}>是
		<input type="radio" name="relatedtag[data][${data.appid}][open]" value="0" ${no}>否
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
	<tr>
		<th>标签相关显示条数:</th>
		<td><input type="text" size="4" name="relatedtag[limit][${data.appid}]" value="${relatedtag['limit'][data.appid]}" />
			设置为 0，则不显示本产品的标签相关。</td>
	</tr>
	<tr>
		<th>标签相关单条模板:<br>
		<c:if test="${jch:isArray(data['tagtemplates'].fields)}">
		<c:forEach items="${data['tagtemplates'].fields}" var="memo">
			<a onclick="insertunit('{${memo.key}}', 'jstemplate_${data.appid}')" href="javascript:;">{${memo.key}}</a> 代表 ${memo.value}<br />
		</c:forEach>
		</c:if>
		</th>
		<td><img src="image/zoomin.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('jstemplate_${data.appid}', 1)">
			<img src="image/zoomout.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('jstemplate_${data.appid}', 0)"><br />
			<textarea cols="100" rows="5" id="jstemplate_${data.appid}" name="relatedtag[data][${data.appid}][template]" style="width: 95%;">${template}</textarea>
			<input type="hidden" name="relatedtag[data][${data.appid}][type]" value="${data.type}">
		</td>
	</tr>
	</table>
	</div>
	<div class="footactions">
	<input type="submit" name="appsubmit" value="提交更新" class="submit">
	</div>

	<br>
	</c:forEach>

	</form>
</div>
</div>

<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>

<script type="text/javascript">
function insertunit(text, obj) {
	if(!obj) {
		obj = 'jstemplate';
	}
	$(obj).focus();
	if(!isUndefined($(obj).selectionStart)) {
		var opn = $(obj).selectionStart + 0;
		$(obj).value = $(obj).value.substr(0, $(obj).selectionStart) + text + $(obj).value.substr($(obj).selectionEnd);
	} else if(document.selection && document.selection.createRange) {
		var sel = document.selection.createRange();
		sel.text = text.replace(/\r?\n/g, '\r\n');
		sel.moveStart('character', -strlen(text));
	} else {
		$(obj).value += text;
	}
}
</script>
<jsp:directive.include file="footer.jsp" />