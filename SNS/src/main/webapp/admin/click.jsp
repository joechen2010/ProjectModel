<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<div class="mainarea">
	<div class="maininner">
		<div class="tabs_header">
			<ul class="tabs">
				<li${actives_view}><a href="admincp.jsp?ac=click"><span>浏览全部动作</span></a></li>
				<li${actives_blogid}><a href="admincp.jsp?ac=click&idtype=blogid"><span>日志动作</span></a></li>
				<li${actives_picid}><a href="admincp.jsp?ac=click&idtype=picid"><span>图片动作</span></a></li>
				<li${actives_tid}><a href="admincp.jsp?ac=click&idtype=tid"><span>话题动作</span></a></li>
				<li class="null"><a href="admincp.jsp?ac=click&op=add">添加新动作</a></li>
			</ul>
		</div>
		<c:choose>
			<c:when test="${jch:jchEmpty(op)}">
				<form method="post" action="admincp.jsp?ac=click">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
					<div class="bdrcontent">
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<th>图标</th>
								<th>动作名称</th>
								<th>系统类型</th>
								<th>显示顺序</th>
								<th>&nbsp;</th>
							</tr>
							<c:forEach items="${clicks}" var="value">
								<tr>
									<td><img src="image/click/${value.icon}"></td>
									<td>${value.name}</td>
									<td>${idtypeName[value.idtype]}</td>
									<td><input type="hidden" name="clickIds" value="${value.clickid}"><input type="text" name="displayorder${value.clickid}" value="${value.displayorder}" size="5"></td>
									<td><a href="admincp.jsp?ac=click&op=edit&clickid=${value.clickid}">编辑</a> | <a href="admincp.jsp?ac=click&op=delete&clickid=${value.clickid }" onclick="return confirm('删除不可恢复\n并会同时清除相关统计数据\n确认删除？');">删除</a></td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div class="footactions"><input type="submit" name="ordersubmit" value="提交" class="submit"></div>
				</form>
			</c:when>
			<c:when test="${op=='add' || op=='edit'}">
				<form method="post" action="admincp.jsp?ac=click">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
					<div class="bdrcontent">
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<th style="width: 12em;">动作名称</th>
								<td><input type="text" name="name" value="${click.name}"></td>
							</tr>
							<tr>
								<th>动作图标</th>
								<td>./image/click/<input type="text" name="icon" value="${click.icon}" size="15"><br>需要确保将该图片上传到程序的 ./image/click/ 目录下面。</td>
							</tr>
							<c:if test="${op=='add'}">
								<tr>
									<th>系统类型</th>
									<td>
										<select name="idtype">
											<option value="blogid">日志</option>
											<option value="picid"}>图片</option>
											<option value="tid">话题</option>
										</select>
									</td>
								</tr>
							</c:if>
							<tr>
								<th>显示顺序</th>
								<td><input type="text" name="displayorder" value="${click.displayorder}"></td>
							</tr>
						</table>
					</div>
					<div class="footactions">
						<input type="hidden" name="clickid" value="${click.clickid}">
						<input type="submit" name="clicksubmit" value="提交" class="submit">
					</div>
				</form>
			</c:when>
		</c:choose>
	</div>
</div>
<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />