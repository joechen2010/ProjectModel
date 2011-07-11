<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:choose>
<c:when test="${op == 'use'}">
	<c:if test="${!frombuy}">
	<h1>使用道具</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	</c:if>
	<div class="toolly" id="__magicuse_form_${mid }">
		<form method="post" id="magicuse_form_${mid}" action="magic.jsp?mid=${mid}&idtype=${idtype }&id=${id }">
			<div class="magic_img">
				<img src="image/magic/${mid}.gif" alt="${magic.name }" />
			</div>
			<div class="magic_info">
				<h3>${magic.name }</h3>
				<p class="gray">${magic.description }</p>
				<p>拥有个数: ${usermagic.count }</p>
				<div>
					<h4>访问方式</h4>
					<input id="way_visit" type="radio" name="visitway" value="visit" checked onclick="$('visitmsgs').style.display='none';$('visitpokes').style.display='none';" />
					<label for="way_visit">访问空间</label>
					<input id="way_poke" type="radio" name="visitway" value="poke" onclick="$('visitmsgs').style.display='none';$('visitpokes').style.display='';" />
					<label for="way_poke">打招呼</label>
					<input id="way_comment" type="radio" name="visitway" value="comment" onclick="$('visitmsgs').style.display='';$('visitpokes').style.display='none';" />
					<label for="way_comment">空间留言</label>
					<ul id="visitmsgs" style="display:none;">
						<li>请选择一条留言内容</li>
						<li>
							<input id="visitmsg_1" type="radio" name="visitmsg" value="踩踩" checked>
							<label for="visitmsg_1">踩踩</label>
						</li>
						<li>
							<input id="visitmsg_2" type="radio" name="visitmsg" value="好久不见">
							<label for="visitmsg_2">好久不见</label>
						</li>
						<li>
							<input id="visitmsg_3" type="radio" name="visitmsg" value="记得回踩哦">
							<label for="visitmsg_3">记得回踩哦</label>
						</li>
						<li>
							<input id="visitmsg_4" type="radio" name="visitmsg" value="我又来了">
							<label for="visitmsg_4">我又来了</label>
						</li>
						<li>
							<input id="visitmsg_5" type="radio" name="visitmsg" value="好漂亮的空间啊">
							<label for="visitmsg_5">好漂亮的空间啊</label>
						</li>
						<li>
							<input id="visitmsg_6" type="radio" name="visitmsg" value="最近在忙什么呢">
							<label for="visitmsg_6">最近在忙什么呢</label>
						</li>
					</ul>
					
					<table id="visitpokes" style="width:100%; display:none;">
					<thead>
					<td colspan="2">请选择一个招呼</td>
					</thead>
					<tbody>
					<tr>
					<c:forEach items="${icons}" var="value" varStatus="key">
					<td>
					<input id="visitpoke_${key.count }" type="radio" name="visitpoke" value="${key.count }"${key.count==1 ? ' checked' : '' } />
					<label for="visitpoke_${key.count }">${value }</label>
					</td>
					${key.count % 2 == 0 ? '</tr><tr>' : '' }
					</c:forEach>
					</tr>
					</tbody>
					</table>
				</div>
				<p class="btn_line">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<input type="hidden" name="refer" value="${sGlobal.refer }"/>
					<input type="hidden" name="usesubmit" value="1" />
					<input type="button" value="使用" class="submit" onclick="ajaxpost('magicuse_form_${mid }')" />
				</p>
			</div>
		</form>
	</div>
</c:when>
<c:when test="${op == 'show'}">
	<div style="width:400px;">
		<p>
			<c:choose>
			<c:when test="${param.visitway == 'poke'}">已给这些好友打招呼</c:when>
			<c:when test="${param.visitway == 'comment'}">已在这些好友空间留言</c:when>
			<c:otherwise>已访问这些好友空间</c:otherwise>
			</c:choose>
		</p>
		<ul class="avatar_list">
			<c:forEach items="${users}" var="value" >
			<li>
				<div class="avatar48"><a href="space.jsp?uid=${value.uid }">${jch:avatar2(value.uid,'small',false,sGlobal,sConfig) }</a></div>
				<p><a href="space.jsp?uid=${value.uid }" title="${sNames[value.uid]}">${sNames[value.uid]}</a></p>
			</li>
			</c:forEach>
		</ul>
	</div>
</c:when>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>