<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <div class="tabs_header">
   <ul class="tabs">
    <li${active_1}><a href="admincp.jsp?ac=report&status=1"><span>待处理举报</span></a></li>
    <li${active_0}><a href="admincp.jsp?ac=report&status=0"><span>已禁止举报</span></a></li>
   </ul>
  </div>
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>举报类型</th>
      <td>
       <select name="idtype">
        <option value="">不限</option>
        <c:forEach items="${idTypes}" var="idType">
         <option value="${idType.key}"${param.idtype==idType.key ? " selected" : ""}>${idType.value}</option>
        </c:forEach>
       </select>
      </td>
      <th>举报状态</th>
      <td>
       <select name="status">
        <option value="2">不限</option>
        <option value="0"<c:if test="${param.status=='0'}"> selected</c:if>>已忽略</option>
        <option value="1"<c:if test="${param.status=='1'}"> selected</c:if>>待处理</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>举报次数</th>
      <td colspan="3">
       <input type="text" name="num1" value="${param.num1}" size="10"> ~
       <input type="text" name="num2" value="${param.num2}" size="10">
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>举报时间</option>
        <option value="num"${orderby_num}>举报数</option>
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
       <input type="hidden" name="ac" value="report">
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=report">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <c:choose><c:when test="${perpage>100}">
      <p>总共有满足条件的数据 <strong>${count}</strong> 个</p>
      <c:forEach items="${list}" var="report">
       <input type="hidden" name="ids" value="${report.rid}">
      </c:forEach>
     </c:when><c:otherwise>
      <table cellspacing="0" cellpadding="0" class="formtable" border="0">
       <tr>
        <td width="25">&nbsp;</td>
        <th>内容</th>
        <th width="50"><a href="admincp.jsp?ac=report&orderby=num&ordersc=${scstr}&status=${param.status}">次数</a></th>
        <th width="120"><a href="admincp.jsp?ac=report&orderby=dateline&ordersc=${scstr}&status=${param.status}">时间</a></th>
        <th width="140">操作</th>
       </tr>
       <c:forEach items="${list}" var="reports">
        <c:forEach items="${reports.value}" var="report">
         <tr${report.value.new>0 ? " bgcolor='#F2F9FD'" : ""}>
          <td><input type="checkbox" name="ids" value="${report.value.rid}"></td>
          <td>
           <c:choose>
            <c:when test="${empty report.value.info}">信息已被删除</c:when>
            <c:when test="${reports.key=='blogid'}"><a href="space.jsp?uid=${report.value.info.uid}&do=blog&id=${report.value.info.blogid}" target="_blank">${report.value.info.subject}</a></c:when>
            <c:when test="${reports.key=='picid'}"><a href="space.jsp?uid=${report.value.info.uid }&do=album&picid=${report.value.info.picid}" target="_blank"><img src="${report.value.info.pic }" width="90" alt="${report.value.info.filename }"></a><br>${report.value.info.title}</c:when>
            <c:when test="${reports.key=='albumid'}"><a href="space.jsp?uid=${report.value.info.uid}&do=album&id=${report.value.info.albumid}" target="_blank"><img src="${report.value.info.pic}" alt="${report.value.info.albumname}" width="100" height="90"></a><br>${report.value.info.albumname}</c:when>
            <c:when test="${reports.key=='tid'}"><a href="space.jsp?do=thread&id=${report.value.info.tid}" target="_blank">${report.value.info.subject}</a></c:when>
            <c:when test="${reports.key=='tagid'}"><a href="space.jsp?do=mtag&tagid=${report.value.info.tagid}" target="_blank">${report.value.info.tagname}</a></c:when>
            <c:when test="${reports.key=='sid'}"><p><a href="admincp.jsp?ac=share&uid=${report.value.info.uid}" target="_blank">${report.value.info.username}</a><a href="space.jsp?uid=${report.value.info.uid }&do=share&id=${report.value.info.sid}" target="_blank">${report.value.info.title_template}</a>&nbsp;${report.value.info.dateline}</p>${report.value.info.body_template}</c:when>
            <c:when test="${reports.key=='uid'}"><a href="space.jsp?uid=${report.value.info.uid}" target="_blank">${report.value.info.avatar}</a><br>用户名: <a href="space.jsp?uid=${report.value.info.uid}">${report.value.info.username}</a></c:when>
            <c:when test="${reports.key=='pid'}"><a href="space.jsp?uid=${report.value.info.uid}&do=poll&pid=${report.value.info.pid}" target="_blank">${report.value.info.subject}</a></c:when>
            <c:when test="${reports.key=='eventid'}"><a href="space.jsp?uid=${report.value.info.uid}&do=event&id=${report.value.info.eventid}" target="_blank">${report.value.info.title}</a></c:when>
            <c:when test="${reports.key=='comment'}"><a href="${report.value.info.url}" target="_blank">${report.value.info.message}(查看详情)</a></c:when>
            <c:when test="${reports.key=='post'}"><a href="space.jsp?do=thread&id=${report.value.info.tid}&pid=${report.value.info.pid}" target="_blank">${report.value.info.message}(查看详情)</a></c:when>
           </c:choose>
           <br><strong>举报理由:</strong>
           <br><ul>${report.value.reason}</ul>
          </td>
          <td>${report.value.num}</td>
          <td>类型: ${idTypes[reports.key]}<br>查阅状态: ${report.value.new>0 ? "未读" : "已读"}<br>${report.value.dateline}</td>
          <td>
           <a href="admincp.jsp?ac=report&rid=${report.value.rid}&op=ignore">禁止举报</a><br>
           <a href="admincp.jsp?ac=report&rid=${report.value.rid}&op=delete" onclick="return confirm('本操作不可恢复，确认继续？');">删除举报</a><br>
           <a href="admincp.jsp?ac=report&rid=${report.value.rid}&op=delete&subop=delinfo" onclick="return confirm('本操作不可恢复，确认继续？');">删除举报及${idTypes[reports.key]}信息</a>
          <c:if test="${reports.key!='tagid'}">
           <br>
           <a href="admincp.jsp?ac=space&op=manage&uid=${report.value.info.uid}">编辑用户</a>
           </c:if>
          </td>
         </tr>
        </c:forEach>
       </c:forEach>
      </table>
     </c:otherwise></c:choose>
    </div>
    <div class="footactions">
     <c:if test="${perpage<=100}"><input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选</c:if>
     <input type="hidden" name="mpurl" value="${mpurl}">
     操作类型：
     <input type="radio" name="optype" value="1" checked>禁止举报
     <input type="radio" name="optype" value="2">删除举报
     <input type="radio" name="optype" value="3">删除举报及信息或者空间
     <input type="submit" name="listsubmit" value="批量操作" class="submit" onclick="return confirm('本操作不可恢复，确认继续？');">
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