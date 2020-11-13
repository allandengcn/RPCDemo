package protocol;/**
 * @Auther: Allan
 * @Date: 2020/8/3 00:25
 * @Description:
 */

/**
 * @ClassName Paramater
 * @Date:2020/8/3 0:25
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class Paramater {
    private String name;
    private Object para;

    public Paramater(String name, Object para) {
        this.name = name;
        this.para = para;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPara() {
        return para;
    }

    public void setPara(Object para) {
        this.para = para;
    }
}
