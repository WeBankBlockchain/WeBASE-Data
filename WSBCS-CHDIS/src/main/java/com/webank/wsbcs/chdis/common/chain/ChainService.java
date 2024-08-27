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
package com.webank.wsbcs.chdis.common.chain;

import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.entity.BaseResponse;
import com.webank.wsbcs.chdis.base.entity.chain.ChainGeneral;
import com.webank.wsbcs.chdis.base.entity.web3.RspTransCountInfo;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.common.dao.entity.TbContractInfo;
import com.webank.wsbcs.chdis.common.dao.entity.TbContractInfoExample;
import com.webank.wsbcs.chdis.common.dao.mapper.TbContractInfoMapper;
import com.webank.wsbcs.chdis.v3.web3api.Web3ApiServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * ChainService.
 */
@Log4j2
@Service
public class ChainService {

    @Autowired
    private Web3ApiServiceV3 web3ApiServiceV3;
    @Autowired
    private TbContractInfoMapper tbContractInfoMapper;

    final int loadDifference = 10;

    public void saveTbContractInfo(String chainId, String contractAddress, String contractAbi) {
        Date date = new Date();
        TbContractInfo entity = new TbContractInfo();
        entity.setChainId(chainId);
        entity.setContractAddress(contractAddress);
        entity.setContractAbi(contractAbi);
        entity.setCreateTime(date);
        entity.setModifyTime(date);
        tbContractInfoMapper.insert(entity);
    }

    public TbContractInfo findByChainIdAndContractAddress(String chainId, String contractAddress) {
        log.debug("chainId:{} contractAddress:{}", chainId, contractAddress);
        TbContractInfoExample example = new TbContractInfoExample();
        example.createCriteria().andContractAddressEqualTo(contractAddress);
        Optional<TbContractInfo> result = tbContractInfoMapper.getOneByExample(example);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public BaseResponse general(String chainId, String bizSeqNo) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        RspTransCountInfo transCountInfo = web3ApiServiceV3
                .getTransCnt(chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId);
        ChainGeneral info = new ChainGeneral();
        if (transCountInfo != null) {
            info.setTxs(transCountInfo.getTxSum());
            info.setBlocks(transCountInfo.getBlockNumber());
        }
        baseResponse.setData(info);
        return baseResponse;
    }

}
