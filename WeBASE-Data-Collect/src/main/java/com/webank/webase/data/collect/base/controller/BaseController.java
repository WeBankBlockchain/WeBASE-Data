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
package com.webank.webase.data.collect.base.controller;

import com.google.common.collect.Maps;
import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.exception.ParamException;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import java.util.Map;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class BaseController {
    /**
     * check param valid result.
     */
    protected void checkBindResult(BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> fields = Maps.newHashMap();
            result.getAllErrors().forEach(e -> {
                if (e instanceof FieldError) {
                    FieldError fe = (FieldError) e;
                    fields.put(fe.getField(), fe.getDefaultMessage());
                }
            });
            String message = "these fields error:" + JacksonUtils.objToString(fields);
            throw new ParamException(ConstantCode.PARAM_EXCEPTION.getCode(), message);
        }
    }
}
