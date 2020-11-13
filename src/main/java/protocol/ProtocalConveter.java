package protocol;/**
 * @Auther: Allan
 * @Date: 2020/8/2 23:34
 * @Description:
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import util.JsonUtil;

import javax.xml.ws.Response;

/**
 * @ClassName ProtocalConveter
 * @Date:2020/8/2 23:34
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class ProtocalConveter {

    public static FullHttpRequest rpc2fullRequest(RpcHttpRequest rpcRequest){
        String json = JsonUtil.objectToJson(rpcRequest.getParameters());
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                rpcRequest.getUrl(),
                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        return request;
    }



    public static RpcHttpResponse full2rpcResponse(FullHttpResponse fullHttpResponse){
        RpcHttpResponse rpcResponse = new RpcHttpResponse();
        String content = fullHttpResponse.content().toString(CharsetUtil.UTF_8);
        if("" != content){
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(content);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            rpcResponse.setSeqid(jsonNode.get("seqid").toString().replace("\"",""));
            rpcResponse.setStatus(jsonNode.get("status").toString().replace("\"",""));
            rpcResponse.setMsg(jsonNode.get("msg").toString().replace("\"",""));
            String data = jsonNode.get("data").toString();
            if(data != null && data.length()!=0){
                String type = jsonNode.get("type").toString().replace("\"","");
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(type);
                    rpcResponse.setData(JsonUtil.jsonToObject(data,clazz));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
        return rpcResponse;
    }
}
