<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:if test="${empty TPL.getmore}">
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<div id="content">
<c:choose>
<c:when test="${space.uid>0 && space.self}">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" width="150">
				<div class="ar_r_t"><div class="ar_l_t"><div class="ar_r_b"><div class="ar_l_b">${jch:avatar2(space.uid, 'middle', false, sGlobal, sConfig)}</div></div></div></div>

				<ul class="u_setting">
					<li class="dropmenu" id="usettingli" onclick="showMenu(this.id)"><a href="javascript:;">个人设置 <img src="image/more.gif" align="absmiddle"></a></li>
				</ul>
				<ul id="usettingli_menu" class="dropmenu_drop" style="display:none;">
					<li><a href="cp.jsp?ac=avatar">我的头像</a></li>
					<li><a href="cp.jsp?ac=profile">个人资料</a></li>
					<li><a href="cp.jsp?ac=theme">主页风格</a></li>
					<li><a href="cp.jsp?ac=credit">我的积分</a></li>
					<c:if test="${sConfig.sendmailday>0}">
					<li><a href="cp.jsp?ac=sendmail">邮件提醒</a></li>
					</c:if>
					<li><a href="cp.jsp?ac=privacy">隐私筛选</a></li>
					<c:if test="${jch:checkPerm(pageContext.request, pageContext.response,'admin')}">
					<li><a href="admincp.jsp">站点管理</a></li>
					</c:if>
					<c:if test="${jch:checkPerm(pageContext.request, pageContext.response,'allowstat')}">
					<li><a href="do.jsp?ac=stat">趋势统计</a></li>
					</c:if>
				</ul>
			</td>
			<td valign="top">
				<h3 class="index_name">
					<div class="r_option">${sGlobal.session.magichidden>0 ? "当前隐身" : "当前在线"}
					<c:if test="${not empty globalMagic.invisible}">
					<c:choose>
					<c:when test="${sGlobal.session.magichidden>0}">
							<img src="image/magic/invisible.small.gif" class="magicicon"><a id="a_magic_appear" href="cp.jsp?ac=magic&op=appear" onclick="ajaxmenu(event,this.id)" class="gray">我要在线</a>
							</c:when>
							<c:otherwise>
							<img src="image/magic/invisible.small.gif" alt="${globalMagic.invisible }" class="magicicon"><a id="a_magic_invisible" href="magic.jsp?mid=invisible" onclick="ajaxmenu(event,this.id,1)" class="gray">我要隐身</a>
							</c:otherwise>
							</c:choose>
						</c:if>
					</div>
					<a href="space.jsp?uid=${space.uid }"${gColor}>${sNames[space.uid] }</a>
					${gIcon }
					<a href="cp.jsp?ac=credit">${space.star }</a>
				</h3>
				
				<div class="index_note">
					已有 ${space.viewnum } 人次访问, ${space.credit } 个积分, ${space.experience } 个经验
				</div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_status.jsp')}"/>
			</td>
		</tr>
	</table>

	<c:if test="${space.allnum>0}">
	<div class="mgs_list">
		<c:if test="${space.notenum>0}"><div><img src="image/icon/notice.gif"><a href="space.jsp?do=notice"><strong>${space.notenum }</strong> 条新通知</a></div></c:if>
		<c:if test="${space.addfriendnum>0}"><div><img src="image/icon/friend.gif" alt="" /><a href="cp.jsp?ac=friend&op=request"><strong>${space.addfriendnum}</strong> 个好友请求</a></div></c:if>
		<c:if test="${space.mtaginvitenum>0}"><div><img src="image/icon/mtag.gif" alt="" /><a href="cp.jsp?ac=mtag&op=mtaginvite"><strong>${space.mtaginvitenum}</strong> 个群组邀请</a></div></c:if>
		<c:if test="${space.eventinvitenum>0}"><div><img src="image/icon/event.gif" alt="" /><a href="cp.jsp?ac=event&op=eventinvite"><strong>${space.eventinvitenum}</strong> 个活动邀请</a></div></c:if>
		<c:if test="${space.myinvitenum>0}"><div><img src="image/icon/userapp.gif" alt="" /><a href="space.jsp?do=notice&view=userapp"><strong>${space.myinvitenum}</strong> 个应用消息</a></div></c:if>
		<c:if test="${space.pmnum>0}"><div><img src="image/icon/pm.gif" alt="" /><a href="space.jsp?do=pm"><strong>${space.pmnum}</strong> 条新短消息</a></div></c:if>
		<c:if test="${space.pokenum>0}"><div><img src="image/icon/poke.gif" alt="" /><a href="cp.jsp?ac=poke"><strong> ${space.pokenum}</strong> 个新招呼</a></div></c:if>
		<c:if test="${space.reportnum>0}"><div><img src="image/icon/report.gif" alt="" /><a href="admincp.jsp?ac=report"><strong>${space.reportnum}</strong> 个举报</a></div></c:if>
		<c:if test="${space.namestatusnum>0}"><div><img src="image/icon/profile.gif" alt="" /><a href="admincp.jsp?ac=space&perpage=20&namestatus=0&searchsubmit=1"><strong>${space.namestatusnum}</strong> 个待认证用户</a></div></c:if>
		<c:if test="${space.eventverifynum>0}"><div><img src="image/icon/event.gif" alt="" /><a href="admincp.jsp?ac=event&perpage=20&grade=0&searchsubmit=1"><strong>${space.eventverifynum}</strong> 个待审核活动</a></div></c:if>
	</div>
	</c:if>

	<c:if test="${isnewer && not empty task}">
	<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
		<div class="task_notice">
			<div class="task_notice_body">
				<div class="notice">您好，${sNames[space.uid] }，欢迎加入我们。有新任务等着您，挺简单的，赶快来参加吧</div>
				<img src="${task.image }" alt="" class="icon" />
				<h3><a href="cp.jsp?ac=task&op=do&taskid=${task.taskid }">${task.name }</a></h3>
				<p>可获得 <span class="num">${task.credit }</span> 积分</p>
			</div>
		</div>
	</div></div></div></div><br>
	</c:if>

	<c:if test="${empty sCookie.closefeedbox && not empty globalAd.feedbox}">
	<div id="feed_box" class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
		<div class="task_notice">
			<a title="忽略" class="float_cancel" href="javascript:;" onclick="close_feedbox();">忽略</a>
			<div class="task_notice_body">${jch:showAd(globalAd.feedbox)}</div>
		</div>
	</div></div></div></div>
	</c:if>

	<div class="tabs_header" style="padding-top:10px;">

		<c:if test="${not empty globalMagic.thunder}">
		<a id="a_magic_thunder" href="magic.jsp?mid=thunder" onclick="ajaxmenu(event,this.id, 1)" class="r_option gray"><img src="image/magic/thunder.small.gif" alt="${globalMagic.thunder}" class="magicicon">${globalMagic.thunder}</a>
		</c:if>

		<ul class="tabs">
			<li${actives.all }><a href="space.jsp?do=home&view=all"><span>全部</span></a></li>
			<li${actives.doing }><a href="space.jsp?do=home&view=all&icon=doing"><span>记录</span></a></li>
			<li${actives.album }><a href="space.jsp?do=home&view=all&icon=album"><span>相册</span></a></li>
			<li${actives.blog }><a href="space.jsp?do=home&view=all&icon=blog"><span>日志</span></a></li>
			<li${actives.poll }><a href="space.jsp?do=home&view=all&icon=poll"><span>投票</span></a></li>
			<li${actives.thread }><a href="space.jsp?do=home&view=all&icon=thread"><span>话题</span></a></li>
			<li${actives.event }><a href="space.jsp?do=home&view=all&icon=event"><span>活动</span></a></li>
			<li${actives.share}><a href="space.jsp?do=home&view=all&icon=share"><span>分享</span></a></li>
		</ul>
	</div>
	</c:when>
	
	<c:when test="${space.uid>0}">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}"/>
	</c:when>
	</c:choose>


	<div class="feed">
	<div id="feed_div" class="enter-content">
	
	<c:if test="${not empty hotlist}">
		<a href="space.jsp?do=feed&view=hot" class="r_option">&raquo; 查看更多热点</a>
		<h4 class="feedtime" style="margin-top:5px;">近期热点推荐</h4>
		<ul>
		<c:forEach items="${hotlist}" var="hot">
		<c:set var="feed" value="${hot.value}" scope="request"></c:set>
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_feed_li.jsp')}"/>
		</c:forEach>
		</ul>
	</c:if>
		
</c:if>	

	<c:if test="${not empty list}">
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
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_feed_li.jsp')}"/>
		</c:forEach>
		</ul>
	</c:forEach>
	</c:if>
	<c:if test="${empty list}">
		<ul>
			<li>没有相关动态</li>
		</ul>
	</c:if>
	
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

<div id="sidebar">
	<c:if test="${!isnewer && not empty task}">
	<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
		<div class="task_notice" style="width:230px;">
			<a title="忽略" class="float_cancel" href="cp.jsp?ac=task&taskid=${task.taskid }&op=ignore">忽略</a>
			<div class="task_notice_body">
				<img src="${task.image }" alt="" class="icon" />
				<h3><a href="cp.jsp?ac=task&op=do&taskid=${task.taskid}">${task.name }</a></h3>
				<p>可获得 <span class="num">${task.credit }</span> 积分</p>
			</div>
		</div>
	</div></div></div></div>
	</c:if>
	
	<c:if test="${not empty topiclist}">
	<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
		<div class="task_notice" style="width:230px;">
			<c:forEach items="${topiclist}" var="value">
			<div class="task_notice_body">
				<c:if test="${not empty value.pic}">
				<a href="space.jsp?do=topic&topicid=${value.topicid }"><img src="${value.pic }" alt="" class="icon" /></a>
				</c:if>
				<h3>
					<img src="image/app/topic.gif" align="absmiddle">
					<a href="space.jsp?do=topic&topicid=${value.topicid }">${value.subject }</a>
				</h3>
				<div class="gray">已有 <span class="num">${value.joinnum }</span> 人参与</div>
			</div>
			</c:forEach>
		</div>
	</div></div></div></div>
	</c:if>

	<c:if test="${not empty newspacelist}">
	<div class="sidebox">
		<h2 class="title">
			<p class="r_option">
				<a href="space.jsp?do=top">排行</a>
			</p>
			热烈欢迎新成员
		</h2>
		<ul class="avatar_list">
			<c:forEach items="${newspacelist}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid }">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
				<p<c:if test="${ols[value.uid]>0}"> class="online_icon_p" title="在线"</c:if>><a href="space.jsp?uid=${value.uid }" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
				<p class="gray"><jch:date dateformat="M月d日" timestamp="${value.dateline}" format="1"/></p>
			</li>
			</c:forEach>
		</ul>
	</div>
	</c:if>
	
	<c:if test="${not empty visitorlist}">
	<div class="sidebox">
		<h2 class="title">
			<p class="r_option">
				<a href="space.jsp?uid=${space.uid }&do=friend&view=visitor">全部</a>
			</p>
			最近来访
			<c:if test="${not empty globalMagic.detector}">
			<span class="gray"><img src="image/magic/detector.small.gif" title="${globalMagic.detector }" /><a id="a_magic_detector" href="magic.jsp?mid=detector" onclick="ajaxmenu(event,this.id,1)">${globalMagic.detector }</a></span>
			</c:if>
		</h2>
		<ul class="avatar_list">
			<c:forEach items="${visitorlist}" var="visitor">
			<li>
				<c:if test="${visitor.value.vusername==''}">
				<div class="avatar48"><img src="image/magic/hidden.gif" alt="匿名" /></a></div>
				<p>匿名</p>
				<p class="gray"><jch:date dateformat="M月d日" timestamp="${visitor.value.dateline}" format="1"/></p>
				</c:if>
				<c:if test="${visitor.value.vusername!=''}">
				<div class="avatar48"><a href="space.jsp?uid=${visitor.value.vuid }">${jch:avatar1(visitor.value.vuid,sGlobal, sConfig)}</a></div>
				<p<c:if test="${ols[visitor.value.vuid]>0}"> class="online_icon_p" title="在线"</c:if>><a href="space.jsp?uid=${visitor.value.vuid }" title="${sNames[visitor.value.vuid]}">${sNames[visitor.value.vuid]}</a></p>
				<p class="gray"><jch:date dateformat="M月d日" timestamp="${visitor.value.dateline}" format="1"/></p>
				</c:if>
			</li>
			</c:forEach>
		</ul>
	</div>
	</c:if>

	<c:if test="${not empty olfriendlist}">
	<div class="sidebox">
		<h2 class="title">
			<p class="r_option">
				<a href="space.jsp?uid=${space.uid }&do=friend">全部</a>
			</p>
			我的好友
			<c:if test="${not empty globalMagic.visit}">
			<span class="gray"><img src="image/magic/visit.small.gif" title="${globalMagic.visit }" /><a id="a_magic_visit" href="magic.jsp?mid=visit" onclick="ajaxmenu(event,this.id,1)">${globalMagic.visit }</a></span>
			</c:if>
		</h2>
		<ul class="avatar_list">
			<c:forEach items="${olfriendlist}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid }">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
				<p<c:if test="${ols[value.uid]>0}"> class="online_icon_p" title="在线"</c:if>><a href="space.jsp?uid=${value.uid }" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
				<p class="gray"><c:choose><c:when test="${!jch:jchEmpty(value.lastactivity)}"><jch:date dateformat="HH:mm" timestamp="${value.lastactivity}" format="1"/></c:when><c:otherwise>热度(${value.num})</c:otherwise></c:choose></p>
			</li>
			</c:forEach>
		</ul>
	</div>
	</c:if>

	<c:if test="${not empty birthList}">
	<div class="searchfriend">
		<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
			<h3>好友生日提醒</h3>
			<div class="box">
			<table cellpadding="2" cellspacing="4">
			<c:forEach items="${birthList}" var="birth">
			<tr>
				<td align="right" valign="top" style="padding-left:10px;">
				${ birth.value[0].birth}
				</td>
				<td style="padding-left:10px;">
				<ul>
				<c:forEach items="${birth.value}" var="value">
				<li><a href="space.jsp?uid=${value.uid }">${sNames[value.uid]}</a></li>
				</c:forEach>
				</ul>
				</td>
			</tr>
			</c:forEach>
			</table>
			</div>
		</div></div></div></div>
	</div>
	</c:if>

	<div class="searchfriend">
		<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
			<h3>搜索用户</h3>
			<form method="post" action="cp.jsp" style="padding:10px 0 5px 0;">
				<input name="searchkey" value="" size="20" class="t_input" type="text">
				<input name="searchsubmit" value="找人" class="submit" type="submit">
				<input type="hidden" name="searchmode" value="1" />
				<input type="hidden" name="ac" value="friend" />
				<input type="hidden" name="op" value="search" />
			</form>
			<p>
				<a href="cp.jsp?ac=friend&op=search">高级搜索</a><span class="pipe">|</span>
				<a href="cp.jsp?ac=friend&op=find">可能认识的人</a><span class="pipe">|</span>
				<a href="cp.jsp?ac=invite">邀请好友</a></p>
		</div></div></div></div>
	</div>

</div>
<!--/sidebar-->

<script type="text/javascript">

	var next = ${start};
	function feed_more() {
		var x = new Ajax('XML', 'ajax_wait');
		var html = '';
		next = next + ${perpage};
		x.get('cp.jsp?ac=feed&op=get&start='+next+'&view=all&appid=${param.appid}&icon=${param.icon}&filter=${param.filter}&day=${param.day}', function(s){
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
${my_checkupdate}
${my_showgift}
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>
</c:if>