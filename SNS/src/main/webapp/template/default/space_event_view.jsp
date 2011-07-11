<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

	<h2 class="title">
		<c:if test="${!jch:jchEmpty(sGlobal.refer)}">
		<div class="r_option">
			<a  href="${sGlobal.refer}">&laquo; 返回上一页</a>
		</div>
		</c:if>
		<img src="image/app/event.gif"/> <a href="space.jsp?do=event">活动</a> - <a href="space.jsp?do=event&id=${event.eventid}">${event.title}</a>
		<c:choose>
		<c:when test="${event.grade==-2}"><span style="color:#f00"> 已关闭</span></c:when>
		<c:when test="${event.grade<=0}"><span style="color:#f00"> 待审核</span></c:when>
		</c:choose>
	</h2>
	<div class="tabs_header">
		<a href="cp.jsp?ac=share&type=event&id=${eventid}" id="a_share" onclick="ajaxmenu(event, this.id, 1)" class="a_share">分享</a>
		<div class="r_option">
			<c:if test="${sGlobal.supe_uid==event.uid || manageevent}">
			<a href="cp.jsp?ac=topic&op=join&id=${event.eventid}&idtype=eventid" id="a_topicjoin_${event.eventid}" onclick="ajaxmenu(event, this.id)">凑热闹</a><span class="pipe">|</span>
			</c:if>
			<c:if test="${manageevent}">
			<a href="admincp.jsp?ac=event&eventid=${event.eventid}" target="_blank">审核管理</a><span class="pipe">|</span>
			<a href="cp.jsp?ac=event&id=${event.eventid}&op=edithot" id="a_hot_${event.eventid}" onclick="ajaxmenu(event, this.id)">热度</a><span class="pipe">|</span>
			</c:if>
			<a href="cp.jsp?ac=common&op=report&idtype=eventid&id=${event.eventid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">举报</a><span class="pipe">|</span>
		</div>

		<ul class="tabs">
			<li ${menu.all}><a href="space.jsp?do=event&id=${event.eventid}"><span>活动</span></a></li>
            <li ${menu.member}><a href="space.jsp?do=event&id=${event.eventid}&view=member&status=2"><span>成员</span></a></li>
			<li ${menu.pic}><a href="space.jsp?do=event&id=${event.eventid}&view=pic"><span>照片</span></a></li>
			<c:if test="${event.tagid>0}">
			<li ${menu.thread}><a href="space.jsp?do=event&id=${event.eventid}&view=thread"><span>话题</span></a></li>
			</c:if>
			<li ${menu.comment}><a href="space.jsp?do=event&id=${event.eventid}&view=comment"><span>留言</span></a></li>
		</ul>
	</div>

