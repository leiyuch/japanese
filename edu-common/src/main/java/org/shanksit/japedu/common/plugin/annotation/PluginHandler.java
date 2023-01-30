package org.shanksit.japedu.common.plugin.annotation;

import java.lang.annotation.*;


@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PluginHandlers.class)
public @interface PluginHandler {

    String code() default "";

    String desc() default "";

}
