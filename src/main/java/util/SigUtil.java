package util;/**
 * @Auther: Allan
 * @Date: 2020/8/2 22:32
 * @Description:
 */

import java.security.MessageDigest;

/**
 * @ClassName SigUtil
 * @Date:2020/8/2 22:32
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class SigUtil {

    public static String createSig(String appid , String appkey , String timestamp ,String seqid,String data){
        String s = appid+appkey+timestamp+seqid+data;
        return createMD5(s);
    }

    public static String createMD5(String message) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = message.getBytes();
            byte[] buff = md.digest(input);
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }

}
