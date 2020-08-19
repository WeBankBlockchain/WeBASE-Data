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
package com.webank.webase.data.collect.chain;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.chain.entity.ChainInfo;
import com.webank.webase.data.collect.chain.entity.TbChain;
import com.webank.webase.data.collect.chain.entity.ToggleInfo;
import com.webank.webase.data.collect.chain.entity.UpdateChainInfo;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * chain controller
 */
@Log4j2
@RestController
@RequestMapping("chain")
public class ChainController extends BaseController {

    @Autowired
    private ChainService chainService;
    @Autowired
    private ConstantProperties cProperties;

    /**
     * add new chain
     */
    @PostMapping("/new")
    public BaseResponse newChain(@RequestBody @Valid ChainInfo chainInfo, BindingResult result) {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start newChain.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbChain tbChain = chainService.newChain(chainInfo);
        baseResponse.setData(tbChain);
        log.info("end newChain useTime:{}", Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * update chain info
     */
    @PostMapping("/update")
    public BaseResponse updateChain(@RequestBody @Valid UpdateChainInfo chainInfo,
            BindingResult result) {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start updateChain.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbChain tbChain = chainService.updateChain(chainInfo);
        baseResponse.setData(tbChain);
        log.info("end updateChain useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * query chain info list.
     */
    @GetMapping("/all")
    public BasePageResponse queryChainList() {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryChainList.");

        // query chain info
        int count = chainService.getChainCount(null);
        pagesponse.setTotalCount(count);
        if (count > 0) {
            List<TbChain> list = chainService.getChainList(null);
            pagesponse.setData(list);
        }

        log.info("end queryChainList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * delete by chainId
     */
    @DeleteMapping("/{chainId}")
    public BaseResponse removeChain(@PathVariable("chainId") Integer chainId) {
        Instant startTime = Instant.now();
        log.info("start removeChain. chainId:{}", chainId);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);

        // remove
        chainService.removeChain(chainId);

        log.info("end removeChain useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
    
    /**
     * toggle of pull data
     */
    @GetMapping("/togglePullData")
    public BaseResponse getTogglePullData() {
        Instant startTime = Instant.now();
        log.info("start getTogglePullData.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);

        // remove
        baseResponse.setData(cProperties.isIfPullData());

        log.info("end getTogglePullData useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
    
    /**
     * update toggle of pull data
     */
    @PostMapping("/togglePullData")
    public BaseResponse updateTogglePullData(@RequestBody ToggleInfo toggleInfo) {
        Instant startTime = Instant.now();
        log.info("start updateTogglePullData.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        
        // update
        cProperties.setIfPullData(toggleInfo.isEnable());
        
        log.info("end updateTogglePullData useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
}
