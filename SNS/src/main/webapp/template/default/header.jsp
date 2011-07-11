<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<c:if test="${sGlobal.inajax==0}">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
 <title>${navtitle}<c:if test="${not empty sNames[space.uid]}">${sNames[space.uid]} - </c:if>${sConfig.sitename} - Powered by JavaCenter Home</title>
 <meta http-equiv="content-type" content="text/html; charset=${jchConfig.charset}">
 <meta http-equiv="x-ua-compatible" content="ie=7">
 <script language="javascript" type="text/javascript" src="source/script_cookie.js"></script>
 <script language="javascript" type="text/javascript" src="source/script_common.js"></script>
 <script language="javascript" type="text/javascript" src="source/script_menu.js"></script>
 <script language="javascript" type="text/javascript" src="source/script_ajax.js"></script>
 <script language="javascript" type="text/javascript" src="source/script_face.js"></script>
 <script language="javascript" type="text/javascript" src="source/script_manage.js"></script>
 <style type="text/css">
  @import url(template/default/style.css);
  @import url(template/default/gift.css);
  <c:if test="${!empty tpl_css}">@import url(template/default/${tpl_css}.css);</c:if>
  <c:choose>
   <c:when test="${not empty sGlobal.space_theme}">@import url(theme/${sGlobal.space_theme}/style.css);</c:when>
   <c:when test="${sConfig.template != 'default'}">@import url(template/${sConfig.template}/style.css);</c:when>
  </c:choose>
  <c:if test="${!empty sGlobal.space_css}">${sGlobal.space_css}</c:if>
 </style>
 <link rel="shortcut icon" href="favicon.ico">
 <link rel="edituri" type="application/rsd+xml" title="rsd" href="xmlrpc.jsp?rsd=${space.uid}">
</head>
<body>
 <div id="append_parent"></div>
 <div id="ajaxwaitid"></div>
 <div id="header">
  <c:if test="${!empty globalAd.header}"><div id="ad_header">${jch:showAd(globalAd.header)}</div></c:if>
  <div class="headerwarp">
   <h1 class="logo"><a href="index.jsp"><img src="template/${sConfig.template}/image/logo.gif" alt="${sConfig.sitename}"></a></h1>
   <ul class="menu">
  <c:choose>
   <c:when test="${sGlobal.supe_uid>0}">
    <li><a href="space.jsp?do=home">首页</a></li>
    <li><a href="space.jsp">个人主页</a></li>
    <li><a href="space.jsp?do=friend">好友</a></li>
    <li class="topdropmenu" id="topmenu" onmouseover="showMenu(this.id)"><a href="space.jsp?do=top">排行榜</a></li>
    <li><a href="network.jsp">随便看看</a></li>
   </c:when>
   <c:otherwise><li><a href="index.jsp">首页</a></li></c:otherwise>
  </c:choose>
  <c:if test="${!empty sGlobal.appmenu}">
   <c:choose>
    <c:when test="${!empty sGlobal.appmenus}"><li class="dropmenu" id="jcappmenu" onclick="showMenu(this.id)"><a href="javascript:;">站内导航</a></li></c:when>
    <c:otherwise><li><a target="_blank" href="${sGlobal.appmenu.url}" title="${sGlobal.appmenu.name}">${sGlobal.appmenu.name}</a></li></c:otherwise>
   </c:choose>
  </c:if>
  <c:choose>
   <c:when test="${sGlobal.supe_uid>0}">
    <li><a href="space.jsp?do=pm${sGlobal.member.newpm>0 ? '&filter=newpm' : ''}" id="msgtips">消息${sGlobal.member.newpm>0 ? '(新)' : ''}</a></li>
    <c:if test="${sGlobal.member.allnotenum>0}"><li class="notify" id="membernotemenu" onmouseover="showMenu(this.id)"><a href="space.jsp?do=notice">${sGlobal.member.allnotenum}个提醒</a></li></c:if>
   </c:when>
   <c:otherwise><li><a href="help.jsp">帮助</a></li></c:otherwise>
  </c:choose>
   </ul>
   <c:if test="${sGlobal.supe_uid>0}">
   <ul id="topmenu_menu" class="topdropmenu_drop" style="display:none">
    <li><a href="space.jsp?do=top">竞价排行</a></li>
    <li><a href="space.jsp?do=top&view=mm">美女排行</a></li>
    <li><a href="space.jsp?do=top&view=gg">帅哥排行</a></li>
    <li><a href="space.jsp?do=top&view=experience">经验排行</a></li>
    <li><a href="space.jsp?do=top&view=credit">积分排行</a></li>
    <li><a href="space.jsp?do=top&view=friendnum">好友数排行</a></li>
    <li><a href="space.jsp?do=top&view=viewnum">访问量排行</a></li>
    <li><a href="space.jsp?do=top&view=online">在线成员</a></li>
    <li class="lastlist"><a href="space.jsp?do=top&view=updatetime">全部成员</a></li>
	</ul>
	</c:if>
   <div class="nav_account">
  <c:choose>
   <c:when test="${sGlobal.supe_uid>0}">
    <a href="space.jsp?uid=${sGlobal.supe_uid}" class="login_thumb">${jch:avatar1(sGlobal.supe_uid,sGlobal,sConfig)}</a>
    <a href="space.jsp?uid=${sGlobal.supe_uid}" class="loginName">${sNames[sGlobal.supe_uid]}</a>
    <c:if test="${sGlobal.member.credit>0}"><a href="cp.jsp?ac=credit" style="font-size:11px;padding:0 0 0 5px;"><img src="image/credit.gif">${sGlobal.member.credit}</a></c:if><br>
    <c:if test="${sConfig.closeinvite==0}"><a href="cp.jsp?ac=invite">邀请</a></c:if>
    <a href="cp.jsp?ac=task">任务</a> 
    <a href="cp.jsp?ac=magic">道具</a>
    <a href="cp.jsp">设置</a> 
    <a href="cp.jsp?ac=common&op=logout&uhash=${sGlobal.uhash}">退出</a>
   </c:when>
   <c:otherwise>
    <a href="do.jsp?ac=${sConfig.register_action}" class="login_thumb">${jch:avatar1(sGlobal.supe_uid,sGlobal,sConfig)}</a> 欢迎您<br>
    <a href="do.jsp?ac=${sConfig.login_action}">登录</a> | 
    <a href="do.jsp?ac=${sConfig.register_action}">注册</a>
   </c:otherwise>
  </c:choose>
   </div>
  </div>
 </div>
 <div id="wrap">
<c:if test="${empty tpl_noSideBar}">
  <div id="main">
   <div id="app_sidebar">
  <c:choose>
   <c:when test="${sGlobal.supe_uid>0}">
    <ul class="app_list" id="default_userapp">
     <li><img src="image/app/feed.gif"><a href="space.jsp?do=feed">动态</a></li>
     <li><img src="image/app/doing.gif"><a href="space.jsp?do=doing">记录</a></li>
     <li><img src="image/app/album.gif"><a href="space.jsp?do=album">相册</a><em><a href="cp.jsp?ac=upload" class="gray">上传</a></em></li>
     <li><img src="image/app/blog.gif"><a href="space.jsp?do=blog">日志</a><em><a href="cp.jsp?ac=blog" class="gray">发表</a></em></li>
     <li><img src="image/app/poll.gif"><a href="space.jsp?do=poll">投票</a><em><a href="cp.jsp?ac=poll" class="gray">发起</a></em></li>
     <li><img src="image/app/mtag.gif"><a href="space.jsp?do=mtag">群组</a><em><a href="cp.jsp?ac=thread" class="gray">话题</a></em></li>
     <li><img src="image/app/event.gif"><a href="space.jsp?do=event">活动</a><em><a href="cp.jsp?ac=event" class="gray">发起</a></em></li>
     <li><img src="image/app/topic.gif"><a href="space.jsp?do=topic">热闹</a><em><a href="cp.jsp?ac=topic" class="gray">添加</a></em></li>
     <li><img src="image/app/share.gif"><a href="space.jsp?do=share">分享</a><em><a href="space.jsp?do=share&view=me" class="gray">添加</a></em></li>
     <li><img src="image/app/gift.gif"><a href="space.jsp?do=gift">礼物</a><em><a href="cp.jsp?ac=gift" class="gray">送礼</a></em></li>
     <li><img src="image/app/addrbook.gif"/><a href="space.jsp?do=addrbook">通讯录</a></li>
     <li><img src="image/app/wall.gif"/><a href="space.jsp?do=wall">留言板</a></li>
    </ul>
    <ul class="app_list topline" id="my_defaultapp">
   <c:if test="${sConfig.my_status>0}">
    <c:forEach items="${globalUserAPP}" var="userAPP">
     <li><img src="http://appicon.jsprun.com/icons/${userAPP.value.appid}"><a href="userapp.jsp?id=${userAPP.value.appid}">${userAPP.value.appname}</a></li>
    </c:forEach>
   </c:if>
    </ul>
   <c:if test="${sConfig.my_status>0}">
    <ul class="app_list topline" id="my_userapp">
    <c:forEach items="${sGlobal.my_menu}" var="menu">
     <li id="userapp_li_${menu.value.appid}"><img src="http://appicon.jsprun.com/icons/${menu.value.appid}"><a href="userapp.jsp?id=${menu.value.appid}" title="${menu.value.appname}">${menu.value.appname}</a></li>
    </c:forEach>
    </ul>
   </c:if>
    <c:if test="${sConfig.my_menu_more>0}"><p class="app_more"><a href="javascript:;" id="a_app_more" onclick="userapp_open();" class="off">展开</a></p></c:if>
   <c:if test="${sConfig.my_status>0}">
    <div class="app_m">
     <ul>
      <li><img src="image/app_add.gif"><a href="cp.jsp?ac=userapp&my_suffix=%2Fapp%2Flist" class="addApp">添加应用</a></li>
      <li><img src="image/app_set.gif"><a href="cp.jsp?ac=userapp&op=menu" class="myApp">管理应用</a></li>
     </ul>
    </div>
   </c:if>
   </c:when>
   <c:otherwise>
    <div class="bar_text">
     <form id="loginform" name="loginform" action="do.jsp?ac=${sConfig.login_action}&ref" method="post">
      <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
      <p class="title">登录站点</p>
      <p>用户名</p>
      <p><input type="text" name="username" id="username" class="t_input" size="15" value="" /></p>
      <p>密码</p>
      <p><input type="password" name="password" id="password" class="t_input" size="15" value="" /></p>
      <p><input type="checkbox" id="cookietime" name="cookietime" value="315360000" checked /><label for="cookietime">记住我</label></p>
      <p><input type="submit" id="loginsubmit" name="loginsubmit" value="登录" class="submit" /> <input type="button" name="regbutton" value="注册" class="button" onclick="urlto('do.jsp?ac=${sConfig.register_action}');"></p>
     </form>
    </div>
   </c:otherwise>
  </c:choose>
   </div>
   <div id="mainarea">
    <c:if test="${not empty globalAd.contenttop}"><div id="ad_contenttop">${jch:showAd(globalAd.contenttop)}</div></c:if>
</c:if>
</c:if>