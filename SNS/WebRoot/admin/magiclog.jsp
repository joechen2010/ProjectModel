<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <div class="tabs_header">
   <ul class="tabs">
    <li${actives_holdlog}><a href="admincp.jsp?ac=magiclog&view=holdlog"><span>道具持有记录</span></a></li>
    <li${actives_inlog}><a href="admincp.jsp?ac=magiclog&view=inlog"><span>道具获取记录</span></a></li>
    <li${actives_uselog}><a href="admincp.jsp?ac=magiclog&view=uselog"><span>道具使用记录</span></a></li>
    <c:if test="${allowmanage}"><li${actives_storelog}><a href="admincp.jsp?ac=magiclog&view=storelog"><span>道具出售统计</span></a></li></c:if>
   </ul>
  </div>
  <c:choose>
   <c:when test="${param.view == 'inlog'}">
    <form method="post" action="admincp.jsp">
     <div class="block style4">
      <table cellspacing="3" cellpadding="3">
       <tr>
        <th>用户名</th>
        <td><input type="text" name="username" value="${allowmanage ? param.username : sGlobal.supe_username}"${disabled}></td>
        <th>道具</th>
        <td>
         <select name="mid">
          <option value="">不限</option>
          <c:forEach items="${globalMagic}" var="m">
           <option value="${m.key}"${param.mid==m.key ? " selected" : ""}>${m.value}</option>
          </c:forEach>
         </select>
        </td>
       </tr>
       <tr>
        <th>交易量</th>
        <td>
         <select name="count">
          <option value="">不限</option>
          <option value="1-4"${param.count=='1-4' ? " selected" : ""}>1 - 4</option>
          <option value="5-9"${param.count=='5-9' ? " selected" : ""}>5 - 9</option>
          <option value="10-49"${param.count=='10-49' ? " selected" : ""}>10 - 49</option>
          <option value="50-99"${param.count=='50-99' ? " selected" : ""}>50 - 99</option>
          <option value="100-99999"${param.count=='100-99999' ? " selected" : ""}>100以上</option>
         </select>
        </td>
        <th>获得途径</th>
        <td>
         <select name="type">
          <option value="">不限</option>
          <option value="1"${param.type==1 ? " selected" : ""}>购买</option>
          <option value="2"${param.type==2 ? " selected" : ""}>赠送</option>
          <option value="3"${param.type==3 ? " selected" : ""}>升级</option>
         </select>
        </td>
       </tr>
       <tr>
        <th>记录时间</th>
        <td colspan="3">
         <script type="text/javascript" src="source/script_calendar.js" charset="${jchConfig.charset}"></script>
         <input type="text" name="starttime" value="${param.starttime}" onclick="showcalendar(event,this,1)" /> ~
         <input type="text" name="endtime" value="${param.endtime}" onclick="showcalendar(event,this,1)" />
         <input type="hidden" name="view" value="${param.view}">
         <input type="hidden" name="ac" value="magiclog">
         <input type="submit" name="searchsubmit" value="搜索" class="submit">
        </td>
       </tr>
      </table>
     </div>
    </form>
    <c:choose>
     <c:when test="${not empty list}">
      <div class="bdrcontent">
       <table width="100%">
        <tr><th>用户</th><th>方式</th><th>道具</th><th>数量</th><th>时间</th></tr>
        <c:forEach items="${list}" var="value">
         <tr>
          <td><a href="admincp.jsp?ac=magiclog&view=inlog&username=${value.username}">${value.username}</a></td>
          <td>
           <c:choose>
            <c:when test="${value.type == 2}">获赠</c:when>
            <c:when test="${value.type == 3}">升级用户组</c:when>
            <c:otherwise>购买</c:otherwise>
           </c:choose>
          </td>
          <td><a href="admincp.jsp?ac=magiclog&view=inlog&mid=${value.mid}">${globalMagic[value.mid]}</a></td>
          <td>${value.count}</td>
          <td>${value.dateline}</td>
         </tr>
        </c:forEach>
       </table>
      </div>
      <div class="footactions">
       <div class="pages">${multi}</div>
      </div>
     </c:when>
     <c:otherwise><div class="bdrcontent">没有指定数据</div></c:otherwise>
    </c:choose>
   </c:when>
   <c:when test="${param.view == 'uselog'}">
    <form method="post" action="admincp.jsp">
     <div class="block style4">
      <table cellspacing="3" cellpadding="3">
       <tr>
        <th>用户名</th>
        <td><input type="text" name="username" value="${allowmanage ? param.username : sGlobal.supe_username}"${disabled}></td>
        <th>道具</th>
        <td>
         <select name="mid">
          <option value="">不限</option>
          <c:forEach items="${globalMagic}" var="m">
           <option value="${m.key}"${param.mid==m.key ? " selected" : ""}>${m.value}</option>
          </c:forEach>
         </select>
        </td>
       </tr>
       <tr>
        <th>作用对象类型</th>
        <td>
         <select name="idtype">
          <option value="">不限</option>
          <option value="blogid"${param.idtype=='blogid' ? " selected" : ""}>日志</option>
          <option value="tid"${param.idtype=='tid' ? " selected" : ""}>话题</option>
          <option value="cid"${param.idtype=='cid' ? " selected" : ""}>评论/留言</option>
          <option value="uid"${param.idtype=='uid' ? " selected" : ""}>空间</option>
          <option value="picid"${param.idtype=='picid' ? " selected" : ""}>图片</option>
          <option value="pollid"${param.idtype=='pollid' ? " selected" : ""}>投票</option>
          <option value="eventid"${param.idtype=='eventid' ? " selected" : ""}>活动</option>
         </select>
        </td>
        <th>作用对象ID</th>
        <td><input type="text" name="id" value="${param.id}" /></td>
       </tr>
       <tr>
        <th>记录时间</th>
        <td colspan="3">
         <script type="text/javascript" src="source/script_calendar.js"></script>
         <input type="text" name="starttime" value="${param.starttime}" onclick="showcalendar(event,this,1)" /> ~
         <input type="text" name="endtime" value="${param.endtime}" onclick="showcalendar(event,this,1)" />
         <input type="hidden" name="view" value="${param.view}">
         <input type="hidden" name="ac" value="magiclog">
         <input type="submit" name="searchsubmit" value="搜索" class="submit">
        </td>
       </tr>
      </table>
     </div>
    </form>
    <c:choose>
     <c:when test="${not empty list}">
      <div class="bdrcontent">
       <table width="100%">
        <tr><th>用户</th><th>道具</th><th>时间</th></tr>
        <c:forEach items="${list}" var="value">
         <tr>
          <td><a href="admincp.jsp?ac=magiclog&view=uselog&username=${value.username}">${value.username}</a></td>
          <td><a href="admincp.jsp?ac=magiclog&view=uselog&mid=${value.mid}">${globalMagic[value.mid]}</a></td>
          <td>${value.dateline}</td>
         </tr>
        </c:forEach>
       </table>
      </div>
      <div class="footactions">
       <div class="pages">${multi}</div>
      </div>
     </c:when>
     <c:otherwise><div class="bdrcontent">没有指定数据</div></c:otherwise>
    </c:choose>
   </c:when>
   <c:when test="${param.view == 'storelog'}">
    <c:choose>
     <c:when test="${not empty list}">
      <div class="bdrcontent">
       <h3>共售出道具 ${totalcount} 件，回收 ${totalcredit} 积分</h3><br />
       <table width="100%">
        <tr><th>道具</th><th>售出数</th><th>回收积分</th></tr>
        <c:forEach items="${list}" var="value">
         <tr>
          <td><a href="admincp.jsp?ac=magiclog&view=holdlog&mid=${value.mid}">${globalMagic[value.mid]}</a></td>
          <td>${value.sellcount}</td>
          <td>${value.sellcredit}</td>
         </tr>
        </c:forEach>
       </table>
      </div>
      <div class="footactions">
       <div class="pages">${multi}</div>
      </div>
     </c:when>
     <c:otherwise><div class="bdrcontent">没有指定数据</div></c:otherwise>
    </c:choose>
   </c:when>
   <c:otherwise>
    <form method="post" action="admincp.jsp">
     <div class="block style4">
      <table cellspacing="3" cellpadding="3">
       <tr>
        <th>用户UID</th>
        <td><input type="text" name="uid" value="${allowmanage ? param.uid : sGlobal.supe_uid}"${disabled}></td>
        <th>用户名</th>
        <td><input type="text" name="username" value="${allowmanage ? param.username : sGlobal.supe_username}"${disabled}></td>
       </tr>
       <tr>
        <th>道具</th>
        <td colspan="3">
         <select name="mid">
          <option value="">不限</option>
          <c:forEach items="${globalMagic}" var="m">
           <option value="${m.key}"${param.mid==m.key ? " selected" : ""}>${m.value}</option>
          </c:forEach>
         </select>
         <input type="hidden" name="view" value="${param.view}">
         <input type="hidden" name="ac" value="magiclog">
         <input type="submit" name="searchsubmit" value="搜索" class="submit">
        </td>
       </tr>
      </table>
     </div>
    </form>
    <c:choose>
     <c:when test="${not empty list}">
      <div class="bdrcontent">
       <table width="100%">
        <tr><th>用户</th><th>道具</th><th>数量</th></tr>
        <c:forEach items="${list}" var="value">
         <tr>
          <td><a href="admincp.jsp?ac=magiclog&view=holdlog&uid=${value.uid}"> ${value.username} </a></td>
          <td><a href="admincp.jsp?ac=magiclog&view=holdlog&mid=${value.mid}">${globalMagic[value.mid]}</a></td>
          <td>${value.count}</td>
         </tr>
        </c:forEach>
       </table>
      </div>
      <div class="footactions">
       <div class="pages">${multi}</div>
      </div>
     </c:when>
     <c:otherwise><div class="bdrcontent">没有指定数据</div></c:otherwise>
    </c:choose>
   </c:otherwise>
  </c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />