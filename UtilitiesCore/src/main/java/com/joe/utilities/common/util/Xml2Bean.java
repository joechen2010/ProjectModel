package com.joe.utilities.common.util;

/**
 * <p>标题: Xml2Bean.java</p>
 * <p>功能描述: 用于将xml转换成bean<br>
 *
 * </p>
 * <p>版权: Copyright (c) 2007</p>
 * <p>公司: </p>
 * @author fbysss 
 * @version 1.0
 * 
 * </p>
 * <p>修改记录：</p>
 * 创建时间：2007-6-23 13:19:39
 * 类说明：要求jdk1.5版本以上。
 */
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class Xml2Bean {
    public List getObjects(String xml,Object obj){
        String beanName = obj.getClass().getSimpleName();
        StringReader xmlReader = new StringReader(xml);
        Digester digester = new Digester();
        digester.setValidating(true);
        digester.addObjectCreate("beans", ArrayList.class);    
        digester.addObjectCreate("beans/"+beanName ,obj.getClass());
        digester.addSetProperties("beans/"+beanName);     
        addSetters(obj, beanName, digester);
        digester.addSetNext("beans/"+beanName, "add");
        ArrayList beans = null;
        try {
            beans = (ArrayList) digester.parse(xmlReader);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return beans;
    }
    /** *//***
     * 一次性添加bean属性的Setter.否则就得一个一个的添加，很费劲。（也许digester有这个功能，但我没找到，只好自己写一个方法了）
     * @param obj
     * @param beanName
     * @param digester
     */
    private void addSetters(Object obj,String beanName, Digester digester) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            digester.addBeanPropertySetter("beans/"+beanName+"/"+field.getName());
        }        
        /**//*digester.addBeanPropertySetter("beans/"+beanName+"/mid");
        digester.addBeanPropertySetter("beans/"+beanName+"/mclass");
        digester.addBeanPropertySetter("beans/"+beanName+"/mname");
        */
    }
    
}

