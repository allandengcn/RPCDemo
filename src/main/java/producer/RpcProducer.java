package producer;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC annotation for RPC service
 *
 * @author huangyong
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@Component
public @interface RpcProducer {
    Class<?> value();
}
