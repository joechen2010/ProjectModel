<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${op == 'visitor'}">
		<h1>邀请完善联系方式</h1>
		<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>
		<div id="__visitor_${param.uid}" class="toolly">
			<form id="visitor_${param.uid}" action="space.jsp?do=addrbook&op=visitor" method="post">
				<div id="layer_box_tips1" class="layer_box_tips1"><p>是否确定向对方发出完善联系方式的邀请？</p></div>
				<div id="layer_box_buttom" class="layer_box_buttom">
					<input type="hidden" value="${jch:formHash(sGlobal,sConfig,false)}" name="formhash">
					<input type="hidden" value="${sGlobal.refer}" name="refer" />
					<input type="hidden" name="visitorsubmit" value="1" />
					<input type="hidden" name="touid" value="${param.uid}" />
					<input type="button" class="submit" id="visitorsubmit_btn" value="确认" onclick="ajaxpost('visitor_${param.uid}', '', 2000)" />
					<input type="button" onclick="hideMenu();" class="submit" value="取消">
				</div>
			</form>
		</div>
	</c:when>
	<c:when test="${op == 'visitorlist'}">
		<h1>邀请谁完善</h1>
		<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>
		<div id="__visitor_${param.uid}" class="toolly">
			<form id="visitor_${param.uid}" action="space.jsp?do=addrbook&op=visitor" method="post">
				<div class="layer_box_tips2">
					选择分类：
					<select name="tousers">
						<option value="friend">我的好友</option>
						<option value="online">当前在线的好友</option>
						<option value="near">就在我附近的朋友</option>
						<option value="visitor">我的访客</option>
						<option value="trace">我的足迹</option>
					</select>
				</div>
				<div class="layer_box_buttom">
					<input type="hidden" value="${jch:formHash(sGlobal,sConfig,false)}" name="formhash">
					<input type="hidden" value="${sGlobal.refer}" name="refer" />
					<input type="hidden" name="visitorsubmit" value="1" />
					<input type="button" class="submit" id="visitorsubmit_btn" value="确认" onclick="ajaxpost('visitor_${param.uid}', 'disabledVisitor', 2000)" />
					<input type="button" onclick="hideMenu();" class="submit" value="取消">
				</div>
			</form>
		</div>
	</c:when>
	<c:when test="${op == 'fill'}">
		<h1>完善联系方式</h1>
		<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>
		<div id="__fill_${param.uid}" class="toolly">
			<form id="fill_${param.uid}" action="space.jsp?do=addrbook&op=export" method="post">
				<div id="layer_box_tips1" class="layer_box_tips1">
					<p>完善您的手机后，才能使用导出通讯录功能。</p>
					<p>手机：<input type="test" name="mobile" size="25" /></p>
				</div>
				<div id="layer_box_buttom" class="layer_box_buttom">
					<input type="hidden" value="${jch:formHash(sGlobal,sConfig,false)}" name="formhash">
					<input type="hidden" value="${sGlobal.refer}" name="refer" />
					<input type="hidden" name="fillsubmit" value="1" />
					<input type="button" class="submit" id="fillsubmit_btn" value="完善联系方式" onclick="ajaxpost('fill_${param.uid}', '', 2000)" />
					<input type="button" onclick="hideMenu();" class="submit" value="取消">
				</div>
			</form>
		</div>
	</c:when>
	<c:when test="${op == 'export'}">
		<h1>导出通讯录</h1>
		<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>
		<div id="__export_${param.uid}" class="toolly">
			<form id="export_${param.uid}" action="space.jsp?do=addrbook&op=export" method="post">
				<div class="layer_box_tips2">
					选择分类：
					<select name="exportusers">
						<option value="friend">我的好友</option>
						<option value="online">当前在线的好友</option>
						<option value="near">就在我附近的朋友</option>
						<option value="visitor">我的访客</option>
						<option value="trace">我的足迹</option>
					</select>
					<div style="color: #9A9A9A; padding-left: 60px; padding-top: 5px;">通讯录内容是动态变化的，你的同学好友会经<br>常更新自己的联系方式，请注意定期导出。文<br>件导出后将以csv格式保存。</div>
				</div>
				<div id="layer_box_buttom" class="layer_box_buttom">
					<input type="hidden" value="${jch:formHash(sGlobal,sConfig,false)}" name="formhash">
					<input type="hidden" value="${sGlobal.refer}" />
					<input type="hidden" name="exportsubmit" value="1" />
					<input type="submit" class="submit" id="exportsubmit_btn" value="导出" />
					<input type="button" onclick="hideMenu();" class="submit" value="取消">
				</div>
			</form>
		</div>
	</c:when>
	<c:otherwise>
		<!-- 这个是在首页点击通讯录以后显示的“通讯录” -->
		<c:choose>
			<c:when test="${space.self}">
				<div class="searchbar floatright">
					<form method="get" action="space.jsp">
						<input type="hidden" name="do" value="friend">
						<input name="searchkey" value="" size="15" class="t_input" type="text">
						<input name="searchsubmit" value="找好友" class="submit" type="submit">
						<input type="hidden" name="searchmode" value="1" />
					</form>
				</div>
				<h2 class="title"><img src="image/app/addrbook.gif" />通讯录</h2>
				<div class="h_status">
					尽快填写你的
					<a href="cp.jsp?ac=profile&op=base">基本资料</a> 和
					<a href="cp.jsp?ac=profile&op=contact">联系方式</a>，可以让同学好友们更方便的联系到你。
				</div>
			</c:when>
			<c:otherwise><jsp:include page="${jch:template(sConfig, sGlobal, 'space_menu.jsp')}" /></c:otherwise>
		</c:choose>
		<div class="h_status">
			<a href="space.jsp?do=addrbook&addr_type=${addrtype}">全部</a><span class="pipe">|</span>
			<a href="space.jsp?do=addrbook&addr_type=${addrtype}&data_type=mobile">有手机</a>
		</div>
		<div class="addr_content">
			<ul class="tel_list" id="user_list">
				<c:choose>
					<c:when test="${not empty addrlist}">
						<c:forEach items="${addrlist}" var="addr">
							<li class="clearfix">
								<div class="tel_info" id="user_list">
									<div class="friend_head clearfix">
										<p class="friend_name">
											<a href="space.jsp?uid=${addr.uid}" target="_blank">${addr.username}</a><c:if test="${not empty addr.name && addr.username!=addr.name}"><span class="gray">(${addr.name})</span></c:if> 
											<c:if test="${empty addr.mobile}">
												<img src="image/addrbook_invite.gif" />
												<span id="to_visitor_link_${addr.uid}"><a id="to_visitor_${addr.uid}" href="space.jsp?do=addrbook&op=visitor&uid=${addr.uid}" onclick="ajaxmenu(event, this.id, 1)">邀请完善</a></span>
											</c:if>
										</p>
									</div>
									<div class="info_content">
										<div class="address_block">
											<p class="info_1th"><span class="c_tx2">性别：<c:choose><c:when test="${addr.sex == 1}">男</c:when><c:when test="${addr.sex == 2}">女</c:when></c:choose></span></p>
											<p class="info_2th" id="email_${uid}"><span class="c_tx2">邮箱：</span>${addr.email}</p>
											<p class="info_3th" id="addr_${uid}"><span class="c_tx2">家&nbsp;&nbsp;乡：<c:choose><c:when test="${empty birthcity[addr.uid]}">${addr.birthprovince} ${addr.birthcity}</c:when><c:otherwise>${birthcity[addr.uid]}</c:otherwise></c:choose></span></p>
										</div>
										<div class="address_block">
											<p class="info_1th" id="phone_${uid}"><span class="c_tx2">婚恋：<c:choose><c:when test="${empty marry[addr.uid]}"><c:choose><c:when test="${addr.marry == 1}">单身</c:when><c:when test="${addr.marry == 2}">非单身</c:when></c:choose></c:when><c:otherwise>${marry[addr.uid]}</c:otherwise></c:choose></span></p>
											<p class="info_2th" id="mobile_${uid}"><span class="c_tx2">手机：</span><c:choose><c:when test="${empty mobile[addr.uid]}">${addr.mobile}</c:when><c:otherwise><span class="c_tx2">${mobile[addr.uid]}</span></c:otherwise></c:choose></p>
											<p class="info_3th"><span class="c_tx2">所在地：<c:choose><c:when test="${empty residecity[addr.uid]}">${addr.resideprovince} ${addr.residecity}</c:when><c:otherwise>${residecity[addr.uid]}</c:otherwise></c:choose></span></p>
										</div>
									</div>
								</div>
								<div class="avatar48"><a href="space.jsp?uid=${addr.uid}" target="_blank">${jch:avatar1(addr.uid,sGlobal,sConfig)}</a></div>
							</li>
						</c:forEach>
					</c:when>
					<c:otherwise><div class="c_form">没有相关用户列表。</div></c:otherwise>
				</c:choose>
			</ul>
			<div class="page">${multi}</div>
		</div>
		<div id="sidebar" style="width: 150px;">
			<div class="cat">
				<h3>分类</h3>
				<ul>
					<li${active_friend}><a href="space.jsp?do=addrbook&addr_type=friend">我的好友</a></li>
					<li${active_online}><a href="space.jsp?do=addrbook&addr_type=online">当前在线的好友</a></li>
					<li${active_near}><a href="space.jsp?do=addrbook&addr_type=near">就在我附近的朋友</a></li>
					<li${active_visitor}><a href="space.jsp?do=addrbook&addr_type=visitor">我的访客</a></li>
					<li${active_trace}><a href="space.jsp?do=addrbook&addr_type=trace">我的足迹</a></li>
				</ul>
			</div>
			<div class="cat">
				<h3>通讯录功能</h3>
				<ul class="post_list line_list">
					<li><img src="image/addrbook_export_addrbook.gif"/> <a id="to_export_${sGlobal.supe_uid}" href="space.jsp?do=addrbook&op=export&uid=${sGlobal.supe_uid}" onclick="ajaxmenu(event, this.id, 1)">导出通讯录</a></li>
					<li><img src="image/addrbook_invite_fill.gif"/> <a id="to_visitor_list" href="space.jsp?do=addrbook&op=visitorlist&uid=list" onclick="ajaxmenu(event, this.id, 1)">邀请大家填写</a></li>
				</ul>
			</div>
		</div>
		<script type="text/javascript">
		function disabledVisitor(id) {
			var ids = explode('_', id);
			var uid = ids[1];
			if($('to_visitor_link_'+uid)) {
				$('to_visitor_link_'+uid).innerHTML = "已发出邀请信息";
			}
		}
	</script>
	</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />