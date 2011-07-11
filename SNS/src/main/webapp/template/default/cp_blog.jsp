<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${param.op == 'delete'}">

<h1>删除日志</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner">
<form method="post" action="cp.jsp?ac=blog&op=delete&blogid=${blogid}">
	<p>确定删除指定的日志吗？</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}" />
		<input type="hidden" name="deletesubmit" value="true" />
		<input type="submit" name="btnsubmit" value="确定" class="submit" />
	</p>
<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${param.op == 'edithot'}">

<h1>调整热度</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner">
<form method="post" action="cp.jsp?ac=blog&op=edithot&blogid=${blogid }">
	<p class="btn_line">
		新的热度：<input type="text" name="hot" value="${blog.hot }" size="5"> 
		<input type="hidden" name="refer" value="${sGlobal.refer }" />
		<input type="hidden" name="hotsubmit" value="true" />
		<input type="submit" name="btnsubmit" value="确定" class="submit" />
	</p>
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
		<h2 class="title"><img src="image/app/blog.gif" />日志</h2>
		<div class="tabs_header">
			<ul class="tabs">
				<c:if test="${not empty blog.blogid && blog.blogid != 0}">
				<li class="active"><a href="cp.jsp?ac=blog&blogid=${blog.blogid }"><span>编辑日志</span></a></li>
				</c:if>
				<li <c:if test="${empty blog.blogid }">class="active"</c:if>><a href="cp.jsp?ac=blog"><span>发表新日志</span></a></li>
				<li><a href="cp.jsp?ac=import"><span>日志导入</span></a></li>
				<li><a href="space.jsp?uid=${space.uid}&do=blog&view=me"><span>返回我的日志</span></a></li>
			</ul>
		</div>
	</c:otherwise>
	</c:choose>
<div class="c_form">

	<style type="text/css">
		.userData {behavior:url(#default#userdata);}
	</style>


	<form method="post" action="cp.jsp?ac=blog&blogid=${blog.blogid }">
		<table cellspacing="4" cellpadding="4" width="100%" class="infotable">
			<tr>
				<td>
					<select name="classid" id="classid" onchange="addSort(this)">
						<option value="0">选择分类</option>
						<c:forEach items="${classarr}" var="m">
							<c:choose>
							<c:when test="${m.value.classid == blog.classid }">
							<option value="${m.value.classid }" selected>${m.value.classname }</option>
							</c:when>
							<c:otherwise>
							<option value="${m.value.classid }">${m.value.classname }</option>	
							</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:if test="${empty blog.uid || blog.uid == sGlobal.supe_uid}"><option value="addoption" style="color:red;">+新建分类</option></c:if>
					</select>
					<input type="text" class="t_input" id="subject" name="subject" value="${blog.subject }" size="60" onblur="relatekw();" />
				</td>
			</tr>
			<tr>
				<td>
				<a id="doodleBox" href="magic.jsp?mid=doodle&showid=blog_doodle&target=jchome-ttHtmlEditor&from=editor" style="display:none"></a>
				<textarea class="userData" name="message" id="jchome-ttHtmlEditor" style="height:100%;width:100%;display:none;border:0px">${blog.message }</textarea>
				<iframe src="editor.jsp?allowhtml=${allowhtml }&doodle=<c:if test="${!jch:jchEmpty(globalMagic.doodle)}">1</c:if>" name="jchome-ifrHtmlEditor" id="jchome-ifrHtmlEditor" scrolling="no" border="0" frameborder="0" style="width:100%;border: 1px solid #C5C5C5;" height="400"></iframe>
				</td>
			</tr>
		</table>
		<table cellspacing="4" cellpadding="4" width="100%" class="infotable">
			<tr>
				<th width="100">标签</th>
				<td><input type="text" class="t_input" size="40" id="tag" name="tag" value="${blog.tag }"> <input type="button" name="clickbutton[]" value="自动获取" class="button" onclick="relatekw();"></td>
			</tr>
			
			<c:if test="${not empty blog.uid && blog.uid != sGlobal.supe_uid }">
				<c:set var="selectgroupstyle" value="display:none" scope="request"></c:set>
				<tbody style="display:none;">			
			</c:if>
			<tr>
				<th>隐私设置</th>
				<td>
					<select name="friend" onchange="passwordShow(this.value);">
						<option value="0" ${friend == 0 ? "selected" : ""}>全站用户可见</option>
						<option value="1" ${friend == 1 ? "selected" : ""}>全好友可见</option>
						<option value="2" ${friend == 2 ? "selected" : ""}>仅指定的好友可见</option>
						<option value="3" ${friend == 3 ? "selected" : ""}>仅自己可见</option>
						<option value="4" ${friend == 4 ? "selected" : ""}>凭密码查看</option>
					</select>
					<span id="span_password" style="${passwordstyle }">密码:<input type="text" name="password" value="${blog.password}" size="10" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')"></span>
					<input type="checkbox" name="noreply" value="1" <c:if test="${blog.noreply == 1 }">checked</c:if>> 不允许评论
				</td>
			</tr>
			<c:if test="${not empty blog.uid && blog.uid != sGlobal.supe_uid }">
				</tbody>
			</c:if>
			<tbody id="tb_selectgroup" style="${selectgroupstyle}">
			<tr>
				<th>指定好友</th>
				<td><select name="selectgroup" onchange="getgroup(this.value);">
					<option value="">从好友组选择好友</option>
					<c:forEach items="${groups}" var="m">
					<option value="${m.key }">${m.value }</option>
					</c:forEach>
					</select> 多次选择会累加到下面的好友名单
				</td>
			</tr>
			<tr>
				<th>&nbsp;</th>
				<td>
					<textarea name="target_names" id="target_names" style="width:85%;" rows="3">${blog.target_names }</textarea>
					<br>(可以填写多个好友名，请用空格进行分割)
				</td>
			</tr>
			</tbody>
			
			
			<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'manageblog')}">
			<tr>
				<th width="100">热度</th>
				<td>
					<input type="text" class="t_input" name="hot" id="hot" value="${blog.hot }" size="5">
				</td>
			</tr>
			</c:if>
			
			<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'seccode')}">
				<c:choose>
				<c:when test="${sConfig.questionmode==1}">
				<tr>
					<th style="vertical-align: top;">请回答验证问题</th>
					<td>
						<p>${jch:question(pageContext.request,pageContext.response)}</p>
						<input type="text" id="seccode" name="seccode" value="" size="15" class="t_input" />
					</td>
				</tr>
				</c:when>
				<c:otherwise>
				<tr>
					<th style="vertical-align: top;">请填写验证码</th>
					<td>
						<script>seccode();</script>
						<p>请输入上面的4位字母或数字，看不清可<a href="javascript:updateseccode()">更换一张</a></p>
						<input type="text" id="seccode" name="seccode" value="" size="15" class="t_input" />
					</td>
				</tr>
				</c:otherwise>
				</c:choose>
			</c:if>

			<tr>
				<th width="100">动态选项</th>
				<td>
					<input type="checkbox" name="makefeed" id="makefeed" value="1" <c:if test="${blogprivacy}">checked</c:if>> 产生动态 (<a href="cp.jsp?ac=privacy#feed" target="_blank">更改默认设置</a>)
				</td>
			</tr>			
		</table>
		<input type="hidden" name="blogsubmit" value="true" />
		<input type="button" id="blogbutton" name="blogbutton" value="提交发布" onclick="validate(this);" style="display: none;" />
		<input type="hidden" name="topicid" value="${param.topicid }" />
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>

	<c:if test="${sGlobal.inajax == 0 && (empty blog.uid || blog.uid == sGlobal.supe_uid)}">
	<table cellspacing="4" cellpadding="4" width="100%" class="infotable">
		<tr><th width="100">图片</th><td>
		<input type="button" name="clickbutton[]" value="上传图片" class="button" onclick="edit_album_show('pic')">
		<input type="button" name="clickbutton[]" value="插入图片" class="button" onclick="edit_album_show('album')">
		</td></tr>
	</table>
	</c:if>

	<table cellspacing="4" cellpadding="4" width="100%" id="jchome-edit-pic" class="infotable" style="display:none;">
		<tr>
			<th width="100">&nbsp;</th>
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
								<c:forEach items="${albums}" var="album">
								<option value="${album.albumid}">${album.albumname}</option>
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
			<th width="100">&nbsp;</th>
			<td>
				选择相册: <select name="view_albumid" onchange="picView(this.value)">
					<option value="none">选择一个相册</option>
					<option value="0">默认相册</option>
					<c:forEach items="${albums}" var="album"> 
					<option value="${album.albumid}">${album.albumname}</option>
					</c:forEach>
				</select> (点击图片可以插入到内容中)
				<div id="albumpic_body"></div>
			</td>
		</tr>
	</table>
	<table cellspacing="4" cellpadding="4" width="100%" class="infotable">
		<tr>
			<th width="100">&nbsp;</th>
			<td>
			<input type="button" id="issuance" onclick="document.getElementById('blogbutton').click();" value="保存发布" class="submit" /></td>
		</tr>
	</table>
</div>

</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>