/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.webank.webase.data.collect.base.enums;

/**
 * Enumeration of precompiled address.
 */
public enum PrecompiledAddress {
    SYSTEM_CONFIG("0x0000000000000000000000000000000000001000"), TABLE(
            "0x0000000000000000000000000000000000001001"), CRUD(
                    "0x0000000000000000000000000000000000001002"), CONSENSUS(
                            "0x0000000000000000000000000000000000001003"), CNS(
                                    "0x0000000000000000000000000000000000001004"), PERMISSION(
                                            "0x0000000000000000000000000000000000001005"), CSM(
                                                    "0x0000000000000000000000000000000000001007");

    private String address;

    private PrecompiledAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public static boolean isInclude(String address) {
        boolean include = false;
        for (PrecompiledAddress e : PrecompiledAddress.values()) {
            if (e.getAddress().equals(address)) {
                include = true;
                break;
            }
        }
        return include;
    }
}
