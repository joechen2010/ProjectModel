<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:choose>
<c:when test="${param.op== 'resend'}">
<h1>重发邮件</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__resendform_${id}">
<form method="post" id="resendform_${id}" name="resendform_${id}" action="cp.jsp?ac=invite&op=resend&id=${id}">
	<p>确定重新发送邀请邮件吗？</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}">
		<c:choose>
		<c:when test="${sGlobal.inajax>0}">
		<input type="hidden" name="resendsubmit" value="true" />
		<button name="resendsubmit_btn" type="button" class="submit" value="true" onclick="ajaxpost('resendform_${id}', 'resend_mail', 2000)">重发</button>
		</c:when>
		<c:otherwise>
		<button name="resendsubmit" type="submit" class="submit" value="true">确定</button>
		</c:otherwise>
		</c:choose>
	</p>
	
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:when test="${param.op=='delete'}">

<h1>删除记录</h1>
<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
<div class="popupmenu_inner" id="__deleteform_${id}">
<form method="post" id="deleteform_${id}" name="deleteform_${id}" action="cp.jsp?ac=invite&op=delete&id=${id}">
	<p>确定要删除该邀请记录吗？</p>
	<p>删除该邀请记录后,您的好友将不能通过<br/>原邀请记录链接注册成为您的好友!</p>
	<p class="btn_line">
		<input type="hidden" name="refer" value="${sGlobal.refer}">
		<c:choose>
		<c:when test="${sGlobal.inajax>0}">
		<input type="hidden" name="deletesubmit" value="true" />
		<button name="deletesubmit_btn" type="button" class="submit" value="true" onclick="ajaxpost('deleteform_${id}', 'resend_mail', 2000)">删除</button>
		</c:when>
		<c:otherwise>
		<button name="deletesubmit" type="submit" class="submit" value="true">删除</button>
		</c:otherwise>
		</c:choose>
	</p>
	
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
</div>

</c:when>
<c:otherwise>
<h2 class="title"><img src="image/icon/friend.gif">好友</h2>
<div class="tabs_header">
	<ul class="tabs">
		<li><a href="space.jsp?uid=${space.uid}&do=friend"><span>好友列表</span></a></li>
		<li><a href="cp.jsp?ac=friend&op=search"><span>查找好友</span></a></li>
		<li><a href="cp.jsp?ac=friend&op=find"><span>可能认识的人</span></a></li>
		<li class="active"><a href="cp.jsp?ac=invite"><span>邀请好友</span></a></li>
		<li><a href="cp.jsp?ac=friend&op=request"><span>好友请求</span></a></li>
	</ul>
</div>

<div id="content">	
	<form method="post" action="cp.jsp?ac=invite&appid=${appid}&ref" class="c_form">
		<c:if test="${!empty get_invite}">
		<div class="c_mgs"><div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
			每邀请成功一个好友，就可获得 <strong>${get_invite}</strong> 个奖励积分。赶快行动吧！
		</div></div></div></div></div>
		</c:if>
		<table cellspacing="0" cellpadding="0" class="formtable" width="100%">
			<caption>
				<h2><c:choose><c:when test="${appid!=0}">邀请我的好友一起玩${appinfo.appname}</c:when><c:otherwise>我的好友邀请链接</c:otherwise></c:choose></h2>
				<p>
				<div class="composer_header">
					<!--<c:if test="${appid!=0}">
					<div class="ar_r_t"><div class="ar_l_t"><div class="ar_r_b"><div class="ar_l_b"><img src="http://appicon.jsprun.com/logos/${appid}" alt="${appinfo.appname}" /></div></div></div></div>
					</c:if>
					--><p>
						您可以通过QQ、MSN等IM工具，或者发送邮件，把下面的链接告诉你的好友，邀请他们加入进来。
						<table cellspacing="0" cellpadding="0" >
						<c:choose>
						<c:when test="${credit!=0}">
							<c:choose>
							<c:when test="${not empty list_str}">
							<tr>
								<td>一行一个邀请链接，一个链接只能使用一次。<br/><textarea name="invite_urls" rows="4" style="width:98%;">${list_str}</textarea></td>
							</tr>
							</c:when>
							<c:otherwise>
							<tr><td>还没有邀请码。</td></tr>
							</c:otherwise>
							</c:choose>
							<tr>
								<td><img src="image/credit.gif" align="absmiddle"> 每一个邀请码需要<strong>扣减积分 ${credit} 个/每条</strong>，您现在拥有积分 <a href="cp.jsp?ac=credit">${space.credit}</a> 个。</td>
							</tr>
							<tr class="notice">
								<td>获取邀请码(还可获取 ${maxinvitenum} 个)：<input type="text" name="invitenum" value="1" size="5" class="t_input" /> <input type="submit" name="invitesubmit" value="获取" class="submit" /></td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr><td style="font-size:14px;font-weight:bold;"><a onclick="javascript:setCopy('${mailvar[4]}');return false;" href="${mailvar[4]}">${mailvar[4]}</a></td></tr>
						</c:otherwise>
						</c:choose>
						</table>
					</p>
				</div>
				
				</p>
			</caption>
			
			
		</table>
		
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
	<form method="post" action="cp.jsp?ac=invite&type=mail&appid=${appid}&ref" class="c_form">
		<table cellspacing="0" cellpadding="0" class="formtable" width="100%" >
			<caption>
				<h2>给好友发送 Email 邀请<c:if test="${appid>0}">好友一起玩${appinfo.appname}</c:if></h2>
				<p>通过Email发送邮件的方式，邀请你的好友。<br>多个Email使用","分割。</p>
				<c:if test="${!jch:jchEmpty(pay.invite)}">
				<p>
					<img src="image/credit.gif" align="absmiddle"> 发送一个邮件需要<strong>扣减积分 ${pay.invite} 个/每条</strong>，您现在拥有积分 <a href="cp.jsp?ac=credit">${space.credit}</a> 个。
				</p>
				</c:if>
			</caption>
			<tr>
				<td>
					<script type="text/javascript" src="image/contact/contact.js" charset="utf-8"></script>
					<div class="r_option"><a href="#"  onclick="MYCONTACT.showChooser('email', false,null); return false"><img src="image/contact/contact_button.gif" style="margin: 5px 0 0;" alt="从地址簿添加" /></a></div><div><br />好友Email地址</div>
					<textarea name="email" id="email" rows="5" style="width:99%;"></textarea>
				</td>
			</tr>
			<tr>
				<td>
					想对好友说的话<br/>
					<input type="text" name="saymsg" id="saymsg" onkeyup="showPreview(this.value, 'sayPreview')" class="t_input" style="width:98%;">
				</td>
			</tr>
			<tr>
				<td><input type="submit" name="emailinvite" value="邀请" class="submit"></td>
			</tr>
			<tr>
				<td>
					<div class="title" style="margin: 10px 0;">预览邀请函</div>
					<div class="c_mgs"><div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b">
						<table border="0">
						<tr>
						<td valign="top">${mailvar[0]}</td>
						<td valign="top">
						<c:choose>
						<c:when test="${appid>0}">
						<h3>Hi，我是${mailvar[1]}，在${mailvar[2]}上玩${appinfo.appname}，邀请您也加入一起玩。</h3><br>
						</c:when>
						<c:otherwise>
						<h3>Hi，我是${mailvar[1]}，在${mailvar[2]}上建立了个人主页，邀请你也加入并成为我的好友。</h3><br>
						请加入到我的好友中，你就可以通过我的个人主页了解我的近况，分享我的照片，随时与我保持联系。<br>
						</c:otherwise>
						</c:choose>
						<br>
						邀请附言：<br>
						<span id="sayPreview">${mailvar[3]}</span>
						<br><br>
						<strong>请你点击以下链接，接受好友邀请<c:if test="${appid>0}">一起玩${appinfo.appname}</c:if>：</strong><br>
						${mailvar[4]}<br>
						<br>
						<strong>如果你拥有${mailvar[2]}上面的账号，请点击以下链接查看我的个人主页：</strong><br>
						${mailvar[5]}<br>
						</td></tr></table>
					</div></div></div></div></div>
				</td>
			</tr>
		</table>
		
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
	</form>
</div>

<div id="sidebar">
		
		<div class="sidebox">
			<h2 class="title">已邀请好友</h2>
			<c:choose>
			<c:when test="${fn:length(flist)< 24}">
			<ul class="avatar_list">
				<c:forEach items="${flist}" var="value">
				<li>
					<div class="avatar48"><a href="space.jsp?uid=${value.fuid}&do=doing">${jch:avatar1(value.fuid,sGlobal, sConfig)}</a></div>
					<p><a href="space.jsp?uid=${value.fuid}" title="${sNames[value.fuid]}">${sNames[value.fuid]}</a></p>
				</li>
				</c:forEach>
			</ul>
			</c:when>
			<c:otherwise>
			<div>
			<c:forEach items="${flist}" var="value" varStatus="temp">
			<c:if test="${temp.index>0}">、</c:if><a href="space.jsp?uid=${value.fuid}" title="${sNames[value.fuid]}">${sNames[value.fuid]}</a>
			</c:forEach>
			</div>
			</c:otherwise>
			</c:choose>
		</div>

		<c:if test="${not empty maillist}">
		<div class="sidebox">
			<h2 class="title">尚未邀请成功的好友邮件</h2>
			<ul class="sendmail">
				<c:forEach items="${maillist}" var="value" varStatus="temp">
				<li id="sendmail_${value.id}_li">
					<a href="cp.jsp?ac=invite&op=resend&id=${value.id}" id="mail_invite_${value.id}" class="c_resend" onclick="ajaxmenu(event, this.id)" title="重发">重发</a>
					<a href="javascript:;" title="链接" class="c_link" onclick="javascript:setCopy('${value.url}');return false;">链接</a>
					<a href="cp.jsp?ac=invite&op=delete&id=${value.id}" id="del_invite_${value.id}" class="c_delete" onclick="ajaxmenu(event, this.id)" title="删除">删除</a>
					${value.email}
				</li>
				</c:forEach>
			</ul>
		</div>
		</c:if>
</div>
</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>