package util;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @Auther: Allan
 * @Date: 2020/8/2 23:26
 * @Description:
 */
public class SigUtilTest {

    @Test
    public void createSig() {
        String uuid = UUID.randomUUID().toString();
        String time = String.valueOf(System.currentTimeMillis());
        System.out.println(uuid);
        System.out.println(time);
        System.out.println(SigUtil.createSig("appid","appkey",time,uuid,"data"));
    }
}