/**
 * Copyright 2014-2023 the original author or authors.
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
package com.webank.wsbcs.chdis.base.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.util.JsonUtils;

@Log4j2
@RestController
public class ErrorFormatApi extends BasicErrorController {

    public ErrorFormatApi() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body =
                getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        log.warn("error controller get {}", JsonUtils.toJSONString(body));
        String mesage = null;// NOSONAR
        if (body.get("message") != null ) {
            mesage = body.get("message").toString();
        }
        if (StringUtils.isBlank(mesage)) {
            throw new BaseException(ConstantCode.SYSTEM_EXCEPTION);
        }
        int index = mesage.indexOf("_");// NOSONAR
        if (index > 0) {
            String code = mesage.substring(0, index);
            String msg = mesage.substring(index);
            throw new BaseException(Integer.valueOf(code), msg);
        } else {
            throw new BaseException(ConstantCode.SYSTEM_EXCEPTION.getCode(), mesage);
        }
    }

}
