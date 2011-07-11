<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>群组ID</th><td><input type="text" name="tagid" value="${param.tagid}"></td>
      <th>话题ID</th><td><input type="text" name="tid" value="${param.tid}"></td>
     </tr>
     <c:if test="${allowmanage}">
      <tr>
       <th>作者UID</th><td><input type="text" name="uid" value="${param.uid}"></td>
       <th>作者名</th><td><input type="text" name="username" value="${param.username}"></td>
      </tr>
     </c:if>
     <tr>
      <th>发布时间</th>
      <td colspan="3">
       <input type="text" name="dateline1" value="${param.dateline1}" size="10"> ~
       <input type="text" name="dateline2" value="${param.dateline2}" size="10"> (YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>内容*</th><td><input type="text" name="message" value="${param.message}"></td>
      <th>IP</th><td><input type="text" name="ip" value="${param.ip}"></td>
     </tr>
     <tr>
      <th>是否主题帖</th>
      <td colspan="3">
       <select name="isthread">
        <option value="">不限制</option>
        <option value="1"${param.isthread == '1' ? " selected" : ""}>主题帖</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>发布时间</option>
       </select>
       <select name="ordersc">
        <option value="desc"${ordersc_desc}>递减</option>
        <option value="asc"${ordersc_asc}>递增</option>
       </select>
       <select name="perpage">
        <option value="20"${perpage_20}>每页显示20个</option>
        <option value="50"${perpage_50}>每页显示50个</option>
        <option value="100"${perpage_100}>每页显示100个</option>
        <option value="1000"${perpage_1000}>一次处理1000个</option>
       </select>
       <input type="hidden" name="ac" value="post">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=post&tagid=${tagid}">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <c:choose><c:when test="${perpage>100}">
      <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
      <c:forEach items="${list}" var="value">
       <input type="hidden" name="ids" value="${value.pid}">
      </c:forEach>
     </c:when><c:otherwise>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <c:forEach items="${list}" var="value">
        <tr>
         <td width="25"><input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.pid}"></td>
         <td>
          [${value.isthread > 0 ? "主题" : "回帖"}] <a href="space.jsp?uid=${space.uid}&do=thread&id=${value.tid}" target="_blank">${threads[value.tid]}</a>
          <br>${value.message} <c:if test="${wheresql=='1'}"><a href="${mpurl}&pid=${value.pid}">...</a></c:if>
          <p><c:if test="${allowmanage}"><a href="${mpurl}&uid=${value.uid}">${value.username}</a> (<a href="${mpurl}&ip=${value.ip}">${value.ip}</a>)&nbsp;</c:if>${value.dateline}</p>
         </td>
        </tr>
       </c:forEach>
      </table>
     </c:otherwise></c:choose>
    </div>
    <div class="footactions">
     <c:if test="${allowbatch && perpage<=100}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
     <input type="hidden" name="mpurl" value="${mpurl}">
     <input type="submit" name="deletesubmit" value="批量删除" onclick="return confirm('本操作不可恢复，确认删除？');" class="submit">
     <div class="pages">${multi}</div>
    </div>
   </form>
  </c:when><c:otherwise>
   <div class="bdrcontent"><p>指定条件下还没有数据</p></div>
  </c:otherwise></c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />