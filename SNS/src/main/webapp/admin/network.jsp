<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="header.jsp" />
<div class="mainarea">
	<div class="maininner">
		<form method="post" action="admincp.jsp?ac=network">
			<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,true)}" />
			<div class="bdrcontent">
				<div class="title">
					<h3>日志聚合设置</h3>
					<p>设置日志显示在随便看看页面的条件</p>
				</div>
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<td style="width: 10em;">指定日志(blogid)</td>
						<td><input name="network[blog][blogid]" type="text" size="50" value="${globalNetWork.blog.blogid}" /> 多个blogid用","分隔</td>
					</tr>
					<tr>
						<td>指定作者(uid)</td>
						<td><input name="network[blog][uid]" type="text" size="50" value="${globalNetWork.blog.uid}" /> 多个uid用","分隔</td>
					</tr>
					<tr>
						<td>热度范围</td>
						<td>
							<input name="network[blog][hot1]" type="text" size="10" value="${globalNetWork.blog.hot1}" /> ~
							<input name="network[blog][hot2]" type="text" size="10" value="${globalNetWork.blog.hot2}" />
						</td>
					</tr>
					<tr>
						<td>查看数范围</td>
						<td>
							<input name="network[blog][viewnum1]" type="text" size="10" value="${globalNetWork.blog.viewnum1}" /> ~
							<input name="network[blog][viewnum2]" type="text" size="10" value="${globalNetWork.blog.viewnum2}" />
						</td>
					</tr>
					<tr>
						<td>回复数范围</td>
						<td>
							<input name="network[blog][replynum1]" type="text" size="10" value="${globalNetWork.blog.replynum1}" /> ~
							<input name="network[blog][replynum2]" type="text" size="10" value="${globalNetWork.blog.replynum2}" />
						</td>
					</tr>
					<tr>
						<td>发布时间范围</td>
						<td><input type="text" name="network[blog][dateline]" value="${globalNetWork.blog.dateline}" size="10"> 内天发布的才显示</td>
					</tr>
					<tr>
						<td>列表排序</td>
						<td>
							<select name="network[blog][order]">
								<option value="dateline">发布时间</option>
								<option value="viewnum"${orders.order_blog_viewnum}>查看数</option>
								<option value="replynum"${orders.order_blog_replynum}>回复数</option>
								<option value="hot"${orders.order_blog_hot}>热度</option>
							</select>
							<select name="network[blog][sc]">
								<option value="desc">递减</option>
								<option value="asc"${orders.sc_blog_asc}>递增</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>缓存有效时间</td>
						<td><input type="text" name="network[blog][cache]" value="${globalNetWork.blog.cache}" size="10"> 单位:秒 (设为0将不使用缓存机制，这会增加服务器负担)</td>
					</tr>
				</table>
				<br>
				<div class="title">
					<h3>图片聚合设置</h3>
					<p>设置图片显示在随便看看页面的条件</p>
				</div>
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<td style="width: 10em;">指定图片(picid)</td>
						<td><input name="network[pic][picid]" type="text" size="50" value="${globalNetWork.pic.picid}" /> 多个picid用","分隔</td>
					</tr>
					<tr>
						<td>指定作者(uid)</td>
						<td><input name="network[pic][uid]" type="text" size="50" value="${globalNetWork.pic.uid}" /> 多个uid用","分隔</td>
					</tr>
					<tr>
						<td>热度范围</td>
						<td>
							<input name="network[pic][hot1]" type="text" size="10" value="${globalNetWork.pic.hot1}" /> ~
							<input name="network[pic][hot2]" type="text" size="10" value="${globalNetWork.pic.hot2}" />
						</td>
					</tr>
					<tr>
						<td>发布时间范围</td>
						<td><input type="text" name="network[pic][dateline]" value="${globalNetWork.pic.dateline}" size="10"> 内天发布的才显示</td>
					</tr>
					<tr>
						<td>列表排序</td>
						<td>
							<select name="network[pic][order]">
								<option value="dateline">发布时间</option>
								<option value="hot"${orders.order_pic_hot }>热度</option>
							</select>
							<select name="network[pic][sc]">
								<option value="desc">递减</option>
								<option value="asc"${orders.sc_pic_asc }>递增</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>缓存有效时间</td>
						<td><input type="text" name="network[pic][cache]" value="${globalNetWork.pic.cache}" size="10"> 单位:秒 (设为0将不使用缓存机制，这会增加服务器负担)</td>
					</tr>
				</table>
				<br>
				<div class="title">
					<h3>话题聚合设置</h3>
					<p>设置话题显示在随便看看页面的条件</p>
				</div>
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<td style="width: 10em;">指定话题(tid)</td>
						<td><input name="network[thread][tid]" type="text" size="50" value="${globalNetWork.thread.tid}" /> 多个tid用","分隔</td>
					</tr>
					<tr>
						<td>指定作者(uid)</td>
						<td><input name="network[thread][uid]" type="text" size="50" value="${globalNetWork.thread.uid}" /> 多个uid用","分隔</td>
					</tr>
					<tr>
						<td>热度范围</td>
						<td>
							<input name="network[thread][hot1]" type="text" size="10" value="${globalNetWork.thread.hot1}" /> ~
							<input name="network[thread][hot2]" type="text" size="10" value="${globalNetWork.thread.hot2}" />
						</td>
					</tr>
					<tr>
						<td>查看数范围</td>
						<td>
							<input name="network[thread][viewnum1]" type="text" size="10" value="${globalNetWork.thread.viewnum1}" /> ~
							<input name="network[thread][viewnum2]" type="text" size="10" value="${globalNetWork.thread.viewnum2}" />
						</td>
					</tr>
					<tr>
						<td>回复数范围</td>
						<td>
							<input name="network[thread][replynum1]" type="text" size="10" value="${globalNetWork.thread.replynum1}" /> ~
							<input name="network[thread][replynum2]" type="text" size="10" value="${globalNetWork.thread.replynum2}" />
						</td>
					</tr>
					<tr>
						<td>发布时间范围</td>
						<td><input type="text" name="network[thread][dateline]" value="${globalNetWork.thread.dateline}" size="10"> 内天发布的才显示</td>
					</tr>
					<tr>
						<td>回复时间范围</td>
						<td><input type="text" name="network[thread][lastpost]" value="${globalNetWork.thread.lastpost}" size="10"> 内天回复的才显示</td>
					</tr>
					<tr>
						<td>列表排序</td>
						<td>
							<select name="network[thread][order]">
								<option value="dateline">发布时间</option>
								<option value="viewnum"${orders.order_thread_viewnum }>查看数</option>
								<option value="replynum"${orders.order_thread_replynum }>回复数</option>
								<option value="hot"${orders.order_thread_hot }>热度</option>
							</select>
							<select name="network[thread][sc]">
								<option value="desc">递减</option>
								<option value="asc"${orders.sc_thread_asc }>递增</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>缓存有效时间</td>
						<td><input type="text" name="network[thread][cache]" value="${globalNetWork.thread.cache}" size="10"> 单位:秒 (设为0将不使用缓存机制，这会增加服务器负担)</td>
					</tr>
				</table>
				<br>
				<div class="title">
					<h3>活动聚合设置</h3>
					<p>设置活动显示在随便看看页面的条件</p>
				</div>
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<td style="width: 10em;">指定活动(eventid)</td>
						<td><input name="network[event][eventid]" type="text" size="50" value="${globalNetWork.event.eventid}" /> 多个eventid用","分隔</td>
					</tr>
					<tr>
						<td>指定作者(uid)</td>
						<td><input name="network[event][uid]" type="text" size="50" value="${globalNetWork.event.uid}" /> 多个uid用","分隔</td>
					</tr>
					<tr>
						<td>热度范围</td>
						<td>
							<input name="network[event][hot1]" type="text" size="10" value="${globalNetWork.event.hot1}" /> ~
							<input name="network[event][hot2]" type="text" size="10" value="${globalNetWork.event.hot2}" />
						</td>
					</tr>
					<tr>
						<td>参与人数范围</td>
						<td>
							<input name="network[event][membernum1]" type="text" size="10" value="${globalNetWork.event.membernum1}" /> ~
							<input name="network[event][membernum2]" type="text" size="10" value="${globalNetWork.event.membernum2}" />
						</td>
					</tr>
					<tr>
						<td>关注人数范围</td>
						<td>
							<input name="network[event][follownum1]" type="text" size="10" value="${globalNetWork.event.follownum1}" /> ~
							<input name="network[event][follownum2]" type="text" size="10" value="${globalNetWork.event.follownum2}" />
						</td>
					</tr>
					<tr>
						<td>发布时间范围</td>
						<td><input type="text" name="network[event][dateline]" value="${globalNetWork.event.dateline}" size="10"> 内天发布的才显示</td>
					</tr>
					<tr>
						<td>列表排序</td>
						<td>
							<select name="network[event][order]">
								<option value="dateline">发布时间</option>
								<option value="membernum"${orders.order_event_membernum }>参与人数</option>
								<option value="follownum"${orders.order_event_follownum }>关注人数</option>
								<option value="hot"${orders.order_event_hot }>热度</option>
							</select>
							<select name="network[event][sc]">
								<option value="desc">递减</option>
								<option value="asc"${orders.sc_event_asc }>递增</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>缓存有效时间</td>
						<td><input type="text" name="network[event][cache]" value="${globalNetWork.event.cache}" size="10"> 单位:秒 (设为0将不使用缓存机制，这会增加服务器负担)</td>
					</tr>
				</table>
				<br>
				<div class="title">
					<h3>投票聚合设置</h3>
					<p>设置投票显示在随便看看页面的条件</p>
				</div>
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<td style="width: 10em;">指定投票(pid)</td>
						<td><input name="network[poll][pid]" type="text" size="50" value="${globalNetWork.poll.pid}" /> 多个pid用","分隔</td>
					</tr>
					<tr>
						<td>指定作者(uid)</td>
						<td><input name="network[poll][uid]" type="text" size="50" value="${globalNetWork.poll.uid}" /> 多个uid用","分隔</td>
					</tr>
					<tr>
						<td>热度范围</td>
						<td>
							<input name="network[poll][hot1]" type="text" size="10" value="${globalNetWork.poll.hot1}" /> ~
							<input name="network[poll][hot2]" type="text" size="10" value="${globalNetWork.poll.hot2}" />
						</td>
					</tr>
					<tr>
						<td>投票人数范围</td>
						<td>
							<input name="network[poll][voternum1]" type="text" size="10" value="${globalNetWork.poll.voternum1}" /> ~
							<input name="network[poll][voternum2]" type="text" size="10" value="${globalNetWork.poll.voternum2}" />
						</td>
					</tr>
					<tr>
						<td>回复人数范围</td>
						<td>
							<input name="network[poll][replynum1]" type="text" size="10" value="${globalNetWork.poll.replynum1}" /> ~
							<input name="network[poll][replynum2]" type="text" size="10" value="${globalNetWork.poll.replynum2}" />
						</td>
					</tr>
					<tr>
						<td>发布时间范围</td>
						<td><input type="text" name="network[poll][dateline]" value="${globalNetWork.poll.dateline}" size="10"> 内天发布的才显示</td>
					</tr>
					<tr>
						<td>列表排序</td>
						<td>
							<select name="network[poll][order]">
								<option value="dateline">发布时间</option>
								<option value="voternum"${orders.order_poll_voternum }>参与人数</option>
								<option value="replynum"${orders.order_poll_replynum }>关注人数</option>
								<option value="hot"${orders.order_poll_hot }>热度</option>
							</select>
							<select name="network[poll][sc]">
								<option value="desc">递减</option>
								<option value="asc"${orders.sc_poll_asc }>递增</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>缓存有效时间</td>
						<td><input type="text" name="network[poll][cache]" value="${globalNetWork.poll.cache}" size="10"> 单位:秒 (设为0将不使用缓存机制，这会增加服务器负担)</td>
					</tr>
				</table>
				<br>
				<div class="title">
					<h3>分享聚合设置</h3>
					<p>设置分享显示在随便看看页面的条件</p>
				</div>
				<table cellspacing="0" cellpadding="0" class="formtable">
					<tr>
						<td style="width: 10em;">指定分享(sid)</td>
						<td><input name="network[share][sid]" type="text" size="50" value="${globalNetWork.share.sid}" /> 多个sid用","分隔</td>
					</tr>
					<tr>
						<td>指定作者(uid)</td>
						<td><input name="network[share][uid]" type="text" size="50" value="${globalNetWork.share.uid}" /> 多个uid用","分隔</td>
					</tr>
					<tr>
						<td>热度范围</td>
						<td>
							<input name="network[share][hot1]" type="text" size="10" value="${globalNetWork.share.hot1}" /> ~
							<input name="network[share][hot2]" type="text" size="10" value="${globalNetWork.share.hot2}" />
						</td>
					</tr>
					<tr>
						<td>发布时间范围</td>
						<td><input type="text" name="network[share][dateline]" value="${globalNetWork.share.dateline}" size="10"> 内天分享的才显示</td>
					</tr>
					<tr>
						<td>列表排序</td>
						<td>
							<select name="network[share][order]">
								<option value="dateline">发布时间</option>
								<option value="hot"${orders.order_share_hot }>热度</option>
							</select>
							<select name="network[share][sc]">
								<option value="desc">递减</option>
								<option value="asc"${orders.sc_share_asc }>递增</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>缓存有效时间</td>
						<td><input type="text" name="network[share][cache]" value="${globalNetWork.share.cache}" size="10"> 单位:秒 (设为0将不使用缓存机制，这会增加服务器负担)</td>
					</tr>
				</table>
			</div>
			<div class="footactions"><input type="submit" name="networksubmit" value="提交" class="submit"></div>
		</form>
	</div>
</div>
<div class="side">
	<jsp:directive.include file="side.jsp" />
</div>
<jsp:directive.include file="footer.jsp" />