<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%@page import="cn.jcenterhome.util.BeanFactory"%>
<%@page import="cn.jcenterhome.util.JavaCenterHome"%>
<%@page import="cn.jcenterhome.util.Common"%>
<%
Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
int perbatch = 200;
Map<String,Map<Integer,Integer>> logs = new HashMap<String, Map<Integer,Integer>>();
int maxnum = 0,maxlogid = 0;
List<Map<String,Object>> query = dataBaseService.executeQuery("SELECT logid, id, idtype FROM "+JavaCenterHome.getTableName("log")+" ORDER BY logid ASC LIMIT 0,"+perbatch);
String idtype;
Integer id;
Integer count;
Map<Integer,Integer> tempMap;
for(Map<String,Object> value : query){
	idtype = (String)value.get("idtype");
	id = (Integer)value.get("id");
	tempMap = logs.get(idtype);
	if(tempMap == null){
		tempMap = new HashMap<Integer, Integer>();
		logs.put(idtype, tempMap);
	}
	count = tempMap.get(id);
	if(count == null){
		count = 0;
	}
	tempMap.put(id, ++count);
	maxnum++;
	maxlogid = (Integer)value.get("logid");
}
if(maxnum != 0){
	if(maxnum < perbatch) {
		dataBaseService.execute("TRUNCATE TABLE "+JavaCenterHome.getTableName("log"));
	}else{
		dataBaseService.execute("DELETE FROM "+JavaCenterHome.getTableName("log")+" WHERE logid<='"+maxlogid+"'");
	}
}

List<Integer> nums0;
Map<Integer, List<Integer>> nums1;
tempMap = logs.get("uid");
if(!Common.empty(tempMap)){
	Object[] nums = Common.reNum(tempMap);
	nums0 = (List<Integer>)nums[0];
	nums1 = (Map<Integer, List<Integer>>)nums[1];
	for(Integer num : nums0){
		dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("space")+" SET viewnum=viewnum+"+num+" WHERE uid IN ("+Common.sImplode(nums1.get(num))+")");
	}
}
tempMap = logs.get("tid");
if(!Common.empty(tempMap)){
	Object[] nums = Common.reNum(tempMap);
	nums0 = (List<Integer>)nums[0];
	nums1 = (Map<Integer, List<Integer>>)nums[1];
	for(Integer num : nums0){
		dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("thread")+" SET viewnum=viewnum+"+num+" WHERE tid IN ("+Common.sImplode(nums1.get(num))+")");
	}
}
tempMap = logs.get("blogid");
if(!Common.empty(tempMap)){
	Object[] nums = Common.reNum(tempMap);
	nums0 = (List<Integer>)nums[0];
	nums1 = (Map<Integer, List<Integer>>)nums[1];
	for(Integer num : nums0){
		dataBaseService.executeUpdate("UPDATE "+JavaCenterHome.getTableName("blog")+" SET viewnum=viewnum+$num WHERE blogid IN ("+Common.sImplode(nums1.get(num))+")");
	}
}
%>