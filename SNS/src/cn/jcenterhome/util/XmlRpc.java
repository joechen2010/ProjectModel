package cn.jcenterhome.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.jcenterhome.service.DataBaseService;

public class XmlRpc{
	private DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
	private HashMap<String,String> callBack;
	private HashMap<String,Object> xmlMessage;
	private String charSet;
	private Integer timestamp;
	private Map<String,Object> member;
	private Map<String, Object> sGlobal;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	
	public XmlRpc(HttpServletRequest request,HttpServletResponse response){
		this.callBack=this.xmlRpcApi();
		this.xmlMessage=new HashMap<String,Object>();
		this.charSet=JavaCenterHome.jchConfig.get("charset");
		this.request=request;
		this.response=response;
		this.sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		this.timestamp=(Integer)sGlobal.get("timestamp");
	}
	public XmlRpc(){
		
	}
	public XmlRpc xmlRpcSet(HttpServletRequest request,HttpServletResponse response) {
		return new XmlRpc(request,response);
	}
	
	
	public void xmlrpcServer() throws Exception{
		String data=readRequestInputStream();
		if(Common.empty(data)) {
			sendFault(1, "Invalid Method Call");
		} else {
			data =Common.addSlashes(data);
		}
		xmlMessage.put("structTypes", new Vector());
		xmlMessage.put("structs", new Vector());
		xmlMessage.put("struct_name", new Vector());
		if(xmlrpcParse(data)){
			Vector params=(Vector)xmlMessage.get("params");
			Object result=xmlrpcCall(xmlMessage.get("methodname").toString(), params.toArray());
			Map rxml = xmlrpcValue(result,null);
			String outXml = xmlrpcValueXML(rxml);
			outXml = "<methodResponse><params><param><value>"+outXml+"</value></param></params></methodResponse>";
			outXml=Common.siconv(outXml, "UTF-8", charSet, charSet).toString();
			xmlrpcOutXML(outXml);
		}
	}
	
	private String sendFault(int code, String str) throws Exception{
		response.setContentType("text/xml");
		throw new Exception("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
				"<methodResponse><fault><value><struct><member><name>faultCode</name><value><i4>"
				+code+
				"</i4></value></member><member><name>faultString</name><value><string>"
				+str+
				"</string></value></member></struct></value></fault></methodResponse>");
	}
	
	
	private String readRequestInputStream(){
		StringBuilder data= new StringBuilder();
		BufferedReader br=null;
		InputStreamReader isr=null;
		try {
			isr=new InputStreamReader((ServletInputStream)request.getInputStream(),"UTF-8");
			br = new BufferedReader(isr);
	        String line = null;
	        while((line = br.readLine())!=null){
	        	data.append(line);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(isr!=null){
					isr.close();
					isr=null;
				}
				if(br!=null){
					br.close();
					br=null;
				}
			} catch (Exception e) {
			}
		}
		return data.toString();
	}
	
	
	private void xmlrpcOutXML(String xml) throws IOException{
		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+xml;
		response.setHeader("Connection", "close");
		response.setContentLength(Common.strlen(xml,charSet));
		response.setContentType("text/xml");
		String dateFormat = "E, d MMM  yyyy HH:mm:ss Z";
		response.setHeader("Date", Common.sgmdate(request, dateFormat, timestamp));
		PrintWriter out = response.getWriter();
		out.write(xml);
		out.flush();
		return;
	}
	
	
	private Object xmlrpcCall(String methodName, Object[] args) throws Exception{
		String method =callBack.get(methodName);
		if(method==null){
			sendFault(1, "Invalid Method Call");
		}
		Class parameterTypes=Object[].class;
		Object[] params={args};
		Method m= this.getClass().getDeclaredMethod(method, parameterTypes);
		return m.invoke(this,params);
	}
	
	
	private Map xmlrpcValue(Object data,String type) throws Exception{
		Map value=new HashMap();
		value.put("data", data);
		if (Common.empty(type)) {
			type = xmlrpcType(value);
		}
		value.put("type", type);
		if("struct".equals(type)){
			Map dataMap=(Map)value.get("data");
			for (Object key : dataMap.keySet()) {
				dataMap.put(key, xmlrpcValue(dataMap.get(key), null));
			}
		}
		if("array".equals(type)){
			Object[] datas=null;
			if(value.get("data") instanceof Collection){
				Collection dataCollection=(Collection)value.get("data");
				datas=dataCollection.toArray();
			}else{
				datas=(Object[])value.get("data");
			}
			for (int i = 0,j=datas.length; i < j; i++) {
				datas[i]=xmlrpcValue(datas[i], null);
			}
			value.put("data", datas);
		}
		return value;
	}
	
