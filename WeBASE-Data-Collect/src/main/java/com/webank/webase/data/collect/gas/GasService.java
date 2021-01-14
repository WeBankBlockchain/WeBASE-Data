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
package com.webank.webase.data.collect.gas;

import com.webank.webase.data.collect.base.enums.GasRecordType;
import com.webank.webase.data.collect.base.enums.PrecompiledAddress;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.base.tools.TransactionDecoder;
import com.webank.webase.data.collect.chain.ChainService;
import com.webank.webase.data.collect.chain.entity.TbChain;
import com.webank.webase.data.collect.contract.MethodService;
import com.webank.webase.data.collect.contract.entity.MethodInfo;
import com.webank.webase.data.collect.dao.entity.TbGas;
import com.webank.webase.data.collect.dao.entity.TbGasExample;
import com.webank.webase.data.collect.dao.mapper.TbGasMapper;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Numeric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * service of gas.
 */
@Log4j2
@Service
public class GasService {

    @Autowired
    private TbGasMapper tbGasMapper;
    @Autowired
    private MethodService methodService;
    @Autowired
    private ChainService chainService;

    /**
     * parser gas
     * 
     * @throws TransactionException
     * @throws BaseException
     */
    public void parserAndSaveGas(int chainId, int groupId, TransactionReceipt transReceipt,
            LocalDateTime blockTimestamp) throws BaseException, TransactionException {
        BigInteger gasUsed = transReceipt.getGasUsed();
        String contractAddress = transReceipt.getTo();
        // check gasUsed and status
        if ((gasUsed.compareTo(BigInteger.ZERO) == 0
                && !contractAddress.equals(PrecompiledAddress.GAS_CHARGE.getAddress()))
                || (contractAddress.equals(PrecompiledAddress.GAS_CHARGE.getAddress())
                        && !transReceipt.isStatusOK())) {
            return;
        }
        // set common parameter
        TbGas tbGas = new TbGas().setChainId(chainId).setGroupId(groupId)
                .setBlockNumber(transReceipt.getBlockNumber())
                .setTransHash(transReceipt.getTransactionHash())
                .setTransIndex(transReceipt.getTransactionIndex().intValue())
                .setBlockTimestamp(blockTimestamp)
                .setRecordMonth(CommonTools.getYearMonth(blockTimestamp));
        String userAddress = transReceipt.getFrom();
        BigInteger gasValue = gasUsed.negate();
        BigInteger gasRemain = Numeric.decodeQuantity(transReceipt.getRemainGas());
        int gasRecordType = GasRecordType.consume.getType();
        if (contractAddress.equals(PrecompiledAddress.GAS_CHARGE.getAddress())) {
            String methodId = transReceipt.getInput().substring(0, 10);
            List<MethodInfo> methodInfoList =
                    methodService.getByMethodInfo(0, 0, methodId, contractAddress, null);
            if (CollectionUtils.isEmpty(methodInfoList)) {
                log.error("parserAndSaveGas GasChargeManagePrecompiled not been configed.");
                return;
            }
            TbChain tbChain = chainService.getChainById(chainId);
            if (ObjectUtils.isEmpty(tbChain)) {
                return;
            }
            TransactionDecoder transactionDecoder = new TransactionDecoder(
                    methodInfoList.get(0).getContractAbi(), tbChain.getEncryptType());
            InputAndOutputResult inputResult =
                    transactionDecoder.decodeInputReturnObject(transReceipt.getInput());
            InputAndOutputResult outputResult = transactionDecoder
                    .decodeOutputReturnObject(transReceipt.getInput(), transReceipt.getOutput());
            int resultCode = Integer.valueOf(outputResult.getResult().get(0).getData().toString());
            if (resultCode != 0) {
                return;
            }
            if (ConstantProperties.GAS_CHARGE.equals(inputResult.getFunction())) {
                userAddress = inputResult.getResult().get(0).getData().toString();
                gasValue = new BigInteger(inputResult.getResult().get(1).getData().toString());
                gasRemain = new BigInteger(outputResult.getResult().get(1).getData().toString());
                gasRecordType = GasRecordType.charge.getType();
            } else if (ConstantProperties.GAS_DEDUCT.equals(inputResult.getFunction())) {
                userAddress = inputResult.getResult().get(0).getData().toString();
                gasValue = new BigInteger(inputResult.getResult().get(1).getData().toString())
                        .negate();
                gasRemain = new BigInteger(outputResult.getResult().get(1).getData().toString());
                gasRecordType = GasRecordType.deduct.getType();
            } else {
                return;
            }
        }
        tbGas.setUserAddress(userAddress).setGasValue(gasValue).setGasRemain(gasRemain)
                .setRecordType((byte) gasRecordType);
        saveGas(tbGas);
    }

    /**
     * save gas
     */
    public void saveGas(TbGas tbGas) {
        tbGasMapper.insert(tbGas);
    }

    /**
     * get gas count
     */
    public int getGasCount(TbGasExample example) {
        Integer count = (int) tbGasMapper.countByExample(example);
        return count == null ? 0 : count;
    }

    /**
     * get gas list
     */
    public List<TbGas> getGasList(TbGasExample example) {
        return tbGasMapper.selectByExample(example);
    }

    /**
     * delete by chainId
     */
    public int deleteByChainId(int chainId) {
        return tbGasMapper.deleteByChainId(chainId);
    }
}
