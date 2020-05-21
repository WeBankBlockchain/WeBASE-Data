package com.webank.webase.data.collect.base.enums;

public enum EnableStatus {
    ON(1), OFF(0);

    private int value;

    private EnableStatus(Integer onOrOff) {
        this.value = onOrOff;
    }

    public int getValue() {
        return this.value;
    }
}
