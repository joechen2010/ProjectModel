<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title"><img src="image/icon/pm.gif">消息</h2>

<div class="tabs_header">
	<ul class="tabs">
		<li class="active"><a href="space.jsp?do=pm"><span>短消息</span></a></li>
		<li><a href="space.jsp?do=notice"><span>通知</span></a></li>
		<c:if test="${!jch:jchEmpty(sConfig.my_status)}">
		<li><a href="space.jsp?do=notice&view=userapp"><span>应用消息</span></a></li>
		</c:if>
		<li class="null"><a href="cp.jsp?ac=pm">发短消息</a></li>
	</ul>
</div>

<div class="h_status">
	<c:if test="${touid>0}">
	<div class="r_option">
		你们的历史记录：
		<a href="space.jsp?do=pm&subop=view&touid=${touid}&daterange=1"${actives['1']}>最近一天</a> | 
		<a href="space.jsp?do=pm&subop=view&touid=${touid}&daterange=2"${actives['2']}>最近两天</a> | 
		<a href="space.jsp?do=pm&subop=view&touid=${touid}&daterange=3"${actives['3']}>最近三天</a> | 
		<a href="space.jsp?do=pm&subop=view&touid=${touid}&daterange=4"${actives['4']}>本周</a> | 
		<a href="space.jsp?do=pm&subop=view&touid=${touid}&daterange=5"${actives['5']}>全部</a>
	</div>
	</c:if>
	<a href="space.jsp?do=pm&filter=newpm"${actives.newpm}>未读消息</a><span class="pipe">|</span>
	<a href="space.jsp?do=pm&filter=privatepm"${actives.privatepm}>私人消息</a><span class="pipe">|</span>
	<a href="space.jsp?do=pm&filter=systempm"${actives.systempm}>系统消息</a><span class="pipe">|</span>
	<a href="space.jsp?do=pm&filter=announcepm"${actives.announcepm}>公共消息</a><span class="pipe">|</span>
	<a href="space.jsp?do=pm&subop=ignore"${actives.ignore}>忽略列表</a>
</div>

<div class="c_form">

<c:choose>
<c:when test="${param.subop=='view'}">
<c:choose>
<c:when test="${not empty list}">
<ul class="pm_list" id="pm_ul">
	<c:forEach items="${list}" var="value">
	<li class="s_clear">
		<div class="avatar48">
			<c:choose>
			<c:when test="${!jch:jchEmpty(value.msgfromid)}">
			<a href="space.jsp?uid=${value.msgfromid}">${jch:avatar1(value.msgfromid,sGlobal, sConfig)}</a>
			</c:when>
			<c:otherwise>
			<img src="image/systempm.gif" width="48" height="48" />
			</c:otherwise>
			</c:choose>
		</div>
		<div class="pm_body"><div class="pm_h"><div class="pm_f">
			<p><c:if test="${!jch:jchEmpty(value.msgfromid)}"><a href="space.jsp?uid=${value.msgfromid}">${sNames[value.msgfromid]}</a></c:if><span class="gray"> <jch:date dateformat="yyyy-MM-dd HH:mm" timestamp="${value.dateline}" format="1"/></span></p>		
			<div class="pm_c">
				${value.message}
			</div>
		</div></div></div>
	</li>
	</c:forEach>
</ul>
</c:when>
<c:otherwise>
<div class="c_form">
	当前没有相应的短消息。
</div>
</c:otherwise>
</c:choose>

<c:if test="${touid>0 && not empty list}">
<ul class="pm_list" id="pm_ul_post">
	<li class="s_clear">
		<div class="avatar48">
			<a href="space.jsp?uid=${space.uid}">${jch:avatar1(space.uid,sGlobal, sConfig)}</a>
		</div>
		<div class="pm_body"><div class="pm_h"><div class="pm_f">
			<p><a href="space.jsp?uid=${space.uid}">${sNames[space.uid]}</a></p>		
			<div class="pm_c">
				<form id="pmform" name="pmform" method="post" action="cp.jsp?ac=pm&op=send&touid=${touid}&pmid=${pmid}&daterange=${daterange}">
				<textarea id="pm_message" name="message" cols="40" rows="4" style="width: 95%;" onkeydown="ctrlEnter(event, 'pmsubmit');"></textarea><br>
				<p style="padding-top:5px;">
					<input type="submit" name="pmsubmit" id="pmsubmit" value="回复" class="submit" />
				</p>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
				</form>
			</div>
		</div></div></div>
	</li>
</ul>
</c:if>
</c:when>
<c:when test="${param.subop=='ignore'}">

<form id="ignoreform" name="ignoreform" method="post" action="cp.jsp?ac=pm&op=ignore">
	<table cellspacing="0" cellpadding="0" class="formtable" width="100%">
		<caption>
			<h2>忽略列表</h2>
			<p>添加到该列表中的用户给您发送短消息时将不予接收<br />
				添加多个忽略人员名单时用逗号 "," 隔开，如“张三,李四,王五”<br />
				如需禁止所有用户发来的短消息，请设置为 "&#123;ALL&#125;"</p>
		</caption>
		<tr>
			<td><textarea id="ignorelist" name="ignorelist" cols="40" rows="6" style="width: 98%;" onkeydown="ctrlEnter(event, 'ignoresubmit');">${ignorelist.blacklist}</textarea></td>
		</tr>
		<tr>
			<td><input type="submit" name="ignoresubmit" id="ignoresubmit" value="保存" class="submit" /></td>
		</tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${count>0}">
	<ol class="pm_list">
	<c:forEach items="${list}" var="value" varStatus="key">
		<li<c:if test="${key.index%2==1}"> class="alt"</c:if>>
		<div class="avatar48">
			<c:choose>
			<c:when test="${!jch:jchEmpty(value.touid)}">
			<a href="space.jsp?uid=${value.touid}">${jch:avatar1(value.touid,sGlobal, sConfig)}</a>
			</c:when>
			<c:otherwise>
			<img src="image/systempm.gif" width="48" height="48" />
			</c:otherwise>
			</c:choose>
		</div>
		<div class="pm_body"><div class="pm_h"><div class="pm_f">
			<p>
			<c:if test="${!jch:jchEmpty(value.touid)}">
			<a href="space.jsp?uid=${value.touid}">${sNames[value.touid]}</a> 
			</c:if>
			<span class="gray"> <jch:date dateformat="M-d HH:mm" timestamp="${value.dateline}" format="1"/></span></p>		
			<div class="pm_c">
				${value.message}
				<p>
				<c:choose>
				<c:when test="${!jch:jchEmpty(value.touid)}">
				<a href="space.jsp?do=pm&subop=view&pmid=${value.pmid}&touid=${value.touid}&daterange=${value.daterange}">查看详情</a>
				</c:when>
				<c:otherwise>
				<a href="space.jsp?do=pm&subop=view&pmid=${value.pmid}">查看详情</a>
				</c:otherwise>
				</c:choose>
				</p>
			</div>
			<a href="cp.jsp?ac=pm&op=delete&folder=inbox&pmid=${value.pmid}" id="a_delete_${value.pmid}" class="float_del" onclick="ajaxmenu(event, this.id)">删除</a>
		</div></div></div>
		</li>
	</c:forEach>
	</ol>
	
	<div class="page">${multi}</div>
</c:when>
<c:otherwise>
<div class="c_form">
	当前没有相应的短消息。
</div>
</c:otherwise>
</c:choose>

</c:otherwise>
</c:choose>

</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>