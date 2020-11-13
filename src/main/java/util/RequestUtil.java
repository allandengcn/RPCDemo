package util;/**
 * @Auther: Allan
 * @Date: 2020/8/8 18:44
 * @Description:
 */

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName RequestUtil
 * @Date:2020/8/8 18:44
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class RequestUtil {

    public static Logger logger = LoggerFactory.getLogger(RequestUtil.class);
    private static Set<String> set = new HashSet<>();
    static {
        set.add("appid");
        set.add("timestamp");
        set.add("seqid");
        set.add("sig");
    }

    public static Map<String,String> getRequestPara(FullHttpRequest request){
        Map<String,String> res = new HashMap<>();;
        String uri = request.uri();
        ByteBuf byteBuf = ByteBufUtil.copyContent(request.content());
        String data = byteBuf.toString(CharsetUtil.UTF_8);
        String[] uriSplit = uri.split("/");
        if(uriSplit.length != 4){
            logger.error("uri convert error : {}",uri);
            throw new RuntimeException("uri convert error");
        }

        String[] uriSplit2 = uriSplit[3].split("\\?");
        if(uriSplit2.length != 2){
            logger.error("uri convert error : {}",uri);
            throw new RuntimeException("uri convert error");
        }

        String[] paras = uriSplit2[1].split("&");
        if(paras.length != 4){
            logger.error("uri convert error : {}",uri);
            throw new RuntimeException("uri convert error");
        }

        res.put("server",uriSplit[1]);
        res.put("class",uriSplit[2]);
        res.put("method",uriSplit2[0]);

        for(String s:paras){
            String[] split = s.split("=");
            if(split.length != 2){
                logger.error("uri convert para error : {}",uri);
                throw new RuntimeException("uri convert error");
            }

            if(set.contains(split[0])){
                if(res.containsKey(split[0])){
                    logger.error("uri convert para name error : {}",uri);
                    throw new RuntimeException("uri convert error");
                }else{
                    res.put(split[0],split[1]);
                }
            }else{
                logger.error("uri convert para name error : {}",uri);
                throw new RuntimeException("uri convert error");
            }
        }

        res.put("data",data);

        return res;
    }


}
