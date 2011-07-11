<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}" />
<jsp:include page="${jch:template(sConfig, sGlobal, 'cp_header.jsp')}" />
<div class="l_status c_form">
	<a href="cp.jsp?ac=privacy"${cat_active_base}>隐私设置</a><span class="pipe">|</span>
	<a href="cp.jsp?ac=privacy&op=view"${cat_active_view}>动态筛选</a>
</div>
<form method="post" action="cp.jsp?ac=privacy" class="c_form">
	<c:choose>
		<c:when test="${empty param.op}">
			<table cellspacing="0" cellpadding="0" class="formtable">
				<caption>
					<h2>个人隐私设置</h2>
					<p>你可以完全控制哪些人可以看到你的主页上面的内容</p>
				</caption>
				<tr>
					<th width="150">个人主页</th>
					<td>
						<select name="privacy[view][index]">
							<option value="0"${view.index_0}>全站用户可见</option>
							<option value="1"${view.index_1}>仅好友可见</option>
							<option value="2"${view.index_2}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>好友列表</th>
					<td>
						<select name="privacy[view][friend]">
							<option value="0"${view.friend_0}>全站用户可见</option>
							<option value="1"${view.friend_1}>仅好友可见</option>
							<option value="2"${view.friend_2}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>留言板</th>
					<td>
						<select name="privacy[view][wall]">
							<option value="0"${view.wall_0}>全站用户可见</option>
							<option value="1"${view.wall_1}>仅好友可见</option>
							<option value="2"${view.wall_2}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>个人动态</th>
					<td>
						<select name="privacy[view][feed]">
							<option value="0"${view.feed_0}>全站用户可见</option>
							<option value="1"${view.feed_1}>仅好友可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>记录</th>
					<td>
						<select name="privacy[view][doing]">
							<option value="0"${view.doing_0}>全站用户可见</option>
							<option value="1"${view.doing_1}>仅好友可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td class="gray">本隐私设置仅在其他用户查看您主页时有效；<br>在全站的记录列表中可能会出现您的记录。</td>
				</tr>
				<tr>
					<th>日志</th>
					<td>
						<select name="privacy[view][blog]">
							<option value="0"${view.blog_0}>全站用户可见</option>
							<option value="1"${view.blog_1}>仅好友可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td class="gray">本隐私设置仅在其他用户查看您主页时有效；<br>相关浏览权限需要在每篇日志中单独设置方可完全生效。</td>
				</tr>
				<tr>
					<th>相册</th>
					<td>
						<select name="privacy[view][album]">
							<option value="0"${view.album_0}>全站用户可见</option>
							<option value="1"${view.album_1}>仅好友可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td class="gray">本隐私设置仅在其他用户查看您主页时有效；<br>相关浏览权限需要在每个相册中单独设置方可完全生效。</td>
				</tr>
				<tr>
					<th>分享</th>
					<td>
						<select name="privacy[view][share]">
							<option value="0"${view.share_0}>全站用户可见</option>
							<option value="1"${view.share_1}>仅好友可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td class="gray">本隐私设置仅在其他用户查看您主页时有效；<br>在全站的分享列表中可能会出现您的分享。</td>
				</tr>
				<tr>
					<th>活动</th>
					<td>
						<select name="privacy[view][event]">
							<option value="0"${view.event_0}>全站用户可见</option>
							<option value="1"${view.event_1}>仅好友可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td class="gray">本隐私设置仅在其他用户查看您主页时有效；</td>
				</tr>
				<tr>
					<th>投票</th>
					<td>
						<select name="privacy[view][poll]">
							<option value="0"${view.poll_0}>全站用户可见</option>
							<option value="1"${view.poll_1}>仅好友可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td class="gray">本隐私设置仅在其他用户查看您主页时有效；</td>
				</tr>
				<tr>
					<th>礼物</th>
					<td>
						<select name="privacy[view][gift]">
							<option value="0"${view.gift_0}>全站用户可见</option>
							<option value="1"${view.gift_1}>仅好友可见</option>
							<option value="1"${view.gift_2}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td class="gray">本隐私设置仅在其他用户查看您主页时有效；</td>
				</tr>
				<tr>
					<th>群组</th>
					<td>
						<select name="privacy[view][mtag]">
							<option value="0"${view.mtag_0}>全站用户可见</option>
							<option value="1"${view.mtag_1}>仅好友可见</option>
							<option value="2"${view.mtag_2}>仅自己可见</option>
						</select>
					</td>
				</tr>
				<c:if test="${sConfig.videophoto == 1 && space.videostatus == 1}">
					<tr>
						<th></th>
						<td><img src="image/videophoto.gif" align="absmiddle"> 你已经通过视频认证，对于没有通过视频认证的用户，你可以设置以下权限：</td>
					</tr>
					<tr>
						<th>查看我的认证照片</th>
						<td>
							<select name="privacy[view][videoviewphoto]">
								<option value="0"${view.videoviewphoto_0}>站点默认设置</option>
								<option value="1"${view.videoviewphoto_1}>允许</option>
								<option value="2"${view.videoviewphoto_2}>禁止</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>向我申请好友</th>
						<td>
							<select name="privacy[view][videofriend]">
								<option value="0"${view.videofriend_0}>站点默认设置</option>
								<option value="1"${view.videofriend_1}>允许</option>
								<option value="2"${view.videofriend_2}>禁止</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>向我打招呼</th>
						<td>
							<select name="privacy[view][videopoke]">
								<option value="0"${view.videopoke_0}>站点默认设置</option>
								<option value="1"${view.videopoke_1}>允许</option>
								<option value="2"${view.videopoke_2}>禁止</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>给我留言</th>
						<td>
							<select name="privacy[view][videowall]">
								<option value="0"${view.videowall_0}>站点默认设置</option>
								<option value="1"${view.videowall_1}>允许</option>
								<option value="2"${view.videowall_2}>禁止</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>评论我的信息</th>
						<td>
							<select name="privacy[view][videocomment]">
								<option value="0"${view.videocomment_0}>站点默认设置</option>
								<option value="1"${view.videocomment_1}>允许</option>
								<option value="2"${view.videocomment_2}>禁止</option>
							</select>
						</td>
					</tr>
				</c:if>
				<tr>
					<th>&nbsp;</th>
					<td><input type="submit" name="privacysubmit" value="保存设置" class="submit"></td>
				</tr>
			</table>
			<table cellspacing="0" cellpadding="0" class="formtable" id="feed">
				<caption>
					<h2>个人动态发布设置</h2>
					<p>系统会将您的各项动作反映到个人动态里，方便朋友了解你的动态。<br>你可以控制是否在下列动作发生时，在个人动态里发布相关信息</p>
				</caption>
				<tr>
					<th width="150">&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][doing]" value="1"${feed.doing}>记录</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][blog]" value="1"${feed.blog}>撰写日志</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][upload]" value="1"${feed.upload}>上传图片</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][share]" value="1"${feed.share}>添加分享</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][poll]" value="1"${feed.poll}>发起投票</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][joinpoll]" value="1"${feed.joinpoll}>参与投票</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][thread]" value="1"${feed.thread}>发起话题</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][post]" value="1"${feed.post}>对话题回复</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][mtag]" value="1"${feed.mtag}>加入群组</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][event]" value="1"${feed.event}>发起活动</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][join]" value="1"${feed.join}>参加活动</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][friend]" value="1"${feed.friend}>添加好友</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][comment]" value="1"${feed.comment}>发表评论/留言</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][show]" value="1"${feed.show}>竞价排名</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][credit]" value="1"${feed.credit}>积分消费</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][invite]" value="1"${feed.invite}>邀请好友</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][task]" value="1"${feed.task}>完成任务</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][profile]" value="1"${feed.profile}>更新个人资料</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="checkbox" name="privacy[feed][click]" value="1"${feed.click}>对日志/图片/话题表态</td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<td><input type="submit" name="privacysubmit" value="保存设置" class="submit"></td>
				</tr>
			</table>
		</c:when>
		<c:otherwise>
			<table cellspacing="0" cellpadding="0" class="formtable">
				<caption>
					<h2>筛选规则一：屏蔽指定用户组的动态</h2>
					<p>你可以决定屏蔽哪些用户组的动态，屏蔽用户组内的组员所发布的动态都将被屏蔽掉。</p>
				</caption>
				<c:forEach items="${groups}" var="m">
					<tr>
						<th width="150">&nbsp;</th>
						<td><input type="checkbox" name="privacy[filter_gid][${m.key}]" value="${m.key}"${!empty space.privacy.filter_gid[m.key] ? " checked" : ""}> ${m.value}</td>
					</tr>
				</c:forEach>
				<tr>
					<th>&nbsp;</th>
					<td>
						<input type="submit" name="privacy2submit" value="保存设置" class="submit">
						<br>您可以在自己的<a href="space.jsp?do=friend">好友列表</a>中，对好友进行分组，并可以对用户组进行改名。
					</td>
				</tr>
			</table>
			<br>
			<table cellspacing="0" cellpadding="0" class="formtable">
				<caption>
					<h2>筛选规则二：屏蔽指定好友指定类型的动态</h2>
					<p>点击一下首页好友动态列表后面的屏蔽标志，就可以屏蔽指定好友指定类型的动态了。<br>下面列出的是您已经屏蔽的动态类型识别名和好友名，你可以选择是否取消屏蔽。</p>
				</caption>
				<c:choose>
					<c:when test="${!empty icons}">
						<tr>
							<th width="150">&nbsp;</th>
							<td>
								<ul>
									<c:forEach items="${icons}" var="m">
										<c:set var="uid" value="${uids[m.key]}"></c:set>
										<c:set var="icon_uid" value="${m.value}|${uid}"></c:set>
										<li>
											<c:choose>
												<c:when test="${jch:isNumeric(m.value)}"><img src="http://appicon.jsprun.com/icons/${m.value}" align="absmiddle"></c:when>
												<c:otherwise><img src="image/icon/${m.value}.gif" align="absmiddle"></c:otherwise>
											</c:choose>
											<input type="checkbox" name="privacy[filter_icon][${icon_uid}]" value="1" checked="checked">
											<span class="type_${m.value}">${!empty iconnames[m.value] ? iconnames[m.value] : m.value} (<c:choose><c:when test="${!empty users[uid]}"><a href="space.jsp?uid=${uid}" target="_blank">${users[uid]}</a></c:when><c:otherwise>所有好友</c:otherwise></c:choose>)</span>
										</li>
									</c:forEach>
								</ul>
							</td>
						</tr>
						<tr>
							<th>&nbsp;</th>
							<td><input type="submit" name="privacy2submit" value="保存设置" class="submit"></td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<th width="150">&nbsp;</th>
							<td>现在还没有屏蔽的动态类型</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
			<br>
			<table cellspacing="0" cellpadding="0" class="formtable">
				<caption>
					<h2>筛选规则三：屏蔽指定好友指定类型的通知</h2>
					<p>点击一下通知列表后面的屏蔽标志，就可以屏蔽指定好友指定类型的通知了。<br>下面列出的是您已经屏蔽的通知类型和好友名，你可以选择是否取消屏蔽。</p>
				</caption>
				<c:choose>
					<c:when test="${!empty types}">
						<tr>
							<th width="150">&nbsp;</th>
							<td>
								<ul>
									<c:forEach items="${types}" var="m">
										<c:set var="uid" value="${uids[m.key]}"></c:set>
										<c:set var="type_uid" value="${m.value}|${uid}"></c:set>
										<li>
											<input type="checkbox" name="privacy[filter_note][${type_uid}]" value="1" checked="checked">
											<span class="type_${m.value}">${!empty iconnames[m.value] ? iconnames[m.value] : m.value} (<c:choose><c:when test="${!empty users[uid]}"><a href="space.jsp?uid=${uid}" target="_blank">${users[uid]}</a></c:when><c:otherwise>所有好友</c:otherwise></c:choose>)</span>
										</li>
									</c:forEach>
								</ul>
							</td>
						</tr>
						<tr>
							<th>&nbsp;</th>
							<td><input type="submit" name="privacy2submit" value="保存设置" class="submit"></td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<th width="150">&nbsp;</th>
							<td>现在还没有屏蔽的通知类型</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
		</c:otherwise>
	</c:choose>
	<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
</form>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}" />