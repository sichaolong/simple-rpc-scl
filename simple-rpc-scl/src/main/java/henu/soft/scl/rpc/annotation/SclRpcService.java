package henu.soft.scl.rpc.annotation;

import java.lang.annotation.*;



/**
 * RPC service annotation, marked on the service implementation class
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface SclRpcService {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

}
