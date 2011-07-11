<%@ page language="java"  pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />

<div class="mainarea">
<div class="maininner">

<div class="tabs_header">
	<ul class="tabs">
		<li${actives.view}><a href="admincp.jsp?ac=profilefield"><span>浏览全部栏目</span></a></li>
		<li class="null"><a href="admincp.jsp?ac=profilefield&op=add">添加新用户栏目</a></li>
	</ul>
</div>

<c:choose>
<c:when test="${thevalue == null }">
	<form method="post" action="admincp.jsp?ac=profilefield">
	<input type="hidden" name="formhash" value="${FORMHASH}" />
	<div class="bdrcontent">

		<table cellspacing="0" cellpadding="0" class="formtable">
		<tr>
			<th>用户栏目</th>
			<th>栏目字段名</th>
			<th>填写类型</th>
			<th>显示顺序</th>
			<th width="80">操作</th>
		</tr>
		<c:forEach items="${list}" var="value">
		<tr>
			<th>${value.title}</th>
			<th>field_${value.fieldid}</th>
			<td>${_TPL.formtypes[value.formtype]}</td>
			<td><input type="text" name="displayorder[${value.fieldid}]" value="${value.displayorder}" size="5"></td>
			<td width="80">
			<a href="admincp.jsp?ac=profilefield&op=edit&fieldid=${value.fieldid}">编辑</a> | 
			<a href="admincp.jsp?ac=profilefield&op=delete&fieldid=${value.fieldid}" onclick="return confirm('确认删除？');">删除</a></td>
		</tr>
		</c:forEach>
		</table>
	</div>
	
	<div class="footactions">
		<input type="submit" name="ordersubmit" value="更新排序" class="submit">
	</div>
	
	</form>
</c:when>
<c:otherwise>
	<script language="javascript">
		function formtypeShow(value) {
			if(value == 'text') {
				$('tb_choice').style.display = 'none';
			} else {
				$('tb_choice').style.display = '';
			}
		}
	</script>
	
	<form method="post" action="admincp.jsp?ac=profilefield&fieldid=${thevalue.fieldid}">
	<input type="hidden" name="formhash" value="${FORMHASH}" />
	
	<div class="bdrcontent">
		
		<table cellspacing="0" cellpadding="0" class="formtable">
		<tr><th style="width:15em;">栏目名称</th><td><input type="text" name="title" value="${thevalue.title}"></td></tr>
		<tr><th>表单类型</th><td>
			<select name="formtype" onchange="formtypeShow(this.value)">
			<option value="text"${formtypearr.text}>文本输入框</option>
			<option value="select"${formtypearr.select}>列表框</option>
			</select>
		</td></tr>
	
		<tbody id="tb_choice"${thevalue.formtype =='text' ? ' style="display:none;"' : ''} >
		<tr><th>可选值</th><td><textarea name="choice" rows="5" cols="20">${thevalue.choice}</textarea>
			<br />每行一个值，例如输入:<br />北京<br />上海</td></tr>
		</tbody>

		<tr><th>可填写的最多字符</th><td><input type="text" name="maxsize" value="${thevalue.maxsize}" size="5"> (1~255)</td></tr>
		<tr><th>必填</th><td><input type="radio" name="required" value="0"${thevalue.required != 1 ? ' checked' : ''}> 否
			<input type="radio" name="required" value="1"${thevalue.required ==1 ? ' checked' : ''}> 是</td></tr>
		<tr><th>资料页面隐藏</th><td><input type="radio" name="invisible" value="0"${thevalue.invisible != 1 ? ' checked' : ''}> 否
			<input type="radio" name="invisible" value="1"${thevalue.invisible == 1 ? ' checked' : ''}> 是</td></tr>
		<tr><th>允许搜索</th><td><input type="radio" name="allowsearch" value="0"${thevalue.allowsearch != 1 ? ' checked' : ''}> 否
			<input type="radio" name="allowsearch" value="1"${thevalue.allowsearch ==1 ? ' checked' : ''}> 是</td></tr>

		<tr><th>简单介绍</th><td><input type="text" name="note" value="${thevalue.note}" size="40"></td></tr>
		<tr><th>显示顺序</th><td><input type="text" name="displayorder" value="${thevalue.displayorder}" size="5"></td></tr>
		</table>
	</div>
		
	<div class="footactions">
		<input type="submit" name="fieldsubmit" value="提交" class="submit">
	</div>
	
	</form>
</c:otherwise>
</c:choose>
</div>
</div>

<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>

<jsp:directive.include file="footer.jsp" />