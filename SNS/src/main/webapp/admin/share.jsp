<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
	<div class="maininner">
		<form method="post" action="admincp.jsp">
			<div class="block style4">
				<table cellspacing="3" cellpadding="3">
					<c:if test="${allowmanage}">
						<tr>
							<th>作者UID</th><td><input type="text" name="uid" value="${param.uid}"></td>
							<th>作者名</th><td><input type="text" name="username" value="${param.username}"></td>
						</tr>
					</c:if>
					<tr>
						<th>指定分享ID</th><td colspan="3"><input type="text" name="sid" value="${param.sid}" /></td>
					</tr>
					<tr>
						<th>事件类型</th><td colspan="3"><input type="text" name="type" value="${param.type}"></td>
					</tr>
					<tr>
						<th>发布时间</th>
						<td colspan="3">
							<input type="text" name="dateline1" value="${param.dateline1}" size="10"> ~
							<input type="text" name="dateline2" value="${param.dateline2}" size="10"> (YYYY-MM-DD)
						</td>
					</tr>
					<tr>
						<th>热度</th>
						<td colspan="3">
							<input type="text" name="hot1" value="${param.hot1}" size="10"> ~
							<input type="text" name="hot2" value="${param.hot2}" size="10">
						</td>
					</tr>
					<tr>
						<th>结果排序</th>
						<td colspan="3">
							<select name="orderby">
								<option value="">默认排序</option>
								<option value="dateline"${orderby_dateline}>发布时间</option>
								<option value="hot"${orderby_hot}>热度</option>
							</select>
							<select name="ordersc">
								<option value="desc"${ordersc_desc}>递减</option>
								<option value="asc"${ordersc_asc}>递增</option>
							</select>
							<select name="perpage">
								<option value="20"${perpage_20}>每页显示20个</option>
								<option value="50"${perpage_50}>每页显示50个</option>
								<option value="100"${perpage_100}>每页显示100个</option>
								<option value="1000"${perpage_1000}>一次处理1000个</option>
							</select>
							<input type="hidden" name="ac" value="share">
							<input type="submit" name="searchsubmit" value="搜索" class="submit">
						</td>
					</tr>
				</table>
			</div>
		</form>
		<c:choose><c:when test="${not empty list}">
			<form method="post" action="admincp.jsp?ac=share">
				<input type="hidden" name="formhash" value="${FORMHASH}" />
				<div class="bdrcontent">
					<c:choose><c:when test="${perpage>100}">
						<p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
						<c:forEach items="${list}" var="value">
							<input type="hidden" name="ids" value="${value.sid}">
						</c:forEach>
					</c:when><c:otherwise>
						<table cellspacing="0" cellpadding="0" class="formtable">
							<c:forEach items="${list}" var="value">
								<tr>
									<td width="25"><input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.sid}"></td>
									<td>
										<p><a href="admincp.jsp?ac=share&uid=${value.uid}">${value.username}</a> ${value.title_template} &nbsp;${value.dateline} <c:if test="${value.hot>0}"><span style="color: red;">&nbsp;热度(${value.hot})</span></c:if></p>
										${value.body_template}<br><a href="admincp.jsp?ac=comment&id=${value.sid }&idtype=sid">管理评论</a>
									</td>
								</tr>
							</c:forEach>
						</table>
					</c:otherwise></c:choose>
				</div>
				<div class="footactions">
					<c:if test="${allowbatch && perpage<=100}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
					<input type="hidden" name="mpurl" value="${mpurl}">
					<input type="submit" name="batchsubmit" value="批量删除" onclick="return confirm('本操作不可恢复，确认删除？');" class="submit">
					<div class="pages">${multi}</div>
				</div>
			</form>
		</c:when><c:otherwise>
			<div class="bdrcontent"><p>指定条件下还没有数据</p></div>
		</c:otherwise></c:choose>
	</div>
</div>
<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />