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
public class IntegerToEnumConverter<T extends BasicEnum> implements Converter<Integer, T> {

    private Map<Integer, T> enumMap = Maps.newHashMap();

    public IntegerToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(Integer.valueOf(String.valueOf(e.value())), e);
        }
    }

    @Override
    public T convert(Integer source) {
        T t = enumMap.get(source);
        if (ObjectUtil.isNull(t)) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型");
        }
        return t;
    }
}
