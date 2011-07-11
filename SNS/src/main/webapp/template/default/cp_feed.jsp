<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${param.op == 'getcomment'}">

	<div class="fc">

	<div class="comments_list">
		<ul id="comment_ol_${feedid}">
		<c:forEach items="${list}" var="value">
			<c:set var="comment" value="${value}" scope="request"></c:set>
			<jsp:include page="${jch:template(sConfig, sGlobal, 'space_comment_li.jsp')}"/>
		</c:forEach>
		</ul>
	</div>
	<c:if test="${!empty  multi}"><div class="page">${multi}</div></c:if>

	<form id="feedform_${feedid}" method="post" action="cp.jsp?ac=feed&feedid=${feedid}" style="padding:0.5em;">
		<a href="###" id="face_${feedid}" title="插入表情" onclick="showFace(this.id, 'feedmessage_${feedid}');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
		<span id="note_${feedid}"></span><br>
		<textarea id="feedmessage_${feedid}" name="message" rows="2" style="width:100%;"></textarea><br>
		<input type="hidden" name="commentsubmit" value="true" />
		<input type="button" name="feedbutton" class="submit" onclick="ajaxpost('feedform_${feedid}', 'feedcomment_add')" value="评论">
		<span id="__feedform_${feedid}"></span>
		<input type="hidden" name="refer" value="space.jsp?do=hot&id=${feedid}">
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
	</div>

</c:when>
<c:when test="${param.op == 'getfeed'}">

	<div class="feed">
		<div id="feed_div">
			<ul>
			<%
				Map tpl = request.getAttribute("tpl") == null ? new HashMap() : (Map) request.getAttribute("tpl");
				tpl.put("hidden_menu", 1);
				tpl.put("hidden_hot", 1);
				tpl.put("hidden_time", 1);
				tpl.put("hidden_more", 1);
			%>
			<c:forEach items="${feedlist}" var="feed">				
				<!--{eval $value=mkfeed($value);}-->
				<!--{template space_feed_li}-->
			</c:forEach>
			</ul>
		</div>
	</div>
	<c:if test="${!empty feed_multi}"><div class="page">${feed_multi}</div></c:if>

</c:when>
<c:when test="${param.op == 'menu'}">

	<c:choose>
	<c:when test="${feed.uid == sGlobal.supe_uid}">
	
	<h1>删除动态</h1>
	<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
	<div class="popupmenu_inner" id="__feedform_${feedid}">
		<form method="post" id="feedform_${feedid}" name="feedform_${feedid}" action="cp.jsp?ac=feed&op=delete&feedid=${feedid}">
		<p>确定删除该动态吗？</p>
		<p class="btn_line">
			<input type="hidden" name="refer" value="${sGlobal.refer}">
			<c:choose>
			<c:when test="${sGlobal.inajax == 1}">
				<input type="hidden" name="feedsubmit" value="true" />
				<button name="feedsubmit_btn" type="button" class="submit" value="true" onclick="ajaxpost('feedform_${feedid}', 'feed_delete', 2000)">确定</button>
			</c:when>
			<c:otherwise>
				<button name="feedsubmit" type="submit" class="submit" value="true">确定</button>
			</c:otherwise>
			</c:choose>
			<c:if test="${managefeed}">
				<a href="admincp.jsp?ac=feed&op=edit&feedid=${feedid}" target="_blank">编辑</a>
			</c:if>
		</p>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
	</c:when>
	<c:otherwise>
	
	<h1>屏蔽动态</h1>
	<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
	<div class="popupmenu_inner" id="__feedform_${feedid}">
		<form method="post" id="feedform_${feedid}" name="feedform_${feedid}" action="cp.jsp?ac=feed&op=ignore&icon=${feed.icon}">
		<p>在下次浏览时不再显示此类动态</p>
		<p class="btn_line">
			<input type="radio" name="uid" id="uid1" value="${feed.uid}" checked> <label for="uid1">仅屏蔽该好友的</label>
			<br>
			<input type="radio" name="uid" id="uid0" value="0"> <label for="uid0">屏蔽所有好友的</label>
			<br><br>
			<input type="hidden" name="refer" value="${sGlobal.refer}">
			<c:choose>
			<c:when test="${sGlobal.inajax == 1}">
				<input type="hidden" name="feedignoresubmit" value="true" />
				<button name="feedignoresubmit_btn" type="button" class="submit" value="true" onclick="ajaxpost('feedform_${feedid}','',2000)">确定</button>
			</c:when>
			<c:otherwise>
				<button name="feedignoresubmit" type="submit" class="submit" value="true">确定</button>
			</c:otherwise>
			</c:choose>
			<c:if test="${managefeed}">
			<a href="admincp.jsp?ac=feed&op=edit&feedid=${feedid}" target="_blank">编辑</a>
			</c:if>
		</p>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
	</c:otherwise>
	</c:choose>
</c:when>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>