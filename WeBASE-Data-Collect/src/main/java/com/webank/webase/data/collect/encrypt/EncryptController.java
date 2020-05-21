/**
 * Copyright 2014-2020 the original author or authors.
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

package com.webank.webase.data.collect.encrypt;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.web3j.crypto.EncryptType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * return encrypt type to web 0 is standard, 1 is guomi
 */
@Log4j2
@RestController
@RequestMapping(value = "encrypt")
public class EncryptController {
    @GetMapping("")
    public Object getEncryptType() {
        int encrypt = EncryptType.encryptType;
        log.info("getEncryptType:{}", encrypt);
        return new BaseResponse(ConstantCode.SUCCESS, encrypt);
    }
}
