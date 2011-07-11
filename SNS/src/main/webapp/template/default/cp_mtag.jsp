<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<c:choose>
	<c:when test="${op=='manage'}">
		<c:if test="${subop!='member'}">
			<h2 class="title"><img src="image/app/mtag.gif" />群组 - ${mtag.tagname}</h2>
			<div class="tabs_header">
				<ul class="tabs">
					<c:if test="${mtag.grade >= 8}"><li${active_base}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=base"><span>基本设置</span></a></li></c:if>
					<c:if test="${mtag.allowinvite==1}"><li${active_invite}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=invite"><span>邀请好友</span></a></li></c:if>
					<c:if test="${mtag.grade >= 8}">
						<li${active_members}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members"><span>成员管理</span></a></li>
						<li${active_thread}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=thread"><span>话题批量管理</span></a></li>
					</c:if>
					<li><a href="space.jsp?do=mtag&tagid=${mtag.tagid}"><span>返回群组首页</span></a></li>
				</ul>
				<c:if test="${(!empty actives.members || mtag.allowinvite==1) && (subop == 'invite' || subop == 'members')}">
					<script>
						function searchUser() {
							$('searchform').submit();
						}
					</script>
					<form name="searchform" id="searchform" method="get" action="cp.jsp">
						<div style="float: right; margin: 0 6px 5px 0;">
							<table cellspacing="0" cellpadding="0">
								<tr>
									<td style="padding: 0;"><input type="text" id="key" name="key" value="搜索成员" onfocus="if(this.value=='搜索成员')this.value='';" class="t_input" tabindex="1" style="width: 160px; border-right: none;" /></td>
									<td style="padding: 0;"><a href="javascript:searchUser();"><img src="image/search_btn.gif" alt="搜索" /></a></td>
								</tr>
							</table>
						</div>
						<input type="hidden" name="ac" value="mtag">
						<input type="hidden" name="op" value="manage">
						<input type="hidden" name="tagid" value="${mtag.tagid}">
						<input type="hidden" name="subop" value="${subop}">
						<input type="hidden" name="uid" value="${param.uid}">
						<input type="hidden" name="grade" value="${grade}">
						<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					</form>
				</c:if>
			</div>
		</c:if>
		<form id="manageform" name="manageform" method="post" action="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=${subop}&uid=${param.uid}&grade=${grade}&group=${gid}&page=${page}&start=${start}">
			<c:choose>
				<c:when test="${subop=='base'}">
					<table cellspacing="4" cellpadding="4" class="formtable">
						<tr>
							<th width="100">群组名</th>
							<td>${mtag.tagname}</td>
						</tr>
						<tr>
							<th width="100"><label for="pic">封面图片</label></th>
							<td><input id="pic" type="text" name="pic" value="${mtag.pic}" size="80" class="t_input" /><br />请输入 http:// 开头的图片URL地址</td>
						</tr>
						<tr>
							<th><label for="announcement">群组公告</label></th>
							<td><textarea id="announcement" name="announcement" cols="80" rows="5">${mtag.announcement}</textarea><p class="gray">最多可以输入 <strong>5000</strong> 个字符,多出的部分将被自动删除</p></td>
						</tr>
						<c:if test="${mtag.grade==9}">
							<c:choose>
								<c:when test="${field.manualmember>0}">
									<tr>
										<th width="100">加入权限</th>
										<td>
											<select name="joinperm">
												<option value="0"${joinPerm_0}>公开(允许所有人可加入)</option>
												<option value="1"${joinPerm_1}>审核(需要经批准后才可加入)</option>
												<option value="2"${joinPerm_2}>私密(只允许群主邀请加入)</option>
											</select>
										</td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr>
										<th width="100">加入权限</th>
										<td>公开(允许所有人可加入)</td>
									</tr>
								</c:otherwise>
							</c:choose>
							<tr>
								<th width="100">浏览权限</th>
								<td>
									<select name="viewperm">
										<option value="0"${viewPerm_0}>公开(所有人可浏览)</option>
										<option value="1"${viewPerm_1}>封闭(只对会员可见)</option>
									</select>
									<br>为了保护隐私，当群组的浏览权限设置为封闭时，成员的发帖或回帖操作将不产生个人动态。
								</td>
							</tr>
							<tr>
								<th width="100">发新话题权限</th>
								<td>
									<select name="threadperm">
										<option value="0"${threadPerm_0}>需成为会员才可发话题</option>
										<option value="1"${threadPerm_1}>所有人可发话题</option>
									</select>
								</td>
							</tr>
							<tr>
								<th width="100">回帖权限</th>
								<td>
									<select name="postperm">
										<option value="0"${postPerm_0}>需成为会员才可回帖</option>
										<option value="1"${postPerm_1}>所有人可回帖</option>
									</select>
								</td>
							</tr>
							<tr>
								<th>招纳群主</th>
								<td>
									<input type="radio" name="closeapply" value="0"${closeApply_0}/> 开启
									<input type="radio" name="closeapply" value="1"${closeApply_1}/> 关闭
								</td>
							</tr>
						</c:if>
						<tr>
							<th>&nbsp;</th>
							<td><input type="submit" name="basesubmit" value="提交保存" class="submit" />&nbsp;</td>
						</tr>
					</table>
				</c:when>
				<c:when test="${subop=='thread'}">
					<table cellspacing="0" cellpadding="0" class="formtable">
						<tr>
							<td>
								请登录管理平台，来对群组的话题、回帖进行批量删除、精华、置顶等操作。<br><br>
								<a href="admincp.jsp?ac=thread&perpage=20&tagid=${mtag.tagid}&searchsubmit=1" class="submit">话题管理</a> &nbsp;
								<a href="admincp.jsp?ac=post&perpage=20&tagid=${mtag.tagid}&searchsubmit=1" class="submit">回帖管理</a>
							</td>
						</tr>
					</table>
				</c:when>
				<c:when test="${subop=='invite'}">
					<div id="content" style="width: 640px;">
						<div class="h_status">您可以给未加入本群组的好友们发送邀请。</div>
						<c:choose>
							<c:when test="${empty friends}"><div class="c_form">还没有好友。</div></c:when>
							<c:otherwise>
								<div class="h_status">
									<ul class="avatar_list">
										<c:forEach items="${friends}" var="friend"><li>
											<div class="avatar48"><a href="space.jsp?uid=${friend.fuid}" title="${sNames[friend.fuid]}">${jch:avatar1(friend.fuid,sGlobal,sConfig)}</a></div>
											<p><a href="space.jsp?uid=${friend.fuid}" title="${sNames[friend.fuid]}">${sNames[friend.fuid]}</a></p>
											<p><c:choose><c:when test="${empty joins[friend.fuid]}"><input type="checkbox" name="ids" value="${friend.fuid}">选定</c:when><c:otherwise>已邀请</c:otherwise></c:choose></p>
										</li></c:forEach>
									</ul>
									<div class="page">${multi}</div>
								</div>
								<p>
									<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选 &nbsp;
									<input type="submit" name="invitesubmit" value="邀请" class="submit" />
								</p>
							</c:otherwise>
						</c:choose>
					</div>
					<div id="sidebar" style="width: 150px;">
						<div class="cat">
							<h3>好友分类</h3>
							<ul class="post_list line_list">
								<li${gid==-1 ? " class='current'" : ""}><a href="cp.jsp?ac=mtag&tagid=${mtag.tagid}&op=manage&subop=invite&group=-1">全部好友</a></li>
								<c:forEach items="${groups}" var="group">
									<li${gid==group.key ? " class='current'" : ""}><a href="cp.jsp?ac=mtag&tagid=${mtag.tagid}&op=manage&subop=invite&group=${group.key}">${group.value}</a></li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</c:when>
				<c:when test="${subop=='members'}">
					<div id="content" style="width: 640px;">
						<div class="h_status">选择相应的用户进行用户等级变更。</div>
						<div class="h_status">
							<c:choose>
								<c:when test="${empty tagSpaces}"><div class="c_form">还没有相关成员。</div></c:when>
								<c:otherwise>
									<ul class="avatar_list">
										<c:forEach items="${tagSpaces}" var="tagSpace"><li>
											<div class="avatar48"><a href="space.jsp?uid=${tagSpace.uid}" target="_blank">${jch:avatar1(tagSpace.uid,sGlobal,sConfig)}</a></div>
											<p><a href="space.jsp?uid=${tagSpace.uid}">${sNames[tagSpace.uid]}</a></p>
											<p><input type="checkbox" name="ids" value="${tagSpace.uid}">选定</p>
										</li>
										</c:forEach>
									</ul>
									<div class="page">${multi}</div>
								</c:otherwise>
							</c:choose>
						</div>
						<p>
							<input type="checkbox" id="chkall" name="chkall" onclick="checkAll(this.form, 'ids')">全选 &nbsp; 设为：
							<select name="newGrade">
								<c:if test="${mtag.grade==9}">
									<option value="9">主群主</option>
									<option value="8">副群主</option>
								</c:if>
								<option value="1">明星成员</option>
								<option value="0">普通成员</option>
								<option value="-1">禁止发言</option>
								<option value="-2">待审核成员</option>
								<option value="-9">踢出群组</option>
							</select> &nbsp;
							<input type="submit" name="memberssubmit" value="提交" class="submit" />
						</p>
					</div>
					<div id="sidebar" style="width: 150px;">
						<div class="cat">
							<h3>成员级别</h3>
							<ul class="post_list line_list">
								<li${grade==-2 ? " class='current'" : ""}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members&grade=-2">待审核</a></li>
								<li${grade==0 ? " class='current'" : ""}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members&grade=0">普通成员</a></li>
								<li${grade==9 ? " class='current'" : ""}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members&grade=9">群主</a></li>
								<li${grade==8 ? " class='current'" : ""}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members&grade=8">副群主</a></li>
								<li${grade==1 ? " class='current'" : ""}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members&grade=1">明星成员</a></li>
								<li${grade==-1 ? " class='current'" : ""}><a href="cp.jsp?ac=mtag&op=manage&tagid=${mtag.tagid}&subop=members&grade=-1">禁言成员</a></li>
							</ul>
						</div>
					</div>
				</c:when>
				<c:when test="${subop=='member'}">
					<h1>管理成员</h1>
					<a href="javascript:hideMenu();" title="关闭" class="float_del">关闭</a>
					<div id="${param.uid}" class="popupmenu_inner">
						<p>选择一个新身份：</p>
						<p>
							<select name="newGrade">
								<option value="9"${grade9}>主群主</option>
								<option value="8"${grade8}>副群主</option>
								<option value="1"${grade1}>明星成员</option>
								<option value="0"${grade0}>普通成员</option>
								<option value="-1"${grade_1}>禁止发言</option>
								<option value="-2"${grade_2}>待审核成员</option>
								<option value="-9">踢出群组</option>
							</select>
						</p>
						<p class="btn_line">
							<input type="hidden" name="refer" value="${sGlobal.refer}">
							<button name="membersubmit" type="submit" class="submit" value="true">确定</button>
						</p>
					</div>
				</c:when>
			</c:choose>
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
		</form>
	</c:when>
	<c:when test="${op=='mtaginvite'}">
		<h2 class="title"><img src="image/app/mtag.gif" />群组邀请</h2>
		<div class="tabs_header">
			<ul class="tabs">
				<li class="active"><a href="cp.jsp?ac=mtag&op=mtaginvite"><span>群组邀请</span></a></li>
				<li><a href="space.jsp?do=mtag&view=me"><span>返回我的群组</span></a></li>
			</ul>
		</div>
		<div class="h_status">您的好友邀请您加入以下群组<c:if test="${!empty invites}"> <span class="pipe">|</span> <a href="cp.jsp?ac=mtag&op=inviteconfirm&tagid=0&r=0"><span>忽略所有邀请</span></a></c:if></div>
		<div class="c_form">
			<c:choose>
				<c:when test="${empty invites}">暂时没有新的群组邀请。</c:when>
				<c:otherwise>
					<table cellspacing="4" cellpadding="4" class="formtable">
					<c:forEach items="${invites}" var="invite"><tr>
						<td width="80"><div class="threadimg60"><a href="space.jsp?do=mtag&tagid=${invite.tagid}" target="_blank"><img src="${invite.pic}" width="60"></a></div></td>
						<td class="h_status">
							<p><a href="space.jsp?do=mtag&tagid=${invite.tagid}" target="_blank" style="font-size: 14px; font-weight: bold;">${invite.tagname}</a></p>
							<div id="tag_${invite.tagid}" style="padding: 0.5em 0 0.5em 0;">
								<p>已有 ${invite.membernum} 人<c:if test="${invite.viewperm>0}">, 只对会员开放浏览</c:if>, 所属分类: ${invite.title}</p>
								邀请好友：<a href="space.jsp?uid=${invite.fromuid}" target="_blank">${sNames[invite.fromuid]}</a><br>
								邀请时间：${invite.dateline}
								<p style="padding: 1em 0 0 0;">
									<a href="cp.jsp?ac=mtag&op=inviteconfirm&tagid=${invite.tagid}&r=1" class="submit" onclick="ajaxget(this.href, 'tag_${invite.tagid}');return false;">确认邀请</a>
									<a href="cp.jsp?ac=mtag&op=inviteconfirm&tagid=${invite.tagid}&r=0" class="button" onclick="ajaxget(this.href, 'tag_${invite.tagid}');return false;">忽略</a>
								</p>
							</div>
						</td>
					</tr></c:forEach>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:when test="${op=='join'}">
		<h1>加入群组</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner" id="__joinform_${tagId}">
			<form id="joinform_${tagId}" name="joinform_${tagId}" method="post" action="cp.jsp?ac=mtag&op=join&tagid=${tagId}">
				<p>确定加入该群组吗？</p>
				<p class="btn_line">
					<input type="hidden" name="refer" value="${sGlobal.refer}" />
					<input type="submit" name="joinsubmit" value="加入" class="submit" />
				</p>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
		</div>
	</c:when>
	<c:when test="${op=='out'}">
		<h1>退出群组</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner" id="__outform">
			<form id="outform" name="outform" method="post" action="cp.jsp?ac=mtag&op=out&tagid=${tagId}">
				<p>确定要退出该群组吗？</p>
				<p class="btn_line">
					<input type="hidden" name="refer" value="${sGlobal.refer}" />
					<input type="submit" name="outsubmit" value="退出" class="submit" />
				</p>
				<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
			</form>
		</div>
	</c:when>
	<c:when test="${op=='apply'}">
		<h1>申请群主</h1>
		<a href="javascript:hideMenu();" class="float_del" title="关闭">关闭</a>
		<div class="popupmenu_inner" id="__pmapplyform_${tagId}">
			<form id="pmapplyform_${tagId}" name="pmapplyform_${tagId}" method="post" action="cp.jsp?ac=mtag&op=apply&tagid=${tagId}">
				<table cellspacing="0" cellpadding="3">
					<tr><td>请输入您申请群主的理由，您的申请会以短消息的方式发送给群主管理员。</td></tr>
					<tr><td><textarea id="message" name="message" cols="40" rows="4" tabindex="3" style="width: 400px; height: 150px;" onkeydown="ctrlEnter(event, 'pmsubmit_btn');"></textarea></td></tr>
					<tr>
						<td>
							<input type="hidden" name="refer" value="${sGlobal.refer}" />
							<input type="hidden" name="pmsubmit" value="true" />
							<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
							<c:choose>
								<c:when test="${sGlobal.inajax==1}"><input type="button" name="pmsubmit_btn" id="pmsubmit_btn" value="申请" class="submit" onclick="ajaxpost('pmapplyform_${tagId}','',2000)" /></c:when>
								<c:otherwise><input type="submit" name="pmsubmit_btn" id="pmsubmit_btn" value="申请" class="submit" /></c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</c:when>
	<c:otherwise>
		<h2 class="title"><img src="image/app/mtag.gif" />群组</h2>
		<div class="tabs_header">
			<ul class="tabs">
				<li class="active"><a href="cp.jsp?ac=mtag"><span>创建新群组</span></a></li>
				<li><a href="space.jsp?do=mtag&view=me"><span>返回我的群组</span></a></li>
			</ul>
		</div>
		<c:choose>
			<c:when test="${op=='multiresult'}">
				<div class="c_form">
					<table cellspacing="0" cellpadding="0" class="formtable">
						<caption>
							<h2>您已经成功加入以下群组</h2>
							<p>现在，您就可以立即访问以下群组，与志同道合的朋友一起交流话题了。</p>
						</caption>
						<c:forEach items="${mtags}" var="mtag"><tr>
							<td><a href="space.jsp?do=mtag&tagid=${mtag.tagid}" target="_blank">${mtag.tagname}</a></td>
							<td>${mtag.title}</td>
							<td>${mtag.membernum} 位成员</td>
							<td><a href="space.jsp?do=mtag&tagid=${mtag.tagid}" class="submit">立即访问本群组</a></td>
						</tr></c:forEach>
					</table>
				</div>
			</c:when>
			<c:when test="${op=='confirm'}">
				<c:choose><c:when test="${empty findmtag}">
					<form method="post" action="cp.jsp?ac=mtag" class="c_form">
						<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
						<c:if test="${!empty likemtags}">
							<table cellspacing="0" cellpadding="0" class="formtable">
								<caption>
									<h2>相似热门群组推荐</h2>
									<p>以下热门的群组与您要创建的群组很接近，您可以不用创建新群组，而选择直接加入热门群组来与更多人一起讨论话题。</p>
								</caption>
								<c:forEach items="${likemtags}" var="likemtag">
								<tr>
									<td><a href="space.jsp?do=mtag&tagid=${likemtag.tagid}" target="_blank">${likemtag.tagname}</a></td>
									<td>${likemtag.membernum} 位成员</td>
									<td>
										<a href="space.jsp?do=mtag&tagid=${likemtag.tagid}" target="_blank">立即访问本群组</a>
										<span class="pipe">|</span>
										<c:choose><c:when test="${likemtag.joinperm < 2}"><a href="cp.jsp?ac=mtag&op=join&tagid=${likemtag.tagid}" id="mtag_like_${likemtag.tagid}" onclick="ajaxmenu(event, this.id)">选择加入该群组</a></c:when><c:otherwise><strong>本群组需要群主邀请才可加入</strong></c:otherwise></c:choose>
									</td>
								</tr>
								</c:forEach>
							</table>
						</c:if>
						<table cellspacing="0" cellpadding="0" class="formtable">
							<caption><h2>确认创建新群组吗？</h2></caption>
							<tr>
								<th width="120">要创建的群组名称</th>
								<td>${newTagName}</td>
							</tr>
							<tr>
								<th width="120">群组分类</th>
								<td>${profield.title}</td>
							</tr>
							<tr>
								<td></td>
								<td>
									<input type="hidden" name="tagname" value="${newTagName}">
									<input type="hidden" name="fieldid" value="${fieldId}">
									<input type="hidden" name="joinmode" value="1">
									<input type="submit" id="textsubmit" name="textsubmit" value="创建群组" class="submit">
								</td>
							</tr>
						</table>
					</form>
				</c:when><c:otherwise>
					<div class="c_form">
						<table cellspacing="0" cellpadding="0" class="formtable">
							<tr>
								<td width="90"><div class="threadimg60"><img src="${findmtag.pic}" width="60" height="60"></div></td>
								<td>
									群组<a href="space.jsp?do=mtag&tagid=${findmtag.tagid}" target="_blank">${findmtag.tagname}</a> 已经存在<br>
									已有 ${findmtag.membernum} 人参与<br><br>
									<a href="space.jsp?do=mtag&tagid=${findmtag.tagid}" class="submit">访问群组</a>
								</td>
							</tr>
						</table>
					</div>
				</c:otherwise></c:choose>
			</c:when>
			<c:otherwise>
				<c:if test="${!empty textList}"><form method="post" action="cp.jsp?ac=mtag" class="c_form">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<table cellspacing="6" cellpadding="6" class="formtable">
						<caption>
							<h2>创建自己的新群组</h2>
							<p>您可以自由创建一个属于自己的群组，并邀请好友，前来进行交流讨论。</p>
						</caption>
						<tr>
							<th width="120">要创建的群组名</th>
							<td><input type="text" name="tagname" value="" class="t_input"></td>
						</tr>
						<tr>
							<th>选择一个合适的分类</th>
							<td>
								<select name="fieldid"><c:forEach items="${textList}" var="text">
									<option value="${text.key}">${text.value}</option></c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<th>&nbsp;</th>
							<td><input type="submit" name="textsubmit" value="创建群组" class="submit"></td>
						</tr>
					</table>
				</form></c:if>
				<c:if test="${!empty choiceList}"><form method="post" action="cp.jsp?ac=mtag" class="c_form">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<table cellspacing="6" cellpadding="6" class="formtable">
						<caption>
							<h2>加入站点固定群组</h2>
							<p>您可以选择加入感兴趣的站点群组，与更多志同道合的朋友一起讨论相关话题。<br>对于已经加入的群组，如果您想取消选择，请访问相应群组首页后选择“退出群组”操作。</p>
						</caption>
						<c:forEach items="${choiceList}" var="choice">
						<tr>
							<th width="120">${choice.title}</th>
							<td><table><tr>
								<c:choose><c:when test="${choice.formtype=='multi'}">
									<c:forEach items="${choice.choice}" var="value" varStatus="index">
									<td><input type="checkbox" name="tagname_${choice.fieldid}" value="${value}"${jch:inArray(exist_mtags[choice.fieldid],value) ? " checked disabled" : ""}> <a href="space.jsp?do=mtag&fieldid=${choice.fieldid}&tagname=${jch:urlEncoder(value)}" target="_blank">${value}</a></td>
									${index.count%3==0 ? "</tr><tr>" : ""}</c:forEach>
								</c:when><c:otherwise>
									<c:forEach items="${choice.choice}" var="value" varStatus="index">
									<td><input type="radio" name="tagname_${choice.fieldid}" value="${value}"${jch:inArray(exist_mtags[choice.fieldid],value) ? " checked" : ""}${empty exist_mtags[choice.fieldid] ? "" : " disabled"}> <a href="space.jsp?do=mtag&fieldid=${choice.fieldid}&tagname=${jch:urlEncoder(value)}" target="_blank">${value}</a></td>
									${index.count%3==0 ? "</tr><tr>" : ""}</c:forEach>
								</c:otherwise></c:choose>
							</tr></table></td>
						</tr>
						</c:forEach>
						<tr>
							<th>&nbsp;</th>
							<td><input type="submit" name="choicesubmit" value="加入群组" class="submit"></td>
						</tr>
					</table>
				</form></c:if>
				<form method="get" action="space.jsp" class="c_form">
					<table cellspacing="6" cellpadding="6" class="formtable">
						<caption>
							<h2>搜索现有群组</h2>
							<p>可以搜索一下，看看有没有自己感兴趣的群组，并申请成为成员。</p>
						</caption>
						<tr>
							<th width="120">搜索群组名</th>
							<td><input name="searchkey" value="" class="t_input" type="text"></td>
						</tr>
						<tr>
							<th>&nbsp;</th>
							<td><input name="searchsubmit" value="搜索群组" class="submit" type="submit"></td>
						</tr>
					</table>
					<input type="hidden" name="searchmode" value="1" />
					<input type="hidden" name="do" value="mtag" />
					<input type="hidden" name="view" value="hot" />
				</form>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />