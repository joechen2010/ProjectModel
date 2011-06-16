package com.joe.utilities.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

 

public class MyHttp {

	private static final Log log = LogFactory.getLog(MyHttp.class);
	    /**  
	     * 判断IP是否在指定范围；  
	     */  
	     
	    public static boolean ipIsValid(String ipSection, String ip) {   
	    	 
	        if (ipSection == null)   
	            throw new NullPointerException("IP段不能为空！");   
	        if (ip == null)   
	            throw new NullPointerException("IP不能为空！");   
	        ipSection = ipSection.trim();   
	        ip = ip.trim();   
	        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";   
	        final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;   
	        if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))   
	            return false;   
	        int idx = ipSection.indexOf('-');   
	        String[] sips = ipSection.substring(0, idx).split("\\.");   
	        String[] sipe = ipSection.substring(idx + 1).split("\\.");   
	        String[] sipt = ip.split("\\.");   
	        long ips = 0L, ipe = 0L, ipt = 0L;   
	        for (int i = 0; i < 4; ++i) {   
	            ips = ips << 8 | Integer.parseInt(sips[i]);   
	            ipe = ipe << 8 | Integer.parseInt(sipe[i]);   
	            ipt = ipt << 8 | Integer.parseInt(sipt[i]);   
	        }   
	        if (ips > ipe) {   
	            long t = ips;   
	            ips = ipe;   
	            ipe = t;   
	        }   
	        return ips <= ipt && ipt <= ipe;   
	    }   
	    
	    public static void main(String[] args) { 
	    	
	    	MyHttp.toSendUrl("http://gx.12530.com/user/searchbyconditiontone.do?pag2=2&querytype=tonename&condition=%D2%BB%B3%A1%D7%ED");
	    }   

	    
	    public static String toSendUrl(String urlString) {

			StringBuffer sb = new StringBuffer();
			HttpURLConnection conn = null;
			int error_count = 0 ;
			boolean flag = false ;
			String result = "" ;
			while(error_count < 1 && !flag){
				log.info("第"+(error_count+1)+"次请求URL开始...");
				result = "" ;
				try {
					URL url = new URL(urlString);
					conn = (HttpURLConnection) url.openConnection();
					
					conn.setConnectTimeout(10000);
					conn.setRequestMethod("GET");
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"GB2312"));
					String line = null;
					while ((line = reader.readLine())!= null){
					    result += line;
					}
						reader.close();   
					log.info("请求URL成功，返回结果:"+result);
					flag = true ;
				} catch (IOException e) {
						log.info("请求URL失败..");
						log.info(e.toString());
						e.printStackTrace();
						error_count++;
						flag = false ;
				} finally {
					 if (conn != null)
						conn.disconnect();
				}
			}
			return result ;
		}
	    
    
	
}
