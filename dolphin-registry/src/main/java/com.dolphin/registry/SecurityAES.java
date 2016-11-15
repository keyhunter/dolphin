package com.dolphin.registry;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SecurityAES {
    private final static String encoding = "UTF-8";

    /**
     * AES\u52A0\u5BC6
     * 
     * @param content
     * @param password
     * @return
     */
    public static String encryptAES(String content, String password) {
        byte[] encryptResult = encrypt(content, password);
        String encryptResultStr = parseByte2HexStr(encryptResult);
        // BASE64\u4F4D\u52A0\u5BC6
        encryptResultStr = ebotongEncrypto(encryptResultStr);
        return encryptResultStr;
    }

    /**
     * AES\u89E3\u5BC6
     * 
     * @param encryptResultStr
     * @param password
     * @return
     */
    public static String decrypt(String encryptResultStr, String password) {
        // BASE64\u4F4D\u89E3\u5BC6
        String decrpt = ebotongDecrypto(encryptResultStr);
        byte[] decryptFrom = parseHexStr2Byte(decrpt);
        byte[] decryptResult = decrypt(decryptFrom, password);
        return new String(decryptResult);
    }

    /**
    * \u52A0\u5BC6\u5B57\u7B26\u4E32
    */
    public static String ebotongEncrypto(String str) {
        BASE64Encoder base64encoder = new BASE64Encoder();
        String result = str;
        if (str != null && str.length() > 0) {
            try {
                byte[] encodeByte = str.getBytes(encoding);
                result = base64encoder.encode(encodeByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //base64\u52A0\u5BC6\u8D85\u8FC7\u4E00\u5B9A\u957F\u5EA6\u4F1A\u81EA\u52A8\u6362\u884C \u9700\u8981\u53BB\u9664\u6362\u884C\u7B26
        return result.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
    }

    /**
     * \u89E3\u5BC6\u5B57\u7B26\u4E32
     */
    public static String ebotongDecrypto(String str) {
        BASE64Decoder base64decoder = new BASE64Decoder();
        try {
            byte[] encodeByte = base64decoder.decodeBuffer(str);
            return new String(encodeByte);
        } catch (IOException e) {
            e.printStackTrace();
            return str;
        }
    }

    /**  
     * \u52A0\u5BC6  
     *   
     * @param content \u9700\u8981\u52A0\u5BC6\u7684\u5185\u5BB9  
     * @param password  \u52A0\u5BC6\u5BC6\u7801  
     * @return  
     */
    private static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            //\u9632\u6B62linux\u4E0B \u968F\u673A\u751F\u6210key
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);
            //kgen.init(128, new SecureRandom(password.getBytes()));   
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// \u521B\u5EFA\u5BC6\u7801\u5668   
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// \u521D\u59CB\u5316   
            byte[] result = cipher.doFinal(byteContent);
            return result; // \u52A0\u5BC6   
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**\u89E3\u5BC6  
     * @param content  \u5F85\u89E3\u5BC6\u5185\u5BB9  
     * @param password \u89E3\u5BC6\u5BC6\u94A5  
     * @return  
     */
    private static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            //\u9632\u6B62linux\u4E0B \u968F\u673A\u751F\u6210key
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);
            //kgen.init(128, new SecureRandom(password.getBytes()));   
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// \u521B\u5EFA\u5BC6\u7801\u5668   
            cipher.init(Cipher.DECRYPT_MODE, key);// \u521D\u59CB\u5316   
            byte[] result = cipher.doFinal(content);
            return result; // \u52A0\u5BC6   
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**\u5C06\u4E8C\u8FDB\u5236\u8F6C\u6362\u621016\u8FDB\u5236  
     * @param buf  
     * @return  
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**\u5C0616\u8FDB\u5236\u8F6C\u6362\u4E3A\u4E8C\u8FDB\u5236  
     * @param hexStr  
     * @return  
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(encrypt("yunjee0515ueopro1234", "yunjee"));
    }

}