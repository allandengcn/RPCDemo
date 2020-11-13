package client;/**
 * @Auther: Allan
 * @Date: 2020/11/5 19:48
 * @Description:
 */

import consumer.ConsumerClient;
import org.junit.Test;
import producer.HelloServiceImpl;
import registry.ServiceDiscovery;

/**
 * @ClassName ConsumerClientTest
 * @Date:2020/11/5 19:48
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class ConsumerClientTest {
    @Test
    public void consumerTest(){
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
        ConsumerClient consumer = new ConsumerClient(serviceDiscovery);

        HelloService helloService = consumer.create(HelloService.class);
        for(int i = 0 ; i < 100 ; i++){
            String str = helloService.arg1("allan");
            System.out.println(str);
        }

    }
}
