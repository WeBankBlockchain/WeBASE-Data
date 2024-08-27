package com.webank.wsbcs.chdis.base.enums;

public enum SendTypeEnum {
    IN(0),
    OUT(1);

    private int value;

    private SendTypeEnum(int status) {
        this.value = status;
    }

    public int getValue() {
        return this.value;
    }

    public static boolean isInclude(int key) {
        boolean include = false;
        for (SendTypeEnum e : SendTypeEnum.values()) {
            if (e.getValue() == key) {
                include = true;
                break;
            }
        }
        return include;
    }
}
