<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<style type="text/css">
.poster {
	max-width: 240px;
	max-height: 180px;
}
</style>
<div class="mainarea">
	<div class="maininner">
		<div class="tabs_header">
			<ul class="tabs">
				<li${actives_view}><a href="admincp.jsp?ac=eventclass"><span>浏览全部分类</span></a></li>
				<li class="null"><a href="admincp.jsp?ac=eventclass&op=add">添加新活动分类</a></li>
			</ul>
		</div>
		<form method="post" action="admincp.jsp?ac=eventclass" enctype="multipart/form-data">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
			<c:choose>
				<c:when test="${param.op == 'add' || param.op == 'edit'}">
					<div class="bdrcontent">
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<th style="width: 15em;">分类名称</th>
								<td><input type="text" name="classname" value="${thevalue.classname}"></td>
							</tr>
							<tr>
								<th>默认海报</th>
								<td>
									活动发起者发起此类型的活动时如果没有上传海报则默认使用此海报<br />
									<img id="posterview" class="poster" src="${thevalue.poster}?v=${sGlobal.timestamp}" alt="默认海报" onerror="this.src='image/event/default.jpg'" />
									<a href="#" onclick="$('posterview').src='image/event/default.jpg'; $('delposter').value='1'; return false;">删除</a>
									<br />
									<input type="hidden" id="delposter" name="delposter" value="0" />
									<input type="file" name="poster" value="">
								</td>
							</tr>
							<tr>
								<th>默认模板</th>
								<td>
									建议活动发起者发起此类型的活动时按此内容模板填写活动介绍<br />
									<textarea name="template" rows="8" cols="80">${thevalue.template}</textarea>
								</td>
							</tr>
							<tr>
								<th>显示顺序</th>
								<td><input type="text" name="displayorder" value="${thevalue.displayorder}" /></td>
							</tr>
						</table>
					</div>
					<div class="footactions">
						<input type="hidden" name="classid" value="${thevalue.classid}" />
						<input type="submit" name="eventclasssubmit" value="提交" class="submit">
					</div>
				</c:when>
				<c:when test="${param.op == 'delete'}">
					<div class="bdrcontent">
						<div class="topactions">该活动分类删除后，请选择该活动分类下面已有的活动会归类到那个新活动分类。</div>
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<th width="150">活动分类下的新归类</th>
								<td>
									<select name="newclassid">
										<c:forEach items="${list}" var="kv">
											<option value="${kv.value.classid}">${kv.value.classname}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
						</table>
					</div>
					<div class="footactions">
						<input type="hidden" name="classid" value="${thevalue.classid}" />
						<input type="submit" name="deletesubmit" value="确认" class="submit">
					</div>
				</c:when>
				<c:otherwise>
					<div class="bdrcontent">
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<th>名称</th>
								<th>设置海报</th>
								<th>设置模板</th>
								<th>显示顺序</th>
								<th width="80">操作</th>
							</tr>
							<c:forEach items="${list}" var="kv">
								<tr>
									<th>${kv.value.classname}</th>
									<td>${kv.value.poster == 'image/event/default.jpg' ? '否' : '是' }</td>
									<td>${jch:jchEmpty(kv.value.template) ? '否' : '是' }</td>
									<td><input type="text" name="displayorder[${kv.key}]" value="${kv.value.displayorder}" size="5"></td>
									<td width="80"><a href="admincp.jsp?ac=eventclass&op=edit&classid=${kv.key}">编辑</a> | <a onclick="return confirm('确认删除？');" href="admincp.jsp?ac=eventclass&op=delete&classid=${kv.key}">删除</a></td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div class="footactions"><input type="submit" name="ordersubmit" value="更新排序" class="submit"></div>
				</c:otherwise>
			</c:choose>
		</form>
	</div>
</div>
<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />