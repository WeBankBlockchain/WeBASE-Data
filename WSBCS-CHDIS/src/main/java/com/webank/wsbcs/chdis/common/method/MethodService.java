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
package com.webank.wsbcs.chdis.common.method;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition.NamedType;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.CommonUtils;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.base.util.Web3Tools;
import com.webank.wsbcs.chdis.base.util.logentity.LogEntity;
import com.webank.wsbcs.chdis.base.util.logentity.LogUtils;
import com.webank.wsbcs.chdis.base.util.logentity.MonitorLog;
import com.webank.wsbcs.chdis.common.dao.entity.TbMethod;
import com.webank.wsbcs.chdis.common.dao.entity.TbMethodExample;
import com.webank.wsbcs.chdis.common.dao.mapper.TbMethodMapper;
import com.webank.wsbcs.chdis.v3.web3api.Web3ApiServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * Method is used in transaction data's analysis Store contract's method in db
 */
@Log4j2
@Service
public class MethodService {

    @Autowired
    private Web3ApiServiceV3 web3ApiServiceV3;
    @Autowired
    private TbMethodMapper tbMethodMapper;
    @Autowired
    ConstantProperties constantProperties;

    private static CryptoSuite cryptoSuite = new CryptoSuite(0);
    public static final String DATA_TBMETHOD = "./data/data_tbMethod";

    public void initMethod() {
        long dbSize = tbMethodMapper.countByExample(new TbMethodExample());
        List<String> records = CommonUtils.readFileToList(DATA_TBMETHOD);
        log.info("init method data dbSize:{} recordsSize: {}", dbSize,
                records == null ? 0 : records.size());
        Date date = new Date();
        if (records != null && dbSize == 0) {
            for (String record : records) {
                TbMethod entity = new TbMethod();
                String[] fileds = record.split("#");
                if (fileds == null || fileds.length != 4) {
                    continue;
                }
                entity.setMethodId(fileds[0]);
                entity.setName(fileds[1]);
                entity.setMethodType(fileds[2]);
                entity.setAbiInfo(fileds[3]);
                entity.setAbiHash(getHash(fileds[3]));
                entity.setCreateTime(date);
                entity.setModifyTime(date);
                tbMethodMapper.insert(entity);
            }
        }
    }

    /**
     * save method info.
     */
    public void saveMethodFromAbiV3(String chainId, String groupId, String contractAbi,
            String bizSeqNo) {
        CryptoSuite cryptoSuiteV3 = web3ApiServiceV3.getCryptoSuite(groupId, chainId);
        List<Object> abiList = JsonUtils.toList(contractAbi);
        List<TbMethod> recordList = new ArrayList<>();
        Instant startTime = Instant.now();
        Date date = new Date();
        for (Object abi : abiList) {
            ABIDefinition abiDefinition = JsonUtils.toJavaObject(abi, ABIDefinition.class);
            if (("function".equals(abiDefinition.getType())
                    || "event".equals(abiDefinition.getType())) && !abiDefinition.isConstant()) {
                String methodId = Web3Tools.buildMethodIdV3(abiDefinition, cryptoSuiteV3);
                List<NamedType> inputs = abiDefinition.getInputs();
                int indexCnt = 0;
                for (NamedType i : inputs) {
                    if (i.isIndexed()) {
                        indexCnt = indexCnt + 1;
                    }
                }
                TbMethod entity = new TbMethod();
                entity.setMethodId(methodId);
                entity.setAbiHash(getHash(JsonUtils.toJSONString(abi)));
                entity.setName(abiDefinition.getName());
                entity.setMethodType(abiDefinition.getType());
                entity.setAbiInfo(JsonUtils.toJSONString(abi));
                entity.setIndexCnt(indexCnt);
                entity.setCreateTime(date);
                entity.setModifyTime(date);
                recordList.add(entity);
            }
        }
        if (recordList.size() > 0) {
            try {
                tbMethodMapper.batchInsert(recordList);
            } catch (Exception e) {
                log.error("{}",
                        MonitorLog.monitorStr("OnError", "[DB]saveMethod", "", "",
                                String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()),
                                Duration.between(startTime, Instant.now()).toMillis(), null),
                        e);
                LogUtils.getMonitorLogger().error(LogEntity.monitorStr("saveMethod", "",
                        String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()), e.getMessage(),
                        bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
                throw new BaseException(ConstantCode.DATABASE_EXCEPTION);
            }
        }
    }

    public List<TbMethod> getMethodIdList(String methodId, Integer topicCnt, String bizSeqNo) {
        log.debug("getMethodIdList methodId:{} bizSeqNo:{}", methodId, bizSeqNo);
        TbMethodExample example = new TbMethodExample();
        example.createCriteria();
        if (StringUtils.isNotBlank(methodId)) {
            example.getOredCriteria().get(0).andMethodIdEqualTo(methodId);
        }
        if (topicCnt != null) {
            example.getOredCriteria().get(0).andIndexCntEqualTo(topicCnt - 1);
        }
        return tbMethodMapper.selectByExampleWithBLOBs(example);
    }

    /**
     * query by methodId.
     */
    public TbMethod getByMethodId(String methodId) {
        TbMethodExample example = new TbMethodExample();
        example.createCriteria().andMethodIdEqualTo(methodId);
        Optional<TbMethod> result = tbMethodMapper.getOneByExample(example);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public String getHash(String abi) {
        return Hex.toHexStringWithPrefix(cryptoSuite.hash(abi.getBytes()));
    }
}
