<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:set var="tpl_noSideBar" value="1" scope="request" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<div id="network">
	<script>
		function setintro(type) {
			var intro = '';
			var bPosition = '';
			if(type == 'doing') {
				intro = '用一句话记录自己生活中的点点滴滴';
				bPosition = '0';
			} else if(type == 'mtag') {
				intro = '创建自己的小圈子，与大家交流感兴趣的话题';
				bPosition = '175px';
			} else if(type == 'pic') {
				intro = '上传照片，分享生活中的精彩瞬间';
				bPosition = '55px';
			} else if(type == 'app') {
				intro = '与好友一起玩转游戏和游戏，增加好友感情';
				bPosition = '118px';
			} else {
				intro = '马上注册，与好友分享日志、照片，一起玩转游戏';
				bPosition = '0';
			}
			$('guest_intro').innerHTML = intro + '......';
			$('guest_intro').style.backgroundPosition = bPosition + ' 100%'
		}
		function scrollPic(e, LN, Width, Price, Speed) {
			id = e.id;
			if(LN == 'Last'){ scrollNum = Width; } else if(LN == 'Next'){ scrollNum = 0 - Width; }
			scrollStart = parseInt(e.style.marginLeft, 10);
			scrollEnd = parseInt(e.style.marginLeft, 10) + scrollNum;
			
			MaxIndex = (e.getElementsByTagName('li').length / Price).toFixed(0);
			sPicMaxScroll = 0 - Width * MaxIndex;
		
			if(scrollStart == 0 && scrollEnd == Width){
				scrollEnd = -1806;
				e.style.marginLeft = parseInt(e.style.marginLeft, 10) - Speed + 'px';
			} else if(scrollStart == sPicMaxScroll + Width && scrollEnd == sPicMaxScroll){
				scrollEnd = 0;
				e.style.marginLeft = parseInt(e.style.marginLeft, 10) + Speed + 'px';
			}
			scrollShowPic = setInterval(scrollShow, 1);
			
			function scrollShow() {
				if(scrollStart > scrollEnd) {
					if(parseInt(e.style.marginLeft, 10) > scrollEnd) {
						$(id + '_last').onclick = function(){ return false; };
						$(id + '_next').onclick = function(){ return false; };
						e.style.marginLeft = parseInt(e.style.marginLeft, 10) - Speed + 'px';
					} else {
						clearInterval(scrollShowPic);
						$(id + '_last').onclick = function(){ scrollPic(e, 'Last', Width, Price, Speed);return false; };
						$(id + '_next').onclick = function(){ scrollPic(e, 'Next', Width, Price, Speed);return false; };
					}
				} else {
					if(parseInt(e.style.marginLeft, 10) < scrollEnd) {
						$(id + '_last').onclick = function(){ return false; };
						$(id + '_next').onclick = function(){ return false; };
						e.style.marginLeft = parseInt(e.style.marginLeft, 10) + Speed + 'px';
					} else {
						clearInterval(scrollShowPic);
						$(id + '_last').onclick = function(){ scrollPic(e, 'Last', Width, Price, Speed);return false; };
						$(id + '_next').onclick = function(){ scrollPic(e, 'Next', Width, Price, Speed);return false; };
					}					
				}
			}
		}
		function scrollShowNav(e, Width, Price, Speed) {
			id = e.id;
			$(id + '_last').onclick = function(){ scrollPic(e, 'Last', Width, Price, Speed);return false; };
			$(id + '_next').onclick = function(){ scrollPic(e, 'Next', Width, Price, Speed);return false; };
			
		}
		function getUserTip(obj) {
			var tipBox = $('usertip_box');
			tipBox.childNodes[0].innerHTML = '<strong>' + obj.rel + ':<\/strong> ' + obj.rev + '...';
			
			var showLeft;
			if(obj.parentNode.offsetLeft > 730) {
				showLeft = $('showuser').offsetLeft + obj.parentNode.offsetLeft - 148;
				tipBox.childNodes[0].style.right = 0;
			} else {
				tipBox.childNodes[0].style.right = 'auto';
				showLeft = $('showuser').offsetLeft + obj.parentNode.offsetLeft;
			}
			tipBox.style.left = showLeft + 'px';
			
			var showTop; 
			if(obj.className == 'uonline') {
				showTop = $('showuser').offsetTop + obj.parentNode.offsetTop - tipBox.childNodes[0].clientHeight;
			} else {
				showTop = $('showuser').offsetTop + obj.parentNode.offsetTop + 48;
			}
			tipBox.style.top = showTop + 'px';
			
			tipBox.style.visibility = 'visible';
		}
	</script>
	<c:if test="${sGlobal.supe_uid == 0}">
		<div id="guestbar" class="nbox">
			<div class="nbox_c">
				<p id="guest_intro">马上注册，与好友分享日志、照片，一起玩转游戏......</p>
				<a href="do.jsp?ac=${sConfig.register_action}" id="regbutton" onmouseover="setintro('register');">马上注册</a>
				<p id="guest_app">
					<a href="javascript:;" class="appdoing" onmouseover="setintro('doing');">记录</a>
					<a href="javascript:;" class="appphotos" onmouseover="setintro('pic');">照片</a>
					<a href="javascript:;" class="appgames" onmouseover="setintro('app');">游戏</a>
					<a href="javascript:;" class="appgroups" onmouseover="setintro('mtag');">群组</a>
				</p>
			</div>
			<div class="nbox_s side_rbox" id="nlogin_box">
				<h3 class="ntitle">请登录</h3>
				<div class="side_rbox_c">
					<form name="loginform" action="do.jsp?ac=${sConfig.login_action}${empty url_plus ? '' : '&'}${url_plus}&ref" method="post">
						<p><label for="username">用户名</label> <input type="text" name="username" id="username" class="t_input" value="${memberName}" /></p>
						<p><label for="password">密　码</label> <input type="password" name="password" id="password" class="t_input" value="${password}" /></p>
						<p class="checkrow"><input type="checkbox" id="cookietime" name="cookietime" value="315360000"${cookieCheck} style="margin-bottom: -2px;" /><label for="cookietime">下次自动登录</label></p>
						<p class="submitrow">
							<input type="hidden" name="refer" value="space.jsp?do=home" />
							<input type="submit" id="loginsubmit" name="loginsubmit" value="登录" class="submit" />
							<a href="do.jsp?ac=lostpasswd">忘记密码?</a>
							<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
						</p>
					</form>
				</div>
			</div>
		</div>
	</c:if>

	<div class="nbox">
		<div class="nbox_c">
			<h2 class="ntitle"><span class="r_option"><a href="space.jsp?do=blog&view=all">更多日志</a></span><img src="image/app/blog.gif" style="vertical-align:middle">&nbsp;日志 &raquo;</h2>
			<ul class="bloglist">
				<c:forEach items="${blogs}" var="blog" varStatus="index">
					<li${index.index % 2 == 1 ? " class='list_r'" : ""}>
						<h3><a href="space.jsp?uid=${blog.uid}&do=blog&id=${blog.blogid}" target="_blank">${blog.subject}</a></h3>
						<div class="d_avatar avatar48"><a href="space.jsp?uid=${blog.uid}" title="${sNames[blog.uid]}" target="_blank">${jch:avatar1(blog.uid,sGlobal,sConfig)}</a></div>
						<p class="message">${blog.message} ...</p>
						<p class="nhot"><a href="space.jsp?uid=${blog.uid}&do=blog&id=${blog.blogid}">${blog.hot} 人推荐</a></p>
						<p class="gray"><a href="space.jsp?uid=${blog.uid}">${sNames[blog.uid]}</a> 发表于 ${blog.dateline}</p>
					</li>
				</c:forEach>
			</ul>
		</div>
		<div class="nbox_s side_rbox side_rbox_w">
			<h2 class="ntitle"><span class="r_option"><a href="space.jsp?do=doing&view=all">更多记录</a></span><img src="image/app/doing.gif" style="vertical-align:middle">&nbsp;记录 &raquo;</h2>
			<div class="side_rbox_c">
				<ul class="side_rbox_c doinglist">
					<c:forEach items="${doings}" var="doing">
						<li>
							<p><a href="space.jsp?uid=${doing.uid}&do=doing&doid=${doing.doid}" target="_blank" class="gray r_option dot" style="margin: 0; background-position-y: 0;">${doing.dateline}</a> <a href="space.jsp?uid=${doing.uid}" title="${sNames[doing.uid]}" class="s_avatar">${jch:avatar1(doing.uid,sGlobal,sConfig)}</a> <a href="space.jsp?uid=${doing.uid}">${sNames[doing.uid]}</a></p>
							<p class="message" title="${doing.title}">${doing.message}</p>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
	<div class="nbox" id="photolist">
		<h2 class="ntitle"><a href="space.jsp?do=album&view=all" class="r_option">更多图片</a><img src="image/app/album.gif" style="vertical-align:middle">&nbsp;图片 &raquo;</h2>
		<div id="p_control">
			<a href="javascript:;" id="spics_last">上一页</a>
			<a href="javascript:;" id="spics_next">下一页</a>
			<ul id="p_control_pages"><li>第一页</li><li>第二页</li><li>第三页</li><li>第四页</li></ul>
		</div>
		<div id="spics_wrap">
			<ul id="spics" style="margin-left: 0;">
				<c:forEach items="${pics}" var="pic" varStatus="index">
					<li class="spic_${index.index}">
						<div class="spic_img"><a href="space.jsp?uid=${pic.uid}&do=album&picid=${pic.picid}" target="_blank"><strong>${pic.hot}</strong><img src="${pic.filepath}" alt="${pic.albumname}" /></a></div>
						<p><a href="space.jsp?uid=${pic.uid}">${sNames[pic.uid]}</a></p>
						<p>${pic.dateline}</p>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
	<script type="text/javascript">
		scrollShowNav($('spics'), 903, 7, 43);
	</script>
	<div id="searchbar" class="nbox s_clear">
		<div class="floatleft">
			<form method="get" action="cp.jsp">
				<input name="searchkey" value="" size="15" class="t_input" type="text" style="padding: 5px;">
				<input name="searchsubmit" value="找人" class="submit" type="submit"> &nbsp; 查找：<a href="cp.jsp?ac=friend&op=search&view=sex" target="_blank">男女朋友</a><span class="pipe">|</span>
				<a href="cp.jsp?ac=friend&op=search&view=reside" target="_blank">同城</a><span class="pipe">|</span>
				<a href="cp.jsp?ac=friend&op=search&view=birth" target="_blank">老乡</a><span class="pipe">|</span>
				<a href="cp.jsp?ac=friend&op=search&view=birthyear" target="_blank">同年同月同日生</a><span class="pipe">|</span>
				<a href="cp.jsp?ac=friend&op=search&view=edu" target="_blank">同学</a><span class="pipe">|</span>
				<a href="cp.jsp?ac=friend&op=search&view=work" target="_blank">同事</a><span class="pipe">|</span>
				<a href="space.jsp?do=top&view=online" target="_blank">在线会员(${onlineCount})</a>
				<input type="hidden" name="searchmode" value="1" />
				<input type="hidden" name="ac" value="friend" />
				<input type="hidden" name="op" value="search" />
			</form>
		</div>
		<div class="floatright">
			<form method="get" action="space.jsp">
				<input name="searchkey" value="" size="15" class="t_input" type="text" style="padding: 5px;">
				<select name="do">
					<option value="blog">日志</option>
					<option value="album">相册</option>
					<option value="thread">话题</option>
					<option value="poll">投票</option>
					<option value="event">活动</option>
				</select>
				<input name="searchsubmit" value="搜索" class="submit" type="submit">
				<input type="hidden" name="view" value="all" />
				<input type="hidden" name="orderby" value="dateline" />
			</form>
		</div>
	</div>
	<div id="showuser" class="nbox">
		<div id="user_recomm">
			<h2>站长推荐</h2>
			<c:if test="${!empty star}">
				<div class="s_avatar"><a href="space.jsp?uid=${star.uid}" target="_blank">${jch:avatar2(star.uid,'middle',false,sGlobal,sConfig)}</a></div>
				<div class="s_cnts">
					<h3><a href="space.jsp?uid=${star.uid}" title="${sNames[star.uid]}">${sNames[star.uid]}</a></h3>
					<p>访问: ${star.viewnum}</p>
					<p>积分: ${star.credit}</p>
					<hr />
					<p>好友: ${star.friendnum}</p>
					<p>更新: ${star.updatetime}</p>
				</div>
			</c:if>
		</div>
		<div id="user_wall" onmouseout="javascript:$('usertip_box').style.visibility = 'hidden';">
			<div id="user_pay" class="s_clear">
				<h2><a href="space.jsp?do=top">竞价排名</a></h2>
				<ul>
					<c:forEach items="${shows}" var="show">
						<li><a href="space.jsp?uid=${show.uid}" target="_blank" rel="${sNames[show.uid]}" rev="${show.note}" onmouseover="getUserTip(this)">${jch:avatar1(show.uid,sGlobal,sConfig)}</a></li>
					</c:forEach>
				</ul>
				<p><a href="space.jsp?do=top">我要上榜</a></p>
			</div>
			<div id="user_online" class="s_clear">
				<h2><a href="space.jsp?do=top&view=online">在线会员</a></h2>
				<ul>
					<c:forEach items="${onlines}" var="online">
						<li><a href="space.jsp?uid=${online.uid}" target="_blank" rel="${sNames[online.uid]}" rev="${online.note}" class="uonline" onmouseover="getUserTip(this)">${jch:avatar1(online.uid,sGlobal,sConfig)}</a></li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
	<div id="usertip_box"><div></div></div>
	<div class="nbox">
		<div class="nbox_c">
			<h2 class="ntitle"><span class="r_option"><a href="space.jsp?do=thread&view=all">更多话题</a></span><img src="image/app/mtag.gif" style="vertical-align:middle">&nbsp;话题 &raquo;</h2>
			<div class="tlist">
				<table cellpadding="0" cellspacing="1">
					<tbody>
						<c:forEach items="${threads}" var="thread" varStatus="index">
							<tr${index.index % 2 == 1 ? " class='color_row'" : ""}>
								<td class="ttopic"><div class="ttop"><div><span>${thread.hot}</span></div></div><a href="space.jsp?uid=${thread.uid}&do=thread&id=${thread.tid}" target="_blank">${thread.subject}</a></td>
								<td class="tuser"><a href="space.jsp?uid=${thread.uid}" target="_blank">${jch:avatar1(thread.uid,sGlobal,sConfig)}</a> <a href="space.jsp?uid=${thread.uid}" target="_blank">${sNames[thread.uid]}</a></td>
								<td class="tgp"><a href="space.jsp?do=mtag&tagid=${thread.tagid}">${thread.tagname}</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div id="npoll" class="nbox_s side_rbox side_rbox_w">
			<div class="side_rbox_c">
				<h2 class="ntitle"><span class="r_option"><a href="space.jsp?do=poll">更多投票</a></span><img src="image/app/poll.gif" style="vertical-align:middle">&nbsp;投票 &raquo;</h2>
				<ul>
					<c:forEach items="${polls}" var="poll" varStatus="index">
						<li class="poll_${index.index}"><a href="space.jsp?uid=${poll.uid}&do=poll&pid=${poll.pid}" target="_blank">${poll.subject}</a><c:if test="${index.index == 0}"><p><a href="space.jsp?uid=${poll.uid}&do=poll&pid=${poll.pid}">已有 ${poll.voternum} 位会员投票</a></p></c:if></li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
	<c:if test="${myAppCount>0}">
		<div class="nbox">
			<div class="nbox_c" style="border: none;">
				<ul class="applist">
					<c:forEach items="${myApps}" var="myApp">
						<li>
							<p class="aimg"><a href="userapp.jsp?id=${myApp.appid}" target="_blank"><img src="http://appicon.jsprun.com/logos/${myApp.appid}" alt="${myApp.appname}" /></a></p>
							<p><a href="userapp.jsp?id=${myApp.appid}" target="_blank">${myApp.appname}</a></p>
						</li>
					</c:forEach>
				</ul>
			</div>
			<div class="susb">
				<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b"><div class="appmo">
					<p>共有 <span>${myAppCount}</span> 个应用</p>
					<p class="appmobutton"><a href="cp.jsp?ac=userapp&my_suffix=%2Fapp%2Flist">查看更多应用</a></p>
				</div></div></div></div></div>
			</div>
		</div>
	</c:if>
	<div class="nbox">
		<div class="nbox_c_revision">
			<h2 class="ntitle">
				<span class="r_option"><a href="space.jsp?do=event&view=recommend">更多活动</a></span><img src="image/app/event.gif" style="vertical-align:middle">&nbsp;活动 &raquo;
				<c:forEach items="${globalEventClass}" var="eventClass">
				&nbsp; <a href="space.jsp?do=event&view=all&type=going&classid=${eventClass.value.classid}">${eventClass.value.classname}</a>
				</c:forEach>
			</h2>
			<ul class="elist">
				<c:forEach items="${events}" var="event">
					<li>
						<h3><a href="space.jsp?do=event&id=${event.eventid}" target="_blank">${event.title}</a></h3>
						<p class="eimage"><a href="space.jsp?do=event&id=${event.eventid}" target="_blank"><img src="${event.poster}"/></a></p>
						<p><span class="gray">时间:</span> ${event.starttime} - ${event.endtime}</p>
						<p><span class="gray">地点:</span> ${event.province} ${event.city} ${event.location}</p>
						<p><span class="gray">发起:</span> <a href="space.jsp?uid=${event.uid}">${sNames[event.uid]}</a></p>
						<p class="egz">${event.membernum} 人参加<span class="pipe">|</span>${event.follownum} 人关注</p>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
	
	
	<div class="nbox">
		<div class="nbox_c_revision">
			<h2 class="ntitle">
				<span class="r_option"><a href="space.jsp?do=share&view=all">更多分享</a> </span><img src="image/app/share.gif" style="vertical-align:middle">&nbsp;分享 &raquo;
				<c:forEach items="${shareClasses}" var="shareClass">
				&nbsp; <a href="space.jsp?do=share&view=all&type=${shareClass.key}">${shareClass.value}</a>
				</c:forEach>
			</h2>
			<ul class="slist">
				<c:forEach items="${shares}" var="share"><li>
					<div class="title">
						<a href="space.jsp?uid=${share.uid}">${sNames[share.uid]}</a>
						<a href="space.jsp?uid=${share.uid}&do=share&id=${share.sid}">${share.title_template}</a>
						<span class="gray">${share.dateline}</span>
					</div>
					<div class="feed">
						<div style="width: 100%; overflow: hidden;">
							<c:if test="${!jch:jchEmpty(share.image)}"><a href="${share.image_link}"><img src="${share.image}" class="simage"/></a></c:if>
							<div class="detail">
								${share.body_template}
							</div>
							<c:choose>
								<c:when test="${'video'==share.type}"><a href="space.jsp?uid=${share.uid}&do=share&id=${share.sid}" target="_blank"><img src="image/vd.gif" class="simage"></a></c:when>
								<c:when test="${'music'==share.type}"><div class="media"><img src="image/music.gif" alt="点击播放" onclick="javascript:showFlash('music', '${share.body_data.musicvar}', this, '${share.sid}');" style="cursor: pointer;" /></div></c:when>
								<c:when test="${'flash'==share.type}"><a href="space.jsp?uid=${share.uid}&do=share&id=${share.sid}" target="_blank"><img src="image/flash.gif" class="simage"/></a></c:when>
							</c:choose>
							<div class="quote"><span id="quote" class="q">${share.body_general}</span></div>
						</div>
					</div>
				</li></c:forEach>
			</ul>
		</div>
	</div>
	<div class="footerbar">
		<div class="fbtop"></div>
		<div class="nbox_c">
			<div class="foobox">
				<div class="fbox">
					<h2 class="ntitle">常用</h2>
					<ul>
						<li><a href="space.jsp?do=feed">动态</a></li>
						<li><a href="space.jsp?do=doing">记录</a></li>
						<li><a href="space.jsp?do=album">相册</a></li>
						<li><a href="space.jsp?do=blog">日志</a></li>
						<li><a href="space.jsp?do=poll">投票</a></li>
						<li><a href="space.jsp?do=mtag">群组</a></li>
						<li><a href="space.jsp?do=event">活动</a></li>
						<li><a href="space.jsp?do=topic">热闹</a></li>
						<li><a href="space.jsp?do=share">分享</a></li>
						<li><a href="space.jsp?do=home" class="alink">更多常用……</a></li>
					</ul>
				</div>
				<div class="fbox">
					<h2 class="ntitle">应用</h2>
					<ul>
						<c:choose>
							<c:when test="${myAppCount>0}">
								<c:forEach items="${myApps}" var="myApp">
									<li><a href="userapp.jsp?id=${myApp.appid}">${myApp.appname}</a></li>
								</c:forEach>
								<li><a href="cp.jsp?ac=userapp&my_suffix=%2Fapp%2Flist" class="alink">查看全部 ${myAppCount} 个应用</a></li>
							</c:when>
							<c:otherwise><li><a href="#">还没有应用</a></li></c:otherwise>
						</c:choose>
					</ul>
				</div>
				<div class="fbox">
					<h2 class="ntitle">发现</h2>
					<ul>
						<li><a href="space.jsp?do=doing&view=all">大家发布的记录</a></li>
						<li><a href="space.jsp?do=album&view=all">大家上传的图片</a></li>
						<li><a href="space.jsp?do=blog&view=all">大家发表的日志</a></li>
						<li><a href="space.jsp?do=thread&view=all">大家的话题</a></li>
						<li><a href="space.jsp?do=event&view=all">大家的活动</a></li>
						<li><a href="space.jsp?do=share&view=all">大家的分享</a></li>
					</ul>
				</div>
			</div>
		</div>
		<div class="nbox_s">
			<h2 class="ntitle">邀请</h2>
			<ul>
				<li><a href="cp.jsp?ac=invite">邀请好友加入，获赠积分奖励</a></li>
				<li><a href="cp.jsp?ac=invite">搜狐邮箱</a></li>
				<li><a href="cp.jsp?ac=invite">sina 邮箱</a></li>
				<li><a href="cp.jsp?ac=invite">139 邮箱</a></li>
				<li><a href="cp.jsp?ac=invite">163、126、yeah 邮箱</a></li>
				<li><a href="cp.jsp?ac=invite">Yahoo! 邮箱</a></li>
				<li><a href="cp.jsp?ac=invite">Google Gmail</a></li>
				<li><a href="cp.jsp?ac=invite">MSN 邮箱</a></li>
				<li><a href="cp.jsp?ac=invite">tom 邮箱</a></li>
				<li><a href="cp.jsp?ac=invite" class="alink">更多联系人……</a></li>
			</ul>
		</div>
		<div class="fbbottom"></div>
	</div>
</div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />