package org.shanksit.japedu.admin.enums;

/**
 * 枚举接口
 * @author ChenBao
 */
public interface BasicEnum<K,V> {

    /**
     * 值
     * @return
     */
    K value();

    /**
     * 描述
     * @return
     */
    V text();

}
