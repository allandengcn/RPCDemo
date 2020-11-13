package consumer;

/**
 * 回调函数
 */
public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);

}
