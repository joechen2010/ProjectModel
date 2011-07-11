<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:choose>
<c:when test="${!empty param.reqtype}">

	<c:choose>
	<c:when test="${param.reqtype == 'tips'}">
		<c:choose>
		<c:when test="${advgiftcount > 0}">你还有&nbsp;<span>${advgiftcount}</span>&nbsp;次赠送高级礼物的机会</c:when>
		<c:otherwise>你暂不能赠送高级礼物&nbsp;<a href="help.jsp?ac=gift"  target="_blank">如何获得高级礼物使用机会</a></c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${param.reqtype == 'balance'}">
		${balance}
	</c:when>
	<c:when test="${param.reqtype == 'feescate'}">
		<ul>
			<li><a id="feesAll" <c:choose><c:when test="${gifttype == 'feeGift' || gifttype == 'feesAll'}">class="active"</c:when><c:otherwise>href="javascript:selectCategory('feesAll');"</c:otherwise></c:choose>>全部</a></li>
			<c:forEach items="${feecatelist}" var="cate">
			<li><a id="${cate.typeid}" <c:choose><c:when test="${cate.typeid == gifttype}">class="active"</c:when><c:otherwise>href="javascript:selectCategory('${cate.typeid}');"</c:otherwise></c:choose>>${cate.typename}</a></li>
			</c:forEach>
		</ul>
	</c:when>
	</c:choose>

</c:when>
<c:when test="${param.view == 'me'}">
	<div id="space_gift" class="feed">
		<h3 class="feed_header">
			<a href="cp.jsp?ac=gift" class="r_option" target="_blank">赠送礼物</a>
			礼物(共 ${giftnum} 个<c:if test="${feenum > 0}">，其中 ${feenum} 个收费礼物</c:if>)
		</h3>
		<c:choose>
		<c:when test="${giftnum > 0}">
		<div id="listData">
			<c:forEach items="${giftlist}" var="gift">
			<div class="item">
				<div class="itemImg"><img title="${gift.tips}" src="${gift.icon}" width="70" height="70"/></div>
				<div <c:choose><c:when test="${gift.status == '1'}">class="itemDetail itemNew"</c:when><c:otherwise>class="itemDetail"</c:otherwise></c:choose>>
					<c:choose>
						<c:when test="${gift.anonymous == 1}">
							匿名<c:if test="${gift.quiet == 1}">悄悄</c:if>赠送<br/>
						</c:when>
						<c:otherwise>
							<c:choose>
							<c:when test="${gift.quiet == 1}">
								<c:choose>
								<c:when test="${space.self}">
								<a href="space.jsp?uid=${gift.senderid}">${gift.sender}</a>悄悄赠送<br/>
								</c:when>
								<c:otherwise>
								某人悄悄赠送<br/>
								</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<a href="space.jsp?uid=${gift.senderid}">${gift.sender}</a>赠送<br/>
							</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<span><jch:date dateformat="M月d日" timestamp="${gift.receipttime}" format="1"/></span>	
				</div>
			</div>
			</c:forEach>
			<div class="page pagePos">${multi}</div>
		</div>
		</c:when>
		<c:otherwise>
			<div id="listTitle">还没有收到过礼物</div>
		</c:otherwise>
		</c:choose>
	</div>

</c:when>
<c:otherwise>

	<c:forEach items="${giftlist}" var="gift">
	<div class="item">
		<div class="itemSel">
			<input type="radio" name="giftid" value="${gift.giftid}" />
		</div>
		<div class="itemDetail">
			<img src="${gift.icon}" title="${empty gift.tips ? gift.giftname : gift.tips}" width="70" height="70" />
			<c:if test="${gift.price > 0}">
			<span class="detailName">${gift.giftname}</span>
			<span class="detailPrice">价格：${gift.price}</span>
			</c:if>
		</div>
	</div>
	</c:forEach>
	
	<c:if test="${!empty multi}">
	<div class="page pagePos">${multi}</div>
	</c:if>
	
</c:otherwise>
</c:choose>
