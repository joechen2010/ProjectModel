<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:if test="${empty TPL.getmore}">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
	<c:choose>
			<c:when test="${space.uid>0 && space.self}">
			<h2 class="title"><img src="image/app/feed.gif">动态</h2>
				<div class="tabs_header">
					<ul class="tabs">
						<c:if test="${space.friendnum>0}">
							<li${actives.we }><a href="space.jsp?do=feed&view=we"><span>好友动态</span> </a></li>
						</c:if>
						<li${actives.hot }><a href="space.jsp?do=feed&view=hot"><span>热门推荐</span> </a></li>
						<li${actives.me }><a href="space.jsp?do=feed&view=me"><span>我的动态</span> </a></li>
						<li${actives.all }><a href="space.jsp?do=feed&view=all"><span>全站动态</span> </a></li>
					</ul>
				</div>
			</c:when>
			<c:when test="${space.uid>0}">
				<jsp:include
					page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}" />
			</c:when>
		</c:choose>
	<div id="content">
		<div class="feed">
			<div id="feed_div" class="enter-content">
				<c:if test="${not empty hotlist}">
					<a href="space.jsp?do=feed&view=hot" class="r_option">&raquo;
						查看更多热点</a>
					<h4 class="feedtime" style="margin-top: 5px;">
						近期热点推荐
					</h4>
					<ul>
						<c:forEach items="${hotlist}" var="hot">
							<c:set var="feed" value="${hot.value}" scope="request"></c:set>
							<jsp:include
								page="${jch:template(sConfig, sGlobal, 'space_feed_li.jsp')}" />
						</c:forEach>
					</ul>
				</c:if>
</c:if>
<c:choose>
	<c:when test="${not empty list}">
		<c:forEach items="${list}" var="values">
			<c:if test="${param.view!='hot'}">
				<h4 class="feedtime">
					<c:choose>
						<c:when test="${values.key=='yesterday'}">昨天</c:when>
						<c:when test="${values.key=='today'}">今天</c:when>
						<c:when test="${values.key=='app'}">看看大家都在玩什么</c:when>
						<c:otherwise>${values.key}</c:otherwise>
					</c:choose>
				</h4>
			</c:if>
			<ul>
				<c:forEach items="${values.value}" var="feed">
					<c:set var="feed" value="${feed}" scope="request"></c:set>
					<jsp:include
						page="${jch:template(sConfig, sGlobal, 'space_feed_li.jsp')}" />
				</c:forEach>
			</ul>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<ul>
			<li>
				没有相关动态
			</li>
		</ul>
	</c:otherwise>
</c:choose>

<c:if test="${filtercount>0}">
	<div class="notice" id="feed_filter_notice_${start}">
		根据您的<a href="cp.jsp?ac=privacy&op=view">筛选设置</a>，有 ${filtercount } 条动态被屏蔽 (<a href="javascript:;" onclick="filter_more(${start});" id="a_feed_privacy_more">点击查看</a>)
	</div>
	<div id="feed_filter_div_${start}" class="enter-content" style="display:none;">
		<h4 class="feedtime">以下是被屏蔽的动态</h4>
		<ul>
		<c:forEach items="${filter_list}" var="value">
		<c:set var="feed" value="${value}" scope="request"></c:set>
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_feed_li.jsp')}"/>
		</c:forEach>
		<li><a href="javascript:;" onclick="filter_more(${start});">&laquo; 收起</a></li>
		</ul>
	</div>
	</c:if>
	
<c:if test="${empty TPL.getmore}">
	</div>

	<c:if test="${count==perpage}">
	<div class="page" style="padding-top:20px;">
		<a href="javascript:;" onclick="feed_more();" id="a_feed_more">查看更多动态</a>
	</div>
	</c:if>

	<div id="ajax_wait"></div>

	</div>
</div>
<!--/content-->
<div id="sidebar" style="width: 150px;">
	<div class="cat">
		<h3>
			动态分类菜单
		</h3>
		<ul>
			<li${feedIcon_actives.all}><a href="space.jsp?do=feed&uid=${space.uid}&view=${param.view}">全部</a>
			<c:forEach items="${icons}" var="icon">
			<li${feedIcon_actives[icon.key]}><a href="space.jsp?do=feed&uid=${space.uid}&view=${param.view}&icon=${icon.key}">${icon.value}</a></li>
			</c:forEach>
		</ul>
	</div>
</div>
<script type="text/javascript">

	var next = ${start};
	function feed_more() {
		var x = new Ajax('XML', 'ajax_wait');
		var html = '';
		next = next + ${perpage};
		x.get('cp.jsp?ac=feed&op=get&start='+next+'&view=${param.view}&appid=${param.appid}&icon=${param.icon}&filter=${param.filter}&day=${param.day}', function(s){
			html = '<h4 class="feedtime">以下是新读取的动态</h4>' + s;
			$('feed_div').innerHTML += html;
		});
	}

	function filter_more(id) {
		if($('feed_filter_div_'+id).style.display == '') {
			$('feed_filter_div_'+id).style.display = 'none';
			$('feed_filter_notice_'+id).style.display = '';
		} else {
			$('feed_filter_div_'+id).style.display = '';
			$('feed_filter_notice_'+id).style.display = 'none';
		}
	}

	function close_feedbox() {
		var x = new Ajax();
		x.get('cp.jsp?ac=common&op=closefeedbox', function(s){
			$('feed_box').style.display = 'none';
		});
	}
	
	var elems = selector('li[class~=magicthunder]', $('feed_div')); 
	for(var i=0; i<elems.length; i++){		
		magicColor(elems[i]); 
	}
</script>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>
</c:if>