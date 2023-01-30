package org.shanksit.japedu.admin.enums;


import lombok.Data;

import java.io.Serializable;

@Data
public class EnumValue<T> implements Serializable {

    private static final long serialVersionUID = -8951161168686020469L;

    private T value;
    private String text;

    public EnumValue(T value, String text) {
        this.value = value;
        this.text = text;
    }

    public static <S> EnumValue<S> build(S value, String text) {
        return new EnumValue<>(value, text);
    }
}
