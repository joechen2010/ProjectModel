package cn.jcenterhome.web.action.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;
import cn.jcenterhome.util.Serializer;
import cn.jcenterhome.web.action.BaseAction;


public class LogAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
		Map<String, Object> space = (Map<String, Object>) request.getAttribute("space");
		String logRoot=JavaCenterHome.jchRoot+"/data/log/";
		File[] files=Common.readDir(logRoot,"log");
		if(files==null){
			return mapping.findForward("log");
		}
		String[] logFiles=new String[files.length];
		int i=0;
		for (File file : files) {
			logFiles[i]=file.getName();
			i++;
		}
		for (int k = 0; k < logFiles.length; k++) {
			for (int j = k + 1; j < logFiles.length; j++) {
				if (logFiles[j].compareToIgnoreCase(logFiles[k])<0){
					String temp = logFiles[k];
					logFiles[k] = logFiles[j];
					logFiles[j] = temp;
				}
			}
		}
		String op=request.getParameter("op");
		if("view".equals(op)){
			Map log=new HashMap();
			String file=request.getParameter("file");
			file=Common.empty(file) ? "" : file.trim();
			
			
			if(!Common.empty(file) && Common.in_array(logFiles, file)){
				int line=Common.intval(request.getParameter("line"));
				File logFile=new File(logRoot+file);
				FileInputStream fis=null;
				BufferedReader bufReader =null;
				try {
					fis=new FileInputStream(logFile);
					bufReader = new BufferedReader(new InputStreamReader(fis,JavaCenterHome.JCH_CHARSET)); 
					String lineStr=null;
					int offset = 0;
					List<Map<String,Object>> list=null;
					Map<String,Object> value=null;
					while((lineStr=bufReader.readLine())!=null){
						if((offset++)==line){
							log=parseLog(lineStr, true);
							log.put("line", line);
							list=dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("space")+" WHERE uid = '"+log.get("uid")+"'");
							if(list.size()>0){
								value=list.get(0);
								Common.realname_set(sGlobal, sConfig, sNames, (Integer)value.get("uid"), (String)value.get("username"), "", 0);
								Common.realname_get(sGlobal, sConfig, sNames, space);
							}
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					try {
						if(fis!=null){
							fis.close();
						}
						if(bufReader!=null){
							bufReader.close();
						}
					} catch (IOException e) {
					}
				}
			}
			request.setAttribute("log", log);
		}else{
			int perpage=50;
			int uid=Common.intval(request.getParameter("uid"));
			String keySearch=Common.stripSearchKey(request.getParameter("keysearch"));
			String ip=request.getParameter("ip");
			ip=Common.empty(ip) ? "" : ip.trim();
			String file=request.getParameter("file");
			file=file==null ? "" : file.trim();
			String starttime=request.getParameter("starttime");
			starttime=starttime==null ? "" : starttime.trim();
			String endtime=request.getParameter("endtime");
			endtime=endtime==null ? "" : endtime.trim();
			String mpurl="admincp.jsp?ac=log&file="+file+"&uid="+uid+"&ip="+ip+"&starttime="+starttime+"&endtime="+endtime+"&keysearch="+keySearch;
			
			Map<String,String[]> paramMap=request.getParameterMap();
			paramMap.put("uid", new String[]{uid+""});
			paramMap.put("keysearch", new String[]{keySearch});
			paramMap.put("ip", new String[]{ip});
			String tmpPath=JavaCenterHome.jchRoot+"/data/temp/";
			File tmpRoot=new File(tmpPath);
			if(!tmpRoot.isDirectory()){
				tmpRoot.mkdirs();
			}
			
			int page=Common.intval(request.getParameter("page"));
			page=page<1 ? 1 : page;
			int start = (page-1)*perpage;
			int maxPage = (Integer) sConfig.get("maxpage");
			String result = Common.ckStart(start, perpage, maxPage);
			if (result != null) {
				return showMessage(request, response, result);
			}
			boolean fromCache=true;
			List uids=new ArrayList();
			List list=new ArrayList();
			int count=0;
			Map logInfo=new HashMap();
			String tmpFilePath=tmpPath+"logsearch_"+Common.md5(mpurl).substring(8, 16)+".tmp";
			File tmpFile=new File(tmpFilePath);
			if(!tmpFile.isFile()){
				fromCache = false;
				File logFile=new File(logRoot+file);
				List lines=new ArrayList();
				RandomAccessFile ras=null;
				if(!Common.empty(file)){
					try {
						ras=new RandomAccessFile(logFile,"r");
						String line=null;
						int cursor= 0;
						int offset= 0;
						boolean valid = true;
						int n=0;
						long o=0;
						String temp=null;
						while((line=ras.readLine())!=null){
							logInfo=parseLog(line, false);
							logInfo.put("line", cursor);
							uids.add(logInfo.get("uid"));
							valid=true;
							temp=new String(line.getBytes("ISO-8859-1"),JavaCenterHome.JCH_CHARSET);
							if((uid>0 && uid!=(Integer)logInfo.get("uid")) || 
									(!Common.empty(starttime) && starttime.compareTo((String)logInfo.get("dateline"))>0) ||
									(!Common.empty(endtime) && endtime.compareTo((String)logInfo.get("dateline"))<0) ||
									(!Common.empty(ip) && !ip.equals(logInfo.get("ip"))) || 
									(!Common.empty(keySearch) && temp.indexOf(keySearch)<0)){
								valid=false;
							}
							
							if(valid){
								n=(line+"\n").length();
								o=ras.getFilePointer()-n;
								lines.add(cursor+"-"+o+"-"+n);
								if(offset >= start && offset < start + perpage) {
									list.add(logInfo);
								}
								offset++;
							}
							cursor++;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(ras!=null){
							try {ras.close();} catch (IOException e1) {}
						}
						ras=null;
					}
				}
				count=lines.size();
				if(count>0){
					FileHelper.writeFile(tmpFilePath, Common.implode(lines, ";"));
				}
			}
			if(fromCache){
				List<String> data=Arrays.asList(sreadFile(tmpFilePath).split(";"));
				count=data.size();
				int toIndex=perpage+start;
				toIndex=toIndex>count ? count : toIndex;
				List<String> lines=data.subList(start, toIndex);
				if(lines!=null && lines.size()>0){
					File logFile=new File(logRoot+file);
					RandomAccessFile ras=null;
					if(!Common.empty(file)){
						try {
							ras=new RandomAccessFile(logFile,"r");
							byte[] b=new byte[1024];
							for (String line : lines) {
								String[] tmp=line.split("-");
								int l=Common.intval(tmp[0]);
								long o=Common.intval(tmp[1]);
								int n=Common.intval(tmp[2]);
								ras.seek(o);
								if(n>0){
									line=ras.readLine().substring(0, n-1);
								}else{
									line="";
								}
								logInfo=parseLog(line, false);
								logInfo.put("line", l);
								uids.add(logInfo.get("uid"));
								list.add(logInfo);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(ras!=null){
								try {ras.close();} catch (IOException e1) {}
							}
							ras=null;
						}
					}
				}
			}
			if(uids.size()>0){
				List<Map<String, Object>> spaceList=dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("space")+" WHERE uid IN ("+Common.sImplode(uids)+')');
				for (Map<String, Object> value : spaceList) {
					Common.realname_set(sGlobal, sConfig, sNames, (Integer)value.get("uid"), (String)value.get("username"), "", 0);
				}
				Common.realname_get(sGlobal, sConfig, sNames, space);
			}
			String multi = Common.multi(request, count, perpage, page,maxPage ,mpurl, null, null);
			request.setAttribute("multi", multi);
			request.setAttribute("logfiles", logFiles);
			request.setAttribute("list", list);
		}
		return mapping.findForward("log");
	}
	
	
	private Map parseLog(String line,boolean detail){
		Map logInfo=new HashMap();
		if(line==null || line.equals("")){
			return logInfo;
		}
		String[] lineList=line.split("\t");
		int lineLength=lineList.length;
		if(lineList==null || lineLength<4){
			return logInfo;
		}
		String dateline=lineList[0];
		String ip=lineList[1];
		String uid=lineList[2];
		String link=lineList[3];
		String extra=null;
		if(lineLength>=5){
			extra=lineList[4];
		}else{
			extra="";
		}
		logInfo.put("ip", ip);
		logInfo.put("uid", Common.intval(uid));
		logInfo.put("link", link);
		logInfo.put("dateline", dateline);
		if(detail){
			if(Common.matches(extra, "GET\\{(.*?);\\}")){
				Map get=new HashMap();
				String[] parts=extra.replaceAll("GET\\{(.*?);\\}", "$1").split(";");
				String list1=null;
				for (String value : parts) {
					if(value.indexOf("=")>0){
						String[] list=value.split("=");
						if(list.length<=1){
							list1="";
						}else{
							list1=list[1];
						}
						get.put(list[0], list1);
					}
				}
				logInfo.put("get", "<pre>"+print_r(get, 1, true)+"</pre>");
				extra = "";
			}
			if(Common.matches(extra, "POST\\{(.*);\\}")){
				Map post=new HashMap();
				String temp=extra.replaceAll("POST\\{(.*);\\}", "$1");
				temp=temp.replaceAll(";(\\w+(\\[(.+?)\\])?)=", "////$1=");
				String[] parts=temp.split("////");
				String list1=null;
				Map tempMap=null;
				if(parts!=null){
					for (String value : parts) {
						if(value.indexOf("=")>0){
							String[] list=value.split("=");
							if(list.length>1){
								list1=list[1];
							}else{
								list1="";
							}
							if(Common.matches(list1, "^a:\\d+:\\{")){
								tempMap=Serializer.unserialize(list1, true);
								post.put(list[0], tempMap);
							}else{
								post.put(list[0], list1);
							}
						}
					}
				}
				logInfo.put("post", "<pre>"+print_r(post, 1, true)+"</pre>");
				extra = "";
			}
			logInfo.put("extra", extra.trim());
		}
		return logInfo;
	}
	
	private String print_r(Map expression,int level,boolean ispre){
		if(expression==null || expression.size()==0){
			return "";
		}
		StringBuffer info=new StringBuffer();
		Set keys=expression.keySet();
		String nbsp="";
		String tab="";
		String sign=null;
		sign=ispre ? "&#9;": "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		for (int i = 0; i < level; i++) {
			nbsp=nbsp+sign;
		}
		tab=nbsp;
		tab=level==1 ? "" : tab;
		info.append("Map {<br/>");
		int i=0;
		int keySize=keys.size();
		for (Object key : keys) {
			i++;
			info.append("<br/>"+nbsp);
			if(expression.get(key) instanceof Map){
				info.append(key+" = "+print_r((Map)expression.get(key),level+1,ispre));
			}else{
				info.append(key+" = "+expression.get(key)+(keySize==i ? "" : ","));
			}
		}
		info.append("<br/>");
		info.append(tab+"}");
		return info.toString();
	}
	
	private String sreadFile(String fileName){
		if(fileName==null){
			return "";
		}
		StringBuffer info=new StringBuffer();
		File file=new File(fileName);
		try {
			FileReader fr=new FileReader(file);
			BufferedReader bufReader=new BufferedReader(fr);
			while (bufReader.ready()) {
				info.append(bufReader.readLine()+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} 
		return info.toString();
	}
	
	
	
	

}
