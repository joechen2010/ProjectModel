<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${op == 'delete'}">
<h1>删除分享</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__shareform_${sid }">
<form id="shareform_${sid }" name="shareform_${sid }" method="post" action="cp.jsp?ac=share&op=delete&sid=${sid }&type=${type }">
	<p>确定删除指定的分享吗？</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer }" />
		<c:choose>
		<c:when test="${!empty sGlobal.inajax && type == 'view'}">
		<input type="hidden" name="deletesubmit" value="true">
		<input type="button" name="deletesubmit_btn" value="确定" class="submit" onclick="ajaxpost('shareform_${sid}', 'share_delete', 2000)" />
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

<c:when test="${op == 'edithot'}">
<h1>调整热度</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner">
<form method="post" action="cp.jsp?ac=share&op=edithot&sid=${sid }">
	<p class="btn_line">
		新的热度：<input type="text" name="hot" value="${share.hot }" size="5"> 
		<input type="hidden" name="refer" value="${sGlobal.refer }" />
		<input type="hidden" name="hotsubmit" value="true" />
		<input type="submit" name="btnsubmit" value="确定" class="submit" />
	</p>
<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>
</c:when>

<c:when test="${op == 'link'}">
	<c:if test="${!empty topic}">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_topic_menu.jsp')}"/>
	</c:if>

	<div class="c_form">
	<form id="shareform" name="shareform" action="cp.jsp?ac=share&type=link" method="post">
	<table cellspacing="2" cellpadding="2" width="100%">
		<tr><td><strong>分享网址、视频、音乐、Flash:</strong></td></tr>
		<tr><td><input type="text" class="t_input" name="link" onfocus="javascript:if('http://'==this.value)this.value='';" onblur="javascript:if(''==this.value)this.value='http://'" id="share_link" style="width:98%;" value="http://" /></td></tr>
		<tr><td><strong>描述:</strong></td></tr>
		<tr>
			<td>
				<textarea id="share_general" name="general" style="width:98%;" rows="3" onkeydown="ctrlEnter(event, 'sharesubmit_btn')"></textarea>
			</td>
		</tr>
		<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'seccode')}">
		<tr>
			<td>
			<c:choose>
			<c:when test="${!jch:jchEmpty(sConfig.questionmode)}">
					<p>请正确回答下面问题后再提交</p>
					<p>${jch:question(pageContext.request,pageContext.response)}</p>
					<p><input type="text" id="seccode" name="seccode" value="" size="15" class="t_input"></p>
			</c:when>
			<c:otherwise>
					<p><script>seccode();</script></p>
					<p>请输入上面的4位字母或数字，看不清可<a href="javascript:updateseccode()">更换一张</a></p>
					<p><input type="text" id="seccode" name="seccode" value="" size="15" class="t_input"></p>
			</c:otherwise>
			</c:choose>
			</td>
		</tr>
		</c:if>
		<tr><td>
		<input type="hidden" name="refer" value="space.jsp?uid=${space.uid }&do=share&view=me" />
		<input type="hidden" name="topicid" value="${topicid }" />
		<input type="submit" name="sharesubmit" value="分享" class="submit" />
		</td></tr>
		<tr><td id="__shareform"></td></tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
	</div>
</c:when>

<c:otherwise>
	<h1>分享</h1>
	<a href="javascript: hideMenu();" class="float_del" title="关闭">关闭</a>
	<div id="__shareform_${id}" class="popupmenu_inner">
	
	<form method="post" id="shareform_${id}" name="shareform_${id}" action="cp.jsp?ac=share&type=${type }&id=${id }">
	<table>
	<tr>
		<td>分享说明:
			<img src="image/zoomin.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('general', 1)">
			<img src="image/zoomout.gif" onmouseover="this.style.cursor='pointer'" onclick="zoomTextarea('general', 0)"><br />
			<textarea id="general" name="general" style="width:380px;" rows="5" onkeydown="ctrlEnter(event, 'sharesubmit_btn')" onkeyup="showPreview(this.value, 'quote')"></textarea>
		</td>
	</tr>
	<tr>
		<td>
		<input type="hidden" name="sharesubmit" value="true">
		<input type="hidden" name="refer" value="${sGlobal.refer }">
		<c:choose>
		<c:when test="${!empty sGlobal.inajax}">
		<button name="sharesubmit_btn" id="sharesubmit_btn" type="button" class="submit" value="true" onclick="ajaxpost('shareform_${id}', 'showreward', 2000);">确定</button>
		</c:when>
		
		<c:otherwise>
		<button name="sharesubmit_btn" id="sharesubmit_btn" type="submit" class="submit" value="true">确定</button>
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
	</table>

	<br />
	<ul class="box">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_share_li.jsp')}"/>
	</ul>

	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
	
	</div>
</c:otherwise>

</c:choose>
	
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>