<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<div id="h_status" class="h_status">
	<div class="r_option">
		<a href="${theurl}&goto=up#h_status">上一张</a><span class="pipe">|</span>
		<a href="${theurl}&goto=down#h_status" id="nextlink">下一张</a><span class="pipe">|</span>
		<c:choose>
			<c:when test="${param.play>0}"><a href="javascript:;" id="playid" onclick="playNextPic(false);">停止播放</a></c:when>
			<c:otherwise><a href="javascript:;" id="playid" onclick="playNextPic(true);">幻灯播放</a></c:otherwise>
		</c:choose>
		<span id="displayNum"></span>
	</div>
	<c:if test="${album.picnum>0}">当前第 ${sequence} 张<span class="pipe">|</span>共 ${album.picnum} 张图片</c:if>&nbsp;
	<c:if test="${album.friend>0}"><span class="locked gray">${friendsName[value.friend]}</span></c:if>
</div>
<div class="photobox">
	<div id="photo_pic" class="<c:if test="${pic.magicframe>0}"> magicframe magicframe${pic.magicframe}</c:if>">
		<c:if test="${pic.magicframe>0}">
			<div class="pic_lb1">
				<table cellpadding="0" cellspacing="0" class="">
					<tr>
						<td class="frame_jiao frame_top_left"></td>
						<td class="frame_x frame_top_middle"></td>
						<td class="frame_jiao frame_top_right"></td>
					</tr>
					<tr>
						<td class="frame_y frame_middle_left"></td>
						<td class="frame_middle_middle">
							</c:if>
							<a href="${theurl}&goto=down#h_status"><img src="${pic.pic}"/></a>
							<c:if test="${pic.magicframe>0}">
						</td>
						<td class="frame_y frame_middle_right"></td>
					</tr>
					<tr>
						<td class="frame_jiao frame_bottom_left"></td>
						<td class="frame_x frame_bottom_middle"></td>
						<td class="frame_jiao frame_bottom_right"></td>
					</tr>
				</table>
			</div>
		</c:if>
	</div>
	<div class="yinfo">
		<p>${pic.title}</p>
		<c:if test="${not empty topic}"><p><img src="image/app/topic.gif" align="absmiddle"> 凑个热闹：<a href="space.jsp?do=topic&topicid=${topic.topicid}">${topic.subject}</a></p></c:if>
		<c:if test="${do!='event' && not empty event.title}"><p>来自活动：<a href="space.jsp?do=event&id=${event.eventid}&view=pic">${event.title}</a></p></c:if>
		<p class="gray">
			<c:if test="${pic.hot>0}"><span class="hot"><em>热</em>${pic.hot}</span></c:if>
			<c:if test="${do=='event'}"><a href="space.jsp?uid=${pic.uid}" target="_blank">${sNames[pic.uid]}</a></c:if>
			上传于 <jch:date dateformat="yyyy-MM-dd HH:mm" timestamp="${pic.dateline}" /> (${pic.size})
		</p>
		<c:if test="${param.exif!=null}">
			<c:choose>
				<c:when test="${not empty exifs}">
					<c:forEach items="${exifs}" var="exif">
						<c:if test="${not empty exif.value}"><p>${exif.key} : ${exif.value}</p></c:if>
					</c:forEach>
				</c:when>
				<c:otherwise><p>无EXIF信息</p></c:otherwise>
			</c:choose>
		</c:if>
	</div>
	<table width="100%">
		<tr>
			<td align="left">
				<a href="${pic.pic}" target="_blank">查看原图</a>
				<c:if test="${param.exif==null}"><span class="pipe">|</span><a href="${theurl}&exif">查看EXIF信息</a></c:if>
			</td>
			<td align="right">
				<a href="cp.jsp?ac=share&type=pic&id=${pic.picid}" id="a_share_${pic.picid}" class="a_share" onclick="ajaxmenu(event, this.id, 1)" class="a_share">分享</a>
				<c:if test="${pic.uid==sGlobal.supe_uid}">
					<c:if test="${not empty globalMagic.frame}">
						<img src="image/magic/frame.small.gif" class="magicicon">
						<c:choose>
							<c:when test="${pic.magicframe>0}"><a id="a_magic_frame" href="cp.jsp?ac=magic&op=cancelframe&idtype=picid&id=${pic.picid}" onclick="ajaxmenu(event,this.id)">取消相框</a></c:when>
							<c:otherwise><a id="a_magic_frame" href="magic.jsp?mid=frame&idtype=picid&id=${pic.picid}" onclick="ajaxmenu(event,this.id, 1)">加相框</a></c:otherwise>
						</c:choose>
						<span class="pipe">|</span>
					</c:if>
					<a href="cp.jsp?ac=album&op=editpic&albumid=${pic.albumid}&picid=${pic.picid}">管理图片</a><span class="pipe">|</span>
				</c:if>
				<c:if test="${sGlobal.supe_uid==pic.uid || managealbum}">
					<a href="cp.jsp?ac=album&op=edittitle&albumid=${pic.albumid}&picid=${pic.picid}" id="a_set_title" onclick="ajaxmenu(event, this.id)">编辑说明</a><span class="pipe">|</span>
					<a href="cp.jsp?ac=topic&op=join&id=${pic.picid}&idtype=picid" id="a_topicjoin_${pic.picid}" onclick="ajaxmenu(event, this.id)">凑热闹</a><span class="pipe">|</span>
					<a href="admincp.jsp?ac=pic&picid=${pic.picid}" target="_blank">删除</a><span class="pipe">|</span>
				</c:if>
				<c:if test="${managealbum}"><a href="cp.jsp?ac=album&picid=${pic.picid}&op=edithot" id="a_hot_${pic.picid}" onclick="ajaxmenu(event, this.id)">热度</a><span class="pipe">|</span></c:if>
				<a href="cp.jsp?ac=common&op=report&idtype=picid&id=${pic.picid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">举报</a>
			</td>
		</tr>
	</table>
