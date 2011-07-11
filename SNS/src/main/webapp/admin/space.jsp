<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <div class="tabs_header">
   <ul class="tabs">
    <li${active_all}><a href="admincp.jsp?ac=space"><span>浏览全部用户</span></a></li>
    <c:if test="${managename}">
     <li${active_0}><a href="admincp.jsp?ac=space&namestatus=0&tab=0"><span>实名未认证</span></a></li>
     <li${active_1}><a href="admincp.jsp?ac=space&namestatus=1&tab=1"><span>实名已认证</span></a></li>
    </c:if>
    <li${active_2}><a href="admincp.jsp?ac=space&videostatus=0&tab=2"><span>视频未认证</span></a></li>
    <li${active_3}><a href="admincp.jsp?ac=space&videostatus=1&tab=3"><span>视频已认证</span></a></li>
    <li${active_4}><a href="admincp.jsp?ac=space&videostatus=0&tab=4"><span>视频待认证</span></a></li>
   </ul>
  </div>
  <form method="post" action="admincp.jsp">
   <div class="block style4">
    <table cellspacing="3" cellpadding="3">
     <tr>
      <th>用户UID</th><td><input type="text" name="uid" value="${param.uid}" size="10"></td>
      <th>用户名</th><td><input type="text" name="username" value="${param.username}"></td>
     </tr>
     <tr>
      <th>姓名*</th><td><input type="text" name="name" value="${param.name}"></td>
      <th>头像</th>
      <td>
       <select name="avatar">
        <option value="">不限</option>
        <option value="1"${param.avatar=='1' ? " selected" : ""}>上传头像</option>
        <option value="0"${param.avatar=='0' ? " selected" : ""}>没有头像</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>实名认证</th>
      <td>
       <select name="namestatus">
        <option value="">不限</option>
        <option value="1"${param.namestatus=='1' ? " selected" : ""}>已认证</option>
        <option value="0"${param.namestatus=='0' ? " selected" : ""}>未认证</option>
       </select>
      </td>
      <th>视频认证</th>
      <td>
       <select name="videostatus">
        <option value="">不限</option>
        <option value="1"${param.videostatus=='1' ? " selected" : ""}>已认证</option>
        <option value="0"${param.videostatus=='0' ? " selected" : ""}>未认证</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>用户组</th>
      <td>
       <select name="groupid">
        <option value="">不限制</option>
        <c:forEach items="${userGroups}" var="userGroup">
         <option value="${userGroup.key}"${param.groupid==userGroup.key ? " selected" : ""}>${userGroup.value}</option>
        </c:forEach>
       </select>
      </td>
      <th>用户状态</th>
      <td>
       <select name="flag">
        <option value="">不限</option>
        <option value="1"${param.flag=='1' ? " selected" : ""}>保护用户</option>
        <option value="0"${param.flag=='0' ? " selected" : ""}>普通用户</option>
        <option value="-1"${param.flag=='-1' ? " selected" : ""}>锁定用户</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>用户经验值</th>
      <td>
       <input type="text" name="experience1" value="${param.experience1}" size="10"> ~
       <input type="text" name="experience2" value="${param.experience2}" size="10">
      </td>
     </tr>
     <tr>
      <th>用户积分</th>
      <td>
       <input type="text" name="credit1" value="${param.credit1}" size="10"> ~
       <input type="text" name="credit2" value="${param.credit2}" size="10">
      </td>
     </tr>
     <tr>
      <th>空间创建时间</th>
      <td colspan="3">
       <input type="text" name="dateline1" value="${param.dateline1}" size="10"> ~
       <input type="text" name="dateline2" value="${param.dateline2}" size="10"> (YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>最后更新时间</th>
      <td colspan="3">
       <input type="text" name="updatetime1" value="${param.updatetime1}" size="10"> ~
       <input type="text" name="updatetime2" value="${param.updatetime2}" size="10"> (YYYY-MM-DD)
      </td>
     </tr>

     <tr>
      <th>最后访问时间</th>
      <td colspan="3">
       <input type="text" name="lastlogin1" value="${param.lastlogin1}" size="10"> ~
       <input type="text" name="lastlogin2" value="${param.lastlogin2}" size="10"> (YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>最后发信息时间</th>
      <td colspan="3">
       <input type="text" name="lastpost1" value="${param.lastpost1}" size="10"> ~
       <input type="text" name="lastpost2" value="${param.lastpost2}" size="10"> (YYYY-MM-DD)
      </td>
     </tr>
     <tr>
      <th>结果排序</th>
      <td colspan="3">
       <select name="orderby">
        <option value="">默认排序</option>
        <option value="dateline"${orderby_dateline}>建立时间</option>
        <option value="updatetime"${orderby_updatetime}>更新时间</option>
        <option value="viewnum"${orderby_viewnum}>访问量</option>
        <option value="experience"${orderby_experience}>经验值</option>
        <option value="credit"${orderby_credit}>积分数</option>
        <option value="friendnum"${orderby_friendnum}>好友数</option>
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
       <input type="hidden" name="ac" value="${ac}">
       <c:if test="${not empty param.tab}"><input type="hidden" name="tab" value="${param.tab}"></c:if>
       <input type="submit" name="searchsubmit" value="搜索" class="submit">
      </td>
     </tr>
    </table>
   </div>
  </form>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=space">
    <input type="hidden" name="formhash" value="${FORMHASH}" />
    <div class="bdrcontent">
     <table cellspacing="2" cellpadding="2" class="listtable">
      <tr class="line"><th width="30">选择</th><th width="55">用户</th><th>用户名/姓名</th><th>用户组</th><th>经验/积分/好友</th><th>附件</th><th>注册/更新</th><th>状态</th><th>操作</th></tr>
      <c:forEach items="${list}" var="value" varStatus="key">
       <tr${key.index%2==1 ? " class='line'" : ""}>
        <td><input type="checkbox" name="uids" value="${value.uid}"></td>
        <td><a href="space.jsp?uid=${value.uid}" target="_blank">${value.avatar}</a></td>
        <td>
         <a href="space.jsp?uid=${value.uid}">${value.username}</a>
         <c:if test="${not empty value.name}"><p ${value.namestatus>0 ? "style='color: red;'" : "class='gray'"}>${value.name}</p></c:if>
        </td>
        <td>
         ${value.grouptitle}
         <c:if test="${fusers[value.uid]!=null}">
          <p>期限: ${fusers[value.uid].expiration}</p>
          <p>操作: <a href="space.jsp?uid=${fusers[value.uid].opuid}">${fusers[value.uid].opusername}</a></p>
         </c:if>
        </td>
        <td class="gray">${value.experience} / ${value.credit} / ${value.friendnum}</td>
        <td>${value.attachsize}</td>
        <td>${value.dateline}<br>${value.updatetime}</td>
        <td>${value.flag}</td>
        <td><a href="admincp.jsp?ac=space&op=manage&uid=${value.uid}">管理</a></td>
       </tr>
      </c:forEach>
     </table>
    </div>
    <div class="footactions">
     <input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'uids')">全选 &nbsp;&nbsp;
     <c:if test="${managename}">
      <input type="radio" name="optype" value="1"> 通过实名
      <input type="radio" name="optype" value="2"> 取消实名
      <input type="radio" name="optype" value="3"> 清空实名
     </c:if>
     <c:if test="${managespacenote}">
      <input type="radio" name="optype" value="4"> 发送邮件
      <input type="radio" name="optype" value="5"> 打招呼
     </c:if>
     <c:if test="${manageconfig}"><input type="radio" name="optype" value="7"> 赠送道具</c:if>
     <c:if test="${managespaceinfo}"><input type="radio" name="optype" value="6"> 清理CSS</c:if>
     <input type="hidden" name="mpurl" value="${mpurl}">
     <input type="submit" name="listsubmit" value="提交" class="submit">
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