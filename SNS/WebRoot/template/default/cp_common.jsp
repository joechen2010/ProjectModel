<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${param.op == 'report'}">
		<h1>举报违规</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner" id="__reportform_${id}">
			<form method="post" id="reportform_${id}" name="reportform_${id}" action="cp.jsp?ac=common&op=report&idtype=${idType}&id=${id}">
				<table>
					<tr>
						<td>
							感谢您能协助我们一起管理站点，我们会对您的举报尽快处理。
							<br>请填写举报理由(最多150个字符):
							<img src="image/zoomin.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('reason', 1)">
							<img src="image/zoomout.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('reason', 0)">
							<br /><textarea id="reason" name="reason" cols="72" rows="3" onkeydown="ctrlEnter(event, 'reportsubmit_btn')"></textarea>
							<c:if test="${!empty reason}">
								<br /><select name="reportinfo" onchange="$('reason').value=this.value">
									<option value="" selected>举报理由</option>
									<c:forEach items="${reason}" var="val"><option value="${val}">${val}</option></c:forEach>
								</select>
							</c:if>
						</td>
					</tr>
					<tr>
						<td>
							<input type="hidden" name="reportsubmit" value="true">
							<input type="hidden" name="refer" value="${sGlobal.refer}">
							<c:choose>
								<c:when test="${sGlobal.inajax == 1}"><button name="reportsubmit_btn" id="reportsubmit_btn" type="button" class="submit" value="true" onclick="ajaxpost('reportform_${id}','',2000);">确定</button></c:when>
								<c:otherwise><button name="reportsubmit_btn" id="reportsubmit_btn" type="submit" class="submit" value="true">确定</button></c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
		</div>
	</c:when>
	<c:when test="${param.op == 'ignore'}">
		<h1>屏蔽通知</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner" id="__ignoreform_${formid}">
			<form method="post" id="ignoreform_${formid}" name="ignoreform_${formid}" action="cp.jsp?ac=common&op=ignore&type=${type}">
				<p>屏蔽后将不再接收指定好友的此类通知</p>
				<p class="btn_line">
					<input type="radio" name="authorid" id="authorid1" value="${param.authorid}" checked>
					<label for="uid1">仅屏蔽该好友的</label>
					<br>
					<input type="radio" name="authorid" id="authorid0" value="0">
					<label for="uid0">屏蔽所有好友的</label>
					<br><br>
					<input type="hidden" name="refer" value="${sGlobal.refer}">
					<c:choose>
						<c:when test="${sGlobal.inajax == 1}">
							<input type="hidden" name="ignoresubmit" value="true" />
							<button name="noteignoresubmit_btn" type="submit" class="submit" value="true" onclick="ajaxpost('ignoreform_${formid}','',2000)">确定</button>
						</c:when>
						<c:otherwise><button name="feedignoresubmit" type="submit" class="submit" value="true">确定</button></c:otherwise>
					</c:choose>
				</p>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
		</div>
	</c:when>
	<c:when test="${param.op == 'getuserapp'}">
		<c:forEach items="${my_userapp}" var="value">
			<c:if test="${value.allowsidenav == 1}"><li><img src="http://appicon.jsprun.com/icons/${value.appid}"><a href="userapp.jsp?id=${value.appid}">${value.appname}</a></li></c:if>
		</c:forEach>
	</c:when>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />