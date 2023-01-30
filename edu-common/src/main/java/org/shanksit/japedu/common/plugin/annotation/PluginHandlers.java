package org.shanksit.japedu.common.plugin.annotation;

import java.lang.annotation.*;


@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginHandlers {

    PluginHandler[] value();

}
