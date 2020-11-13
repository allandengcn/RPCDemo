package registry;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Service;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 服务注册
 */
public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;
    private String serverAddress;
    private String serverName;
    private String nodeName;
    private String nodePath;

    private ZooKeeper zk;



    public ServiceRegistry(String registryAddress, String serverAddress, String serverName , String nodeName) {
        this.registryAddress = registryAddress;
        this.serverName = serverName;
        this.nodeName = nodeName;
        this.serverAddress = serverAddress;
        register(serverAddress,serverName,nodeName);
    }


    public void register(String data , String serviceName,String nodeName) {
        // nodeName在创建node节点后产生
        this.nodePath = Constant.ZK_REGISTRY_PATH + "/"+serviceName + "/" + nodeName;
        if (data != null) {
            this.zk = connectServer();
            if (zk != null) {
                AddRootNode(zk);
                AddServiceNode(zk,serviceName);
                AddProducerNode(zk, data,serviceName,nodeName);
            }
        }
    }


    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        // 待连接成功后，通知阻塞的程序继续执行
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException e) {
            logger.error("", e);
        }
        catch (InterruptedException ex){
            logger.error("", ex);
        }
        return zk;
    }
    
    
    /**
     * 创建根节点（持久类型）
     * @Param [zk]
     * @return void
     **/
    private void AddRootNode(ZooKeeper zk){
        try {
            Stat s = zk.exists(Constant.ZK_REGISTRY_PATH, false);
            if (s == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            logger.error(e.toString());
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
    }

    /**
     * 创建服务类型节点（持久类型）
     * @Param [zk, serviceName]
     * @return void
     **/
    private void AddServiceNode(ZooKeeper zk, String serviceName) {
        String path = Constant.ZK_REGISTRY_PATH + "/"+serviceName;
        try {
            Stat s = zk.exists(path+"/" + serviceName, false);
            if (s == null) {
                zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("zookeeper , service node created : {}",path);
            }else {
                logger.info("zookeeper , service node is exist : {}",path);
            }
        } catch (KeeperException e) {
            logger.error(e.toString());
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
    }

    /**
     *  创建方法节点（临时）
     * @Param [zk, data, serviceName]
     * @return void
     **/
    public void createMethodData(String methodName ,String data) {
        String newStr = "|" + methodName + ":" + data;
        try {
            byte[] bytes = zk.getData(this.nodePath, true, null);
            String oldStr = new String(bytes);
            newStr = oldStr+ newStr;
            zk.setData(this.nodePath, newStr.getBytes(), -1);
            logger.info("change zookeeper node data ({} => {})", oldStr, newStr);
        } catch (KeeperException e) {
            logger.error("", e);
        }
        catch (InterruptedException ex){
            logger.error("", ex);
        }
    }
    public void createMethodData(String methodName){
        createMethodData(methodName , "");
    }

    /**
     *  创建提供者节点（临时排序）
     * @Param [zk, data, serviceName , nodeName ]
     * @return void
     **/
    private void AddProducerNode(ZooKeeper zk, String data, String serviceName , String nodeName) {
        String zkpath = Constant.ZK_REGISTRY_PATH + "/"+serviceName+"/"+nodeName;
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(zkpath, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//            String[] pathArr = path.split("/");
//            String curNodeName = pathArr[pathArr.length -1];
//            this.nodePath = this.nodePath + "/" + curNodeName;
            logger.info("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException e) {
            logger.error("", e);
        }
        catch (InterruptedException ex){
            logger.error("", ex);
        }
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }
}