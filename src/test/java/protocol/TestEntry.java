package protocol;/**
 * @Auther: Allan
 * @Date: 2020/8/4 00:26
 * @Description:
 */

/**
 * @ClassName TestEntry
 * @Date:2020/8/4 0:26
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class TestEntry {
    private int a ;

    private int b ;

    public int getA() {
        return a;
    }


    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public TestEntry() {
    }

    public TestEntry(int a, int b) {
        this.a = a;
        this.b = b;
    }
}
