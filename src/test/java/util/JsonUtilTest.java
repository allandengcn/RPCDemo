package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import protocol.Paramater;
import protocol.RpcHttpRequest;
import protocol.RpcHttpResponse;
import protocol.TestEntry;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @Auther: Allan
 * @Date: 2020/8/3 00:10
 * @Description:
 */
public class JsonUtilTest {

    @Test
    public void serialize() {
        String test = "test";
        System.out.println(JsonUtil.serialize(test));
    }

    @Test
    public void deserialize() {
    }

    @Test
    public void jsonToObject() {
    }

    @Test
    public void jsonToObjectList() {
    }

    @Test
    public void jsonToObjectHashMap() {
    }

    @Test
    public void objectToJson() {
        Paramater[] paramaters = new Paramater[1];
        paramaters[0] = new Paramater("a",new TestEntry(1,2));
        RpcHttpRequest request = new RpcHttpRequest("1","2","3",paramaters);
        String test = "test";
        Map<String,Object> map = new HashMap<>();
        map.put("testkey",request);
        List<Paramater> list = new ArrayList<>();
        list.add(new Paramater("a",request));
        list.add(new Paramater("b","123456"));
        System.out.println(JsonUtil.objectToJson(test));
        System.out.println(JsonUtil.objectToJson(request));
        System.out.println(JsonUtil.objectToJson(map));
        System.out.println(JsonUtil.objectToJson(list).toLowerCase());

        RpcHttpResponse response = new RpcHttpResponse();
        response.setData(new TestEntry(2,3));
        response.setMsg("ok");
        response.setStatus("666");
        response.setSeqid("666qweq");
        System.out.println(JsonUtil.objectToJson(response));


    }

    @Test
    public void jsonToJsonArray() throws JsonProcessingException {
        List<TestEntry> list = new ArrayList<>();
        list.add(new  TestEntry(1,2));
        list.add(new  TestEntry(3,4));
        System.out.println(Arrays.toString(JsonUtil.jsonToJsonArray(JsonUtil.objectToJson(list))));
    }
}