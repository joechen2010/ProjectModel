<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="install_method.jsp"%>
<%
	try{
		Map<String, String> jchConfig=JavaCenterHome.jchConfig;
		mysql_connect(jchConfig.get("dbHost"),jchConfig.get("dbPort"),jchConfig.get("dbName"),jchConfig.get("dbUser"),jchConfig.get("dbPw"),jchConfig.get("dbCharset"));
		Map<String, Map<String,Integer>> privacy =new HashMap<String, Map<String,Integer>>();
		String[] views={"index","profile","friend","wall","feed","mtag","event","doing","blog","album","share","poll"};
		Map<String,Integer> viewMap=new HashMap<String,Integer>(views.length);
		for(String info : views){
			viewMap.put(info,0);
		}
		privacy.put("view",viewMap);
		String[] feeds={"doing","blog","upload","share","poll","joinpoll","thread","post","mtag","event","join","friend","comment","show","spaceopen","credit","invite","task","profile","album","click"};
		Map<String,Integer> feedMap=new HashMap<String,Integer>(views.length);
		for(String info : feeds){
			feedMap.put(info,1);
		}
		privacy.put("feed",feedMap);
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		int timeStamp = (Integer) sGlobal.get("timestamp");
		String[] datas={"('sitename', '我的空间')",
				"('template', 'default')",
				"('adminemail', 'webmaster@"+request.getServerName()+"')",
				"('onlinehold', '1800')",
				"('timeoffset', '8')",
				"('maxpage', '100')",
				"('starcredit', '100')",
				"('starlevelnum', '5')",
				"('cachemode', 'database')",
				"('cachegrade', '0')",
				"('allowcache', '1')",
				"('allowdomain', '0')",
				"('headercharset', '0')",
				"('allowrewrite', '0')",
				"('allowwatermark', '0')",
				"('allowftp', '0')",
				"('holddomain', 'www|*blog*|*space*|x')",
				"('mtagminnum', '5')",
				"('feedday', '7')",
				"('feedmaxnum', '100')",
				"('feedfilternum', '10')",
				"('importnum', '100')",
				"('maxreward', '10')",
				"('singlesent', '50')",
				"('groupnum', '8')",
				"('closeregister', '0')",
				"('closeinvite', '0')",
				"('close', '0')",
				"('networkpublic', '1')",
				"('networkpage', '1')",
				"('seccode_register', '1')",
				"('jc_tagrelated', '1')",
				"('manualmoderator', '1')",
				"('linkguide', '1')",
				"('showall', '1')",
				"('sendmailday', '0')",
				"('realname', '0')",
				"('namecheck', '0')",
				"('namechange', '0')",
				"('videophotocheck', '1')",
				"('videophotochange', '0')",
				"('name_allowviewspace', '1')",
				"('name_allowfriend', '1')",
				"('name_allowpoke', '1')",
				"('name_allowdoing', '1')",
				"('name_allowblog', '0')",
				"('name_allowalbum', '0')",
				"('name_allowthread', '0')",
				"('name_allowshare', '0')",
				"('name_allowcomment', '0')",
				"('name_allowpost', '0')",
				"('name_allowgift', '0')",
				"('showallfriendnum', '10')",
				"('feedtargetblank', '1')",
				"('feedread', '1')",
				"('feedhotnum', '3')",
				"('feedhotday', '2')",
				"('feedhotmin', '3')",
				"('feedhiddenicon', 'friend,profile,task,wall')",
				"('jc_tagrelatedtime', '86400')",
				"('privacy', '"+Serializer.serialize(privacy)+"')",
				"('cronnextrun', '"+timeStamp+"')",
				"('my_status', '0')",
				"('maxreward', '10')",
				"('uniqueemail', '1')",
				"('updatestat', '1')",
				"('my_showgift', '1')",
				"('topcachetime', '60')",
				"('newspacenum', '3')",
				
				"('seccode_login', '0')",
				"('questionmode', '0')",
				"('checkemail', '0')",
				"('regipdate', '0')",
				};
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("config"));
		mysql_update("REPLACE INTO "+JavaCenterHome.getTableName("config")+" (var, datavalue) VALUES "+Common.implode(datas,","));
		datas=new String[]{"('自由联盟', 'text', '100', '0', '1','')","('地区联盟', 'text', '100', '0', '1','')","('兴趣联盟', 'text', '100', '0', '1','')"};
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("profield"));
		mysql_update("REPLACE INTO "+JavaCenterHome.getTableName("profield")+" (title,formtype,inputnum,manualmoderator,manualmember,choice) VALUES "+Common.implode(datas,","));
		Map<String,Object[]> dataMap=new HashMap<String,Object[]>();
		dataMap.put("grouptitle",new String[]{"站点管理员", "信息管理员", "贵宾VIP", "受限会员", "普通会员", "中级会员", "高级会员", "禁止发言", "禁止访问"});
		dataMap.put("gid",new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
		dataMap.put("system",new Integer[]{-1, -1, 1, 0, 0, 0, 0, -1, -1});
		dataMap.put("explower",new Integer[]{0, 0, 0, -999999999, 0, 100, 1000, 0, 0});
		dataMap.put("banvisit",new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 1});
		dataMap.put("searchignore",new Integer[]{1, 1, 1, 0, 0, 0, 1, 0, 0});
		dataMap.put("videophotoignore",new Integer[]{1, 1, 0, 0, 0, 0, 0, 0, 0});
		dataMap.put("spamignore",new Integer[]{1, 1, 1, 0, 0, 0, 0, 0, 0});
		dataMap.put("color",new String[]{"red", "blue", "green", "", "", "", "", "", ""});
		dataMap.put("icon",new String[]{"image/group/admin.gif", "image/group/infor.gif", "image/group/vip.gif", "", "", "", "", "", ""});
		dataMap.put("magicaward",new String[]{"", "", "", "", "", "", "", "", ""});
		dataMap.put("maxfriendnum",new Integer[]{0, 0, 0, 10, 100, 200, 300, 1, 1});
		dataMap.put("maxattachsize",new Integer[]{0, 0, 0, 10, 20, 50, 100, 1, 1});
		dataMap.put("postinterval",new Integer[]{0, 0, 0, 300, 60, 30, 10, 9999, 9999});
		dataMap.put("searchinterval",new Integer[]{0, 0, 0, 600, 60, 30, 10, 9999, 9999});
		
		dataMap.put("verifyevent",new Integer[]{0, 0, 0, 1, 0, 0, 0, 1, 1});

		dataMap.put("domainlength",new Integer[]{1, 3, 3, 0, 0, 5, 3, 99, 99});
		dataMap.put("closeignore",new Integer[]{1, 1, 0, 0, 0, 0, 0, 0, 0});
		dataMap.put("seccode",new Integer[]{0, 0, 0, 1, 0, 0, 0, 1, 1});
		
		dataMap.put("allowhtml",new Integer[]{1, 1, 1, 0, 0, 0, 1, 0, 0});
		dataMap.put("allowcss",new Integer[]{1, 1, 1, 0, 0, 0, 1, 0, 0});
		dataMap.put("allowviewvideopic",new Integer[]{1, 1, 1, 0, 0, 0, 0, 0, 0});
		
		dataMap.put("allowtopic",new Integer[]{1, 1, 0, 0, 0, 0, 0, 0, 0});
		dataMap.put("allowstat",new Integer[]{1, 1, 0, 0, 0, 0, 0, 0, 0});
		
		dataMap.put("allowgift",new Integer[]{1, 1, 1, 0, 1, 1, 1, 0, 0});
		
		for(String value : new String[]{"comment","blog","poll","doing","upload","share","mtag","thread","post","poke","friend","click","event","magic", "pm", "myop"}) {
			dataMap.put("allow"+value,new Integer[]{1, 1, 1, 0, 1, 1, 1, 0, 0});
		}
		for(String value : new String[]{"config","usergroup","credit","profilefield","profield","censor","ad","cache","block","template","backup","stat","cron","app", "network","name","task","report", "eventclass", "magic","magiclog","topic", "batch", "delspace", "spacegroup", "spaceinfo", "spacecredit", "spacenote", "ip", "hotuser", "defaultuser", "click", "videophoto", "log"}) {
			dataMap.put("manage"+value,new Integer[]{1, 0, 0, 0, 0, 0, 0, 0, 0});
		}
		for(String value : new String[]{"tag","mtag","feed","share","doing", "blog","album","comment","thread", "event", "poll"}) {
			dataMap.put("manage"+value,new Integer[]{1, 1, 0, 0, 0, 0, 0, 0, 0});
		}
		
		Set<String> keys= dataMap.keySet();
		int count = dataMap.get("grouptitle").length;
		String[] newDatas = new String[count];
		for (int i=0; i<count; i++) {
			List thes = new ArrayList();
			for(String value : keys) {
				thes.add(dataMap.get(value)[i]);
			}
			newDatas[i] = "("+Common.sImplode(thes)+")";
		}
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("usergroup"));
		mysql_update("REPLACE INTO "+JavaCenterHome.getTableName("usergroup")+" ("+Common.implode(keys,",")+") VALUES "+Common.implode(newDatas, ","));
		List<String> ruls = new ArrayList();
		ruls.add("('开通空间', 'register', '0', '0', '1', '1', '10', '0', '0')");
		ruls.add("('实名认证', 'realname', '0', '0', '1', '1', '20', '0', '20')");
		ruls.add("('邮箱认证', 'realemail', '0', '0', '1', '1', '40', '0', '40')");
		ruls.add("('成功邀请好友', 'invitefriend', '4', '0', '20', '1', '10', '0', '10')");
		ruls.add("('设置头像', 'setavatar', '0', '0', '1', '1', '15', '0', '15')");
		ruls.add("('视频认证', 'videophoto', '0', '0', '1', '1', '40', '0', '40')");
		ruls.add("('成功举报', 'report', '4', '0', '0', '1', '2', '0', '2')");
		ruls.add("('更新心情', 'updatemood', '1', '0', '3', '1', '3', '0', '3')");
		ruls.add("('热点信息', 'hotinfo', '4', '0', '0', '1', '10', '0', '10')");
		ruls.add("('每天登陆', 'daylogin', '1', '0', '1', '1', '15', '0', '15')");
		ruls.add("('访问别人空间', 'visit', '1', '0', '10', '1', '1', '2', '1')");
		ruls.add("('打招呼', 'poke', '1', '0', '10', '1', '1', '2', '1')");
		ruls.add("('留言', 'guestbook', '1', '0', '20', '1', '2', '2', '2')");
		ruls.add("('被留言', 'getguestbook', '1', '0', '5', '1', '1', '2', '0')");
		ruls.add("('发表记录', 'doing', '1', '0', '5', '1', '1', '0', '1')");
		ruls.add("('发表日志', 'publishblog', '1', '0', '3', '1', '5', '0', '5')");
		ruls.add("('上传图片', 'uploadimage', '1', '0', '10', '1', '2', '0', '2')");
		ruls.add("('拍大头贴', 'camera', '1', '0', '5', '1', '3', '0', '3')");
		ruls.add("('发表话题', 'publishthread', '1', '0', '5', '1', '5', '0', '5')");
		ruls.add("('回复话题', 'replythread', '1', '0', '10', '1', '1', '1', '1')");
		ruls.add("('创建投票', 'createpoll', '1', '0', '5', '1', '2', '0', '2')");
		ruls.add("('参与投票', 'joinpoll', '1', '0', '10', '1', '1', '1', '1')");
		ruls.add("('发起活动', 'createevent', '1', '0', '1', '1', '3', '0', '3')");
		ruls.add("('参与活动', 'joinevent', '1', '0', '1', '1', '1', '1', '1')");
		ruls.add("('推荐活动', 'recommendevent', '4', '0', '0', '1', '10', '0', '10')");
		ruls.add("('发起分享', 'createshare', '1', '0', '3', '1', '2', '0', '2')");
		ruls.add("('评论', 'comment', '1', '0', '40', '1', '1', '1', '1')");
		ruls.add("('被评论', 'getcomment', '1', '0', '20', '1', '1', '1', '0')");
		ruls.add("('安装应用', 'installapp', '4', '0', '0', '1', '5', '3', '5')");
		ruls.add("('使用应用', 'useapp', '1', '0', '10', '1', '1', '3', '1')");
		ruls.add("('信息表态', 'click', '1', '0', '10', '1', '1', '1', '1')");
		ruls.add("('修改实名', 'editrealname', '0', '0', '1', '0', '5', '0', '0')");
		ruls.add("('更改邮箱认证', 'editrealemail', '0', '0', '1', '0', '5', '0', '0')");
		ruls.add("('头像被删除', 'delavatar', '0', '0', '1', '0', '10', '0', '10')");
		ruls.add("('获取邀请码', 'invitecode', '0', '0', '1', '0', '0', '0', '0')");
		ruls.add("('搜索一次', 'search', '0', '0', '1', '0', '1', '0', '0')");
		ruls.add("('日志导入', 'blogimport', '0', '0', '1', '0', '10', '0', '0')");
		ruls.add("('修改域名', 'modifydomain', '0', '0', '1', '0', '5', '0', '0')");
		ruls.add("('日志被删除', 'delblog', '0', '0', '1', '0', '10', '0', '10')");
		ruls.add("('记录被删除', 'deldoing', '0', '0', '1', '0', '2', '0', '2')");
		ruls.add("('图片被删除', 'delimage', '0', '0', '1', '0', '4', '0', '4')");
		ruls.add("('投票被删除', 'delpoll', '0', '0', '1', '0', '4', '0', '4')");
		ruls.add("('话题被删除', 'delthread', '0', '0', '1', '0', '4', '0', '4')");
		ruls.add("('活动被删除', 'delevent', '0', '0', '1', '0', '6', '0', '6')");
		ruls.add("('分享被删除', 'delshare', '0', '0', '1', '0', '4', '0', '4')");
		ruls.add("('留言被删除', 'delguestbook', '0', '0', '1', '0', '4', '0', '4')");
		ruls.add("('评论被删除', 'delcomment', '0', '0', '1', '0', '2', '0', '2')");
		mysql_update("INSERT INTO "+JavaCenterHome.getTableName("creditrule")+" (`rulename`, `action`, `cycletype`, `cycletime`, `rewardnum`, `rewardtype`, `credit`, `norepeat`, `experience`) VALUES "+Common.implode(ruls, ","));

		
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("data"));
		Map<String,Object> mails = new HashMap<String,Object>();
		mails.put("maildelimiter",0);
		mails.put("mailusername",1);
		mails.put("server","");
		mails.put("port",0);
		mails.put("auth",0);
		mails.put("from","");
		mails.put("auth_username","");
		mails.put("auth_password","");
		data_set("mail",mails,false);
		Map<String,Integer> settings = new HashMap<String,Integer>();
		settings.put("thumbwidth",100);
		settings.put("thumbheight",100);
		settings.put("maxthumbwidth",0);
		settings.put("maxthumbheight",0);
		settings.put("watermarkpos",4);
		settings.put("watermarktrans",75);
		data_set("setting",settings,false);
		Map<String,Map<String,Integer>> network = new HashMap<String,Map<String,Integer>>();
		Map<String,Integer> blog = new HashMap<String,Integer>();
		blog.put("hot1",3);
		blog.put("cache",600);
		Map<String,Integer> pic = new HashMap<String,Integer>();
		pic.put("hot1",3);
		pic.put("cache",700);
		Map<String,Integer> thread = new HashMap<String,Integer>();
		thread.put("hot1",3);
		thread.put("cache",800);
		Map<String,Integer> event = new HashMap<String,Integer>();
		event.put("cache",900);
		Map<String,Integer> poll = new HashMap<String,Integer>();
		poll.put("cache",500);
		Map<String,Integer> share = new HashMap<String,Integer>();
		share.put("cache",500);
		
		network.put("blog",blog);
		network.put("pic",pic);
		network.put("thread",thread);
		network.put("event",event);
		network.put("poll",poll);
		network.put("share",share);
		data_set("network",network,false);
		datas = new String[]{
			"1, 'system', '更新浏览数统计', 'log.jsp', "+timeStamp+", "+timeStamp+", -1, -1, -1, '0	5	10	15	20	25	30	35	40	45	50	55'",
			"1, 'system', '清理过期feed', 'cleanfeed.jsp', "+timeStamp+", "+timeStamp+", -1, -1, 3, '4'",
			"1, 'system', '清理个人通知', 'cleannotification.jsp', "+timeStamp+", "+timeStamp+", -1, -1, 5, '6'",
			"1, 'system', '清理脚印和最新访客', 'cleantrace.jsp', "+timeStamp+", "+timeStamp+", -1, -1, 2, '3'"
		};
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("cron"));
		mysql_update("INSERT INTO "+JavaCenterHome.getTableName("cron")+" (available, type, name, filename, lastrun, nextrun, weekday, day, hour, minute) VALUES ("+Common.implode(datas,"),(")+")");
		datas = new String[]{
				"1, '更新一下自己的头像', '头像就是你在这里的个人形象。<br>设置自己的头像后，会让更多的朋友记住您。', 'avatar.jsp', 1, '', 0, 20, 'image/task/avatar.gif'",
				"1, '将个人资料补充完整', '把自己的个人资料填写完整吧。<br>这样您会被更多的朋友找到的，系统也会帮您找到朋友。', 'profile.jsp', '', 2, 0, 20, 'image/task/profile.gif'",
				"1, '发表自己的第一篇日志', '现在，就写下自己的第一篇日志吧。<br>与大家一起分享自己的生活感悟。', 'blog.jsp', 3, '', 0, 5, 'image/task/blog.gif'",
				"1, '寻找并添加五位好友', '有了好友，您发的日志、图片等会被好友及时看到并传播出去；<br>您也会在首页方便及时的看到好友的最新动态。', 'friend.jsp', 4, '', 0, 50, 'image/task/friend.gif'",
				"1, '验证激活自己的邮箱', '填写自己真实的邮箱地址并验证通过。<br>您可以在忘记密码的时候使用该邮箱取回自己的密码；<br>还可以及时接受站内的好友通知等等。', 'email.jsp', 5, '', 0, 10, 'image/task/email.gif'",
				"1, '邀请10个新朋友加入', '邀请一下自己的QQ好友或者邮箱联系人，让亲朋好友一起来加入我们吧。', 'invite.jsp', 6, '', 0, 100, 'image/task/friend.gif'",
				"1, '领取每日访问大礼包', '每天登录访问自己的主页，就可领取大礼包。', 'gift.jsp', 99, 'day', 0, 5, 'image/task/gift.gif'"
		};
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("task"));
		mysql_update("INSERT INTO "+JavaCenterHome.getTableName("task")+" (`available`, `name`, `note`, `filename`, `displayorder`, `nexttype`, `nexttime`, `credit`, `image`) VALUES ("+Common.implode(datas,"),(")+")");
		datas = new String[]{
			"1, '生活/聚会', 0, '费用说明：\r\n集合地点：\r\n着装要求：\r\n联系方式：\r\n注意事项：', 1",
			"2, '出行/旅游', 0, '路线说明:\r\n费用说明:\r\n装备要求:\r\n交通工具:\r\n集合地点:\r\n联系方式:\r\n注意事项:', 2",
			"3, '比赛/运动', 0, '费用说明：\r\n集合地点：\r\n着装要求：\r\n场地介绍：\r\n联系方式：\r\n注意事项：', 4",
			"4, '电影/演出', 0, '剧情介绍：\r\n费用说明：\r\n集合地点：\r\n联系方式：\r\n注意事项：', 3",
			"5, '教育/讲座', 0, '主办单位：\r\n活动主题：\r\n费用说明：\r\n集合地点：\r\n联系方式：\r\n注意事项：', 5",
			"6, '其它', 0, '', 6"
		};
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("eventclass"));
		mysql_update("INSERT INTO "+JavaCenterHome.getTableName("eventclass")+" (classid, classname, poster, template, displayorder) VALUES ("+Common.implode(datas,"),(")+")");
		List<String> dataList = new ArrayList<String>();
		dataList.add("'invisible', '隐身草', '让自己隐身登录，不显示在线，24小时内有效', '5', '50', '86400', '10', '86400', '1','',''");
		dataList.add("'friendnum', '好友增容卡', '在允许添加的最多好友数限制外，增加10个好友名额', '3', '30', '86400', '999', '0', '1','',''");
		dataList.add("'attachsize', '附件增容卡', '使用一次，可以给自己增加 10M 的附件空间', '3', '30', '86400', '999', '0', '1','',''");
		dataList.add("'thunder', '雷鸣之声', '发布一条全站信息，让大家知道自己上线了', '5', '500', '86400', '5', '86400', '1','',''");
		dataList.add("'updateline', '救生圈', '把指定对象的发布时间更新为当前时间', '5', '200', '86400', '999', '0', '1','',''");
			
		dataList.add("'downdateline', '时空机', '把指定对象的发布时间修改为过去的时间', '5', '250', '86400', '999', '0', '1','',''");		
		dataList.add("'color', '彩色灯', '把指定对象的标题变成彩色的', '5', '50', '86400', '999', '0', '1','',''");
		dataList.add("'hot', '热点灯', '把指定对象的热度增加站点推荐的热点值', '5', '50', '86400', '999', '0', '1','',''");
		dataList.add("'visit', '互访卡', '随机选择10个好友，向其打招呼、留言或访问空间', '2', '20', '86400', '999', '0', '1','',''");
		dataList.add("'icon', '彩虹蛋', '给指定对象的标题前面增加图标（最多8个图标）', '2', '20', '86400', '999', '0', '1','',''");
			
		dataList.add("'flicker', '彩虹炫', '让评论、留言的文字闪烁起来', '3', '30', '86400', '999', '0', '1','',''");
		dataList.add("'gift', '红包卡', '在自己的空间埋下积分红包送给来访者', '2', '20', '86400', '999', '0', '1','',''");
		dataList.add("'superstar', '超级明星', '在个人主页，给自己的头像增加超级明星标识', '3', '30', '86400', '999', '0', '1','',''");
		dataList.add("'viewmagiclog', '八卦镜', '查看指定用户最近使用的道具记录', '5', '100', '86400', '999', '0', '1','',''");
		dataList.add("'viewmagic', '透视镜', '查看指定用户当前持有的道具', '5', '100', '86400', '999', '0', '1','',''");
			
		dataList.add("'viewvisitor', '偷窥镜', '查看指定用户最近访问过的10个空间', '5', '100', '86400', '999', '0', '1','',''");
		dataList.add("'call', '点名卡', '发通知给自己的好友，让他们来查看指定的对象', '5', '50', '86400', '999', '0', '1','',''");
		dataList.add("'coupon', '代金券','购买道具时折换一定量的积分', '0', '0', '0', '0', '0', '1','',''");
		dataList.add("'frame', '相框', '给自己的照片添上相框', '3', '30', '86400', '999', '0', '1','',''");
		dataList.add("'bgimage', '信纸', '给指定的对象添加信纸背景', '3', '30', '86400', '999', '0', '1','',''");
			
		dataList.add("'doodle', '涂鸦板', '允许在留言、评论等操作时使用涂鸦板', '3', '30', '86400', '999', '0', '1','',''");
		dataList.add("'anonymous', '匿名卡', '在指定的地方，让自己的名字显示为匿名', '5', '50', '86400', '999', '0', '1','',''");
		dataList.add("'reveal', '照妖镜', '可以查看一次匿名用户的真实身份', '5', '100', '86400', '999', '0', '1','',''");
		dataList.add("'license', '道具转让许可证', '使用许可证，将道具赠送给指定好友', '1', '10', '3600', '999', '0', '1','',''");
		dataList.add("'detector', '探测器', '探测埋了红包的空间', '1', '10', '86400', '999', '0', '1','',''");
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("magic"));
		mysql_update("INSERT INTO "+JavaCenterHome.getTableName("magic")+"(`mid`, `name`, `description`, `experience`, `charge`, `provideperoid`, `providecount`, `useperoid`, `usecount`,`forbiddengid`,`custom`) VALUES ("+Common.implode(dataList,"),(")+")");
		datas = new String[]{
			"'1', '路过', 'luguo.gif', 'blogid'",
			"'2', '雷人', 'leiren.gif', 'blogid'",
			"'3', '握手', 'woshou.gif', 'blogid'",
			"'4', '鲜花', 'xianhua.gif', 'blogid'",
			"'5', '鸡蛋', 'jidan.gif', 'blogid'",
			
			"'6', '漂亮', 'piaoliang.gif', 'picid'",
			"'7', '酷毙', 'kubi.gif', 'picid'",
			"'8', '雷人', 'leiren.gif', 'picid'",
			"'9', '鲜花', 'xianhua.gif', 'picid'",
			"'10', '鸡蛋', 'jidan.gif', 'picid'",
			
			"'11', '搞笑', 'gaoxiao.gif', 'tid'",
			"'12', '迷惑', 'mihuo.gif', 'tid'",
			"'13', '雷人', 'leiren.gif', 'tid'",
			"'14', '鲜花', 'xianhua.gif', 'tid'",
			"'15', '鸡蛋', 'jidan.gif', 'tid'"
		};
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("click"));
		mysql_update("INSERT INTO "+JavaCenterHome.getTableName("click")+" (clickid, `name`, icon, idtype) VALUES ("+Common.implode(datas,"),(")+")");
		
		List<String> giftTypeList = new ArrayList<String>();
		giftTypeList.add("'advGift', '高级礼物', '0', '1'");
		giftTypeList.add("'birthday', '生日', '1', '1'");
		giftTypeList.add("'card', '贺卡', '1', '4'");
		giftTypeList.add("'defGift', '普通礼物', '0', '0'");
		giftTypeList.add("'feeGift', '收费礼物', '0', '2'");
		giftTypeList.add("'love', '爱情', '1', '2'");
		giftTypeList.add("'magic', '魔法', '1', '3'");
		giftTypeList.add("'other', '其他', '1', '5'");
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("gifttype"));
		mysql_update("INSERT INTO "+JavaCenterHome.getTableName("gifttype")+"(`typeid`, `typename`, `fee`, `order`) VALUES ("+Common.implode(giftTypeList,"),(")+")");
		
		List<String> giftList = new ArrayList<String>();
		giftList.add("'50', '玫瑰', '一朵玫瑰', '0', '0', 'image/gift/default/1275548853640_7KBa9p.gif', '1275548853', 'defGift'");
		giftList.add("'51', '生日蛋糕', '生日蛋糕', '0', '0', 'image/gift/default/1275548899437_H1fx1b.gif', '1275548899', 'defGift'");
		giftList.add("'52', '棒棒糖', '棒棒糖', '0', '0', 'image/gift/default/1275548925437_RyNFXz.gif', '1275548925', 'defGift'");
		giftList.add("'53', '红包', '恭喜发财-红包', '0', '0', 'image/gift/default/1275548965687_4hXFYg.gif', '1275548965', 'defGift'");
		giftList.add("'54', '元宝', '金元宝', '0', '0', 'image/gift/default/1275548996484_geO3Bd.gif', '1275548996', 'defGift'");
		giftList.add("'55', '熊宝宝', '玩具熊宝宝', '0', '0', 'image/gift/default/1275549026281_PXA293.gif', '1275549026', 'defGift'");
		giftList.add("'56', '啤酒', '啤酒', '0', '0', 'image/gift/default/1275549058015_mg7DN6.gif', '1275549058', 'defGift'");
		giftList.add("'57', '宝箱', '蓝色宝箱', '0', '0', 'image/gift/default/1275549081187_yzonc0.gif', '1275549081', 'defGift'");
		giftList.add("'58', '苹果', '金苹果', '0', '0', 'image/gift/default/1275549206843_AhQFuV.gif', '1275549206', 'defGift'");
		giftList.add("'59', '小天使', '小天使', '0', '0', 'image/gift/default/1275549265218_tg0pGk.gif', '1275549265', 'defGift'");
		giftList.add("'60', '钻石', '钻石', '0', '0', 'image/gift/default/1275549292171_pzXItW.gif', '1275549292', 'defGift'");
		giftList.add("'61', '手枪', '手枪', '0', '0', 'image/gift/advanced/1275549444312_z7tPfQ.gif', '1275549444', 'advGift'");
		giftList.add("'62', '搓衣板', '回家用吧', '0', '0', 'image/gift/advanced/1275549471609_2LsSOz.gif', '1275549471', 'advGift'");
		giftList.add("'63', '红肚兜', '性感肚兜', '0', '0', 'image/gift/advanced/1275549493593_bMSbkz.gif', '1275549493', 'advGift'");
		giftList.add("'64', '内裤', '内裤', '0', '0', 'image/gift/advanced/1275549524671_7kAhdI.gif', '1275549524', 'advGift'");
		giftList.add("'65', '红蜡烛', '红蜡烛', '0', '0', 'image/gift/advanced/1275549544312_7saP3Z.gif', '1275549544', 'advGift'");
		giftList.add("'66', '一束玫瑰', '一束玫瑰', '0', '0', 'image/gift/advanced/1275549566359_pidssZ.gif', '1275549566', 'advGift'");
		giftList.add("'67', '钻戒', '粉色钻戒', '0', '0', 'image/gift/advanced/1275549588921_GVGMbK.gif', '1275549588', 'advGift'");
		giftList.add("'68', 'love香水', 'love香水', '0', '0', 'image/gift/advanced/1275549606843_WDE6IT.gif', '1275549606', 'advGift'");
		giftList.add("'69', 'Zippo', 'Zippo', '0', '0', 'image/gift/advanced/1275549622593_yO4kf7.gif', '1275549622', 'advGift'");
		giftList.add("'70', '避孕套', '避孕套', '0', '0', 'image/gift/advanced/1275549642609_8FYmnF.gif', '1275549642', 'advGift'");
		giftList.add("'71', '饺子', '饺子', '0', '0', 'image/gift/advanced/1275549677296_Y8CQeg.gif', '1275549677', 'advGift'");
		giftList.add("'72', '冰棍', '冰棍', '0', '0', 'image/gift/advanced/1275549898515_XBL4XQ.gif', '1275549898', 'advGift'");
		giftList.add("'73', '创可贴', '邦由创可贴', '0', '0', 'image/gift/advanced/1275549917343_TdOM0Y.gif', '1275549917', 'advGift'");
		giftList.add("'74', '胸罩', '胸罩', '0', '0', 'image/gift/advanced/1275549930687_1ggT7u.gif', '1275549930', 'advGift'");
		giftList.add("'75', '脑白金', '脑白金', '0', '0', 'image/gift/advanced/1275549948562_8EBlaq.gif', '1275549948', 'advGift'");
		giftList.add("'76', '香蕉', '香蕉', '0', '0', 'image/gift/advanced/1275549962421_GGD6ny.gif', '1275549962', 'advGift'");
		giftList.add("'77', '高跟鞋', '高跟鞋', '0', '0', 'image/gift/advanced/1275549975390_K4WqIz.gif', '1275549975', 'advGift'");
		giftList.add("'78', '香烟', '骆驼牌香烟', '0', '0', 'image/gift/advanced/1275549995562_52QIW6.gif', '1275549995', 'advGift'");
		giftList.add("'79', '咖啡', '咖啡', '0', '0', 'image/gift/advanced/1275550025937_gMRcbq.gif', '1275550025', 'advGift'");
		giftList.add("'80', '奶瓶', '奶瓶', '0', '0', 'image/gift/advanced/1275550050468_R4kV3F.gif', '1275550050', 'advGift'");
		giftList.add("'81', '靠枕', '靠枕', '0', '0', 'image/gift/default/1275550116515_KLl7iy.gif', '1275550116', 'defGift'");
		
		mysql_update("TRUNCATE TABLE "+JavaCenterHome.getTableName("gift"));
		mysql_update("INSERT INTO "+JavaCenterHome.getTableName("gift")+"(`giftid`, `giftname`, `tips`, `price`, `buycount`, `icon`, `addtime`, `typeid`) VALUES ("+Common.implode(giftList,"),(")+")");
		int step=(Integer)request.getAttribute("step");
		showMessage("系统默认数据添加完毕，进入下一步操作", step+1, 1, request,response);
	}catch(Exception e){
		String message=getErrorMessage();
		if(message==null){
			message=e.getMessage();
		}
		showMessage(message, 0, 0, request,response);
	}finally{
		mysql_close();
	}
	
%>
