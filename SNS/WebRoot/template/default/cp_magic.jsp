<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:choose>

<c:when test="${op == 'buy'}">

	<h1>购买道具</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	<div class="toolly" id="__magicbuy_${mid }">
	<c:choose>
	<c:when test="${ac == 'magic'}">
		<form id="magicbuy_${mid }" action="cp.jsp?ac=magic&mid=${mid }&op=buy" method="post">
	</c:when>
	<c:otherwise>
		<form id="magicbuy_${mid }" action="magic.jsp?mid=${mid }&op=buy&idtype=${idtype }&id=${id }${extra}" method="post">
	</c:otherwise>
	</c:choose>
			<div class="magic_img">
				<img src="image/magic/${mid }.gif" alt="${magic.name }" />
			</div>
			<div class="magic_info">
				<h3>${magic.name }</h3>
				<p class="gray">${magic.description }</p>
				<c:if test="${magic.experience != null && magic.experience != 0 }">
				<p>增加经验: <span>${magic.experience }</span></p>
				</c:if>
				<p>
					道具单价: <span>${magic.charge }</span> 积分
					<c:choose>
					<c:when test="${discount > 0}">
					（享受 ${discount } 折优惠 <span>${charge }</span> 积分 ）
					</c:when>
					
					<c:when test="${discount < 0}">
					（享受 <span>免费</span> 折扣 ）
					</c:when>
					</c:choose>
				</p>
				<p>现有库存: <span>${magicstore.storage }</span> 个</p>
				<p>购买数量: <input class="t_input" type="text" name="buynum" value="1" style="width:40px;" /> 个（当前最多可购买 ${magicstore.maxbuy } 个）</p>
				<c:if test="${coupon.count != null && coupon.count != 0}">
				<p>用代金券: <input class="t_input" type="text" name="coupon" value="0" style="width:40px;" /> 张（每张抵用 100 积分，拥有 ${coupon.count } 张）</p>
				</c:if>
				<p class="btn_line">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<input type="hidden" name="refer" value="${sGlobal.refer }"/>
					<input type="hidden" name="buysubmit" value="1" />
				<c:choose>
				<c:when test="${sGlobal.inajax != null && sGlobal.inajax != 0}">
					<c:choose>
					<c:when test="${ac == 'magic'}">
					<input type="button" class="submit" id="buysubmit_btn" value="购买" onclick="ajaxpost('magicbuy_${mid }', 'magicBought', 2000)" />
					</c:when>
					
					<c:otherwise>
					<input type="button" class="submit" id="buysubmit_btn" value="购买" onclick="ajaxpost('magicbuy_${mid }')" />
					</c:otherwise>
					</c:choose>
				</c:when>
				
				<c:otherwise>
					<input type="submit" class="submit" id="buysubmit_btn" value="购买">
				</c:otherwise>
				</c:choose>
				</p>
			</div>
		</form>
	</div>
	
</c:when>

<c:when test="${ op == 'present'}">

	<h1>赠送道具</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	<div class="popupmenu_inner" id="__magicpresent_${mid }">
		<form id="magicpresent_${mid }" action="cp.jsp?ac=magic&mid=${mid }" method="post">
			<p>
				要赠送的道具：${magic.name }
			</p>
			<p>
				好友的用户名：
				<input type="text" name="fusername" />
			</p>
			<p class="btn_line">
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
				<input type="hidden" name="refer" value="${sGlobal.refer }"/>
				<input type="hidden" name="presentsubmit" value="1" />
				<c:choose>
				<c:when test="${sGlobal.inajax != null && sGlobal.inajax != 0}">
					<input type="button" class="submit" name="presentsubmit_btn" value="赠送" onclick="ajaxpost('magicpresent_${mid }', 'magicPresent', 2000)" />
				</c:when>
				<c:otherwise>
					<input type="submit" class="submit" name="presentsubmit_btn" value="赠送">
				</c:otherwise>
				</c:choose>
			</p>
		</form>
	</div>
	
</c:when>

<c:when test="${op == 'showusage'}">

	<h1>道具使用示例图</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	<div class="popupmenu_inner">
		<img src="image/magic/usage/${mid }.gif" />		
	</div>
	
</c:when>

<c:when test="${ op == 'appear'}">

	<h1>恢复在线状态</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	<div class="popupmenu_inner" id="__appearform">
	<form action="cp.jsp?ac=magic&op=${op }" method="post" id="appearform">
		<p>
			您确定要取消隐身效果，恢复在线状态吗？
		</p>
		<p class="btn_line">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			<input type="hidden" name="refer" value="${sGlobal.refer }"/>
			<input type="hidden" name="appearsubmit" value="1" />
			<input type="submit" class="submit" value="确定" />
		</p>
	</form>
	</div>

</c:when>
<c:when test="${ op == 'retrieve'}">

	<h1>回收红包</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	<div class="popupmenu_inner" id="__retrieveform">
	<form action="cp.jsp?ac=magic&op=${op }" method="post" id="retrieveform">
		<p>
			红包当前剩余积分 ${leftcredit } ，您确定要回收吗？
		</p>
		<p class="btn_line">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			<input type="hidden" name="refer" value="${sGlobal.refer }"/>
			<input type="hidden" name="retrievesubmit" value="1" />
			<input type="submit" class="submit" value="确定" />
		</p>
	</form>
	</div>

</c:when>

<c:when test="${ op == 'cancelsuperstar' || op == 'cancelflicker' || op == 'cancelcolor' || op == 'cancelframe' || op == 'cancelbgimage' }">

	<h1>取消道具效果</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	<div class="popupmenu_inner" id="__cancelform">
	<form action="cp.jsp?ac=magic&op=${op }&id=${id }&idtype=${idtype }" method="post" id="cancelform">
		<p>
			您确定要取消道具 ${globalMagic[mid] } 的效果吗？
		</p>
		<p class="btn_line">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			<input type="hidden" name="refer" value="${sGlobal.refer }"/>
			<input type="hidden" name="cancelsubmit" value="1" />
			<input type="submit" class="submit" value="确定" />
		</p>
	</form>
	</div>
	
</c:when>

