<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
 <div class="maininner">
  <form method="post" action="admincp.jsp?ac=cache">
   <input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
   <div class="bdrcontent">
    <table cellspacing="0" cellpadding="0" class="formtable">
     <tr>
      <th style="width: 8em;">选择类型</th>
      <td>
       <p><input type="checkbox" id="cachetype1" name="cachetype" value="block" checked /> <label for="cachetype1" style="font-weight: bold;">模块数据缓存</label></p>
       <p>
        站点开启模块数据缓存功能后，站点区块显示数据（如最新日志、最新相册等区块数据）都将进行缓存，以大大提高站点访问速度，降低服务器负载<br />
        通常，这些数据缓存会根据各自模块代码设置的更新间隔自动进行更新，不需要人工更新<br />
        如果你想立即更新所有的模块数据缓存，可以选择本选项<br />
        注意：清空数据缓存后，由于站点需要重新生成各个区块缓存，因此会短时间内对服务器带来很大负载，所以<strong>请选择访问人数较少的时段</strong>进行本操作
       </p>
       <br />
       <p><input type="checkbox" id="cachetype2" name="cachetype" value="database" checked /> <label for="cachetype2" style="font-weight: bold;">系统配置缓存</label></p>
       <p>系统配置缓存一般情况下都会在后台修改设定后自动更新，一般不需要手工更新。如果站点运行过程中出现错误，你可以尝试更新本缓存</p>
       <br />
       <p><input type="checkbox" id="cachetype3" name="cachetype" value="ad" checked /> <label for="cachetype3" style="font-weight: bold;">广告数据缓存</label></p>
       <p>包括系统内置广告和自定义广告的数据缓存。</p>
       <br />
       <p><input type="checkbox" id="cachetype4" name="cachetype" value="network" checked /> <label for="cachetype4" style="font-weight: bold;">其他缓存</label></p>
       <p>包括随便看看的数据缓存、用户排行榜的缓存、首页的数据缓存等。</p>
      </td>
     </tr>
    </table>
   </div>
   <div class="footactions">
    <input type="submit" name="cachesubmit" value="缓存更新" class="submit" />
   </div>
  </form>
 </div>
</div>
<div class="side">
 <jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />