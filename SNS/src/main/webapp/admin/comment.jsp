<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>评论类型</th>
      <td>
       <select name="idtype">
        <option value="">全部</option>
        <option value="uid"${idtype_uid}>留言</option>
        <option value="blogid"${idtype_blogid}>日志</option>
        <option value="picid"${idtype_picid }>图片</option>
        <option value="eventid"${idtype_eventid}>活动</option>
        <option value="sid"${idtype_sid}>分享</option>
       </select>
      <th>评论ID</th><td><input type="text" name="id" value="${param.id}"></td>
     </tr>
     <tr>
      <th>评论UID</th>
      <td><input type="text" name="authorid" value="${param.authorid}"></td>
      <th>评论者</th>
      <td><input type="text" name="author" value="${param.author}"></td>
     </tr>
     <c:if test="${allowmanage}">
      <tr>
       <th>被评论UID</th><td colspan="3"><input type="text" name="uid" value="${param.uid}"></td>
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
      <th>发布IP</th><td><input type="text" name="ip" value="${param.ip}"></td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>评论时间</option>
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
       <input type="hidden" name="ac" value="comment">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=comment">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <c:choose><c:when test="${perpage>100}">
      <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
      <c:forEach items="${list}" var="value">
       <input type="hidden" name="ids" value="${value.cid}">
      </c:forEach>
     </c:when><c:otherwise>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <c:forEach items="${list}" var="value">
        <tr>
         <td width="25"><input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.cid}"></td>
         <td>
          ${value.message} <c:if test="${wheresql=='1'}"><a href="admincp.jsp?ac=comment&cid=${value.cid }">...</a></c:if>
          <p>
           作者: 
           <c:choose>
            <c:when test="${empty value.author}"><a href="space.jsp?uid=${value.authorid}" target="_blank">匿名</a></c:when>
            <c:otherwise><a href="admincp.jsp?ac=comment&author=${value.author }">${sNames[value.authorid]}</a></c:otherwise>
           </c:choose>
           被评论对象: <a href="admincp.jsp?ac=comment&id=${value.id }&idtype=${value.idtype }">${value.idtype}-${value.id}</a>
           <c:if test="${value.idtype=='uid'}">(<a href="space.jsp?uid=${value.id }&do=wall" target="_blank">访问</a>)</c:if>
           IP: <a href="admincp.jsp?ac=comment&ip=${value.ip}">${value.ip}</a> ${value.dateline}
          </p>
         </td>
        </tr>
       </c:forEach>
      </table>
     </c:otherwise></c:choose>
    </div>
    <div class="footactions">
     <c:if test="${allowbatch && perpage<=100}">
      <input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
     <input type="hidden" name="mpurl" value="${mpurl }">
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