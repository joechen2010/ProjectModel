<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:set var="tpl_noSideBar" value="1" scope="request" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<div id="network">
	<style type="text/css">
		#radomuser .nbox_c { width: 970px; width: 966px; }
			#radomuser ul { padding: 10px 0 0 10px; }
				#radomuser li { float: left; width: 190px; height: 70px; color: #999; overflow: hidden; }
	</style>
	<script type="text/javascript">
		function setintro(type) {
			var intro = '';
			var bPosition = '';
			if(type == 'doing') {
				intro = '用一句话记录自己生活中的点点滴滴';
				bPosition = '0';
			} else if(type == 'mtag') {
				intro = '创建自己的小圈子，与大家交流感兴趣的话题';
				bPosition = '175px';
			} else if(type == 'pic') {
				intro = '上传照片，分享生活中的精彩瞬间';
				bPosition = '55px';
			} else if(type == 'app') {
				intro = '与好友一起玩转游戏和应用，增加好友感情';
				bPosition = '118px';
			} else {
				intro = '马上注册，与好友分享日志、照片，一起玩转游戏';
				bPosition = '0';
			}
			$('guest_intro').innerHTML = intro + '......';
			$('guest_intro').style.backgroundPosition = bPosition + ' 100%'
		}
	</script>
	<div id="guestbar" class="nbox">
		<div class="nbox_c">
			<p id="guest_intro">马上注册，与好友分享日志、照片，一起玩转游戏......</p>
			<a href="do.jsp?ac=${sConfig.register_action}" id="regbutton" onmouseover="setintro('register');">马上注册</a>
			<p id="guest_app">
				<a href="javascript:;" class="appdoing" onmouseover="setintro('doing');">记录</a>
				<a href="javascript:;" class="appphotos" onmouseover="setintro('pic');">照片</a>
				<a href="javascript:;" class="appgames" onmouseover="setintro('app');">游戏</a>
				<a href="javascript:;" class="appgroups" onmouseover="setintro('mtag');">群组</a>
			</p>
		</div>
		<div class="nbox_s side_rbox" id="nlogin_box">
			<h3 class="ntitle">请登录</h3>
			<div class="side_rbox_c">
				<form name="loginform" action="do.jsp?ac=${sConfig.login_action}${empty url_plus ? '' : '&'}${url_plus}&ref" method="post">
					<p><label for="username">用户名</label> <input type="text" name="username" id="username" class="t_input" value="${memberName}" /></p>
					<p><label for="password">密　码</label> <input type="password" name="password" id="password" class="t_input" value="${password}" /></p>
					<p class="checkrow"><input type="checkbox" id="cookietime" name="cookietime" value="315360000"${cookieCheck} style="margin-bottom: -2px;" /><label for="cookietime">下次自动登录</label></p>
					<p class="submitrow">
						<input type="hidden" name="refer" value="space.jsp?do=home" />
						<input type="submit" id="loginsubmit" name="loginsubmit" value="登录" class="submit" />
						<a href="do.jsp?ac=lostpasswd">忘记密码?</a>
						<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					</p>
				</form>
			</div>
		</div>
	</div>
	<c:if test="${myAppCount>0}">
		<div class="nbox">
			<div class="nbox_c" style="border: none;">
				<ul class="applist">
					<c:forEach items="${myApps}" var="myApp">
						<li>
							<p class="aimg"><a href="userapp.jsp?id=${myApp.appid}" target="_blank"><img src="http://appicon.jsprun.com/logos/${myApp.appid}" alt="${myApp.appname}" /></a></p>
							<p><a href="userapp.jsp?id=${myApp.appid}" target="_blank">${myApp.appname}</a></p>
						</li>
					</c:forEach>
				</ul>
			</div>
			<div class="susb">
				<div class="ye_r_t"><div class="ye_l_t"><div class="ye_r_b"><div class="ye_l_b"><div class="appmo">
					<p>共有 <span>${myAppCount}</span> 个应用</p>
					<p class="appmobutton"><a href="cp.jsp?ac=userapp&my_suffix=%2Fapp%2Flist">查看更多应用</a></p>
				</div></div></div></div></div>
			</div>
		</div>
	</c:if>
	<div id="radomuser" class="nbox">
		<div class="nbox_c">
			<h2 class="ntitle">欢迎您，与大家一起交流与分享吧</h2>
			<ul class="s_clear">
				<c:forEach items="${spaces}" var="space">
					<li>
						<div class="d_avatar avatar48"><a href="space.jsp?uid=${space.uid}" title="${sNames[space.uid]}" target="_blank">${jch:avatar1(space.uid,sGlobal,sConfig)}</a></div>
						<p><a href="space.jsp?uid=${space.uid}" target="_blank">${sNames[space.uid]}</a></p>
						<p>${space.resideprovince} ${space.residecity}</p>
						<p>${space.friendnum} 位好友</p>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
</div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />