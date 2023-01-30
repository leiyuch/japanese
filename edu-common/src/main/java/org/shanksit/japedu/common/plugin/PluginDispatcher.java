package org.shanksit.japedu.common.plugin;

import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.common.plugin.annotation.PluginHandler;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 扩展规范
 *
 * @author yunliang.li
 * @create 2019 12/17 16:38:51
 */
@Component
@Slf4j
public class PluginDispatcher implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<String, LinkedHashMap<String, Object>> _MAPPING = new LinkedHashMap<String, LinkedHashMap<String, Object>>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        this._get_beans_with_annotation(PluginHandler.class);
    }

    public <A extends Annotation, P> P target(String code, Class<A> aclazz, Class<P> pclazz) {
        this._get_beans_with_annotation(aclazz);
        return (P) _MAPPING.get(aclazz.getName()).get(code);
    }

    public <P> Map<String, P> plugin(Class<P> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public <A extends Annotation, P> Map<String, P> plugin(Class<A> aclazz, Class<P> pclazz) {
        this._get_beans_with_annotation(aclazz);
        Map<String, P> _reval = new LinkedHashMap<String, P>();
        LinkedHashMap<String, Object> _items = _MAPPING.get(aclazz.getName());
        for (String _key : _items.keySet()) {
            if (pclazz.isAssignableFrom(_items.get(_key).getClass())) {
                _reval.put(_key, (P) _items.get(_key));
            }
        }
        return _reval;
    }

    private <A extends Annotation> void _get_beans_with_annotation(Class<A> clazz) {
        try {
            if (_MAPPING.containsKey(clazz.getName())) {
                return;
            }
            _MAPPING.put(clazz.getName(), new LinkedHashMap<String, Object>());
            Map<String, Object> _bean = applicationContext.getBeansWithAnnotation(clazz);
            A[] _annotation = null;
            Class<?> _target = null;
            Method _method = null;
            for (Object _o : _bean.values()) {
                _target = AopProxyUtils.ultimateTargetClass(_o);
                _annotation = _target.getAnnotationsByType(clazz);
                if (null != _annotation) {
                    for (A _ann : _annotation) {
                        _method = _ann.getClass().getDeclaredMethod("code");
                        _method.setAccessible(true);
                        _MAPPING.get(clazz.getName()).put(String.valueOf(_method.invoke(_ann)), _o);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Plugin.dispatcher execute Exception!", e);
        }
    }

}
