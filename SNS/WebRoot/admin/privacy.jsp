<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp?ac=privacy">
   <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}">
   <div class="bdrcontent">
    <div class="title">
     <h3>系统开放程度</h3>
     <p>可以根据自己的需求，决定系统的内容开放程度。</p>
    </div>
    <table class="formtable">
     <tr>
      <th style="width: 12em;">游客开放浏览</th>
      <td>
       <input type="radio" name="config[networkpublic]" value="1"${networkpublic1}>是
       <input type="radio" name="config[networkpublic]" value="0"${networkpublic0}>否
       <br>用户不需要登录也能浏览随便看看、未设置隐私的个人空间，同时，站内的信息也可以被搜索引擎收录。
      </td>
     </tr>
    </table><br>
    <div class="title">
     <h3>新用户默认隐私设置</h3>
     <p>新用户将采用站内的默认隐私设置。用户可以修改自己的隐私设置，以下设置只对其他用户访问个人主页生效</p>
    </div>
    <table class="formtable">
     <tr>
      <th style="width: 6em;">空间首页</th>
      <td>
       <select name="view[index]">
        <option value="0"${view_index0}>全站用户可见</option>
        <option value="1"${view_index1}>仅好友可见</option>
        <option value="2"${view_index2}>仅自己可见</option>
       </select>
      </td>
      <th style="width: 6em;">好友列表</th>
      <td>
       <select name="view[friend]">
        <option value="0"${view_friend0}>全站用户可见</option>
        <option value="1"${view_friend1}>仅好友可见</option>
        <option value="2"${view_friend2}>仅自己可见</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>留言板</th>
      <td>
       <select name="view[wall]">
        <option value="0"${view_wall0}>全站用户可见</option>
        <option value="1"${view_wall1}>仅好友可见</option>
        <option value="2"${view_wall2}>仅自己可见</option>
       </select>
      </td>
      <th>个人动态</th>
      <td>
       <select name="view[feed]">
        <option value="0"${view_feed0}>全站用户可见</option>
        <option value="1"${view_feed1}>仅好友可见</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>群组</th>
      <td>
       <select name="view[mtag]">
        <option value="0"${view_mtag0}>全站用户可见</option>
        <option value="1"${view_mtag1}>仅好友可见</option>
        <option value="2"${view_mtag2}>仅自己可见</option>
       </select>
      </td>
      <th>活动</th>
      <td>
       <select name="view[event]">
        <option value="0"${view_event0}>全站用户可见</option>
        <option value="1"${view_event1}>仅好友可见</option>
       </select>
      </td>
     </tr>
     <tr>
      <th>记录</th>
      <td>
       <select name="view[doing]">
        <option value="0"${view_doing0}>全站用户可见</option>
        <option value="1"${view_doing1}>仅好友可见</option>
       </select>
       <br />在全站的记录列表中可能会出现记录信息。
      </td>
      <th>日志</th>
      <td>
       <select name="view[blog]">
        <option value="0"${view_blog0}>全站用户可见</option>
        <option value="1"${view_blog1}>仅好友可见</option>
       </select>
       <br />相关浏览权限需要在每篇日志中单独设置方可完全生效。
      </td>
     </tr>
     <tr>
      <th>相册</th>
      <td>
       <select name="view[album]">
        <option value="0"${view_album0}>全站用户可见</option>
        <option value="1"${view_album1}>仅好友可见</option>
       </select>
       <br />相关浏览权限需要在每个相册中单独设置方可完全生效。
      </td>
      <th>分享</th>
      <td>
       <select name="view[share]">
        <option value="0"${view_share0}>全站用户可见</option>
        <option value="1"${view_share1}>仅好友可见</option>
       </select>
       <br />在全站的分享列表中可能会出现分享信息。
      </td>
     </tr>
     <tr>
      <th>投票</th>
      <td>
       <select name="view[poll]">
        <option value="0"${view_poll0}>全站用户可见</option>
        <option value="1"${view_poll1}>仅好友可见</option>
       </select>
       <br />在全站的投票列表中可能会出现投票信息。
      </td>
      <th>礼物</th>
      <td>
       <select name="view[gift]">
        <option value="0"${view_gift0}>全站用户可见</option>
        <option value="1"${view_gift1}>仅好友可见</option>
        <option value="2"${view_gift2}>仅自己可见</option>
       </select>
      </td>
     </tr>
    </table><br>
    <div class="title">
     <h3>默认动态发布设置</h3>
     <p>设置系统默认将哪些动作发布到个人动态里面。用户可以修改这些默认设置。</p>
    </div>
    <table class="formtable">
     <tr>
      <td><input type="checkbox" name="feed[doing]" value="1"${feed_doing}>记录</td>
      <td><input type="checkbox" name="feed[blog]" value="1"${feed_blog}>撰写日志</td>
      <td><input type="checkbox" name="feed[upload]" value="1"${feed_upload}>上传图片</td>
      <td><input type="checkbox" name="feed[share]" value="1"${feed_share}>添加分享</td>
     </tr>
     <tr>
      <td><input type="checkbox" name="feed[poll]" value="1"${feed_poll}>发起投票</td>
      <td><input type="checkbox" name="feed[joinpoll]" value="1"${feed_joinpoll}>参与投票</td>
      <td><input type="checkbox" name="feed[thread]" value="1"${feed_thread}>发起话题</td>
      <td><input type="checkbox" name="feed[post]" value="1"${feed_post}>对话题回复</td>
     </tr>
     <tr>
      <td><input type="checkbox" name="feed[mtag]" value="1"${feed_mtag}>加入群组</td>
      <td><input type="checkbox" name="feed[event]" value="1"${feed_event}>组织活动</td>
      <td><input type="checkbox" name="feed[join]" value="1"${feed_join}>参加活动</td>
      <td><input type="checkbox" name="feed[friend]" value="1"${feed_friend}>添加好友</td>
     </tr>
     <tr>
      <td><input type="checkbox" name="feed[comment]" value="1"${feed_comment}>发表评论/留言</td>
      <td><input type="checkbox" name="feed[show]" value="1"${feed_show}>竞价排名</td>
      <td><input type="checkbox" name="feed[credit]" value="1"${feed_credit}>积分消费</td>
      <td><input type="checkbox" name="feed[spaceopen]" value="1"${feed_spaceopen}>新空间开通</td>
     </tr>
     <tr>
      <td><input type="checkbox" name="feed[invite]" value="1"${feed_invite}>邀请好友</td>
      <td><input type="checkbox" name="feed[task]" value="1"${feed_task}>完成任务</td>
      <td><input type="checkbox" name="feed[profile]" value="1"${feed_profile}>更新个人资料</td>
      <td><input type="checkbox" name="feed[click]" value="1"${feed_click}>对日志/图片/话题表态</td>
     </tr>
    </table>
   </div>
   <div class="footactions"><input type="submit" name="thevaluesubmit" value="提交" class="submit"></div>
  </form>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />