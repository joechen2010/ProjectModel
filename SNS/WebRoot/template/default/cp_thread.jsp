<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${param.op=='edit'}">
<h1>编辑</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner">

	<form id="editpostform_${pid}" name="editpostform_${pid}" method="post" action="cp.jsp?ac=thread&op=edit&pid=${pid}&tagid=${tagid}&eventid=${eventid}">
		<table>
			<tr>
				<th style="vertical-align: top;"><label for="message">内容：</label></th>
				<td>
					<a href="###" id="face_${pid}" onclick="showFace(this.id, 'message_${pid}');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
					<img src="image/zoomin.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('message_${pid}', 1)">
					<img src="image/zoomout.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('message_${pid}', 0)"><br/>
					<textarea id="message_${pid}" name="message" onkeydown="ctrlEnter(event, 'posteditsubmit_btn');" <c:choose><c:when test="${!jch:jchEmpty(post.isthread)}">rows="18" style="width:98%;"</c:when><c:otherwise>rows="8" cols="50"</c:otherwise></c:choose>>${post.message}</textarea>
				</td>
			</tr>
			<tbody id="editwebimg">
				<tr>
					<th>图片：</th>
					<td>
						<input class="t_input" type="text" onfocus="if(this.value == 'http://')this.value='';" onblur="if(this.value=='')this.value='http://'" name="pics" value="http://" size="40" />&nbsp;
						<a href="javascript:;" onclick="insertWebImg(this.previousSibling.previousSibling)">插入</a> &nbsp;
						<a href="javascript:;" onclick="delRow(this, 'editwebimg')">删除</a>
					</td>
				</tr>
			</tbody>
			<tr>
				<th>&nbsp;</th>
				<td>
					<a href="javascript:;" onclick="copyRow('editwebimg')">+增加图片</a> <span class="gray">只支持 .jpg、.gif、.png为结尾的URL地址</span>
				</td>
			</tr>
			<tr>
				<th>&nbsp;</th>
				<td>
				<input type="hidden" name="pid" value="${pid}">
				<input type="hidden" name="refer" value="${sGlobal.refer}" />
				<input type="hidden" name="posteditsubmit" value="true" />
				<c:choose>
				<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
				<input type="button" name="posteditsubmit_btn" id="posteditsubmit_btn" value="提交" class="submit" onclick="ajaxpost('editpostform_${pid}', 'post_edit', 1)" />
				</c:when>
				<c:otherwise>
				<input type="submit" name="posteditsubmit_btn" id="posteditsubmit_btn" value="提交" class="submit" />
				</c:otherwise>
				</c:choose>
				
				<div id="__editpostform_${pid}"></div>
				</td>
			</tr>
		</table>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
</div>

</c:when>
<c:when test="${param.op=='delete'}">

<h1>删除回帖</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__deletepostform_${pid}">
<form id="deletepostform_${pid}" name="deletepostform_${pid}" method="post" action="cp.jsp?ac=thread&op=delete&pid=${pid}&tagid=${tagid}&eventid=${eventid}">
	<p>确定删除指定的帖子吗？</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}" />
		<c:choose>
		<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
		<input type="hidden" name="postdeletesubmit" value="true" />
		<input type="button" name="postdeletesubmit_btn" value="提交" class="submit" onclick="ajaxpost('deletepostform_${pid}', 'post_delete', 2000)" />&nbsp;
		</c:when>
		<c:otherwise>
		<input type="submit" name="postdeletesubmit" value="提交" class="submit" />&nbsp;
		</c:otherwise>
		</c:choose>
	</p>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${param.op=='edithot'}">

<h1>调整热度</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner">
<form method="post" action="cp.jsp?ac=thread&op=edithot&tid=${tid}">
	<p class="btn_line">
		新的热度：<input type="text" name="hot" value="${thread.hot}" size="5">
		<input type="hidden" name="refer" value="${sGlobal.refer}" />
		<input type="hidden" name="hotsubmit" value="true" />
		<input type="submit" name="btnsubmit" value="确定" class="submit" />
	</p>
<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${param.op=='reply'}">

