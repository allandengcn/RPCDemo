package protocol;/**
 * @Auther: Allan
 * @Date: 2020/8/3 00:24
 * @Description:
 */

/**
 * @ClassName RpcHttpResponse
 * @Date:2020/8/3 0:24
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class RpcHttpResponse {
    private String status;
    private String msg;
    private String seqid;
    private String type;
    private Object data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSeqid() {
        return seqid;
    }

    public void setSeqid(String seqid) {
        this.seqid = seqid;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
