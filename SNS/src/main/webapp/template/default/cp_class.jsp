<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${param.op == 'edit'}">

<h1>修改分类</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="${classid}">
<form id="classform" name="classform" method="post" action="cp.jsp?ac=class&op=edit&classid=${classid}">
	<p>
		<label for="classname">新分类名：</label>
		<input type="text" id="classname" name="classname" value="${classmap.classname}" size="10">
	</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}" />
		<c:choose>
		<c:when test="${sGlobal.inajax == 1}">
		<input type="hidden" name="editsubmit" value="true">
		<input type="submit" name="editsubmit_btn" value="提交" class="submit" />
		</c:when>
		<c:otherwise>
		<input type="submit" name="editsubmit" value="提交" class="submit" />
		</c:otherwise>
		</c:choose>
	</p>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${param.op == 'delete'}">

<h1>删除分类</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="${classid}">
	<form id="classform" name="classform" method="post" action="cp.jsp?ac=class&op=delete&classid=${classid}">
	<p>确定删除指定的分类吗？</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}" />
		<c:choose>
		<c:when test="${sGlobal.inajax == 1}">
		<input type="hidden" name="deletesubmit" value="true" />
		<input type="submit" name="deletesubmit_btn" value="确定" class="submit" />
		</c:when>
		<c:otherwise>
		<input type="submit" name="deletesubmit" value="确定" class="submit" />
		</c:otherwise>
		</c:choose>
	</p>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
</div>

</c:when>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>