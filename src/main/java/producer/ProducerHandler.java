package producer;

import config.AppConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import net.sf.cglib.reflect.FastClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.ProtocalConveter;
import protocol.RpcHttpRequest;
import protocol.RpcHttpResponse;
import util.JsonUtil;
import util.RequestUtil;
import util.SigUtil;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * RPC Handler（RPC request processor）
 *
 * @author luxiaoxun
 */
public class ProducerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ProducerHandler.class);

    private final Map<String, Object> handlerMap;

    public ProducerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        FullHttpRequest request = (FullHttpRequest)msg;
        ProducerServer.submit(new Runnable() {
            @Override
            public void run() {
                boolean hasError = false;

                RpcHttpResponse response = new RpcHttpResponse();

                Map<String, String> requestPara = RequestUtil.getRequestPara(request);
                response.setSeqid(requestPara.get("seqid"));
                response.setStatus("0");
                response.setMsg("ok");

                logger.debug("request : {}",request.toString());

                // 校验sig和timestamp
                if (!AppConfig.debug && !requestPara.get("sig").equals(
                        SigUtil.createSig(
                                requestPara.get("appid"),
                                AppConfig.appkey,
                                requestPara.get("timestamp"),
                                requestPara.get("seqid"),
                                "")
                )){

                    logger.error("request sig error: {}" , request.toString() );
                    hasError = true;
                    response.setStatus("1");
                    response.setMsg("request sig error!");
                }
                long costTime = System.currentTimeMillis() - Integer.parseInt(requestPara.get("timestamp"));
                logger.debug("Receive request cost time: {}" , costTime);
                if (!AppConfig.debug && costTime > AppConfig.requestTimeout){

                    logger.error("request timeout error: {}" , request.toString() );
                    hasError = true;
                    response.setStatus("2");
                    response.setMsg("request timeout!");
                }

                logger.debug("Receive request , seqid :" + requestPara.get("seqid"));

                if(!hasError){
                    try {
                        Object result = handle(requestPara);
                        response.setData(result);
                    } catch (Throwable t) {
                        response.setStatus("100");
                        response.setMsg("RPC Server handle request error."+t.getMessage());
                        logger.error("RPC Server handle request error : {}",t.getMessage(), t);
                    }
                }

                FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(
                                JsonUtil.objectToJson(response),
                                CharsetUtil.UTF_8
                        )
                );
                ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
//                ctx.writeAndFlush(httpResponse).addListener(new ChannelFutureListener() {
//                    @Override
//                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                       logger.debug("Send response for request " + requestPara.get("seqid"));
//                    }
//                });
            }
        });
    }

    private Object handle(Map<String,String> requestPara) throws Throwable {
        String className = requestPara.get("class");
        String methodName = requestPara.get("method");

        Object serviceBean = handlerMap.get(className);
        if(serviceBean == null){
            throw new Exception("no such class as \'" + className + "\'in this producer" );
        }
        Class<?> serviceClass   = serviceBean.getClass();

        Method[] methods = serviceClass.getDeclaredMethods();
        Method method = null;
        for (Method m:methods){
            if (m.getName().equals(methodName) ){
                method = m;
                break;
            }
        }
        if(method == null){
            throw new Exception("no such method as \'" + methodName + "\'in \'" + className + "\'");
        }
        Class<?>[] parameterTypes = method.getParameterTypes();

        // 分割json中的结果
        String[] parajson ;
        try {
            parajson = JsonUtil.jsonToJsonArray(requestPara.get("data"));
            if(parajson == null){
                parajson = new String[0];
            }
        }catch (Throwable t){
            logger.error("json to para array ,json: {} ",requestPara.get("data"));
            throw new Exception("json to para array");
        }

        // 判断json中长度是否于参数长度相同
        if(parajson.length != parameterTypes.length){
            logger.error("para lenth error,json len:{} , para len:{} ",parajson.length,parameterTypes.length);
            throw new Exception("para lenth error");
        }

        Object[] parameters = new Object[parajson.length];
        for (int i = 0; i < parajson.length; i++) {
            try {
                parameters[i] = JsonUtil.jsonToObject(parajson[i],parameterTypes[i]);
            }catch (Throwable t){
                logger.error("json to object error ,json: {} ,type: {} ",parajson[i],parameterTypes[i].getName());
                throw new Exception("json to object error");
            }
        }

        // 参数反序列化,获取参数名/获取参数名对应的json/获取参数名所对应的类型/json to object

        logger.debug(serviceClass.getName());
        logger.debug(methodName);
        for (int i = 0; i < parameterTypes.length; ++i) {
            logger.debug(parameterTypes[i].getName());
        }
        for (int i = 0; i < parameters.length; ++i) {
            logger.debug(parameters[i].toString());
        }

        // Cglib reflect
        FastClass serviceFastClass = FastClass.create(serviceClass);
        int methodIndex = serviceFastClass.getIndex(methodName, parameterTypes);
        return serviceFastClass.invoke(methodIndex, serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server caught exception", cause);
        ctx.close();
    }
}
