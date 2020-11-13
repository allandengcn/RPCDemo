package consumer.proxy;


import consumer.RPCFuture;

/**
 * Created by luxiaoxun on 2016/3/16.
 */
public interface AsyncObjectProxy {
    public RPCFuture call(String funcName, Object... args);
}