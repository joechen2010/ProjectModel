<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <div class="tabs_header">
   <ul class="tabs">
    <li${actives_enabled}><a href="admincp.jsp?ac=magic&view=enabled"><span>已启用道具</span></a></li>
    <li${actives_disabled}><a href="admincp.jsp?ac=magic&view=disabled"><span>已禁用道具</span></a></li>
   </ul>
  </div>
  <c:choose>
   <c:when test="${param.op == 'edit'}">
    <form method="post" action="admincp.jsp?ac=magic&op=${param.op}&mid=${param.mid}&view=${view}">
     <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
     <div class="bdrcontent">
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th style="width: 15em;">名称</th>
        <td>${magic.name}</td>
       </tr>
       <tr>
        <th style="width: 15em;">道具说明</th>
        <td><textarea name="description" cols="80" rows="2">${magic.description}</textarea></td>
       </tr>
       <tr>
        <th style="width: 15em;">道具单价</th>
        <td><input type="text" name="charge" value="${magic.charge}" /> 购买时单个需要花费的积分值，需小于65535</td></tr>
       <tr>
        <th style="width: 15em;">经验成长</th>
        <td><input type="text" name="experience" value="${magic.experience}" /> 购买单个可以增长的经验值，需小于65535</td>
       </tr>
       <tr>
        <th style="width: 15em;">补给周期</th>
        <td>
         <select name="provideperoid">
          <option value="0"${magic.provideperoid==0 ? " selected" : ""}>总是可以</option>
          <option value="3600"${magic.provideperoid==3600 ? " selected" : ""}>间隔1小时</option>
          <option value="86400"${magic.provideperoid==86400 ? " selected" : ""}>间隔24小时</option>
          <option value="604800"${magic.provideperoid==604800 ? " selected" : ""}>间隔7天</option>
         </select>
         若道具商店中此道具已售光，在补给周期内自动补给一次
        </td>
       </tr>
       <tr>
        <th style="width: 15em;">补给数目</th>
        <td><input type="text" name="providecount" value="${magic.providecount}" maxlength="6" /> 若道具商店中此道具已售光，自动补给一次的数目，需小于65535</td>
       </tr>
       <tr>
        <th style="width: 15em;">使用周期</th>
        <td>
         <select name="useperoid">
          <option value="0"${magic.useperoid==0 ? " selected" : ""}>总是可以</option>
          <option value="3600"${magic.useperoid==3600 ? " selected" : ""}>间隔1小时</option>
          <option value="86400"${magic.useperoid==86400 ? " selected" : ""}>间隔24小时</option>
          <option value="604800"${magic.useperoid==604800 ? " selected" : ""}>间隔7天</option>
         </select>
         设定用户使用此道具的使用周期
        </td>
       </tr>
       <tr>
        <th style="width: 15em;">使用数目</th>
        <td><input type="text" name="usecount" value="${magic.usecount}" maxlength="6" /> 设定用户在使用周期内最多能使用此道具的个数，需小于65535</td>
       </tr>
       <tr>
        <th style="width: 15em;">禁购用户组</th>
        <td>选中的用户组不能在道具商店购买此道具（但可以接受赠与）<br />
         <c:forEach items="${userGroups}" var="userGroup">
          <c:forEach items="${userGroup.value}" var="group">
           <input id="ckgid_${group.key}" type="checkbox" name="forbiddengid" value="${group.key}" ${jch:inArray(magic.forbiddengid,group.key) ? "checked" : ""} /> <label for="ckgid_${group.key}">${group.value}</label>
          </c:forEach><br />
         </c:forEach>
        </td>
       </tr>
       <tr>
        <th style="width: 15em;">库存数目</th>
        <td><input type="text" name="storage" value="${magic.storage}" size="5" maxlength="5" /></td>
       </tr>
       <tr>
        <th style="width: 15em;">显示顺序</th>
        <td><input type="text" name="displayorder" value="${magic.displayorder}" size="5" maxlength="5" /></td>
       </tr>
       <tr>
        <th style="width: 15em;">是否禁用</th>
        <td><input type="checkbox" id="magicclose" name="close" value="1" ${magic.close !=0 ? "checked" : ""} /> <label for="magicclose">禁用后页面上将不显示此道具</label></td>
       </tr>
       <c:if test="${!empty custom}">
        <tr>
         <th style="width: 15em;">自定义效果</th>
         <td>
          请修改道具描述和道具效果一致<br />
          <table>
           <tr>
            <th width="100">${custom.title}</th>
            <td>
             <input type="hidden" name="customName" value="${custom.name}" />
             <select name="custom_${custom.name}">
              <option value="">默认</option>
              <c:forEach items="${custom.option}" var="op">
              <option value="${op.key}"${magic.custom[custom.name] ==op.key ? "selected" : ""}> ${op.value}</option>
              </c:forEach>
             </select>
             默认为 ${custom.desc}
            </td>
           </tr>
          </table>
         </td>
        </tr>
       </c:if>
      </table>
     </div>
     <div class="footactions">
      <input type="hidden" name="mid" value="${magic.mid}" />
      <input type="submit" name="editsubmit" value="提交" class="submit">
     </div>
    </form>
   </c:when>
   <c:otherwise>
    <form method="post" action="admincp.jsp?ac=magic&view=${view}">
     <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
     <div class="bdrcontent">
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th width="100">图标</th>
        <th>道具</th>
        <c:if test="${view != 'disabled'}">
         <th width="80">道具单价</th>
         <th width="80">显示顺序</th>
        </c:if>
        <th width="80">操作</th>
       </tr>
       <c:forEach items="${magics}" var="magic">
        <tr>
         <th><img src="image/magic/${magic.mid}.gif" title="${magic.name}" /></th>
         <td><b>${magic.name}</b><p>${magic.description}</p></td>
         <c:if test="${view != 'disabled'}">
          <td><input type="hidden" name="mid" value="${magic.mid}"><input type="text" name="charge_${magic.mid}" value="${magic.charge}" size="5" maxlength="5" /></td>
          <td><input type="text" name="displayorder_${magic.mid}" value="${magic.displayorder}" size="5" maxlength="5" /></td>
         </c:if>
         <td><a href="admincp.jsp?ac=magic&op=edit&mid=${magic.mid}&view=${view}">编辑</a></td>
        </tr>
       </c:forEach>
      </table>
     </div>
     <c:if test="${view != 'disabled'}"><div class="footactions"><input type="submit" name="ordersubmit" value="更新数据" class="submit"></div></c:if>
    </form>
   </c:otherwise>
  </c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />