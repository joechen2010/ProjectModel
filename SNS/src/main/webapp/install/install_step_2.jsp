<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="install_header.jsp"></jsp:include>
<form id="theform" method="post" action="${theurl}?step=2">
	<table class="showtable">
		<tr><td><strong># 设置JavaCenter Home数据库信息</strong></td></tr>
		<tr><td id="msg1">这里设置JavaCenter Home的数据库信息</td></tr>
	</table>
	<table class=datatable>
		<tr>
			<td width="25%">数据库服务器地址:</td>
			<td><input type="text" name="db[dbHost]" size="20" value="localhost"></td>
			<td width="30%">一般为localhost或127.0.0.1</td>
		</tr>
		<tr>
			<td width="25%">数据库服务器端口:</td>
			<td><input type="text" name="db[dbPort]" size="20" value="3306"></td>
			<td width="30%">一般为3306</td>
		</tr>
		<tr>
			<td>数据库用户名:</td>
			<td><input type="text" name="db[dbUser]" size="20" value=""></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>数据库密码:</td>
			<td><input type="password" name="db[dbPw]" size="20" value=""></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>数据库名:</td>
			<td><input type="text" name="db[dbName]" size="20" value=""></td>
			<td>如果不存在，则会尝试自动创建</td>
		</tr>
		<tr>
			<td>表名前缀:</td>
			<td><input type="text" name="db[tablePre]" size="20" value="jchome_"></td>
			<td>不能为空，默认为jchome_</td>
		</tr>
	</table>
	<table class=button>
		<tr><td><input type="submit" id="sqlsubmit" name="sqlsubmit" value="设置完毕,检测我的数据库配置"></td></tr>
	</table>
	<input type="hidden" name="formhash" value="${formhash}">
</form>
<jsp:include page="install_footer.jsp"></jsp:include>