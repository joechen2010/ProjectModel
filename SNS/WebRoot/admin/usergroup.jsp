<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <div class="tabs_header">
   <ul class="tabs">
    <li${actives_view}><a href="admincp.jsp?ac=usergroup"><span>浏览用户组</span></a></li>
    <li class="null"><a href="admincp.jsp?ac=usergroup&op=add">添加新用户组</a></li>
   </ul>
  </div>
  <c:choose><c:when test="${not empty list}">
   <form method="post" action="admincp.jsp?ac=usergroup">
    <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}">
    <div class="bdrcontent">
     <div class="title">
      <h3>普通用户组</h3>
      <p>普通用户组的用户级别，随着其经验值的变化而自动升级或者降级</p>
     </div>
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr><th>组头衔</th><td>经验值大于</td><td width="100">操作</td></tr>
      <c:forEach items="${list['0']}" var="value">
       <tr>
        <th><span${value.color}>${value.grouptitle}</span>${value.icon}</th>
        <td>
         <input type="hidden" name="gid[]" value="${value.gid}">
         <input type="text" size="15" value="${value.explower}" name="explower_${value.gid}"${value.explower == -999999999 ? " readonly" : ""}>
        </td>
        <td width="100">
         <a href="admincp.jsp?ac=usergroup&op=edit&gid=${value.gid}">编辑</a>
         <a href="admincp.jsp?ac=usergroup&op=copy&gid=${value.gid}">复制</a>
         <a href="admincp.jsp?ac=usergroup&op=delete&gid=${value.gid}">删除</a>
        </td>
       </tr>
      </c:forEach>
     </table>
    </div>
    <div class="footactions"><input type="submit" name="updatesubmit" value="提交更新" class="submit"></div>
   </form>
   <div class="bdrcontent">
    <div class="title">
     <h3>特殊用户组</h3>
     <p>归属于特殊用户组的用户级别身份，不受经验值的影响，始终保持不变</p>
    </div>
    <table cellspacing="0" cellpadding="0" class="formtable">
     <c:forEach items="${list['1']}" var="value">
      <tr>
       <th><span${value.color}>${value.grouptitle}</span>${value.icon}</th>
       <td width="100">
        <a href="admincp.jsp?ac=usergroup&op=edit&gid=${value.gid}">编辑</a>
        <a href="admincp.jsp?ac=usergroup&op=copy&gid=${value.gid}">复制</a>
        <a href="admincp.jsp?ac=usergroup&op=delete&gid=${value.gid}">删除</a>
       </td>
      </tr>
     </c:forEach>
    </table>
   </div><br>
   <div class="bdrcontent">
    <div class="title">
     <h3>系统用户组</h3>
     <p>系统内置，用户组不能被编辑或删除；用户级别身份，不受经验值的变化的影响</p>
    </div>
    <table cellspacing="0" cellpadding="0" class="formtable">
     <c:forEach items="${list['-1']}" var="value">
      <tr>
       <th><span${value.color}>${value.grouptitle}</span>${value.icon}</th>
       <td width="80">
        <a href="admincp.jsp?ac=usergroup&op=edit&gid=${value.gid}">编辑</a> |
        <a href="admincp.jsp?ac=usergroup&op=copy&gid=${value.gid}">复制</a>
       </td>
      </tr>
     </c:forEach>
    </table>
   </div>
  </c:when><c:when test="${param.op == 'copy'}">
   <form method="post" action="admincp.jsp?ac=usergroup&gid=${thevalue.gid}">
    <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
    <div class="bdrcontent">
     <div class="title">
      <h3>权限复制</h3>
      <p>这里可以把目标用户组权限应用给选中的用户组</p>
     </div>
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr>
       <th style="width: 12em;">源用户组</th>
       <td>${thevalue.grouptitle}</td>
      </tr>
      <tr>
       <th>目标用户组</th>
       <td>
        <select name="aimgroup" size="10" multiple="multiple" style="width: 80%">
         <c:forEach items="${userGroups}" var="userGroup">
          <option value="${userGroup.gid}">${userGroup.grouptitle}</option>
         </c:forEach>
        </select>
        <p>选择要将源用户组权限复制到哪些目标用户组，可以按住 CTRL 多选</p>
       </td>
      </tr>
     </table>
    </div>
    <div class="footactions"><input type="submit" name="copysubmit" value="提交" class="submit"></div>
   </form>
  </c:when><c:otherwise>
   <form method="post" action="admincp.jsp?ac=usergroup&gid=${thevalue.gid}">
    <input type="hidden" name="set[gid]" value="${thevalue.gid}">
    <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}">
    <div class="bdrcontent">
     <div class="title">
      <h3>${thevalue.grouptitle} 空间权限 </h3>
      <p>这里设置该用户组成员对自己的个人空间的操作权限许可</p>
     </div>
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr>
       <th style="width: 12em;">名称</th>
       <td><input type="text" name="set[grouptitle]" value="${thevalue.grouptitle}"></td>
      </tr>
      <c:if test="${empty thevalue.system}">
       <tr>
        <th>用户组类型</th>
        <td>
         <input type="radio" name="set[system]" value="0" onclick="credisshow(0)" checked> 普通用户组
         <input type="radio" name="set[system]" value="1" onclick="credisshow(1)"> 特殊用户组 (不受经验值限制)
          <script type="text/javascript">
           function credisshow(value) {
            var display = 'none';
            if(value=='0') {
             display = '';
            }
            $('tr_credit').style.display = display
          }
         </script>
        </td>
       </tr>
      </c:if>
      <tr>
       <th>禁止访问</th>
       <td>
        <input type="radio" name="set[banvisit]" value="0" ${thevalue.banvisit !=1 ? "checked" : ""}> 允许访问
        <input type="radio" name="set[banvisit]" value="1" ${thevalue.banvisit ==1 ? "checked" : ""}> 禁止访问
       </td>
      </tr>
      <c:if test="${thevalue.system == 0 || empty thevalue.system}">
       <tr id="tr_credit">
        <th>经验值下限</th>
        <td>
         <c:choose>
          <c:when test="${thevalue.explower > -999999999}"><input type="text" name="set[explower]" value="${thevalue.explower}" size="20"></c:when>
          <c:otherwise><input type="hidden" name="set[explower]" value="${thevalue.explower}" size="20">${thevalue.explower} (系统默认最低分，不能修改)</c:otherwise>
         </c:choose>
         成为本用户组的最低经验
        </td>
       </tr>
      </c:if>
      <tr>
       <th>用户名显示颜色</th>
       <td><input type="text" name="set[color]" value="${thevalue.color}" size="10"> 例如：#FF0000</td>
      </tr>
      <tr>
       <th>用户身份识别图标</th>
       <td><input type="text" name="set[icon]" value="${thevalue.icon}" size="40"> 填写URL地址，大小 20*20 最佳，会显示在用户名的后面，作为身份识别</td>
      </tr>
      <tr>
       <th>上传空间大小</th>
       <td><input type="text" name="set[maxattachsize]" value="${thevalue.maxattachsize}" size="10"> 单位:M, 0为无限</td>
      </tr>
      <tr>
       <th>最多好友数</th>
       <td><input type="text" name="set[maxfriendnum]" value="${thevalue.maxfriendnum}" size="10"> 0为无限</td>
      </tr>
      <tr>
       <th>两次发布操作间隔</th>
       <td><input type="text" name="set[postinterval]" value="${thevalue.postinterval}" size="10"> 单位:秒 , 0为不限制，包括日志/评论/留言/话题/回帖等发布操作</td>
      </tr>
      <tr>
       <th>发布操作需填验证码</th>
       <td>
        <input type="radio" name="set[seccode]" value="0" ${thevalue.seccode !=1 ? "checked" : ""}> 不需要验证码
        <input type="radio" name="set[seccode]" value="1" ${thevalue.seccode ==1 ? "checked" : ""}> 填写验证码
        <br>包括记录/日志/话题/分享/礼物的发布操作，开启验证码可以防止灌水机等，但会增加用户操作易用度。
       </td>
      </tr>
      <tr>
       <th>两次搜索操作间隔</th>
       <td><input type="text" name="set[searchinterval]" value="${thevalue.searchinterval}" size="10"> 单位:秒 , 0为不限制</td>
      </tr>
      <tr>
       <th>是否免费搜索</th>
       <td>
        <input type="radio" name="set[searchignore]" value="1" ${thevalue.searchignore ==1 ? "checked" : ""}> 免费搜索
        <input type="radio" name="set[searchignore]" value="0" ${thevalue.searchignore !=1 ? "checked" : ""}> 搜索要扣积分
       </td>
      </tr>
      <tr>
       <th>二级域名最短长度</th>
       <td><input type="text" name="set[domainlength]" value="${thevalue.domainlength}" size="10"> 0为禁止使用二级域名，在站点开启二级域名时有效</td>
      </tr>
      <tr>
       <th>防灌水限制</th>
       <td>
        <input type="radio" name="set[spamignore]" value="0" ${thevalue.spamignore !=1? "checked" : ""}> 受限制
        <input type="radio" name="set[spamignore]" value="1" ${thevalue.spamignore==1 ? "checked" : ""}> 不受灌水限制
       </td>
      </tr>
      <tr>
       <th>发表记录</th>
       <td>
        <input type="radio" name="set[allowdoing]" value="1" ${thevalue.allowdoing==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowdoing]" value="0" ${thevalue.allowdoing !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>发表日志</th>
       <td>
        <input type="radio" name="set[allowblog]" value="1" ${thevalue.allowblog==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowblog]" value="0" ${thevalue.allowblog !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>上传图片</th>
       <td>
        <input type="radio" name="set[allowupload]" value="1" ${thevalue.allowupload==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowupload]" value="0" ${thevalue.allowupload !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>发布分享</th>
       <td>
        <input type="radio" name="set[allowshare]" value="1" ${thevalue.allowshare==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowshare]" value="0" ${thevalue.allowshare !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>发表留言/评论</th>
       <td>
        <input type="radio" name="set[allowcomment]" value="1" ${thevalue.allowcomment==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowcomment]" value="0" ${thevalue.allowcomment !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>允许表态</th>
       <td>
        <input type="radio" name="set[allowclick]" value="1" ${thevalue.allowclick==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowclick]" value="0" ${thevalue.allowclick !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>创建新群组</th>
       <td>
        <input type="radio" name="set[allowmtag]" value="1" ${thevalue.allowmtag==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowmtag]" value="0" ${thevalue.allowmtag !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>发表群组话题</th>
       <td>
        <input type="radio" name="set[allowthread]" value="1" ${thevalue.allowthread==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowthread]" value="0" ${thevalue.allowthread !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>编辑话题附加编辑记录</th>
       <td>
        <input type="radio" name="set[edittrail]" value="1" ${thevalue.edittrail==1 ? "checked" : ""}> 是
        <input type="radio" name="set[edittrail]" value="0" ${thevalue.edittrail !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>发表群组回帖</th>
       <td>
        <input type="radio" name="set[allowpost]" value="1" ${thevalue.allowpost==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowpost]" value="0" ${thevalue.allowpost !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>发起投票</th>
       <td>
        <input type="radio" name="set[allowpoll]" value="1" ${thevalue.allowpoll==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowpoll]" value="0" ${thevalue.allowpoll !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>发布活动</th>
       <td>
        <input type="radio" name="set[allowevent]" value="1" ${thevalue.allowevent==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowevent]" value="0" ${thevalue.allowevent !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>发表的活动需要审核</th>
       <td>
        <input type="radio" name="set[verifyevent]" value="1" ${thevalue.verifyevent==1 ? "checked" : ""}> 是
        <input type="radio" name="set[verifyevent]" value="0" ${thevalue.verifyevent !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>允许发送礼物</th>
       <td>
        <input type="radio" name="set[allowgift]" value="1" ${thevalue.allowgift==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowgift]" value="0" ${thevalue.allowgift !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>允许发短消息</th>
       <td>
        <input type="radio" name="set[allowpm]" value="1" ${thevalue.allowpm==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowpm]" value="0" ${thevalue.allowpm !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>允许打招呼</th>
       <td>
        <input type="radio" name="set[allowpoke]" value="1" ${thevalue.allowpoke==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowpoke]" value="0" ${thevalue.allowpoke !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>允许加好友</th>
       <td>
        <input type="radio" name="set[allowfriend]" value="1" ${thevalue.allowfriend==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowfriend]" value="0" ${thevalue.allowfriend !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>视频认证限制</th>
       <td>
        <input type="radio" name="set[videophotoignore]" value="1" ${thevalue.videophotoignore==1 ? "checked" : ""}> 不受视频认证限制
        <input type="radio" name="set[videophotoignore]" value="0" ${thevalue.videophotoignore !=1? "checked" : ""}> 受限制
       </td>
      </tr>
      <tr>
       <th>允许查看视频认证</th>
       <td>
        <input type="radio" name="set[allowviewvideopic]" value="1" ${thevalue.allowviewvideopic==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowviewvideopic]" value="0" ${thevalue.allowviewvideopic !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>允许使用应用</th>
       <td>
        <input type="radio" name="set[allowmyop]" value="1" ${thevalue.allowmyop==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowmyop]" value="0" ${thevalue.allowmyop !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>允许使用道具</th>
       <td>
        <input type="radio" name="set[allowmagic]" value="1" ${thevalue.allowmagic==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowmagic]" value="0" ${thevalue.allowmagic !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>购买道具折扣</th>
       <td>
        <select name="set[magicdiscount]">
         <c:forEach items="${discount}" var="m">
          <option value="${m.key}"${thevalue.magicdiscount== m.key ? " selected" : ""}>${m.value}</option>
         </c:forEach>
        </select>
       </td>
      </tr>
      <tr>
       <th>添加新的热闹</th>
       <td>
        <input type="radio" name="set[allowtopic]" value="1" ${thevalue.allowtopic==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowtopic]" value="0" ${thevalue.allowtopic !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>允许自定义CSS</th>
       <td>
        <input type="radio" name="set[allowcss]" value="1" ${thevalue.allowcss==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowcss]" value="0" ${thevalue.allowcss !=1? "checked" : ""}> 否 &nbsp;谨慎允许，允许自定义CSS可能会造成javascript脚本引起的不安全因素
       </td>
      </tr>
      <tr>
       <th>日志全HTML标签支持</th>
       <td>
        <input type="radio" name="set[allowhtml]" value="1" ${thevalue.allowhtml==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowhtml]" value="0" ${thevalue.allowhtml !=1? "checked" : ""}> 否 &nbsp;谨慎允许，支持所有html标签可能会造成javascript脚本引起的不安全因素
       </td>
      </tr>
      <tr>
       <th>允许查看趋势统计</th>
       <td>
        <input type="radio" name="set[allowstat]" value="1" ${thevalue.allowstat==1 ? "checked" : ""}> 是
        <input type="radio" name="set[allowstat]" value="0" ${thevalue.allowstat !=1? "checked" : ""}> 否
       </td>
      </tr>
      <tr>
       <th>站点关闭和IP屏蔽</th>
       <td>
        <input type="radio" name="set[closeignore]" value="1" ${thevalue.closeignore==1 ? "checked" : ""}> 不受站点关闭和IP屏蔽限制
        <input type="radio" name="set[closeignore]" value="0" ${thevalue.closeignore !=1? "checked" : ""}> 受限制
       </td>
      </tr>
      <tr>
       <th>升级奖励道具</th>
       <td>
        <select id="newmagicaward">
         <c:forEach items="${globalMagic}" var="m">
          <option value="${m.key}">${m.value}</option>
         </c:forEach>
        </select>
        <input type="text" id="newmagicawardnum" value="1" />
        <input class="button" type="button" onclick="addMagicAward()" value="添加" />
        <ul id="magicawards">
         <c:forEach items="${thevalue.magicaward}" var="m">
          <li id="magicaward_${m.value.mid}"><input type="hidden" name="magicaward[]" value="${m.value.mid},${m.value.num}"> ${globalMagic[m.value.mid]} &nbsp;&nbsp; ${m.value.num} &nbsp;&nbsp; <a href="#" onclick="removeMagicAward(this);return false;">删除</a></li>
         </c:forEach>
        </ul>
        <script type="text/javascript">
   function addMagicAward(){
    var mid = $('newmagicaward').value;
    var id = "magicaward_" + mid;
    var num = $('newmagicawardnum').value;
    var name = $('newmagicaward').options[$('newmagicaward').selectedIndex].text;
    if($(id)) {
     removeMagicAward($(id).getElementsByTagName("a")[0]);
    }
    var s = '<li id="' + id + '">';
    s += '<input type="hidden" name="magicaward[]" value="' + mid + ',' + num + '" />';
    s += name + ' &nbsp;&nbsp;' + "\n";
    s += num + ' &nbsp;&nbsp;' + "\n";
    s += '<a href="#" onclick="removeMagicAward(this);return false;">删除</a>';
    s += '</li>';
    $('magicawards').innerHTML += s;
   }
   function removeMagicAward(o) {
    $('magicawards').removeChild(o.parentNode);
   }
   </script>
       </td>
      </tr>
     </table>
     <c:if test="${thevalue.system != 0 && !empty thevalue.system}">
      <br />
      <div class="title">
       <h3>${thevalue.grouptitle} 管理权限</h3>
       <p>设置该用户组成员是否拥有站点管理权限，这可能会影响到站点数据安全，请谨慎选择</p>
      </div>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th style="width: 12em;">管理员身份</th>
        <td>
         <input type="radio" name="set[manageconfig]" value="1" ${thevalue.manageconfig==1 ? "checked" : ""}> 拥有管理员身份
         <input type="radio" name="set[manageconfig]" value="0" ${thevalue.manageconfig !=1? "checked" : ""}> 禁止
         <br>注意，该用户组成员拥有管理员身份后，将不再受下面权限设置，将自动拥有全部权限
        </td>
       </tr>
       <tr>
        <th>批量管理操作</th>
        <td>
         <input type="radio" name="set[managebatch]" value="1" ${thevalue.managebatch==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managebatch]" value="0" ${thevalue.managebatch !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>标签</th>
        <td>
         <input type="radio" name="set[managetag]" value="1" ${thevalue.managetag==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managetag]" value="0" ${thevalue.managetag !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>群组</th>
        <td>
         <input type="radio" name="set[managemtag]" value="1" ${thevalue.managemtag==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managemtag]" value="0" ${thevalue.managemtag !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>活动</th>
        <td>
         <input type="radio" name="set[manageevent]" value="1" ${thevalue.manageevent==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageevent]" value="0" ${thevalue.manageevent !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>动态(feed)</th>
        <td>
         <input type="radio" name="set[managefeed]" value="1" ${thevalue.managefeed==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managefeed]" value="0" ${thevalue.managefeed !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>记录</th>
        <td>
         <input type="radio" name="set[managedoing]" value="1" ${thevalue.managedoing==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managedoing]" value="0" ${thevalue.managedoing !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>分享</th>
        <td>
         <input type="radio" name="set[manageshare]" value="1" ${thevalue.manageshare==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageshare]" value="0" ${thevalue.manageshare !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>日志</th>
        <td>
         <input type="radio" name="set[manageblog]" value="1" ${thevalue.manageblog==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageblog]" value="0" ${thevalue.manageblog !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>相册</th>
        <td>
         <input type="radio" name="set[managealbum]" value="1" ${thevalue.managealbum==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managealbum]" value="0" ${thevalue.managealbum !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>评论</th>
        <td>
         <input type="radio" name="set[managecomment]" value="1" ${thevalue.managecomment==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managecomment]" value="0" ${thevalue.managecomment !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>话题</th>
        <td>
         <input type="radio" name="set[managethread]" value="1" ${thevalue.managethread==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managethread]" value="0" ${thevalue.managethread !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>投票</th>
        <td>
         <input type="radio" name="set[managepoll]" value="1" ${thevalue.managepoll==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managepoll]" value="0" ${thevalue.managepoll !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>礼物</th>
        <td>
         <input type="radio" name="set[managegift]" value="1" ${thevalue.managegift==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managegift]" value="0" ${thevalue.managegift !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>道具记录</th>
        <td>
         <input type="radio" name="set[managemagiclog]" value="1" ${thevalue.managemagiclog==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managemagiclog]" value="0" ${thevalue.managemagiclog !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>随便看看</th>
        <td>
         <input type="radio" name="set[managenetwork]" value="1" ${thevalue.managenetwork==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managenetwork]" value="0" ${thevalue.managenetwork !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>用户组</th>
        <td>
         <input type="radio" name="set[manageusergroup]" value="1" ${thevalue.manageusergroup==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageusergroup]" value="0" ${thevalue.manageusergroup !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>积分规则</th>
        <td>
         <input type="radio" name="set[managecredit]" value="1" ${thevalue.managecredit==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managecredit]" value="0" ${thevalue.managecredit !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>用户栏目</th>
        <td>
         <input type="radio" name="set[manageprofilefield]" value="1" ${thevalue.manageprofilefield==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageprofilefield]" value="0" ${thevalue.manageprofilefield !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>群组栏目</th>
        <td>
         <input type="radio" name="set[manageprofield]" value="1" ${thevalue.manageprofield==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageprofield]" value="0" ${thevalue.manageprofield !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>活动分类</th>
        <td>
         <input type="radio" name="set[manageeventclass]" value="1" ${thevalue.manageeventclass==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageeventclass]" value="0" ${thevalue.manageeventclass !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>词语屏蔽</th>
        <td>
         <input type="radio" name="set[managecensor]" value="1" ${thevalue.managecensor==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managecensor]" value="0" ${thevalue.managecensor !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>广告设置</th>
        <td>
         <input type="radio" name="set[managead]" value="1" ${thevalue.managead==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managead]" value="0" ${thevalue.managead !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>举报管理</th>
        <td>
         <input type="radio" name="set[managereport]" value="1" ${thevalue.managereport==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managereport]" value="0" ${thevalue.managereport !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>缓存更新</th>
        <td>
         <input type="radio" name="set[managecache]" value="1" ${thevalue.managecache==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managecache]" value="0" ${thevalue.managecache !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>多产品/应用</th>
        <td>
         <input type="radio" name="set[manageapp]" value="1" ${thevalue.manageapp==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageapp]" value="0" ${thevalue.manageapp !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>数据调用</th>
        <td>
         <input type="radio" name="set[manageblock]" value="1" ${thevalue.manageblock==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageblock]" value="0" ${thevalue.manageblock !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>模板编辑</th>
        <td>
         <input type="radio" name="set[managetemplate]" value="1" ${thevalue.managetemplate==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managetemplate]" value="0" ${thevalue.managetemplate !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>数据备份</th>
        <td>
         <input type="radio" name="set[managebackup]" value="1" ${thevalue.managebackup==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managebackup]" value="0" ${thevalue.managebackup !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>统计更新</th>
        <td>
         <input type="radio" name="set[managestat]" value="1" ${thevalue.managestat==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managestat]" value="0" ${thevalue.managestat !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>计划任务</th>
        <td>
         <input type="radio" name="set[managecron]" value="1" ${thevalue.managecron==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managecron]" value="0" ${thevalue.managecron !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>IP设置</th>
        <td>
         <input type="radio" name="set[manageip]" value="1" ${thevalue.manageip==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageip]" value="0" ${thevalue.manageip !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>推荐成员设置</th>
        <td>
         <input type="radio" name="set[managehotuser]" value="1" ${thevalue.managehotuser==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managehotuser]" value="0" ${thevalue.managehotuser !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>默认好友设置</th>
        <td>
         <input type="radio" name="set[managedefaultuser]" value="1" ${thevalue.managedefaultuser==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managedefaultuser]" value="0" ${thevalue.managedefaultuser !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>删除用户</th>
        <td>
         <input type="radio" name="set[managedelspace]" value="1" ${thevalue.managedelspace==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managedelspace]" value="0" ${thevalue.managedelspace !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>实名认证</th>
        <td>
         <input type="radio" name="set[managename]" value="1" ${thevalue.managename==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managename]" value="0" ${thevalue.managename !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>视频认证</th>
        <td>
         <input type="radio" name="set[managevideophoto]" value="1" ${thevalue.managevideophoto==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managevideophoto]" value="0" ${thevalue.managevideophoto !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>用户保护信息</th>
        <td>
         <input type="radio" name="set[managespacegroup]" value="1" ${thevalue.managespacegroup==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managespacegroup]" value="0" ${thevalue.managespacegroup !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>用户基本信息</th>
        <td>
         <input type="radio" name="set[managespaceinfo]" value="1" ${thevalue.managespaceinfo==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managespaceinfo]" value="0" ${thevalue.managespaceinfo !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>用户积分</th>
        <td>
         <input type="radio" name="set[managespacecredit]" value="1" ${thevalue.managespacecredit==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managespacecredit]" value="0" ${thevalue.managespacecredit !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>向用户发通知</th>
        <td>
         <input type="radio" name="set[managespacenote]" value="1" ${thevalue.managespacenote==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managespacenote]" value="0" ${thevalue.managespacenote !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>有奖任务</th>
        <td>
         <input type="radio" name="set[managetask]" value="1" ${thevalue.managetask==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managetask]" value="0" ${thevalue.managetask !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>道具设置</th>
        <td>
         <input type="radio" name="set[managemagic]" value="1" ${thevalue.managemagic==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managemagic]" value="0" ${thevalue.managemagic !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>表态动作设置</th>
        <td>
         <input type="radio" name="set[manageclick]" value="1" ${thevalue.manageclick==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[manageclick]" value="0" ${thevalue.manageclick !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>热闹管理</th>
        <td>
         <input type="radio" name="set[managetopic]" value="1" ${thevalue.managetopic==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managetopic]" value="0" ${thevalue.managetopic !=1? "checked" : ""}> 禁止
        </td>
       </tr>
       <tr>
        <th>管理记录</th>
        <td>
         <input type="radio" name="set[managelog]" value="1" ${thevalue.managelog==1 ? "checked" : ""}> 可管理
         <input type="radio" name="set[managelog]" value="0" ${thevalue.managelog !=1? "checked" : ""}> 禁止
        </td>
       </tr>
      </table>
     </c:if>
    </div>
    <div class="footactions"><input type="submit" name="thevaluesubmit" value="提交" class="submit"></div>
   </form>
  </c:otherwise></c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />