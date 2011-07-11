<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <c:choose>
   <c:when test="${uid>0}">
    <form method="post" action="admincp.jsp?ac=space&uid=${uid}" enctype="multipart/form-data">
     <input type="hidden" name="formhash" value="${FORMHASH}"/>
     <div class="bdrcontent">
      <div class="title"><h3>${member.username} 基本信息</h3></div>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th style="width: 12em;">用户名</th>
        <td><a href="space.jsp?uid=${member.uid}" target="_blank">${member.username}</a></td>
       </tr>
       <tr><th>开通时间</th><td>${member.dateline}</td></tr>
       <tr><th>更新时间</th><td>${member.updatetime}</td></tr>
       <tr><th>上次登录</th><td>${member.lastlogin}</td></tr>
       <tr><th>注册IP</th><td>${member.regip}</td></tr>
       <tr><th>好友数</th><td>${member.friendnum}</td></tr>
       <tr><th>查看数</th><td>${member.viewnum}</td></tr>
       <tr>
        <th>批量管理</th>
        <td>
         <a href="admincp.jsp?ac=blog&uid=${member.uid}" target="_blank">日志(${member.blognum})</a> |
         <a href="admincp.jsp?ac=album&uid=${member.uid}" target="_blank">相册(${member.albumnum})</a> |
         <a href="admincp.jsp?ac=thread&uid=${member.uid}" target="_blank">话题(${member.threadnum})</a> |
         <a href="admincp.jsp?ac=poll&uid=${member.uid}" target="_blank">投票(${member.pollnum})</a> |
         <a href="admincp.jsp?ac=event&uid=${member.uid}" target="_blank">活动(${member.eventnum})</a> |
         <a href="admincp.jsp?ac=doing&uid=${member.uid}" target="_blank">记录(${member.doingnum})</a> |
         <a href="admincp.jsp?ac=share&uid=${member.uid}" target="_blank">分享(${member.sharenum})</a> |
         <a href="admincp.jsp?ac=feed&uid=${member.uid}" target="_blank">事件</a> |
         <a href="admincp.jsp?ac=pic&uid=${member.uid}" target="_blank">图片</a> |
         <a href="admincp.jsp?ac=comment&authorid=${member.uid}" target="_blank">评论</a> |
         <a href="admincp.jsp?ac=post&uid=${member.uid}" target="_blank">回帖</a>
        </td>
       </tr>
       <tr>
        <th>&nbsp;</th>
        <td><c:choose>
         <c:when test="${member.flag!=1 && managedelspace}">
          <c:choose>
           <c:when test="${member.flag==-1}"><a href="admincp.jsp?ac=space&op=close&uid=${member.uid}" class="submit">解除锁定状态</a></c:when>
           <c:otherwise><a href="admincp.jsp?ac=space&op=close&uid=${member.uid}" onclick="return confirm('锁定后该空间将被禁止访问，确认锁定吗？');" class="submit">锁定空间(不会删除数据)</a></c:otherwise>
          </c:choose> &nbsp;
          <a href="admincp.jsp?ac=space&op=delete&uid=${member.uid}" onclick="return confirm('危险，这将删除该空间所有数据，并且本操作不可恢复，确认删除？');">删除该空间(删除数据并不可恢复)</a>&nbsp;&nbsp;&nbsp;
         </c:when>
         <c:otherwise>本用户被保护，不能删除、不能锁定</c:otherwise>
        </c:choose></td>
       </tr>
      </table>
     </div>
     <c:if test="${managespaceinfo}">
      <br>
      <div class="bdrcontent">
       <div class="title"><h3>${member.username} 个人资料</h3></div>
       <table cellspacing="0" cellpadding="0" class="formtable">
        <tr>
         <th style="width: 12em;">头像</th>
         <td>
          <a href="space.jsp?uid=${member.uid}" target="_blank">${member.avatar}</a>
          <br>[<a href="admincp.jsp?ac=space&op=deleteavatar&uid=${uid }">删除头像</a>]
         </td>
        </tr>
        <c:if test="${!jch:jchEmpty(sConfig.videophoto)}">
         <tr>
          <th>视频认证</th>
          <td>
           <p>
            <input type="radio" name="videostatus" value="0"${member.videostatus==0 ? " checked" : ""}> 未通过
            <input type="radio" name="videostatus" value="1"${member.videostatus==1 ? " checked" : ""}> 通过(需要有视频照片)
            <c:if test="${member.videostatus==0 && !jch:jchEmpty(member.videopic)}">
            <input type="radio" name="videostatus" value="2"}> 清除视频照片
            </c:if>
           </p>
           <c:if test="${!jch:jchEmpty(videopic)}"><img src="${videopic}" width="100"><br></c:if>
           上传一张该用户照片，更新视频认证照片:<br>
           <input type="file" name="newvideopic" value="">
          </td>
         </tr>
        </c:if>
        <tr>
         <th style="width: 12em;">常用邮箱</th>
         <td>
          <input type="text" id="email" class="t_input" name="email" value="${member.email}" />
          <input type="radio" name="emailcheck" value="0"${member.emailcheck==0 ? " checked" : ""}> 未激活
          <input type="radio" name="emailcheck" value="1"${member.emailcheck==1 ? " checked" : ""}> 已经验证激活
         </td>
        </tr>
        <c:if test="${!jch:jchEmpty(sConfig.allowdomain) && !jch:jchEmpty(sConfig.domainroot)}">
         <tr>
          <th style="width: 12em;">二级域名</th>
          <td><input type="text" class="t_input" name="domain" value="${member.domain}" size="10">.${sConfig.domainroot}</td>
         </tr>
        </c:if>
        <tr>
         <th style="width: 12em;">额外好友数</th>
         <td><input type="text" class="t_input" name="addfriend" value="${member.addfriend}" size="10"> 个</td>
        </tr>
        <tr>
         <th>清空自定义CSS</th>
         <td>
          <input type="radio" name="clearcss" value="0" checked> 不处理
          <input type="radio" name="clearcss" value="1"> 清空
          <p>用户自定义的CSS如果存在恶意代码，可以选择清空。</p>
         </td>
        </tr>
        <tr>
         <th>性别</th>
         <td>
          <label for="sexm"><input id="sexm" type="radio" value="1" name="sex"${member.sex==1 ? " checked" : ""}/>男</label>
          <label for="sexw"><input id="sexw" type="radio" value="2" name="sex"${member.sex==2 ? " checked" : ""}/>女</label>
         </td>
        </tr>
        <tr>
         <th>婚恋状态</th>
         <td>
          <select id="marry" name="marry">
           <option value="0">无</option>
           <option value="1"${member.marry==1 ? " selected" : ""}>单身</option>
           <option value="2"${member.marry==2 ? " selected" : ""}>非单身</option>
          </select>
         </td>
        </tr>
        <tr>
         <th>生日</th>
         <td>
          <select id="birthyear" name="birthyear">
           <option value="0">无</option>
           ${birthyeayhtml}
          </select> 年
          <select id="birthmonth" name="birthmonth">
           <option value="0">无</option>
           ${birthmonthhtml}
          </select> 月
          <select id="birthday" name="birthday">
           <option value="0">无</option>
           ${birthdayhtml}
          </select> 日
         </td>
        </tr>
        <tr>
         <th>血型</th>
         <td>
          <select id="blood" name="blood">
           <option value="">保密</option>
           ${bloodhtml}
          </select>
         </td>
        </tr>
        <tr>
         <th>家乡</th>
         <td id="birthcitybox">
          <script type="text/javascript" src="source/script_city.js"></script>
          <script type="text/javascript">
           showprovince('birthprovince', 'birthcity', '${member.birthprovince}', 'birthcitybox');
           showcity('birthcity', '${member.birthcity}', 'birthprovince', 'birthcitybox');
          </script>
         </td>
        </tr>
        <tr>
         <th>居住地</th>
         <td id="residecitybox">
          <script type="text/javascript">
           showprovince('resideprovince', 'residecity', '${member.resideprovince}', 'residecitybox');
           showcity('residecity', '${member.residecity}', 'resideprovince', 'residecitybox');
          </script>
         </td>
        </tr>
        <tr>
         <th>QQ</th>
         <td><input type="text" class="t_input" name="qq" value="${member.qq}" /></td>
        </tr>
        <tr>
         <th>MSN</th>
         <td><input type="text" class="t_input" name="msn" value="${member.msn}" /></td>
        </tr>
        <c:forEach items="${profileFields}" var="profileField">
         <tr>
          <th>${profileField.title}${profileField.required>0 ? '*' : ''}</th>
          <td>${profileField.formhtml}<c:if test="${!jch:jchEmpty(profileField.note)}"><br>${profileField.note}</c:if></td>
         </tr>
        </c:forEach>
       </table>
      </div>
     </c:if>
     <c:if test="${managename}">
      <br>
      <div class="bdrcontent">
       <div class="title"><h3>${member.username} 实名认证</h3></div>
       <table cellspacing="0" cellpadding="0" class="formtable">
        <tr>
         <th style="width: 12em;">姓名</th>
         <td>
          <input type="text" class="t_input" name="name" value="${member.name}">
          <input type="radio" name="namestatus" value="0"${member.namestatus==0 ? " checked" : ""}> 认证失败
          <input type="radio" name="namestatus" value="1"${member.namestatus==1 ? " checked" : ""}> 认证通过
         </td>
        </tr>
       </table>
      </div>
     </c:if>
     <c:if test="${managespacecredit}">
      <br>
      <div class="bdrcontent">
       <div class="title"><h3>${member.username} 积分、经验值、空间大小管理</h3></div>
       <table cellspacing="0" cellpadding="0" class="formtable">
        <tr>
         <th style="width: 12em;">额外空间大小</th>
         <td><input type="text" class="t_input" name="addsize" value="${member.addsize}" size="10"> M</td>
        </tr>
        <tr>
         <th>积分数</th>
         <td><input type="text" name="credit" class="t_input" value="${member.credit}" size="10"></td>
        </tr>
        <tr>
         <th>经验值</th>
         <td><input type="text" class="t_input" name="experience" value="${member.experience}" size="10"></td>
        </tr>
       </table>
      </div>
     </c:if>
     <c:if test="${managespacegroup}">
      <br>
      <div class="bdrcontent">
       <div class="title"><h3>${member.username} 保护信息</h3></div>
       <table cellspacing="0" cellpadding="0" class="formtable">
        <tr>
         <th style="width: 12em;">用户组</th>
         <td>
          <select name="groupid" onchange="showDateSet(this.value);">
           <option value="0">普通用户组</option>
           <c:forEach items="${userGroups}" var="userGroup">
            <option value="${userGroup.gid}"${userGroup.gid ==member.groupid ? " selected" :""}>${userGroup.grouptitle}</option>
           </c:forEach>
          </select>
          <p>普通用户组，会自动根据用户经验数目的多少进行自动升级/降级<br>系统用户组，用户的身份不受经验值影响</p>
         </td>
        </tr>
        <tr id="expirationtr"${expirationStyle}>
         <th>用户组过期时间</th>
         <td>
          <script type="text/javascript" src="source/script_calendar.js" charset="${jchConfig.charset}"></script>
                 <input type="text" class="t_input" name="expiration" value="${member.expiration}" size="20" onclick="showcalendar(event,this,1)">(格式：2009-08-08 00:00)
          <p>为空则永久有效</p>
         </td>
        </tr>
        <c:if test="${member.flag!=-1}">
         <tr>
          <th>删除保护</th>
          <td>
           <input type="radio" name="flag" value="0"${member.flag==0 ? " checked" : ""}> 不保护
           <input type="radio" name="flag" value="1"${member.flag==1 ? " checked" : ""}> 保护
           <p>保护状态下，该用户将不能够在JavaCenter、以及本应用中删除。</p>
          </td>
         </tr>
        </c:if>
       </table>
       <script type="text/javascript">
        function showDateSet(val) {
         var expObj = $("expirationtr");
         expObj.style.display = parseInt(val) ? '' : 'none';
        }
       </script>
      </div>
     </c:if>
     <div class="footactions">
      <input type="submit" name="usergroupsubmit" value="提交" class="submit">
     </div>
    </form>
   </c:when>
   <c:when test="${optype==4}">
    <form method="post" action="${mpurl}">
     <input type="hidden" name="formhash" value="${FORMHASH}"/>
     <div class="bdrcontent">
      <div class="title">
       <h3>批量发送邮件</h3>
       <p>您可以对选定的用户进行批量发送邮件。注意，本操作将会增加服务器负载。</p>
      </div>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th style="width: 8em;">收件人(UID)</th>
        <td><input type="text" name="uids" value="${uids}" size="60"> 多个UID间用 "," 分隔</td>
       </tr>
       <tr>
        <th>邮件标题</th>
        <td><input type="text" name="subject" value="" size="60"></td>
       </tr>
       <tr>
        <th>邮件内容</th>
        <td><textarea name="message" cols="80" rows="10"></textarea><br>邮件内容支持html代码</td>
       </tr>
      </table>
     </div>
     <div class="footactions">
      <input type="hidden" name=mpurl value="${mpurl}">
      <input type="submit" name="sendemailsubmit" value="发送邮件" class="submit">
     </div>
    </form>
   </c:when>
   <c:when test="${optype==5}">
    <form method="post" action="${mpurl}">
     <input type="hidden" name="formhash" value="${FORMHASH }" />
     <div class="bdrcontent">
      <div class="title">
       <h3>批量打招呼</h3>
       <p>您可以对选定的用户进行批量打招呼，以对其简单说明一些事情。注意，本操作将会增加服务器负载。</p>
      </div>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th style="width: 8em;">收件人(UID)</th>
        <td><input type="text" name="uids" value="${uids}" size="60"> 多个UID间用 "," 分隔</td>
       </tr>
       <tr>
        <th>招呼内容</th>
        <td><input type="text" name="note" value="" size="60"> （不要超过50个字符）</td>
       </tr>
      </table>
     </div>
     <div class="footactions">
      <input type="hidden" name="mpurl" value="${mpurl}">
      <input type="submit" name="pokesubmit" value="打招呼" class="submit">
     </div>
    </form>
   </c:when>
   <c:when test="${optype==7}">
    <form method="post" action="${mpurl}" onsubmit="return checkPresent()">
     <input type="hidden" name="formhash" value="${FORMHASH}"/>
     <div class="bdrcontent">
      <div class="title">
       <h3>批量赠送道具</h3>
       <p>您可以给选定的用户批量赠送道具。注意，本操作将会增加服务器负载。</p>
      </div>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th style="width: 8em;">受赠者(UID)</th>
        <td><input type="text" name="uids" value="${uids}" size="60"> 多个UID间用 "," 分隔</td>
       </tr>
       <tr>
        <th>赠送道具</th>
        <td>
         <select id="newmagicaward">
          <c:forEach items="${globalMagic}" var="magic">
           <option value="${magic.key}">${magic.value}</option>
          </c:forEach>
         </select>
         <input type="text" id="newmagicawardnum" value="1" />
         <input class="button" type="button" onclick="addMagicAward()" value="添加" />
         <ul id="magicawards"></ul>
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
           s += '<input type="hidden" name="magicaward" value="' + mid + ',' + num + '" />';
           s += name + ' &nbsp;&nbsp;' + "\n";
           s += num + ' &nbsp;&nbsp;' + "\n";
           s += '<a href="#" onclick="removeMagicAward(this);return false;">删除</a>';
           s += '</li>';
           $('magicawards').innerHTML += s;
          }
          function removeMagicAward(o) {
           $('magicawards').removeChild(o.parentNode);
          }
          function checkPresent(){
           if($('magicawards').getElementsByTagName("li").length) {
            return true;
           } else {
            alert('请至少选择一种道具并点击“添加”按钮');
            return false;
           }
          }
         </script>
        </td>
       </tr>
      </table>
     </div>
     <div class="footactions">
      <input type="hidden" name="mpurl" value="${mpurl}">
      <input type="submit" name="magicsubmit" value="赠送道具" class="submit">
     </div>
    </form>
   </c:when>
  </c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />