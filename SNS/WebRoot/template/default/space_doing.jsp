<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${sGlobal.inajax>0}">
		<div id="space_doing">
			<h3 class="feed_header"><a href="space.jsp?do=doing&view=me" class="r_option" target="_blank">一句话记录</a> 记录(共 ${count} 条)</h3><br>
			<c:choose>
				<c:when test="${not empty dolist}">
					<div class="doing_list">
						<ol>
							<c:forEach items="${dolist}" var="dv">
								<li id="dl${dv.doid}">
									<div class="doing">
										<div class="doingcontent">
											<a href="space.jsp?uid=${dv.uid}">${sNames[dv.uid]}</a>:
											<span>${dv.message}</span>
											<span class="gray">(<jch:date dateformat="MM-dd HH:mm" timestamp="${dv.dateline}" format="1" />)</span>
											<c:if test="${dv.uid==sGlobal.supe_uid}"><a href="cp.jsp?ac=doing&op=delete&doid=${dv.doid}&id=${dv.id}" id="doing_delete_${dv.doid}_${dv.id}" onclick="ajaxmenu(event, this.id)" class="re gray">删除</a>&nbsp;</c:if>
											<a href="javascript:;" onclick="docomment_form(${dv.doid}, 0);">回复</a>
										</div>
										<c:set var="list" value="${clist[dv.doid]}" scope="request"></c:set>
										<c:set var="dv" value="${dv}" scope="request"></c:set>
										<div class="sub_doing" id="docomment_${dv.doid}" <c:if test="${empty list}"> style="display:none;"</c:if>>
											<span id="docomment_form_${dv.doid}_0"></span>
											<jsp:include page="${jch:template(sConfig, sGlobal, 'space_doing_li.jsp')}" />
										</div>
									</div>
								</li>
							</c:forEach>
						</ol>
						<div class="page">${multi}</div>
					</div>
				</c:when>
				<c:otherwise><div class="c_form">现在还没有记录</div></c:otherwise>
			</c:choose>
		</div><br>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${space.self}">
				<h2 class="title"><img src="image/app/doing.gif">记录</h2>
				<div class="tabs_header">
					<ul class="tabs">
						<c:if test="${space.friendnum>0}"><li${actives.we}><a href="space.jsp?uid=${space.uid}&do=${do}&view=we"><span>最新好友记录</span></a></li></c:if>
						<li${actives.all}><a href="space.jsp?uid=${space.uid}&do=${do}&view=all"><span>大家的记录</span></a></li>
						<li${actives.me}><a href="space.jsp?uid=${space.uid}&do=${do}&view=me"><span>我的记录</span></a></li>
						<li${actives.mood}><a href="space.jsp?uid=${space.uid}&do=mood"><span>同心情的朋友</span></a></li>
					</ul>
				</div>
			</c:when>
			<c:otherwise>
				<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}" />
				<div class="h_status">按照发布时间排序</div>
			</c:otherwise>
		</c:choose>
		<div id="content">
			<c:if test="${space.self}"><jsp:include page="${jch:template(sConfig, sGlobal, 'space_doing_form.jsp')}" /><br></c:if>
			<c:choose>
				<c:when test="${not empty dolist}">
					<div class="doing_list">
						<ol>
							<c:forEach items="${dolist}" var="dv">
								<li id="dl${dv.doid}">
									<div class="avatar48"><a href="space.jsp?uid=${dv.uid}">${jch:avatar1(dv.uid,sGlobal, sConfig)}</a></div>
									<div class="doing">
										<div class="doingcontent">
											<a href="space.jsp?uid=${dv.uid}">${sNames[dv.uid]}</a>: <span>${dv.message}</span>
											<span class="gray">(<jch:date dateformat="MM-dd HH:mm" timestamp="${dv.dateline}" format="1" />)</span>
											<c:if test="${dv.uid==sGlobal.supe_uid}"><a href="cp.jsp?ac=doing&op=delete&doid=${dv.doid}&id=${dv.id}" id="doing_delete_${dv.doid}_${dv.id}" onclick="ajaxmenu(event, this.id)" class="re gray">删除</a>&nbsp;</c:if>
											<a href="javascript:;" onclick="docomment_form(${dv.doid}, 0);">回复</a>
										</div>
										<c:set var="list" value="${clist[dv.doid]}" scope="request"></c:set>
										<c:set var="dv" value="${dv}" scope="request"></c:set>
										<div class="sub_doing" id="docomment_${dv.doid}"${empty list ? " style='display:none;'" : ""}>
											<span id="docomment_form_${dv.doid}_0"></span>
											<jsp:include page="${jch:template(sConfig, sGlobal, 'space_doing_li.jsp')}" />
										</div>
									</div>
								</li>
							</c:forEach>
						</ol>
						<div class="page">${multi}</div>
					</div>
				</c:when>
				<c:otherwise><div class="c_form">现在还没有记录。<c:if test="${space.self}">你可以用一句话记录下这一刻在做什么。</c:if></div></c:otherwise>
			</c:choose>
		</div>
		<div id="sidebar">
			<c:if test="${not empty moodlist}">
				<div class="sidebox">
					<h2 class="title"><p class="r_option"><a href="space.jsp?uid=${space.uid}&do=mood">全部</a></p>跟${space.self ? "我" : sNames[space.uid]}同心情的朋友</h2>
					<ul class="avatar_list">
						<c:forEach items="${moodlist}" var="value">
							<li>
								<div class="avatar48"><a href="space.jsp?uid=${value.uid}&do=doing">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
								<p><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
								<p class="gray"><jch:date dateformat="M月d日" timestamp="${value.updatetime}" format="1"/></p>
							</li>
						</c:forEach>
					</ul>
				</div>
			</c:if>
		</div>
	</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />