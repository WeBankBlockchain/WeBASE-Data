package com.webank.wsbcs.chdis.base.enums;

public enum TrendTypeEnum {
    USER(0),
    ISSUE(1),
    USED(2);

    private int value;

    private TrendTypeEnum(int status) {
        this.value = status;
    }

    public int getValue() {
        return this.value;
    }

    public static boolean isInclude(int key) {
        boolean include = false;
        for (TrendTypeEnum e : TrendTypeEnum.values()) {
            if (e.getValue() == key) {
                include = true;
                break;
            }
        }
        return include;
    }
}
