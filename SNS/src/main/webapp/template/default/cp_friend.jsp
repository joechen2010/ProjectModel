<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>

<c:if test="${op == 'syn' || op == 'find' || op == 'search' || op == 'group' || op == 'request'}">
<div class="searchbar floatright">
<form method="get" action="cp.jsp">
	<input name="searchkey" value="" size="15" class="t_input" type="text">
	<input name="searchsubmit" value="找人" class="submit" type="submit">
	<input type="hidden" name="searchmode" value="1" />
	<input type="hidden" name="ac" value="friend" />
	<input type="hidden" name="op" value="search" />
</form>
</div>
<h2 class="title"><img src="image/icon/friend.gif">好友</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li><a href="space.jsp?uid=${space.uid}&do=friend"><span>好友列表</span></a></li>
		<li${actives.search}><a href="cp.jsp?ac=friend&op=search"><span>查找好友</span></a></li>
		<li${actives.find}><a href="cp.jsp?ac=friend&op=find"><span>可能认识的人</span></a></li>
		<c:if test="${jch:jchEmpty(sConfig.closeinvite)}">
		<li><a href="cp.jsp?ac=invite"><span>邀请好友</span></a></li>
		</c:if>
		<li${actives.request}><a href="cp.jsp?ac=friend&op=request"><span>好友请求</span></a></li>
		<c:if test="${op=='group'}">
		<li${actives.group}><a href="cp.jsp?ac=friend&op=group"><span>好友分组</span></a></li>
		</c:if>
	</ul>
</div>
</c:if>

<c:choose>
<c:when test="${op=='ignore'}">

<h1>忽略好友</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__friendform_${uid}">
<form method="post" id="friendform_${uid}" name="friendform_${uid}" action="cp.jsp?ac=friend&op=ignore&uid=${uid}&confirm=1">
	<p>确定忽略好友关系吗？</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}">
		<c:choose>
		<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
		<input type="hidden" name="friendsubmit" value="true" />
		<button name="friendsubmit_btn" type="button" class="submit" value="true" onclick="ajaxpost('friendform_${uid}', 'friend_delete', 2000)">确定</button>
		</c:when>
		<c:otherwise>
		<button name="friendsubmit" type="submit" class="submit" value="true">确定</button>
		</c:otherwise>
		</c:choose>
	</p>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${op=='find'}">

<c:if test="${not empty nearList}">
<div class="c_form">
	<h2 class="l_status title">惊喜，他们就在您的附近</h2>
	<ul class="avatar_list">
		<c:forEach items="${nearList}" var="value" varStatus="key">
		<li>
			<div class="avatar48"><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}" target="_blank">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
			<p><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
			<p class="gray"><a href="cp.jsp?ac=friend&op=add&uid=${value.uid}" id="a_near_friend_${key.index}" onclick="ajaxmenu(event, this.id, 1)" class="addfriend">加为好友</a></p>
		</li>
		</c:forEach>
	</ul>
</div>
</c:if>

<c:if test="${not empty friendList}">
<div class="c_form">
	<h2 class="l_status title">他们是您的好友的好友，您也可能认识</h2>
	<ul class="avatar_list">
		<c:forEach items="${friendList}" var="friend">
		<li>
			<div class="avatar48"><a href="space.jsp?uid=${friend.value.uid}" title="${sNames[friend.value.uid]}" target="_blank">${jch:avatar1(friend.value.uid,sGlobal, sConfig)}</a></div>
			<p><a href="space.jsp?uid=${friend.value.uid}" title="${sNames[friend.value.uid]}">${sNames[friend.value.uid]}</a></p>
			<p class="gray"><a href="cp.jsp?ac=friend&op=add&uid=${friend.value.uid}" id="a_friend_friend_${friend.key}" onclick="ajaxmenu(event, this.id, 1)" class="addfriend">加为好友</a></p>
		</li>
		</c:forEach>
	</ul>
</div>
</c:if>

<c:if test="${not empty onLineList}">
<div class="c_form">
	<h2 class="l_status title">他们当前正在线，加为好友就可以互动啦</h2>
	<ul class="avatar_list">
		<c:forEach items="${onLineList}" var="value" varStatus="key">
		<li>
			<div class="avatar48"><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}" target="_blank">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
			<p><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
			<p class="gray"><a href="cp.jsp?ac=friend&op=add&uid=${value.uid}" id="a_online_friend_${key.index}" onclick="ajaxmenu(event, this.id, 1)" class="addfriend">加为好友</a></p>
		</li>
		</c:forEach>
	</ul>
</div>
</c:if>

</c:when>
<c:when test="${op=='search'}">

<div class="c_form">
	<c:choose>
	<c:when test="${!jch:jchEmpty(param.searchsubmit)}">
		<c:choose>
		<c:when test="${empty list}">
			<div class="c_form">没有找到相关用户。<a href="cp.jsp?ac=friend&op=search">换个搜索条件试试</a></div>
		</c:when>
		<c:otherwise>
			<div style="padding:0 0 20px 0;">以下是查找到的用户列表(最多显示500个)，您还可以<a href="cp.jsp?ac=friend&op=search">换个搜索条件试试</a>。</div>
			<jsp:include page="${jch:template(sConfig, sGlobal, 'space_list.jsp')}"/>
		</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<table cellspacing="2" cellpadding="2" class="search_table">
		<c:choose>
		<c:when test="${!jch:jchEmpty(param.all)}">
			<tr>
				<td>
				<form action="cp.jsp" method="get">
				<table>
				<tr><td align="right">姓名：</td><td><input type="text" name="name" value="" class="t_input"></td></tr>
				<tr><td align="right">用户名：</td><td><input type="text" name="username" value="" class="t_input"></td></tr>
				<tr><td align="right">用户UID：</td><td><input type="text" name="uid" value="" class="t_input"></td></tr>
				<tr>
					<td align="right" width="100">性别：</td>
					<td>
						<select id="sex" name="sex">
							<option value="0">任意</option>
							<option value="1">男</option>
							<option value="2">女</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">婚恋：</td>
					<td>
						<select id="marry" name="marry">
							<option value="0">任意</option>
							<option value="1">单身</option>
							<option value="2">非单身</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">年龄段：</td>
					<td>
						<input type="text" name="startage" value="" size="10" class="t_input" /> ~ <input type="text" name="endage" value="" size="10" class="t_input" />
					</td>
				</tr>
				<c:if test="${!jch:jchEmpty(sConfig.videophoto)}">
				<tr>
					<td align="right">视频认证：</td>
					<td>
						<input type="checkbox" name="videostatus" value="1">通过视频认证
					</td>
				</tr>
				</c:if>
				<tr>
					<td align="right">上传头像：</td>
					<td>
						<input type="checkbox" name="avatar" value="1">已经上传头像
					</td>
				</tr>
				
				<tr>
					<td align="right">居住地：</td>
					<td id="residecitybox"></td>
				</tr>
				
				<tr>
					<td align="right">出生地：</td>
					<td id="birthcitybox"></td>
				</tr>
				
				<script type="text/javascript" src="source/script_city.js"></script>
				<script type="text/javascript">
				<!--
				showprovince('resideprovince', 'residecity', '', 'residecitybox');
				showcity('residecity', '', 'resideprovince', 'residecitybox');
				showprovince('birthprovince', 'birthcity', '', 'birthcitybox');
				showcity('birthcity', '', 'birthprovince', 'birthcitybox');
				//-->
				</script>	
				
				<tr>
					<td align="right">生日：</td>
					<td>
					<select id="birthyear" name="birthyear" onchange="showbirthday();">
						<option value="0">&nbsp;</option>
						${birthyearhtml}
					</select> 年 
					<select id="birthmonth" name="birthmonth" onchange="showbirthday();">
						<option value="0">&nbsp;</option>
						${birthmonthhtml}
					</select> 月 
					<select id="birthday" name="birthday">
						<option value="0">&nbsp;</option>
						${birthdayhtml}
					</select> 日
					</td>
				</tr>
				
				<tr><td align="right">学校：</td><td><input type="text" name="title" value="" class="t_input"> <select name="startyear">
					<option value="">入学年份</option>
					${yearhtml}
					</select></td></tr>
				<tr><td align="right">班级或院系：</td><td><input type="text" name="subtitle" value="" class="t_input"></td></tr>
			
				<tr><td align="right">公司或机构：</td><td><input type="text" name="title" value="" class="t_input"></td></tr>
				<tr><td align="right">部门：</td><td><input type="text" name="subtitle" value="" class="t_input"></td></tr>
					
				
				<tr>
					<td align="right">血型：</td>
					<td>
						<select id="blood" name="blood">
							<option value="">任意</option>
							${bloodhtml}
						</select>
					</td>
				</tr>
			
				
				<tr>
					<td align="right">QQ：</td>
					<td>
						<input type="text" name="qq" value="" class="t_input" />
					</td>
				</tr>
				<tr>
					<td align="right">MSN：</td>
					<td>
						<input type="text" name="msn" value="" class="t_input" />
					</td>
				</tr>
				<c:forEach items="${fields}" var="fvalue">
				<c:if test="${!jch:jchEmpty(fvalue.value.allowsearch)}">
				<tr>
					<td align="right">${fvalue.value.title}：</td>
					<td>
						${fvalue.value.html}
					</td>
				</tr>
				</c:if>
				</c:forEach>
				
				<tr><td></td><td><input type="submit" name="searchsubmit" value="查找" class="submit"></td></tr>
				</table>
				<input type="hidden" name="ac" value="friend">
				<input type="hidden" name="op" value="search">
				<input type="hidden" name="type" value="all">
				</form>	
			</td></tr>
		</c:when>
		<c:otherwise>
			<tr>
				<th style="border-top: none;"><img src="image/search.gif" align="absmiddle">
					<a href="javascript:;" onclick="showtbody('sex');">查找男女朋友</a></th>
			</tr>
			<tbody id="s_sex" style="display:none;">
			<tr>
				<td>
				<form action="cp.jsp" method="get">
				<table>
				<tr>
					<td align="right" width="100">性别：</td>
					<td>
						<select id="sex" name="sex">
							<option value="0">任意</option>
							<option value="1"${sexarr['1']}>男</option>
							<option value="2"${sexarr['2']}>女</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">婚恋：</td>
					<td>
						<select id="marry" name="marry">
							<option value="0">任意</option>
							<option value="1"${marryarr['1']}>单身</option>
							<option value="2"${marryarr['2']}>非单身</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">年龄段：</td>
					<td>
						<input type="text" name="startage" value="" size="10" class="t_input" /> ~ <input type="text" name="endage" value="" size="10" class="t_input" />
					</td>
				</tr>
				<c:if test="${!jch:jchEmpty(sConfig.videophoto)}">
				<tr>
					<td align="right" width="100">视频认证：</td>
					<td>
						<input type="checkbox" name="videostatus" value="1">通过视频认证
					</td>
				</tr>
				</c:if>
				<tr>
					<td align="right" width="100">上传头像：</td>
					<td>
						<input type="checkbox" name="avatar" value="1">已经上传头像
					</td>
				</tr>
				<tr><td align="right">姓名：</td><td><input type="text" name="name" value="" class="t_input"></td></tr>
				<tr><td align="right">用户名：</td><td><input type="text" name="username" value="" class="t_input"></td></tr>
				<tr><td></td><td><input type="submit" name="searchsubmit" value="查找" class="submit"></td></tr>
				</table>
				<input type="hidden" name="ac" value="friend">
				<input type="hidden" name="op" value="search">
				<input type="hidden" name="type" value="base">
				</form>
				</td>
			</tr>
			</tbody>
		
			
			<tr>
				<th><img src="image/search.gif" align="absmiddle">
					<a href="javascript:;" onclick="showtbody('reside');">查找同城的人</a></th>
			</tr>
			<tbody id="s_reside" style="display:none;">
			<tr>
				<td>
				<form action="cp.jsp" method="get">
				<table>
				<tr>
					<td align="right" width="150">居住地：</td>
					<td id="residecitybox"></td>
				</tr>
				<tr><td align="right">姓名：</td><td><input type="text" name="name" value="" class="t_input"></td></tr>
				<tr><td align="right">用户名：</td><td><input type="text" name="username" value="" class="t_input"></td></tr>
				<tr><td></td><td><input type="submit" name="searchsubmit" value="查找" class="submit"></td></tr>
				</table>
				<script type="text/javascript" src="source/script_city.js"></script>
				<script type="text/javascript">
				<!--
				showprovince('resideprovince', 'residecity', '${space.resideprovince}', 'residecitybox');
				showcity('residecity', '${space.residecity}', 'resideprovince', 'residecitybox');
				//-->
				</script>
				<input type="hidden" name="ac" value="friend">
				<input type="hidden" name="op" value="search">
				<input type="hidden" name="type" value="base">
				</form>
				</td>
			</tr>
			</tbody>
			
		
			<tr>
				<th><img src="image/search.gif" align="absmiddle">
					<a href="javascript:;" onclick="showtbody('birth');">查找老乡</a></th>
			</tr>
			<tbody id="s_birth" style="display:none;">
			<tr>
				<td>
				<form action="cp.jsp" method="get">
				<table>
				<tr>
					<td align="right" width="150">出生地：</td>
					<td id="birthcitybox"></td>
				</tr>
				<tr><td align="right">姓名：</td><td><input type="text" name="name" value="" class="t_input"></td></tr>
				<tr><td align="right">用户名：</td><td><input type="text" name="username" value="" class="t_input"></td></tr>
				<tr><td></td><td><input type="submit" name="searchsubmit" value="查找" class="submit"></td></tr>
				</table>
				<script type="text/javascript" src="source/script_city.js"></script>
				<script type="text/javascript">
				<!--
				showprovince('birthprovince', 'birthcity', '${space.birthprovince}', 'birthcitybox');
				showcity('birthcity', '${space.birthcity}', 'birthprovince', 'birthcitybox');
				//-->
				</script>
				<input type="hidden" name="ac" value="friend">
				<input type="hidden" name="op" value="search">
				<input type="hidden" name="type" value="base">
				</form>
				</td>
			</tr>
			</tbody>
		
			
			<tr>
				<th><img src="image/search.gif" align="absmiddle">
					<a href="javascript:;" onclick="showtbody('birthyear');">查找同年同月同日生的人</a></th>
			</tr>
			<tbody id="s_birthyear" style="display:none;">
			<tr>
				<td>
				<form action="cp.jsp" method="get">
				<table>
				<tr>
					<td align="right" width="150">生日：</td>
					<td>
					<select id="birthyear" name="birthyear" onchange="showbirthday();">
						<option value="0">&nbsp;</option>
						${birthyearhtml}
					</select> 年 
					<select id="birthmonth" name="birthmonth" onchange="showbirthday();">
						<option value="0">&nbsp;</option>
						${birthmonthhtml }
					</select> 月 
					<select id="birthday" name="birthday">
						<option value="0">&nbsp;</option>
						${birthdayhtml}
					</select> 日
					</td>
				</tr>
				<tr><td align="right">姓名：</td><td><input type="text" name="name" value="" class="t_input"></td></tr>
				<tr><td align="right">用户名：</td><td><input type="text" name="username" value="" class="t_input"></td></tr>
				<tr><td></td><td><input type="submit" name="searchsubmit" value="查找" class="submit"></td></tr>
				</table>
				<input type="hidden" name="ac" value="friend">
				<input type="hidden" name="op" value="search">
				<input type="hidden" name="type" value="base">
				</form>
				</td>
			</tr>
			</tbody>
			
			<tr>
				<th>
					<img src="image/search.gif" align="absmiddle">
					<a href="javascript:;" onclick="showtbody('edu');">查找你的同学</a>
				</th>
			</tr>
			<tbody id="s_edu" style="display:none;">
			<tr>
				<td>
				<form action="cp.jsp" method="get">
				<table>
					<tr><td align="right" width="150">学校：</td><td><input type="text" name="title" value="" class="t_input"> <select name="startyear">
						<option value="">入学年份</option>
						${yearhtml}
						</select></td></tr>
					<tr><td align="right">班级或院系：</td><td><input type="text" name="subtitle" value="" class="t_input"></td></tr>
					<tr><td align="right">姓名：</td><td><input type="text" name="name" value="" class="t_input"></td></tr>
					<tr><td align="right">用户名：</td><td><input type="text" name="username" value="" class="t_input"></td></tr>
					<tr><td></td><td><input type="submit" name="searchsubmit" value="查找" class="submit"></td></tr>
				</table>
				<input type="hidden" name="ac" value="friend">
				<input type="hidden" name="op" value="search">
				<input type="hidden" name="type" value="edu">
				</form>
				</td>
			</tr>
			</tbody>
			
			<tr>
				<th><img src="image/search.gif" align="absmiddle">
					<a href="javascript:;" onclick="showtbody('work');">查找你的同事</a></th>
			</tr>
			<tbody id="s_work" style="display:none;">
			<tr>
				<td>
				<form action="cp.jsp" method="get">
				<table>
					<tr><td align="right" width="150">公司或机构：</td><td><input type="text" name="title" value="" class="t_input"></td></tr>
					<tr><td align="right">部门：</td><td><input type="text" name="subtitle" value="" class="t_input"></td></tr>
					<tr><td align="right">姓名：</td><td><input type="text" name="name" value="" class="t_input"></td></tr>
					<tr><td align="right">用户名：</td><td><input type="text" name="username" value="" class="t_input"></td></tr>
					<tr><td></td><td><input type="submit" name="searchsubmit" value="查找" class="submit"></td></tr>
				</table>
				<input type="hidden" name="ac" value="friend">
				<input type="hidden" name="op" value="search">
				<input type="hidden" name="type" value="work">
				</form>
				</td>
			</tr>
			</tbody>
			
			
			<tr>
				<th><img src="image/search.gif" align="absmiddle">
					<a href="javascript:;" onclick="showtbody('name');">精确查找</a></th>
			</tr>
			<tbody id="s_name" style="display:none;">
			<tr>
				<td>
				<form action="cp.jsp" method="get">
				<table>
				<tr><td align="right" width="150">姓名：</td><td><input type="text" name="name" value="" class="t_input"></td></tr>
				<tr><td align="right">用户名：</td><td><input type="text" name="username" value="" class="t_input"></td></tr>
				<tr><td align="right">用户UID：</td><td><input type="text" name="uid" value="" class="t_input"></td></tr>
				<tr><td></td><td><input type="submit" name="searchsubmit" value="查找" class="submit"></td></tr>
				</table>
				<input type="hidden" name="ac" value="friend">
				<input type="hidden" name="op" value="search">
				<input type="hidden" name="type" value="base">
				</form>
				</td>
			</tr>
			</tbody>
			
			<tr>
				<th><img src="image/search.gif" align="absmiddle">
					<a href="cp.jsp?ac=friend&op=search&all=yes">高级方式查找</a></th>
			</tr>
		</c:otherwise>
		</c:choose>
		</table>
	</c:otherwise>
</c:choose>
</div>

<script>
function showtbody(id) {
	var obj = $('s_'+id);
	if(obj.style.display == 'none') {
		obj.style.display = '';
	} else {
		obj.style.display = 'none';
	}
}
<c:if test='${!jch:jchEmpty(param.view)}'>showtbody('${param.view}');</c:if>
</script>

</c:when>
<c:when test="${op=='changenum'}">

<h1>好友热度</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__changenumform_${uid}">
<form method="post" id="changenumform_${uid}" name="changenumform_${uid}" action="cp.jsp?ac=friend&op=changenum&uid=${uid}">
	<p>调整好友的热度</p>
	<p>
		新的热度：<input type="text" name="num" value="${friend.num}" size="5"> (0~9999之间的一个数字）
	</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}">
		<button name="changenumsubmit" type="submit" class="submit" value="true">确定</button>
	</p>
	
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${op=='changegroup'}">

<h1>好友分组</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__changegroupform_${uid}">
<form method="post" id="changegroupform_${uid}" name="changegroupform_${uid}" action="cp.jsp?ac=friend&op=changegroup&uid=${uid}">
	<p>设置好友分组</p>
	<table cellspacing="2" cellpadding="2"><tr>
	<c:forEach items="${groups}" var="group" varStatus="key">
	<td><input type="radio" name="group" value="${group.key}"${groupSelect[group.key]}> ${group.value}</td>
	<c:if test="${key.index%2==1}"></tr><tr></c:if>
	</c:forEach>
	</tr></table>

	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}">
		<c:choose>
		<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
		<input type="hidden" name="changegroupsubmit" value="true" />
		<button name="changegroupsubmit_btn" type="button" class="submit" value="true" onclick="ajaxpost('changegroupform_${uid}', 'friend_changegroup', 2000)">确定</button>
		</c:when>
		<c:otherwise>
		<button name="changegroupsubmit" type="submit" class="submit" value="true">确定</button>
		</c:otherwise>
		</c:choose>
	</p>
	
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${op=='group'}">

<div class="h_status">
对选定的好友进行分组，热度表示的是你跟好友互动的次数。
</div>

<div id="content" style="width: 640px;">
	<form method="post" action="cp.jsp?ac=friend&op=group&ref">
	<c:choose>
	<c:when test="${not empty list}">
	
	<div class="thumb_list" id="friend_ul">
		<ul>
		<c:forEach items="${list}" var="value">
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
				<div class="thumbTitle"><input type="checkbox" name="fuids" value="${value.uid}" /><a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a></div>
				<div class="gray">热度：${value.num}</div>
				<div class="gray">${value.group}</div>
			</li>
		</c:forEach>
		</ul>
	</div>
	<div class="page">${multi}</div>
	<div style="padding:20px 0 0 0;">
		<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'fuids')" /><label for="chkall">全选</label></td>
		设置用户组：<select name="group">
			<c:forEach items="${groups}" var="group">
				<option value="${group.key}">${group.value}</option>
			</c:forEach>
			</select>
			<input type="submit" name="btnsubmit" value="确定" class="submit" />
	</div>
	</c:when>
	<c:otherwise>
	<div class="c_form">
		没有相关用户列表。
	</div>
	</c:otherwise>
	</c:choose>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	<input type="hidden" name="groupsubmin" value="true" />
	</form>
</div>

<div id="sidebar" style="width: 150px;">
	<div class="cat">
	<h3>好友分组</h3>
	<ul class="post_list line_list">
		<li <c:if test="${empty param.group}"> class="current"</c:if>><a href="cp.jsp?ac=friend&op=group">全部好友</a></li>
		<c:forEach items="${groups}" var="group">
		<li<c:if test="${not empty param.group && param.group==group.key}"> class="current"</c:if>><a href="cp.jsp?ac=friend&op=group&group=${group.key}">${group.value}</a></li>
		</c:forEach>
	</ul>
	</div>
</div>

</c:when>
<c:when test="${op=='groupname'}">

<h1>好友组</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__groupnameform_${group}">
<form method="post" id="groupnameform_${group}" name="groupnameform_${group}" action="cp.jsp?ac=friend&op=groupname&group=${group}">
	<p>设置新好友分组名</p>
	<div class="c_form">
		新名称：<input type="text" name="groupname" value="${groups[group]}" size="15" class="t_input">
		<input type="hidden" name="refer" value="${sGlobal.refer}">
		<c:choose>
		<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
		<input type="hidden" name="groupnamesubmit" value="true" />
		<input type="button" name="groupnamesubmit_btn" value="确定" class="submit" onclick="ajaxpost('groupnameform_${group}', 'friend_changegroupname', 2000)" />
		</c:when>
		<c:otherwise>
		<input type="submit" name="groupnamesubmit" value="确定" class="submit" />
		</c:otherwise>
		</c:choose>
	</div>
	
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>


</c:when>
<c:when test="${op=='groupignore'}">

<h1>调整用户组动态</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="${group}">
<form method="post" id="groupignoreform" name="groupignoreform" action="cp.jsp?ac=friend&op=groupignore&group=${group}">
	<c:choose>
	<c:when test="${empty space.privacy.filter_gid[group]}">
	<p>在首页不显示该用户组的好友动态</p>
	</c:when>
	<c:otherwise>
	<p>在首页显示该用户组的好友动态</p>
	</c:otherwise>
	</c:choose>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}">
		<c:choose>
		<c:when test="${!jch:jchEmpty(sGlobal.inajax)}">
		<input type="hidden" name="groupignoresubmit" value="true" />
		<button name="groupignoresubmit_btn" type="submit" class="submit" value="true">确定</button>
		</c:when>
		<c:otherwise>
		<button name="groupignoresubmit" type="submit" class="submit" value="true">确定</button>
		</c:otherwise>
		</c:choose>
	</p>
<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${op=='request'}">

<div class="l_status">
	<div class="r_option">
		<a href="cp.jsp?ac=friend&op=ignore&confirm=1&key=${space.key}" onclick="return confirm('您确定要忽略所有的好友申请吗？');">忽略所有好友申请(慎用)</a>
		 | <a href="cp.jsp?ac=friend&op=addconfirm&key=${space.key}">批准全部申请</a>
	</div>
	<span id="add_friend_div">请选定好友的申请进行批准或忽略</span>
	<c:if test="${maxfriendnum>0}">
	(最多可以添加 ${maxfriendnum} 个好友)
	<p>
		<c:if test="${not empty globalMagic.friendnum}">
		<img src="image/magic/friendnum.small.gif" class="magicicon" />
		<a id="a_magic_friendnum" href="magic.jsp?mid=friendnum" onclick="ajaxmenu(event, this.id, 1)">我要扩容好友数</a>
		(您可以购买道具“${globalMagic.friendnum}”来扩容，让自己可以添加更多的好友。)
		</c:if>
	</p>
	</c:if>
</div>
<c:choose>
<c:when test="${not empty list}">
<div class="thumb_list" id="friend_ul">
<table cellspacing="6" cellpadding="0">
<c:forEach items="${list}" var="value" varStatus="key">
<tbody id="friend_tbody_${value.uid}">
<tr>
	<td class="thumb <c:if test="${ols[value.uid]}">online</c:if>">
		<table cellpadding="0" cellspacing="0">
			<tr>
				<td class="image">
				<div class="ar_r_t"><div class="ar_l_t"><div class="ar_r_b"><div class="ar_l_b">
				<a href="space.jsp?uid=${value.uid}" class="avatarlink">${jch:avatar2(value.uid, 'middle', false, sGlobal, sConfig)}</a>
				</div></div></div></div>
				</td>
				<td>
					<h6 class="l_status">
						<a href="space.jsp?uid=${value.uid}">${sNames[value.uid]}</a>
						<c:if test="${!jch:jchEmpty(value.videostatus)}">
						<img src="image/videophoto.gif" align="absmiddle"> <span class="gray">已通过视频认证</span>
						</c:if>
					</h6>
					<div id="friend_${value.uid}">
					<c:if test="${!jch:jchEmpty(value.note)}"><div class="quote"><span id="quote" class="q">${value.note}</span></div></c:if>
					<p>${jch:sgmdate(pageContext.request, 'MM-dd HH:mm', value.dateline,true)}</p>
					<c:if test="${value.cfcount>0}"><p><a href="cp.jsp?ac=friend&op=getcfriend&fuid=${value.cfriend}" id="a_cfriend_${key.index}" onclick="ajaxmenu(event, this.id, 1)">你们有 ${value.cfcount} 个共同好友</a></p></c:if>
					<p style="margin-top:1em;">
						<a href="cp.jsp?ac=friend&op=add&uid=${value.uid}" id="afr_${value.uid}" onclick="ajaxmenu(event, this.id, 1)" class="submit a">批准申请</a>
						<a href="cp.jsp?ac=friend&op=ignore&uid=${value.uid}&confirm=1" id="afi_${value.uid}" onclick="ajaxmenu(event, this.id, 1)" class="button a">忽略</a>
					</p>
					</div>
				</td>
			</tr>
			<tbody id="cf_${value.uid}"></tbody>
		</table>
	</td>
</tr>
</tbody>
</c:forEach>
</table>
</div>
<div class="page">${multi}</div>
</c:when>
<c:otherwise>
<div class="c_form">
	没有新的好友请求。
</div>
</c:otherwise>
</c:choose>

</c:when>
<c:when test="${op=='getcfriend'}">

<h1>共同好友</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="cfriend">
	<div class="h_status">最多显示15位共同的好友</div>
	<ul class="avatar_list">
		<c:forEach items="${list}" var="value">
		<li>
			<div class="avatar48"><a href="space.jsp?uid=${value.uid}">${jch:avatar1(value.uid,sGlobal, sConfig)}</a></div>
			<p><a href="space.jsp?uid=${value.uid}" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
		</li>
		</c:forEach>
	</ul>
</div>

</c:when>
<c:when test="${op=='add'}">

<h1>加好友</h1>
<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>
<div class="popupmenu_inner" id="__addform_${tospace.uid}">
<form method="post" id="addform_${tospace.uid}" name="addform_${tospace.uid}" action="cp.jsp?ac=friend&op=add&uid=${tospace.uid}">
	<table cellspacing="0" cellpadding="3">
		<tr>
			<th width="52"><a href="space.jsp?uid=${tospace.uid}">${jch:avatar1(tospace.uid,sGlobal, sConfig)}</th>
			<td>加 <strong>${sNames[tospace.uid]}</strong> 为好友，附言：<br />
				<input type="text" name="note" value="" size="20" class="t_input" style="width: 300px;"  onkeydown="ctrlEnter(event, 'addsubmit_btn', 1);" />
				<p class="gray">(附言为可选，${sNames[tospace.uid]} 会看到这条附言，最多50个字符)</p>
			</td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>
				分组: <select name="gid">
				<c:forEach items="${groups}" var="group">
				<option value="${group.key}">${group.value}</option>
				</c:forEach>
				</select>
				<input type="hidden" name="refer" value="${sGlobal.refer}" />
				<input type="hidden" name="addsubmit" value="true" />
				<c:choose>
				<c:when test="${sGlobal.inajax>0}">
				<input type="button" name="addsubmit_btn" id="addsubmit_btn" value="确定" class="submit" onclick="ajaxpost('addform_${tospace.uid}','',2000)" />
				</c:when>
				<c:otherwise>
				<input type="submit" name="addsubmit_btn" id="addsubmit_btn" value="确定" class="submit" />
				</c:otherwise>
				</c:choose>
			</td>
	</table>
	
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>


</c:when>
<c:when test="${op=='add2'}">

<h1>批准请求</h1>
<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>
<div class="popupmenu_inner" id="__addratifyform_${tospace.uid}">
<form method="post" id="addratifyform_${tospace.uid}" name="addratifyform_${tospace.uid}" action="cp.jsp?ac=friend&op=add&uid=${tospace.uid}">
	<table cellspacing="0" cellpadding="3">
		<tr>
			<th width="52"><a href="space.jsp?uid=${tospace.uid}">${jch:avatar1(tospace.uid,sGlobal, sConfig)}</th>
			<td>
			<div class="l_status">批准 <strong>${sNames[tospace.uid]}</strong> 的好友请求，并分组：</div>
				<table cellspacing="2" cellpadding="2"><tr>
				<c:forEach items="${groups}" var="group" varStatus="key">
				<td><input type="radio" name="gid" id="group_${group.key}" value="${group.key}"${groupselect[group.key]}> <label for="group_${group.key}">${group.value}</label></td>
				<c:if test="${key.index%2==1}"></tr><tr></c:if>
				</c:forEach>
				</tr></table>
				
				<p>
				<input type="hidden" name="refer" value="${sGlobal.refer}" />
				<c:choose>
				<c:when test="${sGlobal.inajax>0}">
				<input type="hidden" name="add2submit" value="true" />
				<input type="button" name="add2submit_btn" value="批准" class="submit" onclick="ajaxpost('addratifyform_${tospace.uid}', 'myfriend_post', 2000)" />
				</c:when>
				<c:otherwise>
				<input type="submit" name="add2submit" value="批准" class="submit" />
				</c:otherwise>
				</c:choose>
				</p>
			</td>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>