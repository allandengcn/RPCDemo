package consumer.proxy;


import consumer.ConnectManage;
import consumer.ConsumerHandler;
import consumer.RPCFuture;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.ProtocalConveter;
import protocol.RpcHttpRequest;
import util.JsonUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class ObjectProxy<T> implements InvocationHandler, AsyncObjectProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectProxy.class);
    private Class<T> clazz;

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }


    // InvocationHandler的方法，同步调用时使用
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 判断调用的是不是object类的方法，如equals hashcode toString 这些方法不进行远程调用，直接本地处理之后返回
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        // Debug,输出类名、方法名、参数的类型、参数值
        LOGGER.debug(method.getDeclaringClass().getName());
        LOGGER.debug(method.getName());
        for (int i = 0; i < method.getParameterTypes().length; ++i) {
            LOGGER.debug(method.getParameterTypes()[i].getName());
        }
        for (int i = 0; i < args.length; ++i) {
            LOGGER.debug(args[i].toString());
        }

        // todo 需要修改路由方案
        // 决定调用那个service
        ConsumerHandler handler = ConnectManage.getInstance().chooseHandler();
        // todo 修改为创建RpcHttpRequest再转换为FullHttpRequest
        // 构造请求
        RpcHttpRequest request = createRequest(this.clazz.getSimpleName().toLowerCase(), method.getName(), args);
        // 发送请求
        RPCFuture rpcFuture = handler.sendRequest(request);
        // 等待调用返回
        // todo 增加超时处理
        return rpcFuture.get();
    }


    // AsyncObjectProxy的方法，异步调用时使用。
    // 与同步调用基本相同，只不过同步调用会阻塞等待结果。异步调用不会
    @Override
    public RPCFuture call(String funcName, Object... args) {
        // todo 需要修改路由方案
        // 决定调用那个service
        ConsumerHandler handler = ConnectManage.getInstance().chooseHandler();
        // todo 修改为创建RpcHttpRequest再转换为FullHttpRequest
        // 构造请求
        RpcHttpRequest request = createRequest(this.clazz.getSimpleName().toLowerCase(), funcName, args);
        // 发送请求
        RPCFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture;
    }

    private RpcHttpRequest createRequest(String className, String methodName, Object[] args) {
        LOGGER.debug("call class:{} , method:{} , simplename:{}",className,methodName,this.clazz.getSimpleName().toLowerCase());
        RpcHttpRequest rpcRequest = new RpcHttpRequest("server",className,methodName);
        // todo 生成request的请求体的地方 需要把参数列表转换成json
        //rpcRequest.setData("data");
        rpcRequest.setParameters(args);

        // todo 转换RpcHttpRequest成FullHttpRequest
        return rpcRequest;
//        FullHttpRequest request = ProtocalConveter.rpc2fullRequest(rpcRequest);
//        LOGGER.debug("requset content: {} " , request.content().toString(CharsetUtil.UTF_8));
//        return request;
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }
        return classType;
    }
}
