package protocol;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.junit.Test;
import org.w3c.dom.ls.LSException;
import util.JsonUtil;

import static org.junit.Assert.*;

/**
 * @Auther: Allan
 * @Date: 2020/8/4 00:25
 * @Description:
 */
public class ProtocalConveterTest {

    @Test
    public void rpc2fullRequest() {
        Object[] paramaters = new Object[1];

        paramaters[0] = new TestEntry(1,2);
        RpcHttpRequest request = new RpcHttpRequest("1","2","3",paramaters);
        FullHttpRequest fullRequest = ProtocalConveter.rpc2fullRequest(request);
//        System.out.println(JsonUtil.objectToJson(request));
//        System.out.println(JsonUtil.objectToJson(fullRequest));
//        System.out.println(fullRequest.getUri());
        System.out.println(fullRequest.content().toString(CharsetUtil.UTF_8));
        RpcHttpRequest o = JsonUtil.jsonToObject(JsonUtil.objectToJson(request), RpcHttpRequest.class);
//        System.out.println(o);
    }

    @Test
    public void full2rpcRequest() {
        RpcHttpResponse rpcResponse = new RpcHttpResponse();
        rpcResponse.setType(TestEntry.class.getTypeName());
        rpcResponse.setMsg("ok");
        rpcResponse.setSeqid("123456");
        rpcResponse.setData(new TestEntry(1,2));
        rpcResponse.setStatus("200");
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(
                        JsonUtil.objectToJson(rpcResponse),
                        CharsetUtil.UTF_8)
        );
        RpcHttpResponse rpcHttpResponse = ProtocalConveter.full2rpcResponse(response);
        System.out.println(JsonUtil.objectToJson(rpcHttpResponse));
    }
}