	private String xmlrpcType(Map value) {
		Object data=value.get("data");
		if (data instanceof Boolean) {
			return "boolean";
		}
		if (data instanceof Double) {
			return "double";
		}
		if (data instanceof Integer) {
			return "int";
		}
		if(data instanceof byte[]){
			return "base64";
		}
		if(data instanceof Date){
			return "date";
		}
		if(Common.isArray(data)){
			if(Common.empty(data)){
				return "array";
			}
			if(data instanceof Map){
				value.put("data", data);
				return "struct";
			}else{
				return "array";
			}
		}
		return "string";
	}
	
	
	private String xmlrpcValueXML(Map data){
		Object type=data.get("type");
		type=type==null ? type : type.toString();
		if("boolean".equals(type)){
			return "<boolean>"+ ((Boolean)data.get("data") ? "1" : "0")+"</boolean>";
		}else if("int".equals(type)){
			return "<int>"+data.get("data")+"</int>";
		}else if("double".equals(type)){
			return "<double>"+data.get("data")+"</double>";
		}else if("string".equals(type)){
			return "<string>"+Common.htmlSpecialChars(data.get("data")+"")+"</string>";
		}else if("array".equals(type)){
			String returnStr= "<array><data>";
			Object[] datas=null;
			if(data.get("data") instanceof Collection){
				Collection dataCollection=(Collection)data.get("data");
				datas=dataCollection.toArray();
			}else{
				datas=(Object[])data.get("data");
			}
			for (int i = 0,j=datas.length; i < j; i++) {
				returnStr+="<value>"+xmlrpcValueXML((Map)datas[i])+"</value>";
			}
			returnStr+= "</data></array>";
			return returnStr;
			
		}else if("struct".equals(type)){
			String returnStr= "<struct>";
			Map dataMap=(Map)data.get("data");
			for (Object name : dataMap.keySet()) {
				returnStr+= "<member><name>"+name+"</name>";
				returnStr+= "<value>"+xmlrpcValueXML((Map)dataMap.get(name))+"</value></member>";
			}
			returnStr+= "</struct>";
			return returnStr;
		}else if("date".equals(type)){
			return "<dateTime.iso8601>"+((Map)data.get("data")).get("date")+"</dateTime.iso8601>";
		}else if("base64".equals(type)){
			return "<base64>"+Base64.encode(data.get("data").toString(), charSet)+"</base64>";
		}
		return "";
	}
	
	private boolean xmlrpcParse(String data) throws Exception{
		data=data.replaceAll("<\\?xml(.*)?\\?>", "");
		xmlMessage.put("messages", data);
		if("".equals(Common.trim(data))){
			return false;
		}
		Document document=DocumentHelper.parseText(data);;
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.elements();
        xmltag_open(rootElement.getName());
        xml_parse(list);
        xmltag_close(rootElement.getName());
        rootElement.clearContent();
        document.clearContent();
		return true;
	}
	private void xml_parse(List<Element> list){
		for (Element e : list) {
			 List<Element> listChildern = e.elements();
	       	 if(listChildern.size()>0){
	       		xmltag_open(e.getName());
	       		xml_parse(listChildern);
	       		xmltag_close(e.getQName().getQualifiedName());
	       	 }else{
	       		xmltag_open(e.getName());
	       		xml_data(e.getText());
	       		xmltag_close(e.getQName().getQualifiedName());
	       	 }
		}
	}
	
	private void xmltag_open(String tag){
		xmlMessage.put("tag_content", "");
		xmlMessage.put("last_open", tag);
		if("methodCall".equals(tag) || "methodResponse".equals(tag) || "fault".equals(tag)){
			xmlMessage.put("messageType", tag);
		}else if("data".equals(tag)){
			Vector structTypes=(Vector)xmlMessage.get("structTypes");
			structTypes.add("array");
			Vector structs=(Vector)xmlMessage.get("structs");
			structs.add(new Vector());
		}else if("struct".equals(tag)){
			Vector structTypes=(Vector)xmlMessage.get("structTypes");
			structTypes.add("struct");
			Vector structs=(Vector)xmlMessage.get("structs");
			structs.add(new HashMap());
		}
	}
	
	
	private void xmltag_close(String tag){
		boolean flag = false;
		Object value=null;
		String tag_content=Common.trim(xmlMessage.get("tag_content").toString());
		if("int".equals(tag) || "i4".equals(tag)){
			value = Common.intval(tag_content);
			flag = true;
		}else if("double".equals(tag)){
			value = Double.valueOf(tag_content);
			flag = true;
		}else if("string".equals(tag)){
			value = tag_content;
			flag = true;
		}else if("dateTime.iso8601".equals(tag)){
			value = Common.strToTime(tag_content, Common.getTimeOffset(sGlobal, (Map<String, Object>) request.getAttribute("sConfig")));
			flag = true;
		}else if("value".equals(tag)){
			if (!"".equals(tag_content) || "value".equals(xmlMessage.get("last_open"))) {
				value = tag_content;
				flag = true;
			}
		}else if("boolean".equals(tag)){
			value = Boolean.valueOf(tag_content);
			flag = true;
		}else if("base64".equals(tag)){
			value = Common.sAddSlashes(Base64.decode(tag_content, charSet));
			flag = true;
		}else if("data".equals(tag) || "struct".equals(tag)){
			Vector structs=(Vector)xmlMessage.get("structs");
			structs.remove(structs.size()-1);
			value=structs;
			Vector structTypes=(Vector)xmlMessage.get("structTypes");
			structTypes.remove(structTypes.size()-1);
			flag = true;
		}else if("member".equals(tag)){
			Vector struct_name=(Vector)xmlMessage.get("struct_name");
			struct_name.remove(struct_name.size()-1);
		}else if("name".equals(tag)){
			Vector struct_name=(Vector)xmlMessage.get("struct_name");
			struct_name.add(tag_content);
		}else if("methodName".equals(tag)){
			xmlMessage.put("methodname", tag_content);
		}
		if (flag) {
			Vector structs=(Vector)xmlMessage.get("structs");
			if (structs.size() > 0) {
				Vector structTypes=(Vector)xmlMessage.get("structTypes");
				if ("struct".equals(structTypes.get(structTypes.size()-1))) {
					Vector struct_name=(Vector)xmlMessage.get("struct_name");
					HashMap map=(HashMap)structs.get(structs.size()-1);
					map.put(struct_name.get(struct_name.size()-1), value);
				}else{
					Vector v=(Vector)structs.get(structs.size()-1);
					if(v==null){
						v=new Vector();
						structs.add(v);
					}
					v.add(value);
				}
			} else {
				Vector params=(Vector)xmlMessage.get("params");
				if(params==null){
					params=new Vector();
					xmlMessage.put("params", params);
				}
				params.add(value);
			}
		}
		if (!Common.in_array(new String[]{"data", "struct", "member"},tag)) {
			xmlMessage.put("tag_content", "");
		}
	}
	
	
	private void xml_data(String data){
		xmlMessage.put("tag_content", xmlMessage.get("tag_content").toString()+data);
	}
	
	
	private boolean authUser(String userName, String passWord) throws Exception{
		userName = Common.addSlashes(Common.siconv(userName, charSet, "UTF-8", charSet).toString());
		passWord = Common.addSlashes(Common.siconv(passWord, charSet, "UTF-8", charSet).toString());
		Map<String, Object> member=Common.getPassPort(userName, passWord);
		if(member.isEmpty()){
			sendFault(1, "Authoried Error.Please check your password.");
			return false;
		}else{
			this.member=member;
			sGlobal.put("supe_uid", member.get("uid"));
			this.member.put("username", Common.addSlashes(this.member.get("username").toString()));
			sGlobal.put("supe_username", this.member.get("username"));
			sGlobal.put("timestamp", timestamp);
			return true;
		}
	}
	
	
	private HashMap<String,String> xmlRpcApi() {
		HashMap<String,String> api=new HashMap<String,String>();
		api.put("metaWeblog.newPost", "newPost");
		api.put("metaWeblog.editPost", "editPost");
		api.put("metaWeblog.getPost", "getPost");
		api.put("metaWeblog.newMediaObject", "newMediaObject");
		api.put("metaWeblog.getCategories", "getCategories");
		api.put("metaWeblog.getRecentPosts", "getRecentPosts");
		api.put("mt.getCategoryList", "getCategoryList");
		api.put("mt.setPostCategories", "setPostCategories");
		api.put("blogger.getUsersBlogs", "getUserBlog");
		api.put("blogger.getUserInfo", "getUserInfo");
		api.put("blogger.deletePost", "deletePost");
		api.put("blogger.getPost", "getPost");
		api.put("blogger.getRecentPosts", "getRecentPosts");
		api.put("blogger.newPost", "newPost");
		api.put("blogger.editPost", "editPost");
		return api;
	}
	
	
	private Object getRecentPosts(Object...args) throws Exception{
		int num=50;
		if(args.length>3){
			num=Common.intval(args[3].toString());
		}
		String type=args[0].toString();
		String userName=args[1].toString();
		String passWord=args[2].toString();
		authUser(userName, passWord);
		
		int uid = (Integer)member.get("uid");
		num=num < 1 ? num = 1 : num;
		List<Map<String,Object>> struct=new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> blogList=dataBaseService.executeQuery("SELECT bf.message, b.blogid, b.subject, b.dateline FROM "+JavaCenterHome.getTableName("blog")+" b LEFT JOIN "+JavaCenterHome.getTableName("blogfield")+" bf ON bf.blogid=b.blogid WHERE b.uid ='"+uid+"' ORDER BY b.dateline DESC LIMIT 0,"+num);
		for (Map<String, Object> item : blogList) {
			Map<String, Object> tempMap=new HashMap<String, Object>();
			tempMap.put("postid", item.get("blogid"));
			tempMap.put("userid", uid);
			tempMap.put("dateCreated", Common.sgmdate(request, "yyyyMMdd'T'HH:mm:ss", (Integer)item.get("dateline")));
			tempMap.put("title", item.get("subject"));
			tempMap.put("categories", new Object[]{item.get("classname")});
			tempMap.put("description", item.get("message"));
			tempMap.put("content", item.get("message"));
			struct.add(tempMap);
		}
		return struct;
	}
}
