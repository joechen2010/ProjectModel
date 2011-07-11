<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<form method="post" id="doingform" action="cp.jsp?ac=doing&view=${param.view}" class="post_doing">
	<div class="r_option">还可输入 <strong id="maxlimit">200</strong> 个字符</div>
	<a href="###" id="doingface" onclick="showFace(this.id, 'message');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
	<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'seccode')}">
		<c:choose>
			<c:when test="${sConfig.questionmode>0}">回答提问：${jch:question(pageContext.request,pageContext.response)}</c:when>
			<c:otherwise>输入验证码：<script>seccode();</script></c:otherwise>
		</c:choose>
		<input type="text" id="seccode" name="seccode" value="" size="10" class="t_input">
	</c:if>
	<br>
	<textarea id="message" name="message" onkeyup="textCounter(this, 'maxlimit', 200)" onkeydown="ctrlEnter(event, 'add');" rows="4" style="width: 438px; height: 72px;"></textarea>
	<input type="hidden" name="addsubmit" value="true" />
	<button type="submit" id="add" name="add" class="post_button"></button>
	<input type="hidden" name="refer" value="${theurl}" />
	<input type="hidden" name="topicid" value="${topicid}" />
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>