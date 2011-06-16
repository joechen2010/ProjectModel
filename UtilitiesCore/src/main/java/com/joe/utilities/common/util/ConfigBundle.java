package com.joe.utilities.common.util;

import java.util.*;
import java.io.*;
public class ConfigBundle {
	private static Properties p=null;
    /**
     * init()
     */
	private String path ;
	
    public ConfigBundle(String path)
    { 
    	this.path = path ;
    }
	public  String getString(String s) {
		if (p == null) {
			/*String cfgLocation = System.getProperty("Configure");
			if (cfgLocation==null || cfgLocation.length()<1){
				cfgLocation=System.getProperty("user.dir");
			}*/
			String cfgLocation= path; //"\\WebRoot\\WEB-INF\\jdbc.properties";
			try {
				InputStream in = new BufferedInputStream(new FileInputStream(
						cfgLocation));
				p = new Properties();
				p.load(in);
				in.close();
				return p.getProperty(s);
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
				p.clear();
				p=null;
				return null;
			}catch (IOException ee){
				System.out.println(ee);
				p.clear();
				p=null;
				return null;
			}
		}else{
			return p.getProperty(s,"exit");
		}
	}
	public static void closeCfg(){
		if (p!=null){
			p.clear();
			p=null;
		}
	}

}