<c:choose>
<c:when test="${view=='member'}">

	<div class="sub_menu">
		<div style="width:790px;">
			<form name="searchform" id="searchform" method="get" action="space.jsp" style=" float: right;">
				<table cellspacing="0" cellpadding="0">
					<tbody>
					<tr>
					<td style="padding: 4px 0 0 0;"><input type="text" style="border-right: medium none; width: 160px;" tabindex="1" class="t_input" onfocus="if(this.value=='搜索成员')this.value='';" value="搜索成员" name="key" id="key"/></td>
					<td style="padding: 4px 0 0 0;"><a href="javascript:$('searchform').submit();" style="padding:0; margin:0;"><img alt="搜索" src="image/search_btn.gif"/></a></td>
					</tr>
					</tbody>
				</table>
				<input type="hidden" name="do" value="event">
				<input type="hidden" name="id" value="${eventid}">
				<input type="hidden" name="view" value="member">
				<input type="hidden" name="status" value="${status}">
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" />
			</form>
			<a ${submenus.member} href="space.jsp?do=event&id=${event.eventid}&view=member&status=2">${event.membernum} 人参加</a>
			<a ${submenus.follow} href="space.jsp?do=event&id=${event.eventid}&view=member&status=1">${event.follownum} 人关注</a><c:if test="${sGlobal.supe_userevent.status >= 3}">
			<a ${submenus.verify} href="space.jsp?do=event&id=${event.eventid}&view=member&status=0">${verifynum} 人待审核</a></c:if>
		</div>
	</div>

	<div class="thumb_list">
		<c:choose>
		<c:when test="${not empty members}">
		<table cellspacing="6" cellpadding="0">
		<c:forEach items="${members}" var="value" varStatus="key">
			<tr>
				<td class="thumb <c:if test="${!jch:jchEmpty(ols[value.uid])}">online</c:if>">
					<table cellpadding="4" cellspacing="4">
						<tr>
							<td class="image">
							<div class="ar_r_t"><div class="ar_l_t"><div class="ar_r_b"><div class="ar_l_b">
							<a href="space.jsp?uid=${value.uid}">${jch:avatar2(value.uid, 'big', false, sGlobal, sConfig)}</a>
							</div></div></div></div>
							</td>
							<td>
								<h6>
									<a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a>
									<c:if test="${!jch:jchEmpty(ols[value.uid])}">
									<span class="gray online_icon_p" title="当前在线" style="font-size:12px; font-weight:normal;">(<jch:date dateformat="M月d日" timestamp="${ols[value.uid]}" format="1"/>)</span>
									</c:if>
								</h6>
								<p class="l_status">
								<c:if test="${value.status!=1 && sGlobal.supe_userevent.status>=3 && event.uid!=value.uid}">
									<a href="cp.jsp?ac=event&id=${eventid}&op=member&uid=${value.uid}" id="a_mod_${key.index}" onclick="ajaxmenu(event, this.id)" class="r_option" style="padding-left:0.5em;">管理该成员</a>
								</c:if>
									<span class="r_option gray" id="mtag_member_${value.uid}">
									<c:choose>
									<c:when test="${value.status==4}">发起人</c:when>
									<c:when test="${value.status==3}">组织者</c:when>
									<c:when test="${value.status==2}">成员</c:when>
									<c:when test="${value.status==0}">待审核</c:when>
									</c:choose>
									</span>
									<a href="cp.jsp?ac=friend&op=add&uid=${value.uid}" id="a_friend_${key.index}" onclick="ajaxmenu(event, this.id, 1)">加为好友</a>
									<span class="pipe">|</span><a href="cp.jsp?ac=pm&uid=${value.uid}" id="a_pm_${key.index}" onclick="ajaxmenu(event, this.id, 1)">发短消息</a>
									<span class="pipe">|</span><a href="cp.jsp?ac=poke&op=send&uid=${value.uid}" id="a_poke_${key.index}" onclick="ajaxmenu(event, this.id, 1)">打招呼</a>
								</p>
								<p><span class="gray">性别：</span><c:choose><c:when test="${value.sex==2}">美女</c:when><c:when test="${value.sex==1}">帅哥</c:when><c:otherwise>保密</c:otherwise></c:choose></p>
								<c:if test="${!jch:jchEmpty(value.resideprovince) || !jch:jchEmpty(value.residecity)}">
								<p><span class="gray">地区：</span><a href="cp.jsp?ac=friend&op=search&resideprovince=${value.resideprovince}&residecity=&searchmode=1">${value.resideprovince}</a> <a href="cp.jsp?ac=friend&op=search&resideprovince=${value.resideprovince}&residecity=${value.residecity}&searchmode=1">${value.residecity}</a></p>
								</c:if>
								<c:if test="${!jch:jchEmpty(value.note)}">
								<p><span class="gray">状态：</span>${value.note}</p>
								</c:if>
								<c:if test="${!jch:jchEmpty(value.fellow)}">
								<p><span class="gray">携带好友数：</span>${value.fellow}</p>
								</c:if>
								<c:if test="${!jch:jchEmpty(event.template) && sGlobal.supe_userevent.status>=3}">
								<p>${value.template}</p>
								</c:if>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>
		<div class="c_form">没有找到可浏览的成员信息。</div>
		</c:otherwise>
		</c:choose>
	</div>

	<div class="page">${multi}</div>

