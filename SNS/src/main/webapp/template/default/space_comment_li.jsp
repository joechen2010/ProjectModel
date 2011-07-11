<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:if test="${jch:jchEmpty(ajax_edit)}"><li id="comment_${comment.cid}_li"></c:if>
	<c:choose>
	<c:when test="${not empty comment.author}">
	<div class="avatar48"><a href="space.jsp?uid=${comment.authorid}">${jch:avatar1(comment.authorid,sGlobal, sConfig)}</a></div>
	</c:when>
	<c:otherwise>
	<div class="avatar48"><img src="image/magic/hidden.gif" class="avatar" /></div>
	</c:otherwise>
	</c:choose>
	<div class="title">
		<div class="r_option">
		
		<c:if test="${comment.authorid!=sGlobal.supe_uid && comment.author=='' && not empty globalMagic.reveal}">
			<a id="a_magic_reveal_${comment.cid}" href="magic.jsp?mid=reveal&idtype=cid&id=${comment.cid}" onclick="ajaxmenu(event,this.id,1)"><img src="image/magic/reveal.small.gif" class="magicicon">${globalMagic.reveal}</a>
			<span class="pipe">|</span>
		</c:if>
			<c:if test="${comment.authorid==sGlobal.supe_uid}">
				<c:if test="${not empty globalMagic.anonymous}">
				<img src="image/magic/anonymous.small.gif" class="magicicon">
				<a id="a_magic_anonymous_${comment.cid}" href="magic.jsp?mid=anonymous&idtype=cid&id=${comment.cid}" onclick="ajaxmenu(event,this.id, 1)">${globalMagic.anonymous}</a>
				<span class="pipe">|</span>
				</c:if>
				<c:if test="${not empty globalMagic.flicker}">
				<img src="image/magic/flicker.small.gif" class="magicicon">
					<c:choose>
					<c:when test="${comment.magicflicker>0}">
				<a id="a_magic_flicker_${comment.cid}" href="cp.jsp?ac=magic&op=cancelflicker&idtype=cid&id=${comment.cid}" onclick="ajaxmenu(event,this.id)">取消${globalMagic.flicker}</a>
					</c:when>
					<c:otherwise>
				<a id="a_magic_flicker_${comment.cid}" href="magic.jsp?mid=flicker&idtype=cid&id=${comment.cid}" onclick="ajaxmenu(event,this.id, 1)">${globalMagic.flicker}</a>
					</c:otherwise>
					</c:choose>
				<span class="pipe">|</span>
				</c:if>
				
				<a href="cp.jsp?ac=comment&op=edit&cid=${comment.cid}" id="c_${comment.cid}_edit" onclick="ajaxmenu(event, this.id, 1)">编辑</a>
			</c:if>
			<c:if test="${comment.authorid==sGlobal.supe_uid || comment.uid==sGlobal.supe_uid || jch:checkPerm(pageContext.request, pageContext.response,'managecomment')}">
				<a href="cp.jsp?ac=comment&op=delete&cid=${comment.cid}" id="c_${comment.cid}_delete" onclick="ajaxmenu(event, this.id)">删除</a>
			</c:if>
			<c:if test="${comment.authorid!=sGlobal.supe_uid && (comment.idtype!='uid' || space.self)}">
				<a href="cp.jsp?ac=comment&op=reply&cid=${comment.cid}&feedid=${feedid}" id="c_${comment.cid}_reply" onclick="ajaxmenu(event, this.id, 1)">回复</a>
			</c:if>
			<a href="cp.jsp?ac=common&op=report&idtype=comment&id=${comment.cid}" id="a_report_${comment.cid}" onclick="ajaxmenu(event, this.id, 1)">举报</a>
		</div>
		<c:choose>
		<c:when test="${not empty comment.author}">
		<a href="space.jsp?uid=${comment.authorid}" id="author_${comment.cid}">${sNames[comment.authorid]}</a> 
		</c:when>
		<c:otherwise>
		匿名
		</c:otherwise>
		</c:choose>
		<span class="gray"><jch:date dateformat="yyyy-MM-dd HH:mm" timestamp="${comment.dateline}" format="1"/></span>
	</div>
	<div class="detail<c:if test="${comment.magicflicker>0}"> magicflicker</c:if>" id="comment_${comment.cid}">${comment.message}</div>
	
<c:if test="${jch:jchEmpty(ajax_edit)}"></li></c:if>