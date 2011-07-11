<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <c:choose><c:when test="${empty showform}">
   <form method="post" action="admincp.jsp?ac=backup&op=export">
    <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
    <div class="bdrcontent">
     <div class="title"><h3>数据备份</h3></div>
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr>
       <th colspan="2"><input type="radio" name="type" value="jchomes" checked onclick="$('showtables').style.display='none'">JavaCenter Home 全部数据</th>
      </tr>
      <tr>
       <th><input type="radio" name="type" value="custom" onclick="$('showtables').style.display=''">自定义备份</th>
       <td>根据需要自行选择需要备份的数据表</td>
      </tr>
      <tr>
       <td colspan="2" align="right"><input type="checkbox" onclick="$('advanceoption').style.display=$('advanceoption').style.display == 'none' ? '' : 'none'; this.value = this.value == 1 ? 0 : 1; this.checked = this.value == 1 ? false : true" value="1">更多选项</td>
      </tr>
     </table>
     <div id="showtables" style="display: none">
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <td><input type="checkbox" name="chkall" onclick="checkAll(this.form, 'customtables')" checked>全选<strong>JavaCenter Home表</strong></td>
       </tr>
      </table>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr><c:forEach items="${jchome_tablelist}" var="table" varStatus="state">
         <td><input type="checkbox" name="customtables[]" value="${table.name}" checked>${table.name}</td>${state.count % 4 == 0?  "</tr><tr>" : ""}</c:forEach></tr>
      </table>
     </div>
     <div id="advanceoption" style="display: none">
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th>备份方式</th>
       </tr>
       <tr>
        <td><input type="radio" name="method" value="shell" onclick="if(${dbversion} < '4.1'){if(this.form.sqlcompat[2].checked==true) this.form.sqlcompat[0].checked=true; this.form.sqlcompat[2].disabled=true; this.form.sizelimit.disabled=true;} else {this.form.sqlcharset[0].checked=true; for(var i=1; i<=5; i++) {if(this.form.sqlcharset[i]) this.form.sqlcharset[i].disabled=true;}}">系统 MySQL Dump (Shell) 备份</td>
       </tr>
       <tr>
        <td><input type="radio" name="method" value="multivol" checked onclick="this.form.sqlcompat[2].disabled=false; this.form.sizelimit.disabled=false; for(var i=1; i<=5; i++) {if(this.form.sqlcharset[i]) this.form.sqlcharset[i].disabled=false;}">JavaCenter Home 分卷备份: 文件长度限制 <input type="text" size="5" value="2048" name="sizelimit"> KB</td>
       </tr>
      </table>
      <table cellspacing="0" cellpadding="0" class="formtable">
       <tr>
        <th colspan="2">备份选项</th>
       </tr>
       <tr>
        <th>使用扩展插入</th>
        <td><input type="radio" value="1" name="extendins" checked>是 <input type="radio" value="0" name="extendins" checked>否</td>
       </tr>
       <tr>
        <th>建表语句格式</th>
        <td><input type="radio" value="" name="sqlcompat" checked>默认(MySQL${dbversion}) <input type="radio" value="MYSQL40" name="sqlcompat">MySQL 3.23/4.0.x <input type="radio" value="MYSQL41" name="sqlcompat">MySQL 4.1.x/5.x</td>
       </tr>
       <tr>
        <th>强制字符集</th>
        <td>
         <input class="radio" type="radio" name="sqlcharset" value="" checked>默认(${dbCharset})&nbsp;
         <c:if test="${not empty dbCharset}"><input class="radio" type="radio" name="sqlcharset" value="${dbCharset}">${dbCharset}&nbsp;</c:if>
         <c:if test="${dbversion > 4.1 && dbCharset != 'utf8'}"><input class="radio" type="radio" name="sqlcharset" value="utf8">utf-8</c:if>
        </td>
       </tr>
       <tr>
        <th>十六进制方式</th>
        <td><input type="radio" value="1" name="usehex" checked>是 <input type="radio" value="0" name="usehex" checked>否</td>
       </tr>
       <tr>
        <th>压缩备份文件</th>
        <td><input type="radio" value="1" name="usezip">多分卷压缩成一个文件 <input type="radio" value="2" name="usezip">每个分卷压缩成单独文件 <input type="radio" value="0" name="usezip" checked>不压缩</td>
       </tr>
       <tr>
        <th>备份文件名</th>
        <td><input type="text" size="40" value="${filename}" name="filename">.sql</td>
       </tr>
      </table>
     </div>
    </div>
    <div class="footactions">
     <input type="hidden" name="setup" value="1">
     <input type="submit" value="提 交" class="submit">
    </div>
   </form>
   <br />
   <form method="post" action="admincp.jsp?ac=backup" name="thevalueform">
    <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}"/>
    <div class="bdrcontent">
     <div class="title">
      <h3>数据恢复</h3>
     </div>
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr>
       <td>服务器备份文件名: ./data/<input type="text" name="datafile" value="${backupdir}/" size="50"></td>
      </tr>
     </table>
    </div>
    <div class="footactions">
     <input type="submit" name="importsubmit" value="提交" class="submit">
    </div>
   </form>
   <br />
   <form method="post" action="admincp.jsp?ac=backup" name="thevalueform">
    <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}"/>
    <div class="bdrcontent">
     <div class="title">
      <h3>数据备份记录</h3>
     </div>
     <table cellspacing="0" cellpadding="0" class="formtable">
      <tr>
       <th width="4%">&nbsp;</th>
       <th>文件名</th>
       <th width="15%">版本</th>
       <th width="15%">类型</th>
       <th width="10%">操作</th>
      </tr>
      <c:forEach items="${exportlog}" var="m"><tr>
       <td><input type="checkbox" name="delexport[]" value="${m.filename}"></td>
       <td><a href="./data/${m.filename}">${m.basename}</a> (${m.size})<br />${m.dateline} <c:choose><c:when test="${m.method == 'multivol' && m.type != 'zip'}">多卷</c:when><c:when test="${m.method == 'multivol'}">SHELL</c:when><c:otherwise>压缩</c:otherwise></c:choose> <c:if test="${not empty m.volume}">(${m.volume})</c:if></td>
       <td>${m.version}</td>
       <td>${m.type == 'custom' ? "自定义备份" : "全部备份"}</td>
       <td><c:choose><c:when test="${m.type == 'zip'}"><a href="admincp.jsp?ac=backup&op=import&do=zip&datafile=${m.filename}">[解压]</a></c:when><c:when test="${m.version != JCH_VERSION}"><a href="admincp.jsp?ac=backup&op=import&do=import&datafile=${m.filename}" onclick="return confirm('确定导入?');">[导入]</a></c:when><c:otherwise><a href="admincp.jsp?ac=backup&op=import&do=import&datafile=${m.filename}">[导入]</a></c:otherwise></c:choose></td>
      </tr></c:forEach>
     </table>
    </div>
    <div class="footactions">
     <input type="checkbox" name="chkall" onclick="checkAll(this.form, 'delexport')">全选
     <input type="submit" name="delexportsubmit" value="批量删除" class="submit">
    </div>
   </form>
  </c:when><c:when test="${showform == 1}">
   <form method="get" action="admincp.jsp" name="thevalueform">
    <div class="bdrcontent">
     <div class="title">
      <h3>导入确认</h3>
     </div>
     <p>${baseName}<br />导入版本:${identify[1]}<br />导入类型:${identify[2] == 'custom' ? '自定义备份' : '全部备份'}<br />方式:${identify[3] == 'multivol' ? '多卷' : 'Shell'}<br /><br />确定导入吗?</p>
    </div>
    <div class="footactions">
     <input type="hidden" name="ac" value="backup">
     <input type="hidden" name="op" value="import">
     <input type="hidden" name="do" value="zip">
     <input type="hidden" name="datafile" value="${dataFile}">
     <input type="hidden" name="confirm" value="yes">
     <input type="submit" name="confirmed" value="继续" class="submit">
     <input type="button" value="返回" onClick="location.href='admincp.jsp?ac=backup'">
    </div>
   </form>
  </c:when><c:when test="${showform == 2}">
   <form method="get" action="admincp.jsp" name="thevalueform">
    <div class="bdrcontent">
     <div class="title">
      <h3>自动导入确认</h3>
     </div>
     <p>所有分卷文件解压缩完毕，您需要自动导入备份吗？导入后解压缩的文件将会被删除。</p>
    </div>
    <div class="footactions">
     <input type="hidden" name="ac" value="backup">
     <input type="hidden" name="op" value="import">
     <input type="hidden" name="do" value="import">
     <input type="hidden" name="datafile" value="${datafile_vol1}">
     <input type="hidden" name="delunzip" value="yes">
     <input type="submit" name="confirmed" value="继续" class="submit">
     <input type="button" value="返回" onClick="location.href='admincp.jsp?ac=backup'">
    </div>
   </form>
  </c:when><c:when test="${showform == 3}">
   <form method="get" action="admincp.jsp" name="thevalueform">
    <div class="bdrcontent">
     <div class="title">
      <h3>继续解压确认</h3>
     </div>
     <p>${baseName}<br />备份文件解压缩完毕，您需要自动解压缩其他的分卷文件吗？</p>
    </div>
    <div class="footactions">
     <input type="hidden" name="ac" value="backup">
     <input type="hidden" name="op" value="import">
     <input type="hidden" name="do" value="zip">
     <input type="hidden" name="multivol" value="1">
     <input type="hidden" name="datafile" value="${datafile}">
     <input type="hidden" name="datafile_vol1" value="${datafile_vol1}">
     <input type="hidden" name="confirm" value="yes">
     <input type="submit" name="confirmed" value="继续" class="submit">
     <input type="button" value="返回" onClick="location.href='admincp.jsp?ac=backup'" class="submit">
    </div>
   </form>
  </c:when><c:when test="${showform == 4}">
   <form method="get" action="admincp.jsp" name="thevalueform">
    <div class="bdrcontent">
     <div class="title">
      <h3>自动导入备份确认</h3>
     </div>
     <p>${baseName}<br />导入版本:${identify[1]}<br />导入类型:${identify[2] == 'custom' ? '自定义备份' : '全部备份'}<br />方式:${identify[3] == 'multivol' ? '多卷' : 'Shell'}<br /><br />备份文件解压缩完毕，您需要自动导入备份吗？导入后解压缩的文件将会被删除</p>
    </div>
    <div class="footactions">
     <input type="hidden" name="ac" value="backup">
     <input type="hidden" name="op" value="import">
     <input type="hidden" name="do" value="import">
     <input type="hidden" name="datafile" value="${backupdir}/${importfile}">
     <input type="hidden" name="delunzip" value="yes">
     <input type="submit" name="confirmed" value="继续" class="submit">
     <input type="button" value="返回" onClick="location.href='admincp.jsp?ac=backup'" class="submit">
    </div>
   </form>
  </c:when><c:when test="${showform == 5}">
   <form method="get" action="admincp.jsp" name="thevalueform">
    <div class="bdrcontent">
     <div class="title">
      <h3>自动导入备份确认</h3>
     </div>
     <p>分卷数据成功导入数据库，您需要自动导入本次其他的备份吗？</p>
    </div>
    <div class="footactions">
     <input type="hidden" name="ac" value="backup">
     <input type="hidden" name="op" value="import">
     <input type="hidden" name="do" value="import">
     <input type="hidden" name="datafile" value="${datafile_next}">
     <input type="hidden" name="autoimport" value="yes">
     <c:if test="${param.delunzip != null}"><input type="hidden" name="delunzip" value="yes"></c:if>
     <input type="submit" name="confirmed" value="继续" class="submit">
     <input type="button" value="返回" onClick="location.href='admincp.jsp?ac=backup'" class="submit">
    </div>
   </form>
  </c:when></c:choose>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />