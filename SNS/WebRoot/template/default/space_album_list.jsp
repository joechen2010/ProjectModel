<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${sGlobal.inajax>0}">
		<div id="space_photo">
			<h3 class="feed_header"><a href="cp.jsp?ac=upload" class="r_option" target="_blank">上传图片</a> 相册(共 ${count} 个)</h3>
			<c:choose>
				<c:when test="${count>0}">
					<table cellspacing="4" cellpadding="4" width="100%">
						<tr>
							<c:forEach items="${list}" var="value" varStatus="key">
								<td width="85" align="center"><a href="space.jsp?uid=${space.uid}&do=album&id=${value.albumid}" target="_blank"><img src="${value.pic}" alt="${value.albumname}" width="70" /></a></td>
								<td width="165">
									<h6><a href="space.jsp?uid=${space.uid}&do=album&id=${value.albumid}" target="_blank" title="${value.albumname}">${value.albumname}</a></h6>
									<p class="gray">${value.picnum} 张照片</p>
									<p class="gray">更新于<jch:date dateformat="MM-dd" timestamp="${value.dateline}" format="1" /></p>
								</td>
								${key.index%2 == 1 ? "</tr><tr>" : ""}
							</c:forEach>
						</tr>
					</table>
					<c:if test="${pricount>0}"><div class="c_form">本页有 ${pricount} 个相册因作者的隐私设置而隐藏</div></c:if>
					<div class="page">${multi}</div>
				</c:when>
				<c:otherwise><div class="c_form">没有可浏览的列表。</div></c:otherwise>
			</c:choose>
		</div>
		<br>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${space.self}">
				<div class="searchbar floatright">
					<form method="get" action="space.jsp">
						<input name="searchkey" value="" size="15" class="t_input" type="text">
						<input name="searchsubmit" value="搜索相册" class="submit" type="submit">
						<input type="hidden" name="do" value="album" />
						<input type="hidden" name="view" value="all" />
						<input type="hidden" name="orderby" value="dateline" />
					</form>
				</div>
				<h2 class="title"><img src="image/app/album.gif">相册</h2>
				<div class="tabs_header">
					<ul class="tabs">
						<c:if test="${space.friendnum>0}"><li${actives.we}><a href="space.jsp?uid=${space.uid}&do=${do}&view=we"><span>好友最新相册</span></a></li></c:if>
						<li${actives.all}><a href="space.jsp?uid=${space.uid}&do=${do}&view=all"><span>大家的相册</span></a></li>
						<li${actives.me}><a href="space.jsp?uid=${space.uid}&do=${do}&view=me"><span>我的相册</span></a></li>
						<li${actives.click}><a href="space.jsp?uid=${space.uid}&do=${do}&view=click"><span>我表态过的图片</span></a></li>
						<li class="null"><a href="cp.jsp?ac=upload">上传新图片</a></li>
					</ul>
				</div>
			</c:when>
			<c:otherwise><jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}" /></c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${param.view=='me'}"><div></c:when>
			<c:otherwise><div id="content" style="width: 640px;"></c:otherwise>
		</c:choose>
		<c:if test="${not empty param.orderby && param.orderby!='dateline'}">
			<div class="h_status">
				排行时间范围：
				<a href="space.jsp?do=album&view=all&orderby=${param.orderby}"${day_actives['0']}>全部</a><span class="pipe">|</span>
				<a href="space.jsp?do=album&view=all&orderby=${param.orderby}&day=1"${day_actives['1']}>最近一天</a><span class="pipe">|</span>
				<a href="space.jsp?do=album&view=all&orderby=${param.orderby}&day=2"${day_actives[2]}>最近两天</a><span class="pipe">|</span>
				<a href="space.jsp?do=album&view=all&orderby=${param.orderby}&day=7"${day_actives['7']}>最近七天</a><span class="pipe">|</span>
				<a href="space.jsp?do=album&view=all&orderby=${param.orderby}&day=30"${day_actives['30']}>最近三十天</a><span class="pipe">|</span>
				<a href="space.jsp?do=album&view=all&orderby=${param.orderby}&day=90"${day_actives['90']}>最近三个月</a><span class="pipe">|</span>
				<a href="space.jsp?do=album&view=all&orderby=${param.orderby}&day=120"${day_actives['120']}>最近六个月</a>
			</div>
		</c:if>
		<c:if test="${space.self && param.view=='me'}"><div class="h_status">下面列出的是你自行创建相册列表。<br>你在日志、话题等里面上传的图片附件，全部存放在<a href="space.jsp?uid=${space.uid}&do=album&id=-1">系统默认相册</a>里面。</div></c:if>
		<c:choose>
			<c:when test="${picmode}">
				<c:if test="${pricount>0}"><div class="h_status">本页有 ${pricount} 张图片因作者的隐私设置而隐藏</div></c:if>
				<c:choose>
					<c:when test="${count>0}">
						<table cellspacing="0" cellpadding="0" width="100%" class="pic_list">
							<tr>
								<c:forEach items="${list}" var="value" varStatus="key">
									<td align="center">
										<div class="pic_bg"><a href="space.jsp?uid=${value.uid}&do=album&picid=${value.picid}"><img src="${value.pic}" alt="" /></a></div>
										<a href="space.jsp?uid=${value.uid}&do=${do}&id=${value.albumid}">${value.albumname}</a><br>
										<a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a>
									</td>
									${key.index%4==3 ? "</tr><tr>" : ""}
								</c:forEach>
							</tr>
						</table>
						<div class="page">${multi}</div>
					</c:when>
					<c:otherwise><div class="c_form">没有可浏览的列表。</div></c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:if test="${not empty searchkey}"><div class="h_status">以下是搜索相册 <span style="color: red; font-weight: bold;">${searchkey}</span> 结果列表</div></c:if>
				<c:if test="${pricount>0}"><div class="h_status">提示：本页有 ${pricount} 个相册因作者的隐私设置而隐藏</div></c:if>
				<c:choose>
					<c:when test="${count>0}">
						<table class="album_list" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<c:forEach items="${list}" var="value" varStatus="key">
									<td style="padding-bottom: 20px;">
										<div class="album_bg"><a href="space.jsp?uid=${value.uid}&do=${do}&id=${value.albumid}"><img src="${value.pic}" alt="${value.albumname}" /></a></div>
										<p><a href="space.jsp?uid=${value.uid}&do=${do}&id=${value.albumid}">${not empty value.albumname ? value.albumname : '默认相册'}</a> <c:if test="${value.picnum>0}"><span class="gray">(${value.picnum})</span></c:if></p>
										<c:if test="${param.view!='me'}"><p><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></p></c:if>
										<c:if test="${value.uid==sGlobal.supe_uid}"><p class="gray"><a href="cp.jsp?ac=album&op=editpic&albumid=${value.albumid}">管理相册</a><span class="pipe">|</span><a href="cp.jsp?ac=upload&albumid=${value.albumid}">上传图片</a></p></c:if>
										<c:if test="${param.view!='me'}"><p class="gray">更新: <jch:date dateformat="MM-dd HH:mm" timestamp="${value.updatetime}" format="1" /></p></c:if>
									</td>
									${key.index%4==3 ? "</tr><tr>" : ""}
								</c:forEach>
							</tr>
						</table>
						<div class="page">${multi}</div>
					</c:when>
					<c:otherwise><div class="c_form">没有可浏览的列表。</div></c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
		</div>
		<c:if test="${param.view!='me'}">
			<div id="sidebar" style="width: 150px;">
				<c:if test="${not empty userlist}">
					<div class="cat">
						<h3>按好友查看</h3>
						<ul class="post_list line_list">
							<li>
								选择好友:<br>
								<select name="fuidsel" onchange="fuidgoto(this.value);">
									<option value="">全部好友</option>
									<c:forEach items="${userlist}" var="value">
										<option value="${value.fuid}"${fuid_actives[value.fuid]}>${sNames[value.fuid]}</option>
									</c:forEach>
								</select>
							</li>
						</ul>
					</div>
				</c:if>
				<c:choose>
					<c:when test="${param.view=='click'}">
						<div class="cat">
							<h3>表态动作</h3>
							<ul class="post_list line_list">
								<li${click_actives.all}><a href="space.jsp?do=album&view=click">全部动作</a></li>
								<c:forEach items="${clicks}" var="click">
									<li${click_actives[click.value.clickid]}><a href="space.jsp?do=album&view=click&clickid=${click.value.clickid}">${click.value.name}</a></li>
								</c:forEach>
							</ul>
						</div>
					</c:when>
					<c:when test="${param.view=='all'}">
						<div class="cat">
							<h3>排行榜</h3>
							<ul class="post_list line_list">
								<li${all_actives.all}><a href="space.jsp?do=album&view=all">热图推荐</a></li>
								<li${all_actives.dateline}><a href="space.jsp?do=album&view=all&orderby=dateline">最新更新</a></li>
								<li${all_actives.hot}><a href="space.jsp?do=album&view=all&orderby=hot&day=${param.hotday}">人气排行</a></li>
								<c:forEach items="${clicks}" var="click">
									<c:set var="clickid" value="click_${click.value.clickid}"></c:set>
									<li${all_actives[clickid]}><a href="space.jsp?do=album&view=all&orderby=${clickid}&day=${param.hotday}">${click.value.name}排行</a></li>
								</c:forEach>
							</ul>
						</div>
					</c:when>
				</c:choose>
			</div>
		</c:if>
		<script>
			function fuidgoto(fuid) {
				window.location.href = "space.jsp?do=album&view=we&fuid="+fuid;
			}
		</script>
	</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />