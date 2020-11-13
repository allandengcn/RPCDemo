package producer;

import client.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String arg1(String name) {
        return name;
    }

    @Override
    public String hello() {
        return "hello my firend";
    }
}
