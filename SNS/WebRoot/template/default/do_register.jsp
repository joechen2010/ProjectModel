<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:set var="tpl_noSideBar" value="1" scope="request" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<script>
	function register(id, result) {
		if(result) {
			$('registersubmit').disabled = true;
			window.location.href = "${jumpurl}";
		} else {
			updateseccode();
		}
	}
</script>
<form id="registerform" name="registerform" action="do.jsp?ac=${sConfig.register_action}${empty url_plus ? '' : '&'}${url_plus}&ref" method="post" class="c_form">
	<table cellpadding="0" cellspacing="0" class="formtable">
		<caption>
			<h2>注册本站帐号</h2>
			<p>请完整填写以下信息进行注册。<br>注册完成后，该帐号将作为您在本站的通行帐号，您可以享受本站提供的各种服务。</p>
		</caption>
		<c:if test="${not empty invits}">
			<tr>
				<th width="100">好友邀请</th>
				<td>
					<a href="space.jsp?${url_plus}" target="_blank">${jch:avatar1(invits.uid,sGlobal,sConfig)}</a>
					<a href="space.jsp?${url_plus}" target="_blank">${sNames[invits.uid]}</a>
				</td>
			</tr>
		</c:if>
		<c:if test="${sConfig.seccode_register==1}">
			<c:choose>
				<c:when test="${sConfig.questionmode==1}">
					<tr>
						<th width="100" style="vertical-align: top;">请先回答问题</th>
						<td>
							<p>${jch:question(pageContext.request,pageContext.response)}</p>
							<input type="text" id="seccode" name="seccode" value="" class="t_input" onBlur="checkSeccode()" tabindex="1" autocomplete="off" />&nbsp;<span id="checkseccode">&nbsp;</span>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th width="100" style="vertical-align: top;">验证码</th>
						<td>
							<script>seccode();</script>
							<p>请输入上面的4位字母或数字，看不清可<a href="javascript:updateseccode()">更换一张</a></p>
							<input type="text" id="seccode" name="seccode" value="" class="t_input" onBlur="checkSeccode()" tabindex="1" autocomplete="off" />&nbsp;<span id="checkseccode">&nbsp;</span>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</c:if>
		<tr>
			<th width="100">用户名</th>
			<td><input type="text" id="username" name="username" value="" class="t_input" onBlur="checkUserName()" tabindex="2" />&nbsp;<span id="checkusername">&nbsp;</span></td>
		</tr>
		<tr>
			<th>注册密码</th>
			<td>
				<input type="password" name="password" id="password" value="" class="t_input" onBlur="checkPassword()" onkeyup="checkPwd(this.value);" tabindex="3" />&nbsp;<span id="checkpassword">&nbsp;</span><br />
				<style type="text/css">
					.psdiv0,.psdiv1,.psdiv2,.psdiv3,.psdiv4{position:relative;height:30px;color:#666}
					.strongdepict{position:absolute; width:300px;left:0px;top:3px}
					.strongbg{position:absolute;left:0px;top:22px;width:235px!important;width:234px;height:10px;background-color:#E0E0E0; font-size:0px;line-height:0px}
					.strong{float:left;font-size:0px;line-height:0px;height:10px}
					
					.psdiv0 span{display:none}
					.psdiv1 span{display:inline;color:#F00}
					.psdiv2 span{display:inline;color:#C48002}
					.psdiv3 span{display:inline;color:#2CA4DE}
					.psdiv4 span{display:inline;color:#063}
					
					.psdiv0 .strong{ width:0px}
					.psdiv1 .strong{ width:25%;background-color:#F00}
					.psdiv2 .strong{ width:50%;background-color:#F90}
					.psdiv3 .strong{ width:75%;background-color:#2CA4DE}
					.psdiv4 .strong{ width:100%;background-color:#063}
				</style>
				<div class="psdiv0" id="chkpswd">
					<div class="strongdepict">密码安全程度：<span id="chkpswdcnt">太短</span></div>
					<div class="strongbg"><div class="strong"></div></div>
				</div>
			</td>
		</tr>
		<tr>
			<th>再次输入密码</th>
			<td><input type="password" id="password2" name="password2" value="" class="t_input" onBlur="checkPassword2()" tabindex="4" />&nbsp;<span id="checkpassword2">&nbsp;</span></td>
		</tr>
		<tr>
			<th>邮箱</th>
			<td>
				<input type="text" id="email" name="email" value="@" class="t_input" tabindex="5" />
				<br>请准确填入您的邮箱，在忘记密码，或者您使用邮件通知功能时，会发送邮件到该邮箱。
			</td>
		</tr>
		<c:if test="${not empty registerRule}">
			<tr>
				<th>服务条款</th>
				<td>
					<div name="rule" style="border: 1px solid #C3C3C3; width: 500px; height: 100px; overflow: auto; padding: 5px;">${registerRule}</div>
					<input type="checkbox" name="accede" id="accede" value="1">我已阅读，并同意以上服务条款
					<script type="text/javascript">
						function checkClause() {
							if($('accede').checked) {
								return true;
							} else {
								alert("您必须同意服务条款后才能注册");
								return false;
							}
						}
					</script>
				</td>
			</tr>
		</c:if>
		<tr>
			<th>&nbsp;</th>
			<td>
				<input type="hidden" name="refer" value="space.jsp?do=home" />
				<input type="submit" id="registersubmit" name="registersubmit" value="注册新用户" class="submit" onclick="<c:if test='${not empty registerRule }'>if(!checkClause()){return false;}</c:if>ajaxpost('registerform', 'register');" tabindex="6" />
			</td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td id="__registerform" style="color: red; font-weight: bold;"></td>
		</tr>
	</table>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>

<script type="text/javascript">
	$('username').focus();
	var lastUserName = lastPassword = lastEmail = lastSecCode = '';
	function checkUserName() {
		var userName = $('username').value;
		if(userName == lastUserName) {
			return;
		} else {
			lastUserName = userName;
		}
		var cu = $('checkusername');
		var unLen = userName.replace(/[^\x00-\xff]/g, "**").length;

		if(unLen < 3 || unLen > 15) {
			warning(cu, unLen < 3 ? '用户名小于3个字符' : '用户名超过 15 个字符');
			return;
		}
		ajaxresponse('checkusername', 'op=checkusername&username=' + (is_ie && document.charset == 'utf-8' ? encodeURIComponent(userName) : userName));
	}
	function checkPassword(confirm) {
		var password = $('password').value;
		if(!confirm && password == lastPassword) {
			return;
		} else {
			lastPassword = password;
		}
		var cp = $('checkpassword');
		if(password == '' || /[\'\"\\]/.test(password)) {
			warning(cp, '密码空或包含非法字符');
			return false;
		} else {
			cp.style.display = '';
			cp.innerHTML = '<img src="image/check_right.gif" width="13" height="13">';
			if(!confirm) {
				checkPassword2(true);
			}
			return true;
		}
	}
	function checkPassword2(confirm) {
		var password = $('password').value;
		var password2 = $('password2').value;
		var cp2 = $('checkpassword2');
		if(password2 != '') {
			checkPassword(true);
		}
		if(password == '' || (confirm && password2 == '')) {
			cp2.style.display = 'none';
			return;
		}
		if(password != password2) {
			warning(cp2, '两次输入的密码不一致');
		} else {
			cp2.style.display = '';
			cp2.innerHTML = '<img src="image/check_right.gif" width="13" height="13">';
		}
	}
	function checkSeccode() {
		var seccodeVerify = $('seccode').value;
		if(seccodeVerify == lastSecCode) {
			return;
		} else {
			lastSecCode = seccodeVerify;
		}
		ajaxresponse('checkseccode', 'op=checkseccode&seccode=' + (is_ie && document.charset == 'utf-8' ? encodeURIComponent(seccodeVerify) : seccodeVerify));
	}
	function ajaxresponse(objname, data) {
		var x = new Ajax('XML', objname);
		x.get('do.jsp?ac=${sConfig.register_action}&' + data, function(s){
			var obj = $(objname);
			s = trim(s);
			if(s.indexOf('succeed') > -1) {
				obj.style.display = '';
				obj.innerHTML = '<img src="image/check_right.gif" width="13" height="13">';
				obj.className = "warning";
			} else {
				warning(obj, s);
			}
		});
	}
	function warning(obj, msg) {
		if((ton = obj.id.substr(5, obj.id.length)) != 'password2') {
			$(ton).select();
		}
		obj.style.display = '';
		obj.innerHTML = '<img src="image/check_error.gif" width="13" height="13"> &nbsp; ' + msg;
		obj.className = "warning";
	}

	function checkPwd(pwd){

		if (pwd == "") {
			$("chkpswd").className = "psdiv0";
			$("chkpswdcnt").innerHTML = "";
		} else if (pwd.length < 3) {
			$("chkpswd").className = "psdiv1";
			$("chkpswdcnt").innerHTML = "太短";
		} else if(!isPassword(pwd) || !/^[^%&]*$/.test(pwd)) {
			$("chkpswd").className = "psdiv0";
			$("chkpswdcnt").innerHTML = "";
		} else {
			var csint = checkStrong(pwd);
			switch(csint) {
				case 1:
					$("chkpswdcnt").innerHTML = "很弱";
					$( "chkpswd" ).className = "psdiv"+(csint + 1);
					break;
				case 2:
					$("chkpswdcnt").innerHTML = "一般";
					$( "chkpswd" ).className = "psdiv"+(csint + 1);
					break;
				case 3:		
					$("chkpswdcnt").innerHTML = "很强";
					$("chkpswd").className = "psdiv"+(csint + 1);
					break;
			}
		}
	}
	function isPassword(str){
		if (str.length < 3) return false;
		var len;
		var i;
		len = 0;
		for (i=0;i<str.length;i++){
			if (str.charCodeAt(i)>255) return false;
		}
		return true;
	}
	function charMode(iN){ 
		if (iN>=48 && iN <=57)
		return 1; 
		if (iN>=65 && iN <=90)
		return 2; 
		if (iN>=97 && iN <=122)
		return 4; 
		else 
		return 8;
	}
	function bitTotal(num){ 
		modes=0; 
		for (i=0;i<4;i++){ 
			if (num & 1) modes++; 
			num>>>=1; 
		} 
		return modes; 
	}
	function checkStrong(pwd){ 
		modes=0; 
		for (i=0;i<pwd.length;i++){
			modes|=charMode(pwd.charCodeAt(i)); 
		} 
		return bitTotal(modes);
	}
</script>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />