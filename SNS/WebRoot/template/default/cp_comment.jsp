<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${param.op == 'edit'}">

<h1>编辑</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__editcommentform_${cid}">
<form id="editcommentform_${cid}" name="editcommentform_${cid}" method="post" action="cp.jsp?ac=comment&op=edit&cid=${cid}">
<table>
<tr>
	<td>
		<label for="message">编辑内容：</label>
		<a href="###" id="editface_${cid}" onclick="showFace(this.id, 'message_${cid}');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
		<img src="image/zoomin.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('message_${cid}', 1)">
		<img src="image/zoomout.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('message_${cid}', 0)">
		<br />
		<textarea id="message_${cid}" name="message" cols="70" onkeydown="ctrlEnter(event, 'editsubmit_btn');" rows="8">${comment.message}</textarea>
			
	</td>
</tr>
<tr>
	<td>
	<input type="hidden" name="refer" value="${sGlobal.refer}" />
	<input type="hidden" name="editsubmit" value="true" />
	<c:choose>
	<c:when test="${sGlobal.inajax == 1}">
	<input type="button" name="editsubmit_btn" id="editsubmit_btn" value="提交" class="submit" onclick="ajaxpost('editcommentform_${cid}', 'comment_edit', 2000)" />
	</c:when>
	<c:otherwise>
	<input type="submit" name="editsubmit_btn" id="editsubmit_btn" value="提交" class="submit" />
	</c:otherwise>
	</c:choose>
	</td>
</tr>
</table>
<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${param.op == 'delete'}">

<h1>删除回复</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__deletecommentform_${cid}">
<form id="deletecommentform_${cid}" name="deletecommentform_${cid}" method="post" action="cp.jsp?ac=comment&op=delete&cid=${cid}">
	<p>确定删除指定的回复吗？</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}" />
		<c:choose>
		<c:when test="${sGlobal.inajax == 1}">
		<input type="hidden" name="deletesubmit" value="true" />
		<input type="button" name="deletesubmit_btn" value="确定" class="submit" onclick="ajaxpost('deletecommentform_${cid}', 'comment_delete', 2000)" />
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
<c:when test="${param.op == 'reply'}">


<h1>回复</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__replycommentform_${comment.cid}">
<form id="replycommentform_${comment.cid}" name="replycommentform_${comment.cid}" method="post" action="cp.jsp?ac=comment">
	<table>
		<tbody id="reply_msg_${comment.cid}">
			<tr>
				<td>
					<label for="message">内容：</label>
					<a href="###" id="replyface_${comment.cid}" onclick="showFace(this.id, 'message_pop_${comment.cid}');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
					<img src="image/zoomin.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('message_pop_${comment.cid}', 1)">
	
					<img src="image/zoomout.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('message_pop_${comment.cid}', 0)">
	
					<br />
					<textarea id="message_pop_${comment.cid}" name="message" onkeydown="ctrlEnter(event, 'commentsubmit_btn');" rows="8" cols="70"></textarea>
				</td>
			</tr>
			<tr>
				<td>
					<input type="hidden" name="refer" value="${sGlobal.refer}" />
					<input type="hidden" name="id" value="${comment.id}">
					<input type="hidden" name="idtype" value="${comment.idtype}">
					<input type="hidden" name="cid" value="${comment.cid}" />
					<input type="hidden" name="commentsubmit" value="true" />
					<c:choose>
					<c:when test="${sGlobal.inajax == 1}">
						<c:choose>
						<c:when test="${comment.idtype == 'uid'}">
						<input type="button" name="commentsubmit_btn" id="commentsubmit_btn" value="回复" class="submit" onclick="ajaxpost('replycommentform_${comment.cid}','',2000)" />
						</c:when>
						<c:when test="${!empty param.feedid && param.feedid != 0}">
						<input type="button" name="commentsubmit_btn" id="commentsubmit_btn" value="回复" class="submit" onclick="ajaxpost('replycommentform_${comment.cid}', 'feedcomment_add', 2000)" />
						</c:when>
						<c:otherwise>
						<input type="button" name="commentsubmit_btn" id="commentsubmit_btn" value="回复" class="submit" onclick="ajaxpost('replycommentform_${comment.cid}', 'comment_add', 2000)" />
						</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
					<input type="submit" name="commentsubmit_btn" id="commentsubmit_btn" value="回复" class="submit" />
					</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</tbody>
		<tbody id="reply_doodle_${comment.cid}" style="display: none">
			<tr>
				<td>
					<object id="doodle_${comment.cid}" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,45,0" width="438" height="304">
						<param name="quality" value="high" />
						<param name="allowScriptAccess" value="always" />
						<param name="movie" value="image/doodle.swf?fid=reply_doodle_${comment.cid}&oid=message_pop_${comment.cid}&tid=reply_msg_${comment.cid}" />
						<embed src="image/doodle.swf?fid=reply_doodle_${comment.cid}&oid=message_pop_${comment.cid}&tid=reply_msg_${comment.cid}" allowScriptAccess="always" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="438" height="304"></embed>
					</object>
				</td>
			</tr>
			<tr>
				<td>
					<input type="button" name="back_btn" id="back_btn" value="返回" onclick="selCommentTab('reply_doodle_${comment.cid}', 'reply_msg_${comment.cid}');" class="button" />
				</td>
			</tr>
		</tbody>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>