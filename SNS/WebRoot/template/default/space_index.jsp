<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:set var="tpl_noSideBar" value="1" scope="request"/>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:if test="${not empty narrowlist || not empty widelist}">
<script type="text/javascript" src="source/script_swfobject.js"></script>
</c:if>

<div id="space_page">

	<div id="ubar">

		<div id="space_avatar">
			<c:choose>
			<c:when test="${!jch:jchEmpty(space.magicstar) && space.magicexpire>sGlobal.timestamp}">
			<div class="magicstar">
			<object codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,45,0" width="200" height="250">
				<param name="movie" value="image/magic/star/${space.magicstar}.swf" />
				<param name="quality" value="high" />
				<param NAME="wmode" value="transparent">
				<embed src="image/magic/star/${space.magicstar}.swf" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash"  wmode="transparent" width="200" height="250"></embed>
			</object>
			</div>
			
			<div class="magicavatar"></c:when><c:otherwise><div></c:otherwise></c:choose>${jch:avatar2(space.uid, 'big', false, sGlobal, sConfig)}</div>
			
		</div>

		<c:if test="${space.self && not empty globalMagic.superstar}">
		<div class="borderbox">
			<div style="width:100%; overflow:hidden;">
			<img src="image/magic/superstar.small.gif" class="magicicon" />
			<c:choose>
			<c:when test="${!jch:jchEmpty(space.magicstar) && space.magicexpire>sGlobal.timestamp}">
			<a id="a_magic_superstar" href="cp.jsp?ac=magic&op=cancelsuperstar" onclick="ajaxmenu(event, this.id)">取消超级明星</a>
			</c:when>
			<c:otherwise>
			<a id="a_magic_superstar" href="magic.jsp?mid=superstar" onclick="ajaxmenu(event, this.id, 1)">我要变超级明星</a>
			</c:otherwise>
			</c:choose>
			</div>
		</div><br />
		</c:if>

		<div class="borderbox">
			<ul class="spacemenu_list" style="width:100%; overflow:hidden;">
		<c:choose>
		<c:when test="${space.self}">
			<li><a href="cp.jsp?ac=avatar">我的头像</a></li>
			<li><a href="cp.jsp?ac=profile">个人资料</a></li>
			<li><a href="cp.jsp?ac=theme">主页风格</a></li>
			<li><a href="cp.jsp?ac=credit">我的积分</a></li>
			<c:if test="${sConfig.sendmailday>0}">
			<li><a href="cp.jsp?ac=sendmail">邮件提醒</a></li>
			</c:if>
			<li><a href="cp.jsp?ac=privacy">隐私筛选</a></li>
		</c:when>
		<c:otherwise>
			<c:if test="${!space.isfriend}">
			<li><img src="image/icon/friend.gif"><a href="cp.jsp?ac=friend&op=add&uid=${space.uid}" id="a_friend_li" onclick="ajaxmenu(event, this.id, 1)">加为好友</a></li>
			</c:if>
			<li><img src="image/icon/wall.gif"><a href="#comment">给我留言</a></li>
			<li><img src="image/icon/poke.gif"><a href="cp.jsp?ac=poke&op=send&uid=${space.uid}" id="a_poke" onclick="ajaxmenu(event, this.id, 1)">打个招呼</a></li>
			<li><img src="image/icon/pm.gif"><a href="cp.jsp?ac=pm&uid=${space.uid}" id="a_pm" onclick="ajaxmenu(event, this.id, 1)">发送消息</a></li>
			<c:if test="${space.isfriend}">
			<li><img src="image/icon/friend.gif"><a href="cp.jsp?ac=friend&op=ignore&uid=${space.uid}" id="a_ignore" onclick="ajaxmenu(event, this.id)">解除好友</a></li>
			</c:if>
			<li><img src="image/icon/report.gif"><a href="cp.jsp?ac=common&op=report&idtype=uid&id=${space.uid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">违规举报</a></li>
			<c:if test="${manageName || manageSpaceGroup || manageSpaceInfo || manageSpaceCredit || manageSpaceNote }">
			<li><img src="image/icon/profile.gif"><a href="admincp.jsp?ac=space&op=manage&uid=${space.uid}" id="a_manage">管理用户</a></li>
			</c:if>
			<c:if test="${space.showgiftlink == '1'}">
			<li><img src="image/icon/gift.gif"><a href="cp.jsp?ac=gift&defreceiver=${space.username}">送他礼物</a></li>
			</c:if>
		</c:otherwise>
		</c:choose>
			</ul>
		</div><br />

		<div id="space_mymenu">
			<h2>个人菜单</h2>
			<ul class="line_list">
				<li>
					<c:if test="${space.self}">
					<a href="cp.jsp?ac=profile" class="r_option" target="_blank">完善</a>
					</c:if>
					<img src="image/icon/profile.gif"><a href="javascript:;" onclick="getindex('info');">个人资料</a>
				</li>
				<li>
					<c:if test="${space.self}">
					<a href="space.jsp?do=doing&view=me" class="r_option" target="_blank">记录</a>
					</c:if>
					<img src="image/icon/doing.gif"><a href="javascript:;" onclick="getindex('doing');">记录</a><c:if test="${space.doingnum>0}"><em>(${space.doingnum})</em></c:if>
				</li>
				<li>
					<c:if test="${space.self}">
					<a href="cp.jsp?ac=blog" class="r_option" target="_blank">发表</a>
					</c:if>
					<img src="image/icon/blog.gif"><a href="javascript:;" onclick="getindex('blog');">日志</a><c:if test="${space.blognum>0}"><em>(${space.blognum})</em></c:if></li>
				<li><c:if test="${space.self}">
					<a href="cp.jsp?ac=upload" class="r_option" target="_blank">上传</a>
					</c:if>
					<img src="image/icon/album.gif"><a href="javascript:;" onclick="getindex('album');">相册</a><c:if test="${space.albumnum>0}"><em>(${space.albumnum})</em></c:if></li>
				<li><c:if test="${space.self}">
					<a href="cp.jsp?ac=thread" class="r_option" target="_blank">发表</a>
					</c:if>
					<img src="image/icon/thread.gif"><a href="javascript:;" onclick="getindex('thread');">话题</a><c:if test="${space.threadnum>0}"><em>(${space.threadnum})</em></c:if></li>
				<li><c:if test="${space.self}">
					<a href="cp.jsp?ac=poll" class="r_option" target="_blank">发起</a>
					</c:if>
					<img src="image/icon/poll.gif"><a href="javascript:;" onclick="getindex('poll');">投票</a><c:if test="${space.pollnum>0}"><em>(${space.pollnum})</em></c:if></li>
				<li><c:if test="${space.self}">
					<a href="cp.jsp?ac=event" class="r_option" target="_blank">发起</a>
					</c:if>
					<img src="image/icon/event.gif"><a href="javascript:;" onclick="getindex('event');">活动</a><c:if test="${space.eventnum>0}"><em>(${space.eventnum})</em></c:if></li>
				<li><c:if test="${space.self}">
					<a href="space.jsp?do=share&view=me" class="r_option" target="_blank">添加</a>
					</c:if>
					<img src="image/icon/share.gif"><a href="javascript:;" onclick="getindex('share');">分享</a><c:if test="${space.sharenum>0}"><em>(${space.sharenum})</em></c:if></li>
				<li><c:if test="${space.self}">
					<a href="cp.jsp?ac=gift" class="r_option" target="_blank">送礼</a>
					</c:if>
					<img src="image/icon/gift.gif"><a href="javascript:;" onclick="getindex('gift');">礼物</a><c:if test="${space.giftnum>0}"><em>(${space.giftnum})</em></c:if></li>
				<li><c:if test="${space.self}">
					<a href="cp.jsp?ac=friend&op=search" class="r_option" target="_blank">寻找</a>
					</c:if>
					<img src="image/icon/friend.gif"><a href="javascript:;" onclick="getindex('friend');">好友</a><c:if test="${space.friendnum>0}"><em>(${space.friendnum})</em></c:if></li>
			</ul>
		</div>

		<c:if test="${not empty guidelist}">
		<div id="space_app_guide">
			<h2>应用菜单</h2>
			<ul class="line_list">
			<c:forEach items="${guidelist}" var="value">
			<li id="space_app_profilelink_${value.appid}">
				<c:if test="${space.self}">
				<a href="cp.jsp?ac=space&op=delete&appid=${value.appid}&type=profilelink" id="user_app_profile_${value.appid}" class="r_option float_del" style="position: static;" onclick="ajaxmenu(event, this.id)" title="删除">删除</a>
				</c:if>
				<img src="http://appicon.jsprun.com/icons/${value.appid}">${value.profilelink}
			</li>
			</c:forEach>
			</ul>
		</div>
		</c:if>

		<c:forEach items="${narrowlist}" var="value">
		<div id="space_app_$value[appid]">
			<h2>
				<c:if test="${space.self}">
				<a href="cp.jsp?ac=space&op=delete&appid=${value.appid}" id="user_app_${value.appid}" class="r_option float_del" onclick="ajaxmenu(event, this.id)" title="删除">删除</a>
				</c:if>
				<a href="${value.appurl}">${value.appname}</a>
			</h2>
			<c:if test="${!jch:jchEmpty(value.myml)}">
			<div class="box">
			${value.myml}
			</div>
			</c:if>
		</div>
		</c:forEach>

	</div>

	<div id="content">
			
		<h3 id="spaceindex_name">
		<c:choose>
		<c:when test="${!jch:jchEmpty(sConfig.realname)}">
			<c:choose>
			<c:when test="${!jch:jchEmpty(space.name)}"><a href="space.jsp?uid=${space.uid}"${gColor}>${space.name}</a></c:when>
			<c:otherwise>未填写实名</c:otherwise>
			</c:choose>
			&nbsp;<em>(用户名: ${space.username})</em>
		</c:when>
		<c:otherwise>
			<a href="space.jsp?uid=${space.uid}"${gColor}>${space.username}</a>
			<c:if test="${!jch:jchEmpty(space.name)}">&nbsp;<em>(姓名: ${space.name})</em></c:if>
		</c:otherwise>
		</c:choose>

		<c:if test="${!jch:jchEmpty(sConfig.realname)}">
			<c:choose>
			<c:when test="${!jch:jchEmpty(space.namestatus)}">
				&nbsp;<img src="image/realname_yes.gif" align="absmiddle" alt="已通过实名认证">
			</c:when>
			<c:otherwise>
				&nbsp;<img src="image/realname_no.gif" align="absmiddle" alt="未通过实名认证"> <span class="gray">实名未认证</span>
			</c:otherwise>
			</c:choose>
		</c:if>
			
		<c:if test="${!jch:jchEmpty(sConfig.videophoto)}">
			<c:choose>
			<c:when test="${!jch:jchEmpty(space.videostatus)}">
				&nbsp;<img src="image/videophoto_yes.gif" align="absmiddle" alt="已通过视频认证"> <a id="a_space_videophoto" href="space.jsp?uid=${space.uid}&do=videophoto" onclick="ajaxmenu(event, this.id, 1)"><span style="color:red;font-weight:bold;font-size:12px;">查看视频认证照</span></a>
			</c:when>
			<c:otherwise>
				&nbsp; <img src="image/videophoto_no.gif" align="absmiddle" alt="未通过视频认证"> <span class="gray"><a href="cp.jsp?ac=videophoto">视频未认证</a></span>
			</c:otherwise>
			</c:choose>
		</c:if>
		</h3>


		<div id="spaceindex_note">
			<a href="cp.jsp?ac=share&type=space&id=${space.uid}" class="a_share" id="a_share" onclick="ajaxmenu(event, this.id, 1)">分享</a>
			<a href="rss.jsp?uid=${space.uid}" id="i_rss" title="订阅 RSS">订阅</a>
			
			<ul class="note_list">
				<li>已有 ${space.viewnum} 人次访问, ${space.credit} 个积分, ${space.experience} 个经验 ${space.star}</li>
				<li>用户组别：<a href="cp.jsp?ac=credit&op=usergroup">${globalGroupTitle[space.groupid].grouptitle}</a> ${gIcon}</li>
				<li>主页地址：<a href="${space.domainurl}" onclick="javascript:setCopy('${space.domainurl}');return false;" class="spacelink domainurl">${space.domainurl}</a></li>
			
				<c:if test="${!space.self && !jch:jchEmpty(space.spacenote)}">
				<li>${space.spacenote} <a href="space.jsp?uid=${space.uid}&do=doing">&raquo;</a></li>
				</c:if>
			</ul>
	
			<c:if test="${space.self}">
			<jsp:include page="${jch:template(sConfig, sGlobal, 'space_status.jsp')}"/>
			</c:if>
		</div>

		<div id="maincontent">

		<c:if test="${!space.isfriend}">
		<div class="borderbox">
			<p style="padding-bottom:10px;">如果您认识${sNames[space.uid]}，可以给TA留个言，或者打个招呼，或者添加为好友。<br />成为好友后，您就可以第一时间关注到TA的更新动态。</p>
			<a href="cp.jsp?ac=friend&op=add&uid=${space.uid}" id="a_friend_notice" onclick="ajaxmenu(event, this.id, 1)" class="submit">加为好友</a></p>
		</div><br>
		</c:if>

		<div id="space_info">
			<h3 class="feed_header">
				<c:if test="${space.self}">
				<a href="cp.jsp?ac=profile" class="r_option">完善资料</a>
				</c:if>
				个人资料
			</h3>
			<ul class="spacemenu_list">
				<li><em>创建:</em><jch:date dateformat="yyyy-MM-dd" timestamp="${space.dateline}" format="1"/></li>
				<li><em>登录:</em><c:if test="${!jch:jchEmpty(space.lastlogin)}"><jch:date dateformat="yyyy-MM-dd" timestamp="${space.lastlogin}" format="1"/></c:if></li>
				<c:if test="${not empty isonline}">
				<li><em>活跃:</em>${isonline}(当前在线)</li>
				</c:if>
			<c:if test="${not empty space.sex}">
				<li><em>性别:</em>${space.sex}</li>
			</c:if>
			<c:if test="${not empty space.birth}">
				<li><em>生日:</em>${space.birth}</li>
			</c:if>
			<c:if test="${not empty space.blood}">
				<li><em>血型:</em>${space.blood}</li>
			</c:if>
			<c:if test="${not empty space.marry}">
				<li><em>婚恋:</em>${space.marry}</li>
			</c:if>
			<c:if test="${not empty space.residecity}">
				<li><em>居住:</em>${space.residecity}</li>
			</c:if>
			<c:if test="${not empty space.birthcity}">
				<li><em>家乡:</em>${space.birthcity}</li>
			</c:if>
			<c:if test="${not empty space.mobile}">
				<li><em>手机:</em>${space.mobile}</li>
			</c:if>
			<c:if test="${not empty space.qq}">
				<li><em>QQ:</em>${space.qq}</li>
			</c:if>
			<c:if test="${not empty space.msn}">
				<li><em>MSN:</em>${space.msn}</li>
			</c:if>
			</ul>
			<p class="info_more"><a href="javascript:;" onclick="getindex('info');">&raquo; 查看全部个人资料</a></p>
		</div>

		<c:if test="${not empty feedlist}">
		<div id="space_feed" class="feed">
			<h3 class="feed_header">
				<span class="r_option">
				<a href="space.jsp?uid=${space.uid}&do=feed&view=me" class="action">全部</a>
				</span>
				<span class="entry-title">个人动态</span>
			</h3>
			<div class="box_content">
				<ul>
				<c:forEach items="${feedlist}" var="value">
					<c:set var="feed" value="${value}" scope="request"></c:set>
					<jsp:include page="${jch:template(sConfig, sGlobal, 'space_feed_li.jsp')}"/>
				</c:forEach>
				</ul>
			</div>
		</div>
		</c:if>

		<c:if test="${not empty albumlist}">
		<div id="space_photo">
			<h3 class="feed_header">
				<a href="space.jsp?uid=${space.uid}&do=album&view=me" class="r_option">全部</a>
				相册
			</h3>
			<table cellspacing="4" cellpadding="4" width="100%">
			<tr>
				<c:forEach items="${albumlist}" var="value" varStatus="key">
				<td width="85" align="center"><a href="space.jsp?uid=${space.uid}&do=album&id=${value.albumid}" target="_blank"><img src="${value.pic}" alt="${value.albumname}" width="70" /></a></td>
				<td width="165">
					<h6><a href="space.jsp?uid=${space.uid}&do=album&id=${value.albumid}" target="_blank" title="${value.albumname}">${value.albumname}</a></h6>
					<p class="gray">${value.picnum} 张照片</p>
					<p class="gray">更新于<jch:date dateformat="MM-dd" timestamp="${value.dateline}" format="1"/></p>
				</td>
				<c:if test="${key.index%2==1}"></tr><tr></c:if>
				</c:forEach>
			</tr>
			</table>
		</div>
		</c:if>

		<c:if test="${not empty bloglist}">
		<div id="space_blog" class="feed">
			<h3 class="feed_header">
				<a href="space.jsp?uid=${space.uid}&do=blog&view=me" class="r_option">全部</a>
				日志
			</h3>
			<ul class="line_list">
			<c:forEach items="${bloglist}" var="value">
				<li>
					
					<h4>
						<span class="gray r_option"><jch:date dateformat="MM-dd HH:mm" timestamp="${value.dateline}" format="1"/></span>
						<a href="space.jsp?uid=${space.uid}&do=blog&id=${value.blogid}" target="_blank">${value.subject}</a>
					</h4>
					<div class="detail">
						${value.message}
					</div>
				</li>
			</c:forEach>
			</ul>
		</div>
		</c:if>


		<c:forEach items="${widelist}" var="value">
		<c:if test="${!jch:jchEmpty(value.myml)}">
		<div id="space_app_${value.appid}" class="appbox">
			<h3 class="feed_header">
				<c:if test="${space.self}">
				<a href="cp.jsp?ac=space&op=delete&appid=${value.appid}" id="user_app_${value.appid}" class="r_option float_del" onclick="ajaxmenu(event, this.id)" title="删除">删除</a>
				</c:if>
				<a href="${value.appurl}">${value.appname}</a>
			</h3>
			<div class="box" style="margin: 0 0 20px;">
			${value.myml }
			</div>
		</div>
		</c:if>
		</c:forEach>

		</div>

		<div id="comment" class="comments_list">
			<h3 class="feed_header">
				<a href="space.jsp?uid=${space.uid}&do=wall&view=me" class="r_option">全部</a>
				留言板
			</h3>

			<div class="box">
				<form action="cp.jsp?ac=comment" id="quick_commentform_${space.uid}" name="quick_commentform_${space.uid}" method="post" style="padding:0 0 0 5px;">
					<a href="###" id="editface" onclick="showFace(this.id, 'comment_message');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
					<c:if test="${not empty globalMagic.doodle}">
					<a id="a_magic_doodle" href="magic.jsp?mid=doodle&showid=comment_doodle&target=comment_message" onclick="ajaxmenu(event, this.id, 1)"><img src="image/magic/doodle.small.gif" class="magicicon" />涂鸦板</a>
					</c:if>
					<br />
					<textarea name="message" id="comment_message" rows="4" cols="60" style="width:98%;" onkeydown="ctrlEnter(event, 'commentsubmit_btn');"></textarea><br>
					<input type="hidden" name="refer" value="space.jsp?uid=${space.uid}" />
					<input type="hidden" name="id" value="${space.uid}" />
					<input type="hidden" name="idtype" value="uid" />
					<input type="hidden" name="commentsubmit" value="true" />
					<input type="button" id="commentsubmit_btn" name="commentsubmit_btn" class="submit" value="留言" onclick="ajaxpost('quick_commentform_${space.uid}', 'wall_add')" />
					<span id="__quick_commentform_${space.uid}"></span>
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
				</form>
			</div>

			<div class="box_content">
				<ul class="post_list a_list justify_list" id="comment_ul">
				<c:forEach items="${wallList}" var="value">
				<c:set var="comment" value="${value}" scope="request"></c:set>
				<jsp:include page="${jch:template(sConfig, sGlobal, 'space_comment_li.jsp')}"/>
				</c:forEach>
				</ul>
				<c:if test="${not empty wallList}">
				<p class="r_option" style="padding:5px 0 10px 0;"><a href="space.jsp?uid=${space.uid}&do=wall&view=me">&raquo; 更多留言</a></p>
				</c:if>
			</div>
		</div>
	</div>

	<div id="obar">
		<c:choose>
		<c:when test="${!space.self}">
			<c:if test="${space.magiccredit>0}">
			<div class="magichongbao" id="div_magic_gift">
				<a id="a_magic_gift" href="cp.jsp?&ac=magic&op=receive&uid=${space.uid}" onclick="ajaxmenu(event, this.id)">送你 <span>${space.magiccredit}</span> 积分大红包</a>
			</div>
			</c:if>

			<c:if test="${not empty globalMagic.viewmagiclog || not empty globalMagic.viewmagic || not empty globalMagic.viewvisitor}">
			<div class="indexmagic">
			<c:forEach items="${mids}" var="mid">
				<c:if test="${not empty globalMagic[mid]}">
				<a id="a_magic_${mid}" href="magic.jsp?mid=${mid}&idtype=uid&id=${space.uid}" onclick="ajaxmenu(event,this.id,1)">
				<img src="image/magic/${mid}.small.gif" title="${globalMagic[mid]}" alt="${globalMagic[mid]}">
				</a>
				</c:if>
			</c:forEach>
			</div>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${not empty globalMagic.gift}">
			<div class="magichongbao" id="div_magic_gift">				
				<c:choose>
				<c:when test="${space.magiccredit>0}">
				<a id="a_magic_retrieve" href="cp.jsp?ac=magic&op=retrieve" onclick="ajaxmenu(event,this.id)">回收埋下的积分</a>
				</c:when>
				<c:otherwise>
				<a id="a_magic_gift" href="magic.jsp?mid=gift" onclick="ajaxmenu(event,this.id,1)">给来访者埋个红包</a>
				</c:otherwise>
				</c:choose>
			</div>
			</c:if>
		</c:otherwise>
		</c:choose>


		<c:if test="${not empty visitorlist}">
		<div class="sidebox">
			<h2 class="title">
				<a href="space.jsp?uid=${space.uid}&do=friend&view=visitor" class="r_option">全部</a>
				最近来访
				<c:if test="${!space.self && not empty globalMagic.anonymous}">
				<span class="gray"><img title="${globalMagic.anonymous}" src="image/magic/anonymous.small.gif"/><a id="a_magic_anonymous" href="magic.jsp?mid=anonymous&idtype=uid&id=${space.uid}" onclick="ajaxmenu(event,this.id,1)">匿名</a></span>
				</c:if>
			</h2>
			<ul class="avatar_list">
				<c:forEach items="${visitorlist}" var="visitor">
				<li>
					<c:choose>
					<c:when test="${visitor.value.vusername==''}">
					<div class="avatar48"><img src="image/magic/hidden.gif" alt="匿名" /></div>
					<p>匿名</p>
					<p class="gray"><jch:date dateformat="M月d日" timestamp="${visitor.value.dateline}" format="1"/></p>
					</c:when>
					<c:otherwise>
					<div class="avatar48"><a href="space.jsp?uid=${visitor.value.vuid}">${jch:avatar1(visitor.value.vuid,sGlobal, sConfig)}</a></div>
					<p <c:if test="${ols[visitor.value.vuid]}">class="online_icon_p"</c:if>><a href="space.jsp?uid=${visitor.value.vuid}" title="${sNames[visitor.value.vuid]}">${sNames[visitor.value.vuid]}</a></p>
					<p class="gray"><jch:date dateformat="M月d日" timestamp="${visitor.value.dateline}" format="1"/></p>
					</c:otherwise>
					</c:choose>
				</li>
				</c:forEach>
			</ul>
		</div>
		</c:if>


		<c:if test="${not empty friendlist}">
		<div class="sidebox">
			<h2 class="title">
			<span class="r_option">
				<a href="space.jsp?uid=${space.uid}&do=friend&view=me" class="action">全部(${space.friendnum})</a>
			</span>
			好友
			</h2>
			<ul class="avatar_list">
				<c:forEach items="${friendlist}" var="value">
				<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.fuid}">${jch:avatar1(value.fuid,sGlobal, sConfig)}</a></div>
				<p<c:if test="${ols[value.fuid]}"> class="online_icon_p"</c:if>><a href="space.jsp?uid=${value.fuid}">${sNames[value.fuid]}</a></p>
				</li>
				</c:forEach>
			</ul>
		</div>
		</c:if>

	</div>

</div>

<c:if test="${!jch:jchEmpty(param.theme)}"><div class="nn">您是否想使用这款个性风格?<br /><a href="cp.jsp?ac=theme&op=use&dir=${param.theme}">[应用]</a><a href="cp.jsp?ac=theme">[取消]</a></div></c:if>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>

<script>
function getindex(type) {
	var plus = '';
	if(type == 'event') plus = '&type=self';
	ajaxget('space.jsp?uid=${space.uid}&do='+type+'&view=me'+plus+'&ajaxdiv=maincontent', 'maincontent');
}
var elems = selector('div[class~=magicflicker]'); 
for(var i=0; i<elems.length; i++){
	magicColor(elems[i]);
}

</script>