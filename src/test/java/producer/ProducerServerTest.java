package producer;

import client.HelloService;
import org.junit.Test;
import registry.ServiceRegistry;

import static org.junit.Assert.*;

/*
//请求的形式
GET /server/helloService/arg1?appid=1&timestamp=1231231&sig=asdasd&seqid=123 HTTP/1.1
Host: 127.0.0.1:8001
Content-Type: application/json

["allan"]
 */

/**
 * @Auther: Allan
 * @Date: 2020/8/8 22:19
 * @Description:
 */
public class ProducerServerTest {
    @Test
    public void producerTest(){
        ServiceRegistry serviceRegistry = new ServiceRegistry("127.0.0.1:2181","127.0.0.1:8002","testService","testNode");
        ProducerServer producerServer = new ProducerServer("127.0.0.1:8002",serviceRegistry);
        HelloService helloService = new HelloServiceImpl();
        producerServer.addProducer("helloService",helloService);
        try {
            producerServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}