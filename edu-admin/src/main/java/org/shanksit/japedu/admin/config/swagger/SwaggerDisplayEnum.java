package org.shanksit.japedu.admin.config.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: chenb
 * @date: 2022/3/23 10:16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerDisplayEnum {
    String index() default "value";

    String name() default "text";

}
