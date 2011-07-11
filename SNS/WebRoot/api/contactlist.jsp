<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
 <head>
  <base href="${jchConfig.siteUrl}" />
  <title>JspRun Contact</title>
  <meta http-equiv="content-type" content="text/html; charset=${jchConfig.charset}">
  <script language="javascript" type="text/javascript" src="image/contact/contact.js"></script>
  <script type="text/javascript">
   var textareaID = '${ta}';
  </script>
  <link href="image/contact/contact.css" rel="stylesheet" type="text/css" />
 </head>
 <body>
  <c:choose>
   <c:when test="${list!=null}">
    <div class="pagePart baseContainer" id="header">
     <div class="inner"><h1 id="contact_data_folder">联系人列表</h1></div>
    </div>
    <div class="pagePart baseContainer" id="body">
     <div class="inner">
      <div class="baseContainer" id="list">
       <div id="table_container">
        <div class="headOfTbl baseContainer">
         <div class="beLeft col1"><img src="image/contact/sign_hide.jpg" onclick="MYCONTACT.util.doSwitch(this)" /></div>
         <div class="beLeft col2"><img src="image/contact/icon_folder.jpg" /></div>
         <div class="beLeft col3"><div id="contact_data_count">(${fn:length(list)}个项目)</div></div>
         <div class="beLeft col4"></div>
        </div>
        <div class="bodyOfTbl baseContainer">
         <table>
          <tbody style="" id="contact_data_list">
           <c:forEach var="item" items="${list}" varStatus="row"><tr id="my_list_item_${row.index}" class="">
            <td class="col1">&nbsp;</td>
            <td class="col2"><c:if test="${!item.isexist}"><input name="my_list_chb" value="${row.index}" type="checkbox"></c:if></td>
            <td class="col3"><span id="my_list_name_${row.index}">${item.name}<c:if test="${item.isexist}"><span class="added">(已添加)</span></c:if></span></td>
            <td class="col4"><span id="my_list_addr_${row.index}">${item.addr}</span></td>
           </tr></c:forEach>
          </tbody>
         </table>
        </div>
       </div>
       <div class="baseContainer" id="append_func">
        <div class="beLeft func">
         &middot;<a href="javascript:void(0);" onclick="MYCONTACT.onMYCONTACTFindAll(true)">全选</a>
         &middot;<a href="javascript:void(0);" onclick="MYCONTACT.onMYCONTACTFindAll(false)">取消全选</a>
        </div>
        <div class="beRight seek">
         <label for="seekIpt">查找联系人:</label>
         <input type="input" id="seek_ipt" onkeyup="onSeekMYCONTACTData(this.value)" />
        </div>
       </div>
       <div class="baseContainer" id="btns">
        <div class="beLeft"><a href="contact.jsp?ab=${abid}&ta=${ta}" tabindex="100"><img alt="返回" src="image/contact/button_back.jpg" /></a></div>
        <div class="beRight"><input id="next_btn" type="image" src="image/contact/button_next.jpg" alt="下一步" value="下一步" onclick="MYCONTACT.onSubmit4()" /></div>
       </div>
      </div>
     </div>
    </div>
    <script language="javascript" type="text/javascript">
     function onSeekMYCONTACTData(vlu){
      for (var ii=0; ii<${fn:length(list)}; ii++)
      {
          var dom = MYCONTACT$('my_list_item_'+ii);
          var name = MYCONTACT$('my_list_name_'+ii).innerHTML;
          var addr = MYCONTACT$('my_list_addr_'+ii).innerHTML;
          if (name.indexOf(vlu)>-1 || addr.indexOf(vlu)>-1 ){
            dom.style.display = '';
          }else {
            dom.style.display = 'none';
          }
        }
     }
    </script>
   </c:when>
   <c:when test="${!jch:jchEmpty(abid)}">
    <div class="pagePart baseContainer" id="header">
     <div class="inner"><h1>帐户登录></h1></div>
    </div>
    <div class="pagePart baseContainer" id="body">
     <div class="inner">
      <div class="baseContainer chooser" id="enter_email">
       <div style="display: none; text-align: center;" id="importing"><img style="margin: 0 auto;" alt="请稍等..." src="image/contact/loading.gif" /></div>
       <form autocomplete="false" onsubmit="return MYCONTACT.onSubmit2(this)" action="contact.jsp" method="post">
        <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
        <input type="hidden" name="loginsubmit" value="true" />
        <input type="hidden" name="emails" value="" />
        <input type="hidden" name="ab" value="${abid}" />
        <input type="hidden" value="${ta}" name="ta" />
        <div class="logo"><img src="image/contact/logo_${fn:toLowerCase(abid)}.gif" alt="${abid}"></div>
        <ul class="baseContainer">
         <li><dl>
          <dt><label for="usernameTA">${abid} 邮箱</label></dt>
          <dd><input type="text" class="ipt" id="usernameTA" name="username" value="" /></dd>
         </dl></li>
         <li><dl>
          <dt><label for="passwordTA">${abid} 密码</label></dt>
          <dd><input type="password" class="ipt" id="passwordTA" name="password" /></dd>
         </dl></li>
        </ul>
        <c:if test="${!empty errorMessage}"><div id="errorField"><div class="error_msg">${errorMessage}</div></div></c:if>
        <div class="baseContainer" id="btns">
         <div class="beLeft back"><a href="contact.jsp?ta=${ta}"><img alt="返回" src="image/contact/button_back.jpg" /></a></div>
         <div class="beRight next"><input type="image" src="image/contact/button_sign_in.jpg" alt="登录" value="登录" /></div>
        </div>
       </form>
      </div>
      <div class="baseContainer" id="note">我们不会保存您的帐户和密码, 请放心使用.<br />这些信息将仅被暂时性的用来访问您的帐户, 以取得联系人列表</div>
     </div>
    </div>
   </c:when>
   <c:otherwise>
    <div class="pagePart baseContainer" id="header">
     <div class="inner"><h1>您想从哪里寻找好友?</h1></div>
    </div>
    <div class="pagePart baseContainer" id="body">
     <div class="inner">
      <div class="baseContainer chooser" id="services">
       <form autocomplete="false" action="contact.jsp">
        <input type="hidden" name="op" value="login" />
        <input type="hidden" value="${ta}" name="ta" />
        <ul class="baseContainer"><c:forEach items="${emails}" var="email" varStatus="item">
         <li><dl>
          <dt><input type="radio" name="ab" value="${email}" id="ab_${email}"${item.index==0 ? " checked" : ""}/></dt>
          <dd><label for="ab_${email}"><span style="cursor: pointer"><img src="image/contact/logo_${fn:toLowerCase(email)}.gif" alt="${email}" onclick="MYCONTACT$('ab_${email}').checked=true;"></span></label></dd>
         </dl></li>
        </c:forEach></ul>
        <input id="next_btn" type="image" src="image/contact/button_next.jpg" alt="下一步" />
       </form>
      </div>
      <div class="baseContainer" id="note">本向导可以帮助您从各种邮箱和服务中, 快速找到联系人列表.<br />请在以上项目中选择一项, 并按[下一步]按钮继续</div>
     </div>
    </div>
   </c:otherwise>
  </c:choose>
  <div class="pagePart baseContainer" id="footer">
   <div class="inner"></div>
  </div>
 </body>
</html>