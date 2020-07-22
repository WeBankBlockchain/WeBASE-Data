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
package com.webank.webase.data.collect.parser;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.parser.entity.ResetInfo;
import java.time.Duration;
import java.time.Instant;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("parser")
public class ParserController extends BaseController {

    @Autowired
    private ParserService parserService;

    /**
     * parser reset.
     */
    @PostMapping(value = "/reset")
    public BaseResponse reset(@RequestBody @Valid ResetInfo resetInfo, BindingResult result) {
        checkBindResult(result);
        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start reset. blockNumber:{} ", resetInfo.getBlockNumber());
        parserService.reset(resetInfo);
        log.info("end reset. useTime:{}", Duration.between(startTime, Instant.now()).toMillis());
        return response;
    }
}
