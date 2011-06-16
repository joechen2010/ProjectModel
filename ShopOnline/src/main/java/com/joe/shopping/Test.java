package com.joe.shopping;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Test {
	public static void main(String[] args) {
		encryptByMD5("111111");
	}
	
	public static String encryptByMD5(String plainText) {  
        StringBuffer buf = new StringBuffer("");  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(plainText.getBytes());  
            byte b[] = md.digest();  
  
            int i;  
  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
                if (i < 0)  
                    i += 256;  
                if (i < 16)  
                    buf.append("0");  
                buf.append(Integer.toHexString(i));  
            }  
  
            System.out.println("result: " + buf.toString());
  
            System.out.println("result: " + buf.toString().substring(8, 24));
  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
  
        return buf.toString().substring(8, 24);  
    }  
  
	

}
