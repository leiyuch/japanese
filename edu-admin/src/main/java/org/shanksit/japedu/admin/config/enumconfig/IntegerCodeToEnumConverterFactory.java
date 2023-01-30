package org.shanksit.japedu.admin.config.enumconfig;

import com.google.common.collect.Maps;
import org.shanksit.japedu.admin.enums.BasicEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Map;

/**
 * @author: chenb
 * @date: 2022/3/23 20:16
 */
public class IntegerCodeToEnumConverterFactory implements ConverterFactory<Integer, BasicEnum> {
    private static final Map<Class, Converter> CONVERTERS = Maps.newHashMap();

    /**
     * 获取一个从 Integer 转化为 T 的转换器，T 是一个泛型，有多个实现
     *
     * @param targetType 转换后的类型
     * @return 返回一个转化器
     */
    @Override
    public <T extends BasicEnum> Converter<Integer, T> getConverter(Class<T> targetType) {
        Converter<Integer, T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new IntegerToEnumConverter<>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }
}
