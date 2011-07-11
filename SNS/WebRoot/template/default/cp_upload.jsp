<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${not empty topic}">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_topic_menu.jsp')}"/>
</c:when>
<c:otherwise>
	<c:choose>
	<c:when test="${eventid != 0}">
	<h2 class="title"><img src="image/app/event.gif">活动 - <a href="space.jsp?do=event&id=${eventid}">${event.title}</a></h2>
	</c:when>
	<c:otherwise>
	<h2 class="title"><img src="image/app/album.gif">相册</h2>
	</c:otherwise>
	</c:choose>
	<div class="tabs_header">
		<c:if test="${not empty globalMagic.doodle}">
		<a id="a_doodle" class="r_option" href="magic.jsp?mid=doodle&showid=album_doodle&target=album_message&from=album" onclick="ajaxmenu(event, this.id, 1)"><img src="image/magic/doodle.small.gif" class="magicicon" />涂鸦板</a>
		</c:if>
		<ul class="tabs">
		<c:if test="${albumid != 0}">
			<li><a href="cp.jsp?ac=album&op=edit&albumid=${albumid}"><span>编辑相册信息</span></a></li>
			<li><a href="cp.jsp?ac=album&op=editpic&albumid=${albumid}"><span>编辑图片</span></a></li>
		</c:if>
		<c:choose>
		<c:when test="${eventid != 0}">
			<li ${actives['js']}><a href="cp.jsp?ac=upload&eventid=${eventid}"><span>普通上传</span></a></li>
			<li><a href="space.jsp?do=event&id=${eventid}"><span>返回活动首页</span></a></li>
		</c:when>
		<c:otherwise>
			<li ${actives['js']}><a href="cp.jsp?ac=upload&albumid=${albumid}"><span>普通上传</span></a></li>
			<li ${actives['flash']}><a href="cp.jsp?ac=upload&op=flash&albumid=${albumid}"><span>批量上传</span></a></li>
			<li ${actives['cam']}><a href="cp.jsp?ac=upload&op=cam&albumid=${albumid}"><span>大头帖</span></a></li>
			<li><a href="space.jsp?uid=${space.uid}&do=album&view=me"><span>返回我的相册</span></a></li>
		</c:otherwise>
		</c:choose>
		</ul>
	</div>
</c:otherwise>
</c:choose>

<div class="c_form">

<c:if test="${haveattachsize != '0'}">
<div class="borderbox">
	您当前图片空间还剩余容量 <strong>${haveattachsize}</strong>
	<c:if test="${not empty globalMagic.attachsize}">
	<p>
		<img src="image/magic/attachsize.small.gif" class="magicicon"/>
		<a id="a_magic_attachsize" href="magic.jsp?mid=attachsize" onclick="ajaxmenu(event, this.id, 1)">我要增加空间</a>
		(您可以购买道具“${globalMagic.attachsize}”来增加附件容量，上传更多的图片)
	</p>
	</c:if>
</div><br>
</c:if>

<c:choose>
<c:when test="${empty param.op}">
<table cellspacing="0" cellpadding="0" class="formtable">
	<caption>
		<h2>选择图片</h2>
		<p>从电脑中选择你要上传的图片。<br>提示：选择一张图片后，你可以继续选择下一张图片，这样就可以一次上传多张图片了。</p>
	</caption>
	<tbody id="attachbodyhidden" style="display:none">
		<tr>
			<td>
				<form method="post" id="upload" action="cp.jsp?ac=upload<c:if test="${eventid != 0}">&eventid=${eventid}</c:if>" enctype="multipart/form-data" target="uploadframe">
					<input type="file" name="attach" size="25" style="padding:10px;" />
					<span id="localfile"></span>					
					<input type="hidden" name="uploadsubmit" id="uploadsubmit" value="true" />
					<input type="hidden" name="albumid" id="albumid" value="0" />
					<input type="hidden" name="topicid" id="topicid" value="0" />
					<input type="hidden" name="formhash" value="${formhash}" />
				</form>
			</td>
		</tr>
	</tbody>
	<tbody id="attachbody"></tbody>
</table>

<script>
	no_insert = 1;
	function a_addOption() {
		var obj = $('uploadalbum');
		obj.value = 'addoption';
		addOption(obj);
	}
	function album_op(id) {
		$('selectalbum').style.display = 'none';
		$('creatalbum').style.display = 'none';
		$(id).style.display = '';
	}
</script>

<script src="source/script_upload.js" type="text/javascript"></script>
<iframe id="uploadframe" name="uploadframe" width="0" height="0" marginwidth="0" frameborder="0" src="about:blank"></iframe>
<form method="post" id="albumform" action="cp.jsp?ac=upload" target="uploadframe">
<table cellspacing="0" cellpadding="0" class="formtable">
<c:choose>
<c:when test="${not empty albums}">
	<caption>
		<h2>
		<input type="radio" id="albumop_selectalbum" name="albumop" value="selectalbum" checked onclick="album_op(this.value);"> <label for="albumop_selectalbum">添加到现有相册</label> &nbsp;
		<input type="radio" id="albumop_creatalbum" name="albumop" value="creatalbum" onclick="album_op(this.value);"> <label for="albumop_creatalbum">新建一个相册</label>
		</h2>
	</caption>
	<tbody id="selectalbum">
	<tr><td>
		<table width="100%">
		<tr>
			<th width="60">选择相册</th>
			<td>
			<select name="albumid" id="uploadalbumid">
			<c:forEach items="${albums}" var="value">
				<c:choose>
				<c:when test="${value.albumid == param.albumid}">
					<option value="${value.albumid}" selected>${value.albumname}</option>
				</c:when>
				<c:otherwise>
					<option value="${value.albumid}">${value.albumname}</option>
				</c:otherwise>
				</c:choose>
			</c:forEach>
			</select>
			</td>
		</tr>
		</table>
	</td>
	</tr>
	</tbody>
	<tbody id="creatalbum" style="display:none;">
</c:when>
<c:otherwise>
	<caption>
		<h2>新建一个相册</h2>
		<input type="hidden" name="albumop" value="creatalbum">
	</caption>
	<tbody id="creatalbum">
</c:otherwise>
</c:choose>
	<tr><td>
		<table width="100%">
			<tr>
				<th width="60">相册名</th>
				<td><input type="text" class="t_input" size="20" id="uploadalbumname" name="albumname" value="我的相册"></td>
			</tr>
			<tr>
				<th>隐私设置</th>
				<td>
					<select name="friend" id="uploadfriend" onchange="passwordShow(this.value);">
						<option value="0">全站用户可见</option>
						<c:if test="${empty param.topicid}">
						<option value="1">全好友可见</option>
						<option value="2">仅指定的好友可见</option>
						<option value="3">仅自己可见</option>
						<option value="4">凭密码查看</option>
						</c:if>
					</select>
					<span id="span_password" style="display:none;">密码:<input type="text" id="uploadpassword" name="password" value="" size="10"></span>
				</td>
			</tr>
			<tbody id="tb_selectgroup" style="display:none;">
			<tr>
				<th>指定好友</th>
				<td><select name="selectgroup" onchange="getgroup(this.value);">
					<option value="">从好友组选择好友</option>
					<c:forEach items="${groups}" var="m">
					<option value="${m.key}">${m.value}</option>
					</c:forEach>
					</select> 多次选择会累加到下面的好友名单</td>
			</tr>
			<tr>
				<th>&nbsp;</th>
				<td>
				<textarea name="target_names" id="target_names" style="width:85%;" rows="3"></textarea>
				<br>(可以填写多个好友名，请用空格进行分割)</td>
			</tr>
		</table>
	</td></tr>
	</tbody>
	<tr>
	<td>
	<br>
	<input type="hidden" name="albumsubmit" id="albumsubmit" value="true" />
	<input type="hidden" name="topicid" value="${param.topicid}" />
	<input type="submit" name="uploadsubmit" id="btnupload" value="开始上传" class="submit" />
	</td>
	</tr>
</table>
<input type="hidden" name="formhash" value="${formhash}" />
</form>

<form method="post" id="albumresultform" action="cp.jsp?ac=upload<c:if test="${eventid != 0}">&eventid=${eventid}</c:if>" class="c_form">
	<input type="hidden" name="opalbumid" id="opalbumid" value="0" />
	<input type="hidden" name="topicid" id="optopicid" value="0" />
	<input type="hidden" name="viewAlbumid" id="viewAlbumid" value="true" />
	<input type="hidden" name="formhash" value="${formhash}" />
</form>
</c:when>
<c:when test="${param.op == 'cam'}">
<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,45,0" width="100%" height="415">
	<param name="movie" value="image/cam.swf?site=${siteurl}&albumid=${param.albumid}" />
	<param name="quality" value="high" />
	<param name="WMode" value="transparent" />
	<embed src="image/cam.swf?site=${siteurl}&albumid=${param.albumid}" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="100%" height="415"></embed>
</object>
</c:when>
<c:otherwise>
<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,45,0" width="100%" height="400">
  <param name="movie" value="image/upload.swf?site=${siteurl}&albumid=${param.albumid}&topicid=${param.topicid}" />
  <param name="quality" value="high" />
  <param name="WMode" value="transparent" />
  <embed src="image/upload.swf?site=${siteurl}&albumid=${param.albumid}&topicid=${param.topicid}" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="100%" height="400"></embed>
</object>
</c:otherwise>
</c:choose>

</div>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>