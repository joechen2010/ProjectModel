<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:if test="${op == 'use'}">
	<c:if test="${!frombuy}">
	<h1>使用道具</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	</c:if>
	<div class="toolly popupmenu_inner" id="__magicuse_form_${mid}">
		<form method="post" id="magicuse_form_${mid}" action="magic.jsp?mid=${mid}&idtype=${idtype }&id=${id }">
			<div class="magic_img">
				<img src="image/magic/${mid}.gif" alt="${magic.name }" />
			</div>
			<div class="magic_info">
				<h3>${magic.name }</h3>
				<p class="gray">${magic.description }</p>
				<p>拥有个数: ${usermagic.count }</p>
				<div>
					<h4>设置红包</h4>
					<p>
						您当前积分数为：${space.credit }
					</p>
					<label for="credit">总积分数：</label>
					<input type="text" name="credit" id="credit" size="4" maxlength="4">
					<font class="gray">不能超过当前积分数</font>
					<br />
					<label for="chunk">每份积分：</label>
					<input type="text" name="chunk" id="chunk" size="4" maxlength="3">
					<font class="gray">不能超过 ${magic.custom.maxchunk }</font>
					<c:if test="${leftcredit !=0 }">
					<p style="color:#00663A; font-size:11px; padding:10px 0;">
						提示：您上次埋下的红包还剩余 ${leftcredit } 个积分，将在此次红包设置后返还给您
					</p>
					</c:if>
				</div>
				<p class="btn_line">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<input type="hidden" name="refer" value="${sGlobal.refer }"/>
					<input type="hidden" name="usesubmit" value="1" />
					<input type="button" name="usesubmit_btn" value="使用" class="submit" onclick="ajaxpost('magicuse_form_${mid }', '', 1000)" />
				</p>
			</div>
		</form>
	</div>
</c:if>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>