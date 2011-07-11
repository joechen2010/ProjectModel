<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp?ac=spam">
   <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}">
   <div class="bdrcontent">
    <div class="title">
     <h3>防灌水设置</h3>
     <p>您可以选择一些措施，来增加对灌水机的干扰，避免大量垃圾广告信息的产生，但这也会影响到新用户的使用体验。</p>
    </div>
    <table class="formtable">
     <tr>
      <th style="width: 15em;">自定义登录识别名</th>
      <td><input type="text" name="config[login_action]" value="${configs.login_action}" size="10"> (必须为英文字母)<br>给登录页面设置个性名，可加大灌水机自动登录的难度，如果有必要，可以不定期更改一下。</td>
     </tr>
     <tr>
      <th>自定义注册识别名</th>
      <td><input type="text" name="config[register_action]" value="${configs.register_action}" size="10"> (必须为英文字母)<br>给注册页面设置个性名。注意，不能与登录识别名相同。</td>
     </tr>
     <tr>
      <th>页面开启验证码功能</th>
      <td>
       <input type="checkbox" name="config[seccode_login]" value="1" ${configs.seccode_login == 1 ? "checked" : ""}>登录页面
       <input type="checkbox" name="config[seccode_register]" value="1" ${configs.seccode_register == 1 ? "checked" : ""}>注册页面
       <br>说明：<br>对发布日志等发布操作是否开启验证码功能，您需要在<a href="admincp.jsp?ac=usergroup">用户组管理</a>中，对用户组进行不同的开启设置。
      </td>
     </tr>
     <tr>
      <th>验证码采用个性提问模式</th>
      <td>
       <input type="radio" name="config[questionmode]" value="1" ${configs.questionmode == 1 ? "checked" : ""}>是，使用我自定义的问答（推荐，效果好）
       <input type="radio" name="config[questionmode]" value="0" ${configs.questionmode != 1 ? "checked" : ""}>否，使用系统验证码图片
      </td>
     </tr>
     <tr>
      <th>设置个性问题和答案</th>
      <td id="q_td"><c:forEach items="${questions}" varStatus="index">
        <div style="margin-bottom: 0.5em;">问题：<input type="text" name="data[question]" value="${questions[index.index]}" size="20">&nbsp; 答案：<input type="text" name="data[answer]" value="${answers[index.index]}" size="20"></div></c:forEach>
       <a href="javascript:;" onclick="new_q();">添加新的问答</a> (填写的答案不能为0或者空值)
       <div id="new_div" style="margin-bottom: 0.5em; margin-top: 0.5em;">问题：<input type="text" name="data[question]" value="" size="20">&nbsp; 答案：<input type="text" name="data[answer]" value="" size="20"></div>
      </td>
     </tr>
     <tr>
      <th>强制新用户验证激活邮箱</th>
      <td>
       <input type="radio" name="config[need_email]" value="1" ${configs.need_email == 1 ? "checked" : ""}>是
       <input type="radio" name="config[need_email]" value="0" ${configs.need_email != 1 ? "checked" : ""}>否
       <br>选择是的话，用户必须验证激活自己的邮箱后，才可以进行发布操作。
      </td>
     </tr>
     <tr>
      <th>验证激活邮箱唯一性</th>
      <td>
       <input type="radio" name="config[uniqueemail]" value="1" ${configs.uniqueemail == 1 ? "checked" : ""}>是
       <input type="radio" name="config[uniqueemail]" value="0" ${configs.uniqueemail != 1 ? "checked" : ""}>否
       <br>选择是的话，验证激活的邮箱将唯一性，不允许重复。
      </td>
     </tr>
     <tr>
      <th>新用户见习时间</th>
      <td><input type="text" name="config[newusertime]" value="${configs.newusertime}" size="10"> 小时<br>设置新注册用户必须等待多少小时后才可以发布操作。</td>
     </tr>
     <tr>
      <th>强制新用户上传头像</th>
      <td>
       <input type="radio" name="config[need_avatar]" value="1" ${configs.need_avatar == 1 ? "checked" : ""}>是
       <input type="radio" name="config[need_avatar]" value="0" ${configs.need_avatar != 1 ? "checked" : ""}>否
       <br>选择是的话，用户必须设置自己的头像后才能进行发布操作。
      </td>
     </tr>
     <tr>
      <th>强制新用户好友个数</th>
      <td><input type="text" name="config[need_friendnum]" value="${configs.need_friendnum}" size="10"><br>设置用户必须拥有多少个好友后，才可以进行发布操作。</td>
     </tr>
     <tr>
      <th>发短消息最少注册天数</th>
      <td>
       <input type="text" name="config[pmsendregdays]" value="${configs.pmsendregdays}" size="10">
       <br>注册天数少于次设置的，不允许发送短消息，0为不限制，此举为了限制机器人发广告。
      </td>
     </tr>
     <tr>
      <th>同一用户在 24 小时允许发送短消息的最大数目</th>
      <td><input type="text" name="config[pmlimit1day]" value="${configs.pmlimit1day}" size="10"><br>同一用户在 24 小时内可以发送的短消息的极限，建议在 30 - 100 范围内取值，0 为不限制，此举为了限制通过机器批量发广告。</td>
     </tr>
     <tr>
      <th>发短消息灌水预防</th>
      <td><input type="text" name="config[pmfloodctrl]" value="${configs.pmfloodctrl}" size="10"><br>两次发短消息间隔小于此时间，单位秒，0 为不限制，此举为了限制通过机器批量发广告。</td>
     </tr>
     <tr>
      <th>其他一些防灌水措施设置</th>
      <td>1. 您可以在<a href="admincp.jsp?ac=config">站点设置</a>中，关闭站点注册功能，仅允许好友邀请注册功能。<br>2. 您可以在<a href="admincp.jsp?ac=privacy">隐私设置</a>中，关闭游客开放浏览功能。 </td>
     </tr>
    </table>
   </div>
   <div class="footactions"><input type="submit" name="spamsubmit" value="提交" class="submit"></div>
  </form>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<script>
function new_q() {
 $('q_td').innerHTML += '<div style="margin-bottom:0.5em;">'+$('new_div').innerHTML+'</div>';
}
</script>
<jsp:directive.include file="footer.jsp" />