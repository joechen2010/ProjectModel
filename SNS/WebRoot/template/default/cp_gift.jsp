<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<script language="javascript" type="text/javascript" src="source/script_gift.js"></script>
<h2 class="title"><img src="image/app/gift.gif" />礼物</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li class="active"><a href="cp.jsp?ac=gift"><span>赠送礼物</span></a></li>
		<li><a href="space.jsp?do=gift"><span>返回礼物列表</span></a></li>
	</ul>
	<c:if test="${not empty sGlobal.refer}">
	<div class="r_option">
		<a  href="${sGlobal.refer}">&laquo; 返回上一页</a>
	</div>
	</c:if>
</div>
<form action="cp.jsp?ac=gift" method="POST">
	<div id="giftContent">
		<div id="giftTo">
			<div class="subTitle">赠送给</div>
			<script type="text/javascript" src="source/script_autocomplete.js"></script>
			<input type="text" id="username" name="username" value="${defreceiver}" style="width: 396px;" class="t_input" tabindex="1" <c:if test="${not empty friends}"> onclick="auc.handleEvent(this.value ,event);" onkeyup="auc.handleEvent(this.value ,event);" onkeydown="closeOpt(username,event);inputKeyDown(event);" autocomplete="off" </c:if> />
			<c:if test="${not empty friends}">
				<div id="username_menu" class="ajax_selector" onclick="$('username_menu').style.display='none';" style="display:none">
					<div class="ajax_selector_option" style="width: 396px; height: 100px;">
						<a href="javascript:;" onclick="$('username_menu').style.display='none';" class="float_del" style="margin-right: 5px;">a</a>
						<ul id="friendlist" class="blocklink">
							<c:forEach items="${friends}" var="value">
								<li>${value.username}</li>
							</c:forEach>
						</ul>
					</div>
				</div>
				<script type="text/javascript">
					var close = true;
					var auc = new sAutoComplete("auc", "username_menu", "friendlist", "username");
					auc.addItem('${friendstr}');
					function closeOpt(key,evt) {
						if(evt.keyCode==9) {
							$('username_menu').style.display='none';
						}
					}
					function inputKeyDown(event) {
						if(event.keyCode == 13){
							doane(event);
						}
					}
				</script>
			</c:if>
		</div>
		<div id="giftBox">
			<div class="subTitle">选择礼物</div>
			<div id="giftBoxHeader">
				<ul>
					<c:forEach items="${categories}" var="c" varStatus="vs">
					<li><a id="${c.typeid}" href="javascript:selectCategory('${c.typeid}');" onfocus="this.blur();" <c:if test="${vs.index == 0}">class="active"</c:if>>${c.typename}</a>
					</c:forEach>
				</ul>
				<script type="text/javascript">
				</script>
				<div id="feesAdd">
					余额：
					<span><strong id="virtualCurrency"></strong>&nbsp;J币</span>&nbsp;
					<a target="_blank" href="#">立即充值&gt;&gt;</a>
				</div>
			</div>
			<div id="giftBoxContent" class="borderbox">
				<div id="feesCategory">
					<ul></ul>
				</div>
				<div id="giftMemo">
					<div id="feeOrder">排序：最新 <span>|</span> <a href="#">人气</a></div>
					<div id="advTips"></div>
				</div>
				<div id="giftData">
				</div>
				<div style="clear:both;"></div>
			</div>
		</div>
		<div id="giftPs">
			<div class="subTitle">附言：</div>
			<a href="###" id="doingface" onclick="showFace(this.id, 'message');" onfocus="this.blur();"><img src="image/facelist.gif" align="absmiddle" /></a> 
			<span id="remainderTips">您还可以输入 <strong id="remainder">200</strong> 个字符</span><br>
			<textarea id="message" name="message" onkeyup="textCounter(this, 'remainder', 200);" onkeydown="ctrlEnter(event, 'giftsubmit');"></textarea>
			<div id="giftOption">
				<div><input name="quiet" value="1" type="checkbox">悄悄地送 <span class="siteDescription"> (不让其他人知道是你送的)</span> </div>
				<div><input name="anonymous" value="1" type="checkbox">匿名赠送 <span class="siteDescription"> (不让接收礼物的人知道是你送的)</span> </div>
				<div id="regular">
					<input name="timed  " value="1" type="checkbox">定时发送
					<select name="month">
						<option value="1">01</option>
						<option value="2">02</option>
						<option value="3">03</option>
						<option value="4">04</option>
						<option value="5">05</option>
						<option value="6">06</option>
						<option value="7">07</option>
						<option value="8">08</option>
						<option value="9">09</option>
						<option value="10">10</option>
						<option value="11">11</option>
						<option value="12">12</option>
					</select>月
					<select name="day">
						<option value="1">01</option>
						<option value="2">02</option>
						<option value="3">03</option>
					</select>日
					<select name="hour">
						<option value="1">01</option>
						<option value="2">02</option>
						<option value="3">03</option>
					</select>点
					<select name="minute">
						<option value="1">01</option>
						<option value="2">02</option>
						<option value="3">03</option>
					</select>分 发送
				</div>
				<c:if test="${jch:checkPerm(pageContext.request,pageContext.response,'seccode')}">
				<div id="seccode">
					<c:choose>
					<c:when test="${sConfig.questionmode==1}">
						<span>请回答验证问题：&nbsp;${jch:question(pageContext.request,pageContext.response)}</span>
						<span><input type="text" id="seccode" name="seccode" value="" size="15" class="t_input" /></span>
					</c:when>
					<c:otherwise>
						<span>请填写验证码</span>
						<span><script>seccode();</script></span>
						<p>请输入上面的4位字母或数字，看不清可<a href="javascript:updateseccode()">更换一张</a></p>
						<span><input type="text" id="seccode" name="seccode" value="" size="15" class="t_input" /></span>
					</c:otherwise>
					</c:choose>
				</div>
				</c:if>
			</div>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal, sConfig, false)}" />
			<input id="giftsubmit_btn" name="giftsubmit" value="送出礼物" type="submit" class="submit" />
		</div>
	</div>
</form>
<script type="text/javascript">
	var gift_category_active = "${firstcate}";
	selectCategory("${firstcate}");
</script>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>