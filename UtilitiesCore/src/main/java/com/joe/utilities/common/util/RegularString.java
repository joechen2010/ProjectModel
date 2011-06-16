package com.joe.utilities.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularString {
	 /**
	   * ƥ�������ַ��������ʽ
	   */
	  public static final String CHINESE = "[\\u4e00-\\u9fa5]";
	  
	  /**
	   * ƥ��˫�ֽ��ַ�(��(��������)��
	   */
	  public static final String DOUBLECHINESE = "[^\\x00-\\xff]";

	  /**
	   * ��֤����
	   */
	  public static final String AGE = "^[1-9]\\d?$";

	  /**
	   * ���Ļ�Ӣ��
	   */
	  public static final String CNOREN = "^([\\u4e00-\\u9fa5]{2,})$|^([a-zA-Z0-9]{4,})$";

	  /**
	   * ƥ��HTML��ǵ�������ʽ��
	   */
	  public static final String HTML = "<(\\S*?)[^>]*>.*?</\\1>|<.*? />";

	  /**
	   * ƥ����β�հ��ַ��������ʽ��
	   * ��ע��������4ɾ��������β�Ŀհ��ַ�(��(�ո��Ʊ��ҳ��ȵ�)���ǳ����õı��ʽ
	   */
	  public static final String BESPACE = "^\\s*|\\s*$";

	  /**
	   * ƥ��Email��ַ��������ʽ��
	   */
	  public static final String EMAIL =
	      "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

	  /**
	   * ƥ����ַURL��������ʽ��
	   * ������İ汾���ܺ����ޣ��������������������
	   */
	  public static final String URL = "[a-zA-z]+://[^\\s]*";

	  /**
	   * ƥ���ʺ��Ƿ�Ϸ�(��ĸ��ͷ������5-16�ֽڣ�������ĸ�����»���)��
	   */
	  public static final String ACCOUNT = "^[a-zA-Z][a-zA-Z0-9_]{2,15}$";

	  /**
	   * ƥ����ڹ̶��绰���룺
	   */
	  public static final String TELPHONE = "^\\d{3}-\\d{8}|\\d{4}-\\d{7,8}$";

	  /**
	   * ��ѶQQ�Ŵ�10000��ʼ
	   */
	  public static final String QQNUMBER = "^[1-9][0-9]{4,}$";

	  /**
	   * ƥ���ֻ���룺
	   */
	  public static final String PHONE = "^[1][3]\\d{9}|[1][5][7-9]\\d{8}$";

	  /**
	   * ƥ���й��������룺
	   */
	  public static final String ZIPCODE = "^[1-9]\\d{5}(?!\\d)$";

	  /**
	   * ƥ�����֤��
	   */
	  public static final String IDCARDNO = "^\\d{15}|\\d{18}$";

	  /**
	   * ƥ��ip��ַ��
	   */
	  public static final String IPADDRESS = "\\d+\\.\\d+\\.\\d+\\.\\d+";

	  /**
	   * ƥ����26��Ӣ����ĸ��ɵ��ַ�
	   */
	  public static final String LETTER = "^[A-Za-z]+$";

	  /**
	   * ƥ����26��Ӣ����ĸ�Ĵ�д��ɵ��ַ�
	   */
	  public static final String UPPERCASE = "^[A-Z]+$";

	  /**
	   * ƥ����26��Ӣ����ĸ��Сд��ɵ��ַ�
	   */
	  public static final String LOWERCASE = "^[a-z]+$";

	  /**
	   * ƥ��������ַ�
	   */
	  public static final String SIXNUM = "^\\d{6}$";

	  /**
	   * ƥ�������ֺ�26��Ӣ����ĸ��ɵ��ַ�
	   */
	  public static final String LETTER_DIGITAL = "^[A-Za-z0-9]+$";	  
		  
	  /**
	   * ��֤���ʽ��ʻ���ŷǸ������(0
	   */                                 //  ^[1-9]+\d*$
	  public static final String ACCOUNTID="^[1-9]+\\d*$";
	  
	  /**
	   * ��֤����
	   * 6������
	   * */
	   public static final String pwd = "^\\d{6}$";
	  /**
	   * ��֤���
	   * */                                 // ^\d+(\.\d+)?$
	   public static final String money = "^[1-9]+\\d*(\\.\\d+)?$";
	    /**
	     * �ǿ��ַ� 
	     * **/
	   public static final String Null="^$";
	   /**
	    * ��֤�û���2-5����
	    * */
	   public static final String CUSTOMERNAME = "^[\u4e00-\u9fa5]{2,5}$";
	   /**
	    * ��֤�ֻ����
	    * **/
	   public static final String Phone = "^(13|15)\\d{9}$";
	   /**
	    * ��֤18λ�����֤����
	    * **/
	   public static final String CUSTOMERID="^\\d{18}$";
	   /**
	    * ��֤��ַ  \w{10,50}$  �д����
	    * **/
	   public static final String ADDRESS="^([\\u4e00-\\u9fa5]{2,})$|^([a-zA-Z0-9]{4,})$";
	   
	   
	   
	   /**
		   * 提取text中的电话和手机号码
		   * @param text
		   * @return
		   */
	   public static List<String> pickUp(String text) {
			  
			  List<String> list = new ArrayList<String>();
			Pattern pattern = Pattern.compile("(?<!\\d)(?:(?:1[35]\\d{9})|(?:0[1-9]\\d{1,2}-?\\d{7,8}))(?!\\d)");
		    Matcher matcher = pattern.matcher(text);
		    StringBuffer bf = new StringBuffer(64);
		    while (matcher.find()) {
		      bf.append(matcher.group()).append(",");
		    }
		    int len = bf.length();
		    if (len > 0) {
		      bf.deleteCharAt(len - 1);
		    }
		    String o[] =  bf.toString().split(",");
		    for (int i = 0; i < o.length; i++) {
				list.add(o[i]);
			}
		    return list;
		  }

}
