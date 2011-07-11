<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>标签名*</th>
      <td><input type="text" name="tagname" value="${param.tagname}"></td>
      <th>是否锁定</th>
      <td>
       <select name="close">
        <option value="">不限制</option>
        <option value="1"${param.close==1 ? " selected" : ""}>锁定</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>创建时间</th>
      <td colspan="3">
       <input type="text" name="dateline1" value="${param.dateline1}" size="10"> ~
       <input type="text" name="dateline2" value="${param.dateline2}" size="10"> (YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>日志数</th>
      <td colspan="3">
       <input type="text" name="blognum1" value="${param.blognum1}" size="10"> ~
       <input type="text" name="blognum2" value="${param.blognum2}" size="10">
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>创建时间</option>
        <option value="blognum"${orderby_blognum}>日志数</option>
       </select>
       <select name="ordersc">
        <option value="desc"${ordersc_desc}>递减</option>
        <option value="asc"${ordersc_asc}>递增</option>
       </select>
       <select name="perpage">
        <option value="20"${perpage_20}>每页显示20个</option>
        <option value="50"${perpage_50}>每页显示50个</option>
        <option value="100"${perpage_100}>每页显示100个</option>
       </select>
       <input type="hidden" name="ac" value="tag">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=tag">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr><th>标签名</th><th width="80">日志数</th><th width="100">时间</th></tr>
      <c:forEach items="${list}" var="value">
       <tr>
        <td>
         <input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.tagid}">
         <a href="space.jsp?uid=${space.uid}&do=tag&id=${value.tagid}" target="_blank">${value.tagname}</a>
         <c:if test="${value.close>0}">(<a href="admincp.jsp?ac=tag&close=${value.close}">锁定</a>)</c:if>
        </td>
        <td>${value.blognum}</td>
        <td>${value.dateline}</td>
       </tr>
      </c:forEach>
     </table>
    </div>
    <div class="footactions">
     <input type="hidden" name="mpurl" value="${mpurl}">
     <c:if test="${allowbatch}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
     <input type="radio" name="optype" value="delete"> 删除
     <input type="radio" name="optype" value="merge"> 合并到新标签:<input type="text" name="newtagname" value="" size="5">
     <input type="radio" name="optype" value="close"> 锁定
     <input type="radio" name="optype" value="open"> 开放
     <input type="submit" name="opsubmit" value="执行操作" onclick="return confirm('本操作不可恢复，确认继续？');" class="submit">
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