<c:otherwise>

	<h2 class="title"><img src="image/icon/magic.gif">道具中心</h2>
	<div class="tabs_header">
		<ul class="tabs">
			<li${actives.store }><a href="cp.jsp?ac=magic&view=store"><span>道具商店</span></a></li>
			<li${actives.me }><a href="cp.jsp?ac=magic&view=me"><span>我的道具</span></a></li>
			<li${actives.log }><a href="cp.jsp?ac=magic&view=log"><span>道具记录</span></a></li>
		</ul>
	</div>

	<div style="float:none;">

	<c:choose>
	<c:when test="${param.view == 'me'}">
			
		<c:if test="${mid != null && mid != ''}">
		<p class="notice">
			当前只显示与你操作相关的单个道具，
			<a href="cp.jsp?ac=magic&view=${param.view }">点击此处查看全部道具</a>
		</p>
		<p>&nbsp;</p>
		</c:if>

		<c:choose>
		<c:when test="${list != null }">
		<ul id="magiclist" class="magic_list">
		<c:forEach items="${list}" var="key_value">
			<li id="magic_${key_value.key }">
				<div class="magic_img">
					<img src="image/magic/${key_value.key}.gif" alt="${magics[key_value.key].name }" />
				</div>
				<div class="magic_info">
					<h3>${magics[key_value.key].name }</h3>
					<p class="gray">
					${magics[key_value.key].description }
					</p>
					<p>
						<a id="a_present_${key_value.key }" href="cp.jsp?ac=magic&op=present&mid=${key_value.key }" onclick="ajaxmenu(event, this.id, 1)" class="m_button<c:if test="${key_value.key == 'license'}"> m_off</c:if>">赠送</a>
						拥有 <span id="magiccount_${key_value.key }">${key_value.value.count }</span> 个
					</p>
				</div>
			</li>
		</c:forEach>
		</ul>
		</c:when>
		
		<c:otherwise>
		<p>您还没有购买任何道具，<a href="cp.jsp?ac=magic&view=store">去道具商店看看</a></p>
		</c:otherwise>
		</c:choose>
	</c:when>
	
	<c:when test="${param.view == 'log'}">

		<div class="h_status">
		查看：
		<a ${types.in } href="cp.jsp?ac=magic&view=${param.view }&type=in">获得记录</a>
		<span class="pipe">|</span>
		<a ${types.present } href="cp.jsp?ac=magic&view=${param.view }&type=present">赠送记录</a>
		<span class="pipe">|</span>
		<a ${types.out } href="cp.jsp?ac=magic&view=${param.view }&type=out">使用记录</a>
		</div>
		
		<c:choose>
		<c:when test="${gType == 'in'}">
			<c:choose>
			<c:when test="${list != null}">
			<ul class="line_list">
				<c:forEach items="${list}" var="value">
				<li>
					<c:choose>
					<c:when test="${value.type == '3'}">
					升级获得
					</c:when>
					
					<c:when test="${value.type == '2'}">
					获得了
						<c:choose>
						<c:when test="${value.fromid != null && value.fromid != 0 }">
						<a href="space.jsp?uid=${value.fromid}" target="_blank">${sNames[value.fromid] }</a>
						</c:when>
						
						<c:otherwise>
						管理员
						</c:otherwise>
						</c:choose>
					赠送的
					</c:when>
					
					<c:otherwise>
					购买了
					</c:otherwise>
					</c:choose>
					<a href="cp.jsp?ac=magic&view=store&mid=${value.mid }" target="_blank">
						${globalMagic[value.mid] }
					</a>
					${value.count }个
					<span class="gray">(${value.dateline})</span>
				</li>
				</c:forEach>
			</ul>
			<div class="page">${multi }</div>
			</c:when>
				
			<c:otherwise>
			<p>您近期没有道具收入记录</p>
			</c:otherwise>
			</c:choose>
		</c:when>
			
		<c:when test="${gType == 'present'}">
			<c:choose>
			<c:when test="${list != null}">
			<ul class="line_list">
				<c:forEach items="${list}" var="value">
				<li>
					向
					<a href="space.jsp?uid=${value.uid }">${sNames[value.uid] }</a>
					赠送了
					<a href="cp.jsp?ac=magic&view=store&mid=${value.mid }" target="_blank">
						${globalMagic[value.mid] }
					</a>
					<span class="gray">(${value.dateline})</span>
				</li>
				</c:forEach>
			</ul>
			<div class="page">${multi }</div>
			</c:when>
			
			<c:otherwise>
			<p>您近期没有向他人赠送道具的记录</p>
			</c:otherwise>
			</c:choose>
		</c:when>
				
		<c:otherwise>
			<c:choose>
			<c:when test="${list != null}">
			<ul class="line_list">
				<c:forEach items="${list}" var="value">
				<li>
					使用了
					<a href="cp.jsp?ac=magic&view=store&mid=${value.mid}" target="_blank">
						${globalMagic[value.mid] }
					</a>
					${value.count } 次
					<c:choose>
					<c:when test="${value.mid == 'invisible' || value.mid == 'superstar'}">
					; &nbsp;失效时间：${value.expire}
					</c:when>
					
					<c:when test="${value.mid == 'gift'}">
					; &nbsp;剩余积分数：${value.data.left }
					</c:when>
					<%-- 
					<c:when test="${value.mid == 'superstar'}">
					; &nbsp;失效时间：${value.expire}
					</c:when>
					--%>
					</c:choose>
				
					<span class="gray">(${value.dateline})</span>
				</li>
				</c:forEach>
			</ul>
			<div class="page">${multi }</div>
			</c:when>
			
			<c:otherwise>
			<p>您近期没有道具使用记录</p>
			</c:otherwise>
			</c:choose>
		</c:otherwise>
		</c:choose>
	</c:when>
	
	<c:otherwise>
		<div class="h_status">
		排序：
		<a ${orders.default } href="cp.jsp?ac=magic&view=${view }&order=defalut">默认</a>
		<span class="pipe">|</span>
		<a ${orders.hot } href="cp.jsp?ac=magic&view=${view }&order=hot">热门</a>
		</div>

		<c:if test="${mid != null && mid != ''}">
		<p class="notice">
			当前只显示与你操作相关的单个道具，
			<a href="cp.jsp?ac=magic&view=${param.view }">点击此处查看全部道具</a>
		</p>
		<p>&nbsp;</p>
		</c:if>

		<ul id="magiclist" class="magic_list">
		<c:forEach items="${list}" var="key_value">
			<li id="magic_${key_value.key }">
				<div class="magic_img">
					<a id="a_i_buy_${key_value.key }" href="cp.jsp?ac=magic&op=buy&mid=${key_value.key }" onclick="ajaxmenu(event, this.id, 1)">
					<img src="image/magic/${key_value.key }.gif" alt="${magics[key_value.key].name }" />
					</a>
				</div>
				<div class="magic_info">
					<h3>
						${magics[key_value.key].name }
						<c:if test="${param.order == 'hot'}">
						<small class="gray" style="margin-left:10px;">已售出 ${key_value.value.sellcount } 件</small>
						</c:if>
					</h3>
					<p class="gray">${magics[key_value.key].description }</p>
					<p>
						<c:choose>
						<c:when test="${jch:inArray(magics[key_value.key].forbiddengid,space.groupid) || jch:inArray(blacklist,mid)}">
						<a id="a_buy_${key_value.key }" href="cp.jsp?ac=magic&op=buy&mid=${key_value.key }" onclick="ajaxmenu(event, this.id, 1)" class="m_button m_off">不能购买</a><span>${magics[key_value.key].charge }</span> 积分/个
						</c:when>
						
						<c:otherwise>
						<a id="a_buy_${key_value.key }" href="cp.jsp?ac=magic&op=buy&mid=${key_value.key }" onclick="ajaxmenu(event, this.id, 1)" class="m_button">购买</a><span>${magics[key_value.key].charge }</span> 积分/个
						</c:otherwise>
						</c:choose>
					</p>
				</div>
			</li>
		</c:forEach>
		</ul>
	</c:otherwise>	
	</c:choose>

	</div>
	<script type="text/javascript">
	<!--
	function magicBought(id, result) {
		var ids = explode('_', id);
		var mid = ids[1];
		if($('a_buy_'+mid)) {
			$('a_buy_'+mid).innerHTML = '继续购买';
		}
	}
	function magicPresent(id, result) {
		var ids = explode('_', id);
		var mid = ids[1];
		if($('a_present_'+mid)) {
			$('a_present_'+mid).innerHTML = '继续赠送';
		}
		if($('magiccount_'+mid)) {
			$('magiccount_'+mid).innerHTML = parseInt($('magiccount_'+mid).innerHTML) - 1;
		}
	}
	-->
	</script>

</c:otherwise>

</c:choose>

<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>