<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:choose>
<c:when test="${op=='send' || op=='reply'}">
<c:choose>
<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
	<h1>打招呼</h1>
	<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>

</c:when>
<c:otherwise>

	<h2 class="title"><img src="image/icon/poke.gif">打招呼</h2>
	<div class="tabs_header">
		<ul class="tabs">
			<li><a href="cp.jsp?ac=poke"><span>收到的招呼</span></a></li>
			<li class="null"><a href="cp.jsp?ac=poke&op=send">打招呼</a></li>
		</ul>
	</div>
</c:otherwise>
</c:choose>

<div class="popupmenu_inner" id="__pokeform_${tospace.uid}">
<form method="post" id="pokeform_${tospace.uid}" name="pokeform_${tospace.uid}" action="cp.jsp?ac=poke&op=${op}&uid=${tospace.uid}">
	<table cellspacing="0" cellpadding="3">
		<tr>
		<c:choose>
		<c:when test="${!jch:jchEmpty(tospace.uid)}">
			<th width="52"><div class="avatar48"><a href="space.jsp?uid=${tospace.uid}">${jch:avatar1(tospace.uid,sGlobal, sConfig)}</div></th>
		</c:when>
		<c:otherwise>
			<th></th><td class="l_status">用户名: <input type="text" name="username" value=""></td></tr>
			<tr><th></th>
		</c:otherwise>
		</c:choose>
			<td>
				<c:if test="${!jch:jchEmpty(tospace.uid)}">
					向 <strong>${sNames[tospace.uid]}</strong> 打招呼：<br />
				</c:if>
				<ul class="list2col" style="width:300px;">
					<c:forEach items="${icons}" var="icon">
					<li><input type="radio" name="iconid" id="poke_${icon.key}" value="${icon.key}" /><label for="poke_${icon.key}">${icon.value}</label></li>
					</c:forEach>
				</ul>
				<input type="text" name="note" id="note" value="" size="16" class="t_input" onkeydown="ctrlEnter(event, 'pokesubmit_btn', 1);" style="width: 300px;" maxlength="25" />
				<div class="gray">(内容为可选，并且会覆盖之前的招呼，最多25个汉字)</div>
			</td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>
				<input type="hidden" name="refer" value="${sGlobal.refer}">
				<input type="hidden" name="pokesubmit" value="true" />
				<c:choose>
				<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
				<input type="button" name="pokesubmit_btn" id="pokesubmit_btn" value="确定" class="submit" onclick="ajaxpost('pokeform_${tospace.uid}', 'poke_send', 2000)" />
				</c:when>
				<c:otherwise>
				<input type="submit" name="pokesubmit_btn" id="pokesubmit_btn" value="确定" class="submit" />
				</c:otherwise>
				</c:choose>
			</td>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" />
</form>
</div>
</c:when>
<c:otherwise>

<h2 class="title"><img src="image/icon/poke.gif">打招呼</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li class="active"><a href="cp.jsp?ac=poke"><span>收到的招呼</span></a></li>
		<li class="null"><a href="cp.jsp?ac=poke&op=send">打招呼</a></li>
	</ul>
</div>

<div class="h_status">您可以回复招呼或者进行忽略<span class="pipe">|</span><a href="cp.jsp?ac=poke&op=ignore" id="a_poke" onclick="ajaxmenu(event, this.id, 0, 2000, 'mypoke_all')">全部忽略</a></div>

<!--{if $list}-->
<c:choose>
<c:when test="${not empty list}">
<style>
.list_td td { border-bottom: 1px solid #EBE6C9; padding: 0.5em; }
.list_td img { vertical-align: middle; }
</style>
<div class="c_form" id="poke_ul">
	<table cellpadding="0" cellspacing="0" width="100%" class="list_td">
		<c:forEach items="${list}" var="value" varStatus="key">
		<tbody id="poke_${value.uid}">
		<tr>
			<td width="70">
				<div class="avatar48">
				<a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal, sConfig)}</a>
				</div>
			</td>
			<td>
				<p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a> <span class="gray"><jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1"/></span></p>
				
				<div style="padding:10px 0 10px 0;font-size:14px;">
				${!jch:jchEmpty(value.iconid) ? icons[value.iconid] : '打个招呼' }
				<c:if test="${!jch:jchEmpty(value.note)}">，并对您说：${value.note}</c:if>
				</div>
				<div style="padding:10px 0 10px 0;">
				<a href="cp.jsp?ac=poke&op=reply&uid=${value.uid}" id="a_p_r_${value.uid}" onclick="ajaxmenu(event, this.id, 1)" class="submit">回打招呼</a> 
				<c:if test="${!value.isfriend}"><a href="cp.jsp?ac=friend&op=add&uid=${value.uid}" id="a_friend_${key.index}" onclick="ajaxmenu(event, this.id, 1)" class="submit">加为好友</a></c:if>
				</div>

			</td>
			<td align="right" width="80">
			<a href="cp.jsp?ac=poke&op=ignore&uid=${value.uid}" id="a_p_i_${value.uid}" onclick="ajaxmenu(event, this.id, 0, 2000, 'mypoke')" class="button">忽略</a>
			</td>
		</tr>
		</tbody>
		</c:forEach>
	</table>
	<div class="page">${multi}</div>
</div>

</c:when>
<c:otherwise>
<div class="c_form">
	还没有新招呼。
</div>
</c:otherwise>
</c:choose>

<script>
	function mypoke(id) {
		var liid = id.substr(6);
		$('poke_'+liid).style.display = "none";
	}
	function mypoke_all(id) {
		$('poke_ul').innerHTML = "忽略了全部的招呼";
	}
</script>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>