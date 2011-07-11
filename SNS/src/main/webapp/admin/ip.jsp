<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp?ac=ip">
   <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}">
   <div class="bdrcontent">
    <table cellspacing="0" cellpadding="0" class="formtable">
     <tr><th>允许访问的IP列表</th></tr>
     <tr>
      <td>
       <textarea name="ipaccess" style="width: 98%;" rows="8">${configs.ipaccess}</textarea>
       <br>只有当用户处于本列表中的 IP 地址时才可以访问站点,列表以外的地址访问将视为 IP 被禁止。
       <br>每个 IP 一行，既可输入完整地址，也可只输入 IP 开头。
       <br>例如：<br>"192.168."(不含引号) 可匹配 192.168.0.0～192.168.255.255 范围内的所有地址，留空为所有 IP 均可访问。
       <br>您当前的IP: ${onlineip}
      </td>
     </tr>
     <tr><th>禁止访问的IP列表</th></tr>
     <tr>
      <td>
       <textarea name="ipbanned" style="width: 98%;" rows="8">${configs.ipbanned}</textarea>
       <br>用户处于本列表中的 IP 地址禁止访问站点。每个 IP 一行，既可输入完整地址，也可只输入 IP 开头。
       <br>例如： <br>"192.168."(不含引号) 可匹配 192.168.0.0～192.168.255.255 范围内的所有地址，留空为所有 IP 均可访问。
       <br>您当前的IP: ${onlineip}
      </td>
     </tr>
    </table>
   </div>
   <div class="footactions"><input type="submit" name="thevaluesubmit" value="提交" class="submit"></div>
  </form>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />