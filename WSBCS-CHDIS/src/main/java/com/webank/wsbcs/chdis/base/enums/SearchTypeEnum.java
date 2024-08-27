package com.webank.wsbcs.chdis.base.enums;

public enum SearchTypeEnum {
    NOW(0),
    BLOCK(1),
    TIME(2);

    private int value;

    private SearchTypeEnum(int status) {
        this.value = status;
    }

    public int getValue() {
        return this.value;
    }

    public static boolean isInclude(int key) {
        boolean include = false;
        for (SearchTypeEnum e : SearchTypeEnum.values()) {
            if (e.getValue() == key) {
                include = true;
                break;
            }
        }
        return include;
    }
    
    public static boolean isBlockOrTime(int key) {
        if (BLOCK.getValue() == key || TIME.getValue() == key) {
            return true;
        }
        return false;
    }
}
