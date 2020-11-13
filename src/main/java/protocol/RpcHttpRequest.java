package protocol;/**
 * @Auther: Allan
 * @Date: 2020/8/2 22:22
 * @Description:
 */


import config.AppConfig;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import static util.SigUtil.createSig;

/**
 * @ClassName RpcHttpRequest
 * @Date:2020/8/2 22:22
 * @Description:
 * @Author: Allan Deng
 * @Version: 1.0
 **/
public class RpcHttpRequest {

    private String appid ;

    private String timestamp;

    private String seqid ;

    private String sig ;

    private String serverName ;

    private String className ;

    private String methodName ;

    private Object[] parameters;

    public RpcHttpRequest() {
    }

    public RpcHttpRequest(String serverName, String className, String methodName) {
        this(serverName,className,methodName,null);
    }

    public RpcHttpRequest(String serverName, String className, String methodName , Object[] parameters) {
        this.appid = AppConfig.appid;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.seqid = UUID.randomUUID().toString();
        this.parameters = parameters;
        this.sig = createSig(appid,AppConfig.appkey,timestamp,seqid,"");
        this.serverName = serverName;
        this.className = className;
        this.methodName = methodName;
    }

    public String getUrl(){
        StringBuffer sb = new StringBuffer();
        sb.append("/");
        sb.append(serverName);
        sb.append("/");
        sb.append(className);
        sb.append("/");
        sb.append(methodName);
        sb.append("?appid=");
        sb.append(appid);
        sb.append("&timestamp=");
        sb.append(timestamp);
        sb.append("&seqid=");
        sb.append(seqid);
        sb.append("&sig=");
        sb.append(sig);
        return sb.toString();
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSeqid() {
        return seqid;
    }

    public void setSeqid(String seqid) {
        this.seqid = seqid;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }



    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
