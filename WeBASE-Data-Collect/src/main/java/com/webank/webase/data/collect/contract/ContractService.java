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
package com.webank.webase.data.collect.contract;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.contract.entity.Contract;
import com.webank.webase.data.collect.contract.entity.ContractParam;
import com.webank.webase.data.collect.contract.entity.TbContract;
import com.webank.webase.data.collect.monitor.MonitorService;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * services for contract data.
 */
@Log4j2
@Service
public class ContractService {

    @Autowired
    private ContractMapper contractMapper;
    @Autowired
    @Lazy
    private MonitorService monitorService;

    /**
     * add new contract data.
     */
    public TbContract saveContract(Contract contract) throws BaseException {
        TbContract tbContract;
        if (contract.getContractId() == null) {
            tbContract = newContract(contract);// new
        } else {
            tbContract = updateContract(contract);// update
        }

        if (Objects.nonNull(tbContract) && StringUtils.isNotBlank(tbContract.getContractBin())) {
            // update monitor unusual deployInputParam's info
            monitorService.updateUnusualContract(tbContract.getChainId(), tbContract.getGroupId(),
                    tbContract.getContractName(), tbContract.getContractBin());
        }
        return tbContract;
    }


    /**
     * save new contract.
     */
    private TbContract newContract(Contract contract) {
        // check contract not exist.
        verifyContractNotExist(contract.getChainId(), contract.getGroupId(),
                contract.getContractName(), contract.getContractPath());

        // add to database.
        TbContract tbContract = new TbContract();
        BeanUtils.copyProperties(contract, tbContract);
        contractMapper.add(tbContract);
        return queryByContractId(tbContract.getContractId());
    }


    /**
     * update contract.
     */
    private TbContract updateContract(Contract contract) {
        // check id
        TbContract tbContract = verifyContractIdExist(contract.getChainId(),
                contract.getContractId(), contract.getGroupId());
        // check contractName
        verifyContractNameNotExist(contract.getChainId(), contract.getGroupId(),
                contract.getContractPath(), contract.getContractName(), contract.getContractId());
        BeanUtils.copyProperties(contract, tbContract);
        contractMapper.update(tbContract);
        return queryByContractId(tbContract.getContractId());
    }

    /**
     * delete contract by contractId.
     */
    public void deleteContract(Integer contractId, int groupId) throws BaseException {
        contractMapper.remove(contractId);
    }

    /**
     * query contract list.
     */
    public List<TbContract> qureyContractList(ContractParam param) throws BaseException {
        List<TbContract> listOfContract = contractMapper.listOfContract(param);
        return listOfContract;
    }

    /**
     * query count of contract.
     */
    public int countOfContract(ContractParam param) throws BaseException {
        try {
            return contractMapper.countOfContract(param);
        } catch (RuntimeException ex) {
            log.error("fail countOfContract", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query contract by contract id.
     */
    public TbContract queryByContractId(Integer contractId) throws BaseException {
        try {
            TbContract contractRow = contractMapper.queryByContractId(contractId);
            return contractRow;
        } catch (RuntimeException ex) {
            log.error("fail countOfContract", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }

    }

    /**
     * query DeployInputParam By Address.
     */
    public List<TbContract> queryContractByBin(Integer groupId, String contractBin)
            throws BaseException {
        try {
            if (StringUtils.isEmpty(contractBin)) {
                return null;
            }
            List<TbContract> contractRow = contractMapper.queryContractByBin(groupId, contractBin);
            return contractRow;
        } catch (RuntimeException ex) {
            log.error("fail queryContractByBin", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query contract info.
     */
    public TbContract queryContract(ContractParam queryParam) {
        TbContract tbContract = contractMapper.queryContract(queryParam);
        return tbContract;
    }

    /**
     * verify that the contract does not exist.
     */
    private void verifyContractNotExist(int chainId, int groupId, String name, String path) {
        ContractParam param = new ContractParam(chainId, groupId, path, name);
        TbContract contract = queryContract(param);
        if (Objects.nonNull(contract)) {
            log.warn("contract is exist. groupId:{} name:{} path:{}", groupId, name, path);
            throw new BaseException(ConstantCode.CONTRACT_EXISTS);
        }
    }

    /**
     * verify that the contractId is exist.
     */
    private TbContract verifyContractIdExist(int chainId, int contractId, int groupId) {
        ContractParam param = new ContractParam(chainId, contractId, groupId);
        TbContract contract = queryContract(param);
        if (Objects.isNull(contract)) {
            log.info("contractId is invalid. contractId:{}", contractId);
            throw new BaseException(ConstantCode.INVALID_CONTRACT_ID);
        }
        return contract;
    }

    /**
     * contract name can not be repeated.
     */
    private void verifyContractNameNotExist(int chainId, int groupId, String path, String name,
            int contractId) {
        ContractParam param = new ContractParam(chainId, groupId, path, name);
        TbContract localContract = queryContract(param);
        if (Objects.isNull(localContract)) {
            return;
        }
        if (contractId != localContract.getContractId()) {
            throw new BaseException(ConstantCode.CONTRACT_NAME_REPEAT);
        }
    }

    /**
     * delete by groupId
     */
    public void deleteByGroupId(int groupId) {
        if (groupId == 0) {
            return;
        }
        contractMapper.removeByGroupId(groupId);
    }

}