<h1>回复</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner">
<form id="reply_postform_${post.pid}" name="reply_postform_${post.pid}" method="post" action="cp.jsp?ac=thread&eventid=${eventid}">
	<table>
		<tr>
			<th><label for="message">内容：</label></th>
			<td>
				<a href="###" id="face_${post.pid}" onclick="showFace(this.id, 'message_${post.pid}');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
				<img src="image/zoomin.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('message_${post.pid}', 1)">
				<img src="image/zoomout.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('message_${post.pid}', 0)"><br/>
				<textarea id="message_${post.pid}" name="message" onkeydown="ctrlEnter(event, 'postsubmit');" rows="8" cols="70"></textarea>
			</td>
		</tr>
		<tbody id="replybimg">
			<tr>
				<td>图片：</td>
				<td>
					<input class="t_input" type="text" name="pics" onfocus="if(this.value == 'http://')this.value='';" onblur="if(this.value=='')this.value='http://'" value="http://" size="30" />&nbsp;
					<a href="javascript:;" onclick="insertWebImg(this.previousSibling.previousSibling)">插入</a> &nbsp;
					<a href="javascript:;" onclick="delRow(this, 'replybimg')">删除</a>
				</td>
			</tr>
		</tbody>
		<tr>
			<th>&nbsp;</th>
			<td>
				<a href="javascript:;" onclick="copyRow('replybimg')">+增加图片</a> <span class="gray">只支持 .jpg、.gif、.png为结尾的URL地址</span>
			</td>
		</tr>

		<tr>
			<td>&nbsp;</td>
			<td>
				<input type="hidden" name="refer" value="${sGlobal.refer}" />
				<input type="hidden" name="tid" value="${post.tid}" />
				<input type="hidden" name="pid" value="${post.pid}" />
				<c:choose>
				<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
				<input type="hidden" name="postsubmit" value="true" />
				<input type="submit" name="postsubmit_btn" id="postsubmit" value="回复" class="submit" onclick="ajaxpost('reply_postform_${post.pid}', 'post_add', 1)" />
				</c:when>
				<c:otherwise>
				<input type="submit" name="postsubmit" id="postsubmit" value="回复" class="submit" />
				</c:otherwise>
				</c:choose>
				<div id="__reply_postform_${post.pid}"></div>
			</td>
		</tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:otherwise>

<script language="javascript" src="image/editor/editor_function.js"></script>
<script language="javascript" src="source/script_blog.js"></script>

<c:choose>
<c:when test="${not empty topic}">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_topic_menu.jsp')}"/>
</c:when>
<c:otherwise>
	<h2 class="title">
		<c:choose>
		<c:when test="${eventid!=0}">
		<img src="image/app/event.gif">活动 - <a href="space.jsp?do=event&id=${eventid}">${event.title}</a>
		</c:when>
		<c:otherwise>
		<img src="image/app/mtag.gif" />群组
		</c:otherwise>
		</c:choose>
	</h2>
	<div class="tabs_header">
		<ul class="tabs">
		<c:choose>
		<c:when test="${eventid!=0}">
			<li class="active"><a href="cp.jsp?ac=thread&eventid=${eventid}"><span>发表活动话题</span></a></li>
			<li><a href="space.jsp?do=event&id=${eventid}"><span>返回活动</span></a></li>
		</c:when>
		<c:when test="${not empty thread}">
			<li class="active"><a href="cp.jsp?ac=thread&op=edit&pid=${post.pid}"><span>编辑话题</span></a></li>
			<li><a href="space.jsp?do=mtag&tagid=${thread.tagid}&view=list"><span>返回讨论区</span></a></li>
		</c:when>
		<c:otherwise>
			<li class="active"><a href="cp.jsp?ac=thread"><span>发表新话题</span></a></li>
			<li><a href="space.jsp?do=thread&view=me"><span>返回我的话题</span></a></li>
		</c:otherwise>
		</c:choose>
		</ul>
	</div>
</c:otherwise>
</c:choose>

<div class="c_form">

<style>
	.userData {behavior:url(#default#userdata);}
</style>

	<form method="post" action="cp.jsp?ac=thread&eventid=${eventid}">
		<table cellspacing="4" cellpadding="4" width="100%" class="infotable">
		<c:choose>
		<c:when test="${eventid!=0}">
			<tr>
				<td>
					<input type="hidden" name="tagid" maxlength="100" value="${tagid}" />
				</td>
			</tr>
		</c:when>
		<c:when test="${tagid==0}">
			<tr>
				<td>
				<select name="tagid" id="tagid">
				<c:forEach items="${mtagList}" var="fields">
				<c:forEach items="${fields.value}" var="mtag">
					<option value="${mtag.value.tagid}">[${globalProfield[mtag.value.fieldid].title}] ${mtag.value.tagname}</option>>
				</c:forEach>
				</c:forEach>
				</select>
				<a href="cp.jsp?ac=mtag">创建新群组</a>
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
				<td>${mtag.tagname}<c:if test="${empty thread}"> [<a href="cp.jsp?ac=thread">切换</a>]</c:if>
					<input type="hidden" name="tagid" value="${tagid}" />
				</td>
			</tr>
		</c:otherwise>
		</c:choose>
			<tr>
				<td><input type="text" class="t_input" id="subject" name="subject" value="${thread.subject}" size="60" /></td>
			</tr>
			<tr>
				<td>
				<a id="doodleBox" href="magic.jsp?mid=doodle&showid=blog_doodle&target=jchome-ttHtmlEditor&from=editor" style="display:none"></a>
				<textarea class="userData" name="message" id="jchome-ttHtmlEditor" style="height:100%;width:100%;display:none;border:0px">${post.message}</textarea>
				<iframe src="editor.jsp?charset=${jchConfig.charset}&allowhtml=${jch:checkPerm(pageContext.request, pageContext.response,'allowhtml') ? 1 : 0}&doodle=${not empty globalMagic.doodle ? 1 : ''}" name="jchome-ifrHtmlEditor" id="jchome-ifrHtmlEditor" scrolling="no" border="0" frameborder="0" style="width:100%;border: 1px solid #C5C5C5;" height="400"></iframe>
				</td>
			</tr>

			<c:if test="${jch:checkPerm(pageContext.request, pageContext.response,'seccode')}">
			<c:choose>
			<c:when test="${!jch:jchEmpty(sConfig.questionmode)}">
			<tr>
				<td>
					<p>请回答验证问题</p>
					<p>${jch:question(pageContext.request,pageContext.response)}</p>
					<input type="text" id="seccode" name="seccode" value="" size="15" class="t_input" />
				</td>
			</tr>
			</c:when>
			<c:otherwise>
			<tr>
				<td>
					<p>请填写验证码</p>
					<script>seccode();</script>
					<p>请输入上面的4位字母或数字，看不清可<a href="javascript:updateseccode()">更换一张</a></p>
					<input type="text" id="seccode" name="seccode" value="" size="15" class="t_input" />
				</td>
			</tr>
			</c:otherwise>
			</c:choose>
			</c:if>

			<tr>
				<td>
					<input type="checkbox" name="makefeed" id="makefeed" value="1" <c:if test="${jch:jchEmpty(mtag.viewperm) && ckprivacy}"> checked</c:if>> 产生动态 (<a href="cp.jsp?ac=privacy#feed" target="_blank">更改默认设置</a>)
				</td>
			</tr>

		</table>
		<input type="hidden" name="tid" value="${thread.tid}" />
		<input type="hidden" name="threadsubmit" value="true" />
		<input type="button" id="threadbutton" name="threadbutton" value="提交发布" onclick="validate(this);" style="display: none;" />
		<input type="hidden" name="topicid" value="${param.topicid}" />
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
	<c:if test="${empty thread || thread.uid==0 || thread.uid==sGlobal.supe_uid}">
	<table cellspacing="4" cellpadding="4" width="100%" class="infotable">
		<tr><td>
		<input type="button" name="clickbutton[]" value="上传图片" class="button" onclick="edit_album_show('pic')">
		<input type="button" name="clickbutton[]" value="插入图片" class="button" onclick="edit_album_show('album')">
		</td></tr>
	</table>
	</c:if>
	<table cellspacing="4" cellpadding="4" width="100%" id="jchome-edit-pic" class="infotable" style="display:none;">
		<tr>
			<td>
				<strong>选择图片</strong>:
				<table summary="Upload" cellspacing="2" cellpadding="0">
					<tbody id="attachbodyhidden" style="display:none">
						<tr>
							<td>
								<form method="post" id="upload" action="cp.jsp?ac=upload" enctype="multipart/form-data" target="uploadframe" style="background: transparent;">
									<input type="file" name="attach" style="border: 1px solid #CCC;" />
									<span id="localfile"></span>
									<input type="hidden" name="uploadsubmit" id="uploadsubmit" value="true" />
									<input type="hidden" name="albumid" id="albumid" value="0" />
									<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
								</form>
							</td>
						</tr>
					</tbody>
					<tbody id="attachbody"></tbody>
				</table>
				<strong>存储相册</strong>:
				<table cellspacing="2" cellpadding="0">
					<tr>
						<td>
							<select name="albumid" id="uploadalbum" onchange="addSort(this)">
								<option value="-1">请选择相册</option>
								<option value="-1">默认相册</option>
								<c:forEach items="${albums}" var="value">
								<option value="${value.albumid}">${value.albumname}</option>
								</c:forEach>
								<option value="addoption" style="color:red;">+新建相册</option>
							</select>
							<script src="source/script_upload.js" type="text/javascript"></script>
							<iframe id="uploadframe" name="uploadframe" width="0" height="0" marginwidth="0" frameborder="0" src="about:blank"></iframe>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table cellspacing="4" cellpadding="4" width="100%" class="infotable" id="jchome-edit-album" style="display:none;">
		<tr>
			<td>
				选择相册: <select name="view_albumid" onchange="picView(this.value)">
					<option value="none">选择一个相册</option>
					<option value="0">默认相册</option>
					<c:forEach items="${albums}" var="value">
					<option value="${value.albumid}">${value.albumname}</option>
					</c:forEach>
				</select> (点击图片可以插入到内容中)
				<div id="albumpic_body"></div>
			</td>
		</tr>
	</table>
	<table cellspacing="4" cellpadding="4" width="100%" class="infotable">
		<tr>
			<td><input type="button" id="issuance" onclick="document.getElementById('threadbutton').click();" value="保存发布" class="submit" /></td>
		</tr>
	</table>
</div>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>