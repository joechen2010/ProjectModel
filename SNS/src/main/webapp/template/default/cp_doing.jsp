<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${param.op == 'delete'}">
		<h1>删除记录</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner" id="__doingform_${doid}_${id}">
			<form method="post" id="doingform_${doid}_${id}" name="doingform" action="cp.jsp?ac=doing&op=delete&doid=${doid}&id=${id}">
				<p>确定删除该记录吗？</p>
				<p class="btn_line"><input type="hidden" name="refer" value="${sGlobal.refer}"> <button name="deletesubmit" type="submit" class="submit" value="true">确定</button></p>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
		</div>
	</c:when>
	<c:when test="${param.op == 'getmood'}">${space.spacenote}</c:when>
	<c:when test="${param.op == 'docomment' || param.op == 'getcomment'}">
		<span id="docomment_form_${doid}_${id}">
			<form id="docommform_${doid}_${id}" method="post" action="cp.jsp?ac=doing&op=comment&doid=${doid}&id=${id}" style="padding-left: 10px;">
				<a href="#" id="do_face_${doid}_${id}" title="插入表情" onclick="showFace(this.id, 'do_message_${doid}_${id}');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
				<input type="text" id="do_message_${doid}_${id}" name="message" size="35" class="t_input" onkeydown="return ctrlEnter(event, 'docommform_btn_${doid}_${id}', 1);">
				<input type="hidden" name="commentsubmit" value="true" />
				<input type="button" name="do_button" class="submit" id="docommform_btn_${doid}_${id}" onclick="ajaxpost('docommform_${doid}_${id}', 'docomment_get', 1)" value="回复">
				<button type="button" name="btncancel" class="button" onclick="docomment_form_close(${doid}, ${id});">取消</button>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
			<div id="__docommform_${doid}_${id}"></div>
		</span>
		<c:if test="${param.op == 'getcomment'}"><jsp:include page="${jch:template(sConfig, sGlobal, 'space_doing_li.jsp')}" /></c:if>
	</c:when>
	<c:otherwise><div id="content"><jsp:include page="${jch:template(sConfig, sGlobal, 'space_doing_form.jsp')}" /></div></c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />