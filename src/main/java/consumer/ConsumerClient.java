package consumer;



import consumer.proxy.AsyncObjectProxy;
import consumer.proxy.ObjectProxy;
import registry.ServiceDiscovery;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Rpc Consumer Client
 */
public class ConsumerClient {
    // todo 整理 consumer的调用关系和时序
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));


    // 创建client的方式有2种，直接指明服务提供方地址，或，知名zk的地址
    public ConsumerClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public ConsumerClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    // 此处的同步调用和异步调用表示：调用后是阻塞等待返回结果，还是自行检查是否处理完成

    // 生成一个同步调用的代理类，使用proxy创建代理
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                // ObjectProxy对象实现代理的相关操作
                new ObjectProxy<T>(interfaceClass)
        );
    }

    //生成一个异步调用的代理类，没有对接口创建代理，只返回了一个ObjectProxy对象
    public static <T> AsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass);
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public void stop() {
        threadPoolExecutor.shutdown();
        serviceDiscovery.stop();
        ConnectManage.getInstance().stop();
    }
}

