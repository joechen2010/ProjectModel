<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>群组ID</th><td><input type="text" name="tagid" value="${param.tagid}"></td>
      <th>标题*</th><td><input type="text" name="subject" value="${param.subject}"></td>
     </tr>
     <c:if test="${allowmanage}">
      <tr>
       <th>作者UID</th><td><input type="text" name="uid" value="${param.uid}"></td>
       <th>作者名</th><td><input type="text" name="username" value="${param.username}"></td>
      </tr>
     </c:if>
     <tr>
      <th>指定话题ID</th>
      <td colspan="3"><input type="text" name="tid" value="${param.tid}"/></td>
     </tr>
     <tr>
      <th>发布时间</th>
      <td colspan="3">
       <input type="text" name="dateline1" value="${param.dateline1}" size="10"> ~
       <input type="text" name="dateline2" value="${param.dateline2}" size="10"> (YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>查看数</th>
      <td colspan="3">
       <input type="text" name="viewnum1" value="${param.viewnum1}" size="10"> ~
       <input type="text" name="viewnum2" value="${param.viewnum2}" size="10">
      </td>
     </tr>
     <tr>
      <th>回复数</th>
      <td colspan="3">
       <input type="text" name="replynum1" value="${param.replynum1}" size="10"> ~
       <input type="text" name="replynum2" value="${param.replynum2}" size="10">
      </td>
     </tr>
     <tr>
      <th>热度</th>
      <td colspan="3">
       <input type="text" name="hot1" value="${param.hot1}" size="10"> ~
       <input type="text" name="hot2" value="${param.hot2}" size="10">
      </td>
     </tr>
     <tr>
      <th>是否精华</th>
      <td colspan="3">
       <select name="digest">
        <option value="">不限制</option>
        <option value="1"${param.digest=='1' ? " selected" : ""}>精华帖</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>发布时间</option>
        <option value="lastpost"${orderby_lastpost }>回复时间</option>
        <option value="viewnum"${orderby_viewnum}>查看数</option>
        <option value="replynum"${orderby_replynum}>回复数</option>
        <option value="hot"${orderby_hot}>热度</option>
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
       <input type="hidden" name="ac" value="thread">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=thread&tagid=${tagid}">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <c:choose><c:when test="${perpage>100}">
      <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
      <c:forEach items="${list}" var="value">
       <input type="hidden" name="ids" value="${value.tid}">
      </c:forEach>
     </c:when><c:otherwise>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th width="25">&nbsp;</th>
        <th>标题</td>
        <th width="50">查看</th>
        <th width="50">回复</th>
        <c:if test="${allowmanage}"><th width="80">作者</th></c:if>
        <th width="80">时间</th>
       </tr>
       <c:forEach items="${list}" var="value">
        <tr>
         <td><input type="${allowbatch ? 'checkbox' : 'radio'}" name="ids" value="${value.tid}"></td>
         <td>
          [<a href="${mpurl}&tagid=${value.tagid }">${tags[value.tagid]}</a>]
          <a href="space.jsp?do=thread&id=${value.tid}" target="_blank">${value.subject}</a>
          <c:if test="${value.digest>0}">[<a href="${mpurl}&digest=${value.digest}">精</a>]</c:if>
          <c:if test="${value.displayorder>0}">[<a href="${mpurl}&displayorder=${value.displayorder}">顶</a>]</c:if>
          <c:if test="${value.hot>0}"><span style="color: red;">热度(${value.hot})</span></c:if>
         </td>
         <td>${value.viewnum}</td>
         <td><a href="admincp.jsp?ac=post&tid=${value.tid}">${value.replynum}</a></td>
         <c:if test="${allowmanage}"><td><a href="${mpurl}&uid=${value.uid}">${value.username}</a></td></c:if>
         <td>${value.dateline}</td>
        </tr>
       </c:forEach>
      </table>
     </c:otherwise></c:choose>
    </div>
    <div class="footactions">
     <c:choose><c:when test="${perpage>100}">
      <input type="hidden" name="optype" value="delete">
      <input type="submit" name="opsubmit" value="批量删除" onclick="return confirm('本操作不可恢复，确认删除？');" class="submit">
     </c:when><c:otherwise>
      <c:if test="${allowbatch}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
      <input type="radio" name="optype" value="delete" checked> 删除
      <c:if test="${param.tagid>0}">
       <input type="radio" name="optype" value="digest"> 批量精华
       <select name="digestv">
        <option value="1">设为精华</option>
        <option value="0">取消精华</option>
       </select>
       <input type="radio" name="optype" value="top"> 批量置顶
       <select name="topv">
        <option value="1">设为置顶</option>
        <option value="0">取消置顶</option>
       </select>
      </c:if>
      <input type="submit" name="opsubmit" value="执行操作" onclick="return confirm('本操作不可恢复，确认继续？');" class="submit">
     </c:otherwise></c:choose>
     <input type="hidden" name="mpurl" value="${mpurl}">
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