</div>
<div id="click_div" style="margin: 0 auto; padding: 10px; width: 100%; text-align: left;">
	<jsp:include page="${jch:template(sConfig, sGlobal, 'space_click.jsp')}" />
</div>
<div style="padding-top: 20px; width: 100%; overflow: hidden;" id="pic_comment">
	<h2>评论</h2>
	<div class="page">${multi}</div>
	<div class="comments_list" id="comment">
		<c:if test="${cid>0}"><div class="notice">当前只显示与你操作相关的单个评论，<a href="${theurl}#comment">点击此处查看全部评论</a></div></c:if>
		<ul id="comment_ul">
			<c:forEach items="${list}" var="comment">
				<c:set var="comment" value="${comment}" scope="request"></c:set>
				<jsp:include page="${jch:template(sConfig, sGlobal, 'space_comment_li.jsp')}" />
			</c:forEach>
		</ul>
	</div>
	<div class="page">${multi}</div>
	<form id="quickcommentform_${picid}" name="quickcommentform_${picid}" action="cp.jsp?ac=comment" method="post" class="quickpost" style="padding-bottom: 1em;">
		<table cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<a href="###" id="comment_face" onclick="showFace(this.id, 'comment_message');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
					<c:if test="${not empty globalMagic.doodle}"><a id="a_magic_doodle" href="magic.jsp?mid=doodle&showid=comment_doodle&target=comment_message" onclick="ajaxmenu(event, this.id, 1)"><img src="image/magic/doodle.small.gif" class="magicicon" />涂鸦板</a></c:if>
					<br />
					<textarea id="comment_message" onkeydown="ctrlEnter(event, 'commentsubmit_btn');" name="message" rows="5" cols="60" style="width: 380px;"></textarea>
				</td>
			</tr>
			<tr>
				<td>
					<input type="hidden" name="refer" value="${theurl}" />
					<input type="hidden" name="id" value="${picid}">
					<input type="hidden" name="idtype" value="picid">
					<input type="hidden" name="commentsubmit" value="true">
					<input type="button" id="commentsubmit_btn" name="commentsubmit_btn" class="submit" value="评论" onclick="ajaxpost('quickcommentform_${picid}', 'comment_add')" />
					<span id="__quickcommentform_${picid}"></span>
				</td>
			</tr>
		</table>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" />
	</form>
</div>
<script type="text/javascript">
	var interval = 5000;
	var timerId = -1;
	var derId = -1;
	var replay = false;
	var num = 0;
	var endPlay = false;
	function forward() {
		window.location.href = '${theurl}&goto=down&play=1#h_status';
	}
	function derivativeNum() {
		num++;
		$('displayNum').innerHTML = '[' + (interval/1000 - num) + ']';
	}
	function playNextPic(stat) {
		if(stat || replay) {
			derId = window.setInterval('derivativeNum();', 1000);
			$('displayNum').innerHTML = '[' + (interval/1000 - num) + ']';
			$('playid').innerHTML = '停止播放';
			timerId = window.setInterval('forward();', interval);
		} else {
			replay = true;
			num = 0;
			if(endPlay) {
				$('playid').innerHTML = '重新播放';
			} else {
				$('playid').innerHTML = '幻灯播放';
			}
			$('displayNum').innerHTML = '';
			window.clearInterval(timerId);
			window.clearInterval(derId);
		}
	}
	<c:if test="${param.play>0}">
	<c:choose>
	<c:when test="${sequence>0 && album.picnum>0}">
	if(${sequence} == ${album.picnum}) {
		endPlay = true;
		playNextPic(false);
	} else {
		playNextPic(true);
	}
	</c:when>
	<c:otherwise>
	playNextPic(true);
	</c:otherwise>
	</c:choose>
	</c:if>
	function update_title() {
		$('title_form').style.display='';
	}
	var elems = selector('div[class~=magicflicker]'); 
	for(var i=0; i<elems.length; i++){
		magicColor(elems[i]);
	}
</script>