</c:when>
<c:when test="${view=='pic'}">
	<c:choose>
	<c:when test="${picid>0}">
		<jsp:include page="${jch:template(sConfig, sGlobal, 'space_pic.jsp')}"/>
	</c:when>
	<c:otherwise>
		<div class="h_status">
			<div class="r_option">
			<c:if test="${sGlobal.supe_userevent.status>=3}">
				<a href="cp.jsp?ac=event&op=pic&id=${eventid}">照片管理</a>
			</c:if>
			<c:if test="${event.grade>0 && ((!jch:jchEmpty(event.allowpic) && sGlobal.supe_userevent.status>1) || sGlobal.supe_userevent.status>=3)}">
			<a href="cp.jsp?ac=upload&eventid=${eventid}" class="submit">上传新照片</a>
			</c:if>
			</div>
			<span>活动相册 - 共 ${piccount} 张照片</span>
		</div>

		<c:choose>
		<c:when test="${not empty photolist}">
		<table cellspacing="6" cellpadding="0" width="100%" class="photo_list">
			<tr>
			<c:forEach items="${photolist}" var="value" varStatus="key">
				<td>
					<a title="${value.title}" href="space.jsp?do=event&id=${eventid}&view=pic&picid=${value.picid}">
						<img alt="${value.title}" src="${value.pic}"/>
					</a>
					<p>
						<span class="gray">来自</span> <a href="space.jsp?uid=${value.uid}" style="display:inline;width:auto;height:auto;">${sNames[value.uid]}</a>
					</p>
				</td>
				<c:if test="${key.index%4==3}"></tr><tr></c:if>
			</c:forEach>
			</tr>
		</table>
		<div class="page">${multi}</div>
		</c:when>
		<c:otherwise>
		<div class="c_form">
			<p style="text-align: center">还没有活动照片<c:if test="${event.grade>0 && ((!jch:jchEmpty(event.allowpic) && sGlobal.supe_userevent.status>1) || sGlobal.supe_userevent.status>=3)}">，我来<a href="cp.jsp?ac=upload&eventid=${eventid}">上传</a></c:if></p>
		</div>
		</c:otherwise>
		</c:choose>
	</c:otherwise>
	</c:choose>
</c:when>
<c:when test="${view=='thread'}">
	<div class="m_box">
		<h2 class="h_status">
			<div class="r_option">
				<c:if test="${sGlobal.supe_userevent.status >= 3}">
				<a href="cp.jsp?ac=event&op=thread&id=${eventid}">话题管理</a>
				</c:if>
				<c:if test="${event.grade>0 && sGlobal.supe_userevent.status >= 2}">
				<a href="cp.jsp?ac=thread&tagid=${event.tagid}&eventid=${eventid}" class="submit">发表新话题</a>
				</c:if>
			</div>
			话题
		</h2>
		<c:choose>
		<c:when test="${not empty threadlist}">
		<div class="topic_list">
			<table cellspacing="0" cellpadding="0">
				<thead>
					<tr>
						<td class="subject">主题</td>
						<td class="author">作者 (回应/阅读)</td>
						<td class="lastpost">最后更新</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${threadlist}" var="value" varStatus="key">
					<tr <c:if test="${key.index%2==1}">class="alt"</c:if>>
						<td class="subject">
						${value.magiceggImage}
						<a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}&eventid=${eventid}" <c:if test="${value.magiccolor>0}">class="magiccolor${value.magiccolor}"</c:if>>${value.subject}</a>
						</td>
						<td class="author"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a><br><em>${value.replynum}/${value.viewnum}</em></td>
						<td class="lastpost"><a href="space.jsp?uid=${value.lastauthorid}" title="${sNames[value.lastauthorid]}">${sNames[value.lastauthorid]}</a><br><jch:date dateformat="MM-dd HH:mm" timestamp="${value.lastpost}" format="1"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
		<div class="page">${multi}</div>
		</c:when>
		<c:otherwise>
		<div class="c_form" style="text-align:center;">还没有相关话题。
			<c:if test="${event.grade>0 && sGlobal.supe_userevent.status >= 2}">
			<a href="cp.jsp?ac=thread&tagid=${event.tagid}&eventid=${eventid}">我来发表</a></c:if>
		</div>
		</c:otherwise>
		</c:choose>
	</div>

</c:when>
<c:when test="${view=='comment'}">

	<div class="m_box">
		<div class="h_status">
			<span>活动留言</span>
		</div>
		<c:choose>
		<c:when test="${event.grade>0 && (!jch:jchEmpty(event.allowpost) || sGlobal.supe_userevent.status>1)}">
		<div class="space_wall_post">
			<form action="cp.jsp?ac=comment" id="commentform_${space.uid}" name="commentform_${space.uid}" method="post">
				<a href="###" id="message_face" onclick="showFace(this.id, 'comment_message');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
				<c:if test="${not empty globalMagic.doodle}">
				<a id="a_magic_doodle" href="magic.jsp?mid=doodle&showid=comment_doodle&target=comment_message" onclick="ajaxmenu(event, this.id, 1)"><img src="image/magic/doodle.small.gif" class="magicicon" />涂鸦板</a>
				</c:if>
				<br />
				<textarea name="message" id="comment_message" rows="5" cols="60" onkeydown="ctrlEnter(event, 'commentsubmit_btn');"></textarea>
				<input type="hidden" name="refer" value="space.jsp?do=event&id=${eventid}" />
				<input type="hidden" name="id" value="${eventid}" />
				<input type="hidden" name="idtype" value="eventid" />
				<input type="hidden" name="commentsubmit" value="true" />
				<br>
				<input type="button" id="commentsubmit_btn" name="commentsubmit_btn" class="submit" value="留言" onclick="ajaxpost('commentform_${space.uid}', 'wall_add')" />
				<span id="__commentform_${space.uid}"></span>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" />
			</form>
		</div>
		<br>
		</c:when>
		<c:when test="${event.grade>0}">
		<textarea name="message" id="comment_message" rows="5" cols="60" readonly="">只有活动成员才能发布留言</textarea>
		</c:when>
		</c:choose>
		<c:if test="${cid>0}">
		<div class="notice">
			当前只显示与你操作相关的单个评论，<a href="space.jsp?do=event&id=${eventid}&view=comment">点击此处查看全部评论</a>
		</div>
		</c:if>
		<div class="page">${multi}</div>
		<div class="comments_list" id="comment">
			<input type="hidden" value="1" name="comment_prepend" id="comment_prepend" />
			<ul id="comment_ul">
			<c:forEach items="${comments}" var="value">
			<c:set var="comment" value="${value}" scope="request"></c:set>
				<jsp:include page="${jch:template(sConfig, sGlobal, 'space_comment_li.jsp')}"/>
			</c:forEach>
			</ul>
		</div>
		<div class="page">${multi}</div>
	</div>
	
	<script type="text/javascript">
	var elems = selector('div[class~=magicflicker]'); 
	for(var i=0; i<elems.length; i++){
		magicColor(elems[i]);
	}
	</script>
</c:when>
<c:otherwise>

	<div id="content">
		<div class="m_box">
			<div class="event">

				<div class="event_icon">
						<a href="${event.pic}" target="_blank"><img class="poster_pre" src="${event.pic}" alt="${event.title}" onerror="this.src='${globalEventClass[event.classid].poster}'"></a>
				</div>
				<div class="event_content">
					<dl>
						<dt class="gray">发起者:</dt>
						<dd><a href="space.jsp?uid=${event.uid}">${sNames[event.uid]}</a></dd>
						<dt class="gray">活动类型:</dt>
						<dd>${globalEventClass[event.classid].classname}</dd>
						<dt class="gray">活动地点:</dt>
						<dd>${event.province} ${event.city} ${event.location}</dd>
						<dt class="gray">活动时间:</dt>
						<dd><jch:date dateformat="MM月dd日 HH:mm" timestamp="${event.starttime}"/> - <jch:date dateformat="MM月dd日 HH:mm" timestamp="${event.endtime}"/></dd>
						<dt class="gray">截止报名:</dt>
						<dd>
							<c:choose>
							<c:when test="${event.deadline>=sGlobal.timestamp}"><jch:date dateformat="MM月dd日 HH:mm" timestamp="${event.deadline}"/></c:when>
							<c:otherwise>报名结束</c:otherwise>
							</c:choose>
						</dd>
						<dt class="gray">活动人数:</dt>
						<dd><c:choose><c:when test="${event.limitnum>0}">${event.limitnum} （还剩 ${event.limitnum-event.membernum} 个名额）</c:when><c:otherwise>不限</c:otherwise></c:choose></dd>
						<dt class="gray">需要审核:</dt>
						<dd><c:if test="${event.verify==0}">不</c:if>需要</dd>
					</dl>
					<ul>
						<li>${event.viewnum} 次查看</li>
						<li>${event.membernum} 人参加</li>
						<li>${event.follownum} 人关注</li>
						<c:if test="${verifynum>0 && sGlobal.supe_userevent.status>=3}"><li><b><a href="cp.jsp?ac=event&id=${eventid}&op=members&status=0">${verifynum}</a></b> 人待审核</li></c:if>
					</ul>
					<p class="event_state">
					<c:if test="${event.hot>0}"><span class="hot"><em>热</em>${event.hot}</span></c:if>
					<c:choose>
					<c:when test="${not empty sGlobal.supe_userevent && sGlobal.supe_userevent.status==0 && sGlobal.timestamp<event.endtime}">
						您的报名正在审核中
					</c:when>
					<c:when test="${sGlobal.supe_userevent.status==1}">
						您关注了此活动
					</c:when>
					<c:when test="${sGlobal.supe_userevent.status>=2}">
						您参加了此活动
					</c:when>
					</c:choose>
					<c:choose>
					<c:when test="${event.starttime>sGlobal.timestamp}">
						<c:choose>
						<c:when test="${countdown>0}">
						距活动开始还有 ${countdown} 天
						</c:when>
						<c:otherwise>
						活动今天开始
						</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${event.starttime<=sGlobal.timestamp && event.endtime>=sGlobal.timestamp}">
						此活动正在进行中
					</c:when>
					<c:otherwise>
						活动已经结束
					</c:otherwise>
					</c:choose>
					</p>
					<c:if test="${event.grade>0 && sGlobal.timestamp<=event.endtime}">
					<ul class="buttons">
						<c:choose>
						<c:when test="${empty sGlobal.supe_userevent && event.deadline>sGlobal.timestamp}">
							<c:if test="${event.limitnum==0 || event.membernum<event.limitnum}"><li><a id="a_join" href="cp.jsp?ac=event&op=join&id=${eventid}" onclick="ajaxmenu(event, this.id)" class="do_event_button">我要参加</a></li></c:if>
							<li><a id="a_follow" href="cp.jsp?ac=event&op=follow&id=${eventid}" onclick="ajaxmenu(event, this.id)" class="wish_event_button">我要关注</a></li>
						</c:when>
						<c:when test="${not empty sGlobal.supe_userevent && sGlobal.supe_userevent.status == 0}">
							<c:if test="${event.deadline>sGlobal.timestamp && (!jch:jchEmpty(event.template) || !jch:jchEmpty(event.allowfellow))}"><li><a id="a_join" href="cp.jsp?ac=event&id=${eventid}&op=join" onclick="ajaxmenu(event, this.id)" class="cancel_event_button">修改报名信息</a></li></c:if>
							<li><a id="a_quit" href="cp.jsp?ac=event&id=${eventid}&op=quit" onclick="ajaxmenu(event, this.id)" class="do_cancel_event_button">不参加了</a></li>
						</c:when>
						<c:when test="${sGlobal.supe_userevent.status==1}">
							<c:if test="${event.deadline>sGlobal.timestamp && (event.limitnum==0 || event.membernum<event.limitnum)}"><li><a id="a_join" href="cp.jsp?ac=event&op=join&id=${eventid}" onclick="ajaxmenu(event, this.id)" class="do_event_button">我要参加</a></li></c:if>
							<li><a id="a_cancelfollow" href="cp.jsp?ac=event&id=${eventid}&op=cancelfollow" onclick="ajaxmenu(event, this.id)" class="wish_cancel_event_button">取消关注</a></li>
						</c:when>
						<c:when test="${sGlobal.supe_userevent.status>1}">
							<c:if test="${event.grade>0 && sGlobal.timestamp<=event.deadline && (event.limitnum <= 0 || event.membernum < event.limitnum) && (sGlobal.supe_userevent.status >= 3 || event.allowinvite>0)}"><li><a href="cp.jsp?ac=event&id=${eventid}&op=invite" class="recs_event_button">邀请好友</a></li></c:if>
							<c:if test="${sGlobal.supe_uid!=event.uid}"><li><a id="a_quit" href="cp.jsp?ac=event&id=${eventid}&op=quit" onclick="ajaxmenu(event, this.id)" class="do_cancel_event_button">不参加了</a></li></c:if>
						</c:when>
						</c:choose>
					</ul>
					</c:if>
				</div>
			</div>
		</div>

		<div class="m_box">
			<h3 class="feed_header">活动介绍</h3>
			<div class="event_article">
				${event.detail}
			</div>
		</div>

		<div class="m_box">
			<h3 class="feed_header">
			<div class="r_option"><a href="space.jsp?do=event&id=${eventid}&view=member&status=2">全部</a></div>
			活动成员
			</h3>
			<c:choose>
			<c:when test="${not empty members}">
			<ul class="avatar_list">
				<c:forEach items="${members}" var="userevent">
				<li>
					<div class="avatar48"><a title="${sNames[userevent.uid]}" href="space.jsp?uid=${userevent.uid}">${jch:avatar1(userevent.uid,sGlobal, sConfig)}</a></div>
					<p>
						<a href="space.jsp?uid=${userevent.uid}">${sNames[userevent.uid]}</a>
					</p>
					<c:if test="${event.allowfellow>0}">
					<p><c:if test="${userevent.fellow>0}">携带 ${userevent.fellow} 人</c:if></p>
					</c:if>
				</li>
				</c:forEach>
			</ul>
			</c:when>
			<c:otherwise>
			<p style="text-align:center;">还没有活动成员。
			<c:if test="${event.grade>0 && sGlobal.timestamp<=event.deadline && (event.limitnum <= 0 || event.membernum < event.limitnum) && (sGlobal.supe_userevent.status >= 3 || (event.allowinvite>0 && sGlobal.supe_userevent.status==2))}">
			<a href="cp.jsp?ac=event&id=${eventid}&op=invite">邀请好友参加</a>
			</c:if>
			</p>
			</c:otherwise>
			</c:choose>
		</div>

		<div class="m_box">
			<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
			<h2 class="atitle">
			<div class="r_option">
				<c:if test="${event.grade>0 && ((event.allowpic>0 && sGlobal.supe_userevent.status>1) || sGlobal.supe_userevent.status>=3)}">
				<a href="cp.jsp?ac=upload&eventid=${eventid}">上传</a> | </c:if>
				<a href="space.jsp?do=event&id=${eventid}&view=pic">全部</a>
			</div>
			相册<c:if test="${event.picnum>0}"> <span style="font-weight:normal">(共 ${event.picnum} 张照片)</span></c:if>
			</h2>
			<c:choose>
			<c:when test="${not empty photolist}">
			<ul class="albs2">
				<c:forEach items="${photolist}" var="value">
				<li>
					<a title="${value.title}" href="space.jsp?do=event&id=${eventid}&view=pic&picid=${value.picid}">
					<img style="width: 75px; height: 75px;" alt="${value.title}" src="${value.pic}"/></a>
					<p style="text-align:left;">
						<span class="gray">来自</span> <a href="space.jsp?uid=${value.uid}" style="display:inline;width:auto;height:auto;">${sNames[value.uid]}</a>
					</p>
				</li>
				</c:forEach>
			</ul>
			</c:when>
			<c:otherwise>
			<p class="event_albs_p">还没有活动照片。<c:if test="${event.grade>0 && ((event.allowpic>0 && sGlobal.supe_userevent.status>1) || sGlobal.supe_userevent.status>=3)}"><a href="cp.jsp?ac=upload&eventid=${eventid}">我来上传</a> </c:if></p>
			</c:otherwise>
			</c:choose>
			</div></div></div></div>
		</div>

		<c:if test="${event.tagid>0}">
		<div class="m_box">
			<h2 class="feed_header">
				<div class="r_option">
					<c:if test="${event.grade>0 && sGlobal.supe_userevent.status>=2}">
					<a href="cp.jsp?ac=thread&tagid=${event.tagid}&eventid=${eventid}">发表</a> | </c:if>
					<a href="space.jsp?do=event&id=${eventid}&view=thread">全部</a>
				</div>
				话题<c:if test="${event.threadnum>0}"><span style="font-weight:normal">(共 ${event.threadnum} 个话题)</span></c:if>
			</h2>
			<c:choose>
			<c:when test="${not empty threadlist}">
			<div class="topic_list">
				<table cellspacing="0" cellpadding="0">
					<thead>
						<tr>
							<td class="subject">主题</td>
							<td class="author">作者 (回应/阅读)</td>
							<td class="lastpost">最后更新</td>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${threadlist}" var="value" varStatus="key">
						<tr <c:if test="${key.index%2==1}">class="alt"</c:if>>
							<td class="subject">
							<a href="space.jsp?uid=${value.uid}&do=thread&id=${value.tid}&eventid=${eventid}">${value.subject}</a>
							</td>
							<td class="author"><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a><br><em>${value.replynum}/${value.viewnum}</em></td>
							<td class="lastpost"><a href="space.jsp?uid=${value.lastauthorid}" title="${sNames[value.lastauthorid]}">${sNames[value.lastauthorid]}</a><br><jch:date dateformat="MM-dd HH:mm" timestamp="${value.lastpost}" format="1"/></td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			</c:when>
			<c:otherwise>
			<div class="c_form" style="text-align:center;">还没有相关话题。
				<c:if test="${event.grade>0 && sGlobal.supe_userevent.status>=2}">
				<a href="cp.jsp?ac=thread&tagid=${event.tagid}&eventid=${eventid}">我来发表</a></c:if>
			</div>
			</c:otherwise>
			</c:choose>
		</div>
		</c:if>

		<div class="m_box">
			<h2 class="feed_header">
				<div class="r_option">
					<a href="space.jsp?do=event&id=${eventid}&view=comment">全部</a>
				</div>
				留言
			</h2>
			<c:choose>
			<c:when test="${event.grade>0 && (event.allowpost>0 || sGlobal.supe_userevent.status > 1)}">
			<div class="space_wall_post">
				<form action="cp.jsp?ac=comment" id="commentform_${space.uid}" name="commentform_${space.uid}" method="post">
					<a href="###" id="message_face" onclick="showFace(this.id, 'comment_message');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
					<c:if test="${not empty globalMagic.doodle}">
					<a id="a_magic_doodle" href="magic.jsp?mid=doodle&showid=comment_doodle&target=comment_message" onclick="ajaxmenu(event, this.id, 1)"><img src="image/magic/doodle.small.gif" class="magicicon" />涂鸦板</a>
					</c:if>
					<br />
					<textarea name="message" id="comment_message" rows="5" cols="60" onkeydown="ctrlEnter(event, 'commentsubmit_btn');"></textarea>
					<input type="hidden" name="refer" value="space.jsp?do=event&id=${eventid}" />
					<input type="hidden" name="id" value="${eventid}" />
					<input type="hidden" name="idtype" value="eventid" />
					<input type="hidden" name="commentsubmit" value="true" />
					<br>
					<input type="button" id="commentsubmit_btn" name="commentsubmit_btn" class="submit" value="留言" onclick="ajaxpost('commentform_${space.uid}', 'wall_add')" />
					<span id="__commentform_${space.uid}"></span>
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" />
				</form>
			</div>
			<br>
			</c:when>
			<c:when test="${event.grade>0}">
			<textarea name="message" id="comment_message" rows="5" cols="60" readonly="">只有活动成员才能发布留言</textarea>
			</c:when>
			</c:choose>

			<div class="comments_list" id="comment">
				<input type="hidden" value="1" name="comment_prepend" id="comment_prepend" />
				<ul id="comment_ul">
				
				<c:forEach items="${comments}" var="value">
				<c:set var="comment" value="${value}" scope="request"></c:set>
				<jsp:include page="${jch:template(sConfig, sGlobal, 'space_comment_li.jsp')}"/>
				</c:forEach>
				</ul>
				<c:if test="${not empty comments && fn:length(comments)>=20}">
				<p><a href="space.jsp?do=event&id=${eventid}&view=comment&page=2" style="float:right;">浏览更多留言</a></p>
				</c:if>
			</div>
		</div>

	</div>

	<div id="sidebar">

		<c:if test="${not empty topic}">
		<div class="affiche">
			<img src="image/app/topic.gif" align="absmiddle">
			<strong>凑个热闹</strong>：<a href="space.jsp?do=topic&topicid=${topic.topicid}">${topic.subject}</a>
		</div>
		</c:if>

		<c:if test="${sGlobal.supe_userevent.status > 1}">
		<div class="sidebox">
			<h2 class="title">活动菜单</h2>
			<ul class="menu_list">
				<c:if test="${sGlobal.supe_userevent.status >= 3}">
					<c:if test="${event.grade>0 || event.grade==-2}">
				<li><a href="cp.jsp?ac=event&id=${eventid}&op=members">成员管理</a></li>
					</c:if>
				<li><a href="cp.jsp?ac=event&id=${eventid}&op=edit">活动管理</a></li>
					<c:if test="${event.grade>0 || event.grade==-2}">
				<li><a href="cp.jsp?ac=event&id=${eventid}&op=pic">相册管理</a></li>
						<c:if test="${event.tagid>0}">
				<li><a href="cp.jsp?ac=event&id=${eventid}&op=thread">话题管理</a></li>
						</c:if>
					</c:if>
				</c:if>
				
				<c:if test="${event.grade>0 && sGlobal.timestamp<= event.deadline && (event.limitnum <= 0 || event.membernum < event.limitnum) && (sGlobal.supe_userevent.status >= 3 || event.allowinvite>0)}">
				<li><a href="cp.jsp?ac=event&id=${eventid}&op=invite">邀请好友</a></li>
				</c:if>
				<c:if test="${event.grade>0 && sGlobal.timestamp<= event.deadline && (!jch:jchEmpty(event.template) || event.allowfellow>0)}">
				<li><a id="a_join" href="cp.jsp?ac=event&id=${eventid}&op=join" onclick="ajaxmenu(event, this.id)">报名信息</a></li>
				</c:if>
				<c:if test="${sGlobal.supe_userevent.status>= 3 && event.endtime >= sGlobal.timestamp}">
				<li><a id="a_print" href="cp.jsp?ac=event&id=${eventid}&op=print" onclick="ajaxmenu(event, this.id)">打签到表</a></li>
				</c:if>
				<c:choose>
				<c:when test="${sGlobal.supe_userevent.uid==event.uid}">
					<c:if test="${event.grade>0 && sGlobal.timestamp>event.endtime}">
				<li><a id="a_close" onclick="ajaxmenu(event, this.id)" href="cp.jsp?ac=event&id=${eventid}&op=close">关闭活动</a></li>
					</c:if>
					<c:if test="${event.grade==-2 && sGlobal.timestamp>event.endtime}">
				<li><a id="a_open" onclick="ajaxmenu(event, this.id)" href="cp.jsp?ac=event&id=${eventid}&op=open">开启活动</a></li>
					</c:if>
				<li><a id="a_delete" onclick="ajaxmenu(event, this.id)" href="cp.jsp?ac=event&id=${eventid}&op=delete">取消活动</a></li>
				</c:when>
				<c:when test="${event.endtime>sGlobal.timestamp}">
				<li><a id="a_quit2" onclick="ajaxmenu(event, this.id)" href="cp.jsp?ac=event&id=${eventid}&op=quit">退出活动</a></li>
				</c:when>
				</c:choose>
			</ul>
		</div>
		</c:if>

		<div class="sidebox">
			<h2 class="title">
			组织者
			</h2>
			<ul class="avatar_list">
				<c:forEach items="${admins}" var="userevent">
				<li>
					<div class="avatar48"><a title="${sNames[userevent.uid]}" href="space.jsp?uid=${userevent.uid}">${jch:avatar1(userevent.uid,sGlobal, sConfig)}</a></div>
					<p><a href="space.jsp?uid=${userevent.uid}">${sNames[userevent.uid]}</a></p>
				</li>
				</c:forEach>
			</ul>
		</div>

		<c:if test="${not empty follows}">
		<div class="sidebox">
			<h2 class="title">
				<p class="r_option">
					<a href="space.jsp?do=event&id=${eventid}&view=member&status=1">全部</a>
				</p>
				关注的人
			</h2>
			<ul class="avatar_list">
				<c:forEach items="${follows}" var="userevent">
				<li>
					<div class="avatar48"><a title="${sNames[userevent.uid]}" href="space.jsp?uid=${userevent.uid}">${jch:avatar1(userevent.uid,sGlobal, sConfig)}</a></div>
					<p><a href="space.jsp?uid=${userevent.uid}">${sNames[userevent.uid]}</a></p>
				</li>
				</c:forEach>
			</ul>
		</div>
		</c:if>

		<c:if test="${not empty relatedevents}">
		<div class="sidebox">
			<h2 class="title">
			参加这个活动的人也参加了
			</h2>
			<ul class="attention">
			<c:forEach items="${relatedevents}" var="relatedEvent">
			<li>
				<p>
					<a target="_blank" href="space.jsp?do=event&id=${relatedEvent.value.eventid}">${relatedEvent.value.title}</a>
				</p>
				<p class="gray" style="text-align: left">
					<c:choose>
					<c:when test="${sGlobal.timestamp>relatedEvent.value.endtime}">
						已结束
					</c:when>
					<c:otherwise>
					<jch:date dateformat="M月d日" timestamp="${relatedEvent.value.starttime }"/>
					</c:otherwise>
					</c:choose>&nbsp;
					${relatedEvent.value.membernum} 人参加 /
					${relatedEvent.value.follownum} 人关注
				</p>
				<p>
					<c:if test="${relatedEvent.value.threadnum>0}">
					<a href="space.jsp?do=event&id=${relatedEvent.value.eventid}&view=thread">
						${relatedEvent.value.threadnum} 个话题
					</a> &nbsp;
					</c:if>
					<c:if test="${relatedEvent.value.picnum>0}">
					<a href="space.jsp?do=event&id=${relatedEvent.value.eventid}&view=pic">
						${relatedEvent.value.picnum} 张照片
					</a>
					</c:if>
				</p>
			</li>
			</c:forEach>
			</ul>
		</div>
		</c:if>
	</div>
	
	<script type="text/javascript">
	var elems = selector('div[class~=magicflicker]'); 
	for(var i=0; i<elems.length; i++){
		magicColor(elems[i]);
	}	
	</script>
</c:otherwise>
</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>