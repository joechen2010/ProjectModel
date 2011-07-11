<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<h2 class="title"><img src="image/icon/task.gif">有奖任务</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li${actives.task }><a href="cp.jsp?ac=task"><span>待参与任务</span></a></li>
		<li${actives.done }><a href="cp.jsp?ac=task&view=done"><span>已参与任务</span></a></li>
		<c:if test="${task != null}">
		<li${actives.do }><a href="cp.jsp?ac=task&taskid=${task.taskid }"><span>查看任务</span></a></li>
		</c:if>
	</ul>
</div>

<c:choose>
<c:when test="${!empty task}">

	<c:choose>
	
	<c:when test="${view == 'member'}">
	<div class="h_status">
		完成该任务的用户列表
	</div>
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_list.jsp')}"/>
	</c:when>
	
	<c:otherwise>
	<div id="content">
		<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
			<table cellspacing="0" cellpadding="0" width="100%" class="task_notice">
				<tr>
					<td width="70"><img src="${task.image }" width="64" height="64" alt="Icon" /></td>
					<td>
						<h3>有奖任务：${task.name }</h3>
						<c:if test="${task.starttime != null && task.starttime != 0 }"><p>开始时间：${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', task.starttime,false)}</p></c:if>
						<c:if test="${task.endtime != null && task.endtime != 0}"><p>结束时间：${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', task.endtime,false)}</p></c:if>
						<p>${task.note }</p>
						<c:if test="${task.credit != null && task.credit != 0}"><p>奖励积分：<strong class="num">${task.credit }</strong></p></c:if>
					</td>
				</tr>
			</table>
		</div></div></div></div>
					
		<div style="padding-top:20px;">
		<c:choose>
		<c:when test="${task.done != null && task.done !=0 }">
			<c:choose>
			<c:when test="${task.ignore != null && task.ignore != 0}">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<caption>
						<h2>任务被放弃</h2>
						<p>您已经放弃参与该任务，没有获得任何奖励。</p>
					</caption>
					<tr>
						<td><a href="cp.jsp?ac=task&taskid=${task.taskid }&op=redo" class="submit">重新参与</a></td>
					</tr>
				</table>
			</c:when>
			
			<c:when test="${sGlobal.task_maxnum != null && sGlobal.task_maxnum != 0}">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<caption>
						<h2>参与名额已满</h2>
						<p>该有奖任务目前已经达到名额上限了。</p>
					</caption>
					<tr>
						<td><a href="cp.jsp?ac=task" class="submit">看看其他任务</a></td>
					</tr>
				</table>
			</c:when>
			
			<c:when test="${sGlobal.task_available != null && sGlobal.task_available != 0}">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<caption>
						<h2>任务失效</h2>
						<p>该有奖任务目前已经停止了。</p>
					</caption>
					<tr>
						<td><a href="cp.jsp?ac=task" class="submit">看看其他任务</a></td>
					</tr>
				</table>
			</c:when>
			
			<c:otherwise>
				<table cellspacing="0" cellpadding="0" class="formtable">
					<caption>
						<h2>成功完成任务</h2>
						<p>恭喜，您已经领取到任务奖励！</p>
					</caption>
					<tr>
						<td>
						<c:if test="${task.credit != null && task.credit != 0}"><p style="color:red;font-size:14px;padding:0 0 5px 0;font-weight:bold;">奖励积分 ${task.credit } 个，您现在已经有 ${space.credit } 个积分啦！</p></c:if>
						<p>完成时间：${jch:sgmdate(pageContext.request, 'MM-dd HH:mm', task.dateline,true)}</p>
						<p>参与人次：<a href="cp.jsp?ac=task&taskid=${task.taskid }&view=member">${task.num } 人</a></p>
						</td>
					</tr>
				</table>
				<c:if test="${task.result != null && task.result != ''}">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<caption>
						<h2>任务额外奖励</h2>
					</caption>
					<tr>
						<td>${task.result }</td>
					</tr>
				</table>
				</c:if>
			</c:otherwise>
			</c:choose>
		</c:when>
		
		<c:otherwise>
				<table cellspacing="0" cellpadding="0" class="formtable">
					<caption>
						<h2>参与任务的步骤说明</h2>
						<p>请您仔细阅读下面的参与本任务的步骤说明，按照指示来完成操作并领取任务奖励。</p>
					</caption>
					<tr>
						<td class="article l_status">${task.guide }</td>
					</tr>
				</table><br>
				
				<c:if test="${view == 'result'}">
				<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
					<div class="task_notice">提示：请先按照上面的步骤说明完成任务后再点击领取奖励链接。</div>
				</div></div></div></div>
				</c:if>
					
				<div style="text-align:center;padding:10px;">
					<a href="cp.jsp?ac=task&taskid=${task.taskid }&view=result" class="submit">领取奖励</a>
					<a href="cp.jsp?ac=task&taskid=${task.taskid }&op=ignore" class="button">暂时放弃</a>
				</div>
		</c:otherwise>
		</c:choose>
		</div>
		
	</div>
	
	<div id="sidebar">
		<div class="sidebox">
			<h2 class="title">
				<a href="cp.jsp?ac=task&taskid=${taskid }&view=member" class="r_option">查看全部</a>
				刚刚完成该任务的朋友
			</h2>
			<ul class="avatar_list">
				<c:forEach items="${taskspacelist}" var="listEle_map">
				<li>
					<div class="avatar48"><a href="space.jsp?uid=${listEle_map.uid }">${jch:avatar2(listEle_map.uid,'small',false,sGlobal,sConfig) }</a></div>
					<p><a href="space.jsp?uid=${listEle_map.uid }" title="${sNames[listEle_map.uid] }">${sNames[listEle_map.uid] }</a></p>
					<p class="gray">${jch:sgmdate(pageContext.request, 'M月d日', listEle_map.dateline,true)}</p>
				</li>
				</c:forEach>
			</ul>
		</div>
	</div>
	
	</c:otherwise>
	</c:choose>
</c:when>

<c:otherwise>
<div id="content">

	<div class="h_status">
		按任务优先级排序，参与任务有大奖，开始吧。
	</div>


	<c:if test="${view != 'done'}">
	<div class="task_percent">
		<div class="percent" style="width: ${done_per}%;">&nbsp;</div>
		<div class="label">我当前的任务完成度 ${done_per}%</div>
	</div>
	</c:if>
		
	<c:choose>
	<c:when test="${jch:jchEmpty(tasklist)}">
		<div class="c_form">已经没有新的任务啦。</div>
	</c:when>
	
	<c:otherwise>
		<c:forEach items="${tasklist}" var="value">
		<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
			<table cellspacing="0" cellpadding="0" width="100%" class="task_notice">
				<tr>
					<td width="70"><img src="${value.image }" width="64" height="64" alt="Icon" class="icon" /></td>
					<td>
						<h3><a href="cp.jsp?ac=task&taskid=${value.taskid }">${value.name }</a></h3>
						<c:if test="${value.num != null && value.num != 0}">
							<p>
								<a href="cp.jsp?ac=task&taskid=${value.taskid }&view=member">已参与人次： ${value.num }</a>
								<c:if test="${value.maxnum != null && value.maxnum != 0}">
								/ 本任务还可参与 ${value.maxnum - value.num } 人次
								</c:if>
							</p>
						</c:if>
						<c:if test="${value.starttime != null && value.starttime != 0}"><p>开始时间：${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', value.starttime,false)}</p></c:if>
						<c:if test="${value.endtime != null && value.endtime != 0}"><p>结束时间：${jch:sgmdate(pageContext.request, 'yyyy-MM-dd HH:mm', value.endtime,false)}</p></c:if>
						${value.note }
					</td>
					<td width="150" style="text-align:right;">
						<c:choose>
						<c:when test="${value.done != null && value.done != 0}">
							<c:choose>
							<c:when test="${value.ignore != null && value.ignore != 0}">
								已经放弃该任务<br>
								您可以选择<a href="cp.jsp?ac=task&taskid=${value.taskid }&op=redo">重新完成该任务</a>
							</c:when>
							
							<c:otherwise>
								<c:if test="${value.credit != null && value.credit != 0}"><p>获得积分：<strong class="num">${value.credit }</strong></p></c:if>
								<a href="cp.jsp?ac=task&taskid=${value.taskid }">查看任务</a>
							</c:otherwise>
							</c:choose>
						</c:when>
						
						<c:otherwise>
							<c:if test="${value.credit != null && value.credit != 0}"><p>可获得积分：<strong class="num">${value.credit }</strong></p></c:if>
							<a href="cp.jsp?ac=task&op=do&taskid=${value.taskid }"><img src="image/start_task.gif" alt="立即参与任务" /></a>
						</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</div></div></div></div><br>
		</c:forEach>
	</c:otherwise>
	</c:choose>

</div>

<div id="sidebar">

	<div class="sidebox">
		<h2 class="title">
			刚刚完成任务的朋友
		</h2>
		<ul class="avatar_list">
			<c:forEach items="${taskspacelist}" var="listEle_k_v">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${listEle_k_v.value.uid}">${jch:avatar2(listEle_k_v.value.uid,'small',false,sGlobal,sConfig) }</a></div>
				<p><a href="space.jsp?uid=${listEle_k_v.value.uid}" title="${sNames[listEle_k_v.value.uid] }">${sNames[listEle_k_v.value.uid] }</a></p>
				<p class="gray">${jch:sgmdate(pageContext.request, 'M月d日', listEle_k_v.value.dateline,true)}</p>
			</li>
			</c:forEach>
		</ul>
	</div>
	
</div>

</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>