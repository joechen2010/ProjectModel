<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <div class="tabs_header">
   <ul class="tabs">
    <li${actives_1}><a href="admincp.jsp?ac=credit&rewardtype=1"><span>奖励规则</span></a></li>
    <li${actives_0}><a href="admincp.jsp?ac=credit&rewardtype=0"><span>惩罚规则</span></a></li>
   </ul>
  </div>
  <c:choose>
   <c:when test="${param.op == 'edit'}">
    <div class="bdrcontent">
     <form method="post" action="admincp.jsp?ac=credit">
      <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
      <div class="title">
       <h3>规则设置</h3>
      </div>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th style="width: 10em;">规则名称</th>
        <td>${rule.rulename}</td>
       </tr>
       <tr>
        <td>奖励方式</td>
        <td>${rewardTypes[rule.rewardtype]}</td>
       </tr>
       <tbody id="otherrule" ${rule.rewardtype == 0 ? "style='display: none'" : ""}>
        <tr>
         <td style="width: 10em;">奖励周期</td>
         <td>
          <c:forEach items="${cycleTypes}" var="cycleType">
          <input type="radio" name="cycletype" value="${cycleType.key}" onclick="showoption(${cycleType.key});" ${cycleType.key==rule.cycletype ? "checked" : ""}>${cycleType.value} 
          </c:forEach>
         </td>
        </tr>
        <tr id="cycletimetr" ${rule.cycletype== 0 || rule.cycletype == 1 || rule.cycletype== 4 ? "style='display: none'" : ""}>
         <td>间隔时间</td>
         <td><input type="text" name="cycletime" value="${rule.cycletime}"></td>
        </tr>
        <tr id="rewardnumtr" ${rule.cycletype == 0 ? "style='display: none'" : ""}>
         <td>奖励次数</td>
         <td><input type="text" name="rewardnum" value="${rule.rewardnum}">0为不限次数</td>
        </tr>
       </tbody>
       <tr>
        <td>${rule.rewardtype == 0 ? "扣除" : "奖励"}积分值</td>
        <td><input type="text" name="credit" value="${rule.credit}"></td>
       </tr>
       <tr>
        <td>${rule.rewardtype == 0 ? "扣除" : "奖励"}经验值</td>
        <td><input type="text" name="experience" value="${rule.experience}"></td>
       </tr>
       <c:if test="${rule.norepeat > 0}">
        <tr>
         <td>去重奖励</td>
         <td>
          <c:choose>
           <c:when test="${rule.norepeat == 1}">该条规则针对信息去重,防止所有奖励给一条信息的评论这类的</c:when>
           <c:when test="${rule.norepeat == 2}">该条规则针对人去重，例如对同一个人在一个周期内只有一次奖励机会</c:when>
           <c:otherwise>该条规则针对应用去重，例如同一个周期内第一次使用应用给奖励机会</c:otherwise>
          </c:choose>
         </td>
        </tr>
       </c:if>
      </table><br />
      <input type="submit" name="creditsubmit" value="提交" class="submit">
      <input type="hidden" name="rid" value="${rule.rid}" />
      <script type="text/javascript">
       function showoption(id) {
        switch(id) {
         case 0:
          $('cycletimetr').style.display = "none";
          $('rewardnumtr').style.display = "none";
          break;
         case 1:
         case 4:
          $('cycletimetr').style.display = "none";
          $('rewardnumtr').style.display = "";
          break;
         case 2:
         case 3:
          $('cycletimetr').style.display = "";
          $('rewardnumtr').style.display = "";
          break;
        }
       }
      </script>
     </form>
    </div>
   </c:when>
   <c:otherwise>
    <div class="bdrcontent">
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr>
       <th>动作名称</th>
       <c:if test="${rewardtype != 0}">
        <th width="80">奖励周期</th>
        <th width="80">奖励次数</th>
        <th width="80">奖励方式</th>
       </c:if>
       <th width="80">${rewardtype == 0 ? "扣除" : "获得"}积分</th>
       <th width="80">${rewardtype == 0 ? "扣除" : "获得"}经验值</th>
       <th width="50">操作</th>
      </tr>
      <c:choose>
       <c:when test="${not empty list}">
        <c:forEach items="${list}" var="value">
         <tr>
          <td>${value.rulename}</td>
          <c:if test="${rewardtype != 0}">
           <td>${cycleTypes[value.cycletype]}</td>
           <td>${value.rewardnum == 0 ? "不限次数" : value.rewardnum}</td>
           <td>${rewardTypes[value.rewardtype]}</td>
          </c:if>
          <td>${value.credit}</td>
          <td>${value.experience}</td>
          <td><a href="admincp.jsp?ac=credit&op=edit&rid=${value.rid}">编辑</a></td>
         </tr>
        </c:forEach>
       </c:when>
       <c:otherwise><tr><td colspan="6">暂无相关积分规则</td></tr></c:otherwise>
      </c:choose>
     </table>
    </div>
   </c:otherwise>
  </c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />