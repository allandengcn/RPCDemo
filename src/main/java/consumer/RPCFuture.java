package consumer;


import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用于处理异步请求的Future，异步转同步
 */
public class RPCFuture implements Future<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RPCFuture.class);

    private Sync sync;
    private FullHttpRequest request;
    private FullHttpResponse response;
    private long startTime;
    private long responseTimeThreshold = 5000;

    private List<AsyncRPCCallback> pendingCallbacks = new ArrayList<AsyncRPCCallback>();
    private ReentrantLock lock = new ReentrantLock();

    public RPCFuture(FullHttpRequest request) {
        this.sync = new Sync();
        this.request = request;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        // get的时候获取锁，获取到之后释放锁
        sync.acquire(-1);
        if (this.response != null) {
            return this.response.content();
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.response != null) {
                return this.response.content();
            } else {
                return null;
            }
        } else {
            // todo 补充请求中的RequestId ClassName MethodName
            throw new RuntimeException();
//            throw new RuntimeException("Timeout exception. Request id: " + this.request.getRequestId()
//                    + ". Request class name: " + this.request.getClassName()
//                    + ". Request method: " + this.request.getMethodName());
        }
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public void done(FullHttpResponse reponse) {
        this.response = reponse;
        sync.release(1);
        invokeCallbacks();
        // Threshold
        long responseTime = System.currentTimeMillis() - startTime;
        // 处理响应时已经超时
        if (responseTime > this.responseTimeThreshold) {
            // todo 修改requestid
            // logger.warn("Service response time is too slow. Request id = " + reponse.getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    private void invokeCallbacks() {
        lock.lock();
        try {
            // 调用回调函数
            for (final AsyncRPCCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    public RPCFuture addCallback(AsyncRPCCallback callback) {
        lock.lock();
        // 如果请求时已经收到响应则直接执行callback
        try {
            if (isDone()) {
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }


    private void runCallback(final AsyncRPCCallback callback) {
        final FullHttpResponse res = this.response;
        ConsumerClient.submit(new Runnable() {
            @Override
            public void run() {
                // todo 添加 回调函数处理逻辑
//                if (!res.isError()) {
//                    callback.success(res.getResult());
//                } else {
//                    callback.fail(new RuntimeException("Response error", new Throwable(res.getError())));
//                }
            }
        });
    }

    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            // 尝试获取锁时，判断state是否为1
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            // 尝试释放锁时，判断state是否为0
            if (getState() == pending) {
                // 如果挂起，用cas修改状态为done
                if (compareAndSetState(pending, done)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        protected boolean isDone() {
            return getState() == done;
        }
    }
}
