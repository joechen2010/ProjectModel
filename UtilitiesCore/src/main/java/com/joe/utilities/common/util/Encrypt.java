package com.joe.utilities.common.util;

import javax.crypto.*;
import javax.crypto.spec.*;
import sun.misc.*;

public class Encrypt {
	String key = "dddddddd";


    private byte[] encoder(String message) throws Exception {

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));




        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }


    private byte[] decoder(byte[] src) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        DESKeySpec dks = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey, iv);
        return cipher.doFinal(src);
    }


/*    public String desAndBase64Encoder(String str) throws Exception {
        BASE64Encoder en = new BASE64Encoder();
        byte[] desEncoder = encoder(str);
        String result = en.encode(desEncoder);
        return result;
    }

    public String desAndBase64Decoder(String str) throws Exception {
        BASE64Decoder dn = new BASE64Decoder();
        byte[] base64Decoder = dn.decodeBuffer(str);
        byte[] a = decoder(base64Decoder);
        String result = new String(a, "UTF-8");
        return result;
    }*/
    
    public static void main(String[] arg){
    	
    }
}
