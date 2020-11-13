package util;/**
 * @Auther: Allan
 * @Date: 2020/8/9 00:16
 * @Description:
 */

import io.netty.buffer.ByteBuf;

/**
 * @ClassName ByteBufUtil
 * @Date:2020/8/9 0:16
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class ByteBufUtil {

    public static ByteBuf copyContent(ByteBuf byteBuf){
        // 引用计数加一后复制buf
        byteBuf.retain();
        return byteBuf.duplicate();
    }
}
