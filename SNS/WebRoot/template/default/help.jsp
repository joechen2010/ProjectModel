<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<div id="content">
 <table cellpadding="0" cellspacing="0" class="formtable">
  <c:choose><c:when test="${ac == 'register'}">
   <caption><h2>注册</h2></caption>
   <tr><td>
    注册非常简单，只需选择自己想要用户名、密码，输入一个可用的邮箱地址，就可以立即注册成为站内一员。<br /><br />
    <img src="image/help/register.gif"><br /><br />
   </td></tr>
  </c:when><c:when test="${ac == 'home'}">
   <caption><h2>在首页查看好友的最新动态和通知</h2></caption>
   <tr><td>
    <h2>好友的最新动态</h2>
    登录后，在首页你可以一目了然地看到好友们的最新动态，包括他们最新发布的记录、日志、上传的图片、分享以及发起的话题等等。<br /><br />
    <img src="image/help/home.gif"><br /><br />
   </td></tr>
   <tr><td>
    <h2>最新通知</h2>
    在首页右侧，你可以处理最新的好友申请、站内的通知和朋友们的招呼等等。<br /><br />
    <img src="image/help/home2.gif"><br /><br />
   </td></tr>
  </c:when><c:when test="${ac == 'space'}">
   <caption><h2>我的主页</h2></caption>
   <tr><td>
    这里记录着你的全部动作，是你的朋友了解你的窗口。你可以在“我的主页”填写个人资料、完善个人群组等等。<br /><br />
    <img src="image/help/space.gif"><br /><br />
   </td></tr>
  </c:when><c:when test="${ac == 'doing'}">
   <caption><h2>记录</h2></caption>
   <tr><td>
    可以非常方便的用一句话记录自己的点点滴滴与好友分享。<br /><br />
    <img src="image/help/doing.gif"><br /><br />
   </td></tr>
  </c:when><c:when test="${ac == 'blog'}">
   <caption>
    <h2>日志</h2>
   </caption>
   <tr><td>
    可以撰写图文并茂的日志。<br /><br />
    <img src="image/help/blog.gif"><br /><br />
   </td></tr>
  </c:when><c:when test="${ac == 'album'}">
   <caption><h2>相册</h2></caption>
   <tr><td>
    可以非常方便的批量上传自己的图片，也可进行大头贴拍照。<br /><br />
    <img src="image/help/album.gif"><br /><br />
   </td></tr>
  </c:when><c:when test="${ac == 'share'}">
   <caption><h2>分享</h2></caption>
   <tr><td>
    轻松分享站内的主页、日志、相册、图片、话题、群组等各类信息。<br /><br />
    <img src="image/help/share1.gif"><br /><br />
    也可以与好友一起分享一个网址。<br /><br />
    <img src="image/help/share2.gif"><br /><br />
   </td></tr>
  </c:when><c:when test="${ac == 'mtag'}">
   <caption><h2>群组</h2></caption>
   <tr><td>
    加入自己感兴趣的群组，可以与更多的朋友一起讨论话题。<br /><br />
    <img src="image/help/mtag1.gif" /><br /><br />
    不同的群组随你选择，由你做主。<br /><br />
    <img src="image/help/mtag2.gif"><br /><br />
    加入群组后就可以发起话题了。<br /><br />
    <img src="image/help/mtag3.gif"><br /><br />
   </td></tr>
   </c:when><c:when test="${ac == 'gift'}">
   <caption><h2>礼物</h2></caption>
   <tr><td>
    你每邀请一个朋友到${sConfig.sitename}注册，即可获得3次赠送高级礼物的机会。
   </td></tr>
  </c:when><c:when test="${ac == 'cp'}">
   <caption><h2>空间设置</h2></caption>
   <tr><td>
    可以设置自己的空间属性、选择主页风格、隐私设置。<br /><br />
    <img src="image/help/cp.gif"><br /><br />
    通过隐私设置，自己的主页由谁看，自己说了算。<br /><br />
    <img src="image/help/cp2.gif"><br /><br />
   </td></tr>
  </c:when></c:choose>
 </table>
</div>
<div id="sidebar"><div class="sidebox">
 <h2 class="title">帮助</h2>
 <ul class="line_list">
  <li${active_register}><a href="help.jsp?ac=register">注册</a></li>
  <li${active_home}><a href="help.jsp?ac=home">我的首页</a></li>
  <li${active_space}><a href="help.jsp?ac=space">我的主页</a></li>
  <li${active_doing}><a href="help.jsp?ac=doing">记录</a></li>
  <li${active_blog}><a href="help.jsp?ac=blog">日志</a></li>
  <li${active_album}><a href="help.jsp?ac=album">相册</a></li>
  <li${active_share}><a href="help.jsp?ac=share">分享</a></li>
  <li${active_mtag}><a href="help.jsp?ac=mtag">群组</a></li>
  <li${active_gift}><a href="help.jsp?ac=gift">礼物</a></li>
  <li${active_cp}><a href="help.jsp?ac=cp">空间设置</a></li>
 </ul>
</div></div>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />