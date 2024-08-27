/**
 * Copyright 2014-2020  the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.wsas.front.utils;

public class CommonUtils {
    
    public static int getLocalAppIdBySignUserId(String signUserId) {
        int localAppId = 0;
        // 1.6.2 修复signUserId中存在单个S的情况导致误判
        int firstIndex = signUserId.indexOf("SSS") + 3;
        int finalIndex = signUserId.lastIndexOf("SSS");
        if (finalIndex > firstIndex) {
            String subStr = signUserId.substring(firstIndex, finalIndex);
            if (isDigit(subStr)) {
                localAppId = Integer.valueOf(subStr);
            }
        }
        return localAppId;
    }
    
    public static boolean isDigit(String strNum) {
        if (strNum != null && !strNum.equals("")) {
            for (int i = 0; i < strNum.length(); i++) {
                if (!(strNum.charAt(i) >= 48) || !(strNum.charAt(i) <= 57)) {
                    return false;
                }
            }
        }
        return true;
    }
}
