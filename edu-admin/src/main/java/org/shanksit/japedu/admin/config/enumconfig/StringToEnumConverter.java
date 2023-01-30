package org.shanksit.japedu.admin.config.enumconfig;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import org.shanksit.japedu.admin.enums.BasicEnum;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @author: chenb
 * @date: 2022/3/23 20:07
 */
public class StringToEnumConverter<T extends BasicEnum> implements Converter<String, T> {

    private Map<String, T> enumMap = Maps.newHashMap();

    public StringToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(String.valueOf(e.value()), e);
        }
    }

    @Override
    public T convert(String source) {
        T t = enumMap.get(source);
        if (ObjectUtil.isNull(t)) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型");
        }
        return t;
    }
}
