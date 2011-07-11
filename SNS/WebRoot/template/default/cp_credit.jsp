<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_header.jsp')}" />
<div class="l_status c_form">
	<a href="cp.jsp?ac=credit"${cat_actives_base}>我的积分</a><span class="pipe">|</span>
	<a href="cp.jsp?ac=credit&op=rule"${cat_actives_rule}>积分规则</a><span class="pipe">|</span>
	<a href="cp.jsp?ac=credit&op=usergroup"${cat_actives_usergroup}>用户组规则</a><span class="pipe">|</span>
	<a href="cp.jsp?ac=credit&op=exchange"${cat_actives_exchange}>积分兑换</a>
</div>
<div class="c_form">
	<c:choose>
		<c:when test="${empty param.op}">
			<table cellspacing="0" cellpadding="0" class="formtable">
				<tr>
					<th width="150">经验值:</th>
					<td><span style="color: green; font-size: 14px;">${space.experience}</span> ${star}</td>
				</tr>
				<tr>
					<th width="150">&nbsp;</th>
					<td class="gray">
						经验每满 <strong>${sConfig.starcredit}</strong> 个，就会增加一个图标 <img src="image/star_level1.gif" align="absmiddle"><br>
						每满 <strong>${sConfig.starlevelnum}</strong> 个当前图标就升级为 <strong>1</strong> 个高一等级图标<br>
						图标等级由低到高为：<c:forEach begin="1" end="10" step="1" var="i"><img src="image/star_level${i}.gif"></c:forEach>
					</td>
				</tr>
				<tr>
					<th width="150">用户组:</th>
					<td><span${color}>${space.grouptitle}</span> ${icon}</td>
				</tr>
				<tr>
					<th width="150">积分数:</th>
					<td><img src="image/credit.gif"> <span style="color: red; font-size: 14px;">${space.credit}</span> <span class="gray">(<a href="space.jsp?do=top&view=credit">查看排名</a>)</span></td>
				</tr>
				<tr>
					<th>访问量:</th>
					<td>${space.viewnum} <span class="gray">(<a href="space.jsp?do=top&view=viewnum">查看排名</a>)</span></td>
				</tr>
				<tr>
					<th>创建时间:</th>
					<td>${dateline}</td>
				</tr>
				<tr>
					<th>上次登录:</th>
					<td>${lastlogin}</td>
				</tr>
				<tr>
					<th>最后更新:</th>
					<td>${updatetime}</td>
				</tr>
				<tr>
					<th>空间容量:</th>
					<td>最大空间 ${maxattachsize}, 已用 ${space.attachsize} (${percent}%)</td>
				</tr>
				<c:if test="${space.haveattachsize > 0}">
					<tr>
						<th>剩余空间:</th>
						<td>${space.haveattachsize}</td>
					</tr>
				</c:if>
			</table>
			<br>
			<table cellspacing="0" cellpadding="0" class="listtable">
				<caption>
					<h2>获得积分历史</h2>
					<p>显示你获得积分的动作列表，奖励积分值与经验值只记录最后一次获得的奖励</p>
				</caption>
				<thead>
					<tr class="title">
						<td>动作名称</td>
						<td align="center">总次数</td>
						<td align="center">周期次数</td>
						<td align="center">单次积分</td>
						<td align="center">单次经验值</td>
						<td align="center">最后奖励时间</td>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${!empty list}">
						<c:forEach items="${list}" var="value" varStatus="st">
							<tr${st.index % 2 == 0 ? " class='line'" : ""}>
								<td><a href="cp.jsp?ac=credit&op=rule&rid=${value.rid}">${value.rulename}</a></td>
								<td align="center">${value.total}</td>
								<td align="center">${value.cyclenum}</td>
								<td align="center">${value.credit}</td>
								<td align="center">${value.experience}</td>
								<td align="center">${value.dateline}</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise><tr><td colspan="6">暂时没有获务任何积分</td></tr></c:otherwise>
				</c:choose>
				<c:if test="${!empty multi}"><tr><td colspan="6"><div class="page">${multi}</div></td></tr></c:if>
			</table>
		</c:when>
		<c:when test="${param.op == 'exchange'}">
			<form method="post" action="cp.jsp?ac=credit&op=exchange">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<caption>
						<h2>积分兑换</h2>
						<p>您可以将自己的积分兑换到本站其他的应用（比如论坛）里面。</p>
					</caption>
					<tr>
						<th width="150">目前您的积分数:</th>
						<td>$space[credit]</td>
					</tr>
					<tr>
						<th><label for="password">密码</label>:</th>
						<td><input type="password" name="password" class="t_input" /></td>
					</tr>
					<tr>
						<th>支出积分:</th>
						<td><input type="text" id="amount" name="amount" value="0" class="t_input" onkeyup="calcredit();" /></td>
					</tr>
					<tr>
						<th>兑换成:</th>
						<td>
							<input type="text" id="desamount" value="0" class="t_input" disabled />
							&nbsp;&nbsp;
							<select name="tocredits" id="tocredits" onChange="calcredit();">
								<!--{loop $_CACHE['creditsettings'] $id $ecredits}-->
								<!--{if $ecredits[ratio]}-->
								<option value="$id" unit="$ecredits[unit]" title="$ecredits[title]" ratio="$ecredits[ratio]">$ecredits[title]</option>
								<!--{/if}-->
								<!--{/loop}-->
							</select>
						</td>
					</tr>
					<tr>
						<th>兑换比率:</th>
						<td>
							<span class="bold">1</span>&nbsp;
							<span id="orgcreditunit">积分</span><span id="orgcredittitle"></span>&nbsp;兑换&nbsp;
							<span class="bold" id="descreditamount"></span>&nbsp;
							<span id="descreditunit"></span><span id="descredittitle"></span>
						</td>
					</tr>
					<tr>
						<th>&nbsp;</th>
						<td><input type="submit" name="exchangesubmit" value="兑换积分" class="submit"></td>
					</tr>
				</table>
				<input type="hidden" name="formhash" value="<!--{eval echo formhash();}-->" />
			</form>
			<script type="text/javascript">
				function calcredit() {
					tocredit = $('tocredits')[$('tocredits').selectedIndex];
					$('descreditunit').innerHTML = tocredit.getAttribute('unit');
					$('descredittitle').innerHTML = tocredit.getAttribute('title');
					$('descreditamount').innerHTML = Math.round(1/tocredit.getAttribute('ratio') * 100) / 100;
					$('amount').value = $('amount').value.toInt();
					if($('amount').value != 0) {
						$('desamount').value = Math.floor(1/tocredit.getAttribute('ratio') * $('amount').value);
					} else {
						$('desamount').value = $('amount').value;
					}
				}
				String.prototype.toInt = function() {
					var s = parseInt(this);
					return isNaN(s) ? 0 : s;
				}
				calcredit();
			</script>
		</c:when>
		<c:when test="${param.op == 'rule'}">
			<c:if test="${!empty list}">
				<table cellspacing="0" cellpadding="0" class="listtable">
					<caption>
						<h2>积分奖励规则</h2>
						<p>进行以下事件动作，会得到积分奖励。不过，在一个周期内，您最多得到的奖励次数有限制。</p>
					</caption>
					<tr class="title">
						<td>动作名称</td>
						<td align="center">周期范围</td>
						<td align="center">周期内最多奖励次数</td>
						<td align="center" width="100">单次奖励分值</td>
					</tr>
					<c:forEach items="${list}" var="value" varStatus="st">
						<tr${st.index % 2 == 0 ? " class='line'" : ""}>
							<td>${value.rulename}</td>
							<td align="center">${value.cycletype}</td>
							<td align="center">${value.rewardnum == 0 ? "不限次数" : value.rewardnum}</td>
							<td align="center">${value.credit}</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
			<c:if test="${!empty list2}">
				<br>
				<table cellspacing="0" cellpadding="0" class="listtable">
					<caption>
						<h2>积分扣减规则</h2>
						<p>以下事件动作发生时，会扣减积分。其中，自己发布的信息自己删除，不扣减积分，被管理员删除才会扣减积分。</p>
					</caption>
					<tr class="title">
						<th>动作名称</th>
						<th align="center">单次扣减分值</th>
					</tr>
					<c:forEach items="${list2}" var="value" varStatus="st">
						<tr${st.index % 2 == 0 ? " class='line'" : ""}>
							<td>${value.rulename}</td>
							<td align="center" width="100">${value.credit}</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
		</c:when>
		<c:when test="${param.op == 'usergroup'}">
			<table cellspacing="0" cellpadding="0" class="listtable">
				<caption>
					<h2>普通用户组</h2>
					<p>随着您的经验值的提高，您的用户组所拥有的权限也会越多。</p>
				</caption>
				<tr class="title">
					<th width="150">用户组名</th>
					<td>经验值范围</td>
				</tr>
				<c:forEach items="${groups}" var="value">
					<tr>
						<th><span${value.color}>${value.grouptitle}</span>${value.icon}</th>
						<td>${value.explower} ~ ${value.exphigher}</td>
					</tr>
				</c:forEach>
			</table>
			<table cellspacing="0" cellpadding="0" class="listtable">
				<caption>
					<h2>特殊用户组</h2>
					<p>不受经验值限制。</p>
				</caption>
				<tr class="title">
					<th width="150">用户组名</th>
					<td>经验值范围</td>
				</tr>
				<c:forEach items="${s_groups}" var="value">
					<tr>
						<th><span${value.color}>${value.grouptitle}</span>${value.icon}</th>
						<td>-</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
	</c:choose>
</div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />