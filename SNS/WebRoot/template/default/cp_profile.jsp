<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_header.jsp')}" />
<div class="l_status c_form">
	<a href="cp.jsp?ac=profile&op=base"${cat_actives_base}>基本资料</a><span class="pipe">|</span>
	<a href="cp.jsp?ac=profile&op=contact"${cat_actives_contact}>联系方式</a><span class="pipe">|</span>
	<a href="cp.jsp?ac=profile&op=edu"${cat_actives_edu}>教育情况</a><span class="pipe">|</span>
	<a href="cp.jsp?ac=profile&op=work"${cat_actives_work}>工作情况</a><span class="pipe">|</span>
	<a href="cp.jsp?ac=profile&op=info"${cat_actives_info}>个人信息</a>
</div>
<form method="post" action="${theurl}&ref" class="c_form">
	<c:choose>
		<c:when test="${op == 'base'}">
			<table cellspacing="0" cellpadding="0" class="formtable">
				<tr>
					<th style="width: 10em;">您的登录用户名:</th>
					<td>${username} (<a href="cp.jsp?ac=password">修改登录密码</a>)</td>
					<td></td>
				</tr>
				<tr>
					<th style="width: 10em;">真实姓名:</th>
					<td>
						<c:choose>
							<c:when test="${sConfig.realname == 0}"><input type="text" id="name" name="name" value="${name}" class="t_input" /></c:when>
							<c:when test="${!empty space.name && namechange != 1}">
								<span style="font-weight: bold;">${name}</span>
								<c:if test="${sConfig.namechange == 1}">[<a href="${theurl}&namechange=1">修改</a>]</c:if>
								<c:choose>
									<c:when test="${space.namestatus == 1}">[<font color="red">认证通过</font>]</c:when>
									<c:otherwise><br>等待验证中，您目前将只能使用用户名，并且一些操作可能会受到限制</c:otherwise>
								</c:choose>
								<input type="hidden" name="name" value="${name}" />
							</c:when>
							<c:otherwise>
								<c:if test="${sConfig.namechange != 1}">您的真实姓名一经确认，将不再允许再次修改，请真实填写。<br></c:if>
								<c:if test="${sConfig.namecheck == 1}">您填写/修改真实姓名后，需要等待我们认证后才能有效，在认证通过之前，您将只能使用用户名，并且一些操作可能会受到限制。<br></c:if>
								<input type="text" id="name" name="name" value="${name}" class="t_input" /> (请输入2～5个汉字)
							</c:otherwise>
						</c:choose>
					</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<th style="width: 10em;">性别:</th>
					<td>
						<c:choose>
							<c:when test="${empty space.sex || space.sex == 0}">
								<label for="sexm"><input id="sexm" type="radio" value="1" name="sex" ${sexmap[1]}/>男</label>
								<label for="sexw"><input id="sexw" type="radio" value="2" name="sex" ${sexmap[2]}/>女</label>
								<span style="font-weight: bold; color: red;">(性别选择确定后，将不能再次修改)</span>
							</c:when>
							<c:otherwise>${space.sex == 1 ? "男" : "女"}</c:otherwise>
						</c:choose>
					</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<th style="width: 10em;">婚恋状态:</th>
					<td>
						<select id="marry" name="marry">
							<option value="0">&nbsp;</option>
							<option value="1"${marriagemap['1']}>单身</option>
							<option value="2"${marriagemap['2']}>非单身</option>
						</select>
						<a href="cp.jsp?ac=friend&op=search&view=sex" target="_blank">&raquo; 查找男女朋友</a>
					</td>
					<td>
						<select name="friend[marry]">
							<option value="0"${friendmap.marry['0']}>全用户可见</option>
							<option value="1"${friendmap.marry['1']}>仅好友可见</option>
							<option value="3"${friendmap.marry['3']}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>生日:</th>
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
						<a href="cp.jsp?ac=friend&op=search&view=birthyear" target="_blank">&raquo; 查找同生日朋友</a>
					</td>
					<td>
						<select name="friend[birth]">
							<option value="0"${friendmap.birth['0']}>全用户可见</option>
							<option value="1"${friendmap.birth['1']}>仅好友可见</option>
							<option value="3"${friendmap.birth['3']}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>血型:</th>
					<td>
						<select id="blood" name="blood">
							<option value="">&nbsp;</option>
							${bloodhtml}
						</select>
					</td>
					<td>
						<select name="friend[blood]">
							<option value="0"${friendmap.blood['0']}>全用户可见</option>
							<option value="1"${friendmap.blood['1']}>仅好友可见</option>
							<option value="3"${friendmap.blood['3']}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>家乡:</th>
					<td id="birthcitybox">
						<script type="text/javascript" src="source/script_city.js"></script>
						<script type="text/javascript">
						<!--
						showprovince('birthprovince', 'birthcity', '${space.birthprovince}', 'birthcitybox');
						showcity('birthcity', '${space.birthcity}', 'birthprovince', 'birthcitybox');
						
						//-->
						</script>
						<a href="cp.jsp?ac=friend&op=search&view=birth" target="_blank">&raquo; 查找老乡</a>
					</td>
					<td>
						<select name="friend[birthcity]">
							<option value="0"${friendmap.birthcity['0']}>全用户可见</option>
							<option value="1"${friendmap.birthcity['1']}>仅好友可见</option>
							<option value="3"${friendmap.birthcity['3']}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>居住地:</th>
					<td id="residecitybox">
						<script type="text/javascript">
						<!--
						showprovince('resideprovince', 'residecity', '${space.resideprovince}', 'residecitybox');
						showcity('residecity', '${space.residecity}', 'resideprovince', 'residecitybox');
						//-->
						</script>
						<a href="cp.jsp?ac=friend&op=search&view=reside" target="_blank">&raquo; 查找同城</a>
					</td>
					<td>
						<select name="friend[residecity]">
							<option value="0"${friendmap.residecity['0']}>全用户可见</option>
							<option value="1"${friendmap.residecity['1']}>仅好友可见</option>
							<option value="3"${friendmap.residecity['3']}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<c:forEach items="${profilefields}" var="value">
					<tr>
						<th>${value.title}${value.required != 0 ? "*" : ""}:</th>
						<td>${value.formhtml} <c:if test="${!empty value.note}"><span class="gray">${value.note}</span></c:if></td>
						<td>
							<select name="friend[field_${value.fieldid}]">
								<c:set var="k" value="field_${value.fieldid}"></c:set>
								<c:set var="field_friendmap" value="${friendmap[k]}"></c:set>
								<option value="0"${field_friendmap['0']}>全用户可见</option>
								<option value="1"${field_friendmap['1']}>仅好友可见</option>
								<option value="3"${field_friendmap['3']}>仅自己可见</option>
							</select>
						</td>
					</tr>
				</c:forEach>
				<tr>
					<th style="width: 10em;">&nbsp;</th>
					<td>
						<input type="submit" name="nextsubmit" value="继续下一项" class="submit" />
						<input type="submit" name="profilesubmit" value="保存" class="submit" />
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</c:when>
		<c:when test="${op == 'contact'}">
			<table cellspacing="0" cellpadding="0" class="formtable">
				<c:choose>
					<c:when test="${param.editemail == '1'}">
			</table>
			<div class="borderbox">
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tbody>
						<tr>
							<th style="width: 10em;">本网站的登录密码:</th>
							<td><input type="password" id="password" name="password" value="" class="t_input" /><br>为了您的账号安全，更换新邮箱的时候，需要输入您在本网站的密码。</td>
							<td></td>
						</tr>
						<tr>
							<th style="width: 10em;">新邮箱:</th>
							<td><input type="text" id="email" class="t_input" name="email" value="${space.email}" /><c:if test="${space.emailcheck == 1}"><br>注意：新填写的邮箱只有在验证激活之后，才可以生效。</c:if></td>
							<td></td>
						</tr>
					</tbody>
				</table>
			</div>
			<br>
			<table cellspacing="0" cellpadding="0" class="formtable">
				</c:when>
				<c:otherwise>
					<c:if test="${empty space.email}">
						<tr>
							<th style="width: 10em;">本网站的登录密码:</th>
							<td><input type="password" id="password" name="password" value="" class="t_input" /><br>为了您的账号安全，填写邮箱的时候，需要输入您在本网站的密码。</td>
							<td></td>
						</tr>
					</c:if>
					<tr>
						<th style="width: 10em;">常用邮箱:</th>
						<td>
							<c:choose>
								<c:when test="${!empty space.email}">${space.email}<br>
									<c:choose>
										<c:when test="${space.emailcheck == 1}">当前邮箱已经验证激活 (<a href="${theurl}&editemail=1">更换</a>)</c:when>
										<c:otherwise>邮箱等待验证中...<br>系统已经向该邮箱发送了一封验证激活邮件，请查收邮件，进行验证激活。<br>如果没有收到验证邮件，您可以<a href="${theurl}&editemail=1">更换一个邮箱</a>，或者<a href="${theurl}&resend=1">重新接收验证邮件</a></c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise><input type="text" id="email" class="t_input" name="email" value="" /><br>请准确填写，取回密码、获取通知的时候都会发送到该邮箱。<br>系统同时会向该邮箱发送一封验证激活邮件，请注意查收。<br></c:otherwise>
							</c:choose>
							<c:if test="${!empty space.newemail}"><p>您要更换的新邮箱：<strong>${space.newemail}</strong> 需要激活后才能替换当前邮箱并生效。<br>如果没有收到验证邮件，您可以<a href="${theurl}&resend=1">重新接收验证邮件</a></p></c:if>
						</td>
						<td></td>
					</tr>
				</c:otherwise>
				</c:choose>
				<tr>
					<th style="width: 10em;">手机:</th>
					<td><input type="text" class="t_input" name="mobile" value="${space.mobile}" /></td>
					<td>
						<select name="friend[mobile]">
							<option value="0"${friendmap.mobile['0']}>全用户可见</option>
							<option value="1"${friendmap.mobile['1']}>仅好友可见</option>
							<option value="3"${friendmap.mobile['3']}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th style="width: 10em;">QQ:</th>
					<td><input type="text" class="t_input" name="qq" value="${space.qq}" /></td>
					<td>
						<select name="friend[qq]">
							<option value="0"${friendmap.qq['0']}>全用户可见</option>
							<option value="1"${friendmap.qq['1']}>仅好友可见</option>
							<option value="3"${friendmap.qq['3']}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>MSN:</th>
					<td><input type="text" class="t_input" name="msn" value="${space.msn}" /></td>
					<td>
						<select name="friend[msn]">
							<option value="0"${friendmap.msn['0']}>全用户可见</option>
							<option value="1"${friendmap.msn['1']}>仅好友可见</option>
							<option value="3"${friendmap.msn['3']}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th style="width: 10em;">&nbsp;</th>
					<td>
						<input type="submit" name="nextsubmit" value="继续下一项" class="submit" />
						<input type="submit" name="profilesubmit" value="保存" class="submit" />
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</c:when>
		<c:when test="${op == 'edu'}">
			<c:if test="${!empty list}">
				<table cellspacing="0" cellpadding="0" class="listtable">
					<tr class="title">
						<td>学校/班级或院系</td>
						<td>入学年份</td>
						<td>隐私设置</td>
						<td>操作</td>
					</tr>
					<c:forEach items="${list}" var="value" varStatus="st">
						<tr${st.index % 2 == 1 ? " class='line'" : ""}>
							<td>${value.title}<br>${value.subtitle}</td>
							<td>${value.startyear}</td>
							<td>${farr[value.friend]}</td>
							<td><a href="${theurl}&subop=delete&infoid=${value.infoid}">删除信息</a><br><a href="cp.jsp?ac=friend&op=search&searchmode=1&type=edu&title=${value.title_s}" target="_blank">寻找同学</a></td>
						</tr>
					</c:forEach>
				</table>
				<br>
			</c:if>
			<table cellspacing="0" cellpadding="0" class="formtable">
				<c:if test="${!empty list}"><caption><h2>添加新学校</h2></caption></c:if>
				<tbody id="oldtbody">
					<tr>
						<th style="width: 10em;">学校名称:</th>
						<td><input type="text" name="title[]" value="" class="t_input"></td>
					</tr>
					<tr>
						<th>班级或院系:</th>
						<td><input type="text" name="subtitle[]" value="" class="t_input"></td>
					</tr>
					<tr>
						<th>入学年份:</th>
						<td><select name="startyear[]">${yearhtml}</select> 年</td>
					</tr>
					<tr>
						<th>隐私设置:</th>
						<td>
							<select name="friend[]">
								<option value="0">全用户可见</option>
								<option value="1">仅好友可见</option>
								<option value="3">仅自己可见</option>
							</select>
						</td>
					</tr>
				</tbody>
				<tbody id="newtbody"></tbody>
				<tr>
					<th style="width: 10em;">&nbsp;</th>
					<td><a href="javascript:;" onclick="add_tbody();">添加新的学校信息</a></td>
				</tr>
				<tr>
					<th style="width: 10em;">&nbsp;</th>
					<td>
						<input type="submit" name="nextsubmit" value="继续下一项" class="submit" />
						<input type="submit" name="profilesubmit" value="保存" class="submit" />
					</td>
				</tr>
			</table>
		</c:when>
		<c:when test="${op == 'work'}">
			<c:if test="${!empty list}">
				<table cellspacing="0" cellpadding="0" class="listtable">
					<tr class="title">
						<td>公司或机构/部门</td>
						<td>工作时间</td>
						<td>隐私设置</td>
						<td>操作</td>
					</tr>
					<c:forEach items="${list}" var="value" varStatus="st">
						<tr${st.index % 2 == 1 ? " class='line'" : ""}>
							<td>${value.title}<br>${value.subtitle}</td>
							<td>${value.startyear}年${value.startmonth}月 ~ <c:if test="${value.endyear != 0}">${value.endyear}年</c:if><c:if test="${value.endmonth != 0}">${value.endmonth}月</c:if><c:if test="${value.endyear == 0 && value.endmonth == 0}">现在</c:if></td>
							<td>${farr[value.friend]}</td>
							<td><a href="${theurl}&subop=delete&infoid=${value.infoid}">删除信息</a><br><a href="cp.jsp?ac=friend&op=search&searchmode=1&type=work&title=${value.title_s}" target="_blank">寻找同事</a></td>
						</tr>
					</c:forEach>
				</table>
				<br>
			</c:if>
			<table cellspacing="0" cellpadding="0" class="formtable">
				<c:if test="${!empty list}"><caption><h2>添加新公司或机构</h2></caption></c:if>
				<tbody id="oldtbody">
					<tr>
						<th style="width: 10em;">公司或机构:</th>
						<td><input type="text" name="title[]" value="" class="t_input"></td>
					</tr>
					<tr>
						<th>部门:</th>
						<td><input type="text" name="subtitle[]" value="" class="t_input"></td>
					</tr>
					<tr>
						<th>工作时间:</th>
						<td>
							<select name="startyear[]">
								${yearhtml}
							</select> 年
							<select name="startmonth[]">
								${monthhtml}
							</select> 月 ~
							<select name="endyear[]">
								<option value="">现在</option>
								${yearhtml}
							</select> 年
							<select name="endmonth[]">
								<option value=""></option>
								${monthhtml}
							</select> 月
						</td>
					</tr>
					<tr>
						<th>隐私设置:</th>
						<td>
							<select name="friend[]">
								<option value="0">全用户可见</option>
								<option value="1">仅好友可见</option>
								<option value="3">仅自己可见</option>
							</select>
						</td>
					</tr>
				</tbody>
				<tbody id="newtbody"></tbody>
				<tr>
					<th style="width: 10em;">&nbsp;</th>
					<td><a href="javascript:;" onclick="add_tbody();">添加新的公司或机构</a></td>
				</tr>
				<tr>
					<th style="width: 10em;">&nbsp;</th>
					<td>
						<input type="submit" name="nextsubmit" value="继续下一项" class="submit" />
						<input type="submit" name="profilesubmit" value="保存" class="submit" />
					</td>
				</tr>
			</table>
		</c:when>
		<c:when test="${op == 'info'}">
			<table cellspacing="0" cellpadding="0" class="formtable">
				<tr>
					<th>栏目</th>
					<td>内容</td>
					<td>隐私设置</td>
				</tr>
				<c:forEach items="${infoarr}" var="m">
					<tr>
						<th>${m.value}:</th>
						<td><textarea name="info[${m.key}]" rows="3" cols="50">${list[m.key].title}</textarea></td>
						<td>
							<select name="info_friend[${m.key}]">
								<option value="0"${friends[m.key]['0']}>全用户可见</option>
								<option value="1"${friends[m.key]['1']}>仅好友可见</option>
								<option value="3"${friends[m.key]['3']}>仅自己可见</option>
							</select>
						</td>
					</tr>
				</c:forEach>
				<tr>
					<th style="width: 10em;">&nbsp;</th>
					<td><input type="submit" name="profilesubmit" value="保存" class="submit" /></td>
				</tr>
			</table>
		</c:when>
	</c:choose>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
<script>
function add_tbody() {
	for(i=0; i<$("oldtbody").rows.length; i++) {
		newnode = $("oldtbody").rows[i].cloneNode(true);
		$("newtbody").appendChild(newnode);
	}
}
</script>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />