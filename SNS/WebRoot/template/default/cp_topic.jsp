<%@ page language="java"  pageEncoding="UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${op == 'delete'}">
	<h1>删除热闹</h1>
	<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
	<div class="popupmenu_inner" id="__topicform_${topicid}">
	<form id="topicform_${topicid}" name="topicform_${topicid}" method="post" action="cp.jsp?ac=topic&op=delete&topicid=${topicid }">
		<p>确定删除指定的热闹吗？</p>
		<p class="btn_line">
			<input type="hidden" name="refer" value="${sGlobal.refer }" />
			<input type="submit" name="deletesubmit" value="确定" class="submit" />
		</p>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
	</div>
</c:when>

<c:when test="${op == 'ignore'}">
	<h1>剔除信息</h1>
	<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
	<div class="popupmenu_inner" id="__topicform_${topicid}">
	<form id="topicform_${topicid}" name="topicform_${topicid}" method="post" action="cp.jsp?ac=topic&op=join&topicid=${topicid }&id=${id }&idtype=${idtype }">
		<p>
			确定要将其从此热闹中剔除吗？<br>
			这并不会删除该数据。
		</p>
		<p class="btn_line">
			<input type="hidden" name="refer" value="${sGlobal.refer }" />
			<input type="hidden" name="newtopicid" value="0">
			<input type="submit" name="joinsubmit" value="确定" class="submit" />
		</p>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
	</div>
</c:when>

<c:when test="${op == 'join'}">
	<h1>凑个热闹</h1>
	<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
	<div class="popupmenu_inner" id="__topicjoinform">
	<form id="topicjoinform" name="topicjoinform" method="post" action="cp.jsp?ac=topic&op=join&id=${id }&idtype=${idtype }">
		<p>
			选择个热闹：<br>
			<select name="newtopicid">
			<option value="">&nbsp;</option>
			<c:forEach items="${tlist}" var="value">
			<option value="${value.value.topicid }">${value.value.subject }</option>
			</c:forEach>
			</select>
		</p>
		<p class="btn_line">
			<input type="hidden" name="refer" value="${sGlobal.refer }" />
			<input type="submit" name="joinsubmit" value="确定" class="submit" />
		</p>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
	</div>
</c:when>

<c:otherwise>
	<h2 class="title"><img src="image/app/topic.gif" />热闹</h2>
	<div class="tabs_header">
		<ul class="tabs">
			<li class="active"><a href="cp.jsp?ac=topic"><span>添加新热闹</span></a></li>
			<li><a href="space.jsp?do=topic"><span>返回热闹列表</span></a></li>
		</ul>
		<c:if test="${!empty sGlobal.refer}">
		<div class="r_option">
			<a  href="${sGlobal.refer }">&laquo; 返回上一页</a>
		</div>
		</c:if>
	</div>
	
	<div class="c_form">
		<form method="post" action="cp.jsp?ac=topic&topicid=${topicid}" enctype="multipart/form-data">
		<table cellspacing="4" cellpadding="4" width="100%" class="infotable">
			<tr>
				<th width="100">热闹标题</th>
				<td>
					<input type="text" name="subject" value="${topic.subject }" size="80" />
				</td>
			</tr>
			<tr>
				<th>热闹图片</th>
				<td>
					<table><tr>
					<c:if test="${!empty topic.pic}">
					<td><img src="${topic.pic }" width="150"></td>
					</c:if>
					<td><input type="file" name="pic" value="" /></td>
					</tr></table>
				</td>
			</tr>
			<tr>
				<th>热闹说明</th>
				<td>
					<textarea name="message" rows="8" cols="80">${topic.message }</textarea>
				</td>
			</tr>
			<tr>
				<th>允许参与方式</th>
				<td>
					<input type="checkbox" name="jointype[]" value="blog"${jointypes.blog }>日志
					<input type="checkbox" name="jointype[]" value="pic"${jointypes.pic }>图片
					<input type="checkbox" name="jointype[]" value="thread"${jointypes.thread }>话题
					<input type="checkbox" name="jointype[]" value="poll"${jointypes.poll }>投票
					<input type="checkbox" name="jointype[]" value="event"${jointypes.event }>活动
					<input type="checkbox" name="jointype[]" value="share"${jointypes.share }>分享
				</td>
			</tr>
			
			<tr>
				<th>允许参与的用户组</th>
				<td>
				<table><tr>
				<c:set var="tempKey" value="3"></c:set>
				<c:set scope="page" var="status" value="0" />
				<c:forEach items="${usergroups}" var="groups" >
				<c:forEach items="${groups.value}" var="gid_value">
					<td><input type="checkbox" name="joingid[]" value="${gid_value.key }"${joingids[gid_value.key] }>${gid_value.value.grouptitle }</td>
					<c:if test="${status % 4 == 3}"></tr><tr></c:if>
					<c:set scope="page" var="status" value="${status+1}" />
				</c:forEach>
				</c:forEach>
				</tr></table>
				</td>
			</tr>
			
			<tr>
				<th>允许参与结束日期</th>
				<td>
					<script type="text/javascript" src="source/script_calendar.js" charset="${jchConfig.charset}"></script>
					<input type="text" name="endtime" value="${topic.endtime }" id="endtime" onclick="showcalendar(event,this,1,'${topic.endtime }', '${topic.endtime }')">
					<br>为空则不限制。到期后，该热闹将只能浏览，不再允许参与。
				</td>
			</tr>


			<tr>
				<th>&nbsp;</th>
				<td><input type="submit" name="topicsubmit" value="提交" class="submit" /></td>
			</tr>
		</table>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>