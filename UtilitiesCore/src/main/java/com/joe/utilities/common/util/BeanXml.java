package com.joe.utilities.common.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * 
 * Bean与Xml转换方法类
 * 
 * @author rq2.
 * @version 1.0.0, 2007-09-14
 * @see
 * @since
 * 
 */
public class BeanXml {

	private static Logger logger = Logger.getLogger("BeanXml");

	/**
	 * Bean转换成Xml
	 * 
	 * @param object
	 * @return
	 */
	public static String Bean2Xml(Object object) {
		String beanXml = "";
		ByteArrayOutputStream out = null;
		XMLEncoder encoder = null;
		try {
			out = new ByteArrayOutputStream();
			encoder = new XMLEncoder(out);
			// 对象序列化输出到XML文件
			encoder.writeObject(object);
			encoder.flush();
			// 关闭序列化工具
			encoder.close();
			// 关闭输出流
			out.flush();
			beanXml = new String(out.toByteArray(), "utf-8");
			out.close();
		} catch (Exception e) {
			logger.error("Bean2Xml转换失败");
		} finally {
			if (encoder != null) {
				encoder.close();
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("Bean2Xml转换失败");
				}
			}
		}
		return beanXml;
	}

	/**
	 * Xml转换成Object
	 * 
	 * @param xml
	 * @return
	 */
	public static Object Xml2Bean(String xml) {
		Object object = null;
		ByteArrayInputStream input = null;
		XMLDecoder decoder = null;
		try {
			input = new ByteArrayInputStream(xml.getBytes("utf-8"));
			decoder = new XMLDecoder(input);
			object = decoder.readObject();
			input.close();
			decoder.close();
		} catch (Exception e) {
			logger.error("Xml2Bean转换失败");
		} finally {
			if (decoder != null) {
				decoder.close();
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("Xml2Bean转换失败");
				}
			}
		}
		return object;
	}
	
}
