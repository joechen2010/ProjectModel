<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose><c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
	<div id="space_friend">
		<h3 class="feed_header">
			<a href="cp.jsp?ac=friend&op=search" class="r_option" target="_blank">寻找好友</a>
			好友(共 ${count} 个)
		</h3><br>

		<c:choose>
		<c:when test="${not empty list}">
		<div id="friend_ul">
			<ul class="line_list">
			<c:forEach items="${list}" var="friend">
				<li>
				<table width="100%">
					<tr>
					<td width="70">
						<div class="avatar48"><a href="space.jsp?uid=${friend.value.uid}">${jch:avatar1(friend.value.uid,sGlobal,sConfig)}</a></div>
					</td>
					<td>
					<div class="thumbTitle"><p <c:if test="${not empty ols[friend.value.uid]}"> class="online_icon_p"</c:if>><a href="space.jsp?uid=${friend.value.uid}"${friend.value.gColor}>${sNames[friend.value.uid]}</a> ${friend.value.gIcon}</p></div>
					<c:if test="${not empty friend.value.note}"><div>${friend.value.note}</div></c:if>

					<c:if test="${not empty ols[friend.value.uid]}"><div class="gray"><jch:date dateformat="HH:mm" timestamp="${ols[friend.value.uid]}" format="1"/></div></c:if>
					<div class="setti">

					<c:if test="${!friend.value.isfriend}">
					<a href="cp.jsp?ac=friend&op=add&uid=${friend.value.uid}" id="a_friend_${friend.key}" onclick="ajaxmenu(event, this.id, 1)">加为好友</a>
					</c:if>
					</div>
					</td></tr></table>
				</li>
			</c:forEach>
			</ul>
		</div>
		<div class="page">${multi}</div>

		</c:when>
		<c:otherwise>
		<div class="c_form">
			没有相关用户列表。
		</div>
		</c:otherwise>
		</c:choose>
	</div><br />

</c:when>
<c:otherwise>


<c:choose><c:when test="${space.self}">
<div class="searchbar floatright">
<c:choose>
<c:when test="${param.view=='me'}">
<form method="get" action="space.jsp">
	<input type="hidden" name="do" value="friend">
	<input name="searchkey" value="" size="15" class="t_input" type="text">
	<input name="searchsubmit" value="找好友" class="submit" type="submit">
	<input type="hidden" name="searchmode" value="1" />
</form>
</c:when>
<c:otherwise>
<form method="get" action="cp.jsp">
	<input type="hidden" name="ac" value="friend" />
	<input type="hidden" name="op" value="search" />
	<input name="searchkey" value="" size="15" class="t_input" type="text">
	<input name="searchsubmit" value="找人" class="submit" type="submit">
	<input type="hidden" name="searchmode" value="1" />
</form>
</c:otherwise>
</c:choose>
</div>
<h2 class="title"><img src="image/icon/friend.gif" />好友</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li${actives.me}><a href="space.jsp?uid=${space.uid}&do=friend"><span>好友列表</span></a></li>
		<li><a href="cp.jsp?ac=friend&op=search"><span>查找好友</span></a></li>
		<li><a href="cp.jsp?ac=friend&op=find"><span>可能认识的人</span></a></li>
		<c:if test="${jch:jchEmpty(sConfig.closeinvite)}">
		<li><a href="cp.jsp?ac=invite"><span>邀请好友</span></a></li>
		</c:if>
		<li><a href="cp.jsp?ac=friend&op=request"><span>好友请求</span></a></li>
	</ul>
</div>
<div id="content" style="width: 640px;">

	<div class="c_mgs"><div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
		<c:choose>
		<c:when test="${param.view=='blacklist'}">
			加入到黑名单的用户，将会从您的好友列表中删除。同时，对方将不能进行与您相关的打招呼、踩日志、加好友、评论、留言、短消息等互动行为。
		</c:when>
		<c:when test="${param.view=='me'}">

			当前共有 ${space.friendnum} 个好友。


			<c:if test="${maxfriendnum>0}">
			(最多可以添加 ${maxfriendnum} 个好友)
			<p>
				<c:if test="${!jch:jchEmpty(globalMagic.friendnum)}">
				<img src="image/magic/friendnum.small.gif" class="magicicon" />
				<a id="a_magic_friendnum" href="magic.jsp?mid=friendnum" onclick="ajaxmenu(event, this.id, 1)">我要扩容好友数</a>
				(您可以购买道具“${globalMagic.friendnum}”来扩容，让自己可以添加更多的好友。)
				</c:if>
			</p>
			</c:if>

			<p style="padding-top:10px;">
			好友列表按照好友热度高低排序<br>
			好友热度是系统根据您与好友之间交互的动作自动累计的一个参考值，数值越大，表示您与这位好友互动越频繁。
			</p>
		</c:when>
		<c:when test="${param.view=='online'}">
			<c:choose>
			<c:when test="${param.type=='friend'}">
			这些好友当前在线，赶快去拜访一下吧
			</c:when>
			<c:when test="${param.type=='near'}">
			通过系统匹配，这些朋友就在您附近，您可能认识他们
			</c:when>
			<c:otherwise>
			显示当前全部在线的用户
			</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test="${param.view=='visitor'}">
			他们拜访过您，回访一下吧
		</c:when>
		<c:when test="${param.view=='trace'}">
			您曾经拜访过的用户列表
		</c:when>
		</c:choose>
	</div></div></div></div></div>

	<c:if test="${param.view=='blacklist'}">
	<div class="h_status">
		<h2>添加新黑名单用户</h2>
		<form method="post" name="blackform" action="cp.jsp?ac=friend&op=blacklist&start=${param.start}">
			用户名：<input type="text" name="username" value="" size="15" class="t_input">
			<input type="submit" name="blacklistsubmit_btn" id="moodsubmit_btn" value="添加" class="submit">
			<input type="hidden" name="blacklistsubmit" value="true" />
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</div>
	</c:if>

	<c:choose>
	<c:when test="${not empty list}">
	<div class="thumb_list" id="friend_ul">
		<ul>
		<c:forEach items="${list}" var="friend">
			<li id="friend_${friend.value.uid}_li">
				<c:choose>
				<c:when test="${friend.value.username=='' || friend.value.uid<=0}">
				<div class="avatar48"><img src="image/magic/hidden.gif" alt="匿名" /></div>
				<div class="thumbTitle"><p>匿名</p></div>
				</c:when>
				<c:otherwise>
				<div class="avatar48"><a href="space.jsp?uid=${friend.value.uid}">${jch:avatar1(friend.value.uid,sGlobal,sConfig)}</a></div>
				<div class="thumbTitle">
				<p<c:if test="${not empty ols[friend.value.uid]}"> class="online_icon_p"</c:if>>
					<a href="space.jsp?uid=${friend.value.uid}"${friend.value.gColor}>${sNames[friend.value.uid]}</a> 
					${friend.value.gIcon}
					<c:if test="${!jch:jchEmpty(friend.value.videostatus)}">
					<img src="image/videophoto.gif" align="absmiddle">
					</c:if>
				</p></div>
				<c:if test="${!jch:jchEmpty(friend.value.note)}"><div>${friend.value.note}</div></c:if>
				</c:otherwise>
				</c:choose>

				<c:choose>
				<c:when test="${param.view=='blacklist'}">
					<div class="gray"><a href="cp.jsp?ac=friend&op=blacklist&subop=delete&uid=${friend.value.uid}&start=${param.start}">黑名单除名</a></div>
				</c:when>
				<c:when test="${param.view=='visitor' || param.view=='trace'}">
					<div class="gray"><jch:date dateformat="M月d号" timestamp="${friend.value.dateline}" format="1"/></div>
				</c:when>
				<c:when test="${param.view=='online'}">
					<div class="gray"><jch:date dateformat="HH:mm" timestamp="${ols[friend.value.uid]}" format="1"/></div>
				</c:when>
				<c:otherwise>
					<c:if test="${not empty ols[friend.value.uid]}"><div class="gray"><jch:date dateformat="HH:mm" timestamp="${ols[friend.value.uid]}" format="1"/></div></c:if>
					<div class="gray">
					<c:if test="${friend.value.num>0}"><a href="cp.jsp?ac=friend&op=changenum&uid=${friend.value.uid}" id="friendnum_${friend.value.uid}" onclick="ajaxmenu(event, this.id)">热度(<span id="spannum_${friend.value.uid}">${friend.value.num}</span>)</a><span class="pipe">|</span></c:if>
					<c:choose>
					<c:when test="${!friend.value.isfriend}">
					<a href="cp.jsp?ac=friend&op=add&uid=${friend.value.uid}" id="a_friend_${friend.key}" onclick="ajaxmenu(event, this.id, 1)">加为好友</a>
					</c:when>
					<c:otherwise>
					<a href="cp.jsp?ac=friend&op=changegroup&uid=${friend.value.uid}" id="friend_group_${friend.value.uid}" onclick="ajaxmenu(event, this.id)">分组</a><span class="pipe">|</span>
					<a href="cp.jsp?ac=friend&op=ignore&uid=${friend.value.uid}" id="a_ignore_${friend.key}" onclick="ajaxmenu(event, this.id)">删除</a>
					</c:otherwise>
					</c:choose>
					</div>
				</c:otherwise>
				</c:choose>
			</li>
		</c:forEach>
		</ul>
	</div>
	<div class="page">${multi}</div>

	</c:when>
	<c:otherwise>
	<div class="c_form">
		没有相关用户列表。
	</div>
	</c:otherwise>
	</c:choose>

</div>

<div id="sidebar" style="width: 150px;">
	<!-- 屏蔽应用-->
	<div class="cat">
		<h3>
			好友菜单
		</h3>
		<ul>
			<li${a_actives.me}><a href="space.jsp?uid=${space.uid}&do=friend">全部好友列表</a></li>
			<li${a_actives.onlinefriend}><a href="space.jsp?uid=${space.uid}&do=friend&view=online&type=friend">当前在线的好友</a></li>
			<li${a_actives.onlinenear}><a href="space.jsp?uid=${space.uid}&do=friend&view=online&type=near">就在我附近的朋友</a></li>
			<li${a_actives.visitor}><a href="space.jsp?uid=${space.uid}&do=friend&view=visitor">我的访客</a></li>
			<li${a_actives.trace}><a href="space.jsp?uid=${space.uid}&do=friend&view=trace">我的足迹</a></li>
			<li${a_actives.blacklist}><a href="space.jsp?uid=${space.uid}&do=friend&view=blacklist">我的黑名单</a></li>
		</ul>
	</div>

	<c:if test="${not empty groups}">
	<div class="cat">
		<h3>
			<span class="r_option"><a href="cp.jsp?ac=friend&op=group">批量分组</a></span>
			好友分组
		</h3>
		<ul class="post_list line_list">
			<li><a href="space.jsp?do=friend&group=-1">全部好友</a></li>
			<c:forEach items="${groups}" var="group">
			<li${groupselect[group.key]}>
				<a href="cp.jsp?ac=friend&op=groupignore&group=${group.key}" id="c_ignore_${group.key}" onclick="ajaxmenu(event, this.id)" title="屏蔽用户组动态" class="c_delete">屏蔽</a>
				<c:if test="${group.key>0}">
				<a href="cp.jsp?ac=friend&op=groupname&group=${group.key}" id="c_edit_${group.key}" onclick="ajaxmenu(event, this.id, 1)" title="编辑用户组名" class="c_edit">编辑</a>
				</c:if>
				<c:if test="${!empty space.privacy.filter_gid[group.key]}"><span class="gray">[屏蔽]</span></c:if> <a href="space.jsp?do=friend&group=${group.key}"><span id="friend_groupname_${group.key}">${group.value}</span></a>
			</li>
			</c:forEach>
		</ul>
	</div>
	</c:if>

</div>


</c:when>
<c:otherwise>
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}"/>
	<div class="h_status">共有 ${space.friendnum} 个好友</div>
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_list.jsp')}"/>

</c:otherwise>
</c:choose>

</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>