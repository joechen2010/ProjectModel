<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <div class="tabs_header">
   <ul class="tabs">
    <li${actives_view}><a href="admincp.jsp?ac=profield"><span>浏览栏目</span></a></li>
    <li class="null"><a href="admincp.jsp?ac=profield&op=add">添加新群组栏目</a></li>
   </ul>
  </div>
  <c:choose>
   <c:when test="${param.op == 'delete'}">
    <form method="post" action="admincp.jsp?ac=profield&op=delete&fieldid=${param.fieldid}">
     <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
     <div class="bdrcontent">
      <div class="topactions">该栏目删除后，请选择该栏目下面已有的群组会归类到那个新栏目。</div>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th width="150">栏目下的群组新归类</th>
        <td>
         <select name="newfieldid">
          <c:forEach items="${globalProfield}" var="profield">
           <option value="${profield.value.fieldid}">${profield.value.title}</option>
          </c:forEach>
         </select>
        </td>
       </tr>
      </table>
     </div>
     <div class="footactions"><input type="submit" name="deletesubmit" value="确定删除" class="submit"></div>
    </form>
   </c:when>
   <c:otherwise>
    <c:choose>
     <c:when test="${empty profield}">
      <form method="post" action="admincp.jsp?ac=profield">
       <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
       <div class="bdrcontent">
        <table cellspacing="0" cellpadding="0" class="formtable">
         <tr><th>栏目</th><th>填写类型</th><th>显示顺序</th><th width="80">操作</th></tr>
         <c:forEach items="${profields}" var="profield">
          <tr>
           <th>${profield.title}</th>
           <td>${formTypes[profield.formtype]}</td>
           <td>
            <input type="hidden" name="fieldid[]" value="${profield.fieldid}">
            <input type="text" name="displayorder_${profield.fieldid}" value="${profield.displayorder}" size="5">
           </td>
           <td width="80">
            <a href="admincp.jsp?ac=profield&op=edit&fieldid=${profield.fieldid}">编辑</a> |
            <a href="admincp.jsp?ac=profield&op=delete&fieldid=${profield.fieldid}">删除</a>
           </td>
          </tr>
         </c:forEach>
        </table>
       </div>
       <div class="footactions"><input type="submit" name="ordersubmit" value="更新排序" class="submit"></div>
      </form>
     </c:when>
     <c:otherwise>
      <form method="post" action="admincp.jsp?ac=profield&fieldid=${profield.fieldid}">
       <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
       <div class="bdrcontent">
        <script language="javascript">
         function formtypeShow(value) {
          if(value != 'select') {
           $('tb_inputnum').style.display = '';
           $('tb_choice').style.display = (value=='text'?'none':'');
          } else {
           $('tb_inputnum').style.display = 'none';
           $('tb_choice').style.display = '';
          }
         }
        </script>
        <table cellspacing="0" cellpadding="0" class="formtable">
         <tr>
          <th style="width: 15em;">栏目名称</th>
          <td><input type="text" name="title" value="${profield.title}"></td>
         </tr>
         <tr>
          <th>简单介绍</th>
          <td><input type="text" name="note" value="${profield.note}" size="60"></td>
         </tr>
         <tr>
          <th>群组添加表单类型</th>
          <td>
           <select name="formtype" onchange="formtypeShow(this.value)">
           <c:forEach items="${formTypes}" var="formType">
            <option value="${formType.key}"${formType.key == profield.formtype ? " selected" : ""}>${formType.value}框</option>
           </c:forEach>
          </td>
         </tr>
         <tbody id="tb_inputnum"${profield.formtype == 'select' ? " style='display: none;'" : ""}>
          <tr>
           <th>用户可加入群组最多个数</th>
           <td><input type="text" name="inputnum" value="${profield.inputnum}" size="5"></td>
          </tr>
         </tbody>
         <tbody id="tb_choice"${profield.formtype == 'text' ? " style='display: none;'" : ""}>
          <tr>
           <th>可选值</th>
           <td>
            <textarea name="choice" rows="5" cols="20">${profield.choice}</textarea>
            <br />每行一个值，例如输入:<br />北京<br />上海
           </td>
          </tr>
         </tbody>
         <tr>
          <th>群组讨论区人数下限</th>
          <td><input type="text" name="mtagminnum" value="${profield.mtagminnum}" size="5"> 当群组的成员数达到该数目时，才允许成员在群组内发话题和回帖。</td>
         </tr>
         <tr>
          <th>群组群主手工指定</th>
          <td>
           <input type="radio" name="manualmoderator" value="1"${profield.manualmoderator==1?" checked" : ""}>手工
           <input type="radio" name="manualmoderator" value="0"${profield.manualmoderator !=1?" checked" : ""}>自动
           <br>如果选择不手工指定，则系统会自动将第一次使用某个群组的用户作为群主。
          </td>
         </tr>
         <tr>
          <th>群组成员可由群主控制</th>
          <td>
           <input type="radio" name="manualmember" value="1"${profield.manualmember==1?" checked" : ""}>群主可控制
           <input type="radio" name="manualmember" value="0"${profield.manualmember !=1?" checked" : ""}>会员可自由加入
           <br>群主可控制，则允许群主有权设置群组的会员加入方式，来控制加入群组的会员。
          </td>
         </tr>
         <tr>
          <th>显示顺序</th>
          <td><input type="text" name="displayorder" value="${profield.displayorder}" size="5"></td>
         </tr>
        </table>
       </div>
       <div class="footactions"><input type="submit" name="fieldsubmit" value="提交" class="submit"></div>
      </form>
     </c:otherwise>
    </c:choose>
   </c:otherwise>
  </c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />