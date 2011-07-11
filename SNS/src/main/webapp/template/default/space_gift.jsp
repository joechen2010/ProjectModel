<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<h2 class="title"><img src="image/app/gift.gif" />礼物</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li${active_got}><a href="space.jsp?uid=${sGlobal.supe_uid}&do=${do}&view=got"><span>收到的礼物</span></a></li>
		<li${active_sent}><a href="space.jsp?uid=${sGlobal.supe_uid}&do=${do}&view=sent"><span>送出的礼物</span></a></li>
		<li${active_setting}><a href="space.jsp?uid=${sGlobal.supe_uid}&do=${do}&view=setting"><span>显示设置</span></a></li>
		<li class="null"><a href="cp.jsp?ac=gift">赠送礼物</a></li>
	</ul>
	<c:if test="${not empty sGlobal.refer}">
		<div class="r_option">
			<a href="${sGlobal.refer}">&laquo; 返回上一页</a>
		</div>
	</c:if>
</div>
<c:choose>
	<c:when test="${param.view == 'setting'}">
		<div id="setTitle">别人看你主页时，是否在头像下显示送你礼物的链接：</div>
		<form action="cp.jsp?ac=gift" method="POST">
			<div id="setBody">
				<div id="bodyCheck">
					${space.member}
					<input type="checkbox" name="showlink" value="1" ${showgiftlink == '1' ? " checked" : ""}/>是
				</div>
				<div id="bodyPreview">
					<div class="space_avatar">
						<div${!jch:jchEmpty(space.magicstar) && space.magicexpire>sGlobal.timestamp ? " class='magicavatar'" : ""}>
							${jch:avatar2(space.uid, 'big', false, sGlobal, sConfig)}
						</div>
					</div>
					<br />
					<div class="borderbox" style="clear: both;">
						<ul class="spacemenu_list" style="width: 100%; overflow: hidden;">
							<li><img src="image/icon/friend.gif"><a href="javascript:;">加为好友</a></li>
							<li><img src="image/icon/wall.gif"><a href="javascript:;">给我留言</a></li>
							<li><img src="image/icon/poke.gif"><a href="javascript:;">打个招呼</a></li>
							<li><img src="image/icon/pm.gif"><a href="javascript:;">发送消息</a></li>
							<li><img src="image/icon/gift.gif"><a href="javascript:;">送他礼物</a></li>
						</ul>
					</div>
				</div>
				<div id="bodyButton">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<input type="submit" name="settingsubmit" value="保存设置" class="submit" />
				</div>
			</div>
		</form>
	</c:when>
	<c:when test="${param.view == 'sent'}">
		<c:choose>
			<c:when test="${giftnum > 0}">
				<form id="gform" action="cp.jsp?ac=gift" method="POST">
					<div id="listTitle">你共送出了 <strong>${giftnum}</strong> 件礼物</div>
					<div id="listData">
						<c:forEach items="${giftlist}" var="gift">
							<div class="item" onmouseover="$('${gift.gsid}').style.display='block';" onmouseout="$('${gift.gsid}').style.display='none';">
								<div class="itemImg"><img title="${gift.tips}" src="${gift.icon}" width="70" height="70" /></div>
								<div id="${gift.gsid}" class="itemDel"><a href="javascript:delGift('${gift.gsid}','gform');"></a></div>
								<div class="itemDetail">
									<c:choose><c:when test="${gift.anonymous == 1}">匿名</c:when><c:when test="${gift.quiet == 1}">悄悄</c:when><c:otherwise>赠送</c:otherwise></c:choose>给
									<a href="space.jsp?uid=${gift.receiverid}">${gift.receiver}</a><br />
									<span><jch:date dateformat="M月d日" timestamp="${gift.sendtime}" format="1" /></span>
								</div>
							</div>
						</c:forEach>
						<div class="page pagePos">${multi}</div>
					</div>
					<input type="hidden" name="deletesubmit" value="true" />
					<input type="hidden" name="deltype" value="sent" />
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
				</form>
			</c:when>
			<c:otherwise><div id="listTitle">你没有送出过礼物</div></c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${giftnum > 0}">
				<form id="rform" action="cp.jsp?ac=gift" method="POST">
					<div id="listTitle">你共收到了 <strong>${giftnum}</strong> 件礼物<c:if test="${!empty feenum}"><span>&nbsp;(其中 <strong>${feenum}</strong> 个收费礼物)</span></c:if></div>
					<div id="listData">
						<c:forEach items="${giftlist}" var="gift">
							<div class="item" onmouseover="$('${gift.grid}').style.display='block';" onmouseout="$('${gift.grid}').style.display='none';">
								<div class="itemImg"><img title="${gift.tips}" src="${gift.icon}" width="70" height="70" /></div>
								<div id="${gift.grid}" class="itemDel"><a href="javascript:delGift('${gift.grid}','rform');"></a></div>
								<div class="itemDetail ${gift.status == '1' ? 'itemNew' : ''}">
									<c:choose><c:when test="${gift.anonymous == 1}">匿名</c:when><c:otherwise><a href="space.jsp?uid=${gift.senderid}">${gift.sender}</a></c:otherwise></c:choose>
									<c:if test="${gift.quiet == 1}">悄悄</c:if>赠送<br />
									<c:if test="${gift.anonymous != 1}"><a href="cp.jsp?ac=gift&defreceiver=${gift.sender}">回赠</a></c:if><br />
									<span><jch:date dateformat="M月d日" timestamp="${gift.receipttime}" format="1" /></span>
								</div>
							</div>
						</c:forEach>
						<div class="page pagePos">${multi}</div>
					</div>
					<input type="hidden" name="deletesubmit" value="true" />
					<input type="hidden" name="deltype" value="got" />
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
				</form>
			</c:when>
			<c:otherwise><div id="listTitle">你没有收到过礼物</div></c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<script type="text/javascript">
	function delGift(id,formid) {
		if(confirm("你确认删除这件礼物吗？")) {
			$(formid).action += '&id='+id;
			$(formid).submit();
		}
	}
</script>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />