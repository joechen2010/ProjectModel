<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp?ac=${param.ac}">
   <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}">
   <div class="bdrcontent">
    <table cellspacing="0" cellpadding="0" class="formtable">
     <tr>
      <td colspan="2">
      <c:choose>
       <c:when test="${param.ac == 'defaultuser'}"><textarea name="defaultfusername" style="width: 98%;" rows="12">${configs.defaultfusername}</textarea></c:when>
       <c:otherwise><textarea name="spacebarusername" style="width: 98%;" rows="12">${configs.spacebarusername}</textarea></c:otherwise>
      </c:choose>
      </td>
     </tr>
     <tr>
      <td colspan="2">添加格式：<br>请输入用户名，每个用户名一行。<br>例如：<br>admin<br>webmaster<br>${param.ac == "defaultuser" ? "这些用户会自动将新注册用户添加为好友，并向其打个招呼。注意，指定的这几位用户浏览自己的首页时，可能会因其好友数众多而增加服务器负载。" : "这些用户将随机显示在随便看看页面的“站长推荐”栏目中。"}</td>
     </tr>
    </table>
    <c:if test="${param.ac == 'defaultuser'}">
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr>
       <th style="width: 10em;">默认打招呼内容</th>
       <td><input type="text" class="t_input" name="defaultpoke" value="${configs.defaultpoke}" size="50"> (不要超过25个汉字)<br>设置自动好友向新人打招呼的内容。</td>
      </tr>
     </table>
    </c:if>
   </div>
   <div class="footactions"><input type="submit" name="thevaluesubmit" value="提交" class="submit"></div>
  </form>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />