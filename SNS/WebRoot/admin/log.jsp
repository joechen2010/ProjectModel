<%@ page language="java"  pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>

<div class="mainarea">
	<div class="maininner">
<c:if test="${param.op=='view'}">
		<div class="bdrcontent">	
			<table cellspacing="0" cellpadding="0" class="formtable" border="0">
			<tr>				
				<th width="80">时间</th>
				<td>${log.dateline }</td>
			</tr>
				<th>IP</th>
				<td>${log.ip }</td>
			</tr>
				<th>用户</th>
				<td><a href="space.jsp?uid=${log.uid }" target="_blank">${sNames[log.uid] }</a></td>
			</tr>
				<th>链接</th>
				<td>${log.link }</td>
			</tr>
			<c:if test="${!jch:jchEmpty(log.get)}">
			</tr>
				<th>GET数据</th>
				<td>${log.get }</td>
			</tr>
			</c:if>
			<c:if test="${!jch:jchEmpty(log.post)}">
			</tr>
				<th>POST数据</th>
				<td>${log.post }</td>
			</tr>
			</c:if>
			<c:if test="${!jch:jchEmpty(log.extra)}">
			<tr>
				<th>参考信息</th>
				<td>${log.extra }</td>
			</tr>
			</c:if>
			</table>
		</div>
		<div class="footactions">
			<button onclick="javascript:history.go(-1);" class="submit">返回</button>
		</div>
</c:if>
<c:if test="${param.op!='view'}">	
		<form method="post" action="admincp.jsp">
			<input type="hidden" name="ac" value="log">
			<div class="block style4">				
				<table cellspacing="3" cellpadding="3">				
				<tr>
					<th>选择log文件</th>
					<td colspan="3">
					<select name="file">
						<option value="">选择文件</option>	
						<c:forEach items="${logfiles}" var="value">
						<option value="${value }"<c:if test="${param.file==value}"> selected=""</c:if>>${value }</option>
						</c:forEach>			
					</select>
					</td>
				</tr>
				<tr>
				<tr>
					<td>用户UID</td>
					<td>
						<input type="text" name="uid" value="${param.uid}" />
					</td>
					<th>IP地址</th>
					<td>
						<input type="text" name="ip" value="${param.ip }" />
					</td>
				</tr>
				<th>记录时间</th>
					<td colspan="3">
						<script type="text/javascript" src="source/script_calendar.js" charset="${jchConfig.charset }"></script>
						<input type="text" name="starttime" value="${param.starttime }" onclick="showcalendar(event,this,1)"/> ~
						<input type="text" name="endtime" value="${param.endtime }"  onclick="showcalendar(event,this,1)" />						
					</td>
				</tr>
				<tr>
					<th>关键词</th>
					<td colspan="3">
						<input type="text" name="keysearch" value="${param.keysearch }" />
						<input type="submit" name="searchsubmit" value="搜索" class="submit">
					</td>
				</tr>
				</table>
			</div>
		</form>
	<c:if test="${not empty list}">	
		<div class="bdrcontent">		
			<table cellspacing="0" cellpadding="0" class="formtable" border="0">
			<tr>				
				<th width="160">时间</th>
				<th width="120">IP</th>
				<th width="120">用户</th>
				<th>链接</th>
				<th width="60">操作</th>
			</tr>			
			<c:forEach items="${list}" var="value">
			<tr>				
				<td>${value.dateline }</td>
				<td><a href="admincp.jsp?ac=log&file=${param.file }&uid=${param.uid }&ip=${value.ip }&starttime=${param.starttime }&endtime=${param.endtime }&keysearch=${jch:urlEncoder(param.keysearch) }">${value.ip }</a></td>
				<td>
					<a href="admincp.jsp?ac=log&file=${param.file }&uid=${value.uid }&ip=${param.ip }&starttime=${param.starttime }&endtime=${param.endtime }&keysearch=${jch:urlEncoder(param.keysearch) }">${sNames[value.uid] }</a>
					<p class="gray"><a href="space.jsp?uid=${value.uid }" target="_blank">空间</a></p>
				</td>
				<td>${value.link }</td>
				<td><a href="admincp.jsp?ac=log&op=view&file=${param.file }&line=${value.line }">详细</a></td>
			</tr>
			</c:forEach>
			</table>			
		</div>
		<div class="footactions">
			<div class="pages">${multi }</div>
		</div>
	</c:if>
	<c:if test="${empty list}">	
		<div class="bdrcontent"><p>没有相关记录</p></div>
	</c:if>
</c:if>
	</div>
</div>

<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>

<jsp:directive.include file="footer.jsp" />