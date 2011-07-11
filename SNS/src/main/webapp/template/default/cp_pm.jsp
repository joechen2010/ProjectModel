<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${param.op=='delete'}">

<h1>删除短消息</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="$pmid">
<form id="pmform" name="pmform" method="post" action="cp.jsp?ac=pm&op=delete&folder=${folder}&pmid=${pmid}">
	<p>确定删除指定的短消息吗？</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}" />
		<input type="hidden" name="deletesubmit" value="true" />
		<input type="submit" name="deletesubmit" value="确定" class="submit" />
	</p>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" />
</form>
</div>

</c:when>
<c:otherwise>
<c:choose>
<c:when test="${jch:jchEmpty(sGlobal.inajax)}">
	<h2 class="title"><img src="image/icon/pm.gif">消息</h2>
	<div class="tabs_header">
		<ul class="tabs">
			<li class="active"><a href="cp.jsp?ac=pm"><span>发短消息</span></a></li>
			<li><a href="space.jsp?do=pm&view=inbox"><span>返回收件箱</span></a></li>
		</ul>
	</div>

</c:when>
<c:otherwise>

	<h1>发短消息</h1>
	<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>

</c:otherwise>
</c:choose>

<div class="popupmenu_inner" id="__pmform_${pmid}">
	<form id="pmform_${pmid}" name="pmform_${pmid}" method="post" action="cp.jsp?ac=pm&op=send&touid=${touid}&pmid=${pmid}" class="ajaxshowdiv">
	<table cellspacing="0" cellpadding="3">
	<c:if test="${touid==0}">
	<tr>
		<th><label for="username">收件人：</label></th>
		<td>
			<script type="text/javascript" src="source/script_autocomplete.js"></script>
			<input type="text" id="username" name="username" value="" style="width: 396px;" class="t_input" tabindex="1" <c:if test="${not empty friends}"> onclick="auc.handleEvent(this.value ,event);" onkeyup="auc.handleEvent(this.value ,event);" onkeydown="closeOpt(username,event);inputKeyDown(event);" autocomplete="off" </c:if> />
			<c:if test="${not empty friends}">
			<div id="username_menu" class="ajax_selector" onclick="$('username_menu').style.display='none';" style="display:none">
				<div class="ajax_selector_option" style="width: 396px; height: 100px;">
					<a href="javascript:;" onclick="$('username_menu').style.display='none';" class="float_del" style="margin-right: 5px;">a</a>
					<ul id="friendlist" class="blocklink">
						<c:forEach items="${friends}" var="value">
							<li>${value.username}</li>
						</c:forEach>
					</ul>
				</div>
			</div>
			<script type="text/javascript">
				var close = true;
				var auc = new sAutoComplete("auc", "username_menu", "friendlist", "username");
				auc.addItem('${friendstr}');
				function closeOpt(key,evt) {
					if(evt.keyCode==9) {
						$('username_menu').style.display='none';
					}
				}
				function inputKeyDown(event) {
					if(event.keyCode == 13){
						doane(event);
					}
				}
			</script>
			</c:if>
		</td>
	</tr>
	</c:if>
	<tr>
		<th style="vertical-align: top;"><label for="message">内容：</label></th>
		<td><textarea id="message" name="message" cols="40" rows="4" style="width: 400px; height: 150px;" onkeydown="ctrlEnter(event, 'pmsubmit_btn');"></textarea></td>
	</tr>
	<tr>
		<th>&nbsp;</th>
		<td>
		<input type="hidden" name="refer" value="${sGlobal.refer}" />
		<input type="hidden" name="pmsubmit" value="true" />
		<c:choose>
		<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
		<input type="button" name="pmsubmit_btn" id="pmsubmit_btn" value="发送" class="submit" onclick="ajaxpost('pmform_${pmid}','',2000)" />
		</c:when>
		<c:otherwise>
		<input type="submit" name="pmsubmit_btn" id="pmsubmit_btn" value="发送" class="submit" />
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" />
	</form>
</div>

</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>