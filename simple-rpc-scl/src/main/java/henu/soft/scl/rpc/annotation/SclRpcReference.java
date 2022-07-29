package henu.soft.scl.rpc.annotation;

import java.lang.annotation.*;

/**
 * RPC reference annotation, autowire the service implementation class
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface SclRpcReference {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

}
