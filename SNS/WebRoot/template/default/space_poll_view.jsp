<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:choose>
<c:when test="${space.self}">
<h2 class="title"><img src="image/app/poll.gif" />投票</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li><a href="space.jsp?uid=${space.uid}&do=${do}&view=new"><span>最新投票</span></a></li>
		<li><a href="space.jsp?uid=${space.uid}&do=${do}&view=hot"><span>最热投票</span></a></li>
		<li><a href="space.jsp?uid=${space.uid}&do=${do}&view=reward"><span>悬赏投票</span></a></li>
		<c:if test="${space.friendnum>0}"><li${actives.friend}><a href="space.jsp?uid=${space.uid}&do=${do}&view=friend"><span>好友投票</span></a></li></c:if>
		<li class="active"><a href="space.jsp?uid=${space.uid}&do=${do}&view=me"><span>我的投票</span></a></li>
		<li class="null"><a href="cp.jsp?ac=poll">发表新投票</a></li>
	</ul>
	<c:if test="${not empty sGlobal.refer}">
	<div class="r_option">
		<a  href="${sGlobal.refer}">&laquo; 返回上一页</a>
	</div>
	</c:if>
</div>
</c:when>
<c:otherwise>
<jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}"/>
</c:otherwise>
</c:choose>

<div id="content">
	<div class="poll_header">
		<div class="floatleft">
			<p>发起时间: <jch:date dateformat="yyyy-MM-dd HH:mm" timestamp="${poll.dateline}" format="1"/></p>
			<p>参与人数: <strong>${poll.voternum}</strong> 人</p>
			<c:if test="${space.self && poll.credit>0}">
			<p>剩余悬赏: <strong>${poll.credit}</strong></p>
			</c:if>
			<c:if test="${!jch:jchEmpty(poll.expiration)}"><p>截止时间: <jch:date dateformat="yyyy-MM-dd" timestamp="${poll.expiration}"/></p></c:if>
		</div>
		<c:if test="${space.self}">
		<div class="floatright">
			<script type="text/javascript" src="source/script_calendar.js"></script>
			<c:if test="${poll.credit>0}">
			<p><a href="cp.jsp?ac=poll&op=endreward&pid=${poll.pid}" id="poll_reward_${poll.pid}" onclick="ajaxmenu(event, this.id)">终止悬赏</a></p>
			</c:if>
			<p><a href="cp.jsp?ac=poll&op=addreward&pid=${poll.pid}" id="add_new_reward" onclick="ajaxmenu(event, this.id)">追加悬赏</a></p>
			<p><a href="cp.jsp?ac=poll&op=addopt&pid=${poll.pid}" id="addnewoption" onclick="ajaxmenu(event, this.id, 1)">增加候选项</a></p>
			<p><a href="cp.jsp?ac=poll&pid=${poll.pid}&op=delete" id="poll_delete_${poll.pid}" onclick="ajaxmenu(event, this.id)">删除本投票</a></p>
			<p><a href="cp.jsp?ac=poll&pid=${poll.pid}&op=modify" id="poll_modify_${poll.pid}" onclick="ajaxmenu(event, this.id)">修改截止时间</a></p>
			<p><a href="cp.jsp?ac=poll&pid=${poll.pid}&op=summary" id="poll_summary_${poll.pid}" onclick="ajaxmenu(event, this.id, 1)">写写投票总结</a></p>
		</div>
		</c:if>
	</div>

	<div class="poll_title">
		<c:choose>
		<c:when test="${expiration}">
		<div class="print overtime">[过期]</div>
		</c:when>
		<c:when test="${poll.percredit>0}">
		<div class="print guerdon">[悬赏]</div>
		</c:when>
		</c:choose>
		<c:if test="${poll.hot>0}"><span class="hot"><em>热</em>${poll.hot}</span></c:if><h3><img src="image/poll_icon.gif" /> ${poll.subject}</h3><c:if test="${!jch:jchEmpty(poll.sex) && poll.sex!=sGlobal['member'].sex || poll.multiple>0}">(<c:if test="${!jch:jchEmpty(poll.sex) && poll.sex!=sGlobal['member'].sex}">仅限<strong>${poll.sex==1 ? '男' : '女'}</strong>性参与 </c:if><c:if test="${!jch:jchEmpty(poll.multiple)}">最多可选${poll.maxchoice}项</c:if>) </c:if>
		<c:choose>
		<c:when test="${!jch:jchEmpty(param.reward)}">
		<p style="color: #F30">恭喜您获得  <strong>${param.reward}</strong> 个积分</p>
		</c:when>
		<c:when test="${poll.percredit>0}">
		<p style="color: #F30">投票将获得 <strong>${poll.percredit}</strong> 个积分</p>
		</c:when>
		</c:choose>
	</div>
	<c:if test="${!jch:jchEmpty(poll.message)}"><p class="poll_depiction">${poll.message}</p></c:if>
	<form name="poll" method="post" action="cp.jsp?ac=poll&pid=${poll.pid}&op=vote">

	<ol class="poll_item_list">
		<c:set var="bcid" value="${jch:rand(0,19)}"></c:set>
		<c:forEach items="${option}" var="val" varStatus="key">
		<li>
			<label class="poll_item">${val.option}:</label>
			<c:if test="${bcid>19}">
			<c:set var="bcid" value="${bcid-19}"></c:set>
			</c:if>
			<div class="bar_bg bc_${bcid}">
				<div class="bar_left"></div>
				<div class="bar_middle" id="bar_${key.index}" len="${val.width}"></div>
				<div class="bar_right"></div>
			</div>
			<c:set var="bcid" value="${bcid+1}"></c:set>
			<div class="poll_percent">${val.votenum} (${val.percent}%)</div>
			<div class="floatleft">
				<c:if test="${allowedvote && hasvoted==0}">
				<input type="${poll.multiple>0 ? 'checkbox' : 'radio'}" name="option" value="${val.oid}" <c:if test="${poll.multiple>0}">onclick="checkSelect(this.checked)"</c:if>/>
				</c:if>
			</div>
		</li>
		</c:forEach>
	</ol>
	<div class="poll_submit">
		<c:if test="${allowedvote && hasvoted==0}">
		<input type="hidden" name="votesubmit" value="true" />
		<input type="submit"  class="submit" id="votebutton" name="votebutton" value="投票" /><br />
		<label><input type="checkbox" name="anonymous" value="1"> 匿名投票</label>
		<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</c:if>
	</div>

	</form>

	<c:if test="${not empty poll.summary}">
	<div class="poll_summary">
	<h3 class="poll_sumuptitle">${sNames[poll.uid]}对该投票的总结</h3>
	<p class="poll_sumup">${poll.summary}</p>
	</div>
	</c:if>

	<div class="poll_htitle">

	<a href="cp.jsp?ac=share&type=poll&id=${poll.pid}" id="a_share" onclick="ajaxmenu(event, this.id, 1)" class="a_share">分享</a>
		<div class="r_option">
			<c:if test="${sGlobal.supe_uid==poll.uid || managepoll}">
			<a href="cp.jsp?ac=topic&op=join&id=${poll.pid}&idtype=pid" id="a_topicjoin_${poll.pid}" onclick="ajaxmenu(event, this.id)">凑热闹</a><span class="pipe">|</span>
			<a href="admincp.jsp?ac=poll&pid=${poll.pid}" target="_blank">删除</a><span class="pipe">|</span>
			</c:if>
			<c:if test="${managepoll}">
			<a href="cp.jsp?ac=poll&pid=${poll.pid}&op=edithot" id="a_hot_${poll.pid}" onclick="ajaxmenu(event, this.id)">热度</a><span class="pipe">|</span>
			</c:if>
			<a href="cp.jsp?ac=common&op=report&idtype=pid&id=${poll.pid}" id="a_report" onclick="ajaxmenu(event, this.id, 1)">举报</a><span class="pipe">|</span>
			<a href="cp.jsp?uid=${poll.uid}&ac=poll&op=invite&pid=${poll.pid}"/>邀请好友</a><span class="pipe">|</span>
		</div>
		<div class="custom">
		<a id="newvoter" href="javascript:;" onclick="showVoter('new');">最新投票</a>
		<a id="wevoter" href="javascript:;" onclick="showVoter('we');">好友投票</a>
		</div>
	</div>
	<div id="showvoter"></div>
	<script type="text/javascript">
		<c:if test="${hasvoted==0}">
		var maxSelect = ${poll.maxchoice};
		var alreadySelect = 0;
		function checkSelect(sel) {
			if(sel) {
				alreadySelect++;
				if(alreadySelect == maxSelect) {
					var oObj = document.getElementsByName("option");
					for(i=0; i < oObj.length; i++) {
						if(!oObj[i].checked) {
							oObj[i].disabled = true;
						}
					}
				}
			} else {
				alreadySelect--;
				if(alreadySelect < maxSelect) {
					var oObj = document.getElementsByName("option");
					for(i=0; i < oObj.length; i++) {
						if(oObj[i].disabled) {
							oObj[i].disabled = false;
						}
					}
				}
			}
		}
		</c:if>

		var optionNum = ${fn:length(option)};
		var maxLength = [0,1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15,16,17,18,19];

		var timer;
		var length = 0;
		for(i = 0; i < optionNum; i++) {
			maxLength[i] = $("bar_" + i).getAttribute('len');
		}
		timer = setInterval(function(){
			setLength();
		}, 40);
		function setLength(){
			for (i = 0; i < optionNum; i++) {
				if (length - 1 >= maxLength[i]) {
					$('bar_' + i).style.width = maxLength[i] + "px";
				} else {
					$('bar_' + i).style.width = length + "px";
				}
				length = length + 1;
				if (length > 300) {
					clearInterval(timer);
				}
			}
		}
		function showVoter(filtrate) {
			$('newvoter').className = '';
			$('wevoter').className = '';
			$(filtrate+'voter').className = 'active';
			ajaxget('cp.jsp?ac=poll&op=get&pid=${poll.pid}&filtrate='+filtrate, 'showvoter');
		}
		showVoter('new')
	</script>
	<c:if test="${isfriend}">
	<div class="comments" id="div_main_content" style="padding: 0 0 20px;">

		<h3 class="feed_header"> <div class="r_option"><span id="comment_replynum">${poll.replynum}</span> 个评论</div>评论</h3>
		<div class="page">${multi}</div>
		<div class="comments_list" id="comment">
			<c:if test="${cid>0}">
			<div class="notice">
				当前只显示与你操作相关的单个评论，<a href="space.jsp?uid=${poll.uid}&do=poll&pid=${poll.pid}">点击此处查看全部评论</a>
			</div>
			</c:if>
			<ul id="comment_ul">
			<c:forEach items="${list}" var="value">
			<c:set var="comment" value="${value}" scope="request"></c:set>
				<jsp:include page="${jch:template(sConfig, sGlobal, 'space_comment_li.jsp')}"/>
			</c:forEach>
			</ul>
		</div>
		<div class="page">${multi}</div>

		<form id="quickcommentform_${pid}" name="quickcommentform_${pid}" action="cp.jsp?ac=comment" method="post" class="quickpost">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<a href="###" id="face" title="插入表情" onclick="showFace(this.id, 'comment_message');return false;"><img src="image/facelist.gif" align="absmiddle" /></a>
						<c:if test="${not empty globalMagic.doodle}">
						<a id="a_magic_doodle" href="magic.jsp?mid=doodle&showid=comment_doodle&target=comment_message" onclick="ajaxmenu(event, this.id, 1)"><img src="image/magic/doodle.small.gif" class="magicicon" />涂鸦板</a>
						</c:if>
						<br />
						<textarea id="comment_message" onkeydown="ctrlEnter(event, 'commentsubmit_btn');" name="message" rows="5" style="width:500px;"></textarea>
					</td>
				</tr>
				<tr>
					<td>
						<input type="hidden" name="refer" value="space.jsp?uid=${poll.uid}&do=${do}&id=${pid}" />
						<input type="hidden" name="id" value="${pid}">
						<input type="hidden" name="idtype" value="pid">
						<input type="hidden" name="commentsubmit" value="true" />
						<input type="button" id="commentsubmit_btn" name="commentsubmit_btn" class="submit" value="评论" onclick="ajaxpost('quickcommentform_${pid}', 'comment_add')" />
						<span id="__quickcommentform_${pid}"></span>
					</td>
				</tr>
			</table>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig,false)}" />
		</form><br />
	</div>
	</c:if>

</div>

<div id="sidebar">


	<c:if test="${not empty topic}">
	<div class="affiche">
		<img src="image/app/topic.gif" align="absmiddle">
		<strong>凑个热闹</strong>：<a href="space.jsp?do=topic&topicid=${topic.topicid}">${topic.subject}</a>
	</div>
	</c:if>

	<div class="sidebox">
		<h2 class="title">最新投票</h2>
		<ul class="news_list poll_new">
		<c:choose>
			<c:when test="${not empty newpoll}">
			<c:forEach items="${newpoll}" var="value">
			<li style="height:auto;"><a href="space.jsp?uid=${value.uid}&do=${do}&pid=${value.pid}">${value.subject}</a></li>
			</c:forEach>
			</c:when>
			<c:otherwise>
				<li style="height:auto;">暂时没有新投票</li>
			</c:otherwise>
		</c:choose>
		</ul>
	</div>

	<div class="sidebox">
		<h2 class="title">最热投票</h2>
		<ul class="news_list poll_new">
			<c:choose>
			<c:when test="${not empty hotpoll}">
				<c:forEach items="${hotpoll}" var="value">
				<li style="height:auto;"><a href="space.jsp?uid=${value.uid}&do=${do}&pid=${value.pid}">${value.subject}</a></li>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<li style="height:auto;">暂时没有本月热门投票</li>
			</c:otherwise>
			</c:choose>
		</ul>
	</div>

</div>

<script type="text/javascript">
var elems = selector('div[class~=magicflicker]'); 
for(var i=0; i<elems.length; i++){
	magicColor(elems[i]);
}
</script>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>