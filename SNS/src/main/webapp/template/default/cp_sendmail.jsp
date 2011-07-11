<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_header.jsp')}"/>
<form action="cp.jsp?ac=sendmail&ref" method="post" class="c_form">
 <c:choose><c:when test="${jch:jchEmpty(space.emailcheck)}">
  <table cellspacing="0" cellpadding="0" class="formtable">
   <caption>
    <h2>您首先需要激活您的邮箱</h2>
    <p>填写您常用的邮箱，系统会给您的邮箱发送一封含有激活链接的邮件，把激活链接复制到浏览器进行访问就可以激活您的邮箱,然后你就能可以设置邮件提醒。</p>
   </caption>
   <tr><td><a href="cp.jsp?ac=profile&op=contact">点这里验证激活我的邮箱 ${space.email}</a></td></tr>
  </table>
 </c:when><c:otherwise>
  <table cellspacing="0" cellpadding="0" class="formtable">
   <caption>
    <h2>邮件提醒设置</h2>
    <p>系统会在您超过 ${sConfig.sendmailday} 天没有登录站点时，自动给您发送提醒邮件，你可以选择只接受某些类别的提醒：</p>
   </caption>
   <tr>
    <td>
     <div><b>我与好友间的交流</b></div>
     <div><input type="checkbox" name="sendmail_friend_pm" value="1"${pitchOn.friend_pm}${checked}/>给我发送短消息</div>
     <div><input type="checkbox" name="sendmail_comment_friend" value="1"${pitchOn.comment_friend}${checked}/>给我留言</div>
     <div><input type="checkbox" name="sendmail_comment_friend_reply"value="1"${pitchOn.comment_friend_reply}${checked}/>回复我的留言</div>
     <div><input type="checkbox" name="sendmail_poke" value="1"${pitchOn.poke}${checked}/>给我打招呼</div>
     <div><input type="checkbox" name="sendmail_friend_add" value="1"${pitchOn.friend_add}${checked}/>加为好友邀请</div>
     <br />
     <div><b>日志</b></div>
     <div><input type="checkbox" name="sendmail_blog_comment" value="1"${pitchOn.blog_comment}${checked}/>我的日志新评论</div>
     <div><input type="checkbox" name="sendmail_blog_comment_reply" value="1"${pitchOn.blog_comment_reply}${checked}/>我的日志评论有新回复</div>
     <br />
     <div><b>相册</b></div>
     <div><input type="checkbox" name="sendmail_photo_comment" value="1"${pitchOn.photo_comment}${checked}/>我的照片新评论</div>
     <div><input type="checkbox" name="sendmail_photo_comment_reply" value="1"${pitchOn.photo_comment_reply}${checked}/>我的照片评论有新回复</div>
     <br />
     <div><b>分享</b></div>
     <div><input type="checkbox" name="sendmail_share_comment" value="1"${pitchOn.share_comment}${checked}/>我的分享新评论</div>
     <div><input type="checkbox" name="sendmail_share_comment_reply" value="1"${pitchOn.share_comment_reply}${checked}/>我的分享评论有新回复</div>
     <br />
     <div><b>话题</b></div>
     <div><input type="checkbox" name="sendmail_mtag_reply" value="1"${pitchOn.mtag_reply}${checked}/>我话题新回复</div>
     <br />
     <div><b>活动</b></div>
     <div><input type="checkbox" name="sendmail_event" value="1"${pitchOn.event}${checked}/>我发起的活动有新的待审核成员</div>
     <br />
     <div><b>投票</b></div>
     <div><input type="checkbox" name="sendmail_poll_comment" value="1"${pitchOn.poll_comment}${checked}/>我的投票新评论</div>
     <div><input type="checkbox" name="sendmail_poll_comment_reply" value="1"${pitchOn.poll_comment_reply}${checked}/>我的投票评论有新回复</div>
     <br />
     <div>邮件发送频率
      <select name="sendmail_frequency">
       <option value="0">实时发送</option>
       <option value="86400"${pitchOn.frequency_86400}>每天发送一次</option>
       <option value="604800"${pitchOn.frequency_604800}${selected}>每周发送一次</option>
      </select>
     </div>
     <br />
    </td>
   </tr>
   <tr><td>
    <input type="submit" name="setsendemailsubmit" value="保存设置" class="submit" />
    <div class="gray">提醒邮件会发送到你当前的邮箱 ${space.email} (<a href="cp.jsp?ac=profile&op=contact">修改邮箱</a>)</div>
   </td></tr>
  </table>
 </c:otherwise></c:choose>
